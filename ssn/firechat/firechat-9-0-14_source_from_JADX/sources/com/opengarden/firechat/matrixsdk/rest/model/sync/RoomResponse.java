package com.opengarden.firechat.matrixsdk.rest.model.sync;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import java.util.List;

public class RoomResponse {
    public List<Event> accountData;
    public Event invite;
    public String inviter;
    public String membership;
    public TokensChunkResponse<Event> messages;
    public List<Event> presence;
    public List<Event> receipts;
    public String roomId;
    public List<Event> state;
    public String visibility;
}
