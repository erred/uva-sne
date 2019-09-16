package com.opengarden.firechat.matrixsdk.rest.client;

import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.data.Pusher;
import com.opengarden.firechat.matrixsdk.rest.api.PushersApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.PushersResponse;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.HashMap;
import retrofit2.Call;

public class PushersRestClient extends RestClient<PushersApi> {
    private static final String DATA_KEY_HTTP_URL = "url";
    private static final String LOG_TAG = "PushersRestClient";
    private static final String PUSHER_KIND_HTTP = "http";

    public PushersRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, PushersApi.class, RestClient.URI_API_PREFIX_PATH_R0, true);
    }

    public void addHttpPusher(String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z, boolean z2, ApiCallback<Void> apiCallback) {
        manageHttpPusher(str, str2, str3, str4, str5, str6, str7, z, z2, true, apiCallback);
    }

    public void removeHttpPusher(String str, String str2, String str3, String str4, String str5, String str6, String str7, ApiCallback<Void> apiCallback) {
        manageHttpPusher(str, str2, str3, str4, str5, str6, str7, false, false, false, apiCallback);
    }

    /* access modifiers changed from: private */
    public void manageHttpPusher(String str, String str2, String str3, String str4, String str5, String str6, String str7, boolean z, boolean z2, boolean z3, ApiCallback<Void> apiCallback) {
        Pusher pusher = new Pusher();
        final String str8 = str;
        pusher.pushkey = str8;
        final String str9 = str2;
        pusher.appId = str9;
        final String str10 = str3;
        pusher.profileTag = str10;
        final String str11 = str4;
        pusher.lang = str11;
        pusher.kind = z3 ? "http" : null;
        final String str12 = str5;
        pusher.appDisplayName = str12;
        final String str13 = str6;
        pusher.deviceDisplayName = str13;
        pusher.data = new HashMap();
        String str14 = str7;
        pusher.data.put("url", str14);
        if (z3) {
            pusher.append = Boolean.valueOf(z);
        }
        if (z2) {
            pusher.data.put("format", "event_id_only");
        }
        Call call = ((PushersApi) this.mApi).set(pusher);
        C27961 r14 = r0;
        final String str15 = str14;
        final boolean z4 = z;
        final boolean z5 = z2;
        final boolean z6 = z3;
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C27961 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                PushersRestClient.this.manageHttpPusher(str8, str9, str10, str11, str12, str13, str15, z4, z5, z6, apiCallback2);
            }
        };
        call.enqueue(new RestAdapterCallback("manageHttpPusher", unsentEventsManager, apiCallback, r14));
    }

    public void getPushers(final ApiCallback<PushersResponse> apiCallback) {
        ((PushersApi) this.mApi).get().enqueue(new RestAdapterCallback("getPushers", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                PushersRestClient.this.getPushers(apiCallback);
            }
        }));
    }
}
