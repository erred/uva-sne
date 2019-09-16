package com.opengarden.firechat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import com.opengarden.firechat.C1299R;

public class VideoRecordProgressView extends View {
    private static final int PROGRESS_STEP = 12;
    private static final int START_ANGLE = -90;
    /* access modifiers changed from: private */
    public int mAngle;
    private final Paint mPaint;
    private int mPowerColor;
    private final Runnable mProgressHandler = new Runnable() {
        public void run() {
            VideoRecordProgressView.this.mAngle = VideoRecordProgressView.this.mAngle + 12;
            if (VideoRecordProgressView.this.mAngle >= 360) {
                VideoRecordProgressView.this.mAngle = 0;
                VideoRecordProgressView.this.mRoundCount = VideoRecordProgressView.this.mRoundCount + 1;
                VideoRecordProgressView.this.refreshColor();
            }
            VideoRecordProgressView.this.invalidate();
            VideoRecordProgressView.this.mUIHandler.postDelayed(this, 1000);
        }
    };
    private final RectF mRectF;
    private int mRestColor;
    /* access modifiers changed from: private */
    public int mRoundCount = 0;
    /* access modifiers changed from: private */
    public final Handler mUIHandler = new Handler();

    public VideoRecordProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16842996, 16842997});
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        if (obtainStyledAttributes != null) {
            obtainStyledAttributes.recycle();
        }
        this.mRectF = new RectF(0.0f, 0.0f, (float) dimensionPixelSize, (float) dimensionPixelSize2);
        this.mPaint = new Paint();
    }

    /* access modifiers changed from: private */
    public void refreshColor() {
        if (this.mRoundCount == 0) {
            this.mPowerColor = getResources().getColor(17170443);
            this.mRestColor = getResources().getColor(17170445);
            return;
        }
        int i = (this.mRoundCount - 1) % 2;
        int color = getResources().getColor(C1299R.color.vector_silver_color);
        int color2 = getResources().getColor(17170443);
        this.mPowerColor = i == 0 ? color : color2;
        if (i != 0) {
            color2 = color;
        }
        this.mRestColor = color2;
    }

    public void startAnimation() {
        stopAnimation();
        this.mAngle = 0;
        this.mRoundCount = 0;
        refreshColor();
        this.mProgressHandler.run();
        invalidate();
    }

    public void stopAnimation() {
        this.mUIHandler.removeCallbacks(this.mProgressHandler);
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        if (8 == i || 4 == i) {
            stopAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.mPaint.setColor(this.mPowerColor);
        Canvas canvas2 = canvas;
        canvas2.drawArc(this.mRectF, -90.0f, (float) this.mAngle, true, this.mPaint);
        this.mPaint.setColor(this.mRestColor);
        canvas2.drawArc(this.mRectF, (float) (this.mAngle + START_ANGLE), (float) (360 - this.mAngle), true, this.mPaint);
    }
}
