package com.opengarden.firechat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.p000v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.VectorBaseSearchActivity.IVectorSearchActivity;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.adapters.VectorParticipantsAdapter;
import com.opengarden.firechat.adapters.VectorParticipantsAdapter.OnParticipantsSearchListener;
import com.opengarden.firechat.contacts.Contact;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.ContactsManager.ContactsManagerListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.util.VectorUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VectorSearchPeopleListFragment extends Fragment {
    private static final String ARG_LAYOUT_ID = "VectorSearchPeopleListFragment.ARG_LAYOUT_ID";
    private static final String ARG_MATRIX_ID = "VectorSearchPeopleListFragment.ARG_MATRIX_ID";
    /* access modifiers changed from: private */
    public VectorParticipantsAdapter mAdapter;
    private final ContactsManagerListener mContactsListener = new ContactsManagerListener() {
        public void onContactPresenceUpdate(Contact contact, String str) {
        }

        public void onRefresh() {
            if (VectorSearchPeopleListFragment.this.getActivity() != null) {
                VectorSearchPeopleListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (VectorSearchPeopleListFragment.this.getActivity() instanceof IVectorSearchActivity) {
                            ((IVectorSearchActivity) VectorSearchPeopleListFragment.this.getActivity()).refreshSearch();
                        }
                    }
                });
            }
        }

        public void onPIDsUpdate() {
            if (VectorSearchPeopleListFragment.this.getActivity() != null) {
                VectorSearchPeopleListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorSearchPeopleListFragment.this.mAdapter.onPIdsUpdate();
                    }
                });
            }
        }
    };
    private final MXEventListener mEventsListener = new MXEventListener() {
        public void onPresenceUpdate(Event event, final User user) {
            if (VectorSearchPeopleListFragment.this.getActivity() != null) {
                VectorSearchPeopleListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        HashMap visibleChildViews = VectorUtils.getVisibleChildViews(VectorSearchPeopleListFragment.this.mPeopleListView, VectorSearchPeopleListFragment.this.mAdapter);
                        for (Integer num : visibleChildViews.keySet()) {
                            Iterator it = ((List) visibleChildViews.get(num)).iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                Object child = VectorSearchPeopleListFragment.this.mAdapter.getChild(num.intValue(), ((Integer) it.next()).intValue());
                                if (child instanceof ParticipantAdapterItem) {
                                    if (TextUtils.equals(user.user_id, ((ParticipantAdapterItem) child).mUserId)) {
                                        VectorSearchPeopleListFragment.this.mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    };
    /* access modifiers changed from: private */
    public ExpandableListView mPeopleListView;
    /* access modifiers changed from: private */
    public MXSession mSession;

    public static VectorSearchPeopleListFragment newInstance(String str, int i) {
        VectorSearchPeopleListFragment vectorSearchPeopleListFragment = new VectorSearchPeopleListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_LAYOUT_ID, i);
        bundle.putString(ARG_MATRIX_ID, str);
        vectorSearchPeopleListFragment.setArguments(bundle);
        return vectorSearchPeopleListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        this.mSession = Matrix.getInstance(getActivity()).getSession(arguments.getString(ARG_MATRIX_ID));
        if (this.mSession == null || !this.mSession.isAlive()) {
            throw new RuntimeException("Must have valid default MXSession.");
        }
        View inflate = layoutInflater.inflate(arguments.getInt(ARG_LAYOUT_ID), viewGroup, false);
        this.mPeopleListView = (ExpandableListView) inflate.findViewById(C1299R.C1301id.search_people_list);
        this.mPeopleListView.setGroupIndicator(null);
        VectorParticipantsAdapter vectorParticipantsAdapter = new VectorParticipantsAdapter(getActivity(), C1299R.layout.adapter_item_vector_add_participants, C1299R.layout.adapter_item_vector_people_header, this.mSession, null, false);
        this.mAdapter = vectorParticipantsAdapter;
        this.mPeopleListView.setAdapter(this.mAdapter);
        this.mPeopleListView.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                Object child = VectorSearchPeopleListFragment.this.mAdapter.getChild(i, i2);
                if (child instanceof ParticipantAdapterItem) {
                    ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) child;
                    if (participantAdapterItem.mIsValid) {
                        Intent intent = new Intent(VectorSearchPeopleListFragment.this.getActivity(), VectorMemberDetailsActivity.class);
                        intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, participantAdapterItem.mUserId);
                        if (!TextUtils.isEmpty(participantAdapterItem.mAvatarUrl)) {
                            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_AVATAR_URL, participantAdapterItem.mAvatarUrl);
                        }
                        if (!TextUtils.isEmpty(participantAdapterItem.mDisplayName)) {
                            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_DISPLAY_NAME, participantAdapterItem.mDisplayName);
                        }
                        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorSearchPeopleListFragment.this.mSession.getCredentials().userId);
                        VectorSearchPeopleListFragment.this.startActivity(intent);
                    }
                }
                return true;
            }
        });
        return inflate;
    }

    public boolean isReady() {
        return ContactsManager.getInstance().didPopulateLocalContacts() && this.mAdapter.isKnownMembersInitialized();
    }

    public void searchPattern(final String str, final OnSearchResultListener onSearchResultListener) {
        if (this.mPeopleListView != null) {
            if (!ContactsManager.getInstance().didPopulateLocalContacts()) {
                this.mAdapter.reset();
                return;
            }
            ParticipantAdapterItem participantAdapterItem = null;
            if (!TextUtils.isEmpty(str)) {
                participantAdapterItem = new ParticipantAdapterItem(str, null, str, Patterns.EMAIL_ADDRESS.matcher(str).matches() || MXSession.isUserId(str));
            }
            this.mAdapter.setSearchedPattern(str, participantAdapterItem, new OnParticipantsSearchListener() {
                public void onSearchEnd(final int i) {
                    VectorSearchPeopleListFragment.this.mPeopleListView.post(new Runnable() {
                        public void run() {
                            VectorSearchPeopleListFragment.this.mPeopleListView.setVisibility((i != 0 || TextUtils.isEmpty(str)) ? 0 : 4);
                            onSearchResultListener.onSearchSucceed(i);
                        }
                    });
                }
            });
        }
    }

    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mEventsListener);
        ContactsManager.getInstance().removeListener(this.mContactsListener);
    }

    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventsListener);
        ContactsManager.getInstance().addListener(this.mContactsListener);
    }
}
