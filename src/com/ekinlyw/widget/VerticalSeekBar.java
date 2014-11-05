
package com.ekinlyw.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    private static final String TAG = "VerticalSeekBar";

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

        // super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
                break;
        }

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
        setProgress((int)progress);
        return true;
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
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
