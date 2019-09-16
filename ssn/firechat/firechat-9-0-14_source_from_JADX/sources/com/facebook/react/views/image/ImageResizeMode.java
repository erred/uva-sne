package com.facebook.react.views.image;

import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import javax.annotation.Nullable;

public class ImageResizeMode {
    public static ScaleType toScaleType(@Nullable String str) {
        if ("contain".equals(str)) {
            return ScaleType.FIT_CENTER;
        }
        if ("cover".equals(str)) {
            return ScaleType.CENTER_CROP;
        }
        if ("stretch".equals(str)) {
            return ScaleType.FIT_XY;
        }
        if ("center".equals(str)) {
            return ScaleType.CENTER_INSIDE;
        }
        if (str == null) {
            return defaultValue();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid resize mode: '");
        sb.append(str);
        sb.append("'");
        throw new JSApplicationIllegalArgumentException(sb.toString());
    }

    public static ScaleType defaultValue() {
        return ScaleType.CENTER_CROP;
    }
}
