package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorMemberDetailsDevicesAdapter extends ArrayAdapter<MXDeviceInfo> {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMemberDetailsDevicesAdapter";
    /* access modifiers changed from: private */
    public IDevicesAdapterListener mActivityListener;
    private final int mItemLayoutResourceId;
    private final LayoutInflater mLayoutInflater;
    private final String myDeviceId;

    public interface IDevicesAdapterListener {
        void OnBlockDeviceClick(MXDeviceInfo mXDeviceInfo);

        void OnVerifyDeviceClick(MXDeviceInfo mXDeviceInfo);
    }

    public VectorMemberDetailsDevicesAdapter(Context context, int i, MXSession mXSession) {
        super(context, i);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mItemLayoutResourceId = i;
        if (mXSession.getCredentials() != null) {
            this.myDeviceId = mXSession.getCredentials().deviceId;
        } else {
            this.myDeviceId = null;
        }
    }

    public void setDevicesAdapterListener(IDevicesAdapterListener iDevicesAdapterListener) {
        this.mActivityListener = iDevicesAdapterListener;
    }

    public void notifyDataSetChanged() {
        if (this.myDeviceId != null) {
            setNotifyOnChange(false);
            MXDeviceInfo mXDeviceInfo = null;
            int i = 0;
            while (true) {
                if (i >= getCount()) {
                    break;
                } else if (TextUtils.equals(this.myDeviceId, ((MXDeviceInfo) getItem(i)).deviceId)) {
                    mXDeviceInfo = (MXDeviceInfo) getItem(i);
                    break;
                } else {
                    i++;
                }
            }
            if (mXDeviceInfo != null) {
                remove(mXDeviceInfo);
                insert(mXDeviceInfo, 0);
            }
            setNotifyOnChange(true);
        }
        super.notifyDataSetChanged();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        int i2 = 0;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mItemLayoutResourceId, viewGroup, false);
        }
        final MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) getItem(i);
        Button button = (Button) view.findViewById(C1299R.C1301id.button_verify);
        Button button2 = (Button) view.findViewById(C1299R.C1301id.button_block);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.device_name);
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.device_id);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.device_e2e_icon);
        button.setTransformationMethod(null);
        button2.setTransformationMethod(null);
        textView.setText(mXDeviceInfo.displayName());
        textView2.setText(mXDeviceInfo.deviceId);
        switch (mXDeviceInfo.mVerified) {
            case 1:
                imageView.setImageResource(C1299R.C1300drawable.e2e_verified);
                break;
            case 2:
                imageView.setImageResource(C1299R.C1300drawable.e2e_blocked);
                break;
            default:
                imageView.setImageResource(C1299R.C1300drawable.e2e_warning);
                break;
        }
        switch (mXDeviceInfo.mVerified) {
            case -1:
                button.setText(C1299R.string.encryption_information_verify);
                button2.setText(C1299R.string.encryption_information_block);
                break;
            case 0:
                button.setText(C1299R.string.encryption_information_verify);
                button2.setText(C1299R.string.encryption_information_block);
                break;
            case 1:
                button.setText(C1299R.string.encryption_information_unverify);
                button2.setText(C1299R.string.encryption_information_block);
                break;
            default:
                button.setText(C1299R.string.encryption_information_verify);
                button2.setText(C1299R.string.encryption_information_unblock);
                break;
        }
        button.setVisibility(TextUtils.equals(this.myDeviceId, mXDeviceInfo.deviceId) ? 4 : 0);
        if (TextUtils.equals(this.myDeviceId, mXDeviceInfo.deviceId)) {
            i2 = 4;
        }
        button2.setVisibility(i2);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorMemberDetailsDevicesAdapter.this.mActivityListener != null) {
                    try {
                        VectorMemberDetailsDevicesAdapter.this.mActivityListener.OnVerifyDeviceClick(mXDeviceInfo);
                    } catch (Exception e) {
                        String access$100 = VectorMemberDetailsDevicesAdapter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## getView() : OnVerifyDeviceClick fails ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorMemberDetailsDevicesAdapter.this.mActivityListener != null) {
                    try {
                        VectorMemberDetailsDevicesAdapter.this.mActivityListener.OnBlockDeviceClick(mXDeviceInfo);
                    } catch (Exception e) {
                        String access$100 = VectorMemberDetailsDevicesAdapter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## getView() : OnBlockDeviceClick fails ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
        return view;
    }
}
