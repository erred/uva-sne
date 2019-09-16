package com.facebook.react.views.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.p000v4.view.ViewCompat;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.uimanager.FloatUtil;
import com.facebook.react.uimanager.Spacing;
import com.facebook.yoga.YogaConstants;
import java.util.Arrays;
import java.util.Locale;
import javax.annotation.Nullable;

public class ReactViewBackgroundDrawable extends Drawable {
    private static final int ALL_BITS_SET = -1;
    private static final int ALL_BITS_UNSET = 0;
    private static final int DEFAULT_BORDER_ALPHA = 255;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private static final int DEFAULT_BORDER_RGB = 0;
    private int mAlpha = 255;
    @Nullable
    private Spacing mBorderAlpha;
    @Nullable
    private float[] mBorderCornerRadii;
    @Nullable
    private Spacing mBorderRGB;
    private float mBorderRadius = Float.NaN;
    @Nullable
    private BorderStyle mBorderStyle;
    @Nullable
    private Spacing mBorderWidth;
    private int mColor = 0;
    private boolean mNeedUpdatePathForBorderRadius = false;
    private final Paint mPaint = new Paint(1);
    @Nullable
    private PathEffect mPathEffectForBorderStyle;
    @Nullable
    private Path mPathForBorder;
    @Nullable
    private Path mPathForBorderRadius;
    @Nullable
    private Path mPathForBorderRadiusOutline;
    @Nullable
    private RectF mTempRectForBorderRadius;
    @Nullable
    private RectF mTempRectForBorderRadiusOutline;

    private enum BorderStyle {
        SOLID,
        DASHED,
        DOTTED;

        @Nullable
        public PathEffect getPathEffect(float f) {
            switch (this) {
                case SOLID:
                    return null;
                case DASHED:
                    float f2 = f * 3.0f;
                    return new DashPathEffect(new float[]{f2, f2, f2, f2}, 0.0f);
                case DOTTED:
                    return new DashPathEffect(new float[]{f, f, f, f}, 0.0f);
                default:
                    return null;
            }
        }
    }

    private static int colorFromAlphaAndRGBComponents(float f, float f2) {
        return ((((int) f) << 24) & -16777216) | (((int) f2) & ViewCompat.MEASURED_SIZE_MASK);
    }

