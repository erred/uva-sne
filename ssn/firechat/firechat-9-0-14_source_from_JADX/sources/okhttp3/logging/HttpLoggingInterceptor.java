package okhttp3.logging;

import java.io.EOFException;
import java.nio.charset.Charset;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.internal.platform.Platform;
import okio.Buffer;

public final class HttpLoggingInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private volatile Level level;
    private final Logger logger;

    public enum Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    public interface Logger {
        public static final Logger DEFAULT = new Logger() {
            public void log(String str) {
                Platform.get().log(4, str, null);
            }
        };

        void log(String str);
    }

    public HttpLoggingInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLoggingInterceptor(Logger logger2) {
        this.level = Level.NONE;
        this.logger = logger2;
    }

    public HttpLoggingInterceptor setLevel(Level level2) {
        if (level2 == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.level = level2;
        return this;
    }

    public Level getLevel() {
        return this.level;
    }

    /* JADX WARNING: Removed duplicated region for block: B:94:0x0334  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public okhttp3.C3012Response intercept(okhttp3.Interceptor.Chain r18) throws java.io.IOException {
        /*
            r17 = this;
            r1 = r17
            r2 = r18
            okhttp3.logging.HttpLoggingInterceptor$Level r3 = r1.level
            okhttp3.Request r4 = r18.request()
            okhttp3.logging.HttpLoggingInterceptor$Level r5 = okhttp3.logging.HttpLoggingInterceptor.Level.NONE
            if (r3 != r5) goto L_0x0013
            okhttp3.Response r2 = r2.proceed(r4)
            return r2
        L_0x0013:
            okhttp3.logging.HttpLoggingInterceptor$Level r5 = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
            r7 = 1
            if (r3 != r5) goto L_0x001a
            r5 = 1
            goto L_0x001b
        L_0x001a:
            r5 = 0
        L_0x001b:
            if (r5 != 0) goto L_0x0024
            okhttp3.logging.HttpLoggingInterceptor$Level r8 = okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
            if (r3 != r8) goto L_0x0022
            goto L_0x0024
        L_0x0022:
            r3 = 0
            goto L_0x0025
        L_0x0024:
            r3 = 1
        L_0x0025:
            okhttp3.RequestBody r8 = r4.body()
            if (r8 == 0) goto L_0x002c
            goto L_0x002d
        L_0x002c:
            r7 = 0
        L_0x002d:
            okhttp3.Connection r9 = r18.connection()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "--> "
            r10.append(r11)
            java.lang.String r11 = r4.method()
            r10.append(r11)
            r11 = 32
            r10.append(r11)
            okhttp3.HttpUrl r12 = r4.url()
            r10.append(r12)
            if (r9 == 0) goto L_0x0066
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = " "
            r12.append(r13)
            okhttp3.Protocol r9 = r9.protocol()
            r12.append(r9)
            java.lang.String r9 = r12.toString()
            goto L_0x0068
        L_0x0066:
            java.lang.String r9 = ""
        L_0x0068:
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            if (r3 != 0) goto L_0x0090
            if (r7 == 0) goto L_0x0090
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r9)
            java.lang.String r9 = " ("
            r10.append(r9)
            long r12 = r8.contentLength()
            r10.append(r12)
            java.lang.String r9 = "-byte body)"
            r10.append(r9)
            java.lang.String r9 = r10.toString()
        L_0x0090:
            okhttp3.logging.HttpLoggingInterceptor$Logger r10 = r1.logger
            r10.log(r9)
            r9 = -1
            if (r3 == 0) goto L_0x01ed
            if (r7 == 0) goto L_0x00dd
            okhttp3.MediaType r12 = r8.contentType()
            if (r12 == 0) goto L_0x00bb
            okhttp3.logging.HttpLoggingInterceptor$Logger r12 = r1.logger
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Content-Type: "
            r13.append(r14)
            okhttp3.MediaType r14 = r8.contentType()
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r12.log(r13)
        L_0x00bb:
            long r12 = r8.contentLength()
            int r14 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r14 == 0) goto L_0x00dd
            okhttp3.logging.HttpLoggingInterceptor$Logger r12 = r1.logger
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "Content-Length: "
            r13.append(r14)
            long r14 = r8.contentLength()
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r12.log(r13)
        L_0x00dd:
            okhttp3.Headers r12 = r4.headers()
            int r13 = r12.size()
            r14 = 0
        L_0x00e6:
            if (r14 >= r13) goto L_0x011e
            java.lang.String r15 = r12.name(r14)
            java.lang.String r6 = "Content-Type"
            boolean r6 = r6.equalsIgnoreCase(r15)
            if (r6 != 0) goto L_0x0119
            java.lang.String r6 = "Content-Length"
            boolean r6 = r6.equalsIgnoreCase(r15)
            if (r6 != 0) goto L_0x0119
            okhttp3.logging.HttpLoggingInterceptor$Logger r6 = r1.logger
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r15)
            java.lang.String r15 = ": "
            r11.append(r15)
            java.lang.String r15 = r12.value(r14)
            r11.append(r15)
            java.lang.String r11 = r11.toString()
            r6.log(r11)
        L_0x0119:
            int r14 = r14 + 1
            r11 = 32
            goto L_0x00e6
        L_0x011e:
            if (r5 == 0) goto L_0x01d3
            if (r7 != 0) goto L_0x0124
            goto L_0x01d3
        L_0x0124:
            okhttp3.Headers r6 = r4.headers()
            boolean r6 = r1.bodyHasUnknownEncoding(r6)
            if (r6 == 0) goto L_0x014f
            okhttp3.logging.HttpLoggingInterceptor$Logger r6 = r1.logger
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "--> END "
            r7.append(r8)
            java.lang.String r8 = r4.method()
            r7.append(r8)
            java.lang.String r8 = " (encoded body omitted)"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.log(r7)
            goto L_0x01ed
        L_0x014f:
            okio.Buffer r6 = new okio.Buffer
            r6.<init>()
            r8.writeTo(r6)
            java.nio.charset.Charset r7 = UTF8
            okhttp3.MediaType r11 = r8.contentType()
            if (r11 == 0) goto L_0x0165
            java.nio.charset.Charset r7 = UTF8
            java.nio.charset.Charset r7 = r11.charset(r7)
        L_0x0165:
            okhttp3.logging.HttpLoggingInterceptor$Logger r11 = r1.logger
            java.lang.String r12 = ""
            r11.log(r12)
            boolean r11 = isPlaintext(r6)
            if (r11 == 0) goto L_0x01a7
            okhttp3.logging.HttpLoggingInterceptor$Logger r11 = r1.logger
            java.lang.String r6 = r6.readString(r7)
            r11.log(r6)
            okhttp3.logging.HttpLoggingInterceptor$Logger r6 = r1.logger
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r11 = "--> END "
            r7.append(r11)
            java.lang.String r11 = r4.method()
            r7.append(r11)
            java.lang.String r11 = " ("
            r7.append(r11)
            long r11 = r8.contentLength()
            r7.append(r11)
            java.lang.String r8 = "-byte body)"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.log(r7)
            goto L_0x01ed
        L_0x01a7:
            okhttp3.logging.HttpLoggingInterceptor$Logger r6 = r1.logger
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r11 = "--> END "
            r7.append(r11)
            java.lang.String r11 = r4.method()
            r7.append(r11)
            java.lang.String r11 = " (binary "
            r7.append(r11)
            long r11 = r8.contentLength()
            r7.append(r11)
            java.lang.String r8 = "-byte body omitted)"
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.log(r7)
            goto L_0x01ed
        L_0x01d3:
            okhttp3.logging.HttpLoggingInterceptor$Logger r6 = r1.logger
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "--> END "
            r7.append(r8)
            java.lang.String r8 = r4.method()
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r6.log(r7)
        L_0x01ed:
            long r6 = java.lang.System.nanoTime()
            okhttp3.Response r2 = r2.proceed(r4)     // Catch:{ Exception -> 0x03e0 }
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.NANOSECONDS
            long r11 = java.lang.System.nanoTime()
            long r13 = r11 - r6
            long r6 = r4.toMillis(r13)
            okhttp3.ResponseBody r4 = r2.body()
            long r11 = r4.contentLength()
            int r8 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r8 == 0) goto L_0x021f
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r11)
            java.lang.String r9 = "-byte"
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            goto L_0x0221
        L_0x021f:
            java.lang.String r8 = "unknown-length"
        L_0x0221:
            okhttp3.logging.HttpLoggingInterceptor$Logger r9 = r1.logger
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r13 = "<-- "
            r10.append(r13)
            int r13 = r2.code()
            r10.append(r13)
            java.lang.String r13 = r2.message()
            boolean r13 = r13.isEmpty()
            if (r13 == 0) goto L_0x0243
            java.lang.String r13 = ""
            r14 = 32
            goto L_0x0258
        L_0x0243:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r14 = 32
            r13.append(r14)
            java.lang.String r15 = r2.message()
            r13.append(r15)
            java.lang.String r13 = r13.toString()
        L_0x0258:
            r10.append(r13)
            r10.append(r14)
            okhttp3.Request r13 = r2.request()
            okhttp3.HttpUrl r13 = r13.url()
            r10.append(r13)
            java.lang.String r13 = " ("
            r10.append(r13)
            r10.append(r6)
            java.lang.String r6 = "ms"
            r10.append(r6)
            if (r3 != 0) goto L_0x028f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = ", "
            r6.append(r7)
            r6.append(r8)
            java.lang.String r7 = " body"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            goto L_0x0291
        L_0x028f:
            java.lang.String r6 = ""
        L_0x0291:
            r10.append(r6)
            r6 = 41
            r10.append(r6)
            java.lang.String r6 = r10.toString()
            r9.log(r6)
            if (r3 == 0) goto L_0x03df
            okhttp3.Headers r3 = r2.headers()
            int r6 = r3.size()
            r7 = 0
        L_0x02ab:
            if (r7 >= r6) goto L_0x02d1
            okhttp3.logging.HttpLoggingInterceptor$Logger r8 = r1.logger
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = r3.name(r7)
            r9.append(r10)
            java.lang.String r10 = ": "
            r9.append(r10)
            java.lang.String r10 = r3.value(r7)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.log(r9)
            int r7 = r7 + 1
            goto L_0x02ab
        L_0x02d1:
            if (r5 == 0) goto L_0x03d8
            boolean r5 = okhttp3.internal.http.HttpHeaders.hasBody(r2)
            if (r5 != 0) goto L_0x02db
            goto L_0x03d8
        L_0x02db:
            okhttp3.Headers r5 = r2.headers()
            boolean r5 = r1.bodyHasUnknownEncoding(r5)
            if (r5 == 0) goto L_0x02ee
            okhttp3.logging.HttpLoggingInterceptor$Logger r3 = r1.logger
            java.lang.String r4 = "<-- END HTTP (encoded body omitted)"
            r3.log(r4)
            goto L_0x03df
        L_0x02ee:
            okio.BufferedSource r5 = r4.source()
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r5.request(r6)
            okio.Buffer r5 = r5.buffer()
            java.lang.String r6 = "gzip"
            java.lang.String r7 = "Content-Encoding"
            java.lang.String r3 = r3.get(r7)
            boolean r3 = r6.equalsIgnoreCase(r3)
            r6 = 0
            if (r3 == 0) goto L_0x0338
            long r7 = r5.size()
            java.lang.Long r3 = java.lang.Long.valueOf(r7)
            okio.GzipSource r7 = new okio.GzipSource     // Catch:{ all -> 0x0330 }
            okio.Buffer r5 = r5.clone()     // Catch:{ all -> 0x0330 }
            r7.<init>(r5)     // Catch:{ all -> 0x0330 }
            okio.Buffer r5 = new okio.Buffer     // Catch:{ all -> 0x032c }
            r5.<init>()     // Catch:{ all -> 0x032c }
            r5.writeAll(r7)     // Catch:{ all -> 0x032c }
            if (r7 == 0) goto L_0x0339
            r7.close()
            goto L_0x0339
        L_0x032c:
            r0 = move-exception
            r2 = r0
            r6 = r7
            goto L_0x0332
        L_0x0330:
            r0 = move-exception
            r2 = r0
        L_0x0332:
            if (r6 == 0) goto L_0x0337
            r6.close()
        L_0x0337:
            throw r2
        L_0x0338:
            r3 = r6
        L_0x0339:
            java.nio.charset.Charset r6 = UTF8
            okhttp3.MediaType r4 = r4.contentType()
            if (r4 == 0) goto L_0x0347
            java.nio.charset.Charset r6 = UTF8
            java.nio.charset.Charset r6 = r4.charset(r6)
        L_0x0347:
            boolean r4 = isPlaintext(r5)
            if (r4 != 0) goto L_0x0374
            okhttp3.logging.HttpLoggingInterceptor$Logger r3 = r1.logger
            java.lang.String r4 = ""
            r3.log(r4)
            okhttp3.logging.HttpLoggingInterceptor$Logger r3 = r1.logger
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "<-- END HTTP (binary "
            r4.append(r6)
            long r5 = r5.size()
            r4.append(r5)
            java.lang.String r5 = "-byte body omitted)"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.log(r4)
            return r2
        L_0x0374:
            r7 = 0
            int r4 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r4 == 0) goto L_0x038e
            okhttp3.logging.HttpLoggingInterceptor$Logger r4 = r1.logger
            java.lang.String r7 = ""
            r4.log(r7)
            okhttp3.logging.HttpLoggingInterceptor$Logger r4 = r1.logger
            okio.Buffer r7 = r5.clone()
            java.lang.String r6 = r7.readString(r6)
            r4.log(r6)
        L_0x038e:
            if (r3 == 0) goto L_0x03b8
            okhttp3.logging.HttpLoggingInterceptor$Logger r4 = r1.logger
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "<-- END HTTP ("
            r6.append(r7)
            long r7 = r5.size()
            r6.append(r7)
            java.lang.String r5 = "-byte, "
            r6.append(r5)
            r6.append(r3)
            java.lang.String r3 = "-gzipped-byte body)"
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            r4.log(r3)
            goto L_0x03df
        L_0x03b8:
            okhttp3.logging.HttpLoggingInterceptor$Logger r3 = r1.logger
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "<-- END HTTP ("
            r4.append(r6)
            long r5 = r5.size()
            r4.append(r5)
            java.lang.String r5 = "-byte body)"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.log(r4)
            goto L_0x03df
        L_0x03d8:
            okhttp3.logging.HttpLoggingInterceptor$Logger r3 = r1.logger
            java.lang.String r4 = "<-- END HTTP"
            r3.log(r4)
        L_0x03df:
            return r2
        L_0x03e0:
            r0 = move-exception
            okhttp3.logging.HttpLoggingInterceptor$Logger r2 = r1.logger
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "<-- HTTP FAILED: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r2.log(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.logging.HttpLoggingInterceptor.intercept(okhttp3.Interceptor$Chain):okhttp3.Response");
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer buffer2 = new Buffer();
            buffer.copyTo(buffer2, 0, buffer.size() < 64 ? buffer.size() : 64);
            int i = 0;
            while (true) {
                if (i >= 16) {
                    break;
                } else if (buffer2.exhausted()) {
                    break;
                } else {
                    int readUtf8CodePoint = buffer2.readUtf8CodePoint();
                    if (Character.isISOControl(readUtf8CodePoint) && !Character.isWhitespace(readUtf8CodePoint)) {
                        return false;
                    }
                    i++;
                }
            }
            return true;
        } catch (EOFException unused) {
            return false;
        }
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String str = headers.get("Content-Encoding");
        return str != null && !str.equalsIgnoreCase(InterpolationAnimatedNode.EXTRAPOLATE_TYPE_IDENTITY) && !str.equalsIgnoreCase("gzip");
    }
}
