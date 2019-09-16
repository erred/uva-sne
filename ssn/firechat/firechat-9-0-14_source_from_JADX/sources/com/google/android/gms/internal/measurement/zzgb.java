package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgb extends zzza<zzgb> {
    public String zzafx;
    public Long zzawe;
    private Integer zzawf;
    public zzgc[] zzawg;
    public zzga[] zzawh;
    public zzfu[] zzawi;
    private String zzawj;

    public zzgb() {
        this.zzawe = null;
        this.zzafx = null;
        this.zzawf = null;
        this.zzawg = zzgc.zzmn();
        this.zzawh = zzga.zzmm();
        this.zzawi = zzfu.zzmi();
        this.zzawj = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgb)) {
            return false;
        }
        zzgb zzgb = (zzgb) obj;
        if (this.zzawe == null) {
            if (zzgb.zzawe != null) {
                return false;
            }
        } else if (!this.zzawe.equals(zzgb.zzawe)) {
            return false;
        }
        if (this.zzafx == null) {
            if (zzgb.zzafx != null) {
                return false;
            }
        } else if (!this.zzafx.equals(zzgb.zzafx)) {
            return false;
        }
        if (this.zzawf == null) {
            if (zzgb.zzawf != null) {
                return false;
            }
        } else if (!this.zzawf.equals(zzgb.zzawf)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzawg, (Object[]) zzgb.zzawg) || !zzze.equals((Object[]) this.zzawh, (Object[]) zzgb.zzawh) || !zzze.equals((Object[]) this.zzawi, (Object[]) zzgb.zzawi)) {
            return false;
        }
        if (this.zzawj == null) {
            if (zzgb.zzawj != null) {
                return false;
            }
        } else if (!this.zzawj.equals(zzgb.zzawj)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgb.zzcfc == null || zzgb.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgb.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((((((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzawe == null ? 0 : this.zzawe.hashCode())) * 31) + (this.zzafx == null ? 0 : this.zzafx.hashCode())) * 31) + (this.zzawf == null ? 0 : this.zzawf.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzawg)) * 31) + zzze.hashCode((Object[]) this.zzawh)) * 31) + zzze.hashCode((Object[]) this.zzawi)) * 31) + (this.zzawj == null ? 0 : this.zzawj.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzawe != null) {
            zzyy.zzi(1, this.zzawe.longValue());
        }
        if (this.zzafx != null) {
            zzyy.zzb(2, this.zzafx);
        }
        if (this.zzawf != null) {
            zzyy.zzd(3, this.zzawf.intValue());
        }
        if (this.zzawg != null && this.zzawg.length > 0) {
            for (zzgc zzgc : this.zzawg) {
                if (zzgc != null) {
                    zzyy.zza(4, (zzzg) zzgc);
                }
            }
        }
        if (this.zzawh != null && this.zzawh.length > 0) {
            for (zzga zzga : this.zzawh) {
                if (zzga != null) {
                    zzyy.zza(5, (zzzg) zzga);
                }
            }
        }
        if (this.zzawi != null && this.zzawi.length > 0) {
            for (zzfu zzfu : this.zzawi) {
                if (zzfu != null) {
                    zzyy.zza(6, (zzzg) zzfu);
                }
            }
        }
        if (this.zzawj != null) {
            zzyy.zzb(7, this.zzawj);
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzawe != null) {
            zzf += zzyy.zzd(1, this.zzawe.longValue());
        }
        if (this.zzafx != null) {
            zzf += zzyy.zzc(2, this.zzafx);
        }
        if (this.zzawf != null) {
            zzf += zzyy.zzh(3, this.zzawf.intValue());
        }
        if (this.zzawg != null && this.zzawg.length > 0) {
            int i = zzf;
            for (zzgc zzgc : this.zzawg) {
                if (zzgc != null) {
                    i += zzyy.zzb(4, (zzzg) zzgc);
                }
            }
            zzf = i;
        }
        if (this.zzawh != null && this.zzawh.length > 0) {
            int i2 = zzf;
            for (zzga zzga : this.zzawh) {
                if (zzga != null) {
                    i2 += zzyy.zzb(5, (zzzg) zzga);
                }
            }
            zzf = i2;
        }
        if (this.zzawi != null && this.zzawi.length > 0) {
            for (zzfu zzfu : this.zzawi) {
                if (zzfu != null) {
                    zzf += zzyy.zzb(6, (zzzg) zzfu);
                }
            }
        }
        return this.zzawj != null ? zzf + zzyy.zzc(7, this.zzawj) : zzf;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                this.zzawe = Long.valueOf(zzyx.zzuz());
            } else if (zzug == 18) {
                this.zzafx = zzyx.readString();
            } else if (zzug == 24) {
                this.zzawf = Integer.valueOf(zzyx.zzuy());
            } else if (zzug == 34) {
                int zzb = zzzj.zzb(zzyx, 34);
                int length = this.zzawg == null ? 0 : this.zzawg.length;
                zzgc[] zzgcArr = new zzgc[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzawg, 0, zzgcArr, 0, length);
                }
                while (length < zzgcArr.length - 1) {
                    zzgcArr[length] = new zzgc();
                    zzyx.zza((zzzg) zzgcArr[length]);
                    zzyx.zzug();
                    length++;
                }
                zzgcArr[length] = new zzgc();
                zzyx.zza((zzzg) zzgcArr[length]);
                this.zzawg = zzgcArr;
            } else if (zzug == 42) {
                int zzb2 = zzzj.zzb(zzyx, 42);
                int length2 = this.zzawh == null ? 0 : this.zzawh.length;
                zzga[] zzgaArr = new zzga[(zzb2 + length2)];
                if (length2 != 0) {
                    System.arraycopy(this.zzawh, 0, zzgaArr, 0, length2);
                }
                while (length2 < zzgaArr.length - 1) {
                    zzgaArr[length2] = new zzga();
                    zzyx.zza((zzzg) zzgaArr[length2]);
                    zzyx.zzug();
                    length2++;
                }
                zzgaArr[length2] = new zzga();
                zzyx.zza((zzzg) zzgaArr[length2]);
                this.zzawh = zzgaArr;
            } else if (zzug == 50) {
                int zzb3 = zzzj.zzb(zzyx, 50);
                int length3 = this.zzawi == null ? 0 : this.zzawi.length;
                zzfu[] zzfuArr = new zzfu[(zzb3 + length3)];
                if (length3 != 0) {
                    System.arraycopy(this.zzawi, 0, zzfuArr, 0, length3);
                }
                while (length3 < zzfuArr.length - 1) {
                    zzfuArr[length3] = new zzfu();
                    zzyx.zza((zzzg) zzfuArr[length3]);
                    zzyx.zzug();
                    length3++;
                }
                zzfuArr[length3] = new zzfu();
                zzyx.zza((zzzg) zzfuArr[length3]);
                this.zzawi = zzfuArr;
            } else if (zzug == 58) {
                this.zzawj = zzyx.readString();
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
