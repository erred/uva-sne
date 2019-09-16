package com.facebook.react.flat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.support.p000v4.view.ViewCompat;
import javax.annotation.Nullable;

abstract class AbstractDrawBorder extends AbstractDrawCommand {
    private static final int BORDER_PATH_DIRTY = 1;
    private static final Paint PAINT = new Paint(1);
    private static final RectF TMP_RECT = new RectF();
    private int mBorderColor = ViewCompat.MEASURED_STATE_MASK;
    private float mBorderRadius;
    private float mBorderWidth;
    @Nullable
    private Path mPathForBorderRadius;
    private int mSetPropertiesFlag;

    /* access modifiers changed from: protected */
    @Nullable
    public PathEffect getPathEffectForBorderStyle() {
        return null;
    }

    AbstractDrawBorder() {
    }

    static {
        PAINT.setStyle(Style.STROKE);
    }

    public final void setBorderWidth(float f) {
        this.mBorderWidth = f;
        setFlag(1);
    }

    public final float getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderRadius(float f) {
        this.mBorderRadius = f;
        setFlag(1);
    }

    public final float getBorderRadius() {
        return this.mBorderRadius;
    }

    public final void setBorderColor(int i) {
        this.mBorderColor = i;
    }

    public final int getBorderColor() {
        return this.mBorderColor;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChanged() {
        setFlag(1);
    }

    /* access modifiers changed from: protected */
    public final void drawBorders(Canvas canvas) {
        if (this.mBorderWidth >= 0.5f && this.mBorderColor != 0) {
            PAINT.setColor(this.mBorderColor);
            PAINT.setStrokeWidth(this.mBorderWidth);
            PAINT.setPathEffect(getPathEffectForBorderStyle());
            canvas.drawPath(getPathForBorderRadius(), PAINT);
        }
    }

    /* access modifiers changed from: protected */
    public final void updatePath(Path path, float f) {
        path.reset();
        TMP_RECT.set(getLeft() + f, getTop() + f, getRight() - f, getBottom() - f);
        path.addRoundRect(TMP_RECT, this.mBorderRadius, this.mBorderRadius, Direction.CW);
    }

    /* access modifiers changed from: protected */
    public final boolean isFlagSet(int i) {
        return (this.mSetPropertiesFlag & i) == i;
    }

    /* access modifiers changed from: protected */
    public final void setFlag(int i) {
        this.mSetPropertiesFlag = i | this.mSetPropertiesFlag;
    }

    /* access modifiers changed from: protected */
    public final void resetFlag(int i) {
        this.mSetPropertiesFlag = (i ^ -1) & this.mSetPropertiesFlag;
    }

    /* access modifiers changed from: protected */
    public final Path getPathForBorderRadius() {
        if (isFlagSet(1)) {
            if (this.mPathForBorderRadius == null) {
                this.mPathForBorderRadius = new Path();
            }
            updatePath(this.mPathForBorderRadius, this.mBorderWidth * 0.5f);
            resetFlag(1);
        }
        return this.mPathForBorderRadius;
    }
}
