package com.opengarden.firechat.matrixsdk.crypto;

import android.support.p000v4.app.NotificationCompat;
import android.text.TextUtils;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.data.MXDeviceInfo;
import com.opengarden.firechat.matrixsdk.crypto.data.MXUsersDevicesMap;
import com.opengarden.firechat.matrixsdk.data.cryptostore.IMXCryptoStore;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.crypto.KeysQueryResponse;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class MXDeviceList {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "MXDeviceList";
    public static final int TRACKING_STATUS_DOWNLOAD_IN_PROGRESS = 2;
    public static final int TRACKING_STATUS_NOT_TRACKED = -1;
    public static final int TRACKING_STATUS_PENDING_DOWNLOAD = 1;
    public static final int TRACKING_STATUS_UNREACHABLE_SERVER = 4;
    public static final int TRACKING_STATUS_UP_TO_DATE = 3;
    private final IMXCryptoStore mCryptoStore;
    private final List<DownloadKeysPromise> mDownloadKeysQueues = new ArrayList();
    private boolean mIsDownloadingKeys;
    private final HashSet<String> mNotReadyToRetryHS = new HashSet<>();
    /* access modifiers changed from: private */
    public final HashMap<String, String> mPendingDownloadKeysRequestToken = new HashMap<>();
    private final HashSet<String> mUserKeyDownloadsInProgress = new HashSet<>();
    /* access modifiers changed from: private */
    public final MXCrypto mxCrypto;
    private final MXSession mxSession;

    class DownloadKeysPromise {
        final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> mCallback;
        final List<String> mPendingUserIdsList;
        final List<String> mUserIdsList;

        DownloadKeysPromise(List<String> list, ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback) {
            this.mPendingUserIdsList = new ArrayList(list);
            this.mUserIdsList = new ArrayList(list);
            this.mCallback = apiCallback;
        }
    }

    public MXDeviceList(MXSession mXSession, MXCrypto mXCrypto) {
        boolean z = false;
        this.mIsDownloadingKeys = false;
        this.mxSession = mXSession;
        this.mxCrypto = mXCrypto;
        this.mCryptoStore = mXCrypto.getCryptoStore();
        Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
        for (String str : deviceTrackingStatuses.keySet()) {
            int intValue = ((Integer) deviceTrackingStatuses.get(str)).intValue();
            if (2 == intValue || 4 == intValue) {
                deviceTrackingStatuses.put(str, Integer.valueOf(1));
                z = true;
            }
        }
        if (z) {
            this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
        }
    }

    private boolean canRetryKeysDownload(String str) {
        boolean z = false;
        if (!TextUtils.isEmpty(str) && str.contains(":")) {
            try {
                synchronized (this.mNotReadyToRetryHS) {
                    z = !this.mNotReadyToRetryHS.contains(str.substring(str.lastIndexOf(":") + 1));
                }
            } catch (Exception e) {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## canRetryKeysDownload() failed : ");
                sb.append(e.getMessage());
                Log.m211e(str2, sb.toString());
            }
        }
        return z;
    }

    private List<String> addDownloadKeysPromise(List<String> list, ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback) {
        if (list == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (String str : list) {
            if (MXSession.isUserId(str)) {
                arrayList.add(str);
            } else {
                String str2 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## userId ");
                sb.append(str);
                sb.append("is not a valid user id");
                Log.m211e(str2, sb.toString());
                arrayList2.add(str);
            }
        }
        synchronized (this.mUserKeyDownloadsInProgress) {
            arrayList.removeAll(this.mUserKeyDownloadsInProgress);
            this.mUserKeyDownloadsInProgress.addAll(list);
            this.mUserKeyDownloadsInProgress.removeAll(arrayList2);
            list.removeAll(arrayList2);
        }
        this.mDownloadKeysQueues.add(new DownloadKeysPromise(list, apiCallback));
        return arrayList;
    }

    private void clearUnavailableServersList() {
        synchronized (this.mNotReadyToRetryHS) {
            this.mNotReadyToRetryHS.clear();
        }
    }

    public void startTrackingDeviceList(List<String> list) {
        if (list != null) {
            boolean z = false;
            Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
            for (String str : list) {
                if (!deviceTrackingStatuses.containsKey(str) || -1 == ((Integer) deviceTrackingStatuses.get(str)).intValue()) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## startTrackingDeviceList() : Now tracking device list for ");
                    sb.append(str);
                    Log.m209d(str2, sb.toString());
                    deviceTrackingStatuses.put(str, Integer.valueOf(1));
                    z = true;
                }
            }
            if (z) {
                this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
            }
        }
    }

    public void handleDeviceListsChanges(List<String> list, List<String> list2) {
        Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
        boolean z = false;
        if (!(list == null || list.size() == 0)) {
            clearUnavailableServersList();
            for (String str : list) {
                if (deviceTrackingStatuses.containsKey(str)) {
                    String str2 = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## invalidateUserDeviceList() : Marking device list outdated for ");
                    sb.append(str);
                    Log.m209d(str2, sb.toString());
                    deviceTrackingStatuses.put(str, Integer.valueOf(1));
                    z = true;
                }
            }
        }
        if (!(list2 == null || list2.size() == 0)) {
            clearUnavailableServersList();
            for (String str3 : list2) {
                if (deviceTrackingStatuses.containsKey(str3)) {
                    String str4 = LOG_TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("## invalidateUserDeviceList() : No longer tracking device list for ");
                    sb2.append(str3);
                    Log.m209d(str4, sb2.toString());
                    deviceTrackingStatuses.put(str3, Integer.valueOf(-1));
                    z = true;
                }
            }
        }
        if (z) {
            this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
        }
    }

    public void invalidateAllDeviceLists() {
        handleDeviceListsChanges(new ArrayList(this.mCryptoStore.getDeviceTrackingStatuses().keySet()), null);
    }

    /* access modifiers changed from: private */
    public void onKeysDownloadFailed(List<String> list) {
        if (list != null) {
            synchronized (this.mUserKeyDownloadsInProgress) {
                Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
                for (String str : list) {
                    this.mUserKeyDownloadsInProgress.remove(str);
                    deviceTrackingStatuses.put(str, Integer.valueOf(1));
                }
                this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
            }
        }
        this.mIsDownloadingKeys = false;
    }

    /* access modifiers changed from: private */
    public void onKeysDownloadSucceed(List<String> list, Map<String, Map<String, Object>> map) {
        if (map != null) {
            for (String str : map.keySet()) {
                Map map2 = (Map) map.get(str);
                if (map2.containsKey(NotificationCompat.CATEGORY_STATUS)) {
                    Object obj = map2.get(NotificationCompat.CATEGORY_STATUS);
                    int i = obj instanceof Double ? ((Double) obj).intValue() : obj instanceof Integer ? ((Integer) obj).intValue() : 0;
                    if (i == 503) {
                        synchronized (this.mNotReadyToRetryHS) {
                            this.mNotReadyToRetryHS.add(str);
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
        if (list != null) {
            if (this.mDownloadKeysQueues.size() > 0) {
                ArrayList arrayList = new ArrayList();
                for (DownloadKeysPromise downloadKeysPromise : this.mDownloadKeysQueues) {
                    downloadKeysPromise.mPendingUserIdsList.removeAll(list);
                    if (downloadKeysPromise.mPendingUserIdsList.size() == 0) {
                        final MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
                        for (String str2 : downloadKeysPromise.mUserIdsList) {
                            Map userDevices = this.mCryptoStore.getUserDevices(str2);
                            if (userDevices != null) {
                                if (deviceTrackingStatuses.containsKey(str2) && 2 == ((Integer) deviceTrackingStatuses.get(str2)).intValue()) {
                                    deviceTrackingStatuses.put(str2, Integer.valueOf(3));
                                    String str3 = LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Device list for ");
                                    sb.append(str2);
                                    sb.append(" now up to date");
                                    Log.m209d(str3, sb.toString());
                                }
                                mXUsersDevicesMap.setObjects(userDevices, str2);
                            } else if (canRetryKeysDownload(str2)) {
                                deviceTrackingStatuses.put(str2, Integer.valueOf(1));
                                String str4 = LOG_TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("failed to retry the devices of ");
                                sb2.append(str2);
                                sb2.append(" : retry later");
                                Log.m211e(str4, sb2.toString());
                            } else if (deviceTrackingStatuses.containsKey(str2) && 2 == ((Integer) deviceTrackingStatuses.get(str2)).intValue()) {
                                deviceTrackingStatuses.put(str2, Integer.valueOf(4));
                                String str5 = LOG_TAG;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("failed to retry the devices of ");
                                sb3.append(str2);
                                sb3.append(" : the HS is not available");
                                Log.m211e(str5, sb3.toString());
                            }
                        }
                        if (!this.mxCrypto.hasBeenReleased()) {
                            final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback = downloadKeysPromise.mCallback;
                            if (apiCallback != null) {
                                this.mxCrypto.getUIHandler().post(new Runnable() {
                                    public void run() {
                                        apiCallback.onSuccess(mXUsersDevicesMap);
                                    }
                                });
                            }
                        }
                        arrayList.add(downloadKeysPromise);
                    }
                }
                this.mDownloadKeysQueues.removeAll(arrayList);
            }
            for (String remove : list) {
                this.mUserKeyDownloadsInProgress.remove(remove);
            }
            this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
        }
        this.mIsDownloadingKeys = false;
    }

    public void downloadKeys(List<String> list, boolean z, final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## downloadKeys() : forceDownload ");
        sb.append(z);
        sb.append(" : ");
        sb.append(list);
        Log.m209d(str, sb.toString());
        final MXUsersDevicesMap mXUsersDevicesMap = new MXUsersDevicesMap();
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            if (z) {
                arrayList.addAll(list);
            } else {
                for (String str2 : list) {
                    Integer valueOf = Integer.valueOf(this.mCryptoStore.getDeviceTrackingStatus(str2, -1));
                    if (!Matrix.getInstance(VectorApp.getInstance()).isConnected()) {
                        Map userDevices = this.mCryptoStore.getUserDevices(str2);
                        if (userDevices != null) {
                            mXUsersDevicesMap.setObjects(userDevices, str2);
                        }
                    } else if (this.mUserKeyDownloadsInProgress.contains(str2) || !(3 == valueOf.intValue() || 4 == valueOf.intValue())) {
                        arrayList.add(str2);
                    } else {
                        Map userDevices2 = this.mCryptoStore.getUserDevices(str2);
                        if (userDevices2 != null) {
                            mXUsersDevicesMap.setObjects(userDevices2, str2);
                        } else {
                            arrayList.add(str2);
                        }
                    }
                }
            }
        }
        if (arrayList.size() == 0) {
            Log.m209d(LOG_TAG, "## downloadKeys() : no new user device");
            if (apiCallback != null) {
                this.mxCrypto.getUIHandler().post(new Runnable() {
                    public void run() {
                        apiCallback.onSuccess(mXUsersDevicesMap);
                    }
                });
                return;
            }
            return;
        }
        Log.m209d(LOG_TAG, "## downloadKeys() : starts");
        final long currentTimeMillis = System.currentTimeMillis();
        final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback2 = apiCallback;
        C25023 r2 = new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
            public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                String access$000 = MXDeviceList.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## downloadKeys() : doKeyDownloadForUsers succeeds after ");
                sb.append(System.currentTimeMillis() - currentTimeMillis);
                sb.append(" ms");
                Log.m209d(access$000, sb.toString());
                mXUsersDevicesMap.addEntriesFromMap(mXUsersDevicesMap);
                if (apiCallback2 != null) {
                    apiCallback2.onSuccess(mXUsersDevicesMap);
                }
            }

            public void onNetworkError(Exception exc) {
                String access$000 = MXDeviceList.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## downloadKeys() : doKeyDownloadForUsers onNetworkError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onNetworkError(exc);
                }
            }

            public void onMatrixError(MatrixError matrixError) {
                String access$000 = MXDeviceList.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## downloadKeys() : doKeyDownloadForUsers onMatrixError ");
                sb.append(matrixError.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onMatrixError(matrixError);
                }
            }

            public void onUnexpectedError(Exception exc) {
                String access$000 = MXDeviceList.LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## downloadKeys() : doKeyDownloadForUsers onUnexpectedError ");
                sb.append(exc.getMessage());
                Log.m211e(access$000, sb.toString());
                if (apiCallback2 != null) {
                    apiCallback2.onUnexpectedError(exc);
                }
            }
        };
        doKeyDownloadForUsers(arrayList, r2);
    }

    private void doKeyDownloadForUsers(List<String> list, final ApiCallback<MXUsersDevicesMap<MXDeviceInfo>> apiCallback) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("## doKeyDownloadForUsers() : doKeyDownloadForUsers ");
        sb.append(list);
        Log.m209d(str, sb.toString());
        final List<String> addDownloadKeysPromise = addDownloadKeysPromise(list, apiCallback);
        if (addDownloadKeysPromise.size() != 0 && this.mxSession.getDataHandler() != null && this.mxSession.getDataHandler().getStore() != null) {
            this.mIsDownloadingKeys = true;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(addDownloadKeysPromise.hashCode());
            sb2.append(StringUtils.SPACE);
            sb2.append(System.currentTimeMillis());
            final String sb3 = sb2.toString();
            for (String put : addDownloadKeysPromise) {
                this.mPendingDownloadKeysRequestToken.put(put, sb3);
            }
            this.mxSession.getCryptoRestClient().downloadKeysForUsers(addDownloadKeysPromise, this.mxSession.getDataHandler().getStore().getEventStreamToken(), new ApiCallback<KeysQueryResponse>() {
                public void onSuccess(final KeysQueryResponse keysQueryResponse) {
                    MXDeviceList.this.mxCrypto.getEncryptingThreadHandler().post(new Runnable() {
                        public void run() {
                            String access$000 = MXDeviceList.LOG_TAG;
                            StringBuilder sb = new StringBuilder();
                            sb.append("## doKeyDownloadForUsers() : Got keys for ");
                            sb.append(addDownloadKeysPromise.size());
                            sb.append(" users");
                            Log.m209d(access$000, sb.toString());
                            MXDeviceInfo myDevice = MXDeviceList.this.mxCrypto.getMyDevice();
                            IMXCryptoStore cryptoStore = MXDeviceList.this.mxCrypto.getCryptoStore();
                            for (String str : new ArrayList(addDownloadKeysPromise)) {
                                if (!TextUtils.equals((CharSequence) MXDeviceList.this.mPendingDownloadKeysRequestToken.get(str), sb3)) {
                                    String access$0002 = MXDeviceList.LOG_TAG;
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append("## doKeyDownloadForUsers() : Another update in the queue for ");
                                    sb2.append(str);
                                    sb2.append(" not marking up-to-date");
                                    Log.m211e(access$0002, sb2.toString());
                                    addDownloadKeysPromise.remove(str);
                                } else {
                                    Map map = (Map) keysQueryResponse.deviceKeys.get(str);
                                    String access$0003 = MXDeviceList.LOG_TAG;
                                    StringBuilder sb3 = new StringBuilder();
                                    sb3.append("## doKeyDownloadForUsers() : Got keys for ");
                                    sb3.append(str);
                                    sb3.append(" : ");
                                    sb3.append(map);
                                    Log.m209d(access$0003, sb3.toString());
                                    if (map != null) {
                                        HashMap hashMap = new HashMap(map);
                                        Iterator it = new ArrayList(hashMap.keySet()).iterator();
                                        while (it.hasNext()) {
                                            String str2 = (String) it.next();
                                            if (cryptoStore == null) {
                                                break;
                                            }
                                            MXDeviceInfo userDevice = cryptoStore.getUserDevice(str2, str);
                                            MXDeviceInfo mXDeviceInfo = (MXDeviceInfo) hashMap.get(str2);
                                            if (TextUtils.equals(mXDeviceInfo.deviceId, myDevice.deviceId) && TextUtils.equals(str, myDevice.userId)) {
                                                mXDeviceInfo.mVerified = 1;
                                            }
                                            if (!MXDeviceList.this.validateDeviceKeys(mXDeviceInfo, str, str2, userDevice)) {
                                                hashMap.remove(str2);
                                                if (userDevice != null) {
                                                    hashMap.put(str2, userDevice);
                                                }
                                            } else if (userDevice != null) {
                                                ((MXDeviceInfo) hashMap.get(str2)).mVerified = userDevice.mVerified;
                                            }
                                        }
                                        cryptoStore.storeUserDevices(str, hashMap);
                                    }
                                    MXDeviceList.this.mPendingDownloadKeysRequestToken.remove(str);
                                }
                            }
                            MXDeviceList.this.onKeysDownloadSucceed(addDownloadKeysPromise, keysQueryResponse.failures);
                        }
                    });
                }

                private void onFailed() {
                    MXDeviceList.this.mxCrypto.getEncryptingThreadHandler().post(new Runnable() {
                        public void run() {
                            for (String str : new ArrayList(addDownloadKeysPromise)) {
                                if (!TextUtils.equals((CharSequence) MXDeviceList.this.mPendingDownloadKeysRequestToken.get(str), sb3)) {
                                    String access$000 = MXDeviceList.LOG_TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("## doKeyDownloadForUsers() : Another update in the queue for ");
                                    sb.append(str);
                                    sb.append(" not marking up-to-date");
                                    Log.m211e(access$000, sb.toString());
                                    addDownloadKeysPromise.remove(str);
                                } else {
                                    MXDeviceList.this.mPendingDownloadKeysRequestToken.remove(str);
                                }
                            }
                            MXDeviceList.this.onKeysDownloadFailed(addDownloadKeysPromise);
                        }
                    });
                }

                public void onNetworkError(Exception exc) {
                    String access$000 = MXDeviceList.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("##doKeyDownloadForUsers() : onNetworkError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    onFailed();
                    if (apiCallback != null) {
                        apiCallback.onNetworkError(exc);
                    }
                }

                public void onMatrixError(MatrixError matrixError) {
                    String access$000 = MXDeviceList.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("##doKeyDownloadForUsers() : onMatrixError ");
                    sb.append(matrixError.getMessage());
                    Log.m211e(access$000, sb.toString());
                    onFailed();
                    if (apiCallback != null) {
                        apiCallback.onMatrixError(matrixError);
                    }
                }

                public void onUnexpectedError(Exception exc) {
                    String access$000 = MXDeviceList.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("##doKeyDownloadForUsers() : onUnexpectedError ");
                    sb.append(exc.getMessage());
                    Log.m211e(access$000, sb.toString());
                    onFailed();
                    if (apiCallback != null) {
                        apiCallback.onUnexpectedError(exc);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public boolean validateDeviceKeys(MXDeviceInfo mXDeviceInfo, String str, String str2, MXDeviceInfo mXDeviceInfo2) {
        boolean z;
        if (mXDeviceInfo == null) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## validateDeviceKeys() : deviceKeys is null from ");
            sb.append(str);
            sb.append(":");
            sb.append(str2);
            Log.m211e(str3, sb.toString());
            return false;
        } else if (mXDeviceInfo.keys == null) {
            String str4 = LOG_TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("## validateDeviceKeys() : deviceKeys.keys is null from ");
            sb2.append(str);
            sb2.append(":");
            sb2.append(str2);
            Log.m211e(str4, sb2.toString());
            return false;
        } else if (mXDeviceInfo.signatures == null) {
            String str5 = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## validateDeviceKeys() : deviceKeys.signatures is null from ");
            sb3.append(str);
            sb3.append(":");
            sb3.append(str2);
            Log.m211e(str5, sb3.toString());
            return false;
        } else if (!TextUtils.equals(mXDeviceInfo.userId, str)) {
            String str6 = LOG_TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("## validateDeviceKeys() : Mismatched user_id ");
            sb4.append(mXDeviceInfo.userId);
            sb4.append(" from ");
            sb4.append(str);
            sb4.append(":");
            sb4.append(str2);
            Log.m211e(str6, sb4.toString());
            return false;
        } else if (!TextUtils.equals(mXDeviceInfo.deviceId, str2)) {
            String str7 = LOG_TAG;
            StringBuilder sb5 = new StringBuilder();
            sb5.append("## validateDeviceKeys() : Mismatched device_id ");
            sb5.append(mXDeviceInfo.deviceId);
            sb5.append(" from ");
            sb5.append(str);
            sb5.append(":");
            sb5.append(str2);
            Log.m211e(str7, sb5.toString());
            return false;
        } else {
            StringBuilder sb6 = new StringBuilder();
            sb6.append("ed25519:");
            sb6.append(mXDeviceInfo.deviceId);
            String sb7 = sb6.toString();
            String str8 = (String) mXDeviceInfo.keys.get(sb7);
            if (str8 == null) {
                String str9 = LOG_TAG;
                StringBuilder sb8 = new StringBuilder();
                sb8.append("## validateDeviceKeys() : Device ");
                sb8.append(str);
                sb8.append(":");
                sb8.append(mXDeviceInfo.deviceId);
                sb8.append(" has no ed25519 key");
                Log.m211e(str9, sb8.toString());
                return false;
            }
            Map map = (Map) mXDeviceInfo.signatures.get(str);
            if (map == null) {
                String str10 = LOG_TAG;
                StringBuilder sb9 = new StringBuilder();
                sb9.append("## validateDeviceKeys() : Device ");
                sb9.append(str);
                sb9.append(":");
                sb9.append(mXDeviceInfo.deviceId);
                sb9.append(" has no map for ");
                sb9.append(str);
                Log.m211e(str10, sb9.toString());
                return false;
            }
            String str11 = (String) map.get(sb7);
            if (str11 == null) {
                String str12 = LOG_TAG;
                StringBuilder sb10 = new StringBuilder();
                sb10.append("## validateDeviceKeys() : Device ");
                sb10.append(str);
                sb10.append(":");
                sb10.append(mXDeviceInfo.deviceId);
                sb10.append(" is not signed");
                Log.m211e(str12, sb10.toString());
                return false;
            }
            String str13 = null;
            try {
                this.mxCrypto.getOlmDevice().verifySignature(str8, mXDeviceInfo.signalableJSONDictionary(), str11);
                z = true;
            } catch (Exception e) {
                str13 = e.getMessage();
                z = false;
            }
            if (!z) {
                String str14 = LOG_TAG;
                StringBuilder sb11 = new StringBuilder();
                sb11.append("## validateDeviceKeys() : Unable to verify signature on device ");
                sb11.append(str);
                sb11.append(":");
                sb11.append(mXDeviceInfo.deviceId);
                sb11.append(" with error ");
                sb11.append(str13);
                Log.m211e(str14, sb11.toString());
                return false;
            } else if (mXDeviceInfo2 == null || TextUtils.equals(mXDeviceInfo2.fingerprint(), str8)) {
                return true;
            } else {
                String str15 = LOG_TAG;
                StringBuilder sb12 = new StringBuilder();
                sb12.append("## validateDeviceKeys() : WARNING:Ed25519 key for device ");
                sb12.append(str);
                sb12.append(":");
                sb12.append(mXDeviceInfo.deviceId);
                sb12.append(" has changed : ");
                sb12.append(mXDeviceInfo2.fingerprint());
                sb12.append(" -> ");
                sb12.append(str8);
                Log.m211e(str15, sb12.toString());
                String str16 = LOG_TAG;
                StringBuilder sb13 = new StringBuilder();
                sb13.append("## validateDeviceKeys() : ");
                sb13.append(mXDeviceInfo2);
                sb13.append(" -> ");
                sb13.append(mXDeviceInfo);
                Log.m211e(str16, sb13.toString());
                String str17 = LOG_TAG;
                StringBuilder sb14 = new StringBuilder();
                sb14.append("## validateDeviceKeys() : ");
                sb14.append(mXDeviceInfo2.keys);
                sb14.append(" -> ");
                sb14.append(mXDeviceInfo.keys);
                Log.m211e(str17, sb14.toString());
                return false;
            }
        }
    }

    public void refreshOutdatedDeviceLists() {
        final ArrayList<String> arrayList = new ArrayList<>();
        Map deviceTrackingStatuses = this.mCryptoStore.getDeviceTrackingStatuses();
        for (String str : deviceTrackingStatuses.keySet()) {
            if (1 == ((Integer) deviceTrackingStatuses.get(str)).intValue()) {
                arrayList.add(str);
            }
        }
        if (arrayList.size() != 0 && !this.mIsDownloadingKeys) {
            for (String str2 : arrayList) {
                Integer num = (Integer) deviceTrackingStatuses.get(str2);
                if (num != null && 1 == num.intValue()) {
                    deviceTrackingStatuses.put(str2, Integer.valueOf(2));
                }
            }
            this.mCryptoStore.saveDeviceTrackingStatuses(deviceTrackingStatuses);
            doKeyDownloadForUsers(arrayList, new ApiCallback<MXUsersDevicesMap<MXDeviceInfo>>() {
                public void onSuccess(MXUsersDevicesMap<MXDeviceInfo> mXUsersDevicesMap) {
                    MXDeviceList.this.mxCrypto.getEncryptingThreadHandler().post(new Runnable() {
                        public void run() {
                            Log.m209d(MXDeviceList.LOG_TAG, "## refreshOutdatedDeviceLists() : done");
                        }
                    });
                }

                private void onError(String str) {
                    String access$000 = MXDeviceList.LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("## refreshOutdatedDeviceLists() : ERROR updating device keys for users ");
                    sb.append(arrayList);
                    sb.append(" : ");
                    sb.append(str);
                    Log.m211e(access$000, sb.toString());
                }

                public void onNetworkError(Exception exc) {
                    onError(exc.getMessage());
                }

                public void onMatrixError(MatrixError matrixError) {
                    onError(matrixError.getMessage());
                }

                public void onUnexpectedError(Exception exc) {
                    onError(exc.getMessage());
                }
            });
        }
    }
}
