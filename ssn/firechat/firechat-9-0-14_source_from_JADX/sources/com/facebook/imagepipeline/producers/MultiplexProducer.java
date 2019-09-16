package com.facebook.imagepipeline.producers;

import android.util.Pair;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.common.Priority;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class MultiplexProducer<K, T extends Closeable> implements Producer<T> {
    /* access modifiers changed from: private */
    public final Producer<T> mInputProducer;
    @GuardedBy("this")
    @VisibleForTesting
    final Map<K, Multiplexer> mMultiplexers = new HashMap();

    @VisibleForTesting
    class Multiplexer {
        /* access modifiers changed from: private */
        public final CopyOnWriteArraySet<Pair<Consumer<T>, ProducerContext>> mConsumerContextPairs = Sets.newCopyOnWriteArraySet();
        @GuardedBy("Multiplexer.this")
        @Nullable
        private com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer mForwardingConsumer;
        private final K mKey;
        @GuardedBy("Multiplexer.this")
        @Nullable
        private T mLastIntermediateResult;
        @GuardedBy("Multiplexer.this")
        private float mLastProgress;
        /* access modifiers changed from: private */
        @GuardedBy("Multiplexer.this")
        @Nullable
        public BaseProducerContext mMultiplexProducerContext;

        private class ForwardingConsumer extends BaseConsumer<T> {
            private ForwardingConsumer() {
            }

            /* access modifiers changed from: protected */
            public void onNewResultImpl(T t, boolean z) {
                Multiplexer.this.onNextResult(this, t, z);
            }

            /* access modifiers changed from: protected */
            public void onFailureImpl(Throwable th) {
                Multiplexer.this.onFailure(this, th);
            }

            /* access modifiers changed from: protected */
            public void onCancellationImpl() {
                Multiplexer.this.onCancelled(this);
            }

            /* access modifiers changed from: protected */
            public void onProgressUpdateImpl(float f) {
                Multiplexer.this.onProgressUpdate(this, f);
            }
        }

        public Multiplexer(K k) {
            this.mKey = k;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0031, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
            monitor-enter(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
            if (r5 == r7.mLastIntermediateResult) goto L_0x0039;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0037, code lost:
            r5 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0039, code lost:
            if (r5 == null) goto L_0x0041;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
            r5 = r7.this$0.cloneOrNull(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0041, code lost:
            monitor-exit(r7);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0042, code lost:
            if (r5 == null) goto L_0x0052;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
            if (r6 <= 0.0f) goto L_0x004c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            r8.onProgressUpdate(r6);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x004c, code lost:
            r8.onNewResult(r5, false);
            closeSafely(r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0052, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0053, code lost:
            addCallbacks(r0, r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0057, code lost:
            return true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
            com.facebook.imagepipeline.producers.BaseProducerContext.callOnIsPrefetchChanged(r1);
            com.facebook.imagepipeline.producers.BaseProducerContext.callOnPriorityChanged(r3);
            com.facebook.imagepipeline.producers.BaseProducerContext.callOnIsIntermediateResultExpectedChanged(r4);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean addNewConsumer(com.facebook.imagepipeline.producers.Consumer<T> r8, com.facebook.imagepipeline.producers.ProducerContext r9) {
            /*
                r7 = this;
                android.util.Pair r0 = android.util.Pair.create(r8, r9)
                monitor-enter(r7)
                com.facebook.imagepipeline.producers.MultiplexProducer r1 = com.facebook.imagepipeline.producers.MultiplexProducer.this     // Catch:{ all -> 0x005e }
                K r2 = r7.mKey     // Catch:{ all -> 0x005e }
                com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer r1 = r1.getExistingMultiplexer(r2)     // Catch:{ all -> 0x005e }
                r2 = 0
                if (r1 == r7) goto L_0x0012
                monitor-exit(r7)     // Catch:{ all -> 0x005e }
                return r2
            L_0x0012:
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r1 = r7.mConsumerContextPairs     // Catch:{ all -> 0x005e }
                r1.add(r0)     // Catch:{ all -> 0x005e }
                java.util.List r1 = r7.updateIsPrefetch()     // Catch:{ all -> 0x005e }
                java.util.List r3 = r7.updatePriority()     // Catch:{ all -> 0x005e }
                java.util.List r4 = r7.updateIsIntermediateResultExpected()     // Catch:{ all -> 0x005e }
                T r5 = r7.mLastIntermediateResult     // Catch:{ all -> 0x005e }
                float r6 = r7.mLastProgress     // Catch:{ all -> 0x005e }
                monitor-exit(r7)     // Catch:{ all -> 0x005e }
                com.facebook.imagepipeline.producers.BaseProducerContext.callOnIsPrefetchChanged(r1)
                com.facebook.imagepipeline.producers.BaseProducerContext.callOnPriorityChanged(r3)
                com.facebook.imagepipeline.producers.BaseProducerContext.callOnIsIntermediateResultExpectedChanged(r4)
                monitor-enter(r0)
                monitor-enter(r7)     // Catch:{ all -> 0x005b }
                T r1 = r7.mLastIntermediateResult     // Catch:{ all -> 0x0058 }
                if (r5 == r1) goto L_0x0039
                r5 = 0
                goto L_0x0041
            L_0x0039:
                if (r5 == 0) goto L_0x0041
                com.facebook.imagepipeline.producers.MultiplexProducer r1 = com.facebook.imagepipeline.producers.MultiplexProducer.this     // Catch:{ all -> 0x0058 }
                java.io.Closeable r5 = r1.cloneOrNull(r5)     // Catch:{ all -> 0x0058 }
            L_0x0041:
                monitor-exit(r7)     // Catch:{ all -> 0x0058 }
                if (r5 == 0) goto L_0x0052
                r1 = 0
                int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
                if (r1 <= 0) goto L_0x004c
                r8.onProgressUpdate(r6)     // Catch:{ all -> 0x005b }
            L_0x004c:
                r8.onNewResult(r5, r2)     // Catch:{ all -> 0x005b }
                r7.closeSafely(r5)     // Catch:{ all -> 0x005b }
            L_0x0052:
                monitor-exit(r0)     // Catch:{ all -> 0x005b }
                r7.addCallbacks(r0, r9)
                r8 = 1
                return r8
            L_0x0058:
                r8 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x0058 }
                throw r8     // Catch:{ all -> 0x005b }
            L_0x005b:
                r8 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005b }
                throw r8
            L_0x005e:
                r8 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x005e }
                throw r8
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.MultiplexProducer.Multiplexer.addNewConsumer(com.facebook.imagepipeline.producers.Consumer, com.facebook.imagepipeline.producers.ProducerContext):boolean");
        }

        private void addCallbacks(final Pair<Consumer<T>, ProducerContext> pair, ProducerContext producerContext) {
            producerContext.addCallbacks(new BaseProducerContextCallbacks() {
                public void onCancellationRequested() {
                    boolean remove;
                    List list;
                    List list2;
                    List list3;
                    BaseProducerContext baseProducerContext;
                    synchronized (Multiplexer.this) {
                        remove = Multiplexer.this.mConsumerContextPairs.remove(pair);
                        list = null;
                        if (!remove) {
                            baseProducerContext = null;
                            list3 = null;
                        } else if (Multiplexer.this.mConsumerContextPairs.isEmpty()) {
                            baseProducerContext = Multiplexer.this.mMultiplexProducerContext;
                            list3 = null;
                        } else {
                            List access$400 = Multiplexer.this.updateIsPrefetch();
                            list3 = Multiplexer.this.updatePriority();
                            list2 = Multiplexer.this.updateIsIntermediateResultExpected();
                            List list4 = access$400;
                            baseProducerContext = null;
                            list = list4;
                        }
                        list2 = list3;
                    }
                    BaseProducerContext.callOnIsPrefetchChanged(list);
                    BaseProducerContext.callOnPriorityChanged(list3);
                    BaseProducerContext.callOnIsIntermediateResultExpectedChanged(list2);
                    if (baseProducerContext != null) {
                        baseProducerContext.cancel();
                    }
                    if (remove) {
                        ((Consumer) pair.first).onCancellation();
                    }
                }

                public void onIsPrefetchChanged() {
                    BaseProducerContext.callOnIsPrefetchChanged(Multiplexer.this.updateIsPrefetch());
                }

                public void onIsIntermediateResultExpectedChanged() {
                    BaseProducerContext.callOnIsIntermediateResultExpectedChanged(Multiplexer.this.updateIsIntermediateResultExpected());
                }

                public void onPriorityChanged() {
                    BaseProducerContext.callOnPriorityChanged(Multiplexer.this.updatePriority());
                }
            });
        }

        /* access modifiers changed from: private */
        public void startInputProducerIfHasAttachedConsumers() {
            synchronized (this) {
                boolean z = false;
                Preconditions.checkArgument(this.mMultiplexProducerContext == null);
                if (this.mForwardingConsumer == null) {
                    z = true;
                }
                Preconditions.checkArgument(z);
                if (this.mConsumerContextPairs.isEmpty()) {
                    MultiplexProducer.this.removeMultiplexer(this.mKey, this);
                    return;
                }
                ProducerContext producerContext = (ProducerContext) ((Pair) this.mConsumerContextPairs.iterator().next()).second;
                BaseProducerContext baseProducerContext = new BaseProducerContext(producerContext.getImageRequest(), producerContext.getId(), producerContext.getListener(), producerContext.getCallerContext(), producerContext.getLowestPermittedRequestLevel(), computeIsPrefetch(), computeIsIntermediateResultExpected(), computePriority());
                this.mMultiplexProducerContext = baseProducerContext;
                this.mForwardingConsumer = new ForwardingConsumer();
                BaseProducerContext baseProducerContext2 = this.mMultiplexProducerContext;
                com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer forwardingConsumer = this.mForwardingConsumer;
                MultiplexProducer.this.mInputProducer.produceResults(forwardingConsumer, baseProducerContext2);
            }
        }

        /* access modifiers changed from: private */
        @Nullable
        public synchronized List<ProducerContextCallbacks> updateIsPrefetch() {
            if (this.mMultiplexProducerContext == null) {
                return null;
            }
            return this.mMultiplexProducerContext.setIsPrefetchNoCallbacks(computeIsPrefetch());
        }

        private synchronized boolean computeIsPrefetch() {
            Iterator it = this.mConsumerContextPairs.iterator();
            while (it.hasNext()) {
                if (!((ProducerContext) ((Pair) it.next()).second).isPrefetch()) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: private */
        @Nullable
        public synchronized List<ProducerContextCallbacks> updateIsIntermediateResultExpected() {
            if (this.mMultiplexProducerContext == null) {
                return null;
            }
            return this.mMultiplexProducerContext.setIsIntermediateResultExpectedNoCallbacks(computeIsIntermediateResultExpected());
        }

        private synchronized boolean computeIsIntermediateResultExpected() {
            Iterator it = this.mConsumerContextPairs.iterator();
            while (it.hasNext()) {
                if (((ProducerContext) ((Pair) it.next()).second).isIntermediateResultExpected()) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        @Nullable
        public synchronized List<ProducerContextCallbacks> updatePriority() {
            if (this.mMultiplexProducerContext == null) {
                return null;
            }
            return this.mMultiplexProducerContext.setPriorityNoCallbacks(computePriority());
        }

        private synchronized Priority computePriority() {
            Priority priority;
            priority = Priority.LOW;
            Iterator it = this.mConsumerContextPairs.iterator();
            while (it.hasNext()) {
                priority = Priority.getHigherPriority(priority, ((ProducerContext) ((Pair) it.next()).second).getPriority());
            }
            return priority;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
            r0 = (android.util.Pair) r3.next();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002e, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            ((com.facebook.imagepipeline.producers.Consumer) r0.first).onFailure(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0026, code lost:
            if (r3.hasNext() == false) goto L_0x003b;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onFailure(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer r3, java.lang.Throwable r4) {
            /*
                r2 = this;
                monitor-enter(r2)
                com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer<> r0 = r2.mForwardingConsumer     // Catch:{ all -> 0x003c }
                if (r0 == r3) goto L_0x0007
                monitor-exit(r2)     // Catch:{ all -> 0x003c }
                return
            L_0x0007:
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r3 = r2.mConsumerContextPairs     // Catch:{ all -> 0x003c }
                java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x003c }
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r0 = r2.mConsumerContextPairs     // Catch:{ all -> 0x003c }
                r0.clear()     // Catch:{ all -> 0x003c }
                com.facebook.imagepipeline.producers.MultiplexProducer r0 = com.facebook.imagepipeline.producers.MultiplexProducer.this     // Catch:{ all -> 0x003c }
                K r1 = r2.mKey     // Catch:{ all -> 0x003c }
                r0.removeMultiplexer(r1, r2)     // Catch:{ all -> 0x003c }
                T r0 = r2.mLastIntermediateResult     // Catch:{ all -> 0x003c }
                r2.closeSafely(r0)     // Catch:{ all -> 0x003c }
                r0 = 0
                r2.mLastIntermediateResult = r0     // Catch:{ all -> 0x003c }
                monitor-exit(r2)     // Catch:{ all -> 0x003c }
            L_0x0022:
                boolean r0 = r3.hasNext()
                if (r0 == 0) goto L_0x003b
                java.lang.Object r0 = r3.next()
                android.util.Pair r0 = (android.util.Pair) r0
                monitor-enter(r0)
                java.lang.Object r1 = r0.first     // Catch:{ all -> 0x0038 }
                com.facebook.imagepipeline.producers.Consumer r1 = (com.facebook.imagepipeline.producers.Consumer) r1     // Catch:{ all -> 0x0038 }
                r1.onFailure(r4)     // Catch:{ all -> 0x0038 }
                monitor-exit(r0)     // Catch:{ all -> 0x0038 }
                goto L_0x0022
            L_0x0038:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0038 }
                throw r3
            L_0x003b:
                return
            L_0x003c:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x003c }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.MultiplexProducer.Multiplexer.onFailure(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer$ForwardingConsumer, java.lang.Throwable):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0031, code lost:
            if (r3.hasNext() == false) goto L_0x0046;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0033, code lost:
            r0 = (android.util.Pair) r3.next();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0039, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
            ((com.facebook.imagepipeline.producers.Consumer) r0.first).onNewResult(r4, r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0041, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0046, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onNextResult(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer r3, T r4, boolean r5) {
            /*
                r2 = this;
                monitor-enter(r2)
                com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer<> r0 = r2.mForwardingConsumer     // Catch:{ all -> 0x0047 }
                if (r0 == r3) goto L_0x0007
                monitor-exit(r2)     // Catch:{ all -> 0x0047 }
                return
            L_0x0007:
                T r3 = r2.mLastIntermediateResult     // Catch:{ all -> 0x0047 }
                r2.closeSafely(r3)     // Catch:{ all -> 0x0047 }
                r3 = 0
                r2.mLastIntermediateResult = r3     // Catch:{ all -> 0x0047 }
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r3 = r2.mConsumerContextPairs     // Catch:{ all -> 0x0047 }
                java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0047 }
                if (r5 != 0) goto L_0x0020
                com.facebook.imagepipeline.producers.MultiplexProducer r0 = com.facebook.imagepipeline.producers.MultiplexProducer.this     // Catch:{ all -> 0x0047 }
                java.io.Closeable r0 = r0.cloneOrNull(r4)     // Catch:{ all -> 0x0047 }
                r2.mLastIntermediateResult = r0     // Catch:{ all -> 0x0047 }
                goto L_0x002c
            L_0x0020:
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r0 = r2.mConsumerContextPairs     // Catch:{ all -> 0x0047 }
                r0.clear()     // Catch:{ all -> 0x0047 }
                com.facebook.imagepipeline.producers.MultiplexProducer r0 = com.facebook.imagepipeline.producers.MultiplexProducer.this     // Catch:{ all -> 0x0047 }
                K r1 = r2.mKey     // Catch:{ all -> 0x0047 }
                r0.removeMultiplexer(r1, r2)     // Catch:{ all -> 0x0047 }
            L_0x002c:
                monitor-exit(r2)     // Catch:{ all -> 0x0047 }
            L_0x002d:
                boolean r0 = r3.hasNext()
                if (r0 == 0) goto L_0x0046
                java.lang.Object r0 = r3.next()
                android.util.Pair r0 = (android.util.Pair) r0
                monitor-enter(r0)
                java.lang.Object r1 = r0.first     // Catch:{ all -> 0x0043 }
                com.facebook.imagepipeline.producers.Consumer r1 = (com.facebook.imagepipeline.producers.Consumer) r1     // Catch:{ all -> 0x0043 }
                r1.onNewResult(r4, r5)     // Catch:{ all -> 0x0043 }
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                goto L_0x002d
            L_0x0043:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0043 }
                throw r3
            L_0x0046:
                return
            L_0x0047:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0047 }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.MultiplexProducer.Multiplexer.onNextResult(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer$ForwardingConsumer, java.io.Closeable, boolean):void");
        }

        public void onCancelled(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer forwardingConsumer) {
            synchronized (this) {
                if (this.mForwardingConsumer == forwardingConsumer) {
                    this.mForwardingConsumer = null;
                    this.mMultiplexProducerContext = null;
                    closeSafely(this.mLastIntermediateResult);
                    this.mLastIntermediateResult = null;
                    startInputProducerIfHasAttachedConsumers();
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0016, code lost:
            r0 = (android.util.Pair) r3.next();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
            monitor-enter(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
            ((com.facebook.imagepipeline.producers.Consumer) r0.first).onProgressUpdate(r4);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0024, code lost:
            monitor-exit(r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0029, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
            if (r3.hasNext() == false) goto L_0x0029;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onProgressUpdate(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer r3, float r4) {
            /*
                r2 = this;
                monitor-enter(r2)
                com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer.ForwardingConsumer<> r0 = r2.mForwardingConsumer     // Catch:{ all -> 0x002a }
                if (r0 == r3) goto L_0x0007
                monitor-exit(r2)     // Catch:{ all -> 0x002a }
                return
            L_0x0007:
                r2.mLastProgress = r4     // Catch:{ all -> 0x002a }
                java.util.concurrent.CopyOnWriteArraySet<android.util.Pair<com.facebook.imagepipeline.producers.Consumer<T>, com.facebook.imagepipeline.producers.ProducerContext>> r3 = r2.mConsumerContextPairs     // Catch:{ all -> 0x002a }
                java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x002a }
                monitor-exit(r2)     // Catch:{ all -> 0x002a }
            L_0x0010:
                boolean r0 = r3.hasNext()
                if (r0 == 0) goto L_0x0029
                java.lang.Object r0 = r3.next()
                android.util.Pair r0 = (android.util.Pair) r0
                monitor-enter(r0)
                java.lang.Object r1 = r0.first     // Catch:{ all -> 0x0026 }
                com.facebook.imagepipeline.producers.Consumer r1 = (com.facebook.imagepipeline.producers.Consumer) r1     // Catch:{ all -> 0x0026 }
                r1.onProgressUpdate(r4)     // Catch:{ all -> 0x0026 }
                monitor-exit(r0)     // Catch:{ all -> 0x0026 }
                goto L_0x0010
            L_0x0026:
                r3 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0026 }
                throw r3
            L_0x0029:
                return
            L_0x002a:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x002a }
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.MultiplexProducer.Multiplexer.onProgressUpdate(com.facebook.imagepipeline.producers.MultiplexProducer$Multiplexer$ForwardingConsumer, float):void");
        }

        private void closeSafely(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract T cloneOrNull(T t);

    /* access modifiers changed from: protected */
    public abstract K getKey(ProducerContext producerContext);

    protected MultiplexProducer(Producer<T> producer) {
        this.mInputProducer = producer;
    }

    public void produceResults(Consumer<T> consumer, ProducerContext producerContext) {
        boolean z;
        Multiplexer existingMultiplexer;
        Object key = getKey(producerContext);
        do {
            z = false;
            synchronized (this) {
                existingMultiplexer = getExistingMultiplexer(key);
                if (existingMultiplexer == null) {
                    existingMultiplexer = createAndPutNewMultiplexer(key);
                    z = true;
                }
            }
        } while (!existingMultiplexer.addNewConsumer(consumer, producerContext));
        if (z) {
            existingMultiplexer.startInputProducerIfHasAttachedConsumers();
        }
    }

    /* access modifiers changed from: private */
    public synchronized Multiplexer getExistingMultiplexer(K k) {
        return (Multiplexer) this.mMultiplexers.get(k);
    }

    private synchronized Multiplexer createAndPutNewMultiplexer(K k) {
        Multiplexer multiplexer;
        multiplexer = new Multiplexer<>(k);
        this.mMultiplexers.put(k, multiplexer);
        return multiplexer;
    }

    /* access modifiers changed from: private */
    public synchronized void removeMultiplexer(K k, Multiplexer multiplexer) {
        if (this.mMultiplexers.get(k) == multiplexer) {
            this.mMultiplexers.remove(k);
        }
    }
}
