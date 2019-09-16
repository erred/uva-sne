package com.facebook.soloader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build.VERSION;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class SoLoader {
    static final boolean DEBUG = false;
    public static final int SOLOADER_ALLOW_ASYNC_INIT = 2;
    public static final int SOLOADER_ENABLE_EXOPACKAGE = 1;
    private static String SO_STORE_NAME_MAIN = "lib-main";
    static final boolean SYSTRACE_LIBRARY_LOADING = false;
    static final String TAG = "SoLoader";
    private static int sFlags;
    private static final Set<String> sLoadedLibraries = new HashSet();
    @Nullable
    private static ThreadPolicy sOldPolicy;
    @Nullable
    private static SoSource[] sSoSources;

    public static final class WrongAbiError extends UnsatisfiedLinkError {
        WrongAbiError(Throwable th) {
            super("APK was built for a different platform");
            initCause(th);
        }
    }

    public static void init(Context context, int i) throws IOException {
        ThreadPolicy allowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            initImpl(context, i);
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskWrites);
        }
    }

    public static void init(Context context, boolean z) {
        try {
            init(context, z ? 1 : 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void initImpl(Context context, int i) throws IOException {
        synchronized (SoLoader.class) {
            if (sSoSources == null) {
                sFlags = i;
                ArrayList arrayList = new ArrayList();
                String str = System.getenv("LD_LIBRARY_PATH");
                if (str == null) {
                    str = "/vendor/lib:/system/lib";
                }
                String[] split = str.split(":");
                for (String file : split) {
                    arrayList.add(new DirectorySoSource(new File(file), 2));
                }
                if (context != null) {
                    int i2 = 1;
                    if ((i & 1) != 0) {
                        arrayList.add(0, new ExoSoSource(context, SO_STORE_NAME_MAIN));
                    } else {
                        ApplicationInfo applicationInfo = context.getApplicationInfo();
                        if ((applicationInfo.flags & 1) != 0 && (applicationInfo.flags & 128) == 0) {
                            i2 = 0;
                        } else {
                            arrayList.add(0, new DirectorySoSource(new File(applicationInfo.nativeLibraryDir), VERSION.SDK_INT <= 17 ? 1 : 0));
                        }
                        arrayList.add(0, new ApkSoSource(context, SO_STORE_NAME_MAIN, i2));
                    }
                }
                SoSource[] soSourceArr = (SoSource[]) arrayList.toArray(new SoSource[arrayList.size()]);
                int makePrepareFlags = makePrepareFlags();
                int length = soSourceArr.length;
                while (true) {
                    int i3 = length - 1;
                    if (length <= 0) {
                        break;
                    }
                    soSourceArr[i3].prepare(makePrepareFlags);
                    length = i3;
                }
                sSoSources = soSourceArr;
            }
        }
    }

    private static int makePrepareFlags() {
        return (sFlags & 2) != 0 ? 1 : 0;
    }

    public static void setInTestMode() {
        sSoSources = new SoSource[]{new NoopSoSource()};
    }

    public static synchronized void loadLibrary(String str) throws UnsatisfiedLinkError {
        synchronized (SoLoader.class) {
            if (sSoSources == null) {
                if ("http://www.android.com/".equals(System.getProperty("java.vendor.url"))) {
                    assertInitialized();
                } else {
                    System.loadLibrary(str);
                    return;
                }
            }
            try {
                loadLibraryBySoName(System.mapLibraryName(str), 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UnsatisfiedLinkError e2) {
                String message = e2.getMessage();
                if (message == null || !message.contains("unexpected e_machine:")) {
                    throw e2;
                }
                throw new WrongAbiError(e2);
            }
        }
    }

    public static File unpackLibraryAndDependencies(String str) throws UnsatisfiedLinkError {
        assertInitialized();
        try {
            return unpackLibraryBySoName(System.mapLibraryName(str));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadLibraryBySoName(String str, int i) throws IOException {
        boolean z;
        int contains = sLoadedLibraries.contains(str);
        if (contains == 0) {
            int i2 = 0;
            if (sOldPolicy == null) {
                sOldPolicy = StrictMode.allowThreadDiskReads();
                z = true;
            } else {
                z = false;
            }
            while (contains == 0) {
                try {
                    if (i2 >= sSoSources.length) {
                        break;
                    }
                    contains = sSoSources[i2].loadLibrary(str, i);
                    i2++;
                } catch (Throwable th) {
                    if (z) {
                        StrictMode.setThreadPolicy(sOldPolicy);
                        sOldPolicy = null;
                    }
                    throw th;
                }
            }
            if (z) {
                StrictMode.setThreadPolicy(sOldPolicy);
                sOldPolicy = null;
            }
        }
        if (contains == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("couldn't find DSO to load: ");
            sb.append(str);
            throw new UnsatisfiedLinkError(sb.toString());
        } else if (contains == 1) {
            sLoadedLibraries.add(str);
        }
    }

    static File unpackLibraryBySoName(String str) throws IOException {
        for (SoSource unpackLibrary : sSoSources) {
            File unpackLibrary2 = unpackLibrary.unpackLibrary(str);
            if (unpackLibrary2 != null) {
                return unpackLibrary2;
            }
        }
        throw new FileNotFoundException(str);
    }

    private static void assertInitialized() {
        if (sSoSources == null) {
            throw new RuntimeException("SoLoader.init() not yet called");
        }
    }

    public static synchronized void prependSoSource(SoSource soSource) throws IOException {
        synchronized (SoLoader.class) {
            assertInitialized();
            soSource.prepare(makePrepareFlags());
            SoSource[] soSourceArr = new SoSource[(sSoSources.length + 1)];
            soSourceArr[0] = soSource;
            System.arraycopy(sSoSources, 0, soSourceArr, 1, sSoSources.length);
            sSoSources = soSourceArr;
        }
    }

    public static synchronized String makeLdLibraryPath() {
        String join;
        synchronized (SoLoader.class) {
            assertInitialized();
            ArrayList arrayList = new ArrayList();
            SoSource[] soSourceArr = sSoSources;
            for (SoSource addToLdLibraryPath : soSourceArr) {
                addToLdLibraryPath.addToLdLibraryPath(arrayList);
            }
            join = TextUtils.join(":", arrayList);
        }
        return join;
    }
}
