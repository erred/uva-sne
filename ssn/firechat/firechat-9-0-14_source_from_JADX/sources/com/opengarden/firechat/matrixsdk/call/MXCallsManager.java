package com.opengarden.firechat.matrixsdk.call;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.CallRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomParams;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.StringUtils;

public class MXCallsManager {
    private static final String DOMAIN = "matrix.org";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXCallsManager";
    private static final String USER_PREFIX = "fs_";
    private static final HashMap<String, String> mConferenceUserIdByRoomId = new HashMap<>();
    /* access modifiers changed from: private */
    public CallRestClient mCallResClient = null;
    /* access modifiers changed from: private */
    public final HashMap<String, IMXCall> mCallsByCallId = new HashMap<>();
    private Context mContext = null;
    private final Set<IMXCallsManagerListener> mListeners = new HashSet();
    private CallClass mPreferredCallClass = CallClass.WEBRTC_CLASS;
    /* access modifiers changed from: private */
    public MXSession mSession = null;
    private boolean mSuspendTurnServerRefresh = false;
    /* access modifiers changed from: private */
    public JsonElement mTurnServer = null;
    /* access modifiers changed from: private */
    public Timer mTurnServerTimer = null;
    /* access modifiers changed from: private */
    public final Handler mUIThreadHandler;
    /* access modifiers changed from: private */
    public final Set<String> mxPendingIncomingCallId = new HashSet();

    public enum CallClass {
        WEBRTC_CLASS,
        DEFAULT_CLASS
    }

