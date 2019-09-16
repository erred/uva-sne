package com.opengarden.firechat.adapters;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class RoomInvitationViewHolder_ViewBinding extends RoomViewHolder_ViewBinding {
    private RoomInvitationViewHolder target;

    @UiThread
    public RoomInvitationViewHolder_ViewBinding(RoomInvitationViewHolder roomInvitationViewHolder, View view) {
        super(roomInvitationViewHolder, view);
        this.target = roomInvitationViewHolder;
        roomInvitationViewHolder.vRejectButton = (Button) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recents_invite_reject_button, "field 'vRejectButton'", Button.class);
        roomInvitationViewHolder.vPreViewButton = (Button) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recents_invite_preview_button, "field 'vPreViewButton'", Button.class);
    }

    public void unbind() {
        RoomInvitationViewHolder roomInvitationViewHolder = this.target;
        if (roomInvitationViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        roomInvitationViewHolder.vRejectButton = null;
        roomInvitationViewHolder.vPreViewButton = null;
        super.unbind();
    }
}
