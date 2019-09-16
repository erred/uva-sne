package com.opengarden.firechat.matrixsdk.rest.model.crypto;

import java.io.Serializable;

public class RoomKeyRequestBody implements Serializable {
    public String algorithm;
    public String room_id;
    public String sender_key;
    public String session_id;
}
