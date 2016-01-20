package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class CustomRecipesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final int RECIPE_LOADER_CUSTOM = 0;
    SimpleCursorAdapter mCustomRecipesListAdapter;
    ListView mCustomListView;
    static Cursor mCursor;
    static Context mContext;
    TextView mHintText;
    static private CustomRecipesActivityFragment mThis;
    public CustomRecipesActivityFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_custom_recipes, container, false);
        mContext = getActivity();
        mThis = this;
        mHintText = (TextView)rootView.findViewById(R.id.hintText);
        //Load custom recipes
        String [] dataColumns = {"image","name","material"};
        int [] viewIDs = {R.id.image,R.id.name,R.id.material};
        mCustomRecipesListAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_custom_recipe_item, null, dataColumns, viewIDs, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mCustomListView = (ListView)rootView.findViewById(R.id.custom_recipes_list);
        mCustomListView.setAdapter(mCustomRecipesListAdapter);
        mCustomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
        //delete or edit recipe
        mCustomListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                EditOrDeleteTheRecipe(cursor);
                return true;
            }

        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_LOADER_CUSTOM, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //inflater.inflate(R.menu.menu_custom_recipes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理动作按钮的点击事件
        switch (item.getItemId()) {
            case R.id.action_add_custom_recipe:
            {
                Intent intent = new Intent(getActivity(), AddNewRecipeActivity.class);
                startActivity(intent);
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
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Uri uri= EasyKitchenContract.Recipe.buildRecipeUriBySource("custom");


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
        if (cursor.getCount() == 0)
        {
            mHintText.setVisibility(View.VISIBLE);
        }
        else
        {
            mHintText.setVisibility(View.GONE);
        }
        Log.v(LOG_TAG, cursor.toString());
        mCustomRecipesListAdapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCustomRecipesListAdapter.swapCursor(null);
    }
    private void EditOrDeleteTheRecipe(Cursor cursor)
    {
        mCursor = cursor;
        new EditOrDeleteDialogFragment().show(getFragmentManager(),"EditOrDeleteDialog");
        /*
        //实例化对话框;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_edit_or_delete);
        builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get old recipe info
                int nameIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                String name = mCursor.getString(nameIndex);
                int materialIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
                String material = mCursor.getString(materialIndex);
                int stepsIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_STEP);
                String steps = mCursor.getString(stepsIndex);
                int mealTypeIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MEAL_TYPE);
                String mealType = mCursor.getString(mealTypeIndex);
                int seasoningIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_SEASONING);
                String seasoning = mCursor.getString(seasoningIndex);
                Intent intent = new Intent(getActivity(), AddNewRecipeActivity.class).putExtra(Intent.EXTRA_TEXT,name +"@@" + material + "@@"+steps+"@@" +mealType + "@@" + seasoning);
                startActivity(intent);



            }
        });
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int idIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_ID);
                int id = mCursor.getInt(idIndex);
                mContext.getContentResolver().delete(EasyKitchenContract.Recipe.buildRecipeUriById(id),null,null);
                getLoaderManager().restartLoader(RECIPE_LOADER_CUSTOM, null, mThis);

            }
        });
        builder.show();*/
    }
    public static class EditOrDeleteDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.dialog_edit_or_delete_title);
            builder.setNegativeButton(R.string.dialog_edit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //get old recipe info
                    int nameIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                    String name = mCursor.getString(nameIndex);
                    int materialIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
                    String material = mCursor.getString(materialIndex);
                    int stepsIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_STEP);
                    String steps = mCursor.getString(stepsIndex);
                    int mealTypeIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MEAL_TYPE);
                    String mealType = mCursor.getString(mealTypeIndex);
                    int seasoningIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_SEASONING);
                    String seasoning = mCursor.getString(seasoningIndex);
                    Intent intent = new Intent(getActivity(), AddNewRecipeActivity.class).putExtra(Intent.EXTRA_TEXT,name +"@@" + material + "@@"+steps+"@@" +mealType + "@@" + seasoning);
                    startActivity(intent);



                }
            });
            builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int idIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_ID);
                    int id = mCursor.getInt(idIndex);
                    mContext.getContentResolver().delete(EasyKitchenContract.Recipe.buildRecipeUriById(id),null,null);
                    getLoaderManager().restartLoader(RECIPE_LOADER_CUSTOM, null, mThis);

                }
            });
            return builder.create();
        }
    }
}
