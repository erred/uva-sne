package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.util.List;

final class zzur implements zzxi {
    private int tag;
    private final zzuo zzbur;
    private int zzbus;
    private int zzbut = 0;

    public static zzur zza(zzuo zzuo) {
        if (zzuo.zzbuk != null) {
            return zzuo.zzbuk;
        }
        return new zzur(zzuo);
    }

    private zzur(zzuo zzuo) {
        this.zzbur = (zzuo) zzvo.zza(zzuo, "input");
        this.zzbur.zzbuk = this;
    }

    public final int zzve() throws IOException {
        if (this.zzbut != 0) {
            this.tag = this.zzbut;
            this.zzbut = 0;
        } else {
            this.tag = this.zzbur.zzug();
        }
        if (this.tag == 0 || this.tag == this.zzbus) {
            return Integer.MAX_VALUE;
        }
        return this.tag >>> 3;
    }

    public final int getTag() {
        return this.tag;
    }

    public final boolean zzvf() throws IOException {
        if (this.zzbur.zzuw() || this.tag == this.zzbus) {
            return false;
        }
        return this.zzbur.zzao(this.tag);
    }

    private final void zzat(int i) throws IOException {
        if ((this.tag & 7) != i) {
            throw zzvt.zzwo();
        }
    }

    public final double readDouble() throws IOException {
        zzat(1);
        return this.zzbur.readDouble();
    }

    public final float readFloat() throws IOException {
        zzat(5);
        return this.zzbur.readFloat();
    }

    public final long zzuh() throws IOException {
        zzat(0);
        return this.zzbur.zzuh();
    }

    public final long zzui() throws IOException {
        zzat(0);
        return this.zzbur.zzui();
    }

    public final int zzuj() throws IOException {
        zzat(0);
        return this.zzbur.zzuj();
    }

    public final long zzuk() throws IOException {
        zzat(1);
        return this.zzbur.zzuk();
    }

    public final int zzul() throws IOException {
        zzat(5);
        return this.zzbur.zzul();
    }

    public final boolean zzum() throws IOException {
        zzat(0);
        return this.zzbur.zzum();
    }

    public final String readString() throws IOException {
        zzat(2);
        return this.zzbur.readString();
    }

    public final String zzun() throws IOException {
        zzat(2);
        return this.zzbur.zzun();
    }

    public final <T> T zza(zzxj<T> zzxj, zzuz zzuz) throws IOException {
        zzat(2);
        return zzc(zzxj, zzuz);
    }

    public final <T> T zzb(zzxj<T> zzxj, zzuz zzuz) throws IOException {
        zzat(3);
        return zzd(zzxj, zzuz);
    }

    private final <T> T zzc(zzxj<T> zzxj, zzuz zzuz) throws IOException {
        int zzup = this.zzbur.zzup();
        if (this.zzbur.zzbuh >= this.zzbur.zzbui) {
            throw zzvt.zzwp();
        }
        int zzaq = this.zzbur.zzaq(zzup);
        T newInstance = zzxj.newInstance();
        this.zzbur.zzbuh++;
        zzxj.zza(newInstance, this, zzuz);
        zzxj.zzu(newInstance);
        this.zzbur.zzan(0);
        this.zzbur.zzbuh--;
        this.zzbur.zzar(zzaq);
        return newInstance;
    }

    private final <T> T zzd(zzxj<T> zzxj, zzuz zzuz) throws IOException {
        int i = this.zzbus;
        this.zzbus = ((this.tag >>> 3) << 3) | 4;
        try {
            T newInstance = zzxj.newInstance();
            zzxj.zza(newInstance, this, zzuz);
            zzxj.zzu(newInstance);
            if (this.tag == this.zzbus) {
                return newInstance;
            }
            throw zzvt.zzwq();
        } finally {
            this.zzbus = i;
        }
    }

    public final zzud zzuo() throws IOException {
        zzat(2);
        return this.zzbur.zzuo();
    }

    public final int zzup() throws IOException {
        zzat(0);
        return this.zzbur.zzup();
    }

    public final int zzuq() throws IOException {
        zzat(0);
        return this.zzbur.zzuq();
    }

    public final int zzur() throws IOException {
        zzat(5);
        return this.zzbur.zzur();
    }

