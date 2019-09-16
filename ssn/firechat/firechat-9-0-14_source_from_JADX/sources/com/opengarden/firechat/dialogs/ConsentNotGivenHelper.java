package com.opengarden.firechat.dialogs;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.net.Uri;
import android.os.Bundle;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.VectorWebViewActivity;
import com.opengarden.firechat.activity.interfaces.Restorable;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.webview.WebViewMode;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u0006J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0010\u0010\u0010\u001a\u00020\n2\u0006\u0010\u0011\u001a\u00020\u0005H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u000e¢\u0006\u0002\n\u0000¨\u0006\u0013"}, mo21251d2 = {"Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper;", "Lcom/opengarden/firechat/activity/interfaces/Restorable;", "activity", "Landroid/app/Activity;", "savedInstanceState", "Landroid/os/Bundle;", "(Landroid/app/Activity;Landroid/os/Bundle;)V", "isDialogDisplayed", "", "displayDialog", "", "matrixError", "Lcom/opengarden/firechat/matrixsdk/rest/model/MatrixError;", "openWebViewActivity", "consentUri", "", "saveState", "outState", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ConsentNotGivenHelper.kt */
public final class ConsentNotGivenHelper implements Restorable {
    public static final Companion Companion = new Companion(null);
    private static final String KEY_DIALOG_IS_DISPLAYED = "ConsentNotGivenHelper.KEY_DIALOG_IS_DISPLAYED";
    private static final String LOG_TAG = "ConsentNotGivenHelper";
    private final Activity activity;
    /* access modifiers changed from: private */
    public boolean isDialogDisplayed;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000¨\u0006\u0006"}, mo21251d2 = {"Lcom/opengarden/firechat/dialogs/ConsentNotGivenHelper$Companion;", "", "()V", "KEY_DIALOG_IS_DISPLAYED", "", "LOG_TAG", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: ConsentNotGivenHelper.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    public ConsentNotGivenHelper(@NotNull Activity activity2, @Nullable Bundle bundle) {
        Intrinsics.checkParameterIsNotNull(activity2, "activity");
        this.activity = activity2;
        boolean z = true;
        if (bundle == null || !bundle.getBoolean(KEY_DIALOG_IS_DISPLAYED, false)) {
            z = false;
        }
        this.isDialogDisplayed = z;
    }

    public final void displayDialog(@NotNull MatrixError matrixError) {
        Intrinsics.checkParameterIsNotNull(matrixError, "matrixError");
        if (this.isDialogDisplayed) {
            Log.m217w(LOG_TAG, "Filtered dialog request");
        } else if (matrixError.consentUri == null) {
            Log.m211e(LOG_TAG, "Missing required parameter 'consent_uri'");
        } else {
            this.isDialogDisplayed = true;
            Builder title = new Builder(this.activity).setTitle(C1299R.string.settings_app_term_conditions);
            Activity activity2 = this.activity;
            Matrix instance = Matrix.getInstance(this.activity);
            Intrinsics.checkExpressionValueIsNotNull(instance, "Matrix.getInstance(activity)");
            MXSession defaultSession = instance.getDefaultSession();
            Intrinsics.checkExpressionValueIsNotNull(defaultSession, "Matrix.getInstance(activity).defaultSession");
            HomeServerConnectionConfig homeServerConfig = defaultSession.getHomeServerConfig();
            Intrinsics.checkExpressionValueIsNotNull(homeServerConfig, "Matrix.getInstance(activ…tSession.homeServerConfig");
            Uri homeserverUri = homeServerConfig.getHomeserverUri();
            Intrinsics.checkExpressionValueIsNotNull(homeserverUri, "Matrix.getInstance(activ…erverConfig.homeserverUri");
            title.setMessage(activity2.getString(C1299R.string.dialog_user_consent_content, new Object[]{homeserverUri.getHost()})).setPositiveButton(C1299R.string.dialog_user_consent_submit, new ConsentNotGivenHelper$displayDialog$1(this, matrixError)).setNegativeButton(C1299R.string.later, new ConsentNotGivenHelper$displayDialog$2(this)).setOnCancelListener(new ConsentNotGivenHelper$displayDialog$3(this)).show();
        }
    }

    public void saveState(@NotNull Bundle bundle) {
        Intrinsics.checkParameterIsNotNull(bundle, "outState");
        bundle.putBoolean(KEY_DIALOG_IS_DISPLAYED, this.isDialogDisplayed);
    }

    /* access modifiers changed from: private */
    public final void openWebViewActivity(String str) {
        this.activity.startActivity(VectorWebViewActivity.Companion.getIntent(this.activity, str, C1299R.string.settings_app_term_conditions, WebViewMode.CONSENT));
    }
}
