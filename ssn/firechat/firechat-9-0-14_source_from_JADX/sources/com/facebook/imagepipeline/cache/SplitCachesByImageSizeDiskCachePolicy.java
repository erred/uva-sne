package com.facebook.imagepipeline.cache;

import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplitCachesByImageSizeDiskCachePolicy implements DiskCachePolicy {
    private final CacheKeyFactory mCacheKeyFactory;
    private final BufferedDiskCache mDefaultBufferedDiskCache;
    private final int mForceSmallCacheThresholdBytes;
    private final BufferedDiskCache mSmallImageBufferedDiskCache;

    public SplitCachesByImageSizeDiskCachePolicy(BufferedDiskCache bufferedDiskCache, BufferedDiskCache bufferedDiskCache2, CacheKeyFactory cacheKeyFactory, int i) {
        this.mDefaultBufferedDiskCache = bufferedDiskCache;
        this.mSmallImageBufferedDiskCache = bufferedDiskCache2;
        this.mCacheKeyFactory = cacheKeyFactory;
        this.mForceSmallCacheThresholdBytes = i;
    }

    public Task<EncodedImage> createAndStartCacheReadTask(ImageRequest imageRequest, Object obj, final AtomicBoolean atomicBoolean) {
        BufferedDiskCache bufferedDiskCache;
        final BufferedDiskCache bufferedDiskCache2;
        final CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, obj);
        boolean containsSync = this.mSmallImageBufferedDiskCache.containsSync(encodedCacheKey);
        boolean containsSync2 = this.mDefaultBufferedDiskCache.containsSync(encodedCacheKey);
        if (containsSync || !containsSync2) {
            bufferedDiskCache = this.mSmallImageBufferedDiskCache;
            bufferedDiskCache2 = this.mDefaultBufferedDiskCache;
        } else {
            bufferedDiskCache = this.mDefaultBufferedDiskCache;
            bufferedDiskCache2 = this.mSmallImageBufferedDiskCache;
        }
        return bufferedDiskCache.get(encodedCacheKey, atomicBoolean).continueWithTask(new Continuation<EncodedImage, Task<EncodedImage>>() {
            public Task<EncodedImage> then(Task<EncodedImage> task) throws Exception {
                return (SplitCachesByImageSizeDiskCachePolicy.isTaskCancelled(task) || (!task.isFaulted() && task.getResult() != null)) ? task : bufferedDiskCache2.get(encodedCacheKey, atomicBoolean);
            }
        });
    }

    public void writeToCache(EncodedImage encodedImage, ImageRequest imageRequest, Object obj) {
        CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, obj);
        switch (getCacheChoiceForResult(imageRequest, encodedImage)) {
            case DEFAULT:
                this.mDefaultBufferedDiskCache.put(encodedCacheKey, encodedImage);
                return;
            case SMALL:
                this.mSmallImageBufferedDiskCache.put(encodedCacheKey, encodedImage);
                return;
            default:
                return;
        }
    }

    public CacheChoice getCacheChoiceForResult(ImageRequest imageRequest, EncodedImage encodedImage) {
        int size = encodedImage.getSize();
        if (size < 0 || size >= this.mForceSmallCacheThresholdBytes) {
            return CacheChoice.DEFAULT;
        }
        return CacheChoice.SMALL;
    }

    /* access modifiers changed from: private */
    public static boolean isTaskCancelled(Task<?> task) {
        return task.isCancelled() || (task.isFaulted() && (task.getError() instanceof CancellationException));
    }
}
