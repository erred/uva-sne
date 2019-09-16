package com.google.android.gms.internal.measurement;

import java.io.IOException;
import kotlin.jvm.internal.ByteCompanionObject;
import org.altbeacon.bluetooth.Pdu;

public final class zzyx {
    private final byte[] buffer;
    private int zzbuh;
    private int zzbui = 64;
    private int zzbuj = 67108864;
    private int zzbun;
    private int zzbup;
    private int zzbuq = Integer.MAX_VALUE;
    private final int zzcev;
    private final int zzcew;
    private int zzcex;
    private int zzcey;
    private zzuo zzcez;

    public static zzyx zzn(byte[] bArr) {
        return zzj(bArr, 0, bArr.length);
    }

    public static zzyx zzj(byte[] bArr, int i, int i2) {
        return new zzyx(bArr, 0, i2);
    }

    public final int zzug() throws IOException {
        if (this.zzcey == this.zzcex) {
            this.zzbup = 0;
            return 0;
        }
        this.zzbup = zzuy();
        if (this.zzbup != 0) {
            return this.zzbup;
        }
        throw new zzzf("Protocol message contained an invalid tag (zero).");
    }

    public final void zzan(int i) throws zzzf {
        if (this.zzbup != i) {
            throw new zzzf("Protocol message end-group tag did not match expected tag.");
        }
    }

    public final boolean zzao(int i) throws IOException {
        int zzug;
        switch (i & 7) {
            case 0:
                zzuy();
                return true;
            case 1:
                zzvb();
                return true;
            case 2:
                zzas(zzuy());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                zzva();
                return true;
            default:
                throw new zzzf("Protocol message tag had invalid wire type.");
        }
        do {
            zzug = zzug();
            if (zzug != 0) {
            }
            zzan(((i >>> 3) << 3) | 4);
            return true;
        } while (zzao(zzug));
        zzan(((i >>> 3) << 3) | 4);
        return true;
    }

    public final boolean zzum() throws IOException {
        return zzuy() != 0;
    }

    public final String readString() throws IOException {
        int zzuy = zzuy();
        if (zzuy < 0) {
            throw zzzf.zzyx();
        } else if (zzuy > this.zzcex - this.zzcey) {
            throw zzzf.zzyw();
        } else {
            String str = new String(this.buffer, this.zzcey, zzuy, zzze.UTF_8);
            this.zzcey += zzuy;
            return str;
        }
    }

    public final void zza(zzzg zzzg, int i) throws IOException {
        if (this.zzbuh >= this.zzbui) {
            throw zzzf.zzyz();
        }
        this.zzbuh++;
        zzzg.zza(this);
        zzan((i << 3) | 4);
        this.zzbuh--;
    }

    public final void zza(zzzg zzzg) throws IOException {
        int zzuy = zzuy();
        if (this.zzbuh >= this.zzbui) {
            throw zzzf.zzyz();
        }
        int zzaq = zzaq(zzuy);
        this.zzbuh++;
        zzzg.zza(this);
        zzan(0);
        this.zzbuh--;
        zzar(zzaq);
    }

    public final int zzuy() throws IOException {
        byte b;
        byte zzvd = zzvd();
        if (zzvd >= 0) {
            return zzvd;
        }
        byte b2 = zzvd & ByteCompanionObject.MAX_VALUE;
        byte zzvd2 = zzvd();
        if (zzvd2 >= 0) {
            b = b2 | (zzvd2 << 7);
        } else {
            byte b3 = b2 | ((zzvd2 & ByteCompanionObject.MAX_VALUE) << 7);
            byte zzvd3 = zzvd();
            if (zzvd3 >= 0) {
                b = b3 | (zzvd3 << 14);
            } else {
                byte b4 = b3 | ((zzvd3 & ByteCompanionObject.MAX_VALUE) << 14);
                byte zzvd4 = zzvd();
                if (zzvd4 >= 0) {
                    b = b4 | (zzvd4 << 21);
                } else {
                    byte b5 = b4 | ((zzvd4 & ByteCompanionObject.MAX_VALUE) << 21);
                    byte zzvd5 = zzvd();
                    b = b5 | (zzvd5 << 28);
                    if (zzvd5 < 0) {
                        for (int i = 0; i < 5; i++) {
                            if (zzvd() >= 0) {
                                return b;
                            }
                        }
                        throw zzzf.zzyy();
                    }
                }
            }
        }
        return b;
    }

    public final long zzuz() throws IOException {
        int i = 0;
        long j = 0;
        while (i < 64) {
            byte zzvd = zzvd();
            long j2 = j | (((long) (zzvd & ByteCompanionObject.MAX_VALUE)) << i);
            if ((zzvd & ByteCompanionObject.MIN_VALUE) == 0) {
                return j2;
            }
            i += 7;
            j = j2;
        }
        throw zzzf.zzyy();
    }

