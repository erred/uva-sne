package com.google.android.gms.measurement.internal;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.p052b.C0712a;
import com.google.android.gms.common.api.internal.GoogleServices;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.measurement.AppMeasurement.ConditionalUserProperty;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.tagmanager.DataLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public final class zzda extends zzf {
    @VisibleForTesting
    protected zzdu zzaqx;
    private zzcx zzaqy;
    private final Set<zzcy> zzaqz = new CopyOnWriteArraySet();
    private boolean zzara;
    private final AtomicReference<String> zzarb = new AtomicReference<>();
    @VisibleForTesting
    protected boolean zzarc = true;

    protected zzda(zzbw zzbw) {
        super(zzbw);
    }

    /* access modifiers changed from: protected */
    public final boolean zzgy() {
        return false;
    }

    public final void zzkw() {
        if (getContext().getApplicationContext() instanceof Application) {
            ((Application) getContext().getApplicationContext()).unregisterActivityLifecycleCallbacks(this.zzaqx);
        }
    }

    public final Boolean zzkx() {
        AtomicReference atomicReference = new AtomicReference();
        return (Boolean) zzgs().zza(atomicReference, 15000, "boolean test flag value", new zzdb(this, atomicReference));
    }

    public final String zzky() {
        AtomicReference atomicReference = new AtomicReference();
        return (String) zzgs().zza(atomicReference, 15000, "String test flag value", new zzdl(this, atomicReference));
    }

    public final Long zzkz() {
        AtomicReference atomicReference = new AtomicReference();
        return (Long) zzgs().zza(atomicReference, 15000, "long test flag value", new zzdn(this, atomicReference));
    }

    public final Integer zzla() {
        AtomicReference atomicReference = new AtomicReference();
        return (Integer) zzgs().zza(atomicReference, 15000, "int test flag value", new zzdo(this, atomicReference));
    }

    public final Double zzlb() {
        AtomicReference atomicReference = new AtomicReference();
        return (Double) zzgs().zza(atomicReference, 15000, "double test flag value", new zzdp(this, atomicReference));
    }

    public final void setMeasurementEnabled(boolean z) {
        zzcl();
        zzgg();
        zzgs().zzc((Runnable) new zzdq(this, z));
    }

    public final void zzd(boolean z) {
        zzcl();
        zzgg();
        zzgs().zzc((Runnable) new zzdr(this, z));
    }

    /* access modifiers changed from: private */
    public final void zzj(boolean z) {
        zzaf();
        zzgg();
        zzcl();
        zzgt().zzjn().zzg("Setting app measurement enabled (FE)", Boolean.valueOf(z));
        zzgu().setMeasurementEnabled(z);
        zzlc();
    }

    /* access modifiers changed from: private */
    public final void zzlc() {
        if (!zzgv().zzba(zzgk().zzal()) || !this.zzada.isEnabled() || !this.zzarc) {
            zzgt().zzjn().zzby("Updating Scion state (FE)");
            zzgl().zzlg();
            return;
        }
        zzgt().zzjn().zzby("Recording app launch after enabling measurement for the first time (FE)");
        zzld();
    }

    public final void setMinimumSessionDuration(long j) {
        zzgg();
        zzgs().zzc((Runnable) new zzds(this, j));
    }

    public final void setSessionTimeoutDuration(long j) {
        zzgg();
        zzgs().zzc((Runnable) new zzdt(this, j));
    }

    public final void zza(String str, String str2, Bundle bundle, boolean z) {
        logEvent(str, str2, bundle, false, true, zzbx().currentTimeMillis());
    }

    public final void logEvent(String str, String str2, Bundle bundle) {
        logEvent(str, str2, bundle, true, true, zzbx().currentTimeMillis());
    }

    /* access modifiers changed from: 0000 */
    public final void zza(String str, String str2, Bundle bundle) {
        zzgg();
        zzaf();
        zza(str, str2, zzbx().currentTimeMillis(), bundle);
    }

    /* access modifiers changed from: 0000 */
    public final void zza(String str, String str2, long j, Bundle bundle) {
        zzgg();
        zzaf();
        zza(str, str2, j, bundle, true, this.zzaqy == null || zzfy.zzcy(str2), false, null);
    }

    /* access modifiers changed from: private */
    public final void zza(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        zzdx zzdx;
        List list;
        int i;
        int i2;
        String[] strArr;
        long j2;
        ArrayList arrayList;
        String str4 = str;
        String str5 = str2;
        Bundle bundle2 = bundle;
        String str6 = str3;
        Preconditions.checkNotEmpty(str);
        if (!zzgv().zze(str6, zzai.zzalg)) {
            Preconditions.checkNotEmpty(str2);
        }
        Preconditions.checkNotNull(bundle);
        zzaf();
        zzcl();
        if (!this.zzada.isEnabled()) {
            zzgt().zzjn().zzby("Event not sent since app measurement is disabled");
            return;
        }
        int i3 = 0;
        if (!this.zzara) {
            this.zzara = true;
            try {
                try {
                    Class.forName("com.google.android.gms.tagmanager.TagManagerService").getDeclaredMethod("initialize", new Class[]{Context.class}).invoke(null, new Object[]{getContext()});
                } catch (Exception e) {
                    zzgt().zzjj().zzg("Failed to invoke Tag Manager's initialize() method", e);
                }
            } catch (ClassNotFoundException unused) {
                zzgt().zzjm().zzby("Tag Manager is not found and thus will not be used");
            }
        }
        if (z3) {
            zzgw();
            if (!"_iap".equals(str5)) {
                zzfy zzgr = this.zzada.zzgr();
                int i4 = 2;
                if (zzgr.zzs(DataLayer.EVENT_KEY, str5)) {
                    if (!zzgr.zza(DataLayer.EVENT_KEY, zzcu.zzaqq, str5)) {
                        i4 = 13;
                    } else if (zzgr.zza(DataLayer.EVENT_KEY, 40, str5)) {
                        i4 = 0;
                    }
                }
                if (i4 != 0) {
                    zzgt().zzji().zzg("Invalid public event name. Event will not be logged (FE)", zzgq().zzbt(str5));
                    this.zzada.zzgr();
                    this.zzada.zzgr().zza(i4, "_ev", zzfy.zza(str5, 40, true), str5 != null ? str2.length() : 0);
                    return;
                }
            }
        }
        zzgw();
        zzdx zzle = zzgm().zzle();
        if (zzle != null && !bundle2.containsKey("_sc")) {
            zzle.zzarp = true;
        }
        zzdy.zza(zzle, bundle2, z && z3);
        boolean equals = "am".equals(str4);
        boolean zzcy = zzfy.zzcy(str2);
        if (z && this.zzaqy != null && !zzcy && !equals) {
            zzgt().zzjn().zze("Passing event to registered event handler (FE)", zzgq().zzbt(str5), zzgq().zzd(bundle2));
            this.zzaqy.interceptEvent(str, str2, bundle, j);
        } else if (this.zzada.zzkv()) {
            int zzcu = zzgr().zzcu(str5);
            if (zzcu != 0) {
                zzgt().zzji().zzg("Invalid event name. Event will not be logged (FE)", zzgq().zzbt(str5));
                zzgr();
                String zza = zzfy.zza(str5, 40, true);
                if (str5 != null) {
                    i3 = str2.length();
                }
                this.zzada.zzgr().zza(str3, zzcu, "_ev", zza, i3);
                return;
            }
            List listOf = CollectionUtils.listOf((T[]) new String[]{"_o", "_sn", "_sc", "_si"});
            zzdx zzdx2 = zzle;
            Bundle zza2 = zzgr().zza(str3, str2, bundle, listOf, z3, true);
            zzdx zzdx3 = (zza2 == null || !zza2.containsKey("_sc") || !zza2.containsKey("_si")) ? null : new zzdx(zza2.getString("_sn"), zza2.getString("_sc"), Long.valueOf(zza2.getLong("_si")).longValue());
            zzdx zzdx4 = zzdx3 == null ? zzdx2 : zzdx3;
            if (zzgv().zzbk(str6)) {
                zzgw();
                if (zzgm().zzle() != null && Event.APP_EXCEPTION.equals(str5)) {
                    long zzlp = zzgo().zzlp();
                    if (zzlp > 0) {
                        zzgr().zza(zza2, zzlp);
                    }
                }
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(zza2);
            long nextLong = zzgr().zzmk().nextLong();
            if (!zzgv().zzbj(zzgk().zzal()) || zza2.getLong("extend_session", 0) != 1) {
                long j3 = j;
            } else {
                zzgt().zzjo().zzby("EXTEND_SESSION param attached: initiate a new session or extend the current active session");
                this.zzada.zzgo().zza(j, true);
            }
            String[] strArr2 = (String[]) zza2.keySet().toArray(new String[bundle.size()]);
            Arrays.sort(strArr2);
            int length = strArr2.length;
            ArrayList arrayList3 = arrayList2;
            int i5 = 0;
            int i6 = 0;
            while (i5 < length) {
                String str7 = strArr2[i5];
                Object obj = zza2.get(str7);
                zzgr();
                int i7 = i5;
                Bundle[] zzf = zzfy.zzf(obj);
                if (zzf != null) {
                    zza2.putInt(str7, zzf.length);
                    int i8 = 0;
                    while (i8 < zzf.length) {
                        Bundle bundle3 = zzf[i8];
                        Bundle[] bundleArr = zzf;
                        zzdy.zza(zzdx4, bundle3, true);
                        int i9 = length;
                        String[] strArr3 = strArr2;
                        Bundle bundle4 = bundle3;
                        long j4 = nextLong;
                        List list2 = listOf;
                        zzdx zzdx5 = zzdx4;
                        ArrayList arrayList4 = arrayList3;
                        int i10 = i7;
                        Bundle[] bundleArr2 = bundleArr;
                        Bundle zza3 = zzgr().zza(str3, "_ep", bundle4, listOf, z3, false);
                        zza3.putString("_en", str5);
                        zza3.putLong("_eid", j4);
                        zza3.putString("_gn", str7);
                        zza3.putInt("_ll", bundleArr2.length);
                        zza3.putInt("_i", i8);
                        arrayList4.add(zza3);
                        i8++;
                        long j5 = j;
                        zzf = bundleArr2;
                        nextLong = j4;
                        strArr2 = strArr3;
                        length = i9;
                        zzdx4 = zzdx5;
                        String str8 = str;
                        arrayList3 = arrayList4;
                        listOf = list2;
                    }
                    list = listOf;
                    zzdx = zzdx4;
                    i = length;
                    strArr = strArr2;
                    j2 = nextLong;
                    arrayList = arrayList3;
                    i2 = i7;
                    i6 += zzf.length;
                } else {
                    list = listOf;
                    zzdx = zzdx4;
                    i = length;
                    strArr = strArr2;
                    j2 = nextLong;
                    arrayList = arrayList3;
                    int i11 = i6;
                    i2 = i7;
                }
                i5 = i2 + 1;
                long j6 = j;
                arrayList3 = arrayList;
                nextLong = j2;
                strArr2 = strArr;
                length = i;
                listOf = list;
                zzdx4 = zzdx;
                String str9 = str3;
                String str10 = str;
            }
            long j7 = nextLong;
            ArrayList arrayList5 = arrayList3;
            int i12 = i6;
            if (i12 != 0) {
                zza2.putLong("_eid", j7);
                zza2.putInt("_epc", i12);
            }
            int i13 = 0;
            while (i13 < arrayList5.size()) {
                Bundle bundle5 = (Bundle) arrayList5.get(i13);
                String str11 = i13 != 0 ? "_ep" : str5;
                bundle5.putString("_o", str);
                if (z2) {
                    bundle5 = zzgr().zze(bundle5);
                }
                Bundle bundle6 = bundle5;
                zzgt().zzjn().zze("Logging event (FE)", zzgq().zzbt(str5), zzgq().zzd(bundle6));
                String str12 = str5;
                String str13 = str3;
                zzag zzag = new zzag(str11, new zzad(bundle6), str, j);
                zzgl().zzc(zzag, str13);
                if (!equals) {
                    for (zzcy onEvent : this.zzaqz) {
                        onEvent.onEvent(str, str2, new Bundle(bundle6), j);
                    }
                }
                i13++;
                str5 = str12;
            }
            String str14 = str5;
            zzgw();
            if (zzgm().zzle() != null && Event.APP_EXCEPTION.equals(str14)) {
                zzgo().zza(true, true);
            }
        }
    }

    public final void logEvent(String str, String str2, Bundle bundle, boolean z, boolean z2, long j) {
        boolean z3;
        zzgg();
        String str3 = str == null ? "app" : str;
        Bundle bundle2 = bundle == null ? new Bundle() : bundle;
        if (z2) {
            if (this.zzaqy != null && !zzfy.zzcy(str2)) {
                z3 = false;
                zzb(str3, str2, j, bundle2, z2, z3, !z, null);
            }
        }
        z3 = true;
        zzb(str3, str2, j, bundle2, z2, z3, !z, null);
    }

    private final void zzb(String str, String str2, long j, Bundle bundle, boolean z, boolean z2, boolean z3, String str3) {
        Bundle zzf = zzfy.zzf(bundle);
        zzbr zzgs = zzgs();
        zzdc zzdc = new zzdc(this, str, str2, j, zzf, z, z2, z3, str3);
        zzgs.zzc((Runnable) zzdc);
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
        if (z) {
            i = zzgr().zzcv(str2);
        } else {
            zzfy zzgr = zzgr();
            if (zzgr.zzs("user property", str2)) {
                if (!zzgr.zza("user property", zzcw.zzaqu, str2)) {
                    i = 15;
                } else if (zzgr.zza("user property", 24, str2)) {
                    i = 0;
                }
            }
        }
        if (i != 0) {
            zzgr();
            String zza = zzfy.zza(str2, 24, true);
            if (str2 != null) {
                i2 = str2.length();
            }
            this.zzada.zzgr().zza(i, "_ev", zza, i2);
        } else if (obj != null) {
            int zzi = zzgr().zzi(str2, obj);
            if (zzi != 0) {
                zzgr();
                String zza2 = zzfy.zza(str2, 24, true);
                if ((obj instanceof String) || (obj instanceof CharSequence)) {
                    i2 = String.valueOf(obj).length();
                }
                this.zzada.zzgr().zza(zzi, "_ev", zza2, i2);
                return;
            }
            Object zzj = zzgr().zzj(str2, obj);
            if (zzj != null) {
                zza(str3, str2, j, zzj);
            }
        } else {
            zza(str3, str2, j, (Object) null);
        }
    }

    private final void zza(String str, String str2, long j, Object obj) {
        zzbr zzgs = zzgs();
        zzdd zzdd = new zzdd(this, str, str2, obj, j);
        zzgs.zzc((Runnable) zzdd);
    }

    /* access modifiers changed from: 0000 */
    public final void zza(String str, String str2, Object obj, long j) {
        Preconditions.checkNotEmpty(str);
        Preconditions.checkNotEmpty(str2);
        zzaf();
        zzgg();
        zzcl();
        if (!this.zzada.isEnabled()) {
            zzgt().zzjn().zzby("User property not set since app measurement is disabled");
        } else if (this.zzada.zzkv()) {
            zzgt().zzjn().zze("Setting user property (FE)", zzgq().zzbt(str2), obj);
            zzfv zzfv = new zzfv(str2, j, obj, str);
            zzgl().zzb(zzfv);
        }
    }

    public final List<zzfv> zzk(boolean z) {
        zzgg();
        zzcl();
        zzgt().zzjn().zzby("Fetching user attributes (FE)");
        if (zzgs().zzkf()) {
            zzgt().zzjg().zzby("Cannot get all user properties from analytics worker thread");
            return Collections.emptyList();
        } else if (zzn.isMainThread()) {
            zzgt().zzjg().zzby("Cannot get all user properties from main thread");
            return Collections.emptyList();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                this.zzada.zzgs().zzc((Runnable) new zzde(this, atomicReference, z));
                try {
                    atomicReference.wait(5000);
                } catch (InterruptedException e) {
                    zzgt().zzjj().zzg("Interrupted waiting for get user properties", e);
                }
            }
            List<zzfv> list = (List) atomicReference.get();
            if (list != null) {
                return list;
            }
            zzgt().zzjj().zzby("Timed out waiting for get user properties");
            return Collections.emptyList();
        }
    }

    public final String zzgc() {
        zzgg();
        return (String) this.zzarb.get();
    }

    public final String zzag(long j) {
        if (zzgs().zzkf()) {
            zzgt().zzjg().zzby("Cannot retrieve app instance id from analytics worker thread");
            return null;
        } else if (zzn.isMainThread()) {
            zzgt().zzjg().zzby("Cannot retrieve app instance id from main thread");
            return null;
        } else {
            long elapsedRealtime = zzbx().elapsedRealtime();
            String zzah = zzah(120000);
            long elapsedRealtime2 = zzbx().elapsedRealtime() - elapsedRealtime;
            if (zzah == null && elapsedRealtime2 < 120000) {
                zzah = zzah(120000 - elapsedRealtime2);
            }
            return zzah;
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zzcp(String str) {
        this.zzarb.set(str);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:10|11|12|13) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        zzgt().zzjj().zzby("Interrupted waiting for app instance id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        return null;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x001d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final java.lang.String zzah(long r4) {
        /*
            r3 = this;
            java.util.concurrent.atomic.AtomicReference r0 = new java.util.concurrent.atomic.AtomicReference
            r0.<init>()
            monitor-enter(r0)
            com.google.android.gms.measurement.internal.zzbr r1 = r3.zzgs()     // Catch:{ all -> 0x002d }
            com.google.android.gms.measurement.internal.zzdf r2 = new com.google.android.gms.measurement.internal.zzdf     // Catch:{ all -> 0x002d }
            r2.<init>(r3, r0)     // Catch:{ all -> 0x002d }
            r1.zzc(r2)     // Catch:{ all -> 0x002d }
            r0.wait(r4)     // Catch:{ InterruptedException -> 0x001d }
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            java.lang.Object r4 = r0.get()
            java.lang.String r4 = (java.lang.String) r4
            return r4
        L_0x001d:
            com.google.android.gms.measurement.internal.zzas r4 = r3.zzgt()     // Catch:{ all -> 0x002d }
            com.google.android.gms.measurement.internal.zzau r4 = r4.zzjj()     // Catch:{ all -> 0x002d }
            java.lang.String r5 = "Interrupted waiting for app instance id"
            r4.zzby(r5)     // Catch:{ all -> 0x002d }
            r4 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            return r4
        L_0x002d:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002d }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzda.zzah(long):java.lang.String");
    }

    public final void resetAnalyticsData(long j) {
        if (zzgv().zza(zzai.zzalb)) {
            zzcp(null);
        }
        zzgs().zzc((Runnable) new zzdg(this, j));
    }

    public final void zzld() {
        zzaf();
        zzgg();
        zzcl();
        if (this.zzada.zzkv()) {
            zzgl().zzld();
            this.zzarc = false;
            String zzka = zzgu().zzka();
            if (!TextUtils.isEmpty(zzka)) {
                zzgp().zzcl();
                if (!zzka.equals(VERSION.RELEASE)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("_po", zzka);
                    logEvent("auto", "_ou", bundle);
                }
            }
        }
    }

    public final void zza(zzcx zzcx) {
        zzaf();
        zzgg();
        zzcl();
        if (!(zzcx == null || zzcx == this.zzaqy)) {
            Preconditions.checkState(this.zzaqy == null, "EventInterceptor already set.");
        }
        this.zzaqy = zzcx;
    }

    public final void zza(zzcy zzcy) {
        zzgg();
        zzcl();
        Preconditions.checkNotNull(zzcy);
        if (!this.zzaqz.add(zzcy)) {
            zzgt().zzjj().zzby("OnEventListener already registered");
        }
    }

    public final void zzb(zzcy zzcy) {
        zzgg();
        zzcl();
        Preconditions.checkNotNull(zzcy);
        if (!this.zzaqz.remove(zzcy)) {
            zzgt().zzjj().zzby("OnEventListener had not been registered");
        }
    }

    public final void setConditionalUserProperty(ConditionalUserProperty conditionalUserProperty) {
        Preconditions.checkNotNull(conditionalUserProperty);
        zzgg();
        ConditionalUserProperty conditionalUserProperty2 = new ConditionalUserProperty(conditionalUserProperty);
        if (!TextUtils.isEmpty(conditionalUserProperty2.mAppId)) {
            zzgt().zzjj().zzby("Package name should be null when calling setConditionalUserProperty");
        }
        conditionalUserProperty2.mAppId = null;
        zza(conditionalUserProperty2);
    }

    public final void setConditionalUserPropertyAs(ConditionalUserProperty conditionalUserProperty) {
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty.mAppId);
        zzgf();
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
        if (zzgr().zzcv(str) != 0) {
            zzgt().zzjg().zzg("Invalid conditional user property name", zzgq().zzbv(str));
        } else if (zzgr().zzi(str, obj) != 0) {
            zzgt().zzjg().zze("Invalid conditional user property value", zzgq().zzbv(str), obj);
        } else {
            Object zzj = zzgr().zzj(str, obj);
            if (zzj == null) {
                zzgt().zzjg().zze("Unable to normalize conditional user property value", zzgq().zzbv(str), obj);
                return;
            }
            conditionalUserProperty.mValue = zzj;
            long j = conditionalUserProperty.mTriggerTimeout;
            if (TextUtils.isEmpty(conditionalUserProperty.mTriggerEventName) || (j <= 15552000000L && j >= 1)) {
                long j2 = conditionalUserProperty.mTimeToLive;
                if (j2 > 15552000000L || j2 < 1) {
                    zzgt().zzjg().zze("Invalid conditional user property time to live", zzgq().zzbv(str), Long.valueOf(j2));
                } else {
                    zzgs().zzc((Runnable) new zzdi(this, conditionalUserProperty));
                }
            } else {
                zzgt().zzjg().zze("Invalid conditional user property timeout", zzgq().zzbv(str), Long.valueOf(j));
            }
        }
    }

    public final void clearConditionalUserProperty(String str, String str2, Bundle bundle) {
        zzgg();
        zza((String) null, str, str2, bundle);
    }

    public final void clearConditionalUserPropertyAs(String str, String str2, String str3, Bundle bundle) {
        Preconditions.checkNotEmpty(str);
        zzgf();
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
        zzgs().zzc((Runnable) new zzdj(this, conditionalUserProperty));
    }

    /* access modifiers changed from: private */
    public final void zzb(ConditionalUserProperty conditionalUserProperty) {
        ConditionalUserProperty conditionalUserProperty2 = conditionalUserProperty;
        zzaf();
        zzcl();
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mName);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mOrigin);
        Preconditions.checkNotNull(conditionalUserProperty2.mValue);
        if (!this.zzada.isEnabled()) {
            zzgt().zzjn().zzby("Conditional property not sent since collection is disabled");
            return;
        }
        zzfv zzfv = new zzfv(conditionalUserProperty2.mName, conditionalUserProperty2.mTriggeredTimestamp, conditionalUserProperty2.mValue, conditionalUserProperty2.mOrigin);
        try {
            zzag zza = zzgr().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mTriggeredEventName, conditionalUserProperty2.mTriggeredEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            zzag zza2 = zzgr().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mTimedOutEventName, conditionalUserProperty2.mTimedOutEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            zzag zza3 = zzgr().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mExpiredEventName, conditionalUserProperty2.mExpiredEventParams, conditionalUserProperty2.mOrigin, 0, true, false);
            String str = conditionalUserProperty2.mAppId;
            String str2 = conditionalUserProperty2.mOrigin;
            long j = conditionalUserProperty2.mCreationTimestamp;
            String str3 = conditionalUserProperty2.mTriggerEventName;
            long j2 = conditionalUserProperty2.mTriggerTimeout;
            zzo zzo = r3;
            zzo zzo2 = new zzo(str, str2, zzfv, j, false, str3, zza2, j2, zza, conditionalUserProperty2.mTimeToLive, zza3);
            zzgl().zzd(zzo);
        } catch (IllegalArgumentException unused) {
        }
    }

    /* access modifiers changed from: private */
    public final void zzc(ConditionalUserProperty conditionalUserProperty) {
        ConditionalUserProperty conditionalUserProperty2 = conditionalUserProperty;
        zzaf();
        zzcl();
        Preconditions.checkNotNull(conditionalUserProperty);
        Preconditions.checkNotEmpty(conditionalUserProperty2.mName);
        if (!this.zzada.isEnabled()) {
            zzgt().zzjn().zzby("Conditional property not cleared since collection is disabled");
            return;
        }
        zzfv zzfv = new zzfv(conditionalUserProperty2.mName, 0, null, null);
        try {
            zzag zza = zzgr().zza(conditionalUserProperty2.mAppId, conditionalUserProperty2.mExpiredEventName, conditionalUserProperty2.mExpiredEventParams, conditionalUserProperty2.mOrigin, conditionalUserProperty2.mCreationTimestamp, true, false);
            String str = conditionalUserProperty2.mAppId;
            String str2 = conditionalUserProperty2.mOrigin;
            long j = conditionalUserProperty2.mCreationTimestamp;
            boolean z = conditionalUserProperty2.mActive;
            String str3 = conditionalUserProperty2.mTriggerEventName;
            long j2 = conditionalUserProperty2.mTriggerTimeout;
            zzo zzo = r3;
            zzo zzo2 = new zzo(str, str2, zzfv, j, z, str3, null, j2, null, conditionalUserProperty2.mTimeToLive, zza);
            zzgl().zzd(zzo);
        } catch (IllegalArgumentException unused) {
        }
    }

    public final List<ConditionalUserProperty> getConditionalUserProperties(String str, String str2) {
        zzgg();
        return zzf(null, str, str2);
    }

    public final List<ConditionalUserProperty> getConditionalUserPropertiesAs(String str, String str2, String str3) {
        Preconditions.checkNotEmpty(str);
        zzgf();
        return zzf(str, str2, str3);
    }

    @VisibleForTesting
    private final List<ConditionalUserProperty> zzf(String str, String str2, String str3) {
        if (zzgs().zzkf()) {
            zzgt().zzjg().zzby("Cannot get conditional user properties from analytics worker thread");
            return Collections.emptyList();
        } else if (zzn.isMainThread()) {
            zzgt().zzjg().zzby("Cannot get conditional user properties from main thread");
            return Collections.emptyList();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                zzbr zzgs = this.zzada.zzgs();
                zzdk zzdk = new zzdk(this, atomicReference, str, str2, str3);
                zzgs.zzc((Runnable) zzdk);
                try {
                    atomicReference.wait(5000);
                } catch (InterruptedException e) {
                    zzgt().zzjj().zze("Interrupted waiting for get conditional user properties", str, e);
                }
            }
            List<zzo> list = (List) atomicReference.get();
            if (list == null) {
                zzgt().zzjj().zzg("Timed out waiting for get conditional user properties", str);
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList(list.size());
            for (zzo zzo : list) {
                ConditionalUserProperty conditionalUserProperty = new ConditionalUserProperty();
                conditionalUserProperty.mAppId = zzo.packageName;
                conditionalUserProperty.mOrigin = zzo.origin;
                conditionalUserProperty.mCreationTimestamp = zzo.creationTimestamp;
                conditionalUserProperty.mName = zzo.zzags.name;
                conditionalUserProperty.mValue = zzo.zzags.getValue();
                conditionalUserProperty.mActive = zzo.active;
                conditionalUserProperty.mTriggerEventName = zzo.triggerEventName;
                if (zzo.zzagt != null) {
                    conditionalUserProperty.mTimedOutEventName = zzo.zzagt.name;
                    if (zzo.zzagt.zzahu != null) {
                        conditionalUserProperty.mTimedOutEventParams = zzo.zzagt.zzahu.zziy();
                    }
                }
                conditionalUserProperty.mTriggerTimeout = zzo.triggerTimeout;
                if (zzo.zzagu != null) {
                    conditionalUserProperty.mTriggeredEventName = zzo.zzagu.name;
                    if (zzo.zzagu.zzahu != null) {
                        conditionalUserProperty.mTriggeredEventParams = zzo.zzagu.zzahu.zziy();
                    }
                }
                conditionalUserProperty.mTriggeredTimestamp = zzo.zzags.zzauk;
                conditionalUserProperty.mTimeToLive = zzo.timeToLive;
                if (zzo.zzagv != null) {
                    conditionalUserProperty.mExpiredEventName = zzo.zzagv.name;
                    if (zzo.zzagv.zzahu != null) {
                        conditionalUserProperty.mExpiredEventParams = zzo.zzagv.zzahu.zziy();
                    }
                }
                arrayList.add(conditionalUserProperty);
            }
            return arrayList;
        }
    }

    public final Map<String, Object> getUserProperties(String str, String str2, boolean z) {
        zzgg();
        return zzb((String) null, str, str2, z);
    }

    public final Map<String, Object> getUserPropertiesAs(String str, String str2, String str3, boolean z) {
        Preconditions.checkNotEmpty(str);
        zzgf();
        return zzb(str, str2, str3, z);
    }

    @VisibleForTesting
    private final Map<String, Object> zzb(String str, String str2, String str3, boolean z) {
        if (zzgs().zzkf()) {
            zzgt().zzjg().zzby("Cannot get user properties from analytics worker thread");
            return Collections.emptyMap();
        } else if (zzn.isMainThread()) {
            zzgt().zzjg().zzby("Cannot get user properties from main thread");
            return Collections.emptyMap();
        } else {
            AtomicReference atomicReference = new AtomicReference();
            synchronized (atomicReference) {
                zzbr zzgs = this.zzada.zzgs();
                zzdm zzdm = new zzdm(this, atomicReference, str, str2, str3, z);
                zzgs.zzc((Runnable) zzdm);
                try {
                    atomicReference.wait(5000);
                } catch (InterruptedException e) {
                    zzgt().zzjj().zzg("Interrupted waiting for get user properties", e);
                }
            }
            List<zzfv> list = (List) atomicReference.get();
            if (list == null) {
                zzgt().zzjj().zzby("Timed out waiting for get user properties");
                return Collections.emptyMap();
            }
            C0712a aVar = new C0712a(list.size());
            for (zzfv zzfv : list) {
                aVar.put(zzfv.name, zzfv.getValue());
            }
            return aVar;
        }
    }

    public final String getCurrentScreenName() {
        zzdx zzlf = this.zzada.zzgm().zzlf();
        if (zzlf != null) {
            return zzlf.zzuw;
        }
        return null;
    }

    public final String getCurrentScreenClass() {
        zzdx zzlf = this.zzada.zzgm().zzlf();
        if (zzlf != null) {
            return zzlf.zzarn;
        }
        return null;
    }

    public final String getGmpAppId() {
        if (this.zzada.zzko() != null) {
            return this.zzada.zzko();
        }
        try {
            return GoogleServices.getGoogleAppId();
        } catch (IllegalStateException e) {
            this.zzada.zzgt().zzjg().zzg("getGoogleAppId failed with exception", e);
            return null;
        }
    }

    public final /* bridge */ /* synthetic */ void zzgf() {
        super.zzgf();
    }

    public final /* bridge */ /* synthetic */ void zzgg() {
        super.zzgg();
    }

    public final /* bridge */ /* synthetic */ void zzgh() {
        super.zzgh();
    }

    public final /* bridge */ /* synthetic */ void zzaf() {
        super.zzaf();
    }

    public final /* bridge */ /* synthetic */ zza zzgi() {
        return super.zzgi();
    }

    public final /* bridge */ /* synthetic */ zzda zzgj() {
        return super.zzgj();
    }

    public final /* bridge */ /* synthetic */ zzam zzgk() {
        return super.zzgk();
    }

    public final /* bridge */ /* synthetic */ zzeb zzgl() {
        return super.zzgl();
    }

    public final /* bridge */ /* synthetic */ zzdy zzgm() {
        return super.zzgm();
    }

    public final /* bridge */ /* synthetic */ zzao zzgn() {
        return super.zzgn();
    }

    public final /* bridge */ /* synthetic */ zzfd zzgo() {
        return super.zzgo();
    }

    public final /* bridge */ /* synthetic */ zzaa zzgp() {
        return super.zzgp();
    }

    public final /* bridge */ /* synthetic */ Clock zzbx() {
        return super.zzbx();
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final /* bridge */ /* synthetic */ zzaq zzgq() {
        return super.zzgq();
    }

    public final /* bridge */ /* synthetic */ zzfy zzgr() {
        return super.zzgr();
    }

    public final /* bridge */ /* synthetic */ zzbr zzgs() {
        return super.zzgs();
    }

    public final /* bridge */ /* synthetic */ zzas zzgt() {
        return super.zzgt();
    }

    public final /* bridge */ /* synthetic */ zzbd zzgu() {
        return super.zzgu();
    }

    public final /* bridge */ /* synthetic */ zzq zzgv() {
        return super.zzgv();
    }

    public final /* bridge */ /* synthetic */ zzn zzgw() {
        return super.zzgw();
    }
}
