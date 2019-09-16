package com.opengarden.firechat.matrixsdk.rest.model.publicroom;

import com.opengarden.firechat.matrixsdk.data.RoomState;
import java.io.Serializable;

public class PublicRoom extends RoomState implements Serializable {
    public Boolean guestCanJoin;
    public int numJoinedMembers;
    public String serverName;
    public Boolean worldReadable;
}
