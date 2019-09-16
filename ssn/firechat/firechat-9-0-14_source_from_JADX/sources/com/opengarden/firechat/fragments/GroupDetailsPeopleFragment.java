package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter.FilterListener;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.GroupDetailsPeopleAdapter;
import com.opengarden.firechat.adapters.GroupDetailsPeopleAdapter.OnSelectUserListener;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupUser;
import com.opengarden.firechat.util.GroupUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;

public class GroupDetailsPeopleFragment extends GroupDetailsBaseFragment {
    /* access modifiers changed from: private */
    public GroupDetailsPeopleAdapter mAdapter;
    /* access modifiers changed from: private */
    public String mCurrentFilter;
    @BindView(2131296889)
    RecyclerView mRecycler;
    @BindView(2131297010)
    SearchView mSearchView;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_group_details_people, viewGroup, false);
    }

    public void onResume() {
        super.onResume();
        refreshViews();
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mCurrentFilter = this.mSearchView.getQuery().toString();
        this.mAdapter.onFilterDone(this.mCurrentFilter);
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 1, dimension));
        RecyclerView recyclerView = this.mRecycler;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(getActivity(), 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        this.mAdapter = new GroupDetailsPeopleAdapter(getActivity(), new OnSelectUserListener() {
            public void onSelectItem(GroupUser groupUser, int i) {
                GroupUtils.openGroupUserPage(GroupDetailsPeopleFragment.this.mActivity, GroupDetailsPeopleFragment.this.mSession, groupUser);
            }
        });
        this.mRecycler.setAdapter(this.mAdapter);
        this.mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return true;
            }

            public boolean onQueryTextChange(final String str) {
                if (!TextUtils.equals(GroupDetailsPeopleFragment.this.mCurrentFilter, str)) {
                    GroupDetailsPeopleFragment.this.mAdapter.getFilter().filter(str, new FilterListener() {
                        public void onFilterComplete(int i) {
                            GroupDetailsPeopleFragment.this.mCurrentFilter = str;
                        }
                    });
                }
                return true;
            }
        });
        this.mSearchView.setMaxWidth(Integer.MAX_VALUE);
        this.mSearchView.setQueryHint(getString(C1299R.string.filter_group_members));
        this.mSearchView.setFocusable(false);
        this.mSearchView.setIconifiedByDefault(false);
        this.mSearchView.clearFocus();
    }

    public void refreshViews() {
        this.mAdapter.setJoinedGroupUsers(this.mActivity.getGroup().getGroupUsers().getUsers());
        this.mAdapter.setInvitedGroupUsers(this.mActivity.getGroup().getInvitedGroupUsers().getUsers());
    }
}
