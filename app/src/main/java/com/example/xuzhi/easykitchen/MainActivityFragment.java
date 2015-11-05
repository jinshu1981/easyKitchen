package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MaterialAdapter mVegetableAdapter;
    static public MaterialAdapter mMeatAdapter;
    static public MaterialAdapter mFruitAdapter;
    static public MaterialAdapter mSeasoningAdapter;

    static public String MATERIAL_TYPE_VEGETABLE = "VEGETABLE";
    static public String MATERIAL_TYPE_FRUIT = "FRUIT";
    static public String MATERIAL_TYPE_MEAT = "MEAT";
    static public String MATERIAL_TYPE_SEASONING = "SEASONING";

    private static final int MATERIAL_LOADER_VEGETABLE = 0;
    private static final int MATERIAL_LOADER_MEAT = 1;
    private static final int MATERIAL_LOADER_FRUIT = 2;
    private static final int MATERIAL_LOADER_SEASONING = 3;

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        mVegetableAdapter = new MaterialAdapter(getActivity(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_vegetable);
        gridView.setAdapter(mVegetableAdapter);

        mFruitAdapter = new MaterialAdapter(getActivity(), null, 0);
        gridView = (GridView) rootView.findViewById(R.id.grid_view_fruit);
        gridView.setAdapter(mFruitAdapter);

        mMeatAdapter = new MaterialAdapter(getActivity(), null, 0);
        gridView = (GridView) rootView.findViewById(R.id.grid_view_meat);
        gridView.setAdapter(mMeatAdapter);

        mSeasoningAdapter = new MaterialAdapter(getActivity(), null, 0);
        gridView = (GridView) rootView.findViewById(R.id.grid_view_seasoning);
        gridView.setAdapter(mSeasoningAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_VEGETABLE, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_MEAT, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_FRUIT, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_SEASONING, null, this);

        insertVegetables();
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Material._ID + " ASC";
        Uri movieUri;
        String type = MATERIAL_TYPE_VEGETABLE;
        switch (i)
        {
            case MATERIAL_LOADER_VEGETABLE:
                type = MATERIAL_TYPE_VEGETABLE;
                break;
            case MATERIAL_LOADER_FRUIT:
                type = MATERIAL_TYPE_FRUIT;
                break;
            case MATERIAL_LOADER_MEAT:
                type = MATERIAL_TYPE_MEAT;
                break;
            case MATERIAL_LOADER_SEASONING:
                type = MATERIAL_TYPE_SEASONING;
                break;
            default:
                break;

        }
        movieUri = EasyKitchenContract.Material.CONTENT_URI.buildUpon().appendPath("type").appendPath(type).build();

        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        MaterialAdapter adapter = mVegetableAdapter;
        if (cursor==null)
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" +cursorLoader.getId());
            return;
        }
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_VEGETABLE:
                adapter = mVegetableAdapter;
                break;
            case MATERIAL_LOADER_FRUIT:
                adapter = mFruitAdapter;
                break;
            case MATERIAL_LOADER_MEAT:
                adapter = mMeatAdapter;
                break;
            case MATERIAL_LOADER_SEASONING:
                adapter = mSeasoningAdapter;
                break;
            default:
                break;
        }
        Log.v(LOG_TAG, "cursorLoader.getId()" +cursorLoader.getId());
        Log.v(LOG_TAG, cursor.toString());
        adapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        MaterialAdapter adapter = mVegetableAdapter;
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_VEGETABLE:
                adapter = mVegetableAdapter;
                break;
            case MATERIAL_LOADER_FRUIT:
                adapter = mFruitAdapter;
                break;
            case MATERIAL_LOADER_MEAT:
                adapter = mMeatAdapter;
                break;
            case MATERIAL_LOADER_SEASONING:
                adapter = mSeasoningAdapter;
                break;
            default:
                break;
        }
        adapter.swapCursor(null);
    }
    public void insertVegetables()
    {
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues locationValues = new ContentValues();

        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        locationValues.put(EasyKitchenContract.Material.COLUMN_NAME, "huanggua");
        locationValues.put(EasyKitchenContract.Material.COLUMN_TYPE, "VEGETABLE");
        locationValues.put(EasyKitchenContract.Material.COLUMN_IMAGE, "huanggua");
        // Finally, insert location data into the database.
        Uri insertedUri = getActivity().getContentResolver().insert(
                EasyKitchenContract.Material.CONTENT_URI,
                locationValues
        );
       /* locationValues.put(EasyKitchenContract.Material.COLUMN_NAME, "ou");
        locationValues.put(EasyKitchenContract.Material.COLUMN_TYPE, "VEGETABLE");
        locationValues.put(EasyKitchenContract.Material.COLUMN_IMAGE, "ou");
        // Finally, insert location data into the database.
         insertedUri = getActivity().getContentResolver().insert(
                EasyKitchenContract.Material.CONTENT_URI,
                locationValues
        );*/
    }
}


