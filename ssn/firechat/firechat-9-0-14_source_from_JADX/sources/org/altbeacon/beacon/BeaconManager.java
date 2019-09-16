package org.altbeacon.beacon;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.logging.Loggers;
import org.altbeacon.beacon.service.BeaconService;
import org.altbeacon.beacon.service.Callback;
import org.altbeacon.beacon.service.MonitoringStatus;
import org.altbeacon.beacon.service.RangeState;
import org.altbeacon.beacon.service.RangedBeacon;
import org.altbeacon.beacon.service.RegionMonitoringState;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;
import org.altbeacon.beacon.service.ScanJobScheduler;
import org.altbeacon.beacon.service.SettingsData;
import org.altbeacon.beacon.service.StartRMData;
import org.altbeacon.beacon.service.scanner.NonBeaconLeScanCallback;
import org.altbeacon.beacon.simulator.BeaconSimulator;
import org.altbeacon.beacon.utils.ProcessUtils;

public class BeaconManager {
    public static final long DEFAULT_BACKGROUND_BETWEEN_SCAN_PERIOD = 300000;
    public static final long DEFAULT_BACKGROUND_SCAN_PERIOD = 10000;
    public static final long DEFAULT_EXIT_PERIOD = 10000;
    public static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 0;
    public static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1100;
    private static final Object SINGLETON_LOCK = new Object();
    @NonNull
    private static final String TAG = "BeaconManager";
    @Nullable
    protected static BeaconSimulator beaconSimulator = null;
    protected static String distanceModelUpdateUrl = "http://data.altbeacon.org/android-distance.json";
    protected static Class rssiFilterImplClass = RunningAverageRssiFilter.class;
    private static boolean sAndroidLScanningDisabled = false;
    private static long sExitRegionPeriod = 10000;
    @Nullable
    protected static volatile BeaconManager sInstance = null;
    private static boolean sManifestCheckingDisabled = false;
    private long backgroundBetweenScanPeriod;
    private long backgroundScanPeriod;
    @NonNull
    private final List<BeaconParser> beaconParsers = new CopyOnWriteArrayList();
    /* access modifiers changed from: private */
    @NonNull
    public final ConcurrentMap<BeaconConsumer, ConsumerInfo> consumers = new ConcurrentHashMap();
    @Nullable
    protected RangeNotifier dataRequestNotifier = null;
    private long foregroundBetweenScanPeriod;
    private long foregroundScanPeriod;
    private boolean mBackgroundMode;
    private boolean mBackgroundModeUninitialized;
    @NonNull
    private final Context mContext;
    private boolean mMainProcess;
    @Nullable
    private NonBeaconLeScanCallback mNonBeaconLeScanCallback;
    private boolean mRegionStatePersistenceEnabled;
    /* access modifiers changed from: private */
    @Nullable
    public Boolean mScannerInSameProcess;
    private boolean mScheduledScanJobsEnabled;
    @NonNull
    protected final Set<MonitorNotifier> monitorNotifiers = new CopyOnWriteArraySet();
    @NonNull
    protected final Set<RangeNotifier> rangeNotifiers = new CopyOnWriteArraySet();
    @NonNull
    private final ArrayList<Region> rangedRegions = new ArrayList<>();
    /* access modifiers changed from: private */
    @Nullable
    public Messenger serviceMessenger = null;

    private class BeaconServiceConnection implements ServiceConnection {
        private BeaconServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogManager.m260d(BeaconManager.TAG, "we have a connection to the service now", new Object[0]);
            if (BeaconManager.this.mScannerInSameProcess == null) {
                BeaconManager.this.mScannerInSameProcess = Boolean.valueOf(false);
            }
            BeaconManager.this.serviceMessenger = new Messenger(iBinder);
            BeaconManager.this.applySettings();
            synchronized (BeaconManager.this.consumers) {
                for (Entry entry : BeaconManager.this.consumers.entrySet()) {
                    if (!((ConsumerInfo) entry.getValue()).isConnected) {
                        ((BeaconConsumer) entry.getKey()).onBeaconServiceConnect();
                        ((ConsumerInfo) entry.getValue()).isConnected = true;
                    }
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            LogManager.m262e(BeaconManager.TAG, "onServiceDisconnected", new Object[0]);
            BeaconManager.this.serviceMessenger = null;
        }
    }

    private class ConsumerInfo {
        @NonNull
        public BeaconServiceConnection beaconServiceConnection;
        public boolean isConnected;

