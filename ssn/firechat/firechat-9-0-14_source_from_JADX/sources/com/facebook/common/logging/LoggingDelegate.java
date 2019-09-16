package com.facebook.common.logging;

public interface LoggingDelegate {
    /* renamed from: d */
    void mo10543d(String str, String str2);

    /* renamed from: d */
    void mo10544d(String str, String str2, Throwable th);

    /* renamed from: e */
    void mo10545e(String str, String str2);

    /* renamed from: e */
    void mo10546e(String str, String str2, Throwable th);

    int getMinimumLoggingLevel();

    /* renamed from: i */
    void mo10548i(String str, String str2);

    /* renamed from: i */
    void mo10549i(String str, String str2, Throwable th);

    boolean isLoggable(int i);

    void log(int i, String str, String str2);

    void setMinimumLoggingLevel(int i);

    /* renamed from: v */
    void mo10554v(String str, String str2);

    /* renamed from: v */
    void mo10555v(String str, String str2, Throwable th);

    /* renamed from: w */
    void mo10556w(String str, String str2);

    /* renamed from: w */
    void mo10557w(String str, String str2, Throwable th);

    void wtf(String str, String str2);

    void wtf(String str, String str2, Throwable th);
}
