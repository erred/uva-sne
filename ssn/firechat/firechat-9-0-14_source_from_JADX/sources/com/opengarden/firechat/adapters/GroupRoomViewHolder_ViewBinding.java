package com.opengarden.firechat.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupRoomViewHolder_ViewBinding implements Unbinder {
    private GroupRoomViewHolder target;

    @UiThread
    public GroupRoomViewHolder_ViewBinding(GroupRoomViewHolder groupRoomViewHolder, View view) {
        this.target = groupRoomViewHolder;
        groupRoomViewHolder.vContactAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_avatar, "field 'vContactAvatar'", ImageView.class);
        groupRoomViewHolder.vContactName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_name, "field 'vContactName'", TextView.class);
        groupRoomViewHolder.vContactDesc = (TextView) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.contact_desc, "field 'vContactDesc'", TextView.class);
    }

    @CallSuper
    public void unbind() {
        GroupRoomViewHolder groupRoomViewHolder = this.target;
        if (groupRoomViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupRoomViewHolder.vContactAvatar = null;
        groupRoomViewHolder.vContactName = null;
        groupRoomViewHolder.vContactDesc = null;
    }
}