    public MXCallsManager(MXSession mXSession, Context context) {
        this.mSession = mXSession;
        this.mContext = context;
        this.mUIThreadHandler = new Handler(Looper.getMainLooper());
        this.mCallResClient = this.mSession.getCallRestClient();
        this.mSession.getDataHandler().addListener(new MXEventListener() {
            public void onLiveEvent(Event event, RoomState roomState) {
                if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_STATE_ROOM_MEMBER) && TextUtils.equals(event.sender, MXCallsManager.getConferenceUserId(event.roomId))) {
                    EventContent eventContent = JsonUtils.toEventContent(event.getContentAsJsonObject());
                    if (TextUtils.equals(eventContent.membership, RoomMember.MEMBERSHIP_LEAVE)) {
                        MXCallsManager.this.dispatchOnVoipConferenceFinished(event.roomId);
                    }
                    if (TextUtils.equals(eventContent.membership, RoomMember.MEMBERSHIP_JOIN)) {
                        MXCallsManager.this.dispatchOnVoipConferenceStarted(event.roomId);
                    }
                }
            }
        });
        refreshTurnServer();
    }

    public boolean isSupported() {
        return MXWebRtcCall.isSupported(this.mContext);
    }

    public Collection<CallClass> supportedClass() {
        ArrayList arrayList = new ArrayList();
        if (MXWebRtcCall.isSupported(this.mContext)) {
            arrayList.add(CallClass.WEBRTC_CLASS);
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("supportedClass ");
        sb.append(arrayList);
        Log.m209d(str, sb.toString());
        return arrayList;
    }

    public void setDefaultCallClass(CallClass callClass) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("setDefaultCallClass ");
        sb.append(callClass);
        Log.m209d(str, sb.toString());
        if (callClass == CallClass.WEBRTC_CLASS ? MXWebRtcCall.isSupported(this.mContext) : false) {
            this.mPreferredCallClass = callClass;
        }
    }

    private IMXCall createCall(String str) {
        MXWebRtcCall mXWebRtcCall;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("createCall ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        try {
            mXWebRtcCall = new MXWebRtcCall(this.mSession, this.mContext, getTurnServer());
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("createCall ");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
            mXWebRtcCall = null;
        }
        if (str != null) {
            mXWebRtcCall.setCallId(str);
        }
        return mXWebRtcCall;
    }

    public IMXCall getCallWithRoomId(String str) {
        ArrayList arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.mCallsByCallId.values());
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            IMXCall iMXCall = (IMXCall) it.next();
            if (TextUtils.equals(str, iMXCall.getRoom().getRoomId())) {
                if (!TextUtils.equals(iMXCall.getCallState(), IMXCall.CALL_STATE_ENDED)) {
                    return iMXCall;
                }
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getCallWithRoomId() : the call ");
                sb.append(iMXCall.getCallId());
                sb.append(" has been stopped");
                Log.m209d(str2, sb.toString());
                synchronized (this) {
                    this.mCallsByCallId.remove(iMXCall.getCallId());
                }
            }
        }
        return null;
    }

    public IMXCall getCallWithCallId(String str) {
        return getCallWithCallId(str, false);
    }

    /* access modifiers changed from: private */
    public IMXCall getCallWithCallId(String str, boolean z) {
        IMXCall iMXCall;
        IMXCall iMXCall2 = null;
        if (str != null) {
            synchronized (this) {
                iMXCall = (IMXCall) this.mCallsByCallId.get(str);
            }
        } else {
            iMXCall = null;
        }
        if (iMXCall == null || !TextUtils.equals(iMXCall.getCallState(), IMXCall.CALL_STATE_ENDED)) {
            iMXCall2 = iMXCall;
        } else {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getCallWithCallId() : the call ");
            sb.append(str);
            sb.append(" has been stopped");
            Log.m209d(str2, sb.toString());
            synchronized (this) {
                this.mCallsByCallId.remove(iMXCall.getCallId());
            }
        }
        if (iMXCall2 == null && z) {
            iMXCall2 = createCall(str);
            synchronized (this) {
                this.mCallsByCallId.put(iMXCall2.getCallId(), iMXCall2);
            }
        }
        String str3 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("getCallWithCallId ");
        sb2.append(str);
        sb2.append(StringUtils.SPACE);
        sb2.append(iMXCall2);
        Log.m209d(str3, sb2.toString());
        return iMXCall2;
    }

    public static boolean isCallInProgress(IMXCall iMXCall) {
        if (iMXCall == null) {
            return false;
        }
        String callState = iMXCall.getCallState();
        if (TextUtils.equals(callState, IMXCall.CALL_STATE_CREATED) || TextUtils.equals(callState, IMXCall.CALL_STATE_CREATING_CALL_VIEW) || TextUtils.equals(callState, IMXCall.CALL_STATE_READY) || TextUtils.equals(callState, IMXCall.CALL_STATE_WAIT_LOCAL_MEDIA) || TextUtils.equals(callState, IMXCall.CALL_STATE_WAIT_CREATE_OFFER) || TextUtils.equals(callState, IMXCall.CALL_STATE_INVITE_SENT) || TextUtils.equals(callState, IMXCall.CALL_STATE_RINGING) || TextUtils.equals(callState, IMXCall.CALL_STATE_CREATE_ANSWER) || TextUtils.equals(callState, IMXCall.CALL_STATE_CONNECTING) || TextUtils.equals(callState, IMXCall.CALL_STATE_CONNECTED)) {
            return true;
        }
        return false;
    }

    public boolean hasActiveCalls() {
        synchronized (this) {
            ArrayList arrayList = new ArrayList();
            for (String str : this.mCallsByCallId.keySet()) {
                if (TextUtils.equals(((IMXCall) this.mCallsByCallId.get(str)).getCallState(), IMXCall.CALL_STATE_ENDED)) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("# hasActiveCalls() : the call ");
                    sb.append(str);
                    sb.append(" is not anymore valid");
                    Log.m209d(str2, sb.toString());
                    arrayList.add(str);
                } else {
                    String str3 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("# hasActiveCalls() : the call ");
                    sb2.append(str);
                    sb2.append(" is active");
                    Log.m209d(str3, sb2.toString());
                    return true;
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                this.mCallsByCallId.remove((String) it.next());
            }
            Log.m209d(LOG_TAG, "# hasActiveCalls() : no active call");
            return false;
        }
    }

    public void handleCallEvent(final IMXStore iMXStore, final Event event) {
        if (event.isCallEvent() && isSupported()) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("handleCallEvent ");
            sb.append(event.getType());
            Log.m209d(str, sb.toString());
            this.mUIThreadHandler.post(new Runnable() {
                /* JADX WARNING: Removed duplicated region for block: B:11:0x005e A[ADDED_TO_REGION] */
                /* JADX WARNING: Removed duplicated region for block: B:77:? A[ADDED_TO_REGION, ORIG_RETURN, RETURN, SYNTHETIC] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r12 = this;
                        com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r5
                        java.lang.String r0 = r0.getSender()
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        com.opengarden.firechat.matrixsdk.MXSession r1 = r1.mSession
                        java.lang.String r1 = r1.getMyUserId()
                        boolean r0 = android.text.TextUtils.equals(r0, r1)
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        com.opengarden.firechat.matrixsdk.MXSession r1 = r1.mSession
                        com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r1.getDataHandler()
                        com.opengarden.firechat.matrixsdk.data.store.IMXStore r2 = r4
                        com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r5
                        java.lang.String r3 = r3.roomId
                        r4 = 1
                        com.opengarden.firechat.matrixsdk.data.Room r1 = r1.getRoom(r2, r3, r4)
                        r2 = 0
                        com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r5     // Catch:{ Exception -> 0x003d }
                        com.google.gson.JsonObject r3 = r3.getContentAsJsonObject()     // Catch:{ Exception -> 0x003d }
                        java.lang.String r5 = "call_id"
                        com.google.gson.JsonPrimitive r5 = r3.getAsJsonPrimitive(r5)     // Catch:{ Exception -> 0x003b }
                        java.lang.String r5 = r5.getAsString()     // Catch:{ Exception -> 0x003b }
                        goto L_0x005c
                    L_0x003b:
                        r5 = move-exception
                        goto L_0x003f
                    L_0x003d:
                        r5 = move-exception
                        r3 = r2
                    L_0x003f:
                        java.lang.String r6 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.LOG_TAG
                        java.lang.StringBuilder r7 = new java.lang.StringBuilder
                        r7.<init>()
                        java.lang.String r8 = "handleCallEvent : fail to retrieve call_id "
                        r7.append(r8)
                        java.lang.String r5 = r5.getMessage()
                        r7.append(r5)
                        java.lang.String r5 = r7.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)
                        r5 = r2
                    L_0x005c:
                        if (r5 == 0) goto L_0x017f
                        if (r1 == 0) goto L_0x017f
                        java.lang.String r6 = "m.call.invite"
                        com.opengarden.firechat.matrixsdk.rest.model.Event r7 = r5
                        java.lang.String r7 = r7.getType()
                        boolean r6 = r6.equals(r7)
                        if (r6 == 0) goto L_0x00c6
                        com.opengarden.firechat.matrixsdk.rest.model.Event r4 = r5
                        long r6 = r4.getAge()
                        r8 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                        int r4 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                        if (r4 != 0) goto L_0x008a
                        long r6 = java.lang.System.currentTimeMillis()
                        com.opengarden.firechat.matrixsdk.rest.model.Event r4 = r5
                        long r8 = r4.getOriginServerTs()
                        long r10 = r6 - r8
                        r6 = r10
                    L_0x008a:
                        r8 = 120000(0x1d4c0, double:5.9288E-319)
                        int r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                        if (r4 >= 0) goto L_0x00bb
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r4 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        r6 = r0 ^ 1
                        com.opengarden.firechat.matrixsdk.call.IMXCall r4 = r4.getCallWithCallId(r5, r6)
                        if (r4 == 0) goto L_0x017f
                        com.opengarden.firechat.matrixsdk.data.Room r6 = r4.getRoom()
                        if (r6 != 0) goto L_0x00a4
                        r4.setRooms(r1, r1)
                    L_0x00a4:
                        if (r0 != 0) goto L_0x00b4
                        r4.prepareIncomingCall(r3, r5, r2)
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        java.util.Set r0 = r0.mxPendingIncomingCallId
                        r0.add(r5)
                        goto L_0x017f
                    L_0x00b4:
                        com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r5
                        r4.handleCallEvent(r0)
                        goto L_0x017f
                    L_0x00bb:
                        java.lang.String r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.LOG_TAG
                        java.lang.String r1 = "## handleCallEvent() : m.call.invite is ignored because it is too old"
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
                        goto L_0x017f
                    L_0x00c6:
                        java.lang.String r2 = "m.call.candidates"
                        com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r5
                        java.lang.String r3 = r3.getType()
                        boolean r2 = r2.equals(r3)
                        if (r2 == 0) goto L_0x00ee
                        if (r0 != 0) goto L_0x017f
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r0.getCallWithCallId(r5)
                        if (r0 == 0) goto L_0x017f
                        com.opengarden.firechat.matrixsdk.data.Room r2 = r0.getRoom()
                        if (r2 != 0) goto L_0x00e7
                        r0.setRooms(r1, r1)
                    L_0x00e7:
                        com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r5
                        r0.handleCallEvent(r1)
                        goto L_0x017f
                    L_0x00ee:
                        java.lang.String r0 = "m.call.answer"
                        com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r5
                        java.lang.String r2 = r2.getType()
                        boolean r0 = r0.equals(r2)
                        if (r0 == 0) goto L_0x0131
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r0.getCallWithCallId(r5)
                        if (r0 == 0) goto L_0x017f
                        java.lang.String r2 = "IMXCall.CALL_STATE_CREATED"
                        java.lang.String r3 = r0.getCallState()
                        boolean r2 = r2.equals(r3)
                        if (r2 == 0) goto L_0x0122
                        r0.onAnsweredElsewhere()
                        monitor-enter(r12)
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this     // Catch:{ all -> 0x011f }
                        java.util.HashMap r0 = r0.mCallsByCallId     // Catch:{ all -> 0x011f }
                        r0.remove(r5)     // Catch:{ all -> 0x011f }
                        monitor-exit(r12)     // Catch:{ all -> 0x011f }
                        goto L_0x017f
                    L_0x011f:
                        r0 = move-exception
                        monitor-exit(r12)     // Catch:{ all -> 0x011f }
                        throw r0
                    L_0x0122:
                        com.opengarden.firechat.matrixsdk.data.Room r2 = r0.getRoom()
                        if (r2 != 0) goto L_0x012b
                        r0.setRooms(r1, r1)
                    L_0x012b:
                        com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r5
                        r0.handleCallEvent(r1)
                        goto L_0x017f
                    L_0x0131:
                        java.lang.String r0 = "m.call.hangup"
                        com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r5
                        java.lang.String r2 = r2.getType()
                        boolean r0 = r0.equals(r2)
                        if (r0 == 0) goto L_0x017f
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r0 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        com.opengarden.firechat.matrixsdk.call.IMXCall r0 = r0.getCallWithCallId(r5)
                        if (r0 == 0) goto L_0x017f
                        java.lang.String r2 = "IMXCall.CALL_STATE_CREATED"
                        java.lang.String r3 = r0.getCallState()
                        boolean r2 = r2.equals(r3)
                        r2 = r2 ^ r4
                        com.opengarden.firechat.matrixsdk.data.Room r3 = r0.getRoom()
                        if (r3 != 0) goto L_0x015b
                        r0.setRooms(r1, r1)
                    L_0x015b:
                        if (r2 == 0) goto L_0x0162
                        com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r5
                        r0.handleCallEvent(r1)
                    L_0x0162:
                        monitor-enter(r12)
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this     // Catch:{ all -> 0x017c }
                        java.util.HashMap r1 = r1.mCallsByCallId     // Catch:{ all -> 0x017c }
                        r1.remove(r5)     // Catch:{ all -> 0x017c }
                        monitor-exit(r12)     // Catch:{ all -> 0x017c }
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                        android.os.Handler r1 = r1.mUIThreadHandler
                        com.opengarden.firechat.matrixsdk.call.MXCallsManager$2$1 r2 = new com.opengarden.firechat.matrixsdk.call.MXCallsManager$2$1
                        r2.<init>(r0)
                        r1.post(r2)
                        goto L_0x017f
                    L_0x017c:
                        r0 = move-exception
                        monitor-exit(r12)     // Catch:{ all -> 0x017c }
                        throw r0
                    L_0x017f:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23462.run():void");
                }
            });
        }
    }

    public void checkPendingIncomingCalls() {
        this.mUIThreadHandler.post(new Runnable() {
            public void run() {
                if (MXCallsManager.this.mxPendingIncomingCallId.size() > 0) {
                    for (String callWithCallId : MXCallsManager.this.mxPendingIncomingCallId) {
                        final IMXCall callWithCallId2 = MXCallsManager.this.getCallWithCallId(callWithCallId);
                        if (callWithCallId2 != null) {
                            final Room room = callWithCallId2.getRoom();
                            if (room == null || !room.isEncrypted() || !MXCallsManager.this.mSession.getCrypto().warnOnUnknownDevices() || room.getJoinedMembers().size() != 2) {
                                MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                            } else {
                                MXCallsManager.this.mSession.getCrypto().getGlobalBlacklistUnverifiedDevices(new SimpleApiCallback<Boolean>() {
                                    public void onSuccess(Boolean bool) {
                                        if (bool.booleanValue()) {
                                            MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                                        } else {
                                            MXCallsManager.this.mSession.getCrypto().isRoomBlacklistUnverifiedDevices(room.getRoomId(), new SimpleApiCallback<Boolean>() {
                                                public void onSuccess(Boolean bool) {
                                                    if (bool.booleanValue()) {
                                                        MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                                                        return;
                                                    }
                                                    ArrayList arrayList = new ArrayList(room.getJoinedMembers());
                                                    String userId = ((RoomMember) arrayList.get(0)).getUserId();
                                                    String userId2 = ((RoomMember) arrayList.get(1)).getUserId();
                                                    Log.m209d(MXCallsManager.LOG_TAG, "## checkPendingIncomingCalls() : check the unknown devices");
                                                    MXCallsManager.this.mSession.getCrypto().checkUnknownDevices(Arrays.asList(new String[]{userId, userId2}), new ApiCallback<Void>() {
                                                        public void onSuccess(Void voidR) {
                                                            Log.m209d(MXCallsManager.LOG_TAG, "## checkPendingIncomingCalls() : no unknown device");
                                                            MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                                                        }

                                                        public void onNetworkError(Exception exc) {
                                                            String access$300 = MXCallsManager.LOG_TAG;
                                                            StringBuilder sb = new StringBuilder();
                                                            sb.append("## checkPendingIncomingCalls() : checkUnknownDevices failed ");
                                                            sb.append(exc.getMessage());
                                                            Log.m211e(access$300, sb.toString());
                                                            MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                                                        }

                                                        /* JADX WARNING: Removed duplicated region for block: B:7:0x0019  */
                                                        /* JADX WARNING: Removed duplicated region for block: B:8:0x0023  */
                                                        /* Code decompiled incorrectly, please refer to instructions dump. */
                                                        public void onMatrixError(com.opengarden.firechat.matrixsdk.rest.model.MatrixError r5) {
                                                            /*
                                                                r4 = this;
                                                                boolean r0 = r5 instanceof com.opengarden.firechat.matrixsdk.crypto.MXCryptoError
                                                                if (r0 == 0) goto L_0x0016
                                                                r0 = r5
                                                                com.opengarden.firechat.matrixsdk.crypto.MXCryptoError r0 = (com.opengarden.firechat.matrixsdk.crypto.MXCryptoError) r0
                                                                java.lang.String r1 = "UNKNOWN_DEVICES_CODE"
                                                                java.lang.String r2 = r0.errcode
                                                                boolean r1 = r1.equals(r2)
                                                                if (r1 == 0) goto L_0x0016
                                                                java.lang.Object r0 = r0.mExceptionData
                                                                com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap r0 = (com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap) r0
                                                                goto L_0x0017
                                                            L_0x0016:
                                                                r0 = 0
                                                            L_0x0017:
                                                                if (r0 == 0) goto L_0x0023
                                                                java.lang.String r5 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.LOG_TAG
                                                                java.lang.String r1 = "## checkPendingIncomingCalls() : checkUnknownDevices found some unknown devices"
                                                                com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r1)
                                                                goto L_0x003f
                                                            L_0x0023:
                                                                java.lang.String r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.LOG_TAG
                                                                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                                                                r2.<init>()
                                                                java.lang.String r3 = "## checkPendingIncomingCalls() : checkUnknownDevices failed "
                                                                r2.append(r3)
                                                                java.lang.String r5 = r5.getMessage()
                                                                r2.append(r5)
                                                                java.lang.String r5 = r2.toString()
                                                                com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r5)
                                                            L_0x003f:
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager$3$1$1 r5 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.C23491.C23501.this
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager$3$1 r5 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.C23491.this
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager$3 r5 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.this
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager r5 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.this
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager$3$1$1 r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.C23491.C23501.this
                                                                com.opengarden.firechat.matrixsdk.call.MXCallsManager$3$1 r1 = com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.C23491.this
                                                                com.opengarden.firechat.matrixsdk.call.IMXCall r1 = r1
                                                                r5.dispatchOnIncomingCall(r1, r0)
                                                                return
                                                            */
                                                            throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.call.MXCallsManager.C23483.C23491.C23501.C23511.onMatrixError(com.opengarden.firechat.matrixsdk.rest.model.MatrixError):void");
                                                        }

                                                        public void onUnexpectedError(Exception exc) {
                                                            String access$300 = MXCallsManager.LOG_TAG;
                                                            StringBuilder sb = new StringBuilder();
                                                            sb.append("## checkPendingIncomingCalls() : checkUnknownDevices failed ");
                                                            sb.append(exc.getMessage());
                                                            Log.m211e(access$300, sb.toString());
                                                            MXCallsManager.this.dispatchOnIncomingCall(callWithCallId2, null);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
                MXCallsManager.this.mxPendingIncomingCallId.clear();
            }
        });
    }

    public void createCallInRoom(String str, final boolean z, final ApiCallback<IMXCall> apiCallback) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("createCallInRoom in ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        final Room room = this.mSession.getDataHandler().getRoom(str);
        if (room != null) {
            if (isSupported()) {
                int size = room.getJoinedMembers().size();
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("createCallInRoom : the room has ");
                sb2.append(size);
                sb2.append(" joined members");
                Log.m209d(str3, sb2.toString());
                if (size > 1) {
                    if (size != 2) {
                        Log.m209d(LOG_TAG, "createCallInRoom : inviteConferenceUser");
                        inviteConferenceUser(room, new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                Log.m209d(MXCallsManager.LOG_TAG, "createCallInRoom : inviteConferenceUser succeeds");
                                MXCallsManager.this.getConferenceUserRoom(room.getRoomId(), new ApiCallback<Room>() {
                                    public void onSuccess(Room room) {
                                        Log.m209d(MXCallsManager.LOG_TAG, "createCallInRoom : getConferenceUserRoom succeeds");
                                        final IMXCall access$400 = MXCallsManager.this.getCallWithCallId(null, true);
                                        access$400.setRooms(room, room);
                                        access$400.setIsConference(true);
                                        access$400.setIsVideo(z);
                                        MXCallsManager.this.dispatchOnOutgoingCall(access$400);
                                        if (apiCallback != null) {
                                            MXCallsManager.this.mUIThreadHandler.post(new Runnable() {
                                                public void run() {
                                                    apiCallback.onSuccess(access$400);
                                                }
                                            });
                                        }
                                    }

                                    public void onNetworkError(Exception exc) {
                                        String access$300 = MXCallsManager.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("createCallInRoom : getConferenceUserRoom failed ");
                                        sb.append(exc.getMessage());
                                        Log.m209d(access$300, sb.toString());
                                        if (apiCallback != null) {
                                            apiCallback.onNetworkError(exc);
                                        }
                                    }

                                    public void onMatrixError(MatrixError matrixError) {
                                        String access$300 = MXCallsManager.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("createCallInRoom : getConferenceUserRoom failed ");
                                        sb.append(matrixError.getMessage());
                                        Log.m209d(access$300, sb.toString());
                                        if (apiCallback != null) {
                                            apiCallback.onMatrixError(matrixError);
                                        }
                                    }

                                    public void onUnexpectedError(Exception exc) {
                                        String access$300 = MXCallsManager.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("createCallInRoom : getConferenceUserRoom failed ");
                                        sb.append(exc.getMessage());
                                        Log.m209d(access$300, sb.toString());
                                        if (apiCallback != null) {
                                            apiCallback.onUnexpectedError(exc);
                                        }
                                    }
                                });
                            }

                            public void onNetworkError(Exception exc) {
                                String access$300 = MXCallsManager.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("createCallInRoom : inviteConferenceUser fails ");
                                sb.append(exc.getMessage());
                                Log.m209d(access$300, sb.toString());
                                if (apiCallback != null) {
                                    apiCallback.onNetworkError(exc);
                                }
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                String access$300 = MXCallsManager.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("createCallInRoom : inviteConferenceUser fails ");
                                sb.append(matrixError.getMessage());
                                Log.m209d(access$300, sb.toString());
                                if (apiCallback != null) {
                                    apiCallback.onMatrixError(matrixError);
                                }
                            }

                            public void onUnexpectedError(Exception exc) {
                                String access$300 = MXCallsManager.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("createCallInRoom : inviteConferenceUser fails ");
                                sb.append(exc.getMessage());
                                Log.m209d(access$300, sb.toString());
                                if (apiCallback != null) {
                                    apiCallback.onUnexpectedError(exc);
                                }
                            }
                        });
                    } else if (!room.isEncrypted() || !this.mSession.getCrypto().warnOnUnknownDevices()) {
                        final IMXCall callWithCallId = getCallWithCallId(null, true);
                        callWithCallId.setIsVideo(z);
                        dispatchOnOutgoingCall(callWithCallId);
                        callWithCallId.setRooms(room, room);
                        if (apiCallback != null) {
                            this.mUIThreadHandler.post(new Runnable() {
                                public void run() {
                                    apiCallback.onSuccess(callWithCallId);
                                }
                            });
                        }
                    } else {
                        ArrayList arrayList = new ArrayList(room.getJoinedMembers());
                        String userId = ((RoomMember) arrayList.get(0)).getUserId();
                        String userId2 = ((RoomMember) arrayList.get(1)).getUserId();
                        MXCrypto crypto = this.mSession.getCrypto();
                        List asList = Arrays.asList(new String[]{userId, userId2});
                        final boolean z2 = z;
                        final ApiCallback<IMXCall> apiCallback2 = apiCallback;
                        C23524 r1 = new SimpleApiCallback<Void>(apiCallback) {
                            public void onSuccess(Void voidR) {
                                final IMXCall access$400 = MXCallsManager.this.getCallWithCallId(null, true);
                                access$400.setRooms(room, room);
                                access$400.setIsVideo(z2);
                                MXCallsManager.this.dispatchOnOutgoingCall(access$400);
                                if (apiCallback2 != null) {
                                    MXCallsManager.this.mUIThreadHandler.post(new Runnable() {
                                        public void run() {
                                            apiCallback2.onSuccess(access$400);
                                        }
                                    });
                                }
                            }
                        };
                        crypto.checkUnknownDevices(asList, r1);
                    }
                } else if (apiCallback != null) {
                    apiCallback.onMatrixError(new MatrixError(MatrixError.NOT_SUPPORTED, "too few users"));
                }
            } else if (apiCallback != null) {
                apiCallback.onMatrixError(new MatrixError(MatrixError.NOT_SUPPORTED, "VOIP is not supported"));
            }
        } else if (apiCallback != null) {
            apiCallback.onMatrixError(new MatrixError(MatrixError.NOT_FOUND, "room not found"));
        }
    }

    public void pauseTurnServerRefresh() {
        this.mSuspendTurnServerRefresh = true;
    }

    public void unpauseTurnServerRefresh() {
        Log.m209d(LOG_TAG, "unpauseTurnServerRefresh");
        this.mSuspendTurnServerRefresh = false;
        if (this.mTurnServerTimer != null) {
            this.mTurnServerTimer.cancel();
            this.mTurnServerTimer = null;
        }
        refreshTurnServer();
    }

    public void stopTurnServerRefresh() {
        Log.m209d(LOG_TAG, "stopTurnServerRefresh");
        this.mSuspendTurnServerRefresh = true;
        if (this.mTurnServerTimer != null) {
            this.mTurnServerTimer.cancel();
            this.mTurnServerTimer = null;
        }
    }

    private JsonElement getTurnServer() {
        JsonElement jsonElement;
        synchronized (LOG_TAG) {
            jsonElement = this.mTurnServer;
        }
        Log.m209d(LOG_TAG, "getTurnServer ");
        return jsonElement;
    }

    /* access modifiers changed from: private */
    public void refreshTurnServer() {
        if (!this.mSuspendTurnServerRefresh) {
            Log.m209d(LOG_TAG, "## refreshTurnServer () starts");
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    MXCallsManager.this.mCallResClient.getTurnServer(new ApiCallback<JsonObject>() {
                        public void onUnexpectedError(Exception exc) {
                        }

                        private void restartAfter(int i) {
                            if (i <= 0) {
                                String access$300 = MXCallsManager.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## refreshTurnServer() : invalid delay ");
                                sb.append(i);
                                Log.m211e(access$300, sb.toString());
                                return;
                            }
                            if (MXCallsManager.this.mTurnServerTimer != null) {
                                MXCallsManager.this.mTurnServerTimer.cancel();
                            }
                            try {
                                MXCallsManager.this.mTurnServerTimer = new Timer();
                                MXCallsManager.this.mTurnServerTimer.schedule(new TimerTask() {
                                    public void run() {
                                        if (MXCallsManager.this.mTurnServerTimer != null) {
                                            MXCallsManager.this.mTurnServerTimer.cancel();
                                            MXCallsManager.this.mTurnServerTimer = null;
                                        }
                                        MXCallsManager.this.refreshTurnServer();
                                    }
                                }, (long) i);
                            } catch (Throwable unused) {
                                Log.m211e(MXCallsManager.LOG_TAG, "## refreshTurnServer() failed to start the timer");
                                if (MXCallsManager.this.mTurnServerTimer != null) {
                                    MXCallsManager.this.mTurnServerTimer.cancel();
                                    MXCallsManager.this.mTurnServerTimer = null;
                                }
                                MXCallsManager.this.refreshTurnServer();
                            }
                        }

                        public void onSuccess(JsonObject jsonObject) {
                            Log.m209d(MXCallsManager.LOG_TAG, "## refreshTurnServer () : onSuccess");
                            if (jsonObject != null) {
                                if (jsonObject.has("uris")) {
                                    synchronized (MXCallsManager.LOG_TAG) {
                                        MXCallsManager.this.mTurnServer = jsonObject;
                                    }
                                }
                                if (jsonObject.has("ttl")) {
                                    int i = 60000;
                                    try {
                                        int asInt = jsonObject.get("ttl").getAsInt();
                                        try {
                                            i = (asInt * 9) / 10;
                                        } catch (Exception e) {
                                            Exception exc = e;
                                            i = asInt;
                                            e = exc;
                                        }
                                    } catch (Exception e2) {
                                        e = e2;
                                        String access$300 = MXCallsManager.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Fail to retrieve ttl ");
                                        sb.append(e.getMessage());
                                        Log.m211e(access$300, sb.toString());
                                        String access$3002 = MXCallsManager.LOG_TAG;
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("## refreshTurnServer () : onSuccess : retry after ");
                                        sb2.append(i);
                                        sb2.append(" seconds");
                                        Log.m209d(access$3002, sb2.toString());
                                        restartAfter(i * 1000);
                                    }
                                    String access$30022 = MXCallsManager.LOG_TAG;
                                    StringBuilder sb22 = new StringBuilder();
                                    sb22.append("## refreshTurnServer () : onSuccess : retry after ");
                                    sb22.append(i);
                                    sb22.append(" seconds");
                                    Log.m209d(access$30022, sb22.toString());
                                    restartAfter(i * 1000);
                                }
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            String access$300 = MXCallsManager.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshTurnServer () : onNetworkError ");
                            sb.append(exc);
                            Log.m211e(access$300, sb.toString());
                            restartAfter(60000);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            String access$300 = MXCallsManager.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshTurnServer () : onMatrixError() : ");
                            sb.append(matrixError.errcode);
                            Log.m211e(access$300, sb.toString());
                            if (TextUtils.equals(matrixError.errcode, MatrixError.LIMIT_EXCEEDED) && matrixError.retry_after_ms != null) {
                                String access$3002 = MXCallsManager.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("## refreshTurnServer () : onMatrixError() : retry after ");
                                sb2.append(matrixError.retry_after_ms);
                                sb2.append(" ms");
                                Log.m211e(access$3002, sb2.toString());
                                restartAfter(matrixError.retry_after_ms.intValue());
                            }
                        }
                    });
                }
            });
        }
    }

    public static String getConferenceUserId(String str) {
        byte[] bArr;
        if (str == null) {
            return null;
        }
        String str2 = (String) mConferenceUserIdByRoomId.get(str);
        if (str2 == null) {
            try {
                bArr = str.getBytes("UTF-8");
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("conferenceUserIdForRoom failed ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
                bArr = null;
            }
            if (bArr == null) {
                return null;
            }
            String replace = Base64.encodeToString(bArr, 10).replace("=", "");
            StringBuilder sb2 = new StringBuilder();
            sb2.append("@fs_");
            sb2.append(replace);
            sb2.append(":");
            sb2.append(DOMAIN);
            str2 = sb2.toString();
            mConferenceUserIdByRoomId.put(str, str2);
        }
        return str2;
    }

    public static boolean isConferenceUserId(String str) {
        boolean z;
        if (mConferenceUserIdByRoomId.values().contains(str)) {
            return true;
        }
        String str2 = "@fs_";
        String str3 = ":matrix.org";
        if (!TextUtils.isEmpty(str) && str.startsWith(str2) && str.endsWith(str3)) {
            try {
                z = MXSession.isRoomId(new String(Base64.decode(str.substring(str2.length(), str.length() - str3.length()), 10), "UTF-8"));
            } catch (Exception e) {
                String str4 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("isConferenceUserId : failed ");
                sb.append(e.getMessage());
                Log.m211e(str4, sb.toString());
            }
            return z;
        }
        z = false;
        return z;
    }

    private void inviteConferenceUser(Room room, final ApiCallback<Void> apiCallback) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("inviteConferenceUser ");
        sb.append(room.getRoomId());
        Log.m209d(str, sb.toString());
        String conferenceUserId = getConferenceUserId(room.getRoomId());
        RoomMember member = room.getMember(conferenceUserId);
        if (member == null || !TextUtils.equals(member.membership, RoomMember.MEMBERSHIP_JOIN)) {
            room.invite(conferenceUserId, apiCallback);
        } else {
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(null);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void getConferenceUserRoom(String str, final ApiCallback<Room> apiCallback) {
        final Room room;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getConferenceUserRoom with room id ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        String conferenceUserId = getConferenceUserId(str);
        Iterator it = this.mSession.getDataHandler().getStore().getRooms().iterator();
        while (true) {
            if (!it.hasNext()) {
                room = null;
                break;
            }
            room = (Room) it.next();
            if (room.isConferenceUserRoom() && 2 == room.getMembers().size() && room.getMember(conferenceUserId) != null) {
                break;
            }
        }
        if (room != null) {
            Log.m209d(LOG_TAG, "getConferenceUserRoom : the room already exists");
            this.mSession.getDataHandler().getStore().commit();
            this.mUIThreadHandler.post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(room);
                }
            });
            return;
        }
        Log.m209d(LOG_TAG, "getConferenceUserRoom : create the room");
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.preset = CreateRoomParams.PRESET_PRIVATE_CHAT;
        createRoomParams.invite = Arrays.asList(new String[]{conferenceUserId});
        this.mSession.createRoom(createRoomParams, new ApiCallback<String>() {
            public void onSuccess(String str) {
                Log.m209d(MXCallsManager.LOG_TAG, "getConferenceUserRoom : the room creation succeeds");
                Room room = MXCallsManager.this.mSession.getDataHandler().getRoom(str);
                if (room != null) {
                    room.setIsConferenceUserRoom(true);
                    MXCallsManager.this.mSession.getDataHandler().getStore().commit();
                    apiCallback.onSuccess(room);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$300 = MXCallsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("getConferenceUserRoom : failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$300, sb.toString());
                apiCallback.onNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$300 = MXCallsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("getConferenceUserRoom : failed ");
                sb.append(matrixError.getMessage());
                Log.m209d(access$300, sb.toString());
                apiCallback.onMatrixError(matrixError);
            }

            public void onUnexpectedError(Exception exc) {
                String access$300 = MXCallsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("getConferenceUserRoom : failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$300, sb.toString());
                apiCallback.onUnexpectedError(exc);
            }
        });
    }

    public void addListener(IMXCallsManagerListener iMXCallsManagerListener) {
        if (iMXCallsManagerListener != null) {
            synchronized (this) {
                this.mListeners.add(iMXCallsManagerListener);
            }
        }
    }

    public void removeListener(IMXCallsManagerListener iMXCallsManagerListener) {
        if (iMXCallsManagerListener != null) {
            synchronized (this) {
                this.mListeners.remove(iMXCallsManagerListener);
            }
        }
    }

    private Collection<IMXCallsManagerListener> getListeners() {
        HashSet hashSet;
        synchronized (this) {
            hashSet = new HashSet(this.mListeners);
        }
        return hashSet;
    }

    /* access modifiers changed from: private */
    public void dispatchOnIncomingCall(IMXCall iMXCall, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("dispatchOnIncomingCall ");
        sb.append(iMXCall.getCallId());
        Log.m209d(str, sb.toString());
        for (IMXCallsManagerListener onIncomingCall : getListeners()) {
            try {
                onIncomingCall.onIncomingCall(iMXCall, mXUsersDevicesMap);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("dispatchOnIncomingCall ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnOutgoingCall(IMXCall iMXCall) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("dispatchOnOutgoingCall ");
        sb.append(iMXCall.getCallId());
        Log.m209d(str, sb.toString());
        for (IMXCallsManagerListener onOutgoingCall : getListeners()) {
            try {
                onOutgoingCall.onOutgoingCall(iMXCall);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("dispatchOnOutgoingCall ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnCallHangUp(IMXCall iMXCall) {
        Log.m209d(LOG_TAG, "dispatchOnCallHangUp");
        for (IMXCallsManagerListener onCallHangUp : getListeners()) {
            try {
                onCallHangUp.onCallHangUp(iMXCall);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("dispatchOnCallHangUp ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnVoipConferenceStarted(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("dispatchOnVoipConferenceStarted : ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        for (IMXCallsManagerListener onVoipConferenceStarted : getListeners()) {
            try {
                onVoipConferenceStarted.onVoipConferenceStarted(str);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("dispatchOnVoipConferenceStarted ");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnVoipConferenceFinished(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onVoipConferenceFinished : ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        for (IMXCallsManagerListener onVoipConferenceFinished : getListeners()) {
            try {
                onVoipConferenceFinished.onVoipConferenceFinished(str);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("dispatchOnVoipConferenceFinished ");
                sb2.append(e.getMessage());
                Log.m211e(str3, sb2.toString());
            }
        }
    }
}
