package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.utils.BubbleGenerator;
import com.jackss.ag.macroboard.utils.MBUtils;


/**
 *  Item displayed in BottomNavigation
 *
 */

public class BottomNavigationItem extends FrameLayout
{
    private LinearLayout layout;
    private ImageView icon;
    private TextView label;

    private BubbleGenerator bubbleGenerator;
    private GestureDetector detector;

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            if(bubbleGenerator != null) bubbleGenerator.generateBubble(e.getX(), e.getY());
            return false;
        }
    };

    private int selectedColor = Color.BLUE;
    private int defaultColor = Color.GRAY;

    private boolean isCollapsed = false;


    public BottomNavigationItem(Context context)
    {
        this(context, null);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        bubbleGenerator = new BubbleGenerator(this);
        detector = new GestureDetector(context, gestureListener);

        setSoundEffectsEnabled(false);

        buildUI(context);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if(bubbleGenerator != null) bubbleGenerator.determinateMaxRadius();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        bubbleGenerator.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        detector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void buildUI(Context context)
    {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, MBUtils.dp2px(6), 0, MBUtils.dp2px(6));

        icon = new ImageView(context);

        label = new TextView(context);
        label.setGravity(Gravity.BOTTOM);
        label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        initDefaultValues();

        layout.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MBUtils.dp2px(24)));
        layout.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initDefaultValues()
    {
        setIconDrawableById(R.drawable.bni_dummy_icon);
        setLabelTextById(R.string.bni_dummy_text);
    }

    /** Called when this item need to update his colors.
     *  ex. when is selected or colors are changed */
    private void updateColors()
    {
        icon.setColorFilter(getCurrentColor());
        label.setTextColor(getCurrentColor());
    }

    /** Get current color for label + icon */
    public int getCurrentColor()
    {
        if(isSelected()) return selectedColor;
        else return defaultColor;
    }

    public void setLabelText(String text)
    {
        if(label != null) label.setText(text);
    }

    public void setLabelTextById(@StringRes int resId)
    {
        if(label != null) setLabelText(getContext().getResources().getString(resId));
    }

    public void setIconDrawable(Drawable i)
    {
        if(icon != null) icon.setImageDrawable(i);
    }

    public void setIconDrawableById(@DrawableRes int resId)
    {
        if(icon != null)
        {
            setIconDrawable(getContext().getResources().getDrawable(resId, null));
        }
    }

    /** In collapsed mode the label is hidden. Can be use if the space is too small */
    public void setCollapsed(boolean collapsed)
    {
        isCollapsed = collapsed;

        if(label == null) return;

        if(isCollapsed) label.setVisibility(GONE);
        else label.setVisibility(VISIBLE);
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);
        updateColors();
    }

    /** Set color for label + icon when un-selected */
    public void setDefaultColor(int color)
    {
        defaultColor = color;
        updateColors();
    }

    /** Set color for label + icon when selected */
    public void setSelectedColor(int color)
    {
        selectedColor = color;
        updateColors();
    }
}
