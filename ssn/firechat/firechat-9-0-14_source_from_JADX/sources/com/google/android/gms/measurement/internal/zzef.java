package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.BaseGmsClient.BaseConnectionCallbacks;
import com.google.android.gms.common.internal.BaseGmsClient.BaseOnConnectionFailedListener;
import com.google.android.gms.common.internal.GmsClientSupervisor;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.stats.ConnectionTracker;
import com.google.android.gms.common.util.VisibleForTesting;

@VisibleForTesting
public final class zzef implements ServiceConnection, BaseConnectionCallbacks, BaseOnConnectionFailedListener {
    final /* synthetic */ zzdr zzasg;
    /* access modifiers changed from: private */
    public volatile boolean zzasm;
    private volatile zzao zzasn;

    protected zzef(zzdr zzdr) {
        this.zzasg = zzdr;
    }

    @WorkerThread
    public final void zzc(Intent intent) {
        this.zzasg.zzaf();
        Context context = this.zzasg.getContext();
        ConnectionTracker instance = ConnectionTracker.getInstance();
        synchronized (this) {
            if (this.zzasm) {
                this.zzasg.zzgo().zzjl().zzbx("Connection attempt already in progress");
                return;
            }
            this.zzasg.zzgo().zzjl().zzbx("Using local app measurement service");
            this.zzasm = true;
            instance.bindService(context, intent, this.zzasg.zzarz, GmsClientSupervisor.DEFAULT_BIND_FLAGS);
        }
    }

