package com.google.android.gms.tagmanager;

import android.os.Build.VERSION;
import com.google.android.gms.internal.measurement.zza;
import com.google.android.gms.internal.measurement.zzp;
import java.util.Map;

final class zzfk extends zzbq {

    /* renamed from: ID */
    private static final String f6696ID = zza.SDK_VERSION.toString();

    public zzfk() {
        super(f6696ID, new String[0]);
    }

    public final boolean zznk() {
        return true;
    }

    public final zzp zzc(Map<String, zzp> map) {
        return zzgj.zzj(Integer.valueOf(VERSION.SDK_INT));
    }
}
