package com.opengarden.firechat.matrixsdk.rest.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.model.BannedUser;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomParams;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContext;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.ReportContentParams;
import com.opengarden.firechat.matrixsdk.rest.model.RoomAliasDescription;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Typing;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomResponse;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RoomsApi {
    @PUT("user/{userId}/rooms/{roomId}/tags/{tag}")
    Call<Void> addTag(@Path("userId") String str, @Path("roomId") String str2, @Path("tag") String str3, @Body HashMap<String, Object> hashMap);

    @POST("rooms/{roomId}/ban")
    Call<Void> ban(@Path("roomId") String str, @Body BannedUser bannedUser);

    @POST("createRoom")
    Call<CreateRoomResponse> createRoom(@Body CreateRoomParams createRoomParams);

    @POST("rooms/{roomId}/forget")
    Call<Void> forget(@Path("roomId") String str, @Body JsonObject jsonObject);

    @PUT("rooms/{roomId}/send/{eventType}/{txId}")
    Call<Event> forwardMessage(@Path("txId") String str, @Path("roomId") String str2, @Path("eventType") String str3, @Query("access_token") String str4, @Body JsonObject jsonObject);

    @GET("rooms/{roomId}/context/{eventId}")
    Call<EventContext> getContextOfEvent(@Path("roomId") String str, @Path("eventId") String str2, @Query("limit") int i);

    @GET("events/{eventId}")
    Call<Event> getEvent(@Path("eventId") String str);

    @GET("rooms/{roomId}/event/{eventId}")
    Call<Event> getEvent(@Path("roomId") String str, @Path("eventId") String str2);

    @GET("directory/list/room/{roomId}")
    Call<RoomState> getRoomDirectoryVisibility(@Path("roomId") String str);

    @GET("directory/room/{roomAlias}")
    Call<RoomAliasDescription> getRoomIdByAlias(@Path("roomAlias") String str);

    @GET("rooms/{roomId}/messages")
    Call<TokensChunkResponse<Event>> getRoomMessagesFrom(@Path("roomId") String str, @Query("dir") String str2, @Query("from") String str3, @Query("limit") int i);

    @GET("rooms/{roomId}/state/{eventType}")
    Call<JsonElement> getStateEvent(@Path("roomId") String str, @Path("eventType") String str2);

    @GET("rooms/{roomId}/state/{eventType}/{stateKey}")
    Call<JsonElement> getStateEvent(@Path("roomId") String str, @Path("eventType") String str2, @Path("stateKey") String str3);

    @GET("rooms/{roomId}/initialSync")
    Call<RoomResponse> initialSync(@Path("roomId") String str, @Query("limit") int i);

    @POST("rooms/{roomId}/invite")
    Call<Void> invite(@Path("roomId") String str, @Body User user);

    @POST("rooms/{roomId}/invite")
    Call<Void> invite(@Path("roomId") String str, @Body HashMap<String, String> hashMap);

    @POST("rooms/{roomId}/join")
    Call<Void> join(@Path("roomId") String str, @Body JsonObject jsonObject);

    @POST("join/{roomAliasOrId}")
    Call<RoomResponse> joinRoomByAliasOrId(@Path("roomAliasOrId") String str, @Body HashMap<String, Object> hashMap);

    @POST("rooms/{roomId}/leave")
    Call<Void> leave(@Path("roomId") String str, @Body JsonObject jsonObject);

    @POST("rooms/{roomId}/redact/{eventId}")
    Call<Event> redactEvent(@Path("roomId") String str, @Path("eventId") String str2, @Body JsonObject jsonObject);

    @DELETE("directory/room/{roomAlias}")
    Call<Void> removeRoomAlias(@Path("roomAlias") String str);

    @DELETE("user/{userId}/rooms/{roomId}/tags/{tag}")
    Call<Void> removeTag(@Path("userId") String str, @Path("roomId") String str2, @Path("tag") String str3);

    @POST("rooms/{roomId}/report/{eventId}")
    Call<Void> reportEvent(@Path("roomId") String str, @Path("eventId") String str2, @Body ReportContentParams reportContentParams);

    @PUT("rooms/{roomId}/send/{eventType}/{txId}")
    Call<Event> send(@Path("txId") String str, @Path("roomId") String str2, @Path("eventType") String str3, @Body JsonObject jsonObject);

    @PUT("rooms/{roomId}/send/m.room.message/{txId}")
    Call<Event> sendMessage(@Path("txId") String str, @Path("roomId") String str2, @Body Message message);

    @POST("rooms/{roomId}/read_markers")
    Call<Void> sendReadMarker(@Path("roomId") String str, @Body Map<String, String> map);

    @POST("rooms/{roomId}/receipt/m.read/{eventId}")
    Call<Void> sendReadReceipt(@Path("roomId") String str, @Path("eventId") String str2, @Body JsonObject jsonObject);

    @PUT("rooms/{roomId}/state/{state_event_type}/{stateKey}")
    Call<Void> sendStateEvent(@Path("roomId") String str, @Path("state_event_type") String str2, @Path("stateKey") String str3, @Body Map<String, Object> map);

    @PUT("rooms/{roomId}/state/{state_event_type}")
    Call<Void> sendStateEvent(@Path("roomId") String str, @Path("state_event_type") String str2, @Body Map<String, Object> map);

    @PUT("rooms/{roomId}/state/m.room.canonical_alias")
    Call<Void> setCanonicalAlias(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/state/m.room.guest_access")
    Call<Void> setGuestAccess(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/state/m.room.history_visibility")
    Call<Void> setHistoryVisibility(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/state/m.room.join_rules")
    Call<Void> setJoinRules(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/state/m.room.power_levels")
    Call<Void> setPowerLevels(@Path("roomId") String str, @Body PowerLevels powerLevels);

    @PUT("rooms/{roomId}/state/m.room.avatar")
    Call<Void> setRoomAvatarUrl(@Path("roomId") String str, @Body HashMap<String, String> hashMap);

    @PUT("directory/list/room/{roomId}")
    Call<Void> setRoomDirectoryVisibility(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("directory/room/{roomAlias}")
    Call<Void> setRoomIdByAlias(@Path("roomAlias") String str, @Body RoomAliasDescription roomAliasDescription);

    @PUT("rooms/{roomId}/state/m.room.name")
    Call<Void> setRoomName(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/state/m.room.topic")
    Call<Void> setRoomTopic(@Path("roomId") String str, @Body RoomState roomState);

    @PUT("rooms/{roomId}/typing/{userId}")
    Call<Void> setTypingNotification(@Path("roomId") String str, @Path("userId") String str2, @Body Typing typing);

    @POST("rooms/{roomId}/unban")
    Call<Void> unban(@Path("roomId") String str, @Body BannedUser bannedUser);

    @PUT("user/{userId}/rooms/{roomId}/account_data/{tag}")
    Call<Void> updateAccountData(@Path("userId") String str, @Path("roomId") String str2, @Path("tag") String str3, @Body HashMap<String, Object> hashMap);

    @PUT("rooms/{roomId}/state/m.room.member/{userId}")
    Call<Void> updateRoomMember(@Path("roomId") String str, @Path("userId") String str2, @Body RoomMember roomMember);
}
