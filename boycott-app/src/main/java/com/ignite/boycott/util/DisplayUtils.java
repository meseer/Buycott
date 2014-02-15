package com.ignite.boycott.util;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by meseer on 15.02.14.
 */
public class DisplayUtils {
    public static int dpToPx(int dp, DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
