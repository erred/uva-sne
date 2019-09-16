package com.amplitude.api;

import android.util.Log;

public class AmplitudeLog {
    protected static AmplitudeLog instance = new AmplitudeLog();
    private volatile boolean enableLogging = true;
    private volatile int logLevel = 4;

    public static AmplitudeLog getLogger() {
        return instance;
    }

    private AmplitudeLog() {
    }

    /* access modifiers changed from: 0000 */
    public AmplitudeLog setEnableLogging(boolean z) {
        this.enableLogging = z;
        return instance;
    }

    /* access modifiers changed from: 0000 */
    public AmplitudeLog setLogLevel(int i) {
        this.logLevel = i;
        return instance;
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: d */
    public int mo9076d(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 3) {
            return 0;
        }
        return Log.d(str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: d */
    public int mo9077d(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 3) {
            return 0;
        }
        return Log.d(str, str2, th);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: e */
    public int mo9078e(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 6) {
            return 0;
        }
        return Log.e(str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: e */
    public int mo9079e(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 6) {
            return 0;
        }
        return Log.e(str, str2, th);
    }

    /* access modifiers changed from: 0000 */
    public String getStackTraceString(Throwable th) {
        return Log.getStackTraceString(th);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: i */
    public int mo9081i(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 4) {
            return 0;
        }
        return Log.i(str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: i */
    public int mo9082i(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 4) {
            return 0;
        }
        return Log.i(str, str2, th);
    }

    /* access modifiers changed from: 0000 */
    public boolean isLoggable(String str, int i) {
        return Log.isLoggable(str, i);
    }

    /* access modifiers changed from: 0000 */
    public int println(int i, String str, String str2) {
        return Log.println(i, str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: v */
    public int mo9087v(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 2) {
            return 0;
        }
        return Log.v(str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: v */
    public int mo9088v(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 2) {
            return 0;
        }
        return Log.v(str, str2, th);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: w */
    public int mo9089w(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 5) {
            return 0;
        }
        return Log.w(str, str2);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: w */
    public int mo9091w(String str, Throwable th) {
        if (!this.enableLogging || this.logLevel > 5) {
            return 0;
        }
        return Log.w(str, th);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: w */
    public int mo9090w(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 5) {
            return 0;
        }
        return Log.w(str, str2, th);
    }

    /* access modifiers changed from: 0000 */
    public int wtf(String str, String str2) {
        if (!this.enableLogging || this.logLevel > 7) {
            return 0;
        }
        return Log.wtf(str, str2);
    }

    /* access modifiers changed from: 0000 */
    public int wtf(String str, Throwable th) {
        if (!this.enableLogging || this.logLevel > 7) {
            return 0;
        }
        return Log.wtf(str, th);
    }

    /* access modifiers changed from: 0000 */
    public int wtf(String str, String str2, Throwable th) {
        if (!this.enableLogging || this.logLevel > 7) {
            return 0;
        }
        return Log.wtf(str, str2, th);
    }
}
