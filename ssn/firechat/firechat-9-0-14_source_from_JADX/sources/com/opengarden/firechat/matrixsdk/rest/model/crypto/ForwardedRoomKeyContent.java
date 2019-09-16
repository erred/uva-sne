package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;
import java.util.List;

public class ForwardedRoomKeyContent implements Serializable {
    public String algorithm;
    public List<String> forwarding_curve25519_key_chain;
    public String room_id;
    public String sender_claimed_ed25519_key;
    public String sender_key;
    public String session_id;
    public String session_key;
}
