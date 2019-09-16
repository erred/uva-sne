package com.opengarden.firechat.matrixsdk.p007db;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import com.facebook.common.util.UriUtil;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener.DownloadStats;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaUploadListener.UploadStats;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.util.ContentManager;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.MXOsHandler;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

/* renamed from: com.opengarden.firechat.matrixsdk.db.MXMediasCache */
public class MXMediasCache {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXMediasCache";
    private static final String MXMEDIA_STORE_FOLDER = "MXMediaStore3";
    private static final String MXMEDIA_STORE_IMAGES_FOLDER = "Images";
    private static final String MXMEDIA_STORE_MEMBER_THUMBNAILS_FOLDER = "MXMemberThumbnailsStore";
    private static final String MXMEDIA_STORE_OTHERS_FOLDER = "Others";
    private static final String MXMEDIA_STORE_SHARE_FOLDER = "share";
    private static final String MXMEDIA_STORE_TMP_FOLDER = "tmp";
    static MXOsHandler mDecryptingHandler;
    static HandlerThread mDecryptingHandlerThread;
    private static Bitmap mDefaultBitmap;
    static Handler mUIHandler;
    private static final List<String> sPreviousMediaCacheFolders = Arrays.asList(new String[]{"MXMediaStore", "MXMediaStore2"});
    private ContentManager mContentManager;
    private File mImagesFolderFile;
    private MediaScanRestClient mMediaScanRestClient;
    private File mMediasFolderFile;
    private final NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private File mOthersFolderFile;
    private File mShareFolderFile;
    private final List<MXMediaDownloadWorkerTask> mSuspendedTasks = new ArrayList();
    private File mThumbnailsFolderFile;
    /* access modifiers changed from: private */
    public File mTmpFolderFile;

    public MXMediasCache(ContentManager contentManager, NetworkConnectivityReceiver networkConnectivityReceiver, String str, Context context) {
        this.mContentManager = contentManager;
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
        for (String file : sPreviousMediaCacheFolders) {
            File file2 = new File(context.getApplicationContext().getFilesDir(), file);
            if (file2.exists()) {
                ContentUtils.deleteDirectory(file2);
            }
        }
        File file3 = new File(context.getApplicationContext().getFilesDir(), MXMEDIA_STORE_FOLDER);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        this.mMediasFolderFile = new File(file3, str);
        this.mImagesFolderFile = new File(this.mMediasFolderFile, MXMEDIA_STORE_IMAGES_FOLDER);
        this.mOthersFolderFile = new File(this.mMediasFolderFile, MXMEDIA_STORE_OTHERS_FOLDER);
        this.mTmpFolderFile = new File(this.mMediasFolderFile, MXMEDIA_STORE_TMP_FOLDER);
        if (this.mTmpFolderFile.exists()) {
            ContentUtils.deleteDirectory(this.mTmpFolderFile);
        }
        this.mTmpFolderFile.mkdirs();
        this.mShareFolderFile = new File(this.mMediasFolderFile, "share");
        if (this.mShareFolderFile.exists()) {
            ContentUtils.deleteDirectory(this.mShareFolderFile);
        }
        this.mShareFolderFile.mkdirs();
        this.mThumbnailsFolderFile = new File(file3, MXMEDIA_STORE_MEMBER_THUMBNAILS_FOLDER);
        if (mDecryptingHandlerThread == null) {
            mDecryptingHandlerThread = new HandlerThread("MXMediaDecryptingBackgroundThread", 1);
            mDecryptingHandlerThread.start();
            mDecryptingHandler = new MXOsHandler(mDecryptingHandlerThread.getLooper());
            mUIHandler = new Handler(Looper.getMainLooper());
        }
    }

    private File getMediasFolderFile() {
        if (!this.mMediasFolderFile.exists()) {
            this.mMediasFolderFile.mkdirs();
        }
        return this.mMediasFolderFile;
    }

