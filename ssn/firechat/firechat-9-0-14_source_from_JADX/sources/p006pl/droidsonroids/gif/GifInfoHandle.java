package p006pl.droidsonroids.gif;

import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.RequiresApi;
import android.system.ErrnoException;
import android.system.Os;
import android.view.Surface;
import com.facebook.common.util.UriUtil;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import kotlin.text.Typography;

/* renamed from: pl.droidsonroids.gif.GifInfoHandle */
final class GifInfoHandle {
    private volatile long gifInfoPtr;

    private static native void bindSurface(long j, Surface surface, long[] jArr);

    static native int createTempNativeFileDescriptor() throws GifIOException;

    static native int extractNativeFileDescriptor(FileDescriptor fileDescriptor) throws GifIOException;

    private static native void free(long j);

    private static native long getAllocationByteCount(long j);

    private static native String getComment(long j);

    private static native int getCurrentFrameIndex(long j);

    private static native int getCurrentLoop(long j);

    private static native int getCurrentPosition(long j);

    private static native int getDuration(long j);

    private static native int getFrameDuration(long j, int i);

    private static native int getHeight(long j);

    private static native int getLoopCount(long j);

    private static native long getMetadataByteCount(long j);

    private static native int getNativeErrorCode(long j);

    private static native int getNumberOfFrames(long j);

    private static native long[] getSavedState(long j);

    private static native long getSourceLength(long j);

    private static native int getWidth(long j);

    private static native void glTexImage2D(long j, int i, int i2);

    private static native void glTexSubImage2D(long j, int i, int i2);

    private static native void initTexImageDescriptor(long j);

    private static native boolean isAnimationCompleted(long j);

    private static native boolean isOpaque(long j);

    static native long openByteArray(byte[] bArr) throws GifIOException;

    static native long openDirectByteBuffer(ByteBuffer byteBuffer) throws GifIOException;

    static native long openFile(String str) throws GifIOException;

    static native long openNativeFileDescriptor(int i, long j) throws GifIOException;

    static native long openStream(InputStream inputStream) throws GifIOException;

    private static native void postUnbindSurface(long j);

    private static native long renderFrame(long j, Bitmap bitmap);

    private static native boolean reset(long j);

    private static native long restoreRemainder(long j);

    private static native int restoreSavedState(long j, long[] jArr, Bitmap bitmap);

    private static native void saveRemainder(long j);

    private static native void seekToFrame(long j, int i, Bitmap bitmap);

    private static native void seekToFrameGL(long j, int i);

    private static native void seekToTime(long j, int i, Bitmap bitmap);

    private static native void setLoopCount(long j, char c);

    private static native void setOptions(long j, char c, boolean z);

    private static native void setSpeedFactor(long j, float f);

    private static native void startDecoderThread(long j);

    private static native void stopDecoderThread(long j);

    static {
        LibraryLoader.loadLibrary();
    }

    GifInfoHandle() {
    }

    GifInfoHandle(FileDescriptor fileDescriptor) throws GifIOException {
        this.gifInfoPtr = openFd(fileDescriptor, 0);
    }

    GifInfoHandle(byte[] bArr) throws GifIOException {
        this.gifInfoPtr = openByteArray(bArr);
    }

    GifInfoHandle(ByteBuffer byteBuffer) throws GifIOException {
        this.gifInfoPtr = openDirectByteBuffer(byteBuffer);
    }

    GifInfoHandle(String str) throws GifIOException {
        this.gifInfoPtr = openFile(str);
    }

    GifInfoHandle(InputStream inputStream) throws GifIOException {
        if (!inputStream.markSupported()) {
            throw new IllegalArgumentException("InputStream does not support marking");
        }
        this.gifInfoPtr = openStream(inputStream);
    }

    GifInfoHandle(AssetFileDescriptor assetFileDescriptor) throws IOException {
        try {
            this.gifInfoPtr = openFd(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset());
        } finally {
            try {
                assetFileDescriptor.close();
            } catch (IOException unused) {
            }
        }
    }

    private static long openFd(FileDescriptor fileDescriptor, long j) throws GifIOException {
        int i;
        if (VERSION.SDK_INT > 27) {
            try {
                i = getNativeFileDescriptor(fileDescriptor);
            } catch (Exception e) {
                throw new GifIOException(GifError.OPEN_FAILED.errorCode, e.getMessage());
            }
        } else {
            i = extractNativeFileDescriptor(fileDescriptor);
        }
        return openNativeFileDescriptor(i, j);
    }

    @RequiresApi(21)
    private static int getNativeFileDescriptor(FileDescriptor fileDescriptor) throws GifIOException, ErrnoException {
        try {
            int createTempNativeFileDescriptor = createTempNativeFileDescriptor();
            Os.dup2(fileDescriptor, createTempNativeFileDescriptor);
            return createTempNativeFileDescriptor;
        } finally {
            Os.close(fileDescriptor);
        }
    }

