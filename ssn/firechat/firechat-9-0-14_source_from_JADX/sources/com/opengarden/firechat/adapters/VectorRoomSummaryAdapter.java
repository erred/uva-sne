package com.opengarden.firechat.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.RoomUtils.MoreActionListener;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class VectorRoomSummaryAdapter extends BaseExpandableListAdapter {
    private final String DBG_CLASS_NAME;
    private final int mChildLayoutResourceId;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mDirectoryGroupPosition = -1;
    private final boolean mDisplayDirectoryGroupWhenEmpty;
    private int mFavouritesGroupPosition = -1;
    private boolean mForceDirectoryGroupDisplay;
    private final int mHeaderLayoutResourceId;
    private int mInvitedGroupPosition = -1;
    private boolean mIsDragAndDropMode = false;
    private final boolean mIsSearchMode;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public final RoomEventListener mListener;
    private int mLowPriorGroupPosition = -1;
    private Integer mMatchedPublicRoomsCount;
    /* access modifiers changed from: private */
    public final MoreActionListener mMoreActionListener;
    /* access modifiers changed from: private */
    public final MXSession mMxSession;
    private int mNoTagGroupPosition = -1;
    private Integer mPublicRoomsCount;
    private int mRoomByAliasGroupPosition = -1;
    private String mSearchedPattern;
    private ArrayList<ArrayList<RoomSummary>> mSummaryListByGroupPosition;

    public interface RoomEventListener {
        void onGroupCollapsedNotif(int i);

        void onGroupExpandedNotif(int i);

        void onPreviewRoom(MXSession mXSession, String str);

        void onRejectInvitation(MXSession mXSession, String str);
    }

    public Object getChild(int i, int i2) {
        return null;
    }

    public long getChildId(int i, int i2) {
        return 0;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    public VectorRoomSummaryAdapter(Context context, MXSession mXSession, boolean z, boolean z2, int i, int i2, RoomEventListener roomEventListener, MoreActionListener moreActionListener) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        this.mChildLayoutResourceId = i;
        this.mHeaderLayoutResourceId = i2;
        this.DBG_CLASS_NAME = getClass().getName();
        this.mMxSession = mXSession;
        this.mListener = roomEventListener;
        this.mMoreActionListener = moreActionListener;
        this.mIsSearchMode = z;
        this.mDisplayDirectoryGroupWhenEmpty = z2;
    }

    public void setForceDirectoryGroupDisplay(boolean z) {
        this.mForceDirectoryGroupDisplay = z;
    }

    private String getFormattedTimestamp(Event event) {
        String tsToString = AdapterUtils.tsToString(this.mContext, event.getOriginServerTs(), false);
        StringBuilder sb = new StringBuilder();
        sb.append(this.mContext.getString(C1299R.string.today));
        sb.append(StringUtils.SPACE);
        String sb2 = sb.toString();
        return tsToString.startsWith(sb2) ? tsToString.substring(sb2.length()) : tsToString;
    }

    private String getGroupTitle(int i) {
        if (this.mRoomByAliasGroupPosition == i) {
            return this.mContext.getResources().getString(C1299R.string.room_recents_join);
        }
        if (this.mDirectoryGroupPosition == i) {
            return this.mContext.getResources().getString(C1299R.string.room_recents_directory);
        }
        if (this.mFavouritesGroupPosition == i) {
            return this.mContext.getResources().getString(C1299R.string.room_recents_favourites);
        }
        if (this.mNoTagGroupPosition == i) {
            return this.mContext.getResources().getString(C1299R.string.room_recents_conversations);
        }
        if (this.mLowPriorGroupPosition == i) {
            return this.mContext.getResources().getString(C1299R.string.room_recents_low_priority);
        }
        return this.mInvitedGroupPosition == i ? this.mContext.getResources().getString(C1299R.string.room_recents_invites) : "??";
    }

    private void fillList(ArrayList<RoomSummary> arrayList, RoomSummary roomSummary, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            arrayList.add(roomSummary);
        }
    }

    private boolean isMatchedPattern(Room room) {
        boolean z = !this.mIsSearchMode;
        if (TextUtils.isEmpty(this.mSearchedPattern)) {
            return z;
        }
        String roomDisplayName = VectorUtils.getRoomDisplayName(this.mContext, this.mMxSession, room);
        return !TextUtils.isEmpty(roomDisplayName) && roomDisplayName.toLowerCase(VectorApp.getApplicationLocale()).contains(this.mSearchedPattern);
    }

    public boolean isRoomByIdGroupPosition(int i) {
        return this.mRoomByAliasGroupPosition == i;
    }

    public boolean isDirectoryGroupPosition(int i) {
        return this.mDirectoryGroupPosition == i;
    }

    public int getDirectoryGroupPosition() {
        return this.mDirectoryGroupPosition;
    }

    public boolean isDirectoryGroupDisplayed() {
        return -1 != this.mDirectoryGroupPosition;
    }

    public void onGroupCollapsed(int i) {
        super.onGroupCollapsed(i);
        if (this.mListener != null) {
            this.mListener.onGroupCollapsedNotif(i);
        }
    }

    public void onGroupExpanded(int i) {
        super.onGroupExpanded(i);
        if (this.mListener != null) {
            this.mListener.onGroupExpandedNotif(i);
        }
    }

    private ArrayList<ArrayList<RoomSummary>> buildSummariesByGroups(Collection<RoomSummary> collection) {
        int i;
        boolean z;
        boolean z2;
        ArrayList<ArrayList<RoomSummary>> arrayList = new ArrayList<>();
        this.mRoomByAliasGroupPosition = -1;
        this.mDirectoryGroupPosition = -1;
        this.mInvitedGroupPosition = -1;
        this.mFavouritesGroupPosition = -1;
        this.mNoTagGroupPosition = -1;
        this.mLowPriorGroupPosition = -1;
        if (collection != null) {
            RoomSummary roomSummary = new RoomSummary();
            List roomIdsWithTag = this.mMxSession.roomIdsWithTag(RoomTag.ROOM_TAG_FAVOURITE);
            List roomIdsWithTag2 = this.mMxSession.roomIdsWithTag(RoomTag.ROOM_TAG_LOW_PRIORITY);
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList(roomIdsWithTag.size());
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList(roomIdsWithTag2.size());
            fillList(arrayList3, roomSummary, roomIdsWithTag.size());
            fillList(arrayList4, roomSummary, roomIdsWithTag2.size());
            for (RoomSummary roomSummary2 : collection) {
                String roomId = roomSummary2.getRoomId();
                Room room = this.mMxSession.getDataHandler().getStore().getRoom(roomId);
                if (room == null || !isMatchedPattern(room) || room.isConferenceUserRoom()) {
                    if (room == null) {
                        String str = this.DBG_CLASS_NAME;
                        StringBuilder sb = new StringBuilder();
                        sb.append("buildSummariesBySections ");
                        sb.append(roomId);
                        sb.append(" has no known room");
                        Log.m211e(str, sb.toString());
                    }
                } else if (room.isInvited()) {
                    arrayList2.add(roomSummary2);
                } else {
                    int indexOf = roomIdsWithTag.indexOf(roomId);
                    if (indexOf >= 0) {
                        arrayList3.set(indexOf, roomSummary2);
                    } else {
                        int indexOf2 = roomIdsWithTag2.indexOf(roomId);
                        if (indexOf2 >= 0) {
                            arrayList4.set(indexOf2, roomSummary2);
                        } else {
                            arrayList5.add(roomSummary2);
                        }
                    }
                }
            }
            int i2 = 0;
            if (this.mIsSearchMode || this.mDisplayDirectoryGroupWhenEmpty || this.mForceDirectoryGroupDisplay) {
                if (!TextUtils.isEmpty(this.mSearchedPattern)) {
                    if (this.mSearchedPattern.startsWith("!")) {
                        int indexOf3 = this.mSearchedPattern.indexOf(":");
                        if (indexOf3 > 0) {
                            indexOf3 = this.mSearchedPattern.indexOf(".", indexOf3);
                        }
                        if (indexOf3 > 0) {
                            z = true;
                            z2 = false;
                            if (z || z2) {
                                this.mRoomByAliasGroupPosition = 0;
                                i2 = 1;
                            }
                        }
                    } else if (this.mSearchedPattern.startsWith("#")) {
                        int indexOf4 = this.mSearchedPattern.indexOf(":");
                        if (indexOf4 > 0) {
                            indexOf4 = this.mSearchedPattern.indexOf(".", indexOf4);
                        }
                        z2 = indexOf4 > 0;
                        z = false;
                        this.mRoomByAliasGroupPosition = 0;
                        i2 = 1;
                    }
                    z = false;
                    z2 = false;
                    this.mRoomByAliasGroupPosition = 0;
                    i2 = 1;
                }
                i = i2 + 1;
                this.mDirectoryGroupPosition = i2;
                arrayList.add(new ArrayList());
            } else {
                i = 0;
            }
            if (arrayList2.size() != 0) {
                Collections.reverse(arrayList2);
                arrayList.add(arrayList2);
                this.mInvitedGroupPosition = i;
                i++;
            }
            do {
            } while (arrayList3.remove(roomSummary));
            if (arrayList3.size() != 0) {
                arrayList.add(arrayList3);
                this.mFavouritesGroupPosition = i;
                i++;
            }
            if (arrayList5.size() != 0) {
                arrayList.add(arrayList5);
                this.mNoTagGroupPosition = i;
                i++;
            }
            do {
            } while (arrayList4.remove(roomSummary));
            if (arrayList4.size() != 0) {
                arrayList.add(arrayList4);
                this.mLowPriorGroupPosition = i;
                i++;
            }
            if (this.mDisplayDirectoryGroupWhenEmpty && !this.mForceDirectoryGroupDisplay && i > 1) {
                arrayList.remove(this.mDirectoryGroupPosition);
                this.mRoomByAliasGroupPosition = -1;
                this.mDirectoryGroupPosition = -1;
                this.mInvitedGroupPosition--;
                this.mFavouritesGroupPosition--;
                this.mNoTagGroupPosition--;
                this.mLowPriorGroupPosition--;
            }
        }
        return arrayList;
    }

    public RoomSummary getRoomSummaryAt(int i, int i2) {
        return (RoomSummary) ((ArrayList) this.mSummaryListByGroupPosition.get(i)).get(i2);
    }

    public boolean resetUnreadCount(int i, int i2) {
        RoomSummary roomSummaryAt = getRoomSummaryAt(i, i2);
        if (roomSummaryAt != null) {
            Room roomFromRoomSummary = roomFromRoomSummary(roomSummaryAt);
            if (roomFromRoomSummary != null) {
                roomFromRoomSummary.sendReadReceipt();
            }
        }
        return false;
    }

    private Room roomFromRoomSummary(RoomSummary roomSummary) {
        if (roomSummary == null) {
            return null;
        }
        String matrixId = roomSummary.getMatrixId();
        if (matrixId == null) {
            return null;
        }
        MXSession mXSession = Matrix.getMXSession(this.mContext, matrixId);
        if (mXSession == null || !mXSession.isAlive()) {
            return null;
        }
        return mXSession.getDataHandler().getStore().getRoom(roomSummary.getRoomId());
    }

    private void refreshSummariesList() {
        if (this.mMxSession != null) {
            MXDataHandler dataHandler = this.mMxSession.getDataHandler();
            if (dataHandler == null || dataHandler.getStore() == null) {
                Log.m217w(this.DBG_CLASS_NAME, "## refreshSummariesList(): unexpected null values - return");
                return;
            }
            ArrayList arrayList = new ArrayList(dataHandler.getStore().getSummaries());
            Collections.sort(arrayList, new Comparator<RoomSummary>() {
                public int compare(RoomSummary roomSummary, RoomSummary roomSummary2) {
                    if (!(roomSummary == null || roomSummary.getLatestReceivedEvent() == null)) {
                        if (roomSummary2 == null || roomSummary2.getLatestReceivedEvent() == null) {
                            return -1;
                        }
                        long originServerTs = roomSummary2.getLatestReceivedEvent().getOriginServerTs() - roomSummary.getLatestReceivedEvent().getOriginServerTs();
                        if (originServerTs <= 0) {
                            if (originServerTs < 0) {
                                return -1;
                            }
                            return 0;
                        }
                    }
                    return 1;
                }
            });
            this.mSummaryListByGroupPosition = buildSummariesByGroups(arrayList);
        }
    }

    public void notifyDataSetChanged() {
        if (!this.mIsDragAndDropMode) {
            refreshSummariesList();
        }
        super.notifyDataSetChanged();
    }

    public int getGroupCount() {
        if (this.mSummaryListByGroupPosition != null) {
            return this.mSummaryListByGroupPosition.size();
        }
        return 0;
    }

    public Object getGroup(int i) {
        return getGroupTitle(i);
    }

    public long getGroupId(int i) {
        return (long) getGroupTitle(i).hashCode();
    }

    public int getChildrenCount(int i) {
        if (this.mDirectoryGroupPosition == i || this.mRoomByAliasGroupPosition == i) {
            return 1;
        }
        return ((ArrayList) this.mSummaryListByGroupPosition.get(i)).size();
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mHeaderLayoutResourceId, null);
        }
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.heading);
        if (textView != null) {
            textView.setText(getGroupTitle(i));
        }
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.heading_image);
        if (this.mIsSearchMode) {
            imageView.setVisibility(8);
        } else if (z) {
            imageView.setImageResource(C1299R.C1300drawable.ic_material_expand_less_black);
        } else {
            imageView.setImageResource(C1299R.C1300drawable.ic_material_expand_more_black);
        }
        return view;
    }

    private static void setUnreadBackground(View view, int i) {
        if (view != null) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(0);
            gradientDrawable.setCornerRadius(100.0f);
            gradientDrawable.setColor(i);
            view.setBackground(gradientDrawable);
        }
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        TextView textView;
        ImageView imageView;
        int i3;
        View view2;
        ImageView imageView2;
        View view3;
        View view4;
        View view5;
        View view6;
        View view7;
        ImageView imageView3;
        boolean z2;
        View view8;
        int i4;
        int i5;
        TextView textView2;
        View view9;
        int i6;
        int i7;
        View view10;
        int i8;
        View view11;
        int i9;
        boolean z3;
        int i10;
        int i11;
        View view12;
        View view13;
        int i12;
        int i13 = i;
        if (this.mSummaryListByGroupPosition == null) {
            return null;
        }
        View inflate = view == null ? this.mLayoutInflater.inflate(this.mChildLayoutResourceId, viewGroup, false) : view;
        if (!this.mMxSession.isAlive()) {
            return inflate;
        }
        int color = ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.riot_primary_text_color);
        int color2 = ContextCompat.getColor(this.mContext, C1299R.color.vector_fuchsia_color);
        int color3 = ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.default_text_light_color);
        int color4 = ContextCompat.getColor(this.mContext, C1299R.color.vector_green_color);
        int color5 = ContextCompat.getColor(this.mContext, C1299R.color.vector_silver_color);
        ImageView imageView4 = (ImageView) inflate.findViewById(C1299R.C1301id.room_avatar);
        TextView textView3 = (TextView) inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_roomName);
        TextView textView4 = (TextView) inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_roomMessage);
        View findViewById = inflate.findViewById(C1299R.C1301id.bing_indicator_unread_message);
        TextView textView5 = (TextView) inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_ts);
        View findViewById2 = inflate.findViewById(C1299R.C1301id.recents_separator);
        View findViewById3 = inflate.findViewById(C1299R.C1301id.recents_groups_separator_line);
        View findViewById4 = inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_action);
        ImageView imageView5 = (ImageView) inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_action_image);
        int i14 = color4;
        TextView textView6 = (TextView) inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_unread_count);
        int i15 = color5;
        View findViewById5 = inflate.findViewById(C1299R.C1301id.room_avatar_direct_chat_icon);
        View view14 = findViewById2;
        View findViewById6 = inflate.findViewById(C1299R.C1301id.room_avatar_encrypted_icon);
        View findViewById7 = inflate.findViewById(C1299R.C1301id.recents_groups_invitation_group);
        ImageView imageView6 = imageView5;
        Button button = (Button) inflate.findViewById(C1299R.C1301id.recents_invite_preview_button);
        int i16 = color2;
        Button button2 = (Button) inflate.findViewById(C1299R.C1301id.recents_invite_reject_button);
        View findViewById8 = inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_show_more_layout);
        Button button3 = button;
        View view15 = inflate;
        View findViewById9 = inflate.findViewById(C1299R.C1301id.roomSummaryAdapter_action_click_area);
        if (this.mDirectoryGroupPosition == i13) {
            view7 = findViewById5;
            view5 = findViewById7;
            view6 = findViewById6;
            imageView = imageView4;
            view3 = findViewById3;
            view4 = view14;
            imageView2 = imageView6;
            view2 = findViewById9;
            i3 = 8;
            textView = textView4;
        } else if (this.mRoomByAliasGroupPosition == i13) {
            view7 = findViewById5;
            view5 = findViewById7;
            view6 = findViewById6;
            imageView = imageView4;
            textView = textView4;
            view3 = findViewById3;
            view4 = view14;
            imageView2 = imageView6;
            view2 = findViewById9;
            i3 = 8;
        } else {
            findViewById8.setVisibility(8);
            RoomSummary roomSummary = (RoomSummary) ((ArrayList) this.mSummaryListByGroupPosition.get(i13)).get(i2);
            Room room = this.mMxSession.getDataHandler().getStore().getRoom(roomSummary.getRoomId());
            int unreadEventsCount = roomSummary.getUnreadEventsCount();
            if (room != null) {
                i4 = room.getHighlightCount();
                view8 = findViewById7;
                i5 = this.mMxSession.getDataHandler().getBingRulesManager().isRoomMentionOnly(room.getRoomId()) ? i4 : room.getNotificationCount();
            } else {
                view8 = findViewById7;
                i5 = 0;
                i4 = 0;
            }
            CharSequence childMessageToDisplay = getChildMessageToDisplay(roomSummary);
            View view16 = findViewById6;
            View view17 = findViewById5;
            String roomDisplayName = VectorUtils.getRoomDisplayName(this.mContext, this.mMxSession, room);
            TextView textView7 = textView6;
            VectorUtils.loadRoomAvatar(this.mContext, this.mMxSession, imageView4, room);
            textView3.setText(roomDisplayName);
            textView3.setTextColor(color);
            textView3.setTypeface(null, unreadEventsCount != 0 ? 1 : 0);
            textView4.setText(childMessageToDisplay);
            textView5.setText(getFormattedTimestamp(roomSummary.getLatestReceivedEvent()));
            textView5.setTextColor(color3);
            textView5.setTypeface(null, 0);
            int i17 = i4 != 0 ? i16 : i5 != 0 ? i14 : unreadEventsCount != 0 ? i15 : 0;
            findViewById.setBackgroundColor(i17);
            if (i5 != 0) {
                textView2 = textView7;
                textView2.setVisibility(0);
                textView2.setText(String.valueOf(i5));
                textView2.setTypeface(null, 1);
                setUnreadBackground(textView2, i17);
            } else {
                textView2 = textView7;
                textView2.setVisibility(8);
            }
            boolean isInvited = room != null ? room.isInvited() : false;
            if (room != null) {
                if (RoomUtils.isDirectChat(this.mMxSession, room.getRoomId())) {
                    view12 = view17;
                    i11 = 0;
                } else {
                    view12 = view17;
                    i11 = 8;
                }
                view12.setVisibility(i11);
                if (room.isEncrypted()) {
                    view13 = view16;
                    i12 = 0;
                } else {
                    view13 = view16;
                    i12 = 8;
                }
                view13.setVisibility(i12);
            } else {
                View view18 = view16;
                view17.setVisibility(8);
                view18.setVisibility(8);
            }
            findViewById.setVisibility(isInvited ? 4 : 0);
            if (isInvited) {
                view9 = view8;
                i6 = 0;
            } else {
                view9 = view8;
                i6 = 8;
            }
            view9.setVisibility(i6);
            final String roomId = roomSummary.getRoomId();
            if (isInvited) {
                findViewById9.setVisibility(8);
                button3.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VectorRoomSummaryAdapter.this.mListener != null) {
                            VectorRoomSummaryAdapter.this.mListener.onPreviewRoom(VectorRoomSummaryAdapter.this.mMxSession, roomId);
                        }
                    }
                });
                button2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VectorRoomSummaryAdapter.this.mListener != null) {
                            VectorRoomSummaryAdapter.this.mListener.onRejectInvitation(VectorRoomSummaryAdapter.this.mMxSession, roomId);
                        }
                    }
                });
                textView2.setVisibility(0);
                textView2.setText("!");
                textView2.setTypeface(null, 1);
                setUnreadBackground(textView2, i16);
                textView5.setVisibility(8);
                imageView6.setVisibility(8);
                view10 = findViewById3;
                i8 = i;
                i7 = 8;
            } else {
                ImageView imageView7 = imageView6;
                View view19 = findViewById9;
                i8 = i;
                boolean z4 = i8 == this.mFavouritesGroupPosition;
                if (i8 == this.mLowPriorGroupPosition) {
                    i10 = 0;
                    z3 = true;
                } else {
                    i10 = 0;
                    z3 = false;
                }
                view19.setVisibility(i10);
                View view20 = view19;
                ImageView imageView8 = imageView7;
                view10 = findViewById3;
                i7 = 8;
                final Room room2 = room;
                final View view21 = findViewById4;
                final boolean z5 = z4;
                final boolean z6 = z3;
                C18394 r0 = new OnClickListener() {
                    public void onClick(View view) {
                        RoomUtils.displayPopupMenu(VectorRoomSummaryAdapter.this.mContext, VectorRoomSummaryAdapter.this.mMxSession, room2, view21, z5, z6, VectorRoomSummaryAdapter.this.mMoreActionListener);
                    }
                };
                view20.setOnClickListener(r0);
                textView5.setVisibility(this.mIsSearchMode ? 4 : 0);
                imageView8.setVisibility(this.mIsSearchMode ? 4 : 0);
            }
            if (z) {
                view11 = view14;
                i9 = 8;
            } else {
                view11 = view14;
                i9 = 0;
            }
            view11.setVisibility(i9);
            if (z && i8 + 1 < getGroupCount()) {
                i7 = 0;
            }
            view10.setVisibility(i7);
            return view15;
        }
        findViewById.setVisibility(4);
        textView5.setVisibility(i3);
        imageView2.setVisibility(i3);
        view5.setVisibility(i3);
        view4.setVisibility(i3);
        view3.setVisibility(0);
        findViewById8.setVisibility(0);
        view2.setVisibility(i3);
        textView6.setVisibility(i3);
        view7.setVisibility(i3);
        view6.setVisibility(i3);
        if (this.mDirectoryGroupPosition == i13) {
            textView3.setText(this.mContext.getResources().getString(C1299R.string.directory_search_results_title));
            if (TextUtils.isEmpty(this.mSearchedPattern)) {
                TextView textView8 = textView;
                if (this.mPublicRoomsCount == null) {
                    textView8.setText(null);
                } else {
                    z2 = true;
                    textView8.setText(this.mContext.getResources().getQuantityString(C1299R.plurals.directory_search_rooms, this.mPublicRoomsCount.intValue(), new Object[]{this.mPublicRoomsCount}));
                    imageView3 = imageView;
                    imageView3.setImageBitmap(VectorUtils.getAvatar(imageView3.getContext(), VectorUtils.getAvatarColor(null), null, z2));
                }
            } else if (this.mMatchedPublicRoomsCount == null) {
                textView.setText(this.mContext.getResources().getString(C1299R.string.directory_searching_title));
            } else {
                TextView textView9 = textView;
                String num = this.mMatchedPublicRoomsCount.toString();
                if (this.mMatchedPublicRoomsCount.intValue() >= 20) {
                    num = "> 20";
                }
                textView9.setText(this.mContext.getResources().getQuantityString(C1299R.plurals.directory_search_rooms_for, this.mMatchedPublicRoomsCount.intValue(), new Object[]{num, this.mSearchedPattern}));
            }
            imageView3 = imageView;
            z2 = true;
            imageView3.setImageBitmap(VectorUtils.getAvatar(imageView3.getContext(), VectorUtils.getAvatarColor(null), null, z2));
        } else {
            ImageView imageView9 = imageView;
            TextView textView10 = textView;
            textView3.setText(this.mSearchedPattern);
            textView10.setText("");
            imageView9.setImageBitmap(VectorUtils.getAvatar(imageView9.getContext(), VectorUtils.getAvatarColor(null), "@", true));
        }
        return view15;
    }

    private String getMemberDisplayNameFromUserId(String str, String str2) {
        if (!(str == null || str2 == null)) {
            MXSession mXSession = Matrix.getMXSession(this.mContext, str);
            if (mXSession != null && mXSession.isAlive()) {
                User user = mXSession.getDataHandler().getStore().getUser(str2);
                return (user == null || TextUtils.isEmpty(user.displayname)) ? str2 : user.displayname;
            }
        }
        return null;
    }

    private CharSequence getChildMessageToDisplay(RoomSummary roomSummary) {
        String str;
        String str2;
        CharSequence charSequence = null;
        if (roomSummary == null) {
            return null;
        }
        if (roomSummary.getLatestReceivedEvent() != null) {
            RiotEventDisplay riotEventDisplay = new RiotEventDisplay(this.mContext, roomSummary.getLatestReceivedEvent(), roomSummary.getLatestRoomState());
            riotEventDisplay.setPrependMessagesWithAuthor(true);
            charSequence = riotEventDisplay.getTextualDisplay(Integer.valueOf(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.riot_primary_text_color)));
        }
        if (!roomSummary.isInvited() || roomSummary.getInviterUserId() == null) {
            return charSequence;
        }
        RoomState latestRoomState = roomSummary.getLatestRoomState();
        String inviterUserId = roomSummary.getInviterUserId();
        String matrixId = roomSummary.getMatrixId();
        if (latestRoomState != null) {
            str = latestRoomState.getMemberName(inviterUserId);
            str2 = latestRoomState.getMemberName(matrixId);
        } else {
            str = getMemberDisplayNameFromUserId(roomSummary.getMatrixId(), inviterUserId);
            str2 = getMemberDisplayNameFromUserId(roomSummary.getMatrixId(), matrixId);
        }
        if (TextUtils.equals(this.mMxSession.getMyUserId(), roomSummary.getMatrixId())) {
            return this.mContext.getString(C1299R.string.notice_room_invite_you, new Object[]{str});
        }
        return this.mContext.getString(C1299R.string.notice_room_invite, new Object[]{str, str2});
    }

    public void setSearchPattern(String str) {
        if (str != null) {
            str = str.trim().toLowerCase(VectorApp.getApplicationLocale());
            if (TextUtils.getTrimmedLength(str) == 0) {
                str = null;
            }
        }
        if (!TextUtils.equals(str, this.mSearchedPattern)) {
            this.mSearchedPattern = str;
            this.mMatchedPublicRoomsCount = null;
            notifyDataSetChanged();
        }
    }

    public String getSearchedPattern() {
        return this.mSearchedPattern;
    }

    public void setPublicRoomsCount(Integer num) {
        if (num != this.mPublicRoomsCount) {
            this.mPublicRoomsCount = num;
            super.notifyDataSetChanged();
        }
    }

    public int getMatchedPublicRoomsCount() {
        if (this.mMatchedPublicRoomsCount == null) {
            return 0;
        }
        return this.mMatchedPublicRoomsCount.intValue();
    }

    public void setMatchedPublicRoomsCount(Integer num) {
        if (num != this.mMatchedPublicRoomsCount) {
            this.mMatchedPublicRoomsCount = num;
            super.notifyDataSetChanged();
        }
    }

    public boolean isInDragAndDropMode() {
        return this.mIsDragAndDropMode;
    }

    public void setIsDragAndDropMode(boolean z) {
        this.mIsDragAndDropMode = z;
    }

    public void moveChildView(int i, int i2, int i3, int i4) {
        ArrayList arrayList = (ArrayList) this.mSummaryListByGroupPosition.get(i);
        ArrayList arrayList2 = (ArrayList) this.mSummaryListByGroupPosition.get(i3);
        RoomSummary roomSummary = (RoomSummary) arrayList.get(i2);
        arrayList.remove(i2);
        if (i4 >= arrayList2.size()) {
            arrayList2.add(roomSummary);
        } else {
            arrayList2.add(i4, roomSummary);
        }
    }

    public boolean isInvitedRoomPosition(int i) {
        return this.mInvitedGroupPosition == i;
    }

    public boolean isFavouriteRoomPosition(int i) {
        return this.mFavouritesGroupPosition == i;
    }

    public boolean isNoTagRoomPosition(int i) {
        return this.mNoTagGroupPosition == i;
    }

    public boolean isLowPriorityRoomPosition(int i) {
        return this.mLowPriorGroupPosition == i;
    }
}
