package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.FragmentPagerAdapter;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.fragments.GroupDetailsBaseFragment;
import com.opengarden.firechat.fragments.GroupDetailsHomeFragment;
import com.opengarden.firechat.fragments.GroupDetailsPeopleFragment;
import com.opengarden.firechat.fragments.GroupDetailsRoomsFragment;

public class GroupDetailsFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int FRAGMENTS_COUNT = 3;
    private static final int HOME_FRAGMENT_INDEX = 0;
    private static final String LOG_TAG = "GroupDetailsFragmentPagerAdapter";
    private static final int PEOPLE_FRAGMENT_INDEX = 1;
    private static final int ROOMS_FRAGMENT_INDEX = 2;
    private final Context mContext;
    private GroupDetailsHomeFragment mHomeFragment;
    private GroupDetailsPeopleFragment mPeopleFragment;
    private GroupDetailsRoomsFragment mRoomsFragment;

    public int getCount() {
        return 3;
    }

    public GroupDetailsFragmentPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.mContext = context;
    }

    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                GroupDetailsHomeFragment groupDetailsHomeFragment = this.mHomeFragment;
                if (groupDetailsHomeFragment != null) {
                    return groupDetailsHomeFragment;
                }
                GroupDetailsHomeFragment groupDetailsHomeFragment2 = new GroupDetailsHomeFragment();
                this.mHomeFragment = groupDetailsHomeFragment2;
                return groupDetailsHomeFragment2;
            case 1:
                GroupDetailsPeopleFragment groupDetailsPeopleFragment = this.mPeopleFragment;
                if (groupDetailsPeopleFragment != null) {
                    return groupDetailsPeopleFragment;
                }
                GroupDetailsPeopleFragment groupDetailsPeopleFragment2 = new GroupDetailsPeopleFragment();
                this.mPeopleFragment = groupDetailsPeopleFragment2;
                return groupDetailsPeopleFragment2;
            case 2:
                GroupDetailsRoomsFragment groupDetailsRoomsFragment = this.mRoomsFragment;
                if (groupDetailsRoomsFragment != null) {
                    return groupDetailsRoomsFragment;
                }
                GroupDetailsRoomsFragment groupDetailsRoomsFragment2 = new GroupDetailsRoomsFragment();
                this.mRoomsFragment = groupDetailsRoomsFragment2;
                return groupDetailsRoomsFragment2;
            default:
                return null;
        }
    }

    public CharSequence getPageTitle(int i) {
        switch (i) {
            case 0:
                return this.mContext.getString(C1299R.string.group_details_home);
            case 1:
                return this.mContext.getString(C1299R.string.group_details_people);
            case 2:
                return this.mContext.getString(C1299R.string.group_details_rooms);
            default:
                return super.getPageTitle(i);
        }
    }

    public GroupDetailsBaseFragment getHomeFragment() {
        return this.mHomeFragment;
    }

    public GroupDetailsBaseFragment getPeopleFragment() {
        return this.mPeopleFragment;
    }

    public GroupDetailsBaseFragment getRoomsFragment() {
        return this.mRoomsFragment;
    }
}
