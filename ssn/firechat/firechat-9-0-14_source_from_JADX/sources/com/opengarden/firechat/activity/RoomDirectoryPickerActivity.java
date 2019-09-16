package com.opengarden.firechat.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.p003v7.app.AlertDialog.Builder;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.RoomDirectoryAdapter;
import com.opengarden.firechat.adapters.RoomDirectoryAdapter.OnSelectRoomDirectoryListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyProtocol;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyProtocolInstance;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.RoomDirectoryData;
import com.opengarden.firechat.util.ThemeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RoomDirectoryPickerActivity extends RiotAppCompatActivity implements OnSelectRoomDirectoryListener {
    public static final String EXTRA_OUT_ROOM_DIRECTORY_DATA = "EXTRA_OUT_ROOM_DIRECTORY_DATA";
    private static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RoomDirectoryPickerActivity";
    /* access modifiers changed from: private */
    public RoomDirectoryAdapter mRoomDirectoryAdapter;
    /* access modifiers changed from: private */
    public MXSession mSession;

    public int getLayoutRes() {
        return C1299R.layout.activity_room_directory_picker;
    }

    public int getTitleRes() {
        return C1299R.string.select_room_directory;
    }

    public static Intent getIntent(Context context, String str) {
        Intent intent = new Intent(context, RoomDirectoryPickerActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, str);
        return intent;
    }

    public void initUiAndData() {
        setWaitingView(findViewById(C1299R.C1301id.room_directory_loading));
        Toolbar toolbar = (Toolbar) findViewById(C1299R.C1301id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        String stringExtra = getIntent().getStringExtra(EXTRA_SESSION_ID);
        if (stringExtra != null) {
            this.mSession = Matrix.getInstance(this).getSession(stringExtra);
        }
        if (this.mSession == null || !this.mSession.isAlive()) {
            finish();
        } else {
            initViews();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.menu_directory_server_picker, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            setResult(0);
            finish();
            return true;
        }
        if (menuItem.getItemId() == C1299R.C1301id.action_add_custom_hs) {
            displayCustomDirectoryDialog();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshDirectoryServersList() {
        showWaitingView();
        this.mSession.getEventsApiClient().getThirdPartyServerProtocols(new ApiCallback<Map<String, ThirdPartyProtocol>>() {
            private void onDone(List<RoomDirectoryData> list) {
                RoomDirectoryPickerActivity.this.hideWaitingView();
                int i = 1;
                String substring = RoomDirectoryPickerActivity.this.mSession.getMyUserId().substring(RoomDirectoryPickerActivity.this.mSession.getMyUserId().indexOf(":") + 1);
                List<String> asList = Arrays.asList(RoomDirectoryPickerActivity.this.getResources().getStringArray(C1299R.array.room_directory_servers));
                list.add(0, RoomDirectoryData.createIncludingAllNetworks(null, substring));
                if (!list.isEmpty()) {
                    list.add(1, RoomDirectoryData.getDefault());
                    i = 2;
                }
                for (String str : asList) {
                    if (!TextUtils.equals(substring, str)) {
                        int i2 = i + 1;
                        list.add(i, RoomDirectoryData.createIncludingAllNetworks(str, str));
                        i = i2;
                    }
                }
                RoomDirectoryPickerActivity.this.mRoomDirectoryAdapter.updateDirectoryServersList(list);
            }

            public void onSuccess(Map<String, ThirdPartyProtocol> map) {
                ArrayList arrayList = new ArrayList();
                for (String str : map.keySet()) {
                    for (ThirdPartyProtocolInstance thirdPartyProtocolInstance : ((ThirdPartyProtocol) map.get(str)).instances) {
                        RoomDirectoryData roomDirectoryData = new RoomDirectoryData(null, thirdPartyProtocolInstance.desc, thirdPartyProtocolInstance.icon, thirdPartyProtocolInstance.instanceId, false);
                        arrayList.add(roomDirectoryData);
                    }
                }
                onDone(arrayList);
            }

            public void onNetworkError(Exception exc) {
                String access$200 = RoomDirectoryPickerActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## refreshDirectoryServersList() : ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
                onDone(new ArrayList());
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$200 = RoomDirectoryPickerActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onMatrixError() : ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$200, sb.toString());
                onDone(new ArrayList());
            }

            public void onUnexpectedError(Exception exc) {
                String access$200 = RoomDirectoryPickerActivity.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onUnexpectedError() : ");
                sb.append(exc.getMessage());
                Log.m211e(access$200, sb.toString());
                onDone(new ArrayList());
            }
        });
    }

    private void displayCustomDirectoryDialog() {
        Builder builder = new Builder(this);
        View inflate = LayoutInflater.from(this).inflate(C1299R.layout.dialog_directory_picker, null);
        builder.setView(inflate);
        final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.directory_picker_edit_text);
        builder.setPositiveButton((int) C1299R.string.f115ok, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                final String trim = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)) {
                    RoomDirectoryPickerActivity.this.showWaitingView();
                    RoomDirectoryPickerActivity.this.mSession.getEventsApiClient().getPublicRoomsCount(trim, new ApiCallback<Integer>() {
                        public void onSuccess(Integer num) {
                            Intent intent = new Intent();
                            String str = RoomDirectoryPickerActivity.EXTRA_OUT_ROOM_DIRECTORY_DATA;
                            RoomDirectoryData roomDirectoryData = new RoomDirectoryData(trim, trim, null, null, false);
                            intent.putExtra(str, roomDirectoryData);
                            RoomDirectoryPickerActivity.this.setResult(-1, intent);
                            RoomDirectoryPickerActivity.this.finish();
                        }

                        private void onError(String str) {
                            String access$200 = RoomDirectoryPickerActivity.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## onSelectDirectoryServer() failed ");
                            sb.append(str);
                            Log.m211e(access$200, sb.toString());
                            RoomDirectoryPickerActivity.this.hideWaitingView();
                            Toast.makeText(RoomDirectoryPickerActivity.this, C1299R.string.directory_server_fail_to_retrieve_server, 1).show();
                        }

                        public void onNetworkError(Exception exc) {
                            onError(exc.getMessage());
                        }

                        public void onMatrixError(MatrixError matrixError) {
                            onError(matrixError.getMessage());
                        }

                        public void onUnexpectedError(Exception exc) {
                            onError(exc.getMessage());
                        }
                    });
                }
            }
        });
        builder.setNegativeButton((int) C1299R.string.cancel, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(C1299R.C1301id.room_directory_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(1);
        recyclerView.setLayoutManager(linearLayoutManager);
        this.mRoomDirectoryAdapter = new RoomDirectoryAdapter(new ArrayList(), this);
        recyclerView.setAdapter(this.mRoomDirectoryAdapter);
        refreshDirectoryServersList();
    }

    public void onSelectRoomDirectory(RoomDirectoryData roomDirectoryData) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OUT_ROOM_DIRECTORY_DATA, roomDirectoryData);
        setResult(-1, intent);
        finish();
    }
}
