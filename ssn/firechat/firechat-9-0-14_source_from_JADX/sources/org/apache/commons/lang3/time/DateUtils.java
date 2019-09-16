package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final long MILLIS_PER_DAY = 86400000;
    public static final long MILLIS_PER_HOUR = 3600000;
    public static final long MILLIS_PER_MINUTE = 60000;
    public static final long MILLIS_PER_SECOND = 1000;
    public static final int RANGE_MONTH_MONDAY = 6;
    public static final int RANGE_MONTH_SUNDAY = 5;
    public static final int RANGE_WEEK_CENTER = 4;
    public static final int RANGE_WEEK_MONDAY = 2;
    public static final int RANGE_WEEK_RELATIVE = 3;
    public static final int RANGE_WEEK_SUNDAY = 1;
    public static final int SEMI_MONTH = 1001;
    private static final int[][] fields = {new int[]{14}, new int[]{13}, new int[]{12}, new int[]{11, 10}, new int[]{5, 5, 9}, new int[]{2, 1001}, new int[]{1}, new int[]{0}};

    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar calendar, Calendar calendar2) {
            this.endFinal = calendar2;
            this.spot = calendar;
            this.spot.add(5, -1);
        }

        public boolean hasNext() {
            return this.spot.before(this.endFinal);
        }

        public Calendar next() {
            if (this.spot.equals(this.endFinal)) {
                throw new NoSuchElementException();
            }
            this.spot.add(5, 1);
            return (Calendar) this.spot.clone();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private enum ModifyType {
        TRUNCATE,
        ROUND,
        CEILING
    }

    public static boolean isSameDay(Date date, Date date2) {
        if (date == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTime(date2);
        return isSameDay(instance, instance2);
    }

    public static boolean isSameDay(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (calendar.get(0) == calendar2.get(0) && calendar.get(1) == calendar2.get(1) && calendar.get(6) == calendar2.get(6)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSameInstant(Date date, Date date2) {
        if (date != null && date2 != null) {
            return date.getTime() == date2.getTime();
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean isSameInstant(Calendar calendar, Calendar calendar2) {
        if (calendar != null && calendar2 != null) {
            return calendar.getTime().getTime() == calendar2.getTime().getTime();
        }
        throw new IllegalArgumentException("The date must not be null");
    }

    public static boolean isSameLocalTime(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (calendar.get(14) == calendar2.get(14) && calendar.get(13) == calendar2.get(13) && calendar.get(12) == calendar2.get(12) && calendar.get(11) == calendar2.get(11) && calendar.get(6) == calendar2.get(6) && calendar.get(1) == calendar2.get(1) && calendar.get(0) == calendar2.get(0) && calendar.getClass() == calendar2.getClass()) {
            return true;
        } else {
            return false;
        }
    }

    public static Date parseDate(String str, String... strArr) throws ParseException {
        return parseDate(str, null, strArr);
    }

    public static Date parseDate(String str, Locale locale, String... strArr) throws ParseException {
        return parseDateWithLeniency(str, locale, strArr, true);
    }

    public static Date parseDateStrictly(String str, String... strArr) throws ParseException {
        return parseDateStrictly(str, null, strArr);
    }

    public static Date parseDateStrictly(String str, Locale locale, String... strArr) throws ParseException {
        return parseDateWithLeniency(str, locale, strArr, false);
    }

    private static Date parseDateWithLeniency(String str, Locale locale, String[] strArr, boolean z) throws ParseException {
        if (str == null || strArr == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        TimeZone timeZone = TimeZone.getDefault();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ParsePosition parsePosition = new ParsePosition(0);
        Calendar instance = Calendar.getInstance(timeZone, locale);
        instance.setLenient(z);
        for (String fastDateParser : strArr) {
            FastDateParser fastDateParser2 = new FastDateParser(fastDateParser, timeZone, locale);
            instance.clear();
            try {
                if (fastDateParser2.parse(str, parsePosition, instance) && parsePosition.getIndex() == str.length()) {
                    return instance.getTime();
                }
            } catch (IllegalArgumentException unused) {
            }
            parsePosition.setIndex(0);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unable to parse the date: ");
        sb.append(str);
        throw new ParseException(sb.toString(), -1);
    }

    public static Date addYears(Date date, int i) {
        return add(date, 1, i);
    }

    public static Date addMonths(Date date, int i) {
        return add(date, 2, i);
    }

    public static Date addWeeks(Date date, int i) {
        return add(date, 3, i);
    }

    public static Date addDays(Date date, int i) {
        return add(date, 5, i);
    }

    public static Date addHours(Date date, int i) {
        return add(date, 11, i);
    }

    public static Date addMinutes(Date date, int i) {
        return add(date, 12, i);
    }

    public static Date addSeconds(Date date, int i) {
        return add(date, 13, i);
    }

    public static Date addMilliseconds(Date date, int i) {
        return add(date, 14, i);
    }

    private static Date add(Date date, int i, int i2) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(i, i2);
        return instance.getTime();
    }

    public static Date setYears(Date date, int i) {
        return set(date, 1, i);
    }

    public static Date setMonths(Date date, int i) {
        return set(date, 2, i);
    }

    public static Date setDays(Date date, int i) {
        return set(date, 5, i);
    }

    public static Date setHours(Date date, int i) {
        return set(date, 11, i);
    }

    public static Date setMinutes(Date date, int i) {
        return set(date, 12, i);
    }

    public static Date setSeconds(Date date, int i) {
        return set(date, 13, i);
    }

    public static Date setMilliseconds(Date date, int i) {
        return set(date, 14, i);
    }

    private static Date set(Date date, int i, int i2) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setLenient(false);
        instance.setTime(date);
        instance.set(i, i2);
        return instance.getTime();
    }

    public static Calendar toCalendar(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    public static Calendar toCalendar(Date date, TimeZone timeZone) {
        Calendar instance = Calendar.getInstance(timeZone);
        instance.setTime(date);
        return instance;
    }

    public static Date round(Date date, int i) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        modify(instance, i, ModifyType.ROUND);
        return instance.getTime();
    }

    public static Calendar round(Calendar calendar, int i) {
        if (calendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar calendar2 = (Calendar) calendar.clone();
        modify(calendar2, i, ModifyType.ROUND);
        return calendar2;
    }

    public static Date round(Object obj, int i) {
        if (obj == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (obj instanceof Date) {
            return round((Date) obj, i);
        } else {
            if (obj instanceof Calendar) {
                return round((Calendar) obj, i).getTime();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not round ");
            sb.append(obj);
            throw new ClassCastException(sb.toString());
        }
    }

    public static Date truncate(Date date, int i) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        modify(instance, i, ModifyType.TRUNCATE);
        return instance.getTime();
    }

    public static Calendar truncate(Calendar calendar, int i) {
        if (calendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar calendar2 = (Calendar) calendar.clone();
        modify(calendar2, i, ModifyType.TRUNCATE);
        return calendar2;
    }

    public static Date truncate(Object obj, int i) {
        if (obj == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (obj instanceof Date) {
            return truncate((Date) obj, i);
        } else {
            if (obj instanceof Calendar) {
                return truncate((Calendar) obj, i).getTime();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not truncate ");
            sb.append(obj);
            throw new ClassCastException(sb.toString());
        }
    }

    public static Date ceiling(Date date, int i) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        modify(instance, i, ModifyType.CEILING);
        return instance.getTime();
    }

    public static Calendar ceiling(Calendar calendar, int i) {
        if (calendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar calendar2 = (Calendar) calendar.clone();
        modify(calendar2, i, ModifyType.CEILING);
        return calendar2;
    }

    public static Date ceiling(Object obj, int i) {
        if (obj == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (obj instanceof Date) {
            return ceiling((Date) obj, i);
        } else {
            if (obj instanceof Calendar) {
                return ceiling((Calendar) obj, i).getTime();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find ceiling of for type: ");
            sb.append(obj.getClass());
            throw new ClassCastException(sb.toString());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x014e A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0142  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void modify(java.util.Calendar r17, int r18, org.apache.commons.lang3.time.DateUtils.ModifyType r19) {
        /*
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = 1
            int r4 = r0.get(r3)
            r5 = 280000000(0x10b07600, float:6.960157E-29)
            if (r4 <= r5) goto L_0x0018
            java.lang.ArithmeticException r0 = new java.lang.ArithmeticException
            java.lang.String r1 = "Calendar value too large for accurate calculations"
            r0.<init>(r1)
            throw r0
        L_0x0018:
            r4 = 14
            if (r1 != r4) goto L_0x001d
            return
        L_0x001d:
            java.util.Date r5 = r17.getTime()
            long r6 = r5.getTime()
            int r4 = r0.get(r4)
            org.apache.commons.lang3.time.DateUtils$ModifyType r8 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r8 == r2) goto L_0x0031
            r8 = 500(0x1f4, float:7.0E-43)
            if (r4 >= r8) goto L_0x0035
        L_0x0031:
            long r8 = (long) r4
            long r10 = r6 - r8
            r6 = r10
        L_0x0035:
            r4 = 13
            if (r1 != r4) goto L_0x003b
            r9 = 1
            goto L_0x003c
        L_0x003b:
            r9 = 0
        L_0x003c:
            int r4 = r0.get(r4)
            r10 = 30
            if (r9 != 0) goto L_0x0052
            org.apache.commons.lang3.time.DateUtils$ModifyType r11 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r11 == r2) goto L_0x004a
            if (r4 >= r10) goto L_0x0052
        L_0x004a:
            long r11 = (long) r4
            r13 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 * r13
            long r13 = r6 - r11
            goto L_0x0053
        L_0x0052:
            r13 = r6
        L_0x0053:
            r4 = 12
            if (r1 != r4) goto L_0x0058
            r9 = 1
        L_0x0058:
            int r6 = r0.get(r4)
            if (r9 != 0) goto L_0x006d
            org.apache.commons.lang3.time.DateUtils$ModifyType r7 = org.apache.commons.lang3.time.DateUtils.ModifyType.TRUNCATE
            if (r7 == r2) goto L_0x0064
            if (r6 >= r10) goto L_0x006d
        L_0x0064:
            long r6 = (long) r6
            r9 = 60000(0xea60, double:2.9644E-319)
            long r6 = r6 * r9
            long r9 = r13 - r6
            r13 = r9
        L_0x006d:
            long r6 = r5.getTime()
            int r9 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x007b
            r5.setTime(r13)
            r0.setTime(r5)
        L_0x007b:
            int[][] r5 = fields
            int r6 = r5.length
            r7 = 0
            r9 = 0
        L_0x0080:
            if (r7 >= r6) goto L_0x0154
            r10 = r5[r7]
            int r11 = r10.length
            r12 = 0
        L_0x0086:
            r14 = 2
            r15 = 15
            r8 = 1001(0x3e9, float:1.403E-42)
            r4 = 5
            if (r12 >= r11) goto L_0x00d9
            r13 = r10[r12]
            if (r13 != r1) goto L_0x00d3
            org.apache.commons.lang3.time.DateUtils$ModifyType r5 = org.apache.commons.lang3.time.DateUtils.ModifyType.CEILING
            if (r2 == r5) goto L_0x009c
            org.apache.commons.lang3.time.DateUtils$ModifyType r5 = org.apache.commons.lang3.time.DateUtils.ModifyType.ROUND
            if (r2 != r5) goto L_0x00d2
            if (r9 == 0) goto L_0x00d2
        L_0x009c:
            if (r1 != r8) goto L_0x00b1
            int r1 = r0.get(r4)
            if (r1 != r3) goto L_0x00a8
            r0.add(r4, r15)
            goto L_0x00d2
        L_0x00a8:
            r1 = -15
            r0.add(r4, r1)
            r0.add(r14, r3)
            goto L_0x00d2
        L_0x00b1:
            r2 = 9
            if (r1 != r2) goto L_0x00cc
            r1 = 11
            int r2 = r0.get(r1)
            if (r2 != 0) goto L_0x00c3
            r2 = 12
            r0.add(r1, r2)
            goto L_0x00d2
        L_0x00c3:
            r2 = -12
            r0.add(r1, r2)
            r0.add(r4, r3)
            goto L_0x00d2
        L_0x00cc:
            r13 = 0
            r1 = r10[r13]
            r0.add(r1, r3)
        L_0x00d2:
            return
        L_0x00d3:
            r13 = 0
            int r12 = r12 + 1
            r4 = 12
            goto L_0x0086
        L_0x00d9:
            r13 = 0
            r11 = 9
            if (r1 == r11) goto L_0x00fd
            if (r1 == r8) goto L_0x00e3
        L_0x00e0:
            r8 = 12
            goto L_0x0118
        L_0x00e3:
            r8 = r10[r13]
            if (r8 != r4) goto L_0x00e0
            int r4 = r0.get(r4)
            int r4 = r4 - r3
            if (r4 < r15) goto L_0x00f0
            int r4 = r4 + -15
        L_0x00f0:
            r8 = r4
            r4 = 7
            if (r8 <= r4) goto L_0x00f6
            r9 = 1
            goto L_0x00f7
        L_0x00f6:
            r9 = 0
        L_0x00f7:
            r11 = r9
            r4 = 1
            r9 = r8
            r8 = 12
            goto L_0x011b
        L_0x00fd:
            r4 = 0
            r8 = r10[r4]
            r4 = 11
            if (r8 != r4) goto L_0x00e0
            int r4 = r0.get(r4)
            r8 = 12
            if (r4 < r8) goto L_0x010e
            int r4 = r4 + -12
        L_0x010e:
            r9 = 6
            if (r4 < r9) goto L_0x0113
            r9 = 1
            goto L_0x0114
        L_0x0113:
            r9 = 0
        L_0x0114:
            r11 = r9
            r9 = r4
            r4 = 1
            goto L_0x011b
        L_0x0118:
            r11 = r9
            r4 = 0
            r9 = 0
        L_0x011b:
            if (r4 != 0) goto L_0x013a
            r13 = 0
            r4 = r10[r13]
            int r4 = r0.getActualMinimum(r4)
            r9 = r10[r13]
            int r9 = r0.getActualMaximum(r9)
            r11 = r10[r13]
            int r11 = r0.get(r11)
            int r11 = r11 - r4
            int r9 = r9 - r4
            int r9 = r9 / r14
            if (r11 <= r9) goto L_0x0137
            r4 = 1
            goto L_0x0138
        L_0x0137:
            r4 = 0
        L_0x0138:
            r9 = r4
            goto L_0x0140
        L_0x013a:
            r13 = 0
            r16 = r11
            r11 = r9
            r9 = r16
        L_0x0140:
            if (r11 == 0) goto L_0x014e
            r4 = r10[r13]
            r10 = r10[r13]
            int r10 = r0.get(r10)
            int r10 = r10 - r11
            r0.set(r4, r10)
        L_0x014e:
            int r7 = r7 + 1
            r4 = 12
            goto L_0x0080
        L_0x0154:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "The field "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r1 = " is not supported"
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.modify(java.util.Calendar, int, org.apache.commons.lang3.time.DateUtils$ModifyType):void");
    }

    public static Iterator<Calendar> iterator(Date date, int i) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return iterator(instance, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0069, code lost:
        r7 = 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.Iterator<java.util.Calendar> iterator(java.util.Calendar r7, int r8) {
        /*
            if (r7 != 0) goto L_0x000a
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
            java.lang.String r8 = "The date must not be null"
            r7.<init>(r8)
            throw r7
        L_0x000a:
            r0 = -1
            r1 = 2
            r2 = 5
            r3 = 1
            r4 = 7
            switch(r8) {
                case 1: goto L_0x0049;
                case 2: goto L_0x0049;
                case 3: goto L_0x0049;
                case 4: goto L_0x0049;
                case 5: goto L_0x002e;
                case 6: goto L_0x002e;
                default: goto L_0x0012;
            }
        L_0x0012:
            java.lang.IllegalArgumentException r7 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "The range style "
            r0.append(r1)
            r0.append(r8)
            java.lang.String r8 = " is not valid."
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            r7.<init>(r8)
            throw r7
        L_0x002e:
            java.util.Calendar r7 = truncate(r7, r1)
            java.lang.Object r5 = r7.clone()
            java.util.Calendar r5 = (java.util.Calendar) r5
            r5.add(r1, r3)
            r5.add(r2, r0)
            r6 = 6
            if (r8 != r6) goto L_0x0044
            r6 = r5
            r5 = r7
            goto L_0x0069
        L_0x0044:
            r6 = r5
            r1 = 1
            r5 = r7
            r7 = 7
            goto L_0x006d
        L_0x0049:
            java.util.Calendar r5 = truncate(r7, r2)
            java.util.Calendar r6 = truncate(r7, r2)
            switch(r8) {
                case 1: goto L_0x006b;
                case 2: goto L_0x0069;
                case 3: goto L_0x0062;
                case 4: goto L_0x0055;
                default: goto L_0x0054;
            }
        L_0x0054:
            goto L_0x006b
        L_0x0055:
            int r8 = r7.get(r4)
            int r1 = r8 + -3
            int r7 = r7.get(r4)
            int r7 = r7 + 3
            goto L_0x006d
        L_0x0062:
            int r1 = r7.get(r4)
            int r7 = r1 + -1
            goto L_0x006d
        L_0x0069:
            r7 = 1
            goto L_0x006d
        L_0x006b:
            r7 = 7
            r1 = 1
        L_0x006d:
            if (r1 >= r3) goto L_0x0071
            int r1 = r1 + 7
        L_0x0071:
            if (r1 <= r4) goto L_0x0075
            int r1 = r1 + -7
        L_0x0075:
            if (r7 >= r3) goto L_0x0079
            int r7 = r7 + 7
        L_0x0079:
            if (r7 <= r4) goto L_0x007d
            int r7 = r7 + -7
        L_0x007d:
            int r8 = r5.get(r4)
            if (r8 == r1) goto L_0x0087
            r5.add(r2, r0)
            goto L_0x007d
        L_0x0087:
            int r8 = r6.get(r4)
            if (r8 == r7) goto L_0x0091
            r6.add(r2, r3)
            goto L_0x0087
        L_0x0091:
            org.apache.commons.lang3.time.DateUtils$DateIterator r7 = new org.apache.commons.lang3.time.DateUtils$DateIterator
            r7.<init>(r5, r6)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.iterator(java.util.Calendar, int):java.util.Iterator");
    }

    public static Iterator<?> iterator(Object obj, int i) {
        if (obj == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else if (obj instanceof Date) {
            return iterator((Date) obj, i);
        } else {
            if (obj instanceof Calendar) {
                return iterator((Calendar) obj, i);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Could not iterate based on ");
            sb.append(obj);
            throw new ClassCastException(sb.toString());
        }
    }

    public static long getFragmentInMilliseconds(Date date, int i) {
        return getFragment(date, i, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Date date, int i) {
        return getFragment(date, i, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Date date, int i) {
        return getFragment(date, i, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Date date, int i) {
        return getFragment(date, i, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Date date, int i) {
        return getFragment(date, i, TimeUnit.DAYS);
    }

    public static long getFragmentInMilliseconds(Calendar calendar, int i) {
        return getFragment(calendar, i, TimeUnit.MILLISECONDS);
    }

    public static long getFragmentInSeconds(Calendar calendar, int i) {
        return getFragment(calendar, i, TimeUnit.SECONDS);
    }

    public static long getFragmentInMinutes(Calendar calendar, int i) {
        return getFragment(calendar, i, TimeUnit.MINUTES);
    }

    public static long getFragmentInHours(Calendar calendar, int i) {
        return getFragment(calendar, i, TimeUnit.HOURS);
    }

    public static long getFragmentInDays(Calendar calendar, int i) {
        return getFragment(calendar, i, TimeUnit.DAYS);
    }

    private static long getFragment(Date date, int i, TimeUnit timeUnit) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return getFragment(instance, i, timeUnit);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0069, code lost:
        r4 = r2 + r8.convert((long) r6.get(12), java.util.concurrent.TimeUnit.MINUTES);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return r2 + r8.convert((long) r6.get(14), java.util.concurrent.TimeUnit.MILLISECONDS);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static long getFragment(java.util.Calendar r6, int r7, java.util.concurrent.TimeUnit r8) {
        /*
            if (r6 != 0) goto L_0x000a
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.String r7 = "The date must not be null"
            r6.<init>(r7)
            throw r6
        L_0x000a:
            r0 = 0
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.DAYS
            if (r8 != r2) goto L_0x0012
            r2 = 0
            goto L_0x0013
        L_0x0012:
            r2 = 1
        L_0x0013:
            switch(r7) {
                case 1: goto L_0x0028;
                case 2: goto L_0x0018;
                default: goto L_0x0016;
            }
        L_0x0016:
            r4 = r0
            goto L_0x0037
        L_0x0018:
            r3 = 5
            int r3 = r6.get(r3)
            int r3 = r3 - r2
            long r2 = (long) r3
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.DAYS
            long r2 = r8.convert(r2, r4)
            long r4 = r2 + r0
            goto L_0x0037
        L_0x0028:
            r3 = 6
            int r3 = r6.get(r3)
            int r3 = r3 - r2
            long r2 = (long) r3
            java.util.concurrent.TimeUnit r4 = java.util.concurrent.TimeUnit.DAYS
            long r2 = r8.convert(r2, r4)
            long r4 = r2 + r0
        L_0x0037:
            switch(r7) {
                case 1: goto L_0x005a;
                case 2: goto L_0x005a;
                case 3: goto L_0x003a;
                case 4: goto L_0x003a;
                case 5: goto L_0x005a;
                case 6: goto L_0x005a;
                case 7: goto L_0x003a;
                case 8: goto L_0x003a;
                case 9: goto L_0x003a;
                case 10: goto L_0x003a;
                case 11: goto L_0x0058;
                case 12: goto L_0x0078;
                case 13: goto L_0x0056;
                case 14: goto L_0x0096;
                default: goto L_0x003a;
            }
        L_0x003a:
            java.lang.IllegalArgumentException r6 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "The fragment "
            r8.append(r0)
            r8.append(r7)
            java.lang.String r7 = " is not supported"
            r8.append(r7)
            java.lang.String r7 = r8.toString()
            r6.<init>(r7)
            throw r6
        L_0x0056:
            r2 = r4
            goto L_0x0087
        L_0x0058:
            r2 = r4
            goto L_0x0069
        L_0x005a:
            r7 = 11
            int r7 = r6.get(r7)
            long r0 = (long) r7
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.HOURS
            long r0 = r8.convert(r0, r7)
            long r2 = r4 + r0
        L_0x0069:
            r7 = 12
            int r7 = r6.get(r7)
            long r0 = (long) r7
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.MINUTES
            long r0 = r8.convert(r0, r7)
            long r4 = r2 + r0
        L_0x0078:
            r7 = 13
            int r7 = r6.get(r7)
            long r0 = (long) r7
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.SECONDS
            long r0 = r8.convert(r0, r7)
            long r2 = r4 + r0
        L_0x0087:
            r7 = 14
            int r6 = r6.get(r7)
            long r6 = (long) r6
            java.util.concurrent.TimeUnit r0 = java.util.concurrent.TimeUnit.MILLISECONDS
            long r6 = r8.convert(r6, r0)
            long r4 = r2 + r6
        L_0x0096:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.DateUtils.getFragment(java.util.Calendar, int, java.util.concurrent.TimeUnit):long");
    }

    public static boolean truncatedEquals(Calendar calendar, Calendar calendar2, int i) {
        return truncatedCompareTo(calendar, calendar2, i) == 0;
    }

    public static boolean truncatedEquals(Date date, Date date2, int i) {
        return truncatedCompareTo(date, date2, i) == 0;
    }

    public static int truncatedCompareTo(Calendar calendar, Calendar calendar2, int i) {
        return truncate(calendar, i).compareTo(truncate(calendar2, i));
    }

    public static int truncatedCompareTo(Date date, Date date2, int i) {
        return truncate(date, i).compareTo(truncate(date2, i));
    }
}
