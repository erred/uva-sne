package com.opengarden.firechat.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.listeners.IMessagesAdapterActionsListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.ReceiptData;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.URLPreview;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.group.GroupProfile;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.view.HtmlTagHandler;
import com.opengarden.firechat.util.MatrixLinkMovementMethod;
import com.opengarden.firechat.util.MatrixURLSpan;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorImageGetter;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.PillView;
import com.opengarden.firechat.view.PillView.OnUpdateListener;
import com.opengarden.firechat.view.UrlPreviewView;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

class VectorMessagesAdapterHelper {
    private static final String AVATAR_URL_KEY = "avatar_url";
    private static final String DISPLAYNAME_KEY = "displayname";
    public static final String END_FENCED_BLOCK = "</code></pre>";
    private static final Pattern FENCED_CODE_BLOCK_PATTERN = Pattern.compile("(?m)(?=<pre><code>)|(?<=</code></pre>)");
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMessagesAdapterHelper";
    private static final String MEMBERSHIP_KEY = "membership";
    public static final String START_FENCED_BLOCK = "<pre><code>";
    private static final Set<String> mAllowedHTMLTags = new HashSet(Arrays.asList(new String[]{"font", "del", "h1", "h2", "h3", "h4", "h5", "h6", "blockquote", "p", "a", "ul", "ol", "sup", "sub", "nl", "li", "b", "i", "u", "strong", "em", "strike", "code", "hr", "br", "div", "table", "thead", "caption", "tbody", "tr", "th", "td", "pre", "span", "img"}));
    private static final Pattern mHtmlPatter = Pattern.compile("<(\\w+)[^>]*>", 2);
    /* access modifiers changed from: private */
    public final VectorMessagesAdapter mAdapter;
    private Map<String, String[]> mCodeBlocksMap = new HashMap();
    /* access modifiers changed from: private */
    public final Context mContext;
    private final Set<String> mDismissedPreviews = new HashSet();
    /* access modifiers changed from: private */
    public IMessagesAdapterActionsListener mEventsListener;
    private final Map<String, List<String>> mExtractedUrls = new HashMap();
    private final HashMap<String, String> mHtmlMap = new HashMap<>();
    private VectorImageGetter mImageGetter;
    private MatrixLinkMovementMethod mLinkMovementMethod;
    /* access modifiers changed from: private */
    public final Set<String> mPendingUrls = new HashSet();
    /* access modifiers changed from: private */
    public Map<String, Drawable> mPillsDrawableCache = new HashMap();
    private Room mRoom = null;
    /* access modifiers changed from: private */
    public final MXSession mSession;
    /* access modifiers changed from: private */
    public final Map<String, URLPreview> mUrlsPreview = new HashMap();

    VectorMessagesAdapterHelper(Context context, MXSession mXSession, VectorMessagesAdapter vectorMessagesAdapter) {
        this.mContext = context;
        this.mSession = mXSession;
        this.mAdapter = vectorMessagesAdapter;
    }

    /* access modifiers changed from: 0000 */
    public void setVectorMessagesAdapterActionsListener(IMessagesAdapterActionsListener iMessagesAdapterActionsListener) {
        this.mEventsListener = iMessagesAdapterActionsListener;
    }

    /* access modifiers changed from: 0000 */
    public void setLinkMovementMethod(MatrixLinkMovementMethod matrixLinkMovementMethod) {
        this.mLinkMovementMethod = matrixLinkMovementMethod;
    }

    /* access modifiers changed from: 0000 */
    public void setImageGetter(VectorImageGetter vectorImageGetter) {
        this.mImageGetter = vectorImageGetter;
    }

    public static String getUserDisplayName(String str, RoomState roomState) {
        return roomState != null ? roomState.getMemberName(str) : str;
    }

