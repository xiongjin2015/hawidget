package com.haha.hwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class HaExclusiveEventGallery extends Gallery implements OnGestureListener{
    
    private ViewGroup mConflictingView = null;
    
    public HaExclusiveEventGallery(Context context){
        super(context);
    }
    
    public HaExclusiveEventGallery(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public HaExclusiveEventGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void scrollToLeft() {
        onScroll(null, null, -1, 0);
        super.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    public void scrollToRight() {
        onScroll(null, null, 1, 0);
        onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
    }

    public boolean onDown(MotionEvent motionevent) {
        return super.onDown(motionevent);
    }

    public boolean onFling(MotionEvent motionevent, MotionEvent motionevent1, float f, float f1) {
        if (isScrollingLeft(motionevent, motionevent1)) {
            scrollToLeft();
        } else {
            scrollToRight();
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setViewPagerAttribute();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        setViewPagerAttribute();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        setViewPagerAttribute();
        return super.onInterceptTouchEvent(ev);
    }

    public void setConflictingView(ViewGroup conflictingView) {
        this.mConflictingView = conflictingView;
    }

    private void setViewPagerAttribute() {
        if (mConflictingView == null)
            return;
        mConflictingView.requestDisallowInterceptTouchEvent(true);
    }


}
