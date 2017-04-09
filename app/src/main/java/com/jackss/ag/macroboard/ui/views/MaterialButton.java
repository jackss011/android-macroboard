package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.utils.MBUtils;

/**
 * Cool button
 *
 */
public class MaterialButton extends View
{
    private static final int DESIRED_BACKGROUND_DP = 56;

    Paint backgroundPaint;
    Drawable icon;

    RectF backgroundRect = new RectF();
    Rect iconRect = new Rect();

    int backgroundColor = Color.DKGRAY;
    float cornerRadius = MBUtils.dp2px(2);
    int iconSize = MBUtils.dp2px(24);


    public MaterialButton(Context context) { this(context, null); }

    public MaterialButton(Context context, @Nullable AttributeSet attrs) { this(context, attrs, 0); }

    public MaterialButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { this(context, attrs, defStyleAttr, 0); }

    public MaterialButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        icon = getResources().getDrawable(R.drawable.ic_test_icon, null);

        initGraphics();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        backgroundRect.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());

        final float wi = (backgroundRect.width() - iconSize) / 2;
        final float hi = (backgroundRect.height() - iconSize) / 2;

        backgroundRect.round(iconRect);
        iconRect.inset(Math.round(wi), Math.round(hi));
        icon.setBounds(iconRect);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int desiredSize = MBUtils.dp2px(DESIRED_BACKGROUND_DP);

        //
        final int desiredW = desiredSize + getPaddingLeft() + getPaddingRight();
        final int desiredH = desiredSize + getPaddingTop() + getPaddingBottom();

        int measuredW = MBUtils.resolveDesiredMeasure(widthMeasureSpec, desiredW);
        int measuredH = MBUtils.resolveDesiredMeasure(heightMeasureSpec, desiredH);

        setMeasuredDimension(measuredW, measuredH);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // draw the background rect
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // draw the icon
        icon.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    private void initGraphics()
    {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor);

    }
}
