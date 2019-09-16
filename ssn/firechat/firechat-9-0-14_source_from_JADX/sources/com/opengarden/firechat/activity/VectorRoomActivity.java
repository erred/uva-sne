package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.gson.JsonParser;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.ViewedRoomTracker;
import com.opengarden.firechat.activity.util.RequestCodesKt;
import com.opengarden.firechat.fragments.VectorMessageListFragment;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.call.IMXCallListener;
import com.opengarden.firechat.matrixsdk.call.MXCallListener;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoAlgorithms;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomEmailInvitation;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment;
import com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment.OnItemClickListener;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.IEventSendingListener;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.IOnScrollListener;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.IRoomPreviewDataListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXLatestChatMessageCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import com.opengarden.firechat.notifications.NotificationUtils;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.CameraApplicationsUtil;
import com.opengarden.firechat.util.ExternalApplicationsUtilKt;
import com.opengarden.firechat.util.MatrixURLSpan;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.ReadMarkerManager;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.SlashCommandsParser;
import com.opengarden.firechat.util.SlashCommandsParser.SlashCommand;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorMarkdownParser.IVectorMarkdownParserListener;
import com.opengarden.firechat.util.VectorRoomMediasSender;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.ActiveWidgetsBanner;
import com.opengarden.firechat.view.ActiveWidgetsBanner.onUpdateListener;
import com.opengarden.firechat.view.VectorAutoCompleteTextView;
import com.opengarden.firechat.view.VectorOngoingConferenceCallView;
import com.opengarden.firechat.view.VectorOngoingConferenceCallView.ICallClickListener;
import com.opengarden.firechat.view.VectorPendingCallView;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.StringUtils;

