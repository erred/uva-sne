package com.facebook.react.modules.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import com.RNFetchBlob.RNFetchBlobConst;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.module.annotations.ReactModule;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@ReactModule(name = "ImageEditingManager")
public class ImageEditingManager extends ReactContextBaseJavaModule {
    private static final int COMPRESS_QUALITY = 90;
    @SuppressLint({"InlinedApi"})
    private static final String[] EXIF_ATTRIBUTES = {"FNumber", "DateTime", "DateTimeDigitized", "ExposureTime", "Flash", "FocalLength", "GPSAltitude", "GPSAltitudeRef", "GPSDateStamp", "GPSLatitude", "GPSLatitudeRef", "GPSLongitude", "GPSLongitudeRef", "GPSProcessingMethod", "GPSTimeStamp", "ImageLength", "ImageWidth", "ISOSpeedRatings", "Make", "Model", "Orientation", "SubSecTime", "SubSecTimeDigitized", "SubSecTimeOriginal", "WhiteBalance"};
    private static final List<String> LOCAL_URI_PREFIXES = Arrays.asList(new String[]{"file://", RNFetchBlobConst.FILE_PREFIX_CONTENT});
    protected static final String NAME = "ImageEditingManager";
    private static final String TEMP_FILE_PREFIX = "ReactNative_cropped_image_";

    private static class CleanTask extends GuardedAsyncTask<Void, Void> {
        private final Context mContext;

        private CleanTask(ReactContext reactContext) {
            super(reactContext);
            this.mContext = reactContext;
        }

        /* access modifiers changed from: protected */
        public void doInBackgroundGuarded(Void... voidArr) {
            cleanDirectory(this.mContext.getCacheDir());
            File externalCacheDir = this.mContext.getExternalCacheDir();
            if (externalCacheDir != null) {
                cleanDirectory(externalCacheDir);
            }
        }

        private void cleanDirectory(File file) {
            File[] listFiles = file.listFiles(new FilenameFilter() {
                public boolean accept(File file, String str) {
                    return str.startsWith(ImageEditingManager.TEMP_FILE_PREFIX);
                }
            });
            if (listFiles != null) {
                for (File delete : listFiles) {
                    delete.delete();
                }
            }
        }
    }

    private static class CropTask extends GuardedAsyncTask<Void, Void> {
        final Context mContext;
        final Callback mError;
        final int mHeight;
        final Callback mSuccess;
        int mTargetHeight;
        int mTargetWidth;
        final String mUri;
        final int mWidth;

        /* renamed from: mX */
        final int f79mX;

        /* renamed from: mY */
        final int f80mY;

        private CropTask(ReactContext reactContext, String str, int i, int i2, int i3, int i4, Callback callback, Callback callback2) {
            super(reactContext);
            this.mTargetWidth = 0;
            this.mTargetHeight = 0;
            if (i < 0 || i2 < 0 || i3 <= 0 || i4 <= 0) {
                throw new JSApplicationIllegalArgumentException(String.format("Invalid crop rectangle: [%d, %d, %d, %d]", new Object[]{Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4)}));
            }
            this.mContext = reactContext;
            this.mUri = str;
            this.f79mX = i;
            this.f80mY = i2;
            this.mWidth = i3;
            this.mHeight = i4;
            this.mSuccess = callback;
            this.mError = callback2;
        }

