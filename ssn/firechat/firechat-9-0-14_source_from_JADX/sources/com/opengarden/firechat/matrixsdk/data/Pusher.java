package com.opengarden.firechat.matrixsdk.data;

import java.util.Map;

public class Pusher {
    public String appDisplayName;
    public String appId;
    public Boolean append;
    public Map<String, String> data;
    public String deviceDisplayName;
    public Object kind;
    public String lang;
    public String profileTag;
    public String pushkey;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pusher : \n\tappDisplayName ");
        sb.append(this.appDisplayName);
        sb.append("\n\tdeviceDisplayName ");
        sb.append(this.deviceDisplayName);
        sb.append("\n\tpushkey ");
        sb.append(this.pushkey);
        return sb.toString();
    }
}
