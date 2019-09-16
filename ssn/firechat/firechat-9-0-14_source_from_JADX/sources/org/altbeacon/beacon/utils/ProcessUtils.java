package org.altbeacon.beacon.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Process;
import android.support.annotation.NonNull;

public class ProcessUtils {
    Context mContext;

    public ProcessUtils(@NonNull Context context) {
        this.mContext = context;
    }

    public String getProcessName() {
        for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) this.mContext.getSystemService("activity")).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == getPid()) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }

    public String getPackageName() {
        return this.mContext.getApplicationContext().getPackageName();
    }

    public int getPid() {
        return Process.myPid();
    }

    public boolean isMainProcess() {
        return getPackageName().equals(getProcessName());
    }
}
