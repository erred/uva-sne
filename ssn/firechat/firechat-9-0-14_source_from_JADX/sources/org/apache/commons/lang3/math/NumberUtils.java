package org.apache.commons.lang3.math;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;

public class NumberUtils {
    public static final Byte BYTE_MINUS_ONE = Byte.valueOf(-1);
    public static final Byte BYTE_ONE = Byte.valueOf(1);
    public static final Byte BYTE_ZERO = Byte.valueOf(0);
    public static final Double DOUBLE_MINUS_ONE = Double.valueOf(-1.0d);
    public static final Double DOUBLE_ONE = Double.valueOf(1.0d);
    public static final Double DOUBLE_ZERO = Double.valueOf(0.0d);
    public static final Float FLOAT_MINUS_ONE = Float.valueOf(-1.0f);
    public static final Float FLOAT_ONE = Float.valueOf(1.0f);
    public static final Float FLOAT_ZERO = Float.valueOf(0.0f);
    public static final Integer INTEGER_MINUS_ONE = Integer.valueOf(-1);
    public static final Integer INTEGER_ONE = Integer.valueOf(1);
    public static final Integer INTEGER_ZERO = Integer.valueOf(0);
    public static final Long LONG_MINUS_ONE = Long.valueOf(-1);
    public static final Long LONG_ONE = Long.valueOf(1);
    public static final Long LONG_ZERO = Long.valueOf(0);
    public static final Short SHORT_MINUS_ONE = Short.valueOf(-1);
    public static final Short SHORT_ONE = Short.valueOf(1);
    public static final Short SHORT_ZERO = Short.valueOf(0);

    public static int compare(byte b, byte b2) {
        return b - b2;
    }

    public static int compare(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        return i < i2 ? -1 : 1;
    }

    public static int compare(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j < j2 ? -1 : 1;
    }

    public static int compare(short s, short s2) {
        if (s == s2) {
            return 0;
        }
        return s < s2 ? -1 : 1;
    }

    public static byte max(byte b, byte b2, byte b3) {
        if (b2 > b) {
            b = b2;
        }
        return b3 > b ? b3 : b;
    }

    public static int max(int i, int i2, int i3) {
        if (i2 > i) {
            i = i2;
        }
        return i3 > i ? i3 : i;
    }

    public static long max(long j, long j2, long j3) {
        if (j2 > j) {
            j = j2;
        }
        return j3 > j ? j3 : j;
    }

    public static short max(short s, short s2, short s3) {
        if (s2 > s) {
            s = s2;
        }
        return s3 > s ? s3 : s;
    }

    public static byte min(byte b, byte b2, byte b3) {
        if (b2 < b) {
            b = b2;
        }
        return b3 < b ? b3 : b;
    }

    public static int min(int i, int i2, int i3) {
        if (i2 < i) {
            i = i2;
        }
        return i3 < i ? i3 : i;
    }

    public static long min(long j, long j2, long j3) {
        if (j2 < j) {
            j = j2;
        }
        return j3 < j ? j3 : j;
    }

    public static short min(short s, short s2, short s3) {
        if (s2 < s) {
            s = s2;
        }
        return s3 < s ? s3 : s;
    }

    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static int toInt(String str, int i) {
        if (str == null) {
            return i;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0);
    }