    public final int zzva() throws IOException {
        return (zzvd() & Pdu.MANUFACTURER_DATA_PDU_TYPE) | ((zzvd() & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 8) | ((zzvd() & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 16) | ((zzvd() & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 24);
    }

    public final long zzvb() throws IOException {
        return (((long) zzvd()) & 255) | ((((long) zzvd()) & 255) << 8) | ((((long) zzvd()) & 255) << 16) | ((((long) zzvd()) & 255) << 24) | ((((long) zzvd()) & 255) << 32) | ((((long) zzvd()) & 255) << 40) | ((((long) zzvd()) & 255) << 48) | ((((long) zzvd()) & 255) << 56);
    }

    private zzyx(byte[] bArr, int i, int i2) {
        this.buffer = bArr;
        this.zzcev = i;
        int i3 = i2 + i;
        this.zzcex = i3;
        this.zzcew = i3;
        this.zzcey = i;
    }

    public final <T extends zzvm<T, ?>> T zza(zzxd<T> zzxd) throws IOException {
        try {
            if (this.zzcez == null) {
                this.zzcez = zzuo.zzd(this.buffer, this.zzcev, this.zzcew);
            }
            int zzux = this.zzcez.zzux();
            int i = this.zzcey - this.zzcev;
            if (zzux > i) {
                throw new IOException(String.format("CodedInputStream read ahead of CodedInputByteBufferNano: %s > %s", new Object[]{Integer.valueOf(zzux), Integer.valueOf(i)}));
            }
            this.zzcez.zzas(i - zzux);
            this.zzcez.zzap(this.zzbui - this.zzbuh);
            T t = (zzvm) this.zzcez.zza(zzxd, zzuz.zzvp());
            zzao(this.zzbup);
            return t;
        } catch (zzvt e) {
            throw new zzzf("", e);
        }
    }

    public final int zzaq(int i) throws zzzf {
        if (i < 0) {
            throw zzzf.zzyx();
        }
        int i2 = i + this.zzcey;
        int i3 = this.zzbuq;
        if (i2 > i3) {
            throw zzzf.zzyw();
        }
        this.zzbuq = i2;
        zzvc();
        return i3;
    }

    private final void zzvc() {
        this.zzcex += this.zzbun;
        int i = this.zzcex;
        if (i > this.zzbuq) {
            this.zzbun = i - this.zzbuq;
            this.zzcex -= this.zzbun;
            return;
        }
        this.zzbun = 0;
    }

    public final void zzar(int i) {
        this.zzbuq = i;
        zzvc();
    }

    public final int zzyr() {
        if (this.zzbuq == Integer.MAX_VALUE) {
            return -1;
        }
        return this.zzbuq - this.zzcey;
    }

    public final int getPosition() {
        return this.zzcey - this.zzcev;
    }

    public final byte[] zzs(int i, int i2) {
        if (i2 == 0) {
            return zzzj.zzcfx;
        }
        byte[] bArr = new byte[i2];
        System.arraycopy(this.buffer, this.zzcev + i, bArr, 0, i2);
        return bArr;
    }

    public final void zzby(int i) {
        zzt(i, this.zzbup);
    }

    /* access modifiers changed from: 0000 */
    public final void zzt(int i, int i2) {
        if (i > this.zzcey - this.zzcev) {
            int i3 = this.zzcey - this.zzcev;
            StringBuilder sb = new StringBuilder(50);
            sb.append("Position ");
            sb.append(i);
            sb.append(" is beyond current ");
            sb.append(i3);
            throw new IllegalArgumentException(sb.toString());
        } else if (i < 0) {
            StringBuilder sb2 = new StringBuilder(24);
            sb2.append("Bad position ");
            sb2.append(i);
            throw new IllegalArgumentException(sb2.toString());
        } else {
            this.zzcey = this.zzcev + i;
            this.zzbup = i2;
        }
    }

    private final byte zzvd() throws IOException {
        if (this.zzcey == this.zzcex) {
            throw zzzf.zzyw();
        }
        byte[] bArr = this.buffer;
        int i = this.zzcey;
        this.zzcey = i + 1;
        return bArr[i];
    }

    private final void zzas(int i) throws IOException {
        if (i < 0) {
            throw zzzf.zzyx();
        } else if (this.zzcey + i > this.zzbuq) {
            zzas(this.zzbuq - this.zzcey);
            throw zzzf.zzyw();
        } else if (i <= this.zzcex - this.zzcey) {
            this.zzcey += i;
        } else {
            throw zzzf.zzyw();
        }
    }
}
