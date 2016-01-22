package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;
import com.example.xuzhi.easykitchen.data.EasyKitchenDbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class Utility {
    private final static String LOG_TAG = Utility.class.getSimpleName();

    static public void UpdateSingleCursor(Context c, String name,int subWeight) {
        Uri recipeUri = EasyKitchenContract.Recipe.buildRecipeUriByMaterialName(name);
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Cursor recipeCursor = c.getContentResolver().query(recipeUri, new String[]{EasyKitchenContract.Recipe.COLUMN_NAME, EasyKitchenContract.Recipe.COLUMN_MATERIAL, EasyKitchenContract.Recipe.COLUMN_WEIGHT}, null, null, sortOrder);
        try{
            if ((recipeCursor != null) && (recipeCursor.moveToFirst())) {
                ContentValues recipeValues = new ContentValues();
                int weightIndex;
                int weight;
                int recipeNameIndex;
                String recipeName;
                for (int i = 0; i < recipeCursor.getCount(); i++) {
                    recipeNameIndex = recipeCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
                    recipeName = recipeCursor.getString(recipeNameIndex);
                    //filter the unsuitable recipes
                    if (recipeFiltered(name,recipeCursor))
                    {
                        recipeCursor.moveToNext();
                        continue;
                    }

                    weightIndex = recipeCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_WEIGHT);
                    //Log.v(LOG_TAG, "weightIndex = " + weightIndex);
                    weight = recipeCursor.getInt(weightIndex) + subWeight;
                    recipeValues.put(EasyKitchenContract.Recipe.COLUMN_WEIGHT, weight);
                    Log.v(LOG_TAG, "weight = " + weight + ",recipeName = " + recipeName);
                    c.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriByName(recipeName), recipeValues, null, null);
                    recipeCursor.moveToNext();
                }
            }}finally {
            recipeCursor.moveToFirst();
            recipeCursor.close();
        }

    }

    static public boolean recipeFiltered(String materialName,Cursor recipeCursor)
    {
        String [][] recipes = {{"油","蚝油，麻油，酱油"},{"鸡","鸡精"},{"虾","虾仁"}};
        int recipeMaterialIndex = recipeCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
        String recipeMaterial = recipeCursor.getString(recipeMaterialIndex);


        for (String[] materialCouple:recipes)
        {
            if (materialCouple[0].equals(materialName))
            {
                return filterTheMaterials(materialCouple[1].split("，"),recipeMaterial);
            }
        }
        return false;
    }

    static public boolean filterTheMaterials(String[] conditions,String recipeMaterial)
    {
        for (String condition:conditions)
        {
            if (recipeMaterial.contains(condition))
                return true;
        }
        return false;
    }
    static public String getTheOppositeStatus(String status) {
        String OppositeStatus;
        if (status.equals("YES")) {
            OppositeStatus = "NO";
        } else {
            OppositeStatus = "YES";
        }
        return OppositeStatus;
    }



    static public int getRecipeWeight(String materials) {
        //Regular Expressions
        int num = 1;
        Pattern p = Pattern.compile("，");
        Matcher m = p.matcher(materials);
        while (m.find()) {
            num++;
        }
        Log.v(LOG_TAG, "num = " + num);
        /*联想输入最后自带一个中文逗号*/
        if(materials.endsWith("，")) num = num -1;
        Log.v(LOG_TAG, "materials = " + materials);
        Log.v(LOG_TAG, "RecipeWeight = " + num);
        return num;
    }
    /*去除字符串末尾的中文逗号，联想输入时可能会引入*/
    static public String formatString(String string)
    {
        String formatString = string;
        if(string.endsWith("，"))
        {
            formatString = string.substring(0,string.length()-1);
        }
        return formatString;
    }
    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    static public void copyDataBase(Context c) throws IOException {
        String dbname = "easyKitchen1.db";
        // Open your local db as the input stream
        InputStream myInput = c.getAssets().open(dbname);
        // Path to the just created empty db
        //String outFileName = getDatabasePath(dbname);
        // Open the empty db as the output stream
        final File dir = new File(c.getFilesDir() + "/data/data/com.example.xuzhi.easykitchen/databases");
        dir.mkdirs(); //create folders where write files
        //final File file = new File(dir, "easyKitchen.db");

        EasyKitchenDbHelper myDbHelper = new EasyKitchenDbHelper(c);
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        db.close();

        OutputStream myOutput = new FileOutputStream("/data/data/com.example.xuzhi.easykitchen/databases/easyKitchen.db");

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[618496];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
            Log.v(LOG_TAG,"length = " + length +"buffer = " + buffer.toString());
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    static public int getImagebyName(String name)
    {
        return R.mipmap.temp;
    }


    static public void setBoldTextStyle(TextView text){
        TextPaint tp = text.getPaint();
        tp.setFakeBoldText(true);
    }

    static public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    static public void setMenuTextViewColor(Context c,String mealType,TextView breakfastText,TextView lunchText,TextView supperText) {
        int colorSelected = c.getResources().getColor(R.color.black);
        int colorUnselected = c.getResources().getColor(R.color.lightgray);
        switch (mealType){
            case EasyKitchenContract.Recipe.MEAL_TYPE_BREAKFAST:
                breakfastText.setTextColor(colorSelected);
                lunchText.setTextColor(colorUnselected);
                supperText.setTextColor(colorUnselected);
                break;
            case EasyKitchenContract.Recipe.MEAL_TYPE_LUNCH:
                breakfastText.setTextColor(colorUnselected);
                lunchText.setTextColor(colorSelected);
                supperText.setTextColor(colorUnselected);
                break;
            case EasyKitchenContract.Recipe.MEAL_TYPE_SUPPER:
                breakfastText.setTextColor(colorUnselected);
                lunchText.setTextColor(colorUnselected);
                supperText.setTextColor(colorSelected);
                break;
            default:break;
        }

    }
    static public void GenerateMaterialList(Context c)
    {
        Uri uri = EasyKitchenContract.Material.buildAllMaterialUri();
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Cursor cursor = c.getContentResolver().query(uri,  new String[]{EasyKitchenContract.Recipe.COLUMN_NAME}, null, null, sortOrder);
        int nameIndex;
         String name;

        StartActivity.materialList = new String[cursor.getCount()];

        try{
            if ((cursor != null) && (cursor.moveToFirst())) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
                    name = cursor.getString(nameIndex);
                    StartActivity.materialList[i] = name;
                    cursor.moveToNext();
                }
            }}finally {
            cursor.moveToFirst();
            cursor.close();
        }
        Log.v(LOG_TAG,"materialList length= " + StartActivity.materialList.length );
        String temp = "";
        for (int i = 0;i < StartActivity.materialList.length;i++)
        {
            temp += StartActivity.materialList[i] + "，";
        }
        Log.v(LOG_TAG,"materialList content= " + temp );
        
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
}
