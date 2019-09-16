package com.facebook.drawee.generic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.facebook.drawee.C0617R;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils.ScaleType;
import com.facebook.infer.annotation.ReturnsOwnership;
import javax.annotation.Nullable;

public class GenericDraweeHierarchyInflater {
    public static GenericDraweeHierarchy inflateHierarchy(Context context, @Nullable AttributeSet attributeSet) {
        return inflateBuilder(context, attributeSet).build();
    }

    public static GenericDraweeHierarchyBuilder inflateBuilder(Context context, @Nullable AttributeSet attributeSet) {
        return updateBuilder(new GenericDraweeHierarchyBuilder(context.getResources()), context, attributeSet);
    }

    /* JADX INFO: finally extract failed */
    public static GenericDraweeHierarchyBuilder updateBuilder(GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilder, Context context, @Nullable AttributeSet attributeSet) {
        boolean z;
        boolean z2;
        boolean z3;
        int i;
        float f = 0.0f;
        boolean z4 = true;
        int i2 = 0;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C0617R.styleable.GenericDraweeHierarchy);
            try {
                int indexCount = obtainStyledAttributes.getIndexCount();
                int i3 = 0;
                i = 0;
                boolean z5 = true;
                z3 = true;
                z2 = true;
                z = true;
                for (int i4 = 0; i4 < indexCount; i4++) {
                    int index = obtainStyledAttributes.getIndex(i4);
                    if (index == C0617R.styleable.GenericDraweeHierarchy_actualImageScaleType) {
                        genericDraweeHierarchyBuilder.setActualImageScaleType(getScaleTypeFromXml(obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_placeholderImage) {
                        genericDraweeHierarchyBuilder.setPlaceholderImage(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_pressedStateOverlayImage) {
                        genericDraweeHierarchyBuilder.setPressedStateOverlay(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_progressBarImage) {
                        genericDraweeHierarchyBuilder.setProgressBarImage(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_fadeDuration) {
                        genericDraweeHierarchyBuilder.setFadeDuration(obtainStyledAttributes.getInt(index, 0));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_viewAspectRatio) {
                        genericDraweeHierarchyBuilder.setDesiredAspectRatio(obtainStyledAttributes.getFloat(index, 0.0f));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_placeholderImageScaleType) {
                        genericDraweeHierarchyBuilder.setPlaceholderImageScaleType(getScaleTypeFromXml(obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_retryImage) {
                        genericDraweeHierarchyBuilder.setRetryImage(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_retryImageScaleType) {
                        genericDraweeHierarchyBuilder.setRetryImageScaleType(getScaleTypeFromXml(obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_failureImage) {
                        genericDraweeHierarchyBuilder.setFailureImage(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_failureImageScaleType) {
                        genericDraweeHierarchyBuilder.setFailureImageScaleType(getScaleTypeFromXml(obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_progressBarImageScaleType) {
                        genericDraweeHierarchyBuilder.setProgressBarImageScaleType(getScaleTypeFromXml(obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_progressBarAutoRotateInterval) {
                        i3 = obtainStyledAttributes.getInteger(index, i3);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_backgroundImage) {
                        genericDraweeHierarchyBuilder.setBackground(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_overlayImage) {
                        genericDraweeHierarchyBuilder.setOverlay(getDrawable(context, obtainStyledAttributes, index));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundAsCircle) {
                        getRoundingParams(genericDraweeHierarchyBuilder).setRoundAsCircle(obtainStyledAttributes.getBoolean(index, false));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundedCornerRadius) {
                        i = obtainStyledAttributes.getDimensionPixelSize(index, i);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundTopLeft) {
                        z5 = obtainStyledAttributes.getBoolean(index, z5);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundTopRight) {
                        z3 = obtainStyledAttributes.getBoolean(index, z3);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundBottomLeft) {
                        z = obtainStyledAttributes.getBoolean(index, z);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundBottomRight) {
                        z2 = obtainStyledAttributes.getBoolean(index, z2);
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundWithOverlayColor) {
                        getRoundingParams(genericDraweeHierarchyBuilder).setOverlayColor(obtainStyledAttributes.getColor(index, 0));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundingBorderWidth) {
                        getRoundingParams(genericDraweeHierarchyBuilder).setBorderWidth((float) obtainStyledAttributes.getDimensionPixelSize(index, 0));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundingBorderColor) {
                        getRoundingParams(genericDraweeHierarchyBuilder).setBorderColor(obtainStyledAttributes.getColor(index, 0));
                    } else if (index == C0617R.styleable.GenericDraweeHierarchy_roundingBorderPadding) {
                        getRoundingParams(genericDraweeHierarchyBuilder).setPadding((float) obtainStyledAttributes.getDimensionPixelSize(index, 0));
                    }
                }
                obtainStyledAttributes.recycle();
                i2 = i3;
                z4 = z5;
            } catch (Throwable th) {
                obtainStyledAttributes.recycle();
                throw th;
            }
        } else {
            i = 0;
            z3 = true;
            z2 = true;
            z = true;
        }
        if (genericDraweeHierarchyBuilder.getProgressBarImage() != null && i2 > 0) {
            genericDraweeHierarchyBuilder.setProgressBarImage((Drawable) new AutoRotateDrawable(genericDraweeHierarchyBuilder.getProgressBarImage(), i2));
        }
        if (i > 0) {
            RoundingParams roundingParams = getRoundingParams(genericDraweeHierarchyBuilder);
            float f2 = z4 ? (float) i : 0.0f;
            float f3 = z3 ? (float) i : 0.0f;
            float f4 = z2 ? (float) i : 0.0f;
            if (z) {
                f = (float) i;
            }
            roundingParams.setCornersRadii(f2, f3, f4, f);
        }
        return genericDraweeHierarchyBuilder;
    }

    @ReturnsOwnership
    private static RoundingParams getRoundingParams(GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilder) {
        if (genericDraweeHierarchyBuilder.getRoundingParams() == null) {
            genericDraweeHierarchyBuilder.setRoundingParams(new RoundingParams());
        }
        return genericDraweeHierarchyBuilder.getRoundingParams();
    }

    @Nullable
    private static Drawable getDrawable(Context context, TypedArray typedArray, int i) {
        int resourceId = typedArray.getResourceId(i, 0);
        if (resourceId == 0) {
            return null;
        }
        return context.getResources().getDrawable(resourceId);
    }

    @Nullable
    private static ScaleType getScaleTypeFromXml(TypedArray typedArray, int i) {
        switch (typedArray.getInt(i, -2)) {
            case -1:
                return null;
            case 0:
                return ScaleType.FIT_XY;
            case 1:
                return ScaleType.FIT_START;
            case 2:
                return ScaleType.FIT_CENTER;
            case 3:
                return ScaleType.FIT_END;
            case 4:
                return ScaleType.CENTER;
            case 5:
                return ScaleType.CENTER_INSIDE;
            case 6:
                return ScaleType.CENTER_CROP;
            case 7:
                return ScaleType.FOCUS_CROP;
            default:
                throw new RuntimeException("XML attribute not specified!");
        }
    }
}
