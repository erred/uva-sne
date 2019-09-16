package com.facebook.react.flat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.infer.annotation.Assertions;
import javax.annotation.Nullable;

final class InlineImageSpanWithPipeline extends ReplacementSpan implements AttachDetachListener, BitmapUpdateListener {
    private static final RectF TMP_RECT = new RectF();
    @Nullable
    private InvalidateCallback mCallback;
    private boolean mFrozen;
    private float mHeight;
    @Nullable
    private PipelineRequestHelper mRequestHelper;
    private float mWidth;

    public void onImageLoadEvent(int i) {
    }

    InlineImageSpanWithPipeline() {
        this(null, Float.NaN, Float.NaN);
    }

    private InlineImageSpanWithPipeline(@Nullable PipelineRequestHelper pipelineRequestHelper, float f, float f2) {
        this.mRequestHelper = pipelineRequestHelper;
        this.mWidth = f;
        this.mHeight = f2;
    }

    /* access modifiers changed from: 0000 */
    public InlineImageSpanWithPipeline mutableCopy() {
        return new InlineImageSpanWithPipeline(this.mRequestHelper, this.mWidth, this.mHeight);
    }

    /* access modifiers changed from: 0000 */
    public boolean hasImageRequest() {
        return this.mRequestHelper != null;
    }

    /* access modifiers changed from: 0000 */
    public void setImageRequest(@Nullable ImageRequest imageRequest) {
        if (imageRequest == null) {
            this.mRequestHelper = null;
        } else {
            this.mRequestHelper = new PipelineRequestHelper(imageRequest);
        }
    }

    /* access modifiers changed from: 0000 */
    public float getWidth() {
        return this.mWidth;
    }

    /* access modifiers changed from: 0000 */
    public void setWidth(float f) {
        this.mWidth = f;
    }

    /* access modifiers changed from: 0000 */
    public float getHeight() {
        return this.mHeight;
    }

    /* access modifiers changed from: 0000 */
    public void setHeight(float f) {
        this.mHeight = f;
    }

    /* access modifiers changed from: 0000 */
    public void freeze() {
        this.mFrozen = true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isFrozen() {
        return this.mFrozen;
    }

    public void onSecondaryAttach(Bitmap bitmap) {
        ((InvalidateCallback) Assertions.assumeNotNull(this.mCallback)).invalidate();
    }

    public void onBitmapReady(Bitmap bitmap) {
        ((InvalidateCallback) Assertions.assumeNotNull(this.mCallback)).invalidate();
    }

    public void onAttached(InvalidateCallback invalidateCallback) {
        this.mCallback = invalidateCallback;
        if (this.mRequestHelper != null) {
            this.mRequestHelper.attach(this);
        }
    }

    public void onDetached() {
        if (this.mRequestHelper != null) {
            this.mRequestHelper.detach();
            if (this.mRequestHelper.isDetached()) {
                this.mCallback = null;
            }
        }
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            fontMetricsInt.ascent = -Math.round(this.mHeight);
            fontMetricsInt.descent = 0;
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = 0;
        }
        return Math.round(this.mWidth);
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        if (this.mRequestHelper != null) {
            Bitmap bitmap = this.mRequestHelper.getBitmap();
            if (bitmap != null) {
                float f2 = ((float) i5) - ((float) paint.getFontMetricsInt().descent);
                TMP_RECT.set(f, f2 - this.mHeight, this.mWidth + f, f2);
                canvas.drawBitmap(bitmap, null, TMP_RECT, paint);
            }
        }
    }
}
