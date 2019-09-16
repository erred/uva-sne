package com.opengarden.firechat.matrixsdk.p007db;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.p000v4.media.session.PlaybackStateCompat;
import android.support.p000v4.util.LruCache;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import com.google.android.gms.common.util.AndroidUtilsLight;
import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener.DownloadStats;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.MXOsHandler;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.altbeacon.bluetooth.Pdu;

/* renamed from: com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask */
class MXMediaDownloadWorkerTask extends AsyncTask<Void, Void, JsonElement> {
    private static final int DOWNLOAD_BUFFER_READ_SIZE = 32768;
    private static final int DOWNLOAD_TIME_OUT = 10000;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXMediaDownloadWorkerTask";
    /* access modifiers changed from: private */
    public static LruCache<String, Bitmap> sBitmapByDownloadIdCache;
    private static final Map<String, MXMediaDownloadWorkerTask> sPendingDownloadById = new HashMap();
    /* access modifiers changed from: private */
    public static final Object sSyncObject = new Object();
    private static final List<String> sUnreachableUrls = new ArrayList();
    private Context mApplicationContext;
    /* access modifiers changed from: private */
    public Bitmap mDefaultBitmap;
    private File mDirectoryFile;
    private String mDownloadId;
    private final List<IMXMediaDownloadListener> mDownloadListeners = new ArrayList();
    private DownloadStats mDownloadStats;
    private final EncryptedFileInfo mEncryptedFileInfo;
    private final HomeServerConnectionConfig mHsConfig;
    private final List<WeakReference<ImageView>> mImageViewReferences;
    private boolean mIsAvScannerEnabled;
    /* access modifiers changed from: private */
    public boolean mIsDone;
    private boolean mIsDownloadCancelled;
    @Nullable
    private MediaScanRestClient mMediaScanRestClient;
    private String mMimeType;
    private final NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private int mRotation;
    private String mUrl;

    public static void clearBitmapsCache() {
        if (sBitmapByDownloadIdCache != null) {
            sBitmapByDownloadIdCache.evictAll();
        }
        synchronized (sUnreachableUrls) {
            sUnreachableUrls.clear();
        }
    }

    public static MXMediaDownloadWorkerTask getMediaDownloadWorkerTask(String str) {
        MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask;
        if (!sPendingDownloadById.containsKey(str)) {
            return null;
        }
        synchronized (sPendingDownloadById) {
            mXMediaDownloadWorkerTask = (MXMediaDownloadWorkerTask) sPendingDownloadById.get(str);
        }
        return mXMediaDownloadWorkerTask;
    }

    private static String uniqueId(String str) {
        String str2;
        try {
            byte[] digest = MessageDigest.getInstance(AndroidUtilsLight.DIGEST_ALGORITHM_SHA1).digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : digest) {
                stringBuffer.append(Integer.toString((b & Pdu.MANUFACTURER_DATA_PDU_TYPE) + 256, 16).substring(1));
            }
            str2 = stringBuffer.toString();
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("uniqueId failed ");
            sb.append(e.getMessage());
            Log.m212e(str3, sb.toString(), e);
            str2 = null;
        }
        if (str2 != null) {
            return str2;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("");
        int hashCode = str.hashCode();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(System.currentTimeMillis());
        sb3.append("");
        sb2.append(Math.abs(hashCode + sb3.toString().hashCode()));
        return sb2.toString();
    }

