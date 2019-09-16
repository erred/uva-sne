package com.facebook.react.modules.datepicker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;
import javax.annotation.Nullable;

@SuppressLint({"ValidFragment"})
public class SupportDatePickerDialogFragment extends DialogFragment {
    @Nullable
    private OnDateSetListener mOnDateSetListener;
    @Nullable
    private OnDismissListener mOnDismissListener;

    public Dialog onCreateDialog(Bundle bundle) {
        return DatePickerDialogFragment.createDialog(getArguments(), getActivity(), this.mOnDateSetListener);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (this.mOnDismissListener != null) {
            this.mOnDismissListener.onDismiss(dialogInterface);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setOnDateSetListener(@Nullable OnDateSetListener onDateSetListener) {
        this.mOnDateSetListener = onDateSetListener;
    }

    /* access modifiers changed from: 0000 */
    public void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }
}
