package com.facebook.drawee.backends.pipeline;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawable.base.DrawableWithCaches;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.debug.DebugControllerOverlayDrawable;
import com.facebook.drawee.drawable.OrientedDrawable;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.imagepipeline.animated.factory.AnimatedDrawableFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import java.util.Iterator;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

public class PipelineDraweeController extends AbstractDraweeController<CloseableReference<CloseableImage>, ImageInfo> {
    private static final Class<?> TAG = PipelineDraweeController.class;
    /* access modifiers changed from: private */
    public final AnimatedDrawableFactory mAnimatedDrawableFactory;
    private CacheKey mCacheKey;
    private Supplier<DataSource<CloseableReference<CloseableImage>>> mDataSourceSupplier;
    private final DrawableFactory mDefaultDrawableFactory;
    private boolean mDrawDebugOverlay;
    @Nullable
    private final ImmutableList<DrawableFactory> mDrawableFactories;
    @Nullable
    private MemoryCache<CacheKey, CloseableImage> mMemoryCache;
    /* access modifiers changed from: private */
    public final Resources mResources;

    public PipelineDraweeController(Resources resources, DeferredReleaser deferredReleaser, AnimatedDrawableFactory animatedDrawableFactory, Executor executor, MemoryCache<CacheKey, CloseableImage> memoryCache, Supplier<DataSource<CloseableReference<CloseableImage>>> supplier, String str, CacheKey cacheKey, Object obj) {
        this(resources, deferredReleaser, animatedDrawableFactory, executor, memoryCache, supplier, str, cacheKey, obj, null);
    }

    public PipelineDraweeController(Resources resources, DeferredReleaser deferredReleaser, AnimatedDrawableFactory animatedDrawableFactory, Executor executor, MemoryCache<CacheKey, CloseableImage> memoryCache, Supplier<DataSource<CloseableReference<CloseableImage>>> supplier, String str, CacheKey cacheKey, Object obj, @Nullable ImmutableList<DrawableFactory> immutableList) {
        super(deferredReleaser, executor, str, obj);
        this.mDefaultDrawableFactory = new DrawableFactory() {
            public boolean supportsImageType(CloseableImage closeableImage) {
                return true;
            }

            public Drawable createDrawable(CloseableImage closeableImage) {
                if (closeableImage instanceof CloseableStaticBitmap) {
                    CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(PipelineDraweeController.this.mResources, closeableStaticBitmap.getUnderlyingBitmap());
                    return (closeableStaticBitmap.getRotationAngle() == 0 || closeableStaticBitmap.getRotationAngle() == -1) ? bitmapDrawable : new OrientedDrawable(bitmapDrawable, closeableStaticBitmap.getRotationAngle());
                } else if (PipelineDraweeController.this.mAnimatedDrawableFactory != null) {
                    return PipelineDraweeController.this.mAnimatedDrawableFactory.create(closeableImage);
                } else {
                    return null;
                }
            }
        };
        this.mResources = resources;
        this.mAnimatedDrawableFactory = animatedDrawableFactory;
        this.mMemoryCache = memoryCache;
        this.mCacheKey = cacheKey;
        this.mDrawableFactories = immutableList;
        init(supplier);
    }

    public void initialize(Supplier<DataSource<CloseableReference<CloseableImage>>> supplier, String str, CacheKey cacheKey, Object obj) {
        super.initialize(str, obj);
        init(supplier);
        this.mCacheKey = cacheKey;
    }

    public void setDrawDebugOverlay(boolean z) {
        this.mDrawDebugOverlay = z;
    }

    private void init(Supplier<DataSource<CloseableReference<CloseableImage>>> supplier) {
        this.mDataSourceSupplier = supplier;
        maybeUpdateDebugOverlay(null);
    }

    /* access modifiers changed from: protected */
    public Resources getResources() {
        return this.mResources;
    }

    /* access modifiers changed from: protected */
    public DataSource<CloseableReference<CloseableImage>> getDataSource() {
        if (FLog.isLoggable(2)) {
            FLog.m86v(TAG, "controller %x: getDataSource", (Object) Integer.valueOf(System.identityHashCode(this)));
        }
        return (DataSource) this.mDataSourceSupplier.get();
    }

