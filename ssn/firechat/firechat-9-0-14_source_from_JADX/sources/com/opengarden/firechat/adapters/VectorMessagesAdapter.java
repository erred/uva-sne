package com.opengarden.firechat.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.GravityCompat;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.QuoteSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.listeners.IMessagesAdapterActionsListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.interfaces.HtmlToolbox;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.Event.SentState;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.view.HtmlTagHandler;
import com.opengarden.firechat.p008ui.VectorQuoteSpan;
import com.opengarden.firechat.util.EventGroup;
import com.opengarden.firechat.util.MatrixLinkMovementMethod;
import com.opengarden.firechat.util.MatrixURLSpan;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.RiotEventDisplay;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorImageGetter;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VectorMessagesAdapter extends AbstractMessagesAdapter {
    private static final String LOG_TAG = "VectorMessagesAdapter";
    static final int NUM_ROW_TYPES = 12;
    static final int ROW_TYPE_CODE = 10;
    static final int ROW_TYPE_EMOJI = 9;
    static final int ROW_TYPE_EMOTE = 3;
    static final int ROW_TYPE_FILE = 4;
    static final int ROW_TYPE_HIDDEN = 7;
    static final int ROW_TYPE_IMAGE = 1;
    static final int ROW_TYPE_MERGE = 6;
    static final int ROW_TYPE_NOTICE = 2;
    static final int ROW_TYPE_ROOM_MEMBER = 8;
    static final int ROW_TYPE_STICKER = 11;
    static final int ROW_TYPE_TEXT = 0;
    static final int ROW_TYPE_VIDEO = 5;
    private static final Pattern mEmojisPattern = Pattern.compile("((?:[🌀-🗿]|[🤀-🧿]|[😀-🙏]|[🚀-🛿]|[☀-⛿]️?|[✀-➿]️?|Ⓜ️?|[🇦-🇿]{1,2}|[🅰🅱🅾🅿🆎🆑-🆚]️?|[#*0-9]️?⃣|[↔-↙↩-↪]️?|[⬅-⬇⬛⬜⭐⭕]️?|[⤴⤵]️?|[〰〽]️?|[㊗㊙]️?|[🈁🈂🈚🈯🈲-🈺🉐🉑]️?|[‼⁉]️?|[▪▫▶◀◻-◾]️?|[©®]️?|[™ℹ]️?|🀄️?|🃏️?|[⌚⌛⌨⏏⏩-⏳⏸-⏺]️?))");
    private final boolean mAlwaysShowTimeStamps;
    protected BackgroundColorSpan mBackgroundColorSpan;
    private boolean mCanShowReadMarker;
    final Context mContext;
    private final int mDefaultMessageTextColor;
    /* access modifiers changed from: private */
    public HashMap<String, MXDeviceInfo> mE2eDeviceByEventId;
    private HashMap<String, Object> mE2eIconByEventId;
    private final int mEncryptingMessageTextColor;
    private final HashMap<String, String> mEventFormattedTsMap;
    private final List<EventGroup> mEventGroups;
    private final HashMap<String, MessageRow> mEventRowMap;
    private final HashMap<String, Integer> mEventType;
    protected final VectorMessagesAdapterHelper mHelper;
    private final Set<String> mHiddenEventIds;
    private final boolean mHideReadReceipts;
    private final int mHighlightMessageTextColor;
    private String mHighlightedEventId;
    private HtmlToolbox mHtmlToolbox;
    /* access modifiers changed from: private */
    public VectorImageGetter mImageGetter;
    private boolean mIsPreviewMode;
    public boolean mIsRoomEncrypted;
    /* access modifiers changed from: private */
    public boolean mIsSearchMode;
    private boolean mIsUnreadViewMode;
    final LayoutInflater mLayoutInflater;
    private MatrixLinkMovementMethod mLinkMovementMethod;
    private ArrayList<MessageRow> mLiveMessagesRowList;
    private final Locale mLocale;
    private final int mMaxImageHeight;
    private final int mMaxImageWidth;
    private final MXMediasCache mMediasCache;
    private final VectorMessagesAdapterMediasHelper mMediasHelper;
    private ArrayList<Date> mMessagesDateList;
    private final int mNotSentMessageTextColor;
    private final Drawable mPadlockDrawable;
    private String mPattern;
    private String mReadMarkerEventId;
    /* access modifiers changed from: private */
    public ReadMarkerListener mReadMarkerListener;
    private String mReadReceiptEventId;
    private Date mReferenceDate;
    private final HashMap<Integer, Integer> mRowTypeToLayoutId;
    int mSearchHighlightMessageTextColor;
    private String mSearchedEventId;
    /* access modifiers changed from: private */
    public String mSelectedEventId;
    private final int mSendingMessageTextColor;
    final MXSession mSession;
    IMessagesAdapterActionsListener mVectorMessagesAdapterEventsListener;

    public interface ReadMarkerListener {
        void onReadMarkerDisplayed(Event event, View view);
    }

    private static boolean isMergeableEvent(int i) {
        return (2 == i || 8 == i || 7 == i) ? false : true;
    }

    public int getViewTypeCount() {
        return 12;
    }

    public VectorMessagesAdapter(MXSession mXSession, Context context, MXMediasCache mXMediasCache) {
        this(mXSession, context, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_room_member, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_file, C1299R.layout.adapter_item_vector_message_merge, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_message_emoji, C1299R.layout.adapter_item_vector_message_code, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_hidden_message, mXMediasCache);
    }

    VectorMessagesAdapter(MXSession mXSession, Context context, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, MXMediasCache mXMediasCache) {
        Context context2 = context;
        super(context2, 0);
        this.mVectorMessagesAdapterEventsListener = null;
        this.mReferenceDate = new Date();
        this.mMessagesDateList = new ArrayList<>();
        this.mSearchedEventId = null;
        this.mHighlightedEventId = null;
        this.mEventFormattedTsMap = new HashMap<>();
        this.mE2eIconByEventId = new HashMap<>();
        this.mE2eDeviceByEventId = new HashMap<>();
        this.mRowTypeToLayoutId = new HashMap<>();
        this.mEventRowMap = new HashMap<>();
        this.mEventType = new HashMap<>();
        this.mIsSearchMode = false;
        this.mIsPreviewMode = false;
        this.mIsUnreadViewMode = false;
        this.mPattern = null;
        this.mLiveMessagesRowList = null;
        this.mHiddenEventIds = new HashSet();
        this.mHtmlToolbox = new HtmlToolbox() {
            public String convert(String str) {
                String sanitisedHtml = VectorMessagesAdapter.this.mHelper.getSanitisedHtml(str);
                return sanitisedHtml != null ? sanitisedHtml : str;
            }

            @Nullable
            public ImageGetter getImageGetter() {
                return VectorMessagesAdapter.this.mImageGetter;
            }

            @Nullable
            public TagHandler getTagHandler(String str) {
                if (!(!str.contains("<a href=") && !str.contains("<table>"))) {
                    return null;
                }
                HtmlTagHandler htmlTagHandler = new HtmlTagHandler();
                htmlTagHandler.mContext = VectorMessagesAdapter.this.mContext;
                htmlTagHandler.setCodeBlockBackgroundColor(ThemeUtils.INSTANCE.getColor(VectorMessagesAdapter.this.mContext, C1299R.attr.markdown_block_background_color));
                return htmlTagHandler;
            }
        };
        this.mCanShowReadMarker = true;
        this.mEventGroups = new ArrayList();
        this.mContext = context2;
        this.mRowTypeToLayoutId.put(Integer.valueOf(0), Integer.valueOf(i));
        this.mRowTypeToLayoutId.put(Integer.valueOf(1), Integer.valueOf(i2));
        this.mRowTypeToLayoutId.put(Integer.valueOf(2), Integer.valueOf(i3));
        this.mRowTypeToLayoutId.put(Integer.valueOf(8), Integer.valueOf(i4));
        this.mRowTypeToLayoutId.put(Integer.valueOf(3), Integer.valueOf(i5));
        this.mRowTypeToLayoutId.put(Integer.valueOf(4), Integer.valueOf(i6));
        this.mRowTypeToLayoutId.put(Integer.valueOf(5), Integer.valueOf(i8));
        this.mRowTypeToLayoutId.put(Integer.valueOf(6), Integer.valueOf(i7));
        this.mRowTypeToLayoutId.put(Integer.valueOf(5), Integer.valueOf(i8));
        this.mRowTypeToLayoutId.put(Integer.valueOf(7), Integer.valueOf(C1299R.layout.adapter_item_vector_hidden_message));
        this.mRowTypeToLayoutId.put(Integer.valueOf(9), Integer.valueOf(i9));
        this.mRowTypeToLayoutId.put(Integer.valueOf(10), Integer.valueOf(i10));
        this.mRowTypeToLayoutId.put(Integer.valueOf(11), Integer.valueOf(i11));
        this.mRowTypeToLayoutId.put(Integer.valueOf(7), Integer.valueOf(i12));
        this.mMediasCache = mXMediasCache;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        setNotifyOnChange(false);
        this.mDefaultMessageTextColor = getDefaultMessageTextColor();
        this.mNotSentMessageTextColor = getNotSentMessageTextColor();
        this.mSendingMessageTextColor = getSendingMessageTextColor();
        this.mEncryptingMessageTextColor = getEncryptingMessageTextColor();
        this.mHighlightMessageTextColor = getHighlightMessageTextColor();
        this.mBackgroundColorSpan = new BackgroundColorSpan(getSearchHighlightMessageTextColor());
        Point point = new Point(0, 0);
        getScreenSize(point);
        int i13 = point.x;
        int i14 = point.y;
        if (i13 < i14) {
            this.mMaxImageWidth = Math.round(((float) i13) * 0.6f);
            this.mMaxImageHeight = Math.round(((float) i14) * 0.4f);
        } else {
            this.mMaxImageWidth = Math.round(((float) i13) * 0.4f);
            this.mMaxImageHeight = Math.round(((float) i14) * 0.6f);
        }
        this.mSession = mXSession;
        VectorMessagesAdapterMediasHelper vectorMessagesAdapterMediasHelper = new VectorMessagesAdapterMediasHelper(context2, this.mSession, this.mMaxImageWidth, this.mMaxImageHeight, this.mNotSentMessageTextColor, this.mDefaultMessageTextColor);
        this.mMediasHelper = vectorMessagesAdapterMediasHelper;
        this.mHelper = new VectorMessagesAdapterHelper(context2, this.mSession, this);
        this.mLocale = VectorApp.getApplicationLocale();
        this.mAlwaysShowTimeStamps = PreferencesManager.alwaysShowTimeStamps(VectorApp.getInstance());
        this.mHideReadReceipts = PreferencesManager.hideReadReceipts(VectorApp.getInstance());
        this.mPadlockDrawable = CommonActivityUtils.tintDrawable(this.mContext, ContextCompat.getDrawable(this.mContext, C1299R.C1300drawable.e2e_unencrypted), C1299R.attr.settings_icon_tint_color);
    }

    @SuppressLint({"NewApi"})
    private void getScreenSize(Point point) {
        ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay().getSize(point);
    }

    public int getMaxThumbnailWidth() {
        return this.mMaxImageWidth;
    }

    public int getMaxThumbnailHeight() {
        return this.mMaxImageHeight;
    }

    private int getDefaultMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.message_text_color);
    }

    private int getNoticeTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.notice_text_color);
    }

    private int getEncryptingMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.encrypting_message_text_color);
    }

    private int getSendingMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.sending_message_text_color);
    }

    private int getHighlightMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.highlighted_message_text_color);
    }

    private int getSearchHighlightMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.highlighted_searched_message_text_color);
    }

    private int getNotSentMessageTextColor() {
        return ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.unsent_message_text_color);
    }

    /* access modifiers changed from: 0000 */
    public boolean supportMessageRowMerge(MessageRow messageRow) {
        return EventGroup.isSupported(messageRow);
    }

    public void addToFront(MessageRow messageRow) {
        if (isSupportedRow(messageRow)) {
            setNotifyOnChange(false);
            if (this.mIsSearchMode) {
                this.mLiveMessagesRowList.add(0, messageRow);
            } else {
                insert(messageRow, addToEventGroupToFront(messageRow) ? 1 : 0);
            }
            if (messageRow.getEvent().eventId != null) {
                this.mEventRowMap.put(messageRow.getEvent().eventId, messageRow);
            }
        }
    }

    public void remove(MessageRow messageRow) {
        if (messageRow == null) {
            return;
        }
        if (this.mIsSearchMode) {
            this.mLiveMessagesRowList.remove(messageRow);
            return;
        }
        removeFromEventGroup(messageRow);
        int position = getPosition(messageRow);
        super.remove(messageRow);
        checkEventGroupsMerge(messageRow, position);
    }

    public void add(MessageRow messageRow) {
        add(messageRow, true);
    }

    public void add(MessageRow messageRow, boolean z) {
        if (isSupportedRow(messageRow)) {
            setNotifyOnChange(false);
            if (this.mIsSearchMode) {
                this.mLiveMessagesRowList.add(messageRow);
            } else {
                addToEventGroup(messageRow);
                super.add(messageRow);
            }
            if (messageRow.getEvent().eventId != null) {
                this.mEventRowMap.put(messageRow.getEvent().eventId, messageRow);
            }
            if (this.mIsSearchMode || !z) {
                setNotifyOnChange(true);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public MessageRow getMessageRow(String str) {
        if (str != null) {
            return (MessageRow) this.mEventRowMap.get(str);
        }
        return null;
    }

    public MessageRow getClosestRow(Event event) {
        if (event == null) {
            return null;
        }
        return getClosestRowFromTs(event.eventId, event.getOriginServerTs());
    }

    public MessageRow getClosestRowFromTs(String str, long j) {
        MessageRow messageRow = getMessageRow(str);
        if (messageRow == null) {
            for (MessageRow messageRow2 : new ArrayList(this.mEventRowMap.values())) {
                if (!(messageRow2.getEvent() instanceof EventGroup)) {
                    long originServerTs = messageRow2.getEvent().getOriginServerTs();
                    if (originServerTs > j) {
                        if (messageRow != null) {
                            if (originServerTs < messageRow.getEvent().getOriginServerTs()) {
                                String str2 = LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## getClosestRowFromTs() ");
                                sb.append(messageRow2.getEvent().eventId);
                                Log.m209d(str2, sb.toString());
                            }
                        }
                        messageRow = messageRow2;
                    }
                }
            }
        }
        return messageRow;
    }

    public MessageRow getClosestRowBeforeTs(String str, long j) {
        MessageRow messageRow = getMessageRow(str);
        if (messageRow == null) {
            for (MessageRow messageRow2 : new ArrayList(this.mEventRowMap.values())) {
                if (!(messageRow2.getEvent() instanceof EventGroup)) {
                    long originServerTs = messageRow2.getEvent().getOriginServerTs();
                    if (originServerTs < j) {
                        if (messageRow != null) {
                            if (originServerTs > messageRow.getEvent().getOriginServerTs()) {
                                String str2 = LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## getClosestRowBeforeTs() ");
                                sb.append(messageRow2.getEvent().eventId);
                                Log.m209d(str2, sb.toString());
                            }
                        }
                        messageRow = messageRow2;
                    }
                }
            }
        }
        return messageRow;
    }

    public void updateEventById(Event event, String str) {
        if (((MessageRow) this.mEventRowMap.get(event.eventId)) == null) {
            MessageRow messageRow = (MessageRow) this.mEventRowMap.get(str);
            if (messageRow != null) {
                this.mEventRowMap.remove(str);
                this.mEventRowMap.put(event.eventId, messageRow);
            }
        } else {
            removeEventById(str);
        }
        notifyDataSetChanged();
    }

    public void removeEventById(String str) {
        setNotifyOnChange(false);
        MessageRow messageRow = (MessageRow) this.mEventRowMap.get(str);
        if (messageRow != null) {
            remove(messageRow);
        }
    }

    public void setIsPreviewMode(boolean z) {
        this.mIsPreviewMode = z;
    }

    public void setIsUnreadViewMode(boolean z) {
        this.mIsUnreadViewMode = z;
    }

    public boolean isUnreadViewMode() {
        return this.mIsUnreadViewMode;
    }

    public void setSearchPattern(String str) {
        if (!TextUtils.equals(str, this.mPattern)) {
            this.mPattern = str;
            this.mIsSearchMode = !TextUtils.isEmpty(this.mPattern);
            if (this.mIsSearchMode) {
                if (this.mLiveMessagesRowList == null) {
                    this.mLiveMessagesRowList = new ArrayList<>();
                    for (int i = 0; i < getCount(); i++) {
                        this.mLiveMessagesRowList.add(getItem(i));
                    }
                }
            } else if (this.mLiveMessagesRowList != null) {
                clear();
                addAll(this.mLiveMessagesRowList);
                this.mLiveMessagesRowList = null;
            }
        }
    }

    public void clear() {
        super.clear();
        if (!this.mIsSearchMode) {
            this.mEventRowMap.clear();
        }
    }

    public int getItemViewType(int i) {
        if (i >= getCount()) {
            return 0;
        }
        return getItemViewType(((MessageRow) getItem(i)).getEvent());
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        if (i >= getCount()) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getView() : invalid index ");
            sb.append(i);
            sb.append(" >= ");
            sb.append(getCount());
            Log.m211e(str, sb.toString());
            if (view == null) {
                view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(0))).intValue(), viewGroup, false);
            }
            if (this.mVectorMessagesAdapterEventsListener != null) {
                this.mVectorMessagesAdapterEventsListener.onInvalidIndexes();
            }
            return view;
        }
        int itemViewType = getItemViewType(i);
        if (!(view == null || itemViewType == ((Integer) view.getTag()).intValue())) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getView() : invalid view type : got ");
            sb2.append(view.getTag());
            sb2.append(" instead of ");
            sb2.append(itemViewType);
            Log.m211e(str2, sb2.toString());
            view = null;
        }
        switch (itemViewType) {
            case 0:
            case 9:
            case 10:
                view2 = getTextView(itemViewType, i, view, viewGroup);
                break;
            case 1:
            case 5:
            case 11:
                view2 = getImageVideoView(itemViewType, i, view, viewGroup);
                break;
            case 2:
            case 8:
                view2 = getNoticeRoomMemberView(itemViewType, i, view, viewGroup);
                break;
            case 3:
                view2 = getEmoteView(i, view, viewGroup);
                break;
            case 4:
                view2 = getFileView(i, view, viewGroup);
                break;
            case 6:
                view2 = getMergeView(i, view, viewGroup);
                break;
            case 7:
                view2 = getHiddenView(i, view, viewGroup);
                break;
            default:
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Unknown item view type for position ");
                sb3.append(i);
                throw new RuntimeException(sb3.toString());
        }
        if (this.mReadMarkerListener != null) {
            handleReadMarker(view2, i);
        }
        if (view2 != null) {
            view2.setBackgroundColor(0);
            view2.setTag(Integer.valueOf(itemViewType));
        }
        displayE2eIcon(view2, i);
        return view2;
    }

    public void notifyDataSetChanged() {
        int i = 0;
        setNotifyOnChange(false);
        ArrayList arrayList = new ArrayList();
        while (i < getCount()) {
            MessageRow messageRow = (MessageRow) getItem(i);
            Event event = messageRow.getEvent();
            if (event != null && (event.isUndeliverable() || event.isUnkownDevice())) {
                arrayList.add(messageRow);
                remove(messageRow);
                i--;
            }
            i++;
        }
        if (arrayList.size() > 0) {
            try {
                Collections.sort(arrayList, new Comparator<MessageRow>() {
                    public int compare(MessageRow messageRow, MessageRow messageRow2) {
                        long originServerTs = messageRow.getEvent().getOriginServerTs() - messageRow2.getEvent().getOriginServerTs();
                        if (originServerTs > 0) {
                            return 1;
                        }
                        return originServerTs < 0 ? -1 : 0;
                    }
                });
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## notifyDataSetChanged () : failed to sort undeliverableEvents ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
            addAll(arrayList);
        }
        setNotifyOnChange(true);
        refreshRefreshDateList();
        manageCryptoEvents();
        if (!VectorApp.isAppInBackground()) {
            super.notifyDataSetChanged();
        }
    }

    public void onBingRulesUpdate() {
        notifyDataSetChanged();
    }

    public void onPause() {
        this.mEventFormattedTsMap.clear();
    }

    public void onEventTap(String str) {
        if (!this.mIsSearchMode) {
            if (this.mSelectedEventId == null) {
                this.mSelectedEventId = str;
            } else {
                this.mSelectedEventId = null;
            }
            notifyDataSetChanged();
        }
    }

    public void setSearchedEventId(String str) {
        this.mSearchedEventId = str;
        updateHighlightedEventId();
    }

    public void cancelSelectionMode() {
        if (this.mSelectedEventId != null) {
            this.mSelectedEventId = null;
            notifyDataSetChanged();
        }
    }

    public boolean isInSelectionMode() {
        return this.mSelectedEventId != null;
    }

    public void setVectorMessagesAdapterActionsListener(IMessagesAdapterActionsListener iMessagesAdapterActionsListener) {
        this.mVectorMessagesAdapterEventsListener = iMessagesAdapterActionsListener;
        this.mMediasHelper.setVectorMessagesAdapterActionsListener(iMessagesAdapterActionsListener);
        this.mHelper.setVectorMessagesAdapterActionsListener(iMessagesAdapterActionsListener);
        if (this.mLinkMovementMethod != null) {
            this.mLinkMovementMethod.updateListener(iMessagesAdapterActionsListener);
        } else if (iMessagesAdapterActionsListener != null) {
            this.mLinkMovementMethod = new MatrixLinkMovementMethod(iMessagesAdapterActionsListener);
        }
        this.mHelper.setLinkMovementMethod(this.mLinkMovementMethod);
    }

    public MXDeviceInfo getDeviceInfo(String str) {
        if (str != null) {
            return (MXDeviceInfo) this.mE2eDeviceByEventId.get(str);
        }
        return null;
    }

    private static boolean containsOnlyEmojis(String str) {
        boolean z = false;
        if (!TextUtils.isEmpty(str)) {
            Matcher matcher = mEmojisPattern.matcher(str);
            int i = -1;
            int i2 = -1;
            while (matcher.find()) {
                int start = matcher.start();
                if (i < 0) {
                    if (start > 0) {
                        return false;
                    }
                } else if (start != i2) {
                    return false;
                }
                i2 = matcher.end();
                i = start;
            }
            if (-1 != i && i2 == str.length()) {
                z = true;
            }
        }
        return z;
    }

    private int getItemViewType(Event event) {
        String str = event.eventId;
        String type = event.getType();
        if (str != null && this.mHiddenEventIds.contains(str)) {
            return 7;
        }
        if (Event.EVENT_TYPE_MESSAGE_ENCRYPTED.equals(type)) {
            return 0;
        }
        if (event instanceof EventGroup) {
            return 6;
        }
        if (str != null) {
            Integer num = (Integer) this.mEventType.get(str);
            if (num != null) {
                return num.intValue();
            }
        }
        int i = 8;
        if (Event.EVENT_TYPE_MESSAGE.equals(type)) {
            Message message = JsonUtils.toMessage(event.getContent());
            String str2 = message.msgtype;
            if (Message.MSGTYPE_TEXT.equals(str2)) {
                if (containsOnlyEmojis(message.body)) {
                    i = 9;
                } else if (!TextUtils.isEmpty(message.formatted_body) && this.mHelper.containsFencedCodeBlocks(message)) {
                    i = 10;
                }
            } else if (Message.MSGTYPE_IMAGE.equals(str2)) {
                i = 1;
            } else if (Message.MSGTYPE_EMOTE.equals(str2)) {
                i = 3;
            } else if (Message.MSGTYPE_NOTICE.equals(str2)) {
                i = 2;
            } else if (Message.MSGTYPE_FILE.equals(str2) || Message.MSGTYPE_AUDIO.equals(str2)) {
                i = 4;
            } else if (Message.MSGTYPE_VIDEO.equals(str2)) {
                i = 5;
            }
            i = 0;
        } else if (Event.EVENT_TYPE_STICKER.equals(type)) {
            i = 11;
        } else if (!event.isCallEvent() && !Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type) && !Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type) && !Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) && !Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) && !Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type) && !Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type)) {
            if (WidgetsManager.WIDGET_EVENT_TYPE.equals(type)) {
                return 8;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Unknown event type: ");
            sb.append(type);
            throw new RuntimeException(sb.toString());
        }
        if (str != null) {
            this.mEventType.put(str, new Integer(i));
        }
        return i;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x005b  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0110  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void manageSubView(final int r12, android.view.View r13, android.view.View r14, int r15) {
        /*
            r11 = this;
            java.lang.Object r0 = r11.getItem(r12)
            r3 = r0
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r3 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r3
            r0 = 1
            r13.setClickable(r0)
            com.opengarden.firechat.adapters.VectorMessagesAdapter$3 r1 = new com.opengarden.firechat.adapters.VectorMessagesAdapter$3
            r1.<init>(r12)
            r13.setOnClickListener(r1)
            com.opengarden.firechat.adapters.VectorMessagesAdapter$4 r1 = new com.opengarden.firechat.adapters.VectorMessagesAdapter$4
            r1.<init>(r12)
            r13.setOnLongClickListener(r1)
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r3.getEvent()
            boolean r2 = r11.mIsSearchMode
            r4 = 0
            if (r2 != 0) goto L_0x007f
            boolean r2 = isMergeableEvent(r15)
            if (r2 == 0) goto L_0x007f
            if (r12 <= 0) goto L_0x0052
            int r2 = r12 + -1
            java.lang.Object r2 = r11.getItem(r2)
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r2 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r2
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r2.getEvent()
            int r5 = r11.getItemViewType(r2)
            boolean r5 = isMergeableEvent(r5)
            if (r5 == 0) goto L_0x0052
            java.lang.String r2 = r2.getSender()
            java.lang.String r5 = r1.getSender()
            boolean r2 = android.text.TextUtils.equals(r2, r5)
            if (r2 == 0) goto L_0x0052
            r2 = 1
            goto L_0x0053
        L_0x0052:
            r2 = 0
        L_0x0053:
            int r5 = r12 + 1
            int r6 = r11.getCount()
            if (r5 >= r6) goto L_0x0080
            java.lang.Object r5 = r11.getItem(r5)
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r5 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r5
            com.opengarden.firechat.matrixsdk.rest.model.Event r5 = r5.getEvent()
            int r6 = r11.getItemViewType(r5)
            boolean r6 = isMergeableEvent(r6)
            if (r6 == 0) goto L_0x0080
            java.lang.String r5 = r5.getSender()
            java.lang.String r6 = r1.getSender()
            boolean r5 = android.text.TextUtils.equals(r5, r6)
            if (r5 == 0) goto L_0x0080
            r5 = 1
            goto L_0x0081
        L_0x007f:
            r2 = 0
        L_0x0080:
            r5 = 0
        L_0x0081:
            boolean r6 = r11.mergeView(r1, r12, r2)
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r2 = r11.mHelper
            r2.setSenderValue(r13, r3, r6)
            java.lang.String r2 = r11.getFormattedTimestamp(r1)
            android.widget.TextView r2 = com.opengarden.firechat.adapters.VectorMessagesAdapterHelper.setTimestampValue(r13, r2)
            r7 = 8
            if (r2 == 0) goto L_0x00d7
            com.opengarden.firechat.matrixsdk.rest.model.Event r8 = r3.getEvent()
            boolean r8 = r8.isUndeliverable()
            if (r8 != 0) goto L_0x00ba
            com.opengarden.firechat.matrixsdk.rest.model.Event r8 = r3.getEvent()
            boolean r8 = r8.isUnkownDevice()
            if (r8 == 0) goto L_0x00ab
            goto L_0x00ba
        L_0x00ab:
            com.opengarden.firechat.util.ThemeUtils r8 = com.opengarden.firechat.util.ThemeUtils.INSTANCE
            android.content.Context r9 = r11.mContext
            r10 = 2130968721(0x7f040091, float:1.7546104E38)
            int r8 = r8.getColor(r9, r10)
            r2.setTextColor(r8)
            goto L_0x00bf
        L_0x00ba:
            int r8 = r11.mNotSentMessageTextColor
            r2.setTextColor(r8)
        L_0x00bf:
            int r8 = r12 + 1
            int r9 = r11.getCount()
            if (r8 == r9) goto L_0x00d3
            boolean r8 = r11.mIsSearchMode
            if (r8 != 0) goto L_0x00d3
            boolean r8 = r11.mAlwaysShowTimeStamps
            if (r8 == 0) goto L_0x00d0
            goto L_0x00d3
        L_0x00d0:
            r8 = 8
            goto L_0x00d4
        L_0x00d3:
            r8 = 0
        L_0x00d4:
            r2.setVisibility(r8)
        L_0x00d7:
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r2 = r11.mHelper
            android.view.View r8 = r2.setSenderAvatar(r13, r3, r6)
            r2 = 2131296788(0x7f090214, float:1.8211503E38)
            android.view.View r9 = r13.findViewById(r2)
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper.alignSubviewToAvatarView(r14, r9, r8, r6)
            r14 = 2131296809(0x7f090229, float:1.8211545E38)
            android.view.View r14 = r13.findViewById(r14)
            if (r14 == 0) goto L_0x00ff
            if (r5 != 0) goto L_0x00fa
            int r2 = r12 + 1
            int r5 = r11.getCount()
            if (r2 != r5) goto L_0x00fc
        L_0x00fa:
            r4 = 8
        L_0x00fc:
            r14.setVisibility(r4)
        L_0x00ff:
            java.lang.String r14 = r11.headerMessage(r12)
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper.setHeader(r13, r14, r12)
            boolean r12 = r11.mHideReadReceipts
            if (r12 == 0) goto L_0x0110
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r12 = r11.mHelper
            r12.hideReadReceipts(r13)
            goto L_0x0117
        L_0x0110:
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r12 = r11.mHelper
            boolean r14 = r11.mIsPreviewMode
            r12.displayReadReceipts(r13, r3, r14)
        L_0x0117:
            r11.manageSelectionMode(r13, r1, r15)
            r1 = r11
            r2 = r13
            r4 = r6
            r5 = r8
            r6 = r9
            r1.setReadMarker(r2, r3, r4, r5, r6)
            if (r0 == r15) goto L_0x012e
            r12 = 4
            if (r12 == r15) goto L_0x012e
            r12 = 5
            if (r12 == r15) goto L_0x012e
            r12 = 11
            if (r12 != r15) goto L_0x0131
        L_0x012e:
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper.setMediaProgressLayout(r13, r9)
        L_0x0131:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMessagesAdapter.manageSubView(int, android.view.View, android.view.View, int):void");
    }

    private View getTextView(int i, int i2, View view, ViewGroup viewGroup) {
        List<TextView> list;
        int i3;
        boolean z = false;
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(i))).intValue(), viewGroup, false);
        }
        try {
            MessageRow messageRow = (MessageRow) getItem(i2);
            Event event = messageRow.getEvent();
            Message message = JsonUtils.toMessage(event.getContent());
            if (this.mVectorMessagesAdapterEventsListener != null && this.mVectorMessagesAdapterEventsListener.shouldHighlightEvent(event)) {
                z = true;
            }
            if (10 == i) {
                list = populateRowTypeCode(message, view, z);
            } else {
                TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_body);
                if (textView == null) {
                    Log.m211e(LOG_TAG, "getTextView : invalid layout");
                    return view;
                }
                CharSequence textualDisplay = new RiotEventDisplay(this.mContext, event, messageRow.getRoomState(), this.mHtmlToolbox).getTextualDisplay();
                if (textualDisplay == null) {
                    textualDisplay = "";
                }
                SpannableString spannableString = new SpannableString(textualDisplay);
                replaceQuoteSpans(spannableString);
                textView.setText(this.mHelper.highlightPattern(spannableString, this.mPattern, this.mBackgroundColorSpan, z));
                this.mHelper.applyLinkMovementMethod(textView);
                List arrayList = new ArrayList();
                arrayList.add(textView);
                list = arrayList;
            }
            if (messageRow.getEvent().isEncrypting()) {
                i3 = this.mEncryptingMessageTextColor;
            } else {
                if (!messageRow.getEvent().isSending()) {
                    if (!messageRow.getEvent().isUnsent()) {
                        if (!messageRow.getEvent().isUndeliverable()) {
                            if (!messageRow.getEvent().isUnkownDevice()) {
                                i3 = z ? this.mHighlightMessageTextColor : this.mDefaultMessageTextColor;
                            }
                        }
                        i3 = this.mNotSentMessageTextColor;
                    }
                }
                i3 = this.mSendingMessageTextColor;
            }
            for (TextView textColor : list) {
                textColor.setTextColor(i3);
            }
            manageSubView(i2, view, view.findViewById(C1299R.C1301id.messagesAdapter_text_layout), i);
            for (TextView addContentViewListeners : list) {
                addContentViewListeners(view, addContentViewListeners, i2, i);
            }
            this.mHelper.manageURLPreviews(message, view, event.eventId);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getTextView() failed : ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        return view;
    }

    private void replaceQuoteSpans(Spannable spannable) {
        QuoteSpan[] quoteSpanArr;
        for (QuoteSpan quoteSpan : (QuoteSpan[]) spannable.getSpans(0, spannable.length(), QuoteSpan.class)) {
            int spanStart = spannable.getSpanStart(quoteSpan);
            int spanEnd = spannable.getSpanEnd(quoteSpan);
            int spanFlags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            spannable.setSpan(new VectorQuoteSpan(this.mContext), spanStart, spanEnd, spanFlags);
        }
    }

    private List<TextView> populateRowTypeCode(Message message, View view, boolean z) {
        String[] fencedCodeBlocks;
        ArrayList arrayList = new ArrayList();
        LinearLayout linearLayout = (LinearLayout) view.findViewById(C1299R.C1301id.messages_container);
        linearLayout.removeAllViews();
        for (String str : this.mHelper.getFencedCodeBlocks(message)) {
            if (!TextUtils.isEmpty(str)) {
                if (!str.startsWith(VectorMessagesAdapterHelper.START_FENCED_BLOCK) || !str.endsWith(VectorMessagesAdapterHelper.END_FENCED_BLOCK)) {
                    TextView textView = (TextView) this.mLayoutInflater.inflate(C1299R.layout.adapter_item_vector_message_code_text, null);
                    if (TextUtils.equals(Message.FORMAT_MATRIX_HTML, message.format)) {
                        str = str.trim().replace(StringUtils.f158LF, "<br/>").replace(StringUtils.SPACE, "&nbsp;");
                        String sanitisedHtml = this.mHelper.getSanitisedHtml(str);
                        if (sanitisedHtml != null) {
                            str = sanitisedHtml;
                        }
                    }
                    textView.setText(this.mHelper.highlightPattern(new SpannableString(this.mHelper.convertToHtml(str)), this.mPattern, this.mBackgroundColorSpan, z));
                    this.mHelper.applyLinkMovementMethod(textView);
                    linearLayout.addView(textView);
                    arrayList.add(textView);
                } else {
                    String trim = str.substring(VectorMessagesAdapterHelper.START_FENCED_BLOCK.length(), str.length() - VectorMessagesAdapterHelper.END_FENCED_BLOCK.length()).trim();
                    View inflate = this.mLayoutInflater.inflate(C1299R.layout.adapter_item_vector_message_code_block, null);
                    linearLayout.addView(inflate);
                    TextView textView2 = (TextView) inflate.findViewById(C1299R.C1301id.messagesAdapter_body);
                    this.mHelper.convertToHtml(trim);
                    this.mHelper.highlightFencedCode(textView2);
                    this.mHelper.applyLinkMovementMethod(textView2);
                    linearLayout.addView(inflate);
                    arrayList.add(textView2);
                    ((View) textView2.getParent()).setBackgroundColor(ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.markdown_block_background_color));
                }
            }
        }
        return arrayList;
    }

    /* JADX WARNING: type inference failed for: r1v5, types: [com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View getImageVideoView(int r6, int r7, android.view.View r8, android.view.ViewGroup r9) {
        /*
            r5 = this;
            r0 = 0
            if (r8 != 0) goto L_0x0019
            android.view.LayoutInflater r8 = r5.mLayoutInflater
            java.util.HashMap<java.lang.Integer, java.lang.Integer> r1 = r5.mRowTypeToLayoutId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r6)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            android.view.View r8 = r8.inflate(r1, r9, r0)
        L_0x0019:
            java.lang.Object r9 = r5.getItem(r7)     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r9 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r9     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r9 = r9.getEvent()     // Catch:{ Exception -> 0x00c1 }
            r1 = 0
            r2 = -1
            r3 = 1
            if (r6 != r3) goto L_0x0040
            com.google.gson.JsonElement r1 = r9.getContent()     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toImageMessage(r1)     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r3 = "image/gif"
            java.lang.String r4 = r1.getMimeType()     // Catch:{ Exception -> 0x00c1 }
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x00c1 }
            if (r3 == 0) goto L_0x005b
            r2 = 2131230862(0x7f08008e, float:1.8077789E38)
            goto L_0x005b
        L_0x0040:
            r3 = 5
            if (r6 != r3) goto L_0x004f
            com.google.gson.JsonElement r1 = r9.getContent()     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toVideoMessage(r1)     // Catch:{ Exception -> 0x00c1 }
            r2 = 2131230864(0x7f080090, float:1.8077793E38)
            goto L_0x005b
        L_0x004f:
            r3 = 11
            if (r6 != r3) goto L_0x005b
            com.google.gson.JsonElement r1 = r9.getContent()     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toStickerMessage(r1)     // Catch:{ Exception -> 0x00c1 }
        L_0x005b:
            r3 = 2131296797(0x7f09021d, float:1.821152E38)
            android.view.View r3 = r8.findViewById(r3)     // Catch:{ Exception -> 0x00c1 }
            android.widget.ImageView r3 = (android.widget.ImageView) r3     // Catch:{ Exception -> 0x00c1 }
            if (r3 != 0) goto L_0x006e
            java.lang.String r6 = LOG_TAG     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r7 = "getImageVideoView : invalid layout"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r7)     // Catch:{ Exception -> 0x00c1 }
            return r8
        L_0x006e:
            r3.setBackgroundColor(r0)     // Catch:{ Exception -> 0x00c1 }
            if (r2 <= 0) goto L_0x0086
            android.content.Context r4 = r5.getContext()     // Catch:{ Exception -> 0x00c1 }
            android.content.res.Resources r4 = r4.getResources()     // Catch:{ Exception -> 0x00c1 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeResource(r4, r2)     // Catch:{ Exception -> 0x00c1 }
            r3.setImageBitmap(r2)     // Catch:{ Exception -> 0x00c1 }
            r3.setVisibility(r0)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x008b
        L_0x0086:
            r0 = 8
            r3.setVisibility(r0)     // Catch:{ Exception -> 0x00c1 }
        L_0x008b:
            if (r1 == 0) goto L_0x009c
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r0 = r5.mHelper     // Catch:{ Exception -> 0x00c1 }
            r0.hideStickerDescription(r8)     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.adapters.VectorMessagesAdapterMediasHelper r0 = r5.mMediasHelper     // Catch:{ Exception -> 0x00c1 }
            r0.managePendingImageVideoDownload(r8, r9, r1, r7)     // Catch:{ Exception -> 0x00c1 }
            com.opengarden.firechat.adapters.VectorMessagesAdapterMediasHelper r0 = r5.mMediasHelper     // Catch:{ Exception -> 0x00c1 }
            r0.managePendingImageVideoUpload(r8, r9, r1)     // Catch:{ Exception -> 0x00c1 }
        L_0x009c:
            r0 = 2131296796(0x7f09021c, float:1.8211519E38)
            android.view.View r0 = r8.findViewById(r0)     // Catch:{ Exception -> 0x00c1 }
            boolean r9 = r9.isSent()     // Catch:{ Exception -> 0x00c1 }
            if (r9 == 0) goto L_0x00ac
            r9 = 1065353216(0x3f800000, float:1.0)
            goto L_0x00ae
        L_0x00ac:
            r9 = 1056964608(0x3f000000, float:0.5)
        L_0x00ae:
            r0.setAlpha(r9)     // Catch:{ Exception -> 0x00c1 }
            r5.manageSubView(r7, r8, r0, r6)     // Catch:{ Exception -> 0x00c1 }
            r9 = 2131296795(0x7f09021b, float:1.8211517E38)
            android.view.View r9 = r8.findViewById(r9)     // Catch:{ Exception -> 0x00c1 }
            android.widget.ImageView r9 = (android.widget.ImageView) r9     // Catch:{ Exception -> 0x00c1 }
            r5.addContentViewListeners(r8, r9, r7, r6)     // Catch:{ Exception -> 0x00c1 }
            goto L_0x00dc
        L_0x00c1:
            r6 = move-exception
            java.lang.String r7 = LOG_TAG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r0 = "## getImageVideoView() failed : "
            r9.append(r0)
            java.lang.String r6 = r6.getMessage()
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r6)
        L_0x00dc:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMessagesAdapter.getImageVideoView(int, int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    private View getNoticeRoomMemberView(int i, int i2, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(i))).intValue(), viewGroup, false);
        }
        try {
            MessageRow messageRow = (MessageRow) getItem(i2);
            Event event = messageRow.getEvent();
            CharSequence textualDisplay = new RiotEventDisplay(this.mContext, event, messageRow.getRoomState()).getTextualDisplay();
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_body);
            if (textView == null) {
                Log.m211e(LOG_TAG, "getNoticeRoomMemberView : invalid layout");
                return view;
            }
            if (TextUtils.isEmpty(textualDisplay)) {
                textView.setText("");
            } else {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(textualDisplay);
                MatrixURLSpan.refreshMatrixSpans(spannableStringBuilder, this.mVectorMessagesAdapterEventsListener);
                textView.setText(spannableStringBuilder);
            }
            manageSubView(i2, view, view.findViewById(C1299R.C1301id.messagesAdapter_text_layout), i);
            addContentViewListeners(view, textView, i2, i);
            textView.setAlpha(1.0f);
            textView.setTextColor(getNoticeTextColor());
            this.mHelper.manageURLPreviews(JsonUtils.toMessage(event.getContent()), view, event.eventId);
            return view;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getNoticeRoomMemberView() failed : ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    private View getEmoteView(int i, View view, ViewGroup viewGroup) {
        int i2;
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(3))).intValue(), viewGroup, false);
        }
        try {
            MessageRow messageRow = (MessageRow) getItem(i);
            Event event = messageRow.getEvent();
            RoomState roomState = messageRow.getRoomState();
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_body);
            if (textView == null) {
                Log.m211e(LOG_TAG, "getEmoteView : invalid layout");
                return view;
            }
            Message message = JsonUtils.toMessage(event.getContent());
            String sender = roomState == null ? event.getSender() : roomState.getMemberName(event.getSender());
            StringBuilder sb = new StringBuilder();
            sb.append("* ");
            sb.append(sender);
            sb.append(StringUtils.SPACE);
            sb.append(message.body);
            String sb2 = sb.toString();
            if (TextUtils.equals(Message.FORMAT_MATRIX_HTML, message.format)) {
                String sanitisedHtml = this.mHelper.getSanitisedHtml(message.formatted_body);
                if (sanitisedHtml != null) {
                    CharSequence convertToHtml = this.mHelper.convertToHtml(sanitisedHtml);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("* ");
                    sb3.append(sender);
                    sb3.append(StringUtils.SPACE);
                    sb3.append(convertToHtml);
                    sb2 = sb3.toString();
                }
            }
            textView.setText(this.mHelper.highlightPattern(new SpannableString(sb2), null, this.mBackgroundColorSpan, false));
            this.mHelper.applyLinkMovementMethod(textView);
            if (messageRow.getEvent().isEncrypting()) {
                i2 = this.mEncryptingMessageTextColor;
            } else {
                if (!messageRow.getEvent().isSending()) {
                    if (!messageRow.getEvent().isUnsent()) {
                        if (!messageRow.getEvent().isUndeliverable()) {
                            if (!messageRow.getEvent().isUnkownDevice()) {
                                i2 = this.mDefaultMessageTextColor;
                            }
                        }
                        i2 = this.mNotSentMessageTextColor;
                    }
                }
                i2 = this.mSendingMessageTextColor;
            }
            textView.setTextColor(i2);
            manageSubView(i, view, view.findViewById(C1299R.C1301id.messagesAdapter_text_layout), 3);
            addContentViewListeners(view, textView, i, 3);
            this.mHelper.manageURLPreviews(message, view, event.eventId);
            return view;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## getEmoteView() failed : ");
            sb4.append(e.getMessage());
            Log.m211e(str, sb4.toString());
        }
    }

    private View getFileView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(4))).intValue(), viewGroup, false);
        }
        try {
            Event event = ((MessageRow) getItem(i)).getEvent();
            FileMessage fileMessage = JsonUtils.toFileMessage(event.getContent());
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_filename);
            if (textView == null) {
                Log.m211e(LOG_TAG, "getFileView : invalid layout");
                return view;
            }
            textView.setPaintFlags(textView.getPaintFlags() | 8);
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.f158LF);
            sb.append(fileMessage.body);
            sb.append(StringUtils.f158LF);
            textView.setText(sb.toString());
            ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.messagesAdapter_image_type);
            if (imageView != null) {
                imageView.setImageResource(Message.MSGTYPE_AUDIO.equals(fileMessage.msgtype) ? C1299R.C1300drawable.filetype_audio : C1299R.C1300drawable.filetype_attachment);
            }
            imageView.setBackgroundColor(0);
            this.mMediasHelper.managePendingFileDownload(view, event, fileMessage, i);
            this.mMediasHelper.managePendingUpload(view, event, 4, fileMessage.url);
            manageSubView(i, view, view.findViewById(C1299R.C1301id.messagesAdapter_file_layout), 4);
            addContentViewListeners(view, textView, i, 4);
            return view;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## getFileView() failed ");
            sb2.append(e.getMessage());
            Log.m211e(str, sb2.toString());
        }
    }

    private View getHiddenView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(7))).intValue(), viewGroup, false);
        }
        VectorMessagesAdapterHelper.setHeader(view, headerMessage(i), i);
        return view;
    }

    private View getMergeView(int i, View view, ViewGroup viewGroup) {
        float f;
        boolean z = false;
        if (view == null) {
            view = this.mLayoutInflater.inflate(((Integer) this.mRowTypeToLayoutId.get(Integer.valueOf(6))).intValue(), viewGroup, false);
        }
        try {
            final EventGroup eventGroup = (EventGroup) ((MessageRow) getItem(i)).getEvent();
            View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_merge_header_layout);
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_merge_header_text_view);
            TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_merge_summary);
            View findViewById2 = view.findViewById(C1299R.C1301id.messagesAdapter_merge_separator);
            View findViewById3 = view.findViewById(C1299R.C1301id.messagesAdapter_merge_avatar_list);
            if (!(findViewById == null || textView == null || textView2 == null || findViewById2 == null)) {
                if (findViewById3 != null) {
                    findViewById2.setVisibility(eventGroup.isExpanded() ? 0 : 8);
                    textView2.setVisibility(eventGroup.isExpanded() ? 8 : 0);
                    findViewById3.setVisibility(eventGroup.isExpanded() ? 8 : 0);
                    textView.setText(eventGroup.isExpanded() ? "collapse" : "expand");
                    if (!eventGroup.isExpanded()) {
                        findViewById3.setVisibility(0);
                        ArrayList arrayList = new ArrayList();
                        arrayList.add((ImageView) view.findViewById(C1299R.C1301id.mels_list_avatar_1));
                        arrayList.add((ImageView) view.findViewById(C1299R.C1301id.mels_list_avatar_2));
                        arrayList.add((ImageView) view.findViewById(C1299R.C1301id.mels_list_avatar_3));
                        arrayList.add((ImageView) view.findViewById(C1299R.C1301id.mels_list_avatar_4));
                        arrayList.add((ImageView) view.findViewById(C1299R.C1301id.mels_list_avatar_5));
                        List avatarRows = eventGroup.getAvatarRows(arrayList.size());
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            ImageView imageView = (ImageView) arrayList.get(i2);
                            if (i2 < avatarRows.size()) {
                                this.mHelper.loadMemberAvatar(imageView, (MessageRow) avatarRows.get(i2));
                                imageView.setVisibility(0);
                            } else {
                                imageView.setVisibility(8);
                            }
                        }
                        textView2.setText(eventGroup.toString(this.mContext));
                    }
                    findViewById.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            eventGroup.setIsExpanded(!eventGroup.isExpanded());
                            VectorMessagesAdapter.this.updateHighlightedEventId();
                            if (eventGroup.contains(VectorMessagesAdapter.this.mSelectedEventId)) {
                                VectorMessagesAdapter.this.cancelSelectionMode();
                            } else {
                                VectorMessagesAdapter.this.notifyDataSetChanged();
                            }
                        }
                    });
                    view.findViewById(C1299R.C1301id.messagesAdapter_highlight_message_marker).setBackgroundColor(ContextCompat.getColor(this.mContext, TextUtils.equals(this.mHighlightedEventId, eventGroup.eventId) ? C1299R.color.vector_green_color : 17170445));
                    VectorMessagesAdapterHelper.setHeader(view, headerMessage(i), i);
                    if (this.mSelectedEventId != null) {
                        z = true;
                    }
                    boolean equals = TextUtils.equals(eventGroup.eventId, this.mSelectedEventId);
                    if (z) {
                        if (!equals) {
                            f = 0.2f;
                            view.findViewById(C1299R.C1301id.messagesAdapter_body_view).setAlpha(f);
                            return view;
                        }
                    }
                    f = 1.0f;
                    view.findViewById(C1299R.C1301id.messagesAdapter_body_view).setAlpha(f);
                    return view;
                }
            }
            Log.m211e(LOG_TAG, "getMergeView : invalid layout");
            return view;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getMergeView() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    private boolean isSupportedRow(MessageRow messageRow) {
        CharSequence charSequence;
        Event event = messageRow.getEvent();
        if (event == null || event.eventId == null) {
            Log.m211e(LOG_TAG, "## isSupportedRow() : invalid row");
            return false;
        }
        String str = event.eventId;
        MessageRow messageRow2 = (MessageRow) this.mEventRowMap.get(str);
        if (messageRow2 != null) {
            if (event.getAge() == Event.DUMMY_EVENT_AGE) {
                messageRow2.updateEvent(event);
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## isSupportedRow() : update the timestamp of ");
                sb.append(str);
                Log.m209d(str2, sb.toString());
            } else {
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## isSupportedRow() : the event ");
                sb2.append(str);
                sb2.append(" has already been received");
                Log.m211e(str3, sb2.toString());
            }
            return false;
        }
        boolean isDisplayableEvent = VectorMessagesAdapterHelper.isDisplayableEvent(this.mContext, messageRow);
        if (isDisplayableEvent && TextUtils.equals(event.getType(), Event.EVENT_TYPE_STATE_ROOM_MEMBER)) {
            String str4 = JsonUtils.toRoomMember(event.getContent()).membership;
            if (PreferencesManager.hideJoinLeaveMessages(this.mContext)) {
                isDisplayableEvent = !TextUtils.equals(str4, RoomMember.MEMBERSHIP_LEAVE) && !TextUtils.equals(str4, RoomMember.MEMBERSHIP_JOIN);
            }
            if (isDisplayableEvent && PreferencesManager.hideAvatarDisplayNameChangeMessages(this.mContext) && TextUtils.equals(str4, RoomMember.MEMBERSHIP_JOIN)) {
                EventContent eventContent = JsonUtils.toEventContent(event.getContentAsJsonObject());
                EventContent prevContent = event.getPrevContent();
                String str5 = eventContent.displayname;
                String str6 = eventContent.avatar_url;
                String str7 = null;
                if (prevContent != null) {
                    str7 = prevContent.displayname;
                    charSequence = prevContent.avatar_url;
                } else {
                    charSequence = null;
                }
                isDisplayableEvent = TextUtils.equals(str7, str5) && TextUtils.equals(str6, charSequence);
            }
        }
        return isDisplayableEvent;
    }

    private String getFormattedTimestamp(Event event) {
        String str = (String) this.mEventFormattedTsMap.get(event.eventId);
        if (str != null) {
            return str;
        }
        String tsToString = event.isValidOriginServerTs() ? AdapterUtils.tsToString(this.mContext, event.getOriginServerTs(), true) : StringUtils.SPACE;
        this.mEventFormattedTsMap.put(event.eventId, tsToString);
        return tsToString;
    }

    private void refreshRefreshDateList() {
        ArrayList<Date> arrayList = new ArrayList<>();
        Date zeroTimeDate = AdapterUtils.zeroTimeDate(new Date());
        for (int i = 0; i < getCount(); i++) {
            Event event = ((MessageRow) getItem(i)).getEvent();
            if (event.isValidOriginServerTs()) {
                zeroTimeDate = AdapterUtils.zeroTimeDate(new Date(event.getOriginServerTs()));
            }
            arrayList.add(zeroTimeDate);
        }
        synchronized (this) {
            this.mMessagesDateList = arrayList;
            this.mReferenceDate = new Date();
        }
    }

    private String dateDiff(Date date, long j) {
        if (j == 0) {
            return this.mContext.getResources().getString(C1299R.string.today);
        }
        if (j == 1) {
            return this.mContext.getResources().getString(C1299R.string.yesterday);
        }
        if (j < 7) {
            return new SimpleDateFormat("EEEE", this.mLocale).format(date);
        }
        return DateUtils.formatDateRange(this.mContext, new Formatter(new StringBuilder(50), this.mLocale), date.getTime(), date.getTime(), 524310).toString();
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0022 A[Catch:{ all -> 0x0017 }] */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002b A[Catch:{ all -> 0x0017 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String headerMessage(int r11) {
        /*
            r10 = this;
            monitor-enter(r10)
            r0 = 0
            if (r11 <= 0) goto L_0x0019
            java.util.ArrayList<java.util.Date> r1 = r10.mMessagesDateList     // Catch:{ all -> 0x0017 }
            int r1 = r1.size()     // Catch:{ all -> 0x0017 }
            if (r11 >= r1) goto L_0x0019
            java.util.ArrayList<java.util.Date> r1 = r10.mMessagesDateList     // Catch:{ all -> 0x0017 }
            int r2 = r11 + -1
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0017 }
            java.util.Date r1 = (java.util.Date) r1     // Catch:{ all -> 0x0017 }
            goto L_0x001a
        L_0x0017:
            r11 = move-exception
            goto L_0x0058
        L_0x0019:
            r1 = r0
        L_0x001a:
            java.util.ArrayList<java.util.Date> r2 = r10.mMessagesDateList     // Catch:{ all -> 0x0017 }
            int r2 = r2.size()     // Catch:{ all -> 0x0017 }
            if (r11 >= r2) goto L_0x002b
            java.util.ArrayList<java.util.Date> r2 = r10.mMessagesDateList     // Catch:{ all -> 0x0017 }
            java.lang.Object r11 = r2.get(r11)     // Catch:{ all -> 0x0017 }
            java.util.Date r11 = (java.util.Date) r11     // Catch:{ all -> 0x0017 }
            goto L_0x002c
        L_0x002b:
            r11 = r0
        L_0x002c:
            monitor-exit(r10)     // Catch:{ all -> 0x0017 }
            if (r11 != 0) goto L_0x0030
            return r0
        L_0x0030:
            if (r1 == 0) goto L_0x0043
            r2 = 0
            long r4 = r1.getTime()
            long r6 = r11.getTime()
            long r8 = r4 - r6
            int r1 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x0043
            return r0
        L_0x0043:
            java.util.Date r0 = r10.mReferenceDate
            long r0 = r0.getTime()
            long r2 = r11.getTime()
            long r4 = r0 - r2
            r0 = 86400000(0x5265c00, double:4.2687272E-316)
            long r4 = r4 / r0
            java.lang.String r11 = r10.dateDiff(r11, r4)
            return r11
        L_0x0058:
            monitor-exit(r10)     // Catch:{ all -> 0x0017 }
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMessagesAdapter.headerMessage(int):java.lang.String");
    }

    private void manageSelectionMode(View view, Event event, int i) {
        String str = event.eventId;
        boolean z = this.mSelectedEventId != null;
        boolean equals = TextUtils.equals(str, this.mSelectedEventId);
        view.findViewById(C1299R.C1301id.messagesAdapter_action_image).setVisibility(equals ? 0 : 8);
        float f = (!z || equals) ? 1.0f : 0.2f;
        view.findViewById(C1299R.C1301id.messagesAdapter_body_view).setAlpha(f);
        view.findViewById(C1299R.C1301id.messagesAdapter_avatars_list).setAlpha(f);
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_urls_preview_list);
        if (findViewById != null) {
            findViewById.setAlpha(f);
        }
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_timestamp);
        if (z && equals) {
            textView.setVisibility(0);
        }
        if (Event.EVENT_TYPE_STICKER.equals(event.getType())) {
            StickerMessage stickerMessage = JsonUtils.toStickerMessage(event.getContent());
            if (stickerMessage != null && z && equals) {
                this.mHelper.showStickerDescription(view, stickerMessage);
            }
        }
        if (!(event instanceof EventGroup)) {
            View findViewById2 = view.findViewById(C1299R.C1301id.message_timestamp_layout);
            final String str2 = str;
            final Event event2 = event;
            final View view2 = view;
            final int i2 = i;
            C17946 r0 = new OnClickListener() {
                public void onClick(View view) {
                    if (TextUtils.equals(str2, VectorMessagesAdapter.this.mSelectedEventId)) {
                        VectorMessagesAdapter.this.onMessageClick(event2, VectorMessagesAdapter.this.getEventText(view2, event2, i2), view2.findViewById(C1299R.C1301id.messagesAdapter_action_anchor));
                    } else {
                        VectorMessagesAdapter.this.onEventTap(str2);
                    }
                }
            };
            findViewById2.setOnClickListener(r0);
            final Event event3 = event;
            final View view3 = view;
            final int i3 = i;
            final String str3 = str;
            C17957 r02 = new OnLongClickListener() {
                public boolean onLongClick(View view) {
                    if (VectorMessagesAdapter.this.mIsSearchMode) {
                        return false;
                    }
                    VectorMessagesAdapter.this.onMessageClick(event3, VectorMessagesAdapter.this.getEventText(view3, event3, i3), view3.findViewById(C1299R.C1301id.messagesAdapter_action_anchor));
                    VectorMessagesAdapter.this.mSelectedEventId = str3;
                    VectorMessagesAdapter.this.notifyDataSetChanged();
                    return true;
                }
            };
            view.setOnLongClickListener(r02);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean mergeView(Event event, int i, boolean z) {
        if (z) {
            z = headerMessage(i) == null;
        }
        if (!z || event.isCallEvent()) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public String getEventText(View view, Event event, int i) {
        if (view != null) {
            if (10 == i || i == 0) {
                return JsonUtils.toMessage(event.getContent()).body;
            }
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_body);
            if (textView != null) {
                return textView.getText().toString();
            }
        }
        return null;
    }

    private void addContentViewListeners(View view, View view2, final int i, int i2) {
        view2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener != null && i < VectorMessagesAdapter.this.getCount()) {
                    VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener.onContentClick(i);
                }
            }
        });
        final int i3 = i;
        final View view3 = view2;
        final int i4 = i2;
        final View view4 = view;
        C17979 r1 = new OnLongClickListener() {
            public boolean onLongClick(View view) {
                if (i3 < VectorMessagesAdapter.this.getCount()) {
                    Event event = ((MessageRow) VectorMessagesAdapter.this.getItem(i3)).getEvent();
                    if (!VectorMessagesAdapter.this.mIsSearchMode) {
                        VectorMessagesAdapter.this.onMessageClick(event, VectorMessagesAdapter.this.getEventText(view3, event, i4), view4.findViewById(C1299R.C1301id.messagesAdapter_action_anchor));
                        VectorMessagesAdapter.this.mSelectedEventId = event.eventId;
                        VectorMessagesAdapter.this.notifyDataSetChanged();
                        return true;
                    }
                }
                return true;
            }
        };
        view2.setOnLongClickListener(r1);
    }

    private void displayE2eIcon(View view, int i) {
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.message_adapter_e2e_icon);
        if (imageView != null) {
            View findViewById = view.findViewById(C1299R.C1301id.e2e_sender_margin);
            View findViewById2 = view.findViewById(C1299R.C1301id.messagesAdapter_sender);
            final Event event = ((MessageRow) getItem(i)).getEvent();
            if (this.mE2eIconByEventId.containsKey(event.eventId)) {
                if (findViewById != null) {
                    findViewById.setVisibility(findViewById2.getVisibility());
                }
                imageView.setVisibility(0);
                Object obj = this.mE2eIconByEventId.get(event.eventId);
                if (obj instanceof Drawable) {
                    imageView.setImageDrawable((Drawable) obj);
                } else {
                    imageView.setImageResource(((Integer) obj).intValue());
                }
                int itemViewType = getItemViewType(i);
                if (itemViewType == 1 || itemViewType == 5) {
                    View findViewById3 = view.findViewById(C1299R.C1301id.messagesAdapter_body_layout);
                    MarginLayoutParams marginLayoutParams = (MarginLayoutParams) findViewById3.getLayoutParams();
                    MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) imageView.getLayoutParams();
                    marginLayoutParams2.setMargins(marginLayoutParams.leftMargin, marginLayoutParams2.topMargin, marginLayoutParams2.rightMargin, marginLayoutParams2.bottomMargin);
                    marginLayoutParams.setMargins(4, marginLayoutParams.topMargin, marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin);
                    imageView.setLayoutParams(marginLayoutParams2);
                    findViewById3.setLayoutParams(marginLayoutParams);
                }
                imageView.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener != null) {
                            VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener.onE2eIconClick(event, (MXDeviceInfo) VectorMessagesAdapter.this.mE2eDeviceByEventId.get(event.eventId));
                        }
                    }
                });
                return;
            }
            imageView.setVisibility(8);
            if (findViewById != null) {
                findViewById.setVisibility(8);
            }
        }
    }

    private void manageCryptoEvents() {
        HashMap<String, Object> hashMap = new HashMap<>();
        HashMap<String, MXDeviceInfo> hashMap2 = new HashMap<>();
        if (this.mIsRoomEncrypted && this.mSession.isCryptoEnabled()) {
            for (int i = 0; i < getCount(); i++) {
                Event event = ((MessageRow) getItem(i)).getEvent();
                if (event.mSentState != SentState.SENT) {
                    hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_verified));
                } else if (!event.isEncrypted()) {
                    hashMap.put(event.eventId, this.mPadlockDrawable);
                } else if (event.getCryptoError() != null) {
                    hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_blocked));
                } else {
                    EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent().getAsJsonObject());
                    if (!TextUtils.equals(this.mSession.getCredentials().deviceId, encryptedEventContent.device_id) || !TextUtils.equals(this.mSession.getMyUserId(), event.getSender())) {
                        MXDeviceInfo deviceWithIdentityKey = this.mSession.getCrypto().deviceWithIdentityKey(encryptedEventContent.sender_key, event.getSender(), encryptedEventContent.algorithm);
                        if (deviceWithIdentityKey != null) {
                            hashMap2.put(event.eventId, deviceWithIdentityKey);
                            if (deviceWithIdentityKey.isVerified()) {
                                hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_verified));
                            } else if (deviceWithIdentityKey.isBlocked()) {
                                hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_blocked));
                            } else {
                                hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_warning));
                            }
                        } else {
                            hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_warning));
                        }
                    } else {
                        hashMap.put(event.eventId, Integer.valueOf(C1299R.C1300drawable.e2e_verified));
                        MXDeviceInfo deviceWithIdentityKey2 = this.mSession.getCrypto().deviceWithIdentityKey(encryptedEventContent.sender_key, event.getSender(), encryptedEventContent.algorithm);
                        if (deviceWithIdentityKey2 != null) {
                            hashMap2.put(event.eventId, deviceWithIdentityKey2);
                        }
                    }
                }
            }
        }
        this.mE2eDeviceByEventId = hashMap2;
        this.mE2eIconByEventId = hashMap;
    }

    public void resetReadMarker() {
        Log.m209d(LOG_TAG, "resetReadMarker");
        this.mReadMarkerEventId = null;
    }

    public void updateReadMarker(String str, String str2) {
        this.mReadMarkerEventId = str;
        this.mReadReceiptEventId = str2;
        if (str != null && !str.equals(this.mReadMarkerEventId)) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("updateReadMarker read marker id has changed: ");
            sb.append(str);
            Log.m209d(str3, sb.toString());
            this.mCanShowReadMarker = true;
            notifyDataSetChanged();
        }
    }

    public void setReadMarkerListener(ReadMarkerListener readMarkerListener) {
        this.mReadMarkerListener = readMarkerListener;
    }

    public void setImageGetter(VectorImageGetter vectorImageGetter) {
        this.mImageGetter = vectorImageGetter;
        this.mHelper.setImageGetter(vectorImageGetter);
    }

    private void animateReadMarkerView(final Event event, final View view) {
        if (view != null && this.mCanShowReadMarker) {
            this.mCanShowReadMarker = false;
            if (view.getAnimation() == null) {
                Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), C1299R.anim.unread_marker_anim);
                loadAnimation.setStartOffset(500);
                loadAnimation.setAnimationListener(new AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(8);
                    }
                });
                view.setAnimation(loadAnimation);
            }
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    if (view != null && view.getAnimation() != null) {
                        view.setVisibility(0);
                        view.getAnimation().start();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                if (VectorMessagesAdapter.this.mReadMarkerListener != null) {
                                    VectorMessagesAdapter.this.mReadMarkerListener.onReadMarkerDisplayed(event, view);
                                }
                            }
                        }, view.getAnimation().getDuration() + view.getAnimation().getStartOffset());
                    } else if (VectorMessagesAdapter.this.mReadMarkerListener != null) {
                        VectorMessagesAdapter.this.mReadMarkerListener.onReadMarkerDisplayed(event, view);
                    }
                }
            });
        }
    }

    private boolean isReadMarkedEvent(Event event) {
        if (this.mReadMarkerEventId == null || !this.mHiddenEventIds.contains(this.mReadMarkerEventId) || !(event instanceof EventGroup)) {
            return event.eventId.equals(this.mReadMarkerEventId);
        }
        return ((EventGroup) event).contains(this.mReadMarkerEventId);
    }

    private void handleReadMarker(View view, int i) {
        MessageRow messageRow = (MessageRow) getItem(i);
        Event event = messageRow != null ? messageRow.getEvent() : null;
        View findViewById = view.findViewById(C1299R.C1301id.message_read_marker);
        if (findViewById == null) {
            return;
        }
        if (event != null && !event.isDummyEvent() && this.mReadMarkerEventId != null && this.mCanShowReadMarker && isReadMarkedEvent(event) && !this.mIsPreviewMode && !this.mIsSearchMode && (!this.mReadMarkerEventId.equals(this.mReadReceiptEventId) || i < getCount() - 1)) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(" Display read marker ");
            sb.append(event.eventId);
            sb.append(" mReadMarkerEventId");
            sb.append(this.mReadMarkerEventId);
            Log.m209d(str, sb.toString());
            animateReadMarkerView(event, findViewById);
        } else if (8 != findViewById.getVisibility()) {
            Log.m215v(LOG_TAG, "hide read marker");
            findViewById.setVisibility(8);
        }
    }

    private void setReadMarker(View view, MessageRow messageRow, boolean z, View view2, View view3) {
        Event event = messageRow.getEvent();
        View findViewById = view.findViewById(C1299R.C1301id.messagesAdapter_highlight_message_marker);
        View findViewById2 = view.findViewById(C1299R.C1301id.message_read_marker);
        if (findViewById != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) findViewById.getLayoutParams();
            marginLayoutParams.setMargins(5, marginLayoutParams.topMargin, 5, marginLayoutParams.bottomMargin);
            if (!TextUtils.equals(this.mHighlightedEventId, event.eventId)) {
                findViewById.setBackgroundColor(ContextCompat.getColor(this.mContext, 17170445));
            } else if (this.mIsUnreadViewMode) {
                findViewById.setBackgroundColor(ContextCompat.getColor(this.mContext, 17170445));
                if (findViewById2 != null) {
                    animateReadMarkerView(event, findViewById2);
                }
            } else {
                LayoutParams layoutParams = view2.getLayoutParams();
                MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) view3.getLayoutParams();
                if (z) {
                    marginLayoutParams.setMargins(layoutParams.width + 5, marginLayoutParams.topMargin, 5, marginLayoutParams.bottomMargin);
                } else {
                    marginLayoutParams.setMargins(5, marginLayoutParams.topMargin, 5, marginLayoutParams.bottomMargin);
                }
                marginLayoutParams2.setMargins(4, marginLayoutParams2.topMargin, 4, marginLayoutParams2.bottomMargin);
                findViewById.setBackgroundColor(ContextCompat.getColor(this.mContext, C1299R.color.vector_green_color));
            }
            findViewById.setLayoutParams(marginLayoutParams);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void onMessageClick(final Event event, final String str, View view) {
        PopupMenu popupMenu = VERSION.SDK_INT >= 19 ? new PopupMenu(this.mContext, view, GravityCompat.END) : new PopupMenu(this.mContext, view);
        popupMenu.getMenuInflater().inflate(C1299R.C1302menu.vector_room_message_settings, popupMenu.getMenu());
        boolean z = false;
        try {
            Field[] declaredFields = popupMenu.getClass().getDeclaredFields();
            int length = declaredFields.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Field field = declaredFields[i];
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object obj = field.get(popupMenu);
                    Class.forName(obj.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(true)});
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onMessageClick : force to display the icons failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
        Menu menu = popupMenu.getMenu();
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this.mContext, C1299R.attr.settings_icon_tint_color));
        for (int i2 = 0; i2 < menu.size(); i2++) {
            menu.getItem(i2).setVisible(false);
        }
        menu.findItem(C1299R.C1301id.ic_action_view_source).setVisible(true);
        menu.findItem(C1299R.C1301id.ic_action_view_decrypted_source).setVisible(event.isEncrypted() && event.getClearEvent() != null);
        menu.findItem(C1299R.C1301id.ic_action_vector_permalink).setVisible(true);
        if (!TextUtils.isEmpty(str)) {
            menu.findItem(C1299R.C1301id.ic_action_vector_copy).setVisible(true);
            menu.findItem(C1299R.C1301id.ic_action_vector_quote).setVisible(true);
        }
        if (event.isUploadingMedias(this.mMediasCache)) {
            menu.findItem(C1299R.C1301id.ic_action_vector_cancel_upload).setVisible(true);
        }
        if (event.isDownloadingMedias(this.mMediasCache)) {
            menu.findItem(C1299R.C1301id.ic_action_vector_cancel_download).setVisible(true);
        }
        if (event.canBeResent()) {
            menu.findItem(C1299R.C1301id.ic_action_vector_resend_message).setVisible(true);
            if (event.isUndeliverable() || event.isUnkownDevice()) {
                menu.findItem(C1299R.C1301id.ic_action_vector_redact_message).setVisible(true);
            }
        } else if (event.mSentState == SentState.SENT) {
            boolean z2 = !this.mIsPreviewMode && !TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE_ENCRYPTION);
            if (z2) {
                if (!TextUtils.equals(event.sender, this.mSession.getMyUserId())) {
                    Room room = this.mSession.getDataHandler().getRoom(event.roomId);
                    if (!(room == null || room.getLiveState().getPowerLevels() == null)) {
                        PowerLevels powerLevels = room.getLiveState().getPowerLevels();
                        if (powerLevels.getUserPowerLevel(this.mSession.getMyUserId()) < powerLevels.redact) {
                            z2 = false;
                        }
                    }
                }
                z2 = true;
            }
            menu.findItem(C1299R.C1301id.ic_action_vector_redact_message).setVisible(z2);
            if (Event.EVENT_TYPE_MESSAGE.equals(event.getType())) {
                Message message = JsonUtils.toMessage(event.getContentAsJsonObject());
                menu.findItem(C1299R.C1301id.ic_action_vector_share).setVisible(!this.mIsRoomEncrypted);
                menu.findItem(C1299R.C1301id.ic_action_vector_forward).setVisible(true);
                if (Message.MSGTYPE_IMAGE.equals(message.msgtype) || Message.MSGTYPE_VIDEO.equals(message.msgtype) || Message.MSGTYPE_FILE.equals(message.msgtype)) {
                    menu.findItem(C1299R.C1301id.ic_action_vector_save).setVisible(true);
                }
                MenuItem findItem = menu.findItem(C1299R.C1301id.ic_action_vector_report);
                if (!this.mIsPreviewMode && !TextUtils.equals(event.sender, this.mSession.getMyUserId())) {
                    z = true;
                }
                findItem.setVisible(z);
            }
        }
        menu.findItem(C1299R.C1301id.ic_action_device_verification).setVisible(this.mE2eIconByEventId.containsKey(event.eventId));
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener != null) {
                    VectorMessagesAdapter.this.mVectorMessagesAdapterEventsListener.onEventAction(event, str, menuItem.getItemId());
                }
                VectorMessagesAdapter.this.mSelectedEventId = null;
                VectorMessagesAdapter.this.notifyDataSetChanged();
                return true;
            }
        });
        try {
            popupMenu.show();
        } catch (Exception e2) {
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" popup.show failed ");
            sb2.append(e2.getMessage());
            Log.m211e(str3, sb2.toString());
        }
    }

    private boolean addToEventGroupToFront(MessageRow messageRow) {
        MessageRow messageRow2 = null;
        if (supportMessageRowMerge(messageRow)) {
            MessageRow messageRow3 = (getCount() <= 0 || !(((MessageRow) getItem(0)).getEvent() instanceof EventGroup) || !((EventGroup) ((MessageRow) getItem(0)).getEvent()).canAddRow(messageRow)) ? null : (MessageRow) getItem(0);
            if (messageRow3 == null) {
                messageRow3 = new MessageRow(new EventGroup(this.mHiddenEventIds), null);
                this.mEventGroups.add((EventGroup) messageRow3.getEvent());
                super.insert(messageRow3, 0);
                this.mEventRowMap.put(messageRow3.getEvent().eventId, messageRow);
            }
            messageRow2 = messageRow3;
            ((EventGroup) messageRow2.getEvent()).addToFront(messageRow);
            updateHighlightedEventId();
        }
        if (messageRow2 != null) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0027, code lost:
        if (((com.opengarden.firechat.util.EventGroup) r2.getEvent()).canAddRow(r5) != false) goto L_0x003f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addToEventGroup(com.opengarden.firechat.matrixsdk.adapters.MessageRow r5) {
        /*
            r4 = this;
            boolean r0 = r4.supportMessageRowMerge(r5)
            if (r0 == 0) goto L_0x0072
            int r0 = r4.getCount()
            int r0 = r0 + -1
        L_0x000c:
            r1 = 0
            if (r0 < 0) goto L_0x003e
            java.lang.Object r2 = r4.getItem(r0)
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r2 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r2
            com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r2.getEvent()
            boolean r3 = r3 instanceof com.opengarden.firechat.util.EventGroup
            if (r3 == 0) goto L_0x002a
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r2.getEvent()
            com.opengarden.firechat.util.EventGroup r0 = (com.opengarden.firechat.util.EventGroup) r0
            boolean r0 = r0.canAddRow(r5)
            if (r0 == 0) goto L_0x003e
            goto L_0x003f
        L_0x002a:
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r2.getEvent()
            java.lang.String r2 = r2.getType()
            java.lang.String r3 = "m.room.member"
            boolean r2 = android.text.TextUtils.equals(r2, r3)
            if (r2 != 0) goto L_0x003b
            goto L_0x003e
        L_0x003b:
            int r0 = r0 + -1
            goto L_0x000c
        L_0x003e:
            r2 = r1
        L_0x003f:
            if (r2 != 0) goto L_0x0066
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r2 = new com.opengarden.firechat.matrixsdk.adapters.MessageRow
            com.opengarden.firechat.util.EventGroup r0 = new com.opengarden.firechat.util.EventGroup
            java.util.Set<java.lang.String> r3 = r4.mHiddenEventIds
            r0.<init>(r3)
            r2.<init>(r0, r1)
            super.add(r2)
            java.util.List<com.opengarden.firechat.util.EventGroup> r0 = r4.mEventGroups
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r2.getEvent()
            com.opengarden.firechat.util.EventGroup r1 = (com.opengarden.firechat.util.EventGroup) r1
            r0.add(r1)
            java.util.HashMap<java.lang.String, com.opengarden.firechat.matrixsdk.adapters.MessageRow> r0 = r4.mEventRowMap
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r2.getEvent()
            java.lang.String r1 = r1.eventId
            r0.put(r1, r2)
        L_0x0066:
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r2.getEvent()
            com.opengarden.firechat.util.EventGroup r0 = (com.opengarden.firechat.util.EventGroup) r0
            r0.add(r5)
            r4.updateHighlightedEventId()
        L_0x0072:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMessagesAdapter.addToEventGroup(com.opengarden.firechat.matrixsdk.adapters.MessageRow):void");
    }

    private void removeFromEventGroup(MessageRow messageRow) {
        if (supportMessageRowMerge(messageRow)) {
            String str = messageRow.getEvent().eventId;
            for (EventGroup eventGroup : this.mEventGroups) {
                if (eventGroup.contains(str)) {
                    eventGroup.removeByEventId(str);
                    if (eventGroup.isEmpty()) {
                        this.mEventGroups.remove(eventGroup);
                        super.remove(messageRow);
                        updateHighlightedEventId();
                        return;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateHighlightedEventId() {
        if (this.mSearchedEventId != null && !this.mEventGroups.isEmpty() && this.mHiddenEventIds.contains(this.mSearchedEventId)) {
            for (EventGroup eventGroup : this.mEventGroups) {
                if (eventGroup.contains(this.mSearchedEventId)) {
                    this.mHighlightedEventId = eventGroup.eventId;
                    return;
                }
            }
        }
        this.mHighlightedEventId = this.mSearchedEventId;
    }

    private void checkEventGroupsMerge(MessageRow messageRow, int i) {
        if (i > 0 && i < getCount() - 1 && !EventGroup.isSupported(messageRow)) {
            int i2 = i - 1;
            Event event = ((MessageRow) getItem(i2)).getEvent();
            Event event2 = ((MessageRow) getItem(i)).getEvent();
            if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_STATE_ROOM_MEMBER) && (event2 instanceof EventGroup)) {
                EventGroup eventGroup = (EventGroup) event2;
                EventGroup eventGroup2 = null;
                while (true) {
                    if (i2 < 0) {
                        break;
                    } else if (((MessageRow) getItem(i2)).getEvent() instanceof EventGroup) {
                        eventGroup2 = (EventGroup) ((MessageRow) getItem(i2)).getEvent();
                        break;
                    } else {
                        i2--;
                    }
                }
                if (eventGroup2 != null) {
                    ArrayList<MessageRow> arrayList = new ArrayList<>(eventGroup.getRows());
                    if (eventGroup2.canAddRow((MessageRow) arrayList.get(0))) {
                        for (MessageRow add : arrayList) {
                            eventGroup2.add(add);
                        }
                    }
                    MessageRow messageRow2 = (MessageRow) this.mEventRowMap.get(eventGroup.eventId);
                    this.mEventGroups.remove(eventGroup);
                    super.remove(messageRow2);
                    updateHighlightedEventId();
                }
            }
        }
    }
}