    public final long zzus() throws IOException {
        zzat(1);
        return this.zzbur.zzus();
    }

    public final int zzut() throws IOException {
        zzat(0);
        return this.zzbur.zzut();
    }

    public final long zzuu() throws IOException {
        zzat(0);
        return this.zzbur.zzuu();
    }

    public final void zzh(List<Double> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzuw) {
            zzuw zzuw = (zzuw) list;
            switch (this.tag & 7) {
                case 1:
                    break;
                case 2:
                    int zzup = this.zzbur.zzup();
                    zzau(zzup);
                    int zzux = this.zzbur.zzux() + zzup;
                    do {
                        zzuw.zzd(this.zzbur.readDouble());
                    } while (this.zzbur.zzux() < zzux);
                    return;
                default:
                    throw zzvt.zzwo();
            }
            do {
                zzuw.zzd(this.zzbur.readDouble());
                if (!this.zzbur.zzuw()) {
                    zzug2 = this.zzbur.zzug();
                } else {
                    return;
                }
            } while (zzug2 == this.tag);
            this.zzbut = zzug2;
            return;
        }
        switch (this.tag & 7) {
            case 1:
                break;
            case 2:
                int zzup2 = this.zzbur.zzup();
                zzau(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Double.valueOf(this.zzbur.readDouble()));
                } while (this.zzbur.zzux() < zzux2);
                return;
            default:
                throw zzvt.zzwo();
        }
        do {
            list.add(Double.valueOf(this.zzbur.readDouble()));
            if (!this.zzbur.zzuw()) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == this.tag);
        this.zzbut = zzug;
    }

    public final void zzi(List<Float> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvj) {
            zzvj zzvj = (zzvj) list;
            int i = this.tag & 7;
            if (i == 2) {
                int zzup = this.zzbur.zzup();
                zzav(zzup);
                int zzux = this.zzbur.zzux() + zzup;
                do {
                    zzvj.zzc(this.zzbur.readFloat());
                } while (this.zzbur.zzux() < zzux);
            } else if (i != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    zzvj.zzc(this.zzbur.readFloat());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 2) {
                int zzup2 = this.zzbur.zzup();
                zzav(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Float.valueOf(this.zzbur.readFloat()));
                } while (this.zzbur.zzux() < zzux2);
            } else if (i2 != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    list.add(Float.valueOf(this.zzbur.readFloat()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            }
        }
    }

    public final void zzj(List<Long> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzwh) {
            zzwh zzwh = (zzwh) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzwh.zzbg(this.zzbur.zzuh());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzwh.zzbg(this.zzbur.zzuh());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Long.valueOf(this.zzbur.zzuh()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Long.valueOf(this.zzbur.zzuh()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzk(List<Long> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzwh) {
            zzwh zzwh = (zzwh) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzwh.zzbg(this.zzbur.zzui());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzwh.zzbg(this.zzbur.zzui());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Long.valueOf(this.zzbur.zzui()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Long.valueOf(this.zzbur.zzui()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzl(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzvn.zzbm(this.zzbur.zzuj());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzvn.zzbm(this.zzbur.zzuj());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzuj()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Integer.valueOf(this.zzbur.zzuj()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzm(List<Long> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzwh) {
            zzwh zzwh = (zzwh) list;
            switch (this.tag & 7) {
                case 1:
                    break;
                case 2:
                    int zzup = this.zzbur.zzup();
                    zzau(zzup);
                    int zzux = this.zzbur.zzux() + zzup;
                    do {
                        zzwh.zzbg(this.zzbur.zzuk());
                    } while (this.zzbur.zzux() < zzux);
                    return;
                default:
                    throw zzvt.zzwo();
            }
            do {
                zzwh.zzbg(this.zzbur.zzuk());
                if (!this.zzbur.zzuw()) {
                    zzug2 = this.zzbur.zzug();
                } else {
                    return;
                }
            } while (zzug2 == this.tag);
            this.zzbut = zzug2;
            return;
        }
        switch (this.tag & 7) {
            case 1:
                break;
            case 2:
                int zzup2 = this.zzbur.zzup();
                zzau(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Long.valueOf(this.zzbur.zzuk()));
                } while (this.zzbur.zzux() < zzux2);
                return;
            default:
                throw zzvt.zzwo();
        }
        do {
            list.add(Long.valueOf(this.zzbur.zzuk()));
            if (!this.zzbur.zzuw()) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == this.tag);
        this.zzbut = zzug;
    }

    public final void zzn(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 2) {
                int zzup = this.zzbur.zzup();
                zzav(zzup);
                int zzux = this.zzbur.zzux() + zzup;
                do {
                    zzvn.zzbm(this.zzbur.zzul());
                } while (this.zzbur.zzux() < zzux);
            } else if (i != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    zzvn.zzbm(this.zzbur.zzul());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 2) {
                int zzup2 = this.zzbur.zzup();
                zzav(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Integer.valueOf(this.zzbur.zzul()));
                } while (this.zzbur.zzux() < zzux2);
            } else if (i2 != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzul()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            }
        }
    }

    public final void zzo(List<Boolean> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzub) {
            zzub zzub = (zzub) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzub.addBoolean(this.zzbur.zzum());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzub.addBoolean(this.zzbur.zzum());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Boolean.valueOf(this.zzbur.zzum()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Boolean.valueOf(this.zzbur.zzum()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void readStringList(List<String> list) throws IOException {
        zza(list, false);
    }

    public final void zzp(List<String> list) throws IOException {
        zza(list, true);
    }

    private final void zza(List<String> list, boolean z) throws IOException {
        int zzug;
        int zzug2;
        if ((this.tag & 7) != 2) {
            throw zzvt.zzwo();
        } else if (!(list instanceof zzwc) || z) {
            do {
                list.add(z ? zzun() : readString());
                if (!this.zzbur.zzuw()) {
                    zzug = this.zzbur.zzug();
                } else {
                    return;
                }
            } while (zzug == this.tag);
            this.zzbut = zzug;
        } else {
            zzwc zzwc = (zzwc) list;
            do {
                zzwc.zzc(zzuo());
                if (!this.zzbur.zzuw()) {
                    zzug2 = this.zzbur.zzug();
                } else {
                    return;
                }
            } while (zzug2 == this.tag);
            this.zzbut = zzug2;
        }
    }

    public final <T> void zza(List<T> list, zzxj<T> zzxj, zzuz zzuz) throws IOException {
        int zzug;
        if ((this.tag & 7) != 2) {
            throw zzvt.zzwo();
        }
        int i = this.tag;
        do {
            list.add(zzc(zzxj, zzuz));
            if (!this.zzbur.zzuw() && this.zzbut == 0) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == i);
        this.zzbut = zzug;
    }

    public final <T> void zzb(List<T> list, zzxj<T> zzxj, zzuz zzuz) throws IOException {
        int zzug;
        if ((this.tag & 7) != 3) {
            throw zzvt.zzwo();
        }
        int i = this.tag;
        do {
            list.add(zzd(zzxj, zzuz));
            if (!this.zzbur.zzuw() && this.zzbut == 0) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == i);
        this.zzbut = zzug;
    }

    public final void zzq(List<zzud> list) throws IOException {
        int zzug;
        if ((this.tag & 7) != 2) {
            throw zzvt.zzwo();
        }
        do {
            list.add(zzuo());
            if (!this.zzbur.zzuw()) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == this.tag);
        this.zzbut = zzug;
    }

    public final void zzr(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzvn.zzbm(this.zzbur.zzup());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzvn.zzbm(this.zzbur.zzup());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzup()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Integer.valueOf(this.zzbur.zzup()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzs(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzvn.zzbm(this.zzbur.zzuq());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzvn.zzbm(this.zzbur.zzuq());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzuq()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Integer.valueOf(this.zzbur.zzuq()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzt(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 2) {
                int zzup = this.zzbur.zzup();
                zzav(zzup);
                int zzux = this.zzbur.zzux() + zzup;
                do {
                    zzvn.zzbm(this.zzbur.zzur());
                } while (this.zzbur.zzux() < zzux);
            } else if (i != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    zzvn.zzbm(this.zzbur.zzur());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 2) {
                int zzup2 = this.zzbur.zzup();
                zzav(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Integer.valueOf(this.zzbur.zzur()));
                } while (this.zzbur.zzux() < zzux2);
            } else if (i2 != 5) {
                throw zzvt.zzwo();
            } else {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzur()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            }
        }
    }

    public final void zzu(List<Long> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzwh) {
            zzwh zzwh = (zzwh) list;
            switch (this.tag & 7) {
                case 1:
                    break;
                case 2:
                    int zzup = this.zzbur.zzup();
                    zzau(zzup);
                    int zzux = this.zzbur.zzux() + zzup;
                    do {
                        zzwh.zzbg(this.zzbur.zzus());
                    } while (this.zzbur.zzux() < zzux);
                    return;
                default:
                    throw zzvt.zzwo();
            }
            do {
                zzwh.zzbg(this.zzbur.zzus());
                if (!this.zzbur.zzuw()) {
                    zzug2 = this.zzbur.zzug();
                } else {
                    return;
                }
            } while (zzug2 == this.tag);
            this.zzbut = zzug2;
            return;
        }
        switch (this.tag & 7) {
            case 1:
                break;
            case 2:
                int zzup2 = this.zzbur.zzup();
                zzau(zzup2);
                int zzux2 = this.zzbur.zzux() + zzup2;
                do {
                    list.add(Long.valueOf(this.zzbur.zzus()));
                } while (this.zzbur.zzux() < zzux2);
                return;
            default:
                throw zzvt.zzwo();
        }
        do {
            list.add(Long.valueOf(this.zzbur.zzus()));
            if (!this.zzbur.zzuw()) {
                zzug = this.zzbur.zzug();
            } else {
                return;
            }
        } while (zzug == this.tag);
        this.zzbut = zzug;
    }

    public final void zzv(List<Integer> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzvn) {
            zzvn zzvn = (zzvn) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzvn.zzbm(this.zzbur.zzut());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzvn.zzbm(this.zzbur.zzut());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Integer.valueOf(this.zzbur.zzut()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Integer.valueOf(this.zzbur.zzut()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    public final void zzw(List<Long> list) throws IOException {
        int zzug;
        int zzug2;
        if (list instanceof zzwh) {
            zzwh zzwh = (zzwh) list;
            int i = this.tag & 7;
            if (i == 0) {
                do {
                    zzwh.zzbg(this.zzbur.zzuu());
                    if (!this.zzbur.zzuw()) {
                        zzug2 = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug2 == this.tag);
                this.zzbut = zzug2;
            } else if (i != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    zzwh.zzbg(this.zzbur.zzuu());
                } while (this.zzbur.zzux() < zzux);
                zzaw(zzux);
            }
        } else {
            int i2 = this.tag & 7;
            if (i2 == 0) {
                do {
                    list.add(Long.valueOf(this.zzbur.zzuu()));
                    if (!this.zzbur.zzuw()) {
                        zzug = this.zzbur.zzug();
                    } else {
                        return;
                    }
                } while (zzug == this.tag);
                this.zzbut = zzug;
            } else if (i2 != 2) {
                throw zzvt.zzwo();
            } else {
                int zzux2 = this.zzbur.zzux() + this.zzbur.zzup();
                do {
                    list.add(Long.valueOf(this.zzbur.zzuu()));
                } while (this.zzbur.zzux() < zzux2);
                zzaw(zzux2);
            }
        }
    }

    private static void zzau(int i) throws IOException {
        if ((i & 7) != 0) {
            throw zzvt.zzwq();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0052, code lost:
        if (zzvf() == false) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005b, code lost:
        throw new com.google.android.gms.internal.measurement.zzvt("Unable to parse map entry.");
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x004e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final <K, V> void zza(java.util.Map<K, V> r6, com.google.android.gms.internal.measurement.zzwm<K, V> r7, com.google.android.gms.internal.measurement.zzuz r8) throws java.io.IOException {
        /*
            r5 = this;
            r0 = 2
            r5.zzat(r0)
            com.google.android.gms.internal.measurement.zzuo r0 = r5.zzbur
            int r0 = r0.zzup()
            com.google.android.gms.internal.measurement.zzuo r1 = r5.zzbur
            int r0 = r1.zzaq(r0)
            K r1 = r7.zzcas
            V r2 = r7.zzbre
        L_0x0014:
            int r3 = r5.zzve()     // Catch:{ all -> 0x0065 }
            r4 = 2147483647(0x7fffffff, float:NaN)
            if (r3 == r4) goto L_0x005c
            com.google.android.gms.internal.measurement.zzuo r4 = r5.zzbur     // Catch:{ all -> 0x0065 }
            boolean r4 = r4.zzuw()     // Catch:{ all -> 0x0065 }
            if (r4 != 0) goto L_0x005c
            switch(r3) {
                case 1: goto L_0x003b;
                case 2: goto L_0x002d;
                default: goto L_0x0028;
            }
        L_0x0028:
            boolean r3 = r5.zzvf()     // Catch:{ zzvu -> 0x004e }
            goto L_0x0044
        L_0x002d:
            com.google.android.gms.internal.measurement.zzyq r3 = r7.zzcat     // Catch:{ zzvu -> 0x004e }
            V r4 = r7.zzbre     // Catch:{ zzvu -> 0x004e }
            java.lang.Class r4 = r4.getClass()     // Catch:{ zzvu -> 0x004e }
            java.lang.Object r3 = r5.zza(r3, r4, r8)     // Catch:{ zzvu -> 0x004e }
            r2 = r3
            goto L_0x0014
        L_0x003b:
            com.google.android.gms.internal.measurement.zzyq r3 = r7.zzcar     // Catch:{ zzvu -> 0x004e }
            r4 = 0
            java.lang.Object r3 = r5.zza(r3, r4, r4)     // Catch:{ zzvu -> 0x004e }
            r1 = r3
            goto L_0x0014
        L_0x0044:
            if (r3 != 0) goto L_0x0014
            com.google.android.gms.internal.measurement.zzvt r3 = new com.google.android.gms.internal.measurement.zzvt     // Catch:{ zzvu -> 0x004e }
            java.lang.String r4 = "Unable to parse map entry."
            r3.<init>(r4)     // Catch:{ zzvu -> 0x004e }
            throw r3     // Catch:{ zzvu -> 0x004e }
        L_0x004e:
            boolean r3 = r5.zzvf()     // Catch:{ all -> 0x0065 }
            if (r3 != 0) goto L_0x0014
            com.google.android.gms.internal.measurement.zzvt r6 = new com.google.android.gms.internal.measurement.zzvt     // Catch:{ all -> 0x0065 }
            java.lang.String r7 = "Unable to parse map entry."
            r6.<init>(r7)     // Catch:{ all -> 0x0065 }
            throw r6     // Catch:{ all -> 0x0065 }
        L_0x005c:
            r6.put(r1, r2)     // Catch:{ all -> 0x0065 }
            com.google.android.gms.internal.measurement.zzuo r6 = r5.zzbur
            r6.zzar(r0)
            return
        L_0x0065:
            r6 = move-exception
            com.google.android.gms.internal.measurement.zzuo r7 = r5.zzbur
            r7.zzar(r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.measurement.zzur.zza(java.util.Map, com.google.android.gms.internal.measurement.zzwm, com.google.android.gms.internal.measurement.zzuz):void");
    }

    private final Object zza(zzyq zzyq, Class<?> cls, zzuz zzuz) throws IOException {
        switch (zzyq) {
            case BOOL:
                return Boolean.valueOf(zzum());
            case BYTES:
                return zzuo();
            case DOUBLE:
                return Double.valueOf(readDouble());
            case ENUM:
                return Integer.valueOf(zzuq());
            case FIXED32:
                return Integer.valueOf(zzul());
            case FIXED64:
                return Long.valueOf(zzuk());
            case FLOAT:
                return Float.valueOf(readFloat());
            case INT32:
                return Integer.valueOf(zzuj());
            case INT64:
                return Long.valueOf(zzui());
            case MESSAGE:
                zzat(2);
                return zzc(zzxf.zzxn().zzi(cls), zzuz);
            case SFIXED32:
                return Integer.valueOf(zzur());
            case SFIXED64:
                return Long.valueOf(zzus());
            case SINT32:
                return Integer.valueOf(zzut());
            case SINT64:
                return Long.valueOf(zzuu());
            case STRING:
                return zzun();
            case UINT32:
                return Integer.valueOf(zzup());
            case UINT64:
                return Long.valueOf(zzuh());
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    private static void zzav(int i) throws IOException {
        if ((i & 3) != 0) {
            throw zzvt.zzwq();
        }
    }

    private final void zzaw(int i) throws IOException {
        if (this.zzbur.zzux() != i) {
            throw zzvt.zzwk();
        }
    }
}
