package com.jackss.ag.macroboard.ui.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.jackss.ag.macroboard.R;

/**
 *  Bottom navigation used to switch between categories
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
    private boolean condensed = false;
    private int cursorAnimDuration = 200;
    private int cursorHeight = 12;

    private Paint cursorPaint;

    private ValueAnimator rightCursorAnimator;
    private ValueAnimator leftCursorAnimator;

    private OnSelectionListener selectionListener;


    public interface OnSelectionListener { void onSelection(int pos, BottomNavigationItem item); }


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
            condensed = a.getBoolean(R.styleable.BottomNavigation_condensed, condensed);

            cursorColor = a.getColor(R.styleable.BottomNavigation_cursorColor, selectedItemColor);
            cursorAnimDuration = a.getInt(R.styleable.BottomNavigation_cursorAnimDuration, cursorAnimDuration);
            cursorHeight = (int) a.getDimension(R.styleable.BottomNavigation_cursorHeight, cursorHeight);
        }
        finally { a.recycle(); }

        setPadding(0, 0, 0, cursorHeight);

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        addView(layout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initOutline();
        initGraphics();
        initAnimations();

        // if is editor mode add some items to allow view styling
        if(isInEditMode()) buildTestItems();
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

    /** When a new item is being created it is passed to this function to setup static configurations.
     *  For example: colors, condensed mode */
    protected void setupItem(BottomNavigationItem item)
    {
        item.setDefaultColor(defaultItemColor);
        item.setSelectedColor(selectedItemColor);
        item.setCollapsed(condensed);
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

    /** Select the item at the given position and call BottomViewSelectListener callback.
     * @param pos The position of the item
     * @param animate If should play cursor animations or not
     * */
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
    
    /** Add a new NavigationItem at the end of bar
     * @param labelText Text of the item. If null a default value is used
     * @param icon Icon of the item. If null a circle icon is used */
    public void addNavigationItem(String labelText, Drawable icon)
    {
        BottomNavigationItem newItem = new BottomNavigationItem(getContext());
        
        setupItem(newItem);
        if(labelText != null) newItem.setLabelText(labelText);
        if(icon != null) newItem.setIconDrawable(icon);

        layout.addView(newItem, generateNavItemLayoutParams());
    }
}
