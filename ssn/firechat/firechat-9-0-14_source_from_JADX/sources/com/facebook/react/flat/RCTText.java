package com.facebook.react.flat;

import android.support.p000v4.text.TextDirectionHeuristicsCompat;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder;
import com.facebook.fbui.textlayoutbuilder.glyphwarmer.GlyphWarmerImpl;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaMeasureMode;
import com.facebook.yoga.YogaMeasureOutput;
import com.facebook.yoga.YogaNode;
import javax.annotation.Nullable;

final class RCTText extends RCTVirtualText implements YogaMeasureFunction {
    private static final int ALIGNMENT_LEFT = 3;
    private static final int ALIGNMENT_RIGHT = 4;
    private static final TextLayoutBuilder sTextLayoutBuilder = new TextLayoutBuilder().setShouldCacheLayout(false).setShouldWarmText(true).setGlyphWarmer(new GlyphWarmerImpl());
    private int mAlignment = 0;
    @Nullable
    private DrawTextLayout mDrawCommand;
    private boolean mIncludeFontPadding = true;
    private int mNumberOfLines = Integer.MAX_VALUE;
    private float mSpacingAdd = 0.0f;
    private float mSpacingMult = 1.0f;
    @Nullable
    private CharSequence mText;

    /* access modifiers changed from: 0000 */
    public boolean doesDraw() {
        return true;
    }

    public boolean isVirtual() {
        return false;
    }

    public boolean isVirtualAnchor() {
        return true;
    }

    public RCTText() {
        setMeasureFunction(this);
        getSpan().setFontSize(getDefaultFontSize());
    }

    public long measure(YogaNode yogaNode, float f, YogaMeasureMode yogaMeasureMode, float f2, YogaMeasureMode yogaMeasureMode2) {
        SpannableStringBuilder text = getText();
        if (TextUtils.isEmpty(text)) {
            this.mText = null;
            return YogaMeasureOutput.make(0, 0);
        }
        this.mText = text;
        Layout createTextLayout = createTextLayout((int) Math.ceil((double) f), yogaMeasureMode, TruncateAt.END, this.mIncludeFontPadding, this.mNumberOfLines, this.mNumberOfLines == 1, text, getFontSize(), this.mSpacingAdd, this.mSpacingMult, getFontStyle(), getAlignment());
        if (this.mDrawCommand == null || this.mDrawCommand.isFrozen()) {
            this.mDrawCommand = new DrawTextLayout(createTextLayout);
        } else {
            this.mDrawCommand.setLayout(createTextLayout);
        }
        return YogaMeasureOutput.make(this.mDrawCommand.getLayoutWidth(), this.mDrawCommand.getLayoutHeight());
    }

    /* access modifiers changed from: protected */
    public void collectState(StateBuilder stateBuilder, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        boolean z;
        super.collectState(stateBuilder, f, f2, f3, f4, f5, f6, f7, f8);
        if (this.mText == null) {
            if (f4 - f2 > 0.0f && f3 - f > 0.0f) {
                SpannableStringBuilder text = getText();
                if (!TextUtils.isEmpty(text)) {
                    this.mText = text;
                }
            }
            if (this.mText == null) {
                return;
            }
        }
        if (this.mDrawCommand == null) {
            int ceil = (int) Math.ceil((double) (f3 - f));
            YogaMeasureMode yogaMeasureMode = YogaMeasureMode.EXACTLY;
            TruncateAt truncateAt = TruncateAt.END;
            boolean z2 = this.mIncludeFontPadding;
            int i = this.mNumberOfLines;
            boolean z3 = this.mNumberOfLines == 1;
            this.mDrawCommand = new DrawTextLayout(createTextLayout(ceil, yogaMeasureMode, truncateAt, z2, i, z3, this.mText, getFontSize(), this.mSpacingAdd, this.mSpacingMult, getFontStyle(), getAlignment()));
            z = true;
        } else {
            z = false;
        }
        float padding = f + getPadding(0);
        float padding2 = f2 + getPadding(1);
        this.mDrawCommand = (DrawTextLayout) this.mDrawCommand.updateBoundsAndFreeze(padding, padding2, padding + this.mDrawCommand.getLayoutWidth(), padding2 + this.mDrawCommand.getLayoutHeight(), f5, f6, f7, f8);
        stateBuilder.addDrawCommand(this.mDrawCommand);
        if (z) {
            NodeRegion nodeRegion = getNodeRegion();
            if (nodeRegion instanceof TextNodeRegion) {
                ((TextNodeRegion) nodeRegion).setLayout(this.mDrawCommand.getLayout());
            }
        }
        performCollectAttachDetachListeners(stateBuilder);
    }