    @WorkerThread
    public final void zzlg() {
        if (this.zzasn != null && (this.zzasn.isConnected() || this.zzasn.isConnecting())) {
            this.zzasn.disconnect();
        }
        this.zzasn = null;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:22|23) */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        r3.zzasg.zzgo().zzjd().zzbx("Service connect failed to get IMeasurementService");
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x0063 */
    @android.support.annotation.MainThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onServiceConnected(android.content.ComponentName r4, android.os.IBinder r5) {
        /*
            r3 = this;
            java.lang.String r4 = "MeasurementServiceConnection.onServiceConnected"
            com.google.android.gms.common.internal.Preconditions.checkMainThread(r4)
            monitor-enter(r3)
            r4 = 0
            if (r5 != 0) goto L_0x001f
            r3.zzasm = r4     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzdr r4 = r3.zzasg     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzap r4 = r4.zzgo()     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjd()     // Catch:{ all -> 0x001c }
            java.lang.String r5 = "Service connected with null binder"
            r4.zzbx(r5)     // Catch:{ all -> 0x001c }
            monitor-exit(r3)     // Catch:{ all -> 0x001c }
            return
        L_0x001c:
            r4 = move-exception
            goto L_0x009a
        L_0x001f:
            r0 = 0
            java.lang.String r1 = r5.getInterfaceDescriptor()     // Catch:{ RemoteException -> 0x0063 }
            java.lang.String r2 = "com.google.android.gms.measurement.internal.IMeasurementService"
            boolean r2 = r2.equals(r1)     // Catch:{ RemoteException -> 0x0063 }
            if (r2 == 0) goto L_0x0053
            if (r5 != 0) goto L_0x002f
            goto L_0x0043
        L_0x002f:
            java.lang.String r1 = "com.google.android.gms.measurement.internal.IMeasurementService"
            android.os.IInterface r1 = r5.queryLocalInterface(r1)     // Catch:{ RemoteException -> 0x0063 }
            boolean r2 = r1 instanceof com.google.android.gms.measurement.internal.zzag     // Catch:{ RemoteException -> 0x0063 }
            if (r2 == 0) goto L_0x003d
            com.google.android.gms.measurement.internal.zzag r1 = (com.google.android.gms.measurement.internal.zzag) r1     // Catch:{ RemoteException -> 0x0063 }
        L_0x003b:
            r0 = r1
            goto L_0x0043
        L_0x003d:
            com.google.android.gms.measurement.internal.zzai r1 = new com.google.android.gms.measurement.internal.zzai     // Catch:{ RemoteException -> 0x0063 }
            r1.<init>(r5)     // Catch:{ RemoteException -> 0x0063 }
            goto L_0x003b
        L_0x0043:
            com.google.android.gms.measurement.internal.zzdr r5 = r3.zzasg     // Catch:{ RemoteException -> 0x0063 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ RemoteException -> 0x0063 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjl()     // Catch:{ RemoteException -> 0x0063 }
            java.lang.String r1 = "Bound to IMeasurementService interface"
            r5.zzbx(r1)     // Catch:{ RemoteException -> 0x0063 }
            goto L_0x0072
        L_0x0053:
            com.google.android.gms.measurement.internal.zzdr r5 = r3.zzasg     // Catch:{ RemoteException -> 0x0063 }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ RemoteException -> 0x0063 }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ RemoteException -> 0x0063 }
            java.lang.String r2 = "Got binder with a wrong descriptor"
            r5.zzg(r2, r1)     // Catch:{ RemoteException -> 0x0063 }
            goto L_0x0072
        L_0x0063:
            com.google.android.gms.measurement.internal.zzdr r5 = r3.zzasg     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzap r5 = r5.zzgo()     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ all -> 0x001c }
            java.lang.String r1 = "Service connect failed to get IMeasurementService"
            r5.zzbx(r1)     // Catch:{ all -> 0x001c }
        L_0x0072:
            if (r0 != 0) goto L_0x008a
            r3.zzasm = r4     // Catch:{ all -> 0x001c }
            com.google.android.gms.common.stats.ConnectionTracker r4 = com.google.android.gms.common.stats.ConnectionTracker.getInstance()     // Catch:{ IllegalArgumentException -> 0x0098 }
            com.google.android.gms.measurement.internal.zzdr r5 = r3.zzasg     // Catch:{ IllegalArgumentException -> 0x0098 }
            android.content.Context r5 = r5.getContext()     // Catch:{ IllegalArgumentException -> 0x0098 }
            com.google.android.gms.measurement.internal.zzdr r0 = r3.zzasg     // Catch:{ IllegalArgumentException -> 0x0098 }
            com.google.android.gms.measurement.internal.zzef r0 = r0.zzarz     // Catch:{ IllegalArgumentException -> 0x0098 }
            r4.unbindService(r5, r0)     // Catch:{ IllegalArgumentException -> 0x0098 }
            goto L_0x0098
        L_0x008a:
            com.google.android.gms.measurement.internal.zzdr r4 = r3.zzasg     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzbo r4 = r4.zzgn()     // Catch:{ all -> 0x001c }
            com.google.android.gms.measurement.internal.zzeg r5 = new com.google.android.gms.measurement.internal.zzeg     // Catch:{ all -> 0x001c }
            r5.<init>(r3, r0)     // Catch:{ all -> 0x001c }
            r4.zzc(r5)     // Catch:{ all -> 0x001c }
        L_0x0098:
            monitor-exit(r3)     // Catch:{ all -> 0x001c }
            return
        L_0x009a:
            monitor-exit(r3)     // Catch:{ all -> 0x001c }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzef.onServiceConnected(android.content.ComponentName, android.os.IBinder):void");
    }

    @MainThread
    public final void onServiceDisconnected(ComponentName componentName) {
        Preconditions.checkMainThread("MeasurementServiceConnection.onServiceDisconnected");
        this.zzasg.zzgo().zzjk().zzbx("Service disconnected");
        this.zzasg.zzgn().zzc((Runnable) new zzeh(this, componentName));
    }

    @WorkerThread
    public final void zzlh() {
        this.zzasg.zzaf();
        Context context = this.zzasg.getContext();
        synchronized (this) {
            if (this.zzasm) {
                this.zzasg.zzgo().zzjl().zzbx("Connection attempt already in progress");
            } else if (this.zzasn == null || (zzn.zzia() && !this.zzasn.isConnecting() && !this.zzasn.isConnected())) {
                this.zzasn = new zzao(context, Looper.getMainLooper(), this, this);
                this.zzasg.zzgo().zzjl().zzbx("Connecting to remote service");
                this.zzasm = true;
                this.zzasn.checkAvailabilityAndConnect();
            } else {
                this.zzasg.zzgo().zzjl().zzbx("Already awaiting connection attempt");
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        r3.zzasn = null;
        r3.zzasm = false;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0028 */
    @android.support.annotation.MainThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void onConnected(@android.support.annotation.Nullable android.os.Bundle r4) {
        /*
            r3 = this;
            java.lang.String r4 = "MeasurementServiceConnection.onConnected"
            com.google.android.gms.common.internal.Preconditions.checkMainThread(r4)
            monitor-enter(r3)
            r4 = 0
            com.google.android.gms.measurement.internal.zzao r0 = r3.zzasn     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            android.os.IInterface r0 = r0.getService()     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            com.google.android.gms.measurement.internal.zzag r0 = (com.google.android.gms.measurement.internal.zzag) r0     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            boolean r1 = com.google.android.gms.measurement.internal.zzn.zzia()     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            if (r1 != 0) goto L_0x0017
            r3.zzasn = r4     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
        L_0x0017:
            com.google.android.gms.measurement.internal.zzdr r1 = r3.zzasg     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            com.google.android.gms.measurement.internal.zzbo r1 = r1.zzgn()     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            com.google.android.gms.measurement.internal.zzei r2 = new com.google.android.gms.measurement.internal.zzei     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            r2.<init>(r3, r0)     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            r1.zzc(r2)     // Catch:{ DeadObjectException | IllegalStateException -> 0x0028 }
            goto L_0x002d
        L_0x0026:
            r4 = move-exception
            goto L_0x002f
        L_0x0028:
            r3.zzasn = r4     // Catch:{ all -> 0x0026 }
            r4 = 0
            r3.zzasm = r4     // Catch:{ all -> 0x0026 }
        L_0x002d:
            monitor-exit(r3)     // Catch:{ all -> 0x0026 }
            return
        L_0x002f:
            monitor-exit(r3)     // Catch:{ all -> 0x0026 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzef.onConnected(android.os.Bundle):void");
    }

    @MainThread
    public final void onConnectionSuspended(int i) {
        Preconditions.checkMainThread("MeasurementServiceConnection.onConnectionSuspended");
        this.zzasg.zzgo().zzjk().zzbx("Service connection suspended");
        this.zzasg.zzgn().zzc((Runnable) new zzej(this));
    }

    @MainThread
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Preconditions.checkMainThread("MeasurementServiceConnection.onConnectionFailed");
        zzap zzkf = this.zzasg.zzadj.zzkf();
        if (zzkf != null) {
            zzkf.zzjg().zzg("Service connection failed", connectionResult);
        }
        synchronized (this) {
            this.zzasm = false;
            this.zzasn = null;
        }
        this.zzasg.zzgn().zzc((Runnable) new zzek(this));
    }
}
