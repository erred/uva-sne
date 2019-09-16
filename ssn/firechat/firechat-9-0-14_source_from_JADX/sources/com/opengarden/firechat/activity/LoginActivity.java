package com.opengarden.firechat.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.p000v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.facebook.common.util.UriUtil;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.LoginHandler;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.PhoneNumberHandler;
import com.opengarden.firechat.RegistrationManager;
import com.opengarden.firechat.RegistrationManager.RegistrationListener;
import com.opengarden.firechat.RegistrationManager.ThreePidValidationListener;
import com.opengarden.firechat.RegistrationManager.UsernameValidityListener;
import com.opengarden.firechat.UnrecognizedCertHandler;
import com.opengarden.firechat.UnrecognizedCertHandler.Callback;
import com.opengarden.firechat.activity.util.RequestCodesKt;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.client.LoginRestClient;
import com.opengarden.firechat.matrixsdk.rest.client.ProfileRestClient;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.rest.model.login.LoginFlow;
import com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.ssl.CertUtil;
import com.opengarden.firechat.matrixsdk.ssl.UnrecognizedCertificateException;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorRegistrationReceiver;
import com.opengarden.firechat.receiver.VectorUniversalLinkReceiver;
import com.opengarden.firechat.repositories.ServerUrlsRepository;
import com.opengarden.firechat.services.EventStreamService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends MXCActionBarActivity implements RegistrationListener, UsernameValidityListener {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "LoginActivity";
    private static final int MODE_ACCOUNT_CREATION = 2;
    private static final int MODE_ACCOUNT_CREATION_THREE_PID = 5;
    private static final int MODE_FORGOT_PASSWORD = 3;
    private static final int MODE_FORGOT_PASSWORD_WAITING_VALIDATION = 4;
    private static final int MODE_LOGIN = 1;
    private static final int MODE_UNKNOWN = 0;
    private static final int REGISTER_POLLING_PERIOD = 10000;
    private static final int REQUEST_LOGIN_COUNTRY = 5678;
    private static final int REQUEST_REGISTRATION_COUNTRY = 1245;
    private static final String SAVED_CREATION_EMAIL_THREEPID = "SAVED_CREATION_EMAIL_THREEPID";
    private static final String SAVED_CREATION_PASSWORD1 = "SAVED_CREATION_PASSWORD1";
    private static final String SAVED_CREATION_PASSWORD2 = "SAVED_CREATION_PASSWORD2";
    private static final String SAVED_CREATION_REGISTRATION_RESPONSE = "SAVED_CREATION_REGISTRATION_RESPONSE";
    private static final String SAVED_CREATION_USER_NAME = "SAVED_CREATION_USER_NAME";
    private static final String SAVED_FORGOT_EMAIL_ADDRESS = "SAVED_FORGOT_EMAIL_ADDRESS";
    private static final String SAVED_FORGOT_PASSWORD1 = "SAVED_FORGOT_PASSWORD1";
    private static final String SAVED_FORGOT_PASSWORD2 = "SAVED_FORGOT_PASSWORD2";
    private static final String SAVED_HOME_SERVER_URL = "SAVED_HOME_SERVER_URL";
    private static final String SAVED_IDENTITY_SERVER_URL = "SAVED_IDENTITY_SERVER_URL";
    private static final String SAVED_IS_SERVER_URL_EXPANDED = "SAVED_IS_SERVER_URL_EXPANDED";
    private static final String SAVED_LOGIN_EMAIL_ADDRESS = "SAVED_LOGIN_EMAIL_ADDRESS";
    private static final String SAVED_LOGIN_PASSWORD_ADDRESS = "SAVED_LOGIN_PASSWORD_ADDRESS";
    private static final String SAVED_MODE = "SAVED_MODE";
    private View mButtonsView;
    private EditText mCreationPassword1TextView;
    private EditText mCreationPassword2TextView;
    private EditText mCreationUsernameTextView;
    private Dialog mCurrentDialog;
    /* access modifiers changed from: private */
    public EditText mEmailAddress;
    /* access modifiers changed from: private */
    public HashMap<String, String> mEmailValidationExtraParams;
    private TextView mForgotEmailTextView;
    private EditText mForgotPassword1TextView;
    private EditText mForgotPassword2TextView;
    private Button mForgotPasswordButton;
    /* access modifiers changed from: private */
    public HashMap<String, String> mForgotPid = null;
    private Button mForgotValidateEmailButton;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private View mHomeServerOptionLayout;
    /* access modifiers changed from: private */
    public EditText mHomeServerText;
    /* access modifiers changed from: private */
    public String mHomeServerUrl = null;
    private View mHomeServerUrlsLayout;
    private HomeServerConnectionConfig mHomeserverConnectionConfig;
    /* access modifiers changed from: private */
    public EditText mIdentityServerText;
    /* access modifiers changed from: private */
    public String mIdentityServerUrl = null;
    private boolean mIsMailValidationPending;
    /* access modifiers changed from: private */
    public boolean mIsPasswordResetted;
    /* access modifiers changed from: private */
    public boolean mIsPendingLogin;
    /* access modifiers changed from: private */
    public boolean mIsWaitingNetworkConnection = false;
    private Button mLoginButton;
    private EditText mLoginEmailTextView;
    private final LoginHandler mLoginHandler = new LoginHandler();
    private RelativeLayout mLoginMaskView;
    private EditText mLoginPasswordTextView;
    private PhoneNumberHandler mLoginPhoneNumberHandler;
    private View mMainLayout;
    /* access modifiers changed from: private */
    public int mMode = 1;
    private final BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    if (LoginActivity.this.mIsWaitingNetworkConnection) {
                        LoginActivity.this.refreshDisplay();
                    } else {
                        LoginActivity.this.removeNetworkStateNotificationListener();
                    }
                }
            } catch (Exception e) {
                String access$300 = LoginActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## BroadcastReceiver onReceive failed ");
                sb.append(e.getMessage());
                Log.m211e(access$300, sb.toString());
            }
        }
    };
    private TextView mPasswordForgottenTxtView;
    private ThreePid mPendingEmailValidation;
    private EditText mPhoneNumber;
    private View mPhoneNumberLayout;
    private TextView mProgressTextView;
    /* access modifiers changed from: private */
    public Button mRegisterButton;
    /* access modifiers changed from: private */
    public Runnable mRegisterPollingRunnable;
    private RegistrationFlowResponse mRegistrationResponse;
    private Button mSkipThreePidButton;
    private Button mSubmitThreePidButton;
    private TextView mThreePidInstructions;
    private Parcelable mUniversalLinkUri;
    /* access modifiers changed from: private */
    public CheckBox mUseCustomHomeServersCheckbox;

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_login;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.mLoginPhoneNumberHandler != null) {
            this.mLoginPhoneNumberHandler.release();
        }
        if (this.mCurrentDialog != null) {
            this.mCurrentDialog.dismiss();
            this.mCurrentDialog = null;
        }
        cancelEmailPolling();
        RegistrationManager.getInstance().resetSingleton();
        super.onDestroy();
        Log.m213i(LOG_TAG, "## onDestroy(): IN");
        this.mMode = 0;
        this.mEmailValidationExtraParams = null;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        removeNetworkStateNotificationListener();
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.m209d(LOG_TAG, "## onNewIntent(): IN ");
        if (intent == null) {
            Log.m209d(LOG_TAG, "## onNewIntent(): Unexpected value - aIntent=null ");
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.m209d(LOG_TAG, "## onNewIntent(): Unexpected value - extras are missing");
        } else if (extras.containsKey(VectorRegistrationReceiver.EXTRA_EMAIL_VALIDATION_PARAMS)) {
            Log.m209d(LOG_TAG, "## onNewIntent() Login activity started by email verification for registration");
            if (processEmailValidationExtras(extras)) {
                checkIfMailValidationPending();
            }
        }
    }

    public void initUiAndData() {
        if (getIntent() == null) {
            Log.m209d(LOG_TAG, "## onCreate(): IN with no intent");
        } else {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onCreate(): IN with flags ");
            sb.append(Integer.toHexString(getIntent().getFlags()));
            Log.m209d(str, sb.toString());
        }
        CommonActivityUtils.onApplicationStarted(this);
        Intent intent = getIntent();
        if (hasCredentials()) {
            if (intent != null && (intent.getFlags() & 4194304) == 0) {
                Log.m209d(LOG_TAG, "## onCreate(): goToSplash because the credentials are already provided.");
                goToSplash();
            } else if (EventStreamService.getInstance() == null) {
                Log.m209d(LOG_TAG, "## onCreate(): goToSplash with credentials but there is no event stream service.");
                goToSplash();
            } else {
                Log.m209d(LOG_TAG, "## onCreate(): close the login screen because it is a temporary task");
            }
            finish();
            return;
        }
        this.mLoginMaskView = (RelativeLayout) findViewById(C1299R.C1301id.flow_ui_mask_login);
        this.mLoginEmailTextView = (EditText) findViewById(C1299R.C1301id.login_user_name);
        EditText editText = (EditText) findViewById(C1299R.C1301id.login_phone_number_value);
        ((EditText) findViewById(C1299R.C1301id.login_phone_number_country)).setCompoundDrawablesWithIntrinsicBounds(null, null, CommonActivityUtils.tintDrawable(this, ContextCompat.getDrawable(this, C1299R.C1300drawable.ic_material_expand_more_black), C1299R.attr.settings_icon_tint_color), null);
        this.mLoginPasswordTextView = (EditText) findViewById(C1299R.C1301id.login_password);
        this.mCreationUsernameTextView = (EditText) findViewById(C1299R.C1301id.creation_your_name);
        this.mCreationPassword1TextView = (EditText) findViewById(C1299R.C1301id.creation_password1);
        this.mCreationPassword2TextView = (EditText) findViewById(C1299R.C1301id.creation_password2);
        this.mThreePidInstructions = (TextView) findViewById(C1299R.C1301id.instructions);
        this.mEmailAddress = (EditText) findViewById(C1299R.C1301id.registration_email);
        this.mPhoneNumberLayout = findViewById(C1299R.C1301id.registration_phone_number);
        this.mPhoneNumber = (EditText) findViewById(C1299R.C1301id.registration_phone_number_value);
        ((EditText) findViewById(C1299R.C1301id.registration_phone_number_country)).setCompoundDrawablesWithIntrinsicBounds(null, null, CommonActivityUtils.tintDrawable(this, ContextCompat.getDrawable(this, C1299R.C1300drawable.ic_material_expand_more_black), C1299R.attr.settings_icon_tint_color), null);
        this.mSubmitThreePidButton = (Button) findViewById(C1299R.C1301id.button_submit);
        this.mSkipThreePidButton = (Button) findViewById(C1299R.C1301id.button_skip);
        this.mPasswordForgottenTxtView = (TextView) findViewById(C1299R.C1301id.login_forgot_password);
        this.mForgotEmailTextView = (TextView) findViewById(C1299R.C1301id.forget_email_address);
        this.mForgotPassword1TextView = (EditText) findViewById(C1299R.C1301id.forget_new_password);
        this.mForgotPassword2TextView = (EditText) findViewById(C1299R.C1301id.forget_confirm_new_password);
        this.mHomeServerOptionLayout = findViewById(C1299R.C1301id.homeserver_layout);
        this.mHomeServerText = (EditText) findViewById(C1299R.C1301id.login_matrix_server_url);
        this.mIdentityServerText = (EditText) findViewById(C1299R.C1301id.login_identity_url);
        this.mLoginButton = (Button) findViewById(C1299R.C1301id.button_login);
        this.mRegisterButton = (Button) findViewById(C1299R.C1301id.button_register);
        this.mForgotPasswordButton = (Button) findViewById(C1299R.C1301id.button_reset_password);
        this.mForgotValidateEmailButton = (Button) findViewById(C1299R.C1301id.button_forgot_email_validate);
        this.mHomeServerUrlsLayout = findViewById(C1299R.C1301id.login_matrix_server_options_layout);
        this.mUseCustomHomeServersCheckbox = (CheckBox) findViewById(C1299R.C1301id.display_server_url_expand_checkbox);
        this.mProgressTextView = (TextView) findViewById(C1299R.C1301id.flow_progress_message_textview);
        this.mMainLayout = findViewById(C1299R.C1301id.main_input_layout);
        this.mButtonsView = findViewById(C1299R.C1301id.login_actions_bar);
        if (isFirstCreation()) {
            this.mHomeServerText.setText(ServerUrlsRepository.INSTANCE.getLastHomeServerUrl(this));
            this.mHomeServerText.setText(ServerUrlsRepository.INSTANCE.getLastHomeServerUrl(this));
        } else {
            restoreSavedData(getSavedInstanceState());
        }
        if (ServerUrlsRepository.INSTANCE.isDefaultHomeServerUrl(this, this.mHomeServerText.getText().toString())) {
            ServerUrlsRepository.INSTANCE.isDefaultIdentityServerUrl(this, this.mIdentityServerText.getText().toString());
        }
        this.mLoginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.onLoginClick();
            }
        });
        this.mRegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.onRegisterClick(true);
            }
        });
        this.mForgotPasswordButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.onForgotPasswordClick();
            }
        });
        this.mForgotValidateEmailButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.onForgotOnEmailValidated(LoginActivity.this.getHsConfig());
            }
        });
        this.mHomeServerText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                LoginActivity.this.onHomeServerUrlUpdate(true);
                return true;
            }
        });
        this.mHomeServerText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    LoginActivity.this.onHomeServerUrlUpdate(true);
                }
            }
        });
        this.mIdentityServerText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                LoginActivity.this.onIdentityServerUrlUpdate(true);
                return true;
            }
        });
        this.mIdentityServerText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    LoginActivity.this.onIdentityServerUrlUpdate(true);
                }
            }
        });
        this.mPasswordForgottenTxtView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.mMode = 3;
                LoginActivity.this.refreshDisplay();
            }
        });
        this.mUseCustomHomeServersCheckbox.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.mUseCustomHomeServersCheckbox.post(new Runnable() {
                    public void run() {
                        LoginActivity.this.mHomeServerUrl = null;
                        LoginActivity.this.mIdentityServerUrl = null;
                        LoginActivity.this.onIdentityServerUrlUpdate(false);
                        LoginActivity.this.onHomeServerUrlUpdate(false);
                        LoginActivity.this.refreshDisplay();
                    }
                });
            }
        });
        refreshDisplay();
        CommonActivityUtils.updateBadgeCount((Context) this, 0);
        this.mHomeServerText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String access$1500 = LoginActivity.sanitizeUrl(charSequence.toString());
                if (!TextUtils.equals(access$1500, charSequence.toString())) {
                    LoginActivity.this.mHomeServerText.setText(access$1500);
                    LoginActivity.this.mHomeServerText.setSelection(access$1500.length());
                }
            }
        });
        this.mIdentityServerText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String access$1500 = LoginActivity.sanitizeUrl(charSequence.toString());
                if (!TextUtils.equals(access$1500, charSequence.toString())) {
                    LoginActivity.this.mIdentityServerText.setText(access$1500);
                    LoginActivity.this.mIdentityServerText.setSelection(access$1500.length());
                }
            }
        });
        this.mHandler = new Handler(getMainLooper());
        Bundle extras = intent != null ? getIntent().getExtras() : null;
        if (extras != null) {
            if (extras.containsKey(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) {
                this.mUniversalLinkUri = extras.getParcelable(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
                Log.m209d(LOG_TAG, "## onCreate() Login activity started by universal link");
            } else if (extras.containsKey(VectorRegistrationReceiver.EXTRA_EMAIL_VALIDATION_PARAMS)) {
                Log.m209d(LOG_TAG, "## onCreate() Login activity started by email verification for registration");
                if (processEmailValidationExtras(extras)) {
                    this.mPendingEmailValidation = null;
                    checkIfMailValidationPending();
                }
            }
        }
        if (this.mPendingEmailValidation != null) {
            Log.m209d(LOG_TAG, "## onCreate() An email validation was pending");
            HomeServerConnectionConfig hsConfig = getHsConfig();
            if (!(this.mRegistrationResponse == null || hsConfig == null || isFirstCreation())) {
                String string = getSavedInstanceState().getString(SAVED_CREATION_USER_NAME);
                String string2 = getSavedInstanceState().getString(SAVED_CREATION_PASSWORD1);
                Log.m209d(LOG_TAG, "## onCreate() Resume email validation");
                enableLoadingScreen(true);
                RegistrationManager.getInstance().setSupportedRegistrationFlows(this.mRegistrationResponse);
                RegistrationManager.getInstance().setAccountData(string, string2);
                RegistrationManager.getInstance().addEmailThreePid(this.mPendingEmailValidation);
                RegistrationManager.getInstance().attemptRegistration(this, this);
                onWaitingEmailValidation();
            }
        }
    }

    public void onServerUrlsUpdateFromReferrer() {
        this.mHomeServerText.setText(ServerUrlsRepository.INSTANCE.getLastHomeServerUrl(this));
        this.mIdentityServerText.setText(ServerUrlsRepository.INSTANCE.getLastIdentityServerUrl(this));
        if (!this.mUseCustomHomeServersCheckbox.isChecked()) {
            this.mUseCustomHomeServersCheckbox.performClick();
        }
    }

    private String getHomeServerUrl() {
        String defaultHomeServerUrl = ServerUrlsRepository.INSTANCE.getDefaultHomeServerUrl(this);
        if (!this.mUseCustomHomeServersCheckbox.isChecked()) {
            return defaultHomeServerUrl;
        }
        String trim = this.mHomeServerText.getText().toString().trim();
        return trim.endsWith("/") ? trim.substring(0, trim.length() - 1) : trim;
    }

    private String getIdentityServerUrl() {
        return ServerUrlsRepository.INSTANCE.getDefaultIdentityServerUrl(this);
    }

    /* access modifiers changed from: private */
    public void addNetworkStateNotificationListener() {
        if (Matrix.getInstance(getApplicationContext()) != null && !this.mIsWaitingNetworkConnection) {
            try {
                registerReceiver(this.mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                this.mIsWaitingNetworkConnection = true;
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## addNetworkStateNotificationListener : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeNetworkStateNotificationListener() {
        if (Matrix.getInstance(getApplicationContext()) != null && this.mIsWaitingNetworkConnection) {
            try {
                unregisterReceiver(this.mNetworkReceiver);
                this.mIsWaitingNetworkConnection = false;
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## removeNetworkStateNotificationListener : ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean onHomeServerUrlUpdate(boolean z) {
        if (TextUtils.equals(this.mHomeServerUrl, getHomeServerUrl())) {
            return false;
        }
        this.mHomeServerUrl = getHomeServerUrl();
        this.mRegistrationResponse = null;
        this.mHomeserverConnectionConfig = null;
        this.mRegisterButton.setVisibility(0);
        if (z) {
            checkFlows();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public boolean onIdentityServerUrlUpdate(boolean z) {
        if (TextUtils.equals(this.mIdentityServerUrl, getIdentityServerUrl())) {
            return false;
        }
        this.mIdentityServerUrl = getIdentityServerUrl();
        this.mRegistrationResponse = null;
        this.mHomeserverConnectionConfig = null;
        this.mRegisterButton.setVisibility(0);
        if (z) {
            checkFlows();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Log.m209d(LOG_TAG, "## onResume(): IN");
        this.mHomeServerUrl = getHomeServerUrl();
        this.mIdentityServerUrl = getIdentityServerUrl();
        checkFlows();
    }

    private void fallbackToLoginMode() {
        this.mMainLayout.setVisibility(0);
        cancelEmailPolling();
        this.mEmailValidationExtraParams = null;
        this.mRegistrationResponse = null;
        showMainLayout();
        enableLoadingScreen(false);
        this.mMode = 1;
        refreshDisplay();
    }

    private void fallbackToRegistrationMode() {
        this.mMainLayout.setVisibility(0);
        showMainLayout();
        enableLoadingScreen(false);
        this.mMode = 2;
        refreshDisplay();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            Log.m209d(LOG_TAG, "KEYCODE_BACK pressed");
            if (2 == this.mMode && this.mRegistrationResponse != null) {
                Log.m209d(LOG_TAG, "## cancel the registration mode");
                fallbackToLoginMode();
                return true;
            } else if (3 == this.mMode || 4 == this.mMode) {
                Log.m209d(LOG_TAG, "## cancel the forgot password mode");
                fallbackToLoginMode();
                return true;
            } else if (5 == this.mMode) {
                Log.m209d(LOG_TAG, "## cancel the three pid mode");
                cancelEmailPolling();
                RegistrationManager.getInstance().clearThreePid();
                this.mEmailAddress.setText("");
                fallbackToRegistrationMode();
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    private boolean hasCredentials() {
        boolean z = false;
        try {
            MXSession defaultSession = Matrix.getInstance(this).getDefaultSession();
            if (defaultSession != null && defaultSession.isAlive()) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## Exception: ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
            Log.m211e(LOG_TAG, "## hasCredentials() : invalid credentials");
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        CommonActivityUtils.logout(LoginActivity.this);
                    } catch (Exception e) {
                        String access$300 = LoginActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## Exception: ");
                        sb.append(e.getMessage());
                        Log.m217w(access$300, sb.toString());
                    }
                }
            });
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void goToSplash() {
        Log.m209d(LOG_TAG, "## gotoSplash(): Go to splash.");
        Intent intent = new Intent(this, SplashActivity.class);
        if (this.mUniversalLinkUri != null) {
            intent.putExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI, this.mUniversalLinkUri);
        }
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void saveServerUrlsIfCustomValuesHasBeenEntered() {
        if (this.mUseCustomHomeServersCheckbox.isChecked()) {
            ServerUrlsRepository.INSTANCE.saveServerUrls(this, this.mHomeServerText.getText().toString().trim(), this.mIdentityServerText.getText().toString().trim());
        }
    }

    private void checkFlows() {
        if (this.mMode == 1 || this.mMode == 3 || this.mMode == 4) {
            checkLoginFlows();
        } else {
            checkRegistrationFlows();
        }
    }

    /* access modifiers changed from: private */
    public void onForgotPasswordClick() {
        final HomeServerConnectionConfig hsConfig = getHsConfig();
        if (hsConfig != null) {
            final String trim = this.mForgotEmailTextView.getText().toString().trim();
            String trim2 = this.mForgotPassword1TextView.getText().toString().trim();
            String trim3 = this.mForgotPassword2TextView.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_reset_password_missing_email), 0).show();
            } else if (TextUtils.isEmpty(trim2)) {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_reset_password_missing_password), 0).show();
            } else if (trim2.length() < 6) {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_invalid_password), 0).show();
            } else if (!TextUtils.equals(trim2, trim3)) {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_password_dont_match), 0).show();
            } else if (TextUtils.isEmpty(trim) || Patterns.EMAIL_ADDRESS.matcher(trim).matches()) {
                enableLoadingScreen(true);
                ProfileRestClient profileRestClient = new ProfileRestClient(hsConfig);
                Log.m209d(LOG_TAG, "onForgotPasswordClick");
                profileRestClient.forgetPassword(trim, new ApiCallback<ThreePid>() {
                    public void onSuccess(ThreePid threePid) {
                        if (LoginActivity.this.mMode == 3) {
                            Log.m209d(LoginActivity.LOG_TAG, "onForgotPasswordClick : requestEmailValidationToken succeeds");
                            LoginActivity.this.enableLoadingScreen(false);
                            LoginActivity.this.hideMainLayoutAndToast(LoginActivity.this.getResources().getString(C1299R.string.auth_reset_password_email_validation_message, new Object[]{trim}));
                            LoginActivity.this.mMode = 4;
                            LoginActivity.this.refreshDisplay();
                            LoginActivity.this.mForgotPid = new HashMap();
                            LoginActivity.this.mForgotPid.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_CLIENT_SECRET, threePid.clientSecret);
                            LoginActivity.this.mForgotPid.put("id_server", hsConfig.getIdentityServerUri().getHost());
                            LoginActivity.this.mForgotPid.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_IDENTITY_SERVER_SESSION_ID, threePid.sid);
                        }
                    }

                    /* access modifiers changed from: private */
                    public void onError(String str) {
                        String access$300 = LoginActivity.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("onForgotPasswordClick : requestEmailValidationToken fails with error ");
                        sb.append(str);
                        Log.m211e(access$300, sb.toString());
                        if (LoginActivity.this.mMode == 3) {
                            LoginActivity.this.enableLoadingScreen(false);
                            Toast.makeText(LoginActivity.this, str, 1).show();
                        }
                    }

                    public void onNetworkError(final Exception exc) {
                        if (LoginActivity.this.mMode == 3) {
                            UnrecognizedCertificateException certificateException = CertUtil.getCertificateException(exc);
                            if (certificateException != null) {
                                UnrecognizedCertHandler.show(hsConfig, certificateException.getFingerprint(), false, new Callback() {
                                    public void onAccept() {
                                        LoginActivity.this.onForgotPasswordClick();
                                    }

                                    public void onIgnore() {
                                        C141015.this.onError(exc.getLocalizedMessage());
                                    }

                                    public void onReject() {
                                        C141015.this.onError(exc.getLocalizedMessage());
                                    }
                                });
                                return;
                            }
                            onError(exc.getLocalizedMessage());
                        }
                    }

                    public void onUnexpectedError(Exception exc) {
                        onError(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        if (TextUtils.equals(MatrixError.THREEPID_NOT_FOUND, matrixError.errcode)) {
                            onError(LoginActivity.this.getString(C1299R.string.account_email_not_found_error));
                        } else {
                            onError(matrixError.getLocalizedMessage());
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_invalid_email), 0).show();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onForgotOnEmailValidated(final HomeServerConnectionConfig homeServerConnectionConfig) {
        if (this.mIsPasswordResetted) {
            Log.m209d(LOG_TAG, "onForgotOnEmailValidated : go back to login screen");
            this.mIsPasswordResetted = false;
            this.mMode = 1;
            showMainLayout();
            refreshDisplay();
            return;
        }
        ProfileRestClient profileRestClient = new ProfileRestClient(homeServerConnectionConfig);
        enableLoadingScreen(true);
        Log.m209d(LOG_TAG, "onForgotOnEmailValidated : try to reset the password");
        profileRestClient.resetPassword(this.mForgotPassword1TextView.getText().toString().trim(), this.mForgotPid, new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (LoginActivity.this.mMode == 4) {
                    Log.m209d(LoginActivity.LOG_TAG, "onForgotOnEmailValidated : the password has been updated");
                    LoginActivity.this.enableLoadingScreen(false);
                    LoginActivity.this.hideMainLayoutAndToast(LoginActivity.this.getResources().getString(C1299R.string.auth_reset_password_success_message));
                    LoginActivity.this.mIsPasswordResetted = true;
                    LoginActivity.this.refreshDisplay();
                }
            }

            private void onError(String str, boolean z) {
                if (LoginActivity.this.mMode == 4) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onForgotOnEmailValidated : failed ");
                    sb.append(str);
                    Log.m209d(access$300, sb.toString());
                    Toast.makeText(LoginActivity.this.getApplicationContext(), str, 1).show();
                    LoginActivity.this.enableLoadingScreen(false);
                    if (z) {
                        LoginActivity.this.showMainLayout();
                        LoginActivity.this.mMode = 1;
                        LoginActivity.this.refreshDisplay();
                    }
                }
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getLocalizedMessage(), false);
            }

            public void onMatrixError(MatrixError matrixError) {
                if (LoginActivity.this.mMode != 4) {
                    return;
                }
                if (TextUtils.equals(matrixError.errcode, MatrixError.UNAUTHORIZED)) {
                    Log.m209d(LoginActivity.LOG_TAG, "onForgotOnEmailValidated : failed UNAUTHORIZED");
                    onError(LoginActivity.this.getResources().getString(C1299R.string.auth_reset_password_error_unauthorized), false);
                } else if (!TextUtils.equals(matrixError.errcode, MatrixError.NOT_FOUND)) {
                    onError(matrixError.getLocalizedMessage(), true);
                } else if (TextUtils.equals(homeServerConnectionConfig.getHomeserverUri().toString(), LoginActivity.this.getString(C1299R.string.vector_im_server_url))) {
                    homeServerConnectionConfig.setHomeserverUri(Uri.parse(LoginActivity.this.getString(C1299R.string.matrix_org_server_url)));
                    LoginActivity.this.onForgotOnEmailValidated(homeServerConnectionConfig);
                    Log.m209d(LoginActivity.LOG_TAG, "onForgotOnEmailValidated : test with matrix.org as HS");
                } else {
                    onError(matrixError.getLocalizedMessage(), false);
                }
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getLocalizedMessage(), true);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onFailureDuringAuthRequest(MatrixError matrixError) {
        String localizedMessage = matrixError.getLocalizedMessage();
        enableLoadingScreen(false);
        String str = matrixError.errcode;
        if (str != null) {
            if (TextUtils.equals(str, MatrixError.FORBIDDEN)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_forbidden);
            } else if (TextUtils.equals(str, MatrixError.UNKNOWN_TOKEN)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_unknown_token);
            } else if (TextUtils.equals(str, MatrixError.BAD_JSON)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_bad_json);
            } else if (TextUtils.equals(str, MatrixError.NOT_JSON)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_not_json);
            } else if (TextUtils.equals(str, MatrixError.LIMIT_EXCEEDED)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_limit_exceeded);
            } else if (TextUtils.equals(str, MatrixError.USER_IN_USE)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_user_in_use);
            } else if (TextUtils.equals(str, MatrixError.LOGIN_EMAIL_URL_NOT_YET)) {
                localizedMessage = getResources().getString(C1299R.string.login_error_login_email_not_yet);
            }
        }
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onFailureDuringAuthRequest(): Msg= \"");
        sb.append(localizedMessage);
        sb.append("\"");
        Log.m211e(str2, sb.toString());
        Toast.makeText(getApplicationContext(), localizedMessage, 1).show();
    }

    private boolean processEmailValidationExtras(Bundle bundle) {
        Log.m209d(LOG_TAG, "## processEmailValidationExtras() IN");
        boolean z = true;
        if (bundle != null) {
            this.mEmailValidationExtraParams = (HashMap) bundle.getSerializable(VectorRegistrationReceiver.EXTRA_EMAIL_VALIDATION_PARAMS);
            if (this.mEmailValidationExtraParams != null) {
                this.mIsMailValidationPending = true;
                this.mMode = 2;
                Matrix.getInstance(this).clearSessions(this, true, null);
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## processEmailValidationExtras() OUT - reCode=");
                sb.append(z);
                Log.m209d(str, sb.toString());
                return z;
            }
        } else {
            Log.m211e(LOG_TAG, "## processEmailValidationExtras(): Bundle is missing - aRegistrationBundle=null");
        }
        z = false;
        String str2 = LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## processEmailValidationExtras() OUT - reCode=");
        sb2.append(z);
        Log.m209d(str2, sb2.toString());
        return z;
    }

    /* access modifiers changed from: private */
    public void startEmailOwnershipValidation(HashMap<String, String> hashMap) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## startEmailOwnershipValidation(): IN aMapParams=");
        sb.append(hashMap);
        Log.m209d(str, sb.toString());
        if (hashMap != null) {
            enableLoadingScreen(true);
            hideMainLayoutAndToast("");
            this.mMode = 2;
            String str2 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_TOKEN);
            String str3 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_CLIENT_SECRET);
            String str4 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_IDENTITY_SERVER_SESSION_ID);
            String str5 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_SESSION_ID);
            String str6 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_HOME_SERVER_URL);
            String str7 = (String) hashMap.get(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_IDENTITY_SERVER_URL);
            if (str6 == null) {
                str6 = getHomeServerUrl();
            }
            String str8 = str6;
            if (str7 == null) {
                str7 = getIdentityServerUrl();
            }
            String str9 = str7;
            try {
                Uri.parse(str8);
                Uri.parse(str9);
                submitEmailToken(str2, str3, str4, str5, str8, str9);
            } catch (Exception unused) {
                Toast.makeText(this, getString(C1299R.string.login_error_invalid_home_server), 0).show();
            }
        } else {
            Log.m209d(LOG_TAG, "## startEmailOwnershipValidation(): skipped");
        }
    }

    private void submitEmailToken(String str, String str2, String str3, String str4, String str5, String str6) {
        HomeServerConnectionConfig homeServerConnectionConfig = new HomeServerConnectionConfig(Uri.parse(str5), Uri.parse(str6), null, new ArrayList(), false);
        this.mHomeserverConnectionConfig = homeServerConnectionConfig;
        RegistrationManager.getInstance().setHsConfig(homeServerConnectionConfig);
        Log.m209d(LOG_TAG, "## submitEmailToken(): IN");
        if (this.mMode == 2) {
            Log.m209d(LOG_TAG, "## submitEmailToken(): calling submitEmailTokenValidation()..");
            LoginHandler loginHandler = this.mLoginHandler;
            Context applicationContext = getApplicationContext();
            final String str7 = str4;
            final String str8 = str2;
            final HomeServerConnectionConfig homeServerConnectionConfig2 = homeServerConnectionConfig;
            final String str9 = str3;
            final String str10 = str6;
            C141317 r0 = new ApiCallback<Boolean>() {
                private void errorHandler(String str) {
                    Log.m209d(LoginActivity.LOG_TAG, "## submitEmailToken(): errorHandler().");
                    LoginActivity.this.enableLoadingScreen(false);
                    LoginActivity.this.setActionButtonsEnabled(false);
                    LoginActivity.this.showMainLayout();
                    LoginActivity.this.refreshDisplay();
                    Toast.makeText(LoginActivity.this.getApplicationContext(), str, 1).show();
                }

                public void onSuccess(Boolean bool) {
                    if (!bool.booleanValue()) {
                        Log.m209d(LoginActivity.LOG_TAG, "## submitEmailToken(): onSuccess() - failed (success=false)");
                        errorHandler(LoginActivity.this.getString(C1299R.string.login_error_unable_register_mail_ownership));
                    } else if (str7 == null) {
                        Log.m209d(LoginActivity.LOG_TAG, "## submitEmailToken(): onSuccess() - the password update is in progress");
                        LoginActivity.this.mMode = 4;
                        LoginActivity.this.mForgotPid = new HashMap();
                        LoginActivity.this.mForgotPid.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_CLIENT_SECRET, str8);
                        LoginActivity.this.mForgotPid.put("id_server", homeServerConnectionConfig2.getIdentityServerUri().getHost());
                        LoginActivity.this.mForgotPid.put(VectorRegistrationReceiver.KEY_MAIL_VALIDATION_IDENTITY_SERVER_SESSION_ID, str9);
                        LoginActivity.this.mIsPasswordResetted = false;
                        LoginActivity.this.onForgotOnEmailValidated(homeServerConnectionConfig2);
                    } else {
                        Log.m209d(LoginActivity.LOG_TAG, "## submitEmailToken(): onSuccess() - registerAfterEmailValidations() started");
                        LoginActivity.this.mMode = 2;
                        LoginActivity.this.enableLoadingScreen(true);
                        RegistrationManager.getInstance().registerAfterEmailValidation(LoginActivity.this, str8, str9, str10, str7, LoginActivity.this);
                    }
                }

                public void onNetworkError(Exception exc) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## submitEmailToken(): onNetworkError() Msg=");
                    sb.append(exc.getLocalizedMessage());
                    Log.m209d(access$300, sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(LoginActivity.this.getString(C1299R.string.login_error_unable_register));
                    sb2.append(" : ");
                    sb2.append(exc.getLocalizedMessage());
                    errorHandler(sb2.toString());
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## submitEmailToken(): onMatrixError() Msg=");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m209d(access$300, sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(LoginActivity.this.getString(C1299R.string.login_error_unable_register));
                    sb2.append(" : ");
                    sb2.append(matrixError.getLocalizedMessage());
                    errorHandler(sb2.toString());
                }

                public void onUnexpectedError(Exception exc) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## submitEmailToken(): onUnexpectedError() Msg=");
                    sb.append(exc.getLocalizedMessage());
                    Log.m209d(access$300, sb.toString());
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(LoginActivity.this.getString(C1299R.string.login_error_unable_register));
                    sb2.append(" : ");
                    sb2.append(exc.getLocalizedMessage());
                    errorHandler(sb2.toString());
                }
            };
            loginHandler.submitEmailTokenValidation(applicationContext, homeServerConnectionConfig, str, str2, str9, r0);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0032, code lost:
        if (com.facebook.common.util.UriUtil.HTTPS_SCHEME.equals(r2.getScheme()) == false) goto L_0x0050;
     */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0062  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onRegistrationFlow(com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse r6) {
        /*
            r5 = this;
            r0 = 0
            r5.enableLoadingScreen(r0)
            r1 = 1
            r5.setActionButtonsEnabled(r1)
            r5.mRegistrationResponse = r6
            com.opengarden.firechat.RegistrationManager r6 = com.opengarden.firechat.RegistrationManager.getInstance()
            boolean r6 = r6.hasNonSupportedStage()
            if (r6 == 0) goto L_0x0076
            java.lang.String r6 = r5.getHomeServerUrl()
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0035 }
            java.lang.String r3 = "http"
            java.lang.String r4 = r2.getScheme()     // Catch:{ Exception -> 0x0035 }
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x0035 }
            if (r3 != 0) goto L_0x0051
            java.lang.String r3 = "https"
            java.lang.String r2 = r2.getScheme()     // Catch:{ Exception -> 0x0035 }
            boolean r2 = r3.equals(r2)     // Catch:{ Exception -> 0x0035 }
            if (r2 == 0) goto L_0x0050
            goto L_0x0051
        L_0x0035:
            r1 = move-exception
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "## Exception: "
            r3.append(r4)
            java.lang.String r1 = r1.getMessage()
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r1)
        L_0x0050:
            r1 = 0
        L_0x0051:
            if (r1 != 0) goto L_0x0062
            r6 = 2131689829(0x7f0f0165, float:1.9008684E38)
            java.lang.String r6 = r5.getString(r6)
            android.widget.Toast r6 = android.widget.Toast.makeText(r5, r6, r0)
            r6.show()
            return
        L_0x0062:
            r5.fallbackToLoginMode()
            android.content.Intent r0 = new android.content.Intent
            java.lang.Class<com.opengarden.firechat.activity.AccountCreationActivity> r1 = com.opengarden.firechat.activity.AccountCreationActivity.class
            r0.<init>(r5, r1)
            java.lang.String r1 = "AccountCreationActivity.EXTRA_HOME_SERVER_ID"
            r0.putExtra(r1, r6)
            r6 = 314(0x13a, float:4.4E-43)
            r5.startActivityForResult(r0, r6)
        L_0x0076:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.activity.LoginActivity.onRegistrationFlow(com.opengarden.firechat.matrixsdk.rest.model.login.RegistrationFlowResponse):void");
    }

    /* access modifiers changed from: private */
    public void checkIfMailValidationPending() {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## checkIfMailValidationPending(): mIsMailValidationPending=");
        sb.append(this.mIsMailValidationPending);
        Log.m209d(str, sb.toString());
        if (this.mRegistrationResponse == null) {
            Log.m209d(LOG_TAG, "## checkIfMailValidationPending(): pending mail validation delayed (mRegistrationResponse=null)");
        } else if (this.mIsMailValidationPending) {
            this.mIsMailValidationPending = false;
            cancelEmailPolling();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (LoginActivity.this.mEmailValidationExtraParams != null) {
                        LoginActivity.this.startEmailOwnershipValidation(LoginActivity.this.mEmailValidationExtraParams);
                    }
                }
            });
        } else {
            Log.m209d(LOG_TAG, "## checkIfMailValidationPending(): pending mail validation not started");
        }
    }

    private void checkRegistrationFlows() {
        Log.m209d(LOG_TAG, "## checkRegistrationFlows(): IN");
        if (this.mMode == 2) {
            if (this.mRegistrationResponse == null) {
                try {
                    HomeServerConnectionConfig hsConfig = getHsConfig();
                    if (hsConfig == null) {
                        setActionButtonsEnabled(false);
                    } else {
                        enableLoadingScreen(true);
                        this.mLoginHandler.getSupportedRegistrationFlows(this, hsConfig, new SimpleApiCallback<HomeServerConnectionConfig>() {
                            public void onSuccess(HomeServerConnectionConfig homeServerConnectionConfig) {
                            }

                            private void onError(String str) {
                                if (LoginActivity.this.mMode == 2) {
                                    LoginActivity.this.showMainLayout();
                                    LoginActivity.this.enableLoadingScreen(false);
                                    LoginActivity.this.refreshDisplay();
                                    Toast.makeText(LoginActivity.this.getApplicationContext(), str, 1).show();
                                }
                            }

                            public void onNetworkError(Exception exc) {
                                LoginActivity.this.addNetworkStateNotificationListener();
                                if (LoginActivity.this.mMode == 2) {
                                    String access$300 = LoginActivity.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Network Error: ");
                                    sb.append(exc.getMessage());
                                    Log.m212e(access$300, sb.toString(), exc);
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append(LoginActivity.this.getString(C1299R.string.login_error_registration_network_error));
                                    sb2.append(" : ");
                                    sb2.append(exc.getLocalizedMessage());
                                    onError(sb2.toString());
                                    LoginActivity.this.setActionButtonsEnabled(false);
                                }
                            }

                            public void onUnexpectedError(Exception exc) {
                                if (LoginActivity.this.mMode == 2) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(LoginActivity.this.getString(C1299R.string.login_error_unable_register));
                                    sb.append(" : ");
                                    sb.append(exc.getLocalizedMessage());
                                    onError(sb.toString());
                                }
                            }

                            public void onMatrixError(MatrixError matrixError) {
                                LoginActivity.this.removeNetworkStateNotificationListener();
                                if (LoginActivity.this.mMode == 2) {
                                    String access$300 = LoginActivity.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("## checkRegistrationFlows(): onMatrixError - Resp=");
                                    sb.append(matrixError.getLocalizedMessage());
                                    Log.m209d(access$300, sb.toString());
                                    RegistrationFlowResponse registrationFlowResponse = null;
                                    if (matrixError.mStatus != null) {
                                        if (matrixError.mStatus.intValue() == 401) {
                                            try {
                                                registrationFlowResponse = JsonUtils.toRegistrationFlowResponse(matrixError.mErrorBodyAsString);
                                            } catch (Exception e) {
                                                String access$3002 = LoginActivity.LOG_TAG;
                                                StringBuilder sb2 = new StringBuilder();
                                                sb2.append("JsonUtils.toRegistrationFlowResponse ");
                                                sb2.append(e.getLocalizedMessage());
                                                Log.m211e(access$3002, sb2.toString());
                                            }
                                        } else if (matrixError.mStatus.intValue() == 403) {
                                            LoginActivity.this.mRegisterButton.setVisibility(8);
                                            LoginActivity.this.mMode = 1;
                                            LoginActivity.this.refreshDisplay();
                                        }
                                    }
                                    if (registrationFlowResponse != null) {
                                        RegistrationManager.getInstance().setSupportedRegistrationFlows(registrationFlowResponse);
                                        LoginActivity.this.onRegistrationFlow(registrationFlowResponse);
                                    } else {
                                        LoginActivity.this.onFailureDuringAuthRequest(matrixError);
                                    }
                                    LoginActivity.this.checkIfMailValidationPending();
                                }
                            }
                        });
                    }
                } catch (Exception unused) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.login_error_invalid_home_server), 0).show();
                    enableLoadingScreen(false);
                }
            } else {
                setActionButtonsEnabled(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideMainLayoutAndToast(String str) {
        this.mMainLayout.setVisibility(8);
        this.mProgressTextView.setVisibility(0);
        this.mProgressTextView.setText(str);
        this.mButtonsView.setVisibility(8);
    }

    /* access modifiers changed from: private */
    public void showMainLayout() {
        this.mMainLayout.setVisibility(0);
        this.mProgressTextView.setVisibility(8);
        this.mButtonsView.setVisibility(0);
    }

    /* access modifiers changed from: private */
    public void onRegisterClick(boolean z) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onRegisterClick(): IN - checkRegistrationValues=");
        sb.append(z);
        Log.m209d(str, sb.toString());
        onClick();
        if (this.mMode != 2) {
            this.mMode = 2;
            refreshDisplay();
        } else if (this.mRegistrationResponse == null) {
            Log.m209d(LOG_TAG, "## onRegisterClick(): return - mRegistrationResponse=nuul");
        } else {
            String trim = this.mCreationUsernameTextView.getText().toString().trim();
            String trim2 = this.mCreationPassword1TextView.getText().toString().trim();
            String trim3 = this.mCreationPassword2TextView.getText().toString().trim();
            if (z) {
                if (TextUtils.isEmpty(trim)) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_invalid_user_name), 0).show();
                    return;
                } else if (TextUtils.isEmpty(trim2)) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_missing_password), 0).show();
                    return;
                } else if (trim2.length() < 6) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_invalid_password), 0).show();
                    return;
                } else if (!TextUtils.equals(trim2, trim3)) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_password_dont_match), 0).show();
                    return;
                } else if (!Pattern.compile("^[a-z0-9.\\-_]+$", 2).matcher(trim).matches()) {
                    Toast.makeText(getApplicationContext(), getString(C1299R.string.auth_invalid_user_name), 0).show();
                    return;
                }
            }
            RegistrationManager.getInstance().setAccountData(trim, trim2);
            RegistrationManager.getInstance().checkUsernameAvailability(this, this);
        }
    }

    private void onClick() {
        onIdentityServerUrlUpdate(false);
        onHomeServerUrlUpdate(false);
        checkFlows();
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(this.mHomeServerText.getWindowToken(), 0);
    }

    /* access modifiers changed from: private */
    public void onLoginClick() {
        if (onHomeServerUrlUpdate(true) || onIdentityServerUrlUpdate(true)) {
            this.mIsPendingLogin = true;
            Log.m209d(LOG_TAG, "## onLoginClick() : The user taps on login but the IS/HS did not loos the focus");
            return;
        }
        onClick();
        if (this.mMode != 1) {
            showMainLayout();
            this.mMode = 1;
            refreshDisplay();
            return;
        }
        this.mIsPendingLogin = false;
        HomeServerConnectionConfig hsConfig = getHsConfig();
        String homeServerUrl = getHomeServerUrl();
        String identityServerUrl = getIdentityServerUrl();
        if (!homeServerUrl.startsWith(UriUtil.HTTP_SCHEME)) {
            Toast.makeText(this, getString(C1299R.string.login_error_must_start_http), 0).show();
        } else if (!identityServerUrl.startsWith(UriUtil.HTTP_SCHEME)) {
            Toast.makeText(this, getString(C1299R.string.login_error_must_start_http), 0).show();
        } else {
            String trim = this.mLoginEmailTextView.getText().toString().trim();
            String trim2 = this.mLoginPasswordTextView.getText().toString().trim();
            if (TextUtils.isEmpty(trim2)) {
                Toast.makeText(this, getString(C1299R.string.auth_invalid_login_param), 0).show();
            } else if (!TextUtils.isEmpty(trim) || this.mLoginPhoneNumberHandler.isPhoneNumberValidForCountry()) {
                enableLoadingScreen(true);
                login(hsConfig, homeServerUrl, identityServerUrl, trim, "", "", trim2);
            } else if (this.mLoginPhoneNumberHandler.getPhoneNumber() != null) {
                Toast.makeText(this, C1299R.string.auth_invalid_phone, 0).show();
            } else {
                Toast.makeText(this, getString(C1299R.string.auth_invalid_login_param), 0).show();
            }
        }
    }

    private void login(HomeServerConnectionConfig homeServerConnectionConfig, String str, String str2, String str3, String str4, String str5, String str6) {
        try {
            this.mLoginHandler.login(this, homeServerConnectionConfig, str3, str4, str5, str6, new SimpleApiCallback<HomeServerConnectionConfig>(this) {
                public void onSuccess(HomeServerConnectionConfig homeServerConnectionConfig) {
                    LoginActivity.this.enableLoadingScreen(false);
                    LoginActivity.this.saveServerUrlsIfCustomValuesHasBeenEntered();
                    LoginActivity.this.goToSplash();
                    LoginActivity.this.finish();
                }

                public void onNetworkError(Exception exc) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onLoginClick : Network Error: ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                    LoginActivity.this.enableLoadingScreen(false);
                    Toast.makeText(LoginActivity.this.getApplicationContext(), LoginActivity.this.getString(C1299R.string.login_error_network_error), 1).show();
                }

                public void onUnexpectedError(Exception exc) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onLoginClick : onUnexpectedError");
                    sb.append(exc.getMessage());
                    Log.m211e(access$300, sb.toString());
                    LoginActivity.this.enableLoadingScreen(false);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(LoginActivity.this.getString(C1299R.string.login_error_unable_login));
                    sb2.append(" : ");
                    sb2.append(exc.getMessage());
                    Toast.makeText(LoginActivity.this.getApplicationContext(), sb2.toString(), 1).show();
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$300 = LoginActivity.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("onLoginClick : onMatrixError ");
                    sb.append(matrixError.getLocalizedMessage());
                    Log.m211e(access$300, sb.toString());
                    LoginActivity.this.enableLoadingScreen(false);
                    LoginActivity.this.onFailureDuringAuthRequest(matrixError);
                }
            });
        } catch (Exception unused) {
            Toast.makeText(this, getString(C1299R.string.login_error_invalid_home_server), 0).show();
            enableLoadingScreen(false);
            setActionButtonsEnabled(true);
        }
    }

    private void checkLoginFlows() {
        if (this.mMode == 1) {
            try {
                final HomeServerConnectionConfig hsConfig = getHsConfig();
                if (hsConfig == null) {
                    setActionButtonsEnabled(false);
                } else {
                    enableLoadingScreen(true);
                    this.mLoginHandler.getSupportedLoginFlows(this, hsConfig, new SimpleApiCallback<List<LoginFlow>>() {
                        public void onSuccess(List<LoginFlow> list) {
                            LoginActivity.this.removeNetworkStateNotificationListener();
                            boolean z = true;
                            if (LoginActivity.this.mMode == 1) {
                                LoginActivity.this.enableLoadingScreen(false);
                                LoginActivity.this.setActionButtonsEnabled(true);
                                for (LoginFlow loginFlow : list) {
                                    z &= TextUtils.equals(LoginRestClient.LOGIN_FLOW_TYPE_PASSWORD, loginFlow.type);
                                }
                                if (!z) {
                                    Intent intent = new Intent(LoginActivity.this, FallbackLoginActivity.class);
                                    intent.putExtra(FallbackLoginActivity.EXTRA_HOME_SERVER_ID, hsConfig.getHomeserverUri().toString());
                                    LoginActivity.this.startActivityForResult(intent, RequestCodesKt.FALLBACK_LOGIN_ACTIVITY_REQUEST_CODE);
                                } else if (LoginActivity.this.mIsPendingLogin) {
                                    LoginActivity.this.onLoginClick();
                                }
                            }
                        }

                        private void onError(String str) {
                            if (LoginActivity.this.mMode == 1) {
                                LoginActivity.this.enableLoadingScreen(false);
                                LoginActivity.this.setActionButtonsEnabled(false);
                                Toast.makeText(LoginActivity.this.getApplicationContext(), str, 1).show();
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            String access$300 = LoginActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("Network Error: ");
                            sb.append(exc.getMessage());
                            Log.m212e(access$300, sb.toString(), exc);
                            LoginActivity.this.addNetworkStateNotificationListener();
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append(LoginActivity.this.getString(C1299R.string.login_error_unable_login));
                            sb2.append(" : ");
                            sb2.append(exc.getLocalizedMessage());
                            onError(sb2.toString());
                        }

                        public void onUnexpectedError(Exception exc) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(LoginActivity.this.getString(C1299R.string.login_error_unable_login));
                            sb.append(" : ");
                            sb.append(exc.getLocalizedMessage());
                            onError(sb.toString());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            LoginActivity.this.onFailureDuringAuthRequest(matrixError);
                        }
                    });
                }
            } catch (Exception unused) {
                Toast.makeText(getApplicationContext(), getString(C1299R.string.login_error_invalid_home_server), 0).show();
                enableLoadingScreen(false);
            }
        }
    }

    private void restoreSavedData(@NonNull Bundle bundle) {
        Log.m209d(LOG_TAG, "## restoreSavedData(): IN");
        this.mLoginEmailTextView.setText(bundle.getString(SAVED_LOGIN_EMAIL_ADDRESS));
        this.mLoginPasswordTextView.setText(bundle.getString(SAVED_LOGIN_PASSWORD_ADDRESS));
        this.mHomeServerText.setText(bundle.getString(SAVED_HOME_SERVER_URL));
        this.mIdentityServerText.setText(bundle.getString(SAVED_IDENTITY_SERVER_URL));
        this.mCreationUsernameTextView.setText(bundle.getString(SAVED_CREATION_USER_NAME));
        this.mCreationPassword1TextView.setText(bundle.getString(SAVED_CREATION_PASSWORD1));
        this.mCreationPassword2TextView.setText(bundle.getString(SAVED_CREATION_PASSWORD2));
        this.mForgotEmailTextView.setText(bundle.getString(SAVED_FORGOT_EMAIL_ADDRESS));
        this.mForgotPassword1TextView.setText(bundle.getString(SAVED_FORGOT_PASSWORD1));
        this.mForgotPassword2TextView.setText(bundle.getString(SAVED_FORGOT_PASSWORD2));
        this.mRegistrationResponse = (RegistrationFlowResponse) bundle.getSerializable(SAVED_CREATION_REGISTRATION_RESPONSE);
        this.mMode = bundle.getInt(SAVED_MODE, 1);
        if (bundle.containsKey(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) {
            this.mUniversalLinkUri = bundle.getParcelable(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
        }
        this.mPendingEmailValidation = (ThreePid) bundle.getSerializable(SAVED_CREATION_EMAIL_THREEPID);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        Log.m209d(LOG_TAG, "## onRestoreInstanceState(): IN");
        restoreSavedData(bundle);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.m209d(LOG_TAG, "## onSaveInstanceState(): IN");
        if (!TextUtils.isEmpty(this.mLoginEmailTextView.getText().toString().trim())) {
            bundle.putString(SAVED_LOGIN_EMAIL_ADDRESS, this.mLoginEmailTextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mLoginPasswordTextView.getText().toString().trim())) {
            bundle.putString(SAVED_LOGIN_PASSWORD_ADDRESS, this.mLoginPasswordTextView.getText().toString().trim());
        }
        bundle.putBoolean(SAVED_IS_SERVER_URL_EXPANDED, this.mUseCustomHomeServersCheckbox.isChecked());
        if (!TextUtils.isEmpty(this.mHomeServerText.getText().toString().trim())) {
            bundle.putString(SAVED_HOME_SERVER_URL, this.mHomeServerText.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mIdentityServerText.getText().toString().trim())) {
            bundle.putString(SAVED_IDENTITY_SERVER_URL, this.mIdentityServerText.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mCreationUsernameTextView.getText().toString().trim())) {
            bundle.putString(SAVED_CREATION_USER_NAME, this.mCreationUsernameTextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mCreationPassword1TextView.getText().toString().trim())) {
            bundle.putString(SAVED_CREATION_PASSWORD1, this.mCreationPassword1TextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mCreationPassword2TextView.getText().toString().trim())) {
            bundle.putString(SAVED_CREATION_PASSWORD2, this.mCreationPassword2TextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mForgotEmailTextView.getText().toString().trim())) {
            bundle.putString(SAVED_FORGOT_EMAIL_ADDRESS, this.mForgotEmailTextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mForgotPassword1TextView.getText().toString().trim())) {
            bundle.putString(SAVED_FORGOT_PASSWORD1, this.mForgotPassword1TextView.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(this.mForgotPassword2TextView.getText().toString().trim())) {
            bundle.putString(SAVED_FORGOT_PASSWORD2, this.mForgotPassword2TextView.getText().toString().trim());
        }
        if (this.mRegistrationResponse != null) {
            bundle.putSerializable(SAVED_CREATION_REGISTRATION_RESPONSE, this.mRegistrationResponse);
        }
        if (this.mUniversalLinkUri != null) {
            bundle.putParcelable(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI, this.mUniversalLinkUri);
        }
        if (this.mRegisterPollingRunnable != null) {
            ThreePid emailThreePid = RegistrationManager.getInstance().getEmailThreePid();
            if (emailThreePid != null) {
                bundle.putSerializable(SAVED_CREATION_EMAIL_THREEPID, emailThreePid);
            }
        }
        bundle.putInt(SAVED_MODE, this.mMode);
    }

    /* access modifiers changed from: private */
    public void refreshDisplay() {
        checkFlows();
        View findViewById = findViewById(C1299R.C1301id.login_inputs_layout);
        View findViewById2 = findViewById(C1299R.C1301id.creation_inputs_layout);
        View findViewById3 = findViewById(C1299R.C1301id.forget_password_inputs_layout);
        View findViewById4 = findViewById(C1299R.C1301id.three_pid_layout);
        int i = 8;
        findViewById.setVisibility(this.mMode == 1 ? 0 : 8);
        findViewById2.setVisibility(this.mMode == 2 ? 0 : 8);
        findViewById3.setVisibility(this.mMode == 3 ? 0 : 8);
        findViewById4.setVisibility(this.mMode == 5 ? 0 : 8);
        boolean z = this.mMode == 1;
        this.mButtonsView.setVisibility(0);
        this.mPasswordForgottenTxtView.setVisibility(z ? 0 : 8);
        this.mLoginButton.setVisibility((this.mMode == 1 || this.mMode == 2) ? 0 : 8);
        this.mRegisterButton.setVisibility((this.mMode == 1 || this.mMode == 2) ? 0 : 8);
        this.mForgotPasswordButton.setVisibility(this.mMode == 3 ? 0 : 8);
        this.mForgotValidateEmailButton.setVisibility(this.mMode == 4 ? 0 : 8);
        this.mSubmitThreePidButton.setVisibility(this.mMode == 5 ? 0 : 8);
        Button button = this.mSkipThreePidButton;
        if (this.mMode == 5 && RegistrationManager.getInstance().canSkip()) {
            i = 0;
        }
        button.setVisibility(i);
        this.mForgotValidateEmailButton.setText(this.mIsPasswordResetted ? C1299R.string.auth_return_to_login : C1299R.string.auth_reset_password_next_step_button);
        int color = ContextCompat.getColor(this, C1299R.color.vector_green_color);
        int color2 = ContextCompat.getColor(this, 17170443);
        this.mLoginButton.setBackgroundColor(z ? color : color2);
        this.mLoginButton.setTextColor(!z ? color : color2);
        this.mRegisterButton.setBackgroundColor(!z ? color : color2);
        Button button2 = this.mRegisterButton;
        if (!z) {
            color = color2;
        }
        button2.setTextColor(color);
    }

    /* access modifiers changed from: private */
    public void enableLoadingScreen(boolean z) {
        setActionButtonsEnabled(!z);
        if (this.mLoginMaskView != null) {
            this.mLoginMaskView.setVisibility(z ? 0 : 8);
        }
    }

    /* access modifiers changed from: private */
    public void setActionButtonsEnabled(boolean z) {
        boolean z2 = true;
        boolean z3 = this.mMode == 3 || this.mMode == 4;
        int i = 8;
        this.mRegisterButton.setVisibility(z3 ? 8 : 0);
        this.mLoginButton.setVisibility(z3 ? 8 : 0);
        this.mForgotPasswordButton.setVisibility(this.mMode == 3 ? 0 : 8);
        float f = 0.5f;
        this.mForgotPasswordButton.setAlpha(z ? 1.0f : 0.5f);
        this.mForgotPasswordButton.setEnabled(z);
        Button button = this.mForgotValidateEmailButton;
        if (this.mMode == 4) {
            i = 0;
        }
        button.setVisibility(i);
        this.mForgotValidateEmailButton.setAlpha(z ? 1.0f : 0.5f);
        this.mForgotValidateEmailButton.setEnabled(z);
        boolean z4 = z || this.mMode == 2;
        if (!z && this.mMode != 1) {
            z2 = false;
        }
        this.mLoginButton.setEnabled(z4);
        this.mRegisterButton.setEnabled(z2);
        this.mLoginButton.setAlpha(z4 ? 1.0f : 0.5f);
        Button button2 = this.mRegisterButton;
        if (z2) {
            f = 1.0f;
        }
        button2.setAlpha(f);
    }

    /* access modifiers changed from: private */
    public static String sanitizeUrl(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s", "");
    }

    /* access modifiers changed from: private */
    public HomeServerConnectionConfig getHsConfig() {
        if (this.mHomeserverConnectionConfig == null) {
            String homeServerUrl = getHomeServerUrl();
            if (TextUtils.isEmpty(homeServerUrl) || !homeServerUrl.startsWith(UriUtil.HTTP_SCHEME) || TextUtils.equals(homeServerUrl, "http://") || TextUtils.equals(homeServerUrl, "https://")) {
                Toast.makeText(this, getString(C1299R.string.login_error_must_start_http), 0).show();
                return null;
            }
            if (!homeServerUrl.startsWith("http://") && !homeServerUrl.startsWith("https://")) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(homeServerUrl);
                homeServerUrl = sb.toString();
            }
            String identityServerUrl = getIdentityServerUrl();
            if (TextUtils.isEmpty(identityServerUrl) || !identityServerUrl.startsWith(UriUtil.HTTP_SCHEME) || TextUtils.equals(identityServerUrl, "http://") || TextUtils.equals(identityServerUrl, "https://")) {
                Toast.makeText(this, getString(C1299R.string.login_error_must_start_http), 0).show();
                return null;
            }
            if (!identityServerUrl.startsWith("http://") && !identityServerUrl.startsWith("https://")) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("https://");
                sb2.append(identityServerUrl);
                identityServerUrl = sb2.toString();
            }
            try {
                this.mHomeserverConnectionConfig = null;
                HomeServerConnectionConfig homeServerConnectionConfig = new HomeServerConnectionConfig(Uri.parse(homeServerUrl), Uri.parse(identityServerUrl), null, new ArrayList(), false);
                this.mHomeserverConnectionConfig = homeServerConnectionConfig;
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("getHsConfig fails ");
                sb3.append(e.getLocalizedMessage());
                Log.m211e(str, sb3.toString());
            }
        }
        RegistrationManager.getInstance().setHsConfig(this.mHomeserverConnectionConfig);
        return this.mHomeserverConnectionConfig;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onActivityResult(): IN - requestCode=");
        sb.append(i);
        sb.append(" resultCode=");
        sb.append(i2);
        Log.m209d(str, sb.toString());
        if (i2 != -1 || i != REQUEST_REGISTRATION_COUNTRY) {
            if (i2 == -1 && i == REQUEST_LOGIN_COUNTRY) {
                if (intent != null && intent.hasExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_CODE) && this.mLoginPhoneNumberHandler != null) {
                    this.mLoginPhoneNumberHandler.setCountryCode(intent.getStringExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_CODE));
                }
            } else if (316 == i) {
                if (i2 == -1) {
                    Log.m209d(LOG_TAG, "## onActivityResult(): CAPTCHA_CREATION_ACTIVITY_REQUEST_CODE => RESULT_OK");
                    RegistrationManager.getInstance().setCaptchaResponse(intent.getStringExtra("response"));
                    createAccount();
                    return;
                }
                Log.m209d(LOG_TAG, "## onActivityResult(): CAPTCHA_CREATION_ACTIVITY_REQUEST_CODE => RESULT_KO");
                this.mRegistrationResponse = null;
                showMainLayout();
                enableLoadingScreen(false);
                refreshDisplay();
            } else if (314 != i && 315 != i) {
            } else {
                if (i2 == -1) {
                    Log.m209d(LOG_TAG, "## onActivityResult(): ACCOUNT_CREATION_ACTIVITY_REQUEST_CODE => RESULT_OK");
                    String stringExtra = intent.getStringExtra("homeServer");
                    String stringExtra2 = intent.getStringExtra("userId");
                    String stringExtra3 = intent.getStringExtra("accessToken");
                    Credentials credentials = new Credentials();
                    credentials.userId = stringExtra2;
                    credentials.homeServer = stringExtra;
                    credentials.accessToken = stringExtra3;
                    HomeServerConnectionConfig hsConfig = getHsConfig();
                    try {
                        hsConfig.setCredentials(credentials);
                    } catch (Exception e) {
                        String str2 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("hsConfig setCredentials failed ");
                        sb2.append(e.getLocalizedMessage());
                        Log.m209d(str2, sb2.toString());
                    }
                    Log.m209d(LOG_TAG, "Account creation succeeds");
                    Matrix.getInstance(getApplicationContext()).addSession(Matrix.getInstance(getApplicationContext()).createSession(hsConfig));
                    saveServerUrlsIfCustomValuesHasBeenEntered();
                    goToSplash();
                    finish();
                } else if (i2 == 0 && 315 == i) {
                    Log.m209d(LOG_TAG, "## onActivityResult(): RESULT_CANCELED && FALLBACK_LOGIN_ACTIVITY_REQUEST_CODE");
                    this.mHomeServerText.setText("https://");
                    setActionButtonsEnabled(false);
                }
            }
        }
    }

    private void initThreePidView() {
        RegistrationManager.getInstance().clearThreePid();
        this.mEmailAddress.setText("");
        this.mEmailAddress.requestFocus();
        this.mThreePidInstructions.setText(RegistrationManager.getInstance().getThreePidInstructions(this));
        if (RegistrationManager.getInstance().supportStage(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
            this.mEmailAddress.setVisibility(0);
            if (RegistrationManager.getInstance().isOptional(LoginRestClient.LOGIN_FLOW_TYPE_EMAIL_IDENTITY)) {
                this.mEmailAddress.setHint(C1299R.string.auth_opt_email_placeholder);
            } else {
                this.mEmailAddress.setHint(C1299R.string.auth_email_placeholder);
            }
        } else {
            this.mEmailAddress.setVisibility(8);
        }
        if (RegistrationManager.getInstance().canSkip()) {
            this.mSkipThreePidButton.setVisibility(0);
            this.mSkipThreePidButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RegistrationManager.getInstance().clearThreePid();
                    LoginActivity.this.createAccount();
                    LoginActivity.this.mEmailAddress.setText("");
                }
            });
        } else {
            this.mSkipThreePidButton.setVisibility(8);
        }
        this.mSubmitThreePidButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.submitThreePids();
            }
        });
    }

    /* access modifiers changed from: private */
    public void submitThreePids() {
        dismissKeyboard(this);
        RegistrationManager.getInstance().clearThreePid();
        String obj = this.mEmailAddress.getText().toString();
        if (!TextUtils.isEmpty(obj)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(obj).matches()) {
                Toast.makeText(this, C1299R.string.auth_invalid_email, 0).show();
                return;
            }
        } else if (RegistrationManager.getInstance().isEmailRequired()) {
            Toast.makeText(this, C1299R.string.auth_missing_email, 0).show();
            return;
        }
        if (!TextUtils.isEmpty(obj)) {
            RegistrationManager.getInstance().addEmailThreePid(new ThreePid(obj, "email"));
        }
        createAccount();
    }

    private void onPhoneNumberSidReceived(final ThreePid threePid) {
        final View inflate = getLayoutInflater().inflate(C1299R.layout.dialog_phone_number_verification, null);
        this.mCurrentDialog = new Builder(this).setView(inflate).setMessage(C1299R.string.settings_phone_number_verification_instruction).setPositiveButton(C1299R.string.auth_submit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create();
        this.mCurrentDialog.setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface dialogInterface) {
                ((AlertDialog) dialogInterface).getButton(-1).setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        LoginActivity.this.submitPhoneNumber(((TextInputEditText) inflate.findViewById(C1299R.C1301id.phone_number_code_value)).getText().toString(), threePid);
                    }
                });
            }
        });
        this.mCurrentDialog.show();
    }

    /* access modifiers changed from: private */
    public void submitPhoneNumber(String str, ThreePid threePid) {
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, C1299R.string.auth_invalid_token, 0).show();
        } else {
            RegistrationManager.getInstance().submitValidationToken(str, threePid, new ThreePidValidationListener() {
                public void onThreePidValidated(boolean z) {
                    if (z) {
                        LoginActivity.this.createAccount();
                    } else {
                        Toast.makeText(LoginActivity.this, C1299R.string.auth_invalid_token, 0).show();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void createAccount() {
        if (this.mCurrentDialog != null) {
            this.mCurrentDialog.dismiss();
        }
        enableLoadingScreen(true);
        hideMainLayoutAndToast("");
        RegistrationManager.getInstance().attemptRegistration(this, this);
    }

    private void cancelEmailPolling() {
        this.mPendingEmailValidation = null;
        if (this.mHandler != null && this.mRegisterPollingRunnable != null) {
            this.mHandler.removeCallbacks(this.mRegisterPollingRunnable);
        }
    }

    public void onRegistrationSuccess(String str) {
        cancelEmailPolling();
        enableLoadingScreen(false);
        if (!TextUtils.isEmpty(str)) {
            this.mCurrentDialog = new Builder(this).setTitle(C1299R.string.dialog_title_warning).setMessage(str).setPositiveButton(C1299R.string.f115ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.saveServerUrlsIfCustomValuesHasBeenEntered();
                    LoginActivity.this.goToSplash();
                    LoginActivity.this.finish();
                }
            }).show();
            return;
        }
        goToSplash();
        finish();
    }

    public void onRegistrationFailed(String str) {
        cancelEmailPolling();
        this.mEmailValidationExtraParams = null;
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onRegistrationFailed(): ");
        sb.append(str);
        Log.m211e(str2, sb.toString());
        showMainLayout();
        enableLoadingScreen(false);
        refreshDisplay();
        Toast.makeText(this, C1299R.string.login_error_unable_register, 1).show();
    }

    public void onWaitingEmailValidation() {
        Log.m209d(LOG_TAG, "## onWaitingEmailValidation");
        hideMainLayoutAndToast(getResources().getString(C1299R.string.auth_email_validation_message));
        enableLoadingScreen(true);
        this.mRegisterPollingRunnable = new Runnable() {
            public void run() {
                Log.m209d(LoginActivity.LOG_TAG, "## onWaitingEmailValidation attempt registration");
                RegistrationManager.getInstance().attemptRegistration(LoginActivity.this, LoginActivity.this);
                LoginActivity.this.mHandler.postDelayed(LoginActivity.this.mRegisterPollingRunnable, 10000);
            }
        };
        this.mHandler.postDelayed(this.mRegisterPollingRunnable, 10000);
    }

    public void onWaitingCaptcha() {
        cancelEmailPolling();
        String captchaPublicKey = RegistrationManager.getInstance().getCaptchaPublicKey();
        if (!TextUtils.isEmpty(captchaPublicKey)) {
            Log.m209d(LOG_TAG, "## onWaitingCaptcha");
            Intent intent = new Intent(this, AccountCreationCaptchaActivity.class);
            intent.putExtra(AccountCreationCaptchaActivity.EXTRA_HOME_SERVER_URL, this.mHomeServerUrl);
            intent.putExtra(AccountCreationCaptchaActivity.EXTRA_SITE_KEY, captchaPublicKey);
            startActivityForResult(intent, RequestCodesKt.CAPTCHA_CREATION_ACTIVITY_REQUEST_CODE);
            return;
        }
        Log.m209d(LOG_TAG, "## onWaitingCaptcha(): captcha flow cannot be done");
        Toast.makeText(this, getString(C1299R.string.login_error_unable_register), 0).show();
    }

    public void onThreePidRequestFailed(String str) {
        String str2 = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## onThreePidRequestFailed():");
        sb.append(str);
        Log.m209d(str2, sb.toString());
        enableLoadingScreen(false);
        showMainLayout();
        refreshDisplay();
        Toast.makeText(this, str, 0).show();
    }

    public void onUsernameAvailabilityChecked(boolean z) {
        enableLoadingScreen(false);
        if (!z) {
            showMainLayout();
            Toast.makeText(this, C1299R.string.auth_username_in_use, 1).show();
        } else if (RegistrationManager.getInstance().canAddThreePid()) {
            showMainLayout();
            this.mMode = 5;
            initThreePidView();
            refreshDisplay();
        } else {
            createAccount();
        }
    }
}
