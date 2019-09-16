package androidx.core.content;

import android.content.ContentValues;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.text.Typography;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0000\n\u0002\b\u0002\u001a;\u0010\u0000\u001a\u00020\u00012.\u0010\u0002\u001a\u0018\u0012\u0014\b\u0001\u0012\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u00040\u0003\"\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0004¢\u0006\u0002\u0010\u0007¨\u0006\b"}, mo21251d2 = {"contentValuesOf", "Landroid/content/ContentValues;", "pairs", "", "Lkotlin/Pair;", "", "", "([Lkotlin/Pair;)Landroid/content/ContentValues;", "core-ktx_release"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: ContentValues.kt */
public final class ContentValuesKt {
    @NotNull
    public static final ContentValues contentValuesOf(@NotNull Pair<String, ? extends Object>... pairArr) {
        ContentValues contentValues = new ContentValues(((Object[]) pairArr).length);
        for (Pair<String, ? extends Object> pair : pairArr) {
            String str = (String) pair.component1();
            Object component2 = pair.component2();
            if (component2 == null) {
                contentValues.putNull(str);
            } else if (component2 instanceof String) {
                contentValues.put(str, (String) component2);
            } else if (component2 instanceof Integer) {
                contentValues.put(str, (Integer) component2);
            } else if (component2 instanceof Long) {
                contentValues.put(str, (Long) component2);
            } else if (component2 instanceof Boolean) {
                contentValues.put(str, (Boolean) component2);
            } else if (component2 instanceof Float) {
                contentValues.put(str, (Float) component2);
            } else if (component2 instanceof Double) {
                contentValues.put(str, (Double) component2);
            } else if (component2 instanceof byte[]) {
                contentValues.put(str, (byte[]) component2);
            } else if (component2 instanceof Byte) {
                contentValues.put(str, (Byte) component2);
            } else if (component2 instanceof Short) {
                contentValues.put(str, (Short) component2);
            } else {
                String canonicalName = component2.getClass().getCanonicalName();
                StringBuilder sb = new StringBuilder();
                sb.append("Illegal value type ");
                sb.append(canonicalName);
                sb.append(" for key \"");
                sb.append(str);
                sb.append(Typography.quote);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        return contentValues;
    }
}