    @ReactProp(defaultDouble = Double.NaN, name = "lineHeight")
    public void setLineHeight(double d) {
        if (Double.isNaN(d)) {
            this.mSpacingMult = 1.0f;
            this.mSpacingAdd = 0.0f;
        } else {
            this.mSpacingMult = 0.0f;
            this.mSpacingAdd = PixelUtil.toPixelFromSP((float) d);
        }
        notifyChanged(true);
    }

    @ReactProp(defaultInt = Integer.MAX_VALUE, name = "numberOfLines")
    public void setNumberOfLines(int i) {
        this.mNumberOfLines = i;
        notifyChanged(true);
    }

    @ReactProp(defaultBoolean = true, name = "includeFontPadding")
    public void setIncludeFontPadding(boolean z) {
        this.mIncludeFontPadding = z;
    }

    /* access modifiers changed from: 0000 */
    public void updateNodeRegion(float f, float f2, float f3, float f4, boolean z) {
        NodeRegion nodeRegion = getNodeRegion();
        if (this.mDrawCommand == null) {
            if (!nodeRegion.matches(f, f2, f3, f4, z)) {
                TextNodeRegion textNodeRegion = new TextNodeRegion(f, f2, f3, f4, getReactTag(), z, null);
                setNodeRegion(textNodeRegion);
            }
            return;
        }
        Layout layout = null;
        if (nodeRegion instanceof TextNodeRegion) {
            layout = ((TextNodeRegion) nodeRegion).getLayout();
        }
        Layout layout2 = layout;
        Layout layout3 = this.mDrawCommand.getLayout();
        if (!nodeRegion.matches(f, f2, f3, f4, z) || layout2 != layout3) {
            TextNodeRegion textNodeRegion2 = new TextNodeRegion(f, f2, f3, f4, getReactTag(), z, layout3);
            setNodeRegion(textNodeRegion2);
        }
    }

    /* access modifiers changed from: protected */
    public int getDefaultFontSize() {
        return fontSizeFromSp(14.0f);
    }

    /* access modifiers changed from: protected */
    public void notifyChanged(boolean z) {
        dirty();
    }

    @ReactProp(name = "textAlign")
    public void setTextAlign(@Nullable String str) {
        if (str == null || ReactScrollViewHelper.AUTO.equals(str)) {
            this.mAlignment = 0;
        } else if (ViewProps.LEFT.equals(str)) {
            this.mAlignment = 3;
        } else if (ViewProps.RIGHT.equals(str)) {
            this.mAlignment = 5;
        } else if ("center".equals(str)) {
            this.mAlignment = 17;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid textAlign: ");
            sb.append(str);
            throw new JSApplicationIllegalArgumentException(sb.toString());
        }
        notifyChanged(false);
    }

    public Alignment getAlignment() {
        boolean z = getLayoutDirection() == YogaDirection.RTL;
        int i = this.mAlignment;
        char c = 4;
        if (i == 3) {
            if (!z) {
                c = 3;
            }
            return Alignment.values()[c];
        } else if (i == 5) {
            if (z) {
                c = 3;
            }
            return Alignment.values()[c];
        } else if (i != 17) {
            return Alignment.ALIGN_NORMAL;
        } else {
            return Alignment.ALIGN_CENTER;
        }
    }

    private static Layout createTextLayout(int i, YogaMeasureMode yogaMeasureMode, TruncateAt truncateAt, boolean z, int i2, boolean z2, CharSequence charSequence, int i3, float f, float f2, int i4, Alignment alignment) {
        int i5;
        switch (yogaMeasureMode) {
            case UNDEFINED:
                i5 = 0;
                break;
            case EXACTLY:
                i5 = 1;
                break;
            case AT_MOST:
                i5 = 2;
                break;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Unexpected size mode: ");
                sb.append(yogaMeasureMode);
                throw new IllegalStateException(sb.toString());
        }
        sTextLayoutBuilder.setEllipsize(truncateAt).setMaxLines(i2).setSingleLine(z2).setText(charSequence).setTextSize(i3).setWidth(i, i5);
        sTextLayoutBuilder.setTextStyle(i4);
        sTextLayoutBuilder.setTextDirection(TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR);
        sTextLayoutBuilder.setIncludeFontPadding(z);
        sTextLayoutBuilder.setTextSpacingExtra(f);
        sTextLayoutBuilder.setTextSpacingMultiplier(f2);
        sTextLayoutBuilder.setAlignment(alignment);
        Layout build = sTextLayoutBuilder.build();
        sTextLayoutBuilder.setText(null);
        return build;
    }
}
