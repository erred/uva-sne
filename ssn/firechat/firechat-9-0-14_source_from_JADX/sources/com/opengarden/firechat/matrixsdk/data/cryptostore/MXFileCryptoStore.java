package com.opengarden.firechat.matrixsdk.data.cryptostore;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest;
import com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest.RequestState;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.rest.model.login.Credentials;
import com.opengarden.firechat.matrixsdk.util.CompatUtil;
import com.opengarden.firechat.matrixsdk.util.ContentUtils;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import org.altbeacon.bluetooth.Pdu;
import org.apache.commons.lang3.StringUtils;
import org.matrix.olm.OlmAccount;
import org.matrix.olm.OlmSession;

public class MXFileCryptoStore implements IMXCryptoStore {
    private static final String LOG_TAG = "MXFileCryptoStore";
    private static final String MXFILE_CRYPTO_STORE_ACCOUNT_FILE = "account";
    private static final String MXFILE_CRYPTO_STORE_ACCOUNT_FILE_TMP = "account.tmp";
    private static final String MXFILE_CRYPTO_STORE_ALGORITHMS_FILE = "roomsAlgorithms";
    private static final String MXFILE_CRYPTO_STORE_ALGORITHMS_FILE_TMP = "roomsAlgorithms.tmp";
    private static final String MXFILE_CRYPTO_STORE_DEVICES_FILE = "devices";
    private static final String MXFILE_CRYPTO_STORE_DEVICES_FILE_TMP = "devices.tmp";
    private static final String MXFILE_CRYPTO_STORE_DEVICES_FOLDER = "devicesFolder";
    private static final String MXFILE_CRYPTO_STORE_FOLDER = "MXFileCryptoStore";
    private static final String MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FILE = "inboundGroupSessions";
    private static final String MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FILE_TMP = "inboundGroupSessions.tmp";
    private static final String MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FOLDER = "inboundGroupSessionsFolder";
    private static final String MXFILE_CRYPTO_STORE_INCOMING_ROOM_KEY_REQUESTS_FILE = "incomingRoomKeyRequests";
    private static final String MXFILE_CRYPTO_STORE_INCOMING_ROOM_KEY_REQUESTS_FILE_TMP = "incomingRoomKeyRequests.tmp";
    private static final String MXFILE_CRYPTO_STORE_METADATA_FILE = "MXFileCryptoStore";
    private static final String MXFILE_CRYPTO_STORE_METADATA_FILE_TMP = "MXFileCryptoStore.tmp";
    private static final String MXFILE_CRYPTO_STORE_OLM_SESSIONS_FILE = "sessions";
    private static final String MXFILE_CRYPTO_STORE_OLM_SESSIONS_FILE_TMP = "sessions.tmp";
    private static final String MXFILE_CRYPTO_STORE_OLM_SESSIONS_FOLDER = "olmSessionsFolder";
    private static final String MXFILE_CRYPTO_STORE_OUTGOING_ROOM_KEY_REQUEST_FILE = "outgoingRoomKeyRequests";
    private static final String MXFILE_CRYPTO_STORE_OUTGOING_ROOM_KEY_REQUEST_FILE_TMP = "outgoingRoomKeyRequests.tmp";
    private static final String MXFILE_CRYPTO_STORE_TRACKING_STATUSES_FILE = "trackingStatuses";
    private static final String MXFILE_CRYPTO_STORE_TRACKING_STATUSES_FILE_TMP = "trackingStatuses.tmp";
    private static final int MXFILE_CRYPTO_VERSION = 1;
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final Object mOlmSessionsLock = new Object();
    private File mAccountFile;
    private File mAccountFileTmp;
    private File mAlgorithmsFile;
    private File mAlgorithmsFileTmp;
    private Credentials mCredentials;
    private File mDevicesFile;
    private File mDevicesFileTmp;
    private File mDevicesFolder;
    private HashMap<String, HashMap<String, MXOlmInboundGroupSession2>> mInboundGroupSessions;
    private File mInboundGroupSessionsFile;
    private File mInboundGroupSessionsFileTmp;
    private File mInboundGroupSessionsFolder;
    private final Object mInboundGroupSessionsLock = new Object();
    private File mIncomingRoomKeyRequestsFile;
    private File mIncomingRoomKeyRequestsFileTmp;
    private boolean mIsCorrupted = false;
    private boolean mIsReady = false;
    private MXFileCryptoStoreMetaData2 mMetaData;
    private File mMetaDataFile;
    private File mMetaDataFileTmp;
    private OlmAccount mOlmAccount;
    private HashMap<String, HashMap<String, OlmSession>> mOlmSessions;
    private File mOlmSessionsFile;
    private File mOlmSessionsFileTmp;
    private File mOlmSessionsFolder;
    private final Map<Map<String, String>, OutgoingRoomKeyRequest> mOutgoingRoomKeyRequests = new HashMap();
    private File mOutgoingRoomKeyRequestsFile;
    private File mOutgoingRoomKeyRequestsFileTmp;
    private Map<String, Map<String, List<IncomingRoomKeyRequest>>> mPendingIncomingRoomKeyRequests;
    private HashMap<String, String> mRoomsAlgorithms;
    private File mStoreFile;
    private HashMap<String, Integer> mTrackingStatuses;
    private File mTrackingStatusesFile;
    private File mTrackingStatusesFileTmp;
    private MXUsersDevicesMap<MXDeviceInfo> mUsersDevicesInfoMap;
    private final Object mUsersDevicesInfoMapLock = new Object();

