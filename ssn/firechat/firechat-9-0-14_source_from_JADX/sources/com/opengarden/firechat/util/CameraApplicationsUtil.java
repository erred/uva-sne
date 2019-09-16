package com.opengarden.firechat.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class CameraApplicationsUtil {
    private static String LOG_TAG = "CameraApplicationsUtil";

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0067 A[SYNTHETIC, Splitter:B:15:0x0067] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00b7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String openCamera(android.app.Activity r7, java.lang.String r8, int r9) {
        /*
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.media.action.IMAGE_CAPTURE"
            r0.<init>(r1)
            java.util.Date r1 = new java.util.Date
            r1.<init>()
            java.text.SimpleDateFormat r2 = new java.text.SimpleDateFormat
            java.lang.String r3 = "yyyyMMddHHmmss"
            java.util.Locale r4 = java.util.Locale.US
            r2.<init>(r3, r4)
            android.content.ContentValues r3 = new android.content.ContentValues
            r3.<init>()
            java.lang.String r4 = "title"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r8)
            java.lang.String r8 = r2.format(r1)
            r5.append(r8)
            java.lang.String r8 = r5.toString()
            r3.put(r4, r8)
            java.lang.String r8 = "mime_type"
            java.lang.String r1 = "image/jpeg"
            r3.put(r8, r1)
            r8 = 0
            android.content.ContentResolver r1 = r7.getContentResolver()     // Catch:{ UnsupportedOperationException -> 0x005c, Exception -> 0x0052 }
            android.net.Uri r2 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ UnsupportedOperationException -> 0x005c, Exception -> 0x0052 }
            android.net.Uri r1 = r1.insert(r2, r3)     // Catch:{ UnsupportedOperationException -> 0x005c, Exception -> 0x0052 }
            if (r1 != 0) goto L_0x0065
            java.lang.String r2 = LOG_TAG     // Catch:{ UnsupportedOperationException -> 0x0050, Exception -> 0x004e }
            java.lang.String r4 = "Cannot use the external storage media to save image"
            android.util.Log.e(r2, r4)     // Catch:{ UnsupportedOperationException -> 0x0050, Exception -> 0x004e }
            goto L_0x0065
        L_0x004e:
            r2 = move-exception
            goto L_0x0054
        L_0x0050:
            r2 = move-exception
            goto L_0x005e
        L_0x0052:
            r2 = move-exception
            r1 = r8
        L_0x0054:
            java.lang.String r4 = LOG_TAG
            java.lang.String r5 = "Unable to insert camera URI into MediaStore.Images.Media.EXTERNAL_CONTENT_URI. $e"
            android.util.Log.e(r4, r5, r2)
            goto L_0x0065
        L_0x005c:
            r2 = move-exception
            r1 = r8
        L_0x005e:
            java.lang.String r4 = LOG_TAG
            java.lang.String r5 = "Unable to insert camera URI into MediaStore.Images.Media.EXTERNAL_CONTENT_URI - no SD card? Attempting to insert into device storage."
            android.util.Log.e(r4, r5, r2)
        L_0x0065:
            if (r1 != 0) goto L_0x0089
            android.content.ContentResolver r2 = r7.getContentResolver()     // Catch:{ Exception -> 0x007d }
            android.net.Uri r4 = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x007d }
            android.net.Uri r2 = r2.insert(r4, r3)     // Catch:{ Exception -> 0x007d }
            if (r2 != 0) goto L_0x008a
            java.lang.String r1 = LOG_TAG     // Catch:{ Exception -> 0x007b }
            java.lang.String r3 = "Cannot use the internal storage to save media to save image"
            android.util.Log.e(r1, r3)     // Catch:{ Exception -> 0x007b }
            goto L_0x008a
        L_0x007b:
            r1 = move-exception
            goto L_0x0081
        L_0x007d:
            r2 = move-exception
            r6 = r2
            r2 = r1
            r1 = r6
        L_0x0081:
            java.lang.String r3 = LOG_TAG
            java.lang.String r4 = "Unable to insert camera URI into internal storage. Giving up. $e"
            android.util.Log.e(r3, r4, r1)
            goto L_0x008a
        L_0x0089:
            r2 = r1
        L_0x008a:
            if (r2 == 0) goto L_0x00ac
            java.lang.String r1 = "output"
            r0.putExtra(r1, r2)
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "trying to take a photo on "
            r3.append(r4)
            java.lang.String r4 = r2.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.d(r1, r3)
            goto L_0x00b3
        L_0x00ac:
            java.lang.String r1 = LOG_TAG
            java.lang.String r3 = "trying to take a photo with no predefined uri"
            android.util.Log.d(r1, r3)
        L_0x00b3:
            if (r2 != 0) goto L_0x00b7
            r1 = r8
            goto L_0x00bb
        L_0x00b7:
            java.lang.String r1 = r2.toString()
        L_0x00bb:
            r7.startActivityForResult(r0, r9)     // Catch:{ ActivityNotFoundException -> 0x00bf }
            return r1
        L_0x00bf:
            r7 = move-exception
            r7.printStackTrace()
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.CameraApplicationsUtil.openCamera(android.app.Activity, java.lang.String, int):java.lang.String");
    }

    public static void openVideoRecorder(Activity activity, int i) {
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        intent.putExtra("android.intent.extra.videoQuality", 0);
        try {
            activity.startActivityForResult(intent, i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
