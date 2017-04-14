package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.jackss.ag.macroboard.R;

/**
 *  A view used to simulate a laptop-like trackpad
 *
 */

public class TrackpadView extends View
{
    private static final String TAG = "TrackpadView";


    private int baseColor = Color.rgb(200, 200, 200);

    private float borderRadius = 8.f;


    private RectF baseRect;

    private Paint basePaint;


    public TrackpadView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TrackpadView);
        try
        {
            baseColor = a.getColor(R.styleable.TrackpadView_baseColor, baseColor);
            borderRadius = a.getDimension(R.styleable.TrackpadView_borderRadius, borderRadius);
        }
        finally { a.recycle(); }

        setOutlineProvider(new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                if(baseRect != null)
                {
                    Rect rect = new Rect();
                    baseRect.roundOut(rect);
                    outline.setRoundRect(rect, borderRadius);
                }
            }
        });

        initGraphics();
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        baseRect = new RectF(getPaddingLeft(), getPaddingTop(), w - getPaddingLeft(), h - getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawRoundRect(baseRect, borderRadius, borderRadius, basePaint);
    }

    private void initGraphics()
    {
        basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        basePaint.setColor(baseColor);
        basePaint.setStyle(Paint.Style.FILL);
    }
}
