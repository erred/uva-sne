package com.opengarden.firechat.matrixsdk.rest.client;

import android.os.HandlerThread;
import android.support.annotation.NonNull;
import com.opengarden.firechat.matrixsdk.util.MXOsHandler;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

public class MXRestExecutorService extends AbstractExecutorService {
    private MXOsHandler mHandler = new MXOsHandler(this.mHandlerThread.getLooper());
    private HandlerThread mHandlerThread;

    public boolean awaitTermination(long j, @NonNull TimeUnit timeUnit) throws InterruptedException {
        return false;
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public void shutdown() {
    }

    public MXRestExecutorService() {
        StringBuilder sb = new StringBuilder();
        sb.append("MXRestExecutor");
        sb.append(hashCode());
        this.mHandlerThread = new HandlerThread(sb.toString(), 1);
        this.mHandlerThread.start();
    }

    public void execute(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    public void stop() {
        if (this.mHandlerThread != null) {
            this.mHandlerThread.quit();
        }
    }

    @NonNull
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }
}
