package com.facebook.react.flat;

import android.text.SpannableStringBuilder;
import com.facebook.react.uimanager.annotations.ReactProp;
import javax.annotation.Nullable;

final class RCTRawText extends FlatTextShadowNode {
    @Nullable
    private String mText;

    /* access modifiers changed from: protected */
    public void performCollectAttachDetachListeners(StateBuilder stateBuilder) {
    }

    RCTRawText() {
    }

    /* access modifiers changed from: protected */
    public void performCollectText(SpannableStringBuilder spannableStringBuilder) {
        if (this.mText != null) {
            spannableStringBuilder.append(this.mText);
        }
    }

    /* access modifiers changed from: protected */
    public void performApplySpans(SpannableStringBuilder spannableStringBuilder, int i, int i2, boolean z) {
        spannableStringBuilder.setSpan(this, i, i2, 17);
    }

    @ReactProp(name = "text")
    public void setText(@Nullable String str) {
        this.mText = str;
        notifyChanged(true);
    }
}
