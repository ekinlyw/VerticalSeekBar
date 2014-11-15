
package com.ekinlyw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class VerticalSeekBar extends SeekBar {

    private static final String TAG = "VerticalSeekBar";

    private boolean mIsDragging;

    private OnSeekBarChangeListener mOnSeekBarChangeListener;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Swap horizontal space requirements and vertical space requirements
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);

        // Swap the measured width and height of this view
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        // Rotate the canvas as -90 degree.
        c.rotate(-90);

        // Translate "-height" distance in X
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                onStartTrackingTouch();
                trackTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                setPressed(true);
                if (!mIsDragging) {
                    onStartTrackingTouch();
                }
                trackTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }
                setPressed(false);
                break;
        }

        return true;
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        super.setOnSeekBarChangeListener(l);
        mOnSeekBarChangeListener = l;
    }

    private void onStartTrackingTouch() {
        mIsDragging = true;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        mIsDragging = false;
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    private void trackTouchEvent(MotionEvent event) {
        final int y = (int)event.getY();
        final int height = getHeight();
        final int paddingRight = getPaddingRight();
        final int available = height - paddingRight - getPaddingLeft();

        float scale = 0.0f;
        if (y < paddingRight) {
            scale = 1.0f;
        } else if (y > height - paddingRight) {
            scale = 0.0f;
        } else {
            scale = (float)(available - y + paddingRight) / (float)available;
        }

        float progress = (scale * getMax());

        // The new progress is initiated by the user
        setProgress((int)progress, true);
    }

    /**
     * Invoke *private* ProgressBar#setProgress(int, boolean) by reflection. The
     * "boolean" parameter of OnSeekBarChangeListener#onProgressChanged(SeekBar,
     * int, boolean) is passed as "false" by "setProgress(int)
     *
     * @param progress The new progress, between 0 and getMax()
     * @param fromUser True if the progress change was initiated by the user.
     */
    private synchronized void setProgress(int progress, boolean fromUser) {
        // TODO: It might have performance issue by reflection
        try {
            Method method = ProgressBar.class.getDeclaredMethod("setProgress", new Class[] {
                    int.class, boolean.class
            });
            method.setAccessible(true);
            method.invoke(this, new Object[] {
                    progress, fromUser
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        // To update the thumb position internally through onSizeChanged()
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.v(TAG, "onSizeChanged() w=" + w + ", h=" + h + ", oldw=" + oldw + ", oldh=" + oldh);
        // Swap the width with the height
        super.onSizeChanged(h, w, oldh, oldw);
    }
}
