package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class MaterialAdapter extends CursorAdapter {
    public MaterialAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView iView = new ImageView(context);
        iView.setLayoutParams(new GridView.LayoutParams(100, 100));
        Log.v("Adapter", "bindView");

        //taskA++;
        //Log.w("task s", taskA + " count");
        return iView;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        //int pathColumnIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_IMAGE);
        //String path = cursor.getString(pathColumnIndex);
        ImageView image = (ImageView)view;
        image.setImageResource(R.mipmap.ic_launcher);


    }
}
