package com.opengarden.firechat.matrixsdk.crypto.algorithms.olm;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXEncrypting;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmSessionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MXOlmEncryption implements IMXEncrypting {
    /* access modifiers changed from: private */
    public MXCrypto mCrypto;
    /* access modifiers changed from: private */
    public String mRoomId;

    public void initWithMatrixSession(MXSession mXSession, String str) {
        this.mCrypto = mXSession.getCrypto();
        this.mRoomId = str;
    }

    /* access modifiers changed from: private */
    public List<MXDeviceInfo> getUserDevices(String str) {
        Map userDevices = this.mCrypto.getCryptoStore().getUserDevices(str);
        return userDevices != null ? new ArrayList(userDevices.values()) : new ArrayList();
    }

    public void encryptEventContent(JsonElement jsonElement, String str, List<String> list, ApiCallback<JsonElement> apiCallback) {
        final List<String> list2 = list;
        final String str2 = str;
        final JsonElement jsonElement2 = jsonElement;
        final ApiCallback<JsonElement> apiCallback2 = apiCallback;
        C25361 r0 = new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                ArrayList arrayList = new ArrayList();
                for (String access$000 : list2) {
                    List<MXDeviceInfo> access$0002 = MXOlmEncryption.this.getUserDevices(access$000);
                    if (access$0002 != null) {
                        for (MXDeviceInfo mXDeviceInfo : access$0002) {
                            if (!TextUtils.equals(mXDeviceInfo.identityKey(), MXOlmEncryption.this.mCrypto.getOlmDevice().getDeviceCurve25519Key()) && !mXDeviceInfo.isBlocked()) {
                                arrayList.add(mXDeviceInfo);
                            }
                        }
                    }
                }
                HashMap hashMap = new HashMap();
                hashMap.put("room_id", MXOlmEncryption.this.mRoomId);
                hashMap.put("type", str2);
                hashMap.put("content", jsonElement2);
                MXOlmEncryption.this.mCrypto.encryptMessage(hashMap, arrayList);
                apiCallback2.onSuccess(JsonUtils.getGson(false).toJsonTree(hashMap));
            }
        };
        ensureSession(list, r0);
    }

    private void ensureSession(final List<String> list, final ApiCallback<Void> apiCallback) {
        this.mCrypto.getDeviceList().downloadKeys(list, false, new SimpleApiCallback<MXUsersDevicesMap<MXDeviceInfo>>(apiCallback) {
            public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                MXOlmEncryption.this.mCrypto.ensureOlmSessionsForUsers(list, new SimpleApiCallback<MXUsersDevicesMap<MXOlmSessionResult>>(apiCallback) {
                    public void onSuccess(MXUsersDevicesMap<MXOlmSessionResult> mXUsersDevicesMap) {
                        if (apiCallback != null) {
                            apiCallback.onSuccess(null);
                        }
                    }
                });
            }
        });
    }
}
