package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddMaterialActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    static public MultiAutoCompleteTextView mMaterialText;

    static public ArrayAdapter<String> mAdapter;
    static public TextView mHintText;
    static public Button mBConfirm;
    static public String mMaterialType = EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE;
    static public RadioGroup mRadioGroup;
    static public RadioButton mRadioV;
    static public RadioButton mRadioM;
    static public RadioButton mRadioS;
    public AddMaterialActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_material, container, false);

        mMaterialText = (MultiAutoCompleteTextView) rootView.findViewById(R.id.add_material);
        mHintText = (TextView) rootView.findViewById(R.id.add_material_hint);
        mBConfirm = (Button)rootView.findViewById(R.id.button_add_material);

        /*根据内置食材资料自动提示*/
        mAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_multiple_choice,
                StartActivity.materialList);
        mMaterialText.setAdapter(mAdapter);
        mMaterialText.setThreshold(1);
        mMaterialText.setTokenizer(new AddMaterialActivityFragment.CCommaTokenizer());//设置为中文逗号

        /*设置RadioGroup*/
        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        mRadioV = (RadioButton) rootView.findViewById(R.id.rbtnV);
        mRadioM = (RadioButton) rootView.findViewById(R.id.rbtnM);
        mRadioS = (RadioButton) rootView.findViewById(R.id.rbtnS);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mRadioV.getId()) {
                    mMaterialType = EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE;

                } else if (checkedId == mRadioM.getId()) {
                    mMaterialType = EasyKitchenContract.Material.MATERIAL_TYPE_MEAT;
                }else if (checkedId == mRadioS.getId()) {
                    mMaterialType = EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING;
                }else{
                    Log.e(LOG_TAG,"invaid type");
                }
            }
        });
        Button bAddMaterial = (Button) rootView.findViewById(R.id.button_add_material);
        bAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String material = mMaterialText.getText().toString();
                if (material == null || material.trim().length() == 0 || "".equals(material.trim()))
                {
                    ShowHintText("无效输入，请重新输入食材");
                }
                else
                {
                    mHintText.setVisibility(View.GONE);
                    UpdateMaterialDb(getActivity(),material);
                }
            }
        });


        return rootView;
    }
    /*中文逗号分隔符接口*/
    public static class CCommaTokenizer implements MultiAutoCompleteTextView.Tokenizer {
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != '，') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == '，') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == '，') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + "， ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + "， ";
                }
            }
        }
    }

    private void ShowHintText(String s)
    {
        mHintText.setText(s);
        mHintText.setVisibility(View.VISIBLE);
    }

    private void UpdateMaterialDb(Context c,String allMaterials)
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
                    newValue.put(EasyKitchenContract.Material.COLUMN_TYPE, mMaterialType);
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
