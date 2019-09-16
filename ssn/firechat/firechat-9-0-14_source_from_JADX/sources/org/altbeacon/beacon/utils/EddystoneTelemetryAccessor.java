package org.altbeacon.beacon.utils;

import android.annotation.TargetApi;
import android.util.Base64;
import android.util.Log;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Beacon.Builder;
import org.altbeacon.beacon.BeaconParser;
import org.apache.commons.lang3.StringUtils;

public class EddystoneTelemetryAccessor {
    private static final String TAG = "EddystoneTLMAccessor";

    public byte[] getTelemetryBytes(Beacon beacon) {
        if (beacon.getExtraDataFields().size() < 5) {
            return null;
        }
        byte[] beaconAdvertisementData = new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT).getBeaconAdvertisementData(new Builder().setDataFields(beacon.getExtraDataFields()).build());
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Rehydrated telemetry bytes are :");
        sb.append(byteArrayToString(beaconAdvertisementData));
        Log.d(str, sb.toString());
        return beaconAdvertisementData;
    }

    @TargetApi(8)
    public String getBase64EncodedTelemetry(Beacon beacon) {
        byte[] telemetryBytes = getTelemetryBytes(beacon);
        if (telemetryBytes == null) {
            return null;
        }
        String encodeToString = Base64.encodeToString(telemetryBytes, 0);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Base64 telemetry bytes are :");
        sb.append(encodeToString);
        Log.d(str, sb.toString());
        return encodeToString;
    }

    private String byteArrayToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte valueOf : bArr) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(valueOf)}));
            sb.append(StringUtils.SPACE);
        }
        return sb.toString().trim();
    }
}
