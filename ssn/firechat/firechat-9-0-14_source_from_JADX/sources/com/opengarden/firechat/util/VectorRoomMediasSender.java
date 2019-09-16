package com.opengarden.firechat.util;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.fragments.VectorMessageListFragment;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import java.util.ArrayList;
import java.util.List;

public class VectorRoomMediasSender {
    private static final int LARGE_IMAGE_SIZE = 2048;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomMediasSender";
    private static final int MEDIUM_IMAGE_SIZE = 1024;
    private static final int SMALL_IMAGE_SIZE = 512;
    private static final String TAG_FRAGMENT_IMAGE_SIZE_DIALOG = "TAG_FRAGMENT_IMAGE_SIZE_DIALOG";
    private static HandlerThread mHandlerThread;
    private static Handler mMediasSendingHandler;
    /* access modifiers changed from: private */
    public String mImageCompressionDescription;
    /* access modifiers changed from: private */
    public AlertDialog mImageSizesListDialog;
    private final MXMediasCache mMediasCache;
    /* access modifiers changed from: private */
    public List<RoomMediaMessage> mSharedDataItems;
    /* access modifiers changed from: private */
    public final VectorMessageListFragment mVectorMessageListFragment;
    /* access modifiers changed from: private */
    public final VectorRoomActivity mVectorRoomActivity;

    private class ImageCompressionSizes {
        public ImageSize mFullImageSize;
        public ImageSize mLargeImageSize;
        public ImageSize mMediumImageSize;
        public ImageSize mSmallImageSize;

        private ImageCompressionSizes() {
        }

        public List<ImageSize> getImageSizesList() {
            ArrayList arrayList = new ArrayList();
            if (this.mFullImageSize != null) {
                arrayList.add(this.mFullImageSize);
            }
            if (this.mLargeImageSize != null) {
                arrayList.add(this.mLargeImageSize);
            }
            if (this.mMediumImageSize != null) {
                arrayList.add(this.mMediumImageSize);
            }
            if (this.mSmallImageSize != null) {
                arrayList.add(this.mSmallImageSize);
            }
            return arrayList;
        }

        public List<String> getImageSizesDescription(Context context) {
            ArrayList arrayList = new ArrayList();
            if (this.mFullImageSize != null) {
                arrayList.add(context.getString(C1299R.string.compression_opt_list_original));
            }
            if (this.mLargeImageSize != null) {
                arrayList.add(context.getString(C1299R.string.compression_opt_list_large));
            }
            if (this.mMediumImageSize != null) {
                arrayList.add(context.getString(C1299R.string.compression_opt_list_medium));
            }
            if (this.mSmallImageSize != null) {
                arrayList.add(context.getString(C1299R.string.compression_opt_list_small));
            }
            return arrayList;
        }

        public ImageSize getImageSize(Context context, String str) {
            boolean equals = TextUtils.equals(context.getString(C1299R.string.compression_opt_list_original), str);
            if (TextUtils.isEmpty(str) || equals) {
                return this.mFullImageSize;
            }
            boolean equals2 = TextUtils.equals(context.getString(C1299R.string.compression_opt_list_small), str);
            boolean equals3 = TextUtils.equals(context.getString(C1299R.string.compression_opt_list_medium), str);
            boolean equals4 = TextUtils.equals(context.getString(C1299R.string.compression_opt_list_large), str);
            ImageSize imageSize = null;
            if (equals2) {
                imageSize = this.mSmallImageSize;
            }
            if (imageSize == null && (equals2 || equals3)) {
                imageSize = this.mMediumImageSize;
            }
            if (imageSize == null && (equals2 || equals3 || equals4)) {
                imageSize = this.mLargeImageSize;
            }
            if (imageSize == null) {
                imageSize = this.mFullImageSize;
            }
            return imageSize;
        }
    }

    private class ImageSize {
        public int mHeight;
        public int mWidth;

        public ImageSize(int i, int i2) {
            this.mWidth = i;
            this.mHeight = i2;
        }

