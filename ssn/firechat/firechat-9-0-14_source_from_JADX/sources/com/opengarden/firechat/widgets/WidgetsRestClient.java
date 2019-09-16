package com.opengarden.firechat.widgets;

import android.content.Context;
import android.net.Uri;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import java.util.Map;

class WidgetsRestClient extends RestClient<WidgetsApi> {
    public WidgetsRestClient(Context context) {
        super(new HomeServerConnectionConfig(Uri.parse(context.getString(C1299R.string.integrations_rest_url))), WidgetsApi.class, "api/", false);
    }

    public void register(final Map<Object, Object> map, final ApiCallback<Map<String, String>> apiCallback) {
        ((WidgetsApi) this.mApi).register(map, new RestAdapterCallback("Register", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                WidgetsRestClient.this.register(map, apiCallback);
            }
        }));
    }
}
