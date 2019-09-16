package org.altbeacon.beacon.service.scanner;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.p000v4.app.NotificationCompat;
import java.util.Date;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.startup.StartupBroadcastReceiver;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

@TargetApi(18)
public abstract class CycledLeScanner {
    public static final long ANDROID_N_MAX_SCAN_DURATION_MILLIS = 1800000;
    private static final long ANDROID_N_MIN_SCAN_CYCLE_MILLIS = 6000;
    private static final String TAG = "CycledLeScanner";
    protected boolean mBackgroundFlag = false;
    protected long mBetweenScanPeriod;
    private BluetoothAdapter mBluetoothAdapter;
    protected final BluetoothCrashResolver mBluetoothCrashResolver;
    protected final Context mContext;
    private long mCurrentScanStartTime = 0;
    protected final CycledLeScanCallback mCycledLeScanCallback;
    private volatile boolean mDistinctPacketsDetectedPerScan = false;
    @NonNull
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private long mLastScanCycleEndTime = 0;
    private long mLastScanCycleStartTime = 0;
    private boolean mLongScanForcingEnabled = false;
    protected long mNextScanCycleStartTime = 0;
    protected boolean mRestartNeeded = false;
    private long mScanCycleStopTime = 0;
    private boolean mScanCyclerStarted = false;
    @NonNull
    protected final Handler mScanHandler;
    private long mScanPeriod;
    /* access modifiers changed from: private */
    @NonNull
    public final HandlerThread mScanThread;
    private boolean mScanning;
    private boolean mScanningEnabled = false;
    protected boolean mScanningPaused;
    private PendingIntent mWakeUpOperation = null;

    /* access modifiers changed from: protected */
    public abstract boolean deferScanIfNeeded();

    /* access modifiers changed from: protected */
    public abstract void finishScan();

    /* access modifiers changed from: protected */
    public abstract void startScan();

    /* access modifiers changed from: protected */
    public abstract void stopScan();

