package com.opengarden.firechat.activity;

import android.content.Intent;
import android.support.p000v4.app.FragmentManager;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.fragments.VectorPublicRoomsListFragment;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorPublicRoomsActivity extends MXCActionBarActivity {
    public static final String EXTRA_SEARCHED_PATTERN = "VectorPublicRoomsActivity.EXTRA_SEARCHED_PATTERN";
    private static final String LOG_TAG = "VectorPublicRoomsActivity";
    private static final String TAG_FRAGMENT_PUBLIC_ROOMS_LIST = "VectorPublicRoomsActivity.TAG_FRAGMENT_PUBLIC_ROOMS_LIST";

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_public_rooms;
    }

    public int getTitleRes() {
        return C1299R.string.directory_title;
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            CommonActivityUtils.restartApp(this);
            Log.m209d(LOG_TAG, "onCreate : restart the application");
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            Intent intent = getIntent();
            MXSession session = getSession(intent);
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            if (((VectorPublicRoomsListFragment) supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PUBLIC_ROOMS_LIST)) == null) {
                String str = null;
                if (intent.hasExtra(EXTRA_SEARCHED_PATTERN)) {
                    str = intent.getStringExtra(EXTRA_SEARCHED_PATTERN);
                }
                supportFragmentManager.beginTransaction().add(C1299R.C1301id.layout_public__rooms_list, VectorPublicRoomsListFragment.newInstance(session.getMyUserId(), C1299R.layout.fragment_vector_public_rooms_list, str), TAG_FRAGMENT_PUBLIC_ROOMS_LIST).commit();
            }
        }
    }
}
