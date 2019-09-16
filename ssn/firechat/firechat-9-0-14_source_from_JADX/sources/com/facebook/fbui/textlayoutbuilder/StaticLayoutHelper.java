package com.facebook.fbui.textlayoutbuilder;

import android.support.p000v4.text.TextDirectionHeuristicCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import com.facebook.fbui.textlayoutbuilder.proxy.StaticLayoutProxy;
import java.lang.reflect.Field;

class StaticLayoutHelper {
    private static final String SPACE_AND_ELLIPSIS = " …";

    StaticLayoutHelper() {
    }

    private static StaticLayout getStaticLayoutMaybeMaxLines(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        try {
            return StaticLayoutProxy.create(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, truncateAt, i4, i5, textDirectionHeuristicCompat);
        } catch (LinkageError unused) {
            return getStaticLayoutNoMaxLines(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, truncateAt, i4);
        }
    }

    private static StaticLayout getStaticLayoutNoMaxLines(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4) {
        StaticLayout staticLayout = new StaticLayout(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, truncateAt, i4);
        return staticLayout;
    }

    public static StaticLayout make(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        int i6;
        CharSequence charSequence2 = charSequence;
        int i7 = i;
        int i8 = i5;
        StaticLayout staticLayoutMaybeMaxLines = getStaticLayoutMaybeMaxLines(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, truncateAt, i4, i5, textDirectionHeuristicCompat);
        if (i8 > 0) {
            int i9 = i2;
            while (staticLayoutMaybeMaxLines.getLineCount() > i8) {
                int lineStart = staticLayoutMaybeMaxLines.getLineStart(i8);
                if (lineStart >= i9) {
                    break;
                }
                int i10 = lineStart;
                while (i10 > i7 && Character.isSpace(charSequence2.charAt(i10 - 1))) {
                    i10--;
                }
                int i11 = i10;
                staticLayoutMaybeMaxLines = getStaticLayoutMaybeMaxLines(charSequence2, i7, i10, textPaint, i3, alignment, f, f2, z, truncateAt, i4, i8, textDirectionHeuristicCompat);
                if (staticLayoutMaybeMaxLines.getLineCount() < i8 || staticLayoutMaybeMaxLines.getEllipsisCount(i8 - 1) != 0) {
                    i6 = i11;
                    charSequence2 = charSequence;
                } else {
                    StringBuilder sb = new StringBuilder();
                    int i12 = i11;
                    charSequence2 = charSequence;
                    sb.append(charSequence2.subSequence(i7, i12));
                    sb.append(SPACE_AND_ELLIPSIS);
                    String sb2 = sb.toString();
                    i6 = i12;
                    staticLayoutMaybeMaxLines = getStaticLayoutMaybeMaxLines(sb2, 0, sb2.length(), textPaint, i3, alignment, f, f2, z, truncateAt, i4, i8, textDirectionHeuristicCompat);
                }
                i9 = i6;
            }
        }
        do {
        } while (!fixLayout(staticLayoutMaybeMaxLines));
        return staticLayoutMaybeMaxLines;
    }

    public static boolean fixLayout(StaticLayout staticLayout) {
        int lineStart = staticLayout.getLineStart(0);
        int lineCount = staticLayout.getLineCount();
        int i = lineStart;
        int i2 = 0;
        while (i2 < lineCount) {
            int lineEnd = staticLayout.getLineEnd(i2);
            if (lineEnd < i) {
                try {
                    Field declaredField = StaticLayout.class.getDeclaredField("mLines");
                    declaredField.setAccessible(true);
                    Field declaredField2 = StaticLayout.class.getDeclaredField("mColumns");
                    declaredField2.setAccessible(true);
                    int[] iArr = (int[]) declaredField.get(staticLayout);
                    int i3 = declaredField2.getInt(staticLayout);
                    for (int i4 = 0; i4 < i3; i4++) {
                        int i5 = (i3 * i2) + i4;
                        swap(iArr, i5, i5 + i3);
                    }
                    return false;
                } catch (Exception unused) {
                }
            } else {
                i2++;
                i = lineEnd;
            }
        }
        return true;
    }

    private static void swap(int[] iArr, int i, int i2) {
        int i3 = iArr[i];
        iArr[i] = iArr[i2];
        iArr[i2] = i3;
    }
}
