package com.jackss.ag.macroboard.utils;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


/**
 * Utility class used to increase and decrease the elevation of a view at runtime.
 *
 * Main methods: raise(), drop(), cancel().
 *
 * The target view that will be modified is passed in the constructor,  along with the elevation multiplier.
 */
public class ViewLifter
{
    private final View view;

    private float defaultElevation;
    private float raisedElevation;

    private ValueAnimator elevationAnimator = ValueAnimator.ofFloat();

    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener()
    {
        @Override
        public void onAnimationUpdate(ValueAnimator animation)
        {
            if(animation != elevationAnimator) return;

            view.setElevation((float) elevationAnimator.getAnimatedValue());
        }
    };


    public ViewLifter(@NonNull View view)
    {
        this(view, 1);
    }

    public ViewLifter(@NonNull View view, float elevationMultiplier)
    {
        this.view = view;

        defaultElevation = view.getElevation();
        raisedElevation = elevationMultiplier * defaultElevation;

        elevationAnimator.addUpdateListener(mUpdateListener);
        elevationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        elevationAnimator.setDuration(150);
    }

    public ValueAnimator getElevationAnimator()
    {
        return elevationAnimator;
    }

    public void raise()
    {
        elevationAnimator.setFloatValues(view.getElevation(), raisedElevation);
        elevationAnimator.start();
    }

    public void drop()
    {
        elevationAnimator.setFloatValues(view.getElevation(), defaultElevation);
        elevationAnimator.start();
    }

    public void cancel()
    {
        elevationAnimator.cancel();
        view.setElevation(defaultElevation);
    }
}
