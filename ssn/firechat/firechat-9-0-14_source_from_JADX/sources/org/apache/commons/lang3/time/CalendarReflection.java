package org.apache.commons.lang3.time;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.exception.ExceptionUtils;

class CalendarReflection {
    private static final Method GET_WEEK_YEAR = getCalendarMethod("getWeekYear", new Class[0]);
    private static final Method IS_WEEK_DATE_SUPPORTED = getCalendarMethod("isWeekDateSupported", new Class[0]);

    CalendarReflection() {
    }

    private static Method getCalendarMethod(String str, Class<?>... clsArr) {
        try {
            return Calendar.class.getMethod(str, clsArr);
        } catch (Exception unused) {
            return null;
        }
    }

    static boolean isWeekDateSupported(Calendar calendar) {
        try {
            boolean z = false;
            if (IS_WEEK_DATE_SUPPORTED != null && ((Boolean) IS_WEEK_DATE_SUPPORTED.invoke(calendar, new Object[0])).booleanValue()) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            return ((Boolean) ExceptionUtils.rethrow(e)).booleanValue();
        }
    }

    public static int getWeekYear(Calendar calendar) {
        try {
            if (isWeekDateSupported(calendar)) {
                return ((Integer) GET_WEEK_YEAR.invoke(calendar, new Object[0])).intValue();
            }
            int i = calendar.get(1);
            if (IS_WEEK_DATE_SUPPORTED == null && (calendar instanceof GregorianCalendar)) {
                int i2 = calendar.get(2);
                if (i2 != 0) {
                    if (i2 == 11 && calendar.get(3) == 1) {
                        i++;
                    }
                } else if (calendar.get(3) >= 52) {
                    i--;
                }
            }
            return i;
        } catch (Exception e) {
            return ((Integer) ExceptionUtils.rethrow(e)).intValue();
        }
    }
}
