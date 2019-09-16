package com.opengarden.firechat.matrixsdk.rest.callback;

import android.content.Context;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;

public class ToastErrorHandler implements ApiFailureCallback {
    private final Context context;
    private final String msgPrefix;

    public ToastErrorHandler(Context context2, String str) {
        this.context = context2;
        this.msgPrefix = str;
    }

    public void onNetworkError(Exception exc) {
        Toast.makeText(this.context, appendPrefix("Connection error"), 1).show();
    }

    public void onMatrixError(MatrixError matrixError) {
        Toast.makeText(this.context, appendPrefix(matrixError.getLocalizedMessage()), 1).show();
    }

    public void onUnexpectedError(Exception exc) {
        Toast.makeText(this.context, appendPrefix(null), 1).show();
    }

    /* access modifiers changed from: 0000 */
    public String appendPrefix(String str) {
        if (str == null) {
            return this.msgPrefix;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.msgPrefix);
        sb.append(": ");
        sb.append(str);
        return sb.toString();
    }
}
