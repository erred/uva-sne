package com.opengarden.firechat;

import android.os.Handler;
import android.os.Looper;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.HashSet;
import java.util.Set;

public class EventEmitter<T> {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "EventEmitter";
    private final Set<Listener<T>> mCallbacks = new HashSet();
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());

    public interface Listener<T> {
        void onEventFired(EventEmitter<T> eventEmitter, T t);
    }

    public void register(Listener<T> listener) {
        this.mCallbacks.add(listener);
    }

    public void unregister(Listener<T> listener) {
        this.mCallbacks.remove(listener);
    }

    public void fire(final T t) {
        final HashSet hashSet = new HashSet(this.mCallbacks);
        this.mUiHandler.post(new Runnable() {
            public void run() {
                for (Listener onEventFired : hashSet) {
                    try {
                        onEventFired.onEventFired(EventEmitter.this, t);
                    } catch (Exception e) {
                        String access$000 = EventEmitter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Callback threw: ");
                        sb.append(e.getMessage());
                        Log.m212e(access$000, sb.toString(), e);
                    }
                }
            }
        });
    }
}
