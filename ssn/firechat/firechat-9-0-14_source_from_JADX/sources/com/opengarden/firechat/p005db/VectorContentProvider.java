package com.opengarden.firechat.p005db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.webkit.MimeTypeMap;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;
import java.io.FileNotFoundException;

/* renamed from: com.opengarden.firechat.db.VectorContentProvider */
public class VectorContentProvider extends ContentProvider {
    private static final String AUTHORITIES = "im.vector.VectorApp.provider";
    private static final String BUG_SEPARATOR = "bugreport";
    private static final String LOG_TAG = "VectorContentProvider";

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        return null;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        return 0;
    }

    public static Uri absolutePathToUri(Context context, String str) {
        if (str == null) {
            return null;
        }
        String absolutePath = context.getFilesDir().getAbsolutePath();
        if (str.startsWith(absolutePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append("content://im.vector.VectorApp.provider");
            sb.append(str.substring(absolutePath.length()));
            return Uri.parse(sb.toString());
        }
        if (VectorApp.mLogsDirectoryFile != null) {
            String absolutePath2 = VectorApp.mLogsDirectoryFile.getAbsolutePath();
            if (str.startsWith(absolutePath2)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("content://im.vector.VectorApp.provider/bugreport");
                sb2.append(str.substring(absolutePath2.length()));
                return Uri.parse(sb2.toString());
            }
        }
        return null;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        try {
            File file = uri.getPath().contains("/bugreport/") ? VectorApp.mLogsDirectoryFile != null ? new File(VectorApp.mLogsDirectoryFile, uri.getLastPathSegment()) : null : new File(getContext().getFilesDir(), uri.getPath());
            if (file.exists()) {
                return ParcelFileDescriptor.open(file, ErrorDialogData.BINDER_CRASH);
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## openFile() failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
        return null;
    }

    public String getType(Uri uri) {
        String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(uri.toString().toLowerCase(VectorApp.getApplicationLocale()));
        if (fileExtensionFromUrl != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
        }
        return null;
    }
}
