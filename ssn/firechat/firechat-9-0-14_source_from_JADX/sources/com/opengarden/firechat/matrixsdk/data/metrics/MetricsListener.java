package com.opengarden.firechat.matrixsdk.data.metrics;

public interface MetricsListener {
    void onIncrementalSyncFinished(long j);

    void onInitialSyncFinished(long j);

    void onRoomsLoaded(int i);

    void onStorePreloaded(long j);
}
