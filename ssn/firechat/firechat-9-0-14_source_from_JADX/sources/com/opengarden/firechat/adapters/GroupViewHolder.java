package com.opengarden.firechat.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.GroupInvitationListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreGroupActionListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;

public class GroupViewHolder extends ViewHolder {
    private static final String LOG_TAG = "GroupViewHolder";
    @BindView(2131296929)
    ImageView vGroupAvatar;
    @BindView(2131296537)
    TextView vGroupMembersCount;
    @Nullable
    @BindView(2131296541)
    View vGroupMoreActionAnchor;
    @Nullable
    @BindView(2131296542)
    View vGroupMoreActionClickArea;
    @BindView(2131296544)
    TextView vGroupName;
    @Nullable
    @BindView(2131296550)
    TextView vGroupTopic;

    public GroupViewHolder(View view) {
        super(view);
        ButterKnife.bind((Object) this, view);
    }

    public void populateViews(Context context, MXSession mXSession, final Group group, GroupInvitationListener groupInvitationListener, boolean z, final MoreGroupActionListener moreGroupActionListener) {
        if (group == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null group");
            return;
        }
        if (z) {
            this.vGroupMembersCount.setText("!");
            this.vGroupMembersCount.setTypeface(null, 1);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(100.0f);
            gradientDrawable.setColor(ContextCompat.getColor(context, C1299R.color.vector_fuchsia_color));
            this.vGroupMembersCount.setBackground(gradientDrawable);
            this.vGroupMembersCount.setVisibility(0);
        } else {
            this.vGroupMembersCount.setVisibility(8);
        }
        this.vGroupName.setText(group.getDisplayName());
        this.vGroupName.setTypeface(null, 0);
        VectorUtils.loadGroupAvatar(context, mXSession, this.vGroupAvatar, group);
        this.vGroupTopic.setText(group.getShortDescription());
        if (!(this.vGroupMoreActionClickArea == null || this.vGroupMoreActionAnchor == null)) {
            this.vGroupMoreActionClickArea.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (moreGroupActionListener != null) {
                        moreGroupActionListener.onMoreActionClick(GroupViewHolder.this.vGroupMoreActionAnchor, group);
                    }
                }
            });
        }
    }
}
