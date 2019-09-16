package com.google.android.gms.common.data;

import com.google.android.gms.common.data.TextFilterable.StringFilter;
import org.apache.commons.lang3.StringUtils;

final class zze implements StringFilter {
    zze() {
    }

    public final boolean matches(String str, String str2) {
        if (!str.startsWith(str2)) {
            String str3 = StringUtils.SPACE;
            String valueOf = String.valueOf(str2);
            if (!str.contains(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3))) {
                return false;
            }
        }
        return true;
    }
}
