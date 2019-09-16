package org.webrtc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.AndroidException;
import android.util.Range;
import android.util.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat.FramerateRange;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;

@TargetApi(21)
public class Camera2Enumerator implements CameraEnumerator {
    private static final double NANO_SECONDS_PER_SECOND = 1.0E9d;
    private static final String TAG = "Camera2Enumerator";
    private static final Map<String, List<CaptureFormat>> cachedSupportedFormats = new HashMap();
    final CameraManager cameraManager;
    final Context context;

    public Camera2Enumerator(Context context2) {
        this.context = context2;
        this.cameraManager = (CameraManager) context2.getSystemService("camera");
    }

    public String[] getDeviceNames() {
        try {
            return this.cameraManager.getCameraIdList();
        } catch (AndroidException e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Camera access exception: ");
            sb.append(e);
            Logging.m315e(str, sb.toString());
            return new String[0];
        }
    }

    public boolean isFrontFacing(String str) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(str);
        return cameraCharacteristics != null && ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() == 0;
    }

    public boolean isBackFacing(String str) {
        CameraCharacteristics cameraCharacteristics = getCameraCharacteristics(str);
        if (cameraCharacteristics == null || ((Integer) cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)).intValue() != 1) {
            return false;
        }
        return true;
    }

    public List<CaptureFormat> getSupportedFormats(String str) {
        return getSupportedFormats(this.context, str);
    }

    public CameraVideoCapturer createCapturer(String str, CameraEventsHandler cameraEventsHandler) {
        return new Camera2Capturer(this.context, str, cameraEventsHandler);
    }

    private CameraCharacteristics getCameraCharacteristics(String str) {
        try {
            return this.cameraManager.getCameraCharacteristics(str);
        } catch (AndroidException e) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Camera access exception: ");
            sb.append(e);
            Logging.m315e(str2, sb.toString());
            return null;
        }
    }

    public static boolean isSupported(Context context2) {
        if (VERSION.SDK_INT < 21) {
            return false;
        }
        CameraManager cameraManager2 = (CameraManager) context2.getSystemService("camera");
        try {
            for (String cameraCharacteristics : cameraManager2.getCameraIdList()) {
                if (((Integer) cameraManager2.getCameraCharacteristics(cameraCharacteristics).get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue() == 2) {
                    return false;
                }
            }
            return true;
        } catch (AndroidException e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Camera access exception: ");
            sb.append(e);
            Logging.m315e(str, sb.toString());
            return false;
        }
    }

    static int getFpsUnitFactor(Range<Integer>[] rangeArr) {
        int i = 1000;
        if (rangeArr.length == 0) {
            return 1000;
        }
        if (((Integer) rangeArr[0].getUpper()).intValue() >= 1000) {
            i = 1;
        }
        return i;
    }

    static List<Size> getSupportedSizes(CameraCharacteristics cameraCharacteristics) {
        StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        int intValue = ((Integer) cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue();
        List<Size> convertSizes = convertSizes(streamConfigurationMap.getOutputSizes(SurfaceTexture.class));
        if (VERSION.SDK_INT >= 22 || intValue != 2) {
            return convertSizes;
        }
        Rect rect = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        ArrayList arrayList = new ArrayList();
        for (Size size : convertSizes) {
            if (rect.width() * size.height == rect.height() * size.width) {
                arrayList.add(size);
            }
        }
        return arrayList;
    }

    static List<CaptureFormat> getSupportedFormats(Context context2, String str) {
        return getSupportedFormats((CameraManager) context2.getSystemService("camera"), str);
    }

    static List<CaptureFormat> getSupportedFormats(CameraManager cameraManager2, String str) {
        long j;
        int i;
        synchronized (cachedSupportedFormats) {
            if (cachedSupportedFormats.containsKey(str)) {
                List<CaptureFormat> list = (List) cachedSupportedFormats.get(str);
                return list;
            }
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Get supported formats for camera index ");
            sb.append(str);
            sb.append(".");
            Logging.m314d(str2, sb.toString());
            long elapsedRealtime = SystemClock.elapsedRealtime();
            try {
                CameraCharacteristics cameraCharacteristics = cameraManager2.getCameraCharacteristics(str);
                StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Range[] rangeArr = (Range[]) cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                List<FramerateRange> convertFramerates = convertFramerates(rangeArr, getFpsUnitFactor(rangeArr));
                List<Size> supportedSizes = getSupportedSizes(cameraCharacteristics);
                int i2 = 0;
                for (FramerateRange framerateRange : convertFramerates) {
                    i2 = Math.max(i2, framerateRange.max);
                }
                ArrayList arrayList = new ArrayList();
                for (Size size : supportedSizes) {
                    try {
                        j = streamConfigurationMap.getOutputMinFrameDuration(SurfaceTexture.class, new Size(size.width, size.height));
                    } catch (Exception unused) {
                        j = 0;
                    }
                    if (j == 0) {
                        i = i2;
                    } else {
                        i = ((int) Math.round(NANO_SECONDS_PER_SECOND / ((double) j))) * 1000;
                    }
                    arrayList.add(new CaptureFormat(size.width, size.height, 0, i));
                    String str3 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Format: ");
                    sb2.append(size.width);
                    sb2.append("x");
                    sb2.append(size.height);
                    sb2.append("@");
                    sb2.append(i);
                    Logging.m314d(str3, sb2.toString());
                }
                cachedSupportedFormats.put(str, arrayList);
                long elapsedRealtime2 = SystemClock.elapsedRealtime();
                String str4 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Get supported formats for camera index ");
                sb3.append(str);
                sb3.append(" done. Time spent: ");
                sb3.append(elapsedRealtime2 - elapsedRealtime);
                sb3.append(" ms.");
                Logging.m314d(str4, sb3.toString());
                return arrayList;
            } catch (Exception e) {
                String str5 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("getCameraCharacteristics(): ");
                sb4.append(e);
                Logging.m315e(str5, sb4.toString());
                return new ArrayList();
            }
        }
    }

    private static List<Size> convertSizes(Size[] sizeArr) {
        ArrayList arrayList = new ArrayList();
        for (Size size : sizeArr) {
            arrayList.add(new Size(size.getWidth(), size.getHeight()));
        }
        return arrayList;
    }

    static List<FramerateRange> convertFramerates(Range<Integer>[] rangeArr, int i) {
        ArrayList arrayList = new ArrayList();
        for (Range<Integer> range : rangeArr) {
            arrayList.add(new FramerateRange(((Integer) range.getLower()).intValue() * i, ((Integer) range.getUpper()).intValue() * i));
        }
        return arrayList;
    }
}
