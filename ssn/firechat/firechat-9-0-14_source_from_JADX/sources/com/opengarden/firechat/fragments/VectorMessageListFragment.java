package com.opengarden.firechat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.app.FragmentManager;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonElement;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.activity.CommonActivityUtils;
import com.opengarden.firechat.activity.MXCActionBarActivity;
import com.opengarden.firechat.activity.VectorHomeActivity;
import com.opengarden.firechat.activity.VectorMediasViewerActivity;
import com.opengarden.firechat.activity.VectorMemberDetailsActivity;
import com.opengarden.firechat.activity.VectorRoomActivity;
import com.opengarden.firechat.adapters.VectorMessagesAdapter;
import com.opengarden.firechat.listeners.IMessagesAdapterActionsListener;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter;
import com.opengarden.firechat.matrixsdk.adapters.MessageRow;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.RoomState;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessageListFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessagesFragment;
import com.opengarden.firechat.matrixsdk.listeners.MXMediaDownloadListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.Event;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedEventContent;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo;
import com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage;
import com.opengarden.firechat.matrixsdk.rest.model.message.Message;
import com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage;
import com.opengarden.firechat.matrixsdk.util.JsonUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.p005db.VectorContentProvider;
import com.opengarden.firechat.receiver.VectorUniversalLinkReceiver;
import com.opengarden.firechat.util.ExternalApplicationsUtilKt;
import com.opengarden.firechat.util.MatrixSdkExtensionsKt;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.SlidableMediaInfo;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorImageGetter;
import com.opengarden.firechat.util.VectorImageGetter.OnImageDownloadListener;
import com.opengarden.firechat.util.VectorUtils;
import com.opengarden.firechat.widgets.WidgetsManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VectorMessageListFragment extends MatrixMessageListFragment implements IMessagesAdapterActionsListener {
    private static final int ACTION_VECTOR_FORWARD = 2131296606;
    static final int ACTION_VECTOR_OPEN = 123456;
    private static final int ACTION_VECTOR_SAVE = 2131296613;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorMessageListFragment";
    private static final String TAG_FRAGMENT_RECEIPTS_DIALOG = "TAG_FRAGMENT_RECEIPTS_DIALOG";
    private static final String TAG_FRAGMENT_USER_GROUPS_DIALOG = "TAG_FRAGMENT_USER_GROUPS_DIALOG";
    private View mBackProgressView;
    /* access modifiers changed from: private */
    public final ApiCallback<Void> mDeviceVerificationCallback = new ApiCallback<Void>() {
        public void onSuccess(Void voidR) {
            VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
        }

        public void onNetworkError(Exception exc) {
            VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
        }

        public void onMatrixError(MatrixError matrixError) {
            VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
        }

        public void onUnexpectedError(Exception exc) {
            VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
        }
    };
    private View mForwardProgressView;
    private final HashMap<String, Boolean> mHighlightStatusByEventId = new HashMap<>();
    private IListFragmentEventListener mHostActivityListener;
    private int mInvalidIndexesCount = 0;
    private View mMainProgressView;
    private VectorImageGetter mVectorImageGetter;

    public interface IListFragmentEventListener {
        void onListTouch();
    }

    public void onMediaDownloaded(int i) {
    }

    public boolean onRowLongClick(int i) {
        return false;
    }

    public static VectorMessageListFragment newInstance(String str, String str2, String str3, String str4, int i) {
        VectorMessageListFragment vectorMessageListFragment = new VectorMessageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MatrixMessageListFragment.ARG_LAYOUT_ID, i);
        bundle.putString(MatrixMessageListFragment.ARG_MATRIX_ID, str);
        bundle.putString(MatrixMessageListFragment.ARG_ROOM_ID, str2);
        if (str3 != null) {
            bundle.putString(MatrixMessageListFragment.ARG_EVENT_ID, str3);
        }
        if (str4 != null) {
            bundle.putString(MatrixMessageListFragment.ARG_PREVIEW_MODE_ID, str4);
        }
        vectorMessageListFragment.setArguments(bundle);
        return vectorMessageListFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.m209d(LOG_TAG, "onCreateView");
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        Bundle arguments = getArguments();
        if (arguments.containsKey(MatrixMessageListFragment.ARG_EVENT_ID) && (this.mAdapter instanceof VectorMessagesAdapter)) {
            ((VectorMessagesAdapter) this.mAdapter).setSearchedEventId(arguments.getString(MatrixMessageListFragment.ARG_EVENT_ID, ""));
        }
        if (this.mRoom != null) {
            ((VectorMessagesAdapter) this.mAdapter).mIsRoomEncrypted = this.mRoom.isEncrypted();
        }
        if (this.mSession != null) {
            this.mVectorImageGetter = new VectorImageGetter(this.mSession);
            ((VectorMessagesAdapter) this.mAdapter).setImageGetter(this.mVectorImageGetter);
        }
        this.mMessageListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                VectorMessageListFragment.this.onRowClick(i);
            }
        });
        onCreateView.setBackgroundColor(ThemeUtils.INSTANCE.getColor(getActivity(), C1299R.attr.riot_primary_background_color));
        return onCreateView;
    }

    public MatrixMessagesFragment createMessagesFragmentInstance(String str) {
        return VectorMessagesFragment.newInstance(getSession(), str, this);
    }

    /* access modifiers changed from: protected */
    public String getMatrixMessagesFragmentTag() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName());
        sb.append(".MATRIX_MESSAGE_FRAGMENT_TAG");
        return sb.toString();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mHostActivityListener = (IListFragmentEventListener) activity;
        } catch (ClassCastException unused) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onAttach(): host activity does not implement IListFragmentEventListener ");
            sb.append(activity);
            Log.m217w(str, sb.toString());
            this.mHostActivityListener = null;
        }
        this.mBackProgressView = activity.findViewById(C1299R.C1301id.loading_room_paginate_back_progress);
        this.mForwardProgressView = activity.findViewById(C1299R.C1301id.loading_room_paginate_forward_progress);
        this.mMainProgressView = activity.findViewById(C1299R.C1301id.main_progress_layout);
    }

    public void onPause() {
        super.onPause();
        if (this.mAdapter instanceof VectorMessagesAdapter) {
            VectorMessagesAdapter vectorMessagesAdapter = (VectorMessagesAdapter) this.mAdapter;
            vectorMessagesAdapter.setVectorMessagesAdapterActionsListener(null);
            vectorMessagesAdapter.onPause();
        }
        this.mVectorImageGetter.setListener(null);
    }

    public void onResume() {
        super.onResume();
        if (this.mAdapter instanceof VectorMessagesAdapter) {
            ((VectorMessagesAdapter) this.mAdapter).setVectorMessagesAdapterActionsListener(this);
        }
        this.mVectorImageGetter.setListener(new OnImageDownloadListener() {
            public void onImageDownloaded(String str) {
                VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onDetach() {
        super.onDetach();
        this.mHostActivityListener = null;
        this.mBackProgressView = null;
        this.mForwardProgressView = null;
        this.mMainProgressView = null;
    }

    public MXSession getSession(String str) {
        return Matrix.getMXSession(getActivity(), str);
    }

    public MXMediasCache getMXMediasCache() {
        return Matrix.getInstance(getActivity()).getMediasCache();
    }

    public AbstractMessagesAdapter createMessagesAdapter() {
        return new VectorMessagesAdapter(this.mSession, getActivity(), getMXMediasCache());
    }

    public void onListTouch(MotionEvent motionEvent) {
        if (this.mCheckSlideToHide && motionEvent.getY() > ((float) this.mMessageListView.getHeight())) {
            this.mCheckSlideToHide = false;
            MXCActionBarActivity.dismissKeyboard(getActivity());
        }
        if (this.mHostActivityListener != null) {
            this.mHostActivityListener.onListTouch();
        }
    }

    /* access modifiers changed from: protected */
    public boolean canAddEvent(Event event) {
        return TextUtils.equals(WidgetsManager.WIDGET_EVENT_TYPE, event.getType()) || super.canAddEvent(event);
    }

    public void setIsRoomEncrypted(boolean z) {
        if (((VectorMessagesAdapter) this.mAdapter).mIsRoomEncrypted != z) {
            ((VectorMessagesAdapter) this.mAdapter).mIsRoomEncrypted = z;
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public ListView getMessageListView() {
        return this.mMessageListView;
    }

    public AbstractMessagesAdapter getMessageAdapter() {
        return this.mAdapter;
    }

    public void cancelSelectionMode() {
        if (this.mAdapter != null) {
            ((VectorMessagesAdapter) this.mAdapter).cancelSelectionMode();
        }
    }

    public void onE2eIconClick(final Event event, final MXDeviceInfo mXDeviceInfo) {
        Builder builder = new Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent().getAsJsonObject());
        View inflate = layoutInflater.inflate(C1299R.layout.encrypted_event_info, null);
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_user_id)).setText(event.getSender());
        TextView textView = (TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_curve25519_identity_key);
        if (mXDeviceInfo != null) {
            textView.setText(encryptedEventContent.sender_key);
        } else {
            textView.setText(getActivity().getString(C1299R.string.encryption_information_none));
        }
        TextView textView2 = (TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_claimed_ed25519_fingerprint_key);
        if (mXDeviceInfo != null) {
            textView2.setText(MatrixSdkExtensionsKt.getFingerprintHumanReadable(mXDeviceInfo));
        } else {
            textView2.setText(getActivity().getString(C1299R.string.encryption_information_none));
        }
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_algorithm)).setText(encryptedEventContent.algorithm);
        ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_session_id)).setText(encryptedEventContent.session_id);
        View findViewById = inflate.findViewById(C1299R.C1301id.encrypted_info_decryption_error_label);
        TextView textView3 = (TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_decryption_error);
        if (event.getCryptoError() != null) {
            findViewById.setVisibility(0);
            textView3.setVisibility(0);
            StringBuilder sb = new StringBuilder();
            sb.append("**");
            sb.append(event.getCryptoError().getLocalizedMessage());
            sb.append("**");
            textView3.setText(sb.toString());
        } else {
            findViewById.setVisibility(8);
            textView3.setVisibility(8);
        }
        View findViewById2 = inflate.findViewById(C1299R.C1301id.encrypted_info_no_device_information_layout);
        View findViewById3 = inflate.findViewById(C1299R.C1301id.encrypted_info_sender_device_information_layout);
        if (mXDeviceInfo != null) {
            findViewById2.setVisibility(8);
            findViewById3.setVisibility(0);
            ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_name)).setText(mXDeviceInfo.displayName());
            ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_device_id)).setText(mXDeviceInfo.deviceId);
            TextView textView4 = (TextView) inflate.findViewById(C1299R.C1301id.encrypted_info_verification);
            if (mXDeviceInfo.isUnknown() || mXDeviceInfo.isUnverified()) {
                textView4.setText(getActivity().getString(C1299R.string.encryption_information_not_verified));
            } else if (mXDeviceInfo.isVerified()) {
                textView4.setText(getActivity().getString(C1299R.string.encryption_information_verified));
            } else {
                textView4.setText(getActivity().getString(C1299R.string.encryption_information_blocked));
            }
            ((TextView) inflate.findViewById(C1299R.C1301id.encrypted_ed25519_fingerprint)).setText(MatrixSdkExtensionsKt.getFingerprintHumanReadable(mXDeviceInfo));
        } else {
            findViewById2.setVisibility(0);
            findViewById3.setVisibility(8);
        }
        builder.setView(inflate);
        builder.setTitle((int) C1299R.string.encryption_information_title);
        builder.setNeutralButton((int) C1299R.string.f115ok, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        if (!TextUtils.equals(encryptedEventContent.device_id, this.mSession.getCredentials().deviceId) && event.getCryptoError() == null && mXDeviceInfo != null) {
            if (mXDeviceInfo.isUnverified() || mXDeviceInfo.isUnknown()) {
                this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, event.getSender(), this.mDeviceVerificationCallback);
            } else if (mXDeviceInfo.isVerified()) {
                builder.setNegativeButton((int) C1299R.string.encryption_information_unverify, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VectorMessageListFragment.this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, event.getSender(), VectorMessageListFragment.this.mDeviceVerificationCallback);
                    }
                });
                builder.setPositiveButton((int) C1299R.string.encryption_information_block, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VectorMessageListFragment.this.mSession.getCrypto().setDeviceVerification(2, mXDeviceInfo.deviceId, event.getSender(), VectorMessageListFragment.this.mDeviceVerificationCallback);
                    }
                });
            } else {
                builder.setNegativeButton((int) C1299R.string.encryption_information_verify, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CommonActivityUtils.displayDeviceVerificationDialog(mXDeviceInfo, event.getSender(), VectorMessageListFragment.this.mSession, VectorMessageListFragment.this.getActivity(), VectorMessageListFragment.this.mDeviceVerificationCallback);
                    }
                });
                builder.setPositiveButton((int) C1299R.string.encryption_information_unblock, (OnClickListener) new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VectorMessageListFragment.this.mSession.getCrypto().setDeviceVerification(0, mXDeviceInfo.deviceId, event.getSender(), VectorMessageListFragment.this.mDeviceVerificationCallback);
                    }
                });
            }
        }
        final AlertDialog create = builder.create();
        create.show();
        if (mXDeviceInfo == null) {
            this.mSession.getCrypto().getDeviceList().downloadKeys(Collections.singletonList(event.getSender()), true, new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
                public void onMatrixError(MatrixError matrixError) {
                }

                public void onNetworkError(Exception exc) {
                }

                public void onUnexpectedError(Exception exc) {
                }

                public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                    FragmentActivity activity = VectorMessageListFragment.this.getActivity();
                    if (activity != null && !activity.isFinishing() && create.isShowing()) {
                        EncryptedEventContent encryptedEventContent = JsonUtils.toEncryptedEventContent(event.getWireContent().getAsJsonObject());
                        MXDeviceInfo deviceWithIdentityKey = VectorMessageListFragment.this.mSession.getCrypto().deviceWithIdentityKey(encryptedEventContent.sender_key, event.getSender(), encryptedEventContent.algorithm);
                        if (deviceWithIdentityKey != null) {
                            create.cancel();
                            VectorMessageListFragment.this.onE2eIconClick(event, deviceWithIdentityKey);
                        }
                    }
                }
            });
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0167  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onEventAction(final com.opengarden.firechat.matrixsdk.rest.model.Event r13, final java.lang.String r14, final int r15) {
        /*
            r12 = this;
            r0 = 2131296611(0x7f090163, float:1.8211144E38)
            if (r15 != r0) goto L_0x0013
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            com.opengarden.firechat.fragments.VectorMessageListFragment$10 r15 = new com.opengarden.firechat.fragments.VectorMessageListFragment$10
            r15.<init>(r13)
            r14.runOnUiThread(r15)
            goto L_0x019b
        L_0x0013:
            r0 = 2131296609(0x7f090161, float:1.821114E38)
            if (r15 != r0) goto L_0x0026
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            com.opengarden.firechat.fragments.VectorMessageListFragment$11 r15 = new com.opengarden.firechat.fragments.VectorMessageListFragment$11
            r15.<init>(r13)
            r14.runOnUiThread(r15)
            goto L_0x019b
        L_0x0026:
            r0 = 2131296603(0x7f09015b, float:1.8211127E38)
            if (r15 != r0) goto L_0x0039
            android.support.v4.app.FragmentActivity r13 = r12.getActivity()
            com.opengarden.firechat.fragments.VectorMessageListFragment$12 r15 = new com.opengarden.firechat.fragments.VectorMessageListFragment$12
            r15.<init>(r14)
            r13.runOnUiThread(r15)
            goto L_0x019b
        L_0x0039:
            r0 = 2131296602(0x7f09015a, float:1.8211125E38)
            if (r15 == r0) goto L_0x018f
            r0 = 2131296601(0x7f090159, float:1.8211123E38)
            if (r15 != r0) goto L_0x0045
            goto L_0x018f
        L_0x0045:
            r0 = 2131296608(0x7f090160, float:1.8211137E38)
            if (r15 != r0) goto L_0x00b3
            android.support.v4.app.FragmentActivity r13 = r12.getActivity()
            if (r13 == 0) goto L_0x019b
            boolean r15 = r13 instanceof com.opengarden.firechat.activity.VectorRoomActivity
            if (r15 == 0) goto L_0x019b
            java.lang.String r15 = "\n\n"
            java.lang.String[] r14 = r14.split(r15)
            java.lang.String r15 = ""
            r0 = 0
        L_0x005d:
            int r1 = r14.length
            if (r0 >= r1) goto L_0x009b
            r1 = r14[r0]
            java.lang.String r1 = r1.trim()
            java.lang.String r2 = ""
            boolean r1 = r1.equals(r2)
            if (r1 != 0) goto L_0x0084
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            java.lang.String r15 = "> "
            r1.append(r15)
            r15 = r14[r0]
            r1.append(r15)
            java.lang.String r15 = r1.toString()
        L_0x0084:
            int r0 = r0 + 1
            int r1 = r14.length
            if (r0 == r1) goto L_0x005d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            java.lang.String r15 = "\n\n"
            r1.append(r15)
            java.lang.String r15 = r1.toString()
            goto L_0x005d
        L_0x009b:
            com.opengarden.firechat.activity.VectorRoomActivity r13 = (com.opengarden.firechat.activity.VectorRoomActivity) r13
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r15)
            java.lang.String r15 = "\n\n"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            r13.insertQuoteInTextEditor(r14)
            goto L_0x019b
        L_0x00b3:
            r1 = 2131296615(0x7f090167, float:1.8211152E38)
            r2 = 2131296606(0x7f09015e, float:1.8211133E38)
            if (r15 == r1) goto L_0x0110
            if (r15 == r2) goto L_0x0110
            r3 = 2131296613(0x7f090165, float:1.8211148E38)
            if (r15 != r3) goto L_0x00c3
            goto L_0x0110
        L_0x00c3:
            r14 = 2131296607(0x7f09015f, float:1.8211135E38)
            if (r15 != r14) goto L_0x00d9
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            java.lang.String r15 = r13.roomId
            java.lang.String r13 = r13.eventId
            java.lang.String r13 = com.opengarden.firechat.util.VectorUtils.getPermalink(r15, r13)
            com.opengarden.firechat.util.VectorUtils.copyToClipboard(r14, r13)
            goto L_0x019b
        L_0x00d9:
            r14 = 2131296610(0x7f090162, float:1.8211142E38)
            if (r15 != r14) goto L_0x00e3
            r12.onMessageReport(r13)
            goto L_0x019b
        L_0x00e3:
            r14 = 2131296618(0x7f09016a, float:1.8211158E38)
            if (r15 == r14) goto L_0x0102
            r14 = 2131296617(0x7f090169, float:1.8211156E38)
            if (r15 != r14) goto L_0x00ee
            goto L_0x0102
        L_0x00ee:
            r14 = 2131296576(0x7f090140, float:1.8211073E38)
            if (r15 != r14) goto L_0x019b
            com.opengarden.firechat.matrixsdk.adapters.AbstractMessagesAdapter r14 = r12.mAdapter
            com.opengarden.firechat.adapters.VectorMessagesAdapter r14 = (com.opengarden.firechat.adapters.VectorMessagesAdapter) r14
            java.lang.String r15 = r13.eventId
            com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo r14 = r14.getDeviceInfo(r15)
            r12.onE2eIconClick(r13, r14)
            goto L_0x019b
        L_0x0102:
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            com.opengarden.firechat.fragments.VectorMessageListFragment$14 r0 = new com.opengarden.firechat.fragments.VectorMessageListFragment$14
            r0.<init>(r15, r13)
            r14.runOnUiThread(r0)
            goto L_0x019b
        L_0x0110:
            com.google.gson.JsonElement r13 = r13.getContent()
            com.opengarden.firechat.matrixsdk.rest.model.message.Message r13 = com.opengarden.firechat.matrixsdk.util.JsonUtils.toMessage(r13)
            boolean r3 = r13 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage
            r4 = 0
            if (r3 == 0) goto L_0x012e
            r3 = r13
            com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage r3 = (com.opengarden.firechat.matrixsdk.rest.model.message.ImageMessage) r3
            java.lang.String r4 = r3.getUrl()
            java.lang.String r5 = r3.getMimeType()
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r3 = r3.file
        L_0x012a:
            r11 = r3
            r8 = r4
            r9 = r5
            goto L_0x015d
        L_0x012e:
            boolean r3 = r13 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage
            if (r3 == 0) goto L_0x0148
            r3 = r13
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage r3 = (com.opengarden.firechat.matrixsdk.rest.model.message.VideoMessage) r3
            java.lang.String r5 = r3.getUrl()
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r6 = r3.file
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r7 = r3.info
            if (r7 == 0) goto L_0x0144
            com.opengarden.firechat.matrixsdk.rest.model.message.VideoInfo r3 = r3.info
            java.lang.String r3 = r3.mimetype
            r4 = r3
        L_0x0144:
            r9 = r4
            r8 = r5
            r11 = r6
            goto L_0x015d
        L_0x0148:
            boolean r3 = r13 instanceof com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage
            if (r3 == 0) goto L_0x015a
            r3 = r13
            com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage r3 = (com.opengarden.firechat.matrixsdk.rest.model.message.FileMessage) r3
            java.lang.String r4 = r3.getUrl()
            java.lang.String r5 = r3.getMimeType()
            com.opengarden.firechat.matrixsdk.rest.model.crypto.EncryptedFileInfo r3 = r3.file
            goto L_0x012a
        L_0x015a:
            r8 = r4
            r9 = r8
            r11 = r9
        L_0x015d:
            if (r8 == 0) goto L_0x0167
            java.lang.String r10 = r13.body
            r6 = r12
            r7 = r15
            r6.onMediaAction(r7, r8, r9, r10, r11)
            goto L_0x019b
        L_0x0167:
            if (r15 == r1) goto L_0x016d
            if (r15 == r2) goto L_0x016d
            if (r15 != r0) goto L_0x019b
        L_0x016d:
            android.content.Intent r13 = new android.content.Intent
            r13.<init>()
            java.lang.String r0 = "android.intent.action.SEND"
            r13.setAction(r0)
            java.lang.String r0 = "android.intent.extra.TEXT"
            r13.putExtra(r0, r14)
            java.lang.String r14 = "text/plain"
            r13.setType(r14)
            if (r15 != r2) goto L_0x018b
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            com.opengarden.firechat.activity.CommonActivityUtils.sendFilesTo(r14, r13)
            goto L_0x019b
        L_0x018b:
            r12.startActivity(r13)
            goto L_0x019b
        L_0x018f:
            android.support.v4.app.FragmentActivity r14 = r12.getActivity()
            com.opengarden.firechat.fragments.VectorMessageListFragment$13 r0 = new com.opengarden.firechat.fragments.VectorMessageListFragment$13
            r0.<init>(r15, r13)
            r14.runOnUiThread(r0)
        L_0x019b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.fragments.VectorMessageListFragment.onEventAction(com.opengarden.firechat.matrixsdk.rest.model.Event, java.lang.String, int):void");
    }

    private void onMessageReport(final Event event) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle(C1299R.string.room_event_action_report_prompt_reason);
        final EditText editText = new EditText(getActivity());
        builder.setView(editText);
        builder.setPositiveButton(C1299R.string.f115ok, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                VectorMessageListFragment.this.mRoom.report(event.eventId, -100, editText.getText().toString(), new SimpleApiCallback<Void>(VectorMessageListFragment.this.getActivity()) {
                    public void onSuccess(Void voidR) {
                        new android.app.AlertDialog.Builder(VectorMessageListFragment.this.getActivity()).setMessage(C1299R.string.room_event_action_report_prompt_ignore_user).setPositiveButton(C1299R.string.yes, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ArrayList arrayList = new ArrayList();
                                arrayList.add(event.sender);
                                VectorMessageListFragment.this.mSession.ignoreUsers(arrayList, new SimpleApiCallback<Void>() {
                                    public void onSuccess(Void voidR) {
                                    }
                                });
                            }
                        }).setNegativeButton(C1299R.string.f114no, new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    }
                });
            }
        });
        builder.setNegativeButton(C1299R.string.cancel, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /* access modifiers changed from: 0000 */
    public void onMediaAction(final int i, String str, final String str2, String str3, EncryptedFileInfo encryptedFileInfo) {
        final String name = new File(str3).getName();
        MXMediasCache mediasCache = Matrix.getInstance(getActivity()).getMediasCache();
        if (mediasCache.isMediaCached(str, str2)) {
            mediasCache.createTmpMediaFile(str, str2, encryptedFileInfo, new SimpleApiCallback<File>() {
                public void onSuccess(File file) {
                    Parcelable parcelable;
                    if (file != null) {
                        if (i == C1299R.C1301id.ic_action_vector_save || i == VectorMessageListFragment.ACTION_VECTOR_OPEN) {
                            CommonActivityUtils.saveMediaIntoDownloads(VectorMessageListFragment.this.getActivity(), file, name, str2, new SimpleApiCallback<String>() {
                                public void onSuccess(String str) {
                                    if (str == null) {
                                        return;
                                    }
                                    if (i == C1299R.C1301id.ic_action_vector_save) {
                                        Toast.makeText(VectorMessageListFragment.this.getActivity(), VectorMessageListFragment.this.getText(C1299R.string.media_slider_saved), 1).show();
                                    } else {
                                        CommonActivityUtils.openMedia(VectorMessageListFragment.this.getActivity(), str, str2);
                                    }
                                }
                            });
                        } else {
                            if (name != null) {
                                File file2 = new File(file.getParent(), name);
                                if (file2.exists()) {
                                    file2.delete();
                                }
                                file.renameTo(file2);
                                file = file2;
                            }
                            try {
                                parcelable = VectorContentProvider.absolutePathToUri(VectorMessageListFragment.this.getActivity(), file.getAbsolutePath());
                            } catch (Exception e) {
                                String access$2100 = VectorMessageListFragment.LOG_TAG;
                                StringBuilder sb = new StringBuilder();
                                sb.append("onMediaAction VectorContentProvider.absolutePathToUri: ");
                                sb.append(e.getMessage());
                                Log.m211e(access$2100, sb.toString());
                                parcelable = null;
                            }
                            if (parcelable != null) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.SEND");
                                intent.setType(str2);
                                intent.putExtra("android.intent.extra.STREAM", parcelable);
                                if (i == C1299R.C1301id.ic_action_vector_forward) {
                                    CommonActivityUtils.sendFilesTo(VectorMessageListFragment.this.getActivity(), intent);
                                } else {
                                    VectorMessageListFragment.this.startActivity(intent);
                                }
                            }
                        }
                    }
                }
            });
            return;
        }
        String downloadMedia = mediasCache.downloadMedia(getActivity().getApplicationContext(), this.mSession.getHomeServerConfig(), str, str2, encryptedFileInfo);
        this.mAdapter.notifyDataSetChanged();
        if (downloadMedia != null) {
            final String str4 = downloadMedia;
            final int i2 = i;
            final String str5 = str;
            final String str6 = str2;
            final EncryptedFileInfo encryptedFileInfo2 = encryptedFileInfo;
            C209118 r1 = new MXMediaDownloadListener() {
                public void onDownloadError(String str, JsonElement jsonElement) {
                    MatrixError matrixError = JsonUtils.toMatrixError(jsonElement);
                    if (matrixError != null && matrixError.isSupportedErrorCode() && VectorMessageListFragment.this.getActivity() != null) {
                        Toast.makeText(VectorMessageListFragment.this.getActivity(), matrixError.getLocalizedMessage(), 1).show();
                    }
                }

                public void onDownloadComplete(String str) {
                    if (str.equals(str4)) {
                        VectorMessageListFragment.this.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                VectorMessageListFragment.this.onMediaAction(i2, str5, str6, name, encryptedFileInfo2);
                            }
                        });
                    }
                }
            };
            mediasCache.addDownloadListener(downloadMedia, r1);
        }
    }

    public boolean isDisplayAllEvents() {
        return PreferencesManager.displayAllEvents(getActivity());
    }

    private void setViewVisibility(View view, int i) {
        if (view != null && getActivity() != null) {
            view.setVisibility(i);
        }
    }

    public void showLoadingBackProgress() {
        setViewVisibility(this.mBackProgressView, 0);
    }

    public void hideLoadingBackProgress() {
        setViewVisibility(this.mBackProgressView, 8);
    }

    public void showLoadingForwardProgress() {
        setViewVisibility(this.mForwardProgressView, 0);
    }

    public void hideLoadingForwardProgress() {
        setViewVisibility(this.mForwardProgressView, 8);
    }

    public void showInitLoading() {
        setViewVisibility(this.mMainProgressView, 0);
    }

    public void hideInitLoading() {
        setViewVisibility(this.mMainProgressView, 8);
    }

    /* access modifiers changed from: 0000 */
    public ArrayList<SlidableMediaInfo> listSlidableMessages() {
        ArrayList<SlidableMediaInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            Message message = JsonUtils.toMessage(((MessageRow) this.mAdapter.getItem(i)).getEvent().getContent());
            if (Message.MSGTYPE_IMAGE.equals(message.msgtype)) {
                ImageMessage imageMessage = (ImageMessage) message;
                SlidableMediaInfo slidableMediaInfo = new SlidableMediaInfo();
                slidableMediaInfo.mMessageType = Message.MSGTYPE_IMAGE;
                slidableMediaInfo.mFileName = imageMessage.body;
                slidableMediaInfo.mMediaUrl = imageMessage.getUrl();
                slidableMediaInfo.mRotationAngle = imageMessage.getRotation();
                slidableMediaInfo.mOrientation = imageMessage.getOrientation();
                slidableMediaInfo.mMimeType = imageMessage.getMimeType();
                slidableMediaInfo.mEncryptedFileInfo = imageMessage.file;
                arrayList.add(slidableMediaInfo);
            } else if (Message.MSGTYPE_VIDEO.equals(message.msgtype)) {
                VideoMessage videoMessage = (VideoMessage) message;
                SlidableMediaInfo slidableMediaInfo2 = new SlidableMediaInfo();
                slidableMediaInfo2.mMessageType = Message.MSGTYPE_VIDEO;
                slidableMediaInfo2.mFileName = videoMessage.body;
                slidableMediaInfo2.mMediaUrl = videoMessage.getUrl();
                slidableMediaInfo2.mThumbnailUrl = videoMessage.info != null ? videoMessage.info.thumbnail_url : null;
                slidableMediaInfo2.mMimeType = videoMessage.getMimeType();
                slidableMediaInfo2.mEncryptedFileInfo = videoMessage.file;
                arrayList.add(slidableMediaInfo2);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public int getMediaMessagePosition(ArrayList<SlidableMediaInfo> arrayList, Message message) {
        String str = message instanceof ImageMessage ? ((ImageMessage) message).getUrl() : message instanceof VideoMessage ? ((VideoMessage) message).getUrl() : null;
        if (str == null) {
            return -1;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (((SlidableMediaInfo) arrayList.get(i)).mMediaUrl.equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public void onRowClick(int i) {
        try {
            ((VectorMessagesAdapter) this.mAdapter).onEventTap(((MessageRow) this.mAdapter.getItem(i)).getEvent().eventId);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onRowClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void onContentClick(int i) {
        try {
            Event event = ((MessageRow) this.mAdapter.getItem(i)).getEvent();
            VectorMessagesAdapter vectorMessagesAdapter = (VectorMessagesAdapter) this.mAdapter;
            if (vectorMessagesAdapter.isInSelectionMode()) {
                vectorMessagesAdapter.onEventTap(null);
                return;
            }
            Message message = JsonUtils.toMessage(event.getContent());
            if (!Message.MSGTYPE_IMAGE.equals(message.msgtype)) {
                if (!Message.MSGTYPE_VIDEO.equals(message.msgtype)) {
                    if (!Message.MSGTYPE_FILE.equals(message.msgtype)) {
                        if (!Message.MSGTYPE_AUDIO.equals(message.msgtype)) {
                            vectorMessagesAdapter.onEventTap(event.eventId);
                        }
                    }
                    FileMessage fileMessage = JsonUtils.toFileMessage(event.getContent());
                    if (fileMessage.getUrl() != null) {
                        onMediaAction(ACTION_VECTOR_OPEN, fileMessage.getUrl(), fileMessage.getMimeType(), fileMessage.body, fileMessage.file);
                    }
                }
            }
            ArrayList listSlidableMessages = listSlidableMessages();
            int mediaMessagePosition = getMediaMessagePosition(listSlidableMessages, message);
            if (mediaMessagePosition >= 0) {
                Intent intent = new Intent(getActivity(), VectorMediasViewerActivity.class);
                intent.putExtra(VectorMediasViewerActivity.EXTRA_MATRIX_ID, this.mSession.getCredentials().userId);
                intent.putExtra(VectorMediasViewerActivity.KEY_THUMBNAIL_WIDTH, this.mAdapter.getMaxThumbnailWidth());
                intent.putExtra(VectorMediasViewerActivity.KEY_THUMBNAIL_HEIGHT, this.mAdapter.getMaxThumbnailHeight());
                intent.putExtra(VectorMediasViewerActivity.KEY_INFO_LIST, listSlidableMessages);
                intent.putExtra(VectorMediasViewerActivity.KEY_INFO_LIST_INDEX, mediaMessagePosition);
                getActivity().startActivity(intent);
            }
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onContentClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public boolean onContentLongClick(int i) {
        return onRowLongClick(i);
    }

    public void onAvatarClick(String str) {
        try {
            Intent intent = new Intent(getActivity(), VectorMemberDetailsActivity.class);
            if (getRoomPreviewData() != null) {
                intent.putExtra(VectorMemberDetailsActivity.EXTRA_STORE_ID, new Integer(Matrix.getInstance(getActivity()).addTmpStore(this.mEventTimeLine.getStore())));
            }
            intent.putExtra("EXTRA_ROOM_ID", this.mRoom.getRoomId());
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, str);
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            getActivity().startActivityForResult(intent, 2);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onAvatarClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public boolean onAvatarLongClick(String str) {
        if (getActivity() instanceof VectorRoomActivity) {
            try {
                RoomState liveState = this.mRoom.getLiveState();
                if (liveState != null) {
                    String memberName = liveState.getMemberName(str);
                    if (!TextUtils.isEmpty(memberName)) {
                        ((VectorRoomActivity) getActivity()).insertUserDisplayNameInTextEditor(memberName);
                    }
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onAvatarLongClick() failed ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        return true;
    }

    public void onSenderNameClick(String str, String str2) {
        if (getActivity() instanceof VectorRoomActivity) {
            try {
                ((VectorRoomActivity) getActivity()).insertUserDisplayNameInTextEditor(str2);
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onSenderNameClick() failed ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
            }
        }
    }

    public void onMoreReadReceiptClick(String str) {
        try {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            VectorReadReceiptsDialogFragment vectorReadReceiptsDialogFragment = (VectorReadReceiptsDialogFragment) supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_RECEIPTS_DIALOG);
            if (vectorReadReceiptsDialogFragment != null) {
                vectorReadReceiptsDialogFragment.dismissAllowingStateLoss();
            }
            VectorReadReceiptsDialogFragment.newInstance(this.mSession.getMyUserId(), this.mRoom.getRoomId(), str).show(supportFragmentManager, TAG_FRAGMENT_RECEIPTS_DIALOG);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onMoreReadReceiptClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onGroupFlairClick(String str, List<String> list) {
        try {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            VectorUserGroupsDialogFragment vectorUserGroupsDialogFragment = (VectorUserGroupsDialogFragment) supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_USER_GROUPS_DIALOG);
            if (vectorUserGroupsDialogFragment != null) {
                vectorUserGroupsDialogFragment.dismissAllowingStateLoss();
            }
            VectorUserGroupsDialogFragment.newInstance(this.mSession.getMyUserId(), str, list).show(supportFragmentManager, TAG_FRAGMENT_USER_GROUPS_DIALOG);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onGroupFlairClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onURLClick(Uri uri) {
        if (uri != null) {
            try {
                HashMap parseUniversalLink = VectorUniversalLinkReceiver.parseUniversalLink(uri);
                if (parseUniversalLink == null) {
                    ExternalApplicationsUtilKt.openUrlInExternalBrowser((Context) getActivity(), uri);
                } else if (parseUniversalLink.containsKey(VectorUniversalLinkReceiver.ULINK_MATRIX_USER_ID_KEY)) {
                    Intent intent = new Intent(getActivity(), VectorMemberDetailsActivity.class);
                    intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, (String) parseUniversalLink.get(VectorUniversalLinkReceiver.ULINK_MATRIX_USER_ID_KEY));
                    intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
                    getActivity().startActivityForResult(intent, 2);
                } else {
                    Intent intent2 = new Intent(getActivity(), VectorHomeActivity.class);
                    intent2.setFlags(603979776);
                    intent2.putExtra(VectorHomeActivity.EXTRA_JUMP_TO_UNIVERSAL_LINK, uri);
                    getActivity().startActivity(intent2);
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## onURLClick() failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    public void onMatrixUserIdClick(String str) {
        try {
            Intent intent = new Intent(getActivity(), VectorMemberDetailsActivity.class);
            intent.putExtra("EXTRA_ROOM_ID", this.mRoom.getRoomId());
            intent.putExtra(VectorMemberDetailsActivity.EXTRA_MEMBER_ID, str);
            intent.putExtra("MXCActionBarActivity.EXTRA_MATRIX_ID", this.mSession.getCredentials().userId);
            startActivity(intent);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## onMatrixUserIdClick() failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onRoomAliasClick(String str) {
        try {
            onURLClick(Uri.parse(VectorUtils.getPermalink(str, null)));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRoomAliasClick failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onRoomIdClick(String str) {
        try {
            onURLClick(Uri.parse(VectorUtils.getPermalink(str, null)));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRoomIdClick failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onMessageIdClick(String str) {
        try {
            onURLClick(Uri.parse(VectorUtils.getPermalink(this.mRoom.getRoomId(), str)));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRoomIdClick failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onGroupIdClick(String str) {
        try {
            onURLClick(Uri.parse(VectorUtils.getPermalink(str, null)));
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onRoomIdClick failed ");
            sb.append(e.getLocalizedMessage());
            Log.m211e(str2, sb.toString());
        }
    }

    public void onInvalidIndexes() {
        this.mInvalidIndexesCount++;
        if (1 == this.mInvalidIndexesCount) {
            this.mMessageListView.post(new Runnable() {
                public void run() {
                    VectorMessageListFragment.this.mAdapter.notifyDataSetChanged();
                }
            });
        } else {
            this.mMessageListView.post(new Runnable() {
                public void run() {
                    if (VectorMessageListFragment.this.getActivity() != null) {
                        VectorMessageListFragment.this.getActivity().finish();
                    }
                }
            });
        }
    }

    public boolean shouldHighlightEvent(Event event) {
        boolean z = false;
        if (event == null || event.eventId == null) {
            return false;
        }
        String str = event.eventId;
        Boolean bool = (Boolean) this.mHighlightStatusByEventId.get(str);
        if (bool != null) {
            return bool.booleanValue();
        }
        if (this.mSession.getDataHandler().getBingRulesManager().fulfilledHighlightBingRule(event) != null) {
            z = true;
        }
        this.mHighlightStatusByEventId.put(str, Boolean.valueOf(z));
        return z;
    }
}
