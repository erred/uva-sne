package com.opengarden.firechat.matrixsdk.fragments;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.data.EventTimeline;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage.EventCreationListener;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessagesFragment.MatrixMessagesListener;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.message.MediaMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchResponse;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchResult;
import com.opengarden.firechat.matrixsdk.util.EventDisplay;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.view.AutoScrollDownListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MatrixMessageListFragment extends Fragment implements MatrixMessagesListener {
    public static final String ARG_EVENT_ID = "MatrixMessageListFragment.ARG_EVENT_ID";
    public static final String ARG_LAYOUT_ID = "MatrixMessageListFragment.ARG_LAYOUT_ID";
    public static final String ARG_MATRIX_ID = "MatrixMessageListFragment.ARG_MATRIX_ID";
    public static final String ARG_PREVIEW_MODE_ID = "MatrixMessageListFragment.ARG_PREVIEW_MODE_ID";
    public static final String ARG_ROOM_ID = "MatrixMessageListFragment.ARG_ROOM_ID";
    private static final String LOG_TAG = "MatrixMsgsListFrag";
    public static final String PREVIEW_MODE_READ_ONLY = "PREVIEW_MODE_READ_ONLY";
    public static final String PREVIEW_MODE_UNREAD_MESSAGE = "PREVIEW_MODE_UNREAD_MESSAGE";
    private static final int UNDEFINED_VIEW_Y_POS = -12345678;
    protected IOnScrollListener mActivityOnScrollListener;
    /* access modifiers changed from: protected */
    public AbstractMessagesAdapter mAdapter;
    public boolean mCheckSlideToHide = false;
    private boolean mDisplayAllEvents = true;
    private final EventCreationListener mEventCreationListener = new EventCreationListener() {
        public void onEventCreated(RoomMediaMessage roomMediaMessage) {
            MatrixMessageListFragment.this.add(roomMediaMessage);
        }

        public void onEventCreationFailed(RoomMediaMessage roomMediaMessage, String str) {
            MatrixMessageListFragment.this.displayMessageSendingFailed(str);
        }

        public void onEncryptionFailed(RoomMediaMessage roomMediaMessage) {
            MatrixMessageListFragment.this.displayEncryptionAlert();
        }
    };
    protected String mEventId;
    protected long mEventOriginServerTs;
    /* access modifiers changed from: protected */
    public IEventSendingListener mEventSendingListener;
    protected EventTimeline mEventTimeLine;
    private final IMXEventListener mEventsListener = new MXEventListener() {
        /* access modifiers changed from: private */
        public boolean mRefreshAfterEventsDecryption;

        public void onEventSentStateUpdated(Event event) {
            MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                public void run() {
                    MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                }
            });
        }

        public void onEventDecrypted(final Event event) {
            MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                public void run() {
                    if (C26471.this.mRefreshAfterEventsDecryption) {
                        String str = MatrixMessageListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## onEventDecrypted ");
                        sb.append(event.eventId);
                        sb.append(" : there is a pending refresh");
                        Log.m209d(str, sb.toString());
                        return;
                    }
                    String str2 = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onEventDecrypted ");
                    sb2.append(event.eventId);
                    Log.m209d(str2, sb2.toString());
                    C26471.this.mRefreshAfterEventsDecryption = true;
                    MatrixMessageListFragment.this.getUiHandler().postDelayed(new Runnable() {
                        public void run() {
                            Log.m209d(MatrixMessageListFragment.LOG_TAG, "## onEventDecrypted : refresh the list");
                            C26471.this.mRefreshAfterEventsDecryption = false;
                            MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            });
        }
    };
    private boolean mFillHistoryOnResume;
    private int mFirstVisibleRow = -1;
    /* access modifiers changed from: private */
    public int mFirstVisibleRowY = UNDEFINED_VIEW_Y_POS;
    private String mFutureReadMarkerEventId;
    /* access modifiers changed from: protected */
    public boolean mIsBackPaginating = false;
    protected boolean mIsFwdPaginating = false;
    /* access modifiers changed from: protected */
    public boolean mIsInitialSyncing = true;
    protected final boolean mIsLive = true;
    protected boolean mIsMediaSearch;
    private boolean mIsScrollListenerSet;
    /* access modifiers changed from: private */
    public boolean mLockBackPagination = false;
    /* access modifiers changed from: private */
    public boolean mLockFwdPagination = true;
    protected String mMatrixId;
    /* access modifiers changed from: private */
    public MatrixMessagesFragment mMatrixMessagesFragment;
    public AutoScrollDownListView mMessageListView;
    /* access modifiers changed from: protected */
    public String mNextBatch = null;
    protected String mPattern = null;
    /* access modifiers changed from: private */
    public final HashMap<String, Timer> mPendingRelaunchTimersByEventId = new HashMap<>();
    /* access modifiers changed from: protected */
    public Room mRoom;
    protected IRoomPreviewDataListener mRoomPreviewDataListener;
    /* access modifiers changed from: protected */
    public final OnScrollListener mScrollListener = new OnScrollListener() {
        public void onScrollStateChanged(AbsListView absListView, int i) {
            MatrixMessageListFragment.this.mCheckSlideToHide = i == 1;
            if (i == 1) {
                int firstVisiblePosition = MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition();
                if (MatrixMessageListFragment.this.mMessageListView.getLastVisiblePosition() + 10 >= MatrixMessageListFragment.this.mMessageListView.getCount()) {
                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "onScrollStateChanged - forwardPaginate");
                    MatrixMessageListFragment.this.forwardPaginate();
                } else if (firstVisiblePosition < 10) {
                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "onScrollStateChanged - request history");
                    MatrixMessageListFragment.this.backPaginate(false);
                }
            }
            if (MatrixMessageListFragment.this.mActivityOnScrollListener != null) {
                try {
                    MatrixMessageListFragment.this.mActivityOnScrollListener.onScrollStateChanged(i);
                } catch (Exception e) {
                    String str = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## manageScrollListener : onScrollStateChanged failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }

        private void manageScrollListener(AbsListView absListView, int i, int i2, int i3) {
            if (MatrixMessageListFragment.this.mActivityOnScrollListener != null) {
                try {
                    MatrixMessageListFragment.this.mActivityOnScrollListener.onScroll(i, i2, i3);
                } catch (Exception e) {
                    String str = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## manageScrollListener : onScroll failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
                boolean z = false;
                if (i + i2 >= i3) {
                    View childAt = absListView.getChildAt(i2 - 1);
                    if (childAt != null && childAt.getTop() + childAt.getHeight() <= absListView.getHeight()) {
                        z = true;
                    }
                }
                try {
                    MatrixMessageListFragment.this.mActivityOnScrollListener.onLatestEventDisplay(z);
                } catch (Exception e2) {
                    String str2 = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## manageScrollListener : onLatestEventDisplay failed ");
                    sb2.append(e2.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            MatrixMessageListFragment.this.mFirstVisibleRowY = MatrixMessageListFragment.UNDEFINED_VIEW_Y_POS;
            View childAt = MatrixMessageListFragment.this.mMessageListView.getChildAt(i2 == MatrixMessageListFragment.this.mMessageListView.getChildCount() ? 0 : i);
            if (childAt != null) {
                MatrixMessageListFragment.this.mFirstVisibleRowY = childAt.getTop();
            }
            if (i < 10 && i2 != i3 && i2 != 0) {
                if (!MatrixMessageListFragment.this.mLockBackPagination) {
                    String str = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onScroll - backPaginate firstVisibleItem ");
                    sb.append(i);
                    sb.append(" visibleItemCount ");
                    sb.append(i2);
                    sb.append(" totalItemCount ");
                    sb.append(i3);
                    Log.m209d(str, sb.toString());
                }
                MatrixMessageListFragment.this.backPaginate(false);
            } else if (i + i2 + 10 >= i3) {
                if (!MatrixMessageListFragment.this.mLockFwdPagination) {
                    String str2 = MatrixMessageListFragment.LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("onScroll - forwardPaginate firstVisibleItem ");
                    sb2.append(i);
                    sb2.append(" visibleItemCount ");
                    sb2.append(i2);
                    sb2.append(" totalItemCount ");
                    sb2.append(i3);
                    Log.m209d(str2, sb2.toString());
                }
                MatrixMessageListFragment.this.forwardPaginate();
            }
            manageScrollListener(absListView, i, i2, i3);
        }
    };
    /* access modifiers changed from: private */
    public int mScrollToIndex = -1;
    /* access modifiers changed from: protected */
    public MXSession mSession;
    /* access modifiers changed from: protected */
    public Handler mUiHandler;

    public interface IEventSendingListener {
        void onConsentNotGiven(Event event, MatrixError matrixError);

        void onMessageRedacted(Event event);

        void onMessageSendingFailed(Event event);

        void onMessageSendingSucceeded(Event event);

        void onUnknownDevices(Event event, MXCryptoError mXCryptoError);
    }

    public interface IOnScrollListener {
        void onLatestEventDisplay(boolean z);

        void onScroll(int i, int i2, int i3);

        void onScrollStateChanged(int i);
    }

    public interface IRoomPreviewDataListener {
        RoomPreviewData getRoomPreviewData();
    }

    public interface OnSearchResultListener {
        void onSearchFailed();

        void onSearchSucceed(int i);
    }

    public AbstractMessagesAdapter createMessagesAdapter() {
        return null;
    }

    public MXMediasCache getMXMediasCache() {
        return null;
    }

    /* access modifiers changed from: protected */
    public String getMatrixMessagesFragmentTag() {
        return "com.opengarden.firechat.matrixsdk.RoomActivity.TAG_FRAGMENT_MATRIX_MESSAGES";
    }

    public MXSession getSession(String str) {
        return null;
    }

    public void hideInitLoading() {
    }

    public void hideLoadingBackProgress() {
    }

    public void hideLoadingForwardProgress() {
    }

    public boolean isDisplayAllEvents() {
        return true;
    }

    public void onListTouch(MotionEvent motionEvent) {
    }

    public void onLiveEventsChunkProcessed() {
    }

    public void showInitLoading() {
    }

    public void showLoadingBackProgress() {
    }

    public void showLoadingForwardProgress() {
    }

    public static MatrixMessageListFragment newInstance(String str, String str2, int i) {
        MatrixMessageListFragment matrixMessageListFragment = new MatrixMessageListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ROOM_ID, str2);
        bundle.putInt(ARG_LAYOUT_ID, i);
        bundle.putString(ARG_MATRIX_ID, str);
        return matrixMessageListFragment;
    }

    public MXSession getSession() {
        if (this.mSession == null) {
            this.mSession = getSession(this.mMatrixId);
        }
        return this.mSession;
    }

    /* access modifiers changed from: private */
    public Handler getUiHandler() {
        if (this.mUiHandler == null) {
            this.mUiHandler = new Handler(Looper.getMainLooper());
        }
        return this.mUiHandler;
    }

    public void onCreate(Bundle bundle) {
        Log.m209d(LOG_TAG, "onCreate");
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m209d(LOG_TAG, "onCreateView");
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        this.mUiHandler = new Handler(Looper.getMainLooper());
        this.mMatrixId = arguments.getString(ARG_MATRIX_ID);
        this.mSession = getSession(this.mMatrixId);
        if (this.mSession == null) {
            if (getActivity() != null) {
                Log.m211e(LOG_TAG, "Must have valid default MXSession.");
                getActivity().finish();
                return onCreateView;
            }
            throw new RuntimeException("Must have valid default MXSession.");
        } else if (getMXMediasCache() != null) {
            String string = arguments.getString(ARG_ROOM_ID);
            View inflate = layoutInflater.inflate(arguments.getInt(ARG_LAYOUT_ID), viewGroup, false);
            this.mMessageListView = (AutoScrollDownListView) inflate.findViewById(C1299R.C1301id.listView_messages);
            this.mIsScrollListenerSet = false;
            if (this.mAdapter == null) {
                this.mAdapter = createMessagesAdapter();
                if (getMXMediasCache() == null) {
                    throw new RuntimeException("Must have valid default MessagesAdapter.");
                }
            } else if (bundle != null) {
                this.mFirstVisibleRow = bundle.getInt("FIRST_VISIBLE_ROW", -1);
            }
            this.mAdapter.setIsPreviewMode(false);
            if (this.mEventTimeLine == null) {
                this.mEventId = arguments.getString(ARG_EVENT_ID);
                String string2 = arguments.getString(ARG_PREVIEW_MODE_ID);
                if (!TextUtils.isEmpty(this.mEventId)) {
                    this.mEventTimeLine = new EventTimeline(this.mSession.getDataHandler(), string, this.mEventId);
                    this.mRoom = this.mEventTimeLine.getRoom();
                    if (PREVIEW_MODE_UNREAD_MESSAGE.equals(string2)) {
                        this.mAdapter.setIsUnreadViewMode(true);
                    }
                } else if (PREVIEW_MODE_READ_ONLY.equals(string2)) {
                    this.mAdapter.setIsPreviewMode(true);
                    this.mEventTimeLine = new EventTimeline(this.mSession.getDataHandler(), string);
                    this.mRoom = this.mEventTimeLine.getRoom();
                } else if (!TextUtils.isEmpty(string)) {
                    this.mRoom = this.mSession.getDataHandler().getRoom(string);
                    this.mEventTimeLine = this.mRoom.getLiveTimeLine();
                }
            }
            this.mSession.getDataHandler().checkRoom(this.mRoom);
            this.mMessageListView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    MatrixMessageListFragment.this.onListTouch(motionEvent);
                    return false;
                }
            });
            this.mDisplayAllEvents = isDisplayAllEvents();
            return inflate;
        } else if (getActivity() != null) {
            Log.m211e(LOG_TAG, "Must have valid default MediasCache.");
            getActivity().finish();
            return onCreateView;
        } else {
            throw new RuntimeException("Must have valid default MediasCache.");
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mMessageListView != null) {
            int firstVisiblePosition = this.mMessageListView.getFirstVisiblePosition();
            if (firstVisiblePosition > 0) {
                firstVisiblePosition++;
            }
            bundle.putInt("FIRST_VISIBLE_ROW", firstVisiblePosition);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mMatrixMessagesFragment != null) {
            this.mMatrixMessagesFragment.setMatrixMessagesListener(null);
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Bundle arguments = getArguments();
        FragmentManager childFragmentManager = getChildFragmentManager();
        this.mMatrixMessagesFragment = (MatrixMessagesFragment) childFragmentManager.findFragmentByTag(getMatrixMessagesFragmentTag());
        if (this.mMatrixMessagesFragment == null) {
            Log.m209d(LOG_TAG, "onActivityCreated create");
            this.mMatrixMessagesFragment = createMessagesFragmentInstance(arguments.getString(ARG_ROOM_ID));
            childFragmentManager.beginTransaction().add((Fragment) this.mMatrixMessagesFragment, getMatrixMessagesFragmentTag()).commit();
        } else {
            Log.m209d(LOG_TAG, "onActivityCreated - reuse");
            this.mMatrixMessagesFragment.setMatrixMessagesListener(this);
            this.mMatrixMessagesFragment.setMXSession(getSession());
        }
        this.mMatrixMessagesFragment.mKeepRoomHistory = -1 != this.mFirstVisibleRow;
    }

    public void onPause() {
        super.onPause();
        this.mEventSendingListener = null;
        this.mActivityOnScrollListener = null;
        this.mEventSendingListener = null;
        this.mActivityOnScrollListener = null;
        if (this.mRoom != null) {
            this.mRoom.removeEventListener(this.mEventsListener);
        }
        cancelCatchingRequests();
    }

    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity instanceof IEventSendingListener) {
            this.mEventSendingListener = (IEventSendingListener) activity;
        }
        if (activity instanceof IOnScrollListener) {
            this.mActivityOnScrollListener = (IOnScrollListener) activity;
        }
        if (this.mRoom != null && this.mEventTimeLine.isLiveTimeline()) {
            Room room = this.mSession.getDataHandler().getRoom(this.mRoom.getRoomId(), false);
            if (room != null) {
                room.addEventListener(this.mEventsListener);
            } else {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("the room ");
                sb.append(this.mRoom.getRoomId());
                sb.append(" does not exist anymore");
                Log.m211e(str, sb.toString());
            }
        }
        if (this.mFillHistoryOnResume) {
            this.mFillHistoryOnResume = false;
            backPaginate(true);
        }
    }

    public MatrixMessagesFragment createMessagesFragmentInstance(String str) {
        return MatrixMessagesFragment.newInstance(getSession(), str, this);
    }

    public void scrollToIndexWhenLoaded(int i) {
        this.mScrollToIndex = i;
    }

    public int getMaxThumbnailWidth() {
        return this.mAdapter.getMaxThumbnailWidth();
    }

    public int getMaxThumbnailHeight() {
        return this.mAdapter.getMaxThumbnailHeight();
    }

    public void onBingRulesUpdate() {
        this.mAdapter.onBingRulesUpdate();
    }

    public void scrollToBottom(int i) {
        this.mMessageListView.postDelayed(new Runnable() {
            public void run() {
                MatrixMessageListFragment.this.mMessageListView.setSelection(MatrixMessageListFragment.this.mAdapter.getCount() - 1);
            }
        }, (long) Math.max(i, 0));
    }

    public void scrollToBottom() {
        scrollToBottom(300);
    }

    public Event getEvent(int i) {
        if (this.mAdapter.getCount() > i) {
            return ((MessageRow) this.mAdapter.getItem(i)).getEvent();
        }
        return null;
    }

    private boolean canUpdateReadMarker(MessageRow messageRow, MessageRow messageRow2) {
        if (messageRow2 == null || this.mAdapter.getPosition(messageRow) != this.mAdapter.getPosition(messageRow2) + 1 || messageRow.getEvent().getOriginServerTs() <= messageRow2.getEvent().originServerTs) {
            return false;
        }
        return true;
    }

    private MessageRow getReadMarkerMessageRow(MessageRow messageRow) {
        String readMarkerEventId = this.mRoom.getReadMarkerEventId();
        MessageRow messageRow2 = this.mAdapter.getMessageRow(readMarkerEventId);
        if (messageRow2 != null) {
            return messageRow2;
        }
        try {
            Event event = this.mSession.getDataHandler().getStore().getEvent(readMarkerEventId, this.mRoom.getRoomId());
            if (event == null || canAddEvent(event)) {
                return messageRow2;
            }
            MessageRow closestRowFromTs = this.mAdapter.getClosestRowFromTs(event.eventId, event.getOriginServerTs());
            if (closestRowFromTs != null) {
                try {
                    if (!canUpdateReadMarker(messageRow, closestRowFromTs)) {
                        closestRowFromTs = null;
                    }
                } catch (Exception e) {
                    e = e;
                    messageRow2 = closestRowFromTs;
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## getReadMarkerMessageRow() failed : ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                    return messageRow2;
                }
            }
            return closestRowFromTs == null ? this.mAdapter.getClosestRowBeforeTs(event.eventId, event.getOriginServerTs()) : closestRowFromTs;
        } catch (Exception e2) {
            e = e2;
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getReadMarkerMessageRow() failed : ");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
            return messageRow2;
        }
    }

    private MessageRow addMessageRow(RoomMediaMessage roomMediaMessage) {
        if (this.mRoom == null) {
            return null;
        }
        Event event = roomMediaMessage.getEvent();
        MessageRow messageRow = new MessageRow(event, this.mRoom.getState());
        this.mAdapter.add(messageRow);
        if (canUpdateReadMarker(messageRow, getReadMarkerMessageRow(messageRow))) {
            View childAt = this.mMessageListView.getChildAt(this.mMessageListView.getChildCount() - 1);
            if (childAt != null && childAt.getTop() >= 0) {
                this.mFutureReadMarkerEventId = event.eventId;
                this.mAdapter.resetReadMarker();
            }
        }
        scrollToBottom();
        getSession().getDataHandler().getStore().commit();
        return messageRow;
    }

    /* access modifiers changed from: protected */
    public void redactEvent(String str) {
        this.mMatrixMessagesFragment.redact(str, new ApiCallback<Event>() {
            public void onSuccess(final Event event) {
                if (event != null) {
                    MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                        public void run() {
                            Event event = new Event();
                            event.roomId = event.roomId;
                            event.redacts = event.eventId;
                            event.setType(Event.EVENT_TYPE_REDACTION);
                            MatrixMessageListFragment.this.onEvent(event, Direction.FORWARDS, MatrixMessageListFragment.this.mRoom.getState());
                            if (MatrixMessageListFragment.this.mEventSendingListener != null) {
                                try {
                                    MatrixMessageListFragment.this.mEventSendingListener.onMessageRedacted(event);
                                } catch (Exception e) {
                                    String str = MatrixMessageListFragment.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("redactEvent fails : ");
                                    sb.append(e.getMessage());
                                    Log.m211e(str, sb.toString());
                                }
                            }
                        }
                    });
                }
            }

            private void onError() {
                if (MatrixMessageListFragment.this.getActivity() != null) {
                    Toast.makeText(MatrixMessageListFragment.this.getActivity(), MatrixMessageListFragment.this.getActivity().getString(C1299R.string.could_not_redact), 0).show();
                }
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
    }

    /* access modifiers changed from: protected */
    public boolean canAddEvent(Event event) {
        String type = event.getType();
        return this.mDisplayAllEvents || Event.EVENT_TYPE_MESSAGE.equals(type) || Event.EVENT_TYPE_MESSAGE_ENCRYPTED.equals(type) || Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type) || Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) || Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type) || Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type) || Event.EVENT_TYPE_STICKER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_CREATE.equals(type) || (event.isCallEvent() && !Event.EVENT_TYPE_CALL_CANDIDATES.equals(type));
    }

    /* access modifiers changed from: private */
    public void onMessageSendingFailed(Event event) {
        if (this.mEventSendingListener != null) {
            try {
                this.mEventSendingListener.onMessageSendingFailed(event);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onMessageSendingFailed failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void onMessageSendingSucceeded(Event event) {
        if (this.mEventSendingListener != null) {
            try {
                this.mEventSendingListener.onMessageSendingSucceeded(event);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onMessageSendingSucceeded failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void onUnknownDevices(Event event, MXCryptoError mXCryptoError) {
        if (this.mEventSendingListener != null) {
            try {
                this.mEventSendingListener.onUnknownDevices(event, mXCryptoError);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onUnknownDevices failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void onConsentNotGiven(Event event, MatrixError matrixError) {
        if (this.mEventSendingListener != null) {
            try {
                this.mEventSendingListener.onConsentNotGiven(event, matrixError);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onConsentNotGiven failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void add(RoomMediaMessage roomMediaMessage) {
        MessageRow addMessageRow = addMessageRow(roomMediaMessage);
        if (addMessageRow != null) {
            final Event event = addMessageRow.getEvent();
            if (!event.isUndeliverable()) {
                roomMediaMessage.setEventSendingCallback(new ApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                            public void run() {
                                MatrixMessageListFragment.this.onMessageSendingSucceeded(event);
                            }
                        });
                    }

                    private void commonFailure(final Event event) {
                        MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                            public void run() {
                                FragmentActivity activity = MatrixMessageListFragment.this.getActivity();
                                if (activity != null) {
                                    if (event.unsentException != null && event.isUndeliverable()) {
                                        MatrixMessageListFragment.this.resend(event);
                                    } else if (event.unsentMatrixError != null) {
                                        String detailedErrorDescription = event.unsentMatrixError instanceof MXCryptoError ? ((MXCryptoError) event.unsentMatrixError).getDetailedErrorDescription() : event.unsentMatrixError.getLocalizedMessage();
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(activity.getString(C1299R.string.unable_to_send_message));
                                        sb.append(" : ");
                                        sb.append(detailedErrorDescription);
                                        Toast.makeText(activity, sb.toString(), 1).show();
                                    }
                                    MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                                    MatrixMessageListFragment.this.onMessageSendingFailed(event);
                                }
                            }
                        });
                    }

                    public void onNetworkError(Exception exc) {
                        commonFailure(event);
                    }

                    public void onMatrixError(final MatrixError matrixError) {
                        if (event.mSentState == SentState.FAILED_UNKNOWN_DEVICES) {
                            MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                                public void run() {
                                    MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                                    MatrixMessageListFragment.this.onUnknownDevices(event, (MXCryptoError) matrixError);
                                }
                            });
                        } else if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                            MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                                public void run() {
                                    MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                                    MatrixMessageListFragment.this.onConsentNotGiven(event, matrixError);
                                }
                            });
                        } else {
                            commonFailure(event);
                        }
                    }

                    public void onUnexpectedError(Exception exc) {
                        commonFailure(event);
                    }
                });
            }
        }
    }

    public void sendTextMessage(String str) {
        sendTextMessage(Message.MSGTYPE_TEXT, str, null);
    }

    public void sendTextMessage(String str, String str2, String str3) {
        this.mRoom.sendTextMessage(str, str2, str3, this.mEventCreationListener);
    }

    public void sendTextMessage(String str, String str2, @Nullable Event event, String str3) {
        this.mRoom.sendTextMessage(str, str2, str3, event, this.mEventCreationListener);
    }

    public void sendEmote(String str, String str2, String str3) {
        this.mRoom.sendEmoteMessage(str, str2, str3, this.mEventCreationListener);
    }

    public void sendStickerMessage(Event event) {
        this.mRoom.sendStickerMessage(event, this.mEventCreationListener);
    }

    /* access modifiers changed from: private */
    public void commonMediaUploadError(int i, String str, final MessageRow messageRow) {
        if (i == 500) {
            messageRow.getEvent().mSentState = SentState.WAITING_RETRY;
            try {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        if (MatrixMessageListFragment.this.mPendingRelaunchTimersByEventId.containsKey(messageRow.getEvent().eventId)) {
                            MatrixMessageListFragment.this.mPendingRelaunchTimersByEventId.remove(messageRow.getEvent().eventId);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    MatrixMessageListFragment.this.resend(messageRow.getEvent());
                                }
                            });
                        }
                    }
                }, 1000);
                this.mPendingRelaunchTimersByEventId.put(messageRow.getEvent().eventId, timer);
            } catch (Throwable th) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("relaunchTimer.schedule failed ");
                sb.append(th.getMessage());
                Log.m211e(str2, sb.toString());
            }
        } else {
            messageRow.getEvent().mSentState = SentState.UNDELIVERABLE;
            onMessageSendingFailed(messageRow.getEvent());
            this.mAdapter.notifyDataSetChanged();
            if (getActivity() != null) {
                FragmentActivity activity = getActivity();
                if (str == null) {
                    str = getString(C1299R.string.message_failed_to_upload);
                }
                Toast.makeText(activity, str, 1).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public void displayEncryptionAlert() {
        if (getActivity() != null) {
            new Builder(getActivity()).setMessage("Fail to encrypt?").setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setIcon(17301543).show();
        }
    }

    /* access modifiers changed from: private */
    public void displayMessageSendingFailed(String str) {
        if (getActivity() != null) {
            new Builder(getActivity()).setMessage(str).setPositiveButton(17039370, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            }).setIcon(17301543).show();
        }
    }

    public void sendMediaMessage(final RoomMediaMessage roomMediaMessage) {
        this.mRoom.sendMediaMessage(roomMediaMessage, getMaxThumbnailWidth(), getMaxThumbnailHeight(), this.mEventCreationListener);
        roomMediaMessage.setMediaUploadListener(new MXMediaUploadListener() {
            public void onUploadStart(String str) {
                MatrixMessageListFragment.this.onMessageSendingSucceeded(roomMediaMessage.getEvent());
                MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
            }

            public void onUploadCancel(String str) {
                MatrixMessageListFragment.this.onMessageSendingFailed(roomMediaMessage.getEvent());
                MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
            }

            public void onUploadError(String str, int i, String str2) {
                MatrixMessageListFragment.this.commonMediaUploadError(i, str2, MatrixMessageListFragment.this.mAdapter.getMessageRow(roomMediaMessage.getEvent().eventId));
            }

            public void onUploadComplete(String str, String str2) {
                String str3 = MatrixMessageListFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Uploaded to ");
                sb.append(str2);
                Log.m209d(str3, sb.toString());
            }
        });
    }

    public void deleteUnsentEvents() {
        List<Event> unsentEvents = this.mRoom.getUnsentEvents();
        this.mRoom.deleteEvents(unsentEvents);
        for (Event event : unsentEvents) {
            this.mAdapter.removeEventById(event.eventId);
        }
        this.mAdapter.notifyDataSetChanged();
    }

    public void resendUnsentMessages() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    MatrixMessageListFragment.this.resendUnsentMessages();
                }
            });
            return;
        }
        for (Event resend : this.mRoom.getUnsentEvents()) {
            resend(resend);
        }
    }

    /* access modifiers changed from: protected */
    public void resend(final Event event) {
        if (event.eventId == null) {
            Log.m211e(LOG_TAG, "resend : got an event with a null eventId");
        } else if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    MatrixMessageListFragment.this.resend(event);
                }
            });
        } else {
            event.originServerTs = System.currentTimeMillis();
            getSession().getDataHandler().deleteRoomEvent(event);
            this.mAdapter.removeEventById(event.eventId);
            this.mPendingRelaunchTimersByEventId.remove(event.eventId);
            Message message = JsonUtils.toMessage(event.getContent());
            RoomMediaMessage roomMediaMessage = new RoomMediaMessage(new Event(message, this.mSession.getMyUserId(), this.mRoom.getRoomId()));
            if (message instanceof MediaMessage) {
                sendMediaMessage(roomMediaMessage);
            } else {
                this.mRoom.sendMediaMessage(roomMediaMessage, getMaxThumbnailWidth(), getMaxThumbnailHeight(), this.mEventCreationListener);
            }
        }
    }

    public void refresh() {
        this.mAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void onPaginateRequestError(Object obj) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            if (obj instanceof Exception) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Network error: ");
                sb.append(((Exception) obj).getMessage());
                Log.m211e(str, sb.toString());
                Toast.makeText(activity, activity.getString(C1299R.string.network_error), 0).show();
            } else if (obj instanceof MatrixError) {
                MatrixError matrixError = (MatrixError) obj;
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Matrix error : ");
                sb2.append(matrixError.errcode);
                sb2.append(" - ");
                sb2.append(matrixError.getMessage());
                Log.m211e(str2, sb2.toString());
                StringBuilder sb3 = new StringBuilder();
                sb3.append(activity.getString(C1299R.string.matrix_error));
                sb3.append(" : ");
                sb3.append(matrixError.getLocalizedMessage());
                Toast.makeText(activity, sb3.toString(), 0).show();
            }
            hideLoadingBackProgress();
            hideLoadingForwardProgress();
            String str3 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("requestHistory failed ");
            sb4.append(obj);
            Log.m209d(str3, sb4.toString());
            this.mIsBackPaginating = false;
        }
    }

    /* access modifiers changed from: private */
    public void forwardPaginate() {
        if (this.mLockFwdPagination) {
            Log.m209d(LOG_TAG, "The forward pagination is locked.");
        } else if (this.mEventTimeLine != null && !this.mEventTimeLine.isLiveTimeline()) {
            if (this.mIsFwdPaginating) {
                Log.m209d(LOG_TAG, "A forward pagination is in progress, please wait.");
            } else if (!isResumed()) {
                Log.m209d(LOG_TAG, "ignore forward pagination because the fragment is not active");
            } else {
                showLoadingForwardProgress();
                final int count = this.mAdapter.getCount();
                this.mIsFwdPaginating = this.mEventTimeLine.forwardPaginate(new ApiCallback<Integer>() {
                    /* access modifiers changed from: private */
                    public void onEndOfPagination(String str) {
                        if (str != null) {
                            String str2 = MatrixMessageListFragment.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("forwardPaginate fails : ");
                            sb.append(str);
                            Log.m211e(str2, sb.toString());
                        }
                        MatrixMessageListFragment.this.mIsFwdPaginating = false;
                        MatrixMessageListFragment.this.hideLoadingForwardProgress();
                    }

                    public void onSuccess(Integer num) {
                        int firstVisiblePosition = MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition();
                        MatrixMessageListFragment.this.mLockBackPagination = true;
                        if (num.intValue() != 0) {
                            MatrixMessageListFragment.this.mMessageListView.lockSelectionOnResize();
                            MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                            MatrixMessageListFragment.this.mMessageListView.setSelection(firstVisiblePosition);
                            MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                public void run() {
                                    int count = MatrixMessageListFragment.this.mAdapter.getCount() - count;
                                    String str = MatrixMessageListFragment.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("forwardPaginate ends with ");
                                    sb.append(count);
                                    sb.append(" new items.");
                                    Log.m209d(str, sb.toString());
                                    C265514.this.onEndOfPagination(null);
                                    MatrixMessageListFragment.this.mLockBackPagination = false;
                                }
                            });
                            return;
                        }
                        Log.m209d(MatrixMessageListFragment.LOG_TAG, "forwardPaginate ends : nothing to add");
                        onEndOfPagination(null);
                        MatrixMessageListFragment.this.mLockBackPagination = false;
                    }

                    public void onNetworkError(Exception exc) {
                        onEndOfPagination(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        onEndOfPagination(matrixError.getLocalizedMessage());
                    }

                    public void onUnexpectedError(Exception exc) {
                        onEndOfPagination(exc.getLocalizedMessage());
                    }
                });
                if (this.mIsFwdPaginating) {
                    Log.m209d(LOG_TAG, "forwardPaginate starts");
                    showLoadingForwardProgress();
                } else {
                    hideLoadingForwardProgress();
                    Log.m209d(LOG_TAG, "forwardPaginate nothing to do");
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void setMessageListViewScrollListener() {
        if (!this.mIsScrollListenerSet) {
            this.mIsScrollListenerSet = true;
            this.mMessageListView.setOnScrollListener(this.mScrollListener);
        }
    }

    public void backPaginate(final boolean z) {
        if (this.mIsBackPaginating) {
            Log.m209d(LOG_TAG, "backPaginate is in progress : please wait");
        } else if (this.mIsInitialSyncing) {
            Log.m209d(LOG_TAG, "backPaginate : an initial sync is in progress");
        } else if (this.mLockBackPagination) {
            Log.m209d(LOG_TAG, "backPaginate : The back pagination is locked.");
        } else if (!TextUtils.isEmpty(this.mPattern)) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("backPaginate with pattern ");
            sb.append(this.mPattern);
            Log.m209d(str, sb.toString());
            requestSearchHistory();
        } else if (!this.mMatrixMessagesFragment.canBackPaginate()) {
            Log.m209d(LOG_TAG, "backPaginate : cannot back paginating again");
            setMessageListViewScrollListener();
        } else if (!isResumed()) {
            Log.m209d(LOG_TAG, "backPaginate : the fragment is not anymore active");
            this.mFillHistoryOnResume = true;
        } else {
            final int count = this.mAdapter.getCount();
            this.mIsBackPaginating = this.mMatrixMessagesFragment.backPaginate(new SimpleApiCallback<Integer>(getActivity()) {
                public void onSuccess(Integer num) {
                    MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                        public void run() {
                            MatrixMessageListFragment.this.mLockFwdPagination = true;
                            final int count = MatrixMessageListFragment.this.mAdapter.getCount() - count;
                            int firstVisiblePosition = MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition();
                            String str = MatrixMessageListFragment.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("backPaginate : ends with ");
                            sb.append(count);
                            sb.append(" new items (total : ");
                            sb.append(MatrixMessageListFragment.this.mAdapter.getCount());
                            sb.append(")");
                            Log.m209d(str, sb.toString());
                            if (count != 0) {
                                MatrixMessageListFragment.this.mMessageListView.lockSelectionOnResize();
                                MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                                int count2 = z ? MatrixMessageListFragment.this.mAdapter.getCount() - 1 : firstVisiblePosition + count;
                                String str2 = MatrixMessageListFragment.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("backPaginate : expect to jump to ");
                                sb2.append(count2);
                                Log.m209d(str2, sb2.toString());
                                if (z || MatrixMessageListFragment.UNDEFINED_VIEW_Y_POS == MatrixMessageListFragment.this.mFirstVisibleRowY) {
                                    MatrixMessageListFragment.this.mMessageListView.setSelection(count2);
                                } else {
                                    MatrixMessageListFragment.this.mMessageListView.setSelectionFromTop(count2, -MatrixMessageListFragment.this.mFirstVisibleRowY);
                                }
                                MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                    public void run() {
                                        String str = MatrixMessageListFragment.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("backPaginate : jump to ");
                                        sb.append(MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition());
                                        Log.m209d(str, sb.toString());
                                    }
                                });
                            }
                            if (MatrixMessageListFragment.this.mMatrixMessagesFragment.canBackPaginate()) {
                                Log.m209d(MatrixMessageListFragment.LOG_TAG, "backPaginate again");
                                MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                    public void run() {
                                        MatrixMessageListFragment.this.mLockFwdPagination = false;
                                        MatrixMessageListFragment.this.mIsBackPaginating = false;
                                        MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                            public void run() {
                                                if (count == 0) {
                                                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "backPaginate again because there was nothing in the current chunk");
                                                    MatrixMessageListFragment.this.backPaginate(z);
                                                } else if (!z) {
                                                    MatrixMessageListFragment.this.hideLoadingBackProgress();
                                                } else if (MatrixMessageListFragment.this.mMessageListView.getVisibility() != 0 || MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition() >= 10) {
                                                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "backPaginate : history should be filled");
                                                    MatrixMessageListFragment.this.hideLoadingBackProgress();
                                                    MatrixMessageListFragment.this.mIsInitialSyncing = false;
                                                    MatrixMessageListFragment.this.setMessageListViewScrollListener();
                                                } else {
                                                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "backPaginate : fill history");
                                                    MatrixMessageListFragment.this.backPaginate(z);
                                                }
                                            }
                                        });
                                    }
                                });
                                return;
                            }
                            Log.m209d(MatrixMessageListFragment.LOG_TAG, "no more backPaginate");
                            MatrixMessageListFragment.this.setMessageListViewScrollListener();
                            MatrixMessageListFragment.this.hideLoadingBackProgress();
                            MatrixMessageListFragment.this.mIsBackPaginating = false;
                            MatrixMessageListFragment.this.mLockFwdPagination = false;
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    MatrixMessageListFragment.this.onPaginateRequestError(exc);
                }

                public void onMatrixError(MatrixError matrixError) {
                    MatrixMessageListFragment.this.onPaginateRequestError(matrixError);
                }

                public void onUnexpectedError(Exception exc) {
                    MatrixMessageListFragment.this.onPaginateRequestError(exc);
                }
            });
            if (!this.mIsBackPaginating || getActivity() == null) {
                Log.m209d(LOG_TAG, "requestHistory : nothing to do");
            } else {
                Log.m209d(LOG_TAG, "backPaginate : starts");
                showLoadingBackProgress();
            }
        }
    }

    public void cancelCatchingRequests() {
        this.mPattern = null;
        if (this.mEventTimeLine != null) {
            this.mEventTimeLine.cancelPaginationRequest();
        }
        this.mIsInitialSyncing = false;
        this.mIsBackPaginating = false;
        this.mIsFwdPaginating = false;
        this.mLockBackPagination = false;
        this.mLockFwdPagination = false;
        hideInitLoading();
        hideLoadingBackProgress();
        hideLoadingForwardProgress();
    }

    public void scrollToRow(MessageRow messageRow, boolean z) {
        int i = (int) (getResources().getDisplayMetrics().density * 100.0f);
        int position = this.mAdapter.getPosition(messageRow);
        if (z && position < this.mMessageListView.getCount() - 1) {
            position++;
        }
        this.mMessageListView.setSelectionFromTop(position, i);
    }

    public void onEvent(final Event event, Direction direction, RoomState roomState) {
        if (event == null) {
            Log.m211e(LOG_TAG, "## onEvent() : null event");
            return;
        }
        if (TextUtils.equals(event.eventId, this.mEventId)) {
            this.mEventOriginServerTs = event.getOriginServerTs();
        }
        if (direction == Direction.FORWARDS) {
            boolean z = false;
            if (Event.EVENT_TYPE_REDACTION.equals(event.getType())) {
                MessageRow messageRow = this.mAdapter.getMessageRow(event.getRedacts());
                if (messageRow != null) {
                    Event event2 = this.mSession.getDataHandler().getStore().getEvent(event.getRedacts(), event.roomId);
                    if (event2 == null) {
                        this.mAdapter.removeEventById(event.getRedacts());
                    } else {
                        messageRow.updateEvent(event2);
                        JsonObject contentAsJsonObject = messageRow.getEvent().getContentAsJsonObject();
                        if (contentAsJsonObject == null || contentAsJsonObject.entrySet() == null || contentAsJsonObject.entrySet().size() == 0) {
                            z = true;
                        }
                        if (!z && getActivity() != null) {
                            z = TextUtils.isEmpty(new EventDisplay(getActivity(), event2, roomState).getTextualDisplay());
                        }
                        if (z) {
                            this.mAdapter.removeEventById(event2.eventId);
                        }
                    }
                    this.mAdapter.notifyDataSetChanged();
                }
            } else if (canAddEvent(event)) {
                MessageRow messageRow2 = new MessageRow(event, roomState);
                AbstractMessagesAdapter abstractMessagesAdapter = this.mAdapter;
                if (this.mEventTimeLine == null || this.mEventTimeLine.isLiveTimeline()) {
                    z = true;
                }
                abstractMessagesAdapter.add(messageRow2, z);
                if (isResumed() && this.mEventTimeLine != null && this.mEventTimeLine.isLiveTimeline() && canUpdateReadMarker(messageRow2, getReadMarkerMessageRow(messageRow2))) {
                    if (this.mMessageListView.getChildCount() == 0) {
                        this.mMessageListView.post(new Runnable() {
                            public void run() {
                                View childAt = MatrixMessageListFragment.this.mMessageListView.getChildAt(MatrixMessageListFragment.this.mMessageListView.getChildCount() - 2);
                                if (childAt != null && childAt.getTop() >= 0) {
                                    MatrixMessageListFragment.this.mRoom.setReadMakerEventId(event.eventId);
                                    MatrixMessageListFragment.this.mAdapter.resetReadMarker();
                                }
                            }
                        });
                    } else {
                        View childAt = this.mMessageListView.getChildAt(this.mMessageListView.getChildCount() - 1);
                        if (childAt != null && childAt.getTop() >= 0) {
                            this.mRoom.setReadMakerEventId(event.eventId);
                            this.mAdapter.resetReadMarker();
                        }
                    }
                }
            }
        } else if (canAddEvent(event)) {
            this.mAdapter.addToFront(new MessageRow(event, roomState));
        }
    }

    public void onEventSent(Event event, String str) {
        if (this.mAdapter.getMessageRow(event.eventId) != null || !canAddEvent(event)) {
            MessageRow messageRow = this.mAdapter.getMessageRow(str);
            if (messageRow != null) {
                this.mAdapter.remove(messageRow);
                return;
            }
            return;
        }
        if (this.mAdapter.getMessageRow(str) != null) {
            this.mAdapter.updateEventById(event, str);
        } else {
            this.mAdapter.add(new MessageRow(event, this.mRoom.getState()), true);
        }
        if (this.mFutureReadMarkerEventId != null && str.equals(this.mFutureReadMarkerEventId)) {
            this.mFutureReadMarkerEventId = null;
            this.mRoom.setReadMakerEventId(event.eventId);
            RoomSummary summary = this.mRoom.getDataHandler().getStore().getSummary(this.mRoom.getRoomId());
            if (summary != null) {
                this.mAdapter.updateReadMarker(event.eventId, summary.getReadReceiptEventId());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:42:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceiptEvent(java.util.List<java.lang.String> r9) {
        /*
            r8 = this;
            r0 = 1
            com.opengarden.firechat.matrixsdk.MXSession r1 = r8.mSession     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r1.getDataHandler()     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r1 = r1.getStore()     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.view.AutoScrollDownListView r2 = r8.mMessageListView     // Catch:{ Exception -> 0x008a }
            int r2 = r2.getFirstVisiblePosition()     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.view.AutoScrollDownListView r3 = r8.mMessageListView     // Catch:{ Exception -> 0x008a }
            int r3 = r3.getLastVisiblePosition()     // Catch:{ Exception -> 0x008a }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x008a }
            r4.<init>()     // Catch:{ Exception -> 0x008a }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x008a }
            r5.<init>()     // Catch:{ Exception -> 0x008a }
        L_0x0021:
            if (r2 > r3) goto L_0x0048
            com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter r6 = r8.mAdapter     // Catch:{ Exception -> 0x008a }
            java.lang.Object r6 = r6.getItem(r2)     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r6 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r6     // Catch:{ Exception -> 0x008a }
            com.opengarden.firechat.matrixsdk.rest.model.Event r6 = r6.getEvent()     // Catch:{ Exception -> 0x008a }
            java.lang.String r7 = r6.getSender()     // Catch:{ Exception -> 0x008a }
            if (r7 == 0) goto L_0x0045
            java.lang.String r7 = r6.eventId     // Catch:{ Exception -> 0x008a }
            if (r7 == 0) goto L_0x0045
            java.lang.String r7 = r6.getSender()     // Catch:{ Exception -> 0x008a }
            r4.add(r7)     // Catch:{ Exception -> 0x008a }
            java.lang.String r6 = r6.eventId     // Catch:{ Exception -> 0x008a }
            r5.add(r6)     // Catch:{ Exception -> 0x008a }
        L_0x0045:
            int r2 = r2 + 1
            goto L_0x0021
        L_0x0048:
            r2 = 0
            java.util.Iterator r9 = r9.iterator()     // Catch:{ Exception -> 0x0088 }
        L_0x004d:
            boolean r3 = r9.hasNext()     // Catch:{ Exception -> 0x0088 }
            if (r3 == 0) goto L_0x00a6
            java.lang.Object r3 = r9.next()     // Catch:{ Exception -> 0x0088 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0088 }
            com.opengarden.firechat.matrixsdk.MXSession r6 = r8.mSession     // Catch:{ Exception -> 0x0088 }
            java.lang.String r6 = r6.getMyUserId()     // Catch:{ Exception -> 0x0088 }
            boolean r6 = android.text.TextUtils.equals(r3, r6)     // Catch:{ Exception -> 0x0088 }
            if (r6 != 0) goto L_0x004d
            com.opengarden.firechat.matrixsdk.data.Room r6 = r8.mRoom     // Catch:{ Exception -> 0x0088 }
            java.lang.String r6 = r6.getRoomId()     // Catch:{ Exception -> 0x0088 }
            com.opengarden.firechat.matrixsdk.rest.model.ReceiptData r6 = r1.getReceipt(r6, r3)     // Catch:{ Exception -> 0x0088 }
            if (r6 == 0) goto L_0x004d
            java.lang.String r6 = r6.eventId     // Catch:{ Exception -> 0x0088 }
            int r6 = r5.indexOf(r6)     // Catch:{ Exception -> 0x0088 }
            if (r6 < 0) goto L_0x004d
            java.lang.Object r6 = r4.get(r6)     // Catch:{ Exception -> 0x0088 }
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6     // Catch:{ Exception -> 0x0088 }
            boolean r3 = android.text.TextUtils.equals(r6, r3)     // Catch:{ Exception -> 0x0088 }
            r2 = r3 ^ 1
            if (r2 == 0) goto L_0x004d
            goto L_0x00a6
        L_0x0088:
            r9 = move-exception
            goto L_0x008c
        L_0x008a:
            r9 = move-exception
            r2 = 1
        L_0x008c:
            java.lang.String r0 = "MatrixMsgsListFrag"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "onReceiptEvent failed with "
            r1.append(r3)
            java.lang.String r9 = r9.getMessage()
            r1.append(r9)
            java.lang.String r9 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r9)
        L_0x00a6:
            if (r2 == 0) goto L_0x00ad
            com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter r9 = r8.mAdapter
            r9.notifyDataSetChanged()
        L_0x00ad:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.onReceiptEvent(java.util.List):void");
    }

    public void onInitialMessagesLoaded() {
        Log.m209d(LOG_TAG, "onInitialMessagesLoaded");
        getUiHandler().post(new Runnable() {
            public void run() {
                if (MatrixMessageListFragment.this.mMessageListView != null) {
                    MatrixMessageListFragment.this.hideLoadingBackProgress();
                    if (MatrixMessageListFragment.this.mMessageListView.getAdapter() == null) {
                        MatrixMessageListFragment.this.mMessageListView.setAdapter(MatrixMessageListFragment.this.mAdapter);
                    }
                    if (MatrixMessageListFragment.this.mEventTimeLine == null || MatrixMessageListFragment.this.mEventTimeLine.isLiveTimeline()) {
                        if (MatrixMessageListFragment.this.mAdapter.getCount() > 0) {
                            MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                            if (MatrixMessageListFragment.this.mScrollToIndex >= 0) {
                                MatrixMessageListFragment.this.mMessageListView.setSelection(MatrixMessageListFragment.this.mScrollToIndex);
                                MatrixMessageListFragment.this.mScrollToIndex = -1;
                            } else {
                                MatrixMessageListFragment.this.mMessageListView.setSelection(MatrixMessageListFragment.this.mAdapter.getCount() - 1);
                            }
                        }
                        MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                            public void run() {
                                if (MatrixMessageListFragment.this.mMessageListView.getVisibility() != 0 || MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition() >= 10) {
                                    Log.m209d(MatrixMessageListFragment.LOG_TAG, "onInitialMessagesLoaded : history should be filled");
                                    MatrixMessageListFragment.this.mIsInitialSyncing = false;
                                    MatrixMessageListFragment.this.setMessageListViewScrollListener();
                                    return;
                                }
                                Log.m209d(MatrixMessageListFragment.LOG_TAG, "onInitialMessagesLoaded : fill history");
                                MatrixMessageListFragment.this.backPaginate(true);
                            }
                        });
                    } else {
                        Log.m209d(MatrixMessageListFragment.LOG_TAG, "onInitialMessagesLoaded : default behaviour");
                        if (MatrixMessageListFragment.this.mAdapter.getCount() == 0 || MatrixMessageListFragment.this.mScrollToIndex <= 0) {
                            MatrixMessageListFragment.this.mIsInitialSyncing = false;
                            MatrixMessageListFragment.this.setMessageListViewScrollListener();
                        } else {
                            MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                            MatrixMessageListFragment.this.mMessageListView.setSelection(MatrixMessageListFragment.this.mScrollToIndex);
                            MatrixMessageListFragment.this.mScrollToIndex = -1;
                            MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                public void run() {
                                    MatrixMessageListFragment.this.mIsInitialSyncing = false;
                                    MatrixMessageListFragment.this.setMessageListViewScrollListener();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public EventTimeline getEventTimeLine() {
        return this.mEventTimeLine;
    }

    public void onTimelineInitialized() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            this.mMessageListView.post(new Runnable() {
                public void run() {
                    if (MatrixMessageListFragment.this.getActivity() == null) {
                        Log.m211e(MatrixMessageListFragment.LOG_TAG, "## onTimelineInitialized : the fragment is not anymore attached to an activity");
                        return;
                    }
                    int i = 0;
                    MatrixMessageListFragment.this.mLockFwdPagination = false;
                    MatrixMessageListFragment.this.mIsInitialSyncing = false;
                    if (!MatrixMessageListFragment.this.mAdapter.isUnreadViewMode() || MatrixMessageListFragment.this.mAdapter.getMessageRow(MatrixMessageListFragment.this.mEventId) != null) {
                        while (i < MatrixMessageListFragment.this.mAdapter.getCount() && !TextUtils.equals(((MessageRow) MatrixMessageListFragment.this.mAdapter.getItem(i)).getEvent().eventId, MatrixMessageListFragment.this.mEventId)) {
                            i++;
                        }
                        MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                        MatrixMessageListFragment.this.mMessageListView.setAdapter(MatrixMessageListFragment.this.mAdapter);
                        if (MatrixMessageListFragment.this.mAdapter.isUnreadViewMode()) {
                            MatrixMessageListFragment.this.scrollToRow(MatrixMessageListFragment.this.mAdapter.getMessageRow(MatrixMessageListFragment.this.mEventId), true);
                        } else {
                            MatrixMessageListFragment.this.mMessageListView.setSelectionFromTop(i, ((View) MatrixMessageListFragment.this.mMessageListView.getParent()).getHeight() / 2);
                        }
                    } else {
                        MessageRow closestRowFromTs = MatrixMessageListFragment.this.mAdapter.getClosestRowFromTs(MatrixMessageListFragment.this.mEventId, MatrixMessageListFragment.this.mEventOriginServerTs);
                        int position = MatrixMessageListFragment.this.mAdapter.getPosition(closestRowFromTs);
                        if (position > 0) {
                            closestRowFromTs = (MessageRow) MatrixMessageListFragment.this.mAdapter.getItem(position - 1);
                        }
                        if (closestRowFromTs != null) {
                            MatrixMessageListFragment.this.mAdapter.updateReadMarker(closestRowFromTs.getEvent().eventId, null);
                        }
                        MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                        MatrixMessageListFragment.this.mMessageListView.setAdapter(MatrixMessageListFragment.this.mAdapter);
                        if (closestRowFromTs != null) {
                            MatrixMessageListFragment.this.scrollToRow(closestRowFromTs, true);
                        }
                    }
                }
            });
        }
    }

    public RoomPreviewData getRoomPreviewData() {
        if (getActivity() != null) {
            if (this.mRoomPreviewDataListener == null) {
                try {
                    this.mRoomPreviewDataListener = (IRoomPreviewDataListener) getActivity();
                } catch (ClassCastException e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("getRoomPreviewData failed with ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
            if (this.mRoomPreviewDataListener != null) {
                return this.mRoomPreviewDataListener.getRoomPreviewData();
            }
        }
        return null;
    }

    public void onRoomFlush() {
        this.mAdapter.clear();
    }

    /* access modifiers changed from: protected */
    public void cancelSearch() {
        this.mPattern = null;
    }

    public void requestSearchHistory() {
        if (TextUtils.isEmpty(this.mNextBatch)) {
            this.mIsBackPaginating = false;
            return;
        }
        this.mIsBackPaginating = true;
        final int firstVisiblePosition = this.mMessageListView.getFirstVisiblePosition();
        final String str = this.mPattern;
        final int count = this.mAdapter.getCount();
        showLoadingBackProgress();
        List list = null;
        if (this.mRoom != null) {
            list = Arrays.asList(new String[]{this.mRoom.getRoomId()});
        }
        C266719 r0 = new ApiCallback<SearchResponse>() {
            public void onSuccess(SearchResponse searchResponse) {
                if (TextUtils.equals(MatrixMessageListFragment.this.mPattern, str)) {
                    List<SearchResult> list = searchResponse.searchCategories.roomEvents.results;
                    if (list.size() != 0) {
                        MatrixMessageListFragment.this.mAdapter.setNotifyOnChange(false);
                        for (SearchResult searchResult : list) {
                            MatrixMessageListFragment.this.mAdapter.insert(new MessageRow(searchResult.result, MatrixMessageListFragment.this.mRoom == null ? null : MatrixMessageListFragment.this.mRoom.getState()), 0);
                        }
                        MatrixMessageListFragment.this.mNextBatch = searchResponse.searchCategories.roomEvents.nextBatch;
                        MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                            public void run() {
                                int count = firstVisiblePosition + (MatrixMessageListFragment.this.mAdapter.getCount() - count);
                                MatrixMessageListFragment.this.mMessageListView.lockSelectionOnResize();
                                MatrixMessageListFragment.this.mAdapter.notifyDataSetChanged();
                                MatrixMessageListFragment.this.mMessageListView.setSelection(count);
                                MatrixMessageListFragment.this.mMessageListView.post(new Runnable() {
                                    public void run() {
                                        MatrixMessageListFragment.this.mIsBackPaginating = false;
                                        if (MatrixMessageListFragment.this.mMessageListView.getFirstVisiblePosition() <= 2) {
                                            MatrixMessageListFragment.this.requestSearchHistory();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        MatrixMessageListFragment.this.mIsBackPaginating = false;
                    }
                    MatrixMessageListFragment.this.hideLoadingBackProgress();
                }
            }

            private void onError() {
                MatrixMessageListFragment.this.mIsBackPaginating = false;
                MatrixMessageListFragment.this.hideLoadingBackProgress();
            }

            public void onNetworkError(Exception exc) {
                String str = MatrixMessageListFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Network error: ");
                sb.append(exc.getMessage());
                Log.m211e(str, sb.toString());
                onError();
            }

            public void onMatrixError(MatrixError matrixError) {
                String str = MatrixMessageListFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Matrix error : ");
                sb.append(matrixError.errcode);
                sb.append(" - ");
                sb.append(matrixError.getMessage());
                Log.m211e(str, sb.toString());
                onError();
            }

            public void onUnexpectedError(Exception exc) {
                String str = MatrixMessageListFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onUnexpectedError error");
                sb.append(exc.getMessage());
                Log.m211e(str, sb.toString());
                onError();
            }
        };
        if (this.mIsMediaSearch) {
            this.mSession.searchMediasByName(this.mPattern, list, this.mNextBatch, r0);
        } else {
            this.mSession.searchMessagesByText(this.mPattern, list, this.mNextBatch, r0);
        }
    }

    /* access modifiers changed from: protected */
    public void onSearchResponse(SearchResponse searchResponse, OnSearchResultListener onSearchResultListener) {
        List<SearchResult> list = searchResponse.searchCategories.roomEvents.results;
        ArrayList arrayList = new ArrayList(list.size());
        for (SearchResult searchResult : list) {
            RoomState roomState = null;
            if (this.mRoom != null) {
                roomState = this.mRoom.getState();
            }
            if (roomState == null) {
                Room room = this.mSession.getDataHandler().getStore().getRoom(searchResult.result.roomId);
                if (room != null) {
                    roomState = room.getState();
                }
            }
            boolean z = false;
            if (!(searchResult.result == null || searchResult.result.getContent() == null)) {
                JsonObject contentAsJsonObject = searchResult.result.getContentAsJsonObject();
                if (!(contentAsJsonObject == null || contentAsJsonObject.entrySet().size() == 0)) {
                    z = true;
                }
            }
            if (z) {
                arrayList.add(new MessageRow(searchResult.result, roomState));
            }
        }
        Collections.reverse(arrayList);
        this.mAdapter.clear();
        this.mAdapter.addAll(arrayList);
        this.mNextBatch = searchResponse.searchCategories.roomEvents.nextBatch;
        if (onSearchResultListener != null) {
            try {
                onSearchResultListener.onSearchSucceed(arrayList.size());
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onSearchResponse failed with ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void searchPattern(String str, OnSearchResultListener onSearchResultListener) {
        searchPattern(str, false, onSearchResultListener);
    }

    public void searchPattern(final String str, boolean z, final OnSearchResultListener onSearchResultListener) {
        List list;
        if (!TextUtils.equals(this.mPattern, str)) {
            this.mPattern = str;
            this.mIsMediaSearch = z;
            this.mAdapter.setSearchPattern(this.mPattern);
            if (!TextUtils.isEmpty(this.mPattern)) {
                if (this.mRoom != null) {
                    list = Arrays.asList(new String[]{this.mRoom.getRoomId()});
                } else {
                    list = null;
                }
                C267120 r2 = new ApiCallback<SearchResponse>() {
                    public void onSuccess(final SearchResponse searchResponse) {
                        MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                            public void run() {
                                if (TextUtils.equals(MatrixMessageListFragment.this.mPattern, str)) {
                                    MatrixMessageListFragment.this.onSearchResponse(searchResponse, onSearchResultListener);
                                }
                            }
                        });
                    }

                    private void onError() {
                        MatrixMessageListFragment.this.getUiHandler().post(new Runnable() {
                            public void run() {
                                if (onSearchResultListener != null) {
                                    try {
                                        onSearchResultListener.onSearchFailed();
                                    } catch (Exception e) {
                                        String str = MatrixMessageListFragment.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("onSearchResultListener failed with ");
                                        sb.append(e.getMessage());
                                        Log.m211e(str, sb.toString());
                                    }
                                }
                            }
                        });
                    }

                    public void onNetworkError(Exception exc) {
                        String str = MatrixMessageListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Network error: ");
                        sb.append(exc.getMessage());
                        Log.m211e(str, sb.toString());
                        onError();
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        String str = MatrixMessageListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Matrix error : ");
                        sb.append(matrixError.errcode);
                        sb.append(" - ");
                        sb.append(matrixError.getMessage());
                        Log.m211e(str, sb.toString());
                        onError();
                    }

                    public void onUnexpectedError(Exception exc) {
                        String str = MatrixMessageListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onUnexpectedError error");
                        sb.append(exc.getMessage());
                        Log.m211e(str, sb.toString());
                        onError();
                    }
                };
                if (z) {
                    this.mSession.searchMediasByName(this.mPattern, list, null, r2);
                } else {
                    this.mSession.searchMessagesByText(this.mPattern, list, null, r2);
                }
            }
        }
    }
}
