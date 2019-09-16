package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class RoomsFragment_ViewBinding implements Unbinder {
    private RoomsFragment target;

    @UiThread
    public RoomsFragment_ViewBinding(RoomsFragment roomsFragment, View view) {
        this.target = roomsFragment;
        roomsFragment.mRecycler = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recyclerview, "field 'mRecycler'", RecyclerView.class);
    }

    @CallSuper
    public void unbind() {
        RoomsFragment roomsFragment = this.target;
        if (roomsFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        roomsFragment.mRecycler = null;
    }
}
