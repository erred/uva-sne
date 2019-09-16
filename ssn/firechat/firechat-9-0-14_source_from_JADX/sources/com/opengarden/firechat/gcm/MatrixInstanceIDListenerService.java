package com.opengarden.firechat.gcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.util.Log;

public class MatrixInstanceIDListenerService extends FirebaseInstanceIdService {
    private static final String LOG_TAG = "MatrixInstanceIDListenerService";

    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("onTokenRefresh ");
        sb.append(token);
        Log.m209d(str, sb.toString());
        Matrix.getInstance(this).getSharedGCMRegistrationManager().resetGCMRegistration(token);
    }
}
