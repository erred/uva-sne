package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.util.Arrays;
import kotlin.jvm.internal.ByteCompanionObject;
import org.altbeacon.bluetooth.Pdu;

final class zzuq extends zzuo {
    private final byte[] buffer;
    private int limit;
    private int pos;
    private final boolean zzbum;
    private int zzbun;
    private int zzbuo;
    private int zzbup;
    private int zzbuq;

    private zzuq(byte[] bArr, int i, int i2, boolean z) {
        super();
        this.zzbuq = Integer.MAX_VALUE;
        this.buffer = bArr;
        this.limit = i2 + i;
        this.pos = i;
        this.zzbuo = this.pos;
        this.zzbum = z;
    }

    public final int zzug() throws IOException {
        if (zzuw()) {
            this.zzbup = 0;
            return 0;
        }
        this.zzbup = zzuy();
        if ((this.zzbup >>> 3) != 0) {
            return this.zzbup;
        }
        throw new zzvt("Protocol message contained an invalid tag (zero).");
    }

    public final void zzan(int i) throws zzvt {
        if (this.zzbup != i) {
            throw zzvt.zzwn();
        }
    }

    public final boolean zzao(int i) throws IOException {
        int zzug;
        int i2 = 0;
        switch (i & 7) {
            case 0:
                if (this.limit - this.pos >= 10) {
                    while (i2 < 10) {
                        byte[] bArr = this.buffer;
                        int i3 = this.pos;
                        this.pos = i3 + 1;
                        if (bArr[i3] < 0) {
                            i2++;
                        }
                    }
                    throw zzvt.zzwm();
                }
                while (i2 < 10) {
                    if (zzvd() < 0) {
                        i2++;
                    }
                }
                throw zzvt.zzwm();
                return true;
            case 1:
                zzas(8);
                return true;
            case 2:
                zzas(zzuy());
                return true;
            case 3:
                break;
            case 4:
                return false;
            case 5:
                zzas(4);
                return true;
            default:
                throw zzvt.zzwo();
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

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(zzvb());
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(zzva());
    }

    public final long zzuh() throws IOException {
        return zzuz();
    }

    public final long zzui() throws IOException {
        return zzuz();
    }

    public final int zzuj() throws IOException {
        return zzuy();
    }

    public final long zzuk() throws IOException {
        return zzvb();
    }

    public final int zzul() throws IOException {
        return zzva();
    }

    public final boolean zzum() throws IOException {
        return zzuz() != 0;
    }

    public final String readString() throws IOException {
        int zzuy = zzuy();
        if (zzuy > 0 && zzuy <= this.limit - this.pos) {
            String str = new String(this.buffer, this.pos, zzuy, zzvo.UTF_8);
            this.pos += zzuy;
            return str;
        } else if (zzuy == 0) {
            return "";
        } else {
            if (zzuy < 0) {
                throw zzvt.zzwl();
            }
            throw zzvt.zzwk();
        }
    }

    public final String zzun() throws IOException {
        int zzuy = zzuy();
        if (zzuy > 0 && zzuy <= this.limit - this.pos) {
            String zzh = zzyj.zzh(this.buffer, this.pos, zzuy);
            this.pos += zzuy;
            return zzh;
        } else if (zzuy == 0) {
            return "";
        } else {
            if (zzuy <= 0) {
                throw zzvt.zzwl();
            }
            throw zzvt.zzwk();
        }
    }

    public final <T extends zzwt> T zza(zzxd<T> zzxd, zzuz zzuz) throws IOException {
        int zzuy = zzuy();
        if (this.zzbuh >= this.zzbui) {
            throw zzvt.zzwp();
        }
        int zzaq = zzaq(zzuy);
        this.zzbuh++;
        T t = (zzwt) zzxd.zza(this, zzuz);
        zzan(0);
        this.zzbuh--;
        zzar(zzaq);
        return t;
    }

    public final zzud zzuo() throws IOException {
        byte[] bArr;
        int zzuy = zzuy();
        if (zzuy > 0 && zzuy <= this.limit - this.pos) {
            zzud zzb = zzud.zzb(this.buffer, this.pos, zzuy);
            this.pos += zzuy;
            return zzb;
        } else if (zzuy == 0) {
            return zzud.zzbtz;
        } else {
            if (zzuy > 0 && zzuy <= this.limit - this.pos) {
                int i = this.pos;
                this.pos += zzuy;
                bArr = Arrays.copyOfRange(this.buffer, i, this.pos);
            } else if (zzuy > 0) {
                throw zzvt.zzwk();
            } else if (zzuy == 0) {
                bArr = zzvo.zzbzj;
            } else {
                throw zzvt.zzwl();
            }
            return zzud.zzi(bArr);
        }
    }

    public final int zzup() throws IOException {
        return zzuy();
    }

    public final int zzuq() throws IOException {
        return zzuy();
    }

    public final int zzur() throws IOException {
        return zzva();
    }

    public final long zzus() throws IOException {
        return zzvb();
    }

    public final int zzut() throws IOException {
        int zzuy = zzuy();
        return (-(zzuy & 1)) ^ (zzuy >>> 1);
    }

    public final long zzuu() throws IOException {
        long zzuz = zzuz();
        return (zzuz >>> 1) ^ (-(zzuz & 1));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0068, code lost:
        if (r1[r2] >= 0) goto L_0x006a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int zzuy() throws java.io.IOException {
        /*
            r5 = this;
            int r0 = r5.pos
            int r1 = r5.limit
            if (r1 == r0) goto L_0x006d
            byte[] r1 = r5.buffer
            int r2 = r0 + 1
            byte r0 = r1[r0]
            if (r0 < 0) goto L_0x0011
            r5.pos = r2
            return r0
        L_0x0011:
            int r3 = r5.limit
            int r3 = r3 - r2
            r4 = 9
            if (r3 < r4) goto L_0x006d
            int r3 = r2 + 1
            byte r2 = r1[r2]
            int r2 = r2 << 7
            r0 = r0 ^ r2
            if (r0 >= 0) goto L_0x0024
            r0 = r0 ^ -128(0xffffffffffffff80, float:NaN)
            goto L_0x006a
        L_0x0024:
            int r2 = r3 + 1
            byte r3 = r1[r3]
            int r3 = r3 << 14
            r0 = r0 ^ r3
            if (r0 < 0) goto L_0x0031
            r0 = r0 ^ 16256(0x3f80, float:2.278E-41)
        L_0x002f:
            r3 = r2
            goto L_0x006a
        L_0x0031:
            int r3 = r2 + 1
            byte r2 = r1[r2]
            int r2 = r2 << 21
            r0 = r0 ^ r2
            if (r0 >= 0) goto L_0x003f
            r1 = -2080896(0xffffffffffe03f80, float:NaN)
            r0 = r0 ^ r1
            goto L_0x006a
        L_0x003f:
            int r2 = r3 + 1
            byte r3 = r1[r3]
            int r4 = r3 << 28
            r0 = r0 ^ r4
            r4 = 266354560(0xfe03f80, float:2.2112565E-29)
            r0 = r0 ^ r4
            if (r3 >= 0) goto L_0x002f
            int r3 = r2 + 1
            byte r2 = r1[r2]
            if (r2 >= 0) goto L_0x006a
            int r2 = r3 + 1
            byte r3 = r1[r3]
            if (r3 >= 0) goto L_0x002f
            int r3 = r2 + 1
            byte r2 = r1[r2]
            if (r2 >= 0) goto L_0x006a
            int r2 = r3 + 1
            byte r3 = r1[r3]
            if (r3 >= 0) goto L_0x002f
            int r3 = r2 + 1
            byte r1 = r1[r2]
            if (r1 < 0) goto L_0x006d
        L_0x006a:
            r5.pos = r3
            return r0
        L_0x006d:
            long r0 = r5.zzuv()
            int r0 = (int) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.measurement.zzuq.zzuy():int");
    }

    private final long zzuz() throws IOException {
        long j;
        int i;
        long j2;
        long j3;
        long j4;
        int i2 = this.pos;
        if (this.limit != i2) {
            byte[] bArr = this.buffer;
            int i3 = i2 + 1;
            byte b = bArr[i2];
            if (b >= 0) {
                this.pos = i3;
                return (long) b;
            } else if (this.limit - i3 >= 9) {
                int i4 = i3 + 1;
                byte b2 = b ^ (bArr[i3] << 7);
                if (b2 < 0) {
                    j3 = (long) (b2 ^ ByteCompanionObject.MIN_VALUE);
                } else {
                    int i5 = i4 + 1;
                    byte b3 = b2 ^ (bArr[i4] << 14);
                    if (b3 >= 0) {
                        j4 = (long) (b3 ^ ByteCompanionObject.MIN_VALUE);
                        i = i5;
                        j = j4;
                        this.pos = i;
                        return j;
                    }
                    i4 = i5 + 1;
                    byte b4 = b3 ^ (bArr[i5] << 21);
                    if (b4 < 0) {
                        j3 = (long) (b4 ^ ByteCompanionObject.MIN_VALUE);
                    } else {
                        long j5 = (long) b4;
                        int i6 = i4 + 1;
                        long j6 = j5 ^ (((long) bArr[i4]) << 28);
                        if (j6 >= 0) {
                            j2 = j6 ^ 266354560;
                        } else {
                            int i7 = i6 + 1;
                            long j7 = j6 ^ (((long) bArr[i6]) << 35);
                            if (j7 < 0) {
                                j = j7 ^ -34093383808L;
                            } else {
                                i6 = i7 + 1;
                                long j8 = j7 ^ (((long) bArr[i7]) << 42);
                                if (j8 >= 0) {
                                    j2 = j8 ^ 4363953127296L;
                                } else {
                                    i7 = i6 + 1;
                                    long j9 = j8 ^ (((long) bArr[i6]) << 49);
                                    if (j9 < 0) {
                                        j = j9 ^ -558586000294016L;
                                    } else {
                                        int i8 = i7 + 1;
                                        long j10 = (j9 ^ (((long) bArr[i7]) << 56)) ^ 71499008037633920L;
                                        if (j10 < 0) {
                                            int i9 = i8 + 1;
                                            if (((long) bArr[i8]) >= 0) {
                                                i8 = i9;
                                            }
                                        }
                                        j = j10;
                                        this.pos = i;
                                        return j;
                                    }
                                }
                            }
                            i = i7;
                            this.pos = i;
                            return j;
                        }
                        j = j2;
                        this.pos = i;
                        return j;
                    }
                }
                j4 = j3;
                i = i4;
                j = j4;
                this.pos = i;
                return j;
            }
        }
        return zzuv();
    }

    /* access modifiers changed from: 0000 */
    public final long zzuv() throws IOException {
        long j = 0;
        int i = 0;
        while (i < 64) {
            byte zzvd = zzvd();
            long j2 = j | (((long) (zzvd & ByteCompanionObject.MAX_VALUE)) << i);
            if ((zzvd & ByteCompanionObject.MIN_VALUE) == 0) {
                return j2;
            }
            i += 7;
            j = j2;
        }
        throw zzvt.zzwm();
    }

    private final int zzva() throws IOException {
        int i = this.pos;
        if (this.limit - i < 4) {
            throw zzvt.zzwk();
        }
        byte[] bArr = this.buffer;
        this.pos = i + 4;
        return ((bArr[i + 3] & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 24) | (bArr[i] & Pdu.MANUFACTURER_DATA_PDU_TYPE) | ((bArr[i + 1] & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 8) | ((bArr[i + 2] & Pdu.MANUFACTURER_DATA_PDU_TYPE) << 16);
    }

    private final long zzvb() throws IOException {
        int i = this.pos;
        if (this.limit - i < 8) {
            throw zzvt.zzwk();
        }
        byte[] bArr = this.buffer;
        this.pos = i + 8;
        return (((long) bArr[i]) & 255) | ((((long) bArr[i + 1]) & 255) << 8) | ((((long) bArr[i + 2]) & 255) << 16) | ((((long) bArr[i + 3]) & 255) << 24) | ((((long) bArr[i + 4]) & 255) << 32) | ((((long) bArr[i + 5]) & 255) << 40) | ((((long) bArr[i + 6]) & 255) << 48) | ((((long) bArr[i + 7]) & 255) << 56);
    }

    public final int zzaq(int i) throws zzvt {
        if (i < 0) {
            throw zzvt.zzwl();
        }
        int zzux = i + zzux();
        int i2 = this.zzbuq;
        if (zzux > i2) {
            throw zzvt.zzwk();
        }
        this.zzbuq = zzux;
        zzvc();
        return i2;
    }

    private final void zzvc() {
        this.limit += this.zzbun;
        int i = this.limit - this.zzbuo;
        if (i > this.zzbuq) {
            this.zzbun = i - this.zzbuq;
            this.limit -= this.zzbun;
            return;
        }
        this.zzbun = 0;
    }

    public final void zzar(int i) {
        this.zzbuq = i;
        zzvc();
    }

    public final boolean zzuw() throws IOException {
        return this.pos == this.limit;
    }

    public final int zzux() {
        return this.pos - this.zzbuo;
    }

    private final byte zzvd() throws IOException {
        if (this.pos == this.limit) {
            throw zzvt.zzwk();
        }
        byte[] bArr = this.buffer;
        int i = this.pos;
        this.pos = i + 1;
        return bArr[i];
    }

    public final void zzas(int i) throws IOException {
        if (i >= 0 && i <= this.limit - this.pos) {
            this.pos += i;
        } else if (i < 0) {
            throw zzvt.zzwl();
        } else {
            throw zzvt.zzwk();
        }
    }
}
