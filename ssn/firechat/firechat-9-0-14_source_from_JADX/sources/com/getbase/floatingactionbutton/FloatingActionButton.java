package com.getbase.floatingactionbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build.VERSION;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.p000v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FloatingActionButton extends ImageButton {
    public static final int SIZE_MINI = 1;
    public static final int SIZE_NORMAL = 0;
    private float mCircleSize;
    int mColorDisabled;
    int mColorNormal;
    int mColorPressed;
    private int mDrawableSize;
    @DrawableRes
    private int mIcon;
    private Drawable mIconDrawable;
    private float mShadowOffset;
    private float mShadowRadius;
    private int mSize;
    boolean mStrokeVisible;
    String mTitle;

    @Retention(RetentionPolicy.SOURCE)
    public @interface FAB_SIZE {
    }

    private static class TranslucentLayerDrawable extends LayerDrawable {
        private final int mAlpha;

        public TranslucentLayerDrawable(int i, Drawable... drawableArr) {
            super(drawableArr);
            this.mAlpha = i;
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.saveLayerAlpha((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, this.mAlpha, 31);
            super.draw(canvas);
            canvas.restore();
        }
    }

    private int opacityToAlpha(float f) {
        return (int) (f * 255.0f);
    }

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public FloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    /* access modifiers changed from: 0000 */
    public void init(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C1112R.styleable.FloatingActionButton, 0, 0);
        this.mColorNormal = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionButton_fab_colorNormal, getColor(17170451));
        this.mColorPressed = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionButton_fab_colorPressed, getColor(17170450));
        this.mColorDisabled = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionButton_fab_colorDisabled, getColor(17170432));
        this.mSize = obtainStyledAttributes.getInt(C1112R.styleable.FloatingActionButton_fab_size, 0);
        this.mIcon = obtainStyledAttributes.getResourceId(C1112R.styleable.FloatingActionButton_fab_icon, 0);
        this.mTitle = obtainStyledAttributes.getString(C1112R.styleable.FloatingActionButton_fab_title);
        this.mStrokeVisible = obtainStyledAttributes.getBoolean(C1112R.styleable.FloatingActionButton_fab_stroke_visible, true);
        obtainStyledAttributes.recycle();
        updateCircleSize();
        this.mShadowRadius = getDimension(C1112R.dimen.fab_shadow_radius);
        this.mShadowOffset = getDimension(C1112R.dimen.fab_shadow_offset);
        updateDrawableSize();
        updateBackground();
    }

    private void updateDrawableSize() {
        this.mDrawableSize = (int) (this.mCircleSize + (this.mShadowRadius * 2.0f));
    }

    private void updateCircleSize() {
        this.mCircleSize = getDimension(this.mSize == 0 ? C1112R.dimen.fab_size_normal : C1112R.dimen.fab_size_mini);
    }

    public void setSize(int i) {
        if (i != 1 && i != 0) {
            throw new IllegalArgumentException("Use @FAB_SIZE constants only!");
        } else if (this.mSize != i) {
            this.mSize = i;
            updateCircleSize();
            updateDrawableSize();
            updateBackground();
        }
    }

    public int getSize() {
        return this.mSize;
    }

    public void setIcon(@DrawableRes int i) {
        if (this.mIcon != i) {
            this.mIcon = i;
            this.mIconDrawable = null;
            updateBackground();
        }
    }

    public void setIconDrawable(@NonNull Drawable drawable) {
        if (this.mIconDrawable != drawable) {
            this.mIcon = 0;
            this.mIconDrawable = drawable;
            updateBackground();
        }
    }

    public int getColorNormal() {
        return this.mColorNormal;
    }

    public void setColorNormalResId(@ColorRes int i) {
        setColorNormal(getColor(i));
    }

    public void setColorNormal(int i) {
        if (this.mColorNormal != i) {
            this.mColorNormal = i;
            updateBackground();
        }
    }

    public int getColorPressed() {
        return this.mColorPressed;
    }

    public void setColorPressedResId(@ColorRes int i) {
        setColorPressed(getColor(i));
    }

    public void setColorPressed(int i) {
        if (this.mColorPressed != i) {
            this.mColorPressed = i;
            updateBackground();
        }
    }

    public int getColorDisabled() {
        return this.mColorDisabled;
    }

    public void setColorDisabledResId(@ColorRes int i) {
        setColorDisabled(getColor(i));
    }

    public void setColorDisabled(int i) {
        if (this.mColorDisabled != i) {
            this.mColorDisabled = i;
            updateBackground();
        }
    }

    public void setStrokeVisible(boolean z) {
        if (this.mStrokeVisible != z) {
            this.mStrokeVisible = z;
            updateBackground();
        }
    }

    public boolean isStrokeVisible() {
        return this.mStrokeVisible;
    }

    /* access modifiers changed from: 0000 */
    public int getColor(@ColorRes int i) {
        return getResources().getColor(i);
    }

    /* access modifiers changed from: 0000 */
    public float getDimension(@DimenRes int i) {
        return getResources().getDimension(i);
    }

    public void setTitle(String str) {
        this.mTitle = str;
        TextView labelView = getLabelView();
        if (labelView != null) {
            labelView.setText(str);
        }
    }

    /* access modifiers changed from: 0000 */
    public TextView getLabelView() {
        return (TextView) getTag(C1112R.C1114id.fab_label);
    }

    public String getTitle() {
        return this.mTitle;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(this.mDrawableSize, this.mDrawableSize);
    }

    /* access modifiers changed from: 0000 */
    public void updateBackground() {
        float dimension = getDimension(C1112R.dimen.fab_stroke_width);
        float f = dimension / 2.0f;
        Drawable[] drawableArr = new Drawable[4];
        drawableArr[0] = getResources().getDrawable(this.mSize == 0 ? C1112R.C1113drawable.fab_bg_normal : C1112R.C1113drawable.fab_bg_mini);
        drawableArr[1] = createFillDrawable(dimension);
        drawableArr[2] = createOuterStrokeDrawable(dimension);
        drawableArr[3] = getIconDrawable();
        LayerDrawable layerDrawable = new LayerDrawable(drawableArr);
        int dimension2 = ((int) (this.mCircleSize - getDimension(C1112R.dimen.fab_icon_size))) / 2;
        int i = (int) this.mShadowRadius;
        int i2 = (int) (this.mShadowRadius - this.mShadowOffset);
        int i3 = (int) (this.mShadowRadius + this.mShadowOffset);
        layerDrawable.setLayerInset(1, i, i2, i, i3);
        int i4 = (int) (((float) i) - f);
        LayerDrawable layerDrawable2 = layerDrawable;
        layerDrawable2.setLayerInset(2, i4, (int) (((float) i2) - f), i4, (int) (((float) i3) - f));
        int i5 = i + dimension2;
        layerDrawable2.setLayerInset(3, i5, i2 + dimension2, i5, i3 + dimension2);
        setBackgroundCompat(layerDrawable);
    }

    /* access modifiers changed from: 0000 */
    public Drawable getIconDrawable() {
        if (this.mIconDrawable != null) {
            return this.mIconDrawable;
        }
        if (this.mIcon != 0) {
            return getResources().getDrawable(this.mIcon);
        }
        return new ColorDrawable(0);
    }

    private StateListDrawable createFillDrawable(float f) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-16842910}, createCircleDrawable(this.mColorDisabled, f));
        stateListDrawable.addState(new int[]{16842919}, createCircleDrawable(this.mColorPressed, f));
        stateListDrawable.addState(new int[0], createCircleDrawable(this.mColorNormal, f));
        return stateListDrawable;
    }

    private Drawable createCircleDrawable(int i, float f) {
        int alpha = Color.alpha(i);
        int opaque = opaque(i);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(opaque);
        Drawable[] drawableArr = {shapeDrawable, createInnerStrokesDrawable(opaque, f)};
        LayerDrawable layerDrawable = (alpha == 255 || !this.mStrokeVisible) ? new LayerDrawable(drawableArr) : new TranslucentLayerDrawable(alpha, drawableArr);
        int i2 = (int) (f / 2.0f);
        layerDrawable.setLayerInset(1, i2, i2, i2, i2);
        return layerDrawable;
    }

    private Drawable createOuterStrokeDrawable(float f) {
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(f);
        paint.setStyle(Style.STROKE);
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setAlpha(opacityToAlpha(0.02f));
        return shapeDrawable;
    }

    private int darkenColor(int i) {
        return adjustColorBrightness(i, 0.9f);
    }

    private int lightenColor(int i) {
        return adjustColorBrightness(i, 1.1f);
    }

    private int adjustColorBrightness(int i, float f) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        fArr[2] = Math.min(fArr[2] * f, 1.0f);
        return Color.HSVToColor(Color.alpha(i), fArr);
    }

    private int halfTransparent(int i) {
        return Color.argb(Color.alpha(i) / 2, Color.red(i), Color.green(i), Color.blue(i));
    }

    private int opaque(int i) {
        return Color.rgb(Color.red(i), Color.green(i), Color.blue(i));
    }

    private Drawable createInnerStrokesDrawable(int i, float f) {
        if (!this.mStrokeVisible) {
            return new ColorDrawable(0);
        }
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        final int darkenColor = darkenColor(i);
        final int halfTransparent = halfTransparent(darkenColor);
        final int lightenColor = lightenColor(i);
        final int halfTransparent2 = halfTransparent(lightenColor);
        Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(f);
        paint.setStyle(Style.STROKE);
        final int i2 = i;
        C11071 r2 = new ShaderFactory() {
            public Shader resize(int i, int i2) {
                float f = (float) (i / 2);
                float f2 = f;
                LinearGradient linearGradient = new LinearGradient(f2, 0.0f, f, (float) i2, new int[]{lightenColor, halfTransparent2, i2, halfTransparent, darkenColor}, new float[]{0.0f, 0.2f, 0.5f, 0.8f, 1.0f}, TileMode.CLAMP);
                return linearGradient;
            }
        };
        shapeDrawable.setShaderFactory(r2);
        return shapeDrawable;
    }

    @SuppressLint({"NewApi"})
    private void setBackgroundCompat(Drawable drawable) {
        if (VERSION.SDK_INT >= 16) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    public void setVisibility(int i) {
        TextView labelView = getLabelView();
        if (labelView != null) {
            labelView.setVisibility(i);
        }
        super.setVisibility(i);
    }
}
