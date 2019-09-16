package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p000v4.content.ContextCompat;
import android.text.style.BackgroundColorSpan;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class VectorSearchMessagesListAdapter extends VectorMessagesAdapter {
    private static final String LOG_TAG = "VectorSearchMessagesListAdapter";
    private final boolean mDisplayRoomName;
    private String mPattern;

    /* access modifiers changed from: protected */
    public boolean mergeView(Event event, int i, boolean z) {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean supportMessageRowMerge(MessageRow messageRow) {
        return false;
    }

    public VectorSearchMessagesListAdapter(MXSession mXSession, Context context, boolean z, MXMediasCache mXMediasCache) {
        super(mXSession, context, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_room_member, C1299R.layout.adapter_item_vector_message_text_emote_notice, C1299R.layout.adapter_item_vector_message_file, C1299R.layout.adapter_item_vector_message_merge, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_message_emoji, C1299R.layout.adapter_item_vector_message_code, C1299R.layout.adapter_item_vector_message_image_video, C1299R.layout.adapter_item_vector_hidden_message, mXMediasCache);
        setNotifyOnChange(true);
        this.mDisplayRoomName = z;
        this.mBackgroundColorSpan = new BackgroundColorSpan(ContextCompat.getColor(this.mContext, C1299R.color.vector_green_color));
    }

    public void setTextToHighlight(String str) {
        this.mPattern = str;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(24:1|2|(1:4)(1:5)|6|(1:8)|9|(1:11)|12|(1:14)(1:15)|16|(1:18)|19|20|21|22|23|(1:25)(1:26)|27|(1:29)|30|(1:32)(1:33)|34|(1:36)(1:37)|38) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:21:0x00c8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(final int r10, android.view.View r11, android.view.ViewGroup r12) {
        /*
            r9 = this;
            android.view.View r11 = super.getView(r10, r11, r12)
            java.lang.Object r12 = r9.getItem(r10)     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r12 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r12     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.rest.model.Event r0 = r12.getEvent()     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296785(0x7f090211, float:1.8211496E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            r2 = 8
            r1.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296809(0x7f090229, float:1.8211545E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            r1.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296784(0x7f090210, float:1.8211494E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            r1.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296817(0x7f090231, float:1.8211561E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            boolean r3 = r9.mDisplayRoomName     // Catch:{ Throwable -> 0x0171 }
            r4 = 0
            if (r3 == 0) goto L_0x003d
            r3 = 8
            goto L_0x003e
        L_0x003d:
            r3 = 0
        L_0x003e:
            r1.setVisibility(r3)     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296803(0x7f090223, float:1.8211533E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            r1.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.MXSession r1 = r9.mSession     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.MXDataHandler r1 = r1.getDataHandler()     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r1 = r1.getStore()     // Catch:{ Throwable -> 0x0171 }
            java.lang.String r3 = r0.roomId     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.data.Room r1 = r1.getRoom(r3)     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r12.getRoomState()     // Catch:{ Throwable -> 0x0171 }
            if (r3 != 0) goto L_0x0065
            com.opengarden.firechat.matrixsdk.data.RoomState r3 = r1.getLiveState()     // Catch:{ Throwable -> 0x0171 }
        L_0x0065:
            r5 = 2131296810(0x7f09022a, float:1.8211547E38)
            android.view.View r5 = r11.findViewById(r5)     // Catch:{ Throwable -> 0x0171 }
            r6 = 2131296320(0x7f090040, float:1.8210553E38)
            android.view.View r5 = r5.findViewById(r6)     // Catch:{ Throwable -> 0x0171 }
            android.widget.ImageView r5 = (android.widget.ImageView) r5     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r6 = r9.mHelper     // Catch:{ Throwable -> 0x0171 }
            r6.loadMemberAvatar(r5, r12)     // Catch:{ Throwable -> 0x0171 }
            r12 = 2131296814(0x7f09022e, float:1.8211555E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            android.widget.TextView r12 = (android.widget.TextView) r12     // Catch:{ Throwable -> 0x0171 }
            if (r12 == 0) goto L_0x0090
            java.lang.String r5 = r0.getSender()     // Catch:{ Throwable -> 0x0171 }
            java.lang.String r3 = com.opengarden.firechat.adapters.VectorMessagesAdapterHelper.getUserDisplayName(r5, r3)     // Catch:{ Throwable -> 0x0171 }
            r12.setText(r3)     // Catch:{ Throwable -> 0x0171 }
        L_0x0090:
            r12 = 2131296786(0x7f090212, float:1.8211499E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            android.widget.TextView r12 = (android.widget.TextView) r12     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.util.RiotEventDisplay r3 = new com.opengarden.firechat.util.RiotEventDisplay     // Catch:{ Throwable -> 0x0171 }
            android.content.Context r5 = r9.mContext     // Catch:{ Throwable -> 0x0171 }
            if (r1 == 0) goto L_0x00a4
            com.opengarden.firechat.matrixsdk.data.RoomState r6 = r1.getLiveState()     // Catch:{ Throwable -> 0x0171 }
            goto L_0x00a5
        L_0x00a4:
            r6 = 0
        L_0x00a5:
            r3.<init>(r5, r0, r6)     // Catch:{ Throwable -> 0x0171 }
            java.lang.CharSequence r3 = r3.getTextualDisplay()     // Catch:{ Throwable -> 0x0171 }
            if (r3 != 0) goto L_0x00b0
            java.lang.String r3 = ""
        L_0x00b0:
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r5 = r9.mHelper     // Catch:{ Exception -> 0x00c8 }
            android.text.SpannableString r6 = new android.text.SpannableString     // Catch:{ Exception -> 0x00c8 }
            r6.<init>(r3)     // Catch:{ Exception -> 0x00c8 }
            java.lang.String r7 = r9.mPattern     // Catch:{ Exception -> 0x00c8 }
            android.text.style.BackgroundColorSpan r8 = r9.mBackgroundColorSpan     // Catch:{ Exception -> 0x00c8 }
            java.lang.CharSequence r5 = r5.highlightPattern(r6, r7, r8, r4)     // Catch:{ Exception -> 0x00c8 }
            r12.setText(r5)     // Catch:{ Exception -> 0x00c8 }
            com.opengarden.firechat.adapters.VectorMessagesAdapterHelper r5 = r9.mHelper     // Catch:{ Exception -> 0x00c8 }
            r5.applyLinkMovementMethod(r12)     // Catch:{ Exception -> 0x00c8 }
            goto L_0x00cf
        L_0x00c8:
            java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x0171 }
            r12.setText(r3)     // Catch:{ Throwable -> 0x0171 }
        L_0x00cf:
            r12 = 2131296816(0x7f090230, float:1.821156E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            android.widget.TextView r12 = (android.widget.TextView) r12     // Catch:{ Throwable -> 0x0171 }
            android.content.Context r3 = r9.mContext     // Catch:{ Throwable -> 0x0171 }
            long r5 = r0.getOriginServerTs()     // Catch:{ Throwable -> 0x0171 }
            r0 = 1
            java.lang.String r0 = com.opengarden.firechat.adapters.AdapterUtils.tsToString(r3, r5, r0)     // Catch:{ Throwable -> 0x0171 }
            r12.setText(r0)     // Catch:{ Throwable -> 0x0171 }
            r12 = 2131296807(0x7f090227, float:1.8211541E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            boolean r0 = r9.mDisplayRoomName     // Catch:{ Throwable -> 0x0171 }
            if (r0 == 0) goto L_0x00f3
            r0 = 0
            goto L_0x00f5
        L_0x00f3:
            r0 = 8
        L_0x00f5:
            r12.setVisibility(r0)     // Catch:{ Throwable -> 0x0171 }
            boolean r12 = r9.mDisplayRoomName     // Catch:{ Throwable -> 0x0171 }
            if (r12 == 0) goto L_0x0110
            r12 = 2131296808(0x7f090228, float:1.8211543E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            android.widget.TextView r12 = (android.widget.TextView) r12     // Catch:{ Throwable -> 0x0171 }
            android.content.Context r0 = r9.mContext     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.matrixsdk.MXSession r3 = r9.mSession     // Catch:{ Throwable -> 0x0171 }
            java.lang.String r0 = com.opengarden.firechat.util.VectorUtils.getRoomDisplayName(r0, r3, r1)     // Catch:{ Throwable -> 0x0171 }
            r12.setText(r0)     // Catch:{ Throwable -> 0x0171 }
        L_0x0110:
            r12 = 2131296812(0x7f09022c, float:1.8211551E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            java.lang.String r0 = r9.headerMessage(r10)     // Catch:{ Throwable -> 0x0171 }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Throwable -> 0x0171 }
            if (r1 != 0) goto L_0x0145
            r12.setVisibility(r4)     // Catch:{ Throwable -> 0x0171 }
            r1 = 2131296805(0x7f090225, float:1.8211537E38)
            android.view.View r1 = r11.findViewById(r1)     // Catch:{ Throwable -> 0x0171 }
            android.widget.TextView r1 = (android.widget.TextView) r1     // Catch:{ Throwable -> 0x0171 }
            r1.setText(r0)     // Catch:{ Throwable -> 0x0171 }
            r0 = 2131296806(0x7f090226, float:1.821154E38)
            android.view.View r0 = r12.findViewById(r0)     // Catch:{ Throwable -> 0x0171 }
            r0.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            r0 = 2131296804(0x7f090224, float:1.8211535E38)
            android.view.View r12 = r12.findViewById(r0)     // Catch:{ Throwable -> 0x0171 }
            r12.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            goto L_0x0148
        L_0x0145:
            r12.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
        L_0x0148:
            r12 = 2131296813(0x7f09022d, float:1.8211553E38)
            android.view.View r12 = r11.findViewById(r12)     // Catch:{ Throwable -> 0x0171 }
            int r0 = r10 + 1
            java.lang.String r0 = r9.headerMessage(r0)     // Catch:{ Throwable -> 0x0171 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Throwable -> 0x0171 }
            if (r0 != 0) goto L_0x015c
            goto L_0x015d
        L_0x015c:
            r2 = 0
        L_0x015d:
            r12.setVisibility(r2)     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter$1 r12 = new com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter$1     // Catch:{ Throwable -> 0x0171 }
            r12.<init>(r10)     // Catch:{ Throwable -> 0x0171 }
            r11.setOnClickListener(r12)     // Catch:{ Throwable -> 0x0171 }
            com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter$2 r12 = new com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter$2     // Catch:{ Throwable -> 0x0171 }
            r12.<init>(r10)     // Catch:{ Throwable -> 0x0171 }
            r11.setOnLongClickListener(r12)     // Catch:{ Throwable -> 0x0171 }
            goto L_0x018c
        L_0x0171:
            r10 = move-exception
            java.lang.String r12 = LOG_TAG
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "## getView() failed "
            r0.append(r1)
            java.lang.String r10 = r10.getMessage()
            r0.append(r10)
            java.lang.String r10 = r0.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r10)
        L_0x018c:
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorSearchMessagesListAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }
}
