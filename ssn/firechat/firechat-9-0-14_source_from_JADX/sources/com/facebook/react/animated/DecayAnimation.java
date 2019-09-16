package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableMap;

public class DecayAnimation extends AnimationDriver {
    private int mCurrentLoop;
    private final double mDeceleration;
    private double mFromValue = 0.0d;
    private int mIterations;
    private double mLastValue = 0.0d;
    private long mStartFrameTimeMillis = -1;
    private final double mVelocity;

    public DecayAnimation(ReadableMap readableMap) {
        this.mVelocity = readableMap.getDouble("velocity");
        this.mDeceleration = readableMap.getDouble("deceleration");
        boolean z = true;
        this.mIterations = readableMap.hasKey("iterations") ? readableMap.getInt("iterations") : 1;
        this.mCurrentLoop = 1;
        if (this.mIterations != 0) {
            z = false;
        }
        this.mHasFinished = z;
    }

    public void runAnimationStep(long j) {
        long j2 = j / 1000000;
        if (this.mStartFrameTimeMillis == -1) {
            this.mStartFrameTimeMillis = j2 - 16;
            if (this.mFromValue == this.mLastValue) {
                this.mFromValue = this.mAnimatedValue.mValue;
            } else {
                this.mAnimatedValue.mValue = this.mFromValue;
            }
            this.mLastValue = this.mAnimatedValue.mValue;
        }
        double exp = this.mFromValue + ((this.mVelocity / (1.0d - this.mDeceleration)) * (1.0d - Math.exp((-(1.0d - this.mDeceleration)) * ((double) (j2 - this.mStartFrameTimeMillis)))));
        if (Math.abs(this.mLastValue - exp) < 0.1d) {
            if (this.mIterations == -1 || this.mCurrentLoop < this.mIterations) {
                this.mStartFrameTimeMillis = -1;
                this.mCurrentLoop++;
            } else {
                this.mHasFinished = true;
                return;
            }
        }
        this.mLastValue = exp;
        this.mAnimatedValue.mValue = exp;
    }
}
