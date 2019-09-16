package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.FragmentPagerAdapter;
import android.support.p000v4.util.Pair;
import android.support.p000v4.util.SparseArrayCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.fragments.VectorSearchMessagesListFragment;
import com.opengarden.firechat.fragments.VectorSearchPeopleListFragment;
import com.opengarden.firechat.fragments.VectorSearchRoomsFilesListFragment;
import com.opengarden.firechat.fragments.VectorSearchRoomsListFragment;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment.OnSearchResultListener;

public class VectorUnifiedSearchFragmentPagerAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private final SparseArrayCompat<Pair<Integer, Fragment>> mFragmentsData = new SparseArrayCompat<>();
    private final String mRoomId;
    private final MXSession mSession;

    public VectorUnifiedSearchFragmentPagerAdapter(FragmentManager fragmentManager, Context context, MXSession mXSession, String str) {
        super(fragmentManager);
        this.mContext = context;
        this.mSession = mXSession;
        this.mRoomId = str;
        boolean z = !TextUtils.isEmpty(str);
        int i = 0;
        if (!z) {
            this.mFragmentsData.put(0, new Pair(Integer.valueOf(C1299R.string.tab_title_search_rooms), null));
            i = 1;
        }
        this.mFragmentsData.put(i, new Pair(Integer.valueOf(C1299R.string.tab_title_search_messages), null));
        int i2 = i + 1;
        if (!z) {
            this.mFragmentsData.put(i2, new Pair(Integer.valueOf(C1299R.string.tab_title_search_people), null));
            i2++;
        }
        this.mFragmentsData.put(i2, new Pair(Integer.valueOf(C1299R.string.tab_title_search_files), null));
    }

    public int getCount() {
        return this.mFragmentsData.size();
    }

    public Fragment getItem(int i) {
        int i2;
        Fragment fragment;
        Pair pair = (Pair) this.mFragmentsData.get(i);
        if (pair == null) {
            i2 = -1;
        } else {
            i2 = ((Integer) pair.first).intValue();
        }
        if (pair == null) {
            fragment = null;
        } else {
            fragment = (Fragment) pair.second;
        }
        if (fragment == null) {
            switch (i2) {
                case C1299R.string.tab_title_search_files /*2131690319*/:
                    fragment = VectorSearchRoomsFilesListFragment.newInstance(this.mSession.getMyUserId(), this.mRoomId, C1299R.layout.fragment_matrix_message_list_fragment);
                    break;
                case C1299R.string.tab_title_search_messages /*2131690320*/:
                    fragment = VectorSearchMessagesListFragment.newInstance(this.mSession.getMyUserId(), this.mRoomId, C1299R.layout.fragment_matrix_message_list_fragment);
                    break;
                case C1299R.string.tab_title_search_people /*2131690321*/:
                    fragment = VectorSearchPeopleListFragment.newInstance(this.mSession.getMyUserId(), C1299R.layout.fragment_vector_search_people_list);
                    break;
                case C1299R.string.tab_title_search_rooms /*2131690322*/:
                    fragment = VectorSearchRoomsListFragment.newInstance(this.mSession.getMyUserId(), C1299R.layout.fragment_vector_recents_list);
                    break;
            }
            if (fragment == null) {
                return null;
            }
        }
        return fragment;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        Fragment fragment = (Fragment) super.instantiateItem(viewGroup, i);
        Pair pair = (Pair) this.mFragmentsData.get(i);
        if (pair != null) {
            this.mFragmentsData.put(i, new Pair(pair.first, fragment));
        }
        return fragment;
    }

    public CharSequence getPageTitle(int i) {
        if (this.mFragmentsData == null || this.mFragmentsData.get(i) == null) {
            return super.getPageTitle(i);
        }
        return this.mContext.getResources().getString(((Integer) ((Pair) this.mFragmentsData.get(i)).first).intValue());
    }

    public void cancelSearch(int i) {
        int i2;
        Fragment fragment;
        Pair pair = (Pair) this.mFragmentsData.get(i);
        if (pair == null) {
            i2 = -1;
        } else {
            i2 = ((Integer) pair.first).intValue();
        }
        if (pair == null) {
            fragment = null;
        } else {
            fragment = (Fragment) pair.second;
        }
        if (fragment != null) {
            if (i2 == C1299R.string.tab_title_search_messages) {
                ((VectorSearchMessagesListFragment) fragment).cancelCatchingRequests();
            } else if (i2 == C1299R.string.tab_title_search_files) {
                ((VectorSearchRoomsFilesListFragment) fragment).cancelCatchingRequests();
            }
        }
    }

    public boolean search(int i, String str, OnSearchResultListener onSearchResultListener) {
        int i2;
        Fragment fragment;
        boolean z = false;
        if (this.mFragmentsData == null) {
            onSearchResultListener.onSearchSucceed(0);
            return false;
        }
        Pair pair = (Pair) this.mFragmentsData.get(i);
        if (pair == null) {
            i2 = -1;
        } else {
            i2 = ((Integer) pair.first).intValue();
        }
        if (pair == null) {
            fragment = null;
        } else {
            fragment = (Fragment) pair.second;
        }
        if (fragment == null) {
            onSearchResultListener.onSearchSucceed(0);
            return false;
        }
        switch (i2) {
            case C1299R.string.tab_title_search_files /*2131690319*/:
                z = !TextUtils.isEmpty(str);
                ((VectorSearchRoomsFilesListFragment) fragment).searchPattern(str, onSearchResultListener);
                break;
            case C1299R.string.tab_title_search_messages /*2131690320*/:
                z = !TextUtils.isEmpty(str);
                ((VectorSearchMessagesListFragment) fragment).searchPattern(str, onSearchResultListener);
                break;
            case C1299R.string.tab_title_search_people /*2131690321*/:
                VectorSearchPeopleListFragment vectorSearchPeopleListFragment = (VectorSearchPeopleListFragment) fragment;
                z = vectorSearchPeopleListFragment.isReady();
                vectorSearchPeopleListFragment.searchPattern(str, onSearchResultListener);
                break;
            case C1299R.string.tab_title_search_rooms /*2131690322*/:
                z = PublicRoomsManager.getInstance().isRequestInProgress();
                ((VectorSearchRoomsListFragment) fragment).searchPattern(str, onSearchResultListener);
                break;
        }
        return z;
    }

    public int getPermissionsRequest(int i) {
        int i2;
        if (this.mFragmentsData != null) {
            Pair pair = (Pair) this.mFragmentsData.get(i);
            if (pair == null) {
                i2 = -1;
            } else {
                i2 = ((Integer) pair.first).intValue();
            }
            if (i2 == C1299R.string.tab_title_search_people) {
                return 8;
            }
        }
        return 0;
    }

    public boolean isSearchInRoomNameFragment(int i) {
        Pair pair = this.mFragmentsData != null ? (Pair) this.mFragmentsData.get(i) : null;
        return pair != null && C1299R.string.tab_title_search_rooms == ((Integer) pair.first).intValue();
    }

    public boolean isSearchInMessagesFragment(int i) {
        Pair pair = this.mFragmentsData != null ? (Pair) this.mFragmentsData.get(i) : null;
        return pair != null && C1299R.string.tab_title_search_messages == ((Integer) pair.first).intValue();
    }

    public boolean isSearchInFilesFragment(int i) {
        Pair pair = this.mFragmentsData != null ? (Pair) this.mFragmentsData.get(i) : null;
        return pair != null && C1299R.string.tab_title_search_files == ((Integer) pair.first).intValue();
    }

    public boolean isSearchInPeoplesFragment(int i) {
        Pair pair = this.mFragmentsData != null ? (Pair) this.mFragmentsData.get(i) : null;
        return pair != null && C1299R.string.tab_title_search_people == ((Integer) pair.first).intValue();
    }
}
