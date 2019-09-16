package com.opengarden.firechat.matrixsdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.EventTimeline;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.EventTimelineListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.listeners.IMXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomResponse;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSync;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSyncState;
import com.opengarden.firechat.matrixsdk.rest.model.sync.RoomSyncTimeline;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.List;

public class MatrixMessagesFragment extends Fragment {
    public static final String ARG_ROOM_ID = "com.opengarden.firechat.matrixsdk.fragments.MatrixMessageFragment.ARG_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MatrixMessagesFragment";
    /* access modifiers changed from: private */
    public Context mContext;
    private final IMXEventListener mEventListener = new MXEventListener() {
        public void onLiveEventsChunkProcessed(String str, String str2) {
            if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                MatrixMessagesFragment.this.mMatrixMessagesListener.onLiveEventsChunkProcessed();
            }
        }

        public void onReceiptEvent(String str, List<String> list) {
            if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                MatrixMessagesFragment.this.mMatrixMessagesListener.onReceiptEvent(list);
            }
        }

        public void onRoomFlush(String str) {
            if (MatrixMessagesFragment.this.mMatrixMessagesListener != null && MatrixMessagesFragment.this.mEventTimeline.isLiveTimeline()) {
                MatrixMessagesFragment.this.mMatrixMessagesListener.onRoomFlush();
                MatrixMessagesFragment.this.mEventTimeline.initHistory();
                MatrixMessagesFragment.this.requestInitialHistory();
            }
        }

        public void onEventSent(Event event, String str) {
            if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                MatrixMessagesFragment.this.mMatrixMessagesListener.onEventSent(event, str);
            }
        }
    };
    /* access modifiers changed from: private */
    public EventTimeline mEventTimeline;
    private final EventTimelineListener mEventTimelineListener = new EventTimelineListener() {
        public void onEvent(Event event, Direction direction, RoomState roomState) {
            if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                MatrixMessagesFragment.this.mMatrixMessagesListener.onEvent(event, direction, roomState);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mHasPendingInitialHistory;
    public boolean mKeepRoomHistory;
    /* access modifiers changed from: private */
    public MatrixMessagesListener mMatrixMessagesListener;
    /* access modifiers changed from: private */
    public Room mRoom;
    private MXSession mSession;

    public interface MatrixMessagesListener {
        EventTimeline getEventTimeLine();

        RoomPreviewData getRoomPreviewData();

        void hideInitLoading();

        void onEvent(Event event, Direction direction, RoomState roomState);

        void onEventSent(Event event, String str);

        void onInitialMessagesLoaded();

        void onLiveEventsChunkProcessed();

        void onReceiptEvent(List<String> list);

        void onRoomFlush();

        void onTimelineInitialized();

        void showInitLoading();
    }

    public static MatrixMessagesFragment newInstance(MXSession mXSession, String str, MatrixMessagesListener matrixMessagesListener) {
        MatrixMessagesFragment matrixMessagesFragment = new MatrixMessagesFragment();
        Bundle bundle = new Bundle();
        if (matrixMessagesListener == null) {
            throw new RuntimeException("Must define a listener.");
        } else if (mXSession == null) {
            throw new RuntimeException("Must define a session.");
        } else {
            if (str != null) {
                bundle.putString(ARG_ROOM_ID, str);
            }
            matrixMessagesFragment.setArguments(bundle);
            matrixMessagesFragment.setMatrixMessagesListener(matrixMessagesListener);
            matrixMessagesFragment.setMXSession(mXSession);
            return matrixMessagesFragment;
        }
    }

    public void onCreate(Bundle bundle) {
        Log.m209d(LOG_TAG, "onCreate");
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m209d(LOG_TAG, "onCreateView");
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mContext = getActivity().getApplicationContext();
        String string = getArguments().getString(ARG_ROOM_ID);
        if (this.mSession == null) {
            List<Fragment> list = null;
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            if (supportFragmentManager != null) {
                list = supportFragmentManager.getFragments();
            }
            if (list != null) {
                for (Fragment fragment : list) {
                    if (fragment instanceof MatrixMessageListFragment) {
                        MatrixMessageListFragment matrixMessageListFragment = (MatrixMessageListFragment) fragment;
                        this.mMatrixMessagesListener = matrixMessageListFragment;
                        this.mSession = matrixMessageListFragment.getSession();
                    }
                }
            }
        }
        if (this.mSession == null) {
            throw new RuntimeException("Must have valid default MXSession.");
        } else if (this.mEventTimeline == null) {
            this.mEventTimeline = this.mMatrixMessagesListener.getEventTimeLine();
            if (this.mEventTimeline != null) {
                this.mEventTimeline.addEventTimelineListener(this.mEventTimelineListener);
                this.mRoom = this.mEventTimeline.getRoom();
            }
            if (this.mRoom == null) {
                this.mRoom = this.mSession.getDataHandler().getRoom(string);
            }
            this.mSession.getDataHandler().checkRoom(this.mRoom);
            if (this.mEventTimeline == null || this.mEventTimeline.isLiveTimeline() || this.mEventTimeline.getInitialEventId() == null) {
                boolean z = false;
                if (this.mRoom == null || this.mEventTimeline == null) {
                    sendInitialMessagesLoaded();
                } else {
                    this.mEventTimeline.initHistory();
                    if (this.mRoom.getState().getRoomCreateContent() != null) {
                        RoomMember member = this.mRoom.getMember(this.mSession.getCredentials().userId);
                        if (member != null && (RoomMember.MEMBERSHIP_JOIN.equals(member.membership) || RoomMember.MEMBERSHIP_KICK.equals(member.membership) || RoomMember.MEMBERSHIP_BAN.equals(member.membership))) {
                            z = true;
                        }
                    }
                    this.mRoom.addEventListener(this.mEventListener);
                    if (!this.mEventTimeline.isLiveTimeline()) {
                        previewRoom();
                    } else if (!z) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Joining room >> ");
                        sb.append(string);
                        Log.m209d(str, sb.toString());
                        joinRoom();
                    } else {
                        this.mHasPendingInitialHistory = true;
                    }
                }
            } else {
                initializeTimeline();
            }
            return onCreateView;
        } else {
            this.mEventTimeline.addEventTimelineListener(this.mEventTimelineListener);
            sendInitialMessagesLoaded();
            return onCreateView;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mRoom != null && this.mEventTimeline != null) {
            this.mRoom.removeEventListener(this.mEventListener);
            this.mEventTimeline.removeEventTimelineListener(this.mEventTimelineListener);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mHasPendingInitialHistory) {
            requestInitialHistory();
        }
    }

    /* access modifiers changed from: private */
    public void sendInitialMessagesLoaded() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                    MatrixMessagesFragment.this.mMatrixMessagesListener.onInitialMessagesLoaded();
                }
            }
        }, 100);
    }

    private void previewRoom() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Make a room preview of ");
        sb.append(this.mRoom.getRoomId());
        Log.m209d(str, sb.toString());
        if (this.mMatrixMessagesListener != null) {
            RoomPreviewData roomPreviewData = this.mMatrixMessagesListener.getRoomPreviewData();
            if (roomPreviewData != null) {
                if (roomPreviewData.getRoomResponse() != null) {
                    Log.m209d(LOG_TAG, "A preview data is provided with sync response");
                    RoomResponse roomResponse = roomPreviewData.getRoomResponse();
                    RoomSync roomSync = new RoomSync();
                    roomSync.state = new RoomSyncState();
                    roomSync.state.events = roomResponse.state;
                    roomSync.timeline = new RoomSyncTimeline();
                    roomSync.timeline.events = roomResponse.messages.chunk;
                    roomSync.timeline.limited = true;
                    roomSync.timeline.prevBatch = roomResponse.messages.end;
                    this.mEventTimeline.handleJoinedRoomSync(roomSync, true);
                    Log.m209d(LOG_TAG, "The room preview is done -> fill the room history");
                    this.mHasPendingInitialHistory = true;
                } else {
                    Log.m209d(LOG_TAG, "A preview data is provided with no sync response : assume that it is not possible to get a room preview");
                    if (!(getActivity() == null || this.mMatrixMessagesListener == null)) {
                        this.mMatrixMessagesListener.hideInitLoading();
                    }
                }
                return;
            }
        }
        this.mSession.getRoomsApiClient().initialSync(this.mRoom.getRoomId(), new ApiCallback<RoomResponse>() {
            public void onSuccess(RoomResponse roomResponse) {
                RoomSync roomSync = new RoomSync();
                roomSync.state = new RoomSyncState();
                roomSync.state.events = roomResponse.state;
                roomSync.timeline = new RoomSyncTimeline();
                roomSync.timeline.events = roomResponse.messages.chunk;
                MatrixMessagesFragment.this.mEventTimeline.handleJoinedRoomSync(roomSync, true);
                Log.m209d(MatrixMessagesFragment.LOG_TAG, "The room preview is done -> fill the room history");
                MatrixMessagesFragment.this.requestInitialHistory();
            }

            private void onError(String str) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("The room preview of ");
                sb.append(MatrixMessagesFragment.this.mRoom.getRoomId());
                sb.append("failed ");
                sb.append(str);
                Log.m211e(access$200, sb.toString());
                if (MatrixMessagesFragment.this.getActivity() != null) {
                    MatrixMessagesFragment.this.getActivity().finish();
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

    /* access modifiers changed from: protected */
    public void displayInitializeTimelineError(Object obj) {
        String str = "";
        if (obj instanceof MatrixError) {
            str = ((MatrixError) obj).getLocalizedMessage();
        } else if (obj instanceof Exception) {
            str = ((Exception) obj).getLocalizedMessage();
        }
        if (!TextUtils.isEmpty(str)) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("displayInitializeTimelineError : ");
            sb.append(str);
            Log.m209d(str2, sb.toString());
            Toast.makeText(this.mContext, str, 0).show();
        }
    }

    private void initializeTimeline() {
        Log.m209d(LOG_TAG, "initializeTimeline");
        if (this.mMatrixMessagesListener != null) {
            this.mMatrixMessagesListener.showInitLoading();
        }
        this.mEventTimeline.resetPaginationAroundInitialEvent(60, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                Log.m209d(MatrixMessagesFragment.LOG_TAG, "initializeTimeline is done");
                if (MatrixMessagesFragment.this.getActivity() != null && !MatrixMessagesFragment.this.getActivity().isFinishing()) {
                    if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                        MatrixMessagesFragment.this.mMatrixMessagesListener.hideInitLoading();
                        MatrixMessagesFragment.this.mMatrixMessagesListener.onTimelineInitialized();
                    }
                    MatrixMessagesFragment.this.sendInitialMessagesLoaded();
                }
            }

            private void onError() {
                Log.m209d(MatrixMessagesFragment.LOG_TAG, "initializeTimeline fails");
                if (MatrixMessagesFragment.this.getActivity() != null && !MatrixMessagesFragment.this.getActivity().isFinishing() && MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                    MatrixMessagesFragment.this.mMatrixMessagesListener.hideInitLoading();
                    MatrixMessagesFragment.this.mMatrixMessagesListener.onTimelineInitialized();
                }
            }

            public void onNetworkError(Exception exc) {
                MatrixMessagesFragment.this.displayInitializeTimelineError(exc);
                onError();
            }

            public void onMatrixError(MatrixError matrixError) {
                MatrixMessagesFragment.this.displayInitializeTimelineError(matrixError);
                onError();
            }

            public void onUnexpectedError(Exception exc) {
                MatrixMessagesFragment.this.displayInitializeTimelineError(exc);
                onError();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void requestInitialHistory() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("requestInitialHistory ");
        sb.append(this.mRoom.getRoomId());
        Log.m209d(str, sb.toString());
        if (backPaginate(new SimpleApiCallback<Integer>(getActivity()) {
            public void onSuccess(Integer num) {
                Log.m209d(MatrixMessagesFragment.LOG_TAG, "requestInitialHistory onSuccess");
                MatrixMessagesFragment.this.mHasPendingInitialHistory = false;
                if (MatrixMessagesFragment.this.getActivity() != null && MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                    MatrixMessagesFragment.this.mMatrixMessagesListener.hideInitLoading();
                    MatrixMessagesFragment.this.mMatrixMessagesListener.onTimelineInitialized();
                    MatrixMessagesFragment.this.mMatrixMessagesListener.onInitialMessagesLoaded();
                }
            }

            private void onError(String str) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("requestInitialHistory failed");
                sb.append(str);
                Log.m211e(access$200, sb.toString());
                MatrixMessagesFragment.this.mHasPendingInitialHistory = false;
                if (MatrixMessagesFragment.this.getActivity() != null) {
                    Toast.makeText(MatrixMessagesFragment.this.mContext, str, 1).show();
                    if (MatrixMessagesFragment.this.mMatrixMessagesListener != null) {
                        MatrixMessagesFragment.this.mMatrixMessagesListener.hideInitLoading();
                    }
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
        }) && this.mMatrixMessagesListener != null) {
            this.mMatrixMessagesListener.showInitLoading();
        }
    }

    public void setMatrixMessagesListener(MatrixMessagesListener matrixMessagesListener) {
        this.mMatrixMessagesListener = matrixMessagesListener;
    }

    public void setMXSession(MXSession mXSession) {
        this.mSession = mXSession;
    }

    public boolean canBackPaginate() {
        if (this.mEventTimeline != null) {
            return this.mEventTimeline.canBackPaginate();
        }
        return false;
    }

    public boolean backPaginate(ApiCallback<Integer> apiCallback) {
        if (this.mEventTimeline != null) {
            return this.mEventTimeline.backPaginate(apiCallback);
        }
        return false;
    }

    public boolean forwardPaginate(ApiCallback<Integer> apiCallback) {
        if (this.mEventTimeline == null || !this.mEventTimeline.isLiveTimeline()) {
            return false;
        }
        return this.mEventTimeline.forwardPaginate(apiCallback);
    }

    public void redact(String str, ApiCallback<Event> apiCallback) {
        if (this.mRoom != null) {
            this.mRoom.redact(str, apiCallback);
        }
    }

    private void joinRoom() {
        if (this.mMatrixMessagesListener != null) {
            this.mMatrixMessagesListener.showInitLoading();
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("joinRoom ");
        sb.append(this.mRoom.getRoomId());
        Log.m209d(str, sb.toString());
        this.mRoom.join(new SimpleApiCallback<Void>(getActivity()) {
            public void onSuccess(Void voidR) {
                Log.m209d(MatrixMessagesFragment.LOG_TAG, "joinRoom succeeds");
                MatrixMessagesFragment.this.requestInitialHistory();
            }

            private void onError(String str) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("joinRoom error: ");
                sb.append(str);
                Log.m211e(access$200, sb.toString());
                if (MatrixMessagesFragment.this.getActivity() != null) {
                    Toast.makeText(MatrixMessagesFragment.this.mContext, str, 0).show();
                    MatrixMessagesFragment.this.getActivity().finish();
                }
            }

            public void onNetworkError(Exception exc) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("joinRoom Network error: ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
                onError(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("joinRoom onMatrixError : ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$200, sb.toString());
                onError(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                String access$200 = MatrixMessagesFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("joinRoom Override : ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
                onError(exc.getLocalizedMessage());
            }
        });
    }
}
