package com.facebook.react.flat;

import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.flat.AbstractDrawCommand;
import com.facebook.react.flat.DrawImage;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.image.ImageResizeMode;
import javax.annotation.Nullable;

class RCTImageView<T extends AbstractDrawCommand & DrawImage> extends FlatShadowNode {
    static Object sCallerContext = RCTImageView.class;
    private T mDrawImage;

    static void setCallerContext(Object obj) {
        sCallerContext = obj;
    }

    static Object getCallerContext() {
        return sCallerContext;
    }

    RCTImageView(T t) {
        this.mDrawImage = t;
    }

    /* access modifiers changed from: protected */
    public void collectState(StateBuilder stateBuilder, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        StateBuilder stateBuilder2 = stateBuilder;
        super.collectState(stateBuilder, f, f2, f3, f4, f5, f6, f7, f8);
        if (((DrawImage) this.mDrawImage).hasImageRequest()) {
            this.mDrawImage = this.mDrawImage.updateBoundsAndFreeze(f, f2, f3, f4, f5, f6, f7, f8);
            stateBuilder2.addDrawCommand(this.mDrawImage);
            stateBuilder2.addAttachDetachListener((AttachDetachListener) this.mDrawImage);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean doesDraw() {
        return ((DrawImage) this.mDrawImage).hasImageRequest() || super.doesDraw();
    }

    @ReactProp(name = "shouldNotifyLoadEvents")
    public void setShouldNotifyLoadEvents(boolean z) {
        ((DrawImage) getMutableDrawImage()).setReactTag(z ? getReactTag() : 0);
    }

    @ReactProp(name = "src")
    public void setSource(@Nullable ReadableArray readableArray) {
        ((DrawImage) getMutableDrawImage()).setSource(getThemedContext(), readableArray);
    }

    @ReactProp(name = "tintColor")
    public void setTintColor(int i) {
        ((DrawImage) getMutableDrawImage()).setTintColor(i);
    }

    @ReactProp(name = "resizeMode")
    public void setResizeMode(@Nullable String str) {
        ScaleType scaleType = ImageResizeMode.toScaleType(str);
        if (((DrawImage) this.mDrawImage).getScaleType() != scaleType) {
            ((DrawImage) getMutableDrawImage()).setScaleType(scaleType);
        }
    }

    @ReactProp(customType = "Color", name = "borderColor")
    public void setBorderColor(int i) {
        if (((DrawImage) this.mDrawImage).getBorderColor() != i) {
            ((DrawImage) getMutableDrawImage()).setBorderColor(i);
        }
    }

    public void setBorder(int i, float f) {
        super.setBorder(i, f);
        if (i == 8 && ((DrawImage) this.mDrawImage).getBorderWidth() != f) {
            ((DrawImage) getMutableDrawImage()).setBorderWidth(f);
        }
    }

    @ReactProp(name = "borderRadius")
    public void setBorderRadius(float f) {
        if (((DrawImage) this.mDrawImage).getBorderRadius() != f) {
            ((DrawImage) getMutableDrawImage()).setBorderRadius(PixelUtil.toPixelFromDIP(f));
        }
    }

    @ReactProp(name = "fadeDuration")
    public void setFadeDuration(int i) {
        ((DrawImage) getMutableDrawImage()).setFadeDuration(i);
    }

    @ReactProp(name = "progressiveRenderingEnabled")
    public void setProgressiveRenderingEnabled(boolean z) {
        ((DrawImage) getMutableDrawImage()).setProgressiveRenderingEnabled(z);
    }

    private T getMutableDrawImage() {
        if (this.mDrawImage.isFrozen()) {
            this.mDrawImage = this.mDrawImage.mutableCopy();
            invalidate();
        }
        return this.mDrawImage;
    }
}
