package com.opengarden.firechat.view;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.support.p000v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;

public class PillView extends LinearLayout {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PillView";
    /* access modifiers changed from: private */
    public PillImageView mAvatarView;
    /* access modifiers changed from: private */
    public OnUpdateListener mOnUpdateListener = null;
    private View mPillLayout;
    private TextView mTextView;

    public interface OnUpdateListener {
        void onAvatarUpdate();
    }

    public PillView(Context context) {
        super(context);
        initView();
    }

    public PillView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public PillView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    private void initView() {
        View.inflate(getContext(), C1299R.layout.pill_view, this);
        this.mTextView = (TextView) findViewById(C1299R.C1301id.pill_text_view);
        this.mAvatarView = (PillImageView) findViewById(C1299R.C1301id.pill_avatar_view);
        this.mPillLayout = findViewById(C1299R.C1301id.pill_layout);
    }

    private static String getLinkedUrl(String str) {
        if (str != null && str.startsWith("https://matrix.to/#/")) {
            return str.substring("https://matrix.to/#/".length());
        }
        return null;
    }

    public static boolean isPillable(String str) {
        String linkedUrl = getLinkedUrl(str);
        return linkedUrl != null && (MXSession.isRoomAlias(linkedUrl) || MXSession.isUserId(linkedUrl));
    }

    public void initData(CharSequence charSequence, String str, final MXSession mXSession, OnUpdateListener onUpdateListener) {
        this.mOnUpdateListener = onUpdateListener;
        this.mAvatarView.setOnUpdateListener(onUpdateListener);
        this.mTextView.setText(charSequence.toString());
        Theme theme = getContext().getTheme();
        int[] iArr = new int[1];
        iArr[0] = MXSession.isRoomAlias(charSequence.toString()) ? C1299R.attr.pill_background_room_alias : C1299R.attr.pill_background_user_id;
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(iArr);
        int resourceId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
        this.mPillLayout.setBackground(ContextCompat.getDrawable(getContext(), resourceId));
        Theme theme2 = getContext().getTheme();
        int[] iArr2 = new int[1];
        iArr2[0] = MXSession.isRoomAlias(charSequence.toString()) ? C1299R.attr.pill_text_color_room_alias : C1299R.attr.pill_text_color_user_id;
        TypedArray obtainStyledAttributes2 = theme2.obtainStyledAttributes(iArr2);
        int resourceId2 = obtainStyledAttributes2.getResourceId(0, 0);
        obtainStyledAttributes2.recycle();
        this.mTextView.setTextColor(ContextCompat.getColor(getContext(), resourceId2));
        final String linkedUrl = getLinkedUrl(str);
        if (MXSession.isUserId(linkedUrl)) {
            User user = mXSession.getDataHandler().getUser(linkedUrl);
            if (user == null) {
                user = new User();
                user.user_id = linkedUrl;
            }
            VectorUtils.loadUserAvatar(VectorApp.getInstance(), mXSession, this.mAvatarView, user);
            return;
        }
        mXSession.getDataHandler().roomIdByAlias(linkedUrl, new ApiCallback<String>() {
            public void onSuccess(String str) {
                if (PillView.this.mOnUpdateListener != null) {
                    Room room = mXSession.getDataHandler().getRoom(str, false);
                    if (room != null) {
                        VectorUtils.loadRoomAvatar((Context) VectorApp.getInstance(), mXSession, (ImageView) PillView.this.mAvatarView, room);
                        return;
                    }
                    PillView.this.mAvatarView.setImageBitmap(VectorUtils.getAvatar(VectorApp.getInstance(), VectorUtils.getAvatarColor(str), linkedUrl, true));
                    final RoomPreviewData roomPreviewData = new RoomPreviewData(mXSession, str, null, linkedUrl, null);
                    roomPreviewData.fetchPreviewData(new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            if (PillView.this.mOnUpdateListener != null) {
                                VectorUtils.loadRoomAvatar((Context) VectorApp.getInstance(), mXSession, (ImageView) PillView.this.mAvatarView, roomPreviewData);
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            String access$200 = PillView.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## initData() : fetchPreviewData failed ");
                            sb.append(exc.getMessage());
                            Log.m211e(access$200, sb.toString());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            String access$200 = PillView.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## initData() : fetchPreviewData failed ");
                            sb.append(matrixError.getMessage());
                            Log.m211e(access$200, sb.toString());
                        }

                        public void onUnexpectedError(Exception exc) {
                            String access$200 = PillView.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## initData() : fetchPreviewData failed ");
                            sb.append(exc.getMessage());
                            Log.m211e(access$200, sb.toString());
                        }
                    });
                }
            }

            public void onNetworkError(Exception exc) {
                String access$200 = PillView.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initData() : roomIdByAlias failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$200 = PillView.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initData() : roomIdByAlias failed ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$200, sb.toString());
            }

            public void onUnexpectedError(Exception exc) {
                String access$200 = PillView.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initData() : roomIdByAlias failed ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
            }
        });
    }

    public void setHighlighted(boolean z) {
        if (z) {
            this.mPillLayout.setBackground(ContextCompat.getDrawable(getContext(), C1299R.C1300drawable.pill_background_bing));
            this.mTextView.setTextColor(ContextCompat.getColor(getContext(), 17170443));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0032 A[Catch:{ Exception -> 0x0009 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable getDrawable(boolean r4) {
        /*
            r3 = this;
            if (r4 != 0) goto L_0x000b
            android.graphics.Bitmap r4 = r3.getDrawingCache()     // Catch:{ Exception -> 0x0009 }
            if (r4 != 0) goto L_0x002c
            goto L_0x000b
        L_0x0009:
            r4 = move-exception
            goto L_0x0048
        L_0x000b:
            r3.destroyDrawingCache()     // Catch:{ Exception -> 0x0009 }
            r4 = 1
            r3.setDrawingCacheEnabled(r4)     // Catch:{ Exception -> 0x0009 }
            r0 = 0
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r0)     // Catch:{ Exception -> 0x0009 }
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r0)     // Catch:{ Exception -> 0x0009 }
            r3.measure(r1, r2)     // Catch:{ Exception -> 0x0009 }
            int r1 = r3.getMeasuredWidth()     // Catch:{ Exception -> 0x0009 }
            int r2 = r3.getMeasuredHeight()     // Catch:{ Exception -> 0x0009 }
            r3.layout(r0, r0, r1, r2)     // Catch:{ Exception -> 0x0009 }
            r3.buildDrawingCache(r4)     // Catch:{ Exception -> 0x0009 }
        L_0x002c:
            android.graphics.Bitmap r4 = r3.getDrawingCache()     // Catch:{ Exception -> 0x0009 }
            if (r4 == 0) goto L_0x0062
            android.graphics.Bitmap r4 = r3.getDrawingCache()     // Catch:{ Exception -> 0x0009 }
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createBitmap(r4)     // Catch:{ Exception -> 0x0009 }
            android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable     // Catch:{ Exception -> 0x0009 }
            android.content.Context r1 = r3.getContext()     // Catch:{ Exception -> 0x0009 }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ Exception -> 0x0009 }
            r0.<init>(r1, r4)     // Catch:{ Exception -> 0x0009 }
            return r0
        L_0x0048:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "## getDrawable() : failed "
            r1.append(r2)
            java.lang.String r4 = r4.getMessage()
            r1.append(r4)
            java.lang.String r4 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r4)
        L_0x0062:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.view.PillView.getDrawable(boolean):android.graphics.drawable.Drawable");
    }
}
