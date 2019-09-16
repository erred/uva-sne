package com.opengarden.firechat.matrixsdk.call;

import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;

public class MXCallsManagerListener implements IMXCallsManagerListener {
    public void onCallHangUp(IMXCall iMXCall) {
    }

    public void onIncomingCall(IMXCall iMXCall, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
    }

    public void onOutgoingCall(IMXCall iMXCall) {
    }

    public void onVoipConferenceFinished(String str) {
    }

    public void onVoipConferenceStarted(String str) {
    }
}
