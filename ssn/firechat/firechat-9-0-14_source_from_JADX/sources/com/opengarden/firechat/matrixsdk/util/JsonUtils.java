package com.opengarden.firechat.matrixsdk.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.json.ConditionDeserializer;
import com.opengarden.firechat.matrixsdk.rest.model.ContentResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomCreateContent;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.RoomTags;
import com.opengarden.firechat.matrixsdk.rest.model.RoomTombstoneContent;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.Condition;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.ForwardedRoomKeyContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.OlmEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.OlmPayloadContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.RoomKeyRequest;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse;
import com.opengarden.firechat.matrixsdk.rest.model.message.AudioMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.LocationMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.StickerJsonMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.rest.model.pid.RoomThirdPartyInvite;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class JsonUtils {
    private static final String LOG_TAG = "JsonUtils";
    private static final Gson gson = new GsonBuilder().setFieldNamingStrategy(new MatrixFieldNamingStrategy()).excludeFieldsWithModifiers(2, 8).registerTypeAdapter(Condition.class, new ConditionDeserializer()).create();
    private static final Gson gsonWithNullSerialization = new GsonBuilder().setFieldNamingStrategy(new MatrixFieldNamingStrategy()).excludeFieldsWithModifiers(2, 8).serializeNulls().registerTypeAdapter(Condition.class, new ConditionDeserializer()).create();
    private static final Gson gsonWithoutHtmlEscaping = new GsonBuilder().setFieldNamingStrategy(new MatrixFieldNamingStrategy()).disableHtmlEscaping().excludeFieldsWithModifiers(2, 8).registerTypeAdapter(Condition.class, new ConditionDeserializer()).create();

    public static class MatrixFieldNamingStrategy implements FieldNamingStrategy {
        private static String separateCamelCase(String str, String str2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (Character.isUpperCase(charAt) && sb.length() != 0) {
                    sb.append(str2);
                }
                sb.append(charAt);
            }
            return sb.toString();
        }

        public String translateName(Field field) {
            return separateCamelCase(field.getName(), "_").toLowerCase(Locale.ENGLISH);
        }
    }

    public static Gson getGson(boolean z) {
        return z ? gsonWithNullSerialization : gson;
    }

    public static RoomState toRoomState(JsonElement jsonElement) {
        return (RoomState) toClass(jsonElement, RoomState.class);
    }

    public static User toUser(JsonElement jsonElement) {
        return (User) toClass(jsonElement, User.class);
    }

    public static RoomMember toRoomMember(JsonElement jsonElement) {
        return (RoomMember) toClass(jsonElement, RoomMember.class);
    }

    public static RoomTags toRoomTags(JsonElement jsonElement) {
        return (RoomTags) toClass(jsonElement, RoomTags.class);
    }

    public static MatrixError toMatrixError(JsonElement jsonElement) {
        return (MatrixError) toClass(jsonElement, MatrixError.class);
    }

    @Nullable
    public static String getMessageMsgType(JsonElement jsonElement) {
        try {
            return ((Message) gson.fromJson(jsonElement, Message.class)).msgtype;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getMessageMsgType failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    @NonNull
    public static Message toMessage(JsonElement jsonElement) {
        try {
            Message message = (Message) gson.fromJson(jsonElement, Message.class);
            if (Message.MSGTYPE_IMAGE.equals(message.msgtype)) {
                return toImageMessage(jsonElement);
            }
            if (Message.MSGTYPE_VIDEO.equals(message.msgtype)) {
                return toVideoMessage(jsonElement);
            }
            if (Message.MSGTYPE_LOCATION.equals(message.msgtype)) {
                return toLocationMessage(jsonElement);
            }
            if (Message.MSGTYPE_FILE.equals(message.msgtype)) {
                return toFileMessage(jsonElement);
            }
            return Message.MSGTYPE_AUDIO.equals(message.msgtype) ? toAudioMessage(jsonElement) : message;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toMessage failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return new Message();
        }
    }

    public static Event toEvent(JsonElement jsonElement) {
        return (Event) toClass(jsonElement, Event.class);
    }

    public static EncryptedEventContent toEncryptedEventContent(JsonElement jsonElement) {
        return (EncryptedEventContent) toClass(jsonElement, EncryptedEventContent.class);
    }

    public static OlmEventContent toOlmEventContent(JsonElement jsonElement) {
        return (OlmEventContent) toClass(jsonElement, OlmEventContent.class);
    }

    public static OlmPayloadContent toOlmPayloadContent(JsonElement jsonElement) {
        return (OlmPayloadContent) toClass(jsonElement, OlmPayloadContent.class);
    }

    public static EventContent toEventContent(JsonElement jsonElement) {
        return (EventContent) toClass(jsonElement, EventContent.class);
    }

    public static RoomKeyContent toRoomKeyContent(JsonElement jsonElement) {
        return (RoomKeyContent) toClass(jsonElement, RoomKeyContent.class);
    }

    public static RoomKeyRequest toRoomKeyRequest(JsonElement jsonElement) {
        return (RoomKeyRequest) toClass(jsonElement, RoomKeyRequest.class);
    }

    public static ForwardedRoomKeyContent toForwardedRoomKeyContent(JsonElement jsonElement) {
        return (ForwardedRoomKeyContent) toClass(jsonElement, ForwardedRoomKeyContent.class);
    }

    public static ImageMessage toImageMessage(JsonElement jsonElement) {
        return (ImageMessage) toClass(jsonElement, ImageMessage.class);
    }

    public static StickerMessage toStickerMessage(JsonElement jsonElement) {
        return new StickerMessage((StickerJsonMessage) toClass(jsonElement, StickerJsonMessage.class));
    }

    public static FileMessage toFileMessage(JsonElement jsonElement) {
        return (FileMessage) toClass(jsonElement, FileMessage.class);
    }

    public static AudioMessage toAudioMessage(JsonElement jsonElement) {
        return (AudioMessage) toClass(jsonElement, AudioMessage.class);
    }

    public static VideoMessage toVideoMessage(JsonElement jsonElement) {
        return (VideoMessage) toClass(jsonElement, VideoMessage.class);
    }

    public static LocationMessage toLocationMessage(JsonElement jsonElement) {
        return (LocationMessage) toClass(jsonElement, LocationMessage.class);
    }

    public static ContentResponse toContentResponse(String str) {
        return (ContentResponse) toClass(str, ContentResponse.class);
    }

    public static PowerLevels toPowerLevels(JsonElement jsonElement) {
        return (PowerLevels) toClass(jsonElement, PowerLevels.class);
    }

    public static RoomThirdPartyInvite toRoomThirdPartyInvite(JsonElement jsonElement) {
        return (RoomThirdPartyInvite) toClass(jsonElement, RoomThirdPartyInvite.class);
    }

    public static RegistrationFlowResponse toRegistrationFlowResponse(String str) {
        return (RegistrationFlowResponse) toClass(str, RegistrationFlowResponse.class);
    }

    public static RoomTombstoneContent toRoomTombstoneContent(JsonElement jsonElement) {
        return (RoomTombstoneContent) toClass(jsonElement, RoomTombstoneContent.class);
    }

    public static RoomCreateContent toRoomCreateContent(JsonElement jsonElement) {
        return (RoomCreateContent) toClass(jsonElement, RoomCreateContent.class);
    }

    public static <T> T toClass(JsonElement jsonElement, Class<T> cls) {
        T t;
        try {
            t = gson.fromJson(jsonElement, cls);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toClass failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            t = null;
        }
        if (t != null) {
            return t;
        }
        cls.getConstructors();
        try {
            return cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable th) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## toClass failed ");
            sb2.append(th.getMessage());
            Log.m211e(str2, sb2.toString());
            return t;
        }
    }

    public static <T> T toClass(String str, Class<T> cls) {
        T t;
        try {
            t = gson.fromJson(str, cls);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toClass failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            t = null;
        }
        if (t != null) {
            return t;
        }
        cls.getConstructors();
        try {
            return cls.getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable th) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## toClass failed ");
            sb2.append(th.getMessage());
            Log.m211e(str3, sb2.toString());
            return t;
        }
    }

    public static JsonObject toJson(Event event) {
        try {
            return (JsonObject) gson.toJsonTree(event);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toJson failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return new JsonObject();
        }
    }

    public static JsonObject toJson(Message message) {
        try {
            return (JsonObject) gson.toJsonTree(message);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toJson failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    public static JsonObject toJson(MXUsersDevicesMap mXUsersDevicesMap) {
        try {
            return (JsonObject) gson.toJsonTree(mXUsersDevicesMap.getMap());
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## toJson failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    public static String getCanonicalizedJsonString(Object obj) {
        String str;
        if (obj == null) {
            return null;
        }
        if (obj instanceof JsonElement) {
            str = gsonWithoutHtmlEscaping.toJson(canonicalize((JsonElement) obj));
        } else {
            str = gsonWithoutHtmlEscaping.toJson(canonicalize(gsonWithoutHtmlEscaping.toJsonTree(obj)));
        }
        return str != null ? str.replace("\\/", "/") : str;
    }

    public static JsonElement canonicalize(JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }
        if (jsonElement instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) jsonElement;
            JsonArray jsonArray2 = new JsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonArray2.add(canonicalize(jsonArray.get(i)));
            }
            return jsonArray2;
        } else if (!(jsonElement instanceof JsonObject)) {
            return jsonElement;
        } else {
            JsonObject jsonObject = (JsonObject) jsonElement;
            JsonObject jsonObject2 = new JsonObject();
            TreeSet treeSet = new TreeSet();
            for (Entry key : jsonObject.entrySet()) {
                treeSet.add(key.getKey());
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                String str = (String) it.next();
                jsonObject2.add(str, canonicalize(jsonObject.get(str)));
            }
            return jsonObject2;
        }
    }

    public static String convertFromUTF8(String str) {
        if (str == null) {
            return str;
        }
        try {
            return new String(str.getBytes(), "UTF-8");
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## convertFromUTF8()  failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return str;
        }
    }

    public static String convertToUTF8(String str) {
        if (str == null) {
            return str;
        }
        try {
            return new String(str.getBytes("UTF-8"));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## convertToUTF8()  failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return str;
        }
    }

    @Nullable
    public static String getAsString(Map<String, Object> map, String str) {
        if (!map.containsKey(str) || !(map.get(str) instanceof String)) {
            return null;
        }
        return (String) map.get(str);
    }
}
