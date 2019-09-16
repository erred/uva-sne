package com.opengarden.firechat.receiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.p000v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.LoginActivity;
import com.opengarden.firechat.activity.VectorGroupDetailsActivity;
import com.opengarden.firechat.activity.VectorHomeActivity;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint({"LongLogTag"})
public class VectorUniversalLinkReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION_UNIVERSAL_LINK = "im.vector.receiver.UNIVERSAL_LINK";
    public static final String BROADCAST_ACTION_UNIVERSAL_LINK_RESUME = "im.vector.receiver.UNIVERSAL_LINK_RESUME";
    public static final String EXTRA_UNIVERSAL_LINK_SENDER_ID = "EXTRA_UNIVERSAL_LINK_SENDER_ID";
    public static final String EXTRA_UNIVERSAL_LINK_URI = "EXTRA_UNIVERSAL_LINK_URI";
    public static final String HOME_SENDER_ID = VectorHomeActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorUniversalLinkReceiver";
    private static final String SUPPORTED_PATH_APP = "/app/";
    private static final String SUPPORTED_PATH_BETA = "/beta/";
    private static final String SUPPORTED_PATH_DEVELOP = "/develop/";
    private static final String SUPPORTED_PATH_STAGING = "/staging/";
    private static final String ULINK_EVENT_ID_KEY = "ULINK_EVENT_ID_KEY";
    public static final String ULINK_GROUP_ID_KEY = "ULINK_GROUP_ID_KEY";
    public static final String ULINK_MATRIX_USER_ID_KEY = "ULINK_MATRIX_USER_ID_KEY";
    public static final String ULINK_ROOM_ID_OR_ALIAS_KEY = "ULINK_ROOM_ID_OR_ALIAS_KEY";
    private static final List<String> mSupportedVectorLinkPaths = Arrays.asList(new String[]{SUPPORTED_PATH_BETA, SUPPORTED_PATH_DEVELOP, SUPPORTED_PATH_APP, SUPPORTED_PATH_STAGING});
    /* access modifiers changed from: private */
    public HashMap<String, String> mParameters;
    private MXSession mSession;

    public void onReceive(Context context, Intent intent) {
        Uri uri;
        Log.m209d(LOG_TAG, "## onReceive() IN");
        this.mSession = Matrix.getInstance(context).getDefaultSession();
        if (this.mSession == null) {
            Log.m211e(LOG_TAG, "## onReceive() Warning - Unable to proceed URL link: Session is null");
            Intent intent2 = new Intent(context, LoginActivity.class);
            intent2.putExtra(EXTRA_UNIVERSAL_LINK_URI, intent.getData());
            intent2.addFlags(335577088);
            context.startActivity(intent2);
            return;
        }
        if (intent != null) {
            String action = intent.getAction();
            String dataString = intent.getDataString();
            boolean isAlive = this.mSession.isAlive();
            boolean isInitialSyncComplete = this.mSession.getDataHandler().isInitialSyncComplete();
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onReceive() uri getDataString=");
            sb.append(dataString);
            sb.append("isSessionActive=");
            sb.append(isAlive);
            sb.append(" isLoginStepDone=");
            sb.append(isInitialSyncComplete);
            Log.m209d(str, sb.toString());
            if (TextUtils.equals(action, BROADCAST_ACTION_UNIVERSAL_LINK)) {
                Log.m209d(LOG_TAG, "## onReceive() action = BROADCAST_ACTION_UNIVERSAL_LINK");
                uri = intent.getData();
            } else if (TextUtils.equals(action, BROADCAST_ACTION_UNIVERSAL_LINK_RESUME)) {
                Log.m209d(LOG_TAG, "## onReceive() action = BROADCAST_ACTION_UNIVERSAL_LINK_RESUME");
                uri = (Uri) intent.getParcelableExtra(EXTRA_UNIVERSAL_LINK_URI);
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onReceive() Unknown action received (");
                sb2.append(action);
                sb2.append(") - unable to proceed URL link");
                Log.m211e(str2, sb2.toString());
                return;
            }
            if (uri != null) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## onCreate() intentUri - host=");
                sb3.append(uri.getHost());
                sb3.append(" path=");
                sb3.append(uri.getPath());
                sb3.append(" queryParams=");
                sb3.append(uri.getQuery());
                Log.m209d(str3, sb3.toString());
                HashMap<String, String> parseUniversalLink = parseUniversalLink(uri);
                if (parseUniversalLink != null) {
                    if (!isAlive) {
                        Log.m217w(LOG_TAG, "## onReceive() Warning: Session is not alive");
                    }
                    if (!isInitialSyncComplete) {
                        Log.m217w(LOG_TAG, "## onReceive() Warning: Session is not complete - start Login Activity");
                        Intent intent3 = new Intent(context, LoginActivity.class);
                        intent3.putExtra(EXTRA_UNIVERSAL_LINK_URI, intent.getData());
                        intent3.setFlags(ErrorDialogData.BINDER_CRASH);
                        context.startActivity(intent3);
                    } else {
                        this.mParameters = parseUniversalLink;
                        if (this.mParameters.containsKey(ULINK_ROOM_ID_OR_ALIAS_KEY)) {
                            manageRoomOnActivity(context);
                        } else if (this.mParameters.containsKey(ULINK_MATRIX_USER_ID_KEY)) {
                            manageMemberDetailsActivity(context);
                        } else if (this.mParameters.containsKey(ULINK_GROUP_ID_KEY)) {
                            manageGroupDetailsActivity(context);
                        } else {
                            Log.m211e(LOG_TAG, "## onReceive() : nothing to do");
                        }
                    }
                } else {
                    String str4 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## onReceive() Path not supported: ");
                    sb4.append(uri.getPath());
                    Log.m211e(str4, sb4.toString());
                }
            }
        }
    }

    private void manageMemberDetailsActivity(Context context) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## manageMemberDetailsActivity() : open ");
        sb.append((String) this.mParameters.get(ULINK_MATRIX_USER_ID_KEY));
        sb.append(" page");
        Log.m209d(str, sb.toString());
        Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = new Intent(currentActivity, VectorMemberDetailsActivity.class);
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, (String) this.mParameters.get(ULINK_MATRIX_USER_ID_KEY));
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            currentActivity.startActivity(intent);
            return;
        }
        Intent intent2 = new Intent(context, VectorHomeActivity.class);
        intent2.setFlags(872415232);
        intent2.putExtra(VectorHomeActivity.EXTRA_MEMBER_ID, (String) this.mParameters.get(ULINK_MATRIX_USER_ID_KEY));
        context.startActivity(intent2);
    }

    /* access modifiers changed from: private */
    public void manageRoomOnActivity(final Context context) {
        Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    VectorUniversalLinkReceiver.this.manageRoom(context);
                }
            });
            return;
        }
        Intent intent = new Intent(context, VectorHomeActivity.class);
        intent.setFlags(872415232);
        intent.putExtra(VectorHomeActivity.EXTRA_WAITING_VIEW_STATUS, true);
        context.startActivity(intent);
        try {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    timer.cancel();
                    VectorUniversalLinkReceiver.this.manageRoomOnActivity(context);
                }
            }, 200);
        } catch (Throwable th) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## manageRoomOnActivity timer creation failed ");
            sb.append(th.getMessage());
            Log.m211e(str, sb.toString());
            manageRoomOnActivity(context);
        }
    }

    private void manageGroupDetailsActivity(Context context) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## manageMemberDetailsActivity() : open the group");
        sb.append((String) this.mParameters.get(ULINK_GROUP_ID_KEY));
        Log.m209d(str, sb.toString());
        Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity != null) {
            Intent intent = new Intent(currentActivity, VectorGroupDetailsActivity.class);
            intent.putExtra(VectorGroupDetailsActivity.EXTRA_GROUP_ID, (String) this.mParameters.get(ULINK_GROUP_ID_KEY));
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            currentActivity.startActivity(intent);
            return;
        }
        Intent intent2 = new Intent(context, VectorHomeActivity.class);
        intent2.setFlags(872415232);
        intent2.putExtra(VectorHomeActivity.EXTRA_GROUP_ID, (String) this.mParameters.get(ULINK_GROUP_ID_KEY));
        context.startActivity(intent2);
    }

    /* access modifiers changed from: private */
    public void manageRoom(Context context) {
        manageRoom(context, null);
    }

    /* access modifiers changed from: private */
    public void manageRoom(final Context context, String str) {
        final String str2 = (String) this.mParameters.get(ULINK_ROOM_ID_OR_ALIAS_KEY);
        Log.m209d(LOG_TAG, "manageRoom roomIdOrAlias");
        if (!TextUtils.isEmpty(str2)) {
            if (str2.startsWith("!")) {
                RoomPreviewData roomPreviewData = new RoomPreviewData(this.mSession, str2, (String) this.mParameters.get(ULINK_EVENT_ID_KEY), str, this.mParameters);
                Room room = this.mSession.getDataHandler().getRoom(str2, false);
                if (room == null || room.isInvited()) {
                    CommonActivityUtils.previewRoom(VectorApp.getCurrentActivity(), this.mSession, str2, roomPreviewData, null);
                } else {
                    openRoomActivity(context);
                }
            } else {
                Log.m209d(LOG_TAG, "manageRoom : it is a room Alias");
                Intent intent = new Intent(context, VectorHomeActivity.class);
                intent.setFlags(872415232);
                intent.putExtra(VectorHomeActivity.EXTRA_WAITING_VIEW_STATUS, true);
                context.startActivity(intent);
                this.mSession.getDataHandler().roomIdByAlias(str2, new ApiCallback<String>() {
                    public void onSuccess(String str) {
                        String access$200 = VectorUniversalLinkReceiver.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("manageRoom : retrieve the room ID ");
                        sb.append(str);
                        Log.m209d(access$200, sb.toString());
                        if (!TextUtils.isEmpty(str)) {
                            VectorUniversalLinkReceiver.this.mParameters.put(VectorUniversalLinkReceiver.ULINK_ROOM_ID_OR_ALIAS_KEY, str);
                            VectorUniversalLinkReceiver.this.manageRoom(context, str2);
                        }
                    }

                    private void onError(String str) {
                        CommonActivityUtils.displayToast(context, str);
                        VectorUniversalLinkReceiver.this.stopHomeActivitySpinner(context);
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
    }

    private void openRoomActivity(Context context) {
        HashMap hashMap = new HashMap();
        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
        hashMap.put("EXTRA_ROOM_ID", this.mParameters.get(ULINK_ROOM_ID_OR_ALIAS_KEY));
        if (this.mParameters.containsKey(ULINK_EVENT_ID_KEY)) {
            hashMap.put(VectorRoomActivity.EXTRA_EVENT_ID, this.mParameters.get(ULINK_EVENT_ID_KEY));
        }
        Intent intent = new Intent(context, VectorHomeActivity.class);
        intent.setFlags(872415232);
        intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, hashMap);
        context.startActivity(intent);
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0088 A[Catch:{ Exception -> 0x019a }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0192 A[SYNTHETIC, Splitter:B:77:0x0192] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.HashMap<java.lang.String, java.lang.String> parseUniversalLink(android.net.Uri r7) {
        /*
            r0 = 1
            r1 = 0
            if (r7 == 0) goto L_0x019d
            java.lang.String r2 = r7.getPath()     // Catch:{ Exception -> 0x019a }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x019a }
            if (r2 == 0) goto L_0x0010
            goto L_0x019d
        L_0x0010:
            java.lang.String r2 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            java.lang.String r3 = "vector.im"
            boolean r2 = android.text.TextUtils.equals(r2, r3)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x004f
            java.lang.String r2 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            java.lang.String r3 = "riot.im"
            boolean r2 = android.text.TextUtils.equals(r2, r3)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x004f
            java.lang.String r2 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            java.lang.String r3 = "matrix.to"
            boolean r2 = android.text.TextUtils.equals(r2, r3)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x004f
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x019a }
            r3.<init>()     // Catch:{ Exception -> 0x019a }
            java.lang.String r4 = "## parseUniversalLink : unsupported host "
            r3.append(r4)     // Catch:{ Exception -> 0x019a }
            java.lang.String r7 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            r3.append(r7)     // Catch:{ Exception -> 0x019a }
            java.lang.String r7 = r3.toString()     // Catch:{ Exception -> 0x019a }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r7)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x004f:
            java.lang.String r2 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            java.lang.String r3 = "vector.im"
            boolean r2 = android.text.TextUtils.equals(r2, r3)     // Catch:{ Exception -> 0x019a }
            r3 = 0
            if (r2 != 0) goto L_0x006b
            java.lang.String r2 = r7.getHost()     // Catch:{ Exception -> 0x019a }
            java.lang.String r4 = "riot.im"
            boolean r2 = android.text.TextUtils.equals(r2, r4)     // Catch:{ Exception -> 0x019a }
            if (r2 == 0) goto L_0x0069
            goto L_0x006b
        L_0x0069:
            r2 = 0
            goto L_0x006c
        L_0x006b:
            r2 = 1
        L_0x006c:
            if (r2 == 0) goto L_0x0082
            java.util.List<java.lang.String> r4 = mSupportedVectorLinkPaths     // Catch:{ Exception -> 0x019a }
            java.lang.String r5 = r7.getPath()     // Catch:{ Exception -> 0x019a }
            boolean r4 = r4.contains(r5)     // Catch:{ Exception -> 0x019a }
            if (r4 != 0) goto L_0x0082
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.String r2 = "## parseUniversalLink : not supported"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r2)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x0082:
            java.lang.String r4 = r7.getFragment()     // Catch:{ Exception -> 0x019a }
            if (r4 == 0) goto L_0x0192
            java.lang.String r4 = r4.substring(r0)     // Catch:{ Exception -> 0x019a }
            java.lang.String r5 = "/"
            r6 = 3
            java.lang.String[] r4 = r4.split(r5, r6)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x00b0
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x019a }
            java.util.List r4 = java.util.Arrays.asList(r4)     // Catch:{ Exception -> 0x019a }
            r2.<init>(r4)     // Catch:{ Exception -> 0x019a }
            java.lang.String r4 = "room"
            r2.add(r3, r4)     // Catch:{ Exception -> 0x019a }
            int r4 = r2.size()     // Catch:{ Exception -> 0x019a }
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ Exception -> 0x019a }
            java.lang.Object[] r2 = r2.toArray(r4)     // Catch:{ Exception -> 0x019a }
            r4 = r2
            java.lang.String[] r4 = (java.lang.String[]) r4     // Catch:{ Exception -> 0x019a }
        L_0x00b0:
            int r2 = r4.length     // Catch:{ Exception -> 0x019a }
            r5 = 2
            if (r2 >= r5) goto L_0x00bc
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.String r2 = "## parseUniversalLink : too short"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r2)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x00bc:
            r2 = r4[r3]     // Catch:{ Exception -> 0x019a }
            java.lang.String r6 = "room"
            boolean r2 = android.text.TextUtils.equals(r2, r6)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x00e9
            r2 = r4[r3]     // Catch:{ Exception -> 0x019a }
            java.lang.String r6 = "user"
            boolean r2 = android.text.TextUtils.equals(r2, r6)     // Catch:{ Exception -> 0x019a }
            if (r2 != 0) goto L_0x00e9
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x019a }
            r2.<init>()     // Catch:{ Exception -> 0x019a }
            java.lang.String r5 = "## parseUniversalLink : not supported "
            r2.append(r5)     // Catch:{ Exception -> 0x019a }
            r3 = r4[r3]     // Catch:{ Exception -> 0x019a }
            r2.append(r3)     // Catch:{ Exception -> 0x019a }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x019a }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r2)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x00e9:
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x019a }
            r2.<init>()     // Catch:{ Exception -> 0x019a }
            r3 = r4[r0]     // Catch:{ Exception -> 0x0190 }
            boolean r6 = com.opengarden.firechat.matrixsdk.MXSession.isUserId(r3)     // Catch:{ Exception -> 0x0190 }
            if (r6 == 0) goto L_0x0107
            int r6 = r4.length     // Catch:{ Exception -> 0x0190 }
            if (r6 <= r5) goto L_0x0101
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x0190 }
            java.lang.String r3 = "## parseUniversalLink : universal link to member id is too long"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r3)     // Catch:{ Exception -> 0x0190 }
            return r1
        L_0x0101:
            java.lang.String r6 = "ULINK_MATRIX_USER_ID_KEY"
            r2.put(r6, r3)     // Catch:{ Exception -> 0x0190 }
            goto L_0x0125
        L_0x0107:
            boolean r6 = com.opengarden.firechat.matrixsdk.MXSession.isRoomAlias(r3)     // Catch:{ Exception -> 0x0190 }
            if (r6 != 0) goto L_0x0120
            boolean r6 = com.opengarden.firechat.matrixsdk.MXSession.isRoomId(r3)     // Catch:{ Exception -> 0x0190 }
            if (r6 == 0) goto L_0x0114
            goto L_0x0120
        L_0x0114:
            boolean r6 = com.opengarden.firechat.matrixsdk.MXSession.isGroupId(r3)     // Catch:{ Exception -> 0x0190 }
            if (r6 == 0) goto L_0x0125
            java.lang.String r6 = "ULINK_GROUP_ID_KEY"
            r2.put(r6, r3)     // Catch:{ Exception -> 0x0190 }
            goto L_0x0125
        L_0x0120:
            java.lang.String r6 = "ULINK_ROOM_ID_OR_ALIAS_KEY"
            r2.put(r6, r3)     // Catch:{ Exception -> 0x0190 }
        L_0x0125:
            int r3 = r4.length     // Catch:{ Exception -> 0x0190 }
            if (r3 <= r5) goto L_0x01bf
            r3 = r4[r5]     // Catch:{ Exception -> 0x0190 }
            boolean r3 = com.opengarden.firechat.matrixsdk.MXSession.isMessageId(r3)     // Catch:{ Exception -> 0x0190 }
            if (r3 == 0) goto L_0x0139
            java.lang.String r7 = "ULINK_EVENT_ID_KEY"
            r3 = r4[r5]     // Catch:{ Exception -> 0x0190 }
            r2.put(r7, r3)     // Catch:{ Exception -> 0x0190 }
            goto L_0x01bf
        L_0x0139:
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0190 }
            java.lang.String r3 = "#/room/"
            java.lang.String r4 = "room/"
            java.lang.String r7 = r7.replace(r3, r4)     // Catch:{ Exception -> 0x0190 }
            android.net.Uri r7 = android.net.Uri.parse(r7)     // Catch:{ Exception -> 0x0190 }
            java.lang.String r3 = "ULINK_ROOM_ID_OR_ALIAS_KEY"
            java.lang.String r4 = r7.getLastPathSegment()     // Catch:{ Exception -> 0x0190 }
            r2.put(r3, r4)     // Catch:{ Exception -> 0x0190 }
            java.util.Set r3 = r7.getQueryParameterNames()     // Catch:{ Exception -> 0x0190 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ Exception -> 0x0190 }
        L_0x015a:
            boolean r4 = r3.hasNext()     // Catch:{ Exception -> 0x0190 }
            if (r4 == 0) goto L_0x01bf
            java.lang.Object r4 = r3.next()     // Catch:{ Exception -> 0x0190 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0190 }
            java.lang.String r5 = r7.getQueryParameter(r4)     // Catch:{ Exception -> 0x0190 }
            java.lang.String r6 = "UTF-8"
            java.lang.String r5 = java.net.URLDecoder.decode(r5, r6)     // Catch:{ Exception -> 0x0174 }
            r2.put(r4, r5)     // Catch:{ Exception -> 0x0190 }
            goto L_0x015a
        L_0x0174:
            r7 = move-exception
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x0190 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0190 }
            r4.<init>()     // Catch:{ Exception -> 0x0190 }
            java.lang.String r5 = "## parseUniversalLink : URLDecoder.decode "
            r4.append(r5)     // Catch:{ Exception -> 0x0190 }
            java.lang.String r7 = r7.getMessage()     // Catch:{ Exception -> 0x0190 }
            r4.append(r7)     // Catch:{ Exception -> 0x0190 }
            java.lang.String r7 = r4.toString()     // Catch:{ Exception -> 0x0190 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r7)     // Catch:{ Exception -> 0x0190 }
            return r1
        L_0x0190:
            r7 = move-exception
            goto L_0x01a5
        L_0x0192:
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.String r2 = "## parseUniversalLink : cannot extract path"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r2)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x019a:
            r7 = move-exception
            r2 = r1
            goto L_0x01a5
        L_0x019d:
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x019a }
            java.lang.String r2 = "## parseUniversalLink : null"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r2)     // Catch:{ Exception -> 0x019a }
            return r1
        L_0x01a5:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## parseUniversalLink : crashes "
            r4.append(r5)
            java.lang.String r7 = r7.getLocalizedMessage()
            r4.append(r7)
            java.lang.String r7 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r7)
        L_0x01bf:
            if (r2 == 0) goto L_0x01cf
            int r7 = r2.size()
            if (r7 >= r0) goto L_0x01cf
            java.lang.String r7 = LOG_TAG
            java.lang.String r0 = "## parseUniversalLink : empty dictionary"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r0)
            return r1
        L_0x01cf:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.receiver.VectorUniversalLinkReceiver.parseUniversalLink(android.net.Uri):java.util.HashMap");
    }

    /* access modifiers changed from: private */
    public void stopHomeActivitySpinner(Context context) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(VectorHomeActivity.BROADCAST_ACTION_STOP_WAITING_VIEW));
    }
}
