package com.example.xuzhi.easykitchen.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class EasyKitchenContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.xuzhi.easykitchen";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MATERIAL = "Material";
    public static final String PATH_RECIPE = "Recipe";
    public static final String PATH_USER_RECIPE_LIST = "UsersRecipeList";
    public static final String PATH_USER_MENU_LIST = "UsersMenuList";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String DEFAULTS = "d";
    public static final String CUSTOMISED = "c";


    public static final class Material implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATERIAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATERIAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATERIAL;

        public static final String TABLE_NAME = "material";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IMAGE_GREY = "image_grey";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_SOURCE  ="source";
        public static final String COLUMN_BUY  ="buy";

        static public final String MATERIAL_TYPE_VEGETABLE = "素";
        static public final String MATERIAL_TYPE_MEAT = "荤";
        static public final String MATERIAL_TYPE_SEASONING = "调";
        static public final String PATH_MATERIAL_NAME_LIST = "nameList";


        public static Uri buildMaterialUriByName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_NAME).appendPath(name).build();
        }
        public static Uri buildMaterialUriByNameList(String[] nameList)
        {
            String nameString = "";
            for (int i = 0;i < nameList.length;i++)
            {
                nameString = nameString + nameList[i] + ",";
            }
            /*去掉尾字符*/
            nameString = nameString.substring(0,nameString.length()-1);
            Log.v("material","nameString = " + nameString);
            return CONTENT_URI.buildUpon().appendPath(PATH_MATERIAL_NAME_LIST).appendPath(nameString).build();
        }
        public static Uri buildMaterialUriByStatus(String status)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_STATUS).appendPath(status).build();
        }

        public static Uri buildMaterialUriByTypeAndStatus(String type,String status)
        {
            //return CONTENT_URI.buildUpon().appendPath(type).build();
            return CONTENT_URI.buildUpon().appendPath(COLUMN_TYPE).appendPath(type).appendPath(status).build();
        }
        public static Uri buildMaterialUriByWillBuy(String willBuy)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_BUY).appendPath(willBuy).build();
        }
        public static String getTypeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getStatusFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
        public static String getStatusWithoutTypeFromUri(Uri uri) {
            return getTypeFromUri(uri);
        }
        public static String getNameFromUri(Uri uri) {
            return getTypeFromUri(uri);
        }
        public static Uri buildMaterialUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildAllMaterialUri() {
            return CONTENT_URI;
        }
    }

    public static final class Recipe implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MATERIAL = "material";
        public static final String COLUMN_SEASONING = "seasoning";
        public static final String COLUMN_STEP = "step";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_WEIGHT  ="weight";
        public static final String COLUMN_SOURCE  ="source";
        public static final String COLUMN_FAVORITE  ="favorite";
        public static final String COLUMN_MEAL_TYPE  ="mealType";
        public static final String COLUMN_AUTHOR  ="author";
        public static final String COLUMN_TIME_CONSUMING  ="timeConsuming";
        public static final String COLUMN_DIFFICULTY  ="difficulty";
        public static final String COLUMN_DIFFICULTY_LEVEL  ="difficulty_level";
        public static final String COLUMN_POPULARITY  ="popularity";
        public static final String COLUMN_EATEN  ="eaten";
        public static final String COLUMN_TASTE  ="taste";
        public static final String COLUMN_IN_COOKING_LIST  ="inCookingList";
        public static final String MEAL_TYPE_BREAKFAST = "B";
        public static final String MEAL_TYPE_LUNCH = "L";
        public static final String MEAL_TYPE_SUPPER = "S";
        public static final String MEAL_TYPE_ALL = "BLS";

        public static final String DIFFICULTY_TYPE_EASY = "简单";
        public static final String DIFFICULTY_TYPE_MIDIUM = "中等";
        public static final String DIFFICULTY_TYPE_HARD = "困难";
        public static final String PATH_RECIPE_ID_LIST = "idList";
        public static final String PATH_RECIPE_ID = "id";

        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildRecipeUriByMaterialName(String materialName) {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_MATERIAL).appendPath(materialName).build();
        }
        public static String getMaterialFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getNameFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static String getWeightFromUri(Uri uri) {return getMaterialFromUri(uri);   }
        public static String getSourceFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static String getMealTypeFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static String getIdFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static String getWeightFromUriWithMealType(Uri uri) {
            return uri.getPathSegments().get(3);
        }
        public static Uri buildRecipeUriByMoreThanOneMaterials() {
            return CONTENT_URI.buildUpon().appendPath("allMaterials").build();
        }
        public static Uri buildRecipeUriByName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_NAME).appendPath(name).build();
        }
        public static Uri buildRecipeUriById(int id)
        {
            return CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_ID).appendPath(Integer.toString(id)).build();
        }
        public static Uri buildRecipeUriByIdList(int[] idList)
        {
            String idString = "";
           for (int i = 0;i < idList.length;i++)
           {
               idString = idString + idList[i] + ",";
           }
            /*去掉尾字符*/
            idString = idString.substring(0,idString.length()-1);
            Log.v("Id","idString = " + idString);
            return CONTENT_URI.buildUpon().appendPath(PATH_RECIPE_ID_LIST).appendPath(idString).build();
        }
        public static Uri buildRecipeUriByWeight(int weight)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_WEIGHT).appendPath(Integer.toString(weight)).build();
        }
        public static Uri buildRecipeUriBySource(String source)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_SOURCE).appendPath(source).build();
        }
        public static Uri buildRecipeUriByFavorite()
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_FAVORITE).build();
        }
        public static Uri buildRecipeUriByInCookingListStatus(String inCookingList)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_IN_COOKING_LIST).appendPath(inCookingList).build();
        }
        public static Uri buildRecipeUriByMealTypeAndWeight(String mealType,int weight)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_MEAL_TYPE).appendPath(mealType).appendPath(Integer.toString(weight)).build();
        }
    }
    public static final class UsersRecipeList implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_RECIPE_LIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_RECIPE_LIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_RECIPE_LIST;

        public static final String TABLE_NAME = "user_recipe_list";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_MENU_ID = "menu_id";
        public static final String COLUMN_USER_NAME = "user_name";

        public static Uri buildUserRecipeListUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildRecipeUriByUserName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_USER_NAME).appendPath(name).build();
        }
        public static Uri buildRecipeUriByMenuId(int id)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_MENU_ID).appendPath(Integer.toString(id)).build();
        }

        public static String getMenuIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class UsersMenuList implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER_MENU_LIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_MENU_LIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER_MENU_LIST;

        public static final String TABLE_NAME = "users_menu_lists";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_MENU_NAME = "name";
        public static final String COLUMN_MENU_TIME = "time";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_RECIPE_ID = "recipe_id";

        public static Uri buildUserMenuListUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMenuUriByUserName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_USER_NAME).appendPath(name).build();
        }

        public static Uri buildUserMenuUriById(int id)
        {
            return CONTENT_URI.buildUpon().appendPath(UsersMenuList.COLUMN_ID).appendPath(Integer.toString(id)).build();
        }
    }
}
