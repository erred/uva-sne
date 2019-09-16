package org.altbeacon.beacon.service.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import java.util.ArrayList;
import java.util.List;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.DetectionTracker;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

@TargetApi(21)
public class CycledLeScannerForLollipop extends CycledLeScanner {
    private static final long BACKGROUND_L_SCAN_DETECTION_PERIOD_MILLIS = 10000;
    private static final String TAG = "CycledLeScannerForLollipop";
    private ScanCallback leScanCallback;
    private long mBackgroundLScanFirstDetectionTime = 0;
    /* access modifiers changed from: private */
    public long mBackgroundLScanStartTime = 0;
    private final BeaconManager mBeaconManager = BeaconManager.getInstanceForApplication(this.mContext);
    private boolean mMainScanCycleActive = false;
    private BluetoothLeScanner mScanner;

    public CycledLeScannerForLollipop(Context context, long j, long j2, boolean z, CycledLeScanCallback cycledLeScanCallback, BluetoothCrashResolver bluetoothCrashResolver) {
        super(context, j, j2, z, cycledLeScanCallback, bluetoothCrashResolver);
    }

    /* access modifiers changed from: protected */
    public void stopScan() {
        postStopLeScan();
    }

    /* access modifiers changed from: protected */
    public boolean deferScanIfNeeded() {
        long elapsedRealtime = this.mNextScanCycleStartTime - SystemClock.elapsedRealtime();
        boolean z = elapsedRealtime > 0;
        boolean z2 = this.mMainScanCycleActive;
        this.mMainScanCycleActive = !z;
        if (z) {
            long elapsedRealtime2 = SystemClock.elapsedRealtime() - DetectionTracker.getInstance().getLastDetectionTime();
            if (z2) {
                if (elapsedRealtime2 > 10000) {
                    this.mBackgroundLScanStartTime = SystemClock.elapsedRealtime();
                    this.mBackgroundLScanFirstDetectionTime = 0;
                    LogManager.m260d(TAG, "This is Android L. Doing a filtered scan for the background.", new Object[0]);
                    startScan();
                } else {
                    LogManager.m260d(TAG, "This is Android L, but we last saw a beacon only %s ago, so we will not keep scanning in background.", Long.valueOf(elapsedRealtime2));
                }
            }
            if (this.mBackgroundLScanStartTime > 0 && DetectionTracker.getInstance().getLastDetectionTime() > this.mBackgroundLScanStartTime) {
                if (this.mBackgroundLScanFirstDetectionTime == 0) {
                    this.mBackgroundLScanFirstDetectionTime = DetectionTracker.getInstance().getLastDetectionTime();
                }
                if (SystemClock.elapsedRealtime() - this.mBackgroundLScanFirstDetectionTime >= 10000) {
                    LogManager.m260d(TAG, "We've been detecting for a bit.  Stopping Android L background scanning", new Object[0]);
                    stopScan();
                    this.mBackgroundLScanStartTime = 0;
                } else {
                    LogManager.m260d(TAG, "Delivering Android L background scanning results", new Object[0]);
                    this.mCycledLeScanCallback.onCycleEnd();
                }
            }
            LogManager.m260d(TAG, "Waiting to start full Bluetooth scan for another %s milliseconds", Long.valueOf(elapsedRealtime));
            if (z2 && this.mBackgroundFlag) {
                setWakeUpAlarm();
            }
            Handler handler = this.mHandler;
            C30731 r2 = new Runnable() {
                @MainThread
                public void run() {
                    CycledLeScannerForLollipop.this.scanLeDevice(Boolean.valueOf(true));
                }
            };
            if (elapsedRealtime > 1000) {
                elapsedRealtime = 1000;
            }
            handler.postDelayed(r2, elapsedRealtime);
        } else if (this.mBackgroundLScanStartTime > 0) {
            stopScan();
            this.mBackgroundLScanStartTime = 0;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void startScan() {
        ScanSettings scanSettings;
        if (!isBluetoothOn()) {
            LogManager.m260d(TAG, "Not starting scan because bluetooth is off", new Object[0]);
            return;
        }
        List arrayList = new ArrayList();
        if (!this.mBackgroundFlag || this.mMainScanCycleActive) {
            LogManager.m260d(TAG, "starting non-filtered scan in SCAN_MODE_LOW_LATENCY", new Object[0]);
            scanSettings = new Builder().setScanMode(2).build();
        } else {
            LogManager.m260d(TAG, "starting filtered scan in SCAN_MODE_LOW_POWER", new Object[0]);
            ScanSettings build = new Builder().setScanMode(0).build();
            List createScanFiltersForBeaconParsers = new ScanFilterUtils().createScanFiltersForBeaconParsers(this.mBeaconManager.getBeaconParsers());
            scanSettings = build;
            arrayList = createScanFiltersForBeaconParsers;
        }
        postStartLeScan(arrayList, scanSettings);
    }

    /* access modifiers changed from: protected */
    public void finishScan() {
        LogManager.m260d(TAG, "Stopping scan", new Object[0]);
        stopScan();
        this.mScanningPaused = true;
    }

    private void postStartLeScan(List<ScanFilter> list, ScanSettings scanSettings) {
        final BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            final ScanCallback newLeScanCallback = getNewLeScanCallback();
            this.mScanHandler.removeCallbacksAndMessages(null);
            Handler handler = this.mScanHandler;
            final List<ScanFilter> list2 = list;
            final ScanSettings scanSettings2 = scanSettings;
            C30742 r0 = new Runnable() {
                @WorkerThread
                public void run() {
                    try {
                        scanner.startScan(list2, scanSettings2, newLeScanCallback);
                    } catch (IllegalStateException unused) {
                        LogManager.m268w(CycledLeScannerForLollipop.TAG, "Cannot start scan. Bluetooth may be turned off.", new Object[0]);
                    } catch (NullPointerException e) {
                        LogManager.m263e(e, CycledLeScannerForLollipop.TAG, "Cannot start scan. Unexpected NPE.", new Object[0]);
                    } catch (SecurityException unused2) {
                        LogManager.m262e(CycledLeScannerForLollipop.TAG, "Cannot start scan.  Security Exception", new Object[0]);
                    }
                }
            };
            handler.post(r0);
        }
    }

    private void postStopLeScan() {
        if (!isBluetoothOn()) {
            LogManager.m260d(TAG, "Not stopping scan because bluetooth is off", new Object[0]);
            return;
        }
        final BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            final ScanCallback newLeScanCallback = getNewLeScanCallback();
            this.mScanHandler.removeCallbacksAndMessages(null);
            this.mScanHandler.post(new Runnable() {
                @WorkerThread
                public void run() {
                    try {
                        LogManager.m260d(CycledLeScannerForLollipop.TAG, "Stopping LE scan on scan handler", new Object[0]);
                        scanner.stopScan(newLeScanCallback);
                    } catch (IllegalStateException unused) {
                        LogManager.m268w(CycledLeScannerForLollipop.TAG, "Cannot stop scan. Bluetooth may be turned off.", new Object[0]);
                    } catch (NullPointerException e) {
                        LogManager.m263e(e, CycledLeScannerForLollipop.TAG, "Cannot stop scan. Unexpected NPE.", new Object[0]);
                    } catch (SecurityException unused2) {
                        LogManager.m262e(CycledLeScannerForLollipop.TAG, "Cannot stop scan.  Security Exception", new Object[0]);
                    }
                }
            });
        }
    }

