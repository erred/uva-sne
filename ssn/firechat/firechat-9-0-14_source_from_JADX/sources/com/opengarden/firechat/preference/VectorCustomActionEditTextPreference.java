package com.opengarden.firechat.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorCustomActionEditTextPreference extends Preference {
    private static final String LOG_TAG = "VectorCustomActionEditTextPreference";
    private OnPreferenceLongClickListener mOnClickLongListener;
    private int mTypeface = 0;

    public interface OnPreferenceLongClickListener {
        boolean onPreferenceLongClick(Preference preference);
    }

    public VectorCustomActionEditTextPreference(Context context) {
        super(context);
    }

    public VectorCustomActionEditTextPreference(Context context, int i) {
        super(context);
        this.mTypeface = i;
    }

    public VectorCustomActionEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public VectorCustomActionEditTextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        View onCreateView = super.onCreateView(viewGroup);
        addClickListeners(onCreateView);
        return onCreateView;
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        addClickListeners(view);
        try {
            TextView textView = (TextView) view.findViewById(16908310);
            TextView textView2 = (TextView) view.findViewById(16908304);
            if (textView != null) {
                textView.setSingleLine(false);
                textView.setTypeface(null, this.mTypeface);
            }
            if (textView != textView2) {
                textView2.setTypeface(null, this.mTypeface);
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onBindView ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    private void addClickListeners(View view) {
        view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (VectorCustomActionEditTextPreference.this.getOnPreferenceLongClickListener() != null) {
                    return VectorCustomActionEditTextPreference.this.getOnPreferenceLongClickListener().onPreferenceLongClick(VectorCustomActionEditTextPreference.this);
                }
                return false;
            }
        });
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorCustomActionEditTextPreference.this.getOnPreferenceClickListener() != null) {
                    VectorCustomActionEditTextPreference.this.getOnPreferenceClickListener().onPreferenceClick(VectorCustomActionEditTextPreference.this);
                }
            }
        });
    }

    public void setOnPreferenceLongClickListener(OnPreferenceLongClickListener onPreferenceLongClickListener) {
        this.mOnClickLongListener = onPreferenceLongClickListener;
    }

    /* access modifiers changed from: private */
    public OnPreferenceLongClickListener getOnPreferenceLongClickListener() {
        return this.mOnClickLongListener;
    }
}
