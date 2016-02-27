package com.example.xuzhi.easykitchen.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;

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
    static final int EASY_KITCHEN_MATERIAL_WITH_NAME_LIST = 105;
    static final int EASY_KITCHEN_MATERIAL_WITH_WILL_BUY = 106;

    static final int EASY_KITCHEN_RECIPE = 200;
    static final int EASY_KITCHEN_RECIPE_WITH_NAME = 201;
    static final int EASY_KITCHEN_RECIPE_WITH_MATERIAL = 202;
    static final int EASY_KITCHEN_RECIPE_WITH_ALL_MATERIAL = 203;
    static final int EASY_KITCHEN_RECIPE_WITH_WEIGHT = 204;
    static final int EASY_KITCHEN_RECIPE_WITH_SOURCE = 205;
    static final int EASY_KITCHEN_RECIPE_WITH_FAVORITE = 206;
    static final int EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT = 207;
    static final int EASY_KITCHEN_RECIPE_WITH_ID = 208;
    static final int EASY_KITCHEN_RECIPE_WITH_IDLIST = 209;
    static final int EASY_KITCHEN_RECIPE_WITH_IN_COOKING_LIST_STATUS = 210;

    static final int EASY_KITCHEN_USER_RECIPE_LIST = 300;
    static final int EASY_KITCHEN_USER_RECIPE_LIST_WITH_MENU_ID = 301;

    static final int EASY_KITCHEN_USER_MENU_LIST = 400;
    static final int EASY_KITCHEN_USER_MENU_LIST_WITH_USER_NAME = 401;
    static final int EASY_KITCHEN_USER_MENU_LIST_WITH_ID = 402;

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
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/" + EasyKitchenContract.Material.PATH_MATERIAL_NAME_LIST +"/*", EASY_KITCHEN_MATERIAL_WITH_NAME_LIST);
        matcher.addURI(authority, EasyKitchenContract.PATH_MATERIAL + "/" + EasyKitchenContract.Material.COLUMN_BUY +"/*", EASY_KITCHEN_MATERIAL_WITH_WILL_BUY);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE, EASY_KITCHEN_RECIPE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/material/*", EASY_KITCHEN_RECIPE_WITH_MATERIAL);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/name/*", EASY_KITCHEN_RECIPE_WITH_NAME);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/allMaterials/*", EASY_KITCHEN_RECIPE_WITH_ALL_MATERIAL);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/weight/*", EASY_KITCHEN_RECIPE_WITH_WEIGHT);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/source/*", EASY_KITCHEN_RECIPE_WITH_SOURCE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/favorite", EASY_KITCHEN_RECIPE_WITH_FAVORITE);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/mealType/*/*", EASY_KITCHEN_RECIPE_WITH_TYPE_AND_WEIGHT);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/" + EasyKitchenContract.Recipe.PATH_RECIPE_ID +"/*", EASY_KITCHEN_RECIPE_WITH_ID);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/" + EasyKitchenContract.Recipe.PATH_RECIPE_ID_LIST +"/*", EASY_KITCHEN_RECIPE_WITH_IDLIST);
        matcher.addURI(authority, EasyKitchenContract.PATH_RECIPE + "/inCookingList/*", EASY_KITCHEN_RECIPE_WITH_IN_COOKING_LIST_STATUS);

        matcher.addURI(authority, EasyKitchenContract.PATH_USER_RECIPE_LIST, EASY_KITCHEN_USER_RECIPE_LIST);
        matcher.addURI(authority, EasyKitchenContract.PATH_USER_RECIPE_LIST + "/" + EasyKitchenContract.UsersRecipeList.COLUMN_MENU_ID +"/*", EASY_KITCHEN_USER_RECIPE_LIST_WITH_MENU_ID);

        matcher.addURI(authority, EasyKitchenContract.PATH_USER_MENU_LIST, EASY_KITCHEN_USER_MENU_LIST);
        matcher.addURI(authority, EasyKitchenContract.PATH_USER_MENU_LIST + "/" + EasyKitchenContract.UsersMenuList.COLUMN_USER_NAME +"/*", EASY_KITCHEN_USER_MENU_LIST_WITH_USER_NAME);
        matcher.addURI(authority,  EasyKitchenContract.PATH_USER_MENU_LIST + "/" + EasyKitchenContract.UsersMenuList.COLUMN_ID +"/*", EASY_KITCHEN_USER_MENU_LIST_WITH_ID);
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
            case EASY_KITCHEN_MATERIAL_WITH_NAME_LIST:
            case EASY_KITCHEN_MATERIAL_WITH_WILL_BUY:
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
            case EASY_KITCHEN_RECIPE_WITH_NAME:
            case EASY_KITCHEN_RECIPE_WITH_IDLIST:
            case EASY_KITCHEN_RECIPE_WITH_IN_COOKING_LIST_STATUS:
                return EasyKitchenContract.Recipe.CONTENT_TYPE;
            case EASY_KITCHEN_RECIPE_WITH_ID:
                return EasyKitchenContract.Recipe.CONTENT_ITEM_TYPE;

            case EASY_KITCHEN_USER_RECIPE_LIST:
            case EASY_KITCHEN_USER_RECIPE_LIST_WITH_MENU_ID:
                return EasyKitchenContract.UsersRecipeList.CONTENT_TYPE;

            case EASY_KITCHEN_USER_MENU_LIST:
            case EASY_KITCHEN_USER_MENU_LIST_WITH_USER_NAME:
            case EASY_KITCHEN_USER_MENU_LIST_WITH_ID:
                return EasyKitchenContract.UsersMenuList.CONTENT_TYPE;
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

        //Material.name IN (?,?,?...?)
        private static final String sEasyKitchenByMaterialNameListSelection =
                EasyKitchenContract.Material.TABLE_NAME +
                        "." + EasyKitchenContract.Material.COLUMN_NAME + " IN ";

    //Material.buy = ?
    private static final String sEasyKitchenByMaterialWillBuySelection=
            EasyKitchenContract.Material.TABLE_NAME +
                    "." + EasyKitchenContract.Material.COLUMN_BUY + " = ? ";

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
    private int DeleteTheMaterialByName(Uri uri) {

        String name = EasyKitchenContract.Material.getNameFromUri(uri);
        Log.v(LOG_TAG, "name = " + name);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return  db.delete(
                EasyKitchenContract.Material.TABLE_NAME, sEasyKitchenByMaterialNameSelection, new String[]{name});

    }

    private Cursor getMaterialByNameList(
            Uri uri, String[] projection, String sortOrder) {

        String nameString = EasyKitchenContract.Material.getNameFromUri(uri).trim();
        String[] nameArray = nameString.split(",");
        sEasyKitchenQueryBuilder.setTables(EasyKitchenContract.Material.TABLE_NAME);
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialNameListSelection + "(" + TextUtils.join(",", Collections.nCopies(nameArray.length, "?")) + ")",/*generate (?,?,?)*/
                nameArray,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMaterialByWillBuy(
            Uri uri, String[] projection, String sortOrder) {

        String willBuy = EasyKitchenContract.Material.getNameFromUri(uri);
        Log.v(LOG_TAG, "willBuy = " + willBuy);
        sEasyKitchenQueryBuilder.setTables(EasyKitchenContract.Material.TABLE_NAME);
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMaterialWillBuySelection,
                new String[]{willBuy},
                null,
                null,
                sortOrder
        );
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

    //Recipe.weight <= ?
    private static final String sEasyKitchenByRecipeWeightSelection =
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_WEIGHT + " <= ?";
    //recipe.mealType LIKE ? AND weight <= ?
    private static final String sEasyKitchenByRecipeTypeAndWeightSelection =
            EasyKitchenContract.Recipe.TABLE_NAME +
                    "." + EasyKitchenContract.Recipe.COLUMN_MEAL_TYPE + " LIKE ? AND " +
                    EasyKitchenContract.Recipe.COLUMN_WEIGHT + " <= ? ";
    //recipe._id = ?
    private static final String sEasyKitchenByRecipeIdSelection =
            EasyKitchenContract.Recipe.TABLE_NAME +
                    "." + EasyKitchenContract.Recipe.COLUMN_ID + " = ? ";

    //recipe._id IN (?,?,?...?)
    private static final String sEasyKitchenByRecipeIdListSelection =
            EasyKitchenContract.Recipe.TABLE_NAME +
                    "." + EasyKitchenContract.Recipe.COLUMN_ID + " IN ";
    //Recipe.inCookingList = ?
    private static final String sEasyKitchenByRecipeInCookingListSelection=
            EasyKitchenContract.Recipe.TABLE_NAME+
                    "." + EasyKitchenContract.Recipe.COLUMN_IN_COOKING_LIST + " = ?";

    private Cursor getAllRecipes(
            Uri uri, String[] projection, String sortOrder) {
        sEasyKitchenQueryBuilder.setTables("recipe");
        return mOpenHelper.getReadableDatabase().query(
                EasyKitchenContract.Recipe.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getRecipeByMatchMaterial(
            Uri uri, String[] projection, String sortOrder) {

        String material = EasyKitchenContract.Recipe.getMaterialFromUri(uri);
        Log.v(LOG_TAG,"material = " + material);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeMaterialMatchSelection,
                new String[]{"%" + material + "%"},
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
    private Cursor getRecipeById(
            Uri uri, String[] projection, String sortOrder) {

        String id = EasyKitchenContract.Recipe.getIdFromUri(uri);
        Log.v(LOG_TAG,"id = " + id);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeIdSelection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }
    private Cursor getRecipeByIdList(
            Uri uri, String[] projection, String sortOrder) {

        String idString = EasyKitchenContract.Recipe.getIdFromUri(uri).trim();
        String[] idArray = idString.split(",");
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeIdListSelection + "("+ TextUtils.join(",", Collections.nCopies(idArray.length, "?")) + ")",/*generate (?,?,?)*/
                idArray,
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
    private Cursor getRecipeByInCookingListStatus(
            Uri uri, String[] projection, String sortOrder) {

        String status = EasyKitchenContract.Recipe.getSourceFromUri(uri);
        Log.v(LOG_TAG,"in cooking list status = " + status);
        sEasyKitchenQueryBuilder.setTables("recipe");
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeInCookingListSelection,
                new String[]{status},
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
    private int UpdateRecipeById(Uri uri, ContentValues values) {

        String id = EasyKitchenContract.Recipe.getIdFromUri(uri);
        Log.v(LOG_TAG, "id = " + id);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(EasyKitchenContract.Recipe.TABLE_NAME, values, sEasyKitchenByRecipeIdSelection,
                new String[]{id});

    }

    private int DeleteTheRecipeByName(Uri uri) {

        String name = EasyKitchenContract.Recipe.getNameFromUri(uri);
        Log.v(LOG_TAG, "name = " + name);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return  db.delete(
                EasyKitchenContract.Recipe.TABLE_NAME, sEasyKitchenByRecipeNameSelection, new String[]{name});

    }
    private int DeleteTheRecipeById(Uri uri) {

        String id = EasyKitchenContract.Recipe.getIdFromUri(uri);
        Log.v(LOG_TAG, "id = " + id);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return  db.delete(
                EasyKitchenContract.Recipe.TABLE_NAME, sEasyKitchenByRecipeIdSelection, new String[]{id});

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
                new String[]{"%" + mealType + "%", weight},
                null,
                null,
                sortOrder
        );
    }

/**************************************User Recipe Lists Table**********************************************/
//user_recipe_list.menu_id = ?
private static final String sEasyKitchenByRecipeMenuIdSelection =
        EasyKitchenContract.UsersRecipeList.TABLE_NAME+
                "." + EasyKitchenContract.UsersRecipeList.COLUMN_MENU_ID + " = ?";

    private Cursor getAllMyRecipeList(
            Uri uri, String[] projection, String sortOrder) {
        sEasyKitchenQueryBuilder.setTables(EasyKitchenContract.UsersRecipeList.TABLE_NAME);
        return mOpenHelper.getReadableDatabase().query(
                EasyKitchenContract.UsersRecipeList.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);
    }

    private Cursor getRecipeListByMenuId(
            Uri uri, String[] projection, String sortOrder) {

        String menuId = EasyKitchenContract.UsersRecipeList.getMenuIdFromUri(uri);
        Log.v(LOG_TAG,"menuId = " + menuId);
        sEasyKitchenQueryBuilder.setTables(EasyKitchenContract.UsersRecipeList.TABLE_NAME+ " INNER JOIN " +
                EasyKitchenContract.Recipe.TABLE_NAME +
                " ON " + EasyKitchenContract.UsersRecipeList.TABLE_NAME +
                "." + EasyKitchenContract.UsersRecipeList.COLUMN_RECIPE_ID +
                " = " + EasyKitchenContract.Recipe.TABLE_NAME +
                "." + EasyKitchenContract.Recipe.COLUMN_ID);
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByRecipeMenuIdSelection,
                new String[]{menuId},
                null,
                null,
                sortOrder
        );
    }
    /**************************************User Menu Lists Table**********************************************/
    //user_menu_list.user_name = ?
    private static final String sEasyKitchenByMenuUserNameSelection =
            EasyKitchenContract.UsersMenuList.TABLE_NAME+
                    "." + EasyKitchenContract.UsersMenuList.COLUMN_USER_NAME + " = ?";

    //user_menu_list._id = ?
    private static final String sEasyKitchenByUserMenuListIdSelection =
            EasyKitchenContract.UsersMenuList.TABLE_NAME +
                    "." + EasyKitchenContract.UsersMenuList.COLUMN_ID + " = ? ";

    private Cursor getMenuListByUserName(
            Uri uri, String[] projection, String sortOrder) {

        String name = EasyKitchenContract.Recipe.getNameFromUri(uri);
        Log.v(LOG_TAG,"username = " + name);
        sEasyKitchenQueryBuilder.setTables(EasyKitchenContract.UsersMenuList.TABLE_NAME);
        return sEasyKitchenQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEasyKitchenByMenuUserNameSelection,
                new String[]{name},
                null,
                null,
                sortOrder
        );
    }

    private int DeleteTheUsersMenuListById(Uri uri) {
        String id = EasyKitchenContract.Recipe.getIdFromUri(uri);
        Log.v(LOG_TAG, "id = " + id);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return  db.delete(
                EasyKitchenContract.UsersMenuList.TABLE_NAME, sEasyKitchenByUserMenuListIdSelection, new String[]{id});

    }

    private int UpdateUserMenuListById(Uri uri, ContentValues values) {

        String id = EasyKitchenContract.Recipe.getIdFromUri(uri);
        Log.v(LOG_TAG, "id = " + id);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db.update(EasyKitchenContract.UsersMenuList.TABLE_NAME, values, sEasyKitchenByUserMenuListIdSelection,
                new String[]{id});

    }
    /*****************************************************************************************************************/
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
            case EASY_KITCHEN_MATERIAL_WITH_NAME_LIST: {
                retCursor = getMaterialByNameList(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_MATERIAL_WITH_WILL_BUY: {
                retCursor = getMaterialByWillBuy(uri, projection, sortOrder);
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
            case EASY_KITCHEN_RECIPE_WITH_ID: {
                retCursor = getRecipeById(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_IDLIST: {
                retCursor = getRecipeByIdList(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE: {
                retCursor = getAllRecipes(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_RECIPE_WITH_IN_COOKING_LIST_STATUS: {
                retCursor = getRecipeByInCookingListStatus(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_USER_RECIPE_LIST: {
                retCursor = getAllMyRecipeList(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_USER_RECIPE_LIST_WITH_MENU_ID: {
                retCursor = getRecipeListByMenuId(uri, projection, sortOrder);
                break;
            }
            case EASY_KITCHEN_USER_MENU_LIST_WITH_USER_NAME: {
                retCursor = getMenuListByUserName(uri, projection, sortOrder);
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
            case EASY_KITCHEN_USER_RECIPE_LIST: {
                long _id = db.insert(EasyKitchenContract.UsersRecipeList.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyKitchenContract.UsersRecipeList.buildUserRecipeListUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case EASY_KITCHEN_USER_MENU_LIST: {
                long _id = db.insert(EasyKitchenContract.UsersMenuList.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EasyKitchenContract.UsersMenuList.buildUserMenuListUri(_id);
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
            case EASY_KITCHEN_RECIPE_WITH_ID:
                rowsDeleted = DeleteTheRecipeById(uri);
                break;
            case EASY_KITCHEN_MATERIAL_WITH_NAME:
                rowsDeleted = DeleteTheMaterialByName(uri);
                break;

            case EASY_KITCHEN_USER_MENU_LIST_WITH_ID:
                rowsDeleted = DeleteTheUsersMenuListById(uri);
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
            case EASY_KITCHEN_RECIPE_WITH_ID:
                rowsUpdated = UpdateRecipeById(uri, values);
                break;
            case EASY_KITCHEN_USER_MENU_LIST_WITH_ID:
                rowsUpdated = UpdateUserMenuListById(uri, values);
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
            case EASY_KITCHEN_USER_RECIPE_LIST:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(EasyKitchenContract.UsersRecipeList.TABLE_NAME, null, value);
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
