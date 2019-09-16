package com.opengarden.firechat.matrixsdk.rest.client;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.RestClient.EndPointServer;
import com.opengarden.firechat.matrixsdk.crypto.data.MXKey;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.api.CryptoApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeyChangesResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysClaimResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysQueryResponse;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysUploadResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.DeleteDeviceParams;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DevicesListResponse;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import retrofit2.C3224Response;
import retrofit2.Call;

public class CryptoRestClient extends RestClient<CryptoApi> {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "CryptoRestClient";

    public CryptoRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, CryptoApi.class, RestClient.URI_API_PREFIX_PATH_UNSTABLE, false, false);
    }

    public CryptoRestClient(HomeServerConnectionConfig homeServerConnectionConfig, Boolean bool) {
        super(homeServerConnectionConfig, CryptoApi.class, RestClient.URI_API_PREFIX_PATH_UNSTABLE, false, EndPointServer.HOME_SERVER, false);
    }

    public void uploadKeys(Map<String, Object> map, Map<String, Object> map2, String str, ApiCallback<KeysUploadResponse> apiCallback) {
        String convertToUTF8 = JsonUtils.convertToUTF8(str);
        HashMap hashMap = new HashMap();
        if (map != null) {
            hashMap.put("device_keys", map);
        }
        if (map2 != null) {
            hashMap.put("one_time_keys", map2);
        }
        if (!TextUtils.isEmpty(convertToUTF8)) {
            Call uploadKeys = ((CryptoApi) this.mApi).uploadKeys(convertToUTF8, hashMap);
            final Map<String, Object> map3 = map;
            final Map<String, Object> map4 = map2;
            final String str2 = str;
            final ApiCallback<KeysUploadResponse> apiCallback2 = apiCallback;
            C27181 r4 = new RequestRetryCallBack() {
                public void onRetry() {
                    CryptoRestClient.this.uploadKeys(map3, map4, str2, apiCallback2);
                }
            };
            uploadKeys.enqueue(new RestAdapterCallback("uploadKeys", null, apiCallback, r4));
            return;
        }
        Call uploadKeys2 = ((CryptoApi) this.mApi).uploadKeys(hashMap);
        final Map<String, Object> map5 = map;
        final Map<String, Object> map6 = map2;
        final String str3 = str;
        final ApiCallback<KeysUploadResponse> apiCallback3 = apiCallback;
        C27212 r42 = new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.uploadKeys(map5, map6, str3, apiCallback3);
            }
        };
        uploadKeys2.enqueue(new RestAdapterCallback("uploadKeys", null, apiCallback, r42));
    }

    public void downloadKeysForUsers(final List<String> list, final String str, final ApiCallback<KeysQueryResponse> apiCallback) {
        HashMap hashMap = new HashMap();
        if (list != null) {
            for (String put : list) {
                hashMap.put(put, new HashMap());
            }
        }
        HashMap hashMap2 = new HashMap();
        hashMap2.put("device_keys", hashMap);
        if (!TextUtils.isEmpty(str)) {
            hashMap2.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_TOKEN, str);
        }
        ((CryptoApi) this.mApi).downloadKeysForUsers(hashMap2).enqueue(new RestAdapterCallback("downloadKeysForUsers", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.downloadKeysForUsers(list, str, apiCallback);
            }
        }));
    }

    public void claimOneTimeKeysForUsersDevices(final MXUsersDevicesMap<String> mXUsersDevicesMap, final ApiCallback<MXUsersDevicesMap<MXKey>> apiCallback) {
        HashMap hashMap = new HashMap();
        hashMap.put("one_time_keys", mXUsersDevicesMap.getMap());
        Call claimOneTimeKeysForUsersDevices = ((CryptoApi) this.mApi).claimOneTimeKeysForUsersDevices(hashMap);
        final ApiCallback<MXUsersDevicesMap<MXKey>> apiCallback2 = apiCallback;
        C27245 r1 = new RestAdapterCallback<KeysClaimResponse>("claimOneTimeKeysForUsersDevices", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.claimOneTimeKeysForUsersDevices(mXUsersDevicesMap, apiCallback);
            }
        }) {
            public void success(KeysClaimResponse keysClaimResponse, C3224Response response) {
                onEventSent();
                HashMap hashMap = new HashMap();
                if (keysClaimResponse.oneTimeKeys != null) {
                    for (String str : keysClaimResponse.oneTimeKeys.keySet()) {
                        Map map = (Map) keysClaimResponse.oneTimeKeys.get(str);
                        HashMap hashMap2 = new HashMap();
                        for (String str2 : map.keySet()) {
                            try {
                                hashMap2.put(str2, new MXKey((Map) map.get(str2)));
                            } catch (Exception e) {
                                String access$000 = CryptoRestClient.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## claimOneTimeKeysForUsersDevices : fail to create a MXKey ");
                                sb.append(e.getMessage());
                                Log.m211e(access$000, sb.toString());
                            }
                        }
                        if (hashMap2.size() != 0) {
                            hashMap.put(str, hashMap2);
                        }
                    }
                }
                apiCallback2.onSuccess(new MXUsersDevicesMap(hashMap));
            }
        };
        claimOneTimeKeysForUsersDevices.enqueue(r1);
    }

    public void sendToDevice(String str, MXUsersDevicesMap<Map<String, Object>> mXUsersDevicesMap, ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Random().nextInt(Integer.MAX_VALUE));
        sb.append("");
        sendToDevice(str, mXUsersDevicesMap, sb.toString(), apiCallback);
    }

    public void sendToDevice(final String str, final MXUsersDevicesMap<Map<String, Object>> mXUsersDevicesMap, String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("sendToDevice ");
        sb.append(str);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        hashMap.put("messages", mXUsersDevicesMap.getMap());
        ((CryptoApi) this.mApi).sendToDevice(str, str2, hashMap).enqueue(new RestAdapterCallback(sb2, null, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.sendToDevice(str, mXUsersDevicesMap, apiCallback);
            }
        }));
    }

    public void forwardToDevice(String str, JsonObject jsonObject, String str2, String str3, ApiCallback<Void> apiCallback) {
        final String str4 = str;
        StringBuilder sb = new StringBuilder();
        sb.append("sendToDevice ");
        sb.append(str4);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        JsonObject jsonObject2 = jsonObject;
        hashMap.put("messages", JsonUtils.getGson(false).fromJson((JsonElement) jsonObject2, HashMap.class));
        final String str5 = str2;
        String str6 = str3;
        Call forwardToDevice = ((CryptoApi) this.mApi).forwardToDevice(str4, str5, hashMap, str6);
        final JsonObject jsonObject3 = jsonObject2;
        final String str7 = str6;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C27267 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.forwardToDevice(str4, jsonObject3, str5, str7, apiCallback2);
            }
        };
        forwardToDevice.enqueue(new RestAdapterCallback(sb2, null, apiCallback, r0));
    }

    public void getDevices(final ApiCallback<DevicesListResponse> apiCallback) {
        ((CryptoApi) this.mApi).getDevices().enqueue(new RestAdapterCallback("getDevicesListInfo", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.getDevices(apiCallback);
            }
        }));
    }

    public void deleteDevice(final String str, final DeleteDeviceParams deleteDeviceParams, final ApiCallback<Void> apiCallback) {
        ((CryptoApi) this.mApi).deleteDevice(str, deleteDeviceParams).enqueue(new RestAdapterCallback("deleteDevice", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.deleteDevice(str, deleteDeviceParams, apiCallback);
            }
        }));
    }

    public void setDeviceName(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        HashMap hashMap = new HashMap();
        hashMap.put("display_name", TextUtils.isEmpty(str2) ? "" : str2);
        ((CryptoApi) this.mApi).updateDeviceInfo(str, hashMap).enqueue(new RestAdapterCallback("setDeviceName", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.setDeviceName(str, str2, apiCallback);
            }
        }));
    }

    public void getKeyChanges(final String str, final String str2, final ApiCallback<KeyChangesResponse> apiCallback) {
        ((CryptoApi) this.mApi).getKeyChanges(str, str2).enqueue(new RestAdapterCallback("getKeyChanges", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                CryptoRestClient.this.getKeyChanges(str, str2, apiCallback);
            }
        }));
    }
}
