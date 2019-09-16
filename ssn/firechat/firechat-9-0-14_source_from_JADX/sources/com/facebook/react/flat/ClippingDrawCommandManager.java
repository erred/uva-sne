package com.facebook.react.flat;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.uimanager.ReactClippingViewGroup;
import com.facebook.react.uimanager.ReactClippingViewGroupHelper;
import java.util.ArrayList;
import java.util.Arrays;
import javax.annotation.Nullable;

abstract class ClippingDrawCommandManager extends DrawCommandManager {
    private static final String TAG = "ClippingDrawCommandManager";
    private final SparseArray<View> mClippedSubviews = new SparseArray<>();
    protected final Rect mClippingRect = new Rect();
    private final ArrayList<ReactClippingViewGroup> mClippingViewGroups = new ArrayList<>();
    protected float[] mCommandMaxBottom = StateBuilder.EMPTY_FLOAT_ARRAY;
    protected float[] mCommandMinTop = StateBuilder.EMPTY_FLOAT_ARRAY;
    private DrawCommand[] mDrawCommands = DrawCommand.EMPTY_ARRAY;
    private SparseIntArray mDrawViewIndexMap = StateBuilder.EMPTY_SPARSE_INT;
    private final FlatViewGroup mFlatViewGroup;
    private NodeRegion[] mNodeRegions = NodeRegion.EMPTY_ARRAY;
    protected float[] mRegionMaxBottom = StateBuilder.EMPTY_FLOAT_ARRAY;
    protected float[] mRegionMinTop = StateBuilder.EMPTY_FLOAT_ARRAY;
    private int mStart;
    private int mStop;
    private final ArrayList<View> mViewsToKeep = new ArrayList<>();
    private final SparseArray<View> mViewsToRemove = new SparseArray<>();

    /* access modifiers changed from: 0000 */
    public abstract int commandStartIndex();

    /* access modifiers changed from: 0000 */
    public abstract int commandStopIndex(int i);

    /* access modifiers changed from: 0000 */
    public abstract boolean regionAboveTouch(int i, float f, float f2);

    /* access modifiers changed from: 0000 */
    public abstract int regionStopIndex(float f, float f2);

    ClippingDrawCommandManager(FlatViewGroup flatViewGroup, DrawCommand[] drawCommandArr) {
        this.mFlatViewGroup = flatViewGroup;
        initialSetup(drawCommandArr);
    }

    private void initialSetup(DrawCommand[] drawCommandArr) {
        mountDrawCommands(drawCommandArr, this.mDrawViewIndexMap, this.mCommandMaxBottom, this.mCommandMinTop, true);
        updateClippingRect();
    }

    public void mountDrawCommands(DrawCommand[] drawCommandArr, SparseIntArray sparseIntArray, float[] fArr, float[] fArr2, boolean z) {
        this.mDrawCommands = drawCommandArr;
        this.mCommandMaxBottom = fArr;
        this.mCommandMinTop = fArr2;
        this.mDrawViewIndexMap = sparseIntArray;
        if (this.mClippingRect.bottom != this.mClippingRect.top) {
            this.mStart = commandStartIndex();
            this.mStop = commandStopIndex(this.mStart);
            if (!z) {
                updateClippingToCurrentRect();
            }
        }
    }

    public void mountNodeRegions(NodeRegion[] nodeRegionArr, float[] fArr, float[] fArr2) {
        this.mNodeRegions = nodeRegionArr;
        this.mRegionMaxBottom = fArr;
        this.mRegionMinTop = fArr2;
    }

    @Nullable
    public NodeRegion virtualNodeRegionWithinBounds(float f, float f2) {
        int regionStopIndex = regionStopIndex(f, f2);
        while (true) {
            int i = regionStopIndex - 1;
            if (regionStopIndex <= 0) {
                break;
            }
            NodeRegion nodeRegion = this.mNodeRegions[i];
            if (nodeRegion.mIsVirtual) {
                if (regionAboveTouch(i, f, f2)) {
                    break;
                } else if (nodeRegion.withinBounds(f, f2)) {
                    return nodeRegion;
                }
            }
            regionStopIndex = i;
        }
        return null;
    }

