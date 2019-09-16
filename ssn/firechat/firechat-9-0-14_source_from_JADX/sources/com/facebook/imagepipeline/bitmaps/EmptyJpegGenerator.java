package com.facebook.imagepipeline.bitmaps;

import com.facebook.common.memory.PooledByteBufferFactory;
import org.altbeacon.bluetooth.Pdu;

public class EmptyJpegGenerator {
    private static final byte[] EMPTY_JPEG_PREFIX = {-1, -40, -1, -37, 0, 67, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -64, 0, 17, 8};
    private static final byte[] EMPTY_JPEG_SUFFIX = {3, 1, 34, 0, 2, 17, 0, 3, 17, 0, -1, -60, 0, 31, 0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 16, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125, 1, 2, 3, 0, 4, 17, 5, 18, Framer.ENTER_FRAME_PREFIX, Framer.STDOUT_FRAME_PREFIX, 65, 6, 19, 81, 97, 7, 34, 113, 20, Framer.STDERR_FRAME_PREFIX, -127, -111, -95, 8, 35, 66, -79, -63, 21, 82, -47, -16, 36, 51, 98, 114, -126, 9, 10, Pdu.GATT_SERVICE_UUID_PDU_TYPE, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, Framer.EXIT_FRAME_PREFIX, 121, 122, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -60, 0, 31, 1, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, -1, -60, 0, -75, 17, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119, 0, 1, 2, 3, 17, 4, 5, Framer.ENTER_FRAME_PREFIX, Framer.STDOUT_FRAME_PREFIX, 6, 18, 65, 81, 7, 97, 113, 19, 34, Framer.STDERR_FRAME_PREFIX, -127, 8, 20, 66, -111, -95, -79, -63, 9, 35, 51, 82, -16, 21, 98, 114, -47, 10, Pdu.GATT_SERVICE_UUID_PDU_TYPE, 36, 52, -31, 37, -15, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, Framer.EXIT_FRAME_PREFIX, 121, 122, -126, -125, -124, -123, -122, -121, -120, -119, -118, -110, -109, -108, -107, -106, -105, -104, -103, -102, -94, -93, -92, -91, -90, -89, -88, -87, -86, -78, -77, -76, -75, -74, -73, -72, -71, -70, -62, -61, -60, -59, -58, -57, -56, -55, -54, -46, -45, -44, -43, -42, -41, -40, -39, -38, -30, -29, -28, -27, -26, -25, -24, -23, -22, -14, -13, -12, -11, -10, -9, -8, -7, -6, -1, -38, 0, 12, 3, 1, 0, 2, 17, 3, 17, 0, 63, 0, -114, -118, 40, -96, 15, -1, -39};
    private final PooledByteBufferFactory mPooledByteBufferFactory;

    public EmptyJpegGenerator(PooledByteBufferFactory pooledByteBufferFactory) {
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0051  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.facebook.common.references.CloseableReference<com.facebook.common.memory.PooledByteBuffer> generate(short r5, short r6) {
        /*
            r4 = this;
            r0 = 0
            com.facebook.common.memory.PooledByteBufferFactory r1 = r4.mPooledByteBufferFactory     // Catch:{ IOException -> 0x0048 }
            byte[] r2 = EMPTY_JPEG_PREFIX     // Catch:{ IOException -> 0x0048 }
            int r2 = r2.length     // Catch:{ IOException -> 0x0048 }
            byte[] r3 = EMPTY_JPEG_SUFFIX     // Catch:{ IOException -> 0x0048 }
            int r3 = r3.length     // Catch:{ IOException -> 0x0048 }
            int r2 = r2 + r3
            int r2 = r2 + 4
            com.facebook.common.memory.PooledByteBufferOutputStream r1 = r1.newOutputStream(r2)     // Catch:{ IOException -> 0x0048 }
            byte[] r0 = EMPTY_JPEG_PREFIX     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r0)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            int r0 = r6 >> 8
            byte r0 = (byte) r0     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r0)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r6 = r6 & 255(0xff, float:3.57E-43)
            byte r6 = (byte) r6     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r6)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            int r6 = r5 >> 8
            byte r6 = (byte) r6     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r6)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r5 = r5 & 255(0xff, float:3.57E-43)
            byte r5 = (byte) r5     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r5)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            byte[] r5 = EMPTY_JPEG_SUFFIX     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r1.write(r5)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            com.facebook.common.memory.PooledByteBuffer r5 = r1.toByteBuffer()     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            com.facebook.common.references.CloseableReference r5 = com.facebook.common.references.CloseableReference.m129of(r5)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            if (r1 == 0) goto L_0x003f
            r1.close()
        L_0x003f:
            return r5
        L_0x0040:
            r5 = move-exception
            goto L_0x004f
        L_0x0042:
            r5 = move-exception
            r0 = r1
            goto L_0x0049
        L_0x0045:
            r5 = move-exception
            r1 = r0
            goto L_0x004f
        L_0x0048:
            r5 = move-exception
        L_0x0049:
            java.lang.RuntimeException r6 = new java.lang.RuntimeException     // Catch:{ all -> 0x0045 }
            r6.<init>(r5)     // Catch:{ all -> 0x0045 }
            throw r6     // Catch:{ all -> 0x0045 }
        L_0x004f:
            if (r1 == 0) goto L_0x0054
            r1.close()
        L_0x0054:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.bitmaps.EmptyJpegGenerator.generate(short, short):com.facebook.common.references.CloseableReference");
    }
}
