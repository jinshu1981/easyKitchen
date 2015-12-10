package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * Created by xuzhi on 2015/11/4.
 */
public class MenuAdapter extends CursorAdapter {
    private final String LOG_TAG = this.getClass().getSimpleName();
    public MenuAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.menu_list_item, parent, false);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView text = (TextView)view.findViewById(R.id.name);
        TextView textMaterial = (TextView)view.findViewById(R.id.material);

        int textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);
        Utility.setBoldTextStyle(text);

        image.setImageResource(Utility.getImagebyName(textString));

        textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_MATERIAL);
        textString = cursor.getString(textIndex);
        if (textString.length() > 12)
        textString = textString.substring(0, 12)+"...";
        textMaterial.setText(textString);

    }
}