    protected CycledLeScanner(Context context, long j, long j2, boolean z, CycledLeScanCallback cycledLeScanCallback, BluetoothCrashResolver bluetoothCrashResolver) {
        this.mScanPeriod = j;
        this.mBetweenScanPeriod = j2;
        this.mContext = context;
        this.mCycledLeScanCallback = cycledLeScanCallback;
        this.mBluetoothCrashResolver = bluetoothCrashResolver;
        this.mBackgroundFlag = z;
        this.mScanThread = new HandlerThread("CycledLeScannerThread");
        this.mScanThread.start();
        this.mScanHandler = new Handler(this.mScanThread.getLooper());
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.altbeacon.beacon.service.scanner.CycledLeScanner createScanner(android.content.Context r13, long r14, long r16, boolean r18, org.altbeacon.beacon.service.scanner.CycledLeScanCallback r19, org.altbeacon.bluetooth.BluetoothCrashResolver r20) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 0
            r2 = 18
            if (r0 >= r2) goto L_0x0012
            java.lang.String r0 = "CycledLeScanner"
            java.lang.String r2 = "Not supported prior to API 18."
            java.lang.Object[] r1 = new java.lang.Object[r1]
            org.altbeacon.beacon.logging.LogManager.m268w(r0, r2, r1)
            r0 = 0
            return r0
        L_0x0012:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            r3 = 1
            if (r0 >= r2) goto L_0x0024
            java.lang.String r0 = "CycledLeScanner"
            java.lang.String r2 = "This is pre Android 5.0.  We are using old scanning APIs"
            java.lang.Object[] r3 = new java.lang.Object[r1]
            org.altbeacon.beacon.logging.LogManager.m264i(r0, r2, r3)
        L_0x0022:
            r3 = 0
            goto L_0x004f
        L_0x0024:
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 26
            if (r0 >= r2) goto L_0x0044
            boolean r0 = org.altbeacon.beacon.BeaconManager.isAndroidLScanningDisabled()
            if (r0 == 0) goto L_0x003a
            java.lang.String r0 = "CycledLeScanner"
            java.lang.String r2 = "This is Android 5.0, but L scanning is disabled. We are using old scanning APIs"
            java.lang.Object[] r3 = new java.lang.Object[r1]
            org.altbeacon.beacon.logging.LogManager.m264i(r0, r2, r3)
            goto L_0x0022
        L_0x003a:
            java.lang.String r0 = "CycledLeScanner"
            java.lang.String r2 = "This is Android 5.0.  We are using new scanning APIs"
            java.lang.Object[] r4 = new java.lang.Object[r1]
            org.altbeacon.beacon.logging.LogManager.m264i(r0, r2, r4)
            goto L_0x004f
        L_0x0044:
            java.lang.String r0 = "CycledLeScanner"
            java.lang.String r2 = "Using Android O scanner"
            java.lang.Object[] r4 = new java.lang.Object[r1]
            org.altbeacon.beacon.logging.LogManager.m264i(r0, r2, r4)
            r1 = 1
            goto L_0x0022
        L_0x004f:
            if (r1 == 0) goto L_0x0062
            org.altbeacon.beacon.service.scanner.CycledLeScannerForAndroidO r0 = new org.altbeacon.beacon.service.scanner.CycledLeScannerForAndroidO
            r4 = r0
            r5 = r13
            r6 = r14
            r8 = r16
            r10 = r18
            r11 = r19
            r12 = r20
            r4.<init>(r5, r6, r8, r10, r11, r12)
            return r0
        L_0x0062:
            if (r3 == 0) goto L_0x0075
            org.altbeacon.beacon.service.scanner.CycledLeScannerForLollipop r0 = new org.altbeacon.beacon.service.scanner.CycledLeScannerForLollipop
            r1 = r0
            r2 = r13
            r3 = r14
            r5 = r16
            r7 = r18
            r8 = r19
            r9 = r20
            r1.<init>(r2, r3, r5, r7, r8, r9)
            return r0
        L_0x0075:
            org.altbeacon.beacon.service.scanner.CycledLeScannerForJellyBeanMr2 r0 = new org.altbeacon.beacon.service.scanner.CycledLeScannerForJellyBeanMr2
            r1 = r0
            r2 = r13
            r3 = r14
            r5 = r16
            r7 = r18
            r8 = r19
            r9 = r20
            r1.<init>(r2, r3, r5, r7, r8, r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.service.scanner.CycledLeScanner.createScanner(android.content.Context, long, long, boolean, org.altbeacon.beacon.service.scanner.CycledLeScanCallback, org.altbeacon.bluetooth.BluetoothCrashResolver):org.altbeacon.beacon.service.scanner.CycledLeScanner");
    }

    public void setLongScanForcingEnabled(boolean z) {
        this.mLongScanForcingEnabled = z;
    }

    @MainThread
    public void setScanPeriods(long j, long j2, boolean z) {
        long j3 = j;
        long j4 = j2;
        boolean z2 = z;
        LogManager.m260d(TAG, "Set scan periods called with %s, %s Background mode must have changed.", Long.valueOf(j), Long.valueOf(j2));
        if (this.mBackgroundFlag != z2) {
            this.mRestartNeeded = true;
        }
        this.mBackgroundFlag = z2;
        this.mScanPeriod = j3;
        this.mBetweenScanPeriod = j4;
        if (this.mBackgroundFlag) {
            LogManager.m260d(TAG, "We are in the background.  Setting wakeup alarm", new Object[0]);
            setWakeUpAlarm();
        } else {
            LogManager.m260d(TAG, "We are not in the background.  Cancelling wakeup alarm", new Object[0]);
            cancelWakeUpAlarm();
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (this.mNextScanCycleStartTime > elapsedRealtime) {
            long j5 = this.mLastScanCycleEndTime + j4;
            if (j5 < this.mNextScanCycleStartTime) {
                this.mNextScanCycleStartTime = j5;
                LogManager.m264i(TAG, "Adjusted nextScanStartTime to be %s", new Date((this.mNextScanCycleStartTime - SystemClock.elapsedRealtime()) + System.currentTimeMillis()));
            }
        }
        if (this.mScanCycleStopTime > elapsedRealtime) {
            long j6 = this.mLastScanCycleStartTime + j3;
            if (j6 < this.mScanCycleStopTime) {
                this.mScanCycleStopTime = j6;
                LogManager.m264i(TAG, "Adjusted scanStopTime to be %s", Long.valueOf(this.mScanCycleStopTime));
            }
        }
    }

    @MainThread
    public void start() {
        LogManager.m260d(TAG, "start called", new Object[0]);
        this.mScanningEnabled = true;
        if (!this.mScanCyclerStarted) {
            scanLeDevice(Boolean.valueOf(true));
        } else {
            LogManager.m260d(TAG, "scanning already started", new Object[0]);
        }
    }

    @MainThread
    public void stop() {
        LogManager.m260d(TAG, "stop called", new Object[0]);
        this.mScanningEnabled = false;
        if (this.mScanCyclerStarted) {
            scanLeDevice(Boolean.valueOf(false));
        } else {
            LogManager.m260d(TAG, "scanning already stopped", new Object[0]);
        }
    }

    @AnyThread
    public boolean getDistinctPacketsDetectedPerScan() {
        return this.mDistinctPacketsDetectedPerScan;
    }

    @AnyThread
    public void setDistinctPacketsDetectedPerScan(boolean z) {
        this.mDistinctPacketsDetectedPerScan = z;
    }

    @MainThread
    public void destroy() {
        LogManager.m260d(TAG, "Destroying", new Object[0]);
        this.mHandler.removeCallbacksAndMessages(null);
        this.mScanHandler.post(new Runnable() {
            @WorkerThread
            public void run() {
                LogManager.m260d(CycledLeScanner.TAG, "Quitting scan thread", new Object[0]);
                CycledLeScanner.this.mScanThread.quit();
            }
        });
    }

    /* access modifiers changed from: protected */
    @MainThread
    public void scanLeDevice(Boolean bool) {
        try {
            this.mScanCyclerStarted = true;
            if (getBluetoothAdapter() == null) {
                LogManager.m262e(TAG, "No Bluetooth adapter.  beaconService cannot scan.", new Object[0]);
            }
            if (!this.mScanningEnabled || !bool.booleanValue()) {
                LogManager.m260d(TAG, "disabling scan", new Object[0]);
                this.mScanning = false;
                this.mScanCyclerStarted = false;
                stopScan();
                this.mCurrentScanStartTime = 0;
                this.mLastScanCycleEndTime = SystemClock.elapsedRealtime();
                this.mHandler.removeCallbacksAndMessages(null);
                finishScanCycle();
            } else if (!deferScanIfNeeded()) {
                LogManager.m260d(TAG, "starting a new scan cycle", new Object[0]);
                if (this.mScanning && !this.mScanningPaused) {
                    if (!this.mRestartNeeded) {
                        String str = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("We are already scanning and have been for ");
                        sb.append(SystemClock.elapsedRealtime() - this.mCurrentScanStartTime);
                        sb.append(" millis");
                        LogManager.m260d(str, sb.toString(), new Object[0]);
                        this.mScanCycleStopTime = SystemClock.elapsedRealtime() + this.mScanPeriod;
                        scheduleScanCycleStop();
                        LogManager.m260d(TAG, "Scan started", new Object[0]);
                    }
                }
                this.mScanning = true;
                this.mScanningPaused = false;
                try {
                    if (getBluetoothAdapter() != null) {
                        if (getBluetoothAdapter().isEnabled()) {
                            if (this.mBluetoothCrashResolver != null && this.mBluetoothCrashResolver.isRecoveryInProgress()) {
                                LogManager.m268w(TAG, "Skipping scan because crash recovery is in progress.", new Object[0]);
                            } else if (this.mScanningEnabled) {
                                if (this.mRestartNeeded) {
                                    this.mRestartNeeded = false;
                                    LogManager.m260d(TAG, "restarting a bluetooth le scan", new Object[0]);
                                } else {
                                    LogManager.m260d(TAG, "starting a new bluetooth le scan", new Object[0]);
                                }
                                try {
                                    if (VERSION.SDK_INT < 23 || checkLocationPermission()) {
                                        this.mCurrentScanStartTime = SystemClock.elapsedRealtime();
                                        startScan();
                                    }
                                } catch (Exception e) {
                                    LogManager.m263e(e, TAG, "Internal Android exception scanning for beacons", new Object[0]);
                                }
                            } else {
                                LogManager.m260d(TAG, "Scanning unnecessary - no monitoring or ranging active.", new Object[0]);
                            }
                            this.mLastScanCycleStartTime = SystemClock.elapsedRealtime();
                        } else {
                            LogManager.m260d(TAG, "Bluetooth is disabled.  Cannot scan for beacons.", new Object[0]);
                        }
                    }
                } catch (Exception e2) {
                    LogManager.m263e(e2, TAG, "Exception starting Bluetooth scan.  Perhaps Bluetooth is disabled or unavailable?", new Object[0]);
                }
                this.mScanCycleStopTime = SystemClock.elapsedRealtime() + this.mScanPeriod;
                scheduleScanCycleStop();
                LogManager.m260d(TAG, "Scan started", new Object[0]);
            }
        } catch (SecurityException unused) {
            LogManager.m268w(TAG, "SecurityException working accessing bluetooth.", new Object[0]);
        }
    }

    /* access modifiers changed from: protected */
    @MainThread
    public void scheduleScanCycleStop() {
        long elapsedRealtime = this.mScanCycleStopTime - SystemClock.elapsedRealtime();
        if (!this.mScanningEnabled || elapsedRealtime <= 0) {
            finishScanCycle();
            return;
        }
        LogManager.m260d(TAG, "Waiting to stop scan cycle for another %s milliseconds", Long.valueOf(elapsedRealtime));
        if (this.mBackgroundFlag) {
            setWakeUpAlarm();
        }
        Handler handler = this.mHandler;
        C30682 r1 = new Runnable() {
            @MainThread
            public void run() {
                CycledLeScanner.this.scheduleScanCycleStop();
            }
        };
        long j = 1000;
        if (elapsedRealtime <= 1000) {
            j = elapsedRealtime;
        }
        handler.postDelayed(r1, j);
    }

    @MainThread
    private void finishScanCycle() {
        LogManager.m260d(TAG, "Done with scan cycle", new Object[0]);
        try {
            this.mCycledLeScanCallback.onCycleEnd();
            if (this.mScanning) {
                if (getBluetoothAdapter() != null) {
                    if (getBluetoothAdapter().isEnabled()) {
                        if (this.mDistinctPacketsDetectedPerScan && this.mBetweenScanPeriod == 0) {
                            if (!mustStopScanToPreventAndroidNScanTimeout()) {
                                LogManager.m260d(TAG, "Not stopping scanning.  Device capable of multiple indistinct detections per scan.", new Object[0]);
                                this.mLastScanCycleEndTime = SystemClock.elapsedRealtime();
                            }
                        }
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        if (VERSION.SDK_INT < 24 || this.mBetweenScanPeriod + this.mScanPeriod >= ANDROID_N_MIN_SCAN_CYCLE_MILLIS || elapsedRealtime - this.mLastScanCycleStartTime >= ANDROID_N_MIN_SCAN_CYCLE_MILLIS) {
                            try {
                                LogManager.m260d(TAG, "stopping bluetooth le scan", new Object[0]);
                                finishScan();
                            } catch (Exception e) {
                                LogManager.m269w(e, TAG, "Internal Android exception scanning for beacons", new Object[0]);
                            }
                            this.mLastScanCycleEndTime = SystemClock.elapsedRealtime();
                        } else {
                            String str = TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Not stopping scan because this is Android N and we keep scanning for a minimum of 6 seconds at a time. We will stop in ");
                            sb.append(ANDROID_N_MIN_SCAN_CYCLE_MILLIS - (elapsedRealtime - this.mLastScanCycleStartTime));
                            sb.append(" millisconds.");
                            LogManager.m260d(str, sb.toString(), new Object[0]);
                            this.mLastScanCycleEndTime = SystemClock.elapsedRealtime();
                        }
                    } else {
                        LogManager.m260d(TAG, "Bluetooth is disabled.  Cannot scan for beacons.", new Object[0]);
                        this.mRestartNeeded = true;
                    }
                }
                this.mNextScanCycleStartTime = getNextScanStartTime();
                if (this.mScanningEnabled) {
                    scanLeDevice(Boolean.valueOf(true));
                }
            }
            if (!this.mScanningEnabled) {
                LogManager.m260d(TAG, "Scanning disabled. ", new Object[0]);
                this.mScanCyclerStarted = false;
                cancelWakeUpAlarm();
            }
        } catch (SecurityException unused) {
            LogManager.m268w(TAG, "SecurityException working accessing bluetooth.", new Object[0]);
        }
    }

    /* access modifiers changed from: protected */
    public BluetoothAdapter getBluetoothAdapter() {
        try {
            if (this.mBluetoothAdapter == null) {
                this.mBluetoothAdapter = ((BluetoothManager) this.mContext.getApplicationContext().getSystemService("bluetooth")).getAdapter();
                if (this.mBluetoothAdapter == null) {
                    LogManager.m268w(TAG, "Failed to construct a BluetoothAdapter", new Object[0]);
                }
            }
        } catch (SecurityException unused) {
            LogManager.m262e(TAG, "Cannot consruct bluetooth adapter.  Security Exception", new Object[0]);
        }
        return this.mBluetoothAdapter;
    }

    /* access modifiers changed from: protected */
    public void setWakeUpAlarm() {
        long j = 300000;
        if (300000 < this.mBetweenScanPeriod) {
            j = this.mBetweenScanPeriod;
        }
        if (j < this.mScanPeriod) {
            j = this.mScanPeriod;
        }
        ((AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(2, SystemClock.elapsedRealtime() + j, getWakeUpOperation());
        LogManager.m260d(TAG, "Set a wakeup alarm to go off in %s ms: %s", Long.valueOf(j), getWakeUpOperation());
    }

    /* access modifiers changed from: protected */
    public PendingIntent getWakeUpOperation() {
        if (this.mWakeUpOperation == null) {
            Intent intent = new Intent(this.mContext, StartupBroadcastReceiver.class);
            intent.putExtra("wakeup", true);
            this.mWakeUpOperation = PendingIntent.getBroadcast(this.mContext, 0, intent, 134217728);
        }
        return this.mWakeUpOperation;
    }

    /* access modifiers changed from: protected */
    public void cancelWakeUpAlarm() {
        LogManager.m260d(TAG, "cancel wakeup alarm: %s", this.mWakeUpOperation);
        ((AlarmManager) this.mContext.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(2, Long.MAX_VALUE, getWakeUpOperation());
        LogManager.m260d(TAG, "Set a wakeup alarm to go off in %s ms: %s", Long.valueOf(Long.MAX_VALUE - SystemClock.elapsedRealtime()), getWakeUpOperation());
    }

    private long getNextScanStartTime() {
        if (this.mBetweenScanPeriod == 0) {
            return SystemClock.elapsedRealtime();
        }
        long elapsedRealtime = this.mBetweenScanPeriod - (SystemClock.elapsedRealtime() % (this.mScanPeriod + this.mBetweenScanPeriod));
        LogManager.m260d(TAG, "Normalizing between scan period from %s to %s", Long.valueOf(this.mBetweenScanPeriod), Long.valueOf(elapsedRealtime));
        return SystemClock.elapsedRealtime() + elapsedRealtime;
    }

    private boolean checkLocationPermission() {
        return checkPermission("android.permission.ACCESS_COARSE_LOCATION") || checkPermission("android.permission.ACCESS_FINE_LOCATION");
    }

    private boolean checkPermission(String str) {
        return this.mContext.checkPermission(str, Process.myPid(), Process.myUid()) == 0;
    }

    private boolean mustStopScanToPreventAndroidNScanTimeout() {
        if (VERSION.SDK_INT >= 24 && this.mCurrentScanStartTime > 0 && ((SystemClock.elapsedRealtime() + this.mBetweenScanPeriod) + this.mScanPeriod) - this.mCurrentScanStartTime > 1800000) {
            LogManager.m260d(TAG, "The next scan cycle would go over the Android N max duration.", new Object[0]);
            if (this.mLongScanForcingEnabled) {
                LogManager.m260d(TAG, "Stopping scan to prevent Android N scan timeout.", new Object[0]);
                return true;
            }
            LogManager.m268w(TAG, "Allowing a long running scan to be stopped by the OS.  To prevent this, set longScanForcingEnabled in the AndroidBeaconLibrary.", new Object[0]);
        }
        return false;
    }
}
