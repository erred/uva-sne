package com.facebook.react.flat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import com.RNFetchBlob.RNFetchBlobConst;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.views.image.GlobalImageLoadListener;
import com.facebook.react.views.image.ImageResizeMode;
import com.facebook.react.views.imagehelper.ImageSource;
import com.facebook.react.views.imagehelper.MultiSourceHelper;
import com.facebook.react.views.imagehelper.MultiSourceHelper.MultiSourceResult;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

final class DrawImageWithDrawee extends AbstractDrawCommand implements DrawImage, ControllerListener {
    private static final String LOCAL_CONTENT_SCHEME = "content";
    private static final String LOCAL_FILE_SCHEME = "file";
    private int mBorderColor;
    private float mBorderRadius;
    private float mBorderWidth;
    @Nullable
    private InvalidateCallback mCallback;
    @Nullable
    private PorterDuffColorFilter mColorFilter;
    private int mFadeDuration = 300;
    @Nullable
    private final GlobalImageLoadListener mGlobalImageLoadListener;
    private boolean mProgressiveRenderingEnabled;
    private int mReactTag;
    @Nullable
    private DraweeRequestHelper mRequestHelper;
    private ScaleType mScaleType = ImageResizeMode.defaultValue();
    private final List<ImageSource> mSources = new LinkedList();

    public void onIntermediateImageFailed(String str, Throwable th) {
    }

    public void onIntermediateImageSet(String str, @Nullable Object obj) {
    }

    public void onRelease(String str) {
    }

    public DrawImageWithDrawee(@Nullable GlobalImageLoadListener globalImageLoadListener) {
        this.mGlobalImageLoadListener = globalImageLoadListener;
    }

    public boolean hasImageRequest() {
        return !this.mSources.isEmpty();
    }

    public void setSource(Context context, @Nullable ReadableArray readableArray) {
        this.mSources.clear();
        if (readableArray != null && readableArray.size() != 0) {
            if (readableArray.size() == 1) {
                this.mSources.add(new ImageSource(context, readableArray.getMap(0).getString(RNFetchBlobConst.DATA_ENCODE_URI)));
                return;
            }
            for (int i = 0; i < readableArray.size(); i++) {
                ReadableMap map = readableArray.getMap(i);
                List<ImageSource> list = this.mSources;
                ImageSource imageSource = new ImageSource(context, map.getString(RNFetchBlobConst.DATA_ENCODE_URI), map.getDouble("width"), map.getDouble("height"));
                list.add(imageSource);
            }
        }
    }

    public void setTintColor(int i) {
        if (i == 0) {
            this.mColorFilter = null;
        } else {
            this.mColorFilter = new PorterDuffColorFilter(i, Mode.SRC_ATOP);
        }
    }

