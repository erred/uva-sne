package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgk extends zzza<zzgk> {
    private static volatile zzgk[] zzayi;
    public Integer zzawq;
    public long[] zzayj;

    public static zzgk[] zzmt() {
        if (zzayi == null) {
            synchronized (zzze.zzcfl) {
                if (zzayi == null) {
                    zzayi = new zzgk[0];
                }
            }
        }
        return zzayi;
    }

    public zzgk() {
        this.zzawq = null;
        this.zzayj = zzzj.zzcfr;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgk)) {
            return false;
        }
        zzgk zzgk = (zzgk) obj;
        if (this.zzawq == null) {
            if (zzgk.zzawq != null) {
                return false;
            }
        } else if (!this.zzawq.equals(zzgk.zzawq)) {
            return false;
        }
        if (!zzze.equals(this.zzayj, zzgk.zzayj)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgk.zzcfc == null || zzgk.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgk.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((getClass().getName().hashCode() + 527) * 31) + (this.zzawq == null ? 0 : this.zzawq.hashCode())) * 31) + zzze.hashCode(this.zzayj)) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzawq != null) {
            zzyy.zzd(1, this.zzawq.intValue());
        }
        if (this.zzayj != null && this.zzayj.length > 0) {
            for (long zzi : this.zzayj) {
                zzyy.zzi(2, zzi);
            }
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzawq != null) {
            zzf += zzyy.zzh(1, this.zzawq.intValue());
        }
        if (this.zzayj == null || this.zzayj.length <= 0) {
            return zzf;
        }
        int i = 0;
        for (long zzbi : this.zzayj) {
            i += zzyy.zzbi(zzbi);
        }
        return zzf + i + (this.zzayj.length * 1);
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                this.zzawq = Integer.valueOf(zzyx.zzuy());
            } else if (zzug == 16) {
                int zzb = zzzj.zzb(zzyx, 16);
                int length = this.zzayj == null ? 0 : this.zzayj.length;
                long[] jArr = new long[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzayj, 0, jArr, 0, length);
                }
                while (length < jArr.length - 1) {
                    jArr[length] = zzyx.zzuz();
                    zzyx.zzug();
                    length++;
                }
                jArr[length] = zzyx.zzuz();
                this.zzayj = jArr;
            } else if (zzug == 18) {
                int zzaq = zzyx.zzaq(zzyx.zzuy());
                int position = zzyx.getPosition();
                int i = 0;
                while (zzyx.zzyr() > 0) {
                    zzyx.zzuz();
                    i++;
                }
                zzyx.zzby(position);
                int length2 = this.zzayj == null ? 0 : this.zzayj.length;
                long[] jArr2 = new long[(i + length2)];
                if (length2 != 0) {
                    System.arraycopy(this.zzayj, 0, jArr2, 0, length2);
                }
                while (length2 < jArr2.length) {
                    jArr2[length2] = zzyx.zzuz();
                    length2++;
                }
                this.zzayj = jArr2;
                zzyx.zzar(zzaq);
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
