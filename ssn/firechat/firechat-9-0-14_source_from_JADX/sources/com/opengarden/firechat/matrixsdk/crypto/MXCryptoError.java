package com.opengarden.firechat.matrixsdk.crypto;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;

public class MXCryptoError extends MatrixError {
    public static final String BAD_ENCRYPTED_MESSAGE_ERROR_CODE = "BAD_ENCRYPTED_MESSAGE";
    public static final String BAD_ENCRYPTED_MESSAGE_REASON = "Bad Encrypted Message";
    public static final String BAD_RECIPIENT_ERROR_CODE = "BAD_RECIPIENT";
    public static final String BAD_RECIPIENT_KEY_ERROR_CODE = "BAD_RECIPIENT_KEY";
    public static final String BAD_RECIPIENT_KEY_REASON = "Message not intended for this device";
    public static final String BAD_RECIPIENT_REASON = "Message was intended for %1$s";
    public static final String BAD_ROOM_ERROR_CODE = "BAD_ROOM";
    public static final String BAD_ROOM_REASON = "Message intended for room %1$s";
    public static final String DETAILLED_OLM_REASON = "Unable to decrypt %1$s. OLM error: %2$s";
    public static final String DUPLICATED_MESSAGE_INDEX_ERROR_CODE = "DUPLICATED_MESSAGE_INDEX";
    public static final String DUPLICATE_MESSAGE_INDEX_REASON = "Duplicate message index, possible replay attack %1$s";
    public static final String ENCRYPTING_NOT_ENABLED_ERROR_CODE = "ENCRYPTING_NOT_ENABLED";
    public static final String ENCRYPTING_NOT_ENABLED_REASON = "Encryption not enabled";
    public static final String ERROR_MISSING_PROPERTY_REASON = "No '%1$s' property. Cannot prevent unknown-key attack";
    public static final String FORWARDED_MESSAGE_ERROR_CODE = "FORWARDED_MESSAGE";
    public static final String FORWARDED_MESSAGE_REASON = "Message forwarded from %1$s";
    public static final String INBOUND_SESSION_MISMATCH_ROOM_ID_ERROR_CODE = "INBOUND_SESSION_MISMATCH_ROOM_ID";
    public static final String INBOUND_SESSION_MISMATCH_ROOM_ID_REASON = "Mismatched room_id for inbound group session (expected %1$s, was %2$s)";
    public static final String MISSING_CIPHER_TEXT_ERROR_CODE = "MISSING_CIPHER_TEXT";
    public static final String MISSING_CIPHER_TEXT_REASON = "Missing ciphertext";
    public static final String MISSING_FIELDS_ERROR_CODE = "MISSING_FIELDS";
    public static final String MISSING_FIELDS_REASON = "Missing fields in input";
    public static final String MISSING_PROPERTY_ERROR_CODE = "MISSING_PROPERTY";
    public static final String NOT_INCLUDED_IN_RECIPIENT_REASON = "Not included in recipients";
    public static final String NOT_INCLUDE_IN_RECIPIENTS_ERROR_CODE = "NOT_INCLUDE_IN_RECIPIENTS";
    public static final String NO_MORE_ALGORITHM_REASON = "Room was previously configured to use encryption, but is no longer. Perhaps the homeserver is hiding the configuration event.";
    public static final String OLM_ERROR_CODE = "OLM_ERROR_CODE";
    public static final String OLM_REASON = "OLM error: %1$s";
    public static final String UNABLE_TO_DECRYPT = "Unable to decrypt";
    public static final String UNABLE_TO_DECRYPT_ERROR_CODE = "UNABLE_TO_DECRYPT";
    public static final String UNABLE_TO_DECRYPT_REASON = "Unable to decrypt %1$s. Algorithm: %2$s";
    public static final String UNABLE_TO_ENCRYPT = "Unable to encrypt";
    public static final String UNABLE_TO_ENCRYPT_ERROR_CODE = "UNABLE_TO_ENCRYPT";
    public static final String UNABLE_TO_ENCRYPT_REASON = "Unable to encrypt %s";
    public static final String UNKNOWN_DEVICES_CODE = "UNKNOWN_DEVICES_CODE";
    public static final String UNKNOWN_DEVICES_REASON = "This room contains unknown devices which have not been verified. \nWe strongly recommend you verify them before continuing.";
    public static final String UNKNOWN_INBOUND_SESSION_ID_ERROR_CODE = "UNKNOWN_INBOUND_SESSION_ID";
    public static final String UNKNOWN_INBOUND_SESSION_ID_REASON = "Unknown inbound session id";
    private String mDetailedErrorDescription = null;
    public Object mExceptionData = null;

    public MXCryptoError(String str, String str2, String str3) {
        this.errcode = str;
        this.error = str2;
        this.mDetailedErrorDescription = str3;
    }

    public MXCryptoError(String str, String str2, String str3, Object obj) {
        this.errcode = str;
        this.error = str2;
        this.mDetailedErrorDescription = str3;
        this.mExceptionData = obj;
    }

    public boolean isOlmError() {
        return TextUtils.equals(OLM_ERROR_CODE, this.errcode);
    }

    public String getDetailedErrorDescription() {
        if (TextUtils.isEmpty(this.mDetailedErrorDescription)) {
            return this.error;
        }
        return this.mDetailedErrorDescription;
    }
}
