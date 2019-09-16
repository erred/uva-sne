package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.util.ThemeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterSection<T> {
    private final Comparator<T> mComparator;
    private final int mContentViewType;
    private Context mContext;
    CharSequence mCurrentFilterPattern;
    private final List<T> mFilteredItems;
    private final int mHeaderSubView;
    private final int mHeaderViewType;
    private boolean mIsHiddenWhenEmpty;
    private boolean mIsHiddenWhenNoFilter;
    private final List<T> mItems;
    private String mNoItemPlaceholder;
    private String mNoResultPlaceholder;
    final String mTitle;
    private SpannableString mTitleFormatted;

    public AdapterSection(Context context, String str, int i, int i2, int i3, int i4, List<T> list, Comparator<T> comparator) {
        this.mContext = context;
        this.mTitle = str;
        this.mItems = list;
        this.mFilteredItems = new ArrayList(list);
        this.mHeaderSubView = i;
        this.mHeaderViewType = i3;
        this.mContentViewType = i4;
        this.mComparator = comparator;
        updateTitle();
    }

    public void setItems(List<T> list, CharSequence charSequence) {
        if (this.mComparator != null) {
            Collections.sort(list, this.mComparator);
        }
        this.mItems.clear();
        this.mItems.addAll(list);
        setFilteredItems(list, charSequence);
    }

    public void setFilteredItems(List<T> list, CharSequence charSequence) {
        this.mFilteredItems.clear();
        this.mFilteredItems.addAll(list);
        this.mCurrentFilterPattern = charSequence;
        updateTitle();
    }

    /* access modifiers changed from: 0000 */
    public void updateTitle() {
        String str;
        if (getNbItems() > 0) {
            String str2 = this.mTitle;
            StringBuilder sb = new StringBuilder();
            sb.append("   ");
            sb.append(getNbItems());
            str = str2.concat(sb.toString());
        } else {
            str = this.mTitle;
        }
        formatTitle(str);
    }

    /* access modifiers changed from: 0000 */
    public void formatTitle(String str) {
        SpannableString spannableString = new SpannableString(str.toUpperCase(VectorApp.getApplicationLocale()));
        spannableString.setSpan(new ForegroundColorSpan(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.list_header_subtext_color)), this.mTitle.length(), str.length(), 0);
        this.mTitleFormatted = spannableString;
    }

    public SpannableString getTitle() {
        return this.mTitleFormatted;
    }

    public int getHeaderSubView() {
        return this.mHeaderSubView;
    }

    public String getEmptyViewPlaceholder() {
        return TextUtils.isEmpty(this.mCurrentFilterPattern) ? this.mNoItemPlaceholder : this.mNoResultPlaceholder;
    }

    public void setEmptyViewPlaceholder(String str) {
        this.mNoItemPlaceholder = str;
        this.mNoResultPlaceholder = str;
    }

    public void setEmptyViewPlaceholder(String str, String str2) {
        this.mNoItemPlaceholder = str;
        this.mNoResultPlaceholder = str2;
    }

    public int getHeaderViewType() {
        return this.mHeaderViewType;
    }

    public int getContentViewType() {
        return this.mContentViewType;
    }

    public List<T> getItems() {
        return this.mItems;
    }

    public List<T> getFilteredItems() {
        return this.mFilteredItems;
    }

    public int getNbItems() {
        return this.mFilteredItems.size();
    }

    public void resetFilter() {
        this.mFilteredItems.clear();
        this.mFilteredItems.addAll(this.mItems);
        this.mCurrentFilterPattern = null;
        updateTitle();
    }

    public void setIsHiddenWhenEmpty(boolean z) {
        this.mIsHiddenWhenEmpty = z;
    }

    public void setIsHiddenWhenNoFilter(boolean z) {
        this.mIsHiddenWhenNoFilter = z;
    }

    public boolean shouldBeHidden() {
        return (this.mIsHiddenWhenEmpty && getItems().isEmpty()) || (this.mIsHiddenWhenNoFilter && TextUtils.isEmpty(this.mCurrentFilterPattern));
    }

    public boolean removeItem(T t) {
        if (this.mFilteredItems.contains(t)) {
            this.mFilteredItems.remove(t);
            updateTitle();
            return true;
        }
        this.mItems.remove(t);
        return false;
    }
}
