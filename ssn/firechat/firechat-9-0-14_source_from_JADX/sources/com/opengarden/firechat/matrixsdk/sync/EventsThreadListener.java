package com.opengarden.firechat.matrixsdk.sync;

import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;

public interface EventsThreadListener {
    void onConfigurationError(String str);

    void onSyncError(MatrixError matrixError);

    void onSyncResponse(SyncResponse syncResponse, String str, boolean z);
}
