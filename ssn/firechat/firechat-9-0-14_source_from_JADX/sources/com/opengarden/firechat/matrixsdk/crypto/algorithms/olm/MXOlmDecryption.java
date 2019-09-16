package com.opengarden.firechat.matrixsdk.crypto.algorithms.olm;

import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.MXDecryptionException;
import com.opengarden.firechat.matrixsdk.crypto.MXEventDecryptionResult;
import com.opengarden.firechat.matrixsdk.crypto.MXOlmDevice;
import com.opengarden.firechat.matrixsdk.crypto.algorithms.IMXDecrypting;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.OlmEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.OlmPayloadContent;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.matrix.olm.OlmAccount;

public class MXOlmDecryption implements IMXDecrypting {
    private static final String LOG_TAG = "MXOlmDecryption";
    private MXOlmDevice mOlmDevice;
    private MXSession mSession;

    public boolean hasKeysForKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        return false;
    }

    public void onNewSession(String str, String str2) {
    }

    public void onRoomKeyEvent(Event event) {
    }

    public void shareKeysWithDevice(IncomingRoomKeyRequest incomingRoomKeyRequest) {
    }

    public void initWithMatrixSession(MXSession mXSession) {
        this.mSession = mXSession;
        this.mOlmDevice = mXSession.getCrypto().getOlmDevice();
    }

    public MXEventDecryptionResult decryptEvent(Event event, String str) throws MXDecryptionException {
        if (event == null) {
            Log.m211e(LOG_TAG, "## decryptEvent() : null event");
            return null;
        }
        OlmEventContent olmEventContent = JsonUtils.toOlmEventContent(event.getWireContent().getAsJsonObject());
        String str2 = olmEventContent.sender_key;
        Map<String, Object> map = olmEventContent.ciphertext;
        if (map == null) {
            Log.m211e(LOG_TAG, "## decryptEvent() : missing cipher text");
            throw new MXDecryptionException(new MXCryptoError(MXCryptoError.MISSING_CIPHER_TEXT_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.MISSING_CIPHER_TEXT_REASON));
        } else if (!map.containsKey(this.mOlmDevice.getDeviceCurve25519Key())) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## decryptEvent() : our device ");
            sb.append(this.mOlmDevice.getDeviceCurve25519Key());
            sb.append(" is not included in recipients. Event ");
            sb.append(event.getContentAsJsonObject());
            Log.m211e(str3, sb.toString());
            throw new MXDecryptionException(new MXCryptoError(MXCryptoError.NOT_INCLUDE_IN_RECIPIENTS_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.NOT_INCLUDED_IN_RECIPIENT_REASON));
        } else {
            String decryptMessage = decryptMessage((Map) map.get(this.mOlmDevice.getDeviceCurve25519Key()), str2);
            if (decryptMessage == null) {
                String str4 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## decryptEvent() Failed to decrypt Olm event (id= ");
                sb2.append(event.eventId);
                sb2.append(" ) from ");
                sb2.append(str2);
                Log.m211e(str4, sb2.toString());
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.BAD_ENCRYPTED_MESSAGE_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.BAD_ENCRYPTED_MESSAGE_REASON));
            }
            JsonElement parse = new JsonParser().parse(JsonUtils.convertFromUTF8(decryptMessage));
            if (parse == null) {
                Log.m211e(LOG_TAG, "## decryptEvent failed : null payload");
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.UNABLE_TO_DECRYPT_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.MISSING_CIPHER_TEXT_REASON));
            }
            OlmPayloadContent olmPayloadContent = JsonUtils.toOlmPayloadContent(parse);
            if (TextUtils.isEmpty(olmPayloadContent.recipient)) {
                String format = String.format(MXCryptoError.ERROR_MISSING_PROPERTY_REASON, new Object[]{"recipient"});
                String str5 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## decryptEvent() : ");
                sb3.append(format);
                Log.m211e(str5, sb3.toString());
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.MISSING_PROPERTY_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, format));
            } else if (!TextUtils.equals(olmPayloadContent.recipient, this.mSession.getMyUserId())) {
                String str6 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("## decryptEvent() : Event ");
                sb4.append(event.eventId);
                sb4.append(": Intended recipient ");
                sb4.append(olmPayloadContent.recipient);
                sb4.append(" does not match our id ");
                sb4.append(this.mSession.getMyUserId());
                Log.m211e(str6, sb4.toString());
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.BAD_RECIPIENT_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, String.format(MXCryptoError.BAD_RECIPIENT_REASON, new Object[]{olmPayloadContent.recipient})));
            } else if (olmPayloadContent.recipient_keys == null) {
                String str7 = LOG_TAG;
                StringBuilder sb5 = new StringBuilder();
                sb5.append("## decryptEvent() : Olm event (id=");
                sb5.append(event.eventId);
                sb5.append(") contains no 'recipient_keys' property; cannot prevent unknown-key attack");
                Log.m211e(str7, sb5.toString());
                throw new MXDecryptionException(new MXCryptoError(MXCryptoError.MISSING_PROPERTY_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, String.format(MXCryptoError.ERROR_MISSING_PROPERTY_REASON, new Object[]{"recipient_keys"})));
            } else {
                String str8 = (String) olmPayloadContent.recipient_keys.get(OlmAccount.JSON_KEY_FINGER_PRINT_KEY);
                if (!TextUtils.equals(str8, this.mOlmDevice.getDeviceEd25519Key())) {
                    String str9 = LOG_TAG;
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append("## decryptEvent() : Event ");
                    sb6.append(event.eventId);
                    sb6.append(": Intended recipient ed25519 key ");
                    sb6.append(str8);
                    sb6.append(" did not match ours");
                    Log.m211e(str9, sb6.toString());
                    throw new MXDecryptionException(new MXCryptoError(MXCryptoError.BAD_RECIPIENT_KEY_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.BAD_RECIPIENT_KEY_REASON));
                } else if (TextUtils.isEmpty(olmPayloadContent.sender)) {
                    String str10 = LOG_TAG;
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("## decryptEvent() : Olm event (id=");
                    sb7.append(event.eventId);
                    sb7.append(") contains no 'sender' property; cannot prevent unknown-key attack");
                    Log.m211e(str10, sb7.toString());
                    throw new MXDecryptionException(new MXCryptoError(MXCryptoError.MISSING_PROPERTY_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, String.format(MXCryptoError.ERROR_MISSING_PROPERTY_REASON, new Object[]{BingRule.KIND_SENDER})));
                } else if (!TextUtils.equals(olmPayloadContent.sender, event.getSender())) {
                    String str11 = LOG_TAG;
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append("Event ");
                    sb8.append(event.eventId);
                    sb8.append(": original sender ");
                    sb8.append(olmPayloadContent.sender);
                    sb8.append(" does not match reported sender ");
                    sb8.append(event.getSender());
                    Log.m211e(str11, sb8.toString());
                    throw new MXDecryptionException(new MXCryptoError(MXCryptoError.FORWARDED_MESSAGE_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, String.format(MXCryptoError.FORWARDED_MESSAGE_REASON, new Object[]{olmPayloadContent.sender})));
                } else {
                    if (!TextUtils.equals(olmPayloadContent.room_id, event.roomId)) {
                        olmPayloadContent.room_id = event.roomId;
                    }
                    if (olmPayloadContent.keys == null) {
                        Log.m211e(LOG_TAG, "## decryptEvent failed : null keys");
                        throw new MXDecryptionException(new MXCryptoError(MXCryptoError.UNABLE_TO_DECRYPT_ERROR_CODE, MXCryptoError.UNABLE_TO_DECRYPT, MXCryptoError.MISSING_CIPHER_TEXT_REASON));
                    }
                    MXEventDecryptionResult mXEventDecryptionResult = new MXEventDecryptionResult();
                    mXEventDecryptionResult.mClearEvent = parse;
                    mXEventDecryptionResult.mSenderCurve25519Key = str2;
                    mXEventDecryptionResult.mClaimedEd25519Key = (String) olmPayloadContent.keys.get(OlmAccount.JSON_KEY_FINGER_PRINT_KEY);
                    return mXEventDecryptionResult;
                }
            }
        }
    }

    private String decryptMessage(Map<String, Object> map, String str) {
        ArrayList arrayList;
        Integer num;
        Set sessionIds = this.mOlmDevice.getSessionIds(str);
        if (sessionIds == null) {
            arrayList = new ArrayList();
        } else {
            arrayList = new ArrayList(sessionIds);
        }
        String str2 = (String) map.get("body");
        Object obj = map.get("type");
        if (obj != null) {
            if (obj instanceof Double) {
                num = new Integer(((Double) obj).intValue());
            } else if (obj instanceof Integer) {
                num = (Integer) obj;
            } else if (obj instanceof Long) {
                num = new Integer(((Long) obj).intValue());
            }
            if (str2 != null || num == null) {
                return null;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str3 = (String) it.next();
                String decryptMessage = this.mOlmDevice.decryptMessage(str2, num.intValue(), str3, str);
                if (decryptMessage != null) {
                    String str4 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## decryptMessage() : Decrypted Olm message from ");
                    sb.append(str);
                    sb.append(" with session ");
                    sb.append(str3);
                    Log.m209d(str4, sb.toString());
                    return decryptMessage;
                } else if (this.mOlmDevice.matchesSession(str, str3, num.intValue(), str2)) {
                    String str5 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## decryptMessage() : Error decrypting prekey message with existing session id ");
                    sb2.append(str3);
                    sb2.append(":TODO");
                    Log.m211e(str5, sb2.toString());
                    return null;
                }
            }
            if (num.intValue() != 0) {
                if (arrayList.size() == 0) {
                    Log.m211e(LOG_TAG, "## decryptMessage() : No existing sessions");
                } else {
                    Log.m211e(LOG_TAG, "## decryptMessage() : Error decrypting non-prekey message with existing sessions");
                }
                return null;
            }
            Map createInboundSession = this.mOlmDevice.createInboundSession(str, num.intValue(), str2);
            if (createInboundSession == null) {
                Log.m211e(LOG_TAG, "## decryptMessage() :  Error decrypting non-prekey message with existing sessions");
                return null;
            }
            String str6 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## decryptMessage() :  Created new inbound Olm session get id ");
            sb3.append((String) createInboundSession.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID));
            sb3.append(" with ");
            sb3.append(str);
            Log.m209d(str6, sb3.toString());
            return (String) createInboundSession.get("payload");
        }
        num = null;
        if (str2 != null) {
        }
        return null;
    }
}
