package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import android.support.p000v4.p002os.EnvironmentCompat;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ExceptionWithNoStacktrace;
import com.facebook.common.util.UriUtil;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegParser;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.producers.JobScheduler.JobRunnable;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class DecodeProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
    public static final String EXTRA_BITMAP_SIZE = "bitmapSize";
    public static final String EXTRA_HAS_GOOD_QUALITY = "hasGoodQuality";
    public static final String EXTRA_IMAGE_FORMAT_NAME = "imageFormat";
    public static final String EXTRA_IS_FINAL = "isFinal";
    public static final String PRODUCER_NAME = "DecodeProducer";
    public static final String REQUESTED_IMAGE_SIZE = "requestedImageSize";
    public static final String SAMPLE_SIZE = "sampleSize";
    private final ByteArrayPool mByteArrayPool;
    private final boolean mDecodeCancellationEnabled;
    /* access modifiers changed from: private */
    public final boolean mDownsampleEnabled;
    /* access modifiers changed from: private */
    public final boolean mDownsampleEnabledForNetwork;
    /* access modifiers changed from: private */
    public final Executor mExecutor;
    /* access modifiers changed from: private */
    public final ImageDecoder mImageDecoder;
    private final Producer<EncodedImage> mInputProducer;
    private final ProgressiveJpegConfig mProgressiveJpegConfig;

    private class LocalImagesProgressiveDecoder extends ProgressiveDecoder {
        public LocalImagesProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, boolean z) {
            super(consumer, producerContext, z);
        }

        /* access modifiers changed from: protected */
        public synchronized boolean updateDecodeJob(EncodedImage encodedImage, boolean z) {
            if (!z) {
                return false;
            }
            return super.updateDecodeJob(encodedImage, z);
        }

        /* access modifiers changed from: protected */
        public int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            return encodedImage.getSize();
        }

        /* access modifiers changed from: protected */
        public QualityInfo getQualityInfo() {
            return ImmutableQualityInfo.m134of(0, false, false);
        }
    }

    private class NetworkImagesProgressiveDecoder extends ProgressiveDecoder {
        private int mLastScheduledScanNumber = 0;
        private final ProgressiveJpegConfig mProgressiveJpegConfig;
        private final ProgressiveJpegParser mProgressiveJpegParser;

        public NetworkImagesProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, ProgressiveJpegParser progressiveJpegParser, ProgressiveJpegConfig progressiveJpegConfig, boolean z) {
            super(consumer, producerContext, z);
            this.mProgressiveJpegParser = (ProgressiveJpegParser) Preconditions.checkNotNull(progressiveJpegParser);
            this.mProgressiveJpegConfig = (ProgressiveJpegConfig) Preconditions.checkNotNull(progressiveJpegConfig);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0043, code lost:
            return r0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public synchronized boolean updateDecodeJob(com.facebook.imagepipeline.image.EncodedImage r4, boolean r5) {
            /*
                r3 = this;
                monitor-enter(r3)
                boolean r0 = super.updateDecodeJob(r4, r5)     // Catch:{ all -> 0x0044 }
                if (r5 != 0) goto L_0x0042
                boolean r5 = com.facebook.imagepipeline.image.EncodedImage.isValid(r4)     // Catch:{ all -> 0x0044 }
                if (r5 == 0) goto L_0x0042
                com.facebook.imageformat.ImageFormat r5 = r4.getImageFormat()     // Catch:{ all -> 0x0044 }
                com.facebook.imageformat.ImageFormat r1 = com.facebook.imageformat.DefaultImageFormats.JPEG     // Catch:{ all -> 0x0044 }
                if (r5 != r1) goto L_0x0042
                com.facebook.imagepipeline.decoder.ProgressiveJpegParser r5 = r3.mProgressiveJpegParser     // Catch:{ all -> 0x0044 }
                boolean r4 = r5.parseMoreData(r4)     // Catch:{ all -> 0x0044 }
                r5 = 0
                if (r4 != 0) goto L_0x0020
                monitor-exit(r3)
                return r5
            L_0x0020:
                com.facebook.imagepipeline.decoder.ProgressiveJpegParser r4 = r3.mProgressiveJpegParser     // Catch:{ all -> 0x0044 }
                int r4 = r4.getBestScanNumber()     // Catch:{ all -> 0x0044 }
                int r1 = r3.mLastScheduledScanNumber     // Catch:{ all -> 0x0044 }
                if (r4 > r1) goto L_0x002c
                monitor-exit(r3)
                return r5
            L_0x002c:
                com.facebook.imagepipeline.decoder.ProgressiveJpegConfig r1 = r3.mProgressiveJpegConfig     // Catch:{ all -> 0x0044 }
                int r2 = r3.mLastScheduledScanNumber     // Catch:{ all -> 0x0044 }
                int r1 = r1.getNextScanNumberToDecode(r2)     // Catch:{ all -> 0x0044 }
                if (r4 >= r1) goto L_0x0040
                com.facebook.imagepipeline.decoder.ProgressiveJpegParser r1 = r3.mProgressiveJpegParser     // Catch:{ all -> 0x0044 }
                boolean r1 = r1.isEndMarkerRead()     // Catch:{ all -> 0x0044 }
                if (r1 != 0) goto L_0x0040
                monitor-exit(r3)
                return r5
            L_0x0040:
                r3.mLastScheduledScanNumber = r4     // Catch:{ all -> 0x0044 }
            L_0x0042:
                monitor-exit(r3)
                return r0
            L_0x0044:
                r4 = move-exception
                monitor-exit(r3)
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.DecodeProducer.NetworkImagesProgressiveDecoder.updateDecodeJob(com.facebook.imagepipeline.image.EncodedImage, boolean):boolean");
        }

        /* access modifiers changed from: protected */
        public int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            return this.mProgressiveJpegParser.getBestScanEndOffset();
        }

        /* access modifiers changed from: protected */
        public QualityInfo getQualityInfo() {
            return this.mProgressiveJpegConfig.getQualityInfo(this.mProgressiveJpegParser.getBestScanNumber());
        }
    }

    private abstract class ProgressiveDecoder extends DelegatingConsumer<EncodedImage, CloseableReference<CloseableImage>> {
        private final ImageDecodeOptions mImageDecodeOptions;
        @GuardedBy("this")
        private boolean mIsFinished = false;
        /* access modifiers changed from: private */
        public final JobScheduler mJobScheduler;
        /* access modifiers changed from: private */
        public final ProducerContext mProducerContext;
        private final ProducerListener mProducerListener;

        /* access modifiers changed from: protected */
        public abstract int getIntermediateImageEndOffset(EncodedImage encodedImage);

        /* access modifiers changed from: protected */
        public abstract QualityInfo getQualityInfo();

        public ProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, final ProducerContext producerContext, final boolean z) {
            super(consumer);
            this.mProducerContext = producerContext;
            this.mProducerListener = producerContext.getListener();
            this.mImageDecodeOptions = producerContext.getImageRequest().getImageDecodeOptions();
            this.mJobScheduler = new JobScheduler(DecodeProducer.this.mExecutor, new JobRunnable(DecodeProducer.this) {
                public void run(EncodedImage encodedImage, boolean z) {
                    if (encodedImage != null) {
                        if (DecodeProducer.this.mDownsampleEnabled) {
                            ImageRequest imageRequest = producerContext.getImageRequest();
                            if (DecodeProducer.this.mDownsampleEnabledForNetwork || !UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                                encodedImage.setSampleSize(DownsampleUtil.determineSampleSize(imageRequest, encodedImage));
                            }
                        }
                        ProgressiveDecoder.this.doDecode(encodedImage, z);
                    }
                }
            }, this.mImageDecodeOptions.minDecodeIntervalMs);
            this.mProducerContext.addCallbacks(new BaseProducerContextCallbacks(DecodeProducer.this) {
                public void onIsIntermediateResultExpectedChanged() {
                    if (ProgressiveDecoder.this.mProducerContext.isIntermediateResultExpected()) {
                        ProgressiveDecoder.this.mJobScheduler.scheduleJob();
                    }
                }

                public void onCancellationRequested() {
                    if (z) {
                        ProgressiveDecoder.this.handleCancellation();
                    }
                }
            });
        }

        public void onNewResultImpl(EncodedImage encodedImage, boolean z) {
            if (z && !EncodedImage.isValid(encodedImage)) {
                handleError(new ExceptionWithNoStacktrace("Encoded image is not valid."));
            } else if (updateDecodeJob(encodedImage, z)) {
                if (z || this.mProducerContext.isIntermediateResultExpected()) {
                    this.mJobScheduler.scheduleJob();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdateImpl(float f) {
            super.onProgressUpdateImpl(f * 0.99f);
        }

        public void onFailureImpl(Throwable th) {
            handleError(th);
        }

        public void onCancellationImpl() {
            handleCancellation();
        }

        /* access modifiers changed from: protected */
        public boolean updateDecodeJob(EncodedImage encodedImage, boolean z) {
            return this.mJobScheduler.updateJob(encodedImage, z);
        }

        /* access modifiers changed from: private */
        public void doDecode(EncodedImage encodedImage, boolean z) {
            String str;
            String str2;
            String str3;
            long queuedTime;
            QualityInfo qualityInfo;
            if (!isFinished() && EncodedImage.isValid(encodedImage)) {
                ImageFormat imageFormat = encodedImage.getImageFormat();
                String name = imageFormat != null ? imageFormat.getName() : EnvironmentCompat.MEDIA_UNKNOWN;
                if (encodedImage != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(encodedImage.getWidth());
                    sb.append("x");
                    sb.append(encodedImage.getHeight());
                    str = sb.toString();
                    str2 = String.valueOf(encodedImage.getSampleSize());
                } else {
                    str = EnvironmentCompat.MEDIA_UNKNOWN;
                    str2 = EnvironmentCompat.MEDIA_UNKNOWN;
                }
                String str4 = str;
                String str5 = str2;
                ResizeOptions resizeOptions = this.mProducerContext.getImageRequest().getResizeOptions();
                if (resizeOptions != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(resizeOptions.width);
                    sb2.append("x");
                    sb2.append(resizeOptions.height);
                    str3 = sb2.toString();
                } else {
                    str3 = EnvironmentCompat.MEDIA_UNKNOWN;
                }
                String str6 = str3;
                try {
                    queuedTime = this.mJobScheduler.getQueuedTime();
                    int size = z ? encodedImage.getSize() : getIntermediateImageEndOffset(encodedImage);
                    qualityInfo = z ? ImmutableQualityInfo.FULL_QUALITY : getQualityInfo();
                    this.mProducerListener.onProducerStart(this.mProducerContext.getId(), DecodeProducer.PRODUCER_NAME);
                    CloseableImage decode = DecodeProducer.this.mImageDecoder.decode(encodedImage, size, qualityInfo, this.mImageDecodeOptions);
                    this.mProducerListener.onProducerFinishWithSuccess(this.mProducerContext.getId(), DecodeProducer.PRODUCER_NAME, getExtraMap(decode, queuedTime, qualityInfo, z, name, str4, str6, str5));
                    handleResult(decode, z);
                } catch (Exception e) {
                    this.mProducerListener.onProducerFinishWithFailure(this.mProducerContext.getId(), DecodeProducer.PRODUCER_NAME, e, getExtraMap(null, queuedTime, qualityInfo, z, name, str4, str6, str5));
                    handleError(e);
                } finally {
                    EncodedImage.closeSafely(encodedImage);
                }
            }
        }

        private Map<String, String> getExtraMap(@Nullable CloseableImage closeableImage, long j, QualityInfo qualityInfo, boolean z, String str, String str2, String str3, String str4) {
            if (!this.mProducerListener.requiresExtraMap(this.mProducerContext.getId())) {
                return null;
            }
            String valueOf = String.valueOf(j);
            String valueOf2 = String.valueOf(qualityInfo.isOfGoodEnoughQuality());
            String valueOf3 = String.valueOf(z);
            if (closeableImage instanceof CloseableStaticBitmap) {
                Bitmap underlyingBitmap = ((CloseableStaticBitmap) closeableImage).getUnderlyingBitmap();
                StringBuilder sb = new StringBuilder();
                sb.append(underlyingBitmap.getWidth());
                sb.append("x");
                sb.append(underlyingBitmap.getHeight());
                String sb2 = sb.toString();
                HashMap hashMap = new HashMap(8);
                hashMap.put(DecodeProducer.EXTRA_BITMAP_SIZE, sb2);
                hashMap.put("queueTime", valueOf);
                hashMap.put(DecodeProducer.EXTRA_HAS_GOOD_QUALITY, valueOf2);
                hashMap.put(DecodeProducer.EXTRA_IS_FINAL, valueOf3);
                hashMap.put("encodedImageSize", str2);
                hashMap.put(DecodeProducer.EXTRA_IMAGE_FORMAT_NAME, str);
                hashMap.put(DecodeProducer.REQUESTED_IMAGE_SIZE, str3);
                hashMap.put(DecodeProducer.SAMPLE_SIZE, str4);
                return ImmutableMap.copyOf(hashMap);
            }
            HashMap hashMap2 = new HashMap(7);
            hashMap2.put("queueTime", valueOf);
            hashMap2.put(DecodeProducer.EXTRA_HAS_GOOD_QUALITY, valueOf2);
            hashMap2.put(DecodeProducer.EXTRA_IS_FINAL, valueOf3);
            hashMap2.put("encodedImageSize", str2);
            hashMap2.put(DecodeProducer.EXTRA_IMAGE_FORMAT_NAME, str);
            hashMap2.put(DecodeProducer.REQUESTED_IMAGE_SIZE, str3);
            hashMap2.put(DecodeProducer.SAMPLE_SIZE, str4);
            return ImmutableMap.copyOf(hashMap2);
        }

        private synchronized boolean isFinished() {
            return this.mIsFinished;
        }

        private void maybeFinish(boolean z) {
            synchronized (this) {
                if (z) {
                    if (!this.mIsFinished) {
                        getConsumer().onProgressUpdate(1.0f);
                        this.mIsFinished = true;
                        this.mJobScheduler.clearJob();
                    }
                }
            }
        }

        private void handleResult(CloseableImage closeableImage, boolean z) {
            CloseableReference of = CloseableReference.m129of(closeableImage);
            try {
                maybeFinish(z);
                getConsumer().onNewResult(of, z);
            } finally {
                CloseableReference.closeSafely(of);
            }
        }

        private void handleError(Throwable th) {
            maybeFinish(true);
            getConsumer().onFailure(th);
        }

        /* access modifiers changed from: private */
        public void handleCancellation() {
            maybeFinish(true);
            getConsumer().onCancellation();
        }
    }

    public DecodeProducer(ByteArrayPool byteArrayPool, Executor executor, ImageDecoder imageDecoder, ProgressiveJpegConfig progressiveJpegConfig, boolean z, boolean z2, boolean z3, Producer<EncodedImage> producer) {
        this.mByteArrayPool = (ByteArrayPool) Preconditions.checkNotNull(byteArrayPool);
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
        this.mImageDecoder = (ImageDecoder) Preconditions.checkNotNull(imageDecoder);
        this.mProgressiveJpegConfig = (ProgressiveJpegConfig) Preconditions.checkNotNull(progressiveJpegConfig);
        this.mDownsampleEnabled = z;
        this.mDownsampleEnabledForNetwork = z2;
        this.mInputProducer = (Producer) Preconditions.checkNotNull(producer);
        this.mDecodeCancellationEnabled = z3;
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [com.facebook.imagepipeline.producers.Consumer] */
    /* JADX WARNING: type inference failed for: r0v6, types: [com.facebook.imagepipeline.producers.DecodeProducer$LocalImagesProgressiveDecoder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void produceResults(com.facebook.imagepipeline.producers.Consumer<com.facebook.common.references.CloseableReference<com.facebook.imagepipeline.image.CloseableImage>> r10, com.facebook.imagepipeline.producers.ProducerContext r11) {
        /*
            r9 = this;
            com.facebook.imagepipeline.request.ImageRequest r0 = r11.getImageRequest()
            android.net.Uri r0 = r0.getSourceUri()
            boolean r0 = com.facebook.common.util.UriUtil.isNetworkUri(r0)
            if (r0 != 0) goto L_0x0016
            com.facebook.imagepipeline.producers.DecodeProducer$LocalImagesProgressiveDecoder r0 = new com.facebook.imagepipeline.producers.DecodeProducer$LocalImagesProgressiveDecoder
            boolean r1 = r9.mDecodeCancellationEnabled
            r0.<init>(r10, r11, r1)
            goto L_0x002a
        L_0x0016:
            com.facebook.imagepipeline.decoder.ProgressiveJpegParser r6 = new com.facebook.imagepipeline.decoder.ProgressiveJpegParser
            com.facebook.common.memory.ByteArrayPool r0 = r9.mByteArrayPool
            r6.<init>(r0)
            com.facebook.imagepipeline.producers.DecodeProducer$NetworkImagesProgressiveDecoder r0 = new com.facebook.imagepipeline.producers.DecodeProducer$NetworkImagesProgressiveDecoder
            com.facebook.imagepipeline.decoder.ProgressiveJpegConfig r7 = r9.mProgressiveJpegConfig
            boolean r8 = r9.mDecodeCancellationEnabled
            r2 = r0
            r3 = r9
            r4 = r10
            r5 = r11
            r2.<init>(r4, r5, r6, r7, r8)
        L_0x002a:
            com.facebook.imagepipeline.producers.Producer<com.facebook.imagepipeline.image.EncodedImage> r10 = r9.mInputProducer
            r10.produceResults(r0, r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.DecodeProducer.produceResults(com.facebook.imagepipeline.producers.Consumer, com.facebook.imagepipeline.producers.ProducerContext):void");
    }
}
