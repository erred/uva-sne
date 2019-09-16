package com.facebook.react.flat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.UIImplementationProvider;
import com.facebook.react.uimanager.UIManagerModule.ViewManagerResolver;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.events.EventDispatcher;
import java.util.List;

public final class FlatUIImplementationProvider extends UIImplementationProvider {
    private final boolean mMemoryImprovementEnabled;

    public FlatUIImplementationProvider() {
        this.mMemoryImprovementEnabled = true;
    }

    public FlatUIImplementationProvider(boolean z) {
        this.mMemoryImprovementEnabled = z;
    }

    public FlatUIImplementation createUIImplementation(ReactApplicationContext reactApplicationContext, List<ViewManager> list, EventDispatcher eventDispatcher, int i) {
        return FlatUIImplementation.createInstance(reactApplicationContext, list, eventDispatcher, this.mMemoryImprovementEnabled, i);
    }

    public FlatUIImplementation createUIImplementation(ReactApplicationContext reactApplicationContext, ViewManagerResolver viewManagerResolver, EventDispatcher eventDispatcher, int i) {
        throw new UnsupportedOperationException("Lazy version of FlatUIImplementations are not supported");
    }
}
