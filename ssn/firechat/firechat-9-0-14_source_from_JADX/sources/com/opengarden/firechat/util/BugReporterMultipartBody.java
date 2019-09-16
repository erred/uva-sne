package com.opengarden.firechat.util;

import com.facebook.stetho.server.http.HttpHeaders;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kotlin.text.Typography;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.ByteString;

public class BugReporterMultipartBody extends RequestBody {
    private static final byte[] COLONSPACE = {58, 32};
    private static final byte[] CRLF = {13, 10};
    private static final byte[] DASHDASH = {Framer.STDIN_FRAME_PREFIX, Framer.STDIN_FRAME_PREFIX};
    private static final MediaType FORM = MediaType.parse("multipart/form-data");
    private final ByteString mBoundary;
    private long mContentLength;
    private List<Long> mContentLengthSize;
    private final MediaType mContentType;
    private final List<Part> mParts;
    private WriteListener mWriteListener;

    public static final class Builder {
        private final ByteString boundary;
        private final List<Part> parts;

        public Builder() {
            this(UUID.randomUUID().toString());
        }

        public Builder(String str) {
            this.parts = new ArrayList();
            this.boundary = ByteString.encodeUtf8(str);
        }

        public Builder addFormDataPart(String str, String str2) {
            return addPart(Part.createFormData(str, str2));
        }

        public Builder addFormDataPart(String str, String str2, RequestBody requestBody) {
            return addPart(Part.createFormData(str, str2, requestBody));
        }

        public Builder addPart(Part part) {
            if (part == null) {
                throw new NullPointerException("part == null");
            }
            this.parts.add(part);
            return this;
        }

        public BugReporterMultipartBody build() {
            if (!this.parts.isEmpty()) {
                return new BugReporterMultipartBody(this.boundary, this.parts);
            }
            throw new IllegalStateException("Multipart body must have at least one part.");
        }
    }

    public static final class Part {
        final RequestBody body;
        final Headers headers;

        public static Part create(Headers headers2, RequestBody requestBody) {
            if (requestBody == null) {
                throw new NullPointerException("body == null");
            } else if (headers2 != null && headers2.get(HttpHeaders.CONTENT_TYPE) != null) {
                throw new IllegalArgumentException("Unexpected header: Content-Type");
            } else if (headers2 == null || headers2.get(HttpHeaders.CONTENT_LENGTH) == null) {
                return new Part(headers2, requestBody);
            } else {
                throw new IllegalArgumentException("Unexpected header: Content-Length");
            }
        }

        public static Part createFormData(String str, String str2) {
            return createFormData(str, null, RequestBody.create((MediaType) null, str2));
        }

        public static Part createFormData(String str, String str2, RequestBody requestBody) {
            if (str == null) {
                throw new NullPointerException("name == null");
            }
            StringBuilder sb = new StringBuilder("form-data; name=");
            BugReporterMultipartBody.appendQuotedString(sb, str);
            if (str2 != null) {
                sb.append("; filename=");
                BugReporterMultipartBody.appendQuotedString(sb, str2);
            }
            return create(Headers.m235of("Content-Disposition", sb.toString()), requestBody);
        }

        private Part(Headers headers2, RequestBody requestBody) {
            this.headers = headers2;
            this.body = requestBody;
        }
    }

    public interface WriteListener {
        void onWrite(long j, long j2);
    }

    private BugReporterMultipartBody(ByteString byteString, List<Part> list) {
        this.mContentLength = -1;
        this.mContentLengthSize = null;
        this.mBoundary = byteString;
        StringBuilder sb = new StringBuilder();
        sb.append(FORM);
        sb.append("; boundary=");
        sb.append(byteString.utf8());
        this.mContentType = MediaType.parse(sb.toString());
        this.mParts = Util.immutableList(list);
    }

    public MediaType contentType() {
        return this.mContentType;
    }

    public long contentLength() throws IOException {
        long j = this.mContentLength;
        if (j != -1) {
            return j;
        }
        long writeOrCountBytes = writeOrCountBytes(null, true);
        this.mContentLength = writeOrCountBytes;
        return writeOrCountBytes;
    }

    public void writeTo(BufferedSink bufferedSink) throws IOException {
        writeOrCountBytes(bufferedSink, false);
    }

    public void setWriteListener(WriteListener writeListener) {
        this.mWriteListener = writeListener;
    }

    private void onWrite(long j) {
        if (this.mWriteListener != null && this.mContentLength > 0) {
            this.mWriteListener.onWrite(j, this.mContentLength);
        }
    }

