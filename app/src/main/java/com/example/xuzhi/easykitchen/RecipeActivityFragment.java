package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int RECIPE_LOADER = 4;
    private final String LOG_TAG = RecipeActivityFragment.class.getSimpleName();
    private String mRecipeName,mRecipeFavoriteFlag;
    private Context mContext;
    View mRootView;
    public RecipeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        mRootView = rootView;
        mContext = getActivity();
        //set favorite flag
        ImageView imageFavorite = (ImageView)mRootView.findViewById(R.id.image_favorite);
        imageFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mRecipeName == null){
                    return;
                }
                ContentValues recipeValues= new ContentValues();
                String newFavoriteFlag = (mRecipeFavoriteFlag.equals("yes"))?"no":"yes";
                recipeValues.put(EasyKitchenContract.Recipe.COLUMN_FAVORITE, newFavoriteFlag);
                int value = mContext.getContentResolver().update(EasyKitchenContract.Recipe.buildRecipeUriByName(mRecipeName), recipeValues, null, null);
                int imageId = (newFavoriteFlag.equals("yes"))?R.mipmap.temp:R.mipmap.temp_grey;
                ((ImageView) mRootView.findViewById(R.id.image_favorite)).setImageResource(imageId);
                mRecipeFavoriteFlag = newFavoriteFlag;
                Log.v(LOG_TAG,"update value = " + value);
            }
        });
        //set bold text style
        Utility.setBoldTextStyle((TextView) mRootView.findViewById(R.id.title_material));
        Utility.setBoldTextStyle((TextView) mRootView.findViewById(R.id.title_seasoning));
        Utility.setBoldTextStyle((TextView) mRootView.findViewById(R.id.title_step));
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_LOADER, null, this);
        // getLoaderManager().initLoader(MATERIAL_LOADER_DELETE, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = EasyKitchenContract.Recipe.COLUMN_NAME + " ASC";
        /*String materialName = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);*/
        Uri recipeUri= getActivity().getIntent().getData();
        Log.v(LOG_TAG,"recipe uri ="+recipeUri.toString() );
        return new CursorLoader(getActivity(),
                recipeUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor != null && cursor.moveToFirst()) {

            int index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
            String content = cursor.getString(index);
            mRecipeName = content;
            TextView name =  ((TextView) mRootView.findViewById(R.id.name));
            name.setText(content);
            Utility.setBoldTextStyle(name);


            index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
            content = cursor.getString(index);
            ((TextView) mRootView.findViewById(R.id.material))
                    .setText(content);

            index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_SEASONING);
            content = cursor.getString(index);
            ((TextView) mRootView.findViewById(R.id.seasoning))
                    .setText(content);

            index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_STEP);
            content = cursor.getString(index);
            ((TextView) mRootView.findViewById(R.id.step))
                    .setText(content);

            //index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_IMAGE);
            //int imageid = cursor.getInt(index);
            ((ImageView) mRootView.findViewById(R.id.image)).setImageResource(Utility.getImagebyName(content));

            index = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_FAVORITE);
            content = cursor.getString(index);
            mRecipeFavoriteFlag = content;
            Log.v(LOG_TAG,"favorite flag = " + content);
            int imageId = (content.equals("yes") )?R.mipmap.temp:R.mipmap.temp_grey;
            ((ImageView) mRootView.findViewById(R.id.image_favorite)).setImageResource(imageId);

        }
        else
        {
            Log.v(LOG_TAG, "cursor = " + cursor);
        }
        Log.v(LOG_TAG, "onLoadFinished");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {    }
}