    static String buildFileName(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append("file_");
        sb.append(uniqueId(str));
        String sb2 = sb.toString();
        if (TextUtils.isEmpty(str2)) {
            return sb2;
        }
        String extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(str2);
        if ("jpeg".equals(extensionFromMimeType)) {
            extensionFromMimeType = "jpg";
        }
        if (extensionFromMimeType == null) {
            return sb2;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(".");
        sb3.append(extensionFromMimeType);
        return sb3.toString();
    }

    public static boolean isMediaCached(String str) {
        boolean z = false;
        if (sBitmapByDownloadIdCache != null) {
            synchronized (sSyncObject) {
                if (sBitmapByDownloadIdCache.get(str) != null) {
                    z = true;
                }
            }
        }
        return z;
    }

    public static boolean isMediaUrlUnreachable(String str) {
        boolean contains;
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        synchronized (sUnreachableUrls) {
            contains = sUnreachableUrls.contains(str);
        }
        return contains;
    }

    /* JADX INFO: finally extract failed */
    static boolean bitmapForURL(Context context, File file, String str, String str2, int i, String str3, EncryptedFileInfo encryptedFileInfo, ApiCallback<Bitmap> apiCallback) {
        final Bitmap bitmap;
        File file2 = file;
        final String str4 = str2;
        if (TextUtils.isEmpty(str)) {
            Log.m209d(LOG_TAG, "bitmapForURL : null url");
            return false;
        }
        if (sBitmapByDownloadIdCache == null) {
            int min = Math.min(20971520, ((int) Runtime.getRuntime().maxMemory()) / 8);
            String str5 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("bitmapForURL  lruSize : ");
            sb.append(min);
            Log.m209d(str5, sb.toString());
            sBitmapByDownloadIdCache = new LruCache<String, Bitmap>(min) {
                /* access modifiers changed from: protected */
                public int sizeOf(String str, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }
        if (getMediaDownloadWorkerTask(str4) != null || isMediaUrlUnreachable(str)) {
            return false;
        }
        synchronized (sSyncObject) {
            try {
                bitmap = (Bitmap) sBitmapByDownloadIdCache.get(str4);
            } catch (Throwable th) {
                while (true) {
                    throw th;
                }
            }
        }
        if (bitmap != null) {
            final ApiCallback<Bitmap> apiCallback2 = apiCallback;
            MXMediasCache.mUIHandler.post(new Runnable() {
                public void run() {
                    apiCallback2.onSuccess(bitmap);
                }
            });
            return true;
        }
        final ApiCallback<Bitmap> apiCallback3 = apiCallback;
        if (file2 == null) {
            return false;
        }
        String str6 = null;
        String str7 = str;
        if (str7.startsWith("file:")) {
            try {
                str6 = Uri.parse(str7).getPath();
            } catch (Exception e) {
                Exception exc = e;
                String str8 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("bitmapForURL #1 : ");
                sb2.append(exc.getMessage());
                Log.m212e(str8, sb2.toString(), exc);
            }
            if (str6 == null) {
                return false;
            }
        }
        if (str6 == null) {
            str6 = buildFileName(str4, str3);
        }
        final String str9 = str6;
        File file3 = str9.startsWith(File.separator) ? new File(str9) : new File(file2, str9);
        if (!file3.exists()) {
            return false;
        }
        MXOsHandler mXOsHandler = MXMediasCache.mDecryptingHandler;
        final int i2 = i;
        final File file4 = file3;
        final EncryptedFileInfo encryptedFileInfo2 = encryptedFileInfo;
        final Context context2 = context;
        C26343 r1 = new Runnable() {
            /* JADX WARNING: Code restructure failed: missing block: B:48:0x00de, code lost:
                r1 = r0;
             */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0080 A[SYNTHETIC, Splitter:B:28:0x0080] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    int r0 = r2
                    r1 = 0
                    java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.io.File r3 = r3     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r3 = r4     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    if (r3 == 0) goto L_0x0018
                    com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r3 = r4     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.io.InputStream r3 = com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.decryptAttachment(r2, r3)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r2.close()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r2 = r3
                L_0x0018:
                    r3 = 2147483647(0x7fffffff, float:NaN)
                    if (r3 != r0) goto L_0x0029
                    android.content.Context r0 = r5     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.io.File r3 = r3     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    android.net.Uri r3 = android.net.Uri.fromFile(r3)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    int r0 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getRotationAngleForBitmap(r0, r3)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                L_0x0029:
                    if (r2 == 0) goto L_0x0123
                    android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r3.<init>()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r3.inPreferredConfig = r4     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeStream(r2, r1, r3)     // Catch:{ OutOfMemoryError -> 0x0039 }
                    goto L_0x0056
                L_0x0039:
                    r4 = move-exception
                    java.lang.System.gc()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.lang.String r5 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.LOG_TAG     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r6.<init>()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.lang.String r7 = "bitmapForURL() : Out of memory 1 "
                    r6.append(r7)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r6.append(r4)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    java.lang.String r6 = r6.toString()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    com.opengarden.firechat.matrixsdk.util.Log.m212e(r5, r6, r4)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    r4 = r1
                L_0x0056:
                    if (r4 != 0) goto L_0x007d
                    android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeStream(r2, r1, r3)     // Catch:{ OutOfMemoryError -> 0x0064 }
                    goto L_0x007e
                L_0x005d:
                    r0 = move-exception
                    r1 = r4
                    goto L_0x00eb
                L_0x0061:
                    r1 = r4
                    goto L_0x0104
                L_0x0064:
                    r1 = move-exception
                    java.lang.String r3 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.LOG_TAG     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    r5.<init>()     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    java.lang.String r6 = "bitmapForURL() Out of memory 2 "
                    r5.append(r6)     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    r5.append(r1)     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    java.lang.String r5 = r5.toString()     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                    com.opengarden.firechat.matrixsdk.util.Log.m212e(r3, r5, r1)     // Catch:{ FileNotFoundException -> 0x0061, Exception -> 0x005d }
                L_0x007d:
                    r1 = r4
                L_0x007e:
                    if (r1 == 0) goto L_0x00e6
                    java.lang.Object r3 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.sSyncObject     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    monitor-enter(r3)     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    if (r0 == 0) goto L_0x00c5
                    android.graphics.Matrix r10 = new android.graphics.Matrix     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    r10.<init>()     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    float r0 = (float) r0     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    r10.postRotate(r0)     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    r6 = 0
                    r7 = 0
                    int r8 = r1.getWidth()     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    int r9 = r1.getHeight()     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    r11 = 0
                    r5 = r1
                    android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    if (r0 == r1) goto L_0x00c6
                    r1.recycle()     // Catch:{ OutOfMemoryError -> 0x00a8 }
                    goto L_0x00c6
                L_0x00a6:
                    r0 = move-exception
                    goto L_0x00e4
                L_0x00a8:
                    r0 = move-exception
                    java.lang.String r4 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.LOG_TAG     // Catch:{ all -> 0x00a6 }
                    java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a6 }
                    r5.<init>()     // Catch:{ all -> 0x00a6 }
                    java.lang.String r6 = "bitmapForURL rotation error : "
                    r5.append(r6)     // Catch:{ all -> 0x00a6 }
                    java.lang.String r6 = r0.getMessage()     // Catch:{ all -> 0x00a6 }
                    r5.append(r6)     // Catch:{ all -> 0x00a6 }
                    java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00a6 }
                    com.opengarden.firechat.matrixsdk.util.Log.m212e(r4, r5, r0)     // Catch:{ all -> 0x00a6 }
                L_0x00c5:
                    r0 = r1
                L_0x00c6:
                    int r1 = r0.getWidth()     // Catch:{ all -> 0x00e0 }
                    r4 = 1000(0x3e8, float:1.401E-42)
                    if (r1 >= r4) goto L_0x00dd
                    int r1 = r0.getHeight()     // Catch:{ all -> 0x00e0 }
                    if (r1 >= r4) goto L_0x00dd
                    android.support.v4.util.LruCache r1 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.sBitmapByDownloadIdCache     // Catch:{ all -> 0x00e0 }
                    java.lang.String r4 = r6     // Catch:{ all -> 0x00e0 }
                    r1.put(r4, r0)     // Catch:{ all -> 0x00e0 }
                L_0x00dd:
                    monitor-exit(r3)     // Catch:{ all -> 0x00e0 }
                    r1 = r0
                    goto L_0x00e6
                L_0x00e0:
                    r1 = move-exception
                    r12 = r1
                    r1 = r0
                    r0 = r12
                L_0x00e4:
                    monitor-exit(r3)     // Catch:{ all -> 0x00a6 }
                    throw r0     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                L_0x00e6:
                    r2.close()     // Catch:{ FileNotFoundException -> 0x0104, Exception -> 0x00ea }
                    goto L_0x0123
                L_0x00ea:
                    r0 = move-exception
                L_0x00eb:
                    java.lang.String r2 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.LOG_TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "bitmapForURL() "
                    r3.append(r4)
                    r3.append(r0)
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m212e(r2, r3, r0)
                    goto L_0x0123
                L_0x0104:
                    java.lang.String r0 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "bitmapForURL() : "
                    r2.append(r3)
                    java.lang.String r3 = r7
                    r2.append(r3)
                    java.lang.String r3 = " does not exist"
                    r2.append(r3)
                    java.lang.String r2 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
                L_0x0123:
                    android.os.Handler r0 = com.opengarden.firechat.matrixsdk.p007db.MXMediasCache.mUIHandler
                    com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask$3$1 r2 = new com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask$3$1
                    r2.<init>(r1)
                    r0.post(r2)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.C26343.run():void");
            }
        };
        mXOsHandler.post(r1);
        return true;
    }