public class VectorRoomActivity extends MXCActionBarActivity implements IRoomPreviewDataListener, IEventSendingListener, IOnScrollListener {
    private static final String CAMERA_VALUE_TITLE = "attachment";
    private static final String E2E_WARNINGS_PREFERENCES = "E2E_WARNINGS_PREFERENCES";
    public static final String EXTRA_DEFAULT_NAME = "EXTRA_DEFAULT_NAME";
    public static final String EXTRA_DEFAULT_TOPIC = "EXTRA_DEFAULT_TOPIC";
    public static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";
    public static final String EXTRA_EXPAND_ROOM_HEADER = "EXTRA_EXPAND_ROOM_HEADER";
    public static final String EXTRA_IS_UNREAD_PREVIEW_MODE = "EXTRA_IS_UNREAD_PREVIEW_MODE";
    public static final String EXTRA_MATRIX_ID = "MXCActionBarActivity.EXTRA_MATRIX_ID";
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    public static final String EXTRA_ROOM_INTENT = "EXTRA_ROOM_INTENT";
    public static final String EXTRA_ROOM_PREVIEW_ID = "EXTRA_ROOM_PREVIEW_ID";
    public static final String EXTRA_ROOM_PREVIEW_ROOM_ALIAS = "EXTRA_ROOM_PREVIEW_ROOM_ALIAS";
    public static final String EXTRA_START_CALL_ID = "EXTRA_START_CALL_ID";
    private static final String FIRST_VISIBLE_ROW = "FIRST_VISIBLE_ROW";
    public static final int GET_MENTION_REQUEST_CODE = 2;
    private static final boolean HIDE_ACTION_BAR_HEADER = false;
    private static final int INVITE_USER_REQUEST_CODE = 4;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomActivity";
    public static final int RECORD_AUDIO_REQUEST_CODE = 6;
    private static final int REQUEST_FILES_REQUEST_CODE = 0;
    private static final int REQUEST_ROOM_AVATAR_CODE = 3;
    private static final boolean SHOW_ACTION_BAR_HEADER = true;
    /* access modifiers changed from: private */
    public static final String TAG = "VectorRoomActivity";
    private static final String TAG_FRAGMENT_ATTACHMENTS_DIALOG = "TAG_FRAGMENT_ATTACHMENTS_DIALOG";
    private static final String TAG_FRAGMENT_CALL_OPTIONS = "TAG_FRAGMENT_CALL_OPTIONS";
    private static final String TAG_FRAGMENT_MATRIX_MESSAGE_LIST = "TAG_FRAGMENT_MATRIX_MESSAGE_LIST";
    private static final int TAKE_IMAGE_REQUEST_CODE = 1;
    private static final int TYPING_TIMEOUT_MS = 10000;
    public static final int UNREAD_PREVIEW_REQUEST_CODE = 5;
    public static RoomPreviewData sRoomPreviewData;
    private ImageView mActionBarCustomArrowImageView;
    private TextView mActionBarCustomTitle;
    private TextView mActionBarCustomTopic;
    private View mActionBarHeaderActiveMembersInviteButton;
    private View mActionBarHeaderActiveMembersLayout;
    private View mActionBarHeaderActiveMembersListButton;
    private TextView mActionBarHeaderActiveMembersTextView;
    private ImageView mActionBarHeaderRoomAvatar;
    private TextView mActionBarHeaderRoomName;
    private TextView mActionBarHeaderRoomTopic;
    private ActiveWidgetsBanner mActiveWidgetsBanner;
    /* access modifiers changed from: private */
    public AutoCompletionMode mAutoCompletionMode;
    private ImageView mAvatarImageView;
    private String mCallId = null;
    private final IMXCallListener mCallListener = new MXCallListener() {
        public void onPreviewSizeChanged(int i, int i2) {
        }

        public void onCallError(String str) {
            VectorRoomActivity.this.refreshCallButtons(true);
        }

        public void onCallAnsweredElsewhere() {
            VectorRoomActivity.this.refreshCallButtons(true);
        }

        public void onCallEnd(int i) {
            VectorRoomActivity.this.refreshCallButtons(true);
        }
    };
    /* access modifiers changed from: private */
    public int mCameraPermissionAction;
    private View mCanNotPostTextView;
    private String mDefaultRoomName;
    private String mDefaultTopic;
    private final ApiCallback<Void> mDirectMessageListener = new SimpleApiCallback<Void>(this) {
        public void onSuccess(Void voidR) {
        }

        public void onMatrixError(MatrixError matrixError) {
            if (MatrixError.FORBIDDEN.equals(matrixError.errcode)) {
                Toast.makeText(VectorRoomActivity.this, matrixError.error, 1).show();
            }
        }

        public void onNetworkError(Exception exc) {
            Toast.makeText(VectorRoomActivity.this, exc.getMessage(), 1).show();
        }

        public void onUnexpectedError(Exception exc) {
            Toast.makeText(VectorRoomActivity.this, exc.getMessage(), 1).show();
        }
    };
    /* access modifiers changed from: private */
    public ImageView mE2eImageView;
    /* access modifiers changed from: private */
    public VectorAutoCompleteTextView mEditText;
    /* access modifiers changed from: private */
    public String mEventId;
    private final MXEventListener mGlobalEventListener = new MXEventListener() {
        public void onPresenceUpdate(Event event, User user) {
            VectorRoomActivity.this.updateRoomHeaderMembersStatus();
        }

        public void onLeaveRoom(String str) {
            if (VectorRoomActivity.sRoomPreviewData != null && TextUtils.equals(VectorRoomActivity.sRoomPreviewData.getRoomId(), str)) {
                Log.m209d(VectorRoomActivity.LOG_TAG, "The room invitation has been declined from another client");
                VectorRoomActivity.this.onDeclined();
            }
        }

        public void onJoinRoom(String str) {
            if (VectorRoomActivity.sRoomPreviewData != null && TextUtils.equals(VectorRoomActivity.sRoomPreviewData.getRoomId(), str)) {
                VectorRoomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.m209d(VectorRoomActivity.LOG_TAG, "The room invitation has been accepted from another client");
                        VectorRoomActivity.this.onJoined();
                    }
                });
            }
        }

        public void onLiveEventsChunkProcessed(String str, String str2) {
            VectorRoomActivity.this.mSyncInProgressView.setVisibility(8);
        }
    };
    /* access modifiers changed from: private */
    public boolean mIgnoreTextUpdate = false;
    private boolean mIsHeaderViewDisplayed = false;
    /* access modifiers changed from: private */
    public boolean mIsMarkDowning;
    private Boolean mIsScrolledToTheBottom;
    private boolean mIsUnreadPreviewMode;
    /* access modifiers changed from: private */
    public long mLastTypingDate = 0;
    /* access modifiers changed from: private */
    public MXLatestChatMessageCache mLatestChatMessageCache;
    private Event mLatestDisplayedEvent;
    private String mLatestTakePictureCameraUri = null;
    private String mLatestTypingMessage;
    private String mMyUserId;
    private final IMXNetworkEventListener mNetworkEventListener = new IMXNetworkEventListener() {
        public void onNetworkConnectionUpdate(boolean z) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.refreshNotificationsArea();
                    VectorRoomActivity.this.refreshCallButtons(true);
                }
            });
        }
    };
    private ImageView mNotificationIconImageView;
    private TextView mNotificationTextView;
    private View mNotificationsArea;
    /* access modifiers changed from: private */
    public ReadMarkerManager mReadMarkerManager;
    private MenuItem mResendDeleteMenuItem;
    private MenuItem mResendUnsentMenuItem;
    /* access modifiers changed from: private */
    @Nullable
    public Room mRoom;
    private final MXEventListener mRoomEventListener = new MXEventListener() {
        public void onRoomFlush(String str) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.updateActionBarTitleAndTopic();
                    VectorRoomActivity.this.updateRoomHeaderMembersStatus();
                    VectorRoomActivity.this.updateRoomHeaderAvatar();
                }
            });
        }

        public void onLeaveRoom(String str) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.finish();
                }
            });
        }

        public void onRoomKick(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomActivity.this.mSession.getMyUserId());
            hashMap.put("EXTRA_ROOM_ID", VectorRoomActivity.this.mRoom.getRoomId());
            Intent intent = new Intent(VectorRoomActivity.this, VectorHomeActivity.class);
            intent.setFlags(872415232);
            intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, hashMap);
            VectorRoomActivity.this.startActivity(intent);
        }

        public void onLiveEvent(final Event event, RoomState roomState) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r4 = this;
                        com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r2
                        java.lang.String r0 = r0.getType()
                        java.lang.String r1 = com.opengarden.firechat.activity.VectorRoomActivity.LOG_TAG
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = "Received event type: "
                        r2.append(r3)
                        r2.append(r0)
                        java.lang.String r2 = r2.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r1, r2)
                        int r1 = r0.hashCode()
                        r2 = 1
                        r3 = 0
                        switch(r1) {
                            case -2077370164: goto L_0x006e;
                            case -1995536872: goto L_0x0064;
                            case -612186677: goto L_0x005a;
                            case -338982155: goto L_0x0050;
                            case -283996404: goto L_0x0046;
                            case -2395523: goto L_0x003c;
                            case 138277757: goto L_0x0032;
                            case 915435739: goto L_0x0028;
                            default: goto L_0x0027;
                        }
                    L_0x0027:
                        goto L_0x0078
                    L_0x0028:
                        java.lang.String r1 = "m.room.power_levels"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 4
                        goto L_0x0079
                    L_0x0032:
                        java.lang.String r1 = "m.room.name"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 0
                        goto L_0x0079
                    L_0x003c:
                        java.lang.String r1 = "m.room.topic"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 3
                        goto L_0x0079
                    L_0x0046:
                        java.lang.String r1 = "m.room.member"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 2
                        goto L_0x0079
                    L_0x0050:
                        java.lang.String r1 = "m.room.encryption"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 7
                        goto L_0x0079
                    L_0x005a:
                        java.lang.String r1 = "m.room.avatar"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 6
                        goto L_0x0079
                    L_0x0064:
                        java.lang.String r1 = "m.typing"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 5
                        goto L_0x0079
                    L_0x006e:
                        java.lang.String r1 = "m.room.aliases"
                        boolean r1 = r0.equals(r1)
                        if (r1 == 0) goto L_0x0078
                        r1 = 1
                        goto L_0x0079
                    L_0x0078:
                        r1 = -1
                    L_0x0079:
                        switch(r1) {
                            case 0: goto L_0x00f4;
                            case 1: goto L_0x00f4;
                            case 2: goto L_0x00f4;
                            case 3: goto L_0x00e0;
                            case 4: goto L_0x00d8;
                            case 5: goto L_0x00d0;
                            case 6: goto L_0x00c8;
                            case 7: goto L_0x007e;
                            default: goto L_0x007c;
                        }
                    L_0x007c:
                        goto L_0x0109
                    L_0x007e:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        com.opengarden.firechat.matrixsdk.data.Room r1 = r1.mRoom
                        boolean r1 = r1.isEncrypted()
                        if (r1 == 0) goto L_0x009b
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        com.opengarden.firechat.matrixsdk.MXSession r1 = r1.mSession
                        boolean r1 = r1.isCryptoEnabled()
                        if (r1 == 0) goto L_0x009b
                        goto L_0x009c
                    L_0x009b:
                        r2 = 0
                    L_0x009c:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        android.widget.ImageView r1 = r1.mE2eImageView
                        if (r2 == 0) goto L_0x00aa
                        r2 = 2131230854(0x7f080086, float:1.8077773E38)
                        goto L_0x00ad
                    L_0x00aa:
                        r2 = 2131230853(0x7f080085, float:1.807777E38)
                    L_0x00ad:
                        r1.setImageResource(r2)
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        com.opengarden.firechat.fragments.VectorMessageListFragment r1 = r1.mVectorMessageListFragment
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r2 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r2 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        com.opengarden.firechat.matrixsdk.data.Room r2 = r2.mRoom
                        boolean r2 = r2.isEncrypted()
                        r1.setIsRoomEncrypted(r2)
                        goto L_0x0109
                    L_0x00c8:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.updateRoomHeaderAvatar()
                        goto L_0x0109
                    L_0x00d0:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.onRoomTypings()
                        goto L_0x0109
                    L_0x00d8:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.checkSendEventStatus()
                        goto L_0x0109
                    L_0x00e0:
                        com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r2
                        com.google.gson.JsonElement r1 = r1.getContent()
                        com.opengarden.firechat.matrixsdk.data.RoomState r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toRoomState(r1)
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r2 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r2 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        java.lang.String r1 = r1.topic
                        r2.setTopic(r1)
                        goto L_0x0109
                    L_0x00f4:
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.setTitle()
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.updateRoomHeaderMembersStatus()
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r1 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r1 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r1.updateRoomHeaderAvatar()
                    L_0x0109:
                        boolean r1 = com.opengarden.firechat.VectorApp.isAppInBackground()
                        if (r1 != 0) goto L_0x0128
                        java.lang.String r1 = "m.typing"
                        boolean r0 = r1.equals(r0)
                        if (r0 != 0) goto L_0x0128
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r0 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r0 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        com.opengarden.firechat.matrixsdk.data.Room r0 = r0.mRoom
                        if (r0 == 0) goto L_0x0128
                        com.opengarden.firechat.activity.VectorRoomActivity$4 r0 = com.opengarden.firechat.activity.VectorRoomActivity.C16384.this
                        com.opengarden.firechat.activity.VectorRoomActivity r0 = com.opengarden.firechat.activity.VectorRoomActivity.this
                        r0.refreshNotificationsArea()
                    L_0x0128:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorRoomActivity.C16384.C16413.run():void");
                }
            });
        }

        public void onRoomInitialSyncComplete(String str) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.mVectorMessageListFragment.onInitialMessagesLoaded();
                    VectorRoomActivity.this.updateActionBarTitleAndTopic();
                }
            });
        }

        public void onBingRulesUpdate() {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.updateActionBarTitleAndTopic();
                    VectorRoomActivity.this.mVectorMessageListFragment.onBingRulesUpdate();
                }
            });
        }

        public void onEventSentStateUpdated(Event event) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.refreshNotificationsArea();
                }
            });
        }

        public void onEventSent(Event event, String str) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.refreshNotificationsArea();
                }
            });
        }

        public void onReceiptEvent(String str, List<String> list) {
            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomActivity.this.refreshNotificationsArea();
                }
            });
        }

        public void onReadMarkerEvent(String str) {
            if (VectorRoomActivity.this.mReadMarkerManager != null) {
                VectorRoomActivity.this.mReadMarkerManager.onReadMarkerChanged(str);
            }
        }
    };
    /* access modifiers changed from: private */
    public LinearLayout mRoomHeaderView;
    private View mRoomPreviewLayout;
    private int mScrollToIndex = -1;
    private MenuItem mSearchInRoomMenuItem;
    private View mSendButtonLayout;
    /* access modifiers changed from: private */
    public ImageView mSendImageView;
    private View mSendingMessagesLayout;
    /* access modifiers changed from: private */
    public MXSession mSession;
    /* access modifiers changed from: private */
    public View mStartCallLayout;
    /* access modifiers changed from: private */
    public View mStopCallLayout;
    /* access modifiers changed from: private */
    public View mSyncInProgressView;
    private Toolbar mToolbar;
    /* access modifiers changed from: private */
    public Timer mTypingTimer = null;
    /* access modifiers changed from: private */
    public TimerTask mTypingTimerTask;
    private MenuItem mUseMatrixAppsMenuItem;
    /* access modifiers changed from: private */
    public VectorMessageListFragment mVectorMessageListFragment;
    private VectorOngoingConferenceCallView mVectorOngoingConferenceCallView;
    /* access modifiers changed from: private */
    public VectorPendingCallView mVectorPendingCallView;
    private VectorRoomMediasSender mVectorRoomMediasSender;

    private enum AutoCompletionMode {
        USER_MODE("@"),
        COMMAND_MODE("/");
        
        private String startChar;

        private AutoCompletionMode(String str) {
            this.startChar = str;
        }

        /* JADX WARNING: Removed duplicated region for block: B:12:0x0025  */
        /* JADX WARNING: Removed duplicated region for block: B:13:0x0027  */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x002a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static com.opengarden.firechat.activity.VectorRoomActivity.AutoCompletionMode getAutoCompletionMode(java.lang.String r2) {
            /*
                int r0 = r2.hashCode()
                r1 = 47
                if (r0 == r1) goto L_0x0017
                r1 = 64
                if (r0 == r1) goto L_0x000d
                goto L_0x0021
            L_0x000d:
                java.lang.String r0 = "@"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x0021
                r2 = 0
                goto L_0x0022
            L_0x0017:
                java.lang.String r0 = "/"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x0021
                r2 = 1
                goto L_0x0022
            L_0x0021:
                r2 = -1
            L_0x0022:
                switch(r2) {
                    case 0: goto L_0x002a;
                    case 1: goto L_0x0027;
                    default: goto L_0x0025;
                }
            L_0x0025:
                r2 = 0
                goto L_0x002c
            L_0x0027:
                com.opengarden.firechat.activity.VectorRoomActivity$AutoCompletionMode r2 = COMMAND_MODE
                goto L_0x002c
            L_0x002a:
                com.opengarden.firechat.activity.VectorRoomActivity$AutoCompletionMode r2 = USER_MODE
            L_0x002c:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorRoomActivity.AutoCompletionMode.getAutoCompletionMode(java.lang.String):com.opengarden.firechat.activity.VectorRoomActivity$AutoCompletionMode");
        }
    }

    private class cancelAllClickableSpan extends ClickableSpan {
        private cancelAllClickableSpan() {
        }

        public void onClick(View view) {
            VectorRoomActivity.this.mVectorMessageListFragment.deleteUnsentEvents();
            VectorRoomActivity.this.refreshNotificationsArea();
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setColor(ContextCompat.getColor(VectorRoomActivity.this, C1299R.color.vector_fuchsia_color));
            textPaint.bgColor = 0;
            textPaint.setUnderlineText(true);
        }
    }

    private class resendAllClickableSpan extends ClickableSpan {
        private resendAllClickableSpan() {
        }

        public void onClick(View view) {
            VectorRoomActivity.this.mVectorMessageListFragment.resendUnsentMessages();
            VectorRoomActivity.this.refreshNotificationsArea();
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setColor(ContextCompat.getColor(VectorRoomActivity.this, C1299R.color.vector_fuchsia_color));
            textPaint.bgColor = 0;
            textPaint.setUnderlineText(true);
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_room;
    }

    public void initUiAndData() {
        setWaitingView(findViewById(C1299R.C1301id.main_progress_layout));
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "onCreate : Restart the application.");
            CommonActivityUtils.restartApp(this);
            return;
        }
        final Intent intent = getIntent();
        if (!intent.hasExtra("EXTRA_ROOM_ID")) {
            Log.m211e(LOG_TAG, "No room ID extra.");
            finish();
            return;
        }
        this.mSession = getSession(intent);
        if (this.mSession == null || !this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "No MXSession.");
            finish();
            return;
        }
        String stringExtra = intent.getStringExtra("EXTRA_ROOM_ID");
        RoomMember roomMember = null;
        if (!intent.hasExtra(EXTRA_ROOM_PREVIEW_ID)) {
            sRoomPreviewData = null;
            Matrix.getInstance(this).clearTmpStoresList();
        }
        if (CommonActivityUtils.isGoingToSplash(this, this.mSession.getMyUserId(), stringExtra)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
            return;
        }
        this.mRoomHeaderView = (LinearLayout) findViewById(C1299R.C1301id.action_bar_header);
        this.mActionBarHeaderRoomTopic = (TextView) findViewById(C1299R.C1301id.action_bar_header_room_topic);
        this.mActionBarHeaderRoomName = (TextView) findViewById(C1299R.C1301id.action_bar_header_room_title);
        this.mActionBarHeaderActiveMembersLayout = findViewById(C1299R.C1301id.action_bar_header_room_members_layout);
        this.mActionBarHeaderActiveMembersTextView = (TextView) findViewById(C1299R.C1301id.action_bar_header_room_members_text_view);
        this.mActionBarHeaderActiveMembersListButton = findViewById(C1299R.C1301id.action_bar_header_room_members_settings_view);
        this.mActionBarHeaderActiveMembersInviteButton = findViewById(C1299R.C1301id.action_bar_header_room_members_invite_view);
        this.mActionBarHeaderRoomAvatar = (ImageView) this.mRoomHeaderView.findViewById(C1299R.C1301id.avatar_img);
        this.mRoomPreviewLayout = findViewById(C1299R.C1301id.room_preview_info_layout);
        this.mVectorPendingCallView = (VectorPendingCallView) findViewById(C1299R.C1301id.room_pending_call_view);
        this.mVectorOngoingConferenceCallView = (VectorOngoingConferenceCallView) findViewById(C1299R.C1301id.room_ongoing_conference_call_view);
        this.mActiveWidgetsBanner = (ActiveWidgetsBanner) findViewById(C1299R.C1301id.room_pending_widgets_view);
        this.mE2eImageView = (ImageView) findViewById(C1299R.C1301id.room_encrypted_image_view);
        this.mSyncInProgressView = findViewById(C1299R.C1301id.room_sync_in_progress);
        findViewById(C1299R.C1301id.room_bottom_layout).setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                VectorRoomActivity.this.enableActionBarHeader(false);
                return false;
            }
        });
        this.mToolbar = (Toolbar) findViewById(C1299R.C1301id.room_toolbar);
        setSupportActionBar(this.mToolbar);
        boolean z = true;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setActionBarDefaultCustomLayout();
        this.mCallId = intent.getStringExtra(EXTRA_START_CALL_ID);
        this.mEventId = intent.getStringExtra(EXTRA_EVENT_ID);
        this.mDefaultRoomName = intent.getStringExtra(EXTRA_DEFAULT_NAME);
        this.mDefaultTopic = intent.getStringExtra(EXTRA_DEFAULT_TOPIC);
        this.mIsUnreadPreviewMode = intent.getBooleanExtra(EXTRA_IS_UNREAD_PREVIEW_MODE, false);
        if (intent.getAction() != null && intent.getAction().startsWith(NotificationUtils.TAP_TO_VIEW_ACTION)) {
            NotificationUtils.INSTANCE.cancelAllNotifications(this);
        }
        if (this.mIsUnreadPreviewMode) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Displaying ");
            sb.append(stringExtra);
            sb.append(" in unread preview mode");
            Log.m209d(str, sb.toString());
        } else if (!TextUtils.isEmpty(this.mEventId) || sRoomPreviewData != null) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Displaying ");
            sb2.append(stringExtra);
            sb2.append(" in preview mode");
            Log.m209d(str2, sb2.toString());
        } else {
            String str3 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Displaying ");
            sb3.append(stringExtra);
            Log.m209d(str3, sb3.toString());
        }
        this.mEditText = (VectorAutoCompleteTextView) findViewById(C1299R.C1301id.editText_messageBox);
        this.mEditText.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VectorRoomActivity.this.enableActionBarHeader(false);
            }
        });
        this.mEditText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (6 == (i & 255)) {
                    VectorRoomActivity.this.sendTextMessage();
                }
                if (keyEvent == null || keyEvent.isShiftPressed() || keyEvent.getKeyCode() != 66 || VectorRoomActivity.this.getResources().getConfiguration().keyboard == 1) {
                    return false;
                }
                VectorRoomActivity.this.sendTextMessage();
                return true;
            }
        });
        manageKeyboardOptionsToSendMessage();
        this.mRoom = this.mSession.getDataHandler().getRoom(stringExtra, false);
        this.mEditText.initAutoCompletion(this.mSession, this.mRoom != null ? this.mRoom.getRoomId() : null);
        this.mEditText.initAutoCompletionCommandLine(this.mSession, (String) null);
        this.mEditText.setAddColonOnFirstItem(true);
        this.mSendingMessagesLayout = findViewById(C1299R.C1301id.room_sending_message_layout);
        this.mSendImageView = (ImageView) findViewById(C1299R.C1301id.room_send_image_view);
        this.mSendImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                final Integer[] numArr;
                Integer[] numArr2;
                if (!TextUtils.isEmpty(VectorRoomActivity.this.mEditText.getText())) {
                    VectorRoomActivity.this.sendTextMessage();
                    return;
                }
                VectorRoomActivity.this.enableActionBarHeader(false);
                FragmentManager supportFragmentManager = VectorRoomActivity.this.getSupportFragmentManager();
                IconAndTextDialogFragment iconAndTextDialogFragment = (IconAndTextDialogFragment) supportFragmentManager.findFragmentByTag(VectorRoomActivity.TAG_FRAGMENT_ATTACHMENTS_DIALOG);
                if (iconAndTextDialogFragment != null) {
                    iconAndTextDialogFragment.dismissAllowingStateLoss();
                }
                if (PreferencesManager.useNativeCamera(VectorRoomActivity.this)) {
                    numArr = new Integer[]{Integer.valueOf(C1299R.string.option_send_files), Integer.valueOf(C1299R.string.option_send_sticker), Integer.valueOf(C1299R.string.option_take_photo), Integer.valueOf(C1299R.string.option_take_video)};
                    numArr2 = new Integer[]{Integer.valueOf(C1299R.C1300drawable.ic_material_file), Integer.valueOf(C1299R.C1300drawable.ic_send_sticker), Integer.valueOf(C1299R.C1300drawable.ic_material_camera), Integer.valueOf(C1299R.C1300drawable.ic_material_videocam)};
                } else {
                    numArr = new Integer[]{Integer.valueOf(C1299R.string.option_send_files), Integer.valueOf(C1299R.string.option_send_sticker), Integer.valueOf(C1299R.string.option_take_photo_video)};
                    numArr2 = new Integer[]{Integer.valueOf(C1299R.C1300drawable.ic_material_file), Integer.valueOf(C1299R.C1300drawable.ic_send_sticker), Integer.valueOf(C1299R.C1300drawable.ic_material_camera)};
                }
                IconAndTextDialogFragment newInstance = IconAndTextDialogFragment.newInstance(numArr2, numArr, Integer.valueOf(ThemeUtils.INSTANCE.getColor(VectorRoomActivity.this, C1299R.attr.riot_primary_background_color)), Integer.valueOf(ThemeUtils.INSTANCE.getColor(VectorRoomActivity.this, C1299R.attr.riot_primary_text_color)));
                newInstance.setOnClickListener(new OnItemClickListener() {
                    public void onItemClick(IconAndTextDialogFragment iconAndTextDialogFragment, int i) {
                        Integer num = numArr[i];
                        if (num.intValue() == C1299R.string.option_send_files) {
                            VectorRoomActivity.this.launchFileSelectionIntent();
                        } else if (num.intValue() == C1299R.string.option_send_sticker) {
                            VectorRoomActivity.this.startStickerPickerActivity();
                        } else if (num.intValue() == C1299R.string.option_take_photo_video) {
                            if (CommonActivityUtils.checkPermissions(3, (Activity) VectorRoomActivity.this)) {
                                VectorRoomActivity.this.launchNativeCamera();
                            } else {
                                VectorRoomActivity.this.mCameraPermissionAction = C1299R.string.option_take_photo_video;
                            }
                        } else if (num.intValue() == C1299R.string.option_take_photo) {
                            if (CommonActivityUtils.checkPermissions(3, (Activity) VectorRoomActivity.this)) {
                                VectorRoomActivity.this.launchNativeCamera();
                            } else {
                                VectorRoomActivity.this.mCameraPermissionAction = C1299R.string.option_take_photo;
                            }
                        } else if (num.intValue() != C1299R.string.option_take_video) {
                        } else {
                            if (CommonActivityUtils.checkPermissions(3, (Activity) VectorRoomActivity.this)) {
                                VectorRoomActivity.this.launchNativeVideoRecorder();
                            } else {
                                VectorRoomActivity.this.mCameraPermissionAction = C1299R.string.option_take_video;
                            }
                        }
                    }
                });
                newInstance.show(supportFragmentManager, VectorRoomActivity.TAG_FRAGMENT_ATTACHMENTS_DIALOG);
            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (VectorRoomActivity.this.mRoom != null) {
                    boolean z = false;
                    if (!VectorRoomActivity.this.mEditText.getText().toString().isEmpty()) {
                        VectorRoomActivity.this.setAutoCompletionMode(AutoCompletionMode.getAutoCompletionMode(VectorRoomActivity.this.mEditText.getText().toString().substring(0, 1)));
                        VectorRoomActivity.this.setAutoCompletionParam(VectorRoomActivity.this.mAutoCompletionMode);
                    }
                    MXLatestChatMessageCache access$2900 = VectorRoomActivity.this.mLatestChatMessageCache;
                    String latestText = access$2900.getLatestText(VectorRoomActivity.this, VectorRoomActivity.this.mRoom.getRoomId());
                    if (!VectorRoomActivity.this.mIgnoreTextUpdate && !latestText.equals(VectorRoomActivity.this.mEditText.getText().toString())) {
                        access$2900.updateLatestMessage(VectorRoomActivity.this, VectorRoomActivity.this.mRoom.getRoomId(), VectorRoomActivity.this.mEditText.getText().toString());
                        VectorRoomActivity vectorRoomActivity = VectorRoomActivity.this;
                        if (VectorRoomActivity.this.mEditText.getText().length() != 0) {
                            z = true;
                        }
                        vectorRoomActivity.handleTypingNotification(z);
                    }
                    VectorRoomActivity.this.manageSendMoreButtons();
                    VectorRoomActivity.this.refreshCallButtons(true);
                }
            }
        });
        this.mVectorPendingCallView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                IMXCall activeCall = CallsManager.getSharedInstance().getActiveCall();
                if (activeCall != null) {
                    final Intent intent = new Intent(VectorRoomActivity.this, VectorCallViewActivity.class);
                    intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, activeCall.getSession().getCredentials().userId);
                    intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, activeCall.getCallId());
                    VectorRoomActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            VectorRoomActivity.this.startActivity(intent);
                        }
                    });
                    return;
                }
                VectorRoomActivity.this.mVectorPendingCallView.onCallTerminated();
            }
        });
        this.mNotificationsArea = findViewById(C1299R.C1301id.room_notifications_area);
        this.mNotificationIconImageView = (ImageView) this.mNotificationsArea.findViewById(C1299R.C1301id.room_notification_icon);
        this.mNotificationTextView = (TextView) this.mNotificationsArea.findViewById(C1299R.C1301id.room_notification_message);
        this.mCanNotPostTextView = findViewById(C1299R.C1301id.room_cannot_post_textview);
        findViewById(C1299R.C1301id.room_sending_message_layout).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorRoomActivity.this.mEditText.requestFocus()) {
                    ((InputMethodManager) VectorRoomActivity.this.getSystemService("input_method")).showSoftInput(VectorRoomActivity.this.mEditText, 1);
                }
            }
        });
        this.mStartCallLayout = findViewById(C1299R.C1301id.room_start_call_image_view);
        this.mStartCallLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorRoomActivity.this.mRoom != null && VectorRoomActivity.this.mRoom.isEncrypted() && VectorRoomActivity.this.mRoom.getActiveMembers().size() > 2) {
                    Builder builder = new Builder(VectorRoomActivity.this);
                    Resources resources = VectorRoomActivity.this.getResources();
                    builder.setMessage(resources.getString(C1299R.string.room_no_conference_call_in_encrypted_rooms));
                    builder.setIcon(17301543);
                    builder.setPositiveButton(resources.getString(C1299R.string.f115ok), null);
                    builder.show();
                } else if (!VectorRoomActivity.this.isUserAllowedToStartConfCall()) {
                    VectorRoomActivity.this.displayConfCallNotAllowed();
                } else if (VectorRoomActivity.this.mRoom.getActiveMembers().size() > 2) {
                    Builder builder2 = new Builder(VectorRoomActivity.this);
                    builder2.setTitle(C1299R.string.conference_call_warning_title);
                    builder2.setMessage(C1299R.string.conference_call_warning_message);
                    builder2.setIcon(17301543);
                    builder2.setPositiveButton(C1299R.string.f115ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (PreferencesManager.useJitsiConfCall(VectorRoomActivity.this)) {
                                VectorRoomActivity.this.startJitsiCall(true);
                            } else {
                                VectorRoomActivity.this.displayVideoCallIpDialog();
                            }
                        }
                    });
                    builder2.setNegativeButton(C1299R.string.cancel, null);
                    builder2.show();
                } else {
                    VectorRoomActivity.this.displayVideoCallIpDialog();
                }
            }
        });
        this.mStopCallLayout = findViewById(C1299R.C1301id.room_end_call_image_view);
        this.mStopCallLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CallsManager.getSharedInstance().onHangUp(null);
            }
        });
        findViewById(C1299R.C1301id.room_button_margin_right).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorRoomActivity.this.mStopCallLayout.getVisibility() == 0) {
                    VectorRoomActivity.this.mStopCallLayout.performClick();
                } else if (VectorRoomActivity.this.mStartCallLayout.getVisibility() == 0) {
                    VectorRoomActivity.this.mStartCallLayout.performClick();
                } else if (VectorRoomActivity.this.mSendImageView.getVisibility() == 0) {
                    VectorRoomActivity.this.mSendImageView.performClick();
                }
            }
        });
        this.mMyUserId = this.mSession.getCredentials().userId;
        CommonActivityUtils.resumeEventStream(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        this.mVectorMessageListFragment = (VectorMessageListFragment) supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_MATRIX_MESSAGE_LIST);
        if (this.mVectorMessageListFragment == null) {
            Log.m209d(LOG_TAG, "Create VectorMessageListFragment");
            String str4 = sRoomPreviewData == null ? this.mIsUnreadPreviewMode ? MatrixMessageListFragment.PREVIEW_MODE_UNREAD_MESSAGE : null : MatrixMessageListFragment.PREVIEW_MODE_READ_ONLY;
            this.mVectorMessageListFragment = VectorMessageListFragment.newInstance(this.mMyUserId, stringExtra, this.mEventId, str4, C1299R.layout.fragment_matrix_message_list_fragment);
            supportFragmentManager.beginTransaction().add(C1299R.C1301id.anchor_fragment_messages, this.mVectorMessageListFragment, TAG_FRAGMENT_MATRIX_MESSAGE_LIST).commit();
        } else {
            Log.m209d(LOG_TAG, "Reuse VectorMessageListFragment");
        }
        this.mVectorRoomMediasSender = new VectorRoomMediasSender(this, this.mVectorMessageListFragment, Matrix.getInstance(this).getMediasCache());
        manageRoomPreview();
        addRoomHeaderClickListeners();
        if (this.mRoom != null) {
            roomMember = this.mRoom.getMember(this.mMyUserId);
        }
        if (roomMember == null || !roomMember.kickedOrBanned()) {
            z = false;
        }
        if (!TextUtils.isEmpty(this.mEventId) || sRoomPreviewData != null || z) {
            if (!this.mIsUnreadPreviewMode || z) {
                this.mNotificationsArea.setVisibility(8);
                findViewById(C1299R.C1301id.bottom_separator).setVisibility(8);
                findViewById(C1299R.C1301id.room_notification_separator).setVisibility(8);
                findViewById(C1299R.C1301id.room_notifications_area).setVisibility(8);
            }
            View findViewById = findViewById(C1299R.C1301id.room_bottom_layout);
            LayoutParams layoutParams = findViewById.getLayoutParams();
            layoutParams.height = 0;
            findViewById.setLayoutParams(layoutParams);
        }
        if (sRoomPreviewData == null && z) {
            manageBannedHeader(roomMember);
        }
        this.mLatestChatMessageCache = Matrix.getInstance(this).getDefaultLatestChatMessageCache();
        if (intent.hasExtra(EXTRA_ROOM_INTENT)) {
            if (isFirstCreation()) {
                final Intent intent2 = (Intent) intent.getParcelableExtra(EXTRA_ROOM_INTENT);
                if (intent2 != null) {
                    this.mEditText.postDelayed(new Runnable() {
                        public void run() {
                            intent.removeExtra(VectorRoomActivity.EXTRA_ROOM_INTENT);
                            VectorRoomActivity.this.sendMediasIntent(intent2);
                        }
                    }, 1000);
                }
            } else {
                intent.removeExtra(EXTRA_ROOM_INTENT);
                Log.m211e(LOG_TAG, "## onCreate() : ignore EXTRA_ROOM_INTENT because savedInstanceState != null");
            }
        }
        this.mActiveWidgetsBanner.initRoomInfo(this.mSession, this.mRoom);
        this.mActiveWidgetsBanner.setOnUpdateListener(new onUpdateListener() {
            public void onActiveWidgetsListUpdate() {
            }

            public void onCloseWidgetClick(final Widget widget) {
                new Builder(VectorRoomActivity.this).setMessage(C1299R.string.widget_delete_message_confirmation).setPositiveButton(C1299R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        VectorRoomActivity.this.showWaitingView();
                        WidgetsManager.getSharedInstance().closeWidget(VectorRoomActivity.this.mSession, VectorRoomActivity.this.mRoom, widget.getWidgetId(), new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                VectorRoomActivity.this.hideWaitingView();
                            }

                            private void onError(String str) {
                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
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
                        });
                    }
                }).setNegativeButton(C1299R.string.cancel, null).show();
            }

            /* access modifiers changed from: private */
            public void displayWidget(Widget widget) {
                VectorRoomActivity.this.startActivity(WidgetActivity.Companion.getIntent(VectorRoomActivity.this, widget));
            }

            public void onClick(final List<Widget> list) {
                if (list.size() == 1) {
                    displayWidget((Widget) list.get(0));
                } else if (list.size() > 1) {
                    ArrayList arrayList = new ArrayList();
                    CharSequence[] charSequenceArr = new CharSequence[arrayList.size()];
                    for (Widget humanName : list) {
                        arrayList.add(humanName.getHumanName());
                    }
                    new Builder(VectorRoomActivity.this).setSingleChoiceItems((CharSequence[]) arrayList.toArray(charSequenceArr), 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            C160217.this.displayWidget((Widget) list.get(i));
                        }
                    }).setNegativeButton(C1299R.string.cancel, null).show();
                }
            }
        });
        this.mVectorOngoingConferenceCallView.initRoomInfo(this.mSession, this.mRoom);
        this.mVectorOngoingConferenceCallView.setCallClickListener(new ICallClickListener() {
            private void startCall(boolean z) {
                if (CommonActivityUtils.checkPermissions(z ? 5 : 4, (Activity) VectorRoomActivity.this)) {
                    VectorRoomActivity.this.startIpCall(false, z);
                }
            }

            private void onCallClick(Widget widget, boolean z) {
                if (widget != null) {
                    VectorRoomActivity.this.launchJitsiActivity(widget, z);
                } else {
                    startCall(z);
                }
            }

            public void onVoiceCallClick(Widget widget) {
                onCallClick(widget, false);
            }

            public void onVideoCallClick(Widget widget) {
                onCallClick(widget, true);
            }

            public void onCloseWidgetClick(Widget widget) {
                VectorRoomActivity.this.showWaitingView();
                WidgetsManager.getSharedInstance().closeWidget(VectorRoomActivity.this.mSession, VectorRoomActivity.this.mRoom, widget.getWidgetId(), new ApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        VectorRoomActivity.this.hideWaitingView();
                    }

                    private void onError(String str) {
                        CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
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
                });
            }

            public void onActiveWidgetUpdate() {
                VectorRoomActivity.this.refreshCallButtons(false);
            }
        });
        View findViewById2 = findViewById(C1299R.C1301id.room_self_avatar);
        if (findViewById2 != null) {
            this.mAvatarImageView = (ImageView) findViewById2.findViewById(C1299R.C1301id.avatar_img);
        }
        refreshSelfAvatar();
        this.mVectorRoomMediasSender.resumeResizeMediaAndSend();
        enableActionBarHeader(intent.getBooleanExtra(EXTRA_EXPAND_ROOM_HEADER, false));
        intent.removeExtra(EXTRA_EXPAND_ROOM_HEADER);
        if (this.mIsUnreadPreviewMode || (this.mRoom != null && this.mRoom.getLiveTimeLine() != null && this.mRoom.getLiveTimeLine().isLiveTimeline() && TextUtils.isEmpty(this.mEventId))) {
            if (this.mRoom == null) {
                Log.m211e(LOG_TAG, "## onCreate() : null room");
            } else if (this.mSession.getDataHandler().getStore().getSummary(this.mRoom.getRoomId()) == null) {
                Log.m211e(LOG_TAG, "## onCreate() : there is no summary for this room");
            } else {
                ReadMarkerManager readMarkerManager = new ReadMarkerManager(this, this.mVectorMessageListFragment, this.mSession, this.mRoom, this.mIsUnreadPreviewMode ? 1 : 0, findViewById(C1299R.C1301id.jump_to_first_unread));
                this.mReadMarkerManager = readMarkerManager;
            }
        }
        if (this.mRoom != null && !this.mRoom.isEncrypted()) {
            this.mRoom.enableEncryptionWithAlgorithm(MXCryptoAlgorithms.MXCRYPTO_ALGORITHM_MEGOLM, new ApiCallback<Void>() {
                private void onDone() {
                    Log.m209d(VectorRoomActivity.TAG, "room Encrypted");
                }

                public void onSuccess(Void voidR) {
                    onDone();
                }

                public void onNetworkError(Exception exc) {
                    onDone();
                }

                public void onMatrixError(MatrixError matrixError) {
                    onDone();
                }

                public void onUnexpectedError(Exception exc) {
                    onDone();
                }
            });
        }
        Log.m209d(LOG_TAG, "End of create");
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(FIRST_VISIBLE_ROW, this.mVectorMessageListFragment.mMessageListView.getFirstVisiblePosition());
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mScrollToIndex = bundle.getInt(FIRST_VISIBLE_ROW, -1);
    }

    public void onDestroy() {
        if (this.mVectorMessageListFragment != null) {
            this.mVectorMessageListFragment.onDestroy();
        }
        if (this.mVectorOngoingConferenceCallView != null) {
            this.mVectorOngoingConferenceCallView.setCallClickListener(null);
        }
        if (this.mActiveWidgetsBanner != null) {
            this.mActiveWidgetsBanner.setOnUpdateListener(null);
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mReadMarkerManager != null) {
            this.mReadMarkerManager.onPause();
        }
        cancelTypingNotification();
        if (this.mRoom != null) {
            this.mRoom.removeEventListener(this.mRoomEventListener);
        }
        Matrix.getInstance(this).removeNetworkEventListener(this.mNetworkEventListener);
        if (this.mSession.isAlive() && this.mSession.getDataHandler() != null) {
            this.mSession.getDataHandler().removeListener(this.mGlobalEventListener);
        }
        this.mVectorOngoingConferenceCallView.onActivityPause();
        this.mActiveWidgetsBanner.onActivityPause();
        ViewedRoomTracker.getInstance().setViewedRoomId(null);
        ViewedRoomTracker.getInstance().setMatrixId(null);
        this.mEditText.initAutoCompletion(this.mSession, (String) null);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.m209d(LOG_TAG, "++ Resume the activity");
        super.onResume();
        ViewedRoomTracker.getInstance().setMatrixId(this.mSession.getCredentials().userId);
        int i = 8;
        if (this.mRoom != null) {
            if (this.mRoom.isReady()) {
                if (this.mRoom.getMember(this.mMyUserId) == null) {
                    Log.m211e(LOG_TAG, "## onResume() : the user is not anymore a member of the room.");
                    finish();
                    return;
                } else if (!this.mSession.getDataHandler().doesRoomExist(this.mRoom.getRoomId())) {
                    Log.m211e(LOG_TAG, "## onResume() : the user is not anymore a member of the room.");
                    finish();
                    return;
                } else if (this.mRoom.isLeaving()) {
                    Log.m211e(LOG_TAG, "## onResume() : the user is leaving the room.");
                    finish();
                    return;
                }
            }
            ViewedRoomTracker.getInstance().setViewedRoomId(this.mRoom.getRoomId());
            this.mRoom.addEventListener(this.mRoomEventListener);
            this.mEditText.setHint((!this.mRoom.isEncrypted() || !this.mSession.isCryptoEnabled()) ? C1299R.string.room_message_placeholder_not_encrypted : C1299R.string.room_message_placeholder_encrypted);
            View view = this.mSyncInProgressView;
            if (VectorApp.isSessionSyncing(this.mSession)) {
                i = 0;
            }
            view.setVisibility(i);
        } else {
            this.mSyncInProgressView.setVisibility(8);
        }
        this.mSession.getDataHandler().addListener(this.mGlobalEventListener);
        Matrix.getInstance(this).addNetworkEventListener(this.mNetworkEventListener);
        if (this.mRoom != null) {
            EventStreamService.cancelNotificationsForRoomId(this.mSession.getCredentials().userId, this.mRoom.getRoomId());
        }
        if (!(this.mRoom == null || Matrix.getInstance(this).getDefaultLatestChatMessageCache() == null)) {
            String latestText = Matrix.getInstance(this).getDefaultLatestChatMessageCache().getLatestText(this, this.mRoom.getRoomId());
            if (!latestText.equals(this.mEditText.getText().toString())) {
                this.mIgnoreTextUpdate = true;
                this.mEditText.setText("");
                this.mEditText.append(latestText);
                this.mIgnoreTextUpdate = false;
            }
            this.mVectorMessageListFragment.setIsRoomEncrypted(this.mRoom.isEncrypted());
            this.mE2eImageView.setImageResource(this.mRoom.isEncrypted() && this.mSession.isCryptoEnabled() ? C1299R.C1300drawable.e2e_verified : C1299R.C1300drawable.e2e_unencrypted);
            this.mVectorMessageListFragment.setIsRoomEncrypted(this.mRoom.isEncrypted());
        }
        manageSendMoreButtons();
        updateActionBarTitleAndTopic();
        sendReadReceipt();
        refreshCallButtons(true);
        updateRoomHeaderMembersStatus();
        checkSendEventStatus();
        enableActionBarHeader(this.mIsHeaderViewDisplayed);
        this.mVectorMessageListFragment.refresh();
        if (this.mVectorMessageListFragment.mMessageListView != null) {
            this.mVectorMessageListFragment.mMessageListView.lockSelectionOnResize();
        }
        if (this.mScrollToIndex > 0) {
            this.mVectorMessageListFragment.scrollToIndexWhenLoaded(this.mScrollToIndex);
            this.mScrollToIndex = -1;
        }
        if (this.mCallId != null) {
            IMXCall activeCall = CallsManager.getSharedInstance().getActiveCall();
            if (activeCall == null || activeCall.getCallId().equals(this.mCallId)) {
                final Intent intent = new Intent(this, VectorCallViewActivity.class);
                intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, this.mSession.getCredentials().userId);
                intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, this.mCallId);
                enableActionBarHeader(false);
                runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomActivity.this.startActivity(intent);
                    }
                });
            }
            this.mCallId = null;
        }
        if (sRoomPreviewData == null && this.mEventId == null) {
            this.mVectorPendingCallView.checkPendingCall();
            this.mVectorOngoingConferenceCallView.onActivityResume();
            this.mActiveWidgetsBanner.onActivityResume();
        }
        this.mEditText.initAutoCompletion(this.mSession, this.mRoom != null ? this.mRoom.getRoomId() : null);
        this.mEditText.initAutoCompletionCommandLine(this.mSession, (String) null);
        if (this.mReadMarkerManager != null) {
            this.mReadMarkerManager.onResume();
        }
        Log.m209d(LOG_TAG, "-- Resume the activity");
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            return;
        }
        if (i != 12000) {
            switch (i) {
                case 0:
                case 1:
                    sendMediasIntent(intent);
                    return;
                case 2:
                    insertUserDisplayNameInTextEditor(intent.getStringExtra(VectorMemberDetailsActivity.RESULT_MENTION_ID));
                    return;
                case 3:
                    onActivityResultRoomAvatarUpdate(intent);
                    return;
                case 4:
                    onActivityResultRoomInvite(intent);
                    return;
                case 5:
                    this.mVectorMessageListFragment.scrollToBottom(0);
                    return;
                default:
                    return;
            }
        } else {
            sendSticker(intent);
        }
    }

    public void onMessageSendingSucceeded(Event event) {
        refreshNotificationsArea();
    }

    public void onMessageSendingFailed(Event event) {
        refreshNotificationsArea();
    }

    public void onMessageRedacted(Event event) {
        refreshNotificationsArea();
    }

    public void onUnknownDevices(Event event, MXCryptoError mXCryptoError) {
        refreshNotificationsArea();
        CommonActivityUtils.verifyUnknownDevices(this.mSession, CommonActivityUtils.getDevicesList((MXUsersDevicesMap) mXCryptoError.mExceptionData));
        this.mVectorMessageListFragment.resendUnsentMessages();
        refreshNotificationsArea();
    }

    public void onConsentNotGiven(Event event, MatrixError matrixError) {
        refreshNotificationsArea();
        getConsentNotGivenHelper().displayDialog(matrixError);
    }

    private void sendReadReceipt() {
        if (this.mRoom != null && sRoomPreviewData == null) {
            final Event event = this.mLatestDisplayedEvent;
            this.mRoom.sendReadReceipt(event, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    try {
                        if (!VectorRoomActivity.this.isFinishing() && event != null && VectorRoomActivity.this.mVectorMessageListFragment.getMessageAdapter() != null) {
                            VectorRoomActivity.this.mVectorMessageListFragment.getMessageAdapter().updateReadMarker(VectorRoomActivity.this.mRoom.getReadMarkerEventId(), event.eventId);
                        }
                    } catch (Exception e) {
                        String access$300 = VectorRoomActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## sendReadReceipt() : failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$300, sb.toString());
                    }
                }

                public void onNetworkError(Exception exc) {
                    String access$300 = VectorRoomActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## sendReadReceipt() : failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$300 = VectorRoomActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## sendReadReceipt() : failed ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$300, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$300 = VectorRoomActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## sendReadReceipt() : failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                }
            });
            refreshNotificationsArea();
        }
    }

    public void onScroll(int i, int i2, int i3) {
        Event event = this.mVectorMessageListFragment.getEvent((i + i2) - 1);
        Event event2 = this.mVectorMessageListFragment.getEvent(i);
        if (event != null && (this.mLatestDisplayedEvent == null || !TextUtils.equals(event.eventId, this.mLatestDisplayedEvent.eventId))) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onScroll firstVisibleItem ");
            sb.append(i);
            sb.append(" visibleItemCount ");
            sb.append(i2);
            sb.append(" totalItemCount ");
            sb.append(i3);
            Log.m209d(str, sb.toString());
            this.mLatestDisplayedEvent = event;
            if (!VectorApp.isAppInBackground()) {
                sendReadReceipt();
            } else {
                Log.m209d(LOG_TAG, "## onScroll : the app is in background");
            }
        }
        if (this.mReadMarkerManager != null) {
            this.mReadMarkerManager.onScroll(i, i2, i3, event2, event);
        }
    }

    public void onScrollStateChanged(int i) {
        if (this.mReadMarkerManager != null) {
            this.mReadMarkerManager.onScrollStateChanged(i);
        }
    }

    public void onLatestEventDisplay(boolean z) {
        if (this.mIsScrolledToTheBottom == null || z != this.mIsScrolledToTheBottom.booleanValue()) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onLatestEventDisplay : isDisplayed ");
            sb.append(z);
            Log.m209d(str, sb.toString());
            if (z && this.mRoom != null) {
                this.mLatestDisplayedEvent = this.mRoom.getDataHandler().getStore().getLatestEvent(this.mRoom.getRoomId());
                this.mRoom.sendReadReceipt();
            }
            this.mIsScrolledToTheBottom = Boolean.valueOf(z);
            refreshNotificationsArea();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!CommonActivityUtils.shouldRestartApp(this) && this.mSession != null && this.mRoom != null && TextUtils.isEmpty(this.mEventId) && sRoomPreviewData == null) {
            this.mResendUnsentMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_resend_unsent);
            this.mResendDeleteMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_delete_unsent);
            this.mSearchInRoomMenuItem = menu.findItem(C1299R.C1301id.ic_action_search_in_room);
            this.mUseMatrixAppsMenuItem = menu.findItem(C1299R.C1301id.ic_action_matrix_apps);
            RoomMember member = this.mRoom.getMember(this.mSession.getMyUserId());
            if (member != null && member.kickedOrBanned()) {
                menu.findItem(C1299R.C1301id.ic_action_room_leave).setVisible(false);
            }
            refreshNotificationsArea();
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (CommonActivityUtils.shouldRestartApp(this) || this.mSession == null) {
            return false;
        }
        if (TextUtils.isEmpty(this.mEventId) && sRoomPreviewData == null) {
            getMenuInflater().inflate(C1299R.C1302menu.vector_room, menu);
            CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
            this.mResendUnsentMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_resend_unsent);
            this.mResendDeleteMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_delete_unsent);
            this.mSearchInRoomMenuItem = menu.findItem(C1299R.C1301id.ic_action_search_in_room);
            this.mUseMatrixAppsMenuItem = menu.findItem(C1299R.C1301id.ic_action_matrix_apps);
            RoomMember member = this.mRoom.getMember(this.mSession.getMyUserId());
            if (member != null && member.kickedOrBanned()) {
                menu.findItem(C1299R.C1301id.ic_action_room_leave).setVisible(false);
            }
            refreshNotificationsArea();
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        }
        if (itemId == C1299R.C1301id.ic_action_matrix_apps) {
            openIntegrationManagerActivity(null);
        } else if (itemId == C1299R.C1301id.ic_action_search_in_room) {
            try {
                enableActionBarHeader(false);
                Intent intent = new Intent(this, VectorUnifiedSearchActivity.class);
                intent.putExtra(VectorUnifiedSearchActivity.EXTRA_ROOM_ID, this.mRoom.getRoomId());
                startActivity(intent);
            } catch (Exception unused) {
                Log.m213i(LOG_TAG, "## onOptionsItemSelected(): ");
            }
        } else if (itemId == C1299R.C1301id.ic_action_room_settings) {
            launchRoomDetails(0);
        } else if (itemId == C1299R.C1301id.ic_action_room_resend_unsent) {
            this.mVectorMessageListFragment.resendUnsentMessages();
            refreshNotificationsArea();
        } else if (itemId == C1299R.C1301id.ic_action_room_delete_unsent) {
            this.mVectorMessageListFragment.deleteUnsentEvents();
            refreshNotificationsArea();
        } else if (itemId == C1299R.C1301id.ic_action_room_leave && this.mRoom != null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Leave the room ");
            sb.append(this.mRoom.getRoomId());
            Log.m209d(str, sb.toString());
            new Builder(this).setTitle(C1299R.string.room_participants_leave_prompt_title).setMessage(C1299R.string.room_participants_leave_prompt_msg).setPositiveButton(C1299R.string.leave, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    VectorRoomActivity.this.showWaitingView();
                    VectorRoomActivity.this.mRoom.leave(new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            String access$300 = VectorRoomActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("The room ");
                            sb.append(VectorRoomActivity.this.mRoom.getRoomId());
                            sb.append(" is left");
                            Log.m209d(access$300, sb.toString());
                            VectorRoomActivity.this.finish();
                        }

                        private void onError(String str) {
                            VectorRoomActivity.this.hideWaitingView();
                            String access$300 = VectorRoomActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Cannot leave the room ");
                            sb.append(VectorRoomActivity.this.mRoom.getRoomId());
                            sb.append(" : ");
                            sb.append(str);
                            Log.m211e(access$300, sb.toString());
                        }

                        public void onNetworkError(Exception exc) {
                            onError(exc.getLocalizedMessage());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                                VectorRoomActivity.this.hideWaitingView();
                                VectorRoomActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                                return;
                            }
                            onError(matrixError.getLocalizedMessage());
                        }

                        public void onUnexpectedError(Exception exc) {
                            onError(exc.getLocalizedMessage());
                        }
                    });
                }
            }).setNegativeButton(C1299R.string.cancel, null).show();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* access modifiers changed from: private */
    public void openIntegrationManagerActivity(@Nullable String str) {
        if (this.mRoom != null) {
            startActivity(IntegrationManagerActivity.Companion.getIntent(this, this.mMyUserId, this.mRoom.getRoomId(), null, str));
        }
    }

    /* access modifiers changed from: private */
    public boolean isUserAllowedToStartConfCall() {
        boolean z = true;
        if (this.mRoom != null && this.mRoom.isOngoingConferenceCall()) {
            Log.m209d(LOG_TAG, "## isUserAllowedToStartConfCall(): conference in progress");
        } else if (this.mRoom != null && this.mRoom.getActiveMembers().size() > 2) {
            PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
            if (powerLevels == null || powerLevels.getUserPowerLevel(this.mSession.getMyUserId()) < powerLevels.invite) {
                z = false;
            }
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## isUserAllowedToStartConfCall(): isAllowed=");
        sb.append(z);
        Log.m209d(str, sb.toString());
        return z;
    }

    /* access modifiers changed from: private */
    public void displayConfCallNotAllowed() {
        Builder builder = new Builder(this);
        Resources resources = getResources();
        if (resources != null) {
            builder.setTitle(resources.getString(C1299R.string.missing_permissions_title_to_start_conf_call));
            builder.setMessage(resources.getString(C1299R.string.missing_permissions_to_start_conf_call));
            builder.setIcon(17301543);
            builder.setPositiveButton(resources.getString(C1299R.string.f115ok), null);
            builder.show();
            return;
        }
        Log.m211e(LOG_TAG, "## displayConfCallNotAllowed(): impossible to create dialog");
    }

    /* access modifiers changed from: private */
    public void displayVideoCallIpDialog() {
        enableActionBarHeader(false);
        IconAndTextDialogFragment newInstance = IconAndTextDialogFragment.newInstance(new Integer[]{Integer.valueOf(C1299R.C1300drawable.voice_call_green), Integer.valueOf(C1299R.C1300drawable.video_call_green)}, new Integer[]{Integer.valueOf(C1299R.string.action_voice_call), Integer.valueOf(C1299R.string.action_video_call)}, Integer.valueOf(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.riot_primary_background_color)), Integer.valueOf(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.riot_primary_text_color)));
        newInstance.setOnClickListener(new OnItemClickListener() {
            public void onItemClick(IconAndTextDialogFragment iconAndTextDialogFragment, int i) {
                final int i2;
                final boolean z = true;
                if (1 == i) {
                    i2 = 5;
                } else {
                    z = false;
                    i2 = 4;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(VectorRoomActivity.this);
                builder.setTitle((int) C1299R.string.dialog_title_confirmation);
                if (z) {
                    builder.setMessage((CharSequence) VectorRoomActivity.this.getString(C1299R.string.start_video_call_prompt_msg));
                } else {
                    builder.setMessage((CharSequence) VectorRoomActivity.this.getString(C1299R.string.start_voice_call_prompt_msg));
                }
                builder.setPositiveButton((int) C1299R.string.f115ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (CommonActivityUtils.checkPermissions(i2, (Activity) VectorRoomActivity.this)) {
                            VectorRoomActivity.this.startIpCall(PreferencesManager.useJitsiConfCall(VectorRoomActivity.this), z);
                        }
                    }
                });
                builder.setNegativeButton((int) C1299R.string.cancel, (DialogInterface.OnClickListener) null).show();
            }
        });
        newInstance.show(getSupportFragmentManager(), TAG_FRAGMENT_CALL_OPTIONS);
    }

    /* access modifiers changed from: private */
    public void launchJitsiActivity(Widget widget, boolean z) {
        Intent intent = new Intent(this, JitsiCallActivity.class);
        intent.putExtra("EXTRA_WIDGET_ID", widget);
        intent.putExtra(JitsiCallActivity.EXTRA_ENABLE_VIDEO, z);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void startJitsiCall(boolean z) {
        enableActionBarHeader(false);
        showWaitingView();
        WidgetsManager.getSharedInstance().createJitsiWidget(this.mSession, this.mRoom, z, new ApiCallback<Widget>() {
            public void onSuccess(Widget widget) {
                VectorRoomActivity.this.hideWaitingView();
                Intent intent = new Intent(VectorRoomActivity.this, JitsiCallActivity.class);
                intent.putExtra("EXTRA_WIDGET_ID", widget);
                VectorRoomActivity.this.startActivity(intent);
            }

            private void onError(String str) {
                VectorRoomActivity.this.hideWaitingView();
                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
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
        });
    }

    /* access modifiers changed from: private */
    public void startIpCall(final boolean z, final boolean z2) {
        if (this.mRoom != null) {
            if (this.mRoom.getActiveMembers().size() <= 2 || !z) {
                enableActionBarHeader(false);
                showWaitingView();
                this.mSession.mCallsManager.createCallInRoom(this.mRoom.getRoomId(), z2, new ApiCallback<IMXCall>() {
                    public void onSuccess(final IMXCall iMXCall) {
                        Log.m209d(VectorRoomActivity.LOG_TAG, "## startIpCall(): onSuccess");
                        VectorRoomActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRoomActivity.this.hideWaitingView();
                                final Intent intent = new Intent(VectorRoomActivity.this, VectorCallViewActivity.class);
                                intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, VectorRoomActivity.this.mSession.getCredentials().userId);
                                intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, iMXCall.getCallId());
                                VectorRoomActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        VectorRoomActivity.this.startActivity(intent);
                                    }
                                });
                            }
                        });
                    }

                    private void onError(final String str) {
                        VectorRoomActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRoomActivity.this.hideWaitingView();
                                VectorRoomActivity vectorRoomActivity = VectorRoomActivity.this;
                                StringBuilder sb = new StringBuilder();
                                sb.append(vectorRoomActivity.getString(C1299R.string.cannot_start_call));
                                sb.append(" (");
                                sb.append(str);
                                sb.append(")");
                                CommonActivityUtils.displayToastOnUiThread(vectorRoomActivity, sb.toString());
                            }
                        });
                    }

                    public void onNetworkError(Exception exc) {
                        String access$300 = VectorRoomActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startIpCall(): onNetworkError Msg=");
                        sb.append(exc.getMessage());
                        Log.m211e(access$300, sb.toString());
                        onError(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        String access$300 = VectorRoomActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startIpCall(): onMatrixError Msg=");
                        sb.append(matrixError.getLocalizedMessage());
                        Log.m211e(access$300, sb.toString());
                        if (matrixError instanceof MXCryptoError) {
                            MXCryptoError mXCryptoError = (MXCryptoError) matrixError;
                            if (MXCryptoError.UNKNOWN_DEVICES_CODE.equals(mXCryptoError.errcode)) {
                                VectorRoomActivity.this.hideWaitingView();
                                CommonActivityUtils.verifyUnknownDevices(VectorRoomActivity.this.mSession, CommonActivityUtils.getDevicesList((MXUsersDevicesMap) mXCryptoError.mExceptionData));
                                VectorRoomActivity.this.startIpCall(z, z2);
                                return;
                            }
                        }
                        onError(matrixError.getLocalizedMessage());
                    }

                    public void onUnexpectedError(Exception exc) {
                        String access$300 = VectorRoomActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startIpCall(): onUnexpectedError Msg=");
                        sb.append(exc.getLocalizedMessage());
                        Log.m211e(access$300, sb.toString());
                        onError(exc.getLocalizedMessage());
                    }
                });
                return;
            }
            startJitsiCall(z2);
        }
    }

    /* access modifiers changed from: private */
    public void setAutoCompletionMode(AutoCompletionMode autoCompletionMode) {
        this.mAutoCompletionMode = autoCompletionMode;
    }

    /* access modifiers changed from: private */
    public void setAutoCompletionParam(AutoCompletionMode autoCompletionMode) {
        if (autoCompletionMode != null) {
            switch (autoCompletionMode) {
                case USER_MODE:
                    this.mEditText.setAdapter(this.mEditText.mAdapterUser);
                    this.mEditText.setThreshold(3);
                    return;
                case COMMAND_MODE:
                    this.mEditText.setAdapter(this.mEditText.mAdapterCommand);
                    this.mEditText.setThreshold(1);
                    return;
                default:
                    return;
            }
        }
    }

    public void cancelSelectionMode() {
        this.mVectorMessageListFragment.cancelSelectionMode();
    }

    /* access modifiers changed from: private */
    public void sendTextMessage() {
        if (!this.mIsMarkDowning) {
            this.mSendImageView.setEnabled(false);
            this.mIsMarkDowning = true;
            VectorApp.markdownToHtml(this.mEditText.getText().toString().trim(), new IVectorMarkdownParserListener() {
                public void onMarkdownParsed(final String str, final String str2) {
                    VectorRoomActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            VectorRoomActivity.this.mSendImageView.setEnabled(true);
                            VectorRoomActivity.this.mIsMarkDowning = false;
                            VectorRoomActivity.this.enableActionBarHeader(false);
                            VectorRoomActivity.this.sendMessage(str, TextUtils.equals(str, str2) ? null : str2, Message.FORMAT_MATRIX_HTML);
                            VectorRoomActivity.this.mEditText.setText("");
                        }
                    });
                }
            });
        }
    }

    public void sendMessage(String str, String str2, String str3) {
        if (!TextUtils.isEmpty(str)) {
            if (!SlashCommandsParser.manageSplashCommand(this, this.mSession, this.mRoom, str, str2, str3)) {
                cancelSelectionMode();
                this.mVectorMessageListFragment.sendTextMessage(str, str2, str3);
            }
        }
    }

    public void sendEmote(String str, String str2, String str3) {
        if (this.mVectorMessageListFragment != null) {
            this.mVectorMessageListFragment.sendEmote(str, str2, str3);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void sendMediasIntent(Intent intent) {
        if (intent != null || this.mLatestTakePictureCameraUri != null) {
            ArrayList arrayList = new ArrayList();
            if (intent != null) {
                arrayList = new ArrayList(RoomMediaMessage.listRoomMediaMessages(intent, RoomMediaMessage.class.getClassLoader()));
            }
            if (this.mLatestTakePictureCameraUri != null) {
                if (arrayList.size() == 0) {
                    arrayList.add(new RoomMediaMessage(Uri.parse(this.mLatestTakePictureCameraUri)));
                }
                this.mLatestTakePictureCameraUri = null;
            }
            if (arrayList.size() == 0 && intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey("android.intent.extra.TEXT")) {
                    VectorAutoCompleteTextView vectorAutoCompleteTextView = this.mEditText;
                    StringBuilder sb = new StringBuilder();
                    sb.append(this.mEditText.getText());
                    sb.append(extras.getString("android.intent.extra.TEXT"));
                    vectorAutoCompleteTextView.setText(sb.toString());
                    this.mEditText.post(new Runnable() {
                        public void run() {
                            VectorRoomActivity.this.mEditText.setSelection(VectorRoomActivity.this.mEditText.getText().length());
                        }
                    });
                }
            }
            if (arrayList.size() != 0) {
                this.mVectorRoomMediasSender.sendMedias(arrayList);
            }
        }
    }

    private void sendSticker(Intent intent) {
        if (this.mRoom != null) {
            this.mVectorMessageListFragment.sendStickerMessage(new Event(Event.EVENT_TYPE_STICKER, new JsonParser().parse(StickerPickerActivity.Companion.getResultContent(intent)).getAsJsonObject(), this.mSession.getCredentials().userId, this.mRoom.getRoomId()));
        }
    }

    /* access modifiers changed from: private */
    public void handleTypingNotification(boolean z) {
        if (PreferencesManager.dontSendTypingNotifs(this)) {
            Log.m209d(LOG_TAG, "##handleTypingNotification() : the typing notifs are disabled");
        } else if (this.mRoom != null) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##handleTypingNotification() : isTyping ");
            sb.append(z);
            Log.m209d(str, sb.toString());
            int i = -1;
            if (!z) {
                if (this.mTypingTimerTask != null) {
                    this.mTypingTimerTask.cancel();
                    this.mTypingTimerTask = null;
                }
                if (this.mTypingTimer != null) {
                    this.mTypingTimer.cancel();
                    this.mTypingTimer = null;
                }
                this.mLastTypingDate = 0;
            } else if (this.mTypingTimer != null) {
                System.currentTimeMillis();
                this.mLastTypingDate = System.currentTimeMillis();
                return;
            } else {
                int i2 = TYPING_TIMEOUT_MS;
                if (0 != this.mLastTypingDate) {
                    long currentTimeMillis = System.currentTimeMillis() - this.mLastTypingDate;
                    long j = (long) TYPING_TIMEOUT_MS;
                    i2 = currentTimeMillis < j ? (int) (j - currentTimeMillis) : 0;
                } else {
                    this.mLastTypingDate = System.currentTimeMillis();
                }
                if (i2 > 0) {
                    try {
                        this.mTypingTimerTask = new TimerTask() {
                            public void run() {
                                synchronized (VectorRoomActivity.LOG_TAG) {
                                    if (VectorRoomActivity.this.mTypingTimerTask != null) {
                                        VectorRoomActivity.this.mTypingTimerTask.cancel();
                                        VectorRoomActivity.this.mTypingTimerTask = null;
                                    }
                                    if (VectorRoomActivity.this.mTypingTimer != null) {
                                        VectorRoomActivity.this.mTypingTimer.cancel();
                                        VectorRoomActivity.this.mTypingTimer = null;
                                    }
                                    Log.m209d(VectorRoomActivity.LOG_TAG, "##handleTypingNotification() : send end of typing");
                                    VectorRoomActivity.this.handleTypingNotification(0 != VectorRoomActivity.this.mLastTypingDate);
                                }
                            }
                        };
                        try {
                            synchronized (LOG_TAG) {
                                this.mTypingTimer = new Timer();
                                this.mTypingTimer.schedule(this.mTypingTimerTask, 10000);
                            }
                        } catch (Throwable th) {
                            String str2 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("fails to launch typing timer ");
                            sb2.append(th.getMessage());
                            Log.m211e(str2, sb2.toString());
                            this.mTypingTimer = null;
                            this.mTypingTimerTask = null;
                        }
                        i = 20000;
                    } catch (Throwable th2) {
                        String str3 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## mTypingTimerTask creation failed ");
                        sb3.append(th2.getMessage());
                        Log.m211e(str3, sb3.toString());
                        return;
                    }
                } else {
                    z = false;
                }
            }
            this.mRoom.sendTypingNotification(z, i, new SimpleApiCallback<Void>(this) {
                public void onSuccess(Void voidR) {
                    VectorRoomActivity.this.mLastTypingDate = 0;
                }

                public void onNetworkError(Exception exc) {
                    if (VectorRoomActivity.this.mTypingTimerTask != null) {
                        VectorRoomActivity.this.mTypingTimerTask.cancel();
                        VectorRoomActivity.this.mTypingTimerTask = null;
                    }
                    if (VectorRoomActivity.this.mTypingTimer != null) {
                        VectorRoomActivity.this.mTypingTimer.cancel();
                        VectorRoomActivity.this.mTypingTimer = null;
                    }
                }
            });
        }
    }

    private void cancelTypingNotification() {
        if (!(this.mRoom == null || 0 == this.mLastTypingDate)) {
            if (this.mTypingTimerTask != null) {
                this.mTypingTimerTask.cancel();
                this.mTypingTimerTask = null;
            }
            if (this.mTypingTimer != null) {
                this.mTypingTimer.cancel();
                this.mTypingTimer = null;
            }
            this.mLastTypingDate = 0;
            this.mRoom.sendTypingNotification(false, -1, new SimpleApiCallback<Void>(this) {
                public void onSuccess(Void voidR) {
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void launchRoomDetails(int i) {
        if (this.mSession != null && this.mRoom != null && this.mRoom.getMember(this.mSession.getMyUserId()) != null) {
            enableActionBarHeader(false);
            Intent intent = new Intent(this, VectorRoomDetailsActivity.class);
            intent.putExtra(VectorRoomDetailsActivity.EXTRA_ROOM_ID, this.mRoom.getRoomId());
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            intent.putExtra(VectorRoomDetailsActivity.EXTRA_SELECTED_TAB_ID, i);
            startActivityForResult(intent, 2);
        }
    }

    /* access modifiers changed from: private */
    public void launchInvitePeople() {
        if (this.mSession != null && this.mRoom != null) {
            Intent intent = new Intent(this, VectorRoomInviteMembersActivity.class);
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
            intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_ROOM_ID, this.mRoom.getRoomId());
            intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_ADD_CONFIRMATION_DIALOG, true);
            startActivityForResult(intent, 4);
        }
    }

    private void launchAudioRecorderIntent() {
        enableActionBarHeader(false);
        ExternalApplicationsUtilKt.openSoundRecorder(this, 6);
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void launchFileSelectionIntent() {
        enableActionBarHeader(false);
        ExternalApplicationsUtilKt.openFileSelection(this, null, true, 0);
    }

    /* access modifiers changed from: private */
    public void startStickerPickerActivity() {
        String str;
        String str2;
        Iterator it = this.mSession.getUserWidgets().values().iterator();
        while (true) {
            if (!it.hasNext()) {
                str = null;
                str2 = null;
                break;
            }
            Object next = it.next();
            if (next instanceof Map) {
                Map map = (Map) next;
                Object obj = map.get("content");
                if (obj != null && (obj instanceof Map)) {
                    Map map2 = (Map) obj;
                    Object obj2 = map2.get("type");
                    if (obj2 != null && (obj2 instanceof String) && obj2.equals(StickerPickerActivity.WIDGET_NAME)) {
                        str = (String) map2.get(ImagesContract.URL);
                        str2 = (String) map.get("id");
                        break;
                    }
                }
            }
        }
        if (TextUtils.isEmpty(str)) {
            Builder builder = new Builder(this);
            builder.setView(LayoutInflater.from(builder.getContext()).inflate(C1299R.layout.no_sticker_pack_dialog, null)).setPositiveButton(C1299R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    VectorRoomActivity.this.openIntegrationManagerActivity("type_m.stickerpicker");
                }
            }).setNegativeButton(C1299R.string.f114no, null).show();
        } else if (this.mRoom != null) {
            startActivityForResult(StickerPickerActivity.Companion.getIntent(this, this.mMyUserId, this.mRoom.getRoomId(), str, str2), RequestCodesKt.STICKER_PICKER_ACTIVITY_REQUEST_CODE);
        }
    }

    /* access modifiers changed from: private */
    public void launchNativeVideoRecorder() {
        enableActionBarHeader(false);
        CameraApplicationsUtil.openVideoRecorder(this, 1);
    }

    /* access modifiers changed from: private */
    public void launchNativeCamera() {
        enableActionBarHeader(false);
        this.mLatestTakePictureCameraUri = CameraApplicationsUtil.openCamera(this, CAMERA_VALUE_TITLE, 1);
    }

    private void launchCamera() {
        enableActionBarHeader(false);
        Intent intent = new Intent(this, VectorMediasPickerActivity.class);
        intent.putExtra(VectorMediasPickerActivity.EXTRA_VIDEO_RECORDING_MODE, true);
        startActivityForResult(intent, 1);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
            return;
        }
        int i2 = 0;
        if (i == 1) {
            boolean z = false;
            while (i2 < strArr.length) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onRequestPermissionsResult(): ");
                sb2.append(strArr[i2]);
                sb2.append("=");
                sb2.append(iArr[i2]);
                Log.m209d(str2, sb2.toString());
                if ("android.permission.CAMERA".equals(strArr[i2])) {
                    if (iArr[i2] == 0) {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): CAMERA permission granted");
                        z = true;
                    } else {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): CAMERA permission not granted");
                    }
                }
                i2++;
            }
            if (z) {
                Intent intent = new Intent(this, VectorMediasPickerActivity.class);
                intent.putExtra(VectorMediasPickerActivity.EXTRA_AVATAR_MODE, true);
                startActivityForResult(intent, 3);
                return;
            }
            launchRoomDetails(2);
        } else if (i == 3) {
            boolean z2 = false;
            while (i2 < strArr.length) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## onRequestPermissionsResult(): ");
                sb3.append(strArr[i2]);
                sb3.append("=");
                sb3.append(iArr[i2]);
                Log.m209d(str3, sb3.toString());
                if ("android.permission.CAMERA".equals(strArr[i2])) {
                    if (iArr[i2] == 0) {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): CAMERA permission granted");
                        z2 = true;
                    } else {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): CAMERA permission not granted");
                    }
                }
                if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(strArr[i2])) {
                    if (iArr[i2] == 0) {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): WRITE_EXTERNAL_STORAGE permission granted");
                    } else {
                        Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): WRITE_EXTERNAL_STORAGE permission not granted");
                    }
                }
                i2++;
            }
            if (!z2) {
                CommonActivityUtils.displayToast(this, getString(C1299R.string.missing_permissions_warning));
            } else if (C1299R.string.option_take_photo_video == this.mCameraPermissionAction) {
                launchNativeCamera();
            } else if (C1299R.string.option_take_photo == this.mCameraPermissionAction) {
                launchNativeCamera();
            } else if (C1299R.string.option_take_video == this.mCameraPermissionAction) {
                launchNativeVideoRecorder();
            }
        } else if (i == 4) {
            if (CommonActivityUtils.onPermissionResultAudioIpCall(this, strArr, iArr)) {
                startIpCall(PreferencesManager.useJitsiConfCall(this), false);
            }
        } else if (i != 5) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## onRequestPermissionsResult(): Unknown requestCode =");
            sb4.append(i);
            Log.m217w(str4, sb4.toString());
        } else if (CommonActivityUtils.onPermissionResultVideoIpCall(this, strArr, iArr)) {
            startIpCall(PreferencesManager.useJitsiConfCall(this), true);
        }
    }

    private void manageKeyboardOptionsToSendMessage() {
        if (PreferencesManager.useEnterKeyToSendMessage(this)) {
            this.mEditText.setImeOptions(4);
            this.mEditText.setOnEditorActionListener(new OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 4) {
                        return false;
                    }
                    VectorRoomActivity.this.sendTextMessage();
                    return true;
                }
            });
            return;
        }
        this.mEditText.setImeOptions(0);
        this.mEditText.setInputType(131072);
        this.mEditText.setOnKeyListener(null);
        this.mEditText.setSingleLine(false);
        if (this.mEditText.getText().length() > 0) {
            this.mEditText.setText(StringUtils.f158LF);
        }
    }

    /* access modifiers changed from: private */
    public void manageSendMoreButtons() {
        this.mSendImageView.setImageResource(this.mEditText.getText().length() > 0 ? C1299R.C1300drawable.ic_material_send_green : C1299R.C1300drawable.ic_material_file);
    }

    private void refreshSelfAvatar() {
        if (this.mAvatarImageView != null) {
            VectorUtils.loadUserAvatar(this, this.mSession, this.mAvatarImageView, this.mSession.getMyUser());
        }
    }

    public static String sanitizeDisplayname(String str) {
        return (TextUtils.isEmpty(str) || !str.endsWith(" (IRC)")) ? str : str.substring(0, str.length() - " (IRC)".length());
    }

    public void insertTextInTextEditor(String str) {
        if (TextUtils.isEmpty(this.mEditText.getText())) {
            this.mEditText.append(str);
            return;
        }
        Editable text = this.mEditText.getText();
        int selectionStart = this.mEditText.getSelectionStart();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(StringUtils.SPACE);
        text.insert(selectionStart, sb.toString());
    }

    public void insertUserDisplayNameInTextEditor(String str) {
        if (str != null) {
            boolean z = true;
            if (TextUtils.equals(this.mSession.getMyUser().displayname, str)) {
                if (TextUtils.isEmpty(this.mEditText.getText())) {
                    this.mEditText.setText(String.format(VectorApp.getApplicationLocale(), "%s ", new Object[]{SlashCommand.EMOTE.getCommand()}));
                    this.mEditText.setSelection(this.mEditText.getText().length());
                } else {
                    z = false;
                }
            } else if (TextUtils.isEmpty(this.mEditText.getText())) {
                VectorAutoCompleteTextView vectorAutoCompleteTextView = this.mEditText;
                StringBuilder sb = new StringBuilder();
                sb.append(sanitizeDisplayname(str));
                sb.append(": ");
                vectorAutoCompleteTextView.append(sb.toString());
            } else {
                Editable text = this.mEditText.getText();
                int selectionStart = this.mEditText.getSelectionStart();
                StringBuilder sb2 = new StringBuilder();
                sb2.append(sanitizeDisplayname(str));
                sb2.append(StringUtils.SPACE);
                text.insert(selectionStart, sb2.toString());
            }
            if (z && PreferencesManager.vibrateWhenMentioning(this)) {
                Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(100);
                }
            }
        }
    }

    public void insertQuoteInTextEditor(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        if (TextUtils.isEmpty(this.mEditText.getText())) {
            this.mEditText.setText("");
            this.mEditText.append(str);
            return;
        }
        Editable text = this.mEditText.getText();
        int selectionStart = this.mEditText.getSelectionStart();
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.f158LF);
        sb.append(str);
        text.insert(selectionStart, sb.toString());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x01e4  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01f8  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x020b  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0214  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x022d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void refreshNotificationsArea() {
        /*
            r15 = this;
            com.opengarden.firechat.matrixsdk.MXSession r0 = r15.mSession
            com.opengarden.firechat.matrixsdk.MXDataHandler r0 = r0.getDataHandler()
            if (r0 == 0) goto L_0x0241
            com.opengarden.firechat.matrixsdk.data.Room r0 = r15.mRoom
            if (r0 == 0) goto L_0x0241
            com.opengarden.firechat.matrixsdk.data.RoomPreviewData r0 = sRoomPreviewData
            if (r0 == 0) goto L_0x0012
            goto L_0x0241
        L_0x0012:
            android.text.SpannableString r0 = new android.text.SpannableString
            java.lang.String r1 = ""
            r0.<init>(r1)
            android.widget.TextView r1 = r15.mNotificationTextView
            r2 = 0
            r1.setOnClickListener(r2)
            android.widget.ImageView r1 = r15.mNotificationIconImageView
            r1.setOnClickListener(r2)
            com.opengarden.firechat.Matrix r1 = com.opengarden.firechat.Matrix.getInstance(r15)
            boolean r1 = r1.isConnected()
            r3 = 2131230856(0x7f080088, float:1.8077777E38)
            r4 = 2131099798(0x7f060096, float:1.781196E38)
            r5 = -1
            r6 = 1
            r7 = 0
            if (r1 != 0) goto L_0x0052
            int r0 = android.support.p000v4.content.ContextCompat.getColor(r15, r4)
            android.text.SpannableString r1 = new android.text.SpannableString
            android.content.res.Resources r8 = r15.getResources()
            r9 = 2131690017(0x7f0f0221, float:1.9009066E38)
            java.lang.String r8 = r8.getString(r9)
            r1.<init>(r8)
            r9 = r1
            r8 = 1
            r1 = r0
            r0 = 2131230856(0x7f080088, float:1.8077777E38)
            goto L_0x0056
        L_0x0052:
            r9 = r0
            r0 = -1
            r1 = -1
            r8 = 0
        L_0x0056:
            boolean r10 = r15.mIsUnreadPreviewMode
            r11 = 2131230978(0x7f080102, float:1.8078024E38)
            r12 = 2130968970(0x7f04018a, float:1.7546609E38)
            if (r10 == 0) goto L_0x0077
            com.opengarden.firechat.util.ThemeUtils r0 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            int r1 = r0.getColor(r15, r12)
            android.widget.ImageView r0 = r15.mNotificationIconImageView
            com.opengarden.firechat.activity.VectorRoomActivity$33 r2 = new com.opengarden.firechat.activity.VectorRoomActivity$33
            r2.<init>()
            r0.setOnClickListener(r2)
            r0 = 0
            r3 = 2131230978(0x7f080102, float:1.8078024E38)
        L_0x0074:
            r8 = 1
            goto L_0x01da
        L_0x0077:
            com.opengarden.firechat.matrixsdk.MXSession r10 = r15.mSession
            com.opengarden.firechat.matrixsdk.MXDataHandler r10 = r10.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r10 = r10.getStore()
            com.opengarden.firechat.matrixsdk.data.Room r13 = r15.mRoom
            java.lang.String r13 = r13.getRoomId()
            java.util.List r10 = r10.getUndeliverableEvents(r13)
            com.opengarden.firechat.matrixsdk.MXSession r13 = r15.mSession
            com.opengarden.firechat.matrixsdk.MXDataHandler r13 = r13.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r13 = r13.getStore()
            com.opengarden.firechat.matrixsdk.data.Room r14 = r15.mRoom
            java.lang.String r14 = r14.getRoomId()
            java.util.List r13 = r13.getUnknownDeviceEvents(r14)
            if (r10 == 0) goto L_0x00a9
            int r10 = r10.size()
            if (r10 <= 0) goto L_0x00a9
            r10 = 1
            goto L_0x00aa
        L_0x00a9:
            r10 = 0
        L_0x00aa:
            if (r13 == 0) goto L_0x00b4
            int r13 = r13.size()
            if (r13 <= 0) goto L_0x00b4
            r13 = 1
            goto L_0x00b5
        L_0x00b4:
            r13 = 0
        L_0x00b5:
            if (r10 != 0) goto L_0x0170
            if (r13 == 0) goto L_0x00bb
            goto L_0x0170
        L_0x00bb:
            java.lang.Boolean r2 = r15.mIsScrolledToTheBottom
            if (r2 == 0) goto L_0x0152
            java.lang.Boolean r2 = r15.mIsScrolledToTheBottom
            boolean r2 = r2.booleanValue()
            if (r2 != 0) goto L_0x0152
            com.opengarden.firechat.matrixsdk.data.Room r0 = r15.mRoom
            com.opengarden.firechat.matrixsdk.MXDataHandler r0 = r0.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r0 = r0.getStore()
            com.opengarden.firechat.matrixsdk.data.Room r1 = r15.mRoom
            java.lang.String r1 = r1.getRoomId()
            com.opengarden.firechat.matrixsdk.data.RoomSummary r0 = r0.getSummary(r1)
            if (r0 == 0) goto L_0x00f6
            com.opengarden.firechat.matrixsdk.data.Room r1 = r15.mRoom
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r1.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r1 = r1.getStore()
            com.opengarden.firechat.matrixsdk.data.Room r2 = r15.mRoom
            java.lang.String r2 = r2.getRoomId()
            java.lang.String r0 = r0.getReadReceiptEventId()
            int r0 = r1.eventsCountAfter(r2, r0)
            goto L_0x00f7
        L_0x00f6:
            r0 = 0
        L_0x00f7:
            if (r0 <= 0) goto L_0x011e
            r1 = 2131230958(0x7f0800ee, float:1.8077983E38)
            int r2 = android.support.p000v4.content.ContextCompat.getColor(r15, r4)
            android.text.SpannableString r3 = new android.text.SpannableString
            android.content.res.Resources r4 = r15.getResources()
            r8 = 2131558410(0x7f0d000a, float:1.8742135E38)
            java.lang.Object[] r9 = new java.lang.Object[r6]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)
            r9[r7] = r10
            java.lang.String r0 = r4.getQuantityString(r8, r0, r9)
            r3.<init>(r0)
            r1 = r2
            r9 = r3
            r3 = 2131230958(0x7f0800ee, float:1.8077983E38)
            goto L_0x013d
        L_0x011e:
            com.opengarden.firechat.util.ThemeUtils r0 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            int r0 = r0.getColor(r15, r12)
            java.lang.String r1 = r15.mLatestTypingMessage
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0139
            android.text.SpannableString r1 = new android.text.SpannableString
            java.lang.String r2 = r15.mLatestTypingMessage
            r1.<init>(r2)
            r9 = r1
            r3 = 2131230978(0x7f080102, float:1.8078024E38)
            r1 = r0
            goto L_0x013d
        L_0x0139:
            r1 = r0
            r3 = 2131230978(0x7f080102, float:1.8078024E38)
        L_0x013d:
            android.widget.TextView r0 = r15.mNotificationTextView
            com.opengarden.firechat.activity.VectorRoomActivity$34 r2 = new com.opengarden.firechat.activity.VectorRoomActivity$34
            r2.<init>()
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r15.mNotificationIconImageView
            com.opengarden.firechat.activity.VectorRoomActivity$35 r2 = new com.opengarden.firechat.activity.VectorRoomActivity$35
            r2.<init>()
            r0.setOnClickListener(r2)
            goto L_0x016a
        L_0x0152:
            java.lang.String r2 = r15.mLatestTypingMessage
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x016d
            r3 = 2131231013(0x7f080125, float:1.8078095E38)
            android.text.SpannableString r9 = new android.text.SpannableString
            java.lang.String r0 = r15.mLatestTypingMessage
            r9.<init>(r0)
            com.opengarden.firechat.util.ThemeUtils r0 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            int r1 = r0.getColor(r15, r12)
        L_0x016a:
            r0 = 0
            goto L_0x0074
        L_0x016d:
            r3 = r0
            r0 = 0
            goto L_0x01da
        L_0x0170:
            android.content.res.Resources r0 = r15.getResources()
            r1 = 2131690054(0x7f0f0246, float:1.900914E38)
            java.lang.String r0 = r0.getString(r1)
            android.content.res.Resources r1 = r15.getResources()
            r8 = 2131690055(0x7f0f0247, float:1.9009143E38)
            java.lang.String r1 = r1.getString(r8)
            android.content.res.Resources r8 = r15.getResources()
            if (r13 == 0) goto L_0x0190
            r9 = 2131690150(0x7f0f02a6, float:1.9009335E38)
            goto L_0x0193
        L_0x0190:
            r9 = 2131690151(0x7f0f02a7, float:1.9009338E38)
        L_0x0193:
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r7] = r1
            r10[r6] = r0
            java.lang.String r8 = r8.getString(r9, r10)
            int r9 = r8.indexOf(r0)
            int r10 = r8.indexOf(r1)
            android.text.SpannableString r11 = new android.text.SpannableString
            r11.<init>(r8)
            if (r9 < 0) goto L_0x01ba
            com.opengarden.firechat.activity.VectorRoomActivity$cancelAllClickableSpan r8 = new com.opengarden.firechat.activity.VectorRoomActivity$cancelAllClickableSpan
            r8.<init>()
            int r0 = r0.length()
            int r0 = r0 + r9
            r11.setSpan(r8, r9, r0, r7)
        L_0x01ba:
            if (r10 < 0) goto L_0x01c9
            com.opengarden.firechat.activity.VectorRoomActivity$resendAllClickableSpan r0 = new com.opengarden.firechat.activity.VectorRoomActivity$resendAllClickableSpan
            r0.<init>()
            int r1 = r1.length()
            int r1 = r1 + r10
            r11.setSpan(r0, r10, r1, r7)
        L_0x01c9:
            android.widget.TextView r0 = r15.mNotificationTextView
            android.text.method.MovementMethod r1 = android.text.method.LinkMovementMethod.getInstance()
            r0.setMovementMethod(r1)
            int r1 = android.support.p000v4.content.ContextCompat.getColor(r15, r4)
            r9 = r11
            r0 = 1
            goto L_0x0074
        L_0x01da:
            boolean r2 = r15.mIsUnreadPreviewMode
            if (r2 == 0) goto L_0x01e4
            android.view.View r2 = r15.mNotificationsArea
            r2.setVisibility(r7)
            goto L_0x01f6
        L_0x01e4:
            java.lang.String r2 = r15.mEventId
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x01f6
            android.view.View r2 = r15.mNotificationsArea
            if (r8 == 0) goto L_0x01f2
            r4 = 0
            goto L_0x01f3
        L_0x01f2:
            r4 = 4
        L_0x01f3:
            r2.setVisibility(r4)
        L_0x01f6:
            if (r5 == r3) goto L_0x0207
            android.widget.ImageView r2 = r15.mNotificationIconImageView
            r2.setImageResource(r3)
            android.widget.TextView r2 = r15.mNotificationTextView
            r2.setText(r9)
            android.widget.TextView r2 = r15.mNotificationTextView
            r2.setTextColor(r1)
        L_0x0207:
            android.view.MenuItem r1 = r15.mResendUnsentMenuItem
            if (r1 == 0) goto L_0x0210
            android.view.MenuItem r1 = r15.mResendUnsentMenuItem
            r1.setVisible(r0)
        L_0x0210:
            android.view.MenuItem r1 = r15.mResendDeleteMenuItem
            if (r1 == 0) goto L_0x0219
            android.view.MenuItem r1 = r15.mResendDeleteMenuItem
            r1.setVisible(r0)
        L_0x0219:
            android.view.MenuItem r0 = r15.mSearchInRoomMenuItem
            if (r0 == 0) goto L_0x0229
            android.view.MenuItem r0 = r15.mSearchInRoomMenuItem
            com.opengarden.firechat.matrixsdk.data.Room r1 = r15.mRoom
            boolean r1 = r1.isEncrypted()
            r1 = r1 ^ r6
            r0.setVisible(r1)
        L_0x0229:
            android.view.MenuItem r0 = r15.mUseMatrixAppsMenuItem
            if (r0 == 0) goto L_0x0240
            android.view.MenuItem r0 = r15.mUseMatrixAppsMenuItem
            java.lang.String r1 = r15.mEventId
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x023c
            com.opengarden.firechat.matrixsdk.data.RoomPreviewData r1 = sRoomPreviewData
            if (r1 != 0) goto L_0x023c
            goto L_0x023d
        L_0x023c:
            r6 = 0
        L_0x023d:
            r0.setVisible(r6)
        L_0x0240:
            return
        L_0x0241:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorRoomActivity.refreshNotificationsArea():void");
    }

    /* access modifiers changed from: private */
    public void refreshCallButtons(boolean z) {
        if (this.mRoom != null && sRoomPreviewData == null && this.mEventId == null && canSendMessages()) {
            int i = 0;
            boolean z2 = this.mRoom.canPerformCall() && this.mSession.isVoipCallSupported();
            IMXCall activeCall = CallsManager.getSharedInstance().getActiveCall();
            Widget activeWidget = this.mVectorOngoingConferenceCallView.getActiveWidget();
            if (activeCall == null && activeWidget == null) {
                View view = this.mStartCallLayout;
                if (!z2 || this.mEditText.getText().length() != 0) {
                    i = 8;
                }
                view.setVisibility(i);
                this.mStopCallLayout.setVisibility(8);
            } else if (activeWidget != null) {
                this.mStartCallLayout.setVisibility(8);
                this.mStopCallLayout.setVisibility(8);
            } else {
                IMXCall callWithRoomId = this.mSession.mCallsManager.getCallWithRoomId(this.mRoom.getRoomId());
                activeCall.removeListener(this.mCallListener);
                activeCall.addListener(this.mCallListener);
                this.mStartCallLayout.setVisibility(8);
                View view2 = this.mStopCallLayout;
                if (activeCall != callWithRoomId) {
                    i = 8;
                }
                view2.setVisibility(i);
            }
            if (z) {
                this.mVectorOngoingConferenceCallView.refresh();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRoomTypings() {
        if (this.mRoom != null) {
            this.mLatestTypingMessage = null;
            List typingUsers = this.mRoom.getTypingUsers();
            if (typingUsers != null && typingUsers.size() > 0) {
                String myUserId = this.mSession.getMyUserId();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < typingUsers.size(); i++) {
                    RoomMember member = this.mRoom.getMember((String) typingUsers.get(i));
                    if (!(member == null || TextUtils.equals(myUserId, member.getUserId()) || member.displayname == null)) {
                        arrayList.add(member.displayname);
                    }
                }
                if (arrayList.size() == 0) {
                    this.mLatestTypingMessage = null;
                } else if (1 == arrayList.size()) {
                    this.mLatestTypingMessage = String.format(VectorApp.getApplicationLocale(), getString(C1299R.string.room_one_user_is_typing), new Object[]{arrayList.get(0)});
                } else if (2 == arrayList.size()) {
                    this.mLatestTypingMessage = String.format(VectorApp.getApplicationLocale(), getString(C1299R.string.room_two_users_are_typing), new Object[]{arrayList.get(0), arrayList.get(1)});
                } else if (arrayList.size() > 2) {
                    this.mLatestTypingMessage = String.format(VectorApp.getApplicationLocale(), getString(C1299R.string.room_many_users_are_typing), new Object[]{arrayList.get(0), arrayList.get(1)});
                }
            }
            refreshNotificationsArea();
        }
    }

    /* access modifiers changed from: private */
    public void updateActionBarTitleAndTopic() {
        setTitle();
        setTopic();
    }

    private void setTopic() {
        String str = this.mRoom != null ? this.mRoom.getTopic() : (sRoomPreviewData == null || sRoomPreviewData.getRoomState() == null) ? null : sRoomPreviewData.getRoomState().topic;
        setTopic(str);
    }

    /* access modifiers changed from: private */
    public void setTopic(String str) {
        if (!TextUtils.isEmpty(this.mEventId)) {
            this.mActionBarCustomTopic.setVisibility(8);
            return;
        }
        updateRoomHeaderTopic();
        this.mActionBarCustomTopic.setText(str);
        if (8 != this.mRoomHeaderView.getVisibility()) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            this.mActionBarCustomTopic.setVisibility(8);
        } else {
            this.mActionBarCustomTopic.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void updateRoomHeaderAvatar() {
        if (this.mRoom != null) {
            VectorUtils.loadRoomAvatar((Context) this, this.mSession, this.mActionBarHeaderRoomAvatar, this.mRoom);
        } else if (sRoomPreviewData != null) {
            String roomName = sRoomPreviewData.getRoomName();
            if (TextUtils.isEmpty(roomName)) {
                roomName = StringUtils.SPACE;
            }
            VectorUtils.loadUserAvatar(this, sRoomPreviewData.getSession(), this.mActionBarHeaderRoomAvatar, sRoomPreviewData.getRoomAvatarUrl(), sRoomPreviewData.getRoomId(), roomName);
        }
    }

    private void setActionBarDefaultCustomLayout() {
        this.mActionBarCustomTitle = (TextView) findViewById(C1299R.C1301id.room_action_bar_title);
        this.mActionBarCustomTopic = (TextView) findViewById(C1299R.C1301id.room_action_bar_topic);
        this.mActionBarCustomArrowImageView = (ImageView) findViewById(C1299R.C1301id.open_chat_header_arrow);
        View findViewById = findViewById(C1299R.C1301id.header_texts_container);
        this.mActionBarCustomArrowImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorRoomActivity.this.mRoomHeaderView == null) {
                    return;
                }
                if (8 == VectorRoomActivity.this.mRoomHeaderView.getVisibility()) {
                    VectorRoomActivity.this.enableActionBarHeader(true);
                } else {
                    VectorRoomActivity.this.enableActionBarHeader(false);
                }
            }
        });
        findViewById.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(VectorRoomActivity.this.mEventId) && VectorRoomActivity.sRoomPreviewData == null) {
                    VectorRoomActivity.this.enableActionBarHeader(true);
                }
            }
        });
        if (this.mRoomHeaderView != null) {
            this.mRoomHeaderView.setOnTouchListener(new OnTouchListener() {
                private float mStartX;
                private float mStartY;

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        this.mStartX = motionEvent.getX();
                        this.mStartY = motionEvent.getY();
                    } else if (motionEvent.getAction() == 1) {
                        float x = motionEvent.getX();
                        float y = motionEvent.getY() - this.mStartY;
                        if (Math.abs(y) <= Math.abs(x - this.mStartX) || y >= 0.0f) {
                            VectorRoomActivity.this.launchRoomDetails(2);
                        } else {
                            VectorRoomActivity.this.enableActionBarHeader(false);
                        }
                    }
                    return true;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setTitle() {
        String str = this.mDefaultRoomName;
        if (this.mSession != null && this.mRoom != null) {
            str = VectorUtils.getRoomDisplayName(this, this.mSession, this.mRoom);
            if (TextUtils.isEmpty(str)) {
                str = this.mDefaultRoomName;
            }
            if (!TextUtils.isEmpty(this.mEventId) && !this.mIsUnreadPreviewMode) {
                StringBuilder sb = new StringBuilder();
                sb.append(getResources().getText(C1299R.string.search));
                sb.append(" : ");
                sb.append(str);
                str = sb.toString();
            }
        } else if (sRoomPreviewData != null) {
            str = sRoomPreviewData.getRoomName();
        }
        if (this.mActionBarCustomTitle != null) {
            this.mActionBarCustomTitle.setText(str);
        } else {
            setTitle(str);
        }
        if (this.mActionBarHeaderRoomName != null) {
            this.mActionBarHeaderRoomName.setText(str);
        }
    }

    private void updateActionBarHeaderView() {
        updateRoomHeaderAvatar();
        if (this.mRoom != null) {
            this.mActionBarHeaderRoomName.setText(VectorUtils.getRoomDisplayName(this, this.mSession, this.mRoom));
        } else if (sRoomPreviewData != null) {
            this.mActionBarHeaderRoomName.setText(sRoomPreviewData.getRoomName());
        } else {
            this.mActionBarHeaderRoomName.setText("");
        }
        updateRoomHeaderTopic();
        updateRoomHeaderMembersStatus();
    }

    private void updateRoomHeaderTopic() {
        if (this.mActionBarCustomTopic != null) {
            String str = null;
            if (this.mRoom != null) {
                str = this.mRoom.isReady() ? this.mRoom.getTopic() : this.mDefaultTopic;
            } else if (!(sRoomPreviewData == null || sRoomPreviewData.getRoomState() == null)) {
                str = sRoomPreviewData.getRoomState().topic;
            }
            if (TextUtils.isEmpty(str)) {
                this.mActionBarHeaderRoomTopic.setVisibility(8);
                return;
            }
            this.mActionBarHeaderRoomTopic.setVisibility(0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
            MatrixURLSpan.refreshMatrixSpans(spannableStringBuilder, this.mVectorMessageListFragment);
            this.mActionBarHeaderRoomTopic.setText(spannableStringBuilder);
            URLSpan[] uRLSpanArr = (URLSpan[]) spannableStringBuilder.getSpans(0, str.length(), URLSpan.class);
            if (uRLSpanArr != null && uRLSpanArr.length > 0) {
                for (URLSpan makeLinkClickable : uRLSpanArr) {
                    makeLinkClickable(spannableStringBuilder, makeLinkClickable, str);
                }
            }
        }
    }

    public void makeLinkClickable(SpannableStringBuilder spannableStringBuilder, URLSpan uRLSpan, final String str) {
        int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
        int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
        if (spanStart >= 0 && spanEnd >= 0) {
            spannableStringBuilder.setSpan(new ClickableSpan() {
                public void onClick(View view) {
                    if (VectorRoomActivity.this.mVectorMessageListFragment != null) {
                        VectorRoomActivity.this.mVectorMessageListFragment.onURLClick(Uri.parse(VectorUtils.getPermalink(str, null)));
                    }
                }
            }, spanStart, spanEnd, spannableStringBuilder.getSpanFlags(uRLSpan));
            spannableStringBuilder.removeSpan(uRLSpan);
        }
    }

    private boolean canSendMessages() {
        if (this.mRoom == null || this.mRoom.getLiveState() == null) {
            return false;
        }
        PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
        if (powerLevels != null) {
            return powerLevels.maySendMessage(this.mMyUserId);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void checkSendEventStatus() {
        if (this.mRoom != null && this.mRoom.getLiveState() != null) {
            boolean canSendMessages = canSendMessages();
            int i = 8;
            this.mSendingMessagesLayout.setVisibility(canSendMessages ? 0 : 8);
            View view = this.mCanNotPostTextView;
            if (!canSendMessages) {
                i = 0;
            }
            view.setVisibility(i);
        }
    }

    /* access modifiers changed from: private */
    public void updateRoomHeaderMembersStatus() {
        String str;
        if (this.mActionBarHeaderActiveMembersLayout == null) {
            return;
        }
        if (this.mActionBarCustomTitle.getVisibility() != 8) {
            this.mActionBarHeaderActiveMembersLayout.setVisibility(8);
        } else if (this.mRoom == null && sRoomPreviewData == null) {
            this.mActionBarHeaderActiveMembersLayout.setVisibility(8);
        } else {
            RoomState roomState = sRoomPreviewData != null ? sRoomPreviewData.getRoomState() : this.mRoom.getState();
            if (roomState != null) {
                int i = 0;
                int i2 = 0;
                for (RoomMember roomMember : roomState.getDisplayableMembers()) {
                    if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN)) {
                        i++;
                        User user = this.mSession.getDataHandler().getStore().getUser(roomMember.getUserId());
                        if (user != null && user.isActive()) {
                            i2++;
                        }
                    }
                }
                if ((roomState instanceof PublicRoom) && i == 0) {
                    i = ((PublicRoom) roomState).numJoinedMembers;
                    i2 = i;
                }
                boolean z = true;
                if (i == 1) {
                    str = getResources().getString(C1299R.string.room_title_one_member);
                } else if (sRoomPreviewData != null) {
                    str = getResources().getQuantityString(C1299R.plurals.room_title_members, i, new Object[]{Integer.valueOf(i)});
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(i2);
                    sb.append("/");
                    sb.append(getResources().getQuantityString(C1299R.plurals.room_header_active_members_count, i, new Object[]{Integer.valueOf(i)}));
                    str = sb.toString();
                }
                if (!TextUtils.isEmpty(str)) {
                    this.mActionBarHeaderActiveMembersTextView.setText(str);
                    this.mActionBarHeaderActiveMembersLayout.setVisibility(0);
                    if (this.mRoom != null && TextUtils.isEmpty(this.mEventId) && sRoomPreviewData == null) {
                        z = false;
                    }
                    int i3 = 4;
                    this.mActionBarHeaderActiveMembersListButton.setVisibility(z ? 4 : 0);
                    View view = this.mActionBarHeaderActiveMembersInviteButton;
                    if (!z) {
                        i3 = 0;
                    }
                    view.setVisibility(i3);
                    return;
                }
                this.mActionBarHeaderActiveMembersLayout.setVisibility(8);
                return;
            }
            this.mActionBarHeaderActiveMembersLayout.setVisibility(8);
        }
    }

    public void dismissKeyboard() {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
    }

    /* access modifiers changed from: private */
    public void enableActionBarHeader(boolean z) {
        this.mIsHeaderViewDisplayed = z;
        if (true == z) {
            dismissKeyboard();
            this.mActionBarCustomTitle.setVisibility(8);
            this.mActionBarCustomTopic.setVisibility(8);
            updateActionBarHeaderView();
            this.mActionBarCustomArrowImageView.setImageResource(C1299R.C1300drawable.ic_arrow_drop_up_white);
            this.mRoomHeaderView.setVisibility(0);
            this.mToolbar.setBackgroundColor(0);
        } else if (this.mRoomHeaderView.getVisibility() == 0) {
            this.mActionBarCustomTitle.setVisibility(0);
            if (!TextUtils.isEmpty(this.mActionBarCustomTopic.getText())) {
                this.mActionBarCustomTopic.setVisibility(0);
            }
            updateActionBarTitleAndTopic();
            this.mActionBarCustomArrowImageView.setImageResource(C1299R.C1300drawable.ic_arrow_drop_down_white);
            this.mRoomHeaderView.setVisibility(8);
            this.mToolbar.setBackgroundColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.primary_color));
        }
    }

    private void manageBannedHeader(RoomMember roomMember) {
        this.mRoomPreviewLayout.setVisibility(0);
        TextView textView = (TextView) findViewById(C1299R.C1301id.room_preview_invitation_textview);
        if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_BAN)) {
            textView.setText(getString(C1299R.string.has_been_banned, new Object[]{VectorUtils.getRoomDisplayName(this, this.mSession, this.mRoom), this.mRoom.getLiveState().getMemberName(roomMember.mSender)}));
        } else {
            textView.setText(getString(C1299R.string.has_been_kicked, new Object[]{VectorUtils.getRoomDisplayName(this, this.mSession, this.mRoom), this.mRoom.getLiveState().getMemberName(roomMember.mSender)}));
        }
        TextView textView2 = (TextView) findViewById(C1299R.C1301id.room_preview_subinvitation_textview);
        textView2.setText(getString(C1299R.string.reason_colon, new Object[]{roomMember.reason}));
        if (!TextUtils.isEmpty(roomMember.reason)) {
            textView2.setText(getString(C1299R.string.reason_colon, new Object[]{roomMember.reason}));
        } else {
            textView2.setText(null);
        }
        Button button = (Button) findViewById(C1299R.C1301id.button_join_room);
        if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_BAN)) {
            button.setVisibility(4);
        } else {
            button.setText(getString(C1299R.string.rejoin));
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorRoomActivity.this.showWaitingView();
                    VectorRoomActivity.this.mSession.joinRoom(VectorRoomActivity.this.mRoom.getRoomId(), new ApiCallback<String>() {
                        public void onSuccess(String str) {
                            VectorRoomActivity.this.hideWaitingView();
                            HashMap hashMap = new HashMap();
                            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomActivity.this.mSession.getMyUserId());
                            hashMap.put("EXTRA_ROOM_ID", VectorRoomActivity.this.mRoom.getRoomId());
                            Intent intent = new Intent(VectorRoomActivity.this, VectorHomeActivity.class);
                            intent.setFlags(872415232);
                            intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, hashMap);
                            VectorRoomActivity.this.startActivity(intent);
                        }

                        private void onError(String str) {
                            String access$300 = VectorRoomActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("re join failed ");
                            sb.append(str);
                            Log.m209d(access$300, sb.toString());
                            CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                            VectorRoomActivity.this.hideWaitingView();
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
                    });
                }
            });
        }
        Button button2 = (Button) findViewById(C1299R.C1301id.button_decline);
        button2.setText(getString(C1299R.string.forget_room));
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VectorRoomActivity.this.mRoom.forget(new ApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        VectorRoomActivity.this.finish();
                    }

                    private void onError(String str) {
                        String access$300 = VectorRoomActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("forget failed ");
                        sb.append(str);
                        Log.m209d(access$300, sb.toString());
                        CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                        VectorRoomActivity.this.hideWaitingView();
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
                });
            }
        });
        enableActionBarHeader(true);
    }

    public RoomPreviewData getRoomPreviewData() {
        return sRoomPreviewData;
    }

    private void manageRoomPreview() {
        if (sRoomPreviewData != null) {
            this.mRoomPreviewLayout.setVisibility(0);
            TextView textView = (TextView) findViewById(C1299R.C1301id.room_preview_invitation_textview);
            TextView textView2 = (TextView) findViewById(C1299R.C1301id.room_preview_subinvitation_textview);
            Button button = (Button) findViewById(C1299R.C1301id.button_join_room);
            Button button2 = (Button) findViewById(C1299R.C1301id.button_decline);
            final RoomEmailInvitation roomEmailInvitation = sRoomPreviewData.getRoomEmailInvitation();
            String roomName = sRoomPreviewData.getRoomName();
            if (TextUtils.isEmpty(roomName)) {
                roomName = StringUtils.SPACE;
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Preview the room ");
            sb.append(sRoomPreviewData.getRoomId());
            Log.m209d(str, sb.toString());
            if (this.mRoom != null) {
                Log.m209d(LOG_TAG, "manageRoomPreview : The room is known");
                String str2 = "";
                if (roomEmailInvitation != null) {
                    str2 = roomEmailInvitation.inviterName;
                }
                if (TextUtils.isEmpty(str2)) {
                    for (RoomMember roomMember : this.mRoom.getActiveMembers()) {
                        if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN)) {
                            str2 = TextUtils.isEmpty(roomMember.displayname) ? roomMember.getUserId() : roomMember.displayname;
                        }
                    }
                }
                textView.setText(getResources().getString(C1299R.string.room_preview_invitation_format, new Object[]{str2}));
                button2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        Log.m209d(VectorRoomActivity.LOG_TAG, "The user clicked on decline.");
                        VectorRoomActivity.this.showWaitingView();
                        VectorRoomActivity.this.mRoom.leave(new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                Log.m209d(VectorRoomActivity.LOG_TAG, "The invitation is rejected");
                                VectorRoomActivity.this.onDeclined();
                            }

                            private void onError(String str) {
                                String access$300 = VectorRoomActivity.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("The invitation rejection failed ");
                                sb.append(str);
                                Log.m209d(access$300, sb.toString());
                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                                VectorRoomActivity.this.hideWaitingView();
                            }

                            public void onNetworkError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                                    VectorRoomActivity.this.hideWaitingView();
                                    VectorRoomActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                                    return;
                                }
                                onError(matrixError.getLocalizedMessage());
                            }

                            public void onUnexpectedError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }
                        });
                    }
                });
            } else {
                if (roomEmailInvitation == null || TextUtils.isEmpty(roomEmailInvitation.email)) {
                    Resources resources = getResources();
                    Object[] objArr = new Object[1];
                    if (TextUtils.isEmpty(sRoomPreviewData.getRoomName())) {
                        roomName = getResources().getString(C1299R.string.room_preview_try_join_an_unknown_room_default);
                    }
                    objArr[0] = roomName;
                    textView.setText(resources.getString(C1299R.string.room_preview_try_join_an_unknown_room, objArr));
                    if (!(sRoomPreviewData.getRoomResponse() == null || sRoomPreviewData.getRoomResponse().messages == null)) {
                        textView2.setText(getResources().getString(C1299R.string.room_preview_room_interactions_disabled));
                    }
                } else {
                    textView.setText(getResources().getString(C1299R.string.room_preview_invitation_format, new Object[]{roomEmailInvitation.inviterName}));
                    textView2.setText(getResources().getString(C1299R.string.room_preview_unlinked_email_warning, new Object[]{roomEmailInvitation.email}));
                }
                button2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        Log.m209d(VectorRoomActivity.LOG_TAG, "The invitation is declined (unknown room)");
                        VectorRoomActivity.sRoomPreviewData = null;
                        VectorRoomActivity.this.finish();
                    }
                });
            }
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Log.m209d(VectorRoomActivity.LOG_TAG, "The user clicked on Join.");
                    if (VectorRoomActivity.sRoomPreviewData != null) {
                        Room room = VectorRoomActivity.sRoomPreviewData.getSession().getDataHandler().getRoom(VectorRoomActivity.sRoomPreviewData.getRoomId());
                        String str = null;
                        if (roomEmailInvitation != null) {
                            str = roomEmailInvitation.signUrl;
                        }
                        VectorRoomActivity.this.showWaitingView();
                        room.joinWithThirdPartySigned(VectorRoomActivity.sRoomPreviewData.getRoomIdOrAlias(), str, new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                VectorRoomActivity.this.onJoined();
                            }

                            private void onError(String str) {
                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                                VectorRoomActivity.this.hideWaitingView();
                            }

                            public void onNetworkError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                                    VectorRoomActivity.this.hideWaitingView();
                                    VectorRoomActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                                    return;
                                }
                                onError(matrixError.getLocalizedMessage());
                            }

                            public void onUnexpectedError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }
                        });
                        return;
                    }
                    VectorRoomActivity.this.finish();
                }
            });
            enableActionBarHeader(true);
            return;
        }
        this.mRoomPreviewLayout.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void onDeclined() {
        if (sRoomPreviewData != null) {
            finish();
            sRoomPreviewData = null;
        }
    }

    /* access modifiers changed from: private */
    public void onJoined() {
        if (sRoomPreviewData != null) {
            HashMap hashMap = new HashMap();
            processDirectMessageRoom();
            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
            hashMap.put("EXTRA_ROOM_ID", sRoomPreviewData.getRoomId());
            if (sRoomPreviewData.getEventId() != null) {
                hashMap.put(EXTRA_EVENT_ID, sRoomPreviewData.getEventId());
            }
            Intent intent = new Intent(this, VectorHomeActivity.class);
            intent.setFlags(872415232);
            intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, hashMap);
            startActivity(intent);
            sRoomPreviewData = null;
        }
    }

    private void processDirectMessageRoom() {
        Room room = sRoomPreviewData.getSession().getDataHandler().getRoom(sRoomPreviewData.getRoomId());
        if (room != null && room.isDirectChatInvitation()) {
            String myUserId = this.mSession.getMyUserId();
            if (this.mRoom != null) {
                Collection members = this.mRoom.getMembers();
                if (2 == members.size()) {
                    if (!RoomUtils.isDirectChat(this.mSession, sRoomPreviewData.getRoomId())) {
                        Iterator it = members.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            RoomMember roomMember = (RoomMember) it.next();
                            if (!roomMember.getUserId().equals(myUserId)) {
                                CommonActivityUtils.setToggleDirectMessageRoom(this.mSession, sRoomPreviewData.getRoomId(), roomMember.getUserId(), this, this.mDirectMessageListener);
                                break;
                            }
                        }
                    } else {
                        Log.m209d(LOG_TAG, "## processDirectMessageRoom(): attempt to add an already direct message room");
                    }
                }
            }
        }
    }

    private void onActivityResultRoomInvite(Intent intent) {
        List list = (List) intent.getSerializableExtra(VectorRoomInviteMembersActivity.EXTRA_OUT_SELECTED_USER_IDS);
        if (this.mRoom != null && list != null && list.size() > 0) {
            showWaitingView();
            this.mRoom.invite(list, (ApiCallback<Void>) new ApiCallback<Void>() {
                private void onDone(String str) {
                    if (!TextUtils.isEmpty(str)) {
                        CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                    }
                    VectorRoomActivity.this.hideWaitingView();
                }

                public void onSuccess(Void voidR) {
                    onDone(null);
                }

                public void onNetworkError(Exception exc) {
                    onDone(exc.getMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onDone(matrixError.getMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onDone(exc.getMessage());
                }
            });
        }
    }

    private void onActivityResultRoomAvatarUpdate(Intent intent) {
        if (this.mSession != null && this.mRoom != null) {
            Uri thumbnailUriFromIntent = VectorUtils.getThumbnailUriFromIntent(this, intent, this.mSession.getMediasCache());
            if (thumbnailUriFromIntent != null) {
                showWaitingView();
                Resource openResource = ResourceUtils.openResource(this, thumbnailUriFromIntent, null);
                if (openResource != null) {
                    this.mSession.getMediasCache().uploadContent(openResource.mContentStream, null, openResource.mMimeType, null, new MXMediaUploadListener() {
                        public void onUploadError(String str, int i, String str2) {
                            Log.m211e(VectorRoomActivity.LOG_TAG, "Fail to upload the avatar");
                        }

                        public void onUploadComplete(String str, final String str2) {
                            VectorRoomActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.m209d(VectorRoomActivity.LOG_TAG, "The avatar has been uploaded, update the room avatar");
                                    VectorRoomActivity.this.mRoom.updateAvatarUrl(str2, new ApiCallback<Void>() {
                                        private void onDone(String str) {
                                            if (!TextUtils.isEmpty(str)) {
                                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                                            }
                                            VectorRoomActivity.this.hideWaitingView();
                                            VectorRoomActivity.this.updateRoomHeaderAvatar();
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
                            });
                        }
                    });
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRoomTitleClick() {
        if (this.mRoom != null) {
            LayoutInflater from = LayoutInflater.from(this);
            Builder builder = new Builder(this);
            View inflate = from.inflate(C1299R.layout.dialog_text_edittext, null);
            builder.setView(inflate);
            ((TextView) inflate.findViewById(C1299R.C1301id.dialog_title)).setText(getResources().getString(C1299R.string.room_info_room_name));
            final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.dialog_edit_text);
            editText.setText(this.mRoom.getLiveState().name);
            builder.setCancelable(false).setPositiveButton(C1299R.string.f115ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    VectorRoomActivity.this.showWaitingView();
                    VectorRoomActivity.this.mRoom.updateName(editText.getText().toString(), new ApiCallback<Void>() {
                        private void onDone(String str) {
                            if (!TextUtils.isEmpty(str)) {
                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                            }
                            VectorRoomActivity.this.hideWaitingView();
                            VectorRoomActivity.this.updateActionBarTitleAndTopic();
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
            }).setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }

    /* access modifiers changed from: private */
    public void onRoomTopicClick() {
        if (this.mRoom != null) {
            LayoutInflater from = LayoutInflater.from(this);
            Builder builder = new Builder(this);
            View inflate = from.inflate(C1299R.layout.dialog_text_edittext, null);
            builder.setView(inflate);
            ((TextView) inflate.findViewById(C1299R.C1301id.dialog_title)).setText(getResources().getString(C1299R.string.room_info_room_topic));
            final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.dialog_edit_text);
            editText.setText(this.mRoom.getLiveState().topic);
            builder.setCancelable(false).setPositiveButton(C1299R.string.f115ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    VectorRoomActivity.this.showWaitingView();
                    VectorRoomActivity.this.mRoom.updateTopic(editText.getText().toString(), new ApiCallback<Void>() {
                        private void onDone(String str) {
                            if (!TextUtils.isEmpty(str)) {
                                CommonActivityUtils.displayToast(VectorRoomActivity.this, str);
                            }
                            VectorRoomActivity.this.hideWaitingView();
                            VectorRoomActivity.this.updateActionBarTitleAndTopic();
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
            }).setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
        }
    }

    private void addRoomHeaderClickListeners() {
        View findViewById = findViewById(C1299R.C1301id.room_avatar);
        if (findViewById != null) {
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorRoomActivity.this.mRoom != null && VectorRoomActivity.this.mRoom.getLiveState() != null) {
                        if (!CommonActivityUtils.isPowerLevelEnoughForAvatarUpdate(VectorRoomActivity.this.mRoom, VectorRoomActivity.this.mSession)) {
                            VectorRoomActivity.this.launchRoomDetails(2);
                        } else if (CommonActivityUtils.checkPermissions(1, (Activity) VectorRoomActivity.this)) {
                            Intent intent = new Intent(VectorRoomActivity.this, VectorMediasPickerActivity.class);
                            intent.putExtra(VectorMediasPickerActivity.EXTRA_AVATAR_MODE, true);
                            VectorRoomActivity.this.startActivityForResult(intent, 3);
                        }
                    }
                }
            });
        }
        View findViewById2 = findViewById(C1299R.C1301id.action_bar_header_room_title);
        if (findViewById2 != null) {
            findViewById2.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorRoomActivity.this.mRoom != null && VectorRoomActivity.this.mRoom.getLiveState() != null) {
                        PowerLevels powerLevels = VectorRoomActivity.this.mRoom.getLiveState().getPowerLevels();
                        boolean z = false;
                        if (powerLevels != null && powerLevels.getUserPowerLevel(VectorRoomActivity.this.mSession.getMyUserId()) >= powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_NAME)) {
                            z = true;
                        }
                        if (z) {
                            VectorRoomActivity.this.onRoomTitleClick();
                        } else {
                            VectorRoomActivity.this.launchRoomDetails(2);
                        }
                    }
                }
            });
        }
        View findViewById3 = findViewById(C1299R.C1301id.action_bar_header_room_topic);
        if (findViewById3 != null) {
            findViewById3.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorRoomActivity.this.mRoom != null && VectorRoomActivity.this.mRoom.getLiveState() != null) {
                        PowerLevels powerLevels = VectorRoomActivity.this.mRoom.getLiveState().getPowerLevels();
                        boolean z = false;
                        if (powerLevels != null && powerLevels.getUserPowerLevel(VectorRoomActivity.this.mSession.getMyUserId()) >= powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_NAME)) {
                            z = true;
                        }
                        if (z) {
                            VectorRoomActivity.this.onRoomTopicClick();
                        } else {
                            VectorRoomActivity.this.launchRoomDetails(2);
                        }
                    }
                }
            });
        }
        if (this.mActionBarHeaderActiveMembersListButton != null) {
            this.mActionBarHeaderActiveMembersListButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorRoomActivity.this.launchRoomDetails(0);
                }
            });
        }
        if (this.mActionBarHeaderActiveMembersTextView != null) {
            this.mActionBarHeaderActiveMembersTextView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorRoomActivity.this.launchRoomDetails(0);
                }
            });
        }
        if (this.mActionBarHeaderActiveMembersInviteButton != null) {
            this.mActionBarHeaderActiveMembersInviteButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorRoomActivity.this.launchInvitePeople();
                }
            });
        }
    }

    private void displayE2eRoomAlert() {
        if (!isFinishing()) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (!defaultSharedPreferences.contains(E2E_WARNINGS_PREFERENCES) && this.mRoom != null && this.mRoom.isEncrypted()) {
                defaultSharedPreferences.edit().putBoolean(E2E_WARNINGS_PREFERENCES, false).apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle((int) C1299R.string.room_e2e_alert_title);
                builder.setMessage((int) C1299R.string.room_e2e_alert_message);
                builder.setPositiveButton((int) C1299R.string.f115ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create().show();
            }
        }
    }
}
