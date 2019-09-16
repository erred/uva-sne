package org.webrtc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    private static final Logger fallbackLogger = Logger.getLogger("org.webrtc.Logging");
    private static volatile boolean loggingEnabled;
    private static volatile boolean nativeLibLoaded = true;
    private static volatile boolean tracingEnabled;

    public enum Severity {
        LS_SENSITIVE,
        LS_VERBOSE,
        LS_INFO,
        LS_WARNING,
        LS_ERROR,
        LS_NONE
    }

    public enum TraceLevel {
        TRACE_NONE(0),
        TRACE_STATEINFO(1),
        TRACE_WARNING(2),
        TRACE_ERROR(4),
        TRACE_CRITICAL(8),
        TRACE_APICALL(16),
        TRACE_DEFAULT(255),
        TRACE_MODULECALL(32),
        TRACE_MEMORY(256),
        TRACE_TIMER(512),
        TRACE_STREAM(1024),
        TRACE_DEBUG(2048),
        TRACE_INFO(4096),
        TRACE_TERSEINFO(8192),
        TRACE_ALL(65535);
        
        public final int level;

        private TraceLevel(int i) {
            this.level = i;
        }
    }

    private static native void nativeEnableLogThreads();

    private static native void nativeEnableLogTimeStamps();

    private static native void nativeEnableLogToDebugOutput(int i);

    private static native void nativeEnableTracing(String str, int i);

    private static native void nativeLog(int i, String str, String str2);

    static {
        try {
            System.loadLibrary("jingle_peerconnection_so");
        } catch (UnsatisfiedLinkError e) {
            fallbackLogger.setLevel(Level.ALL);
            fallbackLogger.log(Level.WARNING, "Failed to load jingle_peerconnection_so: ", e);
        }
    }

    public static void enableLogThreads() {
        if (!nativeLibLoaded) {
            fallbackLogger.log(Level.WARNING, "Cannot enable log thread because native lib not loaded.");
        } else {
            nativeEnableLogThreads();
        }
    }

    public static void enableLogTimeStamps() {
        if (!nativeLibLoaded) {
            fallbackLogger.log(Level.WARNING, "Cannot enable log timestamps because native lib not loaded.");
        } else {
            nativeEnableLogTimeStamps();
        }
    }

    public static synchronized void enableTracing(String str, EnumSet<TraceLevel> enumSet) {
        synchronized (Logging.class) {
            if (!nativeLibLoaded) {
                fallbackLogger.log(Level.WARNING, "Cannot enable tracing because native lib not loaded.");
            } else if (!tracingEnabled) {
                int i = 0;
                Iterator it = enumSet.iterator();
                while (it.hasNext()) {
                    i |= ((TraceLevel) it.next()).level;
                }
                nativeEnableTracing(str, i);
                tracingEnabled = true;
            }
        }
    }

    public static synchronized void enableLogToDebugOutput(Severity severity) {
        synchronized (Logging.class) {
            if (!nativeLibLoaded) {
                fallbackLogger.log(Level.WARNING, "Cannot enable logging because native lib not loaded.");
                return;
            }
            nativeEnableLogToDebugOutput(severity.ordinal());
            loggingEnabled = true;
        }
    }

    public static void log(Severity severity, String str, String str2) {
        Level level;
        if (loggingEnabled) {
            nativeLog(severity.ordinal(), str, str2);
            return;
        }
        switch (severity) {
            case LS_ERROR:
                level = Level.SEVERE;
                break;
            case LS_WARNING:
                level = Level.WARNING;
                break;
            case LS_INFO:
                level = Level.INFO;
                break;
            default:
                level = Level.FINE;
                break;
        }
        Logger logger = fallbackLogger;
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(": ");
        sb.append(str2);
        logger.log(level, sb.toString());
    }

    /* renamed from: d */
    public static void m314d(String str, String str2) {
        log(Severity.LS_INFO, str, str2);
    }

    /* renamed from: e */
    public static void m315e(String str, String str2) {
        log(Severity.LS_ERROR, str, str2);
    }

    /* renamed from: w */
    public static void m318w(String str, String str2) {
        log(Severity.LS_WARNING, str, str2);
    }

    /* renamed from: e */
    public static void m316e(String str, String str2, Throwable th) {
        log(Severity.LS_ERROR, str, str2);
        log(Severity.LS_ERROR, str, th.toString());
        log(Severity.LS_ERROR, str, getStackTraceString(th));
    }

    /* renamed from: w */
    public static void m319w(String str, String str2, Throwable th) {
        log(Severity.LS_WARNING, str, str2);
        log(Severity.LS_WARNING, str, th.toString());
        log(Severity.LS_WARNING, str, getStackTraceString(th));
    }

    /* renamed from: v */
    public static void m317v(String str, String str2) {
        log(Severity.LS_VERBOSE, str, str2);
    }

    private static String getStackTraceString(Throwable th) {
        if (th == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