    /* access modifiers changed from: protected */
    public Drawable createDrawable(CloseableReference<CloseableImage> closeableReference) {
        Preconditions.checkState(CloseableReference.isValid(closeableReference));
        CloseableImage closeableImage = (CloseableImage) closeableReference.get();
        maybeUpdateDebugOverlay(closeableImage);
        if (this.mDrawableFactories != null) {
            Iterator it = this.mDrawableFactories.iterator();
            while (it.hasNext()) {
                DrawableFactory drawableFactory = (DrawableFactory) it.next();
                if (drawableFactory.supportsImageType(closeableImage)) {
                    Drawable createDrawable = drawableFactory.createDrawable(closeableImage);
                    if (createDrawable != null) {
                        return createDrawable;
                    }
                }
            }
        }
        Drawable createDrawable2 = this.mDefaultDrawableFactory.createDrawable(closeableImage);
        if (createDrawable2 != null) {
            return createDrawable2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized image class: ");
        sb.append(closeableImage);
        throw new UnsupportedOperationException(sb.toString());
    }

    public void setHierarchy(@Nullable DraweeHierarchy draweeHierarchy) {
        super.setHierarchy(draweeHierarchy);
        maybeUpdateDebugOverlay(null);
    }

    private void maybeUpdateDebugOverlay(@Nullable CloseableImage closeableImage) {
        if (this.mDrawDebugOverlay) {
            Drawable controllerOverlay = getControllerOverlay();
            if (controllerOverlay == null) {
                controllerOverlay = new DebugControllerOverlayDrawable();
                setControllerOverlay(controllerOverlay);
            }
            if (controllerOverlay instanceof DebugControllerOverlayDrawable) {
                DebugControllerOverlayDrawable debugControllerOverlayDrawable = (DebugControllerOverlayDrawable) controllerOverlay;
                debugControllerOverlayDrawable.setControllerId(getId());
                DraweeHierarchy hierarchy = getHierarchy();
                ScaleType scaleType = null;
                if (hierarchy != null) {
                    ScaleTypeDrawable activeScaleTypeDrawable = ScalingUtils.getActiveScaleTypeDrawable(hierarchy.getTopLevelDrawable());
                    if (activeScaleTypeDrawable != null) {
                        scaleType = activeScaleTypeDrawable.getScaleType();
                    }
                }
                debugControllerOverlayDrawable.setScaleType(scaleType);
                if (closeableImage != null) {
                    debugControllerOverlayDrawable.setDimensions(closeableImage.getWidth(), closeableImage.getHeight());
                    debugControllerOverlayDrawable.setImageSize(closeableImage.getSizeInBytes());
                } else {
                    debugControllerOverlayDrawable.reset();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public ImageInfo getImageInfo(CloseableReference<CloseableImage> closeableReference) {
        Preconditions.checkState(CloseableReference.isValid(closeableReference));
        return (ImageInfo) closeableReference.get();
    }

    /* access modifiers changed from: protected */
    public int getImageHash(@Nullable CloseableReference<CloseableImage> closeableReference) {
        if (closeableReference != null) {
            return closeableReference.getValueHash();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void releaseImage(@Nullable CloseableReference<CloseableImage> closeableReference) {
        CloseableReference.closeSafely(closeableReference);
    }

    /* access modifiers changed from: protected */
    public void releaseDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof DrawableWithCaches) {
            ((DrawableWithCaches) drawable).dropCaches();
        }
    }

    /* access modifiers changed from: protected */
    public CloseableReference<CloseableImage> getCachedImage() {
        if (this.mMemoryCache == null || this.mCacheKey == null) {
            return null;
        }
        CloseableReference<CloseableImage> closeableReference = this.mMemoryCache.get(this.mCacheKey);
        if (closeableReference == null || ((CloseableImage) closeableReference.get()).getQualityInfo().isOfFullQuality()) {
            return closeableReference;
        }
        closeableReference.close();
        return null;
    }

    public String toString() {
        return Objects.toStringHelper((Object) this).add("super", (Object) super.toString()).add("dataSourceSupplier", (Object) this.mDataSourceSupplier).toString();
    }
}
