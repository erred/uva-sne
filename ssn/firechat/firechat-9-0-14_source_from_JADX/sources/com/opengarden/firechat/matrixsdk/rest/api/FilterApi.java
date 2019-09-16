package com.opengarden.firechat.matrixsdk.rest.api;

import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterBody;
import com.opengarden.firechat.matrixsdk.rest.model.filter.FilterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FilterApi {
    @GET("user/{userId}/filter/{filterId}")
    Call<FilterBody> getFilterById(@Path("userId") String str, @Path("filterId") String str2);

    @POST("user/{userId}/filter")
    Call<FilterResponse> uploadFilter(@Path("userId") String str, @Body FilterBody filterBody);
}
