package com.opengarden.firechat.p008ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.p000v4.content.ContextCompat;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import com.opengarden.firechat.C1299R;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\r\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u00012\u00020\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005J`\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u0007H\u0016Jh\u0010\u001c\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\u00072\u0006\u0010\u001f\u001a\u00020 H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0004¢\u0006\u0002\n\u0000¨\u0006$"}, mo21251d2 = {"Lcom/opengarden/firechat/ui/VectorQuoteSpan;", "Landroid/text/style/LeadingMarginSpan;", "Landroid/text/style/LineBackgroundSpan;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "backgroundColor", "", "gap", "", "stripeColor", "stripeWidth", "drawBackground", "", "c", "Landroid/graphics/Canvas;", "p", "Landroid/graphics/Paint;", "left", "right", "top", "baseline", "bottom", "text", "", "start", "end", "lnum", "drawLeadingMargin", "x", "dir", "first", "", "layout", "Landroid/text/Layout;", "getLeadingMargin", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.ui.VectorQuoteSpan */
/* compiled from: VectorQuoteSpan.kt */
public final class VectorQuoteSpan implements LeadingMarginSpan, LineBackgroundSpan {
    private final int backgroundColor;
    private final float gap;
    private final int stripeColor;
    private final float stripeWidth;

    public VectorQuoteSpan(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.backgroundColor = ContextCompat.getColor(context, C1299R.color.quote_background_color);
        this.stripeColor = ContextCompat.getColor(context, C1299R.color.quote_strip_color);
        this.stripeWidth = context.getResources().getDimension(C1299R.dimen.quote_width);
        this.gap = context.getResources().getDimension(C1299R.dimen.quote_gap);
    }

    public int getLeadingMargin(boolean z) {
        return (int) (this.stripeWidth + this.gap);
    }

    public void drawLeadingMargin(@NotNull Canvas canvas, @NotNull Paint paint, int i, int i2, int i3, int i4, int i5, @NotNull CharSequence charSequence, int i6, int i7, boolean z, @NotNull Layout layout) {
        Paint paint2 = paint;
        Canvas canvas2 = canvas;
        Intrinsics.checkParameterIsNotNull(canvas2, "c");
        Intrinsics.checkParameterIsNotNull(paint2, "p");
        Intrinsics.checkParameterIsNotNull(charSequence, "text");
        Intrinsics.checkParameterIsNotNull(layout, "layout");
        Style style = paint2.getStyle();
        int color = paint2.getColor();
        paint2.setStyle(Style.FILL);
        paint2.setColor(this.stripeColor);
        float f = (float) i;
        canvas2.drawRect(f, (float) i3, f + (((float) i2) * this.stripeWidth), (float) i5, paint2);
        paint2.setStyle(style);
        paint2.setColor(color);
    }

    public void drawBackground(@NotNull Canvas canvas, @NotNull Paint paint, int i, int i2, int i3, int i4, int i5, @NotNull CharSequence charSequence, int i6, int i7, int i8) {
        Paint paint2 = paint;
        Canvas canvas2 = canvas;
        Intrinsics.checkParameterIsNotNull(canvas2, "c");
        Intrinsics.checkParameterIsNotNull(paint2, "p");
        Intrinsics.checkParameterIsNotNull(charSequence, "text");
        int color = paint2.getColor();
        paint2.setColor(this.backgroundColor);
        canvas2.drawRect((float) i, (float) i3, (float) i2, (float) i5, paint2);
        paint2.setColor(color);
    }
}
