package com.opengarden.firechat.contacts;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.contacts.Contact.MXID;
import com.opengarden.firechat.contacts.Contact.PhoneNumber;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PIDsRetriever {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "PIDsRetriever";
    private static PIDsRetriever mPIDsRetriever;
    /* access modifiers changed from: private */
    public PIDsRetrieverListener mListener = null;
    /* access modifiers changed from: private */
    public final HashMap<String, MXID> mMatrixIdsByMedium = new HashMap<>();

    public interface PIDsRetrieverListener {
        void onFailure(String str);

        void onSuccess(String str);
    }

    public static PIDsRetriever getInstance() {
        if (mPIDsRetriever == null) {
            mPIDsRetriever = new PIDsRetriever();
        }
        return mPIDsRetriever;
    }

    public void setPIDsRetrieverListener(PIDsRetrieverListener pIDsRetrieverListener) {
        this.mListener = pIDsRetrieverListener;
    }

    public void onAppBackgrounded() {
        this.mMatrixIdsByMedium.clear();
    }

    public void reset() {
        this.mMatrixIdsByMedium.clear();
        this.mListener = null;
    }

    public MXID getMXID(String str) {
        if (str == null || !this.mMatrixIdsByMedium.containsKey(str)) {
            return null;
        }
        MXID mxid = (MXID) this.mMatrixIdsByMedium.get(str);
        if (mxid.mMatrixId == null) {
            return null;
        }
        return mxid;
    }

    /* access modifiers changed from: private */
    public Set<String> retrieveMatrixIds(List<Contact> list) {
        HashSet hashSet = new HashSet();
        for (Contact contact : list) {
            for (String str : contact.getEmails()) {
                if (this.mMatrixIdsByMedium.containsKey(str)) {
                    MXID mxid = (MXID) this.mMatrixIdsByMedium.get(str);
                    if (mxid != null) {
                        contact.put(str, mxid);
                    }
                } else {
                    hashSet.add(str);
                }
            }
            for (PhoneNumber phoneNumber : contact.getPhonenumbers()) {
                if (this.mMatrixIdsByMedium.containsKey(phoneNumber.mMsisdnPhoneNumber)) {
                    MXID mxid2 = (MXID) this.mMatrixIdsByMedium.get(phoneNumber.mMsisdnPhoneNumber);
                    if (mxid2 != null) {
                        contact.put(phoneNumber.mMsisdnPhoneNumber, mxid2);
                    }
                } else {
                    hashSet.add(phoneNumber.mMsisdnPhoneNumber);
                }
            }
        }
        hashSet.remove(null);
        return hashSet;
    }

    public void retrieveMatrixIds(final Context context, final List<Contact> list, boolean z) {
        String str = LOG_TAG;
        Locale applicationLocale = VectorApp.getApplicationLocale();
        String str2 = "retrieveMatrixIds starts for %d contacts";
        Object[] objArr = new Object[1];
        objArr[0] = Integer.valueOf(list == null ? 0 : list.size());
        Log.m209d(str, String.format(applicationLocale, str2, objArr));
        if (list == null || list.size() == 0) {
            if (this.mListener != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        MXSession defaultSession = Matrix.getInstance(context.getApplicationContext()).getDefaultSession();
                        if (defaultSession != null) {
                            PIDsRetriever.this.mListener.onSuccess(defaultSession.getMyUserId());
                        }
                    }
                });
            }
            return;
        }
        Set<String> retrieveMatrixIds = retrieveMatrixIds(list);
        if (!z && !retrieveMatrixIds.isEmpty()) {
            HashMap hashMap = new HashMap();
            for (String str3 : retrieveMatrixIds) {
                if (str3 != null) {
                    if (Patterns.EMAIL_ADDRESS.matcher(str3).matches()) {
                        hashMap.put(str3, "email");
                    } else {
                        hashMap.put(str3, ThreePid.MEDIUM_MSISDN);
                    }
                }
            }
            final ArrayList arrayList = new ArrayList(hashMap.keySet());
            ArrayList arrayList2 = new ArrayList(hashMap.values());
            for (MXSession mXSession : Matrix.getInstance(context.getApplicationContext()).getSessions()) {
                final String str4 = mXSession.getCredentials().userId;
                mXSession.lookup3Pids(arrayList, arrayList2, new ApiCallback<List<String>>() {
                    public void onSuccess(List<String> list) {
                        String access$100 = PIDsRetriever.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("lookup3Pids success ");
                        sb.append(list.size());
                        Log.m211e(access$100, sb.toString());
                        for (int i = 0; i < arrayList.size(); i++) {
                            String str = (String) arrayList.get(i);
                            String str2 = (String) list.get(i);
                            if (!TextUtils.isEmpty(str2)) {
                                PIDsRetriever.this.mMatrixIdsByMedium.put(str, new MXID(str2, str4));
                            }
                        }
                        PIDsRetriever.this.retrieveMatrixIds(list);
                        if (PIDsRetriever.this.mListener != null) {
                            PIDsRetriever.this.mListener.onSuccess(str4);
                        }
                    }

                    private void onError(String str) {
                        String access$100 = PIDsRetriever.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## retrieveMatrixIds() : failed ");
                        sb.append(str);
                        Log.m211e(access$100, sb.toString());
                        if (PIDsRetriever.this.mListener != null) {
                            PIDsRetriever.this.mListener.onFailure(str4);
                        }
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
    }
}
