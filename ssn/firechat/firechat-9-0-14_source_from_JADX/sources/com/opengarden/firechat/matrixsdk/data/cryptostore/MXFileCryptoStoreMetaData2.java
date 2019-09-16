package com.opengarden.firechat.matrixsdk.data.cryptostore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MXFileCryptoStoreMetaData2 implements Serializable {
    private static final long serialVersionUID = 9166554107081078408L;
    public List<String> mBlacklistUnverifiedDevicesRoomIdsList;
    public boolean mDeviceAnnounced;
    public String mDeviceId;
    public boolean mGlobalBlacklistUnverifiedDevices;
    public String mUserId;
    public int mVersion;

    public MXFileCryptoStoreMetaData2(String str, String str2, int i) {
        this.mUserId = new String(str);
        this.mDeviceId = str2 != null ? new String(str2) : null;
        this.mVersion = i;
        this.mDeviceAnnounced = false;
        this.mGlobalBlacklistUnverifiedDevices = false;
        this.mBlacklistUnverifiedDevicesRoomIdsList = new ArrayList();
    }

    public MXFileCryptoStoreMetaData2(MXFileCryptoStoreMetaData mXFileCryptoStoreMetaData) {
        this.mUserId = mXFileCryptoStoreMetaData.mUserId;
        this.mDeviceId = mXFileCryptoStoreMetaData.mDeviceId;
        this.mVersion = mXFileCryptoStoreMetaData.mVersion;
        this.mDeviceAnnounced = mXFileCryptoStoreMetaData.mDeviceAnnounced;
        this.mGlobalBlacklistUnverifiedDevices = false;
        this.mBlacklistUnverifiedDevicesRoomIdsList = new ArrayList();
    }
}
