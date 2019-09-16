package com.opengarden.firechat.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.VectorGroupDetailsActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupProfile;
import com.opengarden.firechat.util.VectorUtils;
import java.util.HashMap;
import java.util.Map;

public class VectorGroupsListAdapter extends ArrayAdapter<String> {
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public Map<String, Group> mGroupByGroupId = new HashMap();
    private final LayoutInflater mLayoutInflater;
    private final int mLayoutResourceId;
    /* access modifiers changed from: private */
    public final MXSession mSession;

    public VectorGroupsListAdapter(Context context, int i, MXSession mXSession) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        this.mSession = mXSession;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
        }
        final String str = (String) getItem(i);
        Group group = (Group) this.mGroupByGroupId.get(str);
        if (group == null) {
            group = this.mSession.getGroupsManager().getGroup(str);
            if (group != null) {
                this.mGroupByGroupId.put(str, group);
            }
        }
        boolean z = group == null;
        if (group == null) {
            group = new Group(str);
        }
        view.findViewById(C1299R.C1301id.group_members_count).setVisibility(8);
        final TextView textView = (TextView) view.findViewById(C1299R.C1301id.group_name);
        textView.setTag(str);
        textView.setText(group.getDisplayName());
        textView.setTypeface(null, 0);
        final ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.room_avatar);
        VectorUtils.loadGroupAvatar(this.mContext, this.mSession, imageView, group);
        final TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.group_topic);
        textView2.setText(group.getShortDescription());
        view.findViewById(C1299R.C1301id.group_more_action_click_area).setVisibility(4);
        view.findViewById(C1299R.C1301id.group_more_action_anchor).setVisibility(4);
        view.findViewById(C1299R.C1301id.group_more_action_ic).setVisibility(4);
        if (z) {
            GroupsManager groupsManager = this.mSession.getGroupsManager();
            final String str2 = str;
            C17491 r1 = new ApiCallback<GroupProfile>() {
                public void onMatrixError(MatrixError matrixError) {
                }

                public void onNetworkError(Exception exc) {
                }

                public void onUnexpectedError(Exception exc) {
                }

                public void onSuccess(GroupProfile groupProfile) {
                    if (TextUtils.equals((String) textView.getTag(), str2)) {
                        Group group = (Group) VectorGroupsListAdapter.this.mGroupByGroupId.get(str2);
                        if (group == null) {
                            group = new Group(str2);
                            group.setGroupProfile(groupProfile);
                            VectorGroupsListAdapter.this.mGroupByGroupId.put(str2, group);
                        }
                        textView.setText(group.getDisplayName());
                        VectorUtils.loadGroupAvatar(VectorGroupsListAdapter.this.mContext, VectorGroupsListAdapter.this.mSession, imageView, group);
                        textView2.setText(group.getShortDescription());
                    }
                }
            };
            groupsManager.getGroupProfile(str, r1);
        }
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(VectorGroupsListAdapter.this.mContext, VectorGroupDetailsActivity.class);
                intent.putExtra(VectorGroupDetailsActivity.EXTRA_GROUP_ID, str);
                intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorGroupsListAdapter.this.mSession.getCredentials().userId);
                VectorGroupsListAdapter.this.mContext.startActivity(intent);
            }
        });
        view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                ((ClipboardManager) VectorGroupsListAdapter.this.mContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("", str));
                Toast.makeText(VectorGroupsListAdapter.this.mContext, VectorGroupsListAdapter.this.mContext.getResources().getString(C1299R.string.copied_to_clipboard), 0).show();
                return true;
            }
        });
        return view;
    }
}
