package com.opengarden.firechat.preference;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;

public class VectorDividerCategory extends PreferenceCategory {
    private final Context mContext;

    public VectorDividerCategory(Context context) {
        super(context);
        this.mContext = context;
    }

    public VectorDividerCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public VectorDividerCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        View onCreateView = super.onCreateView(viewGroup);
        View inflate = ((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(C1299R.layout.vector_preference_divider, viewGroup, false);
        return inflate != null ? inflate : onCreateView;
    }
}
