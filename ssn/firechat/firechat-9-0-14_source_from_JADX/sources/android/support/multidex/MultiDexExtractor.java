package android.support.multidex;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class MultiDexExtractor {
    private static final int BUFFER_SIZE = 16384;
    private static final String DEX_PREFIX = "classes";
    private static final String DEX_SUFFIX = ".dex";
    private static final String EXTRACTED_NAME_EXT = ".classes";
    private static final String EXTRACTED_SUFFIX = ".zip";
    private static final String KEY_CRC = "crc";
    private static final String KEY_DEX_CRC = "dex.crc.";
    private static final String KEY_DEX_NUMBER = "dex.number";
    private static final String KEY_DEX_TIME = "dex.time.";
    private static final String KEY_TIME_STAMP = "timestamp";
    private static final String LOCK_FILENAME = "MultiDex.lock";
    private static final int MAX_EXTRACT_ATTEMPTS = 3;
    private static final long NO_VALUE = -1;
    private static final String PREFS_FILE = "multidex.version";
    private static final String TAG = "MultiDex";

    private static class ExtractedDex extends File {
        public long crc = -1;

        public ExtractedDex(File file, String str) {
            super(file, str);
        }
    }

    MultiDexExtractor() {
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x00c0 A[SYNTHETIC, Splitter:B:22:0x00c0] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0118 A[SYNTHETIC, Splitter:B:40:0x0118] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0138  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<? extends java.io.File> load(android.content.Context r14, java.io.File r15, java.io.File r16, java.lang.String r17, boolean r18) throws java.io.IOException {
        /*
            r2 = r17
            r1 = r18
            java.lang.String r3 = "MultiDex"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "MultiDexExtractor.load("
            r4.append(r5)
            java.lang.String r5 = r15.getPath()
            r4.append(r5)
            java.lang.String r5 = ", "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r5 = ", "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r5 = ")"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.i(r3, r4)
            long r5 = getZipCrc(r15)
            java.io.File r8 = new java.io.File
            java.lang.String r3 = "MultiDex.lock"
            r4 = r16
            r8.<init>(r4, r3)
            java.io.RandomAccessFile r9 = new java.io.RandomAccessFile
            java.lang.String r3 = "rw"
            r9.<init>(r8, r3)
            r10 = 0
            java.nio.channels.FileChannel r11 = r9.getChannel()     // Catch:{ all -> 0x0112 }
            java.lang.String r3 = "MultiDex"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x010e }
            r7.<init>()     // Catch:{ all -> 0x010e }
            java.lang.String r12 = "Blocking on lock "
            r7.append(r12)     // Catch:{ all -> 0x010e }
            java.lang.String r12 = r8.getPath()     // Catch:{ all -> 0x010e }
            r7.append(r12)     // Catch:{ all -> 0x010e }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x010e }
            android.util.Log.i(r3, r7)     // Catch:{ all -> 0x010e }
            java.nio.channels.FileLock r12 = r11.lock()     // Catch:{ all -> 0x010e }
            java.lang.String r3 = "MultiDex"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x010b }
            r7.<init>()     // Catch:{ all -> 0x010b }
            java.lang.String r13 = r8.getPath()     // Catch:{ all -> 0x010b }
            r7.append(r13)     // Catch:{ all -> 0x010b }
            java.lang.String r13 = " locked"
            r7.append(r13)     // Catch:{ all -> 0x010b }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x010b }
            android.util.Log.i(r3, r7)     // Catch:{ all -> 0x010b }
            if (r1 != 0) goto L_0x00a9
            r1 = r14
            r3 = r15
            boolean r7 = isModified(r1, r3, r5, r2)     // Catch:{ all -> 0x010b }
            if (r7 != 0) goto L_0x00ab
            java.util.List r7 = loadExistingExtractions(r14, r15, r16, r17)     // Catch:{ IOException -> 0x0094 }
            r13 = r7
            goto L_0x00be
        L_0x0094:
            r0 = move-exception
            java.lang.String r7 = "MultiDex"
            java.lang.String r13 = "Failed to reload existing extracted secondary dex files, falling back to fresh extraction"
            android.util.Log.w(r7, r13, r0)     // Catch:{ all -> 0x010b }
            java.util.List r13 = performExtractions(r15, r16)     // Catch:{ all -> 0x010b }
            long r3 = getTimeStamp(r3)     // Catch:{ all -> 0x010b }
            r7 = r13
            putStoredApkInfo(r1, r2, r3, r5, r7)     // Catch:{ all -> 0x010b }
            goto L_0x00be
        L_0x00a9:
            r1 = r14
            r3 = r15
        L_0x00ab:
            java.lang.String r7 = "MultiDex"
            java.lang.String r13 = "Detected that extraction must be performed."
            android.util.Log.i(r7, r13)     // Catch:{ all -> 0x010b }
            java.util.List r13 = performExtractions(r15, r16)     // Catch:{ all -> 0x010b }
            long r3 = getTimeStamp(r3)     // Catch:{ all -> 0x010b }
            r7 = r13
            putStoredApkInfo(r1, r2, r3, r5, r7)     // Catch:{ all -> 0x010b }
        L_0x00be:
            if (r12 == 0) goto L_0x00e0
            r12.release()     // Catch:{ IOException -> 0x00c4 }
            goto L_0x00e0
        L_0x00c4:
            r0 = move-exception
            java.lang.String r1 = "MultiDex"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed to release lock on "
            r2.append(r3)
            java.lang.String r3 = r8.getPath()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Log.e(r1, r2)
            r10 = r0
        L_0x00e0:
            if (r11 == 0) goto L_0x00e5
            closeQuietly(r11)
        L_0x00e5:
            closeQuietly(r9)
            if (r10 == 0) goto L_0x00eb
            throw r10
        L_0x00eb:
            java.lang.String r1 = "MultiDex"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "load found "
            r2.append(r3)
            int r3 = r13.size()
            r2.append(r3)
            java.lang.String r3 = " secondary dex files"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Log.i(r1, r2)
            return r13
        L_0x010b:
            r0 = move-exception
            r1 = r0
            goto L_0x0116
        L_0x010e:
            r0 = move-exception
            r1 = r0
            r12 = r10
            goto L_0x0116
        L_0x0112:
            r0 = move-exception
            r1 = r0
            r11 = r10
            r12 = r11
        L_0x0116:
            if (r12 == 0) goto L_0x0136
            r12.release()     // Catch:{ IOException -> 0x011c }
            goto L_0x0136
        L_0x011c:
            java.lang.String r2 = "MultiDex"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Failed to release lock on "
            r3.append(r4)
            java.lang.String r4 = r8.getPath()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.e(r2, r3)
        L_0x0136:
            if (r11 == 0) goto L_0x013b
            closeQuietly(r11)
        L_0x013b:
            closeQuietly(r9)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.multidex.MultiDexExtractor.load(android.content.Context, java.io.File, java.io.File, java.lang.String, boolean):java.util.List");
    }

    private static List<ExtractedDex> loadExistingExtractions(Context context, File file, File file2, String str) throws IOException {
        String str2 = str;
        Log.i(TAG, "loading existing secondary dex files");
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName());
        sb.append(EXTRACTED_NAME_EXT);
        String sb2 = sb.toString();
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str2);
        sb3.append(KEY_DEX_NUMBER);
        int i = multiDexPreferences.getInt(sb3.toString(), 1);
        ArrayList arrayList = new ArrayList(i - 1);
        int i2 = 2;
        while (i2 <= i) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(sb2);
            sb4.append(i2);
            sb4.append(EXTRACTED_SUFFIX);
            ExtractedDex extractedDex = new ExtractedDex(file2, sb4.toString());
            if (extractedDex.isFile()) {
                extractedDex.crc = getZipCrc(extractedDex);
                StringBuilder sb5 = new StringBuilder();
                sb5.append(str2);
                sb5.append(KEY_DEX_CRC);
                sb5.append(i2);
                long j = multiDexPreferences.getLong(sb5.toString(), -1);
                StringBuilder sb6 = new StringBuilder();
                sb6.append(str2);
                sb6.append(KEY_DEX_TIME);
                sb6.append(i2);
                long j2 = multiDexPreferences.getLong(sb6.toString(), -1);
                long lastModified = extractedDex.lastModified();
                if (j2 == lastModified) {
                    String str3 = sb2;
                    SharedPreferences sharedPreferences = multiDexPreferences;
                    if (j == extractedDex.crc) {
                        arrayList.add(extractedDex);
                        i2++;
                        sb2 = str3;
                        multiDexPreferences = sharedPreferences;
                    }
                }
                StringBuilder sb7 = new StringBuilder();
                sb7.append("Invalid extracted dex: ");
                sb7.append(extractedDex);
                sb7.append(" (key \"");
                sb7.append(str2);
                sb7.append("\"), expected modification time: ");
                sb7.append(j2);
                sb7.append(", modification time: ");
                sb7.append(lastModified);
                sb7.append(", expected crc: ");
                sb7.append(j);
                sb7.append(", file crc: ");
                sb7.append(extractedDex.crc);
                throw new IOException(sb7.toString());
            }
            StringBuilder sb8 = new StringBuilder();
            sb8.append("Missing extracted secondary dex file '");
            sb8.append(extractedDex.getPath());
            sb8.append("'");
            throw new IOException(sb8.toString());
        }
        return arrayList;
    }

    private static boolean isModified(Context context, File file, long j, String str) {
        SharedPreferences multiDexPreferences = getMultiDexPreferences(context);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("timestamp");
        if (multiDexPreferences.getLong(sb.toString(), -1) == getTimeStamp(file)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(KEY_CRC);
            if (multiDexPreferences.getLong(sb2.toString(), -1) == j) {
                return false;
            }
        }
        return true;
    }

    private static long getTimeStamp(File file) {
        long lastModified = file.lastModified();
        return lastModified == -1 ? lastModified - 1 : lastModified;
    }

    private static long getZipCrc(File file) throws IOException {
        long zipCrc = ZipUtil.getZipCrc(file);
        return zipCrc == -1 ? zipCrc - 1 : zipCrc;
    }

    private static List<ExtractedDex> performExtractions(File file, File file2) throws IOException {
        ExtractedDex extractedDex;
        boolean z;
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName());
        sb.append(EXTRACTED_NAME_EXT);
        String sb2 = sb.toString();
        prepareDexDir(file2, sb2);
        ArrayList arrayList = new ArrayList();
        ZipFile zipFile = new ZipFile(file);
        int i = 2;
        try {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(DEX_PREFIX);
            sb3.append(2);
            sb3.append(DEX_SUFFIX);
            ZipEntry entry = zipFile.getEntry(sb3.toString());
            while (entry != null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb2);
                sb4.append(i);
                sb4.append(EXTRACTED_SUFFIX);
                extractedDex = new ExtractedDex(file2, sb4.toString());
                arrayList.add(extractedDex);
                String str = TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Extraction is needed for file ");
                sb5.append(extractedDex);
                Log.i(str, sb5.toString());
                int i2 = 0;
                z = false;
                while (i2 < 3 && !z) {
                    i2++;
                    extract(zipFile, entry, extractedDex, sb2);
                    extractedDex.crc = getZipCrc(extractedDex);
                    z = true;
                    String str2 = TAG;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("Extraction ");
                    sb6.append(z ? "succeeded" : "failed");
                    sb6.append(" - length ");
                    sb6.append(extractedDex.getAbsolutePath());
                    sb6.append(": ");
                    sb6.append(extractedDex.length());
                    sb6.append(" - crc: ");
                    sb6.append(extractedDex.crc);
                    Log.i(str2, sb6.toString());
                    if (!z) {
                        extractedDex.delete();
                        if (extractedDex.exists()) {
                            String str3 = TAG;
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("Failed to delete corrupted secondary dex '");
                            sb7.append(extractedDex.getPath());
                            sb7.append("'");
                            Log.w(str3, sb7.toString());
                        }
                    }
                }
                if (!z) {
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append("Could not create zip file ");
                    sb8.append(extractedDex.getAbsolutePath());
                    sb8.append(" for secondary dex (");
                    sb8.append(i);
                    sb8.append(")");
                    throw new IOException(sb8.toString());
                }
                i++;
                StringBuilder sb9 = new StringBuilder();
                sb9.append(DEX_PREFIX);
                sb9.append(i);
                sb9.append(DEX_SUFFIX);
                entry = zipFile.getEntry(sb9.toString());
            }
            try {
                zipFile.close();
            } catch (IOException e) {
                Log.w(TAG, "Failed to close resource", e);
            }
            return arrayList;
        } catch (IOException e2) {
            String str4 = TAG;
            StringBuilder sb10 = new StringBuilder();
            sb10.append("Failed to read crc from ");
            sb10.append(extractedDex.getAbsolutePath());
            Log.w(str4, sb10.toString(), e2);
            z = false;
        } catch (Throwable th) {
            try {
                zipFile.close();
            } catch (IOException e3) {
                Log.w(TAG, "Failed to close resource", e3);
            }
            throw th;
        }
    }

    private static void putStoredApkInfo(Context context, String str, long j, long j2, List<ExtractedDex> list) {
        Editor edit = getMultiDexPreferences(context).edit();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("timestamp");
        edit.putLong(sb.toString(), j);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(KEY_CRC);
        edit.putLong(sb2.toString(), j2);
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(KEY_DEX_NUMBER);
        edit.putInt(sb3.toString(), list.size() + 1);
        int i = 2;
        for (ExtractedDex extractedDex : list) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(KEY_DEX_CRC);
            sb4.append(i);
            edit.putLong(sb4.toString(), extractedDex.crc);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append(KEY_DEX_TIME);
            sb5.append(i);
            edit.putLong(sb5.toString(), extractedDex.lastModified());
            i++;
        }
        edit.commit();
    }

    private static SharedPreferences getMultiDexPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE, VERSION.SDK_INT < 11 ? 0 : 4);
    }

    private static void prepareDexDir(File file, final String str) {
        File[] listFiles = file.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                return !name.startsWith(str) && !name.equals(MultiDexExtractor.LOCK_FILENAME);
            }
        });
        if (listFiles == null) {
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to list secondary dex dir content (");
            sb.append(file.getPath());
            sb.append(").");
            Log.w(str2, sb.toString());
            return;
        }
        for (File file2 : listFiles) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Trying to delete old file ");
            sb2.append(file2.getPath());
            sb2.append(" of size ");
            sb2.append(file2.length());
            Log.i(str3, sb2.toString());
            if (!file2.delete()) {
                String str4 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Failed to delete old file ");
                sb3.append(file2.getPath());
                Log.w(str4, sb3.toString());
            } else {
                String str5 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Deleted old file ");
                sb4.append(file2.getPath());
                Log.i(str5, sb4.toString());
            }
        }
    }

    private static void extract(ZipFile zipFile, ZipEntry zipEntry, File file, String str) throws IOException, FileNotFoundException {
        ZipOutputStream zipOutputStream;
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        StringBuilder sb = new StringBuilder();
        sb.append("tmp-");
        sb.append(str);
        File createTempFile = File.createTempFile(sb.toString(), EXTRACTED_SUFFIX, file.getParentFile());
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Extracting ");
        sb2.append(createTempFile.getPath());
        Log.i(str2, sb2.toString());
        try {
            zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(createTempFile)));
            ZipEntry zipEntry2 = new ZipEntry("classes.dex");
            zipEntry2.setTime(zipEntry.getTime());
            zipOutputStream.putNextEntry(zipEntry2);
            byte[] bArr = new byte[16384];
            for (int read = inputStream.read(bArr); read != -1; read = inputStream.read(bArr)) {
                zipOutputStream.write(bArr, 0, read);
            }
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            if (!createTempFile.setReadOnly()) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Failed to mark readonly \"");
                sb3.append(createTempFile.getAbsolutePath());
                sb3.append("\" (tmp of \"");
                sb3.append(file.getAbsolutePath());
                sb3.append("\")");
                throw new IOException(sb3.toString());
            }
            String str3 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("Renaming to ");
            sb4.append(file.getPath());
            Log.i(str3, sb4.toString());
            if (!createTempFile.renameTo(file)) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Failed to rename \"");
                sb5.append(createTempFile.getAbsolutePath());
                sb5.append("\" to \"");
                sb5.append(file.getAbsolutePath());
                sb5.append("\"");
                throw new IOException(sb5.toString());
            }
            closeQuietly(inputStream);
            createTempFile.delete();
        } catch (Throwable th) {
            closeQuietly(inputStream);
            createTempFile.delete();
            throw th;
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            Log.w(TAG, "Failed to close resource", e);
        }
    }
}
