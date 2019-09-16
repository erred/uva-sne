package com.opengarden.firechat.matrixsdk.util;

import android.support.annotation.NonNull;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor.Logger;
import org.apache.commons.lang3.StringUtils;

public class FormattedJsonHttpLogger implements Logger {
    private static final int INDENT_SPACE = 2;
    private static final String LOG_TAG = "FormattedJsonHttpLogger";

    public synchronized void log(@NonNull String str) {
    }

    private void logJson(String str) {
        for (String log : str.split(StringUtils.f158LF)) {
            Platform.get().log(4, log, null);
        }
    }
}
