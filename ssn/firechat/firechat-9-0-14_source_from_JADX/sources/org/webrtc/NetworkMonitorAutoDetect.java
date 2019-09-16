package org.webrtc;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest.Builder;
import android.net.wifi.WifiInfo;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.List;

public class NetworkMonitorAutoDetect extends BroadcastReceiver {
    static final long INVALID_NET_ID = -1;
    private static final String TAG = "NetworkMonitorAutoDetect";
    private final NetworkCallback allNetworkCallback;
    private ConnectionType connectionType;
    /* access modifiers changed from: private */
    public ConnectivityManagerDelegate connectivityManagerDelegate;
    private final Context context;
    private final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private boolean isRegistered;
    private final NetworkCallback mobileNetworkCallback;
    /* access modifiers changed from: private */
    public final Observer observer;
    private WifiManagerDelegate wifiManagerDelegate;
    private String wifiSSID;

    public enum ConnectionType {
        CONNECTION_UNKNOWN,
        CONNECTION_ETHERNET,
        CONNECTION_WIFI,
        CONNECTION_4G,
        CONNECTION_3G,
        CONNECTION_2G,
        CONNECTION_UNKNOWN_CELLULAR,
        CONNECTION_BLUETOOTH,
        CONNECTION_NONE
    }

    static class ConnectivityManagerDelegate {
        private final ConnectivityManager connectivityManager;

        ConnectivityManagerDelegate(Context context) {
            this.connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        }

        ConnectivityManagerDelegate() {
            this.connectivityManager = null;
        }

