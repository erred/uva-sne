package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzfu extends zzza<zzfu> {
    private static volatile zzfu[] zzaux;
    public Integer zzauy;
    public zzfy[] zzauz;
    public zzfv[] zzava;
    private Boolean zzavb;
    private Boolean zzavc;

    public static zzfu[] zzmi() {
        if (zzaux == null) {
            synchronized (zzze.zzcfl) {
                if (zzaux == null) {
                    zzaux = new zzfu[0];
                }
            }
        }
        return zzaux;
    }

    public zzfu() {
        this.zzauy = null;
        this.zzauz = zzfy.zzml();
        this.zzava = zzfv.zzmj();
        this.zzavb = null;
        this.zzavc = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfu)) {
            return false;
        }
        zzfu zzfu = (zzfu) obj;
        if (this.zzauy == null) {
            if (zzfu.zzauy != null) {
                return false;
            }
        } else if (!this.zzauy.equals(zzfu.zzauy)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzauz, (Object[]) zzfu.zzauz) || !zzze.equals((Object[]) this.zzava, (Object[]) zzfu.zzava)) {
            return false;
        }
        if (this.zzavb == null) {
            if (zzfu.zzavb != null) {
                return false;
            }
        } else if (!this.zzavb.equals(zzfu.zzavb)) {
            return false;
        }
        if (this.zzavc == null) {
            if (zzfu.zzavc != null) {
                return false;
            }
        } else if (!this.zzavc.equals(zzfu.zzavc)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzfu.zzcfc == null || zzfu.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzfu.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzauy == null ? 0 : this.zzauy.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzauz)) * 31) + zzze.hashCode((Object[]) this.zzava)) * 31) + (this.zzavb == null ? 0 : this.zzavb.hashCode())) * 31) + (this.zzavc == null ? 0 : this.zzavc.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzauy != null) {
            zzyy.zzd(1, this.zzauy.intValue());
        }
        if (this.zzauz != null && this.zzauz.length > 0) {
            for (zzfy zzfy : this.zzauz) {
                if (zzfy != null) {
                    zzyy.zza(2, (zzzg) zzfy);
                }
            }
        }
        if (this.zzava != null && this.zzava.length > 0) {
            for (zzfv zzfv : this.zzava) {
                if (zzfv != null) {
                    zzyy.zza(3, (zzzg) zzfv);
                }
            }
        }
        if (this.zzavb != null) {
            zzyy.zzb(4, this.zzavb.booleanValue());
        }
        if (this.zzavc != null) {
            zzyy.zzb(5, this.zzavc.booleanValue());
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzauy != null) {
            zzf += zzyy.zzh(1, this.zzauy.intValue());
        }
        if (this.zzauz != null && this.zzauz.length > 0) {
            int i = zzf;
            for (zzfy zzfy : this.zzauz) {
                if (zzfy != null) {
                    i += zzyy.zzb(2, (zzzg) zzfy);
                }
            }
            zzf = i;
        }
        if (this.zzava != null && this.zzava.length > 0) {
            for (zzfv zzfv : this.zzava) {
                if (zzfv != null) {
                    zzf += zzyy.zzb(3, (zzzg) zzfv);
                }
            }
        }
        if (this.zzavb != null) {
            this.zzavb.booleanValue();
            zzf += zzyy.zzbb(4) + 1;
        }
        if (this.zzavc == null) {
            return zzf;
        }
        this.zzavc.booleanValue();
        return zzf + zzyy.zzbb(5) + 1;
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
                int zzb = zzzj.zzb(zzyx, 18);
                int length = this.zzauz == null ? 0 : this.zzauz.length;
                zzfy[] zzfyArr = new zzfy[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzauz, 0, zzfyArr, 0, length);
                }
                while (length < zzfyArr.length - 1) {
                    zzfyArr[length] = new zzfy();
                    zzyx.zza((zzzg) zzfyArr[length]);
                    zzyx.zzug();
                    length++;
                }
                zzfyArr[length] = new zzfy();
                zzyx.zza((zzzg) zzfyArr[length]);
                this.zzauz = zzfyArr;
            } else if (zzug == 26) {
                int zzb2 = zzzj.zzb(zzyx, 26);
                int length2 = this.zzava == null ? 0 : this.zzava.length;
                zzfv[] zzfvArr = new zzfv[(zzb2 + length2)];
                if (length2 != 0) {
                    System.arraycopy(this.zzava, 0, zzfvArr, 0, length2);
                }
                while (length2 < zzfvArr.length - 1) {
                    zzfvArr[length2] = new zzfv();
                    zzyx.zza((zzzg) zzfvArr[length2]);
                    zzyx.zzug();
                    length2++;
                }
                zzfvArr[length2] = new zzfv();
                zzyx.zza((zzzg) zzfvArr[length2]);
                this.zzava = zzfvArr;
            } else if (zzug == 32) {
                this.zzavb = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 40) {
                this.zzavc = Boolean.valueOf(zzyx.zzum());
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
