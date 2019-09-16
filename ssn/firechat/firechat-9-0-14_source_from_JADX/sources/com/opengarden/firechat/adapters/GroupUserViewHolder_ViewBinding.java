package com.opengarden.firechat.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupUserViewHolder_ViewBinding implements Unbinder {
    private GroupUserViewHolder target;

    @UiThread
    public GroupUserViewHolder_ViewBinding(GroupUserViewHolder groupUserViewHolder, View view) {
        this.target = groupUserViewHolder;
        groupUserViewHolder.vContactAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_avatar, "field 'vContactAvatar'", ImageView.class);
        groupUserViewHolder.vContactName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_name, "field 'vContactName'", TextView.class);
    }

    @CallSuper
    public void unbind() {
        GroupUserViewHolder groupUserViewHolder = this.target;
        if (groupUserViewHolder == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupUserViewHolder.vContactAvatar = null;
        groupUserViewHolder.vContactName = null;
    }
}