    public MXMediaDownloadWorkerTask(Context context, HomeServerConnectionConfig homeServerConnectionConfig, NetworkConnectivityReceiver networkConnectivityReceiver, File file, String str, String str2, int i, String str3, EncryptedFileInfo encryptedFileInfo, @Nullable MediaScanRestClient mediaScanRestClient, boolean z) {
        this.mApplicationContext = context;
        this.mHsConfig = homeServerConnectionConfig;
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
        this.mDirectoryFile = file;
        this.mUrl = str;
        this.mDownloadId = str2;
        this.mRotation = i;
        this.mMimeType = str3;
        this.mEncryptedFileInfo = encryptedFileInfo;
        this.mMediaScanRestClient = mediaScanRestClient;
        this.mIsAvScannerEnabled = z;
        this.mImageViewReferences = new ArrayList();
        synchronized (sPendingDownloadById) {
            sPendingDownloadById.put(str2, this);
        }
    }

    public MXMediaDownloadWorkerTask(MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask) {
        this.mApplicationContext = mXMediaDownloadWorkerTask.mApplicationContext;
        this.mHsConfig = mXMediaDownloadWorkerTask.mHsConfig;
        this.mNetworkConnectivityReceiver = mXMediaDownloadWorkerTask.mNetworkConnectivityReceiver;
        this.mDirectoryFile = mXMediaDownloadWorkerTask.mDirectoryFile;
        this.mUrl = mXMediaDownloadWorkerTask.mUrl;
        this.mDownloadId = mXMediaDownloadWorkerTask.mDownloadId;
        this.mRotation = mXMediaDownloadWorkerTask.mRotation;
        this.mMimeType = mXMediaDownloadWorkerTask.mMimeType;
        this.mEncryptedFileInfo = mXMediaDownloadWorkerTask.mEncryptedFileInfo;
        this.mIsAvScannerEnabled = mXMediaDownloadWorkerTask.mIsAvScannerEnabled;
        this.mMediaScanRestClient = mXMediaDownloadWorkerTask.mMediaScanRestClient;
        this.mImageViewReferences = mXMediaDownloadWorkerTask.mImageViewReferences;
        synchronized (sPendingDownloadById) {
            sPendingDownloadById.put(this.mDownloadId, this);
        }
    }

    public synchronized void cancelDownload() {
        this.mIsDownloadCancelled = true;
    }

