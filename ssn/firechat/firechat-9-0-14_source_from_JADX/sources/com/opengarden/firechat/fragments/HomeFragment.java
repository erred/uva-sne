package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.GestureDetectorCompat;
import android.support.p000v4.widget.NestedScrollView;
import android.support.p000v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.p003v7.widget.LinearLayoutManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.HomeRoomAdapter.OnSelectRoomListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnRoomChangedListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.view.HomeSectionView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends AbsHomeFragment implements OnSelectRoomListener, OnRoomChangedListener {
    private static final String LOG_TAG = "HomeFragment";
    @BindView(2131296451)
    HomeSectionView mDirectChatsSection;
    private final MXEventListener mEventsListener = new MXEventListener() {
    };
    @BindView(2131296489)
    HomeSectionView mFavouritesSection;
    private List<HomeSectionView> mHomeSectionViews;
    @BindView(2131296632)
    HomeSectionView mInvitationsSection;
    @BindView(2131296703)
    HomeSectionView mLowPrioritySection;
    @BindView(2131296828)
    NestedScrollView mNestedScrollView;
    @BindView(2131296978)
    HomeSectionView mRoomsSection;

    public void onRoomLeft(String str) {
    }

    public void onToggleDirectChat(String str, boolean z) {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_home, viewGroup, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mPrimaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_home);
        this.mSecondaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_home_secondary);
        initViews();
        this.mOnRoomChangedListener = this;
        for (HomeSectionView currentFilter : this.mHomeSectionViews) {
            currentFilter.setCurrentFilter(this.mCurrentFilter);
        }
        this.mActivity.showWaitingView();
    }

    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventsListener);
        initData();
        if (this.mHomeSectionViews != null) {
            for (HomeSectionView scrollToPosition : this.mHomeSectionViews) {
                scrollToPosition.scrollToPosition(0);
            }
        }
    }

    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mEventsListener);
    }

    /* access modifiers changed from: protected */
    public List<Room> getRooms() {
        return new ArrayList(this.mSession.getDataHandler().getStore().getRooms());
    }

    /* access modifiers changed from: protected */
    public void onFilter(String str, OnFilterListener onFilterListener) {
        for (HomeSectionView onFilter : this.mHomeSectionViews) {
            onFilter.onFilter(str, onFilterListener);
        }
    }

    /* access modifiers changed from: protected */
    public void onResetFilter() {
        for (HomeSectionView onFilter : this.mHomeSectionViews) {
            onFilter.onFilter("", null);
        }
    }

    private void initViews() {
        this.mInvitationsSection.setTitle(C1299R.string.invitations_header);
        this.mInvitationsSection.setHideIfEmpty(true);
        this.mInvitationsSection.setPlaceholders(null, getString(C1299R.string.no_result_placeholder));
        this.mInvitationsSection.setupRoomRecyclerView(new LinearLayoutManager(getActivity(), 1, false), C1299R.layout.adapter_item_room_invite, false, this, this, null);
        this.mFavouritesSection.setTitle(C1299R.string.bottom_action_favourites);
        this.mFavouritesSection.setHideIfEmpty(true);
        this.mFavouritesSection.setPlaceholders(null, getString(C1299R.string.no_result_placeholder));
        this.mFavouritesSection.setupRoomRecyclerView(new LinearLayoutManager(getActivity(), 0, false), C1299R.layout.adapter_item_circular_room_view, true, this, null, null);
        this.mDirectChatsSection.setTitle(C1299R.string.bottom_action_people);
        this.mDirectChatsSection.setPlaceholders(getString(C1299R.string.no_conversation_placeholder), getString(C1299R.string.no_result_placeholder));
        this.mDirectChatsSection.setupRoomRecyclerView(new LinearLayoutManager(getActivity(), 0, false), C1299R.layout.adapter_item_circular_room_view, true, this, null, null);
        this.mRoomsSection.setTitle(C1299R.string.bottom_action_rooms);
        this.mRoomsSection.setPlaceholders(getString(C1299R.string.no_room_placeholder), getString(C1299R.string.no_result_placeholder));
        this.mRoomsSection.setupRoomRecyclerView(new LinearLayoutManager(getActivity(), 0, false), C1299R.layout.adapter_item_circular_room_view, true, this, null, null);
        this.mLowPrioritySection.setTitle(C1299R.string.low_priority_header);
        this.mLowPrioritySection.setHideIfEmpty(true);
        this.mLowPrioritySection.setPlaceholders(null, getString(C1299R.string.no_result_placeholder));
        this.mLowPrioritySection.setupRoomRecyclerView(new LinearLayoutManager(getActivity(), 0, false), C1299R.layout.adapter_item_circular_room_view, true, this, null, null);
        this.mHomeSectionViews = Arrays.asList(new HomeSectionView[]{this.mInvitationsSection, this.mFavouritesSection, this.mDirectChatsSection, this.mRoomsSection, this.mLowPrioritySection});
        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(this.mActivity, new SimpleOnGestureListener() {
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (HomeFragment.this.mActivity.getFloatingActionButton() != null && HomeFragment.this.mNestedScrollView.getBottom() > HomeFragment.this.mActivity.getFloatingActionButton().getTop()) {
                    HomeFragment.this.mActivity.hideFloatingActionButton(HomeFragment.this.getTag());
                }
                return true;
            }
        });
        this.mNestedScrollView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (HomeFragment.this.mNestedScrollView == null) {
                    return false;
                }
                gestureDetectorCompat.onTouchEvent(motionEvent);
                return HomeFragment.this.mNestedScrollView.onTouchEvent(motionEvent);
            }
        });
        this.mNestedScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
                HomeFragment.this.mActivity.hideFloatingActionButton(HomeFragment.this.getTag());
            }
        });
    }

    public void onSummariesUpdate() {
        super.onSummariesUpdate();
        if (isResumed() && !this.mActivity.isWaitingViewVisible()) {
            initData();
        }
    }

    private void initData() {
        if (this.mSession == null || this.mSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## initData() : null session");
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        if (this.mSession.getDataHandler().getStore() == null) {
            Log.m211e(LOG_TAG, "## initData() : null store");
            return;
        }
        for (Room room : this.mSession.getDataHandler().getStore().getRooms()) {
            if (!room.isConferenceUserRoom() && !room.isInvited() && !room.isDirectChatInvitation()) {
                if (room.getMember(this.mSession.getMyUserId()) == null) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## initData(): invalid room ");
                    sb.append(room.getRoomId());
                    sb.append(", the user is not anymore member of it");
                    Log.m211e(str, sb.toString());
                } else {
                    RoomAccountData accountData = room.getAccountData();
                    HashSet hashSet = new HashSet();
                    if (accountData != null && accountData.hasTags()) {
                        hashSet.addAll(accountData.getKeys());
                    }
                    if (hashSet.contains(RoomTag.ROOM_TAG_FAVOURITE)) {
                        arrayList.add(room);
                    } else if (hashSet.contains(RoomTag.ROOM_TAG_LOW_PRIORITY)) {
                        arrayList3.add(room);
                    } else if (RoomUtils.isDirectChat(this.mSession, room.getRoomId())) {
                        arrayList2.add(room);
                    } else {
                        arrayList4.add(room);
                    }
                }
            }
        }
        Comparator notifCountRoomsComparator = RoomUtils.getNotifCountRoomsComparator(this.mSession, PreferencesManager.pinMissedNotifications(getActivity()), PreferencesManager.pinUnreadMessages(getActivity()));
        sortAndDisplay(arrayList, notifCountRoomsComparator, this.mFavouritesSection);
        sortAndDisplay(arrayList2, notifCountRoomsComparator, this.mDirectChatsSection);
        sortAndDisplay(arrayList3, notifCountRoomsComparator, this.mLowPrioritySection);
        sortAndDisplay(arrayList4, notifCountRoomsComparator, this.mRoomsSection);
        this.mActivity.hideWaitingView();
        this.mInvitationsSection.setRooms(this.mActivity.getRoomInvitations());
    }

    private void sortAndDisplay(List<Room> list, Comparator comparator, HomeSectionView homeSectionView) {
        try {
            Collections.sort(list, comparator);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## sortAndDisplay() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        homeSectionView.setRooms(list);
    }

    public void onSelectRoom(Room room, int i) {
        openRoom(room);
    }

    public void onLongClickRoom(View view, Room room, int i) {
        Set keys = room.getAccountData().getKeys();
        RoomUtils.displayPopupMenu(getActivity(), this.mSession, room, view, keys != null && keys.contains(RoomTag.ROOM_TAG_FAVOURITE), keys != null && keys.contains(RoomTag.ROOM_TAG_LOW_PRIORITY), this);
    }

    public void onRoomForgot(String str) {
        initData();
    }
}
