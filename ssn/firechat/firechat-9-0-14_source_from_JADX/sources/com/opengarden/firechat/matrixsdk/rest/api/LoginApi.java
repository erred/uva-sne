package com.opengarden.firechat.matrixsdk.rest.api;

import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlowResponse;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginParams;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationParams;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginApi {
    @GET("login")
    Call<LoginFlowResponse> login();

    @POST("login")
    Call<JsonObject> login(@Body LoginParams loginParams);

    @POST("logout")
    Call<JsonObject> logout();

    @POST("register")
    Call<JsonObject> register(@Body RegistrationParams registrationParams);
}
