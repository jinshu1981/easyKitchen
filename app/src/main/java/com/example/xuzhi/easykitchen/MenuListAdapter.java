package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

import java.util.HashMap;

/**
 * Created by xuzhi on 2016/2/10.
 */
public class MenuListAdapter extends CursorTreeAdapter{
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;
    protected final HashMap<Integer, Integer> mGroupMap;
    private MyMenuListsActivityFragment mFragment;
    private LayoutInflater mInflater;

    public MenuListAdapter(Cursor c, Context context, boolean flags,MyMenuListsActivityFragment fragment) {
        super(c,context,flags);
        mContext = context;
        mFragment = fragment;
        mInflater = LayoutInflater.from(context);
        mGroupMap = new HashMap<Integer, Integer>();/*groupid,grouppos*/
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
        Log.v(LOG_TAG,"newGroupView");
        return view;
    }
    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.menu_list_item, parent, false);

        return view;
    }

    @Override
    protected void bindGroupView (View view, Context context, Cursor cursor, boolean isLastChild)
    {
        TextView text = (TextView)view.findViewById(android.R.id.text1);
        int textIndex = cursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);
        Utility.setBoldTextStyle(text);

        TextView text1 = (TextView)view.findViewById(android.R.id.text2);
        int textIndex1 = cursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_MENU_TIME);
        String textString1 = cursor.getString(textIndex1);
        text1.setText(textString1);
        Log.v(LOG_TAG,"bindGroupView");

    }
    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor)
    {
        // Given the group, we return a cursor for all the children within that
        // group
        int groupPos = groupCursor.getPosition();
        int groupId = groupCursor.getInt(groupCursor
                .getColumnIndex(EasyKitchenContract.UsersMenuList._ID));

       Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
       Log.d(LOG_TAG, "getChildrenCursor() for groupId " + groupId);

        mGroupMap.put(groupId, groupPos);

        Loader loader = mFragment.getLoaderManager().getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            mFragment.getLoaderManager().restartLoader(groupId, null,mFragment);
        } else {
            mFragment.getLoaderManager().initLoader(groupId, null,mFragment);
        }
        return null;
    }
    @Override
    protected void bindChildView (View view, Context context, Cursor cursor, boolean isLastChild)
    {
        ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView hideInfoView = (TextView)view.findViewById(R.id.hideInfo);
        TextView text = (TextView)view.findViewById(R.id.name);
        TextView textMaterial = (TextView)view.findViewById(R.id.material);
        TextView author = (TextView)view.findViewById(R.id.author);
        TextView time = (TextView)view.findViewById(R.id.time);
        TextView difficulty = (TextView)view.findViewById(R.id.difficulty);
        TextView popularity = (TextView)view.findViewById(R.id.popularity);
        ImageView imageCook = (ImageView)view.findViewById(R.id.image_cookIt);
        ImageView imageLike = (ImageView)view.findViewById(R.id.image_like);
        String hideInfo = "";
        int timeConsuming = 0;


        int idIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_ID);
        hideInfo = Integer.toString(cursor.getInt(idIndex));

        int textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);
        Utility.setBoldTextStyle(text);

        image.setImageResource(Utility.getImagebyName(textString));

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
        textString = cursor.getString(textIndex);
        if (textString.length() > 12)
        textString = textString.substring(0, 12)+"...";
        textMaterial.setText(textString);

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_AUTHOR);
        textString = cursor.getString(textIndex);
        author.setText(textString);

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_POPULARITY);
        textString = Integer.toString(cursor.getInt(textIndex));
        popularity.setText(textString +"人已做过 |");

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_DIFFICULTY);
        textString = cursor.getString(textIndex);
        difficulty.setText(textString);

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_TIME_CONSUMING);
        timeConsuming = cursor.getInt(textIndex);
        textString = Integer.toString(timeConsuming);
        time.setText(textString + "分钟 |");

        ImageView imageExpandArrow = (ImageView)view.findViewById(R.id.image_details);

        if (imageExpandArrow.getTag() == null)
        {
            imageExpandArrow.setTag(R.drawable.expand_arrow);
        }

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_IN_COOKING_LIST);
        textString = (cursor.getString(textIndex));
        imageCook.setImageResource((textString.equals(EasyKitchenContract.YES))?R.drawable.kitchen_delete:R.drawable.kitchen_select);
        hideInfo = hideInfo + "&" + textString;

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_FAVORITE);
        textString = (cursor.getString(textIndex));
        imageLike.setImageResource((textString.equals(EasyKitchenContract.YES)) ? R.drawable.like_selected : R.drawable.like_unselected);
        hideInfo = hideInfo + "&" + textString;
            /*填充隐藏信息，包括 _id ,inCookingList,favorite*/
        hideInfoView.setText(hideInfo);

            /*下拉/收起操作*/
        imageExpandArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View) (v.getParent());
                View grandpaView = (View) (view.getParent());
                LinearLayout lLayout = (LinearLayout) grandpaView.findViewById(R.id.layout_detail);

                if ((Integer) (v.getTag()) == R.drawable.expand_arrow) {
                    lLayout.setVisibility(View.VISIBLE);
                    ((ImageView) v).setImageResource(R.drawable.collapse_arrow);
                    v.setTag(R.drawable.collapse_arrow);
                } else if ((Integer) (v.getTag()) == R.drawable.collapse_arrow) {
                    lLayout.setVisibility(View.GONE);
                    ((ImageView) v).setImageResource(R.drawable.expand_arrow);
                    v.setTag(R.drawable.expand_arrow);
                } else {
                    Log.v(LOG_TAG, "R.id.image_details error ,tag = " + v.getTag());
                }
            }
        });
             /*加入/移出 待做清单操作*/
        imageCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View) (v.getParent());
                View grandpaView = (View) (view.getParent());
                TextView idView = (TextView) (grandpaView.findViewById(R.id.hideInfo));
                int id = Integer.parseInt((idView.getText().toString().split("&"))[0]);
                String isSelected = (idView.getText().toString().split("&"))[1];
                ContentValues recipeValues = new ContentValues();
                int value = 0;
                if (isSelected.equals(EasyKitchenContract.NO)) {
                    /*update database*/
                    recipeValues.put(EasyKitchenContract.Recipe.COLUMN_IN_COOKING_LIST, EasyKitchenContract.YES);
                    value = mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriById(id), recipeValues, null, null);
                    Log.v(LOG_TAG, "id=" + id);
                    /*update display*/
                    Toast.makeText(mContext, "已加入待做清单",
                            Toast.LENGTH_LONG).show();
                    ((ImageView) v).setImageResource(R.drawable.kitchen_delete);
                } else {
                    /*update database*/
                    recipeValues.put(EasyKitchenContract.Recipe.COLUMN_IN_COOKING_LIST, EasyKitchenContract.NO);
                    value = mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriById(id), recipeValues, null, null);
                    Log.v(LOG_TAG, "id=" + id);
                        /*update display*/
                    Toast.makeText(mContext, "已移出待做清单",
                            Toast.LENGTH_LONG).show();
                    ((ImageView) v).setImageResource(R.drawable.kitchen_select);
                }
                Log.v(LOG_TAG, "update value = " + value);
            }
        });

        /*收藏操作*/
        imageLike.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = (View) (v.getParent());
                View grandpaView = (View) (view.getParent());
                TextView idView = (TextView) (grandpaView.findViewById(R.id.hideInfo));
                int id = Integer.parseInt((idView.getText().toString().split("&"))[0]);
                String isSelected = (idView.getText().toString().split("&"))[2];
                ContentValues recipeValues = new ContentValues();
                int value = 0;
                if (isSelected.equals(EasyKitchenContract.NO)) {
                        /*update database*/
                    recipeValues.put(EasyKitchenContract.Recipe.COLUMN_FAVORITE, EasyKitchenContract.YES);
                    value = mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriById(id), recipeValues, null, null);
                        /*update display*/
                    ((ImageView) v).setImageResource(R.drawable.like_selected);
                } else {
                        /*update database*/
                    recipeValues.put(EasyKitchenContract.Recipe.COLUMN_FAVORITE, EasyKitchenContract.NO);
                    value = mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriById(id), recipeValues, null, null);
                        /*update display*/
                    ((ImageView) v).setImageResource(R.drawable.like_unselected);
                }
                Log.v(LOG_TAG, "update value = " + value);
            }
        });

}

    // Access method
    public HashMap<Integer, Integer> getGroupMap() {
        return mGroupMap;
    }

    /*
        Remember that these views are reused as needed.
     */
    /*
    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.my_menu_lists_listitem, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    /*
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView text = (TextView)view.findViewById(R.id.menu_lists_name);
        int textIndex = cursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);
        Utility.setBoldTextStyle(text);

        ImageView imageExpandArrow = (ImageView)view.findViewById(R.id.image_details);
        //LinearLayout lLayout = (LinearLayout) (view.findViewById(R.id.layout_detail));
        if (imageExpandArrow.getTag() == null)
        {
            imageExpandArrow.setTag(R.drawable.expand_arrow);
        }
        RecipesListFragment fragment = view.findFragmentById(R.id.fragment_menus_recipe_list)
         /*下拉/收起操作*/
        /*imageExpandArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = (View) (v.getParent());
                LinearLayout grandpaView = (LinearLayout)(view.getParent());
                RecipesListFragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_menus_recipe_list)
                if ((Integer) (v.getTag()) == R.drawable.expand_arrow) {
                    //lLayout.setVisibility(View.VISIBLE);
                    fragment = RecipesListFragment.newInstance("1",null);
                    /*update display*/
                 /*   ((ImageView) v).setImageResource(R.drawable.collapse_arrow);
                    v.setTag(R.drawable.collapse_arrow);
                } else if ((Integer) (v.getTag()) == R.drawable.collapse_arrow) {
                    //lLayout.setVisibility(View.GONE);
                    ((ImageView) v).setImageResource(R.drawable.expand_arrow);
                    v.setTag(R.drawable.expand_arrow);
                } else {
                    Log.v(LOG_TAG, "R.id.image_details error ,tag = " + v.getTag());
                }
            }
        });
    }*/

}