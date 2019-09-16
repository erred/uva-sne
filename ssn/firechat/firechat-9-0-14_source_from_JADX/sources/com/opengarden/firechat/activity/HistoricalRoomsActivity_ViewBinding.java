package com.opengarden.firechat.activity;

import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;

public class HistoricalRoomsActivity_ViewBinding extends RiotAppCompatActivity_ViewBinding {
    private HistoricalRoomsActivity target;

    @UiThread
    public HistoricalRoomsActivity_ViewBinding(HistoricalRoomsActivity historicalRoomsActivity) {
        this(historicalRoomsActivity, historicalRoomsActivity.getWindow().getDecorView());
    }

    @UiThread
    public HistoricalRoomsActivity_ViewBinding(HistoricalRoomsActivity historicalRoomsActivity, View view) {
        super(historicalRoomsActivity, view);
        this.target = historicalRoomsActivity;
        historicalRoomsActivity.mSearchView = (SearchView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.search_view, "field 'mSearchView'", SearchView.class);
        historicalRoomsActivity.mHistoricalRecyclerView = (RecyclerView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.historical_recycler_view, "field 'mHistoricalRecyclerView'", RecyclerView.class);
        historicalRoomsActivity.mHistoricalPlaceHolder = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.historical_no_results, "field 'mHistoricalPlaceHolder'", TextView.class);
        historicalRoomsActivity.mToolbar = (Toolbar) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.historical_toolbar, "field 'mToolbar'", Toolbar.class);
        historicalRoomsActivity.waitingView = C0487Utils.findRequiredView(view, C1299R.C1301id.historical_waiting_view, "field 'waitingView'");
    }

    public void unbind() {
        HistoricalRoomsActivity historicalRoomsActivity = this.target;
        if (historicalRoomsActivity == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        historicalRoomsActivity.mSearchView = null;
        historicalRoomsActivity.mHistoricalRecyclerView = null;
        historicalRoomsActivity.mHistoricalPlaceHolder = null;
        historicalRoomsActivity.mToolbar = null;
        historicalRoomsActivity.waitingView = null;
        super.unbind();
    }
}
