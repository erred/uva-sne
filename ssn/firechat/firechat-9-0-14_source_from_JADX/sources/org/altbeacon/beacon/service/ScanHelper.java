package org.altbeacon.beacon.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.le.ScanSettings.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.WorkerThread;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.scanner.CycledLeScanCallback;
import org.altbeacon.beacon.service.scanner.CycledLeScanner;
import org.altbeacon.beacon.service.scanner.DistinctPacketDetector;
import org.altbeacon.beacon.service.scanner.NonBeaconLeScanCallback;
import org.altbeacon.beacon.service.scanner.ScanFilterUtils;
import org.altbeacon.beacon.startup.StartupBroadcastReceiver;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

class ScanHelper {
    /* access modifiers changed from: private */
    public static final String TAG = "ScanHelper";
    private BeaconManager mBeaconManager;
    /* access modifiers changed from: private */
    public Set<BeaconParser> mBeaconParsers = new HashSet();
    /* access modifiers changed from: private */
    public Context mContext;
    private final CycledLeScanCallback mCycledLeScanCallback = new CycledLeScanCallback() {
        @TargetApi(11)
        @MainThread
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            ScanHelper.this.processScanResult(bluetoothDevice, i, bArr);
        }

        @SuppressLint({"WrongThread"})
        @MainThread
        public void onCycleEnd() {
            ScanHelper.this.mDistinctPacketDetector.clearDetections();
            ScanHelper.this.mMonitoringStatus.updateNewlyOutside();
            ScanHelper.this.processRangeData();
            if (ScanHelper.this.mSimulatedScanData != null) {
                LogManager.m268w(ScanHelper.TAG, "Simulated scan data is deprecated and will be removed in a future release. Please use the new BeaconSimulator interface instead.", new Object[0]);
                ApplicationInfo applicationInfo = ScanHelper.this.mContext.getApplicationInfo();
                int i = applicationInfo.flags & 2;
                applicationInfo.flags = i;
                if (i != 0) {
                    for (Beacon access$600 : ScanHelper.this.mSimulatedScanData) {
                        ScanHelper.this.processBeaconFromScan(access$600);
                    }
                } else {
                    LogManager.m268w(ScanHelper.TAG, "Simulated scan data provided, but ignored because we are not running in debug mode.  Please remove simulated scan data for production.", new Object[0]);
                }
            }
            if (BeaconManager.getBeaconSimulator() == null) {
                return;
            }
            if (BeaconManager.getBeaconSimulator().getBeacons() != null) {
                ApplicationInfo applicationInfo2 = ScanHelper.this.mContext.getApplicationInfo();
                int i2 = applicationInfo2.flags & 2;
                applicationInfo2.flags = i2;
                if (i2 != 0) {
                    for (Beacon access$6002 : BeaconManager.getBeaconSimulator().getBeacons()) {
                        ScanHelper.this.processBeaconFromScan(access$6002);
                    }
                    return;
                }
                LogManager.m268w(ScanHelper.TAG, "Beacon simulations provided, but ignored because we are not running in debug mode.  Please remove beacon simulations for production.", new Object[0]);
                return;
            }
            LogManager.m268w(ScanHelper.TAG, "getBeacons is returning null. No simulated beacons to report.", new Object[0]);
        }
    };
    /* access modifiers changed from: private */
    public CycledLeScanner mCycledScanner;
    /* access modifiers changed from: private */
    public DistinctPacketDetector mDistinctPacketDetector = new DistinctPacketDetector();
    private ExecutorService mExecutor;
    private ExtraDataBeaconTracker mExtraDataBeaconTracker;
    /* access modifiers changed from: private */
    public MonitoringStatus mMonitoringStatus;
    private final Map<Region, RangeState> mRangedRegionState = new HashMap();
    /* access modifiers changed from: private */
    public List<Beacon> mSimulatedScanData = null;

    private class ScanData {
        @NonNull
        BluetoothDevice device;
        final int rssi;
        @NonNull
        byte[] scanRecord;

        ScanData(@NonNull BluetoothDevice bluetoothDevice, int i, @NonNull byte[] bArr) {
            this.device = bluetoothDevice;
            this.rssi = i;
            this.scanRecord = bArr;
        }
    }

    private class ScanProcessor extends AsyncTask<ScanData, Void, Void> {
        final DetectionTracker mDetectionTracker = DetectionTracker.getInstance();
        private final NonBeaconLeScanCallback mNonBeaconLeScanCallback;

        /* access modifiers changed from: protected */
        public void onPostExecute(Void voidR) {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Void... voidArr) {
        }

        ScanProcessor(NonBeaconLeScanCallback nonBeaconLeScanCallback) {
            this.mNonBeaconLeScanCallback = nonBeaconLeScanCallback;
        }

        /* access modifiers changed from: protected */
        @WorkerThread
        public Void doInBackground(ScanData... scanDataArr) {
            ScanData scanData = scanDataArr[0];
            Beacon beacon = null;
            for (BeaconParser fromScanData : ScanHelper.this.mBeaconParsers) {
                beacon = fromScanData.fromScanData(scanData.scanRecord, scanData.rssi, scanData.device);
                if (beacon != null) {
                    break;
                }
            }
            if (beacon != null) {
                if (LogManager.isVerboseLoggingEnabled()) {
                    String access$400 = ScanHelper.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Beacon packet detected for: ");
                    sb.append(beacon);
                    sb.append(" with rssi ");
                    sb.append(beacon.getRssi());
                    LogManager.m260d(access$400, sb.toString(), new Object[0]);
                }
                this.mDetectionTracker.recordDetection();
                if (ScanHelper.this.mCycledScanner != null && !ScanHelper.this.mCycledScanner.getDistinctPacketsDetectedPerScan() && !ScanHelper.this.mDistinctPacketDetector.isPacketDistinct(scanData.device.getAddress(), scanData.scanRecord)) {
                    LogManager.m264i(ScanHelper.TAG, "Non-distinct packets detected in a single scan.  Restarting scans unecessary.", new Object[0]);
                    ScanHelper.this.mCycledScanner.setDistinctPacketsDetectedPerScan(true);
                }
                ScanHelper.this.processBeaconFromScan(beacon);
            } else if (this.mNonBeaconLeScanCallback != null) {
                this.mNonBeaconLeScanCallback.onNonBeaconLeScan(scanData.device, scanData.rssi, scanData.scanRecord);
            }
            return null;
        }
    }

    ScanHelper(Context context) {
        this.mContext = context;
        this.mBeaconManager = BeaconManager.getInstanceForApplication(context);
        this.mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    /* access modifiers changed from: 0000 */
    public CycledLeScanner getCycledScanner() {
        return this.mCycledScanner;
    }

    /* access modifiers changed from: 0000 */
    public MonitoringStatus getMonitoringStatus() {
        return this.mMonitoringStatus;
    }

    /* access modifiers changed from: 0000 */
    public void setMonitoringStatus(MonitoringStatus monitoringStatus) {
        this.mMonitoringStatus = monitoringStatus;
    }

    /* access modifiers changed from: 0000 */
    public Map<Region, RangeState> getRangedRegionState() {
        return this.mRangedRegionState;
    }

    /* access modifiers changed from: 0000 */
    public void setRangedRegionState(Map<Region, RangeState> map) {
        synchronized (this.mRangedRegionState) {
            this.mRangedRegionState.clear();
            this.mRangedRegionState.putAll(map);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setExtraDataBeaconTracker(ExtraDataBeaconTracker extraDataBeaconTracker) {
        this.mExtraDataBeaconTracker = extraDataBeaconTracker;
    }

    /* access modifiers changed from: 0000 */
    public void setBeaconParsers(Set<BeaconParser> set) {
        this.mBeaconParsers = set;
    }

    /* access modifiers changed from: 0000 */
    public void setSimulatedScanData(List<Beacon> list) {
        this.mSimulatedScanData = list;
    }

    /* access modifiers changed from: 0000 */
    public void createCycledLeScanner(boolean z, BluetoothCrashResolver bluetoothCrashResolver) {
        this.mCycledScanner = CycledLeScanner.createScanner(this.mContext, BeaconManager.DEFAULT_FOREGROUND_SCAN_PERIOD, 0, z, this.mCycledLeScanCallback, bluetoothCrashResolver);
    }

    /* access modifiers changed from: 0000 */
    @TargetApi(11)
    public void processScanResult(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        try {
            new ScanProcessor(this.mBeaconManager.getNonBeaconLeScanCallback()).executeOnExecutor(this.mExecutor, new ScanData[]{new ScanData(bluetoothDevice, i, bArr)});
        } catch (RejectedExecutionException unused) {
            LogManager.m268w(TAG, "Ignoring scan result because we cannot keep up.", new Object[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public void reloadParsers() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(this.mBeaconManager.getBeaconParsers());
        boolean z = true;
        for (BeaconParser beaconParser : this.mBeaconManager.getBeaconParsers()) {
            if (beaconParser.getExtraDataParsers().size() > 0) {
                z = false;
                hashSet.addAll(beaconParser.getExtraDataParsers());
            }
        }
        this.mBeaconParsers = hashSet;
        this.mExtraDataBeaconTracker = new ExtraDataBeaconTracker(z);
    }

    /* access modifiers changed from: 0000 */
    @RequiresApi(api = 26)
    public void startAndroidOBackgroundScan(Set<BeaconParser> set) {
        ScanSettings build = new Builder().setScanMode(0).build();
        List createScanFiltersForBeaconParsers = new ScanFilterUtils().createScanFiltersForBeaconParsers(new ArrayList(set));
        try {
            BluetoothAdapter adapter = ((BluetoothManager) this.mContext.getApplicationContext().getSystemService("bluetooth")).getAdapter();
            if (adapter == null) {
                LogManager.m268w(TAG, "Failed to construct a BluetoothAdapter", new Object[0]);
                return;
            }
            int startScan = adapter.getBluetoothLeScanner().startScan(createScanFiltersForBeaconParsers, build, getScanCallbackIntent());
            if (startScan != 0) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to start background scan on Android O.  Code: ");
                sb.append(startScan);
                LogManager.m262e(str, sb.toString(), new Object[0]);
                return;
            }
            LogManager.m260d(TAG, "Started passive beacon scan", new Object[0]);
        } catch (SecurityException unused) {
            LogManager.m262e(TAG, "SecurityException making Android O background scanner", new Object[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    @RequiresApi(api = 26)
    public void stopAndroidOBackgroundScan() {
        try {
            BluetoothAdapter adapter = ((BluetoothManager) this.mContext.getApplicationContext().getSystemService("bluetooth")).getAdapter();
            if (adapter == null) {
                LogManager.m268w(TAG, "Failed to construct a BluetoothAdapter", new Object[0]);
            } else {
                adapter.getBluetoothLeScanner().stopScan(getScanCallbackIntent());
            }
        } catch (SecurityException unused) {
            LogManager.m262e(TAG, "SecurityException stopping Android O background scanner", new Object[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public PendingIntent getScanCallbackIntent() {
        Intent intent = new Intent(this.mContext, StartupBroadcastReceiver.class);
        intent.putExtra("o-scan", true);
        return PendingIntent.getBroadcast(this.mContext, 0, intent, 134217728);
    }

    /* access modifiers changed from: 0000 */
    @RestrictTo({Scope.TESTS})
    public CycledLeScanCallback getCycledLeScanCallback() {
        return this.mCycledLeScanCallback;
    }

    /* access modifiers changed from: private */
    public void processRangeData() {
        synchronized (this.mRangedRegionState) {
            for (Region region : this.mRangedRegionState.keySet()) {
                RangeState rangeState = (RangeState) this.mRangedRegionState.get(region);
                LogManager.m260d(TAG, "Calling ranging callback", new Object[0]);
                rangeState.getCallback().call(this.mContext, "rangingData", new RangingData(rangeState.finalizeBeacons(), region).toBundle());
            }
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public void processBeaconFromScan(@NonNull Beacon beacon) {
        if (Stats.getInstance().isEnabled()) {
            Stats.getInstance().log(beacon);
        }
        if (LogManager.isVerboseLoggingEnabled()) {
            LogManager.m260d(TAG, "beacon detected : %s", beacon.toString());
        }
        Beacon track = this.mExtraDataBeaconTracker.track(beacon);
        if (track != null) {
            this.mMonitoringStatus.updateNewlyInsideInRegionsContaining(track);
            LogManager.m260d(TAG, "looking for ranging region matches for this beacon", new Object[0]);
            synchronized (this.mRangedRegionState) {
                for (Region region : matchingRegions(track, this.mRangedRegionState.keySet())) {
                    LogManager.m260d(TAG, "matches ranging region: %s", region);
                    RangeState rangeState = (RangeState) this.mRangedRegionState.get(region);
                    if (rangeState != null) {
                        rangeState.addBeacon(track);
                    }
                }
            }
        } else if (LogManager.isVerboseLoggingEnabled()) {
            LogManager.m260d(TAG, "not processing detections for GATT extra data beacon", new Object[0]);
        }
    }

    private List<Region> matchingRegions(Beacon beacon, Collection<Region> collection) {
        ArrayList arrayList = new ArrayList();
        for (Region region : collection) {
            if (region.matchesBeacon(beacon)) {
                arrayList.add(region);
            } else {
                LogManager.m260d(TAG, "This region (%s) does not match beacon: %s", region, beacon);
            }
        }
        return arrayList;
    }
}
