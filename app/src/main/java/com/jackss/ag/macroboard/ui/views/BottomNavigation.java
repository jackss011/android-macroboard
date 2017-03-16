package com.jackss.ag.macroboard.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 *  Bottom navigation used to switch between categories
 *
 */

public class BottomNavigation extends FrameLayout implements ValueAnimator.AnimatorUpdateListener
{
    private static final int HIGHLIGHT_ANIM_DURATION = 200;

    LinearLayout layout;
    
    private float highlightLeft = 0;
    private float highlightRight = 0;
    
    private int highlightHeight = 12;
    private int highlightColor = Color.RED;
    private int backgroundColor = Color.CYAN;

    private Paint highlightPaint;

    private ValueAnimator rightHlAnimator;
    private ValueAnimator leftHlAnimator;

    public BottomNavigation(Context context)
    {
        this(context, null);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BottomNavigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, 0);
    }

    public BottomNavigation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        setPadding(0, 0, 0, highlightHeight);

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        addView(layout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        for(int i = 0; i < 5; ++i) layout.addView(createTestView(context), generateNavItemLayoutParams());

        initGraphics();
        initAnimations();
    }
    
    
    private void initGraphics()
    {
        highlightPaint = new Paint();
        highlightPaint.setColor(highlightColor);
        highlightPaint.setStyle(Paint.Style.FILL);

        setBackgroundColor(backgroundColor); //TODO remove this statement
    }

    private void initAnimations()
    {
        rightHlAnimator = ValueAnimator.ofFloat();
        setupHlAnimator(rightHlAnimator);

        leftHlAnimator = ValueAnimator.ofFloat();
        setupHlAnimator(leftHlAnimator);
    }

    protected void setupHlAnimator(ValueAnimator animator)
    {
        animator.setDuration(HIGHLIGHT_ANIM_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        canvas.drawRect(highlightLeft, getHeight() - highlightHeight, highlightRight, (float) getHeight(), highlightPaint);
    }

    private ViewGroup.LayoutParams generateNavItemLayoutParams()
    {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    }

    private View createTestView(Context context) //TODO remove this function
    {
        BottomNavigationItem v = new BottomNavigationItem(context);

        return v;
    }

    private void setHighlightChild(int pos)
    {
        View child = layout.getChildAt(pos);
        if(child != null)
        {
            leftHlAnimator.cancel();
            rightHlAnimator.cancel();

            highlightLeft = child.getLeft();
            highlightRight = child.getRight();
        }

        invalidate();
    }

    public void moveHighlightToChild(int pos) //TODO should be private
    {
        View child = layout.getChildAt(pos);
        if(child != null)
        {
            leftHlAnimator.setFloatValues(highlightLeft, child.getLeft());
            leftHlAnimator.start();

            rightHlAnimator.setFloatValues(highlightRight, child.getRight());
            rightHlAnimator.start();
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator)
    {
        if(animator.equals(leftHlAnimator)) highlightLeft = (float) animator.getAnimatedValue();
        else if(animator.equals(rightHlAnimator)) highlightRight = (float) animator.getAnimatedValue();

        invalidate();
    }
}
