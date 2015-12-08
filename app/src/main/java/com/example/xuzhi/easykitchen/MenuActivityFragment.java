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
import android.widget.GridView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MenuActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static public MaterialAdapter mMenuAdapter;
    private static final int MATERIAL_LOADER_MENU = 5;
    private final String LOG_TAG = this.getClass().getSimpleName();
    View mRootView;
    public MenuActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        mRootView = rootView;

       mMenuAdapter = new MaterialAdapter(getContext(), null, 0);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_menu);
        gridView.setAdapter(mMenuAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getLoaderManager().initLoader(MATERIAL_LOADER_MENU, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        String extra = getActivity().getIntent().getStringExtra("mealType");
        Uri uri;
        if (extra == null)/*all type*/ {

            //weight = 0 means all materials are in kitchen
            uri = EasyKitchenContract.Recipe.buildRecipeUriByWeight(0);
        }
        else
        {
            uri = EasyKitchenContract.Recipe.buildRecipeUriByMealTypeAndWeight(extra,0);
        }
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
            ((TextView) mRootView.findViewById(R.id.no_menu_text))
                    .setText("对不起，找到没有合适的菜单");
        }
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMenuAdapter.swapCursor(null);
    }
}
