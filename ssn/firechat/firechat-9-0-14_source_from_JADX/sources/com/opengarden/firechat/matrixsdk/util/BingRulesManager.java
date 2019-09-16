package com.opengarden.firechat.matrixsdk.util;

import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.network.NetworkConnectivityReceiver;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.PushRulesRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.Condition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.ContainsDisplayNameCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.ContentRule;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.EventMatchCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRuleSet;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRulesResponse;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.RoomMemberCountCondition;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.SenderNotificationPermissionCondition;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class BingRulesManager {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "BingRulesManager";
    private final PushRulesRestClient mApiClient;
    private final Set<onBingRulesUpdateListener> mBingRulesUpdateListeners = new HashSet();
    private final MXDataHandler mDataHandler;
    private BingRule mDefaultBingRule = new BingRule(true);
    /* access modifiers changed from: private */
    public boolean mIsInitialized = false;
    private final Map<String, Boolean> mIsMentionOnlyMap = new HashMap();
    /* access modifiers changed from: private */
    public ApiCallback<Void> mLoadRulesCallback;
    private final String mMyUserId;
    private NetworkConnectivityReceiver mNetworkConnectivityReceiver;
    private IMXNetworkEventListener mNetworkListener;
    /* access modifiers changed from: private */
    public Map<String, RoomNotificationState> mRoomNotificationStateByRoomId = new HashMap();
    private final List<BingRule> mRules = new ArrayList();
    private PushRuleSet mRulesSet = new PushRuleSet();
    private final MXSession mSession;

    public enum RoomNotificationState {
        ALL_MESSAGES_NOISY,
        ALL_MESSAGES,
        MENTIONS_ONLY,
        MUTE
    }

    public interface onBingRuleUpdateListener {
        void onBingRuleUpdateFailure(String str);

        void onBingRuleUpdateSuccess();
    }

    public interface onBingRulesUpdateListener {
        void onBingRulesUpdate();
    }

    public BingRulesManager(MXSession mXSession, NetworkConnectivityReceiver networkConnectivityReceiver) {
        this.mSession = mXSession;
        this.mApiClient = mXSession.getBingRulesApiClient();
        this.mMyUserId = mXSession.getCredentials().userId;
        this.mDataHandler = mXSession.getDataHandler();
        this.mNetworkListener = new IMXNetworkEventListener() {
            public void onNetworkConnectionUpdate(boolean z) {
                if (z && BingRulesManager.this.mLoadRulesCallback != null) {
                    BingRulesManager.this.loadRules(BingRulesManager.this.mLoadRulesCallback);
                }
            }
        };
        this.mNetworkConnectivityReceiver = networkConnectivityReceiver;
        networkConnectivityReceiver.addEventListener(this.mNetworkListener);
    }

    public boolean isReady() {
        return this.mIsInitialized;
    }

    /* access modifiers changed from: private */
    public void removeNetworkListener() {
        if (this.mNetworkConnectivityReceiver != null && this.mNetworkListener != null) {
            this.mNetworkConnectivityReceiver.removeEventListener(this.mNetworkListener);
            this.mNetworkConnectivityReceiver = null;
            this.mNetworkListener = null;
        }
    }

    public void addBingRulesUpdateListener(onBingRulesUpdateListener onbingrulesupdatelistener) {
        if (onbingrulesupdatelistener != null) {
            this.mBingRulesUpdateListeners.add(onbingrulesupdatelistener);
        }
    }

    public void removeBingRulesUpdateListener(onBingRulesUpdateListener onbingrulesupdatelistener) {
        if (onbingrulesupdatelistener != null) {
            this.mBingRulesUpdateListeners.remove(onbingrulesupdatelistener);
        }
    }

    private void onBingRulesUpdate() {
        this.mRoomNotificationStateByRoomId.clear();
        for (onBingRulesUpdateListener onBingRulesUpdate : this.mBingRulesUpdateListeners) {
            try {
                onBingRulesUpdate.onBingRulesUpdate();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onBingRulesUpdate() : onBingRulesUpdate failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void loadRules(final ApiCallback<Void> apiCallback) {
        this.mLoadRulesCallback = null;
        Log.m209d(LOG_TAG, "## loadRules() : refresh the bing rules");
        this.mApiClient.getAllRules(new ApiCallback<PushRulesResponse>() {
            public void onSuccess(PushRulesResponse pushRulesResponse) {
                Log.m209d(BingRulesManager.LOG_TAG, "## loadRules() : succeeds");
                BingRulesManager.this.buildRules(pushRulesResponse);
                BingRulesManager.this.mIsInitialized = true;
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
                BingRulesManager.this.removeNetworkListener();
            }

            private void onError(String str) {
                String access$100 = BingRulesManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## loadRules() : failed ");
                sb.append(str);
                Log.m211e(access$100, sb.toString());
                BingRulesManager.this.mLoadRulesCallback = apiCallback;
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getMessage());
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                onError(matrixError.getMessage());
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getMessage());
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    private static boolean caseInsensitiveFind(String str, String str2) {
        boolean z;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("(\\W|^)");
            sb.append(str);
            sb.append("(\\W|$)");
            z = Pattern.compile(sb.toString(), 2).matcher(str2).find();
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("caseInsensitiveFind : pattern.matcher failed with ");
            sb2.append(e.getMessage());
            Log.m211e(str3, sb2.toString());
            z = false;
        }
        return z;
    }

    public BingRule fulfilledHighlightBingRule(Event event) {
        return fulfilledBingRule(event, true);
    }

    public BingRule fulfilledBingRule(Event event) {
        return fulfilledBingRule(event, false);
    }

    private BingRule fulfilledBingRule(Event event, boolean z) {
        ArrayList arrayList;
        String str;
        if (event == null) {
            Log.m211e(LOG_TAG, "## fulfilledBingRule() : null event");
            return null;
        } else if (!this.mIsInitialized) {
            Log.m211e(LOG_TAG, "## fulfilledBingRule() : not initialized");
            return null;
        } else if (this.mRules.size() == 0) {
            Log.m211e(LOG_TAG, "## fulfilledBingRule() : no rules");
            return null;
        } else if (event.getSender() != null && TextUtils.equals(event.getSender(), this.mMyUserId)) {
            return null;
        } else {
            String type = event.getType();
            if (TextUtils.equals(type, Event.EVENT_TYPE_PRESENCE) || TextUtils.equals(type, Event.EVENT_TYPE_TYPING) || TextUtils.equals(type, Event.EVENT_TYPE_REDACTION) || TextUtils.equals(type, Event.EVENT_TYPE_RECEIPT) || TextUtils.equals(type, Event.EVENT_TYPE_TAGS)) {
                return null;
            }
            synchronized (this) {
                arrayList = new ArrayList(this.mRules);
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                BingRule bingRule = (BingRule) it.next();
                if (bingRule.isEnabled && (!z || bingRule.shouldHighlight())) {
                    boolean z2 = false;
                    if (!BingRule.RULE_ID_CONTAIN_USER_NAME.equals(bingRule.ruleId) && !BingRule.RULE_ID_CONTAIN_DISPLAY_NAME.equals(bingRule.ruleId)) {
                        z2 = BingRule.RULE_ID_FALLBACK.equals(bingRule.ruleId) ? true : eventMatchesConditions(event, bingRule.conditions);
                    } else if (Event.EVENT_TYPE_MESSAGE.equals(event.getType())) {
                        Message message = JsonUtils.toMessage(event.getContent());
                        MyUser myUser = this.mSession.getMyUser();
                        if (BingRule.RULE_ID_CONTAIN_USER_NAME.equals(bingRule.ruleId)) {
                            str = this.mMyUserId.indexOf(":") >= 0 ? this.mMyUserId.substring(1, this.mMyUserId.indexOf(":")) : this.mMyUserId;
                        } else if (BingRule.RULE_ID_CONTAIN_DISPLAY_NAME.equals(bingRule.ruleId)) {
                            str = myUser.displayname;
                            if (!(this.mSession.getDataHandler() == null || this.mSession.getDataHandler().getStore() == null)) {
                                Room room = this.mSession.getDataHandler().getStore().getRoom(event.roomId);
                                if (!(room == null || room.getState() == null)) {
                                    String memberName = room.getState().getMemberName(this.mMyUserId);
                                    if (!TextUtils.equals(memberName, this.mMyUserId)) {
                                        str = Pattern.quote(memberName);
                                    }
                                }
                            }
                        } else {
                            str = null;
                        }
                        if (!TextUtils.isEmpty(str)) {
                            z2 = caseInsensitiveFind(str, message.body);
                        }
                    }
                    if (z2) {
                        return bingRule;
                    }
                }
            }
            return null;
        }
    }

    private boolean eventMatchesConditions(Event event, List<Condition> list) {
        if (!(list == null || event == null)) {
            try {
                for (Condition condition : list) {
                    if (condition instanceof EventMatchCondition) {
                        if (!((EventMatchCondition) condition).isSatisfied(event)) {
                            return false;
                        }
                    } else if (condition instanceof ContainsDisplayNameCondition) {
                        if (event.roomId != null) {
                            Room room = this.mDataHandler.getRoom(event.roomId, false);
                            if (!(room == null || room.getMember(this.mMyUserId) == null)) {
                                if (!((ContainsDisplayNameCondition) condition).isSatisfied(event, room.getMember(this.mMyUserId).displayname)) {
                                    return false;
                                }
                            }
                        } else {
                            continue;
                        }
                    } else if (condition instanceof RoomMemberCountCondition) {
                        if (event.roomId != null) {
                            if (!((RoomMemberCountCondition) condition).isSatisfied(this.mDataHandler.getRoom(event.roomId, false))) {
                                return false;
                            }
                        } else {
                            continue;
                        }
                    } else if (!(condition instanceof SenderNotificationPermissionCondition)) {
                        return false;
                    } else {
                        if (event.roomId != null && !((SenderNotificationPermissionCondition) condition).isSatisfied(this.mDataHandler.getRoom(event.roomId, false).getState().getPowerLevels(), event.sender)) {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## eventMatchesConditions() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
                return false;
            }
        }
        return true;
    }

    public void buildRules(PushRulesResponse pushRulesResponse) {
        if (pushRulesResponse != null) {
            updateRulesSet(pushRulesResponse.global);
            onBingRulesUpdate();
        }
    }

    public PushRuleSet pushRules() {
        return this.mRulesSet;
    }

    private void updateRulesSet(PushRuleSet pushRuleSet) {
        synchronized (this) {
            this.mRules.clear();
            if (pushRuleSet == null) {
                this.mRulesSet = new PushRuleSet();
                return;
            }
            if (pushRuleSet.override != null) {
                pushRuleSet.override = new ArrayList(pushRuleSet.override);
                for (BingRule bingRule : pushRuleSet.override) {
                    bingRule.kind = BingRule.KIND_OVERRIDE;
                }
                this.mRules.addAll(pushRuleSet.override);
            } else {
                pushRuleSet.override = new ArrayList(pushRuleSet.override);
            }
            if (pushRuleSet.content != null) {
                pushRuleSet.content = new ArrayList(pushRuleSet.content);
                for (BingRule bingRule2 : pushRuleSet.content) {
                    bingRule2.kind = "content";
                }
                addContentRules(pushRuleSet.content);
            } else {
                pushRuleSet.content = new ArrayList();
            }
            this.mIsMentionOnlyMap.clear();
            if (pushRuleSet.room != null) {
                pushRuleSet.room = new ArrayList(pushRuleSet.room);
                for (BingRule bingRule3 : pushRuleSet.room) {
                    bingRule3.kind = BingRule.KIND_ROOM;
                }
                addRoomRules(pushRuleSet.room);
            } else {
                pushRuleSet.room = new ArrayList();
            }
            if (pushRuleSet.sender != null) {
                pushRuleSet.sender = new ArrayList(pushRuleSet.sender);
                for (BingRule bingRule4 : pushRuleSet.sender) {
                    bingRule4.kind = BingRule.KIND_SENDER;
                }
                addSenderRules(pushRuleSet.sender);
            } else {
                pushRuleSet.sender = new ArrayList();
            }
            if (pushRuleSet.underride != null) {
                pushRuleSet.underride = new ArrayList(pushRuleSet.underride);
                for (BingRule bingRule5 : pushRuleSet.underride) {
                    bingRule5.kind = BingRule.KIND_UNDERRIDE;
                }
                this.mRules.addAll(pushRuleSet.underride);
            } else {
                pushRuleSet.underride = new ArrayList();
            }
            this.mRulesSet = pushRuleSet;
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## updateRules() : has ");
            sb.append(this.mRules.size());
            sb.append(" rules");
            Log.m209d(str, sb.toString());
        }
    }

    private void addContentRules(List<ContentRule> list) {
        if (list != null) {
            for (ContentRule contentRule : list) {
                EventMatchCondition eventMatchCondition = new EventMatchCondition();
                eventMatchCondition.kind = Condition.KIND_EVENT_MATCH;
                eventMatchCondition.key = "content.body";
                eventMatchCondition.pattern = contentRule.pattern;
                contentRule.addCondition(eventMatchCondition);
                this.mRules.add(contentRule);
            }
        }
    }

    private void addRoomRules(List<BingRule> list) {
        if (list != null) {
            for (BingRule bingRule : list) {
                EventMatchCondition eventMatchCondition = new EventMatchCondition();
                eventMatchCondition.kind = Condition.KIND_EVENT_MATCH;
                eventMatchCondition.key = "room_id";
                eventMatchCondition.pattern = bingRule.ruleId;
                bingRule.addCondition(eventMatchCondition);
                this.mRules.add(bingRule);
            }
        }
    }

    private void addSenderRules(List<BingRule> list) {
        if (list != null) {
            for (BingRule bingRule : list) {
                EventMatchCondition eventMatchCondition = new EventMatchCondition();
                eventMatchCondition.kind = Condition.KIND_EVENT_MATCH;
                eventMatchCondition.key = AmplitudeClient.USER_ID_KEY;
                eventMatchCondition.pattern = bingRule.ruleId;
                bingRule.addCondition(eventMatchCondition);
                this.mRules.add(bingRule);
            }
        }
    }

    /* access modifiers changed from: private */
    public void forceRulesRefresh(final String str, final onBingRuleUpdateListener onbingruleupdatelistener) {
        if (onbingruleupdatelistener != null) {
            this.mRoomNotificationStateByRoomId.clear();
            loadRules(new ApiCallback<Void>() {
                private void onDone(String str) {
                    BingRulesManager.this.mRoomNotificationStateByRoomId.clear();
                    try {
                        if (!TextUtils.isEmpty(str) || !TextUtils.isEmpty(str)) {
                            onBingRuleUpdateListener onbingruleupdatelistener = onbingruleupdatelistener;
                            if (!TextUtils.isEmpty(str)) {
                                str = str;
                            }
                            onbingruleupdatelistener.onBingRuleUpdateFailure(str);
                            return;
                        }
                        onbingruleupdatelistener.onBingRuleUpdateSuccess();
                    } catch (Exception e) {
                        String access$100 = BingRulesManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## forceRulesRefresh() : failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }

                public void onSuccess(Void voidR) {
                    onDone(null);
                }

                public void onNetworkError(Exception exc) {
                    onDone(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onDone(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onDone(exc.getLocalizedMessage());
                }
            });
        }
    }

    private ApiCallback<Void> getUpdateCallback(final onBingRuleUpdateListener onbingruleupdatelistener) {
        return new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                BingRulesManager.this.forceRulesRefresh(null, onbingruleupdatelistener);
            }

            private void onError(String str) {
                BingRulesManager.this.forceRulesRefresh(str, onbingruleupdatelistener);
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                onError(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }
        };
    }

    public void updateEnableRuleStatus(BingRule bingRule, boolean z, onBingRuleUpdateListener onbingruleupdatelistener) {
        if (bingRule != null) {
            this.mApiClient.updateEnableRuleStatus(bingRule.kind, bingRule.ruleId, z, getUpdateCallback(onbingruleupdatelistener));
        }
    }

    public void deleteRule(BingRule bingRule, onBingRuleUpdateListener onbingruleupdatelistener) {
        if (bingRule == null) {
            if (onbingruleupdatelistener != null) {
                try {
                    onbingruleupdatelistener.onBingRuleUpdateSuccess();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## deleteRule : onBingRuleUpdateSuccess failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            return;
        }
        this.mApiClient.deleteRule(bingRule.kind, bingRule.ruleId, getUpdateCallback(onbingruleupdatelistener));
    }

    public void deleteRules(List<BingRule> list, onBingRuleUpdateListener onbingruleupdatelistener) {
        deleteRules(list, 0, onbingruleupdatelistener);
    }

    /* access modifiers changed from: private */
    public void deleteRules(final List<BingRule> list, final int i, final onBingRuleUpdateListener onbingruleupdatelistener) {
        if (list == null || i >= list.size()) {
            onBingRulesUpdate();
            if (onbingruleupdatelistener != null) {
                try {
                    onbingruleupdatelistener.onBingRuleUpdateSuccess();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## deleteRules() : onBingRuleUpdateSuccess failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            return;
        }
        deleteRule((BingRule) list.get(i), new onBingRuleUpdateListener() {
            public void onBingRuleUpdateSuccess() {
                BingRulesManager.this.deleteRules(list, i + 1, onbingruleupdatelistener);
            }

            public void onBingRuleUpdateFailure(String str) {
                if (onbingruleupdatelistener != null) {
                    try {
                        onbingruleupdatelistener.onBingRuleUpdateFailure(str);
                    } catch (Exception e) {
                        String access$100 = BingRulesManager.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## deleteRules() : onBingRuleUpdateFailure failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
    }

    public void addRule(BingRule bingRule, onBingRuleUpdateListener onbingruleupdatelistener) {
        if (bingRule == null) {
            if (onbingruleupdatelistener != null) {
                try {
                    onbingruleupdatelistener.onBingRuleUpdateSuccess();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## addRule : onBingRuleUpdateSuccess failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            return;
        }
        this.mApiClient.addRule(bingRule, getUpdateCallback(onbingruleupdatelistener));
    }

    public void updateRule(final BingRule bingRule, final BingRule bingRule2, final onBingRuleUpdateListener onbingruleupdatelistener) {
        if (bingRule == null) {
            addRule(bingRule2, onbingruleupdatelistener);
        } else if (bingRule2 == null) {
            deleteRule(bingRule, onbingruleupdatelistener);
        } else if (bingRule.isEnabled != bingRule2.isEnabled) {
            this.mApiClient.updateEnableRuleStatus(bingRule2.kind, bingRule2.ruleId, bingRule2.isEnabled, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    bingRule.isEnabled = bingRule2.isEnabled;
                    BingRulesManager.this.updateRule(bingRule, bingRule2, onbingruleupdatelistener);
                }

                public void onNetworkError(Exception exc) {
                    BingRulesManager.this.forceRulesRefresh(exc.getLocalizedMessage(), onbingruleupdatelistener);
                }

                public void onMatrixError(MatrixError matrixError) {
                    BingRulesManager.this.forceRulesRefresh(matrixError.getLocalizedMessage(), onbingruleupdatelistener);
                }

                public void onUnexpectedError(Exception exc) {
                    BingRulesManager.this.forceRulesRefresh(exc.getLocalizedMessage(), onbingruleupdatelistener);
                }
            });
        } else if (bingRule.actions != bingRule2.actions) {
            HashMap hashMap = new HashMap();
            ArrayList arrayList = new ArrayList();
            if (bingRule2.actions != null) {
                if (bingRule2.actions.contains(BingRule.ACTION_NOTIFY)) {
                    arrayList.add(BingRule.ACTION_NOTIFY);
                }
                if (bingRule2.actions.contains(BingRule.ACTION_DONT_NOTIFY)) {
                    arrayList.add(BingRule.ACTION_DONT_NOTIFY);
                }
                if (bingRule2.getActionMap(BingRule.ACTION_SET_TWEAK_SOUND_VALUE) != null) {
                    arrayList.add(bingRule2.getActionMap(BingRule.ACTION_SET_TWEAK_SOUND_VALUE));
                }
                if (bingRule2.getActionMap(BingRule.ACTION_SET_TWEAK_HIGHLIGHT_VALUE) != null) {
                    arrayList.add(bingRule2.getActionMap(BingRule.ACTION_SET_TWEAK_HIGHLIGHT_VALUE));
                }
            }
            hashMap.put("actions", arrayList);
            this.mApiClient.updateRuleActions(bingRule2.kind, bingRule2.ruleId, hashMap, new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    bingRule.actions = bingRule2.actions;
                    BingRulesManager.this.updateRule(bingRule, bingRule2, onbingruleupdatelistener);
                }

                public void onNetworkError(Exception exc) {
                    BingRulesManager.this.forceRulesRefresh(exc.getLocalizedMessage(), onbingruleupdatelistener);
                }

                public void onMatrixError(MatrixError matrixError) {
                    BingRulesManager.this.forceRulesRefresh(matrixError.getLocalizedMessage(), onbingruleupdatelistener);
                }

                public void onUnexpectedError(Exception exc) {
                    BingRulesManager.this.forceRulesRefresh(exc.getLocalizedMessage(), onbingruleupdatelistener);
                }
            });
        } else {
            forceRulesRefresh(null, onbingruleupdatelistener);
        }
    }

    private List<BingRule> getPushRulesForRoomId(String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str) && this.mRulesSet != null) {
            if (this.mRulesSet.override != null) {
                for (BingRule bingRule : this.mRulesSet.override) {
                    if (TextUtils.equals(bingRule.ruleId, str)) {
                        arrayList.add(bingRule);
                    }
                }
            }
            if (this.mRulesSet.room != null) {
                for (BingRule bingRule2 : this.mRulesSet.room) {
                    if (TextUtils.equals(bingRule2.ruleId, str)) {
                        arrayList.add(bingRule2);
                    }
                }
            }
        }
        return arrayList;
    }

    public RoomNotificationState getRoomNotificationState(String str) {
        if (TextUtils.isEmpty(str)) {
            return RoomNotificationState.ALL_MESSAGES;
        }
        if (this.mRoomNotificationStateByRoomId.containsKey(str)) {
            return (RoomNotificationState) this.mRoomNotificationStateByRoomId.get(str);
        }
        RoomNotificationState roomNotificationState = RoomNotificationState.ALL_MESSAGES;
        Iterator it = getPushRulesForRoomId(str).iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            BingRule bingRule = (BingRule) it.next();
            if (bingRule.isEnabled) {
                if (bingRule.shouldNotNotify()) {
                    roomNotificationState = TextUtils.equals(bingRule.kind, BingRule.KIND_OVERRIDE) ? RoomNotificationState.MUTE : RoomNotificationState.MENTIONS_ONLY;
                } else if (bingRule.shouldNotify()) {
                    roomNotificationState = bingRule.getNotificationSound() != null ? RoomNotificationState.ALL_MESSAGES_NOISY : RoomNotificationState.ALL_MESSAGES;
                }
            }
        }
        this.mRoomNotificationStateByRoomId.put(str, roomNotificationState);
        return roomNotificationState;
    }

    public void updateRoomNotificationState(final String str, final RoomNotificationState roomNotificationState, final onBingRuleUpdateListener onbingruleupdatelistener) {
        deleteRules(getPushRulesForRoomId(str), new onBingRuleUpdateListener() {
            public void onBingRuleUpdateSuccess() {
                BingRule bingRule;
                if (roomNotificationState == RoomNotificationState.ALL_MESSAGES) {
                    BingRulesManager.this.forceRulesRefresh(null, onbingruleupdatelistener);
                    return;
                }
                if (roomNotificationState == RoomNotificationState.ALL_MESSAGES_NOISY) {
                    bingRule = new BingRule(BingRule.KIND_ROOM, str, Boolean.valueOf(true), Boolean.valueOf(false), true);
                } else {
                    bingRule = new BingRule(roomNotificationState == RoomNotificationState.MENTIONS_ONLY ? BingRule.KIND_ROOM : BingRule.KIND_OVERRIDE, str, Boolean.valueOf(false), null, false);
                    EventMatchCondition eventMatchCondition = new EventMatchCondition();
                    eventMatchCondition.key = "room_id";
                    eventMatchCondition.pattern = str;
                    bingRule.addCondition(eventMatchCondition);
                }
                BingRulesManager.this.addRule(bingRule, onbingruleupdatelistener);
            }

            public void onBingRuleUpdateFailure(String str) {
                onbingruleupdatelistener.onBingRuleUpdateFailure(str);
            }
        });
    }

    public boolean isRoomMentionOnly(String str) {
        return RoomNotificationState.MENTIONS_ONLY == getRoomNotificationState(str);
    }

    public boolean isRoomNotificationsDisabled(String str) {
        RoomNotificationState roomNotificationState = getRoomNotificationState(str);
        return RoomNotificationState.MENTIONS_ONLY == roomNotificationState || RoomNotificationState.MUTE == roomNotificationState;
    }
}
