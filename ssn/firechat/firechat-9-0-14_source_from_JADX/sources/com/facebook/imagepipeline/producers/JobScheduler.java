package com.facebook.imagepipeline.producers;

import android.os.SystemClock;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.image.EncodedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;

public class JobScheduler {
    static final String QUEUE_TIME_KEY = "queueTime";
    private final Runnable mDoJobRunnable = new Runnable() {
        public void run() {
            JobScheduler.this.doJob();
        }
    };
    @GuardedBy("this")
    @VisibleForTesting
    EncodedImage mEncodedImage = null;
    private final Executor mExecutor;
    @GuardedBy("this")
    @VisibleForTesting
    boolean mIsLast = false;
    private final JobRunnable mJobRunnable;
    @GuardedBy("this")
    @VisibleForTesting
    long mJobStartTime = 0;
    @GuardedBy("this")
    @VisibleForTesting
    JobState mJobState = JobState.IDLE;
    @GuardedBy("this")
    @VisibleForTesting
    long mJobSubmitTime = 0;
    private final int mMinimumJobIntervalMs;
    private final Runnable mSubmitJobRunnable = new Runnable() {
        public void run() {
            JobScheduler.this.submitJob();
        }
    };

    public interface JobRunnable {
        void run(EncodedImage encodedImage, boolean z);
    }

    @VisibleForTesting
    static class JobStartExecutorSupplier {
        private static ScheduledExecutorService sJobStarterExecutor;

        JobStartExecutorSupplier() {
        }

        static ScheduledExecutorService get() {
            if (sJobStarterExecutor == null) {
                sJobStarterExecutor = Executors.newSingleThreadScheduledExecutor();
            }
            return sJobStarterExecutor;
        }
    }

    @VisibleForTesting
    enum JobState {
        IDLE,
        QUEUED,
        RUNNING,
        RUNNING_AND_PENDING
    }

    public JobScheduler(Executor executor, JobRunnable jobRunnable, int i) {
        this.mExecutor = executor;
        this.mJobRunnable = jobRunnable;
        this.mMinimumJobIntervalMs = i;
    }

    public void clearJob() {
        EncodedImage encodedImage;
        synchronized (this) {
            encodedImage = this.mEncodedImage;
            this.mEncodedImage = null;
            this.mIsLast = false;
        }
        EncodedImage.closeSafely(encodedImage);
    }

