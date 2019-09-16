package com.facebook.react.flat;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import javax.annotation.Nullable;

final class FontStylingSpan extends MetricAffectingSpan {
    static final FontStylingSpan INSTANCE;
    private int mBackgroundColor;
    @Nullable
    private String mFontFamily;
    private int mFontSize;
    private int mFontStyle;
    private int mFontWeight;
    private boolean mFrozen;
    private boolean mHasStrikeThrough;
    private boolean mHasUnderline;
    private double mTextColor;

    static {
        FontStylingSpan fontStylingSpan = new FontStylingSpan(-1.6777216E7d, 0, -1, -1, -1, false, false, null, true);
        INSTANCE = fontStylingSpan;
    }

    FontStylingSpan() {
    }

    private FontStylingSpan(double d, int i, int i2, int i3, int i4, boolean z, boolean z2, @Nullable String str, boolean z3) {
        this.mTextColor = d;
        this.mBackgroundColor = i;
        this.mFontSize = i2;
        this.mFontStyle = i3;
        this.mFontWeight = i4;
        this.mHasUnderline = z;
        this.mHasStrikeThrough = z2;
        this.mFontFamily = str;
        this.mFrozen = z3;
    }

    /* access modifiers changed from: 0000 */
    public FontStylingSpan mutableCopy() {
        FontStylingSpan fontStylingSpan = new FontStylingSpan(this.mTextColor, this.mBackgroundColor, this.mFontSize, this.mFontStyle, this.mFontWeight, this.mHasUnderline, this.mHasStrikeThrough, this.mFontFamily, false);
        return fontStylingSpan;
    }

    /* access modifiers changed from: 0000 */
    public boolean isFrozen() {
        return this.mFrozen;
    }

    /* access modifiers changed from: 0000 */
    public void freeze() {
        this.mFrozen = true;
    }

    /* access modifiers changed from: 0000 */
    public double getTextColor() {
        return this.mTextColor;
    }

    /* access modifiers changed from: 0000 */
    public void setTextColor(double d) {
        this.mTextColor = d;
    }

    /* access modifiers changed from: 0000 */
    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    /* access modifiers changed from: 0000 */
    public void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
    }

    /* access modifiers changed from: 0000 */
    public int getFontSize() {
        return this.mFontSize;
    }

    /* access modifiers changed from: 0000 */
    public void setFontSize(int i) {
        this.mFontSize = i;
    }

    /* access modifiers changed from: 0000 */
    public int getFontStyle() {
        return this.mFontStyle;
    }

    /* access modifiers changed from: 0000 */
    public void setFontStyle(int i) {
        this.mFontStyle = i;
    }

    /* access modifiers changed from: 0000 */
    public int getFontWeight() {
        return this.mFontWeight;
    }

    /* access modifiers changed from: 0000 */
    public void setFontWeight(int i) {
        this.mFontWeight = i;
    }

    /* access modifiers changed from: 0000 */
    @Nullable
    public String getFontFamily() {
        return this.mFontFamily;
    }

    /* access modifiers changed from: 0000 */
    public void setFontFamily(@Nullable String str) {
        this.mFontFamily = str;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasUnderline() {
        return this.mHasUnderline;
    }

    /* access modifiers changed from: 0000 */
    public void setHasUnderline(boolean z) {
        this.mHasUnderline = z;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasStrikeThrough() {
        return this.mHasStrikeThrough;
    }

    /* access modifiers changed from: 0000 */
    public void setHasStrikeThrough(boolean z) {
        this.mHasStrikeThrough = z;
    }

    public void updateDrawState(TextPaint textPaint) {
        if (!Double.isNaN(this.mTextColor)) {
            textPaint.setColor((int) this.mTextColor);
        }
        textPaint.bgColor = this.mBackgroundColor;
        textPaint.setUnderlineText(this.mHasUnderline);
        textPaint.setStrikeThruText(this.mHasStrikeThrough);
        updateMeasureState(textPaint);
    }

    public void updateMeasureState(TextPaint textPaint) {
        if (this.mFontSize != -1) {
            textPaint.setTextSize((float) this.mFontSize);
        }
        updateTypeface(textPaint);
    }

    private int getNewStyle(int i) {
        if (this.mFontStyle != -1) {
            i = (i & -3) | this.mFontStyle;
        }
        return this.mFontWeight != -1 ? (i & -2) | this.mFontWeight : i;
    }

    private void updateTypeface(TextPaint textPaint) {
        int i;
        Typeface typeface;
        Typeface typeface2 = textPaint.getTypeface();
        if (typeface2 == null) {
            i = 0;
        } else {
            i = typeface2.getStyle();
        }
        int newStyle = getNewStyle(i);
        if (i != newStyle || this.mFontFamily != null) {
            if (this.mFontFamily != null) {
                typeface = TypefaceCache.getTypeface(this.mFontFamily, newStyle);
            } else {
                typeface = TypefaceCache.getTypeface(typeface2, newStyle);
            }
            textPaint.setTypeface(typeface);
        }
    }
}
