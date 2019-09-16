package com.opengarden.firechat.matrixsdk.rest.model.publicroom;

import java.util.List;

public class PublicRoomsResponse {
    public List<PublicRoom> chunk;
    public String next_batch;
    public String prev_batch;
    public Integer total_room_count_estimate;
}
