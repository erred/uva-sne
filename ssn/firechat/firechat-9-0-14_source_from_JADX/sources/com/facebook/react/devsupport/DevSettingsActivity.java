package com.facebook.react.devsupport;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.facebook.react.C0742R;

public class DevSettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(C0742R.string.catalyst_settings_title);
        addPreferencesFromResource(C0742R.xml.preferences);
    }
}
