package io.vamshedhar.coursemanager;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 11/6/17 1:18 PM.
 * vchinta1@uncc.edu
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
        try {
            String inputValue = dest.toString() + source.toString();
            int input = Integer.parseInt(inputValue);
            if (isInRange(min, max, input) && inputValue.length() <= Integer.toString(max).length())
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
