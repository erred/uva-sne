package com.opengarden.firechat.matrixsdk.data;

import android.content.ClipData.Item;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.MimeTypeMap;
import com.facebook.common.util.UriUtil;
import com.opengarden.firechat.matrixsdk.listeners.IMXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class RoomMediaMessage implements Parcelable {
    public static final Creator CREATOR = new Creator() {
        public RoomMediaMessage createFromParcel(Parcel parcel) {
            return new RoomMediaMessage(parcel);
        }

        public RoomMediaMessage[] newArray(int i) {
            return new RoomMediaMessage[i];
        }
    };
    private static final String LOG_TAG = "RoomMediaMessage";
    private static final Uri mDummyUri = Uri.parse("http://www.matrixdummy.org");
    private Item mClipDataItem;
    private Event mEvent;
    private transient EventCreationListener mEventCreationListener;
    private transient ApiCallback<Void> mEventSendingCallback;
    private String mFileName;
    private transient IMXMediaUploadListener mMediaUploadListener;
    private String mMessageType;
    private String mMimeType;
    @Nullable
    private Event mReplyToEvent;
    private Pair<Integer, Integer> mThumbnailSize;
    private Uri mUri;

    public interface EventCreationListener {
        void onEncryptionFailed(RoomMediaMessage roomMediaMessage);

        void onEventCreated(RoomMediaMessage roomMediaMessage);

        void onEventCreationFailed(RoomMediaMessage roomMediaMessage, String str);
    }

    public int describeContents() {
        return 0;
    }

    public RoomMediaMessage(Item item, String str) {
        this.mThumbnailSize = new Pair<>(Integer.valueOf(100), Integer.valueOf(100));
        this.mClipDataItem = item;
        this.mMimeType = str;
    }

    public RoomMediaMessage(CharSequence charSequence, String str, String str2) {
        this.mThumbnailSize = new Pair<>(Integer.valueOf(100), Integer.valueOf(100));
        this.mClipDataItem = new Item(charSequence, str);
        if (str == null) {
            str2 = "text/plain";
        }
        this.mMimeType = str2;
    }

    public RoomMediaMessage(Uri uri) {
        this(uri, (String) null);
    }

    public RoomMediaMessage(Uri uri, String str) {
        this.mThumbnailSize = new Pair<>(Integer.valueOf(100), Integer.valueOf(100));
        this.mUri = uri;
        this.mFileName = str;
    }

    public RoomMediaMessage(Event event) {
        this.mThumbnailSize = new Pair<>(Integer.valueOf(100), Integer.valueOf(100));
        setEvent(event);
        Message message = JsonUtils.toMessage(event.getContent());
        if (message != null) {
            setMessageType(message.msgtype);
        }
    }

    private RoomMediaMessage(Parcel parcel) {
        this.mThumbnailSize = new Pair<>(Integer.valueOf(100), Integer.valueOf(100));
        this.mUri = unformatNullUri((Uri) parcel.readParcelable(Uri.class.getClassLoader()));
        this.mMimeType = unformatNullString(parcel.readString());
        String unformatNullString = unformatNullString(parcel.readString());
        String unformatNullString2 = unformatNullString(parcel.readString());
        Uri unformatNullUri = unformatNullUri((Uri) parcel.readParcelable(Uri.class.getClassLoader()));
        if (!TextUtils.isEmpty(unformatNullString) || !TextUtils.isEmpty(unformatNullString2) || unformatNullUri != null) {
            this.mClipDataItem = new Item(unformatNullString, unformatNullString2, null, unformatNullUri);
        }
        this.mFileName = unformatNullString(parcel.readString());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append("mUri ");
        sb.append(this.mUri);
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(" -- mMimeType ");
        sb3.append(this.mMimeType);
        String sb4 = sb3.toString();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(sb4);
        sb5.append(" -- mEvent ");
        sb5.append(this.mEvent);
        String sb6 = sb5.toString();
        StringBuilder sb7 = new StringBuilder();
        sb7.append(sb6);
        sb7.append(" -- mClipDataItem ");
        sb7.append(this.mClipDataItem);
        String sb8 = sb7.toString();
        StringBuilder sb9 = new StringBuilder();
        sb9.append(sb8);
        sb9.append(" -- mFileName ");
        sb9.append(this.mFileName);
        String sb10 = sb9.toString();
        StringBuilder sb11 = new StringBuilder();
        sb11.append(sb10);
        sb11.append(" -- mMessageType ");
        sb11.append(this.mMessageType);
        String sb12 = sb11.toString();
        StringBuilder sb13 = new StringBuilder();
        sb13.append(sb12);
        sb13.append(" -- mThumbnailSize ");
        sb13.append(this.mThumbnailSize);
        return sb13.toString();
    }

    private static String unformatNullString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str;
    }

    private static Uri unformatNullUri(Uri uri) {
        if (uri == null || mDummyUri.equals(uri)) {
            return null;
        }
        return uri;
    }

    private static String formatNullString(String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }

    private static String formatNullString(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return "";
        }
        return charSequence.toString();
    }

    private static Uri formatNullUri(Uri uri) {
        return uri == null ? mDummyUri : uri;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(formatNullUri(this.mUri), 0);
        parcel.writeString(formatNullString(this.mMimeType));
        if (this.mClipDataItem == null) {
            parcel.writeString("");
            parcel.writeString("");
            parcel.writeParcelable(formatNullUri(null), 0);
        } else {
            parcel.writeString(formatNullString(this.mClipDataItem.getText()));
            parcel.writeString(formatNullString(this.mClipDataItem.getHtmlText()));
            parcel.writeParcelable(formatNullUri(this.mClipDataItem.getUri()), 0);
        }
        parcel.writeString(formatNullString(this.mFileName));
    }

    public void setMessageType(String str) {
        this.mMessageType = str;
    }

    public String getMessageType() {
        return this.mMessageType;
    }

    public void setReplyToEvent(@Nullable Event event) {
        this.mReplyToEvent = event;
    }

    @Nullable
    public Event getReplyToEvent() {
        return this.mReplyToEvent;
    }

    public void setEvent(Event event) {
        this.mEvent = event;
    }

    public Event getEvent() {
        return this.mEvent;
    }

    public void setThumbnailSize(Pair<Integer, Integer> pair) {
        this.mThumbnailSize = pair;
    }

    public Pair<Integer, Integer> getThumbnailSize() {
        return this.mThumbnailSize;
    }

    public void setMediaUploadListener(IMXMediaUploadListener iMXMediaUploadListener) {
        this.mMediaUploadListener = iMXMediaUploadListener;
    }

    public IMXMediaUploadListener getMediaUploadListener() {
        return this.mMediaUploadListener;
    }

    public void setEventSendingCallback(ApiCallback<Void> apiCallback) {
        this.mEventSendingCallback = apiCallback;
    }

    public ApiCallback<Void> getSendingCallback() {
        return this.mEventSendingCallback;
    }

    public void setEventCreationListener(EventCreationListener eventCreationListener) {
        this.mEventCreationListener = eventCreationListener;
    }

    public EventCreationListener getEventCreationListener() {
        return this.mEventCreationListener;
    }

    public CharSequence getText() {
        if (this.mClipDataItem != null) {
            return this.mClipDataItem.getText();
        }
        return null;
    }

    public String getHtmlText() {
        if (this.mClipDataItem != null) {
            return this.mClipDataItem.getHtmlText();
        }
        return null;
    }

    public Intent getIntent() {
        if (this.mClipDataItem != null) {
            return this.mClipDataItem.getIntent();
        }
        return null;
    }

    public Uri getUri() {
        if (this.mUri != null) {
            return this.mUri;
        }
        if (this.mClipDataItem != null) {
            return this.mClipDataItem.getUri();
        }
        return null;
    }

    public String getMimeType(Context context) {
        if (this.mMimeType == null && getUri() != null) {
            try {
                Uri uri = getUri();
                this.mMimeType = context.getContentResolver().getType(uri);
                if (this.mMimeType == null) {
                    String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(uri.toString().toLowerCase());
                    if (fileExtensionFromUrl != null) {
                        this.mMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
                    }
                }
                if (this.mMimeType != null) {
                    this.mMimeType = this.mMimeType.toLowerCase();
                }
            } catch (Exception e) {
                Log.m212e(LOG_TAG, "Failed to open resource input stream", e);
            }
        }
        return this.mMimeType;
    }

    public Bitmap getMiniKindImageThumbnail(Context context) {
        return getImageThumbnail(context, 1);
    }

    public Bitmap getFullScreenImageKindThumbnail(Context context) {
        return getImageThumbnail(context, 2);
    }

    private Bitmap getImageThumbnail(Context context, int i) {
        Long l;
        Bitmap bitmap = null;
        if (getMimeType(context) == null || !getMimeType(context).startsWith("image/")) {
            return null;
        }
        try {
            ContentResolver contentResolver = context.getContentResolver();
            List pathSegments = getUri().getPathSegments();
            String str = (String) pathSegments.get(pathSegments.size() - 1);
            if (str.startsWith("image:")) {
                str = str.substring("image:".length());
            }
            try {
                l = Long.valueOf(Long.parseLong(str));
            } catch (Exception unused) {
                l = null;
            }
            if (l != null) {
                bitmap = Thumbnails.getThumbnail(contentResolver, l.longValue(), i, null);
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("MediaStore.Images.Thumbnails.getThumbnail ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
        return bitmap;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0041, code lost:
        if (r9 != null) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        r9.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0066, code lost:
        if (r9 != null) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006f, code lost:
        if (android.text.TextUtils.isEmpty(r8.mFileName) == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0071, code lost:
        r9 = r0.getPathSegments();
        r8.mFileName = (java.lang.String) r9.get(r9.size() - 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x009e, code lost:
        r8.mFileName = null;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0087 A[Catch:{ Exception -> 0x009e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getFileName(android.content.Context r9) {
        /*
            r8 = this;
            java.lang.String r0 = r8.mFileName
            if (r0 != 0) goto L_0x00a0
            android.net.Uri r0 = r8.getUri()
            if (r0 == 0) goto L_0x00a0
            android.net.Uri r0 = r8.getUri()
            if (r0 == 0) goto L_0x00a0
            r7 = 0
            java.lang.String r1 = r0.toString()     // Catch:{ Exception -> 0x009e }
            java.lang.String r2 = "content://"
            boolean r1 = r1.startsWith(r2)     // Catch:{ Exception -> 0x009e }
            if (r1 == 0) goto L_0x008b
            android.content.ContentResolver r1 = r9.getContentResolver()     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r0
            android.database.Cursor r9 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x004a, all -> 0x0047 }
            if (r9 == 0) goto L_0x0041
            boolean r1 = r9.moveToFirst()     // Catch:{ Exception -> 0x003f }
            if (r1 == 0) goto L_0x0041
            java.lang.String r1 = "_display_name"
            int r1 = r9.getColumnIndex(r1)     // Catch:{ Exception -> 0x003f }
            java.lang.String r1 = r9.getString(r1)     // Catch:{ Exception -> 0x003f }
            r8.mFileName = r1     // Catch:{ Exception -> 0x003f }
            goto L_0x0041
        L_0x003f:
            r1 = move-exception
            goto L_0x004c
        L_0x0041:
            if (r9 == 0) goto L_0x0069
        L_0x0043:
            r9.close()     // Catch:{ Exception -> 0x009e }
            goto L_0x0069
        L_0x0047:
            r0 = move-exception
            r9 = r7
            goto L_0x0085
        L_0x004a:
            r1 = move-exception
            r9 = r7
        L_0x004c:
            java.lang.String r2 = LOG_TAG     // Catch:{ all -> 0x0084 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0084 }
            r3.<init>()     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = "cursor.getString "
            r3.append(r4)     // Catch:{ all -> 0x0084 }
            java.lang.String r1 = r1.getMessage()     // Catch:{ all -> 0x0084 }
            r3.append(r1)     // Catch:{ all -> 0x0084 }
            java.lang.String r1 = r3.toString()     // Catch:{ all -> 0x0084 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)     // Catch:{ all -> 0x0084 }
            if (r9 == 0) goto L_0x0069
            goto L_0x0043
        L_0x0069:
            java.lang.String r9 = r8.mFileName     // Catch:{ Exception -> 0x009e }
            boolean r9 = android.text.TextUtils.isEmpty(r9)     // Catch:{ Exception -> 0x009e }
            if (r9 == 0) goto L_0x00a0
            java.util.List r9 = r0.getPathSegments()     // Catch:{ Exception -> 0x009e }
            int r0 = r9.size()     // Catch:{ Exception -> 0x009e }
            int r0 = r0 + -1
            java.lang.Object r9 = r9.get(r0)     // Catch:{ Exception -> 0x009e }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x009e }
            r8.mFileName = r9     // Catch:{ Exception -> 0x009e }
            goto L_0x00a0
        L_0x0084:
            r0 = move-exception
        L_0x0085:
            if (r9 == 0) goto L_0x008a
            r9.close()     // Catch:{ Exception -> 0x009e }
        L_0x008a:
            throw r0     // Catch:{ Exception -> 0x009e }
        L_0x008b:
            java.lang.String r9 = r0.toString()     // Catch:{ Exception -> 0x009e }
            java.lang.String r1 = "file://"
            boolean r9 = r9.startsWith(r1)     // Catch:{ Exception -> 0x009e }
            if (r9 == 0) goto L_0x00a0
            java.lang.String r9 = r0.getLastPathSegment()     // Catch:{ Exception -> 0x009e }
            r8.mFileName = r9     // Catch:{ Exception -> 0x009e }
            goto L_0x00a0
        L_0x009e:
            r8.mFileName = r7
        L_0x00a0:
            java.lang.String r9 = r8.mFileName
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.RoomMediaMessage.getFileName(android.content.Context):java.lang.String");
    }

    public void saveMedia(Context context, File file) {
        this.mFileName = null;
        Uri uri = getUri();
        if (uri != null) {
            try {
                Resource openResource = ResourceUtils.openResource(context, uri, getMimeType(context));
                if (openResource == null) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## saveMedia : Fail to retrieve the resource ");
                    sb.append(uri);
                    Log.m211e(str, sb.toString());
                    return;
                }
                this.mUri = saveFile(file, openResource.mContentStream, getFileName(context), openResource.mMimeType);
                openResource.mContentStream.close();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## saveMedia : failed ");
                sb2.append(e.getMessage());
                Log.m211e(str2, sb2.toString());
            }
        }
    }

    private static Uri saveFile(File file, InputStream inputStream, String str, String str2) {
        if (str == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(UriUtil.LOCAL_FILE_SCHEME);
            sb.append(System.currentTimeMillis());
            str = sb.toString();
            if (str2 != null) {
                String extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(str2);
                if (extensionFromMimeType != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(str);
                    sb2.append(".");
                    sb2.append(extensionFromMimeType);
                    str = sb2.toString();
                }
            }
        }
        try {
            File file2 = new File(file, str);
            if (file2.exists()) {
                file2.delete();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file2.getPath());
            try {
                byte[] bArr = new byte[32768];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("## saveFile failed ");
                sb3.append(e.getMessage());
                Log.m211e(str3, sb3.toString());
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            return Uri.fromFile(file2);
        } catch (Exception e2) {
            String str4 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## saveFile failed ");
            sb4.append(e2.getMessage());
            Log.m211e(str4, sb4.toString());
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void onEventCreated() {
        if (getEventCreationListener() != null) {
            try {
                getEventCreationListener().onEventCreated(this);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onEventCreated() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        this.mEventCreationListener = null;
    }

    /* access modifiers changed from: 0000 */
    public void onEventCreationFailed(String str) {
        if (getEventCreationListener() != null) {
            try {
                getEventCreationListener().onEventCreationFailed(this, str);
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onEventCreationFailed() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        this.mMediaUploadListener = null;
        this.mEventSendingCallback = null;
        this.mEventCreationListener = null;
    }

    /* access modifiers changed from: 0000 */
    public void onEncryptionFailed() {
        if (getEventCreationListener() != null) {
            try {
                getEventCreationListener().onEncryptionFailed(this);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onEncryptionFailed() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        this.mMediaUploadListener = null;
        this.mEventSendingCallback = null;
        this.mEventCreationListener = null;
    }

    public static List<RoomMediaMessage> listRoomMediaMessages(Intent intent) {
        return listRoomMediaMessages(intent, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00bf, code lost:
        if (((java.lang.String) r8.get(0)).endsWith("/*") != false) goto L_0x00c1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00c9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<com.opengarden.firechat.matrixsdk.data.RoomMediaMessage> listRoomMediaMessages(android.content.Intent r8, java.lang.ClassLoader r9) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            if (r8 == 0) goto L_0x016b
            java.lang.String r1 = r8.getType()
            java.lang.String r2 = "text/plain"
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            r2 = 0
            if (r1 == 0) goto L_0x006f
            java.lang.String r1 = "android.intent.extra.TEXT"
            java.lang.String r1 = r8.getStringExtra(r1)
            if (r1 != 0) goto L_0x0028
            java.lang.String r3 = "android.intent.extra.TEXT"
            java.lang.CharSequence r3 = r8.getCharSequenceExtra(r3)
            if (r3 == 0) goto L_0x0028
            java.lang.String r1 = r3.toString()
        L_0x0028:
            java.lang.String r3 = "android.intent.extra.SUBJECT"
            java.lang.String r3 = r8.getStringExtra(r3)
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 != 0) goto L_0x005c
            boolean r4 = android.text.TextUtils.isEmpty(r1)
            if (r4 == 0) goto L_0x003c
            r1 = r3
            goto L_0x005c
        L_0x003c:
            java.util.regex.Pattern r4 = android.util.Patterns.WEB_URL
            java.util.regex.Matcher r4 = r4.matcher(r1)
            boolean r4 = r4.matches()
            if (r4 == 0) goto L_0x005c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = "\n"
            r4.append(r3)
            r4.append(r1)
            java.lang.String r1 = r4.toString()
        L_0x005c:
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L_0x006f
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r9 = new com.opengarden.firechat.matrixsdk.data.RoomMediaMessage
            java.lang.String r8 = r8.getType()
            r9.<init>(r1, r2, r8)
            r0.add(r9)
            return r0
        L_0x006f:
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r1 < r3) goto L_0x007a
            android.content.ClipData r1 = r8.getClipData()
            goto L_0x007b
        L_0x007a:
            r1 = r2
        L_0x007b:
            if (r1 == 0) goto L_0x00f6
            android.content.ClipDescription r8 = r1.getDescription()
            r9 = 0
            if (r8 == 0) goto L_0x00c1
            android.content.ClipDescription r8 = r1.getDescription()
            int r8 = r8.getMimeTypeCount()
            if (r8 == 0) goto L_0x00c1
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r3 = 0
        L_0x0094:
            android.content.ClipDescription r4 = r1.getDescription()
            int r4 = r4.getMimeTypeCount()
            if (r3 >= r4) goto L_0x00ac
            android.content.ClipDescription r4 = r1.getDescription()
            java.lang.String r4 = r4.getMimeType(r3)
            r8.add(r4)
            int r3 = r3 + 1
            goto L_0x0094
        L_0x00ac:
            int r3 = r8.size()
            r4 = 1
            if (r4 != r3) goto L_0x00c2
            java.lang.Object r3 = r8.get(r9)
            java.lang.String r3 = (java.lang.String) r3
            java.lang.String r4 = "/*"
            boolean r3 = r3.endsWith(r4)
            if (r3 == 0) goto L_0x00c2
        L_0x00c1:
            r8 = r2
        L_0x00c2:
            int r3 = r1.getItemCount()
            r4 = 0
        L_0x00c7:
            if (r4 >= r3) goto L_0x016b
            android.content.ClipData$Item r5 = r1.getItemAt(r4)
            if (r8 == 0) goto L_0x00ea
            int r6 = r8.size()
            if (r4 >= r6) goto L_0x00dc
            java.lang.Object r6 = r8.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            goto L_0x00e2
        L_0x00dc:
            java.lang.Object r6 = r8.get(r9)
            java.lang.String r6 = (java.lang.String) r6
        L_0x00e2:
            java.lang.String r7 = "text/uri-list"
            boolean r7 = android.text.TextUtils.equals(r6, r7)
            if (r7 == 0) goto L_0x00eb
        L_0x00ea:
            r6 = r2
        L_0x00eb:
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r7 = new com.opengarden.firechat.matrixsdk.data.RoomMediaMessage
            r7.<init>(r5, r6)
            r0.add(r7)
            int r4 = r4 + 1
            goto L_0x00c7
        L_0x00f6:
            android.net.Uri r1 = r8.getData()
            if (r1 == 0) goto L_0x0109
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r9 = new com.opengarden.firechat.matrixsdk.data.RoomMediaMessage
            android.net.Uri r8 = r8.getData()
            r9.<init>(r8)
            r0.add(r9)
            goto L_0x016b
        L_0x0109:
            android.os.Bundle r8 = r8.getExtras()
            if (r8 == 0) goto L_0x016b
            if (r9 == 0) goto L_0x011a
            java.lang.Class<com.opengarden.firechat.matrixsdk.data.RoomMediaMessage> r9 = com.opengarden.firechat.matrixsdk.data.RoomMediaMessage.class
            java.lang.ClassLoader r9 = r9.getClassLoader()
            r8.setClassLoader(r9)
        L_0x011a:
            java.lang.String r9 = "android.intent.extra.STREAM"
            boolean r9 = r8.containsKey(r9)
            if (r9 == 0) goto L_0x016b
            java.lang.String r9 = "android.intent.extra.STREAM"
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0164 }
            boolean r9 = r8 instanceof android.net.Uri     // Catch:{ Exception -> 0x0164 }
            if (r9 == 0) goto L_0x0137
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r9 = new com.opengarden.firechat.matrixsdk.data.RoomMediaMessage     // Catch:{ Exception -> 0x0164 }
            android.net.Uri r8 = (android.net.Uri) r8     // Catch:{ Exception -> 0x0164 }
            r9.<init>(r8)     // Catch:{ Exception -> 0x0164 }
            r0.add(r9)     // Catch:{ Exception -> 0x0164 }
            goto L_0x016b
        L_0x0137:
            boolean r9 = r8 instanceof java.util.List     // Catch:{ Exception -> 0x0164 }
            if (r9 == 0) goto L_0x016b
            java.util.List r8 = (java.util.List) r8     // Catch:{ Exception -> 0x0164 }
            java.util.Iterator r8 = r8.iterator()     // Catch:{ Exception -> 0x0164 }
        L_0x0141:
            boolean r9 = r8.hasNext()     // Catch:{ Exception -> 0x0164 }
            if (r9 == 0) goto L_0x016b
            java.lang.Object r9 = r8.next()     // Catch:{ Exception -> 0x0164 }
            boolean r1 = r9 instanceof android.net.Uri     // Catch:{ Exception -> 0x0164 }
            if (r1 == 0) goto L_0x015a
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r1 = new com.opengarden.firechat.matrixsdk.data.RoomMediaMessage     // Catch:{ Exception -> 0x0164 }
            android.net.Uri r9 = (android.net.Uri) r9     // Catch:{ Exception -> 0x0164 }
            r1.<init>(r9)     // Catch:{ Exception -> 0x0164 }
            r0.add(r1)     // Catch:{ Exception -> 0x0164 }
            goto L_0x0141
        L_0x015a:
            boolean r1 = r9 instanceof com.opengarden.firechat.matrixsdk.data.RoomMediaMessage     // Catch:{ Exception -> 0x0164 }
            if (r1 == 0) goto L_0x0141
            com.opengarden.firechat.matrixsdk.data.RoomMediaMessage r9 = (com.opengarden.firechat.matrixsdk.data.RoomMediaMessage) r9     // Catch:{ Exception -> 0x0164 }
            r0.add(r9)     // Catch:{ Exception -> 0x0164 }
            goto L_0x0141
        L_0x0164:
            java.lang.String r8 = LOG_TAG
            java.lang.String r9 = "fail to extract the extra stream"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r8, r9)
        L_0x016b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.RoomMediaMessage.listRoomMediaMessages(android.content.Intent, java.lang.ClassLoader):java.util.List");
    }
}
