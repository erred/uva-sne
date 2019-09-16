package com.opengarden.firechat.fragments;

import com.opengarden.firechat.VectorApp;
import java.util.Comparator;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0006"}, mo21251d2 = {"<anonymous>", "", "u1", "", "kotlin.jvm.PlatformType", "u2", "compare"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$refreshIgnoredUsersList$1<T> implements Comparator<String> {
    public static final VectorSettingsPreferencesFragment$refreshIgnoredUsersList$1 INSTANCE = new VectorSettingsPreferencesFragment$refreshIgnoredUsersList$1();

    VectorSettingsPreferencesFragment$refreshIgnoredUsersList$1() {
    }

    public final int compare(String str, String str2) {
        Intrinsics.checkExpressionValueIsNotNull(str, "u1");
        Locale applicationLocale = VectorApp.getApplicationLocale();
        Intrinsics.checkExpressionValueIsNotNull(applicationLocale, "VectorApp.getApplicationLocale()");
        if (str == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        String lowerCase = str.toLowerCase(applicationLocale);
        Intrinsics.checkExpressionValueIsNotNull(lowerCase, "(this as java.lang.String).toLowerCase(locale)");
        Intrinsics.checkExpressionValueIsNotNull(str2, "u2");
        Locale applicationLocale2 = VectorApp.getApplicationLocale();
        Intrinsics.checkExpressionValueIsNotNull(applicationLocale2, "VectorApp.getApplicationLocale()");
        if (str2 == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        String lowerCase2 = str2.toLowerCase(applicationLocale2);
        Intrinsics.checkExpressionValueIsNotNull(lowerCase2, "(this as java.lang.String).toLowerCase(locale)");
        return lowerCase.compareTo(lowerCase2);
    }
}
