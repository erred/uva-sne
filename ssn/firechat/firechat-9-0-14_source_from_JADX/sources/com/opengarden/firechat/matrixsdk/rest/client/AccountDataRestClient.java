package com.opengarden.firechat.matrixsdk.rest.client;

import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.AccountDataApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

public class AccountDataRestClient extends RestClient<AccountDataApi> {
    public static final String ACCOUNT_DATA_KEY_IGNORED_USERS = "ignored_users";
    public static final String ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE = "disable";
    public static final String ACCOUNT_DATA_TYPE_DIRECT_MESSAGES = "m.direct";
    public static final String ACCOUNT_DATA_TYPE_IGNORED_USER_LIST = "m.ignored_user_list";
    public static final String ACCOUNT_DATA_TYPE_PREVIEW_URLS = "org.matrix.preview_urls";
    public static final String ACCOUNT_DATA_TYPE_WIDGETS = "m.widgets";

    public AccountDataRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, AccountDataApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public void setAccountData(String str, String str2, Object obj, ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("setAccountData userId : ");
        sb.append(str);
        sb.append(" type ");
        sb.append(str2);
        String sb2 = sb.toString();
        Call accountData = ((AccountDataApi) this.mApi).setAccountData(str, str2, obj);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str3 = str;
        final String str4 = str2;
        final Object obj2 = obj;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C27161 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                AccountDataRestClient.this.setAccountData(str3, str4, obj2, apiCallback2);
            }
        };
        accountData.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void openIdToken(final String str, final ApiCallback<Map<Object, Object>> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("openIdToken userId : ");
        sb.append(str);
        ((AccountDataApi) this.mApi).openIdToken(str, new HashMap()).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                AccountDataRestClient.this.openIdToken(str, apiCallback);
            }
        }));
    }
}
