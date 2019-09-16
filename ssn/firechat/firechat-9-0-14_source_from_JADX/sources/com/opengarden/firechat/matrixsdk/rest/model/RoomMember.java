package com.opengarden.firechat.matrixsdk.rest.model;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.util.ContentManager;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

public class RoomMember implements Externalizable {
    private static final String LOG_TAG = "RoomMember";
    public static final String MEMBERSHIP_BAN = "ban";
    public static final String MEMBERSHIP_INVITE = "invite";
    public static final String MEMBERSHIP_JOIN = "join";
    public static final String MEMBERSHIP_KICK = "kick";
    public static final String MEMBERSHIP_LEAVE = "leave";
    public static Comparator<RoomMember> alphaComparator = new Comparator<RoomMember>() {
        public int compare(RoomMember roomMember, RoomMember roomMember2) {
            String name = roomMember.getName();
            String name2 = roomMember2.getName();
            if (name == null) {
                return -1;
            }
            if (name2 == null) {
                return 1;
            }
            if (name.startsWith("@")) {
                name = name.substring(1);
            }
            if (name2.startsWith("@")) {
                name2 = name2.substring(1);
            }
            return String.CASE_INSENSITIVE_ORDER.compare(name, name2);
        }
    };
    public String avatarUrl;
    public String displayname;
    public Boolean is_direct;
    private long mOriginServerTs = -1;
    private String mOriginalEventId = null;
    public String mSender;
    public String membership;
    public String reason;
    public Invite thirdPartyInvite;
    private String userId = null;

    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if (objectInput.readBoolean()) {
            this.displayname = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.avatarUrl = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.membership = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.thirdPartyInvite = (Invite) objectInput.readObject();
        }
        if (objectInput.readBoolean()) {
            this.is_direct = Boolean.valueOf(objectInput.readBoolean());
        }
        if (objectInput.readBoolean()) {
            this.userId = objectInput.readUTF();
        }
        this.mOriginServerTs = objectInput.readLong();
        if (objectInput.readBoolean()) {
            this.mOriginalEventId = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.reason = objectInput.readUTF();
        }
        if (objectInput.readBoolean()) {
            this.mSender = objectInput.readUTF();
        }
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        boolean z = false;
        objectOutput.writeBoolean(this.displayname != null);
        if (this.displayname != null) {
            objectOutput.writeUTF(this.displayname);
        }
        objectOutput.writeBoolean(this.avatarUrl != null);
        if (this.avatarUrl != null) {
            objectOutput.writeUTF(this.avatarUrl);
        }
        objectOutput.writeBoolean(this.membership != null);
        if (this.membership != null) {
            objectOutput.writeUTF(this.membership);
        }
        objectOutput.writeBoolean(this.thirdPartyInvite != null);
        if (this.thirdPartyInvite != null) {
            objectOutput.writeObject(this.thirdPartyInvite);
        }
        objectOutput.writeBoolean(this.is_direct != null);
        if (this.is_direct != null) {
            objectOutput.writeBoolean(this.is_direct.booleanValue());
        }
        objectOutput.writeBoolean(this.userId != null);
        if (this.userId != null) {
            objectOutput.writeUTF(this.userId);
        }
        objectOutput.writeLong(this.mOriginServerTs);
        objectOutput.writeBoolean(this.mOriginalEventId != null);
        if (this.mOriginalEventId != null) {
            objectOutput.writeUTF(this.mOriginalEventId);
        }
        objectOutput.writeBoolean(this.reason != null);
        if (this.reason != null) {
            objectOutput.writeUTF(this.reason);
        }
        if (this.mSender != null) {
            z = true;
        }
        objectOutput.writeBoolean(z);
        if (this.mSender != null) {
            objectOutput.writeUTF(this.mSender);
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String str) {
        this.userId = str;
    }

    public void setOriginServerTs(long j) {
        this.mOriginServerTs = j;
    }

    public long getOriginServerTs() {
        return this.mOriginServerTs;
    }

    public void setOriginalEventId(String str) {
        this.mOriginalEventId = str;
    }

    public String getOriginalEventId() {
        return this.mOriginalEventId;
    }

    public String getAvatarUrl() {
        if (this.avatarUrl == null || this.avatarUrl.toLowerCase().startsWith(ContentManager.MATRIX_CONTENT_URI_SCHEME)) {
            return this.avatarUrl;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## getAvatarUrl() : the member ");
        sb.append(this.userId);
        sb.append(" has an invalid avatar url ");
        sb.append(this.avatarUrl);
        Log.m211e(str, sb.toString());
        return null;
    }

    public void setAvatarUrl(String str) {
        this.avatarUrl = str;
    }

    public String getThirdPartyInviteToken() {
        if (this.thirdPartyInvite == null || this.thirdPartyInvite.signed == null) {
            return null;
        }
        return this.thirdPartyInvite.signed.token;
    }

    public boolean matchWithPattern(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim())) {
            return false;
        }
        boolean z = !TextUtils.isEmpty(this.displayname) && this.displayname.toLowerCase().indexOf(str) >= 0;
        if (!z && !TextUtils.isEmpty(this.userId)) {
            z = this.userId.toLowerCase().indexOf(str) >= 0;
        }
        return z;
    }

    public boolean matchWithRegEx(String str) {
        boolean z = false;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.displayname)) {
            z = this.displayname.matches(str);
        }
        if (!z && !TextUtils.isEmpty(this.userId)) {
            z = this.userId.matches(str);
        }
        return z;
    }

    public boolean equals(RoomMember roomMember) {
        if (roomMember == null) {
            return false;
        }
        boolean equals = TextUtils.equals(this.displayname, roomMember.displayname);
        if (equals) {
            equals = TextUtils.equals(this.avatarUrl, roomMember.avatarUrl);
        }
        if (equals) {
            equals = TextUtils.equals(this.membership, roomMember.membership);
        }
        if (equals) {
            equals = TextUtils.equals(this.userId, roomMember.userId);
        }
        return equals;
    }

    public String getName() {
        if (this.displayname != null) {
            return this.displayname;
        }
        if (this.userId != null) {
            return this.userId;
        }
        return null;
    }

    public void prune() {
        this.displayname = null;
        this.avatarUrl = null;
        this.reason = null;
    }

    public RoomMember deepCopy() {
        RoomMember roomMember = new RoomMember();
        roomMember.displayname = this.displayname;
        roomMember.avatarUrl = this.avatarUrl;
        roomMember.membership = this.membership;
        roomMember.userId = this.userId;
        roomMember.mOriginalEventId = this.mOriginalEventId;
        roomMember.mSender = this.mSender;
        roomMember.reason = this.reason;
        return roomMember;
    }

    public boolean kickedOrBanned() {
        return TextUtils.equals(this.membership, MEMBERSHIP_KICK) || TextUtils.equals(this.membership, MEMBERSHIP_BAN);
    }
}
