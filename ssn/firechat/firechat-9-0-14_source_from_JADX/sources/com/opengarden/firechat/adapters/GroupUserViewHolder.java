package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupUser;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;

public class GroupUserViewHolder extends ViewHolder {
    private static final String LOG_TAG = "GroupUserViewHolder";
    @BindView(2131296394)
    ImageView vContactAvatar;
    @BindView(2131296398)
    TextView vContactName;

    public GroupUserViewHolder(View view) {
        super(view);
        ButterKnife.bind((Object) this, view);
    }

    public void populateViews(Context context, MXSession mXSession, GroupUser groupUser) {
        if (groupUser == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null groupUser");
        } else if (mXSession == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null session");
        } else if (mXSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null dataHandler");
        } else {
            this.vContactName.setText(groupUser.getDisplayname());
            VectorUtils.loadUserAvatar(context, mXSession, this.vContactAvatar, groupUser.avatarUrl, groupUser.userId, groupUser.getDisplayname());
        }
    }
}
