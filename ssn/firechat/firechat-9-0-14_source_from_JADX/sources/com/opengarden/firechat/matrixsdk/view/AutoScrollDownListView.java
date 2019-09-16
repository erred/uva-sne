package com.opengarden.firechat.matrixsdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.opengarden.firechat.matrixsdk.util.Log;

public class AutoScrollDownListView extends ListView {
    private static final String LOG_TAG = "AutoScrollDownListView";
    private boolean mLockSelectionOnResize = false;

    public AutoScrollDownListView(Context context) {
        super(context);
    }

    public AutoScrollDownListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AutoScrollDownListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (!this.mLockSelectionOnResize && i2 < i4) {
            postDelayed(new Runnable() {
                public void run() {
                    AutoScrollDownListView.this.setSelection(AutoScrollDownListView.this.getCount() - 1);
                }
            }, 100);
        }
    }

    public void lockSelectionOnResize() {
        this.mLockSelectionOnResize = true;
    }

    /* access modifiers changed from: protected */
    public void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## layoutChildren() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void setSelectionFromTop(int i, int i2) {
        super.setSelectionFromTop(i, i2);
    }
}
