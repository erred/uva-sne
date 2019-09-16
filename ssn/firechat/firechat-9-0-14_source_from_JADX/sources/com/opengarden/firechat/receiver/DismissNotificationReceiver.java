package com.opengarden.firechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.services.EventStreamService;

public class DismissNotificationReceiver extends BroadcastReceiver {
    private static final String DISMISS_NOTIFICATIONS_TS_KEY = "DISMISS_NOTIFICATIONS_TS_KEY";
    private static final String LATEST_NOTIFIED_MESSAGE_TS_KEY = "LATEST_NOTIFIED_MESSAGE_TS_KEY";

    public void onReceive(Context context, Intent intent) {
        setNotificationDismissTs(context, getLatestNotifiedMessageTs(context));
        EventStreamService.onMessagesNotificationDismiss(Matrix.getInstance(context).getDefaultSession().getMyUserId());
    }

    public static long getNotificationDismissTs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(DISMISS_NOTIFICATIONS_TS_KEY, 0);
    }

    private static void setNotificationDismissTs(Context context, long j) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(DISMISS_NOTIFICATIONS_TS_KEY, j).apply();
    }

    private static long getLatestNotifiedMessageTs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LATEST_NOTIFIED_MESSAGE_TS_KEY, 0);
    }

    public static void setLatestNotifiedMessageTs(Context context, long j) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(LATEST_NOTIFIED_MESSAGE_TS_KEY, j).apply();
    }
}
