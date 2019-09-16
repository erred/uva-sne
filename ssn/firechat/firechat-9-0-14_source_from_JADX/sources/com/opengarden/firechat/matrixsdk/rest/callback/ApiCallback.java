package com.opengarden.firechat.matrixsdk.rest.callback;

public interface ApiCallback<T> extends ApiFailureCallback {
    void onSuccess(T t);
}
