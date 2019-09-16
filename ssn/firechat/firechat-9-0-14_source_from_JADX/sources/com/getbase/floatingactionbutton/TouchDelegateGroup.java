package com.getbase.floatingactionbutton;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import java.util.ArrayList;

public class TouchDelegateGroup extends TouchDelegate {
    private static final Rect USELESS_HACKY_RECT = new Rect();
    private TouchDelegate mCurrentTouchDelegate;
    private boolean mEnabled;
    private final ArrayList<TouchDelegate> mTouchDelegates = new ArrayList<>();

    public TouchDelegateGroup(View view) {
        super(USELESS_HACKY_RECT, view);
    }

    public void addTouchDelegate(@NonNull TouchDelegate touchDelegate) {
        this.mTouchDelegates.add(touchDelegate);
    }

    public void removeTouchDelegate(TouchDelegate touchDelegate) {
        this.mTouchDelegates.remove(touchDelegate);
        if (this.mCurrentTouchDelegate == touchDelegate) {
            this.mCurrentTouchDelegate = null;
        }
    }

    public void clearTouchDelegates() {
        this.mTouchDelegates.clear();
        this.mCurrentTouchDelegate = null;
    }

    public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {
        boolean z = false;
        if (!this.mEnabled) {
            return false;
        }
        TouchDelegate touchDelegate = null;
        switch (motionEvent.getAction()) {
            case 0:
                for (int i = 0; i < this.mTouchDelegates.size(); i++) {
                    TouchDelegate touchDelegate2 = (TouchDelegate) this.mTouchDelegates.get(i);
                    if (touchDelegate2.onTouchEvent(motionEvent)) {
                        this.mCurrentTouchDelegate = touchDelegate2;
                        return true;
                    }
                }
                break;
            case 1:
            case 3:
                TouchDelegate touchDelegate3 = this.mCurrentTouchDelegate;
                this.mCurrentTouchDelegate = null;
                touchDelegate = touchDelegate3;
                break;
            case 2:
                touchDelegate = this.mCurrentTouchDelegate;
                break;
        }
        if (touchDelegate != null && touchDelegate.onTouchEvent(motionEvent)) {
            z = true;
        }
        return z;
    }

    public void setEnabled(boolean z) {
        this.mEnabled = z;
    }
}
