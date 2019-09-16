package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.TokensChunkResponse;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class VectorSearchRoomFilesListFragment extends VectorSearchRoomsFilesListFragment {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorSearchRoomFilesListFragment";
    private static final int MESSAGES_PAGINATION_LIMIT = 50;
    /* access modifiers changed from: private */
    public boolean mCanPaginateBack = true;
    /* access modifiers changed from: private */
    public final String mTimeLineId;

    /* access modifiers changed from: protected */
    public boolean allowSearch(String str) {
        return true;
    }

    public VectorSearchRoomFilesListFragment() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.currentTimeMillis());
        sb.append("");
        this.mTimeLineId = sb.toString();
    }

    public static VectorSearchRoomFilesListFragment newInstance(String str, String str2, int i) {
        VectorSearchRoomFilesListFragment vectorSearchRoomFilesListFragment = new VectorSearchRoomFilesListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MatrixMessageListFragment.ARG_LAYOUT_ID, i);
        bundle.putString(MatrixMessageListFragment.ARG_MATRIX_ID, str);
        if (str2 != null) {
            bundle.putString(MatrixMessageListFragment.ARG_ROOM_ID, str2);
        }
        vectorSearchRoomFilesListFragment.setArguments(bundle);
        return vectorSearchRoomFilesListFragment;
    }

    public void cancelCatchingRequests() {
        super.cancelCatchingRequests();
        this.mIsBackPaginating = false;
        this.mCanPaginateBack = true;
        if (this.mRoom != null) {
            this.mRoom.cancelRemoteHistoryRequest();
            this.mNextBatch = this.mRoom.getLiveState().getToken();
        }
        if (this.mSession != null) {
            this.mSession.getDataHandler().resetReplayAttackCheckInTimeline(this.mTimeLineId);
        }
    }

    public void onPause() {
        super.onPause();
        cancelCatchingRequests();
    }

    public void startFilesSearch(OnSearchResultListener onSearchResultListener) {
        if (!this.mIsBackPaginating) {
            if (onSearchResultListener != null) {
                this.mSearchListeners.add(onSearchResultListener);
            }
            if (this.mMessageListView != null) {
                this.mIsBackPaginating = true;
                this.mMessageListView.setVisibility(8);
                remoteRoomHistoryRequest(new ArrayList(), new ApiCallback<ArrayList<Event>>() {
                    public void onSuccess(ArrayList<Event> arrayList) {
                        ArrayList arrayList2 = new ArrayList(arrayList.size());
                        RoomState liveState = VectorSearchRoomFilesListFragment.this.mRoom.getLiveState();
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            arrayList2.add(new MessageRow((Event) it.next(), liveState));
                        }
                        Collections.reverse(arrayList2);
                        VectorSearchRoomFilesListFragment.this.mAdapter.clear();
                        VectorSearchRoomFilesListFragment.this.mAdapter.addAll(arrayList2);
                        VectorSearchRoomFilesListFragment.this.mMessageListView.setAdapter(VectorSearchRoomFilesListFragment.this.mAdapter);
                        VectorSearchRoomFilesListFragment.this.mMessageListView.setOnScrollListener(VectorSearchRoomFilesListFragment.this.mScrollListener);
                        VectorSearchRoomFilesListFragment.this.scrollToBottom();
                        VectorSearchRoomFilesListFragment.this.mMessageListView.setVisibility(0);
                        Iterator it2 = VectorSearchRoomFilesListFragment.this.mSearchListeners.iterator();
                        while (it2.hasNext()) {
                            try {
                                ((OnSearchResultListener) it2.next()).onSearchSucceed(arrayList2.size());
                            } catch (Exception e) {
                                String access$500 = VectorSearchRoomFilesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## remoteRoomHistoryRequest() : onSearchSucceed failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        VectorSearchRoomFilesListFragment.this.mIsBackPaginating = false;
                        VectorSearchRoomFilesListFragment.this.mSearchListeners.clear();
                    }

                    private void onError() {
                        VectorSearchRoomFilesListFragment.this.mMessageListView.setVisibility(8);
                        VectorSearchRoomFilesListFragment.this.mAdapter.clear();
                        Iterator it = VectorSearchRoomFilesListFragment.this.mSearchListeners.iterator();
                        while (it.hasNext()) {
                            try {
                                ((OnSearchResultListener) it.next()).onSearchFailed();
                            } catch (Exception e) {
                                String access$500 = VectorSearchRoomFilesListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## remoteRoomHistoryRequest() : onSearchFailed failed ");
                                sb.append(e.getMessage());
                                Log.m211e(access$500, sb.toString());
                            }
                        }
                        VectorSearchRoomFilesListFragment.this.mIsBackPaginating = false;
                        VectorSearchRoomFilesListFragment.this.mSearchListeners.clear();
                    }

                    public void onNetworkError(Exception exc) {
                        Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                        onError();
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), matrixError.getLocalizedMessage(), 1).show();
                        onError();
                    }

                    public void onUnexpectedError(Exception exc) {
                        Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                        onError();
                    }
                });
            }
        }
    }

    public void backPaginate(boolean z) {
        if (!this.mIsBackPaginating && this.mCanPaginateBack) {
            this.mIsBackPaginating = true;
            final int firstVisiblePosition = this.mMessageListView.getFirstVisiblePosition();
            final int count = this.mAdapter.getCount();
            if (this.mAdapter.getCount() != 0) {
                showLoadingBackProgress();
            }
            remoteRoomHistoryRequest(new ArrayList(), new ApiCallback<ArrayList<Event>>() {
                public void onSuccess(final ArrayList<Event> arrayList) {
                    VectorSearchRoomFilesListFragment.this.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (arrayList.size() != 0) {
                                VectorSearchRoomFilesListFragment.this.mAdapter.setNotifyOnChange(false);
                                Iterator it = arrayList.iterator();
                                while (it.hasNext()) {
                                    VectorSearchRoomFilesListFragment.this.mAdapter.insert(new MessageRow((Event) it.next(), VectorSearchRoomFilesListFragment.this.mRoom.getLiveState()), 0);
                                }
                                VectorSearchRoomFilesListFragment.this.mUiHandler.post(new Runnable() {
                                    public void run() {
                                        VectorSearchRoomFilesListFragment.this.mAdapter.notifyDataSetChanged();
                                        VectorSearchRoomFilesListFragment.this.mMessageListView.setSelection(firstVisiblePosition + (VectorSearchRoomFilesListFragment.this.mAdapter.getCount() - count));
                                        VectorSearchRoomFilesListFragment.this.mIsBackPaginating = false;
                                        VectorSearchRoomFilesListFragment.this.setMessageListViewScrollListener();
                                        Iterator it = VectorSearchRoomFilesListFragment.this.mSearchListeners.iterator();
                                        while (it.hasNext()) {
                                            try {
                                                ((OnSearchResultListener) it.next()).onSearchSucceed(arrayList.size());
                                            } catch (Exception e) {
                                                String access$500 = VectorSearchRoomFilesListFragment.LOG_TAG;
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("## backPaginate() : onSearchSucceed failed ");
                                                sb.append(e.getMessage());
                                                Log.m211e(access$500, sb.toString());
                                            }
                                        }
                                        VectorSearchRoomFilesListFragment.this.mSearchListeners.clear();
                                        VectorSearchRoomFilesListFragment.this.mMessageListView.post(new Runnable() {
                                            public void run() {
                                                if (VectorSearchRoomFilesListFragment.this.mMessageListView.getFirstVisiblePosition() < 2) {
                                                    VectorSearchRoomFilesListFragment.this.backPaginate(true);
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                VectorSearchRoomFilesListFragment.this.mIsBackPaginating = false;
                                VectorSearchRoomFilesListFragment.this.mUiHandler.post(new Runnable() {
                                    public void run() {
                                        Iterator it = VectorSearchRoomFilesListFragment.this.mSearchListeners.iterator();
                                        while (it.hasNext()) {
                                            try {
                                                ((OnSearchResultListener) it.next()).onSearchSucceed(0);
                                            } catch (Exception e) {
                                                String access$500 = VectorSearchRoomFilesListFragment.LOG_TAG;
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("## backPaginate() : onSearchSucceed failed ");
                                                sb.append(e.getMessage());
                                                Log.m211e(access$500, sb.toString());
                                            }
                                        }
                                    }
                                });
                            }
                            VectorSearchRoomFilesListFragment.this.hideLoadingBackProgress();
                        }
                    });
                }

                private void onError() {
                    VectorSearchRoomFilesListFragment.this.mIsBackPaginating = false;
                    VectorSearchRoomFilesListFragment.this.hideLoadingBackProgress();
                }

                public void onNetworkError(Exception exc) {
                    Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                    onError();
                }

                public void onMatrixError(MatrixError matrixError) {
                    Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), matrixError.getLocalizedMessage(), 1).show();
                    onError();
                }

                public void onUnexpectedError(Exception exc) {
                    Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                    onError();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void appendEvents(ArrayList<Event> arrayList, List<Event> list) {
        ArrayList arrayList2 = new ArrayList(list.size());
        for (Event event : list) {
            if (Event.EVENT_TYPE_MESSAGE.equals(event.getType())) {
                Message message = JsonUtils.toMessage(event.getContent());
                if (Message.MSGTYPE_FILE.equals(message.msgtype) || Message.MSGTYPE_IMAGE.equals(message.msgtype) || Message.MSGTYPE_VIDEO.equals(message.msgtype) || Message.MSGTYPE_AUDIO.equals(message.msgtype)) {
                    arrayList2.add(event);
                }
            }
        }
        arrayList.addAll(arrayList2);
    }

    /* access modifiers changed from: private */
    public void remoteRoomHistoryRequest(final ArrayList<Event> arrayList, final ApiCallback<ArrayList<Event>> apiCallback) {
        this.mRoom.requestServerRoomHistory(this.mNextBatch, 50, new ApiCallback<TokensChunkResponse<Event>>() {
            public void onSuccess(TokensChunkResponse<Event> tokensChunkResponse) {
                if (VectorSearchRoomFilesListFragment.this.mNextBatch != null && !TextUtils.equals(tokensChunkResponse.start, VectorSearchRoomFilesListFragment.this.mNextBatch)) {
                    return;
                }
                if (TextUtils.equals(tokensChunkResponse.start, tokensChunkResponse.end)) {
                    VectorSearchRoomFilesListFragment.this.mCanPaginateBack = false;
                    apiCallback.onSuccess(arrayList);
                    return;
                }
                if (VectorSearchRoomFilesListFragment.this.mRoom.isEncrypted()) {
                    for (T decryptEvent : tokensChunkResponse.chunk) {
                        VectorSearchRoomFilesListFragment.this.mSession.getDataHandler().decryptEvent(decryptEvent, VectorSearchRoomFilesListFragment.this.mTimeLineId);
                    }
                }
                VectorSearchRoomFilesListFragment.this.appendEvents(arrayList, tokensChunkResponse.chunk);
                VectorSearchRoomFilesListFragment.this.mNextBatch = tokensChunkResponse.end;
                if (arrayList.size() >= 10) {
                    apiCallback.onSuccess(arrayList);
                } else {
                    VectorSearchRoomFilesListFragment.this.remoteRoomHistoryRequest(arrayList, apiCallback);
                }
            }

            private void onError() {
                apiCallback.onSuccess(arrayList);
            }

            public void onNetworkError(Exception exc) {
                Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                onError();
            }

            public void onMatrixError(MatrixError matrixError) {
                Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), matrixError.getLocalizedMessage(), 1).show();
                onError();
            }

            public void onUnexpectedError(Exception exc) {
                Toast.makeText(VectorSearchRoomFilesListFragment.this.getActivity(), exc.getLocalizedMessage(), 1).show();
                onError();
            }
        });
    }
}
