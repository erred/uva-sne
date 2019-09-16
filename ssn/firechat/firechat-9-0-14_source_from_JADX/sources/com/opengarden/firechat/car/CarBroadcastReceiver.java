package com.opengarden.firechat.car;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.p000v4.app.RemoteInput;

public class CarBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CarBroadcastReceiver";

    public void onReceive(Context context, Intent intent) {
    }

    private CharSequence getMessageText(Intent intent) {
        RemoteInput.getResultsFromIntent(intent);
        return null;
    }
}
