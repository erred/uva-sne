package com.opengarden.firechat.matrixsdk.rest.model;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.MXEventDecryptionResult;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.LocationMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;
import org.matrix.olm.OlmAccount;

public class Event implements Externalizable {
    public static final long DUMMY_EVENT_AGE = 9223372036854775806L;
    public static final String EVENT_TYPE_CALL_ANSWER = "m.call.answer";
    public static final String EVENT_TYPE_CALL_CANDIDATES = "m.call.candidates";
    public static final String EVENT_TYPE_CALL_HANGUP = "m.call.hangup";
    public static final String EVENT_TYPE_CALL_INVITE = "m.call.invite";
    public static final String EVENT_TYPE_FEEDBACK = "m.room.message.feedback";
    public static final String EVENT_TYPE_FORWARDED_ROOM_KEY = "m.forwarded_room_key";
    public static final String EVENT_TYPE_MESSAGE = "m.room.message";
    public static final String EVENT_TYPE_MESSAGE_ENCRYPTED = "m.room.encrypted";
    public static final String EVENT_TYPE_MESSAGE_ENCRYPTION = "m.room.encryption";
    public static final String EVENT_TYPE_PRESENCE = "m.presence";
    public static final String EVENT_TYPE_READ_MARKER = "m.fully_read";
    public static final String EVENT_TYPE_RECEIPT = "m.receipt";
    public static final String EVENT_TYPE_REDACTION = "m.room.redaction";
    public static final String EVENT_TYPE_ROOM_BOT_OPTIONS = "m.room.bot.options";
    public static final String EVENT_TYPE_ROOM_KEY = "m.room_key";
    public static final String EVENT_TYPE_ROOM_KEY_REQUEST = "m.room_key_request";
    public static final String EVENT_TYPE_ROOM_PLUMBING = "m.room.plumbing";
    public static final String EVENT_TYPE_STATE_CANONICAL_ALIAS = "m.room.canonical_alias";
    public static final String EVENT_TYPE_STATE_HISTORY_VISIBILITY = "m.room.history_visibility";
    public static final String EVENT_TYPE_STATE_RELATED_GROUPS = "m.room.related_groups";
    public static final String EVENT_TYPE_STATE_ROOM_ALIASES = "m.room.aliases";
    public static final String EVENT_TYPE_STATE_ROOM_AVATAR = "m.room.avatar";
    public static final String EVENT_TYPE_STATE_ROOM_CREATE = "m.room.create";
    public static final String EVENT_TYPE_STATE_ROOM_GUEST_ACCESS = "m.room.guest_access";
    public static final String EVENT_TYPE_STATE_ROOM_JOIN_RULES = "m.room.join_rules";
    public static final String EVENT_TYPE_STATE_ROOM_MEMBER = "m.room.member";
    public static final String EVENT_TYPE_STATE_ROOM_NAME = "m.room.name";
    public static final String EVENT_TYPE_STATE_ROOM_POWER_LEVELS = "m.room.power_levels";
    public static final String EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE = "m.room.third_party_invite";
    public static final String EVENT_TYPE_STATE_ROOM_TOMBSTONE = "m.room.tombstone";
    public static final String EVENT_TYPE_STATE_ROOM_TOPIC = "m.room.topic";
    public static final String EVENT_TYPE_STICKER = "m.sticker";
    public static final String EVENT_TYPE_TAGS = "m.tag";
    public static final String EVENT_TYPE_TYPING = "m.typing";
    public static final String EVENT_TYPE_URL_PREVIEW = "org.matrix.room.preview_urls";
    private static final String LOG_TAG = "Event";
    static final long MAX_ORIGIN_SERVER_TS = 1125899906842624L;
    public static final String PAGINATE_BACK_TOKEN_END = "PAGINATE_BACK_TOKEN_END";
    static DateFormat mDateFormat = null;
    static long mFormatterRawOffset = 1234;
    private static final long serialVersionUID = -1431845331022808337L;
    public Long age;
    public transient JsonElement content;
    private String contentAsString;
    public String eventId;
    public List<Event> invite_room_state;
    private transient String mClaimedEd25519Key;
    private transient Event mClearEvent;
    private MXCryptoError mCryptoError;
    private transient List<String> mForwardingCurve25519KeyChain;
    public boolean mIsInternalPaginationToken;
    private String mMatrixId;
    private transient String mSenderCurve25519Key;
    public SentState mSentState;
    private long mTimeZoneRawOffset;
    public String mToken;
    public long originServerTs;
    public transient JsonElement prev_content;
    private String prev_content_as_string;
    public String redacts;
    public String roomId;
    public String sender;
    public String stateKey;
    public String type;
    public Exception unsentException;
    public MatrixError unsentMatrixError;
    public UnsignedData unsigned;
    public String userId;