    private boolean isBluetoothOn() {
        boolean z = false;
        try {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            if (bluetoothAdapter != null) {
                if (bluetoothAdapter.getState() == 12) {
                    z = true;
                }
                return z;
            }
            LogManager.m268w(TAG, "Cannot get bluetooth adapter", new Object[0]);
            return false;
        } catch (SecurityException unused) {
            LogManager.m268w(TAG, "SecurityException checking if bluetooth is on", new Object[0]);
        }
    }

    private BluetoothLeScanner getScanner() {
        try {
            if (this.mScanner == null) {
                LogManager.m260d(TAG, "Making new Android L scanner", new Object[0]);
                if (getBluetoothAdapter() != null) {
                    this.mScanner = getBluetoothAdapter().getBluetoothLeScanner();
                }
                if (this.mScanner == null) {
                    LogManager.m268w(TAG, "Failed to make new Android L scanner", new Object[0]);
                }
            }
        } catch (SecurityException unused) {
            LogManager.m268w(TAG, "SecurityException making new Android L scanner", new Object[0]);
        }
        return this.mScanner;
    }

    private ScanCallback getNewLeScanCallback() {
        if (this.leScanCallback == null) {
            this.leScanCallback = new ScanCallback() {
                @MainThread
                public void onScanResult(int i, ScanResult scanResult) {
                    if (LogManager.isVerboseLoggingEnabled()) {
                        LogManager.m260d(CycledLeScannerForLollipop.TAG, "got record", new Object[0]);
                        List<ParcelUuid> serviceUuids = scanResult.getScanRecord().getServiceUuids();
                        if (serviceUuids != null) {
                            for (ParcelUuid parcelUuid : serviceUuids) {
                                String str = CycledLeScannerForLollipop.TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("with service uuid: ");
                                sb.append(parcelUuid);
                                LogManager.m260d(str, sb.toString(), new Object[0]);
                            }
                        }
                    }
                    CycledLeScannerForLollipop.this.mCycledLeScanCallback.onLeScan(scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                    if (CycledLeScannerForLollipop.this.mBackgroundLScanStartTime > 0) {
                        LogManager.m260d(CycledLeScannerForLollipop.TAG, "got a filtered scan result in the background.", new Object[0]);
                    }
                }

                @MainThread
                public void onBatchScanResults(List<ScanResult> list) {
                    LogManager.m260d(CycledLeScannerForLollipop.TAG, "got batch records", new Object[0]);
                    for (ScanResult scanResult : list) {
                        CycledLeScannerForLollipop.this.mCycledLeScanCallback.onLeScan(scanResult.getDevice(), scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                    }
                    if (CycledLeScannerForLollipop.this.mBackgroundLScanStartTime > 0) {
                        LogManager.m260d(CycledLeScannerForLollipop.TAG, "got a filtered batch scan result in the background.", new Object[0]);
                    }
                }

                @MainThread
                public void onScanFailed(int i) {
                    switch (i) {
                        case 1:
                            LogManager.m262e(CycledLeScannerForLollipop.TAG, "Scan failed: a BLE scan with the same settings is already started by the app", new Object[0]);
                            return;
                        case 2:
                            LogManager.m262e(CycledLeScannerForLollipop.TAG, "Scan failed: app cannot be registered", new Object[0]);
                            return;
                        case 3:
                            LogManager.m262e(CycledLeScannerForLollipop.TAG, "Scan failed: internal error", new Object[0]);
                            return;
                        case 4:
                            LogManager.m262e(CycledLeScannerForLollipop.TAG, "Scan failed: power optimized scan feature is not supported", new Object[0]);
                            return;
                        default:
                            String str = CycledLeScannerForLollipop.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Scan failed with unknown error (errorCode=");
                            sb.append(i);
                            sb.append(")");
                            LogManager.m262e(str, sb.toString(), new Object[0]);
                            return;
                    }
                }
            };
        }
        return this.leScanCallback;
    }
}