    @Nullable
    public NodeRegion anyNodeRegionWithinBounds(float f, float f2) {
        int regionStopIndex = regionStopIndex(f, f2);
        while (true) {
            int i = regionStopIndex - 1;
            if (regionStopIndex <= 0) {
                break;
            }
            NodeRegion nodeRegion = this.mNodeRegions[i];
            if (regionAboveTouch(i, f, f2)) {
                break;
            } else if (nodeRegion.withinBounds(f, f2)) {
                return nodeRegion;
            } else {
                regionStopIndex = i;
            }
        }
        return null;
    }

    private void clip(int i, View view) {
        this.mClippedSubviews.put(i, view);
    }

    private void unclip(int i) {
        this.mClippedSubviews.remove(i);
    }

    private boolean isClipped(int i) {
        return this.mClippedSubviews.get(i) != null;
    }

    private boolean isNotClipped(int i) {
        return this.mClippedSubviews.get(i) == null;
    }

    /* access modifiers changed from: 0000 */
    public void onClippedViewDropped(View view) {
        unclip(view.getId());
        this.mFlatViewGroup.removeDetachedView(view);
    }

    public void mountViews(ViewResolver viewResolver, int[] iArr, int[] iArr2) {
        this.mClippingViewGroups.clear();
        int length = iArr.length;
        for (int i = 0; i < length; i++) {
            int i2 = iArr[i];
            boolean z = i2 > 0;
            if (!z) {
                i2 = -i2;
            }
            int i3 = this.mDrawViewIndexMap.get(i2);
            DrawView drawView = (DrawView) this.mDrawCommands[i3];
            View view = viewResolver.getView(drawView.reactTag);
            ensureViewHasNoParent(view);
            if (view instanceof ReactClippingViewGroup) {
                ReactClippingViewGroup reactClippingViewGroup = (ReactClippingViewGroup) view;
                if (reactClippingViewGroup.getRemoveClippedSubviews()) {
                    this.mClippingViewGroups.add(reactClippingViewGroup);
                }
            }
            if (z) {
                drawView.mWasMounted = true;
                if (animating(view) || withinBounds(i3)) {
                    this.mFlatViewGroup.addViewInLayout(view);
                } else {
                    clip(drawView.reactTag, view);
                }
            } else if (!drawView.mWasMounted) {
                drawView.mWasMounted = true;
                if (animating(view) || withinBounds(i3)) {
                    if (isClipped(drawView.reactTag)) {
                        this.mFlatViewGroup.addViewInLayout(view);
                        unclip(drawView.reactTag);
                    } else {
                        this.mFlatViewGroup.attachViewToParent(view);
                    }
                } else if (isNotClipped(drawView.reactTag)) {
                    this.mFlatViewGroup.removeDetachedView(view);
                    clip(drawView.reactTag, view);
                }
            } else if (isNotClipped(drawView.reactTag)) {
                this.mFlatViewGroup.attachViewToParent(view);
            }
        }
        for (int i4 : iArr2) {
            View view2 = viewResolver.getView(i4);
            if (view2.getParent() != null) {
                throw new RuntimeException("Trying to remove view not owned by FlatViewGroup");
            }
            this.mFlatViewGroup.removeDetachedView(view2);
            unclip(i4);
        }
    }

    private static boolean animating(View view) {
        Animation animation = view.getAnimation();
        return animation != null && !animation.hasEnded();
    }

    private boolean withinBounds(int i) {
        return this.mStart <= i && i < this.mStop;
    }

    public boolean updateClippingRect() {
        ReactClippingViewGroupHelper.calculateClippingRect(this.mFlatViewGroup, this.mClippingRect);
        if (this.mFlatViewGroup.getParent() == null || this.mClippingRect.top == this.mClippingRect.bottom) {
            return false;
        }
        int commandStartIndex = commandStartIndex();
        int commandStopIndex = commandStopIndex(commandStartIndex);
        if (this.mStart > commandStartIndex || commandStopIndex > this.mStop) {
            this.mStart = commandStartIndex;
            this.mStop = commandStopIndex;
            updateClippingToCurrentRect();
            updateClippingRecursively();
            return true;
        }
        updateClippingRecursively();
        return false;
    }

    private void updateClippingRecursively() {
        int size = this.mClippingViewGroups.size();
        for (int i = 0; i < size; i++) {
            ReactClippingViewGroup reactClippingViewGroup = (ReactClippingViewGroup) this.mClippingViewGroups.get(i);
            if (isNotClipped(((View) reactClippingViewGroup).getId())) {
                reactClippingViewGroup.updateClippingRect();
            }
        }
    }

