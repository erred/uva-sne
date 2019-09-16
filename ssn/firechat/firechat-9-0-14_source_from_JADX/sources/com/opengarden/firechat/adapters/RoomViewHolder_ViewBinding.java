package com.opengarden.firechat.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class RoomViewHolder_ViewBinding implements Unbinder {
    private RoomViewHolder target;

    @UiThread
    public RoomViewHolder_ViewBinding(RoomViewHolder roomViewHolder, View view) {
        this.target = roomViewHolder;
        roomViewHolder.vRoomAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.room_avatar, "field 'vRoomAvatar'", ImageView.class);
        roomViewHolder.vRoomName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.room_name, "field 'vRoomName'", TextView.class);
        roomViewHolder.vRoomNameServer = (TextView) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.room_name_server, "field 'vRoomNameServer'", TextView.class);
        roomViewHolder.vRoomLastMessage = (TextView) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.room_message, "field 'vRoomLastMessage'", TextView.class);
        roomViewHolder.vRoomTimestamp = (TextView) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.room_update_date, "field 'vRoomTimestamp'", TextView.class);
        roomViewHolder.vRoomUnreadIndicator = view.findViewById(C1299R.C1301id.indicator_unread_message);
        roomViewHolder.vRoomUnreadCount = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.room_unread_count, "field 'vRoomUnreadCount'", TextView.class);
        roomViewHolder.mDirectChatIndicator = view.findViewById(C1299R.C1301id.direct_chat_indicator);
        roomViewHolder.vRoomEncryptedIcon = C0487Utils.findRequiredView(view, C1299R.C1301id.room_avatar_encrypted_icon, "field 'vRoomEncryptedIcon'");
        roomViewHolder.vRoomMoreActionClickArea = view.findViewById(C1299R.C1301id.room_more_action_click_area);
        roomViewHolder.vRoomMoreActionAnchor = view.findViewById(C1299R.C1301id.room_more_action_anchor);
    }

    @CallSuper
    public void unbind() {
        RoomViewHolder roomViewHolder = this.target;
        if (roomViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        roomViewHolder.vRoomAvatar = null;
        roomViewHolder.vRoomName = null;
        roomViewHolder.vRoomNameServer = null;
        roomViewHolder.vRoomLastMessage = null;
        roomViewHolder.vRoomTimestamp = null;
        roomViewHolder.vRoomUnreadIndicator = null;
        roomViewHolder.vRoomUnreadCount = null;
        roomViewHolder.mDirectChatIndicator = null;
        roomViewHolder.vRoomEncryptedIcon = null;
        roomViewHolder.vRoomMoreActionClickArea = null;
        roomViewHolder.vRoomMoreActionAnchor = null;
    }
}
