package com.opengarden.firechat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AdapterSection;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;

public class SectionView extends RelativeLayout {
    private final String LOG_TAG = SectionView.class.getSimpleName();
    private int mFooterTop;
    private int mHeaderTop;
    private ProgressBar mLoadingView;
    private AdapterSection mSection;
    private View mSubView;
    private TextView mTitleView;

    public SectionView(Context context, AdapterSection adapterSection) {
        super(context);
        setup(adapterSection);
    }

    public SectionView(Context context, AttributeSet attributeSet, AdapterSection adapterSection) {
        super(context, attributeSet);
        setup(adapterSection);
    }

    public SectionView(Context context, AttributeSet attributeSet, int i, AdapterSection adapterSection) {
        super(context, attributeSet, i);
        setup(adapterSection);
    }

    @TargetApi(21)
    public SectionView(Context context, AttributeSet attributeSet, int i, int i2, AdapterSection adapterSection) {
        super(context, attributeSet, i, i2);
        setup(adapterSection);
    }

    private void setup(AdapterSection adapterSection) {
        this.mSection = adapterSection;
        setBackgroundColor(ThemeUtils.INSTANCE.getColor(getContext(), C1299R.attr.list_header_background_color));
        setLayoutParams(new LayoutParams(-1, -2));
        View inflate = inflate(getContext(), C1299R.layout.adapter_sticky_header, null);
        this.mTitleView = (TextView) inflate.findViewById(C1299R.C1301id.section_title);
        this.mTitleView.setText(adapterSection.getTitle());
        this.mLoadingView = (ProgressBar) inflate.findViewById(C1299R.C1301id.section_loading);
        this.mLoadingView.setVisibility(4);
        if (adapterSection.getHeaderSubView() != -1) {
            this.mSubView = inflate(getContext(), adapterSection.getHeaderSubView(), null);
            LayoutParams layoutParams = new LayoutParams(-1, -2);
            layoutParams.addRule(3, inflate.getId());
            this.mSubView.setLayoutParams(layoutParams);
            addView(this.mSubView);
        }
        addView(inflate);
    }

    public void updateTitle() {
        this.mTitleView.setText(this.mSection.getTitle());
    }

    public void showLoadingView() {
        this.mLoadingView.setVisibility(0);
        this.mLoadingView.animate();
    }

    public void hideLoadingView() {
        this.mLoadingView.setVisibility(4);
    }

    public boolean isStickyHeader() {
        return getTranslationY() == ((float) this.mHeaderTop);
    }

    public void onFoldSubView(SectionView sectionView, int i) {
        if (this.mSubView != null && ((float) getHeaderTop()) == getTranslationY()) {
            float min = Math.min(0.0f, (sectionView.getTranslationY() - getTranslationY()) - ((float) getMeasuredHeight()));
            if (this.mSubView.getTranslationY() != min) {
                String str = this.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onFoldSubView new translation ");
                sb.append(min);
                Log.m209d(str, sb.toString());
                this.mSubView.setTranslationY(min);
            }
        }
    }

    public int getStickyHeaderHeight() {
        return getChildAt(this.mSubView != null ? 1 : 0).getMeasuredHeight();
    }

    public int getHeaderTop() {
        return this.mHeaderTop;
    }

    public void setHeaderTop(int i) {
        String str = this.LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("sectionview ");
        sb.append(this.mSection.getTitle());
        sb.append(" setHeaderTop ");
        sb.append(i);
        Log.m209d(str, sb.toString());
        this.mHeaderTop = i;
    }

    public void setHeaderBottom(int i) {
        String str = this.LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("sectionview ");
        sb.append(this.mSection.getTitle());
        sb.append(" setHeaderBottom ");
        sb.append(i);
        Log.m209d(str, sb.toString());
        setBottom(i);
    }

    public int getFooterTop() {
        return this.mFooterTop;
    }

    public void setFooterTop(int i) {
        String str = this.LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("sectionview ");
        sb.append(this.mSection.getTitle());
        sb.append(" setFooterTop ");
        sb.append(i);
        Log.m209d(str, sb.toString());
        this.mFooterTop = i;
    }

    public void setFooterBottom(int i) {
        String str = this.LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("sectionview ");
        sb.append(this.mSection.getTitle());
        sb.append(" setFooterBottom ");
        sb.append(i);
        Log.m209d(str, sb.toString());
    }

    public AdapterSection getSection() {
        return this.mSection;
    }

    public void updatePosition(int i) {
        int min = Math.min(Math.max(this.mHeaderTop, i), this.mFooterTop);
        float f = (float) min;
        if (getTranslationY() != f) {
            String str = this.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("sectionview ");
            sb.append(this.mSection.getTitle());
            sb.append(" updatePosition translation y ");
            sb.append(min);
            Log.m209d(str, sb.toString());
            setTranslationY(f);
            requestLayout();
            invalidate();
        }
    }
}
