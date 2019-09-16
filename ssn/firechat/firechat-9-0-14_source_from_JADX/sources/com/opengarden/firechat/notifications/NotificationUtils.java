package com.opengarden.firechat.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.StringRes;
import android.support.p000v4.app.NotificationCompat.Action;
import android.support.p000v4.app.NotificationCompat.Builder;
import android.support.p000v4.app.NotificationCompat.InboxStyle;
import android.support.p000v4.app.NotificationCompat.WearableExtender;
import android.support.p000v4.app.NotificationManagerCompat;
import android.support.p000v4.app.TaskStackBuilder;
import android.support.p000v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.JoinRoomActivity;
import com.opengarden.firechat.activity.JoinRoomActivity.Companion;
import com.opengarden.firechat.activity.LockScreenActivity;
import com.opengarden.firechat.activity.VectorFakeRoomPreviewActivity;
import com.opengarden.firechat.activity.VectorHomeActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.PreferencesManager;
import java.util.List;
import java.util.Map;
import java.util.Random;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\r\n\u0002\b\u000b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J \u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u001a\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0001\u0010\u001b\u001a\u00020\nH\u0007J(\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u0004H\u0007J*\u0010 \u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002J\u0018\u0010 \u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010#\u001a\u00020$J:\u0010 \u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\u0018\u0010%\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020(0'0&2\u0006\u0010)\u001a\u00020(2\u0006\u0010#\u001a\u00020$J&\u0010*\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010+\u001a\b\u0012\u0004\u0012\u00020,0'2\u0006\u0010!\u001a\u00020\"J0\u0010-\u001a\u00020\u001a2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00042\u0006\u0010.\u001a\u00020\u00042\u0006\u0010\u001e\u001a\u00020\u00042\u0006\u0010\u001f\u001a\u00020\u0004H\u0007J\u000e\u0010/\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u00100\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u00101\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u000e\u00102\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J(\u00103\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010#\u001a\u00020$2\u0006\u00104\u001a\u00020$H\u0003J\u0016\u00105\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u00106\u001a\u00020\u001aR\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \u0007*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nXT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nXT¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0004X\u000e¢\u0006\u0002\n\u0000¨\u00067"}, mo21251d2 = {"Lcom/opengarden/firechat/notifications/NotificationUtils;", "", "()V", "CALL_NOTIFICATION_CHANNEL_ID", "", "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID", "LOG_TAG", "kotlin.jvm.PlatformType", "NOISY_NOTIFICATION_CHANNEL_ID_BASE", "NOTIFICATION_ID_FOREGROUND_SERVICE", "", "NOTIFICATION_ID_MESSAGES", "QUICK_LAUNCH_ACTION", "SILENT_NOTIFICATION_CHANNEL_ID", "TAP_TO_VIEW_ACTION", "noisyNotificationChannelId", "addTextStyle", "", "context", "Landroid/content/Context;", "builder", "Landroid/support/v4/app/NotificationCompat$Builder;", "roomsNotifications", "Lcom/opengarden/firechat/notifications/RoomsNotifications;", "addTextStyleWithSeveralRooms", "buildForegroundServiceNotification", "Landroid/app/Notification;", "subTitleResId", "buildIncomingCallNotification", "roomName", "matrixId", "callId", "buildMessageNotification", "bingRule", "Lcom/opengarden/firechat/matrixsdk/rest/model/bingrules/BingRule;", "isBackground", "", "notifiedEventsByRoomId", "", "", "Lcom/opengarden/firechat/notifications/NotifiedEvent;", "eventToNotify", "buildMessagesListNotification", "messagesStrings", "", "buildPendingCallNotification", "roomId", "cancelAllNotifications", "cancelNotificationForegroundService", "cancelNotificationMessage", "createNotificationChannels", "manageNotificationSound", "isBing", "showNotificationMessage", "notification", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: NotificationUtils.kt */
public final class NotificationUtils {
    private static final String CALL_NOTIFICATION_CHANNEL_ID = "CALL_NOTIFICATION_CHANNEL_ID";
    public static final NotificationUtils INSTANCE = new NotificationUtils();
    private static final String LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID = "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID";
    private static final String LOG_TAG = "NotificationUtils";
    private static final String NOISY_NOTIFICATION_CHANNEL_ID_BASE = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID_BASE";
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 61;
    private static final int NOTIFICATION_ID_MESSAGES = 60;
    private static final String QUICK_LAUNCH_ACTION = "EventStreamService.QUICK_LAUNCH_ACTION";
    private static final String SILENT_NOTIFICATION_CHANNEL_ID = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID";
    @NotNull
    public static final String TAP_TO_VIEW_ACTION = "EventStreamService.TAP_TO_VIEW_ACTION";
    private static String noisyNotificationChannelId;

    private NotificationUtils() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x008f, code lost:
        if (android.text.TextUtils.equals(r1, r5.toString()) == false) goto L_0x0091;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void createNotificationChannels(@org.jetbrains.annotations.NotNull android.content.Context r9) {
        /*
            r8 = this;
            java.lang.String r0 = "context"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r9, r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x000c
            return
        L_0x000c:
            java.lang.String r0 = "notification"
            java.lang.Object r0 = r9.getSystemService(r0)
            if (r0 != 0) goto L_0x001c
            kotlin.TypeCastException r9 = new kotlin.TypeCastException
            java.lang.String r0 = "null cannot be cast to non-null type android.app.NotificationManager"
            r9.<init>(r0)
            throw r9
        L_0x001c:
            android.app.NotificationManager r0 = (android.app.NotificationManager) r0
            java.lang.String r1 = noisyNotificationChannelId
            r2 = 0
            r3 = 0
            if (r1 != 0) goto L_0x0055
            java.util.List r1 = r0.getNotificationChannels()
            java.util.Iterator r1 = r1.iterator()
        L_0x002c:
            boolean r4 = r1.hasNext()
            if (r4 == 0) goto L_0x0055
            java.lang.Object r4 = r1.next()
            android.app.NotificationChannel r4 = (android.app.NotificationChannel) r4
            java.lang.String r5 = "channel"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r4, r5)
            java.lang.String r5 = r4.getId()
            java.lang.String r6 = "channel.id"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r5, r6)
            java.lang.String r6 = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID_BASE"
            r7 = 2
            boolean r5 = kotlin.text.StringsKt.startsWith$default(r5, r6, r2, r7, r3)
            if (r5 == 0) goto L_0x002c
            java.lang.String r1 = r4.getId()
            noisyNotificationChannelId = r1
        L_0x0055:
            java.lang.String r1 = noisyNotificationChannelId
            r4 = 1
            if (r1 == 0) goto L_0x009b
            java.lang.String r1 = noisyNotificationChannelId
            android.app.NotificationChannel r1 = r0.getNotificationChannel(r1)
            java.lang.String r5 = "channel"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r5)
            android.net.Uri r1 = r1.getSound()
            android.net.Uri r5 = com.opengarden.firechat.util.PreferencesManager.getNotificationRingTone(r9)
            if (r1 != 0) goto L_0x0071
            r6 = 1
            goto L_0x0072
        L_0x0071:
            r6 = 0
        L_0x0072:
            if (r5 != 0) goto L_0x0075
            r2 = 1
        L_0x0075:
            r2 = r2 ^ r6
            if (r2 != 0) goto L_0x0091
            if (r1 == 0) goto L_0x009b
            java.lang.String r1 = r1.toString()
            java.lang.CharSequence r1 = (java.lang.CharSequence) r1
            if (r5 != 0) goto L_0x0085
            kotlin.jvm.internal.Intrinsics.throwNpe()
        L_0x0085:
            java.lang.String r2 = r5.toString()
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            if (r1 != 0) goto L_0x009b
        L_0x0091:
            java.lang.String r1 = noisyNotificationChannelId
            r0.deleteNotificationChannel(r1)
            r1 = r3
            java.lang.String r1 = (java.lang.String) r1
            noisyNotificationChannelId = r1
        L_0x009b:
            java.lang.String r1 = noisyNotificationChannelId
            r2 = 3
            if (r1 != 0) goto L_0x00db
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID_BASE_"
            r1.append(r5)
            long r5 = java.lang.System.currentTimeMillis()
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            noisyNotificationChannelId = r1
            android.app.NotificationChannel r1 = new android.app.NotificationChannel
            java.lang.String r5 = noisyNotificationChannelId
            r6 = 2131689919(0x7f0f01bf, float:1.9008867E38)
            java.lang.String r7 = r9.getString(r6)
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            r1.<init>(r5, r7, r2)
            java.lang.String r5 = r9.getString(r6)
            r1.setDescription(r5)
            android.net.Uri r5 = com.opengarden.firechat.util.PreferencesManager.getNotificationRingTone(r9)
            r1.setSound(r5, r3)
            r1.enableVibration(r4)
            r0.createNotificationChannel(r1)
        L_0x00db:
            java.lang.String r1 = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID"
            android.app.NotificationChannel r1 = r0.getNotificationChannel(r1)
            if (r1 != 0) goto L_0x00f9
            android.app.NotificationChannel r1 = new android.app.NotificationChannel
            java.lang.String r5 = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID"
            r6 = 2131689922(0x7f0f01c2, float:1.9008873E38)
            java.lang.String r6 = r9.getString(r6)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            r1.<init>(r5, r6, r2)
            r1.setSound(r3, r3)
            r0.createNotificationChannel(r1)
        L_0x00f9:
            java.lang.String r1 = "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"
            android.app.NotificationChannel r1 = r0.getNotificationChannel(r1)
            if (r1 != 0) goto L_0x011e
            android.app.NotificationChannel r1 = new android.app.NotificationChannel
            java.lang.String r5 = "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"
            r6 = 2131689917(0x7f0f01bd, float:1.9008863E38)
            java.lang.String r7 = r9.getString(r6)
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            r1.<init>(r5, r7, r4)
            java.lang.String r4 = r9.getString(r6)
            r1.setDescription(r4)
            r1.setSound(r3, r3)
            r0.createNotificationChannel(r1)
        L_0x011e:
            java.lang.String r1 = "CALL_NOTIFICATION_CHANNEL_ID"
            android.app.NotificationChannel r1 = r0.getNotificationChannel(r1)
            if (r1 != 0) goto L_0x0143
            android.app.NotificationChannel r1 = new android.app.NotificationChannel
            java.lang.String r4 = "CALL_NOTIFICATION_CHANNEL_ID"
            r5 = 2131689587(0x7f0f0073, float:1.9008194E38)
            java.lang.String r6 = r9.getString(r5)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            r1.<init>(r4, r6, r2)
            java.lang.String r9 = r9.getString(r5)
            r1.setDescription(r9)
            r1.setSound(r3, r3)
            r0.createNotificationChannel(r1)
        L_0x0143:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.notifications.NotificationUtils.createNotificationChannels(android.content.Context):void");
    }

    @NotNull
    @SuppressLint({"NewApi"})
    public final Notification buildForegroundServiceNotification(@NotNull Context context, @StringRes int i) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intent intent = new Intent(context, VectorHomeActivity.class);
        intent.setFlags(603979776);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 0);
        createNotificationChannels(context);
        Builder contentIntent = new Builder(context, LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID).setWhen(System.currentTimeMillis()).setContentTitle(context.getString(C1299R.string.riot_app_name)).setContentText(context.getString(i)).setSmallIcon(C1299R.C1300drawable.firechat).setContentIntent(activity);
        if (VERSION.SDK_INT >= 16) {
            Intrinsics.checkExpressionValueIsNotNull(contentIntent, "builder");
            contentIntent.setPriority(-2);
        }
        Notification build = contentIntent.build();
        build.flags |= 32;
        if (VERSION.SDK_INT < 23) {
            try {
                build.getClass().getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}).invoke(build, new Object[]{context, context.getString(C1299R.string.riot_app_name), context.getString(i), activity});
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## buildNotification(): Exception - setLatestEventInfo() Msg=");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        Intrinsics.checkExpressionValueIsNotNull(build, "notification");
        return build;
    }

    @NotNull
    @SuppressLint({"NewApi"})
    public final Notification buildIncomingCallNotification(@NotNull Context context, @NotNull String str, @NotNull String str2, @NotNull String str3) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "roomName");
        Intrinsics.checkParameterIsNotNull(str2, "matrixId");
        Intrinsics.checkParameterIsNotNull(str3, "callId");
        createNotificationChannels(context);
        Builder lights = new Builder(context, CALL_NOTIFICATION_CHANNEL_ID).setWhen(System.currentTimeMillis()).setContentTitle(str).setContentText(context.getString(C1299R.string.incoming_call)).setSmallIcon(C1299R.C1300drawable.incoming_call_notification_transparent).setLights(-16711936, 500, 500);
        if (VERSION.SDK_INT >= 16) {
            Intrinsics.checkExpressionValueIsNotNull(lights, "builder");
            lights.setPriority(2);
        }
        lights.setContentIntent(TaskStackBuilder.create(context).addParentStack(VectorHomeActivity.class).addNextIntent(new Intent(context, VectorHomeActivity.class).setFlags(872415232).putExtra(VectorHomeActivity.EXTRA_CALL_SESSION_ID, str2).putExtra(VectorHomeActivity.EXTRA_CALL_ID, str3)).getPendingIntent(new Random().nextInt(1000), 134217728));
        Notification build = lights.build();
        Intrinsics.checkExpressionValueIsNotNull(build, "builder.build()");
        return build;
    }

    @NotNull
    @SuppressLint({"NewApi"})
    public final Notification buildPendingCallNotification(@NotNull Context context, @NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "roomName");
        Intrinsics.checkParameterIsNotNull(str2, "roomId");
        Intrinsics.checkParameterIsNotNull(str3, "matrixId");
        Intrinsics.checkParameterIsNotNull(str4, "callId");
        createNotificationChannels(context);
        Builder smallIcon = new Builder(context, CALL_NOTIFICATION_CHANNEL_ID).setWhen(System.currentTimeMillis()).setContentTitle(str).setContentText(context.getString(C1299R.string.call_in_progress)).setSmallIcon(C1299R.C1300drawable.incoming_call_notification_transparent);
        if (VERSION.SDK_INT >= 16) {
            Intrinsics.checkExpressionValueIsNotNull(smallIcon, "builder");
            smallIcon.setPriority(2);
        }
        smallIcon.setContentIntent(TaskStackBuilder.create(context).addParentStack(VectorRoomActivity.class).addNextIntent(new Intent(context, VectorRoomActivity.class).putExtra("EXTRA_ROOM_ID", str2).putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", str3).putExtra(VectorRoomActivity.EXTRA_START_CALL_ID, str4)).getPendingIntent(new Random().nextInt(1000), 134217728));
        Notification build = smallIcon.build();
        Intrinsics.checkExpressionValueIsNotNull(build, "builder.build()");
        return build;
    }

    private final void addTextStyleWithSeveralRooms(Context context, Builder builder, RoomsNotifications roomsNotifications) {
        Intent intent;
        InboxStyle inboxStyle = new InboxStyle();
        for (RoomNotifications roomNotifications : roomsNotifications.mRoomNotifications) {
            SpannableString spannableString = new SpannableString(roomNotifications.mMessagesSummary);
            spannableString.setSpan(new StyleSpan(1), 0, roomNotifications.mMessageHeader.length(), 33);
            inboxStyle.addLine(spannableString);
        }
        inboxStyle.setBigContentTitle(context.getString(C1299R.string.riot_app_name));
        inboxStyle.setSummaryText(roomsNotifications.mSummaryText);
        builder.setStyle(inboxStyle);
        TaskStackBuilder create = TaskStackBuilder.create(context);
        create.addNextIntentWithParentStack(new Intent(context, VectorHomeActivity.class));
        if (roomsNotifications.mIsInvitationEvent) {
            intent = CommonActivityUtils.buildIntentPreviewRoom(roomsNotifications.mSessionId, roomsNotifications.mRoomId, context, VectorFakeRoomPreviewActivity.class);
        } else {
            Intent intent2 = new Intent(context, VectorRoomActivity.class);
            intent2.putExtra("EXTRA_ROOM_ID", roomsNotifications.mRoomId);
            intent = intent2;
        }
        if (intent == null) {
            Intrinsics.throwNpe();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(TAP_TO_VIEW_ACTION);
        sb.append((int) System.currentTimeMillis());
        intent.setAction(sb.toString());
        create.addNextIntent(intent);
        builder.setContentIntent(create.getPendingIntent(0, 134217728));
        NotificationUtils notificationUtils = this;
        builder.addAction(C1299R.C1300drawable.ic_home_black_24dp, context.getString(C1299R.string.bottom_action_home), TaskStackBuilder.create(context).addNextIntent(new Intent(context, VectorHomeActivity.class)).getPendingIntent(0, 134217728));
    }

    private final void addTextStyle(Context context, Builder builder, RoomsNotifications roomsNotifications) {
        Intent intent;
        if (roomsNotifications.mRoomNotifications.size() != 0) {
            if (roomsNotifications.mRoomNotifications.size() > 1) {
                addTextStyleWithSeveralRooms(context, builder, roomsNotifications);
                return;
            }
            SpannableString spannableString = null;
            InboxStyle inboxStyle = new InboxStyle();
            for (CharSequence spannableString2 : roomsNotifications.mReversedMessagesList) {
                inboxStyle.addLine(new SpannableString(spannableString2));
            }
            inboxStyle.setBigContentTitle(roomsNotifications.mContentTitle);
            roomsNotifications.mReversedMessagesList.size();
            if (!TextUtils.isEmpty(roomsNotifications.mSummaryText)) {
                inboxStyle.setSummaryText(roomsNotifications.mSummaryText);
            }
            builder.setStyle(inboxStyle);
            if (!LockScreenActivity.isDisplayingALockScreenActivity()) {
                if (roomsNotifications.mIsInvitationEvent) {
                    NotificationUtils notificationUtils = this;
                    Companion companion = JoinRoomActivity.Companion;
                    String str = roomsNotifications.mRoomId;
                    Intrinsics.checkExpressionValueIsNotNull(str, "roomsNotifications.mRoomId");
                    String str2 = roomsNotifications.mSessionId;
                    Intrinsics.checkExpressionValueIsNotNull(str2, "roomsNotifications.mSessionId");
                    Intent rejectRoomIntent = companion.getRejectRoomIntent(context, str, str2);
                    StringBuilder sb = new StringBuilder();
                    sb.append(QUICK_LAUNCH_ACTION);
                    sb.append((int) System.currentTimeMillis());
                    rejectRoomIntent.setAction(sb.toString());
                    builder.addAction(C1299R.C1300drawable.vector_notification_reject_invitation, context.getString(C1299R.string.reject), PendingIntent.getActivity(context, 0, rejectRoomIntent, 0));
                    Companion companion2 = JoinRoomActivity.Companion;
                    String str3 = roomsNotifications.mRoomId;
                    Intrinsics.checkExpressionValueIsNotNull(str3, "roomsNotifications.mRoomId");
                    String str4 = roomsNotifications.mSessionId;
                    Intrinsics.checkExpressionValueIsNotNull(str4, "roomsNotifications.mSessionId");
                    Intent joinRoomIntent = companion2.getJoinRoomIntent(context, str3, str4);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(QUICK_LAUNCH_ACTION);
                    sb2.append((int) System.currentTimeMillis());
                    joinRoomIntent.setAction(sb2.toString());
                    builder.addAction(C1299R.C1300drawable.vector_notification_accept_invitation, context.getString(C1299R.string.join), PendingIntent.getActivity(context, 0, joinRoomIntent, 0));
                } else {
                    Intent intent2 = new Intent(context, LockScreenActivity.class);
                    intent2.putExtra(LockScreenActivity.EXTRA_ROOM_ID, roomsNotifications.mRoomId);
                    intent2.putExtra(LockScreenActivity.EXTRA_SENDER_NAME, roomsNotifications.mSenderName);
                    intent2.putExtra(LockScreenActivity.EXTRA_MESSAGE_BODY, roomsNotifications.mQuickReplyBody);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(QUICK_LAUNCH_ACTION);
                    sb3.append((int) System.currentTimeMillis());
                    intent2.setAction(sb3.toString());
                    builder.addAction(C1299R.C1300drawable.vector_notification_quick_reply, context.getString(C1299R.string.action_quick_reply), PendingIntent.getActivity(context, 0, intent2, 0));
                }
                if (roomsNotifications.mIsInvitationEvent) {
                    intent = CommonActivityUtils.buildIntentPreviewRoom(roomsNotifications.mSessionId, roomsNotifications.mRoomId, context, VectorFakeRoomPreviewActivity.class);
                    Intrinsics.checkExpressionValueIsNotNull(intent, "CommonActivityUtils.buil…viewActivity::class.java)");
                } else {
                    intent = new Intent(context, VectorRoomActivity.class);
                    intent.putExtra("EXTRA_ROOM_ID", roomsNotifications.mRoomId);
                }
                StringBuilder sb4 = new StringBuilder();
                sb4.append(TAP_TO_VIEW_ACTION);
                sb4.append((int) System.currentTimeMillis());
                intent.setAction(sb4.toString());
                TaskStackBuilder addNextIntent = TaskStackBuilder.create(context).addNextIntentWithParentStack(new Intent(context, VectorHomeActivity.class)).addNextIntent(intent);
                builder.setContentIntent(addNextIntent.getPendingIntent(0, 134217728));
                builder.addAction(C1299R.C1300drawable.vector_notification_open, context.getString(C1299R.string.action_open), addNextIntent.getPendingIntent(0, 134217728));
                if (!roomsNotifications.mIsInvitationEvent) {
                    try {
                        WearableExtender wearableExtender = new WearableExtender();
                        wearableExtender.addAction(new Action.Builder(C1299R.C1300drawable.firechat, roomsNotifications.mWearableMessage, addNextIntent.getPendingIntent(0, 134217728)).build());
                        builder.extend(wearableExtender);
                    } catch (Exception e) {
                        String str5 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## addTextStyleWithSeveralRooms() : WearableExtender failed ");
                        sb5.append(e.getMessage());
                        Log.m211e(str5, sb5.toString());
                    }
                }
            }
        }
    }

    @SuppressLint({"NewApi"})
    private final void manageNotificationSound(Context context, Builder builder, boolean z, boolean z2) {
        int color = ContextCompat.getColor(context, C1299R.color.vector_fuchsia_color);
        if (z) {
            builder.setPriority(0);
            builder.setColor(0);
        } else if (z2) {
            builder.setPriority(1);
            builder.setColor(color);
        } else {
            builder.setPriority(0);
            builder.setColor(0);
        }
        if (!z) {
            builder.setDefaults(4);
            if (z2 && PreferencesManager.getNotificationRingTone(context) != null) {
                builder.setSound(PreferencesManager.getNotificationRingTone(context));
                if (VERSION.SDK_INT >= 26) {
                    String str = noisyNotificationChannelId;
                    if (str == null) {
                        Intrinsics.throwNpe();
                    }
                    builder.setChannelId(str);
                }
            } else if (VERSION.SDK_INT >= 26) {
                builder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID);
            }
            Matrix instance = Matrix.getInstance(VectorApp.getInstance());
            if (instance == null) {
                Intrinsics.throwNpe();
            }
            GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
            Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "Matrix.getInstance(Vecto…redGCMRegistrationManager");
            if (sharedGCMRegistrationManager.isScreenTurnedOn()) {
                Object systemService = VectorApp.getInstance().getSystemService("power");
                if (systemService == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.os.PowerManager");
                }
                WakeLock newWakeLock = ((PowerManager) systemService).newWakeLock(268435466, "manageNotificationSound");
                newWakeLock.acquire(3000);
                newWakeLock.release();
            }
        } else if (VERSION.SDK_INT >= 26) {
            builder.setChannelId(SILENT_NOTIFICATION_CHANNEL_ID);
        }
    }

    @Nullable
    public final Notification buildMessageNotification(@NotNull Context context, boolean z) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Notification notification = null;
        try {
            RoomsNotifications loadRoomsNotifications = RoomsNotifications.loadRoomsNotifications(context);
            if (loadRoomsNotifications != null) {
                return buildMessageNotification(context, loadRoomsNotifications, new BingRule(), z);
            }
            return notification;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## buildMessageNotification() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return notification;
        }
    }

    @Nullable
    public final Notification buildMessageNotification(@NotNull Context context, @NotNull Map<String, ? extends List<? extends NotifiedEvent>> map, @NotNull NotifiedEvent notifiedEvent, boolean z) {
        Notification notification;
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(map, "notifiedEventsByRoomId");
        Intrinsics.checkParameterIsNotNull(notifiedEvent, "eventToNotify");
        try {
            RoomsNotifications roomsNotifications = new RoomsNotifications(notifiedEvent, map);
            BingRule bingRule = notifiedEvent.mBingRule;
            Intrinsics.checkExpressionValueIsNotNull(bingRule, "eventToNotify.mBingRule");
            notification = buildMessageNotification(context, roomsNotifications, bingRule, z);
            try {
                RoomsNotifications.saveRoomNotifications(context, roomsNotifications);
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
            notification = null;
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## buildMessageNotification() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return notification;
        }
        return notification;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(2:10|11) */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        com.opengarden.firechat.matrixsdk.util.Log.m211e(LOG_TAG, "decodeFile failed with an oom");
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0023 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final android.app.Notification buildMessageNotification(android.content.Context r10, com.opengarden.firechat.notifications.RoomsNotifications r11, com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule r12, boolean r13) {
        /*
            r9 = this;
            r0 = 0
            r1 = r0
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1     // Catch:{ Exception -> 0x00e3 }
            boolean r2 = r11.mIsInvitationEvent     // Catch:{ Exception -> 0x00e3 }
            if (r2 != 0) goto L_0x002a
            java.lang.String r2 = r11.mRoomAvatarPath     // Catch:{ Exception -> 0x00e3 }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x00e3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x00e3 }
            if (r2 != 0) goto L_0x002a
            android.graphics.BitmapFactory$Options r2 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00e3 }
            r2.<init>()     // Catch:{ Exception -> 0x00e3 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x00e3 }
            r2.inPreferredConfig = r3     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = r11.mRoomAvatarPath     // Catch:{ OutOfMemoryError -> 0x0023 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r3, r2)     // Catch:{ OutOfMemoryError -> 0x0023 }
            r1 = r2
            goto L_0x002a
        L_0x0023:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = "decodeFile failed with an oom"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)     // Catch:{ Exception -> 0x00e3 }
        L_0x002a:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x00e3 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e3 }
            r3.<init>()     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r4 = "prepareNotification : with sound "
            r3.append(r4)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r4 = r12.getNotificationSound()     // Catch:{ Exception -> 0x00e3 }
            boolean r4 = com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule.isDefaultNotificationSound(r4)     // Catch:{ Exception -> 0x00e3 }
            r3.append(r4)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x00e3 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r3)     // Catch:{ Exception -> 0x00e3 }
            r9.createNotificationChannels(r10)     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = new android.support.v4.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID"
            r2.<init>(r10, r3)     // Catch:{ Exception -> 0x00e3 }
            long r3 = r11.mContentTs     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setWhen(r3)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = r11.mContentTitle     // Catch:{ Exception -> 0x00e3 }
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setContentTitle(r3)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r3 = r11.mContentText     // Catch:{ Exception -> 0x00e3 }
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setContentText(r3)     // Catch:{ Exception -> 0x00e3 }
            r3 = 2131230865(0x7f080091, float:1.8077795E38)
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setSmallIcon(r3)     // Catch:{ Exception -> 0x00e3 }
            r3 = 2131689982(0x7f0f01fe, float:1.9008995E38)
            java.lang.String r3 = r10.getString(r3)     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setGroup(r3)     // Catch:{ Exception -> 0x00e3 }
            r3 = 1
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setGroupSummary(r3)     // Catch:{ Exception -> 0x00e3 }
            android.content.Context r4 = r10.getApplicationContext()     // Catch:{ Exception -> 0x00e3 }
            r5 = 0
            android.content.Intent r6 = new android.content.Intent     // Catch:{ Exception -> 0x00e3 }
            android.content.Context r7 = r10.getApplicationContext()     // Catch:{ Exception -> 0x00e3 }
            java.lang.Class<com.opengarden.firechat.receiver.DismissNotificationReceiver> r8 = com.opengarden.firechat.receiver.DismissNotificationReceiver.class
            r6.<init>(r7, r8)     // Catch:{ Exception -> 0x00e3 }
            r7 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r5, r6, r7)     // Catch:{ Exception -> 0x00e3 }
            android.support.v4.app.NotificationCompat$Builder r2 = r2.setDeleteIntent(r4)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r4 = "builder"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r4)     // Catch:{ Exception -> 0x00a2 }
            r9.addTextStyle(r10, r2, r11)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x00bd
        L_0x00a2:
            r4 = move-exception
            java.lang.String r5 = LOG_TAG     // Catch:{ Exception -> 0x00e3 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00e3 }
            r6.<init>()     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r7 = "## buildMessageNotification() : addTextStyle failed "
            r6.append(r7)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r4 = r4.getMessage()     // Catch:{ Exception -> 0x00e3 }
            r6.append(r4)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r4 = r6.toString()     // Catch:{ Exception -> 0x00e3 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)     // Catch:{ Exception -> 0x00e3 }
        L_0x00bd:
            java.util.List<com.opengarden.firechat.notifications.RoomNotifications> r11 = r11.mRoomNotifications     // Catch:{ Exception -> 0x00e3 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x00e3 }
            if (r11 != r3) goto L_0x00ce
            if (r1 == 0) goto L_0x00ce
            android.graphics.Bitmap r11 = com.opengarden.firechat.util.BitmapUtilKt.createSquareBitmap(r1)     // Catch:{ Exception -> 0x00e3 }
            r2.setLargeIcon(r11)     // Catch:{ Exception -> 0x00e3 }
        L_0x00ce:
            java.lang.String r11 = "builder"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r11)     // Catch:{ Exception -> 0x00e3 }
            java.lang.String r11 = r12.getNotificationSound()     // Catch:{ Exception -> 0x00e3 }
            boolean r11 = com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule.isDefaultNotificationSound(r11)     // Catch:{ Exception -> 0x00e3 }
            r9.manageNotificationSound(r10, r2, r13, r11)     // Catch:{ Exception -> 0x00e3 }
            android.app.Notification r10 = r2.build()     // Catch:{ Exception -> 0x00e3 }
            return r10
        L_0x00e3:
            r10 = move-exception
            java.lang.String r11 = LOG_TAG
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "## buildMessageNotification() : failed"
            r12.append(r13)
            java.lang.String r10 = r10.getMessage()
            r12.append(r10)
            java.lang.String r10 = r12.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r11, r10)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.notifications.NotificationUtils.buildMessageNotification(android.content.Context, com.opengarden.firechat.notifications.RoomsNotifications, com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule, boolean):android.app.Notification");
    }

    @Nullable
    public final Notification buildMessagesListNotification(@NotNull Context context, @NotNull List<? extends CharSequence> list, @NotNull BingRule bingRule) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(list, "messagesStrings");
        Intrinsics.checkParameterIsNotNull(bingRule, "bingRule");
        try {
            createNotificationChannels(context);
            Builder groupSummary = new Builder(context, SILENT_NOTIFICATION_CHANNEL_ID).setWhen(System.currentTimeMillis()).setContentTitle("").setContentText((CharSequence) list.get(0)).setSmallIcon(C1299R.C1300drawable.firechat).setGroup(context.getString(C1299R.string.riot_app_name)).setGroupSummary(true);
            InboxStyle inboxStyle = new InboxStyle();
            int min = Math.min(10, list.size());
            for (int i = 0; i < min; i++) {
                inboxStyle.addLine((CharSequence) list.get(i));
            }
            inboxStyle.setBigContentTitle(context.getString(C1299R.string.riot_app_name)).setSummaryText(context.getResources().getQuantityString(C1299R.plurals.notification_unread_notified_messages, list.size(), new Object[]{Integer.valueOf(list.size())}));
            groupSummary.setStyle(inboxStyle);
            TaskStackBuilder create = TaskStackBuilder.create(context);
            Intent intent = new Intent(context, VectorHomeActivity.class);
            StringBuilder sb = new StringBuilder();
            sb.append(TAP_TO_VIEW_ACTION);
            sb.append((int) System.currentTimeMillis());
            intent.setAction(sb.toString());
            create.addNextIntent(intent);
            groupSummary.setContentIntent(create.getPendingIntent(0, 134217728));
            Intrinsics.checkExpressionValueIsNotNull(groupSummary, "builder");
            manageNotificationSound(context, groupSummary, false, BingRule.isDefaultNotificationSound(bingRule.getNotificationSound()));
            return groupSummary.build();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## buildMessagesListNotification() : failed");
            sb2.append(e.getMessage());
            Log.m211e(str, sb2.toString());
            return null;
        }
    }

    public final void showNotificationMessage(@NotNull Context context, @NotNull Notification notification) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(notification, "notification");
        NotificationManagerCompat.from(context).notify(60, notification);
    }

    public final void cancelNotificationMessage(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        NotificationManagerCompat.from(context).cancel(60);
    }

    public final void cancelNotificationForegroundService(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        NotificationManagerCompat.from(context).cancel(61);
    }

    public final void cancelAllNotifications(@NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        try {
            NotificationManagerCompat.from(context).cancelAll();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## cancelAllNotifications() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }
}
