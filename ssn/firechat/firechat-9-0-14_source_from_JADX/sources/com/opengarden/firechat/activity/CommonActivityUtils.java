package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.p000v4.app.ActivityCompat;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.NotificationCompat;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p003v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.MyPresenceManager;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.adapters.VectorRoomsSelectionAdapter;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.PIDsRetriever;
import com.opengarden.firechat.fragments.VectorUnknownDevicesFragment;
import com.opengarden.firechat.fragments.VectorUnknownDevicesFragment.IUnknownDevicesSendAnywayListener;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager.ThirdPartyRegistrationListener;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.services.EventStreamService.StreamAction;
import com.opengarden.firechat.util.MatrixSdkExtensionsKt;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import p010me.leolin.shortcutbadger.ShortcutBadger;

public class CommonActivityUtils {
    public static final boolean GROUP_IS_COLLAPSED = false;
    public static final boolean GROUP_IS_EXPANDED = true;
    private static final String HTTPS_SCHEME = "https://";
    private static final String HTTP_SCHEME = "http://";
    public static final String KEY_GROUPS_EXPANDED_STATE = "KEY_GROUPS_EXPANDED_STATE";
    public static final String KEY_SEARCH_PATTERN = "KEY_SEARCH_PATTERN";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "CommonActivityUtils";
    private static final String LOW_MEMORY_LOG_TAG = "Memory usage";
    private static final boolean PERMISSIONS_DENIED = false;
    private static final boolean PERMISSIONS_GRANTED = true;
    private static final int PERMISSION_BYPASSED = 0;
    public static final int PERMISSION_CAMERA = 1;
    private static final int PERMISSION_LOCATION = 1000;
    private static final int PERMISSION_READ_CONTACTS = 8;
    private static final int PERMISSION_RECORD_AUDIO = 4;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    public static final int REQUEST_CODE_PERMISSION_AUDIO_IP_CALL = 4;
    private static final int REQUEST_CODE_PERMISSION_BY_PASS = 0;
    public static final int REQUEST_CODE_PERMISSION_HOME_ACTIVITY = 1002;
    public static final int REQUEST_CODE_PERMISSION_LOCATION = 1000;
    public static final int REQUEST_CODE_PERMISSION_MEMBERS_SEARCH = 8;
    public static final int REQUEST_CODE_PERMISSION_MEMBER_DETAILS = 8;
    public static final int REQUEST_CODE_PERMISSION_ROOM_DETAILS = 1;
    public static final int REQUEST_CODE_PERMISSION_TAKE_PHOTO = 3;
    public static final int REQUEST_CODE_PERMISSION_VIDEO_IP_CALL = 5;
    public static final int REQUEST_CODE_PERMISSION_VIDEO_RECORDING = 5;
    private static final String RESTART_IN_PROGRESS_KEY = "RESTART_IN_PROGRESS_KEY";
    private static final int ROOM_SIZE_ONE_TO_ONE = 2;
    private static final String TAG_FRAGMENT_UNKNOWN_DEVICES_DIALOG_DIALOG = "ActionBarActivity.TAG_FRAGMENT_UNKNOWN_DEVICES_DIALOG_DIALOG";
    public static final boolean UTILS_DISPLAY_PROGRESS_BAR = true;
    public static final boolean UTILS_HIDE_PROGRESS_BAR = false;
    public static final float UTILS_OPACITY_FULL = 0.0f;
    public static final float UTILS_OPACITY_HALF = 0.5f;
    public static final float UTILS_OPACITY_NONE = 1.0f;
    public static final float UTILS_POWER_LEVEL_ADMIN = 100.0f;
    public static final float UTILS_POWER_LEVEL_MODERATOR = 50.0f;
    private static int mBadgeValue;

    public static void logout(Context context, List<MXSession> list, boolean z, ApiCallback<Void> apiCallback) {
        logout(context, list.iterator(), z, apiCallback);
    }

