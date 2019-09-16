package com.facebook.imagepipeline.cache;

import bolts.Task;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import java.util.concurrent.atomic.AtomicBoolean;

public interface DiskCachePolicy {
    Task<EncodedImage> createAndStartCacheReadTask(ImageRequest imageRequest, Object obj, AtomicBoolean atomicBoolean);

    CacheChoice getCacheChoiceForResult(ImageRequest imageRequest, EncodedImage encodedImage);

    void writeToCache(EncodedImage encodedImage, ImageRequest imageRequest, Object obj);
}
