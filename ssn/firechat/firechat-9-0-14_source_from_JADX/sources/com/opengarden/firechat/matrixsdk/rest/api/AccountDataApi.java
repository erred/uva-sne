package com.opengarden.firechat.matrixsdk.rest.api;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AccountDataApi {
    @POST("user/{userId}/openid/request_token")
    Call<Map<Object, Object>> openIdToken(@Path("userId") String str, @Body Map<Object, Object> map);

    @PUT("user/{userId}/account_data/{type}")
    Call<Void> setAccountData(@Path("userId") String str, @Path("type") String str2, @Body Object obj);
}
