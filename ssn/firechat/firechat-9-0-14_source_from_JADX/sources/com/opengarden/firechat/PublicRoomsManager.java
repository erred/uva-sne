package com.opengarden.firechat;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoomsResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.List;

public class PublicRoomsManager {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PublicRoomsManager";
    public static final int PUBLIC_ROOMS_LIMIT = 20;
    private static PublicRoomsManager sPublicRoomsManager;
    /* access modifiers changed from: private */
    public boolean mCountRefreshInProgress = false;
    /* access modifiers changed from: private */
    public String mForwardPaginationToken;
    private boolean mIncludeAllNetworks;
    /* access modifiers changed from: private */
    public final List<PublicRoomsManagerListener> mListeners = new ArrayList();
    /* access modifiers changed from: private */
    public Integer mPublicRoomsCount = null;
    /* access modifiers changed from: private */
    public String mRequestKey = null;
    /* access modifiers changed from: private */
    public String mRequestServer;
    private String mSearchedPattern;
    /* access modifiers changed from: private */
    public MXSession mSession;
    private String mThirdPartyInstanceId;

    public interface PublicRoomsManagerListener {
        void onPublicRoomsCountRefresh(Integer num);
    }

    public static PublicRoomsManager getInstance() {
        if (sPublicRoomsManager == null) {
            sPublicRoomsManager = new PublicRoomsManager();
        }
        return sPublicRoomsManager;
    }

    public void setSession(MXSession mXSession) {
        this.mSession = mXSession;
    }

    public boolean isRequestInProgress() {
        return !TextUtils.isEmpty(this.mRequestKey);
    }

    public boolean hasMoreResults() {
        return !TextUtils.isEmpty(this.mForwardPaginationToken);
    }

    /* access modifiers changed from: private */
    public void launchPublicRoomsRequest(final ApiCallback<List<PublicRoom>> apiCallback) {
        final String str = this.mRequestKey;
        if (this.mSession != null) {
            this.mSession.getEventsApiClient().loadPublicRooms(this.mRequestServer, this.mThirdPartyInstanceId, this.mIncludeAllNetworks, this.mSearchedPattern, this.mForwardPaginationToken, 20, new ApiCallback<PublicRoomsResponse>() {
                public void onSuccess(PublicRoomsResponse publicRoomsResponse) {
                    if (TextUtils.equals(str, PublicRoomsManager.this.mRequestKey)) {
                        List list = publicRoomsResponse.chunk;
                        if (list == null) {
                            list = new ArrayList();
                        }
                        String access$100 = PublicRoomsManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## launchPublicRoomsRequest() : retrieves ");
                        sb.append(list.size());
                        sb.append(" rooms");
                        Log.m209d(access$100, sb.toString());
                        PublicRoomsManager.this.mForwardPaginationToken = publicRoomsResponse.next_batch;
                        if (apiCallback != null) {
                            apiCallback.onSuccess(list);
                        }
                        PublicRoomsManager.this.mRequestKey = null;
                        return;
                    }
                    Log.m209d(PublicRoomsManager.LOG_TAG, "## launchPublicRoomsRequest() : the request has been cancelled");
                }

                public void onNetworkError(Exception exc) {
                    if (TextUtils.equals(str, PublicRoomsManager.this.mRequestKey)) {
                        String access$100 = PublicRoomsManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## launchPublicRoomsRequest() : onNetworkError ");
                        sb.append(exc.getMessage());
                        Log.m209d(access$100, sb.toString());
                        if (apiCallback != null) {
                            apiCallback.onNetworkError(exc);
                        }
                        PublicRoomsManager.this.mRequestKey = null;
                        return;
                    }
                    Log.m209d(PublicRoomsManager.LOG_TAG, "## launchPublicRoomsRequest() : the request has been cancelled");
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (TextUtils.equals(str, PublicRoomsManager.this.mRequestKey)) {
                        String access$100 = PublicRoomsManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## launchPublicRoomsRequest() : MatrixError ");
                        sb.append(matrixError.getLocalizedMessage());
                        Log.m209d(access$100, sb.toString());
                        if (MatrixError.UNKNOWN.equals(matrixError.errcode) && PublicRoomsManager.this.mRequestServer == null) {
                            PublicRoomsManager.this.mRequestServer = PublicRoomsManager.this.mSession.getHomeServerConfig().getHomeserverUri().getHost();
                            String access$1002 = PublicRoomsManager.LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## launchPublicRoomsRequest() : mRequestServer == null fails -> try ");
                            sb2.append(PublicRoomsManager.this.mRequestServer);
                            Log.m211e(access$1002, sb2.toString());
                            PublicRoomsManager.this.launchPublicRoomsRequest(apiCallback);
                        } else if (apiCallback != null) {
                            apiCallback.onMatrixError(matrixError);
                        }
                        PublicRoomsManager.this.mRequestKey = null;
                        return;
                    }
                    Log.m209d(PublicRoomsManager.LOG_TAG, "## launchPublicRoomsRequest() : the request has been cancelled");
                }

                public void onUnexpectedError(Exception exc) {
                    if (TextUtils.equals(str, PublicRoomsManager.this.mRequestKey)) {
                        String access$100 = PublicRoomsManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## launchPublicRoomsRequest() : onUnexpectedError ");
                        sb.append(exc.getLocalizedMessage());
                        Log.m209d(access$100, sb.toString());
                        if (apiCallback != null) {
                            apiCallback.onUnexpectedError(exc);
                        }
                        PublicRoomsManager.this.mRequestKey = null;
                        return;
                    }
                    Log.m209d(PublicRoomsManager.LOG_TAG, "## launchPublicRoomsRequest() : the request has been cancelled");
                }
            });
        }
    }

