package com.google.android.gms.tagmanager;

import com.google.android.gms.internal.measurement.zza;
import com.google.android.gms.internal.measurement.zzb;

final class zzei extends zzbq {

    /* renamed from: ID */
    private static final String f6690ID = zza.RANDOM.toString();
    private static final String zzbei = zzb.MIN.toString();
    private static final String zzbej = zzb.MAX.toString();

    public zzei() {
        super(f6690ID, new String[0]);
    }

    public final boolean zznk() {
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003e, code lost:
        if (r0 <= r2) goto L_0x0048;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.google.android.gms.internal.measurement.zzp zzc(java.util.Map<java.lang.String, com.google.android.gms.internal.measurement.zzp> r7) {
        /*
            r6 = this;
            java.lang.String r0 = zzbei
            java.lang.Object r0 = r7.get(r0)
            com.google.android.gms.internal.measurement.zzp r0 = (com.google.android.gms.internal.measurement.zzp) r0
            java.lang.String r1 = zzbej
            java.lang.Object r7 = r7.get(r1)
            com.google.android.gms.internal.measurement.zzp r7 = (com.google.android.gms.internal.measurement.zzp) r7
            if (r0 == 0) goto L_0x0041
            com.google.android.gms.internal.measurement.zzp r1 = com.google.android.gms.tagmanager.zzgj.zzqq()
            if (r0 == r1) goto L_0x0041
            if (r7 == 0) goto L_0x0041
            com.google.android.gms.internal.measurement.zzp r1 = com.google.android.gms.tagmanager.zzgj.zzqq()
            if (r7 == r1) goto L_0x0041
            com.google.android.gms.tagmanager.zzgi r0 = com.google.android.gms.tagmanager.zzgj.zzd(r0)
            com.google.android.gms.tagmanager.zzgi r7 = com.google.android.gms.tagmanager.zzgj.zzd(r7)
            com.google.android.gms.tagmanager.zzgi r1 = com.google.android.gms.tagmanager.zzgj.zzqo()
            if (r0 == r1) goto L_0x0041
            com.google.android.gms.tagmanager.zzgi r1 = com.google.android.gms.tagmanager.zzgj.zzqo()
            if (r7 == r1) goto L_0x0041
            double r0 = r0.doubleValue()
            double r2 = r7.doubleValue()
            int r7 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r7 > 0) goto L_0x0041
            goto L_0x0048
        L_0x0041:
            r0 = 0
            r2 = 4746794007244308480(0x41dfffffffc00000, double:2.147483647E9)
        L_0x0048:
            double r4 = java.lang.Math.random()
            double r2 = r2 - r0
            double r4 = r4 * r2
            double r4 = r4 + r0
            long r0 = java.lang.Math.round(r4)
            java.lang.Long r7 = java.lang.Long.valueOf(r0)
            com.google.android.gms.internal.measurement.zzp r7 = com.google.android.gms.tagmanager.zzgj.zzj(r7)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.tagmanager.zzei.zzc(java.util.Map):com.google.android.gms.internal.measurement.zzp");
    }
}
