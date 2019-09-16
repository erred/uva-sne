package com.opengarden.firechat.matrixsdk.rest.callback;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;

public abstract class SimpleApiCallback<T> implements ApiCallback<T> {
    private static final String LOG_TAG = "SimpleApiCallback";
    private ApiFailureCallback failureCallback = null;
    /* access modifiers changed from: private */
    public Activity mActivity;
    /* access modifiers changed from: private */
    public Context mContext = null;
    private View mPostView = null;

    public SimpleApiCallback() {
    }

    public SimpleApiCallback(Activity activity) {
        this.mActivity = activity;
    }

    public SimpleApiCallback(Context context, View view) {
        this.mContext = context;
        this.mPostView = view;
    }

    public SimpleApiCallback(ApiFailureCallback apiFailureCallback) {
        this.failureCallback = apiFailureCallback;
    }

    private void displayToast(final String str) {
        if (this.mActivity != null) {
            this.mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(SimpleApiCallback.this.mActivity, str, 0).show();
                }
            });
        } else if (this.mContext != null && this.mPostView != null) {
            this.mPostView.post(new Runnable() {
                public void run() {
                    Toast.makeText(SimpleApiCallback.this.mContext, str, 0).show();
                }
            });
        }
    }

    public void onNetworkError(Exception exc) {
        if (this.failureCallback != null) {
            try {
                this.failureCallback.onNetworkError(exc);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onNetworkError() failed");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        } else {
            displayToast("Network Error");
        }
    }

    public void onMatrixError(MatrixError matrixError) {
        if (this.failureCallback != null) {
            try {
                this.failureCallback.onMatrixError(matrixError);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onMatrixError() failed");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Matrix Error : ");
            sb2.append(matrixError.getLocalizedMessage());
            displayToast(sb2.toString());
        }
    }

    public void onUnexpectedError(Exception exc) {
        if (this.failureCallback != null) {
            try {
                this.failureCallback.onUnexpectedError(exc);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onUnexpectedError() failed");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        } else {
            displayToast(exc.getLocalizedMessage());
        }
    }
}
