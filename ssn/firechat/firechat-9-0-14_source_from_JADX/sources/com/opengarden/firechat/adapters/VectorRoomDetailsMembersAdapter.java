package com.opengarden.firechat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.pid.RoomThirdPartyInvite;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class VectorRoomDetailsMembersAdapter extends BaseExpandableListAdapter {
    /* access modifiers changed from: private */
    public final String LOG_TAG = VectorRoomDetailsMembersAdapter.class.getSimpleName();
    private final int mChildLayoutResourceId;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<String> mDisplayNamesList = new ArrayList<>();
    /* access modifiers changed from: private */
    public int mGroupIndexInvitedMembers = -1;
    /* access modifiers changed from: private */
    public int mGroupIndexPresentMembers = -1;
    private final int mGroupLayoutResourceId;
    private boolean mIsMultiSelectionMode;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public OnParticipantsListener mOnParticipantsListener;
    /* access modifiers changed from: private */
    public final Room mRoom;
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<ParticipantAdapterItem>> mRoomMembersListByGroupPosition;
    /* access modifiers changed from: private */
    public String mSearchPattern = "";
    /* access modifiers changed from: private */
    public ArrayList<String> mSelectedUserIds = new ArrayList<>();
    /* access modifiers changed from: private */
    public final MXSession mSession;
    /* access modifiers changed from: private */
    public View mSwipingCellView;

    private static class ChildMemberViewHolder {
        final View mDeleteActionsView;
        final View mHiddenListActionsView;
        final ImageView mMemberAvatarBadgeImageView;
        final ImageView mMemberAvatarImageView;
        final TextView mMemberNameTextView;
        final TextView mMemberStatusTextView;
        final CheckBox mMultipleSelectionCheckBox;
        final RelativeLayout mSwipeCellLayout;

        ChildMemberViewHolder(View view) {
            this.mMemberAvatarImageView = (ImageView) view.findViewById(C1299R.C1301id.filtered_list_avatar);
            this.mMemberAvatarBadgeImageView = (ImageView) view.findViewById(C1299R.C1301id.filtered_list_avatar_badge);
            this.mMemberNameTextView = (TextView) view.findViewById(C1299R.C1301id.filtered_list_name);
            this.mMemberStatusTextView = (TextView) view.findViewById(C1299R.C1301id.filtered_list_status);
            this.mHiddenListActionsView = view.findViewById(C1299R.C1301id.filtered_list_actions);
            this.mSwipeCellLayout = (RelativeLayout) view.findViewById(C1299R.C1301id.filtered_list_cell);
            this.mMultipleSelectionCheckBox = (CheckBox) view.findViewById(C1299R.C1301id.filtered_list_checkbox);
            this.mDeleteActionsView = view.findViewById(C1299R.C1301id.filtered_list_delete_action);
        }
    }

    private static class GroupViewHolder {
        final ImageView mExpanderLogoImageView;
        final TextView mTitleTxtView;

        GroupViewHolder(View view) {
            this.mTitleTxtView = (TextView) view.findViewById(C1299R.C1301id.heading);
            this.mExpanderLogoImageView = (ImageView) view.findViewById(C1299R.C1301id.heading_image);
        }
    }

    public interface OnParticipantsListener {
        void onClick(ParticipantAdapterItem participantAdapterItem);

        void onGroupCollapsedNotif(int i);

        void onGroupExpandedNotif(int i);

        void onLeaveClick();

        void onRemoveClick(ParticipantAdapterItem participantAdapterItem);

        void onSelectUserId(String str);
    }

    public interface OnRoomMembersSearchListener {
        void onSearchEnd(int i, boolean z);
    }

    public long getChildId(int i, int i2) {
        return 0;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    public VectorRoomDetailsMembersAdapter(Context context, int i, int i2, MXSession mXSession, String str, MXMediasCache mXMediasCache) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mChildLayoutResourceId = i;
        this.mGroupLayoutResourceId = i2;
        this.mSession = mXSession;
        this.mRoom = this.mSession.getDataHandler().getRoom(str);
        this.mIsMultiSelectionMode = false;
    }

    @SuppressLint({"LongLogTag"})
    public void setSearchedPattern(String str, OnRoomMembersSearchListener onRoomMembersSearchListener, boolean z) {
        if (TextUtils.isEmpty(str)) {
            this.mSearchPattern = null;
            updateRoomMembersDataModel(onRoomMembersSearchListener);
        } else if (!str.trim().equals(this.mSearchPattern) || z) {
            this.mSearchPattern = str.trim().toLowerCase(VectorApp.getApplicationLocale());
            updateRoomMembersDataModel(onRoomMembersSearchListener);
        } else if (onRoomMembersSearchListener != null) {
            onRoomMembersSearchListener.onSearchEnd(getItemsCount(), false);
        }
    }

    public int getItemsCount() {
        return getChildrenCount(this.mGroupIndexInvitedMembers) + getChildrenCount(this.mGroupIndexPresentMembers);
    }

    public void setOnParticipantsListener(OnParticipantsListener onParticipantsListener) {
        this.mOnParticipantsListener = onParticipantsListener;
    }

    public ArrayList<String> getSelectedUserIds() {
        return this.mSelectedUserIds;
    }

    public void setMultiSelectionMode(boolean z) {
        this.mIsMultiSelectionMode = z;
        this.mSelectedUserIds = new ArrayList<>();
    }

    /* access modifiers changed from: private */
    public boolean isSearchModeEnabled() {
        return !TextUtils.isEmpty(this.mSearchPattern);
    }

    /* access modifiers changed from: private */
    public int alphaComparator(String str, String str2) {
        if (str == null) {
            return -1;
        }
        if (str2 == null) {
            return 1;
        }
        return String.CASE_INSENSITIVE_ORDER.compare(str, str2);
    }

    public void updateRoomMembersDataModel(final OnRoomMembersSearchListener onRoomMembersSearchListener) {
        if (!this.mSession.isAlive()) {
            Log.m211e(this.LOG_TAG, "updateRoomMembersDataModel the session is not anymore valid");
            return;
        }
        final Handler handler = new Handler();
        final String str = this.mSearchPattern;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                final boolean access$000 = VectorRoomDetailsMembersAdapter.this.isSearchModeEnabled();
                final ArrayList arrayList = new ArrayList();
                final ArrayList arrayList2 = new ArrayList();
                final ArrayList arrayList3 = new ArrayList();
                ArrayList arrayList4 = new ArrayList();
                final ArrayList arrayList5 = new ArrayList();
                Collection<RoomMember> activeMembers = VectorRoomDetailsMembersAdapter.this.mRoom.getActiveMembers();
                String myUserId = VectorRoomDetailsMembersAdapter.this.mSession.getMyUserId();
                final PowerLevels powerLevels = VectorRoomDetailsMembersAdapter.this.mRoom.getLiveState().getPowerLevels();
                for (RoomMember roomMember : activeMembers) {
                    ParticipantAdapterItem participantAdapterItem = new ParticipantAdapterItem(roomMember);
                    if (!access$000 || participantAdapterItem.contains(VectorRoomDetailsMembersAdapter.this.mSearchPattern)) {
                        if (roomMember.getUserId().equals(myUserId)) {
                            arrayList.add(participantAdapterItem);
                        } else if ("invite".equals(roomMember.membership)) {
                            arrayList5.add(participantAdapterItem);
                        } else {
                            arrayList4.add(participantAdapterItem);
                        }
                        if (!TextUtils.isEmpty(participantAdapterItem.mDisplayName)) {
                            arrayList3.add(participantAdapterItem.mDisplayName);
                        }
                    }
                }
                for (RoomThirdPartyInvite roomThirdPartyInvite : VectorRoomDetailsMembersAdapter.this.mRoom.getLiveState().thirdPartyInvites()) {
                    if (VectorRoomDetailsMembersAdapter.this.mRoom.getLiveState().memberWithThirdPartyInviteToken(roomThirdPartyInvite.token) == null) {
                        ParticipantAdapterItem participantAdapterItem2 = new ParticipantAdapterItem(roomThirdPartyInvite.display_name, "", null, true);
                        if (!access$000 || participantAdapterItem2.contains(VectorRoomDetailsMembersAdapter.this.mSearchPattern)) {
                            arrayList5.add(participantAdapterItem2);
                        }
                    }
                }
                final MXDataHandler dataHandler = VectorRoomDetailsMembersAdapter.this.mSession.getDataHandler();
                C18281 r9 = new Comparator<ParticipantAdapterItem>() {
                    private final HashMap<String, User> usersMap = new HashMap<>();

                    private User getUser(String str) {
                        if (str == null) {
                            return null;
                        }
                        User user = (User) this.usersMap.get(str);
                        if (user != null) {
                            return user;
                        }
                        User user2 = dataHandler.getUser(str);
                        if (user2 == null) {
                            return user2;
                        }
                        User deepCopy = user2.deepCopy();
                        this.usersMap.put(str, deepCopy);
                        return deepCopy;
                    }

                    public int compare(ParticipantAdapterItem participantAdapterItem, ParticipantAdapterItem participantAdapterItem2) {
                        int i;
                        User user = getUser(participantAdapterItem.mUserId);
                        User user2 = getUser(participantAdapterItem2.mUserId);
                        String comparisonDisplayName = participantAdapterItem.getComparisonDisplayName();
                        String comparisonDisplayName2 = participantAdapterItem2.getComparisonDisplayName();
                        int i2 = 0;
                        boolean booleanValue = (user == null || user.currently_active == null) ? false : user.currently_active.booleanValue();
                        boolean booleanValue2 = (user2 == null || user2.currently_active == null) ? false : user2.currently_active.booleanValue();
                        if (powerLevels != null) {
                            i = (user == null || user.user_id == null) ? 0 : powerLevels.getUserPowerLevel(user.user_id);
                            if (!(user2 == null || user2.user_id == null)) {
                                i2 = powerLevels.getUserPowerLevel(user2.user_id);
                            }
                        } else {
                            i = 0;
                        }
                        if (user == null && user2 == null) {
                            return VectorRoomDetailsMembersAdapter.this.alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                        }
                        int i3 = 1;
                        if (user != null && user2 == null) {
                            return 1;
                        }
                        if (user == null && user2 != null) {
                            return -1;
                        }
                        if (!booleanValue || !booleanValue2) {
                            if (booleanValue && !booleanValue2) {
                                return -1;
                            }
                            if (!booleanValue && booleanValue2) {
                                return 1;
                            }
                            long absoluteLastActiveAgo = user != null ? user.getAbsoluteLastActiveAgo() : 0;
                            long absoluteLastActiveAgo2 = user2 != null ? user2.getAbsoluteLastActiveAgo() : 0;
                            long j = absoluteLastActiveAgo - absoluteLastActiveAgo2;
                            if (j == 0) {
                                return VectorRoomDetailsMembersAdapter.this.alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                            }
                            if (0 == absoluteLastActiveAgo) {
                                return 1;
                            }
                            if (0 == absoluteLastActiveAgo2) {
                                return -1;
                            }
                            if (j <= 0) {
                                i3 = -1;
                            }
                            return i3;
                        } else if (i == i2) {
                            return VectorRoomDetailsMembersAdapter.this.alphaComparator(comparisonDisplayName, comparisonDisplayName2);
                        } else {
                            if (i2 - i <= 0) {
                                i3 = -1;
                            }
                            return i3;
                        }
                    }
                };
                try {
                    Collections.sort(arrayList4, r9);
                    arrayList.addAll(arrayList4);
                    Handler handler = handler;
                    final C18281 r7 = r9;
                    C18303 r1 = new Runnable() {
                        public void run() {
                            if (TextUtils.equals(VectorRoomDetailsMembersAdapter.this.mSearchPattern, str)) {
                                VectorRoomDetailsMembersAdapter.this.mDisplayNamesList = arrayList3;
                                VectorRoomDetailsMembersAdapter.this.mRoomMembersListByGroupPosition = arrayList2;
                                VectorRoomDetailsMembersAdapter.this.mGroupIndexPresentMembers = -1;
                                VectorRoomDetailsMembersAdapter.this.mGroupIndexPresentMembers = -1;
                                int i = 0;
                                if (arrayList.size() != 0) {
                                    arrayList2.add(arrayList);
                                    VectorRoomDetailsMembersAdapter.this.mGroupIndexPresentMembers = 0;
                                    i = 1;
                                }
                                if (arrayList5.size() != 0) {
                                    Collections.sort(arrayList5, r7);
                                    arrayList2.add(arrayList5);
                                    VectorRoomDetailsMembersAdapter.this.mGroupIndexInvitedMembers = i;
                                }
                                if (onRoomMembersSearchListener != null) {
                                    try {
                                        onRoomMembersSearchListener.onSearchEnd(VectorRoomDetailsMembersAdapter.this.getItemsCount(), access$000);
                                    } catch (Exception e) {
                                        String access$500 = VectorRoomDetailsMembersAdapter.this.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("## updateRoomMembersDataModel() : onSearchEnd fails ");
                                        sb.append(e.getMessage());
                                        Log.m211e(access$500, sb.toString());
                                    }
                                }
                                VectorRoomDetailsMembersAdapter.this.notifyDataSetChanged();
                            }
                        }
                    };
                    handler.post(r1);
                } catch (Exception e) {
                    String access$500 = VectorRoomDetailsMembersAdapter.this.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## updateRoomMembersDataModel failed while sorting ");
                    sb.append(e.getMessage());
                    Log.m211e(access$500, sb.toString());
                    if (TextUtils.equals(str, VectorRoomDetailsMembersAdapter.this.mSearchPattern)) {
                        handler.post(new Runnable() {
                            public void run() {
                                VectorRoomDetailsMembersAdapter.this.updateRoomMembersDataModel(onRoomMembersSearchListener);
                            }
                        });
                    }
                }
            }
        });
        thread.setPriority(1);
        thread.start();
    }

    public ArrayList<String> getUserIdsList() {
        ArrayList<String> arrayList = new ArrayList<>();
        if (this.mGroupIndexPresentMembers >= 0) {
            int size = ((ArrayList) this.mRoomMembersListByGroupPosition.get(this.mGroupIndexPresentMembers)).size();
            for (int i = 1; i < size; i++) {
                ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) ((ArrayList) this.mRoomMembersListByGroupPosition.get(this.mGroupIndexPresentMembers)).get(i);
                if (participantAdapterItem.mUserId != null) {
                    arrayList.add(participantAdapterItem.mUserId);
                }
            }
        }
        return arrayList;
    }

    private String getGroupTitle(int i) {
        if (this.mGroupIndexInvitedMembers == i) {
            return this.mContext.getResources().getString(C1299R.string.room_details_people_invited_group_name);
        }
        return this.mGroupIndexPresentMembers == i ? this.mContext.getResources().getString(C1299R.string.room_details_people_present_group_name) : "??";
    }

    public void onGroupCollapsed(int i) {
        super.onGroupCollapsed(i);
        if (this.mOnParticipantsListener != null) {
            this.mOnParticipantsListener.onGroupCollapsedNotif(i);
        }
    }

    public void onGroupExpanded(int i) {
        super.onGroupExpanded(i);
        if (this.mOnParticipantsListener != null) {
            this.mOnParticipantsListener.onGroupExpandedNotif(i);
        }
    }

    public int getGroupCount() {
        if (this.mRoomMembersListByGroupPosition != null) {
            return this.mRoomMembersListByGroupPosition.size();
        }
        return 0;
    }

    @SuppressLint({"LongLogTag"})
    public int getChildrenCount(int i) {
        try {
            if (this.mRoomMembersListByGroupPosition == null || -1 == i) {
                return 0;
            }
            return ((ArrayList) this.mRoomMembersListByGroupPosition.get(i)).size();
        } catch (Exception e) {
            String str = this.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getChildrenCount(): Exception Msg=");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            return 0;
        }
    }

    public Object getGroup(int i) {
        return getGroupTitle(i);
    }

    public Object getChild(int i, int i2) {
        if (this.mRoomMembersListByGroupPosition != null) {
            return ((ArrayList) this.mRoomMembersListByGroupPosition.get(i)).get(i2);
        }
        return null;
    }

    public long getGroupId(int i) {
        return (long) getGroupTitle(i).hashCode();
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        GroupViewHolder groupViewHolder;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mGroupLayoutResourceId, null);
            groupViewHolder = new GroupViewHolder(view);
            view.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }
        groupViewHolder.mTitleTxtView.setText(getGroupTitle(i));
        groupViewHolder.mExpanderLogoImageView.setImageResource(z ? C1299R.C1300drawable.ic_material_expand_more_black : C1299R.C1300drawable.ic_material_expand_less_black);
        return view;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0196, code lost:
        if (r5 >= r14) goto L_0x0199;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x019f, code lost:
        if (r9.mRoom == null) goto L_0x019b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getChildView(int r10, int r11, boolean r12, android.view.View r13, android.view.ViewGroup r14) {
        /*
            r9 = this;
            boolean r12 = r9.isSearchModeEnabled()
            r0 = 1
            r1 = 0
            if (r11 != 0) goto L_0x000e
            int r2 = r9.mGroupIndexPresentMembers
            if (r2 != r10) goto L_0x000e
            r2 = 1
            goto L_0x000f
        L_0x000e:
            r2 = 0
        L_0x000f:
            java.util.ArrayList<java.util.ArrayList<com.opengarden.firechat.adapters.ParticipantAdapterItem>> r3 = r9.mRoomMembersListByGroupPosition
            java.lang.Object r10 = r3.get(r10)
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            java.lang.Object r10 = r10.get(r11)
            com.opengarden.firechat.adapters.ParticipantAdapterItem r10 = (com.opengarden.firechat.adapters.ParticipantAdapterItem) r10
            if (r13 != 0) goto L_0x0030
            android.view.LayoutInflater r11 = r9.mLayoutInflater
            int r13 = r9.mChildLayoutResourceId
            android.view.View r13 = r11.inflate(r13, r14, r1)
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$ChildMemberViewHolder r11 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$ChildMemberViewHolder
            r11.<init>(r13)
            r13.setTag(r11)
            goto L_0x0036
        L_0x0030:
            java.lang.Object r11 = r13.getTag()
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$ChildMemberViewHolder r11 = (com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter.ChildMemberViewHolder) r11
        L_0x0036:
            com.opengarden.firechat.matrixsdk.MXSession r14 = r9.mSession
            boolean r14 = r14.isAlive()
            if (r14 != 0) goto L_0x0046
            java.lang.String r10 = r9.LOG_TAG
            java.lang.String r11 = "getChildView : the session is not anymore valid"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r10, r11)
            return r13
        L_0x0046:
            android.graphics.Bitmap r14 = r10.getAvatarBitmap()
            if (r14 == 0) goto L_0x0056
            android.widget.ImageView r14 = r11.mMemberAvatarImageView
            android.graphics.Bitmap r3 = r10.getAvatarBitmap()
            r14.setImageBitmap(r3)
            goto L_0x00bf
        L_0x0056:
            java.lang.String r14 = r10.mUserId
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 == 0) goto L_0x006e
            android.content.Context r3 = r9.mContext
            com.opengarden.firechat.matrixsdk.MXSession r4 = r9.mSession
            android.widget.ImageView r5 = r11.mMemberAvatarImageView
            java.lang.String r6 = r10.mAvatarUrl
            java.lang.String r7 = r10.mDisplayName
            java.lang.String r8 = r10.mDisplayName
            com.opengarden.firechat.util.VectorUtils.loadUserAvatar(r3, r4, r5, r6, r7, r8)
            goto L_0x00bf
        L_0x006e:
            java.lang.String r14 = r10.mUserId
            java.lang.String r3 = r10.mDisplayName
            boolean r14 = android.text.TextUtils.equals(r14, r3)
            if (r14 != 0) goto L_0x0080
            java.lang.String r14 = r10.mAvatarUrl
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 == 0) goto L_0x00b0
        L_0x0080:
            com.opengarden.firechat.matrixsdk.MXSession r14 = r9.mSession
            com.opengarden.firechat.matrixsdk.MXDataHandler r14 = r14.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r14 = r14.getStore()
            java.lang.String r3 = r10.mUserId
            com.opengarden.firechat.matrixsdk.rest.model.User r14 = r14.getUser(r3)
            if (r14 == 0) goto L_0x00b0
            java.lang.String r3 = r10.mUserId
            java.lang.String r4 = r10.mDisplayName
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 == 0) goto L_0x00a8
            java.lang.String r3 = r14.displayname
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x00a8
            java.lang.String r3 = r14.displayname
            r10.mDisplayName = r3
        L_0x00a8:
            java.lang.String r3 = r10.mAvatarUrl
            if (r3 != 0) goto L_0x00b0
            java.lang.String r14 = r14.avatar_url
            r10.mAvatarUrl = r14
        L_0x00b0:
            android.content.Context r3 = r9.mContext
            com.opengarden.firechat.matrixsdk.MXSession r4 = r9.mSession
            android.widget.ImageView r5 = r11.mMemberAvatarImageView
            java.lang.String r6 = r10.mAvatarUrl
            java.lang.String r7 = r10.mUserId
            java.lang.String r8 = r10.mDisplayName
            com.opengarden.firechat.util.VectorUtils.loadUserAvatar(r3, r4, r5, r6, r7, r8)
        L_0x00bf:
            java.lang.String r14 = r10.mDisplayName
            boolean r3 = android.text.TextUtils.isEmpty(r14)
            if (r3 != 0) goto L_0x00fd
            java.util.ArrayList<java.lang.String> r3 = r9.mDisplayNamesList
            int r3 = r3.indexOf(r14)
            if (r3 < 0) goto L_0x00d8
            java.util.ArrayList<java.lang.String> r4 = r9.mDisplayNamesList
            int r4 = r4.lastIndexOf(r14)
            if (r3 != r4) goto L_0x00d8
            r3 = -1
        L_0x00d8:
            if (r3 < 0) goto L_0x00fd
            java.lang.String r3 = r10.mUserId
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x00fd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            java.lang.String r14 = " ("
            r3.append(r14)
            java.lang.String r14 = r10.mUserId
            r3.append(r14)
            java.lang.String r14 = ")"
            r3.append(r14)
            java.lang.String r14 = r3.toString()
        L_0x00fd:
            android.widget.TextView r3 = r11.mMemberNameTextView
            r3.setText(r14)
            android.widget.ImageView r14 = r11.mMemberAvatarBadgeImageView
            r3 = 8
            r14.setVisibility(r3)
            com.opengarden.firechat.matrixsdk.data.Room r14 = r9.mRoom
            r4 = 0
            if (r14 == 0) goto L_0x0150
            com.opengarden.firechat.matrixsdk.data.Room r14 = r9.mRoom
            com.opengarden.firechat.matrixsdk.data.RoomState r14 = r14.getLiveState()
            com.opengarden.firechat.matrixsdk.rest.model.PowerLevels r14 = r14.getPowerLevels()
            if (r14 == 0) goto L_0x0151
            java.lang.String r5 = r10.mUserId
            int r5 = r14.getUserPowerLevel(r5)
            float r5 = (float) r5
            r6 = 1120403456(0x42c80000, float:100.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x0135
            android.widget.ImageView r5 = r11.mMemberAvatarBadgeImageView
            r5.setVisibility(r1)
            android.widget.ImageView r5 = r11.mMemberAvatarBadgeImageView
            r6 = 2131230809(0x7f080059, float:1.8077681E38)
            r5.setImageResource(r6)
            goto L_0x0151
        L_0x0135:
            java.lang.String r5 = r10.mUserId
            int r5 = r14.getUserPowerLevel(r5)
            float r5 = (float) r5
            r6 = 1112014848(0x42480000, float:50.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x0151
            android.widget.ImageView r5 = r11.mMemberAvatarBadgeImageView
            r5.setVisibility(r1)
            android.widget.ImageView r5 = r11.mMemberAvatarBadgeImageView
            r6 = 2131230956(0x7f0800ec, float:1.807798E38)
            r5.setImageResource(r6)
            goto L_0x0151
        L_0x0150:
            r14 = r4
        L_0x0151:
            android.widget.TextView r5 = r11.mMemberStatusTextView
            android.content.Context r6 = r9.mContext
            com.opengarden.firechat.matrixsdk.MXSession r7 = r9.mSession
            java.lang.String r8 = r10.mUserId
            java.lang.String r6 = com.opengarden.firechat.util.VectorUtils.getUserOnlineStatus(r6, r7, r8, r4)
            r5.setText(r6)
            android.view.View r5 = r11.mDeleteActionsView
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$2 r6 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$2
            r6.<init>(r2, r10)
            r5.setOnClickListener(r6)
            android.view.View r5 = r9.mSwipingCellView
            r6 = 0
            if (r5 == 0) goto L_0x0176
            android.view.View r5 = r9.mSwipingCellView
            r5.setTranslationX(r6)
            r9.mSwipingCellView = r4
        L_0x0176:
            android.widget.RelativeLayout r5 = r11.mSwipeCellLayout
            r5.setTranslationX(r6)
            if (r14 == 0) goto L_0x019d
            com.opengarden.firechat.matrixsdk.MXSession r5 = r9.mSession
            com.opengarden.firechat.matrixsdk.rest.model.login.Credentials r5 = r5.getCredentials()
            java.lang.String r5 = r5.userId
            int r5 = r14.getUserPowerLevel(r5)
            java.lang.String r6 = r10.mUserId
            int r6 = r14.getUserPowerLevel(r6)
            int r14 = r14.kick
            if (r2 == 0) goto L_0x0194
            goto L_0x0199
        L_0x0194:
            if (r5 <= r6) goto L_0x019b
            if (r5 >= r14) goto L_0x0199
            goto L_0x019b
        L_0x0199:
            r14 = 0
            goto L_0x01a2
        L_0x019b:
            r14 = 1
            goto L_0x01a2
        L_0x019d:
            com.opengarden.firechat.matrixsdk.data.Room r14 = r9.mRoom
            if (r14 != 0) goto L_0x0199
            goto L_0x019b
        L_0x01a2:
            android.widget.RelativeLayout r2 = r11.mSwipeCellLayout
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$3 r5 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$3
            r5.<init>(r10)
            r2.setOnClickListener(r5)
            android.widget.RelativeLayout r2 = r11.mSwipeCellLayout
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$4 r5 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$4
            r5.<init>()
            r2.setOnLongClickListener(r5)
            if (r12 != 0) goto L_0x01ca
            if (r14 != 0) goto L_0x01ca
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r12 = r10.mRoomMember
            if (r12 != 0) goto L_0x01bf
            goto L_0x01ca
        L_0x01bf:
            android.widget.RelativeLayout r12 = r11.mSwipeCellLayout
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$5 r14 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$5
            r14.<init>(r11, r10)
            r12.setOnTouchListener(r14)
            goto L_0x01cf
        L_0x01ca:
            android.widget.RelativeLayout r12 = r11.mSwipeCellLayout
            r12.setOnTouchListener(r4)
        L_0x01cf:
            com.opengarden.firechat.util.ThemeUtils r12 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            android.content.Context r14 = r9.mContext
            r2 = 2130968965(0x7f040185, float:1.7546599E38)
            int r12 = r12.getColor(r14, r2)
            boolean r14 = r9.mIsMultiSelectionMode
            if (r14 == 0) goto L_0x0224
            com.opengarden.firechat.matrixsdk.MXSession r14 = r9.mSession
            java.lang.String r14 = r14.getMyUserId()
            java.lang.String r2 = r10.mUserId
            boolean r14 = android.text.TextUtils.equals(r14, r2)
            if (r14 != 0) goto L_0x0224
            com.opengarden.firechat.matrixsdk.rest.model.RoomMember r14 = r10.mRoomMember
            if (r14 == 0) goto L_0x0224
            android.widget.CheckBox r14 = r11.mMultipleSelectionCheckBox
            r14.setVisibility(r1)
            android.widget.CheckBox r14 = r11.mMultipleSelectionCheckBox
            java.util.ArrayList<java.lang.String> r2 = r9.mSelectedUserIds
            java.lang.String r3 = r10.mUserId
            int r2 = r2.indexOf(r3)
            if (r2 < 0) goto L_0x0202
            goto L_0x0203
        L_0x0202:
            r0 = 0
        L_0x0203:
            r14.setChecked(r0)
            android.widget.CheckBox r14 = r11.mMultipleSelectionCheckBox
            boolean r14 = r14.isChecked()
            if (r14 == 0) goto L_0x0219
            com.opengarden.firechat.util.ThemeUtils r12 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            android.content.Context r14 = r9.mContext
            r0 = 2130968913(0x7f040151, float:1.7546493E38)
            int r12 = r12.getColor(r14, r0)
        L_0x0219:
            android.widget.CheckBox r14 = r11.mMultipleSelectionCheckBox
            com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$6 r0 = new com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter$6
            r0.<init>(r11, r10)
            r14.setOnClickListener(r0)
            goto L_0x0229
        L_0x0224:
            android.widget.CheckBox r10 = r11.mMultipleSelectionCheckBox
            r10.setVisibility(r3)
        L_0x0229:
            android.widget.RelativeLayout r10 = r11.mSwipeCellLayout
            r10.setBackgroundColor(r12)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorRoomDetailsMembersAdapter.getChildView(int, int, boolean, android.view.View, android.view.ViewGroup):android.view.View");
    }
}
