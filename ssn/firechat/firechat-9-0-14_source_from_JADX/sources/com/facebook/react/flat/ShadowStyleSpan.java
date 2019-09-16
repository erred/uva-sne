package com.facebook.react.flat;

import android.text.TextPaint;
import android.text.style.CharacterStyle;

final class ShadowStyleSpan extends CharacterStyle {
    static final ShadowStyleSpan INSTANCE;
    private int mColor;
    private float mDx;
    private float mDy;
    private boolean mFrozen;
    private float mRadius;

    static {
        ShadowStyleSpan shadowStyleSpan = new ShadowStyleSpan(0.0f, 0.0f, 0.0f, 0, true);
        INSTANCE = shadowStyleSpan;
    }

    private ShadowStyleSpan(float f, float f2, float f3, int i, boolean z) {
        this.mDx = f;
        this.mDy = f2;
        this.mRadius = f3;
        this.mColor = i;
        this.mFrozen = z;
    }

    public boolean offsetMatches(float f, float f2) {
        return this.mDx == f && this.mDy == f2;
    }

    public void setOffset(float f, float f2) {
        this.mDx = f;
        this.mDy = f2;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public void setRadius(float f) {
        this.mRadius = f;
    }

    public int getColor() {
        return this.mColor;
    }

    public void setColor(int i) {
        this.mColor = i;
    }

    /* access modifiers changed from: 0000 */
    public ShadowStyleSpan mutableCopy() {
        ShadowStyleSpan shadowStyleSpan = new ShadowStyleSpan(this.mDx, this.mDy, this.mRadius, this.mColor, false);
        return shadowStyleSpan;
    }

    /* access modifiers changed from: 0000 */
    public boolean isFrozen() {
        return this.mFrozen;
    }

    /* access modifiers changed from: 0000 */
    public void freeze() {
        this.mFrozen = true;
    }

    public void updateDrawState(TextPaint textPaint) {
        textPaint.setShadowLayer(this.mRadius, this.mDx, this.mDy, this.mColor);
    }
}
