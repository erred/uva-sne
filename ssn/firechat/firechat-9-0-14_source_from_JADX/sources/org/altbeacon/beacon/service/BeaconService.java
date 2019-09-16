package org.altbeacon.beacon.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.MainThread;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.p000v4.app.NotificationCompat;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BuildConfig;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.scanner.CycledLeScanCallback;
import org.altbeacon.beacon.startup.StartupBroadcastReceiver;
import org.altbeacon.beacon.utils.ProcessUtils;
import org.altbeacon.bluetooth.BluetoothCrashResolver;

public class BeaconService extends Service {
    public static final int MSG_SET_SCAN_PERIODS = 6;
    public static final int MSG_START_MONITORING = 4;
    public static final int MSG_START_RANGING = 2;
    public static final int MSG_STOP_MONITORING = 5;
    public static final int MSG_STOP_RANGING = 3;
    public static final int MSG_SYNC_SETTINGS = 7;
    public static final String TAG = "BeaconService";
    private BluetoothCrashResolver bluetoothCrashResolver;
    private final Handler handler = new Handler();
    final Messenger mMessenger = new Messenger(new IncomingHandler(this));
    private ScanHelper mScanHelper;

    public class BeaconBinder extends Binder {
        public BeaconBinder() {
        }

        public BeaconService getService() {
            LogManager.m264i(BeaconService.TAG, "getService of BeaconBinder called", new Object[0]);
            return BeaconService.this;
        }
    }

    static class IncomingHandler extends Handler {
        private final WeakReference<BeaconService> mService;

        IncomingHandler(BeaconService beaconService) {
            super(Looper.getMainLooper());
            this.mService = new WeakReference<>(beaconService);
        }

        @MainThread
        public void handleMessage(Message message) {
            BeaconService beaconService = (BeaconService) this.mService.get();
            if (beaconService != null) {
                StartRMData fromBundle = StartRMData.fromBundle(message.getData());
                if (fromBundle != null) {
                    switch (message.what) {
                        case 2:
                            LogManager.m264i(BeaconService.TAG, "start ranging received", new Object[0]);
                            beaconService.startRangingBeaconsInRegion(fromBundle.getRegionData(), new Callback(fromBundle.getCallbackPackageName()));
                            beaconService.setScanPeriods(fromBundle.getScanPeriod(), fromBundle.getBetweenScanPeriod(), fromBundle.getBackgroundFlag());
                            return;
                        case 3:
                            LogManager.m264i(BeaconService.TAG, "stop ranging received", new Object[0]);
                            beaconService.stopRangingBeaconsInRegion(fromBundle.getRegionData());
                            beaconService.setScanPeriods(fromBundle.getScanPeriod(), fromBundle.getBetweenScanPeriod(), fromBundle.getBackgroundFlag());
                            return;
                        case 4:
                            LogManager.m264i(BeaconService.TAG, "start monitoring received", new Object[0]);
                            beaconService.startMonitoringBeaconsInRegion(fromBundle.getRegionData(), new Callback(fromBundle.getCallbackPackageName()));
                            beaconService.setScanPeriods(fromBundle.getScanPeriod(), fromBundle.getBetweenScanPeriod(), fromBundle.getBackgroundFlag());
                            return;
                        case 5:
                            LogManager.m264i(BeaconService.TAG, "stop monitoring received", new Object[0]);
                            beaconService.stopMonitoringBeaconsInRegion(fromBundle.getRegionData());
                            beaconService.setScanPeriods(fromBundle.getScanPeriod(), fromBundle.getBetweenScanPeriod(), fromBundle.getBackgroundFlag());
                            return;
                        case 6:
                            LogManager.m264i(BeaconService.TAG, "set scan intervals received", new Object[0]);
                            beaconService.setScanPeriods(fromBundle.getScanPeriod(), fromBundle.getBetweenScanPeriod(), fromBundle.getBackgroundFlag());
                            return;
                        default:
                            super.handleMessage(message);
                            return;
                    }
                } else if (message.what == 7) {
                    LogManager.m264i(BeaconService.TAG, "Received settings update from other process", new Object[0]);
                    SettingsData fromBundle2 = SettingsData.fromBundle(message.getData());
                    if (fromBundle2 != null) {
                        fromBundle2.apply(beaconService);
                    } else {
                        LogManager.m268w(BeaconService.TAG, "Settings data missing", new Object[0]);
                    }
                } else {
                    String str = BeaconService.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Received unknown message from other process : ");
                    sb.append(message.what);
                    LogManager.m264i(str, sb.toString(), new Object[0]);
                }
            }
        }
    }

