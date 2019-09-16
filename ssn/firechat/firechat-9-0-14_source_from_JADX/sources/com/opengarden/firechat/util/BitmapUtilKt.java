package com.opengarden.firechat.util;

import android.graphics.Bitmap;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0001¨\u0006\u0002"}, mo21251d2 = {"createSquareBitmap", "Landroid/graphics/Bitmap;", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: BitmapUtil.kt */
public final class BitmapUtilKt {
    @Nullable
    public static final Bitmap createSquareBitmap(@NotNull Bitmap bitmap) {
        Bitmap createBitmap;
        Intrinsics.checkParameterIsNotNull(bitmap, "$receiver");
        if (bitmap.getWidth() == bitmap.getHeight()) {
            return bitmap;
        }
        if (bitmap.getWidth() > bitmap.getHeight()) {
            try {
                createBitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth() - bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight());
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("## createSquareBitmap ");
                sb.append(e.getMessage());
                Log.m211e("BitmapUtil", sb.toString());
                return bitmap;
            }
        } else {
            try {
                createBitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() - bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth());
            } catch (Exception e2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## createSquareBitmap ");
                sb2.append(e2.getMessage());
                Log.m211e("BitmapUtil", sb2.toString());
                return bitmap;
            }
        }
        return createBitmap;
    }
}
