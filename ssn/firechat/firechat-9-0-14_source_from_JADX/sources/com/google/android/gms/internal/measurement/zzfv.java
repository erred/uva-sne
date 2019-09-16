package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzfv extends zzza<zzfv> {
    private static volatile zzfv[] zzavd;
    public Boolean zzavb;
    public Boolean zzavc;
    public Integer zzave;
    public String zzavf;
    public zzfw[] zzavg;
    private Boolean zzavh;
    public zzfx zzavi;

    public static zzfv[] zzmj() {
        if (zzavd == null) {
            synchronized (zzze.zzcfl) {
                if (zzavd == null) {
                    zzavd = new zzfv[0];
                }
            }
        }
        return zzavd;
    }

    public zzfv() {
        this.zzave = null;
        this.zzavf = null;
        this.zzavg = zzfw.zzmk();
        this.zzavh = null;
        this.zzavi = null;
        this.zzavb = null;
        this.zzavc = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfv)) {
            return false;
        }
        zzfv zzfv = (zzfv) obj;
        if (this.zzave == null) {
            if (zzfv.zzave != null) {
                return false;
            }
        } else if (!this.zzave.equals(zzfv.zzave)) {
            return false;
        }
        if (this.zzavf == null) {
            if (zzfv.zzavf != null) {
                return false;
            }
        } else if (!this.zzavf.equals(zzfv.zzavf)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzavg, (Object[]) zzfv.zzavg)) {
            return false;
        }
        if (this.zzavh == null) {
            if (zzfv.zzavh != null) {
                return false;
            }
        } else if (!this.zzavh.equals(zzfv.zzavh)) {
            return false;
        }
        if (this.zzavi == null) {
            if (zzfv.zzavi != null) {
                return false;
            }
        } else if (!this.zzavi.equals(zzfv.zzavi)) {
            return false;
        }
        if (this.zzavb == null) {
            if (zzfv.zzavb != null) {
                return false;
            }
        } else if (!this.zzavb.equals(zzfv.zzavb)) {
            return false;
        }
        if (this.zzavc == null) {
            if (zzfv.zzavc != null) {
                return false;
            }
        } else if (!this.zzavc.equals(zzfv.zzavc)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzfv.zzcfc == null || zzfv.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzfv.zzcfc);
    }

    public final int hashCode() {
        int i;
        int i2 = 0;
        int hashCode = ((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzave == null ? 0 : this.zzave.hashCode())) * 31) + (this.zzavf == null ? 0 : this.zzavf.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzavg)) * 31) + (this.zzavh == null ? 0 : this.zzavh.hashCode());
        zzfx zzfx = this.zzavi;
        int i3 = hashCode * 31;
        if (zzfx == null) {
            i = 0;
        } else {
            i = zzfx.hashCode();
        }
        int hashCode2 = (((((i3 + i) * 31) + (this.zzavb == null ? 0 : this.zzavb.hashCode())) * 31) + (this.zzavc == null ? 0 : this.zzavc.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i2 = this.zzcfc.hashCode();
        }
        return hashCode2 + i2;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzave != null) {
            zzyy.zzd(1, this.zzave.intValue());
        }
        if (this.zzavf != null) {
            zzyy.zzb(2, this.zzavf);
        }
        if (this.zzavg != null && this.zzavg.length > 0) {
            for (zzfw zzfw : this.zzavg) {
                if (zzfw != null) {
                    zzyy.zza(3, (zzzg) zzfw);
                }
            }
        }
        if (this.zzavh != null) {
            zzyy.zzb(4, this.zzavh.booleanValue());
        }
        if (this.zzavi != null) {
            zzyy.zza(5, (zzzg) this.zzavi);
        }
        if (this.zzavb != null) {
            zzyy.zzb(6, this.zzavb.booleanValue());
        }
        if (this.zzavc != null) {
            zzyy.zzb(7, this.zzavc.booleanValue());
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzave != null) {
            zzf += zzyy.zzh(1, this.zzave.intValue());
        }
        if (this.zzavf != null) {
            zzf += zzyy.zzc(2, this.zzavf);
        }
        if (this.zzavg != null && this.zzavg.length > 0) {
            for (zzfw zzfw : this.zzavg) {
                if (zzfw != null) {
                    zzf += zzyy.zzb(3, (zzzg) zzfw);
                }
            }
        }
        if (this.zzavh != null) {
            this.zzavh.booleanValue();
            zzf += zzyy.zzbb(4) + 1;
        }
        if (this.zzavi != null) {
            zzf += zzyy.zzb(5, (zzzg) this.zzavi);
        }
        if (this.zzavb != null) {
            this.zzavb.booleanValue();
            zzf += zzyy.zzbb(6) + 1;
        }
        if (this.zzavc == null) {
            return zzf;
        }
        this.zzavc.booleanValue();
        return zzf + zzyy.zzbb(7) + 1;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                this.zzave = Integer.valueOf(zzyx.zzuy());
            } else if (zzug == 18) {
                this.zzavf = zzyx.readString();
            } else if (zzug == 26) {
                int zzb = zzzj.zzb(zzyx, 26);
                int length = this.zzavg == null ? 0 : this.zzavg.length;
                zzfw[] zzfwArr = new zzfw[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzavg, 0, zzfwArr, 0, length);
                }
                while (length < zzfwArr.length - 1) {
                    zzfwArr[length] = new zzfw();
                    zzyx.zza((zzzg) zzfwArr[length]);
                    zzyx.zzug();
                    length++;
                }
                zzfwArr[length] = new zzfw();
                zzyx.zza((zzzg) zzfwArr[length]);
                this.zzavg = zzfwArr;
            } else if (zzug == 32) {
                this.zzavh = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 42) {
                if (this.zzavi == null) {
                    this.zzavi = new zzfx();
                }
                zzyx.zza((zzzg) this.zzavi);
            } else if (zzug == 48) {
                this.zzavb = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 56) {
                this.zzavc = Boolean.valueOf(zzyx.zzum());
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
