package com.facebook.react.animated;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import javax.annotation.Nullable;

class InterpolationAnimatedNode extends ValueAnimatedNode {
    public static final String EXTRAPOLATE_TYPE_CLAMP = "clamp";
    public static final String EXTRAPOLATE_TYPE_EXTEND = "extend";
    public static final String EXTRAPOLATE_TYPE_IDENTITY = "identity";
    private final String mExtrapolateLeft;
    private final String mExtrapolateRight;
    private final double[] mInputRange;
    private final double[] mOutputRange;
    @Nullable
    private ValueAnimatedNode mParent;

    private static double[] fromDoubleArray(ReadableArray readableArray) {
        double[] dArr = new double[readableArray.size()];
        for (int i = 0; i < dArr.length; i++) {
            dArr[i] = readableArray.getDouble(i);
        }
        return dArr;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0080, code lost:
        if (r4.equals(EXTRAPOLATE_TYPE_IDENTITY) != false) goto L_0x008e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005f A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b0 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static double interpolate(double r12, double r14, double r16, double r18, double r20, java.lang.String r22, java.lang.String r23) {
        /*
            r3 = r22
            r4 = r23
            int r5 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            r7 = 0
            r8 = 1
            r9 = 94742715(0x5a5a8bb, float:1.5578507E-35)
            r10 = -135761730(0xfffffffff7e870be, float:-9.428903E33)
            r6 = -1289044198(0xffffffffb32abf1a, float:-3.9755015E-8)
            r11 = -1
            if (r5 >= 0) goto L_0x0060
            int r5 = r22.hashCode()
            if (r5 == r6) goto L_0x0033
            if (r5 == r10) goto L_0x0029
            if (r5 == r9) goto L_0x001f
            goto L_0x003d
        L_0x001f:
            java.lang.String r5 = "clamp"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x003d
            r5 = 1
            goto L_0x003e
        L_0x0029:
            java.lang.String r5 = "identity"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x003d
            r5 = 0
            goto L_0x003e
        L_0x0033:
            java.lang.String r5 = "extend"
            boolean r5 = r3.equals(r5)
            if (r5 == 0) goto L_0x003d
            r5 = 2
            goto L_0x003e
        L_0x003d:
            r5 = -1
        L_0x003e:
            switch(r5) {
                case 0: goto L_0x005f;
                case 1: goto L_0x005d;
                case 2: goto L_0x0060;
                default: goto L_0x0041;
            }
        L_0x0041:
            com.facebook.react.bridge.JSApplicationIllegalArgumentException r0 = new com.facebook.react.bridge.JSApplicationIllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid extrapolation type "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r2 = "for left extrapolation"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x005d:
            r0 = r14
            goto L_0x0061
        L_0x005f:
            return r12
        L_0x0060:
            r0 = r12
        L_0x0061:
            int r3 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1))
            if (r3 <= 0) goto L_0x00b1
            int r3 = r23.hashCode()
            if (r3 == r6) goto L_0x0083
            if (r3 == r10) goto L_0x007a
            if (r3 == r9) goto L_0x0070
            goto L_0x008d
        L_0x0070:
            java.lang.String r3 = "clamp"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x008d
            r7 = 1
            goto L_0x008e
        L_0x007a:
            java.lang.String r3 = "identity"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x008d
            goto L_0x008e
        L_0x0083:
            java.lang.String r3 = "extend"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x008d
            r7 = 2
            goto L_0x008e
        L_0x008d:
            r7 = -1
        L_0x008e:
            switch(r7) {
                case 0: goto L_0x00b0;
                case 1: goto L_0x00ad;
                case 2: goto L_0x00b1;
                default: goto L_0x0091;
            }
        L_0x0091:
            com.facebook.react.bridge.JSApplicationIllegalArgumentException r0 = new com.facebook.react.bridge.JSApplicationIllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid extrapolation type "
            r1.append(r2)
            r1.append(r4)
            java.lang.String r2 = "for right extrapolation"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x00ad:
            r0 = r16
            goto L_0x00b1
        L_0x00b0:
            return r0
        L_0x00b1:
            r3 = 0
            double r3 = r20 - r18
            double r0 = r0 - r14
            double r3 = r3 * r0
            double r0 = r16 - r14
            double r3 = r3 / r0
            double r0 = r18 + r3
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.animated.InterpolationAnimatedNode.interpolate(double, double, double, double, double, java.lang.String, java.lang.String):double");
    }

    static double interpolate(double d, double[] dArr, double[] dArr2, String str, String str2) {
        int findRangeIndex = findRangeIndex(d, dArr);
        int i = findRangeIndex + 1;
        return interpolate(d, dArr[findRangeIndex], dArr[i], dArr2[findRangeIndex], dArr2[i], str, str2);
    }

    private static int findRangeIndex(double d, double[] dArr) {
        int i = 1;
        while (i < dArr.length - 1 && dArr[i] < d) {
            i++;
        }
        return i - 1;
    }

    public InterpolationAnimatedNode(ReadableMap readableMap) {
        this.mInputRange = fromDoubleArray(readableMap.getArray("inputRange"));
        this.mOutputRange = fromDoubleArray(readableMap.getArray("outputRange"));
        this.mExtrapolateLeft = readableMap.getString("extrapolateLeft");
        this.mExtrapolateRight = readableMap.getString("extrapolateRight");
    }

    public void onAttachedToNode(AnimatedNode animatedNode) {
        if (this.mParent != null) {
            throw new IllegalStateException("Parent already attached");
        } else if (!(animatedNode instanceof ValueAnimatedNode)) {
            throw new IllegalArgumentException("Parent is of an invalid type");
        } else {
            this.mParent = (ValueAnimatedNode) animatedNode;
        }
    }

    public void onDetachedFromNode(AnimatedNode animatedNode) {
        if (animatedNode != this.mParent) {
            throw new IllegalArgumentException("Invalid parent node provided");
        }
        this.mParent = null;
    }

    public void update() {
        if (this.mParent == null) {
            throw new IllegalStateException("Trying to update interpolation node that has not been attached to the parent");
        }
        this.mValue = interpolate(this.mParent.getValue(), this.mInputRange, this.mOutputRange, this.mExtrapolateLeft, this.mExtrapolateRight);
    }
}
