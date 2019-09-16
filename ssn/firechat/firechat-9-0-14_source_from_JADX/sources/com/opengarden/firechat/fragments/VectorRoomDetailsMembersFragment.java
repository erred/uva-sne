package com.opengarden.firechat.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.p000v4.app.FragmentActivity;
import android.support.p003v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.MXCActionBarActivity;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.activity.VectorRoomInviteMembersActivity;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter;
import com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter.OnParticipantsListener;
import com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter.OnRoomMembersSearchListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang3.StringUtils;

public class VectorRoomDetailsMembersFragment extends VectorBaseFragment {
    private static final int GET_MENTION_REQUEST_CODE = 666;
    private static final int INVITE_USER_REQUEST_CODE = 777;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomDetailsMembersFragment";
    private static final boolean REFRESH_FORCED = true;
    private static final boolean REFRESH_NOT_FORCED = false;
    /* access modifiers changed from: private */
    public VectorRoomDetailsMembersAdapter mAdapter;
    private View mAddMembersFloatingActionButton;
    /* access modifiers changed from: private */
    public ImageView mClearSearchImageView;
    /* access modifiers changed from: private */
    public final ApiCallback<Void> mDefaultCallBack = new ApiCallback<Void>() {
        public void onSuccess(Void voidR) {
            if (VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                    }
                });
            }
        }

        public void onError(final String str) {
            if (VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                        Toast.makeText(VectorRoomDetailsMembersFragment.this.getActivity(), str, 0).show();
                    }
                });
            }
        }

        public void onNetworkError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }

        public void onMatrixError(final MatrixError matrixError) {
            if (VectorRoomDetailsMembersFragment.this.getRiotActivity() == null || !MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                onError(matrixError.getLocalizedMessage());
            } else {
                VectorRoomDetailsMembersFragment.this.getRiotActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                        VectorRoomDetailsMembersFragment.this.getRiotActivity().getConsentNotGivenHelper().displayDialog(matrixError);
                    }
                });
            }
        }

        public void onUnexpectedError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }
    };
    private final MXEventListener mEventListener = new MXEventListener() {
        public void onLiveEvent(final Event event, RoomState roomState) {
            VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String type = event.getType();
                    if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type) || Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS.equals(type)) {
                        VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, true);
                    }
                }
            });
        }

        public void onRoomFlush(String str) {
            VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, true);
                }
            });
        }

        public void onPresenceUpdate(Event event, final User user) {
            if (VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (VectorRoomDetailsMembersFragment.this.mAdapter.getUserIdsList().indexOf(user.user_id) >= 0) {
                            VectorRoomDetailsMembersFragment.this.delayedUpdateRoomMembersDataModel();
                        }
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsInvitingNewMembers;
    /* access modifiers changed from: private */
    public HashMap<Integer, Boolean> mIsListViewGroupExpandedMap;
    /* access modifiers changed from: private */
    public boolean mIsMultiSelectionMode;
    /* access modifiers changed from: private */
    public ExpandableListView mParticipantsListView;
    /* access modifiers changed from: private */
    public EditText mPatternToSearchEditText;
    /* access modifiers changed from: private */
    public String mPatternValue;
    /* access modifiers changed from: private */
    public View mProgressView;
    /* access modifiers changed from: private */
    public Timer mRefreshTimer;
    /* access modifiers changed from: private */
    public TimerTask mRefreshTimerTask;
    private MenuItem mRemoveMembersMenuItem;
    /* access modifiers changed from: private */
    public Room mRoom;
    private final OnRoomMembersSearchListener mSearchListener = new OnRoomMembersSearchListener() {
        public void onSearchEnd(final int i, boolean z) {
            VectorRoomDetailsMembersFragment.this.mParticipantsListView.post(new Runnable() {
                public void run() {
                    if (!VectorRoomDetailsMembersFragment.this.mIsInvitingNewMembers) {
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                    }
                    if (i == 0) {
                        VectorRoomDetailsMembersFragment.this.mSearchNoResultTextView.setVisibility(0);
                    } else {
                        VectorRoomDetailsMembersFragment.this.mSearchNoResultTextView.setVisibility(8);
                    }
                    if (TextUtils.isEmpty(VectorRoomDetailsMembersFragment.this.mPatternValue)) {
                        VectorRoomDetailsMembersFragment.this.updateListExpandingState();
                    } else {
                        VectorRoomDetailsMembersFragment.this.forceListInExpandingState();
                        VectorRoomDetailsMembersFragment.this.mClearSearchImageView.setVisibility(0);
                    }
                    VectorRoomDetailsMembersFragment.this.mParticipantsListView.post(new Runnable() {
                        public void run() {
                            VectorRoomDetailsMembersFragment.this.mParticipantsListView.setSelection(0);
                        }
                    });
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public TextView mSearchNoResultTextView;
    /* access modifiers changed from: private */
    public MXSession mSession;
    private MenuItem mSwitchDeletionMenuItem;
    private final TextWatcher mTextWatcherListener = new TextWatcher() {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void afterTextChanged(Editable editable) {
            final String obj = VectorRoomDetailsMembersFragment.this.mPatternToSearchEditText.getText().toString();
            if (TextUtils.isEmpty(obj)) {
                VectorRoomDetailsMembersFragment.this.mClearSearchImageView.setVisibility(4);
                VectorRoomDetailsMembersFragment.this.mPatternValue = null;
                VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, false);
                return;
            }
            new Timer().schedule(new TimerTask() {
                public void run() {
                    String str;
                    try {
                        str = VectorRoomDetailsMembersFragment.this.mPatternToSearchEditText.getText().toString();
                    } catch (Exception e) {
                        String access$1200 = VectorRoomDetailsMembersFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## afterTextChanged() failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$1200, sb.toString());
                        str = null;
                    }
                    if (TextUtils.equals(str, obj) && VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                        VectorRoomDetailsMembersFragment.this.mPatternValue = str;
                        VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, false);
                            }
                        });
                    }
                }
            }, 100);
            VectorRoomDetailsMembersFragment.this.mClearSearchImageView.setVisibility(0);
        }
    };
    /* access modifiers changed from: private */
    public Handler mUIHandler;
    private final List<String> mUpdatedPresenceUserIds = new ArrayList();
    private View mViewHierarchy;

    public static VectorRoomDetailsMembersFragment newInstance() {
        return new VectorRoomDetailsMembersFragment();
    }

    @SuppressLint({"LongLogTag"})
    public void onPause() {
        super.onPause();
        Log.m209d("RoomDetailsMembersFragment", "## onPause()");
        if (this.mPatternToSearchEditText != null) {
            this.mPatternToSearchEditText.removeTextChangedListener(this.mTextWatcherListener);
        }
        if (this.mRoom != null) {
            this.mRoom.removeEventListener(this.mEventListener);
        }
        if (this.mIsMultiSelectionMode) {
            toggleMultiSelectionMode();
        }
        if (this.mRefreshTimer != null) {
            this.mRefreshTimer.cancel();
            this.mRefreshTimer = null;
            this.mRefreshTimerTask = null;
        }
    }

    public void onStop() {
        if (getActivity() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
            if (!(inputMethodManager == null || this.mPatternToSearchEditText == null)) {
                inputMethodManager.hideSoftInputFromWindow(this.mPatternToSearchEditText.getApplicationWindowToken(), 0);
                this.mPatternToSearchEditText.clearFocus();
            }
        }
        super.onStop();
    }

    @SuppressLint({"LongLogTag"})
    public void onResume() {
        super.onResume();
        Log.m209d("RoomDetailsMembersFragment", "## onResume()");
        if (this.mPatternToSearchEditText != null) {
            this.mPatternToSearchEditText.addTextChangedListener(this.mTextWatcherListener);
        }
        if (this.mRoom != null) {
            this.mRoom.addEventListener(this.mEventListener);
        }
        refreshRoomMembersList(this.mPatternValue, false);
        updateListExpandingState();
        refreshMenuEntries();
        refreshMemberPresences();
    }

    @SuppressLint({"LongLogTag"})
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(CommonActivityUtils.KEY_GROUPS_EXPANDED_STATE, this.mIsListViewGroupExpandedMap);
        bundle.putString(CommonActivityUtils.KEY_SEARCH_PATTERN, this.mPatternValue);
        Log.m209d("RoomDetailsMembersFragment", "## onSaveInstanceState()");
    }

    /* access modifiers changed from: private */
    public void updateListExpandingState() {
        if (this.mParticipantsListView != null) {
            this.mParticipantsListView.post(new Runnable() {
                public void run() {
                    int groupCount = VectorRoomDetailsMembersFragment.this.mParticipantsListView.getExpandableListAdapter().getGroupCount();
                    Boolean valueOf = Boolean.valueOf(true);
                    for (int i = 0; i < groupCount; i++) {
                        if (VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap != null) {
                            valueOf = (Boolean) VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap.get(Integer.valueOf(i));
                        }
                        if (valueOf == null || true == valueOf.booleanValue()) {
                            VectorRoomDetailsMembersFragment.this.mParticipantsListView.expandGroup(i);
                        } else {
                            VectorRoomDetailsMembersFragment.this.mParticipantsListView.collapseGroup(i);
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void forceListInExpandingState() {
        if (this.mParticipantsListView != null) {
            this.mParticipantsListView.post(new Runnable() {
                public void run() {
                    int groupCount = VectorRoomDetailsMembersFragment.this.mParticipantsListView.getExpandableListAdapter().getGroupCount();
                    for (int i = 0; i < groupCount; i++) {
                        VectorRoomDetailsMembersFragment.this.mParticipantsListView.expandGroup(i);
                    }
                }
            });
        }
    }

    @SuppressLint({"LongLogTag"})
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mViewHierarchy = layoutInflater.inflate(C1299R.layout.fragment_vector_add_participants, viewGroup, false);
        FragmentActivity activity = getActivity();
        if (activity instanceof MXCActionBarActivity) {
            MXCActionBarActivity mXCActionBarActivity = (MXCActionBarActivity) activity;
            this.mRoom = mXCActionBarActivity.getRoom();
            this.mSession = mXCActionBarActivity.getSession();
            if (this.mSession == null || !this.mSession.isAlive()) {
                Log.m211e(LOG_TAG, "## onCreateView : the session is null -> kill the activity");
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                finalizeInit();
            }
        }
        if (bundle != null) {
            this.mIsListViewGroupExpandedMap = (HashMap) bundle.getSerializable(CommonActivityUtils.KEY_GROUPS_EXPANDED_STATE);
            this.mPatternValue = bundle.getString(CommonActivityUtils.KEY_SEARCH_PATTERN, null);
        } else if (this.mIsListViewGroupExpandedMap == null) {
            this.mIsListViewGroupExpandedMap = new HashMap<>();
        }
        setHasOptionsMenu(true);
        this.mUIHandler = new Handler(Looper.getMainLooper());
        return this.mViewHierarchy;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (!CommonActivityUtils.shouldRestartApp(getActivity())) {
            getActivity().getMenuInflater().inflate(C1299R.C1302menu.vector_room_details_add_people, menu);
            CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(getContext(), C1299R.attr.icon_tint_on_dark_action_bar_color));
            this.mRemoveMembersMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_details_delete);
            this.mSwitchDeletionMenuItem = menu.findItem(C1299R.C1301id.ic_action_room_details_edition_mode);
            processEditionMode();
            refreshMenuEntries();
        }
    }

    public boolean onBackPressed() {
        if (!this.mIsMultiSelectionMode) {
            return false;
        }
        toggleMultiSelectionMode();
        return true;
    }

    private boolean isUserAdmin() {
        if (this.mRoom == null || this.mSession == null) {
            return false;
        }
        PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
        if (powerLevels == null) {
            return false;
        }
        String myUserId = this.mSession.getMyUserId();
        if (myUserId == null || ((float) powerLevels.getUserPowerLevel(myUserId)) < 100.0f) {
            return false;
        }
        return true;
    }

    private void processEditionMode() {
        if (this.mSwitchDeletionMenuItem != null) {
            boolean z = false;
            if (isUserAdmin() && 1 != this.mAdapter.getItemsCount()) {
                z = true;
            }
            this.mSwitchDeletionMenuItem.setVisible(z);
            this.mSwitchDeletionMenuItem.setEnabled(z);
        }
    }

    /* access modifiers changed from: private */
    public void delayedUpdateRoomMembersDataModel() {
        if (this.mRefreshTimer != null) {
            this.mRefreshTimer.cancel();
            this.mRefreshTimer = null;
            this.mRefreshTimerTask = null;
        }
        try {
            this.mRefreshTimer = new Timer();
            this.mRefreshTimerTask = new TimerTask() {
                public void run() {
                    VectorRoomDetailsMembersFragment.this.mUIHandler.post(new Runnable() {
                        public void run() {
                            if (VectorRoomDetailsMembersFragment.this.mRefreshTimer != null) {
                                VectorRoomDetailsMembersFragment.this.mRefreshTimer.cancel();
                            }
                            VectorRoomDetailsMembersFragment.this.mRefreshTimer = null;
                            VectorRoomDetailsMembersFragment.this.mRefreshTimerTask = null;
                            VectorRoomDetailsMembersFragment.this.mAdapter.updateRoomMembersDataModel(null);
                        }
                    });
                }
            };
            this.mRefreshTimer.schedule(this.mRefreshTimerTask, 1000);
        } catch (Throwable th) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## delayedUpdateRoomMembersDataModel() failed ");
            sb.append(th.getMessage());
            Log.m211e(str, sb.toString());
            if (this.mRefreshTimer != null) {
                this.mRefreshTimer.cancel();
                this.mRefreshTimer = null;
            }
            this.mRefreshTimerTask = null;
            this.mAdapter.updateRoomMembersDataModel(null);
        }
    }

    /* access modifiers changed from: private */
    public void refreshMemberPresences() {
        int firstVisiblePosition = this.mParticipantsListView.getFirstVisiblePosition();
        int lastVisiblePosition = this.mParticipantsListView.getLastVisiblePosition() + 20;
        int count = this.mParticipantsListView.getCount();
        while (firstVisiblePosition <= lastVisiblePosition && firstVisiblePosition < count) {
            Object itemAtPosition = this.mParticipantsListView.getItemAtPosition(firstVisiblePosition);
            if (itemAtPosition instanceof ParticipantAdapterItem) {
                ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) itemAtPosition;
                if (this.mUpdatedPresenceUserIds.indexOf(participantAdapterItem.mUserId) < 0) {
                    this.mUpdatedPresenceUserIds.add(participantAdapterItem.mUserId);
                    VectorUtils.getUserOnlineStatus(getActivity(), this.mSession, participantAdapterItem.mUserId, new SimpleApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            VectorRoomDetailsMembersFragment.this.mUIHandler.post(new Runnable() {
                                public void run() {
                                    VectorRoomDetailsMembersFragment.this.delayedUpdateRoomMembersDataModel();
                                }
                            });
                        }
                    });
                }
            }
            firstVisiblePosition++;
        }
    }

    private void refreshMenuEntries() {
        if (this.mRemoveMembersMenuItem != null) {
            this.mRemoveMembersMenuItem.setVisible(this.mIsMultiSelectionMode);
            if (this.mAddMembersFloatingActionButton != null) {
                this.mAddMembersFloatingActionButton.setVisibility(this.mIsMultiSelectionMode ? 8 : 0);
            }
        }
        if (this.mSwitchDeletionMenuItem != null && this.mSwitchDeletionMenuItem.isEnabled()) {
            this.mSwitchDeletionMenuItem.setVisible(!this.mIsMultiSelectionMode);
        }
    }

    /* access modifiers changed from: private */
    public void setActivityTitle(String str) {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle((CharSequence) str);
        }
    }

    /* access modifiers changed from: private */
    public void resetActivityTitle() {
        this.mRemoveMembersMenuItem.setEnabled(true);
        this.mSwitchDeletionMenuItem.setEnabled(true);
        setActivityTitle(getResources().getString(C1299R.string.room_details_title));
    }

    /* access modifiers changed from: private */
    public void toggleMultiSelectionMode() {
        resetActivityTitle();
        this.mIsMultiSelectionMode = !this.mIsMultiSelectionMode;
        this.mAdapter.setMultiSelectionMode(this.mIsMultiSelectionMode);
        refreshMenuEntries();
        this.mAdapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void kickUsers(final List<String> list, final int i) {
        if (i >= list.size()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                        if (VectorRoomDetailsMembersFragment.this.mIsMultiSelectionMode) {
                            VectorRoomDetailsMembersFragment.this.toggleMultiSelectionMode();
                            VectorRoomDetailsMembersFragment.this.resetActivityTitle();
                        }
                        VectorRoomDetailsMembersFragment.this.mAdapter.notifyDataSetChanged();
                    }
                });
            }
            return;
        }
        this.mRemoveMembersMenuItem.setEnabled(false);
        this.mSwitchDeletionMenuItem.setEnabled(false);
        this.mProgressView.setVisibility(0);
        this.mRoom.kick((String) list.get(i), new ApiCallback<Void>() {
            private void kickNext() {
                VectorRoomDetailsMembersFragment.this.kickUsers(list, i + 1);
            }

            public void onSuccess(Void voidR) {
                kickNext();
            }

            public void onNetworkError(Exception exc) {
                kickNext();
            }

            public void onMatrixError(MatrixError matrixError) {
                if (VectorRoomDetailsMembersFragment.this.getRiotActivity() == null) {
                    kickNext();
                } else if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                    VectorRoomDetailsMembersFragment.this.getRiotActivity().getConsentNotGivenHelper().displayDialog(matrixError);
                } else {
                    Toast.makeText(VectorRoomDetailsMembersFragment.this.getActivity(), matrixError.getLocalizedMessage(), 0).show();
                    kickNext();
                }
            }

            public void onUnexpectedError(Exception exc) {
                kickNext();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == C1299R.C1301id.ic_action_room_details_delete) {
            kickUsers(this.mAdapter.getSelectedUserIds(), 0);
        } else if (itemId == C1299R.C1301id.ic_action_room_details_edition_mode) {
            toggleMultiSelectionMode();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void finalizeInit() {
        MXMediasCache mediasCache = this.mSession.getMediasCache();
        this.mAddMembersFloatingActionButton = this.mViewHierarchy.findViewById(C1299R.C1301id.add_participants_create_view);
        this.mAddMembersFloatingActionButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(VectorRoomDetailsMembersFragment.this.getActivity(), VectorRoomInviteMembersActivity.class);
                intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomDetailsMembersFragment.this.mSession.getMyUserId());
                intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_ROOM_ID, VectorRoomDetailsMembersFragment.this.mRoom.getRoomId());
                intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_ADD_CONFIRMATION_DIALOG, true);
                VectorRoomDetailsMembersFragment.this.getActivity().startActivityForResult(intent, VectorRoomDetailsMembersFragment.INVITE_USER_REQUEST_CODE);
            }
        });
        this.mPatternToSearchEditText = (EditText) this.mViewHierarchy.findViewById(C1299R.C1301id.search_value_edit_text);
        this.mClearSearchImageView = (ImageView) this.mViewHierarchy.findViewById(C1299R.C1301id.clear_search_icon_image_view);
        this.mSearchNoResultTextView = (TextView) this.mViewHierarchy.findViewById(C1299R.C1301id.search_no_results_text_view);
        this.mPatternToSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 3 && i != 2 && i != 6) {
                    return false;
                }
                String access$000 = VectorRoomDetailsMembersFragment.this.mPatternValue;
                VectorRoomDetailsMembersFragment.this.mPatternValue = VectorRoomDetailsMembersFragment.this.mPatternToSearchEditText.getText().toString();
                if (TextUtils.isEmpty(VectorRoomDetailsMembersFragment.this.mPatternValue.trim())) {
                    VectorRoomDetailsMembersFragment.this.mPatternValue = access$000;
                } else {
                    VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, false);
                }
                return true;
            }
        });
        this.mClearSearchImageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VectorRoomDetailsMembersFragment.this.mPatternToSearchEditText.setText("");
                VectorRoomDetailsMembersFragment.this.mPatternValue = null;
                VectorRoomDetailsMembersFragment.this.refreshRoomMembersList(VectorRoomDetailsMembersFragment.this.mPatternValue, false);
                VectorRoomDetailsMembersFragment.this.forceListInExpandingState();
            }
        });
        this.mProgressView = this.mViewHierarchy.findViewById(C1299R.C1301id.add_participants_progress_view);
        this.mParticipantsListView = (ExpandableListView) this.mViewHierarchy.findViewById(C1299R.C1301id.room_details_members_exp_list_view);
        VectorRoomDetailsMembersAdapter vectorRoomDetailsMembersAdapter = new VectorRoomDetailsMembersAdapter(getActivity(), C1299R.layout.adapter_item_vector_add_participants, C1299R.layout.adapter_item_vector_recent_header, this.mSession, this.mRoom.getRoomId(), mediasCache);
        this.mAdapter = vectorRoomDetailsMembersAdapter;
        this.mParticipantsListView.setAdapter(this.mAdapter);
        this.mParticipantsListView.setGroupIndicator(null);
        this.mParticipantsListView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                VectorRoomDetailsMembersFragment.this.refreshMemberPresences();
            }
        });
        this.mAdapter.setOnParticipantsListener(new OnParticipantsListener() {
            public void onClick(ParticipantAdapterItem participantAdapterItem) {
                Intent intent = new Intent(VectorRoomDetailsMembersFragment.this.getActivity(), VectorMemberDetailsActivity.class);
                intent.putExtra("EXTRA_ROOM_ID", VectorRoomDetailsMembersFragment.this.mRoom.getRoomId());
                intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, participantAdapterItem.mUserId);
                intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomDetailsMembersFragment.this.mSession.getCredentials().userId);
                VectorRoomDetailsMembersFragment.this.getActivity().startActivityForResult(intent, VectorRoomDetailsMembersFragment.GET_MENTION_REQUEST_CODE);
            }

            public void onSelectUserId(String str) {
                ArrayList selectedUserIds = VectorRoomDetailsMembersFragment.this.mAdapter.getSelectedUserIds();
                if (selectedUserIds.size() != 0) {
                    VectorRoomDetailsMembersFragment vectorRoomDetailsMembersFragment = VectorRoomDetailsMembersFragment.this;
                    StringBuilder sb = new StringBuilder();
                    sb.append(selectedUserIds.size());
                    sb.append(StringUtils.SPACE);
                    sb.append(VectorRoomDetailsMembersFragment.this.getActivity().getResources().getString(C1299R.string.room_details_selected));
                    vectorRoomDetailsMembersFragment.setActivityTitle(sb.toString());
                    return;
                }
                VectorRoomDetailsMembersFragment.this.resetActivityTitle();
            }

            public void onRemoveClick(final ParticipantAdapterItem participantAdapterItem) {
                new Builder(VectorRoomDetailsMembersFragment.this.getActivity()).setTitle(C1299R.string.dialog_title_confirmation).setMessage(VectorRoomDetailsMembersFragment.this.getActivity().getString(C1299R.string.room_participants_remove_prompt_msg, new Object[]{participantAdapterItem.mDisplayName})).setPositiveButton(C1299R.string.remove, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRoomDetailsMembersFragment.this.kickUsers(Collections.singletonList(participantAdapterItem.mUserId), 0);
                            }
                        });
                    }
                }).setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }

            public void onLeaveClick() {
                new Builder(VectorRoomDetailsMembersFragment.this.getActivity()).setTitle(C1299R.string.room_participants_leave_prompt_title).setMessage(VectorRoomDetailsMembersFragment.this.getActivity().getString(C1299R.string.room_participants_leave_prompt_msg)).setPositiveButton(C1299R.string.leave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(0);
                        VectorRoomDetailsMembersFragment.this.mRoom.leave(new ApiCallback<Void>() {
                            public void onSuccess(Void voidR) {
                                if (VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                                    VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            VectorRoomDetailsMembersFragment.this.getActivity().finish();
                                        }
                                    });
                                }
                            }

                            private void onError(final String str) {
                                if (VectorRoomDetailsMembersFragment.this.getActivity() != null) {
                                    VectorRoomDetailsMembersFragment.this.getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                                            Toast.makeText(VectorRoomDetailsMembersFragment.this.getActivity(), str, 0).show();
                                        }
                                    });
                                }
                            }

                            public void onNetworkError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }

                            public void onMatrixError(final MatrixError matrixError) {
                                if (VectorRoomDetailsMembersFragment.this.getRiotActivity() == null || !MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                                    onError(matrixError.getLocalizedMessage());
                                } else {
                                    VectorRoomDetailsMembersFragment.this.getRiotActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            VectorRoomDetailsMembersFragment.this.mProgressView.setVisibility(8);
                                            VectorRoomDetailsMembersFragment.this.getRiotActivity().getConsentNotGivenHelper().displayDialog(matrixError);
                                        }
                                    });
                                }
                            }

                            public void onUnexpectedError(Exception exc) {
                                onError(exc.getLocalizedMessage());
                            }
                        });
                    }
                }).setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }

            public void onGroupCollapsedNotif(int i) {
                if (VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap != null) {
                    VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap.put(Integer.valueOf(i), Boolean.valueOf(false));
                }
            }

            public void onGroupExpandedNotif(int i) {
                if (VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap != null) {
                    VectorRoomDetailsMembersFragment.this.mIsListViewGroupExpandedMap.put(Integer.valueOf(i), Boolean.valueOf(true));
                }
            }
        });
    }

    /* access modifiers changed from: private */
    @SuppressLint({"LongLogTag"})
    public void refreshRoomMembersList(String str, boolean z) {
        if (this.mAdapter != null) {
            this.mProgressView.setVisibility(0);
            this.mAdapter.setSearchedPattern(str, this.mSearchListener, z);
        } else {
            Log.m217w(LOG_TAG, "## refreshRoomMembersList(): search failure - adapter not initialized");
        }
        processEditionMode();
    }

    private void inviteUserIds(List<String> list) {
        this.mRoom.invite(list, (ApiCallback<Void>) new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                VectorRoomDetailsMembersFragment.this.mIsInvitingNewMembers = false;
                VectorRoomDetailsMembersFragment.this.mDefaultCallBack.onSuccess(null);
            }

            public void onNetworkError(Exception exc) {
                VectorRoomDetailsMembersFragment.this.mIsInvitingNewMembers = false;
                VectorRoomDetailsMembersFragment.this.mDefaultCallBack.onNetworkError(exc);
            }

            public void onMatrixError(MatrixError matrixError) {
                VectorRoomDetailsMembersFragment.this.mIsInvitingNewMembers = false;
                VectorRoomDetailsMembersFragment.this.mDefaultCallBack.onMatrixError(matrixError);
            }

            public void onUnexpectedError(Exception exc) {
                VectorRoomDetailsMembersFragment.this.mIsInvitingNewMembers = false;
                VectorRoomDetailsMembersFragment.this.mDefaultCallBack.onUnexpectedError(exc);
            }
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == INVITE_USER_REQUEST_CODE && i2 == -1) {
            List list = (List) intent.getSerializableExtra(VectorRoomInviteMembersActivity.EXTRA_OUT_SELECTED_USER_IDS);
            if (list != null && list.size() > 0) {
                inviteUserIds(list);
            }
        } else if (i == GET_MENTION_REQUEST_CODE && i2 == -1) {
            String stringExtra = intent.getStringExtra(VectorMemberDetailsActivity.RESULT_MENTION_ID);
            if (!TextUtils.isEmpty(stringExtra) && getActivity() != null) {
                Intent intent2 = new Intent();
                intent2.putExtra(VectorMemberDetailsActivity.RESULT_MENTION_ID, stringExtra);
                getActivity().setResult(-1, intent2);
                getActivity().finish();
            }
        }
    }
}
