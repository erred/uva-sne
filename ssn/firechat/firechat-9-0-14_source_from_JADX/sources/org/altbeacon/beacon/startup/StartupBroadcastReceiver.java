package org.altbeacon.beacon.startup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.ScanJobScheduler;

public class StartupBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupBroadcastReceiver";

    public void onReceive(Context context, Intent intent) {
        LogManager.m260d(TAG, "onReceive called in startup broadcast receiver", new Object[0]);
        if (VERSION.SDK_INT < 18) {
            LogManager.m268w(TAG, "Not starting up beacon service because we do not have API version 18 (Android 4.3).  We have: %s", Integer.valueOf(VERSION.SDK_INT));
            return;
        }
        BeaconManager instanceForApplication = BeaconManager.getInstanceForApplication(context.getApplicationContext());
        if (instanceForApplication.isAnyConsumerBound() || instanceForApplication.getScheduledScanJobsEnabled()) {
            int intExtra = intent.getIntExtra("android.bluetooth.le.extra.CALLBACK_TYPE", -1);
            if (intExtra != -1) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Passive background scan callback type: ");
                sb.append(intExtra);
                LogManager.m260d(str, sb.toString(), new Object[0]);
                LogManager.m260d(TAG, "got Android O background scan via intent", new Object[0]);
                int intExtra2 = intent.getIntExtra("android.bluetooth.le.extra.ERROR_CODE", -1);
                if (intExtra2 != -1) {
                    String str2 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Passive background scan failed.  Code; ");
                    sb2.append(intExtra2);
                    LogManager.m268w(str2, sb2.toString(), new Object[0]);
                }
                ScanJobScheduler.getInstance().scheduleAfterBackgroundWakeup(context, intent.getParcelableArrayListExtra("android.bluetooth.le.extra.LIST_SCAN_RESULT"));
            } else if (intent.getBooleanExtra("wakeup", false)) {
                LogManager.m260d(TAG, "got wake up intent", new Object[0]);
            } else {
                LogManager.m260d(TAG, "Already started.  Ignoring intent: %s of type: %s", intent, intent.getStringExtra("wakeup"));
            }
        } else {
            LogManager.m260d(TAG, "No consumers are bound.  Ignoring broadcast receiver.", new Object[0]);
        }
    }
}
