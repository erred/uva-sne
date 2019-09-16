package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.time.DateUtils;

@VisibleForTesting
public final class zzdr extends zzf {
    /* access modifiers changed from: private */
    public final zzef zzarz;
    /* access modifiers changed from: private */
    public zzag zzasa;
    private volatile Boolean zzasb;
    private final zzv zzasc;
    private final zzev zzasd;
    private final List<Runnable> zzase = new ArrayList();
    private final zzv zzasf;

    protected zzdr(zzbt zzbt) {
        super(zzbt);
        this.zzasd = new zzev(zzbt.zzbx());
        this.zzarz = new zzef(this);
        this.zzasc = new zzds(this, zzbt);
        this.zzasf = new zzdx(this, zzbt);
    }

    /* access modifiers changed from: protected */
    public final boolean zzgt() {
        return false;
    }

    @WorkerThread
    public final boolean isConnected() {
        zzaf();
        zzcl();
        return this.zzasa != null;
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzlc() {
        zzaf();
        zzcl();
        zzf(new zzdy(this, zzm(true)));
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0043  */
    @android.support.annotation.WorkerThread
    @com.google.android.gms.common.util.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zza(com.google.android.gms.measurement.internal.zzag r12, com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable r13, com.google.android.gms.measurement.internal.zzh r14) {
        /*
            r11 = this;
            r11.zzaf()
            r11.zzgb()
            r11.zzcl()
            boolean r0 = r11.zzld()
            r1 = 0
            r2 = 100
            r3 = 0
            r4 = 100
        L_0x0013:
            r5 = 1001(0x3e9, float:1.403E-42)
            if (r3 >= r5) goto L_0x00a9
            if (r4 != r2) goto L_0x00a9
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            if (r0 == 0) goto L_0x0032
            com.google.android.gms.measurement.internal.zzal r5 = r11.zzgi()
            java.util.List r5 = r5.zzr(r2)
            if (r5 == 0) goto L_0x0032
            r4.addAll(r5)
            int r5 = r5.size()
            goto L_0x0033
        L_0x0032:
            r5 = 0
        L_0x0033:
            if (r13 == 0) goto L_0x003a
            if (r5 >= r2) goto L_0x003a
            r4.add(r13)
        L_0x003a:
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            int r6 = r4.size()
            r7 = 0
        L_0x0041:
            if (r7 >= r6) goto L_0x00a4
            java.lang.Object r8 = r4.get(r7)
            int r7 = r7 + 1
            com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable r8 = (com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable) r8
            boolean r9 = r8 instanceof com.google.android.gms.measurement.internal.zzad
            if (r9 == 0) goto L_0x0064
            com.google.android.gms.measurement.internal.zzad r8 = (com.google.android.gms.measurement.internal.zzad) r8     // Catch:{ RemoteException -> 0x0055 }
            r12.zza(r8, r14)     // Catch:{ RemoteException -> 0x0055 }
            goto L_0x0041
        L_0x0055:
            r8 = move-exception
            com.google.android.gms.measurement.internal.zzap r9 = r11.zzgo()
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjd()
            java.lang.String r10 = "Failed to send event to the service"
            r9.zzg(r10, r8)
            goto L_0x0041
        L_0x0064:
            boolean r9 = r8 instanceof com.google.android.gms.measurement.internal.zzfh
            if (r9 == 0) goto L_0x007d
            com.google.android.gms.measurement.internal.zzfh r8 = (com.google.android.gms.measurement.internal.zzfh) r8     // Catch:{ RemoteException -> 0x006e }
            r12.zza(r8, r14)     // Catch:{ RemoteException -> 0x006e }
            goto L_0x0041
        L_0x006e:
            r8 = move-exception
            com.google.android.gms.measurement.internal.zzap r9 = r11.zzgo()
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjd()
            java.lang.String r10 = "Failed to send attribute to the service"
            r9.zzg(r10, r8)
            goto L_0x0041
        L_0x007d:
            boolean r9 = r8 instanceof com.google.android.gms.measurement.internal.zzl
            if (r9 == 0) goto L_0x0096
            com.google.android.gms.measurement.internal.zzl r8 = (com.google.android.gms.measurement.internal.zzl) r8     // Catch:{ RemoteException -> 0x0087 }
            r12.zza(r8, r14)     // Catch:{ RemoteException -> 0x0087 }
            goto L_0x0041
        L_0x0087:
            r8 = move-exception
            com.google.android.gms.measurement.internal.zzap r9 = r11.zzgo()
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjd()
            java.lang.String r10 = "Failed to send conditional property to the service"
            r9.zzg(r10, r8)
            goto L_0x0041
        L_0x0096:
            com.google.android.gms.measurement.internal.zzap r8 = r11.zzgo()
            com.google.android.gms.measurement.internal.zzar r8 = r8.zzjd()
            java.lang.String r9 = "Discarding data. Unrecognized parcel type."
            r8.zzbx(r9)
            goto L_0x0041
        L_0x00a4:
            int r3 = r3 + 1
            r4 = r5
            goto L_0x0013
        L_0x00a9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzdr.zza(com.google.android.gms.measurement.internal.zzag, com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable, com.google.android.gms.measurement.internal.zzh):void");
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzb(zzad zzad, String str) {
        Preconditions.checkNotNull(zzad);
        zzaf();
        zzcl();
        boolean zzld = zzld();
        zzdz zzdz = new zzdz(this, zzld, zzld && zzgi().zza(zzad), zzad, zzm(true), str);
        zzf(zzdz);
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzd(zzl zzl) {
        Preconditions.checkNotNull(zzl);
        zzaf();
        zzcl();
        zzgr();
        zzea zzea = new zzea(this, true, zzgi().zzc(zzl), new zzl(zzl), zzm(true), zzl);
        zzf(zzea);
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zza(AtomicReference<List<zzl>> atomicReference, String str, String str2, String str3) {
        zzaf();
        zzcl();
        zzeb zzeb = new zzeb(this, atomicReference, str, str2, str3, zzm(false));
        zzf(zzeb);
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zza(AtomicReference<List<zzfh>> atomicReference, String str, String str2, String str3, boolean z) {
        zzaf();
        zzcl();
        zzec zzec = new zzec(this, atomicReference, str, str2, str3, z, zzm(false));
        zzf(zzec);
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzb(zzfh zzfh) {
        zzaf();
        zzcl();
        zzf(new zzed(this, zzld() && zzgi().zza(zzfh), zzfh, zzm(true)));
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zza(AtomicReference<List<zzfh>> atomicReference, boolean z) {
        zzaf();
        zzcl();
        zzf(new zzee(this, atomicReference, zzm(false), z));
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void resetAnalyticsData() {
        zzaf();
        zzgb();
        zzcl();
        zzh zzm = zzm(false);
        if (zzld()) {
            zzgi().resetAnalyticsData();
        }
        zzf(new zzdt(this, zzm));
    }

    private final boolean zzld() {
        zzgr();
        return true;
    }

    @WorkerThread
    public final void zza(AtomicReference<String> atomicReference) {
        zzaf();
        zzcl();
        zzf(new zzdu(this, atomicReference, zzm(false)));
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzkz() {
        zzaf();
        zzcl();
        zzf(new zzdv(this, zzm(true)));
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    public final void zzb(zzdn zzdn) {
        zzaf();
        zzcl();
        zzf(new zzdw(this, zzdn));
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzcy() {
        zzaf();
        this.zzasd.start();
        this.zzasc.zzh(((Long) zzaf.zzakj.get()).longValue());
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00c4, code lost:
        r0 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x010f  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zzdj() {
        /*
            r6 = this;
            r6.zzaf()
            r6.zzcl()
            boolean r0 = r6.isConnected()
            if (r0 == 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.Boolean r0 = r6.zzasb
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x011c
            r6.zzaf()
            r6.zzcl()
            com.google.android.gms.measurement.internal.zzba r0 = r6.zzgp()
            java.lang.Boolean r0 = r0.zzju()
            if (r0 == 0) goto L_0x002c
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x002c
            r0 = 1
            goto L_0x0116
        L_0x002c:
            r6.zzgr()
            com.google.android.gms.measurement.internal.zzaj r0 = r6.zzgf()
            int r0 = r0.zzjb()
            if (r0 != r2) goto L_0x003d
        L_0x0039:
            r0 = 1
        L_0x003a:
            r3 = 1
            goto L_0x00f3
        L_0x003d:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjl()
            java.lang.String r3 = "Checking service availability"
            r0.zzbx(r3)
            com.google.android.gms.measurement.internal.zzfk r0 = r6.zzgm()
            com.google.android.gms.common.GoogleApiAvailabilityLight r3 = com.google.android.gms.common.GoogleApiAvailabilityLight.getInstance()
            android.content.Context r0 = r0.getContext()
            r4 = 12451000(0xbdfcb8, float:1.7447567E-38)
            int r0 = r3.isGooglePlayServicesAvailable(r0, r4)
            r3 = 9
            if (r0 == r3) goto L_0x00e5
            r3 = 18
            if (r0 == r3) goto L_0x00d6
            switch(r0) {
                case 0: goto L_0x00c7;
                case 1: goto L_0x00b7;
                case 2: goto L_0x008b;
                case 3: goto L_0x007d;
                default: goto L_0x0068;
            }
        L_0x0068:
            com.google.android.gms.measurement.internal.zzap r3 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjg()
            java.lang.String r4 = "Unexpected service status"
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r3.zzg(r4, r0)
        L_0x0079:
            r0 = 0
        L_0x007a:
            r3 = 0
            goto L_0x00f3
        L_0x007d:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjg()
            java.lang.String r3 = "Service disabled"
            r0.zzbx(r3)
            goto L_0x0079
        L_0x008b:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjk()
            java.lang.String r3 = "Service container out of date"
            r0.zzbx(r3)
            com.google.android.gms.measurement.internal.zzfk r0 = r6.zzgm()
            int r0 = r0.zzme()
            r3 = 13000(0x32c8, float:1.8217E-41)
            if (r0 >= r3) goto L_0x00a5
            goto L_0x00c4
        L_0x00a5:
            com.google.android.gms.measurement.internal.zzba r0 = r6.zzgp()
            java.lang.Boolean r0 = r0.zzju()
            if (r0 == 0) goto L_0x00b5
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x0079
        L_0x00b5:
            r0 = 1
            goto L_0x007a
        L_0x00b7:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjl()
            java.lang.String r3 = "Service missing"
            r0.zzbx(r3)
        L_0x00c4:
            r0 = 0
            goto L_0x003a
        L_0x00c7:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjl()
            java.lang.String r3 = "Service available"
            r0.zzbx(r3)
            goto L_0x0039
        L_0x00d6:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjg()
            java.lang.String r3 = "Service updating"
            r0.zzbx(r3)
            goto L_0x0039
        L_0x00e5:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjg()
            java.lang.String r3 = "Service invalid"
            r0.zzbx(r3)
            goto L_0x0079
        L_0x00f3:
            if (r0 != 0) goto L_0x010d
            com.google.android.gms.measurement.internal.zzn r4 = r6.zzgq()
            boolean r4 = r4.zzib()
            if (r4 == 0) goto L_0x010d
            com.google.android.gms.measurement.internal.zzap r3 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()
            java.lang.String r4 = "No way to upload. Consider using the full version of Analytics"
            r3.zzbx(r4)
            r3 = 0
        L_0x010d:
            if (r3 == 0) goto L_0x0116
            com.google.android.gms.measurement.internal.zzba r3 = r6.zzgp()
            r3.zzg(r0)
        L_0x0116:
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r6.zzasb = r0
        L_0x011c:
            java.lang.Boolean r0 = r6.zzasb
            boolean r0 = r0.booleanValue()
            if (r0 == 0) goto L_0x012a
            com.google.android.gms.measurement.internal.zzef r0 = r6.zzarz
            r0.zzlh()
            return
        L_0x012a:
            com.google.android.gms.measurement.internal.zzn r0 = r6.zzgq()
            boolean r0 = r0.zzib()
            if (r0 != 0) goto L_0x018a
            r6.zzgr()
            android.content.Context r0 = r6.getContext()
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            android.content.Context r4 = r6.getContext()
            java.lang.String r5 = "com.google.android.gms.measurement.AppMeasurementService"
            android.content.Intent r3 = r3.setClassName(r4, r5)
            r4 = 65536(0x10000, float:9.18355E-41)
            java.util.List r0 = r0.queryIntentServices(r3, r4)
            if (r0 == 0) goto L_0x015d
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x015d
            r1 = 1
        L_0x015d:
            if (r1 == 0) goto L_0x017d
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "com.google.android.gms.measurement.START"
            r0.<init>(r1)
            android.content.ComponentName r1 = new android.content.ComponentName
            android.content.Context r2 = r6.getContext()
            r6.zzgr()
            java.lang.String r3 = "com.google.android.gms.measurement.AppMeasurementService"
            r1.<init>(r2, r3)
            r0.setComponent(r1)
            com.google.android.gms.measurement.internal.zzef r1 = r6.zzarz
            r1.zzc(r0)
            return
        L_0x017d:
            com.google.android.gms.measurement.internal.zzap r0 = r6.zzgo()
            com.google.android.gms.measurement.internal.zzar r0 = r0.zzjd()
            java.lang.String r1 = "Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest"
            r0.zzbx(r1)
        L_0x018a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzdr.zzdj():void");
    }

    /* access modifiers changed from: 0000 */
    public final Boolean zzle() {
        return this.zzasb;
    }

    /* access modifiers changed from: protected */
    @WorkerThread
    @VisibleForTesting
    public final void zza(zzag zzag) {
        zzaf();
        Preconditions.checkNotNull(zzag);
        this.zzasa = zzag;
        zzcy();
        zzlf();
    }

    @WorkerThread
    public final void disconnect() {
        zzaf();
        zzcl();
        if (zzn.zzia()) {
            this.zzarz.zzlg();
        }
        try {
            ConnectionTracker.getInstance().unbindService(getContext(), this.zzarz);
        } catch (IllegalArgumentException | IllegalStateException unused) {
        }
        this.zzasa = null;
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void onServiceDisconnected(ComponentName componentName) {
        zzaf();
        if (this.zzasa != null) {
            this.zzasa = null;
            zzgo().zzjl().zzg("Disconnected from device MeasurementService", componentName);
            zzaf();
            zzdj();
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzcz() {
        zzaf();
        if (isConnected()) {
            zzgo().zzjl().zzbx("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    @WorkerThread
    private final void zzf(Runnable runnable) throws IllegalStateException {
        zzaf();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzase.size()) >= 1000) {
            zzgo().zzjd().zzbx("Discarding data. Max runnable queue size reached");
        } else {
            this.zzase.add(runnable);
            this.zzasf.zzh(DateUtils.MILLIS_PER_MINUTE);
            zzdj();
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzlf() {
        zzaf();
        zzgo().zzjl().zzg("Processing queued up service tasks", Integer.valueOf(this.zzase.size()));
        for (Runnable run : this.zzase) {
            try {
                run.run();
            } catch (Exception e) {
                zzgo().zzjd().zzg("Task exception while flushing queue", e);
            }
        }
        this.zzase.clear();
        this.zzasf.cancel();
    }

    @Nullable
    @WorkerThread
    private final zzh zzm(boolean z) {
        zzgr();
        return zzgf().zzbr(z ? zzgo().zzjn() : null);
    }

    public final /* bridge */ /* synthetic */ void zzga() {
        super.zzga();
    }

    public final /* bridge */ /* synthetic */ void zzgb() {
        super.zzgb();
    }

    public final /* bridge */ /* synthetic */ void zzgc() {
        super.zzgc();
    }

    public final /* bridge */ /* synthetic */ void zzaf() {
        super.zzaf();
    }

    public final /* bridge */ /* synthetic */ zza zzgd() {
        return super.zzgd();
    }

    public final /* bridge */ /* synthetic */ zzcs zzge() {
        return super.zzge();
    }

    public final /* bridge */ /* synthetic */ zzaj zzgf() {
        return super.zzgf();
    }

    public final /* bridge */ /* synthetic */ zzdr zzgg() {
        return super.zzgg();
    }

    public final /* bridge */ /* synthetic */ zzdo zzgh() {
        return super.zzgh();
    }

    public final /* bridge */ /* synthetic */ zzal zzgi() {
        return super.zzgi();
    }

    public final /* bridge */ /* synthetic */ zzeq zzgj() {
        return super.zzgj();
    }

    public final /* bridge */ /* synthetic */ zzx zzgk() {
        return super.zzgk();
    }

    public final /* bridge */ /* synthetic */ Clock zzbx() {
        return super.zzbx();
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final /* bridge */ /* synthetic */ zzan zzgl() {
        return super.zzgl();
    }

    public final /* bridge */ /* synthetic */ zzfk zzgm() {
        return super.zzgm();
    }

    public final /* bridge */ /* synthetic */ zzbo zzgn() {
        return super.zzgn();
    }

    public final /* bridge */ /* synthetic */ zzap zzgo() {
        return super.zzgo();
    }

    public final /* bridge */ /* synthetic */ zzba zzgp() {
        return super.zzgp();
    }

    public final /* bridge */ /* synthetic */ zzn zzgq() {
        return super.zzgq();
    }

    public final /* bridge */ /* synthetic */ zzk zzgr() {
        return super.zzgr();
    }
}
