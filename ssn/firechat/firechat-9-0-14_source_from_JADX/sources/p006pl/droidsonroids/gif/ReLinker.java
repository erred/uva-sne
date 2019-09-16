package p006pl.droidsonroids.gif;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import java.io.Closeable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/* renamed from: pl.droidsonroids.gif.ReLinker */
class ReLinker {
    private static final int COPY_BUFFER_SIZE = 8192;
    private static final String LIB_DIR = "lib";
    /* access modifiers changed from: private */
    public static final String MAPPED_BASE_LIB_NAME = System.mapLibraryName("pl_droidsonroids_gif");
    private static final int MAX_TRIES = 5;

    private ReLinker() {
    }

    @SuppressLint({"UnsafeDynamicallyLoadedCode"})
    static void loadLibrary(Context context) {
        synchronized (ReLinker.class) {
            System.load(unpackLibrary(context).getAbsolutePath());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        closeSilently(r3);
        closeSilently(r5);
        setFilePermissions(r1);
     */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00ac A[Catch:{ IOException -> 0x00a9, all -> 0x0096, all -> 0x00b5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00c3 A[SYNTHETIC, Splitter:B:51:0x00c3] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.File unpackLibrary(android.content.Context r8) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = MAPPED_BASE_LIB_NAME
            r0.append(r1)
            java.lang.String r1 = "1.2.16-SNAPSHOT"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.io.File r1 = new java.io.File
            java.lang.String r2 = "lib"
            r3 = 0
            java.io.File r2 = r8.getDir(r2, r3)
            r1.<init>(r2, r0)
            boolean r2 = r1.isFile()
            if (r2 == 0) goto L_0x0026
            return r1
        L_0x0026:
            java.io.File r2 = new java.io.File
            java.io.File r4 = r8.getCacheDir()
            r2.<init>(r4, r0)
            boolean r0 = r2.isFile()
            if (r0 == 0) goto L_0x0036
            return r2
        L_0x0036:
            java.lang.String r0 = "pl_droidsonroids_gif_surface"
            java.lang.String r0 = java.lang.System.mapLibraryName(r0)
            pl.droidsonroids.gif.ReLinker$1 r4 = new pl.droidsonroids.gif.ReLinker$1
            r4.<init>(r0)
            clearOldLibraryFiles(r1, r4)
            clearOldLibraryFiles(r2, r4)
            android.content.pm.ApplicationInfo r8 = r8.getApplicationInfo()
            java.io.File r0 = new java.io.File
            java.lang.String r8 = r8.sourceDir
            r0.<init>(r8)
            r8 = 0
            java.util.zip.ZipFile r0 = openZipFile(r0)     // Catch:{ all -> 0x00bd }
        L_0x0057:
            int r4 = r3 + 1
            r5 = 5
            if (r3 >= r5) goto L_0x00b7
            java.util.zip.ZipEntry r3 = findLibraryEntry(r0)     // Catch:{ all -> 0x00b5 }
            if (r3 != 0) goto L_0x0080
            java.lang.IllegalStateException r8 = new java.lang.IllegalStateException     // Catch:{ all -> 0x00b5 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x00b5 }
            r1.<init>()     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = "Library "
            r1.append(r2)     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = MAPPED_BASE_LIB_NAME     // Catch:{ all -> 0x00b5 }
            r1.append(r2)     // Catch:{ all -> 0x00b5 }
            java.lang.String r2 = " for supported ABIs not found in APK file"
            r1.append(r2)     // Catch:{ all -> 0x00b5 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x00b5 }
            r8.<init>(r1)     // Catch:{ all -> 0x00b5 }
            throw r8     // Catch:{ all -> 0x00b5 }
        L_0x0080:
            java.io.InputStream r3 = r0.getInputStream(r3)     // Catch:{ IOException -> 0x00a7, all -> 0x009e }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x009c, all -> 0x009a }
            r5.<init>(r1)     // Catch:{ IOException -> 0x009c, all -> 0x009a }
            copy(r3, r5)     // Catch:{ IOException -> 0x00a9, all -> 0x0096 }
            closeSilently(r3)     // Catch:{ all -> 0x00b5 }
            closeSilently(r5)     // Catch:{ all -> 0x00b5 }
            setFilePermissions(r1)     // Catch:{ all -> 0x00b5 }
            goto L_0x00b7
        L_0x0096:
            r8 = move-exception
            r1 = r8
            r8 = r5
            goto L_0x00a0
        L_0x009a:
            r1 = move-exception
            goto L_0x00a0
        L_0x009c:
            r5 = r8
            goto L_0x00a9
        L_0x009e:
            r1 = move-exception
            r3 = r8
        L_0x00a0:
            closeSilently(r3)     // Catch:{ all -> 0x00b5 }
            closeSilently(r8)     // Catch:{ all -> 0x00b5 }
            throw r1     // Catch:{ all -> 0x00b5 }
        L_0x00a7:
            r3 = r8
            r5 = r3
        L_0x00a9:
            r6 = 2
            if (r4 <= r6) goto L_0x00ad
            r1 = r2
        L_0x00ad:
            closeSilently(r3)     // Catch:{ all -> 0x00b5 }
            closeSilently(r5)     // Catch:{ all -> 0x00b5 }
            r3 = r4
            goto L_0x0057
        L_0x00b5:
            r8 = move-exception
            goto L_0x00c1
        L_0x00b7:
            if (r0 == 0) goto L_0x00bc
            r0.close()     // Catch:{ IOException -> 0x00bc }
        L_0x00bc:
            return r1
        L_0x00bd:
            r0 = move-exception
            r7 = r0
            r0 = r8
            r8 = r7
        L_0x00c1:
            if (r0 == 0) goto L_0x00c6
            r0.close()     // Catch:{ IOException -> 0x00c6 }
        L_0x00c6:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: p006pl.droidsonroids.gif.ReLinker.unpackLibrary(android.content.Context):java.io.File");
    }

    private static ZipEntry findLibraryEntry(ZipFile zipFile) {
        for (String entry : getSupportedABIs()) {
            ZipEntry entry2 = getEntry(zipFile, entry);
            if (entry2 != null) {
                return entry2;
            }
        }
        return null;
    }

    private static String[] getSupportedABIs() {
        if (VERSION.SDK_INT >= 21) {
            return Build.SUPPORTED_ABIS;
        }
        return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
    }

    private static ZipEntry getEntry(ZipFile zipFile, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("lib/");
        sb.append(str);
        sb.append("/");
        sb.append(MAPPED_BASE_LIB_NAME);
        return zipFile.getEntry(sb.toString());
    }

    private static ZipFile openZipFile(File file) {
        ZipFile zipFile;
        int i = 0;
        while (true) {
            int i2 = i + 1;
            if (i >= 5) {
                zipFile = null;
                break;
            }
            try {
                zipFile = new ZipFile(file, 1);
                break;
            } catch (IOException unused) {
                i = i2;
            }
        }
        if (zipFile != null) {
            return zipFile;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not open APK file: ");
        sb.append(file.getAbsolutePath());
        throw new IllegalStateException(sb.toString());
    }

    private static void clearOldLibraryFiles(File file, FilenameFilter filenameFilter) {
        File[] listFiles = file.getParentFile().listFiles(filenameFilter);
        if (listFiles != null) {
            for (File delete : listFiles) {
                delete.delete();
            }
        }
    }

    @SuppressLint({"SetWorldReadable"})
    private static void setFilePermissions(File file) {
        file.setReadable(true, false);
        file.setExecutable(true, false);
        file.setWritable(true);
    }

    private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[8192];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }
}
