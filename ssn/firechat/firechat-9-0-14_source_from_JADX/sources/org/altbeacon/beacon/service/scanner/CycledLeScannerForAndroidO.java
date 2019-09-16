package org.altbeacon.beacon.service.scanner;

import android.annotation.TargetApi;
import android.content.Context;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

@TargetApi(26)
class CycledLeScannerForAndroidO extends CycledLeScannerForLollipop {
    private static final String TAG = "CycledLeScannerForAndroidO";

    CycledLeScannerForAndroidO(Context context, long j, long j2, boolean z, CycledLeScanCallback cycledLeScanCallback, BluetoothCrashResolver bluetoothCrashResolver) {
        super(context, j, j2, z, cycledLeScanCallback, bluetoothCrashResolver);
    }
}
