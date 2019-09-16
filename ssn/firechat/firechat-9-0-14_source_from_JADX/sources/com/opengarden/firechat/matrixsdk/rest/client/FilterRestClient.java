package com.opengarden.firechat.matrixsdk.rest.client;

import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.FilterApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterResponse;

public class FilterRestClient extends RestClient<FilterApi> {
    public FilterRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, FilterApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public void uploadFilter(final String str, final FilterBody filterBody, final ApiCallback<FilterResponse> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("uploadFilter userId : ");
        sb.append(str);
        sb.append(" filter : ");
        sb.append(filterBody);
        ((FilterApi) this.mApi).uploadFilter(str, filterBody).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                FilterRestClient.this.uploadFilter(str, filterBody, apiCallback);
            }
        }));
    }

    public void getFilter(final String str, final String str2, final ApiCallback<FilterBody> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getFilter userId : ");
        sb.append(str);
        sb.append(" filterId : ");
        sb.append(str2);
        ((FilterApi) this.mApi).getFilterById(str, str2).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                FilterRestClient.this.getFilter(str, str2, apiCallback);
            }
        }));
    }
}
