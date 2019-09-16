package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzgj extends zzza<zzgj> {
    public long[] zzaye;
    public long[] zzayf;
    public zzge[] zzayg;
    public zzgk[] zzayh;

    public zzgj() {
        this.zzaye = zzzj.zzcfr;
        this.zzayf = zzzj.zzcfr;
        this.zzayg = zzge.zzmp();
        this.zzayh = zzgk.zzmt();
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgj)) {
            return false;
        }
        zzgj zzgj = (zzgj) obj;
        if (!zzze.equals(this.zzaye, zzgj.zzaye) || !zzze.equals(this.zzayf, zzgj.zzayf) || !zzze.equals((Object[]) this.zzayg, (Object[]) zzgj.zzayg) || !zzze.equals((Object[]) this.zzayh, (Object[]) zzgj.zzayh)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgj.zzcfc == null || zzgj.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgj.zzcfc);
    }

    public final int hashCode() {
        return ((((((((((getClass().getName().hashCode() + 527) * 31) + zzze.hashCode(this.zzaye)) * 31) + zzze.hashCode(this.zzayf)) * 31) + zzze.hashCode((Object[]) this.zzayg)) * 31) + zzze.hashCode((Object[]) this.zzayh)) * 31) + ((this.zzcfc == null || this.zzcfc.isEmpty()) ? 0 : this.zzcfc.hashCode());
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzaye != null && this.zzaye.length > 0) {
            for (long zza : this.zzaye) {
                zzyy.zza(1, zza);
            }
        }
        if (this.zzayf != null && this.zzayf.length > 0) {
            for (long zza2 : this.zzayf) {
                zzyy.zza(2, zza2);
            }
        }
        if (this.zzayg != null && this.zzayg.length > 0) {
            for (zzge zzge : this.zzayg) {
                if (zzge != null) {
                    zzyy.zza(3, (zzzg) zzge);
                }
            }
        }
        if (this.zzayh != null && this.zzayh.length > 0) {
            for (zzgk zzgk : this.zzayh) {
                if (zzgk != null) {
                    zzyy.zza(4, (zzzg) zzgk);
                }
            }
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzaye != null && this.zzaye.length > 0) {
            int i = 0;
            for (long zzbi : this.zzaye) {
                i += zzyy.zzbi(zzbi);
            }
            zzf = zzf + i + (this.zzaye.length * 1);
        }
        if (this.zzayf != null && this.zzayf.length > 0) {
            int i2 = 0;
            for (long zzbi2 : this.zzayf) {
                i2 += zzyy.zzbi(zzbi2);
            }
            zzf = zzf + i2 + (this.zzayf.length * 1);
        }
        if (this.zzayg != null && this.zzayg.length > 0) {
            int i3 = zzf;
            for (zzge zzge : this.zzayg) {
                if (zzge != null) {
                    i3 += zzyy.zzb(3, (zzzg) zzge);
                }
            }
            zzf = i3;
        }
        if (this.zzayh != null && this.zzayh.length > 0) {
            for (zzgk zzgk : this.zzayh) {
                if (zzgk != null) {
                    zzf += zzyy.zzb(4, (zzzg) zzgk);
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
            if (zzug == 8) {
                int zzb = zzzj.zzb(zzyx, 8);
                int length = this.zzaye == null ? 0 : this.zzaye.length;
                long[] jArr = new long[(zzb + length)];
                if (length != 0) {
                    System.arraycopy(this.zzaye, 0, jArr, 0, length);
                }
                while (length < jArr.length - 1) {
                    jArr[length] = zzyx.zzuz();
                    zzyx.zzug();
                    length++;
                }
                jArr[length] = zzyx.zzuz();
                this.zzaye = jArr;
            } else if (zzug == 10) {
                int zzaq = zzyx.zzaq(zzyx.zzuy());
                int position = zzyx.getPosition();
                int i = 0;
                while (zzyx.zzyr() > 0) {
                    zzyx.zzuz();
                    i++;
                }
                zzyx.zzby(position);
                int length2 = this.zzaye == null ? 0 : this.zzaye.length;
                long[] jArr2 = new long[(i + length2)];
                if (length2 != 0) {
                    System.arraycopy(this.zzaye, 0, jArr2, 0, length2);
                }
                while (length2 < jArr2.length) {
                    jArr2[length2] = zzyx.zzuz();
                    length2++;
                }
                this.zzaye = jArr2;
                zzyx.zzar(zzaq);
            } else if (zzug == 16) {
                int zzb2 = zzzj.zzb(zzyx, 16);
                int length3 = this.zzayf == null ? 0 : this.zzayf.length;
                long[] jArr3 = new long[(zzb2 + length3)];
                if (length3 != 0) {
                    System.arraycopy(this.zzayf, 0, jArr3, 0, length3);
                }
                while (length3 < jArr3.length - 1) {
                    jArr3[length3] = zzyx.zzuz();
                    zzyx.zzug();
                    length3++;
                }
                jArr3[length3] = zzyx.zzuz();
                this.zzayf = jArr3;
            } else if (zzug == 18) {
                int zzaq2 = zzyx.zzaq(zzyx.zzuy());
                int position2 = zzyx.getPosition();
                int i2 = 0;
                while (zzyx.zzyr() > 0) {
                    zzyx.zzuz();
                    i2++;
                }
                zzyx.zzby(position2);
                int length4 = this.zzayf == null ? 0 : this.zzayf.length;
                long[] jArr4 = new long[(i2 + length4)];
                if (length4 != 0) {
                    System.arraycopy(this.zzayf, 0, jArr4, 0, length4);
                }
                while (length4 < jArr4.length) {
                    jArr4[length4] = zzyx.zzuz();
                    length4++;
                }
                this.zzayf = jArr4;
                zzyx.zzar(zzaq2);
            } else if (zzug == 26) {
                int zzb3 = zzzj.zzb(zzyx, 26);
                int length5 = this.zzayg == null ? 0 : this.zzayg.length;
                zzge[] zzgeArr = new zzge[(zzb3 + length5)];
                if (length5 != 0) {
                    System.arraycopy(this.zzayg, 0, zzgeArr, 0, length5);
                }
                while (length5 < zzgeArr.length - 1) {
                    zzgeArr[length5] = new zzge();
                    zzyx.zza((zzzg) zzgeArr[length5]);
                    zzyx.zzug();
                    length5++;
                }
                zzgeArr[length5] = new zzge();
                zzyx.zza((zzzg) zzgeArr[length5]);
                this.zzayg = zzgeArr;
            } else if (zzug == 34) {
                int zzb4 = zzzj.zzb(zzyx, 34);
                int length6 = this.zzayh == null ? 0 : this.zzayh.length;
                zzgk[] zzgkArr = new zzgk[(zzb4 + length6)];
                if (length6 != 0) {
                    System.arraycopy(this.zzayh, 0, zzgkArr, 0, length6);
                }
                while (length6 < zzgkArr.length - 1) {
                    zzgkArr[length6] = new zzgk();
                    zzyx.zza((zzzg) zzgkArr[length6]);
                    zzyx.zzug();
                    length6++;
                }
                zzgkArr[length6] = new zzgk();
                zzyx.zza((zzzg) zzgkArr[length6]);
                this.zzayh = zzgkArr;
            } else if (!super.zza(zzyx, zzug)) {
                return this;
            }
        }
    }
}
