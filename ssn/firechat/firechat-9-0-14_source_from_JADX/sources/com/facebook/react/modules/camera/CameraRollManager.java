package com.facebook.react.modules.camera;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import com.RNFetchBlob.RNFetchBlobConst;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import javax.annotation.Nullable;

@ReactModule(name = "CameraRollManager")
public class CameraRollManager extends ReactContextBaseJavaModule {
    private static final String ERROR_UNABLE_TO_LOAD = "E_UNABLE_TO_LOAD";
    private static final String ERROR_UNABLE_TO_LOAD_PERMISSION = "E_UNABLE_TO_LOAD_PERMISSION";
    private static final String ERROR_UNABLE_TO_SAVE = "E_UNABLE_TO_SAVE";
    public static final boolean IS_JELLY_BEAN_OR_LATER = (VERSION.SDK_INT >= 16);
    protected static final String NAME = "CameraRollManager";
    /* access modifiers changed from: private */
    public static final String[] PROJECTION;
    private static final String SELECTION_BUCKET = "bucket_display_name = ?";
    private static final String SELECTION_DATE_TAKEN = "datetaken < ?";

    private static class GetPhotosTask extends GuardedAsyncTask<Void, Void> {
        @Nullable
        private final String mAfter;
        @Nullable
        private final String mAssetType;
        private final Context mContext;
        private final int mFirst;
        @Nullable
        private final String mGroupName;
        @Nullable
        private final ReadableArray mMimeTypes;
        private final Promise mPromise;

        private GetPhotosTask(ReactContext reactContext, int i, @Nullable String str, @Nullable String str2, @Nullable ReadableArray readableArray, @Nullable String str3, Promise promise) {
            super(reactContext);
            this.mContext = reactContext;
            this.mFirst = i;
            this.mAfter = str;
            this.mGroupName = str2;
            this.mMimeTypes = readableArray;
            this.mPromise = promise;
            this.mAssetType = str3;
        }

        /* access modifiers changed from: protected */
        public void doInBackgroundGuarded(Void... voidArr) {
            Cursor query;
            StringBuilder sb = new StringBuilder("1");
            ArrayList arrayList = new ArrayList();
            if (!TextUtils.isEmpty(this.mAfter)) {
                sb.append(" AND datetaken < ?");
                arrayList.add(this.mAfter);
            }
            if (!TextUtils.isEmpty(this.mGroupName)) {
                sb.append(" AND bucket_display_name = ?");
                arrayList.add(this.mGroupName);
            }
            if (this.mMimeTypes != null && this.mMimeTypes.size() > 0) {
                sb.append(" AND mime_type IN (");
                for (int i = 0; i < this.mMimeTypes.size(); i++) {
                    sb.append("?,");
                    arrayList.add(this.mMimeTypes.getString(i));
                }
                sb.replace(sb.length() - 1, sb.length(), ")");
            }
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            ContentResolver contentResolver = this.mContext.getContentResolver();
            try {
                Uri uri = (this.mAssetType == null || !this.mAssetType.equals("Videos")) ? Media.EXTERNAL_CONTENT_URI : Video.Media.EXTERNAL_CONTENT_URI;
                String[] access$200 = CameraRollManager.PROJECTION;
                String sb2 = sb.toString();
                String[] strArr = (String[]) arrayList.toArray(new String[arrayList.size()]);
                StringBuilder sb3 = new StringBuilder();
                sb3.append("datetaken DESC, date_modified DESC LIMIT ");
                sb3.append(this.mFirst + 1);
                query = contentResolver.query(uri, access$200, sb2, strArr, sb3.toString());
                if (query == null) {
                    this.mPromise.reject(CameraRollManager.ERROR_UNABLE_TO_LOAD, "Could not get photos");
                    return;
                }
                CameraRollManager.putEdges(contentResolver, query, writableNativeMap, this.mFirst, this.mAssetType);
                CameraRollManager.putPageInfo(query, writableNativeMap, this.mFirst);
                query.close();
                this.mPromise.resolve(writableNativeMap);
            } catch (SecurityException e) {
                this.mPromise.reject(CameraRollManager.ERROR_UNABLE_TO_LOAD_PERMISSION, "Could not get photos: need READ_EXTERNAL_STORAGE permission", e);
            } catch (Throwable th) {
                query.close();
                this.mPromise.resolve(writableNativeMap);
                throw th;
            }
        }
    }

