package com.facebook.fbui.textlayoutbuilder;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.C0010Px;
import android.support.annotation.ColorInt;
import android.support.annotation.VisibleForTesting;
import android.support.p000v4.text.TextDirectionHeuristicCompat;
import android.support.p000v4.text.TextDirectionHeuristicsCompat;
import android.support.p000v4.util.LruCache;
import android.support.p000v4.view.ViewCompat;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TextLayoutBuilder {
    public static final int DEFAULT_MAX_LINES = Integer.MAX_VALUE;
    public static final int MEASURE_MODE_AT_MOST = 2;
    public static final int MEASURE_MODE_EXACTLY = 1;
    public static final int MEASURE_MODE_UNSPECIFIED = 0;
    @VisibleForTesting
    static final LruCache<Integer, Layout> sCache = new LruCache<>(100);
    private GlyphWarmer mGlyphWarmer;
    @VisibleForTesting
    final Params mParams = new Params();
    private Layout mSavedLayout = null;
    private boolean mShouldCacheLayout = true;
    private boolean mShouldWarmText = false;

    private static class ComparableTextPaint extends TextPaint {
        private int mShadowColor;
        private float mShadowDx;
        private float mShadowDy;
        private float mShadowRadius;

        public ComparableTextPaint() {
        }

        public ComparableTextPaint(int i) {
            super(i);
        }

        public ComparableTextPaint(Paint paint) {
            super(paint);
        }

        public void setShadowLayer(float f, float f2, float f3, int i) {
            this.mShadowRadius = f;
            this.mShadowDx = f2;
            this.mShadowDy = f3;
            this.mShadowColor = i;
            super.setShadowLayer(f, f2, f3, i);
        }

        public int hashCode() {
            Typeface typeface = getTypeface();
            int color = ((((((((((((((getColor() + 31) * 31) + Float.floatToIntBits(getTextSize())) * 31) + (typeface != null ? typeface.hashCode() : 0)) * 31) + Float.floatToIntBits(this.mShadowDx)) * 31) + Float.floatToIntBits(this.mShadowDy)) * 31) + Float.floatToIntBits(this.mShadowRadius)) * 31) + this.mShadowColor) * 31) + this.linkColor;
            if (this.drawableState == null) {
                return (color * 31) + 0;
            }
            for (int i : this.drawableState) {
                color = (color * 31) + i;
            }
            return color;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface MeasureMode {
    }

    @VisibleForTesting
    static class Params {
        Alignment alignment = Alignment.ALIGN_NORMAL;
        ColorStateList color;
        TruncateAt ellipsize = null;
        boolean includePadding = true;
        boolean mForceNewPaint = false;
        int maxLines = Integer.MAX_VALUE;
        int measureMode;
        TextPaint paint = new ComparableTextPaint(1);
        boolean singleLine = false;
        float spacingAdd = 0.0f;
        float spacingMult = 1.0f;
        CharSequence text;
        TextDirectionHeuristicCompat textDirection = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
        int width;

        Params() {
        }

        /* access modifiers changed from: 0000 */
        public void createNewPaintIfNeeded() {
            if (this.mForceNewPaint) {
                this.paint = new ComparableTextPaint((Paint) this.paint);
                this.mForceNewPaint = false;
            }
        }

        public int hashCode() {
            int i = 0;
            int hashCode = ((((((((((((((((((((((this.paint != null ? this.paint.hashCode() : 0) + 31) * 31) + this.width) * 31) + this.measureMode) * 31) + Float.floatToIntBits(this.spacingMult)) * 31) + Float.floatToIntBits(this.spacingAdd)) * 31) + (this.includePadding ? 1 : 0)) * 31) + (this.ellipsize != null ? this.ellipsize.hashCode() : 0)) * 31) + (this.singleLine ? 1 : 0)) * 31) + this.maxLines) * 31) + (this.alignment != null ? this.alignment.hashCode() : 0)) * 31) + (this.textDirection != null ? this.textDirection.hashCode() : 0)) * 31;
            if (this.text != null) {
                i = this.text.hashCode();
            }
            return hashCode + i;
        }
    }

    public TextLayoutBuilder setWidth(@C0010Px int i) {
        return setWidth(i, i <= 0 ? 0 : 1);
    }

    public TextLayoutBuilder setWidth(@C0010Px int i, int i2) {
        if (!(this.mParams.width == i && this.mParams.measureMode == i2)) {
            this.mParams.width = i;
            this.mParams.measureMode = i2;
            this.mSavedLayout = null;
        }
        return this;
    }

    public CharSequence getText() {
        return this.mParams.text;
    }

    public TextLayoutBuilder setText(CharSequence charSequence) {
        if (charSequence == this.mParams.text || (charSequence != null && this.mParams.text != null && charSequence.equals(this.mParams.text))) {
            return this;
        }
        this.mParams.text = charSequence;
        this.mSavedLayout = null;
        return this;
    }

    public float getTextSize() {
        return this.mParams.paint.getTextSize();
    }

    public TextLayoutBuilder setTextSize(int i) {
        float f = (float) i;
        if (this.mParams.paint.getTextSize() != f) {
            this.mParams.createNewPaintIfNeeded();
            this.mParams.paint.setTextSize(f);
            this.mSavedLayout = null;
        }
        return this;
    }

    @ColorInt
    public int getTextColor() {
        return this.mParams.paint.getColor();
    }

    public TextLayoutBuilder setTextColor(@ColorInt int i) {
        this.mParams.createNewPaintIfNeeded();
        this.mParams.color = null;
        this.mParams.paint.setColor(i);
        this.mSavedLayout = null;
        return this;
    }

    public TextLayoutBuilder setTextColor(ColorStateList colorStateList) {
        this.mParams.createNewPaintIfNeeded();
        this.mParams.color = colorStateList;
        this.mParams.paint.setColor(this.mParams.color != null ? this.mParams.color.getDefaultColor() : ViewCompat.MEASURED_STATE_MASK);
        this.mSavedLayout = null;
        return this;
    }

    @ColorInt
    public int getLinkColor() {
        return this.mParams.paint.linkColor;
    }

    public TextLayoutBuilder setLinkColor(@ColorInt int i) {
        if (this.mParams.paint.linkColor != i) {
            this.mParams.createNewPaintIfNeeded();
            this.mParams.paint.linkColor = i;
            this.mSavedLayout = null;
        }
        return this;
    }

    public float getTextSpacingExtra() {
        return this.mParams.spacingAdd;
    }

    public TextLayoutBuilder setTextSpacingExtra(float f) {
        if (this.mParams.spacingAdd != f) {
            this.mParams.spacingAdd = f;
            this.mSavedLayout = null;
        }
        return this;
    }

    public float getTextSpacingMultiplier() {
        return this.mParams.spacingMult;
    }

    public TextLayoutBuilder setTextSpacingMultiplier(float f) {
        if (this.mParams.spacingMult != f) {
            this.mParams.spacingMult = f;
            this.mSavedLayout = null;
        }
        return this;
    }

    public boolean getIncludeFontPadding() {
        return this.mParams.includePadding;
    }

    public TextLayoutBuilder setIncludeFontPadding(boolean z) {
        if (this.mParams.includePadding != z) {
            this.mParams.includePadding = z;
            this.mSavedLayout = null;
        }
        return this;
    }

    public Alignment getAlignment() {
        return this.mParams.alignment;
    }

    public TextLayoutBuilder setAlignment(Alignment alignment) {
        if (this.mParams.alignment != alignment) {
            this.mParams.alignment = alignment;
            this.mSavedLayout = null;
        }
        return this;
    }

    public TextDirectionHeuristicCompat getTextDirection() {
        return this.mParams.textDirection;
    }

    public TextLayoutBuilder setTextDirection(TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        if (this.mParams.textDirection != textDirectionHeuristicCompat) {
            this.mParams.textDirection = textDirectionHeuristicCompat;
            this.mSavedLayout = null;
        }
        return this;
    }

    public TextLayoutBuilder setShadowLayer(float f, float f2, float f3, @ColorInt int i) {
        this.mParams.createNewPaintIfNeeded();
        this.mParams.paint.setShadowLayer(f, f2, f3, i);
        this.mSavedLayout = null;
        return this;
    }

    public TextLayoutBuilder setTextStyle(int i) {
        return setTypeface(Typeface.defaultFromStyle(i));
    }

    public Typeface getTypeface() {
        return this.mParams.paint.getTypeface();
    }

    public TextLayoutBuilder setTypeface(Typeface typeface) {
        if (this.mParams.paint.getTypeface() != typeface) {
            this.mParams.createNewPaintIfNeeded();
            this.mParams.paint.setTypeface(typeface);
            this.mSavedLayout = null;
        }
        return this;
    }

    public int[] getDrawableState() {
        return this.mParams.paint.drawableState;
    }

    public TextLayoutBuilder setDrawableState(int[] iArr) {
        this.mParams.createNewPaintIfNeeded();
        this.mParams.paint.drawableState = iArr;
        if (this.mParams.color != null && this.mParams.color.isStateful()) {
            this.mParams.paint.setColor(this.mParams.color.getColorForState(iArr, 0));
            this.mSavedLayout = null;
        }
        return this;
    }

    public TruncateAt getEllipsize() {
        return this.mParams.ellipsize;
    }

    public TextLayoutBuilder setEllipsize(TruncateAt truncateAt) {
        if (this.mParams.ellipsize != truncateAt) {
            this.mParams.ellipsize = truncateAt;
            this.mSavedLayout = null;
        }
        return this;
    }

    public boolean getSingleLine() {
        return this.mParams.singleLine;
    }

    public TextLayoutBuilder setSingleLine(boolean z) {
        if (this.mParams.singleLine != z) {
            this.mParams.singleLine = z;
            this.mSavedLayout = null;
        }
        return this;
    }

    public int getMaxLines() {
        return this.mParams.maxLines;
    }

    public TextLayoutBuilder setMaxLines(int i) {
        if (this.mParams.maxLines != i) {
            this.mParams.maxLines = i;
            this.mSavedLayout = null;
        }
        return this;
    }

    public boolean getShouldCacheLayout() {
        return this.mShouldCacheLayout;
    }

    public TextLayoutBuilder setShouldCacheLayout(boolean z) {
        this.mShouldCacheLayout = z;
        return this;
    }

    public boolean getShouldWarmText() {
        return this.mShouldWarmText;
    }

    public TextLayoutBuilder setShouldWarmText(boolean z) {
        this.mShouldWarmText = z;
        return this;
    }

    public GlyphWarmer getGlyphWarmer() {
        return this.mGlyphWarmer;
    }

    public TextLayoutBuilder setGlyphWarmer(GlyphWarmer glyphWarmer) {
        this.mGlyphWarmer = glyphWarmer;
        return this;
    }

    /* JADX WARNING: Removed duplicated region for block: B:57:0x015b A[LOOP:0: B:37:0x00f3->B:57:0x015b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x016f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.text.Layout build() {
        /*
            r21 = this;
            r1 = r21
            boolean r2 = r1.mShouldCacheLayout
            if (r2 == 0) goto L_0x000d
            android.text.Layout r2 = r1.mSavedLayout
            if (r2 == 0) goto L_0x000d
            android.text.Layout r2 = r1.mSavedLayout
            return r2
        L_0x000d:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r2 = r1.mParams
            java.lang.CharSequence r2 = r2.text
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            r3 = 0
            if (r2 == 0) goto L_0x0019
            return r3
        L_0x0019:
            r2 = -1
            boolean r4 = r1.mShouldCacheLayout
            r5 = 0
            r6 = 1
            if (r4 == 0) goto L_0x0043
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            java.lang.CharSequence r4 = r4.text
            boolean r4 = r4 instanceof android.text.Spannable
            if (r4 == 0) goto L_0x0043
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            java.lang.CharSequence r4 = r4.text
            android.text.Spannable r4 = (android.text.Spannable) r4
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            java.lang.CharSequence r7 = r7.text
            int r7 = r7.length()
            int r7 = r7 - r6
            java.lang.Class<android.text.style.ClickableSpan> r8 = android.text.style.ClickableSpan.class
            java.lang.Object[] r4 = r4.getSpans(r5, r7, r8)
            android.text.style.ClickableSpan[] r4 = (android.text.style.ClickableSpan[]) r4
            int r4 = r4.length
            if (r4 <= 0) goto L_0x0043
            r5 = 1
        L_0x0043:
            boolean r4 = r1.mShouldCacheLayout
            if (r4 == 0) goto L_0x005e
            if (r5 != 0) goto L_0x005e
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r2 = r1.mParams
            int r2 = r2.hashCode()
            android.support.v4.util.LruCache<java.lang.Integer, android.text.Layout> r4 = sCache
            java.lang.Integer r7 = java.lang.Integer.valueOf(r2)
            java.lang.Object r4 = r4.get(r7)
            android.text.Layout r4 = (android.text.Layout) r4
            if (r4 == 0) goto L_0x005e
            return r4
        L_0x005e:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            boolean r4 = r4.singleLine
            if (r4 == 0) goto L_0x0066
            r4 = 1
            goto L_0x006a
        L_0x0066:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            int r4 = r4.maxLines
        L_0x006a:
            if (r4 != r6) goto L_0x0078
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r3 = r1.mParams
            java.lang.CharSequence r3 = r3.text
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            android.text.TextPaint r7 = r7.paint
            android.text.BoringLayout$Metrics r3 = android.text.BoringLayout.isBoring(r3, r7)
        L_0x0078:
            r13 = r3
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r3 = r1.mParams
            int r3 = r3.measureMode
            switch(r3) {
                case 0: goto L_0x00bb;
                case 1: goto L_0x00b6;
                case 2: goto L_0x009b;
                default: goto L_0x0080;
            }
        L_0x0080:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Unexpected measure mode "
            r3.append(r4)
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            int r4 = r4.measureMode
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r2.<init>(r3)
            throw r2
        L_0x009b:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r3 = r1.mParams
            java.lang.CharSequence r3 = r3.text
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            android.text.TextPaint r7 = r7.paint
            float r3 = android.text.Layout.getDesiredWidth(r3, r7)
            double r7 = (double) r3
            double r7 = java.lang.Math.ceil(r7)
            int r3 = (int) r7
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            int r7 = r7.width
            int r3 = java.lang.Math.min(r3, r7)
            goto L_0x00cd
        L_0x00b6:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r3 = r1.mParams
            int r3 = r3.width
            goto L_0x00cd
        L_0x00bb:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r3 = r1.mParams
            java.lang.CharSequence r3 = r3.text
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            android.text.TextPaint r7 = r7.paint
            float r3 = android.text.Layout.getDesiredWidth(r3, r7)
            double r7 = (double) r3
            double r7 = java.lang.Math.ceil(r7)
            int r3 = (int) r7
        L_0x00cd:
            if (r13 == 0) goto L_0x00f3
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            java.lang.CharSequence r7 = r4.text
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            android.text.TextPaint r8 = r4.paint
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            android.text.Layout$Alignment r10 = r4.alignment
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            float r11 = r4.spacingMult
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            float r12 = r4.spacingAdd
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            boolean r14 = r4.includePadding
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r4 = r1.mParams
            android.text.TextUtils$TruncateAt r15 = r4.ellipsize
            r9 = r3
            r16 = r3
            android.text.BoringLayout r3 = android.text.BoringLayout.make(r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            goto L_0x012a
        L_0x00f3:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            java.lang.CharSequence r7 = r7.text     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            r8 = 0
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r9 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            java.lang.CharSequence r9 = r9.text     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            int r9 = r9.length()     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r10 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            android.text.TextPaint r10 = r10.paint     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r11 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            android.text.Layout$Alignment r12 = r11.alignment     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r11 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            float r13 = r11.spacingMult     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r11 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            float r14 = r11.spacingAdd     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r11 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            boolean r15 = r11.includePadding     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r11 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            android.text.TextUtils$TruncateAt r11 = r11.ellipsize     // Catch:{ IndexOutOfBoundsException -> 0x0151 }
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r6 = r1.mParams     // Catch:{ IndexOutOfBoundsException -> 0x014e }
            android.support.v4.text.TextDirectionHeuristicCompat r6 = r6.textDirection     // Catch:{ IndexOutOfBoundsException -> 0x014e }
            r16 = r11
            r11 = r3
            r17 = r3
            r18 = r4
            r19 = r6
            android.text.StaticLayout r6 = com.facebook.fbui.textlayoutbuilder.StaticLayoutHelper.make(r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)     // Catch:{ IndexOutOfBoundsException -> 0x014e }
            r3 = r6
        L_0x012a:
            boolean r4 = r1.mShouldCacheLayout
            if (r4 == 0) goto L_0x013b
            if (r5 != 0) goto L_0x013b
            r1.mSavedLayout = r3
            android.support.v4.util.LruCache<java.lang.Integer, android.text.Layout> r4 = sCache
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4.put(r2, r3)
        L_0x013b:
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r2 = r1.mParams
            r6 = 1
            r2.mForceNewPaint = r6
            boolean r2 = r1.mShouldWarmText
            if (r2 == 0) goto L_0x014d
            com.facebook.fbui.textlayoutbuilder.GlyphWarmer r2 = r1.mGlyphWarmer
            if (r2 == 0) goto L_0x014d
            com.facebook.fbui.textlayoutbuilder.GlyphWarmer r2 = r1.mGlyphWarmer
            r2.warmLayout(r3)
        L_0x014d:
            return r3
        L_0x014e:
            r0 = move-exception
            r6 = 1
            goto L_0x0152
        L_0x0151:
            r0 = move-exception
        L_0x0152:
            r7 = r0
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r8 = r1.mParams
            java.lang.CharSequence r8 = r8.text
            boolean r8 = r8 instanceof java.lang.String
            if (r8 != 0) goto L_0x016f
            java.lang.String r8 = "TextLayoutBuilder"
            java.lang.String r9 = "Hit bug #35412, retrying with Spannables removed"
            android.util.Log.e(r8, r9, r7)
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r7 = r1.mParams
            com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder$Params r8 = r1.mParams
            java.lang.CharSequence r8 = r8.text
            java.lang.String r8 = r8.toString()
            r7.text = r8
            goto L_0x00f3
        L_0x016f:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.build():android.text.Layout");
    }
}
