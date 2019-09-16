package org.altbeacon.beacon;

import android.content.Context;
import android.content.Intent;
import java.util.Collection;
import java.util.Set;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.beacon.service.MonitoringData;
import org.altbeacon.beacon.service.MonitoringStatus;
import org.altbeacon.beacon.service.RangingData;

class IntentHandler {
    private static final String TAG = "IntentHandler";

    IntentHandler() {
    }

    public void convertIntentsToCallbacks(Context context, Intent intent) {
        MonitoringData monitoringData;
        RangingData rangingData = null;
        if (intent == null || intent.getExtras() == null) {
            monitoringData = null;
        } else {
            monitoringData = intent.getExtras().getBundle("monitoringData") != null ? MonitoringData.fromBundle(intent.getExtras().getBundle("monitoringData")) : null;
            if (intent.getExtras().getBundle("rangingData") != null) {
                rangingData = RangingData.fromBundle(intent.getExtras().getBundle("rangingData"));
            }
        }
        if (rangingData != null) {
            LogManager.m260d(TAG, "got ranging data", new Object[0]);
            if (rangingData.getBeacons() == null) {
                LogManager.m268w(TAG, "Ranging data has a null beacons collection", new Object[0]);
            }
            Set<RangeNotifier> rangingNotifiers = BeaconManager.getInstanceForApplication(context).getRangingNotifiers();
            Collection beacons = rangingData.getBeacons();
            if (rangingNotifiers != null) {
                for (RangeNotifier didRangeBeaconsInRegion : rangingNotifiers) {
                    didRangeBeaconsInRegion.didRangeBeaconsInRegion(beacons, rangingData.getRegion());
                }
            } else {
                LogManager.m260d(TAG, "but ranging notifier is null, so we're dropping it.", new Object[0]);
            }
            RangeNotifier dataRequestNotifier = BeaconManager.getInstanceForApplication(context).getDataRequestNotifier();
            if (dataRequestNotifier != null) {
                dataRequestNotifier.didRangeBeaconsInRegion(beacons, rangingData.getRegion());
            }
        }
        if (monitoringData != null) {
            LogManager.m260d(TAG, "got monitoring data", new Object[0]);
            Set<MonitorNotifier> monitoringNotifiers = BeaconManager.getInstanceForApplication(context).getMonitoringNotifiers();
            if (monitoringNotifiers != null) {
                for (MonitorNotifier monitorNotifier : monitoringNotifiers) {
                    LogManager.m260d(TAG, "Calling monitoring notifier: %s", monitorNotifier);
                    Region region = monitoringData.getRegion();
                    Integer valueOf = Integer.valueOf(monitoringData.isInside() ? 1 : 0);
                    monitorNotifier.didDetermineStateForRegion(valueOf.intValue(), region);
                    MonitoringStatus.getInstanceForApplication(context).updateLocalState(region, valueOf);
                    if (monitoringData.isInside()) {
                        monitorNotifier.didEnterRegion(monitoringData.getRegion());
                    } else {
                        monitorNotifier.didExitRegion(monitoringData.getRegion());
                    }
                }
            }
        }
    }
}
