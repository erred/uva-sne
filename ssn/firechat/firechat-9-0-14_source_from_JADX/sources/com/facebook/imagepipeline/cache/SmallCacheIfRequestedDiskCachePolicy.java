package com.facebook.imagepipeline.cache;

import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import java.util.concurrent.atomic.AtomicBoolean;

public class SmallCacheIfRequestedDiskCachePolicy implements DiskCachePolicy {
    private final CacheKeyFactory mCacheKeyFactory;
    private final BufferedDiskCache mDefaultBufferedDiskCache;
    private final BufferedDiskCache mSmallImageBufferedDiskCache;

    public SmallCacheIfRequestedDiskCachePolicy(BufferedDiskCache bufferedDiskCache, BufferedDiskCache bufferedDiskCache2, CacheKeyFactory cacheKeyFactory) {
        this.mDefaultBufferedDiskCache = bufferedDiskCache;
        this.mSmallImageBufferedDiskCache = bufferedDiskCache2;
        this.mCacheKeyFactory = cacheKeyFactory;
    }

    public Task<EncodedImage> createAndStartCacheReadTask(ImageRequest imageRequest, Object obj, AtomicBoolean atomicBoolean) {
        CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, obj);
        if (imageRequest.getCacheChoice() == CacheChoice.SMALL) {
            return this.mSmallImageBufferedDiskCache.get(encodedCacheKey, atomicBoolean);
        }
        return this.mDefaultBufferedDiskCache.get(encodedCacheKey, atomicBoolean);
    }

    public void writeToCache(EncodedImage encodedImage, ImageRequest imageRequest, Object obj) {
        CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, obj);
        if (getCacheChoiceForResult(imageRequest, encodedImage) == CacheChoice.SMALL) {
            this.mSmallImageBufferedDiskCache.put(encodedCacheKey, encodedImage);
        } else {
            this.mDefaultBufferedDiskCache.put(encodedCacheKey, encodedImage);
        }
    }

    public CacheChoice getCacheChoiceForResult(ImageRequest imageRequest, EncodedImage encodedImage) {
        if (imageRequest.getCacheChoice() == null) {
            return CacheChoice.DEFAULT;
        }
        return imageRequest.getCacheChoice();
    }
}
