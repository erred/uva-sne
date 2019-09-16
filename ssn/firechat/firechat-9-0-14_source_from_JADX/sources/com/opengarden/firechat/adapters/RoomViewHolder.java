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
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.VectorUtils;

public class RoomViewHolder extends ViewHolder {
    private static final String LOG_TAG = "RoomViewHolder";
    @Nullable
    @BindView(2131296450)
    View mDirectChatIndicator;
    @BindView(2131296929)
    ImageView vRoomAvatar;
    @BindView(2131296931)
    View vRoomEncryptedIcon;
    @Nullable
    @BindView(2131296953)
    TextView vRoomLastMessage;
    @Nullable
    @BindView(2131296954)
    View vRoomMoreActionAnchor;
    @Nullable
    @BindView(2131296955)
    View vRoomMoreActionClickArea;
    @BindView(2131296957)
    TextView vRoomName;
    @Nullable
    @BindView(2131296958)
    TextView vRoomNameServer;
    @Nullable
    @BindView(2131296976)
    TextView vRoomTimestamp;
    @BindView(2131296975)
    TextView vRoomUnreadCount;
    @Nullable
    @BindView(2131296626)
    View vRoomUnreadIndicator;

    public RoomViewHolder(View view) {
        super(view);
        ButterKnife.bind((Object) this, view);
    }

    public void populateViews(Context context, MXSession mXSession, final Room room, boolean z, boolean z2, final MoreRoomActionListener moreRoomActionListener) {
        if (room == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null room");
        } else if (mXSession == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null session");
        } else if (mXSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## populateViews() : null dataHandler");
        } else {
            IMXStore store = mXSession.getDataHandler().getStore(room.getRoomId());
            if (store == null) {
                Log.m211e(LOG_TAG, "## populateViews() : null Store");
                return;
            }
            RoomSummary summary = store.getSummary(room.getRoomId());
            if (summary == null) {
                Log.m211e(LOG_TAG, "## populateViews() : null roomSummary");
                return;
            }
            int unreadEventsCount = summary.getUnreadEventsCount();
            int color = ContextCompat.getColor(context, C1299R.color.vector_fuchsia_color);
            int color2 = ContextCompat.getColor(context, C1299R.color.vector_green_color);
            int color3 = ContextCompat.getColor(context, C1299R.color.vector_silver_color);
            int highlightCount = summary.getHighlightCount();
            int notificationCount = summary.getNotificationCount();
            if (room.getDataHandler() != null && room.getDataHandler().getBingRulesManager().isRoomMentionOnly(room.getRoomId())) {
                notificationCount = highlightCount;
            }
            int i = 0;
            if (!z2 && highlightCount == 0) {
                color = notificationCount != 0 ? color2 : unreadEventsCount != 0 ? color3 : 0;
            }
            int i2 = 1;
            if (z2 || notificationCount > 0) {
                this.vRoomUnreadCount.setText(z2 ? "!" : RoomUtils.formatUnreadMessagesCounter(notificationCount));
                this.vRoomUnreadCount.setTypeface(null, 1);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(0);
                gradientDrawable.setCornerRadius(100.0f);
                gradientDrawable.setColor(color);
                this.vRoomUnreadCount.setBackground(gradientDrawable);
                this.vRoomUnreadCount.setVisibility(0);
            } else {
                this.vRoomUnreadCount.setVisibility(8);
            }
            String roomDisplayName = VectorUtils.getRoomDisplayName(context, mXSession, room);
            if (this.vRoomNameServer == null) {
                this.vRoomName.setText(roomDisplayName);
            } else if (MXSession.isRoomAlias(roomDisplayName)) {
                String[] split = roomDisplayName.split(":");
                StringBuilder sb = new StringBuilder();
                sb.append(split[0]);
                sb.append(":");
                String sb2 = sb.toString();
                String str = split[1];
                this.vRoomName.setLines(1);
                this.vRoomName.setText(sb2);
                this.vRoomNameServer.setText(str);
                this.vRoomNameServer.setVisibility(0);
                this.vRoomNameServer.setTypeface(null, unreadEventsCount != 0 ? 1 : 0);
            } else {
                this.vRoomName.setLines(2);
                this.vRoomNameServer.setVisibility(8);
                this.vRoomName.setText(roomDisplayName);
            }
            TextView textView = this.vRoomName;
            if (unreadEventsCount == 0) {
                i2 = 0;
            }
            textView.setTypeface(null, i2);
            VectorUtils.loadRoomAvatar(context, mXSession, this.vRoomAvatar, room);
            if (this.vRoomLastMessage != null) {
                this.vRoomLastMessage.setText(RoomUtils.getRoomMessageToDisplay(context, mXSession, summary));
            }
            if (this.mDirectChatIndicator != null) {
                this.mDirectChatIndicator.setVisibility(z ? 0 : 4);
            }
            this.vRoomEncryptedIcon.setVisibility(room.isEncrypted() ? 0 : 4);
            if (this.vRoomUnreadIndicator != null) {
                this.vRoomUnreadIndicator.setBackgroundColor(color);
                View view = this.vRoomUnreadIndicator;
                if (summary.isInvited()) {
                    i = 4;
                }
                view.setVisibility(i);
            }
            if (this.vRoomTimestamp != null) {
                this.vRoomTimestamp.setText(RoomUtils.getRoomTimestamp(context, summary.getLatestReceivedEvent()));
            }
            if (!(this.vRoomMoreActionClickArea == null || this.vRoomMoreActionAnchor == null)) {
                this.vRoomMoreActionClickArea.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (moreRoomActionListener != null) {
                            moreRoomActionListener.onMoreActionClick(RoomViewHolder.this.vRoomMoreActionAnchor, room);
                        }
                    }
                });
            }
        }
    }
}
