package com.opengarden.firechat.adapters;

import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupInvitationViewHolder_ViewBinding extends GroupViewHolder_ViewBinding {
    private GroupInvitationViewHolder target;

    @UiThread
    public GroupInvitationViewHolder_ViewBinding(GroupInvitationViewHolder groupInvitationViewHolder, View view) {
        super(groupInvitationViewHolder, view);
        this.target = groupInvitationViewHolder;
        groupInvitationViewHolder.vRejectButton = (Button) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_invite_reject_button, "field 'vRejectButton'", Button.class);
        groupInvitationViewHolder.vJoinButton = (Button) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_invite_join_button, "field 'vJoinButton'", Button.class);
    }

    public void unbind() {
        GroupInvitationViewHolder groupInvitationViewHolder = this.target;
        if (groupInvitationViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupInvitationViewHolder.vRejectButton = null;
        groupInvitationViewHolder.vJoinButton = null;
        super.unbind();
    }
}
