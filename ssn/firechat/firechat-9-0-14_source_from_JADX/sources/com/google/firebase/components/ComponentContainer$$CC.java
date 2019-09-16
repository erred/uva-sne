package com.google.firebase.components;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.firebase.inject.Provider;

/* compiled from: com.google.firebase:firebase-common@@16.0.1 */
public abstract /* synthetic */ class ComponentContainer$$CC {
    @KeepForSdk
    public static Object get(ComponentContainer componentContainer, Class cls) {
        Provider provider = componentContainer.getProvider(cls);
        if (provider == null) {
            return null;
        }
        return provider.get();
    }
}
