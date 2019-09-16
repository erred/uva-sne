package org.altbeacon.beacon.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build.VERSION;
import android.os.Handler;
import java.util.List;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BuildConfig;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.utils.ProcessUtils;

@TargetApi(21)
public class ScanJob extends JobService {
    public static final int IMMMEDIATE_SCAN_JOB_ID = 2;
    public static final int PERIODIC_SCAN_JOB_ID = 1;
    /* access modifiers changed from: private */
    public static final String TAG = "ScanJob";
    private boolean mInitialized = false;
    private ScanHelper mScanHelper;
    /* access modifiers changed from: private */
    public ScanState mScanState;
    private Handler mStopHandler = new Handler();

    public boolean onStartJob(final JobParameters jobParameters) {
        boolean z;
        this.mScanHelper = new ScanHelper(this);
        if (jobParameters.getJobId() == 2) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Running immdiate scan job: instance is ");
            sb.append(this);
            LogManager.m264i(str, sb.toString(), new Object[0]);
        } else {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Running periodic scan job: instance is ");
            sb2.append(this);
            LogManager.m264i(str2, sb2.toString(), new Object[0]);
        }
        List<ScanResult> dumpBackgroundScanResultQueue = ScanJobScheduler.getInstance().dumpBackgroundScanResultQueue();
        LogManager.m260d(TAG, "Processing %d queued scan resuilts", Integer.valueOf(dumpBackgroundScanResultQueue.size()));
        for (ScanResult scanResult : dumpBackgroundScanResultQueue) {
            ScanRecord scanRecord = scanResult.getScanRecord();
            if (scanRecord != null) {
                this.mScanHelper.processScanResult(scanResult.getDevice(), scanResult.getRssi(), scanRecord.getBytes());
            }
        }
        LogManager.m260d(TAG, "Done processing queued scan resuilts", new Object[0]);
        if (this.mInitialized) {
            LogManager.m260d(TAG, "Scanning already started.  Resetting for current parameters", new Object[0]);
            z = restartScanning();
        } else {
            z = startScanning();
        }
        this.mStopHandler.removeCallbacksAndMessages(null);
        if (z) {
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Scan job running for ");
            sb3.append(this.mScanState.getScanJobRuntimeMillis());
            sb3.append(" millis");
            LogManager.m264i(str3, sb3.toString(), new Object[0]);
            this.mStopHandler.postDelayed(new Runnable() {
                public void run() {
                    LogManager.m264i(ScanJob.TAG, "Scan job runtime expired", new Object[0]);
                    ScanJob.this.stopScanning();
                    ScanJob.this.mScanState.save();
                    ScanJob.this.startPassiveScanIfNeeded();
                    ScanJob.this.jobFinished(jobParameters, false);
                }
            }, (long) this.mScanState.getScanJobRuntimeMillis());
        } else {
            LogManager.m264i(TAG, "No monitored or ranged regions. Scan job complete.", new Object[0]);
            jobFinished(jobParameters, false);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void startPassiveScanIfNeeded() {
        LogManager.m260d(TAG, "Checking to see if we need to start a passive scan", new Object[0]);
        boolean z = false;
        for (Region stateOf : this.mScanState.getMonitoringStatus().regions()) {
            RegionMonitoringState stateOf2 = this.mScanState.getMonitoringStatus().stateOf(stateOf);
            if (stateOf2 != null && stateOf2.getInside()) {
                z = true;
            }
        }
        if (z) {
            LogManager.m264i(TAG, "We are inside a beacon region.  We will not scan between cycles.", new Object[0]);
        } else if (VERSION.SDK_INT >= 26) {
            this.mScanHelper.startAndroidOBackgroundScan(this.mScanState.getBeaconParsers());
        } else {
            LogManager.m260d(TAG, "This is not Android O.  No scanning between cycles when using ScanJob", new Object[0]);
        }
    }

    public boolean onStopJob(JobParameters jobParameters) {
        if (jobParameters.getJobId() == 1) {
            LogManager.m264i(TAG, "onStopJob called for periodic scan", new Object[0]);
        } else {
            LogManager.m264i(TAG, "onStopJob called for immediate scan", new Object[0]);
        }
        this.mStopHandler.removeCallbacksAndMessages(null);
        stopScanning();
        startPassiveScanIfNeeded();
        return false;
    }

    /* access modifiers changed from: private */
    public void stopScanning() {
        this.mInitialized = false;
        this.mScanHelper.getCycledScanner().stop();
        this.mScanHelper.getCycledScanner().destroy();
        LogManager.m260d(TAG, "Scanning stopped", new Object[0]);
    }

    private boolean restartScanning() {
        this.mScanState = ScanState.restore(this);
        this.mScanState.setLastScanStartTimeMillis(System.currentTimeMillis());
        this.mScanHelper.setMonitoringStatus(this.mScanState.getMonitoringStatus());
        this.mScanHelper.setRangedRegionState(this.mScanState.getRangedRegionState());
        this.mScanHelper.setBeaconParsers(this.mScanState.getBeaconParsers());
        this.mScanHelper.setExtraDataBeaconTracker(this.mScanState.getExtraBeaconDataTracker());
        if (this.mScanHelper.getCycledScanner() == null) {
            this.mScanHelper.createCycledLeScanner(this.mScanState.getBackgroundMode().booleanValue(), null);
        }
        if (VERSION.SDK_INT >= 26) {
            this.mScanHelper.stopAndroidOBackgroundScan();
        }
        this.mScanHelper.getCycledScanner().setScanPeriods((this.mScanState.getBackgroundMode().booleanValue() ? this.mScanState.getBackgroundScanPeriod() : this.mScanState.getForegroundScanPeriod()).longValue(), (this.mScanState.getBackgroundMode().booleanValue() ? this.mScanState.getBackgroundBetweenScanPeriod() : this.mScanState.getForegroundBetweenScanPeriod()).longValue(), this.mScanState.getBackgroundMode().booleanValue());
        this.mInitialized = true;
        if (this.mScanHelper.getRangedRegionState().size() > 0 || this.mScanHelper.getMonitoringStatus().regions().size() > 0) {
            this.mScanHelper.getCycledScanner().start();
            return true;
        }
        this.mScanHelper.getCycledScanner().stop();
        return false;
    }

    private boolean startScanning() {
        BeaconManager instanceForApplication = BeaconManager.getInstanceForApplication(getApplicationContext());
        instanceForApplication.setScannerInSameProcess(true);
        if (instanceForApplication.isMainProcess()) {
            LogManager.m264i(TAG, "scanJob version %s is starting up on the main process", BuildConfig.VERSION_NAME);
        } else {
            LogManager.m264i(TAG, "beaconScanJob library version %s is starting up on a separate process", BuildConfig.VERSION_NAME);
            ProcessUtils processUtils = new ProcessUtils(this);
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("beaconScanJob PID is ");
            sb.append(processUtils.getPid());
            sb.append(" with process name ");
            sb.append(processUtils.getProcessName());
            LogManager.m264i(str, sb.toString(), new Object[0]);
        }
        Beacon.setDistanceCalculator(new ModelSpecificDistanceCalculator(this, BeaconManager.getDistanceModelUpdateUrl()));
        return restartScanning();
    }
}
