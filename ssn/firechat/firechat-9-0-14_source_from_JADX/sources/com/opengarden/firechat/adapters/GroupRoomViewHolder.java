package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;

public class GroupRoomViewHolder extends ViewHolder {
    private static final String LOG_TAG = "GroupRoomViewHolder";
    @BindView(2131296394)
    ImageView vContactAvatar;
    @Nullable
    @BindView(2131296397)
    TextView vContactDesc;
    @BindView(2131296398)
    TextView vContactName;

    public GroupRoomViewHolder(View view) {
        super(view);
        ButterKnife.bind((Object) this, view);
    }

    public void populateViews(Context context, MXSession mXSession, GroupRoom groupRoom) {
        if (groupRoom == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null groupRoom");
        } else if (mXSession == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null session");
        } else if (mXSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null dataHandler");
        } else {
            this.vContactName.setText(groupRoom.getDisplayName());
            VectorUtils.loadUserAvatar(context, mXSession, this.vContactAvatar, groupRoom.avatar_url, groupRoom.roomId, groupRoom.getDisplayName());
            if (this.vContactDesc != null) {
                this.vContactDesc.setText(groupRoom.topic);
            }
        }
    }
}
