package org.altbeacon.beacon.powersave;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.NonNull;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.logging.LogManager;

@TargetApi(18)
public class BackgroundPowerSaver implements ActivityLifecycleCallbacks {
    @NonNull
    private static final String TAG = "BackgroundPowerSaver";
    private int activeActivityCount;
    @NonNull
    private final BeaconManager beaconManager;

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
    }

    @Deprecated
    public BackgroundPowerSaver(Context context, boolean z) {
        this(context);
    }

    public BackgroundPowerSaver(Context context) {
        this.activeActivityCount = 0;
        if (VERSION.SDK_INT < 18) {
            LogManager.m268w(TAG, "BackgroundPowerSaver requires API 18 or higher.", new Object[0]);
        }
        this.beaconManager = BeaconManager.getInstanceForApplication(context);
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
    }

    public void onActivityResumed(Activity activity) {
        this.activeActivityCount++;
        if (this.activeActivityCount < 1) {
            LogManager.m260d(TAG, "reset active activity count on resume.  It was %s", Integer.valueOf(this.activeActivityCount));
            this.activeActivityCount = 1;
        }
        this.beaconManager.setBackgroundMode(false);
        LogManager.m260d(TAG, "activity resumed: %s active activities: %s", activity, Integer.valueOf(this.activeActivityCount));
    }

    public void onActivityPaused(Activity activity) {
        this.activeActivityCount--;
        LogManager.m260d(TAG, "activity paused: %s active activities: %s", activity, Integer.valueOf(this.activeActivityCount));
        if (this.activeActivityCount < 1) {
            LogManager.m260d(TAG, "setting background mode", new Object[0]);
            this.beaconManager.setBackgroundMode(true);
        }
    }
}
