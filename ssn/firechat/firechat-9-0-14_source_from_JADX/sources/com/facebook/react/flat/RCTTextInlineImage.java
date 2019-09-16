package com.facebook.react.flat;

import android.text.SpannableStringBuilder;
import com.RNFetchBlob.RNFetchBlobConst;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.imagehelper.ImageSource;
import javax.annotation.Nullable;

class RCTTextInlineImage extends FlatTextShadowNode {
    private InlineImageSpanWithPipeline mInlineImageSpan = new InlineImageSpanWithPipeline();

    RCTTextInlineImage() {
    }

    public void setStyleWidth(float f) {
        super.setStyleWidth(f);
        if (this.mInlineImageSpan.getWidth() != f) {
            getMutableSpan().setWidth(f);
            notifyChanged(true);
        }
    }

    public void setStyleHeight(float f) {
        super.setStyleHeight(f);
        if (this.mInlineImageSpan.getHeight() != f) {
            getMutableSpan().setHeight(f);
            notifyChanged(true);
        }
    }

    /* access modifiers changed from: protected */
    public void performCollectText(SpannableStringBuilder spannableStringBuilder) {
        spannableStringBuilder.append("I");
    }

    /* access modifiers changed from: protected */
    public void performApplySpans(SpannableStringBuilder spannableStringBuilder, int i, int i2, boolean z) {
        this.mInlineImageSpan.freeze();
        spannableStringBuilder.setSpan(this.mInlineImageSpan, i, i2, 17);
    }

    /* access modifiers changed from: protected */
    public void performCollectAttachDetachListeners(StateBuilder stateBuilder) {
        stateBuilder.addAttachDetachListener(this.mInlineImageSpan);
    }

    @ReactProp(name = "src")
    public void setSource(@Nullable ReadableArray readableArray) {
        ImageSource imageSource;
        ImageRequest imageRequest = null;
        String string = (readableArray == null || readableArray.size() == 0) ? null : readableArray.getMap(0).getString(RNFetchBlobConst.DATA_ENCODE_URI);
        if (string == null) {
            imageSource = null;
        } else {
            imageSource = new ImageSource(getThemedContext(), string);
        }
        InlineImageSpanWithPipeline mutableSpan = getMutableSpan();
        if (imageSource != null) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(imageSource.getUri()).build();
        }
        mutableSpan.setImageRequest(imageRequest);
    }

    private InlineImageSpanWithPipeline getMutableSpan() {
        if (this.mInlineImageSpan.isFrozen()) {
            this.mInlineImageSpan = this.mInlineImageSpan.mutableCopy();
        }
        return this.mInlineImageSpan;
    }
}
