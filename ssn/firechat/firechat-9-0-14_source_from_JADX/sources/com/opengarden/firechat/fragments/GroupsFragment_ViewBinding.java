package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupsFragment_ViewBinding implements Unbinder {
    private GroupsFragment target;

    @UiThread
    public GroupsFragment_ViewBinding(GroupsFragment groupsFragment, View view) {
        this.target = groupsFragment;
        groupsFragment.mRecycler = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recyclerview, "field 'mRecycler'", RecyclerView.class);
    }

    @CallSuper
    public void unbind() {
        GroupsFragment groupsFragment = this.target;
        if (groupsFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupsFragment.mRecycler = null;
    }
}
