package com.opengarden.firechat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.p000v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.opengarden.firechat.activity.VectorMediasViewerActivity;
import com.opengarden.firechat.adapters.VectorMessagesAdapter;
import com.opengarden.firechat.adapters.VectorSearchFilesListAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import java.util.ArrayList;

public class VectorSearchRoomsFilesListFragment extends VectorSearchMessagesListFragment {
    public static VectorSearchRoomsFilesListFragment newInstance(String str, String str2, int i) {
        VectorSearchRoomsFilesListFragment vectorSearchRoomsFilesListFragment = new VectorSearchRoomsFilesListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MatrixMessageListFragment.ARG_LAYOUT_ID, i);
        bundle.putString(MatrixMessageListFragment.ARG_MATRIX_ID, str);
        if (str2 != null) {
            bundle.putString(MatrixMessageListFragment.ARG_ROOM_ID, str2);
        }
        vectorSearchRoomsFilesListFragment.setArguments(bundle);
        return vectorSearchRoomsFilesListFragment;
    }

    public AbstractMessagesAdapter createMessagesAdapter() {
        boolean z = true;
        this.mIsMediaSearch = true;
        MXSession mXSession = this.mSession;
        FragmentActivity activity = getActivity();
        if (this.mRoomId != null) {
            z = false;
        }
        return new VectorSearchFilesListAdapter(mXSession, activity, z, getMXMediasCache());
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mMessageListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Event event = ((MessageRow) VectorSearchRoomsFilesListFragment.this.mAdapter.getItem(i)).getEvent();
                VectorMessagesAdapter vectorMessagesAdapter = (VectorMessagesAdapter) VectorSearchRoomsFilesListFragment.this.mAdapter;
                if (vectorMessagesAdapter.isInSelectionMode()) {
                    vectorMessagesAdapter.onEventTap(null);
                    return;
                }
                Message message = JsonUtils.toMessage(event.getContent());
                if (Message.MSGTYPE_IMAGE.equals(message.msgtype) || Message.MSGTYPE_VIDEO.equals(message.msgtype)) {
                    ArrayList listSlidableMessages = VectorSearchRoomsFilesListFragment.this.listSlidableMessages();
                    int mediaMessagePosition = VectorSearchRoomsFilesListFragment.this.getMediaMessagePosition(listSlidableMessages, message);
                    if (mediaMessagePosition >= 0) {
                        Intent intent = new Intent(VectorSearchRoomsFilesListFragment.this.getActivity(), VectorMediasViewerActivity.class);
                        intent.putExtra(VectorMediasViewerActivity.EXTRA_MATRIX_ID, VectorSearchRoomsFilesListFragment.this.mSession.getCredentials().userId);
                        intent.putExtra(VectorMediasViewerActivity.KEY_THUMBNAIL_WIDTH, VectorSearchRoomsFilesListFragment.this.mAdapter.getMaxThumbnailWidth());
                        intent.putExtra(VectorMediasViewerActivity.KEY_THUMBNAIL_HEIGHT, VectorSearchRoomsFilesListFragment.this.mAdapter.getMaxThumbnailHeight());
                        intent.putExtra(VectorMediasViewerActivity.KEY_INFO_LIST, listSlidableMessages);
                        intent.putExtra(VectorMediasViewerActivity.KEY_INFO_LIST_INDEX, mediaMessagePosition);
                        VectorSearchRoomsFilesListFragment.this.getActivity().startActivity(intent);
                    }
                } else if (Message.MSGTYPE_FILE.equals(message.msgtype)) {
                    FileMessage fileMessage = JsonUtils.toFileMessage(event.getContent());
                    if (fileMessage.getUrl() != null) {
                        VectorSearchRoomsFilesListFragment.this.onMediaAction(123456, fileMessage.getUrl(), fileMessage.getMimeType(), fileMessage.body, fileMessage.file);
                    }
                }
            }
        });
        return onCreateView;
    }
}
