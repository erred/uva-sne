package com.opengarden.firechat.matrixsdk.rest.client;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.RestClient;
import com.opengarden.firechat.matrixsdk.RestClient.EndPointServer;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.api.RoomsApi;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.RestAdapterCallback.RequestRetryCallBack;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.BannedUser;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomParams;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContext;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.ReportContentParams;
import com.opengarden.firechat.matrixsdk.rest.model.RoomAliasDescription;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.Typing;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.login.PasswordLoginParams;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomResponse;
import com.opengarden.firechat.matrixsdk.util.UnsentEventsManager;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

public class RoomsRestClient extends RestClient<RoomsApi> {
    public static final int DEFAULT_MESSAGES_PAGINATION_LIMIT = 30;
    private static final String LOG_TAG = "RoomsRestClient";
    private static final String READ_MARKER_FULLY_READ = "m.fully_read";
    private static final String READ_MARKER_READ = "m.read";

    public RoomsRestClient(HomeServerConnectionConfig homeServerConnectionConfig) {
        super(homeServerConnectionConfig, RoomsApi.class, RestClient.URI_API_PREFIX_PATH_R0, false);
    }

    public RoomsRestClient(HomeServerConnectionConfig homeServerConnectionConfig, Boolean bool) {
        super(homeServerConnectionConfig, RoomsApi.class, RestClient.URI_API_PREFIX_PATH_R0, false, EndPointServer.HOME_SERVER, false);
    }

