package com.opengarden.firechat.preference;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VectorPreferenceCategory extends PreferenceCategory {
    public VectorPreferenceCategory(Context context) {
        super(context);
    }

    public VectorPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public VectorPreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        View onCreateView = super.onCreateView(viewGroup);
        TextView textView = (TextView) onCreateView.findViewById(16908310);
        if (textView != null) {
            textView.setTypeface(null, 1);
        }
        return onCreateView;
    }
}
