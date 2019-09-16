package com.opengarden.firechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.support.p003v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.GroupDetailsFragmentPagerAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.view.RiotViewPager;

public class VectorGroupDetailsActivity extends MXCActionBarActivity {
    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_TAB_INDEX = "VectorUnifiedSearchActivity.EXTRA_TAB_INDEX";
    private static final String LOG_TAG = VectorRoomDetailsActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public Group mGroup;
    private MXEventListener mGroupEventsListener = new MXEventListener() {
        private void refresh(String str) {
            if (VectorGroupDetailsActivity.this.mGroup != null && TextUtils.equals(VectorGroupDetailsActivity.this.mGroup.getGroupId(), str)) {
                VectorGroupDetailsActivity.this.refreshGroupInfo();
            }
        }

        public void onLeaveGroup(String str) {
            if (VectorGroupDetailsActivity.this.mRoom != null && TextUtils.equals(str, VectorGroupDetailsActivity.this.mGroup.getGroupId())) {
                VectorGroupDetailsActivity.this.finish();
            }
        }

        public void onNewGroupInvitation(String str) {
            refresh(str);
        }

        public void onJoinGroup(String str) {
            refresh(str);
        }

        public void onGroupProfileUpdate(String str) {
            if (VectorGroupDetailsActivity.this.mGroup != null && TextUtils.equals(VectorGroupDetailsActivity.this.mGroup.getGroupId(), str) && VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment() != null) {
                VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment().refreshViews();
            }
        }

        public void onGroupRoomsListUpdate(String str) {
            if (VectorGroupDetailsActivity.this.mGroup != null && TextUtils.equals(VectorGroupDetailsActivity.this.mGroup.getGroupId(), str)) {
                if (VectorGroupDetailsActivity.this.mPagerAdapter.getRoomsFragment() != null) {
                    VectorGroupDetailsActivity.this.mPagerAdapter.getRoomsFragment().refreshViews();
                }
                if (VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment() != null) {
                    VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment().refreshViews();
                }
            }
        }

        public void onGroupUsersListUpdate(String str) {
            if (VectorGroupDetailsActivity.this.mGroup != null && TextUtils.equals(VectorGroupDetailsActivity.this.mGroup.getGroupId(), str)) {
                if (VectorGroupDetailsActivity.this.mPagerAdapter.getPeopleFragment() != null) {
                    VectorGroupDetailsActivity.this.mPagerAdapter.getPeopleFragment().refreshViews();
                }
                if (VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment() != null) {
                    VectorGroupDetailsActivity.this.mPagerAdapter.getHomeFragment().refreshViews();
                }
            }
        }

        public void onGroupInvitedUsersListUpdate(String str) {
            onGroupUsersListUpdate(str);
        }
    };
    /* access modifiers changed from: private */
    public ProgressBar mGroupSyncInProgress;
    private GroupsManager mGroupsManager;
    private View mLoadingView;
    private RiotViewPager mPager;
    /* access modifiers changed from: private */
    public GroupDetailsFragmentPagerAdapter mPagerAdapter;
    private MXSession mSession;

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_group_details;
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            Intent intent = getIntent();
            if (!intent.hasExtra(EXTRA_GROUP_ID)) {
                Log.m211e(LOG_TAG, "No group id");
                finish();
                return;
            }
            this.mSession = Matrix.getInstance(getApplicationContext()).getSession(intent.getStringExtra("MXCActionBarActivity.EXTRA_MATRIX_ID"));
            if (this.mSession == null || !this.mSession.isAlive()) {
                finish();
                return;
            }
            this.mGroupsManager = this.mSession.getGroupsManager();
            String stringExtra = intent.getStringExtra(EXTRA_GROUP_ID);
            if (!MXSession.isGroupId(stringExtra)) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("invalid group id ");
                sb.append(stringExtra);
                Log.m211e(str, sb.toString());
                finish();
                return;
            }
            this.mGroup = this.mGroupsManager.getGroup(stringExtra);
            if (this.mGroup == null) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onCreate() : displaying ");
                sb2.append(stringExtra);
                sb2.append(" in preview mode");
                Log.m209d(str2, sb2.toString());
                this.mGroup = new Group(stringExtra);
            } else {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## onCreate() : displaying ");
                sb3.append(stringExtra);
                Log.m209d(str3, sb3.toString());
            }
            setWaitingView(findViewById(C1299R.C1301id.group_loading_layout));
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setDisplayShowHomeEnabled(true);
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
            this.mGroupSyncInProgress = (ProgressBar) findViewById(C1299R.C1301id.group_sync_in_progress);
            this.mPager = (RiotViewPager) findViewById(C1299R.C1301id.groups_pager);
            this.mPagerAdapter = new GroupDetailsFragmentPagerAdapter(getSupportFragmentManager(), this);
            this.mPager.setAdapter(this.mPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(C1299R.C1301id.group_tabs);
            ThemeUtils.INSTANCE.setTabLayoutTheme(this, tabLayout);
            int i = 0;
            if (intent.hasExtra("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX")) {
                this.mPager.setCurrentItem(getIntent().getIntExtra("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX", 0));
            } else {
                RiotViewPager riotViewPager = this.mPager;
                if (!isFirstCreation()) {
                    i = getSavedInstanceState().getInt("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX", 0);
                }
                riotViewPager.setCurrentItem(i);
            }
            tabLayout.setupWithViewPager(this.mPager);
            this.mPager.addOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageSelected(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                    View currentFocus = VectorGroupDetailsActivity.this.getCurrentFocus();
                    if (currentFocus != null) {
                        ((InputMethodManager) VectorGroupDetailsActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                }
            });
        }
    }

    public Group getGroup() {
        return this.mGroup;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (Fragment onActivityResult : getSupportFragmentManager().getFragments()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX", this.mPager.getCurrentItem());
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mGroupEventsListener);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        refreshGroupInfo();
        this.mSession.getDataHandler().addListener(this.mGroupEventsListener);
    }

    /* access modifiers changed from: private */
    public void refreshGroupInfo() {
        if (this.mGroup != null) {
            this.mGroupSyncInProgress.setVisibility(0);
            this.mGroupsManager.refreshGroupData(this.mGroup, new ApiCallback<Void>() {
                private void onDone() {
                    if (VectorGroupDetailsActivity.this.mGroupSyncInProgress != null) {
                        VectorGroupDetailsActivity.this.mGroupSyncInProgress.setVisibility(8);
                    }
                }

                public void onSuccess(Void voidR) {
                    onDone();
                }

                public void onNetworkError(Exception exc) {
                    onDone();
                }

                public void onMatrixError(MatrixError matrixError) {
                    onDone();
                }

                public void onUnexpectedError(Exception exc) {
                    onDone();
                }
            });
        }
    }
}
