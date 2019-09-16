package com.facebook.react.flat;

import android.graphics.Rect;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.OnLayoutEvent;
import com.facebook.react.uimanager.ReactShadowNodeImpl;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import javax.annotation.Nullable;

class FlatShadowNode extends LayoutShadowNode {
    static final FlatShadowNode[] EMPTY_ARRAY = new FlatShadowNode[0];
    private static final DrawView EMPTY_DRAW_VIEW = new DrawView(0);
    private static final Rect LOGICAL_OFFSET_EMPTY = new Rect();
    private static final String PROP_ACCESSIBILITY_COMPONENT_TYPE = "accessibilityComponentType";
    private static final String PROP_ACCESSIBILITY_LABEL = "accessibilityLabel";
    private static final String PROP_ACCESSIBILITY_LIVE_REGION = "accessibilityLiveRegion";
    protected static final String PROP_HORIZONTAL = "horizontal";
    private static final String PROP_IMPORTANT_FOR_ACCESSIBILITY = "importantForAccessibility";
    private static final String PROP_OPACITY = "opacity";
    protected static final String PROP_REMOVE_CLIPPED_SUBVIEWS = "removeClippedSubviews";
    private static final String PROP_RENDER_TO_HARDWARE_TEXTURE = "renderToHardwareTextureAndroid";
    private static final String PROP_TEST_ID = "testID";
    private static final String PROP_TRANSFORM = "transform";
    private AttachDetachListener[] mAttachDetachListeners = AttachDetachListener.EMPTY_ARRAY;
    private boolean mBackingViewIsCreated;
    private float mClipBottom;
    private float mClipLeft;
    float mClipRadius;
    private float mClipRight;
    boolean mClipToBounds = false;
    private float mClipTop;
    @Nullable
    private DrawBackgroundColor mDrawBackground;
    private DrawCommand[] mDrawCommands = DrawCommand.EMPTY_ARRAY;
    @Nullable
    private DrawView mDrawView;
    private boolean mForceMountChildrenToView;
    private boolean mIsUpdated = true;
    private int mLayoutHeight;
    private int mLayoutWidth;
    private int mLayoutX;
    private int mLayoutY;
    private Rect mLogicalOffset = LOGICAL_OFFSET_EMPTY;
    private FlatShadowNode[] mNativeChildren = EMPTY_ARRAY;
    private int mNativeParentTag;
    private NodeRegion mNodeRegion = NodeRegion.EMPTY;
    private NodeRegion[] mNodeRegions = NodeRegion.EMPTY_ARRAY;
    private boolean mOverflowsContainer;
    private int mViewBottom;
    private int mViewLeft;
    private int mViewRight;
    private int mViewTop;

    public boolean clipsSubviews() {
        return false;
    }

    public boolean isHorizontal() {
        return false;
    }

    FlatShadowNode() {
    }

    /* access modifiers changed from: 0000 */
    public void handleUpdateProperties(ReactStylesDiffMap reactStylesDiffMap) {
        if (mountsToView()) {
            return;
        }
        if (reactStylesDiffMap.hasKey("opacity") || reactStylesDiffMap.hasKey(PROP_RENDER_TO_HARDWARE_TEXTURE) || reactStylesDiffMap.hasKey("testID") || reactStylesDiffMap.hasKey(PROP_ACCESSIBILITY_LABEL) || reactStylesDiffMap.hasKey(PROP_ACCESSIBILITY_COMPONENT_TYPE) || reactStylesDiffMap.hasKey(PROP_ACCESSIBILITY_LIVE_REGION) || reactStylesDiffMap.hasKey(PROP_TRANSFORM) || reactStylesDiffMap.hasKey(PROP_IMPORTANT_FOR_ACCESSIBILITY) || reactStylesDiffMap.hasKey("removeClippedSubviews")) {
            forceMountToView();
        }
    }