        public ConsumerInfo() {
            this.isConnected = false;
            this.isConnected = false;
            this.beaconServiceConnection = new BeaconServiceConnection();
        }
    }

    public class ServiceNotDeclaredException extends RuntimeException {
        public ServiceNotDeclaredException() {
            super("The BeaconService is not properly declared in AndroidManifest.xml.  If using Eclipse, please verify that your project.properties has manifestmerger.enabled=true");
        }
    }

    @Deprecated
    public static void setDebug(boolean z) {
        if (z) {
            LogManager.setLogger(Loggers.verboseLogger());
            LogManager.setVerboseLoggingEnabled(true);
            return;
        }
        LogManager.setLogger(Loggers.empty());
        LogManager.setVerboseLoggingEnabled(false);
    }

    public void setForegroundScanPeriod(long j) {
        this.foregroundScanPeriod = j;
    }

    public void setForegroundBetweenScanPeriod(long j) {
        this.foregroundBetweenScanPeriod = j;
    }

    public void setBackgroundScanPeriod(long j) {
        this.backgroundScanPeriod = j;
    }

    public void setBackgroundBetweenScanPeriod(long j) {
        this.backgroundBetweenScanPeriod = j;
    }

    public static void setRegionExitPeriod(long j) {
        sExitRegionPeriod = j;
        BeaconManager beaconManager = sInstance;
        if (beaconManager != null) {
            beaconManager.applySettings();
        }
    }

    public static long getRegionExitPeriod() {
        return sExitRegionPeriod;
    }

    @NonNull
    public static BeaconManager getInstanceForApplication(@NonNull Context context) {
        BeaconManager beaconManager = sInstance;
        if (beaconManager == null) {
            synchronized (SINGLETON_LOCK) {
                beaconManager = sInstance;
                if (beaconManager == null) {
                    beaconManager = new BeaconManager(context);
                    sInstance = beaconManager;
                }
            }
        }
        return beaconManager;
    }

    protected BeaconManager(@NonNull Context context) {
        boolean z = true;
        this.mRegionStatePersistenceEnabled = true;
        this.mBackgroundMode = false;
        this.mBackgroundModeUninitialized = true;
        this.mMainProcess = false;
        this.mScannerInSameProcess = null;
        this.mScheduledScanJobsEnabled = false;
        this.foregroundScanPeriod = DEFAULT_FOREGROUND_SCAN_PERIOD;
        this.foregroundBetweenScanPeriod = 0;
        this.backgroundScanPeriod = 10000;
        this.backgroundBetweenScanPeriod = 300000;
        this.mContext = context.getApplicationContext();
        checkIfMainProcess();
        if (!sManifestCheckingDisabled) {
            verifyServiceDeclaration();
        }
        this.beaconParsers.add(new AltBeaconParser());
        if (VERSION.SDK_INT < 26) {
            z = false;
        }
        this.mScheduledScanJobsEnabled = z;
    }

    public boolean isMainProcess() {
        return this.mMainProcess;
    }

    public boolean isScannerInDifferentProcess() {
        return this.mScannerInSameProcess != null && !this.mScannerInSameProcess.booleanValue();
    }

    public void setScannerInSameProcess(boolean z) {
        this.mScannerInSameProcess = Boolean.valueOf(z);
    }

    /* access modifiers changed from: protected */
    public void checkIfMainProcess() {
        ProcessUtils processUtils = new ProcessUtils(this.mContext);
        String processName = processUtils.getProcessName();
        String packageName = processUtils.getPackageName();
        int pid = processUtils.getPid();
        this.mMainProcess = processUtils.isMainProcess();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("BeaconManager started up on pid ");
        sb.append(pid);
        sb.append(" named '");
        sb.append(processName);
        sb.append("' for application package '");
        sb.append(packageName);
        sb.append("'.  isMainProcess=");
        sb.append(this.mMainProcess);
        LogManager.m264i(str, sb.toString(), new Object[0]);
    }

    @NonNull
    public List<BeaconParser> getBeaconParsers() {
        return this.beaconParsers;
    }

    @TargetApi(18)
    public boolean checkAvailability() throws BleNotAvailableException {
        if (isBleAvailable()) {
            return ((BluetoothManager) this.mContext.getSystemService("bluetooth")).getAdapter().isEnabled();
        }
        throw new BleNotAvailableException("Bluetooth LE not supported by this device");
    }

