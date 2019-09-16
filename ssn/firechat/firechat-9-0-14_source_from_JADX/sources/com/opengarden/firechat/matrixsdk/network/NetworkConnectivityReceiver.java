package com.opengarden.firechat.matrixsdk.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "NetworkConnectivityReceiver";
    private boolean mIsConnected = false;
    private boolean mIsUseWifiConnection = false;
    private final List<IMXNetworkEventListener> mNetworkEventListeners = new ArrayList();
    private int mNetworkSubType = 0;
    private final List<IMXNetworkEventListener> mOnNetworkConnectedEventListeners = new ArrayList();

    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = null;
        if (intent != null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onReceive() : action ");
            sb.append(intent.getAction());
            Log.m209d(str, sb.toString());
            Bundle extras = intent.getExtras();
            if (extras != null) {
                for (String str2 : extras.keySet()) {
                    String str3 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onReceive() : ");
                    sb2.append(str2);
                    sb2.append(" -> ");
                    sb2.append(extras.get(str2));
                    Log.m209d(str3, sb2.toString());
                }
                if (extras.containsKey("networkInfo")) {
                    Object obj = extras.get("networkInfo");
                    if (obj instanceof NetworkInfo) {
                        networkInfo = (NetworkInfo) obj;
                    }
                }
            }
        } else {
            Log.m209d(LOG_TAG, "## onReceive()");
        }
        checkNetworkConnection(context, networkInfo);
    }

    public void checkNetworkConnection(Context context) {
        checkNetworkConnection(context, null);
    }

    private void checkNetworkConnection(Context context, NetworkInfo networkInfo) {
        synchronized (LOG_TAG) {
            if (networkInfo == null) {
                try {
                    networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Failed to report :");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                } catch (Throwable th) {
                    throw th;
                }
            }
            int i = 0;
            boolean z = true;
            boolean z2 = networkInfo != null && networkInfo.isConnectedOrConnecting();
            if (z2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## checkNetworkConnection() : Connected to ");
                sb2.append(networkInfo);
                Log.m209d(str2, sb2.toString());
            } else if (networkInfo != null) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## checkNetworkConnection() : there is a default connection but it is not connected ");
                sb3.append(networkInfo);
                Log.m209d(str3, sb3.toString());
                listNetworkConnections(context);
            } else {
                Log.m209d(LOG_TAG, "## checkNetworkConnection() : there is no connection");
                listNetworkConnections(context);
            }
            if (networkInfo == null || networkInfo.getType() != 1) {
                z = false;
            }
            this.mIsUseWifiConnection = z;
            if (networkInfo != null) {
                i = networkInfo.getSubtype();
            }
            this.mNetworkSubType = i;
            if (this.mIsConnected != z2) {
                Log.m209d(LOG_TAG, "## checkNetworkConnection() : Warn there is a connection update");
                this.mIsConnected = z2;
                onNetworkUpdate();
            } else {
                Log.m209d(LOG_TAG, "## checkNetworkConnection() : No network update");
            }
        }
    }

    @SuppressLint({"deprecation"})
    private static void listNetworkConnections(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        ArrayList<NetworkInfo> arrayList = new ArrayList<>();
        if (VERSION.SDK_INT >= 21) {
            Network[] allNetworks = connectivityManager.getAllNetworks();
            if (allNetworks != null) {
                for (Network networkInfo : allNetworks) {
                    NetworkInfo networkInfo2 = connectivityManager.getNetworkInfo(networkInfo);
                    if (networkInfo2 != null) {
                        arrayList.add(networkInfo2);
                    }
                }
            }
        } else {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            if (allNetworkInfo != null) {
                arrayList.addAll(Arrays.asList(allNetworkInfo));
            }
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## listNetworkConnections() : ");
        sb.append(arrayList.size());
        sb.append(" connections");
        Log.m209d(str, sb.toString());
        for (NetworkInfo networkInfo3 : arrayList) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("-> ");
            sb2.append(networkInfo3);
            Log.m209d(str2, sb2.toString());
        }
    }

    public void addEventListener(IMXNetworkEventListener iMXNetworkEventListener) {
        if (iMXNetworkEventListener != null) {
            this.mNetworkEventListeners.add(iMXNetworkEventListener);
        }
    }

    public void addOnConnectedEventListener(IMXNetworkEventListener iMXNetworkEventListener) {
        if (iMXNetworkEventListener != null) {
            synchronized (LOG_TAG) {
                this.mOnNetworkConnectedEventListeners.add(iMXNetworkEventListener);
            }
        }
    }

    public void removeEventListener(IMXNetworkEventListener iMXNetworkEventListener) {
        synchronized (LOG_TAG) {
            this.mNetworkEventListeners.remove(iMXNetworkEventListener);
            this.mOnNetworkConnectedEventListeners.remove(iMXNetworkEventListener);
        }
    }

    public void removeListeners() {
        synchronized (LOG_TAG) {
            this.mNetworkEventListeners.clear();
            this.mOnNetworkConnectedEventListeners.clear();
        }
    }

    private synchronized void onNetworkUpdate() {
        for (IMXNetworkEventListener onNetworkConnectionUpdate : this.mNetworkEventListeners) {
            try {
                onNetworkConnectionUpdate.onNetworkConnectionUpdate(this.mIsConnected);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onNetworkUpdate() : onNetworkConnectionUpdate failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        if (this.mIsConnected) {
            for (IMXNetworkEventListener onNetworkConnectionUpdate2 : this.mOnNetworkConnectedEventListeners) {
                try {
                    onNetworkConnectionUpdate2.onNetworkConnectionUpdate(true);
                } catch (Exception e2) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onNetworkUpdate() : onNetworkConnectionUpdate failed ");
                    sb2.append(e2.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            this.mOnNetworkConnectedEventListeners.clear();
        }
    }

    public boolean isConnected() {
        boolean z;
        synchronized (LOG_TAG) {
            z = this.mIsConnected;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## isConnected() : ");
        sb.append(z);
        Log.m209d(str, sb.toString());
        return z;
    }

    public boolean useWifiConnection() {
        boolean z;
        synchronized (LOG_TAG) {
            z = this.mIsUseWifiConnection;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## useWifiConnection() : ");
        sb.append(z);
        Log.m209d(str, sb.toString());
        return z;
    }

    public float getTimeoutScale() {
        float f;
        synchronized (LOG_TAG) {
            int i = this.mNetworkSubType;
            if (i != 13) {
                if (i != 15) {
                    switch (i) {
                        case 1:
                            f = 3.0f;
                            break;
                        case 2:
                            f = 2.5f;
                            break;
                        case 3:
                        case 4:
                            break;
                        default:
                            switch (i) {
                                case 8:
                                case 9:
                                case 10:
                                    break;
                                default:
                                    f = 1.0f;
                                    break;
                            }
                    }
                }
                f = 2.0f;
            } else {
                f = 1.5f;
            }
        }
        return f;
    }
}
