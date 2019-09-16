package com.opengarden.firechat.offlineMessaging;

/* compiled from: Bluetooth */
class ConnectInfo implements Comparable<ConnectInfo> {
    boolean iAccepted;
    int mChannel;
    String mMac;

    public ConnectInfo(String str) {
        this.mMac = str;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && ((ConnectInfo) obj).mMac.equals(this.mMac);
    }

    public int compareTo(ConnectInfo connectInfo) {
        return this.mMac.compareTo(connectInfo.mMac);
    }
}
