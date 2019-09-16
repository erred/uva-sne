package com.facebook.react.flat;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.p000v4.view.ViewCompat;
import javax.annotation.Nullable;

final class DrawBorder extends AbstractDrawBorder {
    private static final int ALL_BITS_SET = -1;
    private static final int ALL_BITS_UNSET = 0;
    private static final int BORDER_BOTTOM_COLOR_SET = 16;
    private static final int BORDER_LEFT_COLOR_SET = 2;
    private static final int BORDER_PATH_EFFECT_DIRTY = 32;
    private static final int BORDER_RIGHT_COLOR_SET = 8;
    private static final int BORDER_STYLE_DASHED = 2;
    private static final int BORDER_STYLE_DOTTED = 1;
    private static final int BORDER_STYLE_SOLID = 0;
    private static final int BORDER_TOP_COLOR_SET = 4;
    private static final Paint PAINT = new Paint(1);
    private static final float[] TMP_FLOAT_ARRAY = new float[4];
    private int mBackgroundColor;
    private int mBorderBottomColor;
    private float mBorderBottomWidth;
    private int mBorderLeftColor;
    private float mBorderLeftWidth;
    private int mBorderRightColor;
    private float mBorderRightWidth;
    private int mBorderStyle = 0;
    private int mBorderTopColor;
    private float mBorderTopWidth;
    @Nullable
    private DashPathEffect mPathEffectForBorderStyle;
    @Nullable
    private Path mPathForBorder;

    private static int fastBorderCompatibleColorOrZero(float f, float f2, float f3, float f4, int i, int i2, int i3, int i4) {
        int i5 = -1;
        int i6 = (f > 0.0f ? i : -1) & (f2 > 0.0f ? i2 : -1) & (f3 > 0.0f ? i3 : -1);
        if (f4 > 0.0f) {
            i5 = i4;
        }
        int i7 = i6 & i5;
        if (f <= 0.0f) {
            i = 0;
        }
        if (f2 <= 0.0f) {
            i2 = 0;
        }
        int i8 = i | i2;
        if (f3 <= 0.0f) {
            i3 = 0;
        }
        int i9 = i8 | i3;
        if (f4 <= 0.0f) {
            i4 = 0;
        }
        if (i7 == (i9 | i4)) {
            return i7;
        }
        return 0;
    }

    private static float resolveWidth(float f, float f2) {
        return (f == 0.0f || f != f) ? f2 : f;
    }

    DrawBorder() {
    }

    public void setBorderWidth(int i, float f) {
        if (i != 8) {
            switch (i) {
                case 0:
                    this.mBorderLeftWidth = f;
                    return;
                case 1:
                    this.mBorderTopWidth = f;
                    return;
                case 2:
                    this.mBorderRightWidth = f;
                    return;
                case 3:
                    this.mBorderBottomWidth = f;
                    return;
                default:
                    return;
            }
        } else {
            setBorderWidth(f);
        }
    }

    public float getBorderWidth(int i) {
        if (i == 8) {
            return getBorderWidth();
        }
        switch (i) {
            case 0:
                return this.mBorderLeftWidth;
            case 1:
                return this.mBorderTopWidth;
            case 2:
                return this.mBorderRightWidth;
            case 3:
                return this.mBorderBottomWidth;
            default:
                return 0.0f;
        }
    }

    public void setBorderStyle(@Nullable String str) {
        if ("dotted".equals(str)) {
            this.mBorderStyle = 1;
        } else if ("dashed".equals(str)) {
            this.mBorderStyle = 2;
        } else {
            this.mBorderStyle = 0;
        }
        setFlag(32);
    }

    public void resetBorderColor(int i) {
        if (i != 8) {
            switch (i) {
                case 0:
                    resetFlag(2);
                    return;
                case 1:
                    resetFlag(4);
                    return;
                case 2:
                    resetFlag(8);
                    return;
                case 3:
                    resetFlag(16);
                    return;
                default:
                    return;
            }
        } else {
            setBorderColor(ViewCompat.MEASURED_STATE_MASK);
        }
    }

