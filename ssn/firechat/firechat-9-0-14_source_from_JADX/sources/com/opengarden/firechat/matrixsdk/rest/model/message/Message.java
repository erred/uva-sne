package com.opengarden.firechat.matrixsdk.rest.model.message;

import com.google.gson.annotations.SerializedName;

public class Message {
    public static final String FORMAT_MATRIX_HTML = "org.matrix.custom.html";
    public static final String MSGTYPE_AUDIO = "m.audio";
    public static final String MSGTYPE_EMOTE = "m.emote";
    public static final String MSGTYPE_FILE = "m.file";
    public static final String MSGTYPE_IMAGE = "m.image";
    public static final String MSGTYPE_LOCATION = "m.location";
    public static final String MSGTYPE_NOTICE = "m.notice";
    public static final String MSGTYPE_STICKER_LOCAL = "org.matrix.android.sdk.sticker";
    public static final String MSGTYPE_TEXT = "m.text";
    public static final String MSGTYPE_VIDEO = "m.video";
    public String body;
    public String format;
    public String formatted_body;
    public String msgtype;
    @SerializedName("m.relates_to")
    public RelatesTo relatesTo;
}
