package com.facebook.react.uimanager;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.uimanager.annotations.ReactPropertyHolder;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaBaselineFunction;
import com.facebook.yoga.YogaConfig;
import com.facebook.yoga.YogaConstants;
import com.facebook.yoga.YogaDirection;
import com.facebook.yoga.YogaDisplay;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaFlexDirection;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaMeasureFunction;
import com.facebook.yoga.YogaNode;
import com.facebook.yoga.YogaOverflow;
import com.facebook.yoga.YogaPositionType;
import com.facebook.yoga.YogaValue;
import com.facebook.yoga.YogaWrap;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

@ReactPropertyHolder
public class ReactShadowNodeImpl implements ReactShadowNode<ReactShadowNodeImpl> {
    private static YogaConfig sYogaConfig;
    @Nullable
    private ArrayList<ReactShadowNodeImpl> mChildren;
    private final Spacing mDefaultPadding = new Spacing(0.0f);
    private boolean mIsLayoutOnly;
    @Nullable
    private ArrayList<ReactShadowNodeImpl> mNativeChildren;
    @Nullable
    private ReactShadowNodeImpl mNativeParent;
    private boolean mNodeUpdated = true;
    private final float[] mPadding = new float[9];
    private final boolean[] mPaddingIsPercent = new boolean[9];
    @Nullable
    private ReactShadowNodeImpl mParent;
    private int mReactTag;
    @Nullable
    private ReactShadowNodeImpl mRootNode;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mScreenX;
    private int mScreenY;
    private boolean mShouldNotifyOnLayout;
    @Nullable
    private ThemedReactContext mThemedContext;
    private int mTotalNativeChildren = 0;
    @Nullable
    private String mViewClassName;
    private final YogaNode mYogaNode;

    public boolean isVirtual() {
        return false;
    }

    public boolean isVirtualAnchor() {
        return false;
    }

    public void onAfterUpdateTransaction() {
    }

    public void onBeforeLayout() {
    }

    public void onCollectExtraUpdates(UIViewOperationQueue uIViewOperationQueue) {
    }

    public void setLocalData(Object obj) {
    }

    public ReactShadowNodeImpl() {
        if (!isVirtual()) {
            YogaNode yogaNode = (YogaNode) YogaNodePool.get().acquire();
            if (sYogaConfig == null) {
                sYogaConfig = new YogaConfig();
                sYogaConfig.setPointScaleFactor(0.0f);
                sYogaConfig.setUseLegacyStretchBehaviour(true);
            }
            if (yogaNode == null) {
                yogaNode = new YogaNode(sYogaConfig);
            }
            this.mYogaNode = yogaNode;
            Arrays.fill(this.mPadding, Float.NaN);
            return;
        }
        this.mYogaNode = null;
    }

    public boolean isYogaLeafNode() {
        return isMeasureDefined();
    }

    public final String getViewClass() {
        return (String) Assertions.assertNotNull(this.mViewClassName);
    }

    public final boolean hasUpdates() {
        return this.mNodeUpdated || hasNewLayout() || isDirty();
    }

    public final void markUpdateSeen() {
        this.mNodeUpdated = false;
        if (hasNewLayout()) {
            markLayoutSeen();
        }
    }

    public void markUpdated() {
        if (!this.mNodeUpdated) {
            this.mNodeUpdated = true;
            ReactShadowNodeImpl parent = getParent();
            if (parent != null) {
                parent.markUpdated();
            }
        }
    }

    public final boolean hasUnseenUpdates() {
        return this.mNodeUpdated;
    }

    public void dirty() {
        if (!isVirtual()) {
            this.mYogaNode.dirty();
        }
    }

    public final boolean isDirty() {
        return this.mYogaNode != null && this.mYogaNode.isDirty();
    }

    public void addChildAt(ReactShadowNodeImpl reactShadowNodeImpl, int i) {
        if (reactShadowNodeImpl.getParent() != null) {
            throw new IllegalViewOperationException("Tried to add child that already has a parent! Remove it from its parent first.");
        }
        if (this.mChildren == null) {
            this.mChildren = new ArrayList<>(4);
        }
        this.mChildren.add(i, reactShadowNodeImpl);
        reactShadowNodeImpl.mParent = this;
        if (this.mYogaNode != null && !isYogaLeafNode()) {
            YogaNode yogaNode = reactShadowNodeImpl.mYogaNode;
            if (yogaNode == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot add a child that doesn't have a YogaNode to a parent without a measure function! (Trying to add a '");
                sb.append(reactShadowNodeImpl.getClass().getSimpleName());
                sb.append("' to a '");
                sb.append(getClass().getSimpleName());
                sb.append("')");
                throw new RuntimeException(sb.toString());
            }
            this.mYogaNode.addChildAt(yogaNode, i);
        }
        markUpdated();
        int totalNativeChildren = reactShadowNodeImpl.isLayoutOnly() ? reactShadowNodeImpl.getTotalNativeChildren() : 1;
        this.mTotalNativeChildren += totalNativeChildren;
        updateNativeChildrenCountInParent(totalNativeChildren);
    }

