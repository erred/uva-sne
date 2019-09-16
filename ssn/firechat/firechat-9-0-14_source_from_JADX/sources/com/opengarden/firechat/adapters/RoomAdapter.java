package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.HeaderViewHolder;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends AbsAdapter {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RoomAdapter";
    private static final int TYPE_HEADER_PUBLIC_ROOM = 0;
    private static final int TYPE_PUBLIC_ROOM = 1;
    /* access modifiers changed from: private */
    public final OnSelectItemListener mListener;
    private final PublicRoomsAdapterSection mPublicRoomsSection;
    private final AdapterSection<Room> mRoomsSection;

    public interface OnSelectItemListener {
        void onSelectItem(Room room, int i);

        void onSelectItem(PublicRoom publicRoom);
    }

    class PublicRoomViewHolder extends ViewHolder {
        @BindView(2131296868)
        ImageView vPublicRoomAvatar;
        @BindView(2131296870)
        TextView vPublicRoomName;
        @BindView(2131296869)
        TextView vPublicRoomsMemberCountTextView;
        @BindView(2131296871)
        TextView vRoomTopic;

        private PublicRoomViewHolder(View view) {
            super(view);
            ButterKnife.bind((Object) this, view);
        }

        /* access modifiers changed from: private */
        public void populateViews(final PublicRoom publicRoom) {
            if (publicRoom == null) {
                Log.m211e(RoomAdapter.LOG_TAG, "## populateViews() : null publicRoom");
                return;
            }
            String publicRoomDisplayName = !TextUtils.isEmpty(publicRoom.name) ? publicRoom.name : VectorUtils.getPublicRoomDisplayName(publicRoom);
            this.vPublicRoomAvatar.setBackgroundColor(ContextCompat.getColor(RoomAdapter.this.mContext, 17170445));
            VectorUtils.loadUserAvatar(RoomAdapter.this.mContext, RoomAdapter.this.mSession, this.vPublicRoomAvatar, publicRoom.getAvatarUrl(), publicRoom.roomId, publicRoomDisplayName);
            this.vRoomTopic.setText(publicRoom.topic);
            this.vPublicRoomName.setText(publicRoomDisplayName);
            this.vPublicRoomsMemberCountTextView.setText(RoomAdapter.this.mContext.getResources().getQuantityString(C1299R.plurals.public_room_nb_users, publicRoom.numJoinedMembers, new Object[]{Integer.valueOf(publicRoom.numJoinedMembers)}));
            this.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RoomAdapter.this.mListener.onSelectItem(publicRoom);
                }
            });
        }
    }

    public class PublicRoomViewHolder_ViewBinding implements Unbinder {
        private PublicRoomViewHolder target;

        @UiThread
        public PublicRoomViewHolder_ViewBinding(PublicRoomViewHolder publicRoomViewHolder, View view) {
            this.target = publicRoomViewHolder;
            publicRoomViewHolder.vPublicRoomAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.public_room_avatar, "field 'vPublicRoomAvatar'", ImageView.class);
            publicRoomViewHolder.vPublicRoomName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.public_room_name, "field 'vPublicRoomName'", TextView.class);
            publicRoomViewHolder.vRoomTopic = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.public_room_topic, "field 'vRoomTopic'", TextView.class);
            publicRoomViewHolder.vPublicRoomsMemberCountTextView = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.public_room_members_count, "field 'vPublicRoomsMemberCountTextView'", TextView.class);
        }

        @CallSuper
        public void unbind() {
            PublicRoomViewHolder publicRoomViewHolder = this.target;
            if (publicRoomViewHolder == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            publicRoomViewHolder.vPublicRoomAvatar = null;
            publicRoomViewHolder.vPublicRoomName = null;
            publicRoomViewHolder.vRoomTopic = null;
            publicRoomViewHolder.vPublicRoomsMemberCountTextView = null;
        }
    }

    public RoomAdapter(Context context, OnSelectItemListener onSelectItemListener, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        super(context, roomInvitationListener, moreRoomActionListener);
        this.mListener = onSelectItemListener;
        AdapterSection adapterSection = new AdapterSection(context, context.getString(C1299R.string.rooms_header), -1, C1299R.layout.adapter_item_room_view, -1, -3, new ArrayList(), RoomUtils.getRoomsDateComparator(this.mSession, false));
        this.mRoomsSection = adapterSection;
        this.mRoomsSection.setEmptyViewPlaceholder(context.getString(C1299R.string.no_room_placeholder), context.getString(C1299R.string.no_result_placeholder));
        PublicRoomsAdapterSection publicRoomsAdapterSection = new PublicRoomsAdapterSection(context, context.getString(C1299R.string.rooms_directory_header), C1299R.layout.adapter_public_room_sticky_header_subview, C1299R.layout.adapter_item_public_room_view, 0, 1, new ArrayList(), null);
        this.mPublicRoomsSection = publicRoomsAdapterSection;
        this.mPublicRoomsSection.setEmptyViewPlaceholder(context.getString(C1299R.string.no_public_room_placeholder), context.getString(C1299R.string.no_result_placeholder));
        addSection(this.mRoomsSection);
        addSection(this.mPublicRoomsSection);
    }

    /* access modifiers changed from: protected */
    public ViewHolder createSubViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 0) {
            View inflate = from.inflate(C1299R.layout.adapter_section_header_public_room, viewGroup, false);
            inflate.setBackgroundColor(-65281);
            return new HeaderViewHolder(inflate);
        } else if (i == -3) {
            return new RoomViewHolder(from.inflate(C1299R.layout.adapter_item_room_view, viewGroup, false));
        } else {
            if (i != 1) {
                return null;
            }
            return new PublicRoomViewHolder(from.inflate(C1299R.layout.adapter_item_public_room_view, viewGroup, false));
        }
    }

    /* access modifiers changed from: protected */
    public void populateViewHolder(int i, ViewHolder viewHolder, int i2) {
        if (i != -3) {
            switch (i) {
                case 0:
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                    for (Pair pair : getSectionsArray()) {
                        if (((Integer) pair.first).intValue() == i2) {
                            headerViewHolder.populateViews((AdapterSection) pair.second);
                            return;
                        }
                    }
                    return;
                case 1:
                    ((PublicRoomViewHolder) viewHolder).populateViews((PublicRoom) getItemForPosition(i2));
                    return;
                default:
                    return;
            }
        } else {
            RoomViewHolder roomViewHolder = (RoomViewHolder) viewHolder;
            final Room room = (Room) getItemForPosition(i2);
            roomViewHolder.populateViews(this.mContext, this.mSession, room, false, false, this.mMoreRoomActionListener);
            roomViewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RoomAdapter.this.mListener.onSelectItem(room, -1);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public int applyFilter(String str) {
        return filterRoomSection(this.mRoomsSection, str) + 0;
    }

    public void setRooms(List<Room> list) {
        this.mRoomsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterRoomSection(this.mRoomsSection, String.valueOf(this.mCurrentFilterPattern));
        }
        updateSections();
    }

    public void setPublicRooms(List<PublicRoom> list) {
        this.mPublicRoomsSection.setItems(list, this.mCurrentFilterPattern);
        updateSections();
    }

    public void setEstimatedPublicRoomsCount(int i) {
        this.mPublicRoomsSection.setEstimatedPublicRoomsCount(i);
    }

    public void setNoMorePublicRooms(boolean z) {
        this.mPublicRoomsSection.setHasMoreResults(z);
    }

    @CallSuper
    public void addPublicRooms(List<PublicRoom> list) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this.mPublicRoomsSection.getItems());
        arrayList.addAll(list);
        this.mPublicRoomsSection.setItems(arrayList, this.mCurrentFilterPattern);
        updateSections();
    }
}
