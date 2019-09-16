package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgc extends zzza<zzgc> {
    private static volatile zzgc[] zzawk;
    public String value;
    public String zzoj;

    public static zzgc[] zzmn() {
        if (zzawk == null) {
            synchronized (zzze.zzcfl) {
                if (zzawk == null) {
                    zzawk = new zzgc[0];
                }
            }
        }
        return zzawk;
    }

    public zzgc() {
        this.zzoj = null;
        this.value = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgc)) {
            return false;
        }
        zzgc zzgc = (zzgc) obj;
        if (this.zzoj == null) {
            if (zzgc.zzoj != null) {
                return false;
            }
        } else if (!this.zzoj.equals(zzgc.zzoj)) {
            return false;
        }
        if (this.value == null) {
            if (zzgc.value != null) {
                return false;
            }
        } else if (!this.value.equals(zzgc.value)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgc.zzcfc == null || zzgc.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgc.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + (this.zzoj == null ? 0 : this.zzoj.hashCode())) * 31) + (this.value == null ? 0 : this.value.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzoj != null) {
            zzyy.zzb(1, this.zzoj);
        }
        if (this.value != null) {
            zzyy.zzb(2, this.value);
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzoj != null) {
            zzf += zzyy.zzc(1, this.zzoj);
        }
        return this.value != null ? zzf + zzyy.zzc(2, this.value) : zzf;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 10) {
                this.zzoj = zzyx.readString();
            } else if (zzug == 18) {
                this.value = zzyx.readString();
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
