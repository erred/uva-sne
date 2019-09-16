package com.getbase.floatingactionbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

public class AddFloatingActionButton extends FloatingActionButton {
    int mPlusColor;

    public AddFloatingActionButton(Context context) {
        this(context, null);
    }

    public AddFloatingActionButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AddFloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: 0000 */
    public void init(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C1112R.styleable.AddFloatingActionButton, 0, 0);
        this.mPlusColor = obtainStyledAttributes.getColor(C1112R.styleable.AddFloatingActionButton_fab_plusIconColor, getColor(17170443));
        obtainStyledAttributes.recycle();
        super.init(context, attributeSet);
    }

    public int getPlusColor() {
        return this.mPlusColor;
    }

    public void setPlusColorResId(@ColorRes int i) {
        setPlusColor(getColor(i));
    }

    public void setPlusColor(int i) {
        if (this.mPlusColor != i) {
            this.mPlusColor = i;
            updateBackground();
        }
    }

    public void setIcon(@DrawableRes int i) {
        throw new UnsupportedOperationException("Use FloatingActionButton if you want to use custom icon");
    }

    /* access modifiers changed from: 0000 */
    public Drawable getIconDrawable() {
        final float dimension = getDimension(C1112R.dimen.fab_icon_size);
        final float f = dimension / 2.0f;
        final float dimension2 = getDimension(C1112R.dimen.fab_plus_icon_stroke) / 2.0f;
        final float dimension3 = (dimension - getDimension(C1112R.dimen.fab_plus_icon_size)) / 2.0f;
        C11061 r1 = new Shape() {
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawRect(dimension3, f - dimension2, dimension - dimension3, dimension2 + f, paint);
                canvas.drawRect(f - dimension2, dimension3, f + dimension2, dimension - dimension3, paint);
            }
        };
        ShapeDrawable shapeDrawable = new ShapeDrawable(r1);
        Paint paint = shapeDrawable.getPaint();
        paint.setColor(this.mPlusColor);
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        return shapeDrawable;
    }
}
