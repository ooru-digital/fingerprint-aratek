package com.aratek.trustfinger.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

public class FingerView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "FingerView";
    private static final int STATE_NORMAL = 0;
    private static final int STATE_SELECTED = 1;
    private static final int STATE_REGISTED = 2;
    private static final int STATE_SELECTED_AND_REGISTED = 3;

    private boolean mSelected = false;
    private boolean mEnrolled = false;
    private boolean mReading = false;

    /*
     * 0:noraml progress_1:selected progress_2:registed progress_3:selected & registed 4:reading
     */
    private Drawable mDrawables[] = new Drawable[5];
    // private int mHeights[] = new int[5];

    public FingerView(Context context) {
        super(context);
        Log.i(TAG, "FingerView(Context context)");
        for (int i = 0; i < mDrawables.length; i++) {
            mDrawables[i] = getDrawable();
        }
    }

    public FingerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "FingerView(Context context, AttributeSet attrs)");
        for (int i = 0; i < mDrawables.length; i++) {
            mDrawables[i] = getDrawable();
        }
    }

    public FingerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i(TAG,
                "FingerView(Context context, AttributeSet attrs, int defStyle)");
        for (int i = 0; i < mDrawables.length; i++) {
            mDrawables[i] = getDrawable();
        }
    }

//    @Override
//    public boolean performClick() {
//        setSelected(!mSelected);
//        return super.performClick();
//    }

    @Override
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        mSelected = isSelected;
        updateView();
    }

    public void setEnrolled(boolean isEnrolled) {
        mEnrolled = isEnrolled;
        updateView();
    }

    public boolean isEnrolled(){
    	return mEnrolled;
    }
    public void setReading(boolean isReading) {
        mReading = isReading;
        updateView();
    }

    private void updateView() {
        Drawable d;
        if (mSelected) {
            if (mEnrolled) {
                d = mDrawables[STATE_SELECTED_AND_REGISTED];
            } else {
                d = mDrawables[STATE_SELECTED];
            }
        } else {
            if (mEnrolled) {
                d = mDrawables[STATE_REGISTED];
            } else {
                d = mDrawables[STATE_NORMAL];
            }
        }
        setImageDrawable(d);
    }

    public void setImageDrawables(Drawable noraml, Drawable selected, Drawable registed, Drawable selectedAndRegisted) {
        mDrawables[STATE_NORMAL] = noraml;
        mDrawables[STATE_SELECTED] = selected;
        mDrawables[STATE_REGISTED] = registed;
        mDrawables[STATE_SELECTED_AND_REGISTED] = selectedAndRegisted;
    }

    public void setImageResources(int noraml, int selected, int registed, int selectedAndRegisted) {
        Resources res = getResources();
        setImageDrawables(res.getDrawable(noraml), res.getDrawable(selected),
                 res.getDrawable(registed),
                res.getDrawable(selectedAndRegisted));
    }

}
