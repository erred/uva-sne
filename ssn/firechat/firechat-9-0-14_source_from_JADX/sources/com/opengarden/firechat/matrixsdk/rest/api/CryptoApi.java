package com.opengarden.firechat.matrixsdk.rest.api;

import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeyChangesResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysClaimResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysQueryResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysUploadResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceParams;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DevicesListResponse;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CryptoApi {
    @POST("keys/claim")
    Call<KeysClaimResponse> claimOneTimeKeysForUsersDevices(@Body Map<String, Object> map);

    @HTTP(hasBody = true, method = "DELETE", path = "devices/{device_id}")
    Call<Void> deleteDevice(@Path("device_id") String str, @Body DeleteDeviceParams deleteDeviceParams);

    @POST("keys/query")
    Call<KeysQueryResponse> downloadKeysForUsers(@Body Map<String, Object> map);

    @PUT("sendToDevice/{eventType}/{random}")
    Call<Void> forwardToDevice(@Path("eventType") String str, @Path("random") String str2, @Body Map<String, Object> map, @Query("access_token") String str3);

    @GET("devices")
    Call<DevicesListResponse> getDevices();

    @GET("keys/changes")
    Call<KeyChangesResponse> getKeyChanges(@Query("from") String str, @Query("to") String str2);

    @PUT("sendToDevice/{eventType}/{random}")
    Call<Void> sendToDevice(@Path("eventType") String str, @Path("random") String str2, @Body Map<String, Object> map);

    @PUT("devices/{device_id}")
    Call<Void> updateDeviceInfo(@Path("device_id") String str, @Body Map<String, String> map);

    @POST("keys/upload/{deviceId}")
    Call<KeysUploadResponse> uploadKeys(@Path("deviceId") String str, @Body Map<String, Object> map);

    @POST("keys/upload")
    Call<KeysUploadResponse> uploadKeys(@Body Map<String, Object> map);
}
