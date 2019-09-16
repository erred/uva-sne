package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupDetailsRoomsFragment_ViewBinding implements Unbinder {
    private GroupDetailsRoomsFragment target;

    @UiThread
    public GroupDetailsRoomsFragment_ViewBinding(GroupDetailsRoomsFragment groupDetailsRoomsFragment, View view) {
        this.target = groupDetailsRoomsFragment;
        groupDetailsRoomsFragment.mRecycler = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recyclerview, "field 'mRecycler'", RecyclerView.class);
        groupDetailsRoomsFragment.mSearchView = (SearchView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.search_view, "field 'mSearchView'", SearchView.class);
    }

    @CallSuper
    public void unbind() {
        GroupDetailsRoomsFragment groupDetailsRoomsFragment = this.target;
        if (groupDetailsRoomsFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupDetailsRoomsFragment.mRecycler = null;
        groupDetailsRoomsFragment.mSearchView = null;
    }
}
