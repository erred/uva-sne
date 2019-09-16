package com.opengarden.firechat.matrixsdk.util;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.TextUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.interfaces.HtmlToolbox;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.EventContent;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import org.apache.commons.lang3.StringUtils;

public class EventDisplay {
    private static final String LOG_TAG = "EventDisplay";
    private static final String MESSAGE_IN_REPLY_TO_FIRST_PART = "<blockquote>";
    private static final String MESSAGE_IN_REPLY_TO_LAST_PART = "</a>";
    public static final boolean mDisplayRedactedEvents = false;
    protected final Context mContext;
    protected final Event mEvent;
    @Nullable
    protected final HtmlToolbox mHtmlToolbox;
    protected boolean mPrependAuthor;
    protected final RoomState mRoomState;

    public static String getRedactionMessage(Context context, Event event, RoomState roomState) {
        return null;
    }

    public EventDisplay(Context context, Event event, RoomState roomState) {
        this(context, event, roomState, null);
    }

    public EventDisplay(Context context, Event event, RoomState roomState, @Nullable HtmlToolbox htmlToolbox) {
        this.mContext = context.getApplicationContext();
        this.mEvent = event;
        this.mRoomState = roomState;
        this.mHtmlToolbox = htmlToolbox;
    }

    public void setPrependMessagesWithAuthor(boolean z) {
        this.mPrependAuthor = z;
    }

    protected static String getUserDisplayName(String str, RoomState roomState) {
        return roomState != null ? roomState.getMemberName(str) : str;
    }

