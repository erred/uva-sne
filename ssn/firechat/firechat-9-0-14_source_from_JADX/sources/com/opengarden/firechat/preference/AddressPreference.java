package com.opengarden.firechat.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.opengarden.firechat.C1299R;

public class AddressPreference extends VectorCustomActionEditTextPreference {
    private boolean mIsMainIconVisible = false;
    private ImageView mMainAddressIconView;

    public AddressPreference(Context context) {
        super(context);
    }

    public AddressPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AddressPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        setWidgetLayoutResource(C1299R.layout.vector_settings_address_preference);
        View onCreateView = super.onCreateView(viewGroup);
        this.mMainAddressIconView = (ImageView) onCreateView.findViewById(C1299R.C1301id.main_address_icon_view);
        this.mMainAddressIconView.setVisibility(this.mIsMainIconVisible ? 0 : 8);
        return onCreateView;
    }

    public void setMainIconVisible(boolean z) {
        this.mIsMainIconVisible = z;
        if (this.mMainAddressIconView != null) {
            this.mMainAddressIconView.setVisibility(this.mIsMainIconVisible ? 0 : 8);
        }
    }

    public View getMainIconView() {
        return this.mMainAddressIconView;
    }
}
