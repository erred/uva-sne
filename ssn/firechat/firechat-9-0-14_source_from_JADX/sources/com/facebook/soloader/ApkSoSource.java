package com.facebook.soloader;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;

public class ApkSoSource extends ExtractFromZipSoSource {
    private static final byte APK_SIGNATURE_VERSION = 1;
    public static final int PREFER_ANDROID_LIBS_DIRECTORY = 1;
    private static final String TAG = "ApkSoSource";
    /* access modifiers changed from: private */
    public final int mFlags;

    protected class ApkUnpacker extends ZipUnpacker {
        private final int mFlags;
        private File mLibDir;

        ApkUnpacker() throws IOException {
            super();
            this.mLibDir = new File(ApkSoSource.this.mContext.getApplicationInfo().nativeLibraryDir);
            this.mFlags = ApkSoSource.this.mFlags;
        }

        /* access modifiers changed from: protected */
        public boolean shouldExtract(ZipEntry zipEntry, String str) {
            String name = zipEntry.getName();
            if ((this.mFlags & 1) == 0) {
                String str2 = ApkSoSource.TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("allowing consideration of ");
                sb.append(name);
                sb.append(": self-extraction preferred");
                Log.d(str2, sb.toString());
                return true;
            }
            File file = new File(this.mLibDir, str);
            if (!file.isFile()) {
                Log.d(ApkSoSource.TAG, String.format("allowing considering of %s: %s not in system lib dir", new Object[]{name, str}));
                return true;
            }
            long length = file.length();
            long size = zipEntry.getSize();
            if (length != size) {
                Log.d(ApkSoSource.TAG, String.format("allowing consideration of %s: sysdir file length is %s, but the file is %s bytes long in the APK", new Object[]{file, Long.valueOf(length), Long.valueOf(size)}));
                return true;
            }
            String str3 = ApkSoSource.TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("not allowing consideration of ");
            sb2.append(name);
            sb2.append(": deferring to libdir");
            Log.d(str3, sb2.toString());
            return false;
        }
    }

    public ApkSoSource(Context context, String str, int i) {
        super(context, str, new File(context.getApplicationInfo().sourceDir), "^lib/([^/]+)/([^/]+\\.so)$");
        this.mFlags = i;
    }

    /* access modifiers changed from: protected */
    public Unpacker makeUnpacker() throws IOException {
        return new ApkUnpacker();
    }

    /* access modifiers changed from: protected */
    public byte[] getDepsBlock() throws IOException {
        return SysUtil.makeApkDepBlock(this.mZipFileName);
    }
}
