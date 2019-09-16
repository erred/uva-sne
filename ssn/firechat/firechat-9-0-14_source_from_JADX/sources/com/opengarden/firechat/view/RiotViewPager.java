package com.opengarden.firechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.p000v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.opengarden.firechat.matrixsdk.util.Log;

public class RiotViewPager extends ViewPager {
    private static final String LOG_TAG = "RiotViewPager";

    public RiotViewPager(Context context) {
        super(context);
    }

    public RiotViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getAdapter() == null || getAdapter().getCount() == 0) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (getAdapter() == null || getAdapter().getCount() == 0) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int i, int i2) {
        try {
            return super.getChildDrawingOrder(i, i2);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getChildDrawingOrder() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return 0;
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## dispatchDraw() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }
}
