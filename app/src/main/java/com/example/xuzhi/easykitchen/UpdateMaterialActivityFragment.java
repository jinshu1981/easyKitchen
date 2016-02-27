package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A placeholder fragment containing a simple view.
 */
public class UpdateMaterialActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    static final int UPDATE_MATERIAL_MENU_LOADER = 0;
    static final int GET_MATERIAL_SOURCE_LOADER = 1;
    View mRootView;
    UpdateMaterialActivityFragment mThis;
    List<String> mMaterails;
    private final String LOG_TAG = this.getClass().getSimpleName();
    public UpdateMaterialActivityFragment() {
        mThis = this;
        mMaterails = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_update_material, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //int[] recipeIdArray = (int[])(getActivity().getIntent().getIntArrayExtra("recipeIdArray"));
        Bundle bundle = new Bundle();
        bundle.putIntArray("recipeIdArray",getActivity().getIntent().getIntArrayExtra("recipeIdArray"));
        getLoaderManager().initLoader(UPDATE_MATERIAL_MENU_LOADER, bundle, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        if (bundle == null)
        {
            Log.v(LOG_TAG,"invalid bundle params");
            return null;

        }
        String sortOrder;
        String[] projection = new String[2];
        Uri uri;
        if (UPDATE_MATERIAL_MENU_LOADER == i) {
            sortOrder = EasyKitchenContract.Recipe.COLUMN_ID + " ASC";
            projection[0] = EasyKitchenContract.Recipe.COLUMN_NAME;
            projection[1] = EasyKitchenContract.Recipe.COLUMN_MATERIAL;
            uri = EasyKitchenContract.Recipe.buildRecipeUriByIdList(bundle.getIntArray("recipeIdArray"));
        }
        else
        {
            sortOrder = EasyKitchenContract.Material.COLUMN_NAME + " ASC";
            projection[0] = EasyKitchenContract.Material.COLUMN_NAME;
            projection[1] =EasyKitchenContract.Material.COLUMN_SOURCE;
            uri = EasyKitchenContract.Material.buildMaterialUriByNameList(bundle.getStringArray("materialNameArray"));
        }
        return new CursorLoader(getActivity(),
                uri,
                projection,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if((cursor==null)||(cursor.getCount() == 0))
        {
            Log.v(LOG_TAG, " return cursorLoader.getId()" + cursorLoader.getId());
            return;
        }
        if (cursorLoader.getId() == UPDATE_MATERIAL_MENU_LOADER) {
            /*first: get matrials*/

            String material;
            int index = 0;
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
                Log.v(LOG_TAG, "material index = " + index);
                material = cursor.getString(index);

                Utility.GenerateMaterialList(getActivity());//get the newest material list
                String[] splitMaterial = material.split("，");
                String standardMatrial;
                for (String singleMaterial : splitMaterial) {
                    standardMatrial = getStandardMaterial(singleMaterial);
                    if (!mMaterails.contains(standardMatrial)) {
                        mMaterails.add(standardMatrial);
                    }
                }
                cursor.moveToNext();
            }
            Log.v(LOG_TAG, "material needed to update are" + mMaterails.toString());

            /*then add the table rows*/
            for (int i = 0;i < mMaterails.size();i++)
            {
                addTableRow(mMaterails.get(i));
            }
            addTableConfirm();
        }
        else
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++)
            {
                String source = cursor.getString(cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_SOURCE));
                String name = cursor.getString(cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME));

                /*内置食材改变状态，用户自定义食材删除*/
                if (source.equals(EasyKitchenContract.DEFAULTS)){
                    ContentValues newValue = new ContentValues();
                    newValue.put(EasyKitchenContract.Material.COLUMN_STATUS, EasyKitchenContract.NO);
                    getActivity().getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(name), newValue, null, null);
                }
                else
                {
                    getActivity().getContentResolver().delete(EasyKitchenContract.Material.buildMaterialUriByName(name), null, null);
                }
                //删除食材，相关菜谱权重+1
                Utility.UpdateSingleCursor(getActivity(), name,1);
                cursor.moveToNext();
            }
        }
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    String getStandardMaterial(String originalMaterial)
    {
        String[] allStandardMaterial = StartActivity.materialList;
        String standardMaterial = originalMaterial;
        int length = 0;
        for (int i = 0; i < allStandardMaterial.length;i++)
        {
            Pattern p = Pattern.compile(allStandardMaterial[i]);
            Matcher m = p.matcher(originalMaterial);
            if (m.find())
            {
                if (m.group().length() > length) {
                    standardMaterial = m.group();
                    length = m.group().length();
                }
            }
        }
        /*to be continued*/
        if (standardMaterial.equals(originalMaterial))
        {
            /*new material discovered ,add to material list*/
            ContentValues newValue = new ContentValues();
            newValue.put(EasyKitchenContract.Material.COLUMN_NAME, standardMaterial);
            newValue.put(EasyKitchenContract.Material.COLUMN_TYPE, EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE);
            getActivity().getContentResolver().insert(EasyKitchenContract.Material.CONTENT_URI, newValue);
        }
        return standardMaterial;
    }
    void addTableRow(String material)
    {
        TableRow tr_head = new TableRow(getActivity());
        tr_head.setId(R.id.tableRow0);
        tr_head.setTag("row" + material);
        tr_head.setBackgroundColor(getResources().getColor(R.color.lightgray));        // part1
        tr_head.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_head.setBackgroundResource(R.drawable.row_border);

        TextView materialTextView = new TextView(getActivity());        ;
        materialTextView.setId(R.id.textview_1);
        materialTextView.setTag("row" + material + " column 0");
        materialTextView.setText(material);
        materialTextView.setTextColor(Color.BLACK);          // part2
        materialTextView.setPadding(20, 20, 20, 20);
        materialTextView.setGravity(Gravity.CENTER);
        materialTextView.setBackgroundResource(R.drawable.column_border);

        materialTextView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        tr_head.addView(materialTextView);// add the column to the table row here

        CheckBox useUp = new CheckBox(getActivity());
        useUp.setId(R.id.textview_1);
        useUp.setTag("row" + material + " column 1");
        useUp.setPadding(20, 20, 20, 20);
        useUp.setBackgroundResource(R.drawable.column_border);
        useUp.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        tr_head.addView(useUp);// add the column to the table row here

        CheckBox buy = new CheckBox(getActivity());
        buy.setId(R.id.textview_1);
        buy.setTag("row" + material + " column 2");
        buy.setPadding(20, 20, 20, 20);
        buy.setBackgroundResource(R.drawable.column_border);
        buy.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        tr_head.addView(buy);// add the column to the table row here
        ((TableLayout) mRootView).addView(tr_head, new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,                    //part4
                ViewGroup.LayoutParams.WRAP_CONTENT));


    }
    void addTableConfirm()
    {
        TableRow tr_head = new TableRow(getActivity());
        tr_head.setId(R.id.button_confirm);
        tr_head.setTag("row" + "confirm");
        tr_head.setBackgroundColor(getResources().getColor(R.color.lightgray));        // part1
        tr_head.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_head.setBackgroundResource(R.drawable.row_border);

        TextView materialTextView = new TextView(getActivity());        ;
        materialTextView.setId(R.id.textview_1);
        materialTextView.setTag("row" + "confirm" + " column 0");
        materialTextView.setText("确认");
        materialTextView.setTextColor(Color.BLACK);          // part2
        materialTextView.setPadding(20, 20, 20, 20);
        materialTextView.setGravity(Gravity.CENTER);
        materialTextView.setBackgroundResource(R.drawable.column_border);

        materialTextView.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        tr_head.addView(materialTextView);// add the column to the table row here

        ((TableLayout) mRootView).addView(tr_head, new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,                    //part4
                ViewGroup.LayoutParams.WRAP_CONTENT));

        tr_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout tlayout = (TableLayout)v.getParent();
                String [] tempEatUp = new String[mMaterails.size()];
                int eatUpNum = 0;
                for (String material:mMaterails) {
                    TableRow temp = (TableRow)(tlayout.findViewWithTag("row" + material));
                    if (((CheckBox)temp.getChildAt(1)).isChecked())//eat up
                    {
                        tempEatUp[eatUpNum++] = material;
                    }
                    if (((CheckBox)temp.getChildAt(2)).isChecked())//buy
                    {
                        //add to need to buy list
                        ContentValues newValue = new ContentValues();
                        newValue.put(EasyKitchenContract.Material.COLUMN_BUY, EasyKitchenContract.YES);
                        getActivity().getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(material), newValue, null, null);
                    }
                }
                if (eatUpNum > 0) {
                    String[] eatUp = new String[eatUpNum];
                    for (int i  =  0;i < eatUp.length;i++) {
                        eatUp[i] = tempEatUp[i];
                    }

                    Bundle bundle = new Bundle();
                    bundle.putStringArray("materialNameArray", eatUp);
                    getLoaderManager().initLoader(GET_MATERIAL_SOURCE_LOADER, bundle, mThis);
                }
                getActivity().finish();

            }
        });
    }

}
