package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class PeopleFragment_ViewBinding implements Unbinder {
    private PeopleFragment target;

    @UiThread
    public PeopleFragment_ViewBinding(PeopleFragment peopleFragment, View view) {
        this.target = peopleFragment;
        peopleFragment.mRecycler = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.recyclerview, "field 'mRecycler'", RecyclerView.class);
    }

    @CallSuper
    public void unbind() {
        PeopleFragment peopleFragment = this.target;
        if (peopleFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        peopleFragment.mRecycler = null;
    }
}
