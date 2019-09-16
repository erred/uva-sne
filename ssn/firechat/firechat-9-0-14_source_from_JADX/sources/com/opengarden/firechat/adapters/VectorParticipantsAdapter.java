package com.opengarden.firechat.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.contacts.Contact;
import com.opengarden.firechat.contacts.Contact.MXID;
import com.opengarden.firechat.contacts.Contact.PhoneNumber;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.PIDsRetriever;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.HelpFormatter;

public class VectorParticipantsAdapter extends BaseExpandableListAdapter {
    private static final String KEY_EXPAND_STATE_SEARCH_LOCAL_CONTACTS_GROUP = "KEY_EXPAND_STATE_SEARCH_LOCAL_CONTACTS_GROUP";
    private static final String KEY_EXPAND_STATE_SEARCH_MATRIX_CONTACTS_GROUP = "KEY_EXPAND_STATE_SEARCH_MATRIX_CONTACTS_GROUP";
    private static final String KEY_FILTER_MATRIX_USERS_ONLY = "KEY_FILTER_MATRIX_USERS_ONLY";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorParticipantsAdapter";
    private static final int MAX_USERS_SEARCH_COUNT = 100;
    private final int mCellLayoutResourceId;
    /* access modifiers changed from: private */
    public List<ParticipantAdapterItem> mContactsParticipants = null;
    /* access modifiers changed from: private */
    public final Context mContext;
    private List<String> mDisplayNamesList = null;
    /* access modifiers changed from: private */
    public ParticipantAdapterItem mFirstEntry;
    private int mFirstEntryPosition = -1;
    private final int mHeaderLayoutResourceId;
    /* access modifiers changed from: private */
    public boolean mIsOfflineContactsSearch;
    private List<ParticipantAdapterItem> mItemsToHide = new ArrayList();
    /* access modifiers changed from: private */
    public boolean mKnownContactsLimited;
    private int mKnownContactsSectionPosition = -1;
    private final LayoutInflater mLayoutInflater;
    private int mLocalContactsSectionPosition = -1;
    private int mLocalContactsSnapshotSession = -1;
    private final List<List<ParticipantAdapterItem>> mParticipantsListsList = new ArrayList();
    /* access modifiers changed from: private */
    public String mPattern = "";
    private final String mRoomId;
    private final MXSession mSession;
    /* access modifiers changed from: private */
    public boolean mShowMatrixUserOnly = false;
    private final Comparator<ParticipantAdapterItem> mSortMethod;
    private List<ParticipantAdapterItem> mUnusedParticipants = null;
    private Set<String> mUsedMemberUserIds = null;
    private final boolean mWithAddIcon;

    public interface OnParticipantsSearchListener {
        void onSearchEnd(int i);
    }

    public boolean hasStableIds() {
        return false;
    }

