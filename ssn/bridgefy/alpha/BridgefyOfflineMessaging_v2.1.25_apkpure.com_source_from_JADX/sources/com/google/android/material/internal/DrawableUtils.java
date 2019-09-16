package com.google.android.material.internal;

import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.util.Log;
import java.lang.reflect.Method;

public class DrawableUtils {
    private static final String LOG_TAG = "DrawableUtils";
    private static Method setConstantStateMethod;
    private static boolean setConstantStateMethodFetched;

    private DrawableUtils() {
    }

    public static boolean setContainerConstantState(DrawableContainer drawableContainer, ConstantState constantState) {
        return setContainerConstantStateV9(drawableContainer, constantState);
    }

    private static boolean setContainerConstantStateV9(DrawableContainer drawableContainer, ConstantState constantState) {
        if (!setConstantStateMethodFetched) {
            try {
                setConstantStateMethod = DrawableContainer.class.getDeclaredMethod("setConstantState", new Class[]{DrawableContainerState.class});
                setConstantStateMethod.setAccessible(true);
            } catch (NoSuchMethodException unused) {
                Log.e(LOG_TAG, "Could not fetch setConstantState(). Oh well.");
            }
            setConstantStateMethodFetched = true;
        }
        if (setConstantStateMethod != null) {
            try {
                setConstantStateMethod.invoke(drawableContainer, new Object[]{constantState});
                return true;
            } catch (Exception unused2) {
                Log.e(LOG_TAG, "Could not invoke setConstantState(). Oh well.");
            }
        }
        return false;
    }
}
