package com.facebook.fbui.textlayoutbuilder.util;

import android.os.Build.VERSION;
import android.text.Layout;
import android.text.StaticLayout;

public class LayoutMeasureUtil {
    public static int getWidth(Layout layout) {
        if (layout == null) {
            return 0;
        }
        int lineCount = layout.getLineCount();
        int i = 0;
        for (int i2 = 0; i2 < lineCount; i2++) {
            i = Math.max(i, (int) layout.getLineRight(i2));
        }
        return i;
    }

    public static int getHeight(Layout layout) {
        int i = 0;
        if (layout == null) {
            return 0;
        }
        if (VERSION.SDK_INT < 20 && (layout instanceof StaticLayout)) {
            float lineDescent = (float) (layout.getLineDescent(layout.getLineCount() - 1) - layout.getLineAscent(layout.getLineCount() - 1));
            float spacingAdd = lineDescent - ((lineDescent - layout.getSpacingAdd()) / layout.getSpacingMultiplier());
            i = spacingAdd >= 0.0f ? (int) (((double) spacingAdd) + 0.5d) : -((int) (((double) (-spacingAdd)) + 0.5d));
        }
        return layout.getHeight() - i;
    }
}
