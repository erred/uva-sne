package com.opengarden.firechat.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.BugReportActivity;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class BugReporter {
    private static final int BUFFER_SIZE = 52428800;
    private static final String CRASH_FILENAME = "crash.log";
    private static final String[] LOGCAT_CMD_DEBUG = {"logcat", "-d", "-v", "threadtime", "*:*"};
    private static final String[] LOGCAT_CMD_ERROR = {"logcat", "-d", "-v", "threadtime", "AndroidRuntime:E libcommunicator:V DEBUG:V *:S"};
    private static final String LOG_CAT_ERROR_FILENAME = "logcatError.log";
    private static final String LOG_CAT_FILENAME = "logcat.log";
    private static final String LOG_CAT_SCREENSHOT_FILENAME = "screenshot.png";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "BugReporter";
    /* access modifiers changed from: private */
    public static Call mBugReportCall = null;
    /* access modifiers changed from: private */
    public static boolean mIsCancelled = false;
    /* access modifiers changed from: private */
    public static final OkHttpClient mOkHttpClient = new OkHttpClient();
    /* access modifiers changed from: private */
    public static Bitmap mScreenshot;

    public interface IMXBugReportListener {
        void onProgress(int i);

        void onUploadCancelled();

        void onUploadFailed(String str);

        void onUploadSucceed();
    }

    public static void sendBugReport(Context context, boolean z, boolean z2, boolean z3, String str, IMXBugReportListener iMXBugReportListener) {
        final String str2 = str;
        final Context context2 = context;
        final boolean z4 = z;
        final boolean z5 = z2;
        final boolean z6 = z3;
        final IMXBugReportListener iMXBugReportListener2 = iMXBugReportListener;
        C28951 r0 = new AsyncTask<Void, Integer, String>() {
            final List<File> mBugReportFiles = new ArrayList();

            /* JADX WARNING: type inference failed for: r8v0 */
            /* JADX WARNING: type inference failed for: r11v22, types: [java.io.InputStream] */
            /* JADX WARNING: type inference failed for: r8v2, types: [java.io.InputStream] */
            /* JADX WARNING: type inference failed for: r1v36 */
            /* JADX WARNING: type inference failed for: r8v3, types: [java.lang.String] */
            /* JADX WARNING: type inference failed for: r11v30 */
            /* JADX WARNING: type inference failed for: r1v37 */
            /* JADX WARNING: type inference failed for: r11v32, types: [java.io.InputStream] */
            /* JADX WARNING: type inference failed for: r8v4, types: [java.lang.String] */
            /* JADX WARNING: type inference failed for: r1v39 */
            /* JADX WARNING: type inference failed for: r8v5 */
            /* JADX WARNING: type inference failed for: r1v40 */
            /* JADX WARNING: type inference failed for: r1v42, types: [java.lang.String] */
            /* JADX WARNING: type inference failed for: r1v43 */
            /* JADX WARNING: type inference failed for: r8v6 */
            /* JADX WARNING: type inference failed for: r0v26, types: [java.lang.String] */
            /* JADX WARNING: type inference failed for: r8v7 */
            /* JADX WARNING: type inference failed for: r2v14, types: [java.lang.String] */
            /* JADX WARNING: type inference failed for: r1v44 */
            /* JADX WARNING: type inference failed for: r8v9 */
            /* JADX WARNING: type inference failed for: r11v45 */
            /* JADX WARNING: type inference failed for: r1v72 */
            /* JADX WARNING: type inference failed for: r1v73 */
            /* JADX WARNING: type inference failed for: r1v74 */
            /* access modifiers changed from: protected */
            /* JADX WARNING: Code restructure failed: missing block: B:105:0x038a, code lost:
                r0 = th;
                r11 = r11;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:106:0x038d, code lost:
                r0 = e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:107:0x038e, code lost:
                r1 = 0;
             */
            /* JADX WARNING: Failed to process nested try/catch */
            /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r1v39
              assigns: []
              uses: []
              mth insns count: 363
            	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
            	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
            	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
            	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
            	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$0(DepthTraversal.java:13)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
            	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
            	at jadx.core.ProcessClass.process(ProcessClass.java:30)
            	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
            	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
            	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
            	at jadx.core.ProcessClass.process(ProcessClass.java:35)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
             */
            /* JADX WARNING: Removed duplicated region for block: B:105:0x038a A[ExcHandler: all (th java.lang.Throwable), Splitter:B:86:0x032f] */
            /* JADX WARNING: Removed duplicated region for block: B:121:0x03da A[SYNTHETIC, Splitter:B:121:0x03da] */
            /* JADX WARNING: Removed duplicated region for block: B:127:0x03ff A[SYNTHETIC, Splitter:B:127:0x03ff] */
            /* JADX WARNING: Removed duplicated region for block: B:142:? A[RETURN, SYNTHETIC] */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x0306  */
            /* JADX WARNING: Unknown variable types count: 11 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public java.lang.String doInBackground(java.lang.Void... r11) {
                /*
                    r10 = this;
                    java.lang.String r11 = r1
                    android.content.Context r0 = r2
                    java.lang.String r0 = com.opengarden.firechat.util.BugReporter.getCrashDescription(r0)
                    if (r0 == 0) goto L_0x002a
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    r1.append(r11)
                    java.lang.String r11 = "\n\n\n\n--------------------------------- crash call stack ---------------------------------\n"
                    r1.append(r11)
                    java.lang.String r11 = r1.toString()
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    r1.append(r11)
                    r1.append(r0)
                    java.lang.String r11 = r1.toString()
                L_0x002a:
                    java.util.ArrayList r0 = new java.util.ArrayList
                    r0.<init>()
                    boolean r1 = r3
                    if (r1 == 0) goto L_0x005c
                    java.util.ArrayList r1 = new java.util.ArrayList
                    r1.<init>()
                    java.util.List r1 = com.opengarden.firechat.matrixsdk.util.Log.addLogFiles(r1)
                    java.util.Iterator r1 = r1.iterator()
                L_0x0040:
                    boolean r2 = r1.hasNext()
                    if (r2 == 0) goto L_0x005c
                    java.lang.Object r2 = r1.next()
                    java.io.File r2 = (java.io.File) r2
                    boolean r3 = com.opengarden.firechat.util.BugReporter.mIsCancelled
                    if (r3 != 0) goto L_0x0040
                    java.io.File r2 = com.opengarden.firechat.util.BugReporter.compressFile(r2)
                    if (r2 == 0) goto L_0x0040
                    r0.add(r2)
                    goto L_0x0040
                L_0x005c:
                    boolean r1 = com.opengarden.firechat.util.BugReporter.mIsCancelled
                    r2 = 0
                    if (r1 != 0) goto L_0x009f
                    boolean r1 = r4
                    if (r1 != 0) goto L_0x006b
                    boolean r1 = r3
                    if (r1 == 0) goto L_0x009f
                L_0x006b:
                    android.content.Context r1 = r2
                    java.io.File r1 = com.opengarden.firechat.util.BugReporter.saveLogCat(r1, r2)
                    if (r1 == 0) goto L_0x0080
                    int r3 = r0.size()
                    if (r3 != 0) goto L_0x007d
                    r0.add(r1)
                    goto L_0x0080
                L_0x007d:
                    r0.add(r2, r1)
                L_0x0080:
                    android.content.Context r1 = r2
                    java.io.File r1 = com.opengarden.firechat.util.BugReporter.getCrashFile(r1)
                    boolean r3 = r1.exists()
                    if (r3 == 0) goto L_0x009f
                    java.io.File r1 = com.opengarden.firechat.util.BugReporter.compressFile(r1)
                    if (r1 == 0) goto L_0x009f
                    int r3 = r0.size()
                    if (r3 != 0) goto L_0x009c
                    r0.add(r1)
                    goto L_0x009f
                L_0x009c:
                    r0.add(r2, r1)
                L_0x009f:
                    android.content.Context r1 = r2
                    com.opengarden.firechat.Matrix r1 = com.opengarden.firechat.Matrix.getInstance(r1)
                    com.opengarden.firechat.matrixsdk.MXSession r1 = r1.getDefaultSession()
                    java.lang.String r3 = "undefined"
                    java.lang.String r4 = "undefined"
                    java.lang.String r5 = "undefined"
                    java.lang.String r6 = "undefined"
                    r7 = 1
                    if (r1 == 0) goto L_0x00c8
                    java.lang.String r4 = r1.getMyUserId()
                    com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r3 = r1.getCredentials()
                    java.lang.String r3 = r3.deviceId
                    java.lang.String r5 = r1.getVersion(r7)
                    android.content.Context r6 = r2
                    java.lang.String r6 = r1.getCryptoVersion(r6, r7)
                L_0x00c8:
                    boolean r1 = com.opengarden.firechat.util.BugReporter.mIsCancelled
                    r8 = 0
                    if (r1 != 0) goto L_0x0432
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r1 = new com.opengarden.firechat.util.BugReporterMultipartBody$Builder
                    r1.<init>()
                    java.lang.String r9 = "text"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r1.addFormDataPart(r9, r11)
                    java.lang.String r1 = "app"
                    java.lang.String r9 = "riot-android"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r9)
                    java.lang.String r1 = "user_agent"
                    java.lang.String r9 = "Android"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r9)
                    java.lang.String r1 = "user_id"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r4)
                    java.lang.String r1 = "device_id"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "version"
                    android.content.Context r3 = r2
                    com.opengarden.firechat.Matrix r3 = com.opengarden.firechat.Matrix.getInstance(r3)
                    java.lang.String r3 = r3.getVersion(r7, r2)
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "branch_name"
                    android.content.Context r3 = r2
                    r4 = 2131689772(0x7f0f012c, float:1.9008569E38)
                    java.lang.String r3 = r3.getString(r4)
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "matrix_sdk_version"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r5)
                    java.lang.String r1 = "olm_version"
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r6)
                    java.lang.String r1 = "device"
                    java.lang.String r3 = android.os.Build.MODEL
                    java.lang.String r3 = r3.trim()
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "os"
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r5 = android.os.Build.VERSION.INCREMENTAL
                    r3.append(r5)
                    java.lang.String r5 = " "
                    r3.append(r5)
                    java.lang.String r5 = android.os.Build.VERSION.RELEASE
                    r3.append(r5)
                    java.lang.String r5 = " "
                    r3.append(r5)
                    java.lang.String r5 = android.os.Build.VERSION.CODENAME
                    r3.append(r5)
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "locale"
                    java.util.Locale r3 = java.util.Locale.getDefault()
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "app_language"
                    java.util.Locale r3 = com.opengarden.firechat.VectorApp.getApplicationLocale()
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    java.lang.String r1 = "default_app_language"
                    java.util.Locale r3 = com.opengarden.firechat.VectorApp.getDeviceLocale()
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.util.BugReporterMultipartBody$Builder r11 = r11.addFormDataPart(r1, r3)
                    android.content.Context r1 = r2
                    r3 = 2131689586(0x7f0f0072, float:1.9008192E38)
                    java.lang.String r1 = r1.getString(r3)
                    boolean r3 = android.text.TextUtils.isEmpty(r1)
                    if (r3 != 0) goto L_0x019b
                    java.lang.String r3 = "0"
                    boolean r3 = r1.equals(r3)
                    if (r3 != 0) goto L_0x019b
                    java.lang.String r3 = "build_number"
                    r11.addFormDataPart(r3, r1)
                L_0x019b:
                    java.util.Iterator r1 = r0.iterator()
                L_0x019f:
                    boolean r3 = r1.hasNext()
                    if (r3 == 0) goto L_0x01bf
                    java.lang.Object r3 = r1.next()
                    java.io.File r3 = (java.io.File) r3
                    java.lang.String r5 = "compressed-log"
                    java.lang.String r6 = r3.getName()
                    java.lang.String r7 = "application/octet-stream"
                    okhttp3.MediaType r7 = okhttp3.MediaType.parse(r7)
                    okhttp3.RequestBody r3 = okhttp3.RequestBody.create(r7, r3)
                    r11.addFormDataPart(r5, r6, r3)
                    goto L_0x019f
                L_0x01bf:
                    java.util.List<java.io.File> r1 = r10.mBugReportFiles
                    r1.addAll(r0)
                    boolean r0 = r5
                    if (r0 == 0) goto L_0x022b
                    android.graphics.Bitmap r0 = com.opengarden.firechat.util.BugReporter.mScreenshot
                    if (r0 == 0) goto L_0x022b
                    java.io.File r1 = new java.io.File
                    android.content.Context r3 = r2
                    java.io.File r3 = r3.getCacheDir()
                    java.lang.String r3 = r3.getAbsolutePath()
                    java.lang.String r5 = "screenshot.png"
                    r1.<init>(r3, r5)
                    boolean r3 = r1.exists()
                    if (r3 == 0) goto L_0x01e8
                    r1.delete()
                L_0x01e8:
                    java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x020e }
                    r3.<init>(r1)     // Catch:{ Exception -> 0x020e }
                    android.graphics.Bitmap$CompressFormat r5 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Exception -> 0x020e }
                    r6 = 100
                    r0.compress(r5, r6, r3)     // Catch:{ Exception -> 0x020e }
                    r3.flush()     // Catch:{ Exception -> 0x020e }
                    r3.close()     // Catch:{ Exception -> 0x020e }
                    java.lang.String r0 = "file"
                    java.lang.String r3 = r1.getName()     // Catch:{ Exception -> 0x020e }
                    java.lang.String r5 = "application/octet-stream"
                    okhttp3.MediaType r5 = okhttp3.MediaType.parse(r5)     // Catch:{ Exception -> 0x020e }
                    okhttp3.RequestBody r1 = okhttp3.RequestBody.create(r5, r1)     // Catch:{ Exception -> 0x020e }
                    r11.addFormDataPart(r0, r3, r1)     // Catch:{ Exception -> 0x020e }
                    goto L_0x022b
                L_0x020e:
                    r0 = move-exception
                    java.lang.String r1 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r5 = "## saveLogCat() : fail to write logcat"
                    r3.append(r5)
                    java.lang.String r0 = r0.toString()
                    r3.append(r0)
                    java.lang.String r0 = r3.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r0)
                L_0x022b:
                    com.opengarden.firechat.util.BugReporter.mScreenshot = r8
                    android.content.Context r0 = r2     // Catch:{ Exception -> 0x0246 }
                    android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ Exception -> 0x0246 }
                    android.content.Context r1 = r2     // Catch:{ Exception -> 0x0246 }
                    java.lang.String r1 = r1.getPackageName()     // Catch:{ Exception -> 0x0246 }
                    android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r2)     // Catch:{ Exception -> 0x0246 }
                    java.lang.String r1 = "label"
                    java.lang.String r0 = r0.versionName     // Catch:{ Exception -> 0x0246 }
                    r11.addFormDataPart(r1, r0)     // Catch:{ Exception -> 0x0246 }
                    goto L_0x0263
                L_0x0246:
                    r0 = move-exception
                    java.lang.String r1 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## sendBugReport() : cannot retrieve the appname "
                    r2.append(r3)
                    java.lang.String r0 = r0.getMessage()
                    r2.append(r0)
                    java.lang.String r0 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r0)
                L_0x0263:
                    java.lang.String r0 = "label"
                    android.content.Context r1 = r2
                    android.content.res.Resources r1 = r1.getResources()
                    r2 = 2131689762(0x7f0f0122, float:1.9008549E38)
                    java.lang.String r1 = r1.getString(r2)
                    r11.addFormDataPart(r0, r1)
                    java.lang.String r0 = "label"
                    android.content.Context r1 = r2
                    java.lang.String r1 = r1.getString(r4)
                    r11.addFormDataPart(r0, r1)
                    android.content.Context r0 = r2
                    java.io.File r0 = com.opengarden.firechat.util.BugReporter.getCrashFile(r0)
                    boolean r0 = r0.exists()
                    if (r0 == 0) goto L_0x0298
                    java.lang.String r0 = "label"
                    java.lang.String r1 = "crash"
                    r11.addFormDataPart(r0, r1)
                    android.content.Context r0 = r2
                    com.opengarden.firechat.util.BugReporter.deleteCrashFile(r0)
                L_0x0298:
                    com.opengarden.firechat.util.BugReporterMultipartBody r11 = r11.build()
                    com.opengarden.firechat.util.BugReporter$1$1 r0 = new com.opengarden.firechat.util.BugReporter$1$1
                    r0.<init>()
                    r11.setWriteListener(r0)
                    okhttp3.Request$Builder r0 = new okhttp3.Request$Builder
                    r0.<init>()
                    android.content.Context r1 = r2
                    android.content.res.Resources r1 = r1.getResources()
                    r2 = 2131689585(0x7f0f0071, float:1.900819E38)
                    java.lang.String r1 = r1.getString(r2)
                    okhttp3.Request$Builder r0 = r0.url(r1)
                    okhttp3.Request$Builder r11 = r0.post(r11)
                    okhttp3.Request r11 = r11.build()
                    r0 = 500(0x1f4, float:7.0E-43)
                    okhttp3.OkHttpClient r1 = com.opengarden.firechat.util.BugReporter.mOkHttpClient     // Catch:{ Exception -> 0x02e0 }
                    okhttp3.Call r11 = r1.newCall(r11)     // Catch:{ Exception -> 0x02e0 }
                    com.opengarden.firechat.util.BugReporter.mBugReportCall = r11     // Catch:{ Exception -> 0x02e0 }
                    okhttp3.Call r11 = com.opengarden.firechat.util.BugReporter.mBugReportCall     // Catch:{ Exception -> 0x02e0 }
                    okhttp3.Response r11 = r11.execute()     // Catch:{ Exception -> 0x02e0 }
                    int r1 = r11.code()     // Catch:{ Exception -> 0x02de }
                    r0 = r1
                    r1 = r8
                    goto L_0x0302
                L_0x02de:
                    r1 = move-exception
                    goto L_0x02e2
                L_0x02e0:
                    r1 = move-exception
                    r11 = r8
                L_0x02e2:
                    java.lang.String r2 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "response "
                    r3.append(r4)
                    java.lang.String r4 = r1.getMessage()
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)
                    java.lang.String r1 = r1.getLocalizedMessage()
                L_0x0302:
                    r2 = 200(0xc8, float:2.8E-43)
                    if (r0 == r2) goto L_0x0432
                    if (r1 == 0) goto L_0x031b
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder
                    r11.<init>()
                    java.lang.String r0 = "Failed with error "
                    r11.append(r0)
                    r11.append(r1)
                    java.lang.String r8 = r11.toString()
                    goto L_0x0432
                L_0x031b:
                    if (r11 == 0) goto L_0x0421
                    okhttp3.ResponseBody r1 = r11.body()
                    if (r1 != 0) goto L_0x0325
                    goto L_0x0421
                L_0x0325:
                    okhttp3.ResponseBody r11 = r11.body()     // Catch:{ Exception -> 0x03ba }
                    java.io.InputStream r11 = r11.byteStream()     // Catch:{ Exception -> 0x03ba }
                    if (r11 == 0) goto L_0x0391
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                    r1.<init>()     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                L_0x0334:
                    int r2 = r11.read()     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                    r3 = -1
                    if (r2 == r3) goto L_0x0340
                    char r2 = (char) r2     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                    r1.append(r2)     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                    goto L_0x0334
                L_0x0340:
                    java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x038d, all -> 0x038a }
                    r11.close()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0354 }
                    r2.<init>(r1)     // Catch:{ JSONException -> 0x0354 }
                    java.lang.String r3 = "error"
                    java.lang.String r2 = r2.getString(r3)     // Catch:{ JSONException -> 0x0354 }
                    r1 = r2
                    goto L_0x0371
                L_0x0354:
                    r2 = move-exception
                    java.lang.String r3 = com.opengarden.firechat.util.BugReporter.LOG_TAG     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    r4.<init>()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.String r5 = "doInBackground ; Json conversion failed "
                    r4.append(r5)     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.String r2 = r2.getMessage()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    r4.append(r2)     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                L_0x0371:
                    if (r1 != 0) goto L_0x0386
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    r2.<init>()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.String r3 = "Failed with error "
                    r2.append(r3)     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    r2.append(r0)     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x0388, all -> 0x038a }
                    r8 = r0
                    goto L_0x0391
                L_0x0386:
                    r8 = r1
                    goto L_0x0391
                L_0x0388:
                    r0 = move-exception
                    goto L_0x038f
                L_0x038a:
                    r0 = move-exception
                    goto L_0x03fd
                L_0x038d:
                    r0 = move-exception
                    r1 = r8
                L_0x038f:
                    r8 = r11
                    goto L_0x03bc
                L_0x0391:
                    if (r11 == 0) goto L_0x0432
                    r11.close()     // Catch:{ Exception -> 0x0398 }
                    goto L_0x0432
                L_0x0398:
                    r11 = move-exception
                    java.lang.String r0 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder
                    r1.<init>()
                    java.lang.String r2 = "## sendBugReport() : failed to close the error stream "
                    r1.append(r2)
                    java.lang.String r11 = r11.getMessage()
                    r1.append(r11)
                    java.lang.String r11 = r1.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r11)
                    goto L_0x0432
                L_0x03b7:
                    r0 = move-exception
                    r11 = r8
                    goto L_0x03fd
                L_0x03ba:
                    r0 = move-exception
                    r1 = r8
                L_0x03bc:
                    java.lang.String r11 = com.opengarden.firechat.util.BugReporter.LOG_TAG     // Catch:{ all -> 0x03b7 }
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03b7 }
                    r2.<init>()     // Catch:{ all -> 0x03b7 }
                    java.lang.String r3 = "## sendBugReport() : failed to parse error "
                    r2.append(r3)     // Catch:{ all -> 0x03b7 }
                    java.lang.String r0 = r0.getMessage()     // Catch:{ all -> 0x03b7 }
                    r2.append(r0)     // Catch:{ all -> 0x03b7 }
                    java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x03b7 }
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)     // Catch:{ all -> 0x03b7 }
                    if (r8 == 0) goto L_0x03fb
                    r8.close()     // Catch:{ Exception -> 0x03de }
                    goto L_0x03fb
                L_0x03de:
                    r11 = move-exception
                    java.lang.String r0 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## sendBugReport() : failed to close the error stream "
                    r2.append(r3)
                    java.lang.String r11 = r11.getMessage()
                    r2.append(r11)
                    java.lang.String r11 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r11)
                L_0x03fb:
                    r8 = r1
                    goto L_0x0432
                L_0x03fd:
                    if (r11 == 0) goto L_0x0420
                    r11.close()     // Catch:{ Exception -> 0x0403 }
                    goto L_0x0420
                L_0x0403:
                    r11 = move-exception
                    java.lang.String r1 = com.opengarden.firechat.util.BugReporter.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## sendBugReport() : failed to close the error stream "
                    r2.append(r3)
                    java.lang.String r11 = r11.getMessage()
                    r2.append(r11)
                    java.lang.String r11 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r11)
                L_0x0420:
                    throw r0
                L_0x0421:
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder
                    r11.<init>()
                    java.lang.String r1 = "Failed with error "
                    r11.append(r1)
                    r11.append(r0)
                    java.lang.String r8 = r11.toString()
                L_0x0432:
                    return r8
                */
                throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.BugReporter.C28951.doInBackground(java.lang.Void[]):java.lang.String");
            }

            /* access modifiers changed from: protected */
            public void onProgressUpdate(Integer... numArr) {
                super.onProgressUpdate(numArr);
                if (iMXBugReportListener2 != null) {
                    try {
                        IMXBugReportListener iMXBugReportListener = iMXBugReportListener2;
                        int i = 0;
                        if (numArr != null) {
                            i = numArr[0].intValue();
                        }
                        iMXBugReportListener.onProgress(i);
                    } catch (Exception e) {
                        String access$600 = BugReporter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## onProgress() : failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$600, sb.toString());
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(String str) {
                BugReporter.mBugReportCall = null;
                for (File delete : this.mBugReportFiles) {
                    delete.delete();
                }
                if (iMXBugReportListener2 != null) {
                    try {
                        if (BugReporter.mIsCancelled) {
                            iMXBugReportListener2.onUploadCancelled();
                        } else if (str == null) {
                            iMXBugReportListener2.onUploadSucceed();
                        } else {
                            iMXBugReportListener2.onUploadFailed(str);
                        }
                    } catch (Exception e) {
                        String access$600 = BugReporter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## onPostExecute() : failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$600, sb.toString());
                    }
                }
            }
        };
        r0.execute(new Void[0]);
    }

    public static void sendBugReport() {
        mScreenshot = takeScreenshot();
        Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity == null) {
            sendBugReport(VectorApp.getInstance().getApplicationContext(), true, true, true, "", null);
        } else {
            currentActivity.startActivity(new Intent(currentActivity, BugReportActivity.class));
        }
    }

    /* access modifiers changed from: private */
    public static File getCrashFile(Context context) {
        return new File(context.getCacheDir().getAbsolutePath(), CRASH_FILENAME);
    }

    public static void deleteCrashFile(Context context) {
        File crashFile = getCrashFile(context);
        if (crashFile.exists()) {
            crashFile.delete();
        }
    }

    public static void saveCrashReport(Context context, String str) {
        File crashFile = getCrashFile(context);
        if (crashFile.exists()) {
            crashFile.delete();
        }
        if (!TextUtils.isEmpty(str)) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(crashFile);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                outputStreamWriter.write(str);
                outputStreamWriter.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## saveCrashReport() : fail to write ");
                sb.append(e.toString());
                Log.m211e(str2, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public static String getCrashDescription(Context context) {
        File crashFile = getCrashFile(context);
        String str = null;
        if (!crashFile.exists()) {
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(crashFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            char[] cArr = new char[fileInputStream.available()];
            String valueOf = String.valueOf(cArr, 0, inputStreamReader.read(cArr, 0, fileInputStream.available()));
            try {
                inputStreamReader.close();
                fileInputStream.close();
                return valueOf;
            } catch (Exception e) {
                e = e;
                str = valueOf;
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getCrashDescription() : fail to read ");
                sb.append(e.toString());
                Log.m211e(str2, sb.toString());
                return str;
            }
        } catch (Exception e2) {
            e = e2;
            String str22 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getCrashDescription() : fail to read ");
            sb2.append(e.toString());
            Log.m211e(str22, sb2.toString());
            return str;
        }
    }

    private static Bitmap takeScreenshot() {
        if (VectorApp.getCurrentActivity() == null) {
            return null;
        }
        View findViewById = VectorApp.getCurrentActivity().findViewById(16908290);
        if (findViewById == null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot find content view on ");
            sb.append(VectorApp.getCurrentActivity());
            sb.append(". Cannot take screenshot.");
            Log.m211e(str, sb.toString());
            return null;
        }
        View rootView = findViewById.getRootView();
        if (rootView == null) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Cannot find root view on ");
            sb2.append(VectorApp.getCurrentActivity());
            sb2.append(". Cannot take screenshot.");
            Log.m211e(str2, sb2.toString());
            return null;
        }
        rootView.setDrawingCacheEnabled(false);
        rootView.setDrawingCacheEnabled(true);
        try {
            return rootView.getDrawingCache();
        } catch (OutOfMemoryError unused) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Cannot get drawing cache for ");
            sb3.append(VectorApp.getCurrentActivity());
            sb3.append(" OOM.");
            Log.m211e(str3, sb3.toString());
            return null;
        } catch (Exception e) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Cannot get snapshot of screen: ");
            sb4.append(e);
            Log.m211e(str4, sb4.toString());
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static File saveLogCat(Context context, boolean z) {
        File file = new File(context.getCacheDir().getAbsolutePath(), z ? LOG_CAT_ERROR_FILENAME : LOG_CAT_FILENAME);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            getLogCatError(outputStreamWriter, z);
            outputStreamWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return compressFile(file);
        } catch (OutOfMemoryError e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## saveLogCat() : fail to write logcat");
            sb.append(e.toString());
            Log.m211e(str, sb.toString());
            return null;
        } catch (Exception e2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## saveLogCat() : fail to write logcat");
            sb2.append(e2.toString());
            Log.m211e(str2, sb2.toString());
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x007a A[SYNTHETIC, Splitter:B:28:0x007a] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x008a A[SYNTHETIC, Splitter:B:34:0x008a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void getLogCatError(java.io.OutputStreamWriter r4, boolean r5) {
        /*
            java.lang.Runtime r0 = java.lang.Runtime.getRuntime()     // Catch:{ IOException -> 0x00aa }
            if (r5 == 0) goto L_0x0009
            java.lang.String[] r5 = LOGCAT_CMD_ERROR     // Catch:{ IOException -> 0x00aa }
            goto L_0x000b
        L_0x0009:
            java.lang.String[] r5 = LOGCAT_CMD_DEBUG     // Catch:{ IOException -> 0x00aa }
        L_0x000b:
            java.lang.Process r5 = r0.exec(r5)     // Catch:{ IOException -> 0x00aa }
            r0 = 0
            java.lang.String r1 = "line.separator"
            java.lang.String r1 = java.lang.System.getProperty(r1)     // Catch:{ IOException -> 0x005d }
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x005d }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x005d }
            java.io.InputStream r5 = r5.getInputStream()     // Catch:{ IOException -> 0x005d }
            r3.<init>(r5)     // Catch:{ IOException -> 0x005d }
            r5 = 52428800(0x3200000, float:4.7019774E-37)
            r2.<init>(r3, r5)     // Catch:{ IOException -> 0x005d }
        L_0x0026:
            java.lang.String r5 = r2.readLine()     // Catch:{ IOException -> 0x0058, all -> 0x0055 }
            if (r5 == 0) goto L_0x0033
            r4.append(r5)     // Catch:{ IOException -> 0x0058, all -> 0x0055 }
            r4.append(r1)     // Catch:{ IOException -> 0x0058, all -> 0x0055 }
            goto L_0x0026
        L_0x0033:
            if (r2 == 0) goto L_0x0087
            r2.close()     // Catch:{ IOException -> 0x0039 }
            goto L_0x0087
        L_0x0039:
            r4 = move-exception
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
        L_0x0041:
            java.lang.String r1 = "getLog fails with "
            r0.append(r1)
            java.lang.String r4 = r4.getLocalizedMessage()
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)
            goto L_0x0087
        L_0x0055:
            r4 = move-exception
            r0 = r2
            goto L_0x0088
        L_0x0058:
            r4 = move-exception
            r0 = r2
            goto L_0x005e
        L_0x005b:
            r4 = move-exception
            goto L_0x0088
        L_0x005d:
            r4 = move-exception
        L_0x005e:
            java.lang.String r5 = LOG_TAG     // Catch:{ all -> 0x005b }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x005b }
            r1.<init>()     // Catch:{ all -> 0x005b }
            java.lang.String r2 = "getLog fails with "
            r1.append(r2)     // Catch:{ all -> 0x005b }
            java.lang.String r4 = r4.getLocalizedMessage()     // Catch:{ all -> 0x005b }
            r1.append(r4)     // Catch:{ all -> 0x005b }
            java.lang.String r4 = r1.toString()     // Catch:{ all -> 0x005b }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)     // Catch:{ all -> 0x005b }
            if (r0 == 0) goto L_0x0087
            r0.close()     // Catch:{ IOException -> 0x007e }
            goto L_0x0087
        L_0x007e:
            r4 = move-exception
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            goto L_0x0041
        L_0x0087:
            return
        L_0x0088:
            if (r0 == 0) goto L_0x00a9
            r0.close()     // Catch:{ IOException -> 0x008e }
            goto L_0x00a9
        L_0x008e:
            r5 = move-exception
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "getLog fails with "
            r1.append(r2)
            java.lang.String r5 = r5.getLocalizedMessage()
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r5)
        L_0x00a9:
            throw r4
        L_0x00aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.BugReporter.getLogCatError(java.io.OutputStreamWriter, boolean):void");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r3v2, types: [java.util.zip.GZIPOutputStream] */
    /* JADX WARNING: type inference failed for: r4v1 */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r4v2, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r3v6, types: [java.util.zip.GZIPOutputStream] */
    /* JADX WARNING: type inference failed for: r3v7 */
    /* JADX WARNING: type inference failed for: r4v3 */
    /* JADX WARNING: type inference failed for: r3v8 */
    /* JADX WARNING: type inference failed for: r4v4, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r3v9, types: [java.util.zip.GZIPOutputStream] */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: type inference failed for: r4v5 */
    /* JADX WARNING: type inference failed for: r3v11 */
    /* JADX WARNING: type inference failed for: r3v12 */
    /* JADX WARNING: type inference failed for: r4v6 */
    /* JADX WARNING: type inference failed for: r3v13 */
    /* JADX WARNING: type inference failed for: r3v14 */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r3v16 */
    /* JADX WARNING: type inference failed for: r3v17, types: [java.util.zip.GZIPOutputStream] */
    /* JADX WARNING: type inference failed for: r4v7 */
    /* JADX WARNING: type inference failed for: r4v8 */
    /* JADX WARNING: type inference failed for: r4v9 */
    /* JADX WARNING: type inference failed for: r4v10, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r4v11 */
    /* JADX WARNING: type inference failed for: r3v19 */
    /* JADX WARNING: type inference failed for: r4v12 */
    /* JADX WARNING: type inference failed for: r4v13 */
    /* JADX WARNING: type inference failed for: r3v20 */
    /* JADX WARNING: type inference failed for: r3v21 */
    /* JADX WARNING: type inference failed for: r3v22 */
    /* JADX WARNING: type inference failed for: r4v14 */
    /* JADX WARNING: type inference failed for: r4v15 */
    /* JADX WARNING: type inference failed for: r3v23 */
    /* JADX WARNING: type inference failed for: r3v24 */
    /* JADX WARNING: type inference failed for: r3v25 */
    /* JADX WARNING: type inference failed for: r3v26 */
    /* JADX WARNING: type inference failed for: r3v27 */
    /* JADX WARNING: type inference failed for: r3v28 */
    /* JADX WARNING: type inference failed for: r3v29 */
    /* JADX WARNING: type inference failed for: r3v30 */
    /* JADX WARNING: type inference failed for: r3v31 */
    /* JADX WARNING: type inference failed for: r4v16 */
    /* JADX WARNING: type inference failed for: r4v17 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r4v1
      assigns: []
      uses: []
      mth insns count: 171
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00fe A[SYNTHETIC, Splitter:B:51:0x00fe] */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0106 A[Catch:{ Exception -> 0x0102 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x010b A[Catch:{ Exception -> 0x0102 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0137 A[SYNTHETIC, Splitter:B:66:0x0137] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x013f A[Catch:{ Exception -> 0x013b }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0144 A[Catch:{ Exception -> 0x013b }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0166 A[SYNTHETIC, Splitter:B:79:0x0166] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x016e A[Catch:{ Exception -> 0x016a }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0173 A[Catch:{ Exception -> 0x016a }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:48:0x00e2=Splitter:B:48:0x00e2, B:63:0x011b=Splitter:B:63:0x011b} */
    /* JADX WARNING: Unknown variable types count: 16 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.File compressFile(java.io.File r9) {
        /*
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## compressFile() : compress "
            r1.append(r2)
            java.lang.String r2 = r9.getName()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            java.io.File r0 = new java.io.File
            java.lang.String r1 = r9.getParent()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r9.getName()
            r2.append(r3)
            java.lang.String r3 = ".gz"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            boolean r1 = r0.exists()
            if (r1 == 0) goto L_0x0041
            r0.delete()
        L_0x0041:
            r1 = 0
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0117, OutOfMemoryError -> 0x00de, all -> 0x00d8 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x0117, OutOfMemoryError -> 0x00de, all -> 0x00d8 }
            java.util.zip.GZIPOutputStream r3 = new java.util.zip.GZIPOutputStream     // Catch:{ Exception -> 0x00d5, OutOfMemoryError -> 0x00d2, all -> 0x00cf }
            r3.<init>(r2)     // Catch:{ Exception -> 0x00d5, OutOfMemoryError -> 0x00d2, all -> 0x00cf }
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00cc, OutOfMemoryError -> 0x00c9, all -> 0x00c5 }
            r4.<init>(r9)     // Catch:{ Exception -> 0x00cc, OutOfMemoryError -> 0x00c9, all -> 0x00c5 }
            r5 = 2048(0x800, float:2.87E-42)
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
        L_0x0055:
            int r6 = r4.read(r5)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            r7 = -1
            if (r6 == r7) goto L_0x0061
            r7 = 0
            r3.write(r5, r7, r6)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            goto L_0x0055
        L_0x0061:
            r3.close()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            r4.close()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.String r5 = LOG_TAG     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            r6.<init>()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.String r7 = "## compressFile() : "
            r6.append(r7)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            long r7 = r9.length()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            r6.append(r7)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.String r9 = " compressed to "
            r6.append(r9)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            long r7 = r0.length()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            r6.append(r7)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.String r9 = " bytes"
            r6.append(r9)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            java.lang.String r9 = r6.toString()     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r9)     // Catch:{ Exception -> 0x00c2, OutOfMemoryError -> 0x00c0 }
            if (r2 == 0) goto L_0x009a
            r2.close()     // Catch:{ Exception -> 0x0098 }
            goto L_0x009a
        L_0x0098:
            r9 = move-exception
            goto L_0x00a5
        L_0x009a:
            if (r3 == 0) goto L_0x009f
            r3.close()     // Catch:{ Exception -> 0x0098 }
        L_0x009f:
            if (r4 == 0) goto L_0x00bf
            r4.close()     // Catch:{ Exception -> 0x0098 }
            goto L_0x00bf
        L_0x00a5:
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## compressFile() failed to close inputStream "
            r2.append(r3)
            java.lang.String r9 = r9.getMessage()
            r2.append(r9)
            java.lang.String r9 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r9)
        L_0x00bf:
            return r0
        L_0x00c0:
            r9 = move-exception
            goto L_0x00e2
        L_0x00c2:
            r9 = move-exception
            goto L_0x011b
        L_0x00c5:
            r9 = move-exception
            r4 = r1
            goto L_0x0164
        L_0x00c9:
            r9 = move-exception
            r4 = r1
            goto L_0x00e2
        L_0x00cc:
            r9 = move-exception
            r4 = r1
            goto L_0x011b
        L_0x00cf:
            r9 = move-exception
            r3 = r1
            goto L_0x00db
        L_0x00d2:
            r9 = move-exception
            r3 = r1
            goto L_0x00e1
        L_0x00d5:
            r9 = move-exception
            r3 = r1
            goto L_0x011a
        L_0x00d8:
            r9 = move-exception
            r2 = r1
            r3 = r2
        L_0x00db:
            r4 = r3
            goto L_0x0164
        L_0x00de:
            r9 = move-exception
            r2 = r1
            r3 = r2
        L_0x00e1:
            r4 = r3
        L_0x00e2:
            java.lang.String r0 = LOG_TAG     // Catch:{ all -> 0x0163 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0163 }
            r5.<init>()     // Catch:{ all -> 0x0163 }
            java.lang.String r6 = "## compressFile() failed "
            r5.append(r6)     // Catch:{ all -> 0x0163 }
            java.lang.String r9 = r9.getMessage()     // Catch:{ all -> 0x0163 }
            r5.append(r9)     // Catch:{ all -> 0x0163 }
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0163 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r9)     // Catch:{ all -> 0x0163 }
            if (r2 == 0) goto L_0x0104
            r2.close()     // Catch:{ Exception -> 0x0102 }
            goto L_0x0104
        L_0x0102:
            r9 = move-exception
            goto L_0x010f
        L_0x0104:
            if (r3 == 0) goto L_0x0109
            r3.close()     // Catch:{ Exception -> 0x0102 }
        L_0x0109:
            if (r4 == 0) goto L_0x0162
            r4.close()     // Catch:{ Exception -> 0x0102 }
            goto L_0x0162
        L_0x010f:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            goto L_0x014f
        L_0x0117:
            r9 = move-exception
            r2 = r1
            r3 = r2
        L_0x011a:
            r4 = r3
        L_0x011b:
            java.lang.String r0 = LOG_TAG     // Catch:{ all -> 0x0163 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0163 }
            r5.<init>()     // Catch:{ all -> 0x0163 }
            java.lang.String r6 = "## compressFile() failed "
            r5.append(r6)     // Catch:{ all -> 0x0163 }
            java.lang.String r9 = r9.getMessage()     // Catch:{ all -> 0x0163 }
            r5.append(r9)     // Catch:{ all -> 0x0163 }
            java.lang.String r9 = r5.toString()     // Catch:{ all -> 0x0163 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r9)     // Catch:{ all -> 0x0163 }
            if (r2 == 0) goto L_0x013d
            r2.close()     // Catch:{ Exception -> 0x013b }
            goto L_0x013d
        L_0x013b:
            r9 = move-exception
            goto L_0x0148
        L_0x013d:
            if (r3 == 0) goto L_0x0142
            r3.close()     // Catch:{ Exception -> 0x013b }
        L_0x0142:
            if (r4 == 0) goto L_0x0162
            r4.close()     // Catch:{ Exception -> 0x013b }
            goto L_0x0162
        L_0x0148:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
        L_0x014f:
            java.lang.String r3 = "## compressFile() failed to close inputStream "
            r2.append(r3)
            java.lang.String r9 = r9.getMessage()
            r2.append(r9)
            java.lang.String r9 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r9)
        L_0x0162:
            return r1
        L_0x0163:
            r9 = move-exception
        L_0x0164:
            if (r2 == 0) goto L_0x016c
            r2.close()     // Catch:{ Exception -> 0x016a }
            goto L_0x016c
        L_0x016a:
            r0 = move-exception
            goto L_0x0177
        L_0x016c:
            if (r3 == 0) goto L_0x0171
            r3.close()     // Catch:{ Exception -> 0x016a }
        L_0x0171:
            if (r4 == 0) goto L_0x0191
            r4.close()     // Catch:{ Exception -> 0x016a }
            goto L_0x0191
        L_0x0177:
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## compressFile() failed to close inputStream "
            r2.append(r3)
            java.lang.String r0 = r0.getMessage()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r0)
        L_0x0191:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.BugReporter.compressFile(java.io.File):java.io.File");
    }
}
