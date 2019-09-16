package org.altbeacon.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.p000v4.content.LocalBroadcastManager;
import org.altbeacon.beacon.logging.LogManager;

public class BeaconLocalBroadcastProcessor {
    public static final String MONITOR_NOTIFICATION = "org.altbeacon.beacon.monitor_notification";
    public static final String RANGE_NOTIFICATION = "org.altbeacon.beacon.range_notification";
    private static final String TAG = "BeaconLocalBroadcastProcessor";
    static int registerCallCount;
    @NonNull
    private Context mContext;
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            new IntentHandler().convertIntentsToCallbacks(context, intent);
        }
    };
    int registerCallCountForInstnace = 0;

    private BeaconLocalBroadcastProcessor() {
    }

    public BeaconLocalBroadcastProcessor(Context context) {
        this.mContext = context;
    }

    public void register() {
        registerCallCount++;
        this.registerCallCountForInstnace++;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Register calls: global=");
        sb.append(registerCallCount);
        sb.append(" instance=");
        sb.append(this.registerCallCountForInstnace);
        LogManager.m260d(str, sb.toString(), new Object[0]);
        unregister();
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mLocalBroadcastReceiver, new IntentFilter(RANGE_NOTIFICATION));
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mLocalBroadcastReceiver, new IntentFilter(MONITOR_NOTIFICATION));
    }

    public void unregister() {
        LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.mLocalBroadcastReceiver);
    }
}
