package com.example.xuzhi.easykitchen;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class BrowseRecipeActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final int BROWSE_RECIPE_LOADER_CUSTOM = 0;
    MenuAdapter mBrowseRecipesListAdapter;
    ListView mBrowseRecipesListView;
    static TextView mtimeConsuming,mdifficulty,mpopularity;
    //static String mSelectTag;
    BrowseRecipeActivityFragment mThis;
    public BrowseRecipeActivityFragment() {
        //mSelectTag = getResources().getString(R.string.browse_recipes_popularity);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle bundle = new Bundle();
        String tag = getResources().getString(R.string.browse_recipes_popularity);
        bundle.putString("tag", tag);
        getLoaderManager().initLoader(BROWSE_RECIPE_LOADER_CUSTOM, bundle, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_browse_recipe, container, false);
        mBrowseRecipesListAdapter = new MenuAdapter(getContext(), null, 0);
        mBrowseRecipesListView = (ListView)rootView.findViewById(R.id.browse_recipes_list);
        mBrowseRecipesListView.setAdapter(mBrowseRecipesListAdapter);
        /*mBrowseRecipesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });*/
        mThis = this;
        mtimeConsuming = (TextView)rootView.findViewById(R.id.timeConsuming);
        mdifficulty = (TextView)rootView.findViewById(R.id.difficulty);
        mpopularity = (TextView)rootView.findViewById(R.id.popularity);

        /*默认按照欢迎程度排序*/
        mpopularity.setTextColor(getResources().getColor(R.color.white));
        mpopularity.setBackgroundColor(getResources().getColor(R.color.lightpink));

        mtimeConsuming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSelectTag = getResources().getString(R.string.browse_recipes_timeConsuming);
                mtimeConsuming.setTextColor(getResources().getColor(R.color.white));
                mtimeConsuming.setBackgroundColor(getResources().getColor(R.color.lightpink));
                mdifficulty.setTextColor(getResources().getColor(R.color.gray));
                mdifficulty.setBackgroundColor(getResources().getColor(R.color.lightgray));
                mpopularity.setTextColor(getResources().getColor(R.color.gray));
                mpopularity.setBackgroundColor(getResources().getColor(R.color.lightgray));
                Bundle bundle = new Bundle();
                String tag = getResources().getString(R.string.browse_recipes_timeConsuming);
                bundle.putString("tag", tag);
                getLoaderManager().restartLoader(BROWSE_RECIPE_LOADER_CUSTOM, bundle, mThis);

            }
        });
        mdifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSelectTag = getResources().getString(R.string.browse_recipes_timeConsuming);
                mdifficulty.setTextColor(getResources().getColor(R.color.white));
                mdifficulty.setBackgroundColor(getResources().getColor(R.color.lightpink));
                mtimeConsuming.setTextColor(getResources().getColor(R.color.gray));
                mtimeConsuming.setBackgroundColor(getResources().getColor(R.color.lightgray));
                mpopularity.setTextColor(getResources().getColor(R.color.gray));
                mpopularity.setBackgroundColor(getResources().getColor(R.color.lightgray));

                Bundle bundle = new Bundle();
                String tag = getResources().getString(R.string.browse_recipes_difficulty);
                bundle.putString("tag", tag);
                getLoaderManager().restartLoader(BROWSE_RECIPE_LOADER_CUSTOM, bundle, mThis);

            }
        });
        mpopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSelectTag = getResources().getString(R.string.browse_recipes_timeConsuming);
                mpopularity.setTextColor(getResources().getColor(R.color.white));
                mpopularity.setBackgroundColor(getResources().getColor(R.color.lightpink));
                mtimeConsuming.setTextColor(getResources().getColor(R.color.gray));
                mtimeConsuming.setBackgroundColor(getResources().getColor(R.color.lightgray));
                mdifficulty.setTextColor(getResources().getColor(R.color.gray));
                mdifficulty.setBackgroundColor(getResources().getColor(R.color.lightgray));

                Bundle bundle = new Bundle();
                String tag = getResources().getString(R.string.browse_recipes_popularity);
                bundle.putString("tag", tag);
                getLoaderManager().restartLoader(BROWSE_RECIPE_LOADER_CUSTOM, bundle, mThis);

            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String tag = bundle.getString("tag");
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_POPULARITY + " ASC";;
        if (tag.equals(getResources().getString(R.string.browse_recipes_timeConsuming)))
        {
            sortOrder = EasyKitchenContract.Recipe.COLUMN_TIME_CONSUMING + " ASC";
        }
        else if (tag.equals(getResources().getString(R.string.browse_recipes_popularity)))
        {
            sortOrder = EasyKitchenContract.Recipe.COLUMN_POPULARITY + " ASC";
        }
        else if (tag.equals(getResources().getString(R.string.browse_recipes_difficulty)))
        {
            sortOrder = EasyKitchenContract.Recipe.COLUMN_DIFFICULTY_LEVEL + " ASC";
        }
        else
        {
            Log.v(LOG_TAG,"unKnown tag");
        }
        Uri uri= EasyKitchenContract.Recipe.CONTENT_URI;


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
        mBrowseRecipesListAdapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mBrowseRecipesListAdapter.swapCursor(null);
    }
}
