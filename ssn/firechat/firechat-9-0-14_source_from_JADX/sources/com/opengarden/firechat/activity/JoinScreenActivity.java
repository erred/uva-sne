package com.opengarden.firechat.activity;

import android.content.Intent;
import android.text.TextUtils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;

public class JoinScreenActivity extends RiotAppCompatActivity {
    public static final String EXTRA_JOIN = "EXTRA_JOIN";
    public static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    public static final String EXTRA_REJECT = "EXTRA_REJECT";
    public static final String EXTRA_ROOM_ID = "EXTRA_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "JoinScreenActivity";

    public int getLayoutRes() {
        return C1299R.layout.activity_empty;
    }

    public void initUiAndData() {
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("EXTRA_ROOM_ID");
        String stringExtra2 = intent.getStringExtra("EXTRA_MATRIX_ID");
        boolean booleanExtra = intent.getBooleanExtra(EXTRA_JOIN, false);
        boolean booleanExtra2 = intent.getBooleanExtra(EXTRA_REJECT, false);
        if (TextUtils.isEmpty(stringExtra) || TextUtils.isEmpty(stringExtra2)) {
            Log.m211e(LOG_TAG, "## onCreate() : invalid parameters");
            finish();
            return;
        }
        MXSession session = Matrix.getInstance(getApplicationContext()).getSession(stringExtra2);
        Room room = session.getDataHandler().getRoom(stringExtra);
        if (session == null || room == null || !session.isAlive()) {
            Log.m211e(LOG_TAG, "## onCreate() : undefined parameters");
            finish();
            return;
        }
        if (booleanExtra) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onCreate() : Join the room ");
            sb.append(stringExtra);
            Log.m209d(str, sb.toString());
            room.join(new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    Log.m209d(JoinScreenActivity.LOG_TAG, "## onCreate() : join succeeds");
                }

                public void onNetworkError(Exception exc) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : join fails ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : join fails ");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m211e(access$000, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : join fails ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
            });
        } else if (booleanExtra2) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## onCreate() : Leave the room ");
            sb2.append(stringExtra);
            Log.m209d(str2, sb2.toString());
            room.leave(new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    Log.m209d(JoinScreenActivity.LOG_TAG, "## onCreate() : Leave succeeds");
                }

                public void onNetworkError(Exception exc) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : Leave fails ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : Leave fails ");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m211e(access$000, sb.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$000 = JoinScreenActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## onCreate() : Leave fails ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                }
            });
        }
        finish();
    }
}
