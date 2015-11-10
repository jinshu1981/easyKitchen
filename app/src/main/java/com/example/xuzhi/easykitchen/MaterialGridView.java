package com.example.xuzhi.easykitchen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by xuzhi on 2015/11/5.
 */
public class MaterialGridView extends GridView {
    public MaterialGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialGridView(Context context) {
        super(context);
    }

    public MaterialGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
