package com.opengarden.firechat.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.VectorUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class VectorRoomCreationAdapter extends ArrayAdapter<ParticipantAdapterItem> {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorRoomCreationAdapter";
    private final int mAddMemberLayoutResourceId;
    private final Context mContext;
    private final ArrayList<String> mDisplayNamesList = new ArrayList<>();
    private final LayoutInflater mLayoutInflater;
    private final int mMemberLayoutResourceId;
    /* access modifiers changed from: private */
    public IRoomCreationAdapterListener mRoomCreationAdapterListener;
    private final MXSession mSession;

    public interface IRoomCreationAdapterListener {
        void OnRemoveParticipantClick(ParticipantAdapterItem participantAdapterItem);
    }

    public int getItemViewType(int i) {
        return i == 0 ? 0 : 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public VectorRoomCreationAdapter(Context context, int i, int i2, MXSession mXSession) {
        super(context, i2);
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mAddMemberLayoutResourceId = i;
        this.mMemberLayoutResourceId = i2;
        this.mSession = mXSession;
    }

    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.mDisplayNamesList.clear();
        for (int i = 0; i < getCount(); i++) {
            ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) getItem(i);
            if (!TextUtils.isEmpty(participantAdapterItem.mDisplayName)) {
                this.mDisplayNamesList.add(participantAdapterItem.mDisplayName.toLowerCase(VectorApp.getApplicationLocale()));
            }
        }
    }

    public void setRoomCreationAdapterListener(IRoomCreationAdapterListener iRoomCreationAdapterListener) {
        this.mRoomCreationAdapterListener = iRoomCreationAdapterListener;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i == 0) {
            if (view == null) {
                view = this.mLayoutInflater.inflate(this.mAddMemberLayoutResourceId, viewGroup, false);
            }
            return view;
        }
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mMemberLayoutResourceId, viewGroup, false);
        }
        final ParticipantAdapterItem participantAdapterItem = (ParticipantAdapterItem) getItem(i);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.filtered_list_name);
        TextView textView2 = (TextView) view.findViewById(C1299R.C1301id.filtered_list_status);
        ImageView imageView = (ImageView) view.findViewById(C1299R.C1301id.filtered_list_matrix_user);
        participantAdapterItem.displayAvatar(this.mSession, (ImageView) view.findViewById(C1299R.C1301id.filtered_list_avatar));
        textView.setText(VectorUtils.getPlainId(participantAdapterItem.getUniqueDisplayName(this.mDisplayNamesList)));
        String str = "";
        Iterator it = Matrix.getMXSessions(this.mContext).iterator();
        User user = null;
        MXSession mXSession = null;
        while (it.hasNext()) {
            MXSession mXSession2 = (MXSession) it.next();
            if (user == null) {
                user = mXSession2.getDataHandler().getUser(participantAdapterItem.mUserId);
                mXSession = mXSession2;
            }
        }
        if (user != null) {
            str = VectorUtils.getUserOnlineStatus(this.mContext, mXSession, participantAdapterItem.mUserId, new SimpleApiCallback<Void>() {
                public void onSuccess(Void voidR) {
                    VectorRoomCreationAdapter.this.notifyDataSetChanged();
                }
            });
        }
        if (participantAdapterItem.mContact != null) {
            textView2.setText(participantAdapterItem.mUserId);
            imageView.setVisibility(Patterns.EMAIL_ADDRESS.matcher(participantAdapterItem.mUserId).matches() ^ true ? 0 : 8);
        } else {
            textView2.setText(str);
            imageView.setVisibility(8);
        }
        ((CheckBox) view.findViewById(C1299R.C1301id.filtered_list_checkbox)).setVisibility(8);
        View findViewById = view.findViewById(C1299R.C1301id.filtered_list_remove_button);
        findViewById.setVisibility(0);
        findViewById.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (VectorRoomCreationAdapter.this.mRoomCreationAdapterListener != null) {
                    try {
                        VectorRoomCreationAdapter.this.mRoomCreationAdapterListener.OnRemoveParticipantClick(participantAdapterItem);
                    } catch (Exception e) {
                        String access$100 = VectorRoomCreationAdapter.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## getView() : OnRemoveParticipantClick fails ");
                        sb.append(e.getMessage());
                        Log.m211e(access$100, sb.toString());
                    }
                }
            }
        });
        return view;
    }
}
