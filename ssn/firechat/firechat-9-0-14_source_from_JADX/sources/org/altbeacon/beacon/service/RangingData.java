package org.altbeacon.beacon.service;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collection;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Region;

public class RangingData {
    private static final String BEACONS_KEY = "beacons";
    private static final String REGION_KEY = "region";
    private static final String TAG = "RangingData";
    private final Collection<Beacon> mBeacons;
    private final Region mRegion;

    public RangingData(Collection<Beacon> collection, Region region) {
        synchronized (collection) {
            this.mBeacons = collection;
        }
        this.mRegion = region;
    }

    public Collection<Beacon> getBeacons() {
        return this.mBeacons;
    }

    public Region getRegion() {
        return this.mRegion;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(REGION_KEY, this.mRegion);
        ArrayList arrayList = new ArrayList();
        for (Beacon add : this.mBeacons) {
            arrayList.add(add);
        }
        bundle.putSerializable(BEACONS_KEY, arrayList);
        return bundle;
    }

    public static RangingData fromBundle(Bundle bundle) {
        bundle.setClassLoader(Region.class.getClassLoader());
        Region region = null;
        Collection collection = bundle.get(BEACONS_KEY) != null ? (Collection) bundle.getSerializable(BEACONS_KEY) : null;
        if (bundle.get(REGION_KEY) != null) {
            region = (Region) bundle.getSerializable(REGION_KEY);
        }
        return new RangingData(collection, region);
    }
}
