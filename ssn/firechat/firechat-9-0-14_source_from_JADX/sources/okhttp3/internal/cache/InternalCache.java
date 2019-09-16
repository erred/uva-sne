package okhttp3.internal.cache;

import java.io.IOException;
import okhttp3.C3012Response;
import okhttp3.Request;

public interface InternalCache {
    C3012Response get(Request request) throws IOException;

    CacheRequest put(C3012Response response) throws IOException;

    void remove(Request request) throws IOException;

    void trackConditionalCacheHit();

    void trackResponse(CacheStrategy cacheStrategy);

    void update(C3012Response response, C3012Response response2);
}