        /* access modifiers changed from: 0000 */
        public NetworkState getNetworkState() {
            if (this.connectivityManager == null) {
                return new NetworkState(false, -1, -1);
            }
            return getNetworkState(this.connectivityManager.getActiveNetworkInfo());
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint({"NewApi"})
        public NetworkState getNetworkState(Network network) {
            if (this.connectivityManager == null) {
                return new NetworkState(false, -1, -1);
            }
            return getNetworkState(this.connectivityManager.getNetworkInfo(network));
        }

        /* access modifiers changed from: 0000 */
        public NetworkState getNetworkState(NetworkInfo networkInfo) {
            if (networkInfo == null || !networkInfo.isConnected()) {
                return new NetworkState(false, -1, -1);
            }
            return new NetworkState(true, networkInfo.getType(), networkInfo.getSubtype());
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint({"NewApi"})
        public Network[] getAllNetworks() {
            if (this.connectivityManager == null) {
                return new Network[0];
            }
            return this.connectivityManager.getAllNetworks();
        }

        /* access modifiers changed from: 0000 */
        public List<NetworkInformation> getActiveNetworkList() {
            if (!supportNetworkCallback()) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (Network networkToInfo : getAllNetworks()) {
                NetworkInformation networkToInfo2 = networkToInfo(networkToInfo);
                if (networkToInfo2 != null) {
                    arrayList.add(networkToInfo2);
                }
            }
            return arrayList;
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint({"NewApi"})
        public long getDefaultNetId() {
            Network[] allNetworks;
            if (!supportNetworkCallback()) {
                return -1;
            }
            NetworkInfo activeNetworkInfo = this.connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return -1;
            }
            long j = -1;
            for (Network network : getAllNetworks()) {
                if (hasInternetCapability(network)) {
                    NetworkInfo networkInfo = this.connectivityManager.getNetworkInfo(network);
                    if (networkInfo != null && networkInfo.getType() == activeNetworkInfo.getType()) {
                        if (j != -1) {
                            throw new RuntimeException("Multiple connected networks of same type are not supported.");
                        }
                        j = NetworkMonitorAutoDetect.networkToNetId(network);
                    }
                }
            }
            return j;
        }

        /* access modifiers changed from: private */
        @SuppressLint({"NewApi"})
        public NetworkInformation networkToInfo(Network network) {
            LinkProperties linkProperties = this.connectivityManager.getLinkProperties(network);
            if (linkProperties == null) {
                String str = NetworkMonitorAutoDetect.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Detected unknown network: ");
                sb.append(network.toString());
                Logging.m318w(str, sb.toString());
                return null;
            } else if (linkProperties.getInterfaceName() == null) {
                String str2 = NetworkMonitorAutoDetect.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Null interface name for network ");
                sb2.append(network.toString());
                Logging.m318w(str2, sb2.toString());
                return null;
            } else {
                NetworkState networkState = getNetworkState(network);
                if (networkState.connected && networkState.getNetworkType() == 17) {
                    networkState = getNetworkState();
                }
                ConnectionType connectionType = NetworkMonitorAutoDetect.getConnectionType(networkState);
                if (connectionType == ConnectionType.CONNECTION_NONE) {
                    String str3 = NetworkMonitorAutoDetect.TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Network ");
                    sb3.append(network.toString());
                    sb3.append(" is disconnected");
                    Logging.m314d(str3, sb3.toString());
                    return null;
                }
                if (connectionType == ConnectionType.CONNECTION_UNKNOWN || connectionType == ConnectionType.CONNECTION_UNKNOWN_CELLULAR) {
                    String str4 = NetworkMonitorAutoDetect.TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Network ");
                    sb4.append(network.toString());
                    sb4.append(" connection type is ");
                    sb4.append(connectionType);
                    sb4.append(" because it has type ");
                    sb4.append(networkState.getNetworkType());
                    sb4.append(" and subtype ");
                    sb4.append(networkState.getNetworkSubType());
                    Logging.m314d(str4, sb4.toString());
                }
                NetworkInformation networkInformation = new NetworkInformation(linkProperties.getInterfaceName(), connectionType, NetworkMonitorAutoDetect.networkToNetId(network), getIPAddresses(linkProperties));
                return networkInformation;
            }
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint({"NewApi"})
        public boolean hasInternetCapability(Network network) {
            boolean z = false;
            if (this.connectivityManager == null) {
                return false;
            }
            NetworkCapabilities networkCapabilities = this.connectivityManager.getNetworkCapabilities(network);
            if (networkCapabilities != null && networkCapabilities.hasCapability(12)) {
                z = true;
            }
            return z;
        }

        @SuppressLint({"NewApi"})
        public void registerNetworkCallback(NetworkCallback networkCallback) {
            this.connectivityManager.registerNetworkCallback(new Builder().addCapability(12).build(), networkCallback);
        }

        @SuppressLint({"NewApi"})
        public void requestMobileNetwork(NetworkCallback networkCallback) {
            Builder builder = new Builder();
            builder.addCapability(12).addTransportType(0);
            this.connectivityManager.requestNetwork(builder.build(), networkCallback);
        }

        /* access modifiers changed from: 0000 */
        @SuppressLint({"NewApi"})
        public IPAddress[] getIPAddresses(LinkProperties linkProperties) {
            IPAddress[] iPAddressArr = new IPAddress[linkProperties.getLinkAddresses().size()];
            int i = 0;
            for (LinkAddress address : linkProperties.getLinkAddresses()) {
                iPAddressArr[i] = new IPAddress(address.getAddress().getAddress());
                i++;
            }
            return iPAddressArr;
        }

        @SuppressLint({"NewApi"})
        public void releaseCallback(NetworkCallback networkCallback) {
            if (supportNetworkCallback()) {
                Logging.m314d(NetworkMonitorAutoDetect.TAG, "Unregister network callback");
                this.connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }

        public boolean supportNetworkCallback() {
            return VERSION.SDK_INT >= 21 && this.connectivityManager != null;
        }
    }

    public static class IPAddress {
        public final byte[] address;

        public IPAddress(byte[] bArr) {
            this.address = bArr;
        }
    }

    public static class NetworkInformation {
        public final long handle;
        public final IPAddress[] ipAddresses;
        public final String name;
        public final ConnectionType type;

        public NetworkInformation(String str, ConnectionType connectionType, long j, IPAddress[] iPAddressArr) {
            this.name = str;
            this.type = connectionType;
            this.handle = j;
            this.ipAddresses = iPAddressArr;
        }
    }

    static class NetworkState {
        /* access modifiers changed from: private */
        public final boolean connected;
        private final int subtype;
        private final int type;

        public NetworkState(boolean z, int i, int i2) {
            this.connected = z;
            this.type = i;
            this.subtype = i2;
        }

        public boolean isConnected() {
            return this.connected;
        }

        public int getNetworkType() {
            return this.type;
        }

        public int getNetworkSubType() {
            return this.subtype;
        }
    }

    public interface Observer {
        void onConnectionTypeChanged(ConnectionType connectionType);

        void onNetworkConnect(NetworkInformation networkInformation);

        void onNetworkDisconnect(long j);
    }

    @SuppressLint({"NewApi"})
    private class SimpleNetworkCallback extends NetworkCallback {
        private SimpleNetworkCallback() {
        }

        public void onAvailable(Network network) {
            String str = NetworkMonitorAutoDetect.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Network becomes available: ");
            sb.append(network.toString());
            Logging.m314d(str, sb.toString());
            onNetworkChanged(network);
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            String str = NetworkMonitorAutoDetect.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("capabilities changed: ");
            sb.append(networkCapabilities.toString());
            Logging.m314d(str, sb.toString());
            onNetworkChanged(network);
        }

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            String str = NetworkMonitorAutoDetect.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("link properties changed: ");
            sb.append(linkProperties.toString());
            Logging.m314d(str, sb.toString());
            onNetworkChanged(network);
        }

        public void onLosing(Network network, int i) {
            String str = NetworkMonitorAutoDetect.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Network ");
            sb.append(network.toString());
            sb.append(" is about to lose in ");
            sb.append(i);
            sb.append("ms");
            Logging.m314d(str, sb.toString());
        }

        public void onLost(Network network) {
            String str = NetworkMonitorAutoDetect.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Network ");
            sb.append(network.toString());
            sb.append(" is disconnected");
            Logging.m314d(str, sb.toString());
            NetworkMonitorAutoDetect.this.observer.onNetworkDisconnect(NetworkMonitorAutoDetect.networkToNetId(network));
        }

        private void onNetworkChanged(Network network) {
            NetworkInformation access$300 = NetworkMonitorAutoDetect.this.connectivityManagerDelegate.networkToInfo(network);
            if (access$300 != null) {
                NetworkMonitorAutoDetect.this.observer.onNetworkConnect(access$300);
            }
        }
    }

    static class WifiManagerDelegate {
        private final Context context;

        WifiManagerDelegate(Context context2) {
            this.context = context2;
        }

        WifiManagerDelegate() {
            this.context = null;
        }

        /* access modifiers changed from: 0000 */
        public String getWifiSSID() {
            Intent registerReceiver = this.context.registerReceiver(null, new IntentFilter("android.net.wifi.STATE_CHANGE"));
            if (registerReceiver != null) {
                WifiInfo wifiInfo = (WifiInfo) registerReceiver.getParcelableExtra("wifiInfo");
                if (wifiInfo != null) {
                    String ssid = wifiInfo.getSSID();
                    if (ssid != null) {
                        return ssid;
                    }
                }
            }
            return "";
        }
    }

    @SuppressLint({"NewApi"})
    public NetworkMonitorAutoDetect(Observer observer2, Context context2) {
        this.observer = observer2;
        this.context = context2;
        this.connectivityManagerDelegate = new ConnectivityManagerDelegate(context2);
        this.wifiManagerDelegate = new WifiManagerDelegate(context2);
        NetworkState networkState = this.connectivityManagerDelegate.getNetworkState();
        this.connectionType = getConnectionType(networkState);
        this.wifiSSID = getWifiSSID(networkState);
        registerReceiver();
        if (this.connectivityManagerDelegate.supportNetworkCallback()) {
            NetworkCallback networkCallback = new NetworkCallback();
            try {
                this.connectivityManagerDelegate.requestMobileNetwork(networkCallback);
            } catch (SecurityException unused) {
                Logging.m318w(TAG, "Unable to obtain permission to request a cellular network.");
                networkCallback = null;
            }
            this.mobileNetworkCallback = networkCallback;
            this.allNetworkCallback = new SimpleNetworkCallback();
            this.connectivityManagerDelegate.registerNetworkCallback(this.allNetworkCallback);
            return;
        }
        this.mobileNetworkCallback = null;
        this.allNetworkCallback = null;
    }

    public boolean supportNetworkCallback() {
        return this.connectivityManagerDelegate.supportNetworkCallback();
    }

    /* access modifiers changed from: 0000 */
    public void setConnectivityManagerDelegateForTests(ConnectivityManagerDelegate connectivityManagerDelegate2) {
        this.connectivityManagerDelegate = connectivityManagerDelegate2;
    }

    /* access modifiers changed from: 0000 */
    public void setWifiManagerDelegateForTests(WifiManagerDelegate wifiManagerDelegate2) {
        this.wifiManagerDelegate = wifiManagerDelegate2;
    }

    /* access modifiers changed from: 0000 */
    public boolean isReceiverRegisteredForTesting() {
        return this.isRegistered;
    }

    /* access modifiers changed from: 0000 */
    public List<NetworkInformation> getActiveNetworkList() {
        return this.connectivityManagerDelegate.getActiveNetworkList();
    }

    public void destroy() {
        if (this.allNetworkCallback != null) {
            this.connectivityManagerDelegate.releaseCallback(this.allNetworkCallback);
        }
        if (this.mobileNetworkCallback != null) {
            this.connectivityManagerDelegate.releaseCallback(this.mobileNetworkCallback);
        }
        unregisterReceiver();
    }

    private void registerReceiver() {
        if (!this.isRegistered) {
            this.isRegistered = true;
            this.context.registerReceiver(this, this.intentFilter);
        }
    }

    private void unregisterReceiver() {
        if (this.isRegistered) {
            this.isRegistered = false;
            this.context.unregisterReceiver(this);
        }
    }

    public NetworkState getCurrentNetworkState() {
        return this.connectivityManagerDelegate.getNetworkState();
    }

    public long getDefaultNetId() {
        return this.connectivityManagerDelegate.getDefaultNetId();
    }

    public static ConnectionType getConnectionType(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return ConnectionType.CONNECTION_NONE;
        }
        switch (networkState.getNetworkType()) {
            case 0:
                switch (networkState.getNetworkSubType()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return ConnectionType.CONNECTION_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return ConnectionType.CONNECTION_3G;
                    case 13:
                        return ConnectionType.CONNECTION_4G;
                    default:
                        return ConnectionType.CONNECTION_UNKNOWN_CELLULAR;
                }
            case 1:
                return ConnectionType.CONNECTION_WIFI;
            case 6:
                return ConnectionType.CONNECTION_4G;
            case 7:
                return ConnectionType.CONNECTION_BLUETOOTH;
            case 9:
                return ConnectionType.CONNECTION_ETHERNET;
            default:
                return ConnectionType.CONNECTION_UNKNOWN;
        }
    }

    private String getWifiSSID(NetworkState networkState) {
        if (getConnectionType(networkState) != ConnectionType.CONNECTION_WIFI) {
            return "";
        }
        return this.wifiManagerDelegate.getWifiSSID();
    }

    public void onReceive(Context context2, Intent intent) {
        NetworkState currentNetworkState = getCurrentNetworkState();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            connectionTypeChanged(currentNetworkState);
        }
    }

    private void connectionTypeChanged(NetworkState networkState) {
        ConnectionType connectionType2 = getConnectionType(networkState);
        String wifiSSID2 = getWifiSSID(networkState);
        if (connectionType2 != this.connectionType || !wifiSSID2.equals(this.wifiSSID)) {
            this.connectionType = connectionType2;
            this.wifiSSID = wifiSSID2;
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Network connectivity changed, type is: ");
            sb.append(this.connectionType);
            Logging.m314d(str, sb.toString());
            this.observer.onConnectionTypeChanged(connectionType2);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public static long networkToNetId(Network network) {
        if (VERSION.SDK_INT >= 23) {
            return network.getNetworkHandle();
        }
        return (long) Integer.parseInt(network.toString());
    }
}
