package com.example.xuzhi.easykitchen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
public class ToBuyActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static final int WILL_BUY_MATERIAL_LOADER = 0;
    private final String LOG_TAG = this.getClass().getSimpleName();
    SimpleCursorAdapter materialListAdapter;
    View rootView;
    public ToBuyActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_to_buy, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.will_buy_list);
        materialListAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{EasyKitchenContract.Material.COLUMN_NAME},
                new int[]{android.R.id.text1},0);
        listView.setAdapter(materialListAdapter);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(WILL_BUY_MATERIAL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        String sortOrder = EasyKitchenContract.Material.COLUMN_NAME+ " ASC";
        String[]projection = {EasyKitchenContract.Material.COLUMN_ID,EasyKitchenContract.Material.COLUMN_NAME};
        Uri uri = EasyKitchenContract.Material.buildMaterialUriByWillBuy(EasyKitchenContract.YES);
        return new CursorLoader(getActivity(),
                uri,
                projection,
                null,
                null,
                sortOrder);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        TextView hintText = (TextView)(rootView.findViewById(R.id.hintText));

        if((cursor==null)||(cursor.getCount() == 0))
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            if (cursor.getCount() == 0)
            {
                hintText.setVisibility(View.VISIBLE);
            }
            return;
        }
        hintText.setVisibility(View.GONE);
        materialListAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        materialListAdapter.swapCursor(null);
    }

}
