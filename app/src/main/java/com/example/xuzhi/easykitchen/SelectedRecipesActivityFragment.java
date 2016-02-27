package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectedRecipesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MenuAdapter mMenuAdapter;
    static final int SELECTED_RECIPES_LOADER_MENU = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private View mRootView;
    Context mContext;
    TextView mHintText,mRecipesCountTextView,mTotalTimeConsumingTextView,mConfrimMenuTextView;
    List mRecipeIdList = new ArrayList();
    int mRecipesCount = 0;
    int mTimeConsuming = 0;
    SelectedRecipesActivityFragment mThis;
    static String mMenuName = "菜单";
    public SelectedRecipesActivityFragment() {
        mThis = this;
        mRecipeIdList.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_selected_recipes, container, false);
        mMenuAdapter = new MenuAdapter(getActivity(), null, 0,this);
        mContext = getActivity();
        mMenuName = "菜单";
        ListView listView = (ListView) mRootView.findViewById(R.id.selected_recipes_list);
        listView.setAdapter(mMenuAdapter);
        Utility.setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int idIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_ID);
                    int id = cursor.getInt(idIndex);
                    Log.v(LOG_TAG, "recipe id = " + id);
                    Intent intent = new Intent(getActivity(), RecipeActivity.class).setData(EasyKitchenContract.Recipe.buildRecipeUriById(id));
                    startActivity(intent);
                }
            }
        });

        mHintText = (TextView) mRootView.findViewById(R.id.hintText);
        mRecipesCountTextView = (TextView) mRootView.findViewById(R.id.recipesCount);
        mTotalTimeConsumingTextView = (TextView) mRootView.findViewById(R.id.timeConsuming);
        mConfrimMenuTextView =  (TextView) mRootView.findViewById(R.id.generateMenu);
        mConfrimMenuTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mRecipeIdList.size() == 0)
                    return;


                Locale locale = Locale.getDefault();
                System.out.println("Locale is : [" + locale + "]"); // make sure there is a default Locale
                Calendar calendar = Calendar.getInstance(locale);
                String menuTime = Integer.toString(calendar.get(Calendar.YEAR)) + "-" +
                        Integer.toString(calendar.get(Calendar.MONTH) + 1) + "-" +
                        Integer.toString(calendar.get(Calendar.DATE)) + "  " +
                        Integer.toString(calendar.get(Calendar.HOUR)) + ":" +
                        Integer.toString(calendar.get(Calendar.MINUTE)) + ":"+
                        Integer.toString(calendar.get(Calendar.SECOND));
                ContentValues menuValue = new ContentValues();
                menuValue.put(EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME, mMenuName);
                menuValue.put(EasyKitchenContract.UsersMenuList.COLUMN_MENU_TIME, menuTime);
                Uri uri = mContext.getContentResolver().insert(EasyKitchenContract.UsersMenuList.CONTENT_URI, menuValue);
                /*get the id of the just inserted row */
                long menuId =  Long.parseLong(uri.getLastPathSegment());
                Log.v(LOG_TAG,"menuId = " + menuId);

                Vector<ContentValues> recipeValues = new Vector<ContentValues>( mRecipeIdList.size());
                ContentValues updateRecipeValue = new ContentValues();
                int [] recipeArray = new int[mRecipeIdList.size()];
                for (int i = 0;i < mRecipeIdList.size();i++)
                {
                    ContentValues recipeValue = new ContentValues();
                    recipeArray[i] = (int)mRecipeIdList.get(i);
                    recipeValue.put(EasyKitchenContract.UsersRecipeList.COLUMN_RECIPE_ID, (int) (mRecipeIdList.get(i)));
                    //recipeValues.put(EasyKitchenContract.UsersRecipeList.COLUMN_USER_NAME, username);后续补充用户名
                    recipeValue.put(EasyKitchenContract.UsersRecipeList.COLUMN_MENU_ID, menuId);
                    updateRecipeValue.put(EasyKitchenContract.Recipe.COLUMN_IN_COOKING_LIST, EasyKitchenContract.NO);
                    mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriById((int) (mRecipeIdList.get(i))), updateRecipeValue, null, null);

                }
                int inserted = 0;
                // add to database
                if ( recipeValues.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[recipeValues.size()];
                    recipeValues.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(EasyKitchenContract.UsersRecipeList.CONTENT_URI, cvArray);
                }
                getLoaderManager().restartLoader(SELECTED_RECIPES_LOADER_MENU, null, mThis);
                /*go to update the materials*/



                Intent intent = new Intent(getActivity(), UpdateMaterialActivity.class).putExtra("recipeIdArray",recipeArray);
                startActivity(intent);

               // new CheckMaterialDialogFragment().show(getFragmentManager(),"checkMaterial");

            }
        });
        return mRootView;
    }
     @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SELECTED_RECIPES_LOADER_MENU, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Uri uri = EasyKitchenContract.Recipe.buildRecipeUriByInCookingListStatus(EasyKitchenContract.YES);
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor==null)
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        mRecipesCount = cursor.getCount();
        if (cursor.getCount() == 0)
        {
            mHintText.setVisibility(View.VISIBLE);
        }
        else
        {
            mHintText.setVisibility(View.GONE);

        }
        mMenuAdapter.swapCursor(cursor);
        mRecipesCountTextView.setText("共计" + cursor.getCount() + "道菜");
        mTotalTimeConsumingTextView.setText("需" + mTimeConsuming + "分钟");
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMenuAdapter.swapCursor(null);
    }


    public static class CheckMaterialDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setView(inflater.inflate(android.R.layout.select_dialog_multichoice, null));
            builder.setTitle(R.string.dialog_check_material_title);
            builder.setNegativeButton(R.string.dialog_check_material_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   // int index = mCursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_ID);
                  //  int id = mCursor.getInt(index);
                  //  mContext.getContentResolver().delete(EasyKitchenContract.UsersMenuList.buildUserMenuUriById(id), null, null);
                    //refresh the display
                   // mCursor.requery();

                }
            });
            builder.setPositiveButton(R.string.dialog_long_click_rename, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*get dialog name*/
                    EditText editText = (EditText) getDialog().findViewById(R.id.add_menu_name);
                    String name = editText.getText().toString().trim();
                    if (!name.equals("")) {
                      /*  int index = mCursor.getColumnIndex(EasyKitchenContract.UsersMenuList.COLUMN_ID);
                        int id = mCursor.getInt(index);
                        ContentValues menuValue = new ContentValues();
                        menuValue.put(EasyKitchenContract.UsersMenuList.COLUMN_MENU_NAME, name);
                        mContext.getContentResolver().update(EasyKitchenContract.UsersMenuList.buildUserMenuUriById(id), menuValue, null, null);*/
                        //refresh the display
                       // mCursor.requery();

                    }
                }
            });
            return builder.create();
        }
    }

}


