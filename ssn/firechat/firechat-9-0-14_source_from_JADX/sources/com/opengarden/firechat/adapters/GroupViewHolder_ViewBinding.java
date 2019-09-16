package com.opengarden.firechat.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupViewHolder_ViewBinding implements Unbinder {
    private GroupViewHolder target;

    @UiThread
    public GroupViewHolder_ViewBinding(GroupViewHolder groupViewHolder, View view) {
        this.target = groupViewHolder;
        groupViewHolder.vGroupAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.room_avatar, "field 'vGroupAvatar'", ImageView.class);
        groupViewHolder.vGroupName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_name, "field 'vGroupName'", TextView.class);
        groupViewHolder.vGroupTopic = (TextView) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.group_topic, "field 'vGroupTopic'", TextView.class);
        groupViewHolder.vGroupMembersCount = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_members_count, "field 'vGroupMembersCount'", TextView.class);
        groupViewHolder.vGroupMoreActionClickArea = view.findViewById(C1299R.C1301id.group_more_action_click_area);
        groupViewHolder.vGroupMoreActionAnchor = view.findViewById(C1299R.C1301id.group_more_action_anchor);
    }

    @CallSuper
    public void unbind() {
        GroupViewHolder groupViewHolder = this.target;
        if (groupViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupViewHolder.vGroupAvatar = null;
        groupViewHolder.vGroupName = null;
        groupViewHolder.vGroupTopic = null;
        groupViewHolder.vGroupMembersCount = null;
        groupViewHolder.vGroupMoreActionClickArea = null;
        groupViewHolder.vGroupMoreActionAnchor = null;
    }
}
