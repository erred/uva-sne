package com.opengarden.firechat.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessagesFragment;
import com.opengarden.firechat.matrixsdk.fragments.MatrixMessagesFragment.MatrixMessagesListener;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.util.Log;

public class VectorMessagesFragment extends MatrixMessagesFragment {
    private static final String LOG_TAG = "VectorMessagesFragment";

    public static VectorMessagesFragment newInstance(MXSession mXSession, String str, MatrixMessagesListener matrixMessagesListener) {
        VectorMessagesFragment vectorMessagesFragment = new VectorMessagesFragment();
        Bundle bundle = new Bundle();
        if (matrixMessagesListener == null) {
            throw new RuntimeException("Must define a listener.");
        } else if (mXSession == null) {
            throw new RuntimeException("Must define a session.");
        } else {
            if (str != null) {
                bundle.putString(MatrixMessagesFragment.ARG_ROOM_ID, str);
            }
            vectorMessagesFragment.setArguments(bundle);
            vectorMessagesFragment.setMatrixMessagesListener(matrixMessagesListener);
            vectorMessagesFragment.setMXSession(mXSession);
            return vectorMessagesFragment;
        }
    }

    /* access modifiers changed from: protected */
    public void displayInitializeTimelineError(Object obj) {
        String localizedMessage;
        String str = "";
        if (obj instanceof MatrixError) {
            MatrixError matrixError = (MatrixError) obj;
            if (TextUtils.equals(matrixError.errcode, MatrixError.NOT_FOUND)) {
                localizedMessage = getContext().getString(C1299R.string.failed_to_load_timeline_position, new Object[]{Matrix.getApplicationName()});
            } else {
                localizedMessage = matrixError.getLocalizedMessage();
            }
            str = localizedMessage;
        } else if (obj instanceof Exception) {
            str = ((Exception) obj).getLocalizedMessage();
        }
        if (!TextUtils.isEmpty(str)) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("displayInitializeTimelineError : ");
            sb.append(str);
            Log.m209d(str2, sb.toString());
            Toast.makeText(getContext(), str, 0).show();
        }
    }
}
