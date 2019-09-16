package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.util.VectorUtils;
import org.apache.commons.lang3.StringUtils;

public class VectorPublicRoomsAdapter extends ArrayAdapter<PublicRoom> {
    private final Context mContext;
    private final LayoutInflater mLayoutInflater = LayoutInflater.from(this.mContext);
    private final int mLayoutResourceId;
    private final MXSession mSession;

    public VectorPublicRoomsAdapter(Context context, int i, MXSession mXSession) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mSession = mXSession;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        String str;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
        }
        PublicRoom publicRoom = (PublicRoom) getItem(i);
        String publicRoomDisplayName = !TextUtils.isEmpty(publicRoom.name) ? publicRoom.name : VectorUtils.getPublicRoomDisplayName(publicRoom);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.room_avatar);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_roomName);
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_roomMessage);
        TextView textView3 = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_ts);
        View findViewById = view.findViewById(C1299R.C1301id.recents_separator);
        VectorUtils.loadUserAvatar(this.mContext, this.mSession, imageView, publicRoom.getAvatarUrl(), publicRoom.roomId, publicRoomDisplayName);
        textView2.setText(publicRoom.topic);
        textView.setText(publicRoomDisplayName);
        if (publicRoom.numJoinedMembers > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(publicRoom.numJoinedMembers);
            sb.append(StringUtils.SPACE);
            sb.append(this.mContext.getResources().getString(C1299R.string.users));
            str = sb.toString();
        } else {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(publicRoom.numJoinedMembers);
            sb2.append(StringUtils.SPACE);
            sb2.append(this.mContext.getResources().getString(C1299R.string.user));
            str = sb2.toString();
        }
        textView3.setText(str);
        findViewById.setVisibility(0);
        view.findViewById(C1299R.C1301id.bing_indicator_unread_message).setVisibility(4);
        view.findViewById(C1299R.C1301id.recents_groups_separator_line).setVisibility(8);
        view.findViewById(C1299R.C1301id.roomSummaryAdapter_action).setVisibility(8);
        view.findViewById(C1299R.C1301id.roomSummaryAdapter_action_image).setVisibility(8);
        view.findViewById(C1299R.C1301id.recents_groups_invitation_group).setVisibility(8);
        return view;
    }
}