    private void updateClippingToCurrentRect() {
        int childCount = this.mFlatViewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mFlatViewGroup.getChildAt(i);
            if (withinBounds(this.mDrawViewIndexMap.get(childAt.getId())) || animating(childAt)) {
                this.mViewsToKeep.add(childAt);
            } else {
                this.mViewsToRemove.append(i, childAt);
                clip(childAt.getId(), childAt);
            }
        }
        int size = this.mViewsToRemove.size();
        boolean z = size > 2;
        if (!z) {
            while (true) {
                int i2 = size - 1;
                if (size <= 0) {
                    break;
                }
                this.mFlatViewGroup.removeViewsInLayout(this.mViewsToRemove.keyAt(i2), 1);
                size = i2;
            }
        } else {
            this.mFlatViewGroup.detachAllViewsFromParent();
            for (int i3 = 0; i3 < size; i3++) {
                this.mFlatViewGroup.removeDetachedView((View) this.mViewsToRemove.valueAt(i3));
            }
        }
        this.mViewsToRemove.clear();
        int i4 = this.mStart;
        int size2 = this.mViewsToKeep.size();
        int i5 = 0;
        for (int i6 = 0; i6 < size2; i6++) {
            View view = (View) this.mViewsToKeep.get(i6);
            int i7 = this.mDrawViewIndexMap.get(view.getId());
            if (i4 <= i7) {
                while (i4 != i7) {
                    if (this.mDrawCommands[i4] instanceof DrawView) {
                        DrawView drawView = (DrawView) this.mDrawCommands[i4];
                        int i8 = i5 + 1;
                        this.mFlatViewGroup.addViewInLayout((View) Assertions.assumeNotNull(this.mClippedSubviews.get(drawView.reactTag)), i5);
                        unclip(drawView.reactTag);
                        i5 = i8;
                    }
                    i4++;
                }
                i4++;
            }
            if (z) {
                this.mFlatViewGroup.attachViewToParent(view, i5);
            }
            i5++;
        }
        this.mViewsToKeep.clear();
        while (i4 < this.mStop) {
            if (this.mDrawCommands[i4] instanceof DrawView) {
                DrawView drawView2 = (DrawView) this.mDrawCommands[i4];
                int i9 = i5 + 1;
                this.mFlatViewGroup.addViewInLayout((View) Assertions.assumeNotNull(this.mClippedSubviews.get(drawView2.reactTag)), i5);
                unclip(drawView2.reactTag);
                i5 = i9;
            }
            i4++;
        }
    }

    public void getClippingRect(Rect rect) {
        rect.set(this.mClippingRect);
    }

    public SparseArray<View> getDetachedViews() {
        return this.mClippedSubviews;
    }

    public void draw(Canvas canvas) {
        int i = this.mStart;
        int childCount = this.mFlatViewGroup.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            int i3 = this.mDrawViewIndexMap.get(this.mFlatViewGroup.getChildAt(i2).getId());
            if (this.mStop < i3) {
                while (i < this.mStop) {
                    int i4 = i + 1;
                    this.mDrawCommands[i].draw(this.mFlatViewGroup, canvas);
                    i = i4;
                }
            } else if (i <= i3) {
                while (i < i3) {
                    int i5 = i + 1;
                    this.mDrawCommands[i].draw(this.mFlatViewGroup, canvas);
                    i = i5;
                }
                i++;
            }
            this.mDrawCommands[i3].draw(this.mFlatViewGroup, canvas);
        }
        while (i < this.mStop) {
            int i6 = i + 1;
            DrawCommand drawCommand = this.mDrawCommands[i];
            if (drawCommand instanceof DrawView) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Unexpected DrawView command at index ");
                sb.append(i6 - 1);
                sb.append(" with mStop=");
                sb.append(this.mStop);
                sb.append(". ");
                sb.append(Arrays.toString(this.mDrawCommands));
                FLog.m105w(str, sb.toString());
            } else {
                drawCommand.draw(this.mFlatViewGroup, canvas);
            }
            i = i6;
        }
    }

    /* access modifiers changed from: 0000 */
    public void debugDraw(Canvas canvas) {
        DrawCommand[] drawCommandArr;
        for (DrawCommand drawCommand : this.mDrawCommands) {
            if (!(drawCommand instanceof DrawView)) {
                drawCommand.debugDraw(this.mFlatViewGroup, canvas);
            } else if (isNotClipped(((DrawView) drawCommand).reactTag)) {
                drawCommand.debugDraw(this.mFlatViewGroup, canvas);
            }
        }
    }
}
