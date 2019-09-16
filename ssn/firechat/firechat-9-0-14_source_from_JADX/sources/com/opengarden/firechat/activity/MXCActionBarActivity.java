package com.opengarden.firechat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.p000v4.app.DialogFragment;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.MyPresenceManager;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import java.util.List;

public abstract class MXCActionBarActivity extends RiotAppCompatActivity {
    public static final String EXTRA_MATRIX_ID = "MXCActionBarActivity.EXTRA_MATRIX_ID";
    Room mRoom = null;
    MXSession mSession = null;

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }

    /* access modifiers changed from: protected */
    @Nullable
    public MXSession getSession(Intent intent) {
        return Matrix.getInstance(this).getSession(intent.hasExtra("MXCActionBarActivity.EXTRA_MATRIX_ID") ? intent.getStringExtra("MXCActionBarActivity.EXTRA_MATRIX_ID") : null);
    }

    public MXSession getSession() {
        return this.mSession;
    }

    public Room getRoom() {
        return this.mRoom;
    }

    public void startActivity(Intent intent) {
        super.startActivity(intent);
        if (VERSION.SDK_INT < 21) {
            overridePendingTransition(C1299R.anim.anim_slide_in_bottom, C1299R.anim.anim_slide_nothing);
        }
    }

    public void finish() {
        super.finish();
        if (VERSION.SDK_INT < 21) {
            overridePendingTransition(C1299R.anim.anim_slide_nothing, C1299R.anim.anim_slide_out_bottom);
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 82 && getSupportActionBar() == null) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    private static void dismissDialogs(FragmentActivity fragmentActivity) {
        List<Fragment> fragments = fragmentActivity.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof DialogFragment) {
                    ((DialogFragment) fragment).dismissAllowingStateLoss();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        Matrix.removeSessionErrorListener(this);
        dismissDialogs(this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        VectorApp.getInstance().getOnActivityDestroyedListener().fire(this);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Matrix.setSessionErrorListener(this);
        if (Matrix.getInstance(this) != null && Matrix.getInstance(this).getSessions() != null) {
            MyPresenceManager.createPresenceManager(this, Matrix.getInstance(this).getSessions());
            MyPresenceManager.advertiseAllOnline();
        }
    }

    public static void dismissKeyboard(Activity activity) {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 2);
        }
    }
}
