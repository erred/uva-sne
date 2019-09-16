package p006pl.droidsonroids.gif;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.NonNull;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;

/* renamed from: pl.droidsonroids.gif.MultiCallback */
public class MultiCallback implements Callback {
    private final CopyOnWriteArrayList<CallbackWeakReference> mCallbacks;
    private final boolean mUseViewInvalidate;

    /* renamed from: pl.droidsonroids.gif.MultiCallback$CallbackWeakReference */
    static final class CallbackWeakReference extends WeakReference<Callback> {
        CallbackWeakReference(Callback callback) {
            super(callback);
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            if (get() != ((CallbackWeakReference) obj).get()) {
                z = false;
            }
            return z;
        }

        public int hashCode() {
            Callback callback = (Callback) get();
            if (callback != null) {
                return callback.hashCode();
            }
            return 0;
        }
    }

    public MultiCallback() {
        this(false);
    }

    public MultiCallback(boolean z) {
        this.mCallbacks = new CopyOnWriteArrayList<>();
        this.mUseViewInvalidate = z;
    }

    public void invalidateDrawable(@NonNull Drawable drawable) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            CallbackWeakReference callbackWeakReference = (CallbackWeakReference) this.mCallbacks.get(i);
            Callback callback = (Callback) callbackWeakReference.get();
            if (callback == null) {
                this.mCallbacks.remove(callbackWeakReference);
            } else if (!this.mUseViewInvalidate || !(callback instanceof View)) {
                callback.invalidateDrawable(drawable);
            } else {
                ((View) callback).invalidate();
            }
        }
    }

    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long j) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            CallbackWeakReference callbackWeakReference = (CallbackWeakReference) this.mCallbacks.get(i);
            Callback callback = (Callback) callbackWeakReference.get();
            if (callback != null) {
                callback.scheduleDrawable(drawable, runnable, j);
            } else {
                this.mCallbacks.remove(callbackWeakReference);
            }
        }
    }

    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            CallbackWeakReference callbackWeakReference = (CallbackWeakReference) this.mCallbacks.get(i);
            Callback callback = (Callback) callbackWeakReference.get();
            if (callback != null) {
                callback.unscheduleDrawable(drawable, runnable);
            } else {
                this.mCallbacks.remove(callbackWeakReference);
            }
        }
    }

    public void addView(Callback callback) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            CallbackWeakReference callbackWeakReference = (CallbackWeakReference) this.mCallbacks.get(i);
            if (((Callback) callbackWeakReference.get()) == null) {
                this.mCallbacks.remove(callbackWeakReference);
            }
        }
        this.mCallbacks.addIfAbsent(new CallbackWeakReference(callback));
    }

    public void removeView(Callback callback) {
        for (int i = 0; i < this.mCallbacks.size(); i++) {
            CallbackWeakReference callbackWeakReference = (CallbackWeakReference) this.mCallbacks.get(i);
            Callback callback2 = (Callback) callbackWeakReference.get();
            if (callback2 == null || callback2 == callback) {
                this.mCallbacks.remove(callbackWeakReference);
            }
        }
    }
}
