package com.opengarden.firechat.offlineMessaging;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertiseSettings.Builder;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.p000v4.app.NotificationCompat;
import android.util.Log;
import com.facebook.react.bridge.BaseJavaModule;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.cli.HelpFormatter;
import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(18)
public class BluetoothLE {
    public static final int BLE_MTU = 20;
    static final String TAG = "BluetoothLE";
    public static final UUID firechatUuid = UUID.fromString("9C89218C-8E00-41EB-B56C-ABE58D9A8FEA");
    public static final UUID notificationUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID userDescriptionStringUuid = UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(intent);
            Log.d(str, sb.toString());
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                switch (intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 12)) {
                    case 12:
                        BluetoothLE.this.bluetoothOn();
                        return;
                    case 13:
                        BluetoothLE.this.stopPeriodicScan();
                        return;
                    default:
                        return;
                }
            }
        }
    };
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BluetoothGattCharacteristic characteristic = null;
    public Map<String, Peer> connectPeripherals = new HashMap();
    BluetoothGattServer gattServer = null;
    /* access modifiers changed from: private */
    public Handler handler = new Handler();
    /* access modifiers changed from: private */
    public final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("GATT Server ");
            sb.append(bluetoothGatt.getDevice());
            sb.append(" status:");
            sb.append(i);
            sb.append(" newState:");
            sb.append(i2);
            Log.i(str, sb.toString());
            if (i2 == 2) {
                bluetoothGatt.discoverServices();
                Log.i(BluetoothLE.TAG, "Connected to GATT server.");
            } else if (i2 == 0) {
                Log.i(BluetoothLE.TAG, "Disconnected from GATT server.");
                BluetoothLE.this.connectPeripherals.remove(bluetoothGatt.getDevice().getAddress());
            }
        }

        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onServicesDiscovered ");
            sb.append(bluetoothGatt.getDevice());
            sb.append(" status:");
            sb.append(i);
            Log.w(str, sb.toString());
            if (i == 0) {
                BluetoothLE.this.probePeripheral(bluetoothGatt);
            }
        }

        public void onMtuChanged(BluetoothGatt bluetoothGatt, int i, int i2) {
            Peer peer = (Peer) BluetoothLE.this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("new mtu:");
            sb.append(i);
            Log.w(str, sb.toString());
        }

        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (i == 0) {
                String str = BluetoothLE.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onCharacteristicRead ");
                sb.append(bluetoothGatt.getDevice());
                sb.append(" status:");
                sb.append(i);
                Log.w(str, sb.toString());
            }
        }

        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            Peer peer = (Peer) BluetoothLE.this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
            if (peer != null && peer.characteristic != null) {
                BluetoothLE.this.receiveMessageBytes(peer, bluetoothGattCharacteristic.getValue());
            }
        }

        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            super.onCharacteristicWrite(bluetoothGatt, bluetoothGattCharacteristic, i);
            Peer peer = (Peer) BluetoothLE.this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
            if (peer != null && peer.gatt != null && peer.characteristic != null) {
                if (i == 0) {
                    peer.sendQueue.pop();
                }
                BluetoothLE.this.sendPacket(peer);
            }
        }

        public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onDescriptorRead ");
            sb.append(bluetoothGatt.getDevice());
            sb.append(" descriptor:");
            sb.append(bluetoothGattDescriptor.getUuid().toString());
            sb.append(" status:");
            sb.append(i);
            Log.w(str, sb.toString());
            try {
                byte[] value = bluetoothGattDescriptor.getValue();
                String str2 = BluetoothLE.TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("onDescriptorRead b:");
                sb2.append(value);
                sb2.append(" l:");
                sb2.append(value.length);
                Log.w(str2, sb2.toString());
                String str3 = new String(value, "UTF-8");
                String str4 = BluetoothLE.TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("onDescriptorRead v:");
                sb3.append(str3);
                Log.w(str4, sb3.toString());
                Peer peer = (Peer) BluetoothLE.this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
                peer.peerId = new JSONObject(str3).getString("i");
            } catch (UnsupportedEncodingException | JSONException e) {
                Log.e(BluetoothLE.TAG, "onDescriptorRead", e);
            }
        }

        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            String str = BluetoothLE.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onDescriptorWrite ");
            sb.append(bluetoothGatt.getDevice());
            sb.append(" descriptor:");
            sb.append(bluetoothGattDescriptor.getUuid().toString());
            sb.append(" status:");
            sb.append(i);
            Log.w(str, sb.toString());
            if (i == 0) {
                Peer peer = (Peer) BluetoothLE.this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
                peer.isSubscribed = true;
                BluetoothGattDescriptor descriptor = peer.characteristic.getDescriptor(BluetoothLE.userDescriptionStringUuid);
                if (descriptor != null) {
                    bluetoothGatt.readDescriptor(descriptor);
                }
            }
        }
    };
    private LeScanCallback mOldLeCallback = new LeScanCallback() {
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            int i2 = 0;
            while (i2 < bArr.length - 2 && bArr[i2] != 0) {
                int i3 = i2 + 1;
                byte b = bArr[i2];
                byte b2 = bArr[i3];
                if (b2 != 7 || b != 17 || i3 + 17 > bArr.length || !BluetoothLE.this.getUuidFromByteArray(bArr, i3 + 16, -1).equals(BluetoothLE.firechatUuid)) {
                    if (b2 == -1 && b >= 21 && bArr[i3 + 1] == 76 && bArr[i3 + 2] == 0 && bArr[i3 + 3] == 2 && bArr[i3 + 4] == 21 && BluetoothLE.this.getUuidFromByteArray(bArr, i3 + 5, 1).equals(BluetoothLE.firechatUuid)) {
                        String str = BluetoothLE.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("found iBeacon on ");
                        sb.append(bluetoothDevice);
                        Log.d(str, sb.toString());
                    }
                } else if (BluetoothLE.this.foundPeripheral(bluetoothDevice)) {
                    String str2 = BluetoothLE.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("found FireChat service on ");
                    sb2.append(bluetoothDevice);
                    Log.d(str2, sb2.toString());
                    bluetoothDevice.connectGatt(VectorApp.getInstance(), false, BluetoothLE.this.mGattCallback);
                } else {
                    return;
                }
                i2 = b + i3;
            }
        }
    };
    ScanCallback newLeCallback;
    /* access modifiers changed from: private */
    public Runnable startPeriodicScanRunnable = new Runnable() {
        public void run() {
            BluetoothLE.this.startOldScan();
            BluetoothLE.this.handler.postDelayed(BluetoothLE.this.stopPeriodicScanRunnable, 2000);
        }
    };
    /* access modifiers changed from: private */
    public Runnable stopPeriodicScanRunnable = new Runnable() {
        public void run() {
            BluetoothLE.this.stopOldScan();
            BluetoothLE.this.handler.postDelayed(BluetoothLE.this.startPeriodicScanRunnable, 4000);
        }
    };
    public Map<String, Peer> subscribedCentrals = new HashMap();
    BluetoothGattDescriptor userDescriptor = null;

    static class Peer {
        BluetoothGattCharacteristic characteristic = null;
        BluetoothDevice device = null;
        int firstChunkSequence = -1;
        BluetoothGatt gatt = null;
        BluetoothGattServer gattServer = null;
        boolean isSubscribed = false;
        int nChunks = 0;
        byte[][] parts = null;
        String peerId;
        LinkedList<byte[]> sendQueue = null;
        int sendSequence = 0;
        int timeAdded = 0;

        public Peer(BluetoothDevice bluetoothDevice) {
            this.device = bluetoothDevice;
            this.peerId = bluetoothDevice.getAddress();
        }

        public boolean equals(Object obj) {
            return (obj instanceof Peer) && this.device.equals(((Peer) obj).device);
        }

        public int hashCode() {
            return this.device.hashCode();
        }
    }

    /* access modifiers changed from: 0000 */
    public BluetoothAdapter bluetoothAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) VectorApp.getInstance().getSystemService("bluetooth");
        if (bluetoothManager == null) {
            return null;
        }
        return bluetoothManager.getAdapter();
    }

    public BluetoothLE() {
        if (((BluetoothManager) VectorApp.getInstance().getSystemService("bluetooth")) != null) {
            if (VERSION.SDK_INT >= 21) {
                initScanCallback();
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            VectorApp.getInstance().registerReceiver(this.broadcastReceiver, intentFilter);
            bluetoothOn();
        }
    }

    /* access modifiers changed from: 0000 */
    public void bluetoothOn() {
        if (VERSION.SDK_INT >= 21) {
            startServer();
        }
        startPeriodicScan();
    }

    public int numPeers() {
        int size = this.subscribedCentrals.size();
        for (Peer peer : this.connectPeripherals.values()) {
            if (peer != null && peer.isSubscribed) {
                size++;
            }
        }
        return size;
    }

    public void probePeripheral(BluetoothGatt bluetoothGatt) {
        BluetoothGattService service = bluetoothGatt.getService(firechatUuid);
        Log.d(TAG, String.format("got gatt service:%s device:%s address:%s", new Object[]{service, bluetoothGatt.getDevice(), bluetoothGatt.getDevice().getAddress()}));
        if (service != null) {
            Peer peer = (Peer) this.connectPeripherals.get(bluetoothGatt.getDevice().getAddress());
            if (peer == null) {
                peer = new Peer(bluetoothGatt.getDevice());
                peer.timeAdded = (int) (System.nanoTime() / 1000000);
            }
            peer.sendQueue = new LinkedList<>();
            peer.gatt = bluetoothGatt;
            peer.characteristic = (BluetoothGattCharacteristic) service.getCharacteristics().get(0);
            bluetoothGatt.setCharacteristicNotification(peer.characteristic, true);
            BluetoothGattDescriptor descriptor = peer.characteristic.getDescriptor(notificationUuid);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bluetoothGatt.writeDescriptor(descriptor);
            }
            BluetoothGattDescriptor descriptor2 = peer.characteristic.getDescriptor(userDescriptionStringUuid);
            if (descriptor2 != null) {
                bluetoothGatt.readDescriptor(descriptor2);
            }
            this.connectPeripherals.put(peer.device.getAddress(), peer);
        }
    }

    /* access modifiers changed from: 0000 */
    public UUID getUuidFromByteArray(byte[] bArr, int i, int i2) {
        int[] iArr = {4, 2, 2, 2, 6};
        StringBuilder sb = new StringBuilder();
        int length = iArr.length;
        int i3 = i;
        int i4 = 0;
        while (i4 < length) {
            int i5 = iArr[i4];
            int i6 = i3;
            for (int i7 = 0; i7 < i5; i7++) {
                int i8 = (bArr[i6] & 240) >> 4;
                sb.append((char) (i8 < 10 ? i8 + 48 : (i8 + 97) - 10));
                byte b = bArr[i6] & 15;
                sb.append((char) (b < 10 ? b + 48 : (b + 97) - 10));
                i6 += i2;
            }
            sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
            i4++;
            i3 = i6;
        }
        return UUID.fromString(sb.deleteCharAt(sb.length() - 1).toString());
    }

    /* access modifiers changed from: 0000 */
    public boolean foundPeripheral(BluetoothDevice bluetoothDevice) {
        Peer peer = (Peer) this.connectPeripherals.get(bluetoothDevice.getAddress());
        if (peer != null || peer != null) {
            return false;
        }
        Peer peer2 = new Peer(bluetoothDevice);
        peer2.timeAdded = (int) (System.nanoTime() / 1000000);
        this.connectPeripherals.put(bluetoothDevice.getAddress(), peer2);
        return true;
    }

    /* access modifiers changed from: 0000 */
    @TargetApi(21)
    public void initScanCallback() {
        ByteBuffer wrap = ByteBuffer.wrap(new byte[18]);
        wrap.put(new byte[]{2, 21});
        wrap.putLong(firechatUuid.getMostSignificantBits());
        wrap.putLong(firechatUuid.getLeastSignificantBits());
        final byte[] array = wrap.array();
        this.newLeCallback = new ScanCallback() {
            public void onScanResult(int i, ScanResult scanResult) {
                BluetoothDevice device = scanResult.getDevice();
                ScanRecord scanRecord = scanResult.getScanRecord();
                List serviceUuids = scanRecord.getServiceUuids();
                if (serviceUuids == null || serviceUuids.indexOf(new ParcelUuid(BluetoothLE.firechatUuid)) == -1) {
                    if (Arrays.equals(scanRecord.getManufacturerSpecificData(76), array)) {
                        String str = BluetoothLE.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("found iBeacon on ");
                        sb.append(device);
                        Log.d(str, sb.toString());
                    }
                } else if (BluetoothLE.this.foundPeripheral(device)) {
                    String str2 = BluetoothLE.TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("found FireChat service on ");
                    sb2.append(device);
                    sb2.append(" connecting...");
                    Log.d(str2, sb2.toString());
                    device.connectGatt(VectorApp.getInstance(), false, BluetoothLE.this.mGattCallback);
                }
            }

            public void onBatchScanResults(List<ScanResult> list) {
                for (ScanResult scanResult : list) {
                    Log.d(BluetoothLE.TAG, String.format("onBatchScanResults %s", new Object[]{scanResult}));
                    onScanResult(0, scanResult);
                }
            }

            public void onScanFailed(int i) {
                Log.e(BluetoothLE.TAG, String.format("onScanFailed %d", new Object[]{Integer.valueOf(i)}));
            }
        };
    }

    @TargetApi(21)
    public void tryStartServer() {
        BluetoothLeAdvertiser bluetoothLeAdvertiser = bluetoothAdapter().getBluetoothLeAdvertiser();
        if (bluetoothLeAdvertiser != null) {
            Builder builder = new Builder();
            builder.setAdvertiseMode(1);
            builder.setTxPowerLevel(3);
            builder.setConnectable(true);
            AdvertiseData.Builder builder2 = new AdvertiseData.Builder();
            builder2.setIncludeDeviceName(true);
            builder2.setIncludeTxPowerLevel(true);
            AdvertiseData.Builder builder3 = new AdvertiseData.Builder();
            builder3.addServiceUuid(new ParcelUuid(firechatUuid));
            bluetoothLeAdvertiser.startAdvertising(builder.build(), builder2.build(), builder3.build(), new AdvertiseCallback() {
                public void onStartSuccess(AdvertiseSettings advertiseSettings) {
                    Log.i(BluetoothLE.TAG, String.format("onStartSuccess called %s", new Object[]{advertiseSettings}));
                }

                public void onStartFailure(int i) {
                    Log.i(BluetoothLE.TAG, String.format("onStartFailure %d", new Object[]{Integer.valueOf(i)}));
                }
            });
        }
        BluetoothManager bluetoothManager = (BluetoothManager) VectorApp.getInstance().getSystemService("bluetooth");
        if (bluetoothManager != null) {
            this.gattServer = bluetoothManager.openGattServer(VectorApp.getInstance(), new BluetoothGattServerCallback() {
                public void onConnectionStateChange(BluetoothDevice bluetoothDevice, int i, int i2) {
                    Log.i(BluetoothLE.TAG, String.format("onConnectionStateChange status:%d state:%d", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
                    if (i2 == 0) {
                        BluetoothLE.this.subscribedCentrals.remove(bluetoothDevice.getAddress());
                    }
                }

                public void onCharacteristicReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                    Log.i(BluetoothLE.TAG, "onCharacteristicReadRequest");
                    BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 2, i2, null);
                }

                public void onDescriptorReadRequest(BluetoothDevice bluetoothDevice, int i, int i2, BluetoothGattDescriptor bluetoothGattDescriptor) {
                    byte[] copyOfRange;
                    Log.i(BluetoothLE.TAG, String.format("onDescriptorReadRequest device:%s requestId:%d offset:%d descriptor:%s", new Object[]{bluetoothDevice, Integer.valueOf(i), Integer.valueOf(i2), bluetoothGattDescriptor.getUuid().toString()}));
                    byte[] bArr = null;
                    if (!bluetoothGattDescriptor.getUuid().equals(BluetoothLE.notificationUuid)) {
                        if (bluetoothGattDescriptor.getUuid().equals(BluetoothLE.userDescriptionStringUuid)) {
                            byte[] value = BluetoothLE.this.userDescriptor.getValue();
                            if (i2 >= value.length) {
                                String str = BluetoothLE.TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("onDescriptorReadRequest responding invalid offset:");
                                sb.append(i2);
                                sb.append(" total:");
                                sb.append(BluetoothLE.this.userDescriptor.getValue().length);
                                Log.d(str, sb.toString());
                                BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 7, i2, null);
                                return;
                            }
                            copyOfRange = Arrays.copyOfRange(value, i2, value.length);
                            String str2 = BluetoothLE.TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("onDescriptorReadRequest responding to offset:");
                            sb2.append(i2);
                            sb2.append(" length:");
                            sb2.append(copyOfRange.length);
                            sb2.append("/");
                            sb2.append(BluetoothLE.this.userDescriptor.getValue().length);
                            Log.d(str2, sb2.toString());
                        }
                        BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                    } else if (BluetoothLE.this.subscribedCentrals.get(bluetoothDevice.getAddress()) != null) {
                        copyOfRange = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                    } else {
                        copyOfRange = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
                    }
                    bArr = copyOfRange;
                    BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                }

                public void onCharacteristicWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattCharacteristic bluetoothGattCharacteristic, boolean z, boolean z2, int i2, byte[] bArr) {
                    if (z2) {
                        BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                    }
                    Peer peer = (Peer) BluetoothLE.this.subscribedCentrals.get(bluetoothDevice.getAddress());
                    if (peer != null) {
                        BluetoothLE.this.receiveMessageBytes(peer, bArr);
                    }
                }

                public void onDescriptorWriteRequest(BluetoothDevice bluetoothDevice, int i, BluetoothGattDescriptor bluetoothGattDescriptor, boolean z, boolean z2, int i2, byte[] bArr) {
                    Log.i(BluetoothLE.TAG, String.format("onDescriptorWriteRequest id:%d descriptor:%s preparedWrite:%b responseNeeded:%b offset:%d value:%s", new Object[]{Integer.valueOf(i), bluetoothGattDescriptor.getUuid().toString(), Boolean.valueOf(z), Boolean.valueOf(z2), Integer.valueOf(i2), Arrays.toString(bArr)}));
                    if (z2) {
                        if (Arrays.equals(bArr, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) && bluetoothGattDescriptor.getUuid().equals(BluetoothLE.notificationUuid)) {
                            String str = BluetoothLE.TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("central:");
                            sb.append(bluetoothDevice.getAddress());
                            sb.append(" subscribed to ");
                            sb.append(BluetoothLE.notificationUuid);
                            Log.d(str, sb.toString());
                            Peer peer = (Peer) BluetoothLE.this.subscribedCentrals.get(bluetoothDevice.getAddress());
                            if (peer == null) {
                                peer = new Peer(bluetoothDevice);
                                peer.timeAdded = (int) (System.nanoTime() / 1000000);
                            }
                            peer.sendQueue = new LinkedList<>();
                            peer.gattServer = BluetoothLE.this.gattServer;
                            peer.characteristic = BluetoothLE.this.characteristic;
                            BluetoothLE.this.subscribedCentrals.put(bluetoothDevice.getAddress(), peer);
                        }
                        BluetoothLE.this.gattServer.sendResponse(bluetoothDevice, i, 0, i2, bArr);
                    }
                }

                public void onServiceAdded(int i, BluetoothGattService bluetoothGattService) {
                    Log.i(BluetoothLE.TAG, String.format("onServiceAdded %d %s", new Object[]{Integer.valueOf(i), bluetoothGattService}));
                }

                public void onExecuteWrite(BluetoothDevice bluetoothDevice, int i, boolean z) {
                    Log.i(BluetoothLE.TAG, "onExecuteWrite");
                }

                public void onNotificationSent(BluetoothDevice bluetoothDevice, int i) {
                    Peer peer = (Peer) BluetoothLE.this.subscribedCentrals.get(bluetoothDevice.getAddress());
                    if (peer != null) {
                        if (i == 0) {
                            peer.sendQueue.pop();
                        }
                        BluetoothLE.this.sendPacket(peer);
                    }
                }
            });
            this.characteristic = new BluetoothGattCharacteristic(firechatUuid, 24, 17);
            this.characteristic.addDescriptor(new BluetoothGattDescriptor(notificationUuid, 17));
            this.userDescriptor = new BluetoothGattDescriptor(userDescriptionStringUuid, 1);
            StringBuilder sb = new StringBuilder();
            sb.append("{\"mtu\":20, \"i\":\"");
            sb.append(firechatUuid);
            sb.append("\"}");
            try {
                this.userDescriptor.setValue(sb.toString().getBytes("UTF-8"));
                this.characteristic.addDescriptor(this.userDescriptor);
            } catch (UnsupportedEncodingException unused) {
            }
            BluetoothGattService bluetoothGattService = new BluetoothGattService(firechatUuid, 0);
            bluetoothGattService.addCharacteristic(this.characteristic);
            this.gattServer.addService(bluetoothGattService);
        }
    }

    @TargetApi(21)
    public boolean startServer() {
        try {
            if (!bluetoothAdapter().isMultipleAdvertisementSupported()) {
                return false;
            }
            tryStartServer();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startServer", e);
            return false;
        }
    }

    @TargetApi(21)
    public void startSpecificScan() {
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(new ParcelUuid(firechatUuid));
        ScanFilter.Builder builder2 = new ScanFilter.Builder();
        ByteBuffer wrap = ByteBuffer.wrap(new byte[18]);
        wrap.put(new byte[]{2, 21});
        wrap.putLong(firechatUuid.getMostSignificantBits());
        wrap.putLong(firechatUuid.getLeastSignificantBits());
        builder2.setManufacturerData(76, wrap.array());
        List asList = Arrays.asList(new ScanFilter[]{builder.build(), builder2.build()});
        ScanSettings.Builder builder3 = new ScanSettings.Builder();
        builder3.setScanMode(1);
        bluetoothAdapter().getBluetoothLeScanner().startScan(asList, builder3.build(), this.newLeCallback);
    }

    @TargetApi(21)
    public void stopSpecificScan() {
        bluetoothAdapter().getBluetoothLeScanner().stopScan(this.newLeCallback);
    }

    public boolean startOldScan() {
        try {
            return bluetoothAdapter().startLeScan(this.mOldLeCallback);
        } catch (NullPointerException unused) {
            return false;
        }
    }

    public void stopOldScan() {
        try {
            bluetoothAdapter().stopLeScan(this.mOldLeCallback);
        } catch (NullPointerException unused) {
        }
    }

    public void startPeriodicScan() {
        if (bluetoothAdapter() != null && bluetoothAdapter().isEnabled()) {
            if (VERSION.SDK_INT >= 21) {
                startSpecificScan();
                return;
            }
            if (startOldScan()) {
                this.handler.removeCallbacksAndMessages(this.startPeriodicScanRunnable);
                this.handler.removeCallbacksAndMessages(this.stopPeriodicScanRunnable);
                this.startPeriodicScanRunnable.run();
            }
        }
    }

    public void stopPeriodicScan() {
        if (bluetoothAdapter() != null && bluetoothAdapter().isEnabled()) {
            if (VERSION.SDK_INT >= 21) {
                stopSpecificScan();
                return;
            }
            this.handler.removeCallbacksAndMessages(this.startPeriodicScanRunnable);
            this.handler.removeCallbacksAndMessages(this.stopPeriodicScanRunnable);
        }
    }

    /* access modifiers changed from: 0000 */
    public void sendPacket(Peer peer) {
        byte[] bArr = (byte[]) peer.sendQueue.peek();
        if (bArr != null && peer.characteristic != null) {
            try {
                peer.characteristic.setValue(bArr);
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(" sending data via BLE ==> ");
                sb.append(new String(bArr));
                Log.d(str, sb.toString());
                if (peer.gattServer != null) {
                    peer.gattServer.notifyCharacteristicChanged(peer.device, peer.characteristic, true);
                } else if (peer.gatt != null) {
                    peer.gatt.writeCharacteristic(peer.characteristic);
                }
            } catch (Exception e) {
                Log.d(TAG, "sendPacket", e);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void queuePackets(Peer peer, byte[] bArr) {
        boolean z = peer.sendQueue.size() > 0;
        int i = 0;
        while (i < bArr.length) {
            int min = Math.min(bArr.length - i, 20);
            byte[] bArr2 = new byte[20];
            System.arraycopy(bArr, i, bArr2, 0, min);
            i += min;
            peer.sendQueue.add(bArr2);
        }
        try {
            peer.sendQueue.add("EOM".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!z) {
            sendPacket(peer);
        }
    }

    /* access modifiers changed from: 0000 */
    public void sendMessage(Peer peer, long j, byte[] bArr) {
        if (peer.peerId != null) {
            queuePackets(peer, bArr);
        }
    }

    public void sendMessage(long j, byte[] bArr) {
        for (Peer peer : this.connectPeripherals.values()) {
            if (peer.gatt != null) {
                sendMessage(peer, j, bArr);
            }
        }
        for (Peer sendMessage : this.subscribedCentrals.values()) {
            sendMessage(sendMessage, j, bArr);
        }
    }

    public void sendMessage(byte[] bArr) {
        for (Peer peer : this.connectPeripherals.values()) {
            if (peer.gatt != null) {
                queuePackets(peer, bArr);
            }
        }
        for (Peer queuePackets : this.subscribedCentrals.values()) {
            queuePackets(queuePackets, bArr);
        }
    }

    public void receiveMessageBytes(Peer peer, byte[] bArr) {
        try {
            String str = new String(bArr);
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getting data via BLE =>");
            sb.append(str);
            Log.d(str2, sb.toString());
            if (str.equals("EOM")) {
                byte[] bArr2 = new byte[this.byteArrayOutputStream.toByteArray().length];
                System.arraycopy(this.byteArrayOutputStream.toByteArray(), 0, bArr2, 0, this.byteArrayOutputStream.toByteArray().length);
                receiveMessage(peer.peerId, bArr2);
                this.byteArrayOutputStream = new ByteArrayOutputStream();
                return;
            }
            this.byteArrayOutputStream.write(bArr, 0, bArr.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(String str, byte[] bArr) {
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        final VectorApp instance = VectorApp.getInstance();
        try {
            jsonObject = jsonParser.parse(new String(bArr).trim()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = null;
        }
        if (jsonObject != null) {
            String asString = jsonObject.get("type").getAsString();
            if (asString != null && asString.equals("toDevice")) {
                Event event = new Event();
                event.setType(Event.EVENT_TYPE_ROOM_KEY);
                event.eventId = jsonObject.get("event_id").getAsString();
                event.roomId = jsonObject.get("room_id").getAsString();
                event.content = jsonObject.getAsJsonObject("payload").getAsJsonObject("content");
                event.setSender(jsonObject.getAsJsonObject("payload").get(BingRule.KIND_SENDER).getAsString());
                final OfflineMessage offlineMessage = new OfflineMessage(event.eventId, event.roomId, jsonObject.get("access_token").getAsString(), event);
                instance.runOnUIThread(new Runnable() {
                    public void run() {
                        instance.handleReceivedOfflineMessage(offlineMessage);
                    }
                });
            } else if (asString == null || !asString.equals("message")) {
                if (asString.equals(BaseJavaModule.METHOD_TYPE_SYNC) && (asString != null)) {
                    instance.handleReceivedOfflineSync(jsonObject);
                }
            } else {
                Event event2 = JsonUtils.toEvent(jsonObject.getAsJsonObject(NotificationCompat.CATEGORY_EVENT));
                final OfflineMessage offlineMessage2 = new OfflineMessage(event2.eventId, event2.roomId, jsonObject.get("access_token").getAsString(), event2);
                instance.runOnUIThread(new Runnable() {
                    public void run() {
                        instance.handleReceivedOfflineMessage(offlineMessage2);
                    }
                });
            }
        }
    }
}
