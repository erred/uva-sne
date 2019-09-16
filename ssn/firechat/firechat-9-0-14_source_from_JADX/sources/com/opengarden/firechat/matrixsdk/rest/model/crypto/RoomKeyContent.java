package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;

public class RoomKeyContent implements Serializable {
    public String algorithm;
    public Object chain_index;
    public String room_id;
    public String session_id;
    public String session_key;
}