    private static class SaveToCameraRoll extends GuardedAsyncTask<Void, Void> {
        private final Context mContext;
        /* access modifiers changed from: private */
        public final Promise mPromise;
        private final Uri mUri;

        public SaveToCameraRoll(ReactContext reactContext, Uri uri, Promise promise) {
            super(reactContext);
            this.mContext = reactContext;
            this.mUri = uri;
            this.mPromise = promise;
        }

        /* access modifiers changed from: protected */
        public void doInBackgroundGuarded(Void... voidArr) {
            FileChannel fileChannel;
            String str;
            int i;
            File file = new File(this.mUri.getPath());
            FileChannel fileChannel2 = null;
            try {
                File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                externalStoragePublicDirectory.mkdirs();
                if (!externalStoragePublicDirectory.isDirectory()) {
                    this.mPromise.reject(CameraRollManager.ERROR_UNABLE_TO_LOAD, "External media storage directory not available");
                    return;
                }
                File file2 = new File(externalStoragePublicDirectory, file.getName());
                String name = file.getName();
                if (name.indexOf(46) >= 0) {
                    String substring = name.substring(0, name.lastIndexOf(46));
                    i = 0;
                    String str2 = substring;
                    str = name.substring(name.lastIndexOf(46));
                    name = str2;
                } else {
                    str = "";
                    i = 0;
                }
                while (!file2.createNewFile()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(name);
                    sb.append("_");
                    int i2 = i + 1;
                    sb.append(i);
                    sb.append(str);
                    file2 = new File(externalStoragePublicDirectory, sb.toString());
                    i = i2;
                }
                FileChannel channel = new FileInputStream(file).getChannel();
                try {
                    fileChannel = new FileOutputStream(file2).getChannel();
                    try {
                        fileChannel.transferFrom(channel, 0, channel.size());
                        channel.close();
                        fileChannel.close();
                        MediaScannerConnection.scanFile(this.mContext, new String[]{file2.getAbsolutePath()}, null, new OnScanCompletedListener() {
                            public void onScanCompleted(String str, Uri uri) {
                                if (uri != null) {
                                    SaveToCameraRoll.this.mPromise.resolve(uri.toString());
                                } else {
                                    SaveToCameraRoll.this.mPromise.reject(CameraRollManager.ERROR_UNABLE_TO_SAVE, "Could not add image to gallery");
                                }
                            }
                        });
                        if (channel != null && channel.isOpen()) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                FLog.m66e(ReactConstants.TAG, "Could not close input channel", (Throwable) e);
                            }
                        }
                        if (fileChannel != null && fileChannel.isOpen()) {
                            try {
                                fileChannel.close();
                            } catch (IOException e2) {
                                FLog.m66e(ReactConstants.TAG, "Could not close output channel", (Throwable) e2);
                            }
                        }
                    } catch (IOException e3) {
                        Throwable th = e3;
                        fileChannel2 = channel;
                        e = th;
                        try {
                            this.mPromise.reject(e);
                            try {
                                fileChannel2.close();
                            } catch (IOException e4) {
                                FLog.m66e(ReactConstants.TAG, "Could not close input channel", (Throwable) e4);
                            }
                            fileChannel.close();
                        } catch (Throwable th2) {
                            th = th2;
                            if (fileChannel2 != null && fileChannel2.isOpen()) {
                                try {
                                    fileChannel2.close();
                                } catch (IOException e5) {
                                    FLog.m66e(ReactConstants.TAG, "Could not close input channel", (Throwable) e5);
                                }
                            }
                            if (fileChannel != null && fileChannel.isOpen()) {
                                try {
                                    fileChannel.close();
                                } catch (IOException e6) {
                                    FLog.m66e(ReactConstants.TAG, "Could not close output channel", (Throwable) e6);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        Throwable th4 = th3;
                        fileChannel2 = channel;
                        th = th4;
                        fileChannel2.close();
                        fileChannel.close();
                        throw th;
                    }
                } catch (IOException e7) {
                    fileChannel2 = channel;
                    e = e7;
                    fileChannel = null;
                    this.mPromise.reject(e);
                    if (fileChannel2 != null && fileChannel2.isOpen()) {
                        fileChannel2.close();
                    }
                    if (fileChannel != null && fileChannel.isOpen()) {
                        fileChannel.close();
                    }
                } catch (Throwable th5) {
                    fileChannel2 = channel;
                    th = th5;
                    fileChannel = null;
                    fileChannel2.close();
                    fileChannel.close();
                    throw th;
                }
            } catch (IOException e8) {
                e = e8;
                fileChannel = null;
                this.mPromise.reject(e);
                fileChannel2.close();
                fileChannel.close();
            } catch (Throwable th6) {
                th = th6;
                fileChannel = null;
                fileChannel2.close();
                fileChannel.close();
                throw th;
            }
        }
    }

