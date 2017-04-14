package com.jackss.ag.macroboard.utils;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private boolean isRaising;

    private ValueAnimator elevationAnimator = ValueAnimator.ofFloat();

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener()
    {
        @Override
        public void onAnimationUpdate(ValueAnimator animation)
        {
            if(animation != elevationAnimator) return;

            view.setElevation((float) elevationAnimator.getAnimatedValue());

            if(lifterUpdateListener != null)
            {
                float fraction = elevationAnimator.getAnimatedFraction();
                if(!isRaising) fraction = 1.f - fraction;

                lifterUpdateListener.onLifterUpdate(fraction);
            }
        }
    };

    public interface OnLifterUpdateListener
    {
        void onLifterUpdate(float fraction);

        void onLifterCancel();
    }

    private OnLifterUpdateListener lifterUpdateListener;


    public ViewLifter(@NonNull View view)
    {
        this(view, 1);
    }

    public ViewLifter(@NonNull View view, float elevationMultiplier)
    {
        this.view = view;

        defaultElevation = view.getElevation();
        raisedElevation = elevationMultiplier * defaultElevation;

        elevationAnimator.addUpdateListener(animatorUpdateListener);
        elevationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        elevationAnimator.setDuration(150);
    }

    public ValueAnimator getElevationAnimator()
    {
        return elevationAnimator;
    }

    /**
     * Set a listener listening for raise, drop updates and cancel.
     * Can be used in the view to play visual effects (ex. background color change)
     * */
    public ViewLifter setLifterUpdateListener(@Nullable OnLifterUpdateListener listener)
    {
        lifterUpdateListener = listener;
        return this;
    }


    public void raise()
    {
        isRaising = true;
        elevationAnimator.setFloatValues(view.getElevation(), raisedElevation);
        elevationAnimator.start();
    }

    public void drop()
    {
        isRaising = false;
        elevationAnimator.setFloatValues(view.getElevation(), defaultElevation);
        elevationAnimator.start();
    }

    public void cancel()
    {
        elevationAnimator.cancel();
        view.setElevation(defaultElevation);

        if(lifterUpdateListener != null) lifterUpdateListener.onLifterCancel();
    }
}