    public void setSenderValue(View view, MessageRow messageRow, boolean z) {
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_sender);
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_flair_groups_list);
        if (textView != null) {
            Event event = messageRow.getEvent();
            findViewById.setVisibility(8);
            findViewById.setTag(null);
            if (z) {
                textView.setVisibility(8);
                return;
            }
            String type = event.getType();
            if (event.isCallEvent() || Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type) || Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type) || Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type)) {
                textView.setVisibility(8);
                return;
            }
            textView.setVisibility(0);
            textView.setText(getUserDisplayName(event.getSender(), messageRow.getRoomState()));
            final String sender = event.getSender();
            final String charSequence = textView.getText() == null ? "" : textView.getText().toString();
            textView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMessagesAdapterHelper.this.mEventsListener != null) {
                        VectorMessagesAdapterHelper.this.mEventsListener.onSenderNameClick(sender, charSequence);
                    }
                }
            });
            refreshGroupFlairView(findViewById, event);
        }
    }

    /* access modifiers changed from: private */
    public void refreshGroupFlairView(View view, Event event, Set<String> set, String str) {
        int i;
        View view2 = view;
        final Event event2 = event;
        Set<String> set2 = set;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## refreshGroupFlairView () : ");
        sb.append(event2.sender);
        sb.append(" allows flair to ");
        sb.append(set2);
        Log.m209d(str2, sb.toString());
        String str3 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## refreshGroupFlairView () : room related groups ");
        sb2.append(this.mRoom.getLiveState().getRelatedGroups());
        Log.m209d(str3, sb2.toString());
        if (!set.isEmpty()) {
            set2.retainAll(this.mRoom.getLiveState().getRelatedGroups());
        }
        String str4 = LOG_TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("## refreshGroupFlairView () : group ids to display ");
        sb3.append(set2);
        Log.m209d(str4, sb3.toString());
        if (set.isEmpty()) {
            view2.setVisibility(8);
        } else if (this.mSession.isAlive()) {
            int i2 = 0;
            view2.setVisibility(0);
            ArrayList arrayList = new ArrayList();
            arrayList.add((ImageView) view2.findViewById(C1299R.C1301id.message_avatar_group_1).findViewById(C1299R.C1301id.avatar_img));
            arrayList.add((ImageView) view2.findViewById(C1299R.C1301id.message_avatar_group_2).findViewById(C1299R.C1301id.avatar_img));
            arrayList.add((ImageView) view2.findViewById(C1299R.C1301id.message_avatar_group_3).findViewById(C1299R.C1301id.avatar_img));
            TextView textView = (TextView) view2.findViewById(C1299R.C1301id.message_more_than_expected);
            final ArrayList arrayList2 = new ArrayList(set2);
            int min = Math.min(arrayList2.size(), arrayList.size());
            int i3 = 0;
            while (i3 < min) {
                final String str5 = (String) arrayList2.get(i3);
                ImageView imageView = (ImageView) arrayList.get(i3);
                imageView.setVisibility(i2);
                Group group = this.mSession.getGroupsManager().getGroup(str5);
                if (group == null) {
                    group = new Group(str5);
                }
                GroupProfile groupProfile = this.mSession.getGroupsManager().getGroupProfile(str5);
                if (groupProfile != null) {
                    String str6 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## refreshGroupFlairView () : profile of ");
                    sb4.append(str5);
                    sb4.append(" is cached");
                    Log.m209d(str6, sb4.toString());
                    group.setGroupProfile(groupProfile);
                    VectorUtils.loadGroupAvatar(this.mContext, this.mSession, imageView, group);
                    i = i3;
                } else {
                    VectorUtils.loadGroupAvatar(this.mContext, this.mSession, imageView, group);
                    String str7 = LOG_TAG;
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append("## refreshGroupFlairView () : get profile of ");
                    sb5.append(str5);
                    Log.m209d(str7, sb5.toString());
                    GroupsManager groupsManager = this.mSession.getGroupsManager();
                    final View view3 = view2;
                    ImageView imageView2 = imageView;
                    final String str8 = str;
                    String str9 = str5;
                    i = i3;
                    final ImageView imageView3 = imageView2;
                    C18002 r0 = new ApiCallback<GroupProfile>() {
                        private void refresh(GroupProfile groupProfile) {
                            if (TextUtils.equals((String) view3.getTag(), str8)) {
                                Group group = new Group(str5);
                                group.setGroupProfile(groupProfile);
                                String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## refreshGroupFlairView () : refresh group avatar ");
                                sb.append(str5);
                                Log.m209d(access$100, sb.toString());
                                VectorUtils.loadGroupAvatar(VectorMessagesAdapterHelper.this.mContext, VectorMessagesAdapterHelper.this.mSession, imageView3, group);
                            }
                        }

                        public void onSuccess(GroupProfile groupProfile) {
                            String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshGroupFlairView () : get profile of ");
                            sb.append(str5);
                            sb.append(" succeeded");
                            Log.m209d(access$100, sb.toString());
                            refresh(groupProfile);
                        }

                        public void onNetworkError(Exception exc) {
                            String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshGroupFlairView () : get profile of ");
                            sb.append(str5);
                            sb.append(" failed ");
                            sb.append(exc.getMessage());
                            Log.m211e(access$100, sb.toString());
                            refresh(null);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshGroupFlairView () : get profile of ");
                            sb.append(str5);
                            sb.append(" failed ");
                            sb.append(matrixError.getMessage());
                            Log.m211e(access$100, sb.toString());
                            refresh(null);
                        }

                        public void onUnexpectedError(Exception exc) {
                            String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## refreshGroupFlairView () : get profile of ");
                            sb.append(str5);
                            sb.append(" failed ");
                            sb.append(exc.getMessage());
                            Log.m211e(access$100, sb.toString());
                            refresh(null);
                        }
                    };
                    groupsManager.getGroupProfile(str9, r0);
                }
                i3 = i + 1;
                i2 = 0;
            }
            for (int i4 = i3; i4 < arrayList.size(); i4++) {
                ((ImageView) arrayList.get(i4)).setVisibility(8);
            }
            int i5 = 8;
            if (set.size() > arrayList.size()) {
                i5 = 0;
            }
            textView.setVisibility(i5);
            StringBuilder sb6 = new StringBuilder();
            sb6.append("+");
            sb6.append(set.size() - arrayList.size());
            textView.setText(sb6.toString());
            if (set.size() > 0) {
                view2.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VectorMessagesAdapterHelper.this.mEventsListener != null) {
                            VectorMessagesAdapterHelper.this.mEventsListener.onGroupFlairClick(event2.getSender(), arrayList2);
                        }
                    }
                });
            } else {
                view2.setOnClickListener(null);
            }
        }
    }

    private void refreshGroupFlairView(final View view, final Event event) {
        StringBuilder sb = new StringBuilder();
        sb.append(event.getSender());
        sb.append("__");
        sb.append(event.eventId);
        final String sb2 = sb.toString();
        if (this.mRoom == null) {
            this.mRoom = this.mSession.getDataHandler().getRoom(event.roomId, false);
            if (this.mRoom == null) {
                Log.m209d(LOG_TAG, "## refreshGroupFlairView () : the room is not available");
                view.setVisibility(8);
                return;
            }
        }
        if (this.mRoom.getLiveState().getRelatedGroups().isEmpty()) {
            Log.m209d(LOG_TAG, "## refreshGroupFlairView () : no related group");
            view.setVisibility(8);
            return;
        }
        view.setTag(sb2);
        String str = LOG_TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("## refreshGroupFlairView () : eventId ");
        sb3.append(event.eventId);
        sb3.append(" from ");
        sb3.append(event.sender);
        Log.m209d(str, sb3.toString());
        Set userPublicisedGroups = this.mSession.getGroupsManager().getUserPublicisedGroups(event.getSender());
        if (userPublicisedGroups != null) {
            refreshGroupFlairView(view, event, userPublicisedGroups, sb2);
        } else {
            view.setVisibility(8);
            this.mSession.getGroupsManager().getUserPublicisedGroups(event.getSender(), false, new ApiCallback<Set<String>>() {
                public void onSuccess(Set<String> set) {
                    VectorMessagesAdapterHelper.this.refreshGroupFlairView(view, event, set, sb2);
                }

                public void onNetworkError(Exception exc) {
                    String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshGroupFlairView failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshGroupFlairView failed ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$100, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$100 = VectorMessagesAdapterHelper.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshGroupFlairView failed ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$100, sb.toString());
                }
            });
        }
    }

    static TextView setTimestampValue(View view, String str) {
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_timestamp);
        if (textView != null) {
            if (TextUtils.isEmpty(str)) {
                textView.setVisibility(8);
            } else {
                textView.setVisibility(0);
                textView.setText(str);
            }
        }
        return textView;
    }

    /* access modifiers changed from: 0000 */
    public void loadMemberAvatar(ImageView imageView, MessageRow messageRow) {
        RoomState roomState = messageRow.getRoomState();
        Event event = messageRow.getEvent();
        String str = null;
        RoomMember member = roomState != null ? roomState.getMember(event.getSender()) : null;
        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
        String asString = (!contentAsJsonObject.has(AVATAR_URL_KEY) || contentAsJsonObject.get(AVATAR_URL_KEY) == JsonNull.INSTANCE) ? null : contentAsJsonObject.get(AVATAR_URL_KEY).getAsString();
        if (contentAsJsonObject.has(MEMBERSHIP_KEY)) {
            CharSequence asString2 = contentAsJsonObject.get(MEMBERSHIP_KEY) == JsonNull.INSTANCE ? null : contentAsJsonObject.get(MEMBERSHIP_KEY).getAsString();
            if (TextUtils.equals(asString2, "invite")) {
                asString = member != null ? member.getAvatarUrl() : null;
            }
            if (TextUtils.equals(asString2, RoomMember.MEMBERSHIP_JOIN) && contentAsJsonObject.has(DISPLAYNAME_KEY) && contentAsJsonObject.get(DISPLAYNAME_KEY) != JsonNull.INSTANCE) {
                str = contentAsJsonObject.get(DISPLAYNAME_KEY).getAsString();
            }
        }
        String sender = event.getSender();
        if (this.mSession.isAlive()) {
            if (TextUtils.isEmpty(str) && member != null) {
                str = member.displayname;
            }
            String str2 = str;
            if (member != null && asString == null) {
                asString = member.getAvatarUrl();
            }
            String str3 = asString;
            if (member != null) {
                VectorUtils.loadUserAvatar(this.mContext, this.mSession, imageView, str3, member.getUserId(), str2);
            } else {
                VectorUtils.loadUserAvatar(this.mContext, this.mSession, imageView, str3, sender, str2);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public View setSenderAvatar(View view, MessageRow messageRow, boolean z) {
        Event event = messageRow.getEvent();
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_roundAvatar);
        if (findViewById != null) {
            final String sender = event.getSender();
            findViewById.setClickable(true);
            findViewById.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    return VectorMessagesAdapterHelper.this.mEventsListener != null && VectorMessagesAdapterHelper.this.mEventsListener.onAvatarLongClick(sender);
                }
            });
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (VectorMessagesAdapterHelper.this.mEventsListener != null) {
                        VectorMessagesAdapterHelper.this.mEventsListener.onAvatarClick(sender);
                    }
                }
            });
        }
        if (findViewById != null) {
            ImageView imageView = (ImageView) findViewById.findViewById(C1299R.C1301id.avatar_img);
            if (z) {
                findViewById.setVisibility(8);
            } else {
                findViewById.setVisibility(0);
                imageView.setTag(null);
                loadMemberAvatar(imageView, messageRow);
            }
        }
        return findViewById;
    }

    static void alignSubviewToAvatarView(View view, View view2, View view3, boolean z) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view2.getLayoutParams();
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = view3.getLayoutParams();
        layoutParams.gravity = 19;
        if (z) {
            marginLayoutParams.setMargins(layoutParams2.width, marginLayoutParams.topMargin, 4, marginLayoutParams.bottomMargin);
        } else {
            marginLayoutParams.setMargins(4, marginLayoutParams.topMargin, 4, marginLayoutParams.bottomMargin);
        }
        view.setLayoutParams(marginLayoutParams);
        view2.setLayoutParams(marginLayoutParams);
        view.setLayoutParams(layoutParams);
    }

    static void setHeader(View view, String str, int i) {
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_message_header);
        if (findViewById == null) {
            return;
        }
        if (str != null) {
            ((TextView) view.findViewById(C1299R.C1301id.messagesAdapter_message_header_text)).setText(str);
            int i2 = 0;
            findViewById.setVisibility(0);
            View findViewById2 = findViewById.findViewById(C1299R.C1301id.messagesAdapter_message_header_top_margin);
            if (i == 0) {
                i2 = 8;
            }
            findViewById2.setVisibility(i2);
            return;
        }
        findViewById.setVisibility(8);
    }

    public void hideStickerDescription(View view) {
        View findViewById = view.findViewById(C1299R.C1301id.message_adapter_sticker_layout);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    public void showStickerDescription(View view, StickerMessage stickerMessage) {
        View findViewById = view.findViewById(C1299R.C1301id.message_adapter_sticker_layout);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.message_adapter_sticker_triangle);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.message_adapter_sticker_description);
        if (findViewById != null && imageView != null && textView != null) {
            findViewById.setVisibility(0);
            imageView.setVisibility(0);
            textView.setVisibility(0);
            textView.setText(stickerMessage.body);
        }
    }

    /* access modifiers changed from: 0000 */
    public void hideReadReceipts(View view) {
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_avatars_list);
        if (findViewById != null) {
            findViewById.setVisibility(8);
        }
    }

    /* access modifiers changed from: 0000 */
    public void displayReadReceipts(View view, MessageRow messageRow, boolean z) {
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_avatars_list);
        if (findViewById != null && this.mSession.isAlive()) {
            final String str = messageRow.getEvent().eventId;
            RoomState roomState = messageRow.getRoomState();
            IMXStore store = this.mSession.getDataHandler().getStore();
            int i = 8;
            if (roomState == null) {
                findViewById.setVisibility(8);
            } else if (z) {
                findViewById.setVisibility(8);
            } else {
                List eventReceipts = store.getEventReceipts(roomState.roomId, str, true, true);
                if (eventReceipts == null || eventReceipts.size() == 0) {
                    findViewById.setVisibility(8);
                    return;
                }
                findViewById.setVisibility(0);
                ArrayList arrayList = new ArrayList();
                arrayList.add(findViewById.findViewById(C1299R.C1301id.message_avatar_receipt_1).findViewById(C1299R.C1301id.avatar_img));
                arrayList.add(findViewById.findViewById(C1299R.C1301id.message_avatar_receipt_2).findViewById(C1299R.C1301id.avatar_img));
                arrayList.add(findViewById.findViewById(C1299R.C1301id.message_avatar_receipt_3).findViewById(C1299R.C1301id.avatar_img));
                arrayList.add(findViewById.findViewById(C1299R.C1301id.message_avatar_receipt_4).findViewById(C1299R.C1301id.avatar_img));
                arrayList.add(findViewById.findViewById(C1299R.C1301id.message_avatar_receipt_5).findViewById(C1299R.C1301id.avatar_img));
                TextView textView = (TextView) findViewById.findViewById(C1299R.C1301id.message_more_than_expected);
                int min = Math.min(eventReceipts.size(), arrayList.size());
                int i2 = 0;
                while (i2 < min) {
                    ReceiptData receiptData = (ReceiptData) eventReceipts.get(i2);
                    RoomMember member = roomState.getMember(receiptData.userId);
                    ImageView imageView = (ImageView) arrayList.get(i2);
                    imageView.setVisibility(0);
                    imageView.setTag(null);
                    if (member != null) {
                        VectorUtils.loadRoomMemberAvatar(this.mContext, this.mSession, imageView, member);
                    } else {
                        VectorUtils.loadUserAvatar(this.mContext, this.mSession, imageView, null, receiptData.userId, receiptData.userId);
                    }
                    i2++;
                }
                if (eventReceipts.size() > arrayList.size()) {
                    i = 0;
                }
                textView.setVisibility(i);
                StringBuilder sb = new StringBuilder();
                sb.append(eventReceipts.size() - arrayList.size());
                sb.append("+");
                textView.setText(sb.toString());
                while (i2 < arrayList.size()) {
                    ((View) arrayList.get(i2)).setVisibility(4);
                    i2++;
                }
                if (eventReceipts.size() > 0) {
                    findViewById.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            if (VectorMessagesAdapterHelper.this.mEventsListener != null) {
                                VectorMessagesAdapterHelper.this.mEventsListener.onMoreReadReceiptClick(str);
                            }
                        }
                    });
                } else {
                    findViewById.setOnClickListener(null);
                }
            }
        }
    }

    static void setMediaProgressLayout(View view, View view2) {
        int i = ((MarginLayoutParams) view2.getLayoutParams()).leftMargin;
        View findViewById = view.findViewById(C1299R.C1301id.content_download_progress_layout);
        if (findViewById != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) findViewById.getLayoutParams();
            marginLayoutParams.setMargins(i, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
            findViewById.setLayoutParams(marginLayoutParams);
        }
        View findViewById2 = view.findViewById(C1299R.C1301id.content_upload_progress_layout);
        if (findViewById2 != null) {
            MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) findViewById2.getLayoutParams();
            marginLayoutParams2.setMargins(i, marginLayoutParams2.topMargin, marginLayoutParams2.rightMargin, marginLayoutParams2.bottomMargin);
            findViewById2.setLayoutParams(marginLayoutParams2);
        }
    }

    private void makeLinkClickable(SpannableStringBuilder spannableStringBuilder, final URLSpan uRLSpan, boolean z) {
        int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
        int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
        if (spanStart >= 0 && spanEnd >= 0) {
            int spanFlags = spannableStringBuilder.getSpanFlags(uRLSpan);
            if (PillView.isPillable(uRLSpan.getURL())) {
                StringBuilder sb = new StringBuilder();
                sb.append(uRLSpan.getURL());
                sb.append(StringUtils.SPACE);
                sb.append(z);
                final String sb2 = sb.toString();
                Drawable drawable = (Drawable) this.mPillsDrawableCache.get(sb2);
                if (drawable == null) {
                    PillView pillView = new PillView(this.mContext);
                    pillView.setBackgroundResource(17170445);
                    final WeakReference weakReference = new WeakReference(pillView);
                    pillView.initData(spannableStringBuilder.subSequence(spanStart, spanEnd), uRLSpan.getURL(), this.mSession, new OnUpdateListener() {
                        public void onAvatarUpdate() {
                            if (weakReference != null && weakReference.get() != null) {
                                VectorMessagesAdapterHelper.this.mPillsDrawableCache.put(sb2, ((PillView) weakReference.get()).getDrawable(true));
                                VectorMessagesAdapterHelper.this.mAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    pillView.setHighlighted(z);
                    drawable = pillView.getDrawable(false);
                }
                if (drawable != null) {
                    this.mPillsDrawableCache.put(sb2, drawable);
                    ImageSpan imageSpan = new ImageSpan(drawable);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    spannableStringBuilder.setSpan(imageSpan, spanStart, spanEnd, spanFlags);
                }
            }
            spannableStringBuilder.setSpan(new ClickableSpan() {
                public void onClick(View view) {
                    if (VectorMessagesAdapterHelper.this.mEventsListener != null) {
                        VectorMessagesAdapterHelper.this.mEventsListener.onURLClick(Uri.parse(uRLSpan.getURL()));
                    }
                }
            }, spanStart, spanEnd, spanFlags);
            spannableStringBuilder.removeSpan(uRLSpan);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean containsFencedCodeBlocks(Message message) {
        return message.formatted_body != null && message.formatted_body.contains(START_FENCED_BLOCK) && message.formatted_body.contains(END_FENCED_BLOCK);
    }

    /* access modifiers changed from: 0000 */
    public String[] getFencedCodeBlocks(Message message) {
        if (TextUtils.isEmpty(message.formatted_body)) {
            return new String[0];
        }
        String[] strArr = (String[]) this.mCodeBlocksMap.get(message.formatted_body);
        if (strArr == null) {
            strArr = FENCED_CODE_BLOCK_PATTERN.split(message.formatted_body);
            this.mCodeBlocksMap.put(message.formatted_body, strArr);
        }
        return strArr;
    }

    /* access modifiers changed from: 0000 */
    public void highlightFencedCode(TextView textView) {
        if (textView != null) {
            textView.setBackgroundColor(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.markdown_block_background_color));
        }
    }

    /* access modifiers changed from: 0000 */
    public void applyLinkMovementMethod(@Nullable TextView textView) {
        if (textView != null && this.mLinkMovementMethod != null) {
            textView.setMovementMethod(this.mLinkMovementMethod);
        }
    }

    /* access modifiers changed from: 0000 */
    public CharSequence highlightPattern(Spannable spannable, String str, CharacterStyle characterStyle, boolean z) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(spannable) && spannable.length() >= str.length()) {
            String lowerCase = spannable.toString().toLowerCase(VectorApp.getApplicationLocale());
            String lowerCase2 = str.toLowerCase(VectorApp.getApplicationLocale());
            int indexOf = lowerCase.indexOf(lowerCase2, 0);
            while (indexOf >= 0) {
                int length = lowerCase2.length() + indexOf;
                spannable.setSpan(characterStyle, indexOf, length, 33);
                spannable.setSpan(new StyleSpan(1), indexOf, length, 33);
                indexOf = lowerCase.indexOf(lowerCase2, length);
            }
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(spannable);
        URLSpan[] uRLSpanArr = (URLSpan[]) spannableStringBuilder.getSpans(0, spannable.length(), URLSpan.class);
        if (uRLSpanArr != null && uRLSpanArr.length > 0) {
            for (URLSpan makeLinkClickable : uRLSpanArr) {
                makeLinkClickable(spannableStringBuilder, makeLinkClickable, z);
            }
        }
        MatrixURLSpan.refreshMatrixSpans(spannableStringBuilder, this.mEventsListener);
        return spannableStringBuilder;
    }

    /* access modifiers changed from: 0000 */
    public CharSequence convertToHtml(String str) {
        HtmlTagHandler htmlTagHandler = new HtmlTagHandler();
        htmlTagHandler.mContext = this.mContext;
        htmlTagHandler.setCodeBlockBackgroundColor(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.markdown_block_background_color));
        if (str == null) {
            return "";
        }
        boolean z = !str.contains("<a href=") && !str.contains("<table>");
        VectorImageGetter vectorImageGetter = this.mImageGetter;
        if (!z) {
            htmlTagHandler = null;
        }
        Spanned fromHtml = Html.fromHtml(str, vectorImageGetter, htmlTagHandler);
        if (TextUtils.isEmpty(fromHtml)) {
            return fromHtml;
        }
        int length = fromHtml.length() - 1;
        int i = 0;
        while (i < fromHtml.length() - 1 && 10 == fromHtml.charAt(i)) {
            i++;
        }
        while (length >= 0 && 10 == fromHtml.charAt(length)) {
            length--;
        }
        if (length < i) {
            return fromHtml.subSequence(0, 0);
        }
        return fromHtml.subSequence(i, length + 1);
    }

    static boolean isDisplayableEvent(Context context, MessageRow messageRow) {
        boolean z = false;
        if (messageRow == null) {
            return false;
        }
        RoomState roomState = messageRow.getRoomState();
        Event event = messageRow.getEvent();
        if (roomState == null || event == null) {
            return false;
        }
        String type = event.getType();
        if (Event.EVENT_TYPE_MESSAGE.equals(type)) {
            Message message = JsonUtils.toMessage(event.getContent());
            if (!event.isRedacted() && (!TextUtils.isEmpty(message.body) || TextUtils.equals(message.msgtype, Message.MSGTYPE_EMOTE))) {
                z = true;
            }
            return z;
        } else if (Event.EVENT_TYPE_STICKER.equals(type)) {
            if (!TextUtils.isEmpty(JsonUtils.toStickerMessage(event.getContent()).body) && !event.isRedacted()) {
                z = true;
            }
            return z;
        } else if (Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type) || Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type)) {
            if (new RiotEventDisplay(context, event, roomState).getTextualDisplay() != null) {
                z = true;
            }
            return z;
        } else if (event.isCallEvent()) {
            if (Event.EVENT_TYPE_CALL_INVITE.equals(type) || Event.EVENT_TYPE_CALL_ANSWER.equals(type) || Event.EVENT_TYPE_CALL_HANGUP.equals(type)) {
                z = true;
            }
            return z;
        } else if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type)) {
            if (new RiotEventDisplay(context, event, roomState).getTextualDisplay() != null) {
                z = true;
            }
            return z;
        } else if (Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type)) {
            return true;
        } else {
            if (Event.EVENT_TYPE_MESSAGE_ENCRYPTED.equals(type) || Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type)) {
                RiotEventDisplay riotEventDisplay = new RiotEventDisplay(context, event, roomState);
                if (event.hasContentFields() && riotEventDisplay.getTextualDisplay() != null) {
                    z = true;
                }
                return z;
            } else if (TextUtils.equals(WidgetsManager.WIDGET_EVENT_TYPE, event.getType())) {
                return true;
            } else {
                return false;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @Nullable
    public String getSanitisedHtml(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String str2 = (String) this.mHtmlMap.get(str);
        if (str2 == null) {
            str2 = sanitiseHTML(str);
            this.mHtmlMap.put(str, str2);
        }
        return str2;
    }

    private static String sanitiseHTML(String str) {
        Matcher matcher = mHtmlPatter.matcher(str);
        HashSet hashSet = new HashSet();
        while (matcher.find()) {
            try {
                String substring = str.substring(matcher.start(1), matcher.end(1));
                if (!mAllowedHTMLTags.contains(substring)) {
                    hashSet.add(substring);
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("sanitiseHTML failed ");
                sb.append(e.getLocalizedMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        if (hashSet.isEmpty()) {
            return str;
        }
        String str3 = "";
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            String str4 = (String) it.next();
            if (!str3.isEmpty()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str3);
                sb2.append("|");
                str3 = sb2.toString();
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str3);
            sb3.append(str4);
            str3 = sb3.toString();
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append("<\\/?(");
        sb4.append(str3);
        sb4.append(")[^>]*>");
        return str.replaceAll(sb4.toString(), "");
    }

    private List<String> extractWebUrl(String str) {
        List<String> list = (List) this.mExtractedUrls.get(str);
        if (list == null) {
            list = new ArrayList<>();
            Matcher matcher = Patterns.WEB_URL.matcher(str);
            while (matcher.find()) {
                try {
                    String substring = str.substring(matcher.start(0), matcher.end(0));
                    if (!list.contains(substring)) {
                        list.add(substring);
                    }
                } catch (Exception e) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## extractWebUrl() ");
                    sb.append(e.getMessage());
                    Log.m211e(str2, sb.toString());
                }
            }
            this.mExtractedUrls.put(str, list);
        }
        return list;
    }

    /* access modifiers changed from: 0000 */
    public void manageURLPreviews(Message message, View view, String str) {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(C1299R.C1301id.messagesAdapter_urls_preview_list);
        if (linearLayout != null) {
            if (TextUtils.isEmpty(message.body)) {
                linearLayout.setVisibility(8);
                return;
            }
            List<String> extractWebUrl = extractWebUrl(message.body);
            if (extractWebUrl.isEmpty()) {
                linearLayout.setVisibility(8);
            } else if (!TextUtils.equals((String) linearLayout.getTag(), str) || linearLayout.getChildCount() != extractWebUrl.size()) {
                linearLayout.setTag(str);
                while (linearLayout.getChildCount() > 0) {
                    linearLayout.removeViewAt(0);
                }
                linearLayout.setVisibility(0);
                for (final String str2 : extractWebUrl) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2.hashCode());
                    sb.append("---");
                    final String sb2 = sb.toString();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(str2);
                    sb3.append("<----->");
                    sb3.append(str);
                    String sb4 = sb3.toString();
                    if (UrlPreviewView.Companion.didUrlPreviewDismiss(sb4)) {
                        String str3 = LOG_TAG;
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("## manageURLPreviews() : ");
                        sb5.append(sb4);
                        sb5.append(" has been dismissed");
                        Log.m209d(str3, sb5.toString());
                    } else if (!this.mPendingUrls.contains(str2)) {
                        if (!this.mUrlsPreview.containsKey(sb2)) {
                            this.mPendingUrls.add(str2);
                            this.mSession.getEventsApiClient().getURLPreview(str2, System.currentTimeMillis(), new ApiCallback<URLPreview>() {
                                public void onSuccess(URLPreview uRLPreview) {
                                    VectorMessagesAdapterHelper.this.mPendingUrls.remove(str2);
                                    if (!VectorMessagesAdapterHelper.this.mUrlsPreview.containsKey(sb2)) {
                                        VectorMessagesAdapterHelper.this.mUrlsPreview.put(sb2, uRLPreview);
                                        VectorMessagesAdapterHelper.this.mAdapter.notifyDataSetChanged();
                                    }
                                }

                                public void onNetworkError(Exception exc) {
                                    onSuccess((URLPreview) null);
                                }

                                public void onMatrixError(MatrixError matrixError) {
                                    onSuccess((URLPreview) null);
                                }

                                public void onUnexpectedError(Exception exc) {
                                    onSuccess((URLPreview) null);
                                }
                            });
                        } else {
                            UrlPreviewView urlPreviewView = new UrlPreviewView(this.mContext);
                            urlPreviewView.setUrlPreview(this.mContext, this.mSession, (URLPreview) this.mUrlsPreview.get(sb2), sb4);
                            linearLayout.addView(urlPreviewView);
                        }
                    }
                }
            }
        }
    }
}
