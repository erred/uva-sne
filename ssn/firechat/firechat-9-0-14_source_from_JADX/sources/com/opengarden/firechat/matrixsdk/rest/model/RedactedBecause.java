package com.opengarden.firechat.matrixsdk.rest.model;

import java.io.Serializable;

public class RedactedBecause implements Serializable {
    public RedactedContent content;
    public String event_id;
    public long origin_server_ts;
    public String redacts;
    public String sender;
    public String type;
    public UnsignedData unsigned;
}
