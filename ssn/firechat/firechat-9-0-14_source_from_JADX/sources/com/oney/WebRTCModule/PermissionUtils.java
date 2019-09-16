package com.oney.WebRTCModule;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.p000v4.content.ContextCompat;
import android.util.Log;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import java.util.ArrayList;
import org.apache.commons.cli.HelpFormatter;

public class PermissionUtils {
    private static final String GRANT_RESULTS = "GRANT_RESULT";
    private static final String PERMISSIONS = "PERMISSION";
    private static final String REQUEST_CODE = "REQUEST_CODE";
    private static final String RESULT_RECEIVER = "RESULT_RECEIVER";
    /* access modifiers changed from: private */
    public static final String TAG = C1267WebRTCModule.TAG;
    private static int requestCode;

    public interface Callback {
        void invoke(String[] strArr, int[] iArr);
    }

    public static class RequestPermissionsFragment extends Fragment {
        private void checkSelfPermissions(boolean z) {
            Bundle arguments = getArguments();
            String[] stringArray = arguments.getStringArray(PermissionUtils.PERMISSIONS);
            int length = stringArray.length;
            Activity activity = getActivity();
            int[] iArr = new int[length];
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < length; i++) {
                String str = stringArray[i];
                int checkSelfPermission = activity.checkSelfPermission(str);
                iArr[i] = checkSelfPermission;
                if (checkSelfPermission != 0) {
                    arrayList.add(str);
                }
            }
            int i2 = arguments.getInt(PermissionUtils.REQUEST_CODE, 0);
            if (arrayList.isEmpty() || !z) {
                finish();
                PermissionUtils.send((ResultReceiver) arguments.getParcelable(PermissionUtils.RESULT_RECEIVER), i2, stringArray, iArr);
                return;
            }
            requestPermissions((String[]) arrayList.toArray(new String[arrayList.size()]), i2);
        }

        private void finish() {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
            }
        }

        public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
            Bundle arguments = getArguments();
            if (arguments.getInt(PermissionUtils.REQUEST_CODE, 0) == i) {
                if (strArr.length == 0 || iArr.length == 0) {
                    Activity activity = getActivity();
                    finish();
                    PermissionUtils.requestPermissions((Context) activity, arguments.getStringArray(PermissionUtils.PERMISSIONS), (ResultReceiver) arguments.getParcelable(PermissionUtils.RESULT_RECEIVER));
                } else {
                    checkSelfPermissions(false);
                }
            }
        }

        public void onResume() {
            super.onResume();
            checkSelfPermissions(true);
        }
    }

    private static Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ReactContext) {
            return ((ReactContext) context).getCurrentActivity();
        }
        return null;
    }

    private static void maybeRequestPermissionsOnHostResume(final Context context, final String[] strArr, int[] iArr, final ResultReceiver resultReceiver, int i) {
        if (!(context instanceof ReactContext)) {
            send(resultReceiver, i, strArr, iArr);
            return;
        }
        final ReactContext reactContext = (ReactContext) context;
        reactContext.addLifecycleEventListener(new LifecycleEventListener() {
            public void onHostDestroy() {
            }

            public void onHostPause() {
            }

            public void onHostResume() {
                reactContext.removeLifecycleEventListener(this);
                PermissionUtils.requestPermissions(context, strArr, resultReceiver);
            }
        });
    }

    /* access modifiers changed from: private */
    public static void requestPermissions(Context context, String[] strArr, ResultReceiver resultReceiver) {
        int length = strArr.length;
        int[] iArr = new int[length];
        boolean z = true;
        for (int i = 0; i < length; i++) {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, strArr[i]);
            iArr[i] = checkSelfPermission;
            if (checkSelfPermission != 0) {
                z = false;
            }
        }
        int i2 = requestCode + 1;
        requestCode = i2;
        if (z || VERSION.SDK_INT < 23 || context.getApplicationInfo().targetSdkVersion < 23) {
            send(resultReceiver, i2, strArr, iArr);
            return;
        }
        Activity activity = getActivity(context);
        if (activity == null) {
            maybeRequestPermissionsOnHostResume(context, strArr, iArr, resultReceiver, i2);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(REQUEST_CODE, i2);
        bundle.putParcelable(RESULT_RECEIVER, resultReceiver);
        bundle.putStringArray(PERMISSIONS, strArr);
        RequestPermissionsFragment requestPermissionsFragment = new RequestPermissionsFragment();
        requestPermissionsFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = activity.getFragmentManager().beginTransaction();
        StringBuilder sb = new StringBuilder();
        sb.append(requestPermissionsFragment.getClass().getName());
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(i2);
        try {
            beginTransaction.add(requestPermissionsFragment, sb.toString()).commit();
        } catch (IllegalStateException unused) {
            maybeRequestPermissionsOnHostResume(context, strArr, iArr, resultReceiver, i2);
        }
    }

    public static void requestPermissions(ReactContext reactContext, String[] strArr, final Callback callback) {
        requestPermissions((Context) reactContext, strArr, (ResultReceiver) new ResultReceiver(new Handler(Looper.getMainLooper())) {
            private boolean resultReceived = false;

            /* access modifiers changed from: protected */
            public void onReceiveResult(int i, Bundle bundle) {
                if (this.resultReceived) {
                    Log.w(PermissionUtils.TAG, "PermissionUtils.ResultReceiver.onReceiveResult invoked more than once!");
                    return;
                }
                this.resultReceived = true;
                callback.invoke(bundle.getStringArray(PermissionUtils.PERMISSIONS), bundle.getIntArray(PermissionUtils.GRANT_RESULTS));
            }
        });
    }

    /* access modifiers changed from: private */
    public static void send(ResultReceiver resultReceiver, int i, String[] strArr, int[] iArr) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSIONS, strArr);
        bundle.putIntArray(GRANT_RESULTS, iArr);
        resultReceiver.send(i, bundle);
    }
}