    /* access modifiers changed from: private */
    public static void logout(final Context context, final Iterator<MXSession> it, final boolean z, final ApiCallback<Void> apiCallback) {
        if (!it.hasNext()) {
            if (apiCallback != null) {
                apiCallback.onSuccess(null);
            }
            return;
        }
        MXSession mXSession = (MXSession) it.next();
        if (mXSession.isAlive()) {
            EventStreamService instance = EventStreamService.getInstance();
            if (instance != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(mXSession.getMyUserId());
                instance.stopAccounts(arrayList);
            }
            MyPresenceManager.getInstance(context, mXSession).advertiseOffline();
            MyPresenceManager.remove(mXSession);
            EventStreamService.removeNotification();
            Matrix.getInstance(context).getSharedGCMRegistrationManager().unregister(mXSession, (ThirdPartyRegistrationListener) null);
            Matrix.getInstance(context).clearSession(context, mXSession, z, new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    CommonActivityUtils.logout(context, it, z, apiCallback);
                }
            });
        }
    }

    public static boolean shouldRestartApp(Context context) {
        EventStreamService instance = EventStreamService.getInstance();
        if (!Matrix.hasValidSessions()) {
            Log.m211e(LOG_TAG, "shouldRestartApp : the client has no valid session");
        }
        if (instance == null) {
            Log.m211e(LOG_TAG, "eventStreamService is null : restart the event stream");
            startEventStreamService(context);
        }
        return !Matrix.hasValidSessions();
    }

    public static boolean isGoingToSplash(Activity activity) {
        return isGoingToSplash(activity, null, null);
    }

    public static boolean isGoingToSplash(Activity activity, String str, String str2) {
        if (Matrix.hasValidSessions()) {
            for (MXSession mXSession : Matrix.getInstance(activity).getSessions()) {
                if (mXSession.isAlive() && !mXSession.getDataHandler().getStore().isReady()) {
                    Intent intent = new Intent(activity, SplashActivity.class);
                    if (!(str == null || str2 == null)) {
                        intent.putExtra("EXTRA_MATRIX_ID", str);
                        intent.putExtra("EXTRA_ROOM_ID", str2);
                    }
                    activity.startActivity(intent);
                    activity.finish();
                    return true;
                }
            }
        }
        return false;
    }

    public static void onApplicationStarted(Activity activity) {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(RESTART_IN_PROGRESS_KEY, false).apply();
    }

    public static void restartApp(Activity activity) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        defaultSharedPreferences.edit();
        if (!defaultSharedPreferences.getBoolean(RESTART_IN_PROGRESS_KEY, false)) {
            displayToast(activity.getApplicationContext(), "Restart the application (low memory)");
            Log.m211e(LOG_TAG, "Kill the application");
            defaultSharedPreferences.edit().putBoolean(RESTART_IN_PROGRESS_KEY, true).apply();
            ((AlarmManager) activity.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 50, PendingIntent.getActivity(activity, 314159, new Intent(activity, LoginActivity.class), ErrorDialogData.BINDER_CRASH));
            System.exit(0);
            return;
        }
        Log.m211e(LOG_TAG, "The application is restarting, please wait !!");
        activity.finish();
    }

    public static void logout(Activity activity) {
        logout(activity, true);
    }

    public static void logout(Activity activity, final boolean z) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## logout() : from ");
        sb.append(activity);
        sb.append(" goToLoginPage ");
        sb.append(z);
        Log.m209d(str, sb.toString());
        final Context applicationContext = activity == 0 ? VectorApp.getInstance().getApplicationContext() : activity;
        EventStreamService.removeNotification();
        stopEventStream(applicationContext);
        try {
            ShortcutBadger.setBadge(applicationContext, 0);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## logout(): Exception Msg=");
            sb2.append(e.getMessage());
            Log.m209d(str2, sb2.toString());
        }
        for (MXSession mXSession : Matrix.getMXSessions(applicationContext)) {
            MyPresenceManager.getInstance(applicationContext, mXSession).advertiseOffline();
            MyPresenceManager.remove(mXSession);
        }
        PreferencesManager.clearPreferences(applicationContext);
        Matrix.getInstance(applicationContext).getSharedGCMRegistrationManager().resetGCMRegistration();
        if (z) {
            Matrix.getInstance(applicationContext).getSharedGCMRegistrationManager().clearPreferences();
            if (activity != 0) {
                activity.startActivity(new Intent(activity, LoggingOutActivity.class));
                activity.finish();
            } else {
                Intent intent = new Intent(applicationContext, LoggingOutActivity.class);
                intent.setFlags(268468224);
                applicationContext.startActivity(intent);
            }
        }
        Matrix.getInstance(applicationContext).clearSessions(applicationContext, true, new SimpleApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Matrix.getInstance(applicationContext).getLoginStorage().clear();
                Matrix.getInstance(applicationContext).clearTmpStoresList();
                PIDsRetriever.getInstance().reset();
                ContactsManager.getInstance().reset();
                MXMediasCache.clearThumbnailsCache(applicationContext);
                if (z) {
                    Activity currentActivity = VectorApp.getCurrentActivity();
                    if (currentActivity != null) {
                        currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
                        currentActivity.finish();
                        return;
                    }
                    Intent intent = new Intent(applicationContext, LoginActivity.class);
                    intent.setFlags(268468224);
                    applicationContext.startActivity(intent);
                }
            }
        });
    }

    public static void deactivateAccount(final Context context, final MXSession mXSession, String str, boolean z, @NonNull final ApiCallback<Void> apiCallback) {
        Matrix.getInstance(context).deactivateSession(context, mXSession, str, z, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                EventStreamService.removeNotification();
                CommonActivityUtils.stopEventStream(context);
                try {
                    ShortcutBadger.setBadge(context, 0);
                } catch (Exception e) {
                    String access$200 = CommonActivityUtils.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## logout(): Exception Msg=");
                    sb.append(e.getMessage());
                    Log.m209d(access$200, sb.toString());
                }
                MyPresenceManager.getInstance(context, mXSession).advertiseOffline();
                MyPresenceManager.remove(mXSession);
                PreferencesManager.clearPreferences(context);
                Matrix.getInstance(context).getSharedGCMRegistrationManager().resetGCMRegistration();
                Matrix.getInstance(context).getSharedGCMRegistrationManager().clearPreferences();
                Matrix.getInstance(context).getLoginStorage().clear();
                Matrix.getInstance(context).clearTmpStoresList();
                PIDsRetriever.getInstance().reset();
                ContactsManager.getInstance().reset();
                MXMediasCache.clearThumbnailsCache(context);
                apiCallback.onSuccess(voidR);
            }
        });
    }

    public static void startLoginActivityNewTask(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(268468224);
        activity.startActivity(intent);
    }

    public static String removeUrlScheme(String str) {
        if (str == null) {
            return str;
        }
        if (str.startsWith(HTTP_SCHEME)) {
            return str.substring(HTTP_SCHEME.length());
        }
        return str.startsWith(HTTPS_SCHEME) ? str.substring(HTTPS_SCHEME.length()) : str;
    }

    private static boolean isUserLogout(Context context) {
        return context == null || Matrix.getInstance(context.getApplicationContext()).getDefaultSession() == null;
    }

    private static void sendEventStreamAction(Context context, StreamAction streamAction) {
        Context applicationContext = context.getApplicationContext();
        if (!isUserLogout(applicationContext)) {
            Intent intent = new Intent(applicationContext, EventStreamService.class);
            if (streamAction != StreamAction.CATCHUP || !EventStreamService.isStopped()) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("sendEventStreamAction ");
                sb.append(streamAction);
                Log.m209d(str, sb.toString());
                intent.putExtra(EventStreamService.EXTRA_STREAM_ACTION, streamAction.ordinal());
            } else {
                Log.m209d(LOG_TAG, "sendEventStreamAction : auto restart");
                intent.putExtra(EventStreamService.EXTRA_AUTO_RESTART_ACTION, EventStreamService.EXTRA_AUTO_RESTART_ACTION);
            }
            applicationContext.startService(intent);
            return;
        }
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## sendEventStreamAction(): \"");
        sb2.append(streamAction);
        sb2.append("\" action not sent - user logged out");
        Log.m209d(str2, sb2.toString());
    }

    /* access modifiers changed from: private */
    public static void stopEventStream(Context context) {
        Log.m209d(LOG_TAG, "stopEventStream");
        sendEventStreamAction(context, StreamAction.STOP);
    }

    public static void pauseEventStream(Context context) {
        Log.m209d(LOG_TAG, "pauseEventStream");
        sendEventStreamAction(context, StreamAction.PAUSE);
    }

    public static void resumeEventStream(Context context) {
        Log.m209d(LOG_TAG, "resumeEventStream");
        sendEventStreamAction(context, StreamAction.RESUME);
    }

    public static void catchupEventStream(Context context) {
        if (VectorApp.isAppInBackground()) {
            Log.m209d(LOG_TAG, "catchupEventStream");
            sendEventStreamAction(context, StreamAction.CATCHUP);
        }
    }

    public static void onGcmUpdate(Context context) {
        Log.m209d(LOG_TAG, "onGcmUpdate");
        sendEventStreamAction(context, StreamAction.GCM_STATUS_UPDATE);
    }

    public static void startEventStreamService(Context context) {
        if (EventStreamService.isStopped()) {
            ArrayList arrayList = new ArrayList();
            ArrayList<MXSession> sessions = Matrix.getInstance(context.getApplicationContext()).getSessions();
            if (sessions != null && sessions.size() > 0) {
                GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(context).getSharedGCMRegistrationManager();
                Log.m211e(LOG_TAG, "## startEventStreamService() : restart EventStreamService");
                for (MXSession mXSession : sessions) {
                    if (!(mXSession.getDataHandler() == null || mXSession.getDataHandler().getStore() == null)) {
                        if (!mXSession.getDataHandler().getStore().isReady()) {
                            String str = LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## startEventStreamService() : the session ");
                            sb.append(mXSession.getMyUserId());
                            sb.append(" is not opened");
                            Log.m211e(str, sb.toString());
                            mXSession.getDataHandler().getStore().open();
                        } else {
                            String str2 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## startEventStreamService() : check if the crypto of the session ");
                            sb2.append(mXSession.getMyUserId());
                            Log.m211e(str2, sb2.toString());
                            mXSession.checkCrypto();
                        }
                        mXSession.setSyncDelay(sharedGCMRegistrationManager.isBackgroundSyncAllowed() ? sharedGCMRegistrationManager.getBackgroundSyncDelay() : 0);
                        mXSession.setSyncTimeout(sharedGCMRegistrationManager.getBackgroundSyncTimeOut());
                        arrayList.add(mXSession.getCredentials().userId);
                    }
                }
                if (arrayList.size() > 0) {
                    Intent intent = new Intent(context, EventStreamService.class);
                    intent.putExtra(EventStreamService.EXTRA_MATRIX_IDS, (String[]) arrayList.toArray(new String[arrayList.size()]));
                    intent.putExtra(EventStreamService.EXTRA_STREAM_ACTION, StreamAction.START.ordinal());
                    context.startService(intent);
                }
            }
            if (EventStreamService.getInstance() != null) {
                EventStreamService.getInstance().refreshForegroundNotification();
            }
        }
    }

    public static boolean isPowerLevelEnoughForAvatarUpdate(Room room, MXSession mXSession) {
        if (room == null || mXSession == null) {
            return false;
        }
        PowerLevels powerLevels = room.getLiveState().getPowerLevels();
        if (powerLevels == null || powerLevels.getUserPowerLevel(mXSession.getMyUserId()) < powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_AVATAR)) {
            return false;
        }
        return true;
    }

    private static boolean checkPermissions(final int i, final Activity activity, final Fragment fragment) {
        if (activity == null) {
            Log.m217w(LOG_TAG, "## checkPermissions(): invalid input data");
            return false;
        }
        if (i != 0) {
            if (3 == i || 4 == i || 5 == i || 8 == i || 1002 == i || 8 == i || 1 == i || 1000 == i) {
                ArrayList<String> arrayList = new ArrayList<>();
                final ArrayList arrayList2 = new ArrayList();
                Resources resources = activity.getResources();
                String str = "";
                boolean updatePermissionsToBeGranted = 1 == (i & 1) ? updatePermissionsToBeGranted(activity, arrayList, arrayList2, "android.permission.CAMERA") | false : false;
                if (1000 == i) {
                    updatePermissionsToBeGranted |= updatePermissionsToBeGranted(activity, arrayList, arrayList2, "android.permission.ACCESS_COARSE_LOCATION");
                }
                if (4 == (i & 4)) {
                    updatePermissionsToBeGranted |= updatePermissionsToBeGranted(activity, arrayList, arrayList2, "android.permission.RECORD_AUDIO");
                }
                if (2 == (i & 2)) {
                    updatePermissionsToBeGranted |= updatePermissionsToBeGranted(activity, arrayList, arrayList2, "android.permission.WRITE_EXTERNAL_STORAGE");
                }
                boolean updatePermissionsToBeGranted2 = 1000 == (i & 1000) ? updatePermissionsToBeGranted(activity, arrayList, arrayList2, "android.permission.ACCESS_COARSE_LOCATION") | updatePermissionsToBeGranted : updatePermissionsToBeGranted;
                if (8 == (i & 8)) {
                    String str2 = "android.permission.READ_CONTACTS";
                    if (VERSION.SDK_INT >= 23) {
                        updatePermissionsToBeGranted2 |= updatePermissionsToBeGranted(activity, arrayList, arrayList2, str2);
                    } else if (!ContactsManager.getInstance().isContactBookAccessRequested()) {
                        arrayList2.add(str2);
                        updatePermissionsToBeGranted2 = true;
                    }
                }
                if (!arrayList.isEmpty()) {
                    if (resources == null) {
                        str = "You are about to be asked to grant permissions..\n\n";
                    } else if (i != 5 && i != 4) {
                        for (String str3 : arrayList) {
                            if ("android.permission.CAMERA".equals(str3)) {
                                if (!TextUtils.isEmpty(str)) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(str);
                                    sb.append("\n\n");
                                    str = sb.toString();
                                }
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append(str);
                                sb2.append(resources.getString(C1299R.string.permissions_rationale_msg_camera));
                                str = sb2.toString();
                            } else if ("android.permission.RECORD_AUDIO".equals(str3)) {
                                if (!TextUtils.isEmpty(str)) {
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append(str);
                                    sb3.append("\n\n");
                                    str = sb3.toString();
                                }
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append(str);
                                sb4.append(resources.getString(C1299R.string.permissions_rationale_msg_record_audio));
                                str = sb4.toString();
                            } else if ("android.permission.WRITE_EXTERNAL_STORAGE".equals(str3)) {
                                if (!TextUtils.isEmpty(str)) {
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append(str);
                                    sb5.append("\n\n");
                                    str = sb5.toString();
                                }
                                StringBuilder sb6 = new StringBuilder();
                                sb6.append(str);
                                sb6.append(resources.getString(C1299R.string.permissions_rationale_msg_storage));
                                str = sb6.toString();
                            } else if ("android.permission.READ_CONTACTS".equals(str3)) {
                                if (!TextUtils.isEmpty(str)) {
                                    StringBuilder sb7 = new StringBuilder();
                                    sb7.append(str);
                                    sb7.append("\n\n");
                                    str = sb7.toString();
                                }
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append(str);
                                sb8.append(resources.getString(C1299R.string.permissions_rationale_msg_contacts));
                                str = sb8.toString();
                            } else if ("android.permission.ACCESS_COARSE_LOCATION".equals(str3)) {
                                if (!TextUtils.isEmpty(str)) {
                                    StringBuilder sb9 = new StringBuilder();
                                    sb9.append(str);
                                    sb9.append("\n\n");
                                    str = sb9.toString();
                                }
                                StringBuilder sb10 = new StringBuilder();
                                sb10.append(str);
                                sb10.append(resources.getString(C1299R.string.permissions_rationale_location));
                                str = sb10.toString();
                            } else {
                                Log.m209d(LOG_TAG, "## checkPermissions(): already denied permission not supported");
                            }
                        }
                    } else if (arrayList.contains("android.permission.CAMERA") && arrayList.contains("android.permission.RECORD_AUDIO")) {
                        StringBuilder sb11 = new StringBuilder();
                        sb11.append(str);
                        sb11.append(resources.getString(C1299R.string.permissions_rationale_msg_camera_and_audio));
                        str = sb11.toString();
                    } else if (arrayList.contains("android.permission.RECORD_AUDIO")) {
                        StringBuilder sb12 = new StringBuilder();
                        sb12.append(str);
                        sb12.append(resources.getString(C1299R.string.permissions_rationale_msg_record_audio));
                        String sb13 = sb12.toString();
                        StringBuilder sb14 = new StringBuilder();
                        sb14.append(sb13);
                        sb14.append(resources.getString(C1299R.string.permissions_rationale_msg_record_audio_explanation));
                        str = sb14.toString();
                    } else if (arrayList.contains("android.permission.CAMERA")) {
                        StringBuilder sb15 = new StringBuilder();
                        sb15.append(str);
                        sb15.append(resources.getString(C1299R.string.permissions_rationale_msg_camera));
                        String sb16 = sb15.toString();
                        StringBuilder sb17 = new StringBuilder();
                        sb17.append(sb16);
                        sb17.append(resources.getString(C1299R.string.permissions_rationale_msg_camera_explanation));
                        str = sb17.toString();
                    }
                    Builder builder = new Builder(activity);
                    if (resources != null) {
                        builder.setTitle(C1299R.string.permissions_rationale_popup_title);
                    }
                    builder.setMessage(str);
                    builder.setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (arrayList2.isEmpty()) {
                                return;
                            }
                            if (fragment != null) {
                                fragment.requestPermissions((String[]) arrayList2.toArray(new String[arrayList2.size()]), i);
                            } else {
                                ActivityCompat.requestPermissions(activity, (String[]) arrayList2.toArray(new String[arrayList2.size()]), i);
                            }
                        }
                    });
                    builder.show().setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialogInterface) {
                            CommonActivityUtils.displayToast(activity, activity.getString(C1299R.string.missing_permissions_warning));
                        }
                    });
                    return false;
                } else if (updatePermissionsToBeGranted2) {
                    final String[] strArr = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
                    if (arrayList2.contains("android.permission.READ_CONTACTS") && VERSION.SDK_INT < 23) {
                        Builder builder2 = new Builder(activity);
                        builder2.setIcon(17301659);
                        if (resources != null) {
                            builder2.setTitle(resources.getString(C1299R.string.permissions_rationale_popup_title));
                        }
                        builder2.setMessage(resources.getString(C1299R.string.permissions_msg_contacts_warning_other_androids));
                        builder2.setPositiveButton(activity.getString(C1299R.string.yes), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactsManager.getInstance().setIsContactBookAccessAllowed(true);
                                if (fragment != null) {
                                    fragment.requestPermissions(strArr, i);
                                } else {
                                    ActivityCompat.requestPermissions(activity, strArr, i);
                                }
                            }
                        });
                        builder2.setNegativeButton(activity.getString(C1299R.string.f114no), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ContactsManager.getInstance().setIsContactBookAccessAllowed(false);
                                if (fragment != null) {
                                    fragment.requestPermissions(strArr, i);
                                } else {
                                    ActivityCompat.requestPermissions(activity, strArr, i);
                                }
                            }
                        });
                        builder2.show();
                        return false;
                    } else if (fragment != null) {
                        fragment.requestPermissions(strArr, i);
                        return false;
                    } else {
                        ActivityCompat.requestPermissions(activity, strArr, i);
                        return false;
                    }
                }
            } else {
                Log.m217w(LOG_TAG, "## checkPermissions(): permissions to be granted are not supported");
                return false;
            }
        }
        return true;
    }

    public static boolean checkPermissions(int i, Activity activity) {
        return checkPermissions(i, activity, null);
    }

    public static void checkPermissions(int i, Fragment fragment) {
        checkPermissions(i, fragment.getActivity(), fragment);
    }

    private static boolean updatePermissionsToBeGranted(Activity activity, List<String> list, List<String> list2, String str) {
        list2.add(str);
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), str) == 0) {
            return false;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, str)) {
            return true;
        }
        list.add(str);
        return true;
    }

    public static boolean onPermissionResultAudioIpCall(Context context, String[] strArr, int[] iArr) {
        try {
            if (!"android.permission.RECORD_AUDIO".equals(strArr[0])) {
                return false;
            }
            if (iArr[0] == 0) {
                Log.m209d(LOG_TAG, "## onPermissionResultAudioIpCall(): RECORD_AUDIO permission granted");
                return true;
            }
            Log.m209d(LOG_TAG, "## onPermissionResultAudioIpCall(): RECORD_AUDIO permission not granted");
            if (context == null) {
                return false;
            }
            displayToast(context, context.getString(C1299R.string.permissions_action_not_performed_missing_permissions));
            return false;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onPermissionResultAudioIpCall(): Exception MSg=");
            sb.append(e.getMessage());
            Log.m209d(str, sb.toString());
            return false;
        }
    }

    public static boolean onPermissionResultVideoIpCall(Context context, String[] strArr, int[] iArr) {
        int i = 0;
        int i2 = 0;
        while (i < strArr.length) {
            try {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onPermissionResultVideoIpCall(): ");
                sb.append(strArr[i]);
                sb.append("=");
                sb.append(iArr[i]);
                Log.m209d(str, sb.toString());
                if ("android.permission.CAMERA".equals(strArr[i])) {
                    if (iArr[i] == 0) {
                        Log.m209d(LOG_TAG, "## onPermissionResultVideoIpCall(): CAMERA permission granted");
                        i2++;
                    } else {
                        Log.m217w(LOG_TAG, "## onPermissionResultVideoIpCall(): CAMERA permission not granted");
                    }
                }
                if ("android.permission.RECORD_AUDIO".equals(strArr[i])) {
                    if (iArr[i] == 0) {
                        Log.m209d(LOG_TAG, "## onPermissionResultVideoIpCall(): WRITE_EXTERNAL_STORAGE permission granted");
                        i2++;
                    } else {
                        Log.m217w(LOG_TAG, "## onPermissionResultVideoIpCall(): RECORD_AUDIO permission not granted");
                    }
                }
                i++;
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## onPermissionResultVideoIpCall(): Exception MSg=");
                sb2.append(e.getMessage());
                Log.m209d(str2, sb2.toString());
                return false;
            }
        }
        if (2 == i2) {
            return true;
        }
        Log.m217w(LOG_TAG, "## onPermissionResultVideoIpCall(): No permissions granted to IP call (video or audio)");
        if (context == null) {
            return false;
        }
        displayToast(context, context.getString(C1299R.string.permissions_action_not_performed_missing_permissions));
        return false;
    }

    public static void previewRoom(Activity activity, RoomPreviewData roomPreviewData) {
        if (activity != null && roomPreviewData != null) {
            VectorRoomActivity.sRoomPreviewData = roomPreviewData;
            Intent intent = new Intent(activity, VectorRoomActivity.class);
            intent.putExtra("EXTRA_ROOM_ID", roomPreviewData.getRoomId());
            intent.putExtra(VectorRoomActivity.EXTRA_ROOM_PREVIEW_ID, roomPreviewData.getRoomId());
            intent.putExtra(VectorRoomActivity.EXTRA_EXPAND_ROOM_HEADER, true);
            activity.startActivity(intent);
        }
    }

    public static Intent buildIntentPreviewRoom(String str, String str2, Context context, Class<?> cls) {
        String str3 = null;
        if (context == null || str2 == null || str == null) {
            return null;
        }
        MXSession session = Matrix.getInstance(context).getSession(str);
        if (session == null) {
            session = Matrix.getInstance(context).getDefaultSession();
        }
        if (session == null || !session.isAlive()) {
            return null;
        }
        Room room = session.getDataHandler().getRoom(str2);
        if (!(room == null || room.getLiveState() == null)) {
            str3 = room.getLiveState().getAlias();
        }
        Intent intent = new Intent(context, cls);
        intent.putExtra("EXTRA_ROOM_ID", str2);
        intent.putExtra(VectorRoomActivity.EXTRA_ROOM_PREVIEW_ID, str2);
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", str);
        intent.putExtra(VectorRoomActivity.EXTRA_EXPAND_ROOM_HEADER, true);
        intent.putExtra(VectorRoomActivity.EXTRA_ROOM_PREVIEW_ROOM_ALIAS, str3);
        return intent;
    }

    public static void previewRoom(Activity activity, MXSession mXSession, String str, String str2, ApiCallback<Void> apiCallback) {
        RoomPreviewData roomPreviewData = new RoomPreviewData(mXSession, str, null, str2, null);
        previewRoom(activity, mXSession, str, roomPreviewData, apiCallback);
    }

    public static void previewRoom(final Activity activity, MXSession mXSession, String str, final RoomPreviewData roomPreviewData, final ApiCallback<Void> apiCallback) {
        Room room = mXSession.getDataHandler().getRoom(str, false);
        if (room != null) {
            if (room.isInvited()) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("previewRoom : the user is invited -> display the preview ");
                sb.append(VectorApp.getCurrentActivity());
                Log.m209d(str2, sb.toString());
                previewRoom(activity, roomPreviewData);
            } else {
                Log.m209d(LOG_TAG, "previewRoom : open the room");
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", mXSession.getMyUserId());
                hashMap.put("EXTRA_ROOM_ID", str);
                goToRoomPage(activity, mXSession, hashMap);
            }
            if (apiCallback != null) {
                apiCallback.onSuccess(null);
                return;
            }
            return;
        }
        roomPreviewData.fetchPreviewData(new ApiCallback<Void>() {
            private void onDone() {
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
                CommonActivityUtils.previewRoom(activity, roomPreviewData);
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

    public static void goToRoomPage(Activity activity, Map<String, Object> map) {
        goToRoomPage(activity, null, map);
    }

    public static void goToRoomPage(final Activity activity, final MXSession mXSession, final Map<String, Object> map) {
        if (mXSession == null) {
            mXSession = Matrix.getMXSession(activity, (String) map.get("MXCActionBarActivity.EXTRA_MATRIX_ID"));
        }
        if (mXSession != null && mXSession.isAlive()) {
            Room room = mXSession.getDataHandler().getRoom((String) map.get("EXTRA_ROOM_ID"));
            if (room == null || !room.isLeaving()) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (!(activity instanceof VectorHomeActivity)) {
                            Log.m209d(CommonActivityUtils.LOG_TAG, "## goToRoomPage(): start VectorHomeActivity..");
                            Intent intent = new Intent(activity, VectorHomeActivity.class);
                            intent.setFlags(603979776);
                            intent.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS, (Serializable) map);
                            activity.startActivity(intent);
                            return;
                        }
                        Log.m209d(CommonActivityUtils.LOG_TAG, "## goToRoomPage(): already in VectorHomeActivity..");
                        Intent intent2 = new Intent(activity, VectorRoomActivity.class);
                        for (String str : map.keySet()) {
                            Object obj = map.get(str);
                            if (obj instanceof String) {
                                intent2.putExtra(str, (String) obj);
                            } else if (obj instanceof Boolean) {
                                intent2.putExtra(str, (Boolean) obj);
                            } else if (obj instanceof Parcelable) {
                                intent2.putExtra(str, (Parcelable) obj);
                            }
                        }
                        if (map.get(VectorRoomActivity.EXTRA_DEFAULT_NAME) == null) {
                            Room room = mXSession.getDataHandler().getRoom((String) map.get("EXTRA_ROOM_ID"));
                            if (room != null && room.isInvited()) {
                                String roomDisplayName = VectorUtils.getRoomDisplayName(activity, mXSession, room);
                                if (roomDisplayName != null) {
                                    intent2.putExtra(VectorRoomActivity.EXTRA_DEFAULT_NAME, roomDisplayName);
                                }
                            }
                        }
                        activity.startActivity(intent2);
                    }
                });
            }
        }
    }

    private static ArrayList<Room> findOneToOneRoomList(MXSession mXSession, String str) {
        ArrayList<Room> arrayList = new ArrayList<>();
        if (!(mXSession == null || str == null)) {
            for (Room room : mXSession.getDataHandler().getStore().getRooms()) {
                List list = (List) room.getJoinedMembers();
                if (list != null && 2 == list.size()) {
                    String userId = ((RoomMember) list.get(0)).getUserId();
                    String userId2 = ((RoomMember) list.get(1)).getUserId();
                    if (userId.equals(str) || userId2.equals(str)) {
                        arrayList.add(room);
                    }
                }
            }
        }
        return arrayList;
    }

    public static void setToggleDirectMessageRoom(MXSession mXSession, String str, String str2, Activity activity, @NonNull final ApiCallback<Void> apiCallback) {
        if (mXSession == null || activity == null || TextUtils.isEmpty(str)) {
            Log.m211e(LOG_TAG, "## setToggleDirectMessageRoom(): failure - invalid input parameters");
            apiCallback.onUnexpectedError(new Exception("## setToggleDirectMessageRoom(): failure - invalid input parameters"));
            return;
        }
        mXSession.toggleDirectChatRoom(str, str2, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                apiCallback.onSuccess(null);
            }
        });
    }

    public static void sendFilesTo(Activity activity, Intent intent) {
        if (Matrix.getMXSessions(activity).size() == 1) {
            sendFilesTo(activity, intent, Matrix.getMXSession(activity, null));
        } else {
            boolean z = activity instanceof FragmentActivity;
        }
    }

    private static void sendFilesTo(final Activity activity, final Intent intent, final MXSession mXSession) {
        if (mXSession != null && mXSession.isAlive() && !activity.isFinishing()) {
            final ArrayList arrayList = new ArrayList(mXSession.getDataHandler().getStore().getSummaries());
            int i = 0;
            while (i < arrayList.size()) {
                Room room = mXSession.getDataHandler().getRoom(((RoomSummary) arrayList.get(i)).getRoomId());
                if (room == null || room.isInvited() || room.isConferenceUserRoom()) {
                    arrayList.remove(i);
                    i--;
                }
                i++;
            }
            Collections.sort(arrayList, new Comparator<RoomSummary>() {
                public int compare(RoomSummary roomSummary, RoomSummary roomSummary2) {
                    if (roomSummary == null || roomSummary.getLatestReceivedEvent() == null) {
                        return 1;
                    }
                    if (roomSummary2 == null || roomSummary2.getLatestReceivedEvent() == null || roomSummary.getLatestReceivedEvent().getOriginServerTs() > roomSummary2.getLatestReceivedEvent().getOriginServerTs()) {
                        return -1;
                    }
                    if (roomSummary.getLatestReceivedEvent().getOriginServerTs() < roomSummary2.getLatestReceivedEvent().getOriginServerTs()) {
                        return 1;
                    }
                    return 0;
                }
            });
            Builder builder = new Builder(activity);
            builder.setTitle(activity.getText(C1299R.string.send_files_in));
            VectorRoomsSelectionAdapter vectorRoomsSelectionAdapter = new VectorRoomsSelectionAdapter(activity, C1299R.layout.adapter_item_vector_recent_room, mXSession);
            vectorRoomsSelectionAdapter.addAll(arrayList);
            builder.setNegativeButton(activity.getText(C1299R.string.cancel), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setAdapter(vectorRoomsSelectionAdapter, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, final int i) {
                    dialogInterface.dismiss();
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            RoomSummary roomSummary = (RoomSummary) arrayList.get(i);
                            HashMap hashMap = new HashMap();
                            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", mXSession.getMyUserId());
                            hashMap.put("EXTRA_ROOM_ID", roomSummary.getRoomId());
                            hashMap.put(VectorRoomActivity.EXTRA_ROOM_INTENT, intent);
                            CommonActivityUtils.goToRoomPage(activity, mXSession, hashMap);
                        }
                    });
                }
            });
            builder.show();
        }
    }

    public static void openMedia(final Activity activity, final String str, final String str2) {
        if (activity != null && str != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        File file = new File(str);
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setDataAndType(Uri.fromFile(file), str2);
                        activity.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(activity, e.getLocalizedMessage(), 1).show();
                    } catch (Exception e2) {
                        String access$200 = CommonActivityUtils.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## openMedia(): Exception Msg=");
                        sb.append(e2.getMessage());
                        Log.m209d(access$200, sb.toString());
                    }
                }
            });
        }
    }

    private static void saveFileInto(final File file, final String str, final String str2, final ApiCallback<String> apiCallback) {
        if (file == null || str == null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onNetworkError(new Exception("Null parameters"));
                    }
                }
            });
            return;
        }
        C135316 r0 = new AsyncTask<Void, Void, Pair<String, Exception>>() {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x00ff A[SYNTHETIC, Splitter:B:51:0x00ff] */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x0107 A[Catch:{ Exception -> 0x0103 }] */
            /* JADX WARNING: Removed duplicated region for block: B:60:0x0130 A[SYNTHETIC, Splitter:B:60:0x0130] */
            /* JADX WARNING: Removed duplicated region for block: B:65:0x0138 A[Catch:{ Exception -> 0x0134 }] */
            /* JADX WARNING: Removed duplicated region for block: B:73:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public android.util.Pair<java.lang.String, java.lang.Exception> doInBackground(java.lang.Void... r10) {
                /*
                    r9 = this;
                    java.lang.String r10 = r4
                    if (r10 != 0) goto L_0x0037
                    java.io.File r10 = r2
                    java.lang.String r10 = r10.getName()
                    java.lang.String r0 = "."
                    int r10 = r10.lastIndexOf(r0)
                    java.lang.String r0 = ""
                    if (r10 <= 0) goto L_0x001e
                    java.io.File r0 = r2
                    java.lang.String r0 = r0.getName()
                    java.lang.String r0 = r0.substring(r10)
                L_0x001e:
                    java.lang.StringBuilder r10 = new java.lang.StringBuilder
                    r10.<init>()
                    java.lang.String r1 = "vector_"
                    r10.append(r1)
                    long r1 = java.lang.System.currentTimeMillis()
                    r10.append(r1)
                    r10.append(r0)
                    java.lang.String r10 = r10.toString()
                    goto L_0x0039
                L_0x0037:
                    java.lang.String r10 = r4
                L_0x0039:
                    java.lang.String r0 = r3
                    java.io.File r0 = android.os.Environment.getExternalStoragePublicDirectory(r0)
                    if (r0 == 0) goto L_0x0044
                    r0.mkdirs()
                L_0x0044:
                    java.io.File r1 = new java.io.File
                    r1.<init>(r0, r10)
                    boolean r2 = r1.exists()
                    r3 = 0
                    if (r2 == 0) goto L_0x0090
                    java.lang.String r2 = ""
                    java.lang.String r4 = "."
                    int r4 = r10.lastIndexOf(r4)
                    if (r4 <= 0) goto L_0x0065
                    java.lang.String r2 = r10.substring(r3, r4)
                    java.lang.String r10 = r10.substring(r4)
                    r8 = r2
                    r2 = r10
                    r10 = r8
                L_0x0065:
                    r4 = 1
                    r5 = 1
                L_0x0067:
                    boolean r6 = r1.exists()
                    if (r6 == 0) goto L_0x0090
                    java.io.File r1 = new java.io.File
                    java.lang.StringBuilder r6 = new java.lang.StringBuilder
                    r6.<init>()
                    r6.append(r10)
                    java.lang.String r7 = "("
                    r6.append(r7)
                    r6.append(r5)
                    java.lang.String r7 = ")"
                    r6.append(r7)
                    r6.append(r2)
                    java.lang.String r6 = r6.toString()
                    r1.<init>(r0, r6)
                    int r5 = r5 + r4
                    goto L_0x0067
                L_0x0090:
                    r10 = 0
                    r1.createNewFile()     // Catch:{ Exception -> 0x00f5, all -> 0x00f1 }
                    java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00f5, all -> 0x00f1 }
                    java.io.File r2 = r2     // Catch:{ Exception -> 0x00f5, all -> 0x00f1 }
                    r0.<init>(r2)     // Catch:{ Exception -> 0x00f5, all -> 0x00f1 }
                    java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00ee, all -> 0x00eb }
                    r2.<init>(r1)     // Catch:{ Exception -> 0x00ee, all -> 0x00eb }
                    r4 = 10240(0x2800, float:1.4349E-41)
                    byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x00e9 }
                L_0x00a4:
                    int r5 = r0.read(r4)     // Catch:{ Exception -> 0x00e9 }
                    r6 = -1
                    if (r5 == r6) goto L_0x00af
                    r2.write(r4, r3, r5)     // Catch:{ Exception -> 0x00e9 }
                    goto L_0x00a4
                L_0x00af:
                    android.util.Pair r3 = new android.util.Pair     // Catch:{ Exception -> 0x00e9 }
                    java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x00e9 }
                    r3.<init>(r1, r10)     // Catch:{ Exception -> 0x00e9 }
                    if (r0 == 0) goto L_0x00c0
                    r0.close()     // Catch:{ Exception -> 0x00be }
                    goto L_0x00c0
                L_0x00be:
                    r0 = move-exception
                    goto L_0x00c7
                L_0x00c0:
                    if (r2 == 0) goto L_0x012c
                    r2.close()     // Catch:{ Exception -> 0x00be }
                    goto L_0x012c
                L_0x00c7:
                    java.lang.String r1 = com.opengarden.firechat.activity.CommonActivityUtils.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## saveFileInto(): Exception Msg="
                    r2.append(r3)
                    java.lang.String r3 = r0.getMessage()
                    r2.append(r3)
                    java.lang.String r2 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
                    android.util.Pair r3 = new android.util.Pair
                    r3.<init>(r10, r0)
                    goto L_0x012c
                L_0x00e9:
                    r1 = move-exception
                    goto L_0x00f8
                L_0x00eb:
                    r1 = move-exception
                    r2 = r10
                    goto L_0x012e
                L_0x00ee:
                    r1 = move-exception
                    r2 = r10
                    goto L_0x00f8
                L_0x00f1:
                    r1 = move-exception
                    r0 = r10
                    r2 = r0
                    goto L_0x012e
                L_0x00f5:
                    r1 = move-exception
                    r0 = r10
                    r2 = r0
                L_0x00f8:
                    android.util.Pair r3 = new android.util.Pair     // Catch:{ all -> 0x012d }
                    r3.<init>(r10, r1)     // Catch:{ all -> 0x012d }
                    if (r0 == 0) goto L_0x0105
                    r0.close()     // Catch:{ Exception -> 0x0103 }
                    goto L_0x0105
                L_0x0103:
                    r0 = move-exception
                    goto L_0x010b
                L_0x0105:
                    if (r2 == 0) goto L_0x012c
                    r2.close()     // Catch:{ Exception -> 0x0103 }
                    goto L_0x012c
                L_0x010b:
                    java.lang.String r1 = com.opengarden.firechat.activity.CommonActivityUtils.LOG_TAG
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder
                    r2.<init>()
                    java.lang.String r3 = "## saveFileInto(): Exception Msg="
                    r2.append(r3)
                    java.lang.String r3 = r0.getMessage()
                    r2.append(r3)
                    java.lang.String r2 = r2.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r2)
                    android.util.Pair r3 = new android.util.Pair
                    r3.<init>(r10, r0)
                L_0x012c:
                    return r3
                L_0x012d:
                    r1 = move-exception
                L_0x012e:
                    if (r0 == 0) goto L_0x0136
                    r0.close()     // Catch:{ Exception -> 0x0134 }
                    goto L_0x0136
                L_0x0134:
                    r0 = move-exception
                    goto L_0x013c
                L_0x0136:
                    if (r2 == 0) goto L_0x015d
                    r2.close()     // Catch:{ Exception -> 0x0134 }
                    goto L_0x015d
                L_0x013c:
                    java.lang.String r2 = com.opengarden.firechat.activity.CommonActivityUtils.LOG_TAG
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "## saveFileInto(): Exception Msg="
                    r3.append(r4)
                    java.lang.String r4 = r0.getMessage()
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)
                    android.util.Pair r2 = new android.util.Pair
                    r2.<init>(r10, r0)
                L_0x015d:
                    throw r1
                */
                throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.CommonActivityUtils.C135316.doInBackground(java.lang.Void[]):android.util.Pair");
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Pair<String, Exception> pair) {
                if (apiCallback == null) {
                    return;
                }
                if (pair == null) {
                    apiCallback.onNetworkError(new Exception("Null parameters"));
                } else if (pair.first != null) {
                    apiCallback.onSuccess(pair.first);
                } else {
                    apiCallback.onNetworkError((Exception) pair.second);
                }
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## saveFileInto() failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
            r0.cancel(true);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(e);
                    }
                }
            });
        }
    }

    @SuppressLint({"NewApi"})
    public static void saveMediaIntoDownloads(final Context context, File file, String str, final String str2, final ApiCallback<String> apiCallback) {
        saveFileInto(file, Environment.DIRECTORY_DOWNLOADS, str, new ApiCallback<String>() {
            public void onSuccess(String str) {
                if (str != null) {
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService("download");
                    try {
                        File file = new File(str);
                        downloadManager.addCompletedDownload(file.getName(), file.getName(), true, str2, file.getAbsolutePath(), file.length(), true);
                    } catch (Exception e) {
                        String access$200 = CommonActivityUtils.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## saveMediaIntoDownloads(): Exception Msg=");
                        sb.append(e.getMessage());
                        Log.m211e(access$200, sb.toString());
                    }
                }
                if (apiCallback != null) {
                    apiCallback.onSuccess(str);
                }
            }

            public void onNetworkError(Exception exc) {
                Toast.makeText(context, exc.getLocalizedMessage(), 1).show();
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                Toast.makeText(context, matrixError.getLocalizedMessage(), 1).show();
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                Toast.makeText(context, exc.getLocalizedMessage(), 1).show();
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    public static void displayToastOnUiThread(final Activity activity, final String str) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    CommonActivityUtils.displayToast(activity.getApplicationContext(), str);
                }
            });
        }
    }

    public static void displayToast(Context context, CharSequence charSequence) {
        Toast.makeText(context, charSequence, 0).show();
    }

    public static int getRoomMaxPowerLevel(Room room) {
        int i = 0;
        if (room != null) {
            PowerLevels powerLevels = room.getLiveState().getPowerLevels();
            if (powerLevels != null) {
                for (RoomMember userId : room.getMembers()) {
                    int userPowerLevel = powerLevels.getUserPowerLevel(userId.getUserId());
                    if (userPowerLevel > i) {
                        i = userPowerLevel;
                    }
                }
            }
        }
        return i;
    }

    public static void updateBadgeCount(Context context, int i) {
        try {
            mBadgeValue = i;
            ShortcutBadger.setBadge(context, i);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## updateBadgeCount(): Exception Msg=");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public static void specificUpdateBadgeUnreadCount(MXSession mXSession, Context context) {
        if (context == null || mXSession == null) {
            Log.m217w(LOG_TAG, "## specificUpdateBadgeUnreadCount(): invalid input null values");
            return;
        }
        MXDataHandler dataHandler = mXSession.getDataHandler();
        if (dataHandler == null) {
            Log.m217w(LOG_TAG, "## specificUpdateBadgeUnreadCount(): invalid DataHandler instance");
        } else if (mXSession.isAlive()) {
            GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(context).getSharedGCMRegistrationManager();
            boolean z = true;
            boolean z2 = !Matrix.getInstance(context).isConnected();
            if (sharedGCMRegistrationManager == null || (sharedGCMRegistrationManager.useGCM() && sharedGCMRegistrationManager.hasRegistrationToken())) {
                z = false;
            }
            if (z2 || z) {
                updateBadgeCount(context, dataHandler);
            }
        }
    }

    private static void updateBadgeCount(Context context, MXDataHandler mXDataHandler) {
        if (context == null || mXDataHandler == null) {
            Log.m217w(LOG_TAG, "## updateBadgeCount(): invalid input null values");
        } else if (mXDataHandler.getStore() == null) {
            Log.m217w(LOG_TAG, "## updateBadgeCount(): invalid store instance");
        } else {
            ArrayList arrayList = new ArrayList(mXDataHandler.getStore().getRooms());
            int i = 0;
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                if (((Room) it.next()).getNotificationCount() > 0) {
                    i++;
                }
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## updateBadgeCount(): badge update count=");
            sb.append(i);
            Log.m209d(str, sb.toString());
            updateBadgeCount(context, i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0133  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean displayMemoryInformation(android.app.Activity r11, java.lang.String r12) {
        /*
            r0 = 0
            java.lang.Runtime r2 = java.lang.Runtime.getRuntime()     // Catch:{ Exception -> 0x0014 }
            long r3 = r2.freeMemory()     // Catch:{ Exception -> 0x0014 }
            long r5 = r2.totalMemory()     // Catch:{ Exception -> 0x0012 }
            r0 = 0
            long r0 = r5 - r3
            goto L_0x001e
        L_0x0012:
            r2 = move-exception
            goto L_0x0016
        L_0x0014:
            r2 = move-exception
            r3 = r0
        L_0x0016:
            r2.printStackTrace()
            r5 = -1
            r9 = r0
            r0 = r5
            r5 = r9
        L_0x001e:
            java.lang.String r2 = "Memory usage"
            java.lang.String r7 = "---------------------------------------------------"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r7)
            java.lang.String r2 = "Memory usage"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "----------- "
            r7.append(r8)
            r7.append(r12)
            java.lang.String r12 = " -----------------"
            r7.append(r12)
            java.lang.String r12 = r7.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r12)
            java.lang.String r12 = "Memory usage"
            java.lang.String r2 = "---------------------------------------------------"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r2)
            java.lang.String r12 = "Memory usage"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "usedSize   "
            r2.append(r7)
            r7 = 1048576(0x100000, double:5.180654E-318)
            long r0 = r0 / r7
            r2.append(r0)
            java.lang.String r0 = " MB"
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r0)
            java.lang.String r12 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "freeSize   "
            r0.append(r1)
            long r3 = r3 / r7
            r0.append(r3)
            java.lang.String r1 = " MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r0)
            java.lang.String r12 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "totalSize  "
            r0.append(r1)
            long r5 = r5 / r7
            r0.append(r5)
            java.lang.String r1 = " MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r0)
            java.lang.String r12 = "Memory usage"
            java.lang.String r0 = "---------------------------------------------------"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r0)
            if (r11 == 0) goto L_0x0133
            android.app.ActivityManager$MemoryInfo r12 = new android.app.ActivityManager$MemoryInfo
            r12.<init>()
            java.lang.String r0 = "activity"
            java.lang.Object r11 = r11.getSystemService(r0)
            android.app.ActivityManager r11 = (android.app.ActivityManager) r11
            r11.getMemoryInfo(r12)
            java.lang.String r11 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "availMem   "
            r0.append(r1)
            long r1 = r12.availMem
            long r1 = r1 / r7
            r0.append(r1)
            java.lang.String r1 = " MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)
            java.lang.String r11 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "totalMem   "
            r0.append(r1)
            long r1 = r12.totalMem
            long r1 = r1 / r7
            r0.append(r1)
            java.lang.String r1 = " MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)
            java.lang.String r11 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "threshold  "
            r0.append(r1)
            long r1 = r12.threshold
            long r1 = r1 / r7
            r0.append(r1)
            java.lang.String r1 = " MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)
            java.lang.String r11 = "Memory usage"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "lowMemory  "
            r0.append(r1)
            boolean r1 = r12.lowMemory
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)
            java.lang.String r11 = "Memory usage"
            java.lang.String r0 = "---------------------------------------------------"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r0)
            boolean r11 = r12.lowMemory
            return r11
        L_0x0133:
            r11 = 0
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.CommonActivityUtils.displayMemoryInformation(android.app.Activity, java.lang.String):boolean");
    }

    public static void onLowMemory(Activity activity) {
        if (!VectorApp.isAppInBackground()) {
            String simpleName = activity != null ? activity.getClass().getSimpleName() : "NotAvailable";
            String str = LOW_MEMORY_LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Active application : onLowMemory from ");
            sb.append(simpleName);
            Log.m211e(str, sb.toString());
            if (!displayMemoryInformation(activity, "onLowMemory test")) {
                Log.m211e(LOW_MEMORY_LOG_TAG, "Wait to be concerned");
            } else if (shouldRestartApp(activity)) {
                Log.m211e(LOW_MEMORY_LOG_TAG, "restart");
                restartApp(activity);
            } else {
                Log.m211e(LOW_MEMORY_LOG_TAG, "clear the application cache");
                Matrix.getInstance(activity).reloadSessions(activity);
            }
        } else {
            Log.m211e(LOW_MEMORY_LOG_TAG, "background application : onLowMemory ");
        }
        displayMemoryInformation(activity, "onLowMemory global");
    }

    public static void onTrimMemory(Activity activity, int i) {
        String simpleName = activity != null ? activity.getClass().getSimpleName() : "NotAvailable";
        String str = LOW_MEMORY_LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Active application : onTrimMemory from ");
        sb.append(simpleName);
        sb.append(" level=");
        sb.append(i);
        Log.m211e(str, sb.toString());
        displayMemoryInformation(activity, "onTrimMemory");
    }

    public static <T> void displayDeviceVerificationDialog(final MXDeviceInfo mXDeviceInfo, final String str, final MXSession mXSession, Activity activity, final ApiCallback<Void> apiCallback) {
        if (mXDeviceInfo == null || str == null || mXSession == null) {
            Log.m211e(LOG_TAG, "## displayDeviceVerificationDialog(): invalid imput parameters");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View inflate = activity.getLayoutInflater().inflate(C1299R.layout.encrypted_verify_device, null);
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_device_info_device_name)).setText(mXDeviceInfo.displayName());
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_device_info_device_id)).setText(mXDeviceInfo.deviceId);
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_device_info_device_key)).setText(MatrixSdkExtensionsKt.getFingerprintHumanReadable(mXDeviceInfo));
        builder.setView(inflate);
        builder.setTitle((int) C1299R.string.encryption_information_verify_device);
        builder.setPositiveButton((int) C1299R.string.encryption_information_verify_key_match, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                mXSession.getCrypto().setDeviceVerification(1, mXDeviceInfo.deviceId, str, apiCallback);
            }
        });
        builder.setNegativeButton((int) C1299R.string.cancel, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }
        });
        builder.create().show();
    }

    public static void exportKeys(final MXSession mXSession, String str, final ApiCallback<String> apiCallback) {
        final VectorApp instance = VectorApp.getInstance();
        if (mXSession.getCrypto() == null) {
            if (apiCallback != null) {
                apiCallback.onMatrixError(new MatrixError("EMPTY", "No crypto"));
            }
            return;
        }
        mXSession.getCrypto().exportRoomKeys(str, new ApiCallback<byte[]>() {
            public void onSuccess(byte[] bArr) {
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
                    MXMediasCache mediasCache = mXSession.getMediasCache();
                    StringBuilder sb = new StringBuilder();
                    sb.append("riot-");
                    sb.append(System.currentTimeMillis());
                    sb.append(".txt");
                    String saveMedia = mediasCache.saveMedia(byteArrayInputStream, sb.toString(), "text/plain");
                    byteArrayInputStream.close();
                    CommonActivityUtils.saveMediaIntoDownloads(instance, new File(Uri.parse(saveMedia).getPath()), "riot-keys.txt", "text/plain", new SimpleApiCallback<String>() {
                        public void onSuccess(String str) {
                            if (apiCallback != null) {
                                apiCallback.onSuccess(str);
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            if (apiCallback != null) {
                                apiCallback.onNetworkError(exc);
                            }
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            if (apiCallback != null) {
                                apiCallback.onMatrixError(matrixError);
                            }
                        }

                        public void onUnexpectedError(Exception exc) {
                            if (apiCallback != null) {
                                apiCallback.onUnexpectedError(exc);
                            }
                        }
                    });
                } catch (Exception e) {
                    if (apiCallback != null) {
                        apiCallback.onMatrixError(new MatrixError(null, e.getLocalizedMessage()));
                    }
                }
            }

            public void onNetworkError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                if (apiCallback != null) {
                    apiCallback.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                if (apiCallback != null) {
                    apiCallback.onUnexpectedError(exc);
                }
            }
        });
    }

    public static void displayUnknownDevicesDialog(MXSession mXSession, FragmentActivity fragmentActivity, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap, IUnknownDevicesSendAnywayListener iUnknownDevicesSendAnywayListener) {
        if (!fragmentActivity.isFinishing() && mXUsersDevicesMap != null && mXUsersDevicesMap.getMap().size() != 0) {
            FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
            VectorUnknownDevicesFragment vectorUnknownDevicesFragment = (VectorUnknownDevicesFragment) supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_UNKNOWN_DEVICES_DIALOG_DIALOG);
            if (vectorUnknownDevicesFragment != null) {
                vectorUnknownDevicesFragment.dismissAllowingStateLoss();
            }
            try {
                VectorUnknownDevicesFragment.newInstance(mXSession.getMyUserId(), mXUsersDevicesMap, iUnknownDevicesSendAnywayListener).show(supportFragmentManager, TAG_FRAGMENT_UNKNOWN_DEVICES_DIALOG_DIALOG);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## displayUnknownDevicesDialog() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public static void tintMenuIcons(Menu menu, int i) {
        for (int i2 = 0; i2 < menu.size(); i2++) {
            MenuItem item = menu.getItem(i2);
            Drawable icon = item.getIcon();
            if (icon != null) {
                Drawable wrap = DrawableCompat.wrap(icon);
                icon.mutate();
                DrawableCompat.setTint(wrap, i);
                item.setIcon(icon);
            }
        }
    }

    public static Drawable tintDrawable(Context context, Drawable drawable, @AttrRes int i) {
        return tintDrawableWithColor(drawable, ThemeUtils.INSTANCE.getColor(context, i));
    }

    public static Drawable tintDrawableWithColor(Drawable drawable, @ColorInt int i) {
        Drawable wrap = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTint(wrap, i);
        return wrap;
    }

    public static List<Pair<String, List<MXDeviceInfo>>> getDevicesList(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        ArrayList arrayList = new ArrayList();
        if (mXUsersDevicesMap != null) {
            for (String str : mXUsersDevicesMap.getUserIds()) {
                ArrayList arrayList2 = new ArrayList();
                for (String object : mXUsersDevicesMap.getUserDeviceIds(str)) {
                    arrayList2.add(mXUsersDevicesMap.getObject(object, str));
                }
                arrayList.add(new Pair(str, arrayList2));
            }
        }
        return arrayList;
    }

    public static void verifyUnknownDevices(MXSession mXSession, List<Pair<String, List<MXDeviceInfo>>> list) {
        C136223 r0 = new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(CommonActivityUtils.LOG_TAG, "device verified");
            }

            public void onNetworkError(Exception exc) {
                Log.m209d(CommonActivityUtils.LOG_TAG, "onNetworkError");
            }

            public void onMatrixError(MatrixError matrixError) {
                Log.m209d(CommonActivityUtils.LOG_TAG, "onMatrixError");
            }

            public void onUnexpectedError(Exception exc) {
                Log.m209d(CommonActivityUtils.LOG_TAG, "onUnexpectedError");
            }
        };
        if (list != null) {
            for (Pair pair : list) {
                for (MXDeviceInfo mXDeviceInfo : (List) pair.second) {
                    mXSession.getCrypto().setDeviceVerification(1, mXDeviceInfo.deviceId, mXDeviceInfo.userId, r0);
                }
            }
        }
    }
}
