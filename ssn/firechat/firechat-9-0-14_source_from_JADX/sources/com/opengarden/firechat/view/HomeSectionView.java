package com.opengarden.firechat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.AdapterDataObserver;
import android.support.p003v7.widget.RecyclerView.LayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Filter.FilterListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.adapters.HomeRoomAdapter;
import com.opengarden.firechat.adapters.HomeRoomAdapter.OnSelectRoomListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.ThemeUtils;
import java.util.List;

public class HomeSectionView extends RelativeLayout {
    private static final String LOG_TAG = "HomeSectionView";
    private HomeRoomAdapter mAdapter;
    @BindView(2131297013)
    TextView mBadge;
    private String mCurrentFilter;
    @BindView(2131297014)
    TextView mHeader;
    private boolean mHideIfEmpty;
    private String mNoItemPlaceholder;
    private String mNoResultPlaceholder;
    @BindView(2131297017)
    TextView mPlaceHolder;
    @BindView(2131297018)
    RecyclerView mRecyclerView;

    public HomeSectionView(Context context) {
        super(context);
        setup();
    }

    public HomeSectionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setup();
    }

    public HomeSectionView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setup();
    }

    @TargetApi(21)
    private HomeSectionView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setup();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAdapter = null;
    }

    private void setup() {
        inflate(getContext(), C1299R.layout.home_section_view, this);
        ButterKnife.bind((View) this);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(100.0f);
        gradientDrawable.setColor(ThemeUtils.INSTANCE.getColor(getContext(), C1299R.attr.activity_bottom_gradient_color));
        this.mBadge.setBackground(gradientDrawable);
        this.mHeader.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (HomeSectionView.this.mRecyclerView != null) {
                    HomeSectionView.this.mRecyclerView.stopScroll();
                    HomeSectionView.this.mRecyclerView.scrollToPosition(0);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void onDataUpdated() {
        if (this.mAdapter != null) {
            try {
                boolean isEmpty = this.mAdapter.isEmpty();
                boolean hasNoResult = this.mAdapter.hasNoResult();
                int badgeCount = this.mAdapter.getBadgeCount();
                int i = 0;
                setVisibility((!this.mHideIfEmpty || !isEmpty) ? 0 : 8);
                this.mBadge.setText(RoomUtils.formatUnreadMessagesCounter(badgeCount));
                this.mBadge.setVisibility(badgeCount == 0 ? 8 : 0);
                this.mRecyclerView.setVisibility(hasNoResult ? 8 : 0);
                TextView textView = this.mPlaceHolder;
                if (!hasNoResult) {
                    i = 8;
                }
                textView.setVisibility(i);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onDataUpdated() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void setTitle(@StringRes int i) {
        this.mHeader.setText(i);
    }

    public void setPlaceholders(String str, String str2) {
        this.mNoItemPlaceholder = str;
        this.mNoResultPlaceholder = str2;
        this.mPlaceHolder.setText(TextUtils.isEmpty(this.mCurrentFilter) ? this.mNoItemPlaceholder : this.mNoResultPlaceholder);
    }

    public void setHideIfEmpty(boolean z) {
        this.mHideIfEmpty = z;
        setVisibility((!this.mHideIfEmpty || (this.mAdapter != null && !this.mAdapter.isEmpty())) ? 0 : 8);
    }

    public void setupRoomRecyclerView(LayoutManager layoutManager, @LayoutRes int i, boolean z, OnSelectRoomListener onSelectRoomListener, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        this.mRecyclerView.setLayoutManager(layoutManager);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setNestedScrollingEnabled(z);
        HomeRoomAdapter homeRoomAdapter = new HomeRoomAdapter(getContext(), i, onSelectRoomListener, roomInvitationListener, moreRoomActionListener);
        this.mAdapter = homeRoomAdapter;
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
            public void onChanged() {
                super.onChanged();
                HomeSectionView.this.onDataUpdated();
            }
        });
    }

    public void onFilter(final String str, final OnFilterListener onFilterListener) {
        this.mAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                if (onFilterListener != null) {
                    onFilterListener.onFilterDone(i);
                }
                HomeSectionView.this.setCurrentFilter(str);
                HomeSectionView.this.mRecyclerView.getLayoutManager().scrollToPosition(0);
                HomeSectionView.this.onDataUpdated();
            }
        });
    }

    public void setCurrentFilter(String str) {
        if (this.mAdapter != null) {
            this.mCurrentFilter = str;
            this.mAdapter.onFilterDone(this.mCurrentFilter);
            this.mPlaceHolder.setText(TextUtils.isEmpty(this.mCurrentFilter) ? this.mNoItemPlaceholder : this.mNoResultPlaceholder);
        }
    }

    public void setRooms(List<Room> list) {
        if (this.mAdapter != null) {
            this.mAdapter.setRooms(list);
        }
    }

    public void scrollToPosition(int i) {
        this.mRecyclerView.scrollToPosition(i);
    }
}
