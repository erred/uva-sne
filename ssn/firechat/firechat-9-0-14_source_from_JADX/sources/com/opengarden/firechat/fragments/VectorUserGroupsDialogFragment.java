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
import com.opengarden.firechat.adapters.VectorGroupsListAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.List;

public class VectorUserGroupsDialogFragment extends DialogFragment {
    private static final String ARG_GROUPS_ID = "ARG_GROUPS_ID";
    private static final String ARG_SESSION_ID = "ARG_SESSION_ID";
    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String LOG_TAG = "VectorUserGroupsDialogFragment";
    private ArrayList<String> mGroupIds;
    private MXSession mSession;
    private String mUserId;

    public static VectorUserGroupsDialogFragment newInstance(String str, String str2, List<String> list) {
        VectorUserGroupsDialogFragment vectorUserGroupsDialogFragment = new VectorUserGroupsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SESSION_ID, str);
        bundle.putString(ARG_USER_ID, str2);
        bundle.putStringArrayList(ARG_GROUPS_ID, new ArrayList(list));
        vectorUserGroupsDialogFragment.setArguments(bundle);
        return vectorUserGroupsDialogFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSession = Matrix.getInstance(getContext()).getSession(getArguments().getString(ARG_SESSION_ID));
        this.mUserId = getArguments().getString(ARG_USER_ID);
        this.mGroupIds = getArguments().getStringArrayList(ARG_GROUPS_ID);
        if (this.mSession == null || TextUtils.isEmpty(this.mUserId)) {
            Log.m211e(LOG_TAG, "## onCreate() : invalid parameters");
            dismiss();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(C1299R.layout.fragment_dialog_groups_list, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(C1299R.C1301id.listView_groups);
        VectorGroupsListAdapter vectorGroupsListAdapter = new VectorGroupsListAdapter(getActivity(), C1299R.layout.adapter_item_group_view, this.mSession);
        vectorGroupsListAdapter.addAll(this.mGroupIds);
        listView.setAdapter(vectorGroupsListAdapter);
        return inflate;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        onCreateDialog.setTitle(getString(C1299R.string.groups_list));
        return onCreateDialog;
    }
}
