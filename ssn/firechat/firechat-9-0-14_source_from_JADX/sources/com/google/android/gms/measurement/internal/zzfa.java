package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import com.amplitude.api.Constants;
import com.amplitude.api.DeviceInfo;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.common.wrappers.Wrappers;
import com.google.android.gms.internal.measurement.zzgb;
import com.google.android.gms.internal.measurement.zzgd;
import com.google.android.gms.internal.measurement.zzgf;
import com.google.android.gms.internal.measurement.zzgg;
import com.google.android.gms.internal.measurement.zzgi;
import com.google.android.gms.internal.measurement.zzgl;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.time.DateUtils;
import org.matrix.olm.OlmException;

public class zzfa implements zzcq {
    private static volatile zzfa zzatc;
    private final zzbt zzadj;
    private zzbn zzatd;
    private zzat zzate;
    private zzq zzatf;
    private zzay zzatg;
    private zzew zzath;
    private zzj zzati;
    private final zzfg zzatj;
    private boolean zzatk;
    @VisibleForTesting
    private long zzatl;
    private List<Runnable> zzatm;
    private int zzatn;
    private int zzato;
    private boolean zzatp;
    private boolean zzatq;
    private boolean zzatr;
    private FileLock zzats;
    private FileChannel zzatt;
    private List<Long> zzatu;
    private List<Long> zzatv;
    private long zzatw;
    private boolean zzvz;

    class zza implements zzs {
        zzgi zzaua;
        List<Long> zzaub;
        List<zzgf> zzauc;
        private long zzaud;

        private zza() {
        }

        public final void zzb(zzgi zzgi) {
            Preconditions.checkNotNull(zzgi);
            this.zzaua = zzgi;
        }

        public final boolean zza(long j, zzgf zzgf) {
            Preconditions.checkNotNull(zzgf);
            if (this.zzauc == null) {
                this.zzauc = new ArrayList();
            }
            if (this.zzaub == null) {
                this.zzaub = new ArrayList();
            }
            if (this.zzauc.size() > 0 && zza((zzgf) this.zzauc.get(0)) != zza(zzgf)) {
                return false;
            }
            long zzvu = this.zzaud + ((long) zzgf.zzvu());
            if (zzvu >= ((long) Math.max(0, ((Integer) zzaf.zzajl.get()).intValue()))) {
                return false;
            }
            this.zzaud = zzvu;
            this.zzauc.add(zzgf);
            this.zzaub.add(Long.valueOf(j));
            if (this.zzauc.size() >= Math.max(1, ((Integer) zzaf.zzajm.get()).intValue())) {
                return false;
            }
            return true;
        }

        private static long zza(zzgf zzgf) {
            return ((zzgf.zzawu.longValue() / 1000) / 60) / 60;
        }

        /* synthetic */ zza(zzfa zzfa, zzfb zzfb) {
            this();
        }
    }

