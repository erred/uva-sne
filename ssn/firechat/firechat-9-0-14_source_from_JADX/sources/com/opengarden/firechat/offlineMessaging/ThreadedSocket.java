package com.opengarden.firechat.offlineMessaging;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opengarden.firechat.VectorApp;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;

public class ThreadedSocket {
    public static int MAX_MESSAGE_LENGTH = 60000;
    public static String TAG = "ThreadedSocket";
    BluetoothSocket btSocket = null;
    private String remoteAddress;
    /* access modifiers changed from: private */
    public HashMap<String, ByteBuffer> sendMap = new HashMap<>();
    /* access modifiers changed from: private */
    public LinkedBlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    private Thread sender;
    /* access modifiers changed from: private */
    public VectorApp vectorApp = VectorApp.getInstance();

    class SenderThread extends Thread {

        /* renamed from: os */
        OutputStream f142os;
        BufferedOutputStream outBytes;

        SenderThread() {
        }

        public void run() {
            try {
                if (ThreadedSocket.this.btSocket != null) {
                    this.f142os = ThreadedSocket.this.btSocket.getOutputStream();
                }
                this.outBytes = new BufferedOutputStream(this.f142os);
                while (ThreadedSocket.this.isConnected()) {
                    final String str = (String) ThreadedSocket.this.sendQueue.take();
                    ByteBuffer byteBuffer = (ByteBuffer) ThreadedSocket.this.sendMap.remove(str);
                    this.outBytes.write(byteBuffer.array());
                    this.outBytes.write(-111);
                    byteBuffer.clear();
                    this.outBytes.flush();
                    this.f142os.flush();
                    ThreadedSocket.this.vectorApp.runOnUIThread(new Runnable() {
                        public void run() {
                            String str = ThreadedSocket.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Message sent  ");
                            sb.append(str);
                            Log.d(str, sb.toString());
                            ThreadedSocket.this.vectorApp.handleSentOfflineMessage(str);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ThreadedSocket.this.close();
        }
    }

    public ThreadedSocket(BluetoothSocket bluetoothSocket) {
        this.remoteAddress = bluetoothSocket.getRemoteDevice().getAddress();
        this.btSocket = bluetoothSocket;
    }

    /* access modifiers changed from: 0000 */
    public String getLocalAddress() {
        if (this.btSocket != null) {
            return Bluetooth.getAddress();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public String getRemoteAddress() {
        if (this.btSocket != null) {
            return this.btSocket.getRemoteDevice().getAddress();
        }
        return null;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:5|6|7|8) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0010 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void close() {
        /*
            r2 = this;
            monitor-enter(r2)
            java.lang.String r0 = r2.remoteAddress     // Catch:{ all -> 0x001f }
            com.opengarden.firechat.offlineMessaging.LocalConnectionManager.connectionLost(r0)     // Catch:{ all -> 0x001f }
            android.bluetooth.BluetoothSocket r0 = r2.btSocket     // Catch:{ all -> 0x001f }
            r1 = 0
            if (r0 == 0) goto L_0x0012
            android.bluetooth.BluetoothSocket r0 = r2.btSocket     // Catch:{ Exception -> 0x0010 }
            r0.close()     // Catch:{ Exception -> 0x0010 }
        L_0x0010:
            r2.btSocket = r1     // Catch:{ all -> 0x001f }
        L_0x0012:
            java.lang.Thread r0 = r2.sender     // Catch:{ all -> 0x001f }
            if (r0 == 0) goto L_0x001d
            java.lang.Thread r0 = r2.sender     // Catch:{ all -> 0x001f }
            r0.interrupt()     // Catch:{ all -> 0x001f }
            r2.sender = r1     // Catch:{ all -> 0x001f }
        L_0x001d:
            monitor-exit(r2)
            return
        L_0x001f:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.offlineMessaging.ThreadedSocket.close():void");
    }

    public void send(String str, byte[] bArr) {
        try {
            ByteBuffer wrap = ByteBuffer.wrap(bArr);
            if (!this.sendMap.containsKey(str)) {
                this.sendMap.put(str, wrap);
                this.sendQueue.put(str);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.sender = new SenderThread();
        this.sender.start();
        receiver();
        if (this.sender != null) {
            try {
                this.sender.join();
            } catch (InterruptedException unused) {
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void receiver() {
        InputStream inputStream = null;
        try {
            if (this.btSocket != null) {
                inputStream = this.btSocket.getInputStream();
            }
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
            while (isConnected()) {
                Vector vector = new Vector();
                if (dataInputStream.available() > 0) {
                    boolean z = true;
                    while (z) {
                        try {
                            int read = dataInputStream.read();
                            String str = TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("reading item ");
                            sb.append(vector.size());
                            sb.append("   ");
                            byte b = (byte) read;
                            sb.append(b);
                            Log.d(str, sb.toString());
                            if (b == -111) {
                                z = false;
                            } else {
                                vector.add(Byte.valueOf(b));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (vector.size() > 0) {
                        String str2 = TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("done reading, size is ");
                        sb2.append(vector.size());
                        Log.d(str2, sb2.toString());
                        Byte[] bArr = new Byte[vector.size()];
                        vector.toArray(bArr);
                        final byte[] primitive = ArrayUtils.toPrimitive(bArr);
                        this.vectorApp.runOnUIThread(new Runnable() {
                            public void run() {
                                OfflineMessage offlineMessage;
                                try {
                                    offlineMessage = (OfflineMessage) SerializationUtils.deserialize(primitive);
                                } catch (Exception e) {
                                    try {
                                        JsonObject asJsonObject = new JsonParser().parse(new String(primitive).trim()).getAsJsonObject();
                                        if (asJsonObject != null) {
                                            ThreadedSocket.this.vectorApp.handleReceivedOfflineSync(asJsonObject);
                                        }
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                    e.printStackTrace();
                                    offlineMessage = null;
                                }
                                if (offlineMessage != null) {
                                    String str = ThreadedSocket.TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("inserting offline message from peer   ");
                                    sb.append(offlineMessage.getTransactionId());
                                    Log.d(str, sb.toString());
                                    ThreadedSocket.this.vectorApp.handleReceivedOfflineMessage(offlineMessage);
                                    return;
                                }
                                Log.d(ThreadedSocket.TAG, "OFFLINE MESSAGE IS NULL");
                            }
                        });
                    } else {
                        Log.d(TAG, "VETOR SIZE MESSAGE IS 0");
                    }
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        close();
    }

    public boolean isConnected() {
        if (this.btSocket == null) {
            return false;
        }
        try {
            return ((Boolean) Reflect.call((Object) this.btSocket, "isConnected", new Object[0])).booleanValue();
        } catch (Exception unused) {
            return true;
        }
    }
}
