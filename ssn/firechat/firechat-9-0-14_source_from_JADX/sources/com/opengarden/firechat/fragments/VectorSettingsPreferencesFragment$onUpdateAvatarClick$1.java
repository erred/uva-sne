package com.opengarden.firechat.fragments;

import android.content.Intent;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorMediasPickerActivity;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onUpdateAvatarClick$1 implements Runnable {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onUpdateAvatarClick$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final void run() {
        if (CommonActivityUtils.checkPermissions(3, this.this$0.getActivity())) {
            Intent intent = new Intent(this.this$0.getActivity(), VectorMediasPickerActivity.class);
            intent.putExtra(VectorMediasPickerActivity.EXTRA_AVATAR_MODE, true);
            this.this$0.startActivityForResult(intent, 1);
        }
    }
}
