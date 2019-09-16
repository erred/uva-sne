package com.facebook.react.modules.systeminfo;

import android.os.Build;
import android.os.Build.VERSION;
import java.util.Locale;

public class AndroidInfoHelpers {
    private static final int DEBUG_SERVER_HOST_PORT = 8081;
    public static final String DEVICE_LOCALHOST = "localhost";
    public static final String EMULATOR_LOCALHOST = "10.0.2.2";
    public static final String GENYMOTION_LOCALHOST = "10.0.3.2";
    private static final int INSPECTOR_PROXY_PORT = 8082;

    private static boolean isRunningOnGenymotion() {
        return Build.FINGERPRINT.contains("vbox");
    }

    private static boolean isRunningOnStockEmulator() {
        return Build.FINGERPRINT.contains("generic");
    }

    public static String getServerHost() {
        return getServerIpAddress(DEBUG_SERVER_HOST_PORT);
    }

    public static String getInspectorProxyHost() {
        return getServerIpAddress(INSPECTOR_PROXY_PORT);
    }

    public static String getFriendlyDeviceName() {
        if (isRunningOnGenymotion()) {
            return Build.MODEL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Build.MODEL);
        sb.append(" - ");
        sb.append(VERSION.RELEASE);
        sb.append(" - API ");
        sb.append(VERSION.SDK_INT);
        return sb.toString();
    }

    private static String getServerIpAddress(int i) {
        String str = isRunningOnGenymotion() ? GENYMOTION_LOCALHOST : isRunningOnStockEmulator() ? EMULATOR_LOCALHOST : DEVICE_LOCALHOST;
        return String.format(Locale.US, "%s:%d", new Object[]{str, Integer.valueOf(i)});
    }
}
