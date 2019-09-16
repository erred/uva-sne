package com.opengarden.firechat.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import android.support.p003v7.widget.SearchView.SearchAutoComplete;
import android.support.p003v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Filter.FilterListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.HomeRoomAdapter;
import com.opengarden.firechat.adapters.HomeRoomAdapter.OnSelectRoomListener;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.RoomUtils.HistoricalRoomActionListener;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HistoricalRoomsActivity extends RiotAppCompatActivity implements OnQueryTextListener, OnSelectRoomListener, MoreRoomActionListener, HistoricalRoomActionListener {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "HistoricalRoomsActivity";
    /* access modifiers changed from: private */
    public HomeRoomAdapter mHistoricalAdapter;
    @BindView(2131296560)
    TextView mHistoricalPlaceHolder;
    @BindView(2131296561)
    RecyclerView mHistoricalRecyclerView;
    @BindView(2131297010)
    SearchView mSearchView;
    /* access modifiers changed from: private */
    public MXSession mSession;
    private final List<AsyncTask> mSortingAsyncTasks = new ArrayList();
    @BindView(2131296563)
    Toolbar mToolbar;
    @BindView(2131296564)
    View waitingView;

    public int getLayoutRes() {
        return C1299R.layout.activity_login_fallback;
    }

    public int getTitleRes() {
        return C1299R.string.login;
    }

    public boolean onQueryTextSubmit(String str) {
        return true;
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            initViews();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        refreshHistorical();
    }

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }

    public void onStop() {
        super.onStop();
        for (AsyncTask cancel : this.mSortingAsyncTasks) {
            cancel.cancel(true);
        }
    }

    private void initViews() {
        setWaitingView(findViewById(C1299R.C1301id.historical_waiting_view));
        setSupportActionBar(this.mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.mSession = Matrix.getInstance(this).getDefaultSession();
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mHistoricalRecyclerView.setLayoutManager(new LinearLayoutManager(this, 1, false));
        this.mHistoricalRecyclerView.setHasFixedSize(true);
        this.mHistoricalRecyclerView.setNestedScrollingEnabled(false);
        this.mHistoricalRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this, 1, dimension));
        RecyclerView recyclerView = this.mHistoricalRecyclerView;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(this, 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        HomeRoomAdapter homeRoomAdapter = new HomeRoomAdapter(this, C1299R.layout.adapter_item_room_view, this, null, this);
        this.mHistoricalAdapter = homeRoomAdapter;
        this.mHistoricalRecyclerView.setAdapter(this.mHistoricalAdapter);
        SearchManager searchManager = (SearchManager) getSystemService(Event.SEARCH);
        LinearLayout linearLayout = (LinearLayout) this.mSearchView.findViewById(C1299R.C1301id.search_edit_frame);
        if (linearLayout != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) linearLayout.getLayoutParams();
            marginLayoutParams.leftMargin = 0;
            linearLayout.setLayoutParams(marginLayoutParams);
        }
        ImageView imageView = (ImageView) this.mSearchView.findViewById(C1299R.C1301id.search_mag_icon);
        if (imageView != null) {
            MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) imageView.getLayoutParams();
            marginLayoutParams2.leftMargin = 0;
            imageView.setLayoutParams(marginLayoutParams2);
        }
        this.mToolbar.setContentInsetStartWithNavigation(0);
        this.mSearchView.setMaxWidth(Integer.MAX_VALUE);
        this.mSearchView.setSubmitButtonEnabled(false);
        this.mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        this.mSearchView.setIconifiedByDefault(false);
        this.mSearchView.setOnQueryTextListener(this);
        this.mSearchView.setQueryHint(getString(C1299R.string.historical_placeholder));
        ((SearchAutoComplete) this.mSearchView.findViewById(C1299R.C1301id.search_src_text)).setHintTextColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.default_text_hint_color));
    }

    private void refreshHistorical() {
        MXDataHandler dataHandler = this.mSession.getDataHandler();
        if (!dataHandler.areLeftRoomsSynced()) {
            this.mHistoricalAdapter.setRooms(new ArrayList());
            showWaitingView();
            dataHandler.retrieveLeftRooms(new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    HistoricalRoomsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            HistoricalRoomsActivity.this.initHistoricalRoomsData();
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    HistoricalRoomsActivity.this.onRequestDone(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
                }
            });
            return;
        }
        initHistoricalRoomsData();
    }

    /* access modifiers changed from: private */
    public void initHistoricalRoomsData() {
        hideWaitingView();
        final ArrayList arrayList = new ArrayList(this.mSession.getDataHandler().getLeftRooms());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if (((Room) it.next()).isConferenceUserRoom()) {
                it.remove();
            }
        }
        C13832 r1 = new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                if (!isCancelled()) {
                    try {
                        Collections.sort(arrayList, RoomUtils.getHistoricalRoomsComparator(HistoricalRoomsActivity.this.mSession, false));
                    } catch (Exception e) {
                        String access$300 = HistoricalRoomsActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## initHistoricalRoomsData() : sort failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$300, sb.toString());
                    }
                }
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                HistoricalRoomsActivity.this.mHistoricalAdapter.setRooms(arrayList);
            }
        };
        try {
            r1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            this.mSortingAsyncTasks.add(r1);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## initHistoricalRoomsData() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            r1.cancel(true);
        }
    }

    public boolean onQueryTextChange(final String str) {
        if (this.mSession.getDataHandler().areLeftRoomsSynced()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    if (TextUtils.equals(HistoricalRoomsActivity.this.mSearchView.getQuery().toString(), str)) {
                        HistoricalRoomsActivity.this.mHistoricalAdapter.getFilter().filter(str, new FilterListener() {
                            public void onFilterComplete(int i) {
                                int i2 = 0;
                                HistoricalRoomsActivity.this.mHistoricalRecyclerView.scrollToPosition(0);
                                TextView textView = HistoricalRoomsActivity.this.mHistoricalPlaceHolder;
                                if (i != 0) {
                                    i2 = 8;
                                }
                                textView.setVisibility(i2);
                            }
                        });
                    }
                }
            }, 500);
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void onRequestDone(final String str) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    HistoricalRoomsActivity.this.hideWaitingView();
                    if (!TextUtils.isEmpty(str)) {
                        Toast.makeText(HistoricalRoomsActivity.this, str, 0).show();
                    }
                }
            });
        }
    }

    public void onSelectRoom(Room room, int i) {
        showWaitingView();
        CommonActivityUtils.previewRoom((Activity) this, this.mSession, room.getRoomId(), "", (ApiCallback<Void>) new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                HistoricalRoomsActivity.this.onRequestDone(null);
            }

            public void onNetworkError(Exception exc) {
                HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                HistoricalRoomsActivity.this.onRequestDone(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
            }
        });
    }

    public void onLongClickRoom(View view, Room room, int i) {
        RoomUtils.displayHistoricalRoomMenu(this, this.mSession, room, view, this);
    }

    public void onMoreActionClick(View view, Room room) {
        RoomUtils.displayHistoricalRoomMenu(this, this.mSession, room, view, this);
    }

    public void onForgotRoom(Room room) {
        showWaitingView();
        room.forget(new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                HistoricalRoomsActivity.this.initHistoricalRoomsData();
            }

            public void onNetworkError(Exception exc) {
                HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                HistoricalRoomsActivity.this.onRequestDone(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                HistoricalRoomsActivity.this.onRequestDone(exc.getLocalizedMessage());
            }
        });
    }
}
