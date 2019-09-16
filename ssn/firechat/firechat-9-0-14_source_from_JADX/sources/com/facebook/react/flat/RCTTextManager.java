package com.facebook.react.flat;

public final class RCTTextManager extends FlatViewManager {
    static final String REACT_CLASS = "RCTText";

    public String getName() {
        return "RCTText";
    }

    public /* bridge */ /* synthetic */ void removeAllViews(FlatViewGroup flatViewGroup) {
        super.removeAllViews(flatViewGroup);
    }

    public /* bridge */ /* synthetic */ void setBackgroundColor(FlatViewGroup flatViewGroup, int i) {
        super.setBackgroundColor(flatViewGroup, i);
    }

    public RCTText createShadowNodeInstance() {
        return new RCTText();
    }

    public Class<RCTText> getShadowNodeClass() {
        return RCTText.class;
    }
}
