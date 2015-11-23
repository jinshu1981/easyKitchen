package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class MaterialAdapter extends CursorAdapter {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public MaterialAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.gridview_item_main, parent, false);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView text = (TextView)view.findViewById(R.id.name);

        int textIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);

        int imageIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_IMAGE);

        Log.v(LOG_TAG,"Activity =" + context.getClass().toString());
        if (-1 == cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_STATUS))
        {
            image.setImageResource(Utility.getImagebyName(textString));
        }
        else {
            int statusIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_STATUS);
            Log.v(LOG_TAG,"statusIndex = " + statusIndex + ",name = " + textString);
            String status = cursor.getString(statusIndex);
            image.setImageResource(Utility.getImagebyNameandStatus(textString,status));
        }

    }
}
