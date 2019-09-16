package androidx.core.util;

import android.support.annotation.RequiresApi;
import android.util.Half;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\n\n\u0002\u0010\u0006\n\u0002\u0010\u0007\n\u0002\u0010\u000e\n\u0000\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\b\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0003H\b\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0004H\b\u001a\r\u0010\u0000\u001a\u00020\u0001*\u00020\u0005H\b¨\u0006\u0006"}, mo21251d2 = {"toHalf", "Landroid/util/Half;", "", "", "", "", "core-ktx_release"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: Half.kt */
public final class HalfKt {
    @NotNull
    @RequiresApi(26)
    public static final Half toHalf(short s) {
        Half valueOf = Half.valueOf(s);
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "Half.valueOf(this)");
        return valueOf;
    }

    @NotNull
    @RequiresApi(26)
    public static final Half toHalf(float f) {
        Half valueOf = Half.valueOf(f);
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "Half.valueOf(this)");
        return valueOf;
    }

    @NotNull
    @RequiresApi(26)
    public static final Half toHalf(@NotNull String str) {
        Half valueOf = Half.valueOf(str);
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "Half.valueOf(this)");
        return valueOf;
    }

    @NotNull
    @RequiresApi(26)
    public static final Half toHalf(double d) {
        Half valueOf = Half.valueOf((float) d);
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "Half.valueOf(this)");
        return valueOf;
    }
}
