package com.opengarden.firechat.gcm;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.Log;

class GCMHelper {
    private static final String LOG_TAG = "GCMHelper";

    GCMHelper() {
    }

    public static String getRegistrationToken() {
        String str = null;
        try {
            if (VectorApp.getInstance() == null) {
                Log.m211e(LOG_TAG, "## getRegistrationToken() : No active application");
            } else if (FirebaseApp.initializeApp(VectorApp.getInstance()) == null) {
                Log.m211e(LOG_TAG, "## getRegistrationToken() : cannot initialise FirebaseApp");
            }
            String token = FirebaseInstanceId.getInstance().getToken();
            try {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getRegistrationToken(): ");
                sb.append(token);
                Log.m209d(str2, sb.toString());
                return token;
            } catch (Exception e) {
                String str3 = token;
                e = e;
                str = str3;
                String str4 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## getRegistrationToken() : failed ");
                sb2.append(e.getMessage());
                Log.m211e(str4, sb2.toString());
                return str;
            }
        } catch (Exception e2) {
            e = e2;
            String str42 = LOG_TAG;
            StringBuilder sb22 = new StringBuilder();
            sb22.append("## getRegistrationToken() : failed ");
            sb22.append(e.getMessage());
            Log.m211e(str42, sb22.toString());
            return str;
        }
    }

    public static void clearRegistrationToken() {
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("##clearRegistrationToken() failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }
}
