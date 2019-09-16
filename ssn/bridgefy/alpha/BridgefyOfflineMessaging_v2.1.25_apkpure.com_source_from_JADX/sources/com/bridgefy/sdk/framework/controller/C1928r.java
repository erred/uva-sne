package com.bridgefy.sdk.framework.controller;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Device;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import p000a.p013b.C0159b;
import p000a.p013b.p019d.C0177a;

/* renamed from: com.bridgefy.sdk.framework.controller.r */
class C1928r {

    /* renamed from: a */
    static long f5998a = 0;

    /* renamed from: b */
    private static HashMap<String, C1908e> f5999b = new HashMap<>();

    /* renamed from: c */
    private static C1932t f6000c;

    /* renamed from: a */
    static C1932t m8006a() {
        return f6000c;
    }

    /* renamed from: a */
    static void m8009a(C1932t tVar) {
        f6000c = tVar;
    }

    /* renamed from: a */
    static synchronized C1932t m8007a(Device device) {
        synchronized (C1928r.class) {
            if (SessionManager.getSession(device.getDeviceAddress()) != null) {
                Log.d("ConnectionManager", "getConnectivity: devices is already in session");
                return null;
            } else if (m8006a() != null && m8006a().mo7556b().equals(device)) {
                StringBuilder sb = new StringBuilder();
                sb.append("getConnectivity: already connecting to ");
                sb.append(device.getDeviceAddress());
                Log.d("ConnectionManager", sb.toString());
                return null;
            } else if (m8010a(device.getDeviceAddress())) {
                Log.d("ConnectionManager", "Device is blacklisted, won't connect");
                return null;
            } else {
                switch (device.getAntennaType()) {
                    case BLUETOOTH:
                        m8009a((C1932t) new C1910g(device, false));
                        break;
                    case BLUETOOTH_LE:
                        m8009a((C1932t) new C1915j(device));
                        break;
                }
                C1932t tVar = f6000c;
                return tVar;
            }
        }
    }

    /* renamed from: a */
    static boolean m8010a(String str) {
        C1908e eVar = (C1908e) f5999b.get(str);
        return eVar != null && !eVar.mo7506a();
    }

    /* renamed from: b */
    static void m8012b(Device device) {
        C1908e eVar = (C1908e) f5999b.get(device.getDeviceAddress());
        if (eVar != null) {
            eVar.mo7504a(eVar.mo7507b() + 1);
        } else {
            eVar = new C1908e(false, 1);
        }
        f5999b.put(device.getDeviceAddress(), eVar);
        m8013c(device);
    }

    /* renamed from: c */
    private static void m8013c(Device device) {
        C1908e eVar = (C1908e) f5999b.get(device.getDeviceAddress());
        if (eVar.mo7507b() <= 10) {
            C0159b.m540a((long) (eVar.mo7507b() * 2 * 1000), TimeUnit.MILLISECONDS).mo339a((C0177a) new C0177a(eVar) {
                private final /* synthetic */ C1908e f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C1928r.m8008a(Device.this, this.f$1);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    C1928r.m8014d(Device.this);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: a */
    public static /* synthetic */ void m8008a(Device device, C1908e eVar) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("run: opening device ");
        sb.append(device.getDeviceAddress());
        sb.append(" for retry");
        Log.i("ConnectionManager", sb.toString());
        eVar.mo7505a(true);
        f5999b.put(device.getDeviceAddress(), eVar);
    }

    /* access modifiers changed from: private */
    /* renamed from: d */
    public static /* synthetic */ void m8014d(Device device) {
        if (Bridgefy.getInstance().getBridgefyCore() != null && Bridgefy.getInstance().getBridgefyCore().mo7365d() != null) {
            Log.i("ConnectionManager", "onDeviceLost: ondevicelost");
            Bridgefy.getInstance().getBridgefyCore().mo7365d().onDeviceBlackListed(device);
        }
    }

    /* renamed from: b */
    public static void m8011b() {
        f5999b = new HashMap<>();
    }
}
