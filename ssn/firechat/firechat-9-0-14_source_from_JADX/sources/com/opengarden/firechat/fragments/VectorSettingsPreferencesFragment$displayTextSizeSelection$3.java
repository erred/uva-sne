package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckedTextView;
import com.opengarden.firechat.settings.FontScale;
import kotlin.Metadata;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/view/View;", "kotlin.jvm.PlatformType", "onClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$displayTextSizeSelection$3 implements OnClickListener {
    final /* synthetic */ Activity $activity;
    final /* synthetic */ AlertDialog $dialog;

    /* renamed from: $v */
    final /* synthetic */ View f123$v;

    VectorSettingsPreferencesFragment$displayTextSizeSelection$3(AlertDialog alertDialog, View view, Activity activity) {
        this.$dialog = alertDialog;
        this.f123$v = view;
        this.$activity = activity;
    }

    public final void onClick(View view) {
        this.$dialog.dismiss();
        FontScale.INSTANCE.updateFontScale(((CheckedTextView) this.f123$v).getText().toString());
        this.$activity.startActivity(this.$activity.getIntent());
        this.$activity.finish();
    }
}
