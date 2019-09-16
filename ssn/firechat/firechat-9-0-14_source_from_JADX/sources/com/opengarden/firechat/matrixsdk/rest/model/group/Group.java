package com.opengarden.firechat.matrixsdk.rest.model.group;

import android.text.TextUtils;
import java.io.Serializable;
import java.util.Comparator;

public class Group implements Serializable {
    public static final Comparator<Group> mGroupsComparator = new Comparator<Group>() {
        public int compare(Group group, Group group2) {
            return group.getGroupId().compareTo(group2.getGroupId());
        }
    };
    private String mGroupId;
    private GroupUsers mInvitedUsers = new GroupUsers();
    private String mInviter;
    private String mMembership;
    private GroupRooms mRooms = new GroupRooms();
    private GroupSummary mSummary = new GroupSummary();
    private GroupUsers mUsers = new GroupUsers();

    public Group(String str) {
        this.mGroupId = str;
    }

    public String getGroupId() {
        return this.mGroupId;
    }

    public void setGroupProfile(GroupProfile groupProfile) {
        if (this.mSummary == null) {
            this.mSummary = new GroupSummary();
        }
        getGroupSummary().profile = groupProfile;
    }

    public GroupProfile getGroupProfile() {
        if (getGroupSummary() != null) {
            return getGroupSummary().profile;
        }
        return null;
    }

    public String getDisplayName() {
        String str = getGroupProfile() != null ? getGroupProfile().name : null;
        return TextUtils.isEmpty(str) ? getGroupId() : str;
    }

    public String getLongDescription() {
        if (getGroupProfile() != null) {
            return getGroupProfile().longDescription;
        }
        return null;
    }

    public String getAvatarUrl() {
        if (getGroupProfile() != null) {
            return getGroupProfile().avatarUrl;
        }
        return null;
    }

    public String getShortDescription() {
        if (getGroupProfile() != null) {
            return getGroupProfile().shortDescription;
        }
        return null;
    }

    public boolean isPublic() {
        return (getGroupProfile() == null || getGroupProfile().isPublic == null || !getGroupProfile().isPublic.booleanValue()) ? false : true;
    }

    public boolean isInvited() {
        return TextUtils.equals(this.mMembership, "invite");
    }

    public GroupSummary getGroupSummary() {
        return this.mSummary;
    }

    public void setGroupSummary(GroupSummary groupSummary) {
        this.mSummary = groupSummary;
    }

    public GroupRooms getGroupRooms() {
        return this.mRooms;
    }

    public void setGroupRooms(GroupRooms groupRooms) {
        this.mRooms = groupRooms;
    }

    public GroupUsers getGroupUsers() {
        return this.mUsers;
    }

    public void setGroupUsers(GroupUsers groupUsers) {
        this.mUsers = groupUsers;
    }

    public GroupUsers getInvitedGroupUsers() {
        return this.mInvitedUsers;
    }

    public void setInvitedGroupUsers(GroupUsers groupUsers) {
        this.mInvitedUsers = groupUsers;
    }

    public void setMembership(String str) {
        this.mMembership = str;
    }

    public String getMembership() {
        return this.mMembership;
    }

    public String getInviter() {
        return this.mInviter;
    }

    public void setInviter(String str) {
        this.mInviter = str;
    }
}
