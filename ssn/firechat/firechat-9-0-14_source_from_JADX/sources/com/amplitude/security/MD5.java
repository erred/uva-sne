package com.amplitude.security;

import com.facebook.imagepipeline.memory.BitmapCounterProvider;
import com.facebook.imageutils.JfifUtil;
import java.security.DigestException;
import java.security.MessageDigest;

public final class MD5 extends MessageDigest implements Cloneable {
    private long bytes;

    /* renamed from: hA */
    private int f39hA;

    /* renamed from: hB */
    private int f40hB;

    /* renamed from: hC */
    private int f41hC;

    /* renamed from: hD */
    private int f42hD;
    private byte[] pad = new byte[64];
    private int padded;

    public int engineGetDigestLength() {
        return 16;
    }

    public MD5() {
        super("MD5");
        init();
    }

    public Object clone() throws CloneNotSupportedException {
        MD5 md5 = (MD5) super.clone();
        md5.pad = (byte[]) this.pad.clone();
        return md5;
    }

    public void engineReset() {
        this.padded = 0;
        this.bytes = 0;
        byte[] bArr = this.pad;
        int i = 60;
        do {
            bArr[i - 4] = 0;
            bArr[i - 3] = 0;
            bArr[i - 2] = 0;
            bArr[i - 1] = 0;
            bArr[i] = 0;
            bArr[i + 1] = 0;
            bArr[i + 2] = 0;
            bArr[i + 3] = 0;
            i -= 8;
        } while (i >= 0);
        init();
    }

    /* access modifiers changed from: protected */
    public void init() {
        this.f39hA = 1732584193;
        this.f40hB = -271733879;
        this.f41hC = -1732584194;
        this.f42hD = 271733878;
    }

    public void engineUpdate(byte b) {
        this.bytes++;
        if (this.padded < 63) {
            byte[] bArr = this.pad;
            int i = this.padded;
            this.padded = i + 1;
            bArr[i] = b;
            return;
        }
        this.pad[63] = b;
        engineUpdate(this.pad, this.padded);
        this.padded = 0;
    }

    public void engineUpdate(byte[] bArr, int i, int i2) {
        if (i < 0 || i2 < 0 || i + i2 > bArr.length) {
            throw new ArrayIndexOutOfBoundsException(i);
        }
        this.bytes += (long) i2;
        if (this.padded > 0 && this.padded + i2 >= 64) {
            int i3 = 64 - this.padded;
            System.arraycopy(bArr, i, this.pad, this.padded, i3);
            byte[] bArr2 = this.pad;
            this.padded = 0;
            engineUpdate(bArr2, 0);
            i += i3;
            i2 -= i3;
        }
        while (i2 >= 512) {
            engineUpdate(bArr, i);
            engineUpdate(bArr, i + 64);
            engineUpdate(bArr, i + 128);
            engineUpdate(bArr, i + JfifUtil.MARKER_SOFn);
            engineUpdate(bArr, i + 256);
            engineUpdate(bArr, i + 320);
            engineUpdate(bArr, i + BitmapCounterProvider.MAX_BITMAP_COUNT);
            engineUpdate(bArr, i + 448);
            i += 512;
            i2 -= 512;
        }
        while (i2 >= 64) {
            engineUpdate(bArr, i);
            i += 64;
            i2 -= 64;
        }
        if (i2 > 0) {
            System.arraycopy(bArr, i, this.pad, this.padded, i2);
            this.padded += i2;
        }
    }

