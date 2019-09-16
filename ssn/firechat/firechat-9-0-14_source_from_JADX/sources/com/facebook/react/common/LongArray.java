package com.facebook.react.common;

public class LongArray {
    private static final double INNER_ARRAY_GROWTH_FACTOR = 1.8d;
    private long[] mArray;
    private int mLength = 0;

    public static LongArray createWithInitialCapacity(int i) {
        return new LongArray(i);
    }

    private LongArray(int i) {
        this.mArray = new long[i];
    }

    public void add(long j) {
        growArrayIfNeeded();
        long[] jArr = this.mArray;
        int i = this.mLength;
        this.mLength = i + 1;
        jArr[i] = j;
    }

    public long get(int i) {
        if (i < this.mLength) {
            return this.mArray[i];
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(i);
        sb.append(" >= ");
        sb.append(this.mLength);
        throw new IndexOutOfBoundsException(sb.toString());
    }

    public void set(int i, long j) {
        if (i >= this.mLength) {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(i);
            sb.append(" >= ");
            sb.append(this.mLength);
            throw new IndexOutOfBoundsException(sb.toString());
        }
        this.mArray[i] = j;
    }

    public int size() {
        return this.mLength;
    }

    public boolean isEmpty() {
        return this.mLength == 0;
    }

    public void dropTail(int i) {
        if (i > this.mLength) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to drop ");
            sb.append(i);
            sb.append(" items from array of length ");
            sb.append(this.mLength);
            throw new IndexOutOfBoundsException(sb.toString());
        }
        this.mLength -= i;
    }

    private void growArrayIfNeeded() {
        if (this.mLength == this.mArray.length) {
            long[] jArr = new long[Math.max(this.mLength + 1, (int) (((double) this.mLength) * INNER_ARRAY_GROWTH_FACTOR))];
            System.arraycopy(this.mArray, 0, jArr, 0, this.mLength);
            this.mArray = jArr;
        }
    }
}