    @MainThread
    public void onCreate() {
        this.bluetoothCrashResolver = new BluetoothCrashResolver(this);
        this.bluetoothCrashResolver.start();
        this.mScanHelper = new ScanHelper(this);
        if (this.mScanHelper.getCycledScanner() == null) {
            this.mScanHelper.createCycledLeScanner(false, this.bluetoothCrashResolver);
        }
        this.mScanHelper.setMonitoringStatus(MonitoringStatus.getInstanceForApplication(this));
        this.mScanHelper.setRangedRegionState(new HashMap());
        this.mScanHelper.setBeaconParsers(new HashSet());
        this.mScanHelper.setExtraDataBeaconTracker(new ExtraDataBeaconTracker());
        BeaconManager instanceForApplication = BeaconManager.getInstanceForApplication(getApplicationContext());
        instanceForApplication.setScannerInSameProcess(true);
        if (instanceForApplication.isMainProcess()) {
            LogManager.m264i(TAG, "beaconService version %s is starting up on the main process", BuildConfig.VERSION_NAME);
        } else {
            LogManager.m264i(TAG, "beaconService version %s is starting up on a separate process", BuildConfig.VERSION_NAME);
            ProcessUtils processUtils = new ProcessUtils(this);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("beaconService PID is ");
            sb.append(processUtils.getPid());
            sb.append(" with process name ");
            sb.append(processUtils.getProcessName());
            LogManager.m264i(str, sb.toString(), new Object[0]);
        }
        try {
            ServiceInfo serviceInfo = getPackageManager().getServiceInfo(new ComponentName(this, BeaconService.class), 128);
            if (!(serviceInfo == null || serviceInfo.metaData == null || serviceInfo.metaData.get("longScanForcingEnabled") == null || !serviceInfo.metaData.get("longScanForcingEnabled").toString().equals("true"))) {
                LogManager.m264i(TAG, "longScanForcingEnabled to keep scans going on Android N for > 30 minutes", new Object[0]);
                this.mScanHelper.getCycledScanner().setLongScanForcingEnabled(true);
            }
        } catch (NameNotFoundException unused) {
        }
        this.mScanHelper.reloadParsers();
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(this, BeaconManager.getDistanceModelUpdateUrl()));
        try {
            this.mScanHelper.setSimulatedScanData((List) Class.forName("org.altbeacon.beacon.SimulatedScanData").getField("beacons").get(null));
        } catch (ClassNotFoundException unused2) {
            LogManager.m260d(TAG, "No org.altbeacon.beacon.SimulatedScanData class exists.", new Object[0]);
        } catch (Exception e) {
            LogManager.m263e(e, TAG, "Cannot get simulated Scan data.  Make sure your org.altbeacon.beacon.SimulatedScanData class defines a field with the signature 'public static List<Beacon> beacons'", new Object[0]);
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        String str;
        String str2 = TAG;
        if (intent == null) {
            str = "starting with null intent";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("starting with intent ");
            sb.append(intent.toString());
            str = sb.toString();
        }
        LogManager.m264i(str2, str, new Object[0]);
        return super.onStartCommand(intent, i, i2);
    }

    public IBinder onBind(Intent intent) {
        LogManager.m264i(TAG, "binding", new Object[0]);
        return this.mMessenger.getBinder();
    }

    public boolean onUnbind(Intent intent) {
        LogManager.m264i(TAG, "unbinding", new Object[0]);
        return false;
    }

