package com.opengarden.firechat.matrixsdk.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments;
import com.opengarden.firechat.matrixsdk.crypto.MXEncryptedAttachments.EncryptionResult;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.message.AudioMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.MediaMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.RelatesTo;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.util.ImageUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.PermalinkUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class RoomMediaMessagesSender {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RoomMediaMessagesSender";
    private static Handler mEncodingHandler;
    private static Handler mEventHandler;
    /* access modifiers changed from: private */
    public static Handler mUiHandler;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final MXDataHandler mDataHandler;
    /* access modifiers changed from: private */
    public final List<RoomMediaMessage> mPendingRoomMediaMessages = new ArrayList();
    /* access modifiers changed from: private */
    public final Room mRoom;
    /* access modifiers changed from: private */
    public RoomMediaMessage mSendingRoomMediaMessage;

    RoomMediaMessagesSender(Context context, MXDataHandler mXDataHandler, Room room) {
        this.mRoom = room;
        this.mContext = context.getApplicationContext();
        this.mDataHandler = mXDataHandler;
        if (mUiHandler == null) {
            mUiHandler = new Handler(Looper.getMainLooper());
            HandlerThread handlerThread = new HandlerThread("RoomDataItemsSender_event", 1);
            handlerThread.start();
            mEventHandler = new Handler(handlerThread.getLooper());
            HandlerThread handlerThread2 = new HandlerThread("RoomDataItemsSender_encoding", 1);
            handlerThread2.start();
            mEncodingHandler = new Handler(handlerThread2.getLooper());
        }
    }

    /* access modifiers changed from: 0000 */
    public void send(final RoomMediaMessage roomMediaMessage) {
        mEventHandler.post(new Runnable() {
            public void run() {
                Message message;
                if (roomMediaMessage.getEvent() == null) {
                    String mimeType = roomMediaMessage.getMimeType(RoomMediaMessagesSender.this.mContext);
                    if (mimeType == null) {
                        mimeType = "";
                    }
                    if (roomMediaMessage.getUri() == null) {
                        message = RoomMediaMessagesSender.this.buildTextMessage(roomMediaMessage);
                    } else if (mimeType.startsWith("image/")) {
                        message = RoomMediaMessagesSender.this.buildImageMessage(roomMediaMessage);
                    } else if (mimeType.startsWith("video/")) {
                        message = RoomMediaMessagesSender.this.buildVideoMessage(roomMediaMessage);
                    } else {
                        message = RoomMediaMessagesSender.this.buildFileMessage(roomMediaMessage);
                    }
                    if (message == null) {
                        String access$500 = RoomMediaMessagesSender.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## send ");
                        sb.append(roomMediaMessage);
                        sb.append(" not supported");
                        Log.m211e(access$500, sb.toString());
                        RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                            public void run() {
                                RoomMediaMessage roomMediaMessage = roomMediaMessage;
                                StringBuilder sb = new StringBuilder();
                                sb.append("not supported ");
                                sb.append(roomMediaMessage);
                                roomMediaMessage.onEventCreationFailed(sb.toString());
                            }
                        });
                        return;
                    }
                    roomMediaMessage.setMessageType(message.msgtype);
                    if (roomMediaMessage.getReplyToEvent() != null) {
                        message.relatesTo = new RelatesTo();
                        message.relatesTo.dict = new HashMap();
                        message.relatesTo.dict.put("event_id", roomMediaMessage.getReplyToEvent().eventId);
                    }
                    roomMediaMessage.setEvent(new Event(message, RoomMediaMessagesSender.this.mDataHandler.getUserId(), RoomMediaMessagesSender.this.mRoom.getRoomId()));
                }
                RoomMediaMessagesSender.this.mDataHandler.updateEventState(roomMediaMessage.getEvent(), SentState.UNSENT);
                RoomMediaMessagesSender.this.mRoom.storeOutgoingEvent(roomMediaMessage.getEvent());
                RoomMediaMessagesSender.this.mDataHandler.getStore().commit();
                RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                    public void run() {
                        roomMediaMessage.onEventCreated();
                    }
                });
                synchronized (RoomMediaMessagesSender.LOG_TAG) {
                    if (!RoomMediaMessagesSender.this.mPendingRoomMediaMessages.contains(roomMediaMessage)) {
                        RoomMediaMessagesSender.this.mPendingRoomMediaMessages.add(roomMediaMessage);
                    }
                }
                RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                    public void run() {
                        RoomMediaMessagesSender.this.sendNext();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void skip() {
        synchronized (LOG_TAG) {
            this.mSendingRoomMediaMessage = null;
        }
        sendNext();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        if (uploadMedias(r1) == false) goto L_0x002b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        sendEvent(r1.getEvent());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0032, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendNext() {
        /*
            r3 = this;
            java.lang.String r0 = LOG_TAG
            monitor-enter(r0)
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r1 = r3.mSendingRoomMediaMessage     // Catch:{ all -> 0x0035 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            return
        L_0x0009:
            java.util.List<com.opengarden.firechat.matrixsdk.data.RoomMediaMessage> r1 = r3.mPendingRoomMediaMessages     // Catch:{ all -> 0x0035 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0035 }
            if (r1 != 0) goto L_0x0033
            java.util.List<com.opengarden.firechat.matrixsdk.data.RoomMediaMessage> r1 = r3.mPendingRoomMediaMessages     // Catch:{ all -> 0x0035 }
            r2 = 0
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0035 }
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r1 = (com.opengarden.firechat.matrixsdk.data.RoomMediaMessage) r1     // Catch:{ all -> 0x0035 }
            r3.mSendingRoomMediaMessage = r1     // Catch:{ all -> 0x0035 }
            java.util.List<com.opengarden.firechat.matrixsdk.data.RoomMediaMessage> r1 = r3.mPendingRoomMediaMessages     // Catch:{ all -> 0x0035 }
            r1.remove(r2)     // Catch:{ all -> 0x0035 }
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r1 = r3.mSendingRoomMediaMessage     // Catch:{ all -> 0x0035 }
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            boolean r0 = r3.uploadMedias(r1)
            if (r0 == 0) goto L_0x002b
            return
        L_0x002b:
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r1.getEvent()
            r3.sendEvent(r0)
            return
        L_0x0033:
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            return
        L_0x0035:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.RoomMediaMessagesSender.sendNext():void");
    }

    /* access modifiers changed from: private */
    public void sendEvent(final Event event) {
        mUiHandler.post(new Runnable() {
            public void run() {
                RoomMediaMessagesSender.this.mRoom.sendEvent(event, new ApiCallback<Void>() {
                    private ApiCallback<Void> getCallback() {
                        ApiCallback<Void> sendingCallback;
                        synchronized (RoomMediaMessagesSender.LOG_TAG) {
                            sendingCallback = RoomMediaMessagesSender.this.mSendingRoomMediaMessage.getSendingCallback();
                            RoomMediaMessagesSender.this.mSendingRoomMediaMessage.setEventSendingCallback(null);
                            RoomMediaMessagesSender.this.mSendingRoomMediaMessage = null;
                        }
                        return sendingCallback;
                    }

                    public void onSuccess(Void voidR) {
                        ApiCallback callback = getCallback();
                        if (callback != null) {
                            try {
                                callback.onSuccess(null);
                            } catch (Exception e) {
                                String access$500 = RoomMediaMessagesSender.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## sendNext() failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        RoomMediaMessagesSender.this.sendNext();
                    }

                    public void onNetworkError(Exception exc) {
                        ApiCallback callback = getCallback();
                        if (callback != null) {
                            try {
                                callback.onNetworkError(exc);
                            } catch (Exception e) {
                                String access$500 = RoomMediaMessagesSender.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## sendNext() failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        RoomMediaMessagesSender.this.sendNext();
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        ApiCallback callback = getCallback();
                        if (callback != null) {
                            try {
                                callback.onMatrixError(matrixError);
                            } catch (Exception e) {
                                String access$500 = RoomMediaMessagesSender.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## sendNext() failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        RoomMediaMessagesSender.this.sendNext();
                    }

                    public void onUnexpectedError(Exception exc) {
                        ApiCallback callback = getCallback();
                        if (callback != null) {
                            try {
                                callback.onUnexpectedError(exc);
                            } catch (Exception e) {
                                String access$500 = RoomMediaMessagesSender.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## sendNext() failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        RoomMediaMessagesSender.this.sendNext();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d3, code lost:
        r11 = r6;
        r10 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00fd, code lost:
        r6 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0137, code lost:
        if (r6 == null) goto L_0x016a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x013f, code lost:
        if (android.text.TextUtils.isEmpty(r3.formatted_body) == false) goto L_0x0144;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0141, code lost:
        r13 = r3.body;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0144, code lost:
        r13 = r3.formatted_body;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0146, code lost:
        r4 = r12;
        r5 = r0;
        r7 = r10;
        r3.body = includeReplyToToBody(r5, r6, r7, r3.body, r1.equals(com.opengarden.firechat.matrixsdk.rest.model.message.Message.MSGTYPE_EMOTE));
        r3.formatted_body = includeReplyToToFormattedBody(r5, r11, r7, r13, r1.equals(com.opengarden.firechat.matrixsdk.rest.model.message.Message.MSGTYPE_EMOTE));
        r3.format = com.opengarden.firechat.matrixsdk.rest.model.message.Message.FORMAT_MATRIX_HTML;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x016a, code lost:
        r0 = LOG_TAG;
        r4 = new java.lang.StringBuilder();
        r4.append("Unsupported 'msgtype': ");
        r4.append(r1);
        r4.append(". Consider calling Room.canReplyTo(Event)");
        com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r4.toString());
        r13.setReplyToEvent(null);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.opengarden.firechat.matrixsdk.rest.model.message.Message buildTextMessage(com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r13) {
        /*
            r12 = this;
            java.lang.CharSequence r0 = r13.getText()
            java.lang.String r1 = r13.getHtmlText()
            r2 = 0
            if (r0 != 0) goto L_0x0018
            if (r1 == 0) goto L_0x0016
            android.text.Spanned r0 = android.text.Html.fromHtml(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x001c
        L_0x0016:
            r0 = r2
            goto L_0x001c
        L_0x0018:
            java.lang.String r0 = r0.toString()
        L_0x001c:
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x002f
            java.lang.String r3 = r13.getMessageType()
            java.lang.String r4 = "m.emote"
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 != 0) goto L_0x002f
            return r2
        L_0x002f:
            com.opengarden.firechat.matrixsdk.rest.model.message.Message r3 = new com.opengarden.firechat.matrixsdk.rest.model.message.Message
            r3.<init>()
            java.lang.String r4 = r13.getMessageType()
            if (r4 != 0) goto L_0x003d
            java.lang.String r4 = "m.text"
            goto L_0x0041
        L_0x003d:
            java.lang.String r4 = r13.getMessageType()
        L_0x0041:
            r3.msgtype = r4
            r3.body = r0
            java.lang.String r0 = r3.body
            if (r0 != 0) goto L_0x004d
            java.lang.String r0 = ""
            r3.body = r0
        L_0x004d:
            boolean r0 = android.text.TextUtils.isEmpty(r1)
            if (r0 != 0) goto L_0x0059
            r3.formatted_body = r1
            java.lang.String r0 = "org.matrix.custom.html"
            r3.format = r0
        L_0x0059:
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r13.getReplyToEvent()
            if (r0 == 0) goto L_0x0193
            com.google.gson.JsonObject r1 = r0.getContentAsJsonObject()
            java.lang.String r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.getMessageMsgType(r1)
            if (r1 == 0) goto L_0x0189
            r4 = -1
            int r5 = r1.hashCode()
            r6 = 1
            r7 = 0
            switch(r5) {
                case -1128764835: goto L_0x00b0;
                case -1128351218: goto L_0x00a6;
                case -636239083: goto L_0x009c;
                case -632772425: goto L_0x0092;
                case -629092198: goto L_0x0088;
                case -617202758: goto L_0x007e;
                case 2118539129: goto L_0x0074;
                default: goto L_0x0073;
            }
        L_0x0073:
            goto L_0x00b9
        L_0x0074:
            java.lang.String r5 = "m.notice"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 1
            goto L_0x00b9
        L_0x007e:
            java.lang.String r5 = "m.video"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 4
            goto L_0x00b9
        L_0x0088:
            java.lang.String r5 = "m.image"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 3
            goto L_0x00b9
        L_0x0092:
            java.lang.String r5 = "m.emote"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 2
            goto L_0x00b9
        L_0x009c:
            java.lang.String r5 = "m.audio"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 5
            goto L_0x00b9
        L_0x00a6:
            java.lang.String r5 = "m.text"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 0
            goto L_0x00b9
        L_0x00b0:
            java.lang.String r5 = "m.file"
            boolean r5 = r1.equals(r5)
            if (r5 == 0) goto L_0x00b9
            r4 = 6
        L_0x00b9:
            switch(r4) {
                case 0: goto L_0x00ff;
                case 1: goto L_0x00ff;
                case 2: goto L_0x00ff;
                case 3: goto L_0x00f4;
                case 4: goto L_0x00ea;
                case 5: goto L_0x00e0;
                case 6: goto L_0x00d6;
                default: goto L_0x00bc;
            }
        L_0x00bc:
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Reply to: unsupported msgtype: "
            r5.append(r6)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m217w(r4, r5)
            r6 = r2
        L_0x00d3:
            r11 = r6
            r10 = 0
            goto L_0x0137
        L_0x00d6:
            android.content.Context r4 = r12.mContext
            r5 = 2131689974(0x7f0f01f6, float:1.9008979E38)
            java.lang.String r4 = r4.getString(r5)
            goto L_0x00fd
        L_0x00e0:
            android.content.Context r4 = r12.mContext
            r5 = 2131689976(0x7f0f01f8, float:1.9008983E38)
            java.lang.String r4 = r4.getString(r5)
            goto L_0x00fd
        L_0x00ea:
            android.content.Context r4 = r12.mContext
            r5 = 2131689975(0x7f0f01f7, float:1.900898E38)
            java.lang.String r4 = r4.getString(r5)
            goto L_0x00fd
        L_0x00f4:
            android.content.Context r4 = r12.mContext
            r5 = 2131689977(0x7f0f01f9, float:1.9008985E38)
            java.lang.String r4 = r4.getString(r5)
        L_0x00fd:
            r6 = r4
            goto L_0x00d3
        L_0x00ff:
            com.google.gson.JsonObject r4 = r0.getContentAsJsonObject()
            com.opengarden.firechat.matrixsdk.rest.model.message.Message r4 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toMessage(r4)
            java.lang.String r5 = r4.body
            java.lang.String r8 = r4.formatted_body
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0114
            java.lang.String r8 = r4.body
            goto L_0x0116
        L_0x0114:
            java.lang.String r8 = r4.formatted_body
        L_0x0116:
            com.opengarden.firechat.matrixsdk.rest.model.message.RelatesTo r9 = r4.relatesTo
            if (r9 == 0) goto L_0x0133
            com.opengarden.firechat.matrixsdk.rest.model.message.RelatesTo r9 = r4.relatesTo
            java.util.Map<java.lang.String, java.lang.String> r9 = r9.dict
            if (r9 == 0) goto L_0x0133
            com.opengarden.firechat.matrixsdk.rest.model.message.RelatesTo r4 = r4.relatesTo
            java.util.Map<java.lang.String, java.lang.String> r4 = r4.dict
            java.lang.String r9 = "event_id"
            java.lang.Object r4 = r4.get(r9)
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0133
            goto L_0x0134
        L_0x0133:
            r6 = 0
        L_0x0134:
            r10 = r6
            r11 = r8
            r6 = r5
        L_0x0137:
            if (r6 == 0) goto L_0x016a
            java.lang.String r13 = r3.formatted_body
            boolean r13 = android.text.TextUtils.isEmpty(r13)
            if (r13 == 0) goto L_0x0144
            java.lang.String r13 = r3.body
            goto L_0x0146
        L_0x0144:
            java.lang.String r13 = r3.formatted_body
        L_0x0146:
            java.lang.String r8 = r3.body
            java.lang.String r2 = "m.emote"
            boolean r9 = r1.equals(r2)
            r4 = r12
            r5 = r0
            r7 = r10
            java.lang.String r2 = r4.includeReplyToToBody(r5, r6, r7, r8, r9)
            r3.body = r2
            java.lang.String r2 = "m.emote"
            boolean r9 = r1.equals(r2)
            r6 = r11
            r8 = r13
            java.lang.String r13 = r4.includeReplyToToFormattedBody(r5, r6, r7, r8, r9)
            r3.formatted_body = r13
            java.lang.String r13 = "org.matrix.custom.html"
            r3.format = r13
            goto L_0x0193
        L_0x016a:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Unsupported 'msgtype': "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r1 = ". Consider calling Room.canReplyTo(Event)"
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r1)
            r13.setReplyToEvent(r2)
            goto L_0x0193
        L_0x0189:
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "Null 'msgtype'. Consider calling Room.canReplyTo(Event)"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r1)
            r13.setReplyToEvent(r2)
        L_0x0193:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.RoomMediaMessagesSender.buildTextMessage(com.opengarden.firechat.matrixsdk.data.RoomMediaMessage):com.opengarden.firechat.matrixsdk.rest.model.message.Message");
    }

    private String includeReplyToToBody(Event event, String str, boolean z, String str2, boolean z2) {
        String[] split = str.split(StringUtils.f158LF);
        int i = 0;
        if (z) {
            while (i < split.length && split[i].startsWith("> ")) {
                i++;
            }
            if (i < split.length && split[i].isEmpty()) {
                i++;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (i < split.length) {
            if (z2) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("* <");
                sb2.append(event.sender);
                sb2.append("> ");
                sb2.append(split[i]);
                split[i] = sb2.toString();
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("<");
                sb3.append(event.sender);
                sb3.append("> ");
                sb3.append(split[i]);
                split[i] = sb3.toString();
            }
            while (i < split.length) {
                sb.append("> ");
                sb.append(split[i]);
                sb.append(StringUtils.f158LF);
                i++;
            }
        }
        sb.append(StringUtils.f158LF);
        sb.append(str2);
        return sb.toString();
    }

    private String includeReplyToToFormattedBody(Event event, String str, boolean z, String str2, boolean z2) {
        if (z) {
            str = str.replaceAll("^<mx-reply>.*</mx-reply>", "");
        }
        StringBuilder sb = new StringBuilder("<mx-reply><blockquote><a href=\"");
        sb.append(PermalinkUtils.createPermalink(event));
        sb.append("\">");
        sb.append(this.mContext.getString(C1299R.string.message_reply_to_prefix));
        sb.append("</a> ");
        if (z2) {
            sb.append("* ");
        }
        sb.append("<a href=\"");
        sb.append(PermalinkUtils.createPermalink(event.sender));
        sb.append("\">");
        sb.append(event.sender);
        sb.append("</a><br>");
        sb.append(str);
        sb.append("</blockquote></mx-reply>");
        sb.append(str2);
        return sb.toString();
    }

    private static String getThumbnailPath(String str) {
        if (TextUtils.isEmpty(str) || !str.endsWith(".jpg")) {
            return null;
        }
        return str.replace(".jpg", "_thumb.jpg");
    }

    private Bitmap getMediasPickerThumbnail(RoomMediaMessage roomMediaMessage) {
        try {
            String thumbnailPath = getThumbnailPath(roomMediaMessage.getUri().getPath());
            if (thumbnailPath == null || !new File(thumbnailPath).exists()) {
                return null;
            }
            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            return BitmapFactory.decodeFile(thumbnailPath, options);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("cannot restore the medias picker thumbnail ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        } catch (OutOfMemoryError unused) {
            Log.m211e(LOG_TAG, "cannot restore the medias picker thumbnail oom");
            return null;
        }
    }

    private String getMediaUrl(RoomMediaMessage roomMediaMessage) {
        String uri = roomMediaMessage.getUri().toString();
        if (uri.startsWith("file:")) {
            return uri;
        }
        String mimeType = roomMediaMessage.getMimeType(this.mContext);
        Resource openResource = ResourceUtils.openResource(this.mContext, roomMediaMessage.getUri(), mimeType);
        String saveMedia = this.mDataHandler.getMediasCache().saveMedia(openResource.mContentStream, null, mimeType);
        openResource.close();
        return saveMedia;
    }

    /* access modifiers changed from: private */
    public Message buildImageMessage(RoomMediaMessage roomMediaMessage) {
        try {
            String mimeType = roomMediaMessage.getMimeType(this.mContext);
            MXMediasCache mediasCache = this.mDataHandler.getMediasCache();
            String mediaUrl = getMediaUrl(roomMediaMessage);
            Bitmap fullScreenImageKindThumbnail = roomMediaMessage.getFullScreenImageKindThumbnail(this.mContext);
            if (fullScreenImageKindThumbnail == null) {
                fullScreenImageKindThumbnail = getMediasPickerThumbnail(roomMediaMessage);
            }
            if (fullScreenImageKindThumbnail == null) {
                Pair thumbnailSize = roomMediaMessage.getThumbnailSize();
                fullScreenImageKindThumbnail = ResourceUtils.createThumbnailBitmap(this.mContext, roomMediaMessage.getUri(), ((Integer) thumbnailSize.first).intValue(), ((Integer) thumbnailSize.second).intValue());
            }
            if (fullScreenImageKindThumbnail == null) {
                fullScreenImageKindThumbnail = roomMediaMessage.getMiniKindImageThumbnail(this.mContext);
            }
            String saveBitmap = fullScreenImageKindThumbnail != null ? mediasCache.saveBitmap(fullScreenImageKindThumbnail, null) : null;
            int rotationAngleForBitmap = ImageUtils.getRotationAngleForBitmap(this.mContext, Uri.parse(mediaUrl));
            if (rotationAngleForBitmap != 0) {
                ImageUtils.rotateImage(this.mContext, saveBitmap, rotationAngleForBitmap, mediasCache);
            }
            ImageMessage imageMessage = new ImageMessage();
            imageMessage.url = mediaUrl;
            imageMessage.body = roomMediaMessage.getFileName(this.mContext);
            if (TextUtils.isEmpty(imageMessage.body)) {
                imageMessage.body = "Image";
            }
            Uri parse = Uri.parse(mediaUrl);
            if (imageMessage.info == null) {
                Room.fillImageInfo(this.mContext, imageMessage, parse, mimeType);
            }
            if (!(saveBitmap == null || imageMessage.info == null || imageMessage.info.thumbnailInfo != null)) {
                Room.fillThumbnailInfo(this.mContext, imageMessage, Uri.parse(saveBitmap), ResourceUtils.MIME_TYPE_JPEG);
                imageMessage.info.thumbnailUrl = saveBitmap;
            }
            return imageMessage;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## buildImageMessage() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    public String getVideoThumbnailUrl(String str) {
        try {
            return this.mDataHandler.getMediasCache().saveBitmap(ThumbnailUtils.createVideoThumbnail(Uri.parse(str).getPath(), 1), null);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getVideoThumbnailUrl() failed with ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return null;
        }
    }

    /* access modifiers changed from: private */
    public Message buildVideoMessage(RoomMediaMessage roomMediaMessage) {
        try {
            String mediaUrl = getMediaUrl(roomMediaMessage);
            String videoThumbnailUrl = getVideoThumbnailUrl(mediaUrl);
            if (videoThumbnailUrl == null) {
                return buildFileMessage(roomMediaMessage);
            }
            VideoMessage videoMessage = new VideoMessage();
            videoMessage.url = mediaUrl;
            videoMessage.body = roomMediaMessage.getFileName(this.mContext);
            Uri parse = Uri.parse(mediaUrl);
            Room.fillVideoInfo(this.mContext, videoMessage, parse, roomMediaMessage.getMimeType(this.mContext), videoThumbnailUrl != null ? Uri.parse(videoThumbnailUrl) : null, ResourceUtils.MIME_TYPE_JPEG);
            if (videoMessage.body == null) {
                videoMessage.body = parse.getLastPathSegment();
            }
            return videoMessage;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## buildVideoMessage() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    /* access modifiers changed from: private */
    public Message buildFileMessage(RoomMediaMessage roomMediaMessage) {
        FileMessage fileMessage;
        try {
            String mimeType = roomMediaMessage.getMimeType(this.mContext);
            String mediaUrl = getMediaUrl(roomMediaMessage);
            if (mimeType.startsWith("audio/")) {
                fileMessage = new AudioMessage();
            } else {
                fileMessage = new FileMessage();
            }
            fileMessage.url = mediaUrl;
            fileMessage.body = roomMediaMessage.getFileName(this.mContext);
            Uri parse = Uri.parse(mediaUrl);
            Room.fillFileInfo(this.mContext, fileMessage, parse, mimeType);
            if (fileMessage.body == null) {
                fileMessage.body = parse.getLastPathSegment();
            }
            return fileMessage;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## buildFileMessage() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    /* access modifiers changed from: private */
    public boolean uploadMedias(RoomMediaMessage roomMediaMessage) {
        String url;
        String mimeType;
        final Event event = roomMediaMessage.getEvent();
        final Message message = JsonUtils.toMessage(event.getContent());
        if (!(message instanceof MediaMessage)) {
            return false;
        }
        final MediaMessage mediaMessage = (MediaMessage) message;
        if (mediaMessage.isThumbnailLocalContent()) {
            url = mediaMessage.getThumbnailUrl();
            mimeType = ResourceUtils.MIME_TYPE_JPEG;
        } else if (!mediaMessage.isLocalContent()) {
            return false;
        } else {
            url = mediaMessage.getUrl();
            mimeType = mediaMessage.getMimeType();
        }
        final String str = url;
        final String str2 = mimeType;
        Handler handler = mEncodingHandler;
        final RoomMediaMessage roomMediaMessage2 = roomMediaMessage;
        C26003 r0 = new Runnable() {
            public void run() {
                final Uri uri;
                String str;
                String str2;
                FileInputStream fileInputStream;
                final EncryptionResult encryptionResult;
                String str3;
                final MXMediasCache mediasCache = RoomMediaMessagesSender.this.mDataHandler.getMediasCache();
                Uri parse = Uri.parse(str);
                String str4 = str2;
                try {
                    FileInputStream fileInputStream2 = new FileInputStream(new File(parse.getPath()));
                    if (!RoomMediaMessagesSender.this.mRoom.isEncrypted() || !RoomMediaMessagesSender.this.mDataHandler.isCryptoEnabled() || fileInputStream2 == null) {
                        if (mediaMessage.isThumbnailLocalContent()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("thumb");
                            sb.append(message.body);
                            str3 = sb.toString();
                        } else {
                            str3 = message.body;
                        }
                        uri = null;
                        str = str4;
                        fileInputStream = fileInputStream2;
                        str2 = str3;
                        encryptionResult = null;
                    } else {
                        encryptionResult = MXEncryptedAttachments.encryptAttachment(fileInputStream2, str4);
                        fileInputStream2.close();
                        if (encryptionResult != null) {
                            Uri parse2 = Uri.parse(mediasCache.saveMedia(encryptionResult.mEncryptedStream, null, str2));
                            FileInputStream fileInputStream3 = new FileInputStream(new File(parse2.getPath()));
                            uri = parse2;
                            str2 = null;
                            str = "application/octet-stream";
                            fileInputStream = fileInputStream3;
                        } else {
                            RoomMediaMessagesSender.this.skip();
                            RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                                public void run() {
                                    RoomMediaMessagesSender.this.mDataHandler.updateEventState(roomMediaMessage2.getEvent(), SentState.UNDELIVERABLE);
                                    RoomMediaMessagesSender.this.mRoom.storeOutgoingEvent(roomMediaMessage2.getEvent());
                                    RoomMediaMessagesSender.this.mDataHandler.getStore().commit();
                                    roomMediaMessage2.onEncryptionFailed();
                                }
                            });
                            return;
                        }
                    }
                    RoomMediaMessagesSender.this.mDataHandler.updateEventState(roomMediaMessage2.getEvent(), SentState.SENDING);
                    mediasCache.uploadContent(fileInputStream, str2, str, str, new MXMediaUploadListener() {
                        public void onUploadStart(final String str) {
                            RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                                public void run() {
                                    if (roomMediaMessage2.getMediaUploadListener() != null) {
                                        roomMediaMessage2.getMediaUploadListener().onUploadStart(str);
                                    }
                                }
                            });
                        }

                        public void onUploadCancel(final String str) {
                            RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                                public void run() {
                                    RoomMediaMessagesSender.this.mDataHandler.updateEventState(roomMediaMessage2.getEvent(), SentState.UNDELIVERABLE);
                                    if (roomMediaMessage2.getMediaUploadListener() != null) {
                                        roomMediaMessage2.getMediaUploadListener().onUploadCancel(str);
                                        roomMediaMessage2.setMediaUploadListener(null);
                                        roomMediaMessage2.setEventSendingCallback(null);
                                    }
                                    RoomMediaMessagesSender.this.skip();
                                }
                            });
                        }

                        public void onUploadError(final String str, final int i, final String str2) {
                            RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                                public void run() {
                                    RoomMediaMessagesSender.this.mDataHandler.updateEventState(roomMediaMessage2.getEvent(), SentState.UNDELIVERABLE);
                                    if (roomMediaMessage2.getMediaUploadListener() != null) {
                                        roomMediaMessage2.getMediaUploadListener().onUploadError(str, i, str2);
                                        roomMediaMessage2.setMediaUploadListener(null);
                                        roomMediaMessage2.setEventSendingCallback(null);
                                    }
                                    RoomMediaMessagesSender.this.skip();
                                }
                            });
                        }

                        public void onUploadComplete(final String str, final String str2) {
                            RoomMediaMessagesSender.mUiHandler.post(new Runnable() {
                                public void run() {
                                    boolean isThumbnailLocalContent = mediaMessage.isThumbnailLocalContent();
                                    if (isThumbnailLocalContent) {
                                        mediaMessage.setThumbnailUrl(encryptionResult, str2);
                                        if (encryptionResult != null) {
                                            mediasCache.saveFileMediaForUrl(str2, uri.toString(), -1, -1, ResourceUtils.MIME_TYPE_JPEG);
                                            try {
                                                new File(Uri.parse(str).getPath()).delete();
                                            } catch (Exception unused) {
                                                Log.m211e(RoomMediaMessagesSender.LOG_TAG, "## cannot delete the uncompress media");
                                            }
                                        } else {
                                            Pair thumbnailSize = roomMediaMessage2.getThumbnailSize();
                                            mediasCache.saveFileMediaForUrl(str2, str, ((Integer) thumbnailSize.first).intValue(), ((Integer) thumbnailSize.second).intValue(), ResourceUtils.MIME_TYPE_JPEG);
                                        }
                                        event.updateContent(JsonUtils.toJson(message));
                                        RoomMediaMessagesSender.this.mDataHandler.getStore().flushRoomEvents(RoomMediaMessagesSender.this.mRoom.getRoomId());
                                        RoomMediaMessagesSender.this.uploadMedias(roomMediaMessage2);
                                    } else {
                                        if (uri != null) {
                                            mediasCache.saveFileMediaForUrl(str2, uri.toString(), mediaMessage.getMimeType());
                                            try {
                                                new File(Uri.parse(str).getPath()).delete();
                                            } catch (Exception unused2) {
                                                Log.m211e(RoomMediaMessagesSender.LOG_TAG, "## cannot delete the uncompress media");
                                            }
                                        } else {
                                            mediasCache.saveFileMediaForUrl(str2, str, mediaMessage.getMimeType());
                                        }
                                        mediaMessage.setUrl(encryptionResult, str2);
                                        event.updateContent(JsonUtils.toJson(message));
                                        RoomMediaMessagesSender.this.mDataHandler.getStore().flushRoomEvents(RoomMediaMessagesSender.this.mRoom.getRoomId());
                                        String access$500 = RoomMediaMessagesSender.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Uploaded to ");
                                        sb.append(str2);
                                        Log.m209d(access$500, sb.toString());
                                        RoomMediaMessagesSender.this.sendEvent(event);
                                    }
                                    if (roomMediaMessage2.getMediaUploadListener() != null) {
                                        roomMediaMessage2.getMediaUploadListener().onUploadComplete(str, str2);
                                        if (!isThumbnailLocalContent) {
                                            roomMediaMessage2.setMediaUploadListener(null);
                                        }
                                    }
                                }
                            });
                        }
                    });
                } catch (Exception unused) {
                    RoomMediaMessagesSender.this.skip();
                }
            }
        };
        handler.post(r0);
        return true;
    }
}
