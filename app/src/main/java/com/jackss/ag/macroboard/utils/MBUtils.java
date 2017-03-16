package com.jackss.ag.macroboard.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 *  Static library for generic methods
 */
public class MBUtils
{
    /** Convert dp to px */
    static int dp2px(float dp)
    {
        return Math.round( Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT * dp );
    }
}