    public void sendMessage(String str, String str2, Message message, ApiCallback<Event> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("SendMessage : roomId ");
        sb.append(str2);
        String sb2 = sb.toString();
        Call sendMessage = ((RoomsApi) this.mApi).sendMessage(str, str2, message);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str3 = str;
        final String str4 = str2;
        final Message message2 = message;
        final ApiCallback<Event> apiCallback2 = apiCallback;
        C27981 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.sendMessage(str3, str4, message2, apiCallback2);
            }
        };
        sendMessage.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void sendEventToRoom(String str, String str2, String str3, JsonObject jsonObject, ApiCallback<Event> apiCallback) {
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final JsonObject jsonObject2 = jsonObject;
        ApiCallback<Event> apiCallback2 = apiCallback;
        StringBuilder sb = new StringBuilder();
        sb.append("sendEvent : roomId ");
        sb.append(str5);
        sb.append(" - eventType ");
        sb.append(str6);
        String sb2 = sb.toString();
        if (!TextUtils.equals(str6, Event.EVENT_TYPE_CALL_INVITE)) {
            Call send = ((RoomsApi) this.mApi).send(str4, str5, str6, jsonObject2);
            UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
            final ApiCallback<Event> apiCallback3 = apiCallback2;
            C28112 r0 = new RequestRetryCallBack() {
                public void onRetry() {
                    RoomsRestClient.this.sendEventToRoom(str4, str5, str6, jsonObject2, apiCallback3);
                }
            };
            send.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback2, r0));
            return;
        }
        ((RoomsApi) this.mApi).send(str4, str5, str6, jsonObject2).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback2, null));
    }

    public void forwardEvent(String str, String str2, String str3, String str4, JsonObject jsonObject, ApiCallback<Event> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("sendEvent : roomId ");
        String str5 = str2;
        sb.append(str5);
        sb.append(" - eventType ");
        String str6 = str3;
        sb.append(str6);
        String sb2 = sb.toString();
        Call forwardMessage = ((RoomsApi) this.mApi).forwardMessage(str, str5, str6, str4, jsonObject);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str7 = str;
        final String str8 = str5;
        final String str9 = str6;
        final String str10 = str4;
        final JsonObject jsonObject2 = jsonObject;
        final ApiCallback<Event> apiCallback2 = apiCallback;
        C28223 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.forwardEvent(str7, str8, str9, str10, jsonObject2, apiCallback2);
            }
        };
        forwardMessage.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r0));
    }

    public void getRoomMessagesFrom(String str, String str2, Direction direction, int i, ApiCallback<TokensChunkResponse<Event>> apiCallback) {
        final String str3 = str;
        final String str4 = str2;
        final Direction direction2 = direction;
        final int i2 = i;
        StringBuilder sb = new StringBuilder();
        sb.append("messagesFrom : roomId ");
        sb.append(str3);
        sb.append(" fromToken ");
        sb.append(str4);
        sb.append("with direction ");
        sb.append(direction2);
        sb.append(" with limit ");
        sb.append(i2);
        String sb2 = sb.toString();
        Call roomMessagesFrom = ((RoomsApi) this.mApi).getRoomMessagesFrom(str3, direction2 == Direction.BACKWARDS ? "b" : "f", str4, i2);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<TokensChunkResponse<Event>> apiCallback2 = apiCallback;
        C28334 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getRoomMessagesFrom(str3, str4, direction2, i2, apiCallback2);
            }
        };
        roomMessagesFrom.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r0));
    }

    public void inviteUserToRoom(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("inviteToRoom : roomId ");
        sb.append(str);
        sb.append(" userId ");
        sb.append(str2);
        String sb2 = sb.toString();
        User user = new User();
        user.user_id = str2;
        ((RoomsApi) this.mApi).invite(str, user).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.inviteUserToRoom(str, str2, apiCallback);
            }
        }));
    }

    public void inviteByEmailToRoom(String str, String str2, ApiCallback<Void> apiCallback) {
        inviteThreePidToRoom("email", str2, str, apiCallback);
    }

    /* access modifiers changed from: private */
    public void inviteThreePidToRoom(String str, String str2, String str3, ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("inviteThreePidToRoom : medium ");
        sb.append(str);
        sb.append(" roomId ");
        sb.append(str3);
        String sb2 = sb.toString();
        String uri = this.mHsConfig.getIdentityServerUri().toString();
        if (uri.startsWith("http://")) {
            uri = uri.substring("http://".length());
        } else if (uri.startsWith("https://")) {
            uri = uri.substring("https://".length());
        }
        HashMap hashMap = new HashMap();
        hashMap.put("id_server", uri);
        hashMap.put("medium", str);
        hashMap.put(PasswordLoginParams.IDENTIFIER_KEY_ADDRESS, str2);
        Call invite = ((RoomsApi) this.mApi).invite(str3, hashMap);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C28376 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.inviteThreePidToRoom(str4, str5, str6, apiCallback2);
            }
        };
        invite.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void joinRoom(String str, ApiCallback<RoomResponse> apiCallback) {
        joinRoom(str, null, apiCallback);
    }

    public void joinRoom(final String str, final HashMap<String, Object> hashMap, final ApiCallback<RoomResponse> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("joinRoom : roomId ");
        sb.append(str);
        ((RoomsApi) this.mApi).joinRoomByAliasOrId(str, hashMap == null ? new HashMap<>() : hashMap).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.joinRoom(str, hashMap, apiCallback);
            }
        }));
    }

    public void leaveRoom(final String str, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("leaveRoom : roomId ");
        sb.append(str);
        ((RoomsApi) this.mApi).leave(str, new JsonObject()).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.leaveRoom(str, apiCallback);
            }
        }));
    }

    public void forgetRoom(final String str, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("forgetRoom : roomId ");
        sb.append(str);
        ((RoomsApi) this.mApi).forget(str, new JsonObject()).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.forgetRoom(str, apiCallback);
            }
        }));
    }

    public void kickFromRoom(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("kickFromRoom : roomId ");
        sb.append(str);
        sb.append(" userId ");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomMember roomMember = new RoomMember();
        roomMember.membership = RoomMember.MEMBERSHIP_LEAVE;
        ((RoomsApi) this.mApi).updateRoomMember(str, str2, roomMember).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.kickFromRoom(str, str2, apiCallback);
            }
        }));
    }

    public void banFromRoom(final String str, final BannedUser bannedUser, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("banFromRoom : roomId ");
        sb.append(str);
        sb.append(" userId ");
        sb.append(bannedUser.userId);
        ((RoomsApi) this.mApi).ban(str, bannedUser).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.banFromRoom(str, bannedUser, apiCallback);
            }
        }));
    }

    public void unbanFromRoom(final String str, final BannedUser bannedUser, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("Unban : roomId ");
        sb.append(str);
        sb.append(" userId ");
        sb.append(bannedUser.userId);
        ((RoomsApi) this.mApi).unban(str, bannedUser).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.unbanFromRoom(str, bannedUser, apiCallback);
            }
        }));
    }

    public void createRoom(final CreateRoomParams createRoomParams, final ApiCallback<CreateRoomResponse> apiCallback) {
        ((RoomsApi) this.mApi).createRoom(createRoomParams).enqueue(new RestAdapterCallback("createRoom", this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.createRoom(createRoomParams, apiCallback);
            }
        }));
    }

    public void initialSync(final String str, final ApiCallback<RoomResponse> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("initialSync : roomId ");
        sb.append(str);
        ((RoomsApi) this.mApi).initialSync(str, 30).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.initialSync(str, apiCallback);
            }
        }));
    }

    public void getEvent(String str, String str2, ApiCallback<Event> apiCallback) {
        final ApiCallback<Event> apiCallback2 = apiCallback;
        final String str3 = str2;
        final String str4 = str;
        C280415 r0 = new SimpleApiCallback<Event>(apiCallback) {
            public void onSuccess(Event event) {
                apiCallback2.onSuccess(event);
            }

            public void onMatrixError(MatrixError matrixError) {
                if (TextUtils.equals(matrixError.errcode, MatrixError.UNRECOGNIZED)) {
                    RoomsRestClient.this.getEventFromEventId(str3, new SimpleApiCallback<Event>(apiCallback2) {
                        public void onSuccess(Event event) {
                            apiCallback2.onSuccess(event);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (TextUtils.equals(matrixError.errcode, MatrixError.UNRECOGNIZED)) {
                                RoomsRestClient.this.getContextOfEvent(str4, str3, 1, new SimpleApiCallback<EventContext>(apiCallback2) {
                                    public void onSuccess(EventContext eventContext) {
                                        apiCallback2.onSuccess(eventContext.event);
                                    }
                                });
                            } else {
                                apiCallback2.onMatrixError(matrixError);
                            }
                        }
                    });
                } else {
                    apiCallback2.onMatrixError(matrixError);
                }
            }
        };
        getEventFromRoomIdEventId(str, str2, r0);
    }

    /* access modifiers changed from: private */
    public void getEventFromRoomIdEventId(final String str, final String str2, final ApiCallback<Event> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getEventFromRoomIdEventId : roomId ");
        sb.append(str);
        sb.append(" eventId ");
        sb.append(str2);
        ((RoomsApi) this.mApi).getEvent(str, str2).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getEventFromRoomIdEventId(str, str2, apiCallback);
            }
        }));
    }

    /* access modifiers changed from: private */
    public void getEventFromEventId(final String str, final ApiCallback<Event> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getEventFromEventId : eventId ");
        sb.append(str);
        ((RoomsApi) this.mApi).getEvent(str).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getEventFromEventId(str, apiCallback);
            }
        }));
    }

    public void getContextOfEvent(String str, String str2, int i, ApiCallback<EventContext> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getContextOfEvent : roomId ");
        sb.append(str);
        sb.append(" eventId ");
        sb.append(str2);
        sb.append(" limit ");
        sb.append(i);
        String sb2 = sb.toString();
        Call contextOfEvent = ((RoomsApi) this.mApi).getContextOfEvent(str, str2, i);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str3 = str;
        final String str4 = str2;
        final int i2 = i;
        final ApiCallback<EventContext> apiCallback2 = apiCallback;
        C280918 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getContextOfEvent(str3, str4, i2, apiCallback2);
            }
        };
        contextOfEvent.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void updateRoomName(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateName : roomId ");
        sb.append(str);
        sb.append(" name ");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.name = str2;
        ((RoomsApi) this.mApi).setRoomName(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateRoomName(str, str2, apiCallback);
            }
        }));
    }

    public void updateCanonicalAlias(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateCanonicalAlias : roomId ");
        sb.append(str);
        sb.append(" canonicalAlias ");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.alias = str2;
        ((RoomsApi) this.mApi).setCanonicalAlias(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateCanonicalAlias(str, str2, apiCallback);
            }
        }));
    }

    public void updateHistoryVisibility(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateHistoryVisibility : roomId ");
        sb.append(str);
        sb.append(" visibility ");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.history_visibility = str2;
        ((RoomsApi) this.mApi).setHistoryVisibility(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateHistoryVisibility(str, str2, apiCallback);
            }
        }));
    }

    public void updateDirectoryVisibility(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateRoomDirectoryVisibility : roomId=");
        sb.append(str);
        sb.append(" visibility=");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.visibility = str2;
        ((RoomsApi) this.mApi).setRoomDirectoryVisibility(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateDirectoryVisibility(str, str2, apiCallback);
            }
        }));
    }

    public void getDirectoryVisibility(final String str, final ApiCallback<RoomState> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getRoomDirectoryVisibility userId=");
        sb.append(str);
        ((RoomsApi) this.mApi).getRoomDirectoryVisibility(str).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getDirectoryVisibility(str, apiCallback);
            }
        }));
    }

    public void updateTopic(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateTopic : roomId ");
        sb.append(str);
        sb.append(" topic ");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.topic = str2;
        ((RoomsApi) this.mApi).setRoomTopic(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateTopic(str, str2, apiCallback);
            }
        }));
    }

    public void redactEvent(final String str, final String str2, final ApiCallback<Event> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("redactEvent : roomId ");
        sb.append(str);
        sb.append(" eventId ");
        sb.append(str2);
        ((RoomsApi) this.mApi).redactEvent(str, str2, new JsonObject()).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.redactEvent(str, str2, apiCallback);
            }
        }));
    }

    public void reportEvent(String str, String str2, int i, String str3, ApiCallback<Void> apiCallback) {
        final String str4 = str;
        final String str5 = str2;
        StringBuilder sb = new StringBuilder();
        sb.append("report : roomId ");
        sb.append(str4);
        sb.append(" eventId ");
        sb.append(str5);
        String sb2 = sb.toString();
        ReportContentParams reportContentParams = new ReportContentParams();
        final int i2 = i;
        reportContentParams.score = i2;
        final String str6 = str3;
        reportContentParams.reason = str6;
        Call reportEvent = ((RoomsApi) this.mApi).reportEvent(str4, str5, reportContentParams);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C281826 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.reportEvent(str4, str5, i2, str6, apiCallback2);
            }
        };
        reportEvent.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r0));
    }

    public void updatePowerLevels(final String str, final PowerLevels powerLevels, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updatePowerLevels : roomId ");
        sb.append(str);
        sb.append(" powerLevels ");
        sb.append(powerLevels);
        ((RoomsApi) this.mApi).setPowerLevels(str, powerLevels).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updatePowerLevels(str, powerLevels, apiCallback);
            }
        }));
    }

    public void sendStateEvent(String str, String str2, @Nullable String str3, Map<String, Object> map, ApiCallback<Void> apiCallback) {
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final Map<String, Object> map2 = map;
        ApiCallback<Void> apiCallback2 = apiCallback;
        StringBuilder sb = new StringBuilder();
        sb.append("sendStateEvent : roomId ");
        sb.append(str4);
        sb.append(" - eventType ");
        sb.append(str5);
        String sb2 = sb.toString();
        if (str6 != null) {
            Call sendStateEvent = ((RoomsApi) this.mApi).sendStateEvent(str4, str5, str6, map2);
            UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
            final ApiCallback<Void> apiCallback3 = apiCallback2;
            C282028 r0 = new RequestRetryCallBack() {
                public void onRetry() {
                    RoomsRestClient.this.sendStateEvent(str4, str5, str6, map2, apiCallback3);
                }
            };
            sendStateEvent.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback2, r0));
            return;
        }
        Call sendStateEvent2 = ((RoomsApi) this.mApi).sendStateEvent(str4, str5, map2);
        UnsentEventsManager unsentEventsManager2 = this.mUnsentEventsManager;
        final Map<String, Object> map3 = map2;
        final ApiCallback<Void> apiCallback4 = apiCallback2;
        C282129 r02 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.sendStateEvent(str4, str5, null, map3, apiCallback4);
            }
        };
        sendStateEvent2.enqueue(new RestAdapterCallback(sb2, unsentEventsManager2, apiCallback2, r02));
    }

    public void getStateEvent(final String str, final String str2, final ApiCallback<JsonElement> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getStateEvent : roomId ");
        sb.append(str);
        sb.append(" eventId ");
        sb.append(str2);
        ((RoomsApi) this.mApi).getStateEvent(str, str2).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getStateEvent(str, str2, apiCallback);
            }
        }));
    }

    public void getStateEvent(String str, String str2, String str3, ApiCallback<JsonElement> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getStateEvent : roomId ");
        sb.append(str);
        sb.append(" eventId ");
        sb.append(str2);
        sb.append(" stateKey ");
        sb.append(str3);
        String sb2 = sb.toString();
        Call stateEvent = ((RoomsApi) this.mApi).getStateEvent(str, str2, str3);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        final ApiCallback<JsonElement> apiCallback2 = apiCallback;
        C282431 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getStateEvent(str4, str5, str6, apiCallback2);
            }
        };
        stateEvent.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void sendTypingNotification(String str, String str2, boolean z, int i, ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("sendTypingNotification : roomId ");
        sb.append(str);
        sb.append(" isTyping ");
        sb.append(z);
        String sb2 = sb.toString();
        Typing typing = new Typing();
        typing.typing = z;
        if (-1 != i) {
            typing.timeout = i;
        }
        ((RoomsApi) this.mApi).setTypingNotification(str, str2, typing).enqueue(new RestAdapterCallback(sb2, null, apiCallback, null));
    }

    public void updateAvatarUrl(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateAvatarUrl : roomId ");
        sb.append(str);
        sb.append(" avatarUrl ");
        sb.append(str2);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        hashMap.put(ImagesContract.URL, str2);
        ((RoomsApi) this.mApi).setRoomAvatarUrl(str, hashMap).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateAvatarUrl(str, str2, apiCallback);
            }
        }));
    }

    public void sendReadMarker(String str, String str2, String str3, ApiCallback<Void> apiCallback) {
        final String str4 = str;
        final String str5 = str2;
        final String str6 = str3;
        StringBuilder sb = new StringBuilder();
        sb.append("sendReadMarker : roomId ");
        sb.append(str4);
        sb.append(" - rmEventId ");
        sb.append(str5);
        sb.append(" -- rrEventId ");
        sb.append(str6);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        if (!TextUtils.isEmpty(str2)) {
            hashMap.put("m.fully_read", str5);
        }
        if (!TextUtils.isEmpty(str3)) {
            hashMap.put(READ_MARKER_READ, str6);
        }
        Call sendReadMarker = ((RoomsApi) this.mApi).sendReadMarker(str4, hashMap);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C282633 r0 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.sendReadMarker(str4, str5, str6, apiCallback2);
            }
        };
        RestAdapterCallback restAdapterCallback = new RestAdapterCallback(sb2, unsentEventsManager, true, apiCallback, r0);
        sendReadMarker.enqueue(restAdapterCallback);
    }

    public void addTag(String str, String str2, Double d, ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("addTag : roomId ");
        sb.append(str);
        sb.append(" - tag ");
        sb.append(str2);
        sb.append(" - order ");
        sb.append(d);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        hashMap.put("order", d);
        Call addTag = ((RoomsApi) this.mApi).addTag(this.mCredentials.userId, str, str2, hashMap);
        UnsentEventsManager unsentEventsManager = this.mUnsentEventsManager;
        final String str3 = str;
        final String str4 = str2;
        final Double d2 = d;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C282734 r4 = new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.addTag(str3, str4, d2, apiCallback2);
            }
        };
        addTag.enqueue(new RestAdapterCallback(sb2, unsentEventsManager, apiCallback, r4));
    }

    public void removeTag(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("removeTag : roomId ");
        sb.append(str);
        sb.append(" - tag ");
        sb.append(str2);
        ((RoomsApi) this.mApi).removeTag(this.mCredentials.userId, str, str2).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.removeTag(str, str2, apiCallback);
            }
        }));
    }

    public void updateURLPreviewStatus(final String str, final boolean z, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateURLPreviewStatus : roomId ");
        sb.append(str);
        sb.append(" - status ");
        sb.append(z);
        String sb2 = sb.toString();
        HashMap hashMap = new HashMap();
        hashMap.put(AccountDataRestClient.ACCOUNT_DATA_KEY_URL_PREVIEW_DISABLE, Boolean.valueOf(!z));
        ((RoomsApi) this.mApi).updateAccountData(this.mCredentials.userId, str, Event.EVENT_TYPE_URL_PREVIEW, hashMap).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateURLPreviewStatus(str, z, apiCallback);
            }
        }));
    }

    public void getRoomIdByAlias(final String str, final ApiCallback<RoomAliasDescription> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("getRoomIdByAlias : ");
        sb.append(str);
        ((RoomsApi) this.mApi).getRoomIdByAlias(str).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.getRoomIdByAlias(str, apiCallback);
            }
        }));
    }

    public void setRoomIdByAlias(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("setRoomIdByAlias : roomAlias ");
        sb.append(str2);
        sb.append(" - roomId : ");
        sb.append(str);
        String sb2 = sb.toString();
        RoomAliasDescription roomAliasDescription = new RoomAliasDescription();
        roomAliasDescription.room_id = str;
        ((RoomsApi) this.mApi).setRoomIdByAlias(str2, roomAliasDescription).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.setRoomIdByAlias(str, str2, apiCallback);
            }
        }));
    }

    public void removeRoomAlias(final String str, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("removeRoomAlias : ");
        sb.append(str);
        ((RoomsApi) this.mApi).removeRoomAlias(str).enqueue(new RestAdapterCallback(sb.toString(), this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.removeRoomAlias(str, apiCallback);
            }
        }));
    }

    public void updateJoinRules(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateJoinRules : roomId=");
        sb.append(str);
        sb.append(" rule=");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.join_rule = str2;
        ((RoomsApi) this.mApi).setJoinRules(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateJoinRules(str, str2, apiCallback);
            }
        }));
    }

    public void updateGuestAccess(final String str, final String str2, final ApiCallback<Void> apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append("updateGuestAccess : roomId=");
        sb.append(str);
        sb.append(" rule=");
        sb.append(str2);
        String sb2 = sb.toString();
        RoomState roomState = new RoomState();
        roomState.guest_access = str2;
        ((RoomsApi) this.mApi).setGuestAccess(str, roomState).enqueue(new RestAdapterCallback(sb2, this.mUnsentEventsManager, apiCallback, new RequestRetryCallBack() {
            public void onRetry() {
                RoomsRestClient.this.updateGuestAccess(str, str2, apiCallback);
            }
        }));
    }
}
