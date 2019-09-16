package org.altbeacon.beacon.service;

import org.altbeacon.beacon.logging.LogManager;

public class ArmaRssiFilter implements RssiFilter {
    private static double DEFAULT_ARMA_SPEED = 0.1d;
    private static final String TAG = "ArmaRssiFilter";
    private int armaMeasurement;
    private double armaSpeed;
    private boolean isInitialized;

    public boolean noMeasurementsAvailable() {
        return false;
    }

    public ArmaRssiFilter() {
        this.armaSpeed = 0.1d;
        this.isInitialized = false;
        this.armaSpeed = DEFAULT_ARMA_SPEED;
    }

    public void addMeasurement(Integer num) {
        LogManager.m260d(TAG, "adding rssi: %s", num);
        if (!this.isInitialized) {
            this.armaMeasurement = num.intValue();
            this.isInitialized = true;
        }
        this.armaMeasurement = Double.valueOf(((double) this.armaMeasurement) - (this.armaSpeed * ((double) (this.armaMeasurement - num.intValue())))).intValue();
        LogManager.m260d(TAG, "armaMeasurement: %s", Integer.valueOf(this.armaMeasurement));
    }

    public double calculateRssi() {
        return (double) this.armaMeasurement;
    }

    public static void setDEFAULT_ARMA_SPEED(double d) {
        DEFAULT_ARMA_SPEED = d;
    }
}
