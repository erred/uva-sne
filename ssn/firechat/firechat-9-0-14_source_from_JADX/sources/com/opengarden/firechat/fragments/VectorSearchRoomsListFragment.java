package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorPublicRoomsActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.VectorRoomSummaryAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.view.RecentsExpandableListView;
import java.util.List;

public class VectorSearchRoomsListFragment extends VectorRecentsListFragment {
    /* access modifiers changed from: private */
    public MXSession mSession;

    /* access modifiers changed from: protected */
    public boolean isDragAndDropSupported() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void updateGroupExpandStatus(int i, boolean z) {
    }

    public static VectorSearchRoomsListFragment newInstance(String str, int i) {
        VectorSearchRoomsListFragment vectorSearchRoomsListFragment = new VectorSearchRoomsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("VectorRecentsListFragment.ARG_LAYOUT_ID", i);
        bundle.putString("VectorRecentsListFragment.ARG_MATRIX_ID", str);
        vectorSearchRoomsListFragment.setArguments(bundle);
        return vectorSearchRoomsListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        this.mMatrixId = arguments.getString("VectorRecentsListFragment.ARG_MATRIX_ID");
        this.mSession = Matrix.getInstance(getActivity()).getSession(this.mMatrixId);
        if (this.mSession == null) {
            throw new RuntimeException("Must have valid default MXSession.");
        }
        View inflate = layoutInflater.inflate(arguments.getInt("VectorRecentsListFragment.ARG_LAYOUT_ID"), viewGroup, false);
        this.mWaitingView = inflate.findViewById(C1299R.C1301id.listView_spinner_views);
        this.mRecentsListView = (RecentsExpandableListView) inflate.findViewById(C1299R.C1301id.fragment_recents_list);
        this.mRecentsListView.setGroupIndicator(null);
        VectorRoomSummaryAdapter vectorRoomSummaryAdapter = new VectorRoomSummaryAdapter(getActivity(), this.mSession, true, false, C1299R.layout.adapter_item_vector_recent_room, C1299R.layout.adapter_item_vector_recent_header, this, this);
        this.mAdapter = vectorRoomSummaryAdapter;
        this.mRecentsListView.setAdapter(this.mAdapter);
        this.mRecentsListView.setVisibility(0);
        this.mRecentsListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                String str = null;
                if (VectorSearchRoomsListFragment.this.mAdapter.isRoomByIdGroupPosition(i)) {
                    final String searchedPattern = VectorSearchRoomsListFragment.this.mAdapter.getSearchedPattern();
                    if (searchedPattern.startsWith("!")) {
                        VectorSearchRoomsListFragment.this.previewRoom(searchedPattern, null);
                    } else {
                        VectorSearchRoomsListFragment.this.showWaitingView();
                        VectorSearchRoomsListFragment.this.mSession.getDataHandler().roomIdByAlias(searchedPattern, new ApiCallback<String>() {
                            public void onSuccess(String str) {
                                VectorSearchRoomsListFragment.this.previewRoom(str, searchedPattern);
                            }

                            private void onError(String str) {
                                VectorSearchRoomsListFragment.this.hideWaitingView();
                                Toast.makeText(VectorSearchRoomsListFragment.this.getActivity(), str, 1).show();
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
                } else if (!VectorSearchRoomsListFragment.this.mAdapter.isDirectoryGroupPosition(i)) {
                    RoomSummary roomSummaryAt = VectorSearchRoomsListFragment.this.mAdapter.getRoomSummaryAt(i, i2);
                    MXSession session = Matrix.getInstance(VectorSearchRoomsListFragment.this.getActivity()).getSession(roomSummaryAt.getMatrixId());
                    String roomId = roomSummaryAt.getRoomId();
                    Room room = session.getDataHandler().getRoom(roomId);
                    if (room != null && !room.isLeaving()) {
                        str = roomId;
                    }
                    if (VectorSearchRoomsListFragment.this.mAdapter.resetUnreadCount(i, i2)) {
                        session.getDataHandler().getStore().flushSummary(roomSummaryAt);
                    }
                    if (str != null) {
                        Intent intent = new Intent(VectorSearchRoomsListFragment.this.getActivity(), VectorRoomActivity.class);
                        intent.putExtra("EXTRA_ROOM_ID", str);
                        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorSearchRoomsListFragment.this.mSession.getCredentials().userId);
                        VectorSearchRoomsListFragment.this.getActivity().startActivity(intent);
                    }
                } else if (TextUtils.isEmpty(VectorSearchRoomsListFragment.this.mAdapter.getSearchedPattern()) || VectorSearchRoomsListFragment.this.mAdapter.getMatchedPublicRoomsCount() > 0) {
                    Intent intent2 = new Intent(VectorSearchRoomsListFragment.this.getActivity(), VectorPublicRoomsActivity.class);
                    intent2.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorSearchRoomsListFragment.this.mSession.getMyUserId());
                    if (!TextUtils.isEmpty(VectorSearchRoomsListFragment.this.mAdapter.getSearchedPattern())) {
                        intent2.putExtra(VectorPublicRoomsActivity.EXTRA_SEARCHED_PATTERN, VectorSearchRoomsListFragment.this.mAdapter.getSearchedPattern());
                    }
                    VectorSearchRoomsListFragment.this.getActivity().startActivity(intent2);
                }
                return true;
            }
        });
        this.mRecentsListView.setOnGroupClickListener(new OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long j) {
                return true;
            }
        });
        return inflate;
    }

    /* access modifiers changed from: private */
    public void previewRoom(String str, String str2) {
        CommonActivityUtils.previewRoom((Activity) getActivity(), this.mSession, str, str2, (ApiCallback<Void>) new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                VectorSearchRoomsListFragment.this.hideWaitingView();
            }

            public void onNetworkError(Exception exc) {
                VectorSearchRoomsListFragment.this.hideWaitingView();
            }

            public void onMatrixError(MatrixError matrixError) {
                VectorSearchRoomsListFragment.this.hideWaitingView();
            }

            public void onUnexpectedError(Exception exc) {
                VectorSearchRoomsListFragment.this.hideWaitingView();
            }
        });
    }

    public void searchPattern(String str, final OnSearchResultListener onSearchResultListener) {
        if (this.mRecentsListView != null) {
            super.applyFilter(str);
            if (!TextUtils.isEmpty(this.mAdapter.getSearchedPattern())) {
                PublicRoomsManager.getInstance().startPublicRoomsSearch(null, null, false, this.mAdapter.getSearchedPattern(), new ApiCallback<List<PublicRoom>>() {
                    private void onDone(int i) {
                        VectorSearchRoomsListFragment.this.mAdapter.setMatchedPublicRoomsCount(Integer.valueOf(i));
                    }

                    public void onSuccess(List<PublicRoom> list) {
                        onDone(list.size());
                    }

                    public void onNetworkError(Exception exc) {
                        onDone(0);
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        onDone(0);
                    }

                    public void onUnexpectedError(Exception exc) {
                        onDone(0);
                    }
                });
            }
            this.mRecentsListView.post(new Runnable() {
                public void run() {
                    onSearchResultListener.onSearchSucceed(1);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void notifyDataSetChanged() {
        this.mAdapter.notifyDataSetChanged();
    }
}
