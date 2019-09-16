package com.facebook.react.views.scroll;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import javax.annotation.Nullable;

public class VelocityHelper {
    @Nullable
    private VelocityTracker mVelocityTracker;
    private float mXVelocity;
    private float mYVelocity;

    public void calculateVelocity(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        if (action == 1 || action == 3) {
            this.mVelocityTracker.computeCurrentVelocity(1);
            this.mXVelocity = this.mVelocityTracker.getXVelocity();
            this.mYVelocity = this.mVelocityTracker.getYVelocity();
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }
    }

    public float getXVelocity() {
        return this.mXVelocity;
    }

    public float getYVelocity() {
        return this.mYVelocity;
    }
}
