package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import org.altbeacon.bluetooth.Pdu;

public abstract class zzud implements Serializable, Iterable<Byte> {
    public static final zzud zzbtz = new zzum(zzvo.zzbzj);
    private static final zzui zzbua = (zzua.zzty() ? new zzun(null) : new zzug(null));
    private static final Comparator<zzud> zzbub = new zzuf();
    private int zzbry = 0;

    zzud() {
    }

    /* access modifiers changed from: private */
    public static int zza(byte b) {
        return b & Pdu.MANUFACTURER_DATA_PDU_TYPE;
    }

    public abstract boolean equals(Object obj);

    public abstract int size();

    /* access modifiers changed from: protected */
    public abstract int zza(int i, int i2, int i3);

    /* access modifiers changed from: protected */
    public abstract String zza(Charset charset);

    /* access modifiers changed from: 0000 */
    public abstract void zza(zzuc zzuc) throws IOException;

    public abstract byte zzal(int i);

    public abstract zzud zzb(int i, int i2);

    public abstract boolean zzub();

    public static zzud zzb(byte[] bArr, int i, int i2) {
        zzb(i, i + i2, bArr.length);
        return new zzum(zzbua.zzc(bArr, i, i2));
    }

    static zzud zzi(byte[] bArr) {
        return new zzum(bArr);
    }

    public static zzud zzfv(String str) {
        return new zzum(str.getBytes(zzvo.UTF_8));
    }

    public final String zzua() {
        return size() == 0 ? "" : zza(zzvo.UTF_8);
    }

    public final int hashCode() {
        int i = this.zzbry;
        if (i == 0) {
            int size = size();
            i = zza(size, 0, size);
            if (i == 0) {
                i = 1;
            }
            this.zzbry = i;
        }
        return i;
    }

    static zzuk zzam(int i) {
        return new zzuk(i, null);
    }

    /* access modifiers changed from: protected */
    public final int zzuc() {
        return this.zzbry;
    }

    static int zzb(int i, int i2, int i3) {
        int i4 = i2 - i;
        if ((i | i2 | i4 | (i3 - i2)) >= 0) {
            return i4;
        }
        if (i < 0) {
            StringBuilder sb = new StringBuilder(32);
            sb.append("Beginning index: ");
            sb.append(i);
            sb.append(" < 0");
            throw new IndexOutOfBoundsException(sb.toString());
        } else if (i2 < i) {
            StringBuilder sb2 = new StringBuilder(66);
            sb2.append("Beginning index larger than ending index: ");
            sb2.append(i);
            sb2.append(", ");
            sb2.append(i2);
            throw new IndexOutOfBoundsException(sb2.toString());
        } else {
            StringBuilder sb3 = new StringBuilder(37);
            sb3.append("End index: ");
            sb3.append(i2);
            sb3.append(" >= ");
            sb3.append(i3);
            throw new IndexOutOfBoundsException(sb3.toString());
        }
    }

    public final String toString() {
        return String.format("<ByteString@%s size=%d>", new Object[]{Integer.toHexString(System.identityHashCode(this)), Integer.valueOf(size())});
    }

    public /* synthetic */ Iterator iterator() {
        return new zzue(this);
    }
}
