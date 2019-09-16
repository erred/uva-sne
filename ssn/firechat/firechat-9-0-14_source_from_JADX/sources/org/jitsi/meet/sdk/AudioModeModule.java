package org.jitsi.meet.sdk;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.stats.netstats.NetstatsParserPatterns;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class AudioModeModule extends ReactContextBaseJavaModule {
    private static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    private static final int AUDIO_CALL = 1;
    private static final int DEFAULT = 0;
    private static final String DEVICE_BLUETOOTH = "BLUETOOTH";
    private static final String DEVICE_EARPIECE = "EARPIECE";
    private static final String DEVICE_HEADPHONES = "HEADPHONES";
    private static final String DEVICE_SPEAKER = "SPEAKER";
    private static final String MODULE_NAME = "AudioMode";
    static final String TAG = "AudioMode";
    private static final int VIDEO_CALL = 2;
    /* access modifiers changed from: private */
    public final AudioManager audioManager;
    /* access modifiers changed from: private */
    public Set<String> availableDevices = new HashSet();
    private BluetoothHeadsetMonitor bluetoothHeadsetMonitor;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public int mode = -1;
    private final Runnable onAudioDeviceChangeRunner = new Runnable() {
        @TargetApi(23)
        public void run() {
            HashSet hashSet = new HashSet();
            for (AudioDeviceInfo type : AudioModeModule.this.audioManager.getDevices(3)) {
                int type2 = type.getType();
                if (type2 != 7) {
                    switch (type2) {
                        case 1:
                            hashSet.add(AudioModeModule.DEVICE_EARPIECE);
                            break;
                        case 2:
                            hashSet.add(AudioModeModule.DEVICE_SPEAKER);
                            break;
                        case 3:
                        case 4:
                            hashSet.add(AudioModeModule.DEVICE_HEADPHONES);
                            break;
                    }
                } else {
                    hashSet.add(AudioModeModule.DEVICE_BLUETOOTH);
                }
            }
            AudioModeModule.this.availableDevices = hashSet;
            StringBuilder sb = new StringBuilder();
            sb.append("Available audio devices: ");
            sb.append(AudioModeModule.this.availableDevices.toString());
            Log.d("AudioMode", sb.toString());
            AudioModeModule.this.userSelectedDevice = null;
            if (AudioModeModule.this.mode != -1) {
                AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode);
            }
        }
    };
    /* access modifiers changed from: private */
    public String selectedDevice;
    private final Runnable updateAudioRouteRunner = new Runnable() {
        public void run() {
            if (AudioModeModule.this.mode != -1) {
                AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode);
            }
        }
    };
    /* access modifiers changed from: private */
    public String userSelectedDevice;

    public String getName() {
        return "AudioMode";
    }

    static {
        int i = VERSION.SDK_INT;
    }

    public AudioModeModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        this.audioManager = (AudioManager) reactApplicationContext.getSystemService("audio");
        setupAudioRouteChangeDetection();
        if (VERSION.SDK_INT >= 23) {
            this.mainThreadHandler.post(this.onAudioDeviceChangeRunner);
            return;
        }
        if (reactApplicationContext.getPackageManager().hasSystemFeature("android.hardware.telephony")) {
            this.availableDevices.add(DEVICE_EARPIECE);
        }
        this.availableDevices.add(DEVICE_SPEAKER);
    }

    public Map<String, Object> getConstants() {
        HashMap hashMap = new HashMap();
        hashMap.put("AUDIO_CALL", Integer.valueOf(1));
        hashMap.put(NetstatsParserPatterns.TYPE_BACKGROUND_PATTERN, Integer.valueOf(0));
        hashMap.put("VIDEO_CALL", Integer.valueOf(2));
        return hashMap;
    }

    @ReactMethod
    public void getAudioDevices(final Promise promise) {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                WritableMap createMap = Arguments.createMap();
                createMap.putString("selected", AudioModeModule.this.selectedDevice);
                WritableArray createArray = Arguments.createArray();
                for (String str : AudioModeModule.this.availableDevices) {
                    if (AudioModeModule.this.mode != 2 || !str.equals(AudioModeModule.DEVICE_EARPIECE)) {
                        createArray.pushString(str);
                    }
                }
                createMap.putArray("devices", createArray);
                promise.resolve(createMap);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void onAudioDeviceChange() {
        this.mainThreadHandler.post(this.onAudioDeviceChangeRunner);
    }

    /* access modifiers changed from: 0000 */
    public void onBluetoothDeviceChange() {
        if (this.bluetoothHeadsetMonitor == null || !this.bluetoothHeadsetMonitor.isHeadsetAvailable()) {
            this.availableDevices.remove(DEVICE_BLUETOOTH);
        } else {
            this.availableDevices.add(DEVICE_BLUETOOTH);
        }
        if (this.mode != -1) {
            updateAudioRoute(this.mode);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onHeadsetDeviceChange() {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                if (AudioModeModule.this.audioManager.isWiredHeadsetOn()) {
                    AudioModeModule.this.availableDevices.add(AudioModeModule.DEVICE_HEADPHONES);
                } else {
                    AudioModeModule.this.availableDevices.remove(AudioModeModule.DEVICE_HEADPHONES);
                }
                if (AudioModeModule.this.mode != -1) {
                    AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode);
                }
            }
        });
    }

    @ReactMethod
    public void setAudioDevice(final String str) {
        this.mainThreadHandler.post(new Runnable() {
            public void run() {
                if (!AudioModeModule.this.availableDevices.contains(str)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Audio device not available: ");
                    sb.append(str);
                    Log.d("AudioMode", sb.toString());
                    AudioModeModule.this.userSelectedDevice = null;
                    return;
                }
                if (AudioModeModule.this.mode != -1) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("User selected device set to: ");
                    sb2.append(str);
                    Log.d("AudioMode", sb2.toString());
                    AudioModeModule.this.userSelectedDevice = str;
                    AudioModeModule.this.updateAudioRoute(AudioModeModule.this.mode);
                }
            }
        });
    }

    private void setBluetoothAudioRoute(boolean z) {
        if (z) {
            this.audioManager.startBluetoothSco();
            this.audioManager.setBluetoothScoOn(true);
            return;
        }
        this.audioManager.setBluetoothScoOn(false);
        this.audioManager.stopBluetoothSco();
    }

    @ReactMethod
    public void setMode(final int i, final Promise promise) {
        if (i == 0 || i == 1 || i == 2) {
            this.mainThreadHandler.post(new Runnable() {
                public void run() {
                    boolean z;
                    try {
                        z = AudioModeModule.this.updateAudioRoute(i);
                    } catch (Throwable th) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Failed to update audio route for mode: ");
                        sb.append(i);
                        Log.e("AudioMode", sb.toString(), th);
                        z = false;
                    }
                    if (z) {
                        AudioModeModule.this.mode = i;
                        promise.resolve(null);
                        return;
                    }
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Failed to set audio mode to ");
                    sb2.append(i);
                    promise.reject("setMode", sb2.toString());
                }
            });
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid audio mode ");
        sb.append(i);
        promise.reject("setMode", sb.toString());
    }

    private void setupAudioRouteChangeDetection() {
        if (VERSION.SDK_INT >= 23) {
            setupAudioRouteChangeDetectionM();
        } else {
            setupAudioRouteChangeDetectionPreM();
        }
    }

    @TargetApi(23)
    private void setupAudioRouteChangeDetectionM() {
        this.audioManager.registerAudioDeviceCallback(new AudioDeviceCallback() {
            public void onAudioDevicesAdded(AudioDeviceInfo[] audioDeviceInfoArr) {
                Log.d("AudioMode", "Audio devices added");
                AudioModeModule.this.onAudioDeviceChange();
            }

            public void onAudioDevicesRemoved(AudioDeviceInfo[] audioDeviceInfoArr) {
                Log.d("AudioMode", "Audio devices removed");
                AudioModeModule.this.onAudioDeviceChange();
            }
        }, null);
    }

    private void setupAudioRouteChangeDetectionPreM() {
        ReactApplicationContext reactApplicationContext = getReactApplicationContext();
        reactApplicationContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.d("AudioMode", "Wired headset added / removed");
                AudioModeModule.this.onHeadsetDeviceChange();
            }
        }, new IntentFilter(ACTION_HEADSET_PLUG));
        this.bluetoothHeadsetMonitor = new BluetoothHeadsetMonitor(this, reactApplicationContext);
    }

    /* access modifiers changed from: private */
    public boolean updateAudioRoute(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("Update audio route for mode: ");
        sb.append(i);
        Log.d("AudioMode", sb.toString());
        if (i == 0) {
            this.audioManager.setMode(0);
            this.audioManager.abandonAudioFocus(null);
            this.audioManager.setSpeakerphoneOn(false);
            setBluetoothAudioRoute(false);
            this.selectedDevice = null;
            this.userSelectedDevice = null;
            return true;
        }
        this.audioManager.setMode(3);
        this.audioManager.setMicrophoneMute(false);
        if (this.audioManager.requestAudioFocus(null, 0, 1) == 0) {
            Log.d("AudioMode", "Audio focus request failed");
            return false;
        }
        String str = this.availableDevices.contains(DEVICE_BLUETOOTH) ? DEVICE_BLUETOOTH : this.availableDevices.contains(DEVICE_HEADPHONES) ? DEVICE_HEADPHONES : (i != 1 || !this.availableDevices.contains(DEVICE_EARPIECE)) ? DEVICE_SPEAKER : DEVICE_EARPIECE;
        if (this.userSelectedDevice != null && this.availableDevices.contains(this.userSelectedDevice)) {
            str = this.userSelectedDevice;
        }
        if (this.selectedDevice != null && this.selectedDevice.equals(str)) {
            return true;
        }
        this.selectedDevice = str;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Selected audio device: ");
        sb2.append(str);
        Log.d("AudioMode", sb2.toString());
        setBluetoothAudioRoute(str.equals(DEVICE_BLUETOOTH));
        this.audioManager.setSpeakerphoneOn(str.equals(DEVICE_SPEAKER));
        return true;
    }
}
