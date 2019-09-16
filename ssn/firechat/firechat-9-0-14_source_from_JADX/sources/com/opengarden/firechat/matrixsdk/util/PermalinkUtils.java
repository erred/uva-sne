package com.opengarden.firechat.matrixsdk.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class PermalinkUtils {
    private static final String MATRIX_TO_URL_BASE = "https://matrix.to/#/";

    @Nullable
    public static String createPermalink(Event event) {
        if (event == null) {
            return null;
        }
        return createPermalink(event.roomId, event.eventId);
    }

    @Nullable
    public static String createPermalink(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(MATRIX_TO_URL_BASE);
        sb.append(escape(str));
        return sb.toString();
    }

    @Nullable
    public static String createPermalink(@NonNull String str, @NonNull String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(MATRIX_TO_URL_BASE);
        sb.append(escape(str));
        sb.append("/");
        sb.append(escape(str2));
        return sb.toString();
    }

    private static String escape(String str) {
        return str.replaceAll("/", "%2F");
    }
}
