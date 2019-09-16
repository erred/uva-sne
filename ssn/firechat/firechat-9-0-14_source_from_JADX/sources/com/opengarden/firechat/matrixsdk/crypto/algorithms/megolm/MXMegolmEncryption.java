package com.opengarden.firechat.matrixsdk.crypto.algorithms.megolm;

import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import com.google.gson.JsonElement;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoAlgorithms;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXEncrypting;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmSessionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXQueuedEncryption;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.offlineMessaging.LocalConnectionManager;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.matrix.olm.OlmAccount;

public class MXMegolmEncryption implements IMXEncrypting {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXMegolmEncryption";
    byte[] bytesArray = null;
    /* access modifiers changed from: private */
    public MXCrypto mCrypto;
    private String mDeviceId;
    private MXOutboundSessionInfo mOutboundSession;
    /* access modifiers changed from: private */
    public final ArrayList<MXQueuedEncryption> mPendingEncryptions = new ArrayList<>();
    /* access modifiers changed from: private */
    public String mRoomId;
    /* access modifiers changed from: private */
    public MXSession mSession;
    private int mSessionRotationPeriodMs;
    private int mSessionRotationPeriodMsgs;
    /* access modifiers changed from: private */
    public boolean mShareOperationIsProgress;

    public void initWithMatrixSession(MXSession mXSession, String str) {
        this.mSession = mXSession;
        this.mCrypto = mXSession.getCrypto();
        this.mRoomId = str;
        this.mDeviceId = mXSession.getCredentials().deviceId;
        this.mSessionRotationPeriodMsgs = 100;
        this.mSessionRotationPeriodMs = 604800000;
    }

