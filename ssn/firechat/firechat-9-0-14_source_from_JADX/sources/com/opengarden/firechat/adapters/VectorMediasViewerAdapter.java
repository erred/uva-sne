package com.opengarden.firechat.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import com.google.gson.JsonElement;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaDownloadListener.DownloadStats;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.ImageUtils;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.view.PieFractionView;
import com.opengarden.firechat.util.SlidableMediaInfo;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class VectorMediasViewerAdapter extends PagerAdapter {
    private static final String LOG_TAG = "VectorMediasViewerAdapter";
    /* access modifiers changed from: private */
    public int mAutoPlayItemAt = -1;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final ArrayList<Integer> mHighResMediaIndex = new ArrayList<>();
    private int mLatestPrimaryItemPosition = -1;
    private View mLatestPrimaryView = null;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public final int mMaxImageHeight;
    /* access modifiers changed from: private */
    public final int mMaxImageWidth;
    /* access modifiers changed from: private */
    public final MXMediasCache mMediasCache;
    /* access modifiers changed from: private */
    public List<SlidableMediaInfo> mMediasMessagesList = null;
    /* access modifiers changed from: private */
    public VideoView mPlayingVideoView = null;
    private final MXSession mSession;

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public VectorMediasViewerAdapter(Context context, MXSession mXSession, MXMediasCache mXMediasCache, List<SlidableMediaInfo> list, int i, int i2) {
        this.mContext = context;
        this.mSession = mXSession;
        this.mMediasMessagesList = list;
        this.mMaxImageWidth = i;
        this.mMaxImageHeight = i2;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mMediasCache = mXMediasCache;
    }

    public int getCount() {
        return this.mMediasMessagesList.size();
    }

    public void setPrimaryItem(ViewGroup viewGroup, final int i, Object obj) {
        if (this.mLatestPrimaryItemPosition != i) {
            this.mLatestPrimaryItemPosition = i;
            final View view = (View) obj;
            this.mLatestPrimaryView = view;
            view.findViewById(C1299R.C1301id.media_download_failed).setVisibility(8);
            view.post(new Runnable() {
                public void run() {
                    VectorMediasViewerAdapter.this.stopPlayingVideo();
                }
            });
            view.post(new Runnable() {
                public void run() {
                    if (VectorMediasViewerAdapter.this.mHighResMediaIndex.indexOf(Integer.valueOf(i)) < 0) {
                        VectorMediasViewerAdapter.this.downloadHighResMedia(view, i);
                    } else if (i == VectorMediasViewerAdapter.this.mAutoPlayItemAt) {
                        final SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) VectorMediasViewerAdapter.this.mMediasMessagesList.get(i);
                        if (slidableMediaInfo.mMessageType.equals(Message.MSGTYPE_VIDEO)) {
                            final VideoView videoView = (VideoView) view.findViewById(C1299R.C1301id.media_slider_videoview);
                            if (VectorMediasViewerAdapter.this.mMediasCache.isMediaCached(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType)) {
                                VectorMediasViewerAdapter.this.mMediasCache.createTmpMediaFile(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                                    public void onSuccess(File file) {
                                        if (file != null) {
                                            VectorMediasViewerAdapter.this.playVideo(view, videoView, file, slidableMediaInfo.mMimeType);
                                        }
                                    }
                                });
                            }
                        }
                        VectorMediasViewerAdapter.this.mAutoPlayItemAt = -1;
                    }
                }
            });
        }
    }

    public void autoPlayItemAt(int i) {
        this.mAutoPlayItemAt = i;
    }

    /* access modifiers changed from: private */
    public void downloadHighResMedia(View view, int i) {
        SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasMessagesList.get(i);
        if (slidableMediaInfo.mMessageType.equals(Message.MSGTYPE_IMAGE)) {
            if (TextUtils.isEmpty(slidableMediaInfo.mMimeType)) {
                slidableMediaInfo.mMimeType = ResourceUtils.MIME_TYPE_JPEG;
            }
            downloadHighResPict(view, i);
            return;
        }
        downloadVideo(view, i);
    }

    /* access modifiers changed from: private */
    public void downloadVideo(View view, int i) {
        downloadVideo(view, i, false);
    }

    private void downloadVideo(View view, int i, boolean z) {
        final View view2 = view;
        final int i2 = i;
        final VideoView videoView = (VideoView) view2.findViewById(C1299R.C1301id.media_slider_videoview);
        final ImageView imageView = (ImageView) view2.findViewById(C1299R.C1301id.media_slider_video_thumbnail);
        final PieFractionView pieFractionView = (PieFractionView) view2.findViewById(C1299R.C1301id.media_slider_piechart);
        final View findViewById = view2.findViewById(C1299R.C1301id.media_download_failed);
        final SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasMessagesList.get(i2);
        String str = slidableMediaInfo.mMediaUrl;
        final String str2 = slidableMediaInfo.mThumbnailUrl;
        if (this.mMediasCache.isMediaCached(str, slidableMediaInfo.mMimeType)) {
            MXMediasCache mXMediasCache = this.mMediasCache;
            String str3 = slidableMediaInfo.mMimeType;
            EncryptedFileInfo encryptedFileInfo = slidableMediaInfo.mEncryptedFileInfo;
            final int i3 = i2;
            final View view3 = view2;
            final VideoView videoView2 = videoView;
            C17673 r0 = new SimpleApiCallback<File>() {
                public void onSuccess(File file) {
                    if (file != null) {
                        VectorMediasViewerAdapter.this.mHighResMediaIndex.add(Integer.valueOf(i3));
                        VectorMediasViewerAdapter.this.loadVideo(i3, view3, str2, Uri.fromFile(file).toString(), slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
                        if (i3 == VectorMediasViewerAdapter.this.mAutoPlayItemAt) {
                            VectorMediasViewerAdapter.this.playVideo(view3, videoView2, file, slidableMediaInfo.mMimeType);
                        }
                        VectorMediasViewerAdapter.this.mAutoPlayItemAt = -1;
                    }
                }
            };
            mXMediasCache.createTmpMediaFile(str, str3, encryptedFileInfo, r0);
        } else if (z || this.mAutoPlayItemAt == i2) {
            String downloadMedia = this.mMediasCache.downloadMedia(this.mContext, this.mSession.getHomeServerConfig(), str, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
            if (downloadMedia != null) {
                pieFractionView.setVisibility(0);
                pieFractionView.setFraction(this.mMediasCache.getProgressValueForDownloadId(downloadMedia));
                pieFractionView.setTag(downloadMedia);
                MXMediasCache mXMediasCache2 = this.mMediasCache;
                String str4 = str2;
                final String str5 = str;
                final String str6 = str4;
                C17684 r02 = new MXMediaDownloadListener() {
                    public void onDownloadError(String str, JsonElement jsonElement) {
                        MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                        if (matrixError != null && matrixError.isSupportedErrorCode()) {
                            Toast.makeText(VectorMediasViewerAdapter.this.mContext, matrixError.getLocalizedMessage(), 1).show();
                        }
                        findViewById.setVisibility(0);
                    }

                    public void onDownloadProgress(String str, DownloadStats downloadStats) {
                        if (str.equals(pieFractionView.getTag())) {
                            pieFractionView.setFraction(downloadStats.mProgress);
                        }
                    }

                    public void onDownloadComplete(String str) {
                        if (str.equals(pieFractionView.getTag())) {
                            pieFractionView.setVisibility(8);
                            if (VectorMediasViewerAdapter.this.mMediasCache.isMediaCached(str5, slidableMediaInfo.mMimeType)) {
                                VectorMediasViewerAdapter.this.mMediasCache.createTmpMediaFile(str5, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                                    public void onSuccess(final File file) {
                                        if (file != null) {
                                            VectorMediasViewerAdapter.this.mHighResMediaIndex.add(Integer.valueOf(i2));
                                            final String uri = Uri.fromFile(file).toString();
                                            imageView.post(new Runnable() {
                                                public void run() {
                                                    VectorMediasViewerAdapter.this.loadVideo(i2, view2, str6, uri, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
                                                    if (i2 == VectorMediasViewerAdapter.this.mAutoPlayItemAt) {
                                                        VectorMediasViewerAdapter.this.playVideo(view2, videoView, file, slidableMediaInfo.mMimeType);
                                                        VectorMediasViewerAdapter.this.mAutoPlayItemAt = -1;
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                findViewById.setVisibility(0);
                            }
                        }
                    }
                };
                mXMediasCache2.addDownloadListener(downloadMedia, r02);
            }
        }
    }

    private void downloadHighResPict(View view, int i) {
        View view2 = view;
        final WebView webView = (WebView) view2.findViewById(C1299R.C1301id.media_slider_image_webview);
        final PieFractionView pieFractionView = (PieFractionView) view2.findViewById(C1299R.C1301id.media_slider_piechart);
        final View findViewById = view2.findViewById(C1299R.C1301id.media_download_failed);
        final int i2 = i;
        final SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasMessagesList.get(i2);
        final String str = slidableMediaInfo.mMediaUrl;
        String loadBitmap = this.mMediasCache.loadBitmap(this.mContext, this.mSession.getHomeServerConfig(), str, slidableMediaInfo.mRotationAngle, slidableMediaInfo.mOrientation, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
        webView.getSettings().setDisplayZoomControls(false);
        if (loadBitmap != null) {
            pieFractionView.setVisibility(0);
            pieFractionView.setFraction(this.mMediasCache.getProgressValueForDownloadId(loadBitmap));
            MXMediasCache mXMediasCache = this.mMediasCache;
            final String str2 = loadBitmap;
            C17715 r0 = new MXMediaDownloadListener() {
                public void onDownloadError(String str, JsonElement jsonElement) {
                    if (str.equals(str2)) {
                        pieFractionView.setVisibility(8);
                        MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                        if (matrixError != null) {
                            Toast.makeText(VectorMediasViewerAdapter.this.mContext, matrixError.getLocalizedMessage(), 1).show();
                        }
                        findViewById.setVisibility(0);
                    }
                }

                public void onDownloadProgress(String str, DownloadStats downloadStats) {
                    if (str.equals(str2)) {
                        pieFractionView.setFraction(downloadStats.mProgress);
                    }
                }

                public void onDownloadComplete(String str) {
                    if (str.equals(str2)) {
                        pieFractionView.setVisibility(8);
                        if (VectorMediasViewerAdapter.this.mMediasCache.isMediaCached(str, slidableMediaInfo.mMimeType)) {
                            VectorMediasViewerAdapter.this.mMediasCache.createTmpMediaFile(str, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                                public void onSuccess(File file) {
                                    if (file != null) {
                                        VectorMediasViewerAdapter.this.mHighResMediaIndex.add(Integer.valueOf(i2));
                                        final String uri = Uri.fromFile(file).toString();
                                        webView.post(new Runnable() {
                                            public void run() {
                                                VectorMediasViewerAdapter.this.loadImage(webView, Uri.parse(uri), "width=640", VectorMediasViewerAdapter.this.computeCss(uri, VectorMediasViewerAdapter.this.mMaxImageWidth, VectorMediasViewerAdapter.this.mMaxImageHeight, slidableMediaInfo.mRotationAngle));
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            findViewById.setVisibility(0);
                        }
                    }
                }
            };
            mXMediasCache.addDownloadListener(loadBitmap, r0);
        }
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        String str;
        int i2;
        int i3;
        ViewGroup viewGroup2 = viewGroup;
        View inflate = this.mLayoutInflater.inflate(C1299R.layout.adapter_vector_medias_viewer, null, false);
        final PieFractionView pieFractionView = (PieFractionView) inflate.findViewById(C1299R.C1301id.media_slider_piechart);
        pieFractionView.setVisibility(8);
        inflate.findViewById(C1299R.C1301id.media_download_failed).setVisibility(8);
        final WebView webView = (WebView) inflate.findViewById(C1299R.C1301id.media_slider_image_webview);
        View findViewById = inflate.findViewById(C1299R.C1301id.media_slider_videolayout);
        ImageView imageView = (ImageView) inflate.findViewById(C1299R.C1301id.media_slider_video_thumbnail);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                VectorMediasViewerAdapter.this.onLongClick();
                return true;
            }
        });
        imageView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                VectorMediasViewerAdapter.this.onLongClick();
                return true;
            }
        });
        inflate.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        webView.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        findViewById.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        int i4 = i;
        SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasMessagesList.get(i4);
        String str2 = slidableMediaInfo.mMediaUrl;
        if (slidableMediaInfo.mMessageType.equals(Message.MSGTYPE_IMAGE)) {
            webView.setVisibility(0);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setBuiltInZoomControls(true);
            findViewById.setVisibility(8);
            final int i5 = slidableMediaInfo.mRotationAngle;
            if (TextUtils.isEmpty(slidableMediaInfo.mMimeType)) {
                slidableMediaInfo.mMimeType = ResourceUtils.MIME_TYPE_JPEG;
            }
            String str3 = slidableMediaInfo.mMimeType;
            if (this.mMediasCache.isMediaCached(str2, str3)) {
                if (this.mHighResMediaIndex.indexOf(Integer.valueOf(i)) < 0) {
                    this.mHighResMediaIndex.add(Integer.valueOf(i));
                }
                i3 = -1;
                i2 = -1;
            } else {
                i2 = this.mMaxImageWidth;
                i3 = this.mMaxImageHeight;
            }
            if (!this.mMediasCache.isMediaCached(str2, i2, i3, str3)) {
                viewGroup2.addView(inflate, 0);
                return inflate;
            }
            MXMediasCache mXMediasCache = this.mMediasCache;
            EncryptedFileInfo encryptedFileInfo = slidableMediaInfo.mEncryptedFileInfo;
            final ViewGroup viewGroup3 = viewGroup2;
            MXMediasCache mXMediasCache2 = mXMediasCache;
            final View view = inflate;
            C17768 r0 = new SimpleApiCallback<File>() {
                public void onSuccess(File file) {
                    if (file != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("file://");
                        sb.append(file.getPath());
                        String sb2 = sb.toString();
                        VectorMediasViewerAdapter.this.loadImage(webView, Uri.parse(sb2), "width=640", VectorMediasViewerAdapter.this.computeCss(sb2, VectorMediasViewerAdapter.this.mMaxImageWidth, VectorMediasViewerAdapter.this.mMaxImageHeight, i5));
                        viewGroup3.addView(view, 0);
                    }
                }
            };
            int i6 = i2;
            mXMediasCache2.createTmpDecryptedMediaFile(str2, i6, i3, str3, encryptedFileInfo, r0);
            str = str2;
        } else {
            str = str2;
            loadVideo(i4, inflate, slidableMediaInfo.mThumbnailUrl, str2, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
            viewGroup2.addView(inflate, 0);
        }
        String downloadMedia = this.mMediasCache.downloadMedia(this.mContext, this.mSession.getHomeServerConfig(), str, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
        if (downloadMedia != null) {
            pieFractionView.setVisibility(0);
            pieFractionView.setFraction(this.mMediasCache.getProgressValueForDownloadId(downloadMedia));
            pieFractionView.setTag(downloadMedia);
            this.mMediasCache.addDownloadListener(downloadMedia, new MXMediaDownloadListener() {
                public void onDownloadError(String str, JsonElement jsonElement) {
                    pieFractionView.setVisibility(8);
                    MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                    if (matrixError != null && matrixError.isSupportedErrorCode()) {
                        Toast.makeText(VectorMediasViewerAdapter.this.mContext, matrixError.getLocalizedMessage(), 1).show();
                    }
                }

                public void onDownloadProgress(String str, DownloadStats downloadStats) {
                    if (str.equals(pieFractionView.getTag())) {
                        pieFractionView.setFraction(downloadStats.mProgress);
                    }
                }

                public void onDownloadComplete(String str) {
                    if (str.equals(pieFractionView.getTag())) {
                        pieFractionView.setVisibility(8);
                    }
                }
            });
        }
        return inflate;
    }

    /* access modifiers changed from: private */
    public void displayVideoThumbnail(View view, boolean z) {
        VideoView videoView = (VideoView) view.findViewById(C1299R.C1301id.media_slider_videoview);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.media_slider_video_thumbnail);
        ImageView imageView2 = (ImageView) view.findViewById(C1299R.C1301id.media_slider_video_playView);
        int i = 0;
        videoView.setVisibility(z ? 8 : 0);
        imageView.setVisibility(z ? 0 : 8);
        if (!z) {
            i = 8;
        }
        imageView2.setVisibility(i);
    }

    public void stopPlayingVideo() {
        if (this.mPlayingVideoView != null) {
            this.mPlayingVideoView.stopPlayback();
            displayVideoThumbnail((View) this.mPlayingVideoView.getParent(), true);
            this.mPlayingVideoView = null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ca A[SYNTHETIC, Splitter:B:46:0x00ca] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00d2 A[Catch:{ Exception -> 0x00ce }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0104 A[SYNTHETIC, Splitter:B:58:0x0104] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x010c A[Catch:{ Exception -> 0x0108 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void playVideo(android.view.View r7, android.widget.VideoView r8, java.io.File r9, java.lang.String r10) {
        /*
            r6 = this;
            if (r9 == 0) goto L_0x0146
            boolean r0 = r9.exists()
            if (r0 == 0) goto L_0x0146
            r6.stopPlayingVideo()     // Catch:{ Exception -> 0x012b }
            android.webkit.MimeTypeMap r0 = android.webkit.MimeTypeMap.getSingleton()     // Catch:{ Exception -> 0x012b }
            java.lang.String r10 = r0.getExtensionFromMimeType(r10)     // Catch:{ Exception -> 0x012b }
            if (r10 == 0) goto L_0x0029
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b }
            r0.<init>()     // Catch:{ Exception -> 0x012b }
            r0.append(r10)     // Catch:{ Exception -> 0x012b }
            java.lang.String r1 = "."
            r0.append(r1)     // Catch:{ Exception -> 0x012b }
            r0.append(r10)     // Catch:{ Exception -> 0x012b }
            java.lang.String r10 = r0.toString()     // Catch:{ Exception -> 0x012b }
        L_0x0029:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x012b }
            android.content.Context r1 = r6.mContext     // Catch:{ Exception -> 0x012b }
            java.io.File r1 = r1.getCacheDir()     // Catch:{ Exception -> 0x012b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b }
            r2.<init>()     // Catch:{ Exception -> 0x012b }
            java.lang.String r3 = "sliderMedia"
            r2.append(r3)     // Catch:{ Exception -> 0x012b }
            r2.append(r10)     // Catch:{ Exception -> 0x012b }
            java.lang.String r10 = r2.toString()     // Catch:{ Exception -> 0x012b }
            r0.<init>(r1, r10)     // Catch:{ Exception -> 0x012b }
            boolean r10 = r0.exists()     // Catch:{ Exception -> 0x012b }
            if (r10 == 0) goto L_0x004e
            r0.delete()     // Catch:{ Exception -> 0x012b }
        L_0x004e:
            r10 = 0
            r1 = 0
            boolean r2 = r0.exists()     // Catch:{ Exception -> 0x00ab, all -> 0x00a7 }
            if (r2 != 0) goto L_0x007d
            r0.createNewFile()     // Catch:{ Exception -> 0x00ab, all -> 0x00a7 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00ab, all -> 0x00a7 }
            r2.<init>(r9)     // Catch:{ Exception -> 0x00ab, all -> 0x00a7 }
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x007a, all -> 0x0076 }
            r9.<init>(r0)     // Catch:{ Exception -> 0x007a, all -> 0x0076 }
            r3 = 10240(0x2800, float:1.4349E-41)
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0074 }
        L_0x0067:
            int r4 = r2.read(r3)     // Catch:{ Exception -> 0x0074 }
            r5 = -1
            if (r4 == r5) goto L_0x0072
            r9.write(r3, r10, r4)     // Catch:{ Exception -> 0x0074 }
            goto L_0x0067
        L_0x0072:
            r1 = r2
            goto L_0x007e
        L_0x0074:
            r0 = move-exception
            goto L_0x00ae
        L_0x0076:
            r7 = move-exception
            r9 = r1
            goto L_0x0102
        L_0x007a:
            r0 = move-exception
            r9 = r1
            goto L_0x00ae
        L_0x007d:
            r9 = r1
        L_0x007e:
            if (r1 == 0) goto L_0x0086
            r1.close()     // Catch:{ Exception -> 0x0084 }
            goto L_0x0086
        L_0x0084:
            r9 = move-exception
            goto L_0x008c
        L_0x0086:
            if (r9 == 0) goto L_0x00f1
            r9.close()     // Catch:{ Exception -> 0x0084 }
            goto L_0x00f1
        L_0x008c:
            java.lang.String r1 = LOG_TAG     // Catch:{ Exception -> 0x012b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b }
            r2.<init>()     // Catch:{ Exception -> 0x012b }
            java.lang.String r3 = "## playVideo() : failed "
            r2.append(r3)     // Catch:{ Exception -> 0x012b }
            java.lang.String r9 = r9.getMessage()     // Catch:{ Exception -> 0x012b }
            r2.append(r9)     // Catch:{ Exception -> 0x012b }
            java.lang.String r9 = r2.toString()     // Catch:{ Exception -> 0x012b }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r9)     // Catch:{ Exception -> 0x012b }
            goto L_0x00f1
        L_0x00a7:
            r7 = move-exception
            r9 = r1
            r2 = r9
            goto L_0x0102
        L_0x00ab:
            r0 = move-exception
            r9 = r1
            r2 = r9
        L_0x00ae:
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x0101 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0101 }
            r4.<init>()     // Catch:{ all -> 0x0101 }
            java.lang.String r5 = "## playVideo() : failed "
            r4.append(r5)     // Catch:{ all -> 0x0101 }
            java.lang.String r0 = r0.getMessage()     // Catch:{ all -> 0x0101 }
            r4.append(r0)     // Catch:{ all -> 0x0101 }
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0101 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r0)     // Catch:{ all -> 0x0101 }
            if (r2 == 0) goto L_0x00d0
            r2.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00d0
        L_0x00ce:
            r9 = move-exception
            goto L_0x00d6
        L_0x00d0:
            if (r9 == 0) goto L_0x00f0
            r9.close()     // Catch:{ Exception -> 0x00ce }
            goto L_0x00f0
        L_0x00d6:
            java.lang.String r0 = LOG_TAG     // Catch:{ Exception -> 0x012b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b }
            r2.<init>()     // Catch:{ Exception -> 0x012b }
            java.lang.String r3 = "## playVideo() : failed "
            r2.append(r3)     // Catch:{ Exception -> 0x012b }
            java.lang.String r9 = r9.getMessage()     // Catch:{ Exception -> 0x012b }
            r2.append(r9)     // Catch:{ Exception -> 0x012b }
            java.lang.String r9 = r2.toString()     // Catch:{ Exception -> 0x012b }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r9)     // Catch:{ Exception -> 0x012b }
        L_0x00f0:
            r0 = r1
        L_0x00f1:
            java.lang.String r9 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x012b }
            r8.setVideoPath(r9)     // Catch:{ Exception -> 0x012b }
            r6.displayVideoThumbnail(r7, r10)     // Catch:{ Exception -> 0x012b }
            r6.mPlayingVideoView = r8     // Catch:{ Exception -> 0x012b }
            r8.start()     // Catch:{ Exception -> 0x012b }
            goto L_0x0146
        L_0x0101:
            r7 = move-exception
        L_0x0102:
            if (r2 == 0) goto L_0x010a
            r2.close()     // Catch:{ Exception -> 0x0108 }
            goto L_0x010a
        L_0x0108:
            r8 = move-exception
            goto L_0x0110
        L_0x010a:
            if (r9 == 0) goto L_0x012a
            r9.close()     // Catch:{ Exception -> 0x0108 }
            goto L_0x012a
        L_0x0110:
            java.lang.String r9 = LOG_TAG     // Catch:{ Exception -> 0x012b }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012b }
            r10.<init>()     // Catch:{ Exception -> 0x012b }
            java.lang.String r0 = "## playVideo() : failed "
            r10.append(r0)     // Catch:{ Exception -> 0x012b }
            java.lang.String r8 = r8.getMessage()     // Catch:{ Exception -> 0x012b }
            r10.append(r8)     // Catch:{ Exception -> 0x012b }
            java.lang.String r8 = r10.toString()     // Catch:{ Exception -> 0x012b }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r9, r8)     // Catch:{ Exception -> 0x012b }
        L_0x012a:
            throw r7     // Catch:{ Exception -> 0x012b }
        L_0x012b:
            r7 = move-exception
            java.lang.String r8 = LOG_TAG
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "## playVideo() : videoView.start(); failed "
            r9.append(r10)
            java.lang.String r7 = r7.getMessage()
            r9.append(r7)
            java.lang.String r7 = r9.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r7)
        L_0x0146:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.adapters.VectorMediasViewerAdapter.playVideo(android.view.View, android.widget.VideoView, java.io.File, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void downloadMedia() {
        final SlidableMediaInfo slidableMediaInfo = (SlidableMediaInfo) this.mMediasMessagesList.get(this.mLatestPrimaryItemPosition);
        if (this.mMediasCache.isMediaCached(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType)) {
            this.mMediasCache.createTmpMediaFile(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                public void onSuccess(File file) {
                    if (file != null) {
                        CommonActivityUtils.saveMediaIntoDownloads(VectorMediasViewerAdapter.this.mContext, file, null, slidableMediaInfo.mMimeType, new SimpleApiCallback<String>() {
                            public void onSuccess(String str) {
                                Toast.makeText(VectorMediasViewerAdapter.this.mContext, VectorMediasViewerAdapter.this.mContext.getText(C1299R.string.media_slider_saved), 1).show();
                            }
                        });
                    }
                }
            });
            return;
        }
        downloadVideo(this.mLatestPrimaryView, this.mLatestPrimaryItemPosition, true);
        final String downloadMedia = this.mMediasCache.downloadMedia(this.mContext, this.mSession.getHomeServerConfig(), slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo);
        if (downloadMedia != null) {
            this.mMediasCache.addDownloadListener(downloadMedia, new MXMediaDownloadListener() {
                public void onDownloadError(String str, JsonElement jsonElement) {
                    MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                    if (matrixError != null && matrixError.isSupportedErrorCode()) {
                        Toast.makeText(VectorMediasViewerAdapter.this.mContext, matrixError.getLocalizedMessage(), 1).show();
                    }
                }

                public void onDownloadComplete(String str) {
                    if (str.equals(downloadMedia) && VectorMediasViewerAdapter.this.mMediasCache.isMediaCached(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType)) {
                        VectorMediasViewerAdapter.this.mMediasCache.createTmpMediaFile(slidableMediaInfo.mMediaUrl, slidableMediaInfo.mMimeType, slidableMediaInfo.mEncryptedFileInfo, new SimpleApiCallback<File>() {
                            public void onSuccess(File file) {
                                if (file != null) {
                                    CommonActivityUtils.saveMediaIntoDownloads(VectorMediasViewerAdapter.this.mContext, file, null, slidableMediaInfo.mMimeType, new SimpleApiCallback<String>() {
                                        public void onSuccess(String str) {
                                            Toast.makeText(VectorMediasViewerAdapter.this.mContext, VectorMediasViewerAdapter.this.mContext.getText(C1299R.string.media_slider_saved), 1).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onLongClick() {
        new Builder(this.mContext).setMessage(C1299R.string.media_slider_saved_message).setPositiveButton(C1299R.string.yes, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                VectorMediasViewerAdapter.this.downloadMedia();
            }
        }).setNegativeButton(C1299R.string.f114no, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    /* access modifiers changed from: private */
    public void loadVideo(int i, View view, String str, String str2, String str3, EncryptedFileInfo encryptedFileInfo) {
        final View view2 = view;
        final VideoView videoView = (VideoView) view2.findViewById(C1299R.C1301id.media_slider_videoview);
        ImageView imageView = (ImageView) view2.findViewById(C1299R.C1301id.media_slider_video_thumbnail);
        ImageView imageView2 = (ImageView) view2.findViewById(C1299R.C1301id.media_slider_video_playView);
        displayVideoThumbnail(view2, !videoView.isPlaying());
        videoView.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                VectorMediasViewerAdapter.this.mPlayingVideoView = null;
                VectorMediasViewerAdapter.this.displayVideoThumbnail(view2, true);
            }
        });
        ((View) videoView.getParent()).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VectorMediasViewerAdapter.this.stopPlayingVideo();
                VectorMediasViewerAdapter.this.displayVideoThumbnail(view2, true);
            }
        });
        videoView.setOnErrorListener(new OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                VectorMediasViewerAdapter.this.mPlayingVideoView = null;
                VectorMediasViewerAdapter.this.displayVideoThumbnail(view2, true);
                return false;
            }
        });
        this.mMediasCache.loadBitmap(this.mSession.getHomeServerConfig(), imageView, str, 0, 0, (String) null, (EncryptedFileInfo) null);
        final String str4 = str2;
        final String str5 = str3;
        final EncryptedFileInfo encryptedFileInfo2 = encryptedFileInfo;
        ImageView imageView3 = imageView2;
        final int i2 = i;
        C176317 r0 = new View.OnClickListener() {
            public void onClick(View view) {
                if (VectorMediasViewerAdapter.this.mMediasCache.isMediaCached(str4, str5)) {
                    VectorMediasViewerAdapter.this.mMediasCache.createTmpMediaFile(str4, str5, encryptedFileInfo2, new SimpleApiCallback<File>() {
                        public void onSuccess(File file) {
                            if (file != null) {
                                VectorMediasViewerAdapter.this.playVideo(view2, videoView, file, str5);
                            }
                        }
                    });
                    return;
                }
                VectorMediasViewerAdapter.this.mAutoPlayItemAt = i2;
                VectorMediasViewerAdapter.this.downloadVideo(view2, i2);
            }
        };
        imageView3.setOnClickListener(r0);
    }

    /* access modifiers changed from: private */
    public void loadImage(WebView webView, Uri uri, String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta name='viewport' content='");
        sb.append(str);
        sb.append("'/><style type='text/css'>");
        sb.append(str2);
        sb.append("</style></head><body> <div class='wrap'><img ");
        sb.append("src='");
        sb.append(uri.toString());
        sb.append("'");
        sb.append(" onerror='this.style.display=\"none\"' id='image' ");
        sb.append(str);
        sb.append("/></div></body></html>");
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
        webView.requestLayout();
    }

    /* access modifiers changed from: private */
    public String computeCss(String str, int i, int i2, int i3) {
        Uri uri;
        int i4;
        int i5;
        String str2 = "body { background-color: #000; height: 100%; width: 100%; margin: 0px; padding: 0px; }.wrap { position: absolute; left: 0px; right: 0px; width: 100%; height: 100%; display: -webkit-box; -webkit-box-pack: center; -webkit-box-align: center; display: box; box-pack: center; box-align: center; } ";
        Bitmap bitmap = null;
        try {
            uri = Uri.parse(str);
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## computeCss() : Uri.parse failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
            uri = null;
        }
        if (uri == null) {
            return str2;
        }
        if (i3 == Integer.MAX_VALUE && str != null) {
            i3 = ImageUtils.getRotationAngleForBitmap(this.mContext, uri);
        }
        if (i3 != 0) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(uri.getPath()));
                Options options = new Options();
                options.inJustDecodeBounds = true;
                options.inPreferredConfig = Config.ARGB_8888;
                options.outWidth = -1;
                options.outHeight = -1;
                try {
                    bitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
                } catch (OutOfMemoryError e2) {
                    String str4 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## computeCss() : BitmapFactory.decodeStream failed ");
                    sb2.append(e2.getMessage());
                    Log.m211e(str4, sb2.toString());
                }
                i5 = options.outWidth;
                try {
                    i4 = options.outHeight;
                    try {
                        fileInputStream.close();
                        bitmap.recycle();
                    } catch (Exception e3) {
                        e = e3;
                    }
                } catch (Exception e4) {
                    e = e4;
                    i4 = i2;
                    String str5 = LOG_TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("## computeCss() : failed ");
                    sb3.append(e.getMessage());
                    Log.m211e(str5, sb3.toString());
                    String calcCssRotation = calcCssRotation(i3, i5, i4);
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append(str2);
                    sb4.append("#image { ");
                    sb4.append(calcCssRotation);
                    sb4.append(" } ");
                    String sb5 = sb4.toString();
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append(sb5);
                    sb6.append("#thumbnail { ");
                    sb6.append(calcCssRotation);
                    sb6.append(" } ");
                    str2 = sb6.toString();
                    return str2;
                }
            } catch (Exception e5) {
                e = e5;
                i5 = i;
                i4 = i2;
                String str52 = LOG_TAG;
                StringBuilder sb32 = new StringBuilder();
                sb32.append("## computeCss() : failed ");
                sb32.append(e.getMessage());
                Log.m211e(str52, sb32.toString());
                String calcCssRotation2 = calcCssRotation(i3, i5, i4);
                StringBuilder sb42 = new StringBuilder();
                sb42.append(str2);
                sb42.append("#image { ");
                sb42.append(calcCssRotation2);
                sb42.append(" } ");
                String sb52 = sb42.toString();
                StringBuilder sb62 = new StringBuilder();
                sb62.append(sb52);
                sb62.append("#thumbnail { ");
                sb62.append(calcCssRotation2);
                sb62.append(" } ");
                str2 = sb62.toString();
                return str2;
            }
            String calcCssRotation22 = calcCssRotation(i3, i5, i4);
            StringBuilder sb422 = new StringBuilder();
            sb422.append(str2);
            sb422.append("#image { ");
            sb422.append(calcCssRotation22);
            sb422.append(" } ");
            String sb522 = sb422.toString();
            StringBuilder sb622 = new StringBuilder();
            sb622.append(sb522);
            sb622.append("#thumbnail { ");
            sb622.append(calcCssRotation22);
            sb622.append(" } ");
            str2 = sb622.toString();
        }
        return str2;
    }

    private String calcCssRotation(int i, int i2, int i3) {
        if (i == 90 || i == 180 || i == 270) {
            Point displaySize = getDisplaySize();
            double min = Math.min(((double) i2) / ((double) i3), ((double) displaySize.y) / ((double) displaySize.x));
            if (i == 90) {
                StringBuilder sb = new StringBuilder();
                sb.append("-webkit-transform-origin: 50% 50%; -webkit-transform: rotate(90deg) scale(");
                sb.append(min);
                sb.append(" , ");
                sb.append(min);
                sb.append(");");
                return sb.toString();
            } else if (i == 180) {
                return "-webkit-transform: rotate(180deg);";
            } else {
                if (i == 270) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("-webkit-transform-origin: 50% 50%; -webkit-transform: rotate(270deg) scale(");
                    sb2.append(min);
                    sb2.append(" , ");
                    sb2.append(min);
                    sb2.append(");");
                    return sb2.toString();
                }
            }
        }
        return "";
    }

    @SuppressLint({"NewApi"})
    private Point getDisplaySize() {
        Point point = new Point();
        ((Activity) this.mContext).getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }

    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        viewGroup.removeView((View) obj);
    }
}
