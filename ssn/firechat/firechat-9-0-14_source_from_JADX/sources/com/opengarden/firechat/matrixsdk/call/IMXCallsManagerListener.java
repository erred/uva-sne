package com.opengarden.firechat.matrixsdk.call;

import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;

public interface IMXCallsManagerListener {
    void onCallHangUp(IMXCall iMXCall);

    void onIncomingCall(IMXCall iMXCall, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap);

    void onOutgoingCall(IMXCall iMXCall);

    void onVoipConferenceFinished(String str);

    void onVoipConferenceStarted(String str);
}
