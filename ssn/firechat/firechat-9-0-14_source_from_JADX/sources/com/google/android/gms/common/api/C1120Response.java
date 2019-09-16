package com.google.android.gms.common.api;

import android.support.annotation.NonNull;
import com.google.android.gms.common.api.Result;

/* renamed from: com.google.android.gms.common.api.Response */
public class C1120Response<T extends Result> {
    private T zzdm;

    public C1120Response() {
    }

    protected C1120Response(@NonNull T t) {
        this.zzdm = t;
    }

    /* access modifiers changed from: protected */
    @NonNull
    public T getResult() {
        return this.zzdm;
    }

    public void setResult(@NonNull T t) {
        this.zzdm = t;
    }
}
