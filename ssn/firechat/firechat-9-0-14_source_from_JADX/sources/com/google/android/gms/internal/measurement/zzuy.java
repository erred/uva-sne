package com.google.android.gms.internal.measurement;

final class zzuy {
    private static final Class<?> zzbvi = zzvk();

    private static Class<?> zzvk() {
        try {
            return Class.forName("com.google.protobuf.ExtensionRegistry");
        } catch (ClassNotFoundException unused) {
            return null;
        }
    }

    public static zzuz zzvl() {
        if (zzbvi != null) {
            try {
                return zzfz("getEmptyRegistry");
            } catch (Exception unused) {
            }
        }
        return zzuz.zzbvm;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0014  */
    /* JADX WARNING: Removed duplicated region for block: B:12:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x000e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.google.android.gms.internal.measurement.zzuz zzvm() {
        /*
            java.lang.Class<?> r0 = zzbvi
            if (r0 == 0) goto L_0x000b
            java.lang.String r0 = "loadGeneratedRegistry"
            com.google.android.gms.internal.measurement.zzuz r0 = zzfz(r0)     // Catch:{ Exception -> 0x000b }
            goto L_0x000c
        L_0x000b:
            r0 = 0
        L_0x000c:
            if (r0 != 0) goto L_0x0012
            com.google.android.gms.internal.measurement.zzuz r0 = com.google.android.gms.internal.measurement.zzuz.zzvm()
        L_0x0012:
            if (r0 != 0) goto L_0x0018
            com.google.android.gms.internal.measurement.zzuz r0 = zzvl()
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.measurement.zzuy.zzvm():com.google.android.gms.internal.measurement.zzuz");
    }

    private static final zzuz zzfz(String str) throws Exception {
        return (zzuz) zzbvi.getDeclaredMethod(str, new Class[0]).invoke(null, new Object[0]);
    }
}
