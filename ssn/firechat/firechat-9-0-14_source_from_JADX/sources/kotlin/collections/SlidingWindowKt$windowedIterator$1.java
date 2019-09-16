package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.experimental.Continuation;
import kotlin.coroutines.experimental.SequenceBuilder;
import kotlin.coroutines.experimental.jvm.internal.CoroutineImpl;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\n¢\u0006\u0004\b\u0005\u0010\u0006"}, mo21251d2 = {"<anonymous>", "", "T", "Lkotlin/coroutines/experimental/SequenceBuilder;", "", "invoke", "(Lkotlin/coroutines/experimental/SequenceBuilder;Lkotlin/coroutines/experimental/Continuation;)Ljava/lang/Object;"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: SlidingWindow.kt */
final class SlidingWindowKt$windowedIterator$1 extends CoroutineImpl implements Function2<SequenceBuilder<? super List<? extends T>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ Iterator $iterator;
    final /* synthetic */ boolean $partialWindows;
    final /* synthetic */ boolean $reuseBuffer;
    final /* synthetic */ int $size;
    final /* synthetic */ int $step;
    int I$0;
    int I$1;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;

    /* renamed from: p$ */
    private SequenceBuilder f145p$;

    SlidingWindowKt$windowedIterator$1(int i, int i2, Iterator it, boolean z, boolean z2, Continuation continuation) {
        this.$step = i;
        this.$size = i2;
        this.$iterator = it;
        this.$reuseBuffer = z;
        this.$partialWindows = z2;
        super(2, continuation);
    }

    @NotNull
    public final Continuation<Unit> create(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        SlidingWindowKt$windowedIterator$1 slidingWindowKt$windowedIterator$1 = new SlidingWindowKt$windowedIterator$1(this.$step, this.$size, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
        slidingWindowKt$windowedIterator$1.f145p$ = sequenceBuilder;
        return slidingWindowKt$windowedIterator$1;
    }

    @Nullable
    public final Object invoke(@NotNull SequenceBuilder<? super List<? extends T>> sequenceBuilder, @NotNull Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(sequenceBuilder, "$receiver");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        return ((SlidingWindowKt$windowedIterator$1) create(sequenceBuilder, continuation)).doResume(Unit.INSTANCE, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0088, code lost:
        if (r0.hasNext() == false) goto L_0x00c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x008a, code lost:
        r7 = r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x008e, code lost:
        if (r4 <= 0) goto L_0x0093;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0090, code lost:
        r4 = r4 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0093, code lost:
        r3.add(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x009c, code lost:
        if (r3.size() != r12.$size) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x009e, code lost:
        r12.L$0 = r5;
        r12.I$0 = r13;
        r12.L$1 = r3;
        r12.I$1 = r4;
        r12.L$2 = r7;
        r12.L$3 = r0;
        r12.label = 1;
        r4 = r5.yield(r3, r12);
        kotlin.jvm.internal.InlineMarker.mark(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b3, code lost:
        if (r4 != r6) goto L_0x00b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b5, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00b8, code lost:
        if (r12.$reuseBuffer == false) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ba, code lost:
        r3.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00be, code lost:
        r3 = new java.util.ArrayList(r12.$size);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c5, code lost:
        r4 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00cf, code lost:
        if ((!r3.isEmpty()) == false) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d3, code lost:
        if (r12.$partialWindows != false) goto L_0x00dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00db, code lost:
        if (r3.size() != r12.$size) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00dd, code lost:
        r12.I$0 = r13;
        r12.L$0 = r3;
        r12.I$1 = r4;
        r12.label = 2;
        r12 = r5.yield(r3, r12);
        kotlin.jvm.internal.InlineMarker.mark(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ec, code lost:
        if (r12 != r6) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ee, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0102, code lost:
        if (r0.hasNext() == false) goto L_0x0140;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0104, code lost:
        r6 = r0.next();
        r3.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x010f, code lost:
        if (r3.isFull() == false) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0113, code lost:
        if (r12.$reuseBuffer == false) goto L_0x0119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x0115, code lost:
        r7 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x0119, code lost:
        r7 = new java.util.ArrayList(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0123, code lost:
        r12.L$0 = r5;
        r12.I$0 = r4;
        r12.L$1 = r3;
        r12.L$2 = r6;
        r12.L$3 = r0;
        r12.label = 3;
        r6 = r5.yield(r7, r12);
        kotlin.jvm.internal.InlineMarker.mark(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0137, code lost:
        if (r6 != r13) goto L_0x013a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0139, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x013a, code lost:
        r3.removeFirst(r12.$step);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0142, code lost:
        if (r12.$partialWindows == false) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0144, code lost:
        r0 = r3;
        r3 = r4;
        r4 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x014d, code lost:
        if (r0.size() <= r12.$step) goto L_0x017a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0151, code lost:
        if (r12.$reuseBuffer == false) goto L_0x0157;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0153, code lost:
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0157, code lost:
        r5 = new java.util.ArrayList(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0161, code lost:
        r12.L$0 = r4;
        r12.I$0 = r3;
        r12.L$1 = r0;
        r12.label = 4;
        r5 = r4.yield(r5, r12);
        kotlin.jvm.internal.InlineMarker.mark(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0171, code lost:
        if (r5 != r13) goto L_0x0174;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0173, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0174, code lost:
        r0.removeFirst(r12.$step);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0182, code lost:
        if ((true ^ r0.isEmpty()) == false) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0184, code lost:
        r12.I$0 = r3;
        r12.L$0 = r0;
        r12.label = 5;
        r12 = r4.yield(r0, r12);
        kotlin.jvm.internal.InlineMarker.mark(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x0192, code lost:
        if (r12 != r13) goto L_0x0195;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0194, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0197, code lost:
        return kotlin.Unit.INSTANCE;
     */
    @org.jetbrains.annotations.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final java.lang.Object doResume(@org.jetbrains.annotations.Nullable java.lang.Object r12, @org.jetbrains.annotations.Nullable java.lang.Throwable r13) {
        /*
            r11 = this;
            java.lang.Object r12 = kotlin.coroutines.experimental.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r0 = r11.label
            r1 = 1
            r2 = 2
            switch(r0) {
                case 0: goto L_0x0068;
                case 1: goto L_0x004f;
                case 2: goto L_0x0044;
                case 3: goto L_0x002d;
                case 4: goto L_0x001c;
                case 5: goto L_0x0013;
                default: goto L_0x000b;
            }
        L_0x000b:
            java.lang.IllegalStateException r12 = new java.lang.IllegalStateException
            java.lang.String r13 = "call to 'resume' before 'invoke' with coroutine"
            r12.<init>(r13)
            throw r12
        L_0x0013:
            java.lang.Object r12 = r11.L$0
            kotlin.collections.RingBuffer r12 = (kotlin.collections.RingBuffer) r12
            int r12 = r11.I$0
            if (r13 == 0) goto L_0x0195
            throw r13
        L_0x001c:
            java.lang.Object r0 = r11.L$1
            kotlin.collections.RingBuffer r0 = (kotlin.collections.RingBuffer) r0
            int r3 = r11.I$0
            java.lang.Object r4 = r11.L$0
            kotlin.coroutines.experimental.SequenceBuilder r4 = (kotlin.coroutines.experimental.SequenceBuilder) r4
            if (r13 == 0) goto L_0x0029
            throw r13
        L_0x0029:
            r13 = r12
            r12 = r11
            goto L_0x0174
        L_0x002d:
            java.lang.Object r0 = r11.L$3
            java.util.Iterator r0 = (java.util.Iterator) r0
            java.lang.Object r3 = r11.L$2
            java.lang.Object r3 = r11.L$1
            kotlin.collections.RingBuffer r3 = (kotlin.collections.RingBuffer) r3
            int r4 = r11.I$0
            java.lang.Object r5 = r11.L$0
            kotlin.coroutines.experimental.SequenceBuilder r5 = (kotlin.coroutines.experimental.SequenceBuilder) r5
            if (r13 == 0) goto L_0x0040
            throw r13
        L_0x0040:
            r13 = r12
            r12 = r11
            goto L_0x013a
        L_0x0044:
            int r12 = r11.I$1
            java.lang.Object r12 = r11.L$0
            java.util.ArrayList r12 = (java.util.ArrayList) r12
            int r12 = r11.I$0
            if (r13 == 0) goto L_0x0195
            throw r13
        L_0x004f:
            java.lang.Object r0 = r11.L$3
            java.util.Iterator r0 = (java.util.Iterator) r0
            java.lang.Object r3 = r11.L$2
            int r3 = r11.I$1
            java.lang.Object r3 = r11.L$1
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            int r4 = r11.I$0
            java.lang.Object r5 = r11.L$0
            kotlin.coroutines.experimental.SequenceBuilder r5 = (kotlin.coroutines.experimental.SequenceBuilder) r5
            if (r13 == 0) goto L_0x0064
            throw r13
        L_0x0064:
            r6 = r12
            r13 = r4
            r12 = r11
            goto L_0x00b6
        L_0x0068:
            if (r13 == 0) goto L_0x006b
            throw r13
        L_0x006b:
            kotlin.coroutines.experimental.SequenceBuilder r13 = r11.f145p$
            int r0 = r11.$step
            int r3 = r11.$size
            int r0 = r0 - r3
            if (r0 < 0) goto L_0x00ef
            java.util.ArrayList r3 = new java.util.ArrayList
            int r4 = r11.$size
            r3.<init>(r4)
            r4 = 0
            java.util.Iterator r5 = r11.$iterator
            r6 = r12
            r12 = r11
            r10 = r5
            r5 = r13
            r13 = r0
            r0 = r10
        L_0x0084:
            boolean r7 = r0.hasNext()
            if (r7 == 0) goto L_0x00c7
            java.lang.Object r7 = r0.next()
            if (r4 <= 0) goto L_0x0093
            int r4 = r4 + -1
            goto L_0x0084
        L_0x0093:
            r3.add(r7)
            int r8 = r3.size()
            int r9 = r12.$size
            if (r8 != r9) goto L_0x0084
            r12.L$0 = r5
            r12.I$0 = r13
            r12.L$1 = r3
            r12.I$1 = r4
            r12.L$2 = r7
            r12.L$3 = r0
            r12.label = r1
            java.lang.Object r4 = r5.yield(r3, r12)
            kotlin.jvm.internal.InlineMarker.mark(r2)
            if (r4 != r6) goto L_0x00b6
            return r6
        L_0x00b6:
            boolean r4 = r12.$reuseBuffer
            if (r4 == 0) goto L_0x00be
            r3.clear()
            goto L_0x00c5
        L_0x00be:
            java.util.ArrayList r3 = new java.util.ArrayList
            int r4 = r12.$size
            r3.<init>(r4)
        L_0x00c5:
            r4 = r13
            goto L_0x0084
        L_0x00c7:
            r0 = r3
            java.util.Collection r0 = (java.util.Collection) r0
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r1
            if (r0 == 0) goto L_0x0195
            boolean r0 = r12.$partialWindows
            if (r0 != 0) goto L_0x00dd
            int r0 = r3.size()
            int r1 = r12.$size
            if (r0 != r1) goto L_0x0195
        L_0x00dd:
            r12.I$0 = r13
            r12.L$0 = r3
            r12.I$1 = r4
            r12.label = r2
            java.lang.Object r12 = r5.yield(r3, r12)
            kotlin.jvm.internal.InlineMarker.mark(r2)
            if (r12 != r6) goto L_0x0195
            return r6
        L_0x00ef:
            kotlin.collections.RingBuffer r3 = new kotlin.collections.RingBuffer
            int r4 = r11.$size
            r3.<init>(r4)
            java.util.Iterator r4 = r11.$iterator
            r5 = r13
            r13 = r12
            r12 = r11
            r10 = r4
            r4 = r0
            r0 = r10
        L_0x00fe:
            boolean r6 = r0.hasNext()
            if (r6 == 0) goto L_0x0140
            java.lang.Object r6 = r0.next()
            r3.add(r6)
            boolean r7 = r3.isFull()
            if (r7 == 0) goto L_0x00fe
            boolean r7 = r12.$reuseBuffer
            if (r7 == 0) goto L_0x0119
            r7 = r3
            java.util.List r7 = (java.util.List) r7
            goto L_0x0123
        L_0x0119:
            java.util.ArrayList r7 = new java.util.ArrayList
            r8 = r3
            java.util.Collection r8 = (java.util.Collection) r8
            r7.<init>(r8)
            java.util.List r7 = (java.util.List) r7
        L_0x0123:
            r12.L$0 = r5
            r12.I$0 = r4
            r12.L$1 = r3
            r12.L$2 = r6
            r12.L$3 = r0
            r6 = 3
            r12.label = r6
            java.lang.Object r6 = r5.yield(r7, r12)
            kotlin.jvm.internal.InlineMarker.mark(r2)
            if (r6 != r13) goto L_0x013a
            return r13
        L_0x013a:
            int r6 = r12.$step
            r3.removeFirst(r6)
            goto L_0x00fe
        L_0x0140:
            boolean r0 = r12.$partialWindows
            if (r0 == 0) goto L_0x0195
            r0 = r3
            r3 = r4
            r4 = r5
        L_0x0147:
            int r5 = r0.size()
            int r6 = r12.$step
            if (r5 <= r6) goto L_0x017a
            boolean r5 = r12.$reuseBuffer
            if (r5 == 0) goto L_0x0157
            r5 = r0
            java.util.List r5 = (java.util.List) r5
            goto L_0x0161
        L_0x0157:
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = r0
            java.util.Collection r6 = (java.util.Collection) r6
            r5.<init>(r6)
            java.util.List r5 = (java.util.List) r5
        L_0x0161:
            r12.L$0 = r4
            r12.I$0 = r3
            r12.L$1 = r0
            r6 = 4
            r12.label = r6
            java.lang.Object r5 = r4.yield(r5, r12)
            kotlin.jvm.internal.InlineMarker.mark(r2)
            if (r5 != r13) goto L_0x0174
            return r13
        L_0x0174:
            int r5 = r12.$step
            r0.removeFirst(r5)
            goto L_0x0147
        L_0x017a:
            r5 = r0
            java.util.Collection r5 = (java.util.Collection) r5
            boolean r5 = r5.isEmpty()
            r1 = r1 ^ r5
            if (r1 == 0) goto L_0x0195
            r12.I$0 = r3
            r12.L$0 = r0
            r1 = 5
            r12.label = r1
            java.lang.Object r12 = r4.yield(r0, r12)
            kotlin.jvm.internal.InlineMarker.mark(r2)
            if (r12 != r13) goto L_0x0195
            return r13
        L_0x0195:
            kotlin.Unit r12 = kotlin.Unit.INSTANCE
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt$windowedIterator$1.doResume(java.lang.Object, java.lang.Throwable):java.lang.Object");
    }
}
