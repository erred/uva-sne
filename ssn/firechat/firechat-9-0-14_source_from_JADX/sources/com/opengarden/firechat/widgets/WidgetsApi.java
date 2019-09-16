package com.opengarden.firechat.widgets;

import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface WidgetsApi {
    @POST("/register")
    void register(@Body Map<Object, Object> map, RestAdapterCallback<Map<String, String>> restAdapterCallback);
}
