package com.facebook.react.flat;

import android.view.View;

public final class RCTVirtualTextManager extends VirtualViewManager<RCTVirtualText> {
    static final String REACT_CLASS = "RCTVirtualText";

    public String getName() {
        return "RCTVirtualText";
    }

    public /* bridge */ /* synthetic */ void updateExtraData(View view, Object obj) {
        super.updateExtraData(view, obj);
    }

    public RCTVirtualText createShadowNodeInstance() {
        return new RCTVirtualText();
    }

    public Class<RCTVirtualText> getShadowNodeClass() {
        return RCTVirtualText.class;
    }
}