    public enum SentState {
        UNSENT,
        ENCRYPTING,
        SENDING,
        WAITING_RETRY,
        SENT,
        UNDELIVERABLE,
        FAILED_UNKNOWN_DEVICES
    }

    private long getTimeZoneOffset() {
        return (long) TimeZone.getDefault().getRawOffset();
    }

    public Event() {
        this.content = null;
        this.contentAsString = null;
        this.prev_content = null;
        this.prev_content_as_string = null;
        this.unsentException = null;
        this.unsentMatrixError = null;
        this.mSentState = SentState.SENT;
        this.mTimeZoneRawOffset = 0;
        this.mForwardingCurve25519KeyChain = new ArrayList();
        this.type = null;
        this.content = null;
        this.prev_content = null;
        this.mIsInternalPaginationToken = false;
        this.eventId = null;
        this.roomId = null;
        this.userId = null;
        this.originServerTs = 0;
        this.age = null;
        this.mTimeZoneRawOffset = getTimeZoneOffset();
        this.stateKey = null;
        this.redacts = null;
        this.unsentMatrixError = null;
        this.unsentException = null;
        this.mMatrixId = null;
        this.mSentState = SentState.SENT;
    }

    public String getSender() {
        return this.sender == null ? this.userId : this.sender;
    }

    public void setSender(String str) {
        this.userId = str;
        this.sender = str;
    }

    public void setMatrixId(String str) {
        this.mMatrixId = str;
    }

    public String getMatrixId() {
        return this.mMatrixId;
    }

    public boolean isValidOriginServerTs() {
        return this.originServerTs < MAX_ORIGIN_SERVER_TS;
    }

    public long getOriginServerTs() {
        return this.originServerTs;
    }

    public void updateContent(JsonElement jsonElement) {
        this.content = jsonElement;
        this.contentAsString = null;
    }

    public boolean hasContentFields() {
        if (getContentAsJsonObject() == null) {
            return false;
        }
        Set entrySet = getContentAsJsonObject().entrySet();
        if (entrySet == null || entrySet.size() == 0) {
            return false;
        }
        return true;
    }

    public boolean isRedacted() {
        return (this.unsigned == null || this.unsigned.redacted_because == null) ? false : true;
    }

    public String formattedOriginServerTs() {
        if (!isValidOriginServerTs()) {
            return StringUtils.SPACE;
        }
        if (mDateFormat == null || mFormatterRawOffset != getTimeZoneOffset()) {
            mDateFormat = new SimpleDateFormat("MMM d HH:mm", Locale.getDefault());
            mFormatterRawOffset = getTimeZoneOffset();
        }
        return mDateFormat.format(new Date(getOriginServerTs()));
    }

    public void setOriginServerTs(long j) {
        this.originServerTs = j;
    }

