package com.opengarden.firechat.util;

import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.OnScrollListener;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.util.Pair;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.opengarden.firechat.adapters.AdapterSection;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.view.SectionView;
import java.util.ArrayList;
import java.util.List;

public class StickySectionHelper extends OnScrollListener implements OnLayoutChangeListener {
    private final String LOG_TAG = StickySectionHelper.class.getSimpleName();
    private int mFooterBottom = 0;
    private int mFooterTop = 0;
    private int mHeaderBottom = 0;
    /* access modifiers changed from: private */
    public final LinearLayoutManager mLayoutManager;
    /* access modifiers changed from: private */
    public final RecyclerView mRecyclerView;
    private final List<Pair<Integer, SectionView>> mSectionViews = new ArrayList();

    public StickySectionHelper(RecyclerView recyclerView, List<Pair<Integer, AdapterSection>> list) {
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnScrollListener(this);
        this.mRecyclerView.addOnLayoutChangeListener(this);
        this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        for (Pair pair : list) {
            final SectionView sectionView = new SectionView(this.mRecyclerView.getContext(), (AdapterSection) pair.second);
            sectionView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    int access$000 = StickySectionHelper.this.getPositionForSectionView(sectionView);
                    StickySectionHelper.this.mRecyclerView.stopScroll();
                    StickySectionHelper.this.mLayoutManager.scrollToPositionWithOffset(access$000, sectionView.getHeaderTop());
                }
            });
            this.mSectionViews.add(new Pair(pair.first, sectionView));
        }
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i4 == i8 || i4 <= this.mFooterBottom) {
            updateStickySection(-1);
        } else {
            computeSectionViewsCoordinates(view, this.mSectionViews);
        }
    }

    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        if (i2 != 0) {
            updateStickySection(i2);
        }
    }

    public void resetSticky(List<Pair<Integer, AdapterSection>> list) {
        Log.m209d(this.LOG_TAG, "resetSticky");
        if (!this.mSectionViews.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                Pair pair = (Pair) this.mSectionViews.get(i);
                if (pair != null) {
                    arrayList.add(new Pair(((Pair) list.get(i)).first, pair.second));
                    ((SectionView) ((Pair) this.mSectionViews.get(i)).second).updateTitle();
                }
            }
            this.mSectionViews.clear();
            this.mSectionViews.addAll(arrayList);
            setBottom(this.mFooterBottom);
            this.mHeaderBottom = 0;
            computeSectionViewsCoordinates(this.mRecyclerView, this.mSectionViews);
        }
    }

    private void setBottom(int i) {
        this.mFooterTop = i;
        this.mFooterBottom = i;
    }

    public SectionView getSectionViewForSectionIndex(int i) {
        return (SectionView) ((Pair) this.mSectionViews.get(i)).second;
    }

    public View findSectionSubViewById(int i) {
        if (this.mSectionViews != null) {
            for (Pair pair : this.mSectionViews) {
                View findViewById = ((SectionView) pair.second).findViewById(i);
                if (findViewById != null) {
                    return findViewById;
                }
            }
        }
        return null;
    }

    private void computeSectionViewsCoordinates(View view, List<Pair<Integer, SectionView>> list) {
        setBottom(view.getBottom());
        this.mHeaderBottom = 0;
        Log.m213i(this.LOG_TAG, "computeSectionViewsCoordinates");
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                SectionView sectionView = (SectionView) ((Pair) list.get(i)).second;
                sectionView.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
                int stickyHeaderHeight = sectionView.getStickyHeaderHeight();
                if (sectionView.getSection().shouldBeHidden()) {
                    sectionView.setVisibility(8);
                    sectionView.setHeaderTop(0 - sectionView.getStickyHeaderHeight());
                    sectionView.setHeaderBottom(0);
                } else {
                    sectionView.setVisibility(0);
                    sectionView.setHeaderTop(this.mHeaderBottom);
                    sectionView.setHeaderBottom(this.mHeaderBottom + sectionView.getStickyHeaderHeight());
                    this.mHeaderBottom += stickyHeaderHeight;
                }
            }
            for (int size = list.size() - 1; size >= 0; size--) {
                SectionView sectionView2 = (SectionView) ((Pair) list.get(size)).second;
                sectionView2.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
                int stickyHeaderHeight2 = sectionView2.getStickyHeaderHeight();
                sectionView2.setFooterTop(this.mFooterTop - sectionView2.getStickyHeaderHeight());
                sectionView2.setFooterBottom(this.mFooterTop);
                if (!sectionView2.getSection().shouldBeHidden()) {
                    this.mFooterTop -= stickyHeaderHeight2;
                }
            }
        }
        for (Pair pair : this.mSectionViews) {
            removeViewFromParent((View) pair.second);
            ((ViewGroup) view.getParent()).addView((View) pair.second);
        }
        updateStickySection(-1);
    }

    private void updateStickySection(int i) {
        int findFirstVisibleItemPosition = this.mLayoutManager.findFirstVisibleItemPosition();
        int findLastVisibleItemPosition = this.mLayoutManager.findLastVisibleItemPosition();
        int size = this.mSectionViews.size() - 1;
        while (size >= 0) {
            SectionView sectionView = size > 0 ? (SectionView) ((Pair) this.mSectionViews.get(size - 1)).second : null;
            int intValue = ((Integer) ((Pair) this.mSectionViews.get(size)).first).intValue();
            SectionView sectionView2 = (SectionView) ((Pair) this.mSectionViews.get(size)).second;
            ViewHolder findViewHolderForLayoutPosition = this.mRecyclerView.findViewHolderForLayoutPosition(intValue);
            if (findViewHolderForLayoutPosition != null) {
                String str = this.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("updateStickySection holder top ");
                sb.append(findViewHolderForLayoutPosition.itemView.getTop());
                sb.append(" bottom ");
                sb.append(findViewHolderForLayoutPosition.itemView.getBottom());
                Log.m209d(str, sb.toString());
                sectionView2.updatePosition(findViewHolderForLayoutPosition.itemView.getTop());
                if (sectionView != null) {
                    sectionView.onFoldSubView(sectionView2, i);
                }
            } else if (intValue < findFirstVisibleItemPosition) {
                sectionView2.updatePosition(sectionView2.getHeaderTop());
            } else if (intValue > findLastVisibleItemPosition) {
                sectionView2.updatePosition(sectionView2.getFooterTop());
            }
            size--;
        }
    }

    /* access modifiers changed from: private */
    public int getPositionForSectionView(SectionView sectionView) {
        for (Pair pair : this.mSectionViews) {
            if (sectionView == pair.second) {
                return ((Integer) pair.first).intValue();
            }
        }
        return -1;
    }

    private static void removeViewFromParent(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }
}
