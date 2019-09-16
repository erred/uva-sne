package com.opengarden.firechat.fragments;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p000v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.util.VectorImageGetter;
import com.opengarden.firechat.util.VectorImageGetter.OnImageDownloadListener;
import com.opengarden.firechat.util.VectorUtils;

public class GroupDetailsHomeFragment extends GroupDetailsBaseFragment {
    private static final String LOG_TAG = "GroupDetailsHomeFragment";
    @BindView(2131296531)
    ImageView mGroupAvatar;
    @BindView(2131296573)
    TextView mGroupHtmlTextView;
    @BindView(2131296538)
    ImageView mGroupMembersIconView;
    @BindView(2131296540)
    TextView mGroupMembersTextView;
    @BindView(2131296545)
    TextView mGroupNameTextView;
    @BindView(2131296546)
    ImageView mGroupRoomsIconView;
    @BindView(2131296547)
    TextView mGroupRoomsTextView;
    @BindView(2131296551)
    TextView mGroupTopicTextView;
    private VectorImageGetter mImageGetter;
    @BindView(2131296830)
    TextView noLongDescriptionTextView;

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (this.mImageGetter == null) {
            this.mImageGetter = new VectorImageGetter(this.mSession);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_group_details_home, viewGroup, false);
    }

    public void onPause() {
        super.onPause();
        this.mImageGetter.setListener(null);
    }

    public void onResume() {
        super.onResume();
        refreshViews();
        this.mImageGetter.setListener(new OnImageDownloadListener() {
            public void onImageDownloaded(String str) {
                GroupDetailsHomeFragment.this.refreshLongDescription();
            }
        });
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        this.mGroupMembersIconView.setImageDrawable(CommonActivityUtils.tintDrawableWithColor(ContextCompat.getDrawable(this.mActivity, C1299R.C1300drawable.riot_tab_groups), this.mGroupMembersTextView.getCurrentTextColor()));
        this.mGroupRoomsIconView.setImageDrawable(CommonActivityUtils.tintDrawableWithColor(ContextCompat.getDrawable(this.mActivity, C1299R.C1300drawable.riot_tab_rooms), this.mGroupMembersTextView.getCurrentTextColor()));
    }

    public void refreshViews() {
        Group group = this.mActivity.getGroup();
        VectorUtils.loadGroupAvatar(this.mActivity, this.mSession, this.mGroupAvatar, group);
        this.mGroupNameTextView.setText(group.getDisplayName());
        this.mGroupTopicTextView.setText(group.getShortDescription());
        this.mGroupTopicTextView.setVisibility(TextUtils.isEmpty(this.mGroupTopicTextView.getText()) ? 8 : 0);
        int estimatedRoomCount = group.getGroupRooms() != null ? group.getGroupRooms().getEstimatedRoomCount() : 0;
        int estimatedUsersCount = group.getGroupUsers() != null ? group.getGroupUsers().getEstimatedUsersCount() : 1;
        this.mGroupRoomsTextView.setText(1 == estimatedRoomCount ? getString(C1299R.string.group_one_room) : getString(C1299R.string.group_rooms, Integer.valueOf(estimatedRoomCount)));
        this.mGroupMembersTextView.setText(1 == estimatedUsersCount ? getString(C1299R.string.group_one_member) : getString(C1299R.string.group_members, Integer.valueOf(estimatedUsersCount)));
        if (!TextUtils.isEmpty(group.getLongDescription())) {
            this.mGroupHtmlTextView.setVisibility(0);
            refreshLongDescription();
            this.noLongDescriptionTextView.setVisibility(8);
            return;
        }
        this.noLongDescriptionTextView.setVisibility(0);
        this.mGroupHtmlTextView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void refreshLongDescription() {
        if (this.mGroupHtmlTextView != null) {
            Group group = this.mActivity.getGroup();
            if (VERSION.SDK_INT >= 24) {
                this.mGroupHtmlTextView.setText(Html.fromHtml(group.getLongDescription(), 0, this.mImageGetter, null));
            } else {
                this.mGroupHtmlTextView.setText(Html.fromHtml(group.getLongDescription(), this.mImageGetter, null));
            }
        }
    }
}
