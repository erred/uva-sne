package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.FieldPosition;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class FastDatePrinter implements DatePrinter, Serializable {
    public static final int FULL = 0;
    public static final int LONG = 1;
    private static final int MAX_DIGITS = 10;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
    private static final long serialVersionUID = 1;
    private final Locale mLocale;
    private transient int mMaxLengthEstimate;
    private final String mPattern;
    private transient Rule[] mRules;
    private final TimeZone mTimeZone;

    private static class CharacterLiteral implements Rule {
        private final char mValue;

        public int estimateLength() {
            return 1;
        }

        CharacterLiteral(char c) {
            this.mValue = c;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendable.append(this.mValue);
        }
    }

    private static class DayInWeekField implements NumberRule {
        private final NumberRule mRule;

        DayInWeekField(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            int i = 7;
            int i2 = calendar.get(7);
            NumberRule numberRule = this.mRule;
            if (i2 != 1) {
                i = i2 - 1;
            }
            numberRule.appendTo(appendable, i);
        }

        public void appendTo(Appendable appendable, int i) throws IOException {
            this.mRule.appendTo(appendable, i);
        }
    }

    private static class Iso8601_Rule implements Rule {
        static final Iso8601_Rule ISO8601_HOURS = new Iso8601_Rule(3);
        static final Iso8601_Rule ISO8601_HOURS_COLON_MINUTES = new Iso8601_Rule(6);
        static final Iso8601_Rule ISO8601_HOURS_MINUTES = new Iso8601_Rule(5);
        final int length;

        static Iso8601_Rule getRule(int i) {
            switch (i) {
                case 1:
                    return ISO8601_HOURS;
                case 2:
                    return ISO8601_HOURS_MINUTES;
                case 3:
                    return ISO8601_HOURS_COLON_MINUTES;
                default:
                    throw new IllegalArgumentException("invalid number of X");
            }
        }

        Iso8601_Rule(int i) {
            this.length = i;
        }

        public int estimateLength() {
            return this.length;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            int i = calendar.get(15) + calendar.get(16);
            if (i == 0) {
                appendable.append("Z");
                return;
            }
            if (i < 0) {
                appendable.append('-');
                i = -i;
            } else {
                appendable.append('+');
            }
            int i2 = i / 3600000;
            FastDatePrinter.appendDigits(appendable, i2);
            if (this.length >= 5) {
                if (this.length == 6) {
                    appendable.append(':');
                }
                FastDatePrinter.appendDigits(appendable, (i / 60000) - (i2 * 60));
            }
        }
    }

    private interface NumberRule extends Rule {
        void appendTo(Appendable appendable, int i) throws IOException;
    }

    private static class PaddedNumberField implements NumberRule {
        private final int mField;
        private final int mSize;

        PaddedNumberField(int i, int i2) {
            if (i2 < 3) {
                throw new IllegalArgumentException();
            }
            this.mField = i;
            this.mSize = i2;
        }

        public int estimateLength() {
            return this.mSize;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(this.mField));
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            FastDatePrinter.appendFullDigits(appendable, i, this.mSize);
        }
    }

    private interface Rule {
        void appendTo(Appendable appendable, Calendar calendar) throws IOException;

        int estimateLength();
    }

    private static class StringLiteral implements Rule {
        private final String mValue;

        StringLiteral(String str) {
            this.mValue = str;
        }

        public int estimateLength() {
            return this.mValue.length();
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendable.append(this.mValue);
        }
    }

    private static class TextField implements Rule {
        private final int mField;
        private final String[] mValues;

        TextField(int i, String[] strArr) {
            this.mField = i;
            this.mValues = strArr;
        }

        public int estimateLength() {
            int length = this.mValues.length;
            int i = 0;
            while (true) {
                length--;
                if (length < 0) {
                    return i;
                }
                int length2 = this.mValues[length].length();
                if (length2 > i) {
                    i = length2;
                }
            }
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendable.append(this.mValues[calendar.get(this.mField)]);
        }
    }

    private static class TimeZoneDisplayKey {
        private final Locale mLocale;
        private final int mStyle;
        private final TimeZone mTimeZone;

        TimeZoneDisplayKey(TimeZone timeZone, boolean z, int i, Locale locale) {
            this.mTimeZone = timeZone;
            if (z) {
                this.mStyle = Integer.MIN_VALUE | i;
            } else {
                this.mStyle = i;
            }
            this.mLocale = locale;
        }

        public int hashCode() {
            return (((this.mStyle * 31) + this.mLocale.hashCode()) * 31) + this.mTimeZone.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TimeZoneDisplayKey)) {
                return false;
            }
            TimeZoneDisplayKey timeZoneDisplayKey = (TimeZoneDisplayKey) obj;
            if (!this.mTimeZone.equals(timeZoneDisplayKey.mTimeZone) || this.mStyle != timeZoneDisplayKey.mStyle || !this.mLocale.equals(timeZoneDisplayKey.mLocale)) {
                z = false;
            }
            return z;
        }
    }

    private static class TimeZoneNameRule implements Rule {
        private final String mDaylight;
        private final Locale mLocale;
        private final String mStandard;
        private final int mStyle;

        TimeZoneNameRule(TimeZone timeZone, Locale locale, int i) {
            this.mLocale = locale;
            this.mStyle = i;
            this.mStandard = FastDatePrinter.getTimeZoneDisplay(timeZone, false, i, locale);
            this.mDaylight = FastDatePrinter.getTimeZoneDisplay(timeZone, true, i, locale);
        }

        public int estimateLength() {
            return Math.max(this.mStandard.length(), this.mDaylight.length());
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            TimeZone timeZone = calendar.getTimeZone();
            if (calendar.get(16) != 0) {
                appendable.append(FastDatePrinter.getTimeZoneDisplay(timeZone, true, this.mStyle, this.mLocale));
            } else {
                appendable.append(FastDatePrinter.getTimeZoneDisplay(timeZone, false, this.mStyle, this.mLocale));
            }
        }
    }

    private static class TimeZoneNumberRule implements Rule {
        static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
        static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
        final boolean mColon;

        public int estimateLength() {
            return 5;
        }

        TimeZoneNumberRule(boolean z) {
            this.mColon = z;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            int i = calendar.get(15) + calendar.get(16);
            if (i < 0) {
                appendable.append('-');
                i = -i;
            } else {
                appendable.append('+');
            }
            int i2 = i / 3600000;
            FastDatePrinter.appendDigits(appendable, i2);
            if (this.mColon) {
                appendable.append(':');
            }
            FastDatePrinter.appendDigits(appendable, (i / 60000) - (i2 * 60));
        }
    }

    private static class TwelveHourField implements NumberRule {
        private final NumberRule mRule;

        TwelveHourField(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            int i = calendar.get(10);
            if (i == 0) {
                i = calendar.getLeastMaximum(10) + 1;
            }
            this.mRule.appendTo(appendable, i);
        }

        public void appendTo(Appendable appendable, int i) throws IOException {
            this.mRule.appendTo(appendable, i);
        }
    }

    private static class TwentyFourHourField implements NumberRule {
        private final NumberRule mRule;

        TwentyFourHourField(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            int i = calendar.get(11);
            if (i == 0) {
                i = calendar.getMaximum(11) + 1;
            }
            this.mRule.appendTo(appendable, i);
        }

        public void appendTo(Appendable appendable, int i) throws IOException {
            this.mRule.appendTo(appendable, i);
        }
    }

    private static class TwoDigitMonthField implements NumberRule {
        static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();

        public int estimateLength() {
            return 2;
        }

        TwoDigitMonthField() {
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            FastDatePrinter.appendDigits(appendable, i);
        }
    }

    private static class TwoDigitNumberField implements NumberRule {
        private final int mField;

        public int estimateLength() {
            return 2;
        }

        TwoDigitNumberField(int i) {
            this.mField = i;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(this.mField));
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            if (i < 100) {
                FastDatePrinter.appendDigits(appendable, i);
            } else {
                FastDatePrinter.appendFullDigits(appendable, i, 2);
            }
        }
    }

    private static class TwoDigitYearField implements NumberRule {
        static final TwoDigitYearField INSTANCE = new TwoDigitYearField();

        public int estimateLength() {
            return 2;
        }

        TwoDigitYearField() {
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(1) % 100);
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            FastDatePrinter.appendDigits(appendable, i);
        }
    }

    private static class UnpaddedMonthField implements NumberRule {
        static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();

        public int estimateLength() {
            return 2;
        }

        UnpaddedMonthField() {
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(2) + 1);
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            if (i < 10) {
                appendable.append((char) (i + 48));
            } else {
                FastDatePrinter.appendDigits(appendable, i);
            }
        }
    }

    private static class UnpaddedNumberField implements NumberRule {
        private final int mField;

        public int estimateLength() {
            return 4;
        }

        UnpaddedNumberField(int i) {
            this.mField = i;
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            appendTo(appendable, calendar.get(this.mField));
        }

        public final void appendTo(Appendable appendable, int i) throws IOException {
            if (i < 10) {
                appendable.append((char) (i + 48));
            } else if (i < 100) {
                FastDatePrinter.appendDigits(appendable, i);
            } else {
                FastDatePrinter.appendFullDigits(appendable, i, 1);
            }
        }
    }

    private static class WeekYear implements NumberRule {
        private final NumberRule mRule;

        WeekYear(NumberRule numberRule) {
            this.mRule = numberRule;
        }

        public int estimateLength() {
            return this.mRule.estimateLength();
        }

        public void appendTo(Appendable appendable, Calendar calendar) throws IOException {
            this.mRule.appendTo(appendable, CalendarReflection.getWeekYear(calendar));
        }

        public void appendTo(Appendable appendable, int i) throws IOException {
            this.mRule.appendTo(appendable, i);
        }
    }

    protected FastDatePrinter(String str, TimeZone timeZone, Locale locale) {
        this.mPattern = str;
        this.mTimeZone = timeZone;
        this.mLocale = locale;
        init();
    }

    private void init() {
        List parsePattern = parsePattern();
        this.mRules = (Rule[]) parsePattern.toArray(new Rule[parsePattern.size()]);
        int length = this.mRules.length;
        int i = 0;
        while (true) {
            length--;
            if (length >= 0) {
                i += this.mRules[length].estimateLength();
            } else {
                this.mMaxLengthEstimate = i;
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<org.apache.commons.lang3.time.FastDatePrinter.Rule> parsePattern() {
        /*
            r17 = this;
            r0 = r17
            java.text.DateFormatSymbols r1 = new java.text.DateFormatSymbols
            java.util.Locale r2 = r0.mLocale
            r1.<init>(r2)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String[] r3 = r1.getEras()
            java.lang.String[] r4 = r1.getMonths()
            java.lang.String[] r5 = r1.getShortMonths()
            java.lang.String[] r6 = r1.getWeekdays()
            java.lang.String[] r7 = r1.getShortWeekdays()
            java.lang.String[] r1 = r1.getAmPmStrings()
            java.lang.String r8 = r0.mPattern
            int r8 = r8.length()
            r9 = 1
            int[] r10 = new int[r9]
            r11 = 0
            r12 = 0
        L_0x0031:
            if (r12 >= r8) goto L_0x018f
            r10[r11] = r12
            java.lang.String r12 = r0.mPattern
            java.lang.String r12 = r0.parseToken(r12, r10)
            r13 = r10[r11]
            int r14 = r12.length()
            if (r14 != 0) goto L_0x0045
            goto L_0x018f
        L_0x0045:
            char r15 = r12.charAt(r11)
            r9 = 7
            switch(r15) {
                case 68: goto L_0x017f;
                case 69: goto L_0x0171;
                case 70: goto L_0x0169;
                case 71: goto L_0x0162;
                case 72: goto L_0x015a;
                default: goto L_0x004d;
            }
        L_0x004d:
            switch(r15) {
                case 87: goto L_0x0154;
                case 88: goto L_0x014f;
                case 89: goto L_0x0120;
                case 90: goto L_0x0122;
                default: goto L_0x0050;
            }
        L_0x0050:
            switch(r15) {
                case 121: goto L_0x0120;
                case 122: goto L_0x0106;
                default: goto L_0x0053;
            }
        L_0x0053:
            r11 = 3
            switch(r15) {
                case 39: goto L_0x00e9;
                case 75: goto L_0x00e1;
                case 77: goto L_0x00c2;
                case 83: goto L_0x00ba;
                case 97: goto L_0x00b1;
                case 100: goto L_0x00aa;
                case 104: goto L_0x009d;
                case 107: goto L_0x0090;
                case 109: goto L_0x0088;
                case 115: goto L_0x0080;
                case 117: goto L_0x0074;
                case 119: goto L_0x006e;
                default: goto L_0x0057;
            }
        L_0x0057:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Illegal pattern component: "
            r2.append(r3)
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r1
        L_0x006e:
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r11, r14)
            goto L_0x0160
        L_0x0074:
            org.apache.commons.lang3.time.FastDatePrinter$DayInWeekField r11 = new org.apache.commons.lang3.time.FastDatePrinter$DayInWeekField
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            r11.<init>(r9)
        L_0x007d:
            r9 = r11
            goto L_0x0160
        L_0x0080:
            r9 = 13
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0160
        L_0x0088:
            r9 = 12
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0160
        L_0x0090:
            org.apache.commons.lang3.time.FastDatePrinter$TwentyFourHourField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TwentyFourHourField
            r11 = 11
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r11 = r0.selectNumberRule(r11, r14)
            r9.<init>(r11)
            goto L_0x0160
        L_0x009d:
            org.apache.commons.lang3.time.FastDatePrinter$TwelveHourField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TwelveHourField
            r11 = 10
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r11 = r0.selectNumberRule(r11, r14)
            r9.<init>(r11)
            goto L_0x0160
        L_0x00aa:
            r9 = 5
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0160
        L_0x00b1:
            org.apache.commons.lang3.time.FastDatePrinter$TextField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TextField
            r11 = 9
            r9.<init>(r11, r1)
            goto L_0x0160
        L_0x00ba:
            r9 = 14
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0160
        L_0x00c2:
            r9 = 4
            if (r14 < r9) goto L_0x00cd
            org.apache.commons.lang3.time.FastDatePrinter$TextField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TextField
            r12 = 2
            r9.<init>(r12, r4)
            goto L_0x0160
        L_0x00cd:
            r12 = 2
            if (r14 != r11) goto L_0x00d7
            org.apache.commons.lang3.time.FastDatePrinter$TextField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TextField
            r9.<init>(r12, r5)
            goto L_0x0160
        L_0x00d7:
            if (r14 != r12) goto L_0x00dd
            org.apache.commons.lang3.time.FastDatePrinter$TwoDigitMonthField r9 = org.apache.commons.lang3.time.FastDatePrinter.TwoDigitMonthField.INSTANCE
            goto L_0x0160
        L_0x00dd:
            org.apache.commons.lang3.time.FastDatePrinter$UnpaddedMonthField r9 = org.apache.commons.lang3.time.FastDatePrinter.UnpaddedMonthField.INSTANCE
            goto L_0x0160
        L_0x00e1:
            r9 = 10
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0160
        L_0x00e9:
            r9 = 1
            java.lang.String r11 = r12.substring(r9)
            int r12 = r11.length()
            if (r12 != r9) goto L_0x0100
            org.apache.commons.lang3.time.FastDatePrinter$CharacterLiteral r9 = new org.apache.commons.lang3.time.FastDatePrinter$CharacterLiteral
            r12 = 0
            char r11 = r11.charAt(r12)
            r9.<init>(r11)
            goto L_0x0160
        L_0x0100:
            org.apache.commons.lang3.time.FastDatePrinter$StringLiteral r9 = new org.apache.commons.lang3.time.FastDatePrinter$StringLiteral
            r9.<init>(r11)
            goto L_0x0160
        L_0x0106:
            r9 = 4
            if (r14 < r9) goto L_0x0114
            org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNameRule r9 = new org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNameRule
            java.util.TimeZone r11 = r0.mTimeZone
            java.util.Locale r12 = r0.mLocale
            r14 = 1
            r9.<init>(r11, r12, r14)
            goto L_0x0160
        L_0x0114:
            r14 = 1
            org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNameRule r9 = new org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNameRule
            java.util.TimeZone r11 = r0.mTimeZone
            java.util.Locale r12 = r0.mLocale
            r15 = 0
            r9.<init>(r11, r12, r15)
            goto L_0x0160
        L_0x0120:
            r9 = 2
            goto L_0x0131
        L_0x0122:
            r9 = 1
            if (r14 != r9) goto L_0x0128
            org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNumberRule r9 = org.apache.commons.lang3.time.FastDatePrinter.TimeZoneNumberRule.INSTANCE_NO_COLON
            goto L_0x0160
        L_0x0128:
            r9 = 2
            if (r14 != r9) goto L_0x012e
            org.apache.commons.lang3.time.FastDatePrinter$Iso8601_Rule r9 = org.apache.commons.lang3.time.FastDatePrinter.Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES
            goto L_0x0160
        L_0x012e:
            org.apache.commons.lang3.time.FastDatePrinter$TimeZoneNumberRule r9 = org.apache.commons.lang3.time.FastDatePrinter.TimeZoneNumberRule.INSTANCE_COLON
            goto L_0x0160
        L_0x0131:
            if (r14 != r9) goto L_0x0136
            org.apache.commons.lang3.time.FastDatePrinter$TwoDigitYearField r9 = org.apache.commons.lang3.time.FastDatePrinter.TwoDigitYearField.INSTANCE
            goto L_0x0142
        L_0x0136:
            r9 = 4
            if (r14 >= r9) goto L_0x013c
            r9 = 1
            r14 = 4
            goto L_0x013d
        L_0x013c:
            r9 = 1
        L_0x013d:
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r11 = r0.selectNumberRule(r9, r14)
            r9 = r11
        L_0x0142:
            r11 = 89
            if (r15 != r11) goto L_0x0160
            org.apache.commons.lang3.time.FastDatePrinter$WeekYear r11 = new org.apache.commons.lang3.time.FastDatePrinter$WeekYear
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = (org.apache.commons.lang3.time.FastDatePrinter.NumberRule) r9
            r11.<init>(r9)
            goto L_0x007d
        L_0x014f:
            org.apache.commons.lang3.time.FastDatePrinter$Iso8601_Rule r9 = org.apache.commons.lang3.time.FastDatePrinter.Iso8601_Rule.getRule(r14)
            goto L_0x0160
        L_0x0154:
            r11 = 4
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r11, r14)
            goto L_0x0160
        L_0x015a:
            r9 = 11
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
        L_0x0160:
            r12 = 0
            goto L_0x0185
        L_0x0162:
            org.apache.commons.lang3.time.FastDatePrinter$TextField r9 = new org.apache.commons.lang3.time.FastDatePrinter$TextField
            r12 = 0
            r9.<init>(r12, r3)
            goto L_0x0185
        L_0x0169:
            r12 = 0
            r9 = 8
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
            goto L_0x0185
        L_0x0171:
            r11 = 4
            r12 = 0
            org.apache.commons.lang3.time.FastDatePrinter$TextField r15 = new org.apache.commons.lang3.time.FastDatePrinter$TextField
            if (r14 >= r11) goto L_0x0179
            r11 = r7
            goto L_0x017a
        L_0x0179:
            r11 = r6
        L_0x017a:
            r15.<init>(r9, r11)
            r9 = r15
            goto L_0x0185
        L_0x017f:
            r12 = 0
            r9 = 6
            org.apache.commons.lang3.time.FastDatePrinter$NumberRule r9 = r0.selectNumberRule(r9, r14)
        L_0x0185:
            r2.add(r9)
            r9 = 1
            int r11 = r13 + 1
            r12 = r11
            r11 = 0
            goto L_0x0031
        L_0x018f:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDatePrinter.parsePattern():java.util.List");
    }

    /* access modifiers changed from: protected */
    public String parseToken(String str, int[] iArr) {
        StringBuilder sb = new StringBuilder();
        int i = iArr[0];
        int length = str.length();
        char charAt = str.charAt(i);
        if ((charAt >= 'A' && charAt <= 'Z') || (charAt >= 'a' && charAt <= 'z')) {
            sb.append(charAt);
            while (true) {
                int i2 = i + 1;
                if (i2 >= length || str.charAt(i2) != charAt) {
                    break;
                }
                sb.append(charAt);
                i = i2;
            }
        } else {
            sb.append('\'');
            boolean z = false;
            while (true) {
                if (i >= length) {
                    break;
                }
                char charAt2 = str.charAt(i);
                if (charAt2 == '\'') {
                    int i3 = i + 1;
                    if (i3 >= length || str.charAt(i3) != '\'') {
                        z = !z;
                    } else {
                        sb.append(charAt2);
                        i = i3;
                    }
                } else if (z || ((charAt2 < 'A' || charAt2 > 'Z') && (charAt2 < 'a' || charAt2 > 'z'))) {
                    sb.append(charAt2);
                }
                i++;
            }
            i--;
        }
        iArr[0] = i;
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public NumberRule selectNumberRule(int i, int i2) {
        switch (i2) {
            case 1:
                return new UnpaddedNumberField(i);
            case 2:
                return new TwoDigitNumberField(i);
            default:
                return new PaddedNumberField(i, i2);
        }
    }

    @Deprecated
    public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        String str;
        if (obj instanceof Date) {
            return format((Date) obj, stringBuffer);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj, stringBuffer);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue(), stringBuffer);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        if (obj == null) {
            str = "<null>";
        } else {
            str = obj.getClass().getName();
        }
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public String format(Object obj) {
        String str;
        if (obj instanceof Date) {
            return format((Date) obj);
        }
        if (obj instanceof Calendar) {
            return format((Calendar) obj);
        }
        if (obj instanceof Long) {
            return format(((Long) obj).longValue());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown class: ");
        if (obj == null) {
            str = "<null>";
        } else {
            str = obj.getClass().getName();
        }
        sb.append(str);
        throw new IllegalArgumentException(sb.toString());
    }

    public String format(long j) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTimeInMillis(j);
        return applyRulesToString(newCalendar);
    }

    private String applyRulesToString(Calendar calendar) {
        return ((StringBuilder) applyRules(calendar, (B) new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    private Calendar newCalendar() {
        return Calendar.getInstance(this.mTimeZone, this.mLocale);
    }

    public String format(Date date) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTime(date);
        return applyRulesToString(newCalendar);
    }

    public String format(Calendar calendar) {
        return ((StringBuilder) format(calendar, (B) new StringBuilder(this.mMaxLengthEstimate))).toString();
    }

    public StringBuffer format(long j, StringBuffer stringBuffer) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTimeInMillis(j);
        return (StringBuffer) applyRules(newCalendar, (B) stringBuffer);
    }

    public StringBuffer format(Date date, StringBuffer stringBuffer) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTime(date);
        return (StringBuffer) applyRules(newCalendar, (B) stringBuffer);
    }

    public StringBuffer format(Calendar calendar, StringBuffer stringBuffer) {
        return format(calendar.getTime(), stringBuffer);
    }

    public <B extends Appendable> B format(long j, B b) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTimeInMillis(j);
        return applyRules(newCalendar, b);
    }

    public <B extends Appendable> B format(Date date, B b) {
        Calendar newCalendar = newCalendar();
        newCalendar.setTime(date);
        return applyRules(newCalendar, b);
    }

    public <B extends Appendable> B format(Calendar calendar, B b) {
        if (!calendar.getTimeZone().equals(this.mTimeZone)) {
            calendar = (Calendar) calendar.clone();
            calendar.setTimeZone(this.mTimeZone);
        }
        return applyRules(calendar, b);
    }

    /* access modifiers changed from: protected */
    @Deprecated
    public StringBuffer applyRules(Calendar calendar, StringBuffer stringBuffer) {
        return (StringBuffer) applyRules(calendar, (B) stringBuffer);
    }

    private <B extends Appendable> B applyRules(Calendar calendar, B b) {
        try {
            for (Rule appendTo : this.mRules) {
                appendTo.appendTo(b, calendar);
            }
        } catch (IOException e) {
            ExceptionUtils.rethrow(e);
        }
        return b;
    }

    public String getPattern() {
        return this.mPattern;
    }

    public TimeZone getTimeZone() {
        return this.mTimeZone;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getMaxLengthEstimate() {
        return this.mMaxLengthEstimate;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof FastDatePrinter)) {
            return false;
        }
        FastDatePrinter fastDatePrinter = (FastDatePrinter) obj;
        if (this.mPattern.equals(fastDatePrinter.mPattern) && this.mTimeZone.equals(fastDatePrinter.mTimeZone) && this.mLocale.equals(fastDatePrinter.mLocale)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return this.mPattern.hashCode() + ((this.mTimeZone.hashCode() + (this.mLocale.hashCode() * 13)) * 13);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FastDatePrinter[");
        sb.append(this.mPattern);
        sb.append(",");
        sb.append(this.mLocale);
        sb.append(",");
        sb.append(this.mTimeZone.getID());
        sb.append("]");
        return sb.toString();
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        init();
    }

    /* access modifiers changed from: private */
    public static void appendDigits(Appendable appendable, int i) throws IOException {
        appendable.append((char) ((i / 10) + 48));
        appendable.append((char) ((i % 10) + 48));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002c, code lost:
        if (r6 < 100) goto L_0x0038;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        r5.append((char) ((r6 / 100) + 48));
        r6 = r6 % 100;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0038, code lost:
        r5.append('0');
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003b, code lost:
        if (r6 < 10) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
        r5.append((char) ((r6 / 10) + 48));
        r6 = r6 % 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        r5.append('0');
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004a, code lost:
        r5.append((char) (r6 + 48));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void appendFullDigits(java.lang.Appendable r5, int r6, int r7) throws java.io.IOException {
        /*
            r0 = 10
            r1 = 48
            r2 = 10000(0x2710, float:1.4013E-41)
            if (r6 >= r2) goto L_0x0050
            r2 = 4
            r3 = 1000(0x3e8, float:1.401E-42)
            r4 = 100
            if (r6 >= r3) goto L_0x0016
            r2 = 3
            if (r6 >= r4) goto L_0x0016
            r2 = 2
            if (r6 >= r0) goto L_0x0016
            r2 = 1
        L_0x0016:
            int r7 = r7 - r2
        L_0x0017:
            if (r7 <= 0) goto L_0x001f
            r5.append(r1)
            int r7 = r7 + -1
            goto L_0x0017
        L_0x001f:
            switch(r2) {
                case 1: goto L_0x004a;
                case 2: goto L_0x003b;
                case 3: goto L_0x002c;
                case 4: goto L_0x0023;
                default: goto L_0x0022;
            }
        L_0x0022:
            goto L_0x0073
        L_0x0023:
            int r7 = r6 / 1000
            int r7 = r7 + r1
            char r7 = (char) r7
            r5.append(r7)
            int r6 = r6 % 1000
        L_0x002c:
            if (r6 < r4) goto L_0x0038
            int r7 = r6 / 100
            int r7 = r7 + r1
            char r7 = (char) r7
            r5.append(r7)
            int r6 = r6 % 100
            goto L_0x003b
        L_0x0038:
            r5.append(r1)
        L_0x003b:
            if (r6 < r0) goto L_0x0047
            int r7 = r6 / 10
            int r7 = r7 + r1
            char r7 = (char) r7
            r5.append(r7)
            int r6 = r6 % 10
            goto L_0x004a
        L_0x0047:
            r5.append(r1)
        L_0x004a:
            int r6 = r6 + r1
            char r6 = (char) r6
            r5.append(r6)
            goto L_0x0073
        L_0x0050:
            char[] r0 = new char[r0]
            r2 = 0
        L_0x0053:
            if (r6 == 0) goto L_0x0061
            int r3 = r2 + 1
            int r4 = r6 % 10
            int r4 = r4 + r1
            char r4 = (char) r4
            r0[r2] = r4
            int r6 = r6 / 10
            r2 = r3
            goto L_0x0053
        L_0x0061:
            if (r2 >= r7) goto L_0x0069
            r5.append(r1)
            int r7 = r7 + -1
            goto L_0x0061
        L_0x0069:
            int r2 = r2 + -1
            if (r2 < 0) goto L_0x0073
            char r6 = r0[r2]
            r5.append(r6)
            goto L_0x0069
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.time.FastDatePrinter.appendFullDigits(java.lang.Appendable, int, int):void");
    }

    static String getTimeZoneDisplay(TimeZone timeZone, boolean z, int i, Locale locale) {
        TimeZoneDisplayKey timeZoneDisplayKey = new TimeZoneDisplayKey(timeZone, z, i, locale);
        String str = (String) cTimeZoneDisplayCache.get(timeZoneDisplayKey);
        if (str != null) {
            return str;
        }
        String displayName = timeZone.getDisplayName(z, i, locale);
        String str2 = (String) cTimeZoneDisplayCache.putIfAbsent(timeZoneDisplayKey, displayName);
        return str2 != null ? str2 : displayName;
    }
}
