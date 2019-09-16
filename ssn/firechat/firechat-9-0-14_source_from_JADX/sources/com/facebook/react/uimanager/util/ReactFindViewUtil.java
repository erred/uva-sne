package com.facebook.react.uimanager.util;

import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.C0742R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class ReactFindViewUtil {
    private static final List<OnViewFoundListener> mOnViewFoundListeners = new ArrayList();

    public interface OnViewFoundListener {
        String getNativeId();

        void onViewFound(View view);
    }

    @Nullable
    public static View findView(View view, String str) {
        String nativeId = getNativeId(view);
        if (nativeId != null && nativeId.equals(str)) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View findView = findView(viewGroup.getChildAt(i), str);
                if (findView != null) {
                    return findView;
                }
            }
        }
        return null;
    }

    public static void findView(View view, OnViewFoundListener onViewFoundListener) {
        View findView = findView(view, onViewFoundListener.getNativeId());
        if (findView != null) {
            onViewFoundListener.onViewFound(findView);
        }
        addViewListener(onViewFoundListener);
    }

    public static void addViewListener(OnViewFoundListener onViewFoundListener) {
        mOnViewFoundListeners.add(onViewFoundListener);
    }

    public static void removeViewListener(OnViewFoundListener onViewFoundListener) {
        mOnViewFoundListeners.remove(onViewFoundListener);
    }

    public static void notifyViewRendered(View view) {
        String nativeId = getNativeId(view);
        if (nativeId != null) {
            Iterator it = mOnViewFoundListeners.iterator();
            while (it.hasNext()) {
                OnViewFoundListener onViewFoundListener = (OnViewFoundListener) it.next();
                if (nativeId != null && nativeId.equals(onViewFoundListener.getNativeId())) {
                    onViewFoundListener.onViewFound(view);
                    it.remove();
                }
            }
        }
    }

    @Nullable
    private static String getNativeId(View view) {
        Object tag = view.getTag(C0742R.C0744id.view_tag_native_id);
        if (tag instanceof String) {
            return (String) tag;
        }
        return null;
    }
}
