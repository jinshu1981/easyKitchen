package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xuzhi.easykitchen.data.EasyKitchenContract;

/**
 * Created by xuzhi on 2016/1/27.
 */
public class RecipeAttributesAdapter extends BaseAdapter {
    private final String LOG_TAG = this.getClass().getSimpleName();
    Cursor mCursor;
    Context mContext;
    public RecipeAttributesAdapter(Context context, Cursor c, int flags) {
        mCursor = c;
        mContext = context;
    }
    private static int tagIndex = 0;
    public int getCount() {
        return 6;
    }
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    // create a new View for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        final View view = inflater.inflate(R.layout.attribute_grid_item, parent, false);
        //view.setBackgroundColor(mContext.getResources().getColor(R.color.lightgray));
        TextView desc_begin = (TextView) view.findViewById(R.id.desc_begin);
        TextView attribute_value = (TextView) view.findViewById(R.id.attribute_value);
        TextView desc_end = (TextView) view.findViewById(R.id.desc_end);
        int textIndex,value;
        String textString;
        switch(position)
        {
            case 0:
                desc_begin.setText(R.string.recipes_timeConsuming_begin);
                desc_end.setText(R.string.recipes_timeConsuming_end);

                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_TIME_CONSUMING);
                value = mCursor.getInt(textIndex);
                attribute_value.setText(Integer.toString(value));
                break;

            case 1:
                desc_begin.setText(R.string.recipes_difficulty);

                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_DIFFICULTY);
                textString = mCursor.getString(textIndex);
                attribute_value.setText(textString);
                break;
            case 2:
                desc_begin.setText(R.string.recipes_taste);

                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_TASTE);                ;
                textString = mCursor.getString(textIndex);
                attribute_value.setText(textString);

                break;
            case 3:
                desc_begin.setText(R.string.recipes_popularity_begin);
                desc_end.setText(R.string.recipes_popularity_end);

                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_POPULARITY);
                value = mCursor.getInt(textIndex);
                attribute_value.setText(Integer.toString(value));
                break;

            case 4:
                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_EATEN);
                textString = mCursor.getString(textIndex);
                if (textString.equals(EasyKitchenContract.NO)) {
                    attribute_value.setText(R.string.recipes_eaten_no_default_value);
                }
                else
                {
                    attribute_value.setText(R.string.recipes_eaten_yes);
                }
                break;

            case 5:
                textIndex = mCursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_FAVORITE);
                textString = mCursor.getString(textIndex);
                if (textString.equals(EasyKitchenContract.NO)) {
                    attribute_value.setText("未收藏");
                }
                else
                {
                    attribute_value.setText("已收藏");
                }
                break;
            default:
                break;
        }
        return view;
    }
/*
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.attribute_grid_item, parent, false);
        view.setTag((tagIndex++) % 6);//up to six items
        Log.v(LOG_TAG,"tagIndex =" + tagIndex);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    /*@Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView desc_begin = (TextView) view.findViewById(R.id.desc_begin);
        TextView attribute_value = (TextView) view.findViewById(R.id.attribute_value);
        TextView desc_end = (TextView) view.findViewById(R.id.desc_end);
        int textIndex,value;
        String textString;
        switch((int)view.getTag())
        {
            case 0:
                desc_begin.setText(R.string.recipes_timeConsuming_begin);
                desc_end.setText(R.string.recipes_timeConsuming_end);

                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_TIME_CONSUMING);
                value = cursor.getInt(textIndex);
                attribute_value.setText(Integer.toString(value));
                break;

            case 1:
                desc_begin.setText(R.string.recipes_difficulty);

                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_DIFFICULTY);
                value = cursor.getInt(textIndex);
                attribute_value.setText(Integer.toString(value));
                break;
            case 2:
                desc_begin.setText(R.string.recipes_taste);

                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_TASTE);                ;
                textString = cursor.getString(textIndex);
                attribute_value.setText(textString);

                break;
            case 3:
                desc_begin.setText(R.string.recipes_popularity_begin);
                desc_end.setText(R.string.recipes_popularity_end);

                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_POPULARITY);
                value = cursor.getInt(textIndex);
                attribute_value.setText(Integer.toString(value));
                break;

            case 4:
                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_EATEN);
                textString = cursor.getString(textIndex);
                attribute_value.setText(textString);
                break;

            case 5:
                textIndex = cursor.getColumnIndex(EasyKitchenContract.Recipe.COLUMN_EATEN);
                textString = cursor.getString(textIndex);
                attribute_value.setText(textString);
                break;
            default:
                break;
        }
   }*/

}
