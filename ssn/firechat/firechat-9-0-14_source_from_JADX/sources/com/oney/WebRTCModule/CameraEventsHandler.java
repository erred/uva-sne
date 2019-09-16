package com.oney.WebRTCModule;

import android.util.Log;

class CameraEventsHandler implements org.webrtc.CameraVideoCapturer.CameraEventsHandler {
    private static final String TAG = C1267WebRTCModule.TAG;

    CameraEventsHandler() {
    }

    public void onCameraClosed() {
        Log.d(TAG, "CameraEventsHandler.onFirstFrameAvailable");
    }

    public void onCameraDisconnected() {
        Log.d(TAG, "CameraEventsHandler.onCameraDisconnected");
    }

    public void onCameraError(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("CameraEventsHandler.onCameraError: errorDescription=");
        sb.append(str);
        Log.d(str2, sb.toString());
    }

    public void onCameraFreezed(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("CameraEventsHandler.onCameraFreezed: errorDescription=");
        sb.append(str);
        Log.d(str2, sb.toString());
    }

    public void onCameraOpening(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("CameraEventsHandler.onCameraOpening: cameraName=");
        sb.append(str);
        Log.d(str2, sb.toString());
    }

    public void onFirstFrameAvailable() {
        Log.d(TAG, "CameraEventsHandler.onFirstFrameAvailable");
    }
}
