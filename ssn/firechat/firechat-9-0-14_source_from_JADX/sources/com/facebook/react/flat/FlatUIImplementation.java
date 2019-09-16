package com.facebook.react.flat;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.modules.fresco.FrescoModule;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.ReactShadowNode;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.UIViewOperationQueue;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.text.ReactRawTextManager;
import com.facebook.react.views.text.ReactTextViewManager;
import com.facebook.react.views.text.ReactVirtualTextViewManager;
import com.facebook.yoga.YogaDirection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class FlatUIImplementation extends UIImplementation {
    private static final Map<String, Class<? extends ViewManager>> flatManagerClassMap = new HashMap();
    private final boolean mMemoryImprovementEnabled;
    private final MoveProxy mMoveProxy = new MoveProxy();
    @Nullable
    private RCTImageViewManager mRCTImageViewManager;
    private final ReactApplicationContext mReactContext;
    private final StateBuilder mStateBuilder;

    static {
        flatManagerClassMap.put("RCTView", RCTViewManager.class);
        flatManagerClassMap.put(ReactTextViewManager.REACT_CLASS, RCTTextManager.class);
        flatManagerClassMap.put(ReactRawTextManager.REACT_CLASS, RCTRawTextManager.class);
        flatManagerClassMap.put(ReactVirtualTextViewManager.REACT_CLASS, RCTVirtualTextManager.class);
        flatManagerClassMap.put("RCTTextInlineImage", RCTTextInlineImageManager.class);
        flatManagerClassMap.put("RCTImageView", RCTImageViewManager.class);
        flatManagerClassMap.put("AndroidTextInput", RCTTextInputManager.class);
        flatManagerClassMap.put("AndroidViewPager", RCTViewPagerManager.class);
        flatManagerClassMap.put("ARTSurfaceView", FlatARTSurfaceViewManager.class);
        flatManagerClassMap.put("RCTModalHostView", RCTModalHostManager.class);
    }

    private static Map<String, ViewManager> buildViewManagerMap(List<ViewManager> list) {
        HashMap hashMap = new HashMap();
        for (ViewManager viewManager : list) {
            hashMap.put(viewManager.getName(), viewManager);
        }
        for (Entry entry : flatManagerClassMap.entrySet()) {
            String str = (String) entry.getKey();
            ViewManager viewManager2 = (ViewManager) hashMap.get(str);
            if (viewManager2 != null) {
                Class cls = (Class) entry.getValue();
                if (viewManager2.getClass() != cls) {
                    try {
                        hashMap.put(str, cls.newInstance());
                    } catch (IllegalAccessException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unable to access flat class for ");
                        sb.append(str);
                        throw new RuntimeException(sb.toString(), e);
                    } catch (InstantiationException e2) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Unable to instantiate flat class for ");
                        sb2.append(str);
                        throw new RuntimeException(sb2.toString(), e2);
                    }
                }
            }
        }
        return hashMap;
    }

    public static FlatUIImplementation createInstance(ReactApplicationContext reactApplicationContext, List<ViewManager> list, EventDispatcher eventDispatcher, boolean z, int i) {
        Map buildViewManagerMap = buildViewManagerMap(list);
        RCTImageViewManager rCTImageViewManager = (RCTImageViewManager) buildViewManagerMap.get("RCTImageView");
        if (rCTImageViewManager != null) {
            Object callerContext = rCTImageViewManager.getCallerContext();
            if (callerContext != null) {
                RCTImageView.setCallerContext(callerContext);
            }
        }
        DraweeRequestHelper.setResources(reactApplicationContext.getResources());
        TypefaceCache.setAssetManager(reactApplicationContext.getAssets());
        ViewManagerRegistry viewManagerRegistry = new ViewManagerRegistry(buildViewManagerMap);
        FlatUIImplementation flatUIImplementation = new FlatUIImplementation(reactApplicationContext, rCTImageViewManager, viewManagerRegistry, new FlatUIViewOperationQueue(reactApplicationContext, new FlatNativeViewHierarchyManager(viewManagerRegistry), i), eventDispatcher, z);
        return flatUIImplementation;
    }

    private FlatUIImplementation(ReactApplicationContext reactApplicationContext, @Nullable RCTImageViewManager rCTImageViewManager, ViewManagerRegistry viewManagerRegistry, FlatUIViewOperationQueue flatUIViewOperationQueue, EventDispatcher eventDispatcher, boolean z) {
        super(reactApplicationContext, viewManagerRegistry, (UIViewOperationQueue) flatUIViewOperationQueue, eventDispatcher);
        this.mReactContext = reactApplicationContext;
        this.mRCTImageViewManager = rCTImageViewManager;
        this.mStateBuilder = new StateBuilder(flatUIViewOperationQueue);
        this.mMemoryImprovementEnabled = z;
    }

    /* access modifiers changed from: protected */
    public ReactShadowNode createRootShadowNode() {
        if (this.mRCTImageViewManager != null) {
            this.mReactContext.getNativeModule(FrescoModule.class);
            DraweeRequestHelper.setDraweeControllerBuilder(this.mRCTImageViewManager.getDraweeControllerBuilder());
            this.mRCTImageViewManager = null;
        }
        FlatRootShadowNode flatRootShadowNode = new FlatRootShadowNode();
        if (I18nUtil.getInstance().isRTL(this.mReactContext)) {
            flatRootShadowNode.setLayoutDirection(YogaDirection.RTL);
        }
        return flatRootShadowNode;
    }

    /* access modifiers changed from: protected */
    public ReactShadowNode createShadowNode(String str) {
        ReactShadowNode createShadowNode = super.createShadowNode(str);
        return ((createShadowNode instanceof FlatShadowNode) || createShadowNode.isVirtual()) ? createShadowNode : new NativeViewWrapper(resolveViewManager(str));
    }

    /* access modifiers changed from: protected */
    public void handleCreateView(ReactShadowNode reactShadowNode, int i, @Nullable ReactStylesDiffMap reactStylesDiffMap) {
        if (reactShadowNode instanceof FlatShadowNode) {
            FlatShadowNode flatShadowNode = (FlatShadowNode) reactShadowNode;
            if (reactStylesDiffMap != null) {
                flatShadowNode.handleUpdateProperties(reactStylesDiffMap);
            }
            if (flatShadowNode.mountsToView()) {
                this.mStateBuilder.enqueueCreateOrUpdateView(flatShadowNode, reactStylesDiffMap);
                return;
            }
            return;
        }
        super.handleCreateView(reactShadowNode, i, reactStylesDiffMap);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateView(ReactShadowNode reactShadowNode, String str, ReactStylesDiffMap reactStylesDiffMap) {
        if (reactShadowNode instanceof FlatShadowNode) {
            FlatShadowNode flatShadowNode = (FlatShadowNode) reactShadowNode;
            flatShadowNode.handleUpdateProperties(reactStylesDiffMap);
            if (flatShadowNode.mountsToView()) {
                this.mStateBuilder.enqueueCreateOrUpdateView(flatShadowNode, reactStylesDiffMap);
                return;
            }
            return;
        }
        super.handleUpdateView(reactShadowNode, str, reactStylesDiffMap);
    }

    public void manageChildren(int i, @Nullable ReadableArray readableArray, @Nullable ReadableArray readableArray2, @Nullable ReadableArray readableArray3, @Nullable ReadableArray readableArray4, @Nullable ReadableArray readableArray5) {
        ReactShadowNode resolveShadowNode = resolveShadowNode(i);
        removeChildren(resolveShadowNode, readableArray, readableArray2, readableArray5);
        addChildren(resolveShadowNode, readableArray3, readableArray4);
    }

    public void setChildren(int i, ReadableArray readableArray) {
        ReactShadowNode resolveShadowNode = resolveShadowNode(i);
        for (int i2 = 0; i2 < readableArray.size(); i2++) {
            addChildAt(resolveShadowNode, resolveShadowNode(readableArray.getInt(i2)), i2, i2 - 1);
        }
    }

    public void measure(int i, Callback callback) {
        measureHelper(i, false, callback);
    }

    private void measureHelper(int i, boolean z, Callback callback) {
        FlatShadowNode flatShadowNode = (FlatShadowNode) resolveShadowNode(i);
        if (flatShadowNode.mountsToView()) {
            this.mStateBuilder.ensureBackingViewIsCreated(flatShadowNode);
            if (z) {
                super.measureInWindow(i, callback);
            } else {
                super.measure(i, callback);
            }
            return;
        }
        while (flatShadowNode != null && flatShadowNode.isVirtual()) {
            flatShadowNode = (FlatShadowNode) flatShadowNode.getParent();
        }
        if (flatShadowNode != null) {
            float layoutWidth = flatShadowNode.getLayoutWidth();
            float layoutHeight = flatShadowNode.getLayoutHeight();
            boolean mountsToView = flatShadowNode.mountsToView();
            float f = 0.0f;
            float layoutX = mountsToView ? flatShadowNode.getLayoutX() : 0.0f;
            if (mountsToView) {
                f = flatShadowNode.getLayoutY();
            }
            while (!flatShadowNode.mountsToView()) {
                if (!flatShadowNode.isVirtual()) {
                    layoutX += flatShadowNode.getLayoutX();
                    f += flatShadowNode.getLayoutY();
                }
                flatShadowNode = (FlatShadowNode) Assertions.assumeNotNull((FlatShadowNode) flatShadowNode.getParent());
            }
            float layoutWidth2 = flatShadowNode.getLayoutWidth();
            float layoutHeight2 = flatShadowNode.getLayoutHeight();
            this.mStateBuilder.getOperationsQueue().enqueueMeasureVirtualView(flatShadowNode.getReactTag(), layoutX / layoutWidth2, f / layoutHeight2, layoutWidth / layoutWidth2, layoutHeight / layoutHeight2, z, callback);
        }
    }

    private void ensureMountsToViewAndBackingViewIsCreated(int i) {
        FlatShadowNode flatShadowNode = (FlatShadowNode) resolveShadowNode(i);
        if (!flatShadowNode.isBackingViewCreated()) {
            flatShadowNode.forceMountToView();
            this.mStateBuilder.ensureBackingViewIsCreated(flatShadowNode);
        }
    }

    public void findSubviewIn(int i, float f, float f2, Callback callback) {
        ensureMountsToViewAndBackingViewIsCreated(i);
        super.findSubviewIn(i, f, f2, callback);
    }

    public void measureInWindow(int i, Callback callback) {
        measureHelper(i, true, callback);
    }

    public void addAnimation(int i, int i2, Callback callback) {
        ensureMountsToViewAndBackingViewIsCreated(i);
        super.addAnimation(i, i2, callback);
    }

    public void dispatchViewManagerCommand(int i, int i2, ReadableArray readableArray) {
        ensureMountsToViewAndBackingViewIsCreated(i);
        this.mStateBuilder.enqueueViewManagerCommand(i, i2, readableArray);
    }

    public void showPopupMenu(int i, ReadableArray readableArray, Callback callback, Callback callback2) {
        ensureMountsToViewAndBackingViewIsCreated(i);
        super.showPopupMenu(i, readableArray, callback, callback2);
    }

    public void sendAccessibilityEvent(int i, int i2) {
        ensureMountsToViewAndBackingViewIsCreated(i);
        super.sendAccessibilityEvent(i, i2);
    }

    private void removeChildren(ReactShadowNode reactShadowNode, @Nullable ReadableArray readableArray, @Nullable ReadableArray readableArray2, @Nullable ReadableArray readableArray3) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        this.mMoveProxy.setup(readableArray, readableArray2);
        int size = this.mMoveProxy.size() - 1;
        if (size == -1) {
            i = -1;
        } else {
            i = this.mMoveProxy.getMoveFrom(size);
        }
        if (readableArray3 == null) {
            i2 = 0;
        } else {
            i2 = readableArray3.size();
        }
        int[] iArr = new int[i2];
        if (i2 > 0) {
            Assertions.assertNotNull(readableArray3);
            for (int i7 = 0; i7 < i2; i7++) {
                iArr[i7] = readableArray3.getInt(i7);
            }
        }
        Arrays.sort(iArr);
        int i8 = Integer.MAX_VALUE;
        if (readableArray3 == null) {
            i3 = -1;
            i4 = -1;
        } else {
            i3 = iArr.length - 1;
            i4 = iArr[i3];
        }
        while (true) {
            if (i > i4) {
                moveChild(removeChildAt(reactShadowNode, i, i8), size);
                size--;
                if (size == -1) {
                    i6 = -1;
                } else {
                    i6 = this.mMoveProxy.getMoveFrom(size);
                }
                int i9 = i6;
                i8 = i;
                i = i9;
            } else if (i4 > i) {
                removeChild(removeChildAt(reactShadowNode, i4, i8), reactShadowNode);
                i3--;
                if (i3 == -1) {
                    i5 = -1;
                } else {
                    i5 = iArr[i3];
                }
                int i10 = i4;
                i4 = i5;
                i8 = i10;
            } else {
                return;
            }
        }
    }

    private void removeChild(ReactShadowNode reactShadowNode, ReactShadowNode reactShadowNode2) {
        dropNativeViews(reactShadowNode, reactShadowNode2);
        removeShadowNode(reactShadowNode);
    }

    private void dropNativeViews(ReactShadowNode reactShadowNode, ReactShadowNode reactShadowNode2) {
        if (reactShadowNode instanceof FlatShadowNode) {
            FlatShadowNode flatShadowNode = (FlatShadowNode) reactShadowNode;
            if (flatShadowNode.mountsToView() && flatShadowNode.isBackingViewCreated()) {
                int i = -1;
                while (true) {
                    if (reactShadowNode2 == null) {
                        break;
                    }
                    if (reactShadowNode2 instanceof FlatShadowNode) {
                        FlatShadowNode flatShadowNode2 = (FlatShadowNode) reactShadowNode2;
                        if (flatShadowNode2.mountsToView() && flatShadowNode2.isBackingViewCreated() && flatShadowNode2.getParent() != null) {
                            i = flatShadowNode2.getReactTag();
                            break;
                        }
                    }
                    reactShadowNode2 = reactShadowNode2.getParent();
                }
                this.mStateBuilder.dropView(flatShadowNode, i);
                return;
            }
        }
        int childCount = reactShadowNode.getChildCount();
        for (int i2 = 0; i2 != childCount; i2++) {
            dropNativeViews(reactShadowNode.getChildAt(i2), reactShadowNode);
        }
    }

    private void moveChild(ReactShadowNode reactShadowNode, int i) {
        this.mMoveProxy.setChildMoveFrom(i, reactShadowNode);
    }

    private void addChildren(ReactShadowNode reactShadowNode, @Nullable ReadableArray readableArray, @Nullable ReadableArray readableArray2) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7 = 0;
        if (this.mMoveProxy.size() == 0) {
            i2 = Integer.MAX_VALUE;
            i = Integer.MAX_VALUE;
        } else {
            i = this.mMoveProxy.getMoveTo(0);
            i2 = 0;
        }
        int i8 = -1;
        if (readableArray2 == null) {
            i7 = Integer.MAX_VALUE;
            i4 = 0;
            i3 = Integer.MAX_VALUE;
        } else {
            i4 = readableArray2.size();
            i3 = readableArray2.getInt(0);
        }
        while (true) {
            if (i3 < i) {
                addChildAt(reactShadowNode, resolveShadowNode(readableArray.getInt(i7)), i3, i8);
                i7++;
                if (i7 == i4) {
                    i6 = Integer.MAX_VALUE;
                } else {
                    i6 = readableArray2.getInt(i7);
                }
                int i9 = i3;
                i3 = i6;
                i8 = i9;
            } else if (i < i3) {
                addChildAt(reactShadowNode, this.mMoveProxy.getChildMoveTo(i2), i, i8);
                i2++;
                if (i2 == this.mMoveProxy.size()) {
                    i5 = Integer.MAX_VALUE;
                } else {
                    i5 = this.mMoveProxy.getMoveTo(i2);
                }
                int i10 = i5;
                i8 = i;
                i = i10;
            } else {
                return;
            }
        }
    }

    private static ReactShadowNode removeChildAt(ReactShadowNode reactShadowNode, int i, int i2) {
        if (i < i2) {
            return reactShadowNode.removeChildAt(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invariant failure, needs sorting! ");
        sb.append(i);
        sb.append(" >= ");
        sb.append(i2);
        throw new RuntimeException(sb.toString());
    }

    private static void addChildAt(ReactShadowNode reactShadowNode, ReactShadowNode reactShadowNode2, int i, int i2) {
        if (i <= i2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invariant failure, needs sorting! ");
            sb.append(i);
            sb.append(" <= ");
            sb.append(i2);
            throw new RuntimeException(sb.toString());
        }
        reactShadowNode.addChildAt(reactShadowNode2, i);
    }

    /* access modifiers changed from: protected */
    public void updateViewHierarchy() {
        super.updateViewHierarchy();
        this.mStateBuilder.afterUpdateViewHierarchy(this.mEventDispatcher);
    }

    /* access modifiers changed from: protected */
    public void applyUpdatesRecursive(ReactShadowNode reactShadowNode, float f, float f2) {
        this.mStateBuilder.applyUpdates((FlatRootShadowNode) reactShadowNode);
    }

    public void removeRootView(int i) {
        if (this.mMemoryImprovementEnabled) {
            removeRootShadowNode(i);
        }
        this.mStateBuilder.removeRootView(i);
    }

    public void setJSResponder(int i, boolean z) {
        ReactShadowNode resolveShadowNode = resolveShadowNode(i);
        while (resolveShadowNode.isVirtual()) {
            resolveShadowNode = resolveShadowNode.getParent();
        }
        int reactTag = resolveShadowNode.getReactTag();
        while ((resolveShadowNode instanceof FlatShadowNode) && !((FlatShadowNode) resolveShadowNode).mountsToView()) {
            resolveShadowNode = resolveShadowNode.getParent();
        }
        FlatUIViewOperationQueue operationsQueue = this.mStateBuilder.getOperationsQueue();
        if (resolveShadowNode != null) {
            reactTag = resolveShadowNode.getReactTag();
        }
        operationsQueue.enqueueSetJSResponder(reactTag, i, z);
    }
}
