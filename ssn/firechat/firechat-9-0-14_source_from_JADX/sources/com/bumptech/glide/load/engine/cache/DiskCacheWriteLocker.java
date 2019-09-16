package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.util.Preconditions;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class DiskCacheWriteLocker {
    private final Map<String, WriteLock> locks = new HashMap();
    private final WriteLockPool writeLockPool = new WriteLockPool();

    private static class WriteLock {
        int interestedThreads;
        final Lock lock = new ReentrantLock();

        WriteLock() {
        }
    }

    private static class WriteLockPool {
        private static final int MAX_POOL_SIZE = 10;
        private final Queue<WriteLock> pool = new ArrayDeque();

        WriteLockPool() {
        }

        /* access modifiers changed from: 0000 */
        public WriteLock obtain() {
            WriteLock writeLock;
            synchronized (this.pool) {
                writeLock = (WriteLock) this.pool.poll();
            }
            return writeLock == null ? new WriteLock() : writeLock;
        }

        /* access modifiers changed from: 0000 */
        public void offer(WriteLock writeLock) {
            synchronized (this.pool) {
                if (this.pool.size() < 10) {
                    this.pool.offer(writeLock);
                }
            }
        }
    }

    DiskCacheWriteLocker() {
    }

    /* access modifiers changed from: 0000 */
    public void acquire(String str) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) this.locks.get(str);
            if (writeLock == null) {
                writeLock = this.writeLockPool.obtain();
                this.locks.put(str, writeLock);
            }
            writeLock.interestedThreads++;
        }
        writeLock.lock.lock();
    }

    /* access modifiers changed from: 0000 */
    public void release(String str) {
        WriteLock writeLock;
        synchronized (this) {
            writeLock = (WriteLock) Preconditions.checkNotNull(this.locks.get(str));
            if (writeLock.interestedThreads < 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot release a lock that is not held, safeKey: ");
                sb.append(str);
                sb.append(", interestedThreads: ");
                sb.append(writeLock.interestedThreads);
                throw new IllegalStateException(sb.toString());
            }
            writeLock.interestedThreads--;
            if (writeLock.interestedThreads == 0) {
                WriteLock writeLock2 = (WriteLock) this.locks.remove(str);
                if (!writeLock2.equals(writeLock)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Removed the wrong lock, expected to remove: ");
                    sb2.append(writeLock);
                    sb2.append(", but actually removed: ");
                    sb2.append(writeLock2);
                    sb2.append(", safeKey: ");
                    sb2.append(str);
                    throw new IllegalStateException(sb2.toString());
                }
                this.writeLockPool.offer(writeLock2);
            }
        }
        writeLock.lock.unlock();
    }
}
