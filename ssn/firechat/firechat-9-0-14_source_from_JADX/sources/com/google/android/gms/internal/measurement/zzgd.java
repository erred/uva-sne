package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgd extends zzza<zzgd> {
    private static volatile zzgd[] zzawl;
    public Integer zzauy;
    public zzgj zzawm;
    public zzgj zzawn;
    public Boolean zzawo;

    public static zzgd[] zzmo() {
        if (zzawl == null) {
            synchronized (zzze.zzcfl) {
                if (zzawl == null) {
                    zzawl = new zzgd[0];
                }
            }
        }
        return zzawl;
    }

    public zzgd() {
        this.zzauy = null;
        this.zzawm = null;
        this.zzawn = null;
        this.zzawo = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgd)) {
            return false;
        }
        zzgd zzgd = (zzgd) obj;
        if (this.zzauy == null) {
            if (zzgd.zzauy != null) {
                return false;
            }
        } else if (!this.zzauy.equals(zzgd.zzauy)) {
            return false;
        }
        if (this.zzawm == null) {
            if (zzgd.zzawm != null) {
                return false;
            }
        } else if (!this.zzawm.equals(zzgd.zzawm)) {
            return false;
        }
        if (this.zzawn == null) {
            if (zzgd.zzawn != null) {
                return false;
            }
        } else if (!this.zzawn.equals(zzgd.zzawn)) {
            return false;
        }
        if (this.zzawo == null) {
            if (zzgd.zzawo != null) {
                return false;
            }
        } else if (!this.zzawo.equals(zzgd.zzawo)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgd.zzcfc == null || zzgd.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgd.zzcfc);
    }

    public final int hashCode() {
        int i;
        int i2;
        int i3 = 0;
        int hashCode = ((getClass().getName().hashCode() + 527) * 31) + (this.zzauy == null ? 0 : this.zzauy.hashCode());
        zzgj zzgj = this.zzawm;
        int i4 = hashCode * 31;
        if (zzgj == null) {
            i = 0;
        } else {
            i = zzgj.hashCode();
        }
        int i5 = i4 + i;
        zzgj zzgj2 = this.zzawn;
        int i6 = i5 * 31;
        if (zzgj2 == null) {
            i2 = 0;
        } else {
            i2 = zzgj2.hashCode();
        }
        int hashCode2 = (((i6 + i2) * 31) + (this.zzawo == null ? 0 : this.zzawo.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i3 = this.zzcfc.hashCode();
        }
        return hashCode2 + i3;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzauy != null) {
            zzyy.zzd(1, this.zzauy.intValue());
        }
        if (this.zzawm != null) {
            zzyy.zza(2, (zzzg) this.zzawm);
        }
        if (this.zzawn != null) {
            zzyy.zza(3, (zzzg) this.zzawn);
        }
        if (this.zzawo != null) {
            zzyy.zzb(4, this.zzawo.booleanValue());
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzauy != null) {
            zzf += zzyy.zzh(1, this.zzauy.intValue());
        }
        if (this.zzawm != null) {
            zzf += zzyy.zzb(2, (zzzg) this.zzawm);
        }
        if (this.zzawn != null) {
            zzf += zzyy.zzb(3, (zzzg) this.zzawn);
        }
        if (this.zzawo == null) {
            return zzf;
        }
        this.zzawo.booleanValue();
        return zzf + zzyy.zzbb(4) + 1;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                this.zzauy = Integer.valueOf(zzyx.zzuy());
            } else if (zzug == 18) {
                if (this.zzawm == null) {
                    this.zzawm = new zzgj();
                }
                zzyx.zza((zzzg) this.zzawm);
            } else if (zzug == 26) {
                if (this.zzawn == null) {
                    this.zzawn = new zzgj();
                }
                zzyx.zza((zzzg) this.zzawn);
            } else if (zzug == 32) {
                this.zzawo = Boolean.valueOf(zzyx.zzum());
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
