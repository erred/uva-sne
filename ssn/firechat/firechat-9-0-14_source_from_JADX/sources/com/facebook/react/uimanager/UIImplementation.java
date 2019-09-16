package com.facebook.react.uimanager;

import android.os.SystemClock;
import android.view.View.MeasureSpec;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.animation.Animation;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.UIManagerModule.ViewManagerResolver;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.yoga.YogaDirection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class UIImplementation {
    protected final EventDispatcher mEventDispatcher;
    private long mLastCalculateLayoutTime;
    private final int[] mMeasureBuffer;
    private final Set<Integer> mMeasuredRootNodes;
    private final NativeViewHierarchyOptimizer mNativeViewHierarchyOptimizer;
    private final UIViewOperationQueue mOperationsQueue;
    protected final ReactApplicationContext mReactContext;
    protected final ShadowNodeRegistry mShadowNodeRegistry;
    private final ViewManagerRegistry mViewManagers;

    public void onHostDestroy() {
    }

    public UIImplementation(ReactApplicationContext reactApplicationContext, ViewManagerResolver viewManagerResolver, EventDispatcher eventDispatcher, int i) {
        this(reactApplicationContext, new ViewManagerRegistry(viewManagerResolver), eventDispatcher, i);
    }

    public UIImplementation(ReactApplicationContext reactApplicationContext, List<ViewManager> list, EventDispatcher eventDispatcher, int i) {
        this(reactApplicationContext, new ViewManagerRegistry(list), eventDispatcher, i);
    }

    private UIImplementation(ReactApplicationContext reactApplicationContext, ViewManagerRegistry viewManagerRegistry, EventDispatcher eventDispatcher, int i) {
        this(reactApplicationContext, viewManagerRegistry, new UIViewOperationQueue(reactApplicationContext, new NativeViewHierarchyManager(viewManagerRegistry), i), eventDispatcher);
    }

    protected UIImplementation(ReactApplicationContext reactApplicationContext, ViewManagerRegistry viewManagerRegistry, UIViewOperationQueue uIViewOperationQueue, EventDispatcher eventDispatcher) {
        this.mShadowNodeRegistry = new ShadowNodeRegistry();
        this.mMeasuredRootNodes = new HashSet();
        this.mMeasureBuffer = new int[4];
        this.mLastCalculateLayoutTime = 0;
        this.mReactContext = reactApplicationContext;
        this.mViewManagers = viewManagerRegistry;
        this.mOperationsQueue = uIViewOperationQueue;
        this.mNativeViewHierarchyOptimizer = new NativeViewHierarchyOptimizer(this.mOperationsQueue, this.mShadowNodeRegistry);
        this.mEventDispatcher = eventDispatcher;
    }

    /* access modifiers changed from: protected */
    public ReactShadowNode createRootShadowNode() {
        ReactShadowNodeImpl reactShadowNodeImpl = new ReactShadowNodeImpl();
        if (I18nUtil.getInstance().isRTL(this.mReactContext)) {
            reactShadowNodeImpl.setLayoutDirection(YogaDirection.RTL);
        }
        reactShadowNodeImpl.setViewClassName("Root");
        return reactShadowNodeImpl;
    }

    /* access modifiers changed from: protected */
    public ReactShadowNode createShadowNode(String str) {
        return this.mViewManagers.get(str).createShadowNodeInstance(this.mReactContext);
    }

    /* access modifiers changed from: protected */
    public final ReactShadowNode resolveShadowNode(int i) {
        return this.mShadowNodeRegistry.getNode(i);
    }

    /* access modifiers changed from: protected */
    public final ViewManager resolveViewManager(String str) {
        return this.mViewManagers.get(str);
    }

    /* access modifiers changed from: 0000 */
    public UIViewOperationQueue getUIViewOperationQueue() {
        return this.mOperationsQueue;
    }

    public void updateRootView(int i, int i2, int i3) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Tried to update non-existent root tag: ");
            sb.append(i);
            FLog.m105w(str, sb.toString());
            return;
        }
        updateRootView(node, i2, i3);
    }

    public void updateRootView(ReactShadowNode reactShadowNode, int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        if (mode == Integer.MIN_VALUE) {
            reactShadowNode.setStyleMaxWidth((float) size);
        } else if (mode == 0) {
            reactShadowNode.setStyleWidthAuto();
        } else if (mode == 1073741824) {
            reactShadowNode.setStyleWidth((float) size);
        }
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        if (mode2 == Integer.MIN_VALUE) {
            reactShadowNode.setStyleMaxHeight((float) size2);
        } else if (mode2 == 0) {
            reactShadowNode.setStyleHeightAuto();
        } else if (mode2 == 1073741824) {
            reactShadowNode.setStyleHeight((float) size2);
        }
    }

    public <T extends SizeMonitoringFrameLayout & MeasureSpecProvider> void registerRootView(T t, int i, ThemedReactContext themedReactContext) {
        ReactShadowNode createRootShadowNode = createRootShadowNode();
        createRootShadowNode.setReactTag(i);
        createRootShadowNode.setThemedContext(themedReactContext);
        MeasureSpecProvider measureSpecProvider = (MeasureSpecProvider) t;
        updateRootView(createRootShadowNode, measureSpecProvider.getWidthMeasureSpec(), measureSpecProvider.getHeightMeasureSpec());
        this.mShadowNodeRegistry.addRootNode(createRootShadowNode);
        this.mOperationsQueue.addRootView(i, t, themedReactContext);
    }

    public void removeRootView(int i) {
        removeRootShadowNode(i);
        this.mOperationsQueue.enqueueRemoveRootView(i);
    }

    public void removeRootShadowNode(int i) {
        this.mShadowNodeRegistry.removeRootNode(i);
    }

    public void updateNodeSize(int i, int i2, int i3) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Tried to update size of non-existent tag: ");
            sb.append(i);
            FLog.m105w(str, sb.toString());
            return;
        }
        node.setStyleWidth((float) i2);
        node.setStyleHeight((float) i3);
        dispatchViewUpdatesIfNeeded();
    }

    public void setViewLocalData(int i, Object obj) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to set local data for view with unknown tag: ");
            sb.append(i);
            throw new IllegalViewOperationException(sb.toString());
        }
        node.setLocalData(obj);
        dispatchViewUpdatesIfNeeded();
    }

    public void profileNextBatch() {
        this.mOperationsQueue.profileNextBatch();
    }

    public Map<String, Long> getProfiledBatchPerfCounters() {
        return this.mOperationsQueue.getProfiledBatchPerfCounters();
    }

    public void createView(int i, String str, int i2, ReadableMap readableMap) {
        ReactStylesDiffMap reactStylesDiffMap;
        ReactShadowNode createShadowNode = createShadowNode(str);
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i2);
        createShadowNode.setReactTag(i);
        createShadowNode.setViewClassName(str);
        createShadowNode.setRootNode(node);
        createShadowNode.setThemedContext(node.getThemedContext());
        this.mShadowNodeRegistry.addNode(createShadowNode);
        if (readableMap != null) {
            reactStylesDiffMap = new ReactStylesDiffMap(readableMap);
            createShadowNode.updateProperties(reactStylesDiffMap);
        } else {
            reactStylesDiffMap = null;
        }
        handleCreateView(createShadowNode, i2, reactStylesDiffMap);
    }

    /* access modifiers changed from: protected */
    public void handleCreateView(ReactShadowNode reactShadowNode, int i, @Nullable ReactStylesDiffMap reactStylesDiffMap) {
        if (!reactShadowNode.isVirtual()) {
            this.mNativeViewHierarchyOptimizer.handleCreateView(reactShadowNode, reactShadowNode.getThemedContext(), reactStylesDiffMap);
        }
    }

    public void updateView(int i, String str, ReadableMap readableMap) {
        if (this.mViewManagers.get(str) == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Got unknown view type: ");
            sb.append(str);
            throw new IllegalViewOperationException(sb.toString());
        }
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Trying to update non-existent view with tag ");
            sb2.append(i);
            throw new IllegalViewOperationException(sb2.toString());
        } else if (readableMap != null) {
            ReactStylesDiffMap reactStylesDiffMap = new ReactStylesDiffMap(readableMap);
            node.updateProperties(reactStylesDiffMap);
            handleUpdateView(node, str, reactStylesDiffMap);
        }
    }

    public void synchronouslyUpdateViewOnUIThread(int i, ReactStylesDiffMap reactStylesDiffMap) {
        UiThreadUtil.assertOnUiThread();
        this.mOperationsQueue.getNativeViewHierarchyManager().updateProperties(i, reactStylesDiffMap);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateView(ReactShadowNode reactShadowNode, String str, ReactStylesDiffMap reactStylesDiffMap) {
        if (!reactShadowNode.isVirtual()) {
            this.mNativeViewHierarchyOptimizer.handleUpdateView(reactShadowNode, str, reactStylesDiffMap);
        }
    }

    public void manageChildren(int i, @Nullable ReadableArray readableArray, @Nullable ReadableArray readableArray2, @Nullable ReadableArray readableArray3, @Nullable ReadableArray readableArray4, @Nullable ReadableArray readableArray5) {
        int i2;
        int i3;
        int i4;
        ReadableArray readableArray6 = readableArray;
        ReadableArray readableArray7 = readableArray2;
        ReadableArray readableArray8 = readableArray3;
        ReadableArray readableArray9 = readableArray4;
        ReadableArray readableArray10 = readableArray5;
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (readableArray6 == null) {
            i2 = 0;
        } else {
            i2 = readableArray.size();
        }
        if (readableArray8 == null) {
            i3 = 0;
        } else {
            i3 = readableArray3.size();
        }
        if (readableArray10 == null) {
            i4 = 0;
        } else {
            i4 = readableArray5.size();
        }
        if (i2 != 0 && (readableArray7 == null || i2 != readableArray2.size())) {
            throw new IllegalViewOperationException("Size of moveFrom != size of moveTo!");
        } else if (i3 == 0 || (readableArray9 != null && i3 == readableArray4.size())) {
            ViewAtIndex[] viewAtIndexArr = new ViewAtIndex[(i2 + i3)];
            int[] iArr = new int[(i2 + i4)];
            int[] iArr2 = new int[iArr.length];
            int[] iArr3 = new int[i4];
            if (i2 > 0) {
                Assertions.assertNotNull(readableArray);
                Assertions.assertNotNull(readableArray2);
                int i5 = 0;
                while (i5 < i2) {
                    int i6 = readableArray6.getInt(i5);
                    int reactTag = node.getChildAt(i6).getReactTag();
                    int[] iArr4 = iArr3;
                    viewAtIndexArr[i5] = new ViewAtIndex(reactTag, readableArray7.getInt(i5));
                    iArr[i5] = i6;
                    iArr2[i5] = reactTag;
                    i5++;
                    iArr3 = iArr4;
                    int i7 = i;
                    readableArray6 = readableArray;
                }
            }
            int[] iArr5 = iArr3;
            if (i3 > 0) {
                Assertions.assertNotNull(readableArray3);
                Assertions.assertNotNull(readableArray4);
                for (int i8 = 0; i8 < i3; i8++) {
                    viewAtIndexArr[i2 + i8] = new ViewAtIndex(readableArray8.getInt(i8), readableArray9.getInt(i8));
                }
            }
            if (i4 > 0) {
                Assertions.assertNotNull(readableArray5);
                for (int i9 = 0; i9 < i4; i9++) {
                    int i10 = readableArray10.getInt(i9);
                    int reactTag2 = node.getChildAt(i10).getReactTag();
                    int i11 = i2 + i9;
                    iArr[i11] = i10;
                    iArr2[i11] = reactTag2;
                    iArr5[i9] = reactTag2;
                }
            }
            Arrays.sort(viewAtIndexArr, ViewAtIndex.COMPARATOR);
            Arrays.sort(iArr);
            int i12 = -1;
            for (int length = iArr.length - 1; length >= 0; length--) {
                if (iArr[length] == i12) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Repeated indices in Removal list for view tag: ");
                    sb.append(i);
                    throw new IllegalViewOperationException(sb.toString());
                }
                int i13 = i;
                node.removeChildAt(iArr[length]);
                i12 = iArr[length];
            }
            for (ViewAtIndex viewAtIndex : viewAtIndexArr) {
                ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(viewAtIndex.mTag);
                if (node2 == null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Trying to add unknown view tag: ");
                    sb2.append(viewAtIndex.mTag);
                    throw new IllegalViewOperationException(sb2.toString());
                }
                node.addChildAt(node2, viewAtIndex.mIndex);
            }
            if (!node.isVirtual() && !node.isVirtualAnchor()) {
                this.mNativeViewHierarchyOptimizer.handleManageChildren(node, iArr, iArr2, viewAtIndexArr, iArr5);
            }
            int[] iArr6 = iArr5;
            for (int node3 : iArr6) {
                removeShadowNode(this.mShadowNodeRegistry.getNode(node3));
            }
        } else {
            throw new IllegalViewOperationException("Size of addChildTags != size of addAtIndices!");
        }
    }

    public void setChildren(int i, ReadableArray readableArray) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        for (int i2 = 0; i2 < readableArray.size(); i2++) {
            ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(readableArray.getInt(i2));
            if (node2 == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Trying to add unknown view tag: ");
                sb.append(readableArray.getInt(i2));
                throw new IllegalViewOperationException(sb.toString());
            }
            node.addChildAt(node2, i2);
        }
        if (!node.isVirtual() && !node.isVirtualAnchor()) {
            this.mNativeViewHierarchyOptimizer.handleSetChildren(node, readableArray);
        }
    }

    public void replaceExistingNonRootView(int i, int i2) {
        if (this.mShadowNodeRegistry.isRootNode(i) || this.mShadowNodeRegistry.isRootNode(i2)) {
            throw new IllegalViewOperationException("Trying to add or replace a root tag!");
        }
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to replace unknown view tag: ");
            sb.append(i);
            throw new IllegalViewOperationException(sb.toString());
        }
        ReactShadowNode parent = node.getParent();
        if (parent == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Node is not attached to a parent: ");
            sb2.append(i);
            throw new IllegalViewOperationException(sb2.toString());
        }
        int indexOf = parent.indexOf(node);
        if (indexOf < 0) {
            throw new IllegalStateException("Didn't find child tag in parent");
        }
        WritableArray createArray = Arguments.createArray();
        createArray.pushInt(i2);
        WritableArray createArray2 = Arguments.createArray();
        createArray2.pushInt(indexOf);
        WritableArray createArray3 = Arguments.createArray();
        createArray3.pushInt(indexOf);
        manageChildren(parent.getReactTag(), null, null, createArray, createArray2, createArray3);
    }

    public void removeSubviewsFromContainerWithID(int i) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to remove subviews of an unknown view tag: ");
            sb.append(i);
            throw new IllegalViewOperationException(sb.toString());
        }
        WritableArray createArray = Arguments.createArray();
        for (int i2 = 0; i2 < node.getChildCount(); i2++) {
            createArray.pushInt(i2);
        }
        manageChildren(i, null, null, null, null, createArray);
    }

    public void findSubviewIn(int i, float f, float f2, Callback callback) {
        this.mOperationsQueue.enqueueFindTargetForTouch(i, f, f2, callback);
    }

    public void viewIsDescendantOf(int i, int i2, Callback callback) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(i2);
        if (node == null || node2 == null) {
            callback.invoke(Boolean.valueOf(false));
            return;
        }
        callback.invoke(Boolean.valueOf(node.isDescendantOf(node2)));
    }

    public void measure(int i, Callback callback) {
        this.mOperationsQueue.enqueueMeasure(i, callback);
    }

    public void measureInWindow(int i, Callback callback) {
        this.mOperationsQueue.enqueueMeasureInWindow(i, callback);
    }

    public void measureLayout(int i, int i2, Callback callback, Callback callback2) {
        try {
            measureLayout(i, i2, this.mMeasureBuffer);
            callback2.invoke(Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[0])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[1])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[3])));
        } catch (IllegalViewOperationException e) {
            callback.invoke(e.getMessage());
        }
    }

    public void measureLayoutRelativeToParent(int i, Callback callback, Callback callback2) {
        try {
            measureLayoutRelativeToParent(i, this.mMeasureBuffer);
            callback2.invoke(Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[0])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[1])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[2])), Float.valueOf(PixelUtil.toDIPFromPixel((float) this.mMeasureBuffer[3])));
        } catch (IllegalViewOperationException e) {
            callback.invoke(e.getMessage());
        }
    }

    public void dispatchViewUpdates(int i) {
        SystraceMessage.beginSection(0, "UIImplementation.dispatchViewUpdates").arg("batchId", i).flush();
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            updateViewHierarchy();
            this.mNativeViewHierarchyOptimizer.onBatchComplete();
            this.mOperationsQueue.dispatchViewUpdates(i, uptimeMillis, this.mLastCalculateLayoutTime);
        } finally {
            Systrace.endSection(0);
        }
    }

    private void dispatchViewUpdatesIfNeeded() {
        if (this.mOperationsQueue.isEmpty()) {
            dispatchViewUpdates(-1);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0063, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0064, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0068, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateViewHierarchy() {
        /*
            r7 = this;
            java.lang.String r0 = "UIImplementation.updateViewHierarchy"
            r1 = 0
            com.facebook.systrace.Systrace.beginSection(r1, r0)
            r0 = 0
        L_0x0008:
            com.facebook.react.uimanager.ShadowNodeRegistry r3 = r7.mShadowNodeRegistry     // Catch:{ all -> 0x0070 }
            int r3 = r3.getRootNodeCount()     // Catch:{ all -> 0x0070 }
            if (r0 >= r3) goto L_0x006c
            com.facebook.react.uimanager.ShadowNodeRegistry r3 = r7.mShadowNodeRegistry     // Catch:{ all -> 0x0070 }
            int r3 = r3.getRootTag(r0)     // Catch:{ all -> 0x0070 }
            com.facebook.react.uimanager.ShadowNodeRegistry r4 = r7.mShadowNodeRegistry     // Catch:{ all -> 0x0070 }
            com.facebook.react.uimanager.ReactShadowNode r4 = r4.getNode(r3)     // Catch:{ all -> 0x0070 }
            java.util.Set<java.lang.Integer> r5 = r7.mMeasuredRootNodes     // Catch:{ all -> 0x0070 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x0070 }
            boolean r3 = r5.contains(r3)     // Catch:{ all -> 0x0070 }
            if (r3 == 0) goto L_0x0069
            java.lang.String r3 = "UIImplementation.notifyOnBeforeLayoutRecursive"
            com.facebook.systrace.SystraceMessage$Builder r3 = com.facebook.systrace.SystraceMessage.beginSection(r1, r3)     // Catch:{ all -> 0x0070 }
            java.lang.String r5 = "rootTag"
            int r6 = r4.getReactTag()     // Catch:{ all -> 0x0070 }
            com.facebook.systrace.SystraceMessage$Builder r3 = r3.arg(r5, r6)     // Catch:{ all -> 0x0070 }
            r3.flush()     // Catch:{ all -> 0x0070 }
            r7.notifyOnBeforeLayoutRecursive(r4)     // Catch:{ all -> 0x0064 }
            com.facebook.systrace.Systrace.endSection(r1)     // Catch:{ all -> 0x0070 }
            r7.calculateRootLayout(r4)     // Catch:{ all -> 0x0070 }
            java.lang.String r3 = "UIImplementation.applyUpdatesRecursive"
            com.facebook.systrace.SystraceMessage$Builder r3 = com.facebook.systrace.SystraceMessage.beginSection(r1, r3)     // Catch:{ all -> 0x0070 }
            java.lang.String r5 = "rootTag"
            int r6 = r4.getReactTag()     // Catch:{ all -> 0x0070 }
            com.facebook.systrace.SystraceMessage$Builder r3 = r3.arg(r5, r6)     // Catch:{ all -> 0x0070 }
            r3.flush()     // Catch:{ all -> 0x0070 }
            r3 = 0
            r7.applyUpdatesRecursive(r4, r3, r3)     // Catch:{ all -> 0x005f }
            com.facebook.systrace.Systrace.endSection(r1)     // Catch:{ all -> 0x0070 }
            goto L_0x0069
        L_0x005f:
            r0 = move-exception
            com.facebook.systrace.Systrace.endSection(r1)     // Catch:{ all -> 0x0070 }
            throw r0     // Catch:{ all -> 0x0070 }
        L_0x0064:
            r0 = move-exception
            com.facebook.systrace.Systrace.endSection(r1)     // Catch:{ all -> 0x0070 }
            throw r0     // Catch:{ all -> 0x0070 }
        L_0x0069:
            int r0 = r0 + 1
            goto L_0x0008
        L_0x006c:
            com.facebook.systrace.Systrace.endSection(r1)
            return
        L_0x0070:
            r0 = move-exception
            com.facebook.systrace.Systrace.endSection(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.UIImplementation.updateViewHierarchy():void");
    }

    public void registerAnimation(Animation animation) {
        this.mOperationsQueue.enqueueRegisterAnimation(animation);
    }

    public void addAnimation(int i, int i2, Callback callback) {
        assertViewExists(i, "addAnimation");
        this.mOperationsQueue.enqueueAddAnimation(i, i2, callback);
    }

    public void removeAnimation(int i, int i2) {
        assertViewExists(i, "removeAnimation");
        this.mOperationsQueue.enqueueRemoveAnimation(i2);
    }

    public void setLayoutAnimationEnabledExperimental(boolean z) {
        this.mOperationsQueue.enqueueSetLayoutAnimationEnabled(z);
    }

    public void configureNextLayoutAnimation(ReadableMap readableMap, Callback callback, Callback callback2) {
        this.mOperationsQueue.enqueueConfigureLayoutAnimation(readableMap, callback, callback2);
    }

    public void setJSResponder(int i, boolean z) {
        assertViewExists(i, "setJSResponder");
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        while (true) {
            if (node.isVirtual() || node.isLayoutOnly()) {
                node = node.getParent();
            } else {
                this.mOperationsQueue.enqueueSetJSResponder(node.getReactTag(), i, z);
                return;
            }
        }
    }

    public void clearJSResponder() {
        this.mOperationsQueue.enqueueClearJSResponder();
    }

    public void dispatchViewManagerCommand(int i, int i2, ReadableArray readableArray) {
        assertViewExists(i, "dispatchViewManagerCommand");
        this.mOperationsQueue.enqueueDispatchCommand(i, i2, readableArray);
    }

    public void showPopupMenu(int i, ReadableArray readableArray, Callback callback, Callback callback2) {
        assertViewExists(i, "showPopupMenu");
        this.mOperationsQueue.enqueueShowPopupMenu(i, readableArray, callback, callback2);
    }

    public void sendAccessibilityEvent(int i, int i2) {
        this.mOperationsQueue.enqueueSendAccessibilityEvent(i, i2);
    }

    public void onHostResume() {
        this.mOperationsQueue.resumeFrameCallback();
    }

    public void onHostPause() {
        this.mOperationsQueue.pauseFrameCallback();
    }

    public void setViewHierarchyUpdateDebugListener(@Nullable NotThreadSafeViewHierarchyUpdateDebugListener notThreadSafeViewHierarchyUpdateDebugListener) {
        this.mOperationsQueue.setViewHierarchyUpdateDebugListener(notThreadSafeViewHierarchyUpdateDebugListener);
    }

    /* access modifiers changed from: protected */
    public final void removeShadowNode(ReactShadowNode reactShadowNode) {
        removeShadowNodeRecursive(reactShadowNode);
        reactShadowNode.dispose();
    }

    private void removeShadowNodeRecursive(ReactShadowNode reactShadowNode) {
        NativeViewHierarchyOptimizer.handleRemoveNode(reactShadowNode);
        this.mShadowNodeRegistry.removeNode(reactShadowNode.getReactTag());
        this.mMeasuredRootNodes.remove(Integer.valueOf(reactShadowNode.getReactTag()));
        for (int childCount = reactShadowNode.getChildCount() - 1; childCount >= 0; childCount--) {
            removeShadowNodeRecursive(reactShadowNode.getChildAt(childCount));
        }
        reactShadowNode.removeAndDisposeAllChildren();
    }

    private void measureLayout(int i, int i2, int[] iArr) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        ReactShadowNode node2 = this.mShadowNodeRegistry.getNode(i2);
        if (node == null || node2 == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Tag ");
            if (node != null) {
                i = i2;
            }
            sb.append(i);
            sb.append(" does not exist");
            throw new IllegalViewOperationException(sb.toString());
        }
        if (node != node2) {
            for (ReactShadowNode parent = node.getParent(); parent != node2; parent = parent.getParent()) {
                if (parent == null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Tag ");
                    sb2.append(i2);
                    sb2.append(" is not an ancestor of tag ");
                    sb2.append(i);
                    throw new IllegalViewOperationException(sb2.toString());
                }
            }
        }
        measureLayoutRelativeToVerifiedAncestor(node, node2, iArr);
    }

    private void measureLayoutRelativeToParent(int i, int[] iArr) {
        ReactShadowNode node = this.mShadowNodeRegistry.getNode(i);
        if (node == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No native view for tag ");
            sb.append(i);
            sb.append(" exists!");
            throw new IllegalViewOperationException(sb.toString());
        }
        ReactShadowNode parent = node.getParent();
        if (parent == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("View with tag ");
            sb2.append(i);
            sb2.append(" doesn't have a parent!");
            throw new IllegalViewOperationException(sb2.toString());
        }
        measureLayoutRelativeToVerifiedAncestor(node, parent, iArr);
    }

    private void measureLayoutRelativeToVerifiedAncestor(ReactShadowNode reactShadowNode, ReactShadowNode reactShadowNode2, int[] iArr) {
        int i;
        int i2;
        if (reactShadowNode != reactShadowNode2) {
            i2 = Math.round(reactShadowNode.getLayoutX());
            i = Math.round(reactShadowNode.getLayoutY());
            for (ReactShadowNode parent = reactShadowNode.getParent(); parent != reactShadowNode2; parent = parent.getParent()) {
                Assertions.assertNotNull(parent);
                assertNodeDoesNotNeedCustomLayoutForChildren(parent);
                i2 += Math.round(parent.getLayoutX());
                i += Math.round(parent.getLayoutY());
            }
            assertNodeDoesNotNeedCustomLayoutForChildren(reactShadowNode2);
        } else {
            i2 = 0;
            i = 0;
        }
        iArr[0] = i2;
        iArr[1] = i;
        iArr[2] = reactShadowNode.getScreenWidth();
        iArr[3] = reactShadowNode.getScreenHeight();
    }

    private void assertViewExists(int i, String str) {
        if (this.mShadowNodeRegistry.getNode(i) == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to execute operation ");
            sb.append(str);
            sb.append(" on view with ");
            sb.append("tag: ");
            sb.append(i);
            sb.append(", since the view does not exists");
            throw new IllegalViewOperationException(sb.toString());
        }
    }

    private void assertNodeDoesNotNeedCustomLayoutForChildren(ReactShadowNode reactShadowNode) {
        ViewManager viewManager = (ViewManager) Assertions.assertNotNull(this.mViewManagers.get(reactShadowNode.getViewClass()));
        if (viewManager instanceof ViewGroupManager) {
            ViewGroupManager viewGroupManager = (ViewGroupManager) viewManager;
            if (viewGroupManager != null && viewGroupManager.needsCustomLayoutForChildren()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Trying to measure a view using measureLayout/measureLayoutRelativeToParent relative to an ancestor that requires custom layout for it's children (");
                sb.append(reactShadowNode.getViewClass());
                sb.append("). Use measure instead.");
                throw new IllegalViewOperationException(sb.toString());
            }
            return;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Trying to use view ");
        sb2.append(reactShadowNode.getViewClass());
        sb2.append(" as a parent, but its Manager doesn't extends ViewGroupManager");
        throw new IllegalViewOperationException(sb2.toString());
    }

    private void notifyOnBeforeLayoutRecursive(ReactShadowNode reactShadowNode) {
        if (reactShadowNode.hasUpdates()) {
            for (int i = 0; i < reactShadowNode.getChildCount(); i++) {
                notifyOnBeforeLayoutRecursive(reactShadowNode.getChildAt(i));
            }
            reactShadowNode.onBeforeLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void calculateRootLayout(ReactShadowNode reactShadowNode) {
        SystraceMessage.beginSection(0, "cssRoot.calculateLayout").arg("rootTag", reactShadowNode.getReactTag()).flush();
        long uptimeMillis = SystemClock.uptimeMillis();
        try {
            reactShadowNode.calculateLayout();
        } finally {
            Systrace.endSection(0);
            this.mLastCalculateLayoutTime = SystemClock.uptimeMillis() - uptimeMillis;
        }
    }

    /* access modifiers changed from: protected */
    public void applyUpdatesRecursive(ReactShadowNode reactShadowNode, float f, float f2) {
        if (reactShadowNode.hasUpdates()) {
            if (!reactShadowNode.isVirtualAnchor()) {
                for (int i = 0; i < reactShadowNode.getChildCount(); i++) {
                    applyUpdatesRecursive(reactShadowNode.getChildAt(i), reactShadowNode.getLayoutX() + f, reactShadowNode.getLayoutY() + f2);
                }
            }
            int reactTag = reactShadowNode.getReactTag();
            if (!this.mShadowNodeRegistry.isRootNode(reactTag) && reactShadowNode.dispatchUpdates(f, f2, this.mOperationsQueue, this.mNativeViewHierarchyOptimizer) && reactShadowNode.shouldNotifyOnLayout()) {
                this.mEventDispatcher.dispatchEvent(OnLayoutEvent.obtain(reactTag, reactShadowNode.getScreenX(), reactShadowNode.getScreenY(), reactShadowNode.getScreenWidth(), reactShadowNode.getScreenHeight()));
            }
            reactShadowNode.markUpdateSeen();
        }
    }

    public void addUIBlock(UIBlock uIBlock) {
        this.mOperationsQueue.enqueueUIBlock(uIBlock);
    }

    public int resolveRootTagFromReactTag(int i) {
        if (this.mShadowNodeRegistry.isRootNode(i)) {
            return i;
        }
        ReactShadowNode resolveShadowNode = resolveShadowNode(i);
        int i2 = 0;
        if (resolveShadowNode != null) {
            i2 = resolveShadowNode.getRootNode().getReactTag();
        } else {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Warning : attempted to resolve a non-existent react shadow node. reactTag=");
            sb.append(i);
            FLog.m105w(str, sb.toString());
        }
        return i2;
    }

    public void enableLayoutCalculationForRootNode(int i) {
        this.mMeasuredRootNodes.add(Integer.valueOf(i));
    }
}
