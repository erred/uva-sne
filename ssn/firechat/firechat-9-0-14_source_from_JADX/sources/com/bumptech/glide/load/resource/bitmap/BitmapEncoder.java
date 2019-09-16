package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;

public class BitmapEncoder implements ResourceEncoder<Bitmap> {
    public static final Option<CompressFormat> COMPRESSION_FORMAT = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionFormat");
    public static final Option<Integer> COMPRESSION_QUALITY = Option.memory("com.bumptech.glide.load.resource.bitmap.BitmapEncoder.CompressionQuality", Integer.valueOf(90));
    private static final String TAG = "BitmapEncoder";
    @Nullable
    private final ArrayPool arrayPool;

    public BitmapEncoder(@NonNull ArrayPool arrayPool2) {
        this.arrayPool = arrayPool2;
    }

    @Deprecated
    public BitmapEncoder() {
        this.arrayPool = null;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:22|(2:39|40)|41|42) */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004b, code lost:
        if (r5 != null) goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006a, code lost:
        if (r5 == null) goto L_0x006d;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00c7 */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0063 A[Catch:{ all -> 0x0057 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c4 A[SYNTHETIC, Splitter:B:39:0x00c4] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean encode(@android.support.annotation.NonNull com.bumptech.glide.load.engine.Resource<android.graphics.Bitmap> r8, @android.support.annotation.NonNull java.io.File r9, @android.support.annotation.NonNull com.bumptech.glide.load.Options r10) {
        /*
            r7 = this;
            java.lang.Object r8 = r8.get()
            android.graphics.Bitmap r8 = (android.graphics.Bitmap) r8
            android.graphics.Bitmap$CompressFormat r0 = r7.getFormat(r8, r10)
            java.lang.String r1 = "encode: [%dx%d] %s"
            int r2 = r8.getWidth()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            int r3 = r8.getHeight()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            com.bumptech.glide.util.pool.GlideTrace.beginSectionFormat(r1, r2, r3, r0)
            long r1 = com.bumptech.glide.util.LogTime.getLogTime()     // Catch:{ all -> 0x00c8 }
            com.bumptech.glide.load.Option<java.lang.Integer> r3 = COMPRESSION_QUALITY     // Catch:{ all -> 0x00c8 }
            java.lang.Object r3 = r10.get(r3)     // Catch:{ all -> 0x00c8 }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x00c8 }
            int r3 = r3.intValue()     // Catch:{ all -> 0x00c8 }
            r4 = 0
            r5 = 0
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0059 }
            r6.<init>(r9)     // Catch:{ IOException -> 0x0059 }
            com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool r9 = r7.arrayPool     // Catch:{ IOException -> 0x0054, all -> 0x0051 }
            if (r9 == 0) goto L_0x0043
            com.bumptech.glide.load.data.BufferedOutputStream r9 = new com.bumptech.glide.load.data.BufferedOutputStream     // Catch:{ IOException -> 0x0054, all -> 0x0051 }
            com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool r5 = r7.arrayPool     // Catch:{ IOException -> 0x0054, all -> 0x0051 }
            r9.<init>(r6, r5)     // Catch:{ IOException -> 0x0054, all -> 0x0051 }
            r5 = r9
            goto L_0x0044
        L_0x0043:
            r5 = r6
        L_0x0044:
            r8.compress(r0, r3, r5)     // Catch:{ IOException -> 0x0059 }
            r5.close()     // Catch:{ IOException -> 0x0059 }
            r4 = 1
            if (r5 == 0) goto L_0x006d
        L_0x004d:
            r5.close()     // Catch:{ IOException -> 0x006d }
            goto L_0x006d
        L_0x0051:
            r8 = move-exception
            r5 = r6
            goto L_0x00c2
        L_0x0054:
            r9 = move-exception
            r5 = r6
            goto L_0x005a
        L_0x0057:
            r8 = move-exception
            goto L_0x00c2
        L_0x0059:
            r9 = move-exception
        L_0x005a:
            java.lang.String r3 = "BitmapEncoder"
            r6 = 3
            boolean r3 = android.util.Log.isLoggable(r3, r6)     // Catch:{ all -> 0x0057 }
            if (r3 == 0) goto L_0x006a
            java.lang.String r3 = "BitmapEncoder"
            java.lang.String r6 = "Failed to encode Bitmap"
            android.util.Log.d(r3, r6, r9)     // Catch:{ all -> 0x0057 }
        L_0x006a:
            if (r5 == 0) goto L_0x006d
            goto L_0x004d
        L_0x006d:
            java.lang.String r9 = "BitmapEncoder"
            r3 = 2
            boolean r9 = android.util.Log.isLoggable(r9, r3)     // Catch:{ all -> 0x00c8 }
            if (r9 == 0) goto L_0x00be
            java.lang.String r9 = "BitmapEncoder"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c8 }
            r3.<init>()     // Catch:{ all -> 0x00c8 }
            java.lang.String r5 = "Compressed with type: "
            r3.append(r5)     // Catch:{ all -> 0x00c8 }
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            java.lang.String r0 = " of size "
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            int r0 = com.bumptech.glide.util.Util.getBitmapByteSize(r8)     // Catch:{ all -> 0x00c8 }
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            java.lang.String r0 = " in "
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            double r0 = com.bumptech.glide.util.LogTime.getElapsedMillis(r1)     // Catch:{ all -> 0x00c8 }
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            java.lang.String r0 = ", options format: "
            r3.append(r0)     // Catch:{ all -> 0x00c8 }
            com.bumptech.glide.load.Option<android.graphics.Bitmap$CompressFormat> r0 = COMPRESSION_FORMAT     // Catch:{ all -> 0x00c8 }
            java.lang.Object r10 = r10.get(r0)     // Catch:{ all -> 0x00c8 }
            r3.append(r10)     // Catch:{ all -> 0x00c8 }
            java.lang.String r10 = ", hasAlpha: "
            r3.append(r10)     // Catch:{ all -> 0x00c8 }
            boolean r8 = r8.hasAlpha()     // Catch:{ all -> 0x00c8 }
            r3.append(r8)     // Catch:{ all -> 0x00c8 }
            java.lang.String r8 = r3.toString()     // Catch:{ all -> 0x00c8 }
            android.util.Log.v(r9, r8)     // Catch:{ all -> 0x00c8 }
        L_0x00be:
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            return r4
        L_0x00c2:
            if (r5 == 0) goto L_0x00c7
            r5.close()     // Catch:{ IOException -> 0x00c7 }
        L_0x00c7:
            throw r8     // Catch:{ all -> 0x00c8 }
        L_0x00c8:
            r8 = move-exception
            com.bumptech.glide.util.pool.GlideTrace.endSection()
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.bumptech.glide.load.resource.bitmap.BitmapEncoder.encode(com.bumptech.glide.load.engine.Resource, java.io.File, com.bumptech.glide.load.Options):boolean");
    }

    private CompressFormat getFormat(Bitmap bitmap, Options options) {
        CompressFormat compressFormat = (CompressFormat) options.get(COMPRESSION_FORMAT);
        if (compressFormat != null) {
            return compressFormat;
        }
        if (bitmap.hasAlpha()) {
            return CompressFormat.PNG;
        }
        return CompressFormat.JPEG;
    }

    @NonNull
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.TRANSFORMED;
    }
}
