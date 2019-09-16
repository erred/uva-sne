package com.opengarden.firechat.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.GravityCompat;
import android.support.p000v4.widget.DrawerLayout;
import android.support.p003v7.app.ActionBarDrawerToggle;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import android.support.p003v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.MyPresenceManager;
import com.opengarden.firechat.PublicRoomsManager;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.fragments.AbsHomeFragment;
import com.opengarden.firechat.fragments.FavouritesFragment;
import com.opengarden.firechat.fragments.GroupsFragment;
import com.opengarden.firechat.fragments.HomeFragment;
import com.opengarden.firechat.fragments.PeopleFragment;
import com.opengarden.firechat.fragments.RoomsFragment;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager.NotificationPrivacy;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.call.IMXCall;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.MyUser;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomPreviewData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomSummary;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.receiver.VectorUniversalLinkReceiver;
import com.opengarden.firechat.services.EventStreamService;
import com.opengarden.firechat.util.BugReporter;
import com.opengarden.firechat.util.CallsManager;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.RoomUtils;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.UnreadCounterBadgeView;
import com.opengarden.firechat.view.VectorPendingCallView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;

public class VectorHomeActivity extends RiotAppCompatActivity implements OnQueryTextListener {
    public static final String BROADCAST_ACTION_STOP_WAITING_VIEW = "im.vector.activity.ACTION_STOP_WAITING_VIEW";
    private static final String CURRENT_MENU_ID = "CURRENT_MENU_ID";
    public static final String EXTRA_CALL_ID = "VectorHomeActivity.EXTRA_CALL_ID";
    public static final String EXTRA_CALL_SESSION_ID = "VectorHomeActivity.EXTRA_CALL_SESSION_ID";
    public static final String EXTRA_CALL_UNKNOWN_DEVICES = "VectorHomeActivity.EXTRA_CALL_UNKNOWN_DEVICES";
    public static final String EXTRA_GROUP_ID = "VectorHomeActivity.EXTRA_GROUP_ID";
    public static final String EXTRA_JUMP_TO_ROOM_PARAMS = "VectorHomeActivity.EXTRA_JUMP_TO_ROOM_PARAMS";
    public static final String EXTRA_JUMP_TO_UNIVERSAL_LINK = "VectorHomeActivity.EXTRA_JUMP_TO_UNIVERSAL_LINK";
    public static final String EXTRA_MEMBER_ID = "VectorHomeActivity.EXTRA_MEMBER_ID";
    public static final String EXTRA_SHARED_INTENT_PARAMS = "VectorHomeActivity.EXTRA_SHARED_INTENT_PARAMS";
    public static final String EXTRA_WAITING_VIEW_STATUS = "VectorHomeActivity.EXTRA_WAITING_VIEW_STATUS";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorHomeActivity";
    private static final String NO_DEVICE_ID_WARNING_KEY = "NO_DEVICE_ID_WARNING_KEY";
    private static final String OPEN_GARDEN_ROOM_ID = "!BVApBgZbTmaSscaQVY:serve2.firech.at";
    private static final int REQUEST_ENABLE_BT = 1001;
    private static final String TAG_FRAGMENT_FAVOURITES = "TAG_FRAGMENT_FAVOURITES";
    private static final String TAG_FRAGMENT_GROUPS = "TAG_FRAGMENT_GROUPS";
    private static final String TAG_FRAGMENT_HOME = "TAG_FRAGMENT_HOME";
    private static final String TAG_FRAGMENT_PEOPLE = "TAG_FRAGMENT_PEOPLE";
    private static final String TAG_FRAGMENT_ROOMS = "TAG_FRAGMENT_ROOMS";
    public static final boolean WAITING_VIEW_START = true;
    private static final boolean WAITING_VIEW_STOP = false;
    private static VectorHomeActivity sharedInstance;
    /* access modifiers changed from: private */
    public Map<String, Object> mAutomaticallyOpenedRoomParams = null;
    private final MXEventListener mBadgeEventsListener = new MXEventListener() {
        private boolean mRefreshBadgeOnChunkEnd = false;

        public void onLiveEventsChunkProcessed(String str, String str2) {
            if (this.mRefreshBadgeOnChunkEnd) {
                VectorHomeActivity.this.refreshUnreadBadges();
                this.mRefreshBadgeOnChunkEnd = false;
            }
        }

        public void onLiveEvent(Event event, RoomState roomState) {
            String type = event.getType();
            this.mRefreshBadgeOnChunkEnd = ((event.roomId != null && RoomSummary.isSupportedEvent(event)) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_REDACTION.equals(type) || Event.EVENT_TYPE_TAGS.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type)) | this.mRefreshBadgeOnChunkEnd;
        }

        public void onReceiptEvent(String str, List<String> list) {
            this.mRefreshBadgeOnChunkEnd |= list.indexOf(VectorHomeActivity.this.mSession.getCredentials().userId) >= 0;
        }

        public void onLeaveRoom(String str) {
            this.mRefreshBadgeOnChunkEnd = true;
        }

        public void onNewRoom(String str) {
            this.mRefreshBadgeOnChunkEnd = true;
        }

        public void onJoinRoom(String str) {
            this.mRefreshBadgeOnChunkEnd = true;
        }

        public void onDirectMessageChatRoomsListUpdate() {
            this.mRefreshBadgeOnChunkEnd = true;
        }

