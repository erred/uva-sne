package com.getbase.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

public class FloatingActionsMenu extends ViewGroup {
    private static final int ANIMATION_DURATION = 300;
    private static final float COLLAPSED_PLUS_ROTATION = 0.0f;
    private static final float EXPANDED_PLUS_ROTATION = 135.0f;
    public static final int EXPAND_DOWN = 1;
    public static final int EXPAND_LEFT = 2;
    public static final int EXPAND_RIGHT = 3;
    public static final int EXPAND_UP = 0;
    public static final int LABELS_ON_LEFT_SIDE = 0;
    public static final int LABELS_ON_RIGHT_SIDE = 1;
    /* access modifiers changed from: private */
    public static Interpolator sAlphaExpandInterpolator = new DecelerateInterpolator();
    /* access modifiers changed from: private */
    public static Interpolator sCollapseInterpolator = new DecelerateInterpolator(3.0f);
    /* access modifiers changed from: private */
    public static Interpolator sExpandInterpolator = new OvershootInterpolator();
    private AddFloatingActionButton mAddButton;
    /* access modifiers changed from: private */
    public int mAddButtonColorNormal;
    /* access modifiers changed from: private */
    public int mAddButtonColorPressed;
    /* access modifiers changed from: private */
    public int mAddButtonPlusColor;
    private int mAddButtonSize;
    /* access modifiers changed from: private */
    public boolean mAddButtonStrokeVisible;
    private int mButtonSpacing;
    private int mButtonsCount;
    /* access modifiers changed from: private */
    public AnimatorSet mCollapseAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet mExpandAnimation;
    /* access modifiers changed from: private */
    public int mExpandDirection;
    private boolean mExpanded;
    private int mLabelsMargin;
    private int mLabelsPosition;
    private int mLabelsStyle;
    private int mLabelsVerticalOffset;
    private OnFloatingActionsMenuUpdateListener mListener;
    private int mMaxButtonHeight;
    private int mMaxButtonWidth;
    /* access modifiers changed from: private */
    public RotatingDrawable mRotatingDrawable;
    private TouchDelegateGroup mTouchDelegateGroup;

    private class LayoutParams extends android.view.ViewGroup.LayoutParams {
        private boolean animationsSetToPlay;
        private ObjectAnimator mCollapseAlpha = new ObjectAnimator();
        /* access modifiers changed from: private */
        public ObjectAnimator mCollapseDir = new ObjectAnimator();
        private ObjectAnimator mExpandAlpha = new ObjectAnimator();
        /* access modifiers changed from: private */
        public ObjectAnimator mExpandDir = new ObjectAnimator();

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mExpandDir.setInterpolator(FloatingActionsMenu.sExpandInterpolator);
            this.mExpandAlpha.setInterpolator(FloatingActionsMenu.sAlphaExpandInterpolator);
            this.mCollapseDir.setInterpolator(FloatingActionsMenu.sCollapseInterpolator);
            this.mCollapseAlpha.setInterpolator(FloatingActionsMenu.sCollapseInterpolator);
            this.mCollapseAlpha.setProperty(View.ALPHA);
            this.mCollapseAlpha.setFloatValues(new float[]{1.0f, 0.0f});
            this.mExpandAlpha.setProperty(View.ALPHA);
            this.mExpandAlpha.setFloatValues(new float[]{0.0f, 1.0f});
            switch (FloatingActionsMenu.this.mExpandDirection) {
                case 0:
                case 1:
                    this.mCollapseDir.setProperty(View.TRANSLATION_Y);
                    this.mExpandDir.setProperty(View.TRANSLATION_Y);
                    return;
                case 2:
                case 3:
                    this.mCollapseDir.setProperty(View.TRANSLATION_X);
                    this.mExpandDir.setProperty(View.TRANSLATION_X);
                    return;
                default:
                    return;
            }
        }

        public void setAnimationsTarget(View view) {
            this.mCollapseAlpha.setTarget(view);
            this.mCollapseDir.setTarget(view);
            this.mExpandAlpha.setTarget(view);
            this.mExpandDir.setTarget(view);
            if (!this.animationsSetToPlay) {
                addLayerTypeListener(this.mExpandDir, view);
                addLayerTypeListener(this.mCollapseDir, view);
                FloatingActionsMenu.this.mCollapseAnimation.play(this.mCollapseAlpha);
                FloatingActionsMenu.this.mCollapseAnimation.play(this.mCollapseDir);
                FloatingActionsMenu.this.mExpandAnimation.play(this.mExpandAlpha);
                FloatingActionsMenu.this.mExpandAnimation.play(this.mExpandDir);
                this.animationsSetToPlay = true;
            }
        }

