package com.opengarden.firechat.matrixsdk.data.store;

import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MXFileStoreMetaData implements Serializable {
    public String mAccessToken = null;
    public String mAntivirusServerPublicKey;
    public Map<String, List<String>> mDirectChatRoomsMap = null;
    public boolean mEndToEndDeviceAnnounced = false;
    public String mEventStreamToken = null;
    public List<String> mIgnoredUsers = new ArrayList();
    public boolean mIsUrlPreviewEnabled = false;
    public Set<String> mRoomsListWithoutURLPrevew = new HashSet();
    public List<ThirdPartyIdentifier> mThirdPartyIdentifiers = null;
    public String mUserAvatarUrl = null;
    public String mUserDisplayName = null;
    public String mUserId = null;
    public Map<String, Object> mUserWidgets = new HashMap();
    public int mVersion = -1;

    public MXFileStoreMetaData deepCopy() {
        MXFileStoreMetaData mXFileStoreMetaData = new MXFileStoreMetaData();
        mXFileStoreMetaData.mUserId = this.mUserId;
        mXFileStoreMetaData.mAccessToken = this.mAccessToken;
        mXFileStoreMetaData.mEventStreamToken = this.mEventStreamToken;
        mXFileStoreMetaData.mVersion = this.mVersion;
        mXFileStoreMetaData.mUserDisplayName = this.mUserDisplayName;
        if (mXFileStoreMetaData.mUserDisplayName != null) {
            mXFileStoreMetaData.mUserDisplayName.trim();
        }
        mXFileStoreMetaData.mUserAvatarUrl = this.mUserAvatarUrl;
        mXFileStoreMetaData.mThirdPartyIdentifiers = this.mThirdPartyIdentifiers;
        mXFileStoreMetaData.mIgnoredUsers = this.mIgnoredUsers;
        mXFileStoreMetaData.mDirectChatRoomsMap = this.mDirectChatRoomsMap;
        mXFileStoreMetaData.mEndToEndDeviceAnnounced = this.mEndToEndDeviceAnnounced;
        mXFileStoreMetaData.mAntivirusServerPublicKey = this.mAntivirusServerPublicKey;
        mXFileStoreMetaData.mIsUrlPreviewEnabled = this.mIsUrlPreviewEnabled;
        mXFileStoreMetaData.mUserWidgets = this.mUserWidgets;
        mXFileStoreMetaData.mRoomsListWithoutURLPrevew = this.mRoomsListWithoutURLPrevew;
        return mXFileStoreMetaData;
    }
}