    public ReactShadowNodeImpl removeChildAt(int i) {
        if (this.mChildren == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Index ");
            sb.append(i);
            sb.append(" out of bounds: node has no children");
            throw new ArrayIndexOutOfBoundsException(sb.toString());
        }
        ReactShadowNodeImpl reactShadowNodeImpl = (ReactShadowNodeImpl) this.mChildren.remove(i);
        reactShadowNodeImpl.mParent = null;
        if (this.mYogaNode != null && !isYogaLeafNode()) {
            this.mYogaNode.removeChildAt(i);
        }
        markUpdated();
        int totalNativeChildren = reactShadowNodeImpl.isLayoutOnly() ? reactShadowNodeImpl.getTotalNativeChildren() : 1;
        this.mTotalNativeChildren -= totalNativeChildren;
        updateNativeChildrenCountInParent(-totalNativeChildren);
        return reactShadowNodeImpl;
    }

    public final int getChildCount() {
        if (this.mChildren == null) {
            return 0;
        }
        return this.mChildren.size();
    }

    public final ReactShadowNodeImpl getChildAt(int i) {
        if (this.mChildren != null) {
            return (ReactShadowNodeImpl) this.mChildren.get(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Index ");
        sb.append(i);
        sb.append(" out of bounds: node has no children");
        throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    public final int indexOf(ReactShadowNodeImpl reactShadowNodeImpl) {
        if (this.mChildren == null) {
            return -1;
        }
        return this.mChildren.indexOf(reactShadowNodeImpl);
    }

    public void removeAndDisposeAllChildren() {
        if (getChildCount() != 0) {
            int i = 0;
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                if (this.mYogaNode != null && !isYogaLeafNode()) {
                    this.mYogaNode.removeChildAt(childCount);
                }
                ReactShadowNodeImpl childAt = getChildAt(childCount);
                childAt.mParent = null;
                childAt.dispose();
                i += childAt.isLayoutOnly() ? childAt.getTotalNativeChildren() : 1;
            }
            ((ArrayList) Assertions.assertNotNull(this.mChildren)).clear();
            markUpdated();
            this.mTotalNativeChildren -= i;
            updateNativeChildrenCountInParent(-i);
        }
    }

    private void updateNativeChildrenCountInParent(int i) {
        if (this.mIsLayoutOnly) {
            ReactShadowNodeImpl parent = getParent();
            while (parent != null) {
                parent.mTotalNativeChildren += i;
                if (parent.isLayoutOnly()) {
                    parent = parent.getParent();
                } else {
                    return;
                }
            }
        }
    }

    public final void updateProperties(ReactStylesDiffMap reactStylesDiffMap) {
        ViewManagerPropertyUpdater.updateProps(this, reactStylesDiffMap);
        onAfterUpdateTransaction();
    }

    public boolean dispatchUpdates(float f, float f2, UIViewOperationQueue uIViewOperationQueue, NativeViewHierarchyOptimizer nativeViewHierarchyOptimizer) {
        if (this.mNodeUpdated) {
            onCollectExtraUpdates(uIViewOperationQueue);
        }
        boolean z = false;
        if (!hasNewLayout()) {
            return false;
        }
        float layoutX = getLayoutX();
        float layoutY = getLayoutY();
        float f3 = f + layoutX;
        int round = Math.round(f3);
        float f4 = f2 + layoutY;
        int round2 = Math.round(f4);
        int round3 = Math.round(f3 + getLayoutWidth());
        int round4 = Math.round(f4 + getLayoutHeight());
        int round5 = Math.round(layoutX);
        int round6 = Math.round(layoutY);
        int i = round3 - round;
        int i2 = round4 - round2;
        if (!(round5 == this.mScreenX && round6 == this.mScreenY && i == this.mScreenWidth && i2 == this.mScreenHeight)) {
            z = true;
        }
        this.mScreenX = round5;
        this.mScreenY = round6;
        this.mScreenWidth = i;
        this.mScreenHeight = i2;
        if (z) {
            nativeViewHierarchyOptimizer.handleUpdateLayout(this);
        }
        return z;
    }

    public final int getReactTag() {
        return this.mReactTag;
    }

    public void setReactTag(int i) {
        this.mReactTag = i;
    }

    public final ReactShadowNodeImpl getRootNode() {
        return (ReactShadowNodeImpl) Assertions.assertNotNull(this.mRootNode);
    }

    public final void setRootNode(ReactShadowNodeImpl reactShadowNodeImpl) {
        this.mRootNode = reactShadowNodeImpl;
    }

    public final void setViewClassName(String str) {
        this.mViewClassName = str;
    }

    @Nullable
    public final ReactShadowNodeImpl getParent() {
        return this.mParent;
    }

    public final ThemedReactContext getThemedContext() {
        return (ThemedReactContext) Assertions.assertNotNull(this.mThemedContext);
    }

    public void setThemedContext(ThemedReactContext themedReactContext) {
        this.mThemedContext = themedReactContext;
    }

    public final boolean shouldNotifyOnLayout() {
        return this.mShouldNotifyOnLayout;
    }

    public void calculateLayout() {
        this.mYogaNode.calculateLayout(Float.NaN, Float.NaN);
    }

    public final boolean hasNewLayout() {
        return this.mYogaNode != null && this.mYogaNode.hasNewLayout();
    }

    public final void markLayoutSeen() {
        if (this.mYogaNode != null) {
            this.mYogaNode.markLayoutSeen();
        }
    }

    public final void addNativeChildAt(ReactShadowNodeImpl reactShadowNodeImpl, int i) {
        Assertions.assertCondition(!this.mIsLayoutOnly);
        Assertions.assertCondition(!reactShadowNodeImpl.mIsLayoutOnly);
        if (this.mNativeChildren == null) {
            this.mNativeChildren = new ArrayList<>(4);
        }
        this.mNativeChildren.add(i, reactShadowNodeImpl);
        reactShadowNodeImpl.mNativeParent = this;
    }

    public final ReactShadowNodeImpl removeNativeChildAt(int i) {
        Assertions.assertNotNull(this.mNativeChildren);
        ReactShadowNodeImpl reactShadowNodeImpl = (ReactShadowNodeImpl) this.mNativeChildren.remove(i);
        reactShadowNodeImpl.mNativeParent = null;
        return reactShadowNodeImpl;
    }

    public final void removeAllNativeChildren() {
        if (this.mNativeChildren != null) {
            for (int size = this.mNativeChildren.size() - 1; size >= 0; size--) {
                ((ReactShadowNodeImpl) this.mNativeChildren.get(size)).mNativeParent = null;
            }
            this.mNativeChildren.clear();
        }
    }

    public final int getNativeChildCount() {
        if (this.mNativeChildren == null) {
            return 0;
        }
        return this.mNativeChildren.size();
    }

    public final int indexOfNativeChild(ReactShadowNodeImpl reactShadowNodeImpl) {
        Assertions.assertNotNull(this.mNativeChildren);
        return this.mNativeChildren.indexOf(reactShadowNodeImpl);
    }

    @Nullable
    public final ReactShadowNodeImpl getNativeParent() {
        return this.mNativeParent;
    }

    public final void setIsLayoutOnly(boolean z) {
        boolean z2 = false;
        Assertions.assertCondition(getParent() == null, "Must remove from no opt parent first");
        Assertions.assertCondition(this.mNativeParent == null, "Must remove from native parent first");
        if (getNativeChildCount() == 0) {
            z2 = true;
        }
        Assertions.assertCondition(z2, "Must remove all native children first");
        this.mIsLayoutOnly = z;
    }

    public final boolean isLayoutOnly() {
        return this.mIsLayoutOnly;
    }

    public final int getTotalNativeChildren() {
        return this.mTotalNativeChildren;
    }

    public boolean isDescendantOf(ReactShadowNodeImpl reactShadowNodeImpl) {
        for (ReactShadowNodeImpl parent = getParent(); parent != null; parent = parent.getParent()) {
            if (parent == reactShadowNodeImpl) {
                return true;
            }
        }
        return false;
    }

    public final int getNativeOffsetForChild(ReactShadowNodeImpl reactShadowNodeImpl) {
        boolean z = false;
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = 1;
            if (i >= getChildCount()) {
                break;
            }
            ReactShadowNodeImpl childAt = getChildAt(i);
            if (reactShadowNodeImpl == childAt) {
                z = true;
                break;
            }
            if (childAt.isLayoutOnly()) {
                i3 = childAt.getTotalNativeChildren();
            }
            i2 += i3;
            i++;
        }
        if (z) {
            return i2;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Child ");
        sb.append(reactShadowNodeImpl.getReactTag());
        sb.append(" was not a child of ");
        sb.append(this.mReactTag);
        throw new RuntimeException(sb.toString());
    }

    public final float getLayoutX() {
        return this.mYogaNode.getLayoutX();
    }

    public final float getLayoutY() {
        return this.mYogaNode.getLayoutY();
    }

    public final float getLayoutWidth() {
        return this.mYogaNode.getLayoutWidth();
    }

    public final float getLayoutHeight() {
        return this.mYogaNode.getLayoutHeight();
    }

    public int getScreenX() {
        return this.mScreenX;
    }

    public int getScreenY() {
        return this.mScreenY;
    }

    public int getScreenWidth() {
        return this.mScreenWidth;
    }

    public int getScreenHeight() {
        return this.mScreenHeight;
    }

    public final YogaDirection getLayoutDirection() {
        return this.mYogaNode.getLayoutDirection();
    }

    public void setLayoutDirection(YogaDirection yogaDirection) {
        this.mYogaNode.setDirection(yogaDirection);
    }

    public final YogaValue getStyleWidth() {
        return this.mYogaNode.getWidth();
    }

    public void setStyleWidth(float f) {
        this.mYogaNode.setWidth(f);
    }

    public void setStyleWidthPercent(float f) {
        this.mYogaNode.setWidthPercent(f);
    }

    public void setStyleWidthAuto() {
        this.mYogaNode.setWidthAuto();
    }

    public void setStyleMinWidth(float f) {
        this.mYogaNode.setMinWidth(f);
    }

    public void setStyleMinWidthPercent(float f) {
        this.mYogaNode.setMinWidthPercent(f);
    }

    public void setStyleMaxWidth(float f) {
        this.mYogaNode.setMaxWidth(f);
    }

    public void setStyleMaxWidthPercent(float f) {
        this.mYogaNode.setMaxWidthPercent(f);
    }

    public final YogaValue getStyleHeight() {
        return this.mYogaNode.getHeight();
    }

    public void setStyleHeight(float f) {
        this.mYogaNode.setHeight(f);
    }

    public void setStyleHeightPercent(float f) {
        this.mYogaNode.setHeightPercent(f);
    }

    public void setStyleHeightAuto() {
        this.mYogaNode.setHeightAuto();
    }

    public void setStyleMinHeight(float f) {
        this.mYogaNode.setMinHeight(f);
    }

    public void setStyleMinHeightPercent(float f) {
        this.mYogaNode.setMinHeightPercent(f);
    }

    public void setStyleMaxHeight(float f) {
        this.mYogaNode.setMaxHeight(f);
    }

    public void setStyleMaxHeightPercent(float f) {
        this.mYogaNode.setMaxHeightPercent(f);
    }

    public void setFlex(float f) {
        this.mYogaNode.setFlex(f);
    }

    public void setFlexGrow(float f) {
        this.mYogaNode.setFlexGrow(f);
    }

    public void setFlexShrink(float f) {
        this.mYogaNode.setFlexShrink(f);
    }

    public void setFlexBasis(float f) {
        this.mYogaNode.setFlexBasis(f);
    }

    public void setFlexBasisAuto() {
        this.mYogaNode.setFlexBasisAuto();
    }

    public void setFlexBasisPercent(float f) {
        this.mYogaNode.setFlexBasisPercent(f);
    }

    public void setStyleAspectRatio(float f) {
        this.mYogaNode.setAspectRatio(f);
    }

    public void setFlexDirection(YogaFlexDirection yogaFlexDirection) {
        this.mYogaNode.setFlexDirection(yogaFlexDirection);
    }

    public void setFlexWrap(YogaWrap yogaWrap) {
        this.mYogaNode.setWrap(yogaWrap);
    }

    public void setAlignSelf(YogaAlign yogaAlign) {
        this.mYogaNode.setAlignSelf(yogaAlign);
    }

    public void setAlignItems(YogaAlign yogaAlign) {
        this.mYogaNode.setAlignItems(yogaAlign);
    }

    public void setAlignContent(YogaAlign yogaAlign) {
        this.mYogaNode.setAlignContent(yogaAlign);
    }

    public void setJustifyContent(YogaJustify yogaJustify) {
        this.mYogaNode.setJustifyContent(yogaJustify);
    }

    public void setOverflow(YogaOverflow yogaOverflow) {
        this.mYogaNode.setOverflow(yogaOverflow);
    }

    public void setDisplay(YogaDisplay yogaDisplay) {
        this.mYogaNode.setDisplay(yogaDisplay);
    }

    public void setMargin(int i, float f) {
        this.mYogaNode.setMargin(YogaEdge.fromInt(i), f);
    }

    public void setMarginPercent(int i, float f) {
        this.mYogaNode.setMarginPercent(YogaEdge.fromInt(i), f);
    }

    public void setMarginAuto(int i) {
        this.mYogaNode.setMarginAuto(YogaEdge.fromInt(i));
    }

    public final float getPadding(int i) {
        return this.mYogaNode.getLayoutPadding(YogaEdge.fromInt(i));
    }

    public final YogaValue getStylePadding(int i) {
        return this.mYogaNode.getPadding(YogaEdge.fromInt(i));
    }

    public void setDefaultPadding(int i, float f) {
        this.mDefaultPadding.set(i, f);
        updatePadding();
    }

    public void setPadding(int i, float f) {
        this.mPadding[i] = f;
        this.mPaddingIsPercent[i] = false;
        updatePadding();
    }

    public void setPaddingPercent(int i, float f) {
        this.mPadding[i] = f;
        this.mPaddingIsPercent[i] = !YogaConstants.isUndefined(f);
        updatePadding();
    }

    private void updatePadding() {
        for (int i = 0; i <= 8; i++) {
            if (i == 0 || i == 2 || i == 4 || i == 5) {
                if (YogaConstants.isUndefined(this.mPadding[i]) && YogaConstants.isUndefined(this.mPadding[6]) && YogaConstants.isUndefined(this.mPadding[8])) {
                    this.mYogaNode.setPadding(YogaEdge.fromInt(i), this.mDefaultPadding.getRaw(i));
                }
            } else if (i == 1 || i == 3) {
                if (YogaConstants.isUndefined(this.mPadding[i]) && YogaConstants.isUndefined(this.mPadding[7]) && YogaConstants.isUndefined(this.mPadding[8])) {
                    this.mYogaNode.setPadding(YogaEdge.fromInt(i), this.mDefaultPadding.getRaw(i));
                }
            } else if (YogaConstants.isUndefined(this.mPadding[i])) {
                this.mYogaNode.setPadding(YogaEdge.fromInt(i), this.mDefaultPadding.getRaw(i));
            }
            if (this.mPaddingIsPercent[i]) {
                this.mYogaNode.setPaddingPercent(YogaEdge.fromInt(i), this.mPadding[i]);
            } else {
                this.mYogaNode.setPadding(YogaEdge.fromInt(i), this.mPadding[i]);
            }
        }
    }

    public void setBorder(int i, float f) {
        this.mYogaNode.setBorder(YogaEdge.fromInt(i), f);
    }

    public void setPosition(int i, float f) {
        this.mYogaNode.setPosition(YogaEdge.fromInt(i), f);
    }

    public void setPositionPercent(int i, float f) {
        this.mYogaNode.setPositionPercent(YogaEdge.fromInt(i), f);
    }

    public void setPositionType(YogaPositionType yogaPositionType) {
        this.mYogaNode.setPositionType(yogaPositionType);
    }

    public void setShouldNotifyOnLayout(boolean z) {
        this.mShouldNotifyOnLayout = z;
    }

    public void setBaselineFunction(YogaBaselineFunction yogaBaselineFunction) {
        this.mYogaNode.setBaselineFunction(yogaBaselineFunction);
    }

    public void setMeasureFunction(YogaMeasureFunction yogaMeasureFunction) {
        if (!((yogaMeasureFunction == null) ^ this.mYogaNode.isMeasureDefined()) || getChildCount() == 0) {
            this.mYogaNode.setMeasureFunction(yogaMeasureFunction);
            return;
        }
        throw new RuntimeException("Since a node with a measure function does not add any native yoga children, it's not safe to transition to/from having a measure function unless a node has no children");
    }

    public boolean isMeasureDefined() {
        return this.mYogaNode.isMeasureDefined();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringWithIndentation(sb, 0);
        return sb.toString();
    }

    private void toStringWithIndentation(StringBuilder sb, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            sb.append("__");
        }
        sb.append(getClass().getSimpleName());
        sb.append(StringUtils.SPACE);
        if (this.mYogaNode != null) {
            sb.append(getLayoutWidth());
            sb.append(",");
            sb.append(getLayoutHeight());
        } else {
            sb.append("(virtual node)");
        }
        sb.append(StringUtils.f158LF);
        if (getChildCount() != 0) {
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                getChildAt(i3).toStringWithIndentation(sb, i + 1);
            }
        }
    }

    public void dispose() {
        if (this.mYogaNode != null) {
            this.mYogaNode.reset();
            YogaNodePool.get().release(this.mYogaNode);
        }
    }
}
