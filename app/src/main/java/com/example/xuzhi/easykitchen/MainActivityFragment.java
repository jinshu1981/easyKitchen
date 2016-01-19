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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static public GridView mVegetableGridView;
    static public GridView mMeatGridView;
    static public GridView mSeasoningGridView;

    static public MaterialAdapter mVegetableAdapter;
    static public MaterialAdapter mMeatAdapter;
    static public MaterialAdapter mSeasoningAdapter;

    private static final int MATERIAL_LOADER_VEGETABLE = 0;
    private static final int MATERIAL_LOADER_MEAT = 1;
    private static final int MATERIAL_LOADER_SEASONING = 2;

    private final String LOG_TAG = this.getClass().getSimpleName();
    public static boolean deletebleTag = false;
    private View mRootView;
    private static MainActivityFragment mThis;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRootView = rootView;
        mThis = this;
        mVegetableAdapter = new MaterialAdapter(getActivity(), null, 0);
        mVegetableGridView = (GridView) rootView.findViewById(R.id.grid_view_vegetable);
        mVegetableGridView.setAdapter(mVegetableAdapter);

        mVegetableGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
                    String name = cursor.getString(nameIndex);
                    Intent intent = new Intent(getActivity(), RecipeActivity.class).setData(EasyKitchenContract.Recipe.buildRecipeUriByMaterialName(name));
                    startActivity(intent);
                }
            }
        });

        mMeatAdapter = new MaterialAdapter(getActivity(), null, 0);
        mMeatGridView = (GridView) rootView.findViewById(R.id.grid_view_meat);
        mMeatGridView.setAdapter(mMeatAdapter);
        mMeatGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
                    String name = cursor.getString(nameIndex);
                    Intent intent = new Intent(getActivity(), RecipeActivity.class).setData(EasyKitchenContract.Recipe.buildRecipeUriByMaterialName(name));
                    startActivity(intent);
                }
            }
        });

        mSeasoningAdapter = new MaterialAdapter(getActivity(), null, 0);
        mSeasoningGridView = (GridView) rootView.findViewById(R.id.grid_view_seasoning);
        mSeasoningGridView.setAdapter(mSeasoningAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATERIAL_LOADER_VEGETABLE, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_MEAT, null, this);
        getLoaderManager().initLoader(MATERIAL_LOADER_SEASONING, null, this);

        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_add:
                if (deletebleTag == false)
                openAddMaterialActivity();
                return true;
            case R.id.action_del: {
                onDeleteOperation(item);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = EasyKitchenContract.Material.COLUMN_NAME + " ASC";
        Uri movieUri;
        String type = EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE;
        switch (i) {
            case MATERIAL_LOADER_VEGETABLE:
                type = EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE;
                break;
            case MATERIAL_LOADER_MEAT:
                type = EasyKitchenContract.Material.MATERIAL_TYPE_MEAT;
                break;
            case MATERIAL_LOADER_SEASONING:
                type = EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING;
                break;
            default:
                break;

        }
        movieUri = EasyKitchenContract.Material.buildMaterialUriByTypeAndStatus(type, EasyKitchenContract.YES);

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
        if (cursor == null) {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        switch (cursorLoader.getId()) {
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

        // Log.v(LOG_TAG, cursor.toString());
        adapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        MaterialAdapter adapter = mVegetableAdapter;
        switch (cursorLoader.getId()) {
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

    public void openAddMaterialActivity() {
        Intent intent = new Intent(getActivity(), AddMaterialActivity.class);
        startActivity(intent);
    }

    public void onDeleteOperation(MenuItem item) {
        deletebleTag = (deletebleTag == true)?false:true;
        mVegetableGridView.setAdapter(mVegetableAdapter);
        mMeatGridView.setAdapter(mMeatAdapter);
        mSeasoningGridView.setAdapter(mSeasoningAdapter);
        if(deletebleTag == true)
        {
            item.setTitle(R.string.action_endDel);
        }
        else
        {
            /*结束删除操作后，刷新界面*/
            item.setTitle(R.string.action_del);
            getLoaderManager().restartLoader(MATERIAL_LOADER_VEGETABLE, null, this);
            getLoaderManager().restartLoader(MATERIAL_LOADER_MEAT, null, this);
            getLoaderManager().restartLoader(MATERIAL_LOADER_SEASONING, null, this);
        }
    }
}


