package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.p000v4.app.NotificationCompat;
import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.RoomsRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.JsonUtilKt;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u000b\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0007\u001a\u00020\u0004H\u0016J&\u0010\b\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u000e\u001a\u00020\u000f2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u0010\u001a\u00020\u000f2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u0011\u001a\u00020\u000f2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0016J&\u0010\u0012\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u0013\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J\b\u0010\u0014\u001a\u00020\u0015H\u0016J&\u0010\u0016\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u0017\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u0018\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J\b\u0010\u0019\u001a\u00020\tH\u0017J&\u0010\u001a\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u001b\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u001c\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u001d\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002J&\u0010\u001e\u001a\u00020\t2\u001c\u0010\n\u001a\u0018\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f0\u000bj\b\u0012\u0004\u0012\u00020\f`\rH\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u0006 "}, mo21251d2 = {"Lcom/opengarden/firechat/activity/IntegrationManagerActivity;", "Lcom/opengarden/firechat/activity/AbstractWidgetActivity;", "()V", "mScreenId", "", "mWidgetId", "buildInterfaceUrl", "scalarToken", "canSendEvent", "", "eventData", "", "", "Lcom/opengarden/firechat/types/JsonDict;", "checkRoomId", "", "checkUserId", "dealsWithWidgetRequest", "getBotOptions", "getJoinRules", "getLayoutRes", "", "getMembershipCount", "getMembershipState", "getWidgets", "initUiAndData", "inviteUser", "setBotOptions", "setBotPower", "setPlumbingState", "setWidget", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: IntegrationManagerActivity.kt */
public final class IntegrationManagerActivity extends AbstractWidgetActivity {
    public static final Companion Companion = new Companion(null);
    @NotNull
    public static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    @NotNull
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    private static final String EXTRA_SCREEN_ID = "EXTRA_SCREEN_ID";
    @NotNull
    public static final String EXTRA_WIDGET_ID = "EXTRA_WIDGET_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "IntegrationManagerActivity";
    private String mScreenId;
    private String mWidgetId;

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J6\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00042\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\n \t*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/IntegrationManagerActivity$Companion;", "", "()V", "EXTRA_MATRIX_ID", "", "EXTRA_ROOM_ID", "EXTRA_SCREEN_ID", "EXTRA_WIDGET_ID", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "getIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "matrixId", "roomId", "widgetId", "screenId", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: IntegrationManagerActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return IntegrationManagerActivity.LOG_TAG;
        }

        @NotNull
        public static /* bridge */ /* synthetic */ Intent getIntent$default(Companion companion, Context context, String str, String str2, String str3, String str4, int i, Object obj) {
            if ((i & 8) != 0) {
                str3 = null;
            }
            String str5 = str3;
            if ((i & 16) != 0) {
                str4 = null;
            }
            return companion.getIntent(context, str, str2, str5, str4);
        }

        @NotNull
        public final Intent getIntent(@NotNull Context context, @NotNull String str, @NotNull String str2, @Nullable String str3, @Nullable String str4) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "matrixId");
            Intrinsics.checkParameterIsNotNull(str2, "roomId");
            Intent intent = new Intent(context, IntegrationManagerActivity.class);
            intent.putExtra("EXTRA_MATRIX_ID", str);
            intent.putExtra("EXTRA_ROOM_ID", str2);
            intent.putExtra("EXTRA_WIDGET_ID", str3);
            intent.putExtra(IntegrationManagerActivity.EXTRA_SCREEN_ID, str4);
            return intent;
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_integration_manager;
    }

    @CallSuper
    public void initUiAndData() {
        this.mWidgetId = getIntent().getStringExtra("EXTRA_WIDGET_ID");
        this.mScreenId = getIntent().getStringExtra(EXTRA_SCREEN_ID);
        setWaitingView(findViewById(C1299R.C1301id.integration_progress_layout));
        showWaitingView();
        super.initUiAndData();
    }

    @Nullable
    public String buildInterfaceUrl(@NotNull String str) {
        Intrinsics.checkParameterIsNotNull(str, "scalarToken");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(C1299R.string.integrations_ui_url));
            sb.append("?");
            sb.append("scalar_token=");
            sb.append(URLEncoder.encode(str, "utf-8"));
            sb.append("&");
            sb.append("room_id=");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(URLEncoder.encode(mRoom.getRoomId(), "utf-8"));
            String sb2 = sb.toString();
            if (this.mWidgetId != null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append("&integ_id=");
                sb3.append(URLEncoder.encode(this.mWidgetId, "utf-8"));
                sb2 = sb3.toString();
            }
            if (this.mScreenId != null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(sb2);
                sb4.append("&screen=");
                sb4.append(URLEncoder.encode(this.mScreenId, "utf-8"));
                sb2 = sb4.toString();
            }
            return sb2;
        } catch (Exception e) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb5 = new StringBuilder();
            sb5.append("## buildInterfaceUrl() failed ");
            sb5.append(e.getMessage());
            Log.m211e(access$getLOG_TAG$p, sb5.toString());
            return null;
        }
    }

    public boolean dealsWithWidgetRequest(@NotNull Map<String, ? extends Object> map) {
        Intrinsics.checkParameterIsNotNull(map, "eventData");
        String str = (String) map.get("action");
        if (str != null) {
            switch (str.hashCode()) {
                case -1330055448:
                    if (str.equals("membership_state")) {
                        getMembershipState(map);
                        Unit unit = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -1209460717:
                    if (str.equals("close_scalar")) {
                        finish();
                        Unit unit2 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -1183699191:
                    if (str.equals("invite")) {
                        inviteUser(map);
                        Unit unit3 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -1166615312:
                    if (str.equals("set_bot_power")) {
                        setBotPower(map);
                        Unit unit4 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -1065531199:
                    if (str.equals("set_widget")) {
                        setWidget(map);
                        Unit unit5 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -992375863:
                    if (str.equals("set_bot_options")) {
                        setBotOptions(map);
                        Unit unit6 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case -248847901:
                    if (str.equals("set_plumbing_state")) {
                        setPlumbingState(map);
                        Unit unit7 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case 122629423:
                    if (str.equals("get_membership_count")) {
                        getMembershipCount(map);
                        Unit unit8 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case 1072392116:
                    if (str.equals("join_rules_state")) {
                        getJoinRules(map);
                        Unit unit9 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case 1416851142:
                    if (str.equals("get_widgets")) {
                        getWidgets(map);
                        Unit unit10 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case 1551692838:
                    if (str.equals("bot_options")) {
                        getBotOptions(map);
                        Unit unit11 = Unit.INSTANCE;
                        return true;
                    }
                    break;
                case 2016326386:
                    if (str.equals("can_send_event")) {
                        canSendEvent(map);
                        Unit unit12 = Unit.INSTANCE;
                        return true;
                    }
                    break;
            }
        }
        return super.dealsWithWidgetRequest(map);
    }

    private final void inviteUser(Map<String, ? extends Object> map) {
        if (!checkRoomId(map) && !checkUserId(map)) {
            Object obj = map.get(AmplitudeClient.USER_ID_KEY);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to invite ");
            sb.append(str);
            sb.append(" into room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            String sb2 = sb.toString();
            Log.m209d(Companion.getLOG_TAG(), sb2);
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            RoomMember member = mRoom2.getMember(str);
            if (member == null || !TextUtils.equals(member.membership, RoomMember.MEMBERSHIP_JOIN)) {
                Room mRoom3 = getMRoom();
                if (mRoom3 == null) {
                    Intrinsics.throwNpe();
                }
                mRoom3.invite(str, (ApiCallback<Void>) new WidgetApiCallback<Void>(this, map, sb2));
            } else {
                sendSuccess(map);
            }
        }
    }

    private final void setWidget(Map<String, ? extends Object> map) {
        Boolean bool = (Boolean) map.get("userWidget");
        if (Intrinsics.areEqual((Object) bool, (Object) Boolean.valueOf(true))) {
            Log.m209d(Companion.getLOG_TAG(), "Received request to set widget for user");
        } else if (!checkRoomId(map)) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to set widget in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            Log.m209d(access$getLOG_TAG$p, sb.toString());
        } else {
            return;
        }
        String str = (String) map.get("widget_id");
        String str2 = (String) map.get("type");
        String str3 = (String) map.get(ImagesContract.URL);
        String str4 = (String) map.get("name");
        Map map2 = (Map) map.get("data");
        if (str == null) {
            String string = getString(C1299R.string.widget_integration_unable_to_create);
            Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…gration_unable_to_create)");
            sendError(string, map);
            return;
        }
        HashMap hashMap = new HashMap();
        if (str3 != null) {
            if (str2 == null) {
                String string2 = getString(C1299R.string.widget_integration_unable_to_create);
                Intrinsics.checkExpressionValueIsNotNull(string2, "getString(R.string.widge…gration_unable_to_create)");
                sendError(string2, map);
                return;
            }
            Map map3 = hashMap;
            map3.put("type", str2);
            map3.put(ImagesContract.URL, str3);
            if (str4 != null) {
                map3.put("name", str4);
            }
            if (map2 != null) {
                map3.put("data", map2);
            }
        }
        if (Intrinsics.areEqual((Object) bool, (Object) Boolean.valueOf(true))) {
            HashMap hashMap2 = new HashMap();
            HashMap hashMap3 = new HashMap();
            hashMap3.put("content", hashMap);
            hashMap3.put("state_key", str);
            hashMap3.put("id", str);
            String str5 = BingRule.KIND_SENDER;
            MXSession mSession = getMSession();
            if (mSession == null) {
                Intrinsics.throwNpe();
            }
            hashMap3.put(str5, mSession.getMyUserId());
            hashMap3.put("type", "m.widget");
            hashMap2.put(str, hashMap3);
            MXSession mSession2 = getMSession();
            if (mSession2 == null) {
                Intrinsics.throwNpe();
            }
            mSession2.addUserWidget(hashMap2, new WidgetApiCallback(this, map, "## setWidget()"));
        } else {
            MXSession mSession3 = getMSession();
            if (mSession3 == null) {
                Intrinsics.throwNpe();
            }
            RoomsRestClient roomsApiClient = mSession3.getRoomsApiClient();
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            roomsApiClient.sendStateEvent(mRoom2.getRoomId(), WidgetsManager.WIDGET_EVENT_TYPE, str, hashMap, new WidgetApiCallback(this, map, "## setWidget()"));
        }
    }

    private final void getWidgets(Map<String, ? extends Object> map) {
        if (!checkRoomId(map)) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to get widget in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            List<Widget> activeWidgets = WidgetsManager.getSharedInstance().getActiveWidgets(getMSession(), getMRoom());
            ArrayList arrayList = new ArrayList();
            for (Widget widget : activeWidgets) {
                Intrinsics.checkExpressionValueIsNotNull(widget, "widget");
                Event widgetEvent = widget.getWidgetEvent();
                Intrinsics.checkExpressionValueIsNotNull(widgetEvent, "widget.widgetEvent");
                Map jsonMap = JsonUtilKt.toJsonMap(widgetEvent);
                if (jsonMap != null) {
                    arrayList.add(jsonMap);
                }
            }
            MXSession mSession = getMSession();
            if (mSession == null) {
                Intrinsics.throwNpe();
            }
            Map userWidgets = mSession.getUserWidgets();
            Intrinsics.checkExpressionValueIsNotNull(userWidgets, "mSession!!.userWidgets");
            for (Entry value : userWidgets.entrySet()) {
                Object value2 = value.getValue();
                if (value2 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.types.JsonDict<kotlin.Any> /* = kotlin.collections.Map<kotlin.String, kotlin.Any> */");
                }
                arrayList.add((Map) value2);
            }
            String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getWidgets() returns ");
            sb2.append(arrayList);
            Log.m209d(access$getLOG_TAG$p2, sb2.toString());
            sendObjectResponse(arrayList, map);
        }
    }

    private final void canSendEvent(Map<String, ? extends Object> map) {
        if (!checkRoomId(map)) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("Received request canSendEvent in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            RoomState liveState = mRoom2.getLiveState();
            MXSession mSession = getMSession();
            if (mSession == null) {
                Intrinsics.throwNpe();
            }
            RoomMember member = liveState.getMember(mSession.getMyUserId());
            if (member == null || !TextUtils.equals(RoomMember.MEMBERSHIP_JOIN, member.membership)) {
                String string = getString(C1299R.string.widget_integration_must_be_in_room);
                Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…egration_must_be_in_room)");
                sendError(string, map);
                return;
            }
            Object obj = map.get("event_type");
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            Object obj2 = map.get("is_state");
            if (obj2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.Boolean");
            }
            boolean booleanValue = ((Boolean) obj2).booleanValue();
            String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## canSendEvent() : eventType ");
            sb2.append(str);
            sb2.append(" isState ");
            sb2.append(booleanValue);
            Log.m209d(access$getLOG_TAG$p2, sb2.toString());
            Room mRoom3 = getMRoom();
            if (mRoom3 == null) {
                Intrinsics.throwNpe();
            }
            RoomState liveState2 = mRoom3.getLiveState();
            Intrinsics.checkExpressionValueIsNotNull(liveState2, "mRoom!!.liveState");
            PowerLevels powerLevels = liveState2.getPowerLevels();
            if (powerLevels == null) {
                Intrinsics.throwNpe();
            }
            MXSession mSession2 = getMSession();
            if (mSession2 == null) {
                Intrinsics.throwNpe();
            }
            int userPowerLevel = powerLevels.getUserPowerLevel(mSession2.getMyUserId());
            boolean z = false;
            if (!booleanValue ? userPowerLevel >= powerLevels.minimumPowerLevelForSendingEventAsMessage(str) : userPowerLevel >= powerLevels.minimumPowerLevelForSendingEventAsStateEvent(str)) {
                z = true;
            }
            if (z) {
                Log.m209d(Companion.getLOG_TAG(), "## canSendEvent() returns true");
                sendBoolResponse(true, map);
            } else {
                Log.m209d(Companion.getLOG_TAG(), "## canSendEvent() returns widget_integration_no_permission_in_room");
                String string2 = getString(C1299R.string.widget_integration_no_permission_in_room);
                Intrinsics.checkExpressionValueIsNotNull(string2, "getString(R.string.widge…on_no_permission_in_room)");
                sendError(string2, map);
            }
        }
    }

    private final void getMembershipState(Map<String, ? extends Object> map) {
        if (!checkRoomId(map) && !checkUserId(map)) {
            Object obj = map.get(AmplitudeClient.USER_ID_KEY);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("membership_state of ");
            sb.append(str);
            sb.append(" in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            sb.append(" requested");
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            mRoom2.getMemberEvent(str, new IntegrationManagerActivity$getMembershipState$1(this, str, map));
        }
    }

    private final void getJoinRules(Map<String, ? extends Object> map) {
        if (!checkRoomId(map)) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("Received request join rules  in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            List stateEvents = mRoom2.getLiveState().getStateEvents(new HashSet(Arrays.asList(new String[]{Event.EVENT_TYPE_STATE_ROOM_JOIN_RULES})));
            if (stateEvents.size() > 0) {
                String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Received request join rules returns ");
                sb2.append((Event) stateEvents.get(stateEvents.size() - 1));
                Log.m209d(access$getLOG_TAG$p2, sb2.toString());
                Object obj = stateEvents.get(stateEvents.size() - 1);
                Intrinsics.checkExpressionValueIsNotNull(obj, "joinedEvents[joinedEvents.size - 1]");
                sendObjectAsJsonMap(obj, map);
            } else {
                Log.m211e(Companion.getLOG_TAG(), "Received request join rules failed widget_integration_failed_to_send_request");
                String string = getString(C1299R.string.widget_integration_failed_to_send_request);
                Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…n_failed_to_send_request)");
                sendError(string, map);
            }
        }
    }

    private final void setPlumbingState(Map<String, ? extends Object> map) {
        if (!checkRoomId(map)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to set plumbing state to status ");
            sb.append(map.get(NotificationCompat.CATEGORY_STATUS));
            sb.append(" in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            sb.append(" requested");
            String sb2 = sb.toString();
            Log.m209d(Companion.getLOG_TAG(), sb2);
            Object obj = map.get(NotificationCompat.CATEGORY_STATUS);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            Map hashMap = new HashMap();
            hashMap.put(NotificationCompat.CATEGORY_STATUS, str);
            MXSession mSession = getMSession();
            if (mSession == null) {
                Intrinsics.throwNpe();
            }
            RoomsRestClient roomsApiClient = mSession.getRoomsApiClient();
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            roomsApiClient.sendStateEvent(mRoom2.getRoomId(), Event.EVENT_TYPE_ROOM_PLUMBING, null, hashMap, new WidgetApiCallback(this, map, sb2));
        }
    }

    private final void getBotOptions(Map<String, ? extends Object> map) {
        if (!checkRoomId(map) && !checkUserId(map)) {
            Object obj = map.get(AmplitudeClient.USER_ID_KEY);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to get options for bot ");
            sb.append(str);
            sb.append(" in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            sb.append(" requested");
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            List<Event> stateEvents = mRoom2.getLiveState().getStateEvents(new HashSet(Arrays.asList(new String[]{Event.EVENT_TYPE_ROOM_BOT_OPTIONS})));
            Event event = null;
            StringBuilder sb2 = new StringBuilder();
            sb2.append('_');
            sb2.append(str);
            String sb3 = sb2.toString();
            for (Event event2 : stateEvents) {
                if (TextUtils.equals(event2.stateKey, sb3) && (event == null || event2.getAge() > event.getAge())) {
                    event = event2;
                }
            }
            if (event != null) {
                String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Received request to get options for bot ");
                sb4.append(str);
                sb4.append(" returns ");
                sb4.append(event);
                Log.m209d(access$getLOG_TAG$p2, sb4.toString());
                sendObjectAsJsonMap(event, map);
            } else {
                String access$getLOG_TAG$p3 = Companion.getLOG_TAG();
                StringBuilder sb5 = new StringBuilder();
                sb5.append("Received request to get options for bot ");
                sb5.append(str);
                sb5.append(" returns null");
                Log.m209d(access$getLOG_TAG$p3, sb5.toString());
                sendObjectResponse(null, map);
            }
        }
    }

    private final void setBotOptions(Map<String, ? extends Object> map) {
        if (!checkRoomId(map) && !checkUserId(map)) {
            Object obj = map.get(AmplitudeClient.USER_ID_KEY);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to set options for bot ");
            sb.append(str);
            sb.append(" in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            String sb2 = sb.toString();
            Log.m209d(Companion.getLOG_TAG(), sb2);
            Object obj2 = map.get("content");
            if (obj2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.types.JsonDict<kotlin.Any> /* = kotlin.collections.Map<kotlin.String, kotlin.Any> */");
            }
            Map map2 = (Map) obj2;
            StringBuilder sb3 = new StringBuilder();
            sb3.append('_');
            sb3.append(str);
            String sb4 = sb3.toString();
            MXSession mSession = getMSession();
            if (mSession == null) {
                Intrinsics.throwNpe();
            }
            RoomsRestClient roomsApiClient = mSession.getRoomsApiClient();
            Room mRoom2 = getMRoom();
            if (mRoom2 == null) {
                Intrinsics.throwNpe();
            }
            roomsApiClient.sendStateEvent(mRoom2.getRoomId(), Event.EVENT_TYPE_ROOM_BOT_OPTIONS, sb4, map2, new WidgetApiCallback(this, map, sb2));
        }
    }

    private final void setBotPower(Map<String, ? extends Object> map) {
        if (!checkRoomId(map) && !checkUserId(map)) {
            Object obj = map.get(AmplitudeClient.USER_ID_KEY);
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
            }
            String str = (String) obj;
            StringBuilder sb = new StringBuilder();
            sb.append("Received request to set power level to ");
            sb.append(map.get(Param.LEVEL));
            sb.append(" for bot ");
            sb.append(str);
            sb.append(" in room ");
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sb.append(mRoom.getRoomId());
            String sb2 = sb.toString();
            Log.m209d(Companion.getLOG_TAG(), sb2);
            Object obj2 = map.get(Param.LEVEL);
            if (obj2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
            }
            int intValue = ((Integer) obj2).intValue();
            if (intValue >= 0) {
                Room mRoom2 = getMRoom();
                if (mRoom2 == null) {
                    Intrinsics.throwNpe();
                }
                mRoom2.updateUserPowerLevels(str, intValue, new WidgetApiCallback(this, map, sb2));
            } else {
                Log.m211e(Companion.getLOG_TAG(), "## setBotPower() : Power level must be positive integer.");
                String string = getString(C1299R.string.widget_integration_positive_power_level);
                Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…ion_positive_power_level)");
                sendError(string, map);
            }
        }
    }

    private final void getMembershipCount(Map<String, ? extends Object> map) {
        if (!checkRoomId(map)) {
            Room mRoom = getMRoom();
            if (mRoom == null) {
                Intrinsics.throwNpe();
            }
            sendIntegerResponse(mRoom.getJoinedMembers().size(), map);
        }
    }

    private final boolean checkRoomId(Map<String, ? extends Object> map) {
        String str = (String) map.get("room_id");
        if (str == null) {
            String string = getString(C1299R.string.widget_integration_missing_room_id);
            Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…egration_missing_room_id)");
            sendError(string, map);
            return true;
        }
        CharSequence charSequence = str;
        Room mRoom = getMRoom();
        if (mRoom == null) {
            Intrinsics.throwNpe();
        }
        if (TextUtils.equals(charSequence, mRoom.getRoomId())) {
            return false;
        }
        String string2 = getString(C1299R.string.widget_integration_room_not_visible);
        Intrinsics.checkExpressionValueIsNotNull(string2, "getString(R.string.widge…gration_room_not_visible)");
        sendError(string2, map);
        return true;
    }

    private final boolean checkUserId(Map<String, ? extends Object> map) {
        if (((String) map.get(AmplitudeClient.USER_ID_KEY)) != null) {
            return false;
        }
        String string = getString(C1299R.string.widget_integration_missing_user_id);
        Intrinsics.checkExpressionValueIsNotNull(string, "getString(R.string.widge…egration_missing_user_id)");
        sendError(string, map);
        return true;
    }
}
