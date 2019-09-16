package com.opengarden.firechat.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.Log;

public class RageShake implements SensorEventListener {
    private static final String LOG_TAG = "RageShake";
    private static final long mIntervalNanos = 3000000;
    private static float mThreshold = 35.0f;
    private static final long mTimeToNextShakeMs = 10000;
    /* access modifiers changed from: private */
    public Context mContext;
    private boolean mIsStarted;
    private long mLastShake = 0;
    private long mLastShakeTimestamp = 0;
    private long mLastUpdate = 0;
    private float mLastX = 0.0f;
    private float mLastY = 0.0f;
    private float mLastZ = 0.0f;
    private Sensor mSensor;
    private SensorManager mSensorManager;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public RageShake(Context context) {
        this.mContext = context;
        this.mSensorManager = (SensorManager) context.getSystemService("sensor");
        if (this.mSensorManager != null) {
            this.mSensor = this.mSensorManager.getDefaultSensor(1);
        }
        if (this.mSensor == null) {
            Log.m211e(LOG_TAG, "No accelerometer in this device. Cannot use rage shake.");
            this.mSensorManager = null;
        }
        String trim = Build.MODEL.trim();
        if ("GT-I9300".equals(trim) || "GT-I9000B".equals(trim) || "GT-S5300B".equals(trim)) {
            mThreshold = 20.0f;
        }
    }

    private void promptForReport() {
        if (VectorApp.getCurrentActivity() != null) {
            try {
                new Builder(VectorApp.getCurrentActivity()).setMessage(C1299R.string.send_bug_report_alert_message).setPositiveButton(C1299R.string.yes, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        BugReporter.sendBugReport();
                    }
                }).setNeutralButton(C1299R.string.disable, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PreferencesManager.setUseRageshake(RageShake.this.mContext, false);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(C1299R.string.f114no, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("promptForReport ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void start() {
        if (this.mSensorManager != null && PreferencesManager.useRageshake(this.mContext) && !VectorApp.isAppInBackground() && !this.mIsStarted) {
            this.mIsStarted = true;
            this.mLastUpdate = 0;
            this.mLastShake = 0;
            this.mSensorManager.registerListener(this, this.mSensor, 3);
        }
    }

    public void stop() {
        if (this.mSensorManager != null) {
            this.mSensorManager.unregisterListener(this, this.mSensor);
        }
        this.mIsStarted = false;
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            long j = sensorEvent.timestamp;
            float f = sensorEvent.values[0];
            float f2 = sensorEvent.values[1];
            float f3 = sensorEvent.values[2];
            if (this.mLastUpdate == 0) {
                this.mLastUpdate = j;
                this.mLastShake = j;
                this.mLastX = f;
                this.mLastY = f2;
                this.mLastZ = f3;
            } else if (j - this.mLastUpdate > 0) {
                if (Float.compare(Math.abs(((((f + f2) + f3) - this.mLastX) - this.mLastY) - this.mLastZ), mThreshold) > 0) {
                    if (j - this.mLastShake < mIntervalNanos || System.currentTimeMillis() - this.mLastShakeTimestamp <= 10000) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Suppress shaking - not passed interval. Ms to go: ");
                        sb.append(10000 - (System.currentTimeMillis() - this.mLastShakeTimestamp));
                        sb.append(" ms");
                        Log.m209d(str, sb.toString());
                    } else {
                        Log.m209d(LOG_TAG, "Shaking detected.");
                        this.mLastShakeTimestamp = System.currentTimeMillis();
                        if (PreferencesManager.useRageshake(this.mContext)) {
                            promptForReport();
                        }
                    }
                    this.mLastShake = j;
                }
                this.mLastX = f;
                this.mLastY = f2;
                this.mLastZ = f3;
                this.mLastUpdate = j;
            }
        }
    }
}
