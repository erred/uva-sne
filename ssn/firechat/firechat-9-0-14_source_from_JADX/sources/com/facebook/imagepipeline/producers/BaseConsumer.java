package com.facebook.imagepipeline.producers;

import com.facebook.common.logging.FLog;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class BaseConsumer<T> implements Consumer<T> {
    private boolean mIsFinished = false;

    /* access modifiers changed from: protected */
    public abstract void onCancellationImpl();

    /* access modifiers changed from: protected */
    public abstract void onFailureImpl(Throwable th);

    /* access modifiers changed from: protected */
    public abstract void onNewResultImpl(T t, boolean z);

    /* access modifiers changed from: protected */
    public void onProgressUpdateImpl(float f) {
    }

    public synchronized void onNewResult(@Nullable T t, boolean z) {
        if (!this.mIsFinished) {
            this.mIsFinished = z;
            try {
                onNewResultImpl(t, z);
            } catch (Exception e) {
                onUnhandledException(e);
            }
        } else {
            return;
        }
        return;
    }

    public synchronized void onFailure(Throwable th) {
        if (!this.mIsFinished) {
            this.mIsFinished = true;
            try {
                onFailureImpl(th);
            } catch (Exception e) {
                onUnhandledException(e);
            }
        } else {
            return;
        }
        return;
    }

    public synchronized void onCancellation() {
        if (!this.mIsFinished) {
            this.mIsFinished = true;
            try {
                onCancellationImpl();
            } catch (Exception e) {
                onUnhandledException(e);
            }
        } else {
            return;
        }
        return;
    }

    public synchronized void onProgressUpdate(float f) {
        if (!this.mIsFinished) {
            try {
                onProgressUpdateImpl(f);
            } catch (Exception e) {
                onUnhandledException(e);
            }
        } else {
            return;
        }
    }

    /* access modifiers changed from: protected */
    public void onUnhandledException(Exception exc) {
        FLog.wtf(getClass(), "unhandled exception", (Throwable) exc);
    }
}
