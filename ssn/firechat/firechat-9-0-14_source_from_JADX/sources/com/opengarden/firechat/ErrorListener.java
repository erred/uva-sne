package com.opengarden.firechat;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import com.opengarden.firechat.UnrecognizedCertHandler.Callback;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.ssl.CertUtil;
import com.opengarden.firechat.matrixsdk.ssl.Fingerprint;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Arrays;

public class ErrorListener implements ApiFailureCallback {
    private static final String LOG_TAG = "ErrorListener";
    /* access modifiers changed from: private */
    public final Activity mActivity;
    /* access modifiers changed from: private */
    public final MXSession mSession;

    public ErrorListener(MXSession mXSession, Activity activity) {
        this.mSession = mXSession;
        this.mActivity = activity;
    }

    public void onNetworkError(Exception exc) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Network error: ");
        sb.append(exc.getMessage());
        Log.m211e(str, sb.toString());
        if (!VectorApp.isAppInBackground()) {
            UnrecognizedCertificateException certificateException = CertUtil.getCertificateException(exc);
            if (certificateException == null) {
                handleNetworkError(exc);
            } else {
                handleCertError(certificateException, exc);
            }
        }
    }

    public void onMatrixError(MatrixError matrixError) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix error: ");
        sb.append(matrixError.errcode);
        sb.append(" - ");
        sb.append(matrixError.error);
        Log.m211e(str, sb.toString());
        if (MatrixError.UNKNOWN_TOKEN.equals(matrixError.errcode)) {
            CommonActivityUtils.logout(this.mActivity);
        }
    }

    public void onUnexpectedError(Exception exc) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected error: ");
        sb.append(exc.getMessage());
        Log.m211e(str, sb.toString());
    }

    /* access modifiers changed from: private */
    public void handleNetworkError(Exception exc) {
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ErrorListener.this.mActivity, ErrorListener.this.mActivity.getString(C1299R.string.network_error), 0).show();
            }
        });
    }

    private void handleCertError(UnrecognizedCertificateException unrecognizedCertificateException, final Exception exc) {
        final Fingerprint fingerprint = unrecognizedCertificateException.getFingerprint();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Found fingerprint: SHA-256: ");
        sb.append(fingerprint.getBytesAsHexString());
        Log.m209d(str, sb.toString());
        this.mActivity.runOnUiThread(new Runnable() {
            public void run() {
                UnrecognizedCertHandler.show(ErrorListener.this.mSession.getHomeServerConfig(), fingerprint, true, new Callback() {
                    public void onAccept() {
                        Matrix.getInstance(ErrorListener.this.mActivity.getApplicationContext()).getLoginStorage().replaceCredentials(ErrorListener.this.mSession.getHomeServerConfig());
                    }

                    public void onIgnore() {
                        ErrorListener.this.handleNetworkError(exc);
                    }

                    public void onReject() {
                        CommonActivityUtils.logout((Context) ErrorListener.this.mActivity, Arrays.asList(new MXSession[]{ErrorListener.this.mSession}), true, null);
                    }
                });
            }
        });
    }
}
