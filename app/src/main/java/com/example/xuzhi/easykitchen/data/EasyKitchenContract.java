package com.example.xuzhi.easykitchen.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

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
    public static final String PATH_CUSTOM_RECIPE = "CustomRecipe";

    public static final class Material implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATERIAL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATERIAL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATERIAL;

        public static final String TABLE_NAME = "material";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IMAGE_GREY = "image_grey";
        public static final String COLUMN_STATUS = "status";

        static public String MATERIAL_TYPE_VEGETABLE = "素";
        static public String MATERIAL_TYPE_MEAT = "荤";
        static public String MATERIAL_TYPE_SEASONING = "调";

        public static Uri buildMaterialUriByName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_NAME).appendPath(name).build();
        }
        public static Uri buildMaterialUriByStatus(String status)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_STATUS).appendPath(status).build();
        }

        public static Uri buildMaterialUriByType(String type,String status)
        {
            //return CONTENT_URI.buildUpon().appendPath(type).build();
            return CONTENT_URI.buildUpon().appendPath(COLUMN_TYPE).appendPath(type).appendPath(status).build();
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
    }

    public static final class Recipe implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RECIPE;

        public static final String TABLE_NAME = "recipe";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MATERIAL = "material";
        public static final String COLUMN_STEP = "step";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_WEIGHT  ="weight";
        public static final String COLUMN_SOURCE  ="source";
        public static final String COLUMN_FAVORITE  ="favorite";

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
        public static String getWeightFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static String getSourceFromUri(Uri uri) {
            return getMaterialFromUri(uri);
        }
        public static Uri buildRecipeUriByMoreThanOneMaterials() {
            return CONTENT_URI.buildUpon().appendPath("allMaterials").build();
        }
        public static Uri buildRecipeUriByName(String name)
        {
            return CONTENT_URI.buildUpon().appendPath("name").appendPath(name).build();
        }
        public static Uri buildRecipeUriByWeight(int weight)
        {
            return CONTENT_URI.buildUpon().appendPath("weight").appendPath(Integer.toString(weight)).build();
        }
        public static Uri buildRecipeUriBySource(String source)
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_SOURCE).appendPath(source).build();
        }
        public static Uri buildRecipeUriByFavorite()
        {
            return CONTENT_URI.buildUpon().appendPath(COLUMN_FAVORITE).build();
        }
    }


}
