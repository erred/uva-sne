package com.opengarden.firechat.activity;

import android.content.Intent;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class VectorSharedFilesActivity extends RiotAppCompatActivity {
    private static final String LOG_TAG = "VectorSharedFilesActivity";
    private final String SHARED_FOLDER = "VectorShared";

    public int getLayoutRes() {
        return C1299R.layout.activity_empty;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0081  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initUiAndData() {
        /*
            r7 = this;
            android.content.Intent r0 = r7.getIntent()
            r1 = 335577088(0x14008000, float:6.487592E-27)
            if (r0 == 0) goto L_0x00a8
            java.lang.String r2 = r0.getAction()
            java.lang.String r3 = r0.getType()
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "onCreate : action "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r6 = " type "
            r5.append(r6)
            r5.append(r3)
            java.lang.String r5 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r4, r5)
            java.lang.String r4 = "android.intent.action.SEND"
            boolean r4 = r4.equals(r2)
            if (r4 != 0) goto L_0x003f
            java.lang.String r4 = "android.intent.action.SEND_MULTIPLE"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0093
        L_0x003f:
            if (r3 == 0) goto L_0x0093
            r1 = 0
            com.opengarden.firechat.Matrix r2 = com.opengarden.firechat.Matrix.getInstance(r7)     // Catch:{ Exception -> 0x005d }
            com.opengarden.firechat.matrixsdk.MXSession r2 = r2.getDefaultSession()     // Catch:{ Exception -> 0x005d }
            if (r2 == 0) goto L_0x007a
            r3 = 1
            com.opengarden.firechat.matrixsdk.MXDataHandler r2 = r2.getDataHandler()     // Catch:{ Exception -> 0x005b }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r2 = r2.getStore()     // Catch:{ Exception -> 0x005b }
            boolean r2 = r2.isReady()     // Catch:{ Exception -> 0x005b }
            r1 = 1
            goto L_0x007b
        L_0x005b:
            r2 = move-exception
            goto L_0x005f
        L_0x005d:
            r2 = move-exception
            r3 = 0
        L_0x005f:
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "## onCreate() : failed "
            r5.append(r6)
            java.lang.String r2 = r2.getMessage()
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r2)
            r1 = r3
        L_0x007a:
            r2 = 0
        L_0x007b:
            if (r1 == 0) goto L_0x0081
            r7.launchActivity(r0, r2)
            goto L_0x00bc
        L_0x0081:
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "onCreate : go to login screen"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.opengarden.firechat.activity.LoginActivity> r1 = com.opengarden.firechat.activity.LoginActivity.class
            r0.<init>(r7, r1)
            r7.startActivity(r0)
            goto L_0x00bc
        L_0x0093:
            java.lang.String r0 = LOG_TAG
            java.lang.String r2 = "onCreate : unsupported action"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.opengarden.firechat.activity.VectorHomeActivity> r2 = com.opengarden.firechat.activity.VectorHomeActivity.class
            r0.<init>(r7, r2)
            r0.addFlags(r1)
            r7.startActivity(r0)
            goto L_0x00bc
        L_0x00a8:
            java.lang.String r0 = LOG_TAG
            java.lang.String r2 = "onCreate : null intent"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.opengarden.firechat.activity.VectorHomeActivity> r2 = com.opengarden.firechat.activity.VectorHomeActivity.class
            r0.<init>(r7, r2)
            r0.addFlags(r1)
            r7.startActivity(r0)
        L_0x00bc:
            r7.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorSharedFilesActivity.initUiAndData():void");
    }

    private void launchActivity(Intent intent, boolean z) {
        Intent intent2;
        File file = new File(getCacheDir(), "VectorShared");
        if (file.exists()) {
            ContentUtils.deleteDirectory(file);
        }
        file.mkdir();
        ArrayList arrayList = new ArrayList(RoomMediaMessage.listRoomMediaMessages(intent));
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((RoomMediaMessage) it.next()).saveMedia(this, file);
            }
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onCreate : launch home activity with the files list ");
        sb.append(arrayList.size());
        sb.append(" files");
        Log.m209d(str, sb.toString());
        if (z) {
            intent2 = new Intent(this, VectorHomeActivity.class);
        } else {
            intent2 = new Intent(this, SplashActivity.class);
        }
        intent2.addFlags(335577088);
        if (arrayList.size() != 0) {
            Intent intent3 = new Intent();
            intent3.setAction("android.intent.action.SEND_MULTIPLE");
            intent3.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
            intent3.setExtrasClassLoader(RoomMediaMessage.class.getClassLoader());
            intent3.setType(ResourceUtils.MIME_TYPE_ALL_CONTENT);
            intent2.putExtra(VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS, intent3);
        }
        startActivity(intent2);
    }
}
