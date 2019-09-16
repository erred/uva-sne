package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

class FrameBasedAnimationDriver extends AnimationDriver {
    private static final double FRAME_TIME_MILLIS = 16.666666666666668d;
    private int mCurrentLoop;
    private final double[] mFrames;
    private double mFromValue;
    private int mIterations;
    private long mStartFrameTimeNanos = -1;
    private final double mToValue;

    FrameBasedAnimationDriver(ReadableMap readableMap) {
        ReadableArray array = readableMap.getArray("frames");
        int size = array.size();
        this.mFrames = new double[size];
        for (int i = 0; i < size; i++) {
            this.mFrames[i] = array.getDouble(i);
        }
        this.mToValue = readableMap.getDouble("toValue");
        boolean z = true;
        this.mIterations = readableMap.hasKey("iterations") ? readableMap.getInt("iterations") : 1;
        this.mCurrentLoop = 1;
        if (this.mIterations != 0) {
            z = false;
        }
        this.mHasFinished = z;
    }

    public void runAnimationStep(long j) {
        double d;
        if (this.mStartFrameTimeNanos < 0) {
            this.mStartFrameTimeNanos = j;
            this.mFromValue = this.mAnimatedValue.mValue;
        }
        int i = (int) (((double) ((j - this.mStartFrameTimeNanos) / 1000000)) / FRAME_TIME_MILLIS);
        if (i < 0) {
            throw new IllegalStateException("Calculated frame index should never be lower than 0");
        } else if (!this.mHasFinished) {
            if (i >= this.mFrames.length - 1) {
                d = this.mToValue;
                if (this.mIterations == -1 || this.mCurrentLoop < this.mIterations) {
                    this.mStartFrameTimeNanos = j;
                    this.mCurrentLoop++;
                } else {
                    this.mHasFinished = true;
                }
            } else {
                d = (this.mFrames[i] * (this.mToValue - this.mFromValue)) + this.mFromValue;
            }
            this.mAnimatedValue.mValue = d;
        }
    }
}