        public ImageSize(ImageSize imageSize) {
            this.mWidth = imageSize.mWidth;
            this.mHeight = imageSize.mHeight;
        }

        /* access modifiers changed from: private */
        public ImageSize computeSizeToFit(float f) {
            if (0.0f == f) {
                return new ImageSize(0, 0);
            }
            ImageSize imageSize = new ImageSize(this);
            if (((float) this.mWidth) > f || ((float) this.mHeight) > f) {
                double highestOneBit = 1.0d / ((double) Integer.highestOneBit((int) Math.floor(1.0d / Math.min((double) (f / ((float) this.mWidth)), (double) (f / ((float) this.mHeight))))));
                imageSize.mWidth = (int) (Math.floor((((double) imageSize.mWidth) * highestOneBit) / 2.0d) * 2.0d);
                imageSize.mHeight = (int) (Math.floor((((double) imageSize.mHeight) * highestOneBit) / 2.0d) * 2.0d);
            }
            return imageSize;
        }
    }

    private interface OnImageUploadListener {
        void onCancel();

        void onDone();
    }

    public VectorRoomMediasSender(VectorRoomActivity vectorRoomActivity, VectorMessageListFragment vectorMessageListFragment, MXMediasCache mXMediasCache) {
        this.mVectorRoomActivity = vectorRoomActivity;
        this.mVectorMessageListFragment = vectorMessageListFragment;
        this.mMediasCache = mXMediasCache;
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread(LOG_TAG, 1);
            mHandlerThread.start();
            mMediasSendingHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    public void resumeResizeMediaAndSend() {
        if (this.mSharedDataItems != null) {
            this.mVectorRoomActivity.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomMediasSender.this.sendMedias();
                }
            });
        }
    }

    public void sendMedias(ArrayList<RoomMediaMessage> arrayList) {
        if (arrayList != null) {
            this.mSharedDataItems = new ArrayList(arrayList);
            sendMedias();
        }
    }

    /* access modifiers changed from: private */
    public void sendMedias() {
        if (this.mVectorRoomActivity == null || this.mVectorMessageListFragment == null || this.mMediasCache == null) {
            Log.m209d(LOG_TAG, "sendMedias : null parameters");
        } else if (this.mSharedDataItems == null || this.mSharedDataItems.size() == 0) {
            Log.m209d(LOG_TAG, "sendMedias : done");
            this.mImageCompressionDescription = null;
            this.mSharedDataItems = null;
            this.mVectorRoomActivity.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomMediasSender.this.mVectorMessageListFragment.scrollToBottom();
                    VectorRoomMediasSender.this.mVectorRoomActivity.cancelSelectionMode();
                    VectorRoomMediasSender.this.mVectorRoomActivity.hideWaitingView();
                }
            });
        } else {
            this.mVectorRoomActivity.cancelSelectionMode();
            this.mVectorRoomActivity.showWaitingView();
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("sendMedias : ");
            sb.append(this.mSharedDataItems.size());
            sb.append(" items to send");
            Log.m209d(str, sb.toString());
            mMediasSendingHandler.post(new Runnable() {
                public void run() {
                    final RoomMediaMessage roomMediaMessage = (RoomMediaMessage) VectorRoomMediasSender.this.mSharedDataItems.get(0);
                    String mimeType = roomMediaMessage.getMimeType(VectorRoomMediasSender.this.mVectorRoomActivity);
                    if (mimeType == null) {
                        mimeType = "";
                    }
                    if (TextUtils.equals("text/vnd.android.intent", mimeType)) {
                        Log.m209d(VectorRoomMediasSender.LOG_TAG, "sendMedias :  unsupported mime type");
                        if (VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                            VectorRoomMediasSender.this.mSharedDataItems.remove(0);
                        }
                        VectorRoomMediasSender.this.sendMedias();
                    } else if (roomMediaMessage.getUri() == null && (TextUtils.equals("text/plain", mimeType) || TextUtils.equals("text/html", mimeType))) {
                        VectorRoomMediasSender.this.sendTextMessage(roomMediaMessage);
                    } else if (roomMediaMessage.getUri() == null) {
                        Log.m211e(VectorRoomMediasSender.LOG_TAG, "sendMedias : null uri");
                        if (VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                            VectorRoomMediasSender.this.mSharedDataItems.remove(0);
                        }
                        VectorRoomMediasSender.this.sendMedias();
                    } else {
                        String fileName = roomMediaMessage.getFileName(VectorRoomMediasSender.this.mVectorRoomActivity);
                        Resource openResource = ResourceUtils.openResource(VectorRoomMediasSender.this.mVectorRoomActivity, roomMediaMessage.getUri(), roomMediaMessage.getMimeType(VectorRoomMediasSender.this.mVectorRoomActivity));
                        if (openResource == null) {
                            String access$400 = VectorRoomMediasSender.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("sendMedias : ");
                            sb.append(fileName);
                            sb.append(" is not found");
                            Log.m211e(access$400, sb.toString());
                            VectorRoomMediasSender.this.mVectorRoomActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(VectorRoomMediasSender.this.mVectorRoomActivity, VectorRoomMediasSender.this.mVectorRoomActivity.getString(C1299R.string.room_message_file_not_found), 1).show();
                                }
                            });
                            if (VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                                VectorRoomMediasSender.this.mSharedDataItems.remove(0);
                            }
                            VectorRoomMediasSender.this.sendMedias();
                        } else if (!mimeType.startsWith("image/") || (!ResourceUtils.MIME_TYPE_JPEG.equals(mimeType) && !ResourceUtils.MIME_TYPE_JPG.equals(mimeType) && !ResourceUtils.MIME_TYPE_IMAGE_ALL.equals(mimeType))) {
                            openResource.close();
                            VectorRoomMediasSender.this.mVectorRoomActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    VectorRoomMediasSender.this.mVectorMessageListFragment.sendMediaMessage(roomMediaMessage);
                                }
                            });
                            if (VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                                VectorRoomMediasSender.this.mSharedDataItems.remove(0);
                            }
                            VectorRoomMediasSender.this.sendMedias();
                        } else {
                            VectorRoomMediasSender.this.sendJpegImage(roomMediaMessage, openResource);
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void sendTextMessage(RoomMediaMessage roomMediaMessage) {
        final CharSequence text = roomMediaMessage.getText();
        final String htmlText = roomMediaMessage.getHtmlText();
        if (TextUtils.isEmpty(text) || htmlText != null) {
            final String str = null;
            if (text != null) {
                str = text.toString();
            } else if (htmlText != null) {
                str = Html.fromHtml(htmlText).toString();
            }
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("sendTextMessage ");
            sb.append(str);
            Log.m209d(str2, sb.toString());
            this.mVectorRoomActivity.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomMediasSender.this.mVectorRoomActivity.sendMessage(str, htmlText, Message.FORMAT_MATRIX_HTML);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    VectorRoomMediasSender.this.mVectorRoomActivity.insertTextInTextEditor(text.toString());
                }
            });
        }
        if (this.mSharedDataItems.size() > 0) {
            this.mSharedDataItems.remove(0);
        }
        sendMedias();
    }

    /* access modifiers changed from: private */
    public void sendJpegImage(final RoomMediaMessage roomMediaMessage, Resource resource) {
        final String mimeType = roomMediaMessage.getMimeType(this.mVectorRoomActivity);
        final String saveMedia = this.mMediasCache.saveMedia(resource.mContentStream, null, mimeType);
        resource.close();
        this.mVectorRoomActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (VectorRoomMediasSender.this.mSharedDataItems != null && VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                    VectorRoomMediasSender.this.sendJpegImage(roomMediaMessage, saveMedia, mimeType, new OnImageUploadListener() {
                        public void onDone() {
                            if (VectorRoomMediasSender.this.mSharedDataItems != null && VectorRoomMediasSender.this.mSharedDataItems.size() > 0) {
                                VectorRoomMediasSender.this.mSharedDataItems.remove(0);
                            }
                            VectorRoomMediasSender.this.sendMedias();
                        }

                        public void onCancel() {
                            if (VectorRoomMediasSender.this.mSharedDataItems != null) {
                                VectorRoomMediasSender.this.mSharedDataItems.clear();
                            }
                            VectorRoomMediasSender.this.sendMedias();
                        }
                    });
                }
            }
        });
    }

    private ImageCompressionSizes computeImageSizes(int i, int i2) {
        ImageCompressionSizes imageCompressionSizes = new ImageCompressionSizes();
        imageCompressionSizes.mFullImageSize = new ImageSize(i, i2);
        int i3 = i2 > i ? i2 : i;
        if (i3 > 512) {
            if (i3 > 2048) {
                imageCompressionSizes.mLargeImageSize = imageCompressionSizes.mFullImageSize.computeSizeToFit(2048.0f);
                if (imageCompressionSizes.mLargeImageSize.mWidth == i && imageCompressionSizes.mLargeImageSize.mHeight == i2) {
                    imageCompressionSizes.mLargeImageSize = null;
                }
            }
            if (i3 > 1024) {
                imageCompressionSizes.mMediumImageSize = imageCompressionSizes.mFullImageSize.computeSizeToFit(1024.0f);
                if (imageCompressionSizes.mMediumImageSize.mWidth == i && imageCompressionSizes.mMediumImageSize.mHeight == i2) {
                    imageCompressionSizes.mMediumImageSize = null;
                }
            }
            if (i3 > 512) {
                imageCompressionSizes.mSmallImageSize = imageCompressionSizes.mFullImageSize.computeSizeToFit(512.0f);
                if (imageCompressionSizes.mSmallImageSize.mWidth == i && imageCompressionSizes.mSmallImageSize.mHeight == i2) {
                    imageCompressionSizes.mSmallImageSize = null;
                }
            }
        }
        return imageCompressionSizes;
    }

    private static int estimateFileSize(ImageSize imageSize) {
        if (imageSize != null) {
            return ((((imageSize.mWidth * imageSize.mHeight) * 2) / 10) / 1024) * 1024;
        }
        return 0;
    }

    private static void addDialogEntry(Context context, ArrayList<String> arrayList, String str, ImageSize imageSize, int i) {
        if (imageSize != null && arrayList != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(": ");
            sb.append(Formatter.formatFileSize(context, (long) i));
            sb.append(" (");
            sb.append(imageSize.mWidth);
            sb.append("x");
            sb.append(imageSize.mHeight);
            sb.append(")");
            arrayList.add(sb.toString());
        }
    }

    private static String[] getImagesCompressionTextsList(Context context, ImageCompressionSizes imageCompressionSizes, int i) {
        ArrayList arrayList = new ArrayList();
        addDialogEntry(context, arrayList, context.getString(C1299R.string.compression_opt_list_original), imageCompressionSizes.mFullImageSize, i);
        addDialogEntry(context, arrayList, context.getString(C1299R.string.compression_opt_list_large), imageCompressionSizes.mLargeImageSize, Math.min(estimateFileSize(imageCompressionSizes.mLargeImageSize), i));
        addDialogEntry(context, arrayList, context.getString(C1299R.string.compression_opt_list_medium), imageCompressionSizes.mMediumImageSize, Math.min(estimateFileSize(imageCompressionSizes.mMediumImageSize), i));
        addDialogEntry(context, arrayList, context.getString(C1299R.string.compression_opt_list_small), imageCompressionSizes.mSmallImageSize, Math.min(estimateFileSize(imageCompressionSizes.mSmallImageSize), i));
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0059 A[Catch:{ Exception -> 0x0068 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x006c A[Catch:{ Exception -> 0x0068 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String resizeImage(java.lang.String r4, java.lang.String r5, com.opengarden.firechat.util.VectorRoomMediasSender.ImageSize r6, com.opengarden.firechat.util.VectorRoomMediasSender.ImageSize r7, int r8) {
        /*
            r3 = this;
            if (r7 == 0) goto L_0x006a
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0068 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0068 }
            r1.<init>(r5)     // Catch:{ Exception -> 0x0068 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0068 }
            r5 = -1
            r1 = 0
            int r6 = r6.mWidth     // Catch:{ OutOfMemoryError -> 0x003b, Exception -> 0x001f }
            int r2 = r7.mWidth     // Catch:{ OutOfMemoryError -> 0x003b, Exception -> 0x001f }
            int r6 = r6 + r2
            int r6 = r6 + -1
            int r7 = r7.mWidth     // Catch:{ OutOfMemoryError -> 0x003b, Exception -> 0x001f }
            int r6 = r6 / r7
            r7 = 75
            java.io.InputStream r5 = com.opengarden.firechat.matrixsdk.util.ImageUtils.resizeImage(r0, r5, r6, r7)     // Catch:{ OutOfMemoryError -> 0x003b, Exception -> 0x001f }
            goto L_0x0057
        L_0x001f:
            r5 = move-exception
            java.lang.String r6 = LOG_TAG     // Catch:{ Exception -> 0x0068 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0068 }
            r7.<init>()     // Catch:{ Exception -> 0x0068 }
            java.lang.String r0 = "resizeImage failed : "
            r7.append(r0)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ Exception -> 0x0068 }
            r7.append(r5)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r5 = r7.toString()     // Catch:{ Exception -> 0x0068 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)     // Catch:{ Exception -> 0x0068 }
            goto L_0x0056
        L_0x003b:
            r5 = move-exception
            java.lang.String r6 = LOG_TAG     // Catch:{ Exception -> 0x0068 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0068 }
            r7.<init>()     // Catch:{ Exception -> 0x0068 }
            java.lang.String r0 = "resizeImage out of memory : "
            r7.append(r0)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ Exception -> 0x0068 }
            r7.append(r5)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r5 = r7.toString()     // Catch:{ Exception -> 0x0068 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)     // Catch:{ Exception -> 0x0068 }
        L_0x0056:
            r5 = r1
        L_0x0057:
            if (r5 == 0) goto L_0x006a
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r6 = r3.mMediasCache     // Catch:{ Exception -> 0x0068 }
            java.lang.String r7 = "image/jpeg"
            java.lang.String r6 = r6.saveMedia(r5, r1, r7)     // Catch:{ Exception -> 0x0068 }
            if (r6 == 0) goto L_0x0064
            r4 = r6
        L_0x0064:
            r5.close()     // Catch:{ Exception -> 0x0068 }
            goto L_0x006a
        L_0x0068:
            r5 = move-exception
            goto L_0x0074
        L_0x006a:
            if (r8 == 0) goto L_0x008e
            com.opengarden.firechat.activity.VectorRoomActivity r5 = r3.mVectorRoomActivity     // Catch:{ Exception -> 0x0068 }
            com.opengarden.firechat.matrixsdk.db.MXMediasCache r6 = r3.mMediasCache     // Catch:{ Exception -> 0x0068 }
            com.opengarden.firechat.matrixsdk.util.ImageUtils.rotateImage(r5, r4, r8, r6)     // Catch:{ Exception -> 0x0068 }
            goto L_0x008e
        L_0x0074:
            java.lang.String r6 = LOG_TAG
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "resizeImage "
            r7.append(r8)
            java.lang.String r5 = r5.getMessage()
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r6, r5)
        L_0x008e:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.VectorRoomMediasSender.resizeImage(java.lang.String, java.lang.String, com.opengarden.firechat.util.VectorRoomMediasSender$ImageSize, com.opengarden.firechat.util.VectorRoomMediasSender$ImageSize, int):java.lang.String");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x011a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendJpegImage(com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r18, java.lang.String r19, java.lang.String r20, com.opengarden.firechat.util.VectorRoomMediasSender.OnImageUploadListener r21) {
        /*
            r17 = this;
            r9 = r17
            r10 = r18
            r1 = r20
            r11 = r21
            if (r19 == 0) goto L_0x0125
            if (r11 != 0) goto L_0x000e
            goto L_0x0125
        L_0x000e:
            r2 = 0
            java.lang.String r3 = "image/jpeg"
            boolean r3 = r3.equals(r1)
            if (r3 != 0) goto L_0x0027
            java.lang.String r3 = "image/jpg"
            boolean r3 = r3.equals(r1)
            if (r3 != 0) goto L_0x0027
            java.lang.String r3 = "image/*"
            boolean r1 = r3.equals(r1)
            if (r1 == 0) goto L_0x0118
        L_0x0027:
            java.lang.System.gc()
            r12 = 1
            android.net.Uri r1 = android.net.Uri.parse(r19)     // Catch:{ Exception -> 0x00fc }
            java.lang.String r5 = r1.getPath()     // Catch:{ Exception -> 0x00fc }
            com.opengarden.firechat.activity.VectorRoomActivity r3 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00fc }
            int r6 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getRotationAngleForBitmap(r3, r1)     // Catch:{ Exception -> 0x00fc }
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x00fc }
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x00fc }
            r3.<init>(r5)     // Catch:{ Exception -> 0x00fc }
            r1.<init>(r3)     // Catch:{ Exception -> 0x00fc }
            int r3 = r1.available()     // Catch:{ Exception -> 0x00fc }
            android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x00fc }
            r7.<init>()     // Catch:{ Exception -> 0x00fc }
            r7.inJustDecodeBounds = r12     // Catch:{ Exception -> 0x00fc }
            android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ Exception -> 0x00fc }
            r7.inPreferredConfig = r8     // Catch:{ Exception -> 0x00fc }
            r13 = -1
            r7.outWidth = r13     // Catch:{ Exception -> 0x00fc }
            r7.outHeight = r13     // Catch:{ Exception -> 0x00fc }
            r8 = 0
            android.graphics.BitmapFactory.decodeStream(r1, r8, r7)     // Catch:{ OutOfMemoryError -> 0x005c }
            goto L_0x0077
        L_0x005c:
            r0 = move-exception
            java.lang.String r8 = LOG_TAG     // Catch:{ Exception -> 0x00fc }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00fc }
            r14.<init>()     // Catch:{ Exception -> 0x00fc }
            java.lang.String r15 = "sendImageMessage out of memory error : "
            r14.append(r15)     // Catch:{ Exception -> 0x00fc }
            java.lang.String r15 = r0.getMessage()     // Catch:{ Exception -> 0x00fc }
            r14.append(r15)     // Catch:{ Exception -> 0x00fc }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x00fc }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r14)     // Catch:{ Exception -> 0x00fc }
        L_0x0077:
            int r8 = r7.outWidth     // Catch:{ Exception -> 0x00fc }
            int r7 = r7.outHeight     // Catch:{ Exception -> 0x00fc }
            com.opengarden.firechat.util.VectorRoomMediasSender$ImageCompressionSizes r7 = r9.computeImageSizes(r8, r7)     // Catch:{ Exception -> 0x00fc }
            r1.close()     // Catch:{ Exception -> 0x00fc }
            java.lang.String r1 = r9.mImageCompressionDescription     // Catch:{ Exception -> 0x00fc }
            if (r1 == 0) goto L_0x00ab
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            java.lang.String r2 = r9.mImageCompressionDescription     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.util.VectorRoomMediasSender$ImageSize r8 = r7.getImageSize(r1, r2)     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.util.VectorRoomMediasSender$ImageSize r7 = r7.mFullImageSize     // Catch:{ Exception -> 0x00a7 }
            r1 = r9
            r2 = r19
            r3 = r5
            r4 = r7
            r5 = r8
            java.lang.String r1 = r1.resizeImage(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.activity.VectorRoomActivity r2 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.util.VectorRoomMediasSender$7 r3 = new com.opengarden.firechat.util.VectorRoomMediasSender$7     // Catch:{ Exception -> 0x00a7 }
            r3.<init>(r1, r10, r11)     // Catch:{ Exception -> 0x00a7 }
            r2.runOnUiThread(r3)     // Catch:{ Exception -> 0x00a7 }
        L_0x00a4:
            r2 = 1
            goto L_0x0118
        L_0x00a7:
            r0 = move-exception
            r1 = r0
            r2 = 1
            goto L_0x00fe
        L_0x00ab:
            com.opengarden.firechat.util.VectorRoomMediasSender$ImageSize r1 = r7.mSmallImageSize     // Catch:{ Exception -> 0x00fc }
            if (r1 == 0) goto L_0x0118
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            android.support.v4.app.FragmentManager r1 = r1.getSupportFragmentManager()     // Catch:{ Exception -> 0x00a7 }
            java.lang.String r2 = "TAG_FRAGMENT_IMAGE_SIZE_DIALOG"
            android.support.v4.app.Fragment r1 = r1.findFragmentByTag(r2)     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.fragments.ImageSizeSelectionDialogFragment r1 = (com.opengarden.firechat.fragments.ImageSizeSelectionDialogFragment) r1     // Catch:{ Exception -> 0x00a7 }
            if (r1 == 0) goto L_0x00c2
            r1.dismissAllowingStateLoss()     // Catch:{ Exception -> 0x00a7 }
        L_0x00c2:
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            java.lang.String[] r14 = getImagesCompressionTextsList(r1, r7, r3)     // Catch:{ Exception -> 0x00a7 }
            android.app.AlertDialog$Builder r15 = new android.app.AlertDialog$Builder     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            r15.<init>(r1)     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity     // Catch:{ Exception -> 0x00a7 }
            r2 = 2131689662(0x7f0f00be, float:1.9008346E38)
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x00a7 }
            r15.setTitle(r1)     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.util.VectorRoomMediasSender$8 r8 = new com.opengarden.firechat.util.VectorRoomMediasSender$8     // Catch:{ Exception -> 0x00a7 }
            r1 = r8
            r2 = r9
            r3 = r7
            r4 = r19
            r7 = r10
            r12 = r8
            r8 = r11
            r1.<init>(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00a7 }
            r15.setSingleChoiceItems(r14, r13, r12)     // Catch:{ Exception -> 0x00a7 }
            android.app.AlertDialog r1 = r15.show()     // Catch:{ Exception -> 0x00a7 }
            r9.mImageSizesListDialog = r1     // Catch:{ Exception -> 0x00a7 }
            android.app.AlertDialog r1 = r9.mImageSizesListDialog     // Catch:{ Exception -> 0x00a7 }
            com.opengarden.firechat.util.VectorRoomMediasSender$9 r2 = new com.opengarden.firechat.util.VectorRoomMediasSender$9     // Catch:{ Exception -> 0x00a7 }
            r2.<init>(r11)     // Catch:{ Exception -> 0x00a7 }
            r1.setOnCancelListener(r2)     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00a4
        L_0x00fc:
            r0 = move-exception
            r1 = r0
        L_0x00fe:
            java.lang.String r3 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "sendImageMessage failed "
            r4.append(r5)
            java.lang.String r1 = r1.getMessage()
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r3, r1)
        L_0x0118:
            if (r2 != 0) goto L_0x0124
            com.opengarden.firechat.activity.VectorRoomActivity r1 = r9.mVectorRoomActivity
            com.opengarden.firechat.util.VectorRoomMediasSender$10 r2 = new com.opengarden.firechat.util.VectorRoomMediasSender$10
            r2.<init>(r10, r11)
            r1.runOnUiThread(r2)
        L_0x0124:
            return
        L_0x0125:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.VectorRoomMediasSender.sendJpegImage(com.opengarden.firechat.matrixsdk.data.RoomMediaMessage, java.lang.String, java.lang.String, com.opengarden.firechat.util.VectorRoomMediasSender$OnImageUploadListener):void");
    }
}
