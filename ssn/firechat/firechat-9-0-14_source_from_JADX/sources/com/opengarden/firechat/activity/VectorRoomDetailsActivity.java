package com.opengarden.firechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentTransaction;
import android.support.p003v7.app.ActionBar;
import android.support.p003v7.app.ActionBar.Tab;
import android.support.p003v7.app.ActionBar.TabListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.fragments.VectorRoomDetailsMembersFragment;
import com.opengarden.firechat.fragments.VectorRoomSettingsFragment;
import com.opengarden.firechat.fragments.VectorSearchRoomFilesListFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorRoomDetailsActivity extends MXCActionBarActivity implements TabListener {
    public static final String EXTRA_ROOM_ID = "VectorRoomDetailsActivity.EXTRA_ROOM_ID";
    public static final String EXTRA_SELECTED_TAB_ID = "VectorRoomDetailsActivity.EXTRA_SELECTED_TAB_ID";
    public static final int FILE_TAB_INDEX = 1;
    private static final String KEY_FRAGMENT_TAG = "KEY_FRAGMENT_TAG";
    private static final String KEY_STATE_CURRENT_TAB_INDEX = "CURRENT_SELECTED_TAB";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomDetailsActivity";
    public static final int PEOPLE_TAB_INDEX = 0;
    public static final int SETTINGS_TAB_INDEX = 2;
    private static final String TAG_FRAGMENT_FILES_DETAILS = "im.vector.activity.TAG_FRAGMENT_FILES_DETAILS";
    private static final String TAG_FRAGMENT_PEOPLE_ROOM_DETAILS = "im.vector.activity.TAG_FRAGMENT_PEOPLE_ROOM_DETAILS";
    private static final String TAG_FRAGMENT_SETTINGS_ROOM_DETAIL = "im.vector.activity.TAG_FRAGMENT_SETTINGS_ROOM_DETAIL";
    private ActionBar mActionBar;
    private int mCurrentTabIndex = -1;
    private final MXEventListener mEventListener = new MXEventListener() {
        public void onLeaveRoom(String str) {
            VectorRoomDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VectorRoomDetailsActivity.this, VectorHomeActivity.class);
                    intent.setFlags(603979776);
                    VectorRoomDetailsActivity.this.startActivity(intent);
                }
            });
        }
    };
    private boolean mIsContactsPermissionChecked;
    private View mLoadOldestContentView;
    /* access modifiers changed from: private */
    public String mMatrixId;
    private VectorRoomDetailsMembersFragment mRoomDetailsMembersFragment;
    /* access modifiers changed from: private */
    public String mRoomId;
    /* access modifiers changed from: private */
    public VectorRoomSettingsFragment mRoomSettingsFragment;
    private VectorSearchRoomFilesListFragment mSearchFilesFragment;

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_room_details;
    }

    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            Intent intent = getIntent();
            if (!intent.hasExtra(EXTRA_ROOM_ID)) {
                Log.m211e(LOG_TAG, "No room ID extra.");
                finish();
                return;
            }
            if (intent.hasExtra("MXCActionBarActivity.EXTRA_MATRIX_ID")) {
                this.mMatrixId = intent.getStringExtra("MXCActionBarActivity.EXTRA_MATRIX_ID");
            }
            this.mSession = Matrix.getInstance(getApplicationContext()).getSession(this.mMatrixId);
            if (this.mSession == null || !this.mSession.isAlive()) {
                finish();
                return;
            }
            this.mRoomId = intent.getStringExtra(EXTRA_ROOM_ID);
            this.mRoom = this.mSession.getDataHandler().getRoom(this.mRoomId);
            int intExtra = intent.getIntExtra(EXTRA_SELECTED_TAB_ID, -1);
            setContentView((int) C1299R.layout.activity_vector_room_details);
            setWaitingView(findViewById(C1299R.C1301id.settings_loading_layout));
            this.mLoadOldestContentView = findViewById(C1299R.C1301id.search_load_oldest_progress);
            this.mActionBar = getSupportActionBar();
            createNavigationTabs(intExtra);
        }
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
        Log.m209d(LOG_TAG, "## onSaveInstanceState(): ");
        if (this.mActionBar != null) {
            bundle.putInt(KEY_STATE_CURRENT_TAB_INDEX, this.mActionBar.getSelectedNavigationIndex());
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
        } else if (i == 8 && "android.permission.READ_CONTACTS".equals(strArr[0])) {
            if (iArr[0] == 0) {
                Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission granted");
            } else {
                Log.m217w(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission not granted");
                CommonActivityUtils.displayToast(this, getString(C1299R.string.missing_permissions_warning));
            }
            ContactsManager.getInstance().refreshLocalContactsSnapshot();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public void onBackPressed() {
        if (!(this.mCurrentTabIndex == 0 ? this.mRoomDetailsMembersFragment.onBackPressed() : false)) {
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mRoom.removeEventListener(this.mEventListener);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mSession.isAlive()) {
            if (this.mRoom.getMember(this.mSession.getMyUserId()) == null || !this.mSession.getDataHandler().doesRoomExist(this.mRoom.getRoomId())) {
                Intent intent = new Intent(this, VectorHomeActivity.class);
                intent.setFlags(603979776);
                startActivity(intent);
                return;
            }
            this.mRoom.addEventListener(this.mEventListener);
            startFileSearch();
        }
    }

    private void saveUiTabContext(Tab tab) {
        tab.setTag((Bundle) tab.getTag());
    }

    private void resetUi() {
        if (getWaitingView() != null) {
            hideWaitingView();
        }
        if (this.mLoadOldestContentView != null) {
            this.mLoadOldestContentView.setVisibility(8);
        }
    }

    private void createNavigationTabs(int i) {
        this.mActionBar.setNavigationMode(2);
        Tab newTab = this.mActionBar.newTab();
        newTab.setText((CharSequence) getResources().getString(C1299R.string.room_details_people));
        newTab.setTabListener(this);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FRAGMENT_TAG, TAG_FRAGMENT_PEOPLE_ROOM_DETAILS);
        newTab.setTag(bundle);
        this.mActionBar.addTab(newTab);
        Tab newTab2 = this.mActionBar.newTab();
        newTab2.setText((CharSequence) getResources().getString(C1299R.string.room_details_files));
        newTab2.setTabListener(this);
        Bundle bundle2 = new Bundle();
        bundle2.putString(KEY_FRAGMENT_TAG, TAG_FRAGMENT_FILES_DETAILS);
        newTab2.setTag(bundle2);
        this.mActionBar.addTab(newTab2);
        Tab newTab3 = this.mActionBar.newTab();
        newTab3.setText((CharSequence) getResources().getString(C1299R.string.room_details_settings));
        newTab3.setTabListener(this);
        Bundle bundle3 = new Bundle();
        bundle3.putString(KEY_FRAGMENT_TAG, TAG_FRAGMENT_SETTINGS_ROOM_DETAIL);
        newTab3.setTag(bundle3);
        this.mActionBar.addTab(newTab3);
        int i2 = isFirstCreation() ? -1 : getSavedInstanceState().getInt(KEY_STATE_CURRENT_TAB_INDEX, -1);
        if (-1 != i2) {
            i = i2;
        }
        if (-1 == i) {
            i = 0;
        }
        this.mActionBar.setSelectedNavigationItem(i);
        this.mCurrentTabIndex = i;
    }

    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        String string = ((Bundle) tab.getTag()).getString(KEY_FRAGMENT_TAG, "");
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onTabSelected() FragTag=");
        sb.append(string);
        Log.m209d(str, sb.toString());
        resetUi();
        int i = 1;
        if (string.equals(TAG_FRAGMENT_PEOPLE_ROOM_DETAILS)) {
            this.mRoomDetailsMembersFragment = (VectorRoomDetailsMembersFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_PEOPLE_ROOM_DETAILS);
            if (this.mRoomDetailsMembersFragment == null) {
                this.mRoomDetailsMembersFragment = VectorRoomDetailsMembersFragment.newInstance();
                fragmentTransaction.replace(C1299R.C1301id.room_details_fragment_container, this.mRoomDetailsMembersFragment, TAG_FRAGMENT_PEOPLE_ROOM_DETAILS);
                Log.m209d(LOG_TAG, "## onTabSelected() people frag replace");
            } else {
                fragmentTransaction.attach(this.mRoomDetailsMembersFragment);
                Log.m209d(LOG_TAG, "## onTabSelected() people frag attach");
            }
            this.mCurrentTabIndex = 0;
            if (!this.mIsContactsPermissionChecked) {
                this.mIsContactsPermissionChecked = true;
                CommonActivityUtils.checkPermissions(8, (Activity) this);
            }
        } else if (string.equals(TAG_FRAGMENT_SETTINGS_ROOM_DETAIL)) {
            onTabSelectSettingsFragment();
            if (!CommonActivityUtils.isPowerLevelEnoughForAvatarUpdate(this.mRoom, this.mSession)) {
                i = 0;
            }
            CommonActivityUtils.checkPermissions(i, (Activity) this);
            this.mCurrentTabIndex = 2;
        } else if (string.equals(TAG_FRAGMENT_FILES_DETAILS)) {
            this.mSearchFilesFragment = (VectorSearchRoomFilesListFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_FILES_DETAILS);
            if (this.mSearchFilesFragment == null) {
                this.mSearchFilesFragment = VectorSearchRoomFilesListFragment.newInstance(this.mSession.getCredentials().userId, this.mRoomId, C1299R.layout.fragment_matrix_message_list_fragment);
                fragmentTransaction.replace(C1299R.C1301id.room_details_fragment_container, this.mSearchFilesFragment, TAG_FRAGMENT_FILES_DETAILS);
                Log.m209d(LOG_TAG, "## onTabSelected() file frag replace");
            } else {
                fragmentTransaction.attach(this.mSearchFilesFragment);
                Log.m209d(LOG_TAG, "## onTabSelected() file frag attach");
            }
            this.mCurrentTabIndex = 1;
            startFileSearch();
        } else {
            Toast.makeText(this, "Not yet implemented", 0).show();
            this.mCurrentTabIndex = 2;
            Log.m217w(LOG_TAG, "## onTabSelected() unknown tab selected!!");
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle((CharSequence) getResources().getString(C1299R.string.room_details_title));
        }
    }

    private void startFileSearch() {
        if (this.mCurrentTabIndex == 1) {
            showWaitingView();
            this.mSearchFilesFragment.startFilesSearch(new OnSearchResultListener() {
                public void onSearchSucceed(int i) {
                    VectorRoomDetailsActivity.this.onSearchEnd(1, i);
                }

                public void onSearchFailed() {
                    VectorRoomDetailsActivity.this.onSearchEnd(1, 0);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onSearchEnd(int i, int i2) {
        if (this.mCurrentTabIndex == i) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onSearchEnd() nbrMsg=");
            sb.append(i2);
            Log.m209d(str, sb.toString());
            hideWaitingView();
        }
    }

    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        String string = ((Bundle) tab.getTag()).getString(KEY_FRAGMENT_TAG, "");
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onTabUnselected() FragTag=");
        sb.append(string);
        Log.m209d(str, sb.toString());
        saveUiTabContext(tab);
        if (string.equals(TAG_FRAGMENT_PEOPLE_ROOM_DETAILS)) {
            if (this.mRoomDetailsMembersFragment != null) {
                fragmentTransaction.detach(this.mRoomDetailsMembersFragment);
            }
        } else if (string.equals(TAG_FRAGMENT_SETTINGS_ROOM_DETAIL)) {
            onTabUnselectedSettingsFragment();
        } else if (!string.equals(TAG_FRAGMENT_FILES_DETAILS)) {
            Log.m217w(LOG_TAG, "## onTabUnselected() unknown tab selected!!");
        } else if (this.mSearchFilesFragment != null) {
            this.mSearchFilesFragment.cancelCatchingRequests();
            fragmentTransaction.detach(this.mSearchFilesFragment);
        }
    }

    private void onTabSelectSettingsFragment() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (VectorRoomDetailsActivity.this.mRoomSettingsFragment == null) {
                    VectorRoomDetailsActivity.this.mRoomSettingsFragment = VectorRoomSettingsFragment.newInstance(VectorRoomDetailsActivity.this.mMatrixId, VectorRoomDetailsActivity.this.mRoomId);
                    VectorRoomDetailsActivity.this.getFragmentManager().beginTransaction().replace(C1299R.C1301id.room_details_fragment_container, VectorRoomDetailsActivity.this.mRoomSettingsFragment, VectorRoomDetailsActivity.TAG_FRAGMENT_SETTINGS_ROOM_DETAIL).commit();
                    Log.m209d(VectorRoomDetailsActivity.LOG_TAG, "## onTabSelectSettingsFragment() settings frag replace");
                    return;
                }
                VectorRoomDetailsActivity.this.getFragmentManager().beginTransaction().attach(VectorRoomDetailsActivity.this.mRoomSettingsFragment).commit();
                Log.m209d(VectorRoomDetailsActivity.LOG_TAG, "## onTabSelectSettingsFragment() settings frag attach");
            }
        });
    }

    private void onTabUnselectedSettingsFragment() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (VectorRoomDetailsActivity.this.mRoomSettingsFragment != null) {
                    VectorRoomDetailsActivity.this.getFragmentManager().beginTransaction().detach(VectorRoomDetailsActivity.this.mRoomSettingsFragment).commit();
                }
            }
        });
    }
}
