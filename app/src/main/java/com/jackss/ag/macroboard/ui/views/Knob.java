package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import com.jackss.ag.macroboard.R;
import android.view.View;

/**
 *  A view which represents a knob
 *
 */

public class Knob extends View
{
    // Calculated variables
    private float centerX;
    private float centerY;
    private float radius;
    private float notchY;
    private float notchRadius;

    // Draw helpers
    private Paint circlePaint;
    private Paint circleStrokePaint;
    private Paint notchPaint;

    // Attributes
    private int circleColor = Color.GRAY;
    private int strokeColor = Color.DKGRAY;
    private float strokeWidth = 4.f;
    private int notchColor = Color.DKGRAY;
    private float notchDisplacement = 0.2f;
    private float notchSizePerc = 0.055f;


    public Knob(Context context)
    {
        this(context, null);
    }

    public Knob(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Knob);
        try
        {
            circleColor = a.getColor(R.styleable.Knob_circleColor, circleColor);
            strokeColor = a.getColor(R.styleable.Knob_strokeColor, strokeColor);
            strokeWidth = a.getDimension(R.styleable.Knob_strokeWidth, strokeWidth);
            notchColor = a.getColor(R.styleable.Knob_notchColor, notchColor);
            notchDisplacement = a.getFloat(R.styleable.Knob_notchDisplacement, notchDisplacement);
            notchSizePerc = a.getFloat(R.styleable.Knob_notchSizePerc, notchSizePerc);
        }
        finally { a.recycle(); }

        initGraphics();
    }


    private void initGraphics()
    {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);

        circleStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleStrokePaint.setStyle(Paint.Style.STROKE);
        circleStrokePaint.setStrokeWidth(strokeWidth);
        circleStrokePaint.setColor(strokeColor);

        notchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        notchPaint.setStyle(Paint.Style.FILL);
        notchPaint.setColor(notchColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        int nopaddX = w - (getPaddingLeft() + getPaddingRight());
        int nopaddY = h - (getPaddingTop() + getPaddingBottom());

        radius = Math.min(nopaddX, nopaddY) / 2 - strokeWidth / 2;

        centerX = getPaddingLeft() + nopaddX / 2;
        centerY = getPaddingTop() + nopaddY / 2;

        notchY = centerY - radius + radius * notchDisplacement;
        notchRadius = radius * notchSizePerc;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.drawCircle(centerX, centerY, radius, circleStrokePaint);

        canvas.save();
        canvas.rotate(getAngle(), centerX, centerY);
        canvas.drawCircle(centerX, notchY, notchRadius, notchPaint);
        canvas.restore();
    }

    public float getAngle()
    {
        return 0;
    }
}
