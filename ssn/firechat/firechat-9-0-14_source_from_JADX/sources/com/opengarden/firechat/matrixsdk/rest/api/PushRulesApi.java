package com.opengarden.firechat.matrixsdk.rest.api;

import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRulesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PushRulesApi {
    @PUT("pushrules/global/{kind}/{ruleId}")
    Call<Void> addRule(@Path("kind") String str, @Path("ruleId") String str2, @Body JsonElement jsonElement);

    @DELETE("pushrules/global/{kind}/{ruleId}")
    Call<Void> deleteRule(@Path("kind") String str, @Path("ruleId") String str2);

    @GET("pushrules/")
    Call<PushRulesResponse> getAllRules();

    @PUT("pushrules/global/{kind}/{ruleId}/enabled")
    Call<Void> updateEnableRuleStatus(@Path("kind") String str, @Path("ruleId") String str2, @Body Boolean bool);

    @PUT("pushrules/global/{kind}/{ruleId}/actions")
    Call<Void> updateRuleActions(@Path("kind") String str, @Path("ruleId") String str2, @Body Object obj);
}
