package com.opengarden.firechat.preference;

import android.content.Context;
import android.util.AttributeSet;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.util.VectorUtils;

public class RoomAvatarPreference extends UserAvatarPreference {
    private Room mRoom;

    public RoomAvatarPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public RoomAvatarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public RoomAvatarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public void refreshAvatar() {
        if (this.mAvatarView != null && this.mRoom != null) {
            VectorUtils.loadRoomAvatar(this.mContext, this.mSession, this.mAvatarView, this.mRoom);
        }
    }

    public void setConfiguration(MXSession mXSession, Room room) {
        this.mSession = mXSession;
        this.mRoom = room;
        refreshAvatar();
    }
}
