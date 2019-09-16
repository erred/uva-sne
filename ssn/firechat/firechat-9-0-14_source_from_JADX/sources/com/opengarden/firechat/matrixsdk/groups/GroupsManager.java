package com.opengarden.firechat.matrixsdk.groups;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.GroupsRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.group.CreateGroupParams;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupProfile;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupRooms;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupSummary;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupSyncProfile;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupUsers;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroupsManager {
    private static final int GROUP_REFRESH_STEP_INVITED_USERS_LIST = 3;
    private static final int GROUP_REFRESH_STEP_PROFILE = 0;
    private static final int GROUP_REFRESH_STEP_ROOMS_LIST = 1;
    private static final int GROUP_REFRESH_STEP_USERS_LIST = 2;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "GroupsManager";
    /* access modifiers changed from: private */
    public MXDataHandler mDataHandler;
    Map<String, GroupProfile> mGroupProfileByGroupId = new HashMap();
    Map<String, List<ApiCallback<GroupProfile>>> mGroupProfileCallback = new HashMap();
    private GroupsRestClient mGroupsRestClient;
    /* access modifiers changed from: private */
    public final Map<String, ApiCallback<Void>> mPendingJoinGroups = new HashMap();
    /* access modifiers changed from: private */
    public final Map<String, ApiCallback<Void>> mPendingLeaveGroups = new HashMap();
    /* access modifiers changed from: private */
    public Map<String, Set<ApiCallback<Set<String>>>> mPendingPubliciseRequests = new HashMap();
    /* access modifiers changed from: private */
    public Map<String, Set<String>> mPubliciseByUserId = new HashMap();
    private Set<ApiCallback<Void>> mRefreshProfilesCallback = new HashSet();
    /* access modifiers changed from: private */
    public IMXStore mStore;
    private Handler mUIHandler;

    public GroupsManager(MXDataHandler mXDataHandler, GroupsRestClient groupsRestClient) {
        this.mDataHandler = mXDataHandler;
        this.mStore = this.mDataHandler.getStore();
        this.mGroupsRestClient = groupsRestClient;
        this.mUIHandler = new Handler(Looper.getMainLooper());
    }

    public GroupsRestClient getGroupsRestClient() {
        return this.mGroupsRestClient;
    }

    public void onSessionPaused() {
        this.mPubliciseByUserId.clear();
    }

    public void onSessionResumed() {
        refreshGroupProfiles(null);
        getUserPublicisedGroups(this.mDataHandler.getUserId(), true, new SimpleApiCallback<Set<String>>() {
            public void onSuccess(Set<String> set) {
            }
        });
        this.mGroupProfileByGroupId.clear();
        this.mGroupProfileCallback.clear();
    }

    public Group getGroup(String str) {
        return this.mStore.getGroup(str);
    }

    public Collection<Group> getGroups() {
        return this.mStore.getGroups();
    }

    public Collection<Group> getInvitedGroups() {
        ArrayList arrayList = new ArrayList();
        for (Group group : getGroups()) {
            if (group.isInvited()) {
                arrayList.add(group);
            }
        }
        return arrayList;
    }

    public Collection<Group> getJoinedGroups() {
        ArrayList arrayList = new ArrayList(getGroups());
        arrayList.removeAll(getInvitedGroups());
        return arrayList;
    }

    public void onJoinGroup(final String str, final boolean z) {
        Group group = getGroup(str);
        if (group == null) {
            group = new Group(str);
        }
        if (TextUtils.equals(RoomMember.MEMBERSHIP_JOIN, group.getMembership())) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onJoinGroup() : the group ");
            sb.append(str);
            sb.append(" was already joined");
            Log.m209d(str2, sb.toString());
            return;
        }
        group.setMembership(RoomMember.MEMBERSHIP_JOIN);
        this.mStore.storeGroup(group);
        this.mGroupsRestClient.getGroupSummary(str, new ApiCallback<GroupSummary>() {
            private void onDone() {
                if (z) {
                    GroupsManager.this.mDataHandler.onJoinGroup(str);
                }
            }

            public void onSuccess(GroupSummary groupSummary) {
                Group group = GroupsManager.this.getGroup(str);
                if (group != null) {
                    group.setGroupSummary(groupSummary);
                    GroupsManager.this.mStore.flushGroup(group);
                    onDone();
                    if (GroupsManager.this.mPendingJoinGroups.get(str) != null) {
                        ((ApiCallback) GroupsManager.this.mPendingJoinGroups.get(str)).onSuccess(null);
                        GroupsManager.this.mPendingJoinGroups.remove(str);
                    }
                }
            }

            public void onNetworkError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onJoinGroup() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
                if (GroupsManager.this.mPendingJoinGroups.get(str) != null) {
                    ((ApiCallback) GroupsManager.this.mPendingJoinGroups.get(str)).onNetworkError(exc);
                    GroupsManager.this.mPendingJoinGroups.remove(str);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onMatrixError() : failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
                if (GroupsManager.this.mPendingJoinGroups.get(str) != null) {
                    ((ApiCallback) GroupsManager.this.mPendingJoinGroups.get(str)).onMatrixError(matrixError);
                    GroupsManager.this.mPendingJoinGroups.remove(str);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onUnexpectedError() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
                if (GroupsManager.this.mPendingJoinGroups.get(str) != null) {
                    ((ApiCallback) GroupsManager.this.mPendingJoinGroups.get(str)).onUnexpectedError(exc);
                    GroupsManager.this.mPendingJoinGroups.remove(str);
                }
            }
        });
    }

    public void onNewGroupInvitation(final String str, GroupSyncProfile groupSyncProfile, String str2, boolean z) {
        Group group = getGroup(str);
        if (group == null) {
            group = new Group(str);
        }
        GroupSummary groupSummary = new GroupSummary();
        groupSummary.profile = new GroupProfile();
        if (groupSyncProfile != null) {
            groupSummary.profile.name = groupSyncProfile.name;
            groupSummary.profile.avatarUrl = groupSyncProfile.avatarUrl;
        }
        group.setGroupSummary(groupSummary);
        group.setInviter(str2);
        group.setMembership("invite");
        this.mStore.storeGroup(group);
        if (z) {
            this.mUIHandler.post(new Runnable() {
                public void run() {
                    GroupsManager.this.mDataHandler.onNewGroupInvitation(str);
                }
            });
        }
    }

    public void onLeaveGroup(final String str, final boolean z) {
        if (this.mStore.getGroup(str) != null) {
            this.mStore.deleteGroup(str);
            this.mUIHandler.post(new Runnable() {
                public void run() {
                    if (z) {
                        GroupsManager.this.mDataHandler.onLeaveGroup(str);
                    }
                    if (GroupsManager.this.mPendingLeaveGroups.containsKey(str)) {
                        ((ApiCallback) GroupsManager.this.mPendingLeaveGroups.get(str)).onSuccess(null);
                        GroupsManager.this.mPendingLeaveGroups.remove(str);
                    }
                }
            });
        }
    }

    public void refreshGroupProfiles(ApiCallback<Void> apiCallback) {
        if (!this.mRefreshProfilesCallback.isEmpty()) {
            Log.m209d(LOG_TAG, "## refreshGroupProfiles() : there already is a pending request");
            this.mRefreshProfilesCallback.add(apiCallback);
            return;
        }
        this.mRefreshProfilesCallback.add(apiCallback);
        refreshGroupProfiles(getGroups().iterator());
    }

    /* access modifiers changed from: private */
    public void refreshGroupProfiles(final Iterator<Group> it) {
        if (!it.hasNext()) {
            for (ApiCallback apiCallback : this.mRefreshProfilesCallback) {
                if (apiCallback != null) {
                    try {
                        apiCallback.onSuccess(null);
                    } catch (Exception e) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## refreshGroupProfiles() failed ");
                        sb.append(e.getMessage());
                        Log.m211e(str, sb.toString());
                    }
                }
            }
            this.mRefreshProfilesCallback.clear();
            return;
        }
        final String groupId = ((Group) it.next()).getGroupId();
        this.mGroupsRestClient.getGroupProfile(groupId, new ApiCallback<GroupProfile>() {
            private void onDone() {
                GroupsManager.this.refreshGroupProfiles(it);
            }

            public void onSuccess(GroupProfile groupProfile) {
                Group group = GroupsManager.this.getGroup(groupId);
                if (group != null) {
                    group.setGroupProfile(groupProfile);
                    GroupsManager.this.mStore.flushGroup(group);
                }
                GroupsManager.this.mDataHandler.onGroupProfileUpdate(groupId);
                GroupsManager.this.mGroupProfileByGroupId.put(groupId, groupProfile);
                onDone();
            }

            public void onNetworkError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshGroupProfiles() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshGroupProfiles() : failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
            }

            public void onUnexpectedError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshGroupProfiles() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$300, sb.toString());
                onDone();
            }
        });
    }

    public void joinGroup(final String str, final ApiCallback<Void> apiCallback) {
        getGroupsRestClient().joinGroup(str, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                Group group = GroupsManager.this.getGroup(str);
                if (group == null || TextUtils.equals(group.getMembership(), "invite")) {
                    GroupsManager.this.mPendingJoinGroups.put(str, apiCallback);
                    GroupsManager.this.onJoinGroup(str, true);
                    return;
                }
                apiCallback.onSuccess(null);
            }
        });
    }

    public void leaveGroup(final String str, final ApiCallback<Void> apiCallback) {
        getGroupsRestClient().leaveGroup(str, new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                if (GroupsManager.this.getGroup(str) != null) {
                    GroupsManager.this.mPendingLeaveGroups.put(str, apiCallback);
                    GroupsManager.this.onLeaveGroup(str, true);
                    return;
                }
                apiCallback.onSuccess(null);
            }
        });
    }

    public void createGroup(String str, String str2, final ApiCallback<String> apiCallback) {
        final CreateGroupParams createGroupParams = new CreateGroupParams();
        createGroupParams.localpart = str;
        createGroupParams.profile = new GroupProfile();
        createGroupParams.profile.name = str2;
        getGroupsRestClient().createGroup(createGroupParams, new SimpleApiCallback<String>(apiCallback) {
            public void onSuccess(String str) {
                if (GroupsManager.this.getGroup(str) == null) {
                    Group group = new Group(str);
                    group.setGroupProfile(createGroupParams.profile);
                    group.setMembership(RoomMember.MEMBERSHIP_JOIN);
                    GroupsManager.this.mStore.storeGroup(group);
                }
                apiCallback.onSuccess(str);
            }
        });
    }

    public void refreshGroupData(Group group, ApiCallback<Void> apiCallback) {
        refreshGroupData(group, 0, apiCallback);
    }

    /* access modifiers changed from: private */
    public void refreshGroupData(final Group group, int i, final ApiCallback<Void> apiCallback) {
        if (i == 0) {
            getGroupsRestClient().getGroupProfile(group.getGroupId(), new SimpleApiCallback<GroupProfile>(apiCallback) {
                public void onSuccess(GroupProfile groupProfile) {
                    group.setGroupProfile(groupProfile);
                    GroupsManager.this.mStore.flushGroup(group);
                    GroupsManager.this.mDataHandler.onGroupProfileUpdate(group.getGroupId());
                    GroupsManager.this.refreshGroupData(group, 1, apiCallback);
                }
            });
        } else if (i == 1) {
            getGroupsRestClient().getGroupRooms(group.getGroupId(), new SimpleApiCallback<GroupRooms>(apiCallback) {
                public void onSuccess(GroupRooms groupRooms) {
                    group.setGroupRooms(groupRooms);
                    GroupsManager.this.mStore.flushGroup(group);
                    GroupsManager.this.mDataHandler.onGroupRoomsListUpdate(group.getGroupId());
                    GroupsManager.this.refreshGroupData(group, 2, apiCallback);
                }
            });
        } else if (i == 2) {
            getGroupsRestClient().getGroupUsers(group.getGroupId(), new SimpleApiCallback<GroupUsers>(apiCallback) {
                public void onSuccess(GroupUsers groupUsers) {
                    group.setGroupUsers(groupUsers);
                    GroupsManager.this.mStore.flushGroup(group);
                    GroupsManager.this.mDataHandler.onGroupUsersListUpdate(group.getGroupId());
                    GroupsManager.this.refreshGroupData(group, 3, apiCallback);
                }
            });
        } else {
            getGroupsRestClient().getGroupInvitedUsers(group.getGroupId(), new SimpleApiCallback<GroupUsers>(apiCallback) {
                public void onSuccess(GroupUsers groupUsers) {
                    group.setInvitedGroupUsers(groupUsers);
                    if (GroupsManager.this.mStore.getGroup(group.getGroupId()) != null) {
                        GroupsManager.this.mStore.flushGroup(group);
                    }
                    GroupsManager.this.mDataHandler.onGroupInvitedUsersListUpdate(group.getGroupId());
                    apiCallback.onSuccess(null);
                }
            });
        }
    }

    public Set<String> getUserPublicisedGroups(String str) {
        if (this.mPubliciseByUserId.containsKey(str)) {
            return new HashSet((Collection) this.mPubliciseByUserId.get(str));
        }
        return null;
    }

    public void getUserPublicisedGroups(final String str, boolean z, @NonNull final ApiCallback<Set<String>> apiCallback) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## getUserPublicisedGroups() : ");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        if (!MXSession.isUserId(str)) {
            this.mUIHandler.post(new Runnable() {
                public void run() {
                    apiCallback.onSuccess(new HashSet());
                }
            });
            return;
        }
        if (z) {
            this.mPubliciseByUserId.remove(str);
        } else if (this.mPubliciseByUserId.containsKey(str)) {
            this.mUIHandler.post(new Runnable() {
                public void run() {
                    String access$300 = GroupsManager.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## getUserPublicisedGroups() : ");
                    sb.append(str);
                    sb.append(" --> cached data ");
                    sb.append(GroupsManager.this.mPubliciseByUserId.get(str));
                    Log.m209d(access$300, sb.toString());
                    if (GroupsManager.this.mPubliciseByUserId.containsKey(str)) {
                        apiCallback.onSuccess(new HashSet((Collection) GroupsManager.this.mPubliciseByUserId.get(str)));
                    } else {
                        apiCallback.onSuccess(new HashSet());
                    }
                }
            });
            return;
        }
        if (this.mPendingPubliciseRequests.containsKey(str)) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getUserPublicisedGroups() : ");
            sb2.append(str);
            sb2.append(" request in progress");
            Log.m209d(str3, sb2.toString());
            ((Set) this.mPendingPubliciseRequests.get(str)).add(apiCallback);
            return;
        }
        this.mPendingPubliciseRequests.put(str, new HashSet());
        ((Set) this.mPendingPubliciseRequests.get(str)).add(apiCallback);
        this.mGroupsRestClient.getUserPublicisedGroups(str, new ApiCallback<List<String>>() {
            private void onDone(Set<String> set) {
                if (set != null) {
                    GroupsManager.this.mPubliciseByUserId.put(str, new HashSet(set));
                } else {
                    set = new HashSet<>();
                }
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getUserPublicisedGroups() : ");
                sb.append(str);
                sb.append(" -- ");
                sb.append(set);
                Log.m209d(access$300, sb.toString());
                Set<ApiCallback> set2 = (Set) GroupsManager.this.mPendingPubliciseRequests.get(str);
                GroupsManager.this.mPendingPubliciseRequests.remove(str);
                if (set2 != null) {
                    for (ApiCallback apiCallback : set2) {
                        if (apiCallback != null) {
                            try {
                                apiCallback.onSuccess(new HashSet(set));
                            } catch (Throwable th) {
                                String access$3002 = GroupsManager.LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("## getUserPublicisedGroups() : callback failed ");
                                sb2.append(th.getMessage());
                                Log.m209d(access$3002, sb2.toString());
                            }
                        }
                    }
                }
            }

            public void onSuccess(List<String> list) {
                onDone(list == null ? new HashSet() : new HashSet(list));
            }

            public void onNetworkError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getUserPublicisedGroups() : request failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$300, sb.toString());
                onDone(null);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getUserPublicisedGroups() : request failed ");
                sb.append(matrixError.getMessage());
                Log.m209d(access$300, sb.toString());
                onDone(null);
            }

            public void onUnexpectedError(Exception exc) {
                String access$300 = GroupsManager.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getUserPublicisedGroups() : request failed ");
                sb.append(exc.getMessage());
                Log.m209d(access$300, sb.toString());
                onDone(null);
            }
        });
    }

    public void updateGroupPublicity(String str, boolean z, ApiCallback<Void> apiCallback) {
        GroupsRestClient groupsRestClient = getGroupsRestClient();
        final String str2 = str;
        final boolean z2 = z;
        final ApiCallback<Void> apiCallback2 = apiCallback;
        C270116 r1 = new SimpleApiCallback<Void>(apiCallback) {
            public void onSuccess(Void voidR) {
                if (GroupsManager.this.mPubliciseByUserId.containsKey(str2)) {
                    if (z2) {
                        ((Set) GroupsManager.this.mPubliciseByUserId.get(str2)).add(str2);
                    } else {
                        ((Set) GroupsManager.this.mPubliciseByUserId.get(str2)).remove(str2);
                    }
                }
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(null);
                }
            }
        };
        groupsRestClient.updateGroupPublicity(str, z, r1);
    }

    public GroupProfile getGroupProfile(String str) {
        return (GroupProfile) this.mGroupProfileByGroupId.get(str);
    }

    public void getGroupProfile(final String str, final ApiCallback<GroupProfile> apiCallback) {
        if (apiCallback != null) {
            if (TextUtils.isEmpty(str) || !MXSession.isGroupId(str)) {
                this.mUIHandler.post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(new GroupProfile());
                    }
                });
            } else if (this.mGroupProfileByGroupId.containsKey(str)) {
                this.mUIHandler.post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(GroupsManager.this.mGroupProfileByGroupId.get(str));
                    }
                });
            } else if (this.mGroupProfileCallback.containsKey(str)) {
                ((List) this.mGroupProfileCallback.get(str)).add(apiCallback);
            } else {
                this.mGroupProfileCallback.put(str, new ArrayList(Arrays.asList(new ApiCallback[]{apiCallback})));
                this.mGroupsRestClient.getGroupProfile(str, new ApiCallback<GroupProfile>() {
                    public void onSuccess(GroupProfile groupProfile) {
                        GroupsManager.this.mGroupProfileByGroupId.put(str, groupProfile);
                        List<ApiCallback> list = (List) GroupsManager.this.mGroupProfileCallback.get(str);
                        GroupsManager.this.mGroupProfileCallback.remove(str);
                        if (list != null) {
                            for (ApiCallback onSuccess : list) {
                                onSuccess.onSuccess(groupProfile);
                            }
                        }
                    }

                    public void onNetworkError(Exception exc) {
                        List<ApiCallback> list = (List) GroupsManager.this.mGroupProfileCallback.get(str);
                        GroupsManager.this.mGroupProfileCallback.remove(str);
                        if (list != null) {
                            for (ApiCallback onNetworkError : list) {
                                onNetworkError.onNetworkError(exc);
                            }
                        }
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        List<ApiCallback> list = (List) GroupsManager.this.mGroupProfileCallback.get(str);
                        GroupsManager.this.mGroupProfileCallback.remove(str);
                        if (list != null) {
                            for (ApiCallback onMatrixError : list) {
                                onMatrixError.onMatrixError(matrixError);
                            }
                        }
                    }

                    public void onUnexpectedError(Exception exc) {
                        List<ApiCallback> list = (List) GroupsManager.this.mGroupProfileCallback.get(str);
                        GroupsManager.this.mGroupProfileCallback.remove(str);
                        if (list != null) {
                            for (ApiCallback onUnexpectedError : list) {
                                onUnexpectedError.onUnexpectedError(exc);
                            }
                        }
                    }
                });
            }
        }
    }
}
