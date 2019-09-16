package com.facebook.common.references;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public abstract class CloseableReference<T> implements Cloneable, Closeable {
    private static final ResourceReleaser<Closeable> DEFAULT_CLOSEABLE_RELEASER = new ResourceReleaser<Closeable>() {
        public void release(Closeable closeable) {
            try {
                Closeables.close(closeable, true);
            } catch (IOException unused) {
            }
        }
    };
    /* access modifiers changed from: private */
    public static Class<CloseableReference> TAG = CloseableReference.class;
    private static volatile boolean sUseFinalizers = true;

    private static class CloseableReferenceWithFinalizer<T> extends CloseableReference<T> {
        @GuardedBy("this")
        private boolean mIsClosed;
        private final SharedReference<T> mSharedReference;

        private CloseableReferenceWithFinalizer(SharedReference<T> sharedReference) {
            this.mIsClosed = false;
            this.mSharedReference = (SharedReference) Preconditions.checkNotNull(sharedReference);
            sharedReference.addReference();
        }

        private CloseableReferenceWithFinalizer(T t, ResourceReleaser<T> resourceReleaser) {
            this.mIsClosed = false;
            this.mSharedReference = new SharedReference<>(t, resourceReleaser);
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            try {
                synchronized (this) {
                    if (this.mIsClosed) {
                        super.finalize();
                        return;
                    }
                    FLog.m103w(CloseableReference.TAG, "Finalized without closing: %x %x (type = %s)", Integer.valueOf(System.identityHashCode(this)), Integer.valueOf(System.identityHashCode(this.mSharedReference)), this.mSharedReference.get().getClass().getSimpleName());
                    close();
                    super.finalize();
                }
            } catch (Throwable th) {
                super.finalize();
                throw th;
            }
        }

        public synchronized T get() {
            Preconditions.checkState(!this.mIsClosed);
            return this.mSharedReference.get();
        }

        public synchronized CloseableReference<T> clone() {
            Preconditions.checkState(isValid());
            return new CloseableReferenceWithFinalizer(this.mSharedReference);
        }

        public synchronized CloseableReference<T> cloneOrNull() {
            if (!isValid()) {
                return null;
            }
            return clone();
        }

        public synchronized boolean isValid() {
            return !this.mIsClosed;
        }

        public synchronized SharedReference<T> getUnderlyingReferenceTestOnly() {
            return this.mSharedReference;
        }

        public int getValueHash() {
            if (isValid()) {
                return System.identityHashCode(this.mSharedReference.get());
            }
            return 0;
        }

        public void close() {
            synchronized (this) {
                if (!this.mIsClosed) {
                    this.mIsClosed = true;
                    this.mSharedReference.deleteReference();
                }
            }
        }
    }

    private static class CloseableReferenceWithoutFinalizer<T> extends CloseableReference<T> {
        /* access modifiers changed from: private */
        public static final ReferenceQueue<CloseableReference> REF_QUEUE = new ReferenceQueue<>();
        private final Destructor mDestructor;
        /* access modifiers changed from: private */
        public final SharedReference<T> mSharedReference;

        private static class Destructor extends PhantomReference<CloseableReference> {
            @GuardedBy("Destructor.class")
            private static Destructor sHead;
            @GuardedBy("this")
            private boolean destroyed;
            private final SharedReference mSharedReference;
            @GuardedBy("Destructor.class")
            private Destructor next;
            @GuardedBy("Destructor.class")
            private Destructor previous;

            public Destructor(CloseableReferenceWithoutFinalizer closeableReferenceWithoutFinalizer, ReferenceQueue<? super CloseableReference> referenceQueue) {
                super(closeableReferenceWithoutFinalizer, referenceQueue);
                this.mSharedReference = closeableReferenceWithoutFinalizer.mSharedReference;
                synchronized (Destructor.class) {
                    if (sHead != null) {
                        sHead.next = this;
                        this.previous = sHead;
                    }
                    sHead = this;
                }
            }

            public synchronized boolean isDestroyed() {
                return this.destroyed;
            }

            /* JADX WARNING: Code restructure failed: missing block: B:12:0x0010, code lost:
                if (r5.previous == null) goto L_0x0018;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:13:0x0012, code lost:
                r5.previous.next = r5.next;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:15:0x001a, code lost:
                if (r5.next == null) goto L_0x0023;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:16:0x001c, code lost:
                r5.next.previous = r5.previous;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:17:0x0023, code lost:
                sHead = r5.previous;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:18:0x0027, code lost:
                monitor-exit(r1);
             */
            /* JADX WARNING: Code restructure failed: missing block: B:19:0x0028, code lost:
                if (r6 != false) goto L_0x005e;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:20:0x002a, code lost:
                com.facebook.common.logging.FLog.m103w(com.facebook.common.references.CloseableReference.access$300(), "GCed without closing: %x %x (type = %s)", java.lang.Integer.valueOf(java.lang.System.identityHashCode(r5)), java.lang.Integer.valueOf(java.lang.System.identityHashCode(r5.mSharedReference)), r5.mSharedReference.get().getClass().getSimpleName());
             */
            /* JADX WARNING: Code restructure failed: missing block: B:21:0x005e, code lost:
                r5.mSharedReference.deleteReference();
             */
            /* JADX WARNING: Code restructure failed: missing block: B:22:0x0063, code lost:
                return;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
                r1 = com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.Destructor.class;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
                monitor-enter(r1);
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void destroy(boolean r6) {
                /*
                    r5 = this;
                    monitor-enter(r5)
                    boolean r0 = r5.destroyed     // Catch:{ all -> 0x0067 }
                    if (r0 == 0) goto L_0x0007
                    monitor-exit(r5)     // Catch:{ all -> 0x0067 }
                    return
                L_0x0007:
                    r0 = 1
                    r5.destroyed = r0     // Catch:{ all -> 0x0067 }
                    monitor-exit(r5)     // Catch:{ all -> 0x0067 }
                    java.lang.Class<com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor> r1 = com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.Destructor.class
                    monitor-enter(r1)
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r2 = r5.previous     // Catch:{ all -> 0x0064 }
                    if (r2 == 0) goto L_0x0018
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r2 = r5.previous     // Catch:{ all -> 0x0064 }
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r3 = r5.next     // Catch:{ all -> 0x0064 }
                    r2.next = r3     // Catch:{ all -> 0x0064 }
                L_0x0018:
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r2 = r5.next     // Catch:{ all -> 0x0064 }
                    if (r2 == 0) goto L_0x0023
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r2 = r5.next     // Catch:{ all -> 0x0064 }
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r3 = r5.previous     // Catch:{ all -> 0x0064 }
                    r2.previous = r3     // Catch:{ all -> 0x0064 }
                    goto L_0x0027
                L_0x0023:
                    com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r2 = r5.previous     // Catch:{ all -> 0x0064 }
                    sHead = r2     // Catch:{ all -> 0x0064 }
                L_0x0027:
                    monitor-exit(r1)     // Catch:{ all -> 0x0064 }
                    if (r6 != 0) goto L_0x005e
                    java.lang.Class r6 = com.facebook.common.references.CloseableReference.TAG
                    java.lang.String r1 = "GCed without closing: %x %x (type = %s)"
                    r2 = 3
                    java.lang.Object[] r2 = new java.lang.Object[r2]
                    r3 = 0
                    int r4 = java.lang.System.identityHashCode(r5)
                    java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                    r2[r3] = r4
                    com.facebook.common.references.SharedReference r3 = r5.mSharedReference
                    int r3 = java.lang.System.identityHashCode(r3)
                    java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                    r2[r0] = r3
                    r0 = 2
                    com.facebook.common.references.SharedReference r3 = r5.mSharedReference
                    java.lang.Object r3 = r3.get()
                    java.lang.Class r3 = r3.getClass()
                    java.lang.String r3 = r3.getSimpleName()
                    r2[r0] = r3
                    com.facebook.common.logging.FLog.m103w(r6, r1, r2)
                L_0x005e:
                    com.facebook.common.references.SharedReference r6 = r5.mSharedReference
                    r6.deleteReference()
                    return
                L_0x0064:
                    r6 = move-exception
                    monitor-exit(r1)     // Catch:{ all -> 0x0064 }
                    throw r6
                L_0x0067:
                    r6 = move-exception
                    monitor-exit(r5)     // Catch:{ all -> 0x0067 }
                    throw r6
                */
                throw new UnsupportedOperationException("Method not decompiled: com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.Destructor.destroy(boolean):void");
            }
        }

        static {
            new Thread(new Runnable() {
                /* JADX WARNING: Can't wrap try/catch for region: R(2:0|1) */
                /* JADX WARNING: Missing exception handler attribute for start block: B:0:0x0000 */
                /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:1:?, LOOP_START, SYNTHETIC, Splitter:B:0:0x0000] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r2 = this;
                    L_0x0000:
                        java.lang.ref.ReferenceQueue r0 = com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.REF_QUEUE     // Catch:{ InterruptedException -> 0x0000 }
                        java.lang.ref.Reference r0 = r0.remove()     // Catch:{ InterruptedException -> 0x0000 }
                        com.facebook.common.references.CloseableReference$CloseableReferenceWithoutFinalizer$Destructor r0 = (com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.Destructor) r0     // Catch:{ InterruptedException -> 0x0000 }
                        r1 = 0
                        r0.destroy(r1)     // Catch:{ InterruptedException -> 0x0000 }
                        goto L_0x0000
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.facebook.common.references.CloseableReference.CloseableReferenceWithoutFinalizer.C06091.run():void");
                }
            }, "CloseableReferenceDestructorThread").start();
        }

        private CloseableReferenceWithoutFinalizer(SharedReference<T> sharedReference) {
            this.mSharedReference = (SharedReference) Preconditions.checkNotNull(sharedReference);
            sharedReference.addReference();
            this.mDestructor = new Destructor(this, REF_QUEUE);
        }

        private CloseableReferenceWithoutFinalizer(T t, ResourceReleaser<T> resourceReleaser) {
            this.mSharedReference = new SharedReference<>(t, resourceReleaser);
            this.mDestructor = new Destructor(this, REF_QUEUE);
        }

        public void close() {
            this.mDestructor.destroy(true);
        }

        public T get() {
            T t;
            synchronized (this.mDestructor) {
                Preconditions.checkState(!this.mDestructor.isDestroyed());
                t = this.mSharedReference.get();
            }
            return t;
        }

        public CloseableReference<T> clone() {
            CloseableReferenceWithoutFinalizer closeableReferenceWithoutFinalizer;
            synchronized (this.mDestructor) {
                Preconditions.checkState(!this.mDestructor.isDestroyed());
                closeableReferenceWithoutFinalizer = new CloseableReferenceWithoutFinalizer(this.mSharedReference);
            }
            return closeableReferenceWithoutFinalizer;
        }

        public CloseableReference<T> cloneOrNull() {
            synchronized (this.mDestructor) {
                if (this.mDestructor.isDestroyed()) {
                    return null;
                }
                CloseableReferenceWithoutFinalizer closeableReferenceWithoutFinalizer = new CloseableReferenceWithoutFinalizer(this.mSharedReference);
                return closeableReferenceWithoutFinalizer;
            }
        }

        public boolean isValid() {
            return !this.mDestructor.isDestroyed();
        }

        public SharedReference<T> getUnderlyingReferenceTestOnly() {
            return this.mSharedReference;
        }

        public int getValueHash() {
            int identityHashCode;
            synchronized (this.mDestructor) {
                identityHashCode = isValid() ? System.identityHashCode(this.mSharedReference.get()) : 0;
            }
            return identityHashCode;
        }
    }

    public abstract CloseableReference<T> clone();

    public abstract CloseableReference<T> cloneOrNull();

    public abstract void close();

    public abstract T get();

    @VisibleForTesting
    public abstract SharedReference<T> getUnderlyingReferenceTestOnly();

    public abstract int getValueHash();

    public abstract boolean isValid();

    @Nullable
    /* renamed from: of */
    public static <T extends Closeable> CloseableReference<T> m129of(@Nullable T t) {
        if (t == null) {
            return null;
        }
        return makeCloseableReference(t, DEFAULT_CLOSEABLE_RELEASER);
    }

    @Nullable
    /* renamed from: of */
    public static <T> CloseableReference<T> m130of(@Nullable T t, ResourceReleaser<T> resourceReleaser) {
        if (t == null) {
            return null;
        }
        return makeCloseableReference(t, resourceReleaser);
    }

    private static <T> CloseableReference<T> makeCloseableReference(@Nullable T t, ResourceReleaser<T> resourceReleaser) {
        if (sUseFinalizers) {
            return new CloseableReferenceWithFinalizer(t, resourceReleaser);
        }
        return new CloseableReferenceWithoutFinalizer(t, resourceReleaser);
    }

    public static boolean isValid(@Nullable CloseableReference<?> closeableReference) {
        return closeableReference != null && closeableReference.isValid();
    }

    @Nullable
    public static <T> CloseableReference<T> cloneOrNull(@Nullable CloseableReference<T> closeableReference) {
        if (closeableReference != null) {
            return closeableReference.cloneOrNull();
        }
        return null;
    }

    public static <T> List<CloseableReference<T>> cloneOrNull(Collection<CloseableReference<T>> collection) {
        if (collection == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(collection.size());
        for (CloseableReference cloneOrNull : collection) {
            arrayList.add(cloneOrNull(cloneOrNull));
        }
        return arrayList;
    }

    public static void closeSafely(@Nullable CloseableReference<?> closeableReference) {
        if (closeableReference != null) {
            closeableReference.close();
        }
    }

    public static void closeSafely(@Nullable Iterable<? extends CloseableReference<?>> iterable) {
        if (iterable != null) {
            for (CloseableReference closeSafely : iterable) {
                closeSafely(closeSafely);
            }
        }
    }

    public static void setUseFinalizers(boolean z) {
        sUseFinalizers = z;
    }
}