        public void onRoomTagEvent(String str) {
            this.mRefreshBadgeOnChunkEnd = true;
        }
    };
    private final Map<Integer, UnreadCounterBadgeView> mBadgeViewByIndex = new HashMap();
    private BluetoothAdapter mBluetoothAdapter;
    @BindView(2131296335)
    BottomNavigationView mBottomNavigationView;
    private final BroadcastReceiver mBrdRcvStopWaitingView = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            VectorHomeActivity.this.hideWaitingView();
        }
    };
    private String mCurrentFragmentTag;
    /* access modifiers changed from: private */
    public int mCurrentMenuId;
    private List<Room> mDirectChatInvitations;
    @BindView(2131296457)
    DrawerLayout mDrawerLayout;
    private MXEventListener mEventsListener;
    @BindView(2131296348)
    FloatingActionButton mFabCreateRoom;
    @BindView(2131296351)
    FloatingActionButton mFabJoinRoom;
    @BindView(2131296485)
    AddFloatingActionButton mFabMain;
    @BindView(2131296356)
    FloatingActionButton mFabStartChat;
    @BindView(2131296513)
    FloatingActionsMenu mFloatingActionsMenu;
    private FragmentManager mFragmentManager;
    private String mGroupIdToOpen = null;
    /* access modifiers changed from: private */
    public Runnable mHideFloatingActionButton;
    private String mMemberIdToOpen = null;
    private List<Room> mRoomInvitations;
    @BindView(2131297010)
    SearchView mSearchView;
    /* access modifiers changed from: private */
    public MXSession mSession;
    /* access modifiers changed from: private */
    public Intent mSharedFilesIntent = null;
    /* access modifiers changed from: private */
    public int mSlidingMenuIndex = -1;
    private boolean mStorePermissionCheck = false;
    @BindView(2131296570)
    ProgressBar mSyncInProgressView;
    @BindView(2131296571)
    Toolbar mToolbar;
    /* access modifiers changed from: private */
    public Uri mUniversalLinkToOpen = null;
    @BindView(2131296673)
    VectorPendingCallView mVectorPendingCallView;
    @BindView(2131296827)
    NavigationView navigationView;
    @BindView(2131296514)
    View touchGuard;
    @BindView(2131296675)
    View waitingView;

    public int getLayoutRes() {
        return C1299R.layout.activity_home;
    }

    public boolean onQueryTextSubmit(String str) {
        return true;
    }

    public static VectorHomeActivity getInstance() {
        return sharedInstance;
    }

    public void initUiAndData() {
        View view;
        this.mFragmentManager = getSupportFragmentManager();
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            sharedInstance = this;
            setupNavigation();
            initSlidingMenu();
            this.mSession = Matrix.getInstance(this).getDefaultSession();
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int i = defaultSharedPreferences.getInt(PreferencesManager.VERSION_BUILD, 0);
            if (i != VectorApp.VERSION_BUILD) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("The application has been updated from version ");
                sb.append(i);
                sb.append(" to version ");
                sb.append(VectorApp.VERSION_BUILD);
                Log.m209d(str, sb.toString());
                defaultSharedPreferences.edit().putInt(PreferencesManager.VERSION_BUILD, VectorApp.VERSION_BUILD).apply();
            }
            Intent intent = getIntent();
            if (!isFirstCreation()) {
                intent.removeExtra(EXTRA_SHARED_INTENT_PARAMS);
                intent.removeExtra(EXTRA_CALL_SESSION_ID);
                intent.removeExtra(EXTRA_CALL_ID);
                intent.removeExtra(EXTRA_CALL_UNKNOWN_DEVICES);
                intent.removeExtra(EXTRA_WAITING_VIEW_STATUS);
                intent.removeExtra(EXTRA_JUMP_TO_UNIVERSAL_LINK);
                intent.removeExtra(EXTRA_JUMP_TO_ROOM_PARAMS);
                intent.removeExtra(EXTRA_MEMBER_ID);
                intent.removeExtra(EXTRA_GROUP_ID);
                intent.removeExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
            } else {
                if (intent.hasExtra(EXTRA_CALL_SESSION_ID) && intent.hasExtra(EXTRA_CALL_ID)) {
                    startCall(intent.getStringExtra(EXTRA_CALL_SESSION_ID), intent.getStringExtra(EXTRA_CALL_ID), (MXUsersDevicesMap) intent.getSerializableExtra(EXTRA_CALL_UNKNOWN_DEVICES));
                    intent.removeExtra(EXTRA_CALL_SESSION_ID);
                    intent.removeExtra(EXTRA_CALL_ID);
                    intent.removeExtra(EXTRA_CALL_UNKNOWN_DEVICES);
                }
                if (intent.getBooleanExtra(EXTRA_WAITING_VIEW_STATUS, false)) {
                    showWaitingView();
                } else {
                    hideWaitingView();
                }
                intent.removeExtra(EXTRA_WAITING_VIEW_STATUS);
                this.mAutomaticallyOpenedRoomParams = (Map) intent.getSerializableExtra(EXTRA_JUMP_TO_ROOM_PARAMS);
                intent.removeExtra(EXTRA_JUMP_TO_ROOM_PARAMS);
                this.mUniversalLinkToOpen = (Uri) intent.getParcelableExtra(EXTRA_JUMP_TO_UNIVERSAL_LINK);
                intent.removeExtra(EXTRA_JUMP_TO_UNIVERSAL_LINK);
                this.mMemberIdToOpen = intent.getStringExtra(EXTRA_MEMBER_ID);
                intent.removeExtra(EXTRA_MEMBER_ID);
                this.mGroupIdToOpen = intent.getStringExtra(EXTRA_GROUP_ID);
                intent.removeExtra(EXTRA_GROUP_ID);
                if (intent.hasExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) {
                    Log.m209d(LOG_TAG, "Has an universal link");
                    final Uri uri = (Uri) intent.getParcelableExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
                    intent.removeExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
                    HashMap parseUniversalLink = VectorUniversalLinkReceiver.parseUniversalLink(uri);
                    if (parseUniversalLink != null && parseUniversalLink.containsKey(VectorUniversalLinkReceiver.ULINK_ROOM_ID_OR_ALIAS_KEY)) {
                        Log.m209d(LOG_TAG, "Has a valid universal link");
                        String str2 = (String) parseUniversalLink.get(VectorUniversalLinkReceiver.ULINK_ROOM_ID_OR_ALIAS_KEY);
                        if (MXSession.isRoomId(str2)) {
                            String str3 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Has a valid universal link to the room ID ");
                            sb2.append(str2);
                            Log.m209d(str3, sb2.toString());
                            if (this.mSession.getDataHandler().getRoom(str2, false) != null) {
                                Log.m209d(LOG_TAG, "Has a valid universal link to a known room");
                                this.mUniversalLinkToOpen = uri;
                            } else {
                                Log.m209d(LOG_TAG, "Has a valid universal link but the room is not yet known");
                                intent.putExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI, uri);
                            }
                        } else if (MXSession.isRoomAlias(str2)) {
                            String str4 = LOG_TAG;
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Has a valid universal link of the room Alias ");
                            sb3.append(str2);
                            Log.m209d(str4, sb3.toString());
                            showWaitingView();
                            this.mSession.getDataHandler().roomIdByAlias(str2, new SimpleApiCallback<String>() {
                                public void onSuccess(String str) {
                                    String access$000 = VectorHomeActivity.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Retrieve the room ID ");
                                    sb.append(str);
                                    Log.m209d(access$000, sb.toString());
                                    VectorHomeActivity.this.getIntent().putExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI, uri);
                                    if (VectorHomeActivity.this.mSession.getDataHandler().getRoom(str, false) != null) {
                                        Log.m209d(VectorHomeActivity.LOG_TAG, "Find the room from room ID : process it");
                                        VectorHomeActivity.this.processIntentUniversalLink();
                                        return;
                                    }
                                    Log.m209d(VectorHomeActivity.LOG_TAG, "Don't know the room");
                                }
                            });
                        }
                    }
                } else {
                    Log.m209d(LOG_TAG, "create with no universal link");
                }
                if (intent.hasExtra(EXTRA_SHARED_INTENT_PARAMS)) {
                    final Intent intent2 = (Intent) intent.getParcelableExtra(EXTRA_SHARED_INTENT_PARAMS);
                    Log.m209d(LOG_TAG, "Has shared intent");
                    if (this.mSession.getDataHandler().getStore().isReady()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Log.m209d(VectorHomeActivity.LOG_TAG, "shared intent : The store is ready -> display sendFilesTo");
                                CommonActivityUtils.sendFilesTo(VectorHomeActivity.this, intent2);
                            }
                        });
                    } else {
                        Log.m209d(LOG_TAG, "shared intent : Wait that the store is ready");
                        this.mSharedFilesIntent = intent2;
                    }
                    intent.removeExtra(EXTRA_SHARED_INTENT_PARAMS);
                }
            }
            if (Matrix.getMXSessions(this).size() == 0) {
                Log.m211e(LOG_TAG, "Weird : onCreate : no session");
                if (Matrix.getInstance(this).getDefaultSession() != null) {
                    Log.m211e(LOG_TAG, "No loaded session : reload them");
                    startActivity(new Intent(this, SplashActivity.class));
                    finish();
                    return;
                }
            }
            if (isFirstCreation()) {
                view = this.mBottomNavigationView.findViewById(C1299R.C1301id.bottom_action_home);
            } else {
                view = this.mBottomNavigationView.findViewById(getSavedInstanceState().getInt(CURRENT_MENU_ID, C1299R.C1301id.bottom_action_home));
            }
            if (view != null) {
                view.performClick();
            }
            PublicRoomsManager.getInstance().setSession(this.mSession);
            PublicRoomsManager.getInstance().refreshPublicRoomsCount(null);
            initViews();
            CommonActivityUtils.checkPermissions(1000, (Activity) this);
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (this.mBluetoothAdapter != null && !this.mBluetoothAdapter.isEnabled() && VectorApp.getInstance().offLineMessagePreference) {
                this.mBluetoothAdapter.enable();
            }
            joinOpenGardenRoom();
        }
    }

    private void joinOpenGardenRoom() {
        if (this.mSession.getDataHandler().getRoom(this.mSession.getDataHandler().getStore(), OPEN_GARDEN_ROOM_ID, false) == null) {
            this.mSession.joinRoom(OPEN_GARDEN_ROOM_ID, new ApiCallback<String>() {
                public void onMatrixError(MatrixError matrixError) {
                }

                public void onNetworkError(Exception exc) {
                }

                public void onUnexpectedError(Exception exc) {
                }

                public void onSuccess(String str) {
                    Log.m209d(VectorHomeActivity.TAG_FRAGMENT_HOME, "Open garden room joined");
                }
            });
        } else {
            Log.m209d(TAG_FRAGMENT_HOME, "Already a member of Open garden room");
        }
    }

    /* access modifiers changed from: private */
    public void showFloatingActionMenuIfRequired() {
        if (this.mCurrentMenuId == C1299R.C1301id.bottom_action_favourites || this.mCurrentMenuId == C1299R.C1301id.bottom_action_groups) {
            concealFloatingActionMenu();
        } else {
            revealFloatingActionMenu();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        MyPresenceManager.createPresenceManager(this, Matrix.getInstance(this).getSessions());
        MyPresenceManager.advertiseAllOnline();
        registerReceiver(this.mBrdRcvStopWaitingView, new IntentFilter(BROADCAST_ACTION_STOP_WAITING_VIEW));
        Intent intent = getIntent();
        if (this.mAutomaticallyOpenedRoomParams != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    CommonActivityUtils.goToRoomPage(VectorHomeActivity.this, VectorHomeActivity.this.mAutomaticallyOpenedRoomParams);
                    VectorHomeActivity.this.mAutomaticallyOpenedRoomParams = null;
                }
            });
        }
        if (this.mUniversalLinkToOpen != null) {
            intent.putExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI, this.mUniversalLinkToOpen);
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                public void run() {
                    VectorHomeActivity.this.processIntentUniversalLink();
                    VectorHomeActivity.this.mUniversalLinkToOpen = null;
                }
            }, 100);
        }
        if (this.mSession.isAlive()) {
            addEventsListener();
        }
        showFloatingActionMenuIfRequired();
        refreshSlidingMenu();
        this.mVectorPendingCallView.checkPendingCall();
        if (VectorApp.getInstance() != null && VectorApp.getInstance().didAppCrash()) {
            try {
                new Builder(this).setMessage(getApplicationContext().getString(C1299R.string.send_bug_report_app_crashed)).setPositiveButton(getString(C1299R.string.yes), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BugReporter.sendBugReport();
                    }
                }).setNegativeButton(getString(C1299R.string.f114no), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BugReporter.deleteCrashFile(VectorHomeActivity.this);
                    }
                }).show();
                VectorApp.getInstance().clearAppCrashStatus();
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onResume() : appCrashedAlert failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        if (!this.mStorePermissionCheck) {
            this.mStorePermissionCheck = true;
            CommonActivityUtils.checkPermissions(1002, (Activity) this);
        }
        if (this.mMemberIdToOpen != null) {
            Intent intent2 = new Intent(this, VectorMemberDetailsActivity.class);
            intent2.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, this.mMemberIdToOpen);
            intent2.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            startActivity(intent2);
            this.mMemberIdToOpen = null;
        }
        if (this.mGroupIdToOpen != null) {
            Intent intent3 = new Intent(this, VectorGroupDetailsActivity.class);
            intent3.putExtra(VectorGroupDetailsActivity.EXTRA_GROUP_ID, this.mGroupIdToOpen);
            intent3.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            startActivity(intent3);
            this.mGroupIdToOpen = null;
        }
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(C1299R.attr.riot_primary_background_color, typedValue, true);
        this.mToolbar.setBackgroundResource(typedValue.resourceId);
        checkDeviceId();
        this.mSyncInProgressView.setVisibility(VectorApp.isSessionSyncing(this.mSession) ? 0 : 8);
        displayCryptoCorruption();
        addBadgeEventsListener();
        checkNotificationPrivacySetting();
    }

    private void checkNotificationPrivacySetting() {
        if (VERSION.SDK_INT >= 23) {
            GcmRegistrationManager sharedGCMRegistrationManager = Matrix.getInstance(this).getSharedGCMRegistrationManager();
            if (sharedGCMRegistrationManager.useGCM() && !PreferencesManager.didAskUserToIgnoreBatteryOptimizations(this)) {
                PreferencesManager.setDidAskUserToIgnoreBatteryOptimizations(this);
                sharedGCMRegistrationManager.setNotificationPrivacy(NotificationPrivacy.LOW_DETAIL);
                Builder builder = new Builder(this);
                builder.setTitle(C1299R.string.startup_notification_privacy_title);
                builder.setMessage(C1299R.string.startup_notification_privacy_message);
                builder.setPositiveButton(C1299R.string.startup_notification_privacy_button_grant, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.m209d(VectorHomeActivity.LOG_TAG, "checkNotificationPrivacySetting: user wants to grant the IgnoreBatteryOptimizations permission");
                        NotificationPrivacyActivity.setNotificationPrivacy(VectorHomeActivity.this, NotificationPrivacy.NORMAL);
                    }
                });
                builder.setNegativeButton(C1299R.string.startup_notification_privacy_button_other, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.m209d(VectorHomeActivity.LOG_TAG, "checkNotificationPrivacySetting: user opens notification policy setting screen");
                        VectorHomeActivity.this.startActivity(NotificationPrivacyActivity.getIntent(VectorHomeActivity.this));
                    }
                });
                builder.create().show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            return false;
        }
        getMenuInflater().inflate(C1299R.C1302menu.vector_home, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case C1299R.C1301id.ic_action_global_search /*2131296578*/:
                Intent intent = new Intent(this, VectorUnifiedSearchActivity.class);
                if (C1299R.C1301id.bottom_action_people == this.mCurrentMenuId) {
                    intent.putExtra("VectorUnifiedSearchActivity.EXTRA_TAB_INDEX", 2);
                }
                startActivity(intent);
                break;
            case C1299R.C1301id.ic_action_historical /*2131296579*/:
                startActivity(new Intent(this, HistoricalRoomsActivity.class));
                break;
            case C1299R.C1301id.ic_action_mark_all_as_read /*2131296580*/:
                return false;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerVisible((int) GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer((int) GravityCompat.START);
        } else if (!TextUtils.isEmpty(this.mSearchView.getQuery().toString())) {
            this.mSearchView.setQuery("", true);
        } else if (this.mFloatingActionsMenu.isExpanded()) {
            this.mFloatingActionsMenu.collapse();
        } else {
            this.mFragmentManager.popBackStack((String) null, 1);
            super.onBackPressed();
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(CURRENT_MENU_ID, this.mCurrentMenuId);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        hideWaitingView();
        try {
            unregisterReceiver(this.mBrdRcvStopWaitingView);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onPause() : unregisterReceiver fails ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        if (this.mSession.isAlive()) {
            removeEventsListener();
        }
        if (!(this.mHideFloatingActionButton == null || this.mFloatingActionsMenu == null)) {
            this.mFloatingActionsMenu.removeCallbacks(this.mHideFloatingActionButton);
            this.mHideFloatingActionButton = null;
        }
        removeBadgeEventsListener();
    }

    public void onDestroy() {
        super.onDestroy();
        if (sharedInstance == this) {
            sharedInstance = null;
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        CommonActivityUtils.onLowMemory(this);
    }

    public void onTrimMemory(int i) {
        super.onTrimMemory(i);
        CommonActivityUtils.onTrimMemory(this, i);
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.mAutomaticallyOpenedRoomParams = (Map) intent.getSerializableExtra(EXTRA_JUMP_TO_ROOM_PARAMS);
        intent.removeExtra(EXTRA_JUMP_TO_ROOM_PARAMS);
        this.mUniversalLinkToOpen = (Uri) intent.getParcelableExtra(EXTRA_JUMP_TO_UNIVERSAL_LINK);
        intent.removeExtra(EXTRA_JUMP_TO_UNIVERSAL_LINK);
        this.mMemberIdToOpen = intent.getStringExtra(EXTRA_MEMBER_ID);
        intent.removeExtra(EXTRA_MEMBER_ID);
        this.mGroupIdToOpen = intent.getStringExtra(EXTRA_GROUP_ID);
        intent.removeExtra(EXTRA_GROUP_ID);
        if (intent.getBooleanExtra(EXTRA_WAITING_VIEW_STATUS, false)) {
            showWaitingView();
        } else {
            hideWaitingView();
        }
        intent.removeExtra(EXTRA_WAITING_VIEW_STATUS);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
        } else if (i == 1002) {
            Log.m217w(LOG_TAG, "## onRequestPermissionsResult(): REQUEST_CODE_PERMISSION_HOME_ACTIVITY");
        } else if (i == 1000) {
            Log.m217w(LOG_TAG, "## onRequestPermissionsResult(): REQUEST_CODE_LOCATION");
        } else {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## onRequestPermissionsResult(): unknown RequestCode = ");
            sb2.append(i);
            Log.m211e(str2, sb2.toString());
        }
    }

    private void setupNavigation() {
        setSupportActionBar(this.mToolbar);
        this.mBottomNavigationView.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                VectorHomeActivity.this.updateSelectedFragment(menuItem);
                return true;
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateSelectedFragment(MenuItem menuItem) {
        Fragment fragment;
        if (this.mCurrentMenuId != menuItem.getItemId()) {
            switch (menuItem.getItemId()) {
                case C1299R.C1301id.bottom_action_favourites /*2131296330*/:
                    Log.m209d(LOG_TAG, "onNavigationItemSelected FAVOURITES");
                    Fragment findFragmentByTag = this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_FAVOURITES);
                    if (findFragmentByTag == null) {
                        findFragmentByTag = FavouritesFragment.newInstance();
                    }
                    this.mCurrentFragmentTag = TAG_FRAGMENT_FAVOURITES;
                    this.mSearchView.setQueryHint(getString(C1299R.string.home_filter_placeholder_favorites));
                    break;
                case C1299R.C1301id.bottom_action_groups /*2131296331*/:
                    Log.m209d(LOG_TAG, "onNavigationItemSelected GROUPS");
                    Fragment findFragmentByTag2 = this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_GROUPS);
                    if (findFragmentByTag2 == null) {
                        findFragmentByTag2 = GroupsFragment.newInstance();
                    }
                    this.mCurrentFragmentTag = TAG_FRAGMENT_GROUPS;
                    this.mSearchView.setQueryHint(getString(C1299R.string.home_filter_placeholder_groups));
                    break;
                case C1299R.C1301id.bottom_action_home /*2131296332*/:
                    Log.m209d(LOG_TAG, "onNavigationItemSelected HOME");
                    Fragment findFragmentByTag3 = this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME);
                    if (findFragmentByTag3 == null) {
                        findFragmentByTag3 = HomeFragment.newInstance();
                    }
                    this.mCurrentFragmentTag = TAG_FRAGMENT_HOME;
                    this.mSearchView.setQueryHint(getString(C1299R.string.home_filter_placeholder_home));
                    break;
                case C1299R.C1301id.bottom_action_people /*2131296333*/:
                    Log.m209d(LOG_TAG, "onNavigationItemSelected PEOPLE");
                    Fragment findFragmentByTag4 = this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_PEOPLE);
                    if (findFragmentByTag4 == null) {
                        findFragmentByTag4 = PeopleFragment.newInstance();
                    }
                    this.mCurrentFragmentTag = TAG_FRAGMENT_PEOPLE;
                    this.mSearchView.setQueryHint(getString(C1299R.string.home_filter_placeholder_people));
                    break;
                case C1299R.C1301id.bottom_action_rooms /*2131296334*/:
                    Log.m209d(LOG_TAG, "onNavigationItemSelected ROOMS");
                    fragment = this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_ROOMS);
                    if (fragment == null) {
                        fragment = RoomsFragment.newInstance();
                    }
                    this.mCurrentFragmentTag = TAG_FRAGMENT_ROOMS;
                    this.mSearchView.setQueryHint(getString(C1299R.string.home_filter_placeholder_rooms));
                    break;
                default:
                    fragment = null;
                    break;
            }
            if (!(this.mHideFloatingActionButton == null || this.mFloatingActionsMenu == null)) {
                this.mFloatingActionsMenu.removeCallbacks(this.mHideFloatingActionButton);
                this.mHideFloatingActionButton = null;
            }
            hideWaitingView();
            this.mCurrentMenuId = menuItem.getItemId();
            showFloatingActionMenuIfRequired();
            if (fragment != null) {
                resetFilter();
                try {
                    this.mFragmentManager.beginTransaction().replace(C1299R.C1301id.fragment_container, fragment, this.mCurrentFragmentTag).addToBackStack(this.mCurrentFragmentTag).commit();
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## updateSelectedFragment() failed : ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    public void updateTabStyle(int i, int i2) {
        this.mToolbar.setBackgroundColor(i);
        Class<FloatingActionsMenu> cls = FloatingActionsMenu.class;
        try {
            Field declaredField = cls.getDeclaredField("mAddButtonColorNormal");
            declaredField.setAccessible(true);
            Field declaredField2 = cls.getDeclaredField("mAddButtonColorPressed");
            declaredField2.setAccessible(true);
            declaredField.set(this.mFloatingActionsMenu, Integer.valueOf(i));
            declaredField2.set(this.mFloatingActionsMenu, Integer.valueOf(i2));
            this.mFabMain.setColorNormal(i);
            this.mFabMain.setColorPressed(i2);
        } catch (Exception unused) {
        }
        this.mFabJoinRoom.setColorNormal(i2);
        this.mFabJoinRoom.setColorPressed(i);
        this.mFabCreateRoom.setColorNormal(i2);
        this.mFabCreateRoom.setColorPressed(i);
        this.mFabStartChat.setColorNormal(i2);
        this.mFabStartChat.setColorPressed(i);
        this.mVectorPendingCallView.updateBackgroundColor(i);
        this.mSyncInProgressView.setBackgroundColor(i);
        if (VERSION.SDK_INT >= 21) {
            this.mSyncInProgressView.setIndeterminateTintList(ColorStateList.valueOf(i2));
        } else {
            this.mSyncInProgressView.getIndeterminateDrawable().setColorFilter(i2, Mode.SRC_IN);
        }
        if (VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(i2);
        }
        EditText editText = (EditText) this.mSearchView.findViewById(C1299R.C1301id.search_src_text);
        editText.setTextColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.primary_text_color));
        editText.setHintTextColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.primary_hint_text_color));
    }

    private void initViews() {
        this.mVectorPendingCallView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IMXCall activeCall = CallsManager.getSharedInstance().getActiveCall();
                if (activeCall != null) {
                    final Intent intent = new Intent(VectorHomeActivity.this, VectorCallViewActivity.class);
                    intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, activeCall.getSession().getCredentials().userId);
                    intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, activeCall.getCallId());
                    VectorHomeActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            VectorHomeActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        });
        addUnreadBadges();
        SearchManager searchManager = (SearchManager) getSystemService(FirebaseAnalytics.Event.SEARCH);
        LinearLayout linearLayout = (LinearLayout) this.mSearchView.findViewById(C1299R.C1301id.search_edit_frame);
        if (linearLayout != null) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) linearLayout.getLayoutParams();
            marginLayoutParams.leftMargin = 0;
            linearLayout.setLayoutParams(marginLayoutParams);
        }
        ImageView imageView = (ImageView) this.mSearchView.findViewById(C1299R.C1301id.search_mag_icon);
        if (imageView != null) {
            MarginLayoutParams marginLayoutParams2 = (MarginLayoutParams) imageView.getLayoutParams();
            marginLayoutParams2.leftMargin = 0;
            imageView.setLayoutParams(marginLayoutParams2);
        }
        this.mToolbar.setContentInsetStartWithNavigation(0);
        this.mSearchView.setMaxWidth(Integer.MAX_VALUE);
        this.mSearchView.setSubmitButtonEnabled(false);
        this.mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        this.mSearchView.setIconifiedByDefault(false);
        this.mSearchView.setOnQueryTextListener(this);
        Class<FloatingActionsMenu> cls = FloatingActionsMenu.class;
        try {
            Field declaredField = cls.getDeclaredField("mLabelsStyle");
            declaredField.setAccessible(true);
            declaredField.set(this.mFloatingActionsMenu, Integer.valueOf(ThemeUtils.INSTANCE.getResourceId(this, C1299R.style.Floating_Actions_Menu)));
            Method declaredMethod = cls.getDeclaredMethod("createLabels", new Class[0]);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this.mFloatingActionsMenu, new Object[0]);
        } catch (Exception unused) {
        }
        this.mFabStartChat.setIconDrawable(ThemeUtils.INSTANCE.tintDrawableWithColor(ContextCompat.getDrawable(this, C1299R.C1300drawable.ic_person_black_24dp), ContextCompat.getColor(this, 17170443)));
        this.mFabCreateRoom.setIconDrawable(ThemeUtils.INSTANCE.tintDrawableWithColor(ContextCompat.getDrawable(this, C1299R.C1300drawable.ic_add_white), ContextCompat.getColor(this, 17170443)));
        this.mFabJoinRoom.setIconDrawable(ThemeUtils.INSTANCE.tintDrawableWithColor(ContextCompat.getDrawable(this, C1299R.C1300drawable.riot_tab_rooms), ContextCompat.getColor(this, 17170443)));
        this.mFloatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {
            public void onMenuExpanded() {
                VectorHomeActivity.this.touchGuard.animate().alpha(0.6f);
                VectorHomeActivity.this.touchGuard.setClickable(true);
            }

            public void onMenuCollapsed() {
                VectorHomeActivity.this.touchGuard.animate().alpha(0.0f);
                VectorHomeActivity.this.touchGuard.setClickable(false);
            }
        });
        this.touchGuard.setClickable(false);
    }

    private void resetFilter() {
        this.mSearchView.setQuery("", false);
        this.mSearchView.clearFocus();
        hideKeyboard();
    }

    private void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public boolean onQueryTextChange(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(this.mCurrentMenuId);
        final String sb2 = sb.toString();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                String charSequence = VectorHomeActivity.this.mSearchView.getQuery().toString();
                StringBuilder sb = new StringBuilder();
                sb.append(charSequence);
                sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                sb.append(VectorHomeActivity.this.mCurrentMenuId);
                if (TextUtils.equals(sb.toString(), sb2)) {
                    VectorHomeActivity.this.applyFilter(charSequence);
                }
            }
        }, 500);
        return true;
    }

    private Fragment getSelectedFragment() {
        switch (this.mCurrentMenuId) {
            case C1299R.C1301id.bottom_action_favourites /*2131296330*/:
                return this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_FAVOURITES);
            case C1299R.C1301id.bottom_action_groups /*2131296331*/:
                return this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_GROUPS);
            case C1299R.C1301id.bottom_action_home /*2131296332*/:
                return this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME);
            case C1299R.C1301id.bottom_action_people /*2131296333*/:
                return this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_PEOPLE);
            case C1299R.C1301id.bottom_action_rooms /*2131296334*/:
                return this.mFragmentManager.findFragmentByTag(TAG_FRAGMENT_ROOMS);
            default:
                return null;
        }
    }

    /* access modifiers changed from: private */
    public void applyFilter(String str) {
        Fragment selectedFragment = getSelectedFragment();
        if (selectedFragment instanceof AbsHomeFragment) {
            ((AbsHomeFragment) selectedFragment).applyFilter(str.trim());
        }
    }

    private void displayCryptoCorruption() {
        if (this.mSession != null && this.mSession.getCrypto() != null && this.mSession.getCrypto().isCorrupted()) {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (defaultSharedPreferences.getBoolean("isFirstCryptoAlertKey", true)) {
                defaultSharedPreferences.edit().putBoolean("isFirstCryptoAlertKey", false).apply();
                Builder builder = new Builder(this);
                builder.setMessage(getString(C1299R.string.e2e_need_log_in_again));
                builder.setCancelable(true).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CommonActivityUtils.logout(VectorApp.getCurrentActivity());
                    }
                });
                builder.create().show();
            }
        }
    }

    /* access modifiers changed from: private */
    public void processIntentUniversalLink() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) {
            Log.m209d(LOG_TAG, "## processIntentUniversalLink(): EXTRA_UNIVERSAL_LINK_URI present1");
            if (((Uri) intent.getParcelableExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI)) != null) {
                Intent intent2 = new Intent(VectorApp.getInstance(), VectorUniversalLinkReceiver.class);
                intent2.setAction(VectorUniversalLinkReceiver.BROADCAST_ACTION_UNIVERSAL_LINK_RESUME);
                intent2.putExtras(getIntent().getExtras());
                intent2.putExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_SENDER_ID, VectorUniversalLinkReceiver.HOME_SENDER_ID);
                sendBroadcast(intent2);
                showWaitingView();
                intent.removeExtra(VectorUniversalLinkReceiver.EXTRA_UNIVERSAL_LINK_URI);
                Log.m209d(LOG_TAG, "## processIntentUniversalLink(): Broadcast BROADCAST_ACTION_UNIVERSAL_LINK_RESUME sent");
            }
        }
    }

    private void revealFloatingActionMenu() {
        if (this.mFloatingActionsMenu != null) {
            this.mFloatingActionsMenu.collapse();
            this.mFloatingActionsMenu.setVisibility(0);
            this.mFabMain.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    VectorHomeActivity.this.mFloatingActionsMenu.setVisibility(0);
                }
            }).start();
        }
    }

    private void concealFloatingActionMenu() {
        if (this.mFloatingActionsMenu != null) {
            this.mFloatingActionsMenu.collapse();
            this.mFabMain.animate().scaleX(0.0f).scaleY(0.0f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    VectorHomeActivity.this.mFloatingActionsMenu.setVisibility(8);
                }
            }).start();
        }
    }

    public void hideFloatingActionButton(String str) {
        synchronized (this) {
            if (TextUtils.equals(this.mCurrentFragmentTag, str) && this.mFloatingActionsMenu != null) {
                if (this.mHideFloatingActionButton == null) {
                    concealFloatingActionMenu();
                    this.mHideFloatingActionButton = new Runnable() {
                        public void run() {
                            VectorHomeActivity.this.mHideFloatingActionButton = null;
                            VectorHomeActivity.this.showFloatingActionMenuIfRequired();
                        }
                    };
                } else {
                    this.mFloatingActionsMenu.removeCallbacks(this.mHideFloatingActionButton);
                }
                try {
                    this.mFloatingActionsMenu.postDelayed(this.mHideFloatingActionButton, 1000);
                } catch (Throwable th) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("failed to postDelayed ");
                    sb.append(th.getMessage());
                    Log.m211e(str2, sb.toString());
                    if (!(this.mHideFloatingActionButton == null || this.mFloatingActionsMenu == null)) {
                        this.mFloatingActionsMenu.removeCallbacks(this.mHideFloatingActionButton);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            VectorHomeActivity.this.showFloatingActionMenuIfRequired();
                        }
                    });
                }
            }
        }
    }

    public View getFloatingActionButton() {
        return this.mFabMain;
    }

    private void invitePeopleToNewRoom() {
        Intent intent = new Intent(this, VectorRoomCreationActivity.class);
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
        startActivity(intent);
    }

    private void createRoom() {
        showWaitingView();
        this.mSession.createRoom(new SimpleApiCallback<String>(this) {
            public void onSuccess(final String str) {
                VectorHomeActivity.this.waitingView.post(new Runnable() {
                    public void run() {
                        VectorHomeActivity.this.hideWaitingView();
                        HashMap hashMap = new HashMap();
                        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorHomeActivity.this.mSession.getMyUserId());
                        hashMap.put("EXTRA_ROOM_ID", str);
                        hashMap.put(VectorRoomActivity.EXTRA_EXPAND_ROOM_HEADER, Boolean.valueOf(true));
                        CommonActivityUtils.goToRoomPage(VectorHomeActivity.this, VectorHomeActivity.this.mSession, hashMap);
                    }
                });
            }

            private void onError(final String str) {
                VectorHomeActivity.this.waitingView.post(new Runnable() {
                    public void run() {
                        if (str != null) {
                            Toast.makeText(VectorHomeActivity.this, str, 1).show();
                        }
                        VectorHomeActivity.this.hideWaitingView();
                    }
                });
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                    VectorHomeActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                } else {
                    onError(matrixError.getLocalizedMessage());
                }
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }
        });
    }

    private void joinARoom() {
        LayoutInflater from = LayoutInflater.from(this);
        Builder builder = new Builder(this);
        View inflate = from.inflate(C1299R.layout.dialog_join_room_by_id, null);
        builder.setView(inflate);
        final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.join_room_edit_text);
        editText.setTextColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.riot_primary_text_color));
        builder.setCancelable(false).setPositiveButton(C1299R.string.join, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                VectorHomeActivity.this.showWaitingView();
                String trim = editText.getText().toString().trim();
                VectorHomeActivity.this.mSession.joinRoom(VectorHomeActivity.this.getString(C1299R.string.room_search_string, new Object[]{trim}), new ApiCallback<String>() {
                    public void onSuccess(String str) {
                        VectorHomeActivity.this.hideWaitingView();
                        HashMap hashMap = new HashMap();
                        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorHomeActivity.this.mSession.getMyUserId());
                        hashMap.put("EXTRA_ROOM_ID", str);
                        CommonActivityUtils.goToRoomPage(VectorHomeActivity.this, VectorHomeActivity.this.mSession, hashMap);
                    }

                    private void onError(final String str) {
                        VectorHomeActivity.this.waitingView.post(new Runnable() {
                            public void run() {
                                if (str != null) {
                                    Toast.makeText(VectorHomeActivity.this, str, 1).show();
                                }
                                VectorHomeActivity.this.hideWaitingView();
                            }
                        });
                    }

                    public void onNetworkError(Exception exc) {
                        onError(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                            VectorHomeActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                        } else {
                            onError(matrixError.getLocalizedMessage());
                        }
                    }

                    public void onUnexpectedError(Exception exc) {
                        onError(exc.getLocalizedMessage());
                    }
                });
            }
        }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog create = builder.create();
        create.show();
        final Button button = create.getButton(-1);
        if (button != null) {
            button.setEnabled(false);
            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    button.setEnabled(!TextUtils.isEmpty(editText.getText().toString().trim()));
                }
            });
        }
    }

    public List<Room> getRoomInvitations() {
        if (this.mRoomInvitations == null) {
            this.mRoomInvitations = new ArrayList();
        } else {
            this.mRoomInvitations.clear();
        }
        if (this.mDirectChatInvitations == null) {
            this.mDirectChatInvitations = new ArrayList();
        } else {
            this.mDirectChatInvitations.clear();
        }
        if (this.mSession.getDataHandler().getStore() == null) {
            Log.m211e(LOG_TAG, "## getRoomInvitations() : null store");
            return new ArrayList();
        }
        for (RoomSummary roomSummary : this.mSession.getDataHandler().getStore().getSummaries()) {
            if (roomSummary != null) {
                Room room = this.mSession.getDataHandler().getStore().getRoom(roomSummary.getRoomId());
                if (room != null && !room.isConferenceUserRoom() && room.isInvited()) {
                    if (room.isDirectChatInvitation()) {
                        this.mDirectChatInvitations.add(room);
                    } else {
                        this.mRoomInvitations.add(room);
                    }
                }
            }
        }
        Comparator roomsDateComparator = RoomUtils.getRoomsDateComparator(this.mSession, true);
        Collections.sort(this.mDirectChatInvitations, roomsDateComparator);
        Collections.sort(this.mRoomInvitations, roomsDateComparator);
        ArrayList arrayList = new ArrayList();
        switch (this.mCurrentMenuId) {
            case C1299R.C1301id.bottom_action_people /*2131296333*/:
                arrayList.addAll(this.mDirectChatInvitations);
                break;
            case C1299R.C1301id.bottom_action_rooms /*2131296334*/:
                arrayList.addAll(this.mRoomInvitations);
                break;
            default:
                arrayList.addAll(this.mDirectChatInvitations);
                arrayList.addAll(this.mRoomInvitations);
                Collections.sort(arrayList, roomsDateComparator);
                break;
        }
        return arrayList;
    }

    public void onPreviewRoom(MXSession mXSession, String str) {
        Room room = mXSession.getDataHandler().getRoom(str);
        RoomPreviewData roomPreviewData = new RoomPreviewData(this.mSession, str, null, (room == null || room.getLiveState() == null) ? null : room.getLiveState().getAlias(), null);
        CommonActivityUtils.previewRoom(this, roomPreviewData);
    }

    private ApiCallback<Void> createForgetLeaveCallback(final String str, final ApiCallback<Void> apiCallback) {
        return new ApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                EventStreamService.cancelNotificationsForRoomId(VectorHomeActivity.this.mSession.getMyUserId(), str);
                VectorHomeActivity.this.hideWaitingView();
                if (apiCallback != null) {
                    apiCallback.onSuccess(null);
                }
            }

            private void onError(String str) {
                VectorHomeActivity.this.hideWaitingView();
                Toast.makeText(VectorHomeActivity.this, str, 1).show();
            }

            public void onNetworkError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }

            public void onMatrixError(MatrixError matrixError) {
                if (MatrixError.M_CONSENT_NOT_GIVEN.equals(matrixError.errcode)) {
                    VectorHomeActivity.this.hideWaitingView();
                    VectorHomeActivity.this.getConsentNotGivenHelper().displayDialog(matrixError);
                    return;
                }
                onError(matrixError.getLocalizedMessage());
            }

            public void onUnexpectedError(Exception exc) {
                onError(exc.getLocalizedMessage());
            }
        };
    }

    public void onForgetRoom(String str, ApiCallback<Void> apiCallback) {
        Room room = this.mSession.getDataHandler().getRoom(str);
        if (room != null) {
            showWaitingView();
            room.forget(createForgetLeaveCallback(str, apiCallback));
        }
    }

    public void onRejectInvitation(String str, ApiCallback<Void> apiCallback) {
        Room room = this.mSession.getDataHandler().getRoom(str);
        if (room != null) {
            showWaitingView();
            room.leave(createForgetLeaveCallback(str, apiCallback));
        }
    }

    /* access modifiers changed from: private */
    public void exportKeysAndSignOut() {
        View inflate = getLayoutInflater().inflate(C1299R.layout.dialog_export_e2e_keys, null);
        Builder builder = new Builder(this);
        builder.setTitle(C1299R.string.encryption_export_room_keys);
        builder.setView(inflate);
        final TextInputEditText textInputEditText = (TextInputEditText) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_passphrase_edit_text);
        final TextInputEditText textInputEditText2 = (TextInputEditText) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_confirm_passphrase_edit_text);
        final Button button = (Button) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_export_button);
        C150426 r4 = new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                button.setEnabled(!TextUtils.isEmpty(textInputEditText.getText()) && TextUtils.equals(textInputEditText.getText(), textInputEditText2.getText()));
            }
        };
        textInputEditText.addTextChangedListener(r4);
        textInputEditText2.addTextChangedListener(r4);
        button.setEnabled(false);
        final AlertDialog show = builder.show();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VectorHomeActivity.this.showWaitingView();
                CommonActivityUtils.exportKeys(VectorHomeActivity.this.mSession, textInputEditText.getText().toString(), new ApiCallback<String>() {
                    private void onDone(String str) {
                        VectorHomeActivity.this.hideWaitingView();
                        Builder builder = new Builder(VectorHomeActivity.this);
                        builder.setMessage(str);
                        builder.setCancelable(false).setPositiveButton(C1299R.string.action_sign_out, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                VectorHomeActivity.this.showWaitingView();
                                CommonActivityUtils.logout(VectorHomeActivity.this);
                            }
                        }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        try {
                            builder.create().show();
                        } catch (Exception e) {
                            String access$000 = VectorHomeActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## exportKeysAndSignOut() failed ");
                            sb.append(e.getMessage());
                            Log.m211e(access$000, sb.toString());
                        }
                    }

                    public void onSuccess(String str) {
                        onDone(VectorHomeActivity.this.getString(C1299R.string.encryption_export_saved_as, new Object[]{str}));
                    }

                    public void onNetworkError(Exception exc) {
                        onDone(exc.getLocalizedMessage());
                    }

                    public void onMatrixError(MatrixError matrixError) {
                        onDone(matrixError.getLocalizedMessage());
                    }

                    public void onUnexpectedError(Exception exc) {
                        onDone(exc.getLocalizedMessage());
                    }
                });
                show.dismiss();
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void initSlidingMenu() {
        C150928 r0 = new ActionBarDrawerToggle(this, this.mDrawerLayout, this.mToolbar, C1299R.string.action_open, C1299R.string.action_close) {
            public void onDrawerOpened(View view) {
            }

            public void onDrawerClosed(View view) {
                switch (VectorHomeActivity.this.mSlidingMenuIndex) {
                    case C1299R.C1301id.sliding_menu_app_tac /*2131297028*/:
                        VectorUtils.displayAppTac();
                        break;
                    case C1299R.C1301id.sliding_menu_exit /*2131297029*/:
                        if (EventStreamService.getInstance() != null) {
                            EventStreamService.getInstance().stopNow();
                        }
                        VectorHomeActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VectorHomeActivity.this.finish();
                                System.exit(0);
                            }
                        });
                        break;
                    case C1299R.C1301id.sliding_menu_send_bug_report /*2131297031*/:
                        BugReporter.sendBugReport();
                        break;
                    case C1299R.C1301id.sliding_menu_settings /*2131297032*/:
                        Intent intent = new Intent(VectorHomeActivity.this, VectorSettingsActivity.class);
                        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorHomeActivity.this.mSession.getMyUserId());
                        VectorHomeActivity.this.startActivity(intent);
                        break;
                    case C1299R.C1301id.sliding_menu_sign_out /*2131297033*/:
                        Builder builder = new Builder(VectorHomeActivity.this);
                        builder.setMessage(VectorHomeActivity.this.getString(C1299R.string.action_sign_out_confirmation));
                        builder.setCancelable(false).setPositiveButton(C1299R.string.action_sign_out, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                VectorHomeActivity.this.showWaitingView();
                                CommonActivityUtils.logout(VectorHomeActivity.this);
                            }
                        }).setNeutralButton(C1299R.string.encryption_export_export, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                VectorHomeActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        VectorHomeActivity.this.exportKeysAndSignOut();
                                    }
                                });
                            }
                        }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create().show();
                        break;
                    case C1299R.C1301id.sliding_menu_third_party_notices /*2131297034*/:
                        VectorUtils.displayThirdPartyLicenses();
                        break;
                }
                VectorHomeActivity.this.mSlidingMenuIndex = -1;
            }
        };
        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                VectorHomeActivity.this.mDrawerLayout.closeDrawers();
                VectorHomeActivity.this.mSlidingMenuIndex = menuItem.getItemId();
                return true;
            }
        });
        this.mDrawerLayout.setDrawerListener(r0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(CommonActivityUtils.tintDrawable(this, ContextCompat.getDrawable(this, C1299R.C1300drawable.ic_material_menu_white), C1299R.attr.primary_control_color));
        }
    }

    /* access modifiers changed from: private */
    public void refreshSlidingMenu() {
        MenuItem findItem = this.navigationView.getMenu().findItem(C1299R.C1301id.sliding_menu_version);
        if (findItem != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(getString(C1299R.string.room_sliding_menu_version));
            sb.append(StringUtils.SPACE);
            sb.append(VectorUtils.getApplicationVersion(this));
            findItem.setTitle(sb.toString());
        }
        TextView textView = (TextView) this.navigationView.findViewById(C1299R.C1301id.home_menu_main_displayname);
        if (textView != null) {
            textView.setText(this.mSession.getMyUser().displayname);
        }
        TextView textView2 = (TextView) this.navigationView.findViewById(C1299R.C1301id.home_menu_main_matrix_id);
        if (textView2 != null) {
            textView2.setText(VectorUtils.getPlainId(this.mSession.getMyUserId()));
        }
        ImageView imageView = (ImageView) this.navigationView.findViewById(C1299R.C1301id.home_menu_main_avatar);
        if (imageView != null) {
            VectorUtils.loadUserAvatar(this, this.mSession, imageView, this.mSession.getMyUser());
        } else {
            this.navigationView.post(new Runnable() {
                public void run() {
                    VectorHomeActivity.this.refreshSlidingMenu();
                }
            });
        }
    }

    public void startCall(String str, String str2, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
        if (str != null && str2 != null) {
            final Intent intent = new Intent(this, VectorCallViewActivity.class);
            intent.putExtra(VectorCallViewActivity.EXTRA_MATRIX_ID, str);
            intent.putExtra(VectorCallViewActivity.EXTRA_CALL_ID, str2);
            if (mXUsersDevicesMap != null) {
                intent.putExtra(VectorCallViewActivity.EXTRA_UNKNOWN_DEVICES, mXUsersDevicesMap);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    VectorHomeActivity.this.startActivity(intent);
                }
            });
        }
    }

    private void addBadgeEventsListener() {
        this.mSession.getDataHandler().addListener(this.mBadgeEventsListener);
        refreshUnreadBadges();
    }

    private void removeBadgeEventsListener() {
        this.mSession.getDataHandler().removeListener(this.mBadgeEventsListener);
    }

    private void removeMenuShiftMode() {
        int childCount = this.mBottomNavigationView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (this.mBottomNavigationView.getChildAt(i) instanceof BottomNavigationMenuView) {
                BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) this.mBottomNavigationView.getChildAt(i);
                try {
                    Field declaredField = bottomNavigationMenuView.getClass().getDeclaredField("mShiftingMode");
                    declaredField.setAccessible(true);
                    declaredField.setBoolean(bottomNavigationMenuView, false);
                    declaredField.setAccessible(false);
                } catch (Exception e) {
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## removeMenuShiftMode failed ");
                    sb.append(e.getMessage());
                    Log.m211e(str, sb.toString());
                }
            }
        }
    }

    @SuppressLint({"RestrictedApi"})
    private void addUnreadBadges() {
        float f = getResources().getDisplayMetrics().density;
        int i = (int) ((18.0f * f) + 0.5f);
        int i2 = (int) ((f * 7.0f) + 0.5f);
        removeMenuShiftMode();
        int dimensionPixelSize = getResources().getDimensionPixelSize(C1299R.dimen.design_bottom_navigation_active_text_size);
        for (int i3 = 0; i3 < this.mBottomNavigationView.getMenu().size(); i3++) {
            try {
                int itemId = this.mBottomNavigationView.getMenu().getItem(i3).getItemId();
                BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) this.mBottomNavigationView.findViewById(itemId);
                bottomNavigationItemView.setShiftingMode(false);
                Field declaredField = bottomNavigationItemView.getClass().getDeclaredField("mDefaultMargin");
                declaredField.setAccessible(true);
                declaredField.setInt(bottomNavigationItemView, declaredField.getInt(bottomNavigationItemView) + (dimensionPixelSize / 2));
                declaredField.setAccessible(false);
                Field declaredField2 = bottomNavigationItemView.getClass().getDeclaredField("mShiftAmount");
                declaredField2.setAccessible(true);
                declaredField2.setInt(bottomNavigationItemView, 0);
                declaredField2.setAccessible(false);
                bottomNavigationItemView.setChecked(bottomNavigationItemView.getItemData().isChecked());
                View findViewById = bottomNavigationItemView.findViewById(C1299R.C1301id.icon);
                if (findViewById.getParent() instanceof FrameLayout) {
                    UnreadCounterBadgeView unreadCounterBadgeView = new UnreadCounterBadgeView(findViewById.getContext());
                    LayoutParams layoutParams = (LayoutParams) findViewById.getLayoutParams();
                    LayoutParams layoutParams2 = new LayoutParams(-2, -2);
                    layoutParams2.setMargins(layoutParams.leftMargin + i, layoutParams.topMargin - i2, layoutParams.rightMargin, layoutParams.bottomMargin);
                    layoutParams2.gravity = layoutParams.gravity;
                    ((FrameLayout) findViewById.getParent()).addView(unreadCounterBadgeView, layoutParams2);
                    this.mBadgeViewByIndex.put(Integer.valueOf(itemId), unreadCounterBadgeView);
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## addUnreadBadges failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        refreshUnreadBadges();
    }

    public void refreshUnreadBadges() {
        MXDataHandler dataHandler = this.mSession.getDataHandler();
        if (dataHandler != null) {
            IMXStore store = dataHandler.getStore();
            if (store != null) {
                BingRulesManager bingRulesManager = dataHandler.getBingRulesManager();
                Collection<RoomSummary> summaries = store.getSummaries();
                HashMap hashMap = new HashMap();
                HashSet hashSet = new HashSet();
                for (RoomSummary roomSummary : summaries) {
                    Room room = store.getRoom(roomSummary.getRoomId());
                    if (room != null) {
                        hashMap.put(room, roomSummary);
                        if (!room.isConferenceUserRoom() && room.isInvited() && room.isDirectChatInvitation()) {
                            hashSet.add(room.getRoomId());
                        }
                    }
                }
                HashSet<Integer> hashSet2 = new HashSet<>(this.mBadgeViewByIndex.keySet());
                hashSet2.remove(Integer.valueOf(C1299R.C1301id.bottom_action_home));
                for (Integer num : hashSet2) {
                    HashSet hashSet3 = new HashSet();
                    if (num.intValue() == C1299R.C1301id.bottom_action_favourites) {
                        for (Room roomId : this.mSession.roomsWithTag(RoomTag.ROOM_TAG_FAVOURITE)) {
                            hashSet3.add(roomId.getRoomId());
                        }
                    } else if (num.intValue() == C1299R.C1301id.bottom_action_people) {
                        hashSet3.addAll(this.mSession.getDataHandler().getDirectChatRoomIdsList());
                        for (Room room2 : hashMap.keySet()) {
                            if (room2.isDirectChatInvitation() && !room2.isConferenceUserRoom()) {
                                hashSet3.add(room2.getRoomId());
                            }
                        }
                        for (Room roomId2 : this.mSession.roomsWithTag(RoomTag.ROOM_TAG_LOW_PRIORITY)) {
                            hashSet3.remove(roomId2.getRoomId());
                        }
                    } else if (num.intValue() == C1299R.C1301id.bottom_action_rooms) {
                        HashSet hashSet4 = new HashSet(this.mSession.getDataHandler().getDirectChatRoomIdsList());
                        HashSet hashSet5 = new HashSet(this.mSession.roomIdsWithTag(RoomTag.ROOM_TAG_LOW_PRIORITY));
                        hashSet4.addAll(hashSet);
                        for (Room room3 : hashMap.keySet()) {
                            if (!room3.isConferenceUserRoom() && !hashSet4.contains(room3.getRoomId()) && !hashSet5.contains(room3.getRoomId())) {
                                hashSet3.add(room3.getRoomId());
                            }
                        }
                    }
                    Iterator it = hashSet3.iterator();
                    int i = 0;
                    int i2 = 0;
                    int i3 = 0;
                    while (it.hasNext()) {
                        String str = (String) it.next();
                        Room room4 = store.getRoom(str);
                        if (room4 != null) {
                            i2 += room4.getHighlightCount();
                            if (room4.isInvited()) {
                                i3++;
                            } else {
                                int notificationCount = room4.getNotificationCount();
                                if (bingRulesManager.isRoomMentionOnly(str)) {
                                    notificationCount = room4.getHighlightCount();
                                }
                                if (notificationCount > 0) {
                                    i3++;
                                }
                            }
                        }
                    }
                    if (i2 == 0) {
                        i = i3 != 0 ? 1 : 2;
                    }
                    if (num.intValue() == C1299R.C1301id.bottom_action_favourites) {
                        ((UnreadCounterBadgeView) this.mBadgeViewByIndex.get(num)).updateText(i3 > 0 ? "•" : "", i);
                    } else {
                        ((UnreadCounterBadgeView) this.mBadgeViewByIndex.get(num)).updateCounter(i3, i);
                    }
                }
            }
        }
    }

    private void checkDeviceId() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(NO_DEVICE_ID_WARNING_KEY, true)) {
            defaultSharedPreferences.edit().putBoolean(NO_DEVICE_ID_WARNING_KEY, false).apply();
            if (TextUtils.isEmpty(this.mSession.getCredentials().deviceId)) {
                new Builder(this).setMessage(C1299R.string.e2e_enabling_on_app_update).setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        CommonActivityUtils.logout(VectorHomeActivity.this);
                    }
                }).setNegativeButton(C1299R.string.later, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @OnClick({2131296514})
    public void touchGuardClicked() {
        this.mFloatingActionsMenu.collapse();
    }

    /* access modifiers changed from: 0000 */
    @OnClick({2131296356})
    public void fabMenuStartChat() {
        this.mFloatingActionsMenu.collapse();
        invitePeopleToNewRoom();
    }

    /* access modifiers changed from: 0000 */
    @OnClick({2131296348})
    public void fabMenuCreateRoom() {
        this.mFloatingActionsMenu.collapse();
        createRoom();
    }

    /* access modifiers changed from: 0000 */
    @OnClick({2131296351})
    public void fabMenuJoinRoom() {
        this.mFloatingActionsMenu.collapse();
        joinARoom();
    }

    public void dispatchOnSummariesUpdate() {
        Fragment selectedFragment = getSelectedFragment();
        if (selectedFragment != null && (selectedFragment instanceof AbsHomeFragment)) {
            ((AbsHomeFragment) selectedFragment).onSummariesUpdate();
        }
    }

    private void addEventsListener() {
        this.mEventsListener = new MXEventListener() {
            private boolean mRefreshOnChunkEnd = false;

            private void onForceRefresh() {
                if (VectorHomeActivity.this.mSyncInProgressView.getVisibility() != 0) {
                    VectorHomeActivity.this.dispatchOnSummariesUpdate();
                }
            }

            public void onAccountInfoUpdate(MyUser myUser) {
                VectorHomeActivity.this.refreshSlidingMenu();
            }

            public void onInitialSyncComplete(String str) {
                Log.m209d(VectorHomeActivity.LOG_TAG, "## onInitialSyncComplete()");
                VectorHomeActivity.this.dispatchOnSummariesUpdate();
            }

            public void onLiveEventsChunkProcessed(String str, String str2) {
                if (VectorApp.getCurrentActivity() == VectorHomeActivity.this && this.mRefreshOnChunkEnd) {
                    VectorHomeActivity.this.dispatchOnSummariesUpdate();
                }
                this.mRefreshOnChunkEnd = false;
                VectorHomeActivity.this.mSyncInProgressView.setVisibility(8);
                VectorHomeActivity.this.processIntentUniversalLink();
            }

            public void onLiveEvent(Event event, RoomState roomState) {
                String type = event.getType();
                this.mRefreshOnChunkEnd = ((event.roomId != null && RoomSummary.isSupportedEvent(event)) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_TAGS.equals(type) || Event.EVENT_TYPE_REDACTION.equals(type) || Event.EVENT_TYPE_RECEIPT.equals(type) || Event.EVENT_TYPE_STATE_ROOM_AVATAR.equals(type) || Event.EVENT_TYPE_STATE_ROOM_THIRD_PARTY_INVITE.equals(type)) | this.mRefreshOnChunkEnd;
            }

            public void onReceiptEvent(String str, List<String> list) {
                this.mRefreshOnChunkEnd |= list.indexOf(VectorHomeActivity.this.mSession.getCredentials().userId) >= 0;
            }

            public void onRoomTagEvent(String str) {
                this.mRefreshOnChunkEnd = true;
            }

            public void onStoreReady() {
                onForceRefresh();
                if (VectorHomeActivity.this.mSharedFilesIntent != null) {
                    Log.m209d(VectorHomeActivity.LOG_TAG, "shared intent : the store is now ready, display sendFilesTo");
                    CommonActivityUtils.sendFilesTo(VectorHomeActivity.this, VectorHomeActivity.this.mSharedFilesIntent);
                    VectorHomeActivity.this.mSharedFilesIntent = null;
                }
            }

            public void onLeaveRoom(String str) {
                EventStreamService.cancelNotificationsForRoomId(VectorHomeActivity.this.mSession.getMyUserId(), str);
                onForceRefresh();
            }

            public void onNewRoom(String str) {
                onForceRefresh();
            }

            public void onJoinRoom(String str) {
                onForceRefresh();
            }

            public void onDirectMessageChatRoomsListUpdate() {
                this.mRefreshOnChunkEnd = true;
            }

            public void onEventDecrypted(Event event) {
                RoomSummary summary = VectorHomeActivity.this.mSession.getDataHandler().getStore().getSummary(event.roomId);
                if (summary != null) {
                    Event latestReceivedEvent = summary.getLatestReceivedEvent();
                    if (latestReceivedEvent != null && TextUtils.equals(latestReceivedEvent.eventId, event.eventId)) {
                        VectorHomeActivity.this.dispatchOnSummariesUpdate();
                    }
                }
            }
        };
        this.mSession.getDataHandler().addListener(this.mEventsListener);
    }

    private void removeEventsListener() {
        if (this.mSession.isAlive()) {
            this.mSession.getDataHandler().removeListener(this.mEventsListener);
        }
    }
}
