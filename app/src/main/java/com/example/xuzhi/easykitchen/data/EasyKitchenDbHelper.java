package com.example.xuzhi.easykitchen.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract.Material;
import com.example.xuzhi.easykitchen.data.EasyKitchenContract.Recipe;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class EasyKitchenDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "easyKitchen.db";


    public EasyKitchenDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       final String SQL_CREATE_MATERIAL_TABLE = "CREATE TABLE " + Material.TABLE_NAME + " (" +
                Material._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Material.COLUMN_NAME + " TEXT NOT NULL, " +
                Material.COLUMN_TYPE + " TEXT NOT NULL, " +
                Material.COLUMN_IMAGE + " INTEGER NOT NULL, " +
                Material.COLUMN_IMAGE_GREY + " INTEGER NOT NULL, " +
                Material.COLUMN_STATUS + " TEXT NOT NULL, " +

                " UNIQUE (" + Material.COLUMN_NAME+ ") ON CONFLICT REPLACE);";

        //sqLiteDatabase.execSQL(SQL_CREATE_MATERIAL_TABLE);

        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + Recipe.TABLE_NAME + " (" +
                Recipe._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Recipe.COLUMN_NAME + " TEXT NOT NULL, " +
                Recipe.COLUMN_MATERIAL + " TEXT NOT NULL, " +
                Recipe.COLUMN_STEP + " TEXT NOT NULL, " +
                Recipe.COLUMN_IMAGE + " INTEGER NOT NULL, " +
                Recipe.COLUMN_WEIGHT + " INTEGER NOT NULL, " +

                " UNIQUE (" + Recipe.COLUMN_NAME+ ") ON CONFLICT REPLACE);";
       // sqLiteDatabase.execSQL(SQL_CREATE_RECIPE_TABLE);
}

@Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Material.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Recipe.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}