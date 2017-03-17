package com.jackss.ag.macroboard.utils;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.support.annotation.NonNull;

/**
 * Utility class used to draw color bubbles on custom views
 *
 */

public class BubbleGenerator implements ValueAnimator.AnimatorUpdateListener
{
    // default values for variables
    private static final int DEFAULT_DURATION = 200;
    private static final float DEFAULT_MAX_RADIUS = 300.f;
    private static final float DEFAULT_MAX_OPACITY = 0.3f;
    private static final int DEFAULT_BUBBLE_COLOR = Color.GRAY;

    // parent used to invalidate view when drawing is required
    private View parent;

    // runtime variables
    private float bubbleX, bubbleY, radius;

    private ValueAnimator radiusAnimator;
    private ValueAnimator opacityAnimator;

    private Paint bubblePaint;

    // runtime variables
    private int duration;
    private float maxRadius;
    private float maxOpacity;
    private int bubbleColor;


    public BubbleGenerator(@NonNull View parent)
    {
        this(parent, DEFAULT_DURATION, DEFAULT_MAX_RADIUS);
    }

    public BubbleGenerator(@NonNull View parent, int duration, float maxRadius)
    {
        this(parent, duration, maxRadius, DEFAULT_MAX_OPACITY, DEFAULT_BUBBLE_COLOR);
    }

    public BubbleGenerator(@NonNull View parent, int duration, float maxRadius, float maxOpacity, int bubbleColor)
    {
        this.parent = parent;

        initialize();

        setDuration(duration);
        setMaxRadius(maxRadius);
        setMaxOpacity(maxOpacity);
        setBubbleColor(bubbleColor);
    }


    private void initialize()
    {
        radiusAnimator = ValueAnimator.ofFloat();
        radiusAnimator.setInterpolator(new LinearInterpolator());
        radiusAnimator.addUpdateListener(this);

        opacityAnimator = ValueAnimator.ofFloat();
        opacityAnimator.setInterpolator(new AccelerateInterpolator());
        opacityAnimator.addUpdateListener(this);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAlpha(0);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        if(animation.equals(radiusAnimator))
        {
            radius = (float) radiusAnimator.getAnimatedValue();
            parent.invalidate();
        }
        else if(animation.equals(opacityAnimator))
        {
            bubblePaint.setAlpha( Math.round((float) opacityAnimator.getAnimatedValue() * 255) );
            parent.invalidate();
        }
    }

    /** This function must me be called in parent onDraw(canvas). If is animating draw the bubble, do nothing otherwise */
    public void draw(Canvas canvas)
    {
        if(isAnimating()) canvas.drawCircle(bubbleX, bubbleY, radius, bubblePaint);
    }

    /** Start bubble animation. If it's already playing restarts the animation.
     *  Coordinates are relative to the parent (i.e. the same used in parent.onDraw(canvas) */
    public void generateBubble(float x, float y)
    {
        bubbleX = x;
        bubbleY = y;

        radiusAnimator.start();
        opacityAnimator.start();
    }

    /** Return true if it's animating a bubble, false otherwise */
    public boolean isAnimating()
    {
        return radiusAnimator.isRunning() || opacityAnimator.isRunning();
    }

    public int getDuration()
    {
        return duration;
    }

    public float getMaxRadius()
    {
        return maxRadius;
    }

    public float getMaxOpacity()
    {
        return maxOpacity;
    }

    public int getBubbleColor()
    {
        return bubbleColor;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;

        radiusAnimator.setDuration(this.duration);
        opacityAnimator.setDuration(this.duration);
    }

    public void setMaxRadius(float maxRadius)
    {
        this.maxRadius = maxRadius;

        radiusAnimator.setFloatValues(0.f, this.maxRadius);
    }

    public void setMaxOpacity(float maxOpacity)
    {
        this.maxOpacity = maxOpacity;

        opacityAnimator.setFloatValues(this.maxOpacity, 0.f);
    }

    public void setBubbleColor(int color)
    {
        this.bubbleColor = color;

        if(bubblePaint != null) bubblePaint.setColor(this.bubbleColor);
    }
}
