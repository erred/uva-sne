package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u0010\u0000\u001a\u00020\u0001H\n¢\u0006\u0002\b\u0002"}, mo21251d2 = {"<anonymous>", "", "run"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$onPasswordUpdateClick$1 implements Runnable {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$onPasswordUpdateClick$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final void run() {
        Builder builder = new Builder(this.this$0.getActivity());
        Activity activity = this.this$0.getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        final View inflate = activity.getLayoutInflater().inflate(C1299R.layout.fragment_dialog_change_password, null);
        builder.setView(inflate);
        builder.setTitle(this.this$0.getString(C1299R.string.settings_change_password));
        final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.change_password_old_pwd_text);
        final EditText editText2 = (EditText) inflate.findViewById(C1299R.C1301id.change_password_new_pwd_text);
        final EditText editText3 = (EditText) inflate.findViewById(C1299R.C1301id.change_password_confirm_new_pwd_text);
        builder.setPositiveButton(C1299R.string.save, new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onPasswordUpdateClick$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                if (this.this$0.this$0.getActivity() != null) {
                    Object systemService = this.this$0.this$0.getActivity().getSystemService("input_method");
                    if (systemService == null) {
                        throw new TypeCastException("null cannot be cast to non-null type android.view.inputmethod.InputMethodManager");
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) systemService;
                    View view = inflate;
                    Intrinsics.checkExpressionValueIsNotNull(view, "view");
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
                EditText editText = editText;
                Intrinsics.checkExpressionValueIsNotNull(editText, "oldPasswordText");
                CharSequence obj = editText.getText().toString();
                int length = obj.length() - 1;
                int i2 = 0;
                boolean z = false;
                while (i2 <= length) {
                    boolean z2 = obj.charAt(!z ? i2 : length) <= ' ';
                    if (!z) {
                        if (!z2) {
                            z = true;
                        } else {
                            i2++;
                        }
                    } else if (!z2) {
                        break;
                    } else {
                        length--;
                    }
                }
                String obj2 = obj.subSequence(i2, length + 1).toString();
                EditText editText2 = editText2;
                Intrinsics.checkExpressionValueIsNotNull(editText2, "newPasswordText");
                CharSequence obj3 = editText2.getText().toString();
                int length2 = obj3.length() - 1;
                int i3 = 0;
                boolean z3 = false;
                while (i3 <= length2) {
                    boolean z4 = obj3.charAt(!z3 ? i3 : length2) <= ' ';
                    if (!z3) {
                        if (!z4) {
                            z3 = true;
                        } else {
                            i3++;
                        }
                    } else if (!z4) {
                        break;
                    } else {
                        length2--;
                    }
                }
                String obj4 = obj3.subSequence(i3, length2 + 1).toString();
                this.this$0.this$0.displayLoadingView();
                VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).updatePassword(obj2, obj4, new ApiCallback<Void>(this) {
                    final /* synthetic */ C22951 this$0;

                    {
                        this.this$0 = r1;
                    }

                    private final void onDone(int i) {
                        if (this.this$0.this$0.this$0.getActivity() != null) {
                            this.this$0.this$0.this$0.getActivity().runOnUiThread(new C2300xfb86c208(this, i));
                        }
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        onDone(C1299R.string.settings_password_updated);
                    }

                    public void onNetworkError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        onDone(C1299R.string.settings_fail_to_update_password);
                    }

                    public void onMatrixError(@NotNull MatrixError matrixError) {
                        Intrinsics.checkParameterIsNotNull(matrixError, "e");
                        onDone(C1299R.string.settings_fail_to_update_password);
                    }

                    public void onUnexpectedError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        onDone(C1299R.string.settings_fail_to_update_password);
                    }
                });
            }
        });
        builder.setNegativeButton(C1299R.string.cancel, new OnClickListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onPasswordUpdateClick$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                if (this.this$0.this$0.getActivity() != null) {
                    Object systemService = this.this$0.this$0.getActivity().getSystemService("input_method");
                    if (systemService == null) {
                        throw new TypeCastException("null cannot be cast to non-null type android.view.inputmethod.InputMethodManager");
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) systemService;
                    View view = inflate;
                    Intrinsics.checkExpressionValueIsNotNull(view, "view");
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            }
        });
        AlertDialog show = builder.show();
        show.setOnCancelListener(new OnCancelListener(this) {
            final /* synthetic */ VectorSettingsPreferencesFragment$onPasswordUpdateClick$1 this$0;

            {
                this.this$0 = r1;
            }

            public final void onCancel(DialogInterface dialogInterface) {
                if (this.this$0.this$0.getActivity() != null) {
                    Object systemService = this.this$0.this$0.getActivity().getSystemService("input_method");
                    if (systemService == null) {
                        throw new TypeCastException("null cannot be cast to non-null type android.view.inputmethod.InputMethodManager");
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) systemService;
                    View view = inflate;
                    Intrinsics.checkExpressionValueIsNotNull(view, "view");
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                }
            }
        });
        final Button button = show.getButton(-1);
        Intrinsics.checkExpressionValueIsNotNull(button, "saveButton");
        button.setEnabled(false);
        editText3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(@NotNull Editable editable) {
                Intrinsics.checkParameterIsNotNull(editable, "s");
            }

            public void beforeTextChanged(@NotNull CharSequence charSequence, int i, int i2, int i3) {
                Intrinsics.checkParameterIsNotNull(charSequence, "s");
            }

            public void onTextChanged(@NotNull CharSequence charSequence, int i, int i2, int i3) {
                Intrinsics.checkParameterIsNotNull(charSequence, "s");
                EditText editText = editText;
                Intrinsics.checkExpressionValueIsNotNull(editText, "oldPasswordText");
                CharSequence obj = editText.getText().toString();
                boolean z = true;
                int length = obj.length() - 1;
                int i4 = 0;
                boolean z2 = false;
                while (i4 <= length) {
                    boolean z3 = obj.charAt(!z2 ? i4 : length) <= ' ';
                    if (!z2) {
                        if (!z3) {
                            z2 = true;
                        } else {
                            i4++;
                        }
                    } else if (!z3) {
                        break;
                    } else {
                        length--;
                    }
                }
                String obj2 = obj.subSequence(i4, length + 1).toString();
                EditText editText2 = editText2;
                Intrinsics.checkExpressionValueIsNotNull(editText2, "newPasswordText");
                CharSequence obj3 = editText2.getText().toString();
                int length2 = obj3.length() - 1;
                int i5 = 0;
                boolean z4 = false;
                while (i5 <= length2) {
                    boolean z5 = obj3.charAt(!z4 ? i5 : length2) <= ' ';
                    if (!z4) {
                        if (!z5) {
                            z4 = true;
                        } else {
                            i5++;
                        }
                    } else if (!z5) {
                        break;
                    } else {
                        length2--;
                    }
                }
                String obj4 = obj3.subSequence(i5, length2 + 1).toString();
                EditText editText3 = editText3;
                Intrinsics.checkExpressionValueIsNotNull(editText3, "confirmNewPasswordText");
                CharSequence obj5 = editText3.getText().toString();
                int length3 = obj5.length() - 1;
                int i6 = 0;
                boolean z6 = false;
                while (i6 <= length3) {
                    boolean z7 = obj5.charAt(!z6 ? i6 : length3) <= ' ';
                    if (!z6) {
                        if (!z7) {
                            z6 = true;
                        } else {
                            i6++;
                        }
                    } else if (!z7) {
                        break;
                    } else {
                        length3--;
                    }
                }
                String obj6 = obj5.subSequence(i6, length3 + 1).toString();
                Button button = button;
                Intrinsics.checkExpressionValueIsNotNull(button, "saveButton");
                if (obj2.length() <= 0 || obj4.length() <= 0 || !TextUtils.equals(obj4, obj6)) {
                    z = false;
                }
                button.setEnabled(z);
            }
        });
    }
}
