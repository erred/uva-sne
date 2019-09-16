package com.facebook.imagepipeline.core;

import com.facebook.common.internal.Supplier;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.common.webp.WebpBitmapFactory.WebpErrorLogger;
import com.facebook.imagepipeline.cache.MediaIdExtractor;
import javax.annotation.Nullable;

public class ImagePipelineExperiments {
    private final boolean mDecodeCancellationEnabled;
    private final boolean mExternalCreatedBitmapLogEnabled;
    private final int mForceSmallCacheThresholdBytes;
    private final MediaIdExtractor mMediaIdExtractor;
    private final Supplier<Boolean> mMediaVariationsIndexEnabled;
    private final boolean mSuppressBitmapPrefetching;
    private final boolean mUseDownsamplingRatioForResizing;
    private final WebpBitmapFactory mWebpBitmapFactory;
    private final WebpErrorLogger mWebpErrorLogger;
    private final boolean mWebpSupportEnabled;

    public static class Builder {
        private final com.facebook.imagepipeline.core.ImagePipelineConfig.Builder mConfigBuilder;
        /* access modifiers changed from: private */
        public boolean mDecodeCancellationEnabled = false;
        /* access modifiers changed from: private */
        public boolean mExternalCreatedBitmapLogEnabled = false;
        /* access modifiers changed from: private */
        public int mForceSmallCacheThresholdBytes = 0;
        /* access modifiers changed from: private */
        public MediaIdExtractor mMediaIdExtractor;
        /* access modifiers changed from: private */
        public Supplier<Boolean> mMediaVariationsIndexEnabled = null;
        /* access modifiers changed from: private */
        public boolean mSuppressBitmapPrefetching = false;
        /* access modifiers changed from: private */
        public boolean mUseDownsamplingRatioForResizing = false;
        /* access modifiers changed from: private */
        public WebpBitmapFactory mWebpBitmapFactory;
        /* access modifiers changed from: private */
        public WebpErrorLogger mWebpErrorLogger;
        /* access modifiers changed from: private */
        public boolean mWebpSupportEnabled = false;

        public Builder(com.facebook.imagepipeline.core.ImagePipelineConfig.Builder builder) {
            this.mConfigBuilder = builder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setExternalCreatedBitmapLogEnabled(boolean z) {
            this.mExternalCreatedBitmapLogEnabled = z;
            return this.mConfigBuilder;
        }

        @Deprecated
        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setForceSmallCacheThresholdBytes(int i) {
            this.mForceSmallCacheThresholdBytes = i;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setMediaVariationsIndexEnabled(Supplier<Boolean> supplier) {
            this.mMediaVariationsIndexEnabled = supplier;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setMediaIdExtractor(MediaIdExtractor mediaIdExtractor) {
            this.mMediaIdExtractor = mediaIdExtractor;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setWebpSupportEnabled(boolean z) {
            this.mWebpSupportEnabled = z;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setUseDownsampligRatioForResizing(boolean z) {
            this.mUseDownsamplingRatioForResizing = z;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setDecodeCancellationEnabled(boolean z) {
            this.mDecodeCancellationEnabled = z;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setWebpErrorLogger(WebpErrorLogger webpErrorLogger) {
            this.mWebpErrorLogger = webpErrorLogger;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setWebpBitmapFactory(WebpBitmapFactory webpBitmapFactory) {
            this.mWebpBitmapFactory = webpBitmapFactory;
            return this.mConfigBuilder;
        }

        public com.facebook.imagepipeline.core.ImagePipelineConfig.Builder setSuppressBitmapPrefetching(boolean z) {
            this.mSuppressBitmapPrefetching = z;
            return this.mConfigBuilder;
        }

        public ImagePipelineExperiments build() {
            return new ImagePipelineExperiments(this, this.mConfigBuilder);
        }
    }

    private ImagePipelineExperiments(Builder builder, com.facebook.imagepipeline.core.ImagePipelineConfig.Builder builder2) {
        this.mForceSmallCacheThresholdBytes = builder.mForceSmallCacheThresholdBytes;
        this.mWebpSupportEnabled = builder.mWebpSupportEnabled;
        this.mExternalCreatedBitmapLogEnabled = builder.mExternalCreatedBitmapLogEnabled;
        if (builder.mMediaVariationsIndexEnabled != null) {
            this.mMediaVariationsIndexEnabled = builder.mMediaVariationsIndexEnabled;
        } else {
            this.mMediaVariationsIndexEnabled = new Supplier<Boolean>() {
                public Boolean get() {
                    return Boolean.FALSE;
                }
            };
        }
        this.mMediaIdExtractor = builder.mMediaIdExtractor;
        this.mWebpErrorLogger = builder.mWebpErrorLogger;
        this.mDecodeCancellationEnabled = builder.mDecodeCancellationEnabled;
        this.mWebpBitmapFactory = builder.mWebpBitmapFactory;
        this.mSuppressBitmapPrefetching = builder.mSuppressBitmapPrefetching;
        this.mUseDownsamplingRatioForResizing = builder.mUseDownsamplingRatioForResizing;
    }

    public boolean isExternalCreatedBitmapLogEnabled() {
        return this.mExternalCreatedBitmapLogEnabled;
    }

    public int getForceSmallCacheThresholdBytes() {
        return this.mForceSmallCacheThresholdBytes;
    }

    public boolean getMediaVariationsIndexEnabled() {
        return ((Boolean) this.mMediaVariationsIndexEnabled.get()).booleanValue();
    }

    @Nullable
    public MediaIdExtractor getMediaIdExtractor() {
        return this.mMediaIdExtractor;
    }

    public boolean getUseDownsamplingRatioForResizing() {
        return this.mUseDownsamplingRatioForResizing;
    }

    public boolean isWebpSupportEnabled() {
        return this.mWebpSupportEnabled;
    }

    public boolean isDecodeCancellationEnabled() {
        return this.mDecodeCancellationEnabled;
    }

    public WebpErrorLogger getWebpErrorLogger() {
        return this.mWebpErrorLogger;
    }

    public WebpBitmapFactory getWebpBitmapFactory() {
        return this.mWebpBitmapFactory;
    }

    public static Builder newBuilder(com.facebook.imagepipeline.core.ImagePipelineConfig.Builder builder) {
        return new Builder(builder);
    }
}