    /* JADX WARNING: type inference failed for: r13v1, types: [okio.BufferedSink] */
    /* JADX WARNING: type inference failed for: r0v1 */
    /* JADX WARNING: type inference failed for: r13v4, types: [okio.Buffer] */
    /* JADX WARNING: type inference failed for: r13v5 */
    /* JADX WARNING: type inference failed for: r13v6 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private long writeOrCountBytes(okio.BufferedSink r13, boolean r14) throws java.io.IOException {
        /*
            r12 = this;
            if (r14 == 0) goto L_0x0010
            okio.Buffer r13 = new okio.Buffer
            r13.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12.mContentLengthSize = r0
            r0 = r13
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            java.util.List<com.opengarden.firechat.util.BugReporterMultipartBody$Part> r1 = r12.mParts
            int r1 = r1.size()
            r2 = 0
            r3 = 0
            r4 = r3
            r3 = 0
        L_0x001c:
            if (r3 >= r1) goto L_0x00e6
            java.util.List<com.opengarden.firechat.util.BugReporterMultipartBody$Part> r6 = r12.mParts
            java.lang.Object r6 = r6.get(r3)
            com.opengarden.firechat.util.BugReporterMultipartBody$Part r6 = (com.opengarden.firechat.util.BugReporterMultipartBody.Part) r6
            okhttp3.Headers r7 = r6.headers
            okhttp3.RequestBody r6 = r6.body
            byte[] r8 = DASHDASH
            r13.write(r8)
            okio.ByteString r8 = r12.mBoundary
            r13.write(r8)
            byte[] r8 = CRLF
            r13.write(r8)
            if (r7 == 0) goto L_0x0060
            int r8 = r7.size()
            r9 = 0
        L_0x0040:
            if (r9 >= r8) goto L_0x0060
            java.lang.String r10 = r7.name(r9)
            okio.BufferedSink r10 = r13.writeUtf8(r10)
            byte[] r11 = COLONSPACE
            okio.BufferedSink r10 = r10.write(r11)
            java.lang.String r11 = r7.value(r9)
            okio.BufferedSink r10 = r10.writeUtf8(r11)
            byte[] r11 = CRLF
            r10.write(r11)
            int r9 = r9 + 1
            goto L_0x0040
        L_0x0060:
            okhttp3.MediaType r7 = r6.contentType()
            if (r7 == 0) goto L_0x0079
            java.lang.String r8 = "Content-Type: "
            okio.BufferedSink r8 = r13.writeUtf8(r8)
            java.lang.String r7 = r7.toString()
            okio.BufferedSink r7 = r8.writeUtf8(r7)
            byte[] r8 = CRLF
            r7.write(r8)
        L_0x0079:
            long r7 = r6.contentLength()
            int r7 = (int) r7
            r8 = -1
            if (r7 == r8) goto L_0x00a2
            java.lang.String r8 = "Content-Length: "
            okio.BufferedSink r8 = r13.writeUtf8(r8)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r7)
            java.lang.String r10 = ""
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            okio.BufferedSink r8 = r8.writeUtf8(r9)
            byte[] r9 = CRLF
            r8.write(r9)
            goto L_0x00aa
        L_0x00a2:
            if (r14 == 0) goto L_0x00aa
            r0.clear()
            r13 = -1
            return r13
        L_0x00aa:
            byte[] r8 = CRLF
            r13.write(r8)
            if (r14 == 0) goto L_0x00bf
            long r6 = (long) r7
            long r8 = r4 + r6
            java.util.List<java.lang.Long> r4 = r12.mContentLengthSize
            java.lang.Long r5 = java.lang.Long.valueOf(r8)
            r4.add(r5)
            r4 = r8
            goto L_0x00dd
        L_0x00bf:
            r6.writeTo(r13)
            java.util.List<java.lang.Long> r6 = r12.mContentLengthSize
            if (r6 == 0) goto L_0x00dd
            java.util.List<java.lang.Long> r6 = r12.mContentLengthSize
            int r6 = r6.size()
            if (r3 >= r6) goto L_0x00dd
            java.util.List<java.lang.Long> r6 = r12.mContentLengthSize
            java.lang.Object r6 = r6.get(r3)
            java.lang.Long r6 = (java.lang.Long) r6
            long r6 = r6.longValue()
            r12.onWrite(r6)
        L_0x00dd:
            byte[] r6 = CRLF
            r13.write(r6)
            int r3 = r3 + 1
            goto L_0x001c
        L_0x00e6:
            byte[] r1 = DASHDASH
            r13.write(r1)
            okio.ByteString r1 = r12.mBoundary
            r13.write(r1)
            byte[] r1 = DASHDASH
            r13.write(r1)
            byte[] r1 = CRLF
            r13.write(r1)
            if (r14 == 0) goto L_0x0106
            long r13 = r0.size()
            long r1 = r4 + r13
            r0.clear()
            goto L_0x0107
        L_0x0106:
            r1 = r4
        L_0x0107:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.BugReporterMultipartBody.writeOrCountBytes(okio.BufferedSink, boolean):long");
    }

    /* access modifiers changed from: private */
    public static void appendQuotedString(StringBuilder sb, String str) {
        sb.append(Typography.quote);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == 10) {
                sb.append("%0A");
            } else if (charAt == 13) {
                sb.append("%0D");
            } else if (charAt != '\"') {
                sb.append(charAt);
            } else {
                sb.append("%22");
            }
        }
        sb.append(Typography.quote);
    }
}
