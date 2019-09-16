package com.google.android.gms.internal.measurement;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class zzwh extends zztz<Long> implements zzvs<Long>, zzxe, RandomAccess {
    private static final zzwh zzcam;
    private int size;
    private long[] zzcan;

    zzwh() {
        this(new long[10], 0);
    }

    private zzwh(long[] jArr, int i) {
        this.zzcan = jArr;
        this.size = i;
    }

    /* access modifiers changed from: protected */
    public final void removeRange(int i, int i2) {
        zztx();
        if (i2 < i) {
            throw new IndexOutOfBoundsException("toIndex < fromIndex");
        }
        System.arraycopy(this.zzcan, i2, this.zzcan, i, this.size - i2);
        this.size -= i2 - i;
        this.modCount++;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzwh)) {
            return super.equals(obj);
        }
        zzwh zzwh = (zzwh) obj;
        if (this.size != zzwh.size) {
            return false;
        }
        long[] jArr = zzwh.zzcan;
        for (int i = 0; i < this.size; i++) {
            if (this.zzcan[i] != jArr[i]) {
                return false;
            }
        }
        return true;
    }

    public final int hashCode() {
        int i = 1;
        for (int i2 = 0; i2 < this.size; i2++) {
            i = (i * 31) + zzvo.zzbf(this.zzcan[i2]);
        }
        return i;
    }

    public final long getLong(int i) {
        zzai(i);
        return this.zzcan[i];
    }

    public final int size() {
        return this.size;
    }

    public final void zzbg(long j) {
        zzk(this.size, j);
    }

    private final void zzk(int i, long j) {
        zztx();
        if (i < 0 || i > this.size) {
            throw new IndexOutOfBoundsException(zzaj(i));
        }
        if (this.size < this.zzcan.length) {
            System.arraycopy(this.zzcan, i, this.zzcan, i + 1, this.size - i);
        } else {
            long[] jArr = new long[(((this.size * 3) / 2) + 1)];
            System.arraycopy(this.zzcan, 0, jArr, 0, i);
            System.arraycopy(this.zzcan, i, jArr, i + 1, this.size - i);
            this.zzcan = jArr;
        }
        this.zzcan[i] = j;
        this.size++;
        this.modCount++;
    }

    public final boolean addAll(Collection<? extends Long> collection) {
        zztx();
        zzvo.checkNotNull(collection);
        if (!(collection instanceof zzwh)) {
            return super.addAll(collection);
        }
        zzwh zzwh = (zzwh) collection;
        if (zzwh.size == 0) {
            return false;
        }
        if (Integer.MAX_VALUE - this.size < zzwh.size) {
            throw new OutOfMemoryError();
        }
        int i = this.size + zzwh.size;
        if (i > this.zzcan.length) {
            this.zzcan = Arrays.copyOf(this.zzcan, i);
        }
        System.arraycopy(zzwh.zzcan, 0, this.zzcan, this.size, zzwh.size);
        this.size = i;
        this.modCount++;
        return true;
    }

    public final boolean remove(Object obj) {
        zztx();
        for (int i = 0; i < this.size; i++) {
            if (obj.equals(Long.valueOf(this.zzcan[i]))) {
                System.arraycopy(this.zzcan, i + 1, this.zzcan, i, this.size - i);
                this.size--;
                this.modCount++;
                return true;
            }
        }
        return false;
    }

    private final void zzai(int i) {
        if (i < 0 || i >= this.size) {
            throw new IndexOutOfBoundsException(zzaj(i));
        }
    }

    private final String zzaj(int i) {
        int i2 = this.size;
        StringBuilder sb = new StringBuilder(35);
        sb.append("Index:");
        sb.append(i);
        sb.append(", Size:");
        sb.append(i2);
        return sb.toString();
    }

    public final /* synthetic */ Object set(int i, Object obj) {
        long longValue = ((Long) obj).longValue();
        zztx();
        zzai(i);
        long j = this.zzcan[i];
        this.zzcan[i] = longValue;
        return Long.valueOf(j);
    }

    public final /* synthetic */ Object remove(int i) {
        zztx();
        zzai(i);
        long j = this.zzcan[i];
        if (i < this.size - 1) {
            System.arraycopy(this.zzcan, i + 1, this.zzcan, i, this.size - i);
        }
        this.size--;
        this.modCount++;
        return Long.valueOf(j);
    }

    public final /* synthetic */ void add(int i, Object obj) {
        zzk(i, ((Long) obj).longValue());
    }

    public final /* synthetic */ zzvs zzak(int i) {
        if (i >= this.size) {
            return new zzwh(Arrays.copyOf(this.zzcan, i), this.size);
        }
        throw new IllegalArgumentException();
    }

    public final /* synthetic */ Object get(int i) {
        return Long.valueOf(getLong(i));
    }

    static {
        zzwh zzwh = new zzwh();
        zzcam = zzwh;
        zzwh.zzsm();
    }
}
