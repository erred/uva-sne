package org.altbeacon.beacon.logging;

import android.util.Log;

final class VerboseAndroidLogger extends AbstractAndroidLogger {
    VerboseAndroidLogger() {
    }

    /* renamed from: v */
    public void mo26221v(String str, String str2, Object... objArr) {
        Log.v(str, formatString(str2, objArr));
    }

    /* renamed from: v */
    public void mo26222v(Throwable th, String str, String str2, Object... objArr) {
        Log.v(str, formatString(str2, objArr), th);
    }

    /* renamed from: d */
    public void mo26215d(String str, String str2, Object... objArr) {
        Log.d(str, formatString(str2, objArr));
    }

    /* renamed from: d */
    public void mo26216d(Throwable th, String str, String str2, Object... objArr) {
        Log.d(str, formatString(str2, objArr), th);
    }

    /* renamed from: i */
    public void mo26219i(String str, String str2, Object... objArr) {
        Log.i(str, formatString(str2, objArr));
    }

    /* renamed from: i */
    public void mo26220i(Throwable th, String str, String str2, Object... objArr) {
        Log.i(str, formatString(str2, objArr), th);
    }

    /* renamed from: w */
    public void mo26223w(String str, String str2, Object... objArr) {
        Log.w(str, formatString(str2, objArr));
    }

    /* renamed from: w */
    public void mo26224w(Throwable th, String str, String str2, Object... objArr) {
        Log.w(str, formatString(str2, objArr), th);
    }

    /* renamed from: e */
    public void mo26217e(String str, String str2, Object... objArr) {
        Log.e(str, formatString(str2, objArr));
    }

    /* renamed from: e */
    public void mo26218e(Throwable th, String str, String str2, Object... objArr) {
        Log.e(str, formatString(str2, objArr), th);
    }
}
