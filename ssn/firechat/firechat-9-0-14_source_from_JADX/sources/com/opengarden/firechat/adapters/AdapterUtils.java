package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.util.PreferencesManager;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.StringUtils;

public class AdapterUtils {
    public static final long MS_IN_DAY = 86400000;

    public static Date zeroTimeDate(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(11, 0);
        gregorianCalendar.set(12, 0);
        gregorianCalendar.set(13, 0);
        gregorianCalendar.set(14, 0);
        return gregorianCalendar.getTime();
    }

    private static int getTimeDisplay(Context context) {
        return PreferencesManager.displayTimeIn12hFormat(context) ? 64 : 128;
    }

    public static String tsToString(Context context, long j, boolean z) {
        long time = (new Date().getTime() - zeroTimeDate(new Date(j)).getTime()) / 86400000;
        if (z) {
            return DateUtils.formatDateTime(context, j, getTimeDisplay(context) | 1);
        }
        if (0 == time) {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(C1299R.string.today));
            sb.append(StringUtils.SPACE);
            sb.append(DateUtils.formatDateTime(context, j, getTimeDisplay(context) | 1));
            return sb.toString();
        } else if (1 == time) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(context.getString(C1299R.string.yesterday));
            sb2.append(StringUtils.SPACE);
            sb2.append(DateUtils.formatDateTime(context, j, getTimeDisplay(context) | 1));
            return sb2.toString();
        } else if (7 > time) {
            return DateUtils.formatDateTime(context, j, 524291 | getTimeDisplay(context));
        } else {
            if (365 > time) {
                return DateUtils.formatDateTime(context, j, 524304);
            }
            return DateUtils.formatDateTime(context, j, 524308);
        }
    }
}
