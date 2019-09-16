package com.google.android.gms.internal.measurement;

import com.google.android.gms.internal.measurement.zzvm.zze;

public final class zzfq {

    public static final class zza extends zzvm<zza, C3228zza> implements zzwv {
        /* access modifiers changed from: private */
        public static final zza zzauq = new zza();
        private static volatile zzxd<zza> zznw;
        private String zzauo = "";
        private long zzaup;
        private int zznr;

        /* renamed from: com.google.android.gms.internal.measurement.zzfq$zza$zza reason: collision with other inner class name */
        public static final class C3228zza extends com.google.android.gms.internal.measurement.zzvm.zza<zza, C3228zza> implements zzwv {
            private C3228zza() {
                super(zza.zzauq);
            }

            /* synthetic */ C3228zza(zzfr zzfr) {
                this();
            }
        }

        private zza() {
        }

        /* access modifiers changed from: protected */
        public final Object zza(int i, Object obj, Object obj2) {
            switch (zzfr.zznq[i - 1]) {
                case 1:
                    return new zza();
                case 2:
                    return new C3228zza(null);
                case 3:
                    Object[] objArr = {"zznr", "zzauo", "zzaup"};
                    return zza((zzwt) zzauq, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0000\u0000\u0001\b\u0000\u0002\u0002\u0001", objArr);
                case 4:
                    return zzauq;
                case 5:
                    zzxd<zza> zzxd = zznw;
                    if (zzxd == null) {
                        synchronized (zza.class) {
                            zzxd = zznw;
                            if (zzxd == null) {
                                zzxd = new com.google.android.gms.internal.measurement.zzvm.zzb<>(zzauq);
                                zznw = zzxd;
                            }
                        }
                    }
                    return zzxd;
                case 6:
                    return Byte.valueOf(1);
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        static {
            zzvm.zza(zza.class, zzauq);
        }
    }

    public static final class zzb extends zzvm<zzb, zza> implements zzwv {
        /* access modifiers changed from: private */
        public static final zzb zzaut = new zzb();
        private static volatile zzxd<zzb> zznw;
        private int zzaur = 1;
        private zzvs<zza> zzaus = zzwc();
        private int zznr;

        public static final class zza extends com.google.android.gms.internal.measurement.zzvm.zza<zzb, zza> implements zzwv {
            private zza() {
                super(zzb.zzaut);
            }

            /* synthetic */ zza(zzfr zzfr) {
                this();
            }
        }

        /* renamed from: com.google.android.gms.internal.measurement.zzfq$zzb$zzb reason: collision with other inner class name */
        public enum C3229zzb implements zzvp {
            RADS(1),
            PROVISIONING(2);
            
            private static final zzvq<C3229zzb> zzoa = null;
            private final int value;

            public final int zzc() {
                return this.value;
            }

            public static C3229zzb zzs(int i) {
                switch (i) {
                    case 1:
                        return RADS;
                    case 2:
                        return PROVISIONING;
                    default:
                        return null;
                }
            }

            public static zzvr zzd() {
                return zzft.zzoc;
            }

            private C3229zzb(int i) {
                this.value = i;
            }

            static {
                zzoa = new zzfs();
            }
        }

        private zzb() {
        }

        /* access modifiers changed from: protected */
        public final Object zza(int i, Object obj, Object obj2) {
            switch (zzfr.zznq[i - 1]) {
                case 1:
                    return new zzb();
                case 2:
                    return new zza(null);
                case 3:
                    Object[] objArr = {"zznr", "zzaur", C3229zzb.zzd(), "zzaus", zza.class};
                    return zza((zzwt) zzaut, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001\f\u0000\u0002\u001b", objArr);
                case 4:
                    return zzaut;
                case 5:
                    zzxd<zzb> zzxd = zznw;
                    if (zzxd == null) {
                        synchronized (zzb.class) {
                            zzxd = zznw;
                            if (zzxd == null) {
                                zzxd = new com.google.android.gms.internal.measurement.zzvm.zzb<>(zzaut);
                                zznw = zzxd;
                            }
                        }
                    }
                    return zzxd;
                case 6:
                    return Byte.valueOf(1);
                case 7:
                    return null;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        public static zzxd<zzb> zza() {
            return (zzxd) zzaut.zza(zze.zzbyz, (Object) null, (Object) null);
        }

        static {
            zzvm.zza(zzb.class, zzaut);
        }
    }
}
