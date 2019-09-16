package com.google.android.gms.measurement.internal;

import android.support.p000v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.measurement.zzfv;
import com.google.android.gms.internal.measurement.zzfw;
import com.google.android.gms.internal.measurement.zzfx;
import com.google.android.gms.internal.measurement.zzfy;
import com.google.android.gms.internal.measurement.zzfz;
import com.google.android.gms.internal.measurement.zzge;
import com.google.android.gms.internal.measurement.zzgg;
import com.google.android.gms.internal.measurement.zzgl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class zzj extends zzez {
    zzj(zzfa zzfa) {
        super(zzfa);
    }

    /* access modifiers changed from: protected */
    public final boolean zzgt() {
        return false;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x02e6  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x038b  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x03e8  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0439  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x01d3  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x02a8  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x02c9  */
    @android.support.annotation.WorkerThread
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final com.google.android.gms.internal.measurement.zzgd[] zza(java.lang.String r89, com.google.android.gms.internal.measurement.zzgf[] r90, com.google.android.gms.internal.measurement.zzgl[] r91) {
        /*
            r88 = this;
            r7 = r88
            r15 = r89
            r13 = r90
            r14 = r91
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r89)
            java.util.HashSet r11 = new java.util.HashSet
            r11.<init>()
            android.support.v4.util.ArrayMap r12 = new android.support.v4.util.ArrayMap
            r12.<init>()
            android.support.v4.util.ArrayMap r10 = new android.support.v4.util.ArrayMap
            r10.<init>()
            android.support.v4.util.ArrayMap r9 = new android.support.v4.util.ArrayMap
            r9.<init>()
            android.support.v4.util.ArrayMap r8 = new android.support.v4.util.ArrayMap
            r8.<init>()
            android.support.v4.util.ArrayMap r6 = new android.support.v4.util.ArrayMap
            r6.<init>()
            com.google.android.gms.measurement.internal.zzn r1 = r88.zzgq()
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Boolean> r2 = com.google.android.gms.measurement.internal.zzaf.zzakw
            boolean r23 = r1.zzd(r15, r2)
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            java.util.Map r1 = r1.zzbo(r15)
            if (r1 == 0) goto L_0x0187
            java.util.Set r2 = r1.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0045:
            boolean r16 = r2.hasNext()
            if (r16 == 0) goto L_0x0187
            java.lang.Object r16 = r2.next()
            r4 = r16
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            java.lang.Object r5 = r1.get(r5)
            com.google.android.gms.internal.measurement.zzgj r5 = (com.google.android.gms.internal.measurement.zzgj) r5
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            java.lang.Object r3 = r10.get(r3)
            java.util.BitSet r3 = (java.util.BitSet) r3
            r27 = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r4)
            java.lang.Object r1 = r9.get(r1)
            java.util.BitSet r1 = (java.util.BitSet) r1
            if (r23 == 0) goto L_0x00b5
            r28 = r1
            android.support.v4.util.ArrayMap r1 = new android.support.v4.util.ArrayMap
            r1.<init>()
            if (r5 == 0) goto L_0x00a9
            r29 = r2
            com.google.android.gms.internal.measurement.zzge[] r2 = r5.zzayg
            if (r2 != 0) goto L_0x0089
            goto L_0x00ab
        L_0x0089:
            com.google.android.gms.internal.measurement.zzge[] r2 = r5.zzayg
            r30 = r11
            int r11 = r2.length
            r14 = 0
        L_0x008f:
            if (r14 >= r11) goto L_0x00ad
            r31 = r11
            r11 = r2[r14]
            r32 = r2
            java.lang.Integer r2 = r11.zzawq
            if (r2 == 0) goto L_0x00a2
            java.lang.Integer r2 = r11.zzawq
            java.lang.Long r11 = r11.zzawr
            r1.put(r2, r11)
        L_0x00a2:
            int r14 = r14 + 1
            r11 = r31
            r2 = r32
            goto L_0x008f
        L_0x00a9:
            r29 = r2
        L_0x00ab:
            r30 = r11
        L_0x00ad:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r8.put(r2, r1)
            goto L_0x00bc
        L_0x00b5:
            r28 = r1
            r29 = r2
            r30 = r11
            r1 = 0
        L_0x00bc:
            if (r3 != 0) goto L_0x00d7
            java.util.BitSet r3 = new java.util.BitSet
            r3.<init>()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r4)
            r10.put(r2, r3)
            java.util.BitSet r2 = new java.util.BitSet
            r2.<init>()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r4)
            r9.put(r11, r2)
            goto L_0x00d9
        L_0x00d7:
            r2 = r28
        L_0x00d9:
            r11 = 0
        L_0x00da:
            long[] r14 = r5.zzaye
            int r14 = r14.length
            int r14 = r14 << 6
            if (r11 >= r14) goto L_0x012f
            long[] r14 = r5.zzaye
            boolean r14 = com.google.android.gms.measurement.internal.zzfg.zza(r14, r11)
            if (r14 == 0) goto L_0x0114
            com.google.android.gms.measurement.internal.zzap r14 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r14 = r14.zzjl()
            r33 = r8
            java.lang.String r8 = "Filter already evaluated. audience ID, filter ID"
            r34 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            r35 = r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r11)
            r14.zze(r8, r9, r10)
            r2.set(r11)
            long[] r8 = r5.zzayf
            boolean r8 = com.google.android.gms.measurement.internal.zzfg.zza(r8, r11)
            if (r8 == 0) goto L_0x011a
            r3.set(r11)
            r8 = 1
            goto L_0x011b
        L_0x0114:
            r33 = r8
            r34 = r9
            r35 = r10
        L_0x011a:
            r8 = 0
        L_0x011b:
            if (r1 == 0) goto L_0x0126
            if (r8 != 0) goto L_0x0126
            java.lang.Integer r8 = java.lang.Integer.valueOf(r11)
            r1.remove(r8)
        L_0x0126:
            int r11 = r11 + 1
            r8 = r33
            r9 = r34
            r10 = r35
            goto L_0x00da
        L_0x012f:
            r33 = r8
            r34 = r9
            r35 = r10
            com.google.android.gms.internal.measurement.zzgd r8 = new com.google.android.gms.internal.measurement.zzgd
            r8.<init>()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            r12.put(r9, r8)
            r9 = 0
            java.lang.Boolean r10 = java.lang.Boolean.valueOf(r9)
            r8.zzawo = r10
            r8.zzawn = r5
            com.google.android.gms.internal.measurement.zzgj r5 = new com.google.android.gms.internal.measurement.zzgj
            r5.<init>()
            r8.zzawm = r5
            com.google.android.gms.internal.measurement.zzgj r5 = r8.zzawm
            long[] r3 = com.google.android.gms.measurement.internal.zzfg.zza(r3)
            r5.zzayf = r3
            com.google.android.gms.internal.measurement.zzgj r3 = r8.zzawm
            long[] r2 = com.google.android.gms.measurement.internal.zzfg.zza(r2)
            r3.zzaye = r2
            if (r23 == 0) goto L_0x0177
            com.google.android.gms.internal.measurement.zzgj r2 = r8.zzawm
            com.google.android.gms.internal.measurement.zzge[] r1 = zzd(r1)
            r2.zzayg = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r4)
            android.support.v4.util.ArrayMap r2 = new android.support.v4.util.ArrayMap
            r2.<init>()
            r6.put(r1, r2)
        L_0x0177:
            r1 = r27
            r2 = r29
            r11 = r30
            r8 = r33
            r9 = r34
            r10 = r35
            r14 = r91
            goto L_0x0045
        L_0x0187:
            r33 = r8
            r34 = r9
            r35 = r10
            r30 = r11
            if (r13 == 0) goto L_0x07b6
            android.support.v4.util.ArrayMap r10 = new android.support.v4.util.ArrayMap
            r10.<init>()
            int r9 = r13.length
            r1 = 0
            r2 = 0
            r8 = 0
            r16 = 0
        L_0x019c:
            if (r8 >= r9) goto L_0x07b6
            r3 = r13[r8]
            java.lang.String r11 = r3.name
            com.google.android.gms.internal.measurement.zzgg[] r14 = r3.zzawt
            com.google.android.gms.measurement.internal.zzn r4 = r88.zzgq()
            com.google.android.gms.measurement.internal.zzaf$zza<java.lang.Boolean> r5 = com.google.android.gms.measurement.internal.zzaf.zzakq
            boolean r4 = r4.zzd(r15, r5)
            if (r4 == 0) goto L_0x0371
            r88.zzjo()
            java.lang.String r4 = "_eid"
            java.lang.Object r4 = com.google.android.gms.measurement.internal.zzfg.zzb(r3, r4)
            java.lang.Long r4 = (java.lang.Long) r4
            if (r4 == 0) goto L_0x01bf
            r5 = 1
            goto L_0x01c0
        L_0x01bf:
            r5 = 0
        L_0x01c0:
            if (r5 == 0) goto L_0x01ce
            r36 = r6
            java.lang.String r6 = "_ep"
            boolean r6 = r11.equals(r6)
            if (r6 == 0) goto L_0x01d0
            r6 = 1
            goto L_0x01d1
        L_0x01ce:
            r36 = r6
        L_0x01d0:
            r6 = 0
        L_0x01d1:
            if (r6 == 0) goto L_0x0324
            r88.zzjo()
            java.lang.String r5 = "_en"
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzfg.zzb(r3, r5)
            r11 = r5
            java.lang.String r11 = (java.lang.String) r11
            boolean r5 = android.text.TextUtils.isEmpty(r11)
            if (r5 == 0) goto L_0x01f6
            com.google.android.gms.measurement.internal.zzap r3 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()
            java.lang.String r5 = "Extra parameter without an event name. eventId"
            r3.zzg(r5, r4)
            r38 = r36
            goto L_0x030d
        L_0x01f6:
            if (r1 == 0) goto L_0x020b
            if (r2 == 0) goto L_0x020b
            long r5 = r4.longValue()
            long r18 = r2.longValue()
            int r20 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x0207
            goto L_0x020b
        L_0x0207:
            r6 = r1
            r18 = r2
            goto L_0x0233
        L_0x020b:
            com.google.android.gms.measurement.internal.zzq r5 = r88.zzjq()
            android.util.Pair r5 = r5.zza(r15, r4)
            if (r5 == 0) goto L_0x02fe
            java.lang.Object r6 = r5.first
            if (r6 != 0) goto L_0x021b
            goto L_0x02fe
        L_0x021b:
            java.lang.Object r1 = r5.first
            com.google.android.gms.internal.measurement.zzgf r1 = (com.google.android.gms.internal.measurement.zzgf) r1
            java.lang.Object r2 = r5.second
            java.lang.Long r2 = (java.lang.Long) r2
            long r16 = r2.longValue()
            r88.zzjo()
            java.lang.String r2 = "_eid"
            java.lang.Object r2 = com.google.android.gms.measurement.internal.zzfg.zzb(r1, r2)
            java.lang.Long r2 = (java.lang.Long) r2
            goto L_0x0207
        L_0x0233:
            r1 = 1
            long r19 = r16 - r1
            r16 = 0
            int r1 = (r19 > r16 ? 1 : (r19 == r16 ? 0 : -1))
            if (r1 > 0) goto L_0x0282
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            r1.zzaf()
            com.google.android.gms.measurement.internal.zzap r2 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjl()
            java.lang.String r4 = "Clearing complex main event info. appId"
            r2.zzg(r4, r15)
            android.database.sqlite.SQLiteDatabase r2 = r1.getWritableDatabase()     // Catch:{ SQLiteException -> 0x0268 }
            java.lang.String r4 = "delete from main_event_params where app_id=?"
            r37 = r3
            r5 = 1
            java.lang.String[] r3 = new java.lang.String[r5]     // Catch:{ SQLiteException -> 0x0266 }
            r21 = 0
            r3[r21] = r15     // Catch:{ SQLiteException -> 0x0264 }
            r2.execSQL(r4, r3)     // Catch:{ SQLiteException -> 0x0264 }
            goto L_0x027c
        L_0x0264:
            r0 = move-exception
            goto L_0x026e
        L_0x0266:
            r0 = move-exception
            goto L_0x026c
        L_0x0268:
            r0 = move-exception
            r37 = r3
            r5 = 1
        L_0x026c:
            r21 = 0
        L_0x026e:
            r2 = r0
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjd()
            java.lang.String r3 = "Error clearing complex main event"
            r1.zzg(r3, r2)
        L_0x027c:
            r1 = r6
            r38 = r36
            r13 = r37
            goto L_0x029a
        L_0x0282:
            r37 = r3
            r5 = 1
            r21 = 0
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            r2 = r15
            r13 = r37
            r3 = r4
            r4 = r19
            r39 = r6
            r38 = r36
            r1.zza(r2, r3, r4, r6)
            r1 = r39
        L_0x029a:
            com.google.android.gms.internal.measurement.zzgg[] r2 = r1.zzawt
            int r2 = r2.length
            int r3 = r14.length
            int r2 = r2 + r3
            com.google.android.gms.internal.measurement.zzgg[] r2 = new com.google.android.gms.internal.measurement.zzgg[r2]
            com.google.android.gms.internal.measurement.zzgg[] r3 = r1.zzawt
            int r4 = r3.length
            r5 = 0
            r6 = 0
        L_0x02a6:
            if (r5 >= r4) goto L_0x02c5
            r40 = r1
            r1 = r3[r5]
            r88.zzjo()
            r41 = r3
            java.lang.String r3 = r1.name
            com.google.android.gms.internal.measurement.zzgg r3 = com.google.android.gms.measurement.internal.zzfg.zza(r13, r3)
            if (r3 != 0) goto L_0x02be
            int r3 = r6 + 1
            r2[r6] = r1
            r6 = r3
        L_0x02be:
            int r5 = r5 + 1
            r1 = r40
            r3 = r41
            goto L_0x02a6
        L_0x02c5:
            r40 = r1
            if (r6 <= 0) goto L_0x02e6
            int r1 = r14.length
            r3 = 0
        L_0x02cb:
            if (r3 >= r1) goto L_0x02d7
            r4 = r14[r3]
            int r5 = r6 + 1
            r2[r6] = r4
            int r3 = r3 + 1
            r6 = r5
            goto L_0x02cb
        L_0x02d7:
            int r1 = r2.length
            if (r6 != r1) goto L_0x02db
            goto L_0x02e2
        L_0x02db:
            java.lang.Object[] r1 = java.util.Arrays.copyOf(r2, r6)
            r2 = r1
            com.google.android.gms.internal.measurement.zzgg[] r2 = (com.google.android.gms.internal.measurement.zzgg[]) r2
        L_0x02e2:
            r29 = r2
            r5 = r11
            goto L_0x02f6
        L_0x02e6:
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjg()
            java.lang.String r2 = "No unique parameters in main event. eventName"
            r1.zzg(r2, r11)
            r5 = r11
            r29 = r14
        L_0x02f6:
            r26 = r18
            r27 = r19
            r24 = 0
            goto L_0x037f
        L_0x02fe:
            r38 = r36
            com.google.android.gms.measurement.internal.zzap r3 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjd()
            java.lang.String r5 = "Extra parameter without existing main event. eventName, eventId"
            r3.zze(r5, r11, r4)
        L_0x030d:
            r47 = r10
            r62 = r12
            r3 = r15
            r10 = r30
            r63 = r33
            r56 = r34
            r59 = r35
            r4 = r38
            r24 = 0
            r33 = r8
            r34 = r9
            goto L_0x07a0
        L_0x0324:
            r13 = r3
            r38 = r36
            if (r5 == 0) goto L_0x0374
            r88.zzjo()
            java.lang.String r1 = "_epc"
            r5 = 0
            java.lang.Long r2 = java.lang.Long.valueOf(r5)
            java.lang.Object r1 = com.google.android.gms.measurement.internal.zzfg.zzb(r13, r1)
            if (r1 != 0) goto L_0x033b
            r1 = r2
        L_0x033b:
            java.lang.Long r1 = (java.lang.Long) r1
            long r16 = r1.longValue()
            int r1 = (r16 > r5 ? 1 : (r16 == r5 ? 0 : -1))
            if (r1 > 0) goto L_0x0357
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjg()
            java.lang.String r2 = "Complex event with zero extra param count. eventName"
            r1.zzg(r2, r11)
            r18 = r4
            r24 = r5
            goto L_0x0367
        L_0x0357:
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            r2 = r15
            r3 = r4
            r18 = r4
            r24 = r5
            r4 = r16
            r6 = r13
            r1.zza(r2, r3, r4, r6)
        L_0x0367:
            r5 = r11
            r40 = r13
            r29 = r14
            r27 = r16
            r26 = r18
            goto L_0x037f
        L_0x0371:
            r13 = r3
            r38 = r6
        L_0x0374:
            r24 = 0
            r40 = r1
            r26 = r2
            r5 = r11
            r29 = r14
            r27 = r16
        L_0x037f:
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            java.lang.String r2 = r13.name
            com.google.android.gms.measurement.internal.zzz r1 = r1.zzg(r15, r2)
            if (r1 != 0) goto L_0x03e8
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjg()
            java.lang.String r2 = "Event aggregate wasn't created during raw event logging. appId, event"
            java.lang.Object r3 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)
            com.google.android.gms.measurement.internal.zzan r4 = r88.zzgl()
            java.lang.String r4 = r4.zzbs(r5)
            r1.zze(r2, r3, r4)
            com.google.android.gms.measurement.internal.zzz r1 = new com.google.android.gms.measurement.internal.zzz
            java.lang.String r2 = r13.name
            r3 = 1
            r16 = 1
            java.lang.Long r6 = r13.zzawu
            long r18 = r6.longValue()
            r20 = 0
            r6 = 0
            r22 = 0
            r31 = 0
            r32 = 0
            r14 = r33
            r33 = r8
            r8 = r1
            r11 = r34
            r34 = r9
            r9 = r15
            r43 = r10
            r42 = r35
            r10 = r2
            r45 = r11
            r44 = r12
            r2 = r30
            r11 = r3
            r46 = r13
            r3 = r14
            r4 = r91
            r13 = r16
            r15 = r18
            r17 = r20
            r19 = r6
            r20 = r22
            r21 = r31
            r22 = r32
            r8.<init>(r9, r10, r11, r13, r15, r17, r19, r20, r21, r22)
            goto L_0x0400
        L_0x03e8:
            r43 = r10
            r44 = r12
            r46 = r13
            r2 = r30
            r3 = r33
            r45 = r34
            r42 = r35
            r4 = r91
            r33 = r8
            r34 = r9
            com.google.android.gms.measurement.internal.zzz r1 = r1.zziu()
        L_0x0400:
            com.google.android.gms.measurement.internal.zzq r6 = r88.zzjq()
            r6.zza(r1)
            long r8 = r1.zzaie
            r10 = r43
            java.lang.Object r1 = r10.get(r5)
            java.util.Map r1 = (java.util.Map) r1
            if (r1 != 0) goto L_0x0428
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            r11 = r89
            java.util.Map r1 = r1.zzl(r11, r5)
            if (r1 != 0) goto L_0x0424
            android.support.v4.util.ArrayMap r1 = new android.support.v4.util.ArrayMap
            r1.<init>()
        L_0x0424:
            r10.put(r5, r1)
            goto L_0x042a
        L_0x0428:
            r11 = r89
        L_0x042a:
            r12 = r1
            java.util.Set r1 = r12.keySet()
            java.util.Iterator r13 = r1.iterator()
        L_0x0433:
            boolean r1 = r13.hasNext()
            if (r1 == 0) goto L_0x078c
            java.lang.Object r1 = r13.next()
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r14 = r1.intValue()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            boolean r1 = r2.contains(r1)
            if (r1 == 0) goto L_0x045f
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r6 = "Skipping failed audience ID"
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
            r1.zzg(r6, r14)
            goto L_0x0433
        L_0x045f:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            r15 = r44
            java.lang.Object r1 = r15.get(r1)
            com.google.android.gms.internal.measurement.zzgd r1 = (com.google.android.gms.internal.measurement.zzgd) r1
            java.lang.Integer r6 = java.lang.Integer.valueOf(r14)
            r47 = r10
            r10 = r42
            java.lang.Object r6 = r10.get(r6)
            java.util.BitSet r6 = (java.util.BitSet) r6
            r48 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r49 = r13
            r13 = r45
            java.lang.Object r2 = r13.get(r2)
            java.util.BitSet r2 = (java.util.BitSet) r2
            if (r23 == 0) goto L_0x04a6
            r50 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            java.lang.Object r2 = r3.get(r2)
            java.util.Map r2 = (java.util.Map) r2
            r51 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r11 = r38
            java.lang.Object r2 = r11.get(r2)
            java.util.Map r2 = (java.util.Map) r2
            goto L_0x04ad
        L_0x04a6:
            r50 = r2
            r11 = r38
            r2 = 0
            r51 = 0
        L_0x04ad:
            if (r1 != 0) goto L_0x0512
            com.google.android.gms.internal.measurement.zzgd r1 = new com.google.android.gms.internal.measurement.zzgd
            r1.<init>()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r14)
            r15.put(r6, r1)
            r52 = r2
            r6 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r6)
            r1.zzawo = r2
            java.util.BitSet r1 = new java.util.BitSet
            r1.<init>()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r10.put(r2, r1)
            java.util.BitSet r2 = new java.util.BitSet
            r2.<init>()
            java.lang.Integer r6 = java.lang.Integer.valueOf(r14)
            r13.put(r6, r2)
            if (r23 == 0) goto L_0x0501
            android.support.v4.util.ArrayMap r6 = new android.support.v4.util.ArrayMap
            r6.<init>()
            r53 = r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            r3.put(r1, r6)
            android.support.v4.util.ArrayMap r1 = new android.support.v4.util.ArrayMap
            r1.<init>()
            r54 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r11.put(r2, r1)
            r55 = r11
            r56 = r13
            r13 = r1
            r11 = r6
            goto L_0x050d
        L_0x0501:
            r53 = r1
            r54 = r2
            r55 = r11
            r56 = r13
            r11 = r51
            r13 = r52
        L_0x050d:
            r6 = r53
            r1 = r54
            goto L_0x051e
        L_0x0512:
            r52 = r2
            r55 = r11
            r56 = r13
            r1 = r50
            r11 = r51
            r13 = r52
        L_0x051e:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            java.lang.Object r2 = r12.get(r2)
            java.util.List r2 = (java.util.List) r2
            java.util.Iterator r2 = r2.iterator()
        L_0x052c:
            boolean r16 = r2.hasNext()
            if (r16 == 0) goto L_0x0776
            java.lang.Object r16 = r2.next()
            r57 = r12
            r12 = r16
            com.google.android.gms.internal.measurement.zzfv r12 = (com.google.android.gms.internal.measurement.zzfv) r12
            r58 = r1
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            r59 = r10
            r10 = 2
            boolean r1 = r1.isLoggable(r10)
            if (r1 == 0) goto L_0x0584
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r10 = "Evaluating filter. audience, filter, event"
            r60 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r14)
            r61 = r3
            java.lang.Integer r3 = r12.zzave
            com.google.android.gms.measurement.internal.zzan r4 = r88.zzgl()
            r62 = r15
            java.lang.String r15 = r12.zzavf
            java.lang.String r4 = r4.zzbs(r15)
            r1.zzd(r10, r2, r3, r4)
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Filter definition"
            com.google.android.gms.measurement.internal.zzfg r3 = r88.zzjo()
            java.lang.String r3 = r3.zza(r12)
            r1.zzg(r2, r3)
            goto L_0x058a
        L_0x0584:
            r60 = r2
            r61 = r3
            r62 = r15
        L_0x058a:
            java.lang.Integer r1 = r12.zzave
            if (r1 == 0) goto L_0x0730
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            r10 = 256(0x100, float:3.59E-43)
            if (r1 <= r10) goto L_0x059a
            goto L_0x0730
        L_0x059a:
            if (r23 == 0) goto L_0x06a5
            if (r12 == 0) goto L_0x05ac
            java.lang.Boolean r1 = r12.zzavb
            if (r1 == 0) goto L_0x05ac
            java.lang.Boolean r1 = r12.zzavb
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x05ac
            r15 = 1
            goto L_0x05ad
        L_0x05ac:
            r15 = 0
        L_0x05ad:
            if (r12 == 0) goto L_0x05be
            java.lang.Boolean r1 = r12.zzavc
            if (r1 == 0) goto L_0x05be
            java.lang.Boolean r1 = r12.zzavc
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x05be
            r16 = 1
            goto L_0x05c0
        L_0x05be:
            r16 = 0
        L_0x05c0:
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            boolean r1 = r6.get(r1)
            if (r1 == 0) goto L_0x05f1
            if (r15 != 0) goto L_0x05f1
            if (r16 != 0) goto L_0x05f1
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Event filter already evaluated true and it is not associated with a dynamic audience. audience ID, filter ID"
            java.lang.Integer r3 = java.lang.Integer.valueOf(r14)
            java.lang.Integer r4 = r12.zzave
            r1.zze(r2, r3, r4)
            r12 = r57
            r1 = r58
            r10 = r59
            r2 = r60
            r3 = r61
            r15 = r62
            goto L_0x0772
        L_0x05f1:
            r4 = r58
            r1 = r7
            r3 = r48
            r17 = r60
            r10 = 1
            r2 = r12
            r10 = r3
            r63 = r61
            r3 = r5
            r64 = r11
            r11 = r4
            r4 = r29
            r18 = r5
            r65 = r13
            r13 = r6
            r5 = r8
            java.lang.Boolean r1 = r1.zza(r2, r3, r4, r5)
            com.google.android.gms.measurement.internal.zzap r2 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjl()
            java.lang.String r3 = "Event filter result"
            if (r1 != 0) goto L_0x061c
            java.lang.String r4 = "null"
            goto L_0x061d
        L_0x061c:
            r4 = r1
        L_0x061d:
            r2.zzg(r3, r4)
            if (r1 != 0) goto L_0x063f
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            r10.add(r1)
        L_0x0629:
            r48 = r10
            r1 = r11
            r6 = r13
        L_0x062d:
            r2 = r17
            r5 = r18
            r12 = r57
            r10 = r59
            r15 = r62
            r3 = r63
            r11 = r64
            r13 = r65
            goto L_0x0772
        L_0x063f:
            java.lang.Integer r2 = r12.zzave
            int r2 = r2.intValue()
            r11.set(r2)
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0629
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            r13.set(r1)
            if (r15 != 0) goto L_0x065b
            if (r16 == 0) goto L_0x0629
        L_0x065b:
            r15 = r46
            java.lang.Long r1 = r15.zzawu
            if (r1 == 0) goto L_0x069e
            if (r16 == 0) goto L_0x068a
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            java.lang.Long r2 = r15.zzawu
            long r2 = r2.longValue()
            r5 = r65
            zzb(r5, r1, r2)
            r48 = r10
            r1 = r11
            r6 = r13
            r46 = r15
            r2 = r17
            r12 = r57
            r10 = r59
            r15 = r62
            r3 = r63
            r11 = r64
            r4 = r91
            goto L_0x06e6
        L_0x068a:
            r5 = r65
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            java.lang.Long r2 = r15.zzawu
            long r2 = r2.longValue()
            r6 = r64
            zza(r6, r1, r2)
            goto L_0x06d3
        L_0x069e:
            r48 = r10
            r1 = r11
            r6 = r13
            r46 = r15
            goto L_0x062d
        L_0x06a5:
            r18 = r5
            r5 = r13
            r15 = r46
            r10 = r48
            r17 = r60
            r63 = r61
            r13 = r6
            r6 = r11
            r11 = r58
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            boolean r1 = r13.get(r1)
            if (r1 == 0) goto L_0x06eb
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r2 = "Event filter already evaluated true. audience ID, filter ID"
            java.lang.Integer r3 = java.lang.Integer.valueOf(r14)
            java.lang.Integer r4 = r12.zzave
            r1.zze(r2, r3, r4)
        L_0x06d3:
            r48 = r10
            r1 = r11
            r46 = r15
            r2 = r17
            r12 = r57
            r10 = r59
            r15 = r62
            r3 = r63
            r4 = r91
            r11 = r6
            r6 = r13
        L_0x06e6:
            r13 = r5
            r5 = r18
            goto L_0x052c
        L_0x06eb:
            r1 = r7
            r2 = r12
            r3 = r18
            r4 = r29
            r52 = r5
            r51 = r6
            r5 = r8
            java.lang.Boolean r1 = r1.zza(r2, r3, r4, r5)
            com.google.android.gms.measurement.internal.zzap r2 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r2 = r2.zzjl()
            java.lang.String r3 = "Event filter result"
            if (r1 != 0) goto L_0x0709
            java.lang.String r4 = "null"
            goto L_0x070a
        L_0x0709:
            r4 = r1
        L_0x070a:
            r2.zzg(r3, r4)
            if (r1 != 0) goto L_0x0717
            java.lang.Integer r1 = java.lang.Integer.valueOf(r14)
            r10.add(r1)
            goto L_0x075c
        L_0x0717:
            java.lang.Integer r2 = r12.zzave
            int r2 = r2.intValue()
            r11.set(r2)
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x075c
            java.lang.Integer r1 = r12.zzave
            int r1 = r1.intValue()
            r13.set(r1)
            goto L_0x075c
        L_0x0730:
            r18 = r5
            r51 = r11
            r52 = r13
            r15 = r46
            r10 = r48
            r11 = r58
            r17 = r60
            r63 = r61
            r13 = r6
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjg()
            java.lang.String r2 = "Invalid event filter ID. appId, id"
            r4 = r55
            r3 = r89
            java.lang.Object r5 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)
            java.lang.Integer r6 = r12.zzave
            java.lang.String r6 = java.lang.String.valueOf(r6)
            r1.zze(r2, r5, r6)
        L_0x075c:
            r48 = r10
            r1 = r11
            r6 = r13
            r46 = r15
            r2 = r17
            r5 = r18
            r11 = r51
            r13 = r52
            r12 = r57
            r10 = r59
            r15 = r62
            r3 = r63
        L_0x0772:
            r4 = r91
            goto L_0x052c
        L_0x0776:
            r62 = r15
            r42 = r10
            r10 = r47
            r2 = r48
            r13 = r49
            r38 = r55
            r45 = r56
            r44 = r62
            r4 = r91
            r11 = r89
            goto L_0x0433
        L_0x078c:
            r63 = r3
            r47 = r10
            r3 = r11
            r4 = r38
            r59 = r42
            r62 = r44
            r56 = r45
            r10 = r2
            r2 = r26
            r16 = r27
            r1 = r40
        L_0x07a0:
            int r8 = r33 + 1
            r13 = r90
            r15 = r3
            r6 = r4
            r30 = r10
            r9 = r34
            r10 = r47
            r34 = r56
            r35 = r59
            r12 = r62
            r33 = r63
            goto L_0x019c
        L_0x07b6:
            r4 = r6
            r62 = r12
            r3 = r15
            r10 = r30
            r63 = r33
            r56 = r34
            r59 = r35
            r1 = r91
            if (r1 == 0) goto L_0x0adc
            android.support.v4.util.ArrayMap r2 = new android.support.v4.util.ArrayMap
            r2.<init>()
            int r5 = r1.length
            r6 = 0
        L_0x07cd:
            if (r6 >= r5) goto L_0x0adc
            r8 = r1[r6]
            java.lang.String r9 = r8.name
            java.lang.Object r9 = r2.get(r9)
            java.util.Map r9 = (java.util.Map) r9
            if (r9 != 0) goto L_0x07f1
            com.google.android.gms.measurement.internal.zzq r9 = r88.zzjq()
            java.lang.String r11 = r8.name
            java.util.Map r9 = r9.zzm(r3, r11)
            if (r9 != 0) goto L_0x07ec
            android.support.v4.util.ArrayMap r9 = new android.support.v4.util.ArrayMap
            r9.<init>()
        L_0x07ec:
            java.lang.String r11 = r8.name
            r2.put(r11, r9)
        L_0x07f1:
            java.util.Set r11 = r9.keySet()
            java.util.Iterator r11 = r11.iterator()
        L_0x07f9:
            boolean r12 = r11.hasNext()
            if (r12 == 0) goto L_0x0ac6
            java.lang.Object r12 = r11.next()
            java.lang.Integer r12 = (java.lang.Integer) r12
            int r12 = r12.intValue()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
            boolean r13 = r10.contains(r13)
            if (r13 == 0) goto L_0x0825
            com.google.android.gms.measurement.internal.zzap r13 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r13 = r13.zzjl()
            java.lang.String r14 = "Skipping failed audience ID"
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r13.zzg(r14, r12)
            goto L_0x07f9
        L_0x0825:
            java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
            r14 = r62
            java.lang.Object r13 = r14.get(r13)
            com.google.android.gms.internal.measurement.zzgd r13 = (com.google.android.gms.internal.measurement.zzgd) r13
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r1 = r59
            java.lang.Object r15 = r1.get(r15)
            java.util.BitSet r15 = (java.util.BitSet) r15
            r66 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r67 = r5
            r5 = r56
            java.lang.Object r2 = r5.get(r2)
            java.util.BitSet r2 = (java.util.BitSet) r2
            if (r23 == 0) goto L_0x086c
            r68 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r69 = r11
            r11 = r63
            java.lang.Object r2 = r11.get(r2)
            java.util.Map r2 = (java.util.Map) r2
            r70 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            java.lang.Object r2 = r4.get(r2)
            java.util.Map r2 = (java.util.Map) r2
            goto L_0x0875
        L_0x086c:
            r68 = r2
            r69 = r11
            r11 = r63
            r2 = 0
            r70 = 0
        L_0x0875:
            if (r13 != 0) goto L_0x08da
            com.google.android.gms.internal.measurement.zzgd r13 = new com.google.android.gms.internal.measurement.zzgd
            r13.<init>()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r14.put(r15, r13)
            r71 = r2
            r15 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r15)
            r13.zzawo = r2
            java.util.BitSet r2 = new java.util.BitSet
            r2.<init>()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
            r1.put(r13, r2)
            java.util.BitSet r13 = new java.util.BitSet
            r13.<init>()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r12)
            r5.put(r15, r13)
            if (r23 == 0) goto L_0x08cb
            android.support.v4.util.ArrayMap r15 = new android.support.v4.util.ArrayMap
            r15.<init>()
            r72 = r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r12)
            r11.put(r2, r15)
            android.support.v4.util.ArrayMap r2 = new android.support.v4.util.ArrayMap
            r2.<init>()
            r73 = r13
            java.lang.Integer r13 = java.lang.Integer.valueOf(r12)
            r4.put(r13, r2)
            r74 = r4
            r75 = r11
            r13 = r73
            r4 = r2
            r2 = r15
            goto L_0x08d7
        L_0x08cb:
            r72 = r2
            r73 = r13
            r74 = r4
            r75 = r11
            r2 = r70
            r4 = r71
        L_0x08d7:
            r15 = r72
            goto L_0x08e6
        L_0x08da:
            r71 = r2
            r74 = r4
            r75 = r11
            r13 = r68
            r2 = r70
            r4 = r71
        L_0x08e6:
            java.lang.Integer r11 = java.lang.Integer.valueOf(r12)
            java.lang.Object r11 = r9.get(r11)
            java.util.List r11 = (java.util.List) r11
            java.util.Iterator r11 = r11.iterator()
        L_0x08f4:
            boolean r16 = r11.hasNext()
            if (r16 == 0) goto L_0x0ab2
            java.lang.Object r16 = r11.next()
            r76 = r9
            r9 = r16
            com.google.android.gms.internal.measurement.zzfy r9 = (com.google.android.gms.internal.measurement.zzfy) r9
            r77 = r11
            com.google.android.gms.measurement.internal.zzap r11 = r88.zzgo()
            r78 = r5
            r5 = 2
            boolean r11 = r11.isLoggable(r5)
            if (r11 == 0) goto L_0x094c
            com.google.android.gms.measurement.internal.zzap r11 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r11 = r11.zzjl()
            java.lang.String r5 = "Evaluating filter. audience, filter, property"
            r79 = r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r12)
            r80 = r1
            java.lang.Integer r1 = r9.zzave
            r81 = r6
            com.google.android.gms.measurement.internal.zzan r6 = r88.zzgl()
            java.lang.String r3 = r9.zzavu
            java.lang.String r3 = r6.zzbu(r3)
            r11.zzd(r5, r14, r1, r3)
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r3 = "Filter definition"
            com.google.android.gms.measurement.internal.zzfg r5 = r88.zzjo()
            java.lang.String r5 = r5.zza(r9)
            r1.zzg(r3, r5)
            goto L_0x0952
        L_0x094c:
            r80 = r1
            r81 = r6
            r79 = r14
        L_0x0952:
            java.lang.Integer r1 = r9.zzave
            if (r1 == 0) goto L_0x0a7d
            java.lang.Integer r1 = r9.zzave
            int r1 = r1.intValue()
            r3 = 256(0x100, float:3.59E-43)
            if (r1 <= r3) goto L_0x0962
            goto L_0x0a7d
        L_0x0962:
            if (r23 == 0) goto L_0x0a14
            if (r9 == 0) goto L_0x0974
            java.lang.Boolean r1 = r9.zzavb
            if (r1 == 0) goto L_0x0974
            java.lang.Boolean r1 = r9.zzavb
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0974
            r1 = 1
            goto L_0x0975
        L_0x0974:
            r1 = 0
        L_0x0975:
            if (r9 == 0) goto L_0x0985
            java.lang.Boolean r5 = r9.zzavc
            if (r5 == 0) goto L_0x0985
            java.lang.Boolean r5 = r9.zzavc
            boolean r5 = r5.booleanValue()
            if (r5 == 0) goto L_0x0985
            r5 = 1
            goto L_0x0986
        L_0x0985:
            r5 = 0
        L_0x0986:
            java.lang.Integer r6 = r9.zzave
            int r6 = r6.intValue()
            boolean r6 = r15.get(r6)
            if (r6 == 0) goto L_0x09ab
            if (r1 != 0) goto L_0x09ab
            if (r5 != 0) goto L_0x09ab
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r5 = "Property filter already evaluated true and it is not associated with a dynamic audience. audience ID, filter ID"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r12)
            java.lang.Integer r9 = r9.zzave
            r1.zze(r5, r6, r9)
            goto L_0x0a33
        L_0x09ab:
            java.lang.Boolean r6 = r7.zza(r9, r8)
            com.google.android.gms.measurement.internal.zzap r11 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r11 = r11.zzjl()
            java.lang.String r14 = "Property filter result"
            if (r6 != 0) goto L_0x09c0
            java.lang.String r16 = "null"
            r3 = r16
            goto L_0x09c1
        L_0x09c0:
            r3 = r6
        L_0x09c1:
            r11.zzg(r14, r3)
            if (r6 != 0) goto L_0x09ce
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r10.add(r1)
            goto L_0x0a33
        L_0x09ce:
            java.lang.Integer r3 = r9.zzave
            int r3 = r3.intValue()
            r13.set(r3)
            java.lang.Integer r3 = r9.zzave
            int r3 = r3.intValue()
            boolean r11 = r6.booleanValue()
            r15.set(r3, r11)
            boolean r3 = r6.booleanValue()
            if (r3 == 0) goto L_0x0a33
            if (r1 != 0) goto L_0x09ee
            if (r5 == 0) goto L_0x0a33
        L_0x09ee:
            java.lang.Long r1 = r8.zzayl
            if (r1 == 0) goto L_0x0a33
            if (r5 == 0) goto L_0x0a04
            java.lang.Integer r1 = r9.zzave
            int r1 = r1.intValue()
            java.lang.Long r3 = r8.zzayl
            long r5 = r3.longValue()
            zzb(r4, r1, r5)
            goto L_0x0a33
        L_0x0a04:
            java.lang.Integer r1 = r9.zzave
            int r1 = r1.intValue()
            java.lang.Long r3 = r8.zzayl
            long r5 = r3.longValue()
            zza(r2, r1, r5)
            goto L_0x0a33
        L_0x0a14:
            java.lang.Integer r1 = r9.zzave
            int r1 = r1.intValue()
            boolean r1 = r15.get(r1)
            if (r1 == 0) goto L_0x0a43
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjl()
            java.lang.String r3 = "Property filter already evaluated true. audience ID, filter ID"
            java.lang.Integer r5 = java.lang.Integer.valueOf(r12)
            java.lang.Integer r6 = r9.zzave
            r1.zze(r3, r5, r6)
        L_0x0a33:
            r9 = r76
            r11 = r77
            r5 = r78
            r14 = r79
            r1 = r80
            r6 = r81
            r3 = r89
            goto L_0x08f4
        L_0x0a43:
            java.lang.Boolean r1 = r7.zza(r9, r8)
            com.google.android.gms.measurement.internal.zzap r3 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r3 = r3.zzjl()
            java.lang.String r5 = "Property filter result"
            if (r1 != 0) goto L_0x0a56
            java.lang.String r6 = "null"
            goto L_0x0a57
        L_0x0a56:
            r6 = r1
        L_0x0a57:
            r3.zzg(r5, r6)
            if (r1 != 0) goto L_0x0a64
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r10.add(r1)
            goto L_0x0a33
        L_0x0a64:
            java.lang.Integer r3 = r9.zzave
            int r3 = r3.intValue()
            r13.set(r3)
            boolean r1 = r1.booleanValue()
            if (r1 == 0) goto L_0x0a33
            java.lang.Integer r1 = r9.zzave
            int r1 = r1.intValue()
            r15.set(r1)
            goto L_0x0a33
        L_0x0a7d:
            com.google.android.gms.measurement.internal.zzap r1 = r88.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjg()
            java.lang.String r2 = "Invalid property filter ID. appId, id"
            r3 = r89
            java.lang.Object r4 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)
            java.lang.Integer r5 = r9.zzave
            java.lang.String r5 = java.lang.String.valueOf(r5)
            r1.zze(r2, r4, r5)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r12)
            r10.add(r1)
            r2 = r66
            r5 = r67
            r11 = r69
            r4 = r74
            r63 = r75
            r9 = r76
            r56 = r78
            r62 = r79
            r59 = r80
            r6 = r81
            goto L_0x0ac2
        L_0x0ab2:
            r59 = r1
            r56 = r5
            r62 = r14
            r2 = r66
            r5 = r67
            r11 = r69
            r4 = r74
            r63 = r75
        L_0x0ac2:
            r1 = r91
            goto L_0x07f9
        L_0x0ac6:
            r66 = r2
            r74 = r4
            r67 = r5
            r81 = r6
            r78 = r56
            r80 = r59
            r79 = r62
            r75 = r63
            int r6 = r81 + 1
            r1 = r91
            goto L_0x07cd
        L_0x0adc:
            r74 = r4
            r78 = r56
            r79 = r62
            r75 = r63
            r1 = r59
            int r2 = r1.size()
            com.google.android.gms.internal.measurement.zzgd[] r2 = new com.google.android.gms.internal.measurement.zzgd[r2]
            java.util.Set r4 = r1.keySet()
            java.util.Iterator r4 = r4.iterator()
            r5 = 0
        L_0x0af5:
            boolean r6 = r4.hasNext()
            if (r6 == 0) goto L_0x0cb8
            java.lang.Object r6 = r4.next()
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
            boolean r8 = r10.contains(r8)
            if (r8 != 0) goto L_0x0cb4
            java.lang.Integer r8 = java.lang.Integer.valueOf(r6)
            r9 = r79
            java.lang.Object r8 = r9.get(r8)
            com.google.android.gms.internal.measurement.zzgd r8 = (com.google.android.gms.internal.measurement.zzgd) r8
            if (r8 != 0) goto L_0x0b22
            com.google.android.gms.internal.measurement.zzgd r8 = new com.google.android.gms.internal.measurement.zzgd
            r8.<init>()
        L_0x0b22:
            int r11 = r5 + 1
            r2[r5] = r8
            java.lang.Integer r5 = java.lang.Integer.valueOf(r6)
            r8.zzauy = r5
            com.google.android.gms.internal.measurement.zzgj r5 = new com.google.android.gms.internal.measurement.zzgj
            r5.<init>()
            r8.zzawm = r5
            com.google.android.gms.internal.measurement.zzgj r5 = r8.zzawm
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            java.lang.Object r12 = r1.get(r12)
            java.util.BitSet r12 = (java.util.BitSet) r12
            long[] r12 = com.google.android.gms.measurement.internal.zzfg.zza(r12)
            r5.zzayf = r12
            com.google.android.gms.internal.measurement.zzgj r5 = r8.zzawm
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            r13 = r78
            java.lang.Object r12 = r13.get(r12)
            java.util.BitSet r12 = (java.util.BitSet) r12
            long[] r12 = com.google.android.gms.measurement.internal.zzfg.zza(r12)
            r5.zzaye = r12
            if (r23 == 0) goto L_0x0c09
            com.google.android.gms.internal.measurement.zzgj r5 = r8.zzawm
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            r14 = r75
            java.lang.Object r12 = r14.get(r12)
            java.util.Map r12 = (java.util.Map) r12
            com.google.android.gms.internal.measurement.zzge[] r12 = zzd(r12)
            r5.zzayg = r12
            com.google.android.gms.internal.measurement.zzgj r5 = r8.zzawm
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            r15 = r74
            java.lang.Object r12 = r15.get(r12)
            java.util.Map r12 = (java.util.Map) r12
            if (r12 != 0) goto L_0x0b8c
            r82 = r1
            r1 = 0
            com.google.android.gms.internal.measurement.zzgk[] r12 = new com.google.android.gms.internal.measurement.zzgk[r1]
            r83 = r4
            r85 = r9
            r86 = r10
            goto L_0x0c06
        L_0x0b8c:
            r82 = r1
            int r1 = r12.size()
            com.google.android.gms.internal.measurement.zzgk[] r1 = new com.google.android.gms.internal.measurement.zzgk[r1]
            r83 = r4
            java.util.Set r4 = r12.keySet()
            java.util.Iterator r4 = r4.iterator()
            r16 = 0
        L_0x0ba0:
            boolean r17 = r4.hasNext()
            if (r17 == 0) goto L_0x0c01
            java.lang.Object r17 = r4.next()
            r84 = r4
            r4 = r17
            java.lang.Integer r4 = (java.lang.Integer) r4
            r85 = r9
            com.google.android.gms.internal.measurement.zzgk r9 = new com.google.android.gms.internal.measurement.zzgk
            r9.<init>()
            r9.zzawq = r4
            java.lang.Object r4 = r12.get(r4)
            java.util.List r4 = (java.util.List) r4
            if (r4 == 0) goto L_0x0bf2
            java.util.Collections.sort(r4)
            r86 = r10
            int r10 = r4.size()
            long[] r10 = new long[r10]
            java.util.Iterator r4 = r4.iterator()
            r17 = 0
        L_0x0bd2:
            boolean r18 = r4.hasNext()
            if (r18 == 0) goto L_0x0bef
            java.lang.Object r18 = r4.next()
            r87 = r4
            r4 = r18
            java.lang.Long r4 = (java.lang.Long) r4
            int r18 = r17 + 1
            long r19 = r4.longValue()
            r10[r17] = r19
            r17 = r18
            r4 = r87
            goto L_0x0bd2
        L_0x0bef:
            r9.zzayj = r10
            goto L_0x0bf4
        L_0x0bf2:
            r86 = r10
        L_0x0bf4:
            int r4 = r16 + 1
            r1[r16] = r9
            r16 = r4
            r4 = r84
            r9 = r85
            r10 = r86
            goto L_0x0ba0
        L_0x0c01:
            r85 = r9
            r86 = r10
            r12 = r1
        L_0x0c06:
            r5.zzayh = r12
            goto L_0x0c15
        L_0x0c09:
            r82 = r1
            r83 = r4
            r85 = r9
            r86 = r10
            r15 = r74
            r14 = r75
        L_0x0c15:
            com.google.android.gms.measurement.internal.zzq r1 = r88.zzjq()
            com.google.android.gms.internal.measurement.zzgj r4 = r8.zzawm
            r1.zzcl()
            r1.zzaf()
            com.google.android.gms.common.internal.Preconditions.checkNotEmpty(r89)
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r4)
            int r5 = r4.zzvu()     // Catch:{ IOException -> 0x0c8e }
            byte[] r5 = new byte[r5]     // Catch:{ IOException -> 0x0c8e }
            int r8 = r5.length     // Catch:{ IOException -> 0x0c8e }
            r9 = 0
            com.google.android.gms.internal.measurement.zzyy r8 = com.google.android.gms.internal.measurement.zzyy.zzk(r5, r9, r8)     // Catch:{ IOException -> 0x0c8c }
            r4.zza(r8)     // Catch:{ IOException -> 0x0c8c }
            r8.zzyt()     // Catch:{ IOException -> 0x0c8c }
            android.content.ContentValues r4 = new android.content.ContentValues
            r4.<init>()
            java.lang.String r8 = "app_id"
            r4.put(r8, r3)
            java.lang.String r8 = "audience_id"
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r4.put(r8, r6)
            java.lang.String r6 = "current_results"
            r4.put(r6, r5)
            android.database.sqlite.SQLiteDatabase r5 = r1.getWritableDatabase()     // Catch:{ SQLiteException -> 0x0c77 }
            java.lang.String r6 = "audience_filter_values"
            r8 = 5
            r10 = 0
            long r4 = r5.insertWithOnConflict(r6, r10, r4, r8)     // Catch:{ SQLiteException -> 0x0c75 }
            r16 = -1
            int r6 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x0ca3
            com.google.android.gms.measurement.internal.zzap r4 = r1.zzgo()     // Catch:{ SQLiteException -> 0x0c75 }
            com.google.android.gms.measurement.internal.zzar r4 = r4.zzjd()     // Catch:{ SQLiteException -> 0x0c75 }
            java.lang.String r5 = "Failed to insert filter results (got -1). appId"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)     // Catch:{ SQLiteException -> 0x0c75 }
            r4.zzg(r5, r6)     // Catch:{ SQLiteException -> 0x0c75 }
            goto L_0x0ca3
        L_0x0c75:
            r0 = move-exception
            goto L_0x0c79
        L_0x0c77:
            r0 = move-exception
            r10 = 0
        L_0x0c79:
            r4 = r0
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjd()
            java.lang.String r5 = "Error storing filter results. appId"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)
            r1.zze(r5, r6, r4)
            goto L_0x0ca3
        L_0x0c8c:
            r0 = move-exception
            goto L_0x0c90
        L_0x0c8e:
            r0 = move-exception
            r9 = 0
        L_0x0c90:
            r10 = 0
            r4 = r0
            com.google.android.gms.measurement.internal.zzap r1 = r1.zzgo()
            com.google.android.gms.measurement.internal.zzar r1 = r1.zzjd()
            java.lang.String r5 = "Configuration loss. Failed to serialize filter results. appId"
            java.lang.Object r6 = com.google.android.gms.measurement.internal.zzap.zzbv(r89)
            r1.zze(r5, r6, r4)
        L_0x0ca3:
            r5 = r11
            r78 = r13
            r75 = r14
            r74 = r15
            r1 = r82
            r4 = r83
            r79 = r85
            r10 = r86
            goto L_0x0af5
        L_0x0cb4:
            r86 = r10
            goto L_0x0af5
        L_0x0cb8:
            java.lang.Object[] r1 = java.util.Arrays.copyOf(r2, r5)
            com.google.android.gms.internal.measurement.zzgd[] r1 = (com.google.android.gms.internal.measurement.zzgd[]) r1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzj.zza(java.lang.String, com.google.android.gms.internal.measurement.zzgf[], com.google.android.gms.internal.measurement.zzgl[]):com.google.android.gms.internal.measurement.zzgd[]");
    }

    private final Boolean zza(zzfv zzfv, String str, zzgg[] zzggArr, long j) {
        zzfw[] zzfwArr;
        zzfw[] zzfwArr2;
        Boolean bool;
        if (zzfv.zzavi != null) {
            Boolean zza = zza(j, zzfv.zzavi);
            if (zza == null) {
                return null;
            }
            if (!zza.booleanValue()) {
                return Boolean.valueOf(false);
            }
        }
        HashSet hashSet = new HashSet();
        for (zzfw zzfw : zzfv.zzavg) {
            if (TextUtils.isEmpty(zzfw.zzavn)) {
                zzgo().zzjg().zzg("null or empty param name in filter. event", zzgl().zzbs(str));
                return null;
            }
            hashSet.add(zzfw.zzavn);
        }
        ArrayMap arrayMap = new ArrayMap();
        for (zzgg zzgg : zzggArr) {
            if (hashSet.contains(zzgg.name)) {
                if (zzgg.zzawx != null) {
                    arrayMap.put(zzgg.name, zzgg.zzawx);
                } else if (zzgg.zzauh != null) {
                    arrayMap.put(zzgg.name, zzgg.zzauh);
                } else if (zzgg.zzamp != null) {
                    arrayMap.put(zzgg.name, zzgg.zzamp);
                } else {
                    zzgo().zzjg().zze("Unknown value for param. event, param", zzgl().zzbs(str), zzgl().zzbt(zzgg.name));
                    return null;
                }
            }
        }
        for (zzfw zzfw2 : zzfv.zzavg) {
            boolean equals = Boolean.TRUE.equals(zzfw2.zzavm);
            String str2 = zzfw2.zzavn;
            if (TextUtils.isEmpty(str2)) {
                zzgo().zzjg().zzg("Event has empty param name. event", zzgl().zzbs(str));
                return null;
            }
            Object obj = arrayMap.get(str2);
            if (obj instanceof Long) {
                if (zzfw2.zzavl == null) {
                    zzgo().zzjg().zze("No number filter for long param. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                    return null;
                }
                Boolean zza2 = zza(((Long) obj).longValue(), zzfw2.zzavl);
                if (zza2 == null) {
                    return null;
                }
                if ((true ^ zza2.booleanValue()) ^ equals) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof Double) {
                if (zzfw2.zzavl == null) {
                    zzgo().zzjg().zze("No number filter for double param. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                    return null;
                }
                Boolean zza3 = zza(((Double) obj).doubleValue(), zzfw2.zzavl);
                if (zza3 == null) {
                    return null;
                }
                if ((true ^ zza3.booleanValue()) ^ equals) {
                    return Boolean.valueOf(false);
                }
            } else if (obj instanceof String) {
                if (zzfw2.zzavk != null) {
                    bool = zza((String) obj, zzfw2.zzavk);
                } else if (zzfw2.zzavl != null) {
                    String str3 = (String) obj;
                    if (zzfg.zzcp(str3)) {
                        bool = zza(str3, zzfw2.zzavl);
                    } else {
                        zzgo().zzjg().zze("Invalid param value for number filter. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                        return null;
                    }
                } else {
                    zzgo().zzjg().zze("No filter for String param. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                    return null;
                }
                if (bool == null) {
                    return null;
                }
                if ((true ^ bool.booleanValue()) ^ equals) {
                    return Boolean.valueOf(false);
                }
            } else if (obj == null) {
                zzgo().zzjl().zze("Missing param for filter. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                return Boolean.valueOf(false);
            } else {
                zzgo().zzjg().zze("Unknown param type. event, param", zzgl().zzbs(str), zzgl().zzbt(str2));
                return null;
            }
        }
        return Boolean.valueOf(true);
    }

    private final Boolean zza(zzfy zzfy, zzgl zzgl) {
        zzfw zzfw = zzfy.zzavv;
        if (zzfw == null) {
            zzgo().zzjg().zzg("Missing property filter. property", zzgl().zzbu(zzgl.name));
            return null;
        }
        boolean equals = Boolean.TRUE.equals(zzfw.zzavm);
        if (zzgl.zzawx != null) {
            if (zzfw.zzavl != null) {
                return zza(zza(zzgl.zzawx.longValue(), zzfw.zzavl), equals);
            }
            zzgo().zzjg().zzg("No number filter for long property. property", zzgl().zzbu(zzgl.name));
            return null;
        } else if (zzgl.zzauh != null) {
            if (zzfw.zzavl != null) {
                return zza(zza(zzgl.zzauh.doubleValue(), zzfw.zzavl), equals);
            }
            zzgo().zzjg().zzg("No number filter for double property. property", zzgl().zzbu(zzgl.name));
            return null;
        } else if (zzgl.zzamp == null) {
            zzgo().zzjg().zzg("User property has no value, property", zzgl().zzbu(zzgl.name));
            return null;
        } else if (zzfw.zzavk != null) {
            return zza(zza(zzgl.zzamp, zzfw.zzavk), equals);
        } else {
            if (zzfw.zzavl == null) {
                zzgo().zzjg().zzg("No string or number filter defined. property", zzgl().zzbu(zzgl.name));
            } else if (zzfg.zzcp(zzgl.zzamp)) {
                return zza(zza(zzgl.zzamp, zzfw.zzavl), equals);
            } else {
                zzgo().zzjg().zze("Invalid user property value for Numeric number filter. property, value", zzgl().zzbu(zzgl.name), zzgl.zzamp);
            }
            return null;
        }
    }

    @VisibleForTesting
    private static Boolean zza(Boolean bool, boolean z) {
        if (bool == null) {
            return null;
        }
        return Boolean.valueOf(bool.booleanValue() ^ z);
    }

    @VisibleForTesting
    private final Boolean zza(String str, zzfz zzfz) {
        String str2;
        List list;
        Preconditions.checkNotNull(zzfz);
        if (str == null || zzfz.zzavw == null || zzfz.zzavw.intValue() == 0) {
            return null;
        }
        if (zzfz.zzavw.intValue() == 6) {
            if (zzfz.zzavz == null || zzfz.zzavz.length == 0) {
                return null;
            }
        } else if (zzfz.zzavx == null) {
            return null;
        }
        int intValue = zzfz.zzavw.intValue();
        boolean z = zzfz.zzavy != null && zzfz.zzavy.booleanValue();
        if (z || intValue == 1 || intValue == 6) {
            str2 = zzfz.zzavx;
        } else {
            str2 = zzfz.zzavx.toUpperCase(Locale.ENGLISH);
        }
        String str3 = str2;
        if (zzfz.zzavz == null) {
            list = null;
        } else {
            String[] strArr = zzfz.zzavz;
            if (z) {
                list = Arrays.asList(strArr);
            } else {
                ArrayList arrayList = new ArrayList();
                for (String upperCase : strArr) {
                    arrayList.add(upperCase.toUpperCase(Locale.ENGLISH));
                }
                list = arrayList;
            }
        }
        return zza(str, intValue, z, str3, list, intValue == 1 ? str3 : null);
    }

    private final Boolean zza(String str, int i, boolean z, String str2, List<String> list, String str3) {
        if (str == null) {
            return null;
        }
        if (i == 6) {
            if (list == null || list.size() == 0) {
                return null;
            }
        } else if (str2 == null) {
            return null;
        }
        if (!z && i != 1) {
            str = str.toUpperCase(Locale.ENGLISH);
        }
        switch (i) {
            case 1:
                try {
                    return Boolean.valueOf(Pattern.compile(str3, z ? 0 : 66).matcher(str).matches());
                } catch (PatternSyntaxException unused) {
                    zzgo().zzjg().zzg("Invalid regular expression in REGEXP audience filter. expression", str3);
                    return null;
                }
            case 2:
                return Boolean.valueOf(str.startsWith(str2));
            case 3:
                return Boolean.valueOf(str.endsWith(str2));
            case 4:
                return Boolean.valueOf(str.contains(str2));
            case 5:
                return Boolean.valueOf(str.equals(str2));
            case 6:
                return Boolean.valueOf(list.contains(str));
            default:
                return null;
        }
    }

    private final Boolean zza(long j, zzfx zzfx) {
        try {
            return zza(new BigDecimal(j), zzfx, 0.0d);
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    private final Boolean zza(double d, zzfx zzfx) {
        try {
            return zza(new BigDecimal(d), zzfx, Math.ulp(d));
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    private final Boolean zza(String str, zzfx zzfx) {
        if (!zzfg.zzcp(str)) {
            return null;
        }
        try {
            return zza(new BigDecimal(str), zzfx, 0.0d);
        } catch (NumberFormatException unused) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0072, code lost:
        if (r3 != null) goto L_0x0074;
     */
    @com.google.android.gms.common.util.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Boolean zza(java.math.BigDecimal r7, com.google.android.gms.internal.measurement.zzfx r8, double r9) {
        /*
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r8)
            java.lang.Integer r0 = r8.zzavo
            r1 = 0
            if (r0 == 0) goto L_0x00f0
            java.lang.Integer r0 = r8.zzavo
            int r0 = r0.intValue()
            if (r0 != 0) goto L_0x0012
            goto L_0x00f0
        L_0x0012:
            java.lang.Integer r0 = r8.zzavo
            int r0 = r0.intValue()
            r2 = 4
            if (r0 != r2) goto L_0x0024
            java.lang.String r0 = r8.zzavr
            if (r0 == 0) goto L_0x0023
            java.lang.String r0 = r8.zzavs
            if (r0 != 0) goto L_0x0029
        L_0x0023:
            return r1
        L_0x0024:
            java.lang.String r0 = r8.zzavq
            if (r0 != 0) goto L_0x0029
            return r1
        L_0x0029:
            java.lang.Integer r0 = r8.zzavo
            int r0 = r0.intValue()
            java.lang.Integer r3 = r8.zzavo
            int r3 = r3.intValue()
            if (r3 != r2) goto L_0x005b
            java.lang.String r3 = r8.zzavr
            boolean r3 = com.google.android.gms.measurement.internal.zzfg.zzcp(r3)
            if (r3 == 0) goto L_0x005a
            java.lang.String r3 = r8.zzavs
            boolean r3 = com.google.android.gms.measurement.internal.zzfg.zzcp(r3)
            if (r3 != 0) goto L_0x0048
            goto L_0x005a
        L_0x0048:
            java.math.BigDecimal r3 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x0059 }
            java.lang.String r4 = r8.zzavr     // Catch:{ NumberFormatException -> 0x0059 }
            r3.<init>(r4)     // Catch:{ NumberFormatException -> 0x0059 }
            java.math.BigDecimal r4 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x0059 }
            java.lang.String r8 = r8.zzavs     // Catch:{ NumberFormatException -> 0x0059 }
            r4.<init>(r8)     // Catch:{ NumberFormatException -> 0x0059 }
            r8 = r3
            r3 = r1
            goto L_0x006d
        L_0x0059:
            return r1
        L_0x005a:
            return r1
        L_0x005b:
            java.lang.String r3 = r8.zzavq
            boolean r3 = com.google.android.gms.measurement.internal.zzfg.zzcp(r3)
            if (r3 != 0) goto L_0x0064
            return r1
        L_0x0064:
            java.math.BigDecimal r3 = new java.math.BigDecimal     // Catch:{ NumberFormatException -> 0x00ef }
            java.lang.String r8 = r8.zzavq     // Catch:{ NumberFormatException -> 0x00ef }
            r3.<init>(r8)     // Catch:{ NumberFormatException -> 0x00ef }
            r8 = r1
            r4 = r8
        L_0x006d:
            if (r0 != r2) goto L_0x0072
            if (r8 != 0) goto L_0x0074
            return r1
        L_0x0072:
            if (r3 == 0) goto L_0x00ee
        L_0x0074:
            r2 = -1
            r5 = 0
            r6 = 1
            switch(r0) {
                case 1: goto L_0x00e2;
                case 2: goto L_0x00d6;
                case 3: goto L_0x008d;
                case 4: goto L_0x007b;
                default: goto L_0x007a;
            }
        L_0x007a:
            goto L_0x00ee
        L_0x007b:
            int r8 = r7.compareTo(r8)
            if (r8 == r2) goto L_0x0088
            int r7 = r7.compareTo(r4)
            if (r7 == r6) goto L_0x0088
            r5 = 1
        L_0x0088:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            return r7
        L_0x008d:
            r0 = 0
            int r8 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r8 == 0) goto L_0x00ca
            java.math.BigDecimal r8 = new java.math.BigDecimal
            r8.<init>(r9)
            java.math.BigDecimal r0 = new java.math.BigDecimal
            r1 = 2
            r0.<init>(r1)
            java.math.BigDecimal r8 = r8.multiply(r0)
            java.math.BigDecimal r8 = r3.subtract(r8)
            int r8 = r7.compareTo(r8)
            if (r8 != r6) goto L_0x00c5
            java.math.BigDecimal r8 = new java.math.BigDecimal
            r8.<init>(r9)
            java.math.BigDecimal r9 = new java.math.BigDecimal
            r9.<init>(r1)
            java.math.BigDecimal r8 = r8.multiply(r9)
            java.math.BigDecimal r8 = r3.add(r8)
            int r7 = r7.compareTo(r8)
            if (r7 != r2) goto L_0x00c5
            r5 = 1
        L_0x00c5:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            return r7
        L_0x00ca:
            int r7 = r7.compareTo(r3)
            if (r7 != 0) goto L_0x00d1
            r5 = 1
        L_0x00d1:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            return r7
        L_0x00d6:
            int r7 = r7.compareTo(r3)
            if (r7 != r6) goto L_0x00dd
            r5 = 1
        L_0x00dd:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            return r7
        L_0x00e2:
            int r7 = r7.compareTo(r3)
            if (r7 != r2) goto L_0x00e9
            r5 = 1
        L_0x00e9:
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r5)
            return r7
        L_0x00ee:
            return r1
        L_0x00ef:
            return r1
        L_0x00f0:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzj.zza(java.math.BigDecimal, com.google.android.gms.internal.measurement.zzfx, double):java.lang.Boolean");
    }

    private static zzge[] zzd(Map<Integer, Long> map) {
        if (map == null) {
            return null;
        }
        int i = 0;
        zzge[] zzgeArr = new zzge[map.size()];
        for (Integer num : map.keySet()) {
            zzge zzge = new zzge();
            zzge.zzawq = num;
            zzge.zzawr = (Long) map.get(num);
            int i2 = i + 1;
            zzgeArr[i] = zzge;
            i = i2;
        }
        return zzgeArr;
    }

    private static void zza(Map<Integer, Long> map, int i, long j) {
        Long l = (Long) map.get(Integer.valueOf(i));
        long j2 = j / 1000;
        if (l == null || j2 > l.longValue()) {
            map.put(Integer.valueOf(i), Long.valueOf(j2));
        }
    }

    private static void zzb(Map<Integer, List<Long>> map, int i, long j) {
        List list = (List) map.get(Integer.valueOf(i));
        if (list == null) {
            list = new ArrayList();
            map.put(Integer.valueOf(i), list);
        }
        list.add(Long.valueOf(j / 1000));
    }
}
