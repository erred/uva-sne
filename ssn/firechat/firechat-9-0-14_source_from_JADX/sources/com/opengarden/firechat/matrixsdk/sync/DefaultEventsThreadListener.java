package com.opengarden.firechat.matrixsdk.sync;

import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;

public class DefaultEventsThreadListener implements EventsThreadListener {
    private final MXDataHandler mDataHandler;

    public DefaultEventsThreadListener(MXDataHandler mXDataHandler) {
        this.mDataHandler = mXDataHandler;
    }

    public void onSyncResponse(SyncResponse syncResponse, String str, boolean z) {
        this.mDataHandler.onSyncResponse(syncResponse, str, z);
    }

    public void onSyncError(MatrixError matrixError) {
        this.mDataHandler.onSyncError(matrixError);
    }

    public void onConfigurationError(String str) {
        this.mDataHandler.onConfigurationError(str);
    }
}
