package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupDetailsHomeFragment_ViewBinding implements Unbinder {
    private GroupDetailsHomeFragment target;

    @UiThread
    public GroupDetailsHomeFragment_ViewBinding(GroupDetailsHomeFragment groupDetailsHomeFragment, View view) {
        this.target = groupDetailsHomeFragment;
        groupDetailsHomeFragment.mGroupAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_avatar, "field 'mGroupAvatar'", ImageView.class);
        groupDetailsHomeFragment.mGroupNameTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_name_text_view, "field 'mGroupNameTextView'", TextView.class);
        groupDetailsHomeFragment.mGroupTopicTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_topic_text_view, "field 'mGroupTopicTextView'", TextView.class);
        groupDetailsHomeFragment.mGroupMembersIconView = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_members_icon_view, "field 'mGroupMembersIconView'", ImageView.class);
        groupDetailsHomeFragment.mGroupMembersTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_members_text_view, "field 'mGroupMembersTextView'", TextView.class);
        groupDetailsHomeFragment.mGroupRoomsIconView = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_rooms_icon_view, "field 'mGroupRoomsIconView'", ImageView.class);
        groupDetailsHomeFragment.mGroupRoomsTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.group_rooms_text_view, "field 'mGroupRoomsTextView'", TextView.class);
        groupDetailsHomeFragment.mGroupHtmlTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.html_text_view, "field 'mGroupHtmlTextView'", TextView.class);
        groupDetailsHomeFragment.noLongDescriptionTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.no_html_text_view, "field 'noLongDescriptionTextView'", TextView.class);
    }

    @CallSuper
    public void unbind() {
        GroupDetailsHomeFragment groupDetailsHomeFragment = this.target;
        if (groupDetailsHomeFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupDetailsHomeFragment.mGroupAvatar = null;
        groupDetailsHomeFragment.mGroupNameTextView = null;
        groupDetailsHomeFragment.mGroupTopicTextView = null;
        groupDetailsHomeFragment.mGroupMembersIconView = null;
        groupDetailsHomeFragment.mGroupMembersTextView = null;
        groupDetailsHomeFragment.mGroupRoomsIconView = null;
        groupDetailsHomeFragment.mGroupRoomsTextView = null;
        groupDetailsHomeFragment.mGroupHtmlTextView = null;
        groupDetailsHomeFragment.noLongDescriptionTextView = null;
    }
}
