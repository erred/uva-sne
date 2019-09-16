package com.opengarden.firechat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.ParticipantAdapterItem;
import com.opengarden.firechat.adapters.VectorRoomCreationAdapter;
import com.opengarden.firechat.adapters.VectorRoomCreationAdapter.IRoomCreationAdapterListener;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.data.store.IMXStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.CreateRoomParams;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VectorRoomCreationActivity extends MXCActionBarActivity {
    private static final int INVITE_USER_REQUEST_CODE = 456;
    private static final String PARTICIPANTS_LIST = "PARTICIPANTS_LIST";
    /* access modifiers changed from: private */
    public final String LOG_TAG = VectorRoomCreationActivity.class.getSimpleName();
    /* access modifiers changed from: private */
    public VectorRoomCreationAdapter mAdapter;
    private final Comparator<ParticipantAdapterItem> mAlphaComparator = new Comparator<ParticipantAdapterItem>() {
        public int compare(ParticipantAdapterItem participantAdapterItem, ParticipantAdapterItem participantAdapterItem2) {
            if (TextUtils.equals(participantAdapterItem.mUserId, VectorRoomCreationActivity.this.mSession.getMyUserId())) {
                return -1;
            }
            if (TextUtils.equals(participantAdapterItem2.mUserId, VectorRoomCreationActivity.this.mSession.getMyUserId())) {
                return 1;
            }
            String comparisonDisplayName = participantAdapterItem.getComparisonDisplayName();
            String comparisonDisplayName2 = participantAdapterItem2.getComparisonDisplayName();
            if (comparisonDisplayName == null) {
                return -1;
            }
            if (comparisonDisplayName2 == null) {
                return 1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(comparisonDisplayName, comparisonDisplayName2);
        }
    };
    private final ApiCallback<String> mCreateDirectMessageCallBack = new ApiCallback<String>() {
        public void onSuccess(String str) {
            HashMap hashMap = new HashMap();
            hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomCreationActivity.this.mSession.getMyUserId());
            hashMap.put("EXTRA_ROOM_ID", str);
            hashMap.put(VectorRoomActivity.EXTRA_EXPAND_ROOM_HEADER, Boolean.valueOf(true));
            Log.m209d(VectorRoomCreationActivity.this.LOG_TAG, "## mCreateDirectMessageCallBack: onSuccess - start goToRoomPage");
            CommonActivityUtils.goToRoomPage(VectorRoomCreationActivity.this, VectorRoomCreationActivity.this.mSession, hashMap);
        }

        private void onError(final String str) {
            VectorRoomCreationActivity.this.getWaitingView().post(new Runnable() {
                public void run() {
                    if (str != null) {
                        Toast.makeText(VectorRoomCreationActivity.this, str, 1).show();
                    }
                    VectorRoomCreationActivity.this.getWaitingView().setVisibility(8);
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
    private boolean mIsFirstResume = true;
    /* access modifiers changed from: private */
    public ArrayList<ParticipantAdapterItem> mParticipants = new ArrayList<>();

    public int getLayoutRes() {
        return C1299R.layout.activity_vector_room_creation;
    }

    public void initUiAndData() {
        if (CommonActivityUtils.shouldRestartApp(this)) {
            Log.m211e(this.LOG_TAG, "onCreate : Restart the application.");
            CommonActivityUtils.restartApp(this);
            return;
        }
        this.mSession = getSession(getIntent());
        if (this.mSession == null) {
            Log.m211e(this.LOG_TAG, "No MXSession.");
            finish();
            return;
        }
        setWaitingView(findViewById(C1299R.C1301id.room_creation_spinner_views));
        ListView listView = (ListView) findViewById(C1299R.C1301id.room_creation_members_list_view);
        this.mAdapter = new VectorRoomCreationAdapter(this, C1299R.layout.adapter_item_vector_creation_add_member, C1299R.layout.adapter_item_vector_add_participants, this.mSession);
        if (isFirstCreation() || !getSavedInstanceState().containsKey(PARTICIPANTS_LIST)) {
            this.mParticipants.add(new ParticipantAdapterItem((User) this.mSession.getMyUser()));
        } else {
            this.mParticipants.clear();
            this.mParticipants = new ArrayList<>((List) getSavedInstanceState().getSerializable(PARTICIPANTS_LIST));
        }
        this.mAdapter.addAll(this.mParticipants);
        listView.setAdapter(this.mAdapter);
        this.mAdapter.setRoomCreationAdapterListener(new IRoomCreationAdapterListener() {
            public void OnRemoveParticipantClick(ParticipantAdapterItem participantAdapterItem) {
                VectorRoomCreationActivity.this.mParticipants.remove(participantAdapterItem);
                VectorRoomCreationActivity.this.mAdapter.remove(participantAdapterItem);
            }
        });
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (i == 0) {
                    VectorRoomCreationActivity.this.launchSearchActivity();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void launchSearchActivity() {
        Intent intent = new Intent(this, VectorRoomInviteMembersActivity.class);
        intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getMyUserId());
        intent.putExtra(VectorRoomInviteMembersActivity.EXTRA_HIDDEN_PARTICIPANT_ITEMS, this.mParticipants);
        startActivityForResult(intent, INVITE_USER_REQUEST_CODE);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.mIsFirstResume) {
            this.mIsFirstResume = false;
            launchSearchActivity();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(PARTICIPANTS_LIST, this.mParticipants);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (bundle != null) {
            if (bundle.containsKey(PARTICIPANTS_LIST)) {
                this.mParticipants = new ArrayList<>((List) bundle.getSerializable(PARTICIPANTS_LIST));
            } else {
                this.mParticipants.clear();
                this.mParticipants.add(new ParticipantAdapterItem((User) this.mSession.getMyUser()));
            }
            this.mAdapter.clear();
            this.mAdapter.addAll(this.mParticipants);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i != INVITE_USER_REQUEST_CODE) {
            return;
        }
        if (i2 == -1) {
            List list = (List) intent.getSerializableExtra(VectorRoomInviteMembersActivity.EXTRA_OUT_SELECTED_PARTICIPANT_ITEMS);
            this.mParticipants.addAll(list);
            this.mAdapter.addAll(list);
            this.mAdapter.sort(this.mAlphaComparator);
        } else if (1 == this.mParticipants.size()) {
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (CommonActivityUtils.shouldRestartApp(this) || this.mSession == null) {
            return false;
        }
        getMenuInflater().inflate(C1299R.C1302menu.vector_room_creation, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != C1299R.C1301id.action_create_room) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mParticipants.size() == 0) {
            createRoom(this.mParticipants);
        } else {
            this.mParticipants.remove(0);
            if (this.mParticipants.size() == 0) {
                createRoom(this.mParticipants);
            } else if (this.mParticipants.size() > 1) {
                createRoom(this.mParticipants);
            } else {
                String isDirectChatRoomAlreadyExist = isDirectChatRoomAlreadyExist(((ParticipantAdapterItem) this.mParticipants.get(0)).mUserId);
                if (isDirectChatRoomAlreadyExist != null) {
                    HashMap hashMap = new HashMap();
                    hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", ((ParticipantAdapterItem) this.mParticipants.get(0)).mUserId);
                    hashMap.put("EXTRA_ROOM_ID", isDirectChatRoomAlreadyExist);
                    CommonActivityUtils.goToRoomPage(this, this.mSession, hashMap);
                } else {
                    showWaitingView();
                    this.mSession.createDirectMessageRoom(((ParticipantAdapterItem) this.mParticipants.get(0)).mUserId, this.mCreateDirectMessageCallBack);
                }
            }
        }
        return true;
    }

    private String isDirectChatRoomAlreadyExist(String str) {
        if (this.mSession != null) {
            IMXStore store = this.mSession.getDataHandler().getStore();
            if (store.getDirectChatRoomsDict() != null) {
                HashMap hashMap = new HashMap(store.getDirectChatRoomsDict());
                if (hashMap.containsKey(str)) {
                    ArrayList arrayList = new ArrayList((Collection) hashMap.get(str));
                    if (arrayList.size() != 0) {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            String str2 = (String) it.next();
                            Room room = this.mSession.getDataHandler().getRoom(str2, false);
                            if (room != null && room.isReady() && !room.isInvited() && !room.isLeaving()) {
                                for (RoomMember userId : room.getActiveMembers()) {
                                    if (TextUtils.equals(userId.getUserId(), str)) {
                                        String str3 = this.LOG_TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("## isDirectChatRoomAlreadyExist(): for user=");
                                        sb.append(str);
                                        sb.append(" roomFound=");
                                        sb.append(str2);
                                        Log.m209d(str3, sb.toString());
                                        return str2;
                                    }
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
        String str4 = this.LOG_TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("## isDirectChatRoomAlreadyExist(): for user=");
        sb2.append(str);
        sb2.append(" no found room");
        Log.m209d(str4, sb2.toString());
        return null;
    }

    private void createRoom(List<ParticipantAdapterItem> list) {
        showWaitingView();
        CreateRoomParams createRoomParams = new CreateRoomParams();
        ArrayList arrayList = new ArrayList();
        for (ParticipantAdapterItem participantAdapterItem : list) {
            if (participantAdapterItem.mUserId != null) {
                arrayList.add(participantAdapterItem.mUserId);
            }
        }
        createRoomParams.addParticipantIds(this.mSession.getHomeServerConfig(), arrayList);
        this.mSession.createRoom(createRoomParams, new SimpleApiCallback<String>(this) {
            public void onSuccess(final String str) {
                VectorRoomCreationActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        HashMap hashMap = new HashMap();
                        hashMap.put("MXCActionBarActivity.EXTRA_MATRIX_ID", VectorRoomCreationActivity.this.mSession.getMyUserId());
                        hashMap.put("EXTRA_ROOM_ID", str);
                        CommonActivityUtils.goToRoomPage(VectorRoomCreationActivity.this, VectorRoomCreationActivity.this.mSession, hashMap);
                    }
                });
            }

            private void onError(final String str) {
                VectorRoomCreationActivity.this.getWaitingView().post(new Runnable() {
                    public void run() {
                        if (str != null) {
                            Toast.makeText(VectorRoomCreationActivity.this, str, 1).show();
                        }
                        VectorRoomCreationActivity.this.hideWaitingView();
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
        });
    }
}