    public VectorParticipantsAdapter(Context context, int i, int i2, MXSession mXSession, String str, boolean z) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mCellLayoutResourceId = i;
        this.mHeaderLayoutResourceId = i2;
        this.mSession = mXSession;
        this.mRoomId = str;
        this.mWithAddIcon = z;
        this.mSortMethod = ParticipantAdapterItem.getComparator(mXSession);
    }

    public void reset() {
        this.mParticipantsListsList.clear();
        this.mFirstEntryPosition = -1;
        this.mLocalContactsSectionPosition = -1;
        this.mKnownContactsSectionPosition = -1;
        this.mPattern = null;
        notifyDataSetChanged();
    }

    public void setSearchedPattern(String str, ParticipantAdapterItem participantAdapterItem, OnParticipantsSearchListener onParticipantsSearchListener) {
        String lowerCase = str == null ? "" : str.toLowerCase().trim().toLowerCase(VectorApp.getApplicationLocale());
        int i = 0;
        if (!lowerCase.equals(this.mPattern) || TextUtils.isEmpty(this.mPattern)) {
            this.mPattern = lowerCase;
            if (TextUtils.isEmpty(this.mPattern)) {
                this.mShowMatrixUserOnly = false;
                Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
                edit.putBoolean(KEY_FILTER_MATRIX_USERS_ONLY, this.mShowMatrixUserOnly);
                edit.apply();
            }
            refresh(participantAdapterItem, onParticipantsSearchListener);
        } else if (onParticipantsSearchListener != null) {
            for (List size : this.mParticipantsListsList) {
                i += size.size();
            }
            onParticipantsSearchListener.onSearchEnd(i);
        }
    }

    /* access modifiers changed from: private */
    public void addContacts(List<ParticipantAdapterItem> list) {
        Collection<Contact> localContactsSnapshot = ContactsManager.getInstance().getLocalContactsSnapshot();
        if (localContactsSnapshot != null) {
            for (Contact contact : localContactsSnapshot) {
                for (String str : contact.getEmails()) {
                    if (!TextUtils.isEmpty(str) && !ParticipantAdapterItem.isBlackedListed(str)) {
                        Contact contact2 = new Contact(str);
                        contact2.setDisplayName(contact.getDisplayName());
                        contact2.addEmailAdress(str);
                        contact2.setThumbnailUri(contact.getThumbnailUri());
                        ParticipantAdapterItem participantAdapterItem = new ParticipantAdapterItem(contact2);
                        MXID mxid = PIDsRetriever.getInstance().getMXID(str);
                        if (mxid != null) {
                            participantAdapterItem.mUserId = mxid.mMatrixId;
                        } else {
                            participantAdapterItem.mUserId = str;
                        }
                        if (this.mUsedMemberUserIds != null && !this.mUsedMemberUserIds.contains(participantAdapterItem.mUserId)) {
                            list.add(participantAdapterItem);
                        }
                    }
                }
                for (PhoneNumber phoneNumber : contact.getPhonenumbers()) {
                    MXID mxid2 = PIDsRetriever.getInstance().getMXID(phoneNumber.mMsisdnPhoneNumber);
                    if (mxid2 != null) {
                        Contact contact3 = new Contact(phoneNumber.mMsisdnPhoneNumber);
                        contact3.setDisplayName(contact.getDisplayName());
                        contact3.addPhoneNumber(phoneNumber.mRawPhoneNumber, phoneNumber.mE164PhoneNumber);
                        contact3.setThumbnailUri(contact.getThumbnailUri());
                        ParticipantAdapterItem participantAdapterItem2 = new ParticipantAdapterItem(contact3);
                        participantAdapterItem2.mUserId = mxid2.mMatrixId;
                        if (this.mUsedMemberUserIds != null && !this.mUsedMemberUserIds.contains(participantAdapterItem2.mUserId)) {
                            list.add(participantAdapterItem2);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void fillUsedMembersList() {
        IMXStore store = this.mSession.getDataHandler().getStore();
        this.mUsedMemberUserIds = new HashSet();
        if (!(this.mRoomId == null || store == null)) {
            Room room = store.getRoom(this.mRoomId);
            if (room != null) {
                for (RoomMember roomMember : room.getLiveState().getDisplayableMembers()) {
                    if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN) || TextUtils.equals(roomMember.membership, "invite")) {
                        this.mUsedMemberUserIds.add(roomMember.getUserId());
                    }
                }
            }
        }
        for (ParticipantAdapterItem participantAdapterItem : this.mItemsToHide) {
            this.mUsedMemberUserIds.add(participantAdapterItem.mUserId);
        }
    }

    /* access modifiers changed from: private */
    public void listOtherMembers() {
        fillUsedMembersList();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(VectorUtils.listKnownParticipants(this.mSession).values());
        addContacts(arrayList);
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) it.next();
            if (!this.mUsedMemberUserIds.isEmpty() && this.mUsedMemberUserIds.contains(participantAdapterItem.mUserId)) {
                it.remove();
            } else if (!TextUtils.isEmpty(participantAdapterItem.mDisplayName)) {
                arrayList2.add(participantAdapterItem.mDisplayName.toLowerCase(VectorApp.getApplicationLocale()));
            }
        }
        synchronized (LOG_TAG) {
            this.mDisplayNamesList = arrayList2;
            this.mUnusedParticipants = arrayList;
        }
    }

    public boolean isKnownMembersInitialized() {
        boolean z;
        synchronized (LOG_TAG) {
            z = this.mDisplayNamesList != null;
        }
        return z;
    }

    private static boolean match(ParticipantAdapterItem participantAdapterItem, String str) {
        return participantAdapterItem.startsWith(str);
    }

    public void onPIdsUpdate() {
        boolean z;
        ArrayList<ParticipantAdapterItem> arrayList = new ArrayList<>();
        ArrayList<ParticipantAdapterItem> arrayList2 = new ArrayList<>();
        synchronized (LOG_TAG) {
            if (this.mUnusedParticipants != null) {
                arrayList = new ArrayList<>(this.mUnusedParticipants);
            }
            z = false;
            if (this.mContactsParticipants != null) {
                ArrayList arrayList3 = new ArrayList();
                addContacts(arrayList3);
                if (!this.mContactsParticipants.containsAll(arrayList3)) {
                    z = true;
                    this.mContactsParticipants = null;
                } else {
                    arrayList2 = new ArrayList<>(this.mContactsParticipants);
                }
            }
        }
        for (ParticipantAdapterItem retrievePids : arrayList) {
            z |= retrievePids.retrievePids();
        }
        for (ParticipantAdapterItem retrievePids2 : arrayList2) {
            z |= retrievePids2.retrievePids();
        }
        if (z) {
            refresh(this.mFirstEntry, null);
        }
    }

    public void setHiddenParticipantItems(List<ParticipantAdapterItem> list) {
        if (list != null) {
            this.mItemsToHide = list;
        }
    }

    /* access modifiers changed from: private */
    public void refresh(final ParticipantAdapterItem participantAdapterItem, final OnParticipantsSearchListener onParticipantsSearchListener) {
        if (!this.mSession.isAlive()) {
            Log.m211e(LOG_TAG, "refresh : the session is not anymore active");
            return;
        }
        if (this.mLocalContactsSnapshotSession != ContactsManager.getInstance().getLocalContactsSnapshotSession()) {
            synchronized (LOG_TAG) {
                this.mUnusedParticipants = null;
                this.mContactsParticipants = null;
                this.mUsedMemberUserIds = null;
                this.mDisplayNamesList = null;
            }
            this.mLocalContactsSnapshotSession = ContactsManager.getInstance().getLocalContactsSnapshotSession();
        }
        if (!TextUtils.isEmpty(this.mPattern)) {
            fillUsedMembersList();
            final String str = this.mPattern;
            this.mSession.searchUsers(this.mPattern, Integer.valueOf(100), this.mUsedMemberUserIds, new ApiCallback<SearchUsersResponse>() {
                public void onSuccess(SearchUsersResponse searchUsersResponse) {
                    if (TextUtils.equals(str, VectorParticipantsAdapter.this.mPattern)) {
                        ArrayList arrayList = new ArrayList();
                        if (searchUsersResponse.results != null) {
                            for (User participantAdapterItem : searchUsersResponse.results) {
                                arrayList.add(new ParticipantAdapterItem(participantAdapterItem));
                            }
                        }
                        VectorParticipantsAdapter.this.mIsOfflineContactsSearch = false;
                        VectorParticipantsAdapter.this.mKnownContactsLimited = searchUsersResponse.limited != null ? searchUsersResponse.limited.booleanValue() : false;
                        VectorParticipantsAdapter.this.searchAccountKnownContacts(participantAdapterItem, arrayList, false, onParticipantsSearchListener);
                    }
                }

                private void onError() {
                    if (TextUtils.equals(str, VectorParticipantsAdapter.this.mPattern)) {
                        VectorParticipantsAdapter.this.mIsOfflineContactsSearch = true;
                        VectorParticipantsAdapter.this.searchAccountKnownContacts(participantAdapterItem, new ArrayList(), true, onParticipantsSearchListener);
                    }
                }

                public void onNetworkError(Exception exc) {
                    onError();
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError();
                }

                public void onUnexpectedError(Exception exc) {
                    onError();
                }
            });
        } else {
            searchAccountKnownContacts(participantAdapterItem, new ArrayList(), true, onParticipantsSearchListener);
        }
    }

    /* access modifiers changed from: private */
    public void searchAccountKnownContacts(final ParticipantAdapterItem participantAdapterItem, List<ParticipantAdapterItem> list, boolean z, final OnParticipantsSearchListener onParticipantsSearchListener) {
        this.mKnownContactsLimited = false;
        if (TextUtils.isEmpty(this.mPattern)) {
            resetGroupExpansionPreferences();
            if (this.mContactsParticipants == null) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        VectorParticipantsAdapter.this.fillUsedMembersList();
                        ArrayList arrayList = new ArrayList();
                        VectorParticipantsAdapter.this.addContacts(arrayList);
                        synchronized (VectorParticipantsAdapter.LOG_TAG) {
                            VectorParticipantsAdapter.this.mContactsParticipants = arrayList;
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                VectorParticipantsAdapter.this.refresh(participantAdapterItem, onParticipantsSearchListener);
                            }
                        });
                    }
                });
                thread.setPriority(1);
                thread.start();
                return;
            }
            ArrayList arrayList = new ArrayList();
            synchronized (LOG_TAG) {
                if (this.mContactsParticipants != null) {
                    arrayList = new ArrayList(this.mContactsParticipants);
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ParticipantAdapterItem participantAdapterItem2 = (ParticipantAdapterItem) it.next();
                if (!this.mUsedMemberUserIds.isEmpty() && this.mUsedMemberUserIds.contains(participantAdapterItem2.mUserId)) {
                    it.remove();
                }
            }
            synchronized (LOG_TAG) {
                if (this.mContactsParticipants != null) {
                    list.addAll(this.mContactsParticipants);
                }
            }
        } else if (this.mUnusedParticipants == null) {
            final ParticipantAdapterItem participantAdapterItem3 = participantAdapterItem;
            final List<ParticipantAdapterItem> list2 = list;
            final boolean z2 = z;
            final OnParticipantsSearchListener onParticipantsSearchListener2 = onParticipantsSearchListener;
            C18152 r2 = new Runnable() {
                public void run() {
                    VectorParticipantsAdapter.this.listOtherMembers();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            VectorParticipantsAdapter.this.searchAccountKnownContacts(participantAdapterItem3, list2, z2, onParticipantsSearchListener2);
                        }
                    });
                }
            };
            Thread thread2 = new Thread(r2);
            thread2.setPriority(1);
            thread2.start();
            return;
        } else {
            ArrayList<ParticipantAdapterItem> arrayList2 = new ArrayList<>();
            synchronized (LOG_TAG) {
                if (this.mUnusedParticipants != null) {
                    arrayList2 = new ArrayList<>(this.mUnusedParticipants);
                }
            }
            for (ParticipantAdapterItem participantAdapterItem4 : arrayList2) {
                if (match(participantAdapterItem4, this.mPattern)) {
                    list.add(participantAdapterItem4);
                }
            }
        }
        onKnownContactsSearchEnd(list, participantAdapterItem, z, onParticipantsSearchListener);
    }

    private void onKnownContactsSearchEnd(List<ParticipantAdapterItem> list, ParticipantAdapterItem participantAdapterItem, boolean z, OnParticipantsSearchListener onParticipantsSearchListener) {
        ContactsManager.getInstance().retrievePids();
        if (!(this.mUsedMemberUserIds == null || participantAdapterItem == null || !this.mUsedMemberUserIds.contains(participantAdapterItem.mUserId))) {
            participantAdapterItem = null;
        }
        int i = 0;
        if (participantAdapterItem != null) {
            list.add(0, participantAdapterItem);
            int i2 = 1;
            while (true) {
                if (i2 >= list.size()) {
                    break;
                } else if (TextUtils.equals(((ParticipantAdapterItem) list.get(i2)).mUserId, participantAdapterItem.mUserId)) {
                    list.remove(i2);
                    break;
                } else {
                    i2++;
                }
            }
            this.mFirstEntry = participantAdapterItem;
        } else {
            this.mFirstEntry = null;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (ParticipantAdapterItem participantAdapterItem2 : list) {
            if (participantAdapterItem2 == this.mFirstEntry) {
                arrayList.add(this.mFirstEntry);
            } else if (participantAdapterItem2.mContact == null) {
                arrayList3.add(participantAdapterItem2);
            } else if (!this.mShowMatrixUserOnly || !participantAdapterItem2.mContact.getMatrixIdMediums().isEmpty()) {
                arrayList2.add(participantAdapterItem2);
            }
        }
        this.mFirstEntryPosition = -1;
        this.mParticipantsListsList.clear();
        if (arrayList.size() > 0) {
            this.mParticipantsListsList.add(arrayList);
            this.mFirstEntryPosition = 0;
        }
        if (ContactsManager.getInstance().isContactBookAccessAllowed()) {
            this.mLocalContactsSectionPosition = this.mFirstEntryPosition + 1;
            this.mKnownContactsSectionPosition = this.mLocalContactsSectionPosition + 1;
            if (arrayList2.size() > 0 || !ContactsManager.getInstance().arePIDsRetrieved() || this.mShowMatrixUserOnly || !TextUtils.isEmpty(this.mPattern)) {
                Collections.sort(arrayList2, ParticipantAdapterItem.alphaComparator);
            }
            this.mParticipantsListsList.add(arrayList2);
        } else {
            this.mKnownContactsSectionPosition = this.mFirstEntryPosition + 1;
        }
        if (!TextUtils.isEmpty(this.mPattern)) {
            if (arrayList3.size() > 0 && z) {
                Collections.sort(arrayList3, this.mSortMethod);
            }
            this.mParticipantsListsList.add(arrayList3);
        }
        if (onParticipantsSearchListener != null) {
            for (List size : this.mParticipantsListsList) {
                i += size.size();
            }
            onParticipantsSearchListener.onSearchEnd(i);
        }
        notifyDataSetChanged();
    }

    public void onGroupCollapsed(int i) {
        super.onGroupCollapsed(i);
        setGroupExpandedStatus(i, false);
    }

    public void onGroupExpanded(int i) {
        super.onGroupExpanded(i);
        setGroupExpandedStatus(i, true);
    }

    public boolean isChildSelectable(int i, int i2) {
        return i != 0 || ((ParticipantAdapterItem) getChild(i, i2)).mIsValid;
    }

    public int getGroupCount() {
        return this.mParticipantsListsList.size();
    }

    private boolean couldHaveUnusedParticipants() {
        boolean z = false;
        if (this.mUnusedParticipants != null) {
            if (this.mUnusedParticipants.size() != 0) {
                z = true;
            }
            return z;
        }
        for (Room members : this.mSession.getDataHandler().getStore().getRooms()) {
            if (members.getMembers().size() > 1) {
                return true;
            }
        }
        return false;
    }

    private String getGroupTitle(int i) {
        int i2;
        String str;
        if (i < this.mParticipantsListsList.size()) {
            i2 = ((List) this.mParticipantsListsList.get(i)).size();
        } else {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getGroupTitle position ");
            sb.append(i);
            sb.append(" is invalid, mParticipantsListsList.size()=");
            sb.append(this.mParticipantsListsList.size());
            Log.m211e(str2, sb.toString());
            i2 = 0;
        }
        if (i == this.mLocalContactsSectionPosition) {
            return this.mContext.getString(C1299R.string.people_search_local_contacts, new Object[]{Integer.valueOf(i2)});
        } else if (i != this.mKnownContactsSectionPosition) {
            return "??";
        } else {
            if (TextUtils.isEmpty(this.mPattern) && couldHaveUnusedParticipants()) {
                str = HelpFormatter.DEFAULT_OPT_PREFIX;
            } else if (this.mIsOfflineContactsSearch) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(this.mContext.getString(C1299R.string.offline));
                sb2.append(", ");
                sb2.append(String.valueOf(i2));
                str = sb2.toString();
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(this.mKnownContactsLimited ? ">" : "");
                sb3.append(String.valueOf(i2));
                str = sb3.toString();
            }
            return this.mContext.getString(C1299R.string.people_search_user_directory, new Object[]{str});
        }
    }

    public Object getGroup(int i) {
        return getGroupTitle(i);
    }

    public long getGroupId(int i) {
        return (long) getGroupTitle(i).hashCode();
    }

    public int getChildrenCount(int i) {
        if (i >= this.mParticipantsListsList.size()) {
            return 0;
        }
        return ((List) this.mParticipantsListsList.get(i)).size();
    }

    public Object getChild(int i, int i2) {
        if (i < this.mParticipantsListsList.size() && i >= 0) {
            List list = (List) this.mParticipantsListsList.get(i);
            if (i2 < list.size() && i2 >= 0) {
                return list.get(i2);
            }
        }
        return null;
    }

    public long getChildId(int i, int i2) {
        Object child = getChild(i, i2);
        if (child != null) {
            return (long) child.hashCode();
        }
        return 0;
    }

    public View getGroupView(final int i, boolean z, View view, final ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mHeaderLayoutResourceId, null);
        }
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.people_header_text_view);
        if (textView != null) {
            textView.setText(getGroupTitle(i));
        }
        View findViewById = view.findViewById(C1299R.C1301id.people_header_sub_layout);
        if (findViewById == null) {
            Log.m211e(LOG_TAG, "## getGroupView() : null subLayout");
            return view;
        }
        int i2 = 8;
        findViewById.setVisibility(i == this.mFirstEntryPosition ? 8 : 0);
        View findViewById2 = findViewById.findViewById(C1299R.C1301id.heading_loading_view);
        if (findViewById2 == null) {
            Log.m211e(LOG_TAG, "## getGroupView() : null loadingView");
            return view;
        }
        findViewById2.setVisibility((i != this.mLocalContactsSectionPosition || ContactsManager.getInstance().arePIDsRetrieved()) ? 8 : 0);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.heading_image);
        View findViewById3 = view.findViewById(C1299R.C1301id.people_header_matrix_contacts_layout);
        if (imageView == null || findViewById3 == null) {
            Log.m211e(LOG_TAG, "## getGroupView() : null UI items");
            return view;
        }
        if (i != this.mKnownContactsSectionPosition || ((List) this.mParticipantsListsList.get(i)).size() > 0) {
            if (z) {
                imageView.setImageResource(C1299R.C1300drawable.ic_material_expand_less_black);
            } else {
                imageView.setImageResource(C1299R.C1300drawable.ic_material_expand_more_black);
            }
            boolean isGroupExpanded = isGroupExpanded(i);
            if (viewGroup instanceof ExpandableListView) {
                ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                if (expandableListView.isGroupExpanded(i) != isGroupExpanded) {
                    if (isGroupExpanded) {
                        expandableListView.expandGroup(i);
                    } else {
                        expandableListView.collapseGroup(i);
                    }
                }
            }
            if (i == this.mLocalContactsSectionPosition && isGroupExpanded) {
                i2 = 0;
            }
            findViewById3.setVisibility(i2);
            CheckBox checkBox = (CheckBox) view.findViewById(C1299R.C1301id.contacts_filter_checkbox);
            checkBox.setChecked(PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(KEY_FILTER_MATRIX_USERS_ONLY, false));
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    VectorParticipantsAdapter.this.mShowMatrixUserOnly = z;
                    VectorParticipantsAdapter.this.refresh(VectorParticipantsAdapter.this.mFirstEntry, null);
                    Editor edit = PreferenceManager.getDefaultSharedPreferences(VectorParticipantsAdapter.this.mContext).edit();
                    edit.putBoolean(VectorParticipantsAdapter.KEY_FILTER_MATRIX_USERS_ONLY, z);
                    edit.apply();
                }
            });
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (!(viewGroup instanceof ExpandableListView)) {
                        return;
                    }
                    if (((ExpandableListView) viewGroup).isGroupExpanded(i)) {
                        ((ExpandableListView) viewGroup).collapseGroup(i);
                    } else {
                        ((ExpandableListView) viewGroup).expandGroup(i);
                    }
                }
            });
        } else {
            imageView.setImageDrawable(null);
            findViewById3.setVisibility(8);
        }
        return view;
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        int i3 = 0;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mCellLayoutResourceId, viewGroup, false);
        }
        if (i >= this.mParticipantsListsList.size()) {
            Log.m211e(LOG_TAG, "## getChildView() : invalid group position");
            return view;
        }
        List list = (List) this.mParticipantsListsList.get(i);
        if (i2 >= list.size()) {
            Log.m211e(LOG_TAG, "## getChildView() : invalid child position");
            return view;
        }
        ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) list.get(i2);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.filtered_list_avatar);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.filtered_list_name);
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.filtered_list_status);
        ImageView imageView2 = (ImageView) view.findViewById(C1299R.C1301id.filtered_list_matrix_user);
        if (imageView == null || textView == null || textView2 == null || imageView2 == null) {
            Log.m211e(LOG_TAG, "## getChildView() : some ui items are null");
            return view;
        }
        participantAdapterItem.displayAvatar(this.mSession, imageView);
        synchronized (LOG_TAG) {
            textView.setText(MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(participantAdapterItem.getUniqueDisplayName(this.mDisplayNamesList)).find() ? participantAdapterItem.getUniqueDisplayName(this.mDisplayNamesList).substring(1).split(":")[0] : participantAdapterItem.getUniqueDisplayName(this.mDisplayNamesList));
        }
        String str = "";
        if (i == this.mKnownContactsSectionPosition) {
            Iterator it = Matrix.getMXSessions(this.mContext).iterator();
            User user = null;
            MXSession mXSession = null;
            while (it.hasNext()) {
                MXSession mXSession2 = (MXSession) it.next();
                if (user == null) {
                    user = mXSession2.getDataHandler().getUser(participantAdapterItem.mUserId);
                    mXSession = mXSession2;
                }
            }
            if (user != null) {
                str = VectorUtils.getUserOnlineStatus(this.mContext, mXSession, participantAdapterItem.mUserId, new SimpleApiCallback<Void>() {
                    public void onSuccess(Void voidR) {
                        VectorParticipantsAdapter.this.refresh(VectorParticipantsAdapter.this.mFirstEntry, null);
                    }
                });
            }
        }
        if (participantAdapterItem.mContact != null) {
            imageView2.setVisibility(MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(participantAdapterItem.mUserId).matches() ? 0 : 8);
            if (participantAdapterItem.mContact.getEmails().size() > 0) {
                textView2.setText((CharSequence) participantAdapterItem.mContact.getEmails().get(0));
            } else {
                textView2.setText(((PhoneNumber) participantAdapterItem.mContact.getPhonenumbers().get(0)).mRawPhoneNumber);
            }
        } else {
            textView2.setText(str);
            imageView2.setVisibility(8);
        }
        view.setAlpha(participantAdapterItem.mIsValid ? 1.0f : 0.5f);
        ((CheckBox) view.findViewById(C1299R.C1301id.filtered_list_checkbox)).setVisibility(8);
        View findViewById = view.findViewById(C1299R.C1301id.filtered_list_add_button);
        if (!this.mWithAddIcon) {
            i3 = 8;
        }
        findViewById.setVisibility(i3);
        return view;
    }

    private void resetGroupExpansionPreferences() {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.remove(KEY_EXPAND_STATE_SEARCH_LOCAL_CONTACTS_GROUP);
        edit.remove(KEY_EXPAND_STATE_SEARCH_MATRIX_CONTACTS_GROUP);
        edit.remove(KEY_FILTER_MATRIX_USERS_ONLY);
        edit.apply();
    }

    private boolean isGroupExpanded(int i) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        if (i == this.mLocalContactsSectionPosition) {
            return defaultSharedPreferences.getBoolean(KEY_EXPAND_STATE_SEARCH_LOCAL_CONTACTS_GROUP, true);
        }
        if (i == this.mKnownContactsSectionPosition) {
            return defaultSharedPreferences.getBoolean(KEY_EXPAND_STATE_SEARCH_MATRIX_CONTACTS_GROUP, true);
        }
        return true;
    }

    private void setGroupExpandedStatus(int i, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        if (i == this.mLocalContactsSectionPosition) {
            edit.putBoolean(KEY_EXPAND_STATE_SEARCH_LOCAL_CONTACTS_GROUP, z);
        } else if (i == this.mKnownContactsSectionPosition) {
            edit.putBoolean(KEY_EXPAND_STATE_SEARCH_MATRIX_CONTACTS_GROUP, z);
        }
        edit.apply();
    }
}
