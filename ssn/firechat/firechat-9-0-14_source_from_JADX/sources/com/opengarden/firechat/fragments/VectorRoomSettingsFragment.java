package com.opengarden.firechat.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.GravityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorMediasPickerActivity;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.RoomAccountData;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.data.RoomTag;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaUploadListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.PowerLevels;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.RoomNotificationState;
import com.opengarden.firechat.matrixsdk.util.BingRulesManager.onBingRuleUpdateListener;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import com.opengarden.firechat.preference.AddressPreference;
import com.opengarden.firechat.preference.RoomAvatarPreference;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference.OnPreferenceLongClickListener;
import com.opengarden.firechat.preference.VectorListPreference;
import com.opengarden.firechat.preference.VectorListPreference.OnPreferenceWarningIconClickListener;
import com.opengarden.firechat.preference.VectorSwitchPreference;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class VectorRoomSettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    private static final String ACCESS_RULES_ANYONE_WITH_LINK_APART_GUEST = "2";
    private static final String ACCESS_RULES_ANYONE_WITH_LINK_INCLUDING_GUEST = "3";
    private static final String ACCESS_RULES_ONLY_PEOPLE_INVITED = "1";
    private static final String ADDRESSES_PREFERENCE_KEY_BASE = "ADDRESSES_PREFERENCE_KEY_BASE";
    private static final String ADD_ADDRESSES_PREFERENCE_KEY = "ADD_ADDRESSES_PREFERENCE_KEY";
    private static final String BANNED_PREFERENCE_KEY_BASE = "BANNED_PREFERENCE_KEY_BASE";
    private static final boolean DO_NOT_UPDATE_UI = false;
    private static final String EXTRA_MATRIX_ID = "KEY_EXTRA_MATRIX_ID";
    private static final String EXTRA_ROOM_ID = "KEY_EXTRA_ROOM_ID";
    private static final String FLAIR_PREFERENCE_KEY_BASE = "FLAIR_PREFERENCE_KEY_BASE";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomSettingsFragment";
    private static final String NO_LOCAL_ADDRESS_PREFERENCE_KEY = "NO_LOCAL_ADDRESS_PREFERENCE_KEY";
    private static final String PREF_KEY_ADDRESSES = "addresses";
    private static final String PREF_KEY_ADVANCED = "advanced";
    private static final String PREF_KEY_BANNED = "banned";
    private static final String PREF_KEY_BANNED_DIVIDER = "banned_divider";
    private static final String PREF_KEY_ENCRYPTION = "encryptionKey";
    private static final String PREF_KEY_FLAIR = "flair";
    private static final String PREF_KEY_FLAIR_DIVIDER = "flair_divider";
    private static final String PREF_KEY_ROOM_ACCESS_RULES_LIST = "roomAccessRulesList";
    private static final String PREF_KEY_ROOM_DIRECTORY_VISIBILITY_SWITCH = "roomNameListedInDirectorySwitch";
    private static final String PREF_KEY_ROOM_HISTORY_READABILITY_LIST = "roomReadHistoryRulesList";
    private static final String PREF_KEY_ROOM_INTERNAL_ID = "roomInternalId";
    private static final String PREF_KEY_ROOM_LEAVE = "roomLeave";
    private static final String PREF_KEY_ROOM_NAME = "roomNameEditText";
    private static final String PREF_KEY_ROOM_NOTIFICATIONS_LIST = "roomNotificationPreference";
    private static final String PREF_KEY_ROOM_PHOTO_AVATAR = "roomPhotoAvatar";
    private static final String PREF_KEY_ROOM_TAG_LIST = "roomTagList";
    private static final String PREF_KEY_ROOM_TOPIC = "roomTopicEditText";
    private static final int REQ_CODE_UPDATE_ROOM_AVATAR = 16;
    private static final String UNKNOWN_VALUE = "UNKNOWN_VALUE";
    private static final boolean UPDATE_UI = true;
    private PreferenceCategory mAddressesSettingsCategory;
    private PreferenceCategory mAdvandceSettingsCategory;
    /* access modifiers changed from: private */
    public final ApiCallback mAliasUpdatesCallback = new ApiCallback<Void>() {
        public void onSuccess(Void voidR) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomSettingsFragment.this.hideLoadingView(false);
                    VectorRoomSettingsFragment.this.refreshAddresses();
                }
            });
        }

        private void onError(final String str) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(VectorRoomSettingsFragment.this.getActivity(), str, 0).show();
                    VectorRoomSettingsFragment.this.hideLoadingView(false);
                    VectorRoomSettingsFragment.this.refreshAddresses();
                }
            });
        }

        public void onNetworkError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }

        public void onMatrixError(MatrixError matrixError) {
            onError(matrixError.getLocalizedMessage());
        }

        public void onUnexpectedError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }
    };
    private PreferenceCategory mBannedMembersSettingsCategory;
    private PreferenceCategory mBannedMembersSettingsCategoryDivider;
    private BingRulesManager mBingRulesManager;
    private final MXEventListener mEventListener = new MXEventListener() {
        public void onLiveEvent(final Event event, RoomState roomState) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    String type = event.getType();
                    if (Event.EVENT_TYPE_STATE_ROOM_NAME.equals(type) || Event.EVENT_TYPE_STATE_ROOM_ALIASES.equals(type) || Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type) || Event.EVENT_TYPE_STATE_ROOM_AVATAR.equals(type) || Event.EVENT_TYPE_STATE_ROOM_TOPIC.equals(type) || Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS.equals(type) || Event.EVENT_TYPE_STATE_HISTORY_VISIBILITY.equals(type) || Event.EVENT_TYPE_STATE_ROOM_JOIN_RULES.equals(type) || Event.EVENT_TYPE_STATE_ROOM_GUEST_ACCESS.equals(type)) {
                        String access$200 = VectorRoomSettingsFragment.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## onLiveEvent() event = ");
                        sb.append(type);
                        Log.m209d(access$200, sb.toString());
                        VectorRoomSettingsFragment.this.updateUi();
                    }
                    if (Event.EVENT_TYPE_MESSAGE_ENCRYPTION.equals(type)) {
                        VectorRoomSettingsFragment.this.refreshEndToEnd();
                    }
                    if (Event.EVENT_TYPE_STATE_CANONICAL_ALIAS.equals(type) || Event.EVENT_TYPE_STATE_ROOM_ALIASES.equals(type) || Event.EVENT_TYPE_STATE_ROOM_POWER_LEVELS.equals(type)) {
                        Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "## onLiveEvent() refresh the addresses list");
                        VectorRoomSettingsFragment.this.refreshAddresses();
                    }
                    if (Event.EVENT_TYPE_STATE_ROOM_MEMBER.equals(type)) {
                        Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "## onLiveEvent() refresh the banned members list");
                        VectorRoomSettingsFragment.this.refreshBannedMembersList();
                    }
                }
            });
        }

        public void onRoomFlush(String str) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomSettingsFragment.this.updateUi();
                }
            });
        }

        public void onRoomTagEvent(String str) {
            Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "## onRoomTagEvent()");
            VectorRoomSettingsFragment.this.updateUi();
        }

        public void onBingRulesUpdate() {
            VectorRoomSettingsFragment.this.updateUi();
        }
    };
    private PreferenceCategory mFlairSettingsCategory;
    /* access modifiers changed from: private */
    public final ApiCallback mFlairUpdatesCallback = new ApiCallback<Void>() {
        public void onSuccess(Void voidR) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomSettingsFragment.this.hideLoadingView(false);
                    VectorRoomSettingsFragment.this.refreshFlair();
                }
            });
        }

        private void onError(final String str) {
            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(VectorRoomSettingsFragment.this.getActivity(), str, 0).show();
                    VectorRoomSettingsFragment.this.hideLoadingView(false);
                    VectorRoomSettingsFragment.this.refreshFlair();
                }
            });
        }

        public void onNetworkError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }

        public void onMatrixError(MatrixError matrixError) {
            onError(matrixError.getLocalizedMessage());
        }

        public void onUnexpectedError(Exception exc) {
            onError(exc.getLocalizedMessage());
        }
    };
    private boolean mIsUiUpdateSkipped;
    private final IMXNetworkEventListener mNetworkListener = new IMXNetworkEventListener() {
        public void onNetworkConnectionUpdate(boolean z) {
            VectorRoomSettingsFragment.this.updateUi();
        }
    };
    /* access modifiers changed from: private */
    public View mParentFragmentContainerView;
    /* access modifiers changed from: private */
    public View mParentLoadingView;
    /* access modifiers changed from: private */
    public Room mRoom;
    private VectorListPreference mRoomAccessRulesListPreference;
    /* access modifiers changed from: private */
    public CheckBoxPreference mRoomDirectoryVisibilitySwitch;
    private ListPreference mRoomHistoryReadabilityRulesListPreference;
    private EditTextPreference mRoomNameEditTxt;
    private ListPreference mRoomNotificationsPreference;
    /* access modifiers changed from: private */
    public RoomAvatarPreference mRoomPhotoAvatar;
    private ListPreference mRoomTagListPreference;
    private EditTextPreference mRoomTopicEditTxt;
    /* access modifiers changed from: private */
    public MXSession mSession;
    /* access modifiers changed from: private */
    public final ApiCallback<Void> mUpdateCallback = new ApiCallback<Void>() {
        private void onDone(String str, final boolean z) {
            if (VectorRoomSettingsFragment.this.getActivity() != null) {
                if (!TextUtils.isEmpty(str)) {
                    Toast.makeText(VectorRoomSettingsFragment.this.getActivity(), str, 1).show();
                }
                VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomSettingsFragment.this.hideLoadingView(z);
                    }
                });
            }
        }

        public void onSuccess(Void voidR) {
            Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "##update succeed");
            onDone(null, true);
        }

        public void onNetworkError(Exception exc) {
            String access$200 = VectorRoomSettingsFragment.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##NetworkError ");
            sb.append(exc.getLocalizedMessage());
            Log.m217w(access$200, sb.toString());
            onDone(exc.getLocalizedMessage(), false);
        }

        public void onMatrixError(MatrixError matrixError) {
            String access$200 = VectorRoomSettingsFragment.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##MatrixError ");
            sb.append(matrixError.getLocalizedMessage());
            Log.m217w(access$200, sb.toString());
            onDone(matrixError.getLocalizedMessage(), false);
        }

        public void onUnexpectedError(Exception exc) {
            String access$200 = VectorRoomSettingsFragment.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##UnexpectedError ");
            sb.append(exc.getLocalizedMessage());
            Log.m217w(access$200, sb.toString());
            onDone(exc.getLocalizedMessage(), false);
        }
    };

    public static VectorRoomSettingsFragment newInstance(String str, String str2) {
        VectorRoomSettingsFragment vectorRoomSettingsFragment = new VectorRoomSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MATRIX_ID, str);
        bundle.putString(EXTRA_ROOM_ID, str2);
        vectorRoomSettingsFragment.setArguments(bundle);
        return vectorRoomSettingsFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.m209d(LOG_TAG, "## onCreate() IN");
        String string = getArguments().getString(EXTRA_MATRIX_ID);
        String string2 = getArguments().getString(EXTRA_ROOM_ID);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            Log.m211e(LOG_TAG, "## onCreate(): fragment extras (MatrixId or RoomId) are missing");
            getActivity().finish();
        } else {
            this.mSession = Matrix.getInstance(getActivity()).getSession(string);
            if (this.mSession != null && this.mSession.isAlive()) {
                this.mRoom = this.mSession.getDataHandler().getRoom(string2);
                this.mBingRulesManager = this.mSession.getDataHandler().getBingRulesManager();
            }
            if (this.mRoom == null) {
                Log.m211e(LOG_TAG, "## onCreate(): unable to retrieve Room object");
                getActivity().finish();
            }
        }
        addPreferencesFromResource(C1299R.xml.vector_room_settings_preferences);
        this.mRoomPhotoAvatar = (RoomAvatarPreference) findPreference(PREF_KEY_ROOM_PHOTO_AVATAR);
        this.mRoomNameEditTxt = (EditTextPreference) findPreference(PREF_KEY_ROOM_NAME);
        this.mRoomTopicEditTxt = (EditTextPreference) findPreference(PREF_KEY_ROOM_TOPIC);
        this.mRoomDirectoryVisibilitySwitch = (CheckBoxPreference) findPreference(PREF_KEY_ROOM_DIRECTORY_VISIBILITY_SWITCH);
        this.mRoomTagListPreference = (ListPreference) findPreference(PREF_KEY_ROOM_TAG_LIST);
        this.mRoomAccessRulesListPreference = (VectorListPreference) findPreference(PREF_KEY_ROOM_ACCESS_RULES_LIST);
        this.mRoomHistoryReadabilityRulesListPreference = (ListPreference) findPreference(PREF_KEY_ROOM_HISTORY_READABILITY_LIST);
        this.mAddressesSettingsCategory = (PreferenceCategory) getPreferenceManager().findPreference(PREF_KEY_ADDRESSES);
        this.mAdvandceSettingsCategory = (PreferenceCategory) getPreferenceManager().findPreference(PREF_KEY_ADVANCED);
        this.mBannedMembersSettingsCategory = (PreferenceCategory) getPreferenceManager().findPreference(PREF_KEY_BANNED);
        this.mBannedMembersSettingsCategoryDivider = (PreferenceCategory) getPreferenceManager().findPreference(PREF_KEY_BANNED_DIVIDER);
        this.mFlairSettingsCategory = (PreferenceCategory) getPreferenceManager().findPreference(PREF_KEY_FLAIR);
        this.mRoomNotificationsPreference = (ListPreference) getPreferenceManager().findPreference(PREF_KEY_ROOM_NOTIFICATIONS_LIST);
        this.mRoomAccessRulesListPreference.setOnPreferenceWarningIconClickListener(new OnPreferenceWarningIconClickListener() {
            public void onWarningIconClick(Preference preference) {
                VectorRoomSettingsFragment.this.displayAccessRoomWarning();
            }
        });
        Preference findPreference = findPreference(PREF_KEY_ROOM_INTERNAL_ID);
        if (findPreference != null) {
            findPreference.setSummary(this.mRoom.getRoomId());
            findPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    VectorUtils.copyToClipboard(VectorRoomSettingsFragment.this.getActivity(), VectorRoomSettingsFragment.this.mRoom.getRoomId());
                    return false;
                }
            });
        }
        Preference findPreference2 = findPreference(PREF_KEY_ROOM_LEAVE);
        if (findPreference2 != null) {
            findPreference2.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new Builder(VectorRoomSettingsFragment.this.getActivity()).setTitle(C1299R.string.room_participants_leave_prompt_title).setMessage(VectorRoomSettingsFragment.this.getString(C1299R.string.room_participants_leave_prompt_msg)).setPositiveButton(C1299R.string.leave, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            VectorRoomSettingsFragment.this.displayLoadingView();
                            VectorRoomSettingsFragment.this.mRoom.leave(new ApiCallback<Void>() {
                                public void onSuccess(Void voidR) {
                                    if (VectorRoomSettingsFragment.this.getActivity() != null) {
                                        VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                VectorRoomSettingsFragment.this.getActivity().finish();
                                            }
                                        });
                                    }
                                }

                                private void onError(final String str) {
                                    if (VectorRoomSettingsFragment.this.getActivity() != null) {
                                        VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                VectorRoomSettingsFragment.this.hideLoadingView(true);
                                                Toast.makeText(VectorRoomSettingsFragment.this.getActivity(), str, 0).show();
                                            }
                                        });
                                    }
                                }

                                public void onNetworkError(Exception exc) {
                                    onError(exc.getLocalizedMessage());
                                }

                                public void onMatrixError(MatrixError matrixError) {
                                    onError(matrixError.getLocalizedMessage());
                                }

                                public void onUnexpectedError(Exception exc) {
                                    onError(exc.getLocalizedMessage());
                                }
                            });
                        }
                    }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                    return true;
                }
            });
        }
        this.mRoomPhotoAvatar.setConfiguration(this.mSession, this.mRoom);
        this.mRoomPhotoAvatar.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                if (VectorRoomSettingsFragment.this.mRoomPhotoAvatar == null || !VectorRoomSettingsFragment.this.mRoomPhotoAvatar.isEnabled()) {
                    return false;
                }
                VectorRoomSettingsFragment.this.onRoomAvatarPreferenceChanged();
                return true;
            }
        });
        enableSharedPreferenceListener(true);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        View findViewById = onCreateView.findViewById(16908298);
        if (findViewById != null) {
            findViewById.setPadding(0, 0, 0, 0);
        }
        onCreateView.setBackgroundColor(ThemeUtils.INSTANCE.getColor(getActivity(), C1299R.attr.riot_primary_background_color));
        return onCreateView;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.mParentLoadingView == null) {
            for (View view2 = getView(); view2 != null && this.mParentLoadingView == null; view2 = (View) view2.getParent()) {
                this.mParentLoadingView = view2.findViewById(C1299R.C1301id.settings_loading_layout);
            }
        }
        if (this.mParentFragmentContainerView == null) {
            for (View view3 = getView(); view3 != null && this.mParentFragmentContainerView == null; view3 = (View) view3.getParent()) {
                this.mParentFragmentContainerView = view3.findViewById(C1299R.C1301id.room_details_fragment_container);
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.mRoom != null) {
            Matrix.getInstance(getActivity()).removeNetworkEventListener(this.mNetworkListener);
            this.mRoom.removeEventListener(this.mEventListener);
        }
        enableSharedPreferenceListener(false);
    }

    public void onResume() {
        super.onResume();
        if (this.mRoom != null) {
            Matrix.getInstance(getActivity()).addNetworkEventListener(this.mNetworkListener);
            this.mRoom.addEventListener(this.mEventListener);
            updateUi();
            updateRoomDirectoryVisibilityAsync();
            refreshAddresses();
            refreshFlair();
            refreshBannedMembersList();
            refreshEndToEnd();
        }
    }

    /* access modifiers changed from: private */
    public void enableSharedPreferenceListener(boolean z) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## enableSharedPreferenceListener(): aIsListenerEnabled=");
        sb.append(z);
        Log.m209d(str, sb.toString());
        this.mIsUiUpdateSkipped = !z;
        try {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (z) {
                defaultSharedPreferences.registerOnSharedPreferenceChangeListener(this);
            } else {
                defaultSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## enableSharedPreferenceListener(): Exception Msg=");
            sb2.append(e.getMessage());
            Log.m211e(str2, sb2.toString());
        }
    }

    /* access modifiers changed from: private */
    public void updateUi() {
        updatePreferenceAccessFromPowerLevel();
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                VectorRoomSettingsFragment.this.enableSharedPreferenceListener(false);
            }
        });
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                VectorRoomSettingsFragment.this.updatePreferenceUiValues();
                VectorRoomSettingsFragment.this.enableSharedPreferenceListener(true);
            }
        });
    }

    private void updateUiOnUiThread() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                VectorRoomSettingsFragment.this.updateUi();
            }
        });
    }

    private void updateRoomDirectoryVisibilityAsync() {
        if (this.mRoom == null || this.mRoomDirectoryVisibilitySwitch == null) {
            Log.m217w(LOG_TAG, "## updateRoomDirectoryVisibilityUi(): not processed due to invalid parameters");
            return;
        }
        displayLoadingView();
        this.mRoom.getDirectoryVisibility(this.mRoom.getRoomId(), new ApiCallback<String>() {
            private void handleResponseOnUiThread(final String str) {
                VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        VectorRoomSettingsFragment.this.hideLoadingView(false);
                        boolean equals = "public".equals(str);
                        VectorRoomSettingsFragment.this.enableSharedPreferenceListener(false);
                        VectorRoomSettingsFragment.this.mRoomDirectoryVisibilitySwitch.setChecked(equals);
                        VectorRoomSettingsFragment.this.enableSharedPreferenceListener(true);
                    }
                });
            }

            public void onSuccess(String str) {
                handleResponseOnUiThread(str);
            }

            public void onNetworkError(Exception exc) {
                String access$200 = VectorRoomSettingsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getDirectoryVisibility(): onNetworkError Msg=");
                sb.append(exc.getLocalizedMessage());
                Log.m217w(access$200, sb.toString());
                handleResponseOnUiThread(null);
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$200 = VectorRoomSettingsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getDirectoryVisibility(): onMatrixError Msg=");
                sb.append(matrixError.getLocalizedMessage());
                Log.m217w(access$200, sb.toString());
                handleResponseOnUiThread(null);
            }

            public void onUnexpectedError(Exception exc) {
                String access$200 = VectorRoomSettingsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getDirectoryVisibility(): onUnexpectedError Msg=");
                sb.append(exc.getLocalizedMessage());
                Log.m217w(access$200, sb.toString());
                handleResponseOnUiThread(null);
            }
        });
    }

    /* access modifiers changed from: private */
    public void displayAccessRoomWarning() {
        Toast.makeText(getActivity(), C1299R.string.room_settings_room_access_warning, 0).show();
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:84:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updatePreferenceAccessFromPowerLevel() {
        /*
            r8 = this;
            android.app.Activity r0 = r8.getActivity()
            com.opengarden.firechat.Matrix r0 = com.opengarden.firechat.Matrix.getInstance(r0)
            boolean r0 = r0.isConnected()
            com.opengarden.firechat.matrixsdk.data.Room r1 = r8.mRoom
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0058
            com.opengarden.firechat.matrixsdk.MXSession r1 = r8.mSession
            if (r1 == 0) goto L_0x0058
            com.opengarden.firechat.matrixsdk.data.Room r1 = r8.mRoom
            com.opengarden.firechat.matrixsdk.data.RoomState r1 = r1.getLiveState()
            com.opengarden.firechat.matrixsdk.rest.model.PowerLevels r1 = r1.getPowerLevels()
            if (r1 == 0) goto L_0x005f
            com.opengarden.firechat.matrixsdk.MXSession r4 = r8.mSession
            java.lang.String r4 = r4.getMyUserId()
            int r4 = r1.getUserPowerLevel(r4)
            java.lang.String r5 = "m.room.avatar"
            int r5 = r1.minimumPowerLevelForSendingEventAsStateEvent(r5)
            if (r4 < r5) goto L_0x0036
            r5 = 1
            goto L_0x0037
        L_0x0036:
            r5 = 0
        L_0x0037:
            java.lang.String r6 = "m.room.name"
            int r6 = r1.minimumPowerLevelForSendingEventAsStateEvent(r6)
            if (r4 < r6) goto L_0x0041
            r6 = 1
            goto L_0x0042
        L_0x0041:
            r6 = 0
        L_0x0042:
            java.lang.String r7 = "m.room.topic"
            int r1 = r1.minimumPowerLevelForSendingEventAsStateEvent(r7)
            if (r4 < r1) goto L_0x004c
            r1 = 1
            goto L_0x004d
        L_0x004c:
            r1 = 0
        L_0x004d:
            float r4 = (float) r4
            r7 = 1120403456(0x42c80000, float:100.0)
            int r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r4 < 0) goto L_0x0056
            r4 = 1
            goto L_0x0063
        L_0x0056:
            r4 = 0
            goto L_0x0063
        L_0x0058:
            java.lang.String r1 = LOG_TAG
            java.lang.String r4 = "## updatePreferenceAccessFromPowerLevel(): session or room may be missing"
            com.opengarden.firechat.matrixsdk.util.Log.m217w(r1, r4)
        L_0x005f:
            r1 = 0
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x0063:
            com.opengarden.firechat.preference.RoomAvatarPreference r7 = r8.mRoomPhotoAvatar
            if (r7 == 0) goto L_0x0073
            com.opengarden.firechat.preference.RoomAvatarPreference r7 = r8.mRoomPhotoAvatar
            if (r5 == 0) goto L_0x006f
            if (r0 == 0) goto L_0x006f
            r5 = 1
            goto L_0x0070
        L_0x006f:
            r5 = 0
        L_0x0070:
            r7.setEnabled(r5)
        L_0x0073:
            android.preference.EditTextPreference r5 = r8.mRoomNameEditTxt
            if (r5 == 0) goto L_0x0083
            android.preference.EditTextPreference r5 = r8.mRoomNameEditTxt
            if (r6 == 0) goto L_0x007f
            if (r0 == 0) goto L_0x007f
            r6 = 1
            goto L_0x0080
        L_0x007f:
            r6 = 0
        L_0x0080:
            r5.setEnabled(r6)
        L_0x0083:
            android.preference.EditTextPreference r5 = r8.mRoomTopicEditTxt
            if (r5 == 0) goto L_0x0093
            android.preference.EditTextPreference r5 = r8.mRoomTopicEditTxt
            if (r1 == 0) goto L_0x008f
            if (r0 == 0) goto L_0x008f
            r1 = 1
            goto L_0x0090
        L_0x008f:
            r1 = 0
        L_0x0090:
            r5.setEnabled(r1)
        L_0x0093:
            android.preference.CheckBoxPreference r1 = r8.mRoomDirectoryVisibilitySwitch
            if (r1 == 0) goto L_0x00a3
            android.preference.CheckBoxPreference r1 = r8.mRoomDirectoryVisibilitySwitch
            if (r4 == 0) goto L_0x009f
            if (r0 == 0) goto L_0x009f
            r5 = 1
            goto L_0x00a0
        L_0x009f:
            r5 = 0
        L_0x00a0:
            r1.setEnabled(r5)
        L_0x00a3:
            android.preference.ListPreference r1 = r8.mRoomTagListPreference
            if (r1 == 0) goto L_0x00ac
            android.preference.ListPreference r1 = r8.mRoomTagListPreference
            r1.setEnabled(r0)
        L_0x00ac:
            com.opengarden.firechat.preference.VectorListPreference r1 = r8.mRoomAccessRulesListPreference
            if (r1 == 0) goto L_0x00e0
            com.opengarden.firechat.preference.VectorListPreference r1 = r8.mRoomAccessRulesListPreference
            if (r4 == 0) goto L_0x00b8
            if (r0 == 0) goto L_0x00b8
            r5 = 1
            goto L_0x00b9
        L_0x00b8:
            r5 = 0
        L_0x00b9:
            r1.setEnabled(r5)
            com.opengarden.firechat.preference.VectorListPreference r1 = r8.mRoomAccessRulesListPreference
            com.opengarden.firechat.matrixsdk.data.Room r5 = r8.mRoom
            java.util.List r5 = r5.getAliases()
            int r5 = r5.size()
            if (r5 != 0) goto L_0x00dc
            java.lang.String r5 = "invite"
            com.opengarden.firechat.matrixsdk.data.Room r6 = r8.mRoom
            com.opengarden.firechat.matrixsdk.data.RoomState r6 = r6.getLiveState()
            java.lang.String r6 = r6.join_rule
            boolean r5 = android.text.TextUtils.equals(r5, r6)
            if (r5 != 0) goto L_0x00dc
            r5 = 1
            goto L_0x00dd
        L_0x00dc:
            r5 = 0
        L_0x00dd:
            r1.setWarningIconVisible(r5)
        L_0x00e0:
            android.preference.ListPreference r1 = r8.mRoomHistoryReadabilityRulesListPreference
            if (r1 == 0) goto L_0x00ef
            android.preference.ListPreference r1 = r8.mRoomHistoryReadabilityRulesListPreference
            if (r4 == 0) goto L_0x00eb
            if (r0 == 0) goto L_0x00eb
            goto L_0x00ec
        L_0x00eb:
            r2 = 0
        L_0x00ec:
            r1.setEnabled(r2)
        L_0x00ef:
            android.preference.ListPreference r1 = r8.mRoomNotificationsPreference
            if (r1 == 0) goto L_0x00f8
            android.preference.ListPreference r1 = r8.mRoomNotificationsPreference
            r1.setEnabled(r0)
        L_0x00f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.fragments.VectorRoomSettingsFragment.updatePreferenceAccessFromPowerLevel():void");
    }

    /* access modifiers changed from: private */
    public void updatePreferenceUiValues() {
        String str;
        String str2;
        String str3;
        CharSequence charSequence;
        String str4;
        if (this.mSession == null || this.mRoom == null) {
            Log.m217w(LOG_TAG, "## updatePreferenceUiValues(): session or room may be missing");
            return;
        }
        if (this.mRoomPhotoAvatar != null) {
            this.mRoomPhotoAvatar.refreshAvatar();
        }
        if (this.mRoomNameEditTxt != null) {
            String str5 = this.mRoom.getLiveState().name;
            this.mRoomNameEditTxt.setSummary(str5);
            this.mRoomNameEditTxt.setText(str5);
        }
        if (this.mRoomTopicEditTxt != null) {
            String topic = this.mRoom.getTopic();
            this.mRoomTopicEditTxt.setSummary(topic);
            this.mRoomTopicEditTxt.setText(topic);
        }
        if (!isAdded()) {
            Log.m211e(LOG_TAG, "## updatePreferenceUiValues(): fragment not added to Activity - isAdded()=false");
            return;
        }
        try {
            Resources resources = getResources();
            String str6 = null;
            if (!(this.mRoomAccessRulesListPreference == null || resources == null)) {
                String str7 = this.mRoom.getLiveState().join_rule;
                String guestAccess = this.mRoom.getLiveState().getGuestAccess();
                if ("invite".equals(str7)) {
                    str4 = ACCESS_RULES_ONLY_PEOPLE_INVITED;
                    charSequence = resources.getString(C1299R.string.room_settings_room_access_entry_only_invited);
                } else if ("public".equals(str7) && RoomState.GUEST_ACCESS_FORBIDDEN.equals(guestAccess)) {
                    str4 = ACCESS_RULES_ANYONE_WITH_LINK_APART_GUEST;
                    charSequence = resources.getString(C1299R.string.room_settings_room_access_entry_anyone_with_link_apart_guest);
                } else if (!"public".equals(str7) || !RoomState.GUEST_ACCESS_CAN_JOIN.equals(guestAccess)) {
                    String str8 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## updatePreferenceUiValues(): unknown room access configuration joinRule=");
                    sb.append(str7);
                    sb.append(" and guestAccessRule=");
                    sb.append(guestAccess);
                    Log.m217w(str8, sb.toString());
                    str4 = null;
                    charSequence = null;
                } else {
                    str4 = ACCESS_RULES_ANYONE_WITH_LINK_INCLUDING_GUEST;
                    charSequence = resources.getString(C1299R.string.room_settings_room_access_entry_anyone_with_link_including_guest);
                }
                if (str4 != null) {
                    this.mRoomAccessRulesListPreference.setValue(str4);
                    this.mRoomAccessRulesListPreference.setSummary(charSequence);
                } else {
                    this.mRoomHistoryReadabilityRulesListPreference.setValue(UNKNOWN_VALUE);
                    this.mRoomHistoryReadabilityRulesListPreference.setSummary("");
                }
            }
            if (this.mRoomNotificationsPreference != null) {
                RoomNotificationState roomNotificationState = this.mSession.getDataHandler().getBingRulesManager().getRoomNotificationState(this.mRoom.getRoomId());
                if (roomNotificationState == RoomNotificationState.ALL_MESSAGES_NOISY) {
                    str3 = getString(C1299R.string.room_settings_all_messages_noisy);
                } else if (roomNotificationState == RoomNotificationState.ALL_MESSAGES) {
                    str3 = getString(C1299R.string.room_settings_all_messages);
                } else if (roomNotificationState == RoomNotificationState.MENTIONS_ONLY) {
                    str3 = getString(C1299R.string.room_settings_mention_only);
                } else {
                    str3 = getString(C1299R.string.room_settings_mute);
                }
                this.mRoomNotificationsPreference.setValue(str3);
                this.mRoomNotificationsPreference.setSummary(str3);
            }
            if (!(this.mRoomTagListPreference == null || this.mRoom.getAccountData() == null || resources == null)) {
                if (this.mRoom.getAccountData().roomTag(RoomTag.ROOM_TAG_FAVOURITE) != null) {
                    str2 = resources.getString(C1299R.string.room_settings_tag_pref_entry_value_favourite);
                    str = resources.getString(C1299R.string.room_settings_tag_pref_entry_favourite);
                } else if (this.mRoom.getAccountData().roomTag(RoomTag.ROOM_TAG_LOW_PRIORITY) != null) {
                    str2 = resources.getString(C1299R.string.room_settings_tag_pref_entry_value_low_priority);
                    str = resources.getString(C1299R.string.room_settings_tag_pref_entry_low_priority);
                } else {
                    str2 = resources.getString(C1299R.string.room_settings_tag_pref_entry_value_none);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("<i>");
                    sb2.append(getResources().getString(C1299R.string.room_settings_tag_pref_no_tag));
                    sb2.append("</i>");
                    str = Html.fromHtml(sb2.toString()).toString();
                }
                this.mRoomTagListPreference.setValue(str2);
                this.mRoomTagListPreference.setSummary(str);
            }
            if (this.mRoomHistoryReadabilityRulesListPreference != null) {
                String historyVisibility = this.mRoom.getLiveState().getHistoryVisibility();
                if (!(historyVisibility == null || resources == null)) {
                    if (historyVisibility.equals(resources.getString(C1299R.string.room_settings_read_history_entry_value_anyone))) {
                        str6 = resources.getString(C1299R.string.room_settings_read_history_entry_anyone);
                    } else if (historyVisibility.equals(resources.getString(C1299R.string.f117xd76645a3))) {
                        str6 = resources.getString(C1299R.string.room_settings_read_history_entry_members_only_option_time_shared);
                    } else if (historyVisibility.equals(resources.getString(C1299R.string.room_settings_read_history_entry_value_members_only_invited))) {
                        str6 = resources.getString(C1299R.string.room_settings_read_history_entry_members_only_invited);
                    } else if (historyVisibility.equals(resources.getString(C1299R.string.room_settings_read_history_entry_value_members_only_joined))) {
                        str6 = resources.getString(C1299R.string.room_settings_read_history_entry_members_only_joined);
                    } else {
                        String str9 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## updatePreferenceUiValues(): unknown room read history value=");
                        sb3.append(historyVisibility);
                        Log.m217w(str9, sb3.toString());
                    }
                }
                if (str6 != null) {
                    this.mRoomHistoryReadabilityRulesListPreference.setValue(historyVisibility);
                    this.mRoomHistoryReadabilityRulesListPreference.setSummary(str6);
                } else {
                    this.mRoomHistoryReadabilityRulesListPreference.setValue(UNKNOWN_VALUE);
                    this.mRoomHistoryReadabilityRulesListPreference.setSummary("");
                }
            }
        } catch (Exception e) {
            String str10 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## updatePreferenceUiValues(): Exception in getResources() - Msg=");
            sb4.append(e.getLocalizedMessage());
            Log.m211e(str10, sb4.toString());
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
        if (this.mIsUiUpdateSkipped) {
            Log.m209d(LOG_TAG, "## onSharedPreferenceChanged(): Skipped");
        } else if (getActivity() == null) {
            Log.m209d(LOG_TAG, "## onSharedPreferenceChanged(): no attached to an activity");
        } else {
            if (str.equals(PREF_KEY_ROOM_PHOTO_AVATAR)) {
                onRoomAvatarPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_NAME)) {
                onRoomNamePreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_TOPIC)) {
                onRoomTopicPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_NOTIFICATIONS_LIST)) {
                onRoomNotificationsPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_DIRECTORY_VISIBILITY_SWITCH)) {
                onRoomDirectoryVisibilityPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_TAG_LIST)) {
                onRoomTagPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_ACCESS_RULES_LIST)) {
                onRoomAccessPreferenceChanged();
            } else if (str.equals(PREF_KEY_ROOM_HISTORY_READABILITY_LIST)) {
                onRoomHistoryReadabilityPreferenceChanged();
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onSharedPreferenceChanged(): unknown aKey = ");
                sb.append(str);
                Log.m217w(str2, sb.toString());
            }
        }
    }

    private void onRoomHistoryReadabilityPreferenceChanged() {
        String str;
        if (this.mRoom == null || this.mRoomHistoryReadabilityRulesListPreference == null) {
            Log.m217w(LOG_TAG, "## onRoomHistoryReadabilityPreferenceChanged(): not processed due to invalid parameters");
            return;
        }
        String str2 = this.mRoom.getLiveState().history_visibility;
        String value = this.mRoomHistoryReadabilityRulesListPreference.getValue();
        if (!TextUtils.equals(value, str2)) {
            if (value.equals(getResources().getString(C1299R.string.room_settings_read_history_entry_value_anyone))) {
                str = RoomState.HISTORY_VISIBILITY_WORLD_READABLE;
            } else if (value.equals(getResources().getString(C1299R.string.f117xd76645a3))) {
                str = RoomState.HISTORY_VISIBILITY_SHARED;
            } else if (value.equals(getResources().getString(C1299R.string.room_settings_read_history_entry_value_members_only_invited))) {
                str = RoomState.HISTORY_VISIBILITY_INVITED;
            } else if (value.equals(getResources().getString(C1299R.string.room_settings_read_history_entry_value_members_only_joined))) {
                str = RoomState.HISTORY_VISIBILITY_JOINED;
            } else {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onRoomHistoryReadabilityPreferenceChanged(): unknown value:");
                sb.append(value);
                Log.m217w(str3, sb.toString());
                str = null;
            }
            if (str != null) {
                displayLoadingView();
                this.mRoom.updateHistoryVisibility(str, this.mUpdateCallback);
            }
        }
    }

    private void onRoomTagPreferenceChanged() {
        if (this.mRoom == null || this.mRoomTagListPreference == null) {
            Log.m217w(LOG_TAG, "## onRoomTagPreferenceChanged(): not processed due to invalid parameters");
            return;
        }
        String value = this.mRoomTagListPreference.getValue();
        Double valueOf = Double.valueOf(0.0d);
        RoomAccountData accountData = this.mRoom.getAccountData();
        String str = (accountData == null || !accountData.hasTags()) ? null : (String) accountData.getKeys().iterator().next();
        boolean z = true;
        if (!value.equals(str)) {
            if (value.equals(getResources().getString(C1299R.string.room_settings_tag_pref_entry_value_favourite))) {
                value = RoomTag.ROOM_TAG_FAVOURITE;
            } else if (value.equals(getResources().getString(C1299R.string.room_settings_tag_pref_entry_value_low_priority))) {
                value = RoomTag.ROOM_TAG_LOW_PRIORITY;
            } else if (value.equals(getResources().getString(C1299R.string.room_settings_tag_pref_entry_value_none))) {
                value = null;
            } else {
                z = false;
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onRoomTagPreferenceChanged() not supported tag = ");
                sb.append(value);
                Log.m217w(str2, sb.toString());
            }
        }
        if (z) {
            displayLoadingView();
            this.mRoom.replaceTag(str, value, valueOf, this.mUpdateCallback);
        }
    }

    private void onRoomAccessPreferenceChanged() {
        String str;
        if (this.mRoom == null || this.mRoomAccessRulesListPreference == null) {
            Log.m217w(LOG_TAG, "## onRoomAccessPreferenceChanged(): not processed due to invalid parameters");
            return;
        }
        String str2 = this.mRoom.getLiveState().join_rule;
        String guestAccess = this.mRoom.getLiveState().getGuestAccess();
        String value = this.mRoomAccessRulesListPreference.getValue();
        String str3 = null;
        if (ACCESS_RULES_ONLY_PEOPLE_INVITED.equals(value)) {
            str = !"invite".equals(str2) ? "invite" : null;
            if (!RoomState.GUEST_ACCESS_CAN_JOIN.equals(guestAccess)) {
                str3 = RoomState.GUEST_ACCESS_CAN_JOIN;
            }
        } else if (ACCESS_RULES_ANYONE_WITH_LINK_APART_GUEST.equals(value)) {
            str = !"public".equals(str2) ? "public" : null;
            if (!RoomState.GUEST_ACCESS_FORBIDDEN.equals(guestAccess)) {
                str3 = RoomState.GUEST_ACCESS_FORBIDDEN;
            }
            if (this.mRoom.getAliases().size() == 0) {
                displayAccessRoomWarning();
            }
        } else if (ACCESS_RULES_ANYONE_WITH_LINK_INCLUDING_GUEST.equals(value)) {
            str = !"public".equals(str2) ? "public" : null;
            if (!RoomState.GUEST_ACCESS_CAN_JOIN.equals(guestAccess)) {
                str3 = RoomState.GUEST_ACCESS_CAN_JOIN;
            }
            if (this.mRoom.getAliases().size() == 0) {
                displayAccessRoomWarning();
            }
        } else {
            String str4 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRoomAccessPreferenceChanged(): unknown selected value = ");
            sb.append(value);
            Log.m209d(str4, sb.toString());
            str = null;
        }
        if (str != null) {
            displayLoadingView();
            this.mRoom.updateJoinRules(str, this.mUpdateCallback);
        }
        if (str3 != null) {
            displayLoadingView();
            this.mRoom.updateGuestAccess(str3, this.mUpdateCallback);
        }
    }

    private void onRoomDirectoryVisibilityPreferenceChanged() {
        String str;
        if (this.mRoom == null || this.mRoomDirectoryVisibilitySwitch == null) {
            Log.m217w(LOG_TAG, "## onRoomDirectoryVisibilityPreferenceChanged(): not processed due to invalid parameters");
            str = null;
        } else {
            str = this.mRoomDirectoryVisibilitySwitch.isChecked() ? "public" : RoomState.DIRECTORY_VISIBILITY_PRIVATE;
        }
        if (str != null) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRoomDirectoryVisibilityPreferenceChanged(): directory visibility set to ");
            sb.append(str);
            Log.m209d(str2, sb.toString());
            displayLoadingView();
            this.mRoom.updateDirectoryVisibility(str, this.mUpdateCallback);
        }
    }

    private void onRoomNotificationsPreferenceChanged() {
        RoomNotificationState roomNotificationState;
        if (this.mRoom != null && this.mBingRulesManager != null) {
            String value = this.mRoomNotificationsPreference.getValue();
            if (TextUtils.equals(value, getString(C1299R.string.room_settings_all_messages_noisy))) {
                roomNotificationState = RoomNotificationState.ALL_MESSAGES_NOISY;
            } else if (TextUtils.equals(value, getString(C1299R.string.room_settings_all_messages))) {
                roomNotificationState = RoomNotificationState.ALL_MESSAGES;
            } else if (TextUtils.equals(value, getString(C1299R.string.room_settings_mention_only))) {
                roomNotificationState = RoomNotificationState.MENTIONS_ONLY;
            } else {
                roomNotificationState = RoomNotificationState.MUTE;
            }
            if (this.mBingRulesManager.getRoomNotificationState(this.mRoom.getRoomId()) != roomNotificationState) {
                displayLoadingView();
                this.mBingRulesManager.updateRoomNotificationState(this.mRoom.getRoomId(), roomNotificationState, new onBingRuleUpdateListener() {
                    public void onBingRuleUpdateSuccess() {
                        Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "##onRoomNotificationsPreferenceChanged(): update succeed");
                        VectorRoomSettingsFragment.this.hideLoadingView(true);
                    }

                    public void onBingRuleUpdateFailure(String str) {
                        Log.m217w(VectorRoomSettingsFragment.LOG_TAG, "##onRoomNotificationsPreferenceChanged(): BingRuleUpdateFailure");
                        VectorRoomSettingsFragment.this.hideLoadingView(false);
                    }
                });
            }
        }
    }

    private void onRoomNamePreferenceChanged() {
        if (this.mRoom != null && this.mSession != null && this.mRoomNameEditTxt != null) {
            String str = this.mRoom.getLiveState().name;
            String text = this.mRoomNameEditTxt.getText();
            if (!TextUtils.equals(str, text)) {
                displayLoadingView();
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("##onRoomNamePreferenceChanged to ");
                sb.append(text);
                Log.m209d(str2, sb.toString());
                this.mRoom.updateName(text, this.mUpdateCallback);
            }
        }
    }

    private void onRoomTopicPreferenceChanged() {
        if (this.mRoom != null) {
            String topic = this.mRoom.getTopic();
            String text = this.mRoomTopicEditTxt.getText();
            if (!TextUtils.equals(topic, text)) {
                displayLoadingView();
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## update topic to ");
                sb.append(text);
                Log.m209d(str, sb.toString());
                this.mRoom.updateTopic(text, this.mUpdateCallback);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onRoomAvatarPreferenceChanged() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(VectorRoomSettingsFragment.this.getActivity(), VectorMediasPickerActivity.class);
                intent.putExtra(VectorMediasPickerActivity.EXTRA_AVATAR_MODE, true);
                VectorRoomSettingsFragment.this.startActivityForResult(intent, 16);
            }
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (16 == i) {
            onActivityResultRoomAvatarUpdate(i2, intent);
        }
    }

    private void onActivityResultRoomAvatarUpdate(int i, Intent intent) {
        if (this.mSession != null && i == -1) {
            Uri thumbnailUriFromIntent = VectorUtils.getThumbnailUriFromIntent(getActivity(), intent, this.mSession.getMediasCache());
            if (thumbnailUriFromIntent != null) {
                displayLoadingView();
                Resource openResource = ResourceUtils.openResource(getActivity(), thumbnailUriFromIntent, null);
                if (openResource != null) {
                    this.mSession.getMediasCache().uploadContent(openResource.mContentStream, null, openResource.mMimeType, null, new MXMediaUploadListener() {
                        public void onUploadError(String str, int i, String str2) {
                            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.m211e(VectorRoomSettingsFragment.LOG_TAG, "Fail to upload the avatar");
                                    VectorRoomSettingsFragment.this.hideLoadingView(false);
                                }
                            });
                        }

                        public void onUploadComplete(String str, final String str2) {
                            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Log.m209d(VectorRoomSettingsFragment.LOG_TAG, "The avatar has been uploaded, update the room avatar");
                                    VectorRoomSettingsFragment.this.mRoom.updateAvatarUrl(str2, VectorRoomSettingsFragment.this.mUpdateCallback);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void displayLoadingView() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (VectorRoomSettingsFragment.this.mParentFragmentContainerView != null) {
                        VectorRoomSettingsFragment.this.mParentFragmentContainerView.setEnabled(false);
                    }
                    if (VectorRoomSettingsFragment.this.mParentLoadingView != null) {
                        VectorRoomSettingsFragment.this.mParentLoadingView.setVisibility(0);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void hideLoadingView(boolean z) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (VectorRoomSettingsFragment.this.mParentFragmentContainerView != null) {
                    VectorRoomSettingsFragment.this.mParentFragmentContainerView.setEnabled(true);
                }
                if (VectorRoomSettingsFragment.this.mParentLoadingView != null) {
                    VectorRoomSettingsFragment.this.mParentLoadingView.setVisibility(8);
                }
            }
        });
        if (z) {
            updateUiOnUiThread();
        }
    }

    /* access modifiers changed from: private */
    public void refreshBannedMembersList() {
        ArrayList arrayList = new ArrayList();
        Collection<RoomMember> members = this.mRoom.getMembers();
        if (members != null) {
            for (RoomMember roomMember : members) {
                if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_BAN)) {
                    arrayList.add(roomMember);
                }
            }
        }
        Collections.sort(arrayList, new Comparator<RoomMember>() {
            public int compare(RoomMember roomMember, RoomMember roomMember2) {
                return roomMember.getUserId().toLowerCase(VectorApp.getApplicationLocale()).compareTo(roomMember2.getUserId().toLowerCase(VectorApp.getApplicationLocale()));
            }
        });
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removePreference(this.mBannedMembersSettingsCategoryDivider);
        preferenceScreen.removePreference(this.mBannedMembersSettingsCategory);
        this.mBannedMembersSettingsCategory.removeAll();
        if (arrayList.size() > 0) {
            preferenceScreen.addPreference(this.mBannedMembersSettingsCategoryDivider);
            preferenceScreen.addPreference(this.mBannedMembersSettingsCategory);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                RoomMember roomMember2 = (RoomMember) it.next();
                VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
                final String userId = roomMember2.getUserId();
                vectorCustomActionEditTextPreference.setTitle(userId);
                StringBuilder sb = new StringBuilder();
                sb.append(BANNED_PREFERENCE_KEY_BASE);
                sb.append(userId);
                vectorCustomActionEditTextPreference.setKey(sb.toString());
                vectorCustomActionEditTextPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(VectorRoomSettingsFragment.this.getActivity(), VectorMemberDetailsActivity.class);
                        intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, userId);
                        intent.putExtra("EXTRA_ROOM_ID", VectorRoomSettingsFragment.this.mRoom.getRoomId());
                        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomSettingsFragment.this.mSession.getCredentials().userId);
                        VectorRoomSettingsFragment.this.getActivity().startActivity(intent);
                        return false;
                    }
                });
                this.mBannedMembersSettingsCategory.addPreference(vectorCustomActionEditTextPreference);
            }
        }
    }

    private boolean canUpdateFlair() {
        PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
        if (powerLevels == null || powerLevels.getUserPowerLevel(this.mSession.getMyUserId()) < powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_RELATED_GROUPS)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void refreshFlair() {
        final List<String> relatedGroups = this.mRoom.getLiveState().getRelatedGroups();
        Collections.sort(relatedGroups, String.CASE_INSENSITIVE_ORDER);
        this.mFlairSettingsCategory.removeAll();
        if (!relatedGroups.isEmpty()) {
            for (final String str : relatedGroups) {
                VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
                vectorCustomActionEditTextPreference.setTitle(str);
                StringBuilder sb = new StringBuilder();
                sb.append(FLAIR_PREFERENCE_KEY_BASE);
                sb.append(str);
                vectorCustomActionEditTextPreference.setKey(sb.toString());
                vectorCustomActionEditTextPreference.setOnPreferenceLongClickListener(new OnPreferenceLongClickListener() {
                    public boolean onPreferenceLongClick(Preference preference) {
                        VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorRoomSettingsFragment.this.displayLoadingView();
                                VectorRoomSettingsFragment.this.mRoom.removeRelatedGroup(str, VectorRoomSettingsFragment.this.mFlairUpdatesCallback);
                            }
                        });
                        return true;
                    }
                });
                this.mFlairSettingsCategory.addPreference(vectorCustomActionEditTextPreference);
            }
        } else {
            VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference2 = new VectorCustomActionEditTextPreference(getActivity());
            vectorCustomActionEditTextPreference2.setTitle(getString(C1299R.string.room_settings_no_flair));
            vectorCustomActionEditTextPreference2.setKey("FLAIR_PREFERENCE_KEY_BASEno_flair");
            this.mFlairSettingsCategory.addPreference(vectorCustomActionEditTextPreference2);
        }
        if (canUpdateFlair()) {
            EditTextPreference editTextPreference = new EditTextPreference(getActivity());
            editTextPreference.setTitle(C1299R.string.room_settings_add_new_group);
            editTextPreference.setDialogTitle(C1299R.string.room_settings_add_new_group);
            editTextPreference.setKey("FLAIR_PREFERENCE_KEY_BASE__add");
            editTextPreference.setIcon(CommonActivityUtils.tintDrawable(getActivity(), ContextCompat.getDrawable(getActivity(), C1299R.C1300drawable.ic_add_black), C1299R.attr.settings_icon_tint_color));
            editTextPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    final String trim = ((String) obj).trim();
                    if (!TextUtils.isEmpty(trim)) {
                        if (!MXSession.isGroupId(trim)) {
                            Builder builder = new Builder(VectorRoomSettingsFragment.this.getActivity());
                            builder.setTitle(C1299R.string.room_settings_invalid_group_format_dialog_title);
                            builder.setMessage(VectorRoomSettingsFragment.this.getString(C1299R.string.room_settings_invalid_group_format_dialog_body, new Object[]{trim}));
                            builder.setPositiveButton(C1299R.string.f115ok, null);
                            builder.create().show();
                        } else if (!relatedGroups.contains(trim)) {
                            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    VectorRoomSettingsFragment.this.displayLoadingView();
                                    VectorRoomSettingsFragment.this.mRoom.addRelatedGroup(trim, VectorRoomSettingsFragment.this.mFlairUpdatesCallback);
                                }
                            });
                        }
                    }
                    return false;
                }
            });
            this.mFlairSettingsCategory.addPreference(editTextPreference);
        }
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void onAddressLongClick(final String str, View view) {
        Activity activity = getActivity();
        PopupMenu popupMenu = VERSION.SDK_INT >= 19 ? new PopupMenu(activity, view, GravityCompat.END) : new PopupMenu(activity, view);
        popupMenu.getMenuInflater().inflate(C1299R.C1302menu.vector_room_settings_addresses, popupMenu.getMenu());
        boolean z = false;
        try {
            Field[] declaredFields = popupMenu.getClass().getDeclaredFields();
            int length = declaredFields.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                Field field = declaredFields[i];
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object obj = field.get(popupMenu);
                    Class.forName(obj.getClass().getName()).getMethod("setForceShowIcon", new Class[]{Boolean.TYPE}).invoke(obj, new Object[]{Boolean.valueOf(true)});
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onMessageClick : force to display the icons failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
        Menu menu = popupMenu.getMenu();
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(activity, C1299R.attr.icon_tint_on_light_action_bar_color));
        String str3 = this.mRoom.getLiveState().alias;
        boolean canUpdateAliases = canUpdateAliases();
        menu.findItem(C1299R.C1301id.ic_action_vector_delete_alias).setVisible(canUpdateAliases);
        menu.findItem(C1299R.C1301id.ic_action_vector_set_as_main_address).setVisible(canUpdateAliases && !TextUtils.equals(str, str3));
        MenuItem findItem = menu.findItem(C1299R.C1301id.ic_action_vector_unset_main_address);
        if (canUpdateAliases && TextUtils.equals(str, str3)) {
            z = true;
        }
        findItem.setVisible(z);
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == C1299R.C1301id.ic_action_vector_unset_main_address) {
                    Builder builder = new Builder(VectorRoomSettingsFragment.this.getActivity());
                    builder.setMessage(C1299R.string.room_settings_addresses_disable_main_address_prompt_msg);
                    builder.setTitle(C1299R.string.room_settings_addresses_disable_main_address_prompt_title);
                    builder.setPositiveButton(C1299R.string.yes, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            VectorRoomSettingsFragment.this.displayLoadingView();
                            VectorRoomSettingsFragment.this.mRoom.updateCanonicalAlias(null, VectorRoomSettingsFragment.this.mAliasUpdatesCallback);
                        }
                    });
                    builder.setNegativeButton(C1299R.string.f114no, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.create().show();
                } else if (menuItem.getItemId() == C1299R.C1301id.ic_action_vector_set_as_main_address) {
                    VectorRoomSettingsFragment.this.displayLoadingView();
                    VectorRoomSettingsFragment.this.mRoom.updateCanonicalAlias(str, VectorRoomSettingsFragment.this.mAliasUpdatesCallback);
                } else if (menuItem.getItemId() == C1299R.C1301id.ic_action_vector_delete_alias) {
                    VectorRoomSettingsFragment.this.displayLoadingView();
                    VectorRoomSettingsFragment.this.mRoom.removeAlias(str, new ApiCallback<Void>() {
                        public void onSuccess(Void voidR) {
                            if (VectorRoomSettingsFragment.this.mRoom.getAliases().size() == 1) {
                                VectorRoomSettingsFragment.this.mRoom.updateCanonicalAlias((String) VectorRoomSettingsFragment.this.mRoom.getAliases().get(0), VectorRoomSettingsFragment.this.mAliasUpdatesCallback);
                            } else {
                                VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onSuccess(voidR);
                            }
                        }

                        public void onNetworkError(Exception exc) {
                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onNetworkError(exc);
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onMatrixError(matrixError);
                        }

                        public void onUnexpectedError(Exception exc) {
                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onUnexpectedError(exc);
                        }
                    });
                } else if (menuItem.getItemId() == C1299R.C1301id.ic_action_vector_room_url) {
                    VectorUtils.copyToClipboard(VectorRoomSettingsFragment.this.getActivity(), VectorUtils.getPermalink(str, null));
                } else {
                    VectorUtils.copyToClipboard(VectorRoomSettingsFragment.this.getActivity(), str);
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private boolean canUpdateAliases() {
        PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
        if (powerLevels == null || powerLevels.getUserPowerLevel(this.mSession.getMyUserId()) < powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_STATE_ROOM_ALIASES)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void refreshAddresses() {
        StringBuilder sb = new StringBuilder();
        sb.append(":");
        sb.append(this.mSession.getHomeServerConfig().getHomeserverUri().getHost());
        String sb2 = sb.toString();
        String str = this.mRoom.getLiveState().alias;
        final ArrayList arrayList = new ArrayList(this.mRoom.getAliases());
        this.mAddressesSettingsCategory.removeAll();
        if (arrayList.size() == 0) {
            AddressPreference addressPreference = new AddressPreference(getActivity());
            addressPreference.setTitle(getString(C1299R.string.room_settings_addresses_no_local_addresses));
            addressPreference.setKey(NO_LOCAL_ADDRESS_PREFERENCE_KEY);
            this.mAddressesSettingsCategory.addPreference(addressPreference);
        } else {
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (str2.endsWith(sb2)) {
                    arrayList2.add(str2);
                } else {
                    arrayList3.add(str2);
                }
            }
            arrayList.clear();
            arrayList.addAll(arrayList2);
            arrayList.addAll(arrayList3);
            int i = 0;
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                final String str3 = (String) it2.next();
                final AddressPreference addressPreference2 = new AddressPreference(getActivity());
                addressPreference2.setTitle(str3);
                StringBuilder sb3 = new StringBuilder();
                sb3.append(ADDRESSES_PREFERENCE_KEY_BASE);
                sb3.append(i);
                addressPreference2.setKey(sb3.toString());
                addressPreference2.setMainIconVisible(TextUtils.equals(str3, str));
                addressPreference2.setOnPreferenceLongClickListener(new OnPreferenceLongClickListener() {
                    public boolean onPreferenceLongClick(Preference preference) {
                        VectorRoomSettingsFragment.this.onAddressLongClick(str3, addressPreference2.getMainIconView());
                        return true;
                    }
                });
                this.mAddressesSettingsCategory.addPreference(addressPreference2);
                i++;
            }
        }
        if (canUpdateAliases()) {
            EditTextPreference editTextPreference = new EditTextPreference(getActivity());
            editTextPreference.setTitle(C1299R.string.room_settings_addresses_add_new_address);
            editTextPreference.setDialogTitle(C1299R.string.room_settings_addresses_add_new_address);
            editTextPreference.setKey(ADD_ADDRESSES_PREFERENCE_KEY);
            editTextPreference.setIcon(CommonActivityUtils.tintDrawable(getActivity(), ContextCompat.getDrawable(getActivity(), C1299R.C1300drawable.ic_add_black), C1299R.attr.settings_icon_tint_color));
            editTextPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    final String trim = ((String) obj).trim();
                    if (!TextUtils.isEmpty(trim)) {
                        if (!MXSession.isRoomAlias(trim)) {
                            Builder builder = new Builder(VectorRoomSettingsFragment.this.getActivity());
                            builder.setTitle(C1299R.string.room_settings_addresses_invalid_format_dialog_title);
                            builder.setMessage(VectorRoomSettingsFragment.this.getString(C1299R.string.room_settings_addresses_invalid_format_dialog_body, new Object[]{trim}));
                            builder.setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            builder.create().show();
                        } else if (arrayList.indexOf(trim) < 0) {
                            VectorRoomSettingsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    VectorRoomSettingsFragment.this.displayLoadingView();
                                    VectorRoomSettingsFragment.this.mRoom.addAlias(trim, new ApiCallback<Void>() {
                                        public void onSuccess(Void voidR) {
                                            if (VectorRoomSettingsFragment.this.mRoom.getAliases().size() == 1) {
                                                VectorRoomSettingsFragment.this.mRoom.updateCanonicalAlias((String) VectorRoomSettingsFragment.this.mRoom.getAliases().get(0), VectorRoomSettingsFragment.this.mAliasUpdatesCallback);
                                            } else {
                                                VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onSuccess(voidR);
                                            }
                                        }

                                        public void onNetworkError(Exception exc) {
                                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onNetworkError(exc);
                                        }

                                        public void onMatrixError(MatrixError matrixError) {
                                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onMatrixError(matrixError);
                                        }

                                        public void onUnexpectedError(Exception exc) {
                                            VectorRoomSettingsFragment.this.mAliasUpdatesCallback.onUnexpectedError(exc);
                                        }
                                    });
                                }
                            });
                        }
                    }
                    return false;
                }
            });
            this.mAddressesSettingsCategory.addPreference(editTextPreference);
        }
    }

    /* access modifiers changed from: private */
    public void refreshEndToEnd() {
        final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(getString(C1299R.string.room_settings_never_send_to_unverified_devices_title));
        if (checkBoxPreference == null) {
            Log.m211e(LOG_TAG, "## refreshEndToEnd() : sendToUnverifiedDevicesPref is null");
            return;
        }
        int i = 0;
        if (this.mSession.getCrypto() == null) {
            this.mAdvandceSettingsCategory.removePreference(checkBoxPreference);
        } else if (checkBoxPreference != null) {
            if (this.mRoom.isEncrypted()) {
                checkBoxPreference.setChecked(false);
                this.mSession.getCrypto().getGlobalBlacklistUnverifiedDevices(new SimpleApiCallback<Boolean>() {
                    public void onSuccess(final Boolean bool) {
                        VectorRoomSettingsFragment.this.mSession.getCrypto().isRoomBlacklistUnverifiedDevices(VectorRoomSettingsFragment.this.mRoom.getRoomId(), new SimpleApiCallback<Boolean>() {
                            public void onSuccess(Boolean bool) {
                                checkBoxPreference.setChecked(bool.booleanValue() || bool.booleanValue());
                                checkBoxPreference.setEnabled(!bool.booleanValue());
                            }
                        });
                    }
                });
            } else if (checkBoxPreference != null) {
                this.mAdvandceSettingsCategory.removePreference(checkBoxPreference);
            }
            checkBoxPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    VectorRoomSettingsFragment.this.mSession.getCrypto().isRoomBlacklistUnverifiedDevices(VectorRoomSettingsFragment.this.mRoom.getRoomId(), new SimpleApiCallback<Boolean>() {
                        public void onSuccess(Boolean bool) {
                            if (checkBoxPreference.isChecked() != bool.booleanValue()) {
                                C22141 r3 = new SimpleApiCallback<Void>() {
                                    public void onSuccess(Void voidR) {
                                    }
                                };
                                if (checkBoxPreference.isChecked()) {
                                    VectorRoomSettingsFragment.this.mSession.getCrypto().setRoomBlacklistUnverifiedDevices(VectorRoomSettingsFragment.this.mRoom.getRoomId(), r3);
                                } else {
                                    VectorRoomSettingsFragment.this.mSession.getCrypto().setRoomUnblacklistUnverifiedDevices(VectorRoomSettingsFragment.this.mRoom.getRoomId(), r3);
                                }
                            }
                        }
                    });
                    return true;
                }
            });
        }
        StringBuilder sb = new StringBuilder();
        sb.append(PREF_KEY_ENCRYPTION);
        sb.append(this.mRoom.getRoomId());
        String sb2 = sb.toString();
        Preference findPreference = this.mAdvandceSettingsCategory.findPreference(sb2);
        if (findPreference != null) {
            this.mAdvandceSettingsCategory.removePreference(findPreference);
        }
        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().remove(sb2).apply();
        if (this.mRoom.isEncrypted()) {
            VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
            vectorCustomActionEditTextPreference.setTitle(C1299R.string.room_settings_addresses_e2e_enabled);
            vectorCustomActionEditTextPreference.setKey(sb2);
            vectorCustomActionEditTextPreference.setIcon(getResources().getDrawable(C1299R.C1300drawable.e2e_verified));
            this.mAdvandceSettingsCategory.addPreference(vectorCustomActionEditTextPreference);
        } else {
            PowerLevels powerLevels = this.mRoom.getLiveState().getPowerLevels();
            int i2 = -1;
            if (powerLevels != null) {
                i2 = powerLevels.getUserPowerLevel(this.mSession.getMyUserId());
                i = powerLevels.minimumPowerLevelForSendingEventAsStateEvent(Event.EVENT_TYPE_MESSAGE_ENCRYPTION);
            }
            if (i2 < i) {
                VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference2 = new VectorCustomActionEditTextPreference(getActivity());
                vectorCustomActionEditTextPreference2.setTitle(C1299R.string.room_settings_addresses_e2e_disabled);
                vectorCustomActionEditTextPreference2.setKey(sb2);
                vectorCustomActionEditTextPreference2.setIcon(CommonActivityUtils.tintDrawable(getActivity(), getResources().getDrawable(C1299R.C1300drawable.e2e_unencrypted), C1299R.attr.settings_icon_tint_color));
                this.mAdvandceSettingsCategory.addPreference(vectorCustomActionEditTextPreference2);
            } else if (this.mSession.isCryptoEnabled()) {
                VectorSwitchPreference vectorSwitchPreference = new VectorSwitchPreference(getActivity());
                vectorSwitchPreference.setTitle(C1299R.string.room_settings_addresses_e2e_encryption_warning);
                vectorSwitchPreference.setKey(sb2);
                vectorSwitchPreference.setIcon(CommonActivityUtils.tintDrawable(getActivity(), getResources().getDrawable(C1299R.C1300drawable.e2e_unencrypted), C1299R.attr.settings_icon_tint_color));
                vectorSwitchPreference.setChecked(true);
                this.mAdvandceSettingsCategory.addPreference(vectorSwitchPreference);
                vectorSwitchPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference preference, Object obj) {
                        if (((Boolean) obj).booleanValue() != VectorRoomSettingsFragment.this.mRoom.isEncrypted()) {
                            new Builder(VectorRoomSettingsFragment.this.getActivity()).setTitle(C1299R.string.room_settings_addresses_e2e_prompt_title).setMessage(C1299R.string.room_settings_addresses_e2e_prompt_message).setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    VectorRoomSettingsFragment.this.refreshEndToEnd();
                                    dialogInterface.dismiss();
                                    VectorRoomSettingsFragment.this.displayLoadingView();
                                }
                            }).create().show();
                        }
                        return true;
                    }
                });
            }
        }
    }
}
