package com.opengarden.firechat.offlineMessaging;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import com.opengarden.firechat.VectorApp;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.UUID;

public class Bluetooth {
    static final String TAG = "Bluetooth";
    public static HashMap<String, ConnectInfo> addressChannelMap;
    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = Bluetooth.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(intent);
            Log.d(str, sb.toString());
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 12)) {
                    case 12:
                        Bluetooth.createAndListen();
                        return;
                    case 13:
                        Bluetooth.channelSocketMap.clear();
                        Bluetooth.addressChannelMap.clear();
                        Bluetooth.stopAcceptThreads();
                        Bluetooth.stopConnectThreads();
                        return;
                    default:
                        return;
                }
            }
        }
    };
    public static HashMap<Integer, BluetoothSocket> channelSocketMap;
    public static BluetoothConnectionManager connectionManager;
    private static BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            if (!"android.bluetooth.device.action.FOUND".equals(action)) {
                if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                    String str = Bluetooth.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("device connected");
                    sb.append(bluetoothDevice.getAddress());
                    Log.d(str, sb.toString());
                } else if (!"android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action) && !"android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED".equals(action) && "android.bluetooth.device.action.ACL_DISCONNECTED".equals(action)) {
                    String address = bluetoothDevice.getAddress();
                    String str2 = Bluetooth.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("device disconnected ");
                    sb2.append(address);
                    Log.d(str2, sb2.toString());
                    LocalConnectionManager.connectionLost(address);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public static volatile AcceptThread sAcceptThreadChannel;
    /* access modifiers changed from: private */
    public static volatile ConnectThread sConnectThread;

    private static class AcceptThread extends Thread {
        public final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket bluetoothServerSocket;
            IOException e;
            try {
                Log.d(Bluetooth.TAG, "<><>trying to create accept socket ");
                bluetoothServerSocket = Bluetooth.getAdapter().listenUsingInsecureRfcommWithServiceRecord("Firechat", Bluetooth.getServiceUUID());
                try {
                    String str = Bluetooth.TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("created insecure with UUID ");
                    sb.append(Bluetooth.getServiceUUID());
                    Log.d(str, sb.toString());
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (IOException e3) {
                IOException iOException = e3;
                bluetoothServerSocket = null;
                e = iOException;
                e.printStackTrace();
                this.mmServerSocket = bluetoothServerSocket;
            }
            this.mmServerSocket = bluetoothServerSocket;
        }

        public void run() {
            BluetoothSocket bluetoothSocket = null;
            do {
                try {
                    Log.d(Bluetooth.TAG, "accept thread started");
                    if (this.mmServerSocket != null) {
                        bluetoothSocket = this.mmServerSocket.accept();
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } while (bluetoothSocket == null);
            Log.d(Bluetooth.TAG, "<><><> accept socket got connecion <><><>");
            String address = bluetoothSocket.getRemoteDevice().getAddress();
            ConnectInfo connectInfo = new ConnectInfo(address);
            connectInfo.iAccepted = true;
            Bluetooth.channelSocketMap.put(Integer.valueOf(Bluetooth.channelSocketMap.size() + 1), bluetoothSocket);
            Bluetooth.addressChannelMap.put(address, connectInfo);
            Bluetooth.passConnectedSocket(bluetoothSocket);
            try {
                this.mmServerSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            Bluetooth.createAndListen();
            Log.d(Bluetooth.TAG, "close");
        }

        public void cancel() {
            try {
                Log.d(Bluetooth.TAG, "cancel accept thread");
                if (this.mmServerSocket != null) {
                    this.mmServerSocket.close();
                }
                Bluetooth.sAcceptThreadChannel = null;
                Log.d(Bluetooth.TAG, "cancel accept thread 1");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class BluetoothConnectionManager {
        PriorityQueue<ConnectInfo> pending = new PriorityQueue<>();

        public void addToQueue(final ConnectInfo connectInfo, boolean z) {
            VectorApp.getInstance().runOnUIThread(new Runnable() {
                public void run() {
                    if (BluetoothConnectionManager.this.pending != null) {
                        if (BluetoothConnectionManager.this.pending.contains(connectInfo)) {
                            String str = Bluetooth.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("pending already contains info: ");
                            sb.append(connectInfo.mMac);
                            sb.append("  channel ");
                            sb.append(connectInfo.mChannel);
                            Log.d(str, sb.toString());
                        }
                        String str2 = Bluetooth.TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("adding to connection queue ");
                        sb2.append(connectInfo.mMac);
                        sb2.append("  channel ");
                        sb2.append(connectInfo.mChannel);
                        Log.d(str2, sb2.toString());
                        BluetoothConnectionManager.this.pending.add(connectInfo);
                        BluetoothConnectionManager.this.connectToNext();
                        return;
                    }
                    BluetoothConnectionManager.this.pending = new PriorityQueue<>();
                    BluetoothConnectionManager.this.pending.add(connectInfo);
                    String str3 = Bluetooth.TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("adding to connection queue ");
                    sb3.append(connectInfo.mMac);
                    sb3.append("  channel ");
                    sb3.append(connectInfo.mChannel);
                    Log.d(str3, sb3.toString());
                    if (BluetoothConnectionManager.this.pending.size() == 1 && Bluetooth.sConnectThread == null) {
                        BluetoothConnectionManager.this.connectToNext();
                    }
                }
            });
        }

        public void connectToNext() {
            VectorApp.getInstance().runOnUIThread(new Runnable() {
                public void run() {
                    if (BluetoothConnectionManager.this.pending.size() > 0 && Bluetooth.sConnectThread == null) {
                        ConnectInfo connectInfo = (ConnectInfo) BluetoothConnectionManager.this.pending.poll();
                        if (connectInfo != null) {
                            BluetoothConnectionManager.this.connect(connectInfo);
                        }
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void connect(ConnectInfo connectInfo) {
            if (!Bluetooth.checkAdapter()) {
                Log.d(Bluetooth.TAG, "bluetooth is off");
            } else if (connectInfo.mMac.equals(Bluetooth.getAddress())) {
                Bluetooth.connectionManager.connectToNext();
                Log.d(Bluetooth.TAG, "not connecting to self");
            } else if (!Bluetooth.checkBluetoothAddress(connectInfo.mMac)) {
                Bluetooth.connectionManager.connectToNext();
                Log.d(Bluetooth.TAG, "invalid mac address self");
            } else {
                BluetoothDevice remoteDevice = Bluetooth.getAdapter().getRemoteDevice(connectInfo.mMac);
                String str = Bluetooth.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("<><><> connect to: ");
                sb.append(remoteDevice.getName());
                sb.append(" (");
                sb.append(remoteDevice.getBondState());
                sb.append(") : ");
                sb.append(remoteDevice.getAddress());
                Log.d(str, sb.toString());
                if (Bluetooth.sConnectThread != null) {
                    Log.d(Bluetooth.TAG, "connect cancel already trying");
                    return;
                }
                Bluetooth.sConnectThread = new ConnectThread(remoteDevice);
                if (Bluetooth.sAcceptThreadChannel != null) {
                    Bluetooth.sAcceptThreadChannel.cancel();
                }
                Bluetooth.sConnectThread.start();
            }
        }
    }

    private static class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothSocket bluetoothSocket;
            this.mmDevice = bluetoothDevice;
            try {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Bluetooth.getServiceUUID());
            } catch (IOException e) {
                e.printStackTrace();
                bluetoothSocket = null;
            }
            this.mmSocket = bluetoothSocket;
        }

        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x00aa */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r5 = this;
                android.bluetooth.BluetoothAdapter r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.getAdapter()
                r0.cancelDiscovery()
                android.bluetooth.BluetoothDevice r0 = r5.mmDevice     // Catch:{ InterruptedException -> 0x002b }
                java.lang.String r0 = r0.getAddress()     // Catch:{ InterruptedException -> 0x002b }
                java.lang.String r1 = com.opengarden.firechat.offlineMessaging.Bluetooth.getAddress()     // Catch:{ InterruptedException -> 0x002b }
                int r0 = r0.compareTo(r1)     // Catch:{ InterruptedException -> 0x002b }
                if (r0 <= 0) goto L_0x002f
                java.util.Random r0 = new java.util.Random     // Catch:{ InterruptedException -> 0x002b }
                r0.<init>()     // Catch:{ InterruptedException -> 0x002b }
                r1 = 10
                int r0 = r0.nextInt(r1)     // Catch:{ InterruptedException -> 0x002b }
                int r0 = r0 * 100
                int r0 = r0 + 800
                long r0 = (long) r0     // Catch:{ InterruptedException -> 0x002b }
                java.lang.Thread.sleep(r0)     // Catch:{ InterruptedException -> 0x002b }
                goto L_0x002f
            L_0x002b:
                r0 = move-exception
                r0.printStackTrace()
            L_0x002f:
                java.lang.String r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.TAG
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "is socket null: "
                r1.append(r2)
                android.bluetooth.BluetoothSocket r2 = r5.mmSocket
                r3 = 0
                r4 = 1
                if (r2 != 0) goto L_0x0043
                r2 = 1
                goto L_0x0044
            L_0x0043:
                r2 = 0
            L_0x0044:
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                android.util.Log.d(r0, r1)
                android.bluetooth.BluetoothSocket r0 = r5.mmSocket
                if (r0 == 0) goto L_0x00af
                java.lang.String r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.TAG     // Catch:{ IOException -> 0x00aa }
                java.lang.String r1 = "about to connect"
                android.util.Log.d(r0, r1)     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothSocket r0 = r5.mmSocket     // Catch:{ IOException -> 0x00aa }
                r0.connect()     // Catch:{ IOException -> 0x00aa }
                java.lang.String r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.TAG     // Catch:{ IOException -> 0x00aa }
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00aa }
                r1.<init>()     // Catch:{ IOException -> 0x00aa }
                java.lang.String r2 = "<><><> connect thread successfully connected"
                r1.append(r2)     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothDevice r2 = r5.mmDevice     // Catch:{ IOException -> 0x00aa }
                java.lang.String r2 = r2.getAddress()     // Catch:{ IOException -> 0x00aa }
                r1.append(r2)     // Catch:{ IOException -> 0x00aa }
                java.lang.String r1 = r1.toString()     // Catch:{ IOException -> 0x00aa }
                android.util.Log.d(r0, r1)     // Catch:{ IOException -> 0x00aa }
                java.util.HashMap<java.lang.Integer, android.bluetooth.BluetoothSocket> r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.channelSocketMap     // Catch:{ IOException -> 0x00aa }
                java.util.HashMap<java.lang.Integer, android.bluetooth.BluetoothSocket> r1 = com.opengarden.firechat.offlineMessaging.Bluetooth.channelSocketMap     // Catch:{ IOException -> 0x00aa }
                int r1 = r1.size()     // Catch:{ IOException -> 0x00aa }
                int r1 = r1 + r4
                java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothSocket r2 = r5.mmSocket     // Catch:{ IOException -> 0x00aa }
                r0.put(r1, r2)     // Catch:{ IOException -> 0x00aa }
                com.opengarden.firechat.offlineMessaging.ConnectInfo r0 = new com.opengarden.firechat.offlineMessaging.ConnectInfo     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothDevice r1 = r5.mmDevice     // Catch:{ IOException -> 0x00aa }
                java.lang.String r1 = r1.getAddress()     // Catch:{ IOException -> 0x00aa }
                r0.<init>(r1)     // Catch:{ IOException -> 0x00aa }
                r0.iAccepted = r3     // Catch:{ IOException -> 0x00aa }
                java.util.HashMap<java.lang.String, com.opengarden.firechat.offlineMessaging.ConnectInfo> r1 = com.opengarden.firechat.offlineMessaging.Bluetooth.addressChannelMap     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothDevice r2 = r5.mmDevice     // Catch:{ IOException -> 0x00aa }
                java.lang.String r2 = r2.getAddress()     // Catch:{ IOException -> 0x00aa }
                r1.put(r2, r0)     // Catch:{ IOException -> 0x00aa }
                android.bluetooth.BluetoothSocket r0 = r5.mmSocket     // Catch:{ IOException -> 0x00aa }
                com.opengarden.firechat.offlineMessaging.Bluetooth.passConnectedSocket(r0)     // Catch:{ IOException -> 0x00aa }
                goto L_0x00af
            L_0x00aa:
                android.bluetooth.BluetoothSocket r0 = r5.mmSocket     // Catch:{ IOException -> 0x00af }
                r0.close()     // Catch:{ IOException -> 0x00af }
            L_0x00af:
                r0 = 0
                com.opengarden.firechat.offlineMessaging.Bluetooth.sConnectThread = r0
                com.opengarden.firechat.offlineMessaging.Bluetooth.createAndListen()
                com.opengarden.firechat.offlineMessaging.Bluetooth$BluetoothConnectionManager r0 = com.opengarden.firechat.offlineMessaging.Bluetooth.connectionManager
                r0.connectToNext()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.offlineMessaging.Bluetooth.ConnectThread.run():void");
        }

        public void cancel() {
            try {
                Log.d(Bluetooth.TAG, "connect thread cancel");
                this.mmSocket.close();
            } catch (IOException unused) {
            }
            Log.d(Bluetooth.TAG, "connect thread cancel 1");
            Bluetooth.sConnectThread = null;
            Bluetooth.createListeningSocket();
            Bluetooth.connectionManager.connectToNext();
        }
    }

    public static BluetoothAdapter getAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static boolean checkAdapter() {
        return getAddress() != null && getAdapter().isEnabled();
    }

    public static boolean checkBluetoothAddress(String str) {
        if (BluetoothAdapter.checkBluetoothAddress(str) && str.length() != 0 && !str.equals("02:00:00:00:00:00")) {
            return true;
        }
        return false;
    }

    public static String getAddress() {
        String str;
        if (getAdapter() == null) {
            return null;
        }
        if (VERSION.SDK_INT >= 23) {
            str = Secure.getString(VectorApp.getInstance().getContentResolver(), "bluetooth_address");
        } else {
            str = getAdapter().getAddress();
        }
        if (!checkBluetoothAddress(str)) {
            return null;
        }
        return str;
    }

    public static void connectionLost(String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("device disconnected ");
        sb.append(str);
        Log.d(str2, sb.toString());
        final ConnectInfo connectInfo = (ConnectInfo) addressChannelMap.get(str);
        if (connectInfo != null) {
            int i = -1;
            for (Entry entry : channelSocketMap.entrySet()) {
                if (((BluetoothSocket) entry.getValue()).getRemoteDevice().getAddress().equals(str)) {
                    i = ((Integer) entry.getKey()).intValue();
                }
            }
            channelSocketMap.remove(Integer.valueOf(i));
            addressChannelMap.remove(str);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Bluetooth.addToConnectQueue(connectInfo.mMac, true);
                    Log.d(Bluetooth.TAG, "added to connect queue");
                }
            }, 2000);
        }
    }

    public static void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        VectorApp.getInstance().registerReceiver(broadcastReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        intentFilter2.addAction("android.bluetooth.device.action.ACL_DISCONNECT_REQUESTED");
        intentFilter2.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        VectorApp.getInstance().registerReceiver(mReceiver, intentFilter2);
        createAndListen();
    }

    public static synchronized void createAndListen() {
        synchronized (Bluetooth.class) {
            VectorApp.getInstance().runOnUIThread(new Runnable() {
                public void run() {
                    if (Bluetooth.connectionManager == null) {
                        Bluetooth.connectionManager = new BluetoothConnectionManager();
                    }
                    Bluetooth.createListeningSocket();
                }
            });
        }
    }

    public static void addToConnectQueue(final String str, final boolean z) {
        if (BluetoothAdapter.checkBluetoothAddress(str)) {
            VectorApp.getInstance().runOnUIThread(new Runnable() {
                public void run() {
                    ConnectInfo connectInfo = new ConnectInfo(str);
                    if (Bluetooth.connectionManager != null && !LocalConnectionManager.isConnected(str)) {
                        Bluetooth.connectionManager.addToQueue(connectInfo, z);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public static void passConnectedSocket(BluetoothSocket bluetoothSocket) {
        LocalConnectionManager.gotConnection(bluetoothSocket, (String) null);
    }

    public static void stopAcceptThreads() {
        if (sAcceptThreadChannel != null) {
            sAcceptThreadChannel.cancel();
            sAcceptThreadChannel = null;
        }
    }

    public static void stopConnectThreads() {
        if (sConnectThread != null) {
            sConnectThread.cancel();
        }
    }

    public static boolean createListeningSocket() {
        if (getAdapter() != null) {
            stopAcceptThreads();
            sAcceptThreadChannel = new AcceptThread();
            if (sAcceptThreadChannel.mmServerSocket != null) {
                BeaconUtil.restartAndroidBeacon();
                sAcceptThreadChannel.start();
            }
            if (channelSocketMap == null) {
                channelSocketMap = new HashMap<>();
            }
            if (addressChannelMap == null) {
                addressChannelMap = new HashMap<>();
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static UUID getServiceUUID() {
        return UUID.fromString("8cf3819a-5dfd-11e8-9c2d-fa7ae01bbebc");
    }
}
