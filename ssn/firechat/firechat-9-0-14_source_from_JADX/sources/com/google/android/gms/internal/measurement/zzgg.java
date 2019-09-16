package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgg extends zzza<zzgg> {
    private static volatile zzgg[] zzaww;
    public String name;
    public String zzamp;
    private Float zzaug;
    public Double zzauh;
    public Long zzawx;

    public static zzgg[] zzmr() {
        if (zzaww == null) {
            synchronized (zzze.zzcfl) {
                if (zzaww == null) {
                    zzaww = new zzgg[0];
                }
            }
        }
        return zzaww;
    }

    public zzgg() {
        this.name = null;
        this.zzamp = null;
        this.zzawx = null;
        this.zzaug = null;
        this.zzauh = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgg)) {
            return false;
        }
        zzgg zzgg = (zzgg) obj;
        if (this.name == null) {
            if (zzgg.name != null) {
                return false;
            }
        } else if (!this.name.equals(zzgg.name)) {
            return false;
        }
        if (this.zzamp == null) {
            if (zzgg.zzamp != null) {
                return false;
            }
        } else if (!this.zzamp.equals(zzgg.zzamp)) {
            return false;
        }
        if (this.zzawx == null) {
            if (zzgg.zzawx != null) {
                return false;
            }
        } else if (!this.zzawx.equals(zzgg.zzawx)) {
            return false;
        }
        if (this.zzaug == null) {
            if (zzgg.zzaug != null) {
                return false;
            }
        } else if (!this.zzaug.equals(zzgg.zzaug)) {
            return false;
        }
        if (this.zzauh == null) {
            if (zzgg.zzauh != null) {
                return false;
            }
        } else if (!this.zzauh.equals(zzgg.zzauh)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgg.zzcfc == null || zzgg.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgg.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + (this.name == null ? 0 : this.name.hashCode())) * 31) + (this.zzamp == null ? 0 : this.zzamp.hashCode())) * 31) + (this.zzawx == null ? 0 : this.zzawx.hashCode())) * 31) + (this.zzaug == null ? 0 : this.zzaug.hashCode())) * 31) + (this.zzauh == null ? 0 : this.zzauh.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.name != null) {
            zzyy.zzb(1, this.name);
        }
        if (this.zzamp != null) {
            zzyy.zzb(2, this.zzamp);
        }
        if (this.zzawx != null) {
            zzyy.zzi(3, this.zzawx.longValue());
        }
        if (this.zzaug != null) {
            zzyy.zza(4, this.zzaug.floatValue());
        }
        if (this.zzauh != null) {
            zzyy.zza(5, this.zzauh.doubleValue());
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.name != null) {
            zzf += zzyy.zzc(1, this.name);
        }
        if (this.zzamp != null) {
            zzf += zzyy.zzc(2, this.zzamp);
        }
        if (this.zzawx != null) {
            zzf += zzyy.zzd(3, this.zzawx.longValue());
        }
        if (this.zzaug != null) {
            this.zzaug.floatValue();
            zzf += zzyy.zzbb(4) + 4;
        }
        if (this.zzauh == null) {
            return zzf;
        }
        this.zzauh.doubleValue();
        return zzf + zzyy.zzbb(5) + 8;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 10) {
                this.name = zzyx.readString();
            } else if (zzug == 18) {
                this.zzamp = zzyx.readString();
            } else if (zzug == 24) {
                this.zzawx = Long.valueOf(zzyx.zzuz());
            } else if (zzug == 37) {
                this.zzaug = Float.valueOf(Float.intBitsToFloat(zzyx.zzva()));
            } else if (zzug == 41) {
                this.zzauh = Double.valueOf(Double.longBitsToDouble(zzyx.zzvb()));
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
