package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.jackss.ag.macroboard.R;

/**
 *  A view which represents a knob
 *
 */

public class Knob extends View
{
    // Calculated variables
    private RectF circleBounds = new RectF();
    private RectF innerCircleBounds = new RectF();
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

    // Position
    private float position = 0.f;


    public Knob(Context context)
    {
        this(context, null);
    }

    public Knob(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline)
            {
                Rect r = new Rect();
                circleBounds.roundOut(r);
                outline.setOval(r);
            }
        });

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
        circleStrokePaint.setStyle(Paint.Style.FILL);
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

        circleBounds = new RectF(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
        if(circleBounds.height() > circleBounds.width())
            circleBounds.inset(0, (circleBounds.height() - circleBounds.width()) / 2);
        else
            circleBounds.inset((circleBounds.width() - circleBounds.height()) / 2, 0);

        innerCircleBounds.set(circleBounds);
        innerCircleBounds.inset(strokeWidth, strokeWidth);

        notchY = innerCircleBounds.top + innerCircleBounds.height() * notchDisplacement / 2;
        notchRadius = innerCircleBounds.height() * notchSizePerc / 2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(strokeWidth > 0.5f) canvas.drawOval(circleBounds, circleStrokePaint);
        canvas.drawOval(innerCircleBounds, circlePaint);

        canvas.save();
        canvas.rotate(getPositionDegrees(), circleBounds.centerX(), circleBounds.centerY());
        canvas.drawCircle(circleBounds.centerX(), notchY, notchRadius, notchPaint);
        canvas.restore();
    }

    /** Get current position converted to degrees from 0 to 360 */
    public float getPositionDegrees()
    {
        return 360 * getPosition();
    }

    /** Position goes from 0 to 1.  */
    public float getPosition()
    {
        return position;
    }

    /** Teleport knob to the given position */
    public void setPosition(float newPos)
    {
        float modPos = newPos % 1;

        // if new position is negative, use the corresponding positive value instead
        // ex. -0.3 becomes 1 - 0.3 = 0.7
        if(modPos < 0) modPos = 1 + modPos;

        position = modPos;
        invalidate();
    }

    /** Teleport knob to current position + delta position */
    public void movePosition(float deltaPos)
    {
        setPosition(getPosition() + deltaPos);
    }
}
