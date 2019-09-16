package com.opengarden.firechat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.p000v4.util.LruCache;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.MXCallsManager;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.rest.model.publicroom.PublicRoom;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;

public class VectorUtils {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorUtils";
    public static final int TAKE_IMAGE = 1;
    private static final LruCache<String, Bitmap> mAvatarImageByKeyDict = new LruCache<>(20971520);
    private static final ArrayList<Integer> mColorList = new ArrayList<>(Arrays.asList(new Integer[]{Integer.valueOf(-8990810), Integer.valueOf(-11476286), Integer.valueOf(-736399)}));
    private static HandlerThread mImagesThread;
    /* access modifiers changed from: private */
    public static Handler mImagesThreadHandler;
    /* access modifiers changed from: private */
    public static AlertDialog mMainAboutDialog;
    /* access modifiers changed from: private */
    public static Handler mUIHandler;
    private static final Pattern mUrlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)", 42);

    public static String getPermalink(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("https://matrix.to/#/");
        sb.append(str);
        String sb2 = sb.toString();
        if (!TextUtils.isEmpty(str2)) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("/");
            sb3.append(str2);
            sb2 = sb3.toString();
        }
        return sb2.replace("$", "%24");
    }

    public static void copyToClipboard(Context context, CharSequence charSequence) {
        ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("", charSequence));
        Toast.makeText(context, context.getString(C1299R.string.copied_to_clipboard), 0).show();
    }

    public static String getPublicRoomDisplayName(PublicRoom publicRoom) {
        String str = publicRoom.name;
        if (TextUtils.isEmpty(str)) {
            if (publicRoom.getAliases().size() > 0) {
                return (String) publicRoom.getAliases().get(0);
            }
            return publicRoom.roomId;
        } else if (str.startsWith("#") || publicRoom.getAliases().size() <= 0) {
            return str;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" (");
            sb.append((String) publicRoom.getAliases().get(0));
            sb.append(")");
            return sb.toString();
        }
    }

    public static String getCallingRoomDisplayName(Context context, MXSession mXSession, Room room) {
        if (context == null || mXSession == null || room == null) {
            return null;
        }
        Collection joinedMembers = room.getJoinedMembers();
        if (2 != joinedMembers.size()) {
            return getRoomDisplayName(context, mXSession, room);
        }
        ArrayList arrayList = new ArrayList(joinedMembers);
        if (TextUtils.equals(((RoomMember) arrayList.get(0)).getUserId(), mXSession.getMyUserId())) {
            return room.getLiveState().getMemberName(((RoomMember) arrayList.get(1)).getUserId());
        }
        return room.getLiveState().getMemberName(((RoomMember) arrayList.get(0)).getUserId());
    }

    public static String getRoomDisplayName(Context context, MXSession mXSession, Room room) {
        String str;
        if (room == null) {
            return null;
        }
        try {
            RoomState liveState = room.getLiveState();
            if (!TextUtils.isEmpty(liveState.name)) {
                return liveState.name;
            }
            String str2 = liveState.alias;
            if (TextUtils.isEmpty(str2) && liveState.getAliases().size() > 0) {
                str2 = (String) liveState.getAliases().get(0);
            }
            if (!TextUtils.isEmpty(str2)) {
                return str2;
            }
            String myUserId = mXSession.getMyUserId();
            Collection<RoomMember> displayableMembers = liveState.getDisplayableMembers();
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (RoomMember roomMember : displayableMembers) {
                if (!TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_LEAVE)) {
                    if (!TextUtils.equals(roomMember.getUserId(), myUserId)) {
                        arrayList.add(roomMember);
                    }
                    arrayList2.add(roomMember);
                }
            }
            Collections.sort(arrayList, new Comparator<RoomMember>() {
                public int compare(RoomMember roomMember, RoomMember roomMember2) {
                    long originServerTs = roomMember.getOriginServerTs() - roomMember2.getOriginServerTs();
                    if (originServerTs == 0) {
                        return 0;
                    }
                    return originServerTs < 0 ? -1 : 1;
                }
            });
            if (arrayList.size() == 0) {
                if (arrayList2.size() == 1) {
                    RoomMember roomMember2 = (RoomMember) arrayList2.get(0);
                    if (!TextUtils.equals(roomMember2.membership, "invite")) {
                        str = context.getString(C1299R.string.room_displayname_no_title);
                    } else if (!TextUtils.isEmpty(roomMember2.mSender)) {
                        str = context.getString(C1299R.string.room_displayname_invite_from, new Object[]{liveState.getMemberName(roomMember2.mSender)});
                    } else {
                        str = context.getString(C1299R.string.room_displayname_room_invite);
                    }
                } else {
                    str = context.getString(C1299R.string.room_displayname_no_title);
                }
            } else if (arrayList.size() == 1) {
                str = liveState.getMemberName(((RoomMember) arrayList.get(0)).getUserId());
            } else if (arrayList.size() == 2) {
                str = context.getString(C1299R.string.room_displayname_two_members, new Object[]{liveState.getMemberName(((RoomMember) arrayList.get(0)).getUserId()), liveState.getMemberName(((RoomMember) arrayList.get(1)).getUserId())});
            } else {
                str = context.getString(C1299R.string.room_displayname_many_members, new Object[]{liveState.getMemberName(((RoomMember) arrayList.get(0)).getUserId()), context.getResources().getQuantityString(C1299R.plurals.others, arrayList.size() - 1, new Object[]{Integer.valueOf(arrayList.size() - 1)})});
            }
            return str;
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getRoomDisplayName() failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
            return room.getRoomId();
        }
    }

    public static int getAvatarColor(String str) {
        long j = 0;
        if (!TextUtils.isEmpty(str)) {
            int i = 0;
            while (i < str.length()) {
                i++;
                j += (long) str.charAt(i);
            }
            j %= (long) mColorList.size();
        }
        return ((Integer) mColorList.get((int) j)).intValue();
    }

    private static Bitmap createAvatarThumbnail(Context context, int i, String str) {
        return createAvatar(i, str, (int) (context.getResources().getDisplayMetrics().density * 42.0f));
    }

    private static Bitmap createAvatar(int i, String str, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i2, i2, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(i);
        Paint paint = new Paint();
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
        paint.setColor(-1);
        paint.setTextSize((float) ((i2 * 2) / 3));
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        canvas.drawText(str, (float) (((canvas.getWidth() - rect.width()) - rect.left) / 2), (float) (((canvas.getHeight() + rect.height()) - rect.bottom) / 2), paint);
        return createBitmap;
    }

    private static String getInitialLetter(String str) {
        String str2 = StringUtils.SPACE;
        if (!TextUtils.isEmpty(str)) {
            int i = 0;
            char charAt = str.charAt(0);
            int i2 = 1;
            if ((charAt == '@' || charAt == '#' || charAt == '+') && str.length() > 1) {
                i = 1;
            }
            char charAt2 = str.charAt(i);
            if (str.length() >= 2 && 8206 == charAt2) {
                i++;
                charAt2 = str.charAt(i);
            }
            if (charAt2 >= 55296 && charAt2 <= 56319) {
                int i3 = i + 1;
                if (str.length() > i3) {
                    char charAt3 = str.charAt(i3);
                    if (charAt3 >= 56320 && charAt3 <= 57343) {
                        i2 = 2;
                    }
                }
            }
            str2 = str.substring(i, i2 + i);
        }
        return str2.toUpperCase(VectorApp.getApplicationLocale());
    }

    public static Bitmap getAvatar(Context context, int i, String str, boolean z) {
        String initialLetter = getInitialLetter(str);
        StringBuilder sb = new StringBuilder();
        sb.append(initialLetter);
        sb.append("_");
        sb.append(i);
        String sb2 = sb.toString();
        Bitmap bitmap = (Bitmap) mAvatarImageByKeyDict.get(sb2);
        if (bitmap != null || !z) {
            return bitmap;
        }
        Bitmap createAvatarThumbnail = createAvatarThumbnail(context, i, initialLetter);
        mAvatarImageByKeyDict.put(sb2, createAvatarThumbnail);
        return createAvatarThumbnail;
    }

    /* access modifiers changed from: private */
    public static void setDefaultMemberAvatar(final ImageView imageView, String str, String str2) {
        if (imageView != null && !TextUtils.isEmpty(str)) {
            final Bitmap avatar = getAvatar(imageView.getContext(), getAvatarColor(str), TextUtils.isEmpty(str2) ? str : str2, true);
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                imageView.setImageBitmap(avatar);
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(" - ");
            sb.append(str2);
            final String sb2 = sb.toString();
            imageView.setTag(sb2);
            mUIHandler.post(new Runnable() {
                public void run() {
                    if (TextUtils.equals(sb2, (String) imageView.getTag())) {
                        imageView.setImageBitmap(avatar);
                    }
                }
            });
        }
    }

    public static void loadRoomAvatar(Context context, MXSession mXSession, ImageView imageView, Room room) {
        if (room != null) {
            loadUserAvatar(context, mXSession, imageView, room.getAvatarUrl(), room.getRoomId(), getRoomDisplayName(context, mXSession, room));
        }
    }

    public static void loadRoomAvatar(Context context, MXSession mXSession, ImageView imageView, RoomPreviewData roomPreviewData) {
        if (roomPreviewData != null) {
            loadUserAvatar(context, mXSession, imageView, roomPreviewData.getRoomAvatarUrl(), roomPreviewData.getRoomId(), roomPreviewData.getRoomName());
        }
    }

    public static void loadGroupAvatar(Context context, MXSession mXSession, ImageView imageView, Group group) {
        if (group != null) {
            loadUserAvatar(context, mXSession, imageView, group.getAvatarUrl(), group.getGroupId(), group.getDisplayName());
        }
    }

    public static void loadCallAvatar(Context context, MXSession mXSession, ImageView imageView, Room room) {
        if (room != null && mXSession != null && imageView != null && mXSession.isAlive()) {
            Bitmap bitmap = null;
            imageView.setTag(null);
            String callAvatarUrl = room.getCallAvatarUrl();
            String roomId = room.getRoomId();
            String roomDisplayName = getRoomDisplayName(context, mXSession, room);
            int i = imageView.getLayoutParams().width;
            if (i < 0) {
                ViewParent parent = imageView.getParent();
                while (i < 0 && parent != null) {
                    if (parent instanceof View) {
                        i = ((View) parent).getLayoutParams().width;
                    }
                    parent = parent.getParent();
                }
            }
            if (mXSession.getMediasCache().isAvatarThumbnailCached(callAvatarUrl, context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size))) {
                mXSession.getMediasCache().loadAvatarThumbnail(mXSession.getHomeServerConfig(), imageView, callAvatarUrl, context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size));
                return;
            }
            if (i > 0) {
                bitmap = createAvatar(getAvatarColor(roomId), getInitialLetter(roomDisplayName), i);
            }
            mXSession.getMediasCache().loadAvatarThumbnail(mXSession.getHomeServerConfig(), imageView, callAvatarUrl, context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size), bitmap);
        }
    }

    public static void loadRoomMemberAvatar(Context context, MXSession mXSession, ImageView imageView, RoomMember roomMember) {
        if (roomMember != null) {
            loadUserAvatar(context, mXSession, imageView, roomMember.getAvatarUrl(), roomMember.getUserId(), roomMember.displayname);
        }
    }

    public static void loadUserAvatar(Context context, MXSession mXSession, ImageView imageView, User user) {
        if (user != null) {
            loadUserAvatar(context, mXSession, imageView, user.getAvatarUrl(), user.user_id, user.displayname);
        }
    }

    public static void loadUserAvatar(Context context, MXSession mXSession, ImageView imageView, String str, String str2, String str3) {
        if (mXSession != null && imageView != null && mXSession.isAlive()) {
            imageView.setTag(null);
            if (mXSession.getMediasCache().isAvatarThumbnailCached(str, context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size))) {
                mXSession.getMediasCache().loadAvatarThumbnail(mXSession.getHomeServerConfig(), imageView, str, context.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size));
            } else {
                if (mImagesThread == null) {
                    mImagesThread = new HandlerThread("ImagesThread", 1);
                    mImagesThread.start();
                    mImagesThreadHandler = new Handler(mImagesThread.getLooper());
                    mUIHandler = new Handler(Looper.getMainLooper());
                }
                Bitmap avatar = getAvatar(imageView.getContext(), getAvatarColor(str2), TextUtils.isEmpty(str3) ? str2 : str3, false);
                if (avatar != null) {
                    imageView.setImageBitmap(avatar);
                    if (!TextUtils.isEmpty(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(str2);
                        sb.append(str3);
                        final String sb2 = sb.toString();
                        imageView.setTag(sb2);
                        if (!MXMediasCache.isMediaUrlUnreachable(str)) {
                            Handler handler = mImagesThreadHandler;
                            final ImageView imageView2 = imageView;
                            final MXSession mXSession2 = mXSession;
                            final String str4 = str;
                            final Context context2 = context;
                            final Bitmap bitmap = avatar;
                            C29393 r0 = new Runnable() {
                                public void run() {
                                    if (TextUtils.equals(sb2, (String) imageView2.getTag())) {
                                        mXSession2.getMediasCache().loadAvatarThumbnail(mXSession2.getHomeServerConfig(), imageView2, str4, context2.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size), bitmap);
                                    }
                                }
                            };
                            handler.post(r0);
                        }
                    }
                } else {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("00");
                    sb3.append(str);
                    sb3.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                    sb3.append(str2);
                    sb3.append(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
                    sb3.append(str3);
                    final String sb4 = sb3.toString();
                    imageView.setTag(sb4);
                    Handler handler2 = mImagesThreadHandler;
                    final ImageView imageView3 = imageView;
                    final String str5 = str2;
                    final String str6 = str3;
                    final String str7 = str;
                    final MXSession mXSession3 = mXSession;
                    final Context context3 = context;
                    C29404 r02 = new Runnable() {
                        public void run() {
                            if (TextUtils.equals(sb4, (String) imageView3.getTag())) {
                                imageView3.setTag(null);
                                VectorUtils.setDefaultMemberAvatar(imageView3, str5, str6);
                                if (!TextUtils.isEmpty(str7) && !MXMediasCache.isMediaUrlUnreachable(str7)) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("11");
                                    sb.append(str7);
                                    sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                                    sb.append(str5);
                                    sb.append(HelpFormatter.DEFAULT_LONG_OPT_PREFIX);
                                    sb.append(str6);
                                    final String sb2 = sb.toString();
                                    imageView3.setTag(sb2);
                                    VectorUtils.mUIHandler.post(new Runnable() {
                                        public void run() {
                                            if (TextUtils.equals(sb2, (String) imageView3.getTag())) {
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("22");
                                                sb.append(str7);
                                                sb.append(str5);
                                                sb.append(str6);
                                                final String sb2 = sb.toString();
                                                imageView3.setTag(sb2);
                                                VectorUtils.mImagesThreadHandler.post(new Runnable() {
                                                    public void run() {
                                                        if (TextUtils.equals(sb2, (String) imageView3.getTag())) {
                                                            mXSession3.getMediasCache().loadAvatarThumbnail(mXSession3.getHomeServerConfig(), imageView3, str7, context3.getResources().getDimensionPixelSize(C1299R.dimen.profile_avatar_size), VectorUtils.getAvatar(imageView3.getContext(), VectorUtils.getAvatarColor(str5), TextUtils.isEmpty(str6) ? str5 : str6, false));
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    };
                    handler2.post(r02);
                }
            }
        }
    }

    public static String getApplicationVersion(Context context) {
        return Matrix.getInstance(context).getVersion(false, true);
    }

    public static void displayThirdPartyLicenses() {
        final Activity currentActivity = VectorApp.getCurrentActivity();
        if (currentActivity != null) {
            if (mMainAboutDialog != null) {
                if (mMainAboutDialog.isShowing() && mMainAboutDialog.getOwnerActivity() != null) {
                    try {
                        mMainAboutDialog.dismiss();
                    } catch (Exception e) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## displayThirdPartyLicenses() : ");
                        sb.append(e.getMessage());
                        Log.m211e(str, sb.toString());
                    }
                }
                mMainAboutDialog = null;
            }
            currentActivity.runOnUiThread(new Runnable() {
                public void run() {
                    WebView webView = (WebView) LayoutInflater.from(currentActivity).inflate(C1299R.layout.dialog_licenses, null);
                    webView.loadUrl("file:///android_asset/open_source_licenses.html");
                    View inflate = LayoutInflater.from(currentActivity).inflate(C1299R.layout.dialog_licenses_header, null);
                    webView.setScrollbarFadingEnabled(false);
                    VectorUtils.mMainAboutDialog = new Builder(currentActivity).setCustomTitle(inflate).setView(webView).setPositiveButton(17039370, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            VectorUtils.mMainAboutDialog = null;
                        }
                    }).setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialogInterface) {
                            VectorUtils.mMainAboutDialog = null;
                        }
                    }).create();
                    VectorUtils.mMainAboutDialog.show();
                }
            });
        }
    }

    private static void displayInWebview(Context context, String str) {
        Builder builder = new Builder(context);
        WebView webView = new WebView(context);
        webView.loadUrl(str);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        builder.setView(webView);
        builder.setPositiveButton(17039370, null);
        builder.show();
    }

    public static void displayAppTac() {
        if (VectorApp.getCurrentActivity() != null) {
            displayInWebview(VectorApp.getCurrentActivity(), "https://www.opengarden.com/firechat-privacy-policy.html?lang=en/");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0033 A[SYNTHETIC, Splitter:B:17:0x0033] */
    @android.annotation.SuppressLint({"NewApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.net.Uri getThumbnailUriFromIntent(android.content.Context r9, android.content.Intent r10, com.opengarden.firechat.matrixsdk.p007db.MXMediasCache r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L_0x00cc
            if (r9 == 0) goto L_0x00cc
            if (r11 == 0) goto L_0x00cc
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 18
            if (r1 < r2) goto L_0x0012
            android.content.ClipData r1 = r10.getClipData()
            goto L_0x0013
        L_0x0012:
            r1 = r0
        L_0x0013:
            if (r1 == 0) goto L_0x0025
            int r10 = r1.getItemCount()
            if (r10 <= 0) goto L_0x0030
            r10 = 0
            android.content.ClipData$Item r10 = r1.getItemAt(r10)
            android.net.Uri r10 = r10.getUri()
            goto L_0x0031
        L_0x0025:
            android.net.Uri r1 = r10.getData()
            if (r1 == 0) goto L_0x0030
            android.net.Uri r10 = r10.getData()
            goto L_0x0031
        L_0x0030:
            r10 = r0
        L_0x0031:
            if (r10 == 0) goto L_0x00cc
            com.opengarden.firechat.matrixsdk.util.ResourceUtils$Resource r1 = com.opengarden.firechat.matrixsdk.util.ResourceUtils.openResource(r9, r10, r0)     // Catch:{ Exception -> 0x00b1 }
            if (r1 == 0) goto L_0x0072
            boolean r2 = r1.isJpegResource()     // Catch:{ Exception -> 0x00b1 }
            if (r2 == 0) goto L_0x0072
            java.io.InputStream r4 = r1.mContentStream     // Catch:{ Exception -> 0x00b1 }
            int r7 = com.opengarden.firechat.matrixsdk.util.ImageUtils.getRotationAngleForBitmap(r9, r10)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x00b1 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b1 }
            r3.<init>()     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r5 = "## getThumbnailUriFromIntent() :  "
            r3.append(r5)     // Catch:{ Exception -> 0x00b1 }
            r3.append(r10)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r10 = " rotationAngle "
            r3.append(r10)     // Catch:{ Exception -> 0x00b1 }
            r3.append(r7)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r10 = r3.toString()     // Catch:{ Exception -> 0x00b1 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r10)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r5 = r1.mMimeType     // Catch:{ Exception -> 0x00b1 }
            r6 = 1024(0x400, float:1.435E-42)
            r3 = r9
            r8 = r11
            java.lang.String r9 = com.opengarden.firechat.matrixsdk.util.ImageUtils.scaleAndRotateImage(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00b1 }
            android.net.Uri r10 = android.net.Uri.parse(r9)     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b0
        L_0x0072:
            if (r1 == 0) goto L_0x0095
            java.lang.String r9 = LOG_TAG     // Catch:{ Exception -> 0x00b1 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b1 }
            r11.<init>()     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r2 = "## getThumbnailUriFromIntent() : cannot manage "
            r11.append(r2)     // Catch:{ Exception -> 0x00b1 }
            r11.append(r10)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r2 = " mMimeType "
            r11.append(r2)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r1 = r1.mMimeType     // Catch:{ Exception -> 0x00b1 }
            r11.append(r1)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x00b1 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r9, r11)     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b0
        L_0x0095:
            java.lang.String r9 = LOG_TAG     // Catch:{ Exception -> 0x00b1 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b1 }
            r11.<init>()     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r1 = "## getThumbnailUriFromIntent() : cannot manage "
            r11.append(r1)     // Catch:{ Exception -> 0x00b1 }
            r11.append(r10)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r1 = " --> cannot open the dedicated file"
            r11.append(r1)     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x00b1 }
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r9, r11)     // Catch:{ Exception -> 0x00b1 }
        L_0x00b0:
            return r10
        L_0x00b1:
            r9 = move-exception
            java.lang.String r10 = LOG_TAG
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r1 = "## getThumbnailUriFromIntent failed "
            r11.append(r1)
            java.lang.String r9 = r9.getMessage()
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r10, r9)
        L_0x00cc:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.VectorUtils.getThumbnailUriFromIntent(android.content.Context, android.content.Intent, com.opengarden.firechat.matrixsdk.db.MXMediasCache):android.net.Uri");
    }

    private static String formatSecondsIntervalFloored(Context context, long j) {
        if (j < 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("0");
            sb.append(context.getResources().getString(C1299R.string.format_time_s));
            return sb.toString();
        } else if (j < 60) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(j);
            sb2.append(context.getResources().getString(C1299R.string.format_time_s));
            return sb2.toString();
        } else if (j < 3600) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(j / 60);
            sb3.append(context.getResources().getString(C1299R.string.format_time_m));
            return sb3.toString();
        } else if (j < 86400) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(j / 3600);
            sb4.append(context.getResources().getString(C1299R.string.format_time_h));
            return sb4.toString();
        } else {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(j / 86400);
            sb5.append(context.getResources().getString(C1299R.string.format_time_d));
            return sb5.toString();
        }
    }

    public static String getUserOnlineStatus(Context context, MXSession mXSession, String str, ApiCallback<Void> apiCallback) {
        String str2 = null;
        if (mXSession == null || str == null) {
            return null;
        }
        User user = mXSession.getDataHandler().getStore().getUser(str);
        boolean z = user == null || user.isPresenceObsolete();
        if (apiCallback != null && z) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Get the user presence : ");
            sb.append(str);
            Log.m209d(str3, sb.toString());
            final String str4 = user != null ? user.presence : null;
            final MXSession mXSession2 = mXSession;
            final String str5 = str;
            final User user2 = user;
            final ApiCallback<Void> apiCallback2 = apiCallback;
            C29477 r2 = new ApiCallback<Void>() {
                /* JADX WARNING: Removed duplicated region for block: B:13:0x00b0  */
                /* JADX WARNING: Removed duplicated region for block: B:20:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void onSuccess(java.lang.Void r5) {
                    /*
                        r4 = this;
                        com.opengarden.firechat.matrixsdk.MXSession r5 = r3
                        com.opengarden.firechat.matrixsdk.MXDataHandler r5 = r5.getDataHandler()
                        com.opengarden.firechat.matrixsdk.data.store.IMXStore r5 = r5.getStore()
                        java.lang.String r0 = r4
                        com.opengarden.firechat.matrixsdk.rest.model.User r5 = r5.getUser(r0)
                        com.opengarden.firechat.matrixsdk.rest.model.User r0 = r5
                        r1 = 1
                        if (r0 != 0) goto L_0x0033
                        if (r5 != 0) goto L_0x0033
                        java.lang.String r5 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        java.lang.String r1 = "Don't find any presence info of "
                        r0.append(r1)
                        java.lang.String r1 = r4
                        r0.append(r1)
                        java.lang.String r0 = r0.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r0)
                        goto L_0x00ad
                    L_0x0033:
                        com.opengarden.firechat.matrixsdk.rest.model.User r0 = r5
                        if (r0 != 0) goto L_0x0054
                        if (r5 == 0) goto L_0x0054
                        java.lang.String r5 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        java.lang.String r2 = "Got the user presence : "
                        r0.append(r2)
                        java.lang.String r2 = r4
                        r0.append(r2)
                        java.lang.String r0 = r0.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r5, r0)
                        goto L_0x00ae
                    L_0x0054:
                        java.lang.String r0 = r6
                        java.lang.String r2 = r5.presence
                        boolean r0 = android.text.TextUtils.equals(r0, r2)
                        if (r0 != 0) goto L_0x00ad
                        java.lang.String r0 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = "Got some new user presence info : "
                        r2.append(r3)
                        java.lang.String r3 = r4
                        r2.append(r3)
                        java.lang.String r2 = r2.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
                        java.lang.String r0 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = "currently_active : "
                        r2.append(r3)
                        java.lang.Boolean r3 = r5.currently_active
                        r2.append(r3)
                        java.lang.String r2 = r2.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r2)
                        java.lang.String r0 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = "lastActiveAgo : "
                        r2.append(r3)
                        java.lang.Long r5 = r5.lastActiveAgo
                        r2.append(r5)
                        java.lang.String r5 = r2.toString()
                        com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r5)
                        goto L_0x00ae
                    L_0x00ad:
                        r1 = 0
                    L_0x00ae:
                        if (r1 == 0) goto L_0x00c4
                        com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r5 = r7
                        if (r5 == 0) goto L_0x00c4
                        com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback r5 = r7     // Catch:{ Exception -> 0x00bb }
                        r0 = 0
                        r5.onSuccess(r0)     // Catch:{ Exception -> 0x00bb }
                        goto L_0x00c4
                    L_0x00bb:
                        java.lang.String r5 = com.opengarden.firechat.util.VectorUtils.LOG_TAG
                        java.lang.String r0 = "getUserOnlineStatus refreshCallback failed"
                        com.opengarden.firechat.matrixsdk.util.Log.m211e(r5, r0)
                    L_0x00c4:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.util.VectorUtils.C29477.onSuccess(java.lang.Void):void");
                }

                public void onNetworkError(Exception exc) {
                    String access$400 = VectorUtils.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("getUserOnlineStatus onNetworkError ");
                    sb.append(exc.getLocalizedMessage());
                    Log.m211e(access$400, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$400 = VectorUtils.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("getUserOnlineStatus onMatrixError ");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m211e(access$400, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$400 = VectorUtils.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("getUserOnlineStatus onUnexpectedError ");
                    sb.append(exc.getLocalizedMessage());
                    Log.m211e(access$400, sb.toString());
                }
            };
            mXSession.refreshUserPresence(str, r2);
        }
        if (user == null) {
            return null;
        }
        if (TextUtils.equals(user.presence, User.PRESENCE_ONLINE)) {
            str2 = context.getResources().getString(C1299R.string.room_participants_online);
        } else if (TextUtils.equals(user.presence, User.PRESENCE_UNAVAILABLE)) {
            str2 = context.getResources().getString(C1299R.string.room_participants_idle);
        } else if (TextUtils.equals(user.presence, User.PRESENCE_OFFLINE) || user.presence == null) {
            str2 = context.getResources().getString(C1299R.string.room_participants_offline);
        }
        if (str2 != null) {
            if (user.currently_active != null && user.currently_active.booleanValue()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str2);
                sb2.append(StringUtils.SPACE);
                sb2.append(context.getResources().getString(C1299R.string.room_participants_now));
                str2 = sb2.toString();
            } else if (user.lastActiveAgo != null && user.lastActiveAgo.longValue() > 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str2);
                sb3.append(StringUtils.SPACE);
                sb3.append(formatSecondsIntervalFloored(context, user.getAbsoluteLastActiveAgo() / 1000));
                sb3.append(StringUtils.SPACE);
                sb3.append(context.getResources().getString(C1299R.string.room_participants_ago));
                str2 = sb3.toString();
            }
        }
        return str2;
    }

    public static Map<String, ParticipantAdapterItem> listKnownParticipants(MXSession mXSession) {
        Collection<User> users = mXSession.getDataHandler().getStore().getUsers();
        HashMap hashMap = new HashMap(users.size());
        for (User user : users) {
            if (!MXCallsManager.isConferenceUserId(user.user_id)) {
                hashMap.put(user.user_id, new ParticipantAdapterItem(user));
            }
        }
        return hashMap;
    }

    public static HashMap<Integer, List<Integer>> getVisibleChildViews(ExpandableListView expandableListView, BaseExpandableListAdapter baseExpandableListAdapter) {
        int i;
        HashMap<Integer, List<Integer>> hashMap = new HashMap<>();
        long expandableListPosition = expandableListView.getExpandableListPosition(expandableListView.getFirstVisiblePosition());
        int packedPositionGroup = ExpandableListView.getPackedPositionGroup(expandableListPosition);
        int packedPositionChild = ExpandableListView.getPackedPositionChild(expandableListPosition);
        long expandableListPosition2 = expandableListView.getExpandableListPosition(expandableListView.getLastVisiblePosition());
        int packedPositionGroup2 = ExpandableListView.getPackedPositionGroup(expandableListPosition2);
        int packedPositionChild2 = ExpandableListView.getPackedPositionChild(expandableListPosition2);
        int i2 = packedPositionGroup;
        while (i2 <= packedPositionGroup2) {
            ArrayList arrayList = new ArrayList();
            if (i2 == packedPositionGroup2) {
                i = packedPositionChild2;
            } else {
                i = baseExpandableListAdapter.getChildrenCount(i2) - 1;
            }
            for (int i3 = i2 == packedPositionGroup ? packedPositionChild : 0; i3 <= i; i3++) {
                arrayList.add(Integer.valueOf(i3));
            }
            hashMap.put(Integer.valueOf(i2), arrayList);
            i2++;
        }
        return hashMap;
    }

    public static String getPlainId(String str) {
        return MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER.matcher(str).find() ? str.substring(1).split(":")[0] : str;
    }
}
