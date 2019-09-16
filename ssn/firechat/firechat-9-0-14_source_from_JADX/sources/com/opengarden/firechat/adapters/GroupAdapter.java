package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p000v4.provider.FontsContractCompat.FontRequestCallback;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AbsAdapter.GroupInvitationListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreGroupActionListener;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends AbsAdapter {
    private static final String LOG_TAG = "GroupAdapter";
    private final AdapterSection<Group> mGroupsSection;
    private final AdapterSection<Group> mInvitedGroupsSection;
    /* access modifiers changed from: private */
    public final OnGroupSelectItemListener mListener;

    public interface OnGroupSelectItemListener {
        boolean onLongPressItem(Group group, int i);

        void onSelectItem(Group group, int i);
    }

    public GroupAdapter(Context context, OnGroupSelectItemListener onGroupSelectItemListener, GroupInvitationListener groupInvitationListener, MoreGroupActionListener moreGroupActionListener) {
        super(context, groupInvitationListener, moreGroupActionListener);
        this.mListener = onGroupSelectItemListener;
        AdapterSection adapterSection = new AdapterSection(context, context.getString(C1299R.string.groups_invite_header), -1, C1299R.layout.adapter_item_group_invite, -1, -5, new ArrayList(), Group.mGroupsComparator);
        this.mInvitedGroupsSection = adapterSection;
        this.mInvitedGroupsSection.setEmptyViewPlaceholder(context.getString(C1299R.string.no_group_placeholder), context.getString(C1299R.string.no_result_placeholder));
        this.mInvitedGroupsSection.setIsHiddenWhenEmpty(true);
        AdapterSection adapterSection2 = new AdapterSection(context, context.getString(C1299R.string.groups_header), -1, C1299R.layout.adapter_item_group_view, -1, -4, new ArrayList(), Group.mGroupsComparator);
        this.mGroupsSection = adapterSection2;
        this.mGroupsSection.setEmptyViewPlaceholder(context.getString(C1299R.string.no_group_placeholder), context.getString(C1299R.string.no_result_placeholder));
        addSection(this.mInvitedGroupsSection);
        addSection(this.mGroupsSection);
    }

    /* access modifiers changed from: protected */
    public ViewHolder createSubViewHolder(ViewGroup viewGroup, int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(" onCreateViewHolder for viewType:");
        sb.append(i);
        Log.m213i(str, sb.toString());
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        switch (i) {
            case -5:
                return new GroupInvitationViewHolder(from.inflate(C1299R.layout.adapter_item_group_invite, viewGroup, false));
            case FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /*-4*/:
                return new GroupViewHolder(from.inflate(C1299R.layout.adapter_item_group_view, viewGroup, false));
            default:
                return null;
        }
    }

    /* access modifiers changed from: protected */
    public void populateViewHolder(int i, ViewHolder viewHolder, int i2) {
        final Group group;
        View view = null;
        switch (i) {
            case -5:
                GroupInvitationViewHolder groupInvitationViewHolder = (GroupInvitationViewHolder) viewHolder;
                group = (Group) getItemForPosition(i2);
                groupInvitationViewHolder.populateViews(this.mContext, this.mSession, group, this.mGroupInvitationListener, true, this.mMoreGroupActionListener);
                view = groupInvitationViewHolder.itemView;
                break;
            case FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /*-4*/:
                GroupViewHolder groupViewHolder = (GroupViewHolder) viewHolder;
                group = (Group) getItemForPosition(i2);
                groupViewHolder.populateViews(this.mContext, this.mSession, group, null, false, this.mMoreGroupActionListener);
                view = groupViewHolder.itemView;
                break;
            default:
                group = null;
                break;
        }
        if (view != null) {
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    GroupAdapter.this.mListener.onSelectItem(group, -1);
                }
            });
            view.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    return GroupAdapter.this.mListener.onLongPressItem(group, -1);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public int applyFilter(String str) {
        return filterGroupSection(this.mInvitedGroupsSection, str) + 0 + filterGroupSection(this.mGroupsSection, str);
    }

    public void setGroups(List<Group> list) {
        this.mGroupsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterGroupSection(this.mGroupsSection, String.valueOf(this.mCurrentFilterPattern));
        }
        updateSections();
    }

    public void setInvitedGroups(List<Group> list) {
        this.mInvitedGroupsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterGroupSection(this.mInvitedGroupsSection, String.valueOf(this.mCurrentFilterPattern));
        }
        updateSections();
    }
}
