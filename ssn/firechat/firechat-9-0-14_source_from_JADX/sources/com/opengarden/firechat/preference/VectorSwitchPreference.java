package com.opengarden.firechat.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorSwitchPreference extends CheckBoxPreference {
    private static final String LOG_TAG = "VectorSwitchPreference";

    public VectorSwitchPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public VectorSwitchPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    @TargetApi(21)
    public VectorSwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init();
    }

    public VectorSwitchPreference(Context context) {
        super(context, null);
        init();
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        if (VERSION.SDK_INT < 21) {
            clearListenerInViewGroup((ViewGroup) view);
        }
        super.onBindView(view);
        try {
            TextView textView = (TextView) view.findViewById(16908310);
            if (textView != null) {
                textView.setSingleLine(false);
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onBindView ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    private void clearListenerInViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof Switch) {
                    ((Switch) childAt).setOnCheckedChangeListener(null);
                    return;
                }
                if (childAt instanceof ViewGroup) {
                    clearListenerInViewGroup((ViewGroup) childAt);
                }
            }
        }
    }

    private void init() {
        setWidgetLayoutResource(C1299R.layout.preference_switch_layout);
    }
}
