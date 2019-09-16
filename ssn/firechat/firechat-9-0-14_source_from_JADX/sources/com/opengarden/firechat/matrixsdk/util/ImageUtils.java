package com.opengarden.firechat.matrixsdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import com.facebook.imagepipeline.common.RotationOptions;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
    private static final String LOG_TAG = "ImageUtils";

    public static int getRotationAngleForBitmap(Context context, Uri uri) {
        int orientationForBitmap = getOrientationForBitmap(context, uri);
        if (6 == orientationForBitmap) {
            return 90;
        }
        if (3 == orientationForBitmap) {
            return RotationOptions.ROTATE_180;
        }
        if (8 == orientationForBitmap) {
            return RotationOptions.ROTATE_270;
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getOrientationForBitmap(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            if (r9 != 0) goto L_0x0004
            return r0
        L_0x0004:
            java.lang.String r1 = r9.getScheme()
            java.lang.String r2 = "content"
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            if (r1 == 0) goto L_0x009e
            r1 = 1
            java.lang.String[] r4 = new java.lang.String[r1]
            java.lang.String r1 = "_data"
            r4[r0] = r1
            r1 = 0
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ Exception -> 0x0077 }
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r9
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0077 }
            if (r8 == 0) goto L_0x006c
            int r1 = r8.getCount()     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            if (r1 <= 0) goto L_0x006c
            r8.moveToFirst()     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.String r1 = "_data"
            int r1 = r8.getColumnIndexOrThrow(r1)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            if (r2 == 0) goto L_0x005b
            java.lang.String r1 = LOG_TAG     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            r2.<init>()     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.String r3 = "Cannot find path in media db for uri "
            r2.append(r3)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            r2.append(r9)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.String r9 = r2.toString()     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            com.opengarden.firechat.matrixsdk.util.Log.m217w(r1, r9)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            if (r8 == 0) goto L_0x005a
            r8.close()
        L_0x005a:
            return r0
        L_0x005b:
            android.media.ExifInterface r9 = new android.media.ExifInterface     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            r9.<init>(r1)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            java.lang.String r1 = "Orientation"
            int r9 = r9.getAttributeInt(r1, r0)     // Catch:{ Exception -> 0x0069, all -> 0x0067 }
            goto L_0x006d
        L_0x0067:
            r9 = move-exception
            goto L_0x0098
        L_0x0069:
            r9 = move-exception
            r1 = r8
            goto L_0x0078
        L_0x006c:
            r9 = 0
        L_0x006d:
            if (r8 == 0) goto L_0x0072
            r8.close()
        L_0x0072:
            r0 = r9
            goto L_0x00de
        L_0x0074:
            r9 = move-exception
            r8 = r1
            goto L_0x0098
        L_0x0077:
            r9 = move-exception
        L_0x0078:
            java.lang.String r8 = LOG_TAG     // Catch:{ all -> 0x0074 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0074 }
            r2.<init>()     // Catch:{ all -> 0x0074 }
            java.lang.String r3 = "Cannot get orientation for bitmap: "
            r2.append(r3)     // Catch:{ all -> 0x0074 }
            java.lang.String r9 = r9.getMessage()     // Catch:{ all -> 0x0074 }
            r2.append(r9)     // Catch:{ all -> 0x0074 }
            java.lang.String r9 = r2.toString()     // Catch:{ all -> 0x0074 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r9)     // Catch:{ all -> 0x0074 }
            if (r1 == 0) goto L_0x00de
            r1.close()
            goto L_0x00de
        L_0x0098:
            if (r8 == 0) goto L_0x009d
            r8.close()
        L_0x009d:
            throw r9
        L_0x009e:
            java.lang.String r8 = r9.getScheme()
            java.lang.String r1 = "file"
            boolean r8 = android.text.TextUtils.equals(r8, r1)
            if (r8 == 0) goto L_0x00de
            android.media.ExifInterface r8 = new android.media.ExifInterface     // Catch:{ Exception -> 0x00bb }
            java.lang.String r1 = r9.getPath()     // Catch:{ Exception -> 0x00bb }
            r8.<init>(r1)     // Catch:{ Exception -> 0x00bb }
            java.lang.String r1 = "Orientation"
            int r8 = r8.getAttributeInt(r1, r0)     // Catch:{ Exception -> 0x00bb }
            r0 = r8
            goto L_0x00de
        L_0x00bb:
            r8 = move-exception
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Cannot get EXIF for file uri "
            r2.append(r3)
            r2.append(r9)
            java.lang.String r9 = " because "
            r2.append(r9)
            java.lang.String r8 = r8.getMessage()
            r2.append(r8)
            java.lang.String r8 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r8)
        L_0x00de:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.util.ImageUtils.getOrientationForBitmap(android.content.Context, android.net.Uri):int");
    }

    public static Options decodeBitmapDimensions(InputStream inputStream) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        if (options.outHeight != -1 && options.outWidth != -1) {
            return options;
        }
        Log.m211e(LOG_TAG, "Cannot resize input stream, failed to get w/h.");
        return null;
    }

    public static int getSampleSize(int i, int i2, int i3) {
        if (i2 > i) {
            i = i2;
        }
        int highestOneBit = Integer.highestOneBit((int) Math.floor(i > i3 ? (double) (i / i3) : 1.0d));
        if (highestOneBit == 0) {
            return 1;
        }
        return highestOneBit;
    }

    public static InputStream resizeImage(InputStream inputStream, int i, int i2, int i3) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[2048];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                break;
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
        inputStream.close();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        byteArrayOutputStream.close();
        Options decodeBitmapDimensions = decodeBitmapDimensions(byteArrayInputStream);
        if (decodeBitmapDimensions == null) {
            return null;
        }
        int i4 = decodeBitmapDimensions.outWidth;
        int i5 = decodeBitmapDimensions.outHeight;
        byteArrayInputStream.reset();
        if (i != -1) {
            i2 = getSampleSize(i4, i5, i);
        }
        if (i2 == 1) {
            return byteArrayInputStream;
        }
        Options options = new Options();
        options.inSampleSize = i2;
        Bitmap decodeStream = BitmapFactory.decodeStream(byteArrayInputStream, null, options);
        if (decodeStream == null) {
            return null;
        }
        byteArrayInputStream.close();
        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        decodeStream.compress(CompressFormat.JPEG, i3, byteArrayOutputStream2);
        decodeStream.recycle();
        return new ByteArrayInputStream(byteArrayOutputStream2.toByteArray());
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0083 A[Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0088 A[Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap rotateImage(android.content.Context r10, java.lang.String r11, int r12, com.opengarden.firechat.matrixsdk.p007db.MXMediasCache r13) {
        /*
            r10 = 0
            android.net.Uri r0 = android.net.Uri.parse(r11)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            if (r12 == 0) goto L_0x00c4
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r1.<init>()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r1.inPreferredConfig = r2     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r2 = -1
            r1.outWidth = r2     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r1.outHeight = r2     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r0 = r0.getPath()     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            java.io.File r3 = new java.io.File     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            r3.<init>(r0)     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            r2.<init>(r3)     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r2, r10, r1)     // Catch:{ OutOfMemoryError -> 0x004c, Exception -> 0x002f }
            r2.close()     // Catch:{ OutOfMemoryError -> 0x002d, Exception -> 0x002b }
            goto L_0x0068
        L_0x002b:
            r1 = move-exception
            goto L_0x0031
        L_0x002d:
            r1 = move-exception
            goto L_0x004e
        L_0x002f:
            r1 = move-exception
            r0 = r10
        L_0x0031:
            java.lang.String r2 = LOG_TAG     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r3.<init>()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r4 = "applyExifRotation "
            r3.append(r4)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r1 = r1.getMessage()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r3.append(r1)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r1 = r3.toString()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            goto L_0x0068
        L_0x004c:
            r1 = move-exception
            r0 = r10
        L_0x004e:
            java.lang.String r2 = LOG_TAG     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r3.<init>()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r4 = "applyExifRotation BitmapFactory.decodeStream : "
            r3.append(r4)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r1 = r1.getMessage()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r3.append(r1)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            java.lang.String r1 = r3.toString()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
        L_0x0068:
            android.graphics.Matrix r8 = new android.graphics.Matrix     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r8.<init>()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            float r12 = (float) r12     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r8.postRotate(r12)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r4 = 0
            r5 = 0
            int r6 = r0.getWidth()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            int r7 = r0.getHeight()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            r9 = 0
            r3 = r0
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
            if (r12 == r0) goto L_0x0086
            r0.recycle()     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
        L_0x0086:
            if (r13 == 0) goto L_0x008b
            r13.saveBitmap(r12, r11)     // Catch:{ OutOfMemoryError -> 0x00a9, Exception -> 0x008d }
        L_0x008b:
            r10 = r12
            goto L_0x00c4
        L_0x008d:
            r11 = move-exception
            java.lang.String r12 = LOG_TAG
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "applyExifRotation "
            r13.append(r0)
            java.lang.String r11 = r11.getMessage()
            r13.append(r11)
            java.lang.String r11 = r13.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r11)
            goto L_0x00c4
        L_0x00a9:
            r11 = move-exception
            java.lang.String r12 = LOG_TAG
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r0 = "applyExifRotation "
            r13.append(r0)
            java.lang.String r11 = r11.getMessage()
            r13.append(r11)
            java.lang.String r11 = r13.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r11)
        L_0x00c4:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.util.ImageUtils.rotateImage(android.content.Context, java.lang.String, int, com.opengarden.firechat.matrixsdk.db.MXMediasCache):android.graphics.Bitmap");
    }

    public static Bitmap applyExifRotation(Context context, String str, MXMediasCache mXMediasCache) {
        try {
            int rotationAngleForBitmap = getRotationAngleForBitmap(context, Uri.parse(str));
            if (rotationAngleForBitmap != 0) {
                return rotateImage(context, str, rotationAngleForBitmap, mXMediasCache);
            }
            return null;
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("applyExifRotation ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return null;
        }
    }

    public static String scaleAndRotateImage(Context context, InputStream inputStream, String str, int i, int i2, MXMediasCache mXMediasCache) {
        String str2 = null;
        if (context == null || inputStream == null || mXMediasCache == null) {
            return null;
        }
        try {
            String saveMedia = mXMediasCache.saveMedia(resizeImage(inputStream, i, 0, 75), null, str);
            try {
                rotateImage(context, saveMedia, i2, mXMediasCache);
                return saveMedia;
            } catch (Exception e) {
                e = e;
                str2 = saveMedia;
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("rotateAndScale ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
                return str2;
            }
        } catch (Exception e2) {
            e = e2;
            String str32 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("rotateAndScale ");
            sb2.append(e.getMessage());
            Log.m211e(str32, sb2.toString());
            return str2;
        }
    }
}
