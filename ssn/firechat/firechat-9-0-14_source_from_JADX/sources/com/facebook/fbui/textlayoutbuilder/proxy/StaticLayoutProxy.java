package com.facebook.fbui.textlayoutbuilder.proxy;

import android.support.p000v4.text.TextDirectionHeuristicCompat;
import android.support.p000v4.text.TextDirectionHeuristicsCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;

public class StaticLayoutProxy {
    public static StaticLayout create(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5, TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        StaticLayout staticLayout = new StaticLayout(charSequence, i, i2, textPaint, i3, alignment, fromTextDirectionHeuristicCompat(textDirectionHeuristicCompat), f, f2, z, truncateAt, i4, i5);
        return staticLayout;
    }

    private static TextDirectionHeuristic fromTextDirectionHeuristicCompat(TextDirectionHeuristicCompat textDirectionHeuristicCompat) {
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.LTR) {
            return TextDirectionHeuristics.LTR;
        }
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.RTL) {
            return TextDirectionHeuristics.RTL;
        }
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR) {
            return TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL) {
            return TextDirectionHeuristics.FIRSTSTRONG_RTL;
        }
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.ANYRTL_LTR) {
            return TextDirectionHeuristics.ANYRTL_LTR;
        }
        if (textDirectionHeuristicCompat == TextDirectionHeuristicsCompat.LOCALE) {
            return TextDirectionHeuristics.LOCALE;
        }
        return TextDirectionHeuristics.FIRSTSTRONG_LTR;
    }
}
