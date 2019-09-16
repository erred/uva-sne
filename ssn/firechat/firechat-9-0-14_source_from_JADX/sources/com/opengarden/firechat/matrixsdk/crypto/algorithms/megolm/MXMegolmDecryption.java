package com.opengarden.firechat.matrixsdk.crypto.algorithms.megolm;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.MXDecryptionException;
import com.opengarden.firechat.matrixsdk.crypto.MXEventDecryptionResult;
import com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXDecrypting;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.MXDecryptionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmSessionResult;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.ForwardedRoomKeyContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequestBody;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.matrix.olm.OlmAccount;

public class MXMegolmDecryption implements IMXDecrypting {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXMegolmDecryption";
    private MXOlmDevice mOlmDevice;
    private HashMap<String, HashMap<String, ArrayList<Event>>> mPendingEvents;
    /* access modifiers changed from: private */
    public MXSession mSession;

    public void initWithMatrixSession(MXSession mXSession) {
        this.mSession = mXSession;
        this.mOlmDevice = mXSession.getCrypto().getOlmDevice();
        this.mPendingEvents = new HashMap<>();
    }

    public MXEventDecryptionResult decryptEvent(Event event, String str) throws MXDecryptionException {
        return decryptEvent(event, str, true);
    }

    private MXEventDecryptionResult decryptEvent(Event event, String str, boolean z) throws MXDecryptionException {
        MXCryptoError mXCryptoError;
        MXDecryptionResult mXDecryptionResult;
        MXEventDecryptionResult mXEventDecryptionResult = null;
        if (event == null) {
            Log.m211e(LOG_TAG, "## decryptEvent() : null event");
            return null;
        }
        EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent().getAsJsonObject());
        String str2 = encryptedEventContent.sender_key;
        String str3 = encryptedEventContent.ciphertext;
        String str4 = encryptedEventContent.session_id;
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str4) || TextUtils.isEmpty(str3)) {
            throw new MXDecryptionException(new MXCryptoError(MXCryptoError.MISSING_FIELDS_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.MISSING_FIELDS_REASON));
        }
        try {
            mXDecryptionResult = this.mOlmDevice.decryptGroupMessage(str3, event.roomId, str, str4, str2);
            mXCryptoError = null;
        } catch (MXDecryptionException e) {
            mXCryptoError = e.getCryptoError();
            mXDecryptionResult = null;
        }
        if (mXDecryptionResult != null && mXDecryptionResult.mPayload != null && mXCryptoError == null) {
            mXEventDecryptionResult = new MXEventDecryptionResult();
            mXEventDecryptionResult.mClearEvent = mXDecryptionResult.mPayload;
            mXEventDecryptionResult.mSenderCurve25519Key = mXDecryptionResult.mSenderKey;
            if (mXDecryptionResult.mKeysClaimed != null) {
                mXEventDecryptionResult.mClaimedEd25519Key = (String) mXDecryptionResult.mKeysClaimed.get(OlmAccount.JSON_KEY_FINGER_PRINT_KEY);
            }
            mXEventDecryptionResult.mForwardingCurve25519KeyChain = mXDecryptionResult.mForwardingCurve25519KeyChain;
        } else if (mXCryptoError != null) {
            if (mXCryptoError.isOlmError()) {
                if (TextUtils.equals("UNKNOWN_MESSAGE_INDEX", mXCryptoError.error)) {
                    addEventToPendingList(event, str);
                    if (z) {
                        requestKeysForEvent(event);
                    }
                }
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.OLM_ERROR_CODE, String.format(MXCryptoError.OLM_REASON, new Object[]{mXCryptoError.error}), String.format(MXCryptoError.DETAILLED_OLM_REASON, new Object[]{str3, mXCryptoError.error})));
            }
            if (TextUtils.equals(mXCryptoError.errcode, MXCryptoError.UNKNOWN_INBOUND_SESSION_ID_ERROR_CODE)) {
                addEventToPendingList(event, str);
                if (z) {
                    requestKeysForEvent(event);
                }
            }
            throw new MXDecryptionException(mXCryptoError);
        }
        return mXEventDecryptionResult;
    }

    private void requestKeysForEvent(Event event) {
        String sender = event.getSender();
        EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent());
        ArrayList arrayList = new ArrayList();
        HashMap hashMap = new HashMap();
        hashMap.put("userId", this.mSession.getMyUserId());
        hashMap.put("deviceId", "*");
        arrayList.add(hashMap);
        if (!TextUtils.equals(sender, this.mSession.getMyUserId())) {
            HashMap hashMap2 = new HashMap();
            hashMap2.put("userId", sender);
            hashMap2.put("deviceId", encryptedEventContent.device_id);
            arrayList.add(hashMap2);
        }
        HashMap hashMap3 = new HashMap();
        hashMap3.put("room_id", event.roomId);
        hashMap3.put("algorithm", encryptedEventContent.algorithm);
        hashMap3.put("sender_key", encryptedEventContent.sender_key);
        hashMap3.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, encryptedEventContent.session_id);
        this.mSession.getCrypto().requestRoomKey(hashMap3, arrayList);
    }

    private void addEventToPendingList(Event event, String str) {
        EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent().getAsJsonObject());
        String str2 = encryptedEventContent.sender_key;
        String str3 = encryptedEventContent.session_id;
        StringBuilder sb = new StringBuilder();
        sb.append(str2);
        sb.append("|");
        sb.append(str3);
        String sb2 = sb.toString();
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        if (!this.mPendingEvents.containsKey(sb2)) {
            this.mPendingEvents.put(sb2, new HashMap());
        }
        if (!((HashMap) this.mPendingEvents.get(sb2)).containsKey(str)) {
            ((HashMap) this.mPendingEvents.get(sb2)).put(str, new ArrayList());
        }
        if (((ArrayList) ((HashMap) this.mPendingEvents.get(sb2)).get(str)).indexOf(event) < 0) {
            String str4 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## addEventToPendingList() : add Event ");
            sb3.append(event.eventId);
            sb3.append(" in room id ");
            sb3.append(event.roomId);
            Log.m209d(str4, sb3.toString());
            ((ArrayList) ((HashMap) this.mPendingEvents.get(sb2)).get(str)).add(event);
        }
    }

    public void onRoomKeyEvent(Event event) {
        String str;
        boolean z;
        Map map;
        List list;
        ArrayList arrayList;
        RoomKeyContent roomKeyContent = JsonUtils.toRoomKeyContent(event.getContentAsJsonObject());
        String str2 = roomKeyContent.room_id;
        String str3 = roomKeyContent.session_id;
        String str4 = roomKeyContent.session_key;
        String senderKey = event.senderKey();
        Map hashMap = new HashMap();
        if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3) || TextUtils.isEmpty(str4)) {
            Log.m211e(LOG_TAG, "## onRoomKeyEvent() :  Key event is missing fields");
            return;
        }
        if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_FORWARDED_ROOM_KEY)) {
            String str5 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRoomKeyEvent(), forward adding key : roomId ");
            sb.append(str2);
            sb.append(" sessionId ");
            sb.append(str3);
            sb.append(" sessionKey ");
            sb.append(str4);
            Log.m209d(str5, sb.toString());
            ForwardedRoomKeyContent forwardedRoomKeyContent = JsonUtils.toForwardedRoomKeyContent(event.getContentAsJsonObject());
            if (forwardedRoomKeyContent.forwarding_curve25519_key_chain == null) {
                arrayList = new ArrayList();
            } else {
                arrayList = new ArrayList(forwardedRoomKeyContent.forwarding_curve25519_key_chain);
            }
            arrayList.add(senderKey);
            String str6 = forwardedRoomKeyContent.sender_key;
            if (str6 == null) {
                Log.m211e(LOG_TAG, "## onRoomKeyEvent() : forwarded_room_key event is missing sender_key field");
                return;
            }
            String str7 = forwardedRoomKeyContent.sender_claimed_ed25519_key;
            if (str7 == null) {
                Log.m211e(LOG_TAG, "## forwarded_room_key_event is missing sender_claimed_ed25519_key field");
                return;
            }
            hashMap.put(OlmAccount.JSON_KEY_FINGER_PRINT_KEY, str7);
            map = hashMap;
            str = str6;
            z = true;
            list = arrayList;
        } else {
            String str8 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## onRoomKeyEvent(), Adding key : roomId ");
            sb2.append(str2);
            sb2.append(" sessionId ");
            sb2.append(str3);
            sb2.append(" sessionKey ");
            sb2.append(str4);
            Log.m209d(str8, sb2.toString());
            if (senderKey == null) {
                Log.m211e(LOG_TAG, "## onRoomKeyEvent() : key event has no sender key (not encrypted?)");
                return;
            }
            map = event.getKeysClaimed();
            str = senderKey;
            list = null;
            z = false;
        }
        this.mOlmDevice.addInboundGroupSession(str3, str4, str2, str, list, map, z);
        HashMap hashMap2 = new HashMap();
        hashMap2.put("algorithm", roomKeyContent.algorithm);
        hashMap2.put("room_id", roomKeyContent.room_id);
        hashMap2.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID, roomKeyContent.session_id);
        hashMap2.put("sender_key", str);
        this.mSession.getCrypto().cancelRoomKeyRequest(hashMap2);
        onNewSession(str, str3);
    }

    public void onNewSession(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("|");
        sb.append(str2);
        String sb2 = sb.toString();
        HashMap hashMap = (HashMap) this.mPendingEvents.get(sb2);
        if (hashMap != null) {
            this.mPendingEvents.remove(sb2);
            for (String str3 : hashMap.keySet()) {
                Iterator it = ((ArrayList) hashMap.get(str3)).iterator();
                while (it.hasNext()) {
                    final Event event = (Event) it.next();
                    final MXEventDecryptionResult mXEventDecryptionResult = null;
                    try {
                        mXEventDecryptionResult = decryptEvent(event, TextUtils.isEmpty(str3) ? null : str3);
                    } catch (MXDecryptionException e) {
                        String str4 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## onNewSession() : Still can't decrypt ");
                        sb3.append(event.eventId);
                        sb3.append(". Error ");
                        sb3.append(e.getMessage());
                        Log.m211e(str4, sb3.toString());
                        event.setCryptoError(e.getCryptoError());
                    }
                    if (mXEventDecryptionResult != null) {
                        this.mSession.getCrypto().getUIHandler().post(new Runnable() {
                            public void run() {
                                event.setClearData(mXEventDecryptionResult);
                                MXMegolmDecryption.this.mSession.getDataHandler().onEventDecrypted(event);
                            }
                        });
                        String str5 = LOG_TAG;
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("## onNewSession() : successful re-decryption of ");
                        sb4.append(event.eventId);
                        Log.m209d(str5, sb4.toString());
                    }
                }
            }
        }
    }

    public boolean hasKeysForKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        return (incomingRoomKeyRequest == null || incomingRoomKeyRequest.mRequestBody == null || !this.mOlmDevice.hasInboundSessionKeys(incomingRoomKeyRequest.mRequestBody.room_id, incomingRoomKeyRequest.mRequestBody.sender_key, incomingRoomKeyRequest.mRequestBody.session_id)) ? false : true;
    }

    public void shareKeysWithDevice(final IncomingRoomKeyRequest incomingRoomKeyRequest) {
        if (incomingRoomKeyRequest != null && incomingRoomKeyRequest.mRequestBody != null) {
            final String str = incomingRoomKeyRequest.mUserId;
            this.mSession.getCrypto().getDeviceList().downloadKeys(Arrays.asList(new String[]{str}), false, new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
                public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                    final String str = incomingRoomKeyRequest.mDeviceId;
                    final MXDeviceInfo userDevice = MXMegolmDecryption.this.mSession.getCrypto().mCryptoStore.getUserDevice(str, str);
                    if (userDevice != null) {
                        final RoomKeyRequestBody roomKeyRequestBody = incomingRoomKeyRequest.mRequestBody;
                        HashMap hashMap = new HashMap();
                        hashMap.put(str, new ArrayList(Arrays.asList(new MXDeviceInfo[]{userDevice})));
                        MXMegolmDecryption.this.mSession.getCrypto().ensureOlmSessionsForDevices(hashMap, new ApiCallback<MXUsersDevicesMap<MXOlmSessionResult>>() {
                            public void onSuccess(MXUsersDevicesMap<MXOlmSessionResult> mXUsersDevicesMap) {
                                MXOlmSessionResult mXOlmSessionResult = (MXOlmSessionResult) mXUsersDevicesMap.getObject(str, str);
                                if (mXOlmSessionResult != null && mXOlmSessionResult.mSessionId != null) {
                                    String access$100 = MXMegolmDecryption.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("## shareKeysWithDevice() : sharing keys for session ");
                                    sb.append(roomKeyRequestBody.sender_key);
                                    sb.append("|");
                                    sb.append(roomKeyRequestBody.session_id);
                                    sb.append(" with device ");
                                    sb.append(str);
                                    sb.append(":");
                                    sb.append(str);
                                    Log.m209d(access$100, sb.toString());
                                    MXOlmInboundGroupSession2 inboundGroupSession = MXMegolmDecryption.this.mSession.getCrypto().getOlmDevice().getInboundGroupSession(roomKeyRequestBody.session_id, roomKeyRequestBody.sender_key, roomKeyRequestBody.room_id);
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("type", Event.EVENT_TYPE_FORWARDED_ROOM_KEY);
                                    hashMap.put("content", inboundGroupSession.exportKeys());
                                    Map encryptMessage = MXMegolmDecryption.this.mSession.getCrypto().encryptMessage(hashMap, Arrays.asList(new MXDeviceInfo[]{userDevice}));
                                    MXUsersDevicesMap mXUsersDevicesMap2 = new MXUsersDevicesMap();
                                    mXUsersDevicesMap2.setObject(encryptMessage, str, str);
                                    String access$1002 = MXMegolmDecryption.LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("## shareKeysWithDevice() : sending to ");
                                    sb2.append(str);
                                    sb2.append(":");
                                    sb2.append(str);
                                    Log.m209d(access$1002, sb2.toString());
                                    MXMegolmDecryption.this.mSession.getCryptoRestClient().sendToDevice(Event.EVENT_TYPE_MESSAGE_ENCRYPTED, mXUsersDevicesMap2, new ApiCallback<Void>() {
                                        public void onSuccess(Void voidR) {
                                            String access$100 = MXMegolmDecryption.LOG_TAG;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("## shareKeysWithDevice() : sent to ");
                                            sb.append(str);
                                            sb.append(":");
                                            sb.append(str);
                                            Log.m209d(access$100, sb.toString());
                                        }

                                        public void onNetworkError(Exception exc) {
                                            String access$100 = MXMegolmDecryption.LOG_TAG;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("## shareKeysWithDevice() : sendToDevice ");
                                            sb.append(str);
                                            sb.append(":");
                                            sb.append(str);
                                            sb.append(" failed ");
                                            sb.append(exc.getMessage());
                                            Log.m211e(access$100, sb.toString());
                                        }

                                        public void onMatrixError(MatrixError matrixError) {
                                            String access$100 = MXMegolmDecryption.LOG_TAG;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("## shareKeysWithDevice() : sendToDevice ");
                                            sb.append(str);
                                            sb.append(":");
                                            sb.append(str);
                                            sb.append(" failed ");
                                            sb.append(matrixError.getMessage());
                                            Log.m211e(access$100, sb.toString());
                                        }

                                        public void onUnexpectedError(Exception exc) {
                                            String access$100 = MXMegolmDecryption.LOG_TAG;
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("## shareKeysWithDevice() : sendToDevice ");
                                            sb.append(str);
                                            sb.append(":");
                                            sb.append(str);
                                            sb.append(" failed ");
                                            sb.append(exc.getMessage());
                                            Log.m211e(access$100, sb.toString());
                                        }
                                    });
                                }
                            }

                            public void onNetworkError(Exception exc) {
                                String access$100 = MXMegolmDecryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareKeysWithDevice() : ensureOlmSessionsForDevices ");
                                sb.append(str);
                                sb.append(":");
                                sb.append(str);
                                sb.append(" failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$100 = MXMegolmDecryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareKeysWithDevice() : ensureOlmSessionsForDevices ");
                                sb.append(str);
                                sb.append(":");
                                sb.append(str);
                                sb.append(" failed ");
                                sb.append(matrixError.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$100 = MXMegolmDecryption.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## shareKeysWithDevice() : ensureOlmSessionsForDevices ");
                                sb.append(str);
                                sb.append(":");
                                sb.append(str);
                                sb.append(" failed ");
                                sb.append(exc.getMessage());
                                Log.m211e(access$100, sb.toString());
                            }
                        });
                        return;
                    }
                    String access$100 = MXMegolmDecryption.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shareKeysWithDevice() : ensureOlmSessionsForDevices ");
                    sb.append(str);
                    sb.append(":");
                    sb.append(str);
                    sb.append(" not found");
                    Log.m211e(access$100, sb.toString());
                }

                public void onNetworkError(Exception exc) {
                    String access$100 = MXMegolmDecryption.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shareKeysWithDevice() : downloadKeys ");
                    sb.append(str);
                    sb.append(" failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$100 = MXMegolmDecryption.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shareKeysWithDevice() : downloadKeys ");
                    sb.append(str);
                    sb.append(" failed ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$100 = MXMegolmDecryption.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## shareKeysWithDevice() : downloadKeys ");
                    sb.append(str);
                    sb.append(" failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
            });
        }
    }
}