    private File getFolderFile(String str) {
        File file;
        if (str == null || str.startsWith("image/")) {
            file = this.mImagesFolderFile;
        } else {
            file = this.mOthersFolderFile;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private File getThumbnailsFolderFile() {
        if (!this.mThumbnailsFolderFile.exists()) {
            this.mThumbnailsFolderFile.mkdirs();
        }
        return this.mThumbnailsFolderFile;
    }

    public static void getCachesSize(final Context context, final ApiCallback<Long> apiCallback) {
        C26411 r0 = new AsyncTask<Void, Void, Long>() {
            /* access modifiers changed from: protected */
            public Long doInBackground(Void... voidArr) {
                return Long.valueOf(ContentUtils.getDirectorySize(context, new File(context.getApplicationContext().getFilesDir(), MXMediasCache.MXMEDIA_STORE_FOLDER), 1));
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Long l) {
                String access$000 = MXMediasCache.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getCachesSize() : ");
                sb.append(l);
                Log.m209d(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onSuccess(l);
                }
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getCachesSize() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            r0.cancel(true);
        }
    }

    public long removeMediasBefore(long j, Set<String> set) {
        return removeMediasBefore(getMediasFolderFile(), j, set) + 0 + removeMediasBefore(getThumbnailsFolderFile(), j, set);
    }

    private long removeMediasBefore(File file, long j, Set<String> set) {
        File[] listFiles = file.listFiles();
        long j2 = 0;
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    j2 += removeMediasBefore(file2, j, set);
                } else if (!set.contains(file2.getPath()) && ContentUtils.getLastAccessTime(file2) < j) {
                    long length = j2 + file2.length();
                    file2.delete();
                    j2 = length;
                }
            }
        }
        return j2;
    }

    public void clear() {
        ContentUtils.deleteDirectory(getMediasFolderFile());
        ContentUtils.deleteDirectory(this.mThumbnailsFolderFile);
        MXMediaDownloadWorkerTask.clearBitmapsCache();
        MXMediaUploadWorkerTask.cancelPendingUploads();
    }

    public static void clearThumbnailsCache(Context context) {
        ContentUtils.deleteDirectory(new File(new File(context.getApplicationContext().getFilesDir(), MXMEDIA_STORE_FOLDER), MXMEDIA_STORE_MEMBER_THUMBNAILS_FOLDER));
    }

