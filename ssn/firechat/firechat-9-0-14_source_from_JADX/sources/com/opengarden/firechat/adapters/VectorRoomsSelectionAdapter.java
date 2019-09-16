package com.opengarden.firechat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import org.apache.commons.lang3.StringUtils;

public class VectorRoomsSelectionAdapter extends ArrayAdapter<RoomSummary> {
    private static final String LOG_TAG = "VectorRoomsSelectionAdapter";
    private final Context mContext;
    private final LayoutInflater mLayoutInflater = LayoutInflater.from(this.mContext);
    private final int mLayoutResourceId;
    private final MXSession mSession;

    public VectorRoomsSelectionAdapter(Context context, int i, MXSession mXSession) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mSession = mXSession;
    }

    private String getFormattedTimestamp(Event event) {
        String tsToString = AdapterUtils.tsToString(this.mContext, event.getOriginServerTs(), false);
        StringBuilder sb = new StringBuilder();
        sb.append(this.mContext.getString(C1299R.string.today));
        sb.append(StringUtils.SPACE);
        String sb2 = sb.toString();
        return tsToString.startsWith(sb2) ? tsToString.substring(sb2.length()) : tsToString;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
        }
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "getView : the session is not anymore valid");
            return view;
        }
        RoomSummary roomSummary = (RoomSummary) getItem(i);
        String roomName = roomSummary.getRoomName();
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.room_avatar);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_roomName);
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_roomMessage);
        TextView textView3 = (TextView) view.findViewById(C1299R.C1301id.roomSummaryAdapter_ts);
        View findViewById = view.findViewById(C1299R.C1301id.recents_separator);
        Room room = this.mSession.getDataHandler().getRoom(roomSummary.getRoomId());
        if (room != null) {
            VectorUtils.loadRoomAvatar(this.mContext, this.mSession, imageView, room);
        }
        if (roomSummary.getLatestReceivedEvent() != null) {
            RiotEventDisplay riotEventDisplay = new RiotEventDisplay(this.mContext, roomSummary.getLatestReceivedEvent(), roomSummary.getLatestRoomState());
            riotEventDisplay.setPrependMessagesWithAuthor(true);
            textView2.setText(riotEventDisplay.getTextualDisplay(Integer.valueOf(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.riot_primary_text_color))));
            textView3.setText(getFormattedTimestamp(roomSummary.getLatestReceivedEvent()));
            textView3.setTextColor(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.default_text_light_color));
            textView3.setTypeface(null, 0);
            textView3.setVisibility(0);
        } else {
            textView2.setText("");
            textView3.setVisibility(8);
        }
        textView.setText(roomName);
        findViewById.setVisibility(0);
        view.findViewById(C1299R.C1301id.bing_indicator_unread_message).setVisibility(4);
        view.findViewById(C1299R.C1301id.recents_groups_separator_line).setVisibility(8);
        view.findViewById(C1299R.C1301id.roomSummaryAdapter_action).setVisibility(8);
        view.findViewById(C1299R.C1301id.roomSummaryAdapter_action_image).setVisibility(8);
        view.findViewById(C1299R.C1301id.recents_groups_invitation_group).setVisibility(8);
        return view;
    }
}
