package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzfw extends zzza<zzfw> {
    private static volatile zzfw[] zzavj;
    public zzfz zzavk;
    public zzfx zzavl;
    public Boolean zzavm;
    public String zzavn;

    public static zzfw[] zzmk() {
        if (zzavj == null) {
            synchronized (zzze.zzcfl) {
                if (zzavj == null) {
                    zzavj = new zzfw[0];
                }
            }
        }
        return zzavj;
    }

    public zzfw() {
        this.zzavk = null;
        this.zzavl = null;
        this.zzavm = null;
        this.zzavn = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfw)) {
            return false;
        }
        zzfw zzfw = (zzfw) obj;
        if (this.zzavk == null) {
            if (zzfw.zzavk != null) {
                return false;
            }
        } else if (!this.zzavk.equals(zzfw.zzavk)) {
            return false;
        }
        if (this.zzavl == null) {
            if (zzfw.zzavl != null) {
                return false;
            }
        } else if (!this.zzavl.equals(zzfw.zzavl)) {
            return false;
        }
        if (this.zzavm == null) {
            if (zzfw.zzavm != null) {
                return false;
            }
        } else if (!this.zzavm.equals(zzfw.zzavm)) {
            return false;
        }
        if (this.zzavn == null) {
            if (zzfw.zzavn != null) {
                return false;
            }
        } else if (!this.zzavn.equals(zzfw.zzavn)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzfw.zzcfc == null || zzfw.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzfw.zzcfc);
    }

    public final int hashCode() {
        int i;
        int i2;
        int hashCode = getClass().getName().hashCode() + 527;
        zzfz zzfz = this.zzavk;
        int i3 = hashCode * 31;
        int i4 = 0;
        if (zzfz == null) {
            i = 0;
        } else {
            i = zzfz.hashCode();
        }
        int i5 = i3 + i;
        zzfx zzfx = this.zzavl;
        int i6 = i5 * 31;
        if (zzfx == null) {
            i2 = 0;
        } else {
            i2 = zzfx.hashCode();
        }
        int hashCode2 = (((((i6 + i2) * 31) + (this.zzavm == null ? 0 : this.zzavm.hashCode())) * 31) + (this.zzavn == null ? 0 : this.zzavn.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i4 = this.zzcfc.hashCode();
        }
        return hashCode2 + i4;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzavk != null) {
            zzyy.zza(1, (zzzg) this.zzavk);
        }
        if (this.zzavl != null) {
            zzyy.zza(2, (zzzg) this.zzavl);
        }
        if (this.zzavm != null) {
            zzyy.zzb(3, this.zzavm.booleanValue());
        }
        if (this.zzavn != null) {
            zzyy.zzb(4, this.zzavn);
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzavk != null) {
            zzf += zzyy.zzb(1, (zzzg) this.zzavk);
        }
        if (this.zzavl != null) {
            zzf += zzyy.zzb(2, (zzzg) this.zzavl);
        }
        if (this.zzavm != null) {
            this.zzavm.booleanValue();
            zzf += zzyy.zzbb(3) + 1;
        }
        return this.zzavn != null ? zzf + zzyy.zzc(4, this.zzavn) : zzf;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 10) {
                if (this.zzavk == null) {
                    this.zzavk = new zzfz();
                }
                zzyx.zza((zzzg) this.zzavk);
            } else if (zzug == 18) {
                if (this.zzavl == null) {
                    this.zzavl = new zzfx();
                }
                zzyx.zza((zzzg) this.zzavl);
            } else if (zzug == 24) {
                this.zzavm = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 34) {
                this.zzavn = zzyx.readString();
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
