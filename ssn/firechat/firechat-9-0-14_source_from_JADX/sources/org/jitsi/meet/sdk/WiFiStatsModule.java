package org.jitsi.meet.sdk;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.measurement.AppMeasurement.Param;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.json.JSONArray;
import org.json.JSONObject;

class WiFiStatsModule extends ReactContextBaseJavaModule {
    private static final String MODULE_NAME = "WiFiStats";
    public static final int SIGNAL_LEVEL_SCALE = 101;
    static final String TAG = "WiFiStats";
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public String getName() {
        return "WiFiStats";
    }

    public WiFiStatsModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    public static InetAddress toInetAddress(int i) throws UnknownHostException {
        return InetAddress.getByAddress(new byte[]{(byte) i, (byte) (i >> 8), (byte) (i >> 16), (byte) (i >> 24)});
    }

    @ReactMethod
    public void getWiFiStats(final Promise promise) {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                try {
                    WifiManager wifiManager = (WifiManager) WiFiStatsModule.this.getReactApplicationContext().getApplicationContext().getSystemService("wifi");
                    if (!wifiManager.isWifiEnabled()) {
                        promise.reject((Throwable) new Exception("Wifi not enabled"));
                        return;
                    }
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo.getNetworkId() == -1) {
                        promise.reject((Throwable) new Exception("Wifi not connected"));
                        return;
                    }
                    int rssi = connectionInfo.getRssi();
                    int calculateSignalLevel = WifiManager.calculateSignalLevel(rssi, 101);
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("rssi", rssi).put("signal", calculateSignalLevel).put(Param.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
                    JSONArray jSONArray = new JSONArray();
                    InetAddress inetAddress = WiFiStatsModule.toInetAddress(connectionInfo.getIpAddress());
                    Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                        boolean z = false;
                        Enumeration inetAddresses = networkInterface.getInetAddresses();
                        while (true) {
                            if (inetAddresses.hasMoreElements()) {
                                if (((InetAddress) inetAddresses.nextElement()).equals(inetAddress)) {
                                    z = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (z) {
                            Enumeration inetAddresses2 = networkInterface.getInetAddresses();
                            while (inetAddresses2.hasMoreElements()) {
                                InetAddress inetAddress2 = (InetAddress) inetAddresses2.nextElement();
                                if (!inetAddress2.isLinkLocalAddress()) {
                                    jSONArray.put(inetAddress2.getHostAddress());
                                }
                            }
                        }
                    }
                    jSONObject.put("addresses", jSONArray);
                    promise.resolve(jSONObject.toString());
                    StringBuilder sb = new StringBuilder();
                    sb.append("WiFi stats: ");
                    sb.append(jSONObject.toString());
                    Log.d("WiFiStats", sb.toString());
                } catch (SocketException unused) {
                    Log.wtf("WiFiStats", "Unable to NetworkInterface.getNetworkInterfaces()");
                } catch (Throwable th) {
                    Log.e("WiFiStats", "Failed to obtain wifi stats", th);
                    promise.reject((Throwable) new Exception("Failed to obtain wifi stats"));
                }
            }
        });
    }
}
