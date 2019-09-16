package okhttp3.internal.cache2;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.ByteString;
import okio.Source;
import okio.Timeout;

final class Relay {
    private static final long FILE_HEADER_SIZE = 32;
    static final ByteString PREFIX_CLEAN = ByteString.encodeUtf8("OkHttp cache v1\n");
    static final ByteString PREFIX_DIRTY = ByteString.encodeUtf8("OkHttp DIRTY :(\n");
    private static final int SOURCE_FILE = 2;
    private static final int SOURCE_UPSTREAM = 1;
    final Buffer buffer = new Buffer();
    final long bufferMaxSize;
    boolean complete;
    RandomAccessFile file;
    private final ByteString metadata;
    int sourceCount;
    Source upstream;
    final Buffer upstreamBuffer = new Buffer();
    long upstreamPos;
    Thread upstreamReader;

    class RelaySource implements Source {
        private FileOperator fileOperator = new FileOperator(Relay.this.file.getChannel());
        private long sourcePos;
        private final Timeout timeout = new Timeout();

        RelaySource() {
        }

        /* JADX INFO: finally extract failed */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
            r14 = r7 - r1.this$0.buffer.size();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0050, code lost:
            if (r1.sourcePos >= r14) goto L_0x0134;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0053, code lost:
            r6 = 2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
            r2 = java.lang.Math.min(r2, r7 - r1.sourcePos);
            r1.this$0.buffer.copyTo(r23, r1.sourcePos - r14, r2);
            r1.sourcePos += r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:82:0x0157, code lost:
            return r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long read(okio.Buffer r23, long r24) throws java.io.IOException {
            /*
                r22 = this;
                r1 = r22
                r2 = r24
                okhttp3.internal.cache2.FileOperator r4 = r1.fileOperator
                if (r4 != 0) goto L_0x0010
                java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
                java.lang.String r3 = "closed"
                r2.<init>(r3)
                throw r2
            L_0x0010:
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r4)
            L_0x0013:
                long r5 = r1.sourcePos     // Catch:{ all -> 0x0158 }
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                long r7 = r7.upstreamPos     // Catch:{ all -> 0x0158 }
                int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                r5 = 2
                r10 = -1
                if (r9 != 0) goto L_0x0041
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                boolean r6 = r6.complete     // Catch:{ all -> 0x0158 }
                if (r6 == 0) goto L_0x0028
                monitor-exit(r4)     // Catch:{ all -> 0x0158 }
                return r10
            L_0x0028:
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                java.lang.Thread r6 = r6.upstreamReader     // Catch:{ all -> 0x0158 }
                if (r6 == 0) goto L_0x0036
                okio.Timeout r5 = r1.timeout     // Catch:{ all -> 0x0158 }
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                r5.waitUntilNotified(r6)     // Catch:{ all -> 0x0158 }
                goto L_0x0013
            L_0x0036:
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                java.lang.Thread r9 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0158 }
                r6.upstreamReader = r9     // Catch:{ all -> 0x0158 }
                r6 = 1
                monitor-exit(r4)     // Catch:{ all -> 0x0158 }
                goto L_0x0054
            L_0x0041:
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                okio.Buffer r6 = r6.buffer     // Catch:{ all -> 0x0158 }
                long r12 = r6.size()     // Catch:{ all -> 0x0158 }
                r6 = 0
                long r14 = r7 - r12
                long r12 = r1.sourcePos     // Catch:{ all -> 0x0158 }
                int r6 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r6 >= 0) goto L_0x0134
                monitor-exit(r4)     // Catch:{ all -> 0x0158 }
                r6 = 2
            L_0x0054:
                r12 = 32
                if (r6 != r5) goto L_0x0074
                long r4 = r1.sourcePos
                long r9 = r7 - r4
                long r8 = java.lang.Math.min(r2, r9)
                okhttp3.internal.cache2.FileOperator r2 = r1.fileOperator
                long r3 = r1.sourcePos
                long r5 = r3 + r12
                r3 = r5
                r5 = r23
                r6 = r8
                r2.read(r3, r5, r6)
                long r2 = r1.sourcePos
                long r4 = r2 + r8
                r1.sourcePos = r4
                return r8
            L_0x0074:
                r4 = 0
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                okio.Source r5 = r5.upstream     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                okio.Buffer r6 = r6.upstreamBuffer     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r9 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                long r14 = r9.bufferMaxSize     // Catch:{ all -> 0x0120 }
                long r5 = r5.read(r6, r14)     // Catch:{ all -> 0x0120 }
                int r9 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
                if (r9 != 0) goto L_0x00a0
                okhttp3.internal.cache2.Relay r2 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                r2.commit(r7)     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r2 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r2)
                okhttp3.internal.cache2.Relay r3 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x009c }
                r3.upstreamReader = r4     // Catch:{ all -> 0x009c }
                okhttp3.internal.cache2.Relay r3 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x009c }
                r3.notifyAll()     // Catch:{ all -> 0x009c }
                monitor-exit(r2)     // Catch:{ all -> 0x009c }
                return r10
            L_0x009c:
                r0 = move-exception
                r3 = r0
                monitor-exit(r2)     // Catch:{ all -> 0x009c }
                throw r3
            L_0x00a0:
                long r2 = java.lang.Math.min(r5, r2)     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r9 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                okio.Buffer r14 = r9.upstreamBuffer     // Catch:{ all -> 0x0120 }
                r16 = 0
                r15 = r23
                r18 = r2
                r14.copyTo(r15, r16, r18)     // Catch:{ all -> 0x0120 }
                long r9 = r1.sourcePos     // Catch:{ all -> 0x0120 }
                r11 = 0
                long r14 = r9 + r2
                r1.sourcePos = r14     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.FileOperator r9 = r1.fileOperator     // Catch:{ all -> 0x0120 }
                r10 = 0
                long r17 = r7 + r12
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                okio.Buffer r7 = r7.upstreamBuffer     // Catch:{ all -> 0x0120 }
                okio.Buffer r19 = r7.clone()     // Catch:{ all -> 0x0120 }
                r16 = r9
                r20 = r5
                r16.write(r17, r19, r20)     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r7 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0120 }
                monitor-enter(r7)     // Catch:{ all -> 0x0120 }
                okhttp3.internal.cache2.Relay r8 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                okio.Buffer r8 = r8.buffer     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r9 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                okio.Buffer r9 = r9.upstreamBuffer     // Catch:{ all -> 0x011c }
                r8.write(r9, r5)     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r8 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                okio.Buffer r8 = r8.buffer     // Catch:{ all -> 0x011c }
                long r8 = r8.size()     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r10 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                long r10 = r10.bufferMaxSize     // Catch:{ all -> 0x011c }
                int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r12 <= 0) goto L_0x0100
                okhttp3.internal.cache2.Relay r8 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                okio.Buffer r8 = r8.buffer     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r9 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                okio.Buffer r9 = r9.buffer     // Catch:{ all -> 0x011c }
                long r9 = r9.size()     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r11 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                long r11 = r11.bufferMaxSize     // Catch:{ all -> 0x011c }
                r13 = 0
                long r13 = r9 - r11
                r8.skip(r13)     // Catch:{ all -> 0x011c }
            L_0x0100:
                okhttp3.internal.cache2.Relay r8 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x011c }
                long r9 = r8.upstreamPos     // Catch:{ all -> 0x011c }
                r11 = 0
                long r11 = r9 + r5
                r8.upstreamPos = r11     // Catch:{ all -> 0x011c }
                monitor-exit(r7)     // Catch:{ all -> 0x011c }
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r5)
                okhttp3.internal.cache2.Relay r6 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0118 }
                r6.upstreamReader = r4     // Catch:{ all -> 0x0118 }
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0118 }
                r4.notifyAll()     // Catch:{ all -> 0x0118 }
                monitor-exit(r5)     // Catch:{ all -> 0x0118 }
                return r2
            L_0x0118:
                r0 = move-exception
                r2 = r0
                monitor-exit(r5)     // Catch:{ all -> 0x0118 }
                throw r2
            L_0x011c:
                r0 = move-exception
                r2 = r0
                monitor-exit(r7)     // Catch:{ all -> 0x011c }
                throw r2     // Catch:{ all -> 0x0120 }
            L_0x0120:
                r0 = move-exception
                r2 = r0
                okhttp3.internal.cache2.Relay r3 = okhttp3.internal.cache2.Relay.this
                monitor-enter(r3)
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0130 }
                r5.upstreamReader = r4     // Catch:{ all -> 0x0130 }
                okhttp3.internal.cache2.Relay r4 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0130 }
                r4.notifyAll()     // Catch:{ all -> 0x0130 }
                monitor-exit(r3)     // Catch:{ all -> 0x0130 }
                throw r2
            L_0x0130:
                r0 = move-exception
                r2 = r0
                monitor-exit(r3)     // Catch:{ all -> 0x0130 }
                throw r2
            L_0x0134:
                long r5 = r1.sourcePos     // Catch:{ all -> 0x0158 }
                r9 = 0
                long r9 = r7 - r5
                long r2 = java.lang.Math.min(r2, r9)     // Catch:{ all -> 0x0158 }
                okhttp3.internal.cache2.Relay r5 = okhttp3.internal.cache2.Relay.this     // Catch:{ all -> 0x0158 }
                okio.Buffer r5 = r5.buffer     // Catch:{ all -> 0x0158 }
                long r6 = r1.sourcePos     // Catch:{ all -> 0x0158 }
                r8 = 0
                long r18 = r6 - r14
                r16 = r5
                r17 = r23
                r20 = r2
                r16.copyTo(r17, r18, r20)     // Catch:{ all -> 0x0158 }
                long r5 = r1.sourcePos     // Catch:{ all -> 0x0158 }
                r7 = 0
                long r7 = r5 + r2
                r1.sourcePos = r7     // Catch:{ all -> 0x0158 }
                monitor-exit(r4)     // Catch:{ all -> 0x0158 }
                return r2
            L_0x0158:
                r0 = move-exception
                r2 = r0
                monitor-exit(r4)     // Catch:{ all -> 0x0158 }
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.cache2.Relay.RelaySource.read(okio.Buffer, long):long");
        }

        public Timeout timeout() {
            return this.timeout;
        }

        public void close() throws IOException {
            if (this.fileOperator != null) {
                Closeable closeable = null;
                this.fileOperator = null;
                synchronized (Relay.this) {
                    Relay.this.sourceCount--;
                    if (Relay.this.sourceCount == 0) {
                        RandomAccessFile randomAccessFile = Relay.this.file;
                        Relay.this.file = null;
                        closeable = randomAccessFile;
                    }
                }
                if (closeable != null) {
                    Util.closeQuietly(closeable);
                }
            }
        }
    }

    private Relay(RandomAccessFile randomAccessFile, Source source, long j, ByteString byteString, long j2) {
        this.file = randomAccessFile;
        this.upstream = source;
        this.complete = source == null;
        this.upstreamPos = j;
        this.metadata = byteString;
        this.bufferMaxSize = j2;
    }

    public static Relay edit(File file2, Source source, ByteString byteString, long j) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        Relay relay = new Relay(randomAccessFile, source, 0, byteString, j);
        randomAccessFile.setLength(0);
        relay.writeHeader(PREFIX_DIRTY, -1, -1);
        return relay;
    }

    public static Relay read(File file2) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
        FileOperator fileOperator = new FileOperator(randomAccessFile.getChannel());
        Buffer buffer2 = new Buffer();
        fileOperator.read(0, buffer2, 32);
        if (!buffer2.readByteString((long) PREFIX_CLEAN.size()).equals(PREFIX_CLEAN)) {
            throw new IOException("unreadable cache file");
        }
        long readLong = buffer2.readLong();
        long readLong2 = buffer2.readLong();
        Buffer buffer3 = new Buffer();
        fileOperator.read(readLong + 32, buffer3, readLong2);
        Relay relay = new Relay(randomAccessFile, null, readLong, buffer3.readByteString(), 0);
        return relay;
    }

    private void writeHeader(ByteString byteString, long j, long j2) throws IOException {
        Buffer buffer2 = new Buffer();
        buffer2.write(byteString);
        buffer2.writeLong(j);
        buffer2.writeLong(j2);
        if (buffer2.size() != 32) {
            throw new IllegalArgumentException();
        }
        new FileOperator(this.file.getChannel()).write(0, buffer2, 32);
    }

    private void writeMetadata(long j) throws IOException {
        Buffer buffer2 = new Buffer();
        buffer2.write(this.metadata);
        new FileOperator(this.file.getChannel()).write(j + 32, buffer2, (long) this.metadata.size());
    }

    /* access modifiers changed from: 0000 */
    public void commit(long j) throws IOException {
        writeMetadata(j);
        this.file.getChannel().force(false);
        writeHeader(PREFIX_CLEAN, j, (long) this.metadata.size());
        this.file.getChannel().force(false);
        synchronized (this) {
            this.complete = true;
        }
        Util.closeQuietly((Closeable) this.upstream);
        this.upstream = null;
    }

    /* access modifiers changed from: 0000 */
    public boolean isClosed() {
        return this.file == null;
    }

    public ByteString metadata() {
        return this.metadata;
    }

    public Source newSource() {
        synchronized (this) {
            if (this.file == null) {
                return null;
            }
            this.sourceCount++;
            return new RelaySource();
        }
    }
}
