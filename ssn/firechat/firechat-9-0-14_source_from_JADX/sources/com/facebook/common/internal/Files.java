package com.facebook.common.internal;

import java.io.IOException;
import java.io.InputStream;

public class Files {
    private Files() {
    }

    static byte[] readFile(InputStream inputStream, long j) throws IOException {
        if (j > 2147483647L) {
            StringBuilder sb = new StringBuilder();
            sb.append("file is too large to fit in a byte array: ");
            sb.append(j);
            sb.append(" bytes");
            throw new OutOfMemoryError(sb.toString());
        } else if (j == 0) {
            return ByteStreams.toByteArray(inputStream);
        } else {
            return ByteStreams.toByteArray(inputStream, (int) j);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x001e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] toByteArray(java.io.File r4) throws java.io.IOException {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ all -> 0x001a }
            r1.<init>(r4)     // Catch:{ all -> 0x001a }
            java.nio.channels.FileChannel r4 = r1.getChannel()     // Catch:{ all -> 0x0018 }
            long r2 = r4.size()     // Catch:{ all -> 0x0018 }
            byte[] r4 = readFile(r1, r2)     // Catch:{ all -> 0x0018 }
            if (r1 == 0) goto L_0x0017
            r1.close()
        L_0x0017:
            return r4
        L_0x0018:
            r4 = move-exception
            goto L_0x001c
        L_0x001a:
            r4 = move-exception
            r1 = r0
        L_0x001c:
            if (r1 == 0) goto L_0x0021
            r1.close()
        L_0x0021:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.common.internal.Files.toByteArray(java.io.File):byte[]");
    }
}
