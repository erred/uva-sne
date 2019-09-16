package com.google.android.gms.internal.measurement;

public class zzwa {
    private static final zzuz zzbtt = zzuz.zzvo();
    private zzud zzcad;
    private volatile zzwt zzcae;
    private volatile zzud zzcaf;

    public int hashCode() {
        return 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof zzwa)) {
            return false;
        }
        zzwa zzwa = (zzwa) obj;
        zzwt zzwt = this.zzcae;
        zzwt zzwt2 = zzwa.zzcae;
        if (zzwt == null && zzwt2 == null) {
            return zztt().equals(zzwa.zztt());
        }
        if (zzwt != null && zzwt2 != null) {
            return zzwt.equals(zzwt2);
        }
        if (zzwt != null) {
            return zzwt.equals(zzwa.zzh(zzwt.zzwf()));
        }
        return zzh(zzwt2.zzwf()).equals(zzwt2);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:7|8|9|10|11|12) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0012 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final com.google.android.gms.internal.measurement.zzwt zzh(com.google.android.gms.internal.measurement.zzwt r2) {
        /*
            r1 = this;
            com.google.android.gms.internal.measurement.zzwt r0 = r1.zzcae
            if (r0 != 0) goto L_0x001d
            monitor-enter(r1)
            com.google.android.gms.internal.measurement.zzwt r0 = r1.zzcae     // Catch:{ all -> 0x001a }
            if (r0 == 0) goto L_0x000b
            monitor-exit(r1)     // Catch:{ all -> 0x001a }
            goto L_0x001d
        L_0x000b:
            r1.zzcae = r2     // Catch:{ zzvt -> 0x0012 }
            com.google.android.gms.internal.measurement.zzud r0 = com.google.android.gms.internal.measurement.zzud.zzbtz     // Catch:{ zzvt -> 0x0012 }
            r1.zzcaf = r0     // Catch:{ zzvt -> 0x0012 }
            goto L_0x0018
        L_0x0012:
            r1.zzcae = r2     // Catch:{ all -> 0x001a }
            com.google.android.gms.internal.measurement.zzud r2 = com.google.android.gms.internal.measurement.zzud.zzbtz     // Catch:{ all -> 0x001a }
            r1.zzcaf = r2     // Catch:{ all -> 0x001a }
        L_0x0018:
            monitor-exit(r1)     // Catch:{ all -> 0x001a }
            goto L_0x001d
        L_0x001a:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x001a }
            throw r2
        L_0x001d:
            com.google.android.gms.internal.measurement.zzwt r2 = r1.zzcae
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.measurement.zzwa.zzh(com.google.android.gms.internal.measurement.zzwt):com.google.android.gms.internal.measurement.zzwt");
    }

    public final zzwt zzi(zzwt zzwt) {
        zzwt zzwt2 = this.zzcae;
        this.zzcad = null;
        this.zzcaf = null;
        this.zzcae = zzwt;
        return zzwt2;
    }

    public final int zzvu() {
        if (this.zzcaf != null) {
            return this.zzcaf.size();
        }
        if (this.zzcae != null) {
            return this.zzcae.zzvu();
        }
        return 0;
    }

    public final zzud zztt() {
        if (this.zzcaf != null) {
            return this.zzcaf;
        }
        synchronized (this) {
            if (this.zzcaf != null) {
                zzud zzud = this.zzcaf;
                return zzud;
            }
            if (this.zzcae == null) {
                this.zzcaf = zzud.zzbtz;
            } else {
                this.zzcaf = this.zzcae.zztt();
            }
            zzud zzud2 = this.zzcaf;
            return zzud2;
        }
    }
}
