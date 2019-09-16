package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.support.p003v7.widget.helper.ItemTouchHelper;
import android.support.p003v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter.FilterListener;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.HomeRoomAdapter;
import com.opengarden.firechat.adapters.HomeRoomAdapter.OnSelectRoomListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavouritesFragment extends AbsHomeFragment implements OnSelectRoomListener {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "FavouritesFragment";
    private ItemTouchHelper mDragAndDropTouchHelper;
    private final MXEventListener mEventsListener = new MXEventListener() {
        public void onRoomTagEvent(String str) {
            if (FavouritesFragment.this.mActivity.isWaitingViewVisible()) {
                FavouritesFragment.this.onRoomTagUpdated(null);
            }
        }
    };
    private final List<Room> mFavorites = new ArrayList();
    /* access modifiers changed from: private */
    public HomeRoomAdapter mFavoritesAdapter;
    @BindView(2131296487)
    TextView mFavoritesPlaceHolder;
    @BindView(2131296488)
    RecyclerView mFavoritesRecyclerView;

    public void onLongClickRoom(View view, Room room, int i) {
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_favourites, viewGroup, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mPrimaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_favourites);
        this.mSecondaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_favourites_secondary);
        initViews();
        this.mFavoritesAdapter.onFilterDone(this.mCurrentFilter);
    }

    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventsListener);
        refreshFavorites();
    }

    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mEventsListener);
    }

    /* access modifiers changed from: protected */
    public List<Room> getRooms() {
        return new ArrayList(this.mFavorites);
    }

    /* access modifiers changed from: protected */
    public void onFilter(String str, final OnFilterListener onFilterListener) {
        this.mFavoritesAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                FavouritesFragment.this.updateRoomsDisplay(i);
                onFilterListener.onFilterDone(i);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResetFilter() {
        this.mFavoritesAdapter.getFilter().filter("", new FilterListener() {
            public void onFilterComplete(int i) {
                String access$200 = FavouritesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onResetFilter ");
                sb.append(i);
                Log.m213i(access$200, sb.toString());
                FavouritesFragment.this.updateRoomsDisplay(FavouritesFragment.this.mFavoritesAdapter.getItemCount());
            }
        });
    }

    private void initViews() {
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.mFavoritesRecyclerView.setHasFixedSize(true);
        this.mFavoritesRecyclerView.setNestedScrollingEnabled(false);
        HomeRoomAdapter homeRoomAdapter = new HomeRoomAdapter(getContext(), C1299R.layout.adapter_item_room_view, this, null, this);
        this.mFavoritesAdapter = homeRoomAdapter;
        this.mFavoritesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 1, dimension));
        RecyclerView recyclerView = this.mFavoritesRecyclerView;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(getActivity(), 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        this.mFavoritesRecyclerView.setAdapter(this.mFavoritesAdapter);
        initFavoritesDragDrop();
    }

    public void onSummariesUpdate() {
        super.onSummariesUpdate();
        if (isResumed() && !this.mActivity.isWaitingViewVisible()) {
            refreshFavorites();
        }
    }

    /* access modifiers changed from: private */
    public void updateRoomsDisplay(int i) {
        int i2 = 8;
        if (this.mFavoritesPlaceHolder != null) {
            this.mFavoritesPlaceHolder.setVisibility(i == 0 ? 0 : 8);
        }
        if (this.mFavoritesRecyclerView != null) {
            RecyclerView recyclerView = this.mFavoritesRecyclerView;
            if (i != 0) {
                i2 = 0;
            }
            recyclerView.setVisibility(i2);
        }
    }

    private void refreshFavorites() {
        final List roomIdsWithTag = this.mSession.roomIdsWithTag(RoomTag.ROOM_TAG_FAVOURITE);
        this.mFavorites.clear();
        if (roomIdsWithTag.size() != 0) {
            IMXStore store = this.mSession.getDataHandler().getStore();
            for (RoomSummary roomSummary : new ArrayList(store.getSummaries())) {
                if (roomIdsWithTag.contains(roomSummary.getRoomId())) {
                    Room room = store.getRoom(roomSummary.getRoomId());
                    if (room != null) {
                        this.mFavorites.add(room);
                    }
                }
            }
            try {
                Collections.sort(this.mFavorites, new Comparator<Room>() {
                    public int compare(Room room, Room room2) {
                        int i = -1;
                        int indexOf = (room == null || room.getRoomId() == null) ? -1 : roomIdsWithTag.indexOf(room.getRoomId());
                        if (!(room2 == null || room2.getRoomId() == null)) {
                            i = roomIdsWithTag.indexOf(room2.getRoomId());
                        }
                        return indexOf - i;
                    }
                });
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshFavorites() : sort failed with error ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        this.mFavoritesAdapter.setRooms(this.mFavorites);
        updateRoomsDisplay(this.mFavorites.size());
        this.mDragAndDropTouchHelper.attachToRecyclerView(this.mFavorites.size() > 1 ? this.mFavoritesRecyclerView : null);
    }

    private void initFavoritesDragDrop() {
        this.mDragAndDropTouchHelper = new ItemTouchHelper(new SimpleCallback(3, 0) {
            private int mFromPosition = -1;
            private String mRoomId;
            private int mToPosition = -1;

            public void onSwiped(ViewHolder viewHolder, int i) {
            }

            public int getMovementFlags(RecyclerView recyclerView, ViewHolder viewHolder) {
                return makeMovementFlags(!TextUtils.isEmpty(FavouritesFragment.this.mCurrentFilter) ? 0 : 3, 0);
            }

            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder viewHolder2) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (-1 == this.mFromPosition) {
                    this.mFromPosition = adapterPosition;
                    this.mRoomId = FavouritesFragment.this.mFavoritesAdapter.getRoom(this.mFromPosition).getRoomId();
                }
                this.mToPosition = viewHolder2.getAdapterPosition();
                FavouritesFragment.this.mFavoritesAdapter.notifyItemMoved(this.mFromPosition, this.mToPosition);
                return true;
            }

            public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                if (this.mFromPosition >= 0 && this.mToPosition >= 0) {
                    String access$200 = FavouritesFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## initFavoritesDragDrop() : move room id ");
                    sb.append(this.mRoomId);
                    sb.append(" from ");
                    sb.append(this.mFromPosition);
                    sb.append(" to ");
                    sb.append(this.mToPosition);
                    Log.m209d(access$200, sb.toString());
                    Double tagOrderToBeAtIndex = FavouritesFragment.this.mSession.tagOrderToBeAtIndex(this.mToPosition, this.mFromPosition, RoomTag.ROOM_TAG_FAVOURITE);
                    FavouritesFragment.this.mActivity.showWaitingView();
                    RoomUtils.updateRoomTag(FavouritesFragment.this.mSession, this.mRoomId, tagOrderToBeAtIndex, RoomTag.ROOM_TAG_FAVOURITE, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                        }

                        public void onNetworkError(Exception exc) {
                            FavouritesFragment.this.onRoomTagUpdated(exc.getLocalizedMessage());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            FavouritesFragment.this.onRoomTagUpdated(matrixError.getLocalizedMessage());
                        }

                        public void onUnexpectedError(Exception exc) {
                            FavouritesFragment.this.onRoomTagUpdated(exc.getLocalizedMessage());
                        }
                    });
                }
                this.mFromPosition = -1;
                this.mToPosition = -1;
            }
        });
    }

    /* access modifiers changed from: private */
    public void onRoomTagUpdated(String str) {
        this.mActivity.hideWaitingView();
        refreshFavorites();
        this.mFavoritesAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(str)) {
            Toast.makeText(getActivity(), str, 1).show();
        }
    }

    public void onSelectRoom(Room room, int i) {
        openRoom(room);
    }
}
