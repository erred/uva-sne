package kotlin.collections;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\n\n\u0000\n\u0002\u0010(\n\u0002\b\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002H\n¢\u0006\u0002\b\u0003"}, mo21251d2 = {"<anonymous>", "", "T", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: _Arrays.kt */
final class ArraysKt___ArraysKt$withIndex$1 extends Lambda implements Function0<Iterator<? extends T>> {
    final /* synthetic */ Object[] receiver$0;

    ArraysKt___ArraysKt$withIndex$1(Object[] objArr) {
        this.receiver$0 = objArr;
        super(0);
    }

    @NotNull
    public final Iterator<T> invoke() {
        return ArrayIteratorKt.iterator(this.receiver$0);
    }
}
