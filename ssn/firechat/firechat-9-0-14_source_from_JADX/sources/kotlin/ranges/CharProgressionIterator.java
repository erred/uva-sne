package kotlin.ranges;

import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.collections.CharIterator;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0000\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\t\u0010\t\u001a\u00020\nH\u0002J\b\u0010\u000e\u001a\u00020\u0003H\u0016R\u000e\u0010\b\u001a\u00020\u0006X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000f"}, mo21251d2 = {"Lkotlin/ranges/CharProgressionIterator;", "Lkotlin/collections/CharIterator;", "first", "", "last", "step", "", "(CCI)V", "finalElement", "hasNext", "", "next", "getStep", "()I", "nextChar", "kotlin-runtime"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ProgressionIterators.kt */
public final class CharProgressionIterator extends CharIterator {
    private final int finalElement;
    private boolean hasNext;
    private int next;
    private final int step;

    /* JADX WARNING: Incorrect type for immutable var: ssa=char, code=int, for r3v0, types: [int, char] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public CharProgressionIterator(int r3, char r4, int r5) {
        /*
            r2 = this;
            r2.<init>()
            r2.step = r5
            r2.finalElement = r4
            int r5 = r2.step
            r0 = 0
            r1 = 1
            if (r5 <= 0) goto L_0x0010
            if (r3 > r4) goto L_0x0013
            goto L_0x0012
        L_0x0010:
            if (r3 < r4) goto L_0x0013
        L_0x0012:
            r0 = 1
        L_0x0013:
            r2.hasNext = r0
            boolean r4 = r2.hasNext
            if (r4 == 0) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            int r3 = r2.finalElement
        L_0x001c:
            r2.next = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.ranges.CharProgressionIterator.<init>(char, char, int):void");
    }

    public final int getStep() {
        return this.step;
    }

    public boolean hasNext() {
        return this.hasNext;
    }

    public char nextChar() {
        int i = this.next;
        if (i != this.finalElement) {
            this.next += this.step;
        } else if (!this.hasNext) {
            throw new NoSuchElementException();
        } else {
            this.hasNext = false;
        }
        return (char) i;
    }
}
