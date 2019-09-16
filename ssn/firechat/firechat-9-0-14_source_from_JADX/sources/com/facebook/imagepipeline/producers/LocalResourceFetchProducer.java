package com.facebook.imagepipeline.producers;

import android.content.res.Resources;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.util.concurrent.Executor;

public class LocalResourceFetchProducer extends LocalFetchProducer {
    public static final String PRODUCER_NAME = "LocalResourceFetchProducer";
    private final Resources mResources;

    /* access modifiers changed from: protected */
    public String getProducerName() {
        return PRODUCER_NAME;
    }

    public LocalResourceFetchProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, Resources resources) {
        super(executor, pooledByteBufferFactory);
        this.mResources = resources;
    }

    /* access modifiers changed from: protected */
    public EncodedImage getEncodedImage(ImageRequest imageRequest) throws IOException {
        return getEncodedImage(this.mResources.openRawResource(getResourceId(imageRequest)), getLength(imageRequest));
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0020 A[SYNTHETIC, Splitter:B:17:0x0020] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0027 A[SYNTHETIC, Splitter:B:24:0x0027] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getLength(com.facebook.imagepipeline.request.ImageRequest r4) {
        /*
            r3 = this;
            r0 = 0
            android.content.res.Resources r1 = r3.mResources     // Catch:{ NotFoundException -> 0x0024, all -> 0x001d }
            int r4 = getResourceId(r4)     // Catch:{ NotFoundException -> 0x0024, all -> 0x001d }
            android.content.res.AssetFileDescriptor r4 = r1.openRawResourceFd(r4)     // Catch:{ NotFoundException -> 0x0024, all -> 0x001d }
            long r0 = r4.getLength()     // Catch:{ NotFoundException -> 0x001b, all -> 0x0016 }
            int r0 = (int) r0
            if (r4 == 0) goto L_0x0015
            r4.close()     // Catch:{ IOException -> 0x0015 }
        L_0x0015:
            return r0
        L_0x0016:
            r0 = move-exception
            r2 = r0
            r0 = r4
            r4 = r2
            goto L_0x001e
        L_0x001b:
            r0 = r4
            goto L_0x0024
        L_0x001d:
            r4 = move-exception
        L_0x001e:
            if (r0 == 0) goto L_0x0023
            r0.close()     // Catch:{ IOException -> 0x0023 }
        L_0x0023:
            throw r4
        L_0x0024:
            r4 = -1
            if (r0 == 0) goto L_0x002a
            r0.close()     // Catch:{ IOException -> 0x002a }
        L_0x002a:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.producers.LocalResourceFetchProducer.getLength(com.facebook.imagepipeline.request.ImageRequest):int");
    }

    private static int getResourceId(ImageRequest imageRequest) {
        return Integer.parseInt(imageRequest.getSourceUri().getPath().substring(1));
    }
}
