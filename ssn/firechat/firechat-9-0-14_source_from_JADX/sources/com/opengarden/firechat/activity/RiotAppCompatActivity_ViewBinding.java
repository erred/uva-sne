package com.opengarden.firechat.activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class RiotAppCompatActivity_ViewBinding implements Unbinder {
    private RiotAppCompatActivity target;

    @UiThread
    public RiotAppCompatActivity_ViewBinding(RiotAppCompatActivity riotAppCompatActivity) {
        this(riotAppCompatActivity, riotAppCompatActivity.getWindow().getDecorView());
    }

    @UiThread
    public RiotAppCompatActivity_ViewBinding(RiotAppCompatActivity riotAppCompatActivity, View view) {
        this.target = riotAppCompatActivity;
        riotAppCompatActivity.toolbar = (Toolbar) C0487Utils.findOptionalViewAsType(view, C1299R.C1301id.toolbar, "field 'toolbar'", Toolbar.class);
    }

    @CallSuper
    public void unbind() {
        RiotAppCompatActivity riotAppCompatActivity = this.target;
        if (riotAppCompatActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        riotAppCompatActivity.toolbar = null;
    }
}
