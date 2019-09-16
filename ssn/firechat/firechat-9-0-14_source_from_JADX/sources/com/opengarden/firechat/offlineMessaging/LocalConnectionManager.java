package com.opengarden.firechat.offlineMessaging;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothSocket;
import android.support.p000v4.app.NotificationCompat;
import android.util.Log;
import com.google.gson.JsonObject;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.SerializationUtils;

public class LocalConnectionManager {
    private static final String TAG = "LocalConnectionManager";
    public static Map<String, ThreadedSocket> connections = new ConcurrentHashMap();

    static boolean isConnected(String str) {
        return connections.containsKey(str);
    }

    static void gotConnection(BluetoothSocket bluetoothSocket, String str) {
        gotConnection(new ThreadedSocket(bluetoothSocket), str);
    }

    static void removeDuplicateConnections(ThreadedSocket threadedSocket) {
        String localAddress = threadedSocket.getLocalAddress();
        String remoteAddress = threadedSocket.getRemoteAddress();
        if (remoteAddress != null) {
            for (ThreadedSocket threadedSocket2 : connections.values()) {
                String remoteAddress2 = threadedSocket2.getRemoteAddress();
                if (threadedSocket != threadedSocket2 && remoteAddress2 != null && remoteAddress.equals(remoteAddress2) && remoteAddress.compareTo(localAddress) > 0) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("removed duplicate connection to ");
                    sb.append(remoteAddress2);
                    Log.i(str, sb.toString());
                    threadedSocket2.close();
                }
            }
        }
    }

    static void connectionLost(final String str) {
        connections.remove(str);
        VectorApp.getInstance().runOnUIThread(new Runnable() {
            public void run() {
                Bluetooth.connectionLost(str);
            }
        });
    }

    @TargetApi(21)
    static void gotConnection(final ThreadedSocket threadedSocket, String str) {
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("<><><><><><>gotConnection:");
        sb.append(threadedSocket.getRemoteAddress());
        Log.d(str2, sb.toString());
        removeDuplicateConnections(threadedSocket);
        connections.put(threadedSocket.getRemoteAddress(), threadedSocket);
        new Thread() {
            public void run() {
                threadedSocket.run();
                try {
                    LocalConnectionManager.connections.remove(threadedSocket.getRemoteAddress());
                } catch (NullPointerException unused) {
                }
            }
        }.start();
    }

    public static void sendToPeers(String str, Event event, String str2, int i) {
        byte[] serialize = SerializationUtils.serialize(new OfflineMessage(event.eventId, event.roomId, str2, event));
        for (ThreadedSocket threadedSocket : connections.values()) {
            String str3 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("<><><>sendToPeers:");
            sb.append(threadedSocket.getRemoteAddress());
            Log.i(str3, sb.toString());
            String remoteAddress = threadedSocket.getRemoteAddress();
            if (threadedSocket.btSocket == null) {
                String str4 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("<><><>btsocket is null");
                sb2.append(threadedSocket.getLocalAddress());
                sb2.append(threadedSocket.getRemoteAddress());
                Log.d(str4, sb2.toString());
            }
            String str5 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("<><><>btsocket is local address ");
            sb3.append(threadedSocket.getLocalAddress());
            sb3.append(" and remote address is: ");
            sb3.append(threadedSocket.getRemoteAddress());
            Log.d(str5, sb3.toString());
            if (remoteAddress != null) {
                threadedSocket.send(str, serialize);
            }
        }
        byte[] bArr = null;
        if (i == 0) {
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject2 = new JsonObject();
            jsonObject.add("content", event.getContentAsJsonObject());
            jsonObject.addProperty("type", Event.EVENT_TYPE_MESSAGE_ENCRYPTED);
            jsonObject.addProperty(BingRule.KIND_SENDER, event.getSender());
            jsonObject2.add("payload", jsonObject);
            jsonObject2.addProperty("type", "toDevice");
            jsonObject2.addProperty("event_id", event.eventId);
            jsonObject2.addProperty("room_id", event.roomId);
            jsonObject2.addProperty("access_token", str2);
            try {
                bArr = jsonObject2.toString().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bArr != null && VectorApp.getInstance().mBtle != null) {
                VectorApp.getInstance().mBtle.sendMessage(bArr);
                return;
            }
            return;
        }
        JsonObject jsonObject3 = event.toJsonObject();
        JsonObject jsonObject4 = new JsonObject();
        jsonObject4.addProperty("type", "message");
        jsonObject4.add(NotificationCompat.CATEGORY_EVENT, jsonObject3);
        jsonObject4.addProperty("access_token", str2);
        try {
            bArr = jsonObject4.toString().getBytes("UTF-8");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (bArr != null && VectorApp.getInstance().mBtle != null) {
            VectorApp.getInstance().mBtle.sendMessage(bArr);
        }
    }

    public static void sendToPeers(JsonObject jsonObject) {
        byte[] bArr;
        try {
            bArr = jsonObject.toString().getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        for (ThreadedSocket threadedSocket : connections.values()) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("<><><>sendToPeers:");
            sb.append(threadedSocket.getRemoteAddress());
            Log.i(str, sb.toString());
            String remoteAddress = threadedSocket.getRemoteAddress();
            if (threadedSocket.btSocket == null) {
                String str2 = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("<><><>btsocket is null");
                sb2.append(threadedSocket.getLocalAddress());
                sb2.append(threadedSocket.getRemoteAddress());
                Log.d(str2, sb2.toString());
            }
            String str3 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("<><><>btsocket is local address ");
            sb3.append(threadedSocket.getLocalAddress());
            sb3.append(" and remote address is: ");
            sb3.append(threadedSocket.getRemoteAddress());
            Log.d(str3, sb3.toString());
            if (remoteAddress != null) {
                threadedSocket.send(jsonObject.get("event_id").getAsString(), bArr);
            }
        }
        VectorApp.getInstance().mBtle.sendMessage(bArr);
    }
}
