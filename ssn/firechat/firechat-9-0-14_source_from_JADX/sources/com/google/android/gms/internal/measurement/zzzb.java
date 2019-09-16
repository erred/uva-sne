package com.google.android.gms.internal.measurement;

import com.google.android.gms.internal.measurement.zzza;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class zzzb<M extends zzza<M>, T> {
    public final int tag;
    private final int type;
    private final zzvm<?, ?> zzbyp;
    protected final Class<T> zzcfd;
    protected final boolean zzcfe;

    public static <M extends zzza<M>, T extends zzzg> zzzb<M, T> zza(int i, Class<T> cls, long j) {
        return new zzzb<>(11, cls, 810, false);
    }

    private zzzb(int i, Class<T> cls, int i2, boolean z) {
        this(11, cls, null, 810, false);
    }

    private zzzb(int i, Class<T> cls, zzvm<?, ?> zzvm, int i2, boolean z) {
        this.type = i;
        this.zzcfd = cls;
        this.tag = i2;
        this.zzcfe = false;
        this.zzbyp = null;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzzb)) {
            return false;
        }
        zzzb zzzb = (zzzb) obj;
        return this.type == zzzb.type && this.zzcfd == zzzb.zzcfd && this.tag == zzzb.tag && this.zzcfe == zzzb.zzcfe;
    }

    public final int hashCode() {
        return ((((((this.type + 1147) * 31) + this.zzcfd.hashCode()) * 31) + this.tag) * 31) + (this.zzcfe ? 1 : 0);
    }

    /* access modifiers changed from: 0000 */
    public final T zzah(List<zzzi> list) {
        if (list == null) {
            return null;
        }
        if (this.zzcfe) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                zzzi zzzi = (zzzi) list.get(i);
                if (zzzi.zzbug.length != 0) {
                    arrayList.add(zze(zzyx.zzn(zzzi.zzbug)));
                }
            }
            int size = arrayList.size();
            if (size == 0) {
                return null;
            }
            T cast = this.zzcfd.cast(Array.newInstance(this.zzcfd.getComponentType(), size));
            for (int i2 = 0; i2 < size; i2++) {
                Array.set(cast, i2, arrayList.get(i2));
            }
            return cast;
        } else if (list.isEmpty()) {
            return null;
        } else {
            return this.zzcfd.cast(zze(zzyx.zzn(((zzzi) list.get(list.size() - 1)).zzbug)));
        }
    }

    private final Object zze(zzyx zzyx) {
        Class<T> componentType = this.zzcfe ? this.zzcfd.getComponentType() : this.zzcfd;
        try {
            switch (this.type) {
                case 10:
                    zzzg zzzg = (zzzg) componentType.newInstance();
                    zzyx.zza(zzzg, this.tag >>> 3);
                    return zzzg;
                case 11:
                    zzzg zzzg2 = (zzzg) componentType.newInstance();
                    zzyx.zza(zzzg2);
                    return zzzg2;
                default:
                    int i = this.type;
                    StringBuilder sb = new StringBuilder(24);
                    sb.append("Unknown type ");
                    sb.append(i);
                    throw new IllegalArgumentException(sb.toString());
            }
        } catch (InstantiationException e) {
            String valueOf = String.valueOf(componentType);
            StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 33);
            sb2.append("Error creating instance of class ");
            sb2.append(valueOf);
            throw new IllegalArgumentException(sb2.toString(), e);
        } catch (IllegalAccessException e2) {
            String valueOf2 = String.valueOf(componentType);
            StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf2).length() + 33);
            sb3.append("Error creating instance of class ");
            sb3.append(valueOf2);
            throw new IllegalArgumentException(sb3.toString(), e2);
        } catch (IOException e3) {
            throw new IllegalArgumentException("Error reading extension field", e3);
        }
    }

    /* access modifiers changed from: protected */
    public final void zza(Object obj, zzyy zzyy) {
        try {
            zzyy.zzca(this.tag);
            switch (this.type) {
                case 10:
                    int i = this.tag >>> 3;
                    ((zzzg) obj).zza(zzyy);
                    zzyy.zzc(i, 4);
                    return;
                case 11:
                    zzyy.zzb((zzzg) obj);
                    return;
                default:
                    int i2 = this.type;
                    StringBuilder sb = new StringBuilder(24);
                    sb.append("Unknown type ");
                    sb.append(i2);
                    throw new IllegalArgumentException(sb.toString());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /* access modifiers changed from: protected */
    public final int zzak(Object obj) {
        int i = this.tag >>> 3;
        switch (this.type) {
            case 10:
                return (zzyy.zzbb(i) << 1) + ((zzzg) obj).zzvu();
            case 11:
                return zzyy.zzb(i, (zzzg) obj);
            default:
                int i2 = this.type;
                StringBuilder sb = new StringBuilder(24);
                sb.append("Unknown type ");
                sb.append(i2);
                throw new IllegalArgumentException(sb.toString());
        }
    }
}
