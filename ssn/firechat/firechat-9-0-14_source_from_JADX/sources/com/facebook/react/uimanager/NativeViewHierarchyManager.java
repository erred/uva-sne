package com.facebook.react.uimanager;

import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.amplitude.api.DeviceInfo;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.animation.Animation;
import com.facebook.react.animation.AnimationListener;
import com.facebook.react.animation.AnimationRegistry;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.touch.JSResponderHandler;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationController;
import com.facebook.react.uimanager.layoutanimation.LayoutAnimationListener;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.commons.lang3.StringUtils;

@NotThreadSafe
public class NativeViewHierarchyManager {
    private static final String TAG = "NativeViewHierarchyManager";
    /* access modifiers changed from: private */
    public final AnimationRegistry mAnimationRegistry;
    private final JSResponderHandler mJSResponderHandler;
    private boolean mLayoutAnimationEnabled;
    private final LayoutAnimationController mLayoutAnimator;
    private final SparseBooleanArray mRootTags;
    private final RootViewManager mRootViewManager;
    private final SparseArray<ViewManager> mTagsToViewManagers;
    private final SparseArray<View> mTagsToViews;
    private final ViewManagerRegistry mViewManagers;

    private static class PopupMenuCallbackHandler implements OnMenuItemClickListener, OnDismissListener {
        boolean mConsumed;
        final Callback mSuccess;

        private PopupMenuCallbackHandler(Callback callback) {
            this.mConsumed = false;
            this.mSuccess = callback;
        }

        public void onDismiss(PopupMenu popupMenu) {
            if (!this.mConsumed) {
                this.mSuccess.invoke(UIManagerModuleConstants.ACTION_DISMISSED);
                this.mConsumed = true;
            }
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            if (this.mConsumed) {
                return false;
            }
            this.mSuccess.invoke(UIManagerModuleConstants.ACTION_ITEM_SELECTED, Integer.valueOf(menuItem.getOrder()));
            this.mConsumed = true;
            return true;
        }
    }

    public NativeViewHierarchyManager(ViewManagerRegistry viewManagerRegistry) {
        this(viewManagerRegistry, new RootViewManager());
    }

    public NativeViewHierarchyManager(ViewManagerRegistry viewManagerRegistry, RootViewManager rootViewManager) {
        this.mJSResponderHandler = new JSResponderHandler();
        this.mLayoutAnimator = new LayoutAnimationController();
        this.mAnimationRegistry = new AnimationRegistry();
        this.mViewManagers = viewManagerRegistry;
        this.mTagsToViews = new SparseArray<>();
        this.mTagsToViewManagers = new SparseArray<>();
        this.mRootTags = new SparseBooleanArray();
        this.mRootViewManager = rootViewManager;
    }

