package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    int PADDING_BOTTOM = 10;
    int PADDING_TOP = 6;
    int ICON_SIZE = 24;


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
        layout.setPadding(0, MBUtils.dp2px(6), 0, MBUtils.dp2px(10));

        icon = new ImageView(context);
        icon.setImageResource(R.mipmap.ic_launcher);

        label = new TextView(context);
        label.setText("Test");
        label.setGravity(Gravity.BOTTOM);
        label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

        layout.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MBUtils.dp2px(24)));
        layout.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
