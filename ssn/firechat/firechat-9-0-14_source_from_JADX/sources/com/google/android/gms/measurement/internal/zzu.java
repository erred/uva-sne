package com.google.android.gms.measurement.internal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class zzu {
    @WorkerThread
    private static Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
        HashSet hashSet = new HashSet();
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 22);
        sb.append("SELECT * FROM ");
        sb.append(str);
        sb.append(" LIMIT 0");
        Cursor rawQuery = sQLiteDatabase.rawQuery(sb.toString(), null);
        try {
            Collections.addAll(hashSet, rawQuery.getColumnNames());
            return hashSet;
        } finally {
            rawQuery.close();
        }
    }

    @WorkerThread
    static void zza(zzap zzap, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String[] strArr) throws SQLiteException {
        String[] split;
        if (zzap == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(zzap, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        if (zzap == null) {
            try {
                throw new IllegalArgumentException("Monitor must not be null");
            } catch (SQLiteException e) {
                zzap.zzjd().zzg("Failed to verify columns on table that was just created", str);
                throw e;
            }
        } else {
            Set zzb = zzb(sQLiteDatabase, str);
            for (String str4 : str3.split(",")) {
                if (!zzb.remove(str4)) {
                    StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 35 + String.valueOf(str4).length());
                    sb.append("Table ");
                    sb.append(str);
                    sb.append(" is missing required column: ");
                    sb.append(str4);
                    throw new SQLiteException(sb.toString());
                }
            }
            if (strArr != null) {
                for (int i = 0; i < strArr.length; i += 2) {
                    if (!zzb.remove(strArr[i])) {
                        sQLiteDatabase.execSQL(strArr[i + 1]);
                    }
                }
            }
            if (!zzb.isEmpty()) {
                zzap.zzjg().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0049  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean zza(com.google.android.gms.measurement.internal.zzap r11, android.database.sqlite.SQLiteDatabase r12, java.lang.String r13) {
        /*
            if (r11 != 0) goto L_0x000a
            java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
            java.lang.String r12 = "Monitor must not be null"
            r11.<init>(r12)
            throw r11
        L_0x000a:
            r0 = 0
            r1 = 0
            java.lang.String r3 = "SQLITE_MASTER"
            r2 = 1
            java.lang.String[] r4 = new java.lang.String[r2]     // Catch:{ SQLiteException -> 0x0037 }
            java.lang.String r5 = "name"
            r4[r1] = r5     // Catch:{ SQLiteException -> 0x0037 }
            java.lang.String r5 = "name=?"
            java.lang.String[] r6 = new java.lang.String[r2]     // Catch:{ SQLiteException -> 0x0037 }
            r6[r1] = r13     // Catch:{ SQLiteException -> 0x0037 }
            r7 = 0
            r8 = 0
            r9 = 0
            r2 = r12
            android.database.Cursor r12 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ SQLiteException -> 0x0037 }
            boolean r0 = r12.moveToFirst()     // Catch:{ SQLiteException -> 0x0030, all -> 0x002d }
            if (r12 == 0) goto L_0x002c
            r12.close()
        L_0x002c:
            return r0
        L_0x002d:
            r11 = move-exception
            r0 = r12
            goto L_0x0047
        L_0x0030:
            r0 = move-exception
            r10 = r0
            r0 = r12
            r12 = r10
            goto L_0x0038
        L_0x0035:
            r11 = move-exception
            goto L_0x0047
        L_0x0037:
            r12 = move-exception
        L_0x0038:
            com.google.android.gms.measurement.internal.zzar r11 = r11.zzjg()     // Catch:{ all -> 0x0035 }
            java.lang.String r2 = "Error querying for table"
            r11.zze(r2, r13, r12)     // Catch:{ all -> 0x0035 }
            if (r0 == 0) goto L_0x0046
            r0.close()
        L_0x0046:
            return r1
        L_0x0047:
            if (r0 == 0) goto L_0x004c
            r0.close()
        L_0x004c:
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzu.zza(com.google.android.gms.measurement.internal.zzap, android.database.sqlite.SQLiteDatabase, java.lang.String):boolean");
    }

    static void zza(zzap zzap, SQLiteDatabase sQLiteDatabase) {
        if (zzap == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        File file = new File(sQLiteDatabase.getPath());
        if (!file.setReadable(false, false)) {
            zzap.zzjg().zzbx("Failed to turn off database read permission");
        }
        if (!file.setWritable(false, false)) {
            zzap.zzjg().zzbx("Failed to turn off database write permission");
        }
        if (!file.setReadable(true, true)) {
            zzap.zzjg().zzbx("Failed to turn on database read permission for owner");
        }
        if (!file.setWritable(true, true)) {
            zzap.zzjg().zzbx("Failed to turn on database write permission for owner");
        }
    }
}
