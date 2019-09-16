package com.opengarden.firechat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.repositories.ServerUrlsRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PreferencesManager {
    private static final String DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY = "DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY";
    public static final String LAST_HEARTBEAT = "LAST_HEARTBEAT";
    private static final String LOG_TAG = "PreferencesManager";
    private static final int MEDIA_SAVING_1_MONTH = 2;
    private static final int MEDIA_SAVING_1_WEEK = 1;
    private static final int MEDIA_SAVING_3_DAYS = 0;
    private static final int MEDIA_SAVING_FOREVER = 3;
    private static final String SETTINGS_12_24_TIMESTAMPS_KEY = "SETTINGS_12_24_TIMESTAMPS_KEY";
    private static final String SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY = "SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY";
    public static final String SETTINGS_APP_TERM_CONDITIONS_PREFERENCE_KEY = "SETTINGS_APP_TERM_CONDITIONS_PREFERENCE_KEY";
    public static final String SETTINGS_BACKGROUND_SYNC_DIVIDER_PREFERENCE_KEY = "SETTINGS_BACKGROUND_SYNC_DIVIDER_PREFERENCE_KEY";
    public static final String SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY = "SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY";
    public static final String SETTINGS_CALL_INVITATIONS_PREFERENCE_KEY = "SETTINGS_CALL_INVITATIONS_PREFERENCE_KEY_2";
    public static final String SETTINGS_CHANGE_PASSWORD_PREFERENCE_KEY = "SETTINGS_CHANGE_PASSWORD_PREFERENCE_KEY";
    public static final String SETTINGS_CLEAR_CACHE_PREFERENCE_KEY = "SETTINGS_CLEAR_CACHE_PREFERENCE_KEY";
    public static final String SETTINGS_CLEAR_MEDIA_CACHE_PREFERENCE_KEY = "SETTINGS_CLEAR_MEDIA_CACHE_PREFERENCE_KEY";
    public static final String SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY = "SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY";
    public static final String SETTINGS_CONTACT_PREFERENCE_KEYS = "SETTINGS_CONTACT_PREFERENCE_KEYS";
    public static final String SETTINGS_CONTAINING_MY_DISPLAY_NAME_PREFERENCE_KEY = "SETTINGS_CONTAINING_MY_DISPLAY_NAME_PREFERENCE_KEY_2";
    public static final String SETTINGS_CONTAINING_MY_USER_NAME_PREFERENCE_KEY = "SETTINGS_CONTAINING_MY_USER_NAME_PREFERENCE_KEY_2";
    public static final String SETTINGS_COPYRIGHT_PREFERENCE_KEY = "SETTINGS_COPYRIGHT_PREFERENCE_KEY";
    public static final String SETTINGS_CRYPTOGRAPHY_DIVIDER_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_DIVIDER_PREFERENCE_KEY";
    public static final String SETTINGS_CRYPTOGRAPHY_PREFERENCE_KEY = "SETTINGS_CRYPTOGRAPHY_PREFERENCE_KEY";
    public static final String SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY = "SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY";
    public static final String SETTINGS_DEACTIVATE_ACCOUNT_KEY = "SETTINGS_DEACTIVATE_ACCOUNT_KEY";
    public static final String SETTINGS_DEVICES_DIVIDER_PREFERENCE_KEY = "SETTINGS_DEVICES_DIVIDER_PREFERENCE_KEY";
    public static final String SETTINGS_DEVICES_LIST_PREFERENCE_KEY = "SETTINGS_DEVICES_LIST_PREFERENCE_KEY";
    private static final String SETTINGS_DISABLE_MARKDOWN_KEY = "SETTINGS_DISABLE_MARKDOWN_KEY";
    private static final String SETTINGS_DISABLE_PIWIK_SETTINGS_PREFERENCE_KEY = "SETTINGS_DISABLE_PIWIK_SETTINGS_PREFERENCE_KEY";
    private static final String SETTINGS_DISPLAY_ALL_EVENTS_KEY = "SETTINGS_DISPLAY_ALL_EVENTS_KEY";
    public static final String SETTINGS_DISPLAY_NAME_PREFERENCE_KEY = "SETTINGS_DISPLAY_NAME_PREFERENCE_KEY";
    private static final String SETTINGS_DONT_SEND_TYPING_NOTIF_KEY = "SETTINGS_DONT_SEND_TYPING_NOTIF_KEY";
    public static final String SETTINGS_ENABLE_ALL_NOTIF_PREFERENCE_KEY = "SETTINGS_ENABLE_ALL_NOTIF_PREFERENCE_KEY";
    public static final String SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY = "SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY";
    public static final String SETTINGS_ENABLE_CONTENT_SENDING_PREFERENCE_KEY = "SETTINGS_ENABLE_CONTENT_SENDING_PREFERENCE_KEY";
    public static final String SETTINGS_ENABLE_OFFLINE_MESSAGING = "SETTINGS_ENABLE_OFFLINE_MESSAGING";
    public static final String SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY = "SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_EXPORT_E2E_ROOM_KEYS_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_EXPORT_E2E_ROOM_KEYS_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_IMPORT_E2E_ROOM_KEYS_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_IMPORT_E2E_ROOM_KEYS_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_INFORMATION_DEVICE_ID_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_INFORMATION_DEVICE_ID_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_INFORMATION_DEVICE_KEY_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_INFORMATION_DEVICE_KEY_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_INFORMATION_DEVICE_NAME_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_INFORMATION_DEVICE_NAME_PREFERENCE_KEY";
    public static final String SETTINGS_ENCRYPTION_NEVER_SENT_TO_PREFERENCE_KEY = "SETTINGS_ENCRYPTION_NEVER_SENT_TO_PREFERENCE_KEY";
    public static final String SETTINGS_GROUPS_FLAIR_KEY = "SETTINGS_GROUPS_FLAIR_KEY";
    private static final String SETTINGS_HIDE_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY = "SETTINGS_HIDE_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY";
    private static final String SETTINGS_HIDE_JOIN_LEAVE_MESSAGES_KEY = "SETTINGS_HIDE_JOIN_LEAVE_MESSAGES_KEY";
    private static final String SETTINGS_HIDE_READ_RECEIPTS_KEY = "SETTINGS_HIDE_READ_RECEIPTS_KEY";
    public static final String SETTINGS_HOME_SERVER_PREFERENCE_KEY = "SETTINGS_HOME_SERVER_PREFERENCE_KEY";
    public static final String SETTINGS_IDENTITY_SERVER_PREFERENCE_KEY = "SETTINGS_IDENTITY_SERVER_PREFERENCE_KEY";
    public static final String SETTINGS_IGNORED_USERS_PREFERENCE_KEY = "SETTINGS_IGNORED_USERS_PREFERENCE_KEY";
    public static final String SETTINGS_IGNORE_USERS_DIVIDER_PREFERENCE_KEY = "SETTINGS_IGNORE_USERS_DIVIDER_PREFERENCE_KEY";
    public static final String SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY = "SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY";
    public static final String SETTINGS_INTERFACE_TEXT_SIZE_KEY = "SETTINGS_INTERFACE_TEXT_SIZE_KEY";
    public static final String SETTINGS_INVITED_TO_ROOM_PREFERENCE_KEY = "SETTINGS_INVITED_TO_ROOM_PREFERENCE_KEY_2";
    public static final String SETTINGS_LABS_PREFERENCE_KEY = "SETTINGS_LABS_PREFERENCE_KEY";
    public static final String SETTINGS_LOGGED_IN_PREFERENCE_KEY = "SETTINGS_LOGGED_IN_PREFERENCE_KEY";
    public static final String SETTINGS_MEDIA_SAVING_PERIOD_KEY = "SETTINGS_MEDIA_SAVING_PERIOD_KEY";
    private static final String SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY = "SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY";
    public static final String SETTINGS_MESSAGES_IN_GROUP_CHAT_PREFERENCE_KEY = "SETTINGS_MESSAGES_IN_GROUP_CHAT_PREFERENCE_KEY_2";
    public static final String SETTINGS_MESSAGES_IN_ONE_TO_ONE_PREFERENCE_KEY = "SETTINGS_MESSAGES_IN_ONE_TO_ONE_PREFERENCE_KEY_2";
    public static final String SETTINGS_MESSAGES_SENT_BY_BOT_PREFERENCE_KEY = "SETTINGS_MESSAGES_SENT_BY_BOT_PREFERENCE_KEY_2";
    public static final String SETTINGS_NOTIFICATIONS_KEY = "SETTINGS_NOTIFICATIONS_KEY";
    public static final String SETTINGS_NOTIFICATIONS_TARGETS_PREFERENCE_KEY = "SETTINGS_NOTIFICATIONS_TARGETS_PREFERENCE_KEY";
    public static final String SETTINGS_NOTIFICATIONS_TARGET_DIVIDER_PREFERENCE_KEY = "SETTINGS_NOTIFICATIONS_TARGET_DIVIDER_PREFERENCE_KEY";
    public static final String SETTINGS_NOTIFICATION_PRIVACY_PREFERENCE_KEY = "SETTINGS_NOTIFICATION_PRIVACY_PREFERENCE_KEY";
    private static final String SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY = "SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY";
    public static final String SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY = "SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY";
    public static final String SETTINGS_OLM_VERSION_PREFERENCE_KEY = "SETTINGS_OLM_VERSION_PREFERENCE_KEY";
    public static final String SETTINGS_OTHERS_PREFERENCE_KEY = "SETTINGS_OTHERS_PREFERENCE_KEY";
    private static final String SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY = "SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY";
    private static final String SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY = "SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY";
    public static final String SETTINGS_PRIVACY_POLICY_PREFERENCE_KEY = "SETTINGS_PRIVACY_POLICY_PREFERENCE_KEY";
    public static final String SETTINGS_PROFILE_PICTURE_PREFERENCE_KEY = "SETTINGS_PROFILE_PICTURE_PREFERENCE_KEY";
    public static final String SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_IS_ACTIVE_PREFERENCE_KEY = "SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_IS_ACTIVE_PREFERENCE_KEY";
    public static final String SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY = "SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY";
    public static final String SETTINGS_SEND_MESSAGE_ENTER_KEY = "SETTINGS_SEND_MESSAGE_ENTER_KEY";
    public static final String SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY = "SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY";
    public static final String SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY = "SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY";
    public static final String SETTINGS_SHOW_URL_PREVIEW_KEY = "SETTINGS_SHOW_URL_PREVIEW_KEY";
    public static final String SETTINGS_START_ON_BOOT_PREFERENCE_KEY = "SETTINGS_START_ON_BOOT_PREFERENCE_KEY";
    public static final String SETTINGS_THIRD_PARTY_NOTICES_PREFERENCE_KEY = "SETTINGS_THIRD_PARTY_NOTICES_PREFERENCE_KEY";
    public static final String SETTINGS_TURN_SCREEN_ON_PREFERENCE_KEY = "SETTINGS_TURN_SCREEN_ON_PREFERENCE_KEY";
    public static final String SETTINGS_USER_SETTINGS_PREFERENCE_KEY = "SETTINGS_USER_SETTINGS_PREFERENCE_KEY";
    public static final String SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY = "SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY";
    private static final String SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY = "SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY";
    public static final String SETTINGS_USE_RAGE_SHAKE_KEY = "SETTINGS_USE_RAGE_SHAKE_KEY";
    public static final String SETTINGS_VERSION_PREFERENCE_KEY = "SETTINGS_VERSION_PREFERENCE_KEY";
    private static final String SETTINGS_VIBRATE_ON_MENTION_KEY = "SETTINGS_VIBRATE_ON_MENTION_KEY";
    public static final String VERSION_BUILD = "VERSION_BUILD";
    private static final List<String> mKeysToKeepAfterLogout = Arrays.asList(new String[]{SETTINGS_HIDE_READ_RECEIPTS_KEY, SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY, SETTINGS_12_24_TIMESTAMPS_KEY, SETTINGS_DONT_SEND_TYPING_NOTIF_KEY, SETTINGS_HIDE_JOIN_LEAVE_MESSAGES_KEY, SETTINGS_HIDE_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY, SETTINGS_MEDIA_SAVING_PERIOD_KEY, SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY, SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY, SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY, SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY, SETTINGS_START_ON_BOOT_PREFERENCE_KEY, SETTINGS_INTERFACE_TEXT_SIZE_KEY, SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY, SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY, SETTINGS_NOTIFICATION_RINGTONE_SELECTION_PREFERENCE_KEY, SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY, SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY, SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY, SETTINGS_CONTACTS_PHONEBOOK_COUNTRY_PREFERENCE_KEY, SETTINGS_INTERFACE_LANGUAGE_PREFERENCE_KEY, SETTINGS_BACKGROUND_SYNC_PREFERENCE_KEY, SETTINGS_ENABLE_BACKGROUND_SYNC_PREFERENCE_KEY, SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY, SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY, SETTINGS_USE_RAGE_SHAKE_KEY});

    public static void clearPreferences(Context context) {
        HashSet hashSet = new HashSet(mKeysToKeepAfterLogout);
        hashSet.add(ServerUrlsRepository.HOME_SERVER_URL_PREF);
        hashSet.add(ServerUrlsRepository.IDENTITY_SERVER_URL_PREF);
        hashSet.add(ThemeUtils.APPLICATION_THEME_KEY);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor edit = defaultSharedPreferences.edit();
        Set<String> keySet = defaultSharedPreferences.getAll().keySet();
        keySet.removeAll(hashSet);
        for (String remove : keySet) {
            edit.remove(remove);
        }
        edit.commit();
    }

    @SuppressLint({"NewApi"})
    public static boolean useBatteryOptimisation(Context context) {
        if (VERSION.SDK_INT >= 23) {
            return !((PowerManager) context.getSystemService("power")).isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return false;
    }

    public static boolean didAskUserToIgnoreBatteryOptimizations(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY, false);
    }

    public static void setDidAskUserToIgnoreBatteryOptimizations(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(DID_ASK_TO_IGNORE_BATTERY_OPTIMIZATIONS_KEY, true).apply();
    }

    public static boolean displayTimeIn12hFormat(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_12_24_TIMESTAMPS_KEY, false);
    }

    public static boolean hideJoinLeaveMessages(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_HIDE_JOIN_LEAVE_MESSAGES_KEY, false);
    }

    public static boolean hideAvatarDisplayNameChangeMessages(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_HIDE_AVATAR_DISPLAY_NAME_CHANGES_MESSAGES_KEY, false);
    }

    public static boolean useNativeCamera(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_USE_NATIVE_CAMERA_PREFERENCE_KEY, false);
    }

    public static void setNotificationRingTone(Context context, Uri uri) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        String str = "";
        if (uri != null) {
            str = uri.toString();
            if (str.startsWith("file://")) {
                return;
            }
        }
        edit.putString(SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY, str);
        edit.commit();
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getNotificationRingTone(android.content.Context r3) {
        /*
            android.content.SharedPreferences r3 = android.preference.PreferenceManager.getDefaultSharedPreferences(r3)
            java.lang.String r0 = "SETTINGS_NOTIFICATION_RINGTONE_PREFERENCE_KEY"
            r1 = 0
            java.lang.String r3 = r3.getString(r0, r1)
            java.lang.String r0 = ""
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x0014
            return r1
        L_0x0014:
            if (r3 == 0) goto L_0x002a
            java.lang.String r0 = "file://"
            boolean r0 = r3.startsWith(r0)
            if (r0 != 0) goto L_0x002a
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0023 }
            goto L_0x002b
        L_0x0023:
            java.lang.String r3 = LOG_TAG
            java.lang.String r0 = "## getNotificationRingTone() : Uri.parse failed"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r0)
        L_0x002a:
            r3 = r1
        L_0x002b:
            if (r3 != 0) goto L_0x0032
            r3 = 2
            android.net.Uri r3 = android.media.RingtoneManager.getDefaultUri(r3)
        L_0x0032:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## getNotificationRingTone() returns "
            r1.append(r2)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.PreferencesManager.getNotificationRingTone(android.content.Context):android.net.Uri");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004d, code lost:
        r6 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004f, code lost:
        r1 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0050, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0073, code lost:
        r6.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x004c A[ExcHandler: all (th java.lang.Throwable), Splitter:B:7:0x001d] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0079  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getNotificationRingToneName(android.content.Context r8) {
        /*
            android.net.Uri r1 = getNotificationRingTone(r8)
            r6 = 0
            if (r1 != 0) goto L_0x0008
            return r6
        L_0x0008:
            r0 = 1
            java.lang.String[] r2 = new java.lang.String[r0]     // Catch:{ Exception -> 0x0055 }
            java.lang.String r0 = "_data"
            r7 = 0
            r2[r7] = r0     // Catch:{ Exception -> 0x0055 }
            android.content.ContentResolver r0 = r8.getContentResolver()     // Catch:{ Exception -> 0x0055 }
            r3 = 0
            r4 = 0
            r5 = 0
            android.database.Cursor r8 = r0.query(r1, r2, r3, r4, r5)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r0 = "_data"
            int r0 = r8.getColumnIndexOrThrow(r0)     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            r8.moveToFirst()     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            java.lang.String r0 = r8.getString(r0)     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            r1.<init>(r0)     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            java.lang.String r0 = r1.getName()     // Catch:{ Exception -> 0x004f, all -> 0x004c }
            java.lang.String r1 = "."
            boolean r1 = r0.contains(r1)     // Catch:{ Exception -> 0x004a, all -> 0x004c }
            if (r1 == 0) goto L_0x0044
            java.lang.String r1 = "."
            int r1 = r0.lastIndexOf(r1)     // Catch:{ Exception -> 0x004a, all -> 0x004c }
            java.lang.String r1 = r0.substring(r7, r1)     // Catch:{ Exception -> 0x004a, all -> 0x004c }
            r0 = r1
        L_0x0044:
            if (r8 == 0) goto L_0x0076
            r8.close()
            goto L_0x0076
        L_0x004a:
            r1 = move-exception
            goto L_0x0051
        L_0x004c:
            r0 = move-exception
            r6 = r8
            goto L_0x0077
        L_0x004f:
            r1 = move-exception
            r0 = r6
        L_0x0051:
            r6 = r8
            goto L_0x0057
        L_0x0053:
            r0 = move-exception
            goto L_0x0077
        L_0x0055:
            r1 = move-exception
            r0 = r6
        L_0x0057:
            java.lang.String r8 = LOG_TAG     // Catch:{ all -> 0x0053 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0053 }
            r2.<init>()     // Catch:{ all -> 0x0053 }
            java.lang.String r3 = "## getNotificationRingToneName() failed() : "
            r2.append(r3)     // Catch:{ all -> 0x0053 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x0053 }
            r2.append(r1)     // Catch:{ all -> 0x0053 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x0053 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r1)     // Catch:{ all -> 0x0053 }
            if (r6 == 0) goto L_0x0076
            r6.close()
        L_0x0076:
            return r0
        L_0x0077:
            if (r6 == 0) goto L_0x007c
            r6.close()
        L_0x007c:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.PreferencesManager.getNotificationRingToneName(android.content.Context):java.lang.String");
    }

    public static boolean useDataSaveMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY, false);
    }

    public static boolean useJitsiConfCall(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_USE_JITSI_CONF_PREFERENCE_KEY, true);
    }

    public static boolean autoStartOnBoot(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_START_ON_BOOT_PREFERENCE_KEY, false);
    }

    public static void setAutoStartOnBoot(Context context, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(SETTINGS_START_ON_BOOT_PREFERENCE_KEY, z);
        edit.commit();
    }

    public static CharSequence[] getMediasSavingItemsChoicesList(Context context) {
        return new CharSequence[]{context.getString(C1299R.string.media_saving_period_3_days), context.getString(C1299R.string.media_saving_period_1_week), context.getString(C1299R.string.media_saving_period_1_month), context.getString(C1299R.string.media_saving_period_forever)};
    }

    public static int getSelectedMediasSavingPeriod(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY, 1);
    }

    public static void setSelectedMediasSavingPeriod(Context context, int i) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putInt(SETTINGS_MEDIA_SAVING_PERIOD_SELECTED_KEY, i);
        edit.commit();
    }

    public static long getMinMediasLastAccessTime(Context context) {
        switch (getSelectedMediasSavingPeriod(context)) {
            case 0:
                return (System.currentTimeMillis() / 1000) - 259200;
            case 1:
                return (System.currentTimeMillis() / 1000) - 604800;
            case 2:
                return (System.currentTimeMillis() / 1000) - 2592000;
            case 3:
                return 0;
            default:
                return 0;
        }
    }

    public static String getSelectedMediasSavingPeriodString(Context context) {
        switch (getSelectedMediasSavingPeriod(context)) {
            case 0:
                return context.getString(C1299R.string.media_saving_period_3_days);
            case 1:
                return context.getString(C1299R.string.media_saving_period_1_week);
            case 2:
                return context.getString(C1299R.string.media_saving_period_1_month);
            case 3:
                return context.getString(C1299R.string.media_saving_period_forever);
            default:
                return "?";
        }
    }

    public static void fixMigrationIssues(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSharedPreferences.contains(context.getString(C1299R.string.settings_pin_missed_notifications))) {
            Editor edit = defaultSharedPreferences.edit();
            edit.putBoolean(SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY, defaultSharedPreferences.getBoolean(context.getString(C1299R.string.settings_pin_missed_notifications), false));
            edit.remove(context.getString(C1299R.string.settings_pin_missed_notifications));
            edit.commit();
        }
        if (defaultSharedPreferences.contains(context.getString(C1299R.string.settings_pin_unread_messages))) {
            Editor edit2 = defaultSharedPreferences.edit();
            edit2.putBoolean(SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY, defaultSharedPreferences.getBoolean(context.getString(C1299R.string.settings_pin_unread_messages), false));
            edit2.remove(context.getString(C1299R.string.settings_pin_unread_messages));
            edit2.commit();
        }
        if (defaultSharedPreferences.contains("MARKDOWN_PREFERENCE_KEY")) {
            Editor edit3 = defaultSharedPreferences.edit();
            edit3.putBoolean(SETTINGS_DISABLE_MARKDOWN_KEY, !defaultSharedPreferences.getBoolean("MARKDOWN_PREFERENCE_KEY", false));
            edit3.remove("MARKDOWN_PREFERENCE_KEY");
            edit3.commit();
        }
    }

    public static boolean isMarkdownEnabled(Context context) {
        return !PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_DISABLE_MARKDOWN_KEY, false);
    }

    public static void setMarkdownEnabled(Context context, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(SETTINGS_DISABLE_MARKDOWN_KEY, !z);
        edit.commit();
    }

    public static boolean hideReadReceipts(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_HIDE_READ_RECEIPTS_KEY, false);
    }

    public static boolean alwaysShowTimeStamps(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_ALWAYS_SHOW_TIMESTAMPS_KEY, false);
    }

    public static boolean dontSendTypingNotifs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_DONT_SEND_TYPING_NOTIF_KEY, false);
    }

    public static boolean pinMissedNotifications(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_PIN_MISSED_NOTIFICATIONS_PREFERENCE_KEY, true);
    }

    public static boolean pinUnreadMessages(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_PIN_UNREAD_MESSAGES_PREFERENCE_KEY, true);
    }

    public static boolean vibrateWhenMentioning(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_VIBRATE_ON_MENTION_KEY, false);
    }

    public static boolean useRageshake(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_USE_RAGE_SHAKE_KEY, true);
    }

    public static void setUseRageshake(Context context, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(SETTINGS_USE_RAGE_SHAKE_KEY, z);
        edit.commit();
    }

    public static boolean useEnterKeyToSendMessage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_SEND_MESSAGE_ENTER_KEY, false);
    }

    public static void setUseEnterKeyToSendMessage(Context context, boolean z) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(SETTINGS_SEND_MESSAGE_ENTER_KEY, z).apply();
    }

    public static boolean displayAllEvents(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_DISPLAY_ALL_EVENTS_KEY, false);
    }

    public static boolean getOfflinePreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_ENABLE_OFFLINE_MESSAGING, true);
    }

    public static void setOfflinePreference(Context context, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(SETTINGS_ENABLE_OFFLINE_MESSAGING, z);
        edit.commit();
    }

    public static long getLastHeartbeat(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_HEARTBEAT, -1);
    }

    public static void setLastHeartbeat(Context context, Long l) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putLong(LAST_HEARTBEAT, l.longValue());
        edit.commit();
    }
}
