package org.altbeacon.beacon.startup;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.logging.LogManager;

public class RegionBootstrap {
    protected static final String TAG = "AppStarter";
    /* access modifiers changed from: private */
    public BootstrapNotifier application;
    private BeaconConsumer beaconConsumer;
    /* access modifiers changed from: private */
    public BeaconManager beaconManager;
    private boolean disabled = false;
    /* access modifiers changed from: private */
    public List<Region> regions;
    /* access modifiers changed from: private */
    public boolean serviceConnected = false;

    private class InternalBeaconConsumer implements BeaconConsumer {
        private Intent serviceIntent;

        private InternalBeaconConsumer() {
        }

        public void onBeaconServiceConnect() {
            LogManager.m260d(RegionBootstrap.TAG, "Activating background region monitoring", new Object[0]);
            RegionBootstrap.this.beaconManager.addMonitorNotifier(RegionBootstrap.this.application);
            RegionBootstrap.this.serviceConnected = true;
            try {
                for (Region region : RegionBootstrap.this.regions) {
                    LogManager.m260d(RegionBootstrap.TAG, "Background region monitoring activated for region %s", region);
                    RegionBootstrap.this.beaconManager.startMonitoringBeaconsInRegion(region);
                    if (RegionBootstrap.this.beaconManager.isBackgroundModeUninitialized()) {
                        RegionBootstrap.this.beaconManager.setBackgroundMode(true);
                    }
                }
            } catch (RemoteException e) {
                LogManager.m263e(e, RegionBootstrap.TAG, "Can't set up bootstrap regions", new Object[0]);
            }
        }

        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            this.serviceIntent = intent;
            RegionBootstrap.this.application.getApplicationContext().startService(intent);
            return RegionBootstrap.this.application.getApplicationContext().bindService(intent, serviceConnection, i);
        }

        public Context getApplicationContext() {
            return RegionBootstrap.this.application.getApplicationContext();
        }

        public void unbindService(ServiceConnection serviceConnection) {
            RegionBootstrap.this.application.getApplicationContext().unbindService(serviceConnection);
            RegionBootstrap.this.application.getApplicationContext().stopService(this.serviceIntent);
            RegionBootstrap.this.serviceConnected = false;
        }
    }

    public RegionBootstrap(BootstrapNotifier bootstrapNotifier, Region region) {
        if (bootstrapNotifier.getApplicationContext() == null) {
            throw new NullPointerException("The BootstrapNotifier instance is returning null from its getApplicationContext() method.  Have you implemented this method?");
        }
        this.beaconManager = BeaconManager.getInstanceForApplication(bootstrapNotifier.getApplicationContext());
        this.application = bootstrapNotifier;
        this.regions = new ArrayList();
        this.regions.add(region);
        this.beaconConsumer = new InternalBeaconConsumer();
        this.beaconManager.bind(this.beaconConsumer);
        LogManager.m260d(TAG, "Waiting for BeaconService connection", new Object[0]);
    }

    public RegionBootstrap(BootstrapNotifier bootstrapNotifier, List<Region> list) {
        if (bootstrapNotifier.getApplicationContext() == null) {
            throw new NullPointerException("The BootstrapNotifier instance is returning null from its getApplicationContext() method.  Have you implemented this method?");
        }
        this.beaconManager = BeaconManager.getInstanceForApplication(bootstrapNotifier.getApplicationContext());
        this.application = bootstrapNotifier;
        this.regions = list;
        this.beaconConsumer = new InternalBeaconConsumer();
        this.beaconManager.bind(this.beaconConsumer);
        LogManager.m260d(TAG, "Waiting for BeaconService connection", new Object[0]);
    }

    public void disable() {
        if (!this.disabled) {
            this.disabled = true;
            try {
                for (Region stopMonitoringBeaconsInRegion : this.regions) {
                    this.beaconManager.stopMonitoringBeaconsInRegion(stopMonitoringBeaconsInRegion);
                }
            } catch (RemoteException e) {
                LogManager.m263e(e, TAG, "Can't stop bootstrap regions", new Object[0]);
            }
            this.beaconManager.unbind(this.beaconConsumer);
        }
    }

    public void addRegion(Region region) {
        if (!this.regions.contains(region)) {
            if (this.serviceConnected) {
                try {
                    this.beaconManager.startMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    LogManager.m263e(e, TAG, "Can't add bootstrap region", new Object[0]);
                }
            } else {
                LogManager.m268w(TAG, "Adding a region: service not yet Connected", new Object[0]);
            }
            this.regions.add(region);
        }
    }

    public void removeRegion(Region region) {
        if (this.regions.contains(region)) {
            if (this.serviceConnected) {
                try {
                    this.beaconManager.stopMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    LogManager.m263e(e, TAG, "Can't stop bootstrap region", new Object[0]);
                }
            } else {
                LogManager.m268w(TAG, "Removing a region: service not yet Connected", new Object[0]);
            }
            this.regions.remove(region);
        }
    }
}
