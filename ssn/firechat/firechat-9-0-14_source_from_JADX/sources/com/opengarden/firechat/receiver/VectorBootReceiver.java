package com.opengarden.firechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.PreferencesManager;

public class VectorBootReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "VectorBootReceiver";

    public void onReceive(Context context, Intent intent) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onReceive() : ");
        sb.append(intent.getAction());
        Log.m209d(str, sb.toString());
        if (!TextUtils.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            return;
        }
        if (PreferencesManager.autoStartOnBoot(context)) {
            Log.m209d(LOG_TAG, "## onReceive() : starts the application");
            CommonActivityUtils.startEventStreamService(context);
            return;
        }
        Log.m209d(LOG_TAG, "## onReceive() : the autostart is disabled");
    }
}