    static GifInfoHandle openUri(ContentResolver contentResolver, Uri uri) throws IOException {
        if (UriUtil.LOCAL_FILE_SCHEME.equals(uri.getScheme())) {
            return new GifInfoHandle(uri.getPath());
        }
        AssetFileDescriptor openAssetFileDescriptor = contentResolver.openAssetFileDescriptor(uri, "r");
        if (openAssetFileDescriptor != null) {
            return new GifInfoHandle(openAssetFileDescriptor);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not open AssetFileDescriptor for ");
        sb.append(uri);
        throw new IOException(sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public synchronized long renderFrame(Bitmap bitmap) {
        return renderFrame(this.gifInfoPtr, bitmap);
    }

    /* access modifiers changed from: 0000 */
    public void bindSurface(Surface surface, long[] jArr) {
        bindSurface(this.gifInfoPtr, surface, jArr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void recycle() {
        free(this.gifInfoPtr);
        this.gifInfoPtr = 0;
    }

    /* access modifiers changed from: 0000 */
    public synchronized long restoreRemainder() {
        return restoreRemainder(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized boolean reset() {
        return reset(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void saveRemainder() {
        saveRemainder(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized String getComment() {
        return getComment(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getLoopCount() {
        return getLoopCount(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void setLoopCount(@IntRange(from = 0, mo107to = 65535) int i) {
        if (i < 0 || i > 65535) {
            throw new IllegalArgumentException("Loop count of range <0, 65535>");
        }
        synchronized (this) {
            setLoopCount(this.gifInfoPtr, (char) i);
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getSourceLength() {
        return getSourceLength(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getNativeErrorCode() {
        return getNativeErrorCode(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void setSpeedFactor(@FloatRange(from = 0.0d, fromInclusive = false) float f) {
        if (f <= 0.0f || Float.isNaN(f)) {
            throw new IllegalArgumentException("Speed factor is not positive");
        }
        if (f < 4.656613E-10f) {
            f = 4.656613E-10f;
        }
        synchronized (this) {
            setSpeedFactor(this.gifInfoPtr, f);
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getDuration() {
        return getDuration(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getCurrentPosition() {
        return getCurrentPosition(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getCurrentFrameIndex() {
        return getCurrentFrameIndex(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getCurrentLoop() {
        return getCurrentLoop(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void seekToTime(@IntRange(from = 0, mo107to = 2147483647L) int i, Bitmap bitmap) {
        seekToTime(this.gifInfoPtr, i, bitmap);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void seekToFrame(@IntRange(from = 0, mo107to = 2147483647L) int i, Bitmap bitmap) {
        seekToFrame(this.gifInfoPtr, i, bitmap);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getAllocationByteCount() {
        return getAllocationByteCount(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getMetadataByteCount() {
        return getMetadataByteCount(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized boolean isRecycled() {
        return this.gifInfoPtr == 0;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            recycle();
        } finally {
            super.finalize();
        }
    }

    /* access modifiers changed from: 0000 */
    public synchronized void postUnbindSurface() {
        postUnbindSurface(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized boolean isAnimationCompleted() {
        return isAnimationCompleted(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long[] getSavedState() {
        return getSavedState(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int restoreSavedState(long[] jArr, Bitmap bitmap) {
        return restoreSavedState(this.gifInfoPtr, jArr, bitmap);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getFrameDuration(@IntRange(from = 0) int i) {
        throwIfFrameIndexOutOfBounds(i);
        return getFrameDuration(this.gifInfoPtr, i);
    }

    /* access modifiers changed from: 0000 */
    public void setOptions(char c, boolean z) {
        setOptions(this.gifInfoPtr, c, z);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getWidth() {
        return getWidth(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getHeight() {
        return getHeight(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized int getNumberOfFrames() {
        return getNumberOfFrames(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public synchronized boolean isOpaque() {
        return isOpaque(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void glTexImage2D(int i, int i2) {
        glTexImage2D(this.gifInfoPtr, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public void glTexSubImage2D(int i, int i2) {
        glTexSubImage2D(this.gifInfoPtr, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public void startDecoderThread() {
        startDecoderThread(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void stopDecoderThread() {
        stopDecoderThread(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void initTexImageDescriptor() {
        initTexImageDescriptor(this.gifInfoPtr);
    }

    /* access modifiers changed from: 0000 */
    public void seekToFrameGL(@IntRange(from = 0) int i) {
        throwIfFrameIndexOutOfBounds(i);
        seekToFrameGL(this.gifInfoPtr, i);
    }

    private void throwIfFrameIndexOutOfBounds(@IntRange(from = 0) int i) {
        int numberOfFrames = getNumberOfFrames(this.gifInfoPtr);
        if (i < 0 || i >= numberOfFrames) {
            StringBuilder sb = new StringBuilder();
            sb.append("Frame index is not in range <0;");
            sb.append(numberOfFrames);
            sb.append(Typography.greater);
            throw new IndexOutOfBoundsException(sb.toString());
        }
    }
}
