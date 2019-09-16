package com.opengarden.firechat.util;

import android.content.Context;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005R*\u0010\u0003\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004j\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005`\u0006X\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, mo21251d2 = {"Lcom/opengarden/firechat/util/AssetReader;", "", "()V", "cache", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "clearCache", "", "readAssetFile", "context", "Landroid/content/Context;", "assetFilename", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: AssetReader.kt */
public final class AssetReader {
    public static final AssetReader INSTANCE = new AssetReader();
    private static final HashMap<String, String> cache = new HashMap<>();

    private AssetReader() {
    }

    @Nullable
    public final String readAssetFile(@NotNull Context context, @NotNull String str) {
        String str2;
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "assetFilename");
        if (cache.containsKey(str)) {
            return (String) cache.get(str);
        }
        String str3 = null;
        try {
            InputStream open = context.getAssets().open(str);
            char[] cArr = new char[1024];
            StringBuilder sb = new StringBuilder();
            InputStreamReader inputStreamReader = new InputStreamReader(open, "UTF-8");
            while (true) {
                int read = inputStreamReader.read(cArr, 0, cArr.length);
                if (read < 0) {
                    break;
                }
                sb.append(cArr, 0, read);
            }
            str2 = sb.toString();
            try {
                cache.put(str, str2);
                inputStreamReader.close();
                open.close();
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
            str2 = str3;
        }
        return str2;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## readAssetFile() failed : ");
        sb2.append(e.getMessage());
        Log.m211e("AssetReader", sb2.toString());
        return str2;
    }

    public final void clearCache() {
        cache.clear();
    }
}
