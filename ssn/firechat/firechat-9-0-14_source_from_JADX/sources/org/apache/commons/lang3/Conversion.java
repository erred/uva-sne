package org.apache.commons.lang3;

import java.util.UUID;
import org.altbeacon.bluetooth.Pdu;

public class Conversion {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean[] FFFF = {false, false, false, false};
    private static final boolean[] FFFT = {false, false, false, true};
    private static final boolean[] FFTF = {false, false, true, false};
    private static final boolean[] FFTT = {false, false, true, true};
    private static final boolean[] FTFF = {false, true, false, false};
    private static final boolean[] FTFT = {false, true, false, true};
    private static final boolean[] FTTF = {false, true, true, false};
    private static final boolean[] FTTT = {false, true, true, true};
    private static final boolean[] TFFF = {true, false, false, false};
    private static final boolean[] TFFT = {true, false, false, true};
    private static final boolean[] TFTF = {true, false, true, false};
    private static final boolean[] TFTT = {true, false, true, true};
    private static final boolean[] TTFF = {true, true, false, false};
    private static final boolean[] TTFT = {true, true, false, true};
    private static final boolean[] TTTF = {true, true, true, false};
    private static final boolean[] TTTT = {true, true, true, true};

    public static int hexDigitToInt(char c) {
        int digit = Character.digit(c, 16);
        if (digit >= 0) {
            return digit;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Cannot interpret '");
        sb.append(c);
        sb.append("' as a hexadecimal digit");
        throw new IllegalArgumentException(sb.toString());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002c, code lost:
        return 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002e, code lost:
        return 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0031, code lost:
        return 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
        return 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0027, code lost:
        return 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0029, code lost:
        return 7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int hexDigitMsb0ToInt(char r3) {
        /*
            switch(r3) {
                case 48: goto L_0x004b;
                case 49: goto L_0x0048;
                case 50: goto L_0x0046;
                case 51: goto L_0x0043;
                case 52: goto L_0x0041;
                case 53: goto L_0x003e;
                case 54: goto L_0x003c;
                case 55: goto L_0x0039;
                case 56: goto L_0x0037;
                case 57: goto L_0x0034;
                default: goto L_0x0003;
            }
        L_0x0003:
            switch(r3) {
                case 65: goto L_0x0032;
                case 66: goto L_0x002f;
                case 67: goto L_0x002d;
                case 68: goto L_0x002a;
                case 69: goto L_0x0028;
                case 70: goto L_0x0025;
                default: goto L_0x0006;
            }
        L_0x0006:
            switch(r3) {
                case 97: goto L_0x0032;
                case 98: goto L_0x002f;
                case 99: goto L_0x002d;
                case 100: goto L_0x002a;
                case 101: goto L_0x0028;
                case 102: goto L_0x0025;
                default: goto L_0x0009;
            }
        L_0x0009:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot interpret '"
            r1.append(r2)
            r1.append(r3)
            java.lang.String r3 = "' as a hexadecimal digit"
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r0.<init>(r3)
            throw r0
        L_0x0025:
            r3 = 15
            return r3
        L_0x0028:
            r3 = 7
            return r3
        L_0x002a:
            r3 = 11
            return r3
        L_0x002d:
            r3 = 3
            return r3
        L_0x002f:
            r3 = 13
            return r3
        L_0x0032:
            r3 = 5
            return r3
        L_0x0034:
            r3 = 9
            return r3
        L_0x0037:
            r3 = 1
            return r3
        L_0x0039:
            r3 = 14
            return r3
        L_0x003c:
            r3 = 6
            return r3
        L_0x003e:
            r3 = 10
            return r3
        L_0x0041:
            r3 = 2
            return r3
        L_0x0043:
            r3 = 12
            return r3
        L_0x0046:
            r3 = 4
            return r3
        L_0x0048:
            r3 = 8
            return r3
        L_0x004b:
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToInt(char):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003f, code lost:
        return (boolean[]) TFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0048, code lost:
        return (boolean[]) FFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0051, code lost:
        return (boolean[]) TTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005a, code lost:
        return (boolean[]) FTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002d, code lost:
        return (boolean[]) TTTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0036, code lost:
        return (boolean[]) FTTT.clone();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitToBinary(char r3) {
        /*
            switch(r3) {
                case 48: goto L_0x00ac;
                case 49: goto L_0x00a3;
                case 50: goto L_0x009a;
                case 51: goto L_0x0091;
                case 52: goto L_0x0088;
                case 53: goto L_0x007f;
                case 54: goto L_0x0076;
                case 55: goto L_0x006d;
                case 56: goto L_0x0064;
                case 57: goto L_0x005b;
                default: goto L_0x0003;
            }
        L_0x0003:
            switch(r3) {
                case 65: goto L_0x0052;
                case 66: goto L_0x0049;
                case 67: goto L_0x0040;
                case 68: goto L_0x0037;
                case 69: goto L_0x002e;
                case 70: goto L_0x0025;
                default: goto L_0x0006;
            }
        L_0x0006:
            switch(r3) {
                case 97: goto L_0x0052;
                case 98: goto L_0x0049;
                case 99: goto L_0x0040;
                case 100: goto L_0x0037;
                case 101: goto L_0x002e;
                case 102: goto L_0x0025;
                default: goto L_0x0009;
            }
        L_0x0009:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot interpret '"
            r1.append(r2)
            r1.append(r3)
            java.lang.String r3 = "' as a hexadecimal digit"
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r0.<init>(r3)
            throw r0
        L_0x0025:
            boolean[] r3 = TTTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x002e:
            boolean[] r3 = FTTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0037:
            boolean[] r3 = TFTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0040:
            boolean[] r3 = FFTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0049:
            boolean[] r3 = TTFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0052:
            boolean[] r3 = FTFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x005b:
            boolean[] r3 = TFFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0064:
            boolean[] r3 = FFFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x006d:
            boolean[] r3 = TTTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0076:
            boolean[] r3 = FTTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x007f:
            boolean[] r3 = TFTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0088:
            boolean[] r3 = FFTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0091:
            boolean[] r3 = TTFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x009a:
            boolean[] r3 = FTFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x00a3:
            boolean[] r3 = TFFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x00ac:
            boolean[] r3 = FFFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitToBinary(char):boolean[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003f, code lost:
        return (boolean[]) TTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0048, code lost:
        return (boolean[]) TTFF.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0051, code lost:
        return (boolean[]) TFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005a, code lost:
        return (boolean[]) TFTF.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002d, code lost:
        return (boolean[]) TTTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0036, code lost:
        return (boolean[]) TTTF.clone();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitMsb0ToBinary(char r3) {
        /*
            switch(r3) {
                case 48: goto L_0x00ac;
                case 49: goto L_0x00a3;
                case 50: goto L_0x009a;
                case 51: goto L_0x0091;
                case 52: goto L_0x0088;
                case 53: goto L_0x007f;
                case 54: goto L_0x0076;
                case 55: goto L_0x006d;
                case 56: goto L_0x0064;
                case 57: goto L_0x005b;
                default: goto L_0x0003;
            }
        L_0x0003:
            switch(r3) {
                case 65: goto L_0x0052;
                case 66: goto L_0x0049;
                case 67: goto L_0x0040;
                case 68: goto L_0x0037;
                case 69: goto L_0x002e;
                case 70: goto L_0x0025;
                default: goto L_0x0006;
            }
        L_0x0006:
            switch(r3) {
                case 97: goto L_0x0052;
                case 98: goto L_0x0049;
                case 99: goto L_0x0040;
                case 100: goto L_0x0037;
                case 101: goto L_0x002e;
                case 102: goto L_0x0025;
                default: goto L_0x0009;
            }
        L_0x0009:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot interpret '"
            r1.append(r2)
            r1.append(r3)
            java.lang.String r3 = "' as a hexadecimal digit"
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            r0.<init>(r3)
            throw r0
        L_0x0025:
            boolean[] r3 = TTTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x002e:
            boolean[] r3 = TTTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0037:
            boolean[] r3 = TTFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0040:
            boolean[] r3 = TTFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0049:
            boolean[] r3 = TFTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0052:
            boolean[] r3 = TFTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x005b:
            boolean[] r3 = TFFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0064:
            boolean[] r3 = TFFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x006d:
            boolean[] r3 = FTTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0076:
            boolean[] r3 = FTTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x007f:
            boolean[] r3 = FTFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0088:
            boolean[] r3 = FTFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x0091:
            boolean[] r3 = FFTT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x009a:
            boolean[] r3 = FFTF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x00a3:
            boolean[] r3 = FFFT
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        L_0x00ac:
            boolean[] r3 = FFFF
            java.lang.Object r3 = r3.clone()
            boolean[] r3 = (boolean[]) r3
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToBinary(char):boolean[]");
    }

    public static char binaryToHexDigit(boolean[] zArr) {
        return binaryToHexDigit(zArr, 0);
    }

    public static char binaryToHexDigit(boolean[] zArr, int i) {
        if (zArr.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        }
        int i2 = i + 3;
        if (zArr.length <= i2 || !zArr[i2]) {
            int i3 = i + 2;
            if (zArr.length <= i3 || !zArr[i3]) {
                int i4 = i + 1;
                if (zArr.length <= i4 || !zArr[i4]) {
                    return zArr[i] ? '1' : '0';
                }
                return zArr[i] ? '3' : '2';
            }
            int i5 = i + 1;
            if (zArr.length <= i5 || !zArr[i5]) {
                return zArr[i] ? '5' : '4';
            }
            return zArr[i] ? '7' : '6';
        }
        int i6 = i + 2;
        if (zArr.length <= i6 || !zArr[i6]) {
            int i7 = i + 1;
            if (zArr.length <= i7 || !zArr[i7]) {
                return zArr[i] ? '9' : '8';
            }
            return zArr[i] ? 'b' : 'a';
        }
        int i8 = i + 1;
        if (zArr.length <= i8 || !zArr[i8]) {
            return zArr[i] ? 'd' : 'c';
        }
        return zArr[i] ? 'f' : 'e';
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] zArr) {
        return binaryToHexDigitMsb0_4bits(zArr, 0);
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] zArr, int i) {
        if (zArr.length > 8) {
            StringBuilder sb = new StringBuilder();
            sb.append("src.length>8: src.length=");
            sb.append(zArr.length);
            throw new IllegalArgumentException(sb.toString());
        } else if (zArr.length - i < 4) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("src.length-srcPos<4: src.length=");
            sb2.append(zArr.length);
            sb2.append(", srcPos=");
            sb2.append(i);
            throw new IllegalArgumentException(sb2.toString());
        } else if (zArr[i + 3]) {
            if (zArr[i + 2]) {
                if (zArr[i + 1]) {
                    return zArr[i] ? 'f' : '7';
                }
                return zArr[i] ? 'b' : '3';
            } else if (zArr[i + 1]) {
                return zArr[i] ? 'd' : '5';
            } else {
                return zArr[i] ? '9' : '1';
            }
        } else if (zArr[i + 2]) {
            if (zArr[i + 1]) {
                return zArr[i] ? 'e' : '6';
            }
            return zArr[i] ? 'a' : '2';
        } else if (zArr[i + 1]) {
            return zArr[i] ? 'c' : '4';
        } else {
            return zArr[i] ? '8' : '0';
        }
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] zArr) {
        return binaryBeMsb0ToHexDigit(zArr, 0);
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] zArr, int i) {
        if (zArr.length == 0) {
            throw new IllegalArgumentException("Cannot convert an empty array.");
        }
        int length = ((zArr.length - 1) - i) + 1;
        int min = Math.min(4, length);
        boolean[] zArr2 = new boolean[4];
        System.arraycopy(zArr, length - min, zArr2, 4 - min, min);
        if (zArr2[0]) {
            if (zArr2.length <= 1 || !zArr2[1]) {
                if (zArr2.length <= 2 || !zArr2[2]) {
                    return (zArr2.length <= 3 || !zArr2[3]) ? '8' : '9';
                }
                return (zArr2.length <= 3 || !zArr2[3]) ? 'a' : 'b';
            } else if (zArr2.length <= 2 || !zArr2[2]) {
                return (zArr2.length <= 3 || !zArr2[3]) ? 'c' : 'd';
            } else {
                return (zArr2.length <= 3 || !zArr2[3]) ? 'e' : 'f';
            }
        } else if (zArr2.length <= 1 || !zArr2[1]) {
            if (zArr2.length <= 2 || !zArr2[2]) {
                return (zArr2.length <= 3 || !zArr2[3]) ? '0' : '1';
            }
            return (zArr2.length <= 3 || !zArr2[3]) ? '2' : '3';
        } else if (zArr2.length <= 2 || !zArr2[2]) {
            return (zArr2.length <= 3 || !zArr2[3]) ? '4' : '5';
        } else {
            return (zArr2.length <= 3 || !zArr2[3]) ? '6' : '7';
        }
    }

    public static char intToHexDigit(int i) {
        char forDigit = Character.forDigit(i, 16);
        if (forDigit != 0) {
            return forDigit;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("nibble value not between 0 and 15: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public static char intToHexDigitMsb0(int i) {
        switch (i) {
            case 0:
                return '0';
            case 1:
                return '8';
            case 2:
                return '4';
            case 3:
                return 'c';
            case 4:
                return '2';
            case 5:
                return 'a';
            case 6:
                return '6';
            case 7:
                return 'e';
            case 8:
                return '1';
            case 9:
                return '9';
            case 10:
                return '5';
            case 11:
                return 'd';
            case 12:
                return '3';
            case 13:
                return 'b';
            case 14:
                return '7';
            case 15:
                return 'f';
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("nibble value not between 0 and 15: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
        }
    }

    public static long intArrayToLong(int[] iArr, int i, long j, int i2, int i3) {
        if ((iArr.length == 0 && i == 0) || i3 == 0) {
            return j;
        }
        if (((i3 - 1) * 32) + i2 >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+dstPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 32) + i2;
            j = (j & ((4294967295 << i5) ^ -1)) | ((((long) iArr[i4 + i]) & 4294967295L) << i5);
        }
        return j;
    }

    public static long shortArrayToLong(short[] sArr, int i, long j, int i2, int i3) {
        if ((sArr.length == 0 && i == 0) || i3 == 0) {
            return j;
        }
        if (((i3 - 1) * 16) + i2 >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 16) + i2;
            j = (j & ((65535 << i5) ^ -1)) | ((((long) sArr[i4 + i]) & 65535) << i5);
        }
        return j;
    }

    public static int shortArrayToInt(short[] sArr, int i, int i2, int i3, int i4) {
        if ((sArr.length == 0 && i == 0) || i4 == 0) {
            return i2;
        }
        if (((i4 - 1) * 16) + i3 >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = (i5 * 16) + i3;
            i2 = (i2 & ((65535 << i6) ^ -1)) | ((sArr[i5 + i] & 65535) << i6);
        }
        return i2;
    }

    public static long byteArrayToLong(byte[] bArr, int i, long j, int i2, int i3) {
        if ((bArr.length == 0 && i == 0) || i3 == 0) {
            return j;
        }
        if (((i3 - 1) * 8) + i2 >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 8) + i2;
            j = (j & ((255 << i5) ^ -1)) | ((((long) bArr[i4 + i]) & 255) << i5);
        }
        return j;
    }

    public static int byteArrayToInt(byte[] bArr, int i, int i2, int i3, int i4) {
        if ((bArr.length == 0 && i == 0) || i4 == 0) {
            return i2;
        }
        if (((i4 - 1) * 8) + i3 >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = (i5 * 8) + i3;
            i2 = (i2 & ((255 << i6) ^ -1)) | ((bArr[i5 + i] & Pdu.MANUFACTURER_DATA_PDU_TYPE) << i6);
        }
        return i2;
    }

    public static short byteArrayToShort(byte[] bArr, int i, short s, int i2, int i3) {
        if ((bArr.length == 0 && i == 0) || i3 == 0) {
            return s;
        }
        if (((i3 - 1) * 8) + i2 >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 16");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 8) + i2;
            s = (short) ((s & ((255 << i5) ^ -1)) | ((bArr[i4 + i] & Pdu.MANUFACTURER_DATA_PDU_TYPE) << i5));
        }
        return s;
    }

    public static long hexToLong(String str, int i, long j, int i2, int i3) {
        if (i3 == 0) {
            return j;
        }
        if (((i3 - 1) * 4) + i2 >= 64) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 4) + i2;
            j = (j & ((15 << i5) ^ -1)) | ((((long) hexDigitToInt(str.charAt(i4 + i))) & 15) << i5);
        }
        return j;
    }

    public static int hexToInt(String str, int i, int i2, int i3, int i4) {
        if (i4 == 0) {
            return i2;
        }
        if (((i4 - 1) * 4) + i3 >= 32) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = (i5 * 4) + i3;
            i2 = (i2 & ((15 << i6) ^ -1)) | ((hexDigitToInt(str.charAt(i5 + i)) & 15) << i6);
        }
        return i2;
    }

    public static short hexToShort(String str, int i, short s, int i2, int i3) {
        if (i3 == 0) {
            return s;
        }
        if (((i3 - 1) * 4) + i2 >= 16) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 16");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 4) + i2;
            s = (short) ((s & ((15 << i5) ^ -1)) | ((hexDigitToInt(str.charAt(i4 + i)) & 15) << i5));
        }
        return s;
    }

    public static byte hexToByte(String str, int i, byte b, int i2, int i3) {
        if (i3 == 0) {
            return b;
        }
        if (((i3 - 1) * 4) + i2 >= 8) {
            throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 8");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (i4 * 4) + i2;
            b = (byte) ((b & ((15 << i5) ^ -1)) | ((hexDigitToInt(str.charAt(i4 + i)) & 15) << i5));
        }
        return b;
    }

    public static long binaryToLong(boolean[] zArr, int i, long j, int i2, int i3) {
        if ((zArr.length == 0 && i == 0) || i3 == 0) {
            return j;
        }
        if ((i3 - 1) + i2 >= 64) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i2;
            j = (j & ((1 << i5) ^ -1)) | ((zArr[i4 + i] ? 1 : 0) << i5);
        }
        return j;
    }

    public static int binaryToInt(boolean[] zArr, int i, int i2, int i3, int i4) {
        if ((zArr.length == 0 && i == 0) || i4 == 0) {
            return i2;
        }
        if ((i4 - 1) + i3 >= 32) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = i5 + i3;
            i2 = (i2 & ((1 << i6) ^ -1)) | ((zArr[i5 + i] ? 1 : 0) << i6);
        }
        return i2;
    }

    public static short binaryToShort(boolean[] zArr, int i, short s, int i2, int i3) {
        if ((zArr.length == 0 && i == 0) || i3 == 0) {
            return s;
        }
        if ((i3 - 1) + i2 >= 16) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 16");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i2;
            s = (short) ((s & ((1 << i5) ^ -1)) | ((zArr[i4 + i] ? 1 : 0) << i5));
        }
        return s;
    }

    public static byte binaryToByte(boolean[] zArr, int i, byte b, int i2, int i3) {
        if ((zArr.length == 0 && i == 0) || i3 == 0) {
            return b;
        }
        if ((i3 - 1) + i2 >= 8) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 8");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 + i2;
            b = (byte) ((b & ((1 << i5) ^ -1)) | ((zArr[i4 + i] ? 1 : 0) << i5));
        }
        return b;
    }

    public static int[] longToIntArray(long j, int i, int[] iArr, int i2, int i3) {
        if (i3 == 0) {
            return iArr;
        }
        if (((i3 - 1) * 32) + i >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+srcPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i2 + i4] = (int) ((j >> ((i4 * 32) + i)) & -1);
        }
        return iArr;
    }

    public static short[] longToShortArray(long j, int i, short[] sArr, int i2, int i3) {
        if (i3 == 0) {
            return sArr;
        }
        if (((i3 - 1) * 16) + i >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            sArr[i2 + i4] = (short) ((int) ((j >> ((i4 * 16) + i)) & 65535));
        }
        return sArr;
    }

    public static short[] intToShortArray(int i, int i2, short[] sArr, int i3, int i4) {
        if (i4 == 0) {
            return sArr;
        }
        if (((i4 - 1) * 16) + i2 >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            sArr[i3 + i5] = (short) ((i >> ((i5 * 16) + i2)) & 65535);
        }
        return sArr;
    }

    public static byte[] longToByteArray(long j, int i, byte[] bArr, int i2, int i3) {
        if (i3 == 0) {
            return bArr;
        }
        if (((i3 - 1) * 8) + i >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            bArr[i2 + i4] = (byte) ((int) ((j >> ((i4 * 8) + i)) & 255));
        }
        return bArr;
    }

    public static byte[] intToByteArray(int i, int i2, byte[] bArr, int i3, int i4) {
        if (i4 == 0) {
            return bArr;
        }
        if (((i4 - 1) * 8) + i2 >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            bArr[i3 + i5] = (byte) ((i >> ((i5 * 8) + i2)) & 255);
        }
        return bArr;
    }

    public static byte[] shortToByteArray(short s, int i, byte[] bArr, int i2, int i3) {
        if (i3 == 0) {
            return bArr;
        }
        if (((i3 - 1) * 8) + i >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 16");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            bArr[i2 + i4] = (byte) ((s >> ((i4 * 8) + i)) & 255);
        }
        return bArr;
    }

    public static String longToHex(long j, int i, String str, int i2, int i3) {
        if (i3 == 0) {
            return str;
        }
        if (((i3 - 1) * 4) + i >= 64) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 64");
        }
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (int) ((j >> ((i4 * 4) + i)) & 15);
            int i6 = i2 + i4;
            if (i6 == length) {
                length++;
                sb.append(intToHexDigit(i5));
            } else {
                sb.setCharAt(i6, intToHexDigit(i5));
            }
        }
        return sb.toString();
    }

    public static String intToHex(int i, int i2, String str, int i3, int i4) {
        if (i4 == 0) {
            return str;
        }
        if (((i4 - 1) * 4) + i2 >= 32) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 32");
        }
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = (i >> ((i5 * 4) + i2)) & 15;
            int i7 = i3 + i5;
            if (i7 == length) {
                length++;
                sb.append(intToHexDigit(i6));
            } else {
                sb.setCharAt(i7, intToHexDigit(i6));
            }
        }
        return sb.toString();
    }

    public static String shortToHex(short s, int i, String str, int i2, int i3) {
        if (i3 == 0) {
            return str;
        }
        if (((i3 - 1) * 4) + i >= 16) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 16");
        }
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (s >> ((i4 * 4) + i)) & 15;
            int i6 = i2 + i4;
            if (i6 == length) {
                length++;
                sb.append(intToHexDigit(i5));
            } else {
                sb.setCharAt(i6, intToHexDigit(i5));
            }
        }
        return sb.toString();
    }

    public static String byteToHex(byte b, int i, String str, int i2, int i3) {
        if (i3 == 0) {
            return str;
        }
        if (((i3 - 1) * 4) + i >= 8) {
            throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 8");
        }
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = (b >> ((i4 * 4) + i)) & 15;
            int i6 = i2 + i4;
            if (i6 == length) {
                length++;
                sb.append(intToHexDigit(i5));
            } else {
                sb.setCharAt(i6, intToHexDigit(i5));
            }
        }
        return sb.toString();
    }

    public static boolean[] longToBinary(long j, int i, boolean[] zArr, int i2, int i3) {
        if (i3 == 0) {
            return zArr;
        }
        if ((i3 - 1) + i >= 64) {
            throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 64");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            zArr[i2 + i4] = ((j >> (i4 + i)) & 1) != 0;
        }
        return zArr;
    }

    public static boolean[] intToBinary(int i, int i2, boolean[] zArr, int i3, int i4) {
        if (i4 == 0) {
            return zArr;
        }
        if ((i4 - 1) + i2 >= 32) {
            throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 32");
        }
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = i3 + i5;
            boolean z = true;
            if (((i >> (i5 + i2)) & 1) == 0) {
                z = false;
            }
            zArr[i6] = z;
        }
        return zArr;
    }

    public static boolean[] shortToBinary(short s, int i, boolean[] zArr, int i2, int i3) {
        if (i3 == 0) {
            return zArr;
        }
        if ((i3 - 1) + i >= 16) {
            throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 16");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i2 + i4;
            boolean z = true;
            if (((s >> (i4 + i)) & 1) == 0) {
                z = false;
            }
            zArr[i5] = z;
        }
        return zArr;
    }

    public static boolean[] byteToBinary(byte b, int i, boolean[] zArr, int i2, int i3) {
        if (i3 == 0) {
            return zArr;
        }
        if ((i3 - 1) + i >= 8) {
            throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 8");
        }
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i2 + i4;
            boolean z = true;
            if (((b >> (i4 + i)) & 1) == 0) {
                z = false;
            }
            zArr[i5] = z;
        }
        return zArr;
    }

    public static byte[] uuidToByteArray(UUID uuid, byte[] bArr, int i, int i2) {
        if (i2 == 0) {
            return bArr;
        }
        if (i2 > 16) {
            throw new IllegalArgumentException("nBytes is greather than 16");
        }
        longToByteArray(uuid.getMostSignificantBits(), 0, bArr, i, i2 > 8 ? 8 : i2);
        if (i2 >= 8) {
            longToByteArray(uuid.getLeastSignificantBits(), 0, bArr, i + 8, i2 - 8);
        }
        return bArr;
    }

    public static UUID byteArrayToUuid(byte[] bArr, int i) {
        if (bArr.length - i < 16) {
            throw new IllegalArgumentException("Need at least 16 bytes for UUID");
        }
        return new UUID(byteArrayToLong(bArr, i, 0, 0, 8), byteArrayToLong(bArr, i + 8, 0, 0, 8));
    }
}
