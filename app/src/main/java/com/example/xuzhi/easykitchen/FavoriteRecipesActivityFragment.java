package com.example.xuzhi.easykitchen;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoriteRecipesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final int RECIPE_LOADER_FAVORITE = 0;
    //private SimpleCursorAdapter mFavoriteRecipesListAdapter;
    private ListView mFavoriteListView;
    private MenuAdapter mFavoriteRecipesListAdapter;
    public FavoriteRecipesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite_recipes, container, false);

        /*String [] dataColumns = {"image","name"};
        int [] viewIDs = {R.id.image,R.id.name};
        mFavoriteRecipesListAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_custom_recipe_item, null, dataColumns, viewIDs, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mFavoriteListView = (ListView)rootView.findViewById(R.id.favorite_recipes_list);
        mFavoriteListView.setAdapter(mFavoriteRecipesListAdapter);*/

        mFavoriteRecipesListAdapter = new MenuAdapter(getContext(), null, 0);
        mFavoriteListView = (ListView) rootView.findViewById(R.id.favorite_recipes_list);
        mFavoriteListView.setAdapter(mFavoriteRecipesListAdapter);
        Utility.setListViewHeightBasedOnChildren(mFavoriteListView);

        mFavoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                    String name = cursor.getString(nameIndex);
                    Intent intent = new Intent(getActivity(), RecipeActivity.class).setData(EasyKitchenContract.Recipe.buildRecipeUriByName(name));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_LOADER_FAVORITE, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Uri uri= EasyKitchenContract.Recipe.buildRecipeUriByFavorite();
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

        Log.v(LOG_TAG, cursor.toString());
        mFavoriteRecipesListAdapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mFavoriteRecipesListAdapter.swapCursor(null);
    }

}
