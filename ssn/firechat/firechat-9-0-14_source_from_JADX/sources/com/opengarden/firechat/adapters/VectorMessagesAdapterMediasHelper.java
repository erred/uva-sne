package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonElement;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.listeners.IMessagesAdapterActionsListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener.DownloadStats;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaUploadListener.UploadStats;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.HashMap;
import java.util.Map;

class VectorMessagesAdapterMediasHelper {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMessagesAdapterMediasHelper";
    /* access modifiers changed from: private */
    public final Context mContext;
    private final int mDefaultMessageTextColor;
    private final int mMaxImageHeight;
    private final int mMaxImageWidth;
    private final MXMediasCache mMediasCache;
    private final int mNotSentMessageTextColor;
    private final MXSession mSession;
    private Map<String, String> mUrlByBitmapIndex = new HashMap();
    /* access modifiers changed from: private */
    public IMessagesAdapterActionsListener mVectorMessagesAdapterEventsListener;

    VectorMessagesAdapterMediasHelper(Context context, MXSession mXSession, int i, int i2, int i3, int i4) {
        this.mContext = context;
        this.mSession = mXSession;
        this.mMaxImageWidth = i;
        this.mMaxImageHeight = i2;
        this.mMediasCache = this.mSession.getMediasCache();
        this.mNotSentMessageTextColor = i3;
        this.mDefaultMessageTextColor = i4;
    }

    /* access modifiers changed from: 0000 */
    public void setVectorMessagesAdapterActionsListener(IMessagesAdapterActionsListener iMessagesAdapterActionsListener) {
        this.mVectorMessagesAdapterEventsListener = iMessagesAdapterActionsListener;
    }

    /* access modifiers changed from: 0000 */
    public void managePendingUpload(View view, Event event, int i, String str) {
        View view2 = view;
        int i2 = i;
        String str2 = str;
        View findViewById = view2.findViewById(C1299R.C1301id.content_upload_progress_layout);
        ProgressBar progressBar = (ProgressBar) view2.findViewById(C1299R.C1301id.upload_event_spinner);
        if (findViewById != null && progressBar != null) {
            findViewById.setTag(str2);
            if (!this.mSession.getMyUserId().equals(event.getSender()) || !event.isSending()) {
                Event event2 = event;
                findViewById.setVisibility(8);
                progressBar.setVisibility(8);
                showUploadFailure(view2, i2, event.isUndeliverable());
                return;
            }
            UploadStats statsForUploadId = this.mSession.getMediasCache().getStatsForUploadId(str2);
            if (statsForUploadId != null) {
                MXMediasCache mediasCache = this.mSession.getMediasCache();
                final View view3 = findViewById;
                final Event event3 = event;
                final View view4 = view2;
                final int i3 = i2;
                C18081 r13 = r0;
                final ProgressBar progressBar2 = progressBar;
                C18081 r0 = new MXMediaUploadListener() {
                    public void onUploadProgress(String str, UploadStats uploadStats) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            VectorMessagesAdapterMediasHelper.this.refreshUploadViews(event3, uploadStats, view3);
                        }
                    }

                    private void onUploadStop(String str) {
                        if (!TextUtils.isEmpty(str)) {
                            Toast.makeText(VectorMessagesAdapterMediasHelper.this.mContext, str, 1).show();
                        }
                        VectorMessagesAdapterMediasHelper.this.showUploadFailure(view4, i3, true);
                        view3.setVisibility(8);
                        progressBar2.setVisibility(8);
                    }

                    public void onUploadCancel(String str) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            onUploadStop(null);
                        }
                    }

