package com.opengarden.firechat.matrixsdk.util;

import android.os.Build.VERSION;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class CompatUtil {
    public static GZIPOutputStream createGzipOutputStream(FileOutputStream fileOutputStream) throws IOException {
        if (VERSION.SDK_INT == 19) {
            return new GZIPOutputStream(fileOutputStream, false);
        }
        return new GZIPOutputStream(fileOutputStream);
    }
}
