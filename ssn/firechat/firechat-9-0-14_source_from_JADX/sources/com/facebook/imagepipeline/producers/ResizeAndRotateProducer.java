package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.util.TriState;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.JobScheduler.JobRunnable;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class ResizeAndRotateProducer implements Producer<EncodedImage> {
    @VisibleForTesting
    static final int DEFAULT_JPEG_QUALITY = 85;
    private static final String DOWNSAMPLE_ENUMERATOR_KEY = "downsampleEnumerator";
    private static final String FRACTION_KEY = "Fraction";
    private static final int FULL_ROUND = 360;
    @VisibleForTesting
    static final int MAX_JPEG_SCALE_NUMERATOR = 8;
    @VisibleForTesting
    static final int MIN_TRANSFORM_INTERVAL_MS = 100;
    private static final String ORIGINAL_SIZE_KEY = "Original size";
    public static final String PRODUCER_NAME = "ResizeAndRotateProducer";
    private static final String REQUESTED_SIZE_KEY = "Requested size";
    private static final String ROTATION_ANGLE_KEY = "rotationAngle";
    private static final String SOFTWARE_ENUMERATOR_KEY = "softwareEnumerator";
    /* access modifiers changed from: private */
    public final Executor mExecutor;
    private final Producer<EncodedImage> mInputProducer;
    /* access modifiers changed from: private */
    public final PooledByteBufferFactory mPooledByteBufferFactory;
    /* access modifiers changed from: private */
    public final boolean mResizingEnabled;
    /* access modifiers changed from: private */
    public final boolean mUseDownsamplingRatio;

    private class TransformingConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        /* access modifiers changed from: private */
        public boolean mIsCancelled = false;
        /* access modifiers changed from: private */
        public final JobScheduler mJobScheduler;
        /* access modifiers changed from: private */
        public final ProducerContext mProducerContext;

        public TransformingConsumer(final Consumer<EncodedImage> consumer, ProducerContext producerContext) {
            super(consumer);
            this.mProducerContext = producerContext;
            this.mJobScheduler = new JobScheduler(ResizeAndRotateProducer.this.mExecutor, new JobRunnable(ResizeAndRotateProducer.this) {
                public void run(EncodedImage encodedImage, boolean z) {
                    TransformingConsumer.this.doTransform(encodedImage, z);
                }
            }, 100);
            this.mProducerContext.addCallbacks(new BaseProducerContextCallbacks(ResizeAndRotateProducer.this) {
                public void onIsIntermediateResultExpectedChanged() {
                    if (TransformingConsumer.this.mProducerContext.isIntermediateResultExpected()) {
                        TransformingConsumer.this.mJobScheduler.scheduleJob();
                    }
                }

                public void onCancellationRequested() {
                    TransformingConsumer.this.mJobScheduler.clearJob();
                    TransformingConsumer.this.mIsCancelled = true;
                    consumer.onCancellation();
                }
            });
        }

        /* access modifiers changed from: protected */
        public void onNewResultImpl(@Nullable EncodedImage encodedImage, boolean z) {
            if (!this.mIsCancelled) {
                if (encodedImage == null) {
                    if (z) {
                        getConsumer().onNewResult(null, true);
                    }
                    return;
                }
                TriState access$600 = ResizeAndRotateProducer.shouldTransform(this.mProducerContext.getImageRequest(), encodedImage, ResizeAndRotateProducer.this.mResizingEnabled);
                if (!z && access$600 == TriState.UNSET) {
                    return;
                }
                if (access$600 != TriState.YES) {
                    getConsumer().onNewResult(encodedImage, z);
                } else if (this.mJobScheduler.updateJob(encodedImage, z)) {
                    if (z || this.mProducerContext.isIntermediateResultExpected()) {
                        this.mJobScheduler.scheduleJob();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x009e, code lost:
            r13 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x009f, code lost:
            r12 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a2, code lost:
            r13 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x00a3, code lost:
            r12 = null;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00a2 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:1:0x0022] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void doTransform(com.facebook.imagepipeline.image.EncodedImage r12, boolean r13) {
            /*
                r11 = this;
                com.facebook.imagepipeline.producers.ProducerContext r0 = r11.mProducerContext
                com.facebook.imagepipeline.producers.ProducerListener r0 = r0.getListener()
                com.facebook.imagepipeline.producers.ProducerContext r1 = r11.mProducerContext
                java.lang.String r1 = r1.getId()
                java.lang.String r2 = "ResizeAndRotateProducer"
                r0.onProducerStart(r1, r2)
                com.facebook.imagepipeline.producers.ProducerContext r0 = r11.mProducerContext
                com.facebook.imagepipeline.request.ImageRequest r3 = r0.getImageRequest()
                com.facebook.imagepipeline.producers.ResizeAndRotateProducer r0 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.this
                com.facebook.common.memory.PooledByteBufferFactory r0 = r0.mPooledByteBufferFactory
                com.facebook.common.memory.PooledByteBufferOutputStream r0 = r0.newOutputStream()
                r8 = 0
                com.facebook.imagepipeline.producers.ResizeAndRotateProducer r1 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.this     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                boolean r1 = r1.mResizingEnabled     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                int r6 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.getSoftwareNumerator(r3, r12, r1)     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                int r1 = com.facebook.imagepipeline.producers.DownsampleUtil.determineSampleSize(r3, r12)     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                int r5 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.calculateDownsampleNumerator(r1)     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                com.facebook.imagepipeline.producers.ResizeAndRotateProducer r1 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.this     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                boolean r1 = r1.mUseDownsamplingRatio     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                if (r1 == 0) goto L_0x003e
                r9 = r5
                goto L_0x003f
            L_0x003e:
                r9 = r6
            L_0x003f:
                com.facebook.imagepipeline.common.RotationOptions r1 = r3.getRotationOptions()     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                int r10 = com.facebook.imagepipeline.producers.ResizeAndRotateProducer.getRotationAngle(r1, r12)     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                r1 = r11
                r2 = r12
                r4 = r9
                r7 = r10
                java.util.Map r1 = r1.getExtraMap(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x00a5, all -> 0x00a2 }
                java.io.InputStream r12 = r12.getInputStream()     // Catch:{ Exception -> 0x009e, all -> 0x00a2 }
                r2 = 85
                com.facebook.imagepipeline.nativecode.JpegTranscoder.transcodeJpeg(r12, r0, r10, r9, r2)     // Catch:{ Exception -> 0x009c }
                com.facebook.common.memory.PooledByteBuffer r2 = r0.toByteBuffer()     // Catch:{ Exception -> 0x009c }
                com.facebook.common.references.CloseableReference r2 = com.facebook.common.references.CloseableReference.m129of(r2)     // Catch:{ Exception -> 0x009c }
                com.facebook.imagepipeline.image.EncodedImage r3 = new com.facebook.imagepipeline.image.EncodedImage     // Catch:{ all -> 0x0097 }
                r3.<init>(r2)     // Catch:{ all -> 0x0097 }
                com.facebook.imageformat.ImageFormat r4 = com.facebook.imageformat.DefaultImageFormats.JPEG     // Catch:{ all -> 0x0097 }
                r3.setImageFormat(r4)     // Catch:{ all -> 0x0097 }
                r3.parseMetaData()     // Catch:{ all -> 0x0092 }
                com.facebook.imagepipeline.producers.ProducerContext r4 = r11.mProducerContext     // Catch:{ all -> 0x0092 }
                com.facebook.imagepipeline.producers.ProducerListener r4 = r4.getListener()     // Catch:{ all -> 0x0092 }
                com.facebook.imagepipeline.producers.ProducerContext r5 = r11.mProducerContext     // Catch:{ all -> 0x0092 }
                java.lang.String r5 = r5.getId()     // Catch:{ all -> 0x0092 }
                java.lang.String r6 = "ResizeAndRotateProducer"
                r4.onProducerFinishWithSuccess(r5, r6, r1)     // Catch:{ all -> 0x0092 }
                com.facebook.imagepipeline.producers.Consumer r4 = r11.getConsumer()     // Catch:{ all -> 0x0092 }
                r4.onNewResult(r3, r13)     // Catch:{ all -> 0x0092 }
                com.facebook.imagepipeline.image.EncodedImage.closeSafely(r3)     // Catch:{ all -> 0x0097 }
                com.facebook.common.references.CloseableReference.closeSafely(r2)     // Catch:{ Exception -> 0x009c }
                com.facebook.common.internal.Closeables.closeQuietly(r12)
                r0.close()
                return
            L_0x0092:
                r13 = move-exception
                com.facebook.imagepipeline.image.EncodedImage.closeSafely(r3)     // Catch:{ all -> 0x0097 }
                throw r13     // Catch:{ all -> 0x0097 }
            L_0x0097:
                r13 = move-exception
                com.facebook.common.references.CloseableReference.closeSafely(r2)     // Catch:{ Exception -> 0x009c }
                throw r13     // Catch:{ Exception -> 0x009c }
            L_0x009c:
                r13 = move-exception
                goto L_0x00a0
            L_0x009e:
                r13 = move-exception
                r12 = r8
            L_0x00a0:
                r8 = r1
                goto L_0x00a7
            L_0x00a2:
                r13 = move-exception
                r12 = r8
                goto L_0x00c7
            L_0x00a5:
                r13 = move-exception
                r12 = r8
            L_0x00a7:
                com.facebook.imagepipeline.producers.ProducerContext r1 = r11.mProducerContext     // Catch:{ all -> 0x00c6 }
                com.facebook.imagepipeline.producers.ProducerListener r1 = r1.getListener()     // Catch:{ all -> 0x00c6 }
                com.facebook.imagepipeline.producers.ProducerContext r2 = r11.mProducerContext     // Catch:{ all -> 0x00c6 }
                java.lang.String r2 = r2.getId()     // Catch:{ all -> 0x00c6 }
                java.lang.String r3 = "ResizeAndRotateProducer"
                r1.onProducerFinishWithFailure(r2, r3, r13, r8)     // Catch:{ all -> 0x00c6 }
                com.facebook.imagepipeline.producers.Consumer r1 = r11.getConsumer()     // Catch:{ all -> 0x00c6 }
                r1.onFailure(r13)     // Catch:{ all -> 0x00c6 }
                com.facebook.common.internal.Closeables.closeQuietly(r12)
                r0.close()
                return
            L_0x00c6:
                r13 = move-exception
            L_0x00c7:
                com.facebook.common.internal.Closeables.closeQuietly(r12)
                r0.close()
                throw r13
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.ResizeAndRotateProducer.TransformingConsumer.doTransform(com.facebook.imagepipeline.image.EncodedImage, boolean):void");
        }

        private Map<String, String> getExtraMap(EncodedImage encodedImage, ImageRequest imageRequest, int i, int i2, int i3, int i4) {
            String str;
            String str2;
            if (!this.mProducerContext.getListener().requiresExtraMap(this.mProducerContext.getId())) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(encodedImage.getWidth());
            sb.append("x");
            sb.append(encodedImage.getHeight());
            String sb2 = sb.toString();
            if (imageRequest.getResizeOptions() != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(imageRequest.getResizeOptions().width);
                sb3.append("x");
                sb3.append(imageRequest.getResizeOptions().height);
                str = sb3.toString();
            } else {
                str = "Unspecified";
            }
            if (i > 0) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(i);
                sb4.append("/8");
                str2 = sb4.toString();
            } else {
                str2 = "";
            }
            HashMap hashMap = new HashMap();
            hashMap.put(ResizeAndRotateProducer.ORIGINAL_SIZE_KEY, sb2);
            hashMap.put(ResizeAndRotateProducer.REQUESTED_SIZE_KEY, str);
            hashMap.put(ResizeAndRotateProducer.FRACTION_KEY, str2);
            hashMap.put("queueTime", String.valueOf(this.mJobScheduler.getQueuedTime()));
            hashMap.put(ResizeAndRotateProducer.DOWNSAMPLE_ENUMERATOR_KEY, Integer.toString(i2));
            hashMap.put(ResizeAndRotateProducer.SOFTWARE_ENUMERATOR_KEY, Integer.toString(i3));
            hashMap.put(ResizeAndRotateProducer.ROTATION_ANGLE_KEY, Integer.toString(i4));
            return ImmutableMap.copyOf(hashMap);
        }
    }

    @VisibleForTesting
    static int roundNumerator(float f, float f2) {
        return (int) (f2 + (f * 8.0f));
    }

    private static boolean shouldResize(int i) {
        return i < 8;
    }

    public ResizeAndRotateProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, boolean z, Producer<EncodedImage> producer, boolean z2) {
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
        this.mPooledByteBufferFactory = (PooledByteBufferFactory) Preconditions.checkNotNull(pooledByteBufferFactory);
        this.mResizingEnabled = z;
        this.mInputProducer = (Producer) Preconditions.checkNotNull(producer);
        this.mUseDownsamplingRatio = z2;
    }

    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        this.mInputProducer.produceResults(new TransformingConsumer(consumer, producerContext), producerContext);
    }

    /* access modifiers changed from: private */
    public static TriState shouldTransform(ImageRequest imageRequest, EncodedImage encodedImage, boolean z) {
        if (encodedImage == null || encodedImage.getImageFormat() == ImageFormat.UNKNOWN) {
            return TriState.UNSET;
        }
        if (encodedImage.getImageFormat() != DefaultImageFormats.JPEG) {
            return TriState.NO;
        }
        return TriState.valueOf(shouldRotate(imageRequest.getRotationOptions(), encodedImage) || shouldResize(getSoftwareNumerator(imageRequest, encodedImage, z)));
    }

    @VisibleForTesting
    static float determineResizeRatio(ResizeOptions resizeOptions, int i, int i2) {
        if (resizeOptions == null) {
            return 1.0f;
        }
        float f = (float) i;
        float f2 = (float) i2;
        float max = Math.max(((float) resizeOptions.width) / f, ((float) resizeOptions.height) / f2);
        if (f * max > resizeOptions.maxBitmapSize) {
            max = resizeOptions.maxBitmapSize / f;
        }
        if (f2 * max > resizeOptions.maxBitmapSize) {
            max = resizeOptions.maxBitmapSize / f2;
        }
        return max;
    }

    /* access modifiers changed from: private */
    public static int getSoftwareNumerator(ImageRequest imageRequest, EncodedImage encodedImage, boolean z) {
        int i;
        int i2;
        if (!z) {
            return 8;
        }
        ResizeOptions resizeOptions = imageRequest.getResizeOptions();
        if (resizeOptions == null) {
            return 8;
        }
        int rotationAngle = getRotationAngle(imageRequest.getRotationOptions(), encodedImage);
        boolean z2 = rotationAngle == 90 || rotationAngle == 270;
        if (z2) {
            i = encodedImage.getHeight();
        } else {
            i = encodedImage.getWidth();
        }
        if (z2) {
            i2 = encodedImage.getWidth();
        } else {
            i2 = encodedImage.getHeight();
        }
        int roundNumerator = roundNumerator(determineResizeRatio(resizeOptions, i, i2), resizeOptions.roundUpFraction);
        if (roundNumerator > 8) {
            return 8;
        }
        if (roundNumerator < 1) {
            roundNumerator = 1;
        }
        return roundNumerator;
    }

    /* access modifiers changed from: private */
    public static int getRotationAngle(RotationOptions rotationOptions, EncodedImage encodedImage) {
        if (!rotationOptions.rotationEnabled()) {
            return 0;
        }
        int extractOrientationFromMetadata = extractOrientationFromMetadata(encodedImage);
        if (rotationOptions.useImageMetadata()) {
            return extractOrientationFromMetadata;
        }
        return (extractOrientationFromMetadata + rotationOptions.getForcedAngle()) % FULL_ROUND;
    }

    private static int extractOrientationFromMetadata(EncodedImage encodedImage) {
        int rotationAngle = encodedImage.getRotationAngle();
        if (rotationAngle == 90 || rotationAngle == 180 || rotationAngle == 270) {
            return encodedImage.getRotationAngle();
        }
        return 0;
    }

    private static boolean shouldRotate(RotationOptions rotationOptions, EncodedImage encodedImage) {
        return !rotationOptions.canDeferUntilRendered() && getRotationAngle(rotationOptions, encodedImage) != 0;
    }

    @VisibleForTesting
    static int calculateDownsampleNumerator(int i) {
        return Math.max(1, 8 / i);
    }
}
