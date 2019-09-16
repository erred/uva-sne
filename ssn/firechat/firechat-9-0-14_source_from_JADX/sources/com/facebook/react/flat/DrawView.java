package com.facebook.react.flat;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import javax.annotation.Nullable;

final class DrawView extends AbstractDrawCommand {
    public static final DrawView[] EMPTY_ARRAY = new DrawView[0];
    static final float MINIMUM_ROUNDED_CLIPPING_VALUE = 0.5f;
    private final RectF TMP_RECT = new RectF();
    private float mClipRadius;
    float mLogicalBottom;
    float mLogicalLeft;
    float mLogicalRight;
    float mLogicalTop;
    @Nullable
    private Path mPath;
    boolean mWasMounted;
    final int reactTag;

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
    }

    public DrawView(int i) {
        this.reactTag = i;
    }

    public DrawView collectDrawView(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13) {
        boolean z;
        float f14 = f5;
        float f15 = f6;
        float f16 = f7;
        float f17 = f8;
        float f18 = f9;
        float f19 = f10;
        float f20 = f11;
        float f21 = f12;
        float f22 = f13;
        if (!isFrozen()) {
            setBounds(f, f2, f3, f4);
            setClipBounds(f18, f19, f20, f21);
            setClipRadius(f22);
            setLogicalBounds(f14, f15, f16, f17);
            freeze();
            return this;
        }
        boolean boundsMatch = boundsMatch(f, f2, f3, f4);
        boolean clipBoundsMatch = clipBoundsMatch(f18, f19, f20, f21);
        boolean z2 = this.mClipRadius == f22;
        boolean logicalBoundsMatch = logicalBoundsMatch(f14, f15, f16, f17);
        if (boundsMatch && clipBoundsMatch && z2 && logicalBoundsMatch) {
            return this;
        }
        DrawView drawView = (DrawView) mutableCopy();
        if (!boundsMatch) {
            z = boundsMatch;
            drawView.setBounds(f, f2, f3, f4);
        } else {
            z = boundsMatch;
        }
        if (!clipBoundsMatch) {
            drawView.setClipBounds(f18, f19, f20, f21);
        }
        if (!logicalBoundsMatch) {
            drawView.setLogicalBounds(f14, f15, f16, f17);
        }
        if (!z2 || !z) {
            drawView.setClipRadius(f13);
        }
        drawView.mWasMounted = false;
        drawView.freeze();
        return drawView;
    }

    private boolean logicalBoundsMatch(float f, float f2, float f3, float f4) {
        return f == this.mLogicalLeft && f2 == this.mLogicalTop && f3 == this.mLogicalRight && f4 == this.mLogicalBottom;
    }

    private void setLogicalBounds(float f, float f2, float f3, float f4) {
        this.mLogicalLeft = f;
        this.mLogicalTop = f2;
        this.mLogicalRight = f3;
        this.mLogicalBottom = f4;
    }

    public void draw(FlatViewGroup flatViewGroup, Canvas canvas) {
        onPreDraw(flatViewGroup, canvas);
        if (this.mNeedsClipping || this.mClipRadius > 0.5f) {
            canvas.save(2);
            applyClipping(canvas);
            flatViewGroup.drawNextChild(canvas);
            canvas.restore();
            return;
        }
        flatViewGroup.drawNextChild(canvas);
    }

    /* access modifiers changed from: 0000 */
    public void setClipRadius(float f) {
        this.mClipRadius = f;
        if (f > 0.5f) {
            updateClipPath();
        } else {
            this.mPath = null;
        }
    }

    private void updateClipPath() {
        this.mPath = new Path();
        this.TMP_RECT.set(getLeft(), getTop(), getRight(), getBottom());
        this.mPath.addRoundRect(this.TMP_RECT, this.mClipRadius, this.mClipRadius, Direction.CW);
    }

    /* access modifiers changed from: protected */
    public void applyClipping(Canvas canvas) {
        if (this.mClipRadius > 0.5f) {
            canvas.clipPath(this.mPath);
        } else {
            super.applyClipping(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onDebugDraw(FlatViewGroup flatViewGroup, Canvas canvas) {
        flatViewGroup.debugDrawNextChild(canvas);
    }

    /* access modifiers changed from: protected */
    public void onDebugDrawHighlight(Canvas canvas) {
        if (this.mPath != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("borderRadius: ");
            sb.append(this.mClipRadius);
            debugDrawWarningHighlight(canvas, sb.toString());
        } else if (!boundsMatch(this.mLogicalLeft, this.mLogicalTop, this.mLogicalRight, this.mLogicalBottom)) {
            StringBuilder sb2 = new StringBuilder("Overflow: { ");
            String[] strArr = {"left: ", "top: ", "right: ", "bottom: "};
            float[] fArr = {getLeft() - this.mLogicalLeft, getTop() - this.mLogicalTop, this.mLogicalRight - getRight(), this.mLogicalBottom - getBottom()};
            for (int i = 0; i < 4; i++) {
                if (fArr[i] != 0.0f) {
                    sb2.append(strArr[i]);
                    sb2.append(fArr[i]);
                    sb2.append(", ");
                }
            }
            sb2.append("}");
            debugDrawCautionHighlight(canvas, sb2.toString());
        }
    }
}
