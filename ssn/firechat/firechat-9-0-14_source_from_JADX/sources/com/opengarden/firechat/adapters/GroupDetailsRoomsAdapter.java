package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupRoom;
import com.opengarden.firechat.util.GroupUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GroupDetailsRoomsAdapter extends AbsAdapter {
    private static final int TYPE_GROUP_ROOMS = 22;
    private static final Comparator<GroupRoom> mComparator = new Comparator<GroupRoom>() {
        public int compare(GroupRoom groupRoom, GroupRoom groupRoom2) {
            return groupRoom.getDisplayName().compareTo(groupRoom2.getDisplayName());
        }
    };
    private final GroupAdapterSection<GroupRoom> mGroupRoomsSection;
    /* access modifiers changed from: private */
    public final OnSelectRoomListener mListener;

    public interface OnSelectRoomListener {
        void onSelectItem(GroupRoom groupRoom, int i);
    }

    public GroupDetailsRoomsAdapter(Context context, OnSelectRoomListener onSelectRoomListener) {
        super(context);
        this.mListener = onSelectRoomListener;
        GroupAdapterSection groupAdapterSection = new GroupAdapterSection(context, context.getString(C1299R.string.rooms), -1, C1299R.layout.adapter_item_group_user_room_view, -1, 22, new ArrayList(), mComparator);
        this.mGroupRoomsSection = groupAdapterSection;
        this.mGroupRoomsSection.setEmptyViewPlaceholder(null, context.getString(C1299R.string.no_result_placeholder));
        addSection(this.mGroupRoomsSection);
    }

    /* access modifiers changed from: protected */
    public ViewHolder createSubViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 22) {
            return new GroupRoomViewHolder(from.inflate(C1299R.layout.adapter_item_group_user_room_view, viewGroup, false));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void populateViewHolder(int i, ViewHolder viewHolder, int i2) {
        if (i == 22) {
            GroupRoomViewHolder groupRoomViewHolder = (GroupRoomViewHolder) viewHolder;
            final GroupRoom groupRoom = (GroupRoom) getItemForPosition(i2);
            groupRoomViewHolder.populateViews(this.mContext, this.mSession, groupRoom);
            groupRoomViewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GroupDetailsRoomsAdapter.this.mListener.onSelectItem(groupRoom, -1);
                }
            });
        }
    }

    /* access modifiers changed from: 0000 */
    public int filterGroupRoomsSection(AdapterSection<GroupRoom> adapterSection, String str) {
        if (adapterSection == null) {
            return 0;
        }
        if (!TextUtils.isEmpty(str)) {
            adapterSection.setFilteredItems(GroupUtils.getFilteredGroupRooms(adapterSection.getItems(), str), str);
        } else {
            adapterSection.resetFilter();
        }
        return adapterSection.getFilteredItems().size();
    }

    /* access modifiers changed from: protected */
    public int applyFilter(String str) {
        return filterGroupRoomsSection(this.mGroupRoomsSection, str);
    }

    public void setGroupRooms(List<GroupRoom> list) {
        this.mGroupRoomsSection.setItems(list, this.mCurrentFilterPattern);
        updateSections();
    }
}
