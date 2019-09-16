package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.notifications.NotificationUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\b"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/JoinRoomActivity;", "Lcom/opengarden/firechat/activity/RiotAppCompatActivity;", "()V", "getLayoutRes", "", "initUiAndData", "", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: JoinRoomActivity.kt */
public final class JoinRoomActivity extends RiotAppCompatActivity {
    public static final Companion Companion = new Companion(null);
    private static final String EXTRA_JOIN = "EXTRA_JOIN";
    private static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    private static final String EXTRA_REJECT = "EXTRA_REJECT";
    private static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "JoinRoomActivity";

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0004J\u001e\u0010\u0012\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\n \t*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0013"}, mo21251d2 = {"Lcom/opengarden/firechat/activity/JoinRoomActivity$Companion;", "", "()V", "EXTRA_JOIN", "", "EXTRA_MATRIX_ID", "EXTRA_REJECT", "EXTRA_ROOM_ID", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "getJoinRoomIntent", "Landroid/content/Intent;", "context", "Landroid/content/Context;", "roomId", "matrixId", "getRejectRoomIntent", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: JoinRoomActivity.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return JoinRoomActivity.LOG_TAG;
        }

        @NotNull
        public final Intent getJoinRoomIntent(@NotNull Context context, @NotNull String str, @NotNull String str2) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "roomId");
            Intrinsics.checkParameterIsNotNull(str2, "matrixId");
            Intent putExtra = new Intent(context, JoinRoomActivity.class).putExtra("EXTRA_ROOM_ID", str).putExtra("EXTRA_MATRIX_ID", str2).putExtra("EXTRA_JOIN", true);
            Intrinsics.checkExpressionValueIsNotNull(putExtra, "Intent(context, JoinRoom…utExtra(EXTRA_JOIN, true)");
            return putExtra;
        }

        @NotNull
        public final Intent getRejectRoomIntent(@NotNull Context context, @NotNull String str, @NotNull String str2) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(str, "roomId");
            Intrinsics.checkParameterIsNotNull(str2, "matrixId");
            Intent putExtra = new Intent(context, JoinRoomActivity.class).putExtra("EXTRA_ROOM_ID", str).putExtra("EXTRA_MATRIX_ID", str2).putExtra("EXTRA_REJECT", true);
            Intrinsics.checkExpressionValueIsNotNull(putExtra, "Intent(context, JoinRoom…Extra(EXTRA_REJECT, true)");
            return putExtra;
        }
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_empty;
    }

    public void initUiAndData() {
        String stringExtra = getIntent().getStringExtra("EXTRA_ROOM_ID");
        String stringExtra2 = getIntent().getStringExtra("EXTRA_MATRIX_ID");
        boolean booleanExtra = getIntent().getBooleanExtra("EXTRA_JOIN", false);
        boolean booleanExtra2 = getIntent().getBooleanExtra("EXTRA_REJECT", false);
        NotificationUtils.INSTANCE.cancelNotificationMessage(this);
        if (TextUtils.isEmpty(stringExtra) || TextUtils.isEmpty(stringExtra2)) {
            Log.m211e(Companion.getLOG_TAG(), "## onCreate() : invalid parameters");
            finish();
            return;
        }
        Matrix instance = Matrix.getInstance(getApplicationContext());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        MXSession session = instance.getSession(stringExtra2);
        if (session == null || !session.isAlive()) {
            Log.m211e(Companion.getLOG_TAG(), "## onCreate() : undefined parameters");
            finish();
            return;
        }
        Room room = session.getDataHandler().getRoom(stringExtra);
        if (room == null) {
            Log.m211e(Companion.getLOG_TAG(), "## onCreate() : undefined parameters");
            finish();
            return;
        }
        if (booleanExtra) {
            String access$getLOG_TAG$p = Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("## onCreate() : Join the room ");
            sb.append(stringExtra);
            Log.m209d(access$getLOG_TAG$p, sb.toString());
            room.join(new JoinRoomActivity$initUiAndData$1());
        } else if (booleanExtra2) {
            String access$getLOG_TAG$p2 = Companion.getLOG_TAG();
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## onCreate() : reject the invitation to room ");
            sb2.append(stringExtra);
            Log.m209d(access$getLOG_TAG$p2, sb2.toString());
            room.leave(new JoinRoomActivity$initUiAndData$2());
        }
        finish();
    }
}
