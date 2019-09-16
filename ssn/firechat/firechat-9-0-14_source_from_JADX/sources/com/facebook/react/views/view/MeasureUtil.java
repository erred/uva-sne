package com.facebook.react.views.view;

import android.view.View.MeasureSpec;
import com.facebook.yoga.YogaMeasureMode;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;

public class MeasureUtil {
    public static int getMeasureSpec(float f, YogaMeasureMode yogaMeasureMode) {
        if (yogaMeasureMode == YogaMeasureMode.EXACTLY) {
            return MeasureSpec.makeMeasureSpec((int) f, ErrorDialogData.SUPPRESSED);
        }
        if (yogaMeasureMode == YogaMeasureMode.AT_MOST) {
            return MeasureSpec.makeMeasureSpec((int) f, Integer.MIN_VALUE);
        }
        return MeasureSpec.makeMeasureSpec(0, 0);
    }
}
