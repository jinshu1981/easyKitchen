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
                if (MainActivityFragment.deletebleTag == true) {
                    String name = materialText.getText().toString();

                    Cursor cursor = mContext.getContentResolver().query(EasyKitchenContract.Material.buildMaterialUriByName(name), null, null, null, null);
                    try{
                            if ((cursor != null)&&(cursor.moveToFirst())){
                            int sourceIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_SOURCE);
                            String source = cursor.getString(sourceIndex);
                            if (source.equals(EasyKitchenContract.DEFAULTS)){
                                ContentValues newValue = new ContentValues();
                                newValue.put(EasyKitchenContract.Material.COLUMN_STATUS, EasyKitchenContract.NO);
                                mContext.getContentResolver().update(EasyKitchenContract.Material.buildMaterialUriByName(name), newValue, null, null);
                            }
                            else
                            {
                                mContext.getContentResolver().delete(EasyKitchenContract.Material.buildMaterialUriByName(name), null, null);
                            }
                            //新增食材，菜谱权重+1
                            Utility.UpdateSingleCursor(mContext, name,1);
                        }
                    }finally{
                        cursor.moveToFirst();
                        cursor.close();
                }
                    materialText.setBackgroundResource(R.color.highlightpink);
                    materialText.setTextColor(mContext.getResources().getColor(R.color.highlightpink));
                }
            }

        });

        if (MainActivityFragment.deletebleTag == true)
        {
            materialText.setBackgroundResource(R.color.lightgray);
        }
        else
        {
            materialText.setBackgroundResource(R.color.white);
        }
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView text = (TextView)view.findViewById(R.id.name);

        int textIndex = cursor.getColumnIndex(EasyKitchenContract.Material.COLUMN_NAME);
        String textString = cursor.getString(textIndex);
        text.setText(textString);

    }
}