    public String getType() {
        if (this.mClearEvent != null) {
            return this.mClearEvent.type;
        }
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getWireType() {
        return this.type;
    }

    public JsonElement getContent() {
        if (this.mClearEvent != null) {
            return this.mClearEvent.getWireContent();
        }
        return getWireContent();
    }

    public JsonElement getWireContent() {
        finalizeDeserialization();
        return this.content;
    }

    public JsonObject toJsonObject() {
        finalizeDeserialization();
        return JsonUtils.toJson(this);
    }

    @Nullable
    public JsonObject getContentAsJsonObject() {
        JsonElement content2 = getContent();
        if (content2 == null || !content2.isJsonObject()) {
            return null;
        }
        return content2.getAsJsonObject();
    }

    public JsonObject getPrevContentAsJsonObject() {
        finalizeDeserialization();
        if (!(this.unsigned == null || this.unsigned.prev_content == null)) {
            if (this.prev_content == null) {
                this.prev_content = this.unsigned.prev_content;
            }
            this.unsigned.prev_content = null;
        }
        if (this.prev_content == null || !this.prev_content.isJsonObject()) {
            return null;
        }
        return this.prev_content.getAsJsonObject();
    }

    public EventContent getEventContent() {
        if (getContent() != null) {
            return JsonUtils.toEventContent(getContent());
        }
        return null;
    }

    public EventContent getWireEventContent() {
        if (getWireContent() != null) {
            return JsonUtils.toEventContent(getWireContent());
        }
        return null;
    }

    public EventContent getPrevContent() {
        if (getPrevContentAsJsonObject() != null) {
            return JsonUtils.toEventContent(getPrevContentAsJsonObject());
        }
        return null;
    }

    public long getAge() {
        if (this.age != null) {
            return this.age.longValue();
        }
        if (this.unsigned == null || this.unsigned.age == null) {
            return Long.MAX_VALUE;
        }
        this.age = this.unsigned.age;
        return this.age.longValue();
    }

    public String getRedacts() {
        if (this.redacts != null) {
            return this.redacts;
        }
        if (!isRedacted()) {
            return null;
        }
        this.redacts = this.unsigned.redacted_because.redacts;
        return this.redacts;
    }

    public Event(Message message, String str, String str2) {
        this.content = null;
        this.contentAsString = null;
        this.prev_content = null;
        this.prev_content_as_string = null;
        this.unsentException = null;
        this.unsentMatrixError = null;
        this.mSentState = SentState.SENT;
        this.mTimeZoneRawOffset = 0;
        this.mForwardingCurve25519KeyChain = new ArrayList();
        this.type = EVENT_TYPE_MESSAGE;
        this.content = JsonUtils.toJson(message);
        this.originServerTs = System.currentTimeMillis();
        this.userId = str;
        this.sender = str;
        this.roomId = str2;
        this.mSentState = SentState.SENDING;
        createDummyEventId();
    }

    public Event(String str, JsonObject jsonObject, String str2, String str3) {
        this.content = null;
        this.contentAsString = null;
        this.prev_content = null;
        this.prev_content_as_string = null;
        this.unsentException = null;
        this.unsentMatrixError = null;
        this.mSentState = SentState.SENT;
        this.mTimeZoneRawOffset = 0;
        this.mForwardingCurve25519KeyChain = new ArrayList();
        this.type = str;
        this.content = jsonObject;
        this.originServerTs = System.currentTimeMillis();
        this.userId = str2;
        this.sender = str2;
        this.roomId = str3;
        this.mSentState = SentState.SENDING;
        createDummyEventId();
    }

    public void createDummyEventId() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.roomId);
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(this.originServerTs);
        this.eventId = sb.toString();
        this.age = Long.valueOf(DUMMY_EVENT_AGE);
    }

    public boolean isDummyEvent() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.roomId);
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(this.originServerTs);
        return sb.toString().equals(this.eventId);
    }

    public void setInternalPaginationToken(String str) {
        this.mToken = str;
        this.mIsInternalPaginationToken = true;
    }

    public boolean isInternalPaginationToken() {
        return this.mIsInternalPaginationToken;
    }

    public boolean hasToken() {
        return this.mToken != null && !this.mIsInternalPaginationToken;
    }

    public boolean isCallEvent() {
        return EVENT_TYPE_CALL_INVITE.equals(getType()) || EVENT_TYPE_CALL_CANDIDATES.equals(getType()) || EVENT_TYPE_CALL_ANSWER.equals(getType()) || EVENT_TYPE_CALL_HANGUP.equals(getType());
    }

    public Event deepCopy() {
        finalizeDeserialization();
        Event event = new Event();
        event.type = this.type;
        event.content = this.content;
        event.contentAsString = this.contentAsString;
        event.eventId = this.eventId;
        event.roomId = this.roomId;
        event.userId = this.userId;
        event.sender = this.sender;
        event.originServerTs = this.originServerTs;
        event.mTimeZoneRawOffset = this.mTimeZoneRawOffset;
        event.age = this.age;
        event.stateKey = this.stateKey;
        event.prev_content = this.prev_content;
        event.prev_content_as_string = this.prev_content_as_string;
        event.unsigned = this.unsigned;
        event.invite_room_state = this.invite_room_state;
        event.redacts = this.redacts;
        event.mSentState = this.mSentState;
        event.unsentException = this.unsentException;
        event.unsentMatrixError = this.unsentMatrixError;
        event.mMatrixId = this.mMatrixId;
        event.mToken = this.mToken;
        event.mIsInternalPaginationToken = this.mIsInternalPaginationToken;
        return event;
    }

    public boolean canBeResent() {
        return this.mSentState == SentState.WAITING_RETRY || this.mSentState == SentState.UNDELIVERABLE || this.mSentState == SentState.FAILED_UNKNOWN_DEVICES;
    }

    public boolean isEncrypting() {
        return this.mSentState == SentState.ENCRYPTING;
    }

    public boolean isUnsent() {
        return this.mSentState == SentState.UNSENT;
    }

    public boolean isSending() {
        return this.mSentState == SentState.SENDING || this.mSentState == SentState.WAITING_RETRY;
    }

    public boolean isUndeliverable() {
        return this.mSentState == SentState.UNDELIVERABLE;
    }

    public boolean isUnkownDevice() {
        return this.mSentState == SentState.FAILED_UNKNOWN_DEVICES;
    }

    public boolean isSent() {
        return this.mSentState == SentState.SENT;
    }

    public List<String> getMediaUrls() {
        ArrayList arrayList = new ArrayList();
        if (EVENT_TYPE_MESSAGE.equals(getType())) {
            String messageMsgType = JsonUtils.getMessageMsgType(getContent());
            if (Message.MSGTYPE_IMAGE.equals(messageMsgType)) {
                ImageMessage imageMessage = JsonUtils.toImageMessage(getContent());
                if (imageMessage.getUrl() != null) {
                    arrayList.add(imageMessage.getUrl());
                }
                if (imageMessage.getThumbnailUrl() != null) {
                    arrayList.add(imageMessage.getThumbnailUrl());
                }
            } else if (Message.MSGTYPE_FILE.equals(messageMsgType) || Message.MSGTYPE_AUDIO.equals(messageMsgType)) {
                FileMessage fileMessage = JsonUtils.toFileMessage(getContent());
                if (fileMessage.getUrl() != null) {
                    arrayList.add(fileMessage.getUrl());
                }
            } else if (Message.MSGTYPE_VIDEO.equals(messageMsgType)) {
                VideoMessage videoMessage = JsonUtils.toVideoMessage(getContent());
                if (videoMessage.getUrl() != null) {
                    arrayList.add(videoMessage.getUrl());
                }
                if (videoMessage.getThumbnailUrl() != null) {
                    arrayList.add(videoMessage.getThumbnailUrl());
                }
            } else if (Message.MSGTYPE_LOCATION.equals(messageMsgType)) {
                LocationMessage locationMessage = JsonUtils.toLocationMessage(getContent());
                if (locationMessage.thumbnail_url != null) {
                    arrayList.add(locationMessage.thumbnail_url);
                }
            }
        } else if (EVENT_TYPE_STICKER.equals(getType())) {
            StickerMessage stickerMessage = JsonUtils.toStickerMessage(getContent());
            if (stickerMessage.getUrl() != null) {
                arrayList.add(stickerMessage.getUrl());
            }
            if (stickerMessage.getThumbnailUrl() != null) {
                arrayList.add(stickerMessage.getThumbnailUrl());
            }
        }
        return arrayList;
    }

    public List<EncryptedFileInfo> getEncryptedFileInfos() {
        ArrayList arrayList = new ArrayList();
        if (!isEncrypted()) {
            return arrayList;
        }
        if (EVENT_TYPE_MESSAGE.equals(getType())) {
            String messageMsgType = JsonUtils.getMessageMsgType(getContent());
            if (Message.MSGTYPE_IMAGE.equals(messageMsgType)) {
                ImageMessage imageMessage = JsonUtils.toImageMessage(getContent());
                if (imageMessage.file != null) {
                    arrayList.add(imageMessage.file);
                }
                if (!(imageMessage.info == null || imageMessage.info.thumbnail_file == null)) {
                    arrayList.add(imageMessage.info.thumbnail_file);
                }
            } else if (Message.MSGTYPE_FILE.equals(messageMsgType) || Message.MSGTYPE_AUDIO.equals(messageMsgType)) {
                FileMessage fileMessage = JsonUtils.toFileMessage(getContent());
                if (fileMessage.file != null) {
                    arrayList.add(fileMessage.file);
                }
            } else if (Message.MSGTYPE_VIDEO.equals(messageMsgType)) {
                VideoMessage videoMessage = JsonUtils.toVideoMessage(getContent());
                if (videoMessage.file != null) {
                    arrayList.add(videoMessage.file);
                }
                if (!(videoMessage.info == null || videoMessage.info.thumbnail_file == null)) {
                    arrayList.add(videoMessage.info.thumbnail_file);
                }
            }
        } else if (EVENT_TYPE_STICKER.equals(getType())) {
            StickerMessage stickerMessage = JsonUtils.toStickerMessage(getContent());
            if (stickerMessage.file != null) {
                arrayList.add(stickerMessage.file);
            }
            if (!(stickerMessage.info == null || stickerMessage.info.thumbnail_file == null)) {
                arrayList.add(stickerMessage.info.thumbnail_file);
            }
        }
        return arrayList;
    }

    public boolean isUploadingMedias(MXMediasCache mXMediasCache) {
        for (String progressValueForUploadId : getMediaUrls()) {
            if (mXMediasCache.getProgressValueForUploadId(progressValueForUploadId) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isDownloadingMedias(MXMediasCache mXMediasCache) {
        for (String downloadIdFromUrl : getMediaUrls()) {
            if (mXMediasCache.getProgressValueForDownloadId(mXMediasCache.downloadIdFromUrl(downloadIdFromUrl)) >= 0) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"age\" : ");
        sb.append(this.age);
        sb.append(",\n");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append("  \"content\" {\n");
        String sb4 = sb3.toString();
        if (getWireContent() != null) {
            if (getWireContent().isJsonArray()) {
                Iterator it = getWireContent().getAsJsonArray().iterator();
                while (it.hasNext()) {
                    JsonElement jsonElement = (JsonElement) it.next();
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(sb4);
                    sb5.append("   ");
                    sb5.append(jsonElement.toString());
                    sb5.append(",\n");
                    sb4 = sb5.toString();
                }
            } else if (getWireContent().isJsonObject()) {
                for (Entry entry : getWireContent().getAsJsonObject().entrySet()) {
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append(sb4);
                    sb6.append("    \"");
                    sb6.append((String) entry.getKey());
                    sb6.append(": ");
                    sb6.append(((JsonElement) entry.getValue()).toString());
                    sb6.append(",\n");
                    sb4 = sb6.toString();
                }
            } else {
                StringBuilder sb7 = new StringBuilder();
                sb7.append(sb4);
                sb7.append(getWireContent().toString());
                sb4 = sb7.toString();
            }
        }
        StringBuilder sb8 = new StringBuilder();
        sb8.append(sb4);
        sb8.append("  },\n");
        String sb9 = sb8.toString();
        StringBuilder sb10 = new StringBuilder();
        sb10.append(sb9);
        sb10.append("  \"eventId\": \"");
        sb10.append(this.eventId);
        sb10.append("\",\n");
        String sb11 = sb10.toString();
        StringBuilder sb12 = new StringBuilder();
        sb12.append(sb11);
        sb12.append("  \"originServerTs\": ");
        sb12.append(this.originServerTs);
        sb12.append(",\n");
        String sb13 = sb12.toString();
        StringBuilder sb14 = new StringBuilder();
        sb14.append(sb13);
        sb14.append("  \"roomId\": \"");
        sb14.append(this.roomId);
        sb14.append("\",\n");
        String sb15 = sb14.toString();
        StringBuilder sb16 = new StringBuilder();
        sb16.append(sb15);
        sb16.append("  \"type\": \"");
        sb16.append(this.type);
        sb16.append("\",\n");
        String sb17 = sb16.toString();
        StringBuilder sb18 = new StringBuilder();
        sb18.append(sb17);
        sb18.append("  \"userId\": \"");
        sb18.append(this.userId);
        sb18.append("\"\n");
        String sb19 = sb18.toString();
        StringBuilder sb20 = new StringBuilder();
        sb20.append(sb19);
        sb20.append("  \"sender\": \"");
        sb20.append(this.sender);
        sb20.append("\"\n");
        String sb21 = sb20.toString();
        StringBuilder sb22 = new StringBuilder();
        sb22.append(sb21);
        sb22.append("  \"\n\n Sent state : ");
        String sb23 = sb22.toString();
        if (this.mSentState == SentState.UNSENT) {
            StringBuilder sb24 = new StringBuilder();
            sb24.append(sb23);
            sb24.append("UNSENT");
            sb23 = sb24.toString();
        } else if (this.mSentState == SentState.SENDING) {
            StringBuilder sb25 = new StringBuilder();
            sb25.append(sb23);
            sb25.append("SENDING");
            sb23 = sb25.toString();
        } else if (this.mSentState == SentState.WAITING_RETRY) {
            StringBuilder sb26 = new StringBuilder();
            sb26.append(sb23);
            sb26.append("WAITING_RETRY");
            sb23 = sb26.toString();
        } else if (this.mSentState == SentState.SENT) {
            StringBuilder sb27 = new StringBuilder();
            sb27.append(sb23);
            sb27.append("SENT");
            sb23 = sb27.toString();
        } else if (this.mSentState == SentState.UNDELIVERABLE) {
            StringBuilder sb28 = new StringBuilder();
            sb28.append(sb23);
            sb28.append("UNDELIVERABLE");
            sb23 = sb28.toString();
        } else if (this.mSentState == SentState.FAILED_UNKNOWN_DEVICES) {
            StringBuilder sb29 = new StringBuilder();
            sb29.append(sb23);
            sb29.append("FAILED UNKNOWN DEVICES");
            sb23 = sb29.toString();
        }
        if (this.unsentException != null) {
            StringBuilder sb30 = new StringBuilder();
            sb30.append(sb23);
            sb30.append("\n\n Exception reason: ");
            sb30.append(this.unsentException.getMessage());
            sb30.append(StringUtils.f158LF);
            sb23 = sb30.toString();
        }
        if (this.unsentMatrixError == null) {
            return sb23;
        }
        StringBuilder sb31 = new StringBuilder();
        sb31.append(sb23);
        sb31.append("\n\n Matrix reason: ");
        sb31.append(this.unsentMatrixError.getLocalizedMessage());
        sb31.append(StringUtils.f158LF);
        return sb31.toString();
    }

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if (objectInput.readBoolean()) {
            this.type = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.contentAsString = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.prev_content_as_string = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.eventId = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.roomId = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.userId = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.sender = objectInput.readUTF();
        }
        this.originServerTs = objectInput.readLong();
        if (objectInput.readBoolean()) {
            this.age = Long.valueOf(objectInput.readLong());
        }
        if (objectInput.readBoolean()) {
            this.stateKey = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.unsigned = (UnsignedData) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.redacts = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.invite_room_state = (List) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.unsentException = (Exception) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.unsentMatrixError = (MatrixError) objectInput.readObject();
        }
        this.mSentState = (SentState) objectInput.readObject();
        if (objectInput.readBoolean()) {
            this.mToken = objectInput.readUTF();
        }
        this.mIsInternalPaginationToken = objectInput.readBoolean();
        if (objectInput.readBoolean()) {
            this.mMatrixId = objectInput.readUTF();
        }
        this.mTimeZoneRawOffset = objectInput.readLong();
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        prepareSerialization();
        boolean z = false;
        objectOutput.writeBoolean(this.type != null);
        if (this.type != null) {
            objectOutput.writeUTF(this.type);
        }
        objectOutput.writeBoolean(this.contentAsString != null);
        if (this.contentAsString != null) {
            objectOutput.writeUTF(this.contentAsString);
        }
        objectOutput.writeBoolean(this.prev_content_as_string != null);
        if (this.prev_content_as_string != null) {
            objectOutput.writeUTF(this.prev_content_as_string);
        }
        objectOutput.writeBoolean(this.eventId != null);
        if (this.eventId != null) {
            objectOutput.writeUTF(this.eventId);
        }
        objectOutput.writeBoolean(this.roomId != null);
        if (this.roomId != null) {
            objectOutput.writeUTF(this.roomId);
        }
        objectOutput.writeBoolean(this.userId != null);
        if (this.userId != null) {
            objectOutput.writeUTF(this.userId);
        }
        objectOutput.writeBoolean(this.sender != null);
        if (this.sender != null) {
            objectOutput.writeUTF(this.sender);
        }
        objectOutput.writeLong(this.originServerTs);
        objectOutput.writeBoolean(this.age != null);
        if (this.age != null) {
            objectOutput.writeLong(this.age.longValue());
        }
        objectOutput.writeBoolean(this.stateKey != null);
        if (this.stateKey != null) {
            objectOutput.writeUTF(this.stateKey);
        }
        objectOutput.writeBoolean(this.unsigned != null);
        if (this.unsigned != null) {
            objectOutput.writeObject(this.unsigned);
        }
        objectOutput.writeBoolean(this.redacts != null);
        if (this.redacts != null) {
            objectOutput.writeUTF(this.redacts);
        }
        objectOutput.writeBoolean(this.invite_room_state != null);
        if (this.invite_room_state != null) {
            objectOutput.writeObject(this.invite_room_state);
        }
        objectOutput.writeBoolean(this.unsentException != null);
        if (this.unsentException != null) {
            objectOutput.writeObject(this.unsentException);
        }
        objectOutput.writeBoolean(this.unsentMatrixError != null);
        if (this.unsentMatrixError != null) {
            objectOutput.writeObject(this.unsentMatrixError);
        }
        objectOutput.writeObject(this.mSentState);
        objectOutput.writeBoolean(this.mToken != null);
        if (this.mToken != null) {
            objectOutput.writeUTF(this.mToken);
        }
        objectOutput.writeBoolean(this.mIsInternalPaginationToken);
        if (this.mMatrixId != null) {
            z = true;
        }
        objectOutput.writeBoolean(z);
        if (this.mMatrixId != null) {
            objectOutput.writeUTF(this.mMatrixId);
        }
        objectOutput.writeLong(this.mTimeZoneRawOffset);
    }

    private void prepareSerialization() {
        if (this.content != null && this.contentAsString == null) {
            this.contentAsString = this.content.toString();
        }
        if (getPrevContentAsJsonObject() != null && this.prev_content_as_string == null) {
            this.prev_content_as_string = getPrevContentAsJsonObject().toString();
        }
        if (this.unsigned != null && this.unsigned.prev_content != null) {
            this.unsigned.prev_content = null;
        }
    }

    private void finalizeDeserialization() {
        if (this.contentAsString != null && this.content == null) {
            try {
                this.content = new JsonParser().parse(this.contentAsString).getAsJsonObject();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("finalizeDeserialization : contentAsString deserialization ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
                this.contentAsString = null;
            }
        }
        if (this.prev_content_as_string != null && this.prev_content == null) {
            try {
                this.prev_content = new JsonParser().parse(this.prev_content_as_string).getAsJsonObject();
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("finalizeDeserialization : prev_content_as_string deserialization ");
                sb2.append(e2.getMessage());
                Log.m211e(str2, sb2.toString());
                this.prev_content_as_string = null;
            }
        }
    }

    private static JsonObject filterInContentWithKeys(JsonObject jsonObject, ArrayList<String> arrayList) {
        if (jsonObject == null) {
            return null;
        }
        JsonObject jsonObject2 = new JsonObject();
        if (arrayList == null || arrayList.size() == 0) {
            return new JsonObject();
        }
        Set<Entry> entrySet = jsonObject.entrySet();
        if (entrySet != null) {
            for (Entry entry : entrySet) {
                if (arrayList.indexOf(entry.getKey()) >= 0) {
                    jsonObject2.add((String) entry.getKey(), (JsonElement) entry.getValue());
                }
            }
        }
        return jsonObject2;
    }

    public void prune(Event event) {
        ArrayList arrayList;
        if (TextUtils.equals(EVENT_TYPE_STATE_ROOM_MEMBER, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"membership"}));
        } else if (TextUtils.equals(EVENT_TYPE_STATE_ROOM_CREATE, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"creator"}));
        } else if (TextUtils.equals(EVENT_TYPE_STATE_ROOM_JOIN_RULES, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"join_rule"}));
        } else if (TextUtils.equals(EVENT_TYPE_STATE_ROOM_POWER_LEVELS, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"users", "users_default", "events", "events_default", "state_default", RoomMember.MEMBERSHIP_BAN, RoomMember.MEMBERSHIP_KICK, "redact", "invite"}));
        } else if (TextUtils.equals(EVENT_TYPE_STATE_ROOM_ALIASES, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"aliases"}));
        } else if (TextUtils.equals(EVENT_TYPE_STATE_CANONICAL_ALIAS, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"alias"}));
        } else if (TextUtils.equals(EVENT_TYPE_FEEDBACK, this.type)) {
            arrayList = new ArrayList(Arrays.asList(new String[]{"type", "target_event_id"}));
        } else {
            if (TextUtils.equals(EVENT_TYPE_MESSAGE_ENCRYPTED, this.type)) {
                this.mClearEvent = null;
            }
            arrayList = null;
        }
        this.content = filterInContentWithKeys(getContentAsJsonObject(), arrayList);
        this.prev_content = filterInContentWithKeys(getPrevContentAsJsonObject(), arrayList);
        this.prev_content_as_string = null;
        this.contentAsString = null;
        if (event != null) {
            if (this.unsigned == null) {
                this.unsigned = new UnsignedData();
            }
            this.unsigned.redacted_because = new RedactedBecause();
            this.unsigned.redacted_because.type = event.type;
            this.unsigned.redacted_because.origin_server_ts = event.originServerTs;
            this.unsigned.redacted_because.sender = event.sender;
            this.unsigned.redacted_because.event_id = event.eventId;
            this.unsigned.redacted_because.unsigned = event.unsigned;
            this.unsigned.redacted_because.redacts = event.redacts;
            this.unsigned.redacted_because.content = new RedactedContent();
            JsonObject contentAsJsonObject = getContentAsJsonObject();
            if (contentAsJsonObject != null && contentAsJsonObject.has("reason")) {
                try {
                    this.unsigned.redacted_because.content.reason = contentAsJsonObject.get("reason").getAsString();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("unsigned.redacted_because.content.reason failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    public boolean isEncrypted() {
        return TextUtils.equals(getWireType(), EVENT_TYPE_MESSAGE_ENCRYPTED);
    }

    public void setClearData(@Nullable MXEventDecryptionResult mXEventDecryptionResult) {
        this.mClearEvent = null;
        if (mXEventDecryptionResult != null) {
            if (mXEventDecryptionResult.mClearEvent != null) {
                this.mClearEvent = JsonUtils.toEvent(mXEventDecryptionResult.mClearEvent);
            }
            if (this.mClearEvent != null) {
                this.mClearEvent.mSenderCurve25519Key = mXEventDecryptionResult.mSenderCurve25519Key;
                this.mClearEvent.mClaimedEd25519Key = mXEventDecryptionResult.mClaimedEd25519Key;
                if (mXEventDecryptionResult.mForwardingCurve25519KeyChain != null) {
                    this.mClearEvent.mForwardingCurve25519KeyChain = mXEventDecryptionResult.mForwardingCurve25519KeyChain;
                } else {
                    this.mClearEvent.mForwardingCurve25519KeyChain = new ArrayList();
                }
                try {
                    if (getWireContent().getAsJsonObject().has("m.relates_to")) {
                        this.mClearEvent.getContentAsJsonObject().add("m.relates_to", getWireContent().getAsJsonObject().get("m.relates_to"));
                    }
                } catch (Exception e) {
                    Log.m212e(LOG_TAG, "Unable to restore 'm.relates_to' the clear event", e);
                }
            }
            this.mCryptoError = null;
        }
    }

    public String senderKey() {
        if (this.mClearEvent != null) {
            return this.mClearEvent.mSenderCurve25519Key;
        }
        return this.mSenderCurve25519Key;
    }

    public Map<String, String> getKeysClaimed() {
        HashMap hashMap = new HashMap();
        String str = getClearEvent() != null ? getClearEvent().mClaimedEd25519Key : this.mClaimedEd25519Key;
        if (str != null) {
            hashMap.put(OlmAccount.JSON_KEY_FINGER_PRINT_KEY, str);
        }
        return hashMap;
    }

    public MXCryptoError getCryptoError() {
        return this.mCryptoError;
    }

    public void setCryptoError(MXCryptoError mXCryptoError) {
        this.mCryptoError = mXCryptoError;
        if (mXCryptoError != null) {
            this.mClearEvent = null;
        }
    }

    public Event getClearEvent() {
        return this.mClearEvent;
    }
}
