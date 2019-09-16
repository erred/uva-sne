package com.opengarden.firechat.matrixsdk.rest.model.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupUsers implements Serializable {
    public List<GroupUser> chunk;
    private List<GroupUser> mFilteredUsers;
    public Integer totalUserCountEstimate;

    public List<GroupUser> getUsers() {
        if (this.chunk == null) {
            ArrayList arrayList = new ArrayList();
            this.chunk = arrayList;
            this.mFilteredUsers = arrayList;
        } else if (this.mFilteredUsers == null) {
            this.mFilteredUsers = new ArrayList();
            HashMap hashMap = new HashMap();
            for (GroupUser groupUser : this.chunk) {
                if (groupUser.userId != null) {
                    hashMap.put(groupUser.userId, groupUser);
                } else {
                    this.mFilteredUsers.add(groupUser);
                }
            }
            this.mFilteredUsers.addAll(hashMap.values());
        }
        return this.mFilteredUsers;
    }

    public int getEstimatedUsersCount() {
        if (this.totalUserCountEstimate == null) {
            this.totalUserCountEstimate = Integer.valueOf(getUsers().size());
        }
        return this.totalUserCountEstimate.intValue();
    }
}
