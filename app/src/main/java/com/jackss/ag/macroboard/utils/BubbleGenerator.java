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
    // parent used to invalidate view when drawing is required
    private View parent;

    // runtime variables
    private float bubbleX, bubbleY, radius;

    private ValueAnimator radiusAnimator;
    private ValueAnimator opacityAnimator;

    private Paint bubblePaint;

    // configurations
    private int duration = 200;
    private float maxRadius = 300.f;
    private float maxOpacity = 0.3f;
    private int bubbleColor = Color.GRAY;


    public BubbleGenerator(@NonNull View parent)
    {
        this.parent = parent;

        initialize();
    }

    private void initialize()
    {
        radiusAnimator = ValueAnimator.ofFloat(0.f, this.maxRadius);
        radiusAnimator.setInterpolator(new LinearInterpolator());
        radiusAnimator.addUpdateListener(this);
        radiusAnimator.setDuration(duration);

        opacityAnimator = ValueAnimator.ofFloat(this.maxOpacity, 0.f);
        opacityAnimator.setInterpolator(new AccelerateInterpolator());
        opacityAnimator.addUpdateListener(this);
        opacityAnimator.setDuration(duration);

        bubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bubblePaint.setColor(bubbleColor);
        bubblePaint.setStyle(Paint.Style.FILL);
        bubblePaint.setAlpha(0);
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
}
