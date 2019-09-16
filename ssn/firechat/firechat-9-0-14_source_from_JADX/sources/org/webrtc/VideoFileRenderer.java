package org.webrtc;

import android.os.Handler;
import android.os.HandlerThread;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.webrtc.EglBase.Context;
import org.webrtc.VideoRenderer.Callbacks;
import org.webrtc.VideoRenderer.I420Frame;

public class VideoFileRenderer implements Callbacks {
    private static final String TAG = "VideoFileRenderer";
    /* access modifiers changed from: private */
    public EglBase eglBase;
    private final Object handlerLock = new Object();
    private final int outputFileHeight;
    private final String outputFileName;
    private final int outputFileWidth;
    private final ByteBuffer outputFrameBuffer;
    private final int outputFrameSize;
    private ArrayList<ByteBuffer> rawFrames = new ArrayList<>();
    /* access modifiers changed from: private */
    public final HandlerThread renderThread;
    private final Handler renderThreadHandler;
    private final FileOutputStream videoOutFile;
    /* access modifiers changed from: private */
    public YuvConverter yuvConverter;

    public static native ByteBuffer nativeCreateNativeByteBuffer(int i);

    public static native void nativeFreeNativeByteBuffer(ByteBuffer byteBuffer);

    public static native void nativeI420Scale(ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2, ByteBuffer byteBuffer3, int i3, int i4, int i5, ByteBuffer byteBuffer4, int i6, int i7);

    public VideoFileRenderer(String str, int i, int i2, final Context context) throws IOException {
        if (i % 2 == 1 || i2 % 2 == 1) {
            throw new IllegalArgumentException("Does not support uneven width or height");
        }
        this.outputFileName = str;
        this.outputFileWidth = i;
        this.outputFileHeight = i2;
        this.outputFrameSize = ((i * i2) * 3) / 2;
        this.outputFrameBuffer = ByteBuffer.allocateDirect(this.outputFrameSize);
        this.videoOutFile = new FileOutputStream(str);
        FileOutputStream fileOutputStream = this.videoOutFile;
        StringBuilder sb = new StringBuilder();
        sb.append("YUV4MPEG2 C420 W");
        sb.append(i);
        sb.append(" H");
        sb.append(i2);
        sb.append(" Ip F30:1 A1:1\n");
        fileOutputStream.write(sb.toString().getBytes());
        this.renderThread = new HandlerThread(TAG);
        this.renderThread.start();
        this.renderThreadHandler = new Handler(this.renderThread.getLooper());
        ThreadUtils.invokeAtFrontUninterruptibly(this.renderThreadHandler, (Runnable) new Runnable() {
            public void run() {
                VideoFileRenderer.this.eglBase = EglBase.create(context, EglBase.CONFIG_PIXEL_BUFFER);
                VideoFileRenderer.this.eglBase.createDummyPbufferSurface();
                VideoFileRenderer.this.eglBase.makeCurrent();
                VideoFileRenderer.this.yuvConverter = new YuvConverter();
            }
        });
    }

    public void renderFrame(final I420Frame i420Frame) {
        this.renderThreadHandler.post(new Runnable() {
            public void run() {
                VideoFileRenderer.this.renderFrameOnRenderThread(i420Frame);
            }
        });
    }

    /* access modifiers changed from: private */
    public void renderFrameOnRenderThread(I420Frame i420Frame) {
        I420Frame i420Frame2 = i420Frame;
        float[] multiplyMatrices = RendererCommon.multiplyMatrices(RendererCommon.rotateTextureMatrix(i420Frame2.samplingMatrix, (float) i420Frame2.rotationDegree), RendererCommon.getLayoutMatrix(false, ((float) i420Frame.rotatedWidth()) / ((float) i420Frame.rotatedHeight()), ((float) this.outputFileWidth) / ((float) this.outputFileHeight)));
        try {
            ByteBuffer nativeCreateNativeByteBuffer = nativeCreateNativeByteBuffer(this.outputFrameSize);
            if (!i420Frame2.yuvFrame) {
                this.yuvConverter.convert(this.outputFrameBuffer, this.outputFileWidth, this.outputFileHeight, this.outputFileWidth, i420Frame2.textureId, multiplyMatrices);
                int i = this.outputFileWidth;
                byte[] array = this.outputFrameBuffer.array();
                int arrayOffset = this.outputFrameBuffer.arrayOffset();
                nativeCreateNativeByteBuffer.put(array, arrayOffset, this.outputFileWidth * this.outputFileHeight);
                for (int i2 = this.outputFileHeight; i2 < (this.outputFileHeight * 3) / 2; i2++) {
                    nativeCreateNativeByteBuffer.put(array, (i2 * i) + arrayOffset, i / 2);
                }
                for (int i3 = this.outputFileHeight; i3 < (this.outputFileHeight * 3) / 2; i3++) {
                    nativeCreateNativeByteBuffer.put(array, (i3 * i) + arrayOffset + (i / 2), i / 2);
                }
            } else {
                nativeI420Scale(i420Frame2.yuvPlanes[0], i420Frame2.yuvStrides[0], i420Frame2.yuvPlanes[1], i420Frame2.yuvStrides[1], i420Frame2.yuvPlanes[2], i420Frame2.yuvStrides[2], i420Frame2.width, i420Frame2.height, this.outputFrameBuffer, this.outputFileWidth, this.outputFileHeight);
                nativeCreateNativeByteBuffer.put(this.outputFrameBuffer.array(), this.outputFrameBuffer.arrayOffset(), this.outputFrameSize);
            }
            nativeCreateNativeByteBuffer.rewind();
            this.rawFrames.add(nativeCreateNativeByteBuffer);
            VideoRenderer.renderFrameDone(i420Frame);
        } catch (Throwable th) {
            Throwable th2 = th;
            VideoRenderer.renderFrameDone(i420Frame);
            throw th2;
        }
    }

    public void release() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.renderThreadHandler.post(new Runnable() {
            public void run() {
                VideoFileRenderer.this.yuvConverter.release();
                VideoFileRenderer.this.eglBase.release();
                VideoFileRenderer.this.renderThread.quit();
                countDownLatch.countDown();
            }
        });
        ThreadUtils.awaitUninterruptibly(countDownLatch);
        try {
            Iterator it = this.rawFrames.iterator();
            while (it.hasNext()) {
                ByteBuffer byteBuffer = (ByteBuffer) it.next();
                this.videoOutFile.write("FRAME\n".getBytes());
                byte[] bArr = new byte[this.outputFrameSize];
                byteBuffer.get(bArr);
                this.videoOutFile.write(bArr);
                nativeFreeNativeByteBuffer(byteBuffer);
            }
            this.videoOutFile.close();
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Video written to disk as ");
            sb.append(this.outputFileName);
            sb.append(". Number frames are ");
            sb.append(this.rawFrames.size());
            sb.append(" and the dimension of the frames are ");
            sb.append(this.outputFileWidth);
            sb.append("x");
            sb.append(this.outputFileHeight);
            sb.append(".");
            Logging.m314d(str, sb.toString());
        } catch (IOException e) {
            Logging.m316e(TAG, "Error writing video to disk", e);
        }
    }
}
