package com.opengarden.firechat.matrixsdk.rest.model.pid;

import java.io.Serializable;

public class RoomThirdPartyInvite implements Serializable {
    public String display_name;
    private String mOriginalEventId = null;
    public String token;

    public RoomThirdPartyInvite deepCopy() {
        RoomThirdPartyInvite roomThirdPartyInvite = new RoomThirdPartyInvite();
        roomThirdPartyInvite.display_name = this.display_name;
        roomThirdPartyInvite.token = this.token;
        roomThirdPartyInvite.mOriginalEventId = this.mOriginalEventId;
        return roomThirdPartyInvite;
    }

    public void setOriginalEventid(String str) {
        this.mOriginalEventId = str;
    }

    public String getOriginalEventId() {
        return this.mOriginalEventId;
    }
}
