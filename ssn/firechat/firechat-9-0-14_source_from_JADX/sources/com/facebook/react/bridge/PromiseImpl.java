package com.facebook.react.bridge;

import javax.annotation.Nullable;

public class PromiseImpl implements Promise {
    private static final String DEFAULT_ERROR = "EUNSPECIFIED";
    @Nullable
    private Callback mReject;
    @Nullable
    private Callback mResolve;

    public PromiseImpl(@Nullable Callback callback, @Nullable Callback callback2) {
        this.mResolve = callback;
        this.mReject = callback2;
    }

    public void resolve(Object obj) {
        if (this.mResolve != null) {
            this.mResolve.invoke(obj);
        }
    }

    public void reject(String str, String str2) {
        reject(str, str2, null);
    }

    @Deprecated
    public void reject(String str) {
        reject(DEFAULT_ERROR, str, null);
    }

    public void reject(String str, Throwable th) {
        reject(str, th.getMessage(), th);
    }

    public void reject(Throwable th) {
        reject(DEFAULT_ERROR, th.getMessage(), th);
    }

    public void reject(String str, String str2, @Nullable Throwable th) {
        if (this.mReject != null) {
            if (str == null) {
                str = DEFAULT_ERROR;
            }
            WritableNativeMap writableNativeMap = new WritableNativeMap();
            writableNativeMap.putString("code", str);
            writableNativeMap.putString("message", str2);
            this.mReject.invoke(writableNativeMap);
        }
    }
}
