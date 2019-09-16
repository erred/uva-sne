package org.webrtc;

public class IceCandidate {
    public final String sdp;
    public final int sdpMLineIndex;
    public final String sdpMid;
    public final String serverUrl;

    public IceCandidate(String str, int i, String str2) {
        this.sdpMid = str;
        this.sdpMLineIndex = i;
        this.sdp = str2;
        this.serverUrl = "";
    }

    private IceCandidate(String str, int i, String str2, String str3) {
        this.sdpMid = str;
        this.sdpMLineIndex = i;
        this.sdp = str2;
        this.serverUrl = str3;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sdpMid);
        sb.append(":");
        sb.append(this.sdpMLineIndex);
        sb.append(":");
        sb.append(this.sdp);
        sb.append(":");
        sb.append(this.serverUrl);
        return sb.toString();
    }
}
