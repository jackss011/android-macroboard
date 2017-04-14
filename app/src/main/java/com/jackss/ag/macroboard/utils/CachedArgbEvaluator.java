package com.jackss.ag.macroboard.utils;

import android.animation.ArgbEvaluator;

/**
 * Same of ArgbEvaluator but this one cache Integer(s) to reduce garbage collection.
 */
public class CachedArgbEvaluator extends ArgbEvaluator
{
    private Integer startValue = 0;
    private Integer endValue = 0;

    public CachedArgbEvaluator(int startValue, int endValue)
    {
        super();

        this.startValue = startValue;
        this.endValue = endValue;
    }

    public int evaluate(float fraction)
    {
        return (int) super.evaluate(fraction, startValue, endValue);
    }
}
