package com.facebook.react.flat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import org.matrix.olm.OlmException;

abstract class AbstractDrawCommand extends DrawCommand implements Cloneable {
    private static Paint sDebugHighlightOverlayText;
    private static Paint sDebugHighlightRed;
    private static Paint sDebugHighlightYellow;
    private float mBottom;
    private float mClipBottom;
    private float mClipLeft;
    private float mClipRight;
    private float mClipTop;
    private boolean mFrozen;
    private float mLeft;
    protected boolean mNeedsClipping;
    private float mRight;
    private float mTop;

    protected static int getDebugBorderColor() {
        return -16711681;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChanged() {
    }

    /* access modifiers changed from: protected */
    public void onDebugDrawHighlight(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public abstract void onDraw(Canvas canvas);

    /* access modifiers changed from: protected */
    public void onPreDraw(FlatViewGroup flatViewGroup, Canvas canvas) {
    }

    AbstractDrawCommand() {
    }

    public final boolean clipBoundsMatch(float f, float f2, float f3, float f4) {
        return this.mClipLeft == f && this.mClipTop == f2 && this.mClipRight == f3 && this.mClipBottom == f4;
    }

    /* access modifiers changed from: protected */
    public final void setClipBounds(float f, float f2, float f3, float f4) {
        this.mClipLeft = f;
        this.mClipTop = f2;
        this.mClipRight = f3;
        this.mClipBottom = f4;
        this.mNeedsClipping = this.mClipLeft != Float.NEGATIVE_INFINITY;
    }

    public final float getClipLeft() {
        return this.mClipLeft;
    }

    public final float getClipTop() {
        return this.mClipTop;
    }

    public final float getClipRight() {
        return this.mClipRight;
    }

    public final float getClipBottom() {
        return this.mClipBottom;
    }

    /* access modifiers changed from: protected */
    public void applyClipping(Canvas canvas) {
        canvas.clipRect(this.mClipLeft, this.mClipTop, this.mClipRight, this.mClipBottom);
    }

    public void draw(FlatViewGroup flatViewGroup, Canvas canvas) {
        onPreDraw(flatViewGroup, canvas);
        if (!this.mNeedsClipping || !shouldClip()) {
            onDraw(canvas);
            return;
        }
        canvas.save(2);
        applyClipping(canvas);
        onDraw(canvas);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public String getDebugName() {
        return getClass().getSimpleName().substring(4);
    }

    private void initDebugHighlightResources(FlatViewGroup flatViewGroup) {
        if (sDebugHighlightRed == null) {
            sDebugHighlightRed = new Paint();
            sDebugHighlightRed.setARGB(75, 255, 0, 0);
        }
        if (sDebugHighlightYellow == null) {
            sDebugHighlightYellow = new Paint();
            sDebugHighlightYellow.setARGB(100, 255, OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_FIRST_KNOWN_INDEX, 0);
        }
        if (sDebugHighlightOverlayText == null) {
            sDebugHighlightOverlayText = new Paint();
            sDebugHighlightOverlayText.setAntiAlias(true);
            sDebugHighlightOverlayText.setARGB(200, 50, 50, 50);
            sDebugHighlightOverlayText.setTextAlign(Align.RIGHT);
            sDebugHighlightOverlayText.setTypeface(Typeface.MONOSPACE);
            sDebugHighlightOverlayText.setTextSize((float) flatViewGroup.dipsToPixels(9));
        }
    }

    private void debugDrawHighlightRect(Canvas canvas, Paint paint, String str) {
        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), paint);
        canvas.drawText(str, getRight() - 5.0f, getBottom() - 5.0f, sDebugHighlightOverlayText);
    }

    /* access modifiers changed from: protected */
    public void debugDrawWarningHighlight(Canvas canvas, String str) {
        debugDrawHighlightRect(canvas, sDebugHighlightRed, str);
    }

    /* access modifiers changed from: protected */
    public void debugDrawCautionHighlight(Canvas canvas, String str) {
        debugDrawHighlightRect(canvas, sDebugHighlightYellow, str);
    }

    public final void debugDraw(FlatViewGroup flatViewGroup, Canvas canvas) {
        onDebugDraw(flatViewGroup, canvas);
    }

    /* access modifiers changed from: protected */
    public void onDebugDraw(FlatViewGroup flatViewGroup, Canvas canvas) {
        flatViewGroup.debugDrawNamedRect(canvas, getDebugBorderColor(), getDebugName(), this.mLeft, this.mTop, this.mRight, this.mBottom);
    }

    public AbstractDrawCommand updateBoundsAndFreeze(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        if (this.mFrozen) {
            boolean boundsMatch = boundsMatch(f, f2, f3, f4);
            boolean clipBoundsMatch = clipBoundsMatch(f5, f6, f7, f8);
            if (boundsMatch && clipBoundsMatch) {
                return this;
            }
            try {
                AbstractDrawCommand abstractDrawCommand = (AbstractDrawCommand) clone();
                if (!boundsMatch) {
                    abstractDrawCommand.setBounds(f, f2, f3, f4);
                }
                if (!clipBoundsMatch) {
                    abstractDrawCommand.setClipBounds(f5, f6, f7, f8);
                }
                return abstractDrawCommand;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        } else {
            setBounds(f, f2, f3, f4);
            setClipBounds(f5, f6, f7, f8);
            this.mFrozen = true;
            return this;
        }
    }

    public final AbstractDrawCommand mutableCopy() {
        try {
            AbstractDrawCommand abstractDrawCommand = (AbstractDrawCommand) super.clone();
            abstractDrawCommand.mFrozen = false;
            return abstractDrawCommand;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public final boolean isFrozen() {
        return this.mFrozen;
    }

    public final void freeze() {
        this.mFrozen = true;
    }

    public final float getLeft() {
        return this.mLeft;
    }

    public final float getTop() {
        return this.mTop;
    }

    public final float getRight() {
        return this.mRight;
    }

    public final float getBottom() {
        return this.mBottom;
    }

    /* access modifiers changed from: protected */
    public boolean shouldClip() {
        return this.mLeft < getClipLeft() || this.mTop < getClipTop() || this.mRight > getClipRight() || this.mBottom > getClipBottom();
    }

    /* access modifiers changed from: protected */
    public final void setBounds(float f, float f2, float f3, float f4) {
        this.mLeft = f;
        this.mTop = f2;
        this.mRight = f3;
        this.mBottom = f4;
        onBoundsChanged();
    }

    /* access modifiers changed from: protected */
    public final boolean boundsMatch(float f, float f2, float f3, float f4) {
        return this.mLeft == f && this.mTop == f2 && this.mRight == f3 && this.mBottom == f4;
    }
}
