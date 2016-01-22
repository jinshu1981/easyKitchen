package com.example.xuzhi.easykitchen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        final TextView materialText = (TextView)view.findViewById(R.id.name);
        materialText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MaterialActivityFragment.deletebleTag == true) {
                    String name = materialText.getText().toString();

                    Cursor cursor = mContext.getContentResolver().query(EasyKitchenContract.Material.buildMaterialUriByName(name), null, null, null, null);
                    try{
                            if ((cursor != null)&&(cursor.moveToFirst())){
                            int sourceIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_SOURCE);
                            String source = cursor.getString(sourceIndex);
                            /*内置食材改变状态，用户自定义食材删除*/
                            if (source.equals(EasyKitchenContract.DEFAULTS)){
                                ContentValues newValue = new ContentValues();
                                newValue.put(EasyKitchenContract.Material.COLUMN_STATUS, EasyKitchenContract.NO);
                                mContext.getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(name), newValue, null, null);
                            }
                            else
                            {
                                mContext.getContentResolver().delete(EasyKitchenContract.Material.buildMaterialUriByName(name), null, null);
                            }
                            //删除食材，相关菜谱权重+1
                            Utility.UpdateSingleCursor(mContext, name,1);
                        }
                    }finally{
                        cursor.moveToFirst();
                        cursor.close();
                }
                    materialText.setAlpha((float)0.5);
                }
            }

        });

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView text = (TextView)view.findViewById(R.id.name);

        int textIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);

    }
}
