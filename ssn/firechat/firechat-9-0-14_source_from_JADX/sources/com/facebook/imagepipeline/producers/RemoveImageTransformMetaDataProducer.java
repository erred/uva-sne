package com.facebook.imagepipeline.producers;

import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;

public class RemoveImageTransformMetaDataProducer implements Producer<CloseableReference<PooledByteBuffer>> {
    private final Producer<EncodedImage> mInputProducer;

    private class RemoveImageTransformMetaDataConsumer extends DelegatingConsumer<EncodedImage, CloseableReference<PooledByteBuffer>> {
        private RemoveImageTransformMetaDataConsumer(Consumer<CloseableReference<PooledByteBuffer>> consumer) {
            super(consumer);
        }

        /* access modifiers changed from: protected */
        public void onNewResultImpl(EncodedImage encodedImage, boolean z) {
            CloseableReference closeableReference = null;
            try {
                if (EncodedImage.isValid(encodedImage)) {
                    closeableReference = encodedImage.getByteBufferRef();
                }
                getConsumer().onNewResult(closeableReference, z);
            } finally {
                CloseableReference.closeSafely(closeableReference);
            }
        }
    }

    public RemoveImageTransformMetaDataProducer(Producer<EncodedImage> producer) {
        this.mInputProducer = producer;
    }

    public void produceResults(Consumer<CloseableReference<PooledByteBuffer>> consumer, ProducerContext producerContext) {
        this.mInputProducer.produceResults(new RemoveImageTransformMetaDataConsumer(consumer), producerContext);
    }
}
