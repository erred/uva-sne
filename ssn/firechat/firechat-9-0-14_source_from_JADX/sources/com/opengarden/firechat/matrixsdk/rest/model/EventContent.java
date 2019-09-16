package com.opengarden.firechat.matrixsdk.rest.model;

import com.opengarden.firechat.matrixsdk.rest.model.pid.RoomThirdPartyInvite;
import java.io.Serializable;

public class EventContent implements Serializable {
    public String algorithm;
    public String avatar_url;
    public String displayname;
    public String membership;
    public RoomThirdPartyInvite third_party_invite;
}
