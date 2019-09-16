package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.GroupUtils;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.StickySectionHelper;
import com.opengarden.firechat.view.SectionView;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdapter extends AbsFilterableAdapter {
    private static final String LOG_TAG = "AbsAdapter";
    static final int TYPE_GROUP = -4;
    static final int TYPE_GROUP_INVITATION = -5;
    static final int TYPE_HEADER_DEFAULT = -1;
    static final int TYPE_ROOM = -3;
    static final int TYPE_ROOM_INVITATION = -2;
    /* access modifiers changed from: private */
    public AdapterSection<Room> mInviteSection;
    /* access modifiers changed from: private */
    public final List<Pair<Integer, AdapterSection>> mSections = new ArrayList();
    /* access modifiers changed from: private */
    public StickySectionHelper mStickySectionHelper;

    private class AdapterDataObserver extends android.support.p003v7.widget.RecyclerView.AdapterDataObserver {
        private AdapterDataObserver() {
        }

        private void resetStickyHeaders() {
            if (AbsAdapter.this.mStickySectionHelper != null) {
                AbsAdapter.this.mStickySectionHelper.resetSticky(AbsAdapter.this.mSections);
            }
        }

        public void onChanged() {
            resetStickyHeaders();
        }

        public void onItemRangeInserted(int i, int i2) {
            resetStickyHeaders();
        }

        public void onItemRangeRemoved(int i, int i2) {
            resetStickyHeaders();
        }

        public void onItemRangeChanged(int i, int i2) {
            resetStickyHeaders();
        }

        public void onItemRangeMoved(int i, int i2, int i3) {
            resetStickyHeaders();
        }
    }

    public interface GroupInvitationListener {
        void onJoinGroup(MXSession mXSession, String str);

        void onRejectInvitation(MXSession mXSession, String str);
    }

    public class HeaderViewHolder extends ViewHolder {
        private AdapterSection mSection;
        @BindView(2131297019)
        TextView vSectionTitle;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind((Object) this, view);
        }

        /* access modifiers changed from: 0000 */
        public void populateViews(AdapterSection adapterSection) {
            this.mSection = adapterSection;
            this.vSectionTitle.setText(adapterSection != null ? adapterSection.getTitle() : null);
        }

        public AdapterSection getSection() {
            return this.mSection;
        }
    }

    public class HeaderViewHolder_ViewBinding implements Unbinder {
        private HeaderViewHolder target;

        @UiThread
        public HeaderViewHolder_ViewBinding(HeaderViewHolder headerViewHolder, View view) {
            this.target = headerViewHolder;
            headerViewHolder.vSectionTitle = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.section_title, "field 'vSectionTitle'", TextView.class);
        }

        @CallSuper
        public void unbind() {
            HeaderViewHolder headerViewHolder = this.target;
            if (headerViewHolder == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            headerViewHolder.vSectionTitle = null;
        }
    }

    public interface MoreGroupActionListener {
        void onMoreActionClick(View view, Group group);
    }

    public interface MoreRoomActionListener {
        void onMoreActionClick(View view, Room room);
    }

    public interface RoomInvitationListener {
        void onPreviewRoom(MXSession mXSession, String str);

        void onRejectInvitation(MXSession mXSession, String str);
    }

    /* access modifiers changed from: protected */
    public abstract int applyFilter(String str);

    /* access modifiers changed from: protected */
    public abstract ViewHolder createSubViewHolder(ViewGroup viewGroup, int i);

    /* access modifiers changed from: protected */
    public abstract void populateViewHolder(int i, ViewHolder viewHolder, int i2);

    AbsAdapter(Context context, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        super(context, roomInvitationListener, moreRoomActionListener);
        registerAdapterDataObserver(new AdapterDataObserver());
        AdapterSection adapterSection = new AdapterSection(context, context.getString(C1299R.string.room_recents_invites), -1, C1299R.layout.adapter_item_room_view, -1, -2, new ArrayList(), null);
        this.mInviteSection = adapterSection;
        this.mInviteSection.setEmptyViewPlaceholder(null, context.getString(C1299R.string.no_result_placeholder));
        this.mInviteSection.setIsHiddenWhenEmpty(true);
        addSection(this.mInviteSection);
    }

    AbsAdapter(Context context, GroupInvitationListener groupInvitationListener, MoreGroupActionListener moreGroupActionListener) {
        super(context, groupInvitationListener, moreGroupActionListener);
        registerAdapterDataObserver(new AdapterDataObserver());
    }

    AbsAdapter(Context context) {
        super(context);
        registerAdapterDataObserver(new AdapterDataObserver());
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mStickySectionHelper = new StickySectionHelper(recyclerView, this.mSections);
    }

    public int getItemViewType(int i) {
        for (Pair pair : this.mSections) {
            if (((Integer) pair.first).intValue() == i) {
                return ((AdapterSection) pair.second).getHeaderViewType();
            }
            if (i <= ((Integer) pair.first).intValue() + ((AdapterSection) pair.second).getNbItems()) {
                return ((AdapterSection) pair.second).getContentViewType();
            }
        }
        return 0;
    }

    public int getItemCount() {
        Pair pair = (Pair) this.mSections.get(this.mSections.size() - 1);
        return ((Integer) pair.first).intValue() + ((AdapterSection) pair.second).getNbItems() + 1;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(" onCreateViewHolder for viewType:");
        sb.append(i);
        Log.m213i(str, sb.toString());
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        switch (i) {
            case -2:
                return new RoomInvitationViewHolder(from.inflate(C1299R.layout.adapter_item_room_invite, viewGroup, false));
            case -1:
                return new HeaderViewHolder(from.inflate(C1299R.layout.adapter_section_header, viewGroup, false));
            default:
                return createSubViewHolder(viewGroup, i);
        }
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(" onBindViewHolder for position:");
        sb.append(i);
        Log.m213i(str, sb.toString());
        int itemViewType = getItemViewType(i);
        switch (itemViewType) {
            case -2:
                ((RoomInvitationViewHolder) viewHolder).populateViews(this.mContext, this.mSession, (Room) getItemForPosition(i), this.mRoomInvitationListener, this.mMoreRoomActionListener);
                return;
            case -1:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                for (Pair pair : this.mSections) {
                    if (((Integer) pair.first).intValue() == i) {
                        if (((AdapterSection) pair.second).shouldBeHidden()) {
                            headerViewHolder.itemView.setVisibility(8);
                            headerViewHolder.itemView.getLayoutParams().height = 0;
                            headerViewHolder.itemView.requestLayout();
                            headerViewHolder.populateViews(null);
                            return;
                        }
                        if (headerViewHolder.itemView.getVisibility() != 0) {
                            headerViewHolder.itemView.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
                            headerViewHolder.itemView.getLayoutParams().height = headerViewHolder.itemView.getMeasuredHeight();
                            headerViewHolder.itemView.setVisibility(0);
                        }
                        headerViewHolder.populateViews((AdapterSection) pair.second);
                        return;
                    }
                }
                return;
            default:
                populateViewHolder(itemViewType, viewHolder, i);
                return;
        }
    }

    /* access modifiers changed from: protected */
    public Filter createFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String trim = !TextUtils.isEmpty(charSequence) ? charSequence.toString().trim() : null;
                filterResults.count = AbsAdapter.this.applyFilter(trim) + AbsAdapter.this.filterRoomSection(AbsAdapter.this.mInviteSection, trim);
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                AbsAdapter.this.onFilterDone(charSequence);
                AbsAdapter.this.updateSections();
                if (AbsAdapter.this.mStickySectionHelper != null) {
                    AbsAdapter.this.mStickySectionHelper.resetSticky(AbsAdapter.this.mSections);
                }
            }
        };
    }

    public void setInvitation(List<Room> list) {
        if (this.mInviteSection != null) {
            this.mInviteSection.setItems(list, this.mCurrentFilterPattern);
            if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
                filterRoomSection(this.mInviteSection, String.valueOf(this.mCurrentFilterPattern));
            }
            updateSections();
        }
    }

    public SectionView getSectionViewForSectionIndex(int i) {
        if (this.mStickySectionHelper != null) {
            return this.mStickySectionHelper.getSectionViewForSectionIndex(i);
        }
        return null;
    }

    public View findSectionSubViewById(@IdRes int i) {
        if (this.mStickySectionHelper != null) {
            return this.mStickySectionHelper.findSectionSubViewById(i);
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public Object getItemForPosition(int i) {
        for (int i2 = 0; i2 < this.mSections.size(); i2++) {
            Pair pair = (Pair) this.mSections.get(i2);
            if (i > ((Integer) pair.first).intValue()) {
                int intValue = (i - ((Integer) pair.first).intValue()) - 1;
                if (intValue < ((AdapterSection) pair.second).getFilteredItems().size()) {
                    return ((AdapterSection) pair.second).getFilteredItems().get(intValue);
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void addSection(AdapterSection adapterSection) {
        addSection(adapterSection, -1);
    }

    private void addSection(AdapterSection adapterSection, int i) {
        int intValue = this.mSections.size() > 0 ? ((Integer) ((Pair) this.mSections.get(this.mSections.size() - 1)).first).intValue() + 1 + ((AdapterSection) ((Pair) this.mSections.get(this.mSections.size() - 1)).second).getNbItems() : 0;
        if (i != -1) {
            this.mSections.add(i, new Pair(Integer.valueOf(intValue), adapterSection));
        } else {
            this.mSections.add(new Pair(Integer.valueOf(intValue), adapterSection));
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("New section ");
        sb.append(adapterSection.getTitle());
        sb.append(", header at ");
        sb.append(intValue);
        sb.append(" with nbItem ");
        sb.append(adapterSection.getNbItems());
        Log.m213i(str, sb.toString());
    }

    /* access modifiers changed from: 0000 */
    public void updateSections() {
        List<AdapterSection> sections = getSections();
        this.mSections.clear();
        for (AdapterSection addSection : sections) {
            addSection(addSection);
        }
        notifyDataSetChanged();
    }

    public int getSectionsCount() {
        return this.mSections.size();
    }

    /* access modifiers changed from: 0000 */
    public List<AdapterSection> getSections() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mSections.size(); i++) {
            arrayList.add((AdapterSection) ((Pair) this.mSections.get(i)).second);
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public List<Pair<Integer, AdapterSection>> getSectionsArray() {
        return new ArrayList(this.mSections);
    }

    /* access modifiers changed from: 0000 */
    public int getSectionHeaderPosition(AdapterSection adapterSection) {
        for (Pair pair : this.mSections) {
            if (pair.second == adapterSection) {
                return ((Integer) pair.first).intValue();
            }
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public int filterRoomSection(AdapterSection<Room> adapterSection, String str) {
        if (adapterSection == null) {
            return 0;
        }
        if (!TextUtils.isEmpty(str)) {
            adapterSection.setFilteredItems(RoomUtils.getFilteredRooms(this.mContext, this.mSession, adapterSection.getItems(), str), str);
        } else {
            adapterSection.resetFilter();
        }
        return adapterSection.getFilteredItems().size();
    }

    /* access modifiers changed from: 0000 */
    public int filterGroupSection(AdapterSection<Group> adapterSection, String str) {
        if (adapterSection == null) {
            return 0;
        }
        if (!TextUtils.isEmpty(str)) {
            adapterSection.setFilteredItems(GroupUtils.getFilteredGroups(adapterSection.getItems(), str), str);
        } else {
            adapterSection.resetFilter();
        }
        return adapterSection.getFilteredItems().size();
    }
}
