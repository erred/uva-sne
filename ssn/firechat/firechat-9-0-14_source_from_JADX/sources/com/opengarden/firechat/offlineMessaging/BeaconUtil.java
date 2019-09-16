package com.opengarden.firechat.offlineMessaging;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.opengarden.firechat.VectorApp;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Beacon.Builder;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.cli.HelpFormatter;

public class BeaconUtil {
    private static BeaconTransmitter AndroidBeaconEmitter = null;
    private static final String AndroidLayout = "m:2-3=99ab,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final long BEACON_SCAN_INTERVAL = 10000;
    /* access modifiers changed from: private */
    public static final String TAG = "BeaconUtil";
    public static final int TEST_ID = 0;
    static MeshkitBeaconConsumer beaconConsumer = null;
    /* access modifiers changed from: private */
    public static BeaconManager beaconManager = null;
    private static BeaconTransmitter endpointBeaconEmitter = null;
    private static boolean isReceiverInit = false;

    static class MeshkitBeaconConsumer implements BeaconConsumer {
        MeshkitBeaconConsumer() {
        }

        public void onBeaconServiceConnect() {
            BeaconUtil.beaconManager.setRangeNotifier(new RangeNotifier() {
                public void didRangeBeaconsInRegion(final Collection<Beacon> collection, Region region) {
                    new Handler(VectorApp.getInstance().getMainLooper()).post(new Runnable() {
                        public void run() {
                            String access$100 = BeaconUtil.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("didRangeBeaconsInRegion.run() called; got ");
                            sb.append(collection.size());
                            sb.append(" beacons.");
                            Log.d(access$100, sb.toString());
                            BeaconUtil.startAndroidSpecificBeacon(VectorApp.getInstance().getApplicationContext());
                            for (Beacon beacon : collection) {
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("");
                                sb2.append((char) Integer.parseInt(beacon.getId2().toString(), 16));
                                if (!sb2.toString().equals("X")) {
                                    String access$300 = BeaconUtil.UUIDtoString(beacon.getId1().toString());
                                    long longValue = ((Long) beacon.getDataFields().get(0)).longValue();
                                    String identifier = beacon.getId3().toString();
                                    String identifier2 = beacon.getId2().toString();
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("");
                                    sb3.append((char) Integer.parseInt(identifier2, 16));
                                    String sb4 = sb3.toString();
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append(access$300);
                                    sb5.append(sb4);
                                    String sb6 = sb5.toString();
                                    String access$1002 = BeaconUtil.TAG;
                                    StringBuilder sb7 = new StringBuilder();
                                    sb7.append("found beacon with mac ");
                                    sb7.append(sb6);
                                    sb7.append(" and channel ");
                                    sb7.append(longValue);
                                    Log.d(access$1002, sb7.toString());
                                    StringBuilder sb8 = new StringBuilder();
                                    sb8.append(identifier);
                                    sb8.append("");
                                    if (identifier.equals(sb8.toString())) {
                                        Bluetooth.addToConnectQueue(sb6, false);
                                    }
                                }
                            }
                        }
                    });
                }
            });
            try {
                BeaconUtil.beaconManager.startRangingBeaconsInRegion(new Region("Firechat", null, null, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public Context getApplicationContext() {
            return VectorApp.getInstance();
        }

        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            return VectorApp.getInstance().bindService(intent, serviceConnection, i);
        }

        public void unbindService(ServiceConnection serviceConnection) {
            VectorApp.getInstance().unbindService(serviceConnection);
        }
    }

    /* access modifiers changed from: private */
    public static void startAndroidSpecificBeacon(Context context) {
        if (BeaconTransmitter.checkTransmissionSupported(context) == 0) {
            try {
                long longValue = Long.valueOf(-1).longValue();
                String address = Bluetooth.getAddress();
                if (address != null) {
                    Beacon build = new Builder().setId1(StringToUUID(address)).setId2(toHex(address.substring(address.length() - 1))).setId3("0").setTxPower(-59).setDataFields(Arrays.asList(new Long[]{Long.valueOf(longValue)})).build();
                    build.getId2().toString();
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("started advertising bt with mac address of: ");
                    sb.append(address);
                    Log.d(str, sb.toString());
                    AndroidBeaconEmitter = new BeaconTransmitter(context, new BeaconParser().setBeaconLayout(AndroidLayout));
                    if (AndroidBeaconEmitter != null) {
                        AndroidBeaconEmitter.startAdvertising(build);
                    }
                } else {
                    Log.d("wtf", "address is null");
                }
            } catch (NoSuchMethodError e) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error related to beacon 2: NoSuchMethodError ");
                sb2.append(e.getMessage());
                Log.e(str2, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public static void bindBeaconObserver() {
        if (!beaconManager.isBound(beaconConsumer)) {
            Log.d(TAG, "Binding Beacon Receiver");
            beaconManager.bind(beaconConsumer);
            startBeaconReceiver();
        }
    }

    private static String StringToUUID(String str) {
        String str2 = "4621192d-315f-478a-943f-b2ca635213df";
        String str3 = "";
        char[] charArray = str.toCharArray();
        int i = 0;
        while (str3.length() < str2.length()) {
            int length = str3.length();
            if (length == 8 || length == 13 || length == 18 || length == 23) {
                StringBuilder sb = new StringBuilder();
                sb.append(str3);
                sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                str3 = sb.toString();
            } else if (i < charArray.length) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str3);
                StringBuilder sb3 = new StringBuilder();
                sb3.append("");
                sb3.append(charArray[i]);
                sb2.append(toHex(sb3.toString()));
                str3 = sb2.toString();
                i++;
            } else {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str3);
                sb4.append("00");
                str3 = sb4.toString();
            }
        }
        return str3;
    }

    /* access modifiers changed from: private */
    public static String UUIDtoString(String str) {
        String trim = str.replaceAll(HelpFormatter.DEFAULT_OPT_PREFIX, "").trim();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < trim.length()) {
            int i2 = i + 2;
            sb.append((char) Integer.parseInt(trim.substring(i, i2), 16));
            i = i2;
        }
        System.out.println(sb);
        return sb.toString();
    }

    static String toHex(String str) {
        return String.format("%02x", new Object[]{new BigInteger(1, str.getBytes())});
    }

    static void restartAndroidBeacon() {
        stopAndroidSpecificBeacon();
        startAndroidSpecificBeacon(VectorApp.getInstance());
        startBeaconReceiver();
    }

    public static void startBeaconReceiver() {
        if (beaconConsumer == null) {
            beaconConsumer = new MeshkitBeaconConsumer();
        }
        if (!isReceiverInit) {
            Log.d(TAG, "And Beacon Receiver Init");
            beaconManager = BeaconManager.getInstanceForApplication(VectorApp.getInstance());
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(AndroidLayout));
            beaconManager.setForegroundScanPeriod(10000);
            beaconManager.setBackgroundScanPeriod(10000);
            beaconManager.setForegroundBetweenScanPeriod(10000);
            beaconManager.setBackgroundBetweenScanPeriod(10000);
            isReceiverInit = true;
        }
        VectorApp.getInstance().runOnUIThreadDelayed(new Runnable() {
            public void run() {
                if (Bluetooth.checkAdapter()) {
                    BeaconUtil.bindBeaconObserver();
                }
            }
        }, 15000);
    }

    public static void stopAndroidSpecificBeacon() {
        if (AndroidBeaconEmitter != null) {
            AndroidBeaconEmitter.stopAdvertising();
            AndroidBeaconEmitter = null;
        }
    }
}
