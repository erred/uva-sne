package androidx.core.graphics.drawable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.support.annotation.C0010Px;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\u001a*\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0003\u0010\u0003\u001a\u00020\u00042\b\b\u0003\u0010\u0005\u001a\u00020\u00042\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u001a2\u0010\b\u001a\u00020\t*\u00020\u00022\b\b\u0003\u0010\n\u001a\u00020\u00042\b\b\u0003\u0010\u000b\u001a\u00020\u00042\b\b\u0003\u0010\f\u001a\u00020\u00042\b\b\u0003\u0010\r\u001a\u00020\u0004¨\u0006\u000e"}, mo21251d2 = {"toBitmap", "Landroid/graphics/Bitmap;", "Landroid/graphics/drawable/Drawable;", "width", "", "height", "config", "Landroid/graphics/Bitmap$Config;", "updateBounds", "", "left", "top", "right", "bottom", "core-ktx_release"}, mo21252k = 2, mo21253mv = {1, 1, 9})
/* compiled from: Drawable.kt */
public final class DrawableKt {
    @NotNull
    public static /* bridge */ /* synthetic */ Bitmap toBitmap$default(Drawable drawable, int i, int i2, Config config, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = drawable.getIntrinsicWidth();
        }
        if ((i3 & 2) != 0) {
            i2 = drawable.getIntrinsicHeight();
        }
        if ((i3 & 4) != 0) {
            config = null;
        }
        return toBitmap(drawable, i, i2, config);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x001a, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r0.getConfig(), (java.lang.Object) r8) != false) goto L_0x001c;
     */
    @org.jetbrains.annotations.NotNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final android.graphics.Bitmap toBitmap(@org.jetbrains.annotations.NotNull android.graphics.drawable.Drawable r5, @android.support.annotation.C0010Px int r6, @android.support.annotation.C0010Px int r7, @org.jetbrains.annotations.Nullable android.graphics.Bitmap.Config r8) {
        /*
            boolean r0 = r5 instanceof android.graphics.drawable.BitmapDrawable
            if (r0 == 0) goto L_0x0043
            if (r8 == 0) goto L_0x001c
            r0 = r5
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            java.lang.String r1 = "bitmap"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r1)
            android.graphics.Bitmap$Config r0 = r0.getConfig()
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r8)
            if (r0 == 0) goto L_0x0043
        L_0x001c:
            android.graphics.drawable.BitmapDrawable r5 = (android.graphics.drawable.BitmapDrawable) r5
            int r8 = r5.getIntrinsicWidth()
            if (r6 != r8) goto L_0x0034
            int r8 = r5.getIntrinsicHeight()
            if (r7 != r8) goto L_0x0034
            android.graphics.Bitmap r5 = r5.getBitmap()
            java.lang.String r6 = "bitmap"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r5, r6)
            return r5
        L_0x0034:
            android.graphics.Bitmap r5 = r5.getBitmap()
            r8 = 1
            android.graphics.Bitmap r5 = android.graphics.Bitmap.createScaledBitmap(r5, r6, r7, r8)
            java.lang.String r6 = "Bitmap.createScaledBitma…map, width, height, true)"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r5, r6)
            return r5
        L_0x0043:
            android.graphics.Rect r0 = r5.getBounds()
            int r1 = r0.left
            int r2 = r0.top
            int r3 = r0.right
            int r0 = r0.bottom
            if (r8 == 0) goto L_0x0052
            goto L_0x0054
        L_0x0052:
            android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888
        L_0x0054:
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createBitmap(r6, r7, r8)
            r4 = 0
            r5.setBounds(r4, r4, r6, r7)
            android.graphics.Canvas r6 = new android.graphics.Canvas
            r6.<init>(r8)
            r5.draw(r6)
            r5.setBounds(r1, r2, r3, r0)
            java.lang.String r5 = "bitmap"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r8, r5)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.graphics.drawable.DrawableKt.toBitmap(android.graphics.drawable.Drawable, int, int, android.graphics.Bitmap$Config):android.graphics.Bitmap");
    }

    public static /* bridge */ /* synthetic */ void updateBounds$default(Drawable drawable, int i, int i2, int i3, int i4, int i5, Object obj) {
        if ((i5 & 1) != 0) {
            i = drawable.getBounds().left;
        }
        if ((i5 & 2) != 0) {
            i2 = drawable.getBounds().top;
        }
        if ((i5 & 4) != 0) {
            i3 = drawable.getBounds().right;
        }
        if ((i5 & 8) != 0) {
            i4 = drawable.getBounds().bottom;
        }
        updateBounds(drawable, i, i2, i3, i4);
    }

    public static final void updateBounds(@NotNull Drawable drawable, @C0010Px int i, @C0010Px int i2, @C0010Px int i3, @C0010Px int i4) {
        drawable.setBounds(i, i2, i3, i4);
    }
}
