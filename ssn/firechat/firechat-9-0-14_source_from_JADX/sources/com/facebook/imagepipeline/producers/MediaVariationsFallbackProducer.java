package com.facebook.imagepipeline.producers;

import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.DiskCachePolicy;
import com.facebook.imagepipeline.cache.MediaIdExtractor;
import com.facebook.imagepipeline.cache.MediaVariationsIndex;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import com.facebook.imagepipeline.request.MediaVariations;
import com.facebook.imagepipeline.request.MediaVariations.Builder;
import com.facebook.imagepipeline.request.MediaVariations.Variant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

public class MediaVariationsFallbackProducer implements Producer<EncodedImage> {
    public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
    public static final String EXTRA_CACHED_VALUE_USED_AS_LAST = "cached_value_used_as_last";
    public static final String EXTRA_VARIANTS_COUNT = "variants_count";
    public static final String EXTRA_VARIANTS_SOURCE = "variants_source";
    public static final String PRODUCER_NAME = "MediaVariationsFallbackProducer";
    /* access modifiers changed from: private */
    public final CacheKeyFactory mCacheKeyFactory;
    private final BufferedDiskCache mDefaultBufferedDiskCache;
    /* access modifiers changed from: private */
    public final DiskCachePolicy mDiskCachePolicy;
    private final Producer<EncodedImage> mInputProducer;
    @Nullable
    private MediaIdExtractor mMediaIdExtractor;
    /* access modifiers changed from: private */
    public final MediaVariationsIndex mMediaVariationsIndex;
    private final BufferedDiskCache mSmallImageBufferedDiskCache;

    @VisibleForTesting
    class MediaVariationsConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private final String mMediaId;
        private final ProducerContext mProducerContext;

        public MediaVariationsConsumer(Consumer<EncodedImage> consumer, ProducerContext producerContext, String str) {
            super(consumer);
            this.mProducerContext = producerContext;
            this.mMediaId = str;
        }

        /* access modifiers changed from: protected */
        public void onNewResultImpl(EncodedImage encodedImage, boolean z) {
            if (z && encodedImage != null) {
                storeResultInDatabase(encodedImage);
            }
            getConsumer().onNewResult(encodedImage, z);
        }

