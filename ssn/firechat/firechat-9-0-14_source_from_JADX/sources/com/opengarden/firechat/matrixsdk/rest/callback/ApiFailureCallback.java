package com.opengarden.firechat.matrixsdk.rest.callback;

import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;

public interface ApiFailureCallback {
    void onMatrixError(MatrixError matrixError);

    void onNetworkError(Exception exc);

    void onUnexpectedError(Exception exc);
}
