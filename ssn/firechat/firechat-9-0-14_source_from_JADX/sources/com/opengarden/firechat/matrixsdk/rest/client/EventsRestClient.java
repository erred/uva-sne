package com.opengarden.firechat.matrixsdk.rest.client;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.rest.api.EventsApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.URLPreview;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyProtocol;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsFilter;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsParams;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchRoomEventCategoryParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersRequestResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersRequestResponse.User;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

public class EventsRestClient extends RestClient<EventsApi> {
    private static final int EVENT_STREAM_TIMEOUT_MS = 30000;
    /* access modifiers changed from: private */
    public String mSearchEventsMediaNameIdentifier = null;
    /* access modifiers changed from: private */
    public String mSearchEventsPatternIdentifier = null;
    /* access modifiers changed from: private */
    public String mSearchUsersPatternIdentifier = null;

    public EventsRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, EventsApi.class, "", false);
    }

    protected EventsRestClient(EventsApi eventsApi) {
        this.mApi = eventsApi;
    }

    public void getThirdPartyServerProtocols(final ApiCallback<Map<String, ThirdPartyProtocol>> apiCallback) {
        ((EventsApi) this.mApi).thirdPartyProtocols().enqueue(new RestAdapterCallback("getThirdPartyServerProtocols", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.getThirdPartyServerProtocols(apiCallback);
            }
        }));
    }

    public void getPublicRoomsCount(ApiCallback<Integer> apiCallback) {
        getPublicRoomsCount(null, null, false, apiCallback);
    }

    public void getPublicRoomsCount(String str, ApiCallback<Integer> apiCallback) {
        getPublicRoomsCount(str, null, false, apiCallback);
    }

    public void getPublicRoomsCount(String str, String str2, boolean z, final ApiCallback<Integer> apiCallback) {
        loadPublicRooms(str, str2, z, null, null, 0, new SimpleApiCallback<PublicRoomsResponse>(apiCallback) {
            public void onSuccess(PublicRoomsResponse publicRoomsResponse) {
                apiCallback.onSuccess(publicRoomsResponse.total_room_count_estimate);
            }
        });
    }

    public void loadPublicRooms(String str, String str2, boolean z, String str3, String str4, int i, ApiCallback<PublicRoomsResponse> apiCallback) {
        final String str5;
        PublicRoomsParams publicRoomsParams = new PublicRoomsParams();
        final String str6 = str2;
        publicRoomsParams.thirdPartyInstanceId = str6;
        final boolean z2 = z;
        publicRoomsParams.includeAllNetworks = z2;
        final int i2 = i;
        publicRoomsParams.limit = Integer.valueOf(Math.max(0, i2));
        final String str7 = str4;
        publicRoomsParams.since = str7;
        if (!TextUtils.isEmpty(str3)) {
            publicRoomsParams.filter = new PublicRoomsFilter();
            str5 = str3;
            publicRoomsParams.filter.generic_search_term = str5;
        } else {
            str5 = str3;
        }
        final String str8 = str;
        Call publicRooms = ((EventsApi) this.mApi).publicRooms(str8, publicRoomsParams);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<PublicRoomsResponse> apiCallback2 = apiCallback;
        C27353 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.loadPublicRooms(str8, str6, z2, str5, str7, i2, apiCallback2);
            }
        };
        publicRooms.enqueue(new RestAdapterCallback("loadPublicRooms", unsentEventsManager, apiCallback, r0));
    }

    public void syncFromToken(List<String> list, String str, int i, int i2, String str2, String str3, ApiCallback<ResponseBody> apiCallback) {
        final String str4;
        final String str5;
        final String str6 = str;
        HashMap hashMap = new HashMap();
        if (!TextUtils.isEmpty(str)) {
            hashMap.put("since", str6);
        }
        final int i3 = i;
        int i4 = -1 != i3 ? i3 : 30;
        if (!TextUtils.isEmpty(str2)) {
            str4 = str2;
            hashMap.put("set_presence", str4);
        } else {
            str4 = str2;
        }
        if (!TextUtils.isEmpty(str3)) {
            str5 = str3;
            hashMap.put("filter", str5);
        } else {
            str5 = str3;
        }
        hashMap.put("timeout", Integer.valueOf(i4));
        setConnectionTimeout((str6 == null ? 2 : 1) * EVENT_STREAM_TIMEOUT_MS);
        final List<String> list2 = list;
        Call sync = ((EventsApi) this.mApi).sync(hashMap, list2);
        final int i5 = i2;
        final ApiCallback<ResponseBody> apiCallback2 = apiCallback;
        C27364 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.syncFromToken(list2, str6, i3, i5, str4, str5, apiCallback2);
            }
        };
        RestAdapterCallback restAdapterCallback = new RestAdapterCallback("syncFromToken", null, false, apiCallback, r0);
        sync.enqueue(restAdapterCallback);
    }

    public void syncFromToken(String str, int i, int i2, String str2, String str3, ApiCallback<SyncResponse> apiCallback) {
        final String str4;
        final String str5;
        final String str6 = str;
        HashMap hashMap = new HashMap();
        if (!TextUtils.isEmpty(str)) {
            hashMap.put("since", str6);
        }
        final int i3 = i;
        int i4 = -1 != i3 ? i3 : 30;
        if (!TextUtils.isEmpty(str2)) {
            str4 = str2;
            hashMap.put("set_presence", str4);
        } else {
            str4 = str2;
        }
        if (!TextUtils.isEmpty(str3)) {
            str5 = str3;
            hashMap.put("filter", str5);
        } else {
            str5 = str3;
        }
        hashMap.put("timeout", Integer.valueOf(i4));
        setConnectionTimeout((str6 == null ? 2 : 1) * EVENT_STREAM_TIMEOUT_MS);
        Call sync = ((EventsApi) this.mApi).sync(hashMap);
        final int i5 = i2;
        final ApiCallback<SyncResponse> apiCallback2 = apiCallback;
        C27375 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.syncFromToken(str6, i3, i5, str4, str5, apiCallback2);
            }
        };
        RestAdapterCallback restAdapterCallback = new RestAdapterCallback("syncFromToken", null, false, apiCallback, r0);
        sync.enqueue(restAdapterCallback);
    }

    public void searchMessagesByText(String str, List<String> list, int i, int i2, String str2, ApiCallback<SearchResponse> apiCallback) {
        final String str3 = str;
        final List<String> list2 = list;
        SearchParams searchParams = new SearchParams();
        SearchRoomEventCategoryParams searchRoomEventCategoryParams = new SearchRoomEventCategoryParams();
        searchRoomEventCategoryParams.search_term = str3;
        searchRoomEventCategoryParams.order_by = "recent";
        searchRoomEventCategoryParams.event_context = new HashMap<>();
        searchRoomEventCategoryParams.event_context.put("before_limit", Integer.valueOf(i));
        searchRoomEventCategoryParams.event_context.put("after_limit", Integer.valueOf(i2));
        searchRoomEventCategoryParams.event_context.put("include_profile", Boolean.valueOf(true));
        if (list2 != null) {
            searchRoomEventCategoryParams.filter = new HashMap<>();
            searchRoomEventCategoryParams.filter.put("rooms", list2);
        }
        searchParams.search_categories = new HashMap<>();
        searchParams.search_categories.put("room_events", searchRoomEventCategoryParams);
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        final String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(str3);
        this.mSearchEventsPatternIdentifier = sb3.toString();
        final String str4 = str2;
        Call searchEvents = ((EventsApi) this.mApi).searchEvents(searchParams, str4);
        final ApiCallback<SearchResponse> apiCallback2 = apiCallback;
        C27386 r13 = new ApiCallback<SearchResponse>() {
            private boolean isActiveRequest() {
                String access$000 = EventsRestClient.this.mSearchEventsPatternIdentifier;
                StringBuilder sb = new StringBuilder();
                sb.append(sb2);
                sb.append(str3);
                return TextUtils.equals(access$000, sb.toString());
            }

            public void onSuccess(SearchResponse searchResponse) {
                if (isActiveRequest()) {
                    if (apiCallback2 != null) {
                        apiCallback2.onSuccess(searchResponse);
                    }
                    EventsRestClient.this.mSearchEventsPatternIdentifier = null;
                }
            }

            public void onNetworkError(Exception exc) {
                if (isActiveRequest()) {
                    if (apiCallback2 != null) {
                        apiCallback2.onNetworkError(exc);
                    }
                    EventsRestClient.this.mSearchEventsPatternIdentifier = null;
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (isActiveRequest()) {
                    if (apiCallback2 != null) {
                        apiCallback2.onMatrixError(matrixError);
                    }
                    EventsRestClient.this.mSearchEventsPatternIdentifier = null;
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (isActiveRequest()) {
                    if (apiCallback2 != null) {
                        apiCallback2.onUnexpectedError(exc);
                    }
                    EventsRestClient.this.mSearchEventsPatternIdentifier = null;
                }
            }
        };
        final int i3 = i;
        final int i4 = i2;
        C27397 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.searchMessagesByText(str3, list2, i3, i4, str4, apiCallback2);
            }
        };
        searchEvents.enqueue(new RestAdapterCallback("searchMessageText", null, r13, r0));
    }

    public void searchMediasByText(String str, List<String> list, int i, int i2, String str2, ApiCallback<SearchResponse> apiCallback) {
        final String str3 = str;
        final List<String> list2 = list;
        SearchParams searchParams = new SearchParams();
        SearchRoomEventCategoryParams searchRoomEventCategoryParams = new SearchRoomEventCategoryParams();
        searchRoomEventCategoryParams.search_term = str3;
        searchRoomEventCategoryParams.order_by = "recent";
        searchRoomEventCategoryParams.event_context = new HashMap<>();
        searchRoomEventCategoryParams.event_context.put("before_limit", Integer.valueOf(i));
        searchRoomEventCategoryParams.event_context.put("after_limit", Integer.valueOf(i2));
        searchRoomEventCategoryParams.event_context.put("include_profile", Boolean.valueOf(true));
        searchRoomEventCategoryParams.filter = new HashMap<>();
        if (list2 != null) {
            searchRoomEventCategoryParams.filter.put("rooms", list2);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(Event.EVENT_TYPE_MESSAGE);
        searchRoomEventCategoryParams.filter.put("types", arrayList);
        searchRoomEventCategoryParams.filter.put("contains_url", Boolean.valueOf(true));
        searchParams.search_categories = new HashMap<>();
        searchParams.search_categories.put("room_events", searchRoomEventCategoryParams);
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        final String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(str3);
        this.mSearchEventsMediaNameIdentifier = sb3.toString();
        final String str4 = str2;
        Call searchEvents = ((EventsApi) this.mApi).searchEvents(searchParams, str4);
        final ApiCallback<SearchResponse> apiCallback2 = apiCallback;
        C27408 r13 = new ApiCallback<SearchResponse>() {
            private boolean isActiveRequest() {
                String access$100 = EventsRestClient.this.mSearchEventsMediaNameIdentifier;
                StringBuilder sb = new StringBuilder();
                sb.append(sb2);
                sb.append(str3);
                return TextUtils.equals(access$100, sb.toString());
            }

            public void onSuccess(SearchResponse searchResponse) {
                if (isActiveRequest()) {
                    apiCallback2.onSuccess(searchResponse);
                    EventsRestClient.this.mSearchEventsMediaNameIdentifier = null;
                }
            }

            public void onNetworkError(Exception exc) {
                if (isActiveRequest()) {
                    apiCallback2.onNetworkError(exc);
                    EventsRestClient.this.mSearchEventsMediaNameIdentifier = null;
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (isActiveRequest()) {
                    apiCallback2.onMatrixError(matrixError);
                    EventsRestClient.this.mSearchEventsMediaNameIdentifier = null;
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (isActiveRequest()) {
                    apiCallback2.onUnexpectedError(exc);
                    EventsRestClient.this.mSearchEventsMediaNameIdentifier = null;
                }
            }
        };
        final int i3 = i;
        final int i4 = i2;
        C27419 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.searchMediasByText(str3, list2, i3, i4, str4, apiCallback2);
            }
        };
        searchEvents.enqueue(new RestAdapterCallback("searchMediasByText", null, r13, r0));
    }

    public void searchUsers(String str, Integer num, Set<String> set, ApiCallback<SearchUsersResponse> apiCallback) {
        final String str2 = str;
        final Set<String> set2 = set;
        SearchUsersParams searchUsersParams = new SearchUsersParams();
        searchUsersParams.search_term = str2;
        searchUsersParams.limit = Integer.valueOf(num.intValue() + (set2 != null ? set.size() : 0));
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append(StringUtils.SPACE);
        sb.append(str2);
        sb.append(StringUtils.SPACE);
        final Integer num2 = num;
        sb.append(num2);
        final String sb2 = sb.toString();
        this.mSearchUsersPatternIdentifier = sb2;
        Call searchUsers = ((EventsApi) this.mApi).searchUsers(searchUsersParams);
        final ApiCallback<SearchUsersResponse> apiCallback2 = apiCallback;
        C273010 r11 = new ApiCallback<SearchUsersRequestResponse>() {
            private boolean isActiveRequest() {
                return TextUtils.equals(EventsRestClient.this.mSearchUsersPatternIdentifier, sb2);
            }

            public void onSuccess(SearchUsersRequestResponse searchUsersRequestResponse) {
                if (isActiveRequest()) {
                    SearchUsersResponse searchUsersResponse = new SearchUsersResponse();
                    searchUsersResponse.limited = searchUsersRequestResponse.limited;
                    searchUsersResponse.results = new ArrayList();
                    Set hashSet = set2 != null ? set2 : new HashSet();
                    if (searchUsersRequestResponse.results != null) {
                        for (User user : searchUsersRequestResponse.results) {
                            if (user.user_id != null && !hashSet.contains(user.user_id)) {
                                com.opengarden.firechat.matrixsdk.rest.model.User user2 = new com.opengarden.firechat.matrixsdk.rest.model.User();
                                user2.user_id = user.user_id;
                                user2.avatar_url = user.avatar_url;
                                user2.displayname = user.display_name;
                                searchUsersResponse.results.add(user2);
                            }
                        }
                    }
                    apiCallback2.onSuccess(searchUsersResponse);
                    EventsRestClient.this.mSearchUsersPatternIdentifier = null;
                }
            }

            public void onNetworkError(Exception exc) {
                if (isActiveRequest()) {
                    apiCallback2.onNetworkError(exc);
                    EventsRestClient.this.mSearchUsersPatternIdentifier = null;
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (isActiveRequest()) {
                    apiCallback2.onMatrixError(matrixError);
                    EventsRestClient.this.mSearchUsersPatternIdentifier = null;
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (isActiveRequest()) {
                    apiCallback2.onUnexpectedError(exc);
                    EventsRestClient.this.mSearchUsersPatternIdentifier = null;
                }
            }
        };
        C273111 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.searchUsers(str2, num2, set2, apiCallback2);
            }
        };
        searchUsers.enqueue(new RestAdapterCallback("searchUsers", null, r11, r0));
    }

    public void cancelSearchMediasByText() {
        this.mSearchEventsMediaNameIdentifier = null;
    }

    public void cancelSearchMessagesByText() {
        this.mSearchEventsPatternIdentifier = null;
    }

    public void cancelUsersSearch() {
        this.mSearchUsersPatternIdentifier = null;
    }

    public void getURLPreview(String str, long j, ApiCallback<URLPreview> apiCallback) {
        final String str2 = str;
        final long j2 = j;
        StringBuilder sb = new StringBuilder();
        sb.append("getURLPreview : URL ");
        sb.append(str2);
        sb.append(" with ts ");
        sb.append(j2);
        String sb2 = sb.toString();
        Call uRLPreview = ((EventsApi) this.mApi).getURLPreview(str2, j2);
        final ApiCallback<URLPreview> apiCallback2 = apiCallback;
        C273212 r11 = new SimpleApiCallback<Map<String, Object>>(apiCallback2) {
            public void onSuccess(Map<String, Object> map) {
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(new URLPreview(map, str2));
                }
            }
        };
        C273313 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                EventsRestClient.this.getURLPreview(str2, j2, apiCallback2);
            }
        };
        RestAdapterCallback restAdapterCallback = new RestAdapterCallback(sb2, null, false, r11, r0);
        uRLPreview.enqueue(restAdapterCallback);
    }
}
