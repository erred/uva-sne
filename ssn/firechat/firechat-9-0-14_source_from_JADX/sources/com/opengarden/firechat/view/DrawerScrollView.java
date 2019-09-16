package com.opengarden.firechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DrawerScrollView extends ScrollView {
    private static final String FLAG_FOOTER = "-footer";
    private static final String FLAG_HEADER = "-header";
    private static final String LOG_TAG = "DrawerScrollView";
    private static final String STICKY_TAG = "sticky";
    private boolean clipToPaddingHasBeenSet;
    private boolean clippingToPadding;
    private View mCurrentFooter;
    private View mCurrentHeader;
    private List<View> mFooters;
    private List<View> mHeaders;
    private int stickyViewLeftOffset;
    private float stickyViewTopOffset;

    public DrawerScrollView(Context context) {
        this(context, null);
        setup();
    }

    public DrawerScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842880);
        setup();
    }

    public DrawerScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setup();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onSizeChanged y ");
        sb.append(i2);
        sb.append(StringUtils.SPACE);
        sb.append(i4);
        Log.m209d(str, sb.toString());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onLayout changed ");
        sb.append(z);
        sb.append(" t:");
        sb.append(i2);
        sb.append(" b:");
        sb.append(i4);
        Log.m209d(str, sb.toString());
        if (!this.clipToPaddingHasBeenSet) {
            this.clippingToPadding = true;
        }
        if (z) {
            notifyHierarchyChanged();
        }
    }

    public void setClipToPadding(boolean z) {
        super.setClipToPadding(z);
        this.clippingToPadding = z;
        this.clipToPaddingHasBeenSet = true;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mCurrentHeader != null) {
            canvas.save();
            canvas.translate((float) (getPaddingLeft() + this.stickyViewLeftOffset), ((float) getScrollY()) + this.stickyViewTopOffset + ((float) (this.clippingToPadding ? getPaddingTop() : 0)));
            canvas.clipRect(0.0f, this.clippingToPadding ? -this.stickyViewTopOffset : 0.0f, (float) (getWidth() - this.stickyViewLeftOffset), (float) (this.mCurrentHeader.getHeight() + 1));
            canvas.clipRect(0.0f, this.clippingToPadding ? -this.stickyViewTopOffset : 0.0f, (float) getWidth(), (float) this.mCurrentHeader.getHeight());
            this.mCurrentHeader.draw(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onScrollChanged new y:");
        sb.append(i2);
        sb.append(" old y:");
        sb.append(i4);
        Log.m209d(str, sb.toString());
        if (this.mCurrentFooter != null) {
            this.mCurrentFooter.setTranslationY(0.0f);
        }
        doTheStickyThing();
    }

    private void setup() {
        this.mHeaders = new ArrayList();
        this.mFooters = new ArrayList();
    }

    private void doTheStickyThing() {
        int i;
        float f;
        Iterator it = this.mHeaders.iterator();
        View view = null;
        View view2 = null;
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            View view3 = (View) it.next();
            int topForViewRelativeOnlyChild = (getTopForViewRelativeOnlyChild(view3) - getScrollY()) + (this.clippingToPadding ? 0 : getPaddingTop());
            if (topForViewRelativeOnlyChild <= 0) {
                if (view != null) {
                    int topForViewRelativeOnlyChild2 = getTopForViewRelativeOnlyChild(view) - getScrollY();
                    if (!this.clippingToPadding) {
                        i = getPaddingTop();
                    }
                    if (topForViewRelativeOnlyChild <= topForViewRelativeOnlyChild2 + i) {
                    }
                }
                view = view3;
            } else {
                if (view2 != null) {
                    int topForViewRelativeOnlyChild3 = getTopForViewRelativeOnlyChild(view2) - getScrollY();
                    if (!this.clippingToPadding) {
                        i = getPaddingTop();
                    }
                    if (topForViewRelativeOnlyChild >= topForViewRelativeOnlyChild3 + i) {
                    }
                }
                view2 = view3;
            }
        }
        stopStickingCurrentlyStickingViews();
        if (view != null) {
            if (view2 == null) {
                f = 0.0f;
            } else {
                f = (float) Math.min(0, ((getTopForViewRelativeOnlyChild(view2) - getScrollY()) + (this.clippingToPadding ? 0 : getPaddingTop())) - view.getHeight());
            }
            this.stickyViewTopOffset = f;
            if (view != this.mCurrentHeader) {
                this.stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(view);
                this.mCurrentHeader = view;
            }
        } else if (this.mCurrentHeader != null) {
            this.mCurrentHeader = null;
        }
        if (this.mCurrentHeader != null) {
            i = this.mHeaders.indexOf(this.mCurrentHeader);
        }
        View view4 = (this.mFooters == null || i >= this.mFooters.size()) ? null : (View) this.mFooters.get(i);
        if (view4 != null) {
            if (view4 != this.mCurrentFooter) {
                this.mCurrentFooter = view4;
                this.mCurrentFooter.setTranslationY((float) (((-this.mCurrentFooter.getTop()) + getHeight()) - this.mCurrentFooter.getHeight()));
            }
            this.mCurrentFooter = view4;
            int height = (((-this.mCurrentFooter.getTop()) + getHeight()) - this.mCurrentFooter.getHeight()) + getScrollY();
            if (height >= 0) {
                this.mCurrentFooter.setTranslationY(0.0f);
                this.mCurrentFooter = null;
                return;
            }
            this.mCurrentFooter.setTranslationY((float) height);
        } else if (this.mCurrentFooter != null) {
            this.mCurrentFooter.setTranslationY(0.0f);
            this.mCurrentFooter = null;
        }
    }

    private void stopStickingCurrentlyStickingViews() {
        if (this.mCurrentHeader != null) {
            this.mCurrentHeader = null;
        }
        if (this.mCurrentFooter != null) {
            this.mCurrentFooter.setTranslationY(0.0f);
            this.mCurrentFooter = null;
        }
    }

    private void notifyHierarchyChanged() {
        Log.m211e(LOG_TAG, "notifyHierarchyChanged");
        stopStickingCurrentlyStickingViews();
        this.mHeaders.clear();
        this.mFooters.clear();
        findStickyViews(this);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("headers ");
        sb.append(this.mHeaders.size());
        Log.m209d(str, sb.toString());
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("footers ");
        sb2.append(this.mFooters.size());
        Log.m209d(str2, sb2.toString());
        doTheStickyThing();
        invalidate();
    }

    private void findStickyViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                String stringTagForView = getStringTagForView(viewGroup.getChildAt(i));
                if (stringTagForView != null && stringTagForView.contains(STICKY_TAG)) {
                    if (stringTagForView.contains(FLAG_HEADER)) {
                        this.mHeaders.add(viewGroup.getChildAt(i));
                    }
                    if (stringTagForView.contains(FLAG_FOOTER)) {
                        this.mFooters.add(viewGroup.getChildAt(i));
                    }
                    viewGroup.getChildAt(i).setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            DrawerScrollView.this.smoothScrollTo(0, view.getTop());
                        }
                    });
                }
                if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                    findStickyViews(viewGroup.getChildAt(i));
                }
            }
            return;
        }
        String str = (String) view.getTag();
        if (str != null && str.contains(STICKY_TAG)) {
            if (str.contains(FLAG_HEADER)) {
                this.mHeaders.add(view);
            }
            if (str.contains(FLAG_FOOTER)) {
                this.mFooters.add(view);
            }
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    DrawerScrollView.this.smoothScrollTo(0, view.getTop());
                }
            });
        }
    }

    private String getStringTagForView(View view) {
        return String.valueOf(view.getTag());
    }

    private int getLeftForViewRelativeOnlyChild(View view) {
        int left = view.getLeft();
        while (view.getParent() != getChildAt(0)) {
            view = (View) view.getParent();
            left += view.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View view) {
        int top = view.getTop();
        while (view.getParent() != getChildAt(0)) {
            view = (View) view.getParent();
            top += view.getTop();
        }
        return top;
    }
}
