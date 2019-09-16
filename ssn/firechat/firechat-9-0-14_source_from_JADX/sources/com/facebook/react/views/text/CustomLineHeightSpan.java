package com.facebook.react.views.text;

import android.graphics.Paint.FontMetricsInt;
import android.text.style.LineHeightSpan;

public class CustomLineHeightSpan implements LineHeightSpan {
    private final int mHeight;

    CustomLineHeightSpan(float f) {
        this.mHeight = (int) Math.ceil((double) f);
    }

    public void chooseHeight(CharSequence charSequence, int i, int i2, int i3, int i4, FontMetricsInt fontMetricsInt) {
        if ((-fontMetricsInt.ascent) > this.mHeight) {
            int i5 = -this.mHeight;
            fontMetricsInt.ascent = i5;
            fontMetricsInt.top = i5;
            fontMetricsInt.descent = 0;
            fontMetricsInt.bottom = 0;
        } else if ((-fontMetricsInt.ascent) + fontMetricsInt.descent > this.mHeight) {
            fontMetricsInt.top = fontMetricsInt.ascent;
            int i6 = this.mHeight + fontMetricsInt.ascent;
            fontMetricsInt.descent = i6;
            fontMetricsInt.bottom = i6;
        } else if ((-fontMetricsInt.ascent) + fontMetricsInt.bottom > this.mHeight) {
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = fontMetricsInt.ascent + this.mHeight;
        } else if ((-fontMetricsInt.top) + fontMetricsInt.bottom > this.mHeight) {
            fontMetricsInt.top = fontMetricsInt.bottom - this.mHeight;
        } else {
            int i7 = this.mHeight - ((-fontMetricsInt.top) + fontMetricsInt.bottom);
            fontMetricsInt.top -= i7;
            fontMetricsInt.ascent -= i7;
        }
    }
}