    public void startPublicRoomsSearch(String str, String str2, boolean z, String str3, ApiCallback<List<PublicRoom>> apiCallback) {
        String str4 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## startPublicRoomsSearch()  : server ");
        sb.append(str);
        sb.append(" pattern ");
        sb.append(str3);
        Log.m209d(str4, sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("startPublicRoomsSearch");
        sb2.append(System.currentTimeMillis());
        this.mRequestKey = sb2.toString();
        this.mRequestServer = str;
        this.mThirdPartyInstanceId = str2;
        this.mIncludeAllNetworks = z;
        this.mSearchedPattern = str3;
        this.mForwardPaginationToken = null;
        launchPublicRoomsRequest(apiCallback);
    }

    public boolean forwardPaginate(ApiCallback<List<PublicRoom>> apiCallback) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## forwardPaginate()  : server ");
        sb.append(this.mRequestServer);
        sb.append(" pattern ");
        sb.append(this.mSearchedPattern);
        sb.append(" mForwardPaginationToken ");
        sb.append(this.mForwardPaginationToken);
        Log.m209d(str, sb.toString());
        if (isRequestInProgress()) {
            Log.m209d(LOG_TAG, "## forwardPaginate() : a request is already in progress");
            return false;
        } else if (TextUtils.isEmpty(this.mForwardPaginationToken)) {
            Log.m209d(LOG_TAG, "## forwardPaginate() : there is no forward token");
            return false;
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("forwardPaginate");
            sb2.append(System.currentTimeMillis());
            this.mRequestKey = sb2.toString();
            launchPublicRoomsRequest(apiCallback);
            return true;
        }
    }

    public Integer getPublicRoomsCount() {
        return this.mPublicRoomsCount;
    }

    public void removeListener(PublicRoomsManagerListener publicRoomsManagerListener) {
        if (publicRoomsManagerListener != null) {
            this.mListeners.remove(publicRoomsManagerListener);
        }
    }

    public void refreshPublicRoomsCount(PublicRoomsManagerListener publicRoomsManagerListener) {
        if (this.mSession == null) {
            return;
        }
        if (!this.mCountRefreshInProgress) {
            this.mCountRefreshInProgress = true;
            if (publicRoomsManagerListener != null) {
                this.mListeners.add(publicRoomsManagerListener);
            }
            this.mSession.getEventsApiClient().getPublicRoomsCount(new SimpleApiCallback<Integer>() {
                public void onSuccess(Integer num) {
                    String access$100 = PublicRoomsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshPublicRoomsCount() : Got the rooms public list count : ");
                    sb.append(num);
                    Log.m209d(access$100, sb.toString());
                    PublicRoomsManager.this.mPublicRoomsCount = num;
                    for (PublicRoomsManagerListener onPublicRoomsCountRefresh : PublicRoomsManager.this.mListeners) {
                        onPublicRoomsCountRefresh.onPublicRoomsCountRefresh(PublicRoomsManager.this.mPublicRoomsCount);
                    }
                    PublicRoomsManager.this.mListeners.clear();
                    PublicRoomsManager.this.mCountRefreshInProgress = false;
                }

                public void onNetworkError(Exception exc) {
                    super.onNetworkError(exc);
                    PublicRoomsManager.this.mCountRefreshInProgress = false;
                    String access$100 = PublicRoomsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshPublicRoomsCount() : fails to retrieve the public room list ");
                    sb.append(exc.getLocalizedMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    super.onMatrixError(matrixError);
                    PublicRoomsManager.this.mCountRefreshInProgress = false;
                    String access$100 = PublicRoomsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshPublicRoomsCount() : fails to retrieve the public room list ");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    super.onUnexpectedError(exc);
                    PublicRoomsManager.this.mCountRefreshInProgress = false;
                    String access$100 = PublicRoomsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshPublicRoomsCount() : fails to retrieve the public room list ");
                    sb.append(exc.getLocalizedMessage());
                    Log.m211e(access$100, sb.toString());
                }
            });
        } else if (publicRoomsManagerListener != null) {
            this.mListeners.add(publicRoomsManagerListener);
        }
    }
}
