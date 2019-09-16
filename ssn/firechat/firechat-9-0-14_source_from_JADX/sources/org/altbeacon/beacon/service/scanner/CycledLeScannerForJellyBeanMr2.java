package org.altbeacon.beacon.service.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

@TargetApi(18)
public class CycledLeScannerForJellyBeanMr2 extends CycledLeScanner {
    private static final String TAG = "CycledLeScannerForJellyBeanMr2";
    private LeScanCallback leScanCallback;

    public CycledLeScannerForJellyBeanMr2(Context context, long j, long j2, boolean z, CycledLeScanCallback cycledLeScanCallback, BluetoothCrashResolver bluetoothCrashResolver) {
        super(context, j, j2, z, cycledLeScanCallback, bluetoothCrashResolver);
    }

    /* access modifiers changed from: protected */
    public void stopScan() {
        postStopLeScan();
    }

    /* access modifiers changed from: protected */
    public boolean deferScanIfNeeded() {
        long elapsedRealtime = this.mNextScanCycleStartTime - SystemClock.elapsedRealtime();
        if (elapsedRealtime <= 0) {
            return false;
        }
        LogManager.m260d(TAG, "Waiting to start next Bluetooth scan for another %s milliseconds", Long.valueOf(elapsedRealtime));
        if (this.mBackgroundFlag) {
            setWakeUpAlarm();
        }
        Handler handler = this.mHandler;
        C30691 r1 = new Runnable() {
            @MainThread
            public void run() {
                CycledLeScannerForJellyBeanMr2.this.scanLeDevice(Boolean.valueOf(true));
            }
        };
        if (elapsedRealtime > 1000) {
            elapsedRealtime = 1000;
        }
        handler.postDelayed(r1, elapsedRealtime);
        return true;
    }

    /* access modifiers changed from: protected */
    public void startScan() {
        postStartLeScan();
    }

    /* access modifiers changed from: protected */
    public void finishScan() {
        postStopLeScan();
        this.mScanningPaused = true;
    }

    private void postStartLeScan() {
        final BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            final LeScanCallback leScanCallback2 = getLeScanCallback();
            this.mScanHandler.removeCallbacksAndMessages(null);
            this.mScanHandler.post(new Runnable() {
                @WorkerThread
                public void run() {
                    try {
                        bluetoothAdapter.startLeScan(leScanCallback2);
                    } catch (Exception e) {
                        LogManager.m263e(e, CycledLeScannerForJellyBeanMr2.TAG, "Internal Android exception in startLeScan()", new Object[0]);
                    }
                }
            });
        }
    }

    private void postStopLeScan() {
        final BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        if (bluetoothAdapter != null) {
            final LeScanCallback leScanCallback2 = getLeScanCallback();
            this.mScanHandler.removeCallbacksAndMessages(null);
            this.mScanHandler.post(new Runnable() {
                @WorkerThread
                public void run() {
                    try {
                        bluetoothAdapter.stopLeScan(leScanCallback2);
                    } catch (Exception e) {
                        LogManager.m263e(e, CycledLeScannerForJellyBeanMr2.TAG, "Internal Android exception in stopLeScan()", new Object[0]);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public LeScanCallback getLeScanCallback() {
        if (this.leScanCallback == null) {
            this.leScanCallback = new LeScanCallback() {
                public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                    LogManager.m260d(CycledLeScannerForJellyBeanMr2.TAG, "got record", new Object[0]);
                    CycledLeScannerForJellyBeanMr2.this.mCycledLeScanCallback.onLeScan(bluetoothDevice, i, bArr);
                    if (CycledLeScannerForJellyBeanMr2.this.mBluetoothCrashResolver != null) {
                        CycledLeScannerForJellyBeanMr2.this.mBluetoothCrashResolver.notifyScannedDevice(bluetoothDevice, CycledLeScannerForJellyBeanMr2.this.getLeScanCallback());
                    }
                }
            };
        }
        return this.leScanCallback;
    }
}
