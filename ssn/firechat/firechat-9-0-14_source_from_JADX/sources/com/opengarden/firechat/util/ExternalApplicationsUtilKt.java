package com.opengarden.firechat.util;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000:\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a \u0010\u0002\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007\u001a(\u0010\b\u001a\u00020\t2\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0006\u001a\u00020\u0007\u001a\u001e\u0010\u000e\u001a\u00020\t2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0001\u001a\u0016\u0010\u0011\u001a\u00020\t2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007\u001a\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016\u001a\u0018\u0010\u0012\u001a\u00020\t2\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001\u001a\u0016\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0006\u001a\u00020\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001XT¢\u0006\u0002\n\u0000¨\u0006\u0019"}, mo21251d2 = {"LOG_TAG", "", "openCamera", "activity", "Landroid/app/Activity;", "titlePrefix", "requestCode", "", "openFileSelection", "", "fragment", "Landroid/app/Fragment;", "allowMultipleSelection", "", "openMedia", "savedMediaPath", "mimeType", "openSoundRecorder", "openUrlInExternalBrowser", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "url", "openVideoRecorder", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: ExternalApplicationsUtil.kt */
public final class ExternalApplicationsUtilKt {
    private static final String LOG_TAG = "ExternalApplicationsUtil";

    public static final void openUrlInExternalBrowser(@NotNull Context context, @Nullable String str) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        if (str != null) {
            openUrlInExternalBrowser(context, Uri.parse(str));
        }
    }

    public static final void openUrlInExternalBrowser(@NotNull Context context, @Nullable Uri uri) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        if (uri != null) {
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.putExtra("com.android.browser.application_id", context.getPackageName());
            try {
                context.startActivity(intent);
                Unit unit = Unit.INSTANCE;
            } catch (ActivityNotFoundException unused) {
                Toast makeText = Toast.makeText(context, C1299R.string.error_no_external_application_found, 0);
                makeText.show();
                Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, res…uration).apply { show() }");
            }
        }
    }

    public static final void openSoundRecorder(@NotNull Activity activity, int i) {
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        try {
            activity.startActivityForResult(Intent.createChooser(new Intent("android.provider.MediaStore.RECORD_SOUND"), activity.getString(C1299R.string.go_on_with)), i);
        } catch (ActivityNotFoundException unused) {
            Toast makeText = Toast.makeText(activity, C1299R.string.error_no_external_application_found, 0);
            makeText.show();
            Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, res…uration).apply { show() }");
        }
    }

    public static final void openFileSelection(@NotNull Activity activity, @Nullable Fragment fragment, boolean z, int i) {
        Intent intent;
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        if (VERSION.SDK_INT >= 19) {
            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        } else {
            intent = new Intent("android.intent.action.GET_CONTENT");
        }
        if (VERSION.SDK_INT >= 18) {
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }
        intent.setType(ResourceUtils.MIME_TYPE_ALL_CONTENT);
        if (fragment != null) {
            try {
                fragment.startActivityForResult(intent, i);
            } catch (ActivityNotFoundException unused) {
                Toast makeText = Toast.makeText(activity, C1299R.string.error_no_external_application_found, 0);
                makeText.show();
                Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, res…uration).apply { show() }");
            }
        } else {
            activity.startActivityForResult(intent, i);
        }
    }

    public static final void openVideoRecorder(@NotNull Activity activity, int i) {
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        intent.putExtra("android.intent.extra.videoQuality", 0);
        try {
            activity.startActivityForResult(intent, i);
        } catch (ActivityNotFoundException unused) {
            Toast makeText = Toast.makeText(activity, C1299R.string.error_no_external_application_found, 0);
            makeText.show();
            Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, res…uration).apply { show() }");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0082 A[SYNTHETIC, Splitter:B:16:0x0082] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e4  */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String openCamera(@org.jetbrains.annotations.NotNull android.app.Activity r8, @org.jetbrains.annotations.NotNull java.lang.String r9, int r10) {
        /*
            java.lang.String r0 = "activity"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r8, r0)
            java.lang.String r0 = "titlePrefix"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
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
            r5.append(r9)
            java.lang.String r9 = r2.format(r1)
            r5.append(r9)
            java.lang.String r9 = r5.toString()
            r3.put(r4, r9)
            java.lang.String r9 = "mime_type"
            java.lang.String r1 = "image/jpeg"
            r3.put(r9, r1)
            r9 = 0
            r1 = r9
            android.net.Uri r1 = (android.net.Uri) r1
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ UnsupportedOperationException -> 0x0078, Exception -> 0x005d }
            android.net.Uri r4 = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI     // Catch:{ UnsupportedOperationException -> 0x0078, Exception -> 0x005d }
            android.net.Uri r2 = r2.insert(r4, r3)     // Catch:{ UnsupportedOperationException -> 0x0078, Exception -> 0x005d }
            if (r2 != 0) goto L_0x0080
            java.lang.String r1 = "ExternalApplicationsUtil"
            java.lang.String r4 = "Cannot use the external storage media to save image"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r4)     // Catch:{ UnsupportedOperationException -> 0x0079, Exception -> 0x005b }
            goto L_0x0080
        L_0x005b:
            r1 = move-exception
            goto L_0x0061
        L_0x005d:
            r2 = move-exception
            r7 = r2
            r2 = r1
            r1 = r7
        L_0x0061:
            java.lang.String r4 = "ExternalApplicationsUtil"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Unable to insert camera URI into MediaStore.Images.Media.EXTERNAL_CONTENT_URI. "
            r5.append(r6)
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r1)
            goto L_0x0080
        L_0x0078:
            r2 = r1
        L_0x0079:
            java.lang.String r1 = "ExternalApplicationsUtil"
            java.lang.String r4 = "Unable to insert camera URI into MediaStore.Images.Media.EXTERNAL_CONTENT_URI - no SD card? Attempting to insert into device storage."
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r4)
        L_0x0080:
            if (r2 != 0) goto L_0x00b4
            android.content.ContentResolver r1 = r8.getContentResolver()     // Catch:{ Exception -> 0x009d }
            android.net.Uri r4 = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI     // Catch:{ Exception -> 0x009d }
            android.net.Uri r1 = r1.insert(r4, r3)     // Catch:{ Exception -> 0x009d }
            if (r1 != 0) goto L_0x009b
            java.lang.String r2 = "ExternalApplicationsUtil"
            java.lang.String r3 = "Cannot use the internal storage to save media to save image"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)     // Catch:{ Exception -> 0x0096 }
            goto L_0x009b
        L_0x0096:
            r2 = move-exception
            r7 = r2
            r2 = r1
            r1 = r7
            goto L_0x009e
        L_0x009b:
            r2 = r1
            goto L_0x00b4
        L_0x009d:
            r1 = move-exception
        L_0x009e:
            java.lang.String r3 = "ExternalApplicationsUtil"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unable to insert camera URI into internal storage. Giving up. "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r1)
        L_0x00b4:
            if (r2 == 0) goto L_0x00d9
            java.lang.String r1 = "output"
            r3 = r2
            android.os.Parcelable r3 = (android.os.Parcelable) r3
            r0.putExtra(r1, r3)
            java.lang.String r1 = "ExternalApplicationsUtil"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "trying to take a photo on "
            r3.append(r4)
            java.lang.String r4 = r2.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r3)
            goto L_0x00e0
        L_0x00d9:
            java.lang.String r1 = "ExternalApplicationsUtil"
            java.lang.String r3 = "trying to take a photo with no predefined uri"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r3)
        L_0x00e0:
            if (r2 != 0) goto L_0x00e4
            r1 = r9
            goto L_0x00e8
        L_0x00e4:
            java.lang.String r1 = r2.toString()
        L_0x00e8:
            r8.startActivityForResult(r0, r10)     // Catch:{ ActivityNotFoundException -> 0x00ec }
            return r1
        L_0x00ec:
            android.content.Context r8 = (android.content.Context) r8
            r10 = 2131689754(0x7f0f011a, float:1.9008532E38)
            r0 = 0
            android.widget.Toast r8 = android.widget.Toast.makeText(r8, r10, r0)
            r8.show()
            java.lang.String r10 = "Toast.makeText(this, res…uration).apply { show() }"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r8, r10)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.ExternalApplicationsUtilKt.openCamera(android.app.Activity, java.lang.String, int):java.lang.String");
    }

    public static final void openMedia(@NotNull Activity activity, @NotNull String str, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        Intrinsics.checkParameterIsNotNull(str, "savedMediaPath");
        Intrinsics.checkParameterIsNotNull(str2, "mimeType");
        File file = new File(str);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), str2);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast makeText = Toast.makeText(activity, C1299R.string.error_no_external_application_found, 0);
            makeText.show();
            Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, res…uration).apply { show() }");
        }
    }
}
