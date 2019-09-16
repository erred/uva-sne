package com.opengarden.firechat.matrixsdk.rest.client;

import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.CallRulesApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.DefaultRetrofit2CallbackWrapper;

public class CallRestClient extends RestClient<CallRulesApi> {
    public CallRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, CallRulesApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public void getTurnServer(ApiCallback<JsonObject> apiCallback) {
        ((CallRulesApi) this.mApi).getTurnServer().enqueue(new DefaultRetrofit2CallbackWrapper(apiCallback));
    }
}
