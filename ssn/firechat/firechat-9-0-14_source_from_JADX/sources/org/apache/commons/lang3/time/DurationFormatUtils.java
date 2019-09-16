package org.apache.commons.lang3.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class DurationFormatUtils {

    /* renamed from: H */
    static final Object f162H = "H";
    public static final String ISO_EXTENDED_FORMAT_PATTERN = "'P'yyyy'Y'M'M'd'DT'H'H'm'M's.SSS'S'";

    /* renamed from: M */
    static final Object f163M = "M";

    /* renamed from: S */
    static final Object f164S = "S";

    /* renamed from: d */
    static final Object f165d = "d";

    /* renamed from: m */
    static final Object f166m = "m";

    /* renamed from: s */
    static final Object f167s = "s";

    /* renamed from: y */
    static final Object f168y = "y";

    static class Token {
        private int count;
        private final Object value;

        static boolean containsTokenWithValue(Token[] tokenArr, Object obj) {
            for (Token value2 : tokenArr) {
                if (value2.getValue() == obj) {
                    return true;
                }
            }
            return false;
        }

        Token(Object obj) {
            this.value = obj;
            this.count = 1;
        }

        Token(Object obj, int i) {
            this.value = obj;
            this.count = i;
        }

        /* access modifiers changed from: 0000 */
        public void increment() {
            this.count++;
        }

        /* access modifiers changed from: 0000 */
        public int getCount() {
            return this.count;
        }

        /* access modifiers changed from: 0000 */
        public Object getValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!(obj instanceof Token)) {
                return false;
            }
            Token token = (Token) obj;
            if (this.value.getClass() != token.value.getClass() || this.count != token.count) {
                return false;
            }
            if (this.value instanceof StringBuilder) {
                return this.value.toString().equals(token.value.toString());
            }
            if (this.value instanceof Number) {
                return this.value.equals(token.value);
            }
            if (this.value == token.value) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return this.value.hashCode();
        }

        public String toString() {
            return StringUtils.repeat(this.value.toString(), this.count);
        }
    }

    public static String formatDurationHMS(long j) {
        return formatDuration(j, "HH:mm:ss.SSS");
    }

    public static String formatDurationISO(long j) {
        return formatDuration(j, ISO_EXTENDED_FORMAT_PATTERN, false);
    }

    public static String formatDuration(long j, String str) {
        return formatDuration(j, str, true);
    }

    public static String formatDuration(long j, String str, boolean z) {
        long j2;
        long j3;
        long j4;
        long j5;
        long j6;
        long j7;
        Validate.inclusiveBetween(0, Long.MAX_VALUE, j, "durationMillis must not be negative");
        Token[] lexx = lexx(str);
        if (Token.containsTokenWithValue(lexx, f165d)) {
            long j8 = j / 86400000;
            j2 = j - (86400000 * j8);
            j3 = j8;
        } else {
            j2 = j;
            j3 = 0;
        }
        if (Token.containsTokenWithValue(lexx, f162H)) {
            long j9 = j2 / DateUtils.MILLIS_PER_HOUR;
            j2 -= DateUtils.MILLIS_PER_HOUR * j9;
            j4 = j9;
        } else {
            j4 = 0;
        }
        if (Token.containsTokenWithValue(lexx, f166m)) {
            long j10 = j2 / DateUtils.MILLIS_PER_MINUTE;
            j2 -= DateUtils.MILLIS_PER_MINUTE * j10;
            j5 = j10;
        } else {
            j5 = 0;
        }
        if (Token.containsTokenWithValue(lexx, f167s)) {
            long j11 = j2 / 1000;
            j7 = j11;
            j6 = j2 - (1000 * j11);
        } else {
            j7 = 0;
            j6 = j2;
        }
        return format(lexx, 0, 0, j3, j4, j5, j7, j6, z);
    }

    public static String formatDurationWords(long j, boolean z, boolean z2) {
        String formatDuration = formatDuration(j, "d' days 'H' hours 'm' minutes 's' seconds'");
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.SPACE);
            sb.append(formatDuration);
            formatDuration = sb.toString();
            String replaceOnce = StringUtils.replaceOnce(formatDuration, " 0 days", "");
            if (replaceOnce.length() != formatDuration.length()) {
                String replaceOnce2 = StringUtils.replaceOnce(replaceOnce, " 0 hours", "");
                if (replaceOnce2.length() != replaceOnce.length()) {
                    formatDuration = StringUtils.replaceOnce(replaceOnce2, " 0 minutes", "");
                    if (formatDuration.length() != formatDuration.length()) {
                        formatDuration = StringUtils.replaceOnce(formatDuration, " 0 seconds", "");
                    }
                } else {
                    formatDuration = replaceOnce;
                }
            }
            if (formatDuration.length() != 0) {
                formatDuration = formatDuration.substring(1);
            }
        }
        if (z2) {
            String replaceOnce3 = StringUtils.replaceOnce(formatDuration, " 0 seconds", "");
            if (replaceOnce3.length() != formatDuration.length()) {
                formatDuration = StringUtils.replaceOnce(replaceOnce3, " 0 minutes", "");
                if (formatDuration.length() != replaceOnce3.length()) {
                    String replaceOnce4 = StringUtils.replaceOnce(formatDuration, " 0 hours", "");
                    if (replaceOnce4.length() != formatDuration.length()) {
                        formatDuration = StringUtils.replaceOnce(replaceOnce4, " 0 days", "");
                    }
                } else {
                    formatDuration = replaceOnce3;
                }
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(StringUtils.SPACE);
        sb2.append(formatDuration);
        return StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(sb2.toString(), " 1 seconds", " 1 second"), " 1 minutes", " 1 minute"), " 1 hours", " 1 hour"), " 1 days", " 1 day").trim();
    }

    public static String formatPeriodISO(long j, long j2) {
        return formatPeriod(j, j2, ISO_EXTENDED_FORMAT_PATTERN, false, TimeZone.getDefault());
    }

    public static String formatPeriod(long j, long j2, String str) {
        return formatPeriod(j, j2, str, true, TimeZone.getDefault());
    }

    public static String formatPeriod(long j, long j2, String str, boolean z, TimeZone timeZone) {
        int i;
        long j3 = j;
        long j4 = j2;
        Validate.isTrue(j3 <= j4, "startMillis must not be greater than endMillis", new Object[0]);
        Token[] lexx = lexx(str);
        Calendar instance = Calendar.getInstance(timeZone);
        instance.setTime(new Date(j3));
        Calendar instance2 = Calendar.getInstance(timeZone);
        instance2.setTime(new Date(j4));
        int i2 = instance2.get(14) - instance.get(14);
        int i3 = instance2.get(13) - instance.get(13);
        int i4 = instance2.get(12) - instance.get(12);
        int i5 = instance2.get(11) - instance.get(11);
        int i6 = instance2.get(5) - instance.get(5);
        int i7 = instance2.get(2) - instance.get(2);
        int i8 = instance2.get(1) - instance.get(1);
        while (i2 < 0) {
            i2 += 1000;
            i3--;
        }
        while (i3 < 0) {
            i3 += 60;
            i4--;
        }
        while (i4 < 0) {
            i4 += 60;
            i5--;
        }
        while (i5 < 0) {
            i5 += 24;
            i6--;
        }
        if (Token.containsTokenWithValue(lexx, f163M)) {
            while (i6 < 0) {
                i6 += instance.getActualMaximum(5);
                i7--;
                instance.add(2, 1);
            }
            while (i < 0) {
                i7 = i + 12;
                i8--;
            }
            if (!Token.containsTokenWithValue(lexx, f168y) && i8 != 0) {
                while (i8 != 0) {
                    i += i8 * 12;
                    i8 = 0;
                }
            }
        } else {
            if (!Token.containsTokenWithValue(lexx, f168y)) {
                int i9 = instance2.get(1);
                if (i7 < 0) {
                    i9--;
                }
                while (instance.get(1) != i9) {
                    int actualMaximum = i6 + (instance.getActualMaximum(6) - instance.get(6));
                    if ((instance instanceof GregorianCalendar) && instance.get(2) == 1 && instance.get(5) == 29) {
                        actualMaximum++;
                    }
                    instance.add(1, 1);
                    i6 = actualMaximum + instance.get(6);
                }
                i8 = 0;
            }
            while (instance.get(2) != instance2.get(2)) {
                i6 += instance.getActualMaximum(5);
                instance.add(2, 1);
            }
            i = 0;
            while (i6 < 0) {
                i6 += instance.getActualMaximum(5);
                i--;
                instance.add(2, 1);
            }
        }
        if (!Token.containsTokenWithValue(lexx, f165d)) {
            i5 += i6 * 24;
            i6 = 0;
        }
        if (!Token.containsTokenWithValue(lexx, f162H)) {
            i4 += i5 * 60;
            i5 = 0;
        }
        if (!Token.containsTokenWithValue(lexx, f166m)) {
            i3 += i4 * 60;
            i4 = 0;
        }
        if (!Token.containsTokenWithValue(lexx, f167s)) {
            i2 += i3 * 1000;
            i3 = 0;
        }
        return format(lexx, (long) i8, (long) i, (long) i6, (long) i5, (long) i4, (long) i3, (long) i2, z);
    }

    static String format(Token[] tokenArr, long j, long j2, long j3, long j4, long j5, long j6, long j7, boolean z) {
        int i;
        int i2;
        Token[] tokenArr2 = tokenArr;
        long j8 = j7;
        boolean z2 = z;
        StringBuilder sb = new StringBuilder();
        int length = tokenArr2.length;
        int i3 = 0;
        boolean z3 = false;
        while (i3 < length) {
            Token token = tokenArr2[i3];
            Object value = token.getValue();
            int count = token.getCount();
            if (value instanceof StringBuilder) {
                sb.append(value.toString());
                long j9 = j3;
                i = length;
                i2 = i3;
            } else {
                if (value.equals(f168y)) {
                    sb.append(paddedValue(j, z2, count));
                    long j10 = j3;
                    i = length;
                    i2 = i3;
                } else {
                    long j11 = j;
                    if (value.equals(f163M)) {
                        i2 = i3;
                        sb.append(paddedValue(j2, z2, count));
                        long j12 = j3;
                    } else {
                        i2 = i3;
                        long j13 = j2;
                        if (value.equals(f165d)) {
                            sb.append(paddedValue(j3, z2, count));
                        } else {
                            long j14 = j3;
                            if (value.equals(f162H)) {
                                i = length;
                                sb.append(paddedValue(j4, z2, count));
                            } else {
                                i = length;
                                long j15 = j4;
                                if (value.equals(f166m)) {
                                    sb.append(paddedValue(j5, z2, count));
                                } else {
                                    long j16 = j5;
                                    if (value.equals(f167s)) {
                                        sb.append(paddedValue(j6, z2, count));
                                        z3 = true;
                                    } else {
                                        long j17 = j6;
                                        if (value.equals(f164S)) {
                                            if (z3) {
                                                int i4 = 3;
                                                if (z2) {
                                                    i4 = Math.max(3, count);
                                                }
                                                sb.append(paddedValue(j8, true, i4));
                                            } else {
                                                sb.append(paddedValue(j8, z2, count));
                                            }
                                            z3 = false;
                                        }
                                    }
                                    i3 = i2 + 1;
                                    length = i;
                                }
                            }
                            long j18 = j6;
                            z3 = false;
                            i3 = i2 + 1;
                            length = i;
                        }
                    }
                    i = length;
                }
                z3 = false;
            }
            long j19 = j6;
            i3 = i2 + 1;
            length = i;
        }
        return sb.toString();
    }

    private static String paddedValue(long j, boolean z, int i) {
        String l = Long.toString(j);
        return z ? StringUtils.leftPad(l, i, '0') : l;
    }

    static Token[] lexx(String str) {
        Object obj;
        ArrayList arrayList = new ArrayList(str.length());
        StringBuilder sb = null;
        Token token = null;
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (!z || charAt == '\'') {
                if (charAt != '\'') {
                    if (charAt == 'H') {
                        obj = f162H;
                    } else if (charAt == 'M') {
                        obj = f163M;
                    } else if (charAt == 'S') {
                        obj = f164S;
                    } else if (charAt == 'd') {
                        obj = f165d;
                    } else if (charAt == 'm') {
                        obj = f166m;
                    } else if (charAt == 's') {
                        obj = f167s;
                    } else if (charAt != 'y') {
                        if (sb == null) {
                            sb = new StringBuilder();
                            arrayList.add(new Token(sb));
                        }
                        sb.append(charAt);
                        obj = null;
                    } else {
                        obj = f168y;
                    }
                } else if (z) {
                    sb = null;
                    obj = null;
                    z = false;
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    arrayList.add(new Token(sb2));
                    obj = null;
                    sb = sb2;
                    z = true;
                }
                if (obj != null) {
                    if (token == null || !token.getValue().equals(obj)) {
                        token = new Token(obj);
                        arrayList.add(token);
                    } else {
                        token.increment();
                    }
                    sb = null;
                }
            } else {
                sb.append(charAt);
            }
        }
        if (!z) {
            return (Token[]) arrayList.toArray(new Token[arrayList.size()]);
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Unmatched quote in format: ");
        sb3.append(str);
        throw new IllegalArgumentException(sb3.toString());
    }
}
