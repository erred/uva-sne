package com.opengarden.firechat.matrixsdk.rest.model.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupRooms implements Serializable {
    public List<GroupRoom> chunk;
    public Integer totalRoomCountEstimate;

    public List<GroupRoom> getRoomsList() {
        if (this.chunk == null) {
            this.chunk = new ArrayList();
        }
        return this.chunk;
    }

    public int getEstimatedRoomCount() {
        if (this.totalRoomCountEstimate == null) {
            this.totalRoomCountEstimate = Integer.valueOf(getRoomsList().size());
        }
        return this.totalRoomCountEstimate.intValue();
    }
}
