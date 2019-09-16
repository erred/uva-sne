package com.facebook.react.uimanager;

import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaBaselineFunction;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaValue;
import com.facebook.yoga.YogaWrap;
import javax.annotation.Nullable;

public interface ReactShadowNode<T extends ReactShadowNode> {
    void addChildAt(T t, int i);

    void addNativeChildAt(T t, int i);

    void calculateLayout();

    void dirty();

    boolean dispatchUpdates(float f, float f2, UIViewOperationQueue uIViewOperationQueue, NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer);

    void dispose();

    T getChildAt(int i);

    int getChildCount();

    YogaDirection getLayoutDirection();

    float getLayoutHeight();

    float getLayoutWidth();

    float getLayoutX();

    float getLayoutY();

    int getNativeChildCount();

    int getNativeOffsetForChild(T t);

    @Nullable
    T getNativeParent();

    float getPadding(int i);

    @Nullable
    T getParent();

    int getReactTag();

    T getRootNode();

    int getScreenHeight();

    int getScreenWidth();

    int getScreenX();

    int getScreenY();

    YogaValue getStyleHeight();

    YogaValue getStylePadding(int i);

    YogaValue getStyleWidth();

    ThemedReactContext getThemedContext();

    int getTotalNativeChildren();

    String getViewClass();

    boolean hasNewLayout();

    boolean hasUnseenUpdates();

    boolean hasUpdates();

    int indexOf(T t);

    int indexOfNativeChild(T t);

    boolean isDescendantOf(T t);

    boolean isDirty();

    boolean isLayoutOnly();

    boolean isMeasureDefined();

    boolean isVirtual();

    boolean isVirtualAnchor();

    boolean isYogaLeafNode();

    void markLayoutSeen();

    void markUpdateSeen();

    void markUpdated();

    void onAfterUpdateTransaction();

    void onBeforeLayout();

    void onCollectExtraUpdates(UIViewOperationQueue uIViewOperationQueue);

    void removeAllNativeChildren();

    void removeAndDisposeAllChildren();

    T removeChildAt(int i);

    T removeNativeChildAt(int i);

    void setAlignContent(YogaAlign yogaAlign);

    void setAlignItems(YogaAlign yogaAlign);

    void setAlignSelf(YogaAlign yogaAlign);

    void setBaselineFunction(YogaBaselineFunction yogaBaselineFunction);

    void setBorder(int i, float f);

    void setDefaultPadding(int i, float f);

    void setDisplay(YogaDisplay yogaDisplay);

    void setFlex(float f);

    void setFlexBasis(float f);

    void setFlexBasisAuto();

    void setFlexBasisPercent(float f);

    void setFlexDirection(YogaFlexDirection yogaFlexDirection);

    void setFlexGrow(float f);

    void setFlexShrink(float f);

    void setFlexWrap(YogaWrap yogaWrap);

    void setIsLayoutOnly(boolean z);

    void setJustifyContent(YogaJustify yogaJustify);

    void setLayoutDirection(YogaDirection yogaDirection);

    void setLocalData(Object obj);

    void setMargin(int i, float f);

    void setMarginAuto(int i);

    void setMarginPercent(int i, float f);

    void setMeasureFunction(YogaMeasureFunction yogaMeasureFunction);

    void setOverflow(YogaOverflow yogaOverflow);

    void setPadding(int i, float f);

    void setPaddingPercent(int i, float f);

    void setPosition(int i, float f);

    void setPositionPercent(int i, float f);

    void setPositionType(YogaPositionType yogaPositionType);

    void setReactTag(int i);

    void setRootNode(T t);

    void setShouldNotifyOnLayout(boolean z);

    void setStyleAspectRatio(float f);

    void setStyleHeight(float f);

    void setStyleHeightAuto();

    void setStyleHeightPercent(float f);

    void setStyleMaxHeight(float f);

    void setStyleMaxHeightPercent(float f);

    void setStyleMaxWidth(float f);

    void setStyleMaxWidthPercent(float f);

    void setStyleMinHeight(float f);

    void setStyleMinHeightPercent(float f);

    void setStyleMinWidth(float f);

    void setStyleMinWidthPercent(float f);

    void setStyleWidth(float f);

    void setStyleWidthAuto();

    void setStyleWidthPercent(float f);

    void setThemedContext(ThemedReactContext themedReactContext);

    void setViewClassName(String str);

    boolean shouldNotifyOnLayout();

    void updateProperties(ReactStylesDiffMap reactStylesDiffMap);
}
