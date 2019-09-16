package com.opengarden.firechat.fragments;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class FavouritesFragment_ViewBinding implements Unbinder {
    private FavouritesFragment target;

    @UiThread
    public FavouritesFragment_ViewBinding(FavouritesFragment favouritesFragment, View view) {
        this.target = favouritesFragment;
        favouritesFragment.mFavoritesRecyclerView = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.favorites_recycler_view, "field 'mFavoritesRecyclerView'", RecyclerView.class);
        favouritesFragment.mFavoritesPlaceHolder = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.favorites_placeholder, "field 'mFavoritesPlaceHolder'", TextView.class);
    }

    @CallSuper
    public void unbind() {
        FavouritesFragment favouritesFragment = this.target;
        if (favouritesFragment == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        favouritesFragment.mFavoritesRecyclerView = null;
        favouritesFragment.mFavoritesPlaceHolder = null;
    }
}
