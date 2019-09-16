package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.internal.measurement.zzfv;
import com.google.android.gms.internal.measurement.zzfw;
import com.google.android.gms.internal.measurement.zzfx;
import com.google.android.gms.internal.measurement.zzfy;
import com.google.android.gms.internal.measurement.zzfz;
import com.google.android.gms.internal.measurement.zzgd;
import com.google.android.gms.internal.measurement.zzgf;
import com.google.android.gms.internal.measurement.zzgg;
import com.google.android.gms.internal.measurement.zzgh;
import com.google.android.gms.internal.measurement.zzgi;
import com.google.android.gms.internal.measurement.zzgj;
import com.google.android.gms.internal.measurement.zzgl;
import com.google.android.gms.internal.measurement.zzyy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.StringUtils;
import p010me.leolin.shortcutbadger.impl.NewHtcHomeBadger;

public final class zzfg extends zzez {
    zzfg(zzfa zzfa) {
        super(zzfa);
    }

    /* access modifiers changed from: protected */
    public final boolean zzgt() {
        return false;
    }

    /* access modifiers changed from: 0000 */
    public final void zza(zzgl zzgl, Object obj) {
        Preconditions.checkNotNull(obj);
        zzgl.zzamp = null;
        zzgl.zzawx = null;
        zzgl.zzauh = null;
        if (obj instanceof String) {
            zzgl.zzamp = (String) obj;
        } else if (obj instanceof Long) {
            zzgl.zzawx = (Long) obj;
        } else if (obj instanceof Double) {
            zzgl.zzauh = (Double) obj;
        } else {
            zzgo().zzjd().zzg("Ignoring invalid (type) user attribute value", obj);
        }
    }

    /* access modifiers changed from: 0000 */
    public final void zza(zzgg zzgg, Object obj) {
        Preconditions.checkNotNull(obj);
        zzgg.zzamp = null;
        zzgg.zzawx = null;
        zzgg.zzauh = null;
        if (obj instanceof String) {
            zzgg.zzamp = (String) obj;
        } else if (obj instanceof Long) {
            zzgg.zzawx = (Long) obj;
        } else if (obj instanceof Double) {
            zzgg.zzauh = (Double) obj;
        } else {
            zzgo().zzjd().zzg("Ignoring invalid (type) event param value", obj);
        }
    }

    /* access modifiers changed from: 0000 */
    public final byte[] zza(zzgh zzgh) {
        try {
            byte[] bArr = new byte[zzgh.zzvu()];
            zzyy zzk = zzyy.zzk(bArr, 0, bArr.length);
            zzgh.zza(zzk);
            zzk.zzyt();
            return bArr;
        } catch (IOException e) {
            zzgo().zzjd().zzg("Data loss. Failed to serialize batch", e);
            return null;
        }
    }

    static zzgg zza(zzgf zzgf, String str) {
        zzgg[] zzggArr;
        for (zzgg zzgg : zzgf.zzawt) {
            if (zzgg.name.equals(str)) {
                return zzgg;
            }
        }
        return null;
    }

    static Object zzb(zzgf zzgf, String str) {
        zzgg zza = zza(zzgf, str);
        if (zza != null) {
            if (zza.zzamp != null) {
                return zza.zzamp;
            }
            if (zza.zzawx != null) {
                return zza.zzawx;
            }
            if (zza.zzauh != null) {
                return zza.zzauh;
            }
        }
        return null;
    }

    static zzgg[] zza(zzgg[] zzggArr, String str, Object obj) {
        for (zzgg zzgg : zzggArr) {
            if (str.equals(zzgg.name)) {
                zzgg.zzawx = null;
                zzgg.zzamp = null;
                zzgg.zzauh = null;
                if (obj instanceof Long) {
                    zzgg.zzawx = (Long) obj;
                } else if (obj instanceof String) {
                    zzgg.zzamp = (String) obj;
                } else if (obj instanceof Double) {
                    zzgg.zzauh = (Double) obj;
                }
                return zzggArr;
            }
        }
        zzgg[] zzggArr2 = new zzgg[(zzggArr.length + 1)];
        System.arraycopy(zzggArr, 0, zzggArr2, 0, zzggArr.length);
        zzgg zzgg2 = new zzgg();
        zzgg2.name = str;
        if (obj instanceof Long) {
            zzgg2.zzawx = (Long) obj;
        } else if (obj instanceof String) {
            zzgg2.zzamp = (String) obj;
        } else if (obj instanceof Double) {
            zzgg2.zzauh = (Double) obj;
        }
        zzggArr2[zzggArr.length] = zzgg2;
        return zzggArr2;
    }