                    public void onUploadError(String str, int i, String str2) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            onUploadStop(str2);
                        }
                    }

                    public void onUploadComplete(String str, String str2) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            progressBar2.setVisibility(8);
                        }
                    }
                };
                mediasCache.addUploadListener(str2, r13);
            }
            int i4 = 0;
            showUploadFailure(view2, i2, false);
            if (statsForUploadId != null) {
                i4 = 8;
            }
            progressBar.setVisibility(i4);
            refreshUploadViews(event, statsForUploadId, findViewById);
        }
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0105, code lost:
        if (android.text.TextUtils.equals(r5.toString(), (java.lang.CharSequence) r0.mUrlByBitmapIndex.get(r7)) == false) goto L_0x0107;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void managePendingImageVideoDownload(android.view.View r27, com.opengarden.firechat.matrixsdk.rest.model.Event r28, com.opengarden.firechat.matrixsdk.rest.model.message.Message r29, int r30) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            r2 = r28
            r3 = r29
            int r14 = r0.mMaxImageWidth
            int r15 = r0.mMaxImageHeight
            boolean r13 = r3 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage
            r12 = 0
            r16 = -1
            r4 = 0
            if (r13 == 0) goto L_0x0076
            r6 = r3
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage r6 = (com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage) r6
            r6.checkMediaUrls()
            java.lang.String r7 = r6.getThumbnailUrl()
            if (r7 == 0) goto L_0x002f
            java.lang.String r7 = r6.getThumbnailUrl()
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r8 = r6.info
            if (r8 == 0) goto L_0x002d
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r8 = r6.info
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r8 = r8.thumbnail_file
            goto L_0x003e
        L_0x002d:
            r8 = r4
            goto L_0x003e
        L_0x002f:
            java.lang.String r7 = r6.getUrl()
            if (r7 == 0) goto L_0x003c
            java.lang.String r7 = r6.getUrl()
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r8 = r6.file
            goto L_0x003e
        L_0x003c:
            r7 = r4
            r8 = r7
        L_0x003e:
            int r9 = r6.getRotation()
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageInfo r6 = r6.info
            if (r6 == 0) goto L_0x006a
            java.lang.Integer r10 = r6.f136w
            if (r10 == 0) goto L_0x005b
            java.lang.Integer r10 = r6.f135h
            if (r10 == 0) goto L_0x005b
            java.lang.Integer r10 = r6.f136w
            int r10 = r10.intValue()
            java.lang.Integer r11 = r6.f135h
            int r11 = r11.intValue()
            goto L_0x005d
        L_0x005b:
            r10 = -1
            r11 = -1
        L_0x005d:
            java.lang.Integer r5 = r6.orientation
            if (r5 == 0) goto L_0x0068
            java.lang.Integer r5 = r6.orientation
            int r5 = r5.intValue()
            goto L_0x006d
        L_0x0068:
            r5 = 1
            goto L_0x006d
        L_0x006a:
            r5 = 1
            r10 = -1
            r11 = -1
        L_0x006d:
            r17 = r8
            r18 = r10
            r19 = r11
            r10 = r5
            r11 = r9
            goto L_0x00d8
        L_0x0076:
            boolean r5 = r3 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage
            if (r5 == 0) goto L_0x00cf
            r5 = r3
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage r5 = (com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage) r5
            r5.checkMediaUrls()
            java.lang.String r6 = r5.getThumbnailUrl()
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r7 = r5.info
            if (r7 == 0) goto L_0x008d
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r7 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r7 = r7.thumbnail_file
            goto L_0x008e
        L_0x008d:
            r7 = r4
        L_0x008e:
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r8 = r5.info
            if (r8 == 0) goto L_0x00c5
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r8 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo r8 = r8.thumbnail_info
            if (r8 == 0) goto L_0x00c5
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r8 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo r8 = r8.thumbnail_info
            java.lang.Integer r8 = r8.f138w
            if (r8 == 0) goto L_0x00c5
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r8 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo r8 = r8.thumbnail_info
            java.lang.Integer r8 = r8.f137h
            if (r8 == 0) goto L_0x00c5
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r8 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo r8 = r8.thumbnail_info
            java.lang.Integer r8 = r8.f138w
            int r8 = r8.intValue()
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r5 = r5.info
            com.opengarden.firechat.matrixsdk.rest.model.message.ThumbnailInfo r5 = r5.thumbnail_info
            java.lang.Integer r5 = r5.f137h
            int r5 = r5.intValue()
            r19 = r5
            r17 = r7
            r18 = r8
            r10 = 1
            r11 = 0
            goto L_0x00cd
        L_0x00c5:
            r17 = r7
            r10 = 1
            r11 = 0
            r18 = -1
            r19 = -1
        L_0x00cd:
            r7 = r6
            goto L_0x00d8
        L_0x00cf:
            r7 = r4
            r17 = r7
            r10 = 1
            r11 = 0
            r18 = -1
            r19 = -1
        L_0x00d8:
            r5 = 2131296795(0x7f09021b, float:1.8211517E38)
            android.view.View r5 = r1.findViewById(r5)
            r9 = r5
            android.widget.ImageView r9 = (android.widget.ImageView) r9
            if (r7 == 0) goto L_0x0107
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r6 = r9.hashCode()
            r5.append(r6)
            java.lang.String r6 = ""
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.util.Map<java.lang.String, java.lang.String> r6 = r0.mUrlByBitmapIndex
            java.lang.Object r6 = r6.get(r7)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            boolean r5 = android.text.TextUtils.equals(r5, r6)
            if (r5 != 0) goto L_0x0126
        L_0x0107:
            r9.setImageBitmap(r4)
            if (r7 == 0) goto L_0x0126
            java.util.Map<java.lang.String, java.lang.String> r5 = r0.mUrlByBitmapIndex
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r8 = r9.hashCode()
            r6.append(r8)
            java.lang.String r8 = ""
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            r5.put(r7, r6)
        L_0x0126:
            r5 = 2131296796(0x7f09021c, float:1.8211519E38)
            android.view.View r5 = r1.findViewById(r5)
            android.widget.RelativeLayout r5 = (android.widget.RelativeLayout) r5
            android.view.ViewGroup$LayoutParams r5 = r5.getLayoutParams()
            r8 = r5
            android.widget.FrameLayout$LayoutParams r8 = (android.widget.FrameLayout.LayoutParams) r8
            java.lang.String r5 = r28.getType()
            java.lang.String r6 = "m.sticker"
            boolean r5 = r5.equals(r6)
            if (r5 != 0) goto L_0x0166
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r4 = r0.mMediasCache
            com.opengarden.firechat.matrixsdk.MXSession r5 = r0.mSession
            com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig r5 = r5.getHomeServerConfig()
            r20 = 0
            java.lang.String r21 = "image/jpeg"
            r6 = r9
            r22 = r8
            r8 = r14
            r1 = r9
            r9 = r15
            r23 = r10
            r10 = r11
            r24 = r11
            r11 = r20
            r12 = r21
            r20 = r13
            r13 = r17
            java.lang.String r4 = r4.loadBitmap(r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x016f
        L_0x0166:
            r22 = r8
            r1 = r9
            r23 = r10
            r24 = r11
            r20 = r13
        L_0x016f:
            if (r4 != 0) goto L_0x01ac
            boolean r5 = r3 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage
            if (r5 == 0) goto L_0x0183
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r4 = r0.mMediasCache
            r5 = r3
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage r5 = (com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage) r5
            java.lang.String r5 = r5.getUrl()
            java.lang.String r4 = r4.downloadIdFromUrl(r5)
            goto L_0x01ac
        L_0x0183:
            if (r20 == 0) goto L_0x01ac
            java.lang.String r4 = r28.getType()
            java.lang.String r5 = "m.sticker"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x019f
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r4 = r0.mMediasCache
            r5 = r3
            com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage r5 = (com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage) r5
            java.lang.String r5 = r5.getUrl()
            java.lang.String r4 = r4.downloadIdFromUrl(r5)
            goto L_0x01ac
        L_0x019f:
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r4 = r0.mMediasCache
            r5 = r3
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage r5 = (com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage) r5
            java.lang.String r5 = r5.getUrl()
            java.lang.String r4 = r4.downloadIdFromUrl(r5)
        L_0x01ac:
            java.lang.String r5 = r28.getType()
            java.lang.String r6 = "m.sticker"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x01f3
            com.opengarden.firechat.matrixsdk.MXSession r5 = r0.mSession
            com.opengarden.firechat.matrixsdk.util.ContentManager r5 = r5.getContentManager()
            com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage r3 = (com.opengarden.firechat.matrixsdk.rest.model.message.StickerMessage) r3
            java.lang.String r3 = r3.getUrl()
            java.lang.String r3 = r5.getDownloadableUrl(r3)
            r5 = 2131230984(0x7f080108, float:1.8078036E38)
            if (r3 == 0) goto L_0x01f0
            android.content.Context r6 = r0.mContext
            com.bumptech.glide.RequestManager r6 = com.bumptech.glide.Glide.with(r6)
            com.bumptech.glide.RequestBuilder r3 = r6.load(r3)
            com.bumptech.glide.request.RequestOptions r6 = new com.bumptech.glide.request.RequestOptions
            r6.<init>()
            com.bumptech.glide.request.RequestOptions r6 = r6.override(r14, r15)
            com.bumptech.glide.request.RequestOptions r6 = r6.fitCenter()
            com.bumptech.glide.request.RequestOptions r5 = r6.placeholder(r5)
            com.bumptech.glide.RequestBuilder r3 = r3.apply(r5)
            r3.into(r1)
            goto L_0x01f3
        L_0x01f0:
            r1.setImageResource(r5)
        L_0x01f3:
            r3 = 2131296404(0x7f090094, float:1.8210724E38)
            r5 = r1
            r1 = r27
            android.view.View r1 = r1.findViewById(r3)
            if (r1 != 0) goto L_0x0200
            return
        L_0x0200:
            r1.setTag(r4)
            r3 = 8
            if (r18 <= 0) goto L_0x022f
            if (r19 <= 0) goto L_0x022f
            r6 = 90
            r12 = r24
            if (r12 == r6) goto L_0x0222
            r6 = 270(0x10e, float:3.78E-43)
            if (r12 != r6) goto L_0x0214
            goto L_0x0222
        L_0x0214:
            r6 = 6
            r7 = r23
            if (r7 == r6) goto L_0x0222
            if (r7 != r3) goto L_0x021c
            goto L_0x0222
        L_0x021c:
            r25 = r19
            r19 = r18
            r18 = r25
        L_0x0222:
            int r14 = r14 * r18
            int r14 = r14 / r19
            int r16 = java.lang.Math.min(r14, r15)
            int r19 = r19 * r16
            int r6 = r19 / r18
            goto L_0x0230
        L_0x022f:
            r6 = -1
        L_0x0230:
            if (r16 >= 0) goto L_0x0235
            int r7 = r0.mMaxImageHeight
            goto L_0x0237
        L_0x0235:
            r7 = r16
        L_0x0237:
            if (r6 >= 0) goto L_0x023b
            int r6 = r0.mMaxImageWidth
        L_0x023b:
            r8 = r6
            r6 = r22
            r6.height = r7
            r6.width = r8
            if (r4 == 0) goto L_0x025e
            r6 = 0
            r1.setVisibility(r6)
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r3 = r0.mMediasCache
            com.opengarden.firechat.adapters.VectorMessagesAdapterMediasHelper$2 r7 = new com.opengarden.firechat.adapters.VectorMessagesAdapterMediasHelper$2
            r8 = r30
            r7.<init>(r1, r2, r8)
            r3.addDownloadListener(r4, r7)
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r3 = r0.mMediasCache
            com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener$DownloadStats r3 = r3.getStatsForDownloadId(r4)
            r0.refreshDownloadViews(r2, r3, r1)
            goto L_0x0262
        L_0x025e:
            r6 = 0
            r1.setVisibility(r3)
        L_0x0262:
            r5.setBackgroundColor(r6)
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER_CROP
            r5.setScaleType(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMessagesAdapterMediasHelper.managePendingImageVideoDownload(android.view.View, com.opengarden.firechat.matrixsdk.rest.model.Event, com.opengarden.firechat.matrixsdk.rest.model.message.Message, int):void");
    }

    /* access modifiers changed from: 0000 */
    public void managePendingImageVideoUpload(View view, Event event, Message message) {
        String thumbnailUrl;
        boolean isThumbnailLocalContent;
        boolean z;
        int i;
        String str;
        String str2;
        boolean z2;
        String url;
        View view2 = view;
        Message message2 = message;
        View findViewById = view2.findViewById(C1299R.C1301id.content_upload_progress_layout);
        ProgressBar progressBar = (ProgressBar) view2.findViewById(C1299R.C1301id.upload_event_spinner);
        boolean z3 = message2 instanceof VideoMessage;
        if (findViewById != null && progressBar != null) {
            findViewById.setTag(null);
            boolean z4 = (z3 ? ((VideoMessage) message2).info : ((ImageMessage) message2).info) != null;
            if (!this.mSession.getMyUserId().equals(event.getSender()) || event.isUndeliverable() || !z4) {
                Event event2 = event;
                findViewById.setVisibility(8);
                progressBar.setVisibility(8);
                showUploadFailure(view2, z3 ? 5 : 1, event.isUndeliverable());
                return;
            }
            if (z3) {
                VideoMessage videoMessage = (VideoMessage) message2;
                thumbnailUrl = videoMessage.getThumbnailUrl();
                isThumbnailLocalContent = videoMessage.isThumbnailLocalContent();
            } else {
                ImageMessage imageMessage = (ImageMessage) message2;
                thumbnailUrl = imageMessage.getThumbnailUrl();
                isThumbnailLocalContent = imageMessage.isThumbnailLocalContent();
            }
            boolean z5 = isThumbnailLocalContent;
            if (z5) {
                i = this.mSession.getMediasCache().getProgressValueForUploadId(thumbnailUrl);
                str = thumbnailUrl;
                z = false;
            } else {
                if (z3) {
                    VideoMessage videoMessage2 = (VideoMessage) message2;
                    url = videoMessage2.getUrl();
                    z2 = videoMessage2.isLocalContent();
                } else if (message2 instanceof ImageMessage) {
                    ImageMessage imageMessage2 = (ImageMessage) message2;
                    url = imageMessage2.getUrl();
                    z2 = imageMessage2.isLocalContent();
                } else {
                    z2 = false;
                    z = z2;
                    i = this.mSession.getMediasCache().getProgressValueForUploadId(thumbnailUrl);
                    str = thumbnailUrl;
                }
                thumbnailUrl = url;
                z = z2;
                i = this.mSession.getMediasCache().getProgressValueForUploadId(thumbnailUrl);
                str = thumbnailUrl;
            }
            if (i >= 0) {
                findViewById.setTag(str);
                final View view3 = findViewById;
                final Event event3 = event;
                C18103 r13 = r0;
                final boolean z6 = z5;
                MXMediasCache mediasCache = this.mSession.getMediasCache();
                final View view4 = view2;
                str2 = str;
                final boolean z7 = z3;
                final ProgressBar progressBar2 = progressBar;
                C18103 r0 = new MXMediaUploadListener() {
                    public void onUploadProgress(String str, UploadStats uploadStats) {
                        int i;
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            VectorMessagesAdapterMediasHelper.this.refreshUploadViews(event3, uploadStats, view3);
                            if (!z6) {
                                i = ((uploadStats.mProgress * 90) / 100) + 10;
                            } else {
                                i = (uploadStats.mProgress * 10) / 100;
                            }
                            VectorMessagesAdapterMediasHelper.updateUploadProgress(view3, i);
                        }
                    }

                    private void onUploadStop(String str) {
                        if (!TextUtils.isEmpty(str)) {
                            Toast.makeText(VectorMessagesAdapterMediasHelper.this.mContext, str, 1).show();
                        }
                        VectorMessagesAdapterMediasHelper.this.showUploadFailure(view4, z7 ? 5 : 1, true);
                        view3.setVisibility(8);
                        progressBar2.setVisibility(8);
                    }

                    public void onUploadCancel(String str) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            onUploadStop(null);
                        }
                    }

                    public void onUploadError(String str, int i, String str2) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            onUploadStop(str2);
                        }
                    }

                    public void onUploadComplete(String str, String str2) {
                        if (TextUtils.equals((String) view3.getTag(), str)) {
                            progressBar2.setVisibility(8);
                        }
                    }
                };
                mediasCache.addUploadListener(str2, r13);
            } else {
                str2 = str;
            }
            int i2 = 0;
            showUploadFailure(view2, z3 ? 5 : 1, false);
            progressBar.setVisibility((i >= 0 || !event.isSending()) ? 8 : 0);
            refreshUploadViews(event, this.mSession.getMediasCache().getStatsForUploadId(str2), findViewById);
            if (z) {
                i = ((i * 90) / 100) + 10;
            } else if (z5) {
                i = (i * 10) / 100;
            }
            int i3 = i;
            updateUploadProgress(findViewById, i3);
            if (i3 < 0 || !event.isSending()) {
                i2 = 8;
            }
            findViewById.setVisibility(i2);
        }
    }

    /* access modifiers changed from: private */
    public static void updateUploadProgress(View view, int i) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(C1299R.C1301id.media_progress_view);
        if (progressBar != null) {
            progressBar.setProgress(i);
        }
    }

    /* access modifiers changed from: private */
    public void refreshUploadViews(final Event event, UploadStats uploadStats, View view) {
        if (uploadStats != null) {
            view.setVisibility(0);
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.media_progress_text_view);
            ProgressBar progressBar = (ProgressBar) view.findViewById(C1299R.C1301id.media_progress_view);
            if (textView != null) {
                textView.setText(formatUploadStats(this.mContext, uploadStats));
            }
            if (progressBar != null) {
                progressBar.setProgress(uploadStats.mProgress);
            }
            final View findViewById = view.findViewById(C1299R.C1301id.media_progress_cancel);
            if (findViewById != null) {
                findViewById.setTag(event);
                findViewById.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (event == findViewById.getTag() && VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener != null) {
                            VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener.onEventAction(event, "", C1299R.C1301id.ic_action_vector_cancel_upload);
                        }
                    }
                });
                return;
            }
            return;
        }
        view.setVisibility(8);
    }

    /* access modifiers changed from: 0000 */
    public void managePendingFileDownload(View view, final Event event, FileMessage fileMessage, final int i) {
        String downloadIdFromUrl = this.mMediasCache.downloadIdFromUrl(fileMessage.getUrl());
        final View findViewById = view.findViewById(C1299R.C1301id.content_download_progress_layout);
        if (findViewById != null) {
            findViewById.setTag(downloadIdFromUrl);
            if (downloadIdFromUrl != null) {
                findViewById.setVisibility(0);
                this.mMediasCache.addDownloadListener(downloadIdFromUrl, new MXMediaDownloadListener() {
                    public void onDownloadCancel(String str) {
                        if (TextUtils.equals(str, (String) findViewById.getTag())) {
                            findViewById.setVisibility(8);
                        }
                    }

                    public void onDownloadError(String str, JsonElement jsonElement) {
                        if (TextUtils.equals(str, (String) findViewById.getTag())) {
                            MatrixError matrixError = null;
                            try {
                                matrixError = JsonUtils.toMatrixError(jsonElement);
                            } catch (Exception e) {
                                String access$300 = VectorMessagesAdapterMediasHelper.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("Cannot cast to Matrix error ");
                                sb.append(e.getLocalizedMessage());
                                Log.m211e(access$300, sb.toString());
                            }
                            findViewById.setVisibility(8);
                            if (matrixError != null && matrixError.isSupportedErrorCode()) {
                                Toast.makeText(VectorMessagesAdapterMediasHelper.this.mContext, matrixError.getLocalizedMessage(), 1).show();
                            } else if (jsonElement != null) {
                                Toast.makeText(VectorMessagesAdapterMediasHelper.this.mContext, jsonElement.toString(), 1).show();
                            }
                        }
                    }

                    public void onDownloadProgress(String str, DownloadStats downloadStats) {
                        if (TextUtils.equals(str, (String) findViewById.getTag())) {
                            VectorMessagesAdapterMediasHelper.this.refreshDownloadViews(event, downloadStats, findViewById);
                        }
                    }

                    public void onDownloadComplete(String str) {
                        if (TextUtils.equals(str, (String) findViewById.getTag())) {
                            findViewById.setVisibility(8);
                            if (VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener != null) {
                                VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener.onMediaDownloaded(i);
                            }
                        }
                    }
                });
                refreshDownloadViews(event, this.mMediasCache.getStatsForDownloadId(downloadIdFromUrl), findViewById);
            } else {
                findViewById.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public void showUploadFailure(View view, int i, boolean z) {
        if (4 == i) {
            TextView textView = (TextView) view.findViewById(C1299R.C1301id.messagesAdapter_filename);
            if (textView != null) {
                textView.setTextColor(z ? this.mNotSentMessageTextColor : this.mDefaultMessageTextColor);
            }
        } else if (1 == i || 5 == i) {
            View findViewById = view.findViewById(C1299R.C1301id.media_upload_failed);
            if (findViewById != null) {
                findViewById.setVisibility(z ? 0 : 8);
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshDownloadViews(final Event event, DownloadStats downloadStats, View view) {
        if (downloadStats == null || !isMediaDownloading(event)) {
            view.setVisibility(8);
            return;
        }
        view.setVisibility(0);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.media_progress_text_view);
        ProgressBar progressBar = (ProgressBar) view.findViewById(C1299R.C1301id.media_progress_view);
        if (textView != null) {
            textView.setText(formatDownloadStats(this.mContext, downloadStats));
        }
        if (progressBar != null) {
            progressBar.setProgress(downloadStats.mProgress);
        }
        final View findViewById = view.findViewById(C1299R.C1301id.media_progress_cancel);
        if (findViewById != null) {
            findViewById.setTag(event);
            findViewById.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (event == findViewById.getTag() && VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener != null) {
                        VectorMessagesAdapterMediasHelper.this.mVectorMessagesAdapterEventsListener.onEventAction(event, "", C1299R.C1301id.ic_action_vector_cancel_download);
                    }
                }
            });
        }
    }

    private boolean isMediaDownloading(Event event) {
        boolean z = false;
        if (TextUtils.equals(event.getType(), Event.EVENT_TYPE_MESSAGE)) {
            Message message = JsonUtils.toMessage(event.getContent());
            String str = null;
            if (TextUtils.equals(message.msgtype, Message.MSGTYPE_IMAGE)) {
                str = JsonUtils.toImageMessage(event.getContent()).getUrl();
            } else if (TextUtils.equals(message.msgtype, Message.MSGTYPE_VIDEO)) {
                str = JsonUtils.toVideoMessage(event.getContent()).getUrl();
            } else if (TextUtils.equals(message.msgtype, Message.MSGTYPE_FILE)) {
                str = JsonUtils.toFileMessage(event.getContent()).getUrl();
            }
            if (!TextUtils.isEmpty(str)) {
                if (this.mSession.getMediasCache().downloadIdFromUrl(str) != null) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private static String vectorRemainingTimeToString(Context context, int i) {
        if (i < 0) {
            return "";
        }
        if (i <= 1) {
            return "< 1s";
        }
        if (i < 60) {
            return context.getString(C1299R.string.attachment_remaining_time_seconds, new Object[]{Integer.valueOf(i)});
        } else if (i >= 3600) {
            return DateUtils.formatElapsedTime((long) i);
        } else {
            return context.getString(C1299R.string.attachment_remaining_time_minutes, new Object[]{Integer.valueOf(i / 60), Integer.valueOf(i % 60)});
        }
    }

    private static String formatStats(Context context, int i, int i2, int i3) {
        String str = "";
        if (i2 > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(Formatter.formatShortFileSize(context, (long) i));
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append(" / ");
            sb3.append(Formatter.formatShortFileSize(context, (long) i2));
            str = sb3.toString();
        }
        if (i3 <= 0) {
            return str;
        }
        if (!TextUtils.isEmpty(str)) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append(" (");
            sb4.append(vectorRemainingTimeToString(context, i3));
            sb4.append(")");
            return sb4.toString();
        }
        StringBuilder sb5 = new StringBuilder();
        sb5.append(str);
        sb5.append(vectorRemainingTimeToString(context, i3));
        return sb5.toString();
    }

    private static String formatDownloadStats(Context context, DownloadStats downloadStats) {
        return formatStats(context, downloadStats.mDownloadedSize, downloadStats.mFileSize, downloadStats.mEstimatedRemainingTime);
    }

    private static String formatUploadStats(Context context, UploadStats uploadStats) {
        return formatStats(context, uploadStats.mUploadedSize, uploadStats.mFileSize, uploadStats.mEstimatedRemainingTime);
    }
}
