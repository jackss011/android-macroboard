package com.jackss.ag.macroboard.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jackss.ag.macroboard.R;
import com.jackss.ag.macroboard.utils.BubbleGenerator;
import com.jackss.ag.macroboard.utils.ButtonDetector;
import com.jackss.ag.macroboard.utils.MBUtils;


/**
 *  Item used in BottomNavigation.
 *
 *  Nothing should be called on this class, use BottomNavigation methods instead.
 */
public class BottomNavigationItem extends FrameLayout
{
    private final static int TOP_PADDING_DP = 5;
    private final static int BOTTOM_PADDING_DP = 2;
    private final static int TEXT_SIZE_SP = 12;
    private final static int ICON_SIZE_DP = 24;

    private LinearLayout layout;
    private ImageView icon;
    private TextView label;

    private BubbleGenerator bubbleGenerator;
    private ButtonDetector detector;

    private int selectedColor = Color.BLUE;
    private int defaultColor = Color.GRAY;


    private ButtonDetector.OnButtonEventListener mButtonEventListener = new ButtonDetector.OnButtonEventListener()
    {
        @Override
        public void onDown(float x, float y) {}
        @Override
        public void onTap(float x, float y) {}
        @Override
        public void onCancel() {}

        @Override
        public void onUp(float x, float y)
        {
            bubbleGenerator.generateBubble(x, y);
            performClick();
        }
    };


    public BottomNavigationItem(Context context)
    {
        this(context, null);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setSoundEffectsEnabled(false);

        initHelpers();
        initUI();
        initDefaultValues();
    }


    private void initHelpers()
    {
        bubbleGenerator = new BubbleGenerator(this)
                .setDuration(BubbleGenerator.DURATION_LONG)
                .setMaxOpacity(BubbleGenerator.OPACITY_LIGHT);
        detector = new ButtonDetector(mButtonEventListener);
    }

    private void initUI()
    {
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(0, MBUtils.dp2px(TOP_PADDING_DP), 0, MBUtils.dp2px(BOTTOM_PADDING_DP));

        icon = new ImageView(getContext());

        label = new TextView(getContext());
        label.setGravity(Gravity.BOTTOM);
        label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);

        layout.addView(icon, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MBUtils.dp2px(ICON_SIZE_DP)));
        layout.addView(label, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        addView(layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initDefaultValues()
    {
        setIconDrawable(R.drawable.bni_dummy_icon);
        setLabelText(R.string.bni_dummy_text);
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

        return true;
    }

    // Called when this item need to update his colors.
    // ex. when is selected or colors are changed
    private void updateColors()
    {
        icon.setColorFilter(getCurrentColor());
        label.setTextColor(getCurrentColor());
    }

    /** Get current color ued for label and icon */
    public int getCurrentColor()
    {
        if(isSelected())
            return selectedColor;
        else
            return defaultColor;
    }

    /** Set label text for this item */
    public void setLabelText(String text)
    {
        if(label != null) label.setText(text);
    }

    /** Set label text using a string resource */
    public void setLabelText(@StringRes int resId)
    {
        if(label != null) setLabelText(getContext().getResources().getString(resId));
    }

    /** Set the icon for this item */
    public void setIconDrawable(Drawable i)
    {
        if(icon != null) icon.setImageDrawable(i);
    }

    /** Set icon using a drawable resource */
    public void setIconDrawable(@DrawableRes int resId)
    {
        if(icon != null) setIconDrawable(getContext().getResources().getDrawable(resId, null));
    }

    /** Condensed mode allow to save space hiding the label or the icon */
    public void setCondensedMode(BottomNavigation.CondensedMode condensedMode)
    {
        switch (condensedMode)
        {
            case None:
                label.setVisibility(VISIBLE);
                icon.setVisibility(VISIBLE);
                break;

            case Label:
                label.setVisibility(GONE);
                icon.setVisibility(VISIBLE);
                break;

            case Icon:
                label.setVisibility(VISIBLE);
                icon.setVisibility(GONE);
                break;
        }
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
