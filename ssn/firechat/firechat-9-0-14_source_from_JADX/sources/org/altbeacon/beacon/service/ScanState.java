package org.altbeacon.beacon.service;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;

public class ScanState implements Serializable {
    public static int MIN_SCAN_JOB_INTERVAL_MILLIS = 300000;
    private static final String STATUS_PRESERVATION_FILE_NAME = "android-beacon-library-scan-state";
    private static final String TAG = "ScanState";
    private static final String TEMP_STATUS_PRESERVATION_FILE_NAME = "android-beacon-library-scan-state-temp";
    private long mBackgroundBetweenScanPeriod;
    private boolean mBackgroundMode;
    private long mBackgroundScanPeriod;
    private Set<BeaconParser> mBeaconParsers = new HashSet();
    private transient Context mContext;
    private ExtraDataBeaconTracker mExtraBeaconDataTracker = new ExtraDataBeaconTracker();
    private long mForegroundBetweenScanPeriod;
    private long mForegroundScanPeriod;
    private long mLastScanStartTimeMillis = 0;
    private transient MonitoringStatus mMonitoringStatus;
    private Map<Region, RangeState> mRangedRegionState = new HashMap();

    public Boolean getBackgroundMode() {
        return Boolean.valueOf(this.mBackgroundMode);
    }

    public void setBackgroundMode(Boolean bool) {
        this.mBackgroundMode = bool.booleanValue();
    }

    public Long getBackgroundBetweenScanPeriod() {
        return Long.valueOf(this.mBackgroundBetweenScanPeriod);
    }

    public void setBackgroundBetweenScanPeriod(Long l) {
        this.mBackgroundBetweenScanPeriod = l.longValue();
    }

    public Long getBackgroundScanPeriod() {
        return Long.valueOf(this.mBackgroundScanPeriod);
    }

    public void setBackgroundScanPeriod(Long l) {
        this.mBackgroundScanPeriod = l.longValue();
    }

    public Long getForegroundBetweenScanPeriod() {
        return Long.valueOf(this.mForegroundBetweenScanPeriod);
    }

    public void setForegroundBetweenScanPeriod(Long l) {
        this.mForegroundBetweenScanPeriod = l.longValue();
    }

    public Long getForegroundScanPeriod() {
        return Long.valueOf(this.mForegroundScanPeriod);
    }

    public void setForegroundScanPeriod(Long l) {
        this.mForegroundScanPeriod = l.longValue();
    }

    public ScanState(Context context) {
        this.mContext = context;
    }

    public MonitoringStatus getMonitoringStatus() {
        return this.mMonitoringStatus;
    }

    public void setMonitoringStatus(MonitoringStatus monitoringStatus) {
        this.mMonitoringStatus = monitoringStatus;
    }

    public Map<Region, RangeState> getRangedRegionState() {
        return this.mRangedRegionState;
    }

    public void setRangedRegionState(Map<Region, RangeState> map) {
        this.mRangedRegionState = map;
    }

    public ExtraDataBeaconTracker getExtraBeaconDataTracker() {
        return this.mExtraBeaconDataTracker;
    }

    public void setExtraBeaconDataTracker(ExtraDataBeaconTracker extraDataBeaconTracker) {
        this.mExtraBeaconDataTracker = extraDataBeaconTracker;
    }

    public Set<BeaconParser> getBeaconParsers() {
        return this.mBeaconParsers;
    }

    public void setBeaconParsers(Set<BeaconParser> set) {
        this.mBeaconParsers = set;
    }

    public long getLastScanStartTimeMillis() {
        return this.mLastScanStartTimeMillis;
    }

