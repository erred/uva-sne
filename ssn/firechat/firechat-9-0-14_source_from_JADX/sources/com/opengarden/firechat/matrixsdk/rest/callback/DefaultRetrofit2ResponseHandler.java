package com.opengarden.firechat.matrixsdk.rest.callback;

import com.opengarden.firechat.matrixsdk.rest.model.HttpError;
import java.io.IOException;
import retrofit2.C3224Response;

public class DefaultRetrofit2ResponseHandler {

    public interface Listener<T> {
        void onHttpError(HttpError httpError);

        void onSuccess(C3224Response<T> response);
    }

    public static <T> void handleResponse(C3224Response<T> response, Listener<T> listener) throws IOException {
        if (response.isSuccessful()) {
            listener.onSuccess(response);
        } else {
            listener.onHttpError(new HttpError(response.errorBody().string(), response.code()));
        }
    }
}
