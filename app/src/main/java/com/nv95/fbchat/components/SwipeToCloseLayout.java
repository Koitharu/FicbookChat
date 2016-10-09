package com.nv95.fbchat.components;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by nv95 on 09.10.16.
 */

public class SwipeToCloseLayout extends FrameLayout {

    private OnTouchListener mSwipeTouchListener;

    public SwipeToCloseLayout(Context context) {
        super(context);
    }

    public SwipeToCloseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeToCloseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwipeToCloseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setSwipeTouchListener(OnTouchListener swipeTouchListener) {
        mSwipeTouchListener = swipeTouchListener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){

        boolean intercepted = super.onInterceptTouchEvent(ev);
        if (mSwipeTouchListener != null) {
            mSwipeTouchListener.onTouch(this, ev);
        }
        return intercepted;
    }
}
