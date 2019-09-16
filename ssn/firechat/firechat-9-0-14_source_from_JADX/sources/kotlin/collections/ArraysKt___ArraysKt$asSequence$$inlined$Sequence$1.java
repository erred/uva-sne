package kotlin.collections;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0017\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\u0010(\n\u0000*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0004H\u0002¨\u0006\u0005¸\u0006\u0000"}, mo21251d2 = {"kotlin/sequences/SequencesKt__SequencesKt$Sequence$1", "Lkotlin/sequences/Sequence;", "(Lkotlin/jvm/functions/Function0;)V", "iterator", "", "kotlin-stdlib"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: Sequences.kt */
public final class ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1 implements Sequence<T> {
    final /* synthetic */ Object[] receiver$0$inlined;

    public ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1(Object[] objArr) {
        this.receiver$0$inlined = objArr;
    }

    @NotNull
    public Iterator<T> iterator() {
        return ArrayIteratorKt.iterator(this.receiver$0$inlined);
    }
}