        private void addLayerTypeListener(Animator animator, final View view) {
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    view.setLayerType(0, null);
                }

                public void onAnimationStart(Animator animator) {
                    view.setLayerType(2, null);
                }
            });
        }
    }

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuCollapsed();

        void onMenuExpanded();
    }

    private static class RotatingDrawable extends LayerDrawable {
        private float mRotation;

        public RotatingDrawable(Drawable drawable) {
            super(new Drawable[]{drawable});
        }

        public float getRotation() {
            return this.mRotation;
        }

        public void setRotation(float f) {
            this.mRotation = f;
            invalidateSelf();
        }

        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(this.mRotation, (float) getBounds().centerX(), (float) getBounds().centerY());
            super.draw(canvas);
            canvas.restore();
        }
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public boolean mExpanded;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            boolean z = true;
            if (parcel.readInt() != 1) {
                z = false;
            }
            this.mExpanded = z;
        }

        public void writeToParcel(@NonNull Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mExpanded ? 1 : 0);
        }
    }

    public FloatingActionsMenu(Context context) {
        this(context, null);
    }

    public FloatingActionsMenu(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mExpandAnimation = new AnimatorSet().setDuration(300);
        this.mCollapseAnimation = new AnimatorSet().setDuration(300);
        init(context, attributeSet);
    }

    public FloatingActionsMenu(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mExpandAnimation = new AnimatorSet().setDuration(300);
        this.mCollapseAnimation = new AnimatorSet().setDuration(300);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mButtonSpacing = (int) ((getResources().getDimension(C1112R.dimen.fab_actions_spacing) - getResources().getDimension(C1112R.dimen.fab_shadow_radius)) - getResources().getDimension(C1112R.dimen.fab_shadow_offset));
        this.mLabelsMargin = getResources().getDimensionPixelSize(C1112R.dimen.fab_labels_margin);
        this.mLabelsVerticalOffset = getResources().getDimensionPixelSize(C1112R.dimen.fab_shadow_offset);
        this.mTouchDelegateGroup = new TouchDelegateGroup(this);
        setTouchDelegate(this.mTouchDelegateGroup);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C1112R.styleable.FloatingActionsMenu, 0, 0);
        this.mAddButtonPlusColor = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionsMenu_fab_addButtonPlusIconColor, getColor(17170443));
        this.mAddButtonColorNormal = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionsMenu_fab_addButtonColorNormal, getColor(17170451));
        this.mAddButtonColorPressed = obtainStyledAttributes.getColor(C1112R.styleable.FloatingActionsMenu_fab_addButtonColorPressed, getColor(17170450));
        this.mAddButtonSize = obtainStyledAttributes.getInt(C1112R.styleable.FloatingActionsMenu_fab_addButtonSize, 0);
        this.mAddButtonStrokeVisible = obtainStyledAttributes.getBoolean(C1112R.styleable.FloatingActionsMenu_fab_addButtonStrokeVisible, true);
        this.mExpandDirection = obtainStyledAttributes.getInt(C1112R.styleable.FloatingActionsMenu_fab_expandDirection, 0);
        this.mLabelsStyle = obtainStyledAttributes.getResourceId(C1112R.styleable.FloatingActionsMenu_fab_labelStyle, 0);
        this.mLabelsPosition = obtainStyledAttributes.getInt(C1112R.styleable.FloatingActionsMenu_fab_labelsPosition, 0);
        obtainStyledAttributes.recycle();
        if (this.mLabelsStyle == 0 || !expandsHorizontally()) {
            createAddButton(context);
            return;
        }
        throw new IllegalStateException("Action labels in horizontal expand orientation is not supported.");
    }

    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener onFloatingActionsMenuUpdateListener) {
        this.mListener = onFloatingActionsMenuUpdateListener;
    }

    private boolean expandsHorizontally() {
        return this.mExpandDirection == 2 || this.mExpandDirection == 3;
    }

    private void createAddButton(Context context) {
        this.mAddButton = new AddFloatingActionButton(context) {
            /* access modifiers changed from: 0000 */
            public void updateBackground() {
                this.mPlusColor = FloatingActionsMenu.this.mAddButtonPlusColor;
                this.mColorNormal = FloatingActionsMenu.this.mAddButtonColorNormal;
                this.mColorPressed = FloatingActionsMenu.this.mAddButtonColorPressed;
                this.mStrokeVisible = FloatingActionsMenu.this.mAddButtonStrokeVisible;
                super.updateBackground();
            }

            /* access modifiers changed from: 0000 */
            public Drawable getIconDrawable() {
                RotatingDrawable rotatingDrawable = new RotatingDrawable(super.getIconDrawable());
                FloatingActionsMenu.this.mRotatingDrawable = rotatingDrawable;
                OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", new float[]{FloatingActionsMenu.EXPANDED_PLUS_ROTATION, 0.0f});
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", new float[]{0.0f, FloatingActionsMenu.EXPANDED_PLUS_ROTATION});
                ofFloat.setInterpolator(overshootInterpolator);
                ofFloat2.setInterpolator(overshootInterpolator);
                FloatingActionsMenu.this.mExpandAnimation.play(ofFloat2);
                FloatingActionsMenu.this.mCollapseAnimation.play(ofFloat);
                return rotatingDrawable;
            }
        };
        this.mAddButton.setId(C1112R.C1114id.fab_expand_menu_button);
        this.mAddButton.setSize(this.mAddButtonSize);
        this.mAddButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                FloatingActionsMenu.this.toggle();
            }
        });
        addView(this.mAddButton, super.generateDefaultLayoutParams());
        this.mButtonsCount++;
    }

    public void addButton(FloatingActionButton floatingActionButton) {
        addView(floatingActionButton, this.mButtonsCount - 1);
        this.mButtonsCount++;
        if (this.mLabelsStyle != 0) {
            createLabels();
        }
    }

    public void removeButton(FloatingActionButton floatingActionButton) {
        removeView(floatingActionButton.getLabelView());
        removeView(floatingActionButton);
        floatingActionButton.setTag(C1112R.C1114id.fab_label, null);
        this.mButtonsCount--;
    }

    private int getColor(@ColorRes int i) {
        return getResources().getColor(i);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        measureChildren(i, i2);
        int i3 = 0;
        this.mMaxButtonWidth = 0;
        this.mMaxButtonHeight = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < this.mButtonsCount; i7++) {
            View childAt = getChildAt(i7);
            if (childAt.getVisibility() != 8) {
                switch (this.mExpandDirection) {
                    case 0:
                    case 1:
                        this.mMaxButtonWidth = Math.max(this.mMaxButtonWidth, childAt.getMeasuredWidth());
                        i5 += childAt.getMeasuredHeight();
                        break;
                    case 2:
                    case 3:
                        i6 += childAt.getMeasuredWidth();
                        this.mMaxButtonHeight = Math.max(this.mMaxButtonHeight, childAt.getMeasuredHeight());
                        break;
                }
                if (!expandsHorizontally()) {
                    TextView textView = (TextView) childAt.getTag(C1112R.C1114id.fab_label);
                    if (textView != null) {
                        i4 = Math.max(i4, textView.getMeasuredWidth());
                    }
                }
            }
        }
        if (!expandsHorizontally()) {
            int i8 = this.mMaxButtonWidth;
            if (i4 > 0) {
                i3 = this.mLabelsMargin + i4;
            }
            i6 = i8 + i3;
        } else {
            i5 = this.mMaxButtonHeight;
        }
        switch (this.mExpandDirection) {
            case 0:
            case 1:
                i5 = adjustForOvershoot(i5 + (this.mButtonSpacing * (this.mButtonsCount - 1)));
                break;
            case 2:
            case 3:
                i6 = adjustForOvershoot(i6 + (this.mButtonSpacing * (this.mButtonsCount - 1)));
                break;
        }
        setMeasuredDimension(i6, i5);
    }

    private int adjustForOvershoot(int i) {
        return (i * 12) / 10;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        int i8;
        int i9 = 8;
        float f = 0.0f;
        char c = 0;
        char c2 = 1;
        switch (this.mExpandDirection) {
            case 0:
            case 1:
                boolean z2 = this.mExpandDirection == 0;
                if (z) {
                    this.mTouchDelegateGroup.clearTouchDelegates();
                }
                int measuredHeight = z2 ? (i4 - i2) - this.mAddButton.getMeasuredHeight() : 0;
                int i10 = this.mLabelsPosition == 0 ? (i3 - i) - (this.mMaxButtonWidth / 2) : this.mMaxButtonWidth / 2;
                int measuredWidth = i10 - (this.mAddButton.getMeasuredWidth() / 2);
                this.mAddButton.layout(measuredWidth, measuredHeight, this.mAddButton.getMeasuredWidth() + measuredWidth, this.mAddButton.getMeasuredHeight() + measuredHeight);
                int i11 = (this.mMaxButtonWidth / 2) + this.mLabelsMargin;
                int i12 = this.mLabelsPosition == 0 ? i10 - i11 : i11 + i10;
                if (z2) {
                    i5 = measuredHeight - this.mButtonSpacing;
                } else {
                    i5 = this.mAddButton.getMeasuredHeight() + measuredHeight + this.mButtonSpacing;
                }
                int i13 = this.mButtonsCount - 1;
                while (i13 >= 0) {
                    View childAt = getChildAt(i13);
                    if (childAt == this.mAddButton || childAt.getVisibility() == i9) {
                        i6 = i10;
                    } else {
                        int measuredWidth2 = i10 - (childAt.getMeasuredWidth() / 2);
                        if (z2) {
                            i5 -= childAt.getMeasuredHeight();
                        }
                        childAt.layout(measuredWidth2, i5, childAt.getMeasuredWidth() + measuredWidth2, childAt.getMeasuredHeight() + i5);
                        float f2 = (float) (measuredHeight - i5);
                        childAt.setTranslationY(this.mExpanded ? 0.0f : f2);
                        childAt.setAlpha(this.mExpanded ? 1.0f : 0.0f);
                        LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                        ObjectAnimator access$700 = layoutParams.mCollapseDir;
                        i6 = i10;
                        float[] fArr = new float[2];
                        fArr[c] = f;
                        fArr[c2] = f2;
                        access$700.setFloatValues(fArr);
                        ObjectAnimator access$800 = layoutParams.mExpandDir;
                        float[] fArr2 = new float[2];
                        fArr2[c] = f2;
                        fArr2[c2] = f;
                        access$800.setFloatValues(fArr2);
                        layoutParams.setAnimationsTarget(childAt);
                        View view = (View) childAt.getTag(C1112R.C1114id.fab_label);
                        if (view != null) {
                            if (this.mLabelsPosition == 0) {
                                i7 = i12 - view.getMeasuredWidth();
                            } else {
                                i7 = view.getMeasuredWidth() + i12;
                            }
                            int i14 = this.mLabelsPosition == 0 ? i7 : i12;
                            if (this.mLabelsPosition == 0) {
                                i7 = i12;
                            }
                            int measuredHeight2 = (i5 - this.mLabelsVerticalOffset) + ((childAt.getMeasuredHeight() - view.getMeasuredHeight()) / 2);
                            view.layout(i14, measuredHeight2, i7, measuredHeight2 + view.getMeasuredHeight());
                            this.mTouchDelegateGroup.addTouchDelegate(new TouchDelegate(new Rect(Math.min(measuredWidth2, i14), i5 - (this.mButtonSpacing / 2), Math.max(measuredWidth2 + childAt.getMeasuredWidth(), i7), childAt.getMeasuredHeight() + i5 + (this.mButtonSpacing / 2)), childAt));
                            view.setTranslationY(this.mExpanded ? 0.0f : f2);
                            view.setAlpha(this.mExpanded ? 1.0f : 0.0f);
                            LayoutParams layoutParams2 = (LayoutParams) view.getLayoutParams();
                            layoutParams2.mCollapseDir.setFloatValues(new float[]{0.0f, f2});
                            layoutParams2.mExpandDir.setFloatValues(new float[]{f2, 0.0f});
                            layoutParams2.setAnimationsTarget(view);
                        }
                        if (z2) {
                            i5 -= this.mButtonSpacing;
                        } else {
                            i5 = i5 + childAt.getMeasuredHeight() + this.mButtonSpacing;
                        }
                    }
                    i13--;
                    i10 = i6;
                    i9 = 8;
                    f = 0.0f;
                    c = 0;
                    c2 = 1;
                }
                return;
            case 2:
            case 3:
                boolean z3 = this.mExpandDirection == 2;
                int measuredWidth3 = z3 ? (i3 - i) - this.mAddButton.getMeasuredWidth() : 0;
                int measuredHeight3 = ((i4 - i2) - this.mMaxButtonHeight) + ((this.mMaxButtonHeight - this.mAddButton.getMeasuredHeight()) / 2);
                this.mAddButton.layout(measuredWidth3, measuredHeight3, this.mAddButton.getMeasuredWidth() + measuredWidth3, this.mAddButton.getMeasuredHeight() + measuredHeight3);
                if (z3) {
                    i8 = measuredWidth3 - this.mButtonSpacing;
                } else {
                    i8 = this.mAddButton.getMeasuredWidth() + measuredWidth3 + this.mButtonSpacing;
                }
                for (int i15 = this.mButtonsCount - 1; i15 >= 0; i15--) {
                    View childAt2 = getChildAt(i15);
                    if (!(childAt2 == this.mAddButton || childAt2.getVisibility() == 8)) {
                        if (z3) {
                            i8 -= childAt2.getMeasuredWidth();
                        }
                        int measuredHeight4 = ((this.mAddButton.getMeasuredHeight() - childAt2.getMeasuredHeight()) / 2) + measuredHeight3;
                        childAt2.layout(i8, measuredHeight4, childAt2.getMeasuredWidth() + i8, childAt2.getMeasuredHeight() + measuredHeight4);
                        float f3 = (float) (measuredWidth3 - i8);
                        childAt2.setTranslationX(this.mExpanded ? 0.0f : f3);
                        childAt2.setAlpha(this.mExpanded ? 1.0f : 0.0f);
                        LayoutParams layoutParams3 = (LayoutParams) childAt2.getLayoutParams();
                        layoutParams3.mCollapseDir.setFloatValues(new float[]{0.0f, f3});
                        layoutParams3.mExpandDir.setFloatValues(new float[]{f3, 0.0f});
                        layoutParams3.setAnimationsTarget(childAt2);
                        if (z3) {
                            i8 -= this.mButtonSpacing;
                        } else {
                            i8 = i8 + childAt2.getMeasuredWidth() + this.mButtonSpacing;
                        }
                    }
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(super.generateLayoutParams(attributeSet));
    }

    /* access modifiers changed from: protected */
    public android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(super.generateLayoutParams(layoutParams));
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return super.checkLayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        bringChildToFront(this.mAddButton);
        this.mButtonsCount = getChildCount();
        if (this.mLabelsStyle != 0) {
            createLabels();
        }
    }

    private void createLabels() {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), this.mLabelsStyle);
        for (int i = 0; i < this.mButtonsCount; i++) {
            FloatingActionButton floatingActionButton = (FloatingActionButton) getChildAt(i);
            String title = floatingActionButton.getTitle();
            if (!(floatingActionButton == this.mAddButton || title == null || floatingActionButton.getTag(C1112R.C1114id.fab_label) != null)) {
                TextView textView = new TextView(contextThemeWrapper);
                textView.setTextAppearance(getContext(), this.mLabelsStyle);
                textView.setText(floatingActionButton.getTitle());
                addView(textView);
                floatingActionButton.setTag(C1112R.C1114id.fab_label, textView);
            }
        }
    }

    public void collapse() {
        collapse(false);
    }

    public void collapseImmediately() {
        collapse(true);
    }

    private void collapse(boolean z) {
        if (this.mExpanded) {
            this.mExpanded = false;
            this.mTouchDelegateGroup.setEnabled(false);
            this.mCollapseAnimation.setDuration(z ? 0 : 300);
            this.mCollapseAnimation.start();
            this.mExpandAnimation.cancel();
            if (this.mListener != null) {
                this.mListener.onMenuCollapsed();
            }
        }
    }

    public void toggle() {
        if (this.mExpanded) {
            collapse();
        } else {
            expand();
        }
    }

    public void expand() {
        if (!this.mExpanded) {
            this.mExpanded = true;
            this.mTouchDelegateGroup.setEnabled(true);
            this.mCollapseAnimation.cancel();
            this.mExpandAnimation.start();
            if (this.mListener != null) {
                this.mListener.onMenuExpanded();
            }
        }
    }

    public boolean isExpanded() {
        return this.mExpanded;
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mAddButton.setEnabled(z);
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mExpanded = this.mExpanded;
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            this.mExpanded = savedState.mExpanded;
            this.mTouchDelegateGroup.setEnabled(this.mExpanded);
            if (this.mRotatingDrawable != null) {
                this.mRotatingDrawable.setRotation(this.mExpanded ? EXPANDED_PLUS_ROTATION : 0.0f);
            }
            super.onRestoreInstanceState(savedState.getSuperState());
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }
}
