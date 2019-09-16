package androidx.core.p004os;

import android.os.Build.VERSION;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Typography;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\b\u0002\u001a=\u0010\u0000\u001a\u00020\u00012.\u0010\u0002\u001a\u0018\u0012\u0014\b\u0001\u0012\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u00040\u0003\"\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0004H\u0007¢\u0006\u0002\u0010\u0007¨\u0006\b"}, mo21251d2 = {"persistableBundleOf", "Landroid/os/PersistableBundle;", "pairs", "", "Lkotlin/Pair;", "", "", "([Lkotlin/Pair;)Landroid/os/PersistableBundle;", "core-ktx_release"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* renamed from: androidx.core.os.PersistableBundleKt */
/* compiled from: PersistableBundle.kt */
public final class PersistableBundleKt {
    @NotNull
    @RequiresApi(21)
    public static final PersistableBundle persistableBundleOf(@NotNull Pair<String, ? extends Object>... pairArr) {
        PersistableBundle persistableBundle = new PersistableBundle(((Object[]) pairArr).length);
        for (Pair<String, ? extends Object> pair : pairArr) {
            String str = (String) pair.component1();
            Object component2 = pair.component2();
            if (component2 == null) {
                persistableBundle.putString(str, null);
            } else if (component2 instanceof Boolean) {
                if (VERSION.SDK_INT >= 22) {
                    persistableBundle.putBoolean(str, ((Boolean) component2).booleanValue());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Illegal value type boolean for key \"");
                    sb.append(str);
                    sb.append(Typography.quote);
                    throw new IllegalArgumentException(sb.toString());
                }
            } else if (component2 instanceof Double) {
                persistableBundle.putDouble(str, ((Number) component2).doubleValue());
            } else if (component2 instanceof Integer) {
                persistableBundle.putInt(str, ((Number) component2).intValue());
            } else if (component2 instanceof Long) {
                persistableBundle.putLong(str, ((Number) component2).longValue());
            } else if (component2 instanceof String) {
                persistableBundle.putString(str, (String) component2);
            } else if (component2 instanceof boolean[]) {
                if (VERSION.SDK_INT >= 22) {
                    persistableBundle.putBooleanArray(str, (boolean[]) component2);
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Illegal value type boolean[] for key \"");
                    sb2.append(str);
                    sb2.append(Typography.quote);
                    throw new IllegalArgumentException(sb2.toString());
                }
            } else if (component2 instanceof double[]) {
                persistableBundle.putDoubleArray(str, (double[]) component2);
            } else if (component2 instanceof int[]) {
                persistableBundle.putIntArray(str, (int[]) component2);
            } else if (component2 instanceof long[]) {
                persistableBundle.putLongArray(str, (long[]) component2);
            } else if (component2 instanceof Object[]) {
                Class componentType = component2.getClass().getComponentType();
                if (!String.class.isAssignableFrom(componentType)) {
                    Intrinsics.checkExpressionValueIsNotNull(componentType, "componentType");
                    String canonicalName = componentType.getCanonicalName();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Illegal value array type ");
                    sb3.append(canonicalName);
                    sb3.append(" for key \"");
                    sb3.append(str);
                    sb3.append(Typography.quote);
                    throw new IllegalArgumentException(sb3.toString());
                } else if (component2 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<kotlin.String>");
                } else {
                    persistableBundle.putStringArray(str, (String[]) component2);
                }
            } else {
                String canonicalName2 = component2.getClass().getCanonicalName();
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Illegal value type ");
                sb4.append(canonicalName2);
                sb4.append(" for key \"");
                sb4.append(str);
                sb4.append(Typography.quote);
                throw new IllegalArgumentException(sb4.toString());
            }
        }
        return persistableBundle;
    }
}
