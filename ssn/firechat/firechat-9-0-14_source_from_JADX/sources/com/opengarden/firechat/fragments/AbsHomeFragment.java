package com.opengarden.firechat.fragments;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.OnScrollListener;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorHomeActivity;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.RoomNotificationState;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRuleUpdateListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.RoomUtils.MoreActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class AbsHomeFragment extends VectorBaseFragment implements RoomInvitationListener, MoreRoomActionListener, MoreActionListener {
    private static final String CURRENT_FILTER = "CURRENT_FILTER";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "AbsHomeFragment";
    VectorHomeActivity mActivity;
    String mCurrentFilter;
    OnRoomChangedListener mOnRoomChangedListener;
    int mPrimaryColor = -1;
    final OnScrollListener mScrollListener = new OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (AbsHomeFragment.this.mActivity != null && i2 != 0) {
                AbsHomeFragment.this.mActivity.hideFloatingActionButton(AbsHomeFragment.this.getTag());
            }
        }
    };
    int mSecondaryColor = -1;
    MXSession mSession;

    public interface OnFilterListener {
        void onFilterDone(int i);
    }

    public interface OnRoomChangedListener {
        void onRoomForgot(String str);

        void onRoomLeft(String str);

        void onToggleDirectChat(String str, boolean z);
    }

    /* access modifiers changed from: protected */
    public abstract List<Room> getRooms();

    public boolean onFabClick() {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract void onFilter(String str, OnFilterListener onFilterListener);

    /* access modifiers changed from: protected */
    public abstract void onResetFilter();

    public void onSummariesUpdate() {
    }

    @CallSuper
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
    }

    @CallSuper
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (getActivity() instanceof VectorHomeActivity) {
            this.mActivity = (VectorHomeActivity) getActivity();
        }
        this.mSession = Matrix.getInstance(getActivity()).getDefaultSession();
        if (bundle != null && bundle.containsKey(CURRENT_FILTER)) {
            this.mCurrentFilter = bundle.getString(CURRENT_FILTER);
        }
    }

    @CallSuper
    public void onResume() {
        super.onResume();
        if (this.mPrimaryColor != -1 && this.mActivity != null) {
            this.mActivity.updateTabStyle(this.mPrimaryColor, this.mSecondaryColor != -1 ? this.mSecondaryColor : this.mPrimaryColor);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != C1299R.C1301id.ic_action_mark_all_as_read) {
            return false;
        }
        Log.m211e(LOG_TAG, "onOptionsItemSelected mark all as read");
        onMarkAllAsRead();
        return true;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(CURRENT_FILTER, this.mCurrentFilter);
    }

    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        this.mCurrentFilter = null;
    }

    @CallSuper
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    public void onPreviewRoom(MXSession mXSession, String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onPreviewRoom ");
        sb.append(str);
        Log.m213i(str2, sb.toString());
        this.mActivity.onPreviewRoom(mXSession, str);
    }

    public void onRejectInvitation(MXSession mXSession, String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onRejectInvitation ");
        sb.append(str);
        Log.m213i(str2, sb.toString());
        this.mActivity.onRejectInvitation(str, null);
    }

    public void onMoreActionClick(View view, Room room) {
        Set keys = room.getAccountData().getKeys();
        RoomUtils.displayPopupMenu(this.mActivity, this.mSession, room, view, keys != null && keys.contains(RoomTag.ROOM_TAG_FAVOURITE), keys != null && keys.contains(RoomTag.ROOM_TAG_LOW_PRIORITY), this);
    }

    public void onUpdateRoomNotificationsState(MXSession mXSession, String str, RoomNotificationState roomNotificationState) {
        this.mActivity.showWaitingView();
        mXSession.getDataHandler().getBingRulesManager().updateRoomNotificationState(str, roomNotificationState, new onBingRuleUpdateListener() {
            public void onBingRuleUpdateSuccess() {
                AbsHomeFragment.this.onRequestDone(null);
            }

            public void onBingRuleUpdateFailure(String str) {
                AbsHomeFragment.this.onRequestDone(str);
            }
        });
    }

    public void onToggleDirectChat(MXSession mXSession, final String str) {
        this.mActivity.showWaitingView();
        RoomUtils.toggleDirectChat(mXSession, str, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                AbsHomeFragment.this.onRequestDone(null);
                if (AbsHomeFragment.this.mOnRoomChangedListener != null) {
                    AbsHomeFragment.this.mOnRoomChangedListener.onToggleDirectChat(str, RoomUtils.isDirectChat(AbsHomeFragment.this.mSession, str));
                }
            }

            public void onNetworkError(Exception exc) {
                AbsHomeFragment.this.onRequestDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                AbsHomeFragment.this.onRequestDone(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                AbsHomeFragment.this.onRequestDone(exc.getLocalizedMessage());
            }
        });
    }

    public void moveToFavorites(MXSession mXSession, String str) {
        updateTag(str, null, RoomTag.ROOM_TAG_FAVOURITE);
    }

    public void moveToConversations(MXSession mXSession, String str) {
        updateTag(str, null, null);
    }

    public void moveToLowPriority(MXSession mXSession, String str) {
        updateTag(str, null, RoomTag.ROOM_TAG_LOW_PRIORITY);
    }

    public void onLeaveRoom(MXSession mXSession, final String str) {
        RoomUtils.showLeaveRoomDialog(getActivity(), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (AbsHomeFragment.this.mActivity != null && !AbsHomeFragment.this.mActivity.isFinishing()) {
                    AbsHomeFragment.this.mActivity.onRejectInvitation(str, new SimpleApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            if (AbsHomeFragment.this.mOnRoomChangedListener != null) {
                                AbsHomeFragment.this.mOnRoomChangedListener.onRoomLeft(str);
                            }
                        }
                    });
                }
            }
        });
    }

    public void onForgetRoom(MXSession mXSession, final String str) {
        this.mActivity.onForgetRoom(str, new SimpleApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (AbsHomeFragment.this.mOnRoomChangedListener != null) {
                    AbsHomeFragment.this.mOnRoomChangedListener.onRoomForgot(str);
                }
            }
        });
    }

    public void addHomeScreenShortcut(MXSession mXSession, String str) {
        RoomUtils.addHomeScreenShortcut(getActivity(), mXSession, str);
    }

    public void applyFilter(final String str) {
        if (TextUtils.isEmpty(str)) {
            if (this.mCurrentFilter != null) {
                onResetFilter();
                this.mCurrentFilter = null;
            }
        } else if (!TextUtils.equals(this.mCurrentFilter, str)) {
            onFilter(str, new OnFilterListener() {
                public void onFilterDone(int i) {
                    AbsHomeFragment.this.mCurrentFilter = str;
                }
            });
        }
    }

    /* access modifiers changed from: 0000 */
    public void openRoom(Room room) {
        if (this.mSession.getDataHandler() != null && this.mSession.getDataHandler().getStore() != null) {
            String roomId = (room == null || room.isLeaving()) ? null : room.getRoomId();
            if (roomId != null) {
                if (this.mSession.getDataHandler().getStore().getSummary(roomId) != null) {
                    room.sendReadReceipt();
                }
                CommonActivityUtils.specificUpdateBadgeUnreadCount(this.mSession, getContext());
                HashMap hashMap = new HashMap();
                hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
                hashMap.put("EXTRA_ROOM_ID", roomId);
                CommonActivityUtils.goToRoomPage(getActivity(), this.mSession, hashMap);
            }
        }
    }

    private void updateTag(String str, Double d, String str2) {
        this.mActivity.showWaitingView();
        RoomUtils.updateRoomTag(this.mSession, str, d, str2, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                AbsHomeFragment.this.onRequestDone(null);
            }

            public void onNetworkError(Exception exc) {
                AbsHomeFragment.this.onRequestDone(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                AbsHomeFragment.this.onRequestDone(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                AbsHomeFragment.this.onRequestDone(exc.getLocalizedMessage());
            }
        });
    }

    /* access modifiers changed from: private */
    public void onRequestDone(final String str) {
        if (this.mActivity != null && !this.mActivity.isFinishing()) {
            this.mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    AbsHomeFragment.this.mActivity.hideWaitingView();
                    if (!TextUtils.isEmpty(str)) {
                        Toast.makeText(AbsHomeFragment.this.mActivity, str, 0).show();
                    }
                }
            });
        }
    }

    private void onMarkAllAsRead() {
        this.mActivity.showWaitingView();
        this.mSession.markRoomsAsRead((Collection<Room>) getRooms(), (ApiCallback<Void>) new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (AbsHomeFragment.this.mActivity != null && !AbsHomeFragment.this.mActivity.isFinishing()) {
                    AbsHomeFragment.this.mActivity.hideWaitingView();
                    AbsHomeFragment.this.mActivity.refreshUnreadBadges();
                    if (AbsHomeFragment.this.isResumed()) {
                        AbsHomeFragment.this.onSummariesUpdate();
                    } else {
                        AbsHomeFragment.this.mActivity.dispatchOnSummariesUpdate();
                    }
                }
            }

            private void onError(String str) {
                String access$100 = AbsHomeFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## markAllMessagesAsRead() failed ");
                sb.append(str);
                Log.m211e(access$100, sb.toString());
                onSuccess((Void) null);
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                onError(matrixError.getMessage());
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getMessage());
            }
        });
    }
}
