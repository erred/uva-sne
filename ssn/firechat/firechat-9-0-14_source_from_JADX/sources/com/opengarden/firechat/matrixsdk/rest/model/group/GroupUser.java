package com.opengarden.firechat.matrixsdk.rest.model.group;

import java.io.Serializable;

public class GroupUser implements Serializable {
    public String avatarUrl;
    public String displayname;
    public Boolean isPrivileged;
    public Boolean isPublic;
    public String userId;

    public String getDisplayname() {
        return this.displayname != null ? this.displayname : this.userId;
    }
}
