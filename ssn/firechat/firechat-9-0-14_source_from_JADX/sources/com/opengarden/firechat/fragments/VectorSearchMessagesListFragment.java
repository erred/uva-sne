package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter;
import com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.data.EventTimeline.Direction;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VectorSearchMessagesListFragment extends VectorMessageListFragment {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorSearchMessagesListFragment";
    private View mProgressView = null;
    String mRoomId;
    final ArrayList<OnSearchResultListener> mSearchListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public String mSearchingPattern;

    public boolean isDisplayAllEvents() {
        return true;
    }

    public boolean onContentLongClick(int i) {
        return false;
    }

    public void onEvent(Event event, Direction direction, RoomState roomState) {
    }

    public void onListTouch(MotionEvent motionEvent) {
    }

    public void onLiveEventsChunkProcessed() {
    }

    public void onReceiptEvent(List<String> list) {
    }

    public static VectorSearchMessagesListFragment newInstance(String str, String str2, int i) {
        VectorSearchMessagesListFragment vectorSearchMessagesListFragment = new VectorSearchMessagesListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MatrixMessageListFragment.ARG_LAYOUT_ID, i);
        bundle.putString(MatrixMessageListFragment.ARG_MATRIX_ID, str);
        if (str2 != null) {
            bundle.putString(MatrixMessageListFragment.ARG_ROOM_ID, str2);
        }
        vectorSearchMessagesListFragment.setArguments(bundle);
        return vectorSearchMessagesListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mRoomId = arguments.getString(MatrixMessageListFragment.ARG_ROOM_ID, null);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public AbstractMessagesAdapter createMessagesAdapter() {
        return new VectorSearchMessagesListAdapter(this.mSession, getActivity(), this.mRoomId == null, getMXMediasCache());
    }

    public void onPause() {
        super.onPause();
        if (this.mSession.isAlive()) {
            cancelSearch();
            if (this.mIsMediaSearch) {
                this.mSession.cancelSearchMediasByText();
            } else {
                this.mSession.cancelSearchMessagesByText();
            }
            this.mSearchingPattern = null;
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mProgressView = getActivity().findViewById(C1299R.C1301id.search_load_oldest_progress);
    }

    public void showLoadingBackProgress() {
        if (this.mProgressView != null) {
            this.mProgressView.setVisibility(0);
        }
    }

    public void hideLoadingBackProgress() {
        if (this.mProgressView != null) {
            this.mProgressView.setVisibility(8);
        }
    }

    public void scrollToBottom() {
        if (this.mAdapter.getCount() != 0) {
            this.mMessageListView.setSelection(this.mAdapter.getCount() - 1);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean allowSearch(String str) {
        return !TextUtils.isEmpty(str);
    }

    public void onInitialMessagesLoaded() {
        if (!allowSearch(this.mPattern)) {
            Log.m211e(LOG_TAG, "## onInitialMessagesLoaded() : history filling is cancelled");
        } else {
            super.onInitialMessagesLoaded();
        }
    }

    public void searchPattern(final String str, OnSearchResultListener onSearchResultListener) {
        if (onSearchResultListener != null) {
            this.mSearchListeners.add(onSearchResultListener);
        }
        if (this.mMessageListView != null) {
            if (TextUtils.equals(this.mSearchingPattern, str)) {
                this.mSearchListeners.add(onSearchResultListener);
                return;
            }
            if (!allowSearch(str)) {
                this.mPattern = null;
                this.mMessageListView.setVisibility(8);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Iterator it = VectorSearchMessagesListFragment.this.mSearchListeners.iterator();
                        while (it.hasNext()) {
                            try {
                                ((OnSearchResultListener) it.next()).onSearchSucceed(0);
                            } catch (Exception e) {
                                String access$000 = VectorSearchMessagesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## searchPattern() : failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$000, sb.toString());
                            }
                        }
                        VectorSearchMessagesListFragment.this.mSearchListeners.clear();
                        VectorSearchMessagesListFragment.this.mSearchingPattern = null;
                    }
                });
            } else if (TextUtils.equals(this.mPattern, str)) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        Iterator it = VectorSearchMessagesListFragment.this.mSearchListeners.iterator();
                        while (it.hasNext()) {
                            try {
                                ((OnSearchResultListener) it.next()).onSearchSucceed(VectorSearchMessagesListFragment.this.mAdapter.getCount());
                            } catch (Exception e) {
                                String access$000 = VectorSearchMessagesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## searchPattern() : failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$000, sb.toString());
                            }
                        }
                        VectorSearchMessagesListFragment.this.mSearchListeners.clear();
                    }
                });
            } else {
                this.mAdapter.clear();
                this.mSearchingPattern = str;
                if (this.mAdapter instanceof VectorSearchMessagesListAdapter) {
                    ((VectorSearchMessagesListAdapter) this.mAdapter).setTextToHighlight(str);
                }
                super.searchPattern(str, this.mIsMediaSearch, new OnSearchResultListener() {
                    public void onSearchSucceed(int i) {
                        if (!TextUtils.equals(str, VectorSearchMessagesListFragment.this.mSearchingPattern)) {
                            VectorSearchMessagesListFragment.this.mAdapter.clear();
                            VectorSearchMessagesListFragment.this.mMessageListView.setVisibility(8);
                            return;
                        }
                        VectorSearchMessagesListFragment.this.mIsInitialSyncing = false;
                        VectorSearchMessagesListFragment.this.mMessageListView.setOnScrollListener(VectorSearchMessagesListFragment.this.mScrollListener);
                        VectorSearchMessagesListFragment.this.mMessageListView.setAdapter(VectorSearchMessagesListFragment.this.mAdapter);
                        VectorSearchMessagesListFragment.this.mMessageListView.setVisibility(0);
                        VectorSearchMessagesListFragment.this.scrollToBottom();
                        Iterator it = VectorSearchMessagesListFragment.this.mSearchListeners.iterator();
                        while (it.hasNext()) {
                            try {
                                ((OnSearchResultListener) it.next()).onSearchSucceed(i);
                            } catch (Exception e) {
                                String access$000 = VectorSearchMessagesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## searchPattern() : failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$000, sb.toString());
                            }
                        }
                        VectorSearchMessagesListFragment.this.mSearchListeners.clear();
                        VectorSearchMessagesListFragment.this.mSearchingPattern = null;
                        VectorSearchMessagesListFragment.this.backPaginate(true);
                    }

                    public void onSearchFailed() {
                        VectorSearchMessagesListFragment.this.mMessageListView.setVisibility(8);
                        VectorSearchMessagesListFragment.this.mAdapter.clear();
                        Iterator it = VectorSearchMessagesListFragment.this.mSearchListeners.iterator();
                        while (it.hasNext()) {
                            try {
                                ((OnSearchResultListener) it.next()).onSearchFailed();
                            } catch (Exception e) {
                                String access$000 = VectorSearchMessagesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## searchPattern() : onSearchFailed failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$000, sb.toString());
                            }
                        }
                        VectorSearchMessagesListFragment.this.mSearchListeners.clear();
                        VectorSearchMessagesListFragment.this.mSearchingPattern = null;
                    }
                });
            }
        }
    }

    public boolean onRowLongClick(int i) {
        onContentClick(i);
        return true;
    }

    public void onContentClick(int i) {
        Event event = ((MessageRow) this.mAdapter.getItem(i)).getEvent();
        Intent intent = new Intent(getActivity(), VectorRoomActivity.class);
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
        intent.putExtra("EXTRA_ROOM_ID", event.roomId);
        intent.putExtra(VectorRoomActivity.EXTRA_EVENT_ID, event.eventId);
        getActivity().startActivity(intent);
    }
}
