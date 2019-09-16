package com.opengarden.firechat.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.util.PreferencesManager;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\b\u0010\u0007\u001a\u00020\u0004H\u0016¨\u0006\b"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$mEventsListener$1", "Lcom/opengarden/firechat/matrixsdk/listeners/MXEventListener;", "(Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;)V", "onAccountInfoUpdate", "", "myUser", "Lcom/opengarden/firechat/matrixsdk/data/MyUser;", "onBingRulesUpdate", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$mEventsListener$1 extends MXEventListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$mEventsListener$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public void onBingRulesUpdate() {
        this.this$0.refreshPreferences();
        this.this$0.refreshDisplay();
    }

    public void onAccountInfoUpdate(@NotNull MyUser myUser) {
        Intrinsics.checkParameterIsNotNull(myUser, "myUser");
        VectorApp instance = VectorApp.getInstance();
        Intrinsics.checkExpressionValueIsNotNull(instance, "VectorApp.getInstance()");
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(instance.getApplicationContext());
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…nce().applicationContext)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        edit.putString(PreferencesManager.SETTINGS_DISPLAY_NAME_PREFERENCE_KEY, myUser.displayname);
        edit.apply();
        this.this$0.refreshDisplay();
    }
}
