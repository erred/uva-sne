package com.facebook.react.flat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.p000v4.internal.view.SupportMenu;
import android.support.p000v4.view.ViewCompat;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.touch.OnInterceptTouchEventListener;
import com.facebook.react.touch.ReactHitSlopView;
import com.facebook.react.touch.ReactInterceptingViewGroup;
import com.facebook.react.uimanager.PointerEvents;
import com.facebook.react.uimanager.ReactClippingViewGroup;
import com.facebook.react.uimanager.ReactCompoundViewGroup;
import com.facebook.react.uimanager.ReactPointerEventsView;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.image.ImageLoadEvent;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.annotation.Nullable;

final class FlatViewGroup extends ViewGroup implements ReactInterceptingViewGroup, ReactClippingViewGroup, ReactCompoundViewGroup, ReactHitSlopView, ReactPointerEventsView, FlatMeasuredViewGroup {
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_DRAW_TEXT = false;
    static final boolean DEBUG_HIGHLIGHT_PERFORMANCE_ISSUES = false;
    private static final SparseArray<View> EMPTY_DETACHED_VIEWS = new SparseArray<>(0);
    private static final ArrayList<FlatViewGroup> LAYOUT_REQUESTS = new ArrayList<>();
    private static final Rect VIEW_BOUNDS = new Rect();
    private static Paint sDebugCornerPaint;
    private static Rect sDebugRect;
    private static Paint sDebugRectPaint;
    private static Paint sDebugTextBackgroundPaint;
    private static Paint sDebugTextPaint;
    private boolean mAndroidDebugDraw;
    private AttachDetachListener[] mAttachDetachListeners = AttachDetachListener.EMPTY_ARRAY;
    private int mDrawChildIndex = 0;
    @Nullable
    private DrawCommandManager mDrawCommandManager;
    private DrawCommand[] mDrawCommands = DrawCommand.EMPTY_ARRAY;
    @Nullable
    private Rect mHitSlopRect;
    private Drawable mHotspot;
    @Nullable
    private InvalidateCallback mInvalidateCallback;
    private boolean mIsAttached = false;
    private boolean mIsLayoutRequested = false;
    private long mLastTouchDownTime;
    private boolean mNeedsOffscreenAlphaCompositing = false;
    private NodeRegion[] mNodeRegions = NodeRegion.EMPTY_ARRAY;
    @Nullable
    private OnInterceptTouchEventListener mOnInterceptTouchEventListener;
    private PointerEvents mPointerEvents = PointerEvents.AUTO;

    static final class InvalidateCallback extends WeakReference<FlatViewGroup> {
        private InvalidateCallback(FlatViewGroup flatViewGroup) {
            super(flatViewGroup);
        }

        public void invalidate() {
            FlatViewGroup flatViewGroup = (FlatViewGroup) get();
            if (flatViewGroup != null) {
                flatViewGroup.invalidate();
            }
        }

        public void dispatchImageLoadEvent(int i, int i2) {
            FlatViewGroup flatViewGroup = (FlatViewGroup) get();
            if (flatViewGroup != null) {
                ((UIManagerModule) ((ReactContext) flatViewGroup.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new ImageLoadEvent(i, i2));
            }
        }
    }

