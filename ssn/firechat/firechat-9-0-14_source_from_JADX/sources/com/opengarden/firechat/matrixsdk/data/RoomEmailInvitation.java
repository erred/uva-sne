package com.opengarden.firechat.matrixsdk.data;

import java.util.Map;

public class RoomEmailInvitation {
    public String email;
    public String guestAccessToken;
    public String guestUserId;
    public String inviterName;
    public String roomAvatarUrl;
    public String roomName;
    public String signUrl;

    public RoomEmailInvitation(Map<String, String> map) {
        if (map != null) {
            this.email = (String) map.get("email");
            this.signUrl = (String) map.get("signurl");
            this.roomName = (String) map.get("room_name");
            this.roomAvatarUrl = (String) map.get("room_avatar_url");
            this.inviterName = (String) map.get("inviter_name");
            this.guestAccessToken = (String) map.get("guestAccessToken");
            this.guestUserId = (String) map.get("guest_user_id");
        }
    }
}
