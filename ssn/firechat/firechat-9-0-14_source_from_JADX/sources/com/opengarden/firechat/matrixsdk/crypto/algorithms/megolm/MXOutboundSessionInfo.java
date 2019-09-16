package com.opengarden.firechat.matrixsdk.crypto.algorithms.megolm;

import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Iterator;

public class MXOutboundSessionInfo {
    private static final String LOG_TAG = "MXOutboundSessionInfo";
    private final long mCreationTime = System.currentTimeMillis();
    public final String mSessionId;
    public final MXUsersDevicesMap<Integer> mSharedWithDevices = new MXUsersDevicesMap<>();
    public int mUseCount = 0;

    public MXOutboundSessionInfo(String str) {
        this.mSessionId = str;
    }

    public boolean needsRotation(int i, int i2) {
        long currentTimeMillis = System.currentTimeMillis() - this.mCreationTime;
        if (this.mUseCount < i && currentTimeMillis < ((long) i2)) {
            return false;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## needsRotation() : Rotating megolm session after ");
        sb.append(this.mUseCount);
        sb.append(", ");
        sb.append(currentTimeMillis);
        sb.append("ms");
        Log.m209d(str, sb.toString());
        return true;
    }

    public boolean sharedWithTooManyDevices(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        for (String str : this.mSharedWithDevices.getUserIds()) {
            if (mXUsersDevicesMap.getUserDeviceIds(str) == null) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## sharedWithTooManyDevices() : Starting new session because we shared with ");
                sb.append(str);
                Log.m209d(str2, sb.toString());
                return true;
            }
            Iterator it = this.mSharedWithDevices.getUserDeviceIds(str).iterator();
            while (true) {
                if (it.hasNext()) {
                    String str3 = (String) it.next();
                    if (mXUsersDevicesMap.getObject(str3, str) == null) {
                        String str4 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## sharedWithTooManyDevices() : Starting new session because we shared with ");
                        sb2.append(str);
                        sb2.append(":");
                        sb2.append(str3);
                        Log.m209d(str4, sb2.toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
