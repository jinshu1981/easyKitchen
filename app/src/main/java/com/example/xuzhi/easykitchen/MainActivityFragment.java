package com.example.xuzhi.easykitchen;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_VEGETABLE, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_MEAT, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_FRUIT, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_SEASONING, null, this);

        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Material._ID + " ASC";
        Uri movieUri = EasyKitchenContract.Material.CONTENT_URI;
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
        adapter.swapCursor(cursor);
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
}


