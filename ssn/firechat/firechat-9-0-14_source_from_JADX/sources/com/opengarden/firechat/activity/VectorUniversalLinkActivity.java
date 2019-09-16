package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.net.Uri;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.LoginHandler;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint({"LongLogTag"})
public class VectorUniversalLinkActivity extends RiotAppCompatActivity {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorUniversalLinkActivity";

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_universal_link_activity;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initUiAndData() {
        /*
            r7 = this;
            java.lang.String r0 = "im.vector.receiver.UNIVERSAL_LINK"
            r1 = 0
            java.lang.String r2 = "/_matrix/identity/api/v1/validate/email/submitToken"
            android.content.Intent r3 = r7.getIntent()     // Catch:{ Exception -> 0x0074 }
            android.net.Uri r3 = r3.getData()     // Catch:{ Exception -> 0x0074 }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0074 }
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x0074 }
            if (r2 == 0) goto L_0x0070
            android.content.Intent r2 = r7.getIntent()     // Catch:{ Exception -> 0x0074 }
            android.net.Uri r2 = r2.getData()     // Catch:{ Exception -> 0x0074 }
            java.util.HashMap r3 = com.opengarden.firechat.receiver.VectorRegistrationReceiver.parseMailRegistrationLink(r2)     // Catch:{ Exception -> 0x0074 }
            com.opengarden.firechat.Matrix r4 = com.opengarden.firechat.Matrix.getInstance(r7)     // Catch:{ Exception -> 0x0074 }
            com.opengarden.firechat.matrixsdk.MXSession r4 = r4.getDefaultSession()     // Catch:{ Exception -> 0x0074 }
            java.lang.String r5 = "nextLink"
            boolean r5 = r3.containsKey(r5)     // Catch:{ Exception -> 0x0074 }
            if (r5 != 0) goto L_0x003f
            if (r4 != 0) goto L_0x0036
            goto L_0x003f
        L_0x0036:
            r7.emailBinding(r2, r3)     // Catch:{ Exception -> 0x003a }
            goto L_0x0072
        L_0x003a:
            r0 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
            goto L_0x0075
        L_0x003f:
            if (r4 == 0) goto L_0x006d
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0074 }
            java.lang.String r3 = "## onCreate(): logout the current sessions, before finalizing an account creation based on an email validation"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ Exception -> 0x0074 }
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x003a }
            java.lang.Class<com.opengarden.firechat.receiver.VectorRegistrationReceiver> r2 = com.opengarden.firechat.receiver.VectorRegistrationReceiver.class
            r0.<init>(r7, r2)     // Catch:{ Exception -> 0x003a }
            java.lang.String r2 = "im.vector.receiver.BROADCAST_ACTION_REGISTRATION"
            r0.setAction(r2)     // Catch:{ Exception -> 0x003a }
            android.content.Intent r2 = r7.getIntent()     // Catch:{ Exception -> 0x003a }
            android.net.Uri r2 = r2.getData()     // Catch:{ Exception -> 0x003a }
            r0.setData(r2)     // Catch:{ Exception -> 0x003a }
            java.util.ArrayList r2 = com.opengarden.firechat.Matrix.getMXSessions(r7)     // Catch:{ Exception -> 0x003a }
            r3 = 1
            com.opengarden.firechat.activity.VectorUniversalLinkActivity$1 r4 = new com.opengarden.firechat.activity.VectorUniversalLinkActivity$1     // Catch:{ Exception -> 0x003a }
            r4.<init>(r0)     // Catch:{ Exception -> 0x003a }
            com.opengarden.firechat.activity.CommonActivityUtils.logout(r7, r2, r3, r4)     // Catch:{ Exception -> 0x003a }
            goto L_0x0072
        L_0x006d:
            java.lang.String r1 = "im.vector.receiver.BROADCAST_ACTION_REGISTRATION"
            goto L_0x0072
        L_0x0070:
            java.lang.String r1 = "im.vector.receiver.UNIVERSAL_LINK"
        L_0x0072:
            r0 = r1
            goto L_0x008f
        L_0x0074:
            r1 = move-exception
        L_0x0075:
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "## onCreate(): Exception - Msg="
            r3.append(r4)
            java.lang.String r1 = r1.getMessage()
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)
        L_0x008f:
            if (r0 == 0) goto L_0x00b7
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "im.vector.receiver.UNIVERSAL_LINK"
            boolean r2 = android.text.TextUtils.equals(r0, r2)
            if (r2 == 0) goto L_0x009e
            java.lang.Class<com.opengarden.firechat.receiver.VectorUniversalLinkReceiver> r2 = com.opengarden.firechat.receiver.VectorUniversalLinkReceiver.class
            goto L_0x00a0
        L_0x009e:
            java.lang.Class<com.opengarden.firechat.receiver.VectorRegistrationReceiver> r2 = com.opengarden.firechat.receiver.VectorRegistrationReceiver.class
        L_0x00a0:
            r1.<init>(r7, r2)
            r1.setAction(r0)
            android.content.Intent r0 = r7.getIntent()
            android.net.Uri r0 = r0.getData()
            r1.setData(r0)
            r7.sendBroadcast(r1)
            r7.finish()
        L_0x00b7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorUniversalLinkActivity.initUiAndData():void");
    }

    private void emailBinding(Uri uri, HashMap<String, String> hashMap) {
        Log.m209d(LOG_TAG, "## emailBinding()");
        StringBuilder sb = new StringBuilder();
        sb.append(uri.getScheme());
        sb.append("://");
        sb.append(uri.getHost());
        String sb2 = sb.toString();
        HomeServerConnectionConfig homeServerConnectionConfig = new HomeServerConnectionConfig(Uri.parse(sb2), Uri.parse(sb2), null, new ArrayList(), false);
        new LoginHandler().submitEmailTokenValidation(getApplicationContext(), homeServerConnectionConfig, (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_TOKEN), (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_CLIENT_SECRET), (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_IDENTITY_SERVER_SESSION_ID), new ApiCallback<Boolean>() {
            /* access modifiers changed from: private */
            public void bringAppToForeground() {
                ActivityManager activityManager = (ActivityManager) VectorUniversalLinkActivity.this.getSystemService("activity");
                List runningTasks = activityManager.getRunningTasks(100);
                if (!runningTasks.isEmpty()) {
                    int size = runningTasks.size();
                    for (int i = 0; i < size; i++) {
                        RunningTaskInfo runningTaskInfo = (RunningTaskInfo) runningTasks.get(i);
                        if (runningTaskInfo.topActivity.getPackageName().equals(VectorUniversalLinkActivity.this.getApplicationContext().getPackageName())) {
                            Log.m209d(VectorUniversalLinkActivity.LOG_TAG, "## emailBinding(): bring the app in foreground.");
                            activityManager.moveTaskToFront(runningTaskInfo.id, 0);
                        }
                    }
                }
                VectorUniversalLinkActivity.this.finish();
            }

            private void errorHandler(final String str) {
                VectorUniversalLinkActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(VectorUniversalLinkActivity.this.getApplicationContext(), str, 1).show();
                        C17112.this.bringAppToForeground();
                    }
                });
            }

            public void onSuccess(Boolean bool) {
                VectorUniversalLinkActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.m209d(VectorUniversalLinkActivity.LOG_TAG, "## emailBinding(): succeeds.");
                        C17112.this.bringAppToForeground();
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                String access$000 = VectorUniversalLinkActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## emailBinding(): onNetworkError() Msg=");
                sb.append(exc.getLocalizedMessage());
                Log.m209d(access$000, sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append(VectorUniversalLinkActivity.this.getString(C1299R.string.login_error_unable_register));
                sb2.append(" : ");
                sb2.append(exc.getLocalizedMessage());
                errorHandler(sb2.toString());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = VectorUniversalLinkActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## emailBinding(): onMatrixError() Msg=");
                sb.append(matrixError.getLocalizedMessage());
                Log.m209d(access$000, sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append(VectorUniversalLinkActivity.this.getString(C1299R.string.login_error_unable_register));
                sb2.append(" : ");
                sb2.append(matrixError.getLocalizedMessage());
                errorHandler(sb2.toString());
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = VectorUniversalLinkActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## emailBinding(): onUnexpectedError() Msg=");
                sb.append(exc.getLocalizedMessage());
                Log.m209d(access$000, sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append(VectorUniversalLinkActivity.this.getString(C1299R.string.login_error_unable_register));
                sb2.append(" : ");
                sb2.append(exc.getLocalizedMessage());
                errorHandler(sb2.toString());
            }
        });
    }
}