    public void setScaleType(ScaleType scaleType) {
        this.mScaleType = scaleType;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void setBorderWidth(float f) {
        this.mBorderWidth = f;
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderRadius(float f) {
        this.mBorderRadius = f;
    }

    public float getBorderRadius() {
        return this.mBorderRadius;
    }

    public void setBorderColor(int i) {
        this.mBorderColor = i;
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setFadeDuration(int i) {
        this.mFadeDuration = i;
    }

    public void setProgressiveRenderingEnabled(boolean z) {
        this.mProgressiveRenderingEnabled = z;
    }

    public void setReactTag(int i) {
        this.mReactTag = i;
    }

    public void onDraw(Canvas canvas) {
        if (this.mRequestHelper != null) {
            this.mRequestHelper.getDrawable().draw(canvas);
        }
    }

    public void onAttached(InvalidateCallback invalidateCallback) {
        this.mCallback = invalidateCallback;
        if (this.mRequestHelper == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No DraweeRequestHelper - width: ");
            sb.append(getRight() - getLeft());
            sb.append(" - height: ");
            sb.append(getBottom() - getTop());
            sb.append(" - number of sources: ");
            sb.append(this.mSources.size());
            throw new RuntimeException(sb.toString());
        }
        GenericDraweeHierarchy hierarchy = this.mRequestHelper.getHierarchy();
        RoundingParams roundingParams = hierarchy.getRoundingParams();
        if (shouldDisplayBorder()) {
            if (roundingParams == null) {
                roundingParams = new RoundingParams();
            }
            roundingParams.setBorder(this.mBorderColor, this.mBorderWidth);
            roundingParams.setCornersRadius(this.mBorderRadius);
            hierarchy.setRoundingParams(roundingParams);
        } else if (roundingParams != null) {
            hierarchy.setRoundingParams(null);
        }
        hierarchy.setActualImageScaleType(this.mScaleType);
        hierarchy.setActualImageColorFilter(this.mColorFilter);
        hierarchy.setFadeDuration(this.mFadeDuration);
        hierarchy.getTopLevelDrawable().setBounds(Math.round(getLeft()), Math.round(getTop()), Math.round(getRight()), Math.round(getBottom()));
        this.mRequestHelper.attach(invalidateCallback);
    }

    public void onDetached() {
        if (this.mRequestHelper != null) {
            this.mRequestHelper.detach();
        }
    }

    public void onSubmit(String str, Object obj) {
        if (this.mCallback != null && this.mReactTag != 0) {
            this.mCallback.dispatchImageLoadEvent(this.mReactTag, 4);
        }
    }

    public void onFinalImageSet(String str, @Nullable Object obj, @Nullable Animatable animatable) {
        if (this.mCallback != null && this.mReactTag != 0) {
            this.mCallback.dispatchImageLoadEvent(this.mReactTag, 2);
            this.mCallback.dispatchImageLoadEvent(this.mReactTag, 3);
        }
    }

    public void onFailure(String str, Throwable th) {
        if (this.mCallback != null && this.mReactTag != 0) {
            this.mCallback.dispatchImageLoadEvent(this.mReactTag, 1);
            this.mCallback.dispatchImageLoadEvent(this.mReactTag, 3);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChanged() {
        super.onBoundsChanged();
        computeRequestHelper();
    }

    private void computeRequestHelper() {
        MultiSourceResult bestSourceForSize = MultiSourceHelper.getBestSourceForSize(Math.round(getRight() - getLeft()), Math.round(getBottom() - getTop()), this.mSources);
        ImageSource bestResult = bestSourceForSize.getBestResult();
        ImageSource bestResultInCache = bestSourceForSize.getBestResultInCache();
        ImageRequest imageRequest = null;
        if (bestResult == null) {
            this.mRequestHelper = null;
            return;
        }
        ResizeOptions resizeOptions = shouldResize(bestResult) ? new ResizeOptions((int) (getRight() - getLeft()), (int) (getBottom() - getTop())) : null;
        ImageRequest build = ImageRequestBuilder.newBuilderWithSource(bestResult.getUri()).setResizeOptions(resizeOptions).setProgressiveRenderingEnabled(this.mProgressiveRenderingEnabled).build();
        if (this.mGlobalImageLoadListener != null) {
            this.mGlobalImageLoadListener.onLoadAttempt(bestResult.getUri());
        }
        if (bestResultInCache != null) {
            imageRequest = ImageRequestBuilder.newBuilderWithSource(bestResultInCache.getUri()).setResizeOptions(resizeOptions).setProgressiveRenderingEnabled(this.mProgressiveRenderingEnabled).build();
        }
        this.mRequestHelper = new DraweeRequestHelper((ImageRequest) Assertions.assertNotNull(build), imageRequest, this);
    }

    private boolean shouldDisplayBorder() {
        return this.mBorderColor != 0 || this.mBorderRadius >= 0.5f;
    }

    private static boolean shouldResize(ImageSource imageSource) {
        String str;
        Uri uri = imageSource.getUri();
        if (uri == null) {
            str = null;
        } else {
            str = uri.getScheme();
        }
        return "file".equals(str) || "content".equals(str);
    }

    /* access modifiers changed from: protected */
    public void onDebugDrawHighlight(Canvas canvas) {
        if (this.mCallback != null) {
            debugDrawCautionHighlight(canvas, "Invalidate Drawee");
        }
    }
}
