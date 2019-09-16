package com.opengarden.firechat.matrixsdk.call;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.HashSet;
import java.util.Set;

public class HeadsetConnectionReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "HeadsetConnectionReceiver";
    private static AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public static Boolean mIsHeadsetPlugged;
    private static HeadsetConnectionReceiver mSharedInstance;
    private final Set<OnHeadsetStatusUpdateListener> mListeners = new HashSet();

    public interface OnHeadsetStatusUpdateListener {
        void onBluetoothHeadsetUpdate(boolean z);

        void onWiredHeadsetUpdate(boolean z);
    }

    public static HeadsetConnectionReceiver getSharedInstance(Context context) {
        if (mSharedInstance == null) {
            mSharedInstance = new HeadsetConnectionReceiver();
            context.registerReceiver(mSharedInstance, new IntentFilter("android.intent.action.HEADSET_PLUG"));
            context.registerReceiver(mSharedInstance, new IntentFilter("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED"));
            context.registerReceiver(mSharedInstance, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
            context.registerReceiver(mSharedInstance, new IntentFilter("android.bluetooth.device.action.ACL_CONNECTED"));
            context.registerReceiver(mSharedInstance, new IntentFilter("android.bluetooth.device.action.ACL_DISCONNECTED"));
        }
        return mSharedInstance;
    }

    public void addListener(OnHeadsetStatusUpdateListener onHeadsetStatusUpdateListener) {
        synchronized (LOG_TAG) {
            this.mListeners.add(onHeadsetStatusUpdateListener);
        }
    }

    public void removeListener(OnHeadsetStatusUpdateListener onHeadsetStatusUpdateListener) {
        synchronized (LOG_TAG) {
            this.mListeners.remove(onHeadsetStatusUpdateListener);
        }
    }

    /* access modifiers changed from: private */
    public void onBluetoothHeadsetUpdate(boolean z) {
        synchronized (LOG_TAG) {
            for (OnHeadsetStatusUpdateListener onBluetoothHeadsetUpdate : this.mListeners) {
                try {
                    onBluetoothHeadsetUpdate.onBluetoothHeadsetUpdate(z);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onBluetoothHeadsetUpdate()) failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onWiredHeadsetUpdate(boolean z) {
        synchronized (LOG_TAG) {
            for (OnHeadsetStatusUpdateListener onWiredHeadsetUpdate : this.mListeners) {
                try {
                    onWiredHeadsetUpdate.onWiredHeadsetUpdate(z);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onWiredHeadsetUpdate()) failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    public void onReceive(Context context, Intent intent) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onReceive() : ");
        sb.append(intent.getExtras());
        Log.m209d(str, sb.toString());
        String action = intent.getAction();
        if (TextUtils.equals(action, "android.intent.action.HEADSET_PLUG") || TextUtils.equals(action, "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED") || TextUtils.equals(action, "android.bluetooth.adapter.action.STATE_CHANGED") || TextUtils.equals(action, "android.bluetooth.device.action.ACL_CONNECTED") || TextUtils.equals(action, "android.bluetooth.device.action.ACL_DISCONNECTED")) {
            Boolean bool = null;
            final boolean z = false;
            if (TextUtils.equals(action, "android.intent.action.HEADSET_PLUG")) {
                switch (intent.getIntExtra("state", -1)) {
                    case 0:
                        Log.m209d(LOG_TAG, "Headset is unplugged");
                        bool = Boolean.valueOf(false);
                        break;
                    case 1:
                        Log.m209d(LOG_TAG, "Headset is plugged");
                        bool = Boolean.valueOf(true);
                        break;
                    default:
                        Log.m209d(LOG_TAG, "undefined state");
                        break;
                }
            } else {
                int profileConnectionState = BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(1);
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("bluetooth headset state ");
                sb2.append(profileConnectionState);
                Log.m209d(str2, sb2.toString());
                bool = Boolean.valueOf(2 == profileConnectionState);
                if (mIsHeadsetPlugged != bool) {
                    z = true;
                }
            }
            if (bool != mIsHeadsetPlugged) {
                mIsHeadsetPlugged = bool;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        if (z) {
                            HeadsetConnectionReceiver.this.onBluetoothHeadsetUpdate(HeadsetConnectionReceiver.mIsHeadsetPlugged.booleanValue());
                        } else {
                            HeadsetConnectionReceiver.this.onWiredHeadsetUpdate(HeadsetConnectionReceiver.mIsHeadsetPlugged.booleanValue());
                        }
                    }
                }, 1000);
            }
        }
    }

    private static AudioManager getAudioManager(Context context) {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService("audio");
        }
        return mAudioManager;
    }

    @SuppressLint({"Deprecation"})
    public static boolean isHeadsetPlugged(Context context) {
        if (mIsHeadsetPlugged == null) {
            mIsHeadsetPlugged = Boolean.valueOf(isBTHeadsetPlugged() || getAudioManager(context).isWiredHeadsetOn());
        }
        return mIsHeadsetPlugged.booleanValue();
    }

    public static boolean isBTHeadsetPlugged() {
        return 2 == BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(1);
    }
}
