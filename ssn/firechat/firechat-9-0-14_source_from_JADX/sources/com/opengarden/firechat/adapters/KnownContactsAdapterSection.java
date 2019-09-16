package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.TextUtils;
import java.util.Comparator;
import java.util.List;

class KnownContactsAdapterSection extends AdapterSection<ParticipantAdapterItem> {
    private String mCustomHeaderExtra;
    private boolean mIsLimited;

    public KnownContactsAdapterSection(Context context, String str, int i, int i2, int i3, int i4, List<ParticipantAdapterItem> list, Comparator<ParticipantAdapterItem> comparator) {
        super(context, str, i, i2, i3, i4, list, comparator);
    }

    public void setIsLimited(boolean z) {
        this.mIsLimited = z;
    }

    public void setCustomHeaderExtra(String str) {
        this.mCustomHeaderExtra = str;
    }

    /* access modifiers changed from: protected */
    public void updateTitle() {
        String str;
        if (getNbItems() <= 0) {
            str = this.mTitle;
        } else if (!TextUtils.isEmpty(this.mCustomHeaderExtra)) {
            String str2 = this.mTitle;
            StringBuilder sb = new StringBuilder();
            sb.append("   ");
            sb.append(this.mCustomHeaderExtra);
            sb.append(", ");
            sb.append(getNbItems());
            str = str2.concat(sb.toString());
        } else if (!this.mIsLimited) {
            String str3 = this.mTitle;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("   ");
            sb2.append(getNbItems());
            str = str3.concat(sb2.toString());
        } else {
            String str4 = this.mTitle;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("   >");
            sb3.append(getNbItems());
            str = str4.concat(sb3.toString());
        }
        formatTitle(str);
    }
}
