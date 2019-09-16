package com.opengarden.firechat.notifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.util.Comparator;

public class RoomNotifications implements Parcelable {
    public static final Creator<RoomNotifications> CREATOR = new Creator<RoomNotifications>() {
        public RoomNotifications createFromParcel(Parcel parcel) {
            return new RoomNotifications(parcel);
        }

        public RoomNotifications[] newArray(int i) {
            return new RoomNotifications[i];
        }
    };
    private static final String LOG_TAG = "RoomNotifications";
    static final Comparator<RoomNotifications> mRoomNotificationsComparator = new Comparator<RoomNotifications>() {
        public int compare(RoomNotifications roomNotifications, RoomNotifications roomNotifications2) {
            long j = roomNotifications.mLatestEventTs;
            long j2 = roomNotifications2.mLatestEventTs;
            if (j > j2) {
                return -1;
            }
            return j < j2 ? 1 : 0;
        }
    };
    long mLatestEventTs;
    String mMessageHeader;
    CharSequence mMessagesSummary;
    String mRoomId;
    String mRoomName;
    String mSenderName;
    int mUnreadMessagesCount;

    public int describeContents() {
        return 0;
    }

    public RoomNotifications() {
        this.mRoomId = "";
        this.mRoomName = "";
        this.mMessageHeader = "";
        this.mMessagesSummary = "";
        this.mLatestEventTs = -1;
        this.mSenderName = "";
        this.mUnreadMessagesCount = -1;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mRoomId);
        parcel.writeString(this.mRoomName);
        parcel.writeString(this.mMessageHeader);
        TextUtils.writeToParcel(this.mMessagesSummary, parcel, 0);
        parcel.writeLong(this.mLatestEventTs);
        parcel.writeString(this.mSenderName);
        parcel.writeInt(this.mUnreadMessagesCount);
    }

    private RoomNotifications(Parcel parcel) {
        this.mRoomId = "";
        this.mRoomName = "";
        this.mMessageHeader = "";
        this.mMessagesSummary = "";
        this.mLatestEventTs = -1;
        this.mSenderName = "";
        this.mUnreadMessagesCount = -1;
        this.mRoomId = parcel.readString();
        this.mRoomName = parcel.readString();
        this.mMessageHeader = parcel.readString();
        this.mMessagesSummary = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mLatestEventTs = parcel.readLong();
        this.mSenderName = parcel.readString();
        this.mUnreadMessagesCount = parcel.readInt();
    }
}
