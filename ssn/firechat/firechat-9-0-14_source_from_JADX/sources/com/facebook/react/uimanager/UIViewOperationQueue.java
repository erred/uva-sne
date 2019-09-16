package com.facebook.react.uimanager;

import android.os.SystemClock;
import com.facebook.common.logging.FLog;
import com.facebook.react.animation.Animation;
import com.facebook.react.animation.AnimationRegistry;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedRunnable;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.modules.core.ReactChoreographer;
import com.facebook.react.modules.core.ReactChoreographer.CallbackType;
import com.facebook.react.uimanager.debug.NotThreadSafeViewHierarchyUpdateDebugListener;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class UIViewOperationQueue {
    public static final int DEFAULT_MIN_TIME_LEFT_IN_FRAME_FOR_NONBATCHED_OPERATION_MS = 8;
    /* access modifiers changed from: private */
    public final AnimationRegistry mAnimationRegistry;
    private final Object mDispatchRunnablesLock = new Object();
    private final DispatchUIFrameCallback mDispatchUIFrameCallback;
    @GuardedBy("mDispatchRunnablesLock")
    private ArrayList<Runnable> mDispatchUIRunnables = new ArrayList<>();
    private boolean mIsDispatchUIFrameCallbackEnqueued = false;
    /* access modifiers changed from: private */
    public boolean mIsInIllegalUIState = false;
    /* access modifiers changed from: private */
    public boolean mIsProfilingNextBatch = false;
    /* access modifiers changed from: private */
    public final int[] mMeasureBuffer = new int[4];
    /* access modifiers changed from: private */
    public final NativeViewHierarchyManager mNativeViewHierarchyManager;
    /* access modifiers changed from: private */
    public long mNonBatchedExecutionTotalTime;
    /* access modifiers changed from: private */
    @GuardedBy("mNonBatchedOperationsLock")
    public ArrayDeque<UIOperation> mNonBatchedOperations = new ArrayDeque<>();
    /* access modifiers changed from: private */
    public final Object mNonBatchedOperationsLock = new Object();
    private ArrayList<UIOperation> mOperations = new ArrayList<>();
    private long mProfiledBatchBatchedExecutionTime;
    /* access modifiers changed from: private */
    public long mProfiledBatchCommitStartTime;
    /* access modifiers changed from: private */
    public long mProfiledBatchDispatchViewUpdatesTime;
    /* access modifiers changed from: private */
    public long mProfiledBatchLayoutTime;
    private long mProfiledBatchNonBatchedExecutionTime;
    /* access modifiers changed from: private */
    public long mProfiledBatchRunStartTime;
    private final ReactApplicationContext mReactApplicationContext;
    /* access modifiers changed from: private */
    @Nullable
    public NotThreadSafeViewHierarchyUpdateDebugListener mViewHierarchyUpdateDebugListener;

    private class AddAnimationOperation extends AnimationOperation {
        private final int mReactTag;
        private final Callback mSuccessCallback;

        private AddAnimationOperation(int i, int i2, Callback callback) {
            super(i2);
            this.mReactTag = i;
            this.mSuccessCallback = callback;
        }

        public void execute() {
            Animation animation = UIViewOperationQueue.this.mAnimationRegistry.getAnimation(this.mAnimationID);
            if (animation != null) {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.startAnimationForNativeView(this.mReactTag, animation, this.mSuccessCallback);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Animation with id ");
            sb.append(this.mAnimationID);
            sb.append(" was not found");
            throw new IllegalViewOperationException(sb.toString());
        }
    }

    private static abstract class AnimationOperation implements UIOperation {
        protected final int mAnimationID;

        public AnimationOperation(int i) {
            this.mAnimationID = i;
        }
    }

    private final class ChangeJSResponderOperation extends ViewOperation {
        private final boolean mBlockNativeResponder;
        private final boolean mClearResponder;
        private final int mInitialTag;

        public ChangeJSResponderOperation(int i, int i2, boolean z, boolean z2) {
            super(i);
            this.mInitialTag = i2;
            this.mClearResponder = z;
            this.mBlockNativeResponder = z2;
        }

        public void execute() {
            if (!this.mClearResponder) {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.setJSResponder(this.mTag, this.mInitialTag, this.mBlockNativeResponder);
            } else {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.clearJSResponder();
            }
        }
    }

    private class ConfigureLayoutAnimationOperation implements UIOperation {
        private final ReadableMap mConfig;

        private ConfigureLayoutAnimationOperation(ReadableMap readableMap) {
            this.mConfig = readableMap;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.configureLayoutAnimation(this.mConfig);
        }
    }

    private final class CreateViewOperation extends ViewOperation {
        private final String mClassName;
        @Nullable
        private final ReactStylesDiffMap mInitialProps;
        private final ThemedReactContext mThemedContext;

        public CreateViewOperation(ThemedReactContext themedReactContext, int i, String str, @Nullable ReactStylesDiffMap reactStylesDiffMap) {
            super(i);
            this.mThemedContext = themedReactContext;
            this.mClassName = str;
            this.mInitialProps = reactStylesDiffMap;
            Systrace.startAsyncFlow(0, "createView", this.mTag);
        }

        public void execute() {
            Systrace.endAsyncFlow(0, "createView", this.mTag);
            UIViewOperationQueue.this.mNativeViewHierarchyManager.createView(this.mThemedContext, this.mTag, this.mClassName, this.mInitialProps);
        }
    }

    private final class DispatchCommandOperation extends ViewOperation {
        @Nullable
        private final ReadableArray mArgs;
        private final int mCommand;

        public DispatchCommandOperation(int i, int i2, @Nullable ReadableArray readableArray) {
            super(i);
            this.mCommand = i2;
            this.mArgs = readableArray;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.dispatchCommand(this.mTag, this.mCommand, this.mArgs);
        }
    }

    private class DispatchUIFrameCallback extends GuardedFrameCallback {
        private static final int FRAME_TIME_MS = 16;
        private final int mMinTimeLeftInFrameForNonBatchedOperationMs;

        private DispatchUIFrameCallback(ReactContext reactContext, int i) {
            super(reactContext);
            this.mMinTimeLeftInFrameForNonBatchedOperationMs = i;
        }

        /* JADX INFO: finally extract failed */
        public void doFrameGuarded(long j) {
            if (UIViewOperationQueue.this.mIsInIllegalUIState) {
                FLog.m105w(ReactConstants.TAG, "Not flushing pending UI operations because of previously thrown Exception");
                return;
            }
            Systrace.beginSection(0, "dispatchNonBatchedUIOperations");
            try {
                dispatchPendingNonBatchedOperations(j);
                Systrace.endSection(0);
                UIViewOperationQueue.this.flushPendingBatches();
                ReactChoreographer.getInstance().postFrameCallback(CallbackType.DISPATCH_UI, this);
            } catch (Throwable th) {
                Systrace.endSection(0);
                throw th;
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
            r2 = android.os.SystemClock.uptimeMillis();
            r1.execute();
            com.facebook.react.uimanager.UIViewOperationQueue.access$2402(r10.this$0, com.facebook.react.uimanager.UIViewOperationQueue.access$2400(r10.this$0) + (android.os.SystemClock.uptimeMillis() - r2));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0054, code lost:
            r11 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0055, code lost:
            com.facebook.react.uimanager.UIViewOperationQueue.access$2002(r10.this$0, true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x005b, code lost:
            throw r11;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void dispatchPendingNonBatchedOperations(long r11) {
            /*
                r10 = this;
            L_0x0000:
                r0 = 16
                long r2 = java.lang.System.nanoTime()
                long r4 = r2 - r11
                r2 = 1000000(0xf4240, double:4.940656E-318)
                long r4 = r4 / r2
                long r2 = r0 - r4
                int r0 = r10.mMinTimeLeftInFrameForNonBatchedOperationMs
                long r0 = (long) r0
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 >= 0) goto L_0x0016
                goto L_0x002a
            L_0x0016:
                com.facebook.react.uimanager.UIViewOperationQueue r0 = com.facebook.react.uimanager.UIViewOperationQueue.this
                java.lang.Object r0 = r0.mNonBatchedOperationsLock
                monitor-enter(r0)
                com.facebook.react.uimanager.UIViewOperationQueue r1 = com.facebook.react.uimanager.UIViewOperationQueue.this     // Catch:{ all -> 0x005c }
                java.util.ArrayDeque r1 = r1.mNonBatchedOperations     // Catch:{ all -> 0x005c }
                boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x005c }
                if (r1 == 0) goto L_0x002b
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
            L_0x002a:
                return
            L_0x002b:
                com.facebook.react.uimanager.UIViewOperationQueue r1 = com.facebook.react.uimanager.UIViewOperationQueue.this     // Catch:{ all -> 0x005c }
                java.util.ArrayDeque r1 = r1.mNonBatchedOperations     // Catch:{ all -> 0x005c }
                java.lang.Object r1 = r1.pollFirst()     // Catch:{ all -> 0x005c }
                com.facebook.react.uimanager.UIViewOperationQueue$UIOperation r1 = (com.facebook.react.uimanager.UIViewOperationQueue.UIOperation) r1     // Catch:{ all -> 0x005c }
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
                long r2 = android.os.SystemClock.uptimeMillis()     // Catch:{ Exception -> 0x0054 }
                r1.execute()     // Catch:{ Exception -> 0x0054 }
                com.facebook.react.uimanager.UIViewOperationQueue r0 = com.facebook.react.uimanager.UIViewOperationQueue.this     // Catch:{ Exception -> 0x0054 }
                com.facebook.react.uimanager.UIViewOperationQueue r1 = com.facebook.react.uimanager.UIViewOperationQueue.this     // Catch:{ Exception -> 0x0054 }
                long r4 = r1.mNonBatchedExecutionTotalTime     // Catch:{ Exception -> 0x0054 }
                long r6 = android.os.SystemClock.uptimeMillis()     // Catch:{ Exception -> 0x0054 }
                r1 = 0
                long r8 = r6 - r2
                long r1 = r4 + r8
                r0.mNonBatchedExecutionTotalTime = r1     // Catch:{ Exception -> 0x0054 }
                goto L_0x0000
            L_0x0054:
                r11 = move-exception
                com.facebook.react.uimanager.UIViewOperationQueue r12 = com.facebook.react.uimanager.UIViewOperationQueue.this
                r0 = 1
                r12.mIsInIllegalUIState = r0
                throw r11
            L_0x005c:
                r11 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x005c }
                throw r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.UIViewOperationQueue.DispatchUIFrameCallback.dispatchPendingNonBatchedOperations(long):void");
        }
    }

    private final class FindTargetForTouchOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;
        private final float mTargetX;
        private final float mTargetY;

        private FindTargetForTouchOperation(int i, float f, float f2, Callback callback) {
            this.mReactTag = i;
            this.mTargetX = f;
            this.mTargetY = f2;
            this.mCallback = callback;
        }

        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                float f = (float) UIViewOperationQueue.this.mMeasureBuffer[0];
                float f2 = (float) UIViewOperationQueue.this.mMeasureBuffer[1];
                int findTargetTagForTouch = UIViewOperationQueue.this.mNativeViewHierarchyManager.findTargetTagForTouch(this.mReactTag, this.mTargetX, this.mTargetY);
                try {
                    UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(findTargetTagForTouch, UIViewOperationQueue.this.mMeasureBuffer);
                    float dIPFromPixel = PixelUtil.toDIPFromPixel(((float) UIViewOperationQueue.this.mMeasureBuffer[0]) - f);
                    float dIPFromPixel2 = PixelUtil.toDIPFromPixel(((float) UIViewOperationQueue.this.mMeasureBuffer[1]) - f2);
                    float dIPFromPixel3 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[2]);
                    float dIPFromPixel4 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[3]);
                    this.mCallback.invoke(Integer.valueOf(findTargetTagForTouch), Float.valueOf(dIPFromPixel), Float.valueOf(dIPFromPixel2), Float.valueOf(dIPFromPixel3), Float.valueOf(dIPFromPixel4));
                } catch (IllegalViewOperationException unused) {
                    this.mCallback.invoke(new Object[0]);
                }
            } catch (IllegalViewOperationException unused2) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    private final class ManageChildrenOperation extends ViewOperation {
        @Nullable
        private final int[] mIndicesToRemove;
        @Nullable
        private final int[] mTagsToDelete;
        @Nullable
        private final ViewAtIndex[] mViewsToAdd;

        public ManageChildrenOperation(int i, @Nullable int[] iArr, @Nullable ViewAtIndex[] viewAtIndexArr, @Nullable int[] iArr2) {
            super(i);
            this.mIndicesToRemove = iArr;
            this.mViewsToAdd = viewAtIndexArr;
            this.mTagsToDelete = iArr2;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.manageChildren(this.mTag, this.mIndicesToRemove, this.mViewsToAdd, this.mTagsToDelete);
        }
    }

    private final class MeasureInWindowOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;

        private MeasureInWindowOperation(int i, Callback callback) {
            this.mReactTag = i;
            this.mCallback = callback;
        }

        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measureInWindow(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                float dIPFromPixel = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[0]);
                float dIPFromPixel2 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[1]);
                float dIPFromPixel3 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[2]);
                float dIPFromPixel4 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[3]);
                this.mCallback.invoke(Float.valueOf(dIPFromPixel), Float.valueOf(dIPFromPixel2), Float.valueOf(dIPFromPixel3), Float.valueOf(dIPFromPixel4));
            } catch (NoSuchNativeViewException unused) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    private final class MeasureOperation implements UIOperation {
        private final Callback mCallback;
        private final int mReactTag;

        private MeasureOperation(int i, Callback callback) {
            this.mReactTag = i;
            this.mCallback = callback;
        }

        public void execute() {
            try {
                UIViewOperationQueue.this.mNativeViewHierarchyManager.measure(this.mReactTag, UIViewOperationQueue.this.mMeasureBuffer);
                float dIPFromPixel = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[0]);
                float dIPFromPixel2 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[1]);
                float dIPFromPixel3 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[2]);
                float dIPFromPixel4 = PixelUtil.toDIPFromPixel((float) UIViewOperationQueue.this.mMeasureBuffer[3]);
                this.mCallback.invoke(Integer.valueOf(0), Integer.valueOf(0), Float.valueOf(dIPFromPixel3), Float.valueOf(dIPFromPixel4), Float.valueOf(dIPFromPixel), Float.valueOf(dIPFromPixel2));
            } catch (NoSuchNativeViewException unused) {
                this.mCallback.invoke(new Object[0]);
            }
        }
    }

    private class RegisterAnimationOperation extends AnimationOperation {
        private final Animation mAnimation;

        private RegisterAnimationOperation(Animation animation) {
            super(animation.getAnimationID());
            this.mAnimation = animation;
        }

        public void execute() {
            UIViewOperationQueue.this.mAnimationRegistry.registerAnimation(this.mAnimation);
        }
    }

    private final class RemoveAnimationOperation extends AnimationOperation {
        private RemoveAnimationOperation(int i) {
            super(i);
        }

        public void execute() {
            Animation animation = UIViewOperationQueue.this.mAnimationRegistry.getAnimation(this.mAnimationID);
            if (animation != null) {
                animation.cancel();
            }
        }
    }

    private final class RemoveRootViewOperation extends ViewOperation {
        public RemoveRootViewOperation(int i) {
            super(i);
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.removeRootView(this.mTag);
        }
    }

    private final class SendAccessibilityEvent extends ViewOperation {
        private final int mEventType;

        private SendAccessibilityEvent(int i, int i2) {
            super(i);
            this.mEventType = i2;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.sendAccessibilityEvent(this.mTag, this.mEventType);
        }
    }

    private final class SetChildrenOperation extends ViewOperation {
        private final ReadableArray mChildrenTags;

        public SetChildrenOperation(int i, ReadableArray readableArray) {
            super(i);
            this.mChildrenTags = readableArray;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.setChildren(this.mTag, this.mChildrenTags);
        }
    }

    private class SetLayoutAnimationEnabledOperation implements UIOperation {
        private final boolean mEnabled;

        private SetLayoutAnimationEnabledOperation(boolean z) {
            this.mEnabled = z;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.setLayoutAnimationEnabled(this.mEnabled);
        }
    }

    private final class ShowPopupMenuOperation extends ViewOperation {
        private final ReadableArray mItems;
        private final Callback mSuccess;

        public ShowPopupMenuOperation(int i, ReadableArray readableArray, Callback callback) {
            super(i);
            this.mItems = readableArray;
            this.mSuccess = callback;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.showPopupMenu(this.mTag, this.mItems, this.mSuccess);
        }
    }

    private class UIBlockOperation implements UIOperation {
        private final UIBlock mBlock;

        public UIBlockOperation(UIBlock uIBlock) {
            this.mBlock = uIBlock;
        }

        public void execute() {
            this.mBlock.execute(UIViewOperationQueue.this.mNativeViewHierarchyManager);
        }
    }

    public interface UIOperation {
        void execute();
    }

    private final class UpdateLayoutOperation extends ViewOperation {
        private final int mHeight;
        private final int mParentTag;
        private final int mWidth;

        /* renamed from: mX */
        private final int f83mX;

        /* renamed from: mY */
        private final int f84mY;

        public UpdateLayoutOperation(int i, int i2, int i3, int i4, int i5, int i6) {
            super(i2);
            this.mParentTag = i;
            this.f83mX = i3;
            this.f84mY = i4;
            this.mWidth = i5;
            this.mHeight = i6;
            Systrace.startAsyncFlow(0, "updateLayout", this.mTag);
        }

        public void execute() {
            Systrace.endAsyncFlow(0, "updateLayout", this.mTag);
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateLayout(this.mParentTag, this.mTag, this.f83mX, this.f84mY, this.mWidth, this.mHeight);
        }
    }

    private final class UpdatePropertiesOperation extends ViewOperation {
        private final ReactStylesDiffMap mProps;

        private UpdatePropertiesOperation(int i, ReactStylesDiffMap reactStylesDiffMap) {
            super(i);
            this.mProps = reactStylesDiffMap;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateProperties(this.mTag, this.mProps);
        }
    }

    private final class UpdateViewExtraData extends ViewOperation {
        private final Object mExtraData;

        public UpdateViewExtraData(int i, Object obj) {
            super(i);
            this.mExtraData = obj;
        }

        public void execute() {
            UIViewOperationQueue.this.mNativeViewHierarchyManager.updateViewExtraData(this.mTag, this.mExtraData);
        }
    }

    private abstract class ViewOperation implements UIOperation {
        public int mTag;

        public ViewOperation(int i) {
            this.mTag = i;
        }
    }

    public UIViewOperationQueue(ReactApplicationContext reactApplicationContext, NativeViewHierarchyManager nativeViewHierarchyManager, int i) {
        this.mNativeViewHierarchyManager = nativeViewHierarchyManager;
        this.mAnimationRegistry = nativeViewHierarchyManager.getAnimationRegistry();
        if (i == -1) {
            i = 8;
        }
        this.mDispatchUIFrameCallback = new DispatchUIFrameCallback(reactApplicationContext, i);
        this.mReactApplicationContext = reactApplicationContext;
    }

    /* access modifiers changed from: 0000 */
    public NativeViewHierarchyManager getNativeViewHierarchyManager() {
        return this.mNativeViewHierarchyManager;
    }

    public void setViewHierarchyUpdateDebugListener(@Nullable NotThreadSafeViewHierarchyUpdateDebugListener notThreadSafeViewHierarchyUpdateDebugListener) {
        this.mViewHierarchyUpdateDebugListener = notThreadSafeViewHierarchyUpdateDebugListener;
    }

    public void profileNextBatch() {
        this.mIsProfilingNextBatch = true;
        this.mProfiledBatchCommitStartTime = 0;
    }

    public Map<String, Long> getProfiledBatchPerfCounters() {
        HashMap hashMap = new HashMap();
        hashMap.put("CommitStartTime", Long.valueOf(this.mProfiledBatchCommitStartTime));
        hashMap.put("LayoutTime", Long.valueOf(this.mProfiledBatchLayoutTime));
        hashMap.put("DispatchViewUpdatesTime", Long.valueOf(this.mProfiledBatchDispatchViewUpdatesTime));
        hashMap.put("RunStartTime", Long.valueOf(this.mProfiledBatchRunStartTime));
        hashMap.put("BatchedExecutionTime", Long.valueOf(this.mProfiledBatchBatchedExecutionTime));
        hashMap.put("NonBatchedExecutionTime", Long.valueOf(this.mProfiledBatchNonBatchedExecutionTime));
        return hashMap;
    }

    public boolean isEmpty() {
        return this.mOperations.isEmpty();
    }

    public void addRootView(int i, SizeMonitoringFrameLayout sizeMonitoringFrameLayout, ThemedReactContext themedReactContext) {
        this.mNativeViewHierarchyManager.addRootView(i, sizeMonitoringFrameLayout, themedReactContext);
    }

    /* access modifiers changed from: protected */
    public void enqueueUIOperation(UIOperation uIOperation) {
        SoftAssertions.assertNotNull(uIOperation);
        this.mOperations.add(uIOperation);
    }

    public void enqueueRemoveRootView(int i) {
        this.mOperations.add(new RemoveRootViewOperation(i));
    }

    public void enqueueSetJSResponder(int i, int i2, boolean z) {
        ArrayList<UIOperation> arrayList = this.mOperations;
        ChangeJSResponderOperation changeJSResponderOperation = new ChangeJSResponderOperation(i, i2, false, z);
        arrayList.add(changeJSResponderOperation);
    }

    public void enqueueClearJSResponder() {
        ArrayList<UIOperation> arrayList = this.mOperations;
        ChangeJSResponderOperation changeJSResponderOperation = new ChangeJSResponderOperation(0, 0, true, false);
        arrayList.add(changeJSResponderOperation);
    }

    public void enqueueDispatchCommand(int i, int i2, ReadableArray readableArray) {
        this.mOperations.add(new DispatchCommandOperation(i, i2, readableArray));
    }

    public void enqueueUpdateExtraData(int i, Object obj) {
        this.mOperations.add(new UpdateViewExtraData(i, obj));
    }

    public void enqueueShowPopupMenu(int i, ReadableArray readableArray, Callback callback, Callback callback2) {
        this.mOperations.add(new ShowPopupMenuOperation(i, readableArray, callback2));
    }

    public void enqueueCreateView(ThemedReactContext themedReactContext, int i, String str, @Nullable ReactStylesDiffMap reactStylesDiffMap) {
        synchronized (this.mNonBatchedOperationsLock) {
            ArrayDeque<UIOperation> arrayDeque = this.mNonBatchedOperations;
            CreateViewOperation createViewOperation = new CreateViewOperation(themedReactContext, i, str, reactStylesDiffMap);
            arrayDeque.addLast(createViewOperation);
        }
    }

    public void enqueueUpdateProperties(int i, String str, ReactStylesDiffMap reactStylesDiffMap) {
        this.mOperations.add(new UpdatePropertiesOperation(i, reactStylesDiffMap));
    }

    public void enqueueUpdateLayout(int i, int i2, int i3, int i4, int i5, int i6) {
        ArrayList<UIOperation> arrayList = this.mOperations;
        UpdateLayoutOperation updateLayoutOperation = new UpdateLayoutOperation(i, i2, i3, i4, i5, i6);
        arrayList.add(updateLayoutOperation);
    }

    public void enqueueManageChildren(int i, @Nullable int[] iArr, @Nullable ViewAtIndex[] viewAtIndexArr, @Nullable int[] iArr2) {
        ArrayList<UIOperation> arrayList = this.mOperations;
        ManageChildrenOperation manageChildrenOperation = new ManageChildrenOperation(i, iArr, viewAtIndexArr, iArr2);
        arrayList.add(manageChildrenOperation);
    }

    public void enqueueSetChildren(int i, ReadableArray readableArray) {
        this.mOperations.add(new SetChildrenOperation(i, readableArray));
    }

    public void enqueueRegisterAnimation(Animation animation) {
        this.mOperations.add(new RegisterAnimationOperation(animation));
    }

    public void enqueueAddAnimation(int i, int i2, Callback callback) {
        ArrayList<UIOperation> arrayList = this.mOperations;
        AddAnimationOperation addAnimationOperation = new AddAnimationOperation(i, i2, callback);
        arrayList.add(addAnimationOperation);
    }

    public void enqueueRemoveAnimation(int i) {
        this.mOperations.add(new RemoveAnimationOperation(i));
    }

    public void enqueueSetLayoutAnimationEnabled(boolean z) {
        this.mOperations.add(new SetLayoutAnimationEnabledOperation(z));
    }

    public void enqueueConfigureLayoutAnimation(ReadableMap readableMap, Callback callback, Callback callback2) {
        this.mOperations.add(new ConfigureLayoutAnimationOperation(readableMap));
    }

    public void enqueueMeasure(int i, Callback callback) {
        this.mOperations.add(new MeasureOperation(i, callback));
    }

    public void enqueueMeasureInWindow(int i, Callback callback) {
        this.mOperations.add(new MeasureInWindowOperation(i, callback));
    }

    public void enqueueFindTargetForTouch(int i, float f, float f2, Callback callback) {
        ArrayList<UIOperation> arrayList = this.mOperations;
        FindTargetForTouchOperation findTargetForTouchOperation = new FindTargetForTouchOperation(i, f, f2, callback);
        arrayList.add(findTargetForTouchOperation);
    }

    public void enqueueSendAccessibilityEvent(int i, int i2) {
        this.mOperations.add(new SendAccessibilityEvent(i, i2));
    }

    public void enqueueUIBlock(UIBlock uIBlock) {
        this.mOperations.add(new UIBlockOperation(uIBlock));
    }

    /* access modifiers changed from: 0000 */
    public void dispatchViewUpdates(int i, long j, long j2) {
        final ArrayList arrayList;
        final ArrayDeque<UIOperation> arrayDeque;
        int i2 = i;
        SystraceMessage.beginSection(0, "UIViewOperationQueue.dispatchViewUpdates").arg("batchId", i2).flush();
        try {
            final long uptimeMillis = SystemClock.uptimeMillis();
            ArrayDeque<UIOperation> arrayDeque2 = null;
            if (!this.mOperations.isEmpty()) {
                ArrayList<UIOperation> arrayList2 = this.mOperations;
                this.mOperations = new ArrayList<>();
                arrayList = arrayList2;
            } else {
                arrayList = null;
            }
            synchronized (this.mNonBatchedOperationsLock) {
                if (!this.mNonBatchedOperations.isEmpty()) {
                    arrayDeque2 = this.mNonBatchedOperations;
                    this.mNonBatchedOperations = new ArrayDeque<>();
                }
                arrayDeque = arrayDeque2;
            }
            if (this.mViewHierarchyUpdateDebugListener != null) {
                this.mViewHierarchyUpdateDebugListener.onViewHierarchyUpdateEnqueued();
            }
            final int i3 = i2;
            final long j3 = j;
            C09811 r16 = r1;
            final long j4 = j2;
            C09811 r1 = new Runnable() {
                public void run() {
                    SystraceMessage.beginSection(0, "DispatchUI").arg("BatchId", i3).flush();
                    try {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        if (arrayDeque != null) {
                            Iterator it = arrayDeque.iterator();
                            while (it.hasNext()) {
                                ((UIOperation) it.next()).execute();
                            }
                        }
                        if (arrayList != null) {
                            Iterator it2 = arrayList.iterator();
                            while (it2.hasNext()) {
                                ((UIOperation) it2.next()).execute();
                            }
                        }
                        if (UIViewOperationQueue.this.mIsProfilingNextBatch && UIViewOperationQueue.this.mProfiledBatchCommitStartTime == 0) {
                            UIViewOperationQueue.this.mProfiledBatchCommitStartTime = j3;
                            UIViewOperationQueue.this.mProfiledBatchLayoutTime = j4;
                            UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime = uptimeMillis;
                            UIViewOperationQueue.this.mProfiledBatchRunStartTime = uptimeMillis;
                            Systrace.beginAsyncSection(0, "delayBeforeDispatchViewUpdates", 0, UIViewOperationQueue.this.mProfiledBatchCommitStartTime * 1000000);
                            Systrace.endAsyncSection(0, "delayBeforeDispatchViewUpdates", 0, UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime * 1000000);
                            Systrace.beginAsyncSection(0, "delayBeforeBatchRunStart", 0, UIViewOperationQueue.this.mProfiledBatchDispatchViewUpdatesTime * 1000000);
                            Systrace.endAsyncSection(0, "delayBeforeBatchRunStart", 0, UIViewOperationQueue.this.mProfiledBatchRunStartTime * 1000000);
                        }
                        UIViewOperationQueue.this.mNativeViewHierarchyManager.clearLayoutAnimation();
                        if (UIViewOperationQueue.this.mViewHierarchyUpdateDebugListener != null) {
                            UIViewOperationQueue.this.mViewHierarchyUpdateDebugListener.onViewHierarchyUpdateFinished();
                        }
                        Systrace.endSection(0);
                    } catch (Exception e) {
                        Exception exc = e;
                        UIViewOperationQueue.this.mIsInIllegalUIState = true;
                        throw exc;
                    } catch (Throwable th) {
                        Throwable th2 = th;
                        Systrace.endSection(0);
                        throw th2;
                    }
                }
            };
            SystraceMessage.beginSection(0, "acquiring mDispatchRunnablesLock").arg("batchId", i2).flush();
            synchronized (this.mDispatchRunnablesLock) {
                Systrace.endSection(0);
                this.mDispatchUIRunnables.add(r16);
            }
            if (!this.mIsDispatchUIFrameCallbackEnqueued) {
                UiThreadUtil.runOnUiThread(new GuardedRunnable(this.mReactApplicationContext) {
                    public void runGuarded() {
                        UIViewOperationQueue.this.flushPendingBatches();
                    }
                });
            }
            Systrace.endSection(0);
        } catch (Throwable th) {
            Throwable th2 = th;
            Systrace.endSection(0);
            throw th2;
        }
    }

    /* access modifiers changed from: 0000 */
    public void resumeFrameCallback() {
        this.mIsDispatchUIFrameCallbackEnqueued = true;
        ReactChoreographer.getInstance().postFrameCallback(CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
    }

    /* access modifiers changed from: 0000 */
    public void pauseFrameCallback() {
        this.mIsDispatchUIFrameCallbackEnqueued = false;
        ReactChoreographer.getInstance().removeFrameCallback(CallbackType.DISPATCH_UI, this.mDispatchUIFrameCallback);
        flushPendingBatches();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        r2 = android.os.SystemClock.uptimeMillis();
        r0 = r1.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
        if (r0.hasNext() == false) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
        ((java.lang.Runnable) r0.next()).run();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003d, code lost:
        if (r12.mIsProfilingNextBatch == false) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003f, code lost:
        r12.mProfiledBatchBatchedExecutionTime = android.os.SystemClock.uptimeMillis() - r2;
        r12.mProfiledBatchNonBatchedExecutionTime = r12.mNonBatchedExecutionTotalTime;
        r12.mIsProfilingNextBatch = false;
        com.facebook.systrace.Systrace.beginAsyncSection(0, "batchedExecutionTime", 0, 1000000 * r2);
        com.facebook.systrace.Systrace.endAsyncSection(0, "batchedExecutionTime", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0060, code lost:
        r12.mNonBatchedExecutionTotalTime = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0062, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void flushPendingBatches() {
        /*
            r12 = this;
            boolean r0 = r12.mIsInIllegalUIState
            if (r0 == 0) goto L_0x000c
            java.lang.String r0 = "ReactNative"
            java.lang.String r1 = "Not flushing pending UI operations because of previously thrown Exception"
            com.facebook.common.logging.FLog.m105w(r0, r1)
            return
        L_0x000c:
            java.lang.Object r0 = r12.mDispatchRunnablesLock
            monitor-enter(r0)
            java.util.ArrayList<java.lang.Runnable> r1 = r12.mDispatchUIRunnables     // Catch:{ all -> 0x0065 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0065 }
            if (r1 != 0) goto L_0x0063
            java.util.ArrayList<java.lang.Runnable> r1 = r12.mDispatchUIRunnables     // Catch:{ all -> 0x0065 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x0065 }
            r2.<init>()     // Catch:{ all -> 0x0065 }
            r12.mDispatchUIRunnables = r2     // Catch:{ all -> 0x0065 }
            monitor-exit(r0)     // Catch:{ all -> 0x0065 }
            long r2 = android.os.SystemClock.uptimeMillis()
            java.util.Iterator r0 = r1.iterator()
        L_0x0029:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0039
            java.lang.Object r1 = r0.next()
            java.lang.Runnable r1 = (java.lang.Runnable) r1
            r1.run()
            goto L_0x0029
        L_0x0039:
            boolean r0 = r12.mIsProfilingNextBatch
            r4 = 0
            if (r0 == 0) goto L_0x0060
            long r0 = android.os.SystemClock.uptimeMillis()
            long r6 = r0 - r2
            r12.mProfiledBatchBatchedExecutionTime = r6
            long r0 = r12.mNonBatchedExecutionTotalTime
            r12.mProfiledBatchNonBatchedExecutionTime = r0
            r0 = 0
            r12.mIsProfilingNextBatch = r0
            r6 = 0
            java.lang.String r8 = "batchedExecutionTime"
            r9 = 0
            r10 = 1000000(0xf4240, double:4.940656E-318)
            long r10 = r10 * r2
            com.facebook.systrace.Systrace.beginAsyncSection(r6, r8, r9, r10)
            java.lang.String r1 = "batchedExecutionTime"
            com.facebook.systrace.Systrace.endAsyncSection(r4, r1, r0)
        L_0x0060:
            r12.mNonBatchedExecutionTotalTime = r4
            return
        L_0x0063:
            monitor-exit(r0)     // Catch:{ all -> 0x0065 }
            return
        L_0x0065:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0065 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.UIViewOperationQueue.flushPendingBatches():void");
    }
}
