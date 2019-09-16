package org.webrtc;

import android.content.Context;
import android.os.SystemClock;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.webrtc.VideoCapturer.CapturerObserver;

public class FileVideoCapturer implements VideoCapturer {
    private static final String TAG = "FileVideoCapturer";
    private CapturerObserver capturerObserver;
    private final TimerTask tickTask = new TimerTask() {
        public void run() {
            FileVideoCapturer.this.tick();
        }
    };
    private final Timer timer = new Timer();
    private final VideoReader videoReader;

    private interface VideoReader {
        void close();

        int getFrameHeight();

        int getFrameWidth();

        byte[] getNextFrame();
    }

    private static class VideoReaderY4M implements VideoReader {
        private static final String TAG = "VideoReaderY4M";
        private static final String Y4M_FRAME_DELIMETER = "FRAME";
        private final int frameHeight;
        private final int frameSize;
        private final int frameWidth;
        private final RandomAccessFile mediaFileStream;
        private final long videoStart;

        public int getFrameWidth() {
            return this.frameWidth;
        }

        public int getFrameHeight() {
            return this.frameHeight;
        }

        public VideoReaderY4M(String str) throws IOException {
            String[] split;
            this.mediaFileStream = new RandomAccessFile(str, "r");
            StringBuilder sb = new StringBuilder();
            while (true) {
                int read = this.mediaFileStream.read();
                if (read == -1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Found end of file before end of header for file: ");
                    sb2.append(str);
                    throw new RuntimeException(sb2.toString());
                } else if (read == 10) {
                    this.videoStart = this.mediaFileStream.getFilePointer();
                    String str2 = "";
                    int i = 0;
                    int i2 = 0;
                    for (String str3 : sb.toString().split("[ ]")) {
                        char charAt = str3.charAt(0);
                        if (charAt == 'C') {
                            str2 = str3.substring(1);
                        } else if (charAt == 'H') {
                            i2 = Integer.parseInt(str3.substring(1));
                        } else if (charAt == 'W') {
                            i = Integer.parseInt(str3.substring(1));
                        }
                    }
                    String str4 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Color space: ");
                    sb3.append(str2);
                    Logging.m314d(str4, sb3.toString());
                    if (!str2.equals("420") && !str2.equals("420mpeg2")) {
                        throw new IllegalArgumentException("Does not support any other color space than I420 or I420mpeg2");
                    } else if (i % 2 == 1 || i2 % 2 == 1) {
                        throw new IllegalArgumentException("Does not support odd width or height");
                    } else {
                        this.frameWidth = i;
                        this.frameHeight = i2;
                        this.frameSize = ((i * i2) * 3) / 2;
                        String str5 = TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("frame dim: (");
                        sb4.append(i);
                        sb4.append(", ");
                        sb4.append(i2);
                        sb4.append(") frameSize: ");
                        sb4.append(this.frameSize);
                        Logging.m314d(str5, sb4.toString());
                        return;
                    }
                } else {
                    sb.append((char) read);
                }
            }
        }

        public byte[] getNextFrame() {
            byte[] bArr = new byte[this.frameSize];
            try {
                byte[] bArr2 = new byte[(Y4M_FRAME_DELIMETER.length() + 1)];
                if (this.mediaFileStream.read(bArr2) < bArr2.length) {
                    this.mediaFileStream.seek(this.videoStart);
                    if (this.mediaFileStream.read(bArr2) < bArr2.length) {
                        throw new RuntimeException("Error looping video");
                    }
                }
                String str = new String(bArr2);
                if (!str.equals("FRAME\n")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Frames should be delimited by FRAME plus newline, found delimter was: '");
                    sb.append(str);
                    sb.append("'");
                    throw new RuntimeException(sb.toString());
                }
                this.mediaFileStream.readFully(bArr);
                byte[] bArr3 = new byte[this.frameSize];
                FileVideoCapturer.nativeI420ToNV21(bArr, this.frameWidth, this.frameHeight, bArr3);
                return bArr3;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            try {
                this.mediaFileStream.close();
            } catch (IOException e) {
                Logging.m316e(TAG, "Problem closing file", e);
            }
        }
    }

    public static native void nativeI420ToNV21(byte[] bArr, int i, int i2, byte[] bArr2);

    public void changeCaptureFormat(int i, int i2, int i3) {
    }

    public boolean isScreencast() {
        return false;
    }

    private int getFrameWidth() {
        return this.videoReader.getFrameWidth();
    }

    private int getFrameHeight() {
        return this.videoReader.getFrameHeight();
    }

    public FileVideoCapturer(String str) throws IOException {
        try {
            this.videoReader = new VideoReaderY4M(str);
        } catch (IOException e) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not open video file: ");
            sb.append(str);
            Logging.m314d(str2, sb.toString());
            throw e;
        }
    }

    private byte[] getNextFrame() {
        return this.videoReader.getNextFrame();
    }

    public void tick() {
        long nanos = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
        this.capturerObserver.onByteBufferFrameCaptured(getNextFrame(), getFrameWidth(), getFrameHeight(), 0, nanos);
    }

    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context context, CapturerObserver capturerObserver2) {
        this.capturerObserver = capturerObserver2;
    }

    public void startCapture(int i, int i2, int i3) {
        this.timer.schedule(this.tickTask, 0, (long) (1000 / i3));
    }

    public void stopCapture() throws InterruptedException {
        this.timer.cancel();
    }

    public void dispose() {
        this.videoReader.close();
    }
}
