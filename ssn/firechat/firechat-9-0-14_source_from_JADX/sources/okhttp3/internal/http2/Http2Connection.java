package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.Protocol;
import okhttp3.internal.NamedRunnable;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;

public final class Http2Connection implements Closeable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int OKHTTP_CLIENT_WINDOW_SIZE = 16777216;
    /* access modifiers changed from: private */
    public static final ExecutorService listenerExecutor;
    /* access modifiers changed from: private */
    public boolean awaitingPong;
    long bytesLeftInWriteWindow;
    final boolean client;
    final Set<Integer> currentPushRequests = new LinkedHashSet();
    final String hostname;
    int lastGoodStreamId;
    final Listener listener;
    int nextStreamId;
    Settings okHttpSettings = new Settings();
    final Settings peerSettings = new Settings();
    private final ExecutorService pushExecutor;
    final PushObserver pushObserver;
    final ReaderRunnable readerRunnable;
    boolean receivedInitialPeerSettings = false;
    boolean shutdown;
    final Socket socket;
    final Map<Integer, Http2Stream> streams = new LinkedHashMap();
    long unacknowledgedBytesRead = 0;
    final Http2Writer writer;
    /* access modifiers changed from: private */
    public final ScheduledExecutorService writerExecutor;

    public static class Builder {
        boolean client;
        String hostname;
        Listener listener = Listener.REFUSE_INCOMING_STREAMS;
        int pingIntervalMillis;
        PushObserver pushObserver = PushObserver.CANCEL;
        BufferedSink sink;
        Socket socket;
        BufferedSource source;

        public Builder(boolean z) {
            this.client = z;
        }

        public Builder socket(Socket socket2) throws IOException {
            return socket(socket2, ((InetSocketAddress) socket2.getRemoteSocketAddress()).getHostName(), Okio.buffer(Okio.source(socket2)), Okio.buffer(Okio.sink(socket2)));
        }

        public Builder socket(Socket socket2, String str, BufferedSource bufferedSource, BufferedSink bufferedSink) {
            this.socket = socket2;
            this.hostname = str;
            this.source = bufferedSource;
            this.sink = bufferedSink;
            return this;
        }

        public Builder listener(Listener listener2) {
            this.listener = listener2;
            return this;
        }

        public Builder pushObserver(PushObserver pushObserver2) {
            this.pushObserver = pushObserver2;
            return this;
        }

        public Builder pingIntervalMillis(int i) {
            this.pingIntervalMillis = i;
            return this;
        }

        public Http2Connection build() {
            return new Http2Connection(this);
        }
    }

    public static abstract class Listener {
        public static final Listener REFUSE_INCOMING_STREAMS = new Listener() {
            public void onStream(Http2Stream http2Stream) throws IOException {
                http2Stream.close(ErrorCode.REFUSED_STREAM);
            }
        };

        public void onSettings(Http2Connection http2Connection) {
        }

        public abstract void onStream(Http2Stream http2Stream) throws IOException;
    }

    final class PingRunnable extends NamedRunnable {
        final int payload1;
        final int payload2;
        final boolean reply;

        PingRunnable(boolean z, int i, int i2) {
            super("OkHttp %s ping %08x%08x", Http2Connection.this.hostname, Integer.valueOf(i), Integer.valueOf(i2));
            this.reply = z;
            this.payload1 = i;
            this.payload2 = i2;
        }

        public void execute() {
            Http2Connection.this.writePing(this.reply, this.payload1, this.payload2);
        }
    }

    class ReaderRunnable extends NamedRunnable implements Handler {
        final Http2Reader reader;

        public void ackSettings() {
        }

        public void alternateService(int i, String str, ByteString byteString, String str2, int i2, long j) {
        }

        public void priority(int i, int i2, int i3, boolean z) {
        }

        ReaderRunnable(Http2Reader http2Reader) {
            super("OkHttp %s", Http2Connection.this.hostname);
            this.reader = http2Reader;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Can't wrap try/catch for region: R(5:12|11|14|15|(7:16|17|18|19|20|21|23)) */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x001c, code lost:
            r2 = th;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x001e */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void execute() {
            /*
                r5 = this;
                okhttp3.internal.http2.ErrorCode r0 = okhttp3.internal.http2.ErrorCode.INTERNAL_ERROR
                okhttp3.internal.http2.ErrorCode r1 = okhttp3.internal.http2.ErrorCode.INTERNAL_ERROR
                okhttp3.internal.http2.Http2Reader r2 = r5.reader     // Catch:{ IOException -> 0x001e }
                r2.readConnectionPreface(r5)     // Catch:{ IOException -> 0x001e }
            L_0x0009:
                okhttp3.internal.http2.Http2Reader r2 = r5.reader     // Catch:{ IOException -> 0x001e }
                r3 = 0
                boolean r2 = r2.nextFrame(r3, r5)     // Catch:{ IOException -> 0x001e }
                if (r2 == 0) goto L_0x0013
                goto L_0x0009
            L_0x0013:
                okhttp3.internal.http2.ErrorCode r2 = okhttp3.internal.http2.ErrorCode.NO_ERROR     // Catch:{ IOException -> 0x001e }
                okhttp3.internal.http2.ErrorCode r0 = okhttp3.internal.http2.ErrorCode.CANCEL     // Catch:{ IOException -> 0x001a }
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ IOException -> 0x0027 }
                goto L_0x0024
            L_0x001a:
                r0 = r2
                goto L_0x001e
            L_0x001c:
                r2 = move-exception
                goto L_0x0031
            L_0x001e:
                okhttp3.internal.http2.ErrorCode r2 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR     // Catch:{ all -> 0x001c }
                okhttp3.internal.http2.ErrorCode r0 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR     // Catch:{ all -> 0x002d }
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ IOException -> 0x0027 }
            L_0x0024:
                r1.close(r2, r0)     // Catch:{ IOException -> 0x0027 }
            L_0x0027:
                okhttp3.internal.http2.Http2Reader r0 = r5.reader
                okhttp3.internal.Util.closeQuietly(r0)
                return
            L_0x002d:
                r0 = move-exception
                r4 = r2
                r2 = r0
                r0 = r4
            L_0x0031:
                okhttp3.internal.http2.Http2Connection r3 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ IOException -> 0x0036 }
                r3.close(r0, r1)     // Catch:{ IOException -> 0x0036 }
            L_0x0036:
                okhttp3.internal.http2.Http2Reader r0 = r5.reader
                okhttp3.internal.Util.closeQuietly(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.execute():void");
        }

        public void data(boolean z, int i, BufferedSource bufferedSource, int i2) throws IOException {
            if (Http2Connection.this.pushedStream(i)) {
                Http2Connection.this.pushDataLater(i, bufferedSource, i2, z);
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(i);
            if (stream == null) {
                Http2Connection.this.writeSynResetLater(i, ErrorCode.PROTOCOL_ERROR);
                bufferedSource.skip((long) i2);
                return;
            }
            stream.receiveData(bufferedSource, i2);
            if (z) {
                stream.receiveFin();
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0071, code lost:
            r0.receiveHeaders(r13);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0074, code lost:
            if (r10 == false) goto L_0x0079;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0076, code lost:
            r0.receiveFin();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0079, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void headers(boolean r10, int r11, int r12, java.util.List<okhttp3.internal.http2.Header> r13) {
            /*
                r9 = this;
                okhttp3.internal.http2.Http2Connection r12 = okhttp3.internal.http2.Http2Connection.this
                boolean r12 = r12.pushedStream(r11)
                if (r12 == 0) goto L_0x000e
                okhttp3.internal.http2.Http2Connection r12 = okhttp3.internal.http2.Http2Connection.this
                r12.pushHeadersLater(r11, r13, r10)
                return
            L_0x000e:
                okhttp3.internal.http2.Http2Connection r12 = okhttp3.internal.http2.Http2Connection.this
                monitor-enter(r12)
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                okhttp3.internal.http2.Http2Stream r0 = r0.getStream(r11)     // Catch:{ all -> 0x007a }
                if (r0 != 0) goto L_0x0070
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                boolean r0 = r0.shutdown     // Catch:{ all -> 0x007a }
                if (r0 == 0) goto L_0x0021
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                return
            L_0x0021:
                okhttp3.internal.http2.Http2Connection r0 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                int r0 = r0.lastGoodStreamId     // Catch:{ all -> 0x007a }
                if (r11 > r0) goto L_0x0029
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                return
            L_0x0029:
                int r0 = r11 % 2
                okhttp3.internal.http2.Http2Connection r1 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                int r1 = r1.nextStreamId     // Catch:{ all -> 0x007a }
                r2 = 2
                int r1 = r1 % r2
                if (r0 != r1) goto L_0x0035
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                return
            L_0x0035:
                okhttp3.internal.http2.Http2Stream r0 = new okhttp3.internal.http2.Http2Stream     // Catch:{ all -> 0x007a }
                okhttp3.internal.http2.Http2Connection r5 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                r6 = 0
                r3 = r0
                r4 = r11
                r7 = r10
                r8 = r13
                r3.<init>(r4, r5, r6, r7, r8)     // Catch:{ all -> 0x007a }
                okhttp3.internal.http2.Http2Connection r10 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                r10.lastGoodStreamId = r11     // Catch:{ all -> 0x007a }
                okhttp3.internal.http2.Http2Connection r10 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r10 = r10.streams     // Catch:{ all -> 0x007a }
                java.lang.Integer r13 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x007a }
                r10.put(r13, r0)     // Catch:{ all -> 0x007a }
                java.util.concurrent.ExecutorService r10 = okhttp3.internal.http2.Http2Connection.listenerExecutor     // Catch:{ all -> 0x007a }
                okhttp3.internal.http2.Http2Connection$ReaderRunnable$1 r13 = new okhttp3.internal.http2.Http2Connection$ReaderRunnable$1     // Catch:{ all -> 0x007a }
                java.lang.String r1 = "OkHttp %s stream %d"
                java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x007a }
                r3 = 0
                okhttp3.internal.http2.Http2Connection r4 = okhttp3.internal.http2.Http2Connection.this     // Catch:{ all -> 0x007a }
                java.lang.String r4 = r4.hostname     // Catch:{ all -> 0x007a }
                r2[r3] = r4     // Catch:{ all -> 0x007a }
                r3 = 1
                java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x007a }
                r2[r3] = r11     // Catch:{ all -> 0x007a }
                r13.<init>(r1, r2, r0)     // Catch:{ all -> 0x007a }
                r10.execute(r13)     // Catch:{ all -> 0x007a }
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                return
            L_0x0070:
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                r0.receiveHeaders(r13)
                if (r10 == 0) goto L_0x0079
                r0.receiveFin()
            L_0x0079:
                return
            L_0x007a:
                r10 = move-exception
                monitor-exit(r12)     // Catch:{ all -> 0x007a }
                throw r10
            */
            throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.ReaderRunnable.headers(boolean, int, int, java.util.List):void");
        }

        public void rstStream(int i, ErrorCode errorCode) {
            if (Http2Connection.this.pushedStream(i)) {
                Http2Connection.this.pushResetLater(i, errorCode);
                return;
            }
            Http2Stream removeStream = Http2Connection.this.removeStream(i);
            if (removeStream != null) {
                removeStream.receiveRstStream(errorCode);
            }
        }

        public void settings(boolean z, Settings settings) {
            Http2Stream[] http2StreamArr;
            long j;
            int i;
            synchronized (Http2Connection.this) {
                int initialWindowSize = Http2Connection.this.peerSettings.getInitialWindowSize();
                if (z) {
                    Http2Connection.this.peerSettings.clear();
                }
                Http2Connection.this.peerSettings.merge(settings);
                applyAndAckSettings(settings);
                int initialWindowSize2 = Http2Connection.this.peerSettings.getInitialWindowSize();
                http2StreamArr = null;
                if (initialWindowSize2 == -1 || initialWindowSize2 == initialWindowSize) {
                    j = 0;
                } else {
                    j = (long) (initialWindowSize2 - initialWindowSize);
                    if (!Http2Connection.this.receivedInitialPeerSettings) {
                        Http2Connection.this.addBytesToWriteWindow(j);
                        Http2Connection.this.receivedInitialPeerSettings = true;
                    }
                    if (!Http2Connection.this.streams.isEmpty()) {
                        http2StreamArr = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                    }
                }
                Http2Connection.listenerExecutor.execute(new NamedRunnable("OkHttp %s settings", Http2Connection.this.hostname) {
                    public void execute() {
                        Http2Connection.this.listener.onSettings(Http2Connection.this);
                    }
                });
            }
            if (http2StreamArr != null && j != 0) {
                for (Http2Stream http2Stream : http2StreamArr) {
                    synchronized (http2Stream) {
                        http2Stream.addBytesToWriteWindow(j);
                    }
                }
            }
        }

        private void applyAndAckSettings(final Settings settings) {
            try {
                Http2Connection.this.writerExecutor.execute(new NamedRunnable("OkHttp %s ACK Settings", new Object[]{Http2Connection.this.hostname}) {
                    public void execute() {
                        try {
                            Http2Connection.this.writer.applyAndAckSettings(settings);
                        } catch (IOException unused) {
                            Http2Connection.this.failConnection();
                        }
                    }
                });
            } catch (RejectedExecutionException unused) {
            }
        }

        public void ping(boolean z, int i, int i2) {
            if (z) {
                synchronized (Http2Connection.this) {
                    Http2Connection.this.awaitingPong = false;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            try {
                Http2Connection.this.writerExecutor.execute(new PingRunnable(true, i, i2));
            } catch (RejectedExecutionException unused) {
            }
        }

        public void goAway(int i, ErrorCode errorCode, ByteString byteString) {
            Http2Stream[] http2StreamArr;
            byteString.size();
            synchronized (Http2Connection.this) {
                http2StreamArr = (Http2Stream[]) Http2Connection.this.streams.values().toArray(new Http2Stream[Http2Connection.this.streams.size()]);
                Http2Connection.this.shutdown = true;
            }
            for (Http2Stream http2Stream : http2StreamArr) {
                if (http2Stream.getId() > i && http2Stream.isLocallyInitiated()) {
                    http2Stream.receiveRstStream(ErrorCode.REFUSED_STREAM);
                    Http2Connection.this.removeStream(http2Stream.getId());
                }
            }
        }

        public void windowUpdate(int i, long j) {
            if (i == 0) {
                synchronized (Http2Connection.this) {
                    Http2Connection.this.bytesLeftInWriteWindow += j;
                    Http2Connection.this.notifyAll();
                }
                return;
            }
            Http2Stream stream = Http2Connection.this.getStream(i);
            if (stream != null) {
                synchronized (stream) {
                    stream.addBytesToWriteWindow(j);
                }
            }
        }

        public void pushPromise(int i, int i2, List<Header> list) {
            Http2Connection.this.pushRequestLater(i2, list);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean pushedStream(int i) {
        return i != 0 && (i & 1) == 0;
    }

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp Http2Connection", true));
        listenerExecutor = threadPoolExecutor;
    }

    Http2Connection(Builder builder) {
        Builder builder2 = builder;
        this.pushObserver = builder2.pushObserver;
        this.client = builder2.client;
        this.listener = builder2.listener;
        this.nextStreamId = builder2.client ? 1 : 2;
        if (builder2.client) {
            this.nextStreamId += 2;
        }
        if (builder2.client) {
            this.okHttpSettings.set(7, 16777216);
        }
        this.hostname = builder2.hostname;
        this.writerExecutor = new ScheduledThreadPoolExecutor(1, Util.threadFactory(Util.format("OkHttp %s Writer", this.hostname), false));
        if (builder2.pingIntervalMillis != 0) {
            this.writerExecutor.scheduleAtFixedRate(new PingRunnable(false, 0, 0), (long) builder2.pingIntervalMillis, (long) builder2.pingIntervalMillis, TimeUnit.MILLISECONDS);
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(), Util.threadFactory(Util.format("OkHttp %s Push Observer", this.hostname), true));
        this.pushExecutor = threadPoolExecutor;
        this.peerSettings.set(7, 65535);
        this.peerSettings.set(5, 16384);
        this.bytesLeftInWriteWindow = (long) this.peerSettings.getInitialWindowSize();
        this.socket = builder2.socket;
        this.writer = new Http2Writer(builder2.sink, this.client);
        this.readerRunnable = new ReaderRunnable(new Http2Reader(builder2.source, this.client));
    }

    public Protocol getProtocol() {
        return Protocol.HTTP_2;
    }

    public synchronized int openStreamCount() {
        return this.streams.size();
    }

    /* access modifiers changed from: 0000 */
    public synchronized Http2Stream getStream(int i) {
        return (Http2Stream) this.streams.get(Integer.valueOf(i));
    }

    /* access modifiers changed from: 0000 */
    public synchronized Http2Stream removeStream(int i) {
        Http2Stream http2Stream;
        http2Stream = (Http2Stream) this.streams.remove(Integer.valueOf(i));
        notifyAll();
        return http2Stream;
    }

    public synchronized int maxConcurrentStreams() {
        return this.peerSettings.getMaxConcurrentStreams(Integer.MAX_VALUE);
    }

    public Http2Stream pushStream(int i, List<Header> list, boolean z) throws IOException {
        if (!this.client) {
            return newStream(i, list, z);
        }
        throw new IllegalStateException("Client cannot push requests.");
    }

    public Http2Stream newStream(List<Header> list, boolean z) throws IOException {
        return newStream(0, list, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0049  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private okhttp3.internal.http2.Http2Stream newStream(int r11, java.util.List<okhttp3.internal.http2.Header> r12, boolean r13) throws java.io.IOException {
        /*
            r10 = this;
            r6 = r13 ^ 1
            r4 = 0
            okhttp3.internal.http2.Http2Writer r7 = r10.writer
            monitor-enter(r7)
            monitor-enter(r10)     // Catch:{ all -> 0x0078 }
            int r0 = r10.nextStreamId     // Catch:{ all -> 0x0075 }
            r1 = 1073741823(0x3fffffff, float:1.9999999)
            if (r0 <= r1) goto L_0x0013
            okhttp3.internal.http2.ErrorCode r0 = okhttp3.internal.http2.ErrorCode.REFUSED_STREAM     // Catch:{ all -> 0x0075 }
            r10.shutdown(r0)     // Catch:{ all -> 0x0075 }
        L_0x0013:
            boolean r0 = r10.shutdown     // Catch:{ all -> 0x0075 }
            if (r0 == 0) goto L_0x001d
            okhttp3.internal.http2.ConnectionShutdownException r11 = new okhttp3.internal.http2.ConnectionShutdownException     // Catch:{ all -> 0x0075 }
            r11.<init>()     // Catch:{ all -> 0x0075 }
            throw r11     // Catch:{ all -> 0x0075 }
        L_0x001d:
            int r8 = r10.nextStreamId     // Catch:{ all -> 0x0075 }
            int r0 = r10.nextStreamId     // Catch:{ all -> 0x0075 }
            int r0 = r0 + 2
            r10.nextStreamId = r0     // Catch:{ all -> 0x0075 }
            okhttp3.internal.http2.Http2Stream r9 = new okhttp3.internal.http2.Http2Stream     // Catch:{ all -> 0x0075 }
            r0 = r9
            r1 = r8
            r2 = r10
            r3 = r6
            r5 = r12
            r0.<init>(r1, r2, r3, r4, r5)     // Catch:{ all -> 0x0075 }
            if (r13 == 0) goto L_0x0042
            long r0 = r10.bytesLeftInWriteWindow     // Catch:{ all -> 0x0075 }
            r2 = 0
            int r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r13 == 0) goto L_0x0042
            long r0 = r9.bytesLeftInWriteWindow     // Catch:{ all -> 0x0075 }
            int r13 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r13 != 0) goto L_0x0040
            goto L_0x0042
        L_0x0040:
            r13 = 0
            goto L_0x0043
        L_0x0042:
            r13 = 1
        L_0x0043:
            boolean r0 = r9.isOpen()     // Catch:{ all -> 0x0075 }
            if (r0 == 0) goto L_0x0052
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r0 = r10.streams     // Catch:{ all -> 0x0075 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0075 }
            r0.put(r1, r9)     // Catch:{ all -> 0x0075 }
        L_0x0052:
            monitor-exit(r10)     // Catch:{ all -> 0x0075 }
            if (r11 != 0) goto L_0x005b
            okhttp3.internal.http2.Http2Writer r0 = r10.writer     // Catch:{ all -> 0x0078 }
            r0.synStream(r6, r8, r11, r12)     // Catch:{ all -> 0x0078 }
            goto L_0x006c
        L_0x005b:
            boolean r0 = r10.client     // Catch:{ all -> 0x0078 }
            if (r0 == 0) goto L_0x0067
            java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0078 }
            java.lang.String r12 = "client streams shouldn't have associated stream IDs"
            r11.<init>(r12)     // Catch:{ all -> 0x0078 }
            throw r11     // Catch:{ all -> 0x0078 }
        L_0x0067:
            okhttp3.internal.http2.Http2Writer r0 = r10.writer     // Catch:{ all -> 0x0078 }
            r0.pushPromise(r11, r8, r12)     // Catch:{ all -> 0x0078 }
        L_0x006c:
            monitor-exit(r7)     // Catch:{ all -> 0x0078 }
            if (r13 == 0) goto L_0x0074
            okhttp3.internal.http2.Http2Writer r11 = r10.writer
            r11.flush()
        L_0x0074:
            return r9
        L_0x0075:
            r11 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x0075 }
            throw r11     // Catch:{ all -> 0x0078 }
        L_0x0078:
            r11 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x0078 }
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.newStream(int, java.util.List, boolean):okhttp3.internal.http2.Http2Stream");
    }

    /* access modifiers changed from: 0000 */
    public void writeSynReply(int i, boolean z, List<Header> list) throws IOException {
        this.writer.synReply(z, i, list);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(3:26|27|28) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r2 = java.lang.Math.min((int) java.lang.Math.min(r14, r10.bytesLeftInWriteWindow), r10.writer.maxDataLength());
        r6 = (long) r2;
        r10.bytesLeftInWriteWindow -= r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0063, code lost:
        throw new java.io.InterruptedIOException();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:26:0x005e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeData(int r11, boolean r12, okio.Buffer r13, long r14) throws java.io.IOException {
        /*
            r10 = this;
            r0 = 0
            int r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            r3 = 0
            if (r2 != 0) goto L_0x000d
            okhttp3.internal.http2.Http2Writer r14 = r10.writer
            r14.data(r12, r11, r13, r3)
            return
        L_0x000d:
            int r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r2 <= 0) goto L_0x0066
            monitor-enter(r10)
        L_0x0012:
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ InterruptedException -> 0x005e }
            int r2 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r2 > 0) goto L_0x0030
            java.util.Map<java.lang.Integer, okhttp3.internal.http2.Http2Stream> r2 = r10.streams     // Catch:{ InterruptedException -> 0x005e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)     // Catch:{ InterruptedException -> 0x005e }
            boolean r2 = r2.containsKey(r4)     // Catch:{ InterruptedException -> 0x005e }
            if (r2 != 0) goto L_0x002c
            java.io.IOException r11 = new java.io.IOException     // Catch:{ InterruptedException -> 0x005e }
            java.lang.String r12 = "stream closed"
            r11.<init>(r12)     // Catch:{ InterruptedException -> 0x005e }
            throw r11     // Catch:{ InterruptedException -> 0x005e }
        L_0x002c:
            r10.wait()     // Catch:{ InterruptedException -> 0x005e }
            goto L_0x0012
        L_0x0030:
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ all -> 0x005c }
            long r4 = java.lang.Math.min(r14, r4)     // Catch:{ all -> 0x005c }
            int r2 = (int) r4     // Catch:{ all -> 0x005c }
            okhttp3.internal.http2.Http2Writer r4 = r10.writer     // Catch:{ all -> 0x005c }
            int r4 = r4.maxDataLength()     // Catch:{ all -> 0x005c }
            int r2 = java.lang.Math.min(r2, r4)     // Catch:{ all -> 0x005c }
            long r4 = r10.bytesLeftInWriteWindow     // Catch:{ all -> 0x005c }
            long r6 = (long) r2     // Catch:{ all -> 0x005c }
            long r8 = r4 - r6
            r10.bytesLeftInWriteWindow = r8     // Catch:{ all -> 0x005c }
            monitor-exit(r10)     // Catch:{ all -> 0x005c }
            r4 = 0
            long r4 = r14 - r6
            okhttp3.internal.http2.Http2Writer r14 = r10.writer
            if (r12 == 0) goto L_0x0056
            int r15 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r15 != 0) goto L_0x0056
            r15 = 1
            goto L_0x0057
        L_0x0056:
            r15 = 0
        L_0x0057:
            r14.data(r15, r11, r13, r2)
            r14 = r4
            goto L_0x000d
        L_0x005c:
            r11 = move-exception
            goto L_0x0064
        L_0x005e:
            java.io.InterruptedIOException r11 = new java.io.InterruptedIOException     // Catch:{ all -> 0x005c }
            r11.<init>()     // Catch:{ all -> 0x005c }
            throw r11     // Catch:{ all -> 0x005c }
        L_0x0064:
            monitor-exit(r10)     // Catch:{ all -> 0x005c }
            throw r11
        L_0x0066:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.writeData(int, boolean, okio.Buffer, long):void");
    }

    /* access modifiers changed from: 0000 */
    public void addBytesToWriteWindow(long j) {
        this.bytesLeftInWriteWindow += j;
        if (j > 0) {
            notifyAll();
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeSynResetLater(int i, ErrorCode errorCode) {
        try {
            ScheduledExecutorService scheduledExecutorService = this.writerExecutor;
            final int i2 = i;
            final ErrorCode errorCode2 = errorCode;
            C30251 r1 = new NamedRunnable("OkHttp %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) {
                public void execute() {
                    try {
                        Http2Connection.this.writeSynReset(i2, errorCode2);
                    } catch (IOException unused) {
                        Http2Connection.this.failConnection();
                    }
                }
            };
            scheduledExecutorService.execute(r1);
        } catch (RejectedExecutionException unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public void writeSynReset(int i, ErrorCode errorCode) throws IOException {
        this.writer.rstStream(i, errorCode);
    }

    /* access modifiers changed from: 0000 */
    public void writeWindowUpdateLater(int i, long j) {
        try {
            ScheduledExecutorService scheduledExecutorService = this.writerExecutor;
            final int i2 = i;
            final long j2 = j;
            C30262 r1 = new NamedRunnable("OkHttp Window Update %s stream %d", new Object[]{this.hostname, Integer.valueOf(i)}) {
                public void execute() {
                    try {
                        Http2Connection.this.writer.windowUpdate(i2, j2);
                    } catch (IOException unused) {
                        Http2Connection.this.failConnection();
                    }
                }
            };
            scheduledExecutorService.execute(r1);
        } catch (RejectedExecutionException unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public void writePing(boolean z, int i, int i2) {
        boolean z2;
        if (!z) {
            synchronized (this) {
                z2 = this.awaitingPong;
                this.awaitingPong = true;
            }
            if (z2) {
                failConnection();
                return;
            }
        }
        try {
            this.writer.ping(z, i, i2);
        } catch (IOException unused) {
            failConnection();
        }
    }

    /* access modifiers changed from: 0000 */
    public void writePingAndAwaitPong() throws IOException, InterruptedException {
        writePing(false, 1330343787, -257978967);
        awaitPong();
    }

    /* access modifiers changed from: 0000 */
    public synchronized void awaitPong() throws IOException, InterruptedException {
        while (this.awaitingPong) {
            wait();
        }
    }

    public void flush() throws IOException {
        this.writer.flush();
    }

    public void shutdown(ErrorCode errorCode) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (!this.shutdown) {
                    this.shutdown = true;
                    int i = this.lastGoodStreamId;
                    this.writer.goAway(i, errorCode, Util.EMPTY_BYTE_ARRAY);
                }
            }
        }
    }

    public void close() throws IOException {
        close(ErrorCode.NO_ERROR, ErrorCode.CANCEL);
    }

    /* access modifiers changed from: 0000 */
    public void close(ErrorCode errorCode, ErrorCode errorCode2) throws IOException {
        Http2Stream[] http2StreamArr = null;
        try {
            shutdown(errorCode);
            e = null;
        } catch (IOException e) {
            e = e;
        }
        synchronized (this) {
            if (!this.streams.isEmpty()) {
                http2StreamArr = (Http2Stream[]) this.streams.values().toArray(new Http2Stream[this.streams.size()]);
                this.streams.clear();
            }
        }
        if (http2StreamArr != null) {
            for (Http2Stream close : http2StreamArr) {
                try {
                    close.close(errorCode2);
                } catch (IOException e2) {
                    if (e != null) {
                        e = e2;
                    }
                }
            }
        }
        try {
            this.writer.close();
        } catch (IOException e3) {
            if (e == null) {
                e = e3;
            }
        }
        try {
            this.socket.close();
        } catch (IOException e4) {
            e = e4;
        }
        this.writerExecutor.shutdown();
        this.pushExecutor.shutdown();
        if (e != null) {
            throw e;
        }
    }

    /* access modifiers changed from: private */
    public void failConnection() {
        try {
            close(ErrorCode.PROTOCOL_ERROR, ErrorCode.PROTOCOL_ERROR);
        } catch (IOException unused) {
        }
    }

    public void start() throws IOException {
        start(true);
    }

    /* access modifiers changed from: 0000 */
    public void start(boolean z) throws IOException {
        if (z) {
            this.writer.connectionPreface();
            this.writer.settings(this.okHttpSettings);
            int initialWindowSize = this.okHttpSettings.getInitialWindowSize();
            if (initialWindowSize != 65535) {
                this.writer.windowUpdate(0, (long) (initialWindowSize - 65535));
            }
        }
        new Thread(this.readerRunnable).start();
    }

    public void setSettings(Settings settings) throws IOException {
        synchronized (this.writer) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new ConnectionShutdownException();
                }
                this.okHttpSettings.merge(settings);
            }
            this.writer.settings(settings);
        }
    }

    public synchronized boolean isShutdown() {
        return this.shutdown;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r0 = r8.pushExecutor;
        r2 = r8;
        r5 = r9;
        r6 = r10;
        r1 = new okhttp3.internal.http2.Http2Connection.C30273(r2, "OkHttp %s Push Request[%s]", new java.lang.Object[]{r8.hostname, java.lang.Integer.valueOf(r9)});
        r0.execute(r1);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void pushRequestLater(int r9, java.util.List<okhttp3.internal.http2.Header> r10) {
        /*
            r8 = this;
            monitor-enter(r8)
            java.util.Set<java.lang.Integer> r0 = r8.currentPushRequests     // Catch:{ all -> 0x003e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x003e }
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x003e }
            if (r0 == 0) goto L_0x0014
            okhttp3.internal.http2.ErrorCode r10 = okhttp3.internal.http2.ErrorCode.PROTOCOL_ERROR     // Catch:{ all -> 0x003e }
            r8.writeSynResetLater(r9, r10)     // Catch:{ all -> 0x003e }
            monitor-exit(r8)     // Catch:{ all -> 0x003e }
            return
        L_0x0014:
            java.util.Set<java.lang.Integer> r0 = r8.currentPushRequests     // Catch:{ all -> 0x003e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x003e }
            r0.add(r1)     // Catch:{ all -> 0x003e }
            monitor-exit(r8)     // Catch:{ all -> 0x003e }
            java.util.concurrent.ExecutorService r0 = r8.pushExecutor     // Catch:{ RejectedExecutionException -> 0x003d }
            okhttp3.internal.http2.Http2Connection$3 r7 = new okhttp3.internal.http2.Http2Connection$3     // Catch:{ RejectedExecutionException -> 0x003d }
            java.lang.String r3 = "OkHttp %s Push Request[%s]"
            r1 = 2
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch:{ RejectedExecutionException -> 0x003d }
            r1 = 0
            java.lang.String r2 = r8.hostname     // Catch:{ RejectedExecutionException -> 0x003d }
            r4[r1] = r2     // Catch:{ RejectedExecutionException -> 0x003d }
            r1 = 1
            java.lang.Integer r2 = java.lang.Integer.valueOf(r9)     // Catch:{ RejectedExecutionException -> 0x003d }
            r4[r1] = r2     // Catch:{ RejectedExecutionException -> 0x003d }
            r1 = r7
            r2 = r8
            r5 = r9
            r6 = r10
            r1.<init>(r3, r4, r5, r6)     // Catch:{ RejectedExecutionException -> 0x003d }
            r0.execute(r7)     // Catch:{ RejectedExecutionException -> 0x003d }
        L_0x003d:
            return
        L_0x003e:
            r9 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x003e }
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: okhttp3.internal.http2.Http2Connection.pushRequestLater(int, java.util.List):void");
    }

    /* access modifiers changed from: 0000 */
    public void pushHeadersLater(int i, List<Header> list, boolean z) {
        try {
            ExecutorService executorService = this.pushExecutor;
            final int i2 = i;
            final List<Header> list2 = list;
            final boolean z2 = z;
            C30284 r1 = new NamedRunnable("OkHttp %s Push Headers[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
                public void execute() {
                    boolean onHeaders = Http2Connection.this.pushObserver.onHeaders(i2, list2, z2);
                    if (onHeaders) {
                        try {
                            Http2Connection.this.writer.rstStream(i2, ErrorCode.CANCEL);
                        } catch (IOException unused) {
                            return;
                        }
                    }
                    if (onHeaders || z2) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i2));
                        }
                    }
                }
            };
            executorService.execute(r1);
        } catch (RejectedExecutionException unused) {
        }
    }

    /* access modifiers changed from: 0000 */
    public void pushDataLater(int i, BufferedSource bufferedSource, int i2, boolean z) throws IOException {
        final Buffer buffer = new Buffer();
        long j = (long) i2;
        bufferedSource.require(j);
        bufferedSource.read(buffer, j);
        if (buffer.size() != j) {
            StringBuilder sb = new StringBuilder();
            sb.append(buffer.size());
            sb.append(" != ");
            sb.append(i2);
            throw new IOException(sb.toString());
        }
        ExecutorService executorService = this.pushExecutor;
        final int i3 = i;
        final int i4 = i2;
        final boolean z2 = z;
        C30295 r0 = new NamedRunnable("OkHttp %s Push Data[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                try {
                    boolean onData = Http2Connection.this.pushObserver.onData(i3, buffer, i4, z2);
                    if (onData) {
                        Http2Connection.this.writer.rstStream(i3, ErrorCode.CANCEL);
                    }
                    if (onData || z2) {
                        synchronized (Http2Connection.this) {
                            Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i3));
                        }
                    }
                } catch (IOException unused) {
                }
            }
        };
        executorService.execute(r0);
    }

    /* access modifiers changed from: 0000 */
    public void pushResetLater(int i, ErrorCode errorCode) {
        ExecutorService executorService = this.pushExecutor;
        final int i2 = i;
        final ErrorCode errorCode2 = errorCode;
        C30306 r1 = new NamedRunnable("OkHttp %s Push Reset[%s]", new Object[]{this.hostname, Integer.valueOf(i)}) {
            public void execute() {
                Http2Connection.this.pushObserver.onReset(i2, errorCode2);
                synchronized (Http2Connection.this) {
                    Http2Connection.this.currentPushRequests.remove(Integer.valueOf(i2));
                }
            }
        };
        executorService.execute(r1);
    }
}