    public byte[] engineDigest() {
        try {
            byte[] bArr = new byte[16];
            engineDigest(bArr, 0, 16);
            return bArr;
        } catch (DigestException unused) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002b, code lost:
        r15[61] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002d, code lost:
        r15[62] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
        r15[63] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0031, code lost:
        engineUpdate(r15, 0);
        r1 = -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
        switch((r1 & 7)) {
            case 0: goto L_0x009f;
            case 1: goto L_0x0086;
            case 2: goto L_0x0071;
            case 3: goto L_0x0060;
            case 4: goto L_0x0053;
            case 5: goto L_0x0048;
            case 6: goto L_0x0040;
            case 7: goto L_0x003c;
            default: goto L_0x003a;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003c, code lost:
        r1 = r1 - 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0040, code lost:
        r1 = r1 - 2;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0048, code lost:
        r1 = r1 - 1;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0053, code lost:
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0060, code lost:
        r1 = r1 + 1;
        r15[r1] = 0;
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0071, code lost:
        r1 = r1 + 2;
        r15[r1 - 1] = 0;
        r15[r1] = 0;
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0086, code lost:
        r1 = r1 + 3;
        r15[r1 - 2] = 0;
        r15[r1 - 1] = 0;
        r15[r1] = 0;
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x009f, code lost:
        r1 = r1 + 4;
        r15[r1 - 3] = 0;
        r15[r1 - 2] = 0;
        r15[r1 - 1] = 0;
        r15[r1] = 0;
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00bb, code lost:
        r1 = r1 + 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00bf, code lost:
        if (r1 > 52) goto L_0x00e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00c1, code lost:
        r15[r1 - 4] = 0;
        r15[r1 - 3] = 0;
        r15[r1 - 2] = 0;
        r15[r1 - 1] = 0;
        r15[r1] = 0;
        r15[r1 + 1] = 0;
        r15[r1 + 2] = 0;
        r15[r1 + 3] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00e0, code lost:
        r10 = ((int) r12.bytes) << 3;
        r15[56] = (byte) r10;
        r15[57] = (byte) (r10 >>> 8);
        r15[58] = (byte) (r10 >>> 16);
        r15[59] = (byte) (r10 >>> 24);
        r1 = (int) (r12.bytes >>> 29);
        r15[60] = (byte) r1;
        r15[61] = (byte) (r1 >>> 8);
        r15[62] = (byte) (r1 >>> 16);
        r15[63] = (byte) (r1 >>> 24);
        engineUpdate(r15, 0);
        r15 = r12.f39hA;
        r13[r14] = (byte) r15;
        r13[r14 + 1] = (byte) (r15 >>> 8);
        r13[r14 + 2] = (byte) (r15 >>> 16);
        r13[r14 + 3] = (byte) (r15 >>> 24);
        r15 = r14 + 4;
        r1 = r12.f40hB;
        r13[r15] = (byte) r1;
        r13[r14 + 5] = (byte) (r1 >>> 8);
        r14 = r14 + 10;
        r13[r14 - 4] = (byte) (r1 >>> 16);
        r13[r14 - 3] = (byte) (r1 >>> 24);
        r15 = r14 - 2;
        r1 = r12.f41hC;
        r13[r15] = (byte) r1;
        r13[r14 - 1] = (byte) (r1 >>> 8);
        r13[r14] = (byte) (r1 >>> 16);
        r13[r14 + 1] = (byte) (r1 >>> 24);
        r15 = r14 + 2;
        r1 = r12.f42hD;
        r13[r15] = (byte) r1;
        r13[r14 + 3] = (byte) (r1 >>> 8);
        r13[r14 + 4] = (byte) (r1 >>> 16);
        r13[r14 + 5] = (byte) (r1 >>> 24);
        engineReset();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0185, code lost:
        return 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0025, code lost:
        r15[58] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        r15[59] = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0029, code lost:
        r15[60] = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int engineDigest(byte[] r13, int r14, int r15) throws java.security.DigestException {
        /*
            r12 = this;
            r0 = 16
            if (r15 < r0) goto L_0x018e
            int r15 = r13.length
            int r15 = r15 - r14
            if (r15 < r0) goto L_0x0186
            byte[] r15 = r12.pad
            int r1 = r12.padded
            r2 = -128(0xffffffffffffff80, float:NaN)
            r15[r1] = r2
            r2 = 63
            r3 = 62
            r4 = 61
            r5 = 60
            r6 = 59
            r7 = 58
            r8 = 57
            r9 = 0
            switch(r1) {
                case 56: goto L_0x0023;
                case 57: goto L_0x0025;
                case 58: goto L_0x0027;
                case 59: goto L_0x0029;
                case 60: goto L_0x002b;
                case 61: goto L_0x002d;
                case 62: goto L_0x002f;
                case 63: goto L_0x0031;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x0035
        L_0x0023:
            r15[r8] = r9
        L_0x0025:
            r15[r7] = r9
        L_0x0027:
            r15[r6] = r9
        L_0x0029:
            r15[r5] = r9
        L_0x002b:
            r15[r4] = r9
        L_0x002d:
            r15[r3] = r9
        L_0x002f:
            r15[r2] = r9
        L_0x0031:
            r12.engineUpdate(r15, r9)
            r1 = -1
        L_0x0035:
            r10 = r1 & 7
            switch(r10) {
                case 0: goto L_0x009f;
                case 1: goto L_0x0086;
                case 2: goto L_0x0071;
                case 3: goto L_0x0060;
                case 4: goto L_0x0053;
                case 5: goto L_0x0048;
                case 6: goto L_0x0040;
                case 7: goto L_0x003c;
                default: goto L_0x003a;
            }
        L_0x003a:
            goto L_0x00bb
        L_0x003c:
            int r1 = r1 + -3
            goto L_0x00bb
        L_0x0040:
            int r1 = r1 + -2
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x0048:
            int r1 = r1 + -1
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x0053:
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x0060:
            int r1 = r1 + 1
            r15[r1] = r9
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x0071:
            int r1 = r1 + 2
            int r10 = r1 + -1
            r15[r10] = r9
            r15[r1] = r9
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x0086:
            int r1 = r1 + 3
            int r10 = r1 + -2
            r15[r10] = r9
            int r10 = r1 + -1
            r15[r10] = r9
            r15[r1] = r9
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x009f:
            int r1 = r1 + 4
            int r10 = r1 + -3
            r15[r10] = r9
            int r10 = r1 + -2
            r15[r10] = r9
            int r10 = r1 + -1
            r15[r10] = r9
            r15[r1] = r9
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
        L_0x00bb:
            int r1 = r1 + 8
            r10 = 52
            if (r1 > r10) goto L_0x00e0
            int r10 = r1 + -4
            r15[r10] = r9
            int r10 = r1 + -3
            r15[r10] = r9
            int r10 = r1 + -2
            r15[r10] = r9
            int r10 = r1 + -1
            r15[r10] = r9
            r15[r1] = r9
            int r10 = r1 + 1
            r15[r10] = r9
            int r10 = r1 + 2
            r15[r10] = r9
            int r10 = r1 + 3
            r15[r10] = r9
            goto L_0x00bb
        L_0x00e0:
            r1 = 56
            long r10 = r12.bytes
            int r10 = (int) r10
            int r10 = r10 << 3
            byte r11 = (byte) r10
            r15[r1] = r11
            int r1 = r10 >>> 8
            byte r1 = (byte) r1
            r15[r8] = r1
            int r1 = r10 >>> 16
            byte r1 = (byte) r1
            r15[r7] = r1
            int r1 = r10 >>> 24
            byte r1 = (byte) r1
            r15[r6] = r1
            long r6 = r12.bytes
            r1 = 29
            long r6 = r6 >>> r1
            int r1 = (int) r6
            byte r6 = (byte) r1
            r15[r5] = r6
            int r5 = r1 >>> 8
            byte r5 = (byte) r5
            r15[r4] = r5
            int r4 = r1 >>> 16
            byte r4 = (byte) r4
            r15[r3] = r4
            int r1 = r1 >>> 24
            byte r1 = (byte) r1
            r15[r2] = r1
            r12.engineUpdate(r15, r9)
            int r15 = r12.f39hA
            byte r1 = (byte) r15
            r13[r14] = r1
            int r1 = r14 + 1
            int r2 = r15 >>> 8
            byte r2 = (byte) r2
            r13[r1] = r2
            int r1 = r14 + 2
            int r2 = r15 >>> 16
            byte r2 = (byte) r2
            r13[r1] = r2
            int r1 = r14 + 3
            int r15 = r15 >>> 24
            byte r15 = (byte) r15
            r13[r1] = r15
            int r15 = r14 + 4
            int r1 = r12.f40hB
            byte r2 = (byte) r1
            r13[r15] = r2
            int r15 = r14 + 5
            int r2 = r1 >>> 8
            byte r2 = (byte) r2
            r13[r15] = r2
            int r14 = r14 + 10
            int r15 = r14 + -4
            int r2 = r1 >>> 16
            byte r2 = (byte) r2
            r13[r15] = r2
            int r15 = r14 + -3
            int r1 = r1 >>> 24
            byte r1 = (byte) r1
            r13[r15] = r1
            int r15 = r14 + -2
            int r1 = r12.f41hC
            byte r2 = (byte) r1
            r13[r15] = r2
            int r15 = r14 + -1
            int r2 = r1 >>> 8
            byte r2 = (byte) r2
            r13[r15] = r2
            int r15 = r1 >>> 16
            byte r15 = (byte) r15
            r13[r14] = r15
            int r15 = r14 + 1
            int r1 = r1 >>> 24
            byte r1 = (byte) r1
            r13[r15] = r1
            int r15 = r14 + 2
            int r1 = r12.f42hD
            byte r2 = (byte) r1
            r13[r15] = r2
            int r15 = r14 + 3
            int r2 = r1 >>> 8
            byte r2 = (byte) r2
            r13[r15] = r2
            int r15 = r14 + 4
            int r2 = r1 >>> 16
            byte r2 = (byte) r2
            r13[r15] = r2
            int r14 = r14 + 5
            int r15 = r1 >>> 24
            byte r15 = (byte) r15
            r13[r14] = r15
            r12.engineReset()
            return r0
        L_0x0186:
            java.security.DigestException r13 = new java.security.DigestException
            java.lang.String r14 = "insufficient space in output buffer to store the digest"
            r13.<init>(r14)
            throw r13
        L_0x018e:
            java.security.DigestException r13 = new java.security.DigestException
            java.lang.String r14 = "partial digests not returned"
            r13.<init>(r14)
            throw r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.security.MD5.engineDigest(byte[], int, int):int");
    }

    /* JADX WARNING: type inference failed for: r27v0, types: [byte[]] */
    /* JADX WARNING: type inference failed for: r7v0, types: [byte] */
    /* JADX WARNING: type inference failed for: r8v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r8v5, types: [byte] */
    /* JADX WARNING: type inference failed for: r8v9, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r9v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v5, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v9, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r10v13, types: [byte] */
    /* JADX WARNING: type inference failed for: r11v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r11v4, types: [byte] */
    /* JADX WARNING: type inference failed for: r11v8, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r11v12, types: [byte] */
    /* JADX WARNING: type inference failed for: r12v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r12v5, types: [byte] */
    /* JADX WARNING: type inference failed for: r12v9, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r13v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r14v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r14v5, types: [byte] */
    /* JADX WARNING: type inference failed for: r14v9, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r14v12, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v5, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v9, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v13, types: [byte] */
    /* JADX WARNING: type inference failed for: r0v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v16, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v20, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v24, types: [byte] */
    /* JADX WARNING: type inference failed for: r3v1, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v26, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v30, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v34, types: [byte] */
    /* JADX WARNING: type inference failed for: r3v7, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v37, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v41, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v45, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v18, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v48, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v52, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v55, types: [byte] */
    /* JADX WARNING: type inference failed for: r3v13, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v58, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v62, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v66, types: [byte] */
    /* JADX WARNING: type inference failed for: r11v17, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v69, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v73, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v77, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v24, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v79, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v83, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v87, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v30, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v90, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v94, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v98, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v36, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v101, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v105, types: [int, byte] */
    /* JADX WARNING: type inference failed for: r15v108, types: [byte] */
    /* JADX WARNING: type inference failed for: r10v42, types: [byte] */
    /* JADX WARNING: type inference failed for: r15v111, types: [byte] */
    /* JADX WARNING: type inference failed for: r1v0, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r0v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v13, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v18, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v24, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v30, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v36, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v42, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v5, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r10v9, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r11v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r11v12, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r11v17, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r11v4, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r11v8, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r12v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r12v5, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r12v9, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r13v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r14v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r14v12, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r14v5, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r14v9, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v101, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v105, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v108, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v111, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v13, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v16, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v20, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v24, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v26, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v30, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v34, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v37, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v41, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v45, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v48, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v5, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v52, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v55, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v58, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v62, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v66, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v69, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v73, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v77, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v79, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v83, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v87, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v9, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v90, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v94, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r15v98, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r1v0, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r3v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r3v13, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r3v7, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r7v0, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r8v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r8v5, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r8v9, types: [int, byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte, code=null, for r9v1, types: [byte] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=byte[], code=null, for r27v0, types: [byte[]] */
    /* JADX WARNING: Unknown variable types count: 65 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void engineUpdate(byte[] r27, int r28) {
        /*
            r26 = this;
            r0 = r26
            int r3 = r0.f40hB
            int r4 = r0.f41hC
            int r5 = r0.f42hD
            r6 = r4 ^ r5
            r6 = r6 & r3
            r6 = r6 ^ r5
            byte r7 = r27[r28]
            r7 = r7 & 255(0xff, float:3.57E-43)
            int r8 = r28 + 1
            byte r8 = r27[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 << 8
            r7 = r7 | r8
            int r8 = r28 + 2
            byte r8 = r27[r8]
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r8 = r8 << 16
            r7 = r7 | r8
            int r8 = r28 + 3
            byte r8 = r27[r8]
            int r8 = r8 << 24
            r7 = r7 | r8
            int r6 = r6 + r7
            r8 = -680876936(0xffffffffd76aa478, float:-2.57992109E14)
            int r6 = r6 + r8
            int r8 = r0.f39hA
            int r6 = r6 + r8
            int r8 = r6 << 7
            int r6 = r6 >>> 25
            r6 = r6 | r8
            int r6 = r6 + r3
            r8 = r3 ^ r4
            r8 = r8 & r6
            r8 = r8 ^ r4
            int r9 = r28 + 4
            byte r9 = r27[r9]
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r10 = r28 + 5
            byte r10 = r27[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r9 = r9 | r10
            int r2 = r28 + 10
            int r10 = r2 + -4
            byte r10 = r27[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 16
            r9 = r9 | r10
            int r10 = r2 + -3
            byte r10 = r27[r10]
            int r10 = r10 << 24
            r9 = r9 | r10
            int r8 = r8 + r9
            r10 = -389564586(0xffffffffe8c7b756, float:-7.545063E24)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 12
            int r8 = r8 >>> 20
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r3
            r8 = r8 & r5
            r8 = r8 ^ r3
            int r10 = r2 + -2
            byte r10 = r27[r10]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r11 = r2 + -1
            byte r11 = r27[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r11 = r11 << 8
            r10 = r10 | r11
            byte r11 = r27[r2]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r11 = r11 << 16
            r10 = r10 | r11
            int r11 = r2 + 1
            byte r11 = r27[r11]
            int r11 = r11 << 24
            r10 = r10 | r11
            int r8 = r8 + r10
            r11 = 606105819(0x242070db, float:3.4790062E-17)
            int r8 = r8 + r11
            int r8 = r8 + r4
            int r4 = r8 << 17
            int r8 = r8 >>> 15
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r6
            r8 = r8 & r4
            r8 = r8 ^ r6
            int r11 = r2 + 2
            byte r11 = r27[r11]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r12 = r2 + 3
            byte r12 = r27[r12]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r12 = r12 << 8
            r11 = r11 | r12
            int r12 = r2 + 4
            byte r12 = r27[r12]
            r12 = r12 & 255(0xff, float:3.57E-43)
            int r12 = r12 << 16
            r11 = r11 | r12
            int r12 = r2 + 5
            byte r12 = r27[r12]
            int r12 = r12 << 24
            r11 = r11 | r12
            int r8 = r8 + r11
            r12 = -1044525330(0xffffffffc1bdceee, float:-23.72604)
            int r8 = r8 + r12
            int r8 = r8 + r3
            int r12 = r8 << 22
            int r8 = r8 >>> 10
            r8 = r8 | r12
            int r8 = r8 + r4
            r12 = r4 ^ r5
            r12 = r12 & r8
            r12 = r12 ^ r5
            int r2 = r2 + 10
            int r13 = r2 + -4
            byte r13 = r27[r13]
            r13 = r13 & 255(0xff, float:3.57E-43)
            int r14 = r2 + -3
            byte r14 = r27[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r14 = r14 << 8
            r13 = r13 | r14
            int r14 = r2 + -2
            byte r14 = r27[r14]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r14 = r14 << 16
            r13 = r13 | r14
            int r14 = r2 + -1
            byte r14 = r27[r14]
            int r14 = r14 << 24
            r13 = r13 | r14
            int r12 = r12 + r13
            r14 = -176418897(0xfffffffff57c0faf, float:-3.1952561E32)
            int r12 = r12 + r14
            int r12 = r12 + r6
            int r6 = r12 << 7
            int r12 = r12 >>> 25
            r6 = r6 | r12
            int r6 = r6 + r8
            r12 = r8 ^ r4
            r12 = r12 & r6
            r12 = r12 ^ r4
            byte r14 = r27[r2]
            r14 = r14 & 255(0xff, float:3.57E-43)
            int r15 = r2 + 1
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 8
            r14 = r14 | r15
            int r15 = r2 + 2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r14 = r14 | r15
            int r15 = r2 + 3
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r14 = r14 | r15
            int r12 = r12 + r14
            r15 = 1200080426(0x4787c62a, float:69516.33)
            int r12 = r12 + r15
            int r12 = r12 + r5
            int r5 = r12 << 12
            int r12 = r12 >>> 20
            r5 = r5 | r12
            int r5 = r5 + r6
            r12 = r6 ^ r8
            r12 = r12 & r5
            r12 = r12 ^ r8
            int r15 = r2 + 4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 5
            byte r0 = r27[r16]
            r0 = r0 & 255(0xff, float:3.57E-43)
            int r0 = r0 << 8
            r0 = r0 | r15
            int r2 = r2 + 10
            int r15 = r2 + -4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r0 = r0 | r15
            int r15 = r2 + -3
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r0 = r0 | r15
            int r12 = r12 + r0
            r15 = -1473231341(0xffffffffa8304613, float:-9.7851575E-15)
            int r12 = r12 + r15
            int r12 = r12 + r4
            int r4 = r12 << 17
            int r12 = r12 >>> 15
            r4 = r4 | r12
            int r4 = r4 + r5
            r12 = r5 ^ r6
            r12 = r12 & r4
            r12 = r12 ^ r6
            int r15 = r2 + -2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + -1
            r17 = r3
            byte r3 = r27[r16]
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 8
            r3 = r3 | r15
            byte r15 = r27[r2]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r3 = r3 | r15
            int r15 = r2 + 1
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r3 = r3 | r15
            int r12 = r12 + r3
            r15 = -45705983(0xfffffffffd469501, float:-1.6497551E37)
            int r12 = r12 + r15
            int r12 = r12 + r8
            int r8 = r12 << 22
            int r12 = r12 >>> 10
            r8 = r8 | r12
            int r8 = r8 + r4
            r12 = r4 ^ r5
            r12 = r12 & r8
            r12 = r12 ^ r5
            int r15 = r2 + 2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 3
            r18 = r3
            byte r3 = r27[r16]
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 8
            r3 = r3 | r15
            int r15 = r2 + 4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r3 = r3 | r15
            int r15 = r2 + 5
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r3 = r3 | r15
            int r12 = r12 + r3
            r15 = 1770035416(0x698098d8, float:1.9433036E25)
            int r12 = r12 + r15
            int r12 = r12 + r6
            int r6 = r12 << 7
            int r12 = r12 >>> 25
            r6 = r6 | r12
            int r6 = r6 + r8
            r12 = r8 ^ r4
            r12 = r12 & r6
            r12 = r12 ^ r4
            int r2 = r2 + 10
            int r15 = r2 + -4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + -3
            r19 = r10
            byte r10 = r27[r16]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r10 = r10 | r15
            int r15 = r2 + -2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r10 = r10 | r15
            int r15 = r2 + -1
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r10 = r10 | r15
            int r12 = r12 + r10
            r15 = -1958414417(0xffffffff8b44f7af, float:-3.7934563E-32)
            int r12 = r12 + r15
            int r12 = r12 + r5
            int r5 = r12 << 12
            int r12 = r12 >>> 20
            r5 = r5 | r12
            int r5 = r5 + r6
            r12 = r6 ^ r8
            r12 = r12 & r5
            r12 = r12 ^ r8
            byte r15 = r27[r2]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 1
            r20 = r3
            byte r3 = r27[r16]
            r3 = r3 & 255(0xff, float:3.57E-43)
            int r3 = r3 << 8
            r3 = r3 | r15
            int r15 = r2 + 2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r3 = r3 | r15
            int r15 = r2 + 3
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r3 = r3 | r15
            int r12 = r12 + r3
            r15 = -42063(0xffffffffffff5bb1, float:NaN)
            int r12 = r12 + r15
            int r12 = r12 + r4
            int r4 = r12 << 17
            int r12 = r12 >>> 15
            r4 = r4 | r12
            int r4 = r4 + r5
            r12 = r5 ^ r6
            r12 = r12 & r4
            r12 = r12 ^ r6
            int r15 = r2 + 4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 5
            r21 = r11
            byte r11 = r27[r16]
            r11 = r11 & 255(0xff, float:3.57E-43)
            int r11 = r11 << 8
            r11 = r11 | r15
            int r2 = r2 + 10
            int r15 = r2 + -4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r11 = r11 | r15
            int r15 = r2 + -3
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r11 = r11 | r15
            int r12 = r12 + r11
            r15 = -1990404162(0xffffffff895cd7be, float:-2.6582978E-33)
            int r12 = r12 + r15
            int r12 = r12 + r8
            int r8 = r12 << 22
            int r12 = r12 >>> 10
            r8 = r8 | r12
            int r8 = r8 + r4
            r12 = r4 ^ r5
            r12 = r12 & r8
            r12 = r12 ^ r5
            int r15 = r2 + -2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + -1
            r22 = r10
            byte r10 = r27[r16]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r10 = r10 | r15
            byte r15 = r27[r2]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r10 = r10 | r15
            int r15 = r2 + 1
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r10 = r10 | r15
            int r12 = r12 + r10
            r15 = 1804603682(0x6b901122, float:3.4833245E26)
            int r12 = r12 + r15
            int r12 = r12 + r6
            int r6 = r12 << 7
            int r12 = r12 >>> 25
            r6 = r6 | r12
            int r6 = r6 + r8
            r12 = r8 ^ r4
            r12 = r12 & r6
            r12 = r12 ^ r4
            int r15 = r2 + 2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 3
            r23 = r10
            byte r10 = r27[r16]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r10 = r10 | r15
            int r15 = r2 + 4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r10 = r10 | r15
            int r15 = r2 + 5
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r10 = r10 | r15
            int r12 = r12 + r10
            r15 = -40341101(0xfffffffffd987193, float:-2.5329046E37)
            int r12 = r12 + r15
            int r12 = r12 + r5
            int r5 = r12 << 12
            int r12 = r12 >>> 20
            r5 = r5 | r12
            int r5 = r5 + r6
            r12 = r6 ^ r8
            r12 = r12 & r5
            r12 = r12 ^ r8
            int r2 = r2 + 10
            int r15 = r2 + -4
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + -3
            r24 = r10
            byte r10 = r27[r16]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r10 = r10 | r15
            int r15 = r2 + -2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r10 = r10 | r15
            int r15 = r2 + -1
            byte r15 = r27[r15]
            int r15 = r15 << 24
            r10 = r10 | r15
            int r12 = r12 + r10
            r15 = -1502002290(0xffffffffa679438e, float:-8.6480783E-16)
            int r12 = r12 + r15
            int r12 = r12 + r4
            int r4 = r12 << 17
            int r12 = r12 >>> 15
            r4 = r4 | r12
            int r4 = r4 + r5
            r12 = r5 ^ r6
            r12 = r12 & r4
            r12 = r12 ^ r6
            byte r15 = r27[r2]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r16 = r2 + 1
            r25 = r10
            byte r10 = r27[r16]
            r10 = r10 & 255(0xff, float:3.57E-43)
            int r10 = r10 << 8
            r10 = r10 | r15
            int r15 = r2 + 2
            byte r15 = r27[r15]
            r15 = r15 & 255(0xff, float:3.57E-43)
            int r15 = r15 << 16
            r10 = r10 | r15
            int r2 = r2 + 3
            byte r1 = r27[r2]
            int r1 = r1 << 24
            r1 = r1 | r10
            int r12 = r12 + r1
            r2 = 1236535329(0x49b40821, float:1474820.1)
            int r12 = r12 + r2
            int r12 = r12 + r8
            int r2 = r12 << 22
            int r8 = r12 >>> 10
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 & r5
            r8 = r8 ^ r4
            int r8 = r8 + r9
            r10 = -165796510(0xfffffffff61e2562, float:-8.018956E32)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 5
            int r8 = r8 >>> 27
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 & r4
            r8 = r8 ^ r2
            int r8 = r8 + r0
            r10 = -1069501632(0xffffffffc040b340, float:-3.0109406)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 9
            int r8 = r8 >>> 23
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 & r2
            r8 = r8 ^ r6
            int r8 = r8 + r11
            r10 = 643717713(0x265e5a51, float:7.7144124E-16)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 14
            int r8 = r8 >>> 18
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 & r6
            r8 = r8 ^ r5
            int r8 = r8 + r7
            r10 = -373897302(0xffffffffe9b6c7aa, float:-2.7620923E25)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 20
            int r8 = r8 >>> 12
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 & r5
            r8 = r8 ^ r4
            int r8 = r8 + r14
            r10 = -701558691(0xffffffffd62f105d, float:-4.8121204E13)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 5
            int r8 = r8 >>> 27
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 & r4
            r8 = r8 ^ r2
            int r8 = r8 + r3
            r10 = 38016083(0x2441453, float:1.4405639E-37)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 9
            int r8 = r8 >>> 23
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 & r2
            r8 = r8 ^ r6
            int r8 = r8 + r1
            r10 = -660478335(0xffffffffd8a1e681, float:-1.42409103E15)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 14
            int r8 = r8 >>> 18
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 & r6
            r8 = r8 ^ r5
            int r8 = r8 + r13
            r10 = -405537848(0xffffffffe7d3fbc8, float:-2.0021277E24)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 20
            int r8 = r8 >>> 12
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 & r5
            r8 = r8 ^ r4
            int r8 = r8 + r22
            r10 = 568446438(0x21e1cde6, float:1.5301094E-18)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 5
            int r8 = r8 >>> 27
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 & r4
            r8 = r8 ^ r2
            int r8 = r8 + r25
            r10 = -1019803690(0xffffffffc33707d6, float:-183.03061)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 9
            int r8 = r8 >>> 23
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 & r2
            r8 = r8 ^ r6
            int r8 = r8 + r21
            r10 = -187363961(0xfffffffff4d50d87, float:-1.3503828E32)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 14
            int r8 = r8 >>> 18
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 & r6
            r8 = r8 ^ r5
            int r8 = r8 + r20
            r10 = 1163531501(0x455a14ed, float:3489.3079)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 20
            int r8 = r8 >>> 12
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 & r5
            r8 = r8 ^ r4
            int r8 = r8 + r24
            r10 = -1444681467(0xffffffffa9e3e905, float:-1.01212475E-13)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 5
            int r8 = r8 >>> 27
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 & r4
            r8 = r8 ^ r2
            int r8 = r8 + r19
            r10 = -51403784(0xfffffffffcefa3f8, float:-9.954277E36)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 9
            int r8 = r8 >>> 23
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 & r2
            r8 = r8 ^ r6
            int r8 = r8 + r18
            r10 = 1735328473(0x676f02d9, float:1.1286981E24)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 14
            int r8 = r8 >>> 18
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 & r6
            r8 = r8 ^ r5
            int r8 = r8 + r23
            r10 = -1926607734(0xffffffff8d2a4c8a, float:-5.2477425E-31)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 20
            int r8 = r8 >>> 12
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 ^ r5
            int r8 = r8 + r14
            r10 = -378558(0xfffffffffffa3942, float:NaN)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 4
            int r8 = r8 >>> 28
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 ^ r4
            int r8 = r8 + r20
            r10 = -2022574463(0xffffffff8771f681, float:-1.8203266E-34)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 11
            int r8 = r8 >>> 21
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 ^ r2
            int r8 = r8 + r11
            r10 = 1839030562(0x6d9d6122, float:6.0883216E27)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 16
            int r8 = r8 >>> 16
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 ^ r6
            int r8 = r8 + r25
            r10 = -35309556(0xfffffffffde5380c, float:-3.8085528E37)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 23
            int r8 = r8 >>> 9
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 ^ r5
            int r8 = r8 + r9
            r10 = -1530992060(0xffffffffa4beea44, float:-8.2796227E-17)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 4
            int r8 = r8 >>> 28
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 ^ r4
            int r8 = r8 + r13
            r10 = 1272893353(0x4bdecfa9, float:2.9204306E7)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 11
            int r8 = r8 >>> 21
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 ^ r2
            int r8 = r8 + r18
            r10 = -155497632(0xfffffffff6bb4b60, float:-1.8993912E33)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 16
            int r8 = r8 >>> 16
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 ^ r6
            int r8 = r8 + r3
            r10 = -1094730640(0xffffffffbebfbc70, float:-0.37448454)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 23
            int r8 = r8 >>> 9
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 ^ r5
            int r8 = r8 + r24
            r10 = 681279174(0x289b7ec6, float:1.7263436E-14)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 4
            int r8 = r8 >>> 28
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 ^ r4
            int r8 = r8 + r7
            r10 = -358537222(0xffffffffeaa127fa, float:-9.741292E25)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 11
            int r8 = r8 >>> 21
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 ^ r2
            int r8 = r8 + r21
            r10 = -722521979(0xffffffffd4ef3085, float:-8.2184897E12)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 16
            int r8 = r8 >>> 16
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 ^ r6
            int r8 = r8 + r0
            r10 = 76029189(0x4881d05, float:3.2000097E-36)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 23
            int r8 = r8 >>> 9
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r4 ^ r2
            r8 = r8 ^ r5
            int r8 = r8 + r22
            r10 = -640364487(0xffffffffd9d4d039, float:-7.4877048E15)
            int r8 = r8 + r10
            int r8 = r8 + r6
            int r6 = r8 << 4
            int r8 = r8 >>> 28
            r6 = r6 | r8
            int r6 = r6 + r2
            r8 = r2 ^ r6
            r8 = r8 ^ r4
            int r8 = r8 + r23
            r10 = -421815835(0xffffffffe6db99e5, float:-5.1851856E23)
            int r8 = r8 + r10
            int r8 = r8 + r5
            int r5 = r8 << 11
            int r8 = r8 >>> 21
            r5 = r5 | r8
            int r5 = r5 + r6
            r8 = r6 ^ r5
            r8 = r8 ^ r2
            int r8 = r8 + r1
            r10 = 530742520(0x1fa27cf8, float:6.881641E-20)
            int r8 = r8 + r10
            int r8 = r8 + r4
            int r4 = r8 << 16
            int r8 = r8 >>> 16
            r4 = r4 | r8
            int r4 = r4 + r5
            r8 = r5 ^ r4
            r8 = r8 ^ r6
            int r8 = r8 + r19
            r10 = -995338651(0xffffffffc4ac5665, float:-1378.6998)
            int r8 = r8 + r10
            int r8 = r8 + r2
            int r2 = r8 << 23
            int r8 = r8 >>> 9
            r2 = r2 | r8
            int r2 = r2 + r4
            r8 = r5 ^ -1
            r8 = r8 | r2
            r8 = r8 ^ r4
            int r8 = r8 + r7
            r7 = -198630844(0xfffffffff4292244, float:-5.3600657E31)
            int r8 = r8 + r7
            int r8 = r8 + r6
            int r6 = r8 << 6
            int r7 = r8 >>> 26
            r6 = r6 | r7
            int r6 = r6 + r2
            r7 = r4 ^ -1
            r7 = r7 | r6
            r7 = r7 ^ r2
            int r7 = r7 + r18
            r8 = 1126891415(0x432aff97, float:170.9984)
            int r7 = r7 + r8
            int r7 = r7 + r5
            int r5 = r7 << 10
            int r7 = r7 >>> 22
            r5 = r5 | r7
            int r5 = r5 + r6
            r7 = r2 ^ -1
            r7 = r7 | r5
            r7 = r7 ^ r6
            int r7 = r7 + r25
            r8 = -1416354905(0xffffffffab9423a7, float:-1.0525928E-12)
            int r7 = r7 + r8
            int r7 = r7 + r4
            int r4 = r7 << 15
            int r7 = r7 >>> 17
            r4 = r4 | r7
            int r4 = r4 + r5
            r7 = r6 ^ -1
            r7 = r7 | r4
            r7 = r7 ^ r5
            int r7 = r7 + r14
            r8 = -57434055(0xfffffffffc93a039, float:-6.132139E36)
            int r7 = r7 + r8
            int r7 = r7 + r2
            int r2 = r7 << 21
            int r7 = r7 >>> 11
            r2 = r2 | r7
            int r2 = r2 + r4
            r7 = r5 ^ -1
            r7 = r7 | r2
            r7 = r7 ^ r4
            int r7 = r7 + r23
            r8 = 1700485571(0x655b59c3, float:6.474088E22)
            int r7 = r7 + r8
            int r7 = r7 + r6
            int r6 = r7 << 6
            int r7 = r7 >>> 26
            r6 = r6 | r7
            int r6 = r6 + r2
            r7 = r4 ^ -1
            r7 = r7 | r6
            r7 = r7 ^ r2
            int r7 = r7 + r21
            r8 = -1894986606(0xffffffff8f0ccc92, float:-6.941932E-30)
            int r7 = r7 + r8
            int r7 = r7 + r5
            int r5 = r7 << 10
            int r7 = r7 >>> 22
            r5 = r5 | r7
            int r5 = r5 + r6
            r7 = r2 ^ -1
            r7 = r7 | r5
            r7 = r7 ^ r6
            int r7 = r7 + r3
            r3 = -1051523(0xffffffffffeff47d, float:NaN)
            int r7 = r7 + r3
            int r7 = r7 + r4
            int r3 = r7 << 15
            int r4 = r7 >>> 17
            r3 = r3 | r4
            int r3 = r3 + r5
            r4 = r6 ^ -1
            r4 = r4 | r3
            r4 = r4 ^ r5
            int r4 = r4 + r9
            r7 = -2054922799(0xffffffff85845dd1, float:-1.2447683E-35)
            int r4 = r4 + r7
            int r4 = r4 + r2
            int r2 = r4 << 21
            int r4 = r4 >>> 11
            r2 = r2 | r4
            int r2 = r2 + r3
            r4 = r5 ^ -1
            r4 = r4 | r2
            r4 = r4 ^ r3
            int r4 = r4 + r20
            r7 = 1873313359(0x6fa87e4f, float:1.0429236E29)
            int r4 = r4 + r7
            int r4 = r4 + r6
            int r6 = r4 << 6
            int r4 = r4 >>> 26
            r4 = r4 | r6
            int r4 = r4 + r2
            r6 = r3 ^ -1
            r6 = r6 | r4
            r6 = r6 ^ r2
            int r6 = r6 + r1
            r1 = -30611744(0xfffffffffe2ce6e0, float:-5.7456497E37)
            int r6 = r6 + r1
            int r6 = r6 + r5
            int r1 = r6 << 10
            int r5 = r6 >>> 22
            r1 = r1 | r5
            int r1 = r1 + r4
            r5 = r2 ^ -1
            r5 = r5 | r1
            r5 = r5 ^ r4
            int r5 = r5 + r0
            r0 = -1560198380(0xffffffffa3014314, float:-7.007308E-18)
            int r5 = r5 + r0
            int r5 = r5 + r3
            int r0 = r5 << 15
            int r3 = r5 >>> 17
            r0 = r0 | r3
            int r0 = r0 + r1
            r3 = r4 ^ -1
            r3 = r3 | r0
            r3 = r3 ^ r1
            int r3 = r3 + r24
            r5 = 1309151649(0x4e0811a1, float:5.7071418E8)
            int r3 = r3 + r5
            int r3 = r3 + r2
            int r2 = r3 << 21
            int r3 = r3 >>> 11
            r2 = r2 | r3
            int r2 = r2 + r0
            r3 = r1 ^ -1
            r3 = r3 | r2
            r3 = r3 ^ r0
            int r3 = r3 + r13
            r5 = -145523070(0xfffffffff7537e82, float:-4.2896114E33)
            int r3 = r3 + r5
            int r3 = r3 + r4
            int r4 = r3 << 6
            int r3 = r3 >>> 26
            r3 = r3 | r4
            int r3 = r3 + r2
            r4 = r0 ^ -1
            r4 = r4 | r3
            r4 = r4 ^ r2
            int r4 = r4 + r11
            r5 = -1120210379(0xffffffffbd3af235, float:-0.045641143)
            int r4 = r4 + r5
            int r4 = r4 + r1
            int r1 = r4 << 10
            int r4 = r4 >>> 22
            r1 = r1 | r4
            int r1 = r1 + r3
            r4 = r2 ^ -1
            r4 = r4 | r1
            r4 = r4 ^ r3
            int r4 = r4 + r19
            r5 = 718787259(0x2ad7d2bb, float:3.8337896E-13)
            int r4 = r4 + r5
            int r4 = r4 + r0
            int r0 = r4 << 15
            int r4 = r4 >>> 17
            r0 = r0 | r4
            int r0 = r0 + r1
            int r4 = r17 + r0
            r5 = r3 ^ -1
            r5 = r5 | r0
            r5 = r5 ^ r1
            int r5 = r5 + r22
            r6 = -343485551(0xffffffffeb86d391, float:-3.259903E26)
            int r5 = r5 + r6
            int r5 = r5 + r2
            int r2 = r5 << 21
            int r5 = r5 >>> 11
            r2 = r2 | r5
            int r4 = r4 + r2
            r2 = r26
            r2.f40hB = r4
            int r4 = r2.f41hC
            int r4 = r4 + r0
            r2.f41hC = r4
            int r0 = r2.f42hD
            int r0 = r0 + r1
            r2.f42hD = r0
            int r0 = r2.f39hA
            int r0 = r0 + r3
            r2.f39hA = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.security.MD5.engineUpdate(byte[], int):void");
    }
}
