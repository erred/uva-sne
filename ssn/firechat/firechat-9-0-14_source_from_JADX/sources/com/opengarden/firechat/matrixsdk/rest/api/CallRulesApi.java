package com.opengarden.firechat.matrixsdk.rest.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CallRulesApi {
    @GET("voip/turnServer")
    Call<JsonObject> getTurnServer();
}
