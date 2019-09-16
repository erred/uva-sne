package com.opengarden.firechat.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.util.Log;

@SuppressLint({"LongLogTag"})
public class VectorFakeRoomPreviewActivity extends RiotAppCompatActivity {
    private static final String LOG_TAG = "VectorFakeRoomPreviewActivity";

    public int getLayoutRes() {
        return C1299R.layout.activity_empty;
    }

    public void initUiAndData() {
        Intent intent = getIntent();
        if (intent == null) {
            Log.m217w(LOG_TAG, "## onCreate(): Failure - received intent is null");
        } else {
            String stringExtra = intent.getStringExtra("EXTRA_ROOM_ID");
            if (stringExtra == null) {
                Log.m217w(LOG_TAG, "## onCreate(): Failure - matrix ID is null");
            } else {
                MXSession session = Matrix.getInstance(getApplicationContext()).getSession(stringExtra);
                if (session == null) {
                    session = Matrix.getInstance(getApplicationContext()).getDefaultSession();
                }
                MXSession mXSession = session;
                if (mXSession == null || !mXSession.isAlive()) {
                    Log.m217w(LOG_TAG, "## onCreate(): Failure - session is null");
                } else {
                    RoomPreviewData roomPreviewData = new RoomPreviewData(mXSession, intent.getStringExtra("EXTRA_ROOM_ID"), null, intent.getStringExtra(VectorRoomActivity.EXTRA_ROOM_PREVIEW_ROOM_ALIAS), null);
                    VectorRoomActivity.sRoomPreviewData = roomPreviewData;
                    Intent intent2 = new Intent(intent);
                    intent2.setClass(this, VectorRoomActivity.class);
                    intent2.setFlags(603979776);
                    startActivity(intent2);
                }
            }
        }
        finish();
    }
}
