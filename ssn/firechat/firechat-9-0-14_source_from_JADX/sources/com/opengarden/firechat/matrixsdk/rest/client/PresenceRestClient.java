package com.opengarden.firechat.matrixsdk.rest.client;

import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.PresenceApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.User;

public class PresenceRestClient extends RestClient<PresenceApi> {
    public PresenceRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, PresenceApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public void getPresence(final String str, final ApiCallback<User> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getPresence userId : ");
        sb.append(str);
        ((PresenceApi) this.mApi).presenceStatus(str).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                PresenceRestClient.this.getPresence(str, apiCallback);
            }
        }));
    }
}
