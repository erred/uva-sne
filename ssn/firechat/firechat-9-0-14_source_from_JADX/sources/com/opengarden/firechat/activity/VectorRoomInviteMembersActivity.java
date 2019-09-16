package com.opengarden.firechat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.p003v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.adapters.VectorParticipantsAdapter;
import com.opengarden.firechat.adapters.VectorParticipantsAdapter.OnParticipantsSearchListener;
import com.opengarden.firechat.contacts.Contact;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.contacts.ContactsManager.ContactsManagerListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.VectorAutoCompleteTextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VectorRoomInviteMembersActivity extends VectorBaseSearchActivity {
    public static final String EXTRA_ADD_CONFIRMATION_DIALOG = "VectorInviteMembersActivity.EXTRA_ADD_CONFIRMATION_DIALOG";
    public static final String EXTRA_HIDDEN_PARTICIPANT_ITEMS = "VectorInviteMembersActivity.EXTRA_HIDDEN_PARTICIPANT_ITEMS";
    public static final String EXTRA_OUT_SELECTED_PARTICIPANT_ITEMS = "VectorInviteMembersActivity.EXTRA_OUT_SELECTED_PARTICIPANT_ITEMS";
    public static final String EXTRA_OUT_SELECTED_USER_IDS = "VectorInviteMembersActivity.EXTRA_OUT_SELECTED_USER_IDS";
    public static final String EXTRA_ROOM_ID = "VectorInviteMembersActivity.EXTRA_ROOM_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomInviteMembersActivity";
    /* access modifiers changed from: private */
    public VectorParticipantsAdapter mAdapter;
    private boolean mAddConfirmationDialog;
    private final ContactsManagerListener mContactsListener = new ContactsManagerListener() {
        public void onContactPresenceUpdate(Contact contact, String str) {
        }

        public void onRefresh() {
            VectorRoomInviteMembersActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomInviteMembersActivity.this.onPatternUpdate(false);
                }
            });
        }

        public void onPIDsUpdate() {
            VectorRoomInviteMembersActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    VectorRoomInviteMembersActivity.this.mAdapter.onPIdsUpdate();
                }
            });
        }
    };
    private final MXEventListener mEventsListener = new MXEventListener() {
        public void onPresenceUpdate(Event event, final User user) {
            VectorRoomInviteMembersActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    HashMap visibleChildViews = VectorUtils.getVisibleChildViews(VectorRoomInviteMembersActivity.this.mListView, VectorRoomInviteMembersActivity.this.mAdapter);
                    for (Integer num : visibleChildViews.keySet()) {
                        Iterator it = ((List) visibleChildViews.get(num)).iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            Object child = VectorRoomInviteMembersActivity.this.mAdapter.getChild(num.intValue(), ((Integer) it.next()).intValue());
                            if (child instanceof ParticipantAdapterItem) {
                                if (TextUtils.equals(user.user_id, ((ParticipantAdapterItem) child).mUserId)) {
                                    VectorRoomInviteMembersActivity.this.mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
                }
            });
        }
    };
    private List<ParticipantAdapterItem> mHiddenParticipantItems = new ArrayList();
    /* access modifiers changed from: private */
    public ExpandableListView mListView;
    private String mMatrixId;

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_invite_members;
    }

    public void initUiAndData() {
        super.initUiAndData();
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(LOG_TAG, "Restart the application.");
            CommonActivityUtils.restartApp(this);
        } else if (CommonActivityUtils.isGoingToSplash(this)) {
            Log.m209d(LOG_TAG, "onCreate : Going to splash screen");
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra("MXCActionBarActivity.EXTRA_MATRIX_ID")) {
                this.mMatrixId = intent.getStringExtra("MXCActionBarActivity.EXTRA_MATRIX_ID");
            }
            this.mSession = Matrix.getInstance(getApplicationContext()).getSession(this.mMatrixId);
            if (this.mSession == null || !this.mSession.isAlive()) {
                finish();
                return;
            }
            if (intent.hasExtra(EXTRA_HIDDEN_PARTICIPANT_ITEMS)) {
                this.mHiddenParticipantItems = (List) intent.getSerializableExtra(EXTRA_HIDDEN_PARTICIPANT_ITEMS);
            }
            String stringExtra = intent.getStringExtra(EXTRA_ROOM_ID);
            if (stringExtra != null) {
                this.mRoom = this.mSession.getDataHandler().getStore().getRoom(stringExtra);
            }
            this.mAddConfirmationDialog = intent.getBooleanExtra(EXTRA_ADD_CONFIRMATION_DIALOG, false);
            if (this.mPatternToSearchEditText != null) {
                this.mPatternToSearchEditText.setHint(C1299R.string.room_participants_invite_search_another_user);
            }
            setWaitingView(findViewById(C1299R.C1301id.search_in_progress_view));
            this.mListView = (ExpandableListView) findViewById(C1299R.C1301id.room_details_members_list);
            this.mListView.setGroupIndicator(null);
            VectorParticipantsAdapter vectorParticipantsAdapter = new VectorParticipantsAdapter(this, C1299R.layout.adapter_item_vector_add_participants, C1299R.layout.adapter_item_vector_people_header, this.mSession, stringExtra, true);
            this.mAdapter = vectorParticipantsAdapter;
            this.mAdapter.setHiddenParticipantItems(this.mHiddenParticipantItems);
            this.mListView.setAdapter(this.mAdapter);
            this.mListView.setOnChildClickListener(new OnChildClickListener() {
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
                    Object child = VectorRoomInviteMembersActivity.this.mAdapter.getChild(i, i2);
                    if (child instanceof ParticipantAdapterItem) {
                        ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) child;
                        if (participantAdapterItem.mIsValid) {
                            VectorRoomInviteMembersActivity.this.finish(new ArrayList(Arrays.asList(new ParticipantAdapterItem[]{participantAdapterItem})));
                            return true;
                        }
                    }
                    return false;
                }
            });
            findViewById(C1299R.C1301id.search_invite_by_id).setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VectorRoomInviteMembersActivity.this.displayInviteByUserId();
                }
            });
            CommonActivityUtils.checkPermissions(8, (Activity) this);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventsListener);
        ContactsManager.getInstance().addListener(this.mContactsListener);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mEventsListener);
        ContactsManager.getInstance().removeListener(this.mContactsListener);
    }

    public void onRequestPermissionsResult(int i, @NonNull String[] strArr, @NonNull int[] iArr) {
        if (strArr.length == 0) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRequestPermissionsResult(): cancelled ");
            sb.append(i);
            Log.m211e(str, sb.toString());
        } else if (i != 8) {
        } else {
            if (iArr[0] == 0) {
                Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission granted");
                ContactsManager.getInstance().refreshLocalContactsSnapshot();
                onPatternUpdate(false);
                return;
            }
            Log.m209d(LOG_TAG, "## onRequestPermissionsResult(): READ_CONTACTS permission not granted");
            CommonActivityUtils.displayToast(this, getString(C1299R.string.missing_permissions_warning));
        }
    }

    /* access modifiers changed from: protected */
    public void onPatternUpdate(boolean z) {
        String obj = this.mPatternToSearchEditText.getText().toString();
        if (!this.mAdapter.isKnownMembersInitialized()) {
            showWaitingView();
        }
        if (!ContactsManager.getInstance().didPopulateLocalContacts()) {
            Log.m209d(LOG_TAG, "## onPatternUpdate() : The local contacts are not yet populated");
            this.mAdapter.reset();
            showWaitingView();
            return;
        }
        this.mAdapter.setSearchedPattern(obj, null, new OnParticipantsSearchListener() {
            public void onSearchEnd(int i) {
                VectorRoomInviteMembersActivity.this.mListView.post(new Runnable() {
                    public void run() {
                        VectorRoomInviteMembersActivity.this.hideWaitingView();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: private */
    public void finish(final ArrayList<ParticipantAdapterItem> arrayList) {
        String str;
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (ParticipantAdapterItem participantAdapterItem : this.mHiddenParticipantItems) {
            arrayList2.add(participantAdapterItem.mUserId);
        }
        if (this.mRoom != null) {
            for (RoomMember roomMember : this.mRoom.getLiveState().getDisplayableMembers()) {
                if (TextUtils.equals(roomMember.membership, RoomMember.MEMBERSHIP_JOIN) || TextUtils.equals(roomMember.membership, "invite")) {
                    arrayList2.add(roomMember.getUserId());
                }
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ParticipantAdapterItem participantAdapterItem2 = (ParticipantAdapterItem) it.next();
            if (!arrayList2.contains(participantAdapterItem2.mUserId)) {
                arrayList3.add(participantAdapterItem2.mUserId);
                if (MXSession.isUserId(participantAdapterItem2.mUserId)) {
                    User user = this.mSession.getDataHandler().getStore().getUser(participantAdapterItem2.mUserId);
                    if (user == null || TextUtils.isEmpty(user.displayname)) {
                        arrayList4.add(participantAdapterItem2.mUserId);
                    } else {
                        arrayList4.add(user.displayname);
                    }
                } else {
                    arrayList4.add(participantAdapterItem2.mUserId);
                }
            }
        }
        if (!this.mAddConfirmationDialog || arrayList4.size() <= 0) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_OUT_SELECTED_USER_IDS, arrayList3);
            intent.putExtra(EXTRA_OUT_SELECTED_PARTICIPANT_ITEMS, arrayList);
            setResult(-1, intent);
            finish();
            return;
        }
        Builder builder = new Builder(this);
        builder.setTitle((int) C1299R.string.dialog_title_confirmation);
        String str2 = "";
        if (arrayList4.size() == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(VectorUtils.getPlainId((String) arrayList4.get(0)));
            str = sb.toString();
        } else {
            String str3 = str2;
            for (int i = 0; i < arrayList4.size() - 2; i++) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str3);
                sb2.append(VectorUtils.getPlainId((String) arrayList4.get(i)));
                sb2.append(", ");
                str3 = sb2.toString();
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str3);
            sb3.append(VectorUtils.getPlainId((String) arrayList4.get(arrayList4.size() - 2)));
            sb3.append(StringUtils.SPACE);
            sb3.append(getText(C1299R.string.and));
            sb3.append(StringUtils.SPACE);
            sb3.append(VectorUtils.getPlainId((String) arrayList4.get(arrayList4.size() - 1)));
            str = sb3.toString();
        }
        builder.setMessage((CharSequence) getString(C1299R.string.room_participants_invite_prompt_msg, new Object[]{str}));
        builder.setPositiveButton((int) C1299R.string.f115ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_OUT_SELECTED_USER_IDS, arrayList3);
                intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_OUT_SELECTED_PARTICIPANT_ITEMS, arrayList);
                VectorRoomInviteMembersActivity.this.setResult(-1, intent);
                VectorRoomInviteMembersActivity.this.finish();
            }
        });
        builder.setNegativeButton((int) C1299R.string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    /* access modifiers changed from: private */
    public void displayInviteByUserId() {
        View inflate = getLayoutInflater().inflate(C1299R.layout.dialog_invite_by_id, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(C1299R.string.people_search_invite_by_id_dialog_title);
        builder.setView(inflate);
        final VectorAutoCompleteTextView vectorAutoCompleteTextView = (VectorAutoCompleteTextView) inflate.findViewById(C1299R.C1301id.invite_by_id_edit_text);
        vectorAutoCompleteTextView.initAutoCompletion(this.mSession);
        vectorAutoCompleteTextView.setProvideMatrixIdOnly(true);
        builder.setPositiveButton(C1299R.string.invite, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setNegativeButton(C1299R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog show = builder.show();
        final Button button = show.getButton(-1);
        if (button != null) {
            button.setEnabled(false);
            button.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String obj = vectorAutoCompleteTextView.getText().toString();
                    if (!Patterns.EMAIL_ADDRESS.matcher(obj).find()) {
                        obj = VectorRoomInviteMembersActivity.this.getString(C1299R.string.search_string, new Object[]{obj});
                    }
                    ArrayList arrayList = new ArrayList();
                    for (Pattern matcher : Arrays.asList(new Pattern[]{MXSession.PATTERN_CONTAIN_MATRIX_USER_IDENTIFIER, Patterns.EMAIL_ADDRESS})) {
                        Matcher matcher2 = matcher.matcher(obj);
                        while (matcher2.find()) {
                            try {
                                String substring = obj.substring(matcher2.start(0), matcher2.end(0));
                                arrayList.add(new ParticipantAdapterItem(substring, null, substring, true));
                            } catch (Exception e) {
                                String access$400 = VectorRoomInviteMembersActivity.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("## displayInviteByUserId() ");
                                sb.append(e.getMessage());
                                Log.m211e(access$400, sb.toString());
                            }
                        }
                    }
                    VectorRoomInviteMembersActivity.this.finish(arrayList);
                    show.dismiss();
                }
            });
        }
        vectorAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (button != null) {
                    String obj = vectorAutoCompleteTextView.getText().toString();
                    button.setEnabled(!TextUtils.isEmpty(obj) || Patterns.EMAIL_ADDRESS.matcher(obj).find());
                }
            }
        });
    }
}
