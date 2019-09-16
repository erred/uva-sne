package com.opengarden.firechat.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ImagesContract;
import com.opengarden.firechat.C1299R;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\f\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\r\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\nJ\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0004J\u0016\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0004J\u001e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u0004J\u001e\u0010\u0016\u001a\u00020\u00132\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0017"}, mo21251d2 = {"Lcom/opengarden/firechat/repositories/ServerUrlsRepository;", "", "()V", "DEFAULT_REFERRER_HOME_SERVER_URL_PREF", "", "DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF", "HOME_SERVER_URL_PREF", "IDENTITY_SERVER_URL_PREF", "getDefaultHomeServerUrl", "context", "Landroid/content/Context;", "getDefaultIdentityServerUrl", "getLastHomeServerUrl", "getLastIdentityServerUrl", "isDefaultHomeServerUrl", "", "url", "isDefaultIdentityServerUrl", "saveServerUrls", "", "homeServerUrl", "identityServerUrl", "setDefaultUrlsFromReferrer", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ServerUrlsRepositories.kt */
public final class ServerUrlsRepository {
    private static final String DEFAULT_REFERRER_HOME_SERVER_URL_PREF = "default_referrer_home_server_url";
    private static final String DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF = "default_referrer_identity_server_url";
    @NotNull
    public static final String HOME_SERVER_URL_PREF = "home_server_url";
    @NotNull
    public static final String IDENTITY_SERVER_URL_PREF = "identity_server_url";
    public static final ServerUrlsRepository INSTANCE = new ServerUrlsRepository();

    private ServerUrlsRepository() {
    }

    public final void setDefaultUrlsFromReferrer(@NotNull Context context, @NotNull String str, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "homeServerUrl");
        Intrinsics.checkParameterIsNotNull(str2, "identityServerUrl");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…haredPreferences(context)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        if (!TextUtils.isEmpty(str)) {
            edit.putString(DEFAULT_REFERRER_HOME_SERVER_URL_PREF, str);
        }
        if (!TextUtils.isEmpty(str2)) {
            edit.putString(DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF, str2);
        }
        edit.apply();
    }

    public final void saveServerUrls(@NotNull Context context, @NotNull String str, @NotNull String str2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "homeServerUrl");
        Intrinsics.checkParameterIsNotNull(str2, "identityServerUrl");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…haredPreferences(context)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        edit.putString(HOME_SERVER_URL_PREF, str);
        edit.putString(IDENTITY_SERVER_URL_PREF, str2);
        edit.apply();
    }

    @NotNull
    public final String getLastHomeServerUrl(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString(HOME_SERVER_URL_PREF, defaultSharedPreferences.getString(DEFAULT_REFERRER_HOME_SERVER_URL_PREF, getDefaultHomeServerUrl(context)));
        Intrinsics.checkExpressionValueIsNotNull(string, "prefs.getString(HOME_SER…tHomeServerUrl(context)))");
        return string;
    }

    @NotNull
    public final String getLastIdentityServerUrl(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String string = defaultSharedPreferences.getString(IDENTITY_SERVER_URL_PREF, defaultSharedPreferences.getString(DEFAULT_REFERRER_IDENTITY_SERVER_URL_PREF, getDefaultIdentityServerUrl(context)));
        Intrinsics.checkExpressionValueIsNotNull(string, "prefs.getString(IDENTITY…ntityServerUrl(context)))");
        return string;
    }

    public final boolean isDefaultHomeServerUrl(@NotNull Context context, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        return Intrinsics.areEqual((Object) str, (Object) getDefaultHomeServerUrl(context));
    }

    public final boolean isDefaultIdentityServerUrl(@NotNull Context context, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, ImagesContract.URL);
        return Intrinsics.areEqual((Object) str, (Object) getDefaultIdentityServerUrl(context));
    }

    @NotNull
    public final String getDefaultHomeServerUrl(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        String string = context.getString(C1299R.string.default_hs_server_url);
        Intrinsics.checkExpressionValueIsNotNull(string, "context.getString(R.string.default_hs_server_url)");
        return string;
    }

    @NotNull
    public final String getDefaultIdentityServerUrl(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        String string = context.getString(C1299R.string.default_identity_server_url);
        Intrinsics.checkExpressionValueIsNotNull(string, "context.getString(R.stri…ault_identity_server_url)");
        return string;
    }
}
