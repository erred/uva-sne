package com.opengarden.firechat;

import com.opengarden.firechat.UnrecognizedCertHandler.Callback;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiFailureCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;

public class UnrecognizedCertApiCallback<T> extends SimpleApiCallback<T> {
    private ApiCallback mCallback;
    private HomeServerConnectionConfig mHsConfig;

    public void onAcceptedCert() {
    }

    public void onSuccess(T t) {
    }

    public UnrecognizedCertApiCallback(HomeServerConnectionConfig homeServerConnectionConfig, ApiCallback apiCallback) {
        super((ApiFailureCallback) apiCallback);
        this.mHsConfig = homeServerConnectionConfig;
        this.mCallback = apiCallback;
    }

    public UnrecognizedCertApiCallback(HomeServerConnectionConfig homeServerConnectionConfig) {
        this.mHsConfig = homeServerConnectionConfig;
    }

    public void onTLSOrNetworkError(Exception exc) {
        super.onNetworkError(exc);
    }

    public void onNetworkError(final Exception exc) {
        if (!UnrecognizedCertHandler.handle(this.mHsConfig, exc, new Callback() {
            public void onAccept() {
                UnrecognizedCertApiCallback.this.onAcceptedCert();
            }

            public void onIgnore() {
                UnrecognizedCertApiCallback.this.onTLSOrNetworkError(exc);
            }

            public void onReject() {
                UnrecognizedCertApiCallback.this.onTLSOrNetworkError(exc);
            }
        })) {
            onTLSOrNetworkError(exc);
        }
    }
}
