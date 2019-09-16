package org.webrtc;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import java.util.ArrayList;
import java.util.List;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat.FramerateRange;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;

public class Camera1Enumerator implements CameraEnumerator {
    private static final String TAG = "Camera1Enumerator";
    private static List<List<CaptureFormat>> cachedSupportedFormats;
    private final boolean captureToTexture;

    public Camera1Enumerator() {
        this(true);
    }

    public Camera1Enumerator(boolean z) {
        this.captureToTexture = z;
    }

    public String[] getDeviceNames() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            String deviceName = getDeviceName(i);
            if (deviceName != null) {
                arrayList.add(deviceName);
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Index: ");
                sb.append(i);
                sb.append(". ");
                sb.append(deviceName);
                Logging.m314d(str, sb.toString());
            } else {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Index: ");
                sb2.append(i);
                sb2.append(". Failed to query camera name.");
                Logging.m315e(str2, sb2.toString());
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    public boolean isFrontFacing(String str) {
        CameraInfo cameraInfo = getCameraInfo(getCameraIndex(str));
        if (cameraInfo == null || cameraInfo.facing != 1) {
            return false;
        }
        return true;
    }

    public boolean isBackFacing(String str) {
        CameraInfo cameraInfo = getCameraInfo(getCameraIndex(str));
        return cameraInfo != null && cameraInfo.facing == 0;
    }

    public List<CaptureFormat> getSupportedFormats(String str) {
        return getSupportedFormats(getCameraIndex(str));
    }

    public CameraVideoCapturer createCapturer(String str, CameraEventsHandler cameraEventsHandler) {
        return new Camera1Capturer(str, cameraEventsHandler, this.captureToTexture);
    }

    private static CameraInfo getCameraInfo(int i) {
        CameraInfo cameraInfo = new CameraInfo();
        try {
            Camera.getCameraInfo(i, cameraInfo);
            return cameraInfo;
        } catch (Exception e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getCameraInfo failed on index ");
            sb.append(i);
            Logging.m316e(str, sb.toString(), e);
            return null;
        }
    }

    static synchronized List<CaptureFormat> getSupportedFormats(int i) {
        List<CaptureFormat> list;
        synchronized (Camera1Enumerator.class) {
            if (cachedSupportedFormats == null) {
                cachedSupportedFormats = new ArrayList();
                for (int i2 = 0; i2 < Camera.getNumberOfCameras(); i2++) {
                    cachedSupportedFormats.add(enumerateFormats(i2));
                }
            }
            list = (List) cachedSupportedFormats.get(i);
        }
        return list;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ef  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.List<org.webrtc.CameraEnumerationAndroid.CaptureFormat> enumerateFormats(int r9) {
        /*
            java.lang.String r0 = "Camera1Enumerator"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Get supported formats for camera index "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r2 = "."
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            org.webrtc.Logging.m314d(r0, r1)
            long r0 = android.os.SystemClock.elapsedRealtime()
            r2 = 0
            java.lang.String r3 = "Camera1Enumerator"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00cb }
            r4.<init>()     // Catch:{ RuntimeException -> 0x00cb }
            java.lang.String r5 = "Opening camera with index "
            r4.append(r5)     // Catch:{ RuntimeException -> 0x00cb }
            r4.append(r9)     // Catch:{ RuntimeException -> 0x00cb }
            java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x00cb }
            org.webrtc.Logging.m314d(r3, r4)     // Catch:{ RuntimeException -> 0x00cb }
            android.hardware.Camera r3 = android.hardware.Camera.open(r9)     // Catch:{ RuntimeException -> 0x00cb }
            android.hardware.Camera$Parameters r2 = r3.getParameters()     // Catch:{ RuntimeException -> 0x00c6, all -> 0x00c3 }
            if (r3 == 0) goto L_0x0043
            r3.release()
        L_0x0043:
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.List r4 = r2.getSupportedPreviewFpsRange()     // Catch:{ Exception -> 0x0082 }
            r5 = 0
            if (r4 == 0) goto L_0x0060
            int r6 = r4.size()     // Catch:{ Exception -> 0x0082 }
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r4 = r4.get(r6)     // Catch:{ Exception -> 0x0082 }
            int[] r4 = (int[]) r4     // Catch:{ Exception -> 0x0082 }
            r5 = r4[r5]     // Catch:{ Exception -> 0x0082 }
            r4 = r4[r7]     // Catch:{ Exception -> 0x0082 }
            goto L_0x0061
        L_0x0060:
            r4 = 0
        L_0x0061:
            java.util.List r2 = r2.getSupportedPreviewSizes()     // Catch:{ Exception -> 0x0082 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x0082 }
        L_0x0069:
            boolean r6 = r2.hasNext()     // Catch:{ Exception -> 0x0082 }
            if (r6 == 0) goto L_0x0099
            java.lang.Object r6 = r2.next()     // Catch:{ Exception -> 0x0082 }
            android.hardware.Camera$Size r6 = (android.hardware.Camera.Size) r6     // Catch:{ Exception -> 0x0082 }
            org.webrtc.CameraEnumerationAndroid$CaptureFormat r7 = new org.webrtc.CameraEnumerationAndroid$CaptureFormat     // Catch:{ Exception -> 0x0082 }
            int r8 = r6.width     // Catch:{ Exception -> 0x0082 }
            int r6 = r6.height     // Catch:{ Exception -> 0x0082 }
            r7.<init>(r8, r6, r5, r4)     // Catch:{ Exception -> 0x0082 }
            r3.add(r7)     // Catch:{ Exception -> 0x0082 }
            goto L_0x0069
        L_0x0082:
            r2 = move-exception
            java.lang.String r4 = "Camera1Enumerator"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "getSupportedFormats() failed on camera index "
            r5.append(r6)
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            org.webrtc.Logging.m316e(r4, r5, r2)
        L_0x0099:
            long r4 = android.os.SystemClock.elapsedRealtime()
            java.lang.String r2 = "Camera1Enumerator"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Get supported formats for camera index "
            r6.append(r7)
            r6.append(r9)
            java.lang.String r9 = " done. Time spent: "
            r6.append(r9)
            long r7 = r4 - r0
            r6.append(r7)
            java.lang.String r9 = " ms."
            r6.append(r9)
            java.lang.String r9 = r6.toString()
            org.webrtc.Logging.m314d(r2, r9)
            return r3
        L_0x00c3:
            r9 = move-exception
            r2 = r3
            goto L_0x00ed
        L_0x00c6:
            r0 = move-exception
            r2 = r3
            goto L_0x00cc
        L_0x00c9:
            r9 = move-exception
            goto L_0x00ed
        L_0x00cb:
            r0 = move-exception
        L_0x00cc:
            java.lang.String r1 = "Camera1Enumerator"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c9 }
            r3.<init>()     // Catch:{ all -> 0x00c9 }
            java.lang.String r4 = "Open camera failed on camera index "
            r3.append(r4)     // Catch:{ all -> 0x00c9 }
            r3.append(r9)     // Catch:{ all -> 0x00c9 }
            java.lang.String r9 = r3.toString()     // Catch:{ all -> 0x00c9 }
            org.webrtc.Logging.m316e(r1, r9, r0)     // Catch:{ all -> 0x00c9 }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ all -> 0x00c9 }
            r9.<init>()     // Catch:{ all -> 0x00c9 }
            if (r2 == 0) goto L_0x00ec
            r2.release()
        L_0x00ec:
            return r9
        L_0x00ed:
            if (r2 == 0) goto L_0x00f2
            r2.release()
        L_0x00f2:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.webrtc.Camera1Enumerator.enumerateFormats(int):java.util.List");
    }

    static List<Size> convertSizes(List<Size> list) {
        ArrayList arrayList = new ArrayList();
        for (Size size : list) {
            arrayList.add(new Size(size.width, size.height));
        }
        return arrayList;
    }

    static List<FramerateRange> convertFramerates(List<int[]> list) {
        ArrayList arrayList = new ArrayList();
        for (int[] iArr : list) {
            arrayList.add(new FramerateRange(iArr[0], iArr[1]));
        }
        return arrayList;
    }

    static int getCameraIndex(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getCameraIndex: ");
        sb.append(str);
        Logging.m314d(str2, sb.toString());
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            if (str.equals(getDeviceName(i))) {
                return i;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("No such camera: ");
        sb2.append(str);
        throw new IllegalArgumentException(sb2.toString());
    }

    static String getDeviceName(int i) {
        CameraInfo cameraInfo = getCameraInfo(i);
        if (cameraInfo == null) {
            return null;
        }
        String str = cameraInfo.facing == 1 ? "front" : "back";
        StringBuilder sb = new StringBuilder();
        sb.append("Camera ");
        sb.append(i);
        sb.append(", Facing ");
        sb.append(str);
        sb.append(", Orientation ");
        sb.append(cameraInfo.orientation);
        return sb.toString();
    }
}