    /* access modifiers changed from: 0000 */
    public final void forceMountChildrenToView() {
        if (!this.mForceMountChildrenToView) {
            this.mForceMountChildrenToView = true;
            int childCount = getChildCount();
            for (int i = 0; i != childCount; i++) {
                ReactShadowNodeImpl childAt = getChildAt(i);
                if (childAt instanceof FlatShadowNode) {
                    ((FlatShadowNode) childAt).forceMountToView();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void collectState(StateBuilder stateBuilder, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        if (this.mDrawBackground != null) {
            this.mDrawBackground = (DrawBackgroundColor) this.mDrawBackground.updateBoundsAndFreeze(f, f2, f3, f4, f5, f6, f7, f8);
            stateBuilder.addDrawCommand(this.mDrawBackground);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean doesDraw() {
        return (this.mDrawView == null && this.mDrawBackground == null) ? false : true;
    }

    @ReactProp(name = "backgroundColor")
    public void setBackgroundColor(int i) {
        this.mDrawBackground = i == 0 ? null : new DrawBackgroundColor(i);
        invalidate();
    }

    public void setOverflow(String str) {
        super.setOverflow(str);
        this.mClipToBounds = User.PRESENCE_HIDDEN.equals(str);
        if (this.mClipToBounds) {
            this.mOverflowsContainer = false;
            if (this.mClipRadius > 0.5f) {
                forceMountToView();
            }
        } else {
            updateOverflowsContainer();
        }
        invalidate();
    }

    public final boolean clipToBounds() {
        return this.mClipToBounds;
    }

    public final int getScreenX() {
        return this.mViewLeft;
    }

    public final int getScreenY() {
        return this.mViewTop;
    }

    public final int getScreenWidth() {
        if (mountsToView()) {
            return this.mViewRight - this.mViewLeft;
        }
        return Math.round(this.mNodeRegion.getRight() - this.mNodeRegion.getLeft());
    }

    public final int getScreenHeight() {
        if (mountsToView()) {
            return this.mViewBottom - this.mViewTop;
        }
        return Math.round(this.mNodeRegion.getBottom() - this.mNodeRegion.getTop());
    }

    public void addChildAt(ReactShadowNodeImpl reactShadowNodeImpl, int i) {
        super.addChildAt(reactShadowNodeImpl, i);
        if (this.mForceMountChildrenToView && (reactShadowNodeImpl instanceof FlatShadowNode)) {
            ((FlatShadowNode) reactShadowNodeImpl).forceMountToView();
        }
    }

    /* access modifiers changed from: protected */
    public final void invalidate() {
        FlatShadowNode flatShadowNode = this;
        while (true) {
            if (flatShadowNode.mountsToView()) {
                if (!flatShadowNode.mIsUpdated) {
                    flatShadowNode.mIsUpdated = true;
                } else {
                    return;
                }
            }
            ReactShadowNodeImpl parent = flatShadowNode.getParent();
            if (parent != null) {
                flatShadowNode = (FlatShadowNode) parent;
            } else {
                return;
            }
        }
    }

    public void markUpdated() {
        super.markUpdated();
        this.mIsUpdated = true;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public final boolean isUpdated() {
        return this.mIsUpdated;
    }

    /* access modifiers changed from: 0000 */
    public final void resetUpdated() {
        this.mIsUpdated = false;
    }

    /* access modifiers changed from: 0000 */
    public final boolean clipBoundsChanged(float f, float f2, float f3, float f4) {
        return (this.mClipLeft == f && this.mClipTop == f2 && this.mClipRight == f3 && this.mClipBottom == f4) ? false : true;
    }

    /* access modifiers changed from: 0000 */
    public final void setClipBounds(float f, float f2, float f3, float f4) {
        this.mClipLeft = f;
        this.mClipTop = f2;
        this.mClipRight = f3;
        this.mClipBottom = f4;
    }

    /* access modifiers changed from: 0000 */
    public final DrawCommand[] getDrawCommands() {
        return this.mDrawCommands;
    }

    /* access modifiers changed from: 0000 */
    public final void setDrawCommands(DrawCommand[] drawCommandArr) {
        this.mDrawCommands = drawCommandArr;
    }

    /* access modifiers changed from: 0000 */
    public final void setAttachDetachListeners(AttachDetachListener[] attachDetachListenerArr) {
        this.mAttachDetachListeners = attachDetachListenerArr;
    }

    /* access modifiers changed from: 0000 */
    public final AttachDetachListener[] getAttachDetachListeners() {
        return this.mAttachDetachListeners;
    }

    /* access modifiers changed from: 0000 */
    public final FlatShadowNode[] getNativeChildren() {
        return this.mNativeChildren;
    }

    /* access modifiers changed from: 0000 */
    public final void setNativeChildren(FlatShadowNode[] flatShadowNodeArr) {
        this.mNativeChildren = flatShadowNodeArr;
    }

    /* access modifiers changed from: 0000 */
    public final int getNativeParentTag() {
        return this.mNativeParentTag;
    }

    /* access modifiers changed from: 0000 */
    public final void setNativeParentTag(int i) {
        this.mNativeParentTag = i;
    }

    /* access modifiers changed from: 0000 */
    public final NodeRegion[] getNodeRegions() {
        return this.mNodeRegions;
    }

    /* access modifiers changed from: 0000 */
    public final void setNodeRegions(NodeRegion[] nodeRegionArr) {
        this.mNodeRegions = nodeRegionArr;
        updateOverflowsContainer();
    }

    /* access modifiers changed from: 0000 */
    public final void updateOverflowsContainer() {
        boolean z;
        NodeRegion[] nodeRegionArr;
        int right = (int) (this.mNodeRegion.getRight() - this.mNodeRegion.getLeft());
        int bottom = (int) (this.mNodeRegion.getBottom() - this.mNodeRegion.getTop());
        float f = (float) right;
        float f2 = (float) bottom;
        Rect rect = null;
        if (this.mClipToBounds || bottom <= 0 || right <= 0) {
            z = false;
        } else {
            float f3 = f;
            float f4 = f2;
            float f5 = 0.0f;
            z = false;
            float f6 = 0.0f;
            for (NodeRegion nodeRegion : this.mNodeRegions) {
                if (nodeRegion.getLeft() < f5) {
                    f5 = nodeRegion.getLeft();
                    z = true;
                }
                if (nodeRegion.getRight() > f3) {
                    f3 = nodeRegion.getRight();
                    z = true;
                }
                if (nodeRegion.getTop() < f6) {
                    f6 = nodeRegion.getTop();
                    z = true;
                }
                if (nodeRegion.getBottom() > f4) {
                    f4 = nodeRegion.getBottom();
                    z = true;
                }
            }
            if (z) {
                rect = new Rect((int) f5, (int) f6, (int) (f3 - f), (int) (f4 - f2));
            }
        }
        if (!z && this.mNodeRegion != NodeRegion.EMPTY) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                ReactShadowNodeImpl childAt = getChildAt(i);
                if (childAt instanceof FlatShadowNode) {
                    FlatShadowNode flatShadowNode = (FlatShadowNode) childAt;
                    if (flatShadowNode.mOverflowsContainer) {
                        Rect rect2 = flatShadowNode.mLogicalOffset;
                        if (rect == null) {
                            rect = new Rect();
                        }
                        rect.union(rect2);
                        z = true;
                    }
                }
            }
        }
        if (this.mOverflowsContainer != z) {
            this.mOverflowsContainer = z;
            if (rect == null) {
                rect = LOGICAL_OFFSET_EMPTY;
            }
            this.mLogicalOffset = rect;
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateNodeRegion(float f, float f2, float f3, float f4, boolean z) {
        if (!this.mNodeRegion.matches(f, f2, f3, f4, z)) {
            NodeRegion nodeRegion = new NodeRegion(f, f2, f3, f4, getReactTag(), z);
            setNodeRegion(nodeRegion);
        }
    }

    /* access modifiers changed from: protected */
    public final void setNodeRegion(NodeRegion nodeRegion) {
        this.mNodeRegion = nodeRegion;
        updateOverflowsContainer();
    }

    /* access modifiers changed from: 0000 */
    public final NodeRegion getNodeRegion() {
        return this.mNodeRegion;
    }

    /* access modifiers changed from: 0000 */
    public final void setViewBounds(int i, int i2, int i3, int i4) {
        this.mViewLeft = i;
        this.mViewTop = i2;
        this.mViewRight = i3;
        this.mViewBottom = i4;
    }

    /* access modifiers changed from: 0000 */
    public final int getViewLeft() {
        return this.mViewLeft;
    }

    /* access modifiers changed from: 0000 */
    public final int getViewTop() {
        return this.mViewTop;
    }

    /* access modifiers changed from: 0000 */
    public final int getViewRight() {
        return this.mViewRight;
    }

    /* access modifiers changed from: 0000 */
    public final int getViewBottom() {
        return this.mViewBottom;
    }

    /* access modifiers changed from: 0000 */
    public final void forceMountToView() {
        if (!isVirtual() && this.mDrawView == null) {
            this.mDrawView = EMPTY_DRAW_VIEW;
            invalidate();
            this.mNodeRegion = NodeRegion.EMPTY;
        }
    }

    /* access modifiers changed from: 0000 */
    public final DrawView collectDrawView(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        Assertions.assumeNotNull(this.mDrawView);
        if (this.mDrawView == EMPTY_DRAW_VIEW) {
            this.mDrawView = new DrawView(getReactTag());
        }
        this.mDrawView = this.mDrawView.collectDrawView(f, f2, f3, f4, f + ((float) this.mLogicalOffset.left), f2 + ((float) this.mLogicalOffset.top), f3 + ((float) this.mLogicalOffset.right), f4 + ((float) this.mLogicalOffset.bottom), f5, f6, f7, f8, this.mClipToBounds ? this.mClipRadius : 0.0f);
        return this.mDrawView;
    }

    /* access modifiers changed from: 0000 */
    @Nullable
    public final OnLayoutEvent obtainLayoutEvent(int i, int i2, int i3, int i4) {
        if (this.mLayoutX == i && this.mLayoutY == i2 && this.mLayoutWidth == i3 && this.mLayoutHeight == i4) {
            return null;
        }
        this.mLayoutX = i;
        this.mLayoutY = i2;
        this.mLayoutWidth = i3;
        this.mLayoutHeight = i4;
        return OnLayoutEvent.obtain(getReactTag(), i, i2, i3, i4);
    }

    /* access modifiers changed from: 0000 */
    public final boolean mountsToView() {
        return this.mDrawView != null;
    }

    /* access modifiers changed from: 0000 */
    public final boolean isBackingViewCreated() {
        return this.mBackingViewIsCreated;
    }

    /* access modifiers changed from: 0000 */
    public final void signalBackingViewIsCreated() {
        this.mBackingViewIsCreated = true;
    }
}
