package com.opengarden.firechat.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.MXCryptoError;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.notifications.NotificationUtils;

public class LockScreenActivity extends RiotAppCompatActivity {
    private static final String EXTRA_MATRIX_ID = "extra_matrix_id";
    public static final String EXTRA_MESSAGE_BODY = "extra_chat_body";
    public static final String EXTRA_ROOM_ID = "extra_room_id";
    public static final String EXTRA_SENDER_NAME = "extra_sender_name";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "LockScreenActivity";
    private static LockScreenActivity mLockScreenActivity;
    private LinearLayout mMainLayout;

    public int getLayoutRes() {
        return C1299R.layout.activity_lock_screen;
    }

    public static boolean isDisplayingALockScreenActivity() {
        return mLockScreenActivity != null;
    }

    public void doBeforeSetContentView() {
        mLockScreenActivity = this;
        Window window = getWindow();
        window.addFlags(524288);
        window.addFlags(2048);
        window.addFlags(4194304);
        window.addFlags(2097152);
        requestWindowFeature(1);
    }

    public void initUiAndData() {
        if (mLockScreenActivity != null) {
            mLockScreenActivity.finish();
        }
        mLockScreenActivity = this;
        NotificationUtils.INSTANCE.cancelAllNotifications(this);
        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_ROOM_ID)) {
            finish();
        } else if (!intent.hasExtra(EXTRA_SENDER_NAME)) {
            finish();
        } else {
            final String stringExtra = intent.getStringExtra(EXTRA_ROOM_ID);
            String str = null;
            if (intent.hasExtra(EXTRA_MATRIX_ID)) {
                str = intent.getStringExtra(EXTRA_MATRIX_ID);
            }
            final MXSession session = Matrix.getInstance(getApplicationContext()).getSession(str);
            final Room room = session.getDataHandler().getRoom(stringExtra);
            setTitle(room.getName(session.getCredentials().userId));
            TextView textView = (TextView) findViewById(C1299R.C1301id.lock_screen_sender);
            StringBuilder sb = new StringBuilder();
            sb.append(intent.getStringExtra(EXTRA_SENDER_NAME));
            sb.append(" : ");
            textView.setText(sb.toString());
            ((TextView) findViewById(C1299R.C1301id.lock_screen_body)).setText(intent.getStringExtra(EXTRA_MESSAGE_BODY));
            ((TextView) findViewById(C1299R.C1301id.lock_screen_room_name)).setText(room.getName(session.getCredentials().userId));
            final ImageButton imageButton = (ImageButton) findViewById(C1299R.C1301id.lock_screen_sendbutton);
            final EditText editText = (EditText) findViewById(C1299R.C1301id.lock_screen_edittext);
            imageButton.setEnabled(false);
            imageButton.setAlpha(0.5f);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (TextUtils.isEmpty(editText.getText().toString())) {
                        imageButton.setEnabled(false);
                        imageButton.setAlpha(0.5f);
                        return;
                    }
                    imageButton.setEnabled(true);
                    imageButton.setAlpha(1.0f);
                }
            });
            C14002 r2 = new OnClickListener() {
                public void onClick(View view) {
                    Log.m209d(LockScreenActivity.LOG_TAG, "Send a message ...");
                    String obj = editText.getText().toString();
                    Message message = new Message();
                    message.msgtype = Message.MSGTYPE_TEXT;
                    message.body = obj;
                    Event event = new Event(message, session.getCredentials().userId, stringExtra);
                    room.storeOutgoingEvent(event);
                    room.sendEvent(event, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            Log.m209d(LockScreenActivity.LOG_TAG, "Send message : onSuccess ");
                        }

                        public void onNetworkError(Exception exc) {
                            String access$000 = LockScreenActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Send message : onNetworkError ");
                            sb.append(exc.getMessage());
                            Log.m209d(access$000, sb.toString());
                            CommonActivityUtils.displayToast(LockScreenActivity.this, exc.getLocalizedMessage());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            String access$000 = LockScreenActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Send message : onMatrixError ");
                            sb.append(matrixError.getMessage());
                            Log.m209d(access$000, sb.toString());
                            if (matrixError instanceof MXCryptoError) {
                                CommonActivityUtils.displayToast(LockScreenActivity.this, ((MXCryptoError) matrixError).getDetailedErrorDescription());
                            } else {
                                CommonActivityUtils.displayToast(LockScreenActivity.this, matrixError.getLocalizedMessage());
                            }
                        }

                        public void onUnexpectedError(Exception exc) {
                            String access$000 = LockScreenActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Send message : onUnexpectedError ");
                            sb.append(exc.getMessage());
                            Log.m209d(access$000, sb.toString());
                            CommonActivityUtils.displayToast(LockScreenActivity.this, exc.getLocalizedMessage());
                        }
                    });
                    LockScreenActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            LockScreenActivity.this.finish();
                        }
                    });
                }
            };
            imageButton.setOnClickListener(r2);
            this.mMainLayout = (LinearLayout) findViewById(C1299R.C1301id.lock_main_layout);
        }
    }

    private void refreshMainLayout() {
        if (this.mMainLayout != null) {
            LayoutParams layoutParams = this.mMainLayout.getLayoutParams();
            layoutParams.width = (int) (((float) getResources().getDisplayMetrics().widthPixels) * 0.8f);
            this.mMainLayout.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        refreshMainLayout();
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        refreshMainLayout();
    }

    public void onDestroy() {
        super.onDestroy();
        if (this == mLockScreenActivity) {
            mLockScreenActivity = null;
        }
    }
}
