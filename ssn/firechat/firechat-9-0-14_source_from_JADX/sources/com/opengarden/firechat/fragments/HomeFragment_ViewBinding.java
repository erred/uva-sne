package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p000v4.widget.NestedScrollView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.view.HomeSectionView;

public class HomeFragment_ViewBinding implements Unbinder {
    private HomeFragment target;

    @UiThread
    public HomeFragment_ViewBinding(HomeFragment homeFragment, View view) {
        this.target = homeFragment;
        homeFragment.mNestedScrollView = (NestedScrollView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.nested_scrollview, "field 'mNestedScrollView'", NestedScrollView.class);
        homeFragment.mInvitationsSection = (HomeSectionView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.invitations_section, "field 'mInvitationsSection'", HomeSectionView.class);
        homeFragment.mFavouritesSection = (HomeSectionView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.favourites_section, "field 'mFavouritesSection'", HomeSectionView.class);
        homeFragment.mDirectChatsSection = (HomeSectionView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.direct_chats_section, "field 'mDirectChatsSection'", HomeSectionView.class);
        homeFragment.mRoomsSection = (HomeSectionView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.rooms_section, "field 'mRoomsSection'", HomeSectionView.class);
        homeFragment.mLowPrioritySection = (HomeSectionView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.low_priority_section, "field 'mLowPrioritySection'", HomeSectionView.class);
    }

    @CallSuper
    public void unbind() {
        HomeFragment homeFragment = this.target;
        if (homeFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        homeFragment.mNestedScrollView = null;
        homeFragment.mInvitationsSection = null;
        homeFragment.mFavouritesSection = null;
        homeFragment.mDirectChatsSection = null;
        homeFragment.mRoomsSection = null;
        homeFragment.mLowPrioritySection = null;
    }
}