    public void setLastScanStartTimeMillis(long j) {
        this.mLastScanStartTimeMillis = j;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:52|(0)|(0)|61|62) */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001d, code lost:
        if (r4 == null) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0023, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0024, code lost:
        r9 = r3;
        r3 = r2;
        r2 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0028, code lost:
        r10 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0046, code lost:
        org.altbeacon.beacon.logging.LogManager.m260d(TAG, "Serialized ScanState has wrong class. Just ignoring saved state...", new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0050, code lost:
        org.altbeacon.beacon.logging.LogManager.m262e(TAG, "Deserialization exception", new java.lang.Object[0]);
        android.util.Log.e(TAG, "error: ", r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0065, code lost:
        if (r4 != null) goto L_0x001f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x00c8 */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0028 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:7:0x0010] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0046 A[Catch:{ all -> 0x00ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0050 A[Catch:{ all -> 0x00ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0062 A[SYNTHETIC, Splitter:B:38:0x0062] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00bd A[SYNTHETIC, Splitter:B:54:0x00bd] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c5 A[SYNTHETIC, Splitter:B:59:0x00c5] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static org.altbeacon.beacon.service.ScanState restore(android.content.Context r10) {
        /*
            java.lang.Class<org.altbeacon.beacon.service.ScanState> r0 = org.altbeacon.beacon.service.ScanState.class
            monitor-enter(r0)
            r1 = 0
            r2 = 0
            java.lang.String r3 = "android-beacon-library-scan-state"
            java.io.FileInputStream r3 = r10.openFileInput(r3)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x003f, all -> 0x003b }
            java.io.ObjectInputStream r4 = new java.io.ObjectInputStream     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x0035, all -> 0x0030 }
            r4.<init>(r3)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x0035, all -> 0x0030 }
            java.lang.Object r5 = r4.readObject()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x002a, all -> 0x0028 }
            org.altbeacon.beacon.service.ScanState r5 = (org.altbeacon.beacon.service.ScanState) r5     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x002a, all -> 0x0028 }
            r5.mContext = r10     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x0023, all -> 0x0028 }
            if (r3 == 0) goto L_0x001d
            r3.close()     // Catch:{ IOException -> 0x001d }
        L_0x001d:
            if (r4 == 0) goto L_0x0068
        L_0x001f:
            r4.close()     // Catch:{ IOException -> 0x0068 }
            goto L_0x0068
        L_0x0023:
            r2 = move-exception
            r9 = r3
            r3 = r2
            r2 = r9
            goto L_0x0042
        L_0x0028:
            r10 = move-exception
            goto L_0x0032
        L_0x002a:
            r5 = move-exception
            r9 = r5
            r5 = r2
            r2 = r3
            r3 = r9
            goto L_0x0042
        L_0x0030:
            r10 = move-exception
            r4 = r2
        L_0x0032:
            r2 = r3
            goto L_0x00bb
        L_0x0035:
            r4 = move-exception
            r5 = r2
            r2 = r3
            r3 = r4
            r4 = r5
            goto L_0x0042
        L_0x003b:
            r10 = move-exception
            r4 = r2
            goto L_0x00bb
        L_0x003f:
            r3 = move-exception
            r4 = r2
            r5 = r4
        L_0x0042:
            boolean r6 = r3 instanceof java.io.InvalidClassException     // Catch:{ all -> 0x00ba }
            if (r6 == 0) goto L_0x0050
            java.lang.String r3 = TAG     // Catch:{ all -> 0x00ba }
            java.lang.String r6 = "Serialized ScanState has wrong class. Just ignoring saved state..."
            java.lang.Object[] r7 = new java.lang.Object[r1]     // Catch:{ all -> 0x00ba }
            org.altbeacon.beacon.logging.LogManager.m260d(r3, r6, r7)     // Catch:{ all -> 0x00ba }
            goto L_0x0060
        L_0x0050:
            java.lang.String r6 = TAG     // Catch:{ all -> 0x00ba }
            java.lang.String r7 = "Deserialization exception"
            java.lang.Object[] r8 = new java.lang.Object[r1]     // Catch:{ all -> 0x00ba }
            org.altbeacon.beacon.logging.LogManager.m262e(r6, r7, r8)     // Catch:{ all -> 0x00ba }
            java.lang.String r6 = TAG     // Catch:{ all -> 0x00ba }
            java.lang.String r7 = "error: "
            android.util.Log.e(r6, r7, r3)     // Catch:{ all -> 0x00ba }
        L_0x0060:
            if (r2 == 0) goto L_0x0065
            r2.close()     // Catch:{ IOException -> 0x0065 }
        L_0x0065:
            if (r4 == 0) goto L_0x0068
            goto L_0x001f
        L_0x0068:
            if (r5 != 0) goto L_0x006f
            org.altbeacon.beacon.service.ScanState r5 = new org.altbeacon.beacon.service.ScanState     // Catch:{ all -> 0x00c1 }
            r5.<init>(r10)     // Catch:{ all -> 0x00c1 }
        L_0x006f:
            org.altbeacon.beacon.service.ExtraDataBeaconTracker r2 = r5.mExtraBeaconDataTracker     // Catch:{ all -> 0x00c1 }
            if (r2 != 0) goto L_0x007a
            org.altbeacon.beacon.service.ExtraDataBeaconTracker r2 = new org.altbeacon.beacon.service.ExtraDataBeaconTracker     // Catch:{ all -> 0x00c1 }
            r2.<init>()     // Catch:{ all -> 0x00c1 }
            r5.mExtraBeaconDataTracker = r2     // Catch:{ all -> 0x00c1 }
        L_0x007a:
            org.altbeacon.beacon.service.MonitoringStatus r10 = org.altbeacon.beacon.service.MonitoringStatus.getInstanceForApplication(r10)     // Catch:{ all -> 0x00c1 }
            r5.mMonitoringStatus = r10     // Catch:{ all -> 0x00c1 }
            java.lang.String r10 = TAG     // Catch:{ all -> 0x00c1 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c1 }
            r2.<init>()     // Catch:{ all -> 0x00c1 }
            java.lang.String r3 = "Scan state restore regions: monitored="
            r2.append(r3)     // Catch:{ all -> 0x00c1 }
            org.altbeacon.beacon.service.MonitoringStatus r3 = r5.getMonitoringStatus()     // Catch:{ all -> 0x00c1 }
            java.util.Set r3 = r3.regions()     // Catch:{ all -> 0x00c1 }
            int r3 = r3.size()     // Catch:{ all -> 0x00c1 }
            r2.append(r3)     // Catch:{ all -> 0x00c1 }
            java.lang.String r3 = " ranged="
            r2.append(r3)     // Catch:{ all -> 0x00c1 }
            java.util.Map r3 = r5.getRangedRegionState()     // Catch:{ all -> 0x00c1 }
            java.util.Set r3 = r3.keySet()     // Catch:{ all -> 0x00c1 }
            int r3 = r3.size()     // Catch:{ all -> 0x00c1 }
            r2.append(r3)     // Catch:{ all -> 0x00c1 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00c1 }
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x00c1 }
            org.altbeacon.beacon.logging.LogManager.m260d(r10, r2, r1)     // Catch:{ all -> 0x00c1 }
            monitor-exit(r0)     // Catch:{ all -> 0x00c1 }
            return r5
        L_0x00ba:
            r10 = move-exception
        L_0x00bb:
            if (r2 == 0) goto L_0x00c3
            r2.close()     // Catch:{ IOException -> 0x00c3 }
            goto L_0x00c3
        L_0x00c1:
            r10 = move-exception
            goto L_0x00c9
        L_0x00c3:
            if (r4 == 0) goto L_0x00c8
            r4.close()     // Catch:{ IOException -> 0x00c8 }
        L_0x00c8:
            throw r10     // Catch:{ all -> 0x00c1 }
        L_0x00c9:
            monitor-exit(r0)     // Catch:{ all -> 0x00c1 }
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.service.ScanState.restore(android.content.Context):org.altbeacon.beacon.service.ScanState");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:42|(2:44|45)|(2:49|50)|51|52) */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x008a, code lost:
        if (r4 == null) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b8, code lost:
        if (r4 == null) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        r10.mMonitoringStatus.saveMonitoringStatusIfOn();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00bb */
    /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x00d0 */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b5 A[SYNTHETIC, Splitter:B:34:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00c5 A[SYNTHETIC, Splitter:B:44:0x00c5] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00cd A[SYNTHETIC, Splitter:B:49:0x00cd] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:51:0x00d0=Splitter:B:51:0x00d0, B:38:0x00bb=Splitter:B:38:0x00bb} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void save() {
        /*
            r10 = this;
            java.lang.Class<org.altbeacon.beacon.service.ScanState> r0 = org.altbeacon.beacon.service.ScanState.class
            monitor-enter(r0)
            r1 = 0
            r2 = 0
            android.content.Context r3 = r10.mContext     // Catch:{ IOException -> 0x009f, all -> 0x009a }
            java.lang.String r4 = "android-beacon-library-scan-state-temp"
            java.io.FileOutputStream r3 = r3.openFileOutput(r4, r2)     // Catch:{ IOException -> 0x009f, all -> 0x009a }
            java.io.ObjectOutputStream r4 = new java.io.ObjectOutputStream     // Catch:{ IOException -> 0x0095, all -> 0x0092 }
            r4.<init>(r3)     // Catch:{ IOException -> 0x0095, all -> 0x0092 }
            r4.writeObject(r10)     // Catch:{ IOException -> 0x0090 }
            java.io.File r1 = new java.io.File     // Catch:{ IOException -> 0x0090 }
            android.content.Context r5 = r10.mContext     // Catch:{ IOException -> 0x0090 }
            java.io.File r5 = r5.getFilesDir()     // Catch:{ IOException -> 0x0090 }
            java.lang.String r6 = "android-beacon-library-scan-state"
            r1.<init>(r5, r6)     // Catch:{ IOException -> 0x0090 }
            java.io.File r5 = new java.io.File     // Catch:{ IOException -> 0x0090 }
            android.content.Context r6 = r10.mContext     // Catch:{ IOException -> 0x0090 }
            java.io.File r6 = r6.getFilesDir()     // Catch:{ IOException -> 0x0090 }
            java.lang.String r7 = "android-beacon-library-scan-state-temp"
            r5.<init>(r6, r7)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r6 = TAG     // Catch:{ IOException -> 0x0090 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0090 }
            r7.<init>()     // Catch:{ IOException -> 0x0090 }
            java.lang.String r8 = "Temp file is "
            r7.append(r8)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r8 = r5.getAbsolutePath()     // Catch:{ IOException -> 0x0090 }
            r7.append(r8)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x0090 }
            java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0090 }
            org.altbeacon.beacon.logging.LogManager.m260d(r6, r7, r8)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r6 = TAG     // Catch:{ IOException -> 0x0090 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0090 }
            r7.<init>()     // Catch:{ IOException -> 0x0090 }
            java.lang.String r8 = "Perm file is "
            r7.append(r8)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r8 = r1.getAbsolutePath()     // Catch:{ IOException -> 0x0090 }
            r7.append(r8)     // Catch:{ IOException -> 0x0090 }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x0090 }
            java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0090 }
            org.altbeacon.beacon.logging.LogManager.m260d(r6, r7, r8)     // Catch:{ IOException -> 0x0090 }
            boolean r6 = r1.delete()     // Catch:{ IOException -> 0x0090 }
            if (r6 != 0) goto L_0x0076
            java.lang.String r6 = TAG     // Catch:{ IOException -> 0x0090 }
            java.lang.String r7 = "Error while saving scan status to file: Cannot delete existing file."
            java.lang.Object[] r8 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0090 }
            org.altbeacon.beacon.logging.LogManager.m262e(r6, r7, r8)     // Catch:{ IOException -> 0x0090 }
        L_0x0076:
            boolean r1 = r5.renameTo(r1)     // Catch:{ IOException -> 0x0090 }
            if (r1 != 0) goto L_0x0085
            java.lang.String r1 = TAG     // Catch:{ IOException -> 0x0090 }
            java.lang.String r5 = "Error while saving scan status to file: Cannot rename temp file."
            java.lang.Object[] r6 = new java.lang.Object[r2]     // Catch:{ IOException -> 0x0090 }
            org.altbeacon.beacon.logging.LogManager.m262e(r1, r5, r6)     // Catch:{ IOException -> 0x0090 }
        L_0x0085:
            if (r3 == 0) goto L_0x008a
            r3.close()     // Catch:{ IOException -> 0x008a }
        L_0x008a:
            if (r4 == 0) goto L_0x00bb
        L_0x008c:
            r4.close()     // Catch:{ IOException -> 0x00bb }
            goto L_0x00bb
        L_0x0090:
            r1 = move-exception
            goto L_0x00a3
        L_0x0092:
            r2 = move-exception
            r4 = r1
            goto L_0x009d
        L_0x0095:
            r4 = move-exception
            r9 = r4
            r4 = r1
            r1 = r9
            goto L_0x00a3
        L_0x009a:
            r2 = move-exception
            r3 = r1
            r4 = r3
        L_0x009d:
            r1 = r2
            goto L_0x00c3
        L_0x009f:
            r3 = move-exception
            r4 = r1
            r1 = r3
            r3 = r4
        L_0x00a3:
            java.lang.String r5 = TAG     // Catch:{ all -> 0x00c2 }
            java.lang.String r6 = "Error while saving scan status to file: "
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x00c2 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x00c2 }
            r7[r2] = r1     // Catch:{ all -> 0x00c2 }
            org.altbeacon.beacon.logging.LogManager.m262e(r5, r6, r7)     // Catch:{ all -> 0x00c2 }
            if (r3 == 0) goto L_0x00b8
            r3.close()     // Catch:{ IOException -> 0x00b8 }
        L_0x00b8:
            if (r4 == 0) goto L_0x00bb
            goto L_0x008c
        L_0x00bb:
            org.altbeacon.beacon.service.MonitoringStatus r1 = r10.mMonitoringStatus     // Catch:{ all -> 0x00c9 }
            r1.saveMonitoringStatusIfOn()     // Catch:{ all -> 0x00c9 }
            monitor-exit(r0)     // Catch:{ all -> 0x00c9 }
            return
        L_0x00c2:
            r1 = move-exception
        L_0x00c3:
            if (r3 == 0) goto L_0x00cb
            r3.close()     // Catch:{ IOException -> 0x00cb }
            goto L_0x00cb
        L_0x00c9:
            r1 = move-exception
            goto L_0x00d1
        L_0x00cb:
            if (r4 == 0) goto L_0x00d0
            r4.close()     // Catch:{ IOException -> 0x00d0 }
        L_0x00d0:
            throw r1     // Catch:{ all -> 0x00c9 }
        L_0x00d1:
            monitor-exit(r0)     // Catch:{ all -> 0x00c9 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.service.ScanState.save():void");
    }

    public int getScanJobIntervalMillis() {
        long j;
        if (getBackgroundMode().booleanValue()) {
            j = getBackgroundScanPeriod().longValue() + getBackgroundBetweenScanPeriod().longValue();
        } else {
            j = getForegroundScanPeriod().longValue() + getForegroundBetweenScanPeriod().longValue();
        }
        return j > ((long) MIN_SCAN_JOB_INTERVAL_MILLIS) ? (int) j : MIN_SCAN_JOB_INTERVAL_MILLIS;
    }

    public int getScanJobRuntimeMillis() {
        long j;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ScanState says background mode for ScanJob is ");
        sb.append(getBackgroundMode());
        LogManager.m260d(str, sb.toString(), new Object[0]);
        if (getBackgroundMode().booleanValue()) {
            j = getBackgroundScanPeriod().longValue();
        } else {
            j = getForegroundScanPeriod().longValue();
        }
        return (getBackgroundMode().booleanValue() || j >= ((long) MIN_SCAN_JOB_INTERVAL_MILLIS)) ? (int) j : MIN_SCAN_JOB_INTERVAL_MILLIS;
    }

    public void applyChanges(BeaconManager beaconManager) {
        this.mBeaconParsers = new HashSet(beaconManager.getBeaconParsers());
        this.mForegroundScanPeriod = beaconManager.getForegroundScanPeriod();
        this.mForegroundBetweenScanPeriod = beaconManager.getForegroundBetweenScanPeriod();
        this.mBackgroundScanPeriod = beaconManager.getBackgroundScanPeriod();
        this.mBackgroundBetweenScanPeriod = beaconManager.getBackgroundBetweenScanPeriod();
        this.mBackgroundMode = beaconManager.getBackgroundMode();
        ArrayList arrayList = new ArrayList(this.mMonitoringStatus.regions());
        ArrayList arrayList2 = new ArrayList(this.mRangedRegionState.keySet());
        ArrayList arrayList3 = new ArrayList(beaconManager.getMonitoredRegions());
        ArrayList arrayList4 = new ArrayList(beaconManager.getRangedRegions());
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ranged regions: old=");
        sb.append(arrayList2.size());
        sb.append(" new=");
        sb.append(arrayList4.size());
        LogManager.m260d(str, sb.toString(), new Object[0]);
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("monitored regions: old=");
        sb2.append(arrayList.size());
        sb2.append(" new=");
        sb2.append(arrayList3.size());
        LogManager.m260d(str2, sb2.toString(), new Object[0]);
        Iterator it = arrayList4.iterator();
        while (it.hasNext()) {
            Region region = (Region) it.next();
            if (!arrayList2.contains(region)) {
                String str3 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Starting ranging region: ");
                sb3.append(region);
                LogManager.m260d(str3, sb3.toString(), new Object[0]);
                this.mRangedRegionState.put(region, new RangeState(new Callback(this.mContext.getPackageName())));
            }
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            Region region2 = (Region) it2.next();
            if (!arrayList4.contains(region2)) {
                String str4 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Stopping ranging region: ");
                sb4.append(region2);
                LogManager.m260d(str4, sb4.toString(), new Object[0]);
                this.mRangedRegionState.remove(region2);
            }
        }
        String str5 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("Updated state with ");
        sb5.append(arrayList4.size());
        sb5.append(" ranging regions and ");
        sb5.append(arrayList3.size());
        sb5.append(" monitoring regions.");
        LogManager.m260d(str5, sb5.toString(), new Object[0]);
        save();
    }
}
