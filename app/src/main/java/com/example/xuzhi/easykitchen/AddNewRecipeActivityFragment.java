package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddNewRecipeActivityFragment extends Fragment{
    private final String LOG_TAG = this.getClass().getSimpleName();
    static EditText mRecipeName,mRecipeSteps;
    static MultiAutoCompleteTextView mRecipeMaterials,mRecipeSeasoning;
    static public ArrayAdapter<String> mAdapter;
    View mRootView;
    Button mButton_confirm;
    static CheckBox mCheckBox_breakfast,mCheckBox_lunch,mCheckBox_supper;
    Context mContext;
   // String mMealType = "";
    static int mMealType = 0;/*0-0-0,breakfast-lunch-supper */
    private AddNewRecipeActivityFragment mThis;
    public AddNewRecipeActivityFragment() {
        mMealType = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_add_new_recipe, container, false);
        mRootView = rootView;
        mContext = getActivity();
        mThis = this;
        mButton_confirm = (Button)rootView.findViewById(R.id.button_confirm);
        mRecipeName = (EditText)rootView.findViewById(R.id.new_recipe_name);
        mRecipeSteps = (EditText)rootView.findViewById(R.id.new_recipe_steps);

        mRecipeMaterials = (MultiAutoCompleteTextView)rootView.findViewById(R.id.new_recipe_material);
        mRecipeSeasoning = (MultiAutoCompleteTextView)rootView.findViewById(R.id.new_recipe_seasoning);

        /*根据内置食材资料自动提示*/
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                StartActivity.materialList);
        mRecipeMaterials.setAdapter(mAdapter);
        mRecipeMaterials.setThreshold(1);
        mRecipeMaterials.setTokenizer(new Utility.CCommaTokenizer());//设置为中文逗号
        mRecipeSeasoning.setAdapter(mAdapter);
        mRecipeSeasoning.setThreshold(1);
        mRecipeSeasoning.setTokenizer(new Utility.CCommaTokenizer());//设置为中文逗号

        mCheckBox_breakfast = (CheckBox)rootView.findViewById(R.id.checkbox_breakfast);
        mCheckBox_lunch = (CheckBox)rootView.findViewById(R.id.checkbox_lunch);
        mCheckBox_supper = (CheckBox)rootView.findViewById(R.id.checkbox_supper);

        if (getActivity().getIntent().hasExtra(Intent.EXTRA_TEXT))
        {
            String recipe = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
            String[] recipes = recipe.split("@@");
            mRecipeName.setText(recipes[0]);
            mRecipeMaterials.setText(recipes[1]);
            mRecipeSeasoning.setText(recipes[4]);
            mRecipeSteps.setText(recipes[2]);
            if (recipes[3].contains(EasyKitchenContract.Recipe.MEAL_TYPE_BREAKFAST))
            {
                mCheckBox_breakfast.setChecked(true);
            }
            if (recipes[3].contains(EasyKitchenContract.Recipe.MEAL_TYPE_LUNCH))
            {
                mCheckBox_lunch.setChecked(true);
            }
            if (recipes[3].contains(EasyKitchenContract.Recipe.MEAL_TYPE_SUPPER))
            {
                mCheckBox_supper.setChecked(true);
            }
        }

        //add new recipes
        mButton_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String recipeName = mRecipeName.getText().toString().trim();
                String recipeMaterials = Utility.formatString(mRecipeMaterials.getText().toString().trim());
                String recipeSeasoning = Utility.formatString(mRecipeSeasoning.getText().toString().trim());
                /*如果食谱没有辅料，默认填充为“无”*/
                if (recipeSeasoning == null || recipeSeasoning.trim().length() == 0 || "".equals(recipeSeasoning.trim()))
                {
                    recipeSeasoning = getResources().getString(R.string.new_recipes_none);
                }
                String recipeSteps = mRecipeSteps.getText().toString().trim();
                String mealType = getMealTypeString(mCheckBox_breakfast,mCheckBox_lunch,mCheckBox_supper);
                Log.v(LOG_TAG, "recipe is " + recipeName + recipeMaterials + recipeSeasoning + recipeSteps + mealType);

                /*菜谱名，主料，步骤，是否自定义，是否偏爱，早中晚餐类型，辅料*/
                String[] recipe = {recipeName, recipeMaterials, recipeSteps, EasyKitchenContract.CUSTOMISED,EasyKitchenContract.NO,mealType,recipeSeasoning,};
                //check and insert the recipe
                insertCustomRecipe(getActivity(), recipe);

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //getLoaderManager().initLoader(RECIPE_LOADER_CUSTOM, null, this);
        super.onActivityCreated(savedInstanceState);
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
        //calc weight by materials number
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_WEIGHT, getCustomRecipeWeight(recipe));
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_MEAL_TYPE,recipe[5]);

        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_SEASONING, recipe[6]);
        // Finally, insert recipe data into the database.
        insertedUri = c.getContentResolver().insert(
                EasyKitchenContract.Recipe.CONTENT_URI,
                recipeValues
        );

        new ConfirmDialogFragment().show(this.getFragmentManager(),"confirm");
        Log.v(LOG_TAG, "insertedUri = " + insertedUri);
    }
    private int getCustomRecipeWeight(String[] recipe)
    {
        String recipeMaterial = recipe[1].trim();
        int weight = Utility.getRecipeWeight(recipeMaterial);
        Uri materialUri = EasyKitchenContract.Material.buildMaterialUriByStatus(EasyKitchenContract.YES);
        String sortOrder = EasyKitchenContract.Material.COLUMN_NAME + " ASC";
        Cursor cursor = mContext.getContentResolver().query(materialUri, null, null, null, sortOrder);
        try{
            if ((cursor!= null)&& cursor.moveToFirst())
            {
                Log.v(LOG_TAG, "cursor.getCount() = " + cursor.getCount());
                for (int i =  0;i < cursor.getCount();i++)
                {
                    int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
                    String name = cursor.getString(nameIndex);
                    Log.v(LOG_TAG, "material name = " + name);
                    if (recipeMaterial.contains(name))
                    {
                        weight--;
                    }
                    cursor.moveToNext();
                }
            }
        }finally {
            cursor.moveToFirst();
            cursor.close();
        }
        assert (weight >=0);
        Log.v(LOG_TAG, "CustomRecipeWeight = " + weight);
        return weight;
    }
    public boolean CustomRecipeIsValid(Context c,String[] recipe) {

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
            new ValidInputCheckDialogFragment().show(this.getFragmentManager(), "CheckValidInput");
            return false;
        }else
        {
            return true;
        }
    }
    public String getMealTypeString(CheckBox breakfast_checkbox,CheckBox lunch_checkbox,CheckBox supper_checkbox)
    {
        String mealString = "";
        if (breakfast_checkbox.isChecked())
        {
            mealString = mealString + EasyKitchenContract.Recipe.MEAL_TYPE_BREAKFAST;;
        }
        if (lunch_checkbox.isChecked())
        {
            mealString = mealString + EasyKitchenContract.Recipe.MEAL_TYPE_LUNCH;;
        }
        if (supper_checkbox.isChecked())
        {
            mealString = mealString + EasyKitchenContract.Recipe.MEAL_TYPE_SUPPER;;
        }
        if (mealString.equals(""))
        {
            return EasyKitchenContract.Recipe.MEAL_TYPE_ALL;
        }
        else {
            return mealString;
        }
    }

    public static class ValidInputCheckDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.dialog_ValidInputCheck_title);
            builder.setMessage(R.string.dialog_ValidInputCheck_message);
            builder.setPositiveButton(R.string.dialog_ValidInputCheck_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                }
            });
            return builder.create();
        }
    }

    public static class ConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.dialog_confirm_title);
            builder.setNegativeButton(R.string.dialog_confirm_continue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mRecipeName.setText("");
                    mRecipeMaterials.setText("");
                    mRecipeSteps.setText("");
                    mCheckBox_breakfast.setChecked(false);
                    mCheckBox_lunch.setChecked(false);
                    mCheckBox_supper.setChecked(false);
                }
            });
            builder.setPositiveButton(R.string.dialog_confirm_finish, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), CustomRecipesActivity.class);
                    startActivity(intent);
                }
            });
            return builder.create();
        }
    }
}
