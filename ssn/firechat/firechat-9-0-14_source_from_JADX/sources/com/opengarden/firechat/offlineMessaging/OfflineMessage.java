package com.opengarden.firechat.offlineMessaging;

import com.opengarden.firechat.matrixsdk.rest.model.Event;
import java.io.Serializable;

public class OfflineMessage implements Serializable {
    private String accessToken;
    private Event event;
    private String roomId;
    private String transactionId;

    public OfflineMessage(String str, String str2, String str3, Event event2) {
        this.transactionId = str;
        this.roomId = str2;
        this.accessToken = str3;
        this.event = event2;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String str) {
        this.accessToken = str;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String str) {
        this.transactionId = str;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public void setRoomId(String str) {
        this.roomId = str;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event2) {
        this.event = event2;
    }
}
