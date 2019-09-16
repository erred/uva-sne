package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.content.ContextWrapper;
import androidx.fragment.p081a.C1071e;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;

@KeepForSdk
public class LifecycleActivity {
    private final Object zzbd;

    public LifecycleActivity(Activity activity) {
        Preconditions.checkNotNull(activity, "Activity must not be null");
        this.zzbd = activity;
    }

    @KeepForSdk
    public boolean isChimera() {
        return false;
    }

    @KeepForSdk
    public LifecycleActivity(ContextWrapper contextWrapper) {
        throw new UnsupportedOperationException();
    }

    @KeepForSdk
    public boolean isSupport() {
        return this.zzbd instanceof C1071e;
    }

    public final boolean zzh() {
        return this.zzbd instanceof Activity;
    }

    @KeepForSdk
    public Activity asActivity() {
        return (Activity) this.zzbd;
    }

    @KeepForSdk
    public C1071e asFragmentActivity() {
        return (C1071e) this.zzbd;
    }

    @KeepForSdk
    public Object asObject() {
        return this.zzbd;
    }
}
