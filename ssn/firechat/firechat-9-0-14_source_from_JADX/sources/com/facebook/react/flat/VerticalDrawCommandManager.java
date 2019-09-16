package com.facebook.react.flat;

import android.util.SparseIntArray;
import java.util.Arrays;

final class VerticalDrawCommandManager extends ClippingDrawCommandManager {
    VerticalDrawCommandManager(FlatViewGroup flatViewGroup, DrawCommand[] drawCommandArr) {
        super(flatViewGroup, drawCommandArr);
    }

    /* access modifiers changed from: 0000 */
    public int commandStartIndex() {
        int binarySearch = Arrays.binarySearch(this.mCommandMaxBottom, (float) this.mClippingRect.top);
        return binarySearch < 0 ? binarySearch ^ -1 : binarySearch;
    }

    /* access modifiers changed from: 0000 */
    public int commandStopIndex(int i) {
        int binarySearch = Arrays.binarySearch(this.mCommandMinTop, i, this.mCommandMinTop.length, (float) this.mClippingRect.bottom);
        return binarySearch < 0 ? binarySearch ^ -1 : binarySearch;
    }

    /* access modifiers changed from: 0000 */
    public int regionStopIndex(float f, float f2) {
        int binarySearch = Arrays.binarySearch(this.mRegionMinTop, f2 + 1.0E-4f);
        return binarySearch < 0 ? binarySearch ^ -1 : binarySearch;
    }

    /* access modifiers changed from: 0000 */
    public boolean regionAboveTouch(int i, float f, float f2) {
        return this.mRegionMaxBottom[i] < f2;
    }

    public static void fillMaxMinArrays(NodeRegion[] nodeRegionArr, float[] fArr, float[] fArr2) {
        float f = 0.0f;
        for (int i = 0; i < nodeRegionArr.length; i++) {
            f = Math.max(f, nodeRegionArr[i].getTouchableBottom());
            fArr[i] = f;
        }
        for (int length = nodeRegionArr.length - 1; length >= 0; length--) {
            f = Math.min(f, nodeRegionArr[length].getTouchableTop());
            fArr2[length] = f;
        }
    }

    public static void fillMaxMinArrays(DrawCommand[] drawCommandArr, float[] fArr, float[] fArr2, SparseIntArray sparseIntArray) {
        float min;
        float f = 0.0f;
        for (int i = 0; i < drawCommandArr.length; i++) {
            if (drawCommandArr[i] instanceof DrawView) {
                DrawView drawView = drawCommandArr[i];
                sparseIntArray.append(drawView.reactTag, i);
                f = Math.max(f, drawView.mLogicalBottom);
            } else {
                f = Math.max(f, drawCommandArr[i].getBottom());
            }
            fArr[i] = f;
        }
        for (int length = drawCommandArr.length - 1; length >= 0; length--) {
            if (drawCommandArr[length] instanceof DrawView) {
                min = Math.min(f, drawCommandArr[length].mLogicalTop);
            } else {
                min = Math.min(f, drawCommandArr[length].getTop());
            }
            f = min;
            fArr2[length] = f;
        }
    }
}
