package com.opengarden.firechat.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.view.GravityCompat;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.VectorGroupDetailsActivity;
import com.opengarden.firechat.adapters.AbsAdapter.GroupInvitationListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreGroupActionListener;
import com.opengarden.firechat.adapters.GroupAdapter;
import com.opengarden.firechat.adapters.GroupAdapter.OnGroupSelectItemListener;
import com.opengarden.firechat.fragments.AbsHomeFragment.OnFilterListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.listeners.MXEventListener;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.view.EmptyViewItemDecoration;
import com.opengarden.firechat.view.SimpleDividerItemDecoration;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends AbsHomeFragment {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "GroupsFragment";
    /* access modifiers changed from: private */
    public GroupAdapter mAdapter;
    private final MXEventListener mEventListener = new MXEventListener() {
        public void onNewGroupInvitation(String str) {
            GroupsFragment.this.refreshGroups();
        }

        public void onJoinGroup(String str) {
            GroupsFragment.this.refreshGroups();
        }

        public void onLeaveGroup(String str) {
            GroupsFragment.this.refreshGroups();
        }
    };
    /* access modifiers changed from: private */
    public GroupsManager mGroupsManager;
    private final List<Group> mInvitedGroups = new ArrayList();
    private final List<Group> mJoinedGroups = new ArrayList();
    @BindView(2131296889)
    RecyclerView mRecycler;

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1299R.layout.fragment_groups, viewGroup, false);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mGroupsManager = this.mSession.getGroupsManager();
        this.mPrimaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_groups);
        this.mSecondaryColor = ContextCompat.getColor(getActivity(), C1299R.color.tab_groups_secondary);
        initViews();
        this.mAdapter.onFilterDone(this.mCurrentFilter);
    }

    public void onResume() {
        super.onResume();
        this.mSession.getDataHandler().addListener(this.mEventListener);
        this.mRecycler.addOnScrollListener(this.mScrollListener);
        refreshGroupsAndProfiles();
    }

    public void onPause() {
        super.onPause();
        this.mSession.getDataHandler().removeListener(this.mEventListener);
        this.mRecycler.removeOnScrollListener(this.mScrollListener);
    }

    /* access modifiers changed from: protected */
    public List<Room> getRooms() {
        return new ArrayList();
    }

    /* access modifiers changed from: protected */
    public void onFilter(String str, final OnFilterListener onFilterListener) {
        this.mAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                String access$100 = GroupsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onFilterComplete ");
                sb.append(i);
                Log.m213i(access$100, sb.toString());
                if (onFilterListener != null) {
                    onFilterListener.onFilterDone(i);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onResetFilter() {
        this.mAdapter.getFilter().filter("", new FilterListener() {
            public void onFilterComplete(int i) {
                String access$100 = GroupsFragment.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("onResetFilter ");
                sb.append(i);
                Log.m213i(access$100, sb.toString());
            }
        });
    }

    private void initViews() {
        int dimension = (int) getResources().getDimension(C1299R.dimen.item_decoration_left_margin);
        this.mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        this.mRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity(), 1, dimension));
        RecyclerView recyclerView = this.mRecycler;
        EmptyViewItemDecoration emptyViewItemDecoration = new EmptyViewItemDecoration(getActivity(), 1, 40, 16, 14);
        recyclerView.addItemDecoration(emptyViewItemDecoration);
        this.mAdapter = new GroupAdapter(getActivity(), new OnGroupSelectItemListener() {
            public void onSelectItem(Group group, int i) {
                Intent intent = new Intent(GroupsFragment.this.getActivity(), VectorGroupDetailsActivity.class);
                intent.putExtra(VectorGroupDetailsActivity.EXTRA_GROUP_ID, group.getGroupId());
                intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", GroupsFragment.this.mSession.getCredentials().userId);
                GroupsFragment.this.startActivity(intent);
            }

            public boolean onLongPressItem(Group group, int i) {
                VectorUtils.copyToClipboard(GroupsFragment.this.getActivity(), group.getGroupId());
                return true;
            }
        }, new GroupInvitationListener() {
            public void onJoinGroup(MXSession mXSession, String str) {
                GroupsFragment.this.mActivity.showWaitingView();
                GroupsFragment.this.mGroupsManager.joinGroup(str, new ApiCallback<Void>() {
                    private void onDone(String str) {
                        if (!(str == null || GroupsFragment.this.getActivity() == null)) {
                            Toast.makeText(GroupsFragment.this.getActivity(), str, 0).show();
                        }
                        GroupsFragment.this.mActivity.hideWaitingView();
                    }

                    public void onSuccess(Void voidR) {
                        onDone(null);
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
            }

            public void onRejectInvitation(MXSession mXSession, String str) {
                GroupsFragment.this.leaveOrReject(str);
            }
        }, new MoreGroupActionListener() {
            public void onMoreActionClick(View view, Group group) {
                GroupsFragment.this.displayGroupPopupMenu(group, view);
            }
        });
        this.mRecycler.setAdapter(this.mAdapter);
    }

    /* access modifiers changed from: private */
    public void refreshGroups() {
        this.mJoinedGroups.clear();
        this.mJoinedGroups.addAll(this.mGroupsManager.getJoinedGroups());
        this.mAdapter.setGroups(this.mJoinedGroups);
        this.mInvitedGroups.clear();
        this.mInvitedGroups.addAll(this.mGroupsManager.getInvitedGroups());
        this.mAdapter.setInvitedGroups(this.mInvitedGroups);
    }

    private void refreshGroupsAndProfiles() {
        refreshGroups();
        this.mSession.getGroupsManager().refreshGroupProfiles((ApiCallback<Void>) new SimpleApiCallback<Void>() {
            public void onSuccess(Void voidR) {
                if (GroupsFragment.this.mActivity != null && !GroupsFragment.this.mActivity.isFinishing()) {
                    GroupsFragment.this.mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void leaveOrReject(String str) {
        this.mActivity.showWaitingView();
        this.mGroupsManager.leaveGroup(str, new ApiCallback<Void>() {
            private void onDone(String str) {
                if (!(str == null || GroupsFragment.this.getActivity() == null)) {
                    Toast.makeText(GroupsFragment.this.getActivity(), str, 0).show();
                }
                GroupsFragment.this.mActivity.hideWaitingView();
            }

            public void onSuccess(Void voidR) {
                onDone(null);
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
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void displayGroupPopupMenu(final Group group, View view) {
        PopupMenu popupMenu;
        FragmentActivity activity = getActivity();
        if (VERSION.SDK_INT >= 19) {
            popupMenu = new PopupMenu(activity, view, GravityCompat.END);
        } else {
            popupMenu = new PopupMenu(activity, view);
        }
        popupMenu.getMenuInflater().inflate(C1299R.C1302menu.vector_home_group_settings, popupMenu.getMenu());
        CommonActivityUtils.tintMenuIcons(popupMenu.getMenu(), ThemeUtils.INSTANCE.getColor(activity, C1299R.attr.settings_icon_tint_color));
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == C1299R.C1301id.ic_action_select_remove_group) {
                    GroupsFragment.this.leaveOrReject(group.getGroupId());
                }
                return false;
            }
        });
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
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## displayGroupPopupMenu() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
        popupMenu.show();
    }

    public boolean onFabClick() {
        Builder builder = new Builder(getActivity());
        View inflate = getActivity().getLayoutInflater().inflate(C1299R.layout.dialog_create_group, null);
        builder.setView(inflate);
        final EditText editText = (EditText) inflate.findViewById(C1299R.C1301id.community_name_edit_text);
        final EditText editText2 = (EditText) inflate.findViewById(C1299R.C1301id.community_id_edit_text);
        final String host = this.mSession.getHomeServerConfig().getHomeserverUri().getHost();
        TextView textView = (TextView) inflate.findViewById(C1299R.C1301id.community_hs_name_text_view);
        StringBuilder sb = new StringBuilder();
        sb.append(":");
        sb.append(host);
        textView.setText(sb.toString());
        builder.setCancelable(false).setTitle(C1299R.string.create_community).setPositiveButton(C1299R.string.create, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String trim = editText2.getText().toString().trim();
                String trim2 = editText.getText().toString().trim();
                GroupsFragment.this.mActivity.showWaitingView();
                GroupsFragment.this.mGroupsManager.createGroup(trim, trim2, new ApiCallback<String>() {
                    private void onDone(String str) {
                        if (GroupsFragment.this.getActivity() != null) {
                            if (str != null) {
                                Toast.makeText(GroupsFragment.this.getActivity(), str, 1).show();
                            }
                            GroupsFragment.this.mActivity.hideWaitingView();
                            GroupsFragment.this.refreshGroups();
                        }
                    }

                    public void onSuccess(String str) {
                        onDone(null);
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
            }
        }).setNegativeButton(C1299R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        create.show();
        final Button button = create.getButton(-1);
        editText2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                Button button = button;
                StringBuilder sb = new StringBuilder();
                sb.append("+");
                sb.append(editText2.getText().toString().trim());
                sb.append(":");
                sb.append(host);
                button.setEnabled(MXSession.isGroupId(sb.toString()));
            }
        });
        button.setEnabled(false);
        return true;
    }
}
