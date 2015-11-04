package com.example.xuzhi.easykitchen.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract.Material;

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
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                Material._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                Material.COLUMN_NAME + " TEXT NOT NULL, " +
                Material.COLUMN_TYPE + " TEXT NOT NULL, " +
                Material.COLUMN_IMAGE + " TEXT NOT NULL, " +
                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + Material.COLUMN_NAME+ ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MATERIAL_TABLE);
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
        onCreate(sqLiteDatabase);
    }
}
