package com.opengarden.firechat.notifications;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.widget.ImageView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.LockScreenActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.VectorUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RoomsNotifications implements Parcelable {
    public static final Creator<RoomsNotifications> CREATOR = new Creator<RoomsNotifications>() {
        public RoomsNotifications createFromParcel(Parcel parcel) {
            RoomsNotifications roomsNotifications = new RoomsNotifications();
            roomsNotifications.init(parcel);
            return roomsNotifications;
        }

        public RoomsNotifications[] newArray(int i) {
            return new RoomsNotifications[i];
        }
    };
    private static final String LOG_TAG = "RoomsNotifications";
    static final int MAX_NUMBER_NOTIFICATION_LINES = 10;
    private static final String ROOMS_NOTIFICATIONS_FILE_NAME = "ROOMS_NOTIFICATIONS_FILE_NAME";
    String mContentText;
    String mContentTitle;
    long mContentTs;
    private Context mContext;
    private Event mEvent;
    private NotifiedEvent mEventToNotify;
    boolean mIsInvitationEvent;
    private Map<String, List<NotifiedEvent>> mNotifiedEventsByRoomId;
    String mQuickReplyBody;
    List<CharSequence> mReversedMessagesList;
    private Room mRoom;
    String mRoomAvatarPath;
    String mRoomId;
    List<RoomNotifications> mRoomNotifications;
    String mSenderName;
    private MXSession mSession;
    String mSessionId;
    String mSummaryText;
    String mWearableMessage;

    public int describeContents() {
        return 0;
    }

    public RoomsNotifications() {
        this.mSessionId = "";
        this.mRoomId = "";
        this.mSummaryText = "";
        this.mQuickReplyBody = "";
        this.mWearableMessage = "";
        this.mIsInvitationEvent = false;
        this.mRoomAvatarPath = "";
        this.mContentTs = -1;
        this.mContentTitle = "";
        this.mContentText = "";
        this.mSenderName = "";
        this.mRoomNotifications = new ArrayList();
        this.mReversedMessagesList = new ArrayList();
    }

    public RoomsNotifications(NotifiedEvent notifiedEvent, Map<String, List<NotifiedEvent>> map) {
        this.mSessionId = "";
        this.mRoomId = "";
        this.mSummaryText = "";
        this.mQuickReplyBody = "";
        this.mWearableMessage = "";
        this.mIsInvitationEvent = false;
        this.mRoomAvatarPath = "";
        this.mContentTs = -1;
        this.mContentTitle = "";
        this.mContentText = "";
        this.mSenderName = "";
        this.mRoomNotifications = new ArrayList();
        this.mReversedMessagesList = new ArrayList();
        this.mContext = VectorApp.getInstance();
        this.mSession = Matrix.getInstance(this.mContext).getDefaultSession();
        IMXStore store = this.mSession.getDataHandler().getStore();
        this.mEventToNotify = notifiedEvent;
        this.mNotifiedEventsByRoomId = map;
        this.mSessionId = this.mSession.getMyUserId();
        this.mRoomId = notifiedEvent.mRoomId;
        this.mRoom = store.getRoom(this.mEventToNotify.mRoomId);
        this.mEvent = store.getEvent(this.mEventToNotify.mEventId, this.mEventToNotify.mRoomId);
        if (this.mRoom == null || this.mEvent == null) {
            if (this.mRoom == null) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## RoomsNotifications() : null room ");
                sb.append(this.mEventToNotify.mRoomId);
                Log.m211e(str, sb.toString());
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## RoomsNotifications() : null event ");
                sb2.append(this.mEventToNotify.mEventId);
                sb2.append(StringUtils.SPACE);
                sb2.append(this.mEventToNotify.mRoomId);
                Log.m211e(str2, sb2.toString());
            }
            return;
        }
        this.mIsInvitationEvent = false;
        RiotEventDisplay riotEventDisplay = new RiotEventDisplay(this.mContext, this.mEvent, this.mRoom.getLiveState());
        boolean z = true;
        riotEventDisplay.setPrependMessagesWithAuthor(true);
        CharSequence textualDisplay = riotEventDisplay.getTextualDisplay();
        String charSequence = !TextUtils.isEmpty(textualDisplay) ? textualDisplay.toString() : "";
        if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(this.mEvent.getType())) {
            try {
                this.mIsInvitationEvent = "invite".equals(this.mEvent.getContentAsJsonObject().getAsJsonPrimitive("membership").getAsString());
            } catch (Exception unused) {
                Log.m211e(LOG_TAG, "RoomsNotifications : invitation parsing failed");
            }
        }
        if (!this.mIsInvitationEvent) {
            int dimensionPixelSize = this.mContext.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size);
            File thumbnailCacheFile = this.mSession.getMediasCache().thumbnailCacheFile(this.mRoom.getAvatarUrl(), dimensionPixelSize);
            if (thumbnailCacheFile != null) {
                this.mRoomAvatarPath = thumbnailCacheFile.getPath();
            } else {
                this.mSession.getMediasCache().loadAvatarThumbnail(this.mSession.getHomeServerConfig(), new ImageView(this.mContext), this.mRoom.getAvatarUrl(), dimensionPixelSize);
            }
        }
        String roomName = getRoomName(this.mContext, this.mSession, this.mRoom, this.mEvent);
        this.mContentTs = this.mEvent.getOriginServerTs();
        this.mContentTitle = roomName;
        this.mContentText = charSequence;
        RoomMember member = this.mRoom.getMember(this.mEvent.getSender());
        this.mSenderName = member == null ? this.mEvent.getSender() : member.getName();
        if (this.mNotifiedEventsByRoomId.size() != 1) {
            z = false;
        }
        if (z) {
            initSingleRoom();
        } else {
            initMultiRooms();
        }
    }

    private void initSingleRoom() {
        RoomNotifications roomNotifications = new RoomNotifications();
        this.mRoomNotifications.add(roomNotifications);
        roomNotifications.mRoomId = this.mEvent.roomId;
        roomNotifications.mRoomName = this.mContentTitle;
        List<NotifiedEvent> list = (List) this.mNotifiedEventsByRoomId.get(roomNotifications.mRoomId);
        int size = list.size();
        Collections.reverse(list);
        if (list.size() > 10) {
            list = list.subList(0, 10);
        }
        IMXStore store = this.mSession.getDataHandler().getStore();
        for (NotifiedEvent notifiedEvent : list) {
            RiotEventDisplay riotEventDisplay = new RiotEventDisplay(this.mContext, store.getEvent(notifiedEvent.mEventId, notifiedEvent.mRoomId), this.mRoom.getLiveState());
            riotEventDisplay.setPrependMessagesWithAuthor(true);
            CharSequence textualDisplay = riotEventDisplay.getTextualDisplay();
            if (!TextUtils.isEmpty(textualDisplay)) {
                this.mReversedMessagesList.add(textualDisplay);
            }
        }
        int size2 = list.size();
        if (size > 10) {
            this.mSummaryText = this.mContext.getResources().getQuantityString(C1299R.plurals.notification_unread_notified_messages, size, new Object[]{Integer.valueOf(size)});
        }
        if (!LockScreenActivity.isDisplayingALockScreenActivity() && !this.mIsInvitationEvent) {
            Event event = store.getEvent(this.mEventToNotify.mEventId, this.mEventToNotify.mRoomId);
            RoomMember member = this.mRoom.getMember(event.getSender());
            roomNotifications.mSenderName = member == null ? event.getSender() : member.getName();
            RiotEventDisplay riotEventDisplay2 = new RiotEventDisplay(this.mContext, event, this.mRoom.getLiveState());
            riotEventDisplay2.setPrependMessagesWithAuthor(false);
            CharSequence textualDisplay2 = riotEventDisplay2.getTextualDisplay();
            this.mQuickReplyBody = !TextUtils.isEmpty(textualDisplay2) ? textualDisplay2.toString() : "";
        }
        initWearableMessage(this.mContext, this.mRoom, store.getEvent(((NotifiedEvent) list.get(list.size() - 1)).mEventId, roomNotifications.mRoomId), this.mIsInvitationEvent);
    }

    private void initMultiRooms() {
        String str;
        String str2;
        IMXStore store = this.mSession.getDataHandler().getStore();
        int i = 0;
        int i2 = 0;
        for (String str3 : this.mNotifiedEventsByRoomId.keySet()) {
            Room room = this.mSession.getDataHandler().getRoom(str3);
            String roomName = getRoomName(this.mContext, this.mSession, room, null);
            List list = (List) this.mNotifiedEventsByRoomId.get(str3);
            Event event = store.getEvent(((NotifiedEvent) list.get(list.size() - 1)).mEventId, str3);
            RiotEventDisplay riotEventDisplay = new RiotEventDisplay(this.mContext, event, room.getLiveState());
            riotEventDisplay.setPrependMessagesWithAuthor(false);
            if (room.isInvited()) {
                StringBuilder sb = new StringBuilder();
                sb.append(roomName);
                sb.append(": ");
                String sb2 = sb.toString();
                CharSequence textualDisplay = riotEventDisplay.getTextualDisplay();
                String charSequence = !TextUtils.isEmpty(textualDisplay) ? textualDisplay.toString() : "";
                str = sb2;
                str2 = charSequence;
            } else if (1 == list.size()) {
                RiotEventDisplay riotEventDisplay2 = new RiotEventDisplay(this.mContext, event, room.getLiveState());
                riotEventDisplay2.setPrependMessagesWithAuthor(false);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(roomName);
                sb3.append(": ");
                sb3.append(room.getLiveState().getMemberName(event.getSender()));
                sb3.append(StringUtils.SPACE);
                str = sb3.toString();
                CharSequence textualDisplay2 = riotEventDisplay2.getTextualDisplay();
                str2 = !TextUtils.isEmpty(textualDisplay2) ? textualDisplay2.toString() : "";
            } else {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(roomName);
                sb4.append(": ");
                str = sb4.toString();
                str2 = this.mContext.getResources().getQuantityString(C1299R.plurals.notification_unread_notified_messages, list.size(), new Object[]{Integer.valueOf(list.size())});
            }
            if (!TextUtils.isEmpty(str2)) {
                RoomNotifications roomNotifications = new RoomNotifications();
                this.mRoomNotifications.add(roomNotifications);
                roomNotifications.mRoomId = str3;
                roomNotifications.mLatestEventTs = event.getOriginServerTs();
                roomNotifications.mMessageHeader = str;
                StringBuilder sb5 = new StringBuilder();
                sb5.append(str);
                sb5.append(str2);
                roomNotifications.mMessagesSummary = sb5.toString();
                i += list.size();
                i2++;
            }
        }
        Collections.sort(this.mRoomNotifications, RoomNotifications.mRoomNotificationsComparator);
        if (this.mRoomNotifications.size() > 10) {
            this.mRoomNotifications = this.mRoomNotifications.subList(0, 10);
        }
        this.mSummaryText = this.mContext.getString(C1299R.string.notification_unread_notified_messages_in_room, new Object[]{this.mContext.getResources().getQuantityString(C1299R.plurals.notification_unread_notified_messages_in_room_msgs, i, new Object[]{Integer.valueOf(i)}), this.mContext.getResources().getQuantityString(C1299R.plurals.notification_unread_notified_messages_in_room_rooms, i2, new Object[]{Integer.valueOf(i2)})});
    }

    private void initWearableMessage(Context context, Room room, Event event, boolean z) {
        if (!z && event != null && room != null) {
            String roomName = getRoomName(context, Matrix.getInstance(context).getDefaultSession(), room, null);
            RiotEventDisplay riotEventDisplay = new RiotEventDisplay(context, event, room.getLiveState());
            riotEventDisplay.setPrependMessagesWithAuthor(false);
            StringBuilder sb = new StringBuilder();
            sb.append(roomName);
            sb.append(": ");
            sb.append(room.getLiveState().getMemberName(event.getSender()));
            sb.append(StringUtils.SPACE);
            this.mWearableMessage = sb.toString();
            CharSequence textualDisplay = riotEventDisplay.getTextualDisplay();
            if (!TextUtils.isEmpty(textualDisplay)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mWearableMessage);
                sb2.append(textualDisplay.toString());
                this.mWearableMessage = sb2.toString();
            }
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mSessionId);
        parcel.writeString(this.mRoomId);
        parcel.writeString(this.mSummaryText);
        parcel.writeString(this.mQuickReplyBody);
        parcel.writeString(this.mWearableMessage);
        parcel.writeInt(this.mIsInvitationEvent ? 1 : 0);
        parcel.writeString(this.mRoomAvatarPath);
        parcel.writeLong(this.mContentTs);
        parcel.writeString(this.mContentTitle);
        parcel.writeString(this.mContentText);
        parcel.writeString(this.mSenderName);
        RoomNotifications[] roomNotificationsArr = new RoomNotifications[this.mRoomNotifications.size()];
        this.mRoomNotifications.toArray(roomNotificationsArr);
        parcel.writeArray(roomNotificationsArr);
        parcel.writeInt(this.mReversedMessagesList.size());
        for (CharSequence writeToParcel : this.mReversedMessagesList) {
            TextUtils.writeToParcel(writeToParcel, parcel, 0);
        }
    }

    /* access modifiers changed from: private */
    public void init(Parcel parcel) {
        this.mSessionId = parcel.readString();
        this.mRoomId = parcel.readString();
        this.mSummaryText = parcel.readString();
        this.mQuickReplyBody = parcel.readString();
        this.mWearableMessage = parcel.readString();
        boolean z = true;
        if (1 != parcel.readInt()) {
            z = false;
        }
        this.mIsInvitationEvent = z;
        this.mRoomAvatarPath = parcel.readString();
        this.mContentTs = parcel.readLong();
        this.mContentTitle = parcel.readString();
        this.mContentText = parcel.readString();
        this.mSenderName = parcel.readString();
        for (Object obj : parcel.readArray(RoomNotifications.class.getClassLoader())) {
            this.mRoomNotifications.add((RoomNotifications) obj);
        }
        int readInt = parcel.readInt();
        this.mReversedMessagesList = new ArrayList();
        for (int i = 0; i < readInt; i++) {
            this.mReversedMessagesList.add(TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel));
        }
    }

    private byte[] marshall() {
        Parcel obtain = Parcel.obtain();
        writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        return marshall;
    }

    private RoomsNotifications(byte[] bArr) {
        this.mSessionId = "";
        this.mRoomId = "";
        this.mSummaryText = "";
        this.mQuickReplyBody = "";
        this.mWearableMessage = "";
        this.mIsInvitationEvent = false;
        this.mRoomAvatarPath = "";
        this.mContentTs = -1;
        this.mContentTitle = "";
        this.mContentText = "";
        this.mSenderName = "";
        this.mRoomNotifications = new ArrayList();
        this.mReversedMessagesList = new ArrayList();
        Parcel obtain = Parcel.obtain();
        obtain.unmarshall(bArr, 0, bArr.length);
        obtain.setDataPosition(0);
        init(obtain);
        obtain.recycle();
    }

    public static void deleteCachedRoomNotifications(Context context) {
        File file = new File(context.getApplicationContext().getCacheDir(), ROOMS_NOTIFICATIONS_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x005d A[SYNTHETIC, Splitter:B:21:0x005d] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0065 A[Catch:{ Exception -> 0x0061 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void saveRoomNotifications(android.content.Context r4, com.opengarden.firechat.notifications.RoomsNotifications r5) {
        /*
            deleteCachedRoomNotifications(r4)
            java.util.List<com.opengarden.firechat.notifications.RoomNotifications> r0 = r5.mRoomNotifications
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x000c
            return
        L_0x000c:
            r0 = 0
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream     // Catch:{ Throwable -> 0x003e }
            byte[] r5 = r5.marshall()     // Catch:{ Throwable -> 0x003e }
            r1.<init>(r5)     // Catch:{ Throwable -> 0x003e }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Throwable -> 0x003b }
            java.io.File r2 = new java.io.File     // Catch:{ Throwable -> 0x003b }
            android.content.Context r4 = r4.getApplicationContext()     // Catch:{ Throwable -> 0x003b }
            java.io.File r4 = r4.getCacheDir()     // Catch:{ Throwable -> 0x003b }
            java.lang.String r3 = "ROOMS_NOTIFICATIONS_FILE_NAME"
            r2.<init>(r4, r3)     // Catch:{ Throwable -> 0x003b }
            r5.<init>(r2)     // Catch:{ Throwable -> 0x003b }
            r4 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r4]     // Catch:{ Throwable -> 0x0039 }
        L_0x002e:
            r2 = 0
            int r3 = r1.read(r0, r2, r4)     // Catch:{ Throwable -> 0x0039 }
            if (r3 <= 0) goto L_0x005b
            r5.write(r0, r2, r3)     // Catch:{ Throwable -> 0x0039 }
            goto L_0x002e
        L_0x0039:
            r4 = move-exception
            goto L_0x0041
        L_0x003b:
            r4 = move-exception
            r5 = r0
            goto L_0x0041
        L_0x003e:
            r4 = move-exception
            r5 = r0
            r1 = r5
        L_0x0041:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## saveRoomNotifications() failed "
            r2.append(r3)
            java.lang.String r4 = r4.getMessage()
            r2.append(r4)
            java.lang.String r4 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r4)
        L_0x005b:
            if (r1 == 0) goto L_0x0063
            r1.close()     // Catch:{ Exception -> 0x0061 }
            goto L_0x0063
        L_0x0061:
            r4 = move-exception
            goto L_0x0069
        L_0x0063:
            if (r5 == 0) goto L_0x0083
            r5.close()     // Catch:{ Exception -> 0x0061 }
            goto L_0x0083
        L_0x0069:
            java.lang.String r5 = LOG_TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "## saveRoomNotifications() failed "
            r0.append(r1)
            java.lang.String r4 = r4.getMessage()
            r0.append(r4)
            java.lang.String r4 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r4)
        L_0x0083:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.notifications.RoomsNotifications.saveRoomNotifications(android.content.Context, com.opengarden.firechat.notifications.RoomsNotifications):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f A[SYNTHETIC, Splitter:B:22:0x005f] */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0067 A[Catch:{ Exception -> 0x0063 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.opengarden.firechat.notifications.RoomsNotifications loadRoomsNotifications(android.content.Context r6) {
        /*
            java.io.File r0 = new java.io.File
            android.content.Context r6 = r6.getApplicationContext()
            java.io.File r6 = r6.getCacheDir()
            java.lang.String r1 = "ROOMS_NOTIFICATIONS_FILE_NAME"
            r0.<init>(r6, r1)
            boolean r6 = r0.exists()
            r1 = 0
            if (r6 != 0) goto L_0x0017
            return r1
        L_0x0017:
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch:{ Throwable -> 0x0040 }
            r6.<init>(r0)     // Catch:{ Throwable -> 0x0040 }
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ Throwable -> 0x003d }
            r0.<init>()     // Catch:{ Throwable -> 0x003d }
            r2 = 1024(0x400, float:1.435E-42)
            byte[] r3 = new byte[r2]     // Catch:{ Throwable -> 0x003b }
        L_0x0025:
            r4 = 0
            int r5 = r6.read(r3, r4, r2)     // Catch:{ Throwable -> 0x003b }
            if (r5 <= 0) goto L_0x0030
            r0.write(r3, r4, r5)     // Catch:{ Throwable -> 0x003b }
            goto L_0x0025
        L_0x0030:
            com.opengarden.firechat.notifications.RoomsNotifications r2 = new com.opengarden.firechat.notifications.RoomsNotifications     // Catch:{ Throwable -> 0x003b }
            byte[] r3 = r0.toByteArray()     // Catch:{ Throwable -> 0x003b }
            r2.<init>(r3)     // Catch:{ Throwable -> 0x003b }
            r1 = r2
            goto L_0x005d
        L_0x003b:
            r2 = move-exception
            goto L_0x0043
        L_0x003d:
            r2 = move-exception
            r0 = r1
            goto L_0x0043
        L_0x0040:
            r2 = move-exception
            r6 = r1
            r0 = r6
        L_0x0043:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## loadRoomsNotifications() failed "
            r4.append(r5)
            java.lang.String r2 = r2.getMessage()
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)
        L_0x005d:
            if (r6 == 0) goto L_0x0065
            r6.close()     // Catch:{ Exception -> 0x0063 }
            goto L_0x0065
        L_0x0063:
            r6 = move-exception
            goto L_0x006b
        L_0x0065:
            if (r0 == 0) goto L_0x0085
            r0.close()     // Catch:{ Exception -> 0x0063 }
            goto L_0x0085
        L_0x006b:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "## loadRoomsNotifications() failed "
            r2.append(r3)
            java.lang.String r6 = r6.getMessage()
            r2.append(r6)
            java.lang.String r6 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r6)
        L_0x0085:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.notifications.RoomsNotifications.loadRoomsNotifications(android.content.Context):com.opengarden.firechat.notifications.RoomsNotifications");
    }

    public static String getRoomName(Context context, MXSession mXSession, Room room, Event event) {
        String roomDisplayName = VectorUtils.getRoomDisplayName(context, mXSession, room);
        if (!TextUtils.equals(roomDisplayName, room.getRoomId())) {
            return roomDisplayName;
        }
        String name = room.getName(mXSession.getMyUserId());
        if (!TextUtils.equals(name, room.getRoomId()) || event == null) {
            return name;
        }
        User user = mXSession.getDataHandler().getStore().getUser(event.sender);
        if (user != null) {
            return user.displayname;
        }
        return event.sender;
    }
}