    public String getName() {
        return NAME;
    }

    static {
        if (IS_JELLY_BEAN_OR_LATER) {
            PROJECTION = new String[]{"_id", "mime_type", "bucket_display_name", "datetaken", "width", "height", "longitude", "latitude"};
        } else {
            PROJECTION = new String[]{"_id", "mime_type", "bucket_display_name", "datetaken", "longitude", "latitude"};
        }
    }

    public CameraRollManager(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @ReactMethod
    public void saveToCameraRoll(String str, String str2, Promise promise) {
        new SaveToCameraRoll(getReactApplicationContext(), Uri.parse(str), promise).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @ReactMethod
    public void getPhotos(ReadableMap readableMap, Promise promise) {
        int i = readableMap.getInt("first");
        String string = readableMap.hasKey("after") ? readableMap.getString("after") : null;
        String string2 = readableMap.hasKey("groupName") ? readableMap.getString("groupName") : null;
        String string3 = readableMap.hasKey("assetType") ? readableMap.getString("assetType") : null;
        ReadableArray array = readableMap.hasKey("mimeTypes") ? readableMap.getArray("mimeTypes") : null;
        if (readableMap.hasKey("groupTypes")) {
            throw new JSApplicationIllegalArgumentException("groupTypes is not supported on Android");
        }
        GetPhotosTask getPhotosTask = new GetPhotosTask(getReactApplicationContext(), i, string, string2, array, string3, promise);
        getPhotosTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* access modifiers changed from: private */
    public static void putPageInfo(Cursor cursor, WritableMap writableMap, int i) {
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        writableNativeMap.putBoolean("has_next_page", i < cursor.getCount());
        if (i < cursor.getCount()) {
            cursor.moveToPosition(i - 1);
            writableNativeMap.putString("end_cursor", cursor.getString(cursor.getColumnIndex("datetaken")));
        }
        writableMap.putMap("page_info", writableNativeMap);
    }

    /* access modifiers changed from: private */
    public static void putEdges(ContentResolver contentResolver, Cursor cursor, WritableMap writableMap, int i, @Nullable String str) {
        WritableNativeArray writableNativeArray;
        Cursor cursor2 = cursor;
        WritableNativeArray writableNativeArray2 = new WritableNativeArray();
        cursor.moveToFirst();
        int columnIndex = cursor2.getColumnIndex("_id");
        int columnIndex2 = cursor2.getColumnIndex("mime_type");
        int columnIndex3 = cursor2.getColumnIndex("bucket_display_name");
        int columnIndex4 = cursor2.getColumnIndex("datetaken");
        int columnIndex5 = IS_JELLY_BEAN_OR_LATER ? cursor2.getColumnIndex("width") : -1;
        int columnIndex6 = IS_JELLY_BEAN_OR_LATER ? cursor2.getColumnIndex("height") : -1;
        int columnIndex7 = cursor2.getColumnIndex("longitude");
        int columnIndex8 = cursor2.getColumnIndex("latitude");
        int i2 = i;
        int i3 = 0;
        while (i3 < i2 && !cursor.isAfterLast()) {
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            WritableNativeMap writableNativeMap2 = new WritableNativeMap();
            WritableNativeMap writableNativeMap3 = writableNativeMap2;
            WritableNativeArray writableNativeArray3 = writableNativeArray2;
            WritableNativeMap writableNativeMap4 = writableNativeMap;
            int i4 = i3;
            int i5 = columnIndex;
            int i6 = columnIndex8;
            if (putImageInfo(contentResolver, cursor2, writableNativeMap2, columnIndex, columnIndex5, columnIndex6, str)) {
                WritableNativeMap writableNativeMap5 = writableNativeMap3;
                putBasicNodeInfo(cursor2, writableNativeMap5, columnIndex2, columnIndex3, columnIndex4);
                putLocationInfo(cursor2, writableNativeMap5, columnIndex7, i6);
                writableNativeMap4.putMap("node", writableNativeMap5);
                writableNativeArray = writableNativeArray3;
                writableNativeArray.pushMap(writableNativeMap4);
            } else {
                writableNativeArray = writableNativeArray3;
                i4--;
            }
            cursor.moveToNext();
            i3 = i4 + 1;
            i2 = i;
            writableNativeArray2 = writableNativeArray;
            columnIndex8 = i6;
            columnIndex = i5;
        }
        writableMap.putArray("edges", writableNativeArray2);
    }

    private static void putBasicNodeInfo(Cursor cursor, WritableMap writableMap, int i, int i2, int i3) {
        writableMap.putString("type", cursor.getString(i));
        writableMap.putString("group_name", cursor.getString(i2));
        writableMap.putDouble(Param.TIMESTAMP, ((double) cursor.getLong(i3)) / 1000.0d);
    }

    private static boolean putImageInfo(ContentResolver contentResolver, Cursor cursor, WritableMap writableMap, int i, int i2, int i3, @Nullable String str) {
        Uri uri;
        float f;
        WritableNativeMap writableNativeMap = new WritableNativeMap();
        if (str == null || !str.equals("Videos")) {
            uri = Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI, cursor.getString(i));
        } else {
            uri = Uri.withAppendedPath(Video.Media.EXTERNAL_CONTENT_URI, cursor.getString(i));
        }
        writableNativeMap.putString(RNFetchBlobConst.DATA_ENCODE_URI, uri.toString());
        float f2 = -1.0f;
        if (IS_JELLY_BEAN_OR_LATER) {
            f2 = (float) cursor.getInt(i2);
            f = (float) cursor.getInt(i3);
        } else {
            f = -1.0f;
        }
        if (str != null && str.equals("Videos") && VERSION.SDK_INT >= 10) {
            try {
                AssetFileDescriptor openAssetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r");
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(openAssetFileDescriptor.getFileDescriptor());
                if (f2 <= 0.0f || f <= 0.0f) {
                    f2 = (float) Integer.parseInt(mediaMetadataRetriever.extractMetadata(18));
                    f = (float) Integer.parseInt(mediaMetadataRetriever.extractMetadata(19));
                }
                writableNativeMap.putInt("playableDuration", Integer.parseInt(mediaMetadataRetriever.extractMetadata(9)) / 1000);
                mediaMetadataRetriever.release();
                openAssetFileDescriptor.close();
            } catch (IOException e) {
                String str2 = ReactConstants.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Could not get video metadata for ");
                sb.append(uri.toString());
                FLog.m66e(str2, sb.toString(), (Throwable) e);
                return false;
            }
        }
        if (f2 <= 0.0f || f <= 0.0f) {
            try {
                AssetFileDescriptor openAssetFileDescriptor2 = contentResolver.openAssetFileDescriptor(uri, "r");
                Options options = new Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(openAssetFileDescriptor2.getFileDescriptor(), null, options);
                f2 = (float) options.outWidth;
                f = (float) options.outHeight;
                openAssetFileDescriptor2.close();
            } catch (IOException e2) {
                String str3 = ReactConstants.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Could not get width/height for ");
                sb2.append(uri.toString());
                FLog.m66e(str3, sb2.toString(), (Throwable) e2);
                return false;
            }
        }
        writableNativeMap.putDouble("width", (double) f2);
        writableNativeMap.putDouble("height", (double) f);
        writableMap.putMap("image", writableNativeMap);
        return true;
    }

    private static void putLocationInfo(Cursor cursor, WritableMap writableMap, int i, int i2) {
        double d = cursor.getDouble(i);
        double d2 = cursor.getDouble(i2);
        if (d > 0.0d || d2 > 0.0d) {
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putDouble("longitude", d);
            writableNativeMap.putDouble("latitude", d2);
            writableMap.putMap(FirebaseAnalytics.Param.LOCATION, writableNativeMap);
        }
    }
}