    public synchronized boolean isDownloadCancelled() {
        return this.mIsDownloadCancelled;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public void addImageView(ImageView imageView) {
        this.mImageViewReferences.add(new WeakReference(imageView));
    }

    public void setDefaultBitmap(Bitmap bitmap) {
        this.mDefaultBitmap = bitmap;
    }

    public void addDownloadListener(IMXMediaDownloadListener iMXMediaDownloadListener) {
        if (iMXMediaDownloadListener != null) {
            this.mDownloadListeners.add(iMXMediaDownloadListener);
        }
    }

    public int getProgress() {
        if (this.mDownloadStats != null) {
            return this.mDownloadStats.mProgress;
        }
        return -1;
    }

    public DownloadStats getDownloadStats() {
        return this.mDownloadStats;
    }

    private boolean isBitmapDownloadTask() {
        return this.mMimeType != null && this.mMimeType.startsWith("image/");
    }

    /* access modifiers changed from: private */
    public void updateAndPublishProgress(long j) {
        this.mDownloadStats.mElapsedTime = (int) ((System.currentTimeMillis() - j) / 1000);
        if (this.mDownloadStats.mFileSize <= 0) {
            this.mDownloadStats.mProgress = -1;
        } else if (this.mDownloadStats.mDownloadedSize >= this.mDownloadStats.mFileSize) {
            this.mDownloadStats.mProgress = 99;
        } else {
            this.mDownloadStats.mProgress = (int) ((((long) this.mDownloadStats.mDownloadedSize) * 100) / ((long) this.mDownloadStats.mFileSize));
        }
        if (System.currentTimeMillis() != j) {
            this.mDownloadStats.mBitRate = (int) (((((long) this.mDownloadStats.mDownloadedSize) * 1000) / (System.currentTimeMillis() - j)) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
        } else {
            this.mDownloadStats.mBitRate = -1;
        }
        if (this.mDownloadStats.mBitRate == 0 || this.mDownloadStats.mFileSize <= 0 || this.mDownloadStats.mFileSize <= this.mDownloadStats.mDownloadedSize) {
            this.mDownloadStats.mEstimatedRemainingTime = -1;
        } else {
            this.mDownloadStats.mEstimatedRemainingTime = ((this.mDownloadStats.mFileSize - this.mDownloadStats.mDownloadedSize) / 1024) / this.mDownloadStats.mBitRate;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("updateAndPublishProgress ");
        sb.append(this);
        sb.append(" : ");
        sb.append(this.mDownloadStats.mProgress);
        Log.m209d(str, sb.toString());
        publishProgress(new Void[0]);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0218 A[SYNTHETIC, Splitter:B:104:0x0218] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0233 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0360 A[SYNTHETIC, Splitter:B:165:0x0360] */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0384  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x03fa  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0406 A[SYNTHETIC, Splitter:B:195:0x0406] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0185 A[SYNTHETIC, Splitter:B:70:0x0185] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01dd A[SYNTHETIC, Splitter:B:88:0x01dd] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.google.gson.JsonElement doInBackground(java.lang.Void... r19) {
        /*
            r18 = this;
            r1 = r18
            com.opengarden.firechat.matrixsdk.rest.model.MatrixError r2 = new com.opengarden.firechat.matrixsdk.rest.model.MatrixError
            r2.<init>()
            java.lang.String r3 = "M_UNKNOWN"
            r2.errcode = r3
            r3 = 0
            r4 = 0
            java.net.URL r5 = new java.net.URL     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r6 = r1.mUrl     // Catch:{ Exception -> 0x03d3 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r6 = LOG_TAG     // Catch:{ Exception -> 0x03d3 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d3 }
            r7.<init>()     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r8 = "MXMediaDownloadWorkerTask "
            r7.append(r8)     // Catch:{ Exception -> 0x03d3 }
            r7.append(r1)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r8 = " starts"
            r7.append(r8)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x03d3 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r6, r7)     // Catch:{ Exception -> 0x03d3 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = new com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats     // Catch:{ Exception -> 0x03d3 }
            r6.<init>()     // Catch:{ Exception -> 0x03d3 }
            r1.mDownloadStats = r6     // Catch:{ Exception -> 0x03d3 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x03d3 }
            r7 = -1
            r6.mEstimatedRemainingTime = r7     // Catch:{ Exception -> 0x03d3 }
            r6 = 1
            java.net.URLConnection r5 = r5.openConnection()     // Catch:{ Exception -> 0x014a }
            java.net.HttpURLConnection r5 = (java.net.HttpURLConnection) r5     // Catch:{ Exception -> 0x014a }
            java.lang.String r8 = com.opengarden.firechat.matrixsdk.RestClient.getUserAgent()     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x0051
            java.lang.String r8 = "User-Agent"
            java.lang.String r9 = com.opengarden.firechat.matrixsdk.RestClient.getUserAgent()     // Catch:{ Exception -> 0x0145 }
            r5.setRequestProperty(r8, r9)     // Catch:{ Exception -> 0x0145 }
        L_0x0051:
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r8 = r1.mHsConfig     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x008f
            boolean r8 = r5 instanceof javax.net.ssl.HttpsURLConnection     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x008f
            r8 = r5
            javax.net.ssl.HttpsURLConnection r8 = (javax.net.ssl.HttpsURLConnection) r8     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r9 = r1.mHsConfig     // Catch:{ Exception -> 0x0073 }
            android.util.Pair r9 = com.opengarden.firechat.matrixsdk.ssl.CertUtil.newPinnedSSLSocketFactory(r9)     // Catch:{ Exception -> 0x0073 }
            java.lang.Object r9 = r9.first     // Catch:{ Exception -> 0x0073 }
            javax.net.ssl.SSLSocketFactory r9 = (javax.net.ssl.SSLSocketFactory) r9     // Catch:{ Exception -> 0x0073 }
            r8.setSSLSocketFactory(r9)     // Catch:{ Exception -> 0x0073 }
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r9 = r1.mHsConfig     // Catch:{ Exception -> 0x0073 }
            javax.net.ssl.HostnameVerifier r9 = com.opengarden.firechat.matrixsdk.ssl.CertUtil.newHostnameVerifier(r9)     // Catch:{ Exception -> 0x0073 }
            r8.setHostnameVerifier(r9)     // Catch:{ Exception -> 0x0073 }
            goto L_0x008f
        L_0x0073:
            r0 = move-exception
            r8 = r0
            java.lang.String r9 = LOG_TAG     // Catch:{ Exception -> 0x0145 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0145 }
            r10.<init>()     // Catch:{ Exception -> 0x0145 }
            java.lang.String r11 = "doInBackground SSL exception "
            r10.append(r11)     // Catch:{ Exception -> 0x0145 }
            java.lang.String r11 = r8.getMessage()     // Catch:{ Exception -> 0x0145 }
            r10.append(r11)     // Catch:{ Exception -> 0x0145 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r9, r10, r8)     // Catch:{ Exception -> 0x0145 }
        L_0x008f:
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r8 = r1.mNetworkConnectivityReceiver     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x009a
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r8 = r1.mNetworkConnectivityReceiver     // Catch:{ Exception -> 0x0145 }
            float r8 = r8.getTimeoutScale()     // Catch:{ Exception -> 0x0145 }
            goto L_0x009c
        L_0x009a:
            r8 = 1065353216(0x3f800000, float:1.0)
        L_0x009c:
            r9 = 1176256512(0x461c4000, float:10000.0)
            float r8 = r8 * r9
            int r8 = (int) r8     // Catch:{ Exception -> 0x0145 }
            r5.setReadTimeout(r8)     // Catch:{ Exception -> 0x0145 }
            boolean r8 = r1.mIsAvScannerEnabled     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x0131
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r8 = r1.mEncryptedFileInfo     // Catch:{ Exception -> 0x0145 }
            if (r8 == 0) goto L_0x0131
            java.lang.String r8 = "POST"
            r5.setRequestMethod(r8)     // Catch:{ Exception -> 0x0145 }
            java.lang.String r8 = "Content-Type"
            java.lang.String r9 = "application/json; charset=UTF-8"
            r5.setRequestProperty(r8, r9)     // Catch:{ Exception -> 0x0145 }
            r5.setDoOutput(r6)     // Catch:{ Exception -> 0x0145 }
            r5.setUseCaches(r4)     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanBody r8 = new com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanBody     // Catch:{ Exception -> 0x0145 }
            r8.<init>()     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r9 = r1.mEncryptedFileInfo     // Catch:{ Exception -> 0x0145 }
            r8.encryptedFileInfo = r9     // Catch:{ Exception -> 0x0145 }
            java.lang.String r8 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getCanonicalizedJsonString(r8)     // Catch:{ Exception -> 0x0145 }
            java.lang.String r9 = r18.getAntivirusServerPublicKey()     // Catch:{ Exception -> 0x0145 }
            if (r9 != 0) goto L_0x00da
            java.lang.Exception r8 = new java.lang.Exception     // Catch:{ Exception -> 0x0145 }
            java.lang.String r9 = "Unable to get public key"
            r8.<init>(r9)     // Catch:{ Exception -> 0x0145 }
            throw r8     // Catch:{ Exception -> 0x0145 }
        L_0x00da:
            boolean r10 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x0145 }
            if (r10 != 0) goto L_0x00fc
            org.matrix.olm.OlmPkEncryption r10 = new org.matrix.olm.OlmPkEncryption     // Catch:{ Exception -> 0x0145 }
            r10.<init>()     // Catch:{ Exception -> 0x0145 }
            r10.setRecipientKey(r9)     // Catch:{ Exception -> 0x0145 }
            org.matrix.olm.OlmPkMessage r8 = r10.encrypt(r8)     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanEncryptedBody r9 = new com.opengarden.firechat.matrixsdk.rest.model.EncryptedMediaScanEncryptedBody     // Catch:{ Exception -> 0x0145 }
            r9.<init>()     // Catch:{ Exception -> 0x0145 }
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedBodyFileInfo r10 = new com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedBodyFileInfo     // Catch:{ Exception -> 0x0145 }
            r10.<init>(r8)     // Catch:{ Exception -> 0x0145 }
            r9.encryptedBodyFileInfo = r10     // Catch:{ Exception -> 0x0145 }
            java.lang.String r8 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getCanonicalizedJsonString(r9)     // Catch:{ Exception -> 0x0145 }
        L_0x00fc:
            java.io.OutputStream r9 = r5.getOutputStream()     // Catch:{ Exception -> 0x0145 }
            java.lang.String r10 = "UTF-8"
            byte[] r8 = r8.getBytes(r10)     // Catch:{ Exception -> 0x0110 }
            r9.write(r8)     // Catch:{ Exception -> 0x0110 }
        L_0x0109:
            r9.close()     // Catch:{ Exception -> 0x0145 }
            goto L_0x0131
        L_0x010d:
            r0 = move-exception
            r8 = r0
            goto L_0x012d
        L_0x0110:
            r0 = move-exception
            r8 = r0
            java.lang.String r10 = LOG_TAG     // Catch:{ all -> 0x010d }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x010d }
            r11.<init>()     // Catch:{ all -> 0x010d }
            java.lang.String r12 = "doInBackground Failed to serialize encryption info "
            r11.append(r12)     // Catch:{ all -> 0x010d }
            java.lang.String r12 = r8.getMessage()     // Catch:{ all -> 0x010d }
            r11.append(r12)     // Catch:{ all -> 0x010d }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x010d }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r10, r11, r8)     // Catch:{ all -> 0x010d }
            goto L_0x0109
        L_0x012d:
            r9.close()     // Catch:{ Exception -> 0x0145 }
            throw r8     // Catch:{ Exception -> 0x0145 }
        L_0x0131:
            int r8 = r5.getContentLength()     // Catch:{ Exception -> 0x0145 }
            java.io.InputStream r9 = r5.getInputStream()     // Catch:{ Exception -> 0x0141 }
            r17 = r5
            r5 = r3
            r3 = r9
            r9 = r17
            goto L_0x01ea
        L_0x0141:
            r0 = move-exception
            r9 = r8
            r8 = r5
            goto L_0x0148
        L_0x0145:
            r0 = move-exception
            r8 = r5
            r9 = -1
        L_0x0148:
            r5 = r0
            goto L_0x014e
        L_0x014a:
            r0 = move-exception
            r5 = r0
            r8 = r3
            r9 = -1
        L_0x014e:
            java.lang.String r10 = LOG_TAG     // Catch:{ Exception -> 0x03d3 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d3 }
            r11.<init>()     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r12 = "bitmapForURL : fail to open the connection "
            r11.append(r12)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r12 = r5.getMessage()     // Catch:{ Exception -> 0x03d3 }
            r11.append(r12)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x03d3 }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r10, r11, r5)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r5 = r5.getLocalizedMessage()     // Catch:{ Exception -> 0x03d3 }
            r2.error = r5     // Catch:{ Exception -> 0x03d3 }
            int r5 = r8.getResponseCode()     // Catch:{ Exception -> 0x03d3 }
            r10 = 403(0x193, float:5.65E-43)
            if (r5 != r10) goto L_0x017f
            com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient r5 = r1.mMediaScanRestClient     // Catch:{ Exception -> 0x03d3 }
            if (r5 == 0) goto L_0x017f
            com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient r5 = r1.mMediaScanRestClient     // Catch:{ Exception -> 0x03d3 }
            r5.resetServerPublicKey()     // Catch:{ Exception -> 0x03d3 }
        L_0x017f:
            java.io.InputStream r5 = r8.getErrorStream()     // Catch:{ Exception -> 0x03d3 }
            if (r5 == 0) goto L_0x01ca
            java.io.BufferedReader r10 = new java.io.BufferedReader     // Catch:{ Exception -> 0x01ae }
            java.io.InputStreamReader r11 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x01ae }
            java.lang.String r12 = "UTF-8"
            r11.<init>(r5, r12)     // Catch:{ Exception -> 0x01ae }
            r10.<init>(r11)     // Catch:{ Exception -> 0x01ae }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ae }
            r5.<init>()     // Catch:{ Exception -> 0x01ae }
        L_0x0196:
            java.lang.String r11 = r10.readLine()     // Catch:{ Exception -> 0x01ae }
            if (r11 == 0) goto L_0x01a0
            r5.append(r11)     // Catch:{ Exception -> 0x01ae }
            goto L_0x0196
        L_0x01a0:
            com.google.gson.JsonParser r10 = new com.google.gson.JsonParser     // Catch:{ Exception -> 0x01ae }
            r10.<init>()     // Catch:{ Exception -> 0x01ae }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x01ae }
            com.google.gson.JsonElement r5 = r10.parse(r5)     // Catch:{ Exception -> 0x01ae }
            goto L_0x01cb
        L_0x01ae:
            r0 = move-exception
            r5 = r0
            java.lang.String r10 = LOG_TAG     // Catch:{ Exception -> 0x03d3 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03d3 }
            r11.<init>()     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r12 = "bitmapForURL : Error parsing error "
            r11.append(r12)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r12 = r5.getMessage()     // Catch:{ Exception -> 0x03d3 }
            r11.append(r12)     // Catch:{ Exception -> 0x03d3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x03d3 }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r10, r11, r5)     // Catch:{ Exception -> 0x03d3 }
        L_0x01ca:
            r5 = r3
        L_0x01cb:
            java.lang.String r10 = LOG_TAG     // Catch:{ Exception -> 0x03cd }
            java.lang.String r11 = "MediaWorkerTask an url does not exist"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r10, r11)     // Catch:{ Exception -> 0x03cd }
            boolean r10 = r1.mIsAvScannerEnabled     // Catch:{ Exception -> 0x03cd }
            if (r10 == 0) goto L_0x01da
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r10 = r1.mEncryptedFileInfo     // Catch:{ Exception -> 0x03cd }
            if (r10 != 0) goto L_0x01e5
        L_0x01da:
            java.util.List<java.lang.String> r10 = sUnreachableUrls     // Catch:{ Exception -> 0x03cd }
            monitor-enter(r10)     // Catch:{ Exception -> 0x03cd }
            java.util.List<java.lang.String> r11 = sUnreachableUrls     // Catch:{ all -> 0x03cf }
            java.lang.String r12 = r1.mUrl     // Catch:{ all -> 0x03cf }
            r11.add(r12)     // Catch:{ all -> 0x03cf }
            monitor-exit(r10)     // Catch:{ all -> 0x03cf }
        L_0x01e5:
            r17 = r9
            r9 = r8
            r8 = r17
        L_0x01ea:
            r18.dispatchDownloadStart()     // Catch:{ Exception -> 0x03cd }
            if (r3 != 0) goto L_0x022b
            if (r5 != 0) goto L_0x022b
            com.google.gson.JsonParser r10 = new com.google.gson.JsonParser     // Catch:{ Exception -> 0x03cd }
            r10.<init>()     // Catch:{ Exception -> 0x03cd }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03cd }
            r11.<init>()     // Catch:{ Exception -> 0x03cd }
            java.lang.String r12 = "Cannot open "
            r11.append(r12)     // Catch:{ Exception -> 0x03cd }
            java.lang.String r12 = r1.mUrl     // Catch:{ Exception -> 0x03cd }
            r11.append(r12)     // Catch:{ Exception -> 0x03cd }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x03cd }
            com.google.gson.JsonElement r10 = r10.parse(r11)     // Catch:{ Exception -> 0x03cd }
            boolean r5 = r1.mIsAvScannerEnabled     // Catch:{ Exception -> 0x0226 }
            if (r5 == 0) goto L_0x0215
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r5 = r1.mEncryptedFileInfo     // Catch:{ Exception -> 0x0226 }
            if (r5 != 0) goto L_0x0220
        L_0x0215:
            java.util.List<java.lang.String> r5 = sUnreachableUrls     // Catch:{ Exception -> 0x0226 }
            monitor-enter(r5)     // Catch:{ Exception -> 0x0226 }
            java.util.List<java.lang.String> r11 = sUnreachableUrls     // Catch:{ all -> 0x0222 }
            java.lang.String r12 = r1.mUrl     // Catch:{ all -> 0x0222 }
            r11.add(r12)     // Catch:{ all -> 0x0222 }
            monitor-exit(r5)     // Catch:{ all -> 0x0222 }
        L_0x0220:
            r5 = r10
            goto L_0x022b
        L_0x0222:
            r0 = move-exception
            r3 = r0
            monitor-exit(r5)     // Catch:{ all -> 0x0222 }
            throw r3     // Catch:{ Exception -> 0x0226 }
        L_0x0226:
            r0 = move-exception
            r3 = r0
            r5 = r10
            goto L_0x03d6
        L_0x022b:
            boolean r10 = r18.isDownloadCancelled()     // Catch:{ Exception -> 0x03cd }
            r11 = 100
            if (r10 != 0) goto L_0x0358
            if (r5 != 0) goto L_0x0358
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0353 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0353 }
            r10.<init>()     // Catch:{ Exception -> 0x0353 }
            java.lang.String r14 = r1.mDownloadId     // Catch:{ Exception -> 0x0353 }
            java.lang.String r15 = r1.mMimeType     // Catch:{ Exception -> 0x0353 }
            java.lang.String r14 = buildFileName(r14, r15)     // Catch:{ Exception -> 0x0353 }
            r10.append(r14)     // Catch:{ Exception -> 0x0353 }
            java.lang.String r14 = ".tmp"
            r10.append(r14)     // Catch:{ Exception -> 0x0353 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0353 }
            java.io.FileOutputStream r14 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0353 }
            java.io.File r15 = new java.io.File     // Catch:{ Exception -> 0x0353 }
            java.io.File r6 = r1.mDirectoryFile     // Catch:{ Exception -> 0x0353 }
            r15.<init>(r6, r10)     // Catch:{ Exception -> 0x0353 }
            r14.<init>(r15)     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            java.lang.String r15 = r1.mDownloadId     // Catch:{ Exception -> 0x0353 }
            r6.mDownloadId = r15     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mProgress = r4     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mDownloadedSize = r4     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mFileSize = r8     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mElapsedTime = r4     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mEstimatedRemainingTime = r7     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r6 = r1.mDownloadStats     // Catch:{ Exception -> 0x0353 }
            r6.mBitRate = r4     // Catch:{ Exception -> 0x0353 }
            java.util.Timer r6 = new java.util.Timer     // Catch:{ Exception -> 0x0353 }
            r6.<init>()     // Catch:{ Exception -> 0x0353 }
            com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask$4 r8 = new com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask$4     // Catch:{ Exception -> 0x0353 }
            r8.<init>(r12)     // Catch:{ Exception -> 0x0353 }
            java.util.Date r12 = new java.util.Date     // Catch:{ Exception -> 0x0353 }
            r12.<init>()     // Catch:{ Exception -> 0x0353 }
            r16 = r5
            r4 = 100
            r6.scheduleAtFixedRate(r8, r12, r4)     // Catch:{ Exception -> 0x037f }
            r4 = 32768(0x8000, float:4.5918E-41)
            byte[] r4 = new byte[r4]     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
        L_0x0297:
            boolean r5 = r18.isDownloadCancelled()     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            if (r5 != 0) goto L_0x02af
            int r5 = r3.read(r4)     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            if (r5 == r7) goto L_0x02af
            r8 = 0
            r14.write(r4, r8, r5)     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r8 = r1.mDownloadStats     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            int r12 = r8.mDownloadedSize     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            int r12 = r12 + r5
            r8.mDownloadedSize = r12     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            goto L_0x0297
        L_0x02af:
            boolean r4 = r18.isDownloadCancelled()     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            if (r4 != 0) goto L_0x02b9
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r4 = r1.mDownloadStats     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
            r4.mProgress = r11     // Catch:{ OutOfMemoryError -> 0x02de, Exception -> 0x02bb }
        L_0x02b9:
            r4 = 1
            goto L_0x02ee
        L_0x02bb:
            r0 = move-exception
            r4 = r0
            java.lang.String r5 = LOG_TAG     // Catch:{ Exception -> 0x037f }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x037f }
            r7.<init>()     // Catch:{ Exception -> 0x037f }
            java.lang.String r8 = "doInBackground fail to read image "
            r7.append(r8)     // Catch:{ Exception -> 0x037f }
            java.lang.String r8 = r4.getMessage()     // Catch:{ Exception -> 0x037f }
            r7.append(r8)     // Catch:{ Exception -> 0x037f }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x037f }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r5, r7, r4)     // Catch:{ Exception -> 0x037f }
            java.lang.String r4 = r4.getLocalizedMessage()     // Catch:{ Exception -> 0x037f }
            r2.error = r4     // Catch:{ Exception -> 0x037f }
            goto L_0x02b9
        L_0x02de:
            r0 = move-exception
            r4 = r0
            java.lang.String r5 = LOG_TAG     // Catch:{ Exception -> 0x037f }
            java.lang.String r7 = "doInBackground: out of memory"
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r5, r7, r4)     // Catch:{ Exception -> 0x037f }
            java.lang.String r4 = r4.getLocalizedMessage()     // Catch:{ Exception -> 0x037f }
            r2.error = r4     // Catch:{ Exception -> 0x037f }
            goto L_0x02b9
        L_0x02ee:
            r1.mIsDone = r4     // Catch:{ Exception -> 0x037f }
            r1.close(r3)     // Catch:{ Exception -> 0x037f }
            r14.flush()     // Catch:{ Exception -> 0x037f }
            r14.close()     // Catch:{ Exception -> 0x037f }
            r6.cancel()     // Catch:{ Exception -> 0x037f }
            if (r9 == 0) goto L_0x0305
            boolean r3 = r9 instanceof javax.net.ssl.HttpsURLConnection     // Catch:{ Exception -> 0x037f }
            if (r3 == 0) goto L_0x0305
            r9.disconnect()     // Catch:{ Exception -> 0x037f }
        L_0x0305:
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r3 = r1.mDownloadStats     // Catch:{ Exception -> 0x037f }
            int r3 = r3.mProgress     // Catch:{ Exception -> 0x037f }
            if (r3 != r11) goto L_0x035a
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x0330 }
            java.io.File r4 = r1.mDirectoryFile     // Catch:{ Exception -> 0x0330 }
            r3.<init>(r4, r10)     // Catch:{ Exception -> 0x0330 }
            java.lang.String r4 = r1.mDownloadId     // Catch:{ Exception -> 0x0330 }
            java.lang.String r5 = r1.mMimeType     // Catch:{ Exception -> 0x0330 }
            java.lang.String r4 = buildFileName(r4, r5)     // Catch:{ Exception -> 0x0330 }
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x0330 }
            java.io.File r6 = r1.mDirectoryFile     // Catch:{ Exception -> 0x0330 }
            r5.<init>(r6, r4)     // Catch:{ Exception -> 0x0330 }
            boolean r6 = r5.exists()     // Catch:{ Exception -> 0x0330 }
            if (r6 == 0) goto L_0x032c
            android.content.Context r6 = r1.mApplicationContext     // Catch:{ Exception -> 0x0330 }
            r6.deleteFile(r4)     // Catch:{ Exception -> 0x0330 }
        L_0x032c:
            r3.renameTo(r5)     // Catch:{ Exception -> 0x0330 }
            goto L_0x035a
        L_0x0330:
            r0 = move-exception
            r3 = r0
            java.lang.String r4 = LOG_TAG     // Catch:{ Exception -> 0x037f }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x037f }
            r5.<init>()     // Catch:{ Exception -> 0x037f }
            java.lang.String r6 = "doInBackground : renaming error "
            r5.append(r6)     // Catch:{ Exception -> 0x037f }
            java.lang.String r6 = r3.getMessage()     // Catch:{ Exception -> 0x037f }
            r5.append(r6)     // Catch:{ Exception -> 0x037f }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x037f }
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r4, r5, r3)     // Catch:{ Exception -> 0x037f }
            java.lang.String r3 = r3.getLocalizedMessage()     // Catch:{ Exception -> 0x037f }
            r2.error = r3     // Catch:{ Exception -> 0x037f }
            goto L_0x035a
        L_0x0353:
            r0 = move-exception
            r16 = r5
            goto L_0x03d5
        L_0x0358:
            r16 = r5
        L_0x035a:
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r3 = r1.mDownloadStats     // Catch:{ Exception -> 0x03c9 }
            int r3 = r3.mProgress     // Catch:{ Exception -> 0x03c9 }
            if (r3 != r11) goto L_0x0384
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x037f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x037f }
            r4.<init>()     // Catch:{ Exception -> 0x037f }
            java.lang.String r5 = "The download "
            r4.append(r5)     // Catch:{ Exception -> 0x037f }
            r4.append(r1)     // Catch:{ Exception -> 0x037f }
            java.lang.String r5 = " is done."
            r4.append(r5)     // Catch:{ Exception -> 0x037f }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x037f }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)     // Catch:{ Exception -> 0x037f }
            r5 = r16
            goto L_0x03f2
        L_0x037f:
            r0 = move-exception
            r3 = r0
            r5 = r16
            goto L_0x03d6
        L_0x0384:
            if (r16 == 0) goto L_0x03ab
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x03c9 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03c9 }
            r4.<init>()     // Catch:{ Exception -> 0x03c9 }
            java.lang.String r5 = "The download "
            r4.append(r5)     // Catch:{ Exception -> 0x03c9 }
            r4.append(r1)     // Catch:{ Exception -> 0x03c9 }
            java.lang.String r5 = " failed : mErrorAsJsonElement "
            r4.append(r5)     // Catch:{ Exception -> 0x03c9 }
            r5 = r16
            java.lang.String r6 = r5.toString()     // Catch:{ Exception -> 0x03cd }
            r4.append(r6)     // Catch:{ Exception -> 0x03cd }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03cd }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)     // Catch:{ Exception -> 0x03cd }
            goto L_0x03f2
        L_0x03ab:
            r5 = r16
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x03cd }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03cd }
            r4.<init>()     // Catch:{ Exception -> 0x03cd }
            java.lang.String r6 = "The download "
            r4.append(r6)     // Catch:{ Exception -> 0x03cd }
            r4.append(r1)     // Catch:{ Exception -> 0x03cd }
            java.lang.String r6 = " failed."
            r4.append(r6)     // Catch:{ Exception -> 0x03cd }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x03cd }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)     // Catch:{ Exception -> 0x03cd }
            goto L_0x03f2
        L_0x03c9:
            r0 = move-exception
            r5 = r16
            goto L_0x03d5
        L_0x03cd:
            r0 = move-exception
            goto L_0x03d5
        L_0x03cf:
            r0 = move-exception
            r3 = r0
            monitor-exit(r10)     // Catch:{ all -> 0x03cf }
            throw r3     // Catch:{ Exception -> 0x03cd }
        L_0x03d3:
            r0 = move-exception
            r5 = r3
        L_0x03d5:
            r3 = r0
        L_0x03d6:
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Unable to download media "
            r6.append(r7)
            r6.append(r1)
            java.lang.String r6 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m212e(r4, r6, r3)
            java.lang.String r3 = r3.getMessage()
            r2.error = r3
        L_0x03f2:
            java.lang.String r3 = r2.error
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0403
            r3 = 0
            com.google.gson.Gson r3 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getGson(r3)
            com.google.gson.JsonElement r5 = r3.toJsonTree(r2)
        L_0x0403:
            java.util.Map<java.lang.String, com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask> r2 = sPendingDownloadById
            monitor-enter(r2)
            java.util.Map<java.lang.String, com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask> r3 = sPendingDownloadById     // Catch:{ all -> 0x040f }
            java.lang.String r4 = r1.mDownloadId     // Catch:{ all -> 0x040f }
            r3.remove(r4)     // Catch:{ all -> 0x040f }
            monitor-exit(r2)     // Catch:{ all -> 0x040f }
            return r5
        L_0x040f:
            r0 = move-exception
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x040f }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.doInBackground(java.lang.Void[]):com.google.gson.JsonElement");
    }

    @Nullable
    private String getAntivirusServerPublicKey() {
        if (this.mMediaScanRestClient == null) {
            Log.m211e(LOG_TAG, "Mandatory mMediaScanRestClient is null");
            return null;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String[] strArr = new String[1];
        this.mMediaScanRestClient.getServerPublicKey(new ApiCallback<String>() {
            public void onSuccess(String str) {
                strArr[0] = str;
                countDownLatch.countDown();
            }

            public void onNetworkError(Exception exc) {
                countDownLatch.countDown();
            }

            public void onMatrixError(MatrixError matrixError) {
                countDownLatch.countDown();
            }

            public void onUnexpectedError(Exception exc) {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException unused) {
        }
        return strArr[0];
    }

    private void close(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("close error ");
            sb.append(e.getMessage());
            Log.m212e(str, sb.toString(), e);
        }
    }

    /* access modifiers changed from: protected */
    public void onProgressUpdate(Void... voidArr) {
        super.onProgressUpdate(new Void[0]);
        dispatchOnDownloadProgress(this.mDownloadStats);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(JsonElement jsonElement) {
        if (jsonElement != null) {
            dispatchOnDownloadError(jsonElement);
        } else if (isDownloadCancelled()) {
            dispatchDownloadCancel();
        } else {
            dispatchOnDownloadComplete();
            if (isBitmapDownloadTask() && !bitmapForURL(this.mApplicationContext, this.mDirectoryFile, this.mUrl, this.mDownloadId, this.mRotation, this.mMimeType, this.mEncryptedFileInfo, new SimpleApiCallback<Bitmap>() {
                public void onSuccess(Bitmap bitmap) {
                    MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask = MXMediaDownloadWorkerTask.this;
                    if (bitmap == null) {
                        bitmap = MXMediaDownloadWorkerTask.this.mDefaultBitmap;
                    }
                    mXMediaDownloadWorkerTask.setBitmap(bitmap);
                }
            })) {
                setBitmap(this.mDefaultBitmap);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            for (WeakReference weakReference : this.mImageViewReferences) {
                ImageView imageView = (ImageView) weakReference.get();
                if (imageView != null && TextUtils.equals(this.mDownloadId, (String) imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void dispatchDownloadStart() {
        for (IMXMediaDownloadListener onDownloadStart : this.mDownloadListeners) {
            try {
                onDownloadStart.onDownloadStart(this.mDownloadId);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchDownloadStart error ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
    }

    private void dispatchOnDownloadProgress(DownloadStats downloadStats) {
        for (IMXMediaDownloadListener onDownloadProgress : this.mDownloadListeners) {
            try {
                onDownloadProgress.onDownloadProgress(this.mDownloadId, downloadStats);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchOnDownloadProgress error ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
    }

    private void dispatchOnDownloadError(JsonElement jsonElement) {
        for (IMXMediaDownloadListener onDownloadError : this.mDownloadListeners) {
            try {
                onDownloadError.onDownloadError(this.mDownloadId, jsonElement);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchOnDownloadError error ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
    }

    private void dispatchOnDownloadComplete() {
        for (IMXMediaDownloadListener onDownloadComplete : this.mDownloadListeners) {
            try {
                onDownloadComplete.onDownloadComplete(this.mDownloadId);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchOnDownloadComplete error ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
    }

    private void dispatchDownloadCancel() {
        for (IMXMediaDownloadListener onDownloadCancel : this.mDownloadListeners) {
            try {
                onDownloadCancel.onDownloadCancel(this.mDownloadId);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchDownloadCancel error ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
    }
}