    private static int fastBorderCompatibleColorOrZero(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        int i9 = -1;
        int i10 = (i > 0 ? i5 : -1) & (i2 > 0 ? i6 : -1) & (i3 > 0 ? i7 : -1);
        if (i4 > 0) {
            i9 = i8;
        }
        int i11 = i9 & i10;
        if (i <= 0) {
            i5 = 0;
        }
        if (i2 <= 0) {
            i6 = 0;
        }
        int i12 = i5 | i6;
        if (i3 <= 0) {
            i7 = 0;
        }
        int i13 = i12 | i7;
        if (i4 <= 0) {
            i8 = 0;
        }
        if (i11 == (i13 | i8)) {
            return i11;
        }
        return 0;
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void draw(Canvas canvas) {
        updatePathEffect();
        if (!(this.mBorderCornerRadii != null || (!YogaConstants.isUndefined(this.mBorderRadius) && this.mBorderRadius > 0.0f))) {
            drawRectangularBackgroundWithBorders(canvas);
        } else {
            drawRoundedBackgroundWithBorders(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mNeedUpdatePathForBorderRadius = true;
    }

    public void setAlpha(int i) {
        if (i != this.mAlpha) {
            this.mAlpha = i;
            invalidateSelf();
        }
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public int getOpacity() {
        return ColorUtil.getOpacityFromColor(ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha));
    }

    public void getOutline(Outline outline) {
        if (VERSION.SDK_INT < 21) {
            super.getOutline(outline);
            return;
        }
        if ((YogaConstants.isUndefined(this.mBorderRadius) || this.mBorderRadius <= 0.0f) && this.mBorderCornerRadii == null) {
            outline.setRect(getBounds());
        } else {
            updatePath();
            outline.setConvexPath(this.mPathForBorderRadiusOutline);
        }
    }

    public void setBorderWidth(int i, float f) {
        if (this.mBorderWidth == null) {
            this.mBorderWidth = new Spacing();
        }
        if (!FloatUtil.floatsEqual(this.mBorderWidth.getRaw(i), f)) {
            this.mBorderWidth.set(i, f);
            if (i == 8) {
                this.mNeedUpdatePathForBorderRadius = true;
            }
            invalidateSelf();
        }
    }

    public void setBorderColor(int i, float f, float f2) {
        setBorderRGB(i, f);
        setBorderAlpha(i, f2);
    }

    private void setBorderRGB(int i, float f) {
        if (this.mBorderRGB == null) {
            this.mBorderRGB = new Spacing(0.0f);
        }
        if (!FloatUtil.floatsEqual(this.mBorderRGB.getRaw(i), f)) {
            this.mBorderRGB.set(i, f);
            invalidateSelf();
        }
    }

    private void setBorderAlpha(int i, float f) {
        if (this.mBorderAlpha == null) {
            this.mBorderAlpha = new Spacing(255.0f);
        }
        if (!FloatUtil.floatsEqual(this.mBorderAlpha.getRaw(i), f)) {
            this.mBorderAlpha.set(i, f);
            invalidateSelf();
        }
    }

    public void setBorderStyle(@Nullable String str) {
        BorderStyle borderStyle;
        if (str == null) {
            borderStyle = null;
        } else {
            borderStyle = BorderStyle.valueOf(str.toUpperCase(Locale.US));
        }
        if (this.mBorderStyle != borderStyle) {
            this.mBorderStyle = borderStyle;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public void setRadius(float f) {
        if (!FloatUtil.floatsEqual(this.mBorderRadius, f)) {
            this.mBorderRadius = f;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public void setRadius(float f, int i) {
        if (this.mBorderCornerRadii == null) {
            this.mBorderCornerRadii = new float[4];
            Arrays.fill(this.mBorderCornerRadii, Float.NaN);
        }
        if (!FloatUtil.floatsEqual(this.mBorderCornerRadii[i], f)) {
            this.mBorderCornerRadii[i] = f;
            this.mNeedUpdatePathForBorderRadius = true;
            invalidateSelf();
        }
    }

    public float getRadius() {
        return this.mBorderRadius;
    }

    public void setColor(int i) {
        this.mColor = i;
        invalidateSelf();
    }

    @VisibleForTesting
    public int getColor() {
        return this.mColor;
    }

    private void drawRoundedBackgroundWithBorders(Canvas canvas) {
        updatePath();
        int multiplyColorAlpha = ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha);
        if (Color.alpha(multiplyColorAlpha) != 0) {
            this.mPaint.setColor(multiplyColorAlpha);
            this.mPaint.setStyle(Style.FILL);
            canvas.drawPath(this.mPathForBorderRadius, this.mPaint);
        }
        float fullBorderWidth = getFullBorderWidth();
        if (fullBorderWidth > 0.0f) {
            this.mPaint.setColor(ColorUtil.multiplyColorAlpha(getFullBorderColor(), this.mAlpha));
            this.mPaint.setStyle(Style.STROKE);
            this.mPaint.setStrokeWidth(fullBorderWidth);
            canvas.drawPath(this.mPathForBorderRadius, this.mPaint);
        }
    }

    private void updatePath() {
        if (this.mNeedUpdatePathForBorderRadius) {
            this.mNeedUpdatePathForBorderRadius = false;
            if (this.mPathForBorderRadius == null) {
                this.mPathForBorderRadius = new Path();
                this.mTempRectForBorderRadius = new RectF();
                this.mPathForBorderRadiusOutline = new Path();
                this.mTempRectForBorderRadiusOutline = new RectF();
            }
            this.mPathForBorderRadius.reset();
            this.mPathForBorderRadiusOutline.reset();
            this.mTempRectForBorderRadius.set(getBounds());
            this.mTempRectForBorderRadiusOutline.set(getBounds());
            float fullBorderWidth = getFullBorderWidth();
            if (fullBorderWidth > 0.0f) {
                float f = fullBorderWidth * 0.5f;
                this.mTempRectForBorderRadius.inset(f, f);
            }
            float f2 = !YogaConstants.isUndefined(this.mBorderRadius) ? this.mBorderRadius : 0.0f;
            float f3 = (this.mBorderCornerRadii == null || YogaConstants.isUndefined(this.mBorderCornerRadii[0])) ? f2 : this.mBorderCornerRadii[0];
            float f4 = (this.mBorderCornerRadii == null || YogaConstants.isUndefined(this.mBorderCornerRadii[1])) ? f2 : this.mBorderCornerRadii[1];
            float f5 = (this.mBorderCornerRadii == null || YogaConstants.isUndefined(this.mBorderCornerRadii[2])) ? f2 : this.mBorderCornerRadii[2];
            if (this.mBorderCornerRadii != null && !YogaConstants.isUndefined(this.mBorderCornerRadii[3])) {
                f2 = this.mBorderCornerRadii[3];
            }
            this.mPathForBorderRadius.addRoundRect(this.mTempRectForBorderRadius, new float[]{f3, f3, f4, f4, f5, f5, f2, f2}, Direction.CW);
            float f6 = this.mBorderWidth != null ? this.mBorderWidth.get(8) / 2.0f : 0.0f;
            float f7 = f3 + f6;
            float f8 = f4 + f6;
            float f9 = f5 + f6;
            float f10 = f2 + f6;
            this.mPathForBorderRadiusOutline.addRoundRect(this.mTempRectForBorderRadiusOutline, new float[]{f7, f7, f8, f8, f9, f9, f10, f10}, Direction.CW);
        }
    }

    private void updatePathEffect() {
        this.mPathEffectForBorderStyle = this.mBorderStyle != null ? this.mBorderStyle.getPathEffect(getFullBorderWidth()) : null;
        this.mPaint.setPathEffect(this.mPathEffectForBorderStyle);
    }

    public float getFullBorderWidth() {
        if (this.mBorderWidth == null || YogaConstants.isUndefined(this.mBorderWidth.getRaw(8))) {
            return 0.0f;
        }
        return this.mBorderWidth.getRaw(8);
    }

    private int getFullBorderColor() {
        return colorFromAlphaAndRGBComponents((this.mBorderAlpha == null || YogaConstants.isUndefined(this.mBorderAlpha.getRaw(8))) ? 255.0f : this.mBorderAlpha.getRaw(8), (this.mBorderRGB == null || YogaConstants.isUndefined(this.mBorderRGB.getRaw(8))) ? 0.0f : this.mBorderRGB.getRaw(8));
    }

    private void drawRectangularBackgroundWithBorders(Canvas canvas) {
        int i;
        int i2;
        Canvas canvas2 = canvas;
        int multiplyColorAlpha = ColorUtil.multiplyColorAlpha(this.mColor, this.mAlpha);
        if (Color.alpha(multiplyColorAlpha) != 0) {
            this.mPaint.setColor(multiplyColorAlpha);
            this.mPaint.setStyle(Style.FILL);
            canvas2.drawRect(getBounds(), this.mPaint);
        }
        if (getBorderWidth(0) > 0 || getBorderWidth(1) > 0 || getBorderWidth(2) > 0 || getBorderWidth(3) > 0) {
            Rect bounds = getBounds();
            int borderWidth = getBorderWidth(0);
            int borderWidth2 = getBorderWidth(1);
            int borderWidth3 = getBorderWidth(2);
            int borderWidth4 = getBorderWidth(3);
            int borderColor = getBorderColor(0);
            int borderColor2 = getBorderColor(1);
            int borderColor3 = getBorderColor(2);
            int borderColor4 = getBorderColor(3);
            int i3 = bounds.left;
            int i4 = borderColor;
            int i5 = borderColor;
            int i6 = bounds.top;
            int i7 = i3;
            int i8 = borderColor2;
            int fastBorderCompatibleColorOrZero = fastBorderCompatibleColorOrZero(borderWidth, borderWidth2, borderWidth3, borderWidth4, i4, borderColor2, borderColor3, borderColor4);
            if (fastBorderCompatibleColorOrZero == 0) {
                int i9 = i7;
                int i10 = i6;
                if (this.mPathForBorder == null) {
                    this.mPathForBorder = new Path();
                }
                this.mPaint.setAntiAlias(false);
                int width = bounds.width();
                int height = bounds.height();
                if (borderWidth > 0 && i5 != 0) {
                    this.mPaint.setColor(i5);
                    this.mPathForBorder.reset();
                    float f = (float) i9;
                    float f2 = (float) i10;
                    this.mPathForBorder.moveTo(f, f2);
                    float f3 = (float) (i9 + borderWidth);
                    this.mPathForBorder.lineTo(f3, (float) (i10 + borderWidth2));
                    int i11 = i10 + height;
                    this.mPathForBorder.lineTo(f3, (float) (i11 - borderWidth4));
                    this.mPathForBorder.lineTo(f, (float) i11);
                    this.mPathForBorder.lineTo(f, f2);
                    canvas2.drawPath(this.mPathForBorder, this.mPaint);
                }
                if (borderWidth2 > 0) {
                    int i12 = i8;
                    if (i12 != 0) {
                        this.mPaint.setColor(i12);
                        this.mPathForBorder.reset();
                        float f4 = (float) i9;
                        float f5 = (float) i10;
                        this.mPathForBorder.moveTo(f4, f5);
                        float f6 = (float) (i10 + borderWidth2);
                        this.mPathForBorder.lineTo((float) (i9 + borderWidth), f6);
                        int i13 = i9 + width;
                        this.mPathForBorder.lineTo((float) (i13 - borderWidth3), f6);
                        this.mPathForBorder.lineTo((float) i13, f5);
                        this.mPathForBorder.lineTo(f4, f5);
                        canvas2.drawPath(this.mPathForBorder, this.mPaint);
                    }
                }
                if (borderWidth3 > 0 && borderColor3 != 0) {
                    this.mPaint.setColor(borderColor3);
                    this.mPathForBorder.reset();
                    int i14 = i9 + width;
                    float f7 = (float) i14;
                    float f8 = (float) i10;
                    this.mPathForBorder.moveTo(f7, f8);
                    int i15 = i10 + height;
                    this.mPathForBorder.lineTo(f7, (float) i15);
                    float f9 = (float) (i14 - borderWidth3);
                    this.mPathForBorder.lineTo(f9, (float) (i15 - borderWidth4));
                    this.mPathForBorder.lineTo(f9, (float) (i10 + borderWidth2));
                    this.mPathForBorder.lineTo(f7, f8);
                    canvas2.drawPath(this.mPathForBorder, this.mPaint);
                }
                if (borderWidth4 > 0 && borderColor4 != 0) {
                    this.mPaint.setColor(borderColor4);
                    this.mPathForBorder.reset();
                    float f10 = (float) i9;
                    int i16 = i10 + height;
                    float f11 = (float) i16;
                    this.mPathForBorder.moveTo(f10, f11);
                    int i17 = i9 + width;
                    this.mPathForBorder.lineTo((float) i17, f11);
                    float f12 = (float) (i16 - borderWidth4);
                    this.mPathForBorder.lineTo((float) (i17 - borderWidth3), f12);
                    this.mPathForBorder.lineTo((float) (i9 + borderWidth), f12);
                    this.mPathForBorder.lineTo(f10, f11);
                    canvas2.drawPath(this.mPathForBorder, this.mPaint);
                }
                this.mPaint.setAntiAlias(true);
            } else if (Color.alpha(fastBorderCompatibleColorOrZero) != 0) {
                int i18 = bounds.right;
                int i19 = bounds.bottom;
                this.mPaint.setColor(fastBorderCompatibleColorOrZero);
                if (borderWidth > 0) {
                    i = i7;
                    float f13 = (float) (i19 - borderWidth4);
                    i2 = i6;
                    canvas2.drawRect((float) i7, (float) i6, (float) (i7 + borderWidth), f13, this.mPaint);
                } else {
                    i = i7;
                    i2 = i6;
                }
                if (borderWidth2 > 0) {
                    canvas2.drawRect((float) (i + borderWidth), (float) i2, (float) i18, (float) (i2 + borderWidth2), this.mPaint);
                }
                if (borderWidth3 > 0) {
                    canvas2.drawRect((float) (i18 - borderWidth3), (float) (i2 + borderWidth2), (float) i18, (float) i19, this.mPaint);
                }
                if (borderWidth4 > 0) {
                    canvas2.drawRect((float) i, (float) (i19 - borderWidth4), (float) (i18 - borderWidth3), (float) i19, this.mPaint);
                }
            }
        }
    }

    private int getBorderWidth(int i) {
        if (this.mBorderWidth != null) {
            return Math.round(this.mBorderWidth.get(i));
        }
        return 0;
    }

    private int getBorderColor(int i) {
        return colorFromAlphaAndRGBComponents(this.mBorderAlpha != null ? this.mBorderAlpha.get(i) : 255.0f, this.mBorderRGB != null ? this.mBorderRGB.get(i) : 0.0f);
    }
}
