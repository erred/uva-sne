package com.opengarden.firechat.view;

import android.preference.PreferenceManager;
import com.opengarden.firechat.VectorApp;
import java.util.HashSet;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u0010\u0000\u001a\u0010\u0012\f\u0012\n \u0003*\u0004\u0018\u00010\u00020\u00020\u0001H\n¢\u0006\u0002\b\u0004"}, mo21251d2 = {"<anonymous>", "Ljava/util/HashSet;", "", "kotlin.jvm.PlatformType", "invoke"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: UrlPreviewView.kt */
final class UrlPreviewView$Companion$sDismissedUrlsPreviews$2 extends Lambda implements Function0<HashSet<String>> {
    public static final UrlPreviewView$Companion$sDismissedUrlsPreviews$2 INSTANCE = new UrlPreviewView$Companion$sDismissedUrlsPreviews$2();

    UrlPreviewView$Companion$sDismissedUrlsPreviews$2() {
        super(0);
    }

    @NotNull
    public final HashSet<String> invoke() {
        return new HashSet<>(PreferenceManager.getDefaultSharedPreferences(VectorApp.getInstance()).getStringSet("DISMISSED_URL_PREVIEWS_PREF_KEY", new HashSet()));
    }
}
