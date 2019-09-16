package com.opengarden.firechat.view;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class HomeSectionView_ViewBinding implements Unbinder {
    private HomeSectionView target;

    @UiThread
    public HomeSectionView_ViewBinding(HomeSectionView homeSectionView) {
        this(homeSectionView, homeSectionView);
    }

    @UiThread
    public HomeSectionView_ViewBinding(HomeSectionView homeSectionView, View view) {
        this.target = homeSectionView;
        homeSectionView.mHeader = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.section_header, "field 'mHeader'", TextView.class);
        homeSectionView.mBadge = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.section_badge, "field 'mBadge'", TextView.class);
        homeSectionView.mRecyclerView = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.section_recycler_view, "field 'mRecyclerView'", RecyclerView.class);
        homeSectionView.mPlaceHolder = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.section_placeholder, "field 'mPlaceHolder'", TextView.class);
    }

    @CallSuper
    public void unbind() {
        HomeSectionView homeSectionView = this.target;
        if (homeSectionView == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        homeSectionView.mHeader = null;
        homeSectionView.mBadge = null;
        homeSectionView.mRecyclerView = null;
        homeSectionView.mPlaceHolder = null;
    }
}
