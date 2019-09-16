package com.opengarden.firechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.util.FileUtilsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J\u001a\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0002J\u0018\u0010\r\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000fH\u0016¨\u0006\u0011"}, mo21251d2 = {"Lcom/opengarden/firechat/receiver/DebugReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "alterScalarToken", "", "context", "Landroid/content/Context;", "dumpPreferences", "logPrefs", "name", "", "sharedPreferences", "Landroid/content/SharedPreferences;", "onReceive", "intent", "Landroid/content/Intent;", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: DebugReciever.kt */
public final class DebugReceiver extends BroadcastReceiver {
    public static final Companion Companion = new Companion(null);
    private static final String DEBUG_ACTION_ALTER_SCALAR_TOKEN = "im.vector.receiver.DEBUG_ACTION_ALTER_SCALAR_TOKEN";
    private static final String DEBUG_ACTION_DUMP_FILESYSTEM = "im.vector.receiver.DEBUG_ACTION_DUMP_FILESYSTEM";
    private static final String DEBUG_ACTION_DUMP_PREFERENCES = "im.vector.receiver.DEBUG_ACTION_DUMP_PREFERENCES";
    private static final String LOG_TAG = "DebugReceiver";

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\n"}, mo21251d2 = {"Lcom/opengarden/firechat/receiver/DebugReceiver$Companion;", "", "()V", "DEBUG_ACTION_ALTER_SCALAR_TOKEN", "", "DEBUG_ACTION_DUMP_FILESYSTEM", "DEBUG_ACTION_DUMP_PREFERENCES", "LOG_TAG", "getIntentFilter", "Landroid/content/IntentFilter;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: DebugReciever.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @NotNull
        public final IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DebugReceiver.DEBUG_ACTION_DUMP_FILESYSTEM);
            intentFilter.addAction(DebugReceiver.DEBUG_ACTION_DUMP_PREFERENCES);
            intentFilter.addAction(DebugReceiver.DEBUG_ACTION_ALTER_SCALAR_TOKEN);
            return intentFilter;
        }
    }

    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Received debug action: ");
        sb.append(intent.getAction());
        Log.d(str, sb.toString());
        String action = intent.getAction();
        if (action != null) {
            int hashCode = action.hashCode();
            if (hashCode != -1220548329) {
                if (hashCode != 15867372) {
                    if (hashCode == 1194919566 && action.equals(DEBUG_ACTION_ALTER_SCALAR_TOKEN)) {
                        alterScalarToken(context);
                    }
                } else if (action.equals(DEBUG_ACTION_DUMP_PREFERENCES)) {
                    dumpPreferences(context);
                }
            } else if (action.equals(DEBUG_ACTION_DUMP_FILESYSTEM)) {
                FileUtilsKt.lsFiles(context);
            }
        }
    }

    private final void dumpPreferences(Context context) {
        logPrefs("DefaultSharedPreferences", PreferenceManager.getDefaultSharedPreferences(context));
        logPrefs("Vector.LoginStorage", context.getSharedPreferences("Vector.LoginStorage", 0));
        logPrefs("GcmRegistrationManager", context.getSharedPreferences("GcmRegistrationManager", 0));
    }

    private final void logPrefs(String str, SharedPreferences sharedPreferences) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("SharedPreferences ");
        sb.append(str);
        sb.append(':');
        Log.d(str2, sb.toString());
        if (sharedPreferences != null) {
            for (String str3 : sharedPreferences.getAll().keySet()) {
                String str4 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("");
                sb2.append(str3);
                sb2.append(" : ");
                sb2.append(sharedPreferences.getAll().get(str3));
                Log.d(str4, sb2.toString());
            }
        }
    }

    private final void alterScalarToken(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…haredPreferences(context)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        StringBuilder sb = new StringBuilder();
        sb.append("SCALAR_TOKEN_PREFERENCE_KEY");
        Matrix instance = Matrix.getInstance(context);
        Intrinsics.checkExpressionValueIsNotNull(instance, "Matrix.getInstance(context)");
        MXSession defaultSession = instance.getDefaultSession();
        Intrinsics.checkExpressionValueIsNotNull(defaultSession, "Matrix.getInstance(context).defaultSession");
        sb.append(defaultSession.getMyUserId());
        edit.putString(sb.toString(), "bad_token");
        edit.apply();
    }
}
