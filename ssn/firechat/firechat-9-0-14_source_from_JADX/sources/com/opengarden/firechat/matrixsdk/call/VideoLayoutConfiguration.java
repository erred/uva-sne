package com.opengarden.firechat.matrixsdk.call;

import java.io.Serializable;

public class VideoLayoutConfiguration implements Serializable {
    public static final int INVALID_VALUE = -1;
    public int mDisplayHeight;
    public int mDisplayWidth;
    public int mHeight;
    public boolean mIsPortrait;
    public int mWidth;

    /* renamed from: mX */
    public int f127mX;

    /* renamed from: mY */
    public int f128mY;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VideoLayoutConfiguration{mIsPortrait=");
        sb.append(this.mIsPortrait);
        sb.append(", X=");
        sb.append(this.f127mX);
        sb.append(", Y=");
        sb.append(this.f128mY);
        sb.append(", Width=");
        sb.append(this.mWidth);
        sb.append(", Height=");
        sb.append(this.mHeight);
        sb.append('}');
        return sb.toString();
    }

    public VideoLayoutConfiguration(int i, int i2, int i3, int i4) {
        this(i, i2, i3, i4, -1, -1);
    }

    public VideoLayoutConfiguration(int i, int i2, int i3, int i4, int i5, int i6) {
        this.f127mX = i;
        this.f128mY = i2;
        this.mWidth = i3;
        this.mHeight = i4;
        this.mDisplayWidth = i5;
        this.mDisplayHeight = i6;
    }

    public VideoLayoutConfiguration() {
        this.f127mX = -1;
        this.f128mY = -1;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDisplayWidth = -1;
        this.mDisplayHeight = -1;
    }
}
