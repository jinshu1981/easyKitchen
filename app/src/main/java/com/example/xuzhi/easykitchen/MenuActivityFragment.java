package com.example.xuzhi.easykitchen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MenuAdapter mMenuAdapter;
    private static final int MATERIAL_LOADER_MENU = 5;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static MenuActivityFragment mThis;
    private static String mMealType = "null";

    View mRootView;
    public MenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        mRootView = rootView;
        mThis = this;
        final TextView breakfastText = (TextView) rootView.findViewById(R.id.button_breakfast);
        final TextView lunchText = (TextView) rootView.findViewById(R.id.button_lunch);
        final TextView supperText = (TextView) rootView.findViewById(R.id.button_supper);
        mMenuAdapter = new MenuAdapter(getContext(), null, 0);
        ListView listView = (ListView) rootView.findViewById(R.id.recommended_recipes_list);
        listView.setAdapter(mMenuAdapter);
        Utility.setListViewHeightBasedOnChildren(listView);
        Utility.setMenuTextViewColor(getActivity(), mMealType, breakfastText, lunchText, supperText);
        Log.v(LOG_TAG, "setMenuTextViewColor mMealType = " + mMealType);
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


        breakfastText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mMealType = EasyKitchenContract.Recipe.MEAL_TYPE_BREAKFAST;
                bundle.putString("mealType", mMealType);
                getLoaderManager().restartLoader(MATERIAL_LOADER_MENU, bundle, mThis);
                Utility.setMenuTextViewColor(getActivity(), mMealType, breakfastText, lunchText, supperText);
            }
        });


        lunchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mMealType = EasyKitchenContract.Recipe.MEAL_TYPE_LUNCH;
                bundle.putString("mealType", mMealType);
                getLoaderManager().restartLoader(MATERIAL_LOADER_MENU, bundle, mThis);
                Utility.setMenuTextViewColor(getActivity(),mMealType,breakfastText,lunchText,supperText);
            }
        });

         supperText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                mMealType = EasyKitchenContract.Recipe.MEAL_TYPE_SUPPER;
                bundle.putString("mealType", mMealType);
                getLoaderManager().restartLoader(MATERIAL_LOADER_MENU, bundle, mThis);
                Utility.setMenuTextViewColor(getActivity(),mMealType,breakfastText,lunchText,supperText);
            }
        });

        return rootView;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        /*get resume args*/
        String extra = getActivity().getIntent().getStringExtra("mealType");
        SharedPreferences settings = getActivity().getPreferences(0);
        String mealType = settings.getString("mealType", "NULL");
        if (extra == null){

            Log.v(LOG_TAG," onCreate no extra mealType = " + mealType);
            mMealType = mealType;
        }
        else
        {
            mMealType = extra;
            Log.v(LOG_TAG,"onCreate mMealType = " + mMealType);
            getActivity().getIntent().removeExtra("mealType");
        }

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        bundle.putString("mealType", mMealType);
        getLoaderManager().initLoader(MATERIAL_LOADER_MENU, bundle, this);

        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_my_kitchen:
                openMineActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onPause()
    {
        SharedPreferences settings = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mealType", mMealType);
        // Commit the edits!
        editor.commit();
        Log.v(LOG_TAG, "onPause mMealType = " + mMealType);
        super.onPause();

    }
    public void  openMineActivity()
    {
        Intent intent = new Intent(getActivity(), MineActivity.class);
        startActivity(intent);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        String extra = bundle.getString("mealType");
        Log.v(LOG_TAG, "onCreateLoader mMealType = " + mMealType);
        Uri uri;
        if (extra == null)/*all type*/ {

            //weight = 0 means all materials are in kitchen
            uri = EasyKitchenContract.Recipe.buildRecipeUriByWeight(0);
        }
        else
        {
            uri = EasyKitchenContract.Recipe.buildRecipeUriByMealTypeAndWeight(extra,0);
        }
        mMealType = extra;
        return new CursorLoader(getActivity(),
                uri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        mMenuAdapter.swapCursor(cursor);
        if ((cursor == null)||(cursor.getCount()==0))
        {
            TextView noMenuText = (TextView) mRootView.findViewById(R.id.no_menu_text);
            noMenuText.setVisibility(View.GONE);
            noMenuText.setText("对不起，找到没有合适的菜单");

        }
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMenuAdapter.swapCursor(null);
    }
}
