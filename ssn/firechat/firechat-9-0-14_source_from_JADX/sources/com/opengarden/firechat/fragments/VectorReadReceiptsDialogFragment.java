package com.opengarden.firechat.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.VectorReadReceiptsAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;

public class VectorReadReceiptsDialogFragment extends DialogFragment {
    private static final String ARG_EVENT_ID = "VectorReadReceiptsDialogFragment.ARG_EVENT_ID";
    private static final String ARG_ROOM_ID = "VectorReadReceiptsDialogFragment.ARG_ROOM_ID";
    private static final String ARG_SESSION_ID = "VectorReadReceiptsDialogFragment.ARG_SESSION_ID";
    private static final String LOG_TAG = VectorPublicRoomsListFragment.class.getSimpleName();
    private String mEventId;
    private String mRoomId;
    private MXSession mSession;

    public static VectorReadReceiptsDialogFragment newInstance(String str, String str2, String str3) {
        VectorReadReceiptsDialogFragment vectorReadReceiptsDialogFragment = new VectorReadReceiptsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SESSION_ID, str);
        bundle.putString(ARG_ROOM_ID, str2);
        bundle.putString(ARG_EVENT_ID, str3);
        vectorReadReceiptsDialogFragment.setArguments(bundle);
        return vectorReadReceiptsDialogFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSession = Matrix.getInstance(getContext()).getSession(getArguments().getString(ARG_SESSION_ID));
        this.mRoomId = getArguments().getString(ARG_ROOM_ID);
        this.mEventId = getArguments().getString(ARG_EVENT_ID);
        if (this.mSession == null || TextUtils.isEmpty(this.mRoomId) || TextUtils.isEmpty(this.mEventId)) {
            Log.m211e(LOG_TAG, "## onCreate() : invalid parameters");
            dismiss();
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        onCreateDialog.setTitle(getString(C1299R.string.read_receipts_list));
        return onCreateDialog;
    }

    private MXMediasCache getMXMediasCache() {
        return Matrix.getInstance(getActivity()).getMediasCache();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(C1299R.layout.fragment_dialog_member_list, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(C1299R.C1301id.listView_members);
        VectorReadReceiptsAdapter vectorReadReceiptsAdapter = new VectorReadReceiptsAdapter(getActivity(), C1299R.layout.adapter_item_read_receipt, this.mSession, this.mSession.getDataHandler().getRoom(this.mRoomId), getMXMediasCache());
        vectorReadReceiptsAdapter.addAll(new ArrayList(this.mSession.getDataHandler().getStore().getEventReceipts(this.mRoomId, this.mEventId, true, true)));
        listView.setAdapter(vectorReadReceiptsAdapter);
        return inflate;
    }
}
