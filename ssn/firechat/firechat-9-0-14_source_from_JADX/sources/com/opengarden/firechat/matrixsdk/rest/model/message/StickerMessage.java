package com.opengarden.firechat.matrixsdk.rest.model.message;

public class StickerMessage extends ImageMessage {
    public StickerMessage() {
        this.msgtype = Message.MSGTYPE_STICKER_LOCAL;
    }

    public StickerMessage(StickerJsonMessage stickerJsonMessage) {
        this();
        this.info = stickerJsonMessage.info;
        this.url = stickerJsonMessage.url;
        this.body = stickerJsonMessage.body;
        this.format = stickerJsonMessage.format;
    }

    public StickerMessage deepCopy() {
        StickerMessage stickerMessage = new StickerMessage();
        stickerMessage.info = this.info;
        stickerMessage.url = this.url;
        stickerMessage.body = this.body;
        stickerMessage.format = this.format;
        if (this.file != null) {
            stickerMessage.file = this.file.deepCopy();
        }
        return stickerMessage;
    }
}
