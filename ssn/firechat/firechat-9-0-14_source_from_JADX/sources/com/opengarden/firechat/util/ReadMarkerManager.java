package com.opengarden.firechat.util;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.VectorMessagesAdapter;
import com.opengarden.firechat.adapters.VectorMessagesAdapter.ReadMarkerListener;
import com.opengarden.firechat.fragments.VectorMessageListFragment;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ReadMarkerManager implements ReadMarkerListener {
    public static final int LIVE_MODE = 0;
    private static final String LOG_TAG = "ReadMarkerManager";
    public static final int PREVIEW_MODE = 1;
    private static final int UNREAD_BACK_PAGINATE_EVENT_COUNT = 100;
    /* access modifiers changed from: private */
    public VectorRoomActivity mActivity;
    private View mCloseJumpToUnreadView;
    private Event mFirstVisibleEvent;
    private boolean mHasJumpedToBottom;
    /* access modifiers changed from: private */
    public boolean mHasJumpedToFirstUnread;
    private View mJumpToUnreadView;
    private View mJumpToUnreadViewSpinner;
    private Event mLastVisibleEvent;
    /* access modifiers changed from: private */
    public String mReadMarkerEventId;
    /* access modifiers changed from: private */
    public Room mRoom;
    private RoomSummary mRoomSummary;
    private int mScrollState = -1;
    private MXSession mSession;
    private int mUpdateMode = -1;
    /* access modifiers changed from: private */
    public VectorMessageListFragment mVectorMessageListFragment;

    @Retention(RetentionPolicy.SOURCE)
    @interface UpdateMode {
    }

    public ReadMarkerManager(final VectorRoomActivity vectorRoomActivity, VectorMessageListFragment vectorMessageListFragment, MXSession mXSession, Room room, int i, View view) {
        if (room != null) {
            this.mActivity = vectorRoomActivity;
            this.mVectorMessageListFragment = vectorMessageListFragment;
            this.mSession = mXSession;
            this.mRoom = room;
            this.mRoomSummary = this.mRoom.getDataHandler().getStore().getSummary(this.mRoom.getRoomId());
            this.mReadMarkerEventId = this.mRoomSummary.getReadMarkerEventId();
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Create ReadMarkerManager instance id:");
            sb.append(this.mReadMarkerEventId);
            sb.append(" for room:");
            sb.append(this.mRoom.getRoomId());
            Log.m209d(str, sb.toString());
            this.mUpdateMode = i;
            if (view != null) {
                this.mJumpToUnreadView = view;
                TextView textView = (TextView) view.findViewById(C1299R.C1301id.jump_to_first_unread_label);
                textView.setPaintFlags(textView.getPaintFlags() | 8);
                this.mCloseJumpToUnreadView = view.findViewById(C1299R.C1301id.close_jump_to_first_unread);
                this.mJumpToUnreadViewSpinner = view.findViewById(C1299R.C1301id.jump_to_read_spinner);
                if (isLiveMode()) {
                    textView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            vectorRoomActivity.dismissKeyboard();
                            ReadMarkerManager.this.updateReadMarkerValue();
                            if (!TextUtils.isEmpty(ReadMarkerManager.this.mReadMarkerEventId)) {
                                Event event = ReadMarkerManager.this.mRoom.getDataHandler().getStore().getEvent(ReadMarkerManager.this.mReadMarkerEventId, ReadMarkerManager.this.mRoom.getRoomId());
                                if (event == null) {
                                    ReadMarkerManager.this.openPreviewToGivenEvent(ReadMarkerManager.this.mReadMarkerEventId);
                                } else {
                                    ReadMarkerManager.this.scrollUpToGivenEvent(event);
                                }
                            }
                        }
                    });
                    this.mCloseJumpToUnreadView.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            ReadMarkerManager.this.forgetReadMarker();
                        }
                    });
                }
            }
        }
    }

    public void onResume() {
        ((VectorMessagesAdapter) this.mVectorMessageListFragment.getMessageAdapter()).setReadMarkerListener(this);
        updateJumpToBanner();
    }

    public void onPause() {
        if (!isLiveMode() || this.mHasJumpedToFirstUnread) {
            setReadMarkerToLastVisibleRow();
        }
    }

    public void onScroll(int i, int i2, int i3, Event event, Event event2) {
        this.mFirstVisibleEvent = event;
        this.mLastVisibleEvent = event2;
        if (isLiveMode()) {
            updateJumpToBanner();
        } else if (this.mVectorMessageListFragment.getEventTimeLine().hasReachedHomeServerForwardsPaginationEnd()) {
            ListView messageListView = this.mVectorMessageListFragment.getMessageListView();
            if (messageListView != null && i + i2 == i3 && messageListView.getChildAt(messageListView.getChildCount() - 1).getBottom() == messageListView.getBottom()) {
                this.mActivity.setResult(-1);
                this.mActivity.finish();
            }
        }
    }

    public void onScrollStateChanged(int i) {
        if (i == 0 && (this.mScrollState == 2 || this.mScrollState == 1)) {
            checkUnreadMessage();
        }
        this.mScrollState = i;
    }

    public void onReadMarkerChanged(String str) {
        if (TextUtils.equals(this.mRoom.getRoomId(), str)) {
            String readMarkerEventId = this.mRoomSummary.getReadMarkerEventId();
            if (!TextUtils.equals(readMarkerEventId, this.mReadMarkerEventId)) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onReadMarkerChanged");
                sb.append(readMarkerEventId);
                Log.m209d(str2, sb.toString());
                refresh();
            }
        }
    }

    public void handleJumpToBottom() {
        this.mHasJumpedToBottom = true;
        if (isLiveMode() && this.mHasJumpedToFirstUnread) {
            setReadMarkerToLastVisibleRow();
            this.mHasJumpedToFirstUnread = false;
        }
        this.mVectorMessageListFragment.getMessageAdapter().updateReadMarker(this.mReadMarkerEventId, this.mRoomSummary.getReadReceiptEventId());
        this.mVectorMessageListFragment.scrollToBottom(0);
    }

    private void checkUnreadMessage() {
        Log.m209d(LOG_TAG, "checkUnreadMessage");
        if (this.mJumpToUnreadView.getVisibility() != 0) {
            String readReceiptEventId = this.mRoomSummary.getReadReceiptEventId();
            if (this.mReadMarkerEventId != null && !this.mReadMarkerEventId.equals(readReceiptEventId)) {
                if (isLiveMode() && !this.mHasJumpedToFirstUnread) {
                    MessageRow messageRow = this.mVectorMessageListFragment.getMessageAdapter().getMessageRow(this.mReadMarkerEventId);
                    if (messageRow != null && messageRow.getEvent() != null && this.mFirstVisibleEvent != null && messageRow.getEvent().getOriginServerTs() >= this.mFirstVisibleEvent.getOriginServerTs()) {
                        Log.m209d(LOG_TAG, "checkUnreadMessage: first unread has been reached by scrolling up");
                        forgetReadMarker();
                    }
                } else if (this.mLastVisibleEvent == null) {
                } else {
                    if (this.mLastVisibleEvent.eventId.equals(this.mRoomSummary.getLatestReceivedEvent().eventId)) {
                        Log.m209d(LOG_TAG, "checkUnreadMessage: last received event has been reached by scrolling down");
                        markAllAsRead();
                    } else if (!isLiveMode()) {
                        Log.m209d(LOG_TAG, "checkUnreadMessage: preview mode, set read marker to last visible row");
                        setReadMarkerToLastVisibleRow();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateReadMarkerValue() {
        this.mReadMarkerEventId = this.mRoomSummary.getReadMarkerEventId();
        this.mVectorMessageListFragment.getMessageAdapter().updateReadMarker(this.mReadMarkerEventId, this.mRoomSummary.getReadReceiptEventId());
    }

    private void refresh() {
        Log.m209d(LOG_TAG, "refresh");
        updateReadMarkerValue();
        updateJumpToBanner();
        checkUnreadMessage();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void updateJumpToBanner() {
        /*
            r9 = this;
            monitor-enter(r9)
            com.opengarden.firechat.matrixsdk.data.RoomSummary r0 = r9.mRoomSummary     // Catch:{ all -> 0x0111 }
            java.lang.String r0 = r0.getReadMarkerEventId()     // Catch:{ all -> 0x0111 }
            r9.mReadMarkerEventId = r0     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.data.RoomSummary r0 = r9.mRoomSummary     // Catch:{ all -> 0x0111 }
            r1 = 0
            if (r0 == 0) goto L_0x00e6
            java.lang.String r0 = r9.mReadMarkerEventId     // Catch:{ all -> 0x0111 }
            if (r0 == 0) goto L_0x00e6
            boolean r0 = r9.mHasJumpedToFirstUnread     // Catch:{ all -> 0x0111 }
            if (r0 != 0) goto L_0x00e6
            com.opengarden.firechat.matrixsdk.data.RoomSummary r0 = r9.mRoomSummary     // Catch:{ all -> 0x0111 }
            java.lang.String r0 = r0.getReadReceiptEventId()     // Catch:{ all -> 0x0111 }
            java.lang.String r2 = r9.mReadMarkerEventId     // Catch:{ all -> 0x0111 }
            boolean r2 = r2.equals(r0)     // Catch:{ all -> 0x0111 }
            r3 = 1
            if (r2 != 0) goto L_0x00d1
            java.lang.String r2 = r9.mReadMarkerEventId     // Catch:{ all -> 0x0111 }
            boolean r2 = com.opengarden.firechat.matrixsdk.MXSession.isMessageId(r2)     // Catch:{ all -> 0x0111 }
            if (r2 != 0) goto L_0x0036
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0111 }
            java.lang.String r3 = "updateJumpToBanner: Read marker event id is invalid, ignore it as it should not occur"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r3)     // Catch:{ all -> 0x0111 }
            goto L_0x00d1
        L_0x0036:
            java.lang.String r2 = r9.mReadMarkerEventId     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r9.getEvent(r2)     // Catch:{ all -> 0x0111 }
            if (r2 != 0) goto L_0x0040
            goto L_0x00d2
        L_0x0040:
            com.opengarden.firechat.matrixsdk.data.Room r4 = r9.mRoom     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r4 = r4.getDataHandler()     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r4 = r4.getStore()     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.data.Room r5 = r9.mRoom     // Catch:{ all -> 0x0111 }
            java.lang.String r5 = r5.getRoomId()     // Catch:{ all -> 0x0111 }
            java.util.Collection r4 = r4.getRoomMessages(r5)     // Catch:{ all -> 0x0111 }
            if (r4 != 0) goto L_0x006f
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x0111 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0111 }
            r4.<init>()     // Catch:{ all -> 0x0111 }
            java.lang.String r5 = "updateJumpToBanner getRoomMessages returned null instead of collection with event "
            r4.append(r5)     // Catch:{ all -> 0x0111 }
            java.lang.String r2 = r2.eventId     // Catch:{ all -> 0x0111 }
            r4.append(r2)     // Catch:{ all -> 0x0111 }
            java.lang.String r2 = r4.toString()     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r2)     // Catch:{ all -> 0x0111 }
            goto L_0x00d1
        L_0x006f:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ all -> 0x0111 }
            r5.<init>(r4)     // Catch:{ all -> 0x0111 }
            int r2 = r5.indexOf(r2)     // Catch:{ all -> 0x0111 }
            r4 = -1
            if (r2 == r4) goto L_0x007d
            int r2 = r2 + r3
            goto L_0x007e
        L_0x007d:
            r2 = -1
        L_0x007e:
            if (r2 == r4) goto L_0x00d1
            int r4 = r5.size()     // Catch:{ all -> 0x0111 }
            if (r2 >= r4) goto L_0x00d1
            java.lang.Object r2 = r5.get(r2)     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = (com.opengarden.firechat.matrixsdk.rest.model.Event) r2     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r4 = r9.mFirstVisibleEvent     // Catch:{ all -> 0x0111 }
            if (r4 == 0) goto L_0x00d1
            if (r2 == 0) goto L_0x00d1
            long r4 = r2.getOriginServerTs()     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r6 = r9.mFirstVisibleEvent     // Catch:{ all -> 0x0111 }
            long r6 = r6.getOriginServerTs()     // Catch:{ all -> 0x0111 }
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 <= 0) goto L_0x00a1
            goto L_0x00d1
        L_0x00a1:
            long r4 = r2.getOriginServerTs()     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r9.mFirstVisibleEvent     // Catch:{ all -> 0x0111 }
            long r6 = r2.getOriginServerTs()     // Catch:{ all -> 0x0111 }
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x00d2
            com.opengarden.firechat.fragments.VectorMessageListFragment r2 = r9.mVectorMessageListFragment     // Catch:{ all -> 0x0111 }
            android.widget.ListView r2 = r2.getMessageListView()     // Catch:{ all -> 0x0111 }
            if (r2 == 0) goto L_0x00bc
            android.view.View r2 = r2.getChildAt(r1)     // Catch:{ all -> 0x0111 }
            goto L_0x00bd
        L_0x00bc:
            r2 = 0
        L_0x00bd:
            if (r2 == 0) goto L_0x00c6
            int r2 = r2.getTop()     // Catch:{ all -> 0x0111 }
            if (r2 >= 0) goto L_0x00c6
            goto L_0x00c7
        L_0x00c6:
            r3 = 0
        L_0x00c7:
            boolean r2 = r9.mHasJumpedToFirstUnread     // Catch:{ all -> 0x0111 }
            if (r2 == 0) goto L_0x00d2
            if (r3 != 0) goto L_0x00d2
            r9.forgetReadMarker()     // Catch:{ all -> 0x0111 }
            goto L_0x00d2
        L_0x00d1:
            r3 = 0
        L_0x00d2:
            com.opengarden.firechat.fragments.VectorMessageListFragment r2 = r9.mVectorMessageListFragment     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter r2 = r2.getMessageAdapter()     // Catch:{ all -> 0x0111 }
            if (r2 == 0) goto L_0x00e7
            com.opengarden.firechat.fragments.VectorMessageListFragment r2 = r9.mVectorMessageListFragment     // Catch:{ all -> 0x0111 }
            com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter r2 = r2.getMessageAdapter()     // Catch:{ all -> 0x0111 }
            java.lang.String r4 = r9.mReadMarkerEventId     // Catch:{ all -> 0x0111 }
            r2.updateReadMarker(r4, r0)     // Catch:{ all -> 0x0111 }
            goto L_0x00e7
        L_0x00e6:
            r3 = 0
        L_0x00e7:
            boolean r0 = r9.isLiveMode()     // Catch:{ all -> 0x0111 }
            r2 = 8
            if (r0 == 0) goto L_0x0101
            if (r3 == 0) goto L_0x0101
            android.view.View r0 = r9.mJumpToUnreadViewSpinner     // Catch:{ all -> 0x0111 }
            r0.setVisibility(r2)     // Catch:{ all -> 0x0111 }
            android.view.View r0 = r9.mCloseJumpToUnreadView     // Catch:{ all -> 0x0111 }
            r0.setVisibility(r1)     // Catch:{ all -> 0x0111 }
            android.view.View r0 = r9.mJumpToUnreadView     // Catch:{ all -> 0x0111 }
            r0.setVisibility(r1)     // Catch:{ all -> 0x0111 }
            goto L_0x0106
        L_0x0101:
            android.view.View r0 = r9.mJumpToUnreadView     // Catch:{ all -> 0x0111 }
            r0.setVisibility(r2)     // Catch:{ all -> 0x0111 }
        L_0x0106:
            boolean r0 = r9.mHasJumpedToBottom     // Catch:{ all -> 0x0111 }
            if (r0 == 0) goto L_0x010f
            r9.mHasJumpedToBottom = r1     // Catch:{ all -> 0x0111 }
            r9.checkUnreadMessage()     // Catch:{ all -> 0x0111 }
        L_0x010f:
            monitor-exit(r9)
            return
        L_0x0111:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.ReadMarkerManager.updateJumpToBanner():void");
    }

    private Event getEvent(String str) {
        MessageRow messageRow = this.mVectorMessageListFragment.getMessageAdapter().getMessageRow(str);
        Event event = messageRow != null ? messageRow.getEvent() : null;
        return event == null ? this.mVectorMessageListFragment.getEventTimeLine().getStore().getEvent(this.mReadMarkerEventId, this.mRoom.getRoomId()) : event;
    }

    private boolean isLiveMode() {
        return this.mUpdateMode == 0;
    }

    /* access modifiers changed from: private */
    public void openPreviewToGivenEvent(String str) {
        if (!TextUtils.isEmpty(str)) {
            Intent intent = new Intent(this.mActivity, VectorRoomActivity.class);
            intent.putExtra("EXTRA_ROOM_ID", this.mRoom.getRoomId());
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
            intent.putExtra(VectorRoomActivity.EXTRA_EVENT_ID, str);
            intent.putExtra(VectorRoomActivity.EXTRA_IS_UNREAD_PREVIEW_MODE, true);
            this.mActivity.startActivityForResult(intent, 5);
        }
    }

    /* access modifiers changed from: private */
    public void scrollUpToGivenEvent(final Event event) {
        if (event != null) {
            this.mCloseJumpToUnreadView.setVisibility(8);
            this.mJumpToUnreadViewSpinner.setVisibility(0);
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("scrollUpToGivenEvent ");
            sb.append(event.eventId);
            Log.m209d(str, sb.toString());
            if (!scrollToAdapterEvent(event)) {
                this.mRoom.getLiveTimeLine().backPaginate(100, true, new ApiCallback<Integer>() {
                    public void onSuccess(Integer num) {
                        if (!ReadMarkerManager.this.mActivity.isFinishing()) {
                            ReadMarkerManager.this.mVectorMessageListFragment.getMessageAdapter().notifyDataSetChanged();
                            if (!ReadMarkerManager.this.scrollToAdapterEvent(event)) {
                                ReadMarkerManager.this.openPreviewToGivenEvent(event.eventId);
                            }
                        }
                    }

                    public void onNetworkError(Exception exc) {
                        ReadMarkerManager.this.openPreviewToGivenEvent(event.eventId);
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        ReadMarkerManager.this.openPreviewToGivenEvent(event.eventId);
                    }

                    public void onUnexpectedError(Exception exc) {
                        ReadMarkerManager.this.openPreviewToGivenEvent(event.eventId);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean scrollToAdapterEvent(Event event) {
        Event event2 = null;
        MessageRow messageRow = this.mVectorMessageListFragment.getMessageAdapter() != null ? this.mVectorMessageListFragment.getMessageAdapter().getMessageRow(event.eventId) : null;
        if (messageRow != null) {
            scrollToRow(messageRow, true);
            return true;
        }
        Log.m209d(LOG_TAG, "scrollToAdapterEvent: need to load more events in adapter or eventId is not displayed");
        if (this.mVectorMessageListFragment.getMessageAdapter().getCount() <= 0) {
            return false;
        }
        MessageRow messageRow2 = (MessageRow) this.mVectorMessageListFragment.getMessageAdapter().getItem(0);
        Event event3 = messageRow2 != null ? messageRow2.getEvent() : null;
        MessageRow messageRow3 = (MessageRow) this.mVectorMessageListFragment.getMessageAdapter().getItem(this.mVectorMessageListFragment.getMessageAdapter().getCount() - 1);
        if (messageRow3 != null) {
            event2 = messageRow3.getEvent();
        }
        if (event3 == null || event2 == null || event.getOriginServerTs() <= event3.getOriginServerTs() || event.getOriginServerTs() >= event2.getOriginServerTs()) {
            return false;
        }
        MessageRow closestRow = this.mVectorMessageListFragment.getMessageAdapter().getClosestRow(event);
        if (closestRow == null) {
            return false;
        }
        scrollToRow(closestRow, closestRow.getEvent().eventId.equals(event.eventId));
        return true;
    }

    private void scrollToRow(final MessageRow messageRow, final boolean z) {
        this.mVectorMessageListFragment.getMessageListView().post(new Runnable() {
            public void run() {
                ReadMarkerManager.this.mVectorMessageListFragment.scrollToRow(messageRow, z);
                ReadMarkerManager.this.mHasJumpedToFirstUnread = true;
            }
        });
    }

    private void setReadMarkerToLastVisibleRow() {
        Event event;
        Log.m209d(LOG_TAG, "setReadMarkerToLastVisibleRow");
        ListView messageListView = this.mVectorMessageListFragment.getMessageListView();
        if (messageListView != null && messageListView.getChildCount() != 0 && this.mVectorMessageListFragment.getMessageAdapter() != null) {
            int lastVisiblePosition = messageListView.getLastVisiblePosition();
            if (messageListView.getChildAt(messageListView.getChildCount() - 1).getBottom() <= messageListView.getBottom()) {
                event = this.mVectorMessageListFragment.getEvent(lastVisiblePosition);
            } else {
                event = this.mVectorMessageListFragment.getEvent(lastVisiblePosition - 1);
            }
            Event event2 = getEvent(this.mReadMarkerEventId);
            if (event2 != null) {
                long originServerTs = event2.getOriginServerTs();
                MessageRow closestRow = this.mVectorMessageListFragment.getMessageAdapter().getClosestRow(event);
                if (closestRow != null) {
                    Event event3 = closestRow.getEvent();
                    long originServerTs2 = event3.getOriginServerTs();
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("setReadMarkerToLastVisibleRow currentReadMarkerEvent:");
                    sb.append(event2.eventId);
                    sb.append(" TS:");
                    sb.append(originServerTs);
                    sb.append(" closestEvent:");
                    sb.append(event3.eventId);
                    sb.append(" TS:");
                    sb.append(event3.getOriginServerTs());
                    Log.m215v(str, sb.toString());
                    if (originServerTs2 > originServerTs) {
                        String str2 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("setReadMarkerToLastVisibleRow update read marker to:");
                        sb2.append(event.eventId);
                        sb2.append(" isMessageId:");
                        sb2.append(MXSession.isMessageId(event.eventId));
                        Log.m209d(str2, sb2.toString());
                        this.mRoom.setReadMakerEventId(event.eventId);
                        onReadMarkerChanged(this.mRoom.getRoomId());
                    }
                }
            }
        }
    }

    private void markAllAsRead() {
        Log.m209d(LOG_TAG, "markAllAsRead");
        this.mRoom.markAllAsRead(null);
    }

    /* access modifiers changed from: private */
    public void forgetReadMarker() {
        Log.m209d(LOG_TAG, "forgetReadMarker");
        this.mRoom.forgetReadMarker(new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                ReadMarkerManager.this.updateJumpToBanner();
            }

            public void onNetworkError(Exception exc) {
                ReadMarkerManager.this.updateJumpToBanner();
            }

            public void onMatrixError(MatrixError matrixError) {
                ReadMarkerManager.this.updateJumpToBanner();
            }

            public void onUnexpectedError(Exception exc) {
                ReadMarkerManager.this.updateJumpToBanner();
            }
        });
    }

    public void onReadMarkerDisplayed(Event event, View view) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onReadMarkerDisplayed for ");
        sb.append(event.eventId);
        Log.m209d(str, sb.toString());
        if (!this.mActivity.isFinishing()) {
            if (this.mLastVisibleEvent == null) {
                try {
                    this.mLastVisibleEvent = this.mVectorMessageListFragment.getEvent(this.mVectorMessageListFragment.getMessageListView().getLastVisiblePosition());
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## onReadMarkerDisplayed() : crash while retrieving mLastVisibleEvent ");
                    sb2.append(e.getMessage());
                    Log.m211e(str2, sb2.toString());
                }
            }
            if (this.mFirstVisibleEvent == null) {
                try {
                    this.mFirstVisibleEvent = this.mVectorMessageListFragment.getEvent(this.mVectorMessageListFragment.getMessageListView().getFirstVisiblePosition());
                } catch (Exception e2) {
                    String str3 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## onReadMarkerDisplayed() : crash while retrieving mFirstVisibleEvent ");
                    sb3.append(e2.getMessage());
                    Log.m211e(str3, sb3.toString());
                }
            }
            checkUnreadMessage();
        }
    }
}
