package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.p003v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;

public class PhoneNumberVerificationActivity extends RiotAppCompatActivity implements OnEditorActionListener, TextWatcher {
    private static final String EXTRA_MATRIX_ID = "EXTRA_MATRIX_ID";
    private static final String EXTRA_PID = "EXTRA_PID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PhoneNumberVerificationActivity";
    private boolean mIsSubmittingToken;
    private View mLoadingView;
    private TextInputEditText mPhoneNumberCode;
    private TextInputLayout mPhoneNumberCodeLayout;
    private MXSession mSession;
    /* access modifiers changed from: private */
    public ThreePid mThreePid;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public int getLayoutRes() {
        return C1299R.layout.activity_phone_number_verification;
    }

    public int getTitleRes() {
        return C1299R.string.settings_phone_number_verification;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public static Intent getIntent(Context context, String str, ThreePid threePid) {
        Intent intent = new Intent(context, PhoneNumberVerificationActivity.class);
        intent.putExtra("EXTRA_MATRIX_ID", str);
        intent.putExtra(EXTRA_PID, threePid);
        return intent;
    }

    public void initUiAndData() {
        setSupportActionBar((Toolbar) findViewById(C1299R.C1301id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mPhoneNumberCode = (TextInputEditText) findViewById(C1299R.C1301id.phone_number_code_value);
        this.mPhoneNumberCodeLayout = (TextInputLayout) findViewById(C1299R.C1301id.phone_number_code);
        setWaitingView(findViewById(C1299R.C1301id.loading_view));
        Intent intent = getIntent();
        this.mSession = Matrix.getInstance(this).getSession(intent.getStringExtra("EXTRA_MATRIX_ID"));
        if (this.mSession == null || !this.mSession.isAlive()) {
            finish();
            return;
        }
        this.mThreePid = (ThreePid) intent.getSerializableExtra(EXTRA_PID);
        this.mPhoneNumberCode.addTextChangedListener(this);
        this.mPhoneNumberCode.setOnEditorActionListener(this);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mIsSubmittingToken = false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.menu_phone_number_verification, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        } else if (itemId != C1299R.C1301id.action_verify_phone_number) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            submitCode();
            return true;
        }
    }

    private void submitCode() {
        if (!this.mIsSubmittingToken) {
            this.mIsSubmittingToken = true;
            if (TextUtils.isEmpty(this.mPhoneNumberCode.getText())) {
                this.mPhoneNumberCodeLayout.setErrorEnabled(true);
                this.mPhoneNumberCodeLayout.setError(getString(C1299R.string.settings_phone_number_verification_error_empty_code));
                return;
            }
            showWaitingView();
            this.mSession.getThirdPidRestClient().submitValidationToken(this.mThreePid.medium, this.mPhoneNumberCode.getText().toString(), this.mThreePid.clientSecret, this.mThreePid.sid, new ApiCallback<Boolean>() {
                public void onSuccess(Boolean bool) {
                    if (bool.booleanValue()) {
                        Log.m211e(PhoneNumberVerificationActivity.LOG_TAG, "## submitPhoneNumberValidationToken(): onSuccess() - registerAfterEmailValidations() started");
                        PhoneNumberVerificationActivity.this.registerAfterPhoneNumberValidation(PhoneNumberVerificationActivity.this.mThreePid);
                        return;
                    }
                    Log.m211e(PhoneNumberVerificationActivity.LOG_TAG, "## submitPhoneNumberValidationToken(): onSuccess() - failed (success=false)");
                    PhoneNumberVerificationActivity.this.onSubmitCodeError(PhoneNumberVerificationActivity.this.getString(C1299R.string.settings_phone_number_verification_error));
                }

                public void onNetworkError(Exception exc) {
                    PhoneNumberVerificationActivity.this.onSubmitCodeError(exc.getLocalizedMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    PhoneNumberVerificationActivity.this.onSubmitCodeError(matrixError.getLocalizedMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    PhoneNumberVerificationActivity.this.onSubmitCodeError(exc.getLocalizedMessage());
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void registerAfterPhoneNumberValidation(ThreePid threePid) {
        this.mSession.getMyUser().add3Pid(threePid, true, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                PhoneNumberVerificationActivity.this.setResult(-1, new Intent());
                PhoneNumberVerificationActivity.this.finish();
            }

            public void onNetworkError(Exception exc) {
                PhoneNumberVerificationActivity.this.onSubmitCodeError(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                PhoneNumberVerificationActivity.this.onSubmitCodeError(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                PhoneNumberVerificationActivity.this.onSubmitCodeError(exc.getLocalizedMessage());
            }
        });
    }

    /* access modifiers changed from: private */
    public void onSubmitCodeError(String str) {
        this.mIsSubmittingToken = false;
        hideWaitingView();
        Toast.makeText(this, str, 0).show();
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || isFinishing()) {
            return false;
        }
        submitCode();
        return true;
    }

    public void afterTextChanged(Editable editable) {
        if (this.mPhoneNumberCodeLayout.getError() != null) {
            this.mPhoneNumberCodeLayout.setError(null);
            this.mPhoneNumberCodeLayout.setErrorEnabled(false);
        }
    }
}
