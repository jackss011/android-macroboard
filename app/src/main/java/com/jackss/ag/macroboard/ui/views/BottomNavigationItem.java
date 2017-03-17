package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.jackss.ag.macroboard.R;
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

    private GestureDetector detector;

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            Log.v("Test", "Tap:" + e.getX() + " - " + e.getY());
            return true;
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

        detector = new GestureDetector(context, gestureListener);
        setSoundEffectsEnabled(false);

        buildUI(context);
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

        icon.setColorFilter(getCurrentColor());
        label.setTextColor(getCurrentColor());
    }

    public int getCurrentColor()
    {
        if(isSelected()) return selectedColor;
        else return defaultColor;
    }
}
