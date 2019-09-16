package com.opengarden.firechat.matrixsdk.rest.model.message;

public class StickerJsonMessage {
    public String body;
    public String format;
    public ImageInfo info;
    public final String msgtype = Message.MSGTYPE_STICKER_LOCAL;
    public String url;
}
