package p006pl.droidsonroids.gif;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

/* renamed from: pl.droidsonroids.gif.GifOptions */
public class GifOptions {
    boolean inIsOpaque;
    char inSampleSize;

    public GifOptions() {
        reset();
    }

    private void reset() {
        this.inSampleSize = 1;
        this.inIsOpaque = false;
    }

    public void setInSampleSize(@IntRange(from = 1, mo107to = 65535) int i) {
        if (i < 1 || i > 65535) {
            this.inSampleSize = 1;
        } else {
            this.inSampleSize = (char) i;
        }
    }

    public void setInIsOpaque(boolean z) {
        this.inIsOpaque = z;
    }

    /* access modifiers changed from: 0000 */
    public void setFrom(@Nullable GifOptions gifOptions) {
        if (gifOptions == null) {
            reset();
            return;
        }
        this.inIsOpaque = gifOptions.inIsOpaque;
        this.inSampleSize = gifOptions.inSampleSize;
    }
}
