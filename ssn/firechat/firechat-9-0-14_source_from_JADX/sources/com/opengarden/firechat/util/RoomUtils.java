package com.opengarden.firechat.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ShortcutManager;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.p000v4.view.GravityCompat;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.adapters.AdapterUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.RoomNotificationState;
import com.opengarden.firechat.matrixsdk.util.EventDisplay;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class RoomUtils {
    private static final String LOG_TAG = "RoomUtils";

    public interface HistoricalRoomActionListener {
        void onForgotRoom(Room room);
    }

    public interface MoreActionListener {
        void addHomeScreenShortcut(MXSession mXSession, String str);

        void moveToConversations(MXSession mXSession, String str);

        void moveToFavorites(MXSession mXSession, String str);

        void moveToLowPriority(MXSession mXSession, String str);

        void onForgetRoom(MXSession mXSession, String str);

        void onLeaveRoom(MXSession mXSession, String str);

        void onToggleDirectChat(MXSession mXSession, String str);

        void onUpdateRoomNotificationsState(MXSession mXSession, String str, RoomNotificationState roomNotificationState);
    }

    public static Comparator<Room> getRoomsDateComparator(final MXSession mXSession, final boolean z) {
        return new Comparator<Room>() {
            private Comparator<RoomSummary> mRoomSummaryComparator;
            private final HashMap<String, RoomSummary> mSummaryByRoomIdMap = new HashMap<>();

            private Comparator<RoomSummary> getSummaryComparator() {
                if (this.mRoomSummaryComparator == null) {
                    this.mRoomSummaryComparator = RoomUtils.getRoomSummaryComparator(z);
                }
                return this.mRoomSummaryComparator;
            }

            private RoomSummary getSummary(String str) {
                if (TextUtils.isEmpty(str)) {
                    return null;
                }
                RoomSummary roomSummary = (RoomSummary) this.mSummaryByRoomIdMap.get(str);
                if (roomSummary == null) {
                    roomSummary = mXSession.getDataHandler().getStore().getSummary(str);
                    if (roomSummary != null) {
                        this.mSummaryByRoomIdMap.put(str, roomSummary);
                    }
                }
                return roomSummary;
            }

            public int compare(Room room, Room room2) {
                return getSummaryComparator().compare(getSummary(room.getRoomId()), getSummary(room2.getRoomId()));
            }
        };
    }

    public static Comparator<Room> getNotifCountRoomsComparator(final MXSession mXSession, final boolean z, final boolean z2) {
        return new Comparator<Room>() {
            private Comparator<RoomSummary> mRoomSummaryComparator;
            private final HashMap<String, RoomSummary> mSummaryByRoomIdMap = new HashMap<>();

            private Comparator<RoomSummary> getSummaryComparator() {
                if (this.mRoomSummaryComparator == null) {
                    this.mRoomSummaryComparator = RoomUtils.getNotifCountRoomSummaryComparator(mXSession.getDataHandler().getBingRulesManager(), z, z2);
                }
                return this.mRoomSummaryComparator;
            }

            private RoomSummary getSummary(String str) {
                if (TextUtils.isEmpty(str)) {
                    return null;
                }
                RoomSummary roomSummary = (RoomSummary) this.mSummaryByRoomIdMap.get(str);
                if (roomSummary == null) {
                    roomSummary = mXSession.getDataHandler().getStore().getSummary(str);
                    if (roomSummary != null) {
                        this.mSummaryByRoomIdMap.put(str, roomSummary);
                    }
                }
                return roomSummary;
            }

            public int compare(Room room, Room room2) {
                return getSummaryComparator().compare(getSummary(room.getRoomId()), getSummary(room2.getRoomId()));
            }
        };
    }

    public static Comparator<Room> getHistoricalRoomsComparator(final MXSession mXSession, final boolean z) {
        return new Comparator<Room>() {
            public int compare(Room room, Room room2) {
                return RoomUtils.getRoomSummaryComparator(z).compare(mXSession.getDataHandler().getStore(room.getRoomId()).getSummary(room.getRoomId()), mXSession.getDataHandler().getStore(room2.getRoomId()).getSummary(room2.getRoomId()));
            }
        };
    }

    /* access modifiers changed from: private */
    public static Comparator<RoomSummary> getRoomSummaryComparator(final boolean z) {
        return new Comparator<RoomSummary>() {
            /* JADX WARNING: Removed duplicated region for block: B:15:0x0039  */
            /* JADX WARNING: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int compare(com.opengarden.firechat.matrixsdk.data.RoomSummary r7, com.opengarden.firechat.matrixsdk.data.RoomSummary r8) {
                /*
                    r6 = this;
                    r0 = -1
                    r1 = 1
                    if (r7 == 0) goto L_0x0034
                    com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r7.getLatestReceivedEvent()
                    if (r2 != 0) goto L_0x000b
                    goto L_0x0034
                L_0x000b:
                    if (r8 == 0) goto L_0x0035
                    com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r8.getLatestReceivedEvent()
                    if (r2 != 0) goto L_0x0014
                    goto L_0x0035
                L_0x0014:
                    com.opengarden.firechat.matrixsdk.rest.model.Event r8 = r8.getLatestReceivedEvent()
                    long r2 = r8.getOriginServerTs()
                    com.opengarden.firechat.matrixsdk.rest.model.Event r7 = r7.getLatestReceivedEvent()
                    long r7 = r7.getOriginServerTs()
                    long r4 = r2 - r7
                    r7 = 0
                    int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                    if (r2 <= 0) goto L_0x002d
                    goto L_0x0034
                L_0x002d:
                    int r1 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                    if (r1 >= 0) goto L_0x0032
                    goto L_0x0035
                L_0x0032:
                    r0 = 0
                    goto L_0x0035
                L_0x0034:
                    r0 = 1
                L_0x0035:
                    boolean r7 = r1
                    if (r7 == 0) goto L_0x003a
                    int r0 = -r0
                L_0x003a:
                    return r0
                */
                throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.RoomUtils.C29114.compare(com.opengarden.firechat.matrixsdk.data.RoomSummary, com.opengarden.firechat.matrixsdk.data.RoomSummary):int");
            }
        };
    }

    /* access modifiers changed from: private */
    public static Comparator<RoomSummary> getNotifCountRoomSummaryComparator(final BingRulesManager bingRulesManager, final boolean z, final boolean z2) {
        return new Comparator<RoomSummary>() {
            public int compare(RoomSummary roomSummary, RoomSummary roomSummary2) {
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                int i6;
                if (roomSummary != null) {
                    i3 = roomSummary.getHighlightCount();
                    i2 = roomSummary.getNotificationCount();
                    i = roomSummary.getUnreadEventsCount();
                    if (bingRulesManager.isRoomMentionOnly(roomSummary.getRoomId())) {
                        i2 = i3;
                    }
                } else {
                    i3 = 0;
                    i2 = 0;
                    i = 0;
                }
                if (roomSummary2 != null) {
                    i6 = roomSummary2.getHighlightCount();
                    i5 = roomSummary2.getNotificationCount();
                    i4 = roomSummary2.getUnreadEventsCount();
                    if (bingRulesManager.isRoomMentionOnly(roomSummary2.getRoomId())) {
                        i5 = i6;
                    }
                } else {
                    i6 = 0;
                    i5 = 0;
                    i4 = 0;
                }
                if (!(roomSummary == null || roomSummary.getLatestReceivedEvent() == null)) {
                    if (roomSummary2 == null || roomSummary2.getLatestReceivedEvent() == null) {
                        return -1;
                    }
                    if (!z || i6 <= 0 || i3 != 0) {
                        if (z && i6 == 0 && i3 > 0) {
                            return -1;
                        }
                        if (!z || i5 <= 0 || i2 != 0) {
                            if (z && i5 == 0 && i2 > 0) {
                                return -1;
                            }
                            if (!z2 || i4 <= 0 || i != 0) {
                                if (z2 && i4 == 0 && i > 0) {
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
                        }
                    }
                }
                return 1;
            }
        };
    }

    public static String getRoomTimestamp(Context context, Event event) {
        String tsToString = AdapterUtils.tsToString(context, event.getOriginServerTs(), false);
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(C1299R.string.today));
        sb.append(StringUtils.SPACE);
        String sb2 = sb.toString();
        return tsToString.startsWith(sb2) ? tsToString.substring(sb2.length()) : tsToString;
    }

    public static CharSequence getRoomMessageToDisplay(Context context, MXSession mXSession, RoomSummary roomSummary) {
        String str;
        String str2;
        CharSequence charSequence = null;
        if (roomSummary == null) {
            return null;
        }
        if (roomSummary.getLatestReceivedEvent() != null) {
            EventDisplay eventDisplay = new EventDisplay(context, roomSummary.getLatestReceivedEvent(), roomSummary.getLatestRoomState());
            eventDisplay.setPrependMessagesWithAuthor(true);
            charSequence = eventDisplay.getTextualDisplay(Integer.valueOf(ThemeUtils.INSTANCE.getColor(context, C1299R.attr.room_notification_text_color)));
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
            str = getMemberDisplayNameFromUserId(context, roomSummary.getMatrixId(), inviterUserId);
            str2 = getMemberDisplayNameFromUserId(context, roomSummary.getMatrixId(), matrixId);
        }
        if (TextUtils.equals(mXSession.getMyUserId(), roomSummary.getMatrixId())) {
            return context.getString(C1299R.string.notice_room_invite_you, new Object[]{str});
        }
        return context.getString(C1299R.string.notice_room_invite, new Object[]{str, str2});
    }

    private static String getMemberDisplayNameFromUserId(Context context, String str, String str2) {
        if (!(str == null || str2 == null)) {
            MXSession mXSession = Matrix.getMXSession(context, str);
            if (mXSession != null && mXSession.isAlive()) {
                User user = mXSession.getDataHandler().getStore().getUser(str2);
                return (user == null || TextUtils.isEmpty(user.displayname)) ? str2 : user.displayname;
            }
        }
        return null;
    }

    public static void displayPopupMenu(Context context, MXSession mXSession, Room room, View view, boolean z, boolean z2, @NonNull MoreActionListener moreActionListener) {
        if (moreActionListener != null) {
            displayPopupMenu(context, mXSession, room, view, z, z2, moreActionListener, null);
        }
    }

    public static void displayHistoricalRoomMenu(Context context, MXSession mXSession, Room room, View view, @NonNull HistoricalRoomActionListener historicalRoomActionListener) {
        if (historicalRoomActionListener != null) {
            displayPopupMenu(context, mXSession, room, view, false, false, null, historicalRoomActionListener);
        }
    }

    @SuppressLint({"NewApi"})
    private static void displayPopupMenu(Context context, MXSession mXSession, Room room, View view, boolean z, boolean z2, MoreActionListener moreActionListener, HistoricalRoomActionListener historicalRoomActionListener) {
        PopupMenu popupMenu;
        Context context2 = context;
        final Room room2 = room;
        View view2 = view;
        final HistoricalRoomActionListener historicalRoomActionListener2 = historicalRoomActionListener;
        if (room2 != null) {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context2, C1299R.style.PopMenuStyle);
            if (VERSION.SDK_INT >= 19) {
                popupMenu = new PopupMenu(contextThemeWrapper, view2, GravityCompat.END);
            } else {
                popupMenu = new PopupMenu(contextThemeWrapper, view2);
            }
            PopupMenu popupMenu2 = popupMenu;
            popupMenu2.getMenuInflater().inflate(C1299R.C1302menu.vector_home_room_settings, popupMenu2.getMenu());
            CommonActivityUtils.tintMenuIcons(popupMenu2.getMenu(), ThemeUtils.INSTANCE.getColor(context2, C1299R.attr.settings_icon_tint_color));
            if (room2.isLeft()) {
                popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.active_room_actions, false);
                popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.add_shortcut_actions, false);
                popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.historical_room_actions, true);
                if (historicalRoomActionListener2 != null) {
                    popupMenu2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getItemId() == C1299R.C1301id.action_forget_room) {
                                historicalRoomActionListener2.onForgotRoom(room2);
                            }
                            return true;
                        }
                    });
                }
            } else {
                popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.active_room_actions, true);
                if (VERSION.SDK_INT < 26) {
                    popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.add_shortcut_actions, false);
                } else if (!((ShortcutManager) context2.getSystemService(ShortcutManager.class)).isRequestPinShortcutSupported()) {
                    popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.add_shortcut_actions, false);
                } else {
                    popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.add_shortcut_actions, true);
                }
                popupMenu2.getMenu().setGroupVisible(C1299R.C1301id.historical_room_actions, false);
                RoomNotificationState roomNotificationState = mXSession.getDataHandler().getBingRulesManager().getRoomNotificationState(room2.getRoomId());
                if (RoomNotificationState.ALL_MESSAGES_NOISY != roomNotificationState) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_notifications_noisy).setIcon(null);
                }
                if (RoomNotificationState.ALL_MESSAGES != roomNotificationState) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_notifications_all_message).setIcon(null);
                }
                if (RoomNotificationState.MENTIONS_ONLY != roomNotificationState) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_notifications_mention_only).setIcon(null);
                }
                if (RoomNotificationState.MUTE != roomNotificationState) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_notifications_mute).setIcon(null);
                }
                if (!z) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_select_fav).setIcon(null);
                }
                if (!z2) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_select_deprioritize).setIcon(null);
                }
                if (!room2.isDirect()) {
                    popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_select_direct_chat).setIcon(null);
                }
                RoomMember member = room2.getMember(mXSession.getMyUserId());
                final boolean z3 = member != null && member.kickedOrBanned();
                if (z3) {
                    MenuItem findItem = popupMenu2.getMenu().findItem(C1299R.C1301id.ic_action_select_remove);
                    if (findItem != null) {
                        findItem.setTitle(C1299R.string.forget_room);
                    }
                }
                if (moreActionListener != null) {
                    final MoreActionListener moreActionListener2 = moreActionListener;
                    final MXSession mXSession2 = mXSession;
                    final boolean z4 = z;
                    final boolean z5 = z2;
                    C29147 r1 = new OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int itemId = menuItem.getItemId();
                            if (itemId != C1299R.C1301id.ic_action_add_homescreen_shortcut) {
                                switch (itemId) {
                                    case C1299R.C1301id.ic_action_notifications_all_message /*2131296582*/:
                                        moreActionListener2.onUpdateRoomNotificationsState(mXSession2, room2.getRoomId(), RoomNotificationState.ALL_MESSAGES);
                                        break;
                                    case C1299R.C1301id.ic_action_notifications_mention_only /*2131296583*/:
                                        moreActionListener2.onUpdateRoomNotificationsState(mXSession2, room2.getRoomId(), RoomNotificationState.MENTIONS_ONLY);
                                        break;
                                    case C1299R.C1301id.ic_action_notifications_mute /*2131296584*/:
                                        moreActionListener2.onUpdateRoomNotificationsState(mXSession2, room2.getRoomId(), RoomNotificationState.MUTE);
                                        break;
                                    case C1299R.C1301id.ic_action_notifications_noisy /*2131296585*/:
                                        moreActionListener2.onUpdateRoomNotificationsState(mXSession2, room2.getRoomId(), RoomNotificationState.ALL_MESSAGES_NOISY);
                                        break;
                                    default:
                                        switch (itemId) {
                                            case C1299R.C1301id.ic_action_select_deprioritize /*2131296593*/:
                                                if (!z5) {
                                                    moreActionListener2.moveToLowPriority(mXSession2, room2.getRoomId());
                                                    break;
                                                } else {
                                                    moreActionListener2.moveToConversations(mXSession2, room2.getRoomId());
                                                    break;
                                                }
                                            case C1299R.C1301id.ic_action_select_direct_chat /*2131296594*/:
                                                moreActionListener2.onToggleDirectChat(mXSession2, room2.getRoomId());
                                                break;
                                            case C1299R.C1301id.ic_action_select_fav /*2131296595*/:
                                                if (!z4) {
                                                    moreActionListener2.moveToFavorites(mXSession2, room2.getRoomId());
                                                    break;
                                                } else {
                                                    moreActionListener2.moveToConversations(mXSession2, room2.getRoomId());
                                                    break;
                                                }
                                            case C1299R.C1301id.ic_action_select_remove /*2131296596*/:
                                                if (!z3) {
                                                    moreActionListener2.onLeaveRoom(mXSession2, room2.getRoomId());
                                                    break;
                                                } else {
                                                    moreActionListener2.onForgetRoom(mXSession2, room2.getRoomId());
                                                    break;
                                                }
                                        }
                                }
                            } else {
                                moreActionListener2.addHomeScreenShortcut(mXSession2, room2.getRoomId());
                            }
                            return false;
                        }
                    };
                    popupMenu2.setOnMenuItemClickListener(r1);
                }
            }
            try {
                Field[] declaredFields = popupMenu2.getClass().getDeclaredFields();
                int length = declaredFields.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Field field = declaredFields[i];
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object obj = field.get(popupMenu2);
                        Class.forName(obj.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(true)});
                        break;
                    }
                    i++;
                }
            } catch (Exception e) {
                Exception exc = e;
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## displayPopupMenu() : failed ");
                sb.append(exc.getMessage());
                Log.m211e(str, sb.toString());
            }
            popupMenu2.show();
        }
    }

    public static void showLeaveRoomDialog(Context context, final OnClickListener onClickListener) {
        new Builder(context).setTitle(C1299R.string.room_participants_leave_prompt_title).setMessage(C1299R.string.room_participants_leave_prompt_msg).setPositiveButton(C1299R.string.leave, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onClickListener.onClick(dialogInterface, i);
            }
        }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0065  */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addHomeScreenShortcut(android.content.Context r6, com.opengarden.firechat.matrixsdk.MXSession r7, java.lang.String r8) {
        /*
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 26
            if (r0 >= r1) goto L_0x0007
            return
        L_0x0007:
            java.lang.Class<android.content.pm.ShortcutManager> r0 = android.content.pm.ShortcutManager.class
            java.lang.Object r0 = r6.getSystemService(r0)
            android.content.pm.ShortcutManager r0 = (android.content.pm.ShortcutManager) r0
            boolean r1 = r0.isRequestPinShortcutSupported()
            if (r1 != 0) goto L_0x0016
            return
        L_0x0016:
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r7.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.Room r1 = r1.getRoom(r8)
            if (r1 != 0) goto L_0x0021
            return
        L_0x0021:
            java.lang.String r2 = com.opengarden.firechat.util.VectorUtils.getRoomDisplayName(r6, r7, r1)
            java.lang.String r3 = r1.getAvatarUrl()
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            r4 = 0
            if (r3 != 0) goto L_0x0062
            android.content.res.Resources r3 = r6.getResources()
            r5 = 2131165351(0x7f0700a7, float:1.7944917E38)
            int r3 = r3.getDimensionPixelSize(r5)
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r7 = r7.getMediasCache()
            java.lang.String r1 = r1.getAvatarUrl()
            java.io.File r7 = r7.thumbnailCacheFile(r1, r3)
            if (r7 == 0) goto L_0x0062
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            r1.inPreferredConfig = r3
            java.lang.String r7 = r7.getPath()     // Catch:{ OutOfMemoryError -> 0x005b }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeFile(r7, r1)     // Catch:{ OutOfMemoryError -> 0x005b }
            goto L_0x0063
        L_0x005b:
            java.lang.String r7 = LOG_TAG
            java.lang.String r1 = "decodeFile failed with an oom"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r1)
        L_0x0062:
            r7 = r4
        L_0x0063:
            if (r7 != 0) goto L_0x006e
            int r7 = com.opengarden.firechat.util.VectorUtils.getAvatarColor(r8)
            r1 = 1
            android.graphics.Bitmap r7 = com.opengarden.firechat.util.VectorUtils.getAvatar(r6, r7, r2, r1)
        L_0x006e:
            android.graphics.drawable.Icon r7 = android.graphics.drawable.Icon.createWithBitmap(r7)
            android.content.Intent r1 = new android.content.Intent
            java.lang.Class<com.opengarden.firechat.activity.VectorRoomActivity> r3 = com.opengarden.firechat.activity.VectorRoomActivity.class
            r1.<init>(r6, r3)
            r3 = 268468224(0x10008000, float:2.5342157E-29)
            r1.setFlags(r3)
            java.lang.String r3 = "android.intent.action.VIEW"
            r1.setAction(r3)
            java.lang.String r3 = "EXTRA_ROOM_ID"
            r1.putExtra(r3, r8)
            android.content.pm.ShortcutInfo$Builder r3 = new android.content.pm.ShortcutInfo$Builder
            r3.<init>(r6, r8)
            android.content.pm.ShortcutInfo$Builder r6 = r3.setShortLabel(r2)
            android.content.pm.ShortcutInfo$Builder r6 = r6.setIcon(r7)
            android.content.pm.ShortcutInfo$Builder r6 = r6.setIntent(r1)
            android.content.pm.ShortcutInfo r6 = r6.build()
            r0.requestPinShortcut(r6, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.RoomUtils.addHomeScreenShortcut(android.content.Context, com.opengarden.firechat.matrixsdk.MXSession, java.lang.String):void");
    }

    public static void updateRoomTag(MXSession mXSession, String str, Double d, String str2, ApiCallback<Void> apiCallback) {
        Room room = mXSession.getDataHandler().getRoom(str);
        if (room != null) {
            String str3 = null;
            RoomAccountData accountData = room.getAccountData();
            if (accountData != null && accountData.hasTags()) {
                str3 = (String) accountData.getKeys().iterator().next();
            }
            if (d == null) {
                d = Double.valueOf(0.0d);
                if (str2 != null) {
                    d = mXSession.tagOrderToBeAtIndex(0, Integer.MAX_VALUE, str2);
                }
            }
            room.replaceTag(str3, str2, d, apiCallback);
        }
    }

    public static void toggleDirectChat(MXSession mXSession, String str, ApiCallback<Void> apiCallback) {
        if (mXSession.getDataHandler().getRoom(str) != null) {
            mXSession.toggleDirectChatRoom(str, null, apiCallback);
        }
    }

    public static boolean isDirectChat(MXSession mXSession, String str) {
        return str != null && mXSession.getDataHandler().getDirectChatRoomIdsList().contains(str);
    }

    public static List<Room> getFilteredRooms(Context context, MXSession mXSession, List<Room> list, CharSequence charSequence) {
        String trim = charSequence != null ? charSequence.toString().trim() : null;
        if (TextUtils.isEmpty(trim)) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(Pattern.quote(trim), 2);
        for (Room room : list) {
            if (compile.matcher(VectorUtils.getRoomDisplayName(context, mXSession, room)).find()) {
                arrayList.add(room);
            }
        }
        return arrayList;
    }

    public static String formatUnreadMessagesCounter(int i) {
        if (i <= 0) {
            return null;
        }
        if (i <= 999) {
            return String.valueOf(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(i / 1000);
        sb.append(".");
        sb.append((i % 1000) / 100);
        sb.append("K");
        return sb.toString();
    }
}
