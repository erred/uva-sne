package com.facebook.react.flat;

import android.view.View;

public final class RCTTextInlineImageManager extends VirtualViewManager<RCTTextInlineImage> {
    static final String REACT_CLASS = "RCTTextInlineImage";

    public String getName() {
        return REACT_CLASS;
    }

    public /* bridge */ /* synthetic */ void updateExtraData(View view, Object obj) {
        super.updateExtraData(view, obj);
    }

    public RCTTextInlineImage createShadowNodeInstance() {
        return new RCTTextInlineImage();
    }

    public Class<RCTTextInlineImage> getShadowNodeClass() {
        return RCTTextInlineImage.class;
    }
}
