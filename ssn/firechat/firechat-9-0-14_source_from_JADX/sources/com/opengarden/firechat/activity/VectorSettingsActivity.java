package com.opengarden.firechat.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment;
import com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment.Companion;
import com.opengarden.firechat.matrixsdk.MXSession;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0004H\u0016J\b\u0010\u0006\u001a\u00020\u0007H\u0016J+\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u00042\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0016¢\u0006\u0002\u0010\u000f¨\u0006\u0010"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/VectorSettingsActivity;", "Lcom/opengarden/firechat/activity/MXCActionBarActivity;", "()V", "getLayoutRes", "", "getTitleRes", "initUiAndData", "", "onRequestPermissionsResult", "aRequestCode", "aPermissions", "", "", "aGrantResults", "", "(I[Ljava/lang/String;[I)V", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsActivity.kt */
public final class VectorSettingsActivity extends MXCActionBarActivity {
    public int getLayoutRes() {
        return C1299R.layout.activity_vector_settings;
    }

    public int getTitleRes() {
        return C1299R.string.title_activity_settings;
    }

    public void initUiAndData() {
        MXSession session = getSession(getIntent());
        if (session == null) {
            Matrix instance = Matrix.getInstance(this);
            Intrinsics.checkExpressionValueIsNotNull(instance, "Matrix.getInstance(this)");
            session = instance.getDefaultSession();
        }
        if (session == null) {
            finish();
            return;
        }
        if (isFirstCreation()) {
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            Companion companion = VectorSettingsPreferencesFragment.Companion;
            String myUserId = session.getMyUserId();
            Intrinsics.checkExpressionValueIsNotNull(myUserId, "session.myUserId");
            beginTransaction.replace(C1299R.C1301id.vector_settings_page, companion.newInstance(myUserId)).commit();
        }
    }

    public void onRequestPermissionsResult(int i, @NotNull String[] strArr, @NotNull int[] iArr) {
        Intrinsics.checkParameterIsNotNull(strArr, "aPermissions");
        Intrinsics.checkParameterIsNotNull(iArr, "aGrantResults");
        if (i == 3) {
            int length = iArr.length;
            int i2 = 0;
            boolean z = false;
            while (true) {
                boolean z2 = true;
                if (i2 >= length) {
                    break;
                }
                if (iArr[i2] != 0) {
                    z2 = false;
                }
                z |= z2;
                i2++;
            }
            if (z) {
                Intent intent = new Intent(this, VectorMediasPickerActivity.class);
                intent.putExtra(VectorMediasPickerActivity.EXTRA_AVATAR_MODE, true);
                startActivityForResult(intent, 1);
            }
        }
    }
}
