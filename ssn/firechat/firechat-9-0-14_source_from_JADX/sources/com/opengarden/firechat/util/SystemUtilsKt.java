package com.opengarden.firechat.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000 \n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\u001a\u000e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007¨\u0006\n"}, mo21251d2 = {"isIgnoringBatteryOptimizations", "", "context", "Landroid/content/Context;", "requestDisablingBatteryOptimization", "", "activity", "Landroid/app/Activity;", "requestCode", "", "vector_appfirechatRelease"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: SystemUtils.kt */
public final class SystemUtilsKt {
    public static final boolean isIgnoringBatteryOptimizations(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        return powerManager != null && powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    @TargetApi(23)
    public static final void requestDisablingBatteryOptimization(@NotNull Activity activity, int i) {
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        Intent intent = new Intent();
        intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
        StringBuilder sb = new StringBuilder();
        sb.append("package:");
        sb.append(activity.getPackageName());
        intent.setData(Uri.parse(sb.toString()));
        activity.startActivityForResult(intent, i);
    }
}
