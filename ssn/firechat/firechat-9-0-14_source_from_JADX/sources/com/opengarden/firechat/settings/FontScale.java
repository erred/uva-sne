package com.opengarden.firechat.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.ObjectRef;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010$\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0011\u001a\u00020\u000eJ\u0006\u0010\u0012\u001a\u00020\u0004J\u0006\u0010\u0013\u001a\u00020\u0004J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0004J\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00040\rX\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00100\rX\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"}, mo21251d2 = {"Lcom/opengarden/firechat/settings/FontScale;", "", "()V", "APPLICATION_FONT_SCALE_KEY", "", "FONT_SCALE_HUGE", "FONT_SCALE_LARGE", "FONT_SCALE_LARGER", "FONT_SCALE_LARGEST", "FONT_SCALE_NORMAL", "FONT_SCALE_SMALL", "FONT_SCALE_TINY", "fontScaleToPrefValue", "", "", "prefValueToNameResId", "", "getFontScale", "getFontScaleDescription", "getFontScalePrefValue", "saveFontScale", "", "scaleValue", "updateFontScale", "fontScaleDescription", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: FontScale.kt */
public final class FontScale {
    private static final String APPLICATION_FONT_SCALE_KEY = "APPLICATION_FONT_SCALE_KEY";
    private static final String FONT_SCALE_HUGE = "FONT_SCALE_HUGE";
    private static final String FONT_SCALE_LARGE = "FONT_SCALE_LARGE";
    private static final String FONT_SCALE_LARGER = "FONT_SCALE_LARGER";
    private static final String FONT_SCALE_LARGEST = "FONT_SCALE_LARGEST";
    private static final String FONT_SCALE_NORMAL = "FONT_SCALE_NORMAL";
    private static final String FONT_SCALE_SMALL = "FONT_SCALE_SMALL";
    private static final String FONT_SCALE_TINY = "FONT_SCALE_TINY";
    public static final FontScale INSTANCE = new FontScale();
    private static final Map<Float, String> fontScaleToPrefValue = MapsKt.mapOf(TuplesKt.m228to(Float.valueOf(0.7f), FONT_SCALE_TINY), TuplesKt.m228to(Float.valueOf(0.85f), FONT_SCALE_SMALL), TuplesKt.m228to(Float.valueOf(1.0f), FONT_SCALE_NORMAL), TuplesKt.m228to(Float.valueOf(1.15f), FONT_SCALE_LARGE), TuplesKt.m228to(Float.valueOf(1.3f), FONT_SCALE_LARGER), TuplesKt.m228to(Float.valueOf(1.45f), FONT_SCALE_LARGEST), TuplesKt.m228to(Float.valueOf(1.6f), FONT_SCALE_HUGE));
    private static final Map<String, Integer> prefValueToNameResId = MapsKt.mapOf(TuplesKt.m228to(FONT_SCALE_TINY, Integer.valueOf(C1299R.string.tiny)), TuplesKt.m228to(FONT_SCALE_SMALL, Integer.valueOf(C1299R.string.small)), TuplesKt.m228to(FONT_SCALE_NORMAL, Integer.valueOf(C1299R.string.normal)), TuplesKt.m228to(FONT_SCALE_LARGE, Integer.valueOf(C1299R.string.large)), TuplesKt.m228to(FONT_SCALE_LARGER, Integer.valueOf(C1299R.string.larger)), TuplesKt.m228to(FONT_SCALE_LARGEST, Integer.valueOf(C1299R.string.largest)), TuplesKt.m228to(FONT_SCALE_HUGE, Integer.valueOf(C1299R.string.huge)));

    private FontScale() {
    }

    @NotNull
    public final String getFontScalePrefValue() {
        VectorApp instance = VectorApp.getInstance();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(instance);
        ObjectRef objectRef = new ObjectRef();
        if (!defaultSharedPreferences.contains(APPLICATION_FONT_SCALE_KEY)) {
            Intrinsics.checkExpressionValueIsNotNull(instance, "context");
            Resources resources = instance.getResources();
            Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
            float f = resources.getConfiguration().fontScale;
            objectRef.element = FONT_SCALE_NORMAL;
            if (fontScaleToPrefValue.containsKey(Float.valueOf(f))) {
                T t = fontScaleToPrefValue.get(Float.valueOf(f));
                if (t == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
                }
                objectRef.element = (String) t;
            }
            Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "preferences");
            Editor edit = defaultSharedPreferences.edit();
            Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
            edit.putString(APPLICATION_FONT_SCALE_KEY, (String) objectRef.element);
            edit.apply();
        } else {
            T string = defaultSharedPreferences.getString(APPLICATION_FONT_SCALE_KEY, FONT_SCALE_NORMAL);
            Intrinsics.checkExpressionValueIsNotNull(string, "preferences.getString(AP…E_KEY, FONT_SCALE_NORMAL)");
            objectRef.element = string;
        }
        return (String) objectRef.element;
    }

    public final float getFontScale() {
        String fontScalePrefValue = getFontScalePrefValue();
        if (fontScaleToPrefValue.containsValue(fontScalePrefValue)) {
            for (Entry entry : fontScaleToPrefValue.entrySet()) {
                if (TextUtils.equals((CharSequence) entry.getValue(), fontScalePrefValue)) {
                    return ((Number) entry.getKey()).floatValue();
                }
            }
        }
        return 1.0f;
    }

    @NotNull
    public final String getFontScaleDescription() {
        VectorApp instance = VectorApp.getInstance();
        String fontScalePrefValue = getFontScalePrefValue();
        if (prefValueToNameResId.containsKey(fontScalePrefValue)) {
            Object obj = prefValueToNameResId.get(fontScalePrefValue);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
            }
            String string = instance.getString(((Integer) obj).intValue());
            Intrinsics.checkExpressionValueIsNotNull(string, "context.getString(prefVa…eResId[fontScale] as Int)");
            return string;
        }
        String string2 = instance.getString(C1299R.string.normal);
        Intrinsics.checkExpressionValueIsNotNull(string2, "context.getString(R.string.normal)");
        return string2;
    }

    public final void updateFontScale(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "fontScaleDescription");
        VectorApp instance = VectorApp.getInstance();
        for (Entry entry : prefValueToNameResId.entrySet()) {
            if (TextUtils.equals(instance.getString(((Number) entry.getValue()).intValue()), str)) {
                saveFontScale((String) entry.getKey());
            }
        }
        Intrinsics.checkExpressionValueIsNotNull(instance, "context");
        Resources resources = instance.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.fontScale = getFontScale();
        Resources resources2 = instance.getResources();
        Resources resources3 = instance.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources3, "context.resources");
        resources2.updateConfiguration(configuration, resources3.getDisplayMetrics());
    }

    public final void saveFontScale(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "scaleValue");
        VectorApp instance = VectorApp.getInstance();
        if (!TextUtils.isEmpty(str)) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(instance);
            Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…haredPreferences(context)");
            Editor edit = defaultSharedPreferences.edit();
            Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
            edit.putString(APPLICATION_FONT_SCALE_KEY, str);
            edit.apply();
        }
    }
}
