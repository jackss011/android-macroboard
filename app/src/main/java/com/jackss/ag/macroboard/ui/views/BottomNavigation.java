package com.jackss.ag.macroboard.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.jackss.ag.macroboard.R;

/**
 *  Bottom navigation similar to the one defined in material design.
 *
 *  Navigation can be filled with NavigationItems using addNavigationItem(...).
 *  The suggested number of child is between 2 and 6.
 *
 *  Condensed mode can be used to hide the text of all the items, reducing its height.
 *  A cursor (colored underline) is displayed under the selected item.
 *
 *  Navigation selection event can be listened using BottomNavigation.OnSelectionLister through its setter
 *
 */
public class BottomNavigation extends FrameLayout implements ValueAnimator.AnimatorUpdateListener, View.OnClickListener
{
    LinearLayout layout;
    
    private float cursorLeft = 0.f;
    private float cursorRight = 0.f;
    
    // Attributes
    private int defaultItemColor = Color.GRAY;
    private int selectedItemColor = Color.BLUE;
    private int cursorColor = Color.RED;
    private CondensedMode condensedMode;
    private int cursorAnimDuration = 200;
    private int cursorHeight = 12;

    private Paint cursorPaint;

    private ValueAnimator rightCursorAnimator;
    private ValueAnimator leftCursorAnimator;

    private OnSelectionListener selectionListener;


    public interface OnSelectionListener
    {
        /** Called when an item of BottomNavigation is clicked */
        void onSelection(int pos, BottomNavigationItem item);
    }

    public enum CondensedMode
    {
        None,   // Show label and icon
        Label,  // Hide the label
        Icon    // Hide the icon
    }


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

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomNavigation, defStyleAttr, defStyleRes);
        try
        {
            defaultItemColor = a.getColor(R.styleable.BottomNavigation_defaultItemColor, defaultItemColor);
            selectedItemColor = a.getColor(R.styleable.BottomNavigation_selectedItemColor, selectedItemColor);
            condensedMode = resolveCondensedMode( a.getInteger(R.styleable.BottomNavigation_condensedMode, 0) );

            cursorColor = a.getColor(R.styleable.BottomNavigation_cursorColor, selectedItemColor);
            cursorAnimDuration = a.getInt(R.styleable.BottomNavigation_cursorAnimDuration, cursorAnimDuration);
            cursorHeight = (int) a.getDimension(R.styleable.BottomNavigation_cursorHeight, cursorHeight);
        }
        finally { a.recycle(); }

        setPadding(0, 0, 0, cursorHeight);

        initUI();
        initOutline();
        initGraphics();
        initAnimations();

        // if is editor mode add some items to allow view styling
        if(isInEditMode()) buildTestItems();
    }

    private void initUI()
    {
        layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        addView(layout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void initOutline()
    {
        setOutlineProvider(new ViewOutlineProvider()
        {
            @Override
            public void getOutline(View view, Outline outline)
            {
                outline.setRect(0, 0, getWidth(), getHeight());
            }
        });
    }

    private void initGraphics()
    {
        cursorPaint = new Paint();
        cursorPaint.setColor(cursorColor);
        cursorPaint.setStyle(Paint.Style.FILL);
    }

    private void initAnimations()
    {
        rightCursorAnimator = ValueAnimator.ofFloat();
        setupCursorAnimator(rightCursorAnimator);

        leftCursorAnimator = ValueAnimator.ofFloat();
        setupCursorAnimator(leftCursorAnimator);
    }

    // Map the int value used in xml enum to a real java enum
    private CondensedMode resolveCondensedMode(int attrValue)
    {
        switch (attrValue)
        {
            case 0: return CondensedMode.None;
            case 1: return CondensedMode.Label;
            case 2: return CondensedMode.Icon;

            default: return CondensedMode.None;
        }
    }

    /** When a new item is being created it is passed to this function to setup static configurations.
     *  For example: colors, condensed mode, listeners */
    protected void setupItem(BottomNavigationItem item)
    {
        item.setDefaultColor(defaultItemColor);
        item.setSelectedColor(selectedItemColor);
        item.setCondensedMode(condensedMode);
        item.setOnClickListener(this);
    }

    /** Called to setup cursor animators. Can be overridden for custom behavior.
     *  NOTE: super method should be called before custom behavior. */
    protected void setupCursorAnimator(ValueAnimator animator)
    {
        animator.setDuration(cursorAnimDuration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(this);
    }

    // Create LayoutParams for an item
    private ViewGroup.LayoutParams generateNavItemLayoutParams()
    {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    }

    // Add 4 dummy items
    private void buildTestItems()
    {
        for(int i = 0; i < 4; ++i) addNavigationItem(null, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        
        setWillNotDraw(false);
        selectItem(0, false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        canvas.drawRect(cursorLeft, getHeight() - cursorHeight, cursorRight, (float) getHeight(), cursorPaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator)
    {
        if(animator.equals(leftCursorAnimator)) cursorLeft = (float) animator.getAnimatedValue();
        else if(animator.equals(rightCursorAnimator)) cursorRight = (float) animator.getAnimatedValue();

        invalidate();
    }

    @Override
    public void onClick(View v)
    {
        final int index = layout.indexOfChild(v);
        if(index != -1) selectItem(index, true);
    }

    // Teleport the cursor to the specified child position, cancelling animation
    private void setCursorToChild(int pos)
    {
        View child = layout.getChildAt(pos);
        if(child != null)
        {
            leftCursorAnimator.cancel();
            rightCursorAnimator.cancel();

            cursorLeft = child.getLeft();
            cursorRight = child.getRight();
        }

        invalidate();
    }

    // Animate the cursor to the specified position
    private void moveCursorToChild(int pos)
    {
        View child = layout.getChildAt(pos);
        if(child != null)
        {
            leftCursorAnimator.setFloatValues(cursorLeft, child.getLeft());
            leftCursorAnimator.start();

            rightCursorAnimator.setFloatValues(cursorRight, child.getRight());
            rightCursorAnimator.start();
        }
    }

    /**
     * Select the item at the given position and call BottomViewSelectListener callback.
     * @param pos       The position of the item
     * @param animate   If should play cursor animations or not
     */
    public void selectItem(int pos, boolean animate)
    {
        if(layout.getChildAt(pos) == null)
        {
            return;
        }

        for (int i = 0; i < layout.getChildCount(); ++i)
        {
            BottomNavigationItem child = (BottomNavigationItem) layout.getChildAt(i);
            if(child == null) continue;

            if(pos == i)
            {
                child.setSelected(true);

                // notify selection
                if(selectionListener != null) selectionListener.onSelection(pos, child);

                // should play animations
                if(animate)
                    moveCursorToChild(i);
                else
                    setCursorToChild(i);
            }
            else child.setSelected(false);
        }
    }

    /** Listens for BottomNavigation selection */
    public void setOnSelectionListener(@Nullable OnSelectionListener selectionListener)
    {
        this.selectionListener = selectionListener;
    }
    
    /**
     * Add a new NavigationItem at the end of bar
     * @param labelText Text of the item. If null a default value is used
     * @param icon      Icon of the item. If null a circle icon is used
     */
    public void addNavigationItem(String labelText, Drawable icon)
    {
        BottomNavigationItem newItem = new BottomNavigationItem(getContext());
        
        setupItem(newItem);
        if(labelText != null) newItem.setLabelText(labelText);
        if(icon != null) newItem.setIconDrawable(icon);

        layout.addView(newItem, generateNavItemLayoutParams());
    }
}