    @Nullable
    public File thumbnailCacheFile(String str, int i) {
        String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str);
        if (downloadTaskIdForMatrixMediaContent != null) {
            if (i > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(downloadTaskIdForMatrixMediaContent);
                sb.append("_w_");
                sb.append(i);
                sb.append("_h_");
                sb.append(i);
                downloadTaskIdForMatrixMediaContent = sb.toString();
            }
            try {
                File file = new File(getThumbnailsFolderFile(), MXMediaDownloadWorkerTask.buildFileName(downloadTaskIdForMatrixMediaContent, ResourceUtils.MIME_TYPE_JPEG));
                if (file.exists()) {
                    return file;
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("thumbnailCacheFile failed ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
        return null;
    }

    @Nullable
    private File mediaCacheFile(String str, int i, int i2, String str2) {
        if (str == null) {
            return null;
        }
        if (!str.startsWith("file:")) {
            String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str);
            if (downloadTaskIdForMatrixMediaContent == null) {
                return null;
            }
            if (i > 0 && i2 > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(downloadTaskIdForMatrixMediaContent);
                sb.append("_w_");
                sb.append(i);
                sb.append("_h_");
                sb.append(i2);
                downloadTaskIdForMatrixMediaContent = sb.toString();
            }
            str = MXMediaDownloadWorkerTask.buildFileName(downloadTaskIdForMatrixMediaContent, str2);
        }
        try {
            if (str.startsWith("file:")) {
                str = Uri.parse(str).getLastPathSegment();
            }
            File file = new File(getFolderFile(str2), str);
            if (file.exists()) {
                return file;
            }
            return null;
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("mediaCacheFile failed ");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
        }
    }

    public boolean isMediaCached(String str, String str2) {
        return isMediaCached(str, -1, -1, str2);
    }

    public boolean isMediaCached(String str, int i, int i2, String str2) {
        return mediaCacheFile(str, i, i2, str2) != null;
    }

    public boolean createTmpMediaFile(String str, String str2, EncryptedFileInfo encryptedFileInfo, ApiCallback<File> apiCallback) {
        return createTmpDecryptedMediaFile(str, -1, -1, str2, encryptedFileInfo, apiCallback);
    }

    public boolean createTmpDecryptedMediaFile(String str, int i, int i2, String str2, final EncryptedFileInfo encryptedFileInfo, final ApiCallback<File> apiCallback) {
        final File mediaCacheFile = mediaCacheFile(str, i, i2, str2);
        if (mediaCacheFile != null) {
            mDecryptingHandler.post(new Runnable() {
                public void run() {
                    final File file = new File(MXMediasCache.this.mTmpFolderFile, mediaCacheFile.getName());
                    if (!file.exists()) {
                        try {
                            InputStream fileInputStream = new FileInputStream(mediaCacheFile);
                            if (encryptedFileInfo != null) {
                                InputStream decryptAttachment = MXEncryptedAttachments.decryptAttachment(fileInputStream, encryptedFileInfo);
                                fileInputStream.close();
                                fileInputStream = decryptAttachment;
                            }
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            byte[] bArr = new byte[2048];
                            while (true) {
                                int read = fileInputStream.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                            fileInputStream.close();
                            fileOutputStream.close();
                        } catch (Exception e) {
                            String access$000 = MXMediasCache.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## createTmpDecryptedMediaFile() failed ");
                            sb.append(e.getMessage());
                            Log.m212e(access$000, sb.toString(), e);
                        }
                    }
                    MXMediasCache.mUIHandler.post(new Runnable() {
                        public void run() {
                            apiCallback.onSuccess(file);
                        }
                    });
                }
            });
        }
        return mediaCacheFile != null;
    }

    public void clearTmpDecryptedMediaCache() {
        Log.m209d(LOG_TAG, "clearTmpDecryptedMediaCache()");
        if (this.mTmpFolderFile.exists()) {
            ContentUtils.deleteDirectory(this.mTmpFolderFile);
        }
        if (!this.mTmpFolderFile.exists()) {
            this.mTmpFolderFile.mkdirs();
        }
    }

    public File moveToShareFolder(File file, String str) {
        File file2 = new File(this.mShareFolderFile, str);
        if (file2.exists() && !file2.delete()) {
            Log.m217w(LOG_TAG, "Unable to delete file");
        }
        if (file.renameTo(file2)) {
            return file2;
        }
        Log.m217w(LOG_TAG, "Unable to rename file");
        return file;
    }

    public void clearShareDecryptedMediaCache() {
        Log.m209d(LOG_TAG, "clearShareDecryptedMediaCache()");
        if (this.mShareFolderFile.exists()) {
            ContentUtils.deleteDirectory(this.mShareFolderFile);
        }
        if (!this.mShareFolderFile.exists()) {
            this.mShareFolderFile.mkdirs();
        }
    }

    public String saveBitmap(Bitmap bitmap, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(UriUtil.LOCAL_FILE_SCHEME);
        sb.append(System.currentTimeMillis());
        sb.append(".jpg");
        String sb2 = sb.toString();
        if (str != null) {
            try {
                File file = new File(getFolderFile(null), str);
                file.delete();
                sb2 = Uri.fromFile(file).getLastPathSegment();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("saveBitmap failed ");
                sb3.append(e.getMessage());
                Log.m211e(str2, sb3.toString());
                return null;
            }
        }
        File file2 = new File(getFolderFile(null), sb2);
        FileOutputStream fileOutputStream = new FileOutputStream(file2.getPath());
        if (bitmap.isRecycled()) {
            Log.m217w(LOG_TAG, "Trying to compress a recycled Bitmap. Create a copy first.");
            bitmap = Bitmap.createBitmap(bitmap);
        }
        bitmap.compress(CompressFormat.JPEG, 100, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        return Uri.fromFile(file2).toString();
    }

    public String saveMedia(InputStream inputStream, String str, String str2) {
        if (str == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(UriUtil.LOCAL_FILE_SCHEME);
            sb.append(System.currentTimeMillis());
            str = sb.toString();
            if (str2 != null) {
                String extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(str2);
                if (extensionFromMimeType == null && str2.lastIndexOf("/") >= 0) {
                    extensionFromMimeType = str2.substring(str2.lastIndexOf("/") + 1);
                }
                if (!TextUtils.isEmpty(extensionFromMimeType)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(".");
                    sb2.append(extensionFromMimeType);
                    str = sb2.toString();
                }
            }
        }
        try {
            File file = new File(getFolderFile(str2), str);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
            try {
                byte[] bArr = new byte[32768];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("saveMedia failed ");
                sb3.append(e.getMessage());
                Log.m211e(str3, sb3.toString());
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            return Uri.fromFile(file).toString();
        } catch (Exception e2) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("saveMedia failed ");
            sb4.append(e2.getMessage());
            Log.m211e(str4, sb4.toString());
            return null;
        }
    }

    public void saveFileMediaForUrl(String str, String str2, String str3) {
        saveFileMediaForUrl(str, str2, -1, -1, str3);
    }

    public void saveFileMediaForUrl(String str, String str2, int i, int i2, String str3) {
        saveFileMediaForUrl(str, str2, i, i2, str3, false);
    }

    public void saveFileMediaForUrl(String str, String str2, int i, int i2, String str3, boolean z) {
        String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str);
        if (downloadTaskIdForMatrixMediaContent != null) {
            if (i > 0 && i2 > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(downloadTaskIdForMatrixMediaContent);
                sb.append("_w_");
                sb.append(i);
                sb.append("_h_");
                sb.append(i2);
                downloadTaskIdForMatrixMediaContent = sb.toString();
            }
            try {
                File file = new File(getFolderFile(str3), MXMediaDownloadWorkerTask.buildFileName(downloadTaskIdForMatrixMediaContent, str3));
                if (file.exists()) {
                    try {
                        file.delete();
                    } catch (Exception e) {
                        String str4 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("saveFileMediaForUrl delete failed ");
                        sb2.append(e.getMessage());
                        Log.m211e(str4, sb2.toString());
                    }
                }
                File file2 = new File(Uri.parse(str2).getPath());
                if (z) {
                    FileInputStream fileInputStream = new FileInputStream(file2);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int read = fileInputStream.read(bArr);
                        if (read > 0) {
                            fileOutputStream.write(bArr, 0, read);
                        } else {
                            fileInputStream.close();
                            fileOutputStream.close();
                            return;
                        }
                    }
                } else {
                    file2.renameTo(file);
                }
            } catch (Exception e2) {
                String str5 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("saveFileMediaForUrl failed ");
                sb3.append(e2.getMessage());
                Log.m211e(str5, sb3.toString());
            }
        }
    }

    public String loadAvatarThumbnail(HomeServerConnectionConfig homeServerConnectionConfig, ImageView imageView, String str, int i) {
        return loadBitmap(imageView.getContext(), homeServerConnectionConfig, imageView, str, i, i, 0, 0, null, getThumbnailsFolderFile(), null);
    }

    public String loadAvatarThumbnail(HomeServerConnectionConfig homeServerConnectionConfig, ImageView imageView, String str, int i, Bitmap bitmap) {
        return loadBitmap(imageView.getContext(), homeServerConnectionConfig, imageView, str, i, i, 0, 0, null, getThumbnailsFolderFile(), bitmap, null);
    }

    public boolean isAvatarThumbnailCached(String str, int i) {
        String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str);
        if (downloadTaskIdForMatrixMediaContent == null) {
            return false;
        }
        if (i > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(downloadTaskIdForMatrixMediaContent);
            sb.append("_w_");
            sb.append(i);
            sb.append("_h_");
            sb.append(i);
            downloadTaskIdForMatrixMediaContent = sb.toString();
        }
        boolean isMediaCached = MXMediaDownloadWorkerTask.isMediaCached(downloadTaskIdForMatrixMediaContent);
        if (isMediaCached) {
            return isMediaCached;
        }
        try {
            return new File(getThumbnailsFolderFile(), MXMediaDownloadWorkerTask.buildFileName(downloadTaskIdForMatrixMediaContent, ResourceUtils.MIME_TYPE_JPEG)).exists();
        } catch (Throwable th) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## isAvatarThumbnailCached() : failed ");
            sb2.append(th.getMessage());
            Log.m211e(str2, sb2.toString());
            return isMediaCached;
        }
    }

