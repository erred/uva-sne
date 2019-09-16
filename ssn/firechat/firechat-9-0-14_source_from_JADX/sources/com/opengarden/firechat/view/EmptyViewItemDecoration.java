package com.opengarden.firechat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.support.p003v7.widget.DividerItemDecoration;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.LayoutParams;
import android.support.p003v7.widget.RecyclerView.State;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.HeaderViewHolder;
import com.opengarden.firechat.adapters.AdapterSection;
import com.opengarden.firechat.util.ThemeUtils;

public class EmptyViewItemDecoration extends DividerItemDecoration {
    private final float mEmptyViewHeight;
    private final float mEmptyViewLeftMargin;
    private final int mOrientation;
    private final int mTextColor;
    private final float mTextSize;

    public EmptyViewItemDecoration(Context context, int i, int i2, int i3, int i4) {
        super(context, i);
        float f = context.getResources().getDisplayMetrics().density;
        this.mOrientation = i;
        this.mTextSize = ((float) i4) * f;
        this.mEmptyViewHeight = ((float) i2) * f;
        this.mEmptyViewLeftMargin = ((float) i3) * f;
        this.mTextColor = ThemeUtils.INSTANCE.getColor(context, C1299R.attr.list_divider_color);
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        if (this.mOrientation != 1 || !isDecorated(view, recyclerView)) {
            rect.set(0, 0, 0, 0);
        } else {
            rect.set(0, 0, 0, (int) this.mEmptyViewHeight);
        }
    }

    public void onDraw(Canvas canvas, RecyclerView recyclerView, State state) {
        RecyclerView recyclerView2 = recyclerView;
        if (recyclerView.getLayoutManager() != null) {
            if (this.mOrientation == 0) {
                super.onDraw(canvas, recyclerView, state);
            } else {
                canvas.save();
                int childCount = recyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = recyclerView2.getChildAt(i);
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    ViewHolder childViewHolder = recyclerView2.getChildViewHolder(childAt);
                    if (!layoutParams.isItemRemoved() && !layoutParams.isViewInvalid() && isDecorated(childAt, recyclerView2)) {
                        String emptyViewPlaceholder = ((HeaderViewHolder) childViewHolder).getSection().getEmptyViewPlaceholder();
                        if (!TextUtils.isEmpty(emptyViewPlaceholder)) {
                            TextPaint textPaint = new TextPaint();
                            textPaint.setAntiAlias(true);
                            textPaint.setTextSize(this.mTextSize);
                            textPaint.setColor(this.mTextColor);
                            textPaint.setTextAlign(Align.LEFT);
                            StaticLayout staticLayout = new StaticLayout(emptyViewPlaceholder, textPaint, (int) textPaint.measureText(emptyViewPlaceholder), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            staticLayout.getOffsetToLeftOf(10);
                            Canvas canvas2 = canvas;
                            canvas2.drawText(emptyViewPlaceholder, this.mEmptyViewLeftMargin, (((float) childViewHolder.itemView.getBottom()) + this.mEmptyViewHeight) - ((this.mEmptyViewHeight - ((float) staticLayout.getHeight())) / 1.5f), textPaint);
                        }
                    }
                    Canvas canvas3 = canvas;
                }
                Canvas canvas4 = canvas;
                canvas.restore();
            }
        }
    }

    private boolean isDecorated(View view, RecyclerView recyclerView) {
        ViewHolder childViewHolder = recyclerView.getChildViewHolder(view);
        boolean z = false;
        if (!(childViewHolder instanceof HeaderViewHolder)) {
            return false;
        }
        AdapterSection section = ((HeaderViewHolder) childViewHolder).getSection();
        if (section != null && !TextUtils.isEmpty(section.getEmptyViewPlaceholder()) && section.getNbItems() == 0) {
            z = true;
        }
        return z;
    }
}