    public final synchronized View resolveView(int i) {
        View view;
        view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to resolve view with tag ");
            sb.append(i);
            sb.append(" which doesn't exist");
            throw new IllegalViewOperationException(sb.toString());
        }
        return view;
    }

    public final synchronized ViewManager resolveViewManager(int i) {
        ViewManager viewManager;
        viewManager = (ViewManager) this.mTagsToViewManagers.get(i);
        if (viewManager == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("ViewManager for tag ");
            sb.append(i);
            sb.append(" could not be found");
            throw new IllegalViewOperationException(sb.toString());
        }
        return viewManager;
    }

    public AnimationRegistry getAnimationRegistry() {
        return this.mAnimationRegistry;
    }

    public void setLayoutAnimationEnabled(boolean z) {
        this.mLayoutAnimationEnabled = z;
    }

    public synchronized void updateProperties(int i, ReactStylesDiffMap reactStylesDiffMap) {
        UiThreadUtil.assertOnUiThread();
        try {
            resolveViewManager(i).updateProperties(resolveView(i), reactStylesDiffMap);
        } catch (IllegalViewOperationException e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to update properties for view tag ");
            sb.append(i);
            Log.e(str, sb.toString(), e);
        }
        return;
    }

    public synchronized void updateViewExtraData(int i, Object obj) {
        UiThreadUtil.assertOnUiThread();
        resolveViewManager(i).updateExtraData(resolveView(i), obj);
    }

    public synchronized void updateLayout(int i, int i2, int i3, int i4, int i5, int i6) {
        UiThreadUtil.assertOnUiThread();
        SystraceMessage.beginSection(0, "NativeViewHierarchyManager_updateLayout").arg("parentTag", i).arg("tag", i2).flush();
        try {
            View resolveView = resolveView(i2);
            resolveView.measure(MeasureSpec.makeMeasureSpec(i5, ErrorDialogData.SUPPRESSED), MeasureSpec.makeMeasureSpec(i6, ErrorDialogData.SUPPRESSED));
            ViewParent parent = resolveView.getParent();
            if (parent instanceof RootView) {
                parent.requestLayout();
            }
            if (!this.mRootTags.get(i)) {
                ViewManager viewManager = (ViewManager) this.mTagsToViewManagers.get(i);
                if (viewManager instanceof ViewGroupManager) {
                    ViewGroupManager viewGroupManager = (ViewGroupManager) viewManager;
                    if (viewGroupManager != null && !viewGroupManager.needsCustomLayoutForChildren()) {
                        updateLayout(resolveView, i3, i4, i5, i6);
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Trying to use view with tag ");
                    sb.append(i2);
                    sb.append(" as a parent, but its Manager doesn't extends ViewGroupManager");
                    throw new IllegalViewOperationException(sb.toString());
                }
            } else {
                updateLayout(resolveView, i3, i4, i5, i6);
            }
        } finally {
            Systrace.endSection(0);
        }
    }

    private void updateLayout(View view, int i, int i2, int i3, int i4) {
        if (!this.mLayoutAnimationEnabled || !this.mLayoutAnimator.shouldAnimateLayout(view)) {
            view.layout(i, i2, i3 + i, i4 + i2);
        } else {
            this.mLayoutAnimator.applyLayoutUpdate(view, i, i2, i3, i4);
        }
    }

    public synchronized void createView(ThemedReactContext themedReactContext, int i, String str, @Nullable ReactStylesDiffMap reactStylesDiffMap) {
        UiThreadUtil.assertOnUiThread();
        SystraceMessage.beginSection(0, "NativeViewHierarchyManager_createView").arg("tag", i).arg("className", (Object) str).flush();
        try {
            ViewManager viewManager = this.mViewManagers.get(str);
            View createView = viewManager.createView(themedReactContext, this.mJSResponderHandler);
            this.mTagsToViews.put(i, createView);
            this.mTagsToViewManagers.put(i, viewManager);
            createView.setId(i);
            if (reactStylesDiffMap != null) {
                viewManager.updateProperties(createView, reactStylesDiffMap);
            }
        } finally {
            Systrace.endSection(0);
        }
    }

    private static String constructManageChildrenErrorMessage(ViewGroup viewGroup, ViewGroupManager viewGroupManager, @Nullable int[] iArr, @Nullable ViewAtIndex[] viewAtIndexArr, @Nullable int[] iArr2) {
        StringBuilder sb = new StringBuilder();
        if (viewGroup != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("View tag:");
            sb2.append(viewGroup.getId());
            sb2.append(StringUtils.f158LF);
            sb.append(sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  children(");
            sb3.append(viewGroupManager.getChildCount(viewGroup));
            sb3.append("): [\n");
            sb.append(sb3.toString());
            for (int i = 0; i < viewGroupManager.getChildCount(viewGroup); i += 16) {
                int i2 = 0;
                while (true) {
                    int i3 = i + i2;
                    if (i3 >= viewGroupManager.getChildCount(viewGroup) || i2 >= 16) {
                        sb.append(StringUtils.f158LF);
                    } else {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(viewGroupManager.getChildAt(viewGroup, i3).getId());
                        sb4.append(",");
                        sb.append(sb4.toString());
                        i2++;
                    }
                }
                sb.append(StringUtils.f158LF);
            }
            sb.append(" ],\n");
        }
        if (iArr != null) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("  indicesToRemove(");
            sb5.append(iArr.length);
            sb5.append("): [\n");
            sb.append(sb5.toString());
            for (int i4 = 0; i4 < iArr.length; i4 += 16) {
                int i5 = 0;
                while (true) {
                    int i6 = i4 + i5;
                    if (i6 >= iArr.length || i5 >= 16) {
                        sb.append(StringUtils.f158LF);
                    } else {
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append(iArr[i6]);
                        sb6.append(",");
                        sb.append(sb6.toString());
                        i5++;
                    }
                }
                sb.append(StringUtils.f158LF);
            }
            sb.append(" ],\n");
        }
        if (viewAtIndexArr != null) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append("  viewsToAdd(");
            sb7.append(viewAtIndexArr.length);
            sb7.append("): [\n");
            sb.append(sb7.toString());
            for (int i7 = 0; i7 < viewAtIndexArr.length; i7 += 16) {
                int i8 = 0;
                while (true) {
                    int i9 = i7 + i8;
                    if (i9 >= viewAtIndexArr.length || i8 >= 16) {
                        sb.append(StringUtils.f158LF);
                    } else {
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("[");
                        sb8.append(viewAtIndexArr[i9].mIndex);
                        sb8.append(",");
                        sb8.append(viewAtIndexArr[i9].mTag);
                        sb8.append("],");
                        sb.append(sb8.toString());
                        i8++;
                    }
                }
                sb.append(StringUtils.f158LF);
            }
            sb.append(" ],\n");
        }
        if (iArr2 != null) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append("  tagsToDelete(");
            sb9.append(iArr2.length);
            sb9.append("): [\n");
            sb.append(sb9.toString());
            for (int i10 = 0; i10 < iArr2.length; i10 += 16) {
                int i11 = 0;
                while (true) {
                    int i12 = i10 + i11;
                    if (i12 >= iArr2.length || i11 >= 16) {
                        sb.append(StringUtils.f158LF);
                    } else {
                        StringBuilder sb10 = new StringBuilder();
                        sb10.append(iArr2[i12]);
                        sb10.append(",");
                        sb.append(sb10.toString());
                        i11++;
                    }
                }
                sb.append(StringUtils.f158LF);
            }
            sb.append(" ]\n");
        }
        return sb.toString();
    }

    public synchronized void manageChildren(int i, @Nullable int[] iArr, @Nullable ViewAtIndex[] viewAtIndexArr, @Nullable int[] iArr2) {
        UiThreadUtil.assertOnUiThread();
        final ViewGroup viewGroup = (ViewGroup) this.mTagsToViews.get(i);
        final ViewGroupManager viewGroupManager = (ViewGroupManager) resolveViewManager(i);
        if (viewGroup == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to manageChildren view with tag ");
            sb.append(i);
            sb.append(" which doesn't exist\n detail: ");
            sb.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
            throw new IllegalViewOperationException(sb.toString());
        }
        int childCount = viewGroupManager.getChildCount(viewGroup);
        if (iArr != null) {
            int length = iArr.length - 1;
            while (length >= 0) {
                int i2 = iArr[length];
                if (i2 < 0) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Trying to remove a negative view index:");
                    sb2.append(i2);
                    sb2.append(" view tag: ");
                    sb2.append(i);
                    sb2.append("\n detail: ");
                    sb2.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
                    throw new IllegalViewOperationException(sb2.toString());
                } else if (i2 >= viewGroupManager.getChildCount(viewGroup)) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Trying to remove a view index above child count ");
                    sb3.append(i2);
                    sb3.append(" view tag: ");
                    sb3.append(i);
                    sb3.append("\n detail: ");
                    sb3.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
                    throw new IllegalViewOperationException(sb3.toString());
                } else if (i2 >= childCount) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Trying to remove an out of order view index:");
                    sb4.append(i2);
                    sb4.append(" view tag: ");
                    sb4.append(i);
                    sb4.append("\n detail: ");
                    sb4.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
                    throw new IllegalViewOperationException(sb4.toString());
                } else {
                    View childAt = viewGroupManager.getChildAt(viewGroup, i2);
                    if (!this.mLayoutAnimationEnabled || !this.mLayoutAnimator.shouldAnimateLayout(childAt) || !arrayContains(iArr2, childAt.getId())) {
                        viewGroupManager.removeViewAt(viewGroup, i2);
                    }
                    length--;
                    childCount = i2;
                }
            }
        }
        if (viewAtIndexArr != null) {
            for (ViewAtIndex viewAtIndex : viewAtIndexArr) {
                View view = (View) this.mTagsToViews.get(viewAtIndex.mTag);
                if (view == null) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append("Trying to add unknown view tag: ");
                    sb5.append(viewAtIndex.mTag);
                    sb5.append("\n detail: ");
                    sb5.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
                    throw new IllegalViewOperationException(sb5.toString());
                }
                viewGroupManager.addView(viewGroup, view, viewAtIndex.mIndex);
            }
        }
        if (iArr2 != null) {
            for (int i3 : iArr2) {
                final View view2 = (View) this.mTagsToViews.get(i3);
                if (view2 == null) {
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("Trying to destroy unknown view tag: ");
                    sb6.append(i3);
                    sb6.append("\n detail: ");
                    sb6.append(constructManageChildrenErrorMessage(viewGroup, viewGroupManager, iArr, viewAtIndexArr, iArr2));
                    throw new IllegalViewOperationException(sb6.toString());
                }
                if (!this.mLayoutAnimationEnabled || !this.mLayoutAnimator.shouldAnimateLayout(view2)) {
                    dropView(view2);
                } else {
                    this.mLayoutAnimator.deleteView(view2, new LayoutAnimationListener() {
                        public void onAnimationEnd() {
                            viewGroupManager.removeView(viewGroup, view2);
                            NativeViewHierarchyManager.this.dropView(view2);
                        }
                    });
                }
            }
        }
    }

    private boolean arrayContains(@Nullable int[] iArr, int i) {
        if (iArr == null) {
            return false;
        }
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    private static String constructSetChildrenErrorMessage(ViewGroup viewGroup, ViewGroupManager viewGroupManager, ReadableArray readableArray) {
        ViewAtIndex[] viewAtIndexArr = new ViewAtIndex[readableArray.size()];
        for (int i = 0; i < readableArray.size(); i++) {
            viewAtIndexArr[i] = new ViewAtIndex(readableArray.getInt(i), i);
        }
        return constructManageChildrenErrorMessage(viewGroup, viewGroupManager, null, viewAtIndexArr, null);
    }

    public synchronized void setChildren(int i, ReadableArray readableArray) {
        UiThreadUtil.assertOnUiThread();
        ViewGroup viewGroup = (ViewGroup) this.mTagsToViews.get(i);
        ViewGroupManager viewGroupManager = (ViewGroupManager) resolveViewManager(i);
        for (int i2 = 0; i2 < readableArray.size(); i2++) {
            View view = (View) this.mTagsToViews.get(readableArray.getInt(i2));
            if (view == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Trying to add unknown view tag: ");
                sb.append(readableArray.getInt(i2));
                sb.append("\n detail: ");
                sb.append(constructSetChildrenErrorMessage(viewGroup, viewGroupManager, readableArray));
                throw new IllegalViewOperationException(sb.toString());
            }
            viewGroupManager.addView(viewGroup, view, i2);
        }
    }

    public synchronized void addRootView(int i, SizeMonitoringFrameLayout sizeMonitoringFrameLayout, ThemedReactContext themedReactContext) {
        addRootViewGroup(i, sizeMonitoringFrameLayout, themedReactContext);
    }

    /* access modifiers changed from: protected */
    public final synchronized void addRootViewGroup(int i, ViewGroup viewGroup, ThemedReactContext themedReactContext) {
        if (viewGroup.getId() != -1) {
            throw new IllegalViewOperationException("Trying to add a root view with an explicit id already set. React Native uses the id field to track react tags and will overwrite this field. If that is fine, explicitly overwrite the id field to View.NO_ID before calling addRootView.");
        }
        this.mTagsToViews.put(i, viewGroup);
        this.mTagsToViewManagers.put(i, this.mRootViewManager);
        this.mRootTags.put(i, true);
        viewGroup.setId(i);
    }

    /* access modifiers changed from: protected */
    public synchronized void dropView(View view) {
        UiThreadUtil.assertOnUiThread();
        if (!this.mRootTags.get(view.getId())) {
            resolveViewManager(view.getId()).onDropViewInstance(view);
        }
        ViewManager viewManager = (ViewManager) this.mTagsToViewManagers.get(view.getId());
        if ((view instanceof ViewGroup) && (viewManager instanceof ViewGroupManager)) {
            ViewGroup viewGroup = (ViewGroup) view;
            ViewGroupManager viewGroupManager = (ViewGroupManager) viewManager;
            for (int childCount = viewGroupManager.getChildCount(viewGroup) - 1; childCount >= 0; childCount--) {
                View childAt = viewGroupManager.getChildAt(viewGroup, childCount);
                if (this.mTagsToViews.get(childAt.getId()) != null) {
                    dropView(childAt);
                }
            }
            viewGroupManager.removeAllViews(viewGroup);
        }
        this.mTagsToViews.remove(view.getId());
        this.mTagsToViewManagers.remove(view.getId());
    }

    public synchronized void removeRootView(int i) {
        UiThreadUtil.assertOnUiThread();
        if (!this.mRootTags.get(i)) {
            StringBuilder sb = new StringBuilder();
            sb.append("View with tag ");
            sb.append(i);
            sb.append(" is not registered as a root view");
            SoftAssertions.assertUnreachable(sb.toString());
        }
        dropView((View) this.mTagsToViews.get(i));
        this.mRootTags.delete(i);
    }

    public synchronized void measure(int i, int[] iArr) {
        UiThreadUtil.assertOnUiThread();
        View view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No native view for ");
            sb.append(i);
            sb.append(" currently exists");
            throw new NoSuchNativeViewException(sb.toString());
        }
        View view2 = (View) RootViewUtil.getRootView(view);
        if (view2 == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Native view ");
            sb2.append(i);
            sb2.append(" is no longer on screen");
            throw new NoSuchNativeViewException(sb2.toString());
        }
        view2.getLocationInWindow(iArr);
        int i2 = iArr[0];
        int i3 = iArr[1];
        view.getLocationInWindow(iArr);
        iArr[0] = iArr[0] - i2;
        iArr[1] = iArr[1] - i3;
        iArr[2] = view.getWidth();
        iArr[3] = view.getHeight();
    }

    public synchronized void measureInWindow(int i, int[] iArr) {
        UiThreadUtil.assertOnUiThread();
        View view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("No native view for ");
            sb.append(i);
            sb.append(" currently exists");
            throw new NoSuchNativeViewException(sb.toString());
        }
        view.getLocationOnScreen(iArr);
        Resources resources = view.getContext().getResources();
        int identifier = resources.getIdentifier("status_bar_height", "dimen", DeviceInfo.OS_NAME);
        if (identifier > 0) {
            iArr[1] = iArr[1] - ((int) resources.getDimension(identifier));
        }
        iArr[2] = view.getWidth();
        iArr[3] = view.getHeight();
    }

    public synchronized int findTargetTagForTouch(int i, float f, float f2) {
        View view;
        UiThreadUtil.assertOnUiThread();
        view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find view with tag ");
            sb.append(i);
            throw new JSApplicationIllegalArgumentException(sb.toString());
        }
        return TouchTargetHelper.findTargetTagForTouch(f, f2, (ViewGroup) view);
    }

    public synchronized void setJSResponder(int i, int i2, boolean z) {
        if (!z) {
            this.mJSResponderHandler.setJSResponder(i2, null);
            return;
        }
        View view = (View) this.mTagsToViews.get(i);
        if (i2 == i || !(view instanceof ViewParent)) {
            if (this.mRootTags.get(i)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot block native responder on ");
                sb.append(i);
                sb.append(" that is a root view");
                SoftAssertions.assertUnreachable(sb.toString());
            }
            this.mJSResponderHandler.setJSResponder(i2, view.getParent());
            return;
        }
        this.mJSResponderHandler.setJSResponder(i2, (ViewParent) view);
    }

    public void clearJSResponder() {
        this.mJSResponderHandler.clearJSResponder();
    }

    /* access modifiers changed from: 0000 */
    public void configureLayoutAnimation(ReadableMap readableMap) {
        this.mLayoutAnimator.initializeFromConfig(readableMap);
    }

    /* access modifiers changed from: 0000 */
    public void clearLayoutAnimation() {
        this.mLayoutAnimator.reset();
    }

    /* access modifiers changed from: 0000 */
    public synchronized void startAnimationForNativeView(int i, Animation animation, @Nullable final Callback callback) {
        UiThreadUtil.assertOnUiThread();
        View view = (View) this.mTagsToViews.get(i);
        final int animationID = animation.getAnimationID();
        if (view != null) {
            animation.setAnimationListener(new AnimationListener() {
                public void onFinished() {
                    Assertions.assertNotNull(NativeViewHierarchyManager.this.mAnimationRegistry.removeAnimation(animationID), "Animation was already removed somehow!");
                    if (callback != null) {
                        callback.invoke(Boolean.valueOf(true));
                    }
                }

                public void onCancel() {
                    Assertions.assertNotNull(NativeViewHierarchyManager.this.mAnimationRegistry.removeAnimation(animationID), "Animation was already removed somehow!");
                    if (callback != null) {
                        callback.invoke(Boolean.valueOf(false));
                    }
                }
            });
            animation.start(view);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("View with tag ");
            sb.append(i);
            sb.append(" not found");
            throw new IllegalViewOperationException(sb.toString());
        }
    }

    public synchronized void dispatchCommand(int i, int i2, @Nullable ReadableArray readableArray) {
        UiThreadUtil.assertOnUiThread();
        View view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Trying to send command to a non-existing view with tag ");
            sb.append(i);
            throw new IllegalViewOperationException(sb.toString());
        }
        resolveViewManager(i).receiveCommand(view, i2, readableArray);
    }

    public synchronized void showPopupMenu(int i, ReadableArray readableArray, Callback callback) {
        UiThreadUtil.assertOnUiThread();
        View view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find view with tag ");
            sb.append(i);
            throw new JSApplicationIllegalArgumentException(sb.toString());
        }
        PopupMenu popupMenu = new PopupMenu(getReactContextForView(i), view);
        Menu menu = popupMenu.getMenu();
        for (int i2 = 0; i2 < readableArray.size(); i2++) {
            menu.add(0, 0, i2, readableArray.getString(i2));
        }
        PopupMenuCallbackHandler popupMenuCallbackHandler = new PopupMenuCallbackHandler(callback);
        popupMenu.setOnMenuItemClickListener(popupMenuCallbackHandler);
        popupMenu.setOnDismissListener(popupMenuCallbackHandler);
        popupMenu.show();
    }

    private ThemedReactContext getReactContextForView(int i) {
        View view = (View) this.mTagsToViews.get(i);
        if (view != null) {
            return (ThemedReactContext) view.getContext();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not find view with tag ");
        sb.append(i);
        throw new JSApplicationIllegalArgumentException(sb.toString());
    }

    public void sendAccessibilityEvent(int i, int i2) {
        View view = (View) this.mTagsToViews.get(i);
        if (view == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not find view with tag ");
            sb.append(i);
            throw new JSApplicationIllegalArgumentException(sb.toString());
        }
        AccessibilityHelper.sendAccessibilityEvent(view, i2);
    }
}
