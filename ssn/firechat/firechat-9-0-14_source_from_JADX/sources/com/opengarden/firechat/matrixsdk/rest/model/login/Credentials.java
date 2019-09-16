package com.opengarden.firechat.matrixsdk.rest.model.login;

import android.text.TextUtils;
import com.amplitude.api.AmplitudeClient;
import org.json.JSONException;
import org.json.JSONObject;

public class Credentials {
    public String accessToken;
    public String deviceId;
    public String homeServer;
    public String refreshToken;
    public String userId;

    public JSONObject toJson() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(AmplitudeClient.USER_ID_KEY, this.userId);
        jSONObject.put("home_server", this.homeServer);
        jSONObject.put("access_token", this.accessToken);
        jSONObject.put("refresh_token", TextUtils.isEmpty(this.refreshToken) ? JSONObject.NULL : this.refreshToken);
        jSONObject.put(AmplitudeClient.DEVICE_ID_KEY, this.deviceId);
        return jSONObject;
    }

    public static Credentials fromJson(JSONObject jSONObject) throws JSONException {
        Credentials credentials = new Credentials();
        credentials.userId = jSONObject.getString(AmplitudeClient.USER_ID_KEY);
        credentials.homeServer = jSONObject.getString("home_server");
        credentials.accessToken = jSONObject.getString("access_token");
        if (jSONObject.has(AmplitudeClient.DEVICE_ID_KEY)) {
            credentials.deviceId = jSONObject.getString(AmplitudeClient.DEVICE_ID_KEY);
        }
        if (jSONObject.has("refresh_token")) {
            try {
                credentials.refreshToken = jSONObject.getString("refresh_token");
            } catch (Exception unused) {
                credentials.refreshToken = null;
            }
            return credentials;
        }
        throw new RuntimeException("refresh_token is required.");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Credentials{userId='");
        sb.append(this.userId);
        sb.append('\'');
        sb.append(", homeServer='");
        sb.append(this.homeServer);
        sb.append('\'');
        sb.append(", refreshToken.length='");
        sb.append(this.refreshToken != null ? Integer.valueOf(this.refreshToken.length()) : "null");
        sb.append('\'');
        sb.append(", accessToken.length='");
        sb.append(this.accessToken != null ? Integer.valueOf(this.accessToken.length()) : "null");
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }
}
