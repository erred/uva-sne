package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzfz extends zzza<zzfz> {
    public Integer zzavw;
    public String zzavx;
    public Boolean zzavy;
    public String[] zzavz;

    public zzfz() {
        this.zzavw = null;
        this.zzavx = null;
        this.zzavy = null;
        this.zzavz = zzzj.zzcfv;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzfz)) {
            return false;
        }
        zzfz zzfz = (zzfz) obj;
        if (this.zzavw == null) {
            if (zzfz.zzavw != null) {
                return false;
            }
        } else if (!this.zzavw.equals(zzfz.zzavw)) {
            return false;
        }
        if (this.zzavx == null) {
            if (zzfz.zzavx != null) {
                return false;
            }
        } else if (!this.zzavx.equals(zzfz.zzavx)) {
            return false;
        }
        if (this.zzavy == null) {
            if (zzfz.zzavy != null) {
                return false;
            }
        } else if (!this.zzavy.equals(zzfz.zzavy)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzavz, (Object[]) zzfz.zzavz)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzfz.zzcfc == null || zzfz.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzfz.zzcfc);
    }

    public final int hashCode() {
        int i = 0;
        int hashCode = (((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzavw == null ? 0 : this.zzavw.intValue())) * 31) + (this.zzavx == null ? 0 : this.zzavx.hashCode())) * 31) + (this.zzavy == null ? 0 : this.zzavy.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzavz)) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i = this.zzcfc.hashCode();
        }
        return hashCode + i;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzavw != null) {
            zzyy.zzd(1, this.zzavw.intValue());
        }
        if (this.zzavx != null) {
            zzyy.zzb(2, this.zzavx);
        }
        if (this.zzavy != null) {
            zzyy.zzb(3, this.zzavy.booleanValue());
        }
        if (this.zzavz != null && this.zzavz.length > 0) {
            for (String str : this.zzavz) {
                if (str != null) {
                    zzyy.zzb(4, str);
                }
            }
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzavw != null) {
            zzf += zzyy.zzh(1, this.zzavw.intValue());
        }
        if (this.zzavx != null) {
            zzf += zzyy.zzc(2, this.zzavx);
        }
        if (this.zzavy != null) {
            this.zzavy.booleanValue();
            zzf += zzyy.zzbb(3) + 1;
        }
        if (this.zzavz == null || this.zzavz.length <= 0) {
            return zzf;
        }
        int i = 0;
        int i2 = 0;
        for (String str : this.zzavz) {
            if (str != null) {
                i2++;
                i += zzyy.zzfx(str);
            }
        }
        return zzf + i + (i2 * 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: zzd */
    public final zzfz zza(zzyx zzyx) throws IOException {
        int zzuy;
        while (true) {
            int zzug = zzyx.zzug();
            if (zzug == 0) {
                return this;
            }
            if (zzug == 8) {
                try {
                    zzuy = zzyx.zzuy();
                    if (zzuy < 0 || zzuy > 6) {
                        StringBuilder sb = new StringBuilder(41);
                        sb.append(zzuy);
                        sb.append(" is not a valid enum MatchType");
                    } else {
                        this.zzavw = Integer.valueOf(zzuy);
                    }
                } catch (IllegalArgumentException unused) {
                    zzyx.zzby(zzyx.getPosition());
                    zza(zzyx, zzug);
                }
            } else if (zzug == 18) {
                this.zzavx = zzyx.readString();
            } else if (zzug == 24) {
                this.zzavy = Boolean.valueOf(zzyx.zzum());
            } else if (zzug == 34) {
                int zzb = zzzj.zzb(zzyx, 34);
                int length = this.zzavz == null ? 0 : this.zzavz.length;
                String[] strArr = new String[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzavz, 0, strArr, 0, length);
                }
                while (length < strArr.length - 1) {
                    strArr[length] = zzyx.readString();
                    zzyx.zzug();
                    length++;
                }
                strArr[length] = zzyx.readString();
                this.zzavz = strArr;
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
        StringBuilder sb2 = new StringBuilder(41);
        sb2.append(zzuy);
        sb2.append(" is not a valid enum MatchType");
        throw new IllegalArgumentException(sb2.toString());
    }
}
