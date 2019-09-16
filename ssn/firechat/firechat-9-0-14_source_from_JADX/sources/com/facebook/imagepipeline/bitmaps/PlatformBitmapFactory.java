package com.facebook.imagepipeline.bitmaps;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.support.p000v4.view.ViewCompat;
import android.util.DisplayMetrics;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import javax.annotation.Nullable;

public abstract class PlatformBitmapFactory {
    private static BitmapCreationObserver sBitmapCreationObserver;

    /* renamed from: com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory$1 */
    static /* synthetic */ class C06391 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Config.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            /*
                android.graphics.Bitmap$Config[] r0 = android.graphics.Bitmap.Config.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$android$graphics$Bitmap$Config = r0
                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x0014 }
                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x001f }
                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ALPHA_8     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x002a }
                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ARGB_4444     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x0035 }
                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory.C06391.<clinit>():void");
        }
    }

    public interface BitmapCreationObserver {
        void onBitmapCreated(Bitmap bitmap, @Nullable Object obj);
    }

    public abstract CloseableReference<Bitmap> createBitmapInternal(int i, int i2, Config config);

    public CloseableReference<Bitmap> createBitmap(int i, int i2, Config config) {
        return createBitmap(i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2) {
        return createBitmap(i, i2, Config.ARGB_8888);
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2, Config config, @Nullable Object obj) {
        CloseableReference<Bitmap> createBitmapInternal = createBitmapInternal(i, i2, config);
        addBitmapReference((Bitmap) createBitmapInternal.get(), obj);
        return createBitmapInternal;
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2, @Nullable Object obj) {
        return createBitmap(i, i2, Config.ARGB_8888, obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap) {
        return createBitmap(bitmap, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, @Nullable Object obj) {
        return createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return createBitmap(bitmap, i, i2, i3, i4, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Object obj) {
        return createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false, obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Matrix matrix, boolean z) {
        return createBitmap(bitmap, i, i2, i3, i4, matrix, z, (Object) null);
    }

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        return createScaledBitmap(bitmap, i, i2, z, null);
    }

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z, @Nullable Object obj) {
        checkWidthHeight(i, i2);
        Matrix matrix = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setScale(((float) i) / ((float) width), ((float) i2) / ((float) height));
        return createBitmap(bitmap, 0, 0, width, height, matrix, z, obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Matrix matrix, boolean z, @Nullable Object obj) {
        CloseableReference<Bitmap> closeableReference;
        Paint paint;
        Bitmap bitmap2 = bitmap;
        int i5 = i;
        int i6 = i2;
        int i7 = i3;
        int i8 = i4;
        Matrix matrix2 = matrix;
        Preconditions.checkNotNull(bitmap2, "Source bitmap cannot be null");
        checkXYSign(i, i2);
        checkWidthHeight(i3, i4);
        checkFinalImageBounds(bitmap, i, i2, i3, i4);
        Canvas canvas = new Canvas();
        Rect rect = new Rect(i5, i6, i5 + i7, i6 + i8);
        RectF rectF = new RectF(0.0f, 0.0f, (float) i7, (float) i8);
        Config suitableBitmapConfig = getSuitableBitmapConfig(bitmap);
        if (matrix2 == null || matrix.isIdentity()) {
            closeableReference = createBitmap(i7, i8, suitableBitmapConfig, bitmap.hasAlpha(), obj);
            paint = null;
        } else {
            boolean z2 = !matrix.rectStaysRect();
            RectF rectF2 = new RectF();
            matrix2.mapRect(rectF2, rectF);
            int round = Math.round(rectF2.width());
            int round2 = Math.round(rectF2.height());
            if (z2) {
                suitableBitmapConfig = Config.ARGB_8888;
            }
            closeableReference = createBitmap(round, round2, suitableBitmapConfig, z2 || bitmap.hasAlpha(), obj);
            canvas.translate(-rectF2.left, -rectF2.top);
            canvas.concat(matrix2);
            paint = new Paint();
            paint.setFilterBitmap(z);
            if (z2) {
                paint.setAntiAlias(true);
            }
        }
        Bitmap bitmap3 = (Bitmap) closeableReference.get();
        bitmap3.setDensity(bitmap.getDensity());
        if (VERSION.SDK_INT >= 12) {
            bitmap3.setHasAlpha(bitmap.hasAlpha());
        }
        if (VERSION.SDK_INT >= 19) {
            bitmap3.setPremultiplied(bitmap.isPremultiplied());
        }
        canvas.setBitmap(bitmap3);
        canvas.drawBitmap(bitmap2, rect, rectF, paint);
        canvas.setBitmap(null);
        return closeableReference;
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config) {
        return createBitmap(displayMetrics, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config, @Nullable Object obj) {
        return createBitmap(displayMetrics, i, i2, config, true, obj);
    }

    private CloseableReference<Bitmap> createBitmap(int i, int i2, Config config, boolean z) {
        return createBitmap(i, i2, config, z, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(int i, int i2, Config config, boolean z, @Nullable Object obj) {
        return createBitmap((DisplayMetrics) null, i, i2, config, z, obj);
    }

    private CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config, boolean z) {
        return createBitmap(displayMetrics, i, i2, config, z, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Config config, boolean z, @Nullable Object obj) {
        checkWidthHeight(i, i2);
        CloseableReference<Bitmap> createBitmapInternal = createBitmapInternal(i, i2, config);
        Bitmap bitmap = (Bitmap) createBitmapInternal.get();
        if (displayMetrics != null) {
            bitmap.setDensity(displayMetrics.densityDpi);
        }
        if (VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(z);
        }
        if (config == Config.ARGB_8888 && !z) {
            bitmap.eraseColor(ViewCompat.MEASURED_STATE_MASK);
        }
        addBitmapReference((Bitmap) createBitmapInternal.get(), obj);
        return createBitmapInternal;
    }

    public CloseableReference<Bitmap> createBitmap(int[] iArr, int i, int i2, Config config) {
        return createBitmap(iArr, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int[] iArr, int i, int i2, Config config, @Nullable Object obj) {
        CloseableReference<Bitmap> createBitmapInternal = createBitmapInternal(i, i2, config);
        ((Bitmap) createBitmapInternal.get()).setPixels(iArr, 0, i, 0, 0, i, i2);
        addBitmapReference((Bitmap) createBitmapInternal.get(), obj);
        return createBitmapInternal;
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, Config config) {
        return createBitmap(displayMetrics, iArr, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, Config config, @Nullable Object obj) {
        return createBitmap(displayMetrics, iArr, 0, i, i, i2, config, obj);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, int i3, int i4, Config config) {
        return createBitmap(displayMetrics, iArr, i, i2, i3, i4, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, int i3, int i4, Config config, @Nullable Object obj) {
        CloseableReference<Bitmap> createBitmap = createBitmap(displayMetrics, i3, i4, config, obj);
        ((Bitmap) createBitmap.get()).setPixels(iArr, i, i2, 0, 0, i3, i4);
        return createBitmap;
    }

    private static Config getSuitableBitmapConfig(Bitmap bitmap) {
        Config config = Config.ARGB_8888;
        Config config2 = bitmap.getConfig();
        if (config2 == null) {
            return config;
        }
        switch (C06391.$SwitchMap$android$graphics$Bitmap$Config[config2.ordinal()]) {
            case 1:
                return Config.RGB_565;
            case 2:
                return Config.ALPHA_8;
            default:
                return Config.ARGB_8888;
        }
    }

    private static void checkWidthHeight(int i, int i2) {
        boolean z = false;
        Preconditions.checkArgument(i > 0, "width must be > 0");
        if (i2 > 0) {
            z = true;
        }
        Preconditions.checkArgument(z, "height must be > 0");
    }

    private static void checkXYSign(int i, int i2) {
        boolean z = false;
        Preconditions.checkArgument(i >= 0, "x must be >= 0");
        if (i2 >= 0) {
            z = true;
        }
        Preconditions.checkArgument(z, "y must be >= 0");
    }

    private static void checkFinalImageBounds(Bitmap bitmap, int i, int i2, int i3, int i4) {
        boolean z = false;
        Preconditions.checkArgument(i + i3 <= bitmap.getWidth(), "x + width must be <= bitmap.width()");
        if (i2 + i4 <= bitmap.getHeight()) {
            z = true;
        }
        Preconditions.checkArgument(z, "y + height must be <= bitmap.height()");
    }

    public void setCreationListener(BitmapCreationObserver bitmapCreationObserver) {
        if (sBitmapCreationObserver == null) {
            sBitmapCreationObserver = bitmapCreationObserver;
        }
    }

    public void addBitmapReference(Bitmap bitmap, @Nullable Object obj) {
        if (sBitmapCreationObserver != null) {
            sBitmapCreationObserver.onBitmapCreated(bitmap, obj);
        }
    }
}