    public CharSequence getTextualDisplay() {
        return getTextualDisplay(null);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v1, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r0v1 */
    /* JADX WARNING: type inference failed for: r11v2 */
    /* JADX WARNING: type inference failed for: r0v2 */
    /* JADX WARNING: type inference failed for: r11v22, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v28, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v33 */
    /* JADX WARNING: type inference failed for: r11v34, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r11v36, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r4v9, types: [java.lang.Object[]] */
    /* JADX WARNING: type inference failed for: r11v37, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v39, types: [java.lang.CharSequence, java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v40, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v41 */
    /* JADX WARNING: type inference failed for: r11v47, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v48, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v56, types: [android.text.SpannableString] */
    /* JADX WARNING: type inference failed for: r11v64, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r0v3, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r11v69, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v72, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r11v73 */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r0v7, types: [java.lang.CharSequence, java.lang.Object] */
    /* JADX WARNING: type inference failed for: r7v1, types: [java.lang.Object[]] */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r0v10 */
    /* JADX WARNING: type inference failed for: r11v80, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v82, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v84, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r1v42, types: [java.lang.CharSequence] */
    /* JADX WARNING: type inference failed for: r0v13 */
    /* JADX WARNING: type inference failed for: r4v21, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r0v14 */
    /* JADX WARNING: type inference failed for: r11v85, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r11v90, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r0v15 */
    /* JADX WARNING: type inference failed for: r0v16 */
    /* JADX WARNING: type inference failed for: r11v129 */
    /* JADX WARNING: type inference failed for: r11v130 */
    /* JADX WARNING: type inference failed for: r11v131 */
    /* JADX WARNING: type inference failed for: r11v132 */
    /* JADX WARNING: type inference failed for: r11v133 */
    /* JADX WARNING: type inference failed for: r11v134 */
    /* JADX WARNING: type inference failed for: r11v135 */
    /* JADX WARNING: type inference failed for: r11v136 */
    /* JADX WARNING: type inference failed for: r11v137 */
    /* JADX WARNING: type inference failed for: r11v138 */
    /* JADX WARNING: type inference failed for: r11v139 */
    /* JADX WARNING: type inference failed for: r11v140 */
    /* JADX WARNING: type inference failed for: r11v141 */
    /* JADX WARNING: type inference failed for: r11v142 */
    /* JADX WARNING: type inference failed for: r11v143 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v2
      assigns: []
      uses: []
      mth insns count: 379
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 25 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.CharSequence getTextualDisplay(java.lang.Integer r11) {
        /*
            r10 = this;
            r0 = 0
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.google.gson.JsonObject r1 = r1.getContentAsJsonObject()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r2 = r2.getSender()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r2 = getUserDisplayName(r2, r3)     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r3 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r3 = r3.getType()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r4 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            boolean r4 = r4.isCallEvent()     // Catch:{ Exception -> 0x03c3 }
            r5 = 1
            r6 = 0
            if (r4 == 0) goto L_0x00ad
            java.lang.String r11 = "m.call.invite"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0080
            java.lang.String r11 = "offer"
            com.google.gson.JsonElement r11 = r1.get(r11)     // Catch:{ Exception -> 0x0046 }
            com.google.gson.JsonObject r11 = r11.getAsJsonObject()     // Catch:{ Exception -> 0x0046 }
            java.lang.String r1 = "sdp"
            com.google.gson.JsonElement r11 = r11.get(r1)     // Catch:{ Exception -> 0x0046 }
            java.lang.String r11 = r11.getAsString()     // Catch:{ Exception -> 0x0046 }
            java.lang.String r1 = "m=video"
            boolean r11 = r11.contains(r1)     // Catch:{ Exception -> 0x0046 }
            goto L_0x0062
        L_0x0046:
            r11 = move-exception
            java.lang.String r1 = LOG_TAG     // Catch:{ Exception -> 0x03c3 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03c3 }
            r3.<init>()     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r4 = "getTextualDisplay : "
            r3.append(r4)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getMessage()     // Catch:{ Exception -> 0x03c3 }
            r3.append(r11)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r3.toString()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r11)     // Catch:{ Exception -> 0x03c3 }
            r11 = 0
        L_0x0062:
            if (r11 == 0) goto L_0x0072
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689890(0x7f0f01a2, float:1.9008808E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            return r11
        L_0x0072:
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689891(0x7f0f01a3, float:1.900881E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            return r11
        L_0x0080:
            java.lang.String r11 = "m.call.answer"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0096
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689875(0x7f0f0193, float:1.9008778E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            return r11
        L_0x0096:
            java.lang.String r11 = "m.call.hangup"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x00ac
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689885(0x7f0f019d, float:1.9008798E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            return r11
        L_0x00ac:
            return r3
        L_0x00ad:
            java.lang.String r4 = "m.room.history_visibility"
            boolean r4 = r4.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            r7 = 2
            if (r4 == 0) goto L_0x0131
            java.lang.String r11 = "history_visibility"
            com.google.gson.JsonElement r11 = r1.get(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x00c9
            java.lang.String r11 = "history_visibility"
            com.google.gson.JsonElement r11 = r1.get(r11)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getAsString()     // Catch:{ Exception -> 0x03c3 }
            goto L_0x00cb
        L_0x00c9:
            java.lang.String r11 = "shared"
        L_0x00cb:
            java.lang.String r1 = "shared"
            boolean r1 = android.text.TextUtils.equals(r11, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x00dd
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689910(0x7f0f01b6, float:1.9008849E38)
            java.lang.String r11 = r11.getString(r1)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x0120
        L_0x00dd:
            java.lang.String r1 = "invited"
            boolean r1 = android.text.TextUtils.equals(r11, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x00ef
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689908(0x7f0f01b4, float:1.9008845E38)
            java.lang.String r11 = r11.getString(r1)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x0120
        L_0x00ef:
            java.lang.String r1 = "joined"
            boolean r1 = android.text.TextUtils.equals(r11, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x0101
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689909(0x7f0f01b5, float:1.9008847E38)
            java.lang.String r11 = r11.getString(r1)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x0120
        L_0x0101:
            java.lang.String r1 = "world_readable"
            boolean r1 = android.text.TextUtils.equals(r11, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x0113
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689912(0x7f0f01b8, float:1.9008853E38)
            java.lang.String r11 = r11.getString(r1)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x0120
        L_0x0113:
            android.content.Context r1 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r3 = 2131689911(0x7f0f01b7, float:1.900885E38)
            java.lang.Object[] r4 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r4[r6] = r11     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x03c3 }
        L_0x0120:
            android.content.Context r1 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r3 = 2131689889(0x7f0f01a1, float:1.9008806E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r4[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            r4[r5] = r11     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x0131:
            java.lang.String r4 = "m.receipt"
            boolean r4 = r4.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r4 == 0) goto L_0x013d
            java.lang.String r11 = "Read Receipt"
            goto L_0x03df
        L_0x013d:
            java.lang.String r4 = "m.room.message"
            boolean r4 = r4.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            r8 = 33
            if (r4 == 0) goto L_0x0217
            java.lang.String r3 = "msgtype"
            com.google.gson.JsonElement r3 = r1.get(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r3 == 0) goto L_0x015a
            java.lang.String r3 = "msgtype"
            com.google.gson.JsonElement r3 = r1.get(r3)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r3 = r3.getAsString()     // Catch:{ Exception -> 0x03c3 }
            goto L_0x015c
        L_0x015a:
            java.lang.String r3 = ""
        L_0x015c:
            java.lang.String r4 = "body"
            boolean r4 = r1.has(r4)     // Catch:{ Exception -> 0x03c3 }
            if (r4 == 0) goto L_0x016f
            java.lang.String r4 = "body"
            com.google.gson.JsonElement r4 = r1.get(r4)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r4 = r4.getAsString()     // Catch:{ Exception -> 0x03c3 }
            r0 = r4
        L_0x016f:
            java.lang.String r4 = "formatted_body"
            boolean r4 = r1.has(r4)     // Catch:{ Exception -> 0x03c3 }
            if (r4 == 0) goto L_0x0188
            java.lang.String r4 = "format"
            boolean r4 = r1.has(r4)     // Catch:{ Exception -> 0x03c3 }
            if (r4 == 0) goto L_0x0188
            android.content.Context r4 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.interfaces.HtmlToolbox r9 = r10.mHtmlToolbox     // Catch:{ Exception -> 0x03c3 }
            java.lang.CharSequence r1 = r10.getFormattedMessage(r4, r1, r9)     // Catch:{ Exception -> 0x03c3 }
            r0 = r1
        L_0x0188:
            java.lang.String r1 = "m.image"
            boolean r1 = android.text.TextUtils.equals(r3, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x01a6
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x01a6
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131690317(0x7f0f034d, float:1.9009674E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
        L_0x01a3:
            r0 = r11
            goto L_0x03de
        L_0x01a6:
            java.lang.String r1 = "m.emote"
            boolean r1 = android.text.TextUtils.equals(r3, r1)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x01c8
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03c3 }
            r11.<init>()     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r1 = "* "
            r11.append(r1)     // Catch:{ Exception -> 0x03c3 }
            r11.append(r2)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r1 = " "
            r11.append(r1)     // Catch:{ Exception -> 0x03c3 }
            r11.append(r0)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x03c3 }
            goto L_0x01a3
        L_0x01c8:
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x01d1
            java.lang.String r11 = ""
            goto L_0x01a3
        L_0x01d1:
            boolean r1 = r10.mPrependAuthor     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x03de
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x03c3 }
            android.content.Context r3 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r4 = 2131690316(0x7f0f034c, float:1.9009672E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r7[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            r7[r5] = r0     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r3 = r3.getString(r4, r7)     // Catch:{ Exception -> 0x03c3 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0214
            r0 = r1
            android.text.SpannableStringBuilder r0 = (android.text.SpannableStringBuilder) r0     // Catch:{ Exception -> 0x0210 }
            android.text.style.ForegroundColorSpan r3 = new android.text.style.ForegroundColorSpan     // Catch:{ Exception -> 0x0210 }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x0210 }
            r3.<init>(r11)     // Catch:{ Exception -> 0x0210 }
            int r11 = r2.length()     // Catch:{ Exception -> 0x0210 }
            int r11 = r11 + r5
            r0.setSpan(r3, r6, r11, r8)     // Catch:{ Exception -> 0x0210 }
            r11 = r1
            android.text.SpannableStringBuilder r11 = (android.text.SpannableStringBuilder) r11     // Catch:{ Exception -> 0x0210 }
            android.text.style.StyleSpan r0 = new android.text.style.StyleSpan     // Catch:{ Exception -> 0x0210 }
            r0.<init>(r5)     // Catch:{ Exception -> 0x0210 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0210 }
            int r2 = r2 + r5
            r11.setSpan(r0, r6, r2, r8)     // Catch:{ Exception -> 0x0210 }
            goto L_0x0214
        L_0x0210:
            r11 = move-exception
            r0 = r1
            goto L_0x03c4
        L_0x0214:
            r0 = r1
            goto L_0x03de
        L_0x0217:
            java.lang.String r11 = "m.sticker"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0247
            java.lang.String r11 = "body"
            boolean r11 = r1.has(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0232
            java.lang.String r11 = "body"
            com.google.gson.JsonElement r11 = r1.get(r11)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getAsString()     // Catch:{ Exception -> 0x03c3 }
            r0 = r11
        L_0x0232:
            boolean r11 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x03de
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131690318(0x7f0f034e, float:1.9009676E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x0247:
            java.lang.String r11 = "m.room.encryption"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0268
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689884(0x7f0f019c, float:1.9008796E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r2 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.EventContent r2 = r2.getWireEventContent()     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r2 = r2.algorithm     // Catch:{ Exception -> 0x03c3 }
            r3[r5] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x0268:
            java.lang.String r11 = "m.room.encrypted"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x02cd
            com.opengarden.firechat.matrixsdk.rest.model.Event r11 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            boolean r11 = r11.isRedacted()     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x028a
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r2 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = getRedactionMessage(r11, r1, r2)     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x0289
            return r0
        L_0x0289:
            return r11
        L_0x028a:
            com.opengarden.firechat.matrixsdk.rest.model.Event r11 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.crypto.MXCryptoError r11 = r11.getCryptoError()     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x02b3
            com.opengarden.firechat.matrixsdk.rest.model.Event r11 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.crypto.MXCryptoError r11 = r11.getCryptoError()     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r1 = r11.errcode     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r2 = "UNKNOWN_INBOUND_SESSION_ID"
            boolean r1 = android.text.TextUtils.equals(r1, r2)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x02af
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            android.content.res.Resources r11 = r11.getResources()     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689879(0x7f0f0197, float:1.9008786E38)
            r11.getString(r1)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x02b2
        L_0x02af:
            r11.getLocalizedMessage()     // Catch:{ Exception -> 0x03c3 }
        L_0x02b2:
            return r0
        L_0x02b3:
            boolean r11 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x02ba
            return r0
        L_0x02ba:
            android.text.SpannableString r11 = new android.text.SpannableString     // Catch:{ Exception -> 0x03c3 }
            r11.<init>(r0)     // Catch:{ Exception -> 0x03c3 }
            android.text.style.StyleSpan r1 = new android.text.style.StyleSpan     // Catch:{ Exception -> 0x03c3 }
            r1.<init>(r7)     // Catch:{ Exception -> 0x03c3 }
            int r2 = r0.length()     // Catch:{ Exception -> 0x03c3 }
            r11.setSpan(r1, r6, r2, r8)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x02cd:
            java.lang.String r11 = "m.room.topic"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x031e
            java.lang.String r11 = "topic"
            com.google.gson.JsonPrimitive r11 = r1.getAsJsonPrimitive(r11)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getAsString()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = r1.isRedacted()     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x02f8
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = getRedactionMessage(r11, r1, r3)     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x02f8
            return r0
        L_0x02f8:
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 != 0) goto L_0x030f
            android.content.Context r1 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r3 = 2131689914(0x7f0f01ba, float:1.9008857E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r4[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            r4[r5] = r11     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x01a3
        L_0x030f:
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689906(0x7f0f01b2, float:1.900884E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x01a3
        L_0x031e:
            java.lang.String r11 = "m.room.name"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x0373
            java.lang.String r11 = "name"
            com.google.gson.JsonPrimitive r11 = r1.getAsJsonPrimitive(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r11 != 0) goto L_0x0330
            r11 = r0
            goto L_0x0334
        L_0x0330:
            java.lang.String r11 = r11.getAsString()     // Catch:{ Exception -> 0x03c3 }
        L_0x0334:
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = r1.isRedacted()     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x034d
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = getRedactionMessage(r11, r1, r3)     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x034d
            return r0
        L_0x034d:
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 != 0) goto L_0x0364
            android.content.Context r1 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r3 = 2131689901(0x7f0f01ad, float:1.900883E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r4[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            r4[r5] = r11     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x01a3
        L_0x0364:
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r1 = 2131689902(0x7f0f01ae, float:1.9008832E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x03c3 }
            r3[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.getString(r1, r3)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x01a3
        L_0x0373:
            java.lang.String r11 = "m.room.third_party_invite"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x03b0
            com.opengarden.firechat.matrixsdk.rest.model.Event r11 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.google.gson.JsonElement r11 = r11.getContent()     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.pid.RoomThirdPartyInvite r11 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toRoomThirdPartyInvite(r11)     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r11.display_name     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = r1.isRedacted()     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x03a0
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = getRedactionMessage(r11, r1, r3)     // Catch:{ Exception -> 0x03c3 }
            boolean r1 = android.text.TextUtils.isEmpty(r11)     // Catch:{ Exception -> 0x03c3 }
            if (r1 == 0) goto L_0x03a0
            return r0
        L_0x03a0:
            android.content.Context r1 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            r3 = 2131689904(0x7f0f01b0, float:1.9008837E38)
            java.lang.Object[] r4 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x03c3 }
            r4[r6] = r2     // Catch:{ Exception -> 0x03c3 }
            r4[r5] = r11     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x03b0:
            java.lang.String r11 = "m.room.member"
            boolean r11 = r11.equals(r3)     // Catch:{ Exception -> 0x03c3 }
            if (r11 == 0) goto L_0x03de
            android.content.Context r11 = r10.mContext     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r1 = r10.mEvent     // Catch:{ Exception -> 0x03c3 }
            com.opengarden.firechat.matrixsdk.data.RoomState r2 = r10.mRoomState     // Catch:{ Exception -> 0x03c3 }
            java.lang.String r11 = getMembershipNotice(r11, r1, r2)     // Catch:{ Exception -> 0x03c3 }
            goto L_0x03df
        L_0x03c3:
            r11 = move-exception
        L_0x03c4:
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "getTextualDisplay() "
            r2.append(r3)
            java.lang.String r11 = r11.getMessage()
            r2.append(r11)
            java.lang.String r11 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r11)
        L_0x03de:
            r11 = r0
        L_0x03df:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.util.EventDisplay.getTextualDisplay(java.lang.Integer):java.lang.CharSequence");
    }

    protected static String senderDisplayNameForEvent(Event event, EventContent eventContent, EventContent eventContent2, RoomState roomState) {
        String sender = event.getSender();
        if (event.isRedacted()) {
            return sender;
        }
        if (roomState != null) {
            sender = roomState.getMemberName(event.getSender());
        }
        if (eventContent == null || !TextUtils.equals(RoomMember.MEMBERSHIP_JOIN, eventContent.membership)) {
            return sender;
        }
        return (!TextUtils.isEmpty(eventContent.displayname) || (eventContent2 != null && TextUtils.equals(RoomMember.MEMBERSHIP_JOIN, eventContent2.membership) && !TextUtils.isEmpty(eventContent2.displayname))) ? eventContent.displayname : sender;
    }

    public static String getMembershipNotice(Context context, Event event, RoomState roomState) {
        JsonObject contentAsJsonObject = event.getContentAsJsonObject();
        String str = null;
        if (contentAsJsonObject == null || contentAsJsonObject.entrySet().size() == 0) {
            return null;
        }
        EventContent eventContent = JsonUtils.toEventContent(event.getContentAsJsonObject());
        EventContent prevContent = event.getPrevContent();
        String senderDisplayNameForEvent = senderDisplayNameForEvent(event, eventContent, prevContent, roomState);
        String str2 = prevContent != null ? prevContent.membership : null;
        String str3 = prevContent != null ? prevContent.displayname : null;
        String str4 = eventContent.displayname;
        if (str4 == null) {
            str4 = event.stateKey;
            if (!(str4 == null || roomState == null || event.isRedacted())) {
                str4 = roomState.getMemberName(str4);
            }
        }
        if (TextUtils.equals(str2, eventContent.membership)) {
            String redactionMessage = getRedactionMessage(context, event, roomState);
            if (!event.isRedacted()) {
                String str5 = "";
                if (!TextUtils.equals(senderDisplayNameForEvent, str3)) {
                    if (!TextUtils.isEmpty(str3)) {
                        str5 = TextUtils.isEmpty(senderDisplayNameForEvent) ? context.getString(C1299R.string.notice_display_name_removed, new Object[]{event.getSender(), str3}) : context.getString(C1299R.string.notice_display_name_changed_from, new Object[]{event.getSender(), str3, senderDisplayNameForEvent});
                    } else if (!TextUtils.equals(event.getSender(), senderDisplayNameForEvent)) {
                        str5 = context.getString(C1299R.string.notice_display_name_set, new Object[]{event.getSender(), senderDisplayNameForEvent});
                    }
                }
                String str6 = eventContent.avatar_url;
                if (prevContent != null) {
                    str = prevContent.avatar_url;
                }
                if (!TextUtils.equals(str, str6)) {
                    if (!TextUtils.isEmpty(str5)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str5);
                        sb.append(StringUtils.SPACE);
                        sb.append(context.getString(C1299R.string.notice_avatar_changed_too));
                        str5 = sb.toString();
                    } else {
                        str5 = context.getString(C1299R.string.notice_avatar_url_changed, new Object[]{senderDisplayNameForEvent});
                    }
                }
                return str5;
            } else if (redactionMessage == null) {
                return null;
            } else {
                return context.getString(C1299R.string.notice_profile_change_redacted, new Object[]{senderDisplayNameForEvent, redactionMessage});
            }
        } else if ("invite".equals(eventContent.membership)) {
            if (eventContent.third_party_invite != null) {
                return context.getString(C1299R.string.notice_room_third_party_registered_invite, new Object[]{str4, eventContent.third_party_invite.display_name});
            }
            if (!(roomState == null || roomState.getDataHandler() == null)) {
                str = roomState.getDataHandler().getUserId();
            }
            if (TextUtils.equals(event.stateKey, str)) {
                return context.getString(C1299R.string.notice_room_invite_you, new Object[]{senderDisplayNameForEvent});
            } else if (event.stateKey == null) {
                return context.getString(C1299R.string.notice_room_invite_no_invitee, new Object[]{senderDisplayNameForEvent});
            } else if (str4.equals(MXCallsManager.getConferenceUserId(event.roomId))) {
                return context.getString(C1299R.string.notice_requested_voip_conference, new Object[]{senderDisplayNameForEvent});
            } else {
                return context.getString(C1299R.string.notice_room_invite, new Object[]{senderDisplayNameForEvent, str4});
            }
        } else if (!RoomMember.MEMBERSHIP_JOIN.equals(eventContent.membership)) {
            if (RoomMember.MEMBERSHIP_LEAVE.equals(eventContent.membership)) {
                if (TextUtils.equals(event.sender, MXCallsManager.getConferenceUserId(event.roomId))) {
                    return context.getString(C1299R.string.notice_voip_finished);
                }
                if (TextUtils.equals(event.getSender(), event.stateKey)) {
                    if (prevContent == null || !TextUtils.equals(prevContent.membership, "invite")) {
                        if (eventContent.displayname == null && str3 != null) {
                            senderDisplayNameForEvent = str3;
                        }
                        return context.getString(C1299R.string.notice_room_leave, new Object[]{senderDisplayNameForEvent});
                    }
                    return context.getString(C1299R.string.notice_room_reject, new Object[]{senderDisplayNameForEvent});
                } else if (str2 != null) {
                    if (str2.equals("invite")) {
                        return context.getString(C1299R.string.notice_room_withdraw, new Object[]{senderDisplayNameForEvent, str4});
                    } else if (str2.equals(RoomMember.MEMBERSHIP_JOIN)) {
                        return context.getString(C1299R.string.notice_room_kick, new Object[]{senderDisplayNameForEvent, str4});
                    } else if (str2.equals(RoomMember.MEMBERSHIP_BAN)) {
                        return context.getString(C1299R.string.notice_room_unban, new Object[]{senderDisplayNameForEvent, str4});
                    }
                }
            } else if (RoomMember.MEMBERSHIP_BAN.equals(eventContent.membership)) {
                return context.getString(C1299R.string.notice_room_ban, new Object[]{senderDisplayNameForEvent, str4});
            } else if (RoomMember.MEMBERSHIP_KICK.equals(eventContent.membership)) {
                return context.getString(C1299R.string.notice_room_kick, new Object[]{senderDisplayNameForEvent, str4});
            } else {
                String str7 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Unknown membership: ");
                sb2.append(eventContent.membership);
                Log.m211e(str7, sb2.toString());
            }
            return null;
        } else if (TextUtils.equals(event.sender, MXCallsManager.getConferenceUserId(event.roomId))) {
            return context.getString(C1299R.string.notice_voip_started);
        } else {
            return context.getString(C1299R.string.notice_room_join, new Object[]{senderDisplayNameForEvent});
        }
    }

    private CharSequence getFormattedMessage(@NonNull Context context, @NonNull JsonObject jsonObject, @Nullable HtmlToolbox htmlToolbox) {
        TagHandler tagHandler;
        ImageGetter imageGetter = null;
        if (!Message.FORMAT_MATRIX_HTML.equals(jsonObject.getAsJsonPrimitive("format").getAsString())) {
            return null;
        }
        String asString = jsonObject.getAsJsonPrimitive("formatted_body").getAsString();
        if (htmlToolbox != null) {
            asString = htmlToolbox.convert(asString);
        }
        if (jsonObject.has("m.relates_to")) {
            JsonElement jsonElement = jsonObject.get("m.relates_to");
            if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("m.in_reply_to") && asString.startsWith(MESSAGE_IN_REPLY_TO_FIRST_PART)) {
                int indexOf = asString.indexOf(MESSAGE_IN_REPLY_TO_LAST_PART);
                if (indexOf != -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<blockquote><a href=\"#\">");
                    sb.append(context.getString(C1299R.string.message_reply_to_prefix));
                    sb.append(asString.substring(indexOf + MESSAGE_IN_REPLY_TO_LAST_PART.length()));
                    asString = sb.toString();
                }
            }
        }
        if (TextUtils.isEmpty(asString)) {
            return null;
        }
        if (htmlToolbox != null) {
            imageGetter = htmlToolbox.getImageGetter();
            tagHandler = htmlToolbox.getTagHandler(asString);
        } else {
            tagHandler = null;
        }
        if (VERSION.SDK_INT >= 24) {
            return Html.fromHtml(asString, 12, imageGetter, tagHandler);
        }
        return Html.fromHtml(asString, imageGetter, tagHandler);
    }
}
