package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;

public class EncryptedEventContent implements Serializable {
    public String algorithm;
    public String ciphertext;
    public String device_id;
    public String sender_key;
    public String session_id;
}
