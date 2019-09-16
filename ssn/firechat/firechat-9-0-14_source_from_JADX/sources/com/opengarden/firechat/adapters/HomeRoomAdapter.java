package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.util.RoomUtils;
import java.util.ArrayList;
import java.util.List;

public class HomeRoomAdapter extends AbsFilterableAdapter<RoomViewHolder> {
    /* access modifiers changed from: private */
    public final List<Room> mFilteredRooms = new ArrayList();
    private final int mLayoutRes;
    /* access modifiers changed from: private */
    public final OnSelectRoomListener mListener;
    private final MoreRoomActionListener mMoreActionListener;
    private final List<Room> mRooms = new ArrayList();

    public interface OnSelectRoomListener {
        void onLongClickRoom(View view, Room room, int i);

        void onSelectRoom(Room room, int i);
    }

    public HomeRoomAdapter(Context context, @LayoutRes int i, OnSelectRoomListener onSelectRoomListener, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        super(context, roomInvitationListener, moreRoomActionListener);
        this.mLayoutRes = i;
        this.mListener = onSelectRoomListener;
        this.mMoreActionListener = moreRoomActionListener;
    }

    public RoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mLayoutRes, viewGroup, false);
        return this.mLayoutRes == C1299R.layout.adapter_item_room_invite ? new RoomInvitationViewHolder(inflate) : new RoomViewHolder(inflate);
    }

    public void onBindViewHolder(final RoomViewHolder roomViewHolder, int i) {
        if (i < this.mFilteredRooms.size()) {
            final Room room = (Room) this.mFilteredRooms.get(i);
            if (this.mLayoutRes == C1299R.layout.adapter_item_room_invite) {
                ((RoomInvitationViewHolder) roomViewHolder).populateViews(this.mContext, this.mSession, room, this.mRoomInvitationListener, this.mMoreActionListener);
                return;
            }
            roomViewHolder.populateViews(this.mContext, this.mSession, room, room.isDirect(), false, this.mMoreActionListener);
            roomViewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    HomeRoomAdapter.this.mListener.onSelectRoom(room, roomViewHolder.getAdapterPosition());
                }
            });
            roomViewHolder.itemView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    HomeRoomAdapter.this.mListener.onLongClickRoom(view, room, roomViewHolder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public int getItemCount() {
        return this.mFilteredRooms.size();
    }

    /* access modifiers changed from: protected */
    public Filter createFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                HomeRoomAdapter.this.filterRooms(charSequence);
                filterResults.values = HomeRoomAdapter.this.mFilteredRooms;
                filterResults.count = HomeRoomAdapter.this.mFilteredRooms.size();
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                HomeRoomAdapter.this.onFilterDone(charSequence);
                HomeRoomAdapter.this.notifyDataSetChanged();
            }
        };
    }

    @CallSuper
    public void setRooms(List<Room> list) {
        if (list != null) {
            this.mRooms.clear();
            this.mRooms.addAll(list);
            filterRooms(this.mCurrentFilterPattern);
        }
        notifyDataSetChanged();
    }

    public Room getRoom(int i) {
        if (i < this.mRooms.size()) {
            return (Room) this.mRooms.get(i);
        }
        return null;
    }

    public boolean isEmpty() {
        return this.mRooms.isEmpty();
    }

    public boolean hasNoResult() {
        return this.mFilteredRooms.isEmpty();
    }

    public int getBadgeCount() {
        int i = 0;
        for (Room room : this.mFilteredRooms) {
            if (!(room.getDataHandler() == null || room.getDataHandler().getBingRulesManager() == null)) {
                if (room.getDataHandler().getBingRulesManager().isRoomMentionOnly(room.getRoomId())) {
                    i += room.getHighlightCount();
                } else {
                    i += room.getNotificationCount();
                }
            }
        }
        return i;
    }

    /* access modifiers changed from: private */
    public void filterRooms(CharSequence charSequence) {
        this.mFilteredRooms.clear();
        this.mFilteredRooms.addAll(RoomUtils.getFilteredRooms(this.mContext, this.mSession, this.mRooms, charSequence));
    }
}
