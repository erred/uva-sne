package com.opengarden.firechat.adapters;

import android.content.Context;
import java.util.Comparator;
import java.util.List;

public class GroupAdapterSection<T> extends AdapterSection<T> {
    public GroupAdapterSection(Context context, String str, int i, int i2, int i3, int i4, List<T> list, Comparator<T> comparator) {
        super(context, str, i, i2, i3, i4, list, comparator);
    }

    /* access modifiers changed from: 0000 */
    public void updateTitle() {
        String str;
        if (getItems().size() == getFilteredItems().size() || getNbItems() <= 0) {
            str = this.mTitle;
        } else {
            String str2 = this.mTitle;
            StringBuilder sb = new StringBuilder();
            sb.append("   ");
            sb.append(getNbItems());
            str = str2.concat(sb.toString());
        }
        formatTitle(str);
    }
}
