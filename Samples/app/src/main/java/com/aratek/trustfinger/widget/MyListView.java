package com.aratek.trustfinger.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by hecl on 2018/9/23.
 */
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int maxHeight = 240;

    public void setmaxHeight(int maxHeight){
        this.maxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        int measureSpec1 = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, measureSpec1);
    }
}