    public void initWithCredentials(Context context, Credentials credentials) {
        this.mCredentials = credentials;
        this.mStoreFile = new File(new File(context.getApplicationContext().getFilesDir(), "MXFileCryptoStore"), this.mCredentials.userId);
        this.mMetaDataFile = new File(this.mStoreFile, "MXFileCryptoStore");
        this.mMetaDataFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_METADATA_FILE_TMP);
        this.mAccountFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_ACCOUNT_FILE);
        this.mAccountFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_ACCOUNT_FILE_TMP);
        this.mDevicesFolder = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_DEVICES_FOLDER);
        this.mDevicesFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_DEVICES_FILE);
        this.mDevicesFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_DEVICES_FILE_TMP);
        this.mAlgorithmsFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_ALGORITHMS_FILE);
        this.mAlgorithmsFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_ALGORITHMS_FILE_TMP);
        this.mTrackingStatusesFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_TRACKING_STATUSES_FILE);
        this.mTrackingStatusesFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_TRACKING_STATUSES_FILE_TMP);
        this.mOlmSessionsFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_OLM_SESSIONS_FILE);
        this.mOlmSessionsFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_OLM_SESSIONS_FILE_TMP);
        this.mOlmSessionsFolder = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_OLM_SESSIONS_FOLDER);
        this.mInboundGroupSessionsFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FILE);
        this.mInboundGroupSessionsFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FILE_TMP);
        this.mInboundGroupSessionsFolder = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_INBOUND_GROUP_SESSSIONS_FOLDER);
        this.mOutgoingRoomKeyRequestsFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_OUTGOING_ROOM_KEY_REQUEST_FILE);
        this.mOutgoingRoomKeyRequestsFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_OUTGOING_ROOM_KEY_REQUEST_FILE_TMP);
        this.mIncomingRoomKeyRequestsFile = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_INCOMING_ROOM_KEY_REQUESTS_FILE);
        this.mIncomingRoomKeyRequestsFileTmp = new File(this.mStoreFile, MXFILE_CRYPTO_STORE_INCOMING_ROOM_KEY_REQUESTS_FILE_TMP);
        if (!(this.mMetaData != null || credentials.homeServer == null || credentials.userId == null || credentials.accessToken == null)) {
            this.mMetaData = new MXFileCryptoStoreMetaData2(this.mCredentials.userId, this.mCredentials.deviceId, 1);
        }
        this.mUsersDevicesInfoMap = new MXUsersDevicesMap<>();
        this.mRoomsAlgorithms = new HashMap<>();
        this.mTrackingStatuses = new HashMap<>();
        this.mOlmSessions = new HashMap<>();
        this.mInboundGroupSessions = new HashMap<>();
    }

    public boolean hasData() {
        boolean exists = this.mStoreFile.exists();
        if (!exists) {
            return exists;
        }
        loadMetaData();
        if (this.mMetaData != null) {
            return TextUtils.isEmpty(this.mMetaData.mDeviceId) || TextUtils.equals(this.mCredentials.deviceId, this.mMetaData.mDeviceId);
        }
        return exists;
    }

    public boolean isCorrupted() {
        return this.mIsCorrupted;
    }

    public void deleteStore() {
        try {
            ContentUtils.deleteDirectory(this.mStoreFile);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("deleteStore failed ");
            sb.append(e.getMessage());
            Log.m211e(str, sb.toString());
        }
    }

    public void open() {
        if (this.mIsReady) {
            Log.m211e(LOG_TAG, "## open() : the store is already opened");
            return;
        }
        this.mMetaData = null;
        loadMetaData();
        if (this.mMetaData == null) {
            resetData();
        } else if (1 != this.mMetaData.mVersion) {
            Log.m211e(LOG_TAG, "## open() : New MXFileCryptoStore version detected");
            resetData();
        } else if (!TextUtils.equals(this.mMetaData.mUserId, this.mCredentials.userId) || (this.mCredentials.deviceId != null && !TextUtils.equals(this.mCredentials.deviceId, this.mMetaData.mDeviceId))) {
            Log.m211e(LOG_TAG, "## open() : Credentials do not match");
            resetData();
        }
        if (this.mMetaData != null) {
            preloadCryptoData();
        }
        if (this.mMetaData != null || this.mCredentials.homeServer == null || this.mCredentials.userId == null || this.mCredentials.accessToken == null) {
            this.mIsReady = true;
            return;
        }
        this.mMetaData = new MXFileCryptoStoreMetaData2(this.mCredentials.userId, this.mCredentials.deviceId, 1);
        this.mIsReady = true;
        saveMetaData();
    }

    public void storeDeviceId(String str) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeDeviceId() : the store is not ready");
            return;
        }
        this.mMetaData.mDeviceId = str;
        saveMetaData();
    }

    public String getDeviceId() {
        if (this.mIsReady) {
            return this.mMetaData.mDeviceId;
        }
        Log.m211e(LOG_TAG, "## getDeviceId() : the store is not ready");
        return null;
    }

    private boolean storeObject(Object obj, File file, String str, String str2) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeObject() : the store is not ready");
            return false;
        } else if (obj == null || file == null || str == null) {
            Log.m211e(LOG_TAG, "## storeObject() : invalid parameters");
            return false;
        } else {
            if (!file.exists() && !file.mkdirs()) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot create the folder ");
                sb.append(file);
                Log.m211e(str3, sb.toString());
            }
            return storeObject(obj, new File(file, str), str2);
        }
    }

    private boolean storeObject(Object obj, File file, String str) {
        boolean z = false;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeObject() : the store is not ready");
            return false;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## storeObject() : should not be called in the UI thread ");
            sb.append(str);
            Log.m211e(str2, sb.toString());
        }
        synchronized (LOG_TAG) {
            try {
                long currentTimeMillis = System.currentTimeMillis();
                if (file.exists()) {
                    file.delete();
                }
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(CompatUtil.createGzipOutputStream(new FileOutputStream(file)));
                objectOutputStream.writeObject(obj);
                objectOutputStream.close();
                z = true;
                String str3 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## storeObject () : ");
                sb2.append(str);
                sb2.append(" done in ");
                sb2.append(System.currentTimeMillis() - currentTimeMillis);
                sb2.append(" ms");
                Log.m209d(str3, sb2.toString());
            } catch (OutOfMemoryError e) {
                String str4 = LOG_TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("storeObject failed : ");
                sb3.append(str);
                sb3.append(" -- ");
                sb3.append(e.getMessage());
                Log.m211e(str4, sb3.toString());
            } catch (Exception e2) {
                String str5 = LOG_TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("storeObject failed : ");
                sb4.append(str);
                sb4.append(" -- ");
                sb4.append(e2.getMessage());
                Log.m211e(str5, sb4.toString());
            }
        }
        return z;
    }

    private void saveMetaData() {
        if (this.mMetaDataFileTmp.exists()) {
            this.mMetaDataFileTmp.delete();
        }
        if (this.mMetaDataFile.exists()) {
            this.mMetaDataFile.renameTo(this.mMetaDataFileTmp);
        }
        if (storeObject(this.mMetaData, this.mMetaDataFile, "saveMetaData")) {
            if (this.mMetaDataFileTmp.exists()) {
                this.mMetaDataFileTmp.delete();
            }
        } else if (this.mMetaDataFileTmp.exists()) {
            this.mMetaDataFileTmp.renameTo(this.mMetaDataFile);
        }
    }

    public void storeAccount(OlmAccount olmAccount) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeAccount() : the store is not ready");
            return;
        }
        this.mOlmAccount = olmAccount;
        if (this.mAccountFileTmp.exists()) {
            this.mAccountFileTmp.delete();
        }
        if (this.mAccountFile.exists()) {
            this.mAccountFile.renameTo(this.mAccountFileTmp);
        }
        if (storeObject(this.mOlmAccount, this.mAccountFile, "storeAccount")) {
            if (this.mAccountFileTmp.exists()) {
                this.mAccountFileTmp.delete();
            }
        } else if (this.mAccountFileTmp.exists()) {
            this.mAccountFileTmp.renameTo(this.mAccountFile);
        }
    }

    public OlmAccount getAccount() {
        if (this.mIsReady) {
            return this.mOlmAccount;
        }
        Log.m211e(LOG_TAG, "## getAccount() : the store is not ready");
        return null;
    }

    private void loadUserDevices(String str) {
        boolean containsKey;
        if (!TextUtils.isEmpty(str)) {
            synchronized (this.mUsersDevicesInfoMapLock) {
                containsKey = this.mUsersDevicesInfoMap.getMap().containsKey(str);
            }
            if (!containsKey) {
                File file = new File(this.mDevicesFolder, str);
                if (file.exists()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    this.mIsCorrupted = false;
                    StringBuilder sb = new StringBuilder();
                    sb.append("load devices of ");
                    sb.append(str);
                    Object loadObject = loadObject(file, sb.toString());
                    if (loadObject != null) {
                        try {
                            synchronized (this.mUsersDevicesInfoMapLock) {
                                this.mUsersDevicesInfoMap.setObjects((Map) loadObject, str);
                            }
                        } catch (Exception e) {
                            String str2 = LOG_TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("## loadUserDevices : mUsersDevicesInfoMap.setObjects failed ");
                            sb2.append(e.getMessage());
                            Log.m211e(str2, sb2.toString());
                            this.mIsCorrupted = true;
                        }
                    }
                    if (this.mIsCorrupted) {
                        String str3 = LOG_TAG;
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("## loadUserDevices : failed to load the device of ");
                        sb3.append(str);
                        Log.m211e(str3, sb3.toString());
                        file.delete();
                        this.mIsCorrupted = false;
                        return;
                    }
                    String str4 = LOG_TAG;
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("## loadUserDevices : Load the devices of ");
                    sb4.append(str);
                    sb4.append(" in ");
                    sb4.append(System.currentTimeMillis() - currentTimeMillis);
                    sb4.append("ms");
                    Log.m209d(str4, sb4.toString());
                }
            }
        }
    }

    public void storeUserDevice(String str, MXDeviceInfo mXDeviceInfo) {
        HashMap hashMap;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeUserDevice() : the store is not ready");
            return;
        }
        loadUserDevices(str);
        synchronized (this.mUsersDevicesInfoMapLock) {
            this.mUsersDevicesInfoMap.setObject(mXDeviceInfo, str, mXDeviceInfo.deviceId);
            hashMap = new HashMap((Map) this.mUsersDevicesInfoMap.getMap().get(str));
        }
        File file = this.mDevicesFolder;
        StringBuilder sb = new StringBuilder();
        sb.append("storeUserDevice ");
        sb.append(str);
        sb.append(" with ");
        sb.append(hashMap.size());
        sb.append(" devices");
        storeObject(hashMap, file, str, sb.toString());
    }

    public MXDeviceInfo getUserDevice(String str, String str2) {
        MXDeviceInfo mXDeviceInfo;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getUserDevice() : the store is not ready");
            return null;
        }
        loadUserDevices(str2);
        synchronized (this.mUsersDevicesInfoMapLock) {
            mXDeviceInfo = (MXDeviceInfo) this.mUsersDevicesInfoMap.getObject(str, str2);
        }
        return mXDeviceInfo;
    }

    public void storeUserDevices(String str, Map<String, MXDeviceInfo> map) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeUserDevices() : the store is not ready");
            return;
        }
        synchronized (this.mUsersDevicesInfoMapLock) {
            this.mUsersDevicesInfoMap.setObjects(map, str);
        }
        File file = this.mDevicesFolder;
        StringBuilder sb = new StringBuilder();
        sb.append("storeUserDevice ");
        sb.append(str);
        storeObject(map, file, str, sb.toString());
    }

    public Map<String, MXDeviceInfo> getUserDevices(String str) {
        Map<String, MXDeviceInfo> map;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getUserDevices() : the store is not ready");
            return null;
        } else if (str == null) {
            return null;
        } else {
            loadUserDevices(str);
            synchronized (this.mUsersDevicesInfoMapLock) {
                map = (Map) this.mUsersDevicesInfoMap.getMap().get(str);
            }
            return map;
        }
    }

    public void storeRoomAlgorithm(String str, String str2) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeRoomAlgorithm() : the store is not ready");
            return;
        }
        if (!(str == null || str2 == null)) {
            this.mRoomsAlgorithms.put(str, str2);
            if (this.mAlgorithmsFileTmp.exists()) {
                this.mAlgorithmsFileTmp.delete();
            }
            if (this.mAlgorithmsFile.exists()) {
                this.mAlgorithmsFile.renameTo(this.mAlgorithmsFileTmp);
            }
            if (storeObject(this.mRoomsAlgorithms, this.mAlgorithmsFile, "storeAlgorithmForRoom - in background")) {
                if (this.mAlgorithmsFileTmp.exists()) {
                    this.mAlgorithmsFileTmp.delete();
                }
            } else if (this.mAlgorithmsFileTmp.exists()) {
                this.mAlgorithmsFileTmp.renameTo(this.mAlgorithmsFile);
            }
        }
    }

    public String getRoomAlgorithm(String str) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getRoomAlgorithm() : the store is not ready");
            return null;
        } else if (str != null) {
            return (String) this.mRoomsAlgorithms.get(str);
        } else {
            return null;
        }
    }

    public int getDeviceTrackingStatus(String str, int i) {
        if (this.mIsReady) {
            return (str == null || !this.mTrackingStatuses.containsKey(str)) ? i : ((Integer) this.mTrackingStatuses.get(str)).intValue();
        }
        Log.m211e(LOG_TAG, "## getDeviceTrackingStatus() : the store is not ready");
        return i;
    }

    public Map<String, Integer> getDeviceTrackingStatuses() {
        if (this.mIsReady) {
            return new HashMap(this.mTrackingStatuses);
        }
        Log.m211e(LOG_TAG, "## getDeviceTrackingStatuses() : the store is not ready");
        return null;
    }

    private void saveDeviceTrackingStatuses() {
        if (this.mTrackingStatusesFileTmp.exists()) {
            this.mTrackingStatusesFileTmp.delete();
        }
        if (this.mTrackingStatusesFile.exists()) {
            this.mTrackingStatusesFile.renameTo(this.mTrackingStatusesFileTmp);
        }
        if (storeObject(this.mTrackingStatuses, this.mTrackingStatusesFile, "saveDeviceTrackingStatus - in background")) {
            if (this.mTrackingStatusesFileTmp.exists()) {
                this.mTrackingStatusesFileTmp.delete();
            }
        } else if (this.mTrackingStatusesFileTmp.exists()) {
            this.mTrackingStatusesFileTmp.renameTo(this.mTrackingStatusesFile);
        }
    }

    public void saveDeviceTrackingStatuses(Map<String, Integer> map) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## saveDeviceTrackingStatuses() : the store is not ready");
            return;
        }
        this.mTrackingStatuses.clear();
        this.mTrackingStatuses.putAll(map);
        saveDeviceTrackingStatuses();
    }

    public void storeSession(OlmSession olmSession, String str) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeSession() : the store is not ready");
            return;
        }
        String str2 = null;
        if (olmSession != null) {
            try {
                str2 = olmSession.sessionIdentifier();
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## storeSession : session.sessionIdentifier() failed ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
            }
        }
        if (!(str == null || str2 == null)) {
            synchronized (mOlmSessionsLock) {
                if (!this.mOlmSessions.containsKey(str)) {
                    this.mOlmSessions.put(str, new HashMap());
                }
                OlmSession olmSession2 = (OlmSession) ((HashMap) this.mOlmSessions.get(str)).get(str2);
                if (olmSession != olmSession2) {
                    if (olmSession2 != null) {
                        olmSession2.releaseSession();
                    }
                    ((HashMap) this.mOlmSessions.get(str)).put(str2, olmSession);
                }
            }
            File file = new File(this.mOlmSessionsFolder, encodeFilename(str));
            if (!file.exists()) {
                file.mkdir();
            }
            String encodeFilename = encodeFilename(str2);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Store olm session ");
            sb2.append(str);
            sb2.append(StringUtils.SPACE);
            sb2.append(str2);
            storeObject(olmSession, file, encodeFilename, sb2.toString());
        }
    }

    public Map<String, OlmSession> getDeviceSessions(String str) {
        Map<String, OlmSession> map;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeSession() : the store is not ready");
            return null;
        } else if (str == null) {
            return null;
        } else {
            synchronized (mOlmSessionsLock) {
                map = (Map) this.mOlmSessions.get(str);
            }
            return map;
        }
    }

    public void removeInboundGroupSession(String str, String str2) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## removeInboundGroupSession() : the store is not ready");
            return;
        }
        if (!(str == null || str2 == null)) {
            synchronized (this.mInboundGroupSessionsLock) {
                if (this.mInboundGroupSessions.containsKey(str2)) {
                    MXOlmInboundGroupSession2 mXOlmInboundGroupSession2 = (MXOlmInboundGroupSession2) ((HashMap) this.mInboundGroupSessions.get(str2)).get(str);
                    if (mXOlmInboundGroupSession2 != null) {
                        ((HashMap) this.mInboundGroupSessions.get(str2)).remove(str);
                        File file = new File(this.mInboundGroupSessionsFolder, encodeFilename(mXOlmInboundGroupSession2.mSenderKey));
                        if (file.exists() && !new File(file, encodeFilename(str)).delete()) {
                            String str3 = LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## removeInboundGroupSession() : fail to remove the sessionid ");
                            sb.append(str);
                            Log.m211e(str3, sb.toString());
                        }
                        mXOlmInboundGroupSession2.mSession.releaseSession();
                    }
                }
            }
        }
    }

    public void storeInboundGroupSession(MXOlmInboundGroupSession2 mXOlmInboundGroupSession2) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## storeInboundGroupSession() : the store is not ready");
            return;
        }
        String str = null;
        if (!(mXOlmInboundGroupSession2 == null || mXOlmInboundGroupSession2.mSenderKey == null || mXOlmInboundGroupSession2.mSession == null)) {
            try {
                str = mXOlmInboundGroupSession2.mSession.sessionIdentifier();
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## storeInboundGroupSession() : sessionIdentifier failed ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        if (str != null) {
            synchronized (this.mInboundGroupSessionsLock) {
                if (!this.mInboundGroupSessions.containsKey(mXOlmInboundGroupSession2.mSenderKey)) {
                    this.mInboundGroupSessions.put(mXOlmInboundGroupSession2.mSenderKey, new HashMap());
                }
                MXOlmInboundGroupSession2 mXOlmInboundGroupSession22 = (MXOlmInboundGroupSession2) ((HashMap) this.mInboundGroupSessions.get(mXOlmInboundGroupSession2.mSenderKey)).get(str);
                if (mXOlmInboundGroupSession22 != mXOlmInboundGroupSession2) {
                    if (mXOlmInboundGroupSession22 != null) {
                        mXOlmInboundGroupSession22.mSession.releaseSession();
                    }
                    ((HashMap) this.mInboundGroupSessions.get(mXOlmInboundGroupSession2.mSenderKey)).put(str, mXOlmInboundGroupSession2);
                }
            }
            String str3 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## storeInboundGroupSession() : store session ");
            sb2.append(str);
            Log.m209d(str3, sb2.toString());
            File file = new File(this.mInboundGroupSessionsFolder, encodeFilename(mXOlmInboundGroupSession2.mSenderKey));
            if (!file.exists()) {
                file.mkdir();
            }
            storeObject(mXOlmInboundGroupSession2, file, encodeFilename(str), "storeInboundGroupSession - in background");
        }
    }

    public MXOlmInboundGroupSession2 getInboundGroupSession(String str, String str2) {
        MXOlmInboundGroupSession2 mXOlmInboundGroupSession2;
        MXOlmInboundGroupSession2 mXOlmInboundGroupSession22 = null;
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getInboundGroupSession() : the store is not ready");
            return null;
        } else if (str == null || str2 == null || !this.mInboundGroupSessions.containsKey(str2)) {
            return null;
        } else {
            try {
                synchronized (this.mInboundGroupSessionsLock) {
                    try {
                        mXOlmInboundGroupSession2 = (MXOlmInboundGroupSession2) ((HashMap) this.mInboundGroupSessions.get(str2)).get(str);
                        try {
                        } catch (Throwable th) {
                            th = th;
                            mXOlmInboundGroupSession22 = mXOlmInboundGroupSession2;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        throw th;
                    }
                }
            } catch (Exception e) {
                String str3 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getInboundGroupSession() failed ");
                sb.append(e.getMessage());
                Log.m211e(str3, sb.toString());
                mXOlmInboundGroupSession2 = mXOlmInboundGroupSession22;
            }
        }
        return mXOlmInboundGroupSession2;
    }

    public List<MXOlmInboundGroupSession2> getInboundGroupSessions() {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getInboundGroupSessions() : the store is not ready");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        synchronized (this.mInboundGroupSessionsLock) {
            for (String str : this.mInboundGroupSessions.keySet()) {
                arrayList.addAll(((HashMap) this.mInboundGroupSessions.get(str)).values());
            }
        }
        return arrayList;
    }

    public void close() {
        ArrayList arrayList = new ArrayList();
        for (HashMap values : this.mOlmSessions.values()) {
            arrayList.addAll(values.values());
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ((OlmSession) it.next()).releaseSession();
        }
        this.mOlmSessions.clear();
        ArrayList arrayList2 = new ArrayList();
        for (HashMap values2 : this.mInboundGroupSessions.values()) {
            arrayList2.addAll(values2.values());
        }
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            MXOlmInboundGroupSession2 mXOlmInboundGroupSession2 = (MXOlmInboundGroupSession2) it2.next();
            if (mXOlmInboundGroupSession2.mSession != null) {
                mXOlmInboundGroupSession2.mSession.releaseSession();
            }
        }
        this.mInboundGroupSessions.clear();
    }

    public void setGlobalBlacklistUnverifiedDevices(boolean z) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## setGlobalBlacklistUnverifiedDevices() : the store is not ready");
            return;
        }
        this.mMetaData.mGlobalBlacklistUnverifiedDevices = z;
        saveMetaData();
    }

    public boolean getGlobalBlacklistUnverifiedDevices() {
        if (this.mIsReady) {
            return this.mMetaData.mGlobalBlacklistUnverifiedDevices;
        }
        Log.m211e(LOG_TAG, "## getGlobalBlacklistUnverifiedDevices() : the store is not ready");
        return false;
    }

    public void setRoomsListBlacklistUnverifiedDevices(List<String> list) {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## setRoomsListBlacklistUnverifiedDevices() : the store is not ready");
            return;
        }
        this.mMetaData.mBlacklistUnverifiedDevicesRoomIdsList = list;
        saveMetaData();
    }

    public List<String> getRoomsListBlacklistUnverifiedDevices() {
        if (!this.mIsReady) {
            Log.m211e(LOG_TAG, "## getRoomsListBlacklistUnverifiedDevices() : the store is not ready");
            return null;
        } else if (this.mMetaData.mBlacklistUnverifiedDevicesRoomIdsList == null) {
            return new ArrayList();
        } else {
            return new ArrayList(this.mMetaData.mBlacklistUnverifiedDevicesRoomIdsList);
        }
    }

    private void saveOutgoingRoomKeyRequests() {
        if (this.mOutgoingRoomKeyRequestsFileTmp.exists()) {
            this.mOutgoingRoomKeyRequestsFileTmp.delete();
        }
        if (this.mOutgoingRoomKeyRequestsFile.exists()) {
            this.mOutgoingRoomKeyRequestsFile.renameTo(this.mOutgoingRoomKeyRequestsFileTmp);
        }
        if (storeObject(this.mOutgoingRoomKeyRequests, this.mOutgoingRoomKeyRequestsFile, "saveOutgoingRoomKeyRequests")) {
            if (this.mOutgoingRoomKeyRequestsFileTmp.exists()) {
                this.mOutgoingRoomKeyRequestsFileTmp.delete();
            }
        } else if (this.mOutgoingRoomKeyRequestsFileTmp.exists()) {
            this.mOutgoingRoomKeyRequestsFileTmp.renameTo(this.mOutgoingRoomKeyRequestsFile);
        }
    }

    public OutgoingRoomKeyRequest getOutgoingRoomKeyRequest(Map<String, String> map) {
        if (map != null) {
            return (OutgoingRoomKeyRequest) this.mOutgoingRoomKeyRequests.get(map);
        }
        return null;
    }

    public OutgoingRoomKeyRequest getOrAddOutgoingRoomKeyRequest(OutgoingRoomKeyRequest outgoingRoomKeyRequest) {
        if (outgoingRoomKeyRequest == null || outgoingRoomKeyRequest.mRequestBody == null) {
            return null;
        }
        if (this.mOutgoingRoomKeyRequests.containsKey(outgoingRoomKeyRequest.mRequestBody)) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getOrAddOutgoingRoomKeyRequest() : `already have key request outstanding for ");
            sb.append(outgoingRoomKeyRequest.getRoomId());
            sb.append(" / ");
            sb.append(outgoingRoomKeyRequest.getSessionId());
            sb.append(" not sending another");
            Log.m209d(str, sb.toString());
            return (OutgoingRoomKeyRequest) this.mOutgoingRoomKeyRequests.get(outgoingRoomKeyRequest.mRequestBody);
        }
        this.mOutgoingRoomKeyRequests.put(outgoingRoomKeyRequest.mRequestBody, outgoingRoomKeyRequest);
        saveOutgoingRoomKeyRequests();
        return outgoingRoomKeyRequest;
    }

    private OutgoingRoomKeyRequest getOutgoingRoomKeyRequestByTxId(String str) {
        if (str != null) {
            for (OutgoingRoomKeyRequest outgoingRoomKeyRequest : this.mOutgoingRoomKeyRequests.values()) {
                if (TextUtils.equals(outgoingRoomKeyRequest.mRequestId, str)) {
                    return outgoingRoomKeyRequest;
                }
            }
        }
        return null;
    }

    public OutgoingRoomKeyRequest getOutgoingRoomKeyRequestByState(Set<RequestState> set) {
        for (OutgoingRoomKeyRequest outgoingRoomKeyRequest : this.mOutgoingRoomKeyRequests.values()) {
            if (set.contains(outgoingRoomKeyRequest.mState)) {
                return outgoingRoomKeyRequest;
            }
        }
        return null;
    }

    public void updateOutgoingRoomKeyRequest(OutgoingRoomKeyRequest outgoingRoomKeyRequest) {
        if (outgoingRoomKeyRequest != null) {
            saveOutgoingRoomKeyRequests();
        }
    }

    public void deleteOutgoingRoomKeyRequest(String str) {
        OutgoingRoomKeyRequest outgoingRoomKeyRequestByTxId = getOutgoingRoomKeyRequestByTxId(str);
        if (outgoingRoomKeyRequestByTxId != null) {
            this.mOutgoingRoomKeyRequests.remove(outgoingRoomKeyRequestByTxId.mRequestBody);
            saveOutgoingRoomKeyRequests();
        }
    }

    private void resetData() {
        close();
        synchronized (LOG_TAG) {
            deleteStore();
        }
        if (!this.mStoreFile.exists()) {
            this.mStoreFile.mkdirs();
        }
        if (!this.mDevicesFolder.exists()) {
            this.mDevicesFolder.mkdirs();
        }
        if (!this.mOlmSessionsFolder.exists()) {
            this.mOlmSessionsFolder.mkdir();
        }
        if (!this.mInboundGroupSessionsFolder.exists()) {
            this.mInboundGroupSessionsFolder.mkdirs();
        }
        this.mMetaData = null;
    }

    private Object loadObject(File file, String str) {
        Object readObject;
        Object obj = null;
        if (!file.exists()) {
            return null;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            Object readObject2 = objectInputStream.readObject();
            try {
                objectInputStream.close();
                return readObject2;
            } catch (Exception e) {
                e = e;
                obj = readObject2;
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("failed : ");
                sb.append(e.getMessage());
                sb.append(" step 1");
                Log.m211e(str2, sb.toString());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    readObject = new ObjectInputStream(fileInputStream).readObject();
                    try {
                        fileInputStream.close();
                        return readObject;
                    } catch (Exception e2) {
                        e = e2;
                        obj = readObject;
                        this.mIsCorrupted = true;
                        String str3 = LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append("failed : ");
                        sb2.append(e.getMessage());
                        sb2.append(" step 2");
                        Log.m211e(str3, sb2.toString());
                        return obj;
                    }
                } catch (Exception e3) {
                    e = e3;
                    this.mIsCorrupted = true;
                    String str32 = LOG_TAG;
                    StringBuilder sb22 = new StringBuilder();
                    sb22.append(str);
                    sb22.append("failed : ");
                    sb22.append(e.getMessage());
                    sb22.append(" step 2");
                    Log.m211e(str32, sb22.toString());
                    return obj;
                }
            }
        } catch (Exception e4) {
            e = e4;
            String str22 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append("failed : ");
            sb3.append(e.getMessage());
            sb3.append(" step 1");
            Log.m211e(str22, sb3.toString());
            FileInputStream fileInputStream2 = new FileInputStream(file);
            readObject = new ObjectInputStream(fileInputStream2).readObject();
            fileInputStream2.close();
            return readObject;
        }
    }

    private void loadMetaData() {
        Object obj;
        if (this.mMetaDataFileTmp.exists()) {
            obj = loadObject(this.mMetaDataFileTmp, "loadMetadata");
        } else {
            obj = loadObject(this.mMetaDataFile, "loadMetadata");
        }
        if (obj != null) {
            try {
                if (obj instanceof MXFileCryptoStoreMetaData2) {
                    this.mMetaData = (MXFileCryptoStoreMetaData2) obj;
                } else {
                    this.mMetaData = new MXFileCryptoStoreMetaData2((MXFileCryptoStoreMetaData) obj);
                }
            } catch (Exception e) {
                this.mIsCorrupted = true;
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## loadMetadata() : metadata has been corrupted ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:107:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x049c  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x019d A[SYNTHETIC, Splitter:B:45:0x019d] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01cd  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x01d5  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0208  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x02a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void preloadCryptoData() {
        /*
            r15 = this;
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "## preloadCryptoData() starts"
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            long r0 = java.lang.System.currentTimeMillis()
            java.io.File r2 = r15.mAccountFileTmp
            boolean r2 = r2.exists()
            if (r2 == 0) goto L_0x001c
            java.io.File r2 = r15.mAccountFileTmp
            java.lang.String r3 = "preloadCryptoData - mAccountFile - tmp"
            java.lang.Object r2 = r15.loadObject(r2, r3)
            goto L_0x0024
        L_0x001c:
            java.io.File r2 = r15.mAccountFile
            java.lang.String r3 = "preloadCryptoData - mAccountFile"
            java.lang.Object r2 = r15.loadObject(r2, r3)
        L_0x0024:
            r3 = 1
            if (r2 == 0) goto L_0x0049
            org.matrix.olm.OlmAccount r2 = (org.matrix.olm.OlmAccount) r2     // Catch:{ Exception -> 0x002c }
            r15.mOlmAccount = r2     // Catch:{ Exception -> 0x002c }
            goto L_0x0049
        L_0x002c:
            r2 = move-exception
            r15.mIsCorrupted = r3
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "## preloadCryptoData() - invalid mAccountFile "
            r5.append(r6)
            java.lang.String r2 = r2.getMessage()
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r2)
        L_0x0049:
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## preloadCryptoData() : load mOlmAccount in "
            r4.append(r5)
            long r5 = java.lang.System.currentTimeMillis()
            long r7 = r5 - r0
            r4.append(r7)
            java.lang.String r0 = " ms"
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r0)
            java.io.File r0 = r15.mDevicesFolder
            boolean r0 = r0.exists()
            r1 = 0
            if (r0 != 0) goto L_0x0105
            java.io.File r0 = r15.mDevicesFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0084
            java.io.File r0 = r15.mDevicesFileTmp
            java.lang.String r2 = "preloadCryptoData - mUsersDevicesInfoMap - tmp"
            java.lang.Object r0 = r15.loadObject(r0, r2)
            goto L_0x008c
        L_0x0084:
            java.io.File r0 = r15.mDevicesFile
            java.lang.String r2 = "preloadCryptoData - mUsersDevicesInfoMap"
            java.lang.Object r0 = r15.loadObject(r0, r2)
        L_0x008c:
            if (r0 == 0) goto L_0x00ba
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap r0 = (com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap) r0     // Catch:{ Exception -> 0x009c }
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap r2 = new com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap     // Catch:{ Exception -> 0x009c }
            java.util.HashMap r0 = r0.getMap()     // Catch:{ Exception -> 0x009c }
            r2.<init>(r0)     // Catch:{ Exception -> 0x009c }
            r15.mUsersDevicesInfoMap = r2     // Catch:{ Exception -> 0x009c }
            goto L_0x00bc
        L_0x009c:
            r0 = move-exception
            r15.mIsCorrupted = r3
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## preloadCryptoData() - invalid mUsersDevicesInfoMap "
            r4.append(r5)
            java.lang.String r0 = r0.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r0)
            goto L_0x00bc
        L_0x00ba:
            r15.mIsCorrupted = r1
        L_0x00bc:
            java.io.File r0 = r15.mDevicesFolder
            r0.mkdirs()
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap<com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo> r0 = r15.mUsersDevicesInfoMap
            if (r0 == 0) goto L_0x010c
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap<com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo> r0 = r15.mUsersDevicesInfoMap
            java.util.HashMap r0 = r0.getMap()
            java.util.Set r2 = r0.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x00d3:
            boolean r4 = r2.hasNext()
            if (r4 == 0) goto L_0x00fa
            java.lang.Object r4 = r2.next()
            java.lang.String r4 = (java.lang.String) r4
            java.lang.Object r5 = r0.get(r4)
            java.io.File r6 = r15.mDevicesFolder
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "convert devices map of "
            r7.append(r8)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            r15.storeObject(r5, r6, r4, r7)
            goto L_0x00d3
        L_0x00fa:
            java.io.File r0 = r15.mDevicesFileTmp
            r0.delete()
            java.io.File r0 = r15.mDevicesFile
            r0.delete()
            goto L_0x010c
        L_0x0105:
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap r0 = new com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap
            r0.<init>()
            r15.mUsersDevicesInfoMap = r0
        L_0x010c:
            long r4 = java.lang.System.currentTimeMillis()
            java.io.File r0 = r15.mAlgorithmsFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0121
            java.io.File r0 = r15.mAlgorithmsFileTmp
            java.lang.String r2 = "preloadCryptoData - mRoomsAlgorithms - tmp"
            java.lang.Object r0 = r15.loadObject(r0, r2)
            goto L_0x0129
        L_0x0121:
            java.io.File r0 = r15.mAlgorithmsFile
            java.lang.String r2 = "preloadCryptoData - mRoomsAlgorithms"
            java.lang.Object r0 = r15.loadObject(r0, r2)
        L_0x0129:
            if (r0 == 0) goto L_0x0158
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ Exception -> 0x013b }
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x013b }
            r2.<init>(r0)     // Catch:{ Exception -> 0x013b }
            r15.mRoomsAlgorithms = r2     // Catch:{ Exception -> 0x013b }
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r15.mRoomsAlgorithms     // Catch:{ Exception -> 0x013b }
            int r0 = r0.size()     // Catch:{ Exception -> 0x013b }
            goto L_0x0159
        L_0x013b:
            r0 = move-exception
            r15.mIsCorrupted = r3
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "## preloadCryptoData() - invalid mAlgorithmsFile "
            r6.append(r7)
            java.lang.String r0 = r0.getMessage()
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r0)
        L_0x0158:
            r0 = 0
        L_0x0159:
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "## preloadCryptoData() : load mRoomsAlgorithms ("
            r6.append(r7)
            r6.append(r0)
            java.lang.String r0 = " algos) in "
            r6.append(r0)
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r7 - r4
            r6.append(r9)
            java.lang.String r0 = " ms"
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r0)
            java.io.File r0 = r15.mTrackingStatusesFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0193
            java.io.File r0 = r15.mTrackingStatusesFileTmp
            java.lang.String r2 = "preloadCryptoData - mTrackingStatuses - tmp"
            java.lang.Object r0 = r15.loadObject(r0, r2)
            goto L_0x019b
        L_0x0193:
            java.io.File r0 = r15.mTrackingStatusesFile
            java.lang.String r2 = "preloadCryptoData - mTrackingStatuses"
            java.lang.Object r0 = r15.loadObject(r0, r2)
        L_0x019b:
            if (r0 == 0) goto L_0x01c2
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x01a7 }
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ Exception -> 0x01a7 }
            r2.<init>(r0)     // Catch:{ Exception -> 0x01a7 }
            r15.mTrackingStatuses = r2     // Catch:{ Exception -> 0x01a7 }
            goto L_0x01c2
        L_0x01a7:
            r0 = move-exception
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## preloadCryptoData() - invalid mTrackingStatuses "
            r4.append(r5)
            java.lang.String r0 = r0.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r0)
        L_0x01c2:
            java.io.File r0 = r15.mOutgoingRoomKeyRequestsFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x01cd
            java.io.File r0 = r15.mOutgoingRoomKeyRequestsFileTmp
            goto L_0x01cf
        L_0x01cd:
            java.io.File r0 = r15.mOutgoingRoomKeyRequestsFile
        L_0x01cf:
            boolean r2 = r0.exists()
            if (r2 == 0) goto L_0x0200
            java.lang.String r2 = "get outgoing key request"
            java.lang.Object r0 = r15.loadObject(r0, r2)
            if (r0 == 0) goto L_0x0200
            java.util.Map<java.util.Map<java.lang.String, java.lang.String>, com.opengarden.firechat.matrixsdk.crypto.OutgoingRoomKeyRequest> r2 = r15.mOutgoingRoomKeyRequests     // Catch:{ Exception -> 0x01e5 }
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ Exception -> 0x01e5 }
            r2.putAll(r0)     // Catch:{ Exception -> 0x01e5 }
            goto L_0x0200
        L_0x01e5:
            r0 = move-exception
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## preloadCryptoData() : mOutgoingRoomKeyRequests init failed "
            r4.append(r5)
            java.lang.String r0 = r0.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r0)
        L_0x0200:
            java.io.File r0 = r15.mOlmSessionsFolder
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x02a1
            long r4 = java.lang.System.currentTimeMillis()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r15.mOlmSessions = r0
            java.io.File r0 = r15.mOlmSessionsFolder
            java.lang.String[] r0 = r0.list()
            if (r0 == 0) goto L_0x03ab
            r2 = 0
        L_0x021c:
            int r6 = r0.length
            if (r2 >= r6) goto L_0x0275
            r6 = r0[r2]
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            java.io.File r8 = new java.io.File
            java.io.File r9 = r15.mOlmSessionsFolder
            r8.<init>(r9, r6)
            java.lang.String[] r9 = r8.list()
            if (r9 == 0) goto L_0x0269
            r10 = 0
        L_0x0234:
            int r11 = r9.length
            if (r10 >= r11) goto L_0x0269
            r11 = r9[r10]
            java.io.File r12 = new java.io.File
            r12.<init>(r8, r11)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "load the olmSession "
            r13.append(r14)
            r13.append(r6)
            java.lang.String r14 = " "
            r13.append(r14)
            r13.append(r11)
            java.lang.String r13 = r13.toString()
            java.lang.Object r12 = r15.loadObject(r12, r13)
            org.matrix.olm.OlmSession r12 = (org.matrix.olm.OlmSession) r12
            if (r12 == 0) goto L_0x0266
            java.lang.String r11 = decodeFilename(r11)
            r7.put(r11, r12)
        L_0x0266:
            int r10 = r10 + 1
            goto L_0x0234
        L_0x0269:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, org.matrix.olm.OlmSession>> r8 = r15.mOlmSessions
            java.lang.String r6 = decodeFilename(r6)
            r8.put(r6, r7)
            int r2 = r2 + 1
            goto L_0x021c
        L_0x0275:
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "## preloadCryptoData() : load "
            r6.append(r7)
            int r0 = r0.length
            r6.append(r0)
            java.lang.String r0 = " olmsessions in "
            r6.append(r0)
            long r7 = java.lang.System.currentTimeMillis()
            long r9 = r7 - r4
            r6.append(r9)
            java.lang.String r0 = " ms"
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r2, r0)
            goto L_0x03ab
        L_0x02a1:
            java.io.File r0 = r15.mOlmSessionsFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x02b2
            java.io.File r0 = r15.mOlmSessionsFileTmp
            java.lang.String r2 = "preloadCryptoData - mOlmSessions - tmp"
            java.lang.Object r0 = r15.loadObject(r0, r2)
            goto L_0x02ba
        L_0x02b2:
            java.io.File r0 = r15.mOlmSessionsFile
            java.lang.String r2 = "preloadCryptoData - mOlmSessions"
            java.lang.Object r0 = r15.loadObject(r0, r2)
        L_0x02ba:
            if (r0 == 0) goto L_0x03ab
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ Exception -> 0x0384 }
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x0384 }
            r2.<init>()     // Catch:{ Exception -> 0x0384 }
            r15.mOlmSessions = r2     // Catch:{ Exception -> 0x0384 }
            java.util.Set r2 = r0.keySet()     // Catch:{ Exception -> 0x0384 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x0384 }
        L_0x02cd:
            boolean r4 = r2.hasNext()     // Catch:{ Exception -> 0x0384 }
            if (r4 == 0) goto L_0x02ea
            java.lang.Object r4 = r2.next()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0384 }
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, org.matrix.olm.OlmSession>> r5 = r15.mOlmSessions     // Catch:{ Exception -> 0x0384 }
            java.util.HashMap r6 = new java.util.HashMap     // Catch:{ Exception -> 0x0384 }
            java.lang.Object r7 = r0.get(r4)     // Catch:{ Exception -> 0x0384 }
            java.util.Map r7 = (java.util.Map) r7     // Catch:{ Exception -> 0x0384 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0384 }
            r5.put(r4, r6)     // Catch:{ Exception -> 0x0384 }
            goto L_0x02cd
        L_0x02ea:
            java.io.File r2 = r15.mOlmSessionsFolder     // Catch:{ Exception -> 0x0384 }
            boolean r2 = r2.mkdir()     // Catch:{ Exception -> 0x0384 }
            if (r2 != 0) goto L_0x030a
            java.lang.String r2 = LOG_TAG     // Catch:{ Exception -> 0x0384 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0384 }
            r4.<init>()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r5 = "Cannot create the folder "
            r4.append(r5)     // Catch:{ Exception -> 0x0384 }
            java.io.File r5 = r15.mOlmSessionsFolder     // Catch:{ Exception -> 0x0384 }
            r4.append(r5)     // Catch:{ Exception -> 0x0384 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0384 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r4)     // Catch:{ Exception -> 0x0384 }
        L_0x030a:
            java.util.Set r2 = r0.keySet()     // Catch:{ Exception -> 0x0384 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ Exception -> 0x0384 }
        L_0x0312:
            boolean r4 = r2.hasNext()     // Catch:{ Exception -> 0x0384 }
            if (r4 == 0) goto L_0x03a1
            java.lang.Object r4 = r2.next()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0384 }
            java.lang.Object r5 = r0.get(r4)     // Catch:{ Exception -> 0x0384 }
            java.util.Map r5 = (java.util.Map) r5     // Catch:{ Exception -> 0x0384 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0384 }
            java.io.File r7 = r15.mOlmSessionsFolder     // Catch:{ Exception -> 0x0384 }
            java.lang.String r8 = encodeFilename(r4)     // Catch:{ Exception -> 0x0384 }
            r6.<init>(r7, r8)     // Catch:{ Exception -> 0x0384 }
            boolean r7 = r6.mkdir()     // Catch:{ Exception -> 0x0384 }
            if (r7 != 0) goto L_0x034b
            java.lang.String r7 = LOG_TAG     // Catch:{ Exception -> 0x0384 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0384 }
            r8.<init>()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r9 = "Cannot create the folder "
            r8.append(r9)     // Catch:{ Exception -> 0x0384 }
            r8.append(r6)     // Catch:{ Exception -> 0x0384 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0384 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r7, r8)     // Catch:{ Exception -> 0x0384 }
        L_0x034b:
            java.util.Set r7 = r5.keySet()     // Catch:{ Exception -> 0x0384 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ Exception -> 0x0384 }
        L_0x0353:
            boolean r8 = r7.hasNext()     // Catch:{ Exception -> 0x0384 }
            if (r8 == 0) goto L_0x0312
            java.lang.Object r8 = r7.next()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x0384 }
            java.lang.Object r9 = r5.get(r8)     // Catch:{ Exception -> 0x0384 }
            java.lang.String r10 = encodeFilename(r8)     // Catch:{ Exception -> 0x0384 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0384 }
            r11.<init>()     // Catch:{ Exception -> 0x0384 }
            java.lang.String r12 = "Convert olmSession "
            r11.append(r12)     // Catch:{ Exception -> 0x0384 }
            r11.append(r4)     // Catch:{ Exception -> 0x0384 }
            java.lang.String r12 = " "
            r11.append(r12)     // Catch:{ Exception -> 0x0384 }
            r11.append(r8)     // Catch:{ Exception -> 0x0384 }
            java.lang.String r8 = r11.toString()     // Catch:{ Exception -> 0x0384 }
            r15.storeObject(r9, r6, r10, r8)     // Catch:{ Exception -> 0x0384 }
            goto L_0x0353
        L_0x0384:
            r0 = move-exception
            r15.mIsCorrupted = r3
            java.lang.String r2 = LOG_TAG
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "## preloadCryptoData() - invalid mSessionsFile "
            r4.append(r5)
            java.lang.String r0 = r0.getMessage()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r2, r0)
        L_0x03a1:
            java.io.File r0 = r15.mOlmSessionsFileTmp
            r0.delete()
            java.io.File r0 = r15.mOlmSessionsFile
            r0.delete()
        L_0x03ab:
            java.io.File r0 = r15.mInboundGroupSessionsFolder
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x049c
            long r4 = java.lang.System.currentTimeMillis()
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r15.mInboundGroupSessions = r0
            java.io.File r0 = r15.mInboundGroupSessionsFolder
            java.lang.String[] r0 = r0.list()
            if (r0 == 0) goto L_0x0471
            r2 = 0
            r6 = 0
        L_0x03c8:
            int r7 = r0.length
            if (r2 >= r7) goto L_0x0470
            java.io.File r7 = new java.io.File
            java.io.File r8 = r15.mInboundGroupSessionsFolder
            r9 = r0[r2]
            r7.<init>(r8, r9)
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            java.lang.String[] r9 = r7.list()
            if (r9 == 0) goto L_0x0461
            r10 = r6
            r6 = 0
        L_0x03e1:
            int r11 = r9.length
            if (r6 >= r11) goto L_0x0460
            java.io.File r11 = new java.io.File
            r12 = r9[r6]
            r11.<init>(r7, r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0442 }
            r12.<init>()     // Catch:{ Exception -> 0x0442 }
            java.lang.String r13 = "load inboundsession "
            r12.append(r13)     // Catch:{ Exception -> 0x0442 }
            r13 = r9[r6]     // Catch:{ Exception -> 0x0442 }
            r12.append(r13)     // Catch:{ Exception -> 0x0442 }
            java.lang.String r13 = " "
            r12.append(r13)     // Catch:{ Exception -> 0x0442 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0442 }
            java.lang.Object r12 = r15.loadObject(r11, r12)     // Catch:{ Exception -> 0x0442 }
            if (r12 == 0) goto L_0x0415
            boolean r13 = r12 instanceof com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession     // Catch:{ Exception -> 0x0442 }
            if (r13 == 0) goto L_0x0415
            com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2 r13 = new com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2     // Catch:{ Exception -> 0x0442 }
            com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession r12 = (com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession) r12     // Catch:{ Exception -> 0x0442 }
            r13.<init>(r12)     // Catch:{ Exception -> 0x0442 }
            goto L_0x0418
        L_0x0415:
            r13 = r12
            com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2 r13 = (com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2) r13     // Catch:{ Exception -> 0x0442 }
        L_0x0418:
            if (r13 == 0) goto L_0x0424
            r11 = r9[r6]     // Catch:{ Exception -> 0x0442 }
            java.lang.String r11 = decodeFilename(r11)     // Catch:{ Exception -> 0x0442 }
            r8.put(r11, r13)     // Catch:{ Exception -> 0x0442 }
            goto L_0x043f
        L_0x0424:
            java.lang.String r12 = LOG_TAG     // Catch:{ Exception -> 0x0442 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0442 }
            r13.<init>()     // Catch:{ Exception -> 0x0442 }
            java.lang.String r14 = "## preloadCryptoData() : delete "
            r13.append(r14)     // Catch:{ Exception -> 0x0442 }
            r13.append(r11)     // Catch:{ Exception -> 0x0442 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0442 }
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r13)     // Catch:{ Exception -> 0x0442 }
            r11.delete()     // Catch:{ Exception -> 0x0442 }
            r15.mIsCorrupted = r1     // Catch:{ Exception -> 0x0442 }
        L_0x043f:
            int r10 = r10 + 1
            goto L_0x045d
        L_0x0442:
            r11 = move-exception
            java.lang.String r12 = LOG_TAG
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "## preloadCryptoData() - invalid mInboundGroupSessions "
            r13.append(r14)
            java.lang.String r11 = r11.getMessage()
            r13.append(r11)
            java.lang.String r11 = r13.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r12, r11)
        L_0x045d:
            int r6 = r6 + 1
            goto L_0x03e1
        L_0x0460:
            r6 = r10
        L_0x0461:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2>> r7 = r15.mInboundGroupSessions
            r9 = r0[r2]
            java.lang.String r9 = decodeFilename(r9)
            r7.put(r9, r8)
            int r2 = r2 + 1
            goto L_0x03c8
        L_0x0470:
            r1 = r6
        L_0x0471:
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "## preloadCryptoData() : load "
            r2.append(r6)
            r2.append(r1)
            java.lang.String r1 = " inboundGroupSessions in "
            r2.append(r1)
            long r6 = java.lang.System.currentTimeMillis()
            long r8 = r6 - r4
            r2.append(r8)
            java.lang.String r1 = " ms"
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m209d(r0, r1)
            goto L_0x0593
        L_0x049c:
            java.io.File r0 = r15.mInboundGroupSessionsFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x04ad
            java.io.File r0 = r15.mInboundGroupSessionsFileTmp
            java.lang.String r1 = "preloadCryptoData - mInboundGroupSessions - tmp"
            java.lang.Object r0 = r15.loadObject(r0, r1)
            goto L_0x04b5
        L_0x04ad:
            java.io.File r0 = r15.mInboundGroupSessionsFile
            java.lang.String r1 = "preloadCryptoData - mInboundGroupSessions"
            java.lang.Object r0 = r15.loadObject(r0, r1)
        L_0x04b5:
            if (r0 == 0) goto L_0x0589
            java.util.Map r0 = (java.util.Map) r0     // Catch:{ Exception -> 0x04e5 }
            java.util.HashMap r1 = new java.util.HashMap     // Catch:{ Exception -> 0x04e5 }
            r1.<init>()     // Catch:{ Exception -> 0x04e5 }
            r15.mInboundGroupSessions = r1     // Catch:{ Exception -> 0x04e5 }
            java.util.Set r1 = r0.keySet()     // Catch:{ Exception -> 0x04e5 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ Exception -> 0x04e5 }
        L_0x04c8:
            boolean r2 = r1.hasNext()     // Catch:{ Exception -> 0x04e5 }
            if (r2 == 0) goto L_0x0502
            java.lang.Object r2 = r1.next()     // Catch:{ Exception -> 0x04e5 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x04e5 }
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2>> r4 = r15.mInboundGroupSessions     // Catch:{ Exception -> 0x04e5 }
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x04e5 }
            java.lang.Object r6 = r0.get(r2)     // Catch:{ Exception -> 0x04e5 }
            java.util.Map r6 = (java.util.Map) r6     // Catch:{ Exception -> 0x04e5 }
            r5.<init>(r6)     // Catch:{ Exception -> 0x04e5 }
            r4.put(r2, r5)     // Catch:{ Exception -> 0x04e5 }
            goto L_0x04c8
        L_0x04e5:
            r0 = move-exception
            r15.mIsCorrupted = r3
            java.lang.String r1 = LOG_TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "## preloadCryptoData() - invalid mInboundGroupSessions "
            r2.append(r4)
            java.lang.String r0 = r0.getMessage()
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r1, r0)
        L_0x0502:
            java.io.File r0 = r15.mInboundGroupSessionsFolder
            boolean r0 = r0.mkdirs()
            if (r0 != 0) goto L_0x0522
            java.lang.String r0 = LOG_TAG
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot create the folder "
            r1.append(r2)
            java.io.File r2 = r15.mInboundGroupSessionsFolder
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r1)
        L_0x0522:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2>> r0 = r15.mInboundGroupSessions
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x052c:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0589
            java.lang.Object r1 = r0.next()
            java.lang.String r1 = (java.lang.String) r1
            java.io.File r2 = new java.io.File
            java.io.File r4 = r15.mInboundGroupSessionsFolder
            java.lang.String r5 = encodeFilename(r1)
            r2.<init>(r4, r5)
            boolean r4 = r2.mkdirs()
            if (r4 != 0) goto L_0x055f
            java.lang.String r4 = LOG_TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Cannot create the folder "
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r4, r5)
        L_0x055f:
            java.util.HashMap<java.lang.String, java.util.HashMap<java.lang.String, com.opengarden.firechat.matrixsdk.crypto.data.MXOlmInboundGroupSession2>> r4 = r15.mInboundGroupSessions
            java.lang.Object r1 = r4.get(r1)
            java.util.Map r1 = (java.util.Map) r1
            java.util.Set r4 = r1.keySet()
            java.util.Iterator r4 = r4.iterator()
        L_0x056f:
            boolean r5 = r4.hasNext()
            if (r5 == 0) goto L_0x052c
            java.lang.Object r5 = r4.next()
            java.lang.String r5 = (java.lang.String) r5
            java.lang.Object r6 = r1.get(r5)
            java.lang.String r5 = encodeFilename(r5)
            java.lang.String r7 = "Convert inboundsession"
            r15.storeObject(r6, r2, r5, r7)
            goto L_0x056f
        L_0x0589:
            java.io.File r0 = r15.mInboundGroupSessionsFileTmp
            r0.delete()
            java.io.File r0 = r15.mInboundGroupSessionsFile
            r0.delete()
        L_0x0593:
            org.matrix.olm.OlmAccount r0 = r15.mOlmAccount
            if (r0 != 0) goto L_0x05ac
            com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap<com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo> r0 = r15.mUsersDevicesInfoMap
            java.util.HashMap r0 = r0.getMap()
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x05ac
            r15.mIsCorrupted = r3
            java.lang.String r0 = LOG_TAG
            java.lang.String r1 = "## preloadCryptoData() - there is no account but some devices are defined"
            com.opengarden.firechat.matrixsdk.util.Log.m211e(r0, r1)
        L_0x05ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.cryptostore.MXFileCryptoStore.preloadCryptoData():void");
    }

    private static String encodeFilename(String str) {
        if (str == null) {
            return null;
        }
        try {
            byte[] bytes = str.getBytes("UTF-8");
            char[] cArr = new char[(bytes.length * 2)];
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i] & Pdu.MANUFACTURER_DATA_PDU_TYPE;
                int i2 = i * 2;
                cArr[i2] = hexArray[b >>> 4];
                cArr[i2 + 1] = hexArray[b & 15];
            }
            return new String(cArr);
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## encodeFilename() - failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return str;
        }
    }

    private static String decodeFilename(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        try {
            return new String(bArr, "UTF-8");
        } catch (Exception e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## decodeFilename() - failed ");
            sb.append(e.getMessage());
            Log.m211e(str2, sb.toString());
            return str;
        }
    }

    private boolean isValidIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        return incomingRoomKeyRequest != null && !TextUtils.isEmpty(incomingRoomKeyRequest.mUserId) && !TextUtils.isEmpty(incomingRoomKeyRequest.mDeviceId) && !TextUtils.isEmpty(incomingRoomKeyRequest.mRequestId);
    }

    public IncomingRoomKeyRequest getIncomingRoomKeyRequest(String str, String str2, String str3) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3) || !this.mPendingIncomingRoomKeyRequests.containsKey(str) || !((Map) this.mPendingIncomingRoomKeyRequests.get(str)).containsKey(str2)) {
            return null;
        }
        for (IncomingRoomKeyRequest incomingRoomKeyRequest : (List) ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).get(str2)) {
            if (TextUtils.equals(str3, incomingRoomKeyRequest.mRequestId)) {
                return incomingRoomKeyRequest;
            }
        }
        return null;
    }

    public List<IncomingRoomKeyRequest> getPendingIncomingRoomKeyRequests() {
        loadIncomingRoomKeyRequests();
        ArrayList arrayList = new ArrayList();
        for (String str : this.mPendingIncomingRoomKeyRequests.keySet()) {
            for (String str2 : ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).keySet()) {
                arrayList.addAll((Collection) ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).get(str2));
            }
        }
        return arrayList;
    }

    private void addIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        String str = incomingRoomKeyRequest.mUserId;
        String str2 = incomingRoomKeyRequest.mDeviceId;
        if (!this.mPendingIncomingRoomKeyRequests.containsKey(str)) {
            this.mPendingIncomingRoomKeyRequests.put(str, new HashMap());
        }
        if (!((Map) this.mPendingIncomingRoomKeyRequests.get(str)).containsKey(str2)) {
            ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).put(str2, new ArrayList());
        }
        ((List) ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).get(str2)).add(incomingRoomKeyRequest);
    }

    public void storeIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        loadIncomingRoomKeyRequests();
        if (isValidIncomingRoomKeyRequest(incomingRoomKeyRequest) && getIncomingRoomKeyRequest(incomingRoomKeyRequest.mUserId, incomingRoomKeyRequest.mDeviceId, incomingRoomKeyRequest.mRequestId) == null) {
            addIncomingRoomKeyRequest(incomingRoomKeyRequest);
            saveIncomingRoomKeyRequests();
        }
    }

    public void deleteIncomingRoomKeyRequest(IncomingRoomKeyRequest incomingRoomKeyRequest) {
        loadIncomingRoomKeyRequests();
        if (isValidIncomingRoomKeyRequest(incomingRoomKeyRequest)) {
            IncomingRoomKeyRequest incomingRoomKeyRequest2 = getIncomingRoomKeyRequest(incomingRoomKeyRequest.mUserId, incomingRoomKeyRequest.mDeviceId, incomingRoomKeyRequest.mRequestId);
            if (incomingRoomKeyRequest2 != null) {
                String str = incomingRoomKeyRequest.mUserId;
                String str2 = incomingRoomKeyRequest.mDeviceId;
                ((List) ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).get(str2)).remove(incomingRoomKeyRequest2);
                if (((List) ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).get(str2)).isEmpty()) {
                    ((Map) this.mPendingIncomingRoomKeyRequests.get(str)).remove(str2);
                }
                if (((Map) this.mPendingIncomingRoomKeyRequests.get(str)).isEmpty()) {
                    this.mPendingIncomingRoomKeyRequests.remove(str);
                }
                saveIncomingRoomKeyRequests();
            }
        }
    }

    private void saveIncomingRoomKeyRequests() {
        if (this.mIncomingRoomKeyRequestsFileTmp.exists()) {
            this.mIncomingRoomKeyRequestsFileTmp.delete();
        }
        if (this.mIncomingRoomKeyRequestsFile.exists()) {
            this.mIncomingRoomKeyRequestsFile.renameTo(this.mIncomingRoomKeyRequestsFileTmp);
        }
        if (storeObject(getPendingIncomingRoomKeyRequests(), this.mIncomingRoomKeyRequestsFile, "savedIncomingRoomKeyRequests - in background")) {
            if (this.mIncomingRoomKeyRequestsFileTmp.exists()) {
                this.mIncomingRoomKeyRequestsFileTmp.delete();
            }
        } else if (this.mIncomingRoomKeyRequestsFileTmp.exists()) {
            this.mIncomingRoomKeyRequestsFileTmp.renameTo(this.mIncomingRoomKeyRequestsFile);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0043 A[LOOP:0: B:14:0x003d->B:16:0x0043, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadIncomingRoomKeyRequests() {
        /*
            r2 = this;
            java.util.Map<java.lang.String, java.util.Map<java.lang.String, java.util.List<com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest>>> r0 = r2.mPendingIncomingRoomKeyRequests
            if (r0 != 0) goto L_0x004d
            java.io.File r0 = r2.mIncomingRoomKeyRequestsFileTmp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0015
            java.io.File r0 = r2.mIncomingRoomKeyRequestsFileTmp
            java.lang.String r1 = "loadIncomingRoomKeyRequests - tmp"
            java.lang.Object r0 = r2.loadObject(r0, r1)
            goto L_0x001d
        L_0x0015:
            java.io.File r0 = r2.mIncomingRoomKeyRequestsFile
            java.lang.String r1 = "loadIncomingRoomKeyRequests"
            java.lang.Object r0 = r2.loadObject(r0, r1)
        L_0x001d:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            if (r0 == 0) goto L_0x0031
            java.util.List r0 = (java.util.List) r0     // Catch:{ Exception -> 0x0027 }
            goto L_0x0032
        L_0x0027:
            java.io.File r0 = r2.mIncomingRoomKeyRequestsFileTmp
            r0.delete()
            java.io.File r0 = r2.mIncomingRoomKeyRequestsFile
            r0.delete()
        L_0x0031:
            r0 = r1
        L_0x0032:
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2.mPendingIncomingRoomKeyRequests = r1
            java.util.Iterator r0 = r0.iterator()
        L_0x003d:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x004d
            java.lang.Object r1 = r0.next()
            com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest r1 = (com.opengarden.firechat.matrixsdk.crypto.IncomingRoomKeyRequest) r1
            r2.addIncomingRoomKeyRequest(r1)
            goto L_0x003d
        L_0x004d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.matrixsdk.data.cryptostore.MXFileCryptoStore.loadIncomingRoomKeyRequests():void");
    }
}
