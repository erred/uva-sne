package kotlin.collections;

import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.ArrayIteratorsKt;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "Lkotlin/collections/LongIterator;", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: _Arrays.kt */
final class ArraysKt___ArraysKt$withIndex$5 extends Lambda implements Function0<LongIterator> {
    final /* synthetic */ long[] receiver$0;

    ArraysKt___ArraysKt$withIndex$5(long[] jArr) {
        this.receiver$0 = jArr;
        super(0);
    }

    @NotNull
    public final LongIterator invoke() {
        return ArrayIteratorsKt.iterator(this.receiver$0);
    }
}
