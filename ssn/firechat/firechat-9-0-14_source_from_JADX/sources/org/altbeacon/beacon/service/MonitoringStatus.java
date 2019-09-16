package org.altbeacon.beacon.service;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;

public class MonitoringStatus {
    private static final int MAX_REGIONS_FOR_STATUS_PRESERVATION = 50;
    private static final int MAX_STATUS_PRESERVATION_FILE_AGE_TO_RESTORE_SECS = 900;
    private static final Object SINGLETON_LOCK = new Object();
    public static final String STATUS_PRESERVATION_FILE_NAME = "org.altbeacon.beacon.service.monitoring_status_state";
    private static final String TAG = "MonitoringStatus";
    private static volatile MonitoringStatus sInstance;
    private Context mContext;
    private Map<Region, RegionMonitoringState> mRegionsStatesMap;
    private boolean mStatePreservationIsOn = true;

    public static MonitoringStatus getInstanceForApplication(Context context) {
        MonitoringStatus monitoringStatus = sInstance;
        if (monitoringStatus == null) {
            synchronized (SINGLETON_LOCK) {
                monitoringStatus = sInstance;
                if (monitoringStatus == null) {
                    monitoringStatus = new MonitoringStatus(context.getApplicationContext());
                    sInstance = monitoringStatus;
                }
            }
        }
        return monitoringStatus;
    }

    public MonitoringStatus(Context context) {
        this.mContext = context;
    }

    public synchronized void addRegion(Region region, Callback callback) {
        addLocalRegion(region, callback);
        saveMonitoringStatusIfOn();
    }

    public synchronized void removeRegion(Region region) {
        removeLocalRegion(region);
        saveMonitoringStatusIfOn();
    }

    public synchronized Set<Region> regions() {
        return getRegionsStateMap().keySet();
    }

    public synchronized int regionsCount() {
        return regions().size();
    }

    public synchronized RegionMonitoringState stateOf(Region region) {
        return (RegionMonitoringState) getRegionsStateMap().get(region);
    }

    public synchronized void updateNewlyOutside() {
        boolean z = false;
        for (Region region : regions()) {
            RegionMonitoringState stateOf = stateOf(region);
            if (stateOf.markOutsideIfExpired()) {
                LogManager.m260d(TAG, "found a monitor that expired: %s", region);
                stateOf.getCallback().call(this.mContext, "monitoringData", new MonitoringData(stateOf.getInside(), region).toBundle());
                z = true;
            }
        }
        if (z) {
            saveMonitoringStatusIfOn();
        } else {
            updateMonitoringStatusTime(System.currentTimeMillis());
        }
    }

    public synchronized void updateNewlyInsideInRegionsContaining(Beacon beacon) {
        boolean z = false;
        for (Region region : regionsMatchingTo(beacon)) {
            RegionMonitoringState regionMonitoringState = (RegionMonitoringState) getRegionsStateMap().get(region);
            if (regionMonitoringState != null && regionMonitoringState.markInside()) {
                z = true;
                regionMonitoringState.getCallback().call(this.mContext, "monitoringData", new MonitoringData(regionMonitoringState.getInside(), region).toBundle());
            }
        }
        if (z) {
            saveMonitoringStatusIfOn();
        } else {
            updateMonitoringStatusTime(System.currentTimeMillis());
        }
    }

    private Map<Region, RegionMonitoringState> getRegionsStateMap() {
        if (this.mRegionsStatesMap == null) {
            restoreOrInitializeMonitoringStatus();
        }
        return this.mRegionsStatesMap;
    }