    public static long toLong(String str, long j) {
        if (str == null) {
            return j;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }

    public static float toFloat(String str, float f) {
        if (str == null) {
            return f;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException unused) {
            return f;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0.0d);
    }

    public static double toDouble(String str, double d) {
        if (str == null) {
            return d;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException unused) {
            return d;
        }
    }

    public static byte toByte(String str) {
        return toByte(str, 0);
    }

    public static byte toByte(String str, byte b) {
        if (str == null) {
            return b;
        }
        try {
            return Byte.parseByte(str);
        } catch (NumberFormatException unused) {
            return b;
        }
    }

    public static short toShort(String str) {
        return toShort(str, 0);
    }

    public static short toShort(String str, short s) {
        if (str == null) {
            return s;
        }
        try {
            return Short.parseShort(str);
        } catch (NumberFormatException unused) {
            return s;
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(11:(1:61)|62|(1:67)(1:66)|68|(5:70|(3:72|(2:74|(2:76|(1:78)))|(2:94|95)(3:88|89|90))|96|97|(1:103))|104|105|(1:111)|112|113|114) */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:127|128|129) */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x01c0, code lost:
        r1 = new java.lang.StringBuilder();
        r1.append(r13);
        r1.append(" is not a valid number.");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x01d6, code lost:
        throw new java.lang.NumberFormatException(r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x01f6, code lost:
        return createLong(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01fb, code lost:
        return createBigInteger(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0150, code lost:
        if (r1 != 'l') goto L_0x01c0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:104:0x01a5 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:112:0x01bb */
    /* JADX WARNING: Missing exception handler attribute for start block: B:127:0x01f2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Number createNumber(java.lang.String r13) throws java.lang.NumberFormatException {
        /*
            r0 = 0
            if (r13 != 0) goto L_0x0004
            return r0
        L_0x0004:
            boolean r1 = org.apache.commons.lang3.StringUtils.isBlank(r13)
            if (r1 == 0) goto L_0x0012
            java.lang.NumberFormatException r13 = new java.lang.NumberFormatException
            java.lang.String r0 = "A blank string is not a valid number"
            r13.<init>(r0)
            throw r13
        L_0x0012:
            r1 = 6
            java.lang.String[] r1 = new java.lang.String[r1]
            java.lang.String r2 = "0x"
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "0X"
            r4 = 1
            r1[r4] = r2
            r2 = 2
            java.lang.String r5 = "-0x"
            r1[r2] = r5
            r2 = 3
            java.lang.String r5 = "-0X"
            r1[r2] = r5
            r2 = 4
            java.lang.String r5 = "#"
            r1[r2] = r5
            r2 = 5
            java.lang.String r5 = "-#"
            r1[r2] = r5
            int r2 = r1.length
            r5 = 0
        L_0x0035:
            if (r5 >= r2) goto L_0x0048
            r6 = r1[r5]
            boolean r7 = r13.startsWith(r6)
            if (r7 == 0) goto L_0x0045
            int r1 = r6.length()
            int r1 = r1 + r3
            goto L_0x0049
        L_0x0045:
            int r5 = r5 + 1
            goto L_0x0035
        L_0x0048:
            r1 = 0
        L_0x0049:
            if (r1 <= 0) goto L_0x0087
            r0 = r1
        L_0x004c:
            int r2 = r13.length()
            if (r1 >= r2) goto L_0x005f
            char r3 = r13.charAt(r1)
            r2 = 48
            if (r3 != r2) goto L_0x005f
            int r0 = r0 + 1
            int r1 = r1 + 1
            goto L_0x004c
        L_0x005f:
            int r1 = r13.length()
            int r1 = r1 - r0
            r0 = 16
            if (r1 > r0) goto L_0x0082
            r2 = 55
            if (r1 != r0) goto L_0x006f
            if (r3 <= r2) goto L_0x006f
            goto L_0x0082
        L_0x006f:
            r0 = 8
            if (r1 > r0) goto L_0x007d
            if (r1 != r0) goto L_0x0078
            if (r3 <= r2) goto L_0x0078
            goto L_0x007d
        L_0x0078:
            java.lang.Integer r13 = createInteger(r13)
            return r13
        L_0x007d:
            java.lang.Long r13 = createLong(r13)
            return r13
        L_0x0082:
            java.math.BigInteger r13 = createBigInteger(r13)
            return r13
        L_0x0087:
            int r1 = r13.length()
            int r1 = r1 - r4
            char r1 = r13.charAt(r1)
            r2 = 46
            int r5 = r13.indexOf(r2)
            r6 = 101(0x65, float:1.42E-43)
            int r6 = r13.indexOf(r6)
            r7 = 69
            int r7 = r13.indexOf(r7)
            int r6 = r6 + r7
            int r6 = r6 + r4
            r7 = -1
            if (r5 <= r7) goto L_0x00db
            if (r6 <= r7) goto L_0x00d0
            if (r6 < r5) goto L_0x00b9
            int r8 = r13.length()
            if (r6 <= r8) goto L_0x00b2
            goto L_0x00b9
        L_0x00b2:
            int r8 = r5 + 1
            java.lang.String r8 = r13.substring(r8, r6)
            goto L_0x00d6
        L_0x00b9:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            java.lang.String r13 = " is not a valid number."
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            r0.<init>(r13)
            throw r0
        L_0x00d0:
            int r8 = r5 + 1
            java.lang.String r8 = r13.substring(r8)
        L_0x00d6:
            java.lang.String r5 = getMantissa(r13, r5)
            goto L_0x0104
        L_0x00db:
            if (r6 <= r7) goto L_0x00ff
            int r5 = r13.length()
            if (r6 <= r5) goto L_0x00fa
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            java.lang.String r13 = " is not a valid number."
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            r0.<init>(r13)
            throw r0
        L_0x00fa:
            java.lang.String r5 = getMantissa(r13, r6)
            goto L_0x0103
        L_0x00ff:
            java.lang.String r5 = getMantissa(r13)
        L_0x0103:
            r8 = r0
        L_0x0104:
            boolean r9 = java.lang.Character.isDigit(r1)
            r10 = 0
            r12 = 0
            if (r9 != 0) goto L_0x01d7
            if (r1 == r2) goto L_0x01d7
            if (r6 <= r7) goto L_0x0122
            int r2 = r13.length()
            int r2 = r2 - r4
            if (r6 >= r2) goto L_0x0122
            int r6 = r6 + r4
            int r0 = r13.length()
            int r0 = r0 - r4
            java.lang.String r0 = r13.substring(r6, r0)
        L_0x0122:
            int r2 = r13.length()
            int r2 = r2 - r4
            java.lang.String r2 = r13.substring(r3, r2)
            boolean r5 = isAllZeros(r5)
            if (r5 == 0) goto L_0x0139
            boolean r5 = isAllZeros(r0)
            if (r5 == 0) goto L_0x0139
            r5 = 1
            goto L_0x013a
        L_0x0139:
            r5 = 0
        L_0x013a:
            r6 = 68
            if (r1 == r6) goto L_0x01a5
            r6 = 70
            if (r1 == r6) goto L_0x0190
            r6 = 76
            if (r1 == r6) goto L_0x0153
            r6 = 100
            if (r1 == r6) goto L_0x01a5
            r6 = 102(0x66, float:1.43E-43)
            if (r1 == r6) goto L_0x0190
            r5 = 108(0x6c, float:1.51E-43)
            if (r1 == r5) goto L_0x0153
            goto L_0x01c0
        L_0x0153:
            if (r8 != 0) goto L_0x0179
            if (r0 != 0) goto L_0x0179
            char r0 = r2.charAt(r3)
            r1 = 45
            if (r0 != r1) goto L_0x0169
            java.lang.String r0 = r2.substring(r4)
            boolean r0 = isDigits(r0)
            if (r0 != 0) goto L_0x016f
        L_0x0169:
            boolean r0 = isDigits(r2)
            if (r0 == 0) goto L_0x0179
        L_0x016f:
            java.lang.Long r13 = createLong(r2)     // Catch:{ NumberFormatException -> 0x0174 }
            return r13
        L_0x0174:
            java.math.BigInteger r13 = createBigInteger(r2)
            return r13
        L_0x0179:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            java.lang.String r13 = " is not a valid number."
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            r0.<init>(r13)
            throw r0
        L_0x0190:
            java.lang.Float r0 = createFloat(r13)     // Catch:{ NumberFormatException -> 0x01a5 }
            boolean r1 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x01a5 }
            if (r1 != 0) goto L_0x01a5
            float r1 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x01a5 }
            int r1 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x01a4
            if (r5 == 0) goto L_0x01a5
        L_0x01a4:
            return r0
        L_0x01a5:
            java.lang.Double r0 = createDouble(r13)     // Catch:{ NumberFormatException -> 0x01bb }
            boolean r1 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x01bb }
            if (r1 != 0) goto L_0x01bb
            float r1 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x01bb }
            double r3 = (double) r1
            int r1 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r1 != 0) goto L_0x01ba
            if (r5 == 0) goto L_0x01bb
        L_0x01ba:
            return r0
        L_0x01bb:
            java.math.BigDecimal r0 = createBigDecimal(r2)     // Catch:{ NumberFormatException -> 0x01c0 }
            return r0
        L_0x01c0:
            java.lang.NumberFormatException r0 = new java.lang.NumberFormatException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            java.lang.String r13 = " is not a valid number."
            r1.append(r13)
            java.lang.String r13 = r1.toString()
            r0.<init>(r13)
            throw r0
        L_0x01d7:
            if (r6 <= r7) goto L_0x01e9
            int r1 = r13.length()
            int r1 = r1 - r4
            if (r6 >= r1) goto L_0x01e9
            int r6 = r6 + r4
            int r0 = r13.length()
            java.lang.String r0 = r13.substring(r6, r0)
        L_0x01e9:
            if (r8 != 0) goto L_0x01fc
            if (r0 != 0) goto L_0x01fc
            java.lang.Integer r0 = createInteger(r13)     // Catch:{ NumberFormatException -> 0x01f2 }
            return r0
        L_0x01f2:
            java.lang.Long r0 = createLong(r13)     // Catch:{ NumberFormatException -> 0x01f7 }
            return r0
        L_0x01f7:
            java.math.BigInteger r13 = createBigInteger(r13)
            return r13
        L_0x01fc:
            boolean r1 = isAllZeros(r5)
            if (r1 == 0) goto L_0x0209
            boolean r0 = isAllZeros(r0)
            if (r0 == 0) goto L_0x0209
            r3 = 1
        L_0x0209:
            java.lang.Float r0 = createFloat(r13)     // Catch:{ NumberFormatException -> 0x0254 }
            java.lang.Double r1 = createDouble(r13)     // Catch:{ NumberFormatException -> 0x0254 }
            boolean r2 = r0.isInfinite()     // Catch:{ NumberFormatException -> 0x0254 }
            if (r2 != 0) goto L_0x0230
            float r2 = r0.floatValue()     // Catch:{ NumberFormatException -> 0x0254 }
            int r2 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r2 != 0) goto L_0x0221
            if (r3 == 0) goto L_0x0230
        L_0x0221:
            java.lang.String r2 = r0.toString()     // Catch:{ NumberFormatException -> 0x0254 }
            java.lang.String r4 = r1.toString()     // Catch:{ NumberFormatException -> 0x0254 }
            boolean r2 = r2.equals(r4)     // Catch:{ NumberFormatException -> 0x0254 }
            if (r2 == 0) goto L_0x0230
            return r0
        L_0x0230:
            boolean r0 = r1.isInfinite()     // Catch:{ NumberFormatException -> 0x0254 }
            if (r0 != 0) goto L_0x0254
            double r4 = r1.doubleValue()     // Catch:{ NumberFormatException -> 0x0254 }
            int r0 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0240
            if (r3 == 0) goto L_0x0254
        L_0x0240:
            java.math.BigDecimal r0 = createBigDecimal(r13)     // Catch:{ NumberFormatException -> 0x0254 }
            double r2 = r1.doubleValue()     // Catch:{ NumberFormatException -> 0x0254 }
            java.math.BigDecimal r2 = java.math.BigDecimal.valueOf(r2)     // Catch:{ NumberFormatException -> 0x0254 }
            int r2 = r0.compareTo(r2)     // Catch:{ NumberFormatException -> 0x0254 }
            if (r2 != 0) goto L_0x0253
            return r1
        L_0x0253:
            return r0
        L_0x0254:
            java.math.BigDecimal r13 = createBigDecimal(r13)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.createNumber(java.lang.String):java.lang.Number");
    }

    private static String getMantissa(String str) {
        return getMantissa(str, str.length());
    }

    private static String getMantissa(String str, int i) {
        char charAt = str.charAt(0);
        return charAt == '-' || charAt == '+' ? str.substring(1, i) : str.substring(0, i);
    }

    private static boolean isAllZeros(String str) {
        boolean z = true;
        if (str == null) {
            return true;
        }
        for (int length = str.length() - 1; length >= 0; length--) {
            if (str.charAt(length) != '0') {
                return false;
            }
        }
        if (str.length() <= 0) {
            z = false;
        }
        return z;
    }

    public static Float createFloat(String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }

    public static Integer createInteger(String str) {
        if (str == null) {
            return null;
        }
        return Integer.decode(str);
    }

    public static Long createLong(String str) {
        if (str == null) {
            return null;
        }
        return Long.decode(str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0054  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.math.BigInteger createBigInteger(java.lang.String r5) {
        /*
            if (r5 != 0) goto L_0x0004
            r5 = 0
            return r5
        L_0x0004:
            r0 = 10
            java.lang.String r1 = "-"
            boolean r1 = r5.startsWith(r1)
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0012
            r2 = 1
            goto L_0x0013
        L_0x0012:
            r3 = 0
        L_0x0013:
            java.lang.String r1 = "0x"
            boolean r1 = r5.startsWith(r1, r2)
            r4 = 16
            if (r1 != 0) goto L_0x0045
            java.lang.String r1 = "0X"
            boolean r1 = r5.startsWith(r1, r2)
            if (r1 == 0) goto L_0x0026
            goto L_0x0045
        L_0x0026:
            java.lang.String r1 = "#"
            boolean r1 = r5.startsWith(r1, r2)
            if (r1 == 0) goto L_0x0031
            int r2 = r2 + 1
            goto L_0x0047
        L_0x0031:
            java.lang.String r1 = "0"
            boolean r1 = r5.startsWith(r1, r2)
            if (r1 == 0) goto L_0x0049
            int r1 = r5.length()
            int r4 = r2 + 1
            if (r1 <= r4) goto L_0x0049
            r0 = 8
            r2 = r4
            goto L_0x0049
        L_0x0045:
            int r2 = r2 + 2
        L_0x0047:
            r0 = 16
        L_0x0049:
            java.math.BigInteger r1 = new java.math.BigInteger
            java.lang.String r5 = r5.substring(r2)
            r1.<init>(r5, r0)
            if (r3 == 0) goto L_0x0058
            java.math.BigInteger r1 = r1.negate()
        L_0x0058:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.math.NumberUtils.createBigInteger(java.lang.String):java.math.BigInteger");
    }

    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isBlank(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        } else if (!str.trim().startsWith(HelpFormatter.DEFAULT_LONG_OPT_PREFIX)) {
            return new BigDecimal(str);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" is not a valid number.");
            throw new NumberFormatException(sb.toString());
        }
    }

    public static long min(long... jArr) {
        validateArray(jArr);
        long j = jArr[0];
        for (int i = 1; i < jArr.length; i++) {
            if (jArr[i] < j) {
                j = jArr[i];
            }
        }
        return j;
    }

    public static int min(int... iArr) {
        validateArray(iArr);
        int i = iArr[0];
        for (int i2 = 1; i2 < iArr.length; i2++) {
            if (iArr[i2] < i) {
                i = iArr[i2];
            }
        }
        return i;
    }

    public static short min(short... sArr) {
        validateArray(sArr);
        short s = sArr[0];
        for (int i = 1; i < sArr.length; i++) {
            if (sArr[i] < s) {
                s = sArr[i];
            }
        }
        return s;
    }

    public static byte min(byte... bArr) {
        validateArray(bArr);
        byte b = bArr[0];
        for (int i = 1; i < bArr.length; i++) {
            if (bArr[i] < b) {
                b = bArr[i];
            }
        }
        return b;
    }

    public static double min(double... dArr) {
        validateArray(dArr);
        double d = dArr[0];
        for (int i = 1; i < dArr.length; i++) {
            if (Double.isNaN(dArr[i])) {
                return Double.NaN;
            }
            if (dArr[i] < d) {
                d = dArr[i];
            }
        }
        return d;
    }

    public static float min(float... fArr) {
        validateArray(fArr);
        float f = fArr[0];
        for (int i = 1; i < fArr.length; i++) {
            if (Float.isNaN(fArr[i])) {
                return Float.NaN;
            }
            if (fArr[i] < f) {
                f = fArr[i];
            }
        }
        return f;
    }

    public static long max(long... jArr) {
        validateArray(jArr);
        long j = jArr[0];
        for (int i = 1; i < jArr.length; i++) {
            if (jArr[i] > j) {
                j = jArr[i];
            }
        }
        return j;
    }

    public static int max(int... iArr) {
        validateArray(iArr);
        int i = iArr[0];
        for (int i2 = 1; i2 < iArr.length; i2++) {
            if (iArr[i2] > i) {
                i = iArr[i2];
            }
        }
        return i;
    }

    public static short max(short... sArr) {
        validateArray(sArr);
        short s = sArr[0];
        for (int i = 1; i < sArr.length; i++) {
            if (sArr[i] > s) {
                s = sArr[i];
            }
        }
        return s;
    }

    public static byte max(byte... bArr) {
        validateArray(bArr);
        byte b = bArr[0];
        for (int i = 1; i < bArr.length; i++) {
            if (bArr[i] > b) {
                b = bArr[i];
            }
        }
        return b;
    }

    public static double max(double... dArr) {
        validateArray(dArr);
        double d = dArr[0];
        for (int i = 1; i < dArr.length; i++) {
            if (Double.isNaN(dArr[i])) {
                return Double.NaN;
            }
            if (dArr[i] > d) {
                d = dArr[i];
            }
        }
        return d;
    }

    public static float max(float... fArr) {
        validateArray(fArr);
        float f = fArr[0];
        for (int i = 1; i < fArr.length; i++) {
            if (Float.isNaN(fArr[i])) {
                return Float.NaN;
            }
            if (fArr[i] > f) {
                f = fArr[i];
            }
        }
        return f;
    }

    private static void validateArray(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        Validate.isTrue(Array.getLength(obj) != 0, "Array cannot be empty.", new Object[0]);
    }

    public static double min(double d, double d2, double d3) {
        return Math.min(Math.min(d, d2), d3);
    }

    public static float min(float f, float f2, float f3) {
        return Math.min(Math.min(f, f2), f3);
    }

    public static double max(double d, double d2, double d3) {
        return Math.max(Math.max(d, d2), d3);
    }

    public static float max(float f, float f2, float f3) {
        return Math.max(Math.max(f, f2), f3);
    }

    public static boolean isDigits(String str) {
        return StringUtils.isNumeric(str);
    }

    @Deprecated
    public static boolean isNumber(String str) {
        return isCreatable(str);
    }

    public static boolean isCreatable(String str) {
        boolean z = false;
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        boolean z2 = true;
        int i = (charArray[0] == '-' || charArray[0] == '+') ? 1 : 0;
        boolean z3 = i == 1 && charArray[0] == '+';
        int i2 = i + 1;
        if (length > i2 && charArray[i] == '0') {
            if (charArray[i2] == 'x' || charArray[i2] == 'X') {
                int i3 = i + 2;
                if (i3 == length) {
                    return false;
                }
                while (i3 < charArray.length) {
                    if ((charArray[i3] < '0' || charArray[i3] > '9') && ((charArray[i3] < 'a' || charArray[i3] > 'f') && (charArray[i3] < 'A' || charArray[i3] > 'F'))) {
                        return false;
                    }
                    i3++;
                }
                return true;
            } else if (Character.isDigit(charArray[i2])) {
                while (i2 < charArray.length) {
                    if (charArray[i2] < '0' || charArray[i2] > '7') {
                        return false;
                    }
                    i2++;
                }
                return true;
            }
        }
        int i4 = length - 1;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        boolean z7 = false;
        while (true) {
            if (i >= i4) {
                if (i >= i4 + 1 || !z4 || z5) {
                }
            }
            if (charArray[i] >= '0' && charArray[i] <= '9') {
                z4 = false;
                z5 = true;
            } else if (charArray[i] == '.') {
                if (z6 || z7) {
                    return false;
                }
                z6 = true;
            } else if (charArray[i] != 'e' && charArray[i] != 'E') {
                if (charArray[i] != '+') {
                    if (charArray[i] != '-') {
                        return false;
                    }
                }
                if (!z4) {
                    return false;
                }
                z4 = false;
                z5 = false;
            } else if (z7 || !z5) {
                return false;
            } else {
                z4 = true;
                z7 = true;
            }
            i++;
            z2 = true;
        }
        if (i >= charArray.length) {
            if (!z4 && z5) {
                z = true;
            }
            return z;
        } else if (charArray[i] < '0' || charArray[i] > '9') {
            if (charArray[i] == 'e' || charArray[i] == 'E') {
                return false;
            }
            if (charArray[i] == '.') {
                if (z6 || z7) {
                    return false;
                }
                return z5;
            } else if (!z4 && (charArray[i] == 'd' || charArray[i] == 'D' || charArray[i] == 'f' || charArray[i] == 'F')) {
                return z5;
            } else {
                if (charArray[i] != 'l' && charArray[i] != 'L') {
                    return false;
                }
                if (z5 && !z7 && !z6) {
                    z = true;
                }
                return z;
            }
        } else if (!SystemUtils.IS_JAVA_1_6 || !z3 || z6) {
            return z2;
        } else {
            return false;
        }
    }

    public static boolean isParsable(String str) {
        if (StringUtils.isEmpty(str) || str.charAt(str.length() - 1) == '.') {
            return false;
        }
        if (str.charAt(0) != '-') {
            return withDecimalsParsing(str, 0);
        }
        if (str.length() == 1) {
            return false;
        }
        return withDecimalsParsing(str, 1);
    }

    private static boolean withDecimalsParsing(String str, int i) {
        int i2 = 0;
        while (i < str.length()) {
            boolean z = str.charAt(i) == '.';
            if (z) {
                i2++;
            }
            if (i2 > 1) {
                return false;
            }
            if (!z && !Character.isDigit(str.charAt(i))) {
                return false;
            }
            i++;
        }
        return true;
    }
}
