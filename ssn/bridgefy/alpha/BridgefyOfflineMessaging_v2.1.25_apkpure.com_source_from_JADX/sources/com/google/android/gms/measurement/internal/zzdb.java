package com.google.android.gms.measurement.internal;

import java.util.concurrent.atomic.AtomicReference;

final class zzdb implements Runnable {
    private final /* synthetic */ AtomicReference zzard;
    private final /* synthetic */ zzda zzare;

    zzdb(zzda zzda, AtomicReference atomicReference) {
        this.zzare = zzda;
        this.zzard = atomicReference;
    }

    public final void run() {
        synchronized (this.zzard) {
            try {
                this.zzard.set(Boolean.valueOf(this.zzare.zzgv().zzax(this.zzare.zzgk().zzal())));
                this.zzard.notify();
            } catch (Throwable th) {
                this.zzard.notify();
                throw th;
            }
        }
    }
}