    private static int sign(float f) {
        return f >= 0.0f ? 1 : -1;
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingSuperCall"})
    public boolean verifyDrawable(Drawable drawable) {
        return true;
    }

    FlatViewGroup(Context context) {
        super(context);
        setClipChildren(false);
    }

    /* access modifiers changed from: protected */
    public void detachAllViewsFromParent() {
        super.detachAllViewsFromParent();
    }

    @SuppressLint({"MissingSuperCall"})
    public void requestLayout() {
        if (!this.mIsLayoutRequested) {
            this.mIsLayoutRequested = true;
            LAYOUT_REQUESTS.add(this);
        }
    }

    public int reactTagForTouch(float f, float f2) {
        SoftAssertions.assertCondition(this.mPointerEvents != PointerEvents.NONE, "TouchTargetHelper should not allow calling this method when pointer events are NONE");
        if (this.mPointerEvents != PointerEvents.BOX_ONLY) {
            NodeRegion virtualNodeRegionWithinBounds = virtualNodeRegionWithinBounds(f, f2);
            if (virtualNodeRegionWithinBounds != null) {
                return virtualNodeRegionWithinBounds.getReactTag(f, f2);
            }
        }
        return getId();
    }

    public boolean interceptsTouchEvent(float f, float f2) {
        NodeRegion anyNodeRegionWithinBounds = anyNodeRegionWithinBounds(f, f2);
        return anyNodeRegionWithinBounds != null && anyNodeRegionWithinBounds.mIsVirtual;
    }

    /* access modifiers changed from: protected */
    public void onDebugDraw(Canvas canvas) {
        this.mAndroidDebugDraw = true;
    }

    public void dispatchDraw(Canvas canvas) {
        this.mAndroidDebugDraw = false;
        super.dispatchDraw(canvas);
        if (this.mDrawCommandManager != null) {
            this.mDrawCommandManager.draw(canvas);
        } else {
            for (DrawCommand draw : this.mDrawCommands) {
                draw.draw(this, canvas);
            }
        }
        if (this.mDrawChildIndex != getChildCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Did not draw all children: ");
            sb.append(this.mDrawChildIndex);
            sb.append(" / ");
            sb.append(getChildCount());
            throw new RuntimeException(sb.toString());
        }
        this.mDrawChildIndex = 0;
        if (this.mAndroidDebugDraw) {
            initDebugDrawResources();
            debugDraw(canvas);
        }
        if (this.mHotspot != null) {
            this.mHotspot.draw(canvas);
        }
    }

    private void debugDraw(Canvas canvas) {
        if (this.mDrawCommandManager != null) {
            this.mDrawCommandManager.debugDraw(canvas);
        } else {
            for (DrawCommand debugDraw : this.mDrawCommands) {
                debugDraw.debugDraw(this, canvas);
            }
        }
        this.mDrawChildIndex = 0;
    }

    /* access modifiers changed from: 0000 */
    public void debugDrawNextChild(Canvas canvas) {
        View childAt = getChildAt(this.mDrawChildIndex);
        debugDrawRect(canvas, childAt instanceof FlatViewGroup ? -12303292 : SupportMenu.CATEGORY_MASK, (float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
        this.mDrawChildIndex++;
    }

    /* access modifiers changed from: 0000 */
    public int dipsToPixels(int i) {
        return (int) ((((float) i) * getResources().getDisplayMetrics().density) + 0.5f);
    }

    private static void fillRect(Canvas canvas, Paint paint, float f, float f2, float f3, float f4) {
        float f5;
        float f6;
        float f7;
        float f8;
        if (f != f3 && f2 != f4) {
            if (f > f3) {
                f5 = f;
                f6 = f3;
            } else {
                f6 = f;
                f5 = f3;
            }
            if (f2 > f4) {
                f7 = f2;
                f8 = f4;
            } else {
                f8 = f2;
                f7 = f4;
            }
            canvas.drawRect(f6, f8, f5, f7, paint);
        }
    }

    private static void drawCorner(Canvas canvas, Paint paint, float f, float f2, float f3, float f4, float f5) {
        Canvas canvas2 = canvas;
        Paint paint2 = paint;
        float f6 = f;
        float f7 = f2;
        fillRect(canvas2, paint2, f6, f7, f + f3, f2 + (((float) sign(f4)) * f5));
        fillRect(canvas2, paint2, f6, f7, f + (f5 * ((float) sign(f3))), f2 + f4);
    }

    private static void drawRectCorners(Canvas canvas, float f, float f2, float f3, float f4, Paint paint, int i, int i2) {
        float f5 = (float) i;
        Canvas canvas2 = canvas;
        Paint paint2 = paint;
        float f6 = f;
        float f7 = f5;
        float f8 = (float) i2;
        drawCorner(canvas2, paint2, f6, f2, f7, f5, f8);
        float f9 = (float) (-i);
        drawCorner(canvas2, paint2, f6, f4, f7, f9, f8);
        float f10 = f3;
        float f11 = f9;
        drawCorner(canvas2, paint2, f10, f2, f11, f5, f8);
        drawCorner(canvas2, paint2, f10, f4, f11, f9, f8);
    }

    private void initDebugDrawResources() {
        if (sDebugTextPaint == null) {
            sDebugTextPaint = new Paint();
            sDebugTextPaint.setTextAlign(Align.RIGHT);
            sDebugTextPaint.setTextSize((float) dipsToPixels(9));
            sDebugTextPaint.setTypeface(Typeface.MONOSPACE);
            sDebugTextPaint.setAntiAlias(true);
            sDebugTextPaint.setColor(SupportMenu.CATEGORY_MASK);
        }
        if (sDebugTextBackgroundPaint == null) {
            sDebugTextBackgroundPaint = new Paint();
            sDebugTextBackgroundPaint.setColor(-1);
            sDebugTextBackgroundPaint.setAlpha(200);
            sDebugTextBackgroundPaint.setStyle(Style.FILL);
        }
        if (sDebugRectPaint == null) {
            sDebugRectPaint = new Paint();
            sDebugRectPaint.setAlpha(100);
            sDebugRectPaint.setStyle(Style.STROKE);
        }
        if (sDebugCornerPaint == null) {
            sDebugCornerPaint = new Paint();
            sDebugCornerPaint.setAlpha(200);
            sDebugCornerPaint.setColor(Color.rgb(63, 127, 255));
            sDebugCornerPaint.setStyle(Style.FILL);
        }
        if (sDebugRect == null) {
            sDebugRect = new Rect();
        }
    }

    private void debugDrawRect(Canvas canvas, int i, float f, float f2, float f3, float f4) {
        debugDrawNamedRect(canvas, i, "", f, f2, f3, f4);
    }

    /* access modifiers changed from: 0000 */
    public void debugDrawNamedRect(Canvas canvas, int i, String str, float f, float f2, float f3, float f4) {
        sDebugRectPaint.setColor((sDebugRectPaint.getColor() & ViewCompat.MEASURED_STATE_MASK) | (16777215 & i));
        sDebugRectPaint.setAlpha(100);
        canvas.drawRect(f, f2, f3 - 1.0f, f4 - 1.0f, sDebugRectPaint);
        drawRectCorners(canvas, f, f2, f3, f4, sDebugCornerPaint, dipsToPixels(8), dipsToPixels(1));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        if (!this.mIsAttached) {
            this.mIsAttached = true;
            super.onAttachedToWindow();
            dispatchOnAttached(this.mAttachDetachListeners);
            updateClippingRect();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        if (!this.mIsAttached) {
            throw new RuntimeException("Double detach");
        }
        this.mIsAttached = false;
        super.onDetachedFromWindow();
        dispatchOnDetached(this.mAttachDetachListeners);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.mHotspot != null) {
            this.mHotspot.setBounds(0, 0, i, i2);
            invalidate();
        }
        updateClippingRect();
    }

    public void dispatchDrawableHotspotChanged(float f, float f2) {
        if (this.mHotspot != null) {
            this.mHotspot.setHotspot(f, f2);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mHotspot != null && this.mHotspot.isStateful()) {
            this.mHotspot.setState(getDrawableState());
        }
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mHotspot != null) {
            this.mHotspot.jumpToCurrentState();
        }
    }

    public void invalidate() {
        invalidate(0, 0, getWidth() + 1, getHeight() + 1);
    }

    public boolean hasOverlappingRendering() {
        return this.mNeedsOffscreenAlphaCompositing;
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener onInterceptTouchEventListener) {
        this.mOnInterceptTouchEventListener = onInterceptTouchEventListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        long downTime = motionEvent.getDownTime();
        if (downTime != this.mLastTouchDownTime) {
            this.mLastTouchDownTime = downTime;
            if (interceptsTouchEvent(motionEvent.getX(), motionEvent.getY())) {
                return true;
            }
        }
        if ((this.mOnInterceptTouchEventListener != null && this.mOnInterceptTouchEventListener.onInterceptTouchEvent(this, motionEvent)) || this.mPointerEvents == PointerEvents.NONE || this.mPointerEvents == PointerEvents.BOX_ONLY) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mPointerEvents == PointerEvents.NONE) {
            return false;
        }
        if (this.mPointerEvents == PointerEvents.BOX_NONE && virtualNodeRegionWithinBounds(motionEvent.getX(), motionEvent.getY()) == null) {
            return false;
        }
        return true;
    }

    public PointerEvents getPointerEvents() {
        return this.mPointerEvents;
    }

    /* access modifiers changed from: 0000 */
    public void setPointerEvents(PointerEvents pointerEvents) {
        this.mPointerEvents = pointerEvents;
    }

    /* access modifiers changed from: 0000 */
    public void setNeedsOffscreenAlphaCompositing(boolean z) {
        this.mNeedsOffscreenAlphaCompositing = z;
    }

    /* access modifiers changed from: 0000 */
    public void setHotspot(Drawable drawable) {
        if (this.mHotspot != null) {
            this.mHotspot.setCallback(null);
            unscheduleDrawable(this.mHotspot);
        }
        if (drawable != null) {
            drawable.setCallback(this);
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        this.mHotspot = drawable;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void drawNextChild(Canvas canvas) {
        View childAt = getChildAt(this.mDrawChildIndex);
        if (childAt instanceof FlatViewGroup) {
            super.drawChild(canvas, childAt, getDrawingTime());
        } else {
            canvas.save(2);
            childAt.getHitRect(VIEW_BOUNDS);
            canvas.clipRect(VIEW_BOUNDS);
            super.drawChild(canvas, childAt, getDrawingTime());
            canvas.restore();
        }
        this.mDrawChildIndex++;
    }

    /* access modifiers changed from: 0000 */
    public void mountDrawCommands(DrawCommand[] drawCommandArr) {
        this.mDrawCommands = drawCommandArr;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void mountClippingDrawCommands(DrawCommand[] drawCommandArr, SparseIntArray sparseIntArray, float[] fArr, float[] fArr2, boolean z) {
        ((DrawCommandManager) Assertions.assertNotNull(this.mDrawCommandManager)).mountDrawCommands(drawCommandArr, sparseIntArray, fArr, fArr2, z);
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void onViewDropped(View view) {
        if (this.mDrawCommandManager != null) {
            this.mDrawCommandManager.onClippedViewDropped(view);
        }
    }

    /* access modifiers changed from: 0000 */
    public NodeRegion getNodeRegionForTag(int i) {
        NodeRegion[] nodeRegionArr;
        for (NodeRegion nodeRegion : this.mNodeRegions) {
            if (nodeRegion.matchesTag(i)) {
                return nodeRegion;
            }
        }
        return NodeRegion.EMPTY;
    }

    /* access modifiers changed from: 0000 */
    public SparseArray<View> getDetachedViews() {
        if (this.mDrawCommandManager == null) {
            return EMPTY_DETACHED_VIEWS;
        }
        return this.mDrawCommandManager.getDetachedViews();
    }

    /* access modifiers changed from: 0000 */
    public void removeDetachedView(View view) {
        removeDetachedView(view, false);
    }

    public void removeAllViewsInLayout() {
        this.mDrawCommands = DrawCommand.EMPTY_ARRAY;
        super.removeAllViewsInLayout();
    }

    /* access modifiers changed from: 0000 */
    public void mountAttachDetachListeners(AttachDetachListener[] attachDetachListenerArr) {
        if (this.mIsAttached) {
            dispatchOnAttached(attachDetachListenerArr);
            dispatchOnDetached(this.mAttachDetachListeners);
        }
        this.mAttachDetachListeners = attachDetachListenerArr;
    }

    /* access modifiers changed from: 0000 */
    public void mountNodeRegions(NodeRegion[] nodeRegionArr) {
        this.mNodeRegions = nodeRegionArr;
    }

    /* access modifiers changed from: 0000 */
    public void mountClippingNodeRegions(NodeRegion[] nodeRegionArr, float[] fArr, float[] fArr2) {
        this.mNodeRegions = nodeRegionArr;
        ((DrawCommandManager) Assertions.assertNotNull(this.mDrawCommandManager)).mountNodeRegions(nodeRegionArr, fArr, fArr2);
    }

    /* access modifiers changed from: 0000 */
    public void mountViews(ViewResolver viewResolver, int[] iArr, int[] iArr2) {
        if (this.mDrawCommandManager != null) {
            this.mDrawCommandManager.mountViews(viewResolver, iArr, iArr2);
        } else {
            for (int i : iArr) {
                if (i > 0) {
                    View view = viewResolver.getView(i);
                    ensureViewHasNoParent(view);
                    addViewInLayout(view);
                } else {
                    View view2 = viewResolver.getView(-i);
                    ensureViewHasNoParent(view2);
                    attachViewToParent(view2);
                }
            }
            for (int view3 : iArr2) {
                View view4 = viewResolver.getView(view3);
                if (view4.getParent() != null) {
                    throw new RuntimeException("Trying to remove view not owned by FlatViewGroup");
                }
                removeDetachedView(view4, false);
            }
        }
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void addViewInLayout(View view) {
        addViewInLayout(view, -1, ensureLayoutParams(view.getLayoutParams()), true);
    }

    /* access modifiers changed from: 0000 */
    public void addViewInLayout(View view, int i) {
        addViewInLayout(view, i, ensureLayoutParams(view.getLayoutParams()), true);
    }

    /* access modifiers changed from: 0000 */
    public void attachViewToParent(View view) {
        attachViewToParent(view, -1, ensureLayoutParams(view.getLayoutParams()));
    }

    /* access modifiers changed from: 0000 */
    public void attachViewToParent(View view, int i) {
        attachViewToParent(view, i, ensureLayoutParams(view.getLayoutParams()));
    }

    private void processLayoutRequest() {
        this.mIsLayoutRequested = false;
        int childCount = getChildCount();
        for (int i = 0; i != childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.isLayoutRequested()) {
                childAt.measure(MeasureSpec.makeMeasureSpec(childAt.getWidth(), ErrorDialogData.SUPPRESSED), MeasureSpec.makeMeasureSpec(childAt.getHeight(), ErrorDialogData.SUPPRESSED));
                childAt.layout(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
            }
        }
    }

    static void processLayoutRequests() {
        int size = LAYOUT_REQUESTS.size();
        for (int i = 0; i != size; i++) {
            ((FlatViewGroup) LAYOUT_REQUESTS.get(i)).processLayoutRequest();
        }
        LAYOUT_REQUESTS.clear();
    }

    public Rect measureWithCommands() {
        DrawCommand[] drawCommandArr;
        int childCount = getChildCount();
        if (childCount == 0 && this.mDrawCommands.length == 0) {
            return new Rect(0, 0, 0, 0);
        }
        int i = Integer.MAX_VALUE;
        int i2 = Integer.MAX_VALUE;
        int i3 = Integer.MIN_VALUE;
        int i4 = Integer.MIN_VALUE;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            i = Math.min(i, childAt.getLeft());
            i2 = Math.min(i2, childAt.getTop());
            i3 = Math.max(i3, childAt.getRight());
            i4 = Math.max(i4, childAt.getBottom());
        }
        for (DrawCommand drawCommand : this.mDrawCommands) {
            if (drawCommand instanceof AbstractDrawCommand) {
                AbstractDrawCommand abstractDrawCommand = (AbstractDrawCommand) drawCommand;
                i = Math.min(i, Math.round(abstractDrawCommand.getLeft()));
                i2 = Math.min(i2, Math.round(abstractDrawCommand.getTop()));
                i3 = Math.max(i3, Math.round(abstractDrawCommand.getRight()));
                i4 = Math.max(i4, Math.round(abstractDrawCommand.getBottom()));
            }
        }
        return new Rect(i, i2, i3, i4);
    }

    @Nullable
    private NodeRegion virtualNodeRegionWithinBounds(float f, float f2) {
        if (this.mDrawCommandManager != null) {
            return this.mDrawCommandManager.virtualNodeRegionWithinBounds(f, f2);
        }
        for (int length = this.mNodeRegions.length - 1; length >= 0; length--) {
            NodeRegion nodeRegion = this.mNodeRegions[length];
            if (nodeRegion.mIsVirtual && nodeRegion.withinBounds(f, f2)) {
                return nodeRegion;
            }
        }
        return null;
    }

    @Nullable
    private NodeRegion anyNodeRegionWithinBounds(float f, float f2) {
        if (this.mDrawCommandManager != null) {
            return this.mDrawCommandManager.anyNodeRegionWithinBounds(f, f2);
        }
        for (int length = this.mNodeRegions.length - 1; length >= 0; length--) {
            NodeRegion nodeRegion = this.mNodeRegions[length];
            if (nodeRegion.withinBounds(f, f2)) {
                return nodeRegion;
            }
        }
        return null;
    }

    private static void ensureViewHasNoParent(View view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot add view ");
            sb.append(view);
            sb.append(" to FlatViewGroup while it has a parent ");
            sb.append(parent);
            throw new RuntimeException(sb.toString());
        }
    }

    private void dispatchOnAttached(AttachDetachListener[] attachDetachListenerArr) {
        if (attachDetachListenerArr.length != 0) {
            InvalidateCallback invalidateCallback = getInvalidateCallback();
            for (AttachDetachListener onAttached : attachDetachListenerArr) {
                onAttached.onAttached(invalidateCallback);
            }
        }
    }

    private InvalidateCallback getInvalidateCallback() {
        if (this.mInvalidateCallback == null) {
            this.mInvalidateCallback = new InvalidateCallback();
        }
        return this.mInvalidateCallback;
    }

    private static void dispatchOnDetached(AttachDetachListener[] attachDetachListenerArr) {
        for (AttachDetachListener onDetached : attachDetachListenerArr) {
            onDetached.onDetached();
        }
    }

    private LayoutParams ensureLayoutParams(LayoutParams layoutParams) {
        if (checkLayoutParams(layoutParams)) {
            return layoutParams;
        }
        return generateDefaultLayoutParams();
    }

    public void updateClippingRect() {
        if (this.mDrawCommandManager != null && this.mDrawCommandManager.updateClippingRect()) {
            invalidate();
        }
    }

    public void getClippingRect(Rect rect) {
        if (this.mDrawCommandManager == null) {
            throw new RuntimeException("Trying to get the clipping rect for a non-clipping FlatViewGroup");
        }
        this.mDrawCommandManager.getClippingRect(rect);
    }

    public void setRemoveClippedSubviews(boolean z) {
        boolean removeClippedSubviews = getRemoveClippedSubviews();
        if (z != removeClippedSubviews) {
            if (removeClippedSubviews) {
                throw new RuntimeException("Trying to transition FlatViewGroup from clipping to non-clipping state");
            }
            this.mDrawCommandManager = DrawCommandManager.getVerticalClippingInstance(this, this.mDrawCommands);
            this.mDrawCommands = DrawCommand.EMPTY_ARRAY;
        }
    }

    public boolean getRemoveClippedSubviews() {
        return this.mDrawCommandManager != null;
    }

    @Nullable
    public Rect getHitSlopRect() {
        return this.mHitSlopRect;
    }

    /* access modifiers changed from: 0000 */
    public void setHitSlopRect(@Nullable Rect rect) {
        this.mHitSlopRect = rect;
    }
}
