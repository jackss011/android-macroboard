package com.jackss.ag.macroboard.utils;

import android.view.MotionEvent;

/**
 * Utility class used to parse on touch events.
 * 
 * Callbacks available are defined in OnButtonEventListener
 */
public class ButtonDetector
{
    public interface OnButtonEventListener
    {
        /** First finger down */
        void onDown(float x, float y);

        /** First finger up. Not called on tap events */
        void onUp(float x, float y);

        /** Called insted of onUp(x, y) if the press time is < than tapDuration */
        void onTap(float x, float y);

        /** Called when ACTION_CANCEL is found in a parsed event */
        void onCancel();
    }

    private OnButtonEventListener listener;

    private long downStamp = -1;

    private int tapDuration;


    public ButtonDetector(OnButtonEventListener listener)
    {
        this(listener, -1);
    }

    public ButtonDetector(OnButtonEventListener listener, int tapDuration)
    {
        this.listener = listener;
        this.tapDuration = tapDuration;
    }


    public boolean isTapEnabled()
    {
        return tapDuration > 0;
    }

    public void onTouchEvent(MotionEvent event)
    {
        final int action = event.getActionMasked();

        // only if action pointer id = 0. (i.e first finger down)
        if(event.getPointerId(event.getActionIndex()) == 0)
        {
            if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                downStamp = System.currentTimeMillis();

                if(listener != null) listener.onDown(event.getX(), event.getY());
            }
            else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
            {
                if(isTapEnabled() && downStamp > 0 && System.currentTimeMillis() - downStamp <= tapDuration)
                {
                    if(listener != null) listener.onTap(event.getX(), event.getY());
                }
                else
                {
                    if(listener != null) listener.onUp(event.getX(), event.getY());
                }

                downStamp = -1;
            }
        }
        else if(action == MotionEvent.ACTION_CANCEL)
        {
            downStamp = -1;

            if(listener != null) listener.onCancel();
        }
    }
}
