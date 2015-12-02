package com.example.xuzhi.easykitchen;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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

    static public void insertVegetables(Context c) {
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues locationValues = new ContentValues();
        Uri insertedUri;
        String[][] database = {{"西瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"黄瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"西红柿", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"芹菜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"香菜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"西蓝花", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"冬瓜", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"香菇", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"西葫芦", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"茼蒿", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"绿豆", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE}, {"赤豆", EasyKitchenContract.Material.MATERIAL_TYPE_VEGETABLE},
                {"猪肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"鸡肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"牛肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT}, {"羊肉", EasyKitchenContract.Material.MATERIAL_TYPE_MEAT},
                {"葱", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"生姜", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"蒜", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"花椒", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING},
                {"生抽", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"醋", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"糖", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}, {"盐", EasyKitchenContract.Material.MATERIAL_TYPE_SEASONING}
        };
        //read json file

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

        //update material status
        int nameIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
        String name = cursor.getString(nameIndex);
        Log.v(LOG_TAG, "recipe's materialName = " + name);
        int statusIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_STATUS);
        String status = getTheOppositeStatus(cursor.getString(statusIndex));

        MaterialValues.put(EasyKitchenContract.Material.COLUMN_STATUS, status);
        c.getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(name), MaterialValues, null, null);

        //update weight of recipes
        int subWeight = (status == "NO") ? 1 : -1;
        Uri recipeUri = EasyKitchenContract.Recipe.buildRecipeUriByMaterialName(name);
        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        Cursor recipeCursor = c.getContentResolver().query(recipeUri, null, null, null, sortOrder);
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
              /*  if (recipeFiltered(name,recipeCursor))
                {
                    recipeCursor.moveToNext();
                    continue;
                }*/

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
        String [][] recipes = {{"油","蚝油，麻油"}};
        int recipeMaterialIndex = recipeCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
        String recipeMaterial = recipeCursor.getString(recipeMaterialIndex);


        for (String[] materialCouple:recipes)
        {
            if (materialCouple[0] == materialName)
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
        Log.v(LOG_TAG, "RecipeWeight = " + num);
        return num;
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
    static public int getImagebyNameandStatus(String name,String status)
    {
        if (status.equals("YES")) {
            return R.mipmap.temp;
        } else {
            return R.mipmap.temp_grey;
        }
    }
}
