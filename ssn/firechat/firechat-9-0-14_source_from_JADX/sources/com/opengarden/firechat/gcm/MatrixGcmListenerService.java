package com.opengarden.firechat.gcm;

import android.os.Handler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonParser;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.Map;

public class MatrixGcmListenerService extends FirebaseMessagingService {
    private static final String LOG_TAG = "MatrixGcmListenerService";
    private Boolean mCheckLaunched = Boolean.valueOf(false);
    private Handler mUIHandler = null;

    private Event parseEvent(Map<String, String> map) {
        if (map == null || !map.containsKey("room_id") || !map.containsKey("event_id")) {
            return null;
        }
        try {
            Event event = new Event();
            event.eventId = (String) map.get("event_id");
            event.sender = (String) map.get(BingRule.KIND_SENDER);
            event.roomId = (String) map.get("room_id");
            event.setType((String) map.get("type"));
            if (map.containsKey("content")) {
                event.updateContent(new JsonParser().parse((String) map.get("content")).getAsJsonObject());
            }
            return event;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("buildEvent fails ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str, sb.toString());
            return null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0086 A[Catch:{ Exception -> 0x0043 }] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x008e A[Catch:{ Exception -> 0x0043 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMessageReceivedInternal(java.util.Map<java.lang.String, java.lang.String> r8) {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            if (r8 == 0) goto L_0x0046
            java.lang.String r2 = "unread"
            boolean r2 = r8.containsKey(r2)     // Catch:{ Exception -> 0x0043 }
            if (r2 == 0) goto L_0x0046
            java.lang.String r2 = "unread"
            boolean r2 = r8.containsKey(r2)     // Catch:{ Exception -> 0x0043 }
            if (r2 == 0) goto L_0x0020
            java.lang.String r0 = "unread"
            java.lang.Object r0 = r8.get(r0)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0043 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0043 }
        L_0x0020:
            java.lang.String r2 = "room_id"
            boolean r2 = r8.containsKey(r2)     // Catch:{ Exception -> 0x0043 }
            if (r2 == 0) goto L_0x0031
            java.lang.String r2 = "room_id"
            java.lang.Object r2 = r8.get(r2)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0043 }
            goto L_0x0032
        L_0x0031:
            r2 = r1
        L_0x0032:
            java.lang.String r3 = "event_id"
            boolean r3 = r8.containsKey(r3)     // Catch:{ Exception -> 0x0043 }
            if (r3 == 0) goto L_0x0047
            java.lang.String r1 = "event_id"
            java.lang.Object r1 = r8.get(r1)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0043 }
            goto L_0x0047
        L_0x0043:
            r8 = move-exception
            goto L_0x01a9
        L_0x0046:
            r2 = r1
        L_0x0047:
            java.lang.String r3 = LOG_TAG     // Catch:{ Exception -> 0x0043 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0043 }
            r4.<init>()     // Catch:{ Exception -> 0x0043 }
            java.lang.String r5 = "## onMessageReceivedInternal() : roomId "
            r4.append(r5)     // Catch:{ Exception -> 0x0043 }
            r4.append(r2)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r5 = " eventId "
            r4.append(r5)     // Catch:{ Exception -> 0x0043 }
            r4.append(r1)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r5 = " unread "
            r4.append(r5)     // Catch:{ Exception -> 0x0043 }
            r4.append(r0)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r3, r4)     // Catch:{ Exception -> 0x0043 }
            android.content.Context r3 = r7.getApplicationContext()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.activity.CommonActivityUtils.updateBadgeCount(r3, r0)     // Catch:{ Exception -> 0x0043 }
            android.content.Context r3 = r7.getApplicationContext()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.Matrix r3 = com.opengarden.firechat.Matrix.getInstance(r3)     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.gcm.GcmRegistrationManager r3 = r3.getSharedGCMRegistrationManager()     // Catch:{ Exception -> 0x0043 }
            boolean r4 = r3.areDeviceNotificationsAllowed()     // Catch:{ Exception -> 0x0043 }
            if (r4 != 0) goto L_0x008e
            java.lang.String r8 = LOG_TAG     // Catch:{ Exception -> 0x0043 }
            java.lang.String r0 = "## onMessageReceivedInternal() : the notifications are disabled"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r8, r0)     // Catch:{ Exception -> 0x0043 }
            return
        L_0x008e:
            boolean r3 = r3.isBackgroundSyncAllowed()     // Catch:{ Exception -> 0x0043 }
            if (r3 != 0) goto L_0x0102
            boolean r3 = com.opengarden.firechat.VectorApp.isAppInBackground()     // Catch:{ Exception -> 0x0043 }
            if (r3 == 0) goto L_0x0102
            com.opengarden.firechat.services.EventStreamService r1 = com.opengarden.firechat.services.EventStreamService.getInstance()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r7.parseEvent(r8)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r4 = "room_name"
            java.lang.Object r4 = r8.get(r4)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0043 }
            if (r4 != 0) goto L_0x00dc
            if (r2 == 0) goto L_0x00dc
            android.content.Context r5 = r7.getApplicationContext()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.Matrix r5 = com.opengarden.firechat.Matrix.getInstance(r5)     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.MXSession r5 = r5.getDefaultSession()     // Catch:{ Exception -> 0x0043 }
            if (r5 == 0) goto L_0x00dc
            com.opengarden.firechat.matrixsdk.MXDataHandler r6 = r5.getDataHandler()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r6 = r6.getStore()     // Catch:{ Exception -> 0x0043 }
            boolean r6 = r6.isReady()     // Catch:{ Exception -> 0x0043 }
            if (r6 == 0) goto L_0x00dc
            com.opengarden.firechat.matrixsdk.MXDataHandler r6 = r5.getDataHandler()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r6 = r6.getStore()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.data.Room r2 = r6.getRoom(r2)     // Catch:{ Exception -> 0x0043 }
            if (r2 == 0) goto L_0x00dc
            java.lang.String r4 = com.opengarden.firechat.util.VectorUtils.getRoomDisplayName(r7, r5, r2)     // Catch:{ Exception -> 0x0043 }
        L_0x00dc:
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0043 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0043 }
            r5.<init>()     // Catch:{ Exception -> 0x0043 }
            java.lang.String r6 = "## onMessageReceivedInternal() : the background sync is disabled with eventStreamService "
            r5.append(r6)     // Catch:{ Exception -> 0x0043 }
            r5.append(r1)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r1)     // Catch:{ Exception -> 0x0043 }
            android.content.Context r1 = r7.getApplicationContext()     // Catch:{ Exception -> 0x0043 }
            java.lang.String r2 = "sender_display_name"
            java.lang.Object r8 = r8.get(r2)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.services.EventStreamService.onStaticNotifiedEvent(r1, r3, r4, r8, r0)     // Catch:{ Exception -> 0x0043 }
            return
        L_0x0102:
            java.lang.Boolean r8 = r7.mCheckLaunched     // Catch:{ Exception -> 0x0043 }
            boolean r8 = r8.booleanValue()     // Catch:{ Exception -> 0x0043 }
            if (r8 != 0) goto L_0x0122
            android.content.Context r8 = r7.getApplicationContext()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.Matrix r8 = com.opengarden.firechat.Matrix.getInstance(r8)     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.MXSession r8 = r8.getDefaultSession()     // Catch:{ Exception -> 0x0043 }
            if (r8 == 0) goto L_0x0122
            com.opengarden.firechat.activity.CommonActivityUtils.startEventStreamService(r7)     // Catch:{ Exception -> 0x0043 }
            r8 = 1
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r8)     // Catch:{ Exception -> 0x0043 }
            r7.mCheckLaunched = r8     // Catch:{ Exception -> 0x0043 }
        L_0x0122:
            if (r1 == 0) goto L_0x01a5
            if (r2 == 0) goto L_0x01a5
            android.content.Context r8 = r7.getApplicationContext()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.Matrix r8 = com.opengarden.firechat.Matrix.getInstance(r8)     // Catch:{ Exception -> 0x018a }
            java.util.ArrayList r8 = r8.getSessions()     // Catch:{ Exception -> 0x018a }
            if (r8 == 0) goto L_0x01a5
            int r0 = r8.size()     // Catch:{ Exception -> 0x018a }
            if (r0 <= 0) goto L_0x01a5
            java.util.Iterator r8 = r8.iterator()     // Catch:{ Exception -> 0x018a }
        L_0x013e:
            boolean r0 = r8.hasNext()     // Catch:{ Exception -> 0x018a }
            if (r0 == 0) goto L_0x01a5
            java.lang.Object r0 = r8.next()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.MXSession r0 = (com.opengarden.firechat.matrixsdk.MXSession) r0     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.MXDataHandler r3 = r0.getDataHandler()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r3 = r3.getStore()     // Catch:{ Exception -> 0x018a }
            boolean r3 = r3.isReady()     // Catch:{ Exception -> 0x018a }
            if (r3 == 0) goto L_0x013e
            com.opengarden.firechat.matrixsdk.MXDataHandler r0 = r0.getDataHandler()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r0 = r0.getStore()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r0.getEvent(r1, r2)     // Catch:{ Exception -> 0x018a }
            if (r0 == 0) goto L_0x013e
            java.lang.String r8 = LOG_TAG     // Catch:{ Exception -> 0x018a }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018a }
            r0.<init>()     // Catch:{ Exception -> 0x018a }
            java.lang.String r3 = "## onMessageReceivedInternal() : ignore the event "
            r0.append(r3)     // Catch:{ Exception -> 0x018a }
            r0.append(r1)     // Catch:{ Exception -> 0x018a }
            java.lang.String r1 = " in room "
            r0.append(r1)     // Catch:{ Exception -> 0x018a }
            r0.append(r2)     // Catch:{ Exception -> 0x018a }
            java.lang.String r1 = "because it is already known"
            r0.append(r1)     // Catch:{ Exception -> 0x018a }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x018a }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r0)     // Catch:{ Exception -> 0x018a }
            return
        L_0x018a:
            r8 = move-exception
            java.lang.String r0 = LOG_TAG     // Catch:{ Exception -> 0x0043 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0043 }
            r1.<init>()     // Catch:{ Exception -> 0x0043 }
            java.lang.String r2 = "## onMessageReceivedInternal() : failed to check if the event was already defined "
            r1.append(r2)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r8 = r8.getMessage()     // Catch:{ Exception -> 0x0043 }
            r1.append(r8)     // Catch:{ Exception -> 0x0043 }
            java.lang.String r8 = r1.toString()     // Catch:{ Exception -> 0x0043 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r8)     // Catch:{ Exception -> 0x0043 }
        L_0x01a5:
            com.opengarden.firechat.activity.CommonActivityUtils.catchupEventStream(r7)     // Catch:{ Exception -> 0x0043 }
            goto L_0x01c3
        L_0x01a9:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## onMessageReceivedInternal() failed : "
            r1.append(r2)
            java.lang.String r8 = r8.getMessage()
            r1.append(r8)
            java.lang.String r8 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r8)
        L_0x01c3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.gcm.MatrixGcmListenerService.onMessageReceivedInternal(java.util.Map):void");
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        final Map data = remoteMessage.getData();
        if (this.mUIHandler == null) {
            this.mUIHandler = new Handler(VectorApp.getInstance().getMainLooper());
        }
        this.mUIHandler.post(new Runnable() {
            public void run() {
                MatrixGcmListenerService.this.onMessageReceivedInternal(data);
            }
        });
    }
}
