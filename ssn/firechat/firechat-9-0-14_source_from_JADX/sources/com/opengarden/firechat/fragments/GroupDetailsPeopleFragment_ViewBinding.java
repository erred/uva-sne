package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class GroupDetailsPeopleFragment_ViewBinding implements Unbinder {
    private GroupDetailsPeopleFragment target;

    @UiThread
    public GroupDetailsPeopleFragment_ViewBinding(GroupDetailsPeopleFragment groupDetailsPeopleFragment, View view) {
        this.target = groupDetailsPeopleFragment;
        groupDetailsPeopleFragment.mRecycler = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recyclerview, "field 'mRecycler'", RecyclerView.class);
        groupDetailsPeopleFragment.mSearchView = (SearchView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.search_view, "field 'mSearchView'", SearchView.class);
    }

    @CallSuper
    public void unbind() {
        GroupDetailsPeopleFragment groupDetailsPeopleFragment = this.target;
        if (groupDetailsPeopleFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        groupDetailsPeopleFragment.mRecycler = null;
        groupDetailsPeopleFragment.mSearchView = null;
    }
}