    public static zzfa zzm(Context context) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(context.getApplicationContext());
        if (zzatc == null) {
            synchronized (zzfa.class) {
                if (zzatc == null) {
                    zzatc = new zzfa(new zzff(context));
                }
            }
        }
        return zzatc;
    }

    private zzfa(zzff zzff) {
        this(zzff, null);
    }

    private zzfa(zzff zzff, zzbt zzbt) {
        this.zzvz = false;
        Preconditions.checkNotNull(zzff);
        this.zzadj = zzbt.zza(zzff.zzri, (zzak) null);
        this.zzatw = -1;
        zzfg zzfg = new zzfg(this);
        zzfg.zzq();
        this.zzatj = zzfg;
        zzat zzat = new zzat(this);
        zzat.zzq();
        this.zzate = zzat;
        zzbn zzbn = new zzbn(this);
        zzbn.zzq();
        this.zzatd = zzbn;
        this.zzadj.zzgn().zzc((Runnable) new zzfb(this, zzff));
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zza(zzff zzff) {
        this.zzadj.zzgn().zzaf();
        zzq zzq = new zzq(this);
        zzq.zzq();
        this.zzatf = zzq;
        this.zzadj.zzgq().zza((zzp) this.zzatd);
        zzj zzj = new zzj(this);
        zzj.zzq();
        this.zzati = zzj;
        zzew zzew = new zzew(this);
        zzew.zzq();
        this.zzath = zzew;
        this.zzatg = new zzay(this);
        if (this.zzatn != this.zzato) {
            this.zzadj.zzgo().zzjd().zze("Not all upload components initialized", Integer.valueOf(this.zzatn), Integer.valueOf(this.zzato));
        }
        this.zzvz = true;
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void start() {
        this.zzadj.zzgn().zzaf();
        zzjq().zzif();
        if (this.zzadj.zzgp().zzane.get() == 0) {
            this.zzadj.zzgp().zzane.set(this.zzadj.zzbx().currentTimeMillis());
        }
        zzlv();
    }

    public final zzk zzgr() {
        return this.zzadj.zzgr();
    }

    public final zzn zzgq() {
        return this.zzadj.zzgq();
    }

    public final zzap zzgo() {
        return this.zzadj.zzgo();
    }

    public final zzbo zzgn() {
        return this.zzadj.zzgn();
    }

    private final zzbn zzln() {
        zza((zzez) this.zzatd);
        return this.zzatd;
    }

    public final zzat zzlo() {
        zza((zzez) this.zzate);
        return this.zzate;
    }

    public final zzq zzjq() {
        zza((zzez) this.zzatf);
        return this.zzatf;
    }

    private final zzay zzlp() {
        if (this.zzatg != null) {
            return this.zzatg;
        }
        throw new IllegalStateException("Network broadcast receiver not created");
    }

    private final zzew zzlq() {
        zza((zzez) this.zzath);
        return this.zzath;
    }

    public final zzj zzjp() {
        zza((zzez) this.zzati);
        return this.zzati;
    }

    public final zzfg zzjo() {
        zza((zzez) this.zzatj);
        return this.zzatj;
    }

    public final zzan zzgl() {
        return this.zzadj.zzgl();
    }

    public final Context getContext() {
        return this.zzadj.getContext();
    }

    public final Clock zzbx() {
        return this.zzadj.zzbx();
    }

    public final zzfk zzgm() {
        return this.zzadj.zzgm();
    }

    @WorkerThread
    private final void zzaf() {
        this.zzadj.zzgn().zzaf();
    }

    /* access modifiers changed from: 0000 */
    public final void zzlr() {
        if (!this.zzvz) {
            throw new IllegalStateException("UploadController is not initialized");
        }
    }

    private static void zza(zzez zzez) {
        if (zzez == null) {
            throw new IllegalStateException("Upload Component not created");
        } else if (!zzez.isInitialized()) {
            String valueOf = String.valueOf(zzez.getClass());
            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 27);
            sb.append("Component not initialized: ");
            sb.append(valueOf);
            throw new IllegalStateException(sb.toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zze(zzh zzh) {
        zzaf();
        zzlr();
        Preconditions.checkNotEmpty(zzh.packageName);
        zzg(zzh);
    }

    private final long zzls() {
        long currentTimeMillis = this.zzadj.zzbx().currentTimeMillis();
        zzba zzgp = this.zzadj.zzgp();
        zzgp.zzcl();
        zzgp.zzaf();
        long j = zzgp.zzani.get();
        if (j == 0) {
            long nextInt = ((long) zzgp.zzgm().zzmd().nextInt(86400000)) + 1;
            zzgp.zzani.set(nextInt);
            j = nextInt;
        }
        return ((((currentTimeMillis + j) / 1000) / 60) / 60) / 24;
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzc(zzad zzad, String str) {
        zzad zzad2 = zzad;
        String str2 = str;
        zzg zzbl = zzjq().zzbl(str2);
        if (zzbl == null || TextUtils.isEmpty(zzbl.zzak())) {
            this.zzadj.zzgo().zzjk().zzg("No app data available; dropping event", str2);
            return;
        }
        Boolean zzc = zzc(zzbl);
        if (zzc == null) {
            if (!"_ui".equals(zzad2.name)) {
                this.zzadj.zzgo().zzjg().zzg("Could not find package. appId", zzap.zzbv(str));
            }
        } else if (!zzc.booleanValue()) {
            this.zzadj.zzgo().zzjd().zzg("App version does not match; dropping event. appId", zzap.zzbv(str));
            return;
        }
        zzh zzh = r2;
        zzh zzh2 = new zzh(str2, zzbl.getGmpAppId(), zzbl.zzak(), zzbl.zzha(), zzbl.zzhb(), zzbl.zzhc(), zzbl.zzhd(), (String) null, zzbl.isMeasurementEnabled(), false, zzbl.getFirebaseInstanceId(), zzbl.zzhq(), 0, 0, zzbl.zzhr(), zzbl.zzhs(), false, zzbl.zzgw());
        zzc(zzad2, zzh);
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzc(zzad zzad, zzh zzh) {
        List<zzl> list;
        List<zzl> list2;
        List list3;
        zzad zzad2 = zzad;
        zzh zzh2 = zzh;
        Preconditions.checkNotNull(zzh);
        Preconditions.checkNotEmpty(zzh2.packageName);
        zzaf();
        zzlr();
        String str = zzh2.packageName;
        long j = zzad2.zzaip;
        if (zzjo().zze(zzad2, zzh2)) {
            if (!zzh2.zzagg) {
                zzg(zzh2);
                return;
            }
            zzjq().beginTransaction();
            try {
                zzq zzjq = zzjq();
                Preconditions.checkNotEmpty(str);
                zzjq.zzaf();
                zzjq.zzcl();
                if (j < 0) {
                    zzjq.zzgo().zzjg().zze("Invalid time querying timed out conditional properties", zzap.zzbv(str), Long.valueOf(j));
                    list = Collections.emptyList();
                } else {
                    list = zzjq.zzb("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[]{str, String.valueOf(j)});
                }
                for (zzl zzl : list) {
                    if (zzl != null) {
                        this.zzadj.zzgo().zzjk().zzd("User property timed out", zzl.packageName, this.zzadj.zzgl().zzbu(zzl.zzahb.name), zzl.zzahb.getValue());
                        if (zzl.zzahc != null) {
                            zzd(new zzad(zzl.zzahc, j), zzh2);
                        }
                        zzjq().zzk(str, zzl.zzahb.name);
                    }
                }
                zzq zzjq2 = zzjq();
                Preconditions.checkNotEmpty(str);
                zzjq2.zzaf();
                zzjq2.zzcl();
                if (j < 0) {
                    zzjq2.zzgo().zzjg().zze("Invalid time querying expired conditional properties", zzap.zzbv(str), Long.valueOf(j));
                    list2 = Collections.emptyList();
                } else {
                    list2 = zzjq2.zzb("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[]{str, String.valueOf(j)});
                }
                ArrayList arrayList = new ArrayList(list2.size());
                for (zzl zzl2 : list2) {
                    if (zzl2 != null) {
                        this.zzadj.zzgo().zzjk().zzd("User property expired", zzl2.packageName, this.zzadj.zzgl().zzbu(zzl2.zzahb.name), zzl2.zzahb.getValue());
                        zzjq().zzh(str, zzl2.zzahb.name);
                        if (zzl2.zzahe != null) {
                            arrayList.add(zzl2.zzahe);
                        }
                        zzjq().zzk(str, zzl2.zzahb.name);
                    }
                }
                ArrayList arrayList2 = arrayList;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    Object obj = arrayList2.get(i);
                    i++;
                    zzd(new zzad((zzad) obj, j), zzh2);
                }
                zzq zzjq3 = zzjq();
                String str2 = zzad2.name;
                Preconditions.checkNotEmpty(str);
                Preconditions.checkNotEmpty(str2);
                zzjq3.zzaf();
                zzjq3.zzcl();
                if (j < 0) {
                    zzjq3.zzgo().zzjg().zzd("Invalid time querying triggered conditional properties", zzap.zzbv(str), zzjq3.zzgl().zzbs(str2), Long.valueOf(j));
                    list3 = Collections.emptyList();
                } else {
                    list3 = zzjq3.zzb("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[]{str, str2, String.valueOf(j)});
                }
                ArrayList arrayList3 = new ArrayList(list3.size());
                Iterator it = list3.iterator();
                while (it.hasNext()) {
                    zzl zzl3 = (zzl) it.next();
                    if (zzl3 != null) {
                        zzfh zzfh = zzl3.zzahb;
                        zzfj zzfj = r5;
                        Iterator it2 = it;
                        zzl zzl4 = zzl3;
                        zzfj zzfj2 = new zzfj(zzl3.packageName, zzl3.origin, zzfh.name, j, zzfh.getValue());
                        if (zzjq().zza(zzfj)) {
                            this.zzadj.zzgo().zzjk().zzd("User property triggered", zzl4.packageName, this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                        } else {
                            this.zzadj.zzgo().zzjd().zzd("Too many active user properties, ignoring", zzap.zzbv(zzl4.packageName), this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                        }
                        if (zzl4.zzahd != null) {
                            arrayList3.add(zzl4.zzahd);
                        }
                        zzl4.zzahb = new zzfh(zzfj);
                        zzl4.active = true;
                        zzjq().zza(zzl4);
                        it = it2;
                    }
                }
                zzd(zzad, zzh);
                ArrayList arrayList4 = arrayList3;
                int size2 = arrayList4.size();
                int i2 = 0;
                while (i2 < size2) {
                    Object obj2 = arrayList4.get(i2);
                    i2++;
                    zzd(new zzad((zzad) obj2, j), zzh2);
                }
                zzjq().setTransactionSuccessful();
                zzjq().endTransaction();
            } catch (Throwable th) {
                Throwable th2 = th;
                zzjq().endTransaction();
                throw th2;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:144:0x05cd A[Catch:{ IOException -> 0x05d0, all -> 0x0641 }] */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x05fb A[Catch:{ IOException -> 0x05d0, all -> 0x0641 }] */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzd(com.google.android.gms.measurement.internal.zzad r25, com.google.android.gms.measurement.internal.zzh r26) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            r3 = r26
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r26)
            java.lang.String r4 = r3.packageName
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r4)
            long r4 = java.lang.System.nanoTime()
            r24.zzaf()
            r24.zzlr()
            java.lang.String r15 = r3.packageName
            com.google.android.gms.measurement.internal.zzfg r6 = r24.zzjo()
            boolean r6 = r6.zze(r2, r3)
            if (r6 != 0) goto L_0x0025
            return
        L_0x0025:
            boolean r6 = r3.zzagg
            if (r6 != 0) goto L_0x002d
            r1.zzg(r3)
            return
        L_0x002d:
            com.google.android.gms.measurement.internal.zzbn r6 = r24.zzln()
            java.lang.String r7 = r2.name
            boolean r6 = r6.zzo(r15, r7)
            r14 = 0
            r13 = 1
            if (r6 == 0) goto L_0x00d8
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjg()
            java.lang.String r4 = "Dropping blacklisted event. appId"
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzap.zzbv(r15)
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj
            com.google.android.gms.measurement.internal.zzan r6 = r6.zzgl()
            java.lang.String r7 = r2.name
            java.lang.String r6 = r6.zzbs(r7)
            r3.zze(r4, r5, r6)
            com.google.android.gms.measurement.internal.zzbn r3 = r24.zzln()
            boolean r3 = r3.zzck(r15)
            if (r3 != 0) goto L_0x0070
            com.google.android.gms.measurement.internal.zzbn r3 = r24.zzln()
            boolean r3 = r3.zzcl(r15)
            if (r3 == 0) goto L_0x006f
            goto L_0x0070
        L_0x006f:
            r13 = 0
        L_0x0070:
            if (r13 != 0) goto L_0x008d
            java.lang.String r3 = "_err"
            java.lang.String r4 = r2.name
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x008d
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj
            com.google.android.gms.measurement.internal.zzfk r6 = r3.zzgm()
            r8 = 11
            java.lang.String r9 = "_ev"
            java.lang.String r10 = r2.name
            r11 = 0
            r7 = r15
            r6.zza(r7, r8, r9, r10, r11)
        L_0x008d:
            if (r13 == 0) goto L_0x00d7
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            com.google.android.gms.measurement.internal.zzg r2 = r2.zzbl(r15)
            if (r2 == 0) goto L_0x00d7
            long r3 = r2.zzhg()
            long r5 = r2.zzhf()
            long r3 = java.lang.Math.max(r3, r5)
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj
            com.google.android.gms.common.util.Clock r5 = r5.zzbx()
            long r5 = r5.currentTimeMillis()
            long r7 = r5 - r3
            long r3 = java.lang.Math.abs(r7)
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r5 = com.google.android.gms.measurement.internal.zzaf.zzakc
            java.lang.Object r5 = r5.get()
            java.lang.Long r5 = (java.lang.Long) r5
            long r5 = r5.longValue()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x00d7
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjk()
            java.lang.String r4 = "Fetching config for blacklisted app"
            r3.zzbx(r4)
            r1.zzb(r2)
        L_0x00d7:
            return
        L_0x00d8:
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()
            r12 = 2
            boolean r6 = r6.isLoggable(r12)
            if (r6 == 0) goto L_0x00fe
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjl()
            java.lang.String r7 = "Logging event"
            com.google.android.gms.measurement.internal.zzbt r8 = r1.zzadj
            com.google.android.gms.measurement.internal.zzan r8 = r8.zzgl()
            java.lang.String r8 = r8.zzb(r2)
            r6.zzg(r7, r8)
        L_0x00fe:
            com.google.android.gms.measurement.internal.zzq r6 = r24.zzjq()
            r6.beginTransaction()
            r1.zzg(r3)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = "_iap"
            java.lang.String r7 = r2.name     // Catch:{ all -> 0x0641 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0641 }
            if (r6 != 0) goto L_0x011c
            java.lang.String r6 = "ecommerce_purchase"
            java.lang.String r7 = r2.name     // Catch:{ all -> 0x0641 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x0131
        L_0x011c:
            boolean r6 = r1.zza(r15, r2)     // Catch:{ all -> 0x0641 }
            if (r6 != 0) goto L_0x0131
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            return
        L_0x0131:
            java.lang.String r6 = r2.name     // Catch:{ all -> 0x0641 }
            boolean r16 = com.google.android.gms.measurement.internal.zzfk.zzcq(r6)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = "_err"
            java.lang.String r7 = r2.name     // Catch:{ all -> 0x0641 }
            boolean r17 = r6.equals(r7)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r6 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            long r7 = r24.zzls()     // Catch:{ all -> 0x0641 }
            r10 = 1
            r18 = 0
            r19 = 0
            r9 = r15
            r11 = r16
            r12 = r18
            r13 = r17
            r21 = r4
            r4 = 0
            r14 = r19
            com.google.android.gms.measurement.internal.zzr r5 = r6.zza(r7, r9, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x0641 }
            long r6 = r5.zzahr     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r8 = com.google.android.gms.measurement.internal.zzaf.zzajn     // Catch:{ all -> 0x0641 }
            java.lang.Object r8 = r8.get()     // Catch:{ all -> 0x0641 }
            java.lang.Integer r8 = (java.lang.Integer) r8     // Catch:{ all -> 0x0641 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x0641 }
            long r8 = (long) r8     // Catch:{ all -> 0x0641 }
            long r10 = r6 - r8
            r13 = 0
            int r6 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            r7 = 1000(0x3e8, double:4.94E-321)
            r13 = 1
            if (r6 <= 0) goto L_0x01a4
            long r10 = r10 % r7
            int r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x0195
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjd()     // Catch:{ all -> 0x0641 }
            java.lang.String r3 = "Data loss. Too many events logged. appId, count"
            java.lang.Object r4 = com.google.android.gms.measurement.internal.zzap.zzbv(r15)     // Catch:{ all -> 0x0641 }
            long r5 = r5.zzahr     // Catch:{ all -> 0x0641 }
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0641 }
            r2.zze(r3, r4, r5)     // Catch:{ all -> 0x0641 }
        L_0x0195:
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            return
        L_0x01a4:
            if (r16 == 0) goto L_0x01fc
            long r9 = r5.zzahq     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r6 = com.google.android.gms.measurement.internal.zzaf.zzajp     // Catch:{ all -> 0x0641 }
            java.lang.Object r6 = r6.get()     // Catch:{ all -> 0x0641 }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ all -> 0x0641 }
            int r6 = r6.intValue()     // Catch:{ all -> 0x0641 }
            long r11 = (long) r6     // Catch:{ all -> 0x0641 }
            long r18 = r9 - r11
            r9 = 0
            int r6 = (r18 > r9 ? 1 : (r18 == r9 ? 0 : -1))
            if (r6 <= 0) goto L_0x01fc
            long r18 = r18 % r7
            int r3 = (r18 > r13 ? 1 : (r18 == r13 ? 0 : -1))
            if (r3 != 0) goto L_0x01dc
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()     // Catch:{ all -> 0x0641 }
            java.lang.String r4 = "Data loss. Too many public events logged. appId, count"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r15)     // Catch:{ all -> 0x0641 }
            long r7 = r5.zzahq     // Catch:{ all -> 0x0641 }
            java.lang.Long r5 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0641 }
            r3.zze(r4, r6, r5)     // Catch:{ all -> 0x0641 }
        L_0x01dc:
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r3.zzgm()     // Catch:{ all -> 0x0641 }
            r8 = 16
            java.lang.String r9 = "_ev"
            java.lang.String r10 = r2.name     // Catch:{ all -> 0x0641 }
            r11 = 0
            r7 = r15
            r6.zza(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            return
        L_0x01fc:
            if (r17 == 0) goto L_0x024e
            long r6 = r5.zzaht     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r8 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzn r8 = r8.zzgq()     // Catch:{ all -> 0x0641 }
            java.lang.String r9 = r3.packageName     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r10 = com.google.android.gms.measurement.internal.zzaf.zzajo     // Catch:{ all -> 0x0641 }
            int r8 = r8.zzb(r9, r10)     // Catch:{ all -> 0x0641 }
            r9 = 1000000(0xf4240, float:1.401298E-39)
            int r8 = java.lang.Math.min(r9, r8)     // Catch:{ all -> 0x0641 }
            int r8 = java.lang.Math.max(r4, r8)     // Catch:{ all -> 0x0641 }
            long r8 = (long) r8     // Catch:{ all -> 0x0641 }
            long r10 = r6 - r8
            r6 = 0
            int r8 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x024e
            int r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x023f
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjd()     // Catch:{ all -> 0x0641 }
            java.lang.String r3 = "Too many error events logged. appId, count"
            java.lang.Object r4 = com.google.android.gms.measurement.internal.zzap.zzbv(r15)     // Catch:{ all -> 0x0641 }
            long r5 = r5.zzaht     // Catch:{ all -> 0x0641 }
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0641 }
            r2.zze(r3, r4, r5)     // Catch:{ all -> 0x0641 }
        L_0x023f:
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            return
        L_0x024e:
            com.google.android.gms.measurement.internal.zzaa r5 = r2.zzaid     // Catch:{ all -> 0x0641 }
            android.os.Bundle r5 = r5.zziv()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = "_o"
            java.lang.String r8 = r2.origin     // Catch:{ all -> 0x0641 }
            r6.zza(r5, r7, r8)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0641 }
            boolean r6 = r6.zzcw(r15)     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x028b
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = "_dbg"
            java.lang.Long r8 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0641 }
            r6.zza(r5, r7, r8)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = "_r"
            java.lang.Long r8 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0641 }
            r6.zza(r5, r7, r8)     // Catch:{ all -> 0x0641 }
        L_0x028b:
            com.google.android.gms.measurement.internal.zzq r6 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            long r6 = r6.zzbm(r15)     // Catch:{ all -> 0x0641 }
            r13 = 0
            int r8 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r8 <= 0) goto L_0x02b0
            com.google.android.gms.measurement.internal.zzbt r8 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r8 = r8.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r8 = r8.zzjg()     // Catch:{ all -> 0x0641 }
            java.lang.String r9 = "Data lost. Too many events stored on disk, deleted. appId"
            java.lang.Object r10 = com.google.android.gms.measurement.internal.zzap.zzbv(r15)     // Catch:{ all -> 0x0641 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0641 }
            r8.zze(r9, r10, r6)     // Catch:{ all -> 0x0641 }
        L_0x02b0:
            com.google.android.gms.measurement.internal.zzy r11 = new com.google.android.gms.measurement.internal.zzy     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0641 }
            java.lang.String r8 = r2.origin     // Catch:{ all -> 0x0641 }
            java.lang.String r10 = r2.name     // Catch:{ all -> 0x0641 }
            long r13 = r2.zzaip     // Catch:{ all -> 0x0641 }
            r17 = 0
            r6 = r11
            r9 = r15
            r2 = r11
            r11 = r13
            r13 = r17
            r4 = r15
            r15 = r5
            r6.<init>(r7, r8, r9, r10, r11, r13, r15)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r5 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r2.name     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzz r5 = r5.zzg(r4, r6)     // Catch:{ all -> 0x0641 }
            if (r5 != 0) goto L_0x033a
            com.google.android.gms.measurement.internal.zzq r5 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            long r5 = r5.zzbp(r4)     // Catch:{ all -> 0x0641 }
            r7 = 500(0x1f4, double:2.47E-321)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x031f
            if (r16 == 0) goto L_0x031f
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = "Too many event names used, ignoring event. appId, name, supported count"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r4)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzan r7 = r7.zzgl()     // Catch:{ all -> 0x0641 }
            java.lang.String r2 = r2.name     // Catch:{ all -> 0x0641 }
            java.lang.String r2 = r7.zzbs(r2)     // Catch:{ all -> 0x0641 }
            r7 = 500(0x1f4, float:7.0E-43)
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0641 }
            r3.zzd(r5, r6, r2, r7)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r2.zzgm()     // Catch:{ all -> 0x0641 }
            r8 = 8
            r9 = 0
            r10 = 0
            r11 = 0
            r7 = r4
            r6.zza(r7, r8, r9, r10, r11)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            return
        L_0x031f:
            com.google.android.gms.measurement.internal.zzz r5 = new com.google.android.gms.measurement.internal.zzz     // Catch:{ all -> 0x0641 }
            java.lang.String r8 = r2.name     // Catch:{ all -> 0x0641 }
            r9 = 0
            r11 = 0
            long r13 = r2.timestamp     // Catch:{ all -> 0x0641 }
            r15 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r6 = r5
            r7 = r4
            r6.<init>(r7, r8, r9, r11, r13, r15, r17, r18, r19, r20)     // Catch:{ all -> 0x0641 }
            r11 = r2
            goto L_0x0348
        L_0x033a:
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj     // Catch:{ all -> 0x0641 }
            long r6 = r5.zzaig     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzy r11 = r2.zza(r4, r6)     // Catch:{ all -> 0x0641 }
            long r6 = r11.timestamp     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzz r5 = r5.zzai(r6)     // Catch:{ all -> 0x0641 }
        L_0x0348:
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.zza(r5)     // Catch:{ all -> 0x0641 }
            r24.zzaf()     // Catch:{ all -> 0x0641 }
            r24.zzlr()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r11)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r26)     // Catch:{ all -> 0x0641 }
            java.lang.String r2 = r11.zztt     // Catch:{ all -> 0x0641 }
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r2)     // Catch:{ all -> 0x0641 }
            java.lang.String r2 = r11.zztt     // Catch:{ all -> 0x0641 }
            java.lang.String r4 = r3.packageName     // Catch:{ all -> 0x0641 }
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.common.internal.Preconditions.checkArgument(r2)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.internal.measurement.zzgi r2 = new com.google.android.gms.internal.measurement.zzgi     // Catch:{ all -> 0x0641 }
            r2.<init>()     // Catch:{ all -> 0x0641 }
            r4 = 1
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0641 }
            r2.zzaxa = r5     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = "android"
            r2.zzaxi = r5     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = r3.packageName     // Catch:{ all -> 0x0641 }
            r2.zztt = r5     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = r3.zzage     // Catch:{ all -> 0x0641 }
            r2.zzage = r5     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = r3.zzts     // Catch:{ all -> 0x0641 }
            r2.zzts = r5     // Catch:{ all -> 0x0641 }
            long r5 = r3.zzagd     // Catch:{ all -> 0x0641 }
            r7 = -2147483648(0xffffffff80000000, double:NaN)
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            r5 = 0
            if (r9 != 0) goto L_0x0393
            r6 = r5
            goto L_0x039a
        L_0x0393:
            long r6 = r3.zzagd     // Catch:{ all -> 0x0641 }
            int r6 = (int) r6     // Catch:{ all -> 0x0641 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0641 }
        L_0x039a:
            r2.zzaxu = r6     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzadt     // Catch:{ all -> 0x0641 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0641 }
            r2.zzaxm = r6     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzafx     // Catch:{ all -> 0x0641 }
            r2.zzafx = r6     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzagk     // Catch:{ all -> 0x0641 }
            r2.zzawj = r6     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzagf     // Catch:{ all -> 0x0641 }
            r8 = 0
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x03b6
            r6 = r5
            goto L_0x03bc
        L_0x03b6:
            long r6 = r3.zzagf     // Catch:{ all -> 0x0641 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0641 }
        L_0x03bc:
            r2.zzaxq = r6     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzba r6 = r6.zzgp()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r3.packageName     // Catch:{ all -> 0x0641 }
            android.util.Pair r6 = r6.zzby(r7)     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x03e7
            java.lang.Object r7 = r6.first     // Catch:{ all -> 0x0641 }
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7     // Catch:{ all -> 0x0641 }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x0641 }
            if (r7 != 0) goto L_0x03e7
            boolean r7 = r3.zzagi     // Catch:{ all -> 0x0641 }
            if (r7 == 0) goto L_0x0444
            java.lang.Object r7 = r6.first     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0641 }
            r2.zzaxo = r7     // Catch:{ all -> 0x0641 }
            java.lang.Object r6 = r6.second     // Catch:{ all -> 0x0641 }
            java.lang.Boolean r6 = (java.lang.Boolean) r6     // Catch:{ all -> 0x0641 }
            r2.zzaxp = r6     // Catch:{ all -> 0x0641 }
            goto L_0x0444
        L_0x03e7:
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzx r6 = r6.zzgk()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0641 }
            android.content.Context r7 = r7.getContext()     // Catch:{ all -> 0x0641 }
            boolean r6 = r6.zzl(r7)     // Catch:{ all -> 0x0641 }
            if (r6 != 0) goto L_0x0444
            boolean r6 = r3.zzagj     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x0444
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            android.content.Context r6 = r6.getContext()     // Catch:{ all -> 0x0641 }
            android.content.ContentResolver r6 = r6.getContentResolver()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = "android_id"
            java.lang.String r6 = android.provider.Settings.Secure.getString(r6, r7)     // Catch:{ all -> 0x0641 }
            if (r6 != 0) goto L_0x0427
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjg()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = "null secure ID. appId"
            java.lang.String r10 = r2.zztt     // Catch:{ all -> 0x0641 }
            java.lang.Object r10 = com.google.android.gms.measurement.internal.zzap.zzbv(r10)     // Catch:{ all -> 0x0641 }
            r6.zzg(r7, r10)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = "null"
            goto L_0x0442
        L_0x0427:
            boolean r7 = r6.isEmpty()     // Catch:{ all -> 0x0641 }
            if (r7 == 0) goto L_0x0442
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r7 = r7.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r7 = r7.zzjg()     // Catch:{ all -> 0x0641 }
            java.lang.String r10 = "empty secure ID. appId"
            java.lang.String r12 = r2.zztt     // Catch:{ all -> 0x0641 }
            java.lang.Object r12 = com.google.android.gms.measurement.internal.zzap.zzbv(r12)     // Catch:{ all -> 0x0641 }
            r7.zzg(r10, r12)     // Catch:{ all -> 0x0641 }
        L_0x0442:
            r2.zzaxx = r6     // Catch:{ all -> 0x0641 }
        L_0x0444:
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzx r6 = r6.zzgk()     // Catch:{ all -> 0x0641 }
            r6.zzcl()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = android.os.Build.MODEL     // Catch:{ all -> 0x0641 }
            r2.zzaxk = r6     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzx r6 = r6.zzgk()     // Catch:{ all -> 0x0641 }
            r6.zzcl()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = android.os.Build.VERSION.RELEASE     // Catch:{ all -> 0x0641 }
            r2.zzaxj = r6     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzx r6 = r6.zzgk()     // Catch:{ all -> 0x0641 }
            long r6 = r6.zzis()     // Catch:{ all -> 0x0641 }
            int r6 = (int) r6     // Catch:{ all -> 0x0641 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0641 }
            r2.zzaxl = r6     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzx r6 = r6.zzgk()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r6.zzit()     // Catch:{ all -> 0x0641 }
            r2.zzaia = r6     // Catch:{ all -> 0x0641 }
            r2.zzaxn = r5     // Catch:{ all -> 0x0641 }
            r2.zzaxd = r5     // Catch:{ all -> 0x0641 }
            r2.zzaxe = r5     // Catch:{ all -> 0x0641 }
            r2.zzaxf = r5     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzagh     // Catch:{ all -> 0x0641 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0641 }
            r2.zzaxz = r6     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            boolean r6 = r6.isEnabled()     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x049b
            boolean r6 = com.google.android.gms.measurement.internal.zzn.zzhz()     // Catch:{ all -> 0x0641 }
            if (r6 == 0) goto L_0x049b
            r2.zzaya = r5     // Catch:{ all -> 0x0641 }
        L_0x049b:
            com.google.android.gms.measurement.internal.zzq r5 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.packageName     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzg r5 = r5.zzbl(r6)     // Catch:{ all -> 0x0641 }
            if (r5 != 0) goto L_0x0509
            com.google.android.gms.measurement.internal.zzg r5 = new com.google.android.gms.measurement.internal.zzg     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r3.packageName     // Catch:{ all -> 0x0641 }
            r5.<init>(r6, r7)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r6.zzmf()     // Catch:{ all -> 0x0641 }
            r5.zzam(r6)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzafz     // Catch:{ all -> 0x0641 }
            r5.zzaq(r6)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzafx     // Catch:{ all -> 0x0641 }
            r5.zzan(r6)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzba r6 = r6.zzgp()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r3.packageName     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r6.zzbz(r7)     // Catch:{ all -> 0x0641 }
            r5.zzap(r6)     // Catch:{ all -> 0x0641 }
            r5.zzx(r8)     // Catch:{ all -> 0x0641 }
            r5.zzs(r8)     // Catch:{ all -> 0x0641 }
            r5.zzt(r8)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzts     // Catch:{ all -> 0x0641 }
            r5.setAppVersion(r6)     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzagd     // Catch:{ all -> 0x0641 }
            r5.zzu(r6)     // Catch:{ all -> 0x0641 }
            java.lang.String r6 = r3.zzage     // Catch:{ all -> 0x0641 }
            r5.zzar(r6)     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzadt     // Catch:{ all -> 0x0641 }
            r5.zzv(r6)     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzagf     // Catch:{ all -> 0x0641 }
            r5.zzw(r6)     // Catch:{ all -> 0x0641 }
            boolean r6 = r3.zzagg     // Catch:{ all -> 0x0641 }
            r5.setMeasurementEnabled(r6)     // Catch:{ all -> 0x0641 }
            long r6 = r3.zzagh     // Catch:{ all -> 0x0641 }
            r5.zzag(r6)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r6 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r6.zza(r5)     // Catch:{ all -> 0x0641 }
        L_0x0509:
            java.lang.String r6 = r5.getAppInstanceId()     // Catch:{ all -> 0x0641 }
            r2.zzafw = r6     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = r5.getFirebaseInstanceId()     // Catch:{ all -> 0x0641 }
            r2.zzafz = r5     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r5 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            java.lang.String r3 = r3.packageName     // Catch:{ all -> 0x0641 }
            java.util.List r3 = r5.zzbk(r3)     // Catch:{ all -> 0x0641 }
            int r5 = r3.size()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.internal.measurement.zzgl[] r5 = new com.google.android.gms.internal.measurement.zzgl[r5]     // Catch:{ all -> 0x0641 }
            r2.zzaxc = r5     // Catch:{ all -> 0x0641 }
            r5 = 0
        L_0x0528:
            int r6 = r3.size()     // Catch:{ all -> 0x0641 }
            if (r5 >= r6) goto L_0x0561
            com.google.android.gms.internal.measurement.zzgl r6 = new com.google.android.gms.internal.measurement.zzgl     // Catch:{ all -> 0x0641 }
            r6.<init>()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r2.zzaxc     // Catch:{ all -> 0x0641 }
            r7[r5] = r6     // Catch:{ all -> 0x0641 }
            java.lang.Object r7 = r3.get(r5)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfj r7 = (com.google.android.gms.measurement.internal.zzfj) r7     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r7.name     // Catch:{ all -> 0x0641 }
            r6.name = r7     // Catch:{ all -> 0x0641 }
            java.lang.Object r7 = r3.get(r5)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfj r7 = (com.google.android.gms.measurement.internal.zzfj) r7     // Catch:{ all -> 0x0641 }
            long r12 = r7.zzaue     // Catch:{ all -> 0x0641 }
            java.lang.Long r7 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x0641 }
            r6.zzayl = r7     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfg r7 = r24.zzjo()     // Catch:{ all -> 0x0641 }
            java.lang.Object r10 = r3.get(r5)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzfj r10 = (com.google.android.gms.measurement.internal.zzfj) r10     // Catch:{ all -> 0x0641 }
            java.lang.Object r10 = r10.value     // Catch:{ all -> 0x0641 }
            r7.zza(r6, r10)     // Catch:{ all -> 0x0641 }
            int r5 = r5 + 1
            goto L_0x0528
        L_0x0561:
            com.google.android.gms.measurement.internal.zzq r3 = r24.zzjq()     // Catch:{ IOException -> 0x05d0 }
            long r5 = r3.zza(r2)     // Catch:{ IOException -> 0x05d0 }
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzaa r3 = r11.zzaid     // Catch:{ all -> 0x0641 }
            if (r3 == 0) goto L_0x05c6
            com.google.android.gms.measurement.internal.zzaa r3 = r11.zzaid     // Catch:{ all -> 0x0641 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0641 }
        L_0x0577:
            boolean r7 = r3.hasNext()     // Catch:{ all -> 0x0641 }
            if (r7 == 0) goto L_0x058c
            java.lang.Object r7 = r3.next()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0641 }
            java.lang.String r10 = "_r"
            boolean r7 = r10.equals(r7)     // Catch:{ all -> 0x0641 }
            if (r7 == 0) goto L_0x0577
            goto L_0x05c7
        L_0x058c:
            com.google.android.gms.measurement.internal.zzbn r3 = r24.zzln()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r11.zztt     // Catch:{ all -> 0x0641 }
            java.lang.String r10 = r11.name     // Catch:{ all -> 0x0641 }
            boolean r3 = r3.zzp(r7, r10)     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzq r12 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            long r13 = r24.zzls()     // Catch:{ all -> 0x0641 }
            java.lang.String r15 = r11.zztt     // Catch:{ all -> 0x0641 }
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            com.google.android.gms.measurement.internal.zzr r7 = r12.zza(r13, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x0641 }
            if (r3 == 0) goto L_0x05c6
            long r12 = r7.zzahu     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzn r3 = r3.zzgq()     // Catch:{ all -> 0x0641 }
            java.lang.String r7 = r11.zztt     // Catch:{ all -> 0x0641 }
            int r3 = r3.zzat(r7)     // Catch:{ all -> 0x0641 }
            long r14 = (long) r3     // Catch:{ all -> 0x0641 }
            int r3 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r3 >= 0) goto L_0x05c6
            goto L_0x05c7
        L_0x05c6:
            r4 = 0
        L_0x05c7:
            boolean r2 = r2.zza(r11, r5, r4)     // Catch:{ all -> 0x0641 }
            if (r2 == 0) goto L_0x05e7
            r1.zzatl = r8     // Catch:{ all -> 0x0641 }
            goto L_0x05e7
        L_0x05d0:
            r0 = move-exception
            r3 = r0
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r4 = r4.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjd()     // Catch:{ all -> 0x0641 }
            java.lang.String r5 = "Data loss. Failed to insert raw event metadata. appId"
            java.lang.String r2 = r2.zztt     // Catch:{ all -> 0x0641 }
            java.lang.Object r2 = com.google.android.gms.measurement.internal.zzap.zzbv(r2)     // Catch:{ all -> 0x0641 }
            r4.zze(r5, r2, r3)     // Catch:{ all -> 0x0641 }
        L_0x05e7:
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()     // Catch:{ all -> 0x0641 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0641 }
            r3 = 2
            boolean r2 = r2.isLoggable(r3)     // Catch:{ all -> 0x0641 }
            if (r2 == 0) goto L_0x0614
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjl()     // Catch:{ all -> 0x0641 }
            java.lang.String r3 = "Event recorded"
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj     // Catch:{ all -> 0x0641 }
            com.google.android.gms.measurement.internal.zzan r4 = r4.zzgl()     // Catch:{ all -> 0x0641 }
            java.lang.String r4 = r4.zza(r11)     // Catch:{ all -> 0x0641 }
            r2.zzg(r3, r4)     // Catch:{ all -> 0x0641 }
        L_0x0614:
            com.google.android.gms.measurement.internal.zzq r2 = r24.zzjq()
            r2.endTransaction()
            r24.zzlv()
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjl()
            java.lang.String r3 = "Background event processing time, ms"
            long r4 = java.lang.System.nanoTime()
            long r6 = r4 - r21
            r4 = 500000(0x7a120, double:2.47033E-318)
            long r8 = r6 + r4
            r4 = 1000000(0xf4240, double:4.940656E-318)
            long r8 = r8 / r4
            java.lang.Long r4 = java.lang.Long.valueOf(r8)
            r2.zzg(r3, r4)
            return
        L_0x0641:
            r0 = move-exception
            r2 = r0
            com.google.android.gms.measurement.internal.zzq r3 = r24.zzjq()
            r3.endTransaction()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzd(com.google.android.gms.measurement.internal.zzad, com.google.android.gms.measurement.internal.zzh):void");
    }

    private final boolean zza(String str, zzad zzad) {
        long j;
        zzfj zzfj;
        String string = zzad.zzaid.getString(Param.CURRENCY);
        if (Event.ECOMMERCE_PURCHASE.equals(zzad.name)) {
            double doubleValue = zzad.zzaid.zzbq("value").doubleValue() * 1000000.0d;
            if (doubleValue == 0.0d) {
                doubleValue = ((double) zzad.zzaid.getLong("value").longValue()) * 1000000.0d;
            }
            if (doubleValue > 9.223372036854776E18d || doubleValue < -9.223372036854776E18d) {
                this.zzadj.zzgo().zzjg().zze("Data lost. Currency value is too big. appId", zzap.zzbv(str), Double.valueOf(doubleValue));
                return false;
            }
            j = Math.round(doubleValue);
        } else {
            j = zzad.zzaid.getLong("value").longValue();
        }
        if (!TextUtils.isEmpty(string)) {
            String upperCase = string.toUpperCase(Locale.US);
            if (upperCase.matches("[A-Z]{3}")) {
                String valueOf = String.valueOf("_ltv_");
                String valueOf2 = String.valueOf(upperCase);
                String concat = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
                zzfj zzi = zzjq().zzi(str, concat);
                if (zzi == null || !(zzi.value instanceof Long)) {
                    zzq zzjq = zzjq();
                    int zzb = this.zzadj.zzgq().zzb(str, zzaf.zzakh) - 1;
                    Preconditions.checkNotEmpty(str);
                    zzjq.zzaf();
                    zzjq.zzcl();
                    try {
                        zzjq.getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(zzb)});
                    } catch (SQLiteException e) {
                        zzjq.zzgo().zzjd().zze("Error pruning currencies. appId", zzap.zzbv(str), e);
                    }
                    zzfj = new zzfj(str, zzad.origin, concat, this.zzadj.zzbx().currentTimeMillis(), Long.valueOf(j));
                } else {
                    zzfj zzfj2 = new zzfj(str, zzad.origin, concat, this.zzadj.zzbx().currentTimeMillis(), Long.valueOf(((Long) zzi.value).longValue() + j));
                    zzfj = zzfj2;
                }
                if (!zzjq().zza(zzfj)) {
                    this.zzadj.zzgo().zzjd().zzd("Too many unique user properties are set. Ignoring user property. appId", zzap.zzbv(str), this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                    this.zzadj.zzgm().zza(str, 9, (String) null, (String) null, 0);
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Can't wrap try/catch for region: R(2:88|89) */
    /* JADX WARNING: Code restructure failed: missing block: B:89:?, code lost:
        r14.zzadj.zzgo().zzjd().zze("Failed to parse upload URL. Not uploading. appId", com.google.android.gms.measurement.internal.zzap.zzbv(r4), r5);
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:88:0x029a */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzlt() {
        /*
            r14 = this;
            r14.zzaf()
            r14.zzlr()
            r0 = 1
            r14.zzatr = r0
            r1 = 0
            com.google.android.gms.measurement.internal.zzbt r2 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            r2.zzgr()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbt r2 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzdr r2 = r2.zzgg()     // Catch:{ all -> 0x02d8 }
            java.lang.Boolean r2 = r2.zzle()     // Catch:{ all -> 0x02d8 }
            if (r2 != 0) goto L_0x0030
            com.google.android.gms.measurement.internal.zzbt r0 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r0 = r0.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjg()     // Catch:{ all -> 0x02d8 }
            java.lang.String r2 = "Upload data called on the client side before use of service was decided"
            r0.zzbx(r2)     // Catch:{ all -> 0x02d8 }
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x0030:
            boolean r2 = r2.booleanValue()     // Catch:{ all -> 0x02d8 }
            if (r2 == 0) goto L_0x004b
            com.google.android.gms.measurement.internal.zzbt r0 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r0 = r0.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjd()     // Catch:{ all -> 0x02d8 }
            java.lang.String r2 = "Upload called in the client side when service should be used"
            r0.zzbx(r2)     // Catch:{ all -> 0x02d8 }
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x004b:
            long r2 = r14.zzatl     // Catch:{ all -> 0x02d8 }
            r4 = 0
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x005c
            r14.zzlv()     // Catch:{ all -> 0x02d8 }
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x005c:
            r14.zzaf()     // Catch:{ all -> 0x02d8 }
            java.util.List<java.lang.Long> r2 = r14.zzatu     // Catch:{ all -> 0x02d8 }
            if (r2 == 0) goto L_0x0065
            r2 = 1
            goto L_0x0066
        L_0x0065:
            r2 = 0
        L_0x0066:
            if (r2 == 0) goto L_0x007d
            com.google.android.gms.measurement.internal.zzbt r0 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r0 = r0.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjl()     // Catch:{ all -> 0x02d8 }
            java.lang.String r2 = "Uploading requested multiple times"
            r0.zzbx(r2)     // Catch:{ all -> 0x02d8 }
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x007d:
            com.google.android.gms.measurement.internal.zzat r2 = r14.zzlo()     // Catch:{ all -> 0x02d8 }
            boolean r2 = r2.zzfb()     // Catch:{ all -> 0x02d8 }
            if (r2 != 0) goto L_0x009f
            com.google.android.gms.measurement.internal.zzbt r0 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r0 = r0.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjl()     // Catch:{ all -> 0x02d8 }
            java.lang.String r2 = "Network not connected, ignoring upload request"
            r0.zzbx(r2)     // Catch:{ all -> 0x02d8 }
            r14.zzlv()     // Catch:{ all -> 0x02d8 }
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x009f:
            com.google.android.gms.measurement.internal.zzbt r2 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.common.util.Clock r2 = r2.zzbx()     // Catch:{ all -> 0x02d8 }
            long r2 = r2.currentTimeMillis()     // Catch:{ all -> 0x02d8 }
            long r6 = com.google.android.gms.measurement.internal.zzn.zzhx()     // Catch:{ all -> 0x02d8 }
            r8 = 0
            long r8 = r2 - r6
            r6 = 0
            r14.zzd(r6, r8)     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbt r7 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzba r7 = r7.zzgp()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbd r7 = r7.zzane     // Catch:{ all -> 0x02d8 }
            long r7 = r7.get()     // Catch:{ all -> 0x02d8 }
            int r9 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x00de
            com.google.android.gms.measurement.internal.zzbt r4 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r4 = r4.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjk()     // Catch:{ all -> 0x02d8 }
            java.lang.String r5 = "Uploading events. Elapsed time since last upload attempt (ms)"
            r9 = 0
            long r9 = r2 - r7
            long r7 = java.lang.Math.abs(r9)     // Catch:{ all -> 0x02d8 }
            java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x02d8 }
            r4.zzg(r5, r7)     // Catch:{ all -> 0x02d8 }
        L_0x00de:
            com.google.android.gms.measurement.internal.zzq r4 = r14.zzjq()     // Catch:{ all -> 0x02d8 }
            java.lang.String r4 = r4.zzid()     // Catch:{ all -> 0x02d8 }
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x02d8 }
            r7 = -1
            if (r5 != 0) goto L_0x02ae
            long r9 = r14.zzatw     // Catch:{ all -> 0x02d8 }
            int r5 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x00fe
            com.google.android.gms.measurement.internal.zzq r5 = r14.zzjq()     // Catch:{ all -> 0x02d8 }
            long r7 = r5.zzik()     // Catch:{ all -> 0x02d8 }
            r14.zzatw = r7     // Catch:{ all -> 0x02d8 }
        L_0x00fe:
            com.google.android.gms.measurement.internal.zzbt r5 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzn r5 = r5.zzgq()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r7 = com.google.android.gms.measurement.internal.zzaf.zzajj     // Catch:{ all -> 0x02d8 }
            int r5 = r5.zzb(r4, r7)     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbt r7 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzn r7 = r7.zzgq()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r8 = com.google.android.gms.measurement.internal.zzaf.zzajk     // Catch:{ all -> 0x02d8 }
            int r7 = r7.zzb(r4, r8)     // Catch:{ all -> 0x02d8 }
            int r7 = java.lang.Math.max(r1, r7)     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzq r8 = r14.zzjq()     // Catch:{ all -> 0x02d8 }
            java.util.List r5 = r8.zzb(r4, r5, r7)     // Catch:{ all -> 0x02d8 }
            boolean r7 = r5.isEmpty()     // Catch:{ all -> 0x02d8 }
            if (r7 != 0) goto L_0x02d2
            java.util.Iterator r7 = r5.iterator()     // Catch:{ all -> 0x02d8 }
        L_0x012c:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x02d8 }
            if (r8 == 0) goto L_0x0147
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x02d8 }
            android.util.Pair r8 = (android.util.Pair) r8     // Catch:{ all -> 0x02d8 }
            java.lang.Object r8 = r8.first     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi r8 = (com.google.android.gms.internal.measurement.zzgi) r8     // Catch:{ all -> 0x02d8 }
            java.lang.String r9 = r8.zzaxo     // Catch:{ all -> 0x02d8 }
            boolean r9 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x02d8 }
            if (r9 != 0) goto L_0x012c
            java.lang.String r7 = r8.zzaxo     // Catch:{ all -> 0x02d8 }
            goto L_0x0148
        L_0x0147:
            r7 = r6
        L_0x0148:
            if (r7 == 0) goto L_0x0173
            r8 = 0
        L_0x014b:
            int r9 = r5.size()     // Catch:{ all -> 0x02d8 }
            if (r8 >= r9) goto L_0x0173
            java.lang.Object r9 = r5.get(r8)     // Catch:{ all -> 0x02d8 }
            android.util.Pair r9 = (android.util.Pair) r9     // Catch:{ all -> 0x02d8 }
            java.lang.Object r9 = r9.first     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi r9 = (com.google.android.gms.internal.measurement.zzgi) r9     // Catch:{ all -> 0x02d8 }
            java.lang.String r10 = r9.zzaxo     // Catch:{ all -> 0x02d8 }
            boolean r10 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x02d8 }
            if (r10 != 0) goto L_0x0170
            java.lang.String r9 = r9.zzaxo     // Catch:{ all -> 0x02d8 }
            boolean r9 = r9.equals(r7)     // Catch:{ all -> 0x02d8 }
            if (r9 != 0) goto L_0x0170
            java.util.List r5 = r5.subList(r1, r8)     // Catch:{ all -> 0x02d8 }
            goto L_0x0173
        L_0x0170:
            int r8 = r8 + 1
            goto L_0x014b
        L_0x0173:
            com.google.android.gms.internal.measurement.zzgh r7 = new com.google.android.gms.internal.measurement.zzgh     // Catch:{ all -> 0x02d8 }
            r7.<init>()     // Catch:{ all -> 0x02d8 }
            int r8 = r5.size()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi[] r8 = new com.google.android.gms.internal.measurement.zzgi[r8]     // Catch:{ all -> 0x02d8 }
            r7.zzawy = r8     // Catch:{ all -> 0x02d8 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ all -> 0x02d8 }
            int r9 = r5.size()     // Catch:{ all -> 0x02d8 }
            r8.<init>(r9)     // Catch:{ all -> 0x02d8 }
            boolean r9 = com.google.android.gms.measurement.internal.zzn.zzhz()     // Catch:{ all -> 0x02d8 }
            if (r9 == 0) goto L_0x019d
            com.google.android.gms.measurement.internal.zzbt r9 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzn r9 = r9.zzgq()     // Catch:{ all -> 0x02d8 }
            boolean r9 = r9.zzav(r4)     // Catch:{ all -> 0x02d8 }
            if (r9 == 0) goto L_0x019d
            r9 = 1
            goto L_0x019e
        L_0x019d:
            r9 = 0
        L_0x019e:
            r10 = 0
        L_0x019f:
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            int r11 = r11.length     // Catch:{ all -> 0x02d8 }
            if (r10 >= r11) goto L_0x01f7
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            java.lang.Object r12 = r5.get(r10)     // Catch:{ all -> 0x02d8 }
            android.util.Pair r12 = (android.util.Pair) r12     // Catch:{ all -> 0x02d8 }
            java.lang.Object r12 = r12.first     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi r12 = (com.google.android.gms.internal.measurement.zzgi) r12     // Catch:{ all -> 0x02d8 }
            r11[r10] = r12     // Catch:{ all -> 0x02d8 }
            java.lang.Object r11 = r5.get(r10)     // Catch:{ all -> 0x02d8 }
            android.util.Pair r11 = (android.util.Pair) r11     // Catch:{ all -> 0x02d8 }
            java.lang.Object r11 = r11.second     // Catch:{ all -> 0x02d8 }
            java.lang.Long r11 = (java.lang.Long) r11     // Catch:{ all -> 0x02d8 }
            r8.add(r11)     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            r11 = r11[r10]     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbt r12 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzn r12 = r12.zzgq()     // Catch:{ all -> 0x02d8 }
            long r12 = r12.zzhc()     // Catch:{ all -> 0x02d8 }
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch:{ all -> 0x02d8 }
            r11.zzaxn = r12     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            r11 = r11[r10]     // Catch:{ all -> 0x02d8 }
            java.lang.Long r12 = java.lang.Long.valueOf(r2)     // Catch:{ all -> 0x02d8 }
            r11.zzaxd = r12     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            r11 = r11[r10]     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzbt r12 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            r12.zzgr()     // Catch:{ all -> 0x02d8 }
            java.lang.Boolean r12 = java.lang.Boolean.valueOf(r1)     // Catch:{ all -> 0x02d8 }
            r11.zzaxs = r12     // Catch:{ all -> 0x02d8 }
            if (r9 != 0) goto L_0x01f4
            com.google.android.gms.internal.measurement.zzgi[] r11 = r7.zzawy     // Catch:{ all -> 0x02d8 }
            r11 = r11[r10]     // Catch:{ all -> 0x02d8 }
            r11.zzaya = r6     // Catch:{ all -> 0x02d8 }
        L_0x01f4:
            int r10 = r10 + 1
            goto L_0x019f
        L_0x01f7:
            com.google.android.gms.measurement.internal.zzbt r5 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x02d8 }
            r9 = 2
            boolean r5 = r5.isLoggable(r9)     // Catch:{ all -> 0x02d8 }
            if (r5 == 0) goto L_0x020c
            com.google.android.gms.measurement.internal.zzfg r5 = r14.zzjo()     // Catch:{ all -> 0x02d8 }
            java.lang.String r6 = r5.zzb(r7)     // Catch:{ all -> 0x02d8 }
        L_0x020c:
            com.google.android.gms.measurement.internal.zzfg r5 = r14.zzjo()     // Catch:{ all -> 0x02d8 }
            byte[] r11 = r5.zza(r7)     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.String> r5 = com.google.android.gms.measurement.internal.zzaf.zzajt     // Catch:{ all -> 0x02d8 }
            java.lang.Object r5 = r5.get()     // Catch:{ all -> 0x02d8 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x02d8 }
            java.net.URL r10 = new java.net.URL     // Catch:{ MalformedURLException -> 0x029a }
            r10.<init>(r5)     // Catch:{ MalformedURLException -> 0x029a }
            boolean r9 = r8.isEmpty()     // Catch:{ MalformedURLException -> 0x029a }
            r9 = r9 ^ r0
            com.google.android.gms.common.internal.Preconditions.checkArgument(r9)     // Catch:{ MalformedURLException -> 0x029a }
            java.util.List<java.lang.Long> r9 = r14.zzatu     // Catch:{ MalformedURLException -> 0x029a }
            if (r9 == 0) goto L_0x023d
            com.google.android.gms.measurement.internal.zzbt r8 = r14.zzadj     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzap r8 = r8.zzgo()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzar r8 = r8.zzjd()     // Catch:{ MalformedURLException -> 0x029a }
            java.lang.String r9 = "Set uploading progress before finishing the previous upload"
            r8.zzbx(r9)     // Catch:{ MalformedURLException -> 0x029a }
            goto L_0x0244
        L_0x023d:
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ MalformedURLException -> 0x029a }
            r9.<init>(r8)     // Catch:{ MalformedURLException -> 0x029a }
            r14.zzatu = r9     // Catch:{ MalformedURLException -> 0x029a }
        L_0x0244:
            com.google.android.gms.measurement.internal.zzbt r8 = r14.zzadj     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzba r8 = r8.zzgp()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzbd r8 = r8.zzanf     // Catch:{ MalformedURLException -> 0x029a }
            r8.set(r2)     // Catch:{ MalformedURLException -> 0x029a }
            java.lang.String r2 = "?"
            com.google.android.gms.internal.measurement.zzgi[] r3 = r7.zzawy     // Catch:{ MalformedURLException -> 0x029a }
            int r3 = r3.length     // Catch:{ MalformedURLException -> 0x029a }
            if (r3 <= 0) goto L_0x025c
            com.google.android.gms.internal.measurement.zzgi[] r2 = r7.zzawy     // Catch:{ MalformedURLException -> 0x029a }
            r2 = r2[r1]     // Catch:{ MalformedURLException -> 0x029a }
            java.lang.String r2 = r2.zztt     // Catch:{ MalformedURLException -> 0x029a }
        L_0x025c:
            com.google.android.gms.measurement.internal.zzbt r3 = r14.zzadj     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjl()     // Catch:{ MalformedURLException -> 0x029a }
            java.lang.String r7 = "Uploading data. app, uncompressed size, data"
            int r8 = r11.length     // Catch:{ MalformedURLException -> 0x029a }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ MalformedURLException -> 0x029a }
            r3.zzd(r7, r2, r8, r6)     // Catch:{ MalformedURLException -> 0x029a }
            r14.zzatq = r0     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzat r8 = r14.zzlo()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzfc r13 = new com.google.android.gms.measurement.internal.zzfc     // Catch:{ MalformedURLException -> 0x029a }
            r13.<init>(r14, r4)     // Catch:{ MalformedURLException -> 0x029a }
            r8.zzaf()     // Catch:{ MalformedURLException -> 0x029a }
            r8.zzcl()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r10)     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r11)     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r13)     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzbo r0 = r8.zzgn()     // Catch:{ MalformedURLException -> 0x029a }
            com.google.android.gms.measurement.internal.zzax r2 = new com.google.android.gms.measurement.internal.zzax     // Catch:{ MalformedURLException -> 0x029a }
            r12 = 0
            r7 = r2
            r9 = r4
            r7.<init>(r8, r9, r10, r11, r12, r13)     // Catch:{ MalformedURLException -> 0x029a }
            r0.zzd(r2)     // Catch:{ MalformedURLException -> 0x029a }
            goto L_0x02d2
        L_0x029a:
            com.google.android.gms.measurement.internal.zzbt r0 = r14.zzadj     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzap r0 = r0.zzgo()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjd()     // Catch:{ all -> 0x02d8 }
            java.lang.String r2 = "Failed to parse upload URL. Not uploading. appId"
            java.lang.Object r3 = com.google.android.gms.measurement.internal.zzap.zzbv(r4)     // Catch:{ all -> 0x02d8 }
            r0.zze(r2, r3, r5)     // Catch:{ all -> 0x02d8 }
            goto L_0x02d2
        L_0x02ae:
            r14.zzatw = r7     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzq r0 = r14.zzjq()     // Catch:{ all -> 0x02d8 }
            long r4 = com.google.android.gms.measurement.internal.zzn.zzhx()     // Catch:{ all -> 0x02d8 }
            r6 = 0
            long r6 = r2 - r4
            java.lang.String r0 = r0.zzah(r6)     // Catch:{ all -> 0x02d8 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x02d8 }
            if (r2 != 0) goto L_0x02d2
            com.google.android.gms.measurement.internal.zzq r2 = r14.zzjq()     // Catch:{ all -> 0x02d8 }
            com.google.android.gms.measurement.internal.zzg r0 = r2.zzbl(r0)     // Catch:{ all -> 0x02d8 }
            if (r0 == 0) goto L_0x02d2
            r14.zzb(r0)     // Catch:{ all -> 0x02d8 }
        L_0x02d2:
            r14.zzatr = r1
            r14.zzlw()
            return
        L_0x02d8:
            r0 = move-exception
            r14.zzatr = r1
            r14.zzlw()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzlt():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0040, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0254, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0255, code lost:
        r8 = r3;
        r7 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0041, code lost:
        r2 = r0;
        r8 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0045, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0046, code lost:
        r7 = null;
        r8 = null;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0040 A[ExcHandler: all (r0v14 'th' java.lang.Throwable A[CUSTOM_DECLARE]), PHI: r3 
      PHI: (r3v75 android.database.Cursor) = (r3v70 android.database.Cursor), (r3v70 android.database.Cursor), (r3v70 android.database.Cursor), (r3v78 android.database.Cursor), (r3v78 android.database.Cursor), (r3v78 android.database.Cursor), (r3v78 android.database.Cursor), (r3v1 android.database.Cursor), (r3v1 android.database.Cursor) binds: [B:47:0x00e2, B:53:0x00ef, B:54:?, B:24:0x0080, B:30:0x008d, B:32:0x0091, B:33:?, B:9:0x0031, B:10:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:9:0x0031] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0275 A[SYNTHETIC, Splitter:B:129:0x0275] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x027c A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x028a A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0382 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0384 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0387 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0388 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x059d A[ADDED_TO_REGION, Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x065e A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x074b A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:315:0x0765 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0785 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:367:0x08eb A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x08fa A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x08fd A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:373:0x0922 A[Catch:{ IOException -> 0x022c, all -> 0x0d01 }] */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0ce9 A[SYNTHETIC, Splitter:B:492:0x0ce9] */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0cfd A[SYNTHETIC, Splitter:B:499:0x0cfd] */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x0762 A[SYNTHETIC] */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean zzd(java.lang.String r58, long r59) {
        /*
            r57 = this;
            r1 = r57
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()
            r2.beginTransaction()
            com.google.android.gms.measurement.internal.zzfa$zza r2 = new com.google.android.gms.measurement.internal.zzfa$zza     // Catch:{ all -> 0x0d01 }
            r3 = 0
            r2.<init>(r1, r3)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r4 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            long r5 = r1.zzatw     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r2)     // Catch:{ all -> 0x0d01 }
            r4.zzaf()     // Catch:{ all -> 0x0d01 }
            r4.zzcl()     // Catch:{ all -> 0x0d01 }
            r8 = -1
            r10 = 2
            r11 = 0
            r12 = 1
            android.database.sqlite.SQLiteDatabase r15 = r4.getWritableDatabase()     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            boolean r13 = android.text.TextUtils.isEmpty(r3)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            if (r13 == 0) goto L_0x00a0
            int r13 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r13 == 0) goto L_0x004b
            java.lang.String[] r13 = new java.lang.String[r10]     // Catch:{ SQLiteException -> 0x0045, all -> 0x0040 }
            java.lang.String r14 = java.lang.String.valueOf(r5)     // Catch:{ SQLiteException -> 0x0045, all -> 0x0040 }
            r13[r11] = r14     // Catch:{ SQLiteException -> 0x0045, all -> 0x0040 }
            java.lang.String r14 = java.lang.String.valueOf(r59)     // Catch:{ SQLiteException -> 0x0045, all -> 0x0040 }
            r13[r12] = r14     // Catch:{ SQLiteException -> 0x0045, all -> 0x0040 }
            goto L_0x0053
        L_0x0040:
            r0 = move-exception
            r2 = r0
            r8 = r3
            goto L_0x0cfb
        L_0x0045:
            r0 = move-exception
            r7 = r3
            r8 = r7
        L_0x0048:
            r3 = r0
            goto L_0x0262
        L_0x004b:
            java.lang.String[] r13 = new java.lang.String[r12]     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r14 = java.lang.String.valueOf(r59)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r13[r11] = r14     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
        L_0x0053:
            int r14 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x005a
            java.lang.String r14 = "rowid <= ? and "
            goto L_0x005c
        L_0x005a:
            java.lang.String r14 = ""
        L_0x005c:
            java.lang.String r7 = java.lang.String.valueOf(r14)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            int r7 = r7.length()     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            int r7 = r7 + 148
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r3.<init>(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r7 = "select app_id, metadata_fingerprint from raw_events where "
            r3.append(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r3.append(r14)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r7 = "app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;"
            r3.append(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r3 = r3.toString()     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            android.database.Cursor r3 = r15.rawQuery(r3, r13)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            boolean r7 = r3.moveToFirst()     // Catch:{ SQLiteException -> 0x0254, all -> 0x0040 }
            if (r7 != 0) goto L_0x008d
            if (r3 == 0) goto L_0x0278
            r3.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x008d:
            java.lang.String r7 = r3.getString(r11)     // Catch:{ SQLiteException -> 0x0254, all -> 0x0040 }
            java.lang.String r13 = r3.getString(r12)     // Catch:{ SQLiteException -> 0x009d, all -> 0x0040 }
            r3.close()     // Catch:{ SQLiteException -> 0x009d, all -> 0x0040 }
            r22 = r3
            r3 = r7
            r7 = r13
            goto L_0x00fa
        L_0x009d:
            r0 = move-exception
            r8 = r3
            goto L_0x0048
        L_0x00a0:
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x00b0
            java.lang.String[] r3 = new java.lang.String[r10]     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r7 = 0
            r3[r11] = r7     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r7 = java.lang.String.valueOf(r5)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r3[r12] = r7     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            goto L_0x00b5
        L_0x00b0:
            java.lang.String[] r3 = new java.lang.String[r12]     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r7 = 0
            r3[r11] = r7     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
        L_0x00b5:
            int r7 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x00bc
            java.lang.String r7 = " and rowid <= ?"
            goto L_0x00be
        L_0x00bc:
            java.lang.String r7 = ""
        L_0x00be:
            java.lang.String r13 = java.lang.String.valueOf(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            int r13 = r13.length()     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            int r13 = r13 + 84
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r14.<init>(r13)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r13 = "select metadata_fingerprint from raw_events where app_id = ?"
            r14.append(r13)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            r14.append(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r7 = " order by rowid limit 1;"
            r14.append(r7)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            java.lang.String r7 = r14.toString()     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            android.database.Cursor r3 = r15.rawQuery(r7, r3)     // Catch:{ SQLiteException -> 0x025e, all -> 0x0259 }
            boolean r7 = r3.moveToFirst()     // Catch:{ SQLiteException -> 0x0254, all -> 0x0040 }
            if (r7 != 0) goto L_0x00ef
            if (r3 == 0) goto L_0x0278
            r3.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x00ef:
            java.lang.String r13 = r3.getString(r11)     // Catch:{ SQLiteException -> 0x0254, all -> 0x0040 }
            r3.close()     // Catch:{ SQLiteException -> 0x0254, all -> 0x0040 }
            r22 = r3
            r7 = r13
            r3 = 0
        L_0x00fa:
            java.lang.String r14 = "raw_events_metadata"
            java.lang.String[] r13 = new java.lang.String[r12]     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            java.lang.String r16 = "metadata"
            r13[r11] = r16     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            java.lang.String r16 = "app_id = ? and metadata_fingerprint = ?"
            java.lang.String[] r8 = new java.lang.String[r10]     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            r8[r11] = r3     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            r8[r12] = r7     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            r18 = 0
            r19 = 0
            java.lang.String r20 = "rowid"
            java.lang.String r21 = "2"
            r9 = r13
            r13 = r15
            r23 = r15
            r15 = r9
            r17 = r8
            android.database.Cursor r8 = r13.query(r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ SQLiteException -> 0x024e, all -> 0x0248 }
            boolean r9 = r8.moveToFirst()     // Catch:{ SQLiteException -> 0x0244 }
            if (r9 != 0) goto L_0x013b
            com.google.android.gms.measurement.internal.zzap r5 = r4.zzgo()     // Catch:{ SQLiteException -> 0x0244 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r6 = "Raw event metadata record is missing. appId"
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r3)     // Catch:{ SQLiteException -> 0x0244 }
            r5.zzg(r6, r7)     // Catch:{ SQLiteException -> 0x0244 }
            if (r8 == 0) goto L_0x0278
            r8.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x013b:
            byte[] r9 = r8.getBlob(r11)     // Catch:{ SQLiteException -> 0x0244 }
            int r13 = r9.length     // Catch:{ SQLiteException -> 0x0244 }
            com.google.android.gms.internal.measurement.zzyx r9 = com.google.android.gms.internal.measurement.zzyx.zzj(r9, r11, r13)     // Catch:{ SQLiteException -> 0x0244 }
            com.google.android.gms.internal.measurement.zzgi r13 = new com.google.android.gms.internal.measurement.zzgi     // Catch:{ SQLiteException -> 0x0244 }
            r13.<init>()     // Catch:{ SQLiteException -> 0x0244 }
            r13.zza(r9)     // Catch:{ IOException -> 0x022c }
            boolean r9 = r8.moveToNext()     // Catch:{ SQLiteException -> 0x0244 }
            if (r9 == 0) goto L_0x0163
            com.google.android.gms.measurement.internal.zzap r9 = r4.zzgo()     // Catch:{ SQLiteException -> 0x0244 }
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjg()     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r14 = "Get multiple raw event metadata records, expected one. appId"
            java.lang.Object r15 = com.google.android.gms.measurement.internal.zzap.zzbv(r3)     // Catch:{ SQLiteException -> 0x0244 }
            r9.zzg(r14, r15)     // Catch:{ SQLiteException -> 0x0244 }
        L_0x0163:
            r8.close()     // Catch:{ SQLiteException -> 0x0244 }
            r2.zzb(r13)     // Catch:{ SQLiteException -> 0x0244 }
            r13 = -1
            int r9 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x0183
            java.lang.String r9 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?"
            r13 = 3
            java.lang.String[] r14 = new java.lang.String[r13]     // Catch:{ SQLiteException -> 0x0244 }
            r14[r11] = r3     // Catch:{ SQLiteException -> 0x0244 }
            r14[r12] = r7     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r5 = java.lang.String.valueOf(r5)     // Catch:{ SQLiteException -> 0x0244 }
            r14[r10] = r5     // Catch:{ SQLiteException -> 0x0244 }
            r16 = r9
            r17 = r14
            goto L_0x018f
        L_0x0183:
            java.lang.String r5 = "app_id = ? and metadata_fingerprint = ?"
            java.lang.String[] r6 = new java.lang.String[r10]     // Catch:{ SQLiteException -> 0x0244 }
            r6[r11] = r3     // Catch:{ SQLiteException -> 0x0244 }
            r6[r12] = r7     // Catch:{ SQLiteException -> 0x0244 }
            r16 = r5
            r17 = r6
        L_0x018f:
            java.lang.String r14 = "raw_events"
            r5 = 4
            java.lang.String[] r15 = new java.lang.String[r5]     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r5 = "rowid"
            r15[r11] = r5     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r5 = "name"
            r15[r12] = r5     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r5 = "timestamp"
            r15[r10] = r5     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r5 = "data"
            r6 = 3
            r15[r6] = r5     // Catch:{ SQLiteException -> 0x0244 }
            r18 = 0
            r19 = 0
            java.lang.String r20 = "rowid"
            r21 = 0
            r13 = r23
            android.database.Cursor r5 = r13.query(r14, r15, r16, r17, r18, r19, r20, r21)     // Catch:{ SQLiteException -> 0x0244 }
            boolean r6 = r5.moveToFirst()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            if (r6 != 0) goto L_0x01d1
            com.google.android.gms.measurement.internal.zzap r6 = r4.zzgo()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjg()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            java.lang.String r7 = "Raw event data disappeared while in transaction. appId"
            java.lang.Object r8 = com.google.android.gms.measurement.internal.zzap.zzbv(r3)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r6.zzg(r7, r8)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            if (r5 == 0) goto L_0x0278
            r5.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x01d1:
            long r6 = r5.getLong(r11)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r8 = 3
            byte[] r9 = r5.getBlob(r8)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            int r8 = r9.length     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            com.google.android.gms.internal.measurement.zzyx r8 = com.google.android.gms.internal.measurement.zzyx.zzj(r9, r11, r8)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            com.google.android.gms.internal.measurement.zzgf r9 = new com.google.android.gms.internal.measurement.zzgf     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r9.<init>()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r9.zza(r8)     // Catch:{ IOException -> 0x0204 }
            java.lang.String r8 = r5.getString(r12)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r9.name = r8     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            long r13 = r5.getLong(r10)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            java.lang.Long r8 = java.lang.Long.valueOf(r13)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r9.zzawu = r8     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            boolean r6 = r2.zza(r6, r9)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            if (r6 != 0) goto L_0x0216
            if (r5 == 0) goto L_0x0278
            r5.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x0204:
            r0 = move-exception
            com.google.android.gms.measurement.internal.zzap r6 = r4.zzgo()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjd()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            java.lang.String r7 = "Data loss. Failed to merge raw event. appId"
            java.lang.Object r8 = com.google.android.gms.measurement.internal.zzap.zzbv(r3)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            r6.zze(r7, r8, r0)     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
        L_0x0216:
            boolean r6 = r5.moveToNext()     // Catch:{ SQLiteException -> 0x0227, all -> 0x0222 }
            if (r6 != 0) goto L_0x01d1
            if (r5 == 0) goto L_0x0278
            r5.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x0222:
            r0 = move-exception
            r2 = r0
            r8 = r5
            goto L_0x0cfb
        L_0x0227:
            r0 = move-exception
            r7 = r3
            r8 = r5
            goto L_0x0048
        L_0x022c:
            r0 = move-exception
            com.google.android.gms.measurement.internal.zzap r5 = r4.zzgo()     // Catch:{ SQLiteException -> 0x0244 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ SQLiteException -> 0x0244 }
            java.lang.String r6 = "Data loss. Failed to merge raw event metadata. appId"
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r3)     // Catch:{ SQLiteException -> 0x0244 }
            r5.zze(r6, r7, r0)     // Catch:{ SQLiteException -> 0x0244 }
            if (r8 == 0) goto L_0x0278
            r8.close()     // Catch:{ all -> 0x0d01 }
            goto L_0x0278
        L_0x0244:
            r0 = move-exception
            r7 = r3
            goto L_0x0048
        L_0x0248:
            r0 = move-exception
            r2 = r0
            r8 = r22
            goto L_0x0cfb
        L_0x024e:
            r0 = move-exception
            r7 = r3
            r8 = r22
            goto L_0x0048
        L_0x0254:
            r0 = move-exception
            r8 = r3
            r7 = 0
            goto L_0x0048
        L_0x0259:
            r0 = move-exception
            r2 = r0
            r8 = 0
            goto L_0x0cfb
        L_0x025e:
            r0 = move-exception
            r3 = r0
            r7 = 0
            r8 = 0
        L_0x0262:
            com.google.android.gms.measurement.internal.zzap r4 = r4.zzgo()     // Catch:{ all -> 0x0cf9 }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjd()     // Catch:{ all -> 0x0cf9 }
            java.lang.String r5 = "Data loss. Error selecting raw event. appId"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0cf9 }
            r4.zze(r5, r6, r3)     // Catch:{ all -> 0x0cf9 }
            if (r8 == 0) goto L_0x0278
            r8.close()     // Catch:{ all -> 0x0d01 }
        L_0x0278:
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r3 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x0287
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r3 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x0285
            goto L_0x0287
        L_0x0285:
            r3 = 0
            goto L_0x0288
        L_0x0287:
            r3 = 1
        L_0x0288:
            if (r3 != 0) goto L_0x0ce9
            com.google.android.gms.internal.measurement.zzgi r3 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r4 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            int r4 = r4.size()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r4 = new com.google.android.gms.internal.measurement.zzgf[r4]     // Catch:{ all -> 0x0d01 }
            r3.zzaxb = r4     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r4 = r4.zzgq()     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r3.zztt     // Catch:{ all -> 0x0d01 }
            boolean r4 = r4.zzax(r5)     // Catch:{ all -> 0x0d01 }
            r7 = 0
            r8 = 0
            r9 = 0
            r13 = 0
        L_0x02a7:
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r15 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            int r15 = r15.size()     // Catch:{ all -> 0x0d01 }
            if (r7 >= r15) goto L_0x06b9
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r15 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            java.lang.Object r15 = r15.get(r7)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf r15 = (com.google.android.gms.internal.measurement.zzgf) r15     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbn r10 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r12 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r12 = r12.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r15.name     // Catch:{ all -> 0x0d01 }
            boolean r5 = r10.zzo(r12, r5)     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x0334
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Dropping blacklisted raw event. appId"
            com.google.android.gms.internal.measurement.zzgi r10 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r10.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r10 = com.google.android.gms.measurement.internal.zzap.zzbv(r10)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r12 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzan r12 = r12.zzgl()     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r15.name     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r12.zzbs(r11)     // Catch:{ all -> 0x0d01 }
            r5.zze(r6, r10, r11)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbn r5 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            boolean r5 = r5.zzck(r6)     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x0309
            com.google.android.gms.measurement.internal.zzbn r5 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            boolean r5 = r5.zzcl(r6)     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x0307
            goto L_0x0309
        L_0x0307:
            r5 = 0
            goto L_0x030a
        L_0x0309:
            r5 = 1
        L_0x030a:
            if (r5 != 0) goto L_0x032f
            java.lang.String r5 = "_err"
            java.lang.String r6 = r15.name     // Catch:{ all -> 0x0d01 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x032f
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzfk r16 = r5.zzgm()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r5 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r5.zztt     // Catch:{ all -> 0x0d01 }
            r18 = 11
            java.lang.String r19 = "_ev"
            java.lang.String r6 = r15.name     // Catch:{ all -> 0x0d01 }
            r21 = 0
            r17 = r5
            r20 = r6
            r16.zza(r17, r18, r19, r20, r21)     // Catch:{ all -> 0x0d01 }
        L_0x032f:
            r27 = r7
            r10 = 3
            goto L_0x06b2
        L_0x0334:
            com.google.android.gms.measurement.internal.zzbn r5 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r15.name     // Catch:{ all -> 0x0d01 }
            boolean r5 = r5.zzp(r6, r10)     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x038e
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r15.name     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r6)     // Catch:{ all -> 0x0d01 }
            int r11 = r6.hashCode()     // Catch:{ all -> 0x0d01 }
            r12 = 94660(0x171c4, float:1.32647E-40)
            if (r11 == r12) goto L_0x0374
            r12 = 95025(0x17331, float:1.33158E-40)
            if (r11 == r12) goto L_0x036a
            r12 = 95027(0x17333, float:1.33161E-40)
            if (r11 == r12) goto L_0x0360
            goto L_0x037e
        L_0x0360:
            java.lang.String r11 = "_ui"
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x037e
            r6 = 1
            goto L_0x037f
        L_0x036a:
            java.lang.String r11 = "_ug"
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x037e
            r6 = 2
            goto L_0x037f
        L_0x0374:
            java.lang.String r11 = "_in"
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x037e
            r6 = 0
            goto L_0x037f
        L_0x037e:
            r6 = -1
        L_0x037f:
            switch(r6) {
                case 0: goto L_0x0384;
                case 1: goto L_0x0384;
                case 2: goto L_0x0384;
                default: goto L_0x0382;
            }     // Catch:{ all -> 0x0d01 }
        L_0x0382:
            r6 = 0
            goto L_0x0385
        L_0x0384:
            r6 = 1
        L_0x0385:
            if (r6 == 0) goto L_0x0388
            goto L_0x038e
        L_0x0388:
            r27 = r7
        L_0x038a:
            r39 = r8
            goto L_0x058d
        L_0x038e:
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            if (r6 != 0) goto L_0x0397
            r6 = 0
            com.google.android.gms.internal.measurement.zzgg[] r11 = new com.google.android.gms.internal.measurement.zzgg[r6]     // Catch:{ all -> 0x0d01 }
            r15.zzawt = r11     // Catch:{ all -> 0x0d01 }
        L_0x0397:
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r11 = r6.length     // Catch:{ all -> 0x0d01 }
            r12 = 0
            r16 = 0
            r17 = 0
        L_0x039f:
            if (r12 >= r11) goto L_0x03dd
            r10 = r6[r12]     // Catch:{ all -> 0x0d01 }
            r25 = r6
            java.lang.String r6 = "_c"
            r26 = r11
            java.lang.String r11 = r10.name     // Catch:{ all -> 0x0d01 }
            boolean r6 = r6.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x03be
            r27 = r7
            r6 = 1
            java.lang.Long r11 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            r10.zzawx = r11     // Catch:{ all -> 0x0d01 }
            r16 = 1
            goto L_0x03d4
        L_0x03be:
            r27 = r7
            java.lang.String r6 = "_r"
            java.lang.String r7 = r10.name     // Catch:{ all -> 0x0d01 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x03d4
            r6 = 1
            java.lang.Long r11 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            r10.zzawx = r11     // Catch:{ all -> 0x0d01 }
            r17 = 1
        L_0x03d4:
            int r12 = r12 + 1
            r6 = r25
            r11 = r26
            r7 = r27
            goto L_0x039f
        L_0x03dd:
            r27 = r7
            if (r16 != 0) goto L_0x0423
            if (r5 == 0) goto L_0x0423
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjl()     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = "Marking event as conversion"
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzan r10 = r10.zzgl()     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r15.name     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r10.zzbs(r11)     // Catch:{ all -> 0x0d01 }
            r6.zzg(r7, r10)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r7 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            r10 = 1
            int r7 = r7 + r10
            java.lang.Object[] r6 = java.util.Arrays.copyOf(r6, r7)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = (com.google.android.gms.internal.measurement.zzgg[]) r6     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg r7 = new com.google.android.gms.internal.measurement.zzgg     // Catch:{ all -> 0x0d01 }
            r7.<init>()     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = "_c"
            r7.name = r10     // Catch:{ all -> 0x0d01 }
            r10 = 1
            java.lang.Long r12 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0d01 }
            r7.zzawx = r12     // Catch:{ all -> 0x0d01 }
            int r10 = r6.length     // Catch:{ all -> 0x0d01 }
            r11 = 1
            int r10 = r10 - r11
            r6[r10] = r7     // Catch:{ all -> 0x0d01 }
            r15.zzawt = r6     // Catch:{ all -> 0x0d01 }
        L_0x0423:
            if (r17 != 0) goto L_0x0465
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjl()     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = "Marking event as real-time"
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzan r10 = r10.zzgl()     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r15.name     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r10.zzbs(r11)     // Catch:{ all -> 0x0d01 }
            r6.zzg(r7, r10)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r7 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            r10 = 1
            int r7 = r7 + r10
            java.lang.Object[] r6 = java.util.Arrays.copyOf(r6, r7)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = (com.google.android.gms.internal.measurement.zzgg[]) r6     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg r7 = new com.google.android.gms.internal.measurement.zzgg     // Catch:{ all -> 0x0d01 }
            r7.<init>()     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = "_r"
            r7.name = r10     // Catch:{ all -> 0x0d01 }
            r10 = 1
            java.lang.Long r10 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0d01 }
            r7.zzawx = r10     // Catch:{ all -> 0x0d01 }
            int r10 = r6.length     // Catch:{ all -> 0x0d01 }
            r11 = 1
            int r10 = r10 - r11
            r6[r10] = r7     // Catch:{ all -> 0x0d01 }
            r15.zzawt = r6     // Catch:{ all -> 0x0d01 }
        L_0x0465:
            com.google.android.gms.measurement.internal.zzq r28 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            long r29 = r57.zzls()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            r36 = 1
            r31 = r6
            com.google.android.gms.measurement.internal.zzr r6 = r28.zza(r29, r31, r32, r33, r34, r35, r36)     // Catch:{ all -> 0x0d01 }
            long r6 = r6.zzahu     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r10 = r10.zzgq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r11 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r11.zztt     // Catch:{ all -> 0x0d01 }
            int r10 = r10.zzat(r11)     // Catch:{ all -> 0x0d01 }
            long r10 = (long) r10     // Catch:{ all -> 0x0d01 }
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x04cb
            r6 = 0
        L_0x0497:
            com.google.android.gms.internal.measurement.zzgg[] r7 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            if (r6 >= r7) goto L_0x04cc
            java.lang.String r7 = "_r"
            com.google.android.gms.internal.measurement.zzgg[] r10 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            r10 = r10[r6]     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r10.name     // Catch:{ all -> 0x0d01 }
            boolean r7 = r7.equals(r10)     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x04c8
            com.google.android.gms.internal.measurement.zzgg[] r7 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            r10 = 1
            int r7 = r7 - r10
            com.google.android.gms.internal.measurement.zzgg[] r7 = new com.google.android.gms.internal.measurement.zzgg[r7]     // Catch:{ all -> 0x0d01 }
            if (r6 <= 0) goto L_0x04b9
            com.google.android.gms.internal.measurement.zzgg[] r10 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            r11 = 0
            java.lang.System.arraycopy(r10, r11, r7, r11, r6)     // Catch:{ all -> 0x0d01 }
        L_0x04b9:
            int r10 = r7.length     // Catch:{ all -> 0x0d01 }
            if (r6 >= r10) goto L_0x04c5
            com.google.android.gms.internal.measurement.zzgg[] r10 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r11 = r6 + 1
            int r12 = r7.length     // Catch:{ all -> 0x0d01 }
            int r12 = r12 - r6
            java.lang.System.arraycopy(r10, r11, r7, r6, r12)     // Catch:{ all -> 0x0d01 }
        L_0x04c5:
            r15.zzawt = r7     // Catch:{ all -> 0x0d01 }
            goto L_0x04cc
        L_0x04c8:
            int r6 = r6 + 1
            goto L_0x0497
        L_0x04cb:
            r8 = 1
        L_0x04cc:
            java.lang.String r6 = r15.name     // Catch:{ all -> 0x0d01 }
            boolean r6 = com.google.android.gms.measurement.internal.zzfk.zzcq(r6)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x038a
            if (r5 == 0) goto L_0x038a
            com.google.android.gms.measurement.internal.zzq r28 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            long r29 = r57.zzls()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            r32 = 0
            r33 = 0
            r34 = 1
            r35 = 0
            r36 = 0
            r31 = r6
            com.google.android.gms.measurement.internal.zzr r6 = r28.zza(r29, r31, r32, r33, r34, r35, r36)     // Catch:{ all -> 0x0d01 }
            long r6 = r6.zzahs     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r10 = r10.zzgq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r11 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r11.zztt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r12 = com.google.android.gms.measurement.internal.zzaf.zzajq     // Catch:{ all -> 0x0d01 }
            int r10 = r10.zzb(r11, r12)     // Catch:{ all -> 0x0d01 }
            long r10 = (long) r10     // Catch:{ all -> 0x0d01 }
            int r12 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x038a
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = "Too many conversions. Not logging as conversion. appId"
            com.google.android.gms.internal.measurement.zzgi r10 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r10 = r10.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r10 = com.google.android.gms.measurement.internal.zzap.zzbv(r10)     // Catch:{ all -> 0x0d01 }
            r6.zzg(r7, r10)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r7 = r6.length     // Catch:{ all -> 0x0d01 }
            r10 = 0
            r11 = 0
            r12 = 0
        L_0x0526:
            if (r10 >= r7) goto L_0x0550
            r37 = r7
            r7 = r6[r10]     // Catch:{ all -> 0x0d01 }
            r38 = r6
            java.lang.String r6 = "_c"
            r39 = r8
            java.lang.String r8 = r7.name     // Catch:{ all -> 0x0d01 }
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x053c
            r12 = r7
            goto L_0x0547
        L_0x053c:
            java.lang.String r6 = "_err"
            java.lang.String r7 = r7.name     // Catch:{ all -> 0x0d01 }
            boolean r6 = r6.equals(r7)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x0547
            r11 = 1
        L_0x0547:
            int r10 = r10 + 1
            r7 = r37
            r6 = r38
            r8 = r39
            goto L_0x0526
        L_0x0550:
            r39 = r8
            if (r11 == 0) goto L_0x0567
            if (r12 == 0) goto L_0x0567
            com.google.android.gms.internal.measurement.zzgg[] r6 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            r7 = 1
            com.google.android.gms.internal.measurement.zzgg[] r8 = new com.google.android.gms.internal.measurement.zzgg[r7]     // Catch:{ all -> 0x0d01 }
            r7 = 0
            r8[r7] = r12     // Catch:{ all -> 0x0d01 }
            java.lang.Object[] r6 = com.google.android.gms.common.util.ArrayUtils.removeAll((T[]) r6, (T[]) r8)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = (com.google.android.gms.internal.measurement.zzgg[]) r6     // Catch:{ all -> 0x0d01 }
            r15.zzawt = r6     // Catch:{ all -> 0x0d01 }
            goto L_0x058d
        L_0x0567:
            if (r12 == 0) goto L_0x0576
            java.lang.String r6 = "_err"
            r12.name = r6     // Catch:{ all -> 0x0d01 }
            r6 = 10
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            r12.zzawx = r6     // Catch:{ all -> 0x0d01 }
            goto L_0x058d
        L_0x0576:
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzjd()     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = "Did not find conversion parameter. appId"
            com.google.android.gms.internal.measurement.zzgi r8 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r8 = r8.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r8 = com.google.android.gms.measurement.internal.zzap.zzbv(r8)     // Catch:{ all -> 0x0d01 }
            r6.zzg(r7, r8)     // Catch:{ all -> 0x0d01 }
        L_0x058d:
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r6 = r6.zzgq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r7 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r7.zztt     // Catch:{ all -> 0x0d01 }
            boolean r6 = r6.zzbf(r7)     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x064d
            if (r5 == 0) goto L_0x064d
            com.google.android.gms.internal.measurement.zzgg[] r5 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            r6 = 0
            r7 = -1
            r8 = -1
        L_0x05a4:
            int r10 = r5.length     // Catch:{ all -> 0x0d01 }
            if (r6 >= r10) goto L_0x05c5
            java.lang.String r10 = "value"
            r11 = r5[r6]     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r11.name     // Catch:{ all -> 0x0d01 }
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r10 == 0) goto L_0x05b5
            r7 = r6
            goto L_0x05c2
        L_0x05b5:
            java.lang.String r10 = "currency"
            r11 = r5[r6]     // Catch:{ all -> 0x0d01 }
            java.lang.String r11 = r11.name     // Catch:{ all -> 0x0d01 }
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x0d01 }
            if (r10 == 0) goto L_0x05c2
            r8 = r6
        L_0x05c2:
            int r6 = r6 + 1
            goto L_0x05a4
        L_0x05c5:
            r6 = -1
            if (r7 == r6) goto L_0x05f5
            r6 = r5[r7]     // Catch:{ all -> 0x0d01 }
            java.lang.Long r6 = r6.zzawx     // Catch:{ all -> 0x0d01 }
            if (r6 != 0) goto L_0x05f7
            r6 = r5[r7]     // Catch:{ all -> 0x0d01 }
            java.lang.Double r6 = r6.zzauh     // Catch:{ all -> 0x0d01 }
            if (r6 != 0) goto L_0x05f7
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzji()     // Catch:{ all -> 0x0d01 }
            java.lang.String r8 = "Value must be specified with a numeric type."
            r6.zzbx(r8)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r7)     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "_c"
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r6)     // Catch:{ all -> 0x0d01 }
            r6 = 18
            java.lang.String r7 = "value"
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r6, r7)     // Catch:{ all -> 0x0d01 }
        L_0x05f5:
            r10 = 3
            goto L_0x064a
        L_0x05f7:
            r6 = -1
            if (r8 != r6) goto L_0x05fd
            r6 = 1
            r10 = 3
            goto L_0x0627
        L_0x05fd:
            r6 = r5[r8]     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zzamp     // Catch:{ all -> 0x0d01 }
            if (r6 == 0) goto L_0x0625
            int r8 = r6.length()     // Catch:{ all -> 0x0d01 }
            r10 = 3
            if (r8 == r10) goto L_0x060b
            goto L_0x0626
        L_0x060b:
            r8 = 0
        L_0x060c:
            int r11 = r6.length()     // Catch:{ all -> 0x0d01 }
            if (r8 >= r11) goto L_0x0623
            int r11 = r6.codePointAt(r8)     // Catch:{ all -> 0x0d01 }
            boolean r12 = java.lang.Character.isLetter(r11)     // Catch:{ all -> 0x0d01 }
            if (r12 != 0) goto L_0x061d
            goto L_0x0626
        L_0x061d:
            int r11 = java.lang.Character.charCount(r11)     // Catch:{ all -> 0x0d01 }
            int r8 = r8 + r11
            goto L_0x060c
        L_0x0623:
            r6 = 0
            goto L_0x0627
        L_0x0625:
            r10 = 3
        L_0x0626:
            r6 = 1
        L_0x0627:
            if (r6 == 0) goto L_0x064a
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r6 = r6.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r6 = r6.zzji()     // Catch:{ all -> 0x0d01 }
            java.lang.String r8 = "Value parameter discarded. You must also supply a 3-letter ISO_4217 currency code in the currency parameter."
            r6.zzbx(r8)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r7)     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "_c"
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r6)     // Catch:{ all -> 0x0d01 }
            r6 = 19
            java.lang.String r7 = "currency"
            com.google.android.gms.internal.measurement.zzgg[] r5 = zza(r5, r6, r7)     // Catch:{ all -> 0x0d01 }
        L_0x064a:
            r15.zzawt = r5     // Catch:{ all -> 0x0d01 }
            goto L_0x064e
        L_0x064d:
            r10 = 3
        L_0x064e:
            if (r4 == 0) goto L_0x06a9
            java.lang.String r5 = "_e"
            java.lang.String r6 = r15.name     // Catch:{ all -> 0x0d01 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x06a9
            com.google.android.gms.internal.measurement.zzgg[] r5 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x0692
            com.google.android.gms.internal.measurement.zzgg[] r5 = r15.zzawt     // Catch:{ all -> 0x0d01 }
            int r5 = r5.length     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x0664
            goto L_0x0692
        L_0x0664:
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = "_et"
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzfg.zzb(r15, r5)     // Catch:{ all -> 0x0d01 }
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x0689
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Engagement event does not include duration. appId"
            com.google.android.gms.internal.measurement.zzgi r7 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r7.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0d01 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0d01 }
            goto L_0x06a9
        L_0x0689:
            long r5 = r5.longValue()     // Catch:{ all -> 0x0d01 }
            r7 = 0
            long r7 = r13 + r5
            r13 = r7
            goto L_0x06a9
        L_0x0692:
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Engagement event does not contain any parameters. appId"
            com.google.android.gms.internal.measurement.zzgi r7 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r7.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0d01 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0d01 }
        L_0x06a9:
            com.google.android.gms.internal.measurement.zzgf[] r5 = r3.zzaxb     // Catch:{ all -> 0x0d01 }
            int r6 = r9 + 1
            r5[r9] = r15     // Catch:{ all -> 0x0d01 }
            r9 = r6
            r8 = r39
        L_0x06b2:
            int r7 = r27 + 1
            r10 = 2
            r11 = 0
            r12 = 1
            goto L_0x02a7
        L_0x06b9:
            java.util.List<com.google.android.gms.internal.measurement.zzgf> r5 = r2.zzauc     // Catch:{ all -> 0x0d01 }
            int r5 = r5.size()     // Catch:{ all -> 0x0d01 }
            if (r9 >= r5) goto L_0x06cb
            com.google.android.gms.internal.measurement.zzgf[] r5 = r3.zzaxb     // Catch:{ all -> 0x0d01 }
            java.lang.Object[] r5 = java.util.Arrays.copyOf(r5, r9)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r5 = (com.google.android.gms.internal.measurement.zzgf[]) r5     // Catch:{ all -> 0x0d01 }
            r3.zzaxb = r5     // Catch:{ all -> 0x0d01 }
        L_0x06cb:
            if (r4 == 0) goto L_0x079d
            com.google.android.gms.measurement.internal.zzq r4 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r3.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "_lte"
            com.google.android.gms.measurement.internal.zzfj r4 = r4.zzi(r5, r6)     // Catch:{ all -> 0x0d01 }
            if (r4 == 0) goto L_0x0709
            java.lang.Object r5 = r4.value     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x06e0
            goto L_0x0709
        L_0x06e0:
            com.google.android.gms.measurement.internal.zzfj r5 = new com.google.android.gms.measurement.internal.zzfj     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r3.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r17 = "auto"
            java.lang.String r18 = "_lte"
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.util.Clock r7 = r7.zzbx()     // Catch:{ all -> 0x0d01 }
            long r19 = r7.currentTimeMillis()     // Catch:{ all -> 0x0d01 }
            java.lang.Object r4 = r4.value     // Catch:{ all -> 0x0d01 }
            java.lang.Long r4 = (java.lang.Long) r4     // Catch:{ all -> 0x0d01 }
            long r9 = r4.longValue()     // Catch:{ all -> 0x0d01 }
            r4 = 0
            long r11 = r9 + r13
            java.lang.Long r21 = java.lang.Long.valueOf(r11)     // Catch:{ all -> 0x0d01 }
            r15 = r5
            r16 = r6
            r15.<init>(r16, r17, r18, r19, r21)     // Catch:{ all -> 0x0d01 }
            r4 = r5
            goto L_0x0726
        L_0x0709:
            com.google.android.gms.measurement.internal.zzfj r4 = new com.google.android.gms.measurement.internal.zzfj     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r3.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r26 = "auto"
            java.lang.String r27 = "_lte"
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.util.Clock r6 = r6.zzbx()     // Catch:{ all -> 0x0d01 }
            long r28 = r6.currentTimeMillis()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r30 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0d01 }
            r24 = r4
            r25 = r5
            r24.<init>(r25, r26, r27, r28, r30)     // Catch:{ all -> 0x0d01 }
        L_0x0726:
            com.google.android.gms.internal.measurement.zzgl r5 = new com.google.android.gms.internal.measurement.zzgl     // Catch:{ all -> 0x0d01 }
            r5.<init>()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "_lte"
            r5.name = r6     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.util.Clock r6 = r6.zzbx()     // Catch:{ all -> 0x0d01 }
            long r6 = r6.currentTimeMillis()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            r5.zzayl = r6     // Catch:{ all -> 0x0d01 }
            java.lang.Object r6 = r4.value     // Catch:{ all -> 0x0d01 }
            java.lang.Long r6 = (java.lang.Long) r6     // Catch:{ all -> 0x0d01 }
            r5.zzawx = r6     // Catch:{ all -> 0x0d01 }
            r6 = 0
        L_0x0746:
            com.google.android.gms.internal.measurement.zzgl[] r7 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            if (r6 >= r7) goto L_0x0762
            java.lang.String r7 = "_lte"
            com.google.android.gms.internal.measurement.zzgl[] r9 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            r9 = r9[r6]     // Catch:{ all -> 0x0d01 }
            java.lang.String r9 = r9.name     // Catch:{ all -> 0x0d01 }
            boolean r7 = r7.equals(r9)     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x075f
            com.google.android.gms.internal.measurement.zzgl[] r7 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            r7[r6] = r5     // Catch:{ all -> 0x0d01 }
            r6 = 1
            goto L_0x0763
        L_0x075f:
            int r6 = r6 + 1
            goto L_0x0746
        L_0x0762:
            r6 = 0
        L_0x0763:
            if (r6 != 0) goto L_0x077f
            com.google.android.gms.internal.measurement.zzgl[] r6 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            r9 = 1
            int r7 = r7 + r9
            java.lang.Object[] r6 = java.util.Arrays.copyOf(r6, r7)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgl[] r6 = (com.google.android.gms.internal.measurement.zzgl[]) r6     // Catch:{ all -> 0x0d01 }
            r3.zzaxc = r6     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgl[] r6 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r7 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r7.zzaxc     // Catch:{ all -> 0x0d01 }
            int r7 = r7.length     // Catch:{ all -> 0x0d01 }
            r9 = 1
            int r7 = r7 - r9
            r6[r7] = r5     // Catch:{ all -> 0x0d01 }
        L_0x077f:
            r5 = 0
            int r7 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x079d
            com.google.android.gms.measurement.internal.zzq r5 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            r5.zza(r4)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjk()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Updated lifetime engagement user property with value. Value"
            java.lang.Object r4 = r4.value     // Catch:{ all -> 0x0d01 }
            r5.zzg(r6, r4)     // Catch:{ all -> 0x0d01 }
        L_0x079d:
            java.lang.String r4 = r3.zztt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgl[] r5 = r3.zzaxc     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r6 = r3.zzaxb     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgd[] r4 = r1.zza(r4, r5, r6)     // Catch:{ all -> 0x0d01 }
            r3.zzaxt = r4     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r4 = r4.zzgq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r5 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r5.zztt     // Catch:{ all -> 0x0d01 }
            boolean r4 = r4.zzaw(r5)     // Catch:{ all -> 0x0d01 }
            if (r4 == 0) goto L_0x0b22
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ all -> 0x0d01 }
            r4.<init>()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r5 = r3.zzaxb     // Catch:{ all -> 0x0d01 }
            int r5 = r5.length     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r5 = new com.google.android.gms.internal.measurement.zzgf[r5]     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r6 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzfk r6 = r6.zzgm()     // Catch:{ all -> 0x0d01 }
            java.security.SecureRandom r6 = r6.zzmd()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r7 = r3.zzaxb     // Catch:{ all -> 0x0d01 }
            int r9 = r7.length     // Catch:{ all -> 0x0d01 }
            r10 = 0
            r11 = 0
        L_0x07d2:
            if (r10 >= r9) goto L_0x0ae9
            r12 = r7[r10]     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = r12.name     // Catch:{ all -> 0x0d01 }
            java.lang.String r14 = "_ep"
            boolean r13 = r13.equals(r14)     // Catch:{ all -> 0x0d01 }
            if (r13 == 0) goto L_0x086f
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = "_en"
            java.lang.Object r13 = com.google.android.gms.measurement.internal.zzfg.zzb(r12, r13)     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ all -> 0x0d01 }
            java.lang.Object r14 = r4.get(r13)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r14 = (com.google.android.gms.measurement.internal.zzz) r14     // Catch:{ all -> 0x0d01 }
            if (r14 != 0) goto L_0x0802
            com.google.android.gms.measurement.internal.zzq r14 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r15 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r15 = r15.zztt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r14 = r14.zzg(r15, r13)     // Catch:{ all -> 0x0d01 }
            r4.put(r13, r14)     // Catch:{ all -> 0x0d01 }
        L_0x0802:
            java.lang.Long r13 = r14.zzaij     // Catch:{ all -> 0x0d01 }
            if (r13 != 0) goto L_0x085e
            java.lang.Long r13 = r14.zzaik     // Catch:{ all -> 0x0d01 }
            long r15 = r13.longValue()     // Catch:{ all -> 0x0d01 }
            r17 = 1
            int r13 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1))
            if (r13 <= 0) goto L_0x0824
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r13 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            java.lang.String r15 = "_sr"
            r40 = r7
            java.lang.Long r7 = r14.zzaik     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r7 = com.google.android.gms.measurement.internal.zzfg.zza(r13, r15, r7)     // Catch:{ all -> 0x0d01 }
            r12.zzawt = r7     // Catch:{ all -> 0x0d01 }
            goto L_0x0826
        L_0x0824:
            r40 = r7
        L_0x0826:
            java.lang.Boolean r7 = r14.zzail     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x0848
            java.lang.Boolean r7 = r14.zzail     // Catch:{ all -> 0x0d01 }
            boolean r7 = r7.booleanValue()     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x0848
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r7 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = "_efs"
            r41 = r9
            r14 = 1
            java.lang.Long r9 = java.lang.Long.valueOf(r14)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r7 = com.google.android.gms.measurement.internal.zzfg.zza(r7, r13, r9)     // Catch:{ all -> 0x0d01 }
            r12.zzawt = r7     // Catch:{ all -> 0x0d01 }
            goto L_0x084a
        L_0x0848:
            r41 = r9
        L_0x084a:
            int r7 = r11 + 1
            r5[r11] = r12     // Catch:{ all -> 0x0d01 }
            r56 = r2
            r45 = r3
            r53 = r5
            r49 = r6
            r11 = r7
            r42 = r8
            r5 = 1
            r7 = r4
            goto L_0x0ad6
        L_0x085e:
            r40 = r7
            r41 = r9
            r56 = r2
            r45 = r3
            r7 = r4
            r53 = r5
            r49 = r6
            r42 = r8
            goto L_0x091e
        L_0x086f:
            r40 = r7
            r41 = r9
            com.google.android.gms.measurement.internal.zzbn r7 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r9 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r9 = r9.zztt     // Catch:{ all -> 0x0d01 }
            long r13 = r7.zzcj(r9)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            r7.zzgm()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r7 = r12.zzawu     // Catch:{ all -> 0x0d01 }
            r42 = r8
            long r7 = r7.longValue()     // Catch:{ all -> 0x0d01 }
            long r7 = com.google.android.gms.measurement.internal.zzfk.zzc(r7, r13)     // Catch:{ all -> 0x0d01 }
            java.lang.String r9 = "_dbg"
            r43 = r13
            r13 = 1
            java.lang.Long r15 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0d01 }
            boolean r13 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x0d01 }
            if (r13 != 0) goto L_0x08e6
            if (r15 != 0) goto L_0x08a3
            goto L_0x08e6
        L_0x08a3:
            com.google.android.gms.internal.measurement.zzgg[] r13 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            int r14 = r13.length     // Catch:{ all -> 0x0d01 }
            r45 = r3
            r3 = 0
        L_0x08a9:
            if (r3 >= r14) goto L_0x08e8
            r46 = r14
            r14 = r13[r3]     // Catch:{ all -> 0x0d01 }
            r47 = r13
            java.lang.String r13 = r14.name     // Catch:{ all -> 0x0d01 }
            boolean r13 = r9.equals(r13)     // Catch:{ all -> 0x0d01 }
            if (r13 == 0) goto L_0x08df
            boolean r3 = r15 instanceof java.lang.Long     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x08c5
            java.lang.Long r3 = r14.zzawx     // Catch:{ all -> 0x0d01 }
            boolean r3 = r15.equals(r3)     // Catch:{ all -> 0x0d01 }
            if (r3 != 0) goto L_0x08dd
        L_0x08c5:
            boolean r3 = r15 instanceof java.lang.String     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x08d1
            java.lang.String r3 = r14.zzamp     // Catch:{ all -> 0x0d01 }
            boolean r3 = r15.equals(r3)     // Catch:{ all -> 0x0d01 }
            if (r3 != 0) goto L_0x08dd
        L_0x08d1:
            boolean r3 = r15 instanceof java.lang.Double     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x08e8
            java.lang.Double r3 = r14.zzauh     // Catch:{ all -> 0x0d01 }
            boolean r3 = r15.equals(r3)     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x08e8
        L_0x08dd:
            r3 = 1
            goto L_0x08e9
        L_0x08df:
            int r3 = r3 + 1
            r14 = r46
            r13 = r47
            goto L_0x08a9
        L_0x08e6:
            r45 = r3
        L_0x08e8:
            r3 = 0
        L_0x08e9:
            if (r3 != 0) goto L_0x08fa
            com.google.android.gms.measurement.internal.zzbn r3 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r9 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r9 = r9.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = r12.name     // Catch:{ all -> 0x0d01 }
            int r3 = r3.zzq(r9, r13)     // Catch:{ all -> 0x0d01 }
            goto L_0x08fb
        L_0x08fa:
            r3 = 1
        L_0x08fb:
            if (r3 > 0) goto L_0x0922
            com.google.android.gms.measurement.internal.zzbt r7 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r7 = r7.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r7 = r7.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r8 = "Sample rate must be positive. event, rate"
            java.lang.String r9 = r12.name     // Catch:{ all -> 0x0d01 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x0d01 }
            r7.zze(r8, r9, r3)     // Catch:{ all -> 0x0d01 }
            int r3 = r11 + 1
            r5[r11] = r12     // Catch:{ all -> 0x0d01 }
        L_0x0916:
            r56 = r2
            r11 = r3
            r7 = r4
            r53 = r5
            r49 = r6
        L_0x091e:
            r5 = 1
            goto L_0x0ad6
        L_0x0922:
            java.lang.String r9 = r12.name     // Catch:{ all -> 0x0d01 }
            java.lang.Object r9 = r4.get(r9)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r9 = (com.google.android.gms.measurement.internal.zzz) r9     // Catch:{ all -> 0x0d01 }
            if (r9 != 0) goto L_0x0976
            com.google.android.gms.measurement.internal.zzq r9 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r13 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = r13.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r14 = r12.name     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r9 = r9.zzg(r13, r14)     // Catch:{ all -> 0x0d01 }
            if (r9 != 0) goto L_0x0976
            com.google.android.gms.measurement.internal.zzbt r9 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r9 = r9.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = "Event being bundled has no eventAggregate. appId, eventName"
            com.google.android.gms.internal.measurement.zzgi r14 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r14 = r14.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r15 = r12.name     // Catch:{ all -> 0x0d01 }
            r9.zze(r13, r14, r15)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r9 = new com.google.android.gms.measurement.internal.zzz     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r13 = r2.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = r13.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.String r14 = r12.name     // Catch:{ all -> 0x0d01 }
            r27 = 1
            r29 = 1
            java.lang.Long r15 = r12.zzawu     // Catch:{ all -> 0x0d01 }
            long r31 = r15.longValue()     // Catch:{ all -> 0x0d01 }
            r33 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            r24 = r9
            r25 = r13
            r26 = r14
            r24.<init>(r25, r26, r27, r29, r31, r33, r35, r36, r37, r38)     // Catch:{ all -> 0x0d01 }
        L_0x0976:
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            java.lang.String r13 = "_eid"
            java.lang.Object r13 = com.google.android.gms.measurement.internal.zzfg.zzb(r12, r13)     // Catch:{ all -> 0x0d01 }
            java.lang.Long r13 = (java.lang.Long) r13     // Catch:{ all -> 0x0d01 }
            if (r13 == 0) goto L_0x0985
            r14 = 1
            goto L_0x0986
        L_0x0985:
            r14 = 0
        L_0x0986:
            java.lang.Boolean r14 = java.lang.Boolean.valueOf(r14)     // Catch:{ all -> 0x0d01 }
            r15 = 1
            if (r3 != r15) goto L_0x09af
            int r3 = r11 + 1
            r5[r11] = r12     // Catch:{ all -> 0x0d01 }
            boolean r7 = r14.booleanValue()     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x0916
            java.lang.Long r7 = r9.zzaij     // Catch:{ all -> 0x0d01 }
            if (r7 != 0) goto L_0x09a3
            java.lang.Long r7 = r9.zzaik     // Catch:{ all -> 0x0d01 }
            if (r7 != 0) goto L_0x09a3
            java.lang.Boolean r7 = r9.zzail     // Catch:{ all -> 0x0d01 }
            if (r7 == 0) goto L_0x0916
        L_0x09a3:
            r7 = 0
            com.google.android.gms.measurement.internal.zzz r8 = r9.zza(r7, r7, r7)     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r12.name     // Catch:{ all -> 0x0d01 }
            r4.put(r7, r8)     // Catch:{ all -> 0x0d01 }
            goto L_0x0916
        L_0x09af:
            int r15 = r6.nextInt(r3)     // Catch:{ all -> 0x0d01 }
            if (r15 != 0) goto L_0x09f5
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r13 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            java.lang.String r15 = "_sr"
            r48 = r2
            long r2 = (long) r3     // Catch:{ all -> 0x0d01 }
            r49 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r2)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r6 = com.google.android.gms.measurement.internal.zzfg.zza(r13, r15, r6)     // Catch:{ all -> 0x0d01 }
            r12.zzawt = r6     // Catch:{ all -> 0x0d01 }
            int r6 = r11 + 1
            r5[r11] = r12     // Catch:{ all -> 0x0d01 }
            boolean r11 = r14.booleanValue()     // Catch:{ all -> 0x0d01 }
            if (r11 == 0) goto L_0x09de
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ all -> 0x0d01 }
            r3 = 0
            com.google.android.gms.measurement.internal.zzz r9 = r9.zza(r3, r2, r3)     // Catch:{ all -> 0x0d01 }
        L_0x09de:
            java.lang.String r2 = r12.name     // Catch:{ all -> 0x0d01 }
            java.lang.Long r3 = r12.zzawu     // Catch:{ all -> 0x0d01 }
            long r11 = r3.longValue()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r3 = r9.zza(r11, r7)     // Catch:{ all -> 0x0d01 }
            r4.put(r2, r3)     // Catch:{ all -> 0x0d01 }
            r7 = r4
            r53 = r5
            r11 = r6
            r56 = r48
            goto L_0x091e
        L_0x09f5:
            r48 = r2
            r49 = r6
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzn r2 = r2.zzgq()     // Catch:{ all -> 0x0d01 }
            r6 = r48
            com.google.android.gms.internal.measurement.zzgi r15 = r6.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r15 = r15.zztt     // Catch:{ all -> 0x0d01 }
            boolean r2 = r2.zzbh(r15)     // Catch:{ all -> 0x0d01 }
            if (r2 == 0) goto L_0x0a43
            java.lang.Long r2 = r9.zzaii     // Catch:{ all -> 0x0d01 }
            if (r2 == 0) goto L_0x0a1e
            java.lang.Long r2 = r9.zzaii     // Catch:{ all -> 0x0d01 }
            long r15 = r2.longValue()     // Catch:{ all -> 0x0d01 }
            r52 = r4
            r53 = r5
            r50 = r13
            r51 = r14
            goto L_0x0a37
        L_0x0a1e:
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            r2.zzgm()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r2 = r12.zzawv     // Catch:{ all -> 0x0d01 }
            r50 = r13
            r51 = r14
            long r13 = r2.longValue()     // Catch:{ all -> 0x0d01 }
            r52 = r4
            r53 = r5
            r4 = r43
            long r15 = com.google.android.gms.measurement.internal.zzfk.zzc(r13, r4)     // Catch:{ all -> 0x0d01 }
        L_0x0a37:
            int r2 = (r15 > r7 ? 1 : (r15 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0a3d
            r2 = 1
            goto L_0x0a3e
        L_0x0a3d:
            r2 = 0
        L_0x0a3e:
            r56 = r6
            r54 = r7
            goto L_0x0a68
        L_0x0a43:
            r52 = r4
            r53 = r5
            r50 = r13
            r51 = r14
            long r4 = r9.zzaih     // Catch:{ all -> 0x0d01 }
            java.lang.Long r2 = r12.zzawu     // Catch:{ all -> 0x0d01 }
            long r13 = r2.longValue()     // Catch:{ all -> 0x0d01 }
            r2 = 0
            r56 = r6
            r54 = r7
            long r6 = r13 - r4
            long r4 = java.lang.Math.abs(r6)     // Catch:{ all -> 0x0d01 }
            r6 = 86400000(0x5265c00, double:4.2687272E-316)
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 < 0) goto L_0x0a67
            r2 = 1
            goto L_0x0a68
        L_0x0a67:
            r2 = 0
        L_0x0a68:
            if (r2 == 0) goto L_0x0abe
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r2 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            java.lang.String r4 = "_efs"
            r5 = 1
            java.lang.Long r7 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r2 = com.google.android.gms.measurement.internal.zzfg.zza(r2, r4, r7)     // Catch:{ all -> 0x0d01 }
            r12.zzawt = r2     // Catch:{ all -> 0x0d01 }
            r57.zzjo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r2 = r12.zzawt     // Catch:{ all -> 0x0d01 }
            java.lang.String r4 = "_sr"
            long r7 = (long) r3     // Catch:{ all -> 0x0d01 }
            java.lang.Long r3 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgg[] r2 = com.google.android.gms.measurement.internal.zzfg.zza(r2, r4, r3)     // Catch:{ all -> 0x0d01 }
            r12.zzawt = r2     // Catch:{ all -> 0x0d01 }
            int r2 = r11 + 1
            r53[r11] = r12     // Catch:{ all -> 0x0d01 }
            r3 = r51
            boolean r3 = r3.booleanValue()     // Catch:{ all -> 0x0d01 }
            if (r3 == 0) goto L_0x0aa9
            java.lang.Long r3 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x0d01 }
            r4 = 1
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r4)     // Catch:{ all -> 0x0d01 }
            r4 = 0
            com.google.android.gms.measurement.internal.zzz r9 = r9.zza(r4, r3, r7)     // Catch:{ all -> 0x0d01 }
        L_0x0aa9:
            java.lang.String r3 = r12.name     // Catch:{ all -> 0x0d01 }
            java.lang.Long r4 = r12.zzawu     // Catch:{ all -> 0x0d01 }
            long r7 = r4.longValue()     // Catch:{ all -> 0x0d01 }
            r11 = r54
            com.google.android.gms.measurement.internal.zzz r4 = r9.zza(r7, r11)     // Catch:{ all -> 0x0d01 }
            r7 = r52
            r7.put(r3, r4)     // Catch:{ all -> 0x0d01 }
            r11 = r2
            goto L_0x0ad6
        L_0x0abe:
            r3 = r51
            r7 = r52
            r5 = 1
            boolean r2 = r3.booleanValue()     // Catch:{ all -> 0x0d01 }
            if (r2 == 0) goto L_0x0ad6
            java.lang.String r2 = r12.name     // Catch:{ all -> 0x0d01 }
            r13 = r50
            r3 = 0
            com.google.android.gms.measurement.internal.zzz r4 = r9.zza(r13, r3, r3)     // Catch:{ all -> 0x0d01 }
            r7.put(r2, r4)     // Catch:{ all -> 0x0d01 }
        L_0x0ad6:
            int r10 = r10 + 1
            r4 = r7
            r7 = r40
            r9 = r41
            r8 = r42
            r3 = r45
            r6 = r49
            r5 = r53
            r2 = r56
            goto L_0x07d2
        L_0x0ae9:
            r56 = r2
            r2 = r3
            r7 = r4
            r53 = r5
            r42 = r8
            com.google.android.gms.internal.measurement.zzgf[] r3 = r2.zzaxb     // Catch:{ all -> 0x0d01 }
            int r3 = r3.length     // Catch:{ all -> 0x0d01 }
            if (r11 >= r3) goto L_0x0b00
            r3 = r53
            java.lang.Object[] r3 = java.util.Arrays.copyOf(r3, r11)     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgf[] r3 = (com.google.android.gms.internal.measurement.zzgf[]) r3     // Catch:{ all -> 0x0d01 }
            r2.zzaxb = r3     // Catch:{ all -> 0x0d01 }
        L_0x0b00:
            java.util.Set r3 = r7.entrySet()     // Catch:{ all -> 0x0d01 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0d01 }
        L_0x0b08:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0d01 }
            if (r4 == 0) goto L_0x0b27
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0d01 }
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r5 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            java.lang.Object r4 = r4.getValue()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzz r4 = (com.google.android.gms.measurement.internal.zzz) r4     // Catch:{ all -> 0x0d01 }
            r5.zza(r4)     // Catch:{ all -> 0x0d01 }
            goto L_0x0b08
        L_0x0b22:
            r56 = r2
            r2 = r3
            r42 = r8
        L_0x0b27:
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x0d01 }
            r2.zzaxe = r3     // Catch:{ all -> 0x0d01 }
            r3 = -9223372036854775808
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x0d01 }
            r2.zzaxf = r3     // Catch:{ all -> 0x0d01 }
            r3 = 0
        L_0x0b3b:
            com.google.android.gms.internal.measurement.zzgf[] r4 = r2.zzaxb     // Catch:{ all -> 0x0d01 }
            int r4 = r4.length     // Catch:{ all -> 0x0d01 }
            if (r3 >= r4) goto L_0x0b6f
            com.google.android.gms.internal.measurement.zzgf[] r4 = r2.zzaxb     // Catch:{ all -> 0x0d01 }
            r4 = r4[r3]     // Catch:{ all -> 0x0d01 }
            java.lang.Long r5 = r4.zzawu     // Catch:{ all -> 0x0d01 }
            long r5 = r5.longValue()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r7 = r2.zzaxe     // Catch:{ all -> 0x0d01 }
            long r7 = r7.longValue()     // Catch:{ all -> 0x0d01 }
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 >= 0) goto L_0x0b58
            java.lang.Long r5 = r4.zzawu     // Catch:{ all -> 0x0d01 }
            r2.zzaxe = r5     // Catch:{ all -> 0x0d01 }
        L_0x0b58:
            java.lang.Long r5 = r4.zzawu     // Catch:{ all -> 0x0d01 }
            long r5 = r5.longValue()     // Catch:{ all -> 0x0d01 }
            java.lang.Long r7 = r2.zzaxf     // Catch:{ all -> 0x0d01 }
            long r7 = r7.longValue()     // Catch:{ all -> 0x0d01 }
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 <= 0) goto L_0x0b6c
            java.lang.Long r4 = r4.zzawu     // Catch:{ all -> 0x0d01 }
            r2.zzaxf = r4     // Catch:{ all -> 0x0d01 }
        L_0x0b6c:
            int r3 = r3 + 1
            goto L_0x0b3b
        L_0x0b6f:
            r3 = r56
            com.google.android.gms.internal.measurement.zzgi r4 = r3.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r4 = r4.zztt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r5 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzg r5 = r5.zzbl(r4)     // Catch:{ all -> 0x0d01 }
            if (r5 != 0) goto L_0x0b97
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Bundling raw events w/o app info. appId"
            com.google.android.gms.internal.measurement.zzgi r7 = r3.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r7.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0d01 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0d01 }
            goto L_0x0bf3
        L_0x0b97:
            com.google.android.gms.internal.measurement.zzgf[] r6 = r2.zzaxb     // Catch:{ all -> 0x0d01 }
            int r6 = r6.length     // Catch:{ all -> 0x0d01 }
            if (r6 <= 0) goto L_0x0bf3
            long r6 = r5.zzgz()     // Catch:{ all -> 0x0d01 }
            r8 = 0
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x0bab
            java.lang.Long r8 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            goto L_0x0bac
        L_0x0bab:
            r8 = 0
        L_0x0bac:
            r2.zzaxh = r8     // Catch:{ all -> 0x0d01 }
            long r8 = r5.zzgy()     // Catch:{ all -> 0x0d01 }
            r10 = 0
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 != 0) goto L_0x0bb9
            goto L_0x0bba
        L_0x0bb9:
            r6 = r8
        L_0x0bba:
            int r8 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r8 == 0) goto L_0x0bc3
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            goto L_0x0bc4
        L_0x0bc3:
            r6 = 0
        L_0x0bc4:
            r2.zzaxg = r6     // Catch:{ all -> 0x0d01 }
            r5.zzhh()     // Catch:{ all -> 0x0d01 }
            long r6 = r5.zzhe()     // Catch:{ all -> 0x0d01 }
            int r6 = (int) r6     // Catch:{ all -> 0x0d01 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0d01 }
            r2.zzaxr = r6     // Catch:{ all -> 0x0d01 }
            java.lang.Long r6 = r2.zzaxe     // Catch:{ all -> 0x0d01 }
            long r6 = r6.longValue()     // Catch:{ all -> 0x0d01 }
            r5.zzs(r6)     // Catch:{ all -> 0x0d01 }
            java.lang.Long r6 = r2.zzaxf     // Catch:{ all -> 0x0d01 }
            long r6 = r6.longValue()     // Catch:{ all -> 0x0d01 }
            r5.zzt(r6)     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r5.zzhp()     // Catch:{ all -> 0x0d01 }
            r2.zzagv = r6     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r6 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            r6.zza(r5)     // Catch:{ all -> 0x0d01 }
        L_0x0bf3:
            com.google.android.gms.internal.measurement.zzgf[] r5 = r2.zzaxb     // Catch:{ all -> 0x0d01 }
            int r5 = r5.length     // Catch:{ all -> 0x0d01 }
            if (r5 <= 0) goto L_0x0c48
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            r5.zzgr()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzbn r5 = r57.zzln()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgi r6 = r3.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = r6.zztt     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.internal.measurement.zzgb r5 = r5.zzcf(r6)     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x0c15
            java.lang.Long r6 = r5.zzawe     // Catch:{ all -> 0x0d01 }
            if (r6 != 0) goto L_0x0c10
            goto L_0x0c15
        L_0x0c10:
            java.lang.Long r5 = r5.zzawe     // Catch:{ all -> 0x0d01 }
            r2.zzaxy = r5     // Catch:{ all -> 0x0d01 }
            goto L_0x0c3f
        L_0x0c15:
            com.google.android.gms.internal.measurement.zzgi r5 = r3.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = r5.zzafx     // Catch:{ all -> 0x0d01 }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0d01 }
            if (r5 == 0) goto L_0x0c28
            r5 = -1
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0d01 }
            r2.zzaxy = r5     // Catch:{ all -> 0x0d01 }
            goto L_0x0c3f
        L_0x0c28:
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Did not find measurement config or missing version info. appId"
            com.google.android.gms.internal.measurement.zzgi r7 = r3.zzaua     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = r7.zztt     // Catch:{ all -> 0x0d01 }
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0d01 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0d01 }
        L_0x0c3f:
            com.google.android.gms.measurement.internal.zzq r5 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            r11 = r42
            r5.zza(r2, r11)     // Catch:{ all -> 0x0d01 }
        L_0x0c48:
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            java.util.List<java.lang.Long> r3 = r3.zzaub     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r3)     // Catch:{ all -> 0x0d01 }
            r2.zzaf()     // Catch:{ all -> 0x0d01 }
            r2.zzcl()     // Catch:{ all -> 0x0d01 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "rowid in ("
            r5.<init>(r6)     // Catch:{ all -> 0x0d01 }
            r6 = 0
        L_0x0c5f:
            int r7 = r3.size()     // Catch:{ all -> 0x0d01 }
            if (r6 >= r7) goto L_0x0c7c
            if (r6 == 0) goto L_0x0c6c
            java.lang.String r7 = ","
            r5.append(r7)     // Catch:{ all -> 0x0d01 }
        L_0x0c6c:
            java.lang.Object r7 = r3.get(r6)     // Catch:{ all -> 0x0d01 }
            java.lang.Long r7 = (java.lang.Long) r7     // Catch:{ all -> 0x0d01 }
            long r7 = r7.longValue()     // Catch:{ all -> 0x0d01 }
            r5.append(r7)     // Catch:{ all -> 0x0d01 }
            int r6 = r6 + 1
            goto L_0x0c5f
        L_0x0c7c:
            java.lang.String r6 = ")"
            r5.append(r6)     // Catch:{ all -> 0x0d01 }
            android.database.sqlite.SQLiteDatabase r6 = r2.getWritableDatabase()     // Catch:{ all -> 0x0d01 }
            java.lang.String r7 = "raw_events"
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0d01 }
            r8 = 0
            int r5 = r6.delete(r7, r5, r8)     // Catch:{ all -> 0x0d01 }
            int r6 = r3.size()     // Catch:{ all -> 0x0d01 }
            if (r5 == r6) goto L_0x0caf
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjd()     // Catch:{ all -> 0x0d01 }
            java.lang.String r6 = "Deleted fewer rows from raw events table than expected"
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0d01 }
            int r3 = r3.size()     // Catch:{ all -> 0x0d01 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x0d01 }
            r2.zze(r6, r5, r3)     // Catch:{ all -> 0x0d01 }
        L_0x0caf:
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            android.database.sqlite.SQLiteDatabase r3 = r2.getWritableDatabase()     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = "delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)"
            r6 = 2
            java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ SQLiteException -> 0x0cc6 }
            r7 = 0
            r6[r7] = r4     // Catch:{ SQLiteException -> 0x0cc6 }
            r7 = 1
            r6[r7] = r4     // Catch:{ SQLiteException -> 0x0cc6 }
            r3.execSQL(r5, r6)     // Catch:{ SQLiteException -> 0x0cc6 }
            goto L_0x0cd9
        L_0x0cc6:
            r0 = move-exception
            r3 = r0
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjd()     // Catch:{ all -> 0x0d01 }
            java.lang.String r5 = "Failed to remove unused event metadata. appId"
            java.lang.Object r4 = com.google.android.gms.measurement.internal.zzap.zzbv(r4)     // Catch:{ all -> 0x0d01 }
            r2.zze(r5, r4, r3)     // Catch:{ all -> 0x0d01 }
        L_0x0cd9:
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()
            r2.endTransaction()
            r2 = 1
            return r2
        L_0x0ce9:
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()     // Catch:{ all -> 0x0d01 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0d01 }
            com.google.android.gms.measurement.internal.zzq r2 = r57.zzjq()
            r2.endTransaction()
            r2 = 0
            return r2
        L_0x0cf9:
            r0 = move-exception
            r2 = r0
        L_0x0cfb:
            if (r8 == 0) goto L_0x0d00
            r8.close()     // Catch:{ all -> 0x0d01 }
        L_0x0d00:
            throw r2     // Catch:{ all -> 0x0d01 }
        L_0x0d01:
            r0 = move-exception
            r2 = r0
            com.google.android.gms.measurement.internal.zzq r3 = r57.zzjq()
            r3.endTransaction()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzd(java.lang.String, long):boolean");
    }

    @VisibleForTesting
    private static zzgg[] zza(zzgg[] zzggArr, @NonNull String str) {
        int i = 0;
        while (true) {
            if (i >= zzggArr.length) {
                i = -1;
                break;
            } else if (str.equals(zzggArr[i].name)) {
                break;
            } else {
                i++;
            }
        }
        if (i < 0) {
            return zzggArr;
        }
        return zza(zzggArr, i);
    }

    @VisibleForTesting
    private static zzgg[] zza(zzgg[] zzggArr, int i) {
        zzgg[] zzggArr2 = new zzgg[(zzggArr.length - 1)];
        if (i > 0) {
            System.arraycopy(zzggArr, 0, zzggArr2, 0, i);
        }
        if (i < zzggArr2.length) {
            System.arraycopy(zzggArr, i + 1, zzggArr2, i, zzggArr2.length - i);
        }
        return zzggArr2;
    }

    @VisibleForTesting
    private static zzgg[] zza(zzgg[] zzggArr, int i, String str) {
        for (zzgg zzgg : zzggArr) {
            if ("_err".equals(zzgg.name)) {
                return zzggArr;
            }
        }
        zzgg[] zzggArr2 = new zzgg[(zzggArr.length + 2)];
        System.arraycopy(zzggArr, 0, zzggArr2, 0, zzggArr.length);
        zzgg zzgg2 = new zzgg();
        zzgg2.name = "_err";
        zzgg2.zzawx = Long.valueOf((long) i);
        zzgg zzgg3 = new zzgg();
        zzgg3.name = "_ev";
        zzgg3.zzamp = str;
        zzggArr2[zzggArr2.length - 2] = zzgg2;
        zzggArr2[zzggArr2.length - 1] = zzgg3;
        return zzggArr2;
    }

    private final zzgd[] zza(String str, zzgl[] zzglArr, zzgf[] zzgfArr) {
        Preconditions.checkNotEmpty(str);
        return zzjp().zza(str, zzgfArr, zzglArr);
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: 0000 */
    @WorkerThread
    @VisibleForTesting
    public final void zza(int i, Throwable th, byte[] bArr, String str) {
        zzq zzjq;
        zzaf();
        zzlr();
        if (bArr == null) {
            try {
                bArr = new byte[0];
            } catch (Throwable th2) {
                this.zzatq = false;
                zzlw();
                throw th2;
            }
        }
        List<Long> list = this.zzatu;
        this.zzatu = null;
        boolean z = true;
        if ((i == 200 || i == 204) && th == null) {
            try {
                this.zzadj.zzgp().zzane.set(this.zzadj.zzbx().currentTimeMillis());
                this.zzadj.zzgp().zzanf.set(0);
                zzlv();
                this.zzadj.zzgo().zzjl().zze("Successful upload. Got network response. code, size", Integer.valueOf(i), Integer.valueOf(bArr.length));
                zzjq().beginTransaction();
                try {
                    for (Long l : list) {
                        try {
                            zzjq = zzjq();
                            long longValue = l.longValue();
                            zzjq.zzaf();
                            zzjq.zzcl();
                            if (zzjq.getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(longValue)}) != 1) {
                                throw new SQLiteException("Deleted fewer rows from queue than expected");
                            }
                        } catch (SQLiteException e) {
                            zzjq.zzgo().zzjd().zzg("Failed to delete a bundle in a queue table", e);
                            throw e;
                        } catch (SQLiteException e2) {
                            if (this.zzatv == null || !this.zzatv.contains(l)) {
                                throw e2;
                            }
                        }
                    }
                    zzjq().setTransactionSuccessful();
                    zzjq().endTransaction();
                    this.zzatv = null;
                    if (!zzlo().zzfb() || !zzlu()) {
                        this.zzatw = -1;
                        zzlv();
                    } else {
                        zzlt();
                    }
                    this.zzatl = 0;
                } catch (Throwable th3) {
                    zzjq().endTransaction();
                    throw th3;
                }
            } catch (SQLiteException e3) {
                this.zzadj.zzgo().zzjd().zzg("Database error while trying to delete uploaded bundles", e3);
                this.zzatl = this.zzadj.zzbx().elapsedRealtime();
                this.zzadj.zzgo().zzjl().zzg("Disable upload, time", Long.valueOf(this.zzatl));
            }
        } else {
            this.zzadj.zzgo().zzjl().zze("Network upload failed. Will retry later. code, error", Integer.valueOf(i), th);
            this.zzadj.zzgp().zzanf.set(this.zzadj.zzbx().currentTimeMillis());
            if (i != 503) {
                if (i != 429) {
                    z = false;
                }
            }
            if (z) {
                this.zzadj.zzgp().zzang.set(this.zzadj.zzbx().currentTimeMillis());
            }
            if (this.zzadj.zzgq().zzaz(str)) {
                zzjq().zzc(list);
            }
            zzlv();
        }
        this.zzatq = false;
        zzlw();
    }

    private final boolean zzlu() {
        zzaf();
        zzlr();
        return zzjq().zzii() || !TextUtils.isEmpty(zzjq().zzid());
    }

    @WorkerThread
    private final void zzb(zzg zzg) {
        Map map;
        zzaf();
        if (!TextUtils.isEmpty(zzg.getGmpAppId()) || (zzn.zzic() && !TextUtils.isEmpty(zzg.zzgw()))) {
            zzn zzgq = this.zzadj.zzgq();
            Builder builder = new Builder();
            String gmpAppId = zzg.getGmpAppId();
            if (TextUtils.isEmpty(gmpAppId) && zzn.zzic()) {
                gmpAppId = zzg.zzgw();
            }
            Builder encodedAuthority = builder.scheme((String) zzaf.zzajh.get()).encodedAuthority((String) zzaf.zzaji.get());
            String str = "config/app/";
            String valueOf = String.valueOf(gmpAppId);
            encodedAuthority.path(valueOf.length() != 0 ? str.concat(valueOf) : new String(str)).appendQueryParameter("app_instance_id", zzg.getAppInstanceId()).appendQueryParameter("platform", DeviceInfo.OS_NAME).appendQueryParameter("gmp_version", String.valueOf(zzgq.zzhc()));
            String uri = builder.build().toString();
            try {
                URL url = new URL(uri);
                this.zzadj.zzgo().zzjl().zzg("Fetching remote configuration", zzg.zzal());
                zzgb zzcf = zzln().zzcf(zzg.zzal());
                String zzcg = zzln().zzcg(zzg.zzal());
                if (zzcf == null || TextUtils.isEmpty(zzcg)) {
                    map = null;
                } else {
                    ArrayMap arrayMap = new ArrayMap();
                    arrayMap.put("If-Modified-Since", zzcg);
                    map = arrayMap;
                }
                this.zzatp = true;
                zzat zzlo = zzlo();
                String zzal = zzg.zzal();
                zzfd zzfd = new zzfd(this);
                zzlo.zzaf();
                zzlo.zzcl();
                Preconditions.checkNotNull(url);
                Preconditions.checkNotNull(zzfd);
                zzbo zzgn = zzlo.zzgn();
                zzax zzax = new zzax(zzlo, zzal, url, null, map, zzfd);
                zzgn.zzd((Runnable) zzax);
            } catch (MalformedURLException unused) {
                this.zzadj.zzgo().zzjd().zze("Failed to parse config URL. Not fetching. appId", zzap.zzbv(zzg.zzal()), uri);
            }
        } else {
            zzb(zzg.zzal(), OlmException.EXCEPTION_CODE_INBOUND_GROUP_SESSION_FIRST_KNOWN_INDEX, null, null, null);
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x013e A[Catch:{ all -> 0x0191, all -> 0x000f }] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x014e A[Catch:{ all -> 0x0191, all -> 0x000f }] */
    @android.support.annotation.WorkerThread
    @com.google.android.gms.common.util.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzb(java.lang.String r7, int r8, java.lang.Throwable r9, byte[] r10, java.util.Map<java.lang.String, java.util.List<java.lang.String>> r11) {
        /*
            r6 = this;
            r6.zzaf()
            r6.zzlr()
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r7)
            r0 = 0
            if (r10 != 0) goto L_0x0012
            byte[] r10 = new byte[r0]     // Catch:{ all -> 0x000f }
            goto L_0x0012
        L_0x000f:
            r7 = move-exception
            goto L_0x019a
        L_0x0012:
            com.google.android.gms.measurement.internal.zzbt r1 = r6.zzadj     // Catch:{ all -> 0x000f }
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()     // Catch:{ all -> 0x000f }
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()     // Catch:{ all -> 0x000f }
            java.lang.String r2 = "onConfigFetched. Response size"
            int r3 = r10.length     // Catch:{ all -> 0x000f }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x000f }
            r1.zzg(r2, r3)     // Catch:{ all -> 0x000f }
            com.google.android.gms.measurement.internal.zzq r1 = r6.zzjq()     // Catch:{ all -> 0x000f }
            r1.beginTransaction()     // Catch:{ all -> 0x000f }
            com.google.android.gms.measurement.internal.zzq r1 = r6.zzjq()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzg r1 = r1.zzbl(r7)     // Catch:{ all -> 0x0191 }
            r2 = 200(0xc8, float:2.8E-43)
            r3 = 1
            r4 = 304(0x130, float:4.26E-43)
            if (r8 == r2) goto L_0x0042
            r2 = 204(0xcc, float:2.86E-43)
            if (r8 == r2) goto L_0x0042
            if (r8 != r4) goto L_0x0046
        L_0x0042:
            if (r9 != 0) goto L_0x0046
            r2 = 1
            goto L_0x0047
        L_0x0046:
            r2 = 0
        L_0x0047:
            if (r1 != 0) goto L_0x005e
            com.google.android.gms.measurement.internal.zzbt r8 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzap r8 = r8.zzgo()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzar r8 = r8.zzjg()     // Catch:{ all -> 0x0191 }
            java.lang.String r9 = "App does not exist in onConfigFetched. appId"
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r7)     // Catch:{ all -> 0x0191 }
            r8.zzg(r9, r7)     // Catch:{ all -> 0x0191 }
            goto L_0x017d
        L_0x005e:
            r5 = 404(0x194, float:5.66E-43)
            if (r2 != 0) goto L_0x00ce
            if (r8 != r5) goto L_0x0065
            goto L_0x00ce
        L_0x0065:
            com.google.android.gms.measurement.internal.zzbt r10 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.common.util.Clock r10 = r10.zzbx()     // Catch:{ all -> 0x0191 }
            long r10 = r10.currentTimeMillis()     // Catch:{ all -> 0x0191 }
            r1.zzz(r10)     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzq r10 = r6.zzjq()     // Catch:{ all -> 0x0191 }
            r10.zza(r1)     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbt r10 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzap r10 = r10.zzgo()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzar r10 = r10.zzjl()     // Catch:{ all -> 0x0191 }
            java.lang.String r11 = "Fetching config failed. code, error"
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0191 }
            r10.zze(r11, r1, r9)     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbn r9 = r6.zzln()     // Catch:{ all -> 0x0191 }
            r9.zzch(r7)     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbt r7 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzba r7 = r7.zzgp()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbd r7 = r7.zzanf     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbt r9 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.common.util.Clock r9 = r9.zzbx()     // Catch:{ all -> 0x0191 }
            long r9 = r9.currentTimeMillis()     // Catch:{ all -> 0x0191 }
            r7.set(r9)     // Catch:{ all -> 0x0191 }
            r7 = 503(0x1f7, float:7.05E-43)
            if (r8 == r7) goto L_0x00b2
            r7 = 429(0x1ad, float:6.01E-43)
            if (r8 != r7) goto L_0x00b1
            goto L_0x00b2
        L_0x00b1:
            r3 = 0
        L_0x00b2:
            if (r3 == 0) goto L_0x00c9
            com.google.android.gms.measurement.internal.zzbt r7 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzba r7 = r7.zzgp()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbd r7 = r7.zzang     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzbt r8 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.common.util.Clock r8 = r8.zzbx()     // Catch:{ all -> 0x0191 }
            long r8 = r8.currentTimeMillis()     // Catch:{ all -> 0x0191 }
            r7.set(r8)     // Catch:{ all -> 0x0191 }
        L_0x00c9:
            r6.zzlv()     // Catch:{ all -> 0x0191 }
            goto L_0x017d
        L_0x00ce:
            r9 = 0
            if (r11 == 0) goto L_0x00da
            java.lang.String r2 = "Last-Modified"
            java.lang.Object r11 = r11.get(r2)     // Catch:{ all -> 0x0191 }
            java.util.List r11 = (java.util.List) r11     // Catch:{ all -> 0x0191 }
            goto L_0x00db
        L_0x00da:
            r11 = r9
        L_0x00db:
            if (r11 == 0) goto L_0x00ea
            int r2 = r11.size()     // Catch:{ all -> 0x0191 }
            if (r2 <= 0) goto L_0x00ea
            java.lang.Object r11 = r11.get(r0)     // Catch:{ all -> 0x0191 }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ all -> 0x0191 }
            goto L_0x00eb
        L_0x00ea:
            r11 = r9
        L_0x00eb:
            if (r8 == r5) goto L_0x0107
            if (r8 != r4) goto L_0x00f0
            goto L_0x0107
        L_0x00f0:
            com.google.android.gms.measurement.internal.zzbn r9 = r6.zzln()     // Catch:{ all -> 0x0191 }
            boolean r9 = r9.zza(r7, r10, r11)     // Catch:{ all -> 0x0191 }
            if (r9 != 0) goto L_0x0128
            com.google.android.gms.measurement.internal.zzq r7 = r6.zzjq()     // Catch:{ all -> 0x000f }
            r7.endTransaction()     // Catch:{ all -> 0x000f }
            r6.zzatp = r0
            r6.zzlw()
            return
        L_0x0107:
            com.google.android.gms.measurement.internal.zzbn r11 = r6.zzln()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.internal.measurement.zzgb r11 = r11.zzcf(r7)     // Catch:{ all -> 0x0191 }
            if (r11 != 0) goto L_0x0128
            com.google.android.gms.measurement.internal.zzbn r11 = r6.zzln()     // Catch:{ all -> 0x0191 }
            boolean r9 = r11.zza(r7, r9, r9)     // Catch:{ all -> 0x0191 }
            if (r9 != 0) goto L_0x0128
            com.google.android.gms.measurement.internal.zzq r7 = r6.zzjq()     // Catch:{ all -> 0x000f }
            r7.endTransaction()     // Catch:{ all -> 0x000f }
            r6.zzatp = r0
            r6.zzlw()
            return
        L_0x0128:
            com.google.android.gms.measurement.internal.zzbt r9 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.common.util.Clock r9 = r9.zzbx()     // Catch:{ all -> 0x0191 }
            long r2 = r9.currentTimeMillis()     // Catch:{ all -> 0x0191 }
            r1.zzy(r2)     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzq r9 = r6.zzjq()     // Catch:{ all -> 0x0191 }
            r9.zza(r1)     // Catch:{ all -> 0x0191 }
            if (r8 != r5) goto L_0x014e
            com.google.android.gms.measurement.internal.zzbt r8 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzap r8 = r8.zzgo()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzar r8 = r8.zzji()     // Catch:{ all -> 0x0191 }
            java.lang.String r9 = "Config not found. Using empty config. appId"
            r8.zzg(r9, r7)     // Catch:{ all -> 0x0191 }
            goto L_0x0166
        L_0x014e:
            com.google.android.gms.measurement.internal.zzbt r7 = r6.zzadj     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzap r7 = r7.zzgo()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzar r7 = r7.zzjl()     // Catch:{ all -> 0x0191 }
            java.lang.String r9 = "Successfully fetched config. Got network response. code, size"
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0191 }
            int r10 = r10.length     // Catch:{ all -> 0x0191 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ all -> 0x0191 }
            r7.zze(r9, r8, r10)     // Catch:{ all -> 0x0191 }
        L_0x0166:
            com.google.android.gms.measurement.internal.zzat r7 = r6.zzlo()     // Catch:{ all -> 0x0191 }
            boolean r7 = r7.zzfb()     // Catch:{ all -> 0x0191 }
            if (r7 == 0) goto L_0x017a
            boolean r7 = r6.zzlu()     // Catch:{ all -> 0x0191 }
            if (r7 == 0) goto L_0x017a
            r6.zzlt()     // Catch:{ all -> 0x0191 }
            goto L_0x017d
        L_0x017a:
            r6.zzlv()     // Catch:{ all -> 0x0191 }
        L_0x017d:
            com.google.android.gms.measurement.internal.zzq r7 = r6.zzjq()     // Catch:{ all -> 0x0191 }
            r7.setTransactionSuccessful()     // Catch:{ all -> 0x0191 }
            com.google.android.gms.measurement.internal.zzq r7 = r6.zzjq()     // Catch:{ all -> 0x000f }
            r7.endTransaction()     // Catch:{ all -> 0x000f }
            r6.zzatp = r0
            r6.zzlw()
            return
        L_0x0191:
            r7 = move-exception
            com.google.android.gms.measurement.internal.zzq r8 = r6.zzjq()     // Catch:{ all -> 0x000f }
            r8.endTransaction()     // Catch:{ all -> 0x000f }
            throw r7     // Catch:{ all -> 0x000f }
        L_0x019a:
            r6.zzatp = r0
            r6.zzlw()
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzb(java.lang.String, int, java.lang.Throwable, byte[], java.util.Map):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01ba  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void zzlv() {
        /*
            r20 = this;
            r0 = r20
            r20.zzaf()
            r20.zzlr()
            boolean r1 = r20.zzlz()
            if (r1 != 0) goto L_0x000f
            return
        L_0x000f:
            long r1 = r0.zzatl
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0056
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.common.util.Clock r1 = r1.zzbx()
            long r1 = r1.elapsedRealtime()
            r5 = 3600000(0x36ee80, double:1.7786363E-317)
            long r7 = r0.zzatl
            long r9 = r1 - r7
            long r1 = java.lang.Math.abs(r9)
            long r7 = r5 - r1
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 <= 0) goto L_0x0054
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Upload has been suspended. Will update scheduling later in approximately ms"
            java.lang.Long r3 = java.lang.Long.valueOf(r7)
            r1.zzg(r2, r3)
            com.google.android.gms.measurement.internal.zzay r1 = r20.zzlp()
            r1.unregister()
            com.google.android.gms.measurement.internal.zzew r1 = r20.zzlq()
            r1.cancel()
            return
        L_0x0054:
            r0.zzatl = r3
        L_0x0056:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            boolean r1 = r1.zzkr()
            if (r1 == 0) goto L_0x0265
            boolean r1 = r20.zzlu()
            if (r1 != 0) goto L_0x0066
            goto L_0x0265
        L_0x0066:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.common.util.Clock r1 = r1.zzbx()
            long r1 = r1.currentTimeMillis()
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r5 = com.google.android.gms.measurement.internal.zzaf.zzakd
            java.lang.Object r5 = r5.get()
            java.lang.Long r5 = (java.lang.Long) r5
            long r5 = r5.longValue()
            long r5 = java.lang.Math.max(r3, r5)
            com.google.android.gms.measurement.internal.zzq r7 = r20.zzjq()
            boolean r7 = r7.zzij()
            if (r7 != 0) goto L_0x0097
            com.google.android.gms.measurement.internal.zzq r7 = r20.zzjq()
            boolean r7 = r7.zzie()
            if (r7 == 0) goto L_0x0095
            goto L_0x0097
        L_0x0095:
            r7 = 0
            goto L_0x0098
        L_0x0097:
            r7 = 1
        L_0x0098:
            if (r7 == 0) goto L_0x00d4
            com.google.android.gms.measurement.internal.zzbt r9 = r0.zzadj
            com.google.android.gms.measurement.internal.zzn r9 = r9.zzgq()
            java.lang.String r9 = r9.zzhy()
            boolean r10 = android.text.TextUtils.isEmpty(r9)
            if (r10 != 0) goto L_0x00c3
            java.lang.String r10 = ".none."
            boolean r9 = r10.equals(r9)
            if (r9 != 0) goto L_0x00c3
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r9 = com.google.android.gms.measurement.internal.zzaf.zzajy
            java.lang.Object r9 = r9.get()
            java.lang.Long r9 = (java.lang.Long) r9
            long r9 = r9.longValue()
            long r9 = java.lang.Math.max(r3, r9)
            goto L_0x00e4
        L_0x00c3:
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r9 = com.google.android.gms.measurement.internal.zzaf.zzajx
            java.lang.Object r9 = r9.get()
            java.lang.Long r9 = (java.lang.Long) r9
            long r9 = r9.longValue()
            long r9 = java.lang.Math.max(r3, r9)
            goto L_0x00e4
        L_0x00d4:
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r9 = com.google.android.gms.measurement.internal.zzaf.zzajw
            java.lang.Object r9 = r9.get()
            java.lang.Long r9 = (java.lang.Long) r9
            long r9 = r9.longValue()
            long r9 = java.lang.Math.max(r3, r9)
        L_0x00e4:
            com.google.android.gms.measurement.internal.zzbt r11 = r0.zzadj
            com.google.android.gms.measurement.internal.zzba r11 = r11.zzgp()
            com.google.android.gms.measurement.internal.zzbd r11 = r11.zzane
            long r11 = r11.get()
            com.google.android.gms.measurement.internal.zzbt r13 = r0.zzadj
            com.google.android.gms.measurement.internal.zzba r13 = r13.zzgp()
            com.google.android.gms.measurement.internal.zzbd r13 = r13.zzanf
            long r13 = r13.get()
            com.google.android.gms.measurement.internal.zzq r15 = r20.zzjq()
            r16 = r9
            long r8 = r15.zzig()
            com.google.android.gms.measurement.internal.zzq r10 = r20.zzjq()
            r18 = r5
            long r5 = r10.zzih()
            long r5 = java.lang.Math.max(r8, r5)
            int r8 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r8 != 0) goto L_0x011b
        L_0x0118:
            r5 = r3
            goto L_0x0198
        L_0x011b:
            r8 = 0
            long r8 = r5 - r1
            long r5 = java.lang.Math.abs(r8)
            long r8 = r1 - r5
            long r5 = r11 - r1
            long r5 = java.lang.Math.abs(r5)
            long r10 = r1 - r5
            long r5 = r13 - r1
            long r5 = java.lang.Math.abs(r5)
            long r12 = r1 - r5
            long r1 = java.lang.Math.max(r10, r12)
            long r5 = r8 + r18
            if (r7 == 0) goto L_0x0147
            int r7 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r7 <= 0) goto L_0x0147
            long r5 = java.lang.Math.min(r8, r1)
            long r10 = r5 + r16
            r5 = r10
        L_0x0147:
            com.google.android.gms.measurement.internal.zzfg r7 = r20.zzjo()
            r10 = r16
            boolean r7 = r7.zzb(r1, r10)
            if (r7 != 0) goto L_0x0155
            long r5 = r1 + r10
        L_0x0155:
            int r1 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0198
            int r1 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r1 < 0) goto L_0x0198
            r1 = 0
        L_0x015e:
            r2 = 20
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Integer> r7 = com.google.android.gms.measurement.internal.zzaf.zzakf
            java.lang.Object r7 = r7.get()
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            r8 = 0
            int r7 = java.lang.Math.max(r8, r7)
            int r2 = java.lang.Math.min(r2, r7)
            if (r1 >= r2) goto L_0x0118
            r9 = 1
            long r9 = r9 << r1
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r2 = com.google.android.gms.measurement.internal.zzaf.zzake
            java.lang.Object r2 = r2.get()
            java.lang.Long r2 = (java.lang.Long) r2
            long r14 = r2.longValue()
            long r14 = java.lang.Math.max(r3, r14)
            long r14 = r14 * r9
            long r9 = r5 + r14
            int r2 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r2 <= 0) goto L_0x0194
            r5 = r9
            goto L_0x0198
        L_0x0194:
            int r1 = r1 + 1
            r5 = r9
            goto L_0x015e
        L_0x0198:
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 != 0) goto L_0x01ba
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Next upload time is 0"
            r1.zzbx(r2)
            com.google.android.gms.measurement.internal.zzay r1 = r20.zzlp()
            r1.unregister()
            com.google.android.gms.measurement.internal.zzew r1 = r20.zzlq()
            r1.cancel()
            return
        L_0x01ba:
            com.google.android.gms.measurement.internal.zzat r1 = r20.zzlo()
            boolean r1 = r1.zzfb()
            if (r1 != 0) goto L_0x01e2
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "No network"
            r1.zzbx(r2)
            com.google.android.gms.measurement.internal.zzay r1 = r20.zzlp()
            r1.zzey()
            com.google.android.gms.measurement.internal.zzew r1 = r20.zzlq()
            r1.cancel()
            return
        L_0x01e2:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzba r1 = r1.zzgp()
            com.google.android.gms.measurement.internal.zzbd r1 = r1.zzang
            long r1 = r1.get()
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r7 = com.google.android.gms.measurement.internal.zzaf.zzaju
            java.lang.Object r7 = r7.get()
            java.lang.Long r7 = (java.lang.Long) r7
            long r7 = r7.longValue()
            long r7 = java.lang.Math.max(r3, r7)
            com.google.android.gms.measurement.internal.zzfg r9 = r20.zzjo()
            boolean r9 = r9.zzb(r1, r7)
            if (r9 != 0) goto L_0x020e
            long r9 = r1 + r7
            long r5 = java.lang.Math.max(r5, r9)
        L_0x020e:
            com.google.android.gms.measurement.internal.zzay r1 = r20.zzlp()
            r1.unregister()
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.common.util.Clock r1 = r1.zzbx()
            long r1 = r1.currentTimeMillis()
            long r7 = r5 - r1
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 > 0) goto L_0x024a
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Long> r1 = com.google.android.gms.measurement.internal.zzaf.zzajz
            java.lang.Object r1 = r1.get()
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            long r7 = java.lang.Math.max(r3, r1)
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzba r1 = r1.zzgp()
            com.google.android.gms.measurement.internal.zzbd r1 = r1.zzane
            com.google.android.gms.measurement.internal.zzbt r2 = r0.zzadj
            com.google.android.gms.common.util.Clock r2 = r2.zzbx()
            long r2 = r2.currentTimeMillis()
            r1.set(r2)
        L_0x024a:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Upload scheduled in approximately ms"
            java.lang.Long r3 = java.lang.Long.valueOf(r7)
            r1.zzg(r2, r3)
            com.google.android.gms.measurement.internal.zzew r1 = r20.zzlq()
            r1.zzh(r7)
            return
        L_0x0265:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Nothing to upload or uploading impossible"
            r1.zzbx(r2)
            com.google.android.gms.measurement.internal.zzay r1 = r20.zzlp()
            r1.unregister()
            com.google.android.gms.measurement.internal.zzew r1 = r20.zzlq()
            r1.cancel()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzlv():void");
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzg(Runnable runnable) {
        zzaf();
        if (this.zzatm == null) {
            this.zzatm = new ArrayList();
        }
        this.zzatm.add(runnable);
    }

    @WorkerThread
    private final void zzlw() {
        zzaf();
        if (this.zzatp || this.zzatq || this.zzatr) {
            this.zzadj.zzgo().zzjl().zzd("Not stopping services. fetch, network, upload", Boolean.valueOf(this.zzatp), Boolean.valueOf(this.zzatq), Boolean.valueOf(this.zzatr));
            return;
        }
        this.zzadj.zzgo().zzjl().zzbx("Stopping uploading service(s)");
        if (this.zzatm != null) {
            for (Runnable run : this.zzatm) {
                run.run();
            }
            this.zzatm.clear();
        }
    }

    @WorkerThread
    private final Boolean zzc(zzg zzg) {
        try {
            if (zzg.zzha() != -2147483648L) {
                if (zzg.zzha() == ((long) Wrappers.packageManager(this.zzadj.getContext()).getPackageInfo(zzg.zzal(), 0).versionCode)) {
                    return Boolean.valueOf(true);
                }
            } else {
                String str = Wrappers.packageManager(this.zzadj.getContext()).getPackageInfo(zzg.zzal(), 0).versionName;
                if (zzg.zzak() != null && zzg.zzak().equals(str)) {
                    return Boolean.valueOf(true);
                }
            }
            return Boolean.valueOf(false);
        } catch (NameNotFoundException unused) {
            return null;
        }
    }

    @WorkerThread
    @VisibleForTesting
    private final boolean zzlx() {
        zzaf();
        try {
            this.zzatt = new RandomAccessFile(new File(this.zzadj.getContext().getFilesDir(), "google_app_measurement.db"), "rw").getChannel();
            this.zzats = this.zzatt.tryLock();
            if (this.zzats != null) {
                this.zzadj.zzgo().zzjl().zzbx("Storage concurrent access okay");
                return true;
            }
            this.zzadj.zzgo().zzjd().zzbx("Storage concurrent data access panic");
            return false;
        } catch (FileNotFoundException e) {
            this.zzadj.zzgo().zzjd().zzg("Failed to acquire storage lock", e);
        } catch (IOException e2) {
            this.zzadj.zzgo().zzjd().zzg("Failed to access storage lock file", e2);
        }
    }

    @WorkerThread
    @VisibleForTesting
    private final int zza(FileChannel fileChannel) {
        int i;
        zzaf();
        if (fileChannel == null || !fileChannel.isOpen()) {
            this.zzadj.zzgo().zzjd().zzbx("Bad channel to read from");
            return 0;
        }
        ByteBuffer allocate = ByteBuffer.allocate(4);
        try {
            fileChannel.position(0);
            int read = fileChannel.read(allocate);
            if (read != 4) {
                if (read != -1) {
                    this.zzadj.zzgo().zzjg().zzg("Unexpected data length. Bytes read", Integer.valueOf(read));
                }
                return 0;
            }
            allocate.flip();
            i = allocate.getInt();
            return i;
        } catch (IOException e) {
            this.zzadj.zzgo().zzjd().zzg("Failed to read from channel", e);
            i = 0;
        }
    }

    @WorkerThread
    @VisibleForTesting
    private final boolean zza(int i, FileChannel fileChannel) {
        zzaf();
        if (fileChannel == null || !fileChannel.isOpen()) {
            this.zzadj.zzgo().zzjd().zzbx("Bad channel to read from");
            return false;
        }
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt(i);
        allocate.flip();
        try {
            fileChannel.truncate(0);
            fileChannel.write(allocate);
            fileChannel.force(true);
            if (fileChannel.size() != 4) {
                this.zzadj.zzgo().zzjd().zzg("Error writing to channel. Bytes written", Long.valueOf(fileChannel.size()));
            }
            return true;
        } catch (IOException e) {
            this.zzadj.zzgo().zzjd().zzg("Failed to write to channel", e);
            return false;
        }
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzly() {
        zzaf();
        zzlr();
        if (!this.zzatk) {
            this.zzadj.zzgo().zzjj().zzbx("This instance being marked as an uploader");
            zzaf();
            zzlr();
            if (zzlz() && zzlx()) {
                int zza2 = zza(this.zzatt);
                int zzja = this.zzadj.zzgf().zzja();
                zzaf();
                if (zza2 > zzja) {
                    this.zzadj.zzgo().zzjd().zze("Panic: can't downgrade version. Previous, current version", Integer.valueOf(zza2), Integer.valueOf(zzja));
                } else if (zza2 < zzja) {
                    if (zza(zzja, this.zzatt)) {
                        this.zzadj.zzgo().zzjl().zze("Storage version upgraded. Previous, current version", Integer.valueOf(zza2), Integer.valueOf(zzja));
                    } else {
                        this.zzadj.zzgo().zzjd().zze("Storage version upgrade failed. Previous, current version", Integer.valueOf(zza2), Integer.valueOf(zzja));
                    }
                }
            }
            this.zzatk = true;
            zzlv();
        }
    }

    @WorkerThread
    private final boolean zzlz() {
        zzaf();
        zzlr();
        return this.zzatk;
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    @VisibleForTesting
    public final void zzd(zzh zzh) {
        if (this.zzatu != null) {
            this.zzatv = new ArrayList();
            this.zzatv.addAll(this.zzatu);
        }
        zzq zzjq = zzjq();
        String str = zzh.packageName;
        Preconditions.checkNotEmpty(str);
        zzjq.zzaf();
        zzjq.zzcl();
        try {
            SQLiteDatabase writableDatabase = zzjq.getWritableDatabase();
            String[] strArr = {str};
            int delete = writableDatabase.delete("apps", "app_id=?", strArr) + 0 + writableDatabase.delete("events", "app_id=?", strArr) + writableDatabase.delete("user_attributes", "app_id=?", strArr) + writableDatabase.delete("conditional_properties", "app_id=?", strArr) + writableDatabase.delete("raw_events", "app_id=?", strArr) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr) + writableDatabase.delete("queue", "app_id=?", strArr) + writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + writableDatabase.delete("main_event_params", "app_id=?", strArr);
            if (delete > 0) {
                zzjq.zzgo().zzjl().zze("Reset analytics data. app, records", str, Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzjq.zzgo().zzjd().zze("Error resetting analytics data. appId, error", zzap.zzbv(str), e);
        }
        zzh zza2 = zza(this.zzadj.getContext(), zzh.packageName, zzh.zzafx, zzh.zzagg, zzh.zzagi, zzh.zzagj, zzh.zzagx, zzh.zzagk);
        if (!this.zzadj.zzgq().zzbd(zzh.packageName) || zzh.zzagg) {
            zzf(zza2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0057 A[Catch:{ NameNotFoundException -> 0x00bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final com.google.android.gms.measurement.internal.zzh zza(android.content.Context r27, java.lang.String r28, java.lang.String r29, boolean r30, boolean r31, boolean r32, long r33, java.lang.String r35) {
        /*
            r26 = this;
            r0 = r26
            r2 = r28
            java.lang.String r1 = "Unknown"
            java.lang.String r3 = "Unknown"
            java.lang.String r4 = "Unknown"
            android.content.pm.PackageManager r5 = r27.getPackageManager()
            r6 = 0
            if (r5 != 0) goto L_0x0021
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjd()
            java.lang.String r2 = "PackageManager is null, can not log app install information"
            r1.zzbx(r2)
            return r6
        L_0x0021:
            java.lang.String r5 = r5.getInstallerPackageName(r2)     // Catch:{ IllegalArgumentException -> 0x0026 }
            goto L_0x003a
        L_0x0026:
            com.google.android.gms.measurement.internal.zzbt r5 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()
            java.lang.String r7 = "Error retrieving installer package name. appId"
            java.lang.Object r8 = com.google.android.gms.measurement.internal.zzap.zzbv(r28)
            r5.zzg(r7, r8)
            r5 = r1
        L_0x003a:
            if (r5 != 0) goto L_0x0040
            java.lang.String r1 = "manual_install"
        L_0x003e:
            r7 = r1
            goto L_0x004c
        L_0x0040:
            java.lang.String r1 = "com.android.vending"
            boolean r1 = r1.equals(r5)
            if (r1 == 0) goto L_0x004b
            java.lang.String r1 = ""
            goto L_0x003e
        L_0x004b:
            r7 = r5
        L_0x004c:
            com.google.android.gms.common.wrappers.PackageManagerWrapper r1 = com.google.android.gms.common.wrappers.Wrappers.packageManager(r27)     // Catch:{ NameNotFoundException -> 0x00bd }
            r5 = 0
            android.content.pm.PackageInfo r1 = r1.getPackageInfo(r2, r5)     // Catch:{ NameNotFoundException -> 0x00bd }
            if (r1 == 0) goto L_0x006f
            com.google.android.gms.common.wrappers.PackageManagerWrapper r3 = com.google.android.gms.common.wrappers.Wrappers.packageManager(r27)     // Catch:{ NameNotFoundException -> 0x00bd }
            java.lang.CharSequence r3 = r3.getApplicationLabel(r2)     // Catch:{ NameNotFoundException -> 0x00bd }
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ NameNotFoundException -> 0x00bd }
            if (r5 != 0) goto L_0x006a
            java.lang.String r3 = r3.toString()     // Catch:{ NameNotFoundException -> 0x00bd }
            r4 = r3
        L_0x006a:
            java.lang.String r3 = r1.versionName     // Catch:{ NameNotFoundException -> 0x00bd }
            int r1 = r1.versionCode     // Catch:{ NameNotFoundException -> 0x00bd }
            goto L_0x0071
        L_0x006f:
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
        L_0x0071:
            r4 = r3
            r16 = 0
            com.google.android.gms.measurement.internal.zzbt r3 = r0.zzadj
            r3.zzgr()
            r5 = 0
            com.google.android.gms.measurement.internal.zzbt r3 = r0.zzadj
            com.google.android.gms.measurement.internal.zzn r3 = r3.zzgq()
            boolean r3 = r3.zzbe(r2)
            if (r3 == 0) goto L_0x008a
            r18 = r33
            goto L_0x008c
        L_0x008a:
            r18 = r5
        L_0x008c:
            com.google.android.gms.measurement.internal.zzh r25 = new com.google.android.gms.measurement.internal.zzh
            long r5 = (long) r1
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzn r1 = r1.zzgq()
            long r8 = r1.zzhc()
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzfk r1 = r1.zzgm()
            r3 = r27
            long r10 = r1.zzd(r3, r2)
            r12 = 0
            r14 = 0
            java.lang.String r15 = ""
            r20 = 0
            r23 = 0
            r1 = r25
            r3 = r29
            r13 = r30
            r21 = r31
            r22 = r32
            r24 = r35
            r1.<init>(r2, r3, r4, r5, r7, r8, r10, r12, r13, r14, r15, r16, r18, r20, r21, r22, r23, r24)
            return r25
        L_0x00bd:
            com.google.android.gms.measurement.internal.zzbt r1 = r0.zzadj
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjd()
            java.lang.String r3 = "Error retrieving newly installed package info. appId, appName"
            java.lang.Object r2 = com.google.android.gms.measurement.internal.zzap.zzbv(r28)
            r1.zze(r3, r2, r4)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zza(android.content.Context, java.lang.String, java.lang.String, boolean, boolean, boolean, long, java.lang.String):com.google.android.gms.measurement.internal.zzh");
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzb(zzfh zzfh, zzh zzh) {
        zzaf();
        zzlr();
        if (TextUtils.isEmpty(zzh.zzafx) && TextUtils.isEmpty(zzh.zzagk)) {
            return;
        }
        if (!zzh.zzagg) {
            zzg(zzh);
            return;
        }
        if (this.zzadj.zzgq().zze(zzh.packageName, zzaf.zzalj) && "_ap".equals(zzfh.name)) {
            zzfj zzi = zzjq().zzi(zzh.packageName, "_ap");
            if (zzi != null && ReactScrollViewHelper.AUTO.equals(zzfh.origin) && !ReactScrollViewHelper.AUTO.equals(zzi.origin)) {
                this.zzadj.zzgo().zzjk().zzbx("Not setting lower priority ad personalization property");
                return;
            }
        }
        int zzcs = this.zzadj.zzgm().zzcs(zzfh.name);
        if (zzcs != 0) {
            this.zzadj.zzgm();
            this.zzadj.zzgm().zza(zzh.packageName, zzcs, "_ev", zzfk.zza(zzfh.name, 24, true), zzfh.name != null ? zzfh.name.length() : 0);
            return;
        }
        int zzi2 = this.zzadj.zzgm().zzi(zzfh.name, zzfh.getValue());
        if (zzi2 != 0) {
            this.zzadj.zzgm();
            String zza2 = zzfk.zza(zzfh.name, 24, true);
            Object value = zzfh.getValue();
            this.zzadj.zzgm().zza(zzh.packageName, zzi2, "_ev", zza2, (value == null || (!(value instanceof String) && !(value instanceof CharSequence))) ? 0 : String.valueOf(value).length());
            return;
        }
        Object zzj = this.zzadj.zzgm().zzj(zzfh.name, zzfh.getValue());
        if (zzj != null) {
            zzfj zzfj = new zzfj(zzh.packageName, zzfh.origin, zzfh.name, zzfh.zzaue, zzj);
            this.zzadj.zzgo().zzjk().zze("Setting user property", this.zzadj.zzgl().zzbu(zzfj.name), zzj);
            zzjq().beginTransaction();
            try {
                zzg(zzh);
                boolean zza3 = zzjq().zza(zzfj);
                zzjq().setTransactionSuccessful();
                if (zza3) {
                    this.zzadj.zzgo().zzjk().zze("User property set", this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                } else {
                    this.zzadj.zzgo().zzjd().zze("Too many unique user properties are set. Ignoring user property", this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                    this.zzadj.zzgm().zza(zzh.packageName, 9, (String) null, (String) null, 0);
                }
            } finally {
                zzjq().endTransaction();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzc(zzfh zzfh, zzh zzh) {
        zzaf();
        zzlr();
        if (TextUtils.isEmpty(zzh.zzafx) && TextUtils.isEmpty(zzh.zzagk)) {
            return;
        }
        if (!zzh.zzagg) {
            zzg(zzh);
            return;
        }
        if (this.zzadj.zzgq().zze(zzh.packageName, zzaf.zzalj) && "_ap".equals(zzfh.name)) {
            zzfj zzi = zzjq().zzi(zzh.packageName, "_ap");
            if (zzi != null && ReactScrollViewHelper.AUTO.equals(zzfh.origin) && !ReactScrollViewHelper.AUTO.equals(zzi.origin)) {
                this.zzadj.zzgo().zzjk().zzbx("Not removing higher priority ad personalization property");
                return;
            }
        }
        this.zzadj.zzgo().zzjk().zzg("Removing user property", this.zzadj.zzgl().zzbu(zzfh.name));
        zzjq().beginTransaction();
        try {
            zzg(zzh);
            zzjq().zzh(zzh.packageName, zzfh.name);
            zzjq().setTransactionSuccessful();
            this.zzadj.zzgo().zzjk().zzg("User property removed", this.zzadj.zzgl().zzbu(zzfh.name));
        } finally {
            zzjq().endTransaction();
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zzb(zzez zzez) {
        this.zzatn++;
    }

    /* access modifiers changed from: 0000 */
    public final void zzma() {
        this.zzato++;
    }

    /* access modifiers changed from: 0000 */
    public final zzbt zzmb() {
        return this.zzadj;
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzf(zzh zzh) {
        zzg zzbl;
        long j;
        PackageInfo packageInfo;
        ApplicationInfo applicationInfo;
        boolean z;
        zzq zzjq;
        String zzal;
        zzh zzh2 = zzh;
        zzaf();
        zzlr();
        Preconditions.checkNotNull(zzh);
        Preconditions.checkNotEmpty(zzh2.packageName);
        if (!TextUtils.isEmpty(zzh2.zzafx) || !TextUtils.isEmpty(zzh2.zzagk)) {
            zzg zzbl2 = zzjq().zzbl(zzh2.packageName);
            if (zzbl2 != null && TextUtils.isEmpty(zzbl2.getGmpAppId()) && !TextUtils.isEmpty(zzh2.zzafx)) {
                zzbl2.zzy(0);
                zzjq().zza(zzbl2);
                zzln().zzci(zzh2.packageName);
            }
            if (!zzh2.zzagg) {
                zzg(zzh);
                return;
            }
            long j2 = zzh2.zzagx;
            if (j2 == 0) {
                j2 = this.zzadj.zzbx().currentTimeMillis();
            }
            int i = zzh2.zzagy;
            if (!(i == 0 || i == 1)) {
                this.zzadj.zzgo().zzjg().zze("Incorrect app type, assuming installed app. appId, appType", zzap.zzbv(zzh2.packageName), Integer.valueOf(i));
                i = 0;
            }
            zzjq().beginTransaction();
            try {
                zzbl = zzjq().zzbl(zzh2.packageName);
                if (zzbl != null) {
                    this.zzadj.zzgm();
                    if (zzfk.zza(zzh2.zzafx, zzbl.getGmpAppId(), zzh2.zzagk, zzbl.zzgw())) {
                        this.zzadj.zzgo().zzjg().zzg("New GMP App Id passed in. Removing cached database data. appId", zzap.zzbv(zzbl.zzal()));
                        zzjq = zzjq();
                        zzal = zzbl.zzal();
                        zzjq.zzcl();
                        zzjq.zzaf();
                        Preconditions.checkNotEmpty(zzal);
                        SQLiteDatabase writableDatabase = zzjq.getWritableDatabase();
                        String[] strArr = {zzal};
                        int delete = writableDatabase.delete("events", "app_id=?", strArr) + 0 + writableDatabase.delete("user_attributes", "app_id=?", strArr) + writableDatabase.delete("conditional_properties", "app_id=?", strArr) + writableDatabase.delete("apps", "app_id=?", strArr) + writableDatabase.delete("raw_events", "app_id=?", strArr) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr) + writableDatabase.delete("event_filters", "app_id=?", strArr) + writableDatabase.delete("property_filters", "app_id=?", strArr) + writableDatabase.delete("audience_filter_values", "app_id=?", strArr);
                        if (delete > 0) {
                            zzjq.zzgo().zzjl().zze("Deleted application data. app, records", zzal, Integer.valueOf(delete));
                        }
                        zzbl = null;
                    }
                }
            } catch (SQLiteException e) {
                zzjq.zzgo().zzjd().zze("Error deleting application data. appId, error", zzap.zzbv(zzal), e);
            } catch (Throwable th) {
                Throwable th2 = th;
                zzjq().endTransaction();
                throw th2;
            }
            if (zzbl != null) {
                if (zzbl.zzha() != -2147483648L) {
                    if (zzbl.zzha() != zzh2.zzagd) {
                        Bundle bundle = new Bundle();
                        bundle.putString("_pv", zzbl.zzak());
                        zzad zzad = new zzad("_au", new zzaa(bundle), ReactScrollViewHelper.AUTO, j2);
                        zzc(zzad, zzh2);
                    }
                } else if (zzbl.zzak() != null && !zzbl.zzak().equals(zzh2.zzts)) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("_pv", zzbl.zzak());
                    zzad zzad2 = new zzad("_au", new zzaa(bundle2), ReactScrollViewHelper.AUTO, j2);
                    zzc(zzad2, zzh2);
                }
            }
            zzg(zzh);
            zzz zzz = i == 0 ? zzjq().zzg(zzh2.packageName, "_f") : i == 1 ? zzjq().zzg(zzh2.packageName, "_v") : null;
            if (zzz == null) {
                long j3 = DateUtils.MILLIS_PER_HOUR * ((j2 / DateUtils.MILLIS_PER_HOUR) + 1);
                if (i == 0) {
                    j = 1;
                    zzfh zzfh = new zzfh("_fot", j2, Long.valueOf(j3), ReactScrollViewHelper.AUTO);
                    zzb(zzfh, zzh2);
                    if (this.zzadj.zzgq().zzbg(zzh2.zzafx)) {
                        zzaf();
                        this.zzadj.zzkg().zzcd(zzh2.packageName);
                    }
                    zzaf();
                    zzlr();
                    Bundle bundle3 = new Bundle();
                    bundle3.putLong("_c", 1);
                    bundle3.putLong("_r", 1);
                    bundle3.putLong("_uwa", 0);
                    bundle3.putLong("_pfo", 0);
                    bundle3.putLong("_sys", 0);
                    bundle3.putLong("_sysu", 0);
                    if (this.zzadj.zzgq().zzbd(zzh2.packageName) && zzh2.zzagz) {
                        bundle3.putLong("_dac", 1);
                    }
                    if (this.zzadj.getContext().getPackageManager() == null) {
                        this.zzadj.zzgo().zzjd().zzg("PackageManager is null, first open report might be inaccurate. appId", zzap.zzbv(zzh2.packageName));
                    } else {
                        try {
                            packageInfo = Wrappers.packageManager(this.zzadj.getContext()).getPackageInfo(zzh2.packageName, 0);
                        } catch (NameNotFoundException e2) {
                            this.zzadj.zzgo().zzjd().zze("Package info is null, first open report might be inaccurate. appId", zzap.zzbv(zzh2.packageName), e2);
                            packageInfo = null;
                        }
                        if (!(packageInfo == null || packageInfo.firstInstallTime == 0)) {
                            if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) {
                                bundle3.putLong("_uwa", 1);
                                z = false;
                            } else {
                                z = true;
                            }
                            zzfh zzfh2 = new zzfh("_fi", j2, Long.valueOf(z ? 1 : 0), ReactScrollViewHelper.AUTO);
                            zzb(zzfh2, zzh2);
                        }
                        try {
                            applicationInfo = Wrappers.packageManager(this.zzadj.getContext()).getApplicationInfo(zzh2.packageName, 0);
                        } catch (NameNotFoundException e3) {
                            this.zzadj.zzgo().zzjd().zze("Application info is null, first open report might be inaccurate. appId", zzap.zzbv(zzh2.packageName), e3);
                            applicationInfo = null;
                        }
                        if (applicationInfo != null) {
                            if ((applicationInfo.flags & 1) != 0) {
                                bundle3.putLong("_sys", 1);
                            }
                            if ((applicationInfo.flags & 128) != 0) {
                                bundle3.putLong("_sysu", 1);
                            }
                        }
                    }
                    zzq zzjq2 = zzjq();
                    String str = zzh2.packageName;
                    Preconditions.checkNotEmpty(str);
                    zzjq2.zzaf();
                    zzjq2.zzcl();
                    long zzn = zzjq2.zzn(str, "first_open_count");
                    if (zzn >= 0) {
                        bundle3.putLong("_pfo", zzn);
                    }
                    zzad zzad3 = new zzad("_f", new zzaa(bundle3), ReactScrollViewHelper.AUTO, j2);
                    zzc(zzad3, zzh2);
                } else {
                    j = 1;
                    if (i == 1) {
                        zzfh zzfh3 = new zzfh("_fvt", j2, Long.valueOf(j3), ReactScrollViewHelper.AUTO);
                        zzb(zzfh3, zzh2);
                        zzaf();
                        zzlr();
                        Bundle bundle4 = new Bundle();
                        bundle4.putLong("_c", 1);
                        bundle4.putLong("_r", 1);
                        if (this.zzadj.zzgq().zzbd(zzh2.packageName) && zzh2.zzagz) {
                            bundle4.putLong("_dac", 1);
                        }
                        zzad zzad4 = new zzad("_v", new zzaa(bundle4), ReactScrollViewHelper.AUTO, j2);
                        zzc(zzad4, zzh2);
                    }
                }
                Bundle bundle5 = new Bundle();
                bundle5.putLong("_et", j);
                zzad zzad5 = new zzad("_e", new zzaa(bundle5), ReactScrollViewHelper.AUTO, j2);
                zzc(zzad5, zzh2);
            } else if (zzh2.zzagw) {
                zzad zzad6 = new zzad("_cd", new zzaa(new Bundle()), ReactScrollViewHelper.AUTO, j2);
                zzc(zzad6, zzh2);
            }
            zzjq().setTransactionSuccessful();
            zzjq().endTransaction();
        }
    }

    @WorkerThread
    private final zzh zzco(String str) {
        String str2 = str;
        zzg zzbl = zzjq().zzbl(str2);
        if (zzbl == null || TextUtils.isEmpty(zzbl.zzak())) {
            this.zzadj.zzgo().zzjk().zzg("No app data available; dropping", str2);
            return null;
        }
        Boolean zzc = zzc(zzbl);
        if (zzc == null || zzc.booleanValue()) {
            zzh zzh = new zzh(str2, zzbl.getGmpAppId(), zzbl.zzak(), zzbl.zzha(), zzbl.zzhb(), zzbl.zzhc(), zzbl.zzhd(), (String) null, zzbl.isMeasurementEnabled(), false, zzbl.getFirebaseInstanceId(), zzbl.zzhq(), 0, 0, zzbl.zzhr(), zzbl.zzhs(), false, zzbl.zzgw());
            return zzh;
        }
        this.zzadj.zzgo().zzjd().zzg("App version does not match; dropping. appId", zzap.zzbv(str));
        return null;
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zze(zzl zzl) {
        zzh zzco = zzco(zzl.packageName);
        if (zzco != null) {
            zzb(zzl, zzco);
        }
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzb(zzl zzl, zzh zzh) {
        Preconditions.checkNotNull(zzl);
        Preconditions.checkNotEmpty(zzl.packageName);
        Preconditions.checkNotNull(zzl.origin);
        Preconditions.checkNotNull(zzl.zzahb);
        Preconditions.checkNotEmpty(zzl.zzahb.name);
        zzaf();
        zzlr();
        if (TextUtils.isEmpty(zzh.zzafx) && TextUtils.isEmpty(zzh.zzagk)) {
            return;
        }
        if (!zzh.zzagg) {
            zzg(zzh);
            return;
        }
        zzl zzl2 = new zzl(zzl);
        boolean z = false;
        zzl2.active = false;
        zzjq().beginTransaction();
        try {
            zzl zzj = zzjq().zzj(zzl2.packageName, zzl2.zzahb.name);
            if (zzj != null && !zzj.origin.equals(zzl2.origin)) {
                this.zzadj.zzgo().zzjg().zzd("Updating a conditional user property with different origin. name, origin, origin (from DB)", this.zzadj.zzgl().zzbu(zzl2.zzahb.name), zzl2.origin, zzj.origin);
            }
            if (zzj != null && zzj.active) {
                zzl2.origin = zzj.origin;
                zzl2.creationTimestamp = zzj.creationTimestamp;
                zzl2.triggerTimeout = zzj.triggerTimeout;
                zzl2.triggerEventName = zzj.triggerEventName;
                zzl2.zzahd = zzj.zzahd;
                zzl2.active = zzj.active;
                zzfh zzfh = new zzfh(zzl2.zzahb.name, zzj.zzahb.zzaue, zzl2.zzahb.getValue(), zzj.zzahb.origin);
                zzl2.zzahb = zzfh;
            } else if (TextUtils.isEmpty(zzl2.triggerEventName)) {
                zzfh zzfh2 = new zzfh(zzl2.zzahb.name, zzl2.creationTimestamp, zzl2.zzahb.getValue(), zzl2.zzahb.origin);
                zzl2.zzahb = zzfh2;
                zzl2.active = true;
                z = true;
            }
            if (zzl2.active) {
                zzfh zzfh3 = zzl2.zzahb;
                zzfj zzfj = new zzfj(zzl2.packageName, zzl2.origin, zzfh3.name, zzfh3.zzaue, zzfh3.getValue());
                if (zzjq().zza(zzfj)) {
                    this.zzadj.zzgo().zzjk().zzd("User property updated immediately", zzl2.packageName, this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                } else {
                    this.zzadj.zzgo().zzjd().zzd("(2)Too many active user properties, ignoring", zzap.zzbv(zzl2.packageName), this.zzadj.zzgl().zzbu(zzfj.name), zzfj.value);
                }
                if (z && zzl2.zzahd != null) {
                    zzd(new zzad(zzl2.zzahd, zzl2.creationTimestamp), zzh);
                }
            }
            if (zzjq().zza(zzl2)) {
                this.zzadj.zzgo().zzjk().zzd("Conditional property added", zzl2.packageName, this.zzadj.zzgl().zzbu(zzl2.zzahb.name), zzl2.zzahb.getValue());
            } else {
                this.zzadj.zzgo().zzjd().zzd("Too many conditional properties, ignoring", zzap.zzbv(zzl2.packageName), this.zzadj.zzgl().zzbu(zzl2.zzahb.name), zzl2.zzahb.getValue());
            }
            zzjq().setTransactionSuccessful();
        } finally {
            zzjq().endTransaction();
        }
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzf(zzl zzl) {
        zzh zzco = zzco(zzl.packageName);
        if (zzco != null) {
            zzc(zzl, zzco);
        }
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zzc(zzl zzl, zzh zzh) {
        Preconditions.checkNotNull(zzl);
        Preconditions.checkNotEmpty(zzl.packageName);
        Preconditions.checkNotNull(zzl.zzahb);
        Preconditions.checkNotEmpty(zzl.zzahb.name);
        zzaf();
        zzlr();
        if (TextUtils.isEmpty(zzh.zzafx) && TextUtils.isEmpty(zzh.zzagk)) {
            return;
        }
        if (!zzh.zzagg) {
            zzg(zzh);
            return;
        }
        zzjq().beginTransaction();
        try {
            zzg(zzh);
            zzl zzj = zzjq().zzj(zzl.packageName, zzl.zzahb.name);
            if (zzj != null) {
                this.zzadj.zzgo().zzjk().zze("Removing conditional user property", zzl.packageName, this.zzadj.zzgl().zzbu(zzl.zzahb.name));
                zzjq().zzk(zzl.packageName, zzl.zzahb.name);
                if (zzj.active) {
                    zzjq().zzh(zzl.packageName, zzl.zzahb.name);
                }
                if (zzl.zzahe != null) {
                    Bundle bundle = null;
                    if (zzl.zzahe.zzaid != null) {
                        bundle = zzl.zzahe.zzaid.zziv();
                    }
                    Bundle bundle2 = bundle;
                    zzd(this.zzadj.zzgm().zza(zzl.packageName, zzl.zzahe.name, bundle2, zzj.origin, zzl.zzahe.zzaip, true, false), zzh);
                }
            } else {
                this.zzadj.zzgo().zzjg().zze("Conditional user property doesn't exist", zzap.zzbv(zzl.packageName), this.zzadj.zzgl().zzbu(zzl.zzahb.name));
            }
            zzjq().setTransactionSuccessful();
        } finally {
            zzjq().endTransaction();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x010c  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0136  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x015a  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.google.android.gms.measurement.internal.zzg zzg(com.google.android.gms.measurement.internal.zzh r9) {
        /*
            r8 = this;
            r8.zzaf()
            r8.zzlr()
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r9)
            java.lang.String r0 = r9.packageName
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r0)
            com.google.android.gms.measurement.internal.zzq r0 = r8.zzjq()
            java.lang.String r1 = r9.packageName
            com.google.android.gms.measurement.internal.zzg r0 = r0.zzbl(r1)
            com.google.android.gms.measurement.internal.zzbt r1 = r8.zzadj
            com.google.android.gms.measurement.internal.zzba r1 = r1.zzgp()
            java.lang.String r2 = r9.packageName
            java.lang.String r1 = r1.zzbz(r2)
            r2 = 1
            if (r0 != 0) goto L_0x0042
            com.google.android.gms.measurement.internal.zzg r0 = new com.google.android.gms.measurement.internal.zzg
            com.google.android.gms.measurement.internal.zzbt r3 = r8.zzadj
            java.lang.String r4 = r9.packageName
            r0.<init>(r3, r4)
            com.google.android.gms.measurement.internal.zzbt r3 = r8.zzadj
            com.google.android.gms.measurement.internal.zzfk r3 = r3.zzgm()
            java.lang.String r3 = r3.zzmf()
            r0.zzam(r3)
            r0.zzap(r1)
        L_0x0040:
            r1 = 1
            goto L_0x005e
        L_0x0042:
            java.lang.String r3 = r0.zzgx()
            boolean r3 = r1.equals(r3)
            if (r3 != 0) goto L_0x005d
            r0.zzap(r1)
            com.google.android.gms.measurement.internal.zzbt r1 = r8.zzadj
            com.google.android.gms.measurement.internal.zzfk r1 = r1.zzgm()
            java.lang.String r1 = r1.zzmf()
            r0.zzam(r1)
            goto L_0x0040
        L_0x005d:
            r1 = 0
        L_0x005e:
            java.lang.String r3 = r9.zzafx
            java.lang.String r4 = r0.getGmpAppId()
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 != 0) goto L_0x0070
            java.lang.String r1 = r9.zzafx
            r0.zzan(r1)
            r1 = 1
        L_0x0070:
            java.lang.String r3 = r9.zzagk
            java.lang.String r4 = r0.zzgw()
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 != 0) goto L_0x0082
            java.lang.String r1 = r9.zzagk
            r0.zzao(r1)
            r1 = 1
        L_0x0082:
            java.lang.String r3 = r9.zzafz
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x009c
            java.lang.String r3 = r9.zzafz
            java.lang.String r4 = r0.getFirebaseInstanceId()
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x009c
            java.lang.String r1 = r9.zzafz
            r0.zzaq(r1)
            r1 = 1
        L_0x009c:
            long r3 = r9.zzadt
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x00b4
            long r3 = r9.zzadt
            long r5 = r0.zzhc()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x00b4
            long r3 = r9.zzadt
            r0.zzv(r3)
            r1 = 1
        L_0x00b4:
            java.lang.String r3 = r9.zzts
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x00ce
            java.lang.String r3 = r9.zzts
            java.lang.String r4 = r0.zzak()
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x00ce
            java.lang.String r1 = r9.zzts
            r0.setAppVersion(r1)
            r1 = 1
        L_0x00ce:
            long r3 = r9.zzagd
            long r5 = r0.zzha()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x00de
            long r3 = r9.zzagd
            r0.zzu(r3)
            r1 = 1
        L_0x00de:
            java.lang.String r3 = r9.zzage
            if (r3 == 0) goto L_0x00f4
            java.lang.String r3 = r9.zzage
            java.lang.String r4 = r0.zzhb()
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x00f4
            java.lang.String r1 = r9.zzage
            r0.zzar(r1)
            r1 = 1
        L_0x00f4:
            long r3 = r9.zzagf
            long r5 = r0.zzhd()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0104
            long r3 = r9.zzagf
            r0.zzw(r3)
            r1 = 1
        L_0x0104:
            boolean r3 = r9.zzagg
            boolean r4 = r0.isMeasurementEnabled()
            if (r3 == r4) goto L_0x0112
            boolean r1 = r9.zzagg
            r0.setMeasurementEnabled(r1)
            r1 = 1
        L_0x0112:
            java.lang.String r3 = r9.zzagv
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x012c
            java.lang.String r3 = r9.zzagv
            java.lang.String r4 = r0.zzho()
            boolean r3 = r3.equals(r4)
            if (r3 != 0) goto L_0x012c
            java.lang.String r1 = r9.zzagv
            r0.zzas(r1)
            r1 = 1
        L_0x012c:
            long r3 = r9.zzagh
            long r5 = r0.zzhq()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x013c
            long r3 = r9.zzagh
            r0.zzag(r3)
            r1 = 1
        L_0x013c:
            boolean r3 = r9.zzagi
            boolean r4 = r0.zzhr()
            if (r3 == r4) goto L_0x014a
            boolean r1 = r9.zzagi
            r0.zze(r1)
            r1 = 1
        L_0x014a:
            boolean r3 = r9.zzagj
            boolean r4 = r0.zzhs()
            if (r3 == r4) goto L_0x0158
            boolean r9 = r9.zzagj
            r0.zzf(r9)
            r1 = 1
        L_0x0158:
            if (r1 == 0) goto L_0x0161
            com.google.android.gms.measurement.internal.zzq r9 = r8.zzjq()
            r9.zza(r0)
        L_0x0161:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zzg(com.google.android.gms.measurement.internal.zzh):com.google.android.gms.measurement.internal.zzg");
    }

    /* JADX WARNING: type inference failed for: r19v1 */
    /* JADX WARNING: type inference failed for: r19v2 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final byte[] zza(@android.support.annotation.NonNull com.google.android.gms.measurement.internal.zzad r34, @android.support.annotation.Size(min = 1) java.lang.String r35) {
        /*
            r33 = this;
            r1 = r33
            r2 = r34
            r15 = r35
            r33.zzlr()
            r33.zzaf()
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj
            r3.zzga()
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r34)
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r35)
            com.google.android.gms.internal.measurement.zzgh r14 = new com.google.android.gms.internal.measurement.zzgh
            r14.<init>()
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()
            r3.beginTransaction()
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzg r12 = r3.zzbl(r15)     // Catch:{ all -> 0x0514 }
            r13 = 0
            if (r12 != 0) goto L_0x0047
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjk()     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = "Log and bundle not available. package_name"
            r2.zzg(r3, r15)     // Catch:{ all -> 0x0514 }
            byte[] r2 = new byte[r13]     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()
            r3.endTransaction()
            return r2
        L_0x0047:
            boolean r3 = r12.isMeasurementEnabled()     // Catch:{ all -> 0x0514 }
            if (r3 != 0) goto L_0x0066
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r2 = r2.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjk()     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = "Log and bundle disabled. package_name"
            r2.zzg(r3, r15)     // Catch:{ all -> 0x0514 }
            byte[] r2 = new byte[r13]     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()
            r3.endTransaction()
            return r2
        L_0x0066:
            java.lang.String r3 = "_iap"
            java.lang.String r4 = r2.name     // Catch:{ all -> 0x0514 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0514 }
            if (r3 != 0) goto L_0x007a
            java.lang.String r3 = "ecommerce_purchase"
            java.lang.String r4 = r2.name     // Catch:{ all -> 0x0514 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x0093
        L_0x007a:
            boolean r3 = r1.zza(r15, r2)     // Catch:{ all -> 0x0514 }
            if (r3 != 0) goto L_0x0093
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjg()     // Catch:{ all -> 0x0514 }
            java.lang.String r4 = "Failed to handle purchase event at single event bundle creation. appId"
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzap.zzbv(r35)     // Catch:{ all -> 0x0514 }
            r3.zzg(r4, r5)     // Catch:{ all -> 0x0514 }
        L_0x0093:
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzn r3 = r3.zzgq()     // Catch:{ all -> 0x0514 }
            boolean r3 = r3.zzax(r15)     // Catch:{ all -> 0x0514 }
            r10 = 0
            java.lang.Long r4 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x00f6
            java.lang.String r5 = "_e"
            java.lang.String r6 = r2.name     // Catch:{ all -> 0x0514 }
            boolean r5 = r5.equals(r6)     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x00f6
            com.google.android.gms.measurement.internal.zzaa r5 = r2.zzaid     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x00e3
            com.google.android.gms.measurement.internal.zzaa r5 = r2.zzaid     // Catch:{ all -> 0x0514 }
            int r5 = r5.size()     // Catch:{ all -> 0x0514 }
            if (r5 != 0) goto L_0x00bc
            goto L_0x00e3
        L_0x00bc:
            com.google.android.gms.measurement.internal.zzaa r5 = r2.zzaid     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "_et"
            java.lang.Long r5 = r5.getLong(r6)     // Catch:{ all -> 0x0514 }
            if (r5 != 0) goto L_0x00da
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "The engagement event does not include duration. appId"
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r35)     // Catch:{ all -> 0x0514 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0514 }
            goto L_0x00f6
        L_0x00da:
            com.google.android.gms.measurement.internal.zzaa r4 = r2.zzaid     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "_et"
            java.lang.Long r4 = r4.getLong(r5)     // Catch:{ all -> 0x0514 }
            goto L_0x00f6
        L_0x00e3:
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjg()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "The engagement event does not contain any parameters. appId"
            java.lang.Object r7 = com.google.android.gms.measurement.internal.zzap.zzbv(r35)     // Catch:{ all -> 0x0514 }
            r5.zzg(r6, r7)     // Catch:{ all -> 0x0514 }
        L_0x00f6:
            com.google.android.gms.internal.measurement.zzgi r8 = new com.google.android.gms.internal.measurement.zzgi     // Catch:{ all -> 0x0514 }
            r8.<init>()     // Catch:{ all -> 0x0514 }
            r9 = 1
            com.google.android.gms.internal.measurement.zzgi[] r5 = new com.google.android.gms.internal.measurement.zzgi[r9]     // Catch:{ all -> 0x0514 }
            r5[r13] = r8     // Catch:{ all -> 0x0514 }
            r14.zzawy = r5     // Catch:{ all -> 0x0514 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x0514 }
            r8.zzaxa = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "android"
            r8.zzaxi = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.zzal()     // Catch:{ all -> 0x0514 }
            r8.zztt = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.zzhb()     // Catch:{ all -> 0x0514 }
            r8.zzage = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.zzak()     // Catch:{ all -> 0x0514 }
            r8.zzts = r5     // Catch:{ all -> 0x0514 }
            long r5 = r12.zzha()     // Catch:{ all -> 0x0514 }
            r16 = -2147483648(0xffffffff80000000, double:NaN)
            int r7 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            r23 = r14
            r14 = 0
            if (r7 != 0) goto L_0x012e
            r5 = r14
            goto L_0x0133
        L_0x012e:
            int r5 = (int) r5     // Catch:{ all -> 0x0514 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0514 }
        L_0x0133:
            r8.zzaxu = r5     // Catch:{ all -> 0x0514 }
            long r5 = r12.zzhc()     // Catch:{ all -> 0x0514 }
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0514 }
            r8.zzaxm = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.getGmpAppId()     // Catch:{ all -> 0x0514 }
            r8.zzafx = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r8.zzafx     // Catch:{ all -> 0x0514 }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x0153
            java.lang.String r5 = r12.zzgw()     // Catch:{ all -> 0x0514 }
            r8.zzawj = r5     // Catch:{ all -> 0x0514 }
        L_0x0153:
            long r5 = r12.zzhd()     // Catch:{ all -> 0x0514 }
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ all -> 0x0514 }
            r8.zzaxq = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            boolean r5 = r5.isEnabled()     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x017b
            boolean r5 = com.google.android.gms.measurement.internal.zzn.zzhz()     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x017b
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzn r5 = r5.zzgq()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = r8.zztt     // Catch:{ all -> 0x0514 }
            boolean r5 = r5.zzav(r6)     // Catch:{ all -> 0x0514 }
            if (r5 == 0) goto L_0x017b
            r8.zzaya = r14     // Catch:{ all -> 0x0514 }
        L_0x017b:
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzba r5 = r5.zzgp()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = r12.zzal()     // Catch:{ all -> 0x0514 }
            android.util.Pair r5 = r5.zzby(r6)     // Catch:{ all -> 0x0514 }
            boolean r6 = r12.zzhr()     // Catch:{ all -> 0x0514 }
            if (r6 == 0) goto L_0x01a7
            if (r5 == 0) goto L_0x01a7
            java.lang.Object r6 = r5.first     // Catch:{ all -> 0x0514 }
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6     // Catch:{ all -> 0x0514 }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x0514 }
            if (r6 != 0) goto L_0x01a7
            java.lang.Object r6 = r5.first     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0514 }
            r8.zzaxo = r6     // Catch:{ all -> 0x0514 }
            java.lang.Object r5 = r5.second     // Catch:{ all -> 0x0514 }
            java.lang.Boolean r5 = (java.lang.Boolean) r5     // Catch:{ all -> 0x0514 }
            r8.zzaxp = r5     // Catch:{ all -> 0x0514 }
        L_0x01a7:
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzx r5 = r5.zzgk()     // Catch:{ all -> 0x0514 }
            r5.zzcl()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = android.os.Build.MODEL     // Catch:{ all -> 0x0514 }
            r8.zzaxk = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzx r5 = r5.zzgk()     // Catch:{ all -> 0x0514 }
            r5.zzcl()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = android.os.Build.VERSION.RELEASE     // Catch:{ all -> 0x0514 }
            r8.zzaxj = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzx r5 = r5.zzgk()     // Catch:{ all -> 0x0514 }
            long r5 = r5.zzis()     // Catch:{ all -> 0x0514 }
            int r5 = (int) r5     // Catch:{ all -> 0x0514 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0514 }
            r8.zzaxl = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzx r5 = r5.zzgk()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r5.zzit()     // Catch:{ all -> 0x0514 }
            r8.zzaia = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.getAppInstanceId()     // Catch:{ all -> 0x0514 }
            r8.zzafw = r5     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r12.getFirebaseInstanceId()     // Catch:{ all -> 0x0514 }
            r8.zzafz = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r5 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = r12.zzal()     // Catch:{ all -> 0x0514 }
            java.util.List r5 = r5.zzbk(r6)     // Catch:{ all -> 0x0514 }
            int r6 = r5.size()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r6 = new com.google.android.gms.internal.measurement.zzgl[r6]     // Catch:{ all -> 0x0514 }
            r8.zzaxc = r6     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x0265
            com.google.android.gms.measurement.internal.zzq r6 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            java.lang.String r7 = r8.zztt     // Catch:{ all -> 0x0514 }
            java.lang.String r13 = "_lte"
            com.google.android.gms.measurement.internal.zzfj r6 = r6.zzi(r7, r13)     // Catch:{ all -> 0x0514 }
            if (r6 == 0) goto L_0x0249
            java.lang.Object r7 = r6.value     // Catch:{ all -> 0x0514 }
            if (r7 != 0) goto L_0x0213
            goto L_0x0249
        L_0x0213:
            long r16 = r4.longValue()     // Catch:{ all -> 0x0514 }
            int r7 = (r16 > r10 ? 1 : (r16 == r10 ? 0 : -1))
            if (r7 <= 0) goto L_0x0266
            com.google.android.gms.measurement.internal.zzfj r7 = new com.google.android.gms.measurement.internal.zzfj     // Catch:{ all -> 0x0514 }
            java.lang.String r13 = r8.zztt     // Catch:{ all -> 0x0514 }
            java.lang.String r18 = "auto"
            java.lang.String r19 = "_lte"
            com.google.android.gms.measurement.internal.zzbt r14 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.common.util.Clock r14 = r14.zzbx()     // Catch:{ all -> 0x0514 }
            long r20 = r14.currentTimeMillis()     // Catch:{ all -> 0x0514 }
            java.lang.Object r6 = r6.value     // Catch:{ all -> 0x0514 }
            java.lang.Long r6 = (java.lang.Long) r6     // Catch:{ all -> 0x0514 }
            long r16 = r6.longValue()     // Catch:{ all -> 0x0514 }
            long r24 = r4.longValue()     // Catch:{ all -> 0x0514 }
            r6 = 0
            long r10 = r16 + r24
            java.lang.Long r22 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0514 }
            r16 = r7
            r17 = r13
            r16.<init>(r17, r18, r19, r20, r22)     // Catch:{ all -> 0x0514 }
            r6 = r7
            goto L_0x0266
        L_0x0249:
            com.google.android.gms.measurement.internal.zzfj r6 = new com.google.android.gms.measurement.internal.zzfj     // Catch:{ all -> 0x0514 }
            java.lang.String r7 = r8.zztt     // Catch:{ all -> 0x0514 }
            java.lang.String r18 = "auto"
            java.lang.String r19 = "_lte"
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.common.util.Clock r10 = r10.zzbx()     // Catch:{ all -> 0x0514 }
            long r20 = r10.currentTimeMillis()     // Catch:{ all -> 0x0514 }
            r16 = r6
            r17 = r7
            r22 = r4
            r16.<init>(r17, r18, r19, r20, r22)     // Catch:{ all -> 0x0514 }
            goto L_0x0266
        L_0x0265:
            r6 = 0
        L_0x0266:
            r7 = 0
            r10 = 0
        L_0x0268:
            int r11 = r5.size()     // Catch:{ all -> 0x0514 }
            if (r7 >= r11) goto L_0x02c4
            com.google.android.gms.internal.measurement.zzgl r11 = new com.google.android.gms.internal.measurement.zzgl     // Catch:{ all -> 0x0514 }
            r11.<init>()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r13 = r8.zzaxc     // Catch:{ all -> 0x0514 }
            r13[r7] = r11     // Catch:{ all -> 0x0514 }
            java.lang.Object r13 = r5.get(r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfj r13 = (com.google.android.gms.measurement.internal.zzfj) r13     // Catch:{ all -> 0x0514 }
            java.lang.String r13 = r13.name     // Catch:{ all -> 0x0514 }
            r11.name = r13     // Catch:{ all -> 0x0514 }
            java.lang.Object r13 = r5.get(r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfj r13 = (com.google.android.gms.measurement.internal.zzfj) r13     // Catch:{ all -> 0x0514 }
            long r13 = r13.zzaue     // Catch:{ all -> 0x0514 }
            java.lang.Long r13 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0514 }
            r11.zzayl = r13     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfg r13 = r33.zzjo()     // Catch:{ all -> 0x0514 }
            java.lang.Object r14 = r5.get(r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfj r14 = (com.google.android.gms.measurement.internal.zzfj) r14     // Catch:{ all -> 0x0514 }
            java.lang.Object r14 = r14.value     // Catch:{ all -> 0x0514 }
            r13.zza(r11, r14)     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x02c1
            java.lang.String r13 = "_lte"
            java.lang.String r14 = r11.name     // Catch:{ all -> 0x0514 }
            boolean r13 = r13.equals(r14)     // Catch:{ all -> 0x0514 }
            if (r13 == 0) goto L_0x02c1
            java.lang.Object r10 = r6.value     // Catch:{ all -> 0x0514 }
            java.lang.Long r10 = (java.lang.Long) r10     // Catch:{ all -> 0x0514 }
            r11.zzawx = r10     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.common.util.Clock r10 = r10.zzbx()     // Catch:{ all -> 0x0514 }
            long r13 = r10.currentTimeMillis()     // Catch:{ all -> 0x0514 }
            java.lang.Long r10 = java.lang.Long.valueOf(r13)     // Catch:{ all -> 0x0514 }
            r11.zzayl = r10     // Catch:{ all -> 0x0514 }
            r10 = r11
        L_0x02c1:
            int r7 = r7 + 1
            goto L_0x0268
        L_0x02c4:
            if (r3 == 0) goto L_0x02fd
            if (r10 != 0) goto L_0x02fd
            com.google.android.gms.internal.measurement.zzgl r3 = new com.google.android.gms.internal.measurement.zzgl     // Catch:{ all -> 0x0514 }
            r3.<init>()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "_lte"
            r3.name = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r5 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.common.util.Clock r5 = r5.zzbx()     // Catch:{ all -> 0x0514 }
            long r10 = r5.currentTimeMillis()     // Catch:{ all -> 0x0514 }
            java.lang.Long r5 = java.lang.Long.valueOf(r10)     // Catch:{ all -> 0x0514 }
            r3.zzayl = r5     // Catch:{ all -> 0x0514 }
            java.lang.Object r5 = r6.value     // Catch:{ all -> 0x0514 }
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ all -> 0x0514 }
            r3.zzawx = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r5 = r8.zzaxc     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r8.zzaxc     // Catch:{ all -> 0x0514 }
            int r7 = r7.length     // Catch:{ all -> 0x0514 }
            int r7 = r7 + r9
            java.lang.Object[] r5 = java.util.Arrays.copyOf(r5, r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r5 = (com.google.android.gms.internal.measurement.zzgl[]) r5     // Catch:{ all -> 0x0514 }
            r8.zzaxc = r5     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r5 = r8.zzaxc     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r8.zzaxc     // Catch:{ all -> 0x0514 }
            int r7 = r7.length     // Catch:{ all -> 0x0514 }
            int r7 = r7 - r9
            r5[r7] = r3     // Catch:{ all -> 0x0514 }
        L_0x02fd:
            long r3 = r4.longValue()     // Catch:{ all -> 0x0514 }
            r10 = 0
            int r5 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r5 <= 0) goto L_0x030e
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            r3.zza(r6)     // Catch:{ all -> 0x0514 }
        L_0x030e:
            com.google.android.gms.measurement.internal.zzaa r3 = r2.zzaid     // Catch:{ all -> 0x0514 }
            android.os.Bundle r14 = r3.zziv()     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = "_iap"
            java.lang.String r4 = r2.name     // Catch:{ all -> 0x0514 }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x0514 }
            r4 = 1
            if (r3 == 0) goto L_0x0339
            java.lang.String r3 = "_c"
            r14.putLong(r3, r4)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjk()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "Marking in-app purchase as real-time"
            r3.zzbx(r6)     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = "_r"
            r14.putLong(r3, r4)     // Catch:{ all -> 0x0514 }
        L_0x0339:
            java.lang.String r3 = "_o"
            java.lang.String r6 = r2.origin     // Catch:{ all -> 0x0514 }
            r14.putString(r3, r6)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfk r3 = r3.zzgm()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = r8.zztt     // Catch:{ all -> 0x0514 }
            boolean r3 = r3.zzcw(r6)     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x036c
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfk r3 = r3.zzgm()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "_dbg"
            java.lang.Long r7 = java.lang.Long.valueOf(r4)     // Catch:{ all -> 0x0514 }
            r3.zza(r14, r6, r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfk r3 = r3.zzgm()     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = "_r"
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ all -> 0x0514 }
            r3.zza(r14, r6, r4)     // Catch:{ all -> 0x0514 }
        L_0x036c:
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            java.lang.String r4 = r2.name     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzz r3 = r3.zzg(r15, r4)     // Catch:{ all -> 0x0514 }
            if (r3 != 0) goto L_0x03bb
            com.google.android.gms.measurement.internal.zzz r13 = new com.google.android.gms.measurement.internal.zzz     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = r2.name     // Catch:{ all -> 0x0514 }
            r6 = 1
            r16 = 0
            long r3 = r2.zzaip     // Catch:{ all -> 0x0514 }
            r18 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r24 = 0
            r25 = r3
            r3 = r13
            r4 = r15
            r27 = r8
            r8 = r16
            r28 = r10
            r10 = r25
            r30 = r12
            r31 = r13
            r12 = r18
            r18 = r14
            r32 = r23
            r19 = 0
            r14 = r20
            r15 = r21
            r16 = r22
            r17 = r24
            r3.<init>(r4, r5, r6, r8, r10, r12, r14, r15, r16, r17)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            r4 = r31
            r3.zza(r4)     // Catch:{ all -> 0x0514 }
            r9 = r28
            goto L_0x03db
        L_0x03bb:
            r27 = r8
            r28 = r10
            r30 = r12
            r18 = r14
            r32 = r23
            r19 = 0
            long r4 = r3.zzaig     // Catch:{ all -> 0x0514 }
            long r6 = r2.zzaip     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzz r3 = r3.zzai(r6)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzz r3 = r3.zziu()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r6 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            r6.zza(r3)     // Catch:{ all -> 0x0514 }
            r9 = r4
        L_0x03db:
            com.google.android.gms.measurement.internal.zzy r12 = new com.google.android.gms.measurement.internal.zzy     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj     // Catch:{ all -> 0x0514 }
            java.lang.String r4 = r2.origin     // Catch:{ all -> 0x0514 }
            java.lang.String r6 = r2.name     // Catch:{ all -> 0x0514 }
            long r7 = r2.zzaip     // Catch:{ all -> 0x0514 }
            r2 = r12
            r5 = r35
            r11 = r18
            r2.<init>(r3, r4, r5, r6, r7, r9, r11)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgf r2 = new com.google.android.gms.internal.measurement.zzgf     // Catch:{ all -> 0x0514 }
            r2.<init>()     // Catch:{ all -> 0x0514 }
            r3 = 1
            com.google.android.gms.internal.measurement.zzgf[] r3 = new com.google.android.gms.internal.measurement.zzgf[r3]     // Catch:{ all -> 0x0514 }
            r4 = 0
            r3[r4] = r2     // Catch:{ all -> 0x0514 }
            r5 = r27
            r5.zzaxb = r3     // Catch:{ all -> 0x0514 }
            long r6 = r12.timestamp     // Catch:{ all -> 0x0514 }
            java.lang.Long r3 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            r2.zzawu = r3     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = r12.name     // Catch:{ all -> 0x0514 }
            r2.name = r3     // Catch:{ all -> 0x0514 }
            long r6 = r12.zzaic     // Catch:{ all -> 0x0514 }
            java.lang.Long r3 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            r2.zzawv = r3     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzaa r3 = r12.zzaid     // Catch:{ all -> 0x0514 }
            int r3 = r3.size()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgg[] r3 = new com.google.android.gms.internal.measurement.zzgg[r3]     // Catch:{ all -> 0x0514 }
            r2.zzawt = r3     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzaa r3 = r12.zzaid     // Catch:{ all -> 0x0514 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0514 }
            r6 = 0
        L_0x0421:
            boolean r7 = r3.hasNext()     // Catch:{ all -> 0x0514 }
            if (r7 == 0) goto L_0x0449
            java.lang.Object r7 = r3.next()     // Catch:{ all -> 0x0514 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgg r8 = new com.google.android.gms.internal.measurement.zzgg     // Catch:{ all -> 0x0514 }
            r8.<init>()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgg[] r9 = r2.zzawt     // Catch:{ all -> 0x0514 }
            int r10 = r6 + 1
            r9[r6] = r8     // Catch:{ all -> 0x0514 }
            r8.name = r7     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzaa r6 = r12.zzaid     // Catch:{ all -> 0x0514 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzfg r7 = r33.zzjo()     // Catch:{ all -> 0x0514 }
            r7.zza(r8, r6)     // Catch:{ all -> 0x0514 }
            r6 = r10
            goto L_0x0421
        L_0x0449:
            r3 = r30
            java.lang.String r6 = r3.zzal()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgl[] r7 = r5.zzaxc     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgf[] r8 = r5.zzaxb     // Catch:{ all -> 0x0514 }
            com.google.android.gms.internal.measurement.zzgd[] r6 = r1.zza(r6, r7, r8)     // Catch:{ all -> 0x0514 }
            r5.zzaxt = r6     // Catch:{ all -> 0x0514 }
            java.lang.Long r6 = r2.zzawu     // Catch:{ all -> 0x0514 }
            r5.zzaxe = r6     // Catch:{ all -> 0x0514 }
            java.lang.Long r2 = r2.zzawu     // Catch:{ all -> 0x0514 }
            r5.zzaxf = r2     // Catch:{ all -> 0x0514 }
            long r6 = r3.zzgz()     // Catch:{ all -> 0x0514 }
            int r2 = (r6 > r28 ? 1 : (r6 == r28 ? 0 : -1))
            if (r2 == 0) goto L_0x046e
            java.lang.Long r14 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            goto L_0x0470
        L_0x046e:
            r14 = r19
        L_0x0470:
            r5.zzaxh = r14     // Catch:{ all -> 0x0514 }
            long r8 = r3.zzgy()     // Catch:{ all -> 0x0514 }
            int r2 = (r8 > r28 ? 1 : (r8 == r28 ? 0 : -1))
            if (r2 != 0) goto L_0x047b
            goto L_0x047c
        L_0x047b:
            r6 = r8
        L_0x047c:
            int r2 = (r6 > r28 ? 1 : (r6 == r28 ? 0 : -1))
            if (r2 == 0) goto L_0x0485
            java.lang.Long r14 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            goto L_0x0487
        L_0x0485:
            r14 = r19
        L_0x0487:
            r5.zzaxg = r14     // Catch:{ all -> 0x0514 }
            r3.zzhh()     // Catch:{ all -> 0x0514 }
            long r6 = r3.zzhe()     // Catch:{ all -> 0x0514 }
            int r2 = (int) r6     // Catch:{ all -> 0x0514 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0514 }
            r5.zzaxr = r2     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzn r2 = r2.zzgq()     // Catch:{ all -> 0x0514 }
            long r6 = r2.zzhc()     // Catch:{ all -> 0x0514 }
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            r5.zzaxn = r2     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj     // Catch:{ all -> 0x0514 }
            com.google.android.gms.common.util.Clock r2 = r2.zzbx()     // Catch:{ all -> 0x0514 }
            long r6 = r2.currentTimeMillis()     // Catch:{ all -> 0x0514 }
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch:{ all -> 0x0514 }
            r5.zzaxd = r2     // Catch:{ all -> 0x0514 }
            java.lang.Boolean r2 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x0514 }
            r5.zzaxs = r2     // Catch:{ all -> 0x0514 }
            java.lang.Long r2 = r5.zzaxe     // Catch:{ all -> 0x0514 }
            long r6 = r2.longValue()     // Catch:{ all -> 0x0514 }
            r3.zzs(r6)     // Catch:{ all -> 0x0514 }
            java.lang.Long r2 = r5.zzaxf     // Catch:{ all -> 0x0514 }
            long r5 = r2.longValue()     // Catch:{ all -> 0x0514 }
            r3.zzt(r5)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r2 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            r2.zza(r3)     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r2 = r33.zzjq()     // Catch:{ all -> 0x0514 }
            r2.setTransactionSuccessful()     // Catch:{ all -> 0x0514 }
            com.google.android.gms.measurement.internal.zzq r2 = r33.zzjq()
            r2.endTransaction()
            r2 = r32
            int r3 = r2.zzvu()     // Catch:{ IOException -> 0x04fe }
            byte[] r3 = new byte[r3]     // Catch:{ IOException -> 0x04fe }
            int r5 = r3.length     // Catch:{ IOException -> 0x04fe }
            com.google.android.gms.internal.measurement.zzyy r4 = com.google.android.gms.internal.measurement.zzyy.zzk(r3, r4, r5)     // Catch:{ IOException -> 0x04fe }
            r2.zza(r4)     // Catch:{ IOException -> 0x04fe }
            r4.zzyt()     // Catch:{ IOException -> 0x04fe }
            com.google.android.gms.measurement.internal.zzfg r2 = r33.zzjo()     // Catch:{ IOException -> 0x04fe }
            byte[] r2 = r2.zzb(r3)     // Catch:{ IOException -> 0x04fe }
            return r2
        L_0x04fe:
            r0 = move-exception
            r2 = r0
            com.google.android.gms.measurement.internal.zzbt r3 = r1.zzadj
            com.google.android.gms.measurement.internal.zzap r3 = r3.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()
            java.lang.String r4 = "Data loss. Failed to bundle and serialize. appId"
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzap.zzbv(r35)
            r3.zze(r4, r5, r2)
            return r19
        L_0x0514:
            r0 = move-exception
            r2 = r0
            com.google.android.gms.measurement.internal.zzq r3 = r33.zzjq()
            r3.endTransaction()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfa.zza(com.google.android.gms.measurement.internal.zzad, java.lang.String):byte[]");
    }

    /* access modifiers changed from: 0000 */
    public final String zzh(zzh zzh) {
        try {
            return (String) this.zzadj.zzgn().zzb((Callable<V>) new zzfe<V>(this, zzh)).get(Constants.EVENT_UPLOAD_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            this.zzadj.zzgo().zzjd().zze("Failed to get app instance id. appId", zzap.zzbv(zzh.packageName), e);
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zzo(boolean z) {
        zzlv();
    }
}
