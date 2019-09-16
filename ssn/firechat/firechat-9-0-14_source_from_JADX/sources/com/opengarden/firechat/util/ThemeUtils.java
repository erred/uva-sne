package com.opengarden.firechat.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.design.widget.TabLayout;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.VectorGroupDetailsActivity;
import java.util.HashMap;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u000b\u001a\u00020\u00042\u0006\u0010\f\u001a\u00020\rJ\u001a\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\r2\b\b\u0001\u0010\u0010\u001a\u00020\nH\u0007J\u0016\u0010\u0011\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0012\u001a\u00020\nJ\"\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\u0018J\u0016\u0010\u0019\u001a\u00020\u00142\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u001a\u001a\u00020\u0004J\u0016\u0010\u001b\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u001dJ \u0010\u001e\u001a\u00020\u001f2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010 \u001a\u00020\u001f2\b\b\u0001\u0010!\u001a\u00020\nJ\u0018\u0010\"\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001f2\b\b\u0001\u0010#\u001a\u00020\nJ\u0016\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020&2\u0006\u0010#\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\tX\u0004¢\u0006\u0002\n\u0000¨\u0006'"}, mo21251d2 = {"Lcom/opengarden/firechat/util/ThemeUtils;", "", "()V", "APPLICATION_THEME_KEY", "", "THEME_BLACK_VALUE", "THEME_DARK_VALUE", "THEME_LIGHT_VALUE", "mColorByAttr", "Ljava/util/HashMap;", "", "getApplicationTheme", "context", "Landroid/content/Context;", "getColor", "c", "colorAttribute", "getResourceId", "resourceId", "setActivityTheme", "", "activity", "Landroid/app/Activity;", "otherThemes", "Lkotlin/Pair;", "setApplicationTheme", "aTheme", "setTabLayoutTheme", "layout", "Landroid/support/design/widget/TabLayout;", "tintDrawable", "Landroid/graphics/drawable/Drawable;", "drawable", "attribute", "tintDrawableWithColor", "color", "tintMenuIcons", "menu", "Landroid/view/Menu;", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: ThemeUtils.kt */
public final class ThemeUtils {
    @NotNull
    public static final String APPLICATION_THEME_KEY = "APPLICATION_THEME_KEY";
    public static final ThemeUtils INSTANCE = new ThemeUtils();
    private static final String THEME_BLACK_VALUE = "black";
    private static final String THEME_DARK_VALUE = "dark";
    private static final String THEME_LIGHT_VALUE = "light";
    private static final HashMap<Integer, Integer> mColorByAttr = new HashMap<>();

    private ThemeUtils() {
    }

    @NotNull
    public final String getApplicationTheme(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        String string = PreferenceManager.getDefaultSharedPreferences(context).getString(APPLICATION_THEME_KEY, THEME_LIGHT_VALUE);
        Intrinsics.checkExpressionValueIsNotNull(string, "PreferenceManager.getDef…E_KEY, THEME_LIGHT_VALUE)");
        return string;
    }

    public final void setApplicationTheme(@NotNull Context context, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "aTheme");
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(APPLICATION_THEME_KEY, str).apply();
        int hashCode = str.hashCode();
        if (hashCode != 3075958) {
            if (hashCode == 93818879 && str.equals(THEME_BLACK_VALUE)) {
                VectorApp.getInstance().setTheme(C1299R.style.AppTheme_Black);
            }
        } else if (str.equals(THEME_DARK_VALUE)) {
            VectorApp.getInstance().setTheme(C1299R.style.AppTheme_Dark);
        }
        mColorByAttr.clear();
    }

    public final void setActivityTheme(@NotNull Activity activity, @NotNull Pair<Integer, Integer> pair) {
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        Intrinsics.checkParameterIsNotNull(pair, "otherThemes");
        String applicationTheme = getApplicationTheme(activity);
        int hashCode = applicationTheme.hashCode();
        if (hashCode != 3075958) {
            if (hashCode == 93818879 && applicationTheme.equals(THEME_BLACK_VALUE)) {
                activity.setTheme(((Number) pair.getSecond()).intValue());
            }
        } else if (applicationTheme.equals(THEME_DARK_VALUE)) {
            activity.setTheme(((Number) pair.getFirst()).intValue());
        }
        mColorByAttr.clear();
    }

    public final void setTabLayoutTheme(@NotNull Activity activity, @NotNull TabLayout tabLayout) {
        int i;
        int i2;
        Intrinsics.checkParameterIsNotNull(activity, "activity");
        Intrinsics.checkParameterIsNotNull(tabLayout, "layout");
        if (activity instanceof VectorGroupDetailsActivity) {
            Context context = activity;
            if (TextUtils.equals(getApplicationTheme(context), THEME_LIGHT_VALUE)) {
                i2 = ContextCompat.getColor(context, 17170443);
                i = ContextCompat.getColor(context, C1299R.color.tab_groups);
            } else {
                i2 = ContextCompat.getColor(context, C1299R.color.tab_groups);
                i = getColor(context, C1299R.attr.primary_color);
            }
            tabLayout.setTabTextColors(i2, i2);
            tabLayout.setSelectedTabIndicatorColor(i2);
            tabLayout.setBackgroundColor(i);
        }
    }

    @ColorInt
    public final int getColor(@NotNull Context context, @AttrRes int i) {
        int i2;
        Intrinsics.checkParameterIsNotNull(context, "c");
        if (mColorByAttr.containsKey(Integer.valueOf(i))) {
            Object obj = mColorByAttr.get(Integer.valueOf(i));
            if (obj != null) {
                return ((Integer) obj).intValue();
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
        }
        try {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(i, typedValue, true);
            i2 = typedValue.data;
        } catch (Exception unused) {
            i2 = ContextCompat.getColor(context, 17170455);
        }
        mColorByAttr.put(Integer.valueOf(i), Integer.valueOf(i2));
        return i2;
    }

    public final int getResourceId(@NotNull Context context, int i) {
        Intrinsics.checkParameterIsNotNull(context, "c");
        if (!TextUtils.equals(getApplicationTheme(context), THEME_LIGHT_VALUE)) {
            return i;
        }
        if (i == C1299R.C1300drawable.line_divider_dark) {
            i = C1299R.C1300drawable.line_divider_light;
        } else if (i == C1299R.style.Floating_Actions_Menu) {
            i = C1299R.style.Floating_Actions_Menu_Light;
        }
        return i;
    }

    public final void tintMenuIcons(@NotNull Menu menu, int i) {
        Intrinsics.checkParameterIsNotNull(menu, "menu");
        int size = menu.size();
        for (int i2 = 0; i2 < size; i2++) {
            MenuItem item = menu.getItem(i2);
            Intrinsics.checkExpressionValueIsNotNull(item, "item");
            Drawable icon = item.getIcon();
            if (icon != null) {
                Drawable wrap = DrawableCompat.wrap(icon);
                icon.mutate();
                DrawableCompat.setTint(wrap, i);
                item.setIcon(icon);
            }
        }
    }

    @NotNull
    public final Drawable tintDrawable(@NotNull Context context, @NotNull Drawable drawable, @AttrRes int i) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(drawable, "drawable");
        return tintDrawableWithColor(drawable, getColor(context, i));
    }

    @NotNull
    public final Drawable tintDrawableWithColor(@NotNull Drawable drawable, @ColorInt int i) {
        Intrinsics.checkParameterIsNotNull(drawable, "drawable");
        Drawable wrap = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(wrap, i);
        Intrinsics.checkExpressionValueIsNotNull(wrap, "tinted");
        return wrap;
    }
}