    public void setBorderColor(int i, int i2) {
        if (i != 8) {
            switch (i) {
                case 0:
                    this.mBorderLeftColor = i2;
                    setFlag(2);
                    return;
                case 1:
                    this.mBorderTopColor = i2;
                    setFlag(4);
                    return;
                case 2:
                    this.mBorderRightColor = i2;
                    setFlag(8);
                    return;
                case 3:
                    this.mBorderBottomColor = i2;
                    setFlag(16);
                    return;
                default:
                    return;
            }
        } else {
            setBorderColor(i2);
        }
    }

    public int getBorderColor(int i) {
        int borderColor = getBorderColor();
        if (i == 8) {
            return borderColor;
        }
        switch (i) {
            case 0:
                return resolveBorderColor(2, this.mBorderLeftColor, borderColor);
            case 1:
                return resolveBorderColor(4, this.mBorderTopColor, borderColor);
            case 2:
                return resolveBorderColor(8, this.mBorderRightColor, borderColor);
            case 3:
                return resolveBorderColor(16, this.mBorderBottomColor, borderColor);
            default:
                return borderColor;
        }
    }

    public void setBackgroundColor(int i) {
        this.mBackgroundColor = i;
    }

    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (getBorderRadius() >= 0.5f || getPathEffectForBorderStyle() != null) {
            drawRoundedBorders(canvas);
        } else {
            drawRectangularBorders(canvas);
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public DashPathEffect getPathEffectForBorderStyle() {
        if (isFlagSet(32)) {
            switch (this.mBorderStyle) {
                case 1:
                    this.mPathEffectForBorderStyle = createDashPathEffect(getBorderWidth());
                    break;
                case 2:
                    this.mPathEffectForBorderStyle = createDashPathEffect(getBorderWidth() * 3.0f);
                    break;
                default:
                    this.mPathEffectForBorderStyle = null;
                    break;
            }
            resetFlag(32);
        }
        return this.mPathEffectForBorderStyle;
    }

    private void drawRoundedBorders(Canvas canvas) {
        if (this.mBackgroundColor != 0) {
            PAINT.setColor(this.mBackgroundColor);
            canvas.drawPath(getPathForBorderRadius(), PAINT);
        }
        drawBorders(canvas);
    }

    private void drawRectangularBorders(Canvas canvas) {
        int i;
        int i2;
        int i3;
        Canvas canvas2 = canvas;
        int borderColor = getBorderColor();
        float borderWidth = getBorderWidth();
        float top = getTop();
        float resolveWidth = resolveWidth(this.mBorderTopWidth, borderWidth);
        float f = top + resolveWidth;
        int resolveBorderColor = resolveBorderColor(4, this.mBorderTopColor, borderColor);
        float bottom = getBottom();
        float resolveWidth2 = resolveWidth(this.mBorderBottomWidth, borderWidth);
        float f2 = bottom - resolveWidth2;
        int resolveBorderColor2 = resolveBorderColor(16, this.mBorderBottomColor, borderColor);
        float left = getLeft();
        float resolveWidth3 = resolveWidth(this.mBorderLeftWidth, borderWidth);
        float f3 = left + resolveWidth3;
        int resolveBorderColor3 = resolveBorderColor(2, this.mBorderLeftColor, borderColor);
        float right = getRight();
        float resolveWidth4 = resolveWidth(this.mBorderRightWidth, borderWidth);
        float f4 = right - resolveWidth4;
        int resolveBorderColor4 = resolveBorderColor(8, this.mBorderRightColor, borderColor);
        int fastBorderCompatibleColorOrZero = fastBorderCompatibleColorOrZero(resolveWidth3, resolveWidth, resolveWidth4, resolveWidth2, resolveBorderColor3, resolveBorderColor, resolveBorderColor4, resolveBorderColor2);
        if (fastBorderCompatibleColorOrZero == 0) {
            if (this.mPathForBorder == null) {
                this.mPathForBorder = new Path();
            }
            if (Color.alpha(this.mBackgroundColor) != 0) {
                PAINT.setColor(this.mBackgroundColor);
                canvas2.drawRect(left, top, right, bottom, PAINT);
            }
            if (resolveWidth == 0.0f || Color.alpha(resolveBorderColor) == 0) {
                i3 = resolveBorderColor4;
                i2 = resolveBorderColor3;
                i = resolveBorderColor2;
            } else {
                PAINT.setColor(resolveBorderColor);
                i3 = resolveBorderColor4;
                i2 = resolveBorderColor3;
                i = resolveBorderColor2;
                updatePathForTopBorder(this.mPathForBorder, top, f, left, f3, right, f4);
                canvas2.drawPath(this.mPathForBorder, PAINT);
            }
            if (!(resolveWidth2 == 0.0f || Color.alpha(i) == 0)) {
                PAINT.setColor(i);
                updatePathForBottomBorder(this.mPathForBorder, bottom, f2, left, f3, right, f4);
                canvas2.drawPath(this.mPathForBorder, PAINT);
            }
            if (!(resolveWidth3 == 0.0f || Color.alpha(i2) == 0)) {
                PAINT.setColor(i2);
                updatePathForLeftBorder(this.mPathForBorder, top, f, bottom, f2, left, f3);
                canvas2.drawPath(this.mPathForBorder, PAINT);
            }
            if (resolveWidth4 != 0.0f && Color.alpha(i3) != 0) {
                PAINT.setColor(i3);
                updatePathForRightBorder(this.mPathForBorder, top, f, bottom, f2, right, f4);
                canvas2.drawPath(this.mPathForBorder, PAINT);
            }
        } else if (Color.alpha(fastBorderCompatibleColorOrZero) != 0) {
            if (Color.alpha(this.mBackgroundColor) != 0) {
                PAINT.setColor(this.mBackgroundColor);
                if (Color.alpha(fastBorderCompatibleColorOrZero) == 255) {
                    canvas2.drawRect(f3, f, f4, f2, PAINT);
                } else {
                    canvas2.drawRect(left, top, right, bottom, PAINT);
                }
            }
            PAINT.setColor(fastBorderCompatibleColorOrZero);
            if (resolveWidth3 > 0.0f) {
                canvas2.drawRect(left, top, f3, f2, PAINT);
            }
            if (resolveWidth > 0.0f) {
                canvas2.drawRect(f3, top, right, f, PAINT);
            }
            if (resolveWidth4 > 0.0f) {
                canvas2.drawRect(f4, f, right, bottom, PAINT);
            }
            if (resolveWidth2 > 0.0f) {
                canvas2.drawRect(left, f2, f4, bottom, PAINT);
            }
        }
    }

    private static void updatePathForTopBorder(Path path, float f, float f2, float f3, float f4, float f5, float f6) {
        path.reset();
        path.moveTo(f3, f);
        path.lineTo(f4, f2);
        path.lineTo(f6, f2);
        path.lineTo(f5, f);
        path.lineTo(f3, f);
    }

    private static void updatePathForBottomBorder(Path path, float f, float f2, float f3, float f4, float f5, float f6) {
        path.reset();
        path.moveTo(f3, f);
        path.lineTo(f5, f);
        path.lineTo(f6, f2);
        path.lineTo(f4, f2);
        path.lineTo(f3, f);
    }

    private static void updatePathForLeftBorder(Path path, float f, float f2, float f3, float f4, float f5, float f6) {
        path.reset();
        path.moveTo(f5, f);
        path.lineTo(f6, f2);
        path.lineTo(f6, f4);
        path.lineTo(f5, f3);
        path.lineTo(f5, f);
    }

    private static void updatePathForRightBorder(Path path, float f, float f2, float f3, float f4, float f5, float f6) {
        path.reset();
        path.moveTo(f5, f);
        path.lineTo(f5, f3);
        path.lineTo(f6, f4);
        path.lineTo(f6, f2);
        path.lineTo(f5, f);
    }

    private int resolveBorderColor(int i, int i2, int i3) {
        return isFlagSet(i) ? i2 : i3;
    }

    private static DashPathEffect createDashPathEffect(float f) {
        for (int i = 0; i < 4; i++) {
            TMP_FLOAT_ARRAY[i] = f;
        }
        return new DashPathEffect(TMP_FLOAT_ARRAY, 0.0f);
    }
}