        private void storeResultInDatabase(EncodedImage encodedImage) {
            ImageRequest imageRequest = this.mProducerContext.getImageRequest();
            if (imageRequest.isDiskCacheEnabled() && this.mMediaId != null) {
                MediaVariationsFallbackProducer.this.mMediaVariationsIndex.saveCachedVariant(this.mMediaId, MediaVariationsFallbackProducer.this.mDiskCachePolicy.getCacheChoiceForResult(imageRequest, encodedImage), MediaVariationsFallbackProducer.this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, this.mProducerContext.getCallerContext()), encodedImage);
            }
        }
    }

    @VisibleForTesting
    static class VariantComparator implements Comparator<Variant> {
        private final ResizeOptions mResizeOptions;

        VariantComparator(ResizeOptions resizeOptions) {
            this.mResizeOptions = resizeOptions;
        }

        public int compare(Variant variant, Variant variant2) {
            boolean access$300 = MediaVariationsFallbackProducer.isBigEnoughForRequestedSize(variant, this.mResizeOptions);
            boolean access$3002 = MediaVariationsFallbackProducer.isBigEnoughForRequestedSize(variant2, this.mResizeOptions);
            if (access$300 && access$3002) {
                return variant.getWidth() - variant2.getWidth();
            }
            if (access$300) {
                return -1;
            }
            if (access$3002) {
                return 1;
            }
            return variant2.getWidth() - variant.getWidth();
        }
    }

    public MediaVariationsFallbackProducer(BufferedDiskCache bufferedDiskCache, BufferedDiskCache bufferedDiskCache2, CacheKeyFactory cacheKeyFactory, MediaVariationsIndex mediaVariationsIndex, @Nullable MediaIdExtractor mediaIdExtractor, DiskCachePolicy diskCachePolicy, Producer<EncodedImage> producer) {
        this.mDefaultBufferedDiskCache = bufferedDiskCache;
        this.mSmallImageBufferedDiskCache = bufferedDiskCache2;
        this.mCacheKeyFactory = cacheKeyFactory;
        this.mMediaVariationsIndex = mediaVariationsIndex;
        this.mMediaIdExtractor = mediaIdExtractor;
        this.mDiskCachePolicy = diskCachePolicy;
        this.mInputProducer = producer;
    }

    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        String str;
        String str2;
        AtomicBoolean atomicBoolean;
        String str3;
        final ImageRequest imageRequest = producerContext.getImageRequest();
        final ResizeOptions resizeOptions = imageRequest.getResizeOptions();
        MediaVariations mediaVariations = imageRequest.getMediaVariations();
        if (!imageRequest.isDiskCacheEnabled() || resizeOptions == null || resizeOptions.height <= 0 || resizeOptions.width <= 0) {
            startInputProducerWithExistingConsumer(consumer, producerContext);
            return;
        }
        if (mediaVariations != null) {
            str3 = mediaVariations.getMediaId();
            str2 = MediaVariations.SOURCE_INDEX_DB;
        } else if (this.mMediaIdExtractor == null) {
            str2 = null;
            str = null;
            if (mediaVariations == null || str != null) {
                producerContext.getListener().onProducerStart(producerContext.getId(), PRODUCER_NAME);
                boolean z = false;
                atomicBoolean = new AtomicBoolean(false);
                if (mediaVariations != null || mediaVariations.getVariantsCount() <= 0) {
                    Builder newBuilderForMediaId = MediaVariations.newBuilderForMediaId(str);
                    if (mediaVariations != null && mediaVariations.shouldForceRequestForSpecifiedUri()) {
                        z = true;
                    }
                    Task cachedVariants = this.mMediaVariationsIndex.getCachedVariants(str, newBuilderForMediaId.setForceRequestForSpecifiedUri(z).setSource(str2));
                    final Consumer<EncodedImage> consumer2 = consumer;
                    final ProducerContext producerContext2 = producerContext;
                    final String str4 = str;
                    final AtomicBoolean atomicBoolean2 = atomicBoolean;
                    C06971 r0 = new Continuation<MediaVariations, Object>() {
                        public Object then(Task<MediaVariations> task) throws Exception {
                            if (task.isCancelled() || task.isFaulted()) {
                                return task;
                            }
                            try {
                                if (task.getResult() != null) {
                                    return MediaVariationsFallbackProducer.this.chooseFromVariants(consumer2, producerContext2, imageRequest, (MediaVariations) task.getResult(), resizeOptions, atomicBoolean2);
                                }
                                MediaVariationsFallbackProducer.this.startInputProducerWithWrappedConsumer(consumer2, producerContext2, str4);
                                return null;
                            } catch (Exception unused) {
                                return null;
                            }
                        }
                    };
                    cachedVariants.continueWith(r0);
                } else {
                    chooseFromVariants(consumer, producerContext, imageRequest, mediaVariations, resizeOptions, atomicBoolean);
                }
                subscribeTaskForRequestCancellation(atomicBoolean, producerContext);
            }
            startInputProducerWithExistingConsumer(consumer, producerContext);
            return;
        } else {
            str3 = this.mMediaIdExtractor.getMediaIdFrom(imageRequest.getSourceUri());
            str2 = MediaVariations.SOURCE_ID_EXTRACTOR;
        }
        str = str3;
        if (mediaVariations == null) {
        }
        producerContext.getListener().onProducerStart(producerContext.getId(), PRODUCER_NAME);
        boolean z2 = false;
        atomicBoolean = new AtomicBoolean(false);
        if (mediaVariations != null) {
        }
        Builder newBuilderForMediaId2 = MediaVariations.newBuilderForMediaId(str);
        z2 = true;
        Task cachedVariants2 = this.mMediaVariationsIndex.getCachedVariants(str, newBuilderForMediaId2.setForceRequestForSpecifiedUri(z2).setSource(str2));
        final Consumer<EncodedImage> consumer22 = consumer;
        final ProducerContext producerContext22 = producerContext;
        final String str42 = str;
        final AtomicBoolean atomicBoolean22 = atomicBoolean;
        C06971 r02 = new Continuation<MediaVariations, Object>() {
            public Object then(Task<MediaVariations> task) throws Exception {
                if (task.isCancelled() || task.isFaulted()) {
                    return task;
                }
                try {
                    if (task.getResult() != null) {
                        return MediaVariationsFallbackProducer.this.chooseFromVariants(consumer22, producerContext22, imageRequest, (MediaVariations) task.getResult(), resizeOptions, atomicBoolean22);
                    }
                    MediaVariationsFallbackProducer.this.startInputProducerWithWrappedConsumer(consumer22, producerContext22, str42);
                    return null;
                } catch (Exception unused) {
                    return null;
                }
            }
        };
        cachedVariants2.continueWith(r02);
        subscribeTaskForRequestCancellation(atomicBoolean, producerContext);
    }

    /* access modifiers changed from: private */
    public Task chooseFromVariants(Consumer<EncodedImage> consumer, ProducerContext producerContext, ImageRequest imageRequest, MediaVariations mediaVariations, ResizeOptions resizeOptions, AtomicBoolean atomicBoolean) {
        if (mediaVariations.getVariantsCount() == 0) {
            return Task.forResult(null).continueWith(onFinishDiskReads(consumer, producerContext, imageRequest, mediaVariations, Collections.emptyList(), 0, atomicBoolean));
        }
        return attemptCacheReadForVariant(consumer, producerContext, imageRequest, mediaVariations, mediaVariations.getSortedVariants(new VariantComparator(resizeOptions)), 0, atomicBoolean);
    }

    /* access modifiers changed from: private */
    public Task attemptCacheReadForVariant(Consumer<EncodedImage> consumer, ProducerContext producerContext, ImageRequest imageRequest, MediaVariations mediaVariations, List<Variant> list, int i, AtomicBoolean atomicBoolean) {
        CacheChoice cacheChoice;
        Variant variant = (Variant) list.get(i);
        CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, variant.getUri(), producerContext.getCallerContext());
        if (variant.getCacheChoice() == null) {
            cacheChoice = imageRequest.getCacheChoice();
        } else {
            cacheChoice = variant.getCacheChoice();
        }
        return (cacheChoice == CacheChoice.SMALL ? this.mSmallImageBufferedDiskCache : this.mDefaultBufferedDiskCache).get(encodedCacheKey, atomicBoolean).continueWith(onFinishDiskReads(consumer, producerContext, imageRequest, mediaVariations, list, i, atomicBoolean));
    }

    /* access modifiers changed from: private */
    public static boolean isBigEnoughForRequestedSize(Variant variant, ResizeOptions resizeOptions) {
        return variant.getWidth() >= resizeOptions.width && variant.getHeight() >= resizeOptions.height;
    }

    private Continuation<EncodedImage, Void> onFinishDiskReads(Consumer<EncodedImage> consumer, ProducerContext producerContext, ImageRequest imageRequest, MediaVariations mediaVariations, List<Variant> list, int i, AtomicBoolean atomicBoolean) {
        final String id = producerContext.getId();
        final ProducerListener listener = producerContext.getListener();
        final Consumer<EncodedImage> consumer2 = consumer;
        final ProducerContext producerContext2 = producerContext;
        final MediaVariations mediaVariations2 = mediaVariations;
        final List<Variant> list2 = list;
        final int i2 = i;
        final ImageRequest imageRequest2 = imageRequest;
        final AtomicBoolean atomicBoolean2 = atomicBoolean;
        C06982 r0 = new Continuation<EncodedImage, Void>() {
            public Void then(Task<EncodedImage> task) throws Exception {
                boolean z = false;
                if (MediaVariationsFallbackProducer.isTaskCancelled(task)) {
                    listener.onProducerFinishWithCancellation(id, MediaVariationsFallbackProducer.PRODUCER_NAME, null);
                    consumer2.onCancellation();
                } else {
                    if (task.isFaulted()) {
                        listener.onProducerFinishWithFailure(id, MediaVariationsFallbackProducer.PRODUCER_NAME, task.getError(), null);
                        MediaVariationsFallbackProducer.this.startInputProducerWithWrappedConsumer(consumer2, producerContext2, mediaVariations2.getMediaId());
                    } else {
                        EncodedImage encodedImage = (EncodedImage) task.getResult();
                        if (encodedImage != null) {
                            if (!mediaVariations2.shouldForceRequestForSpecifiedUri() && MediaVariationsFallbackProducer.isBigEnoughForRequestedSize((Variant) list2.get(i2), imageRequest2.getResizeOptions())) {
                                z = true;
                            }
                            listener.onProducerFinishWithSuccess(id, MediaVariationsFallbackProducer.PRODUCER_NAME, MediaVariationsFallbackProducer.getExtraMap(listener, id, true, list2.size(), mediaVariations2.getSource(), z));
                            if (z) {
                                listener.onUltimateProducerReached(id, MediaVariationsFallbackProducer.PRODUCER_NAME, true);
                                consumer2.onProgressUpdate(1.0f);
                            }
                            consumer2.onNewResult(encodedImage, z);
                            encodedImage.close();
                            z = !z;
                        } else if (i2 < list2.size() - 1) {
                            MediaVariationsFallbackProducer.this.attemptCacheReadForVariant(consumer2, producerContext2, imageRequest2, mediaVariations2, list2, i2 + 1, atomicBoolean2);
                        } else {
                            listener.onProducerFinishWithSuccess(id, MediaVariationsFallbackProducer.PRODUCER_NAME, MediaVariationsFallbackProducer.getExtraMap(listener, id, false, list2.size(), mediaVariations2.getSource(), false));
                        }
                    }
                    z = true;
                }
                if (z) {
                    MediaVariationsFallbackProducer.this.startInputProducerWithWrappedConsumer(consumer2, producerContext2, mediaVariations2.getMediaId());
                }
                return null;
            }
        };
        return r0;
    }

    private void startInputProducerWithExistingConsumer(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        this.mInputProducer.produceResults(consumer, producerContext);
    }

    /* access modifiers changed from: private */
    public void startInputProducerWithWrappedConsumer(Consumer<EncodedImage> consumer, ProducerContext producerContext, String str) {
        this.mInputProducer.produceResults(new MediaVariationsConsumer(consumer, producerContext, str), producerContext);
    }

    /* access modifiers changed from: private */
    public static boolean isTaskCancelled(Task<?> task) {
        return task.isCancelled() || (task.isFaulted() && (task.getError() instanceof CancellationException));
    }

    @VisibleForTesting
    static Map<String, String> getExtraMap(ProducerListener producerListener, String str, boolean z, int i, String str2, boolean z2) {
        if (!producerListener.requiresExtraMap(str)) {
            return null;
        }
        if (z) {
            return ImmutableMap.m40of("cached_value_found", String.valueOf(true), EXTRA_CACHED_VALUE_USED_AS_LAST, String.valueOf(z2), EXTRA_VARIANTS_COUNT, String.valueOf(i), EXTRA_VARIANTS_SOURCE, str2);
        }
        return ImmutableMap.m39of("cached_value_found", String.valueOf(false), EXTRA_VARIANTS_COUNT, String.valueOf(i), EXTRA_VARIANTS_SOURCE, str2);
    }

    private void subscribeTaskForRequestCancellation(final AtomicBoolean atomicBoolean, ProducerContext producerContext) {
        producerContext.addCallbacks(new BaseProducerContextCallbacks() {
            public void onCancellationRequested() {
                atomicBoolean.set(true);
            }
        });
    }
}
