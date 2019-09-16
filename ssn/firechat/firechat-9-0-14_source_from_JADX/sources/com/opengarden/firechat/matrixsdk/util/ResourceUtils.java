package com.opengarden.firechat.matrixsdk.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.InputStream;

public class ResourceUtils {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "ResourceUtils";
    public static final String MIME_TYPE_ALL_CONTENT = "*/*";
    public static final String MIME_TYPE_IMAGE_ALL = "image/*";
    public static final String MIME_TYPE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_JPG = "image/jpg";

    public static class Resource {
        public InputStream mContentStream;
        public String mMimeType;

        public Resource(InputStream inputStream, String str) {
            this.mContentStream = inputStream;
            this.mMimeType = str;
        }

        public void close() {
            try {
                this.mMimeType = null;
                if (this.mContentStream != null) {
                    this.mContentStream.close();
                    this.mContentStream = null;
                }
            } catch (Exception e) {
                String access$000 = ResourceUtils.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Resource.close failed ");
                sb.append(e.getLocalizedMessage());
                Log.m211e(access$000, sb.toString());
            }
        }

        public boolean isJpegResource() {
            return ResourceUtils.MIME_TYPE_JPEG.equals(this.mMimeType) || ResourceUtils.MIME_TYPE_JPG.equals(this.mMimeType);
        }
    }

    public static Resource openResource(Context context, Uri uri, String str) {
        try {
            if (TextUtils.isEmpty(str)) {
                str = context.getContentResolver().getType(uri);
                if (str == null) {
                    String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(uri.toString().toLowerCase());
                    if (fileExtensionFromUrl != null) {
                        str = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                    }
                }
            }
            return new Resource(context.getContentResolver().openInputStream(uri), str);
        } catch (Exception e) {
            Log.m212e(LOG_TAG, "Failed to open resource input stream", e);
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003c A[Catch:{ Exception -> 0x009c }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0096 A[Catch:{ Exception -> 0x009c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap createThumbnailBitmap(android.content.Context r8, android.net.Uri r9, int r10, int r11) {
        /*
            r0 = 0
            com.opengarden.firechat.matrixsdk.util.ResourceUtils$Resource r1 = openResource(r8, r9, r0)
            if (r1 != 0) goto L_0x0008
            return r0
        L_0x0008:
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x009c }
            r1.<init>()     // Catch:{ Exception -> 0x009c }
            android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x009c }
            r1.inPreferredConfig = r2     // Catch:{ Exception -> 0x009c }
            com.opengarden.firechat.matrixsdk.util.ResourceUtils$Resource r8 = openResource(r8, r9, r0)     // Catch:{ Exception -> 0x009c }
            if (r8 == 0) goto L_0x0039
            java.io.InputStream r9 = r8.mContentStream     // Catch:{ Exception -> 0x001e }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r9, r0, r1)     // Catch:{ Exception -> 0x001e }
            goto L_0x003a
        L_0x001e:
            r9 = move-exception
            java.lang.String r1 = LOG_TAG     // Catch:{ Exception -> 0x009c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x009c }
            r2.<init>()     // Catch:{ Exception -> 0x009c }
            java.lang.String r3 = "BitmapFactory.decodeStream fails "
            r2.append(r3)     // Catch:{ Exception -> 0x009c }
            java.lang.String r9 = r9.getLocalizedMessage()     // Catch:{ Exception -> 0x009c }
            r2.append(r9)     // Catch:{ Exception -> 0x009c }
            java.lang.String r9 = r2.toString()     // Catch:{ Exception -> 0x009c }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r9)     // Catch:{ Exception -> 0x009c }
        L_0x0039:
            r9 = r0
        L_0x003a:
            if (r9 == 0) goto L_0x0094
            int r1 = r9.getHeight()     // Catch:{ Exception -> 0x009c }
            if (r1 >= r11) goto L_0x004a
            int r1 = r9.getWidth()     // Catch:{ Exception -> 0x009c }
            if (r1 >= r10) goto L_0x004a
            r0 = r9
            goto L_0x008c
        L_0x004a:
            double r1 = (double) r10     // Catch:{ Exception -> 0x009c }
            double r10 = (double) r11     // Catch:{ Exception -> 0x009c }
            int r3 = r9.getWidth()     // Catch:{ Exception -> 0x009c }
            double r3 = (double) r3     // Catch:{ Exception -> 0x009c }
            int r5 = r9.getHeight()     // Catch:{ Exception -> 0x009c }
            double r5 = (double) r5
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x005f
            double r5 = r5 * r1
            double r10 = r5 / r3
            goto L_0x0063
        L_0x005f:
            double r3 = r3 * r10
            double r1 = r3 / r5
        L_0x0063:
            if (r9 != 0) goto L_0x0067
            r3 = r0
            goto L_0x0068
        L_0x0067:
            r3 = r9
        L_0x0068:
            int r1 = (int) r1
            int r10 = (int) r10
            r11 = 0
            android.graphics.Bitmap r10 = android.graphics.Bitmap.createScaledBitmap(r3, r1, r10, r11)     // Catch:{ OutOfMemoryError -> 0x0071 }
            r0 = r10
            goto L_0x008c
        L_0x0071:
            r10 = move-exception
            java.lang.String r11 = LOG_TAG     // Catch:{ Exception -> 0x009c }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x009c }
            r1.<init>()     // Catch:{ Exception -> 0x009c }
            java.lang.String r2 = "createThumbnailBitmap "
            r1.append(r2)     // Catch:{ Exception -> 0x009c }
            java.lang.String r10 = r10.getMessage()     // Catch:{ Exception -> 0x009c }
            r1.append(r10)     // Catch:{ Exception -> 0x009c }
            java.lang.String r10 = r1.toString()     // Catch:{ Exception -> 0x009c }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r10)     // Catch:{ Exception -> 0x009c }
        L_0x008c:
            if (r9 == 0) goto L_0x0094
            r9.recycle()     // Catch:{ Exception -> 0x009c }
            java.lang.System.gc()     // Catch:{ Exception -> 0x009c }
        L_0x0094:
            if (r8 == 0) goto L_0x00b7
            java.io.InputStream r8 = r8.mContentStream     // Catch:{ Exception -> 0x009c }
            r8.close()     // Catch:{ Exception -> 0x009c }
            goto L_0x00b7
        L_0x009c:
            r8 = move-exception
            java.lang.String r9 = LOG_TAG
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "createThumbnailBitmap fails "
            r10.append(r11)
            java.lang.String r8 = r8.getLocalizedMessage()
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r9, r8)
        L_0x00b7:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.util.ResourceUtils.createThumbnailBitmap(android.content.Context, android.net.Uri, int, int):android.graphics.Bitmap");
    }
}
