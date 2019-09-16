package com.opengarden.firechat.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;

public class ProgressBarPreference extends Preference {
    private final Context mContext;

    public ProgressBarPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public ProgressBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public ProgressBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        super.onCreateView(viewGroup);
        return ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C1299R.layout.vector_settings_spinner_preference, viewGroup, false);
    }
}
