package org.altbeacon.beacon.service;

import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.altbeacon.beacon.logging.LogManager;

public class RunningAverageRssiFilter implements RssiFilter {
    public static final long DEFAULT_SAMPLE_EXPIRATION_MILLISECONDS = 20000;
    private static final String TAG = "RunningAverageRssiFilter";
    private static long sampleExpirationMilliseconds = 20000;
    private ArrayList<Measurement> mMeasurements = new ArrayList<>();

    private class Measurement implements Comparable<Measurement> {
        Integer rssi;
        long timestamp;

        private Measurement() {
        }

        public int compareTo(Measurement measurement) {
            return this.rssi.compareTo(measurement.rssi);
        }
    }

    public void addMeasurement(Integer num) {
        Measurement measurement = new Measurement();
        measurement.rssi = num;
        measurement.timestamp = SystemClock.elapsedRealtime();
        this.mMeasurements.add(measurement);
    }

    public boolean noMeasurementsAvailable() {
        return this.mMeasurements.size() == 0;
    }

    public double calculateRssi() {
        int i;
        refreshMeasurements();
        int size = this.mMeasurements.size();
        int i2 = size - 1;
        if (size > 2) {
            int i3 = size / 10;
            i = i3 + 1;
            i2 = (size - i3) - 2;
        } else {
            i = 0;
        }
        double d = 0.0d;
        for (int i4 = i; i4 <= i2; i4++) {
            d += (double) ((Measurement) this.mMeasurements.get(i4)).rssi.intValue();
        }
        double d2 = d / ((double) ((i2 - i) + 1));
        LogManager.m260d(TAG, "Running average mRssi based on %s measurements: %s", Integer.valueOf(size), Double.valueOf(d2));
        return d2;
    }

    private synchronized void refreshMeasurements() {
        ArrayList<Measurement> arrayList = new ArrayList<>();
        Iterator it = this.mMeasurements.iterator();
        while (it.hasNext()) {
            Measurement measurement = (Measurement) it.next();
            if (SystemClock.elapsedRealtime() - measurement.timestamp < sampleExpirationMilliseconds) {
                arrayList.add(measurement);
            }
        }
        this.mMeasurements = arrayList;
        Collections.sort(this.mMeasurements);
    }

    public static void setSampleExpirationMilliseconds(long j) {
        sampleExpirationMilliseconds = j;
    }

    @RestrictTo({Scope.TESTS})
    static long getSampleExpirationMilliseconds() {
        return sampleExpirationMilliseconds;
    }
}
