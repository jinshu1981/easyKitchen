package com.example.xuzhi.easykitchen.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class EasyKitchenProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EasyKitchenDbHelper  mOpenHelper;

    static final int EASY_KITCHEN_MATERIAL = 100;
    static final int EASY_KITCHEN_MATERIAL_WITH_TYPE = 101;
    static final int EASY_KITCHEN_MATERIAL_WITH_NAME = 102;
    static final int EASY_KITCHEN_MATERIAL_WITH_STATUS = 103;
    static final int EASY_KITCHEN_MATERIAL_WITH_TYPE_AND_STATUS = 104;

    private static final SQLiteQueryBuilder sEasyKitchenQueryBuilder;
    static{
        sEasyKitchenQueryBuilder = new SQLiteQueryBuilder();
    }
    private final String LOG_TAG = EasyKitchenProvider.class.getSimpleName();
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EasyKitchenContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL, EASY_KITCHEN_MATERIAL);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/type/*", EASY_KITCHEN_MATERIAL_WITH_TYPE);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/name/*", EASY_KITCHEN_MATERIAL_WITH_NAME);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/inKitchen/*", EASY_KITCHEN_MATERIAL_WITH_STATUS);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/type/*/*", EASY_KITCHEN_MATERIAL_WITH_TYPE_AND_STATUS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EasyKitchenDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EASY_KITCHEN_MATERIAL:
            case EASY_KITCHEN_MATERIAL_WITH_TYPE:
            case EASY_KITCHEN_MATERIAL_WITH_TYPE_AND_STATUS:
            case EASY_KITCHEN_MATERIAL_WITH_STATUS:
                return EasyKitchenContract.Material.CONTENT_TYPE;
            case EASY_KITCHEN_MATERIAL_WITH_NAME:
                return EasyKitchenContract.Material.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    //Material.type = ?
    private static final String sEasyKitchenByMaterialTypeSelection =
            EasyKitchenContract.Material.TABLE_NAME+
                    "." + EasyKitchenContract.Material.COLUMN_TYPE + " = ? ";
    //Material.inKitchen = ?
    private static final String sEasyKitchenByMaterialStatusSelection =
            EasyKitchenContract.Material.TABLE_NAME+
                    "." + EasyKitchenContract.Material.COLUMN_STATUS + " = ? ";
    //Material.type = ? AND status = ?
    private static final String sEasyKitchenByMaterialTypeAndStatusSelection =
            EasyKitchenContract.Material.TABLE_NAME+
                    "." + EasyKitchenContract.Material.COLUMN_TYPE + " = ? AND " +
                    EasyKitchenContract.Material.COLUMN_STATUS + " = ? ";
    //Material.name = ?
    private static final String sEasyKitchenByMaterialNameSelection=
            EasyKitchenContract.Material.TABLE_NAME+
                    "." + EasyKitchenContract.Material.COLUMN_NAME + " = ? ";


    private Cursor getAllMaterial(
            Uri uri, String[] projection, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                EasyKitchenContract.Material.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getMaterialByType(
            Uri uri, String[] projection, String sortOrder) {

        String type = EasyKitchenContract.Material.getTypeFromUri(uri);
        Log.v(LOG_TAG,"type = " + type);
        sEasyKitchenQueryBuilder.setTables("material");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialTypeSelection,
                new String[]{type},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMaterialByStatus(
            Uri uri, String[] projection, String sortOrder) {

        String status = EasyKitchenContract.Material.getStatusWithoutTypeFromUri(uri);
        Log.v(LOG_TAG,"status = " + status);
        sEasyKitchenQueryBuilder.setTables("material");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialStatusSelection,
                new String[]{status},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMaterialByName(
            Uri uri, String[] projection, String sortOrder) {

        String name = EasyKitchenContract.Material.getNameFromUri(uri);
        sEasyKitchenQueryBuilder.setTables("material");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialNameSelection,
                new String[]{name},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMaterialByTypeAndStatus(
            Uri uri, String[] projection, String sortOrder) {

        String type = EasyKitchenContract.Material.getTypeFromUri(uri);
        String status = EasyKitchenContract.Material.getStatusFromUri(uri);

        Log.v(LOG_TAG,"type = " + type);
        sEasyKitchenQueryBuilder.setTables("material");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialTypeAndStatusSelection,
                new String[]{type,status},
                null,
                null,
                sortOrder
        );
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie_by_high_rating/*"

            case EASY_KITCHEN_MATERIAL: {
                retCursor = getAllMaterial(uri, projection, sortOrder);

                break;
            }
            case EASY_KITCHEN_MATERIAL_WITH_TYPE: {
                retCursor = getMaterialByType(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_MATERIAL_WITH_NAME: {
                retCursor = getMaterialByName(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_MATERIAL_WITH_STATUS: {
                retCursor = getMaterialByStatus(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_MATERIAL_WITH_TYPE_AND_STATUS: {
                retCursor = getMaterialByTypeAndStatus(uri, projection, sortOrder);
                break;
            }


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EASY_KITCHEN_MATERIAL: {
                long _id = db.insert(EasyKitchenContract.Material.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyKitchenContract.Material.buildMaterialUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case EASY_KITCHEN_MATERIAL:
                rowsDeleted = db.delete(
                        EasyKitchenContract.Material.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }



    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EASY_KITCHEN_MATERIAL:
                rowsUpdated = db.update(EasyKitchenContract.Material.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EASY_KITCHEN_MATERIAL:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(EasyKitchenContract.Material.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
