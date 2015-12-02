package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddNewRecipeActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    EditText mRecipeName,mRecipeMaterials,mRecipeSteps;
    SimpleCursorAdapter mCustomRecipeslistAdapter;
    View mRootView;
    Button mButton_add,mButton_confirm;
    ListView mCustomListView;
    Context mContext;
    Cursor mCursor;
    private static final int RECIPE_LOADER_CUSTOM = 0;
    public AddNewRecipeActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_add_new_recipe, container, false);
        mRootView = rootView;
        mContext = getActivity();
        //Load custom recipes
        String [] dataColumns = {"image","name"};
        int [] viewIDs = {R.id.image,R.id.name};
        mCustomRecipeslistAdapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_custom_recipe_item, null, dataColumns, viewIDs, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mCustomListView = (ListView)rootView.findViewById(R.id.custom_recipes_list);
        mCustomListView.setAdapter(mCustomRecipeslistAdapter);

        mButton_add = (Button)rootView.findViewById(R.id.button_add);
        mButton_confirm = (Button)rootView.findViewById(R.id.button_confirm);
        mRecipeName = (EditText)rootView.findViewById(R.id.new_recipe_name);
        mRecipeMaterials = (EditText)rootView.findViewById(R.id.new_recipe_material);
        mRecipeSteps = (EditText)rootView.findViewById(R.id.new_recipe_steps);

        //Show items of add new recipes and hide recipe list
        mButton_add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowItemsOfAddRecipeandHideList();
            }
        });

        //add new recipes
        mButton_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String recipeName = mRecipeName.getText().toString();
                String recipeMaterials = mRecipeMaterials.getText().toString();
                String recipeSteps = mRecipeSteps.getText().toString();
                Log.v(LOG_TAG, "recipe is " + recipeName + recipeMaterials + recipeSteps);

                String []recipe = {recipeName,recipeMaterials,recipeSteps,"custom","NO"};
                //check and insert the recipe
                insertCustomRecipe(getActivity(), recipe);

            }
        });
        //delete recipe
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

        Log.v(LOG_TAG, cursor.toString());
        mCustomRecipeslistAdapter.swapCursor(cursor);
        Log.v(LOG_TAG, "onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCustomRecipeslistAdapter.swapCursor(null);
    }

    public void insertCustomRecipe(Context c,String[] recipe) {
        if (CustomRecipeIsValid(c,recipe)){
            insertRecipes(c, recipe);
        }

    }
    public void insertRecipes(Context c,String[] recipe) {
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues recipeValues = new ContentValues();
        Uri insertedUri;

        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_NAME, recipe[0]);
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_MATERIAL, recipe[1]);
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_STEP, recipe[2]);
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_IMAGE, R.mipmap.temp);
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_SOURCE, recipe[3]);
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_FAVORITE, recipe[4]);
        //init weight by materials number
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_WEIGHT, Utility.getRecipeWeight(recipe[1]));
        // Finally, insert recipe data into the database.
        insertedUri = c.getContentResolver().insert(
                EasyKitchenContract.Recipe.CONTENT_URI,
                recipeValues
        );

        //实例化对话框;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("下一步");
        builder.setNegativeButton("继续添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mRecipeName.setText("");
                mRecipeMaterials.setText("");
                mRecipeSteps.setText("");
            }
        });
        builder.setPositiveButton("结束", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mButton_confirm.setVisibility(View.GONE);
                mRecipeName.setVisibility(View.GONE);
                mRecipeMaterials.setVisibility(View.GONE);
                mRecipeSteps.setVisibility(View.GONE);
                ((TextView) mRootView.findViewById(R.id.textview_1)).setVisibility((View.GONE));
                ((TextView) mRootView.findViewById(R.id.textview_2)).setVisibility((View.GONE));
                ((TextView) mRootView.findViewById(R.id.textview_3)).setVisibility((View.GONE));

                mCustomListView.setVisibility((View.VISIBLE));
                mButton_add.setVisibility(View.VISIBLE);
                ((TextView) mRootView.findViewById(R.id.textview_4)).setVisibility((View.VISIBLE));
            }
        });
        builder.show();
        Log.v(LOG_TAG, "insertedUri = " + insertedUri);
    }
    static public boolean CustomRecipeIsValid(Context c,String[] recipe) {
        //实例化对话框;
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("提示信息");

        //判断菜谱信息是否为空;
        boolean validFlag = true;
        for(String info:recipe)
        {
            if (info == null || info.trim().length() == 0 || "".equals(info.trim()))
            {
                validFlag = false;
                break;
            }
        }
        if(!validFlag){
            builder.setMessage("菜谱信息不能为空，请输入内容.");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            }).show();
            return false;
        }else
        {
            return true;
        }

    }
    private void EditOrDeleteTheRecipe(Cursor cursor)
    {
        mCursor = cursor;

        //实例化对话框;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("编辑或删除");
        builder.setNegativeButton("编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get old recipe info
                int nameIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                String name = mCursor.getString(nameIndex);
                int materialIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
                String material = mCursor.getString(materialIndex);
                int stepsIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_STEP);
                String steps = mCursor.getString(stepsIndex);
                //hide the listview and show edit interface
                ShowItemsOfAddRecipeandHideList();
                mRecipeName.setText(name);
                mRecipeMaterials.setText(material);
                mRecipeSteps.setText(steps);


            }
        });
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int nameIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                String name = mCursor.getString(nameIndex);
                mContext.getContentResolver().delete(EasyKitchenContract.Recipe.buildRecipeUriByName(name),null,null);

            }
        });
        builder.show();
    }
    private void ShowItemsOfAddRecipeandHideList()
    {
        mButton_confirm.setVisibility(View.VISIBLE);
        mRecipeName.setVisibility(View.VISIBLE);
        mRecipeMaterials.setVisibility(View.VISIBLE);
        mRecipeSteps.setVisibility(View.VISIBLE);
        ((TextView)mRootView.findViewById(R.id.textview_1)).setVisibility((View.VISIBLE));
        ((TextView)mRootView.findViewById(R.id.textview_2)).setVisibility((View.VISIBLE));
        ((TextView)mRootView.findViewById(R.id.textview_3)).setVisibility((View.VISIBLE));

        mCustomListView.setVisibility((View.GONE));
        mButton_add.setVisibility(View.GONE);
        ((TextView)mRootView.findViewById(R.id.textview_4)).setVisibility((View.GONE));
    }
}
