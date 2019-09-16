package com.opengarden.firechat.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import butterknife.BindView;
import com.opengarden.firechat.adapters.AbsAdapter.GroupInvitationListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreGroupActionListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;

public class GroupInvitationViewHolder extends GroupViewHolder {
    @BindView(2131296534)
    Button vJoinButton;
    @BindView(2131296535)
    Button vRejectButton;

    GroupInvitationViewHolder(View view) {
        super(view);
    }

    public void populateViews(Context context, final MXSession mXSession, final Group group, final GroupInvitationListener groupInvitationListener, boolean z, MoreGroupActionListener moreGroupActionListener) {
        super.populateViews(context, mXSession, group, groupInvitationListener, true, moreGroupActionListener);
        this.vJoinButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (groupInvitationListener != null) {
                    groupInvitationListener.onJoinGroup(mXSession, group.getGroupId());
                }
            }
        });
        this.vRejectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (groupInvitationListener != null) {
                    groupInvitationListener.onRejectInvitation(mXSession, group.getGroupId());
                }
            }
        });
    }
}
