package com.google.android.gms.internal.measurement;

import com.google.android.gms.internal.measurement.zzvm.zze;
import java.io.IOException;
import java.util.Arrays;

public final class zzyc {
    private static final zzyc zzcco = new zzyc(0, new int[0], new Object[0], false);
    private int count;
    private boolean zzbtu;
    private int zzbyn;
    private Object[] zzcba;
    private int[] zzccp;

    public static zzyc zzyf() {
        return zzcco;
    }

    static zzyc zzyg() {
        return new zzyc();
    }

    static zzyc zza(zzyc zzyc, zzyc zzyc2) {
        int i = zzyc.count + zzyc2.count;
        int[] copyOf = Arrays.copyOf(zzyc.zzccp, i);
        System.arraycopy(zzyc2.zzccp, 0, copyOf, zzyc.count, zzyc2.count);
        Object[] copyOf2 = Arrays.copyOf(zzyc.zzcba, i);
        System.arraycopy(zzyc2.zzcba, 0, copyOf2, zzyc.count, zzyc2.count);
        return new zzyc(i, copyOf, copyOf2, true);
    }

    private zzyc() {
        this(0, new int[8], new Object[8], true);
    }

    private zzyc(int i, int[] iArr, Object[] objArr, boolean z) {
        this.zzbyn = -1;
        this.count = i;
        this.zzccp = iArr;
        this.zzcba = objArr;
        this.zzbtu = z;
    }

    public final void zzsm() {
        this.zzbtu = false;
    }

    /* access modifiers changed from: 0000 */
    public final void zza(zzyw zzyw) throws IOException {
        if (zzyw.zzvj() == zze.zzbzf) {
            for (int i = this.count - 1; i >= 0; i--) {
                zzyw.zza(this.zzccp[i] >>> 3, this.zzcba[i]);
            }
            return;
        }
        for (int i2 = 0; i2 < this.count; i2++) {
            zzyw.zza(this.zzccp[i2] >>> 3, this.zzcba[i2]);
        }
    }

    public final void zzb(zzyw zzyw) throws IOException {
        if (this.count != 0) {
            if (zzyw.zzvj() == zze.zzbze) {
                for (int i = 0; i < this.count; i++) {
                    zzb(this.zzccp[i], this.zzcba[i], zzyw);
                }
                return;
            }
            for (int i2 = this.count - 1; i2 >= 0; i2--) {
                zzb(this.zzccp[i2], this.zzcba[i2], zzyw);
            }
        }
    }

    private static void zzb(int i, Object obj, zzyw zzyw) throws IOException {
        int i2 = i >>> 3;
        int i3 = i & 7;
        if (i3 != 5) {
            switch (i3) {
                case 0:
                    zzyw.zzi(i2, ((Long) obj).longValue());
                    return;
                case 1:
                    zzyw.zzc(i2, ((Long) obj).longValue());
                    return;
                case 2:
                    zzyw.zza(i2, (zzud) obj);
                    return;
                case 3:
                    if (zzyw.zzvj() == zze.zzbze) {
                        zzyw.zzbk(i2);
                        ((zzyc) obj).zzb(zzyw);
                        zzyw.zzbl(i2);
                        return;
                    }
                    zzyw.zzbl(i2);
                    ((zzyc) obj).zzb(zzyw);
                    zzyw.zzbk(i2);
                    return;
                default:
                    throw new RuntimeException(zzvt.zzwo());
            }
        } else {
            zzyw.zzg(i2, ((Integer) obj).intValue());
        }
    }

    public final int zzyh() {
        int i = this.zzbyn;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.count; i3++) {
            i2 += zzut.zzd(this.zzccp[i3] >>> 3, (zzud) this.zzcba[i3]);
        }
        this.zzbyn = i2;
        return i2;
    }

    public final int zzvu() {
        int i;
        int i2 = this.zzbyn;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.count; i4++) {
            int i5 = this.zzccp[i4];
            int i6 = i5 >>> 3;
            int i7 = i5 & 7;
            if (i7 != 5) {
                switch (i7) {
                    case 0:
                        i = zzut.zze(i6, ((Long) this.zzcba[i4]).longValue());
                        break;
                    case 1:
                        i = zzut.zzg(i6, ((Long) this.zzcba[i4]).longValue());
                        break;
                    case 2:
                        i = zzut.zzc(i6, (zzud) this.zzcba[i4]);
                        break;
                    case 3:
                        i = (zzut.zzbb(i6) << 1) + ((zzyc) this.zzcba[i4]).zzvu();
                        break;
                    default:
                        throw new IllegalStateException(zzvt.zzwo());
                }
            } else {
                i = zzut.zzk(i6, ((Integer) this.zzcba[i4]).intValue());
            }
            i3 += i;
        }
        this.zzbyn = i3;
        return i3;
    }

    public final boolean equals(Object obj) {
        boolean z;
        boolean z2;
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof zzyc)) {
            return false;
        }
        zzyc zzyc = (zzyc) obj;
        if (this.count == zzyc.count) {
            int[] iArr = this.zzccp;
            int[] iArr2 = zzyc.zzccp;
            int i = this.count;
            int i2 = 0;
            while (true) {
                if (i2 >= i) {
                    z = true;
                    break;
                } else if (iArr[i2] != iArr2[i2]) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (z) {
                Object[] objArr = this.zzcba;
                Object[] objArr2 = zzyc.zzcba;
                int i3 = this.count;
                int i4 = 0;
                while (true) {
                    if (i4 >= i3) {
                        z2 = true;
                        break;
                    } else if (!objArr[i4].equals(objArr2[i4])) {
                        z2 = false;
                        break;
                    } else {
                        i4++;
                    }
                }
                return z2;
            }
        }
    }

    public final int hashCode() {
        int i = (this.count + 527) * 31;
        int[] iArr = this.zzccp;
        int i2 = 17;
        int i3 = 17;
        for (int i4 = 0; i4 < this.count; i4++) {
            i3 = (i3 * 31) + iArr[i4];
        }
        int i5 = (i + i3) * 31;
        Object[] objArr = this.zzcba;
        for (int i6 = 0; i6 < this.count; i6++) {
            i2 = (i2 * 31) + objArr[i6].hashCode();
        }
        return i5 + i2;
    }

    /* access modifiers changed from: 0000 */
    public final void zzb(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < this.count; i2++) {
            zzww.zzb(sb, i, String.valueOf(this.zzccp[i2] >>> 3), this.zzcba[i2]);
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zzb(int i, Object obj) {
        if (!this.zzbtu) {
            throw new UnsupportedOperationException();
        }
        if (this.count == this.zzccp.length) {
            int i2 = this.count + (this.count < 4 ? 8 : this.count >> 1);
            this.zzccp = Arrays.copyOf(this.zzccp, i2);
            this.zzcba = Arrays.copyOf(this.zzcba, i2);
        }
        this.zzccp[this.count] = i;
        this.zzcba[this.count] = obj;
        this.count++;
    }
}
