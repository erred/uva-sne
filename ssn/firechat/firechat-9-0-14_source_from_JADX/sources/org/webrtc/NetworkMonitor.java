package org.webrtc;

import android.content.Context;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.webrtc.NetworkMonitorAutoDetect.ConnectionType;
import org.webrtc.NetworkMonitorAutoDetect.NetworkInformation;
import org.webrtc.NetworkMonitorAutoDetect.Observer;

public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static NetworkMonitor instance;
    private final Context applicationContext;
    private NetworkMonitorAutoDetect autoDetector;
    private ConnectionType currentConnectionType = ConnectionType.CONNECTION_UNKNOWN;
    private final ArrayList<Long> nativeNetworkObservers;
    private final ArrayList<NetworkObserver> networkObservers;

    public interface NetworkObserver {
        void onConnectionTypeChanged(ConnectionType connectionType);
    }

    private native void nativeNotifyConnectionTypeChanged(long j);

    private native void nativeNotifyOfActiveNetworkList(long j, NetworkInformation[] networkInformationArr);

    private native void nativeNotifyOfNetworkConnect(long j, NetworkInformation networkInformation);

    private native void nativeNotifyOfNetworkDisconnect(long j, long j2);

    private NetworkMonitor(Context context) {
        assertIsTrue(context != null);
        if (context.getApplicationContext() != null) {
            context = context.getApplicationContext();
        }
        this.applicationContext = context;
        this.nativeNetworkObservers = new ArrayList<>();
        this.networkObservers = new ArrayList<>();
    }

    public static NetworkMonitor init(Context context) {
        if (!isInitialized()) {
            instance = new NetworkMonitor(context);
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static NetworkMonitor getInstance() {
        return instance;
    }

    public static void setAutoDetectConnectivityState(boolean z) {
        getInstance().setAutoDetectConnectivityStateInternal(z);
    }

    private static void assertIsTrue(boolean z) {
        if (!z) {
            throw new AssertionError("Expected to be true");
        }
    }

    private void startMonitoring(long j) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Start monitoring from native observer ");
        sb.append(j);
        Logging.m314d(str, sb.toString());
        this.nativeNetworkObservers.add(Long.valueOf(j));
        setAutoDetectConnectivityStateInternal(true);
    }

    private void stopMonitoring(long j) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Stop monitoring from native observer ");
        sb.append(j);
        Logging.m314d(str, sb.toString());
        setAutoDetectConnectivityStateInternal(false);
        this.nativeNetworkObservers.remove(Long.valueOf(j));
    }

    private boolean networkBindingSupported() {
        return this.autoDetector != null && this.autoDetector.supportNetworkCallback();
    }

    private static int androidSdkInt() {
        return VERSION.SDK_INT;
    }

    private ConnectionType getCurrentConnectionType() {
        return this.currentConnectionType;
    }

    private long getCurrentDefaultNetId() {
        if (this.autoDetector == null) {
            return -1;
        }
        return this.autoDetector.getDefaultNetId();
    }

    private void destroyAutoDetector() {
        if (this.autoDetector != null) {
            this.autoDetector.destroy();
            this.autoDetector = null;
        }
    }

    private void setAutoDetectConnectivityStateInternal(boolean z) {
        if (!z) {
            destroyAutoDetector();
            return;
        }
        if (this.autoDetector == null) {
            this.autoDetector = new NetworkMonitorAutoDetect(new Observer() {
                public void onConnectionTypeChanged(ConnectionType connectionType) {
                    NetworkMonitor.this.updateCurrentConnectionType(connectionType);
                }

                public void onNetworkConnect(NetworkInformation networkInformation) {
                    NetworkMonitor.this.notifyObserversOfNetworkConnect(networkInformation);
                }

                public void onNetworkDisconnect(long j) {
                    NetworkMonitor.this.notifyObserversOfNetworkDisconnect(j);
                }
            }, this.applicationContext);
            updateCurrentConnectionType(NetworkMonitorAutoDetect.getConnectionType(this.autoDetector.getCurrentNetworkState()));
            updateActiveNetworkList();
        }
    }

    /* access modifiers changed from: private */
    public void updateCurrentConnectionType(ConnectionType connectionType) {
        this.currentConnectionType = connectionType;
        notifyObserversOfConnectionTypeChange(connectionType);
    }

    private void notifyObserversOfConnectionTypeChange(ConnectionType connectionType) {
        Iterator it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            nativeNotifyConnectionTypeChanged(((Long) it.next()).longValue());
        }
        Iterator it2 = this.networkObservers.iterator();
        while (it2.hasNext()) {
            ((NetworkObserver) it2.next()).onConnectionTypeChanged(connectionType);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkConnect(NetworkInformation networkInformation) {
        Iterator it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            nativeNotifyOfNetworkConnect(((Long) it.next()).longValue(), networkInformation);
        }
    }

    /* access modifiers changed from: private */
    public void notifyObserversOfNetworkDisconnect(long j) {
        Iterator it = this.nativeNetworkObservers.iterator();
        while (it.hasNext()) {
            nativeNotifyOfNetworkDisconnect(((Long) it.next()).longValue(), j);
        }
    }

    private void updateActiveNetworkList() {
        List activeNetworkList = this.autoDetector.getActiveNetworkList();
        if (activeNetworkList != null && activeNetworkList.size() != 0) {
            NetworkInformation[] networkInformationArr = (NetworkInformation[]) activeNetworkList.toArray(new NetworkInformation[activeNetworkList.size()]);
            Iterator it = this.nativeNetworkObservers.iterator();
            while (it.hasNext()) {
                nativeNotifyOfActiveNetworkList(((Long) it.next()).longValue(), networkInformationArr);
            }
        }
    }

    public static void addNetworkObserver(NetworkObserver networkObserver) {
        getInstance().addNetworkObserverInternal(networkObserver);
    }

    private void addNetworkObserverInternal(NetworkObserver networkObserver) {
        this.networkObservers.add(networkObserver);
    }

    public static void removeNetworkObserver(NetworkObserver networkObserver) {
        getInstance().removeNetworkObserverInternal(networkObserver);
    }

    private void removeNetworkObserverInternal(NetworkObserver networkObserver) {
        this.networkObservers.remove(networkObserver);
    }

    public static boolean isOnline() {
        return getInstance().getCurrentConnectionType() != ConnectionType.CONNECTION_NONE;
    }

    static void resetInstanceForTests(Context context) {
        instance = new NetworkMonitor(context);
    }

    public static NetworkMonitorAutoDetect getAutoDetectorForTest() {
        return getInstance().autoDetector;
    }
}