    public void bind(@NonNull BeaconConsumer beaconConsumer) {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            LogManager.m268w(TAG, "This device does not support bluetooth LE.  Will not start beacon scanning.", new Object[0]);
        } else if (this.mScheduledScanJobsEnabled) {
            LogManager.m260d(TAG, "Not starting beacon scanning service. Using scheduled jobs", new Object[0]);
            beaconConsumer.onBeaconServiceConnect();
        } else {
            synchronized (this.consumers) {
                ConsumerInfo consumerInfo = new ConsumerInfo();
                if (((ConsumerInfo) this.consumers.putIfAbsent(beaconConsumer, consumerInfo)) != null) {
                    LogManager.m260d(TAG, "This consumer is already bound", new Object[0]);
                } else {
                    LogManager.m260d(TAG, "This consumer is not bound.  binding: %s", beaconConsumer);
                    beaconConsumer.bindService(new Intent(beaconConsumer.getApplicationContext(), BeaconService.class), consumerInfo.beaconServiceConnection, 1);
                    LogManager.m260d(TAG, "consumer count is now: %s", Integer.valueOf(this.consumers.size()));
                }
            }
        }
    }

    public void unbind(@NonNull BeaconConsumer beaconConsumer) {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
            return;
        }
        synchronized (this.consumers) {
            if (this.consumers.containsKey(beaconConsumer)) {
                LogManager.m260d(TAG, "Unbinding", new Object[0]);
                beaconConsumer.unbindService(((ConsumerInfo) this.consumers.get(beaconConsumer)).beaconServiceConnection);
                this.consumers.remove(beaconConsumer);
                if (this.consumers.size() == 0) {
                    this.serviceMessenger = null;
                    this.mBackgroundMode = false;
                }
            } else {
                LogManager.m260d(TAG, "This consumer is not bound to: %s", beaconConsumer);
                LogManager.m260d(TAG, "Bound consumers: ", new Object[0]);
                for (Entry value : this.consumers.entrySet()) {
                    LogManager.m260d(TAG, String.valueOf(value.getValue()), new Object[0]);
                }
            }
        }
    }

    public boolean isBound(@NonNull BeaconConsumer beaconConsumer) {
        boolean z;
        synchronized (this.consumers) {
            if (beaconConsumer != null) {
                try {
                    if (!(this.consumers.get(beaconConsumer) == null || this.serviceMessenger == null)) {
                        z = true;
                    }
                } finally {
                }
            }
            z = false;
        }
        return z;
    }

    public boolean isAnyConsumerBound() {
        boolean z;
        synchronized (this.consumers) {
            z = this.consumers.isEmpty() && this.serviceMessenger != null;
        }
        return z;
    }

    public void setBackgroundMode(boolean z) {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
            return;
        }
        this.mBackgroundModeUninitialized = false;
        if (z != this.mBackgroundMode) {
            this.mBackgroundMode = z;
            try {
                updateScanPeriods();
            } catch (RemoteException unused) {
                LogManager.m262e(TAG, "Cannot contact service to set scan periods", new Object[0]);
            }
        }
    }

    public void setEnableScheduledScanJobs(boolean z) {
        if (isAnyConsumerBound()) {
            LogManager.m262e(TAG, "ScanJob may not be configured because a consumer is already bound.", new Object[0]);
            throw new IllegalStateException("Method must be called before calling bind()");
        } else if (!z || VERSION.SDK_INT >= 21) {
            this.mScheduledScanJobsEnabled = z;
        } else {
            LogManager.m262e(TAG, "ScanJob may not be configured because JobScheduler is not availble prior to Android 5.0", new Object[0]);
        }
    }

    public boolean getScheduledScanJobsEnabled() {
        return this.mScheduledScanJobsEnabled;
    }

    public boolean getBackgroundMode() {
        return this.mBackgroundMode;
    }

    public long getBackgroundScanPeriod() {
        return this.backgroundScanPeriod;
    }

    public long getBackgroundBetweenScanPeriod() {
        return this.backgroundBetweenScanPeriod;
    }

    public long getForegroundScanPeriod() {
        return this.foregroundScanPeriod;
    }

    public long getForegroundBetweenScanPeriod() {
        return this.foregroundBetweenScanPeriod;
    }

    public boolean isBackgroundModeUninitialized() {
        return this.mBackgroundModeUninitialized;
    }

    @Deprecated
    public void setRangeNotifier(@Nullable RangeNotifier rangeNotifier) {
        this.rangeNotifiers.clear();
        if (rangeNotifier != null) {
            addRangeNotifier(rangeNotifier);
        }
    }

    public void addRangeNotifier(@NonNull RangeNotifier rangeNotifier) {
        if (rangeNotifier != null) {
            this.rangeNotifiers.add(rangeNotifier);
        }
    }

    public boolean removeRangeNotifier(@NonNull RangeNotifier rangeNotifier) {
        return this.rangeNotifiers.remove(rangeNotifier);
    }

    public void removeAllRangeNotifiers() {
        this.rangeNotifiers.clear();
    }

    @Deprecated
    public void setMonitorNotifier(@Nullable MonitorNotifier monitorNotifier) {
        if (!determineIfCalledFromSeparateScannerProcess()) {
            this.monitorNotifiers.clear();
            if (monitorNotifier != null) {
                addMonitorNotifier(monitorNotifier);
            }
        }
    }

    public void addMonitorNotifier(@NonNull MonitorNotifier monitorNotifier) {
        if (!determineIfCalledFromSeparateScannerProcess() && monitorNotifier != null) {
            this.monitorNotifiers.add(monitorNotifier);
        }
    }

    @Deprecated
    public boolean removeMonitoreNotifier(@NonNull MonitorNotifier monitorNotifier) {
        return removeMonitorNotifier(monitorNotifier);
    }

    public boolean removeMonitorNotifier(@NonNull MonitorNotifier monitorNotifier) {
        if (determineIfCalledFromSeparateScannerProcess()) {
            return false;
        }
        return this.monitorNotifiers.remove(monitorNotifier);
    }

    public void removeAllMonitorNotifiers() {
        if (!determineIfCalledFromSeparateScannerProcess()) {
            this.monitorNotifiers.clear();
        }
    }

    @Deprecated
    public void setRegionStatePeristenceEnabled(boolean z) {
        setRegionStatePersistenceEnabled(z);
    }

    public void setRegionStatePersistenceEnabled(boolean z) {
        this.mRegionStatePersistenceEnabled = z;
        if (!isScannerInDifferentProcess()) {
            if (z) {
                MonitoringStatus.getInstanceForApplication(this.mContext).startStatusPreservation();
            } else {
                MonitoringStatus.getInstanceForApplication(this.mContext).stopStatusPreservation();
            }
        }
        applySettings();
    }

    public boolean isRegionStatePersistenceEnabled() {
        return this.mRegionStatePersistenceEnabled;
    }

    public void requestStateForRegion(@NonNull Region region) {
        if (!determineIfCalledFromSeparateScannerProcess()) {
            RegionMonitoringState stateOf = MonitoringStatus.getInstanceForApplication(this.mContext).stateOf(region);
            int i = 0;
            if (stateOf != null && stateOf.getInside()) {
                i = 1;
            }
            for (MonitorNotifier didDetermineStateForRegion : this.monitorNotifiers) {
                didDetermineStateForRegion.didDetermineStateForRegion(i, region);
            }
        }
    }

    @TargetApi(18)
    public void startRangingBeaconsInRegion(@NonNull Region region) throws RemoteException {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!determineIfCalledFromSeparateScannerProcess()) {
            synchronized (this.rangedRegions) {
                this.rangedRegions.add(region);
            }
            applyChangesToServices(2, region);
        }
    }

    @TargetApi(18)
    public void stopRangingBeaconsInRegion(@NonNull Region region) throws RemoteException {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!determineIfCalledFromSeparateScannerProcess()) {
            synchronized (this.rangedRegions) {
                Region region2 = null;
                Iterator it = this.rangedRegions.iterator();
                while (it.hasNext()) {
                    Region region3 = (Region) it.next();
                    if (region.getUniqueId().equals(region3.getUniqueId())) {
                        region2 = region3;
                    }
                }
                this.rangedRegions.remove(region2);
            }
            applyChangesToServices(3, region);
        }
    }

    public void applySettings() {
        if (!determineIfCalledFromSeparateScannerProcess()) {
            if (!isAnyConsumerBound()) {
                LogManager.m260d(TAG, "Not synchronizing settings to service, as it has not started up yet", new Object[0]);
            } else if (isScannerInDifferentProcess()) {
                LogManager.m260d(TAG, "Synchronizing settings to service", new Object[0]);
                syncSettingsToService();
            } else {
                LogManager.m260d(TAG, "Not synchronizing settings to service, as it is in the same process", new Object[0]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void syncSettingsToService() {
        if (this.mScheduledScanJobsEnabled) {
            ScanJobScheduler.getInstance().applySettingsToScheduledJob(this.mContext, this);
            return;
        }
        try {
            applyChangesToServices(7, null);
        } catch (RemoteException e) {
            LogManager.m262e(TAG, "Failed to sync settings to service", e);
        }
    }

    @TargetApi(18)
    public void startMonitoringBeaconsInRegion(@NonNull Region region) throws RemoteException {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!determineIfCalledFromSeparateScannerProcess()) {
            if (this.mScheduledScanJobsEnabled) {
                MonitoringStatus.getInstanceForApplication(this.mContext).addRegion(region, new Callback(callbackPackageName()));
            }
            applyChangesToServices(4, region);
            if (isScannerInDifferentProcess()) {
                MonitoringStatus.getInstanceForApplication(this.mContext).addLocalRegion(region);
            }
            requestStateForRegion(region);
        }
    }

    @TargetApi(18)
    public void stopMonitoringBeaconsInRegion(@NonNull Region region) throws RemoteException {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!determineIfCalledFromSeparateScannerProcess()) {
            if (this.mScheduledScanJobsEnabled) {
                MonitoringStatus.getInstanceForApplication(this.mContext).removeRegion(region);
            }
            applyChangesToServices(5, region);
            if (isScannerInDifferentProcess()) {
                MonitoringStatus.getInstanceForApplication(this.mContext).removeLocalRegion(region);
            }
        }
    }

    @TargetApi(18)
    public void updateScanPeriods() throws RemoteException {
        if (!isBleAvailable()) {
            LogManager.m268w(TAG, "Method invocation will be ignored.", new Object[0]);
        } else if (!determineIfCalledFromSeparateScannerProcess()) {
            LogManager.m260d(TAG, "updating background flag to %s", Boolean.valueOf(this.mBackgroundMode));
            LogManager.m260d(TAG, "updating scan period to %s, %s", Long.valueOf(getScanPeriod()), Long.valueOf(getBetweenScanPeriod()));
            applyChangesToServices(6, null);
        }
    }

    @TargetApi(18)
    private void applyChangesToServices(int i, Region region) throws RemoteException {
        if (this.mScheduledScanJobsEnabled) {
            ScanJobScheduler.getInstance().applySettingsToScheduledJob(this.mContext, this);
        } else if (this.serviceMessenger == null) {
            throw new RemoteException("The BeaconManager is not bound to the service.  Call beaconManager.bind(BeaconConsumer consumer) and wait for a callback to onBeaconServiceConnect()");
        } else {
            Message obtain = Message.obtain(null, i, 0, 0);
            if (i == 6) {
                StartRMData startRMData = new StartRMData(getScanPeriod(), getBetweenScanPeriod(), this.mBackgroundMode);
                obtain.setData(startRMData.toBundle());
            } else if (i == 7) {
                obtain.setData(new SettingsData().collect(this.mContext).toBundle());
            } else {
                StartRMData startRMData2 = new StartRMData(region, callbackPackageName(), getScanPeriod(), getBetweenScanPeriod(), this.mBackgroundMode);
                obtain.setData(startRMData2.toBundle());
            }
            this.serviceMessenger.send(obtain);
        }
    }

    private String callbackPackageName() {
        String packageName = this.mContext.getPackageName();
        LogManager.m260d(TAG, "callback packageName: %s", packageName);
        return packageName;
    }

    @Nullable
    @Deprecated
    public MonitorNotifier getMonitoringNotifier() {
        Iterator it = this.monitorNotifiers.iterator();
        if (it.hasNext()) {
            return (MonitorNotifier) it.next();
        }
        return null;
    }

    @NonNull
    public Set<MonitorNotifier> getMonitoringNotifiers() {
        return Collections.unmodifiableSet(this.monitorNotifiers);
    }

    @Nullable
    @Deprecated
    public RangeNotifier getRangingNotifier() {
        Iterator it = this.rangeNotifiers.iterator();
        if (it.hasNext()) {
            return (RangeNotifier) it.next();
        }
        return null;
    }

    @NonNull
    public Set<RangeNotifier> getRangingNotifiers() {
        return Collections.unmodifiableSet(this.rangeNotifiers);
    }

    @NonNull
    public Collection<Region> getMonitoredRegions() {
        return MonitoringStatus.getInstanceForApplication(this.mContext).regions();
    }

    @NonNull
    public Collection<Region> getRangedRegions() {
        ArrayList arrayList;
        synchronized (this.rangedRegions) {
            arrayList = new ArrayList(this.rangedRegions);
        }
        return arrayList;
    }

    @Deprecated
    public static void logDebug(String str, String str2) {
        LogManager.m260d(str, str2, new Object[0]);
    }

    @Deprecated
    public static void logDebug(String str, String str2, Throwable th) {
        LogManager.m261d(th, str, str2, new Object[0]);
    }

    public static String getDistanceModelUpdateUrl() {
        return distanceModelUpdateUrl;
    }

    public static void setDistanceModelUpdateUrl(@NonNull String str) {
        warnIfScannerNotInSameProcess();
        distanceModelUpdateUrl = str;
    }

    public static void setRssiFilterImplClass(@NonNull Class cls) {
        warnIfScannerNotInSameProcess();
        rssiFilterImplClass = cls;
    }

    public static Class getRssiFilterImplClass() {
        return rssiFilterImplClass;
    }

    public static void setUseTrackingCache(boolean z) {
        RangeState.setUseTrackingCache(z);
        if (sInstance != null) {
            sInstance.applySettings();
        }
    }

    public void setMaxTrackingAge(int i) {
        RangedBeacon.setMaxTrackinAge(i);
    }

    public static void setBeaconSimulator(BeaconSimulator beaconSimulator2) {
        warnIfScannerNotInSameProcess();
        beaconSimulator = beaconSimulator2;
    }

    @Nullable
    public static BeaconSimulator getBeaconSimulator() {
        return beaconSimulator;
    }

    /* access modifiers changed from: protected */
    public void setDataRequestNotifier(@Nullable RangeNotifier rangeNotifier) {
        this.dataRequestNotifier = rangeNotifier;
    }

    /* access modifiers changed from: protected */
    @Nullable
    public RangeNotifier getDataRequestNotifier() {
        return this.dataRequestNotifier;
    }

    @Nullable
    public NonBeaconLeScanCallback getNonBeaconLeScanCallback() {
        return this.mNonBeaconLeScanCallback;
    }

    public void setNonBeaconLeScanCallback(@Nullable NonBeaconLeScanCallback nonBeaconLeScanCallback) {
        this.mNonBeaconLeScanCallback = nonBeaconLeScanCallback;
    }

    private boolean isBleAvailable() {
        if (VERSION.SDK_INT < 18) {
            LogManager.m268w(TAG, "Bluetooth LE not supported prior to API 18.", new Object[0]);
            return false;
        } else if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            return true;
        } else {
            LogManager.m268w(TAG, "This device does not support bluetooth LE.", new Object[0]);
            return false;
        }
    }

    private long getScanPeriod() {
        if (this.mBackgroundMode) {
            return this.backgroundScanPeriod;
        }
        return this.foregroundScanPeriod;
    }

    private long getBetweenScanPeriod() {
        if (this.mBackgroundMode) {
            return this.backgroundBetweenScanPeriod;
        }
        return this.foregroundBetweenScanPeriod;
    }

    private void verifyServiceDeclaration() {
        List queryIntentServices = this.mContext.getPackageManager().queryIntentServices(new Intent(this.mContext, BeaconService.class), 65536);
        if (queryIntentServices != null && queryIntentServices.isEmpty()) {
            throw new ServiceNotDeclaredException();
        }
    }

    public static boolean isAndroidLScanningDisabled() {
        return sAndroidLScanningDisabled;
    }

    public static void setAndroidLScanningDisabled(boolean z) {
        sAndroidLScanningDisabled = z;
        BeaconManager beaconManager = sInstance;
        if (beaconManager != null) {
            beaconManager.applySettings();
        }
    }

    @Deprecated
    public static void setsManifestCheckingDisabled(boolean z) {
        sManifestCheckingDisabled = z;
    }

    public static void setManifestCheckingDisabled(boolean z) {
        sManifestCheckingDisabled = z;
    }

    public static boolean getManifestCheckingDisabled() {
        return sManifestCheckingDisabled;
    }

    private boolean determineIfCalledFromSeparateScannerProcess() {
        if (!isScannerInDifferentProcess() || isMainProcess()) {
            return false;
        }
        LogManager.m268w(TAG, "Ranging/Monitoring may not be controlled from a separate BeaconScanner process.  To remove this warning, please wrap this call in: if (beaconManager.isMainProcess())", new Object[0]);
        return true;
    }

    private static void warnIfScannerNotInSameProcess() {
        BeaconManager beaconManager = sInstance;
        if (beaconManager != null && beaconManager.isScannerInDifferentProcess()) {
            LogManager.m268w(TAG, "Unsupported configuration change made for BeaconScanner in separate process", new Object[0]);
        }
    }
}
