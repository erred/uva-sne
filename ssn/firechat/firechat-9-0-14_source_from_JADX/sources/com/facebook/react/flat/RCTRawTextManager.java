package com.facebook.react.flat;

import android.view.View;

public final class RCTRawTextManager extends VirtualViewManager<RCTRawText> {
    static final String REACT_CLASS = "RCTRawText";

    public String getName() {
        return "RCTRawText";
    }

    public /* bridge */ /* synthetic */ void updateExtraData(View view, Object obj) {
        super.updateExtraData(view, obj);
    }

    public RCTRawText createShadowNodeInstance() {
        return new RCTRawText();
    }

    public Class<RCTRawText> getShadowNodeClass() {
        return RCTRawText.class;
    }
}
