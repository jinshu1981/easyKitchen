package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddMaterialActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    static public MultiAutoCompleteTextView mVegeMaterialText;
    static public MultiAutoCompleteTextView mMeatMaterialText;
    static public MultiAutoCompleteTextView mSeasoningMaterialText;
    static public ArrayAdapter<String> mAdapter;
    static public TextView mHintText;
    static public Button mBConfirm;
    public AddMaterialActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_material, container, false);

        mVegeMaterialText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_material_vege);
        mMeatMaterialText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_material_meat);
        mSeasoningMaterialText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_material_seasoning);
        mHintText = (TextView) rootView.findViewById(R.id.add_material_hint);
        mBConfirm = (Button)rootView.findViewById(R.id.button_add_material);

        mHintText.setVisibility(View.GONE);

        /*根据内置食材资料自动提示*/
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                StartActivity.materialList);
        mVegeMaterialText.setAdapter(mAdapter);
        mVegeMaterialText.setThreshold(1);
        mVegeMaterialText.setTokenizer(new Utility.CCommaTokenizer());//设置为中文逗号

        mMeatMaterialText.setAdapter(mAdapter);
        mMeatMaterialText.setThreshold(1);
        mMeatMaterialText.setTokenizer(new Utility.CCommaTokenizer());//设置为中文逗号

        mSeasoningMaterialText.setAdapter(mAdapter);
        mSeasoningMaterialText.setThreshold(1);
        mSeasoningMaterialText.setTokenizer(new Utility.CCommaTokenizer());//设置为中文逗号

        Button bAddMaterial = (Button) rootView.findViewById(R.id.button_add_material);
        bAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vegeMaterial = mVegeMaterialText.getText().toString();
                String meatMaterial = mMeatMaterialText.getText().toString();
                String seaoningMaterial = mSeasoningMaterialText.getText().toString();
                if ((vegeMaterial == null || vegeMaterial.trim().length() == 0 || "".equals(vegeMaterial.trim()))
                    &&(meatMaterial == null || meatMaterial.trim().length() == 0 || "".equals(meatMaterial.trim()))
                    &&(seaoningMaterial == null || seaoningMaterial.trim().length() == 0 || "".equals(seaoningMaterial.trim())))
                {
                    mHintText.setText(R.string.valid_check_hint);
                    mHintText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mHintText.setVisibility(View.GONE);
                    UpdateMaterialDb(getActivity(), vegeMaterial, EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE);
                    UpdateMaterialDb(getActivity(),meatMaterial,EasyKitchenContract.Material.MATERIAL_TYPE_MEAT);
                    UpdateMaterialDb(getActivity(),seaoningMaterial,EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING);
                    startActivity(new Intent(getActivity(), MaterialActivity.class));
                }
            }
        });


        return rootView;
    }

    private void UpdateMaterialDb(Context c,String allMaterials,String materialType)
    {
        String[] materialList = allMaterials.toString().split("，");
        Log.v(LOG_TAG,"mMaterialText = " + allMaterials);
        for (String material:materialList)
        {
            material = material.trim();
            if (material.equals("")) {continue;}
            Log.v(LOG_TAG,"material = " + material);
            Uri uri = EasyKitchenContract.Material.buildMaterialUriByName(material);
            String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
            Cursor cursor = c.getContentResolver().query(uri,  new String[]{EasyKitchenContract.Material.COLUMN_NAME,EasyKitchenContract.Material.COLUMN_STATUS}, null, null, sortOrder);

            try{
                if ((cursor != null) && (cursor.moveToFirst())) {
                    //Log.v(LOG_TAG,"cursor = " + cursor.toString());
                    mHintText.setVisibility(View.GONE);
                    int statusIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_STATUS);
                    String status = cursor.getString(statusIndex);
                    Log.v(LOG_TAG, "material= "+material+", status= " + status);
                    if (status.equals(EasyKitchenContract.NO))
                    {
                        ContentValues newValue = new ContentValues();
                        newValue.put(EasyKitchenContract.Material.COLUMN_STATUS, EasyKitchenContract.YES);
                        c.getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(material), newValue, null, null);
                        Log.v(LOG_TAG, "material in kitchen= " + material);
                        //新增食材，菜谱权重-1
                        Utility.UpdateSingleCursor(getActivity(), material, -1);
                    }
                }
                else{
                    ContentValues newValue = new ContentValues();
                    newValue.put(EasyKitchenContract.Material.COLUMN_NAME, material);
                    newValue.put(EasyKitchenContract.Material.COLUMN_TYPE, materialType);
                    newValue.put(EasyKitchenContract.Material.COLUMN_IMAGE, 0);
                    newValue.put(EasyKitchenContract.Material.COLUMN_IMAGE_GREY, 0);
                    newValue.put(EasyKitchenContract.Material.COLUMN_STATUS, EasyKitchenContract.YES);
                    c.getContentResolver().insert(EasyKitchenContract.Material.CONTENT_URI, newValue);
                    Log.v(LOG_TAG, "new material in kitchen= " + material);
                    //新增食材，菜谱权重-1 
                    Utility.UpdateSingleCursor(getActivity(), material,-1);

                }

            }finally {
                cursor.moveToFirst();
                cursor.close();
            }
        }
    }

}
