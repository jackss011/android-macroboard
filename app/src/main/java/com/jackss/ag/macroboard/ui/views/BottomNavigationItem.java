package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
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

    private int PADDING_BOTTOM = 10;
    private int PADDING_TOP = 6;
    private int ICON_SIZE = 24;

    private boolean isCollapsed = false;


    public BottomNavigationItem(Context context)
    {
        this(context, null);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        buildUI(context);
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
}
