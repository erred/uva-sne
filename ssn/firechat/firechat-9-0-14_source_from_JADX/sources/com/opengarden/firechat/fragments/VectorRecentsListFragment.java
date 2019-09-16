package com.opengarden.firechat.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.PublicRoomsManager.PublicRoomsManagerListener;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorPublicRoomsActivity;
import com.opengarden.firechat.adapters.VectorRoomSummaryAdapter;
import com.opengarden.firechat.adapters.VectorRoomSummaryAdapter.RoomEventListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.RoomNotificationState;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRuleUpdateListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.RoomUtils.MoreActionListener;
import com.opengarden.firechat.view.RecentsExpandableListView;
import com.opengarden.firechat.view.RecentsExpandableListView.DragAndDropEventsListener;
import java.util.HashMap;
import java.util.List;

public class VectorRecentsListFragment extends VectorBaseFragment implements RoomEventListener, DragAndDropEventsListener, MoreActionListener {
    static final String ARG_LAYOUT_ID = "VectorRecentsListFragment.ARG_LAYOUT_ID";
    static final String ARG_MATRIX_ID = "VectorRecentsListFragment.ARG_MATRIX_ID";
    private static final String KEY_EXPAND_STATE_FAVOURITE_GROUP = "KEY_EXPAND_STATE_FAVOURITE_GROUP";
    private static final String KEY_EXPAND_STATE_INVITES_GROUP = "KEY_EXPAND_STATE_INVITES_GROUP";
    private static final String KEY_EXPAND_STATE_LOW_PRIORITY_GROUP = "KEY_EXPAND_STATE_LOW_PRIORITY_GROUP";
    private static final String KEY_EXPAND_STATE_ROOMS_GROUP = "KEY_EXPAND_STATE_ROOMS_GROUP";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRecentsListFragment";
    VectorRoomSummaryAdapter mAdapter;
    private int mDestChildPosition = -1;
    private int mDestGroupPosition = -1;
    private View mDraggedView;
    private MXEventListener mEventsListener;
    /* access modifiers changed from: private */
    public int mFirstVisibleIndex = 0;
    /* access modifiers changed from: private */
    public boolean mIgnoreScrollEvent;
    /* access modifiers changed from: private */
    public boolean mIsLoadingPublicRooms = false;
    /* access modifiers changed from: private */
    public boolean mIsPaused = false;
    /* access modifiers changed from: private */
    public boolean mIsWaitingDirectChatEcho;
    /* access modifiers changed from: private */
    public boolean mIsWaitingTagOrderEcho;
    /* access modifiers changed from: private */
    public long mLatestPublicRoomsRefresh = System.currentTimeMillis();
    String mMatrixId;
    private int mOriginChildPosition = -1;
    private int mOriginGroupPosition = -1;
    /* access modifiers changed from: private */
    public final PublicRoomsManagerListener mPublicRoomsListener = new PublicRoomsManagerListener() {
        public void onPublicRoomsCountRefresh(final Integer num) {
            if (VectorRecentsListFragment.this.getActivity() != null) {
                VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRecentsListFragment.this.mLatestPublicRoomsRefresh = System.currentTimeMillis();
                        VectorRecentsListFragment.this.mIsLoadingPublicRooms = false;
                        VectorRecentsListFragment.this.mAdapter.setPublicRoomsCount(num);
                    }
                });
            }
        }
    };
    RecentsExpandableListView mRecentsListView;
    /* access modifiers changed from: private */
    public IVectorRecentsScrollEventListener mScrollEventListener = null;
    /* access modifiers changed from: private */
    public int mScrollToIndex = -1;
    private RelativeLayout mSelectedCellLayout;
    /* access modifiers changed from: private */
    public MXSession mSession;
    View mWaitingView = null;
    /* access modifiers changed from: private */
    public boolean refreshOnChunkEnd = false;

    public interface IVectorRecentsScrollEventListener {
        void onRecentsListFitsScreen();

        void onRecentsListOverScrollUp();

        void onRecentsListScrollDown();

        void onRecentsListScrollUp();
    }

    /* access modifiers changed from: 0000 */
    public boolean isDragAndDropSupported() {
        return true;
    }

    public static VectorRecentsListFragment newInstance(String str, int i) {
        VectorRecentsListFragment vectorRecentsListFragment = new VectorRecentsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_LAYOUT_ID, i);
        bundle.putString(ARG_MATRIX_ID, str);
        vectorRecentsListFragment.setArguments(bundle);
        return vectorRecentsListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        this.mMatrixId = arguments.getString(ARG_MATRIX_ID);
        this.mSession = Matrix.getInstance(getActivity()).getSession(this.mMatrixId);
        if (this.mSession == null) {
            if (getActivity() != null) {
                CommonActivityUtils.logout(getActivity());
            }
            return onCreateView;
        }
        View inflate = layoutInflater.inflate(arguments.getInt(ARG_LAYOUT_ID), viewGroup, false);
        this.mRecentsListView = (RecentsExpandableListView) inflate.findViewById(C1299R.C1301id.fragment_recents_list);
        this.mRecentsListView.setGroupIndicator(null);
        VectorRoomSummaryAdapter vectorRoomSummaryAdapter = new VectorRoomSummaryAdapter(getActivity(), this.mSession, false, true, C1299R.layout.adapter_item_vector_recent_room, C1299R.layout.adapter_item_vector_recent_header, this, this);
        this.mAdapter = vectorRoomSummaryAdapter;
        this.mRecentsListView.setAdapter(this.mAdapter);
        this.mSelectedCellLayout = (RelativeLayout) inflate.findViewById(C1299R.C1301id.fragment_recents_selected_cell_layout);
        this.mRecentsListView.mDragAndDropEventsListener = this;
        this.mRecentsListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                if (VectorRecentsListFragment.this.mAdapter.isDirectoryGroupPosition(i)) {
                    Intent intent = new Intent(VectorRecentsListFragment.this.getActivity(), VectorPublicRoomsActivity.class);
                    intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRecentsListFragment.this.mSession.getMyUserId());
                    if (!TextUtils.isEmpty(VectorRecentsListFragment.this.mAdapter.getSearchedPattern())) {
                        intent.putExtra(VectorPublicRoomsActivity.EXTRA_SEARCHED_PATTERN, VectorRecentsListFragment.this.mAdapter.getSearchedPattern());
                    }
                    VectorRecentsListFragment.this.getActivity().startActivity(intent);
                } else {
                    RoomSummary roomSummaryAt = VectorRecentsListFragment.this.mAdapter.getRoomSummaryAt(i, i2);
                    MXSession session = Matrix.getInstance(VectorRecentsListFragment.this.getActivity()).getSession(roomSummaryAt.getMatrixId());
                    if (session == null || session.getDataHandler() == null) {
                        return true;
                    }
                    String roomId = roomSummaryAt.getRoomId();
                    Room room = session.getDataHandler().getRoom(roomId);
                    if (room == null || room.isLeaving()) {
                        roomId = null;
                    }
                    if (VectorRecentsListFragment.this.mAdapter.resetUnreadCount(i, i2)) {
                        session.getDataHandler().getStore().flushSummary(roomSummaryAt);
                    }
                    CommonActivityUtils.specificUpdateBadgeUnreadCount(VectorRecentsListFragment.this.mSession, VectorRecentsListFragment.this.getContext());
                    if (roomId != null) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", session.getMyUserId());
                        hashMap.put("EXTRA_ROOM_ID", roomId);
                        CommonActivityUtils.goToRoomPage(VectorRecentsListFragment.this.getActivity(), session, hashMap);
                    } else if (room == null) {
                        String access$300 = VectorRecentsListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Cannot open the room ");
                        sb.append(roomId);
                        sb.append(" because there is no matched room.");
                        Log.m211e(access$300, sb.toString());
                    } else {
                        String access$3002 = VectorRecentsListFragment.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Cannot open the room ");
                        sb2.append(roomId);
                        sb2.append(" because the user is leaving the room.");
                        Log.m211e(access$3002, sb2.toString());
                    }
                }
                return true;
            }
        });
        this.mRecentsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
                VectorRecentsListFragment.this.startDragAndDrop();
                return true;
            }
        });
        this.mRecentsListView.setOnScrollListener(new OnScrollListener() {
            private int mPrevOffset = 0;

            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            private void onScrollUp() {
                if (VectorRecentsListFragment.this.getListener() != null) {
                    VectorRecentsListFragment.this.mScrollEventListener.onRecentsListScrollUp();
                }
            }

            private void onScrollDown() {
                if (VectorRecentsListFragment.this.getListener() != null) {
                    VectorRecentsListFragment.this.mScrollEventListener.onRecentsListScrollDown();
                }
            }

            private void onFitScreen() {
                if (VectorRecentsListFragment.this.getListener() != null) {
                    VectorRecentsListFragment.this.mScrollEventListener.onRecentsListFitsScreen();
                }
            }

            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (i == 0 && i3 + 1 < i2) {
                    onFitScreen();
                } else if (i < VectorRecentsListFragment.this.mFirstVisibleIndex) {
                    VectorRecentsListFragment.this.mFirstVisibleIndex = i;
                    this.mPrevOffset = 0;
                    onScrollUp();
                } else if (i > VectorRecentsListFragment.this.mFirstVisibleIndex) {
                    VectorRecentsListFragment.this.mFirstVisibleIndex = i;
                    this.mPrevOffset = 0;
                    onScrollDown();
                } else {
                    View childAt = VectorRecentsListFragment.this.mRecentsListView.getChildAt(i);
                    if (childAt != null) {
                        int top = childAt.getTop();
                        if (top > this.mPrevOffset) {
                            onScrollDown();
                        } else if (top < this.mPrevOffset) {
                            onScrollUp();
                        }
                        this.mPrevOffset = top;
                    }
                }
            }
        });
        return inflate;
    }

    public void onPause() {
        super.onPause();
        this.mIsPaused = true;
        removeSessionListener();
        PublicRoomsManager.getInstance().removeListener(this.mPublicRoomsListener);
    }

    public void onResume() {
        super.onResume();
        this.mIsPaused = false;
        addSessionListener();
        this.mAdapter.setPublicRoomsCount(PublicRoomsManager.getInstance().getPublicRoomsCount());
        notifyDataSetChanged();
        this.mRecentsListView.post(new Runnable() {
            public void run() {
                if ((PublicRoomsManager.getInstance().getPublicRoomsCount() == null || System.currentTimeMillis() - VectorRecentsListFragment.this.mLatestPublicRoomsRefresh < 300000) && !VectorRecentsListFragment.this.mIsLoadingPublicRooms) {
                    PublicRoomsManager.getInstance().refreshPublicRoomsCount(VectorRecentsListFragment.this.mPublicRoomsListener);
                }
                if (-1 != VectorRecentsListFragment.this.mScrollToIndex) {
                    VectorRecentsListFragment.this.mRecentsListView.setSelection(VectorRecentsListFragment.this.mScrollToIndex);
                    VectorRecentsListFragment.this.mScrollToIndex = -1;
                }
            }
        });
    }

    public void onDestroy() {
        this.mScrollEventListener = null;
        if (this.mRecentsListView != null) {
            this.mRecentsListView.setOnChildClickListener(null);
            this.mRecentsListView.setOnItemLongClickListener(null);
            this.mRecentsListView.setOnScrollListener(null);
            this.mRecentsListView.mDragAndDropEventsListener = null;
        }
        super.onDestroy();
    }

    private void findWaitingView() {
        if (this.mWaitingView == null) {
            this.mWaitingView = getActivity().findViewById(C1299R.C1301id.listView_spinner_views);
        }
    }

    /* access modifiers changed from: 0000 */
    public void showWaitingView() {
        findWaitingView();
        if (this.mWaitingView != null) {
            this.mWaitingView.setVisibility(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void hideWaitingView() {
        findWaitingView();
        if (this.mWaitingView != null) {
            this.mWaitingView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public IVectorRecentsScrollEventListener getListener() {
        if (this.mScrollEventListener == null && (getActivity() instanceof IVectorRecentsScrollEventListener)) {
            this.mScrollEventListener = (IVectorRecentsScrollEventListener) getActivity();
        }
        return this.mScrollEventListener;
    }

    /* access modifiers changed from: 0000 */
    public void applyFilter(String str) {
        if (this.mRecentsListView != null) {
            this.mAdapter.setSearchPattern(str);
            this.mRecentsListView.post(new Runnable() {
                public void run() {
                    VectorRecentsListFragment.this.expandsAllSections();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void expandsAllSections() {
        int groupCount = this.mAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            this.mRecentsListView.expandGroup(i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void notifyDataSetChanged() {
        this.mAdapter.notifyDataSetChanged();
        this.mRecentsListView.post(new Runnable() {
            public void run() {
                boolean z;
                if (VectorRecentsListFragment.this.getActivity() != null) {
                    int groupCount = VectorRecentsListFragment.this.mRecentsListView.getExpandableListAdapter().getGroupCount();
                    SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(VectorRecentsListFragment.this.getActivity().getApplicationContext());
                    for (int i = 0; i < groupCount; i++) {
                        if (VectorRecentsListFragment.this.mAdapter.isInvitedRoomPosition(i)) {
                            z = defaultSharedPreferences.getBoolean(VectorRecentsListFragment.KEY_EXPAND_STATE_INVITES_GROUP, true);
                        } else if (VectorRecentsListFragment.this.mAdapter.isFavouriteRoomPosition(i)) {
                            z = defaultSharedPreferences.getBoolean(VectorRecentsListFragment.KEY_EXPAND_STATE_FAVOURITE_GROUP, true);
                        } else if (VectorRecentsListFragment.this.mAdapter.isNoTagRoomPosition(i)) {
                            z = defaultSharedPreferences.getBoolean(VectorRecentsListFragment.KEY_EXPAND_STATE_ROOMS_GROUP, true);
                        } else if (VectorRecentsListFragment.this.mAdapter.isLowPriorityRoomPosition(i)) {
                            z = defaultSharedPreferences.getBoolean(VectorRecentsListFragment.KEY_EXPAND_STATE_LOW_PRIORITY_GROUP, true);
                        } else if (VectorRecentsListFragment.this.mAdapter.isDirectoryGroupPosition(i)) {
                            z = defaultSharedPreferences.getBoolean(VectorRecentsListFragment.KEY_EXPAND_STATE_LOW_PRIORITY_GROUP, true);
                        } else {
                            return;
                        }
                        if (true == z) {
                            VectorRecentsListFragment.this.mRecentsListView.expandGroup(i);
                        } else {
                            VectorRecentsListFragment.this.mRecentsListView.collapseGroup(i);
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void updateGroupExpandStatus(int i, boolean z) {
        String str;
        if (getActivity() != null) {
            if (this.mAdapter.isInvitedRoomPosition(i)) {
                str = KEY_EXPAND_STATE_INVITES_GROUP;
            } else if (this.mAdapter.isFavouriteRoomPosition(i)) {
                str = KEY_EXPAND_STATE_FAVOURITE_GROUP;
            } else if (this.mAdapter.isNoTagRoomPosition(i)) {
                str = KEY_EXPAND_STATE_ROOMS_GROUP;
            } else if (this.mAdapter.isLowPriorityRoomPosition(i)) {
                str = KEY_EXPAND_STATE_LOW_PRIORITY_GROUP;
            } else if (this.mAdapter.isDirectoryGroupPosition(i)) {
                str = KEY_EXPAND_STATE_LOW_PRIORITY_GROUP;
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## updateGroupExpandStatus(): Failure - Unknown group: ");
                sb.append(i);
                Log.m217w(str2, sb.toString());
                return;
            }
            Context applicationContext = getActivity().getApplicationContext();
            if (applicationContext != null) {
                PreferenceManager.getDefaultSharedPreferences(applicationContext).edit().putBoolean(str, z).apply();
            }
        }
    }

    private void addSessionListener() {
        this.mEventsListener = new MXEventListener() {
            /* access modifiers changed from: private */
            public boolean mInitialSyncComplete = false;

            public void onInitialSyncComplete(String str) {
                Log.m209d(VectorRecentsListFragment.LOG_TAG, "## onInitialSyncComplete()");
                VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        C21308.this.mInitialSyncComplete = true;
                        VectorRecentsListFragment.this.notifyDataSetChanged();
                    }
                });
            }

            public void onLiveEventsChunkProcessed(String str, String str2) {
                VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.m209d(VectorRecentsListFragment.LOG_TAG, "onLiveEventsChunkProcessed");
                        if (!VectorRecentsListFragment.this.mIsPaused && VectorRecentsListFragment.this.refreshOnChunkEnd && !VectorRecentsListFragment.this.mIsWaitingTagOrderEcho && !VectorRecentsListFragment.this.mIsWaitingDirectChatEcho) {
                            VectorRecentsListFragment.this.notifyDataSetChanged();
                        }
                        VectorRecentsListFragment.this.refreshOnChunkEnd = false;
                    }
                });
            }

            public void onLiveEvent(final Event event, RoomState roomState) {
                VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        String type = event.getType();
                        VectorRecentsListFragment.this.refreshOnChunkEnd = ((event.roomId != null && RoomSummary.isSupportedEvent(event)) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_TAGS.equals(type) || Event.EVENT_TYPE_REDACTION.equals(type) || Event.EVENT_TYPE_RECEIPT.equals(type) || Event.EVENT_TYPE_STATE_ROOM_AVATAR.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type)) | VectorRecentsListFragment.this.refreshOnChunkEnd;
                    }
                });
            }

            public void onReceiptEvent(String str, List<String> list) {
                VectorRecentsListFragment.this.refreshOnChunkEnd = (list.indexOf(VectorRecentsListFragment.this.mSession.getCredentials().userId) >= 0) | VectorRecentsListFragment.this.refreshOnChunkEnd;
            }

            public void onRoomTagEvent(String str) {
                VectorRecentsListFragment.this.mIsWaitingTagOrderEcho = false;
                VectorRecentsListFragment.this.refreshOnChunkEnd = true;
            }

            private void onForceRefresh() {
                if (this.mInitialSyncComplete) {
                    VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            VectorRecentsListFragment.this.notifyDataSetChanged();
                        }
                    });
                }
            }

            public void onStoreReady() {
                onForceRefresh();
            }

            public void onLeaveRoom(String str) {
                EventStreamService.cancelNotificationsForRoomId(VectorRecentsListFragment.this.mSession.getMyUserId(), str);
                onForceRefresh();
            }

            public void onNewRoom(String str) {
                onForceRefresh();
            }

            public void onJoinRoom(String str) {
                onForceRefresh();
            }

            public void onDirectMessageChatRoomsListUpdate() {
                VectorRecentsListFragment.this.mIsWaitingDirectChatEcho = false;
                VectorRecentsListFragment.this.refreshOnChunkEnd = true;
            }

            public void onEventDecrypted(Event event) {
                RoomSummary summary = VectorRecentsListFragment.this.mSession.getDataHandler().getStore().getSummary(event.roomId);
                if (summary != null) {
                    Event latestReceivedEvent = summary.getLatestReceivedEvent();
                    if (latestReceivedEvent != null && TextUtils.equals(latestReceivedEvent.eventId, event.eventId)) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        };
        this.mSession.getDataHandler().addListener(this.mEventsListener);
    }

    private void removeSessionListener() {
        if (this.mSession.isAlive()) {
            this.mSession.getDataHandler().removeListener(this.mEventsListener);
        }
    }

    public void onGroupCollapsedNotif(int i) {
        updateGroupExpandStatus(i, false);
    }

    public void onGroupExpandedNotif(int i) {
        updateGroupExpandStatus(i, true);
    }

    public void onPreviewRoom(MXSession mXSession, String str) {
        Room room = mXSession.getDataHandler().getRoom(str);
        RoomPreviewData roomPreviewData = new RoomPreviewData(this.mSession, str, null, (room == null || room.getLiveState() == null) ? null : room.getLiveState().getAlias(), null);
        CommonActivityUtils.previewRoom(getActivity(), roomPreviewData);
    }

    public void onRejectInvitation(MXSession mXSession, final String str) {
        Room room = mXSession.getDataHandler().getRoom(str);
        if (room != null) {
            showWaitingView();
            room.leave(new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                EventStreamService.cancelNotificationsForRoomId(VectorRecentsListFragment.this.mSession.getMyUserId(), str);
                                VectorRecentsListFragment.this.hideWaitingView();
                            }
                        });
                    }
                }

                private void onError(final String str) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.hideWaitingView();
                                Toast.makeText(VectorRecentsListFragment.this.getActivity(), str, 1).show();
                            }
                        });
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

    public void onLeaveRoom(final MXSession mXSession, final String str) {
        RoomUtils.showLeaveRoomDialog(getActivity(), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                VectorRecentsListFragment.this.onRejectInvitation(mXSession, str);
            }
        });
    }

    public void onForgetRoom(MXSession mXSession, final String str) {
        Room room = mXSession.getDataHandler().getRoom(str);
        if (room != null) {
            showWaitingView();
            room.forget(new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                EventStreamService.cancelNotificationsForRoomId(VectorRecentsListFragment.this.mSession.getMyUserId(), str);
                                VectorRecentsListFragment.this.hideWaitingView();
                            }
                        });
                    }
                }

                private void onError(final String str) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.hideWaitingView();
                                Toast.makeText(VectorRecentsListFragment.this.getActivity(), str, 1).show();
                            }
                        });
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

    public void addHomeScreenShortcut(MXSession mXSession, String str) {
        RoomUtils.addHomeScreenShortcut(getActivity(), mXSession, str);
    }

    public void onUpdateRoomNotificationsState(MXSession mXSession, String str, RoomNotificationState roomNotificationState) {
        BingRulesManager bingRulesManager = mXSession.getDataHandler().getBingRulesManager();
        showWaitingView();
        bingRulesManager.updateRoomNotificationState(str, roomNotificationState, new onBingRuleUpdateListener() {
            public void onBingRuleUpdateSuccess() {
                if (VectorRecentsListFragment.this.getActivity() != null) {
                    VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            VectorRecentsListFragment.this.hideWaitingView();
                        }
                    });
                }
            }

            public void onBingRuleUpdateFailure(final String str) {
                if (VectorRecentsListFragment.this.getActivity() != null) {
                    VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(VectorRecentsListFragment.this.getActivity(), str, 1).show();
                            VectorRecentsListFragment.this.hideWaitingView();
                        }
                    });
                }
            }
        });
    }

    public void onToggleDirectChat(MXSession mXSession, String str) {
        if (mXSession.getDataHandler().getRoom(str) != null) {
            showWaitingView();
            this.mIsWaitingDirectChatEcho = true;
            this.mSession.getDataHandler().addListener(this.mEventsListener);
            this.mSession.toggleDirectChatRoom(str, null, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.hideWaitingView();
                                VectorRecentsListFragment.this.stopDragAndDropMode();
                            }
                        });
                    }
                }

                private void onFails(final String str) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.mIsWaitingDirectChatEcho = false;
                                VectorRecentsListFragment.this.hideWaitingView();
                                VectorRecentsListFragment.this.stopDragAndDropMode();
                                if (!TextUtils.isEmpty(str)) {
                                    Toast.makeText(VectorRecentsListFragment.this.getActivity(), str, 1).show();
                                }
                            }
                        });
                    }
                }

                public void onNetworkError(Exception exc) {
                    onFails(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onFails(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onFails(exc.getLocalizedMessage());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void startDragAndDrop() {
        this.mIsWaitingTagOrderEcho = false;
        this.mIsWaitingDirectChatEcho = false;
        if (isDragAndDropSupported() && groupIsMovable(this.mRecentsListView.getTouchedGroupPosition())) {
            int touchedGroupPosition = this.mRecentsListView.getTouchedGroupPosition();
            int touchedChildPosition = this.mRecentsListView.getTouchedChildPosition();
            try {
                this.mDraggedView = this.mAdapter.getChildView(touchedGroupPosition, touchedChildPosition, false, null, null);
                this.mAdapter.setIsDragAndDropMode(true);
                this.mSession.getDataHandler().removeListener(this.mEventsListener);
                this.mDraggedView.setBackgroundColor(ContextCompat.getColor(getContext(), C1299R.color.vector_silver_color));
                this.mDraggedView.setAlpha(0.3f);
                LayoutParams layoutParams = new LayoutParams(-2, -2);
                layoutParams.addRule(9, -1);
                layoutParams.addRule(10, -1);
                this.mSelectedCellLayout.addView(this.mDraggedView, layoutParams);
                this.mOriginGroupPosition = touchedGroupPosition;
                this.mDestGroupPosition = touchedGroupPosition;
                this.mOriginChildPosition = touchedChildPosition;
                this.mDestChildPosition = touchedChildPosition;
                onTouchMove(this.mRecentsListView.getTouchedY(), touchedGroupPosition, touchedChildPosition);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## startDragAndDrop() : getChildView failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void onTouchMove(int i, int i2, int i3) {
        int i4;
        if (this.mDraggedView != null && !this.mIgnoreScrollEvent) {
            if (this.mSelectedCellLayout.getVisibility() != 0) {
                this.mSelectedCellLayout.setVisibility(0);
            }
            if (i < 0) {
                i4 = this.mRecentsListView.getFirstVisiblePosition() > 0 ? this.mRecentsListView.getFirstVisiblePosition() - 1 : -1;
                i = 0;
            } else {
                i4 = -1;
            }
            if (this.mSelectedCellLayout.getHeight() + i > this.mRecentsListView.getHeight()) {
                if (this.mRecentsListView.getLastVisiblePosition() < this.mRecentsListView.getCount()) {
                    i4 = this.mRecentsListView.getFirstVisiblePosition() + 2;
                }
                i = this.mRecentsListView.getHeight() - this.mSelectedCellLayout.getHeight();
            }
            LayoutParams layoutParams = new LayoutParams(this.mSelectedCellLayout.getLayoutParams());
            layoutParams.topMargin = i;
            this.mSelectedCellLayout.setLayoutParams(layoutParams);
            if (!(i2 == this.mDestGroupPosition && i3 == this.mDestChildPosition)) {
                this.mAdapter.moveChildView(this.mDestGroupPosition, this.mDestChildPosition, i2, i3);
                notifyDataSetChanged();
                this.mDestGroupPosition = i2;
                this.mDestChildPosition = i3;
            }
            if (-1 != i4) {
                this.mIgnoreScrollEvent = true;
                this.mRecentsListView.setSelection(i4);
                this.mRecentsListView.postDelayed(new Runnable() {
                    public void run() {
                        VectorRecentsListFragment.this.mIgnoreScrollEvent = false;
                    }
                }, 100);
            }
        }
    }

    public void onOverScrolled(boolean z) {
        if (z && getListener() != null) {
            this.mScrollEventListener.onRecentsListOverScrollUp();
        }
    }

    private String roomTagAt(int i) {
        if (this.mAdapter.isFavouriteRoomPosition(i)) {
            return RoomTag.ROOM_TAG_FAVOURITE;
        }
        if (this.mAdapter.isLowPriorityRoomPosition(i)) {
            return RoomTag.ROOM_TAG_LOW_PRIORITY;
        }
        return null;
    }

    private boolean groupIsMovable(int i) {
        return this.mAdapter.isNoTagRoomPosition(i) || this.mAdapter.isFavouriteRoomPosition(i) || this.mAdapter.isLowPriorityRoomPosition(i);
    }

    public void onDrop() {
        if (this.mDraggedView != null) {
            ((ViewGroup) this.mDraggedView.getParent()).removeView(this.mDraggedView);
            this.mDraggedView = null;
            this.mSelectedCellLayout.setVisibility(8);
            if (this.mOriginGroupPosition == this.mDestGroupPosition && this.mOriginChildPosition == this.mDestChildPosition) {
                stopDragAndDropMode();
            } else if (this.mAdapter.isNoTagRoomPosition(this.mOriginGroupPosition) && this.mAdapter.isNoTagRoomPosition(this.mDestGroupPosition)) {
                stopDragAndDropMode();
            } else if (!groupIsMovable(this.mDestGroupPosition)) {
                stopDragAndDropMode();
            } else {
                RoomSummary roomSummaryAt = this.mAdapter.getRoomSummaryAt(this.mDestGroupPosition, this.mDestChildPosition);
                String roomTagAt = roomTagAt(this.mDestGroupPosition);
                updateRoomTag(this.mSession, roomSummaryAt.getRoomId(), this.mSession.tagOrderToBeAtIndex(this.mDestChildPosition, this.mOriginGroupPosition == this.mDestGroupPosition ? this.mOriginChildPosition : Integer.MAX_VALUE, roomTagAt), roomTagAt);
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopDragAndDropMode() {
        if (this.mAdapter.isInDragAndDropMode()) {
            this.mSession.getDataHandler().addListener(this.mEventsListener);
            this.mAdapter.setIsDragAndDropMode(false);
            if (!this.mIsWaitingTagOrderEcho && !this.mIsWaitingDirectChatEcho) {
                notifyDataSetChanged();
            }
        }
    }

    private void updateRoomTag(MXSession mXSession, String str, Double d, String str2) {
        Room room = mXSession.getDataHandler().getRoom(str);
        if (room != null) {
            String str3 = null;
            RoomAccountData accountData = room.getAccountData();
            if (accountData != null && accountData.hasTags()) {
                str3 = (String) accountData.getKeys().iterator().next();
            }
            if (d == null) {
                d = Double.valueOf(0.0d);
                if (str2 != null) {
                    d = mXSession.tagOrderToBeAtIndex(0, Integer.MAX_VALUE, str2);
                }
            }
            showWaitingView();
            this.mIsWaitingTagOrderEcho = true;
            this.mSession.getDataHandler().addListener(this.mEventsListener);
            room.replaceTag(str3, str2, d, new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.hideWaitingView();
                                VectorRecentsListFragment.this.stopDragAndDropMode();
                            }
                        });
                    }
                }

                private void onReplaceFails(final String str) {
                    if (VectorRecentsListFragment.this.getActivity() != null) {
                        VectorRecentsListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRecentsListFragment.this.mIsWaitingTagOrderEcho = false;
                                VectorRecentsListFragment.this.hideWaitingView();
                                VectorRecentsListFragment.this.stopDragAndDropMode();
                                if (!TextUtils.isEmpty(str)) {
                                    Toast.makeText(VectorRecentsListFragment.this.getActivity(), str, 1).show();
                                }
                            }
                        });
                    }
                }

                public void onNetworkError(Exception exc) {
                    onReplaceFails(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onReplaceFails(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onReplaceFails(exc.getLocalizedMessage());
                }
            });
        }
    }

    public void moveToConversations(MXSession mXSession, String str) {
        updateRoomTag(mXSession, str, null, null);
    }

    public void moveToFavorites(MXSession mXSession, String str) {
        updateRoomTag(mXSession, str, null, RoomTag.ROOM_TAG_FAVOURITE);
    }

    public void moveToLowPriority(MXSession mXSession, String str) {
        updateRoomTag(mXSession, str, null, RoomTag.ROOM_TAG_LOW_PRIORITY);
    }
}