        public void setTargetSize(int i, int i2) {
            if (i <= 0 || i2 <= 0) {
                throw new JSApplicationIllegalArgumentException(String.format("Invalid target size: [%d, %d]", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
            }
            this.mTargetWidth = i;
            this.mTargetHeight = i2;
        }

        private InputStream openBitmapInputStream() throws IOException {
            InputStream inputStream;
            if (ImageEditingManager.isLocalUri(this.mUri)) {
                inputStream = this.mContext.getContentResolver().openInputStream(Uri.parse(this.mUri));
            } else {
                inputStream = new URL(this.mUri).openConnection().getInputStream();
            }
            if (inputStream != null) {
                return inputStream;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot open bitmap: ");
            sb.append(this.mUri);
            throw new IOException(sb.toString());
        }

        /* access modifiers changed from: protected */
        public void doInBackgroundGuarded(Void... voidArr) {
            Bitmap bitmap;
            try {
                Options options = new Options();
                if (this.mTargetWidth > 0 && this.mTargetHeight > 0) {
                    bitmap = cropAndResize(this.mTargetWidth, this.mTargetHeight, options);
                } else {
                    bitmap = crop(options);
                }
                String str = options.outMimeType;
                if (str != null) {
                    if (!str.isEmpty()) {
                        File access$300 = ImageEditingManager.createTempFile(this.mContext, str);
                        ImageEditingManager.writeCompressedBitmapToFile(bitmap, str, access$300);
                        if (str.equals(ResourceUtils.MIME_TYPE_JPEG)) {
                            ImageEditingManager.copyExif(this.mContext, Uri.parse(this.mUri), access$300);
                        }
                        this.mSuccess.invoke(Uri.fromFile(access$300).toString());
                        return;
                    }
                }
                throw new IOException("Could not determine MIME type");
            } catch (Exception e) {
                this.mError.invoke(e.getMessage());
            }
        }

        private Bitmap crop(Options options) throws IOException {
            InputStream openBitmapInputStream = openBitmapInputStream();
            BitmapRegionDecoder newInstance = BitmapRegionDecoder.newInstance(openBitmapInputStream, false);
            try {
                return newInstance.decodeRegion(new Rect(this.f79mX, this.f80mY, this.f79mX + this.mWidth, this.f80mY + this.mHeight), options);
            } finally {
                if (openBitmapInputStream != null) {
                    openBitmapInputStream.close();
                }
                newInstance.recycle();
            }
        }

        /* JADX INFO: finally extract failed */
        private Bitmap cropAndResize(int i, int i2, Options options) throws IOException {
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            int i3 = i;
            int i4 = i2;
            Options options2 = options;
            Assertions.assertNotNull(options);
            Options options3 = new Options();
            options3.inJustDecodeBounds = true;
            InputStream openBitmapInputStream = openBitmapInputStream();
            try {
                BitmapFactory.decodeStream(openBitmapInputStream, null, options3);
                float f6 = (float) i3;
                float f7 = (float) i4;
                float f8 = f6 / f7;
                if (((float) this.mWidth) / ((float) this.mHeight) > f8) {
                    f5 = ((float) this.mHeight) * f8;
                    f4 = (float) this.mHeight;
                    f2 = ((float) this.f79mX) + ((((float) this.mWidth) - f5) / 2.0f);
                    f = (float) this.f80mY;
                    f3 = f7 / ((float) this.mHeight);
                } else {
                    f5 = (float) this.mWidth;
                    float f9 = ((float) this.mWidth) / f8;
                    f2 = (float) this.f79mX;
                    f = ((float) this.f80mY) + ((((float) this.mHeight) - f9) / 2.0f);
                    float f10 = f9;
                    f3 = f6 / ((float) this.mWidth);
                    f4 = f10;
                }
                options2.inSampleSize = ImageEditingManager.getDecodeSampleSize(this.mWidth, this.mHeight, i3, i4);
                options3.inJustDecodeBounds = false;
                InputStream openBitmapInputStream2 = openBitmapInputStream();
                try {
                    Bitmap decodeStream = BitmapFactory.decodeStream(openBitmapInputStream2, null, options2);
                    if (decodeStream == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Cannot decode bitmap: ");
                        sb.append(this.mUri);
                        throw new IOException(sb.toString());
                    }
                    if (openBitmapInputStream2 != null) {
                        openBitmapInputStream2.close();
                    }
                    int floor = (int) Math.floor((double) (f2 / ((float) options2.inSampleSize)));
                    int floor2 = (int) Math.floor((double) (f / ((float) options2.inSampleSize)));
                    int floor3 = (int) Math.floor((double) (f5 / ((float) options2.inSampleSize)));
                    int floor4 = (int) Math.floor((double) (f4 / ((float) options2.inSampleSize)));
                    float f11 = f3 * ((float) options2.inSampleSize);
                    Matrix matrix = new Matrix();
                    matrix.setScale(f11, f11);
                    return Bitmap.createBitmap(decodeStream, floor, floor2, floor3, floor4, matrix, true);
                } catch (Throwable th) {
                    Throwable th2 = th;
                    if (openBitmapInputStream2 != null) {
                        openBitmapInputStream2.close();
                    }
                    throw th2;
                }
            } finally {
                if (openBitmapInputStream != null) {
                    openBitmapInputStream.close();
                }
            }
        }
    }

    public String getName() {
        return NAME;
    }

    public ImageEditingManager(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        new CleanTask(getReactApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public Map<String, Object> getConstants() {
        return Collections.emptyMap();
    }

    public void onCatalystInstanceDestroy() {
        new CleanTask(getReactApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    @ReactMethod
    public void cropImage(String str, ReadableMap readableMap, Callback callback, Callback callback2) {
        ReadableMap readableMap2 = readableMap;
        ReadableMap readableMap3 = null;
        ReadableMap map = readableMap2.hasKey("offset") ? readableMap2.getMap("offset") : null;
        if (readableMap2.hasKey("size")) {
            readableMap3 = readableMap2.getMap("size");
        }
        if (map == null || readableMap3 == null || !map.hasKey("x") || !map.hasKey("y") || !readableMap3.hasKey("width") || !readableMap3.hasKey("height")) {
            throw new JSApplicationIllegalArgumentException("Please specify offset and size");
        } else if (str == null || str.isEmpty()) {
            throw new JSApplicationIllegalArgumentException("Please specify a URI");
        } else {
            CropTask cropTask = new CropTask(getReactApplicationContext(), str, (int) map.getDouble("x"), (int) map.getDouble("y"), (int) readableMap3.getDouble("width"), (int) readableMap3.getDouble("height"), callback, callback2);
            if (readableMap2.hasKey("displaySize")) {
                ReadableMap map2 = readableMap2.getMap("displaySize");
                cropTask.setTargetSize((int) map2.getDouble("width"), (int) map2.getDouble("height"));
            }
            cropTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public static void copyExif(Context context, Uri uri, File file) throws IOException {
        String[] strArr;
        File fileFromUri = getFileFromUri(context, uri);
        if (fileFromUri == null) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Couldn't get real path for uri: ");
            sb.append(uri);
            FLog.m105w(str, sb.toString());
            return;
        }
        ExifInterface exifInterface = new ExifInterface(fileFromUri.getAbsolutePath());
        ExifInterface exifInterface2 = new ExifInterface(file.getAbsolutePath());
        for (String str2 : EXIF_ATTRIBUTES) {
            String attribute = exifInterface.getAttribute(str2);
            if (attribute != null) {
                exifInterface2.setAttribute(str2, attribute);
            }
        }
        exifInterface2.saveAttributes();
    }

    @Nullable
    private static File getFileFromUri(Context context, Uri uri) {
        if (uri.getScheme().equals(UriUtil.LOCAL_FILE_SCHEME)) {
            return new File(uri.getPath());
        }
        if (uri.getScheme().equals("content")) {
            Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        String string = query.getString(0);
                        if (!TextUtils.isEmpty(string)) {
                            return new File(string);
                        }
                    }
                    query.close();
                } finally {
                    query.close();
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static boolean isLocalUri(String str) {
        for (String startsWith : LOCAL_URI_PREFIXES) {
            if (str.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    private static String getFileExtensionForType(@Nullable String str) {
        if ("image/png".equals(str)) {
            return ".png";
        }
        return "image/webp".equals(str) ? ".webp" : ".jpg";
    }

    private static CompressFormat getCompressFormatForType(String str) {
        if ("image/png".equals(str)) {
            return CompressFormat.PNG;
        }
        if ("image/webp".equals(str)) {
            return CompressFormat.WEBP;
        }
        return CompressFormat.JPEG;
    }

    /* access modifiers changed from: private */
    public static void writeCompressedBitmapToFile(Bitmap bitmap, String str, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            bitmap.compress(getCompressFormatForType(str), 90, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /* access modifiers changed from: private */
    public static File createTempFile(Context context, @Nullable String str) throws IOException {
        File externalCacheDir = context.getExternalCacheDir();
        File cacheDir = context.getCacheDir();
        if (externalCacheDir == null && cacheDir == null) {
            throw new IOException("No cache directory available");
        }
        if (externalCacheDir == null || (cacheDir != null && externalCacheDir.getFreeSpace() <= cacheDir.getFreeSpace())) {
            externalCacheDir = cacheDir;
        }
        return File.createTempFile(TEMP_FILE_PREFIX, getFileExtensionForType(str), externalCacheDir);
    }

    /* access modifiers changed from: private */
    public static int getDecodeSampleSize(int i, int i2, int i3, int i4) {
        int i5 = 1;
        if (i2 > i3 || i > i4) {
            int i6 = i2 / 2;
            int i7 = i / 2;
            while (i7 / i5 >= i3 && i6 / i5 >= i4) {
                i5 *= 2;
            }
        }
        return i5;
    }
}
