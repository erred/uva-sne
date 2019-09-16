package com.opengarden.firechat.matrixsdk.rest.model.group;

import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;

public class GroupRoom extends PublicRoom {
    public String getDisplayName() {
        if (!TextUtils.isEmpty(this.name)) {
            return this.name;
        }
        if (!TextUtils.isEmpty(getAlias())) {
            return getAlias();
        }
        return this.roomId;
    }
}
