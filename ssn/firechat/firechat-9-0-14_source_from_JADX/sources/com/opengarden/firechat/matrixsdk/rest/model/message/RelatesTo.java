package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class RelatesTo {
    @SerializedName("m.in_reply_to")
    public Map<String, String> dict;
}
