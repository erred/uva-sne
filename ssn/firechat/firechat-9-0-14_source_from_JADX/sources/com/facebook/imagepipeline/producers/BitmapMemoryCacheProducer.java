package com.facebook.imagepipeline.producers;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest.RequestLevel;
import java.util.Map;

public class BitmapMemoryCacheProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
    public static final String PRODUCER_NAME = "BitmapMemoryCacheProducer";
    private final CacheKeyFactory mCacheKeyFactory;
    private final Producer<CloseableReference<CloseableImage>> mInputProducer;
    /* access modifiers changed from: private */
    public final MemoryCache<CacheKey, CloseableImage> mMemoryCache;

    /* access modifiers changed from: protected */
    public String getProducerName() {
        return PRODUCER_NAME;
    }

    public BitmapMemoryCacheProducer(MemoryCache<CacheKey, CloseableImage> memoryCache, CacheKeyFactory cacheKeyFactory, Producer<CloseableReference<CloseableImage>> producer) {
        this.mMemoryCache = memoryCache;
        this.mCacheKeyFactory = cacheKeyFactory;
        this.mInputProducer = producer;
    }

    public void produceResults(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext) {
        ProducerListener listener = producerContext.getListener();
        String id = producerContext.getId();
        listener.onProducerStart(id, getProducerName());
        CacheKey bitmapCacheKey = this.mCacheKeyFactory.getBitmapCacheKey(producerContext.getImageRequest(), producerContext.getCallerContext());
        CloseableReference closeableReference = this.mMemoryCache.get(bitmapCacheKey);
        Map map = null;
        if (closeableReference != null) {
            boolean isOfFullQuality = ((CloseableImage) closeableReference.get()).getQualityInfo().isOfFullQuality();
            if (isOfFullQuality) {
                listener.onProducerFinishWithSuccess(id, getProducerName(), listener.requiresExtraMap(id) ? ImmutableMap.m37of("cached_value_found", "true") : null);
                listener.onUltimateProducerReached(id, getProducerName(), true);
                consumer.onProgressUpdate(1.0f);
            }
            consumer.onNewResult(closeableReference, isOfFullQuality);
            closeableReference.close();
            if (isOfFullQuality) {
                return;
            }
        }
        if (producerContext.getLowestPermittedRequestLevel().getValue() >= RequestLevel.BITMAP_MEMORY_CACHE.getValue()) {
            listener.onProducerFinishWithSuccess(id, getProducerName(), listener.requiresExtraMap(id) ? ImmutableMap.m37of("cached_value_found", "false") : null);
            listener.onUltimateProducerReached(id, getProducerName(), false);
            consumer.onNewResult(null, true);
            return;
        }
        Consumer wrapConsumer = wrapConsumer(consumer, bitmapCacheKey);
        String producerName = getProducerName();
        if (listener.requiresExtraMap(id)) {
            map = ImmutableMap.m37of("cached_value_found", "false");
        }
        listener.onProducerFinishWithSuccess(id, producerName, map);
        this.mInputProducer.produceResults(wrapConsumer, producerContext);
    }

    /* access modifiers changed from: protected */
    public Consumer<CloseableReference<CloseableImage>> wrapConsumer(Consumer<CloseableReference<CloseableImage>> consumer, final CacheKey cacheKey) {
        return new DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>>(consumer) {
            public void onNewResultImpl(CloseableReference<CloseableImage> closeableReference, boolean z) {
                if (closeableReference == null) {
                    if (z) {
                        getConsumer().onNewResult(null, true);
                    }
                } else if (((CloseableImage) closeableReference.get()).isStateful()) {
                    getConsumer().onNewResult(closeableReference, z);
                } else {
                    if (!z) {
                        CloseableReference closeableReference2 = BitmapMemoryCacheProducer.this.mMemoryCache.get(cacheKey);
                        if (closeableReference2 != null) {
                            try {
                                QualityInfo qualityInfo = ((CloseableImage) closeableReference.get()).getQualityInfo();
                                QualityInfo qualityInfo2 = ((CloseableImage) closeableReference2.get()).getQualityInfo();
                                if (qualityInfo2.isOfFullQuality() || qualityInfo2.getQuality() >= qualityInfo.getQuality()) {
                                    getConsumer().onNewResult(closeableReference2, false);
                                    CloseableReference.closeSafely(closeableReference2);
                                    return;
                                }
                            } finally {
                                CloseableReference.closeSafely(closeableReference2);
                            }
                        }
                    }
                    CloseableReference<CloseableImage> cache = BitmapMemoryCacheProducer.this.mMemoryCache.cache(cacheKey, closeableReference);
                    if (z) {
                        try {
                            getConsumer().onProgressUpdate(1.0f);
                        } catch (Throwable th) {
                            CloseableReference.closeSafely(cache);
                            throw th;
                        }
                    }
                    Consumer consumer = getConsumer();
                    if (cache != null) {
                        closeableReference = cache;
                    }
                    consumer.onNewResult(closeableReference, z);
                    CloseableReference.closeSafely(cache);
                }
            }
        };
    }
}
