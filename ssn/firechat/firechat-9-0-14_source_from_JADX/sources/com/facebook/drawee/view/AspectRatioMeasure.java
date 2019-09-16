package com.facebook.drawee.view;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import javax.annotation.Nullable;

public class AspectRatioMeasure {

    public static class Spec {
        public int height;
        public int width;
    }

    private static boolean shouldAdjust(int i) {
        return i == 0 || i == -2;
    }

    public static void updateMeasureSpec(Spec spec, float f, @Nullable LayoutParams layoutParams, int i, int i2) {
        if (f > 0.0f && layoutParams != null) {
            if (shouldAdjust(layoutParams.height)) {
                spec.height = MeasureSpec.makeMeasureSpec(View.resolveSize((int) ((((float) (MeasureSpec.getSize(spec.width) - i)) / f) + ((float) i2)), spec.height), ErrorDialogData.SUPPRESSED);
            } else if (shouldAdjust(layoutParams.width)) {
                spec.width = MeasureSpec.makeMeasureSpec(View.resolveSize((int) ((((float) (MeasureSpec.getSize(spec.height) - i2)) * f) + ((float) i)), spec.width), ErrorDialogData.SUPPRESSED);
            }
        }
    }
}
