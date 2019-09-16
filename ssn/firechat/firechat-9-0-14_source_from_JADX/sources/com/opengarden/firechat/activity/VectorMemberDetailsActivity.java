package com.opengarden.firechat.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.p003v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.VectorMemberDetailsAdapter;
import com.opengarden.firechat.adapters.VectorMemberDetailsAdapter.AdapterMemberActionItems;
import com.opengarden.firechat.adapters.VectorMemberDetailsAdapter.IEnablingActions;
import com.opengarden.firechat.adapters.VectorMemberDetailsDevicesAdapter;
import com.opengarden.firechat.adapters.VectorMemberDetailsDevicesAdapter.IDevicesAdapterListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VectorMemberDetailsActivity extends MXCActionBarActivity implements IEnablingActions, IDevicesAdapterListener {
    private static final String AVATAR_FULLSCREEN_MODE = "AVATAR_FULLSCREEN_MODE";
    public static final String EXTRA_MEMBER_AVATAR_URL = "EXTRA_MEMBER_AVATAR_URL";
    public static final String EXTRA_MEMBER_DISPLAY_NAME = "EXTRA_MEMBER_DISPLAY_NAME";
    public static final String EXTRA_MEMBER_ID = "EXTRA_MEMBER_ID";
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    public static final String EXTRA_STORE_ID = "EXTRA_STORE_ID";
    public static final int ITEM_ACTION_BAN = 3;
    private static final int ITEM_ACTION_DEVICES = 15;
    private static final int ITEM_ACTION_IGNORE = 5;
    private static final int ITEM_ACTION_INVITE = 0;
    public static final int ITEM_ACTION_KICK = 2;
    private static final int ITEM_ACTION_LEAVE = 1;
    private static final int ITEM_ACTION_MENTION = 14;
    private static final int ITEM_ACTION_SET_ADMIN = 9;
    private static final int ITEM_ACTION_SET_DEFAULT_POWER_LEVEL = 7;
    private static final int ITEM_ACTION_SET_MODERATOR = 8;
    private static final int ITEM_ACTION_START_CHAT = 11;
    private static final int ITEM_ACTION_START_VIDEO_CALL = 13;
    private static final int ITEM_ACTION_START_VOICE_CALL = 12;
    private static final int ITEM_ACTION_UNBAN = 4;
    private static final int ITEM_ACTION_UNIGNORE = 6;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMemberDetailsActivity";
    public static final String RESULT_MENTION_ID = "RESULT_MENTION_ID";
    private static final int VECTOR_ROOM_ADMIN_LEVEL = 100;
    private static final int VECTOR_ROOM_MODERATOR_LEVEL = 50;
    /* access modifiers changed from: private */
    public final ApiCallback<String> mCreateDirectMessageCallBack = new ApiCallback<String>() {
        public void onSuccess(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorMemberDetailsActivity.this.mSession.getMyUserId());
            hashMap.put("EXTRA_ROOM_ID", str);
            hashMap.put(VectorRoomActivity.EXTRA_EXPAND_ROOM_HEADER, Boolean.valueOf(true));
            Log.m209d(VectorMemberDetailsActivity.LOG_TAG, "## mCreateDirectMessageCallBack: onSuccess - start goToRoomPage");
            CommonActivityUtils.goToRoomPage(VectorMemberDetailsActivity.this, VectorMemberDetailsActivity.this.mSession, hashMap);
        }

        public void onMatrixError(MatrixError matrixError) {
            String access$100 = VectorMemberDetailsActivity.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## mCreateDirectMessageCallBack: onMatrixError Msg=");
            sb.append(matrixError.getLocalizedMessage());
            Log.m209d(access$100, sb.toString());
            VectorMemberDetailsActivity.this.mRoomActionsListener.onMatrixError(matrixError);
        }

        public void onNetworkError(Exception exc) {
            String access$100 = VectorMemberDetailsActivity.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## mCreateDirectMessageCallBack: onNetworkError Msg=");
            sb.append(exc.getLocalizedMessage());
            Log.m209d(access$100, sb.toString());
            VectorMemberDetailsActivity.this.mRoomActionsListener.onNetworkError(exc);
        }

        public void onUnexpectedError(Exception exc) {
            String access$100 = VectorMemberDetailsActivity.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## mCreateDirectMessageCallBack: onUnexpectedError Msg=");
            sb.append(exc.getLocalizedMessage());
            Log.m209d(access$100, sb.toString());
            VectorMemberDetailsActivity.this.mRoomActionsListener.onUnexpectedError(exc);
        }
    };
    private View mDevicesListHeaderView;
    private ListView mDevicesListView;
    /* access modifiers changed from: private */
    public VectorMemberDetailsDevicesAdapter mDevicesListViewAdapter;
    private final ApiCallback<Void> mDevicesVerificationCallback = new ApiCallback<Void>() {
        public void onSuccess(Void voidR) {
            VectorMemberDetailsActivity.this.mDevicesListViewAdapter.notifyDataSetChanged();
        }

        public void onNetworkError(Exception exc) {
            VectorMemberDetailsActivity.this.mDevicesListViewAdapter.notifyDataSetChanged();
        }

        public void onMatrixError(MatrixError matrixError) {
            VectorMemberDetailsActivity.this.mDevicesListViewAdapter.notifyDataSetChanged();
        }

        public void onUnexpectedError(Exception exc) {
            VectorMemberDetailsActivity.this.mDevicesListViewAdapter.notifyDataSetChanged();
        }
    };
    /* access modifiers changed from: private */
    public ExpandableListView mExpandableListView;
    private ImageView mFullMemberAvatarImageView;
    private View mFullMemberAvatarLayout;
    /* access modifiers changed from: private */
    public VectorMemberDetailsAdapter mListViewAdapter;
    private final MXEventListener mLiveEventsListener = new MXEventListener() {
        public void onLiveEvent(final Event event, RoomState roomState) {
            VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    String type = event.getType();
                    if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS.equals(type)) {
                        VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (VectorMemberDetailsActivity.this.checkRoomMemberStatus()) {
                                    VectorMemberDetailsActivity.this.updateUi();
                                } else if (VectorMemberDetailsActivity.this.mRoom != null) {
                                    VectorMemberDetailsActivity.this.finish();
                                }
                            }
                        });
                    }
                }
            });
        }

        public void onLeaveRoom(String str) {
            VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VectorMemberDetailsActivity.this, VectorHomeActivity.class);
                    intent.setFlags(603979776);
                    VectorMemberDetailsActivity.this.startActivity(intent);
                }
            });
        }
    };
    private ImageView mMemberAvatarBadgeImageView;
    private ImageView mMemberAvatarImageView;
    /* access modifiers changed from: private */
    public String mMemberId;
    /* access modifiers changed from: private */
    public TextView mMemberNameTextView;
    private final MXEventListener mPresenceEventsListener = new MXEventListener() {
        public void onPresenceUpdate(Event event, User user) {
            if (VectorMemberDetailsActivity.this.mMemberId.equals(user.user_id)) {
                VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        VectorMemberDetailsActivity.this.updateMemberAvatarUi();
                        VectorMemberDetailsActivity.this.updatePresenceInfoUi();
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public TextView mPresenceTextView;
    /* access modifiers changed from: private */
    public Room mRoom;
    /* access modifiers changed from: private */
    public final ApiCallback<Void> mRoomActionsListener = new SimpleApiCallback<Void>(this) {
        public void onMatrixError(MatrixError matrixError) {
            if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                VectorMemberDetailsActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
            } else {
                Toast.makeText(VectorMemberDetailsActivity.this, matrixError.getLocalizedMessage(), 1).show();
            }
            VectorMemberDetailsActivity.this.updateUi();
        }

        public void onSuccess(Void voidR) {
            VectorMemberDetailsActivity.this.updateUi();
        }

        public void onNetworkError(Exception exc) {
            Toast.makeText(VectorMemberDetailsActivity.this, exc.getLocalizedMessage(), 1).show();
            VectorMemberDetailsActivity.this.updateUi();
        }

        public void onUnexpectedError(Exception exc) {
            Toast.makeText(VectorMemberDetailsActivity.this, exc.getLocalizedMessage(), 1).show();
            VectorMemberDetailsActivity.this.updateUi();
        }
    };
    /* access modifiers changed from: private */
    public RoomMember mRoomMember;
    /* access modifiers changed from: private */
    public MXSession mSession;
    private User mUser;

    public int getLayoutRes() {
        return C1299R.layout.activity_member_details;
    }

    /* access modifiers changed from: private */
    public void startCall(final boolean z) {
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "startCall : the session is not anymore valid");
        } else {
            this.mSession.mCallsManager.createCallInRoom(this.mRoom.getRoomId(), z, new ApiCallback<IMXCall>() {
                public void onSuccess(final IMXCall iMXCall) {
                    VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            final Intent intent = new Intent(VectorMemberDetailsActivity.this, VectorCallViewActivity.class);
                            intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, VectorMemberDetailsActivity.this.mSession.getCredentials().userId);
                            intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, iMXCall.getCallId());
                            VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    VectorMemberDetailsActivity.this.startActivity(intent);
                                }
                            });
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    CommonActivityUtils.displayToast(VectorMemberDetailsActivity.this, exc.getLocalizedMessage());
                    String access$100 = VectorMemberDetailsActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## startCall() failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    if (matrixError instanceof MXCryptoError) {
                        MXCryptoError mXCryptoError = (MXCryptoError) matrixError;
                        if (MXCryptoError.UNKNOWN_DEVICES_CODE.equals(mXCryptoError.errcode)) {
                            CommonActivityUtils.verifyUnknownDevices(VectorMemberDetailsActivity.this.mSession, CommonActivityUtils.getDevicesList((MXUsersDevicesMap) mXCryptoError.mExceptionData));
                            VectorMemberDetailsActivity.this.startCall(z);
                            return;
                        }
                    }
                    CommonActivityUtils.displayToast(VectorMemberDetailsActivity.this, matrixError.getLocalizedMessage());
                    String access$100 = VectorMemberDetailsActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## startCall() failed ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    CommonActivityUtils.displayToast(VectorMemberDetailsActivity.this, exc.getLocalizedMessage());
                    String access$100 = VectorMemberDetailsActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## startCall() failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
            });
        }
    }

    private void startCheckCallPermissions(boolean z) {
        if (CommonActivityUtils.checkPermissions(z ? 5 : 4, (Activity) this)) {
            startCall(z);
        }
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
        } else if (i == 4) {
            if (CommonActivityUtils.onPermissionResultAudioIpCall(this, strArr, iArr)) {
                startCall(false);
            }
        } else if (i == 5 && CommonActivityUtils.onPermissionResultVideoIpCall(this, strArr, iArr)) {
            startCall(true);
        }
    }

    public void selectRoom(final Room room) {
        runOnUiThread(new Runnable() {
            public void run() {
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorMemberDetailsActivity.this.mSession.getMyUserId());
                hashMap.put("EXTRA_ROOM_ID", room.getRoomId());
                String access$100 = VectorMemberDetailsActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## selectRoom(): open the room ");
                sb.append(room.getRoomId());
                Log.m209d(access$100, sb.toString());
                CommonActivityUtils.goToRoomPage(VectorMemberDetailsActivity.this, VectorMemberDetailsActivity.this.mSession, hashMap);
            }
        });
    }

    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v3, types: [int] */
    /* JADX WARNING: type inference failed for: r4v4, types: [int] */
    /* JADX WARNING: type inference failed for: r4v5, types: [boolean] */
    /* JADX WARNING: type inference failed for: r4v6 */
    /* JADX WARNING: type inference failed for: r4v7 */
    /* JADX WARNING: type inference failed for: r4v8 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r4v0
      assigns: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], ?[boolean, int, float, short, byte, char], int]
      uses: [int, boolean]
      mth insns count: 166
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performItemAction(int r7) {
        /*
            r6 = this;
            com.opengarden.firechat.matrixsdk.MXSession r0 = r6.mSession
            boolean r0 = r0.isAlive()
            if (r0 != 0) goto L_0x0010
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "performItemAction : the session is not anymore valid"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r0)
            return
        L_0x0010:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r1 = r6.mRoomMember
            if (r1 != 0) goto L_0x001c
            java.lang.String r1 = r6.mMemberId
            goto L_0x0031
        L_0x001c:
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r1 = r6.mRoomMember
            java.lang.String r1 = r1.displayname
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x002d
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r1 = r6.mRoomMember
            java.lang.String r1 = r1.getUserId()
            goto L_0x0031
        L_0x002d:
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r1 = r6.mRoomMember
            java.lang.String r1 = r1.displayname
        L_0x0031:
            r2 = 2131689597(0x7f0f007d, float:1.9008214E38)
            r3 = 2131689926(0x7f0f01c6, float:1.9008881E38)
            r4 = 0
            r5 = 1
            switch(r7) {
                case 0: goto L_0x01e0;
                case 1: goto L_0x01ca;
                case 2: goto L_0x01ae;
                case 3: goto L_0x0191;
                case 4: goto L_0x0175;
                case 5: goto L_0x0137;
                case 6: goto L_0x00f9;
                case 7: goto L_0x00d7;
                case 8: goto L_0x00c1;
                case 9: goto L_0x00ab;
                case 10: goto L_0x003c;
                case 11: goto L_0x007d;
                case 12: goto L_0x006c;
                case 13: goto L_0x006c;
                case 14: goto L_0x0059;
                case 15: goto L_0x0054;
                default: goto L_0x003c;
            }
        L_0x003c:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## performItemAction(): unknown action type = "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r7 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m217w(r0, r7)
            goto L_0x01fb
        L_0x0054:
            r6.refreshDevicesListView()
            goto L_0x01fb
        L_0x0059:
            android.content.Intent r7 = new android.content.Intent
            r7.<init>()
            java.lang.String r0 = "RESULT_MENTION_ID"
            r7.putExtra(r0, r1)
            r0 = -1
            r6.setResult(r0, r7)
            r6.finish()
            goto L_0x01fb
        L_0x006c:
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "## performItemAction(): Start call"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            r0 = 13
            if (r0 != r7) goto L_0x0078
            r4 = 1
        L_0x0078:
            r6.startCheckCallPermissions(r4)
            goto L_0x01fb
        L_0x007d:
            android.support.v7.app.AlertDialog$Builder r7 = new android.support.v7.app.AlertDialog$Builder
            r7.<init>(r6)
            r0 = 2131689694(0x7f0f00de, float:1.900841E38)
            r7.setTitle(r0)
            r0 = 2131690305(0x7f0f0341, float:1.900965E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r1
            java.lang.String r0 = r6.getString(r0, r5)
            r7.setMessage(r0)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$7 r0 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$7
            r0.<init>()
            r7.setPositiveButton(r3, r0)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$8 r0 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$8
            r0.<init>()
            r7.setNegativeButton(r2, r0)
            r7.show()
            goto L_0x01fb
        L_0x00ab:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            java.lang.String r7 = r6.mMemberId
            r0 = 100
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r1 = r6.mRoomActionsListener
            r6.updateUserPowerLevels(r7, r0, r1)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Make Admin"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x00c1:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            java.lang.String r7 = r6.mMemberId
            r0 = 50
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r1 = r6.mRoomActionsListener
            r6.updateUserPowerLevels(r7, r0, r1)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Make moderator"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x00d7:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.data.RoomState r7 = r7.getLiveState()
            com.opengarden.firechat.matrixsdk.rest.model.PowerLevels r7 = r7.getPowerLevels()
            if (r7 == 0) goto L_0x00e9
            int r4 = r7.users_default
        L_0x00e9:
            java.lang.String r7 = r6.mMemberId
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r0 = r6.mRoomActionsListener
            r6.updateUserPowerLevels(r7, r4, r0)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): default power level"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x00f9:
            android.app.AlertDialog$Builder r7 = new android.app.AlertDialog$Builder
            r7.<init>(r6)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r5 = 2131690030(0x7f0f022e, float:1.9009092E38)
            java.lang.String r5 = r6.getString(r5)
            r1.append(r5)
            java.lang.String r5 = " ?"
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            r7.setMessage(r1)
            android.app.AlertDialog$Builder r1 = r7.setCancelable(r4)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$12 r4 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$12
            r4.<init>(r0)
            android.app.AlertDialog$Builder r0 = r1.setPositiveButton(r3, r4)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$11 r1 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$11
            r1.<init>()
            r0.setNegativeButton(r2, r1)
            android.app.AlertDialog r7 = r7.create()
            r7.show()
            goto L_0x01fb
        L_0x0137:
            android.app.AlertDialog$Builder r7 = new android.app.AlertDialog$Builder
            r7.<init>(r6)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r5 = 2131690021(0x7f0f0225, float:1.9009074E38)
            java.lang.String r5 = r6.getString(r5)
            r1.append(r5)
            java.lang.String r5 = " ?"
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            r7.setMessage(r1)
            android.app.AlertDialog$Builder r1 = r7.setCancelable(r4)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$10 r4 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$10
            r4.<init>(r0)
            android.app.AlertDialog$Builder r0 = r1.setPositiveButton(r3, r4)
            com.opengarden.firechat.activity.VectorMemberDetailsActivity$9 r1 = new com.opengarden.firechat.activity.VectorMemberDetailsActivity$9
            r1.<init>()
            r0.setNegativeButton(r2, r1)
            android.app.AlertDialog r7 = r7.create()
            r7.show()
            goto L_0x01fb
        L_0x0175:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            r6.enableProgressBarView(r5)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r0 = r6.mRoomMember
            java.lang.String r0 = r0.getUserId()
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r1 = r6.mRoomActionsListener
            r7.unban(r0, r1)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Block (unban)"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x0191:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            r6.enableProgressBarView(r5)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r0 = r6.mRoomMember
            java.lang.String r0 = r0.getUserId()
            r1 = 0
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r2 = r6.mRoomActionsListener
            r7.ban(r0, r1, r2)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Block (Ban)"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x01ae:
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            r6.enableProgressBarView(r5)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r0 = r6.mRoomMember
            java.lang.String r0 = r0.getUserId()
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r1 = r6.mRoomActionsListener
            r7.kick(r0, r1)
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Kick"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            goto L_0x01fb
        L_0x01ca:
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Leave the room"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            r6.enableProgressBarView(r5)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r0 = r6.mRoomActionsListener
            r7.leave(r0)
            goto L_0x01fb
        L_0x01e0:
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## performItemAction(): Invite"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r7, r0)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            if (r7 == 0) goto L_0x01fb
            r6.enableProgressBarView(r5)
            com.opengarden.firechat.matrixsdk.data.Room r7 = r6.mRoom
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r0 = r6.mRoomMember
            java.lang.String r0 = r0.getUserId()
            com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback<java.lang.Void> r1 = r6.mRoomActionsListener
            r7.invite(r0, r1)
        L_0x01fb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.VectorMemberDetailsActivity.performItemAction(int):void");
    }

    /* access modifiers changed from: private */
    public void setScreenDevicesListVisibility(int i) {
        this.mDevicesListHeaderView.setVisibility(i);
        this.mDevicesListView.setVisibility(i);
        if (i == 0) {
            this.mExpandableListView.setVisibility(8);
        } else {
            this.mExpandableListView.setVisibility(0);
        }
    }

    private void refreshDevicesListView() {
        if (this.mSession != null && this.mSession.getCrypto() != null) {
            enableProgressBarView(true);
            this.mSession.getCrypto().getDeviceList().downloadKeys(Collections.singletonList(this.mMemberId), true, new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
                private void onError(String str) {
                    Toast.makeText(VectorMemberDetailsActivity.this, str, 1).show();
                    VectorMemberDetailsActivity.this.updateUi();
                }

                public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                    final boolean access$1300 = VectorMemberDetailsActivity.this.populateDevicesListAdapter(mXUsersDevicesMap);
                    VectorMemberDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            VectorMemberDetailsActivity.this.enableProgressBarView(false);
                            if (access$1300) {
                                VectorMemberDetailsActivity.this.setScreenDevicesListVisibility(0);
                            }
                        }
                    });
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
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (16908332 != menuItem.getItemId()) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mDevicesListView.getVisibility() == 0) {
            setScreenDevicesListVisibility(8);
        } else {
            onBackPressed();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean populateDevicesListAdapter(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        if (!(mXUsersDevicesMap == null || this.mDevicesListViewAdapter == null)) {
            this.mDevicesListViewAdapter.clear();
            if (mXUsersDevicesMap.getMap().containsKey(this.mMemberId)) {
                this.mDevicesListViewAdapter.addAll(new ArrayList(new HashMap((Map) mXUsersDevicesMap.getMap().get(this.mMemberId)).values()));
            } else {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## populateDevicesListAdapter(): invalid response - entry for ");
                sb.append(this.mMemberId);
                sb.append(" is missing");
                Log.m217w(str, sb.toString());
            }
        }
        return (this.mDevicesListViewAdapter == null || this.mDevicesListViewAdapter.getCount() == 0) ? false : true;
    }

    private void updateUserPowerLevels(final String str, final int i, final ApiCallback<Void> apiCallback) {
        PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
        if ((powerLevels != null ? powerLevels.getUserPowerLevel(this.mSession.getMyUserId()) : 0) == i) {
            new Builder(this).setMessage(C1299R.string.room_participants_power_level_prompt).setPositiveButton(C1299R.string.yes, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    VectorMemberDetailsActivity.this.mRoom.updateUserPowerLevels(str, i, apiCallback);
                }
            }).setNegativeButton(C1299R.string.f114no, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            this.mRoom.updateUserPowerLevels(str, i, apiCallback);
        }
    }

    private Room searchCallableRoom() {
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "searchCallableRoom : the session is not anymore valid");
            return null;
        }
        for (Room room : this.mSession.getDataHandler().getStore().getRooms()) {
            Collection<RoomMember> members = room.getMembers();
            if (members.size() == 2) {
                for (RoomMember userId : members) {
                    if (userId.getUserId().equals(this.mMemberId) && room.canPerformCall()) {
                        return room;
                    }
                }
                continue;
            }
        }
        return null;
    }

    private ArrayList<Integer> supportedActionsList() {
        int i;
        int i2;
        int i3;
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "supportedActionsList : the session is not anymore valid");
            return arrayList;
        }
        String myUserId = this.mSession.getMyUserId();
        PowerLevels powerLevels = null;
        if (this.mRoom != null) {
            powerLevels = this.mRoom.getLiveState().getPowerLevels();
        }
        this.mMemberAvatarBadgeImageView.setVisibility(8);
        if (powerLevels != null) {
            i3 = powerLevels.getUserPowerLevel(this.mMemberId);
            i2 = powerLevels.getUserPowerLevel(myUserId);
            float f = (float) i3;
            if (f >= 100.0f) {
                this.mMemberAvatarBadgeImageView.setVisibility(0);
                this.mMemberAvatarBadgeImageView.setImageResource(C1299R.C1300drawable.admin_icon);
            } else if (f >= 50.0f) {
                this.mMemberAvatarBadgeImageView.setVisibility(0);
                this.mMemberAvatarBadgeImageView.setImageResource(C1299R.C1300drawable.mod_icon);
            }
            i = 0;
            for (Integer num : powerLevels.users.values()) {
                if (num != null && ((float) num.intValue()) >= 100.0f) {
                    i++;
                }
            }
        } else {
            i3 = 50;
            i2 = 50;
            i = 0;
        }
        if (TextUtils.equals(this.mMemberId, myUserId)) {
            if (this.mRoom != null) {
                arrayList.add(Integer.valueOf(1));
            }
            if (i > 1 && powerLevels != null && i2 >= powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS)) {
                if (i2 >= 100) {
                    arrayList.add(Integer.valueOf(8));
                }
                if (i2 >= 50) {
                    arrayList.add(Integer.valueOf(7));
                }
            }
        } else if (this.mRoomMember != null) {
            if (searchCallableRoom() != null && this.mSession.isVoipCallSupported() && CallsManager.getSharedInstance().getActiveCall() == null) {
                arrayList.add(Integer.valueOf(12));
                arrayList.add(Integer.valueOf(13));
            }
            String str = this.mRoomMember.membership;
            if (powerLevels != null) {
                if (TextUtils.equals(str, "invite") || TextUtils.equals(str, RoomMember.MEMBERSHIP_JOIN)) {
                    if (i2 >= powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS) && i2 > i3) {
                        if (i2 >= 100) {
                            arrayList.add(Integer.valueOf(9));
                        }
                        if (i2 >= 50 && i3 < 50) {
                            arrayList.add(Integer.valueOf(8));
                        }
                        if (i3 >= 8) {
                            arrayList.add(Integer.valueOf(7));
                        }
                    }
                    if (i2 >= powerLevels.kick && i2 > i3) {
                        arrayList.add(Integer.valueOf(2));
                    }
                    if (i2 >= powerLevels.ban && i2 > i3) {
                        arrayList.add(Integer.valueOf(3));
                    }
                    if (TextUtils.equals(str, RoomMember.MEMBERSHIP_JOIN)) {
                        int roomMaxPowerLevel = CommonActivityUtils.getRoomMaxPowerLevel(this.mRoom);
                        if (i2 == roomMaxPowerLevel && i3 != roomMaxPowerLevel) {
                            arrayList.add(Integer.valueOf(9));
                        }
                        if (!this.mSession.isUserIgnored(this.mRoomMember.getUserId())) {
                            arrayList.add(Integer.valueOf(5));
                        } else {
                            arrayList.add(Integer.valueOf(6));
                        }
                    }
                } else if (TextUtils.equals(str, RoomMember.MEMBERSHIP_LEAVE)) {
                    if (i2 >= powerLevels.invite) {
                        arrayList.add(Integer.valueOf(0));
                    }
                    if (i2 >= powerLevels.ban && i2 > i3) {
                        arrayList.add(Integer.valueOf(3));
                    }
                } else if (TextUtils.equals(str, RoomMember.MEMBERSHIP_BAN) && i2 >= powerLevels.ban && i2 > i3) {
                    arrayList.add(Integer.valueOf(4));
                }
            }
            if (!TextUtils.equals(this.mRoomMember.membership, this.mSession.getMyUserId())) {
                arrayList.add(Integer.valueOf(14));
            }
        } else if (this.mUser != null) {
            if (!this.mSession.isUserIgnored(this.mMemberId)) {
                arrayList.add(Integer.valueOf(5));
            } else {
                arrayList.add(Integer.valueOf(6));
            }
        }
        return arrayList;
    }

    private void updateAdapterListViewItems() {
        if (this.mListViewAdapter == null) {
            Log.m217w(LOG_TAG, "## updateListViewItemsContent(): list view adapter not initialized");
            return;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        ArrayList supportedActionsList = supportedActionsList();
        if (supportedActionsList.indexOf(Integer.valueOf(12)) >= 0) {
            arrayList3.add(new AdapterMemberActionItems(C1299R.C1300drawable.voice_call_black, getResources().getString(C1299R.string.start_voice_call), 12));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(13)) >= 0) {
            arrayList3.add(new AdapterMemberActionItems(C1299R.C1300drawable.video_call_black, getResources().getString(C1299R.string.start_video_call), 13));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(0)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_person_add_black, getResources().getString(C1299R.string.room_participants_action_invite), 0));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(1)) >= 0) {
            arrayList.add(new AdapterMemberActionItems(C1299R.C1300drawable.vector_leave_room_black, getResources().getString(C1299R.string.room_participants_action_leave), 1));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(9)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_verified_user_black, getResources().getString(C1299R.string.room_participants_action_set_admin), 9));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(8)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_verified_user_black, getResources().getString(C1299R.string.room_participants_action_set_moderator), 8));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(7)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_verified_user_black, getResources().getString(C1299R.string.room_participants_action_set_default_power_level), 7));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(2)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_remove_circle_outline_red, getResources().getString(C1299R.string.room_participants_action_remove), 2));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(3)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_block_black, getResources().getString(C1299R.string.room_participants_action_ban), 3));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(4)) >= 0) {
            arrayList2.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_block_black, getResources().getString(C1299R.string.room_participants_action_unban), 4));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(5)) >= 0) {
            arrayList.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_person_outline_black, getResources().getString(C1299R.string.room_participants_action_ignore), 5));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(6)) >= 0) {
            arrayList.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_person_black, getResources().getString(C1299R.string.room_participants_action_unignore), 6));
        }
        if (supportedActionsList.indexOf(Integer.valueOf(14)) >= 0) {
            arrayList.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_comment_black, getResources().getString(C1299R.string.room_participants_action_mention), 14));
        }
        this.mListViewAdapter.setUncategorizedActionsList(arrayList);
        this.mListViewAdapter.setAdminActionsList(arrayList2);
        this.mListViewAdapter.setCallActionsList(arrayList3);
        if (this.mUser != null && MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(this.mMemberId).matches()) {
            arrayList5.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_devices_info, getResources().getString(C1299R.string.room_participants_action_devices_list), 15));
            this.mListViewAdapter.setDevicesActionsList(arrayList5);
        }
        for (String room : this.mSession.getDataHandler().getDirectChatRoomIdsList(this.mMemberId)) {
            Room room2 = this.mSession.getDataHandler().getRoom(room);
            if (room2 != null) {
                arrayList4.add(new AdapterMemberActionItems(room2));
            }
        }
        arrayList4.add(new AdapterMemberActionItems(C1299R.C1300drawable.ic_add_black, getResources().getString(C1299R.string.start_new_chat), 11));
        this.mListViewAdapter.setDirectCallsActionsList(arrayList4);
        this.mListViewAdapter.notifyDataSetChanged();
        this.mExpandableListView.post(new Runnable() {
            public void run() {
                int groupCount = VectorMemberDetailsActivity.this.mListViewAdapter.getGroupCount();
                for (int i = 0; i < groupCount; i++) {
                    VectorMemberDetailsActivity.this.mExpandableListView.expandGroup(i);
                }
            }
        });
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            if (!initContextStateValues()) {
                Log.m211e(LOG_TAG, "## onCreate(): Parameters init failure");
                finish();
            } else {
                checkRoomMemberStatus();
                setSupportActionBar((Toolbar) findViewById(C1299R.C1301id.member_details_toolbar));
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                this.mMemberAvatarImageView = (ImageView) findViewById(C1299R.C1301id.avatar_img);
                this.mMemberAvatarBadgeImageView = (ImageView) findViewById(C1299R.C1301id.member_avatar_badge);
                this.mFullMemberAvatarImageView = (ImageView) findViewById(C1299R.C1301id.member_details_fullscreen_avatar_image_view);
                this.mFullMemberAvatarLayout = findViewById(C1299R.C1301id.member_details_fullscreen_avatar_layout);
                this.mMemberNameTextView = (TextView) findViewById(C1299R.C1301id.member_details_name);
                this.mPresenceTextView = (TextView) findViewById(C1299R.C1301id.member_details_presence);
                setWaitingView(findViewById(C1299R.C1301id.member_details_list_view_progress_bar));
                this.mDevicesListView = (ListView) findViewById(C1299R.C1301id.member_details_devices_list_view);
                this.mDevicesListViewAdapter = new VectorMemberDetailsDevicesAdapter(this, C1299R.layout.adapter_item_member_details_devices, this.mSession);
                this.mDevicesListViewAdapter.setDevicesAdapterListener(this);
                this.mDevicesListView.setAdapter(this.mDevicesListViewAdapter);
                this.mDevicesListHeaderView = findViewById(C1299R.C1301id.devices_header_view);
                TextView textView = (TextView) this.mDevicesListHeaderView.findViewById(C1299R.C1301id.heading);
                if (textView != null) {
                    textView.setText(C1299R.string.room_participants_header_devices);
                }
                this.mListViewAdapter = new VectorMemberDetailsAdapter(this, this.mSession, C1299R.layout.vector_adapter_member_details_items, C1299R.layout.adapter_item_vector_recent_header);
                this.mListViewAdapter.setActionListener(this);
                this.mExpandableListView = (ExpandableListView) findViewById(C1299R.C1301id.member_details_actions_list_view);
                this.mExpandableListView.setGroupIndicator(null);
                this.mExpandableListView.setAdapter(this.mListViewAdapter);
                this.mExpandableListView.setOnGroupClickListener(new OnGroupClickListener() {
                    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long j) {
                        return true;
                    }
                });
                this.mMemberNameTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        User user = VectorMemberDetailsActivity.this.mSession.getDataHandler().getUser(VectorMemberDetailsActivity.this.mMemberId);
                        if (!TextUtils.equals(VectorMemberDetailsActivity.this.mMemberNameTextView.getText(), VectorMemberDetailsActivity.this.mMemberId)) {
                            VectorMemberDetailsActivity.this.mMemberNameTextView.setText(VectorMemberDetailsActivity.this.mMemberId);
                        } else if (user != null && !TextUtils.isEmpty(user.displayname)) {
                            VectorMemberDetailsActivity.this.mMemberNameTextView.setText(user.displayname);
                        }
                    }
                });
                this.mMemberNameTextView.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        VectorUtils.copyToClipboard(VectorMemberDetailsActivity.this, VectorMemberDetailsActivity.this.mMemberNameTextView.getText());
                        return true;
                    }
                });
                this.mMemberAvatarImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        VectorMemberDetailsActivity.this.displayFullScreenAvatar();
                    }
                });
                this.mFullMemberAvatarImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        VectorMemberDetailsActivity.this.hideFullScreenAvatar();
                    }
                });
                this.mFullMemberAvatarLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        VectorMemberDetailsActivity.this.hideFullScreenAvatar();
                    }
                });
                updateUi();
                if (!isFirstCreation() && getSavedInstanceState().getBoolean(AVATAR_FULLSCREEN_MODE, false)) {
                    displayFullScreenAvatar();
                }
            }
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (4 == i) {
            if (this.mFullMemberAvatarLayout.getVisibility() == 0) {
                hideFullScreenAvatar();
                return true;
            } else if (this.mDevicesListView.getVisibility() == 0) {
                setScreenDevicesListVisibility(8);
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(AVATAR_FULLSCREEN_MODE, this.mFullMemberAvatarLayout.getVisibility() == 0);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (bundle.getBoolean(AVATAR_FULLSCREEN_MODE, false)) {
            displayFullScreenAvatar();
        }
    }

    /* access modifiers changed from: private */
    public void hideFullScreenAvatar() {
        this.mFullMemberAvatarLayout.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void displayFullScreenAvatar() {
        String str;
        String str2 = this.mMemberId;
        if (this.mRoomMember != null) {
            str = this.mRoomMember.getAvatarUrl();
            if (TextUtils.isEmpty(str)) {
                str2 = this.mRoomMember.getUserId();
            }
        } else {
            str = null;
        }
        if (TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && this.mUser != null) {
            str = this.mUser.getAvatarUrl();
        }
        String str3 = str;
        if (!TextUtils.isEmpty(str3)) {
            this.mFullMemberAvatarLayout.setVisibility(0);
            this.mSession.getMediasCache().loadBitmap(this.mSession.getHomeServerConfig(), this.mFullMemberAvatarImageView, str3, 0, 0, (String) null, (EncryptedFileInfo) null);
        }
    }

    private boolean initContextStateValues() {
        IMXStore iMXStore;
        Intent intent = getIntent();
        boolean z = false;
        if (intent != null) {
            String stringExtra = intent.getStringExtra(EXTRA_MEMBER_ID);
            this.mMemberId = stringExtra;
            if (stringExtra == null) {
                Log.m211e(LOG_TAG, "member ID missing in extra");
                return false;
            }
            MXSession session = getSession(intent);
            this.mSession = session;
            if (session == null) {
                Log.m211e(LOG_TAG, "Invalid session");
                return false;
            }
            int intExtra = intent.getIntExtra(EXTRA_STORE_ID, -1);
            if (intExtra >= 0) {
                iMXStore = Matrix.getInstance(this).getTmpStore(intExtra);
            } else {
                iMXStore = this.mSession.getDataHandler().getStore();
                if (refreshUser()) {
                    intent.removeExtra("EXTRA_ROOM_ID");
                }
            }
            String stringExtra2 = intent.getStringExtra("EXTRA_ROOM_ID");
            if (stringExtra2 != null) {
                Room room = iMXStore.getRoom(stringExtra2);
                this.mRoom = room;
                if (room == null) {
                    Log.m211e(LOG_TAG, "The room is not found");
                }
            }
            Log.m209d(LOG_TAG, "Parameters init succeed");
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public boolean checkRoomMemberStatus() {
        this.mRoomMember = null;
        if (this.mRoom != null) {
            Iterator it = this.mRoom.getMembers().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                RoomMember roomMember = (RoomMember) it.next();
                if (roomMember.getUserId().equals(this.mMemberId)) {
                    this.mRoomMember = roomMember;
                    break;
                }
            }
        }
        return this.mRoom == null || this.mRoomMember != null;
    }

    private boolean refreshUser() {
        this.mUser = this.mSession.getDataHandler().getStore().getUser(this.mMemberId);
        if (this.mUser != null) {
            return false;
        }
        this.mUser = new User();
        this.mUser.user_id = this.mMemberId;
        this.mUser.displayname = getIntent().getStringExtra(EXTRA_MEMBER_DISPLAY_NAME);
        if (TextUtils.isEmpty(this.mUser.displayname)) {
            this.mUser.displayname = this.mMemberId;
        }
        this.mUser.avatar_url = getIntent().getStringExtra(EXTRA_MEMBER_AVATAR_URL);
        return true;
    }

    /* access modifiers changed from: private */
    public void updateUi() {
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "updateUi : the session is not anymore valid");
            return;
        }
        if (this.mMemberNameTextView != null) {
            if (this.mRoomMember == null || TextUtils.isEmpty(this.mRoomMember.displayname)) {
                refreshUser();
                this.mMemberNameTextView.setText(this.mUser.displayname);
            } else {
                this.mMemberNameTextView.setText(this.mRoomMember.displayname);
            }
            setTitle("");
        }
        enableProgressBarView(false);
        updateMemberAvatarUi();
        updatePresenceInfoUi();
        updateAdapterListViewItems();
        if (this.mListViewAdapter != null) {
            this.mListViewAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void updatePresenceInfoUi() {
        if (this.mPresenceTextView != null) {
            this.mPresenceTextView.setText(VectorUtils.getUserOnlineStatus(this, this.mSession, this.mMemberId, new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    VectorMemberDetailsActivity.this.mPresenceTextView.setText(VectorUtils.getUserOnlineStatus(VectorMemberDetailsActivity.this, VectorMemberDetailsActivity.this.mSession, VectorMemberDetailsActivity.this.mMemberId, null));
                }
            }));
        }
    }

    /* access modifiers changed from: private */
    public void updateMemberAvatarUi() {
        if (this.mMemberAvatarImageView == null) {
            return;
        }
        if (this.mRoomMember != null) {
            String str = this.mRoomMember.displayname;
            String avatarUrl = this.mRoomMember.getAvatarUrl();
            if ((TextUtils.isEmpty(avatarUrl) || TextUtils.isEmpty(str)) && this.mUser != null) {
                if (TextUtils.isEmpty(avatarUrl)) {
                    avatarUrl = this.mUser.avatar_url;
                }
                if (TextUtils.isEmpty(str)) {
                    str = this.mUser.displayname;
                }
            }
            String str2 = avatarUrl;
            VectorUtils.loadUserAvatar(this, this.mSession, this.mMemberAvatarImageView, str2, this.mRoomMember.getUserId(), str);
        } else if (this.mUser != null) {
            VectorUtils.loadUserAvatar(this, this.mSession, this.mMemberAvatarImageView, this.mUser);
        } else {
            VectorUtils.loadUserAvatar(this, this.mSession, this.mMemberAvatarImageView, null, this.mMemberId, this.mMemberId);
        }
    }

    /* access modifiers changed from: private */
    public void enableProgressBarView(boolean z) {
        if (getWaitingView() != null) {
            getWaitingView().setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mSession != null) {
            if (this.mRoom != null) {
                this.mRoom.removeEventListener(this.mLiveEventsListener);
            }
            this.mSession.getDataHandler().removeListener(this.mPresenceEventsListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mSession != null) {
            if (this.mRoom != null) {
                this.mRoom.addEventListener(this.mLiveEventsListener);
            }
            this.mSession.getDataHandler().addListener(this.mPresenceEventsListener);
            updateAdapterListViewItems();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mRoom != null) {
            this.mRoom.removeEventListener(this.mLiveEventsListener);
        }
        if (this.mSession != null) {
            this.mSession.getDataHandler().removeListener(this.mPresenceEventsListener);
        }
    }

    public void OnVerifyDeviceClick(MXDeviceInfo mXDeviceInfo) {
        if (mXDeviceInfo.mVerified != 1) {
            this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, this.mMemberId, this.mDevicesVerificationCallback);
        } else {
            this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, this.mMemberId, this.mDevicesVerificationCallback);
        }
    }

    public void OnBlockDeviceClick(MXDeviceInfo mXDeviceInfo) {
        if (mXDeviceInfo.mVerified == 2) {
            this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, mXDeviceInfo.userId, this.mDevicesVerificationCallback);
        } else {
            this.mSession.getCrypto().setDeviceVerification(2, mXDeviceInfo.deviceId, mXDeviceInfo.userId, this.mDevicesVerificationCallback);
        }
        this.mDevicesListViewAdapter.notifyDataSetChanged();
    }
}
