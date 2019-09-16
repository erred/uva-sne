package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.VectorBaseSearchActivity.IVectorSearchActivity;
import com.opengarden.firechat.adapters.VectorUnifiedSearchFragmentPagerAdapter;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorUnifiedSearchActivity extends VectorBaseSearchActivity implements IVectorSearchActivity {
    public static final String EXTRA_ROOM_ID = "VectorUnifiedSearchActivity.EXTRA_ROOM_ID";
    public static final String EXTRA_TAB_INDEX = "VectorUnifiedSearchActivity.EXTRA_TAB_INDEX";
    private static final String KEY_STATE_CURRENT_TAB_INDEX = "CURRENT_SELECTED_TAB";
    private static final String KEY_STATE_SEARCH_PATTERN = "SEARCH_PATTERN";
    private static final String LOG_TAG = "VectorUnifiedSearchActivity";
    public static final int SEARCH_FILES_TAB_POSITION = 3;
    public static final int SEARCH_MESSAGES_TAB_POSITION = 1;
    public static final int SEARCH_PEOPLE_TAB_POSITION = 2;
    public static final int SEARCH_ROOMS_TAB_POSITION = 0;
    private ImageView mBackgroundImageView;
    private View mLoadOldestContentView;
    private TextView mNoResultsTxtView;
    /* access modifiers changed from: private */
    public VectorUnifiedSearchFragmentPagerAdapter mPagerAdapter;
    private int mPosition;
    private String mRoomId;
    private ViewPager mViewPager;

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_unified_search;
    }

    public void initUiAndData() {
        super.initUiAndData();
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            MXSession defaultSession = Matrix.getInstance(this).getDefaultSession();
            if (defaultSession == null) {
                Log.m211e(LOG_TAG, "No MXSession.");
                finish();
                return;
            }
            this.mBackgroundImageView = (ImageView) findViewById(C1299R.C1301id.search_background_imageview);
            this.mNoResultsTxtView = (TextView) findViewById(C1299R.C1301id.search_no_result_textview);
            setWaitingView(findViewById(C1299R.C1301id.search_in_progress_view));
            this.mLoadOldestContentView = findViewById(C1299R.C1301id.search_load_oldest_progress);
            if (getIntent() != null) {
                this.mRoomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
            }
            this.mPagerAdapter = new VectorUnifiedSearchFragmentPagerAdapter(getSupportFragmentManager(), this, defaultSession, this.mRoomId);
            this.mViewPager = (ViewPager) findViewById(C1299R.C1301id.search_view_pager);
            this.mViewPager.setAdapter(this.mPagerAdapter);
            this.mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    int permissionsRequest = VectorUnifiedSearchActivity.this.mPagerAdapter.getPermissionsRequest(i);
                    if (permissionsRequest != 0) {
                        CommonActivityUtils.checkPermissions(permissionsRequest, (Activity) VectorUnifiedSearchActivity.this);
                    }
                    VectorUnifiedSearchActivity.this.searchAccordingToSelectedTab();
                }
            });
            ((TabLayout) findViewById(C1299R.C1301id.search_filter_tabs)).setupWithViewPager(this.mViewPager);
            int i = 0;
            if (getIntent() == null || !getIntent().hasExtra("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX")) {
                if (!isFirstCreation()) {
                    i = getSavedInstanceState().getInt(KEY_STATE_CURRENT_TAB_INDEX, 0);
                }
                this.mPosition = i;
            } else {
                this.mPosition = getIntent().getIntExtra("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX", 0);
            }
            this.mViewPager.setCurrentItem(this.mPosition);
            EditText editText = this.mPatternToSearchEditText;
            String str = null;
            if (!isFirstCreation()) {
                str = getSavedInstanceState().getString(KEY_STATE_SEARCH_PATTERN, null);
            }
            editText.setText(str);
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /* access modifiers changed from: private */
    public void searchAccordingToSelectedTab() {
        String trim = this.mPatternToSearchEditText.getText().toString().trim();
        final int currentItem = this.mViewPager.getCurrentItem();
        if (this.mPosition != currentItem) {
            this.mPagerAdapter.cancelSearch(this.mPosition);
        }
        this.mPosition = currentItem;
        resetUi(TextUtils.isEmpty(trim) && !this.mPagerAdapter.isSearchInRoomNameFragment(currentItem) && !this.mPagerAdapter.isSearchInPeoplesFragment(currentItem));
        if (this.mPagerAdapter.search(currentItem, trim, new OnSearchResultListener() {
            public void onSearchSucceed(int i) {
                VectorUnifiedSearchActivity.this.onSearchEnd(currentItem, i);
            }

            public void onSearchFailed() {
                VectorUnifiedSearchActivity.this.onSearchEnd(currentItem, 0);
            }
        })) {
            showWaitingView();
        }
    }

    /* access modifiers changed from: protected */
    public void onPatternUpdate(boolean z) {
        int currentItem = this.mViewPager.getCurrentItem();
        if (!z || (!this.mPagerAdapter.isSearchInMessagesFragment(currentItem) && !this.mPagerAdapter.isSearchInFilesFragment(currentItem))) {
            searchAccordingToSelectedTab();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    private void resetUi(boolean z) {
        if (getWaitingView() != null) {
            hideWaitingView();
        }
        if (this.mBackgroundImageView != null) {
            this.mBackgroundImageView.setVisibility(z ? 0 : 8);
        }
        if (this.mNoResultsTxtView != null) {
            this.mNoResultsTxtView.setVisibility(8);
        }
        if (this.mLoadOldestContentView != null) {
            this.mLoadOldestContentView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public void onSearchEnd(int i, int i2) {
        if (this.mViewPager.getCurrentItem() == i) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onSearchEnd() nbrMsg=");
            sb.append(i2);
            Log.m209d(str, sb.toString());
            hideWaitingView();
            int i3 = 8;
            this.mBackgroundImageView.setVisibility((this.mPagerAdapter.isSearchInPeoplesFragment(i) || i2 != 0 || !TextUtils.isEmpty(this.mPatternToSearchEditText.getText().toString())) ? 8 : 0);
            TextView textView = this.mNoResultsTxtView;
            if (i2 == 0 && !TextUtils.isEmpty(this.mPatternToSearchEditText.getText().toString())) {
                i3 = 0;
            }
            textView.setVisibility(i3);
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
        } else if (i != 8) {
        } else {
            if (iArr[0] == 0) {
                Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission granted");
                ContactsManager.getInstance().refreshLocalContactsSnapshot();
                searchAccordingToSelectedTab();
                return;
            }
            Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission not granted");
            CommonActivityUtils.displayToast(this, getString(C1299R.string.missing_permissions_warning));
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"LongLogTag"})
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.m209d(LOG_TAG, "## onSaveInstanceState(): ");
        bundle.putInt(KEY_STATE_CURRENT_TAB_INDEX, this.mViewPager.getCurrentItem());
        String obj = this.mPatternToSearchEditText.getText().toString();
        if (!TextUtils.isEmpty(obj)) {
            bundle.putString(KEY_STATE_SEARCH_PATTERN, obj);
        }
    }

    public void refreshSearch() {
        searchAccordingToSelectedTab();
    }
}
