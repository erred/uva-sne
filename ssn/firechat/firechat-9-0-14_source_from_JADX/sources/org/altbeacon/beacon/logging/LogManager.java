package org.altbeacon.beacon.logging;

public final class LogManager {
    private static Logger sLogger = Loggers.infoLogger();
    private static boolean sVerboseLoggingEnabled = false;

    public static void setLogger(Logger logger) {
        if (logger == null) {
            throw new NullPointerException("Logger may not be null.");
        }
        sLogger = logger;
    }

    public static Logger getLogger() {
        return sLogger;
    }

    public static boolean isVerboseLoggingEnabled() {
        return sVerboseLoggingEnabled;
    }

    public static void setVerboseLoggingEnabled(boolean z) {
        sVerboseLoggingEnabled = z;
    }

    /* renamed from: v */
    public static void m266v(String str, String str2, Object... objArr) {
        sLogger.mo26221v(str, str2, objArr);
    }

    /* renamed from: v */
    public static void m267v(Throwable th, String str, String str2, Object... objArr) {
        sLogger.mo26222v(th, str, str2, objArr);
    }

    /* renamed from: d */
    public static void m260d(String str, String str2, Object... objArr) {
        sLogger.mo26215d(str, str2, objArr);
    }

    /* renamed from: d */
    public static void m261d(Throwable th, String str, String str2, Object... objArr) {
        sLogger.mo26216d(th, str, str2, objArr);
    }

    /* renamed from: i */
    public static void m264i(String str, String str2, Object... objArr) {
        sLogger.mo26219i(str, str2, objArr);
    }

    /* renamed from: i */
    public static void m265i(Throwable th, String str, String str2, Object... objArr) {
        sLogger.mo26220i(th, str, str2, objArr);
    }

    /* renamed from: w */
    public static void m268w(String str, String str2, Object... objArr) {
        sLogger.mo26223w(str, str2, objArr);
    }

    /* renamed from: w */
    public static void m269w(Throwable th, String str, String str2, Object... objArr) {
        sLogger.mo26224w(th, str, str2, objArr);
    }

    /* renamed from: e */
    public static void m262e(String str, String str2, Object... objArr) {
        sLogger.mo26217e(str, str2, objArr);
    }

    /* renamed from: e */
    public static void m263e(Throwable th, String str, String str2, Object... objArr) {
        sLogger.mo26218e(th, str, str2, objArr);
    }

    private LogManager() {
    }
}
