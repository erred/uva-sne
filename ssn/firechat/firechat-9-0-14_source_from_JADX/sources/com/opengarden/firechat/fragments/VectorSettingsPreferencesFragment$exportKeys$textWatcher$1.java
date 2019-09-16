package com.opengarden.firechat.fragments;

import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J(\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0016J(\u0010\r\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0016¨\u0006\u000f"}, mo21251d2 = {"com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$exportKeys$textWatcher$1", "Landroid/text/TextWatcher;", "(Landroid/widget/Button;Landroid/support/design/widget/TextInputEditText;Landroid/support/design/widget/TextInputEditText;)V", "afterTextChanged", "", "s", "Landroid/text/Editable;", "beforeTextChanged", "", "start", "", "count", "after", "onTextChanged", "before", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment$exportKeys$textWatcher$1 implements TextWatcher {
    final /* synthetic */ Button $exportButton;
    final /* synthetic */ TextInputEditText $passPhrase1EditText;
    final /* synthetic */ TextInputEditText $passPhrase2EditText;

    public void afterTextChanged(@NotNull Editable editable) {
        Intrinsics.checkParameterIsNotNull(editable, "s");
    }

    public void beforeTextChanged(@NotNull CharSequence charSequence, int i, int i2, int i3) {
        Intrinsics.checkParameterIsNotNull(charSequence, "s");
    }

    VectorSettingsPreferencesFragment$exportKeys$textWatcher$1(Button button, TextInputEditText textInputEditText, TextInputEditText textInputEditText2) {
        this.$exportButton = button;
        this.$passPhrase1EditText = textInputEditText;
        this.$passPhrase2EditText = textInputEditText2;
    }

    public void onTextChanged(@NotNull CharSequence charSequence, int i, int i2, int i3) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(charSequence, "s");
        Button button = this.$exportButton;
        Intrinsics.checkExpressionValueIsNotNull(button, "exportButton");
        TextInputEditText textInputEditText = this.$passPhrase1EditText;
        Intrinsics.checkExpressionValueIsNotNull(textInputEditText, "passPhrase1EditText");
        if (!TextUtils.isEmpty(textInputEditText.getText())) {
            TextInputEditText textInputEditText2 = this.$passPhrase1EditText;
            Intrinsics.checkExpressionValueIsNotNull(textInputEditText2, "passPhrase1EditText");
            CharSequence text = textInputEditText2.getText();
            TextInputEditText textInputEditText3 = this.$passPhrase2EditText;
            Intrinsics.checkExpressionValueIsNotNull(textInputEditText3, "passPhrase2EditText");
            if (TextUtils.equals(text, textInputEditText3.getText())) {
                z = true;
                button.setEnabled(z);
            }
        }
        z = false;
        button.setEnabled(z);
    }
}