    public static boolean isMediaUrlUnreachable(String str) {
        return MXMediaDownloadWorkerTask.isMediaUrlUnreachable(str);
    }

    public String loadBitmap(HomeServerConnectionConfig homeServerConnectionConfig, ImageView imageView, String str, int i, int i2, String str2, EncryptedFileInfo encryptedFileInfo) {
        return loadBitmap(homeServerConnectionConfig, imageView, str, -1, -1, i, i2, str2, encryptedFileInfo);
    }

    public String loadBitmap(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, int i, int i2, String str2, EncryptedFileInfo encryptedFileInfo) {
        String str3 = str2;
        return loadBitmap(context, homeServerConnectionConfig, null, str, -1, -1, i, i2, str3, getFolderFile(str3), encryptedFileInfo);
    }

    public String loadBitmap(HomeServerConnectionConfig homeServerConnectionConfig, ImageView imageView, String str, int i, int i2, int i3, int i4, String str2, EncryptedFileInfo encryptedFileInfo) {
        String str3 = str2;
        return loadBitmap(imageView.getContext(), homeServerConnectionConfig, imageView, str, i, i2, i3, i4, str3, getFolderFile(str3), encryptedFileInfo);
    }

    @Nullable
    public String downloadIdFromUrl(String str) {
        String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str);
        if (downloadTaskIdForMatrixMediaContent == null || MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(downloadTaskIdForMatrixMediaContent) == null) {
            return null;
        }
        return downloadTaskIdForMatrixMediaContent;
    }

    public String downloadMedia(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, EncryptedFileInfo encryptedFileInfo) {
        return downloadMedia(context, homeServerConnectionConfig, str, str2, encryptedFileInfo, null);
    }

    public String downloadMedia(Context context, HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, EncryptedFileInfo encryptedFileInfo, IMXMediaDownloadListener iMXMediaDownloadListener) {
        String str3 = str;
        String str4 = str2;
        IMXMediaDownloadListener iMXMediaDownloadListener2 = iMXMediaDownloadListener;
        if (str4 == null || context == null) {
            return null;
        }
        String downloadTaskIdForMatrixMediaContent = this.mContentManager.downloadTaskIdForMatrixMediaContent(str3);
        if (downloadTaskIdForMatrixMediaContent == null || isMediaCached(str3, str4)) {
            return null;
        }
        MXMediaDownloadWorkerTask mediaDownloadWorkerTask = MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(downloadTaskIdForMatrixMediaContent);
        if (mediaDownloadWorkerTask != null) {
            mediaDownloadWorkerTask.addDownloadListener(iMXMediaDownloadListener2);
            return downloadTaskIdForMatrixMediaContent;
        }
        String downloadableUrl = this.mContentManager.getDownloadableUrl(str3, encryptedFileInfo != null);
        String str5 = downloadTaskIdForMatrixMediaContent;
        String str6 = downloadTaskIdForMatrixMediaContent;
        MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask = r2;
        MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask2 = new MXMediaDownloadWorkerTask(context, homeServerConnectionConfig, this.mNetworkConnectivityReceiver, getFolderFile(str4), downloadableUrl, str5, 0, str4, encryptedFileInfo, this.mMediaScanRestClient, this.mContentManager.isAvScannerEnabled());
        mXMediaDownloadWorkerTask.addDownloadListener(iMXMediaDownloadListener2);
        try {
            mXMediaDownloadWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (RejectedExecutionException unused) {
            synchronized (this.mSuspendedTasks) {
                mXMediaDownloadWorkerTask.cancel(true);
                this.mSuspendedTasks.add(new MXMediaDownloadWorkerTask(mXMediaDownloadWorkerTask));
                Log.m211e(LOG_TAG, "Suspend the task ");
            }
        } catch (Exception e) {
            Exception exc = e;
            String str7 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("downloadMedia failed ");
            sb.append(exc.getMessage());
            Log.m211e(str7, sb.toString());
            synchronized (this.mSuspendedTasks) {
                mXMediaDownloadWorkerTask.cancel(true);
            }
        } catch (Throwable th) {
            throw th;
        }
        return str6;
    }

    /* access modifiers changed from: private */
    public void launchSuspendedTask() {
        synchronized (this.mSuspendedTasks) {
            if (!this.mSuspendedTasks.isEmpty()) {
                MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask = (MXMediaDownloadWorkerTask) this.mSuspendedTasks.get(0);
                Log.m209d(LOG_TAG, "Restart a task ");
                try {
                    mXMediaDownloadWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    this.mSuspendedTasks.remove(mXMediaDownloadWorkerTask);
                } catch (RejectedExecutionException unused) {
                    mXMediaDownloadWorkerTask.cancel(true);
                    this.mSuspendedTasks.remove(mXMediaDownloadWorkerTask);
                    MXMediaDownloadWorkerTask mXMediaDownloadWorkerTask2 = new MXMediaDownloadWorkerTask(mXMediaDownloadWorkerTask);
                    this.mSuspendedTasks.add(mXMediaDownloadWorkerTask2);
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Suspend again the task ");
                    sb.append(mXMediaDownloadWorkerTask2.getStatus());
                    Log.m209d(str, sb.toString());
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Try to Restart a task fails ");
                    sb2.append(e.getMessage());
                    Log.m209d(str2, sb2.toString());
                }
            }
        }
    }

    public String loadBitmap(Context context, HomeServerConnectionConfig homeServerConnectionConfig, ImageView imageView, String str, int i, int i2, int i3, int i4, String str2, File file, EncryptedFileInfo encryptedFileInfo) {
        return loadBitmap(context, homeServerConnectionConfig, imageView, str, i, i2, i3, i4, str2, file, null, encryptedFileInfo);
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f2  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String loadBitmap(android.content.Context r23, com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r24, android.widget.ImageView r25, java.lang.String r26, int r27, int r28, int r29, int r30, java.lang.String r31, java.io.File r32, android.graphics.Bitmap r33, com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r34) {
        /*
            r22 = this;
            r1 = r22
            r2 = r25
            r3 = r26
            r4 = r27
            r5 = r28
            r6 = r30
            r11 = 0
            if (r4 == 0) goto L_0x0183
            if (r5 != 0) goto L_0x0013
            goto L_0x0183
        L_0x0013:
            android.graphics.Bitmap r7 = mDefaultBitmap
            if (r7 != 0) goto L_0x0024
            android.content.res.Resources r7 = r23.getResources()
            r8 = 17301567(0x108003f, float:2.4979432E-38)
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeResource(r7, r8)
            mDefaultBitmap = r7
        L_0x0024:
            if (r33 != 0) goto L_0x002a
            android.graphics.Bitmap r7 = mDefaultBitmap
            r15 = r7
            goto L_0x002c
        L_0x002a:
            r15 = r33
        L_0x002c:
            com.opengarden.firechat.matrixsdk.util.ContentManager r7 = r1.mContentManager
            java.lang.String r7 = r7.downloadTaskIdForMatrixMediaContent(r3)
            if (r7 != 0) goto L_0x003a
            if (r2 == 0) goto L_0x0039
            r2.setImageBitmap(r15)
        L_0x0039:
            return r11
        L_0x003a:
            r14 = 0
            r13 = 1
            if (r34 != 0) goto L_0x0067
            if (r4 <= 0) goto L_0x0067
            if (r5 <= 0) goto L_0x0067
            com.opengarden.firechat.matrixsdk.util.ContentManager r8 = r1.mContentManager
            java.lang.String r9 = "scale"
            java.lang.String r3 = r8.getDownloadableThumbnailUrl(r3, r4, r5, r9)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r7)
            java.lang.String r7 = "_w_"
            r8.append(r7)
            r8.append(r4)
            java.lang.String r4 = "_h_"
            r8.append(r4)
            r8.append(r5)
            java.lang.String r7 = r8.toString()
            goto L_0x0072
        L_0x0067:
            com.opengarden.firechat.matrixsdk.util.ContentManager r4 = r1.mContentManager
            if (r34 == 0) goto L_0x006d
            r5 = 1
            goto L_0x006e
        L_0x006d:
            r5 = 0
        L_0x006e:
            java.lang.String r3 = r4.getDownloadableUrl(r3, r5)
        L_0x0072:
            if (r34 != 0) goto L_0x00bf
            r4 = 2147483647(0x7fffffff, float:NaN)
            r10 = r29
            if (r10 != r4) goto L_0x00c1
            if (r6 == 0) goto L_0x00c1
            if (r6 == r13) goto L_0x00c1
            java.lang.String r4 = "?"
            boolean r4 = r3.contains(r4)
            if (r4 == 0) goto L_0x0099
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = "&apply_orientation=true"
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            goto L_0x00aa
        L_0x0099:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = "?apply_orientation=true"
            r4.append(r3)
            java.lang.String r3 = r4.toString()
        L_0x00aa:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r7)
            java.lang.String r5 = "_apply_orientation"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r16 = r3
            r9 = r4
            goto L_0x00c4
        L_0x00bf:
            r10 = r29
        L_0x00c1:
            r16 = r3
            r9 = r7
        L_0x00c4:
            if (r2 == 0) goto L_0x00c9
            r2.setTag(r9)
        L_0x00c9:
            if (r31 != 0) goto L_0x00d0
            java.lang.String r3 = "image/jpeg"
            r17 = r3
            goto L_0x00d2
        L_0x00d0:
            r17 = r31
        L_0x00d2:
            android.content.Context r3 = r23.getApplicationContext()
            com.opengarden.firechat.matrixsdk.db.MXMediasCache$3 r8 = new com.opengarden.firechat.matrixsdk.db.MXMediasCache$3
            r8.<init>(r2, r9, r15)
            r4 = r32
            r5 = r16
            r6 = r9
            r7 = r10
            r18 = r8
            r8 = r17
            r19 = r15
            r15 = r9
            r9 = r34
            r10 = r18
            boolean r3 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.bitmapForURL(r3, r4, r5, r6, r7, r8, r9, r10)
            if (r3 == 0) goto L_0x00f6
            r20 = r11
            goto L_0x017e
        L_0x00f6:
            com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask r3 = com.opengarden.firechat.matrixsdk.p007db.MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(r15)
            if (r3 == 0) goto L_0x0105
            if (r2 == 0) goto L_0x0101
            r3.addImageView(r2)
        L_0x0101:
            r20 = r15
            goto L_0x017e
        L_0x0105:
            com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask r11 = new com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask
            com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver r6 = r1.mNetworkConnectivityReceiver
            com.opengarden.firechat.matrixsdk.rest.client.MediaScanRestClient r10 = r1.mMediaScanRestClient
            com.opengarden.firechat.matrixsdk.util.ContentManager r3 = r1.mContentManager
            boolean r18 = r3.isAvScannerEnabled()
            r3 = r11
            r4 = r23
            r5 = r24
            r7 = r32
            r8 = r16
            r9 = r15
            r16 = r10
            r10 = r29
            r20 = r15
            r15 = r11
            r11 = r17
            r12 = r34
            r13 = r16
            r14 = r18
            r3.<init>(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            if (r2 == 0) goto L_0x0132
            r15.addImageView(r2)
        L_0x0132:
            r7 = r19
            r15.setDefaultBitmap(r7)
            com.opengarden.firechat.matrixsdk.db.MXMediasCache$4 r2 = new com.opengarden.firechat.matrixsdk.db.MXMediasCache$4
            r2.<init>()
            r15.addDownloadListener(r2)
            java.util.concurrent.Executor r2 = android.os.AsyncTask.THREAD_POOL_EXECUTOR     // Catch:{ RejectedExecutionException -> 0x0165, Exception -> 0x0148 }
            r3 = 0
            java.lang.Void[] r3 = new java.lang.Void[r3]     // Catch:{ RejectedExecutionException -> 0x0165, Exception -> 0x0148 }
            r15.executeOnExecutor(r2, r3)     // Catch:{ RejectedExecutionException -> 0x0165, Exception -> 0x0148 }
            goto L_0x017e
        L_0x0148:
            r0 = move-exception
            r2 = r0
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "loadBitmap failed "
            r4.append(r5)
            java.lang.String r2 = r2.getMessage()
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)
            goto L_0x017e
        L_0x0165:
            java.util.List<com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask> r2 = r1.mSuspendedTasks
            monitor-enter(r2)
            r3 = 1
            r15.cancel(r3)     // Catch:{ all -> 0x017f }
            com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask r3 = new com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask     // Catch:{ all -> 0x017f }
            r3.<init>(r15)     // Catch:{ all -> 0x017f }
            java.util.List<com.opengarden.firechat.matrixsdk.db.MXMediaDownloadWorkerTask> r4 = r1.mSuspendedTasks     // Catch:{ all -> 0x017f }
            r4.add(r3)     // Catch:{ all -> 0x017f }
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x017f }
            java.lang.String r4 = "Suspend a task "
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r4)     // Catch:{ all -> 0x017f }
            monitor-exit(r2)     // Catch:{ all -> 0x017f }
        L_0x017e:
            return r20
        L_0x017f:
            r0 = move-exception
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x017f }
            throw r3
        L_0x0183:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.p007db.MXMediasCache.loadBitmap(android.content.Context, com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig, android.widget.ImageView, java.lang.String, int, int, int, int, java.lang.String, java.io.File, android.graphics.Bitmap, com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo):java.lang.String");
    }

    public int getProgressValueForDownloadId(String str) {
        MXMediaDownloadWorkerTask mediaDownloadWorkerTask = MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(str);
        if (mediaDownloadWorkerTask != null) {
            return mediaDownloadWorkerTask.getProgress();
        }
        return -1;
    }

    @Nullable
    public DownloadStats getStatsForDownloadId(String str) {
        MXMediaDownloadWorkerTask mediaDownloadWorkerTask = MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(str);
        if (mediaDownloadWorkerTask != null) {
            return mediaDownloadWorkerTask.getDownloadStats();
        }
        return null;
    }

    public void addDownloadListener(String str, IMXMediaDownloadListener iMXMediaDownloadListener) {
        MXMediaDownloadWorkerTask mediaDownloadWorkerTask = MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(str);
        if (mediaDownloadWorkerTask != null) {
            mediaDownloadWorkerTask.addDownloadListener(iMXMediaDownloadListener);
        }
    }

    public void cancelDownload(String str) {
        MXMediaDownloadWorkerTask mediaDownloadWorkerTask = MXMediaDownloadWorkerTask.getMediaDownloadWorkerTask(str);
        if (mediaDownloadWorkerTask != null) {
            mediaDownloadWorkerTask.cancelDownload();
        }
    }

    public void uploadContent(InputStream inputStream, String str, String str2, String str3, IMXMediaUploadListener iMXMediaUploadListener) {
        try {
            MXMediaUploadWorkerTask mXMediaUploadWorkerTask = new MXMediaUploadWorkerTask(this.mContentManager, inputStream, str2, str3, str, iMXMediaUploadListener);
            mXMediaUploadWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception unused) {
            if (iMXMediaUploadListener != null) {
                iMXMediaUploadListener.onUploadError(str3, -1, null);
            }
        }
    }

    public int getProgressValueForUploadId(String str) {
        MXMediaUploadWorkerTask mediaUploadWorkerTask = MXMediaUploadWorkerTask.getMediaUploadWorkerTask(str);
        if (mediaUploadWorkerTask != null) {
            return mediaUploadWorkerTask.getProgress();
        }
        return -1;
    }

    public UploadStats getStatsForUploadId(String str) {
        MXMediaUploadWorkerTask mediaUploadWorkerTask = MXMediaUploadWorkerTask.getMediaUploadWorkerTask(str);
        if (mediaUploadWorkerTask != null) {
            return mediaUploadWorkerTask.getStats();
        }
        return null;
    }

    public void addUploadListener(String str, IMXMediaUploadListener iMXMediaUploadListener) {
        MXMediaUploadWorkerTask mediaUploadWorkerTask = MXMediaUploadWorkerTask.getMediaUploadWorkerTask(str);
        if (mediaUploadWorkerTask != null) {
            mediaUploadWorkerTask.addListener(iMXMediaUploadListener);
        }
    }

    public void cancelUpload(String str) {
        MXMediaUploadWorkerTask mediaUploadWorkerTask = MXMediaUploadWorkerTask.getMediaUploadWorkerTask(str);
        if (mediaUploadWorkerTask != null) {
            mediaUploadWorkerTask.cancelUpload();
        }
    }

    public void setMediaScanRestClient(MediaScanRestClient mediaScanRestClient) {
        this.mMediaScanRestClient = mediaScanRestClient;
    }
}
