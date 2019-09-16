package org.matrix.olm;

import android.content.Context;
import android.util.Log;
import org.apache.commons.cli.HelpFormatter;

public class OlmManager {
    private static final String LOG_TAG = "OlmManager";

    public native String getOlmLibVersionJni();

    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    static {
        try {
            System.loadLibrary("olm");
        } catch (UnsatisfiedLinkError e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Exception loadLibrary() - Msg=");
            sb.append(e.getMessage());
            Log.e(str, sb.toString());
        }
    }

    public String getDetailedVersion(Context context) {
        String string = context.getResources().getString(C3144R.string.git_olm_revision);
        String string2 = context.getResources().getString(C3144R.string.git_olm_revision_date);
        StringBuilder sb = new StringBuilder();
        sb.append(getVersion());
        sb.append(" - olm version (");
        sb.append(getOlmLibVersion());
        sb.append(") - ");
        sb.append(string);
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(string2);
        return sb.toString();
    }

    public String getOlmLibVersion() {
        return getOlmLibVersionJni();
    }
}
