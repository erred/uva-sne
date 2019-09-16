package com.google.android.gms.measurement.internal;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import com.facebook.react.views.scroll.ReactScrollViewHelper;
import com.google.android.gms.common.api.internal.GoogleServices;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.EventInterceptor;
import com.google.android.gms.measurement.AppMeasurement.OnEventListener;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;
import org.altbeacon.beacon.service.RangedBeacon;

public final class zzcs extends zzf {
    @VisibleForTesting
    protected zzdm zzaqv;
    private EventInterceptor zzaqw;
    private final Set<OnEventListener> zzaqx = new CopyOnWriteArraySet();
    private boolean zzaqy;
    private final AtomicReference<String> zzaqz = new AtomicReference<>();
    @VisibleForTesting
    protected boolean zzara = true;

    protected zzcs(zzbt zzbt) {
        super(zzbt);
    }

    /* access modifiers changed from: protected */
    public final boolean zzgt() {
        return false;
    }

    public final void zzks() {
        if (getContext().getApplicationContext() instanceof Application) {
            ((Application) getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(this.zzaqv);
        }
    }

    public final Boolean zzkt() {
        AtomicReference atomicReference = new AtomicReference();
        return (Boolean) zzgn().zza(atomicReference, 15000, "boolean test flag value", new zzct(this, atomicReference));
    }

    public final String zzku() {
        AtomicReference atomicReference = new AtomicReference();
        return (String) zzgn().zza(atomicReference, 15000, "String test flag value", new zzdd(this, atomicReference));
    }

    public final Long zzkv() {
        AtomicReference atomicReference = new AtomicReference();
        return (Long) zzgn().zza(atomicReference, 15000, "long test flag value", new zzdf(this, atomicReference));
    }

    public final Integer zzkw() {
        AtomicReference atomicReference = new AtomicReference();
        return (Integer) zzgn().zza(atomicReference, 15000, "int test flag value", new zzdg(this, atomicReference));
    }

    public final Double zzkx() {
        AtomicReference atomicReference = new AtomicReference();
        return (Double) zzgn().zza(atomicReference, 15000, "double test flag value", new zzdh(this, atomicReference));
    }

    public final void setMeasurementEnabled(boolean z) {
        zzcl();
        zzgb();
        zzgn().zzc((Runnable) new zzdi(this, z));
    }

    public final void zzd(boolean z) {
        zzcl();
        zzgb();
        zzgn().zzc((Runnable) new zzdj(this, z));
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzk(boolean z) {
        zzaf();
        zzgb();
        zzcl();
        zzgo().zzjk().zzg("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        zzgp().setMeasurementEnabled(z);
        zzky();
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzky() {
        if (zzgq().zze(zzgf().zzal(), zzaf.zzalj)) {
            this.zzadj.zzj(false);
        }
        if (!zzgq().zzbd(zzgf().zzal()) || !this.zzadj.isEnabled() || !this.zzara) {
            zzgo().zzjk().zzbx("Updating Scion state (FE)");
            zzgg().zzlc();
            return;
        }
        zzgo().zzjk().zzbx("Recording app launch after enabling measurement for the first time (FE)");
        zzkz();
    }

    public final void setMinimumSessionDuration(long j) {
        zzgb();
        zzgn().zzc((Runnable) new zzdk(this, j));
    }

    public final void setSessionTimeoutDuration(long j) {
        zzgb();
        zzgn().zzc((Runnable) new zzdl(this, j));
    }

    public final void zza(String str, String str2, Bundle bundle, boolean z) {
        logEvent(str, str2, bundle, false, true, zzbx().currentTimeMillis());
    }

    public final void logEvent(String str, String str2, Bundle bundle) {
        logEvent(str, str2, bundle, true, true, zzbx().currentTimeMillis());
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zza(String str, String str2, Bundle bundle) {
        zzgb();
        zzaf();
        zza(str, str2, zzbx().currentTimeMillis(), bundle);
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zza(String str, String str2, long j, Bundle bundle) {
        zzgb();
        zzaf();
        zza(str, str2, j, bundle, true, this.zzaqw == null || zzfk.zzcv(str2), false, null);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ab  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void zza(java.lang.String r32, java.lang.String r33, long r34, android.os.Bundle r36, boolean r37, boolean r38, boolean r39, java.lang.String r40) {
        /*
            r31 = this;
            r1 = r31
            r8 = r32
            r6 = r33
            r5 = r36
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r32)
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r33)
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r36)
            r31.zzaf()
            r31.zzcl()
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj
            boolean r4 = r4.isEnabled()
            if (r4 != 0) goto L_0x002d
            com.google.android.gms.measurement.internal.zzap r2 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjk()
            java.lang.String r3 = "Event not sent since app measurement is disabled"
            r2.zzbx(r3)
            return
        L_0x002d:
            boolean r4 = r1.zzaqy
            r7 = 0
            r16 = 0
            r15 = 1
            if (r4 != 0) goto L_0x0072
            r1.zzaqy = r15
            java.lang.String r4 = "com.google.android.gms.tagmanager.TagManagerService"
            java.lang.Class r4 = java.lang.Class.forName(r4)     // Catch:{ ClassNotFoundException -> 0x0065 }
            java.lang.String r9 = "initialize"
            java.lang.Class[] r10 = new java.lang.Class[r15]     // Catch:{ Exception -> 0x0055 }
            java.lang.Class<android.content.Context> r11 = android.content.Context.class
            r10[r16] = r11     // Catch:{ Exception -> 0x0055 }
            java.lang.reflect.Method r4 = r4.getDeclaredMethod(r9, r10)     // Catch:{ Exception -> 0x0055 }
            java.lang.Object[] r9 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x0055 }
            android.content.Context r10 = r31.getContext()     // Catch:{ Exception -> 0x0055 }
            r9[r16] = r10     // Catch:{ Exception -> 0x0055 }
            r4.invoke(r7, r9)     // Catch:{ Exception -> 0x0055 }
            goto L_0x0072
        L_0x0055:
            r0 = move-exception
            r4 = r0
            com.google.android.gms.measurement.internal.zzap r9 = r31.zzgo()     // Catch:{ ClassNotFoundException -> 0x0065 }
            com.google.android.gms.measurement.internal.zzar r9 = r9.zzjg()     // Catch:{ ClassNotFoundException -> 0x0065 }
            java.lang.String r10 = "Failed to invoke Tag Manager's initialize() method"
            r9.zzg(r10, r4)     // Catch:{ ClassNotFoundException -> 0x0065 }
            goto L_0x0072
        L_0x0065:
            com.google.android.gms.measurement.internal.zzap r4 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjj()
            java.lang.String r9 = "Tag Manager is not found and thus will not be used"
            r4.zzbx(r9)
        L_0x0072:
            r4 = 40
            r9 = 2
            if (r39 == 0) goto L_0x00df
            r31.zzgr()
            java.lang.String r10 = "_iap"
            boolean r10 = r10.equals(r6)
            if (r10 != 0) goto L_0x00df
            com.google.android.gms.measurement.internal.zzbt r10 = r1.zzadj
            com.google.android.gms.measurement.internal.zzfk r10 = r10.zzgm()
            java.lang.String r11 = "event"
            boolean r11 = r10.zzr(r11, r6)
            if (r11 != 0) goto L_0x0092
        L_0x0090:
            r10 = 2
            goto L_0x00a9
        L_0x0092:
            java.lang.String r11 = "event"
            java.lang.String[] r12 = com.google.android.gms.measurement.AppMeasurement.Event.zzadk
            boolean r11 = r10.zza(r11, r12, r6)
            if (r11 != 0) goto L_0x009f
            r10 = 13
            goto L_0x00a9
        L_0x009f:
            java.lang.String r11 = "event"
            boolean r10 = r10.zza(r11, r4, r6)
            if (r10 != 0) goto L_0x00a8
            goto L_0x0090
        L_0x00a8:
            r10 = 0
        L_0x00a9:
            if (r10 == 0) goto L_0x00df
            com.google.android.gms.measurement.internal.zzap r2 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjf()
            java.lang.String r3 = "Invalid public event name. Event will not be logged (FE)"
            com.google.android.gms.measurement.internal.zzan r5 = r31.zzgl()
            java.lang.String r5 = r5.zzbs(r6)
            r2.zzg(r3, r5)
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj
            r2.zzgm()
            java.lang.String r2 = com.google.android.gms.measurement.internal.zzfk.zza(r6, r4, r15)
            if (r6 == 0) goto L_0x00d2
            int r16 = r33.length()
            r3 = r16
            goto L_0x00d3
        L_0x00d2:
            r3 = 0
        L_0x00d3:
            com.google.android.gms.measurement.internal.zzbt r4 = r1.zzadj
            com.google.android.gms.measurement.internal.zzfk r4 = r4.zzgm()
            java.lang.String r5 = "_ev"
            r4.zza(r10, r5, r2, r3)
            return
        L_0x00df:
            r31.zzgr()
            com.google.android.gms.measurement.internal.zzdo r10 = r31.zzgh()
            com.google.android.gms.measurement.internal.zzdn r14 = r10.zzla()
            if (r14 == 0) goto L_0x00f6
            java.lang.String r10 = "_sc"
            boolean r10 = r5.containsKey(r10)
            if (r10 != 0) goto L_0x00f6
            r14.zzarn = r15
        L_0x00f6:
            if (r37 == 0) goto L_0x00fc
            if (r39 == 0) goto L_0x00fc
            r10 = 1
            goto L_0x00fd
        L_0x00fc:
            r10 = 0
        L_0x00fd:
            com.google.android.gms.measurement.internal.zzdo.zza(r14, r5, r10)
            java.lang.String r10 = "am"
            boolean r17 = r10.equals(r8)
            boolean r10 = com.google.android.gms.measurement.internal.zzfk.zzcv(r33)
            if (r37 == 0) goto L_0x013b
            com.google.android.gms.measurement.AppMeasurement$EventInterceptor r2 = r1.zzaqw
            if (r2 == 0) goto L_0x013b
            if (r10 != 0) goto L_0x013b
            if (r17 != 0) goto L_0x013b
            com.google.android.gms.measurement.internal.zzap r2 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjk()
            java.lang.String r3 = "Passing event to registered event handler (FE)"
            com.google.android.gms.measurement.internal.zzan r4 = r31.zzgl()
            java.lang.String r4 = r4.zzbs(r6)
            com.google.android.gms.measurement.internal.zzan r7 = r31.zzgl()
            java.lang.String r7 = r7.zzd(r5)
            r2.zze(r3, r4, r7)
            com.google.android.gms.measurement.AppMeasurement$EventInterceptor r2 = r1.zzaqw
            r3 = r8
            r4 = r6
            r6 = r34
            r2.interceptEvent(r3, r4, r5, r6)
            return
        L_0x013b:
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj
            boolean r2 = r2.zzkr()
            if (r2 != 0) goto L_0x0144
            return
        L_0x0144:
            com.google.android.gms.measurement.internal.zzfk r2 = r31.zzgm()
            int r20 = r2.zzcr(r6)
            if (r20 == 0) goto L_0x0183
            com.google.android.gms.measurement.internal.zzap r2 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjf()
            java.lang.String r3 = "Invalid event name. Event will not be logged (FE)"
            com.google.android.gms.measurement.internal.zzan r5 = r31.zzgl()
            java.lang.String r5 = r5.zzbs(r6)
            r2.zzg(r3, r5)
            r31.zzgm()
            java.lang.String r22 = com.google.android.gms.measurement.internal.zzfk.zza(r6, r4, r15)
            if (r6 == 0) goto L_0x0173
            int r16 = r33.length()
            r23 = r16
            goto L_0x0175
        L_0x0173:
            r23 = 0
        L_0x0175:
            com.google.android.gms.measurement.internal.zzbt r2 = r1.zzadj
            com.google.android.gms.measurement.internal.zzfk r18 = r2.zzgm()
            java.lang.String r21 = "_ev"
            r19 = r40
            r18.zza(r19, r20, r21, r22, r23)
            return
        L_0x0183:
            r2 = 4
            java.lang.String[] r2 = new java.lang.String[r2]
            java.lang.String r4 = "_o"
            r2[r16] = r4
            java.lang.String r4 = "_sn"
            r2[r15] = r4
            java.lang.String r4 = "_sc"
            r2[r9] = r4
            r4 = 3
            java.lang.String r9 = "_si"
            r2[r4] = r9
            java.util.List r2 = com.google.android.gms.common.util.CollectionUtils.listOf((T[]) r2)
            com.google.android.gms.measurement.internal.zzfk r9 = r31.zzgm()
            r4 = 1
            r10 = r40
            r11 = r6
            r12 = r5
            r13 = r2
            r18 = r14
            r14 = r39
            r7 = 1
            r15 = r4
            android.os.Bundle r4 = r9.zza(r10, r11, r12, r13, r14, r15)
            if (r4 == 0) goto L_0x01e3
            java.lang.String r9 = "_sc"
            boolean r9 = r4.containsKey(r9)
            if (r9 == 0) goto L_0x01e3
            java.lang.String r9 = "_si"
            boolean r9 = r4.containsKey(r9)
            if (r9 != 0) goto L_0x01c2
            goto L_0x01e3
        L_0x01c2:
            java.lang.String r9 = "_sn"
            java.lang.String r9 = r4.getString(r9)
            java.lang.String r10 = "_sc"
            java.lang.String r10 = r4.getString(r10)
            java.lang.String r11 = "_si"
            long r11 = r4.getLong(r11)
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            com.google.android.gms.measurement.internal.zzdn r12 = new com.google.android.gms.measurement.internal.zzdn
            long r13 = r11.longValue()
            r12.<init>(r9, r10, r13)
            r14 = r12
            goto L_0x01e4
        L_0x01e3:
            r14 = 0
        L_0x01e4:
            if (r14 != 0) goto L_0x01e9
            r15 = r18
            goto L_0x01ea
        L_0x01e9:
            r15 = r14
        L_0x01ea:
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
            r14.add(r4)
            com.google.android.gms.measurement.internal.zzfk r9 = r31.zzgm()
            java.security.SecureRandom r9 = r9.zzmd()
            long r12 = r9.nextLong()
            java.util.Set r9 = r4.keySet()
            int r5 = r36.size()
            java.lang.String[] r5 = new java.lang.String[r5]
            java.lang.Object[] r5 = r9.toArray(r5)
            java.lang.String[] r5 = (java.lang.String[]) r5
            java.util.Arrays.sort(r5)
            int r11 = r5.length
            r9 = 0
            r10 = 0
        L_0x0214:
            if (r10 >= r11) goto L_0x02ca
            r7 = r5[r10]
            r24 = r5
            java.lang.Object r5 = r4.get(r7)
            r31.zzgm()
            android.os.Bundle[] r5 = com.google.android.gms.measurement.internal.zzfk.zze(r5)
            if (r5 == 0) goto L_0x02aa
            r25 = r9
            int r9 = r5.length
            r4.putInt(r7, r9)
            r26 = r10
            r9 = 0
        L_0x0230:
            int r10 = r5.length
            if (r9 >= r10) goto L_0x0297
            r10 = r5[r9]
            r8 = 1
            com.google.android.gms.measurement.internal.zzdo.zza(r15, r10, r8)
            com.google.android.gms.measurement.internal.zzfk r18 = r31.zzgm()
            java.lang.String r19 = "_ep"
            r20 = 0
            r27 = r9
            r8 = r25
            r9 = r18
            r21 = r10
            r18 = r26
            r10 = r40
            r22 = r11
            r11 = r19
            r28 = r4
            r29 = r5
            r4 = r12
            r12 = r21
            r13 = r2
            r30 = r2
            r2 = r14
            r14 = r39
            r19 = r15
            r15 = r20
            android.os.Bundle r9 = r9.zza(r10, r11, r12, r13, r14, r15)
            java.lang.String r10 = "_en"
            r9.putString(r10, r6)
            java.lang.String r10 = "_eid"
            r9.putLong(r10, r4)
            java.lang.String r10 = "_gn"
            r9.putString(r10, r7)
            java.lang.String r10 = "_ll"
            r11 = r29
            int r12 = r11.length
            r9.putInt(r10, r12)
            java.lang.String r10 = "_i"
            r12 = r27
            r9.putInt(r10, r12)
            r2.add(r9)
            int r9 = r12 + 1
            r14 = r2
            r12 = r4
            r5 = r11
            r15 = r19
            r11 = r22
            r4 = r28
            r2 = r30
            r8 = r32
            goto L_0x0230
        L_0x0297:
            r30 = r2
            r28 = r4
            r22 = r11
            r2 = r14
            r19 = r15
            r8 = r25
            r18 = r26
            r11 = r5
            r4 = r12
            int r7 = r11.length
            int r9 = r8 + r7
            goto L_0x02b7
        L_0x02aa:
            r30 = r2
            r28 = r4
            r8 = r9
            r18 = r10
            r22 = r11
            r4 = r12
            r2 = r14
            r19 = r15
        L_0x02b7:
            int r10 = r18 + 1
            r14 = r2
            r12 = r4
            r15 = r19
            r11 = r22
            r5 = r24
            r4 = r28
            r2 = r30
            r7 = 1
            r8 = r32
            goto L_0x0214
        L_0x02ca:
            r28 = r4
            r8 = r9
            r4 = r12
            r2 = r14
            if (r8 == 0) goto L_0x02dd
            java.lang.String r3 = "_eid"
            r7 = r28
            r7.putLong(r3, r4)
            java.lang.String r3 = "_epc"
            r7.putInt(r3, r8)
        L_0x02dd:
            r8 = 0
        L_0x02de:
            int r3 = r2.size()
            if (r8 >= r3) goto L_0x036a
            java.lang.Object r3 = r2.get(r8)
            android.os.Bundle r3 = (android.os.Bundle) r3
            if (r8 == 0) goto L_0x02ee
            r4 = 1
            goto L_0x02ef
        L_0x02ee:
            r4 = 0
        L_0x02ef:
            if (r4 == 0) goto L_0x02f4
            java.lang.String r4 = "_ep"
            goto L_0x02f5
        L_0x02f4:
            r4 = r6
        L_0x02f5:
            java.lang.String r5 = "_o"
            r7 = 1
            r9 = r32
            r3.putString(r5, r9)
            if (r38 == 0) goto L_0x0307
            com.google.android.gms.measurement.internal.zzfk r5 = r31.zzgm()
            android.os.Bundle r3 = r5.zze(r3)
        L_0x0307:
            r11 = r3
            com.google.android.gms.measurement.internal.zzap r3 = r31.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjk()
            java.lang.String r5 = "Logging event (FE)"
            com.google.android.gms.measurement.internal.zzan r12 = r31.zzgl()
            java.lang.String r12 = r12.zzbs(r6)
            com.google.android.gms.measurement.internal.zzan r13 = r31.zzgl()
            java.lang.String r13 = r13.zzd(r11)
            r3.zze(r5, r12, r13)
            com.google.android.gms.measurement.internal.zzad r12 = new com.google.android.gms.measurement.internal.zzad
            com.google.android.gms.measurement.internal.zzaa r5 = new com.google.android.gms.measurement.internal.zzaa
            r5.<init>(r11)
            r13 = r2
            r2 = r12
            r3 = r4
            r4 = r5
            r5 = r9
            r14 = r6
            r15 = 1
            r6 = r34
            r2.<init>(r3, r4, r5, r6)
            com.google.android.gms.measurement.internal.zzdr r2 = r31.zzgg()
            r6 = r40
            r2.zzb(r12, r6)
            if (r17 != 0) goto L_0x0364
            java.util.Set<com.google.android.gms.measurement.AppMeasurement$OnEventListener> r2 = r1.zzaqx
            java.util.Iterator r12 = r2.iterator()
        L_0x0349:
            boolean r2 = r12.hasNext()
            if (r2 == 0) goto L_0x0364
            java.lang.Object r2 = r12.next()
            com.google.android.gms.measurement.AppMeasurement$OnEventListener r2 = (com.google.android.gms.measurement.AppMeasurement.OnEventListener) r2
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>(r11)
            r3 = r9
            r4 = r14
            r6 = r34
            r2.onEvent(r3, r4, r5, r6)
            r6 = r40
            goto L_0x0349
        L_0x0364:
            int r8 = r8 + 1
            r2 = r13
            r6 = r14
            goto L_0x02de
        L_0x036a:
            r14 = r6
            r15 = 1
            r31.zzgr()
            com.google.android.gms.measurement.internal.zzdo r2 = r31.zzgh()
            com.google.android.gms.measurement.internal.zzdn r2 = r2.zzla()
            if (r2 == 0) goto L_0x0388
            java.lang.String r2 = "_ae"
            boolean r2 = r2.equals(r14)
            if (r2 == 0) goto L_0x0388
            com.google.android.gms.measurement.internal.zzeq r2 = r31.zzgj()
            r2.zzn(r15)
        L_0x0388:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzcs.zza(java.lang.String, java.lang.String, long, android.os.Bundle, boolean, boolean, boolean, java.lang.String):void");
    }

    public final void logEvent(String str, String str2, Bundle bundle, boolean z, boolean z2, long j) {
        zzcs zzcs;
        boolean z3;
        zzgb();
        String str3 = str == null ? "app" : str;
        Bundle bundle2 = bundle == null ? new Bundle() : bundle;
        if (z2) {
            zzcs = this;
            if (zzcs.zzaqw != null && !zzfk.zzcv(str2)) {
                z3 = false;
                zzcs.zzb(str3, str2, j, bundle2, z2, z3, !z, null);
            }
        } else {
            zzcs = this;
        }
        z3 = true;
        zzcs.zzb(str3, str2, j, bundle2, z2, z3, !z, null);
    }

    private final void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        Bundle zzf = zzfk.zzf(bundle);
        zzbo zzgn = zzgn();
        zzcu zzcu = new zzcu(this, str, str2, j, zzf, z, z2, z3, str3);
        zzgn.zzc((Runnable) zzcu);
    }

    public final void zzb(String str, String str2, Object obj, boolean z) {
        zza(str, str2, obj, z, zzbx().currentTimeMillis());
    }

    public final void zza(String str, String str2, Object obj, boolean z, long j) {
        if (str == null) {
            str = "app";
        }
        String str3 = str;
        int i = 6;
        int i2 = 0;
        if (z || "_ap".equals(str2)) {
            i = zzgm().zzcs(str2);
        } else {
            zzfk zzgm = zzgm();
            if (zzgm.zzr("user property", str2)) {
                if (!zzgm.zza("user property", UserProperty.zzado, str2)) {
                    i = 15;
                } else if (zzgm.zza("user property", 24, str2)) {
                    i = 0;
                }
            }
        }
        if (i != 0) {
            zzgm();
            String zza = zzfk.zza(str2, 24, true);
            if (str2 != null) {
                i2 = str2.length();
            }
            this.zzadj.zzgm().zza(i, "_ev", zza, i2);
        } else if (obj != null) {
            int zzi = zzgm().zzi(str2, obj);
            if (zzi != 0) {
                zzgm();
                String zza2 = zzfk.zza(str2, 24, true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i2 = String.valueOf(obj).length();
                }
                this.zzadj.zzgm().zza(zzi, "_ev", zza2, i2);
                return;
            }
            Object zzj = zzgm().zzj(str2, obj);
            if (zzj != null) {
                zza(str3, str2, j, zzj);
            }
        } else {
            zza(str3, str2, j, (Object) null);
        }
    }

    private final void zza(String str, String str2, long j, Object obj) {
        zzbo zzgn = zzgn();
        zzcv zzcv = new zzcv(this, str, str2, obj, j);
        zzgn.zzc((Runnable) zzcv);
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final void zza(String str, String str2, Object obj, long j) {
        Preconditions.checkNotEmpty(str);
        Preconditions.checkNotEmpty(str2);
        zzaf();
        zzgb();
        zzcl();
        if (zzgq().zze(zzgf().zzal(), zzaf.zzalj)) {
            if ("_ap".equals(str2) && !ReactScrollViewHelper.AUTO.equals(str)) {
                if (obj instanceof String) {
                    String str3 = (String) obj;
                    if (!TextUtils.isEmpty(str3)) {
                        obj = Long.valueOf(("true".equals(str3.toLowerCase(Locale.ENGLISH)) || "1".equals(obj)) ? 1 : 0);
                        zzgp().zzans.zzcc(((Long) obj).longValue() == 1 ? "true" : "false");
                    }
                }
                if (obj == null) {
                    zzgp().zzans.zzcc("unset");
                    zzgn().zzc((Runnable) new zzcw(this));
                }
            }
        } else if ("_ap".equals(str2)) {
            return;
        }
        Object obj2 = obj;
        if (!this.zzadj.isEnabled()) {
            zzgo().zzjk().zzbx("User property not set since app measurement is disabled");
        } else if (this.zzadj.zzkr()) {
            zzgo().zzjk().zze("Setting user property (FE)", zzgl().zzbs(str2), obj2);
            zzfh zzfh = new zzfh(str2, j, obj2, str);
            zzgg().zzb(zzfh);
        }
    }

    public final List<zzfh> zzl(boolean z) {
        zzgb();
        zzcl();
        zzgo().zzjk().zzbx("Fetching user attributes (FE)");
        if (zzgn().zzkb()) {
            zzgo().zzjd().zzbx("Cannot get all user properties from analytics worker thread");
            return Collections.emptyList();
        } else if (zzk.isMainThread()) {
            zzgo().zzjd().zzbx("Cannot get all user properties from main thread");
            return Collections.emptyList();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                this.zzadj.zzgn().zzc((Runnable) new zzcx(this, atomicReference, z));
                try {
                    atomicReference.wait(RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
                } catch (InterruptedException e) {
                    zzgo().zzjg().zzg("Interrupted waiting for get user properties", e);
                }
            }
            List<zzfh> list = (List) atomicReference.get();
            if (list != null) {
                return list;
            }
            zzgo().zzjg().zzbx("Timed out waiting for get user properties");
            return Collections.emptyList();
        }
    }

    @Nullable
    public final String zzfx() {
        zzgb();
        return (String) this.zzaqz.get();
    }

    @Nullable
    public final String zzaj(long j) {
        if (zzgn().zzkb()) {
            zzgo().zzjd().zzbx("Cannot retrieve app instance id from analytics worker thread");
            return null;
        } else if (zzk.isMainThread()) {
            zzgo().zzjd().zzbx("Cannot retrieve app instance id from main thread");
            return null;
        } else {
            long elapsedRealtime = zzbx().elapsedRealtime();
            String zzak = zzak(120000);
            long elapsedRealtime2 = zzbx().elapsedRealtime() - elapsedRealtime;
            if (zzak == null && elapsedRealtime2 < 120000) {
                zzak = zzak(120000 - elapsedRealtime2);
            }
            return zzak;
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zzcm(@Nullable String str) {
        this.zzaqz.set(str);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:10|11|12|13) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        zzgo().zzjg().zzbx("Interrupted waiting for app instance id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        return null;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x001d */
    @android.support.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.String zzak(long r4) {
        /*
            r3 = this;
            java.util.concurrent.atomic.AtomicReference r0 = new java.util.concurrent.atomic.AtomicReference
            r0.<init>()
            monitor-enter(r0)
            com.google.android.gms.measurement.internal.zzbo r1 = r3.zzgn()     // Catch:{ all -> 0x002d }
            com.google.android.gms.measurement.internal.zzcy r2 = new com.google.android.gms.measurement.internal.zzcy     // Catch:{ all -> 0x002d }
            r2.<init>(r3, r0)     // Catch:{ all -> 0x002d }
            r1.zzc(r2)     // Catch:{ all -> 0x002d }
            r0.wait(r4)     // Catch:{ InterruptedException -> 0x001d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            java.lang.Object r4 = r0.get()
            java.lang.String r4 = (java.lang.String) r4
            return r4
        L_0x001d:
            com.google.android.gms.measurement.internal.zzap r4 = r3.zzgo()     // Catch:{ all -> 0x002d }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjg()     // Catch:{ all -> 0x002d }
            java.lang.String r5 = "Interrupted waiting for app instance id"
            r4.zzbx(r5)     // Catch:{ all -> 0x002d }
            r4 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return r4
        L_0x002d:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzcs.zzak(long):java.lang.String");
    }

    public final void resetAnalyticsData(long j) {
        if (zzgq().zza(zzaf.zzalk)) {
            zzcm(null);
        }
        zzgn().zzc((Runnable) new zzcz(this, j));
    }

    @WorkerThread
    public final void zzkz() {
        zzaf();
        zzgb();
        zzcl();
        if (this.zzadj.zzkr()) {
            zzgg().zzkz();
            this.zzara = false;
            String zzjw = zzgp().zzjw();
            if (!TextUtils.isEmpty(zzjw)) {
                zzgk().zzcl();
                if (!zzjw.equals(VERSION.RELEASE)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("_po", zzjw);
                    logEvent(ReactScrollViewHelper.AUTO, "_ou", bundle);
                }
            }
        }
    }

    @WorkerThread
    public final void setEventInterceptor(EventInterceptor eventInterceptor) {
        zzaf();
        zzgb();
        zzcl();
        if (!(eventInterceptor == null || eventInterceptor == this.zzaqw)) {
            Preconditions.checkState(this.zzaqw == null, "EventInterceptor already set.");
        }
        this.zzaqw = eventInterceptor;
    }

    public final void registerOnMeasurementEventListener(OnEventListener onEventListener) {
        zzgb();
        zzcl();
        Preconditions.checkNotNull(onEventListener);
        if (!this.zzaqx.add(onEventListener)) {
            zzgo().zzjg().zzbx("OnEventListener already registered");
        }
    }

    public final void unregisterOnMeasurementEventListener(OnEventListener onEventListener) {
        zzgb();
        zzcl();
        Preconditions.checkNotNull(onEventListener);
        if (!this.zzaqx.remove(onEventListener)) {
            zzgo().zzjg().zzbx("OnEventListener had not been registered");
        }
    }

    public final void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        Preconditions.checkNotNull(conditionalUserProperty);
        zzgb();
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            zzgo().zzjg().zzbx("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public final void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty.mAppId);
        zzga();
        zza(new ConditionalUserProperty(conditionalUserProperty));
    }

    private final void zza(ConditionalUserProperty conditionalUserProperty) {
        long currentTimeMillis = zzbx().currentTimeMillis();
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty.mName);
        Preconditions.checkNotEmpty(conditionalUserProperty.mOrigin);
        Preconditions.checkNotNull(conditionalUserProperty.mValue);
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        String str = conditionalUserProperty.mName;
        Object obj = conditionalUserProperty.mValue;
        if (zzgm().zzcs(str) != 0) {
            zzgo().zzjd().zzg("Invalid conditional user property name", zzgl().zzbu(str));
        } else if (zzgm().zzi(str, obj) != 0) {
            zzgo().zzjd().zze("Invalid conditional user property value", zzgl().zzbu(str), obj);
        } else {
            Object zzj = zzgm().zzj(str, obj);
            if (zzj == null) {
                zzgo().zzjd().zze("Unable to normalize conditional user property value", zzgl().zzbu(str), obj);
                return;
            }
            conditionalUserProperty.mValue = zzj;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (TextUtils.isEmpty(conditionalUserProperty.mTriggerEventName) || (j <= 15552000000L && j >= 1)) {
                long j2 = conditionalUserProperty.mTimeToLive;
                if (j2 > 15552000000L || j2 < 1) {
                    zzgo().zzjd().zze("Invalid conditional user property time to live", zzgl().zzbu(str), Long.valueOf(j2));
                } else {
                    zzgn().zzc((Runnable) new zzda(this, conditionalUserProperty));
                }
            } else {
                zzgo().zzjd().zze("Invalid conditional user property timeout", zzgl().zzbu(str), Long.valueOf(j));
            }
        }
    }

    public final void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        zzgb();
        zza((String) null, str, str2, bundle);
    }

    public final void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        Preconditions.checkNotEmpty(str);
        zzga();
        zza(str, str2, str3, bundle);
    }

    private final void zza(String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = zzbx().currentTimeMillis();
        Preconditions.checkNotEmpty(str2);
        ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
        conditionalUserProperty.mAppId = str;
        conditionalUserProperty.mName = str2;
        conditionalUserProperty.mCreationTimestamp = currentTimeMillis;
        if (str3 != null) {
            conditionalUserProperty.mExpiredEventName = str3;
            conditionalUserProperty.mExpiredEventParams = bundle;
        }
        zzgn().zzc((Runnable) new zzdb(this, conditionalUserProperty));
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzb(ConditionalUserProperty conditionalUserProperty) {
        ConditionalUserProperty conditionalUserProperty2 = conditionalUserProperty;
        zzaf();
        zzcl();
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mName);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mOrigin);
        Preconditions.checkNotNull(conditionalUserProperty2.mValue);
        if (!this.zzadj.isEnabled()) {
            zzgo().zzjk().zzbx("Conditional property not sent since collection is disabled");
            return;
        }
        zzfh zzfh = new zzfh(conditionalUserProperty2.mName, conditionalUserProperty2.mTriggeredTimestamp, conditionalUserProperty2.mValue, conditionalUserProperty2.mOrigin);
        try {
            zzad zza = zzgm().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mTriggeredEventName, conditionalUserProperty2.mTriggeredEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            zzad zza2 = zzgm().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mTimedOutEventName, conditionalUserProperty2.mTimedOutEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            zzad zza3 = zzgm().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mExpiredEventName, conditionalUserProperty2.mExpiredEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            String str = conditionalUserProperty2.mAppId;
            String str2 = conditionalUserProperty2.mOrigin;
            long j = conditionalUserProperty2.mCreationTimestamp;
            String str3 = conditionalUserProperty2.mTriggerEventName;
            long j2 = conditionalUserProperty2.mTriggerTimeout;
            zzl zzl = r3;
            zzl zzl2 = new zzl(str, str2, zzfh, j, false, str3, zza2, j2, zza, conditionalUserProperty2.mTimeToLive, zza3);
            zzgg().zzd(zzl);
        } catch (IllegalArgumentException unused) {
        }
    }

    /* access modifiers changed from: private */
    @WorkerThread
    public final void zzc(ConditionalUserProperty conditionalUserProperty) {
        ConditionalUserProperty conditionalUserProperty2 = conditionalUserProperty;
        zzaf();
        zzcl();
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mName);
        if (!this.zzadj.isEnabled()) {
            zzgo().zzjk().zzbx("Conditional property not cleared since collection is disabled");
            return;
        }
        zzfh zzfh = new zzfh(conditionalUserProperty2.mName, 0, null, null);
        try {
            zzad zza = zzgm().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mExpiredEventName, conditionalUserProperty2.mExpiredEventParams, conditionalUserProperty2.mOrigin, conditionalUserProperty2.mCreationTimestamp, true, false);
            String str = conditionalUserProperty2.mAppId;
            String str2 = conditionalUserProperty2.mOrigin;
            long j = conditionalUserProperty2.mCreationTimestamp;
            boolean z = conditionalUserProperty2.mActive;
            String str3 = conditionalUserProperty2.mTriggerEventName;
            long j2 = conditionalUserProperty2.mTriggerTimeout;
            zzl zzl = r3;
            zzl zzl2 = new zzl(str, str2, zzfh, j, z, str3, null, j2, null, conditionalUserProperty2.mTimeToLive, zza);
            zzgg().zzd(zzl);
        } catch (IllegalArgumentException unused) {
        }
    }

    public final List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        zzgb();
        return zzf(null, str, str2);
    }

    public final List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        Preconditions.checkNotEmpty(str);
        zzga();
        return zzf(str, str2, str3);
    }

    @VisibleForTesting
    private final List<ConditionalUserProperty> zzf(String str, String str2, String str3) {
        if (zzgn().zzkb()) {
            zzgo().zzjd().zzbx("Cannot get conditional user properties from analytics worker thread");
            return Collections.emptyList();
        } else if (zzk.isMainThread()) {
            zzgo().zzjd().zzbx("Cannot get conditional user properties from main thread");
            return Collections.emptyList();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                zzbo zzgn = this.zzadj.zzgn();
                zzdc zzdc = new zzdc(this, atomicReference, str, str2, str3);
                zzgn.zzc((Runnable) zzdc);
                try {
                    atomicReference.wait(RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
                } catch (InterruptedException e) {
                    zzgo().zzjg().zze("Interrupted waiting for get conditional user properties", str, e);
                }
            }
            List<zzl> list = (List) atomicReference.get();
            if (list == null) {
                zzgo().zzjg().zzg("Timed out waiting for get conditional user properties", str);
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList(list.size());
            for (zzl zzl : list) {
                ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
                conditionalUserProperty.mAppId = zzl.packageName;
                conditionalUserProperty.mOrigin = zzl.origin;
                conditionalUserProperty.mCreationTimestamp = zzl.creationTimestamp;
                conditionalUserProperty.mName = zzl.zzahb.name;
                conditionalUserProperty.mValue = zzl.zzahb.getValue();
                conditionalUserProperty.mActive = zzl.active;
                conditionalUserProperty.mTriggerEventName = zzl.triggerEventName;
                if (zzl.zzahc != null) {
                    conditionalUserProperty.mTimedOutEventName = zzl.zzahc.name;
                    if (zzl.zzahc.zzaid != null) {
                        conditionalUserProperty.mTimedOutEventParams = zzl.zzahc.zzaid.zziv();
                    }
                }
                conditionalUserProperty.mTriggerTimeout = zzl.triggerTimeout;
                if (zzl.zzahd != null) {
                    conditionalUserProperty.mTriggeredEventName = zzl.zzahd.name;
                    if (zzl.zzahd.zzaid != null) {
                        conditionalUserProperty.mTriggeredEventParams = zzl.zzahd.zzaid.zziv();
                    }
                }
                conditionalUserProperty.mTriggeredTimestamp = zzl.zzahb.zzaue;
                conditionalUserProperty.mTimeToLive = zzl.timeToLive;
                if (zzl.zzahe != null) {
                    conditionalUserProperty.mExpiredEventName = zzl.zzahe.name;
                    if (zzl.zzahe.zzaid != null) {
                        conditionalUserProperty.mExpiredEventParams = zzl.zzahe.zzaid.zziv();
                    }
                }
                arrayList.add(conditionalUserProperty);
            }
            return arrayList;
        }
    }

    public final Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        zzgb();
        return zzb((String) null, str, str2, z);
    }

    public final Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        Preconditions.checkNotEmpty(str);
        zzga();
        return zzb(str, str2, str3, z);
    }

    @VisibleForTesting
    private final Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        if (zzgn().zzkb()) {
            zzgo().zzjd().zzbx("Cannot get user properties from analytics worker thread");
            return Collections.emptyMap();
        } else if (zzk.isMainThread()) {
            zzgo().zzjd().zzbx("Cannot get user properties from main thread");
            return Collections.emptyMap();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                zzbo zzgn = this.zzadj.zzgn();
                zzde zzde = new zzde(this, atomicReference, str, str2, str3, z);
                zzgn.zzc((Runnable) zzde);
                try {
                    atomicReference.wait(RangedBeacon.DEFAULT_MAX_TRACKING_AGE);
                } catch (InterruptedException e) {
                    zzgo().zzjg().zzg("Interrupted waiting for get user properties", e);
                }
            }
            List<zzfh> list = (List) atomicReference.get();
            if (list == null) {
                zzgo().zzjg().zzbx("Timed out waiting for get user properties");
                return Collections.emptyMap();
            }
            ArrayMap arrayMap = new ArrayMap(list.size());
            for (zzfh zzfh : list) {
                arrayMap.put(zzfh.name, zzfh.getValue());
            }
            return arrayMap;
        }
    }

    @Nullable
    public final String getCurrentScreenName() {
        zzdn zzlb = this.zzadj.zzgh().zzlb();
        if (zzlb != null) {
            return zzlb.zzuw;
        }
        return null;
    }

    @Nullable
    public final String getCurrentScreenClass() {
        zzdn zzlb = this.zzadj.zzgh().zzlb();
        if (zzlb != null) {
            return zzlb.zzarl;
        }
        return null;
    }

    @Nullable
    public final String getGmpAppId() {
        if (this.zzadj.zzkk() != null) {
            return this.zzadj.zzkk();
        }
        try {
            return GoogleServices.getGoogleAppId();
        } catch (IllegalStateException e) {
            this.zzadj.zzgo().zzjd().zzg("getGoogleAppId failed with exception", e);
            return null;
        }
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
