package org.jitsi.meet.sdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

class BluetoothHeadsetMonitor {
    /* access modifiers changed from: private */
    public final AudioModeModule audioModeModule;
    private final Context context;
    /* access modifiers changed from: private */
    public BluetoothHeadset headset;
    /* access modifiers changed from: private */
    public boolean headsetAvailable = false;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Runnable updateDevicesRunnable = new Runnable() {
        public void run() {
            BluetoothHeadsetMonitor.this.headsetAvailable = BluetoothHeadsetMonitor.this.headset != null && !BluetoothHeadsetMonitor.this.headset.getConnectedDevices().isEmpty();
            BluetoothHeadsetMonitor.this.audioModeModule.onBluetoothDeviceChange();
        }
    };

    public BluetoothHeadsetMonitor(AudioModeModule audioModeModule2, Context context2) {
        this.audioModeModule = audioModeModule2;
        this.context = context2;
        if (!((AudioManager) context2.getSystemService("audio")).isBluetoothScoAvailableOffCall()) {
            Log.w("AudioMode", "Bluetooth SCO is not available");
            return;
        }
        if (getBluetoothHeadsetProfileProxy()) {
            registerBluetoothReceiver();
            updateDevices();
        }
    }

    private boolean getBluetoothHeadsetProfileProxy() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null) {
            Log.w("AudioMode", "Device doesn't support Bluetooth");
            return false;
        }
        return defaultAdapter.getProfileProxy(this.context, new ServiceListener() {
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                if (i == 1) {
                    BluetoothHeadsetMonitor.this.headset = (BluetoothHeadset) bluetoothProfile;
                    BluetoothHeadsetMonitor.this.updateDevices();
                }
            }

            public void onServiceDisconnected(int i) {
                onServiceConnected(i, null);
            }
        }, 1);
    }

    public boolean isHeadsetAvailable() {
        return this.headsetAvailable;
    }

    /* access modifiers changed from: private */
    public void onBluetoothReceiverReceive(Context context2, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED")) {
            int intExtra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -99);
            if (intExtra == 0 || intExtra == 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("BT headset connection state changed: ");
                sb.append(intExtra);
                Log.d("AudioMode", sb.toString());
                updateDevices();
            }
        } else if (action.equals("android.media.ACTION_SCO_AUDIO_STATE_UPDATED")) {
            int intExtra2 = intent.getIntExtra("android.media.extra.SCO_AUDIO_STATE", -99);
            switch (intExtra2) {
                case 0:
                case 1:
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("BT SCO connection state changed: ");
                    sb2.append(intExtra2);
                    Log.d("AudioMode", sb2.toString());
                    updateDevices();
                    return;
                default:
                    return;
            }
        }
    }

    private void registerBluetoothReceiver() {
        C31373 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                BluetoothHeadsetMonitor.this.onBluetoothReceiverReceive(context, intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
        this.context.registerReceiver(r0, intentFilter);
    }

    /* access modifiers changed from: private */
    public void updateDevices() {
        this.mainThreadHandler.post(this.updateDevicesRunnable);
    }
}
