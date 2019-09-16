package com.opengarden.firechat.matrixsdk.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.StatFs;
import android.system.Os;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo;
import java.io.File;
import java.lang.reflect.Field;
import org.apache.commons.lang3.StringUtils;

public class ContentUtils {
    private static final String LOG_TAG = "ContentUtils";

    public static ImageInfo getImageInfoFromFile(String str) {
        ImageInfo imageInfo = new ImageInfo();
        try {
            Bitmap decodeFile = BitmapFactory.decodeFile(str);
            imageInfo.f136w = Integer.valueOf(decodeFile.getWidth());
            imageInfo.f135h = Integer.valueOf(decodeFile.getHeight());
            imageInfo.size = Long.valueOf(new File(str).length());
            imageInfo.mimetype = getMimeType(str);
        } catch (OutOfMemoryError unused) {
            Log.m211e(LOG_TAG, "## getImageInfoFromFile() : oom");
        }
        return imageInfo;
    }

    public static String getMimeType(String str) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(str.substring(str.lastIndexOf(46) + 1).toLowerCase());
    }

    public static boolean deleteDirectory(File file) {
        boolean z;
        boolean z2;
        boolean z3 = false;
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                z = true;
                for (int i = 0; i < listFiles.length; i++) {
                    if (listFiles[i].isDirectory()) {
                        z2 = deleteDirectory(listFiles[i]);
                    } else {
                        z2 = listFiles[i].delete();
                    }
                    z &= z2;
                }
                if (z && file.delete()) {
                    z3 = true;
                }
                return z3;
            }
        }
        z = true;
        z3 = true;
        return z3;
    }

    @SuppressLint({"deprecation"})
    public static long getDirectorySize(Context context, File file, int i) {
        long j;
        StatFs statFs = new StatFs(file.getAbsolutePath());
        if (VERSION.SDK_INT >= 18) {
            j = statFs.getBlockSizeLong();
        } else {
            j = (long) statFs.getBlockSize();
        }
        if (j < 0) {
            j = 1;
        }
        return getDirectorySize(context, file, i, j);
    }

    public static long getDirectorySize(Context context, File file, int i, long j) {
        File[] listFiles = file.listFiles();
        long j2 = 0;
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (!file2.isDirectory()) {
                    j2 += ((file2.length() / j) + 1) * j;
                } else {
                    j2 += getDirectorySize(context, file2, i - 1);
                }
            }
        }
        if (i > 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getDirectorySize() ");
            sb.append(file.getPath());
            sb.append(StringUtils.SPACE);
            sb.append(Formatter.formatFileSize(context, j2));
            Log.m209d(str, sb.toString());
        }
        return j2;
    }

    @SuppressLint({"NewApi"})
    public static long getLastAccessTime(File file) {
        long j;
        long lastModified = file.lastModified();
        try {
            if (VERSION.SDK_INT >= 21) {
                j = Os.lstat(file.getAbsolutePath()).st_atime;
            } else {
                Field declaredField = Class.forName("libcore.io.Libcore").getDeclaredField("os");
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                }
                Object obj = declaredField.get(null);
                Object invoke = obj.getClass().getMethod("lstat", new Class[]{String.class}).invoke(obj, new Object[]{file.getAbsolutePath()});
                Field declaredField2 = invoke.getClass().getDeclaredField("st_atime");
                if (!declaredField2.isAccessible()) {
                    declaredField2.setAccessible(true);
                }
                j = declaredField2.getLong(invoke);
            }
            return j;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getLastAccessTime() failed ");
            sb.append(e.getMessage());
            sb.append(" for file ");
            sb.append(file);
            Log.m211e(str, sb.toString());
            return lastModified;
        }
    }
}
