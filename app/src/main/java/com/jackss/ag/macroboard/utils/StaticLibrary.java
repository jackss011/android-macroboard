package com.jackss.ag.macroboard.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import java.util.concurrent.ThreadFactory;


/**
 *  Static library for generic methods
 */
public class StaticLibrary
{
    /** Convert dp to px */
    public static int dp2px(float dp)
    {
        return Math.round( Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT * dp );
    }

    /** Return desiredSize if measureSpec allows it, return the value specified by measureSpec otherwise. */
    public static int resolveDesiredMeasure(int measureSpec, int desiredSize)
    {
        final int mode = View.MeasureSpec.getMode(measureSpec);

        if(mode == View.MeasureSpec.UNSPECIFIED)
            return desiredSize;
        else
        {
            int size = View.MeasureSpec.getSize(measureSpec);

            if(mode == View.MeasureSpec.AT_MOST)
                return Math.min(size, desiredSize);
            else
                return size;
        }
    }

    /** Modify the saturation of a color which is converted temporarily to HSV */
    public static int saturateColor(int color, float amount)
    {
        float hsv[] = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * amount;

        return Color.HSVToColor(hsv);
    }

    /** Create a thread factory for network threads */
    public static ThreadFactory buildNetworkThreadFactory()
    {
        return new ThreadFactory()
        {
            @Override
            public Thread newThread(@NonNull Runnable r)
            {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        };
    }

    /** Remove every char after the first dot. Prevent wifi modems to modify socket names. */
    public static String sanitizeHostName(String hostName)
    {
        String res[] = hostName.split("\\.");
        return res.length > 0 ? res[0] : hostName;
    }
}
