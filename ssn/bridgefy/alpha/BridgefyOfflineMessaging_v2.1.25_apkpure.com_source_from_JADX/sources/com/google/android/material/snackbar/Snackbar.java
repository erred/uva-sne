package com.google.android.material.snackbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.C2167R;
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback;
import com.google.common.primitives.Ints;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Snackbar extends BaseTransientBottomBar<Snackbar> {
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    private static final int[] SNACKBAR_BUTTON_STYLE_ATTR = {C2167R.attr.snackbarButtonStyle};
    private final AccessibilityManager accessibilityManager;
    private BaseCallback<Snackbar> callback;
    private boolean hasAction;

    public static class Callback extends BaseCallback<Snackbar> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        public void onDismissed(Snackbar snackbar, int i) {
        }

        public void onShown(Snackbar snackbar) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static final class SnackbarLayout extends SnackbarBaseLayout {
        public /* bridge */ /* synthetic */ void setOnClickListener(OnClickListener onClickListener) {
            super.setOnClickListener(onClickListener);
        }

        public SnackbarLayout(Context context) {
            super(context);
        }

        public SnackbarLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int childCount = getChildCount();
            int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getLayoutParams().width == -1) {
                    childAt.measure(MeasureSpec.makeMeasureSpec(measuredWidth, Ints.MAX_POWER_OF_TWO), MeasureSpec.makeMeasureSpec(childAt.getMeasuredHeight(), Ints.MAX_POWER_OF_TWO));
                }
            }
        }
    }

    private Snackbar(ViewGroup viewGroup, View view, ContentViewCallback contentViewCallback) {
        super(viewGroup, view, contentViewCallback);
        this.accessibilityManager = (AccessibilityManager) viewGroup.getContext().getSystemService("accessibility");
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }

    public boolean isShown() {
        return super.isShown();
    }

    public static Snackbar make(View view, CharSequence charSequence, int i) {
        ViewGroup findSuitableParent = findSuitableParent(view);
        if (findSuitableParent != null) {
            SnackbarContentLayout snackbarContentLayout = (SnackbarContentLayout) LayoutInflater.from(findSuitableParent.getContext()).inflate(hasSnackbarButtonStyleAttr(findSuitableParent.getContext()) ? C2167R.layout.mtrl_layout_snackbar_include : C2167R.layout.design_layout_snackbar_include, findSuitableParent, false);
            Snackbar snackbar = new Snackbar(findSuitableParent, snackbarContentLayout, snackbarContentLayout);
            snackbar.setText(charSequence);
            snackbar.setDuration(i);
            return snackbar;
        }
        throw new IllegalArgumentException("No suitable parent found from the given view. Please provide a valid view.");
    }

    protected static boolean hasSnackbarButtonStyleAttr(Context context) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(SNACKBAR_BUTTON_STYLE_ATTR);
        int resourceId = obtainStyledAttributes.getResourceId(0, -1);
        obtainStyledAttributes.recycle();
        if (resourceId != -1) {
            return true;
        }
        return false;
    }

    public static Snackbar make(View view, int i, int i2) {
        return make(view, view.getResources().getText(i), i2);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup viewGroup = null;
        while (!(view instanceof CoordinatorLayout)) {
            if (view instanceof FrameLayout) {
                if (view.getId() == 16908290) {
                    return (ViewGroup) view;
                }
                viewGroup = (ViewGroup) view;
            }
            if (view != null) {
                ViewParent parent = view.getParent();
                if (parent instanceof View) {
                    view = (View) parent;
                    continue;
                } else {
                    view = null;
                    continue;
                }
            }
            if (view == null) {
                return viewGroup;
            }
        }
        return (ViewGroup) view;
    }

    public Snackbar setText(CharSequence charSequence) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getMessageView().setText(charSequence);
        return this;
    }

    public Snackbar setText(int i) {
        return setText(getContext().getText(i));
    }

    public Snackbar setAction(int i, OnClickListener onClickListener) {
        return setAction(getContext().getText(i), onClickListener);
    }

    public Snackbar setAction(CharSequence charSequence, final OnClickListener onClickListener) {
        Button actionView = ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView();
        if (TextUtils.isEmpty(charSequence) || onClickListener == null) {
            actionView.setVisibility(8);
            actionView.setOnClickListener(null);
            this.hasAction = false;
        } else {
            this.hasAction = true;
            actionView.setVisibility(0);
            actionView.setText(charSequence);
            actionView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onClickListener.onClick(view);
                    Snackbar.this.dispatchDismiss(1);
                }
            });
        }
        return this;
    }

    public int getDuration() {
        if (!this.hasAction || !this.accessibilityManager.isTouchExplorationEnabled()) {
            return super.getDuration();
        }
        return -2;
    }

    public Snackbar setActionTextColor(ColorStateList colorStateList) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView().setTextColor(colorStateList);
        return this;
    }

    public Snackbar setActionTextColor(int i) {
        ((SnackbarContentLayout) this.view.getChildAt(0)).getActionView().setTextColor(i);
        return this;
    }

    @Deprecated
    public Snackbar setCallback(Callback callback2) {
        if (this.callback != null) {
            removeCallback(this.callback);
        }
        if (callback2 != null) {
            addCallback(callback2);
        }
        this.callback = callback2;
        return this;
    }
}
