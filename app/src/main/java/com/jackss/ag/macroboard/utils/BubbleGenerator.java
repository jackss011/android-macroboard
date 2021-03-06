package com.jackss.ag.macroboard.utils;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;


/**
 * Utility class used to draw colored circles which expands over time.
 * Useful to give visual feedback in response to a click.
 *
 * Implementation steps:
 *
 * 1. Create an object using a provided constructor
 * 2. Set the animation values
 * 3. Add BubbleGenerator.draw(canvas) inside View.onDraw(canvas)
 * 4. Call generateBubble(x, y) to start drawing a bubble in that position
 *
 * determinateRadius() can be used to try to calculate a good max radius for the bubble
 * based on the view size.
 */
public class BubbleGenerator implements ValueAnimator.AnimatorUpdateListener
{
    // Some predefined values
    public final static int DURATION_LONG = 400;
    public final static int DURATION_SHORT = 200;
    public final static float OPACITY_LIGHT = 0.3f;
    public final static float OPACITY_DARK = 0.6f;

    // Default values for variables
    private static final float DEFAULT_MAX_RADIUS = 300.f;
    private static final int DEFAULT_BUBBLE_COLOR = Color.GRAY;
    private static final float DEFAULT_AUTORADIUS_MULTIPLIER = 1.3f;

    // Parent used to invalidate view when drawing is required
    private View parent;

    // Runtime variables
    private float bubbleX;
    private float bubbleY;
    private float radius;
    private int duration;
    private float maxRadius;
    private float maxOpacity;
    private int bubbleColor;

    // Animators
    private ValueAnimator radiusAnimator;
    private ValueAnimator opacityAnimator;

    // Draw paint
    private Paint bubblePaint;


    public BubbleGenerator(@NonNull View parent)
    {
        this(parent, DURATION_SHORT, DEFAULT_MAX_RADIUS);
    }

    public BubbleGenerator(@NonNull View parent, int duration, float maxRadius)
    {
        this(parent, duration, maxRadius, OPACITY_DARK, DEFAULT_BUBBLE_COLOR);
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

    public BubbleGenerator setDuration(int duration)
    {
        this.duration = duration;
        radiusAnimator.setDuration(this.duration);
        opacityAnimator.setDuration(this.duration);

        return this;
    }

    public BubbleGenerator setMaxRadius(float maxRadius)
    {
        this.maxRadius = maxRadius;
        radiusAnimator.setFloatValues(0.f, this.maxRadius);

        return this;
    }

    public BubbleGenerator setMaxOpacity(float maxOpacity)
    {
        this.maxOpacity = maxOpacity;
        opacityAnimator.setFloatValues(this.maxOpacity, 0.f);

        return this;
    }

    public BubbleGenerator setBubbleColor(int color)
    {
        this.bubbleColor = color;
        if(bubblePaint != null) bubblePaint.setColor(this.bubbleColor);

        return this;
    }

    public BubbleGenerator setRadiusInterpolator(@NonNull TimeInterpolator interpolator)
    {
        radiusAnimator.setInterpolator(interpolator);
        return this;
    }

    public BubbleGenerator setOpacityInterpolator(@NonNull TimeInterpolator interpolator)
    {
        opacityAnimator.setInterpolator(interpolator);
        return this;
    }

    /** Set maximum radius based on parent dimensions. Should be called after every resize (i.e View.onSizeChanged(...)) */
    public BubbleGenerator determinateMaxRadius()
    {
        if(parent != null)
        {
            final float diagonal = (float) Math.sqrt(Math.pow(parent.getWidth(), 2.f) + Math.pow(parent.getHeight(), 2.f)) / 2;
            setMaxRadius(DEFAULT_AUTORADIUS_MULTIPLIER * diagonal); // slightly increase diagonal radius
        }

        return this;
    }
}
