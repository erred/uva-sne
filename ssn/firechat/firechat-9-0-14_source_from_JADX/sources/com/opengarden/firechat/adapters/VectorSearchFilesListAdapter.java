package com.opengarden.firechat.adapters;

import android.content.Context;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.Event;

public class VectorSearchFilesListAdapter extends VectorMessagesAdapter {
    private final boolean mDisplayRoomName;

    /* access modifiers changed from: protected */
    public boolean mergeView(Event event, int i, boolean z) {
        return false;
    }

    public VectorSearchFilesListAdapter(MXSession mXSession, Context context, boolean z, MXMediasCache mXMediasCache) {
        super(mXSession, context, mXMediasCache);
        this.mDisplayRoomName = z;
        setNotifyOnChange(true);
    }

    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r6v1 */
    /* JADX WARNING: type inference failed for: r6v2 */
    /* JADX WARNING: type inference failed for: r3v14 */
    /* JADX WARNING: type inference failed for: r1v21, types: [com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo] */
    /* JADX WARNING: type inference failed for: r3v15 */
    /* JADX WARNING: type inference failed for: r2v25, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v26 */
    /* JADX WARNING: type inference failed for: r3v16 */
    /* JADX WARNING: type inference failed for: r1v26, types: [com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo] */
    /* JADX WARNING: type inference failed for: r3v17 */
    /* JADX WARNING: type inference failed for: r2v28, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v29 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r12, android.view.View r13, android.view.ViewGroup r14) {
        /*
            r11 = this;
            r0 = 0
            if (r13 != 0) goto L_0x000c
            android.view.LayoutInflater r13 = r11.mLayoutInflater
            r1 = 2131427419(0x7f0b005b, float:1.8476454E38)
            android.view.View r13 = r13.inflate(r1, r14, r0)
        L_0x000c:
            com.opengarden.firechat.matrixsdk.MXSession r14 = r11.mSession
            boolean r14 = r14.isAlive()
            if (r14 != 0) goto L_0x0015
            return r13
        L_0x0015:
            java.lang.Object r12 = r11.getItem(r12)
            com.opengarden.firechat.matrixsdk.adapters.MessageRow r12 = (com.opengarden.firechat.matrixsdk.adapters.MessageRow) r12
            com.opengarden.firechat.matrixsdk.rest.model.Event r12 = r12.getEvent()
            com.google.gson.JsonElement r14 = r12.getContent()
            com.opengarden.firechat.matrixsdk.rest.model.message.Message r14 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toMessage(r14)
            java.lang.String r1 = "m.image"
            java.lang.String r2 = r14.msgtype
            boolean r1 = r1.equals(r2)
            r2 = 2131230860(0x7f08008c, float:1.8077785E38)
            r3 = 0
            if (r1 == 0) goto L_0x0073
            com.google.gson.JsonElement r1 = r12.getContent()
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toImageMessage(r1)
            java.lang.String r2 = r1.getThumbnailUrl()
            if (r2 != 0) goto L_0x0047
            java.lang.String r2 = r1.getUrl()
        L_0x0047:
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r4 = r1.info
            if (r4 == 0) goto L_0x0050
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r4 = r1.info
            java.lang.Long r4 = r4.size
            goto L_0x0051
        L_0x0050:
            r4 = r3
        L_0x0051:
            java.lang.String r5 = "image/gif"
            java.lang.String r6 = r1.getMimeType()
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0061
            r5 = 2131230862(0x7f08008e, float:1.8077789E38)
            goto L_0x0064
        L_0x0061:
            r5 = 2131230863(0x7f08008f, float:1.807779E38)
        L_0x0064:
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r6 = r1.info
            if (r6 == 0) goto L_0x006d
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r1 = r1.info
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r1 = r1.thumbnail_file
            r3 = r1
        L_0x006d:
            r6 = r2
            r10 = r3
            r1 = r4
            r2 = r5
            goto L_0x00e0
        L_0x0073:
            java.lang.String r1 = "m.video"
            java.lang.String r4 = r14.msgtype
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00a6
            com.google.gson.JsonElement r1 = r12.getContent()
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toVideoMessage(r1)
            java.lang.String r2 = r1.getThumbnailUrl()
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r4 = r1.info
            if (r4 == 0) goto L_0x0092
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r4 = r1.info
            java.lang.Long r4 = r4.size
            goto L_0x0093
        L_0x0092:
            r4 = r3
        L_0x0093:
            r5 = 2131230864(0x7f080090, float:1.8077793E38)
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r6 = r1.info
            if (r6 == 0) goto L_0x009f
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r1 = r1.info
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r1 = r1.thumbnail_file
            r3 = r1
        L_0x009f:
            r6 = r2
            r10 = r3
            r1 = r4
            r2 = 2131230864(0x7f080090, float:1.8077793E38)
            goto L_0x00e0
        L_0x00a6:
            java.lang.String r1 = "m.file"
            java.lang.String r4 = r14.msgtype
            boolean r1 = r1.equals(r4)
            if (r1 != 0) goto L_0x00bf
            java.lang.String r1 = "m.audio"
            java.lang.String r4 = r14.msgtype
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x00bb
            goto L_0x00bf
        L_0x00bb:
            r1 = r3
            r6 = r1
        L_0x00bd:
            r10 = r6
            goto L_0x00e0
        L_0x00bf:
            com.google.gson.JsonElement r1 = r12.getContent()
            com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage r1 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toFileMessage(r1)
            com.opengarden.firechat.matrixsdk.rest.model.message.FileInfo r4 = r1.info
            if (r4 == 0) goto L_0x00d0
            com.opengarden.firechat.matrixsdk.rest.model.message.FileInfo r1 = r1.info
            java.lang.Long r1 = r1.size
            goto L_0x00d1
        L_0x00d0:
            r1 = r3
        L_0x00d1:
            java.lang.String r4 = "m.audio"
            java.lang.String r5 = r14.msgtype
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x00de
            r2 = 2131230861(0x7f08008d, float:1.8077787E38)
        L_0x00de:
            r6 = r3
            goto L_0x00bd
        L_0x00e0:
            r3 = 2131296492(0x7f0900ec, float:1.8210902E38)
            android.view.View r3 = r13.findViewById(r3)
            r5 = r3
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r5.setImageResource(r2)
            if (r6 == 0) goto L_0x0122
            if (r10 != 0) goto L_0x0110
            android.content.Context r2 = r11.getContext()
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2131165333(0x7f070095, float:1.794488E38)
            int r2 = r2.getDimensionPixelSize(r3)
            com.opengarden.firechat.matrixsdk.MXSession r3 = r11.mSession
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r3 = r3.getMediasCache()
            com.opengarden.firechat.matrixsdk.MXSession r4 = r11.mSession
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r4 = r4.getHomeServerConfig()
            r3.loadAvatarThumbnail(r4, r5, r6, r2)
            goto L_0x0122
        L_0x0110:
            com.opengarden.firechat.matrixsdk.MXSession r2 = r11.mSession
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r3 = r2.getMediasCache()
            com.opengarden.firechat.matrixsdk.MXSession r2 = r11.mSession
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r4 = r2.getHomeServerConfig()
            r7 = 0
            r8 = 0
            r9 = 0
            r3.loadBitmap(r4, r5, r6, r7, r8, r9, r10)
        L_0x0122:
            r2 = 2131296490(0x7f0900ea, float:1.8210898E38)
            android.view.View r2 = r13.findViewById(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            java.lang.String r14 = r14.body
            r2.setText(r14)
            r14 = 2131296491(0x7f0900eb, float:1.82109E38)
            android.view.View r14 = r13.findViewById(r14)
            android.widget.TextView r14 = (android.widget.TextView) r14
            java.lang.String r2 = ""
            boolean r3 = r11.mDisplayRoomName
            if (r3 == 0) goto L_0x0179
            com.opengarden.firechat.matrixsdk.MXSession r3 = r11.mSession
            com.opengarden.firechat.matrixsdk.MXDataHandler r3 = r3.getDataHandler()
            com.opengarden.firechat.matrixsdk.data.store.IMXStore r3 = r3.getStore()
            java.lang.String r4 = r12.roomId
            com.opengarden.firechat.matrixsdk.data.Room r3 = r3.getRoom(r4)
            if (r3 == 0) goto L_0x0179
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            android.content.Context r2 = r11.mContext
            com.opengarden.firechat.matrixsdk.MXSession r5 = r11.mSession
            java.lang.String r2 = com.opengarden.firechat.util.VectorUtils.getRoomDisplayName(r2, r5, r3)
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r2 = " - "
            r3.append(r2)
            java.lang.String r2 = r3.toString()
        L_0x0179:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            android.content.Context r2 = r11.mContext
            long r4 = r12.getOriginServerTs()
            java.lang.String r12 = com.opengarden.firechat.adapters.AdapterUtils.tsToString(r2, r4, r0)
            r3.append(r12)
            java.lang.String r12 = r3.toString()
            r14.setText(r12)
            r12 = 2131296994(0x7f0902e2, float:1.821192E38)
            android.view.View r12 = r13.findViewById(r12)
            android.widget.TextView r12 = (android.widget.TextView) r12
            if (r1 == 0) goto L_0x01b8
            long r2 = r1.longValue()
            r4 = 1
            int r14 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r14 <= 0) goto L_0x01b8
            android.content.Context r14 = r11.mContext
            long r0 = r1.longValue()
            java.lang.String r14 = android.text.format.Formatter.formatFileSize(r14, r0)
            r12.setText(r14)
            goto L_0x01bd
        L_0x01b8:
            java.lang.String r14 = ""
            r12.setText(r14)
        L_0x01bd:
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorSearchFilesListAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }
}
