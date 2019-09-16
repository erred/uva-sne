package com.opengarden.firechat.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import butterknife.BindView;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;

public class RoomInvitationViewHolder extends RoomViewHolder {
    @BindView(2131296883)
    Button vPreViewButton;
    @BindView(2131296884)
    Button vRejectButton;

    RoomInvitationViewHolder(View view) {
        super(view);
    }

    /* access modifiers changed from: 0000 */
    public void populateViews(Context context, final MXSession mXSession, final Room room, final RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        super.populateViews(context, mXSession, room, room.isDirectChatInvitation(), true, moreRoomActionListener);
        this.vPreViewButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (roomInvitationListener != null) {
                    roomInvitationListener.onPreviewRoom(mXSession, room.getRoomId());
                }
            }
        });
        this.vRejectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (roomInvitationListener != null) {
                    roomInvitationListener.onRejectInvitation(mXSession, room.getRoomId());
                }
            }
        });
    }
}
