package org.altbeacon.beacon.service;

import android.app.job.JobInfo.Builder;
import android.app.job.JobScheduler;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.List;
import org.altbeacon.beacon.BeaconLocalBroadcastProcessor;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.logging.LogManager;

@RequiresApi(api = 21)
public class ScanJobScheduler {
    private static final long MIN_MILLIS_BETWEEN_SCAN_JOB_SCHEDULING = 10000;
    private static final Object SINGLETON_LOCK = new Object();
    private static final String TAG = "ScanJobScheduler";
    @Nullable
    private static volatile ScanJobScheduler sInstance;
    @NonNull
    private List<ScanResult> mBackgroundScanResultQueue = new ArrayList();
    @Nullable
    private BeaconLocalBroadcastProcessor mBeaconNotificationProcessor;
    @NonNull
    private Long mScanJobScheduleTime = Long.valueOf(0);

    @NonNull
    public static ScanJobScheduler getInstance() {
        ScanJobScheduler scanJobScheduler = sInstance;
        if (scanJobScheduler == null) {
            synchronized (SINGLETON_LOCK) {
                scanJobScheduler = sInstance;
                if (scanJobScheduler == null) {
                    scanJobScheduler = new ScanJobScheduler();
                    sInstance = scanJobScheduler;
                }
            }
        }
        return scanJobScheduler;
    }

    private ScanJobScheduler() {
    }

    private void ensureNotificationProcessorSetup(Context context) {
        if (this.mBeaconNotificationProcessor == null) {
            this.mBeaconNotificationProcessor = new BeaconLocalBroadcastProcessor(context);
            this.mBeaconNotificationProcessor.register();
        }
    }

    /* access modifiers changed from: 0000 */
    public List<ScanResult> dumpBackgroundScanResultQueue() {
        List<ScanResult> list = this.mBackgroundScanResultQueue;
        this.mBackgroundScanResultQueue = new ArrayList();
        return list;
    }

    private void applySettingsToScheduledJob(Context context, BeaconManager beaconManager, ScanState scanState) {
        scanState.applyChanges(beaconManager);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Applying scan job settings with background mode ");
        sb.append(scanState.getBackgroundMode());
        LogManager.m260d(str, sb.toString(), new Object[0]);
        schedule(context, scanState, false);
    }

    public void applySettingsToScheduledJob(Context context, BeaconManager beaconManager) {
        LogManager.m260d(TAG, "Applying settings to ScanJob", new Object[0]);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        applySettingsToScheduledJob(context, beaconManager, ScanState.restore(context));
    }

    public void scheduleAfterBackgroundWakeup(Context context, List<ScanResult> list) {
        if (list != null) {
            this.mBackgroundScanResultQueue.addAll(list);
        }
        synchronized (this) {
            if (System.currentTimeMillis() - this.mScanJobScheduleTime.longValue() > 10000) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("scheduling an immediate scan job because last did ");
                sb.append(System.currentTimeMillis() - this.mScanJobScheduleTime.longValue());
                sb.append("seconds ago.");
                LogManager.m260d(str, sb.toString(), new Object[0]);
                this.mScanJobScheduleTime = Long.valueOf(System.currentTimeMillis());
                schedule(context, ScanState.restore(context), true);
                return;
            }
            LogManager.m260d(TAG, "Not scheduling an immediate scan job because we just did recently.", new Object[0]);
        }
    }

    private void schedule(Context context, ScanState scanState, boolean z) {
        long j;
        ensureNotificationProcessorSetup(context);
        long scanJobIntervalMillis = (long) (scanState.getScanJobIntervalMillis() - scanState.getScanJobRuntimeMillis());
        if (z) {
            LogManager.m260d(TAG, "We just woke up in the background based on a new scan result.  Start scan job immediately.", new Object[0]);
            j = 0;
        } else {
            j = scanJobIntervalMillis > 0 ? SystemClock.elapsedRealtime() % ((long) scanState.getScanJobIntervalMillis()) : 0;
            if (j < 50) {
                j = 50;
            }
        }
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService("jobscheduler");
        if (!z && scanState.getBackgroundMode().booleanValue()) {
            LogManager.m260d(TAG, "Not scheduling an immediate scan because we are in background mode.   Cancelling existing immediate scan.", new Object[0]);
            jobScheduler.cancel(2);
        } else if (j < ((long) (scanState.getScanJobIntervalMillis() - 50))) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Scheduling immediate ScanJob to run in ");
            sb.append(j);
            sb.append(" millis");
            LogManager.m260d(str, sb.toString(), new Object[0]);
            int schedule = jobScheduler.schedule(new Builder(2, new ComponentName(context, ScanJob.class)).setPersisted(true).setExtras(new PersistableBundle()).setMinimumLatency(j).setOverrideDeadline(j).build());
            if (schedule < 0) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed to schedule scan job.  Beacons will not be detected. Error: ");
                sb2.append(schedule);
                LogManager.m262e(str2, sb2.toString(), new Object[0]);
            }
        }
        Builder extras = new Builder(1, new ComponentName(context, ScanJob.class)).setPersisted(true).setExtras(new PersistableBundle());
        if (VERSION.SDK_INT >= 24) {
            extras.setPeriodic((long) scanState.getScanJobIntervalMillis(), 0).build();
        } else {
            extras.setPeriodic((long) scanState.getScanJobIntervalMillis()).build();
        }
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Scheduling ScanJob to run every ");
        sb3.append(scanState.getScanJobIntervalMillis());
        sb3.append(" millis");
        LogManager.m260d(str3, sb3.toString(), new Object[0]);
        int schedule2 = jobScheduler.schedule(extras.build());
        if (schedule2 < 0) {
            String str4 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Failed to schedule scan job.  Beacons will not be detected. Error: ");
            sb4.append(schedule2);
            LogManager.m262e(str4, sb4.toString(), new Object[0]);
        }
    }
}
