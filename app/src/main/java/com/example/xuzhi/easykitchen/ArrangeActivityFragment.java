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
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArrangeActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MaterialAdapter mAddAdapter;

    private static final int MATERIAL_LOADER_ADD = 3;

    private final String LOG_TAG = this.getClass().getSimpleName();
    public ArrangeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_arrange, container, false);

        mAddAdapter = new MaterialAdapter(getActivity(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_add_material);
        gridView.setAdapter(mAddAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                try{
                   // Utility.UpdateSingleCursor(getActivity(), cursor);
                }finally{
                    //cursor.close();
                }}
            });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_ADD, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Material.COLUMN_NAME + " ASC";
        Uri movieUri= EasyKitchenContract.Material.CONTENT_URI.buildUpon().build();
        return new CursorLoader(getActivity(),
                movieUri,
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

        Log.v(LOG_TAG, "cursor.count = " + cursor.getCount());
        mAddAdapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAddAdapter.swapCursor(null);
    }
}
