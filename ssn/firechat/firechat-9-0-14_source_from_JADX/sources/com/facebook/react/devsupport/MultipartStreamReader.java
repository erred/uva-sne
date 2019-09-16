package com.facebook.react.devsupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;

public class MultipartStreamReader {
    private static final String CRLF = "\r\n";
    private final String mBoundary;
    private final BufferedSource mSource;

    public interface ChunkCallback {
        void execute(Map<String, String> map, Buffer buffer, boolean z) throws IOException;
    }

    public MultipartStreamReader(BufferedSource bufferedSource, String str) {
        this.mSource = bufferedSource;
        this.mBoundary = str;
    }

    private Map<String, String> parseHeaders(Buffer buffer) {
        String[] split;
        HashMap hashMap = new HashMap();
        for (String str : buffer.readUtf8().split(CRLF)) {
            int indexOf = str.indexOf(":");
            if (indexOf != -1) {
                hashMap.put(str.substring(0, indexOf).trim(), str.substring(indexOf + 1).trim());
            }
        }
        return hashMap;
    }

    private void emitChunk(Buffer buffer, boolean z, ChunkCallback chunkCallback) throws IOException {
        ByteString encodeUtf8 = ByteString.encodeUtf8("\r\n\r\n");
        long indexOf = buffer.indexOf(encodeUtf8);
        if (indexOf == -1) {
            chunkCallback.execute(null, buffer, z);
            return;
        }
        Buffer buffer2 = new Buffer();
        Buffer buffer3 = new Buffer();
        buffer.read(buffer2, indexOf);
        buffer.skip((long) encodeUtf8.size());
        buffer.readAll(buffer3);
        chunkCallback.execute(parseHeaders(buffer2), buffer3, z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x007d, code lost:
        r12 = r10 - r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0082, code lost:
        if (r8 <= 0) goto L_0x0095;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0084, code lost:
        r7 = new okio.Buffer();
        r3.skip(r8);
        r3.read(r7, r12);
        emitChunk(r7, r6, r18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0095, code lost:
        r8 = r18;
        r3.skip(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x009a, code lost:
        if (r6 == false) goto L_0x009d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x009c, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean readAllParts(com.facebook.react.devsupport.MultipartStreamReader.ChunkCallback r18) throws java.io.IOException {
        /*
            r17 = this;
            r0 = r17
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "\r\n--"
            r1.append(r2)
            java.lang.String r2 = r0.mBoundary
            r1.append(r2)
            java.lang.String r2 = "\r\n"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            okio.ByteString r1 = okio.ByteString.encodeUtf8(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "\r\n--"
            r2.append(r3)
            java.lang.String r3 = r0.mBoundary
            r2.append(r3)
            java.lang.String r3 = "--"
            r2.append(r3)
            java.lang.String r3 = "\r\n"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            okio.ByteString r2 = okio.ByteString.encodeUtf8(r2)
            okio.Buffer r3 = new okio.Buffer
            r3.<init>()
            r4 = 0
            r6 = r4
        L_0x0047:
            r8 = r6
        L_0x0048:
            int r10 = r2.size()
            long r10 = (long) r10
            long r12 = r6 - r10
            long r6 = java.lang.Math.max(r12, r8)
            long r10 = r3.indexOf(r1, r6)
            r12 = -1
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            r15 = 1
            r16 = 0
            if (r14 != 0) goto L_0x0066
            long r10 = r3.indexOf(r2, r6)
            r6 = 1
            goto L_0x0067
        L_0x0066:
            r6 = 0
        L_0x0067:
            int r7 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r7 != 0) goto L_0x007d
            long r6 = r3.size()
            okio.BufferedSource r10 = r0.mSource
            r11 = 4096(0x1000, float:5.74E-42)
            long r11 = (long) r11
            long r10 = r10.read(r3, r11)
            int r12 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r12 > 0) goto L_0x0048
            return r16
        L_0x007d:
            r7 = 0
            long r12 = r10 - r8
            int r7 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r7 <= 0) goto L_0x0095
            okio.Buffer r7 = new okio.Buffer
            r7.<init>()
            r3.skip(r8)
            r3.read(r7, r12)
            r8 = r18
            r0.emitChunk(r7, r6, r8)
            goto L_0x009a
        L_0x0095:
            r8 = r18
            r3.skip(r10)
        L_0x009a:
            if (r6 == 0) goto L_0x009d
            return r15
        L_0x009d:
            int r6 = r1.size()
            long r6 = (long) r6
            goto L_0x0047
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.devsupport.MultipartStreamReader.readAllParts(com.facebook.react.devsupport.MultipartStreamReader$ChunkCallback):boolean");
    }
}