    @MainThread
    public void onDestroy() {
        LogManager.m262e(TAG, "onDestroy()", new Object[0]);
        if (VERSION.SDK_INT < 18) {
            LogManager.m268w(TAG, "Not supported prior to API 18.", new Object[0]);
            return;
        }
        this.bluetoothCrashResolver.stop();
        LogManager.m264i(TAG, "onDestroy called.  stopping scanning", new Object[0]);
        this.handler.removeCallbacksAndMessages(null);
        this.mScanHelper.getCycledScanner().stop();
        this.mScanHelper.getCycledScanner().destroy();
        this.mScanHelper.getMonitoringStatus().stopStatusPreservation();
    }

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        LogManager.m260d(TAG, "task removed", new Object[0]);
        if (VERSION.RELEASE.contains("4.4.1") || VERSION.RELEASE.contains("4.4.2") || VERSION.RELEASE.contains("4.4.3")) {
            ((AlarmManager) getApplicationContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, System.currentTimeMillis() + 1000, getRestartIntent());
            LogManager.m260d(TAG, "Setting a wakeup alarm to go off due to Android 4.4.2 service restarting bug.", new Object[0]);
        }
    }

    private PendingIntent getRestartIntent() {
        return PendingIntent.getBroadcast(getApplicationContext(), 1, new Intent(getApplicationContext(), StartupBroadcastReceiver.class), ErrorDialogData.SUPPRESSED);
    }

    @MainThread
    public void startRangingBeaconsInRegion(Region region, Callback callback) {
        synchronized (this.mScanHelper.getRangedRegionState()) {
            if (this.mScanHelper.getRangedRegionState().containsKey(region)) {
                LogManager.m264i(TAG, "Already ranging that region -- will replace existing region.", new Object[0]);
                this.mScanHelper.getRangedRegionState().remove(region);
            }
            this.mScanHelper.getRangedRegionState().put(region, new RangeState(callback));
            LogManager.m260d(TAG, "Currently ranging %s regions.", Integer.valueOf(this.mScanHelper.getRangedRegionState().size()));
        }
        this.mScanHelper.getCycledScanner().start();
    }

    @MainThread
    public void stopRangingBeaconsInRegion(Region region) {
        int size;
        synchronized (this.mScanHelper.getRangedRegionState()) {
            this.mScanHelper.getRangedRegionState().remove(region);
            size = this.mScanHelper.getRangedRegionState().size();
            LogManager.m260d(TAG, "Currently ranging %s regions.", Integer.valueOf(this.mScanHelper.getRangedRegionState().size()));
        }
        if (size == 0 && this.mScanHelper.getMonitoringStatus().regionsCount() == 0) {
            this.mScanHelper.getCycledScanner().stop();
        }
    }

    @MainThread
    public void startMonitoringBeaconsInRegion(Region region, Callback callback) {
        LogManager.m260d(TAG, "startMonitoring called", new Object[0]);
        this.mScanHelper.getMonitoringStatus().addRegion(region, callback);
        LogManager.m260d(TAG, "Currently monitoring %s regions.", Integer.valueOf(this.mScanHelper.getMonitoringStatus().regionsCount()));
        this.mScanHelper.getCycledScanner().start();
    }

    @MainThread
    public void stopMonitoringBeaconsInRegion(Region region) {
        LogManager.m260d(TAG, "stopMonitoring called", new Object[0]);
        this.mScanHelper.getMonitoringStatus().removeRegion(region);
        LogManager.m260d(TAG, "Currently monitoring %s regions.", Integer.valueOf(this.mScanHelper.getMonitoringStatus().regionsCount()));
        if (this.mScanHelper.getMonitoringStatus().regionsCount() == 0 && this.mScanHelper.getRangedRegionState().size() == 0) {
            this.mScanHelper.getCycledScanner().stop();
        }
    }

    @MainThread
    public void setScanPeriods(long j, long j2, boolean z) {
        this.mScanHelper.getCycledScanner().setScanPeriods(j, j2, z);
    }

    public void reloadParsers() {
        this.mScanHelper.reloadParsers();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({Scope.TESTS})
    public CycledLeScanCallback getCycledLeScanCallback() {
        return this.mScanHelper.getCycledLeScanCallback();
    }
}
