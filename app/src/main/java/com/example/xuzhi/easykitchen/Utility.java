package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class Utility {

    static public void insertVegetables(Context c) {
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues locationValues = new ContentValues();
        Uri insertedUri;
        String[][] database = {{"西瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"黄瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"西红柿", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"芹菜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"香菜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"西蓝花", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"冬瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"香菇", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"西葫芦", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"茼蒿", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"绿豆", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"赤豆", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"猪肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"鸡肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"牛肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"羊肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT},
                {"小葱", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"生姜", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"大蒜头", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"花椒", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING},
        };

        for (String[] item : database) {
            // Log.v(LOG_TAG,"item[0]="+ item[0]);
            //Log.v(LOG_TAG,"item[1]="+ item[1]);
            String status = "NO";

            locationValues.put(EasyKitchenContract.Material.COLUMN_NAME, item[0]);
            locationValues.put(EasyKitchenContract.Material.COLUMN_TYPE, item[1]);
            locationValues.put(EasyKitchenContract.Material.COLUMN_IMAGE, R.mipmap.temp);
            locationValues.put(EasyKitchenContract.Material.COLUMN_IMAGE_GREY, R.mipmap.temp_grey);
            locationValues.put(EasyKitchenContract.Material.COLUMN_STATUS, status);
            // Finally, insert location data into the database.
            insertedUri = c.getContentResolver().insert(
                    EasyKitchenContract.Material.CONTENT_URI,
                    locationValues
            );
        }

    }

    static public void UpdateSingleCursor(Context c, Cursor cursor) {
        ContentValues MaterialValues = new ContentValues();
        Uri insertedUri;

        int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
        String name = cursor.getString(nameIndex);

        int typeIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_TYPE);
        String type = cursor.getString(typeIndex);

        int imageIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_IMAGE);
        int image = cursor.getInt(imageIndex);

        int imageGreyIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_IMAGE_GREY);
        int imageGrey = cursor.getInt(imageGreyIndex);

        int statusIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_STATUS);
        String status = getTheOppositeStatus(cursor.getString(statusIndex));


        MaterialValues.put(EasyKitchenContract.Material.COLUMN_NAME, name);
        MaterialValues.put(EasyKitchenContract.Material.COLUMN_TYPE, type);
        MaterialValues.put(EasyKitchenContract.Material.COLUMN_IMAGE, image);
        MaterialValues.put(EasyKitchenContract.Material.COLUMN_IMAGE_GREY, imageGrey);
        MaterialValues.put(EasyKitchenContract.Material.COLUMN_STATUS, status);
        // Finally, insert location data into the database.
        insertedUri = c.getContentResolver().insert(
                EasyKitchenContract.Material.CONTENT_URI,
                MaterialValues);
    }

    static public String getTheOppositeStatus(String status)
    {
        String OppositeStatus;
        if (status.equals("YES")) {
            OppositeStatus = "NO";
        }
        else {
            OppositeStatus = "YES";
        }
        return OppositeStatus;
    }

    static public void insertRecipes(Context c) {
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues recipeValues = new ContentValues();
        Uri insertedUri;

        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_NAME, "凉拌黄瓜");
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_MATERIAL, "黄瓜，盐，白醋");
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_STEP, "1234");
        recipeValues.put(EasyKitchenContract.Recipe.COLUMN_IMAGE, R.mipmap.temp);
            // Finally, insert location data into the database.
            insertedUri = c.getContentResolver().insert(
                    EasyKitchenContract.Recipe.CONTENT_URI,
                    recipeValues
            );
    }
}
