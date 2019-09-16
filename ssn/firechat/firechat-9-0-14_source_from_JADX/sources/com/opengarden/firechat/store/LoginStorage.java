package com.opengarden.firechat.store;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;

public class LoginStorage {
    private static final String LOG_TAG = "LoginStorage";
    private static final String PREFS_KEY_CONNECTION_CONFIGS = "PREFS_KEY_CONNECTION_CONFIGS";
    private static final String PREFS_LOGIN = "Vector.LoginStorage";
    private final Context mContext;

    public LoginStorage(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public ArrayList<HomeServerConnectionConfig> getCredentialsList() {
        String string = this.mContext.getSharedPreferences(PREFS_LOGIN, 0).getString(PREFS_KEY_CONNECTION_CONFIGS, null);
        Log.m209d(LOG_TAG, "Got connection json: ");
        if (string == null) {
            return new ArrayList<>();
        }
        try {
            JSONArray jSONArray = new JSONArray(string);
            ArrayList<HomeServerConnectionConfig> arrayList = new ArrayList<>(jSONArray.length());
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(HomeServerConnectionConfig.fromJson(jSONArray.getJSONObject(i)));
            }
            return arrayList;
        } catch (JSONException e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to deserialize accounts ");
            sb.append(e.getMessage());
            Log.m212e(str, sb.toString(), e);
            throw new RuntimeException("Failed to deserialize accounts");
        }
    }

    public void addCredentials(HomeServerConnectionConfig homeServerConnectionConfig) {
        if (homeServerConnectionConfig != null && homeServerConnectionConfig.getCredentials() != null) {
            Editor edit = this.mContext.getSharedPreferences(PREFS_LOGIN, 0).edit();
            ArrayList credentialsList = getCredentialsList();
            credentialsList.add(homeServerConnectionConfig);
            ArrayList arrayList = new ArrayList(credentialsList.size());
            try {
                Iterator it = credentialsList.iterator();
                while (it.hasNext()) {
                    arrayList.add(((HomeServerConnectionConfig) it.next()).toJson());
                }
                String jSONArray = new JSONArray(arrayList).toString();
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Storing ");
                sb.append(arrayList.size());
                sb.append(" credentials");
                Log.m209d(str, sb.toString());
                edit.putString(PREFS_KEY_CONNECTION_CONFIGS, jSONArray);
                edit.apply();
            } catch (JSONException unused) {
                throw new RuntimeException("Failed to serialize connection config");
            }
        }
    }

    public void removeCredentials(HomeServerConnectionConfig homeServerConnectionConfig) {
        if (!(homeServerConnectionConfig == null || homeServerConnectionConfig.getCredentials() == null)) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Removing account: ");
            sb.append(homeServerConnectionConfig.getCredentials().userId);
            Log.m209d(str, sb.toString());
            boolean z = false;
            Editor edit = this.mContext.getSharedPreferences(PREFS_LOGIN, 0).edit();
            ArrayList credentialsList = getCredentialsList();
            ArrayList arrayList = new ArrayList(credentialsList.size());
            try {
                Iterator it = credentialsList.iterator();
                while (it.hasNext()) {
                    HomeServerConnectionConfig homeServerConnectionConfig2 = (HomeServerConnectionConfig) it.next();
                    if (homeServerConnectionConfig2.getCredentials().userId.equals(homeServerConnectionConfig.getCredentials().userId)) {
                        z = true;
                    } else {
                        arrayList.add(homeServerConnectionConfig2.toJson());
                    }
                }
                if (z) {
                    String jSONArray = new JSONArray(arrayList).toString();
                    String str2 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Storing ");
                    sb2.append(arrayList.size());
                    sb2.append(" credentials");
                    Log.m209d(str2, sb2.toString());
                    edit.putString(PREFS_KEY_CONNECTION_CONFIGS, jSONArray);
                    edit.apply();
                }
            } catch (JSONException unused) {
                throw new RuntimeException("Failed to serialize connection config");
            }
        }
    }

    public void replaceCredentials(HomeServerConnectionConfig homeServerConnectionConfig) {
        if (!(homeServerConnectionConfig == null || homeServerConnectionConfig.getCredentials() == null)) {
            boolean z = false;
            Editor edit = this.mContext.getSharedPreferences(PREFS_LOGIN, 0).edit();
            ArrayList credentialsList = getCredentialsList();
            ArrayList arrayList = new ArrayList(credentialsList.size());
            try {
                Iterator it = credentialsList.iterator();
                while (it.hasNext()) {
                    HomeServerConnectionConfig homeServerConnectionConfig2 = (HomeServerConnectionConfig) it.next();
                    if (homeServerConnectionConfig2.getCredentials().userId.equals(homeServerConnectionConfig.getCredentials().userId)) {
                        arrayList.add(homeServerConnectionConfig.toJson());
                        z = true;
                    } else {
                        arrayList.add(homeServerConnectionConfig2.toJson());
                    }
                }
                if (z) {
                    String jSONArray = new JSONArray(arrayList).toString();
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Storing ");
                    sb.append(arrayList.size());
                    sb.append(" credentials");
                    Log.m209d(str, sb.toString());
                    edit.putString(PREFS_KEY_CONNECTION_CONFIGS, jSONArray);
                    edit.apply();
                }
            } catch (JSONException unused) {
                throw new RuntimeException("Failed to serialize connection config");
            }
        }
    }

    public void clear() {
        Editor edit = this.mContext.getSharedPreferences(PREFS_LOGIN, 0).edit();
        edit.remove(PREFS_KEY_CONNECTION_CONFIGS);
        edit.apply();
    }
}
