package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.VectorPublicRoomsAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.HashMap;
import java.util.List;

public class VectorPublicRoomsListFragment extends VectorBaseFragment {
    private static final String ARG_LAYOUT_ID = "VectorPublicRoomsListFragment.ARG_LAYOUT_ID";
    private static final String ARG_MATRIX_ID = "VectorPublicRoomsListFragment.ARG_MATRIX_ID";
    private static final String ARG_SEARCHED_PATTERN = "VectorPublicRoomsListFragment.ARG_SEARCHED_PATTERN";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorPublicRoomsListFragment";
    /* access modifiers changed from: private */
    public VectorPublicRoomsAdapter mAdapter;
    /* access modifiers changed from: private */
    public View mForwardPaginationView;
    /* access modifiers changed from: private */
    public View mInitializationSpinnerView;
    private String mPattern;
    /* access modifiers changed from: private */
    public ListView mRecentsListView;
    /* access modifiers changed from: private */
    public final OnScrollListener mScrollListener = new OnScrollListener() {
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1) {
                if (VectorPublicRoomsListFragment.this.mRecentsListView.getLastVisiblePosition() + 10 >= VectorPublicRoomsListFragment.this.mRecentsListView.getCount()) {
                    VectorPublicRoomsListFragment.this.forwardPaginate();
                }
            }
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            if (i + i2 + 10 >= i3) {
                VectorPublicRoomsListFragment.this.forwardPaginate();
            }
        }
    };
    /* access modifiers changed from: private */
    public MXSession mSession;

    public static VectorPublicRoomsListFragment newInstance(String str, int i, String str2) {
        VectorPublicRoomsListFragment vectorPublicRoomsListFragment = new VectorPublicRoomsListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_LAYOUT_ID, i);
        bundle.putString(ARG_MATRIX_ID, str);
        if (!TextUtils.isEmpty(str2)) {
            bundle.putString(ARG_SEARCHED_PATTERN, str2);
        }
        vectorPublicRoomsListFragment.setArguments(bundle);
        return vectorPublicRoomsListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        this.mSession = Matrix.getInstance(getActivity()).getSession(arguments.getString(ARG_MATRIX_ID));
        if (this.mSession == null) {
            throw new RuntimeException("Must have valid default MXSession.");
        }
        this.mPattern = arguments.getString(ARG_SEARCHED_PATTERN, null);
        View inflate = layoutInflater.inflate(arguments.getInt(ARG_LAYOUT_ID), viewGroup, false);
        this.mRecentsListView = (ListView) inflate.findViewById(C1299R.C1301id.fragment_public_rooms_list);
        this.mInitializationSpinnerView = inflate.findViewById(C1299R.C1301id.listView_global_spinner_views);
        this.mForwardPaginationView = inflate.findViewById(C1299R.C1301id.listView_forward_spinner_view);
        this.mAdapter = new VectorPublicRoomsAdapter(getActivity(), C1299R.layout.adapter_item_vector_recent_room, this.mSession);
        this.mRecentsListView.setAdapter(this.mAdapter);
        this.mRecentsListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                final PublicRoom publicRoom = (PublicRoom) VectorPublicRoomsListFragment.this.mAdapter.getItem(i);
                if (publicRoom.roomId != null) {
                    final RoomPreviewData roomPreviewData = new RoomPreviewData(VectorPublicRoomsListFragment.this.mSession, publicRoom.roomId, null, publicRoom.getAlias(), null);
                    Room room = VectorPublicRoomsListFragment.this.mSession.getDataHandler().getRoom(publicRoom.roomId, false);
                    if (room == null) {
                        VectorPublicRoomsListFragment.this.mInitializationSpinnerView.setVisibility(0);
                        roomPreviewData.fetchPreviewData(new ApiCallback<Void>() {
                            private void onDone() {
                                VectorPublicRoomsListFragment.this.mInitializationSpinnerView.setVisibility(8);
                                CommonActivityUtils.previewRoom(VectorPublicRoomsListFragment.this.getActivity(), roomPreviewData);
                            }

                            public void onSuccess(Void voidR) {
                                onDone();
                            }

                            private void onError() {
                                roomPreviewData.setRoomState(publicRoom);
                                roomPreviewData.setRoomName(publicRoom.name);
                                onDone();
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
                    } else if (room.isInvited()) {
                        String access$400 = VectorPublicRoomsListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("manageRoom : the user is invited -> display the preview ");
                        sb.append(VectorApp.getCurrentActivity());
                        Log.m209d(access$400, sb.toString());
                        CommonActivityUtils.previewRoom(VectorPublicRoomsListFragment.this.getActivity(), roomPreviewData);
                    } else {
                        Log.m209d(VectorPublicRoomsListFragment.LOG_TAG, "manageRoom : open the room");
                        HashMap hashMap = new HashMap();
                        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorPublicRoomsListFragment.this.mSession.getMyUserId());
                        hashMap.put("EXTRA_ROOM_ID", publicRoom.roomId);
                        if (!TextUtils.isEmpty(publicRoom.name)) {
                            hashMap.put(VectorRoomActivity.EXTRA_DEFAULT_NAME, publicRoom.name);
                        }
                        if (!TextUtils.isEmpty(publicRoom.topic)) {
                            hashMap.put(VectorRoomActivity.EXTRA_DEFAULT_TOPIC, publicRoom.topic);
                        }
                        CommonActivityUtils.goToRoomPage(VectorPublicRoomsListFragment.this.getActivity(), VectorPublicRoomsListFragment.this.mSession, hashMap);
                    }
                }
            }
        });
        return inflate;
    }

    public void onResume() {
        super.onResume();
        if (this.mAdapter.getCount() == 0) {
            this.mInitializationSpinnerView.setVisibility(0);
            PublicRoomsManager.getInstance().startPublicRoomsSearch(null, null, false, this.mPattern, new ApiCallback<List<PublicRoom>>() {
                public void onSuccess(List<PublicRoom> list) {
                    if (VectorPublicRoomsListFragment.this.getActivity() != null) {
                        VectorPublicRoomsListFragment.this.mAdapter.addAll(list);
                        VectorPublicRoomsListFragment.this.mRecentsListView.setOnScrollListener(VectorPublicRoomsListFragment.this.mScrollListener);
                        VectorPublicRoomsListFragment.this.mInitializationSpinnerView.setVisibility(8);
                    }
                }

                private void onError(String str) {
                    if (VectorPublicRoomsListFragment.this.getActivity() != null) {
                        String access$400 = VectorPublicRoomsListFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## startPublicRoomsSearch() failed ");
                        sb.append(str);
                        Log.m211e(access$400, sb.toString());
                        Toast.makeText(VectorPublicRoomsListFragment.this.getActivity(), str, 0).show();
                        VectorPublicRoomsListFragment.this.mInitializationSpinnerView.setVisibility(8);
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

    /* access modifiers changed from: private */
    public void forwardPaginate() {
        if (PublicRoomsManager.getInstance().forwardPaginate(new ApiCallback<List<PublicRoom>>() {
            public void onSuccess(List<PublicRoom> list) {
                if (VectorPublicRoomsListFragment.this.getActivity() != null) {
                    VectorPublicRoomsListFragment.this.mForwardPaginationView.setVisibility(8);
                    VectorPublicRoomsListFragment.this.mAdapter.addAll(list);
                    if (!PublicRoomsManager.getInstance().hasMoreResults()) {
                        VectorPublicRoomsListFragment.this.mRecentsListView.setOnScrollListener(null);
                    }
                }
            }

            private void onError(String str) {
                if (VectorPublicRoomsListFragment.this.getActivity() != null) {
                    String access$400 = VectorPublicRoomsListFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## forwardPaginate() failed ");
                    sb.append(str);
                    Log.m211e(access$400, sb.toString());
                    Toast.makeText(VectorPublicRoomsListFragment.this.getActivity(), str, 0).show();
                    VectorPublicRoomsListFragment.this.mForwardPaginationView.setVisibility(8);
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
        })) {
            this.mForwardPaginationView.setVisibility(0);
        }
    }
}
