package com.opengarden.firechat.fragments;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.adapters.VectorUnknownDevicesAdapter;
import com.opengarden.firechat.adapters.VectorUnknownDevicesAdapter.IVerificationAdapterListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VectorUnknownDevicesFragment extends DialogFragment {
    private static final String ARG_SESSION_ID = "VectorUnknownDevicesFragment.ARG_SESSION_ID";
    /* access modifiers changed from: private */
    public static IUnknownDevicesSendAnywayListener mListener;
    private static MXUsersDevicesMap<MXDeviceInfo> mUnknownDevicesMap;
    private List<Pair<String, List<MXDeviceInfo>>> mDevicesList;
    /* access modifiers changed from: private */
    public ExpandableListView mExpandableListView;
    /* access modifiers changed from: private */
    public boolean mIsSendAnywayTapped = false;
    /* access modifiers changed from: private */
    public MXSession mSession;

    public interface IUnknownDevicesSendAnywayListener {
        void onSendAnyway();
    }

    public static VectorUnknownDevicesFragment newInstance(String str, MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap, IUnknownDevicesSendAnywayListener iUnknownDevicesSendAnywayListener) {
        VectorUnknownDevicesFragment vectorUnknownDevicesFragment = new VectorUnknownDevicesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_SESSION_ID, str);
        mUnknownDevicesMap = mXUsersDevicesMap;
        mListener = iUnknownDevicesSendAnywayListener;
        vectorUnknownDevicesFragment.setArguments(bundle);
        return vectorUnknownDevicesFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSession = Matrix.getMXSession(getActivity(), getArguments().getString(ARG_SESSION_ID));
    }

    private static List<Pair<String, List<MXDeviceInfo>>> getDevicesList() {
        ArrayList arrayList = new ArrayList();
        if (mUnknownDevicesMap != null) {
            for (String str : mUnknownDevicesMap.getUserIds()) {
                ArrayList arrayList2 = new ArrayList();
                for (String object : mUnknownDevicesMap.getUserDeviceIds(str)) {
                    arrayList2.add(mUnknownDevicesMap.getObject(object, str));
                }
                arrayList.add(new Pair(str, arrayList2));
            }
        }
        return arrayList;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Builder builder = new Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View inflate = layoutInflater.inflate(C1299R.layout.dialog_unknown_devices, null);
        this.mExpandableListView = (ExpandableListView) inflate.findViewById(C1299R.C1301id.unknown_devices_list_view);
        this.mDevicesList = getDevicesList();
        final VectorUnknownDevicesAdapter vectorUnknownDevicesAdapter = new VectorUnknownDevicesAdapter(getContext(), this.mDevicesList);
        vectorUnknownDevicesAdapter.setListener(new IVerificationAdapterListener() {
            final ApiCallback<Void> mCallback = new ApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    C23221.this.refresh();
                }

                public void onNetworkError(Exception exc) {
                    C23221.this.refresh();
                }

                public void onMatrixError(MatrixError matrixError) {
                    C23221.this.refresh();
                }

                public void onUnexpectedError(Exception exc) {
                    C23221.this.refresh();
                }
            };

            /* access modifiers changed from: private */
            public void refresh() {
                vectorUnknownDevicesAdapter.notifyDataSetChanged();
            }

            public void OnVerifyDeviceClick(MXDeviceInfo mXDeviceInfo) {
                if (mXDeviceInfo.mVerified != 1) {
                    CommonActivityUtils.displayDeviceVerificationDialog(mXDeviceInfo, mXDeviceInfo.userId, VectorUnknownDevicesFragment.this.mSession, VectorUnknownDevicesFragment.this.getActivity(), this.mCallback);
                } else {
                    VectorUnknownDevicesFragment.this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, mXDeviceInfo.userId, this.mCallback);
                }
            }

            public void OnBlockDeviceClick(MXDeviceInfo mXDeviceInfo) {
                if (mXDeviceInfo.mVerified == 2) {
                    VectorUnknownDevicesFragment.this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, mXDeviceInfo.userId, this.mCallback);
                } else {
                    VectorUnknownDevicesFragment.this.mSession.getCrypto().setDeviceVerification(2, mXDeviceInfo.deviceId, mXDeviceInfo.userId, this.mCallback);
                }
                refresh();
            }
        });
        this.mExpandableListView.addHeaderView(layoutInflater.inflate(C1299R.layout.dialog_unknown_devices_header, null));
        this.mExpandableListView.setGroupIndicator(null);
        this.mExpandableListView.setAdapter(vectorUnknownDevicesAdapter);
        this.mExpandableListView.post(new Runnable() {
            public void run() {
                int groupCount = vectorUnknownDevicesAdapter.getGroupCount();
                for (int i = 0; i < groupCount; i++) {
                    VectorUnknownDevicesFragment.this.mExpandableListView.expandGroup(i);
                }
            }
        });
        builder.setView(inflate).setTitle(C1299R.string.unknown_devices_alert_title);
        if (mListener != null) {
            builder.setPositiveButton(C1299R.string.send_anyway, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    VectorUnknownDevicesFragment.this.mIsSendAnywayTapped = true;
                }
            });
            builder.setNeutralButton(C1299R.string.f115ok, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        } else {
            builder.setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }
        return builder.create();
    }

    public void dismissAllowingStateLoss() {
        if (getFragmentManager() != null) {
            super.dismissAllowingStateLoss();
        }
        mUnknownDevicesMap = null;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        setDevicesKnown(this.mDevicesList);
    }

    private void setDevicesKnown(List<Pair<String, List<MXDeviceInfo>>> list) {
        if (mUnknownDevicesMap != null) {
            mUnknownDevicesMap = null;
            ArrayList arrayList = new ArrayList();
            for (Pair pair : list) {
                arrayList.addAll((Collection) pair.second);
            }
            this.mSession.getCrypto().setDevicesKnown(arrayList, new ApiCallback<Void>() {
                private void onDone() {
                    if (VectorUnknownDevicesFragment.this.mIsSendAnywayTapped && VectorUnknownDevicesFragment.mListener != null) {
                        VectorUnknownDevicesFragment.mListener.onSendAnyway();
                    }
                    VectorUnknownDevicesFragment.mListener = null;
                    if (VectorUnknownDevicesFragment.this.isAdded() && VectorUnknownDevicesFragment.this.isResumed()) {
                        VectorUnknownDevicesFragment.this.dismissAllowingStateLoss();
                    }
                }

                public void onSuccess(Void voidR) {
                    onDone();
                }

                public void onNetworkError(Exception exc) {
                    onDone();
                }

                public void onMatrixError(MatrixError matrixError) {
                    onDone();
                }

                public void onUnexpectedError(Exception exc) {
                    onDone();
                }
            });
        }
    }
}
