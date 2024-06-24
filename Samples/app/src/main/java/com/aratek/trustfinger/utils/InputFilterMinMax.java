package com.aratek.trustfinger.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * Created by hecl on 2018/9/19.
 */

public class InputFilterMinMax implements InputFilter {
    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.e("", "source  ==" + source + "  start=====" + start + "   end======" + end + "   dest====" + dest + "  dstart===" + dstart + "  dend==" + dend);
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            Log.e("", "min" + min);
            Log.e("", "max" + max);
            Log.e("", "input" + input);
            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isInRange(int min, int max, int input) {
        return max > min ? input >= min && input <= max : input >= max && input <= min;
    }

}
