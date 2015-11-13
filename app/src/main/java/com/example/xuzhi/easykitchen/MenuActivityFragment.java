package com.example.xuzhi.easykitchen;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public SimpleCursorAdapter mMenuAdapter;
    private static final int MATERIAL_LOADER_MENU = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();

    public MenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_MENU, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Uri movieUri;

        movieUri = EasyKitchenContract.Material.buildMaterialUriByType(type, "YES");

        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        SimpleCursorAdapter adapter = mVegetableAdapter;
        if (cursor==null)
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_VEGETABLE:
                adapter = mVegetableAdapter;
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

        Log.v(LOG_TAG, cursor.toString());
        adapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        SimpleCursorAdapter adapter = mVegetableAdapter;
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_VEGETABLE:
                adapter = mVegetableAdapter;
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
