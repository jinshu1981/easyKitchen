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

    static final int EASY_KITCHEN_RECIPE = 200;
    static final int EASY_KITCHEN_RECIPE_WITH_NAME = 201;
    static final int EASY_KITCHEN_RECIPE_WITH_MATERIAL = 202;
    static final int EASY_KITCHEN_RECIPE_WITH_ALL_MATERIAL = 203;
    static final int EASY_KITCHEN_RECIPE_WITH_WEIGHT = 204;
    static final int EASY_KITCHEN_RECIPE_WITH_SOURCE = 205;
    static final int EASY_KITCHEN_RECIPE_WITH_FAVORITE = 206;
    static final int EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT = 207;


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
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/status/*", EASY_KITCHEN_MATERIAL_WITH_STATUS);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/type/*/*", EASY_KITCHEN_MATERIAL_WITH_TYPE_AND_STATUS);

        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE, EASY_KITCHEN_RECIPE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/material/*", EASY_KITCHEN_RECIPE_WITH_MATERIAL);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/name/*", EASY_KITCHEN_RECIPE_WITH_NAME);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/allMaterials/*", EASY_KITCHEN_RECIPE_WITH_ALL_MATERIAL);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/weight/*", EASY_KITCHEN_RECIPE_WITH_WEIGHT);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/source/*", EASY_KITCHEN_RECIPE_WITH_SOURCE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/favorite", EASY_KITCHEN_RECIPE_WITH_FAVORITE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/mealType/*/*", EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT);

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

            case EASY_KITCHEN_RECIPE:
            case EASY_KITCHEN_RECIPE_WITH_MATERIAL:
            case EASY_KITCHEN_RECIPE_WITH_ALL_MATERIAL:
            case EASY_KITCHEN_RECIPE_WITH_WEIGHT:
            case EASY_KITCHEN_RECIPE_WITH_SOURCE:
            case EASY_KITCHEN_RECIPE_WITH_FAVORITE:
            case EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT:
                return EasyKitchenContract.Recipe.CONTENT_TYPE;

            case EASY_KITCHEN_RECIPE_WITH_NAME:
                return EasyKitchenContract.Recipe.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /***********************************Material Table*********************************************/
        //Material.type = ?
        private static final String sEasyKitchenByMaterialTypeSelection =
                EasyKitchenContract.Material.TABLE_NAME +
                        "." + EasyKitchenContract.Material.COLUMN_TYPE + " = ? ";
        //Material.status = ?
        private static final String sEasyKitchenByMaterialStatusSelection =
                EasyKitchenContract.Material.TABLE_NAME +
                        "." + EasyKitchenContract.Material.COLUMN_STATUS + " = ? ";
        //Material.type = ? AND status = ?
        private static final String sEasyKitchenByMaterialTypeAndStatusSelection =
                EasyKitchenContract.Material.TABLE_NAME +
                        "." + EasyKitchenContract.Material.COLUMN_TYPE + " = ? AND " +
                        EasyKitchenContract.Material.COLUMN_STATUS + " = ? ";
        //Material.name = ?
        private static final String sEasyKitchenByMaterialNameSelection =
                EasyKitchenContract.Material.TABLE_NAME +
                        "." + EasyKitchenContract.Material.COLUMN_NAME + " = ? ";


    private Cursor getAllMaterial(
            Uri uri, String[] projection, String sortOrder) {
        sEasyKitchenQueryBuilder.setTables("material");
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
        Log.v(LOG_TAG, "type = " + type);
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
        Log.v(LOG_TAG, "status = " + status);
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

        Log.v(LOG_TAG, "type = " + type);
        sEasyKitchenQueryBuilder.setTables("material");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialTypeAndStatusSelection,
                new String[]{type, status},
                null,
                null,
                sortOrder
        );
    }

    private int UpdateMaterialByName(Uri uri, ContentValues values) {

        String name = EasyKitchenContract.Material.getNameFromUri(uri);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(EasyKitchenContract.Material.TABLE_NAME, values, sEasyKitchenByMaterialNameSelection,
                new String[]{name});

    }

    /***********************************Recipe Table*********************************************/
    //Recipe.material Like ?
    private static final String sEasyKitchenByRecipeMaterialMatchSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_MATERIAL + " LIKE ?";
    //Recipe.material = ?
    private static final String sEasyKitchenByRecipeMaterialSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_MATERIAL + " = ?";
    //Recipe.name = ?
    private static final String sEasyKitchenByRecipeNameSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_NAME + " = ?";
    //Recipe.source = ?
    private static final String sEasyKitchenByRecipeSourceSelection=
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_SOURCE + " = ?";
    //Recipe.favorite = yes
    private static final String sEasyKitchenByRecipeFavoriteSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_FAVORITE + " = ?";

    //Recipe.weight = ?
    private static final String sEasyKitchenByRecipeWeightSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_WEIGHT + " = ?";
    //recipe.mealType LIKE ? AND status = ?
    private static final String sEasyKitchenByRecipeTypeAndWeightSelection =
            EasyKitchenContract.Recipe.TABLE_NAME +
                    "." + EasyKitchenContract.Recipe.COLUMN_MEAL_TYPE + " LIKE ? AND " +
                    EasyKitchenContract.Recipe.COLUMN_WEIGHT + " = ? ";

    private Cursor getRecipeByMatchMaterial(
            Uri uri, String[] projection, String sortOrder) {

        String material = EasyKitchenContract.Recipe.getMaterialFromUri(uri);
        Log.v(LOG_TAG,"material = " + material);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeMaterialMatchSelection,
                new String[]{"%"+material+"%"},
                null,
                null,
                sortOrder
        );
    }


    private Cursor getRecipeByWeight(
            Uri uri, String[] projection, String sortOrder) {

        String weight = EasyKitchenContract.Recipe.getWeightFromUri(uri);
        Log.v(LOG_TAG,"weight = " + weight);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeWeightSelection,
                new String[]{weight},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getRecipeBySource(
            Uri uri, String[] projection, String sortOrder) {

        String source = EasyKitchenContract.Recipe.getSourceFromUri(uri);
        Log.v(LOG_TAG,"source = " + source);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeSourceSelection,
                new String[]{source},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getRecipeByName(
            Uri uri, String[] projection, String sortOrder) {

        String name = EasyKitchenContract.Recipe.getNameFromUri(uri);
        Log.v(LOG_TAG,"name = " + name);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeNameSelection,
                new String[]{name},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteRecipes(
            Uri uri, String[] projection, String sortOrder) {

        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeFavoriteSelection,
                new String[]{"yes"},
                null,
                null,
                sortOrder
        );
    }


    private int UpdateRecipeByName(Uri uri, ContentValues values) {

        String name = EasyKitchenContract.Recipe.getNameFromUri(uri);
        Log.v(LOG_TAG, "name = " + name);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(EasyKitchenContract.Recipe.TABLE_NAME, values, sEasyKitchenByRecipeNameSelection,
                new String[]{name});

    }
    private int DeleteTheRecipeByName(Uri uri) {

        String name = EasyKitchenContract.Recipe.getNameFromUri(uri);
        Log.v(LOG_TAG, "name = " + name);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return  db.delete(
                EasyKitchenContract.Recipe.TABLE_NAME, sEasyKitchenByRecipeNameSelection, new String[]{name});

    }

    private Cursor getRecipeByMealTypeAndWeight(
            Uri uri, String[] projection, String sortOrder) {

        String mealType = EasyKitchenContract.Recipe.getMealTypeFromUri(uri);
        String weight = EasyKitchenContract.Recipe.getWeightFromUriWithMealType(uri);
        Log.v(LOG_TAG,"mealType = " + mealType +",weight = " + weight);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeTypeAndWeightSelection,
                new String[]{"%" + mealType + "%",weight},
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
        Log.v(LOG_TAG,"query uri = " + uri.toString());
        switch (sUriMatcher.match(uri)) {


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

            case EASY_KITCHEN_RECIPE_WITH_MATERIAL: {
                retCursor = getRecipeByMatchMaterial(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_WEIGHT:{
                retCursor = getRecipeByWeight(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_SOURCE: {
                retCursor = getRecipeBySource(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_FAVORITE:{
                retCursor = getFavoriteRecipes(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_NAME: {
                retCursor = getRecipeByName(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT: {
                retCursor = getRecipeByMealTypeAndWeight(uri, projection, sortOrder);
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
        Log.v(LOG_TAG,"insert uri = " + uri.toString());
        switch (match) {
            case EASY_KITCHEN_MATERIAL: {
                long _id = db.insert(EasyKitchenContract.Material.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyKitchenContract.Material.buildMaterialUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case EASY_KITCHEN_RECIPE: {
                long _id = db.insert(EasyKitchenContract.Recipe.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyKitchenContract.Recipe.buildRecipeUri(_id);
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
            case EASY_KITCHEN_RECIPE:
                rowsDeleted = db.delete(
                        EasyKitchenContract.Recipe.TABLE_NAME, selection, selectionArgs);
                break;
            case EASY_KITCHEN_RECIPE_WITH_NAME:
                rowsDeleted = DeleteTheRecipeByName(uri);
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
            case EASY_KITCHEN_MATERIAL_WITH_NAME:
                rowsUpdated = UpdateMaterialByName(uri, values);
                break;
            case EASY_KITCHEN_RECIPE:
                rowsUpdated = db.update(EasyKitchenContract.Recipe.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case EASY_KITCHEN_RECIPE_WITH_NAME:
                rowsUpdated = UpdateRecipeByName(uri, values);
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
            case EASY_KITCHEN_RECIPE:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(EasyKitchenContract.Recipe.TABLE_NAME, null, value);
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
