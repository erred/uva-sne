package org.altbeacon.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.SystemClock;
import java.util.HashSet;
import java.util.Set;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.RangedBeacon;

public class BluetoothCrashResolver {
    private static final int BLUEDROID_MAX_BLUETOOTH_MAC_COUNT = 1990;
    private static final int BLUEDROID_POST_DISCOVERY_ESTIMATED_BLUETOOTH_MAC_COUNT = 400;
    private static final String DISTINCT_BLUETOOTH_ADDRESSES_FILE = "BluetoothCrashResolverState.txt";
    private static final long MIN_TIME_BETWEEN_STATE_SAVES_MILLIS = 60000;
    private static final boolean PREEMPTIVE_ACTION_ENABLED = true;
    private static final long SUSPICIOUSLY_SHORT_BLUETOOTH_OFF_INTERVAL_MILLIS = 600;
    private static final String TAG = "BluetoothCrashResolver";
    private static final int TIME_TO_LET_DISCOVERY_RUN_MILLIS = 5000;
    private Context context = null;
    private int detectedCrashCount = 0;
    /* access modifiers changed from: private */
    public boolean discoveryStartConfirmed = false;
    private final Set<String> distinctBluetoothAddresses = new HashSet();
    private long lastBluetoothCrashDetectionTime = 0;
    /* access modifiers changed from: private */
    public long lastBluetoothOffTime = 0;
    /* access modifiers changed from: private */
    public long lastBluetoothTurningOnTime = 0;
    private boolean lastRecoverySucceeded = false;
    private long lastStateSaveTime = 0;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                if (BluetoothCrashResolver.this.recoveryInProgress) {
                    LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth discovery finished", new Object[0]);
                    BluetoothCrashResolver.this.finishRecovery();
                } else {
                    LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth discovery finished (external)", new Object[0]);
                }
            }
            if (action.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                if (BluetoothCrashResolver.this.recoveryInProgress) {
                    BluetoothCrashResolver.this.discoveryStartConfirmed = true;
                    LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth discovery started", new Object[0]);
                } else {
                    LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth discovery started (external)", new Object[0]);
                }
            }
            if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (intExtra != Integer.MIN_VALUE) {
                    switch (intExtra) {
                        case 10:
                            LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth state is OFF", new Object[0]);
                            BluetoothCrashResolver.this.lastBluetoothOffTime = SystemClock.elapsedRealtime();
                            return;
                        case 11:
                            BluetoothCrashResolver.this.lastBluetoothTurningOnTime = SystemClock.elapsedRealtime();
                            LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth state is TURNING_ON", new Object[0]);
                            return;
                        case 12:
                            LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth state is ON", new Object[0]);
                            LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth was turned off for %s milliseconds", Long.valueOf(BluetoothCrashResolver.this.lastBluetoothTurningOnTime - BluetoothCrashResolver.this.lastBluetoothOffTime));
                            if (BluetoothCrashResolver.this.lastBluetoothTurningOnTime - BluetoothCrashResolver.this.lastBluetoothOffTime < BluetoothCrashResolver.SUSPICIOUSLY_SHORT_BLUETOOTH_OFF_INTERVAL_MILLIS) {
                                BluetoothCrashResolver.this.crashDetected();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    LogManager.m260d(BluetoothCrashResolver.TAG, "Bluetooth state is ERROR", new Object[0]);
                }
            }
        }
    };
    private int recoveryAttemptCount = 0;
    /* access modifiers changed from: private */
    public boolean recoveryInProgress = false;
    private UpdateNotifier updateNotifier;

    public interface UpdateNotifier {
        void dataUpdated();
    }

    private int getCrashRiskDeviceCount() {
        return 1590;
    }

    @Deprecated
    public void disableDebug() {
    }

    @Deprecated
    public void enableDebug() {
    }

    public BluetoothCrashResolver(Context context2) {
        this.context = context2.getApplicationContext();
        LogManager.m260d(TAG, "constructed", new Object[0]);
        loadState();
    }

    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.context.registerReceiver(this.receiver, intentFilter);
        LogManager.m260d(TAG, "started listening for BluetoothAdapter events", new Object[0]);
    }

    public void stop() {
        this.context.unregisterReceiver(this.receiver);
        LogManager.m260d(TAG, "stopped listening for BluetoothAdapter events", new Object[0]);
        saveState();
    }

    @TargetApi(18)
    public void notifyScannedDevice(BluetoothDevice bluetoothDevice, LeScanCallback leScanCallback) {
        int size = this.distinctBluetoothAddresses.size();
        synchronized (this.distinctBluetoothAddresses) {
            this.distinctBluetoothAddresses.add(bluetoothDevice.getAddress());
        }
        int size2 = this.distinctBluetoothAddresses.size();
        if (size != size2 && size2 % 100 == 0) {
            LogManager.m260d(TAG, "Distinct Bluetooth devices seen: %s", Integer.valueOf(this.distinctBluetoothAddresses.size()));
        }
        if (this.distinctBluetoothAddresses.size() > getCrashRiskDeviceCount() && !this.recoveryInProgress) {
            LogManager.m268w(TAG, "Large number of Bluetooth devices detected: %s Proactively attempting to clear out address list to prevent a crash", Integer.valueOf(this.distinctBluetoothAddresses.size()));
            LogManager.m268w(TAG, "Stopping LE Scan", new Object[0]);
            BluetoothAdapter.getDefaultAdapter().stopLeScan(leScanCallback);
            startRecovery();
            processStateChange();
        }
    }

    public void crashDetected() {
        if (VERSION.SDK_INT < 18) {
            LogManager.m260d(TAG, "Ignoring crashes before API 18, because BLE is unsupported.", new Object[0]);
            return;
        }
        LogManager.m268w(TAG, "BluetoothService crash detected", new Object[0]);
        if (this.distinctBluetoothAddresses.size() > 0) {
            LogManager.m260d(TAG, "Distinct Bluetooth devices seen at crash: %s", Integer.valueOf(this.distinctBluetoothAddresses.size()));
        }
        this.lastBluetoothCrashDetectionTime = SystemClock.elapsedRealtime();
        this.detectedCrashCount++;
        if (this.recoveryInProgress) {
            LogManager.m260d(TAG, "Ignoring Bluetooth crash because recovery is already in progress.", new Object[0]);
        } else {
            startRecovery();
        }
        processStateChange();
    }

    public long getLastBluetoothCrashDetectionTime() {
        return this.lastBluetoothCrashDetectionTime;
    }

    public int getDetectedCrashCount() {
        return this.detectedCrashCount;
    }

    public int getRecoveryAttemptCount() {
        return this.recoveryAttemptCount;
    }

    public boolean isLastRecoverySucceeded() {
        return this.lastRecoverySucceeded;
    }

    public boolean isRecoveryInProgress() {
        return this.recoveryInProgress;
    }

    public void setUpdateNotifier(UpdateNotifier updateNotifier2) {
        this.updateNotifier = updateNotifier2;
    }

    public void forceFlush() {
        startRecovery();
        processStateChange();
    }

    private void processStateChange() {
        if (this.updateNotifier != null) {
            this.updateNotifier.dataUpdated();
        }
        if (SystemClock.elapsedRealtime() - this.lastStateSaveTime > 60000) {
            saveState();
        }
    }

    @TargetApi(17)
    private void startRecovery() {
        this.recoveryAttemptCount++;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        LogManager.m260d(TAG, "about to check if discovery is active", new Object[0]);
        if (!defaultAdapter.isDiscovering()) {
            LogManager.m268w(TAG, "Recovery attempt started", new Object[0]);
            this.recoveryInProgress = true;
            this.discoveryStartConfirmed = false;
            LogManager.m260d(TAG, "about to command discovery", new Object[0]);
            if (!defaultAdapter.startDiscovery()) {
                LogManager.m268w(TAG, "Can't start discovery.  Is Bluetooth turned on?", new Object[0]);
            }
            LogManager.m260d(TAG, "startDiscovery commanded.  isDiscovering()=%s", Boolean.valueOf(defaultAdapter.isDiscovering()));
            LogManager.m260d(TAG, "We will be cancelling this discovery in %s milliseconds.", Integer.valueOf(TIME_TO_LET_DISCOVERY_RUN_MILLIS));
            cancelDiscovery();
            return;
        }
        LogManager.m268w(TAG, "Already discovering.  Recovery attempt abandoned.", new Object[0]);
    }

    /* access modifiers changed from: private */
    public void finishRecovery() {
        LogManager.m268w(TAG, "Recovery attempt finished", new Object[0]);
        synchronized (this.distinctBluetoothAddresses) {
            this.distinctBluetoothAddresses.clear();
        }
        this.recoveryInProgress = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00bd A[SYNTHETIC, Splitter:B:40:0x00bd] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void saveState() {
        /*
            r7 = this;
            long r0 = android.os.SystemClock.elapsedRealtime()
            r7.lastStateSaveTime = r0
            r0 = 1
            r1 = 0
            r2 = 0
            android.content.Context r3 = r7.context     // Catch:{ IOException -> 0x0093 }
            java.lang.String r4 = "BluetoothCrashResolverState.txt"
            java.io.FileOutputStream r3 = r3.openFileOutput(r4, r1)     // Catch:{ IOException -> 0x0093 }
            java.io.OutputStreamWriter r4 = new java.io.OutputStreamWriter     // Catch:{ IOException -> 0x0093 }
            r4.<init>(r3)     // Catch:{ IOException -> 0x0093 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.<init>()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            long r5 = r7.lastBluetoothCrashDetectionTime     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.append(r5)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r3 = "\n"
            r2.append(r3)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r4.write(r2)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.<init>()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            int r3 = r7.detectedCrashCount     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.append(r3)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r3 = "\n"
            r2.append(r3)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r4.write(r2)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.<init>()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            int r3 = r7.recoveryAttemptCount     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r2.append(r3)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r3 = "\n"
            r2.append(r3)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            r4.write(r2)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            boolean r2 = r7.lastRecoverySucceeded     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            if (r2 == 0) goto L_0x005f
            java.lang.String r2 = "1\n"
            goto L_0x0061
        L_0x005f:
            java.lang.String r2 = "0\n"
        L_0x0061:
            r4.write(r2)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.util.Set<java.lang.String> r2 = r7.distinctBluetoothAddresses     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            monitor-enter(r2)     // Catch:{ IOException -> 0x008e, all -> 0x008c }
            java.util.Set<java.lang.String> r3 = r7.distinctBluetoothAddresses     // Catch:{ all -> 0x0089 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0089 }
        L_0x006d:
            boolean r5 = r3.hasNext()     // Catch:{ all -> 0x0089 }
            if (r5 == 0) goto L_0x0082
            java.lang.Object r5 = r3.next()     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x0089 }
            r4.write(r5)     // Catch:{ all -> 0x0089 }
            java.lang.String r5 = "\n"
            r4.write(r5)     // Catch:{ all -> 0x0089 }
            goto L_0x006d
        L_0x0082:
            monitor-exit(r2)     // Catch:{ all -> 0x0089 }
            if (r4 == 0) goto L_0x00a5
            r4.close()     // Catch:{ IOException -> 0x00a5 }
            goto L_0x00a5
        L_0x0089:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0089 }
            throw r3     // Catch:{ IOException -> 0x008e, all -> 0x008c }
        L_0x008c:
            r0 = move-exception
            goto L_0x00bb
        L_0x008e:
            r2 = r4
            goto L_0x0093
        L_0x0090:
            r0 = move-exception
            r4 = r2
            goto L_0x00bb
        L_0x0093:
            java.lang.String r3 = "BluetoothCrashResolver"
            java.lang.String r4 = "Can't write macs to %s"
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ all -> 0x0090 }
            java.lang.String r6 = "BluetoothCrashResolverState.txt"
            r5[r1] = r6     // Catch:{ all -> 0x0090 }
            org.altbeacon.beacon.logging.LogManager.m268w(r3, r4, r5)     // Catch:{ all -> 0x0090 }
            if (r2 == 0) goto L_0x00a5
            r2.close()     // Catch:{ IOException -> 0x00a5 }
        L_0x00a5:
            java.lang.String r2 = "BluetoothCrashResolver"
            java.lang.String r3 = "Wrote %s Bluetooth addresses"
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.util.Set<java.lang.String> r4 = r7.distinctBluetoothAddresses
            int r4 = r4.size()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r0[r1] = r4
            org.altbeacon.beacon.logging.LogManager.m260d(r2, r3, r0)
            return
        L_0x00bb:
            if (r4 == 0) goto L_0x00c0
            r4.close()     // Catch:{ IOException -> 0x00c0 }
        L_0x00c0:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.bluetooth.BluetoothCrashResolver.saveState():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0073, code lost:
        if (r2 != null) goto L_0x0075;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0086, code lost:
        if (r2 != null) goto L_0x0075;
     */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00a1 A[SYNTHETIC, Splitter:B:47:0x00a1] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadState() {
        /*
            r7 = this;
            r0 = 1
            r1 = 0
            r2 = 0
            android.content.Context r3 = r7.context     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            java.lang.String r4 = "BluetoothCrashResolverState.txt"
            java.io.FileInputStream r3 = r3.openFileInput(r4)     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            java.io.InputStreamReader r5 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            r5.<init>(r3)     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            r4.<init>(r5)     // Catch:{ IOException -> 0x0079, NumberFormatException -> 0x0066 }
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x0021
            long r2 = java.lang.Long.parseLong(r2)     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            r7.lastBluetoothCrashDetectionTime = r2     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
        L_0x0021:
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x002d
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            r7.detectedCrashCount = r2     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
        L_0x002d:
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x0039
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            r7.recoveryAttemptCount = r2     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
        L_0x0039:
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x004b
            r7.lastRecoverySucceeded = r1     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            java.lang.String r3 = "1"
            boolean r2 = r2.equals(r3)     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x004b
            r7.lastRecoverySucceeded = r0     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
        L_0x004b:
            java.lang.String r2 = r4.readLine()     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            if (r2 == 0) goto L_0x0057
            java.util.Set<java.lang.String> r3 = r7.distinctBluetoothAddresses     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            r3.add(r2)     // Catch:{ IOException -> 0x0062, NumberFormatException -> 0x0060, all -> 0x005d }
            goto L_0x004b
        L_0x0057:
            if (r4 == 0) goto L_0x0089
            r4.close()     // Catch:{ IOException -> 0x0089 }
            goto L_0x0089
        L_0x005d:
            r0 = move-exception
            r2 = r4
            goto L_0x009f
        L_0x0060:
            r2 = r4
            goto L_0x0066
        L_0x0062:
            r2 = r4
            goto L_0x0079
        L_0x0064:
            r0 = move-exception
            goto L_0x009f
        L_0x0066:
            java.lang.String r3 = "BluetoothCrashResolver"
            java.lang.String r4 = "Can't parse file %s"
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ all -> 0x0064 }
            java.lang.String r6 = "BluetoothCrashResolverState.txt"
            r5[r1] = r6     // Catch:{ all -> 0x0064 }
            org.altbeacon.beacon.logging.LogManager.m268w(r3, r4, r5)     // Catch:{ all -> 0x0064 }
            if (r2 == 0) goto L_0x0089
        L_0x0075:
            r2.close()     // Catch:{ IOException -> 0x0089 }
            goto L_0x0089
        L_0x0079:
            java.lang.String r3 = "BluetoothCrashResolver"
            java.lang.String r4 = "Can't read macs from %s"
            java.lang.Object[] r5 = new java.lang.Object[r0]     // Catch:{ all -> 0x0064 }
            java.lang.String r6 = "BluetoothCrashResolverState.txt"
            r5[r1] = r6     // Catch:{ all -> 0x0064 }
            org.altbeacon.beacon.logging.LogManager.m268w(r3, r4, r5)     // Catch:{ all -> 0x0064 }
            if (r2 == 0) goto L_0x0089
            goto L_0x0075
        L_0x0089:
            java.lang.String r2 = "BluetoothCrashResolver"
            java.lang.String r3 = "Read %s Bluetooth addresses"
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.util.Set<java.lang.String> r4 = r7.distinctBluetoothAddresses
            int r4 = r4.size()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r0[r1] = r4
            org.altbeacon.beacon.logging.LogManager.m260d(r2, r3, r0)
            return
        L_0x009f:
            if (r2 == 0) goto L_0x00a4
            r2.close()     // Catch:{ IOException -> 0x00a4 }
        L_0x00a4:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.bluetooth.BluetoothCrashResolver.loadState():void");
    }

    private void cancelDiscovery() {
        try {
            Thread.sleep(RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
            if (!this.discoveryStartConfirmed) {
                LogManager.m268w(TAG, "BluetoothAdapter.ACTION_DISCOVERY_STARTED never received.  Recovery may fail.", new Object[0]);
            }
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            if (defaultAdapter.isDiscovering()) {
                LogManager.m260d(TAG, "Cancelling discovery", new Object[0]);
                defaultAdapter.cancelDiscovery();
                return;
            }
            LogManager.m260d(TAG, "Discovery not running.  Won't cancel it", new Object[0]);
        } catch (InterruptedException unused) {
            LogManager.m260d(TAG, "DiscoveryCanceller sleep interrupted.", new Object[0]);
        }
    }
}
