package com.facebook.imagepipeline.producers;

import com.facebook.common.executors.StatefulRunnable;
import java.util.Map;

public abstract class StatefulProducerRunnable<T> extends StatefulRunnable<T> {
    private final Consumer<T> mConsumer;
    private final ProducerListener mProducerListener;
    private final String mProducerName;
    private final String mRequestId;

    /* access modifiers changed from: protected */
    public abstract void disposeResult(T t);

    /* access modifiers changed from: protected */
    public Map<String, String> getExtraMapOnCancellation() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Map<String, String> getExtraMapOnFailure(Exception exc) {
        return null;
    }

    /* access modifiers changed from: protected */
    public Map<String, String> getExtraMapOnSuccess(T t) {
        return null;
    }

    public StatefulProducerRunnable(Consumer<T> consumer, ProducerListener producerListener, String str, String str2) {
        this.mConsumer = consumer;
        this.mProducerListener = producerListener;
        this.mProducerName = str;
        this.mRequestId = str2;
        this.mProducerListener.onProducerStart(this.mRequestId, this.mProducerName);
    }

    /* access modifiers changed from: protected */
    public void onSuccess(T t) {
        this.mProducerListener.onProducerFinishWithSuccess(this.mRequestId, this.mProducerName, this.mProducerListener.requiresExtraMap(this.mRequestId) ? getExtraMapOnSuccess(t) : null);
        this.mConsumer.onNewResult(t, true);
    }

    /* access modifiers changed from: protected */
    public void onFailure(Exception exc) {
        this.mProducerListener.onProducerFinishWithFailure(this.mRequestId, this.mProducerName, exc, this.mProducerListener.requiresExtraMap(this.mRequestId) ? getExtraMapOnFailure(exc) : null);
        this.mConsumer.onFailure(exc);
    }

    /* access modifiers changed from: protected */
    public void onCancellation() {
        this.mProducerListener.onProducerFinishWithCancellation(this.mRequestId, this.mProducerName, this.mProducerListener.requiresExtraMap(this.mRequestId) ? getExtraMapOnCancellation() : null);
        this.mConsumer.onCancellation();
    }
}
