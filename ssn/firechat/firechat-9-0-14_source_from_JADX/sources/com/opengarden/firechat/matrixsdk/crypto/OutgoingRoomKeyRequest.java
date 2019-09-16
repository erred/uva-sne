package com.opengarden.firechat.matrixsdk.crypto;

import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OutgoingRoomKeyRequest implements Serializable {
    public String mCancellationTxnId;
    public List<Map<String, String>> mRecipients;
    public Map<String, String> mRequestBody;
    public String mRequestId;
    public RequestState mState;

    public enum RequestState {
        UNSENT,
        SENT,
        CANCELLATION_PENDING,
        CANCELLATION_PENDING_AND_WILL_RESEND,
        FAILED
    }

    public OutgoingRoomKeyRequest(Map<String, String> map, List<Map<String, String>> list, String str, RequestState requestState) {
        this.mRequestBody = map;
        this.mRecipients = list;
        this.mRequestId = str;
        this.mState = requestState;
    }

    public String getRoomId() {
        if (this.mRequestBody != null) {
            return (String) this.mRequestBody.get("room_id");
        }
        return null;
    }

    public String getSessionId() {
        if (this.mRequestBody != null) {
            return (String) this.mRequestBody.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID);
        }
        return null;
    }
}
