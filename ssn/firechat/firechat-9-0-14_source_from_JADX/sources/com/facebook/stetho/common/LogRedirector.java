package com.facebook.stetho.common;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.lang3.StringUtils;

public class LogRedirector {
    private static volatile Logger sLogger;

    public interface Logger {
        boolean isLoggable(String str, int i);

        void log(int i, String str, String str2);
    }

    public static void setLogger(Logger logger) {
        Util.throwIfNull(logger);
        Util.throwIfNotNull(sLogger);
        sLogger = logger;
    }

    /* renamed from: e */
    public static void m150e(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(StringUtils.f158LF);
        sb.append(formatThrowable(th));
        m149e(str, sb.toString());
    }

    /* renamed from: e */
    public static void m149e(String str, String str2) {
        log(6, str, str2);
    }

    /* renamed from: w */
    public static void m156w(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(StringUtils.f158LF);
        sb.append(formatThrowable(th));
        m155w(str, sb.toString());
    }

    /* renamed from: w */
    public static void m155w(String str, String str2) {
        log(5, str, str2);
    }

    /* renamed from: i */
    public static void m152i(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(StringUtils.f158LF);
        sb.append(formatThrowable(th));
        m151i(str, sb.toString());
    }

    /* renamed from: i */
    public static void m151i(String str, String str2) {
        log(4, str, str2);
    }

    /* renamed from: d */
    public static void m148d(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(StringUtils.f158LF);
        sb.append(formatThrowable(th));
        m147d(str, sb.toString());
    }

    /* renamed from: d */
    public static void m147d(String str, String str2) {
        log(3, str, str2);
    }

    /* renamed from: v */
    public static void m154v(String str, String str2, Throwable th) {
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append(StringUtils.f158LF);
        sb.append(formatThrowable(th));
        m153v(str, sb.toString());
    }

    /* renamed from: v */
    public static void m153v(String str, String str2) {
        log(2, str, str2);
    }

    private static String formatThrowable(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace();
        printWriter.flush();
        return stringWriter.toString();
    }

    private static void log(int i, String str, String str2) {
        Logger logger = sLogger;
        if (logger != null) {
            logger.log(i, str, str2);
        } else {
            Log.println(i, str, str2);
        }
    }

    public static boolean isLoggable(String str, int i) {
        Logger logger = sLogger;
        if (logger != null) {
            return logger.isLoggable(str, i);
        }
        return Log.isLoggable(str, i);
    }
}