    public boolean updateJob(EncodedImage encodedImage, boolean z) {
        EncodedImage encodedImage2;
        if (!shouldProcess(encodedImage, z)) {
            return false;
        }
        synchronized (this) {
            encodedImage2 = this.mEncodedImage;
            this.mEncodedImage = EncodedImage.cloneOrNull(encodedImage);
            this.mIsLast = z;
        }
        EncodedImage.closeSafely(encodedImage2);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003d, code lost:
        if (r3 == false) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003f, code lost:
        enqueueJob(r5 - r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean scheduleJob() {
        /*
            r9 = this;
            long r0 = android.os.SystemClock.uptimeMillis()
            monitor-enter(r9)
            com.facebook.imagepipeline.image.EncodedImage r2 = r9.mEncodedImage     // Catch:{ all -> 0x0045 }
            boolean r3 = r9.mIsLast     // Catch:{ all -> 0x0045 }
            boolean r2 = shouldProcess(r2, r3)     // Catch:{ all -> 0x0045 }
            r3 = 0
            if (r2 != 0) goto L_0x0012
            monitor-exit(r9)     // Catch:{ all -> 0x0045 }
            return r3
        L_0x0012:
            int[] r2 = com.facebook.imagepipeline.producers.JobScheduler.C06903.f73xca5c4655     // Catch:{ all -> 0x0045 }
            com.facebook.imagepipeline.producers.JobScheduler$JobState r4 = r9.mJobState     // Catch:{ all -> 0x0045 }
            int r4 = r4.ordinal()     // Catch:{ all -> 0x0045 }
            r2 = r2[r4]     // Catch:{ all -> 0x0045 }
            r4 = 1
            switch(r2) {
                case 1: goto L_0x0026;
                case 2: goto L_0x003a;
                case 3: goto L_0x0021;
                default: goto L_0x0020;
            }     // Catch:{ all -> 0x0045 }
        L_0x0020:
            goto L_0x003a
        L_0x0021:
            com.facebook.imagepipeline.producers.JobScheduler$JobState r2 = com.facebook.imagepipeline.producers.JobScheduler.JobState.RUNNING_AND_PENDING     // Catch:{ all -> 0x0045 }
            r9.mJobState = r2     // Catch:{ all -> 0x0045 }
            goto L_0x003a
        L_0x0026:
            long r2 = r9.mJobStartTime     // Catch:{ all -> 0x0045 }
            int r5 = r9.mMinimumJobIntervalMs     // Catch:{ all -> 0x0045 }
            long r5 = (long) r5     // Catch:{ all -> 0x0045 }
            long r7 = r2 + r5
            long r2 = java.lang.Math.max(r7, r0)     // Catch:{ all -> 0x0045 }
            r9.mJobSubmitTime = r0     // Catch:{ all -> 0x0045 }
            com.facebook.imagepipeline.producers.JobScheduler$JobState r5 = com.facebook.imagepipeline.producers.JobScheduler.JobState.QUEUED     // Catch:{ all -> 0x0045 }
            r9.mJobState = r5     // Catch:{ all -> 0x0045 }
            r5 = r2
            r3 = 1
            goto L_0x003c
        L_0x003a:
            r5 = 0
        L_0x003c:
            monitor-exit(r9)     // Catch:{ all -> 0x0045 }
            if (r3 == 0) goto L_0x0044
            long r2 = r5 - r0
            r9.enqueueJob(r2)
        L_0x0044:
            return r4
        L_0x0045:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x0045 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.JobScheduler.scheduleJob():boolean");
    }

    private void enqueueJob(long j) {
        if (j > 0) {
            JobStartExecutorSupplier.get().schedule(this.mSubmitJobRunnable, j, TimeUnit.MILLISECONDS);
        } else {
            this.mSubmitJobRunnable.run();
        }
    }

    /* access modifiers changed from: private */
    public void submitJob() {
        this.mExecutor.execute(this.mDoJobRunnable);
    }

    /* access modifiers changed from: private */
    public void doJob() {
        EncodedImage encodedImage;
        boolean z;
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this) {
            encodedImage = this.mEncodedImage;
            z = this.mIsLast;
            this.mEncodedImage = null;
            this.mIsLast = false;
            this.mJobState = JobState.RUNNING;
            this.mJobStartTime = uptimeMillis;
        }
        try {
            if (shouldProcess(encodedImage, z)) {
                this.mJobRunnable.run(encodedImage, z);
            }
        } finally {
            EncodedImage.closeSafely(encodedImage);
            onJobFinished();
        }
    }

    private void onJobFinished() {
        boolean z;
        long j;
        long uptimeMillis = SystemClock.uptimeMillis();
        synchronized (this) {
            if (this.mJobState == JobState.RUNNING_AND_PENDING) {
                j = Math.max(this.mJobStartTime + ((long) this.mMinimumJobIntervalMs), uptimeMillis);
                z = true;
                this.mJobSubmitTime = uptimeMillis;
                this.mJobState = JobState.QUEUED;
            } else {
                this.mJobState = JobState.IDLE;
                j = 0;
                z = false;
            }
        }
        if (z) {
            enqueueJob(j - uptimeMillis);
        }
    }

    private static boolean shouldProcess(EncodedImage encodedImage, boolean z) {
        return z || EncodedImage.isValid(encodedImage);
    }

    public synchronized long getQueuedTime() {
        return this.mJobStartTime - this.mJobSubmitTime;
    }
}
