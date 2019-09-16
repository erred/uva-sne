package com.opengarden.firechat.matrixsdk.rest.callback;

import android.support.p000v4.p002os.EnvironmentCompat;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.opengarden.firechat.matrixsdk.rest.callback.DefaultRetrofit2ResponseHandler.Listener;
import com.opengarden.firechat.matrixsdk.rest.model.HttpError;
import com.opengarden.firechat.matrixsdk.rest.model.HttpException;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.C3224Response;
import retrofit2.Call;
import retrofit2.Callback;

public class RestAdapterCallback<T> implements Callback<T> {
    private static final String LOG_TAG = "RestAdapterCallback";
    private final ApiCallback mApiCallback;
    private final String mEventDescription;
    private final boolean mIgnoreEventTimeLifeInOffline;
    private final RequestRetryCallBack mRequestRetryCallBack;
    private final UnsentEventsManager mUnsentEventsManager;

    public interface RequestRetryCallBack {
        void onRetry();
    }

    public RestAdapterCallback(String str, UnsentEventsManager unsentEventsManager, ApiCallback apiCallback, RequestRetryCallBack requestRetryCallBack) {
        this(str, unsentEventsManager, false, apiCallback, requestRetryCallBack);
    }

    public RestAdapterCallback(String str, UnsentEventsManager unsentEventsManager, boolean z, ApiCallback apiCallback, RequestRetryCallBack requestRetryCallBack) {
        if (str != null) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Trigger the event [");
            sb.append(str);
            sb.append("]");
            Log.m209d(str2, sb.toString());
        }
        this.mEventDescription = str;
        this.mIgnoreEventTimeLifeInOffline = z;
        this.mApiCallback = apiCallback;
        this.mRequestRetryCallBack = requestRetryCallBack;
        this.mUnsentEventsManager = unsentEventsManager;
    }

    /* access modifiers changed from: protected */
    public void onEventSent() {
        if (this.mUnsentEventsManager != null) {
            try {
                if (!this.mUnsentEventsManager.getNetworkConnectivityReceiver().isConnected()) {
                    Log.m209d(LOG_TAG, "## onEventSent(): request succeed, while network seen as disconnected => ask ConnectivityReceiver to dispatch info");
                    this.mUnsentEventsManager.getNetworkConnectivityReceiver().checkNetworkConnection(this.mUnsentEventsManager.getContext());
                }
                this.mUnsentEventsManager.onEventSent(this.mApiCallback);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onEventSent(): Exception ");
                sb.append(e.getMessage());
                Log.m209d(str, sb.toString());
            }
        }
    }

    public void onResponse(Call<T> call, C3224Response<T> response) {
        try {
            handleResponse(response);
        } catch (IOException e) {
            onFailure(call, e);
        }
    }

    private void handleResponse(final C3224Response<T> response) throws IOException {
        DefaultRetrofit2ResponseHandler.handleResponse(response, new Listener<T>() {
            public void onSuccess(C3224Response<T> response) {
                RestAdapterCallback.this.success(response.body(), response);
            }

            public void onHttpError(HttpError httpError) {
                RestAdapterCallback.this.failure(response, new HttpException(httpError));
            }
        });
    }

    public void onFailure(Call<T> call, Throwable th) {
        failure(null, (Exception) th);
    }

    public void success(T t, C3224Response<T> response) {
        if (this.mEventDescription != null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## Succeed() : [");
            sb.append(this.mEventDescription);
            sb.append("]");
            Log.m209d(str, sb.toString());
        }
        try {
            onEventSent();
            if (this.mApiCallback != null) {
                try {
                    this.mApiCallback.onSuccess(t);
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## succeed() : onSuccess failed ");
                    sb2.append(e.getMessage());
                    Log.m211e(str2, sb2.toString());
                    this.mApiCallback.onUnexpectedError(e);
                }
            }
        } catch (Exception e2) {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## succeed(): Exception ");
            sb3.append(e2.getMessage());
            Log.m211e(str3, sb3.toString());
        }
    }

    public void failure(C3224Response<T> response, Exception exc) {
        MatrixError matrixError;
        if (this.mEventDescription != null) {
            String str = exc != null ? exc.getMessage() : response != null ? response.message() : EnvironmentCompat.MEDIA_UNKNOWN;
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## failure(): [");
            sb.append(this.mEventDescription);
            sb.append("] with error ");
            sb.append(str);
            Log.m209d(str2, sb.toString());
        }
        boolean z = true;
        boolean z2 = response == null || response.code() < 400 || response.code() > 500;
        if (exc.getCause() != null && ((exc.getCause() instanceof MalformedJsonException) || (exc.getCause() instanceof JsonSyntaxException))) {
            z = false;
        }
        if ((z && z2) && this.mUnsentEventsManager != null) {
            Log.m209d(LOG_TAG, "Add it to the UnsentEventsManager");
            this.mUnsentEventsManager.onEventSendingFailed(this.mEventDescription, this.mIgnoreEventTimeLifeInOffline, response, exc, this.mApiCallback, this.mRequestRetryCallBack);
        } else if (exc == null || !(exc instanceof IOException)) {
            try {
                HttpError httpError = ((HttpException) exc).getHttpError();
                ResponseBody errorBody = response.errorBody();
                String errorBody2 = httpError.getErrorBody();
                matrixError = (MatrixError) JsonUtils.getGson(false).fromJson(errorBody2, MatrixError.class);
                matrixError.mStatus = Integer.valueOf(response.code());
                matrixError.mReason = response.message();
                matrixError.mErrorBodyMimeType = errorBody.contentType();
                matrixError.mErrorBody = errorBody;
                matrixError.mErrorBodyAsString = errorBody2;
            } catch (Exception unused) {
                matrixError = null;
            }
            if (matrixError == null) {
                try {
                    if (this.mApiCallback != null) {
                        this.mApiCallback.onUnexpectedError(exc);
                    }
                } catch (Exception e) {
                    String str3 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## failure():  UnexpectedError ");
                    sb2.append(e.getMessage());
                    Log.m211e(str3, sb2.toString());
                }
            } else if (MatrixError.LIMIT_EXCEEDED.equals(matrixError.errcode) && this.mUnsentEventsManager != null) {
                this.mUnsentEventsManager.onEventSendingFailed(this.mEventDescription, this.mIgnoreEventTimeLifeInOffline, response, exc, this.mApiCallback, this.mRequestRetryCallBack);
            } else if (!MatrixError.isConfigurationErrorCode(matrixError.errcode) || this.mUnsentEventsManager == null) {
                try {
                    if (this.mApiCallback != null) {
                        this.mApiCallback.onMatrixError(matrixError);
                    }
                } catch (Exception e2) {
                    String str4 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## failure():  MatrixError ");
                    sb3.append(e2.getMessage());
                    Log.m211e(str4, sb3.toString());
                }
            } else {
                this.mUnsentEventsManager.onConfigurationErrorCode(matrixError.errcode, this.mEventDescription);
            }
        } else {
            try {
                if (this.mApiCallback != null) {
                    try {
                        this.mApiCallback.onNetworkError(exc);
                    } catch (Exception unused2) {
                        String str5 = LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## failure(): onNetworkError ");
                        sb4.append(exc.getLocalizedMessage());
                        Log.m211e(str5, sb4.toString());
                    }
                }
            } catch (Exception e3) {
                String str6 = LOG_TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("## failure():  NetworkError ");
                sb5.append(e3.getMessage());
                Log.m211e(str6, sb5.toString());
            }
        }
    }
}
