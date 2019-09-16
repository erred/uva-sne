package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.p000v4.widget.DrawerLayout;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.internal.C0487Utils;
import butterknife.internal.DebouncingOnClickListener;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.view.VectorPendingCallView;

public class VectorHomeActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private VectorHomeActivity target;
    private View view2131296348;
    private View view2131296351;
    private View view2131296356;
    private View view2131296514;

    @UiThread
    public VectorHomeActivity_ViewBinding(VectorHomeActivity vectorHomeActivity) {
        this(vectorHomeActivity, vectorHomeActivity.getWindow().getDecorView());
    }

    @UiThread
    public VectorHomeActivity_ViewBinding(final VectorHomeActivity vectorHomeActivity, View view) {
        super(vectorHomeActivity, view);
        this.target = vectorHomeActivity;
        vectorHomeActivity.waitingView = C0487Utils.findRequiredView(view, C1299R.C1301id.listView_spinner_views, "field 'waitingView'");
        vectorHomeActivity.mFloatingActionsMenu = (FloatingActionsMenu) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.floating_action_menu, "field 'mFloatingActionsMenu'", FloatingActionsMenu.class);
        vectorHomeActivity.mFabMain = (AddFloatingActionButton) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.fab_expand_menu_button, "field 'mFabMain'", AddFloatingActionButton.class);
        View findRequiredView = C0487Utils.findRequiredView(view, C1299R.C1301id.button_start_chat, "field 'mFabStartChat' and method 'fabMenuStartChat'");
        vectorHomeActivity.mFabStartChat = (FloatingActionButton) C0487Utils.castView(findRequiredView, C1299R.C1301id.button_start_chat, "field 'mFabStartChat'", FloatingActionButton.class);
        this.view2131296356 = findRequiredView;
        findRequiredView.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                vectorHomeActivity.fabMenuStartChat();
            }
        });
        View findRequiredView2 = C0487Utils.findRequiredView(view, C1299R.C1301id.button_create_room, "field 'mFabCreateRoom' and method 'fabMenuCreateRoom'");
        vectorHomeActivity.mFabCreateRoom = (FloatingActionButton) C0487Utils.castView(findRequiredView2, C1299R.C1301id.button_create_room, "field 'mFabCreateRoom'", FloatingActionButton.class);
        this.view2131296348 = findRequiredView2;
        findRequiredView2.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                vectorHomeActivity.fabMenuCreateRoom();
            }
        });
        View findRequiredView3 = C0487Utils.findRequiredView(view, C1299R.C1301id.button_join_room, "field 'mFabJoinRoom' and method 'fabMenuJoinRoom'");
        vectorHomeActivity.mFabJoinRoom = (FloatingActionButton) C0487Utils.castView(findRequiredView3, C1299R.C1301id.button_join_room, "field 'mFabJoinRoom'", FloatingActionButton.class);
        this.view2131296351 = findRequiredView3;
        findRequiredView3.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                vectorHomeActivity.fabMenuJoinRoom();
            }
        });
        vectorHomeActivity.mToolbar = (Toolbar) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.home_toolbar, "field 'mToolbar'", Toolbar.class);
        vectorHomeActivity.mDrawerLayout = (DrawerLayout) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.drawer_layout, "field 'mDrawerLayout'", DrawerLayout.class);
        vectorHomeActivity.mBottomNavigationView = (BottomNavigationView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.bottom_navigation, "field 'mBottomNavigationView'", BottomNavigationView.class);
        vectorHomeActivity.navigationView = (NavigationView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.navigation_view, "field 'navigationView'", NavigationView.class);
        vectorHomeActivity.mVectorPendingCallView = (VectorPendingCallView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.listView_pending_callview, "field 'mVectorPendingCallView'", VectorPendingCallView.class);
        vectorHomeActivity.mSyncInProgressView = (ProgressBar) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.home_recents_sync_in_progress, "field 'mSyncInProgressView'", ProgressBar.class);
        vectorHomeActivity.mSearchView = (SearchView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.search_view, "field 'mSearchView'", SearchView.class);
        View findRequiredView4 = C0487Utils.findRequiredView(view, C1299R.C1301id.floating_action_menu_touch_guard, "field 'touchGuard' and method 'touchGuardClicked'");
        vectorHomeActivity.touchGuard = findRequiredView4;
        this.view2131296514 = findRequiredView4;
        findRequiredView4.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View view) {
                vectorHomeActivity.touchGuardClicked();
            }
        });
    }

    public void unbind() {
        VectorHomeActivity vectorHomeActivity = this.target;
        if (vectorHomeActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        vectorHomeActivity.waitingView = null;
        vectorHomeActivity.mFloatingActionsMenu = null;
        vectorHomeActivity.mFabMain = null;
        vectorHomeActivity.mFabStartChat = null;
        vectorHomeActivity.mFabCreateRoom = null;
        vectorHomeActivity.mFabJoinRoom = null;
        vectorHomeActivity.mToolbar = null;
        vectorHomeActivity.mDrawerLayout = null;
        vectorHomeActivity.mBottomNavigationView = null;
        vectorHomeActivity.navigationView = null;
        vectorHomeActivity.mVectorPendingCallView = null;
        vectorHomeActivity.mSyncInProgressView = null;
        vectorHomeActivity.mSearchView = null;
        vectorHomeActivity.touchGuard = null;
        this.view2131296356.setOnClickListener(null);
        this.view2131296356 = null;
        this.view2131296348.setOnClickListener(null);
        this.view2131296348 = null;
        this.view2131296351.setOnClickListener(null);
        this.view2131296351 = null;
        this.view2131296514.setOnClickListener(null);
        this.view2131296514 = null;
        super.unbind();
    }
}
