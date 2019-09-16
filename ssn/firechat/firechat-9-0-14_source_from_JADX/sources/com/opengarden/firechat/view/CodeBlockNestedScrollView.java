package com.opengarden.firechat.view;

import android.content.Context;
import android.support.p000v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

public class CodeBlockNestedScrollView extends NestedScrollView {
    public CodeBlockNestedScrollView(Context context) {
        super(context);
    }

    public CodeBlockNestedScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CodeBlockNestedScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(500, Integer.MIN_VALUE));
    }
}
