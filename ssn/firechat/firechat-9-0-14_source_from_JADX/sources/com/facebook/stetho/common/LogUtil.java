package com.facebook.stetho.common;

import java.util.Locale;

public class LogUtil {
    private static final String TAG = "stetho";

    /* renamed from: e */
    public static void m162e(String str, Object... objArr) {
        m161e(format(str, objArr));
    }

    /* renamed from: e */
    public static void m164e(Throwable th, String str, Object... objArr) {
        m163e(th, format(str, objArr));
    }

    /* renamed from: e */
    public static void m161e(String str) {
        if (isLoggable(6)) {
            LogRedirector.m149e(TAG, str);
        }
    }

    /* renamed from: e */
    public static void m163e(Throwable th, String str) {
        if (isLoggable(6)) {
            LogRedirector.m150e(TAG, str, th);
        }
    }

    /* renamed from: w */
    public static void m174w(String str, Object... objArr) {
        m173w(format(str, objArr));
    }

    /* renamed from: w */
    public static void m176w(Throwable th, String str, Object... objArr) {
        m175w(th, format(str, objArr));
    }

    /* renamed from: w */
    public static void m173w(String str) {
        if (isLoggable(5)) {
            LogRedirector.m155w(TAG, str);
        }
    }

    /* renamed from: w */
    public static void m175w(Throwable th, String str) {
        if (isLoggable(5)) {
            LogRedirector.m156w(TAG, str, th);
        }
    }

    /* renamed from: i */
    public static void m166i(String str, Object... objArr) {
        m165i(format(str, objArr));
    }

    /* renamed from: i */
    public static void m168i(Throwable th, String str, Object... objArr) {
        m167i(th, format(str, objArr));
    }

    /* renamed from: i */
    public static void m165i(String str) {
        if (isLoggable(4)) {
            LogRedirector.m151i(TAG, str);
        }
    }

    /* renamed from: i */
    public static void m167i(Throwable th, String str) {
        if (isLoggable(4)) {
            LogRedirector.m152i(TAG, str, th);
        }
    }

    /* renamed from: d */
    public static void m158d(String str, Object... objArr) {
        m157d(format(str, objArr));
    }

    /* renamed from: d */
    public static void m160d(Throwable th, String str, Object... objArr) {
        m159d(th, format(str, objArr));
    }

    /* renamed from: d */
    public static void m157d(String str) {
        if (isLoggable(3)) {
            LogRedirector.m147d(TAG, str);
        }
    }

    /* renamed from: d */
    public static void m159d(Throwable th, String str) {
        if (isLoggable(3)) {
            LogRedirector.m148d(TAG, str, th);
        }
    }

    /* renamed from: v */
    public static void m170v(String str, Object... objArr) {
        m169v(format(str, objArr));
    }

    /* renamed from: v */
    public static void m172v(Throwable th, String str, Object... objArr) {
        m171v(th, format(str, objArr));
    }

    /* renamed from: v */
    public static void m169v(String str) {
        if (isLoggable(2)) {
            LogRedirector.m153v(TAG, str);
        }
    }

    /* renamed from: v */
    public static void m171v(Throwable th, String str) {
        if (isLoggable(2)) {
            LogRedirector.m154v(TAG, str, th);
        }
    }

    private static String format(String str, Object... objArr) {
        return String.format(Locale.US, str, objArr);
    }

    public static boolean isLoggable(int i) {
        switch (i) {
            case 5:
            case 6:
                return true;
            default:
                return LogRedirector.isLoggable(TAG, i);
        }
    }
}
