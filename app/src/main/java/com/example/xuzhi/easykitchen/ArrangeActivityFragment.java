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
    // static public MaterialAdapter mVegetableAdapter;
    static public MaterialAdapter mAddAdapter;
  //  static public SimpleCursorAdapter mDeleteAdapter;



    private static final int MATERIAL_LOADER_ADD = 10;
  //  private static final int MATERIAL_LOADER_DELETE = 1;

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    public ArrangeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_arrange, container, false);

        String [] dataColumns = {"image","name"};
        int [] viewIDs = {R.id.image,R.id.name};

        mAddAdapter = new MaterialAdapter(getActivity(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_add_material);
        gridView.setAdapter(mAddAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //ImageView MoviesDetail = (ImageView)mMoviesImageAdapter.getItem(position);
                // Log.v(LOG_TAG, "MoviesDetail = " + String.valueOf(MoviesDetail));
                //Log.v(LOG_TAG, "MoviesDetail.id = " + String.valueOf(MoviesDetail.getId()));
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Utility.UpdateSingleCursor(getActivity(), cursor);
                }
            }
            });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_ADD, null, this);
       // getLoaderManager().initLoader(MATERIAL_LOADER_DELETE, null, this);

        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Material.COLUMN_NAME + " ASC";
        Uri movieUri;
       // if (i == MATERIAL_LOADER_ADD)
        {
            movieUri = EasyKitchenContract.Material.CONTENT_URI.buildUpon().build();
        }
       /* else
        {
            movieUri = EasyKitchenContract.Material.CONTENT_URI.buildUpon().appendPath("status").appendPath("YES").build();
        }*/

        return new CursorLoader(getActivity(),
                movieUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        MaterialAdapter adapter = mAddAdapter;
        if (cursor==null)
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_ADD:
                adapter = mAddAdapter;
                break;
            /*case MATERIAL_LOADER_DELETE:
                adapter = mDeleteAdapter;
                break;*/
            default:
                break;
        }

        Log.v(LOG_TAG, cursor.toString());
        adapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        MaterialAdapter adapter = mAddAdapter;
        switch (cursorLoader.getId())
        {
            case MATERIAL_LOADER_ADD:
                adapter = mAddAdapter;
                break;
           /* case MATERIAL_LOADER_DELETE:
                adapter = mDeleteAdapter;
                break;*/
            default:
                break;
        }
        adapter.swapCursor(null);
    }
}
