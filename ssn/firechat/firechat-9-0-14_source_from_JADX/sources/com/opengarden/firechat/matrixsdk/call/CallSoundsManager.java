package com.opengarden.firechat.matrixsdk.call;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore.Audio.Media;
import android.support.p000v4.provider.FontsContractCompat.FontRequestCallback;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CallSoundsManager {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "CallSoundsManager";
    private static final int VIBRATE_DURATION = 500;
    private static final long[] VIBRATE_PATTERN = {0, 500, 1000};
    private static final int VIBRATE_SLEEP = 1000;
    private static final Map<String, Uri> mRingtoneUrlByFileName = new HashMap();
    private static CallSoundsManager mSharedInstance;
    /* access modifiers changed from: private */
    public final Set<OnAudioFocusListener> mAudioFocusListeners = new HashSet();
    private AudioManager mAudioManager = null;
    private Integer mAudioMode = null;
    private final Context mContext;
    private final OnAudioFocusChangeListener mFocusListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int i) {
            switch (i) {
                case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
                case -2:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case -1:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_LOSS");
                    break;
                case 0:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_REQUEST_FAILED");
                    break;
                case 1:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_GAIN");
                    break;
                case 2:
                    Log.m209d(CallSoundsManager.LOG_TAG, "## OnAudioFocusChangeListener(): AUDIOFOCUS_GAIN_TRANSIENT");
                    break;
            }
            synchronized (CallSoundsManager.LOG_TAG) {
                for (OnAudioFocusListener onFocusChanged : CallSoundsManager.this.mAudioFocusListeners) {
                    try {
                        onFocusChanged.onFocusChanged(i);
                    } catch (Exception e) {
                        String access$000 = CallSoundsManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## onFocusChanged() failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$000, sb.toString());
                    }
                }
            }
        }
    };
    private boolean mIsFocusGranted = false;
    private boolean mIsRinging;
    private Boolean mIsSpeakerphoneOn = null;
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer = null;
    private final Set<OnAudioConfigurationUpdateListener> mOnAudioConfigurationUpdateListener = new HashSet();
    /* access modifiers changed from: private */
    public int mPlayingSound = -1;
    private Ringtone mRingTone;

    public interface OnAudioConfigurationUpdateListener {
        void onAudioConfigurationUpdate();
    }

    public interface OnAudioFocusListener {
        void onFocusChanged(int i);
    }

    public interface OnMediaListener {
        void onMediaCompleted();

        void onMediaPlay();

        void onMediaReadyToPlay();
    }

    private CallSoundsManager(Context context) {
        this.mContext = context;
    }

    public static CallSoundsManager getSharedInstance(Context context) {
        if (mSharedInstance == null) {
            mSharedInstance = new CallSoundsManager(context.getApplicationContext());
        }
        return mSharedInstance;
    }

    public void addAudioConfigurationListener(OnAudioConfigurationUpdateListener onAudioConfigurationUpdateListener) {
        synchronized (LOG_TAG) {
            this.mOnAudioConfigurationUpdateListener.add(onAudioConfigurationUpdateListener);
        }
    }

    public void removeAudioConfigurationListener(OnAudioConfigurationUpdateListener onAudioConfigurationUpdateListener) {
        synchronized (LOG_TAG) {
            this.mOnAudioConfigurationUpdateListener.remove(onAudioConfigurationUpdateListener);
        }
    }

    private void dispatchAudioConfigurationUpdate() {
        synchronized (LOG_TAG) {
            for (OnAudioConfigurationUpdateListener onAudioConfigurationUpdate : this.mOnAudioConfigurationUpdateListener) {
                try {
                    onAudioConfigurationUpdate.onAudioConfigurationUpdate();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## dispatchAudioConfigurationUpdate() failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    public void addFocusListener(OnAudioFocusListener onAudioFocusListener) {
        synchronized (LOG_TAG) {
            this.mAudioFocusListeners.add(onAudioFocusListener);
        }
    }

    public void removeFocusListener(OnAudioFocusListener onAudioFocusListener) {
        synchronized (LOG_TAG) {
            this.mAudioFocusListeners.remove(onAudioFocusListener);
        }
    }

    private AudioManager getAudioManager() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        }
        return this.mAudioManager;
    }

    public boolean isRinging() {
        return this.mIsRinging;
    }

    public boolean isFocusGranted() {
        return this.mIsFocusGranted;
    }

    public void stopSounds() {
        this.mIsRinging = false;
        if (this.mRingTone != null) {
            this.mRingTone.stop();
            this.mRingTone = null;
        }
        if (this.mMediaPlayer != null) {
            if (this.mMediaPlayer.isPlaying()) {
                this.mMediaPlayer.stop();
            }
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        this.mPlayingSound = -1;
        enableVibrating(false);
    }

    public void stopRinging() {
        Log.m209d(LOG_TAG, "stopRinging");
        stopSounds();
        enableVibrating(false);
    }

    public void requestAudioFocus() {
        if (!this.mIsFocusGranted) {
            AudioManager audioManager = getAudioManager();
            if (audioManager != null) {
                int requestAudioFocus = audioManager.requestAudioFocus(this.mFocusListener, 0, 1);
                if (1 == requestAudioFocus) {
                    this.mIsFocusGranted = true;
                    Log.m209d(LOG_TAG, "## getAudioFocus(): granted");
                } else {
                    this.mIsFocusGranted = false;
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## getAudioFocus(): refused - focusResult=");
                    sb.append(requestAudioFocus);
                    Log.m217w(str, sb.toString());
                }
            }
            dispatchAudioConfigurationUpdate();
            return;
        }
        Log.m209d(LOG_TAG, "## getAudioFocus(): already granted");
    }

    public void releaseAudioFocus() {
        if (this.mIsFocusGranted) {
            AudioManager audioManager = getAudioManager();
            if (audioManager != null) {
                int abandonAudioFocus = audioManager.abandonAudioFocus(this.mFocusListener);
                if (1 == abandonAudioFocus) {
                    Log.m209d(LOG_TAG, "## releaseAudioFocus(): abandonAudioFocus = AUDIOFOCUS_REQUEST_GRANTED");
                }
                if (abandonAudioFocus == 0) {
                    Log.m209d(LOG_TAG, "## releaseAudioFocus(): abandonAudioFocus = AUDIOFOCUS_REQUEST_FAILED");
                }
            } else {
                Log.m209d(LOG_TAG, "## releaseAudioFocus(): failure - invalid AudioManager");
            }
            this.mIsFocusGranted = false;
        }
        restoreAudioConfig();
        dispatchAudioConfigurationUpdate();
    }

    public void startRinging(int i, String str) {
        if (this.mRingTone != null) {
            Log.m215v(LOG_TAG, "ring tone already ringing");
        }
        stopSounds();
        this.mIsRinging = true;
        this.mRingTone = getRingTone(this.mContext, i, str, RingtoneManager.getDefaultUri(1));
        if (this.mRingTone != null) {
            setSpeakerphoneOn(false, true);
            this.mRingTone.play();
        } else {
            Log.m211e(LOG_TAG, "startRinging : fail to retrieve RING_TONE_START_RINGING");
        }
        enableVibrating(true);
    }

    public void startRingingSilently() {
        this.mIsRinging = true;
    }

    private void enableVibrating(boolean z) {
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        if (vibrator == null || !vibrator.hasVibrator()) {
            Log.m217w(LOG_TAG, "## startVibrating(): vibrator access failed");
        } else if (z) {
            vibrator.vibrate(VIBRATE_PATTERN, 0);
            Log.m209d(LOG_TAG, "## startVibrating(): Vibrate started");
        } else {
            vibrator.cancel();
            Log.m209d(LOG_TAG, "## startVibrating(): Vibrate canceled");
        }
    }

    public void startSound(int i, boolean z, final OnMediaListener onMediaListener) {
        Log.m209d(LOG_TAG, "startSound");
        if (this.mPlayingSound == i) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## startSound() : already playing ");
            sb.append(i);
            Log.m209d(str, sb.toString());
            return;
        }
        stopSounds();
        this.mPlayingSound = i;
        this.mMediaPlayer = MediaPlayer.create(this.mContext, i);
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setLooping(z);
            if (onMediaListener != null) {
                onMediaListener.onMediaReadyToPlay();
            }
            this.mMediaPlayer.start();
            if (onMediaListener != null) {
                onMediaListener.onMediaPlay();
            }
            this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (onMediaListener != null) {
                        onMediaListener.onMediaCompleted();
                    }
                    CallSoundsManager.this.mPlayingSound = -1;
                    if (CallSoundsManager.this.mMediaPlayer != null) {
                        CallSoundsManager.this.mMediaPlayer.release();
                        CallSoundsManager.this.mMediaPlayer = null;
                    }
                }
            });
        } else {
            Log.m211e(LOG_TAG, "startSound : failed");
        }
    }

    private static Uri getRingToneUri(Context context, int i, String str) {
        Uri uri = (Uri) mRingtoneUrlByFileName.get(str);
        if (uri != null) {
            try {
                File file = new File(uri.toString());
                if (file != null && file.exists() && file.canRead()) {
                    return uri;
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getRingToneUri() failed ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        try {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("/");
            sb2.append(context.getApplicationContext().getPackageName().hashCode());
            sb2.append("/Audio/");
            File file2 = new File(externalStorageDirectory, sb2.toString());
            if (!file2.exists()) {
                file2.mkdirs();
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append(file2);
            sb3.append("/");
            File file3 = new File(sb3.toString(), str);
            if (file3.exists()) {
                Cursor query = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_data=? ", new String[]{file3.getAbsolutePath()}, null);
                if (query != null && query.moveToFirst()) {
                    int i2 = query.getInt(query.getColumnIndex("_id"));
                    Uri parse = Uri.parse("content://media/external/audio/media");
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("");
                    sb4.append(i2);
                    uri = Uri.withAppendedPath(parse, sb4.toString());
                }
                if (query != null) {
                    query.close();
                }
            }
            if (uri == null) {
                if (!file3.exists()) {
                    try {
                        byte[] bArr = new byte[1024];
                        InputStream openRawResource = context.getResources().openRawResource(i);
                        FileOutputStream fileOutputStream = new FileOutputStream(file3);
                        for (int read = openRawResource.read(bArr); read != -1; read = openRawResource.read(bArr)) {
                            fileOutputStream.write(bArr, 0, read);
                        }
                        fileOutputStream.close();
                    } catch (Exception e2) {
                        String str3 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## getRingToneUri():  Exception1 Msg=");
                        sb5.append(e2.getMessage());
                        Log.m211e(str3, sb5.toString());
                    }
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put("_data", file3.getAbsolutePath());
                contentValues.put("title", str);
                contentValues.put("mime_type", "audio/ogg");
                contentValues.put("_size", Long.valueOf(file3.length()));
                contentValues.put("artist", Integer.valueOf(C1299R.string.app_name));
                contentValues.put("is_ringtone", Boolean.valueOf(true));
                contentValues.put("is_notification", Boolean.valueOf(true));
                contentValues.put("is_alarm", Boolean.valueOf(true));
                contentValues.put("is_music", Boolean.valueOf(true));
                uri = context.getContentResolver().insert(Media.getContentUriForPath(file3.getAbsolutePath()), contentValues);
            }
            if (uri != null) {
                mRingtoneUrlByFileName.put(str, uri);
                return uri;
            }
        } catch (Exception e3) {
            String str4 = LOG_TAG;
            StringBuilder sb6 = new StringBuilder();
            sb6.append("## getRingToneUri():  Exception2 Msg=");
            sb6.append(e3.getLocalizedMessage());
            Log.m211e(str4, sb6.toString());
        }
        return null;
    }

    private static Ringtone uriToRingTone(Context context, Uri uri) {
        if (uri != null) {
            try {
                return RingtoneManager.getRingtone(context, uri);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## uriToRingTone() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return null;
    }

    private static Ringtone getRingTone(Context context, int i, String str, Uri uri) {
        Ringtone uriToRingTone = uriToRingTone(context, getRingToneUri(context, i, str));
        if (uriToRingTone == null) {
            uriToRingTone = uriToRingTone(context, uri);
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getRingTone() : resId ");
        sb.append(i);
        sb.append(" filename ");
        sb.append(str);
        sb.append(" defaultRingToneUri ");
        sb.append(uri);
        sb.append(" returns ");
        sb.append(uriToRingTone);
        Log.m209d(str2, sb.toString());
        return uriToRingTone;
    }

    private void backupAudioConfig() {
        if (this.mAudioMode == null) {
            AudioManager audioManager = getAudioManager();
            this.mAudioMode = Integer.valueOf(audioManager.getMode());
            this.mIsSpeakerphoneOn = Boolean.valueOf(audioManager.isSpeakerphoneOn());
        }
    }

    private void restoreAudioConfig() {
        if (this.mAudioMode != null && this.mIsSpeakerphoneOn != null) {
            Log.m209d(LOG_TAG, "## restoreAudioConfig() starts");
            AudioManager audioManager = getAudioManager();
            if (this.mAudioMode.intValue() != audioManager.getMode()) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## restoreAudioConfig() : restore audio mode ");
                sb.append(this.mAudioMode);
                Log.m209d(str, sb.toString());
                audioManager.setMode(this.mAudioMode.intValue());
            }
            if (this.mIsSpeakerphoneOn.booleanValue() != audioManager.isSpeakerphoneOn()) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## restoreAudioConfig() : restore speaker ");
                sb2.append(this.mIsSpeakerphoneOn);
                Log.m209d(str2, sb2.toString());
                audioManager.setSpeakerphoneOn(this.mIsSpeakerphoneOn.booleanValue());
            }
            if (audioManager.isBluetoothScoOn()) {
                Log.m209d(LOG_TAG, "## restoreAudioConfig() : ends the bluetooth calls");
                audioManager.stopBluetoothSco();
                audioManager.setBluetoothScoOn(false);
            }
            this.mAudioMode = null;
            this.mIsSpeakerphoneOn = null;
            Log.m209d(LOG_TAG, "## restoreAudioConfig() done");
        }
    }

    public void setCallSpeakerphoneOn(boolean z) {
        setSpeakerphoneOn(true, z);
    }

    public void setSpeakerphoneOn(boolean z, boolean z2) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setCallSpeakerphoneOn ");
        sb.append(z2);
        Log.m209d(str, sb.toString());
        backupAudioConfig();
        try {
            AudioManager audioManager = getAudioManager();
            int i = z ? 3 : 1;
            if (audioManager.getMode() != i) {
                audioManager.setMode(i);
            }
            if (!z2) {
                try {
                    if (HeadsetConnectionReceiver.isBTHeadsetPlugged()) {
                        audioManager.startBluetoothSco();
                        audioManager.setBluetoothScoOn(true);
                    } else if (audioManager.isBluetoothScoOn()) {
                        audioManager.stopBluetoothSco();
                        audioManager.setBluetoothScoOn(false);
                    }
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## setSpeakerphoneOn() failed ");
                    sb2.append(e.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            if (z2 != audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(z2);
            }
        } catch (Exception e2) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## setSpeakerphoneOn() failed ");
            sb3.append(e2.getMessage());
            Log.m211e(str3, sb3.toString());
            restoreAudioConfig();
        }
        dispatchAudioConfigurationUpdate();
    }

    public void toggleSpeaker() {
        AudioManager audioManager = getAudioManager();
        boolean z = !audioManager.isSpeakerphoneOn();
        audioManager.setSpeakerphoneOn(z);
        if (!z) {
            try {
                if (HeadsetConnectionReceiver.isBTHeadsetPlugged()) {
                    audioManager.startBluetoothSco();
                    audioManager.setBluetoothScoOn(true);
                } else if (audioManager.isBluetoothScoOn()) {
                    audioManager.stopBluetoothSco();
                    audioManager.setBluetoothScoOn(false);
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## toggleSpeaker() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        dispatchAudioConfigurationUpdate();
    }

    public boolean isSpeakerphoneOn() {
        return getAudioManager().isSpeakerphoneOn();
    }

    public void setMicrophoneMute(boolean z) {
        getAudioManager().setMicrophoneMute(z);
        dispatchAudioConfigurationUpdate();
    }

    public boolean isMicrophoneMute() {
        return getAudioManager().isMicrophoneMute();
    }
}
