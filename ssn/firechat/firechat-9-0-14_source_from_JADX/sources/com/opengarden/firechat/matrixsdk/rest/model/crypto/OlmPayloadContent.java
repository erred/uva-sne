package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;
import java.util.Map;

public class OlmPayloadContent implements Serializable {
    public Map<String, String> keys;
    public String recipient;
    public Map<String, String> recipient_keys;
    public String room_id;
    public String sender;
}
