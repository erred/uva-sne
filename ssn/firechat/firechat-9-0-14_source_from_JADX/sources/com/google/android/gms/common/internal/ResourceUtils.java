package com.google.android.gms.common.internal;

import android.net.Uri;
import android.net.Uri.Builder;
import com.facebook.common.util.UriUtil;

public final class ResourceUtils {
    private static final Uri zzuw = new Builder().scheme(UriUtil.QUALIFIED_RESOURCE_SCHEME).authority("com.google.android.gms").appendPath("drawable").build();

    @Deprecated
    public interface SignInResources {
        public static final String BUTTON_DARK_TEXT_DEFAULT = "common_google_signin_btn_text_dark_normal";
    }

    private ResourceUtils() {
    }

    public static Uri getDrawableUri(String str) {
        Preconditions.checkNotNull(str, "Resource name must not be null.");
        return zzuw.buildUpon().appendPath(str).build();
    }
}
