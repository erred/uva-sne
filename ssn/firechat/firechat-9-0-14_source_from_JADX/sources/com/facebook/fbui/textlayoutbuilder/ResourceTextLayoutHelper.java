package com.facebook.fbui.textlayoutbuilder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.AttrRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

public class ResourceTextLayoutHelper {
    private static final int DEFAULT_TEXT_SIZE_PX = 15;

    public static void updateFromStyleResource(TextLayoutBuilder textLayoutBuilder, Context context, @StyleRes int i) {
        updateFromStyleResource(textLayoutBuilder, context, 0, i);
    }

    public static void updateFromStyleResource(TextLayoutBuilder textLayoutBuilder, Context context, @AttrRes int i, @StyleRes int i2) {
        updateFromStyleResource(textLayoutBuilder, context, null, i, i2);
    }

    public static void updateFromStyleResource(TextLayoutBuilder textLayoutBuilder, Context context, AttributeSet attributeSet, @AttrRes int i, @StyleRes int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0633R.styleable.TextStyle, i, i2);
        int resourceId = obtainStyledAttributes.getResourceId(C0633R.styleable.TextStyle_android_textAppearance, -1);
        if (resourceId > 0) {
            setTextAppearance(textLayoutBuilder, context, resourceId);
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(C0633R.styleable.TextStyle_android_textColor);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(C0633R.styleable.TextStyle_android_textSize, 15);
        int i3 = obtainStyledAttributes.getInt(C0633R.styleable.TextStyle_android_shadowColor, 0);
        float f = obtainStyledAttributes.getFloat(C0633R.styleable.TextStyle_android_shadowDx, 0.0f);
        float f2 = obtainStyledAttributes.getFloat(C0633R.styleable.TextStyle_android_shadowDy, 0.0f);
        float f3 = obtainStyledAttributes.getFloat(C0633R.styleable.TextStyle_android_shadowRadius, 0.0f);
        int i4 = obtainStyledAttributes.getInt(C0633R.styleable.TextStyle_android_textStyle, -1);
        int i5 = obtainStyledAttributes.getInt(C0633R.styleable.TextStyle_android_ellipsize, 0);
        boolean z = obtainStyledAttributes.getBoolean(C0633R.styleable.TextStyle_android_singleLine, false);
        int i6 = obtainStyledAttributes.getInt(C0633R.styleable.TextStyle_android_maxLines, Integer.MAX_VALUE);
        obtainStyledAttributes.recycle();
        textLayoutBuilder.setTextColor(colorStateList);
        textLayoutBuilder.setTextSize(dimensionPixelSize);
        textLayoutBuilder.setShadowLayer(f3, f, f2, i3);
        if (i4 != -1) {
            textLayoutBuilder.setTypeface(Typeface.defaultFromStyle(i4));
        } else {
            textLayoutBuilder.setTypeface(null);
        }
        if (i5 <= 0 || i5 >= 4) {
            textLayoutBuilder.setEllipsize(null);
        } else {
            textLayoutBuilder.setEllipsize(TruncateAt.values()[i5 - 1]);
        }
        textLayoutBuilder.setSingleLine(z);
        textLayoutBuilder.setMaxLines(i6);
    }

    public static void setTextAppearance(TextLayoutBuilder textLayoutBuilder, Context context, @StyleRes int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(i, C0633R.styleable.TextAppearance);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(C0633R.styleable.TextAppearance_android_textColor);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(C0633R.styleable.TextAppearance_android_textSize, 0);
        int i2 = obtainStyledAttributes.getInt(C0633R.styleable.TextAppearance_android_shadowColor, 0);
        if (i2 != 0) {
            textLayoutBuilder.setShadowLayer(obtainStyledAttributes.getFloat(C0633R.styleable.TextAppearance_android_shadowRadius, 0.0f), obtainStyledAttributes.getFloat(C0633R.styleable.TextAppearance_android_shadowDx, 0.0f), obtainStyledAttributes.getFloat(C0633R.styleable.TextAppearance_android_shadowDy, 0.0f), i2);
        }
        int i3 = obtainStyledAttributes.getInt(C0633R.styleable.TextAppearance_android_textStyle, -1);
        obtainStyledAttributes.recycle();
        if (colorStateList != null) {
            textLayoutBuilder.setTextColor(colorStateList);
        }
        if (dimensionPixelSize != 0) {
            textLayoutBuilder.setTextSize(dimensionPixelSize);
        }
        if (i3 != -1) {
            textLayoutBuilder.setTypeface(Typeface.defaultFromStyle(i3));
        }
    }
}
