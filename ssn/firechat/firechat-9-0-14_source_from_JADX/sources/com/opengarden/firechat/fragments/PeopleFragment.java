package com.opengarden.firechat.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.content.ContextCompat;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter.FilterListener;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.adapters.PeopleAdapter;
import com.opengarden.firechat.adapters.PeopleAdapter.OnSelectItemListener;
import com.opengarden.firechat.contacts.Contact;
import com.opengarden.firechat.contacts.Contact.MXID;
import com.opengarden.firechat.contacts.Contact.PhoneNumber;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.ContactsManager.ContactsManagerListener;
import com.opengarden.firechat.contacts.PIDsRetriever;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnRoomChangedListener;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.search.SearchUsersResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeopleFragment extends AbsHomeFragment implements ContactsManagerListener, OnRoomChangedListener {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PeopleFragment";
    private static final String MATRIX_USER_ONLY_PREF_KEY = "MATRIX_USER_ONLY_PREF_KEY";
    private static final int MAX_KNOWN_CONTACTS_FILTER_COUNT = 50;
    /* access modifiers changed from: private */
    public PeopleAdapter mAdapter;
    private int mContactsSnapshotSession = -1;
    private final List<Room> mDirectChats = new ArrayList();
    private MXEventListener mEventsListener;
    /* access modifiers changed from: private */
    public final List<ParticipantAdapterItem> mKnownContacts = new ArrayList();
    private final List<ParticipantAdapterItem> mLocalContacts = new ArrayList();
    /* access modifiers changed from: private */
    public CheckBox mMatrixUserOnlyCheckbox;
    @BindView(2131296889)
    RecyclerView mRecycler;

    public void onContactPresenceUpdate(Contact contact, String str) {
    }

    public static PeopleFragment newInstance() {
        return new PeopleFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_people, viewGroup, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mEventsListener = new MXEventListener() {
            public void onPresenceUpdate(Event event, User user) {
                PeopleFragment.this.mAdapter.updateKnownContact(user);
            }
        };
        this.mPrimaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_people);
        this.mSecondaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_people_secondary);
        initViews();
        this.mOnRoomChangedListener = this;
        this.mMatrixUserOnlyCheckbox.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(MATRIX_USER_ONLY_PREF_KEY, false));
        this.mAdapter.onFilterDone(this.mCurrentFilter);
        if (!ContactsManager.getInstance().isContactBookAccessRequested()) {
            CommonActivityUtils.checkPermissions(8, (Fragment) this);
        }
        initKnownContacts();
    }

    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventsListener);
        ContactsManager.getInstance().addListener(this);
        initDirectChatsData();
        initDirectChatsViews();
        initContactsData();
        initContactsViews();
        this.mAdapter.setInvitation(this.mActivity.getRoomInvitations());
        this.mRecycler.addOnScrollListener(this.mScrollListener);
    }

    public void onPause() {
        super.onPause();
        if (this.mSession.isAlive()) {
            this.mSession.getDataHandler().removeListener(this.mEventsListener);
        }
        ContactsManager.getInstance().removeListener(this);
        this.mRecycler.removeOnScrollListener(this.mScrollListener);
        this.mSession.cancelUsersSearch();
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (i == 8) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                initContactsData();
            } else {
                ContactsManager.getInstance().refreshLocalContactsSnapshot();
            }
            initContactsViews();
        }
    }

    /* access modifiers changed from: protected */
    public List<Room> getRooms() {
        return new ArrayList(this.mDirectChats);
    }

    /* access modifiers changed from: protected */
    public void onFilter(final String str, final OnFilterListener onFilterListener) {
        this.mAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                boolean z = TextUtils.isEmpty(PeopleFragment.this.mCurrentFilter) && !TextUtils.isEmpty(str);
                String access$100 = PeopleFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onFilterComplete ");
                sb.append(i);
                Log.m213i(access$100, sb.toString());
                if (onFilterListener != null) {
                    onFilterListener.onFilterDone(i);
                }
                PeopleFragment.this.startRemoteKnownContactsSearch(z);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResetFilter() {
        this.mAdapter.getFilter().filter("", new FilterListener() {
            public void onFilterComplete(int i) {
                String access$100 = PeopleFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onResetFilter ");
                sb.append(i);
                Log.m213i(access$100, sb.toString());
            }
        });
    }

    private void initViews() {
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 1, dimension));
        RecyclerView recyclerView = this.mRecycler;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(getActivity(), 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        this.mAdapter = new PeopleAdapter(getActivity(), new OnSelectItemListener() {
            public void onSelectItem(Room room, int i) {
                PeopleFragment.this.openRoom(room);
            }

            public void onSelectItem(ParticipantAdapterItem participantAdapterItem, int i) {
                PeopleFragment.this.onContactSelected(participantAdapterItem);
            }
        }, this, this);
        this.mRecycler.setAdapter(this.mAdapter);
        View findSectionSubViewById = this.mAdapter.findSectionSubViewById(C1299R.C1301id.matrix_only_filter_checkbox);
        if (findSectionSubViewById != null && (findSectionSubViewById instanceof CheckBox)) {
            this.mMatrixUserOnlyCheckbox = (CheckBox) findSectionSubViewById;
            this.mMatrixUserOnlyCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    PreferenceManager.getDefaultSharedPreferences(PeopleFragment.this.getActivity()).edit().putBoolean(PeopleFragment.MATRIX_USER_ONLY_PREF_KEY, PeopleFragment.this.mMatrixUserOnlyCheckbox.isChecked()).apply();
                    PeopleFragment.this.initContactsViews();
                }
            });
        }
    }

    private void initDirectChatsData() {
        if (this.mSession == null || this.mSession.getDataHandler() == null) {
            Log.m211e(LOG_TAG, "## initDirectChatsData() : null session");
        }
        List<String> directChatRoomIdsList = this.mSession.getDataHandler().getDirectChatRoomIdsList();
        MXDataHandler dataHandler = this.mSession.getDataHandler();
        IMXStore store = dataHandler.getStore();
        this.mDirectChats.clear();
        if (directChatRoomIdsList != null && !directChatRoomIdsList.isEmpty()) {
            for (String str : directChatRoomIdsList) {
                Room room = store.getRoom(str);
                if (room != null && !room.isConferenceUserRoom()) {
                    if (room.getMember(this.mSession.getMyUserId()) == null) {
                        String str2 = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## initDirectChatsData(): invalid room ");
                        sb.append(room.getRoomId());
                        sb.append(", the user is not anymore member of it");
                        Log.m211e(str2, sb.toString());
                    } else {
                        Set keys = room.getAccountData().getKeys();
                        if (keys == null || !keys.contains(RoomTag.ROOM_TAG_LOW_PRIORITY)) {
                            this.mDirectChats.add(dataHandler.getRoom(str));
                        }
                    }
                }
            }
        }
    }

    private void initContactsData() {
        ContactsManager.getInstance().retrievePids();
        if (this.mContactsSnapshotSession == -1 || this.mContactsSnapshotSession != ContactsManager.getInstance().getLocalContactsSnapshotSession() || !ContactsManager.getInstance().didPopulateLocalContacts()) {
            this.mLocalContacts.clear();
            for (ParticipantAdapterItem participantAdapterItem : new ArrayList(getContacts())) {
                if (participantAdapterItem.mContact != null) {
                    this.mLocalContacts.add(participantAdapterItem);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void initKnownContacts() {
        C20586 r0 = new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... voidArr) {
                PeopleFragment.this.mKnownContacts.clear();
                PeopleFragment.this.mKnownContacts.addAll(new ArrayList(VectorUtils.listKnownParticipants(PeopleFragment.this.mSession).values()));
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void voidR) {
                PeopleFragment.this.mAdapter.setKnownContacts(PeopleFragment.this.mKnownContacts);
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## initKnownContacts() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            r0.cancel(true);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    PeopleFragment.this.initKnownContacts();
                }
            }, 1000);
        }
    }

    private void showKnownContactLoadingView() {
        this.mAdapter.getSectionViewForSectionIndex(this.mAdapter.getSectionsCount() - 1).showLoadingView();
    }

    /* access modifiers changed from: private */
    public void hideKnownContactLoadingView() {
        this.mAdapter.getSectionViewForSectionIndex(this.mAdapter.getSectionsCount() - 1).hideLoadingView();
    }

    /* access modifiers changed from: private */
    public void startRemoteKnownContactsSearch(boolean z) {
        if (!TextUtils.isEmpty(this.mCurrentFilter)) {
            if (z) {
                this.mAdapter.setFilteredKnownContacts(new ArrayList(), this.mCurrentFilter);
                showKnownContactLoadingView();
            }
            final String str = this.mCurrentFilter;
            this.mSession.searchUsers(this.mCurrentFilter, Integer.valueOf(50), new HashSet(), new ApiCallback<SearchUsersResponse>() {
                public void onSuccess(SearchUsersResponse searchUsersResponse) {
                    if (TextUtils.equals(str, PeopleFragment.this.mCurrentFilter)) {
                        PeopleFragment.this.hideKnownContactLoadingView();
                        ArrayList arrayList = new ArrayList();
                        if (searchUsersResponse.results != null) {
                            for (User participantAdapterItem : searchUsersResponse.results) {
                                arrayList.add(new ParticipantAdapterItem(participantAdapterItem));
                            }
                        }
                        PeopleFragment.this.mAdapter.setKnownContactsExtraTitle(null);
                        PeopleFragment.this.mAdapter.setKnownContactsLimited(searchUsersResponse.limited != null ? searchUsersResponse.limited.booleanValue() : false);
                        PeopleFragment.this.mAdapter.setFilteredKnownContacts(arrayList, PeopleFragment.this.mCurrentFilter);
                    }
                }

                private void onError(String str) {
                    String access$100 = PeopleFragment.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## startRemoteKnownContactsSearch() : failed ");
                    sb.append(str);
                    Log.m211e(access$100, sb.toString());
                    if (TextUtils.equals(str, PeopleFragment.this.mCurrentFilter)) {
                        PeopleFragment.this.hideKnownContactLoadingView();
                        PeopleFragment.this.mAdapter.setKnownContactsExtraTitle(PeopleFragment.this.getContext().getString(C1299R.string.offline));
                        PeopleFragment.this.mAdapter.filterAccountKnownContacts(PeopleFragment.this.mCurrentFilter);
                    }
                }

                public void onNetworkError(Exception exc) {
                    onError(exc.getMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError(matrixError.getMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onError(exc.getMessage());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onContactSelected(ParticipantAdapterItem participantAdapterItem) {
        if (participantAdapterItem.mIsValid) {
            Intent intent = new Intent(getActivity(), VectorMemberDetailsActivity.class);
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, participantAdapterItem.mUserId);
            if (!TextUtils.isEmpty(participantAdapterItem.mAvatarUrl)) {
                intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_AVATAR_URL, participantAdapterItem.mAvatarUrl);
            }
            if (!TextUtils.isEmpty(participantAdapterItem.mDisplayName)) {
                intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_DISPLAY_NAME, participantAdapterItem.mDisplayName);
            }
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            startActivity(intent);
        }
    }

    private List<ParticipantAdapterItem> getContacts() {
        ArrayList arrayList = new ArrayList();
        Collection<Contact> localContactsSnapshot = ContactsManager.getInstance().getLocalContactsSnapshot();
        this.mContactsSnapshotSession = ContactsManager.getInstance().getLocalContactsSnapshotSession();
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
                        arrayList.add(participantAdapterItem);
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
                        arrayList.add(participantAdapterItem2);
                    }
                }
            }
        }
        return arrayList;
    }

    private List<ParticipantAdapterItem> getMatrixUsers() {
        ArrayList arrayList = new ArrayList();
        for (ParticipantAdapterItem participantAdapterItem : this.mLocalContacts) {
            if (!participantAdapterItem.mContact.getMatrixIdMediums().isEmpty()) {
                arrayList.add(participantAdapterItem);
            }
        }
        return arrayList;
    }

    private void initDirectChatsViews() {
        this.mAdapter.setRooms(this.mDirectChats);
    }

    /* access modifiers changed from: private */
    public void initContactsViews() {
        this.mAdapter.setLocalContacts((this.mMatrixUserOnlyCheckbox == null || !this.mMatrixUserOnlyCheckbox.isChecked()) ? this.mLocalContacts : getMatrixUsers());
    }

    public void onSummariesUpdate() {
        super.onSummariesUpdate();
        if (isResumed()) {
            this.mAdapter.setInvitation(this.mActivity.getRoomInvitations());
            initDirectChatsData();
            initDirectChatsViews();
        }
    }

    public void onRefresh() {
        initContactsData();
        initContactsViews();
    }

    public void onPIDsUpdate() {
        List contacts = getContacts();
        if (!this.mLocalContacts.containsAll(contacts)) {
            this.mLocalContacts.clear();
            this.mLocalContacts.addAll(contacts);
            initContactsViews();
        }
    }

    public void onToggleDirectChat(String str, boolean z) {
        if (!z) {
            this.mAdapter.removeDirectChat(str);
        }
    }

    public void onRoomLeft(String str) {
        this.mAdapter.removeDirectChat(str);
    }

    public void onRoomForgot(String str) {
        this.mAdapter.removeDirectChat(str);
    }
}
