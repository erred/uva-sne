package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
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
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.adapters.AbsAdapter.HeaderViewHolder;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.contacts.Contact.PhoneNumber;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeopleAdapter extends AbsAdapter {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PeopleAdapter";
    private static final int TYPE_CONTACT = 1;
    private static final int TYPE_HEADER_LOCAL_CONTACTS = 0;
    private final AdapterSection<Room> mDirectChatsSection;
    private final KnownContactsAdapterSection mKnownContactsSection;
    /* access modifiers changed from: private */
    public final OnSelectItemListener mListener;
    private final AdapterSection<ParticipantAdapterItem> mLocalContactsSection;
    private final String mNoContactAccessPlaceholder;
    private final String mNoResultPlaceholder;

    class ContactViewHolder extends ViewHolder {
        @BindView(2131296394)
        ImageView vContactAvatar;
        @BindView(2131296396)
        ImageView vContactBadge;
        @BindView(2131296397)
        TextView vContactDesc;
        @BindView(2131296398)
        TextView vContactName;

        private ContactViewHolder(View view) {
            super(view);
            ButterKnife.bind((Object) this, view);
        }

        /* access modifiers changed from: private */
        public void populateViews(final ParticipantAdapterItem participantAdapterItem, int i) {
            if (participantAdapterItem == null) {
                Log.m211e(PeopleAdapter.LOG_TAG, "## populateViews() : null participant");
            } else if (i >= PeopleAdapter.this.getItemCount()) {
                String access$300 = PeopleAdapter.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## populateViews() : position out of bound ");
                sb.append(i);
                sb.append(" / ");
                sb.append(PeopleAdapter.this.getItemCount());
                Log.m211e(access$300, sb.toString());
            } else {
                participantAdapterItem.displayAvatar(PeopleAdapter.this.mSession, this.vContactAvatar);
                this.vContactName.setText(participantAdapterItem.getUniqueDisplayName(null));
                int i2 = 8;
                if (participantAdapterItem.mContact != null) {
                    boolean matches = MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(participantAdapterItem.mUserId).matches();
                    ImageView imageView = this.vContactBadge;
                    if (matches) {
                        i2 = 0;
                    }
                    imageView.setVisibility(i2);
                    if (participantAdapterItem.mContact.getEmails().size() > 0) {
                        this.vContactDesc.setText((CharSequence) participantAdapterItem.mContact.getEmails().get(0));
                    } else {
                        this.vContactDesc.setText(((PhoneNumber) participantAdapterItem.mContact.getPhonenumbers().get(0)).mRawPhoneNumber);
                    }
                } else {
                    loadContactPresence(this.vContactDesc, participantAdapterItem, i);
                    this.vContactBadge.setVisibility(8);
                }
                this.itemView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        PeopleAdapter.this.mListener.onSelectItem(participantAdapterItem, -1);
                    }
                });
            }
        }

        private void loadContactPresence(final TextView textView, final ParticipantAdapterItem participantAdapterItem, final int i) {
            textView.setText(VectorUtils.getUserOnlineStatus(PeopleAdapter.this.mContext, PeopleAdapter.this.mSession, participantAdapterItem.mUserId, new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    if (textView != null) {
                        textView.setText(VectorUtils.getUserOnlineStatus(PeopleAdapter.this.mContext, PeopleAdapter.this.mSession, participantAdapterItem.mUserId, null));
                        PeopleAdapter.this.notifyItemChanged(i);
                    }
                }
            }));
        }
    }

    public class ContactViewHolder_ViewBinding implements Unbinder {
        private ContactViewHolder target;

        @UiThread
        public ContactViewHolder_ViewBinding(ContactViewHolder contactViewHolder, View view) {
            this.target = contactViewHolder;
            contactViewHolder.vContactAvatar = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_avatar, "field 'vContactAvatar'", ImageView.class);
            contactViewHolder.vContactBadge = (ImageView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_badge, "field 'vContactBadge'", ImageView.class);
            contactViewHolder.vContactName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_name, "field 'vContactName'", TextView.class);
            contactViewHolder.vContactDesc = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.contact_desc, "field 'vContactDesc'", TextView.class);
        }

        @CallSuper
        public void unbind() {
            ContactViewHolder contactViewHolder = this.target;
            if (contactViewHolder == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            contactViewHolder.vContactAvatar = null;
            contactViewHolder.vContactBadge = null;
            contactViewHolder.vContactName = null;
            contactViewHolder.vContactDesc = null;
        }
    }

    public interface OnSelectItemListener {
        void onSelectItem(ParticipantAdapterItem participantAdapterItem, int i);

        void onSelectItem(Room room, int i);
    }

    public PeopleAdapter(Context context, OnSelectItemListener onSelectItemListener, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        super(context, roomInvitationListener, moreRoomActionListener);
        this.mListener = onSelectItemListener;
        this.mNoContactAccessPlaceholder = context.getString(C1299R.string.no_contact_access_placeholder);
        this.mNoResultPlaceholder = context.getString(C1299R.string.no_result_placeholder);
        AdapterSection adapterSection = new AdapterSection(context, context.getString(C1299R.string.direct_chats_header), -1, C1299R.layout.adapter_item_room_view, -1, -3, new ArrayList(), RoomUtils.getRoomsDateComparator(this.mSession, false));
        this.mDirectChatsSection = adapterSection;
        this.mDirectChatsSection.setEmptyViewPlaceholder(context.getString(C1299R.string.no_conversation_placeholder), context.getString(C1299R.string.no_result_placeholder));
        AdapterSection adapterSection2 = new AdapterSection(context, context.getString(C1299R.string.local_address_book_header), C1299R.layout.adapter_local_contacts_sticky_header_subview, C1299R.layout.adapter_item_contact_view, 0, 1, new ArrayList(), ParticipantAdapterItem.alphaComparator);
        this.mLocalContactsSection = adapterSection2;
        this.mLocalContactsSection.setEmptyViewPlaceholder(!ContactsManager.getInstance().isContactBookAccessAllowed() ? this.mNoContactAccessPlaceholder : this.mNoResultPlaceholder);
        KnownContactsAdapterSection knownContactsAdapterSection = new KnownContactsAdapterSection(context, context.getString(C1299R.string.user_directory_header), -1, C1299R.layout.adapter_item_contact_view, -1, 1, new ArrayList(), null);
        this.mKnownContactsSection = knownContactsAdapterSection;
        this.mKnownContactsSection.setEmptyViewPlaceholder(null, context.getString(C1299R.string.no_result_placeholder));
        this.mKnownContactsSection.setIsHiddenWhenNoFilter(true);
        addSection(this.mDirectChatsSection);
        addSection(this.mLocalContactsSection);
        addSection(this.mKnownContactsSection);
    }

    /* access modifiers changed from: protected */
    public ViewHolder createSubViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
        if (i == 0) {
            View inflate = from.inflate(C1299R.layout.adapter_section_header_local, viewGroup, false);
            inflate.setBackgroundColor(-65281);
            return new HeaderViewHolder(inflate);
        } else if (i == -3) {
            return new RoomViewHolder(from.inflate(C1299R.layout.adapter_item_room_view, viewGroup, false));
        } else {
            if (i != 1) {
                return null;
            }
            return new ContactViewHolder(from.inflate(C1299R.layout.adapter_item_contact_view, viewGroup, false));
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
                    ((ContactViewHolder) viewHolder).populateViews((ParticipantAdapterItem) getItemForPosition(i2), i2);
                    return;
                default:
                    return;
            }
        } else {
            RoomViewHolder roomViewHolder = (RoomViewHolder) viewHolder;
            final Room room = (Room) getItemForPosition(i2);
            roomViewHolder.populateViews(this.mContext, this.mSession, room, true, false, this.mMoreRoomActionListener);
            roomViewHolder.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    PeopleAdapter.this.mListener.onSelectItem(room, -1);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public int applyFilter(String str) {
        int filterRoomSection = filterRoomSection(this.mDirectChatsSection, str) + 0 + filterLocalContacts(str);
        return TextUtils.isEmpty(str) ? filterRoomSection + filterKnownContacts(str) : filterRoomSection;
    }

    public void setRooms(List<Room> list) {
        this.mDirectChatsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterRoomSection(this.mDirectChatsSection, String.valueOf(this.mCurrentFilterPattern));
        }
        updateSections();
    }

    public void setLocalContacts(List<ParticipantAdapterItem> list) {
        this.mLocalContactsSection.setEmptyViewPlaceholder(!ContactsManager.getInstance().isContactBookAccessAllowed() ? this.mNoContactAccessPlaceholder : this.mNoResultPlaceholder);
        this.mLocalContactsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterLocalContacts(String.valueOf(this.mCurrentFilterPattern));
        }
        updateSections();
    }

    public void setKnownContacts(List<ParticipantAdapterItem> list) {
        this.mKnownContactsSection.setItems(list, this.mCurrentFilterPattern);
        if (!TextUtils.isEmpty(this.mCurrentFilterPattern)) {
            filterKnownContacts(String.valueOf(this.mCurrentFilterPattern));
        } else {
            filterKnownContacts(null);
        }
        updateSections();
    }

    public void setFilteredKnownContacts(List<ParticipantAdapterItem> list, String str) {
        Collections.sort(list, ParticipantAdapterItem.getComparator(this.mSession));
        this.mKnownContactsSection.setFilteredItems(list, str);
        updateSections();
    }

    public void setKnownContactsLimited(boolean z) {
        this.mKnownContactsSection.setIsLimited(z);
    }

    public void setKnownContactsExtraTitle(String str) {
        this.mKnownContactsSection.setCustomHeaderExtra(str);
    }

    public void updateKnownContact(User user) {
        int sectionHeaderPosition = getSectionHeaderPosition(this.mKnownContactsSection) + 1;
        List filteredItems = this.mKnownContactsSection.getFilteredItems();
        for (int i = 0; i < filteredItems.size(); i++) {
            if (TextUtils.equals(user.user_id, ((ParticipantAdapterItem) filteredItems.get(i)).mUserId)) {
                notifyItemChanged(sectionHeaderPosition + i);
            }
        }
    }

    private int filterLocalContacts(String str) {
        if (!TextUtils.isEmpty(str)) {
            ArrayList arrayList = new ArrayList();
            String trim = str.toLowerCase(VectorApp.getApplicationLocale()).trim();
            for (ParticipantAdapterItem participantAdapterItem : new ArrayList(this.mLocalContactsSection.getItems())) {
                if (participantAdapterItem.startsWith(trim)) {
                    arrayList.add(participantAdapterItem);
                }
            }
            this.mLocalContactsSection.setFilteredItems(arrayList, str);
        } else {
            this.mLocalContactsSection.resetFilter();
        }
        return this.mLocalContactsSection.getFilteredItems().size();
    }

    public void filterAccountKnownContacts(String str) {
        filterKnownContacts(str);
        updateSections();
    }

    private int filterKnownContacts(String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            String lowerCase = str.trim().toLowerCase(VectorApp.getApplicationLocale());
            for (ParticipantAdapterItem participantAdapterItem : new ArrayList(this.mKnownContactsSection.getItems())) {
                if (participantAdapterItem.startsWith(lowerCase)) {
                    arrayList.add(participantAdapterItem);
                }
            }
        }
        Collections.sort(arrayList, ParticipantAdapterItem.getComparator(this.mSession));
        this.mKnownContactsSection.setFilteredItems(arrayList, str);
        setKnownContactsLimited(false);
        setKnownContactsExtraTitle(null);
        return arrayList.size();
    }

    public void removeDirectChat(String str) {
        if (this.mDirectChatsSection.removeItem(this.mSession.getDataHandler().getRoom(str))) {
            updateSections();
        }
    }
}