    /* access modifiers changed from: private */
    public List<MXQueuedEncryption> getPendingEncryptions() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mPendingEncryptions) {
            arrayList.addAll(this.mPendingEncryptions);
        }
        return arrayList;
    }

    public void encryptEventContent(JsonElement jsonElement, String str, List<String> list, ApiCallback<JsonElement> apiCallback) {
        MXQueuedEncryption mXQueuedEncryption = new MXQueuedEncryption();
        mXQueuedEncryption.mEventContent = jsonElement;
        mXQueuedEncryption.mEventType = str;
        mXQueuedEncryption.mApiCallback = apiCallback;
        synchronized (this.mPendingEncryptions) {
            this.mPendingEncryptions.add(mXQueuedEncryption);
        }
        final long currentTimeMillis = System.currentTimeMillis();
        Log.m209d(LOG_TAG, "## encryptEventContent () starts");
        getDevicesInRoom(list, new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
            /* access modifiers changed from: private */
            public void dispatchNetworkError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptEventContent() : onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                List<MXQueuedEncryption> access$100 = MXMegolmEncryption.this.getPendingEncryptions();
                for (MXQueuedEncryption mXQueuedEncryption : access$100) {
                    mXQueuedEncryption.mApiCallback.onNetworkError(exc);
                }
                synchronized (MXMegolmEncryption.this.mPendingEncryptions) {
                    MXMegolmEncryption.this.mPendingEncryptions.removeAll(access$100);
                }
            }

            /* access modifiers changed from: private */
            public void dispatchMatrixError(MatrixError matrixError) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## encryptEventContent() : onMatrixError ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                List<MXQueuedEncryption> access$100 = MXMegolmEncryption.this.getPendingEncryptions();
                for (MXQueuedEncryption mXQueuedEncryption : access$100) {
                    mXQueuedEncryption.mApiCallback.onMatrixError(matrixError);
                }
                synchronized (MXMegolmEncryption.this.mPendingEncryptions) {
                    MXMegolmEncryption.this.mPendingEncryptions.removeAll(access$100);
                }
            }

            /* access modifiers changed from: private */
            public void dispatchUnexpectedError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onUnexpectedError() : onMatrixError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                List<MXQueuedEncryption> access$100 = MXMegolmEncryption.this.getPendingEncryptions();
                for (MXQueuedEncryption mXQueuedEncryption : access$100) {
                    mXQueuedEncryption.mApiCallback.onUnexpectedError(exc);
                }
                synchronized (MXMegolmEncryption.this.mPendingEncryptions) {
                    MXMegolmEncryption.this.mPendingEncryptions.removeAll(access$100);
                }
            }

            public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                MXMegolmEncryption.this.ensureOutboundSession(mXUsersDevicesMap, new ApiCallback<MXOutboundSessionInfo>() {
                    public void onSuccess(final MXOutboundSessionInfo mXOutboundSessionInfo) {
                        MXMegolmEncryption.this.mCrypto.getEncryptingThreadHandler().post(new Runnable() {
                            public void run() {
                                String access$000 = MXMegolmEncryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## encryptEventContent () processPendingEncryptions after ");
                                sb.append(System.currentTimeMillis() - currentTimeMillis);
                                sb.append("ms");
                                Log.m209d(access$000, sb.toString());
                                MXMegolmEncryption.this.processPendingEncryptions(mXOutboundSessionInfo);
                            }
                        });
                    }

                    public void onNetworkError(Exception exc) {
                        C25191.this.dispatchNetworkError(exc);
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        C25191.this.dispatchMatrixError(matrixError);
                    }

                    public void onUnexpectedError(Exception exc) {
                        C25191.this.dispatchUnexpectedError(exc);
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                dispatchNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                dispatchMatrixError(matrixError);
            }

            public void onUnexpectedError(Exception exc) {
                dispatchUnexpectedError(exc);
            }
        });
    }

    private MXOutboundSessionInfo prepareNewSessionInRoom() {
        MXOlmDevice olmDevice = this.mCrypto.getOlmDevice();
        String createOutboundGroupSession = olmDevice.createOutboundGroupSession();
        HashMap hashMap = new HashMap();
        hashMap.put(OlmAccount.JSON_KEY_FINGER_PRINT_KEY, olmDevice.getDeviceEd25519Key());
        olmDevice.addInboundGroupSession(createOutboundGroupSession, olmDevice.getSessionKey(createOutboundGroupSession), this.mRoomId, olmDevice.getDeviceCurve25519Key(), new ArrayList(), hashMap, false);
        return new MXOutboundSessionInfo(createOutboundGroupSession);
    }

    /* access modifiers changed from: private */
    public void ensureOutboundSession(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap, final ApiCallback<MXOutboundSessionInfo> apiCallback) {
        final MXOutboundSessionInfo mXOutboundSessionInfo = this.mOutboundSession;
        if (mXOutboundSessionInfo == null || mXOutboundSessionInfo.needsRotation(this.mSessionRotationPeriodMsgs, this.mSessionRotationPeriodMs) || mXOutboundSessionInfo.sharedWithTooManyDevices(mXUsersDevicesMap)) {
            mXOutboundSessionInfo = prepareNewSessionInRoom();
            this.mOutboundSession = mXOutboundSessionInfo;
        }
        if (this.mShareOperationIsProgress) {
            Log.m209d(LOG_TAG, "## ensureOutboundSessionInRoom() : already in progress");
            return;
        }
        HashMap hashMap = new HashMap();
        for (String str : mXUsersDevicesMap.getUserIds()) {
            for (String str2 : mXUsersDevicesMap.getUserDeviceIds(str)) {
                MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) mXUsersDevicesMap.getObject(str2, str);
                if (mXOutboundSessionInfo.mSharedWithDevices.getObject(str2, str) == null) {
                    if (!hashMap.containsKey(str)) {
                        hashMap.put(str, new ArrayList());
                    }
                    ((ArrayList) hashMap.get(str)).add(mXDeviceInfo);
                }
            }
        }
        shareKey(mXOutboundSessionInfo, hashMap, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                MXMegolmEncryption.this.mShareOperationIsProgress = false;
                if (apiCallback != null) {
                    apiCallback.onSuccess(mXOutboundSessionInfo);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOutboundSessionInRoom() : shareKey onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
                MXMegolmEncryption.this.mShareOperationIsProgress = false;
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOutboundSessionInRoom() : shareKey onMatrixError ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
                MXMegolmEncryption.this.mShareOperationIsProgress = false;
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## ensureOutboundSessionInRoom() : shareKey onUnexpectedError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
                MXMegolmEncryption.this.mShareOperationIsProgress = false;
            }
        });
    }

    /* access modifiers changed from: private */
    public void shareKey(MXOutboundSessionInfo mXOutboundSessionInfo, HashMap<String, ArrayList<MXDeviceInfo>> hashMap, final ApiCallback<Void> apiCallback) {
        if (hashMap.size() == 0) {
            Log.m209d(LOG_TAG, "## shareKey() : nothing more to do");
            if (apiCallback != null) {
                this.mCrypto.getUIHandler().post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(null);
                    }
                });
            }
            return;
        }
        HashMap hashMap2 = new HashMap();
        final ArrayList arrayList = new ArrayList();
        int i = 0;
        for (String str : hashMap.keySet()) {
            ArrayList arrayList2 = (ArrayList) hashMap.get(str);
            arrayList.add(str);
            hashMap2.put(str, arrayList2);
            i += arrayList2.size();
            if (i > 100) {
                break;
            }
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## shareKey() ; userId ");
        sb.append(arrayList);
        Log.m209d(str2, sb.toString());
        final HashMap<String, ArrayList<MXDeviceInfo>> hashMap3 = hashMap;
        final MXOutboundSessionInfo mXOutboundSessionInfo2 = mXOutboundSessionInfo;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C25244 r1 = new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                MXMegolmEncryption.this.mCrypto.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            hashMap3.remove((String) it.next());
                        }
                        MXMegolmEncryption.this.shareKey(mXOutboundSessionInfo2, hashMap3, apiCallback2);
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareKey() ; userIds ");
                sb.append(arrayList);
                sb.append(" failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareKey() ; userIds ");
                sb.append(arrayList);
                sb.append(" failed ");
                sb.append(matrixError.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareKey() ; userIds ");
                sb.append(arrayList);
                sb.append(" failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onUnexpectedError(exc);
                }
            }
        };
        shareUserDevicesKey(mXOutboundSessionInfo, hashMap2, r1);
    }

    private void shareUserDevicesKey(MXOutboundSessionInfo mXOutboundSessionInfo, HashMap<String, ArrayList<MXDeviceInfo>> hashMap, ApiCallback<Void> apiCallback) {
        String sessionKey = this.mCrypto.getOlmDevice().getSessionKey(mXOutboundSessionInfo.mSessionId);
        final int messageIndex = this.mCrypto.getOlmDevice().getMessageIndex(mXOutboundSessionInfo.mSessionId);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("algorithm", MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_MEGOLM);
        hashMap2.put("room_id", this.mRoomId);
        hashMap2.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, mXOutboundSessionInfo.mSessionId);
        hashMap2.put("session_key", sessionKey);
        hashMap2.put("chain_index", Integer.valueOf(messageIndex));
        final HashMap hashMap3 = new HashMap();
        hashMap3.put("type", Event.EVENT_TYPE_ROOM_KEY);
        hashMap3.put("content", hashMap2);
        final long currentTimeMillis = System.currentTimeMillis();
        Log.m209d(LOG_TAG, "## shareUserDevicesKey() : starts");
        MXCrypto mXCrypto = this.mCrypto;
        final HashMap<String, ArrayList<MXDeviceInfo>> hashMap4 = hashMap;
        final MXOutboundSessionInfo mXOutboundSessionInfo2 = mXOutboundSessionInfo;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C25265 r3 = new ApiCallback<MXUsersDevicesMap<MXOlmSessionResult>>() {
            public void onSuccess(final MXUsersDevicesMap<MXOlmSessionResult> mXUsersDevicesMap) {
                MXMegolmEncryption.this.mCrypto.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        String access$000 = MXMegolmEncryption.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## shareUserDevicesKey() : ensureOlmSessionsForDevices succeeds after ");
                        sb.append(System.currentTimeMillis() - currentTimeMillis);
                        sb.append(" ms");
                        Log.m209d(access$000, sb.toString());
                        MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
                        boolean z = false;
                        for (String str : mXUsersDevicesMap.getUserIds()) {
                            Iterator it = ((ArrayList) hashMap4.get(str)).iterator();
                            while (it.hasNext()) {
                                MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) it.next();
                                String str2 = mXDeviceInfo.deviceId;
                                if (!Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
                                    String access$0002 = MXMegolmEncryption.LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("## shareUserDevicesKey() : Sharing keys with device ");
                                    sb2.append(str);
                                    sb2.append(":");
                                    sb2.append(str2);
                                    Log.m209d(access$0002, sb2.toString());
                                    mXUsersDevicesMap.setObject(MXMegolmEncryption.this.mCrypto.encryptMessage(hashMap3, Arrays.asList(new MXDeviceInfo[]{mXDeviceInfo})), str, str2);
                                } else {
                                    MXOlmSessionResult mXOlmSessionResult = (MXOlmSessionResult) mXUsersDevicesMap.getObject(str2, str);
                                    if (!(mXOlmSessionResult == null || mXOlmSessionResult.mSessionId == null)) {
                                        String access$0003 = MXMegolmEncryption.LOG_TAG;
                                        StringBuilder sb3 = new StringBuilder();
                                        sb3.append("## shareUserDevicesKey() : Sharing keys with device ");
                                        sb3.append(str);
                                        sb3.append(":");
                                        sb3.append(str2);
                                        Log.m209d(access$0003, sb3.toString());
                                        mXUsersDevicesMap.setObject(MXMegolmEncryption.this.mCrypto.encryptMessage(hashMap3, Arrays.asList(new MXDeviceInfo[]{mXOlmSessionResult.mDevice})), str, str2);
                                    }
                                }
                                z = true;
                            }
                        }
                        C25281 r1 = new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                MXMegolmEncryption.this.mCrypto.getEncryptingThreadHandler().post(new Runnable() {
                                    public void run() {
                                        String access$000 = MXMegolmEncryption.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("## shareUserDevicesKey() : sendToDevice succeeds after ");
                                        sb.append(System.currentTimeMillis() - currentTimeMillis);
                                        sb.append(" ms");
                                        Log.m209d(access$000, sb.toString());
                                        for (String str : hashMap4.keySet()) {
                                            for (MXDeviceInfo mXDeviceInfo : (List) hashMap4.get(str)) {
                                                mXOutboundSessionInfo2.mSharedWithDevices.setObject(Integer.valueOf(messageIndex), str, mXDeviceInfo.deviceId);
                                            }
                                        }
                                        MXMegolmEncryption.this.mCrypto.getUIHandler().post(new Runnable() {
                                            public void run() {
                                                if (apiCallback2 != null) {
                                                    apiCallback2.onSuccess(null);
                                                }
                                            }
                                        });
                                    }
                                });
                            }

                            public void onNetworkError(Exception exc) {
                                String access$000 = MXMegolmEncryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareUserDevicesKey() : sendToDevice onNetworkError ");
                                sb.append(exc.getMessage());
                                Log.m209d(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onNetworkError(exc);
                                }
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$000 = MXMegolmEncryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareUserDevicesKey() : sendToDevice onMatrixError ");
                                sb.append(matrixError.getMessage());
                                Log.m209d(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onMatrixError(matrixError);
                                }
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$000 = MXMegolmEncryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareUserDevicesKey() : sendToDevice onUnexpectedError ");
                                sb.append(exc.getMessage());
                                Log.m209d(access$000, sb.toString());
                                if (apiCallback2 != null) {
                                    apiCallback2.onUnexpectedError(exc);
                                }
                            }
                        };
                        if (VectorApp.getInstance().offLineMessagePreference) {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("$");
                            sb4.append(System.currentTimeMillis());
                            sb4.append(MXMegolmEncryption.this.mSession.getMyUserId());
                            sb4.append(":serve2.firech.at");
                            String sb5 = sb4.toString();
                            Event event = new Event();
                            event.eventId = sb5;
                            event.setType(Event.EVENT_TYPE_ROOM_KEY);
                            event.content = JsonUtils.toJson(mXUsersDevicesMap);
                            event.roomId = MXMegolmEncryption.this.mRoomId;
                            event.setSender(MXMegolmEncryption.this.mSession.getMyUserId());
                            LocalConnectionManager.sendToPeers(event.eventId, event, MXMegolmEncryption.this.mSession.getCredentials().accessToken, 0);
                            if (!Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
                                r1.onSuccess(null);
                            }
                        }
                        if (!z || MXMegolmEncryption.this.mCrypto.hasBeenReleased()) {
                            Log.m209d(MXMegolmEncryption.LOG_TAG, "## shareUserDevicesKey() : no need to sharekey");
                            if (apiCallback2 != null) {
                                MXMegolmEncryption.this.mCrypto.getUIHandler().post(new Runnable() {
                                    public void run() {
                                        apiCallback2.onSuccess(null);
                                    }
                                });
                                return;
                            }
                            return;
                        }
                        System.currentTimeMillis();
                        Log.m209d(MXMegolmEncryption.LOG_TAG, "## shareUserDevicesKey() : has target");
                        MXMegolmEncryption.this.mSession.getCryptoRestClient().sendToDevice(Event.EVENT_TYPE_MESSAGE_ENCRYPTED, mXUsersDevicesMap, r1);
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareUserDevicesKey() : ensureOlmSessionsForDevices failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareUserDevicesKey() : ensureOlmSessionsForDevices failed ");
                sb.append(matrixError.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXMegolmEncryption.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## shareUserDevicesKey() : ensureOlmSessionsForDevices failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onUnexpectedError(exc);
                }
            }
        };
        mXCrypto.ensureOlmSessionsForDevices(hashMap, r3);
    }

    /* access modifiers changed from: private */
    public void processPendingEncryptions(MXOutboundSessionInfo mXOutboundSessionInfo) {
        if (mXOutboundSessionInfo != null) {
            List<MXQueuedEncryption> pendingEncryptions = getPendingEncryptions();
            for (final MXQueuedEncryption mXQueuedEncryption : pendingEncryptions) {
                HashMap hashMap = new HashMap();
                hashMap.put("room_id", this.mRoomId);
                hashMap.put("type", mXQueuedEncryption.mEventType);
                hashMap.put("content", mXQueuedEncryption.mEventContent);
                String encryptGroupMessage = this.mCrypto.getOlmDevice().encryptGroupMessage(mXOutboundSessionInfo.mSessionId, JsonUtils.convertToUTF8(JsonUtils.canonicalize(JsonUtils.getGson(false).toJsonTree(hashMap)).toString()));
                final HashMap hashMap2 = new HashMap();
                hashMap2.put("algorithm", MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_MEGOLM);
                hashMap2.put("sender_key", this.mCrypto.getOlmDevice().getDeviceCurve25519Key());
                hashMap2.put("ciphertext", encryptGroupMessage);
                hashMap2.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, mXOutboundSessionInfo.mSessionId);
                hashMap2.put(AmplitudeClient.DEVICE_ID_KEY, this.mDeviceId);
                this.mCrypto.getUIHandler().post(new Runnable() {
                    public void run() {
                        mXQueuedEncryption.mApiCallback.onSuccess(JsonUtils.getGson(false).toJsonTree(hashMap2));
                    }
                });
                mXOutboundSessionInfo.mUseCount++;
            }
            synchronized (this.mPendingEncryptions) {
                this.mPendingEncryptions.removeAll(pendingEncryptions);
            }
        }
    }

    private void getDevicesInRoom(List<String> list, final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback) {
        this.mCrypto.getDeviceList().downloadKeys(list, false, new SimpleApiCallback<MXUsersDevicesMap<MXDeviceInfo>>(apiCallback) {
            public void onSuccess(final MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                MXMegolmEncryption.this.mCrypto.getEncryptingThreadHandler().post(new Runnable() {
                    public void run() {
                        boolean z = MXMegolmEncryption.this.mCrypto.getGlobalBlacklistUnverifiedDevices() || MXMegolmEncryption.this.mCrypto.isRoomBlacklistUnverifiedDevices(MXMegolmEncryption.this.mRoomId);
                        final MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
                        final MXUsersDevicesMap mXUsersDevicesMap2 = new MXUsersDevicesMap();
                        for (String str : mXUsersDevicesMap.getUserIds()) {
                            for (String str2 : mXUsersDevicesMap.getUserDeviceIds(str)) {
                                MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) mXUsersDevicesMap.getObject(str2, str);
                                if (MXMegolmEncryption.this.mCrypto.warnOnUnknownDevices() && mXDeviceInfo.isUnknown()) {
                                    mXUsersDevicesMap2.setObject(mXDeviceInfo, str, str2);
                                } else if (!mXDeviceInfo.isBlocked() && ((mXDeviceInfo.isVerified() || !z) && !TextUtils.equals(mXDeviceInfo.identityKey(), MXMegolmEncryption.this.mCrypto.getOlmDevice().getDeviceCurve25519Key()))) {
                                    mXUsersDevicesMap.setObject(mXDeviceInfo, str, str2);
                                }
                            }
                        }
                        MXMegolmEncryption.this.mCrypto.getUIHandler().post(new Runnable() {
                            public void run() {
                                if (mXUsersDevicesMap2.getMap().size() != 0) {
                                    apiCallback.onMatrixError(new MXCryptoError(MXCryptoError.UNKNOWN_DEVICES_CODE, MXCryptoError.UNABLE_TO_ENCRYPT, MXCryptoError.UNKNOWN_DEVICES_REASON, mXUsersDevicesMap2));
                                } else {
                                    apiCallback.onSuccess(mXUsersDevicesMap);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
