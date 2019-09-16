package com.facebook.cache.common;

import com.facebook.cache.common.CacheEventListener.EvictionReason;
import java.io.IOException;
import javax.annotation.Nullable;

public interface CacheEvent {
    @Nullable
    CacheKey getCacheKey();

    long getCacheLimit();

    long getCacheSize();

    @Nullable
    EvictionReason getEvictionReason();

    @Nullable
    IOException getException();

    long getItemSize();

    @Nullable
    String getResourceId();
}
