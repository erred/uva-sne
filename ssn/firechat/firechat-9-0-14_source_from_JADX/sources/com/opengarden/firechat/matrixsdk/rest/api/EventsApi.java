package com.opengarden.firechat.matrixsdk.rest.api;

import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyProtocol;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsParams;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersParams;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersRequestResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.SyncResponse;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface EventsApi {
    @GET("_matrix/media/r0//preview_url")
    Call<Map<String, Object>> getURLPreview(@Query("url") String str, @Query("ts") long j);

    @POST("_matrix/client/r0/publicRooms")
    Call<PublicRoomsResponse> publicRooms(@Query("server") String str, @Body PublicRoomsParams publicRoomsParams);

    @POST("_matrix/client/r0/search")
    Call<SearchResponse> searchEvents(@Body SearchParams searchParams, @Query("next_batch") String str);

    @POST("_matrix/client/r0//user_directory/search")
    Call<SearchUsersRequestResponse> searchUsers(@Body SearchUsersParams searchUsersParams);

    @GET("_matrix/client/r0/sync")
    Call<SyncResponse> sync(@QueryMap Map<String, Object> map);

    @GET("_matrix/client/r0/sync")
    Call<ResponseBody> sync(@QueryMap Map<String, Object> map, @Query(encoded = true, value = "extra_users") List<String> list);

    @GET("_matrix/client/unstable/thirdparty/protocols")
    Call<Map<String, ThirdPartyProtocol>> thirdPartyProtocols();
}
