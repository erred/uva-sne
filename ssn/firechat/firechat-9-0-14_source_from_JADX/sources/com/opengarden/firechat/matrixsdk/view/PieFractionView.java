package com.opengarden.firechat.matrixsdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.opengarden.firechat.C1299R;

public class PieFractionView extends View {
    private static final int START_ANGLE = -90;
    private int fraction = 0;
    private final Paint paint;
    private final int powerColor;
    private final RectF rectF;
    private final int restColor;

    @SuppressLint({"deprecation"})
    public int fillColor() {
        return getResources().getColor(C1299R.color.pie_fraction_fill);
    }

    @SuppressLint({"deprecation"})
    public int getRestColor() {
        return getResources().getColor(C1299R.color.pie_fraction_rest);
    }

    public PieFractionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16842996, 16842997});
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
        this.rectF = new RectF(0.0f, 0.0f, (float) dimensionPixelSize, (float) dimensionPixelSize2);
        this.paint = new Paint();
        this.powerColor = fillColor();
        this.restColor = getRestColor();
    }

    public void setFraction(int i) {
        if (i >= 0 && i <= 100 && this.fraction != i) {
            this.fraction = i;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int i = (this.fraction * 360) / 100;
        this.paint.setColor(this.powerColor);
        Canvas canvas2 = canvas;
        canvas2.drawArc(this.rectF, -90.0f, (float) i, true, this.paint);
        this.paint.setColor(this.restColor);
        canvas2.drawArc(this.rectF, (float) (i + START_ANGLE), (float) (360 - i), true, this.paint);
    }
}
