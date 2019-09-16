package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzfx extends zzza<zzfx> {
    public Integer zzavo;
    public Boolean zzavp;
    public String zzavq;
    public String zzavr;
    public String zzavs;

    public zzfx() {
        this.zzavo = null;
        this.zzavp = null;
        this.zzavq = null;
        this.zzavr = null;
        this.zzavs = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfx)) {
            return false;
        }
        zzfx zzfx = (zzfx) obj;
        if (this.zzavo == null) {
            if (zzfx.zzavo != null) {
                return false;
            }
        } else if (!this.zzavo.equals(zzfx.zzavo)) {
            return false;
        }
        if (this.zzavp == null) {
            if (zzfx.zzavp != null) {
                return false;
            }
        } else if (!this.zzavp.equals(zzfx.zzavp)) {
            return false;
        }
        if (this.zzavq == null) {
            if (zzfx.zzavq != null) {
                return false;
            }
        } else if (!this.zzavq.equals(zzfx.zzavq)) {
            return false;
        }
        if (this.zzavr == null) {
            if (zzfx.zzavr != null) {
                return false;
            }
        } else if (!this.zzavr.equals(zzfx.zzavr)) {
            return false;
        }
        if (this.zzavs == null) {
            if (zzfx.zzavs != null) {
                return false;
            }
        } else if (!this.zzavs.equals(zzfx.zzavs)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzfx.zzcfc == null || zzfx.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzfx.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzavo == null ? 0 : this.zzavo.intValue())) * 31) + (this.zzavp == null ? 0 : this.zzavp.hashCode())) * 31) + (this.zzavq == null ? 0 : this.zzavq.hashCode())) * 31) + (this.zzavr == null ? 0 : this.zzavr.hashCode())) * 31) + (this.zzavs == null ? 0 : this.zzavs.hashCode())) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzavo != null) {
            zzyy.zzd(1, this.zzavo.intValue());
        }
        if (this.zzavp != null) {
            zzyy.zzb(2, this.zzavp.booleanValue());
        }
        if (this.zzavq != null) {
            zzyy.zzb(3, this.zzavq);
        }
        if (this.zzavr != null) {
            zzyy.zzb(4, this.zzavr);
        }
        if (this.zzavs != null) {
            zzyy.zzb(5, this.zzavs);
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzavo != null) {
            zzf += zzyy.zzh(1, this.zzavo.intValue());
        }
        if (this.zzavp != null) {
            this.zzavp.booleanValue();
            zzf += zzyy.zzbb(2) + 1;
        }
        if (this.zzavq != null) {
            zzf += zzyy.zzc(3, this.zzavq);
        }
        if (this.zzavr != null) {
            zzf += zzyy.zzc(4, this.zzavr);
        }
        return this.zzavs != null ? zzf + zzyy.zzc(5, this.zzavs) : zzf;
    }

    /* access modifiers changed from: private */
    /* renamed from: zzc */
    public final zzfx zza(zzyx zzyx) throws IOException {
        int zzuy;
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                try {
                    zzuy = zzyx.zzuy();
                    if (zzuy < 0 || zzuy > 4) {
                        StringBuilder sb = new StringBuilder(46);
                        sb.append(zzuy);
                        sb.append(" is not a valid enum ComparisonType");
                    } else {
                        this.zzavo = Integer.valueOf(zzuy);
                    }
                } catch (IllegalArgumentException unused) {
                    zzyx.zzby(zzyx.getPosition());
                    zza(zzyx, zzug);
                }
            } else if (zzug == 16) {
                this.zzavp = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 26) {
                this.zzavq = zzyx.readString();
            } else if (zzug == 34) {
                this.zzavr = zzyx.readString();
            } else if (zzug == 42) {
                this.zzavs = zzyx.readString();
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
        StringBuilder sb2 = new StringBuilder(46);
        sb2.append(zzuy);
        sb2.append(" is not a valid enum ComparisonType");
        throw new IllegalArgumentException(sb2.toString());
    }
}
