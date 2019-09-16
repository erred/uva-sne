package com.opengarden.firechat.matrixsdk.rest.model;

import com.google.gson.JsonElement;
import java.io.Serializable;

public class UnsignedData implements Serializable {
    public Long age;
    public transient JsonElement prev_content;
    public RedactedBecause redacted_because;
    public String transaction_id;
}
