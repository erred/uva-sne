package com.facebook.react.flat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.infer.annotation.Assertions;
import javax.annotation.Nullable;

final class DraweeRequestHelper {
    private static AbstractDraweeControllerBuilder sControllerBuilder;
    private static GenericDraweeHierarchyBuilder sHierarchyBuilder;
    private int mAttachCounter;
    private final DraweeController mDraweeController;

    static void setResources(Resources resources) {
        sHierarchyBuilder = new GenericDraweeHierarchyBuilder(resources);
    }

    static void setDraweeControllerBuilder(AbstractDraweeControllerBuilder abstractDraweeControllerBuilder) {
        sControllerBuilder = abstractDraweeControllerBuilder;
    }

    DraweeRequestHelper(ImageRequest imageRequest, @Nullable ImageRequest imageRequest2, ControllerListener controllerListener) {
        AbstractDraweeControllerBuilder controllerListener2 = sControllerBuilder.setImageRequest(imageRequest).setCallerContext(RCTImageView.getCallerContext()).setControllerListener(controllerListener);
        if (imageRequest2 != null) {
            controllerListener2.setLowResImageRequest(imageRequest2);
        }
        AbstractDraweeController build = controllerListener2.build();
        build.setHierarchy(sHierarchyBuilder.build());
        this.mDraweeController = build;
    }

    /* access modifiers changed from: 0000 */
    public void attach(InvalidateCallback invalidateCallback) {
        this.mAttachCounter++;
        if (this.mAttachCounter == 1) {
            getDrawable().setCallback((Callback) invalidateCallback.get());
            this.mDraweeController.onAttach();
        }
    }

    /* access modifiers changed from: 0000 */
    public void detach() {
        this.mAttachCounter--;
        if (this.mAttachCounter == 0) {
            this.mDraweeController.onDetach();
        }
    }

    /* access modifiers changed from: 0000 */
    public GenericDraweeHierarchy getHierarchy() {
        return (GenericDraweeHierarchy) Assertions.assumeNotNull(this.mDraweeController.getHierarchy());
    }

    /* access modifiers changed from: 0000 */
    public Drawable getDrawable() {
        return getHierarchy().getTopLevelDrawable();
    }
}