    /* access modifiers changed from: 0000 */
    public final String zzb(zzgh zzgh) {
        zzgi[] zzgiArr;
        zzgh zzgh2 = zzgh;
        StringBuilder sb = new StringBuilder();
        sb.append("\nbatch {\n");
        if (zzgh2.zzawy != null) {
            for (zzgi zzgi : zzgh2.zzawy) {
                if (!(zzgi == null || zzgi == null)) {
                    zza(sb, 1);
                    sb.append("bundle {\n");
                    zza(sb, 1, "protocol_version", (Object) zzgi.zzaxa);
                    zza(sb, 1, "platform", (Object) zzgi.zzaxi);
                    zza(sb, 1, "gmp_version", (Object) zzgi.zzaxm);
                    zza(sb, 1, "uploading_gmp_version", (Object) zzgi.zzaxn);
                    zza(sb, 1, "config_version", (Object) zzgi.zzaxy);
                    zza(sb, 1, "gmp_app_id", (Object) zzgi.zzafx);
                    zza(sb, 1, "admob_app_id", (Object) zzgi.zzawj);
                    zza(sb, 1, "app_id", (Object) zzgi.zztt);
                    zza(sb, 1, "app_version", (Object) zzgi.zzts);
                    zza(sb, 1, "app_version_major", (Object) zzgi.zzaxu);
                    zza(sb, 1, "firebase_instance_id", (Object) zzgi.zzafz);
                    zza(sb, 1, "dev_cert_hash", (Object) zzgi.zzaxq);
                    zza(sb, 1, "app_store", (Object) zzgi.zzage);
                    zza(sb, 1, "upload_timestamp_millis", (Object) zzgi.zzaxd);
                    zza(sb, 1, "start_timestamp_millis", (Object) zzgi.zzaxe);
                    zza(sb, 1, "end_timestamp_millis", (Object) zzgi.zzaxf);
                    zza(sb, 1, "previous_bundle_start_timestamp_millis", (Object) zzgi.zzaxg);
                    zza(sb, 1, "previous_bundle_end_timestamp_millis", (Object) zzgi.zzaxh);
                    zza(sb, 1, "app_instance_id", (Object) zzgi.zzafw);
                    zza(sb, 1, "resettable_device_id", (Object) zzgi.zzaxo);
                    zza(sb, 1, AmplitudeClient.DEVICE_ID_KEY, (Object) zzgi.zzaxx);
                    zza(sb, 1, "ds_id", (Object) zzgi.zzaya);
                    zza(sb, 1, "limited_ad_tracking", (Object) zzgi.zzaxp);
                    zza(sb, 1, "os_version", (Object) zzgi.zzaxj);
                    zza(sb, 1, "device_model", (Object) zzgi.zzaxk);
                    zza(sb, 1, "user_default_language", (Object) zzgi.zzaia);
                    zza(sb, 1, "time_zone_offset_minutes", (Object) zzgi.zzaxl);
                    zza(sb, 1, "bundle_sequential_index", (Object) zzgi.zzaxr);
                    zza(sb, 1, "service_upload", (Object) zzgi.zzaxs);
                    zza(sb, 1, "health_monitor", (Object) zzgi.zzagv);
                    if (!(zzgi.zzaxz == null || zzgi.zzaxz.longValue() == 0)) {
                        zza(sb, 1, "android_id", (Object) zzgi.zzaxz);
                    }
                    if (zzgi.zzayc != null) {
                        zza(sb, 1, "retry_counter", (Object) zzgi.zzayc);
                    }
                    zzgl[] zzglArr = zzgi.zzaxc;
                    if (zzglArr != null) {
                        for (zzgl zzgl : zzglArr) {
                            if (zzgl != null) {
                                zza(sb, 2);
                                sb.append("user_property {\n");
                                zza(sb, 2, "set_timestamp_millis", (Object) zzgl.zzayl);
                                zza(sb, 2, "name", (Object) zzgl().zzbu(zzgl.name));
                                zza(sb, 2, "string_value", (Object) zzgl.zzamp);
                                zza(sb, 2, "int_value", (Object) zzgl.zzawx);
                                zza(sb, 2, "double_value", (Object) zzgl.zzauh);
                                zza(sb, 2);
                                sb.append("}\n");
                            }
                        }
                    }
                    zzgd[] zzgdArr = zzgi.zzaxt;
                    if (zzgdArr != null) {
                        for (zzgd zzgd : zzgdArr) {
                            if (zzgd != null) {
                                zza(sb, 2);
                                sb.append("audience_membership {\n");
                                zza(sb, 2, "audience_id", (Object) zzgd.zzauy);
                                zza(sb, 2, "new_audience", (Object) zzgd.zzawo);
                                zza(sb, 2, "current_data", zzgd.zzawm);
                                zza(sb, 2, "previous_data", zzgd.zzawn);
                                zza(sb, 2);
                                sb.append("}\n");
                            }
                        }
                    }
                    zzgf[] zzgfArr = zzgi.zzaxb;
                    if (zzgfArr != null) {
                        for (zzgf zzgf : zzgfArr) {
                            if (zzgf != null) {
                                zza(sb, 2);
                                sb.append("event {\n");
                                zza(sb, 2, "name", (Object) zzgl().zzbs(zzgf.name));
                                zza(sb, 2, "timestamp_millis", (Object) zzgf.zzawu);
                                zza(sb, 2, "previous_timestamp_millis", (Object) zzgf.zzawv);
                                zza(sb, 2, NewHtcHomeBadger.COUNT, (Object) zzgf.count);
                                zzgg[] zzggArr = zzgf.zzawt;
                                if (zzggArr != null) {
                                    for (zzgg zzgg : zzggArr) {
                                        if (zzgg != null) {
                                            zza(sb, 3);
                                            sb.append("param {\n");
                                            zza(sb, 3, "name", (Object) zzgl().zzbt(zzgg.name));
                                            zza(sb, 3, "string_value", (Object) zzgg.zzamp);
                                            zza(sb, 3, "int_value", (Object) zzgg.zzawx);
                                            zza(sb, 3, "double_value", (Object) zzgg.zzauh);
                                            zza(sb, 3);
                                            sb.append("}\n");
                                        }
                                    }
                                }
                                zza(sb, 2);
                                sb.append("}\n");
                            }
                        }
                    }
                    zza(sb, 1);
                    sb.append("}\n");
                }
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public final String zza(zzfv zzfv) {
        if (zzfv == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\nevent_filter {\n");
        zza(sb, 0, "filter_id", (Object) zzfv.zzave);
        zza(sb, 0, "event_name", (Object) zzgl().zzbs(zzfv.zzavf));
        zza(sb, 1, "event_count_filter", zzfv.zzavi);
        sb.append("  filters {\n");
        for (zzfw zza : zzfv.zzavg) {
            zza(sb, 2, zza);
        }
        zza(sb, 1);
        sb.append("}\n}\n");
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public final String zza(zzfy zzfy) {
        if (zzfy == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\nproperty_filter {\n");
        zza(sb, 0, "filter_id", (Object) zzfy.zzave);
        zza(sb, 0, "property_name", (Object) zzgl().zzbu(zzfy.zzavu));
        zza(sb, 1, zzfy.zzavv);
        sb.append("}\n");
        return sb.toString();
    }

    private static void zza(StringBuilder sb, int i, String str, zzgj zzgj) {
        if (zzgj != null) {
            zza(sb, 3);
            sb.append(str);
            sb.append(" {\n");
            int i2 = 0;
            if (zzgj.zzayf != null) {
                zza(sb, 4);
                sb.append("results: ");
                long[] jArr = zzgj.zzayf;
                int length = jArr.length;
                int i3 = 0;
                int i4 = 0;
                while (i3 < length) {
                    Long valueOf = Long.valueOf(jArr[i3]);
                    int i5 = i4 + 1;
                    if (i4 != 0) {
                        sb.append(", ");
                    }
                    sb.append(valueOf);
                    i3++;
                    i4 = i5;
                }
                sb.append(10);
            }
            if (zzgj.zzaye != null) {
                zza(sb, 4);
                sb.append("status: ");
                long[] jArr2 = zzgj.zzaye;
                int length2 = jArr2.length;
                int i6 = 0;
                while (i2 < length2) {
                    Long valueOf2 = Long.valueOf(jArr2[i2]);
                    int i7 = i6 + 1;
                    if (i6 != 0) {
                        sb.append(", ");
                    }
                    sb.append(valueOf2);
                    i2++;
                    i6 = i7;
                }
                sb.append(10);
            }
            zza(sb, 3);
            sb.append("}\n");
        }
    }

    private final void zza(StringBuilder sb, int i, String str, zzfx zzfx) {
        if (zzfx != null) {
            zza(sb, i);
            sb.append(str);
            sb.append(" {\n");
            if (zzfx.zzavo != null) {
                String str2 = "UNKNOWN_COMPARISON_TYPE";
                switch (zzfx.zzavo.intValue()) {
                    case 1:
                        str2 = "LESS_THAN";
                        break;
                    case 2:
                        str2 = "GREATER_THAN";
                        break;
                    case 3:
                        str2 = "EQUAL";
                        break;
                    case 4:
                        str2 = "BETWEEN";
                        break;
                }
                zza(sb, i, "comparison_type", (Object) str2);
            }
            zza(sb, i, "match_as_float", (Object) zzfx.zzavp);
            zza(sb, i, "comparison_value", (Object) zzfx.zzavq);
            zza(sb, i, "min_comparison_value", (Object) zzfx.zzavr);
            zza(sb, i, "max_comparison_value", (Object) zzfx.zzavs);
            zza(sb, i);
            sb.append("}\n");
        }
    }

    private final void zza(StringBuilder sb, int i, zzfw zzfw) {
        String[] strArr;
        if (zzfw != null) {
            zza(sb, i);
            sb.append("filter {\n");
            zza(sb, i, "complement", (Object) zzfw.zzavm);
            zza(sb, i, "param_name", (Object) zzgl().zzbt(zzfw.zzavn));
            int i2 = i + 1;
            String str = "string_filter";
            zzfz zzfz = zzfw.zzavk;
            if (zzfz != null) {
                zza(sb, i2);
                sb.append(str);
                sb.append(" {\n");
                if (zzfz.zzavw != null) {
                    String str2 = "UNKNOWN_MATCH_TYPE";
                    switch (zzfz.zzavw.intValue()) {
                        case 1:
                            str2 = "REGEXP";
                            break;
                        case 2:
                            str2 = "BEGINS_WITH";
                            break;
                        case 3:
                            str2 = "ENDS_WITH";
                            break;
                        case 4:
                            str2 = "PARTIAL";
                            break;
                        case 5:
                            str2 = "EXACT";
                            break;
                        case 6:
                            str2 = "IN_LIST";
                            break;
                    }
                    zza(sb, i2, "match_type", (Object) str2);
                }
                zza(sb, i2, "expression", (Object) zzfz.zzavx);
                zza(sb, i2, "case_sensitive", (Object) zzfz.zzavy);
                if (zzfz.zzavz.length > 0) {
                    zza(sb, i2 + 1);
                    sb.append("expression_list {\n");
                    for (String str3 : zzfz.zzavz) {
                        zza(sb, i2 + 2);
                        sb.append(str3);
                        sb.append(StringUtils.f158LF);
                    }
                    sb.append("}\n");
                }
                zza(sb, i2);
                sb.append("}\n");
            }
            zza(sb, i2, "number_filter", zzfw.zzavl);
            zza(sb, i);
            sb.append("}\n");
        }
    }

    private static void zza(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            sb.append("  ");
        }
    }

    private static void zza(StringBuilder sb, int i, String str, Object obj) {
        if (obj != null) {
            zza(sb, i + 1);
            sb.append(str);
            sb.append(": ");
            sb.append(obj);
            sb.append(10);
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
        zzgo().zzjd().zzbx("Failed to load parcelable from buffer");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
        r1.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0030, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001a, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x001c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final <T extends android.os.Parcelable> T zza(byte[] r5, android.os.Parcelable.Creator<T> r6) {
        /*
            r4 = this;
            r0 = 0
            if (r5 != 0) goto L_0x0004
            return r0
        L_0x0004:
            android.os.Parcel r1 = android.os.Parcel.obtain()
            int r2 = r5.length     // Catch:{ ParseException -> 0x001c }
            r3 = 0
            r1.unmarshall(r5, r3, r2)     // Catch:{ ParseException -> 0x001c }
            r1.setDataPosition(r3)     // Catch:{ ParseException -> 0x001c }
            java.lang.Object r5 = r6.createFromParcel(r1)     // Catch:{ ParseException -> 0x001c }
            android.os.Parcelable r5 = (android.os.Parcelable) r5     // Catch:{ ParseException -> 0x001c }
            r1.recycle()
            return r5
        L_0x001a:
            r5 = move-exception
            goto L_0x002d
        L_0x001c:
            com.google.android.gms.measurement.internal.zzap r5 = r4.zzgo()     // Catch:{ all -> 0x001a }
            com.google.android.gms.measurement.internal.zzar r5 = r5.zzjd()     // Catch:{ all -> 0x001a }
            java.lang.String r6 = "Failed to load parcelable from buffer"
            r5.zzbx(r6)     // Catch:{ all -> 0x001a }
            r1.recycle()
            return r0
        L_0x002d:
            r1.recycle()
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzfg.zza(byte[], android.os.Parcelable$Creator):android.os.Parcelable");
    }

    /* access modifiers changed from: 0000 */
    @WorkerThread
    public final boolean zze(zzad zzad, zzh zzh) {
        Preconditions.checkNotNull(zzad);
        Preconditions.checkNotNull(zzh);
        if (!TextUtils.isEmpty(zzh.zzafx) || !TextUtils.isEmpty(zzh.zzagk)) {
            return true;
        }
        zzgr();
        return false;
    }

    static boolean zzcp(String str) {
        return str != null && str.matches("([+-])?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)") && str.length() <= 310;
    }

    static boolean zza(long[] jArr, int i) {
        if (i < (jArr.length << 6) && (jArr[i / 64] & (1 << (i % 64))) != 0) {
            return true;
        }
        return false;
    }

    static long[] zza(BitSet bitSet) {
        int length = (bitSet.length() + 63) / 64;
        long[] jArr = new long[length];
        for (int i = 0; i < length; i++) {
            jArr[i] = 0;
            for (int i2 = 0; i2 < 64; i2++) {
                int i3 = (i << 6) + i2;
                if (i3 >= bitSet.length()) {
                    break;
                }
                if (bitSet.get(i3)) {
                    jArr[i] = jArr[i] | (1 << i2);
                }
            }
        }
        return jArr;
    }

    /* access modifiers changed from: 0000 */
    public final boolean zzb(long j, long j2) {
        return j == 0 || j2 <= 0 || Math.abs(zzbx().currentTimeMillis() - j) > j2;
    }

    /* access modifiers changed from: 0000 */
    public final byte[] zza(byte[] bArr) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr2 = new byte[1024];
            while (true) {
                int read = gZIPInputStream.read(bArr2);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr2, 0, read);
                } else {
                    gZIPInputStream.close();
                    byteArrayInputStream.close();
                    return byteArrayOutputStream.toByteArray();
                }
            }
        } catch (IOException e) {
            zzgo().zzjd().zzg("Failed to ungzip content", e);
            throw e;
        }
    }

    /* access modifiers changed from: 0000 */
    public final byte[] zzb(byte[] bArr) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gZIPOutputStream.write(bArr);
            gZIPOutputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            zzgo().zzjd().zzg("Failed to gzip content", e);
            throw e;
        }
    }

    public final /* bridge */ /* synthetic */ zzfg zzjo() {
        return super.zzjo();
    }

    public final /* bridge */ /* synthetic */ zzj zzjp() {
        return super.zzjp();
    }

    public final /* bridge */ /* synthetic */ zzq zzjq() {
        return super.zzjq();
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
