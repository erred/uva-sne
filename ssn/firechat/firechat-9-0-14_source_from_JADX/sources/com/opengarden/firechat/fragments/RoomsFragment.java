package com.opengarden.firechat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter.FilterListener;
import android.widget.Spinner;
import android.widget.Toast;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.RoomDirectoryPickerActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.AdapterSection;
import com.opengarden.firechat.adapters.RoomAdapter;
import com.opengarden.firechat.adapters.RoomAdapter.OnSelectItemListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnRoomChangedListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.EventsRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomDirectoryData;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SectionView;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RoomsFragment extends AbsHomeFragment implements OnRoomChangedListener {
    private static final int DIRECTORY_SOURCE_ACTIVITY_REQUEST_CODE = 314;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RoomsFragment";
    private static final String SELECTED_ROOM_DIRECTORY = "SELECTED_ROOM_DIRECTORY";
    /* access modifiers changed from: private */
    public RoomAdapter mAdapter;
    /* access modifiers changed from: private */
    public Integer mEstimatedPublicRoomCount = null;
    private final OnScrollListener mPublicRoomScrollListener = new OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            super.onScrolled(recyclerView, i, i2);
            int findLastCompletelyVisibleItemPosition = ((LinearLayoutManager) RoomsFragment.this.mRecycler.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            SectionView sectionViewForSectionIndex = RoomsFragment.this.mAdapter.getSectionViewForSectionIndex(RoomsFragment.this.mAdapter.getSectionsCount() - 1);
            AdapterSection section = sectionViewForSectionIndex != null ? sectionViewForSectionIndex.getSection() : null;
            if (section != null) {
                for (int i3 = 0; i3 < RoomsFragment.this.mAdapter.getSectionsCount() - 1; i3++) {
                    SectionView sectionViewForSectionIndex2 = RoomsFragment.this.mAdapter.getSectionViewForSectionIndex(i3);
                    if (!(sectionViewForSectionIndex2 == null || sectionViewForSectionIndex2.getSection() == null)) {
                        findLastCompletelyVisibleItemPosition -= sectionViewForSectionIndex2.getSection().getNbItems();
                        if (findLastCompletelyVisibleItemPosition <= 0) {
                            return;
                        }
                    }
                }
                if (section.getNbItems() - findLastCompletelyVisibleItemPosition < 10) {
                    RoomsFragment.this.forwardPaginate();
                }
            }
        }
    };
    private Spinner mPublicRoomsSelector;
    @BindView(2131296889)
    RecyclerView mRecycler;
    private ArrayAdapter<CharSequence> mRoomDirectoryAdapter;
    private final List<Room> mRooms = new ArrayList();
    private RoomDirectoryData mSelectedRoomDirectory;

    public void onRoomLeft(String str) {
    }

    public void onToggleDirectChat(String str, boolean z) {
    }

    public static RoomsFragment newInstance() {
        return new RoomsFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_rooms, viewGroup, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mPrimaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_rooms);
        this.mSecondaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_rooms_secondary);
        initViews();
        this.mOnRoomChangedListener = this;
        this.mAdapter.onFilterDone(this.mCurrentFilter);
        if (bundle != null) {
            this.mSelectedRoomDirectory = (RoomDirectoryData) bundle.getSerializable(SELECTED_ROOM_DIRECTORY);
        }
        initPublicRooms(false);
    }

    public void onResume() {
        super.onResume();
        refreshRooms();
        this.mAdapter.setInvitation(this.mActivity.getRoomInvitations());
        this.mRecycler.addOnScrollListener(this.mScrollListener);
    }

    public void onPause() {
        super.onPause();
        this.mEstimatedPublicRoomCount = null;
        this.mRecycler.removeOnScrollListener(this.mScrollListener);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(SELECTED_ROOM_DIRECTORY, this.mSelectedRoomDirectory);
    }

    /* access modifiers changed from: protected */
    public List<Room> getRooms() {
        return new ArrayList(this.mRooms);
    }

    /* access modifiers changed from: protected */
    public void onFilter(String str, final OnFilterListener onFilterListener) {
        this.mAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                String access$000 = RoomsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onFilterComplete ");
                sb.append(i);
                Log.m213i(access$000, sb.toString());
                if (onFilterListener != null) {
                    onFilterListener.onFilterDone(i);
                }
                RoomsFragment.this.initPublicRooms(false);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResetFilter() {
        this.mAdapter.getFilter().filter("", new FilterListener() {
            public void onFilterComplete(int i) {
                String access$000 = RoomsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onResetFilter ");
                sb.append(i);
                Log.m213i(access$000, sb.toString());
                RoomsFragment.this.initPublicRooms(false);
            }
        });
    }

    public void onSummariesUpdate() {
        super.onSummariesUpdate();
        if (isResumed()) {
            refreshRooms();
            this.mAdapter.setInvitation(this.mActivity.getRoomInvitations());
        }
    }

    private void initViews() {
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 1, dimension));
        RecyclerView recyclerView = this.mRecycler;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(getActivity(), 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        this.mAdapter = new RoomAdapter(getActivity(), new OnSelectItemListener() {
            public void onSelectItem(Room room, int i) {
                RoomsFragment.this.openRoom(room);
            }

            public void onSelectItem(PublicRoom publicRoom) {
                RoomsFragment.this.onPublicRoomSelected(publicRoom);
            }
        }, this, this);
        this.mRecycler.setAdapter(this.mAdapter);
        View findSectionSubViewById = this.mAdapter.findSectionSubViewById(C1299R.C1301id.public_rooms_selector);
        if (findSectionSubViewById != null && (findSectionSubViewById instanceof Spinner)) {
            this.mPublicRoomsSelector = (Spinner) findSectionSubViewById;
        }
    }

    private void refreshRooms() {
        if (this.mSession == null || this.mSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## refreshRooms() : null session");
            return;
        }
        IMXStore store = this.mSession.getDataHandler().getStore();
        if (store == null) {
            Log.m211e(LOG_TAG, "## refreshRooms() : null store");
            return;
        }
        ArrayList<RoomSummary> arrayList = new ArrayList<>(store.getSummaries());
        HashSet hashSet = new HashSet(this.mSession.roomIdsWithTag(RoomTag.ROOM_TAG_LOW_PRIORITY));
        this.mRooms.clear();
        for (RoomSummary roomSummary : arrayList) {
            if (!roomSummary.isInvited()) {
                Room room = store.getRoom(roomSummary.getRoomId());
                if (room != null && !room.isConferenceUserRoom() && !RoomUtils.isDirectChat(this.mSession, room.getRoomId()) && !hashSet.contains(room.getRoomId())) {
                    this.mRooms.add(room);
                }
            }
        }
        this.mAdapter.setRooms(this.mRooms);
    }

    /* access modifiers changed from: private */
    public void onPublicRoomSelected(final PublicRoom publicRoom) {
        if (publicRoom.roomId != null) {
            final RoomPreviewData roomPreviewData = new RoomPreviewData(this.mSession, publicRoom.roomId, null, publicRoom.getAlias(), null);
            Room room = this.mSession.getDataHandler().getRoom(publicRoom.roomId, false);
            if (room == null) {
                this.mActivity.showWaitingView();
                roomPreviewData.fetchPreviewData(new ApiCallback<Void>() {
                    private void onDone() {
                        if (RoomsFragment.this.mActivity != null) {
                            RoomsFragment.this.mActivity.hideWaitingView();
                            CommonActivityUtils.previewRoom(RoomsFragment.this.getActivity(), roomPreviewData);
                        }
                    }

                    public void onSuccess(Void voidR) {
                        onDone();
                    }

                    private void onError() {
                        roomPreviewData.setRoomState(publicRoom);
                        roomPreviewData.setRoomName(publicRoom.name);
                        onDone();
                    }

                    public void onNetworkError(Exception exc) {
                        onError();
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        onError();
                    }

                    public void onUnexpectedError(Exception exc) {
                        onError();
                    }
                });
            } else if (room.isInvited()) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("manageRoom : the user is invited -> display the preview ");
                sb.append(getActivity());
                Log.m209d(str, sb.toString());
                CommonActivityUtils.previewRoom(getActivity(), roomPreviewData);
            } else {
                Log.m209d(LOG_TAG, "manageRoom : open the room");
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
                hashMap.put("EXTRA_ROOM_ID", publicRoom.roomId);
                if (!TextUtils.isEmpty(publicRoom.name)) {
                    hashMap.put(VectorRoomActivity.EXTRA_DEFAULT_NAME, publicRoom.name);
                }
                if (!TextUtils.isEmpty(publicRoom.topic)) {
                    hashMap.put(VectorRoomActivity.EXTRA_DEFAULT_TOPIC, publicRoom.topic);
                }
                CommonActivityUtils.goToRoomPage(getActivity(), this.mSession, hashMap);
            }
        }
    }

    private void refreshDirectorySourceSpinner() {
        if (this.mSelectedRoomDirectory == null) {
            this.mSelectedRoomDirectory = RoomDirectoryData.getDefault();
        }
        if (this.mRoomDirectoryAdapter == null) {
            this.mRoomDirectoryAdapter = new ArrayAdapter<>(getActivity(), C1299R.layout.public_room_spinner_item);
        } else {
            this.mRoomDirectoryAdapter.clear();
        }
        if (this.mPublicRoomsSelector != null) {
            if (this.mRoomDirectoryAdapter != this.mPublicRoomsSelector.getAdapter()) {
                this.mPublicRoomsSelector.setAdapter(this.mRoomDirectoryAdapter);
            } else {
                this.mRoomDirectoryAdapter.notifyDataSetChanged();
            }
            this.mPublicRoomsSelector.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        RoomsFragment.this.startActivityForResult(RoomDirectoryPickerActivity.getIntent(RoomsFragment.this.getActivity(), RoomsFragment.this.mSession.getMyUserId()), 314);
                    }
                    return true;
                }
            });
        }
        this.mRoomDirectoryAdapter.add(this.mSelectedRoomDirectory.getDisplayName());
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (-1 == i2 && i == 314) {
            this.mSelectedRoomDirectory = (RoomDirectoryData) intent.getSerializableExtra(RoomDirectoryPickerActivity.EXTRA_OUT_ROOM_DIRECTORY_DATA);
            this.mAdapter.setPublicRooms(new ArrayList());
            initPublicRooms(true);
        }
    }

    private void showPublicRoomsLoadingView() {
        this.mAdapter.getSectionViewForSectionIndex(this.mAdapter.getSectionsCount() - 1).showLoadingView();
    }

    /* access modifiers changed from: private */
    public void hidePublicRoomsLoadingView() {
        this.mAdapter.getSectionViewForSectionIndex(this.mAdapter.getSectionsCount() - 1).hideLoadingView();
    }

    /* access modifiers changed from: private */
    public void initPublicRooms(final boolean z) {
        refreshDirectorySourceSpinner();
        showPublicRoomsLoadingView();
        this.mAdapter.setNoMorePublicRooms(false);
        if (this.mEstimatedPublicRoomCount == null) {
            EventsRestClient eventsApiClient = this.mSession != null ? this.mSession.getEventsApiClient() : null;
            if (eventsApiClient == null) {
                hidePublicRoomsLoadingView();
            } else {
                eventsApiClient.getPublicRoomsCount(this.mSelectedRoomDirectory.getHomeServer(), this.mSelectedRoomDirectory.getThirdPartyInstanceId(), this.mSelectedRoomDirectory.isIncludedAllNetworks(), new ApiCallback<Integer>() {
                    private void onDone(int i) {
                        RoomsFragment.this.mEstimatedPublicRoomCount = Integer.valueOf(i);
                        RoomsFragment.this.mAdapter.setEstimatedPublicRoomsCount(i);
                        RoomsFragment.this.initPublicRooms(z);
                    }

                    public void onSuccess(Integer num) {
                        if (num != null) {
                            onDone(num.intValue());
                        } else {
                            onDone(-1);
                        }
                    }

                    public void onNetworkError(Exception exc) {
                        String access$000 = RoomsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startPublicRoomsSearch() : getPublicRoomsCount failed ");
                        sb.append(exc.getMessage());
                        Log.m211e(access$000, sb.toString());
                        onDone(-1);
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        String access$000 = RoomsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startPublicRoomsSearch() : getPublicRoomsCount failed ");
                        sb.append(matrixError.getMessage());
                        Log.m211e(access$000, sb.toString());
                        onDone(-1);
                    }

                    public void onUnexpectedError(Exception exc) {
                        String access$000 = RoomsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startPublicRoomsSearch() : getPublicRoomsCount failed ");
                        sb.append(exc.getMessage());
                        Log.m211e(access$000, sb.toString());
                        onDone(-1);
                    }
                });
            }
        } else {
            PublicRoomsManager.getInstance().startPublicRoomsSearch(this.mSelectedRoomDirectory.getHomeServer(), this.mSelectedRoomDirectory.getThirdPartyInstanceId(), this.mSelectedRoomDirectory.isIncludedAllNetworks(), this.mCurrentFilter, new ApiCallback<List<PublicRoom>>() {
                public void onSuccess(List<PublicRoom> list) {
                    if (RoomsFragment.this.getActivity() != null) {
                        RoomsFragment.this.mAdapter.setNoMorePublicRooms(list.size() < 20);
                        RoomsFragment.this.mAdapter.setPublicRooms(list);
                        RoomsFragment.this.addPublicRoomsListener();
                        if (z) {
                            RoomsFragment.this.mRecycler.post(new Runnable() {
                                public void run() {
                                    SectionView sectionViewForSectionIndex = RoomsFragment.this.mAdapter.getSectionViewForSectionIndex(RoomsFragment.this.mAdapter.getSectionsCount() - 1);
                                    if (sectionViewForSectionIndex != null && !sectionViewForSectionIndex.isStickyHeader()) {
                                        sectionViewForSectionIndex.callOnClick();
                                    }
                                }
                            });
                        }
                        RoomsFragment.this.hidePublicRoomsLoadingView();
                    }
                }

                private void onError(String str) {
                    if (RoomsFragment.this.getActivity() != null) {
                        String access$000 = RoomsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startPublicRoomsSearch() failed ");
                        sb.append(str);
                        Log.m211e(access$000, sb.toString());
                        RoomsFragment.this.hidePublicRoomsLoadingView();
                    }
                }

                public void onNetworkError(Exception exc) {
                    onError(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onError(exc.getLocalizedMessage());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void forwardPaginate() {
        if (!PublicRoomsManager.getInstance().isRequestInProgress()) {
            if (PublicRoomsManager.getInstance().forwardPaginate(new ApiCallback<List<PublicRoom>>() {
                public void onSuccess(List<PublicRoom> list) {
                    if (RoomsFragment.this.getActivity() != null) {
                        if (!PublicRoomsManager.getInstance().hasMoreResults()) {
                            RoomsFragment.this.mAdapter.setNoMorePublicRooms(true);
                            RoomsFragment.this.removePublicRoomsListener();
                        }
                        RoomsFragment.this.mAdapter.addPublicRooms(list);
                        RoomsFragment.this.hidePublicRoomsLoadingView();
                    }
                }

                private void onError(String str) {
                    if (RoomsFragment.this.getActivity() != null) {
                        String access$000 = RoomsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## forwardPaginate() failed ");
                        sb.append(str);
                        Log.m211e(access$000, sb.toString());
                        Toast.makeText(RoomsFragment.this.getActivity(), str, 0).show();
                    }
                    RoomsFragment.this.hidePublicRoomsLoadingView();
                }

                public void onNetworkError(Exception exc) {
                    onError(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onError(exc.getLocalizedMessage());
                }
            })) {
                showPublicRoomsLoadingView();
            } else {
                hidePublicRoomsLoadingView();
            }
        }
    }

    /* access modifiers changed from: private */
    public void addPublicRoomsListener() {
        this.mRecycler.addOnScrollListener(this.mPublicRoomScrollListener);
    }

    /* access modifiers changed from: private */
    public void removePublicRoomsListener() {
        this.mRecycler.removeOnScrollListener(null);
    }

    public void onRoomForgot(String str) {
        refreshRooms();
    }
}