    private void restoreOrInitializeMonitoringStatus() {
        long currentTimeMillis = System.currentTimeMillis() - getLastMonitoringStatusUpdateTime();
        this.mRegionsStatesMap = new HashMap();
        if (!this.mStatePreservationIsOn) {
            LogManager.m260d(TAG, "Not restoring monitoring state because persistence is disabled", new Object[0]);
        } else if (currentTimeMillis > 900000) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Not restoring monitoring state because it was recorded too many milliseconds ago: ");
            sb.append(currentTimeMillis);
            LogManager.m260d(str, sb.toString(), new Object[0]);
        } else {
            restoreMonitoringStatus();
            LogManager.m260d(TAG, "Done restoring monitoring status", new Object[0]);
        }
    }

    private List<Region> regionsMatchingTo(Beacon beacon) {
        ArrayList arrayList = new ArrayList();
        for (Region region : regions()) {
            if (region.matchesBeacon(beacon)) {
                arrayList.add(region);
            } else {
                LogManager.m260d(TAG, "This region (%s) does not match beacon: %s", region, beacon);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0047, code lost:
        if (r3 == null) goto L_0x007f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007c, code lost:
        if (r3 == null) goto L_0x007f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0079 A[SYNTHETIC, Splitter:B:35:0x0079] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0083 A[SYNTHETIC, Splitter:B:43:0x0083] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0088 A[SYNTHETIC, Splitter:B:47:0x0088] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void saveMonitoringStatusIfOn() {
        /*
            r8 = this;
            boolean r0 = r8.mStatePreservationIsOn
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            java.lang.String r0 = TAG
            java.lang.String r1 = "saveMonitoringStatusIfOn()"
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            org.altbeacon.beacon.logging.LogManager.m260d(r0, r1, r3)
            java.util.Map r0 = r8.getRegionsStateMap()
            int r0 = r0.size()
            r1 = 50
            if (r0 <= r1) goto L_0x002d
            java.lang.String r0 = TAG
            java.lang.String r1 = "Too many regions being monitored.  Will not persist region state"
            java.lang.Object[] r2 = new java.lang.Object[r2]
            org.altbeacon.beacon.logging.LogManager.m268w(r0, r1, r2)
            android.content.Context r0 = r8.mContext
            java.lang.String r1 = "org.altbeacon.beacon.service.monitoring_status_state"
            r0.deleteFile(r1)
            goto L_0x007f
        L_0x002d:
            r0 = 0
            android.content.Context r1 = r8.mContext     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            java.lang.String r3 = "org.altbeacon.beacon.service.monitoring_status_state"
            java.io.FileOutputStream r1 = r1.openFileOutput(r3, r2)     // Catch:{ IOException -> 0x0065, all -> 0x0062 }
            java.io.ObjectOutputStream r3 = new java.io.ObjectOutputStream     // Catch:{ IOException -> 0x005c, all -> 0x0057 }
            r3.<init>(r1)     // Catch:{ IOException -> 0x005c, all -> 0x0057 }
            java.util.Map r0 = r8.getRegionsStateMap()     // Catch:{ IOException -> 0x0052, all -> 0x004d }
            r3.writeObject(r0)     // Catch:{ IOException -> 0x0052, all -> 0x004d }
            if (r1 == 0) goto L_0x0047
            r1.close()     // Catch:{ IOException -> 0x0047 }
        L_0x0047:
            if (r3 == 0) goto L_0x007f
        L_0x0049:
            r3.close()     // Catch:{ IOException -> 0x007f }
            goto L_0x007f
        L_0x004d:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x0081
        L_0x0052:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x0067
        L_0x0057:
            r2 = move-exception
            r3 = r0
            r0 = r1
            r1 = r2
            goto L_0x0081
        L_0x005c:
            r3 = move-exception
            r7 = r3
            r3 = r0
            r0 = r1
            r1 = r7
            goto L_0x0067
        L_0x0062:
            r1 = move-exception
            r3 = r0
            goto L_0x0081
        L_0x0065:
            r1 = move-exception
            r3 = r0
        L_0x0067:
            java.lang.String r4 = TAG     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = "Error while saving monitored region states to file. %s "
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x0080 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x0080 }
            r6[r2] = r1     // Catch:{ all -> 0x0080 }
            org.altbeacon.beacon.logging.LogManager.m262e(r4, r5, r6)     // Catch:{ all -> 0x0080 }
            if (r0 == 0) goto L_0x007c
            r0.close()     // Catch:{ IOException -> 0x007c }
        L_0x007c:
            if (r3 == 0) goto L_0x007f
            goto L_0x0049
        L_0x007f:
            return
        L_0x0080:
            r1 = move-exception
        L_0x0081:
            if (r0 == 0) goto L_0x0086
            r0.close()     // Catch:{ IOException -> 0x0086 }
        L_0x0086:
            if (r3 == 0) goto L_0x008b
            r3.close()     // Catch:{ IOException -> 0x008b }
        L_0x008b:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.service.MonitoringStatus.saveMonitoringStatusIfOn():void");
    }

    /* access modifiers changed from: protected */
    public void updateMonitoringStatusTime(long j) {
        this.mContext.getFileStreamPath(STATUS_PRESERVATION_FILE_NAME).setLastModified(j);
    }

    /* access modifiers changed from: protected */
    public long getLastMonitoringStatusUpdateTime() {
        return this.mContext.getFileStreamPath(STATUS_PRESERVATION_FILE_NAME).lastModified();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00c0 A[Catch:{ all -> 0x00e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ca A[Catch:{ all -> 0x00e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00dc A[SYNTHETIC, Splitter:B:40:0x00dc] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00e6 A[SYNTHETIC, Splitter:B:47:0x00e6] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00eb A[SYNTHETIC, Splitter:B:51:0x00eb] */
    /* JADX WARNING: Removed duplicated region for block: B:62:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void restoreMonitoringStatus() {
        /*
            r10 = this;
            r0 = 0
            r1 = 0
            android.content.Context r2 = r10.mContext     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00b8, all -> 0x00b3 }
            java.lang.String r3 = "org.altbeacon.beacon.service.monitoring_status_state"
            java.io.FileInputStream r2 = r2.openFileInput(r3)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00b8, all -> 0x00b3 }
            java.io.ObjectInputStream r3 = new java.io.ObjectInputStream     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00ae, all -> 0x00ab }
            r3.<init>(r2)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00ae, all -> 0x00ab }
            java.lang.Object r0 = r3.readObject()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r4 = TAG     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r5.<init>()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r6 = "Restored region monitoring state for "
            r5.append(r6)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            int r6 = r0.size()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r5.append(r6)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r6 = " regions."
            r5.append(r6)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            org.altbeacon.beacon.logging.LogManager.m260d(r4, r5, r6)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.util.Set r4 = r0.keySet()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
        L_0x003e:
            boolean r5 = r4.hasNext()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            if (r5 == 0) goto L_0x007b
            java.lang.Object r5 = r4.next()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            org.altbeacon.beacon.Region r5 = (org.altbeacon.beacon.Region) r5     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r6 = TAG     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r7.<init>()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r8 = "Region  "
            r7.append(r8)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r7.append(r5)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r8 = " uniqueId: "
            r7.append(r8)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r8 = r5.getUniqueId()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r7.append(r8)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r8 = " state: "
            r7.append(r8)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.Object r5 = r0.get(r5)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r7.append(r5)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.String r5 = r7.toString()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.lang.Object[] r7 = new java.lang.Object[r1]     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            org.altbeacon.beacon.logging.LogManager.m260d(r6, r5, r7)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            goto L_0x003e
        L_0x007b:
            java.util.Collection r4 = r0.values()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
        L_0x0083:
            boolean r5 = r4.hasNext()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            if (r5 == 0) goto L_0x0099
            java.lang.Object r5 = r4.next()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            org.altbeacon.beacon.service.RegionMonitoringState r5 = (org.altbeacon.beacon.service.RegionMonitoringState) r5     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            boolean r6 = r5.getInside()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            if (r6 == 0) goto L_0x0083
            r5.markInside()     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            goto L_0x0083
        L_0x0099:
            java.util.Map<org.altbeacon.beacon.Region, org.altbeacon.beacon.service.RegionMonitoringState> r4 = r10.mRegionsStatesMap     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            r4.putAll(r0)     // Catch:{ IOException | ClassCastException | ClassNotFoundException -> 0x00a9 }
            if (r2 == 0) goto L_0x00a3
            r2.close()     // Catch:{ IOException -> 0x00a3 }
        L_0x00a3:
            if (r3 == 0) goto L_0x00e2
        L_0x00a5:
            r3.close()     // Catch:{ IOException -> 0x00e2 }
            goto L_0x00e2
        L_0x00a9:
            r0 = move-exception
            goto L_0x00bc
        L_0x00ab:
            r1 = move-exception
            r3 = r0
            goto L_0x00b6
        L_0x00ae:
            r3 = move-exception
            r9 = r3
            r3 = r0
            r0 = r9
            goto L_0x00bc
        L_0x00b3:
            r1 = move-exception
            r2 = r0
            r3 = r2
        L_0x00b6:
            r0 = r1
            goto L_0x00e4
        L_0x00b8:
            r2 = move-exception
            r3 = r0
            r0 = r2
            r2 = r3
        L_0x00bc:
            boolean r4 = r0 instanceof java.io.InvalidClassException     // Catch:{ all -> 0x00e3 }
            if (r4 == 0) goto L_0x00ca
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00e3 }
            java.lang.String r4 = "Serialized Monitoring State has wrong class. Just ignoring saved state..."
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x00e3 }
            org.altbeacon.beacon.logging.LogManager.m260d(r0, r4, r1)     // Catch:{ all -> 0x00e3 }
            goto L_0x00da
        L_0x00ca:
            java.lang.String r4 = TAG     // Catch:{ all -> 0x00e3 }
            java.lang.String r5 = "Deserialization exception, message: %s"
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x00e3 }
            java.lang.String r0 = r0.getMessage()     // Catch:{ all -> 0x00e3 }
            r6[r1] = r0     // Catch:{ all -> 0x00e3 }
            org.altbeacon.beacon.logging.LogManager.m262e(r4, r5, r6)     // Catch:{ all -> 0x00e3 }
        L_0x00da:
            if (r2 == 0) goto L_0x00df
            r2.close()     // Catch:{ IOException -> 0x00df }
        L_0x00df:
            if (r3 == 0) goto L_0x00e2
            goto L_0x00a5
        L_0x00e2:
            return
        L_0x00e3:
            r0 = move-exception
        L_0x00e4:
            if (r2 == 0) goto L_0x00e9
            r2.close()     // Catch:{ IOException -> 0x00e9 }
        L_0x00e9:
            if (r3 == 0) goto L_0x00ee
            r3.close()     // Catch:{ IOException -> 0x00ee }
        L_0x00ee:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.service.MonitoringStatus.restoreMonitoringStatus():void");
    }

    public synchronized void stopStatusPreservation() {
        this.mContext.deleteFile(STATUS_PRESERVATION_FILE_NAME);
        this.mStatePreservationIsOn = false;
    }

    public synchronized void startStatusPreservation() {
        if (!this.mStatePreservationIsOn) {
            this.mStatePreservationIsOn = true;
            saveMonitoringStatusIfOn();
        }
    }

    public boolean isStatePreservationOn() {
        return this.mStatePreservationIsOn;
    }

    public synchronized void clear() {
        this.mContext.deleteFile(STATUS_PRESERVATION_FILE_NAME);
        getRegionsStateMap().clear();
    }

    public void updateLocalState(Region region, Integer num) {
        RegionMonitoringState regionMonitoringState = (RegionMonitoringState) getRegionsStateMap().get(region);
        if (regionMonitoringState == null) {
            regionMonitoringState = addLocalRegion(region);
        }
        if (num != null) {
            if (num.intValue() == 0) {
                regionMonitoringState.markOutside();
            }
            if (num.intValue() == 1) {
                regionMonitoringState.markInside();
            }
        }
    }

    public void removeLocalRegion(Region region) {
        getRegionsStateMap().remove(region);
    }

    public RegionMonitoringState addLocalRegion(Region region) {
        return addLocalRegion(region, new Callback(null));
    }

    private RegionMonitoringState addLocalRegion(Region region, Callback callback) {
        if (getRegionsStateMap().containsKey(region)) {
            Iterator it = getRegionsStateMap().keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Region region2 = (Region) it.next();
                if (region2.equals(region)) {
                    if (region2.hasSameIdentifiers(region)) {
                        return (RegionMonitoringState) getRegionsStateMap().get(region2);
                    }
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Replacing region with unique identifier ");
                    sb.append(region.getUniqueId());
                    LogManager.m260d(str, sb.toString(), new Object[0]);
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Old definition: ");
                    sb2.append(region2);
                    LogManager.m260d(str2, sb2.toString(), new Object[0]);
                    String str3 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("New definition: ");
                    sb3.append(region);
                    LogManager.m260d(str3, sb3.toString(), new Object[0]);
                    LogManager.m260d(TAG, "clearing state", new Object[0]);
                    getRegionsStateMap().remove(region);
                }
            }
        }
        RegionMonitoringState regionMonitoringState = new RegionMonitoringState(callback);
        getRegionsStateMap().put(region, regionMonitoringState);
        return regionMonitoringState;
    }
}
