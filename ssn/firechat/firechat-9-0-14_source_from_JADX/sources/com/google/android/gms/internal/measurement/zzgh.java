package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgh extends zzza<zzgh> {
    public zzgi[] zzawy;

    public zzgh() {
        this.zzawy = zzgi.zzms();
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgh)) {
            return false;
        }
        zzgh zzgh = (zzgh) obj;
        if (!zzze.equals((Object[]) this.zzawy, (Object[]) zzgh.zzawy)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgh.zzcfc == null || zzgh.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgh.zzcfc);
    }

    public final int hashCode() {
        return ((((getClass().getName().hashCode() + 527) * 31) + zzze.hashCode((Object[]) this.zzawy)) * 31) + ((this.zzcfc == null || this.zzcfc.isEmpty()) ? 0 : this.zzcfc.hashCode());
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzawy != null && this.zzawy.length > 0) {
            for (zzgi zzgi : this.zzawy) {
                if (zzgi != null) {
                    zzyy.zza(1, (zzzg) zzgi);
                }
            }
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzawy != null && this.zzawy.length > 0) {
            for (zzgi zzgi : this.zzawy) {
                if (zzgi != null) {
                    zzf += zzyy.zzb(1, (zzzg) zzgi);
                }
            }
        }
        return zzf;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 10) {
                int zzb = zzzj.zzb(zzyx, 10);
                int length = this.zzawy == null ? 0 : this.zzawy.length;
                zzgi[] zzgiArr = new zzgi[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzawy, 0, zzgiArr, 0, length);
                }
                while (length < zzgiArr.length - 1) {
                    zzgiArr[length] = new zzgi();
                    zzyx.zza((zzzg) zzgiArr[length]);
                    zzyx.zzug();
                    length++;
                }
                zzgiArr[length] = new zzgi();
                zzyx.zza((zzzg) zzgiArr[length]);
                this.zzawy = zzgiArr;
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
