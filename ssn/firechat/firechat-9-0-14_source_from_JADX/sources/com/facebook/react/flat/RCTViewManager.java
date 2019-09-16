package com.facebook.react.flat;

import android.graphics.Rect;
import android.os.Build.VERSION;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.facebook.react.views.view.ReactDrawableHelper;
import java.util.Map;
import javax.annotation.Nullable;

public final class RCTViewManager extends FlatViewManager {
    private static final int CMD_HOTSPOT_UPDATE = 1;
    private static final int CMD_SET_PRESSED = 2;
    static final String REACT_CLASS = "RCTView";
    private static final int[] TMP_INT_ARRAY = new int[2];

    public String getName() {
        return "RCTView";
    }

    public /* bridge */ /* synthetic */ void removeAllViews(FlatViewGroup flatViewGroup) {
        super.removeAllViews(flatViewGroup);
    }

    public /* bridge */ /* synthetic */ void setBackgroundColor(FlatViewGroup flatViewGroup, int i) {
        super.setBackgroundColor(flatViewGroup, i);
    }

    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.m139of("hotspotUpdate", Integer.valueOf(1), "setPressed", Integer.valueOf(2));
    }

    public RCTView createShadowNodeInstance() {
        return new RCTView();
    }

    public Class<RCTView> getShadowNodeClass() {
        return RCTView.class;
    }

    @ReactProp(name = "nativeBackgroundAndroid")
    public void setHotspot(FlatViewGroup flatViewGroup, @Nullable ReadableMap readableMap) {
        flatViewGroup.setHotspot(readableMap == null ? null : ReactDrawableHelper.createDrawableFromJSDescription(flatViewGroup.getContext(), readableMap));
    }

    public void receiveCommand(FlatViewGroup flatViewGroup, int i, @Nullable ReadableArray readableArray) {
        switch (i) {
            case 1:
                if (readableArray == null || readableArray.size() != 2) {
                    throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'updateHotspot' command");
                } else if (VERSION.SDK_INT >= 21) {
                    flatViewGroup.getLocationOnScreen(TMP_INT_ARRAY);
                    flatViewGroup.drawableHotspotChanged(PixelUtil.toPixelFromDIP(readableArray.getDouble(0)) - ((float) TMP_INT_ARRAY[0]), PixelUtil.toPixelFromDIP(readableArray.getDouble(1)) - ((float) TMP_INT_ARRAY[1]));
                    return;
                } else {
                    return;
                }
            case 2:
                if (readableArray == null || readableArray.size() != 1) {
                    throw new JSApplicationIllegalArgumentException("Illegal number of arguments for 'setPressed' command");
                }
                flatViewGroup.setPressed(readableArray.getBoolean(0));
                return;
            default:
                return;
        }
    }

    @ReactProp(name = "needsOffscreenAlphaCompositing")
    public void setNeedsOffscreenAlphaCompositing(FlatViewGroup flatViewGroup, boolean z) {
        flatViewGroup.setNeedsOffscreenAlphaCompositing(z);
    }

    @ReactProp(name = "pointerEvents")
    public void setPointerEvents(FlatViewGroup flatViewGroup, @Nullable String str) {
        flatViewGroup.setPointerEvents(parsePointerEvents(str));
    }

    @ReactProp(name = "removeClippedSubviews")
    public void setRemoveClippedSubviews(FlatViewGroup flatViewGroup, boolean z) {
        flatViewGroup.setRemoveClippedSubviews(z);
    }

    private static PointerEvents parsePointerEvents(@Nullable String str) {
        if (str != null) {
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != -2089141766) {
                if (hashCode != -2089112978) {
                    if (hashCode != 3005871) {
                        if (hashCode == 3387192 && str.equals("none")) {
                            c = 0;
                        }
                    } else if (str.equals(ReactScrollViewHelper.AUTO)) {
                        c = 1;
                    }
                } else if (str.equals("box-only")) {
                    c = 3;
                }
            } else if (str.equals("box-none")) {
                c = 2;
            }
            switch (c) {
                case 0:
                    return PointerEvents.NONE;
                case 1:
                    return PointerEvents.AUTO;
                case 2:
                    return PointerEvents.BOX_NONE;
                case 3:
                    return PointerEvents.BOX_ONLY;
            }
        }
        return PointerEvents.AUTO;
    }

    @ReactProp(name = "hitSlop")
    public void setHitSlop(FlatViewGroup flatViewGroup, @Nullable ReadableMap readableMap) {
        if (readableMap == null) {
            flatViewGroup.setHitSlopRect(null);
        } else {
            flatViewGroup.setHitSlopRect(new Rect((int) PixelUtil.toPixelFromDIP(readableMap.getDouble(ViewProps.LEFT)), (int) PixelUtil.toPixelFromDIP(readableMap.getDouble(ViewProps.TOP)), (int) PixelUtil.toPixelFromDIP(readableMap.getDouble(ViewProps.RIGHT)), (int) PixelUtil.toPixelFromDIP(readableMap.getDouble(ViewProps.BOTTOM))));
        }
    }
}
