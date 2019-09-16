package com.google.android.gms.internal.measurement;

import com.facebook.imageutils.JfifUtil;
import com.google.android.gms.internal.measurement.zzfq.zzb;
import java.io.IOException;
import org.matrix.olm.OlmException;

public final class zzgi extends zzza<zzgi> {
    private static volatile zzgi[] zzawz;
    public String zzafw;
    public String zzafx;
    public String zzafz;
    public String zzage;
    public String zzagv;
    public String zzaia;
    public String zzawj;
    public Integer zzaxa;
    public zzgf[] zzaxb;
    public zzgl[] zzaxc;
    public Long zzaxd;
    public Long zzaxe;
    public Long zzaxf;
    public Long zzaxg;
    public Long zzaxh;
    public String zzaxi;
    public String zzaxj;
    public String zzaxk;
    public Integer zzaxl;
    public Long zzaxm;
    public Long zzaxn;
    public String zzaxo;
    public Boolean zzaxp;
    public Long zzaxq;
    public Integer zzaxr;
    public Boolean zzaxs;
    public zzgd[] zzaxt;
    public Integer zzaxu;
    private Integer zzaxv;
    private Integer zzaxw;
    public String zzaxx;
    public Long zzaxy;
    public Long zzaxz;
    public String zzaya;
    private String zzayb;
    public Integer zzayc;
    private zzb zzayd;
    public String zzts;
    public String zztt;

    public static zzgi[] zzms() {
        if (zzawz == null) {
            synchronized (zzze.zzcfl) {
                if (zzawz == null) {
                    zzawz = new zzgi[0];
                }
            }
        }
        return zzawz;
    }

