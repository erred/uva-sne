package com.facebook.react.flat;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import com.facebook.infer.annotation.Assertions;
import java.util.HashMap;
import javax.annotation.Nullable;

final class TypefaceCache {
    private static final String[] EXTENSIONS = {"", "_bold", "_italic", "_bold_italic"};
    private static final String[] FILE_EXTENSIONS = {".ttf", ".otf"};
    private static final HashMap<String, Typeface[]> FONTFAMILY_CACHE = new HashMap<>();
    private static final String FONTS_ASSET_PATH = "fonts/";
    private static final int MAX_STYLES = 4;
    private static final HashMap<Typeface, Typeface[]> TYPEFACE_CACHE = new HashMap<>();
    @Nullable
    private static AssetManager sAssetManager;

    TypefaceCache() {
    }

    public static void setAssetManager(AssetManager assetManager) {
        sAssetManager = assetManager;
    }

    public static Typeface getTypeface(String str, int i) {
        Typeface[] typefaceArr = (Typeface[]) FONTFAMILY_CACHE.get(str);
        if (typefaceArr == null) {
            typefaceArr = new Typeface[4];
            FONTFAMILY_CACHE.put(str, typefaceArr);
        } else if (typefaceArr[i] != null) {
            return typefaceArr[i];
        }
        Typeface createTypeface = createTypeface(str, i);
        typefaceArr[i] = createTypeface;
        TYPEFACE_CACHE.put(createTypeface, typefaceArr);
        return createTypeface;
    }

    private static Typeface createTypeface(String str, int i) {
        String str2 = EXTENSIONS[i];
        StringBuilder sb = new StringBuilder(32);
        sb.append(FONTS_ASSET_PATH);
        sb.append(str);
        sb.append(str2);
        int length = sb.length();
        String[] strArr = FILE_EXTENSIONS;
        int length2 = strArr.length;
        int i2 = 0;
        while (i2 < length2) {
            sb.append(strArr[i2]);
            try {
                return Typeface.createFromAsset(sAssetManager, sb.toString());
            } catch (RuntimeException unused) {
                sb.setLength(length);
                i2++;
            }
        }
        return (Typeface) Assertions.assumeNotNull(Typeface.create(str, i));
    }

    public static Typeface getTypeface(Typeface typeface, int i) {
        if (typeface == null) {
            return Typeface.defaultFromStyle(i);
        }
        Typeface[] typefaceArr = (Typeface[]) TYPEFACE_CACHE.get(typeface);
        if (typefaceArr == null) {
            typefaceArr = new Typeface[4];
            typefaceArr[typeface.getStyle()] = typeface;
        } else if (typefaceArr[i] != null) {
            return typefaceArr[i];
        }
        Typeface create = Typeface.create(typeface, i);
        typefaceArr[i] = create;
        TYPEFACE_CACHE.put(create, typefaceArr);
        return create;
    }
}
