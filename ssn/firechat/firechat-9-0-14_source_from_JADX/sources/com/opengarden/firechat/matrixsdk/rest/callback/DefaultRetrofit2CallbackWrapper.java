package com.opengarden.firechat.matrixsdk.rest.callback;

import com.opengarden.firechat.matrixsdk.rest.callback.DefaultRetrofit2ResponseHandler.Listener;
import com.opengarden.firechat.matrixsdk.rest.model.HttpError;
import com.opengarden.firechat.matrixsdk.rest.model.HttpException;
import java.io.IOException;
import retrofit2.C3224Response;
import retrofit2.Call;
import retrofit2.Callback;

public class DefaultRetrofit2CallbackWrapper<T> implements Callback<T>, Listener<T> {
    private final ApiCallback<T> apiCallback;

    public DefaultRetrofit2CallbackWrapper(ApiCallback<T> apiCallback2) {
        this.apiCallback = apiCallback2;
    }

    public ApiCallback<T> getApiCallback() {
        return this.apiCallback;
    }

    public void onResponse(Call<T> call, C3224Response<T> response) {
        try {
            handleResponse(response);
        } catch (IOException e) {
            this.apiCallback.onUnexpectedError(e);
        }
    }

    private void handleResponse(C3224Response<T> response) throws IOException {
        DefaultRetrofit2ResponseHandler.handleResponse(response, this);
    }

    public void onFailure(Call<T> call, Throwable th) {
        this.apiCallback.onNetworkError((Exception) th);
    }

    public void onSuccess(C3224Response<T> response) {
        this.apiCallback.onSuccess(response.body());
    }

    public void onHttpError(HttpError httpError) {
        this.apiCallback.onNetworkError(new HttpException(httpError));
    }
}