    public zzgi() {
        this.zzaxa = null;
        this.zzaxb = zzgf.zzmq();
        this.zzaxc = zzgl.zzmu();
        this.zzaxd = null;
        this.zzaxe = null;
        this.zzaxf = null;
        this.zzaxg = null;
        this.zzaxh = null;
        this.zzaxi = null;
        this.zzaxj = null;
        this.zzaxk = null;
        this.zzaia = null;
        this.zzaxl = null;
        this.zzage = null;
        this.zztt = null;
        this.zzts = null;
        this.zzaxm = null;
        this.zzaxn = null;
        this.zzaxo = null;
        this.zzaxp = null;
        this.zzafw = null;
        this.zzaxq = null;
        this.zzaxr = null;
        this.zzagv = null;
        this.zzafx = null;
        this.zzaxs = null;
        this.zzaxt = zzgd.zzmo();
        this.zzafz = null;
        this.zzaxu = null;
        this.zzaxv = null;
        this.zzaxw = null;
        this.zzaxx = null;
        this.zzaxy = null;
        this.zzaxz = null;
        this.zzaya = null;
        this.zzayb = null;
        this.zzayc = null;
        this.zzawj = null;
        this.zzayd = null;
        this.zzcfc = null;
        this.zzcfm = -1;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof zzgi)) {
            return false;
        }
        zzgi zzgi = (zzgi) obj;
        if (this.zzaxa == null) {
            if (zzgi.zzaxa != null) {
                return false;
            }
        } else if (!this.zzaxa.equals(zzgi.zzaxa)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzaxb, (Object[]) zzgi.zzaxb) || !zzze.equals((Object[]) this.zzaxc, (Object[]) zzgi.zzaxc)) {
            return false;
        }
        if (this.zzaxd == null) {
            if (zzgi.zzaxd != null) {
                return false;
            }
        } else if (!this.zzaxd.equals(zzgi.zzaxd)) {
            return false;
        }
        if (this.zzaxe == null) {
            if (zzgi.zzaxe != null) {
                return false;
            }
        } else if (!this.zzaxe.equals(zzgi.zzaxe)) {
            return false;
        }
        if (this.zzaxf == null) {
            if (zzgi.zzaxf != null) {
                return false;
            }
        } else if (!this.zzaxf.equals(zzgi.zzaxf)) {
            return false;
        }
        if (this.zzaxg == null) {
            if (zzgi.zzaxg != null) {
                return false;
            }
        } else if (!this.zzaxg.equals(zzgi.zzaxg)) {
            return false;
        }
        if (this.zzaxh == null) {
            if (zzgi.zzaxh != null) {
                return false;
            }
        } else if (!this.zzaxh.equals(zzgi.zzaxh)) {
            return false;
        }
        if (this.zzaxi == null) {
            if (zzgi.zzaxi != null) {
                return false;
            }
        } else if (!this.zzaxi.equals(zzgi.zzaxi)) {
            return false;
        }
        if (this.zzaxj == null) {
            if (zzgi.zzaxj != null) {
                return false;
            }
        } else if (!this.zzaxj.equals(zzgi.zzaxj)) {
            return false;
        }
        if (this.zzaxk == null) {
            if (zzgi.zzaxk != null) {
                return false;
            }
        } else if (!this.zzaxk.equals(zzgi.zzaxk)) {
            return false;
        }
        if (this.zzaia == null) {
            if (zzgi.zzaia != null) {
                return false;
            }
        } else if (!this.zzaia.equals(zzgi.zzaia)) {
            return false;
        }
        if (this.zzaxl == null) {
            if (zzgi.zzaxl != null) {
                return false;
            }
        } else if (!this.zzaxl.equals(zzgi.zzaxl)) {
            return false;
        }
        if (this.zzage == null) {
            if (zzgi.zzage != null) {
                return false;
            }
        } else if (!this.zzage.equals(zzgi.zzage)) {
            return false;
        }
        if (this.zztt == null) {
            if (zzgi.zztt != null) {
                return false;
            }
        } else if (!this.zztt.equals(zzgi.zztt)) {
            return false;
        }
        if (this.zzts == null) {
            if (zzgi.zzts != null) {
                return false;
            }
        } else if (!this.zzts.equals(zzgi.zzts)) {
            return false;
        }
        if (this.zzaxm == null) {
            if (zzgi.zzaxm != null) {
                return false;
            }
        } else if (!this.zzaxm.equals(zzgi.zzaxm)) {
            return false;
        }
        if (this.zzaxn == null) {
            if (zzgi.zzaxn != null) {
                return false;
            }
        } else if (!this.zzaxn.equals(zzgi.zzaxn)) {
            return false;
        }
        if (this.zzaxo == null) {
            if (zzgi.zzaxo != null) {
                return false;
            }
        } else if (!this.zzaxo.equals(zzgi.zzaxo)) {
            return false;
        }
        if (this.zzaxp == null) {
            if (zzgi.zzaxp != null) {
                return false;
            }
        } else if (!this.zzaxp.equals(zzgi.zzaxp)) {
            return false;
        }
        if (this.zzafw == null) {
            if (zzgi.zzafw != null) {
                return false;
            }
        } else if (!this.zzafw.equals(zzgi.zzafw)) {
            return false;
        }
        if (this.zzaxq == null) {
            if (zzgi.zzaxq != null) {
                return false;
            }
        } else if (!this.zzaxq.equals(zzgi.zzaxq)) {
            return false;
        }
        if (this.zzaxr == null) {
            if (zzgi.zzaxr != null) {
                return false;
            }
        } else if (!this.zzaxr.equals(zzgi.zzaxr)) {
            return false;
        }
        if (this.zzagv == null) {
            if (zzgi.zzagv != null) {
                return false;
            }
        } else if (!this.zzagv.equals(zzgi.zzagv)) {
            return false;
        }
        if (this.zzafx == null) {
            if (zzgi.zzafx != null) {
                return false;
            }
        } else if (!this.zzafx.equals(zzgi.zzafx)) {
            return false;
        }
        if (this.zzaxs == null) {
            if (zzgi.zzaxs != null) {
                return false;
            }
        } else if (!this.zzaxs.equals(zzgi.zzaxs)) {
            return false;
        }
        if (!zzze.equals((Object[]) this.zzaxt, (Object[]) zzgi.zzaxt)) {
            return false;
        }
        if (this.zzafz == null) {
            if (zzgi.zzafz != null) {
                return false;
            }
        } else if (!this.zzafz.equals(zzgi.zzafz)) {
            return false;
        }
        if (this.zzaxu == null) {
            if (zzgi.zzaxu != null) {
                return false;
            }
        } else if (!this.zzaxu.equals(zzgi.zzaxu)) {
            return false;
        }
        if (this.zzaxv == null) {
            if (zzgi.zzaxv != null) {
                return false;
            }
        } else if (!this.zzaxv.equals(zzgi.zzaxv)) {
            return false;
        }
        if (this.zzaxw == null) {
            if (zzgi.zzaxw != null) {
                return false;
            }
        } else if (!this.zzaxw.equals(zzgi.zzaxw)) {
            return false;
        }
        if (this.zzaxx == null) {
            if (zzgi.zzaxx != null) {
                return false;
            }
        } else if (!this.zzaxx.equals(zzgi.zzaxx)) {
            return false;
        }
        if (this.zzaxy == null) {
            if (zzgi.zzaxy != null) {
                return false;
            }
        } else if (!this.zzaxy.equals(zzgi.zzaxy)) {
            return false;
        }
        if (this.zzaxz == null) {
            if (zzgi.zzaxz != null) {
                return false;
            }
        } else if (!this.zzaxz.equals(zzgi.zzaxz)) {
            return false;
        }
        if (this.zzaya == null) {
            if (zzgi.zzaya != null) {
                return false;
            }
        } else if (!this.zzaya.equals(zzgi.zzaya)) {
            return false;
        }
        if (this.zzayb == null) {
            if (zzgi.zzayb != null) {
                return false;
            }
        } else if (!this.zzayb.equals(zzgi.zzayb)) {
            return false;
        }
        if (this.zzayc == null) {
            if (zzgi.zzayc != null) {
                return false;
            }
        } else if (!this.zzayc.equals(zzgi.zzayc)) {
            return false;
        }
        if (this.zzawj == null) {
            if (zzgi.zzawj != null) {
                return false;
            }
        } else if (!this.zzawj.equals(zzgi.zzawj)) {
            return false;
        }
        if (this.zzayd == null) {
            if (zzgi.zzayd != null) {
                return false;
            }
        } else if (!this.zzayd.equals(zzgi.zzayd)) {
            return false;
        }
        if (this.zzcfc == null || this.zzcfc.isEmpty()) {
            return zzgi.zzcfc == null || zzgi.zzcfc.isEmpty();
        }
        return this.zzcfc.equals(zzgi.zzcfc);
    }

    public final int hashCode() {
        int i;
        int i2 = 0;
        int hashCode = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((getClass().getName().hashCode() + 527) * 31) + (this.zzaxa == null ? 0 : this.zzaxa.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzaxb)) * 31) + zzze.hashCode((Object[]) this.zzaxc)) * 31) + (this.zzaxd == null ? 0 : this.zzaxd.hashCode())) * 31) + (this.zzaxe == null ? 0 : this.zzaxe.hashCode())) * 31) + (this.zzaxf == null ? 0 : this.zzaxf.hashCode())) * 31) + (this.zzaxg == null ? 0 : this.zzaxg.hashCode())) * 31) + (this.zzaxh == null ? 0 : this.zzaxh.hashCode())) * 31) + (this.zzaxi == null ? 0 : this.zzaxi.hashCode())) * 31) + (this.zzaxj == null ? 0 : this.zzaxj.hashCode())) * 31) + (this.zzaxk == null ? 0 : this.zzaxk.hashCode())) * 31) + (this.zzaia == null ? 0 : this.zzaia.hashCode())) * 31) + (this.zzaxl == null ? 0 : this.zzaxl.hashCode())) * 31) + (this.zzage == null ? 0 : this.zzage.hashCode())) * 31) + (this.zztt == null ? 0 : this.zztt.hashCode())) * 31) + (this.zzts == null ? 0 : this.zzts.hashCode())) * 31) + (this.zzaxm == null ? 0 : this.zzaxm.hashCode())) * 31) + (this.zzaxn == null ? 0 : this.zzaxn.hashCode())) * 31) + (this.zzaxo == null ? 0 : this.zzaxo.hashCode())) * 31) + (this.zzaxp == null ? 0 : this.zzaxp.hashCode())) * 31) + (this.zzafw == null ? 0 : this.zzafw.hashCode())) * 31) + (this.zzaxq == null ? 0 : this.zzaxq.hashCode())) * 31) + (this.zzaxr == null ? 0 : this.zzaxr.hashCode())) * 31) + (this.zzagv == null ? 0 : this.zzagv.hashCode())) * 31) + (this.zzafx == null ? 0 : this.zzafx.hashCode())) * 31) + (this.zzaxs == null ? 0 : this.zzaxs.hashCode())) * 31) + zzze.hashCode((Object[]) this.zzaxt)) * 31) + (this.zzafz == null ? 0 : this.zzafz.hashCode())) * 31) + (this.zzaxu == null ? 0 : this.zzaxu.hashCode())) * 31) + (this.zzaxv == null ? 0 : this.zzaxv.hashCode())) * 31) + (this.zzaxw == null ? 0 : this.zzaxw.hashCode())) * 31) + (this.zzaxx == null ? 0 : this.zzaxx.hashCode())) * 31) + (this.zzaxy == null ? 0 : this.zzaxy.hashCode())) * 31) + (this.zzaxz == null ? 0 : this.zzaxz.hashCode())) * 31) + (this.zzaya == null ? 0 : this.zzaya.hashCode())) * 31) + (this.zzayb == null ? 0 : this.zzayb.hashCode())) * 31) + (this.zzayc == null ? 0 : this.zzayc.hashCode())) * 31) + (this.zzawj == null ? 0 : this.zzawj.hashCode());
        zzb zzb = this.zzayd;
        int i3 = hashCode * 31;
        if (zzb == null) {
            i = 0;
        } else {
            i = zzb.hashCode();
        }
        int i4 = (i3 + i) * 31;
        if (this.zzcfc != null && !this.zzcfc.isEmpty()) {
            i2 = this.zzcfc.hashCode();
        }
        return i4 + i2;
    }

    public final void zza(zzyy zzyy) throws IOException {
        if (this.zzaxa != null) {
            zzyy.zzd(1, this.zzaxa.intValue());
        }
        if (this.zzaxb != null && this.zzaxb.length > 0) {
            for (zzgf zzgf : this.zzaxb) {
                if (zzgf != null) {
                    zzyy.zza(2, (zzzg) zzgf);
                }
            }
        }
        if (this.zzaxc != null && this.zzaxc.length > 0) {
            for (zzgl zzgl : this.zzaxc) {
                if (zzgl != null) {
                    zzyy.zza(3, (zzzg) zzgl);
                }
            }
        }
        if (this.zzaxd != null) {
            zzyy.zzi(4, this.zzaxd.longValue());
        }
        if (this.zzaxe != null) {
            zzyy.zzi(5, this.zzaxe.longValue());
        }
        if (this.zzaxf != null) {
            zzyy.zzi(6, this.zzaxf.longValue());
        }
        if (this.zzaxh != null) {
            zzyy.zzi(7, this.zzaxh.longValue());
        }
        if (this.zzaxi != null) {
            zzyy.zzb(8, this.zzaxi);
        }
        if (this.zzaxj != null) {
            zzyy.zzb(9, this.zzaxj);
        }
        if (this.zzaxk != null) {
            zzyy.zzb(10, this.zzaxk);
        }
        if (this.zzaia != null) {
            zzyy.zzb(11, this.zzaia);
        }
        if (this.zzaxl != null) {
            zzyy.zzd(12, this.zzaxl.intValue());
        }
        if (this.zzage != null) {
            zzyy.zzb(13, this.zzage);
        }
        if (this.zztt != null) {
            zzyy.zzb(14, this.zztt);
        }
        if (this.zzts != null) {
            zzyy.zzb(16, this.zzts);
        }
        if (this.zzaxm != null) {
            zzyy.zzi(17, this.zzaxm.longValue());
        }
        if (this.zzaxn != null) {
            zzyy.zzi(18, this.zzaxn.longValue());
        }
        if (this.zzaxo != null) {
            zzyy.zzb(19, this.zzaxo);
        }
        if (this.zzaxp != null) {
            zzyy.zzb(20, this.zzaxp.booleanValue());
        }
        if (this.zzafw != null) {
            zzyy.zzb(21, this.zzafw);
        }
        if (this.zzaxq != null) {
            zzyy.zzi(22, this.zzaxq.longValue());
        }
        if (this.zzaxr != null) {
            zzyy.zzd(23, this.zzaxr.intValue());
        }
        if (this.zzagv != null) {
            zzyy.zzb(24, this.zzagv);
        }
        if (this.zzafx != null) {
            zzyy.zzb(25, this.zzafx);
        }
        if (this.zzaxg != null) {
            zzyy.zzi(26, this.zzaxg.longValue());
        }
        if (this.zzaxs != null) {
            zzyy.zzb(28, this.zzaxs.booleanValue());
        }
        if (this.zzaxt != null && this.zzaxt.length > 0) {
            for (zzgd zzgd : this.zzaxt) {
                if (zzgd != null) {
                    zzyy.zza(29, (zzzg) zzgd);
                }
            }
        }
        if (this.zzafz != null) {
            zzyy.zzb(30, this.zzafz);
        }
        if (this.zzaxu != null) {
            zzyy.zzd(31, this.zzaxu.intValue());
        }
        if (this.zzaxv != null) {
            zzyy.zzd(32, this.zzaxv.intValue());
        }
        if (this.zzaxw != null) {
            zzyy.zzd(33, this.zzaxw.intValue());
        }
        if (this.zzaxx != null) {
            zzyy.zzb(34, this.zzaxx);
        }
        if (this.zzaxy != null) {
            zzyy.zzi(35, this.zzaxy.longValue());
        }
        if (this.zzaxz != null) {
            zzyy.zzi(36, this.zzaxz.longValue());
        }
        if (this.zzaya != null) {
            zzyy.zzb(37, this.zzaya);
        }
        if (this.zzayb != null) {
            zzyy.zzb(38, this.zzayb);
        }
        if (this.zzayc != null) {
            zzyy.zzd(39, this.zzayc.intValue());
        }
        if (this.zzawj != null) {
            zzyy.zzb(41, this.zzawj);
        }
        if (this.zzayd != null) {
            zzyy.zze(44, this.zzayd);
        }
        super.zza(zzyy);
    }

    /* access modifiers changed from: protected */
    public final int zzf() {
        int zzf = super.zzf();
        if (this.zzaxa != null) {
            zzf += zzyy.zzh(1, this.zzaxa.intValue());
        }
        if (this.zzaxb != null && this.zzaxb.length > 0) {
            int i = zzf;
            for (zzgf zzgf : this.zzaxb) {
                if (zzgf != null) {
                    i += zzyy.zzb(2, (zzzg) zzgf);
                }
            }
            zzf = i;
        }
        if (this.zzaxc != null && this.zzaxc.length > 0) {
            int i2 = zzf;
            for (zzgl zzgl : this.zzaxc) {
                if (zzgl != null) {
                    i2 += zzyy.zzb(3, (zzzg) zzgl);
                }
            }
            zzf = i2;
        }
        if (this.zzaxd != null) {
            zzf += zzyy.zzd(4, this.zzaxd.longValue());
        }
        if (this.zzaxe != null) {
            zzf += zzyy.zzd(5, this.zzaxe.longValue());
        }
        if (this.zzaxf != null) {
            zzf += zzyy.zzd(6, this.zzaxf.longValue());
        }
        if (this.zzaxh != null) {
            zzf += zzyy.zzd(7, this.zzaxh.longValue());
        }
        if (this.zzaxi != null) {
            zzf += zzyy.zzc(8, this.zzaxi);
        }
        if (this.zzaxj != null) {
            zzf += zzyy.zzc(9, this.zzaxj);
        }
        if (this.zzaxk != null) {
            zzf += zzyy.zzc(10, this.zzaxk);
        }
        if (this.zzaia != null) {
            zzf += zzyy.zzc(11, this.zzaia);
        }
        if (this.zzaxl != null) {
            zzf += zzyy.zzh(12, this.zzaxl.intValue());
        }
        if (this.zzage != null) {
            zzf += zzyy.zzc(13, this.zzage);
        }
        if (this.zztt != null) {
            zzf += zzyy.zzc(14, this.zztt);
        }
        if (this.zzts != null) {
            zzf += zzyy.zzc(16, this.zzts);
        }
        if (this.zzaxm != null) {
            zzf += zzyy.zzd(17, this.zzaxm.longValue());
        }
        if (this.zzaxn != null) {
            zzf += zzyy.zzd(18, this.zzaxn.longValue());
        }
        if (this.zzaxo != null) {
            zzf += zzyy.zzc(19, this.zzaxo);
        }
        if (this.zzaxp != null) {
            this.zzaxp.booleanValue();
            zzf += zzyy.zzbb(20) + 1;
        }
        if (this.zzafw != null) {
            zzf += zzyy.zzc(21, this.zzafw);
        }
        if (this.zzaxq != null) {
            zzf += zzyy.zzd(22, this.zzaxq.longValue());
        }
        if (this.zzaxr != null) {
            zzf += zzyy.zzh(23, this.zzaxr.intValue());
        }
        if (this.zzagv != null) {
            zzf += zzyy.zzc(24, this.zzagv);
        }
        if (this.zzafx != null) {
            zzf += zzyy.zzc(25, this.zzafx);
        }
        if (this.zzaxg != null) {
            zzf += zzyy.zzd(26, this.zzaxg.longValue());
        }
        if (this.zzaxs != null) {
            this.zzaxs.booleanValue();
            zzf += zzyy.zzbb(28) + 1;
        }
        if (this.zzaxt != null && this.zzaxt.length > 0) {
            for (zzgd zzgd : this.zzaxt) {
                if (zzgd != null) {
                    zzf += zzyy.zzb(29, (zzzg) zzgd);
                }
            }
        }
        if (this.zzafz != null) {
            zzf += zzyy.zzc(30, this.zzafz);
        }
        if (this.zzaxu != null) {
            zzf += zzyy.zzh(31, this.zzaxu.intValue());
        }
        if (this.zzaxv != null) {
            zzf += zzyy.zzh(32, this.zzaxv.intValue());
        }
        if (this.zzaxw != null) {
            zzf += zzyy.zzh(33, this.zzaxw.intValue());
        }
        if (this.zzaxx != null) {
            zzf += zzyy.zzc(34, this.zzaxx);
        }
        if (this.zzaxy != null) {
            zzf += zzyy.zzd(35, this.zzaxy.longValue());
        }
        if (this.zzaxz != null) {
            zzf += zzyy.zzd(36, this.zzaxz.longValue());
        }
        if (this.zzaya != null) {
            zzf += zzyy.zzc(37, this.zzaya);
        }
        if (this.zzayb != null) {
            zzf += zzyy.zzc(38, this.zzayb);
        }
        if (this.zzayc != null) {
            zzf += zzyy.zzh(39, this.zzayc.intValue());
        }
        if (this.zzawj != null) {
            zzf += zzyy.zzc(41, this.zzawj);
        }
        return this.zzayd != null ? zzf + zzut.zzc(44, (zzwt) this.zzayd) : zzf;
    }

    public final /* synthetic */ zzzg zza(zzyx zzyx) throws IOException {
        while (true) {
            int zzug = zzyx.zzug();
            switch (zzug) {
                case 0:
                    return this;
                case 8:
                    this.zzaxa = Integer.valueOf(zzyx.zzuy());
                    break;
                case 18:
                    int zzb = zzzj.zzb(zzyx, 18);
                    int length = this.zzaxb == null ? 0 : this.zzaxb.length;
                    zzgf[] zzgfArr = new zzgf[(zzb + length)];
                    if (length != 0) {
                        System.arraycopy(this.zzaxb, 0, zzgfArr, 0, length);
                    }
                    while (length < zzgfArr.length - 1) {
                        zzgfArr[length] = new zzgf();
                        zzyx.zza((zzzg) zzgfArr[length]);
                        zzyx.zzug();
                        length++;
                    }
                    zzgfArr[length] = new zzgf();
                    zzyx.zza((zzzg) zzgfArr[length]);
                    this.zzaxb = zzgfArr;
                    break;
                case 26:
                    int zzb2 = zzzj.zzb(zzyx, 26);
                    int length2 = this.zzaxc == null ? 0 : this.zzaxc.length;
                    zzgl[] zzglArr = new zzgl[(zzb2 + length2)];
                    if (length2 != 0) {
                        System.arraycopy(this.zzaxc, 0, zzglArr, 0, length2);
                    }
                    while (length2 < zzglArr.length - 1) {
                        zzglArr[length2] = new zzgl();
                        zzyx.zza((zzzg) zzglArr[length2]);
                        zzyx.zzug();
                        length2++;
                    }
                    zzglArr[length2] = new zzgl();
                    zzyx.zza((zzzg) zzglArr[length2]);
                    this.zzaxc = zzglArr;
                    break;
                case 32:
                    this.zzaxd = Long.valueOf(zzyx.zzuz());
                    break;
                case 40:
                    this.zzaxe = Long.valueOf(zzyx.zzuz());
                    break;
                case 48:
                    this.zzaxf = Long.valueOf(zzyx.zzuz());
                    break;
                case 56:
                    this.zzaxh = Long.valueOf(zzyx.zzuz());
                    break;
                case 66:
                    this.zzaxi = zzyx.readString();
                    break;
                case 74:
                    this.zzaxj = zzyx.readString();
                    break;
                case 82:
                    this.zzaxk = zzyx.readString();
                    break;
                case 90:
                    this.zzaia = zzyx.readString();
                    break;
                case 96:
                    this.zzaxl = Integer.valueOf(zzyx.zzuy());
                    break;
                case 106:
                    this.zzage = zzyx.readString();
                    break;
                case 114:
                    this.zztt = zzyx.readString();
                    break;
                case 130:
                    this.zzts = zzyx.readString();
                    break;
                case 136:
                    this.zzaxm = Long.valueOf(zzyx.zzuz());
                    break;
                case 144:
                    this.zzaxn = Long.valueOf(zzyx.zzuz());
                    break;
                case 154:
                    this.zzaxo = zzyx.readString();
                    break;
                case 160:
                    this.zzaxp = Boolean.valueOf(zzyx.zzum());
                    break;
                case 170:
                    this.zzafw = zzyx.readString();
                    break;
                case 176:
                    this.zzaxq = Long.valueOf(zzyx.zzuz());
                    break;
                case 184:
                    this.zzaxr = Integer.valueOf(zzyx.zzuy());
                    break;
                case 194:
                    this.zzagv = zzyx.readString();
                    break;
                case OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_IDENTIFIER /*202*/:
                    this.zzafx = zzyx.readString();
                    break;
                case JfifUtil.MARKER_RST0 /*208*/:
                    this.zzaxg = Long.valueOf(zzyx.zzuz());
                    break;
                case 224:
                    this.zzaxs = Boolean.valueOf(zzyx.zzum());
                    break;
                case 234:
                    int zzb3 = zzzj.zzb(zzyx, 234);
                    int length3 = this.zzaxt == null ? 0 : this.zzaxt.length;
                    zzgd[] zzgdArr = new zzgd[(zzb3 + length3)];
                    if (length3 != 0) {
                        System.arraycopy(this.zzaxt, 0, zzgdArr, 0, length3);
                    }
                    while (length3 < zzgdArr.length - 1) {
                        zzgdArr[length3] = new zzgd();
                        zzyx.zza((zzzg) zzgdArr[length3]);
                        zzyx.zzug();
                        length3++;
                    }
                    zzgdArr[length3] = new zzgd();
                    zzyx.zza((zzzg) zzgdArr[length3]);
                    this.zzaxt = zzgdArr;
                    break;
                case 242:
                    this.zzafz = zzyx.readString();
                    break;
                case 248:
                    this.zzaxu = Integer.valueOf(zzyx.zzuy());
                    break;
                case 256:
                    this.zzaxv = Integer.valueOf(zzyx.zzuy());
                    break;
                case 264:
                    this.zzaxw = Integer.valueOf(zzyx.zzuy());
                    break;
                case TiffUtil.TIFF_TAG_ORIENTATION /*274*/:
                    this.zzaxx = zzyx.readString();
                    break;
                case 280:
                    this.zzaxy = Long.valueOf(zzyx.zzuz());
                    break;
                case 288:
                    this.zzaxz = Long.valueOf(zzyx.zzuz());
                    break;
                case 298:
                    this.zzaya = zzyx.readString();
                    break;
                case 306:
                    this.zzayb = zzyx.readString();
                    break;
                case 312:
                    this.zzayc = Integer.valueOf(zzyx.zzuy());
                    break;
                case 330:
                    this.zzawj = zzyx.readString();
                    break;
                case 354:
                    this.zzayd = (zzb) zzyx.zza(zzb.zza());
                    break;
                default:
                    if (super.zza(zzyx, zzug)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }
}
